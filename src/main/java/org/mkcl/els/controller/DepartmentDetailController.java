/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DepartmentDetailController.java
 * Created On: Jun 2, 2012
 */
package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DepartmentDetail;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class DepartmentDetailController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/departmentdetail")
public class DepartmentDetailController extends GenericController<DepartmentDetail> {
	 
 	/* (non-Javadoc)
 	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
 	 */
 	@Override
	    protected void populateNew(final ModelMap model,
	            final DepartmentDetail domain,
	            final String locale,
	            final HttpServletRequest request) {
	    	domain.setLocale(locale);
	        String strDepartment = ((CustomParameter) CustomParameter.findByName(
	                CustomParameter.class, "DEFAULT_DEPARTMENT", locale)).getValue();
	        Department defaultDepartment = Department.findByFieldName(Department.class, "name",
	        		strDepartment, locale);
	        domain.setDepartment(defaultDepartment);
	        List<Department> departments = Department.findAll(
	                Department.class, "name", "asc", locale);
	        model.addAttribute("department",departments);
	    }


	    /*
	     * (non-Javadoc)
	     * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap,
	     * org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	     */
	    @Override
	    protected void populateEdit(final ModelMap model,
	            final DepartmentDetail domain,
	            final HttpServletRequest request) {
	    	domain.setLocale(domain.getLocale());
	        List<Department> departments = Department.findAll(
	                Department.class, "name", "asc", domain.getLocale());
	        model.addAttribute("department", departments);
	    }
}
