/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DivisionController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class DivisionController.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_divisions")
public class DivisionController extends GenericController<Division> {

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
	protected void populateNew(final ModelMap model, final Division domain,
			final Locale locale, final HttpServletRequest request) {
		domain.setLocale(locale.toString());
		List<State> states = State.findAll(State.class, "name", ASC,
				locale.toString());
		model.addAttribute("states", states);
		String stateName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_STATE", locale.toString()))
				.getValue();
		State defaultState = State.findByName(State.class, stateName,
				locale.toString());
		domain.setState(defaultState);
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
	protected void populateEdit(final ModelMap model, final Division domain,
			final HttpServletRequest request) {
		List<State> states = State.findAll(State.class, "name", ASC,
				domain.getLocale());
		model.addAttribute("states", states);
	}
}
