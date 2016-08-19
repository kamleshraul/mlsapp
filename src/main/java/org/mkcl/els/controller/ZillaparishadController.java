/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ZillaparishadController.java
 * Created On: june 13, 2016
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Zillaparishad;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class ZillaparishadController.
 *
 * @author rajeshs
 * @since v1.0.0
 */
@Controller
@RequestMapping("/zillaparishad")
public class ZillaparishadController extends GenericController<Zillaparishad> {

	/**
	 * Populate model.
	 *
	 * @param model
	 *            the model
	 * @param domain
	 *            the domain
	 * @param locale
	 *            the locale
	 * @param request
	 *            the request
	 * @author nileshp
	 * @since v1.0.0 Populate new.
	 */
	@Override
	protected void populateNew(final ModelMap model, final Zillaparishad domain,
			final String locale, final HttpServletRequest request) {
		String stateName = ((CustomParameter) CustomParameter.findByName(CustomParameter.class, "DEFAULT_STATE", locale.toString())).getValue();
		populate(model, domain, locale, request, stateName);
		model.addAttribute("zillaparishad", domain);
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
	protected void populateEdit(final ModelMap model, final Zillaparishad domain,
			final HttpServletRequest request) {
		District district = domain.getDistrict();
		String stateName;
		if (district != null) {
			stateName = district.getDivision().getState().getName();
		} else {
			stateName = ((CustomParameter) CustomParameter.findByName(CustomParameter.class, "DEFAULT_STATE", domain.getLocale().toString())).getValue();
		}
		populate(model, domain, domain.getLocale(), request, stateName);
		model.addAttribute("zillaparishad", domain);
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
	 * @author nileshp
	 * @since v1.0.0 Populate.
	 */
	private void populate(final ModelMap model, final Zillaparishad domain,
			final String locale, final HttpServletRequest request,
			final String stateName) {
		domain.setLocale(locale.toString());
		List<State> states = State.findAll(State.class, "name", "asc", locale.toString());
		State selectedState = State.findByName(State.class, stateName, locale.toString());
		List<State> newStates = new ArrayList<State>();
		newStates.add(selectedState);
		states.remove(selectedState);
		newStates.addAll(states);
		model.addAttribute("states", newStates);
		List<District> districts = new ArrayList<District>();
		try{
			districts = District.getDistrictRepository().findDistrictsByStateId(selectedState.getId(), "name", "asc",locale.toString());
		}catch (ELSException e) {
			model.addAttribute("ZillaparishadController", e.getParameter());
			
		}
		if (districts != null) {
			model.addAttribute("districts", districts);
		}
	}

	/**
	 * Custom validate create.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	@Override
	protected void customValidateCreate(final Zillaparishad domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidateZillaparishad(domain, result, request);
	}

	/**
	 * Custom validate update.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	@Override
	protected void customValidateUpdate(final Zillaparishad domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidateZillaparishad(domain, result, request);
	}

	/**
	 * Custom validate.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void customValidateZillaparishad(final Zillaparishad domain, final BindingResult result,
			final HttpServletRequest request) {
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}

}
