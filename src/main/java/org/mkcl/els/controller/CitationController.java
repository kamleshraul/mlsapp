/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.CitationController.java
 * Created On: Oct 5, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class CitationController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/citation")
public class CitationController extends GenericController<Citation>{
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, 
			final Citation domain,
            final String locale, 
            final HttpServletRequest request) {
		domain.setLocale(locale);
        List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", locale);
        model.addAttribute("deviceTypes", deviceTypes);
        
        /*List<Status> statuses=Status.findAll(Status.class, "type", "desc", domain.getLocale());
        model.addAttribute("statuses", statuses);*/
    }
	
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, 
			final Citation domain,
            final HttpServletRequest request) {
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", domain.getLocale());
        model.addAttribute("deviceTypes", deviceTypes);
        DeviceType deviceType=domain.getDeviceType();
        List<Status> statuses = new ArrayList<Status>();
		try {
			statuses = Status.findStartingWith(deviceType.getDevice(), "name", "asc", domain.getLocale());
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        model.addAttribute("statuses", statuses);
    }

	@Override 	
	protected void customValidateUpdate(final Citation domain,
            final BindingResult result, 
            final HttpServletRequest request) {
	}
	
	@Override
	protected void customValidateCreate(final Citation domain,
            final BindingResult result, 
            final HttpServletRequest request) {
	}
}
