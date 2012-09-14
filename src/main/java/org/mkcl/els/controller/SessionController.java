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
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    	 List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "name", "desc", locale.toString());
    	 model.addAttribute("houseTypes",houseTypes);
   

       }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void populateEdit(final ModelMap model,
                                final Session domain,
                                final HttpServletRequest request) {

    	 List<SessionType> sessionTypes = SessionType.findAll(
	    		   SessionType.class, "sessionType", "asc", domain.getLocale());
    	 model.addAttribute("sessionType", sessionTypes);

    	 List<SessionPlace> sessionPlace = SessionPlace.findAll(
	    		   SessionPlace.class, "place", "asc", domain.getLocale());
    	 model.addAttribute("place",sessionPlace);
    	 List<HouseType> houseTypes1=HouseType.findAll(HouseType.class, "name", "desc", domain.getLocale());
    	 model.addAttribute("houseTypes",houseTypes1);
    	 List<House> houses=House.findAll(House.class, "name", "desc", domain.getLocale());
    	 model.addAttribute("houses", houses);
      }

	/**
	 * View rotation order.
	 *
	 * @param id the id
	 * @param model the model
	 * @param request the request
	 * @return the string
	 * @author compaq
	 * @since v1.0.0
	 */
	@RequestMapping(value="/{id}/viewRotationOrder", method = RequestMethod.GET)
    public String viewRotationOrder(final @PathVariable("id") Long id, final ModelMap model,
            final HttpServletRequest request) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String urlPattern=servletPath.split("\\/viewRotationOrder")[0].replace("/"+id,"");
        String messagePattern=urlPattern.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", urlPattern);
        Session domain = Session.findById(Session.class, id);
        model.addAttribute("domain", domain);
       List<Group> groups=Group.findByHouseTypeSessionTypeYear(domain.getHouse().getType(), domain.getType(), domain.getYear());
       model.addAttribute("groups", groups);
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
            request.getSession().removeAttribute("type");
        }
        return urlPattern+"/"+"viewrotationorder";
    }



}

