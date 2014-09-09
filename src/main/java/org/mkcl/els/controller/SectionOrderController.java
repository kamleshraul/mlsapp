/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.TitleController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.SectionOrder;
import org.mkcl.els.domain.SectionOrderSeries;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class SectionOrderController.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
@Controller
@RequestMapping("/sectionorder")
public class SectionOrderController extends GenericController<SectionOrder> {
	
	@Override
	protected void populateNew(final ModelMap model, 
    		final SectionOrder domain,
            final String locale, 
            final HttpServletRequest request) {
        domain.setLocale(locale);
        if(domain.getSectionOrderSeries()!=null) {
			model.addAttribute("series", domain.getSectionOrderSeries().getId());
		} else {
			String series = request.getParameter("series");
	        model.addAttribute("series", series);
		}                
    }	
	
	protected void populateEdit(final ModelMap model, final SectionOrder domain,
            final HttpServletRequest request) {
		if(domain.getSectionOrderSeries()!=null) {
			model.addAttribute("series", domain.getSectionOrderSeries().getId());
		}
    }
	
	@Override
	protected void customValidateCreate(final SectionOrder domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        customValidate(domain, result, request);
    }
	
	@Override
	protected void customValidateUpdate(final SectionOrder domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        customValidate(domain, result, request);
    }
	
	private void customValidate(final SectionOrder domain, 
    		final BindingResult result,
            final HttpServletRequest request) {
		// Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
            return;
        }        
        //name compulsory
        if(domain.getName()==null || domain.getName().isEmpty()) {
        	result.rejectValue("name", "NotEmpty");
        	return;
        }
        //sequence number compulsory
        if(domain.getSequenceNumber()==null) {
        	result.rejectValue("sequenceNumber", "NotEmpty");
        	return;
        }
        //series compulsory
        if(domain.getSectionOrderSeries()==null) {
        	result.rejectValue("version", "NotEmpty.domain.sectionOrderSeries");
        	return;
        }
        // Check for duplicate instance
        List<SectionOrder> sectionOrdersInTheSameSeries = SectionOrder.findAllByFieldName(SectionOrder.class, "sectionOrderSeries", domain.getSectionOrderSeries(), "name", ApplicationConstants.ASC, domain.getLocale());
		if(sectionOrdersInTheSameSeries!=null && !sectionOrdersInTheSameSeries.isEmpty()) {
			for(SectionOrder so: sectionOrdersInTheSameSeries) {
				if(so.getName().equals(domain.getName())
						&& (domain.getId()==null || !so.getId().equals(domain.getId()))) {
					result.rejectValue("name", "NonUnique");
					return;
				} else if(so.getSequenceNumber().equals(domain.getSequenceNumber())
						&& (domain.getId()==null || !so.getId().equals(domain.getId()))) {
					result.rejectValue("sequenceNumber", "NonUnique");
					return;
				}
			}
		}
	}
	
}
