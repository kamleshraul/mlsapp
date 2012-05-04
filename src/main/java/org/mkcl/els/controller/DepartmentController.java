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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Department;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
	        domain.setLocale(locale);
	    	List<Department> deptList = Department.findAll(
	    			 Department.class, "name", "asc", locale);
	    	 model.addAttribute("parentDepartment", deptList);
	    }


	   /* (non-Javadoc)
   	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
   	 */
   	@Override
	   protected void populateEdit(final ModelMap model,
	                                   final Department domain,
	                                   final HttpServletRequest request) {
	        domain.setLocale(domain.getLocale());
	        model.addAttribute("parent", domain.getParentId());
	          List<Department> deptList = Department.findAll(
	    			 Department.class, "name", "asc", domain.getLocale());
	        model.addAttribute("parentDepartment", deptList);

   	}
}
