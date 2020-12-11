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
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class DepartmentDetailController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/subdepartment")
public class SubDepartmentController extends GenericController<SubDepartment> {
	 
 	/* (non-Javadoc)
 	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
 	 */
 	@Override
	    protected void populateNew(final ModelMap model,
	            final SubDepartment domain,
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
	            final SubDepartment domain,
	            final HttpServletRequest request) {
	    	domain.setLocale(domain.getLocale());
	        List<Department> departments = Department.findAll(Department.class, "name", "asc", domain.getLocale());
	        model.addAttribute("department", departments);
	    }


		@Override
		protected void populateCreateIfNoErrors(ModelMap model,
				SubDepartment domain, HttpServletRequest request)
				throws Exception {
			if(domain.getMinistryDisplayName()==null || domain.getMinistryDisplayName().isEmpty()) {
				domain.setMinistryDisplayName(domain.getName());
			}
			if(domain.getDisplayName()==null || domain.getDisplayName().isEmpty()) {
				domain.setDisplayName(domain.getName());
			}
		}


		@Override
		protected void populateUpdateIfNoErrors(ModelMap model,
				SubDepartment domain, HttpServletRequest request)
				throws Exception {
			SubDepartment originalSubDepartment = SubDepartment.findById(SubDepartment.class, domain.getId());
			
			if(originalSubDepartment.getMinistryDisplayName()!=null 
					&& originalSubDepartment.getMinistryDisplayName().equals(originalSubDepartment.getName())
					&& !originalSubDepartment.getName().equals(domain.getName())) {
				
				domain.setMinistryDisplayName(domain.getName());
			}			
			if(originalSubDepartment.getMinistryDisplayName()==null || originalSubDepartment.getMinistryDisplayName().isEmpty()) {
				domain.setMinistryDisplayName(domain.getName());
			}
			
			if(originalSubDepartment.getDisplayName()!=null 
					&& originalSubDepartment.getDisplayName().equals(originalSubDepartment.getName())
					&& !originalSubDepartment.getName().equals(domain.getName())) {
				
				domain.setDisplayName(domain.getName());
			}			
			if(originalSubDepartment.getDisplayName()==null || originalSubDepartment.getDisplayName().isEmpty()) {
				domain.setDisplayName(domain.getName());
			}
		}
	    
	    
}
