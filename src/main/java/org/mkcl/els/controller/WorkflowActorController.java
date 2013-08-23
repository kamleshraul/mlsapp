/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.WorkflowActorController.java
 * Created On: Sep 17, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowActor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkflowActorController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/workflowactor")
public class WorkflowActorController extends GenericController<WorkflowActor>{

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model,
			final WorkflowActor domain, final String locale,
			final HttpServletRequest request) {
		try {
			domain.setLocale(locale);
			List<UserGroup> userGroups = UserGroup.findAll(UserGroup.class,
					"userGroupType", "desc", domain.getLocale());
			model.addAttribute("userGroups", userGroups);
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null) {
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model,
			final WorkflowActor domain, final HttpServletRequest request) {
		try {
			List<UserGroup> userGroups = UserGroup.findAll(UserGroup.class,"userGroupType", "desc", domain.getLocale());
			model.addAttribute("userGroups", userGroups);
		} catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}					
			model.addAttribute("error", message);
		}

	}
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final WorkflowActor domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(final WorkflowActor domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	/**
	 * Custom validate.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void customValidate(final WorkflowActor domain,
			final BindingResult result, final HttpServletRequest request) {
		new HashMap<String, String>();
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		if (result.hasErrors()) {
			System.out.println("error");
		}
	}
}
