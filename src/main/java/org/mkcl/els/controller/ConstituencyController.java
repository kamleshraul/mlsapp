/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ConstituencyController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Airport;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.RailwayStation;
import org.mkcl.els.domain.Reservation;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstituencyController.
 * 
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_constituencies")
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
			final Locale locale, final HttpServletRequest request) {
		String stateName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_STATE", locale.toString()))
				.getValue();
		domain.setLocale(locale.toString());

		String htype = this.getCurrentUser().getHouseType();
		HouseType houseType = HouseType.findByFieldName(HouseType.class,
				"type", htype, locale.toString());
		model.addAttribute("houseType", houseType.getId());

		List<State> states = State.findAll(State.class, "name", "asc",
				locale.toString());
		List<State> newStates = new ArrayList<State>();
		State selectedState = State.findByName(State.class, stateName,
				locale.toString());
		newStates.add(selectedState);
		states.remove(selectedState);
		newStates.addAll(states);
		model.addAttribute("states", newStates);

		List<Division> divisions = Division.findAllByFieldName(Division.class,
				"state", selectedState, "name", "asc", locale.toString());
		model.addAttribute("divisions", divisions);

		List<District> districts = District.findAllByFieldName(District.class,
				"division", divisions.get(0), "name", "asc", locale.toString());
		model.addAttribute("districts", districts);

		/*
		 * if (houseType.getId() == 2 || houseType.getId() == 4) { String name =
		 * divisions.get(0).getName(); model.addAttribute("name", name); }
		 */

		// please confirm below commented code before deleting
		/*
		 * List<ElectionType> electionTypes = ElectionType.findAllByFieldName(
		 * ElectionType.class, "houseType", houseType, "name", "asc",
		 * locale.toString()); model.addAttribute("electionTypes",
		 * electionTypes);
		 */

		List<Reservation> reservations = Reservation.findAll(Reservation.class,
				"name", "asc", locale.toString());
		List<Reservation> newReservations = new ArrayList<Reservation>();
		String reservedName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_RESERVATION", locale.toString()))
				.getValue();
		Reservation selectedReservation = Reservation.findByName(Reservation.class, reservedName,
				locale.toString());
		newReservations.add(selectedReservation);
		reservations.remove(selectedReservation);
		newReservations.addAll(reservations);
		model.addAttribute("reservations", newReservations);

		List<RailwayStation> railwayStations = new LinkedList<RailwayStation>();
		model.addAttribute("railwayStations", railwayStations);

		List<Airport> airports = new LinkedList<Airport>();
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

		model.addAttribute("houseType", domain.getHouseType().getId());

		if ((domain.getHouseType().getId() == 1)
				|| (domain.getHouseType().getId() == 3)) {
			if (domain.getDistricts() != null) {
				String stateName = "";
				String divisionName = "";
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
				List<District> newDistricts = new ArrayList<District>();
				for (District selectedDistrict : domain.getDistricts()) {
					newDistricts.add(selectedDistrict);
					districts.remove(selectedDistrict);
				}
				newDistricts.addAll(districts);
				model.addAttribute("districts", newDistricts);

				List<Reservation> reservations = Reservation.findAll(
						Reservation.class, "name", "asc", domain.getLocale()
								.toString());
				if (domain.getIsReserved()) {
					List<Reservation> newReservations = new ArrayList<Reservation>();
					Reservation selectedReservation = domain.getReservedFor();
					newReservations.add(selectedReservation);
					reservations.remove(selectedReservation);
					newReservations.addAll(reservations);
					model.addAttribute("reservations", newReservations);
				} else {
					model.addAttribute("reservations", reservations);
				}

				List<RailwayStation> railwayStations = new ArrayList<RailwayStation>();
				for (District selectedDistrict : domain.getDistricts()) {
					List<RailwayStation> rs = RailwayStation
							.findAllByFieldName(RailwayStation.class,
									"district", selectedDistrict, "name",
									"asc", domain.getLocale().toString());
					railwayStations.addAll(rs);
				}				
				List<RailwayStation> newRailwayStations = new ArrayList<RailwayStation>();
				if(domain.getNearestRailwayStation()!=null) {
					RailwayStation selectedRailwayStation = domain
							.getNearestRailwayStation();
					newRailwayStations.add(selectedRailwayStation);
					railwayStations.remove(selectedRailwayStation);					
				}
				newRailwayStations.addAll(railwayStations);
				model.addAttribute("railwayStations", newRailwayStations);

				List<Airport> Airports = new ArrayList<Airport>();
				for (District selectedDistrict : domain.getDistricts()) {
					List<Airport> rs = Airport.findAllByFieldName(
							Airport.class, "district", selectedDistrict,
							"name", "asc", domain.getLocale().toString());
					Airports.addAll(rs);
				}
				List<Airport> newAirports = new ArrayList<Airport>();
				if(domain.getNearestAirport()!=null) {
					Airport selectedAirport = domain.getNearestAirport();
					newAirports.add(selectedAirport);
					Airports.remove(selectedAirport);
				}				
				newAirports.addAll(Airports);
				model.addAttribute("airports", newAirports);
				model.addAttribute("isReserved",domain.getIsReserved());
			}
		}		
	}	

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final Constituency domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
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
		
		if( (domain.getHouseType().getId()==1) || (domain.getHouseType().getId()==3) ) {
			if(domain.getNearestRailwayStation()==null) {
				result.rejectValue("nearestRailwayStation", "NotNull");
			}
			if(domain.getNearestAirport()==null) {
				result.rejectValue("nearestAirport", "NotNull");
			}
		}

		// Check for duplicate instance if the instance has a field "name"
		try {
			String nameValue = null;
			try {
				nameValue = (String) domain.getClass()
						.getMethod("getName", null).invoke(domain, null);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
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
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			return;
		}
	}
}
