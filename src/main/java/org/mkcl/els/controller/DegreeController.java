/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DegreeController.java
 * Created On: Mar 17, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Degree;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class DegreeController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_degrees")
public class DegreeController extends GenericController<Degree> {
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	protected void customValidateCreate(final Degree domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	protected void customValidateUpdate(final Degree domain,
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
	private void customValidate(final Degree domain,
			final BindingResult result, final HttpServletRequest request) {
		Map<String, String> names = new HashMap<String, String>();
		names.put("degree", domain.getDegree());

		// Check for duplicate instances
		Boolean duplicateParameter = domain.isDuplicate(names);
		Object[] params = new Object[1];
		params[0] = domain.getLocale().toString();
		if (duplicateParameter) {
			result.rejectValue("degree", "NonUnique", params,
					"Duplicate Parameter");
		}
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}
}
