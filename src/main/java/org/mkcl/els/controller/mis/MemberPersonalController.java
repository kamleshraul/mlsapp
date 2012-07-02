/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberPersonalController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.Degree;
import org.mkcl.els.domain.FamilyMember;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.MaritalStatus;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Nationality;
import org.mkcl.els.domain.Profession;
import org.mkcl.els.domain.Qualification;
import org.mkcl.els.domain.Relation;
import org.mkcl.els.domain.Religion;
import org.mkcl.els.domain.Reservation;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class MemberPersonalController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/member/personal")
public class MemberPersonalController extends GenericController<Member> {

    //adding housetype
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateModule(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest, java.lang.String, org.mkcl.els.common.vo.AuthUser)
     */
    @Override
    protected void populateModule(final ModelMap model,
            final HttpServletRequest request, final String locale,
            final AuthUser currentUser) {
        model.addAttribute("housetype", request.getParameter("houseType"));
    }

    //init binders
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customInitBinderSuperClass(java.lang.Class, org.springframework.web.bind.WebDataBinder)
     */
    @SuppressWarnings("rawtypes")
	@Override
    protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
        binder.registerCustomEditor(Gender.class, new BaseEditor(new Gender()));
        binder.registerCustomEditor(MaritalStatus.class, new BaseEditor(
                new MaritalStatus()));
        binder.registerCustomEditor(List.class, "professions",
                new CustomCollectionEditor(List.class) {

                    @Override
                    protected Object convertElement(final Object element) {
                        String id = null;

                        if (element instanceof String) {
                            id = (String) element;
                        }

                        return id != null ? BaseDomain.findById(
                                Profession.class, Long.valueOf(id)) : null;
                    }
                });
        binder.registerCustomEditor(Nationality.class, new BaseEditor(
                new Nationality()));
        binder.registerCustomEditor(Address.class,
                new BaseEditor(new Address()));
        binder.registerCustomEditor(Contact.class,
                new BaseEditor(new Contact()));
        binder.registerCustomEditor(Title.class, new BaseEditor(new Title()));
    }
    //logic for populating domain and model during new and edit request
    /**
     * Populate.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    private void populate(final ModelMap model, final Member domain,
             final HttpServletRequest request){
        String locale=domain.getLocale();
        model.addAttribute("titles", Title.findAll(Title.class, "name",
                ApplicationConstants.ASC, locale));
        model.addAttribute("nationalities", Nationality.findAll(
                Nationality.class, "name", ApplicationConstants.ASC,
                locale));
        model.addAttribute("genders", Gender.findAll(Gender.class, "name",
                ApplicationConstants.ASC, locale));
        model.addAttribute("religions", Religion.findAll(Religion.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("reservations", Reservation.findAll(
                Reservation.class, "name", ApplicationConstants.ASC,
                locale));
        model.addAttribute("relations", Relation.findAll(Relation.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("degrees", Degree.findAll(Degree.class, "name",
                ApplicationConstants.ASC, locale));
        model.addAttribute("languages", Language.findAllSortedByPriorityAndName(locale));
        model.addAttribute("professions", Profession.findAll(Profession.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("maritalStatuses", MaritalStatus.findAll(
                MaritalStatus.class, "name", ApplicationConstants.ASC,
                locale));
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model, final Member domain,
            final String locale, final HttpServletRequest request) {
        domain.setLocale(locale.toString());
        populate(model, domain,request);
        model.addAttribute("familyCount",0);
        model.addAttribute("qualificationCount",0);
        //alias will always be enabled.
        domain.setAliasEnabled(true);
        //initially nof of sons,daughters and children is set to 0
        model.addAttribute("daughters",0);
        model.addAttribute("sons",0);
        model.addAttribute("children",0);
        //will be used to create default role
        model.addAttribute("house",request.getParameter("house"));
        //will be sued to load appropriate background image
        model.addAttribute("houseType",request.getParameter("houseType"));
       }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateEdit(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
        populate(model, domain,request);
        model.addAttribute("familyMembers", domain.getFamilyMembers());
        model.addAttribute("familyCount", domain.getFamilyMembers().size());
        model.addAttribute("qualifications", domain.getQualifications());
        model.addAttribute("qualificationCount", domain.getQualifications()
                .size());
        int noOfDaughters=0;
        int noOfSons=0;
        int noOfChildren=0;
        String spouseName=null;
        if(!domain.getFamilyMembers().isEmpty()){
        for(FamilyMember i:domain.getFamilyMembers()){
        	String relationName=i.getRelation().getName();
        	if(relationName.equals(ApplicationConstants.mr_IN_DAUGHTER)||relationName.equals(ApplicationConstants.en_US_DAUGHTER)){
        		noOfDaughters++;
        	}else if(relationName.equals(ApplicationConstants.mr_IN_SON)||relationName.equals(ApplicationConstants.en_US_SON)){
        		noOfSons++;
        	}else if(relationName.equals(ApplicationConstants.mr_IN_HUSBAND)||relationName.equals(ApplicationConstants.mr_IN_WIFE)||relationName.equals(ApplicationConstants.en_US_WIFE)||relationName.equals(ApplicationConstants.en_US_HUSBAND)){
        		spouseName=i.getName();
        		model.addAttribute("spouseName",spouseName);
        	}
        }
        }
        noOfChildren=noOfSons+noOfDaughters;
        model.addAttribute("daughters",noOfDaughters);
        model.addAttribute("sons",noOfSons);
        model.addAttribute("children",noOfChildren);
      //will be used to create default role
        model.addAttribute("house",request.getParameter("house"));
        //will be sued to load appropriate background image
        //this is set in session in case of post and put to display the image
        if(request.getSession().getAttribute("houseType")==null){
        model.addAttribute("houseType",request.getParameter("houseType"));
        }else{
            model.addAttribute("houseType",request.getSession().getAttribute("houseType"));
            request.getSession().removeAttribute("houseType");
        }
        domain.setAliasEnabled(true);
    }
    //private utility method for populating domain with family and qualifications
    /**
     * Populate family qualification.
     *
     * @param domain the domain
     * @param request the request
     * @param result the result
     */
    private void populateFamilyQualification(final Member domain, final HttpServletRequest request,final BindingResult result){
        List<FamilyMember> familyMembers = new ArrayList<FamilyMember>();
        Integer familyCount = Integer.parseInt(request
                .getParameter("familyCount"));
        for (int i = 1; i <= familyCount; i++) {
        	String relation=request.getParameter("familyMemberRelation"+ i);
        	if(relation!=null){
        	FamilyMember familyMember=new FamilyMember();

        	String name=request.getParameter("familyMemberName"+i);
        	if(name!=null){
        		if(!name.isEmpty()){
                	familyMember.setName(request.getParameter("familyMemberName"+ i));
        		}//else{
        			//result.rejectValue("familyMembers","NotEmpty");
        		//}
        	}

        	if(!relation.isEmpty()){
        		familyMember.setRelation((Relation) Relation.findById(Relation.class,Long.parseLong(relation)));
        	}

        	String id=request.getParameter("familyMemberId"+ i);
        	if(id!=null){
        		if(!id.isEmpty()){
        		familyMember.setId(Long.parseLong(id));
        		}
        	}

        	String version=request.getParameter("familyMemberVersion"+ i);
        	if(version!=null){
        		if(!version.isEmpty()){
        		familyMember.setVersion(Long.parseLong(version));
        		}
        	}

        	String locale=request.getParameter("familyMemberLocale"+ i);
 	        if(locale!=null){
 	        	if(!locale.isEmpty()){
 	        	familyMember.setLocale(locale);
 	        }
 	        }
 	        familyMembers.add(familyMember);
        }
        }
        domain.setFamilyMembers(familyMembers);

        List<Qualification> qualifications = new ArrayList<Qualification>();
        Integer qualificationCount = Integer.parseInt(request
                .getParameter("qualificationCount"));
        for (int i = 1; i <= qualificationCount; i++) {
            String degree=request.getParameter("qualificationDegree"+ i);
        	if(degree!=null){
            Qualification qualification = new Qualification();

            String detail=request.getParameter("qualificationDetail" + i);
            if(detail!=null){
            qualification.setDetails(detail);
            }

        	if(!degree.isEmpty()){
        		qualification.setDegree((Degree) Degree.findById(Degree.class,Long.parseLong(degree)));
        	}

            qualification.setLocale(domain.getLocale());

            String id=request.getParameter("qualificationId"+ i);
        	if(id!=null){
        		if(!id.isEmpty()){
        			qualification.setId(Long.parseLong(id));
        		}
        	}

        	String version=request.getParameter("qualificationVersion"+ i);
        	if(version!=null){
        		if(!version.isEmpty()){
        			qualification.setVersion(Long.parseLong(version));
        		}
        	}
        	String locale=request.getParameter("qualificationLocale"+ i);
 	        if(locale!=null){
 	        	if(!locale.isEmpty()){
 	        	qualification.setLocale(locale);
 	        }
 	        }
            qualifications.add(qualification);
         }
        }
        domain.setQualifications(qualifications);
    }
    //as we enter post and put we will populate domain with family members and qualifications
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#preValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void preValidateCreate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        populateFamilyQualification(domain,request,result);
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#preValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void preValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        populateFamilyQualification(domain,request,result);
    }
    //in case of errors we need to re populate domain with populate edit logic
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateCreateIfErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateCreateIfErrors(final ModelMap model,
            final Member domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "create_failed");
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateUpdateIfErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateUpdateIfErrors(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "update_failed");
    }
    //here we are just checking for version mis match in validation.there is no check for duplicate entries
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    /**
     * Delete family.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @RequestMapping(value = "/family/{id}/delete", method = RequestMethod.DELETE)
    public String deleteFamily(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
        FamilyMember familyMember=FamilyMember.findById(FamilyMember.class, id);
        familyMember.remove();
        return "info";
    }   

    /**
     * Delete qualification.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @RequestMapping(value = "/qualification/{id}/delete", method = RequestMethod.DELETE)
    public String deleteQualification(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
        Qualification qualification=Qualification.findById(Qualification.class, id);
        qualification.remove();
        return "info";
    }

    @Override
    protected void populateAfterCreate(final ModelMap model,
			final Member domain, final HttpServletRequest request) {
        //for displaying image on edit page after submission
        request.getSession().setAttribute("houseType",request.getParameter("houseType"));
    	//here when a new record is created an entry will be made in house member role asspciation
    	//with default role.This is done so that a new record always belong to some house on creation.
    	String isMember=request.getParameter("isMember");
    	if(isMember!=null){
    		if(!isMember.isEmpty()){
    			if(isMember.equals("true")){
    				Long houseId=Long.parseLong(request.getParameter("house"));
    				HouseMemberRoleAssociation houseMemberRoleAssociation=new HouseMemberRoleAssociation();
    				House house=House.findById(House.class,houseId);
    				houseMemberRoleAssociation.setFromDate(house.getFirstDate());
    				houseMemberRoleAssociation.setToDate(house.getLastDate());
    				houseMemberRoleAssociation.setHouse(house);
    				Date currentDate=new Date();
    				if(house.getLastDate().after(currentDate)){
    					houseMemberRoleAssociation.setIsSitting(true);
    				}
    				houseMemberRoleAssociation.setMember(domain);
    				houseMemberRoleAssociation.setLocale(domain.getLocale());
    				houseMemberRoleAssociation.setRecordIndex(1);
    				String houseType=house.getType().getType();
    				String defaultRole=null;
    				//here locale based code
    				if(domain.getLocale().equals("en_US")){
    					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    						defaultRole=ApplicationConstants.en_US_LOWERHOUSE_DEAFULTROLE;
    					}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    						defaultRole=ApplicationConstants.en_US_UPPERHOUSE_DEAFULTROLE;
    					}
    				}else if(domain.getLocale().equals("mr_IN")){
    					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    						defaultRole=ApplicationConstants.mr_IN_LOWERHOUSE_DEAFULTROLE;
    					}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    						defaultRole=ApplicationConstants.mr_IN_UPPERHOUSE_DEAFULTROLE;
    					}
    				}
    				houseMemberRoleAssociation.setRole(MemberRole.findByNameHouseTypeLocale(defaultRole, house.getType().getId(), domain.getLocale()));
    				houseMemberRoleAssociation.persist();
    			}
    		}
    	}
	}

    @Override
    protected void populateAfterUpdate(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
      //for displaying image on edit page after submission
       request.getSession().setAttribute("houseType",request.getParameter("houseType"));
    }

    @Override
    protected void populateUpdateIfNoErrors(final ModelMap model,
			final Member domain, final HttpServletRequest request) {
		populateIfNoErrors(model, domain, request);
	}
}
