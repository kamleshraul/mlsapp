/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.AssemblyTermController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.AssemblyTerm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class AssemblyTermController.
 *
 * @author samiksham
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_assemblyterms")
public class AssemblyTermController extends GenericController<AssemblyTerm> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl
     * .els.domain.BaseDomain, org.springframework.validation.BindingResult,
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final AssemblyTerm domain,
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
    protected void customValidateUpdate(final AssemblyTerm domain,
                                        final BindingResult result,
                                        final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * Custom validate.
     *
     * @param assemblyterm the assemblyterm
     * @param result the result
     * @param request the request
     * @author nileshp
     * @since v1.0.0 Custom validate.
     */
    private void customValidate(final AssemblyTerm assemblyterm,
                                final BindingResult result,
                                final HttpServletRequest request) {
        Map<String, String> names = new HashMap<String, String>();
        if (assemblyterm.getTerm() != null) {
            names.put("term", assemblyterm.getTerm().toString());
            // Check for duplicate instances
            Boolean duplicateParameter = assemblyterm.isDuplicate(
                    "term", assemblyterm.getTerm().toString());
            Object[] params = new Object[1];
            params[0] = assemblyterm.getTerm();
            if (duplicateParameter) {
                result.rejectValue(
                        "term", "NonUnique", params, "Duplicate Parameter");
            }
            // Check for version mismatch
            if (assemblyterm.isVersionMismatch()) {
                result.rejectValue("VersionMismatch", "version");
            }

            if (result.hasErrors()) {
                System.out.println("error");
            }
        }

    }

}
