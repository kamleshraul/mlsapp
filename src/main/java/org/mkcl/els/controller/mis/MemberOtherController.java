/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberOtherController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.PositionHeld;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class MemberOtherController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/other")
public class MemberOtherController extends GenericController<Member>{

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void preValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
		populate(domain, result, request);
    }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void populateEdit(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
		if(!domain.getPositionsHeld().isEmpty()){
	        model.addAttribute("positionCount", domain.getPositionsHeld().size());
	        model.addAttribute("positions", domain.getPositionsHeld());
		}else{
	        model.addAttribute("positionCount", 0);
		}
    }

	/**
	 * Populate.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void populate(final Member domain,
            final BindingResult result, final HttpServletRequest request){
			List<PositionHeld> positions = new ArrayList<PositionHeld>();
			 Integer positionCount = Integer.parseInt(request
			 .getParameter("positionCount"));
			 for (int i = 1; i <= positionCount; i++) {
			  PositionHeld positionHeld = new PositionHeld();
			 String fromDate=request.getParameter("positionFromDate" + i);
			 if(fromDate!=null){
			 if(!fromDate.isEmpty()){
			 positionHeld.setFromDate(fromDate);
			 }
			 }

			 String toDate=request.getParameter("positionToDate" + i);
			 if(toDate!=null){
			 positionHeld.setToDate(toDate);
			 }

			 String position=request.getParameter("positionPosition" + i);
			 if(position!=null){
			 positionHeld.setPosition(position);
			 }

			String id=request.getParameter("positionId"+ i);
	        if(id!=null){
	        	if(!id.isEmpty()){
	        	positionHeld.setId(Long.parseLong(id));
	        	}
	        }

	        String version=request.getParameter("positionVersion"+ i);
	        if(version!=null){
	        	if(!version.isEmpty()){
	        	positionHeld.setVersion(Long.parseLong(version));
	        	}
	        }

	        String locale=request.getParameter("positionLocale"+ i);
	        if(locale!=null){
	        	if(!locale.isEmpty()){
	        	positionHeld.setLocale(locale);
	        }
	        }
			 positions.add(positionHeld);
		 }
			 domain.setPositionsHeld(positions);
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
    @SuppressWarnings("rawtypes")
	protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
		// Set Date Editor
        CustomParameter parameter = CustomParameter.findByName(
                CustomParameter.class, "SERVER_DATEFORMAT", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue());
        dateFormat.setLenient(true);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));
	}


    /**
     * Delete position.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @RequestMapping(value = "/position/{id}/delete", method = RequestMethod.DELETE)
    public String deletePosition(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
	    PositionHeld positionHeld=PositionHeld.findById(PositionHeld.class, id);
	    positionHeld.remove();
        return "info";
    }
}


