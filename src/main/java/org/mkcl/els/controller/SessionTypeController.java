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

import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class SessionController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/sessiontype")
public class SessionTypeController extends GenericController<SessionType> {


    /*
     * (non-Javadoc)
     * @see
     * org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain
     * , org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final SessionType domain,
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

    @Override
    protected void customValidateUpdate(final SessionType domain,
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
    private void customValidate(final SessionType domain,
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
