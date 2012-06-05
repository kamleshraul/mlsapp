/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberContactController.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.controller.mis;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class MemberContactController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/contact")
public class MemberContactController extends GenericController<Member> {

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateEdit(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
    	model.addAttribute("states", State.findAll(State.class, "name",
                ApplicationConstants.ASC, domain.getLocale()));
        String strDefaultState = ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DEFAULT_STATE", domain.getLocale()))
                .getValue();
        State defaultState = State.findByFieldName(State.class, "name",
                strDefaultState, domain.getLocale());

        List<District> districts = District.findDistrictsByStateId(
                defaultState.getId(), "name", ApplicationConstants.ASC,
                domain.getLocale());
        model.addAttribute("districts", districts);
        //here we will populate tehsils depending on if the district has been set.If it is not then
        //we will populate the tehsils under the district 0.Else we will populate tehsils under the districts
        //in the domain

        District defaultDistrict1 = districts.get(0);
        District defaultDistrict2 = districts.get(0);
        District defaultDistrict3 = districts.get(0);


    	//when bindinG form controls to fields of nested objects it is necessary to instantiate
    	//the nested objects as compiler has no way to initialize a custom object and hence set it to null.
    	//This is necessary only when we are first creating these objects.
        if (domain.getContact() == null) {
            domain.setContact(new Contact());
        }
        if (domain.getPermanentAddress() == null) {
            domain.setPermanentAddress(new Address());
            domain.getPermanentAddress().setState(defaultState);
        }else{
            if(domain.getPermanentAddress().getDistrict()!=null){
                defaultDistrict1=domain.getPermanentAddress().getDistrict();
            }
        }
        if (domain.getPresentAddress() == null) {
            domain.setPresentAddress(new Address());
            domain.getPresentAddress().setState(defaultState);
        }else{
            if(domain.getPresentAddress().getDistrict()!=null){
                defaultDistrict2=domain.getPresentAddress().getDistrict();
            }
        }
        if (domain.getOfficeAddress() == null) {
            domain.setOfficeAddress(new Address());
            domain.getOfficeAddress().setState(defaultState);
        }else{
            if(domain.getOfficeAddress().getDistrict()!=null){
                defaultDistrict3=domain.getOfficeAddress().getDistrict();
            }
        }
        model.addAttribute("tehsils1", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict1, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils2", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict2, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils3", Tehsil.findAllByFieldName(Tehsil.class,
                        "district", defaultDistrict3, "name", ApplicationConstants.ASC,
                        domain.getLocale()));
        domain.getContact().setLocale(domain.getLocale());
        domain.getPermanentAddress().setLocale(domain.getLocale());
        domain.getPresentAddress().setLocale(domain.getLocale());
        domain.getOfficeAddress().setLocale(domain.getLocale());
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

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customInitBinderSuperClass(java.lang.Class, org.springframework.web.bind.WebDataBinder)
     */
    @Override
    protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
        binder.registerCustomEditor(Tehsil.class, new BaseEditor(new Tehsil()));
        binder.registerCustomEditor(District.class, new BaseEditor(
                new District()));
        binder.registerCustomEditor(State.class, new BaseEditor(new State()));
    }
    
    @Override
    protected void populateIfNoErrors(final ModelMap model, final Member domain,
			final HttpServletRequest request) {
	   	Member member=Member.findById(Member.class, domain.getId());
    	  if(!member.getMemberType().getType().equals("member")){
    		  Credential credential1=null;
    		  if(member.getContact()!=null)
    			  if(member.getContact().getEmail1()!="")
    				  credential1=Credential.findByFieldName(Credential.class, "username", member.getContact().getEmail1(),null);
    		  if(credential1==null){
    			if(domain.getContact().getEmail1()!=""){
    				credential1=new Credential();
    				credential1.setUsername(domain.getContact().getEmail1());
    				SecureRandom random = new SecureRandom();  
    				String str = new BigInteger(60, random).toString(32);
    				credential1.setPassword(str);
    				credential1.setEnabled(false);
    				credential1.setEmail(domain.getContact().getEmail1());
    				credential1.persist();
    			}
    		 }else{
    			 credential1.setUsername(domain.getContact().getEmail1());
    			 credential1.setEmail(domain.getContact().getEmail1());
    			 credential1.merge();
    		 }
    		  	User user1=User.findById(User.class, domain.getId());
    		  	if(user1==null){
    		  		if(domain.getContact().getEmail1()!=""){
    		  			User user=new User();
    		  			user.setFirstName(member.getFirstName());
    		  			user.setMiddleName(member.getMiddleName());
    		  			user.setLastName(member.getLastName());
    		  			user.setTitle(member.getTitle().getName());
    		  			user.setCredential(credential1);
    		  			user.setLocale(domain.getLocale());    		  			
    		  			user.persist();
    		  			User.assignMemberId(domain.getId(), user.getId());
    		  		}
    		   	}
        }
        	
        
    		  
  }

}