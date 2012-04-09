/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DepartmentController.java
 * Created On: Apr 9, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Ministry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
// TODO: Auto-generated Javadoc

/**
 * The Class DepartmentController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/department")
public class DepartmentController extends GenericController<Department>{
	
	 /* (non-Javadoc)
 	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
 	 */
 	@Override
	    protected void populateNew(final ModelMap model,
	                               final Department domain,
	                               final String locale,
	                               final HttpServletRequest request) {
	        
	    	 String ministry = ((CustomParameter) CustomParameter.findByName(
	                 CustomParameter.class, "DEFAULT_MINISTRY", locale)).getValue();
	    	 List<Ministry> ministryList = Ministry.findAll(
		    		   Ministry.class, "name", "asc", locale);
		       Ministry defaultMinistry = Ministry.findByFieldName(
		    		   Ministry.class, "name", ministry, locale);
		       domain.setMinistry(defaultMinistry);
	          model.addAttribute("ministry", ministryList);
	    }
	 
	
	   /* (non-Javadoc)
   	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
   	 */
   	@Override
	   protected void populateEdit(final ModelMap model,
	                                   final Department domain,
	                                   final HttpServletRequest request) {
	        domain.setLocale(domain.getLocale());
	        List<Ministry> ministryList = Ministry.findAll(
		    		   Ministry.class, "name", "asc", domain.getLocale());
	        model.addAttribute("ministry", ministryList);
	       }
}
