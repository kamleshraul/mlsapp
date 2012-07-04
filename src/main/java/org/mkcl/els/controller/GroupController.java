/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.GroupController.java
 * Created On: 20 Jun, 2012
 */
package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Group;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class GroupController.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Controller
@RequestMapping("/group")
public class GroupController extends GenericController<Group> {
    
    @Override
    protected void customValidateCreate(final Group domain,
		final BindingResult result, final HttpServletRequest request) {
	customValidateGroup(domain, result, request);
    }
    
    @Override
    protected void customValidateUpdate(final Group domain,
		final BindingResult result, final HttpServletRequest request) {
	customValidateGroup(domain, result, request);
    }
    
    private void customValidateGroup(final Group domain, final BindingResult result, final HttpServletRequest request) {
	// Check for version mismatch
	if (domain.isVersionMismatch()) {
	    result.rejectValue("VersionMismatch", "version");
	}
	
	// Check for duplicate instance if the instance has a field "name"
	if(domain.getName()!=null){
		String nameValue = domain.getName().toString();
		try {	    
		    boolean duplicateParameter = domain.isDuplicate("name", nameValue);
		    Object[] params = new Object[1];
		    params[0] = nameValue;
		    if (duplicateParameter) {
			result.rejectValue("name", "NonUnique", params,
				"Duplicate Parameter");
		    }
		} catch (SecurityException e) {
		    logger.error(e.getMessage());
		    return;
		} catch (IllegalArgumentException e) {
		    logger.error(e.getMessage());
		    return;
		} 
	}
	
    }
    
}
