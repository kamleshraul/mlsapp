/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.HolidayController.java
 * Created On: Jan 8, 2013
 */
package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Holiday;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class HolidayController.
 *
 * @author dhananjay
 * @since v1.0.0
 */
@Controller
@RequestMapping("/holiday")
public class HolidayController extends GenericController<Holiday> {
	
	@Override
	protected void customValidateCreate(final Holiday domain,
            final BindingResult result, final HttpServletRequest request) {
        customValidateHoliday(domain, result, request);
    }
	
	@Override
	protected void customValidateUpdate(final Holiday domain,
            final BindingResult result, final HttpServletRequest request) {
        customValidateHoliday(domain, result, request);
    }
	
	private void customValidateHoliday(final Holiday domain, final BindingResult result,
            final HttpServletRequest request) {
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }       
    }
	
}
