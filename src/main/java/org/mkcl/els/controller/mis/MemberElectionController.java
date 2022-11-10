/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberElectionController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.RivalMember;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class MemberElectionController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/election")
public class MemberElectionController extends GenericController<ElectionResult>{

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
    @Override
    protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
    	Long member = Long.parseLong(request.getParameter("member"));
        populateNames(model,request,locale.toString(),member);
        model.addAttribute("houseType", request.getParameter("houseType"));
    }
    
	/**
	 * Populate edit.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 */
	private void populateNames(final ModelMap model,final HttpServletRequest request, final String locale, final Long member) {
		Member selectedMember=Member.findById(Member.class,member);
		model.addAttribute("fullname", selectedMember.getFullname());
	}

	@Override
	protected void populateNew(final ModelMap model, final ElectionResult domain,
            final String locale, final HttpServletRequest request) {
        domain.setLocale(locale);
        model.addAttribute("rivalCount",0);
		populate(model, domain, request, locale.toString());
    }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, final ElectionResult domain,
            final HttpServletRequest request) {
		String locale=domain.getLocale();
		populate(model, domain, request, locale);
        List<RivalMember> rivalMembers=domain.getRivalMembers();
        model.addAttribute("rivalMembers",rivalMembers);
        model.addAttribute("rivalCount",rivalMembers.size());
    }

	/**
	 * Populate.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 * @param locale the locale
	 */
	private void populate(final ModelMap model, final ElectionResult domain,
            final HttpServletRequest request,final String locale){
		try {
			//populating houseType
			String houseType=null;
			if(request.getParameter("houseType")!=null){
				houseType=request.getParameter("houseType");
			}else{
				if(domain.getElection()!=null){
					if(domain.getElection().getHouse()!=null){
				houseType=domain.getElection().getHouse().getType().getType();
					}
				}
			}
			if(houseType==null){
				houseType=(String) request.getSession().getAttribute("houseType");
				request.getSession().removeAttribute("houseType");
			}
			if(houseType==null){
				houseType=request.getParameter("selectedhouseType");
			}
			model.addAttribute("houseType",houseType);

			//populating houseTypes
			List<HouseType> houseTypes=HouseType.findAll(HouseType.class,"name",ApplicationConstants.ASC, locale);
			model.addAttribute("houseTypes",houseTypes);

			//populating member
			Long member=(long)0;
			if(request.getParameter("member")!=null){
				member=Long.parseLong(request.getParameter("member"));
			}else{
				member=domain.getMember().getId();
			}
			Member selectedMember=Member.findById(Member.class, member);
			model.addAttribute("fullname",selectedMember.getFullname());
			model.addAttribute("member",member);

			//populating elections
			List<Election> elections = Election.findByHouseType(houseType,locale);
			model.addAttribute("elections", elections);
			//// Case of populateNew
			if(domain.getElection() == null){
			    if(elections != null){
			        if(elections.size() > 0){
			            ElectionType electionType = elections.get(0).getElectionType();
			            model.addAttribute("electionType", electionType.getName());
			        }
			    }
			}
			//// Case of populateEdit
			else {
			    String electionType = domain.getElection().getElectionType().getName();
			    model.addAttribute("electionType", electionType);
			}

			//populating parties
			model.addAttribute("parties",Party.findAllByFieldName(Party.class,"isDissolved", false, "name", ApplicationConstants.ASC, locale));

			//populating constituencies
			//constituencies are selected depending on locale and housetype
			List<MasterVO> constituencies = Constituency.findAllByHouseType(houseType, locale);
			model.addAttribute("constituencies",constituencies );

			//populating initial constituency and corresponding division and districts
			Long house=(long)0;
			if(request.getParameter("house")==null){
				if(domain.getElection()!=null){
					house=domain.getElection().getHouse().getId();
				}
			}else{
				house=Long.parseLong(request.getParameter("house"));
			}
			Constituency currentConstituency=null;
			if(domain.getConstituency()!=null){
				currentConstituency=domain.getConstituency();
			}else{
			List<HouseMemberRoleAssociation> houseMemberRoleAssociations=HouseMemberRoleAssociation.findByMemberIdRolePriorityHouseId(member,0,house,locale);
			if(!houseMemberRoleAssociations.isEmpty()){
			if(houseMemberRoleAssociations.get(0).getConstituency()!=null){
					currentConstituency=houseMemberRoleAssociations.get(0).getConstituency();
					domain.setConstituency(currentConstituency);
			}
			}
			if(currentConstituency==null){
				if(!constituencies.isEmpty()){
					currentConstituency=Constituency.findById(Constituency.class,constituencies.get(0).getId());
				}
			}
			}
			if(currentConstituency!=null){
				if(!currentConstituency.getDistricts().isEmpty()){
					model.addAttribute("districts",currentConstituency.getDistricts());
					if(currentConstituency.getDistricts().get(0).getDivision()!=null){
						model.addAttribute("division", currentConstituency.getDistricts().get(0).getDivision().getName());
					}
				}
			}
			if(currentConstituency!=null){
				model.addAttribute("currentConstituency",currentConstituency.getId());
			}
		} catch (NumberFormatException e) {			
			e.printStackTrace();
			model.addAttribute("error", "Invalid number is provided.");
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
	}
	//setting the member field in the session
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateCreateIfNoErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model,
            final ElectionResult domain, final HttpServletRequest request) {
		request.getSession().setAttribute("houseType",request.getParameter("houseType"));
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateUpdateIfNoErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model,
            final ElectionResult domain, final HttpServletRequest request) {
		request.getSession().setAttribute("houseType",request.getParameter("houseType"));
	}
	//populating the rivals in the domain so as to preserve the entered values during error
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void preValidateCreate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
		populateRivals(domain, result, request);
    }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void preValidateUpdate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
		populateRivals(domain, result, request);
    }

	/**
	 * Populate rivals.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void populateRivals(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request){
		List<RivalMember> rivalMembers = new ArrayList<RivalMember>();
	     Integer rivalCount = Integer.parseInt(request
	     .getParameter("rivalCount"));
	     for (int i = 1; i <= rivalCount; i++) {
	     String party=request.getParameter("rivalParty" + i);
	     if(party!=null){
		     RivalMember rivalMember = new RivalMember();
	    	 if(!party.isEmpty()){
	    	     rivalMember.setParty((Party) Party.findById(Party.class,Long.parseLong(party)));
	    	 }

	    	 String name=request.getParameter("rivalName" + i);
	    	 if(name!=null){
	    		 if(!name.isEmpty()){
	    		     rivalMember.setName(name);
	    		 }
	    	 }

	    	 String votes=request.getParameter("rivalVotesReceived" + i);
	    	 //
	    	 if(votes!=null){
	    		 if(!votes.isEmpty()){
	    		 String newVotes=votes.replaceAll(",", "");
    		     rivalMember.setVotesReceived(Integer.parseInt(newVotes.trim()));
	    		 }
	    	 }

	    	 String locale=request.getParameter("rivalLocale" + i);
	    	 if(locale!=null){
	    		 if(!locale.isEmpty()){
    		     rivalMember.setLocale(locale);
	    		 }
	    	 }

	    	 String version=request.getParameter("rivalVersion" + i);
	    	 if(version!=null){
	    		 if(!version.isEmpty()){
    		     rivalMember.setVersion(Long.parseLong(version));
	    		 }
	    	 }

	    	 String id=request.getParameter("rivalId" + i);
	    	 if(id!=null){
	    		 if(!id.isEmpty()){
    		     rivalMember.setId(Long.parseLong(id));
	    		 }
	    	 }
	          rivalMembers.add(rivalMember);
	     }
	     }
	     domain.setRivalMembers(rivalMembers);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void customValidateCreate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("version","VersionMismatch");
        }
        if(domain.getElection()==null){
        	result.rejectValue("election","NotEmpty");
        }
        if(domain.getConstituency()==null){
        	result.rejectValue("constituency","NotEmpty");
        }

        try {
			if (domain.isDuplicate()) {
				Object[] params = new Object[4];
				params[0] = domain.getElection().getName();
				params[1] = domain.getConstituency().getName();
				params[2]=domain.getMember().getFullname();
				//params[2] = domain.getFromDate();
				//params[3] = domain.getToDate();
				result.rejectValue("version", "DuplicateElectionResult", params,
						"Member:"+params[2]+", Election:" + params[0] + ", Constituency:" + params[1]
						                                                     + " already exists");
			}
		} catch (ELSException e) {
			result.reject("version", e.getParameter());
		}

    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateUpdate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    /**
     * Delete rival.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @RequestMapping(value = "/rival/{id}/delete", method = RequestMethod.DELETE)
    public String deleteRival(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
        RivalMember rivalMember=RivalMember.findById(RivalMember.class, id);
        rivalMember.remove();
        return "info";
    }

    @Override
    protected void populateCreateIfErrors(final ModelMap model,
    		final ElectionResult domain, final HttpServletRequest request) {
    	populateEdit(model, domain, request);
    }

    @Override
    protected void populateUpdateIfErrors(final ModelMap model,
    		final ElectionResult domain, final HttpServletRequest request) {
    	populateEdit(model, domain, request);
    }
}


