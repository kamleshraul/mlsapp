/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MessageResourceController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.MessageResource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class MessageResourceController.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/message")
public class MessageResourceController extends
        GenericController<MessageResource> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl
     * .els.domain.BaseDomain, org.springframework.validation.BindingResult,
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final MessageResource domain,
                                        final BindingResult result,
                                        final HttpServletRequest request) {
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
    protected void customValidateUpdate(final MessageResource domain,
                                        final BindingResult result,
                                        final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * Custom validate.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     * @author nileshp
     * @since v1.0.0 Custom validate.
     */
    private void customValidate(final MessageResource domain,
                                final BindingResult result,
                                final HttpServletRequest request) {
        // Check for duplicate instances
        Boolean duplicateParameter = domain.isDuplicate(
                "code", domain.getCode());
        Object[] params = new Object[1];
        params[0] = domain.getCode();
        if (duplicateParameter) {
            result.rejectValue(
                    "code", "NonUnique", params, "Duplicate Parameter");
        }
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }

    }
}
