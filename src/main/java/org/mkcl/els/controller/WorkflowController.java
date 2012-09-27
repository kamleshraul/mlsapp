/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.WorkflowController.java
 * Created On: Sep 24, 2012
 */
package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;


import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Workflow;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkflowController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/wf")
public class WorkflowController extends GenericController<Workflow>{
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, final Workflow domain,
            final String locale, final HttpServletRequest request) {
       domain.setLocale(locale);
       List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", domain.getLocale());
       model.addAttribute("deviceTypes",deviceTypes);
    }
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, final Workflow domain,
            final HttpServletRequest request) {
			List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", domain.getLocale());
	       model.addAttribute("deviceTypes",deviceTypes);

    }
}
