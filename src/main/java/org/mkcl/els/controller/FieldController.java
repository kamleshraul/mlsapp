/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.FieldController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class FieldController.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_fields")
public class FieldController extends GenericController<Field> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mkcl.els.controller.GenericController#populateNew(org.springframework
     * .ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.util.Locale,
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model,
                               final Field domain,
                               final Locale locale,
                               final HttpServletRequest request) {
        domain.setLocale("");
    }
}
