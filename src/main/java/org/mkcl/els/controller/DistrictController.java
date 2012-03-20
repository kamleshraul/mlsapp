/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DistrictController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class DistrictController.
 * 
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_districts")
public class DistrictController extends GenericController<District> {

	/** The Constant ASC. */
	private static final String ASC = "asc";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mkcl.els.controller.GenericController#populateNew(org.springframework
	 * .ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.util.Locale,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, final District domain,
			final Locale locale, final HttpServletRequest request) {
		String stateName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_STATE", locale.toString()))
				.getValue();
		populate(model, domain, locale, request, stateName);
		model.addAttribute("district", domain);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mkcl.els.controller.GenericController#populateEdit(org.springframework
	 * .ui.ModelMap, org.mkcl.els.domain.BaseDomain,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, final District domain,
			final HttpServletRequest request) {
		/*
		 * List<Division> divisions = Division.findAll(Division.class, "name",
		 * ASC, domain.getLocale()); model.addAttribute("divisions", divisions);
		 */
		Division division = domain.getDivision();
		String stateName = division.getState().getName();
		populate(model, domain, new Locale(domain.getLocale()), request,
				stateName);
		model.addAttribute("tehsil", domain);
	}

	/**
	 * Populate.
	 * 
	 * @param model
	 *            the model
	 * @param domain
	 *            the domain
	 * @param locale
	 *            the locale
	 * @param request
	 *            the request
	 * @param stateName
	 *            the state name
	 */
	private void populate(final ModelMap model, final District domain,
			final Locale locale, final HttpServletRequest request,
			final String stateName) {
		domain.setLocale(locale.toString());
		List<State> states = State.findAll(State.class, "name", "asc",
				locale.toString());
		State selectedState = State.findByName(State.class, stateName,
				locale.toString());
		List<State> newStates = new ArrayList<State>();
		newStates.add(selectedState);
		states.remove(selectedState);
		newStates.addAll(states);
		model.addAttribute("states", newStates);
		List<Division> divisions = Division.getDivisionRepository()
				.findDivisionsByStateId(selectedState.getId(), "name", "asc",
						locale.toString());
		model.addAttribute("divisions", divisions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl
	 * .els.domain.BaseDomain, org.springframework.validation.BindingResult,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final District domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}

	/**
	 * Custom validate.
	 * 
	 * @param domain
	 *            the domain
	 * @param result
	 *            the result
	 * @param request
	 *            the request
	 */
	private void customValidate(final District domain,
			final BindingResult result, final HttpServletRequest request) {
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}

		// Check for Nil Number of Constituencies in District
		if (domain.getTotalConstituencies() == 0) {
			result.rejectValue("totalConstituencies", "NumberNil");
		}

		// Check for duplicate instance if the instance has a field "name"
		try {
			String nameValue = domain.getName();
			boolean duplicateParameter = domain.isDuplicate("name", nameValue);
			Object[] params = new Object[1];
			params[0] = nameValue;
			if (duplicateParameter) {
				result.rejectValue("name", "NonUnique", params,
						"Duplicate Parameter");
			}
		} catch (SecurityException e) {
			logger.error(e.getMessage());
			return;
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			return;
		}
	}
}
