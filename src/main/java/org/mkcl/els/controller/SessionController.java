/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.SessionController.java
 * Created On: Apr 6, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.CustomParameter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/session")
public class SessionController extends GenericController<Session> {
	/** The Constant ASC. */
	private static final String ASC = "asc"; 

   /* (non-Javadoc)
    * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
    */
   @Override
	protected void customValidateCreate(final Session domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(final Session domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	/**
	 * Custom validate.
	 *
	 * @param sessiondetails the sessiondetails
	 * @param result the result
	 * @param request the request
	 */
	private void customValidate(final Session sessiondetails,
			final BindingResult result, final HttpServletRequest request) {
		new HashMap<String, String>();
		if (sessiondetails.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		if (result.hasErrors()) {
			System.out.println("error");
		}
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void populateNew(	final ModelMap model,
                               final Session domain,
                               final String locale,
                               final HttpServletRequest request) {
		domain.setLocale(locale.toString());
		 String sessiontype = ((CustomParameter) CustomParameter.findByName(
                 CustomParameter.class, "DEFAULT_SESSIONTYPE", locale.toString())).getValue();
		 SessionType defaultSessionType = SessionType.findByFieldName(SessionType.class, "sessionType", sessiontype, locale.toString());
		 domain.setType(defaultSessionType);
	     List<SessionType> sessionTypes = SessionType.findAll(
	    		   SessionType.class, "sessionType", "asc", locale.toString());
	     model.addAttribute("sessionType", sessionTypes); 
	     
    	 String sessionplace = ((CustomParameter) CustomParameter.findByName(
                 CustomParameter.class, "DEFAULT_SESSIONPLACE", locale.toString())).getValue();
    	 SessionPlace defaultPlace = SessionPlace.findByFieldName(
	    		   SessionPlace.class, "place", sessionplace, locale.toString());
    	 domain.setPlace(defaultPlace);
    	 List<SessionPlace> sessionPlace = SessionPlace.findAll(
	    		   SessionPlace.class, "place", "asc", locale.toString());
    	 model.addAttribute("place",sessionPlace);
    	 
    	 model.addAttribute("houseId",request.getParameter("houseId"));
    	 
    }
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void populateEdit(final ModelMap model,
                                final Session domain,
                                final HttpServletRequest request) {
		
    	House house=domain.getHouse();
    	 List<SessionType> sessionTypes = SessionType.findAll(
	    		   SessionType.class, "sessionType", "asc", domain.getLocale());
    	 model.addAttribute("sessionType", sessionTypes);
    	 
    	 List<SessionPlace> sessionPlace = SessionPlace.findAll(
	    		   SessionPlace.class, "place", "asc", domain.getLocale());
    	 model.addAttribute("place",sessionPlace);
    	 
    	 model.addAttribute("houseId", house.getId());
      }

	
	  
	 
	 /* (non-Javadoc)
 	 * @see org.mkcl.els.controller.GenericController#populateList(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest, java.lang.String, org.mkcl.els.common.vo.AuthUser)
 	 */
	@Override
 	protected void populateList(final ModelMap model, final HttpServletRequest request,
				final String locale, final AuthUser currentUser) {
		 model.addAttribute("houseId",request.getParameter("houseId"));
		}


}

