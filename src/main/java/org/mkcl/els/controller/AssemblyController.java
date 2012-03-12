/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.AssemblyController.java
 * Created On: Mar 7, 2012
 */

package org.mkcl.els.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.DateFormater;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyStructure;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class AssemblyController.
 * 
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/masters_assemblies")
public class AssemblyController extends GenericController<Assembly> {

	/** The Constant ASC. */
	private static final String ASC = "asc";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl
	 * .els.domain.BaseDomain, org.springframework.validation.BindingResult,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final Assembly domain,
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
	protected void customValidateUpdate(final Assembly domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}

	/**
	 * Custom validate.
	 * 
	 * @param assembly
	 *            the assembly
	 * @param result
	 *            the result
	 * @param request
	 *            the request
	 * @author nileshp
	 * @since v1.0.0 Custom validate.
	 */
	private void customValidate(final Assembly assembly,
			final BindingResult result, final HttpServletRequest request) {
		new HashMap<String, String>();

		Boolean isDuplicateParameter = assembly.isDuplicate("assembly",
				assembly.getAssembly());
		Object[] params = new Object[1];
		params[0] = assembly.getAssembly();

		if (isDuplicateParameter) {
			result.rejectValue("assembly", "NonUnique", params,
					"Duplicate Parameter");
		}

		if (assembly.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		DateFormater dateFormater = new DateFormater();
		String strAssemblyEndDate = assembly.getAssemblyEndDate();
		String strAssemblyStartDate = assembly.getAssemblyStartDate();
		System.out.println("dates: " + strAssemblyEndDate + " "
				+ strAssemblyEndDate);
		if ((strAssemblyEndDate != "") && (strAssemblyStartDate != "")) {
			Date assemblyStartDate = dateFormater.formatStringToDate(
					strAssemblyStartDate, "MM/dd/yyyy");
			Date assemblyEndDate = dateFormater.formatStringToDate(
					strAssemblyEndDate, "MM/dd/yyyy");
			/***
			 * condition : assembly start date should not be greater than
			 * assembly end date
			 *****/
			if (assemblyStartDate.after(assemblyEndDate)) {
				result.rejectValue("assemblyStartDate", "assemblyStartDate");
			}

			/***
			 * condition : assembly end date should not be less than todays date
			 * if Assembly is current assembly
			 *****/
			if (assembly.isCurrentAssembly() == true) {
				String strTodayDate = dateFormater.formatDateToString(
						new Date(), "MM/dd/yyyy");
				Date todayDate = dateFormater.formatStringToDate(strTodayDate,
						"MM/dd/yyyy");
				if (assemblyEndDate.before(todayDate)) {
					result.rejectValue("assemblyEndDate", "assemblyEndDate");
				} else {
					assembly.updatePreviousCurrentAssembly(assembly,
							assembly.getLocale());
				}
			}
		}

		if (result.hasErrors()) {
			System.out.println("error");
		}
	}

	/**
	 * Creates the.
	 * 
	 * @param model
	 *            the model
	 * @param assembly
	 *            the assembly
	 * @param locale
	 *            the locale
	 * @param request
	 *            the request
	 * @return the string
	 * @author nileshp
	 * @since v1.0.0
	 */
	@Override
	protected void populateNew(final ModelMap model, final Assembly assembly,
			final Locale locale, final HttpServletRequest request) {
		assembly.setLocale(locale.toString());
		populateModel(model, assembly);
	}

	/**
	 * Update.
	 * 
	 * @param model
	 *            the model
	 * @param assembly
	 *            the assembly
	 * @param request
	 *            the request
	 * @return the string
	 * @author nileshp
	 * @since v1.0.0
	 */
	@Override
	protected void populateEdit(final ModelMap model, final Assembly assembly,
			final HttpServletRequest request) {
		populateModel(model, assembly);
	}

	/**
	 * Populate model.
	 * 
	 * @param model
	 *            the model
	 * @param assembly
	 *            the assembly
	 * @author nileshp
	 * @since v1.0.0 Populate model.
	 */
	private void populateModel(final ModelMap model, final Assembly assembly) {
		model.addAttribute("assemblyStructures", AssemblyStructure.findAll(
				AssemblyStructure.class, "name", ASC, assembly.getLocale()));
		model.addAttribute("assembly", assembly);
	}
}
