/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.SessionController.java
 * Created On: Mar 13, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionController.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_sessions")
public class SessionController extends GenericController<Session> {

	/**
	 * Custom validate create.
	 * 
	 * @param domain
	 *            the domain
	 * @param result
	 *            the result
	 * @param request
	 *            the request
	 */
	protected void customValidateCreate(final Session domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl
	 * .els.domain.BaseDomain, org.springframework.validation.BindingResult,
	 * javax.servlet.http.HttpServletRequest)
	 */

	/**
	 * Custom validate update.
	 * 
	 * @param domain
	 *            the domain
	 * @param result
	 *            the result
	 * @param request
	 *            the request
	 */
	protected void customValidateUpdate(final Session domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}

	/**
	 * Custom validate.
	 * 
	 * @param domain
	 *            the session
	 * @param result
	 *            the result
	 * @param request
	 *            the request
	 * @author Anand
	 * @since v1.0.0 Custom validate.
	 */
	private void customValidate(final Session domain,
			final BindingResult result, final HttpServletRequest request) {
		Map<String, String> names = new HashMap<String, String>();
		names.put("sessionType", domain.getSessionType());

		// Check for duplicate instances
		Boolean duplicateParameter = domain.isDuplicate(names);
		Object[] params = new Object[1];
		params[0] = domain.getLocale().toString();
		if (duplicateParameter) {
			result.rejectValue("sessionType", "NonUnique", params,
					"Duplicate Parameter");
		}
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}

}
