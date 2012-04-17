package org.mkcl.els.controller.mis;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.RivalMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("member/election")
public class MemberElectionController extends GenericController<ElectionResult>{
	@Override
	protected void populateNew(final ModelMap model, final ElectionResult domain,
            final String locale, final HttpServletRequest request) {
        domain.setLocale(locale);
        model.addAttribute("rivalCount",0);
		populate(model, domain, request, locale.toString());
    }
	@Override
	protected void populateEdit(final ModelMap model, final ElectionResult domain,
            final HttpServletRequest request) {
		String locale=domain.getLocale();
		populate(model, domain, request, locale);
        List<RivalMember> rivalMembers=domain.getRivalMembers();
        model.addAttribute("rivalMembers",rivalMembers);
        model.addAttribute("rivalCount",rivalMembers.size());
        //now since the fields are not  directly bound to the spring path attributes hence number formatting is not taking place.
       // Map<String,String> map=new HashMap<String, String>();
//        if(!domain.getRivalMembers().isEmpty()){
//            for(RivalMember i:domain.getRivalMembers()){
//                if(i.getVotesReceived()!=null){
//                if(domain.getLocale().equals("mr_IN")){
//                   model.addAttribute("rivalVotesCount"+i.getId(), NumberFormat.getInstance(new Locale("hi","IN")).format(i.getVotesReceived()));
//                }
//                }
//            }
//        }
    }
	private void populate(final ModelMap model, final ElectionResult domain,
            final HttpServletRequest request,final String locale){
		String houseType=this.getCurrentUser().getHouseType();
        model.addAttribute("elections",Election.findByHouseType(houseType,locale));
        model.addAttribute("parties",Party.findAllByFieldName(Party.class,"isDissolved", false, "name", ApplicationConstants.ASC, locale));
        String defaultState=((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DEFAULT_STATE", locale.toString())).getValue();
        model.addAttribute("constituencies",Constituency.findVOByDefaultStateAndHouseType(defaultState,houseType, locale,"name",ApplicationConstants.ASC));
        if(domain.getConstituency()!=null){
        model.addAttribute("constituency",domain.getConstituency().getId());
        }
        Long member=(long) 0;
        if(request.getParameter("member")==null){
            member=(Long) request.getSession().getAttribute("member");
            request.getSession().removeAttribute("member");
        }else{
            member=Long.parseLong(request.getParameter("member"));
        }
        model.addAttribute("member",member);
	}
	//setting the member field in the session
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model,
            final ElectionResult domain, final HttpServletRequest request) {
		request.getSession().setAttribute("member",domain.getMember().getId());
	}
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model,
            final ElectionResult domain, final HttpServletRequest request) {
		request.getSession().setAttribute("member",domain.getMember().getId());
	}
	//populating the rivals in the domain so as to preserve the entered values during error
	@Override
	protected void preValidateCreate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
		populateRivals(domain, result, request);
    }

	@Override
	protected void preValidateUpdate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
		populateRivals(domain, result, request);
    }

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
	    	 if(votes!=null){
	    		 if(!votes.isEmpty()){
    		     rivalMember.setVotesReceived(Integer.parseInt(votes));
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

	@Override
    protected void customValidateCreate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
    @Override
    protected void customValidateUpdate(final ElectionResult domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    @RequestMapping(value = "/rival/{id}/delete", method = RequestMethod.DELETE)
    public String deleteRival(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
        RivalMember rivalMember=RivalMember.findById(RivalMember.class, id);
        rivalMember.remove();
        return "info";
    }
}


