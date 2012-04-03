/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ApplicationLocaleController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.ApplicationLocale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class ApplicationLocaleController.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/locale")
public class ApplicationLocaleController extends
        GenericController<ApplicationLocale> {

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final ApplicationLocale domain,
                                        final BindingResult result,
                                        final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateUpdate(final ApplicationLocale domain,
                                        final BindingResult result,
                                        final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * Custom validate.
     *
     * @param applicationLocale the application locale
     * @param result the result
     * @param request the request
     * @author nileshp
     * @since v1.0.0
     * Custom validate.
     */
    private void customValidate(final ApplicationLocale applicationLocale,
                                final BindingResult result,
                                final HttpServletRequest request) {
        Map<String, String> names = new HashMap<String, String>();
        names.put("language", applicationLocale.getLanguage());
        names.put("country", applicationLocale.getCountry());
        names.put("variant", applicationLocale.getVariant());
        // Check for duplicate instances
        Boolean duplicateParameter = applicationLocale.isDuplicate(names);
        Object[] params = new Object[1];
        params[0] = applicationLocale.getLocaleString();
        if (duplicateParameter) {
            result.rejectValue(
                    "language", "NonUnique", params, "Duplicate Parameter");
        }
        // Check for version mismatch
        if (applicationLocale.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.util.Locale, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model,
                               final ApplicationLocale domain,
                               final String locale,
                               final HttpServletRequest request) {
        domain.setLocale(locale);
    }
}
