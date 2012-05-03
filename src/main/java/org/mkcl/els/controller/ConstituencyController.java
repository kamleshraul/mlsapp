/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ConstituencyController.java
 * Created On: 17 Apr, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Airport;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.RailwayStation;
import org.mkcl.els.domain.Reservation;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstituencyController.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
@Controller
@RequestMapping("/constituency")
public class ConstituencyController extends GenericController<Constituency> {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.mkcl.els.controller.GenericController#populateNew(org.springframework
	 * .ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.util.Locale,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, final Constituency domain,
			final String locale, final HttpServletRequest request) {
		String stateName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_STATE", locale)).getValue();
		String houseName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_HOUSETYPE", locale)).getValue();
		domain.setLocale(locale);
		domain.setHouseType((HouseType) HouseType.findByFieldName(
				HouseType.class, "name", houseName, locale));
		// This is not needed as there will be a dropdown to select the house
		// type
		// String htype = this.getCurrentUser().getHouseType();
		// HouseType houseType = HouseType.findByFieldName(HouseType.class,
		// "type", htype, locale);
		model.addAttribute("houseTypes", HouseType.findAll(HouseType.class, "type", "asc", domain.getLocale()));
		List<State> states = State.findAll(State.class, "name", "asc", locale);
		List<State> newStates = new ArrayList<State>();
		State selectedState = State.findByName(State.class, stateName, locale);
		newStates.add(selectedState);
		states.remove(selectedState);
		newStates.addAll(states);
		model.addAttribute("states", newStates);

		List<Division> divisions = Division.findAllByFieldName(Division.class,
				"state", selectedState, "name", "asc", locale);
		model.addAttribute("divisions", divisions);

		List<District> districts = District.findAllByFieldName(District.class,
				"division", divisions.get(0), "name", "asc", locale);
		model.addAttribute("districts", districts);

		List<Reservation> reservations = Reservation.findAll(Reservation.class,
				"name", "asc", locale);
		model.addAttribute("reservations", reservations);

		List<RailwayStation> railwayStations = new ArrayList<RailwayStation>();
		model.addAttribute("railwayStations", railwayStations);

		List<Airport> airports = new ArrayList<Airport>();
		model.addAttribute("airports", airports);
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
	protected void populateEdit(final ModelMap model,
			final Constituency domain, final HttpServletRequest request) {
		String hType = domain.getHouseType().getType();
		HouseType houseType = (HouseType) HouseType.findByFieldName(
				HouseType.class, "type", hType, domain.getLocale());
		model.addAttribute("houseType", houseType.getId());
		model.addAttribute("housetype", houseType.getType());
		String stateName = "";
		String divisionName = "";
		if (domain.getHouseType().getType().equals("lowerhouse")) {
			if (!domain.getDistricts().isEmpty()) {
				District district = domain.getDistricts().get(0);
				stateName = district.getDivision().getState().getName();
				divisionName = district.getDivision().getName();
			}

			List<State> states = State.findAll(State.class, "name", "asc",
					domain.getLocale().toString());
			List<State> newStates = new ArrayList<State>();
			State selectedState = State.findByName(State.class, stateName,
					domain.getLocale().toString());
			newStates.add(selectedState);
			states.remove(selectedState);
			newStates.addAll(states);
			model.addAttribute("states", newStates);

			List<Division> divisions = Division.findAllByFieldName(
					Division.class, "state", selectedState, "name", "asc",
					domain.getLocale().toString());
			List<Division> newDivisions = new ArrayList<Division>();
			Division selectedDivision = Division.findByName(Division.class,
					divisionName, domain.getLocale().toString());
			newDivisions.add(selectedDivision);
			divisions.remove(selectedDivision);
			newDivisions.addAll(divisions);
			model.addAttribute("divisions", newDivisions);

			List<District> districts = District.findAllByFieldName(
					District.class, "division", selectedDivision, "name",
					"asc", domain.getLocale().toString());
			model.addAttribute("districts", districts);

			List<Reservation> reservations = Reservation.findAll(
					Reservation.class, "name", "asc", domain.getLocale()
							.toString());
			model.addAttribute("reservations", reservations);

			List<RailwayStation> railwayStations = new ArrayList<RailwayStation>();
			for (District selectedDistrict : domain.getDistricts()) {
				List<RailwayStation> rs = RailwayStation.findAllByFieldName(
						RailwayStation.class, "district", selectedDistrict,
						"name", "asc", domain.getLocale().toString());
				railwayStations.addAll(rs);
			}
			model.addAttribute("railwayStations", railwayStations);

			List<Airport> airports = new ArrayList<Airport>();
			for (District selectedDistrict : domain.getDistricts()) {
				List<Airport> rs = Airport.findAllByFieldName(Airport.class,
						"district", selectedDistrict, "name", "asc", domain
								.getLocale().toString());
				airports.addAll(rs);
			}
			model.addAttribute("airports", airports);
		}
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void preValidateCreate(final Constituency domain,
			final BindingResult result, final HttpServletRequest request) {
		HouseType houseType = HouseType.findByFieldName(HouseType.class,
				"type", request.getParameter("housetype"), domain.getLocale());
		domain.setHouseType(houseType);
		if(domain.getHouseType().getType().equals("lowerhouse")) {
		if (domain.getIsReserved() == false) {
			domain.setReservedFor(null);
		}
		}
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void preValidateUpdate(final Constituency domain,
			final BindingResult result, final HttpServletRequest request) {
		if(domain.getHouseType().getType().equals("lowerhouse")) {
		if (domain.getIsReserved() == false) {
			domain.setReservedFor(null);
		}
		}
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
	protected void customValidateCreate(final Constituency domain,
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
	protected void customValidateUpdate(final Constituency domain,
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
	 * @author nileshp
	 * @since v1.0.0 Custom validate.
	 */
	private void customValidate(final Constituency domain,
			final BindingResult result, final HttpServletRequest request) {
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}

		// Check for duplicate instance if the instance has a field "name"

		boolean duplicateParameter = domain.isDuplicate("name",
				domain.getName());
		Object[] params = new Object[1];
		params[0] = domain.getName();
		if (duplicateParameter) {
			result.rejectValue("name", "NonUnique", params,
					"Duplicate Parameter");
		}

	}

	@Override
	protected void populateCreateIfErrors(final ModelMap model,
			final Constituency domain,
			final HttpServletRequest request) {
		domain.setHouseType((HouseType) HouseType.findByFieldName(HouseType.class, "type", request.getParameter("housetype"), domain.getLocale()));
		List<HouseType> houseTypes =HouseType.findAll(HouseType.class, "type", "asc", domain.getLocale());
		List<HouseType> newHouseTypes = new ArrayList<HouseType>();
		HouseType selectedHouseType = (HouseType) HouseType.findByFieldName(HouseType.class, "type", request.getParameter("housetype"), domain.getLocale());
		newHouseTypes.add(selectedHouseType);
		houseTypes.remove(selectedHouseType);
		newHouseTypes.addAll(houseTypes);
		model.addAttribute("houseTypes", newHouseTypes);		
		populateEdit(model, domain, request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
}