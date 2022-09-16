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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
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
      //will be sued to load appropriate background image
        //this is set in session in case of post and put to display the image
        if(request.getSession().getAttribute("houseType")==null){
        model.addAttribute("houseType",request.getParameter("houseType"));
        }else{
            model.addAttribute("houseType",request.getSession().getAttribute("houseType"));
            request.getSession().removeAttribute("houseType");
        }
        model.addAttribute("states", State.findAll(State.class, "name",
                ApplicationConstants.ASC, domain.getLocale()));
       
        String strDefaultState = ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DEFAULT_STATE", domain.getLocale()))
                .getValue();
        State defaultState = State.findByFieldName(State.class, "name",
                strDefaultState, domain.getLocale());

        List<District> districts = new ArrayList<District>();
        try{
        	districts = District.findDistrictsByStateId(
                defaultState.getId(), "name", ApplicationConstants.ASC,
                domain.getLocale());
        }catch (ELSException e) {
        	model.addAttribute("MEMBER_CONTACT_CONTROLLER", "Request can not be completed at the moment.");
		}
        model.addAttribute("districts", districts);
        //here we will populate tehsils depending on if the district has been set.If it is not then
        //we will populate the tehsils under the district 0.Else we will populate tehsils under the districts
        //in the domain

        District defaultDistrict2 = districts.get(0);
        District defaultDistrict3 = districts.get(0);
        District defaultDistrict1 = districts.get(0);
        District defaultDistrict4 = districts.get(0);
        District defaultDistrict5 = districts.get(0);        
        District defaultDistrict6 = districts.get(0);
        District defaultDistrict7 = districts.get(0);
        District defaultDistrict8 = districts.get(0);
        District defaultDistrict9 = districts.get(0);
        District defaultDistrict10 = districts.get(0);
        District defaultDistrict11 = districts.get(0);

    	//when bindinG form controls to fields of nested objects it is necessary to instantiate
    	//the nested objects as compiler has no way to initialize a custom object and hence set it to null.
    	//This is necessary only when we are first creating these objects.
        
        //these are used to display appropriate labels and addresses
        int permanentAddCount=0;
        int presentAddCount=0;
        int officeAddCount=0;
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
        if (domain.getPermanentAddress1() == null) {
            domain.setPermanentAddress1(new Address());
            domain.getPermanentAddress1().setState(defaultState);
        }else{
        	permanentAddCount++;
            if(domain.getPermanentAddress1().getDistrict()!=null){
                defaultDistrict6=domain.getPermanentAddress1().getDistrict();
            }
        }
        if (domain.getPermanentAddress2() == null) {
            domain.setPermanentAddress2(new Address());
            domain.getPermanentAddress2().setState(defaultState);
        }else{
        	permanentAddCount++;
            if(domain.getPermanentAddress2().getDistrict()!=null){
                defaultDistrict7=domain.getPermanentAddress2().getDistrict();
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
        if (domain.getPresentAddress1() == null) {
            domain.setPresentAddress1(new Address());
            domain.getPresentAddress1().setState(defaultState);
        }else{
        	presentAddCount++;
            if(domain.getPresentAddress1().getDistrict()!=null){
                defaultDistrict8=domain.getPresentAddress1().getDistrict();
            }
        }
        if (domain.getPresentAddress2() == null) {
            domain.setPresentAddress2(new Address());
            domain.getPresentAddress2().setState(defaultState);
        }else{
        	presentAddCount++;
            if(domain.getPresentAddress2().getDistrict()!=null){
                defaultDistrict9=domain.getPresentAddress2().getDistrict();
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
        if (domain.getOfficeAddress1() == null) {
            domain.setOfficeAddress1(new Address());
            domain.getOfficeAddress1().setState(defaultState);
        }else{
        	officeAddCount++;
            if(domain.getOfficeAddress1().getDistrict()!=null){
                defaultDistrict10=domain.getOfficeAddress1().getDistrict();
            }
        }
        if (domain.getOfficeAddress2() == null) {
            domain.setOfficeAddress2(new Address());
            domain.getOfficeAddress2().setState(defaultState);
        }else{
        	officeAddCount++;
            if(domain.getOfficeAddress2().getDistrict()!=null){
                defaultDistrict11=domain.getOfficeAddress2().getDistrict();
            }
        }
        if (domain.getTempAddress1() == null) {
            domain.setTempAddress1(new Address());
            domain.getTempAddress1().setState(defaultState);
        }else{
            if(domain.getTempAddress1().getDistrict()!=null){
                defaultDistrict4=domain.getTempAddress1().getDistrict();
            }
        }
        if (domain.getTempAddress2() == null) {
            domain.setTempAddress2(new Address());
            domain.getTempAddress2().setState(defaultState);
        }else{
            if(domain.getTempAddress2().getDistrict()!=null){
                defaultDistrict5=domain.getTempAddress2().getDistrict();
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
        model.addAttribute("tehsils4", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict4, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils5", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict5, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils6", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict6, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils7", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict7, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils8", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict8, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils9", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict9, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils10", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict10, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils11", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict11, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        domain.getContact().setLocale(domain.getLocale());
        domain.getPermanentAddress().setLocale(domain.getLocale());
        domain.getPermanentAddress1().setLocale(domain.getLocale());
        domain.getPermanentAddress2().setLocale(domain.getLocale());

        domain.getPresentAddress().setLocale(domain.getLocale());
        domain.getPresentAddress1().setLocale(domain.getLocale());
        domain.getPresentAddress2().setLocale(domain.getLocale());

        domain.getOfficeAddress().setLocale(domain.getLocale());
        domain.getOfficeAddress1().setLocale(domain.getLocale());
        domain.getOfficeAddress2().setLocale(domain.getLocale());

        domain.getTempAddress1().setLocale(domain.getLocale());
        domain.getTempAddress2().setLocale(domain.getLocale());
        
        model.addAttribute("permanentAddCount",permanentAddCount);
        model.addAttribute("presentAddCount",presentAddCount);
        model.addAttribute("officeAddCount",officeAddCount);

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
    protected void populateAfterUpdate(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
      //for displaying image on edit page after submission
       request.getSession().setAttribute("houseType",request.getParameter("houseType"));
    }


}