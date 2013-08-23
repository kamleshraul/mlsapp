/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ElectionController.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.House;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class ElectionController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/election")
public class ElectionController extends GenericController<Election> {

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, 
			final Election domain, 
			final String locale,
			final HttpServletRequest request) {
		domain.setLocale(locale);
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateCreateIfErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateCreateIfErrors(final ModelMap model, 
			final Election domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, 
			final Election domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}

	/**
	 * Populate.
	 *
	 * @param model the model
	 * @param locale the locale
	 * @param houseType the house type
	 */
	private void populate(final ModelMap model, 
			final String locale, 
			final String houseType){
		try{
			List<ElectionType> electionTypes = ElectionType.findByHouseType(houseType, locale);
			model.addAttribute("electionTypes", electionTypes);
			List<House> houses=House.findByHouseType(houseType, locale);
			model.addAttribute("houses",houses);
		}catch (ELSException e) {
			logger.error(e.getMessage());
		}
	}
}
