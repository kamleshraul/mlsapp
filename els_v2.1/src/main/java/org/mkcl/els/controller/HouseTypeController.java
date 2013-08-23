/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.AssemblyCouncilController.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.HouseType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class AssemblyCouncilTypeController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/housetype")
public class HouseTypeController extends GenericController<HouseType>{

	/**
	 * Custom validate create.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	@Override
    protected void customValidateCreate(final HouseType domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		customValidate(domain, result, request);
	}

	/**
	 * Custom validate update.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	@Override
    protected void customValidateUpdate(final HouseType domain,
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
	private void customValidate(final HouseType domain,
			final BindingResult result, final HttpServletRequest request) {
		Map<String, String> names = new HashMap<String, String>();
		names.put("type", domain.getType());

		// Check for duplicate instances
		Boolean duplicateParameter = domain.isDuplicate(names);
		Object[] params = new Object[1];
		params[0] = domain.getLocale().toString();
		if (duplicateParameter) {
			result.rejectValue("type", "NonUnique", params,
					"Duplicate Parameter");
		}
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}
}
