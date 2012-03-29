/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.AirportController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Airport;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class AirportController.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_airports")
public class AirportController extends GenericController<Airport> {

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
	protected void populateNew(final ModelMap model, final Airport domain,
			final Locale locale, final HttpServletRequest request) {
		String stateName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_STATE", locale.toString()))
				.getValue();
		populate(model, domain, locale, request, stateName);
		model.addAttribute("airport", domain);
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
	protected void populateEdit(final ModelMap model, final Airport domain,
			final HttpServletRequest request) {
		District district = domain.getDistrict();
		String stateName;
		if (district != null) {
			stateName = district.getDivision().getState().getName();
		} else {
			stateName = ((CustomParameter) CustomParameter.findByName(
					CustomParameter.class, "DEFAULT_STATE", domain.getLocale()
							.toString())).getValue();
		}
		populate(model, domain, new Locale(domain.getLocale()), request,
				stateName);
		model.addAttribute("airport", domain);
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
	private void populate(final ModelMap model, final Airport domain,
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
		List<District> districts = District.getDistrictRepository()
				.findDistrictsByStateId(selectedState.getId(), "name", "asc",
						locale.toString());
		if (districts != null) {
			model.addAttribute("districts", districts);
		}
	}

}