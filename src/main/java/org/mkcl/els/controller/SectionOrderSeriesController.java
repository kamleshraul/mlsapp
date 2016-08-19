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
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.SectionOrder;
import org.mkcl.els.domain.SectionOrderSeries;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class SectionOrderSeriesController.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
@Controller
@RequestMapping("/sectionorderseries")
public class SectionOrderSeriesController extends GenericController<SectionOrderSeries> {
	
	@Override
	protected void populateModule(final ModelMap model,
            final HttpServletRequest request, final String locale,
            final AuthUser currentUser) {
		/** populate languages **/
		List<Language> languages = Language.findAll(Language.class, "priority", ApplicationConstants.ASC, locale);
		if(languages==null || languages.isEmpty()) {
			model.addAttribute("errorcode", "LANGUAGES_NOT_FOUND");
			return;
		}
		model.addAttribute("languages", languages);
		model.addAttribute("selectedLanguage", languages.get(0).getType());
    }	
	
	@Override
	protected void preValidateCreate(final SectionOrderSeries domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        preValidate(domain, result, request);
    }
	
	@Override
	protected void preValidateUpdate(final SectionOrderSeries domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        preValidate(domain, result, request);
    }
	
	private void preValidate(final SectionOrderSeries domain, 
    		final BindingResult result,
            final HttpServletRequest request) {
		/** set language **/
		String selectedLanguageType = request.getParameter("setLanguage");
		if(domain.getLanguage()==null || !domain.getLanguage().getType().equals(selectedLanguageType)) {
			Language selectedLanguage = Language.findByFieldName(Language.class, "type", selectedLanguageType, domain.getLocale());
			domain.setLanguage(selectedLanguage);
		}
	}
	
	@Override
	protected void customValidateCreate(final SectionOrderSeries domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        customValidate(domain, result, request);
    }
	
	@Override
	protected void customValidateUpdate(final SectionOrderSeries domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        customValidate(domain, result, request);
    }
	
	private void customValidate(final SectionOrderSeries domain, 
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
        // Check for duplicate instance
        if(domain.checkDuplicate()) {
        	result.rejectValue("name", "NonUnique");
			return;
        }
	}
	
	@Override
	protected void populateIfNoErrors(final ModelMap model, 
    		final SectionOrderSeries domain,
            final HttpServletRequest request) {				
		/** set is autonomous **/
		if(domain.getIsAutonomous()==null) {
			domain.setIsAutonomous(false);
		}
    }
	
}
