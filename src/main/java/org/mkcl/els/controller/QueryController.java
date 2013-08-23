/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.QueryController.java
 * Created On: Jul 20, 2013
 * @since v1.0.0
 */

package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;


// TODO: Auto-generated Javadoc
/**
 * The Class QueryController.
 *
 * @author vikasg
 * @since v1.0.0
 */
@Controller
@RequestMapping("/query")
public class QueryController extends GenericController<Query> {
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(Query domain, BindingResult result,
			HttpServletRequest request) {
		customValidateQuery(domain, result, request);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(Query domain, BindingResult result,
			HttpServletRequest request) {
		customValidateQuery(domain, result, request);
	}

	/**
	 * Custom validate query.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void customValidateQuery(final Query domain, final BindingResult result,
            final HttpServletRequest request) {
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }       
    }
	
}
