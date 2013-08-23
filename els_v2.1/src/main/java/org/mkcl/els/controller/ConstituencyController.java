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

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Airport;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.RailwayStation;
import org.mkcl.els.domain.Reservation;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.UpperHouseConstituencyType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

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
    protected void populateNew(final ModelMap model, 
    		final Constituency domain,
            final String locale, 
            final HttpServletRequest request) {

        //*****************House and HouseType***************************
        //first we will read default house type from custom parameter and this will be the
        //selected entry in housetypes
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "type", "asc", locale);
        model.addAttribute("houseTypes", houseTypes);
        if(!houseTypes.isEmpty()){
            model.addAttribute("houseTypeType",houseTypes.get(0).getType());
        }
        //**********************State***************************************
        //populating state.Initially default state is read from custom parameter and this will be the
        //state whose division and districts will be populated initially on clicking new
        String stateName=null;
        State defaultState=null;
        CustomParameter customParameter=((CustomParameter) CustomParameter.findByName(CustomParameter.class, "DEFAULT_STATE", locale));
        if(customParameter!=null){
            stateName=customParameter.getValue();
            defaultState = State.findByName(State.class, stateName, locale);
            model.addAttribute("defaultState",defaultState.getId());
        }
        //populating list of all states irrespective of defaultstate is set or not
        List<State> states = State.findAll(State.class, "name", "asc", locale);
        if(defaultState==null){
            if(!states.isEmpty()){
                model.addAttribute("defaultState",states.get(0).getId());
            }
        }
        model.addAttribute("states",states);

        //****************************Division********************************
        //populating division.if defaultstate is not null then fetch divisions of the default state
        //else fetch division of the first state in states
        Division defaultDivision=null;
        List<Division> divisions=new ArrayList<Division>();
        if(defaultState!=null){
        	divisions = Division.findAllByFieldName(Division.class,"state", defaultState, "name", "asc", locale);
        }else{
            if(!states.isEmpty()){
                divisions = Division.findAllByFieldName(Division.class,
                        "state", states.get(0), "name", "asc", locale);
            }
        }
        model.addAttribute("divisions",divisions);
        //**************************Districts******************************
        //if divisions is not empty then districts will be fetched
        List<District> districts=new ArrayList<District>();
        if(!divisions.isEmpty()){
            defaultDivision=divisions.get(0);
            districts = District.findAllByFieldName(District.class,
                    "division", defaultDivision, "name", "asc", locale);
            model.addAttribute("selectedDivision",defaultDivision.getId());
        }
        model.addAttribute("districts", districts);

        //****************Locale************************************
        domain.setLocale(locale);
        //****************Reservations***************************
        List<Reservation> reservations = Reservation.findAll(Reservation.class,
                "name", "asc", locale);
        model.addAttribute("reservations", reservations);
        //**********Constituency Type In case of Upper House***************************
        //for populating constituency names types.we set a custom parameter "NO_OF_UH_CONSTITUENCY_TYPE"
        //this will indicate the number of constituencies categories in case of upper house.Then set the
        //same number of entry in message resources with the pattern upperhouse.constituencytype.no
        CustomParameter customParameter2=CustomParameter.findByName(CustomParameter.class, "NO_OF_UH_CONSTITUENCY_TYPE", "");
        if(customParameter2!=null){
        model.addAttribute("noOfConstituencyTypes", customParameter2.getValue());
        }else{
            model.addAttribute("noOfConstituencyTypes", 0);
        }
        model.addAttribute("upperHouseConstituencyTypes", UpperHouseConstituencyType.findAll(UpperHouseConstituencyType.class,"name",ApplicationConstants.ASC, locale));
        //***********************Retired****************************
        domain.setIsRetired(false);
        //***********************Reserved**************************
        domain.setIsReserved(false);
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
            final Constituency domain, 
            final HttpServletRequest request) {
        //*****************House and HouseType***************************
        //first we will read default house type from custom parameter and this will be the
        //selected entry in housetypes
        String houseName=null;
        HouseType houseType = domain.getHouseType();
        if(houseType!=null){
            model.addAttribute("houseTypeType",houseType.getType());
            houseName=houseType.getType();
        }
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "type", "asc", domain.getLocale());
        model.addAttribute("houseTypes", houseTypes);
        //**********************State,Division and District**************************
        State selectedState = null;
        Division selectedDivision = null;
        List<State> states =new ArrayList<State>();
        List<District> districts=new ArrayList<District>();
        List<Division> divisions=new ArrayList<Division>();
        //populating divisions
        //There will always be some district present in the constituency.If not then selected state will be default
        //state.else selected state can be fetched from the selected district.
        if(domain.getDistricts()!=null){
        if(!domain.getDistricts().isEmpty()){
            selectedDivision=domain.getDistricts().get(0).getDivision();
            if(selectedDivision!=null){
                if(selectedDivision.getState()!=null){
                    selectedState=selectedDivision.getState();
                }
            }
        }
        }
        if(domain.getDivisionName()!=null){
            if(!domain.getDivisionName().isEmpty()){
                selectedDivision=Division.findByFieldName(Division.class,"name",domain.getDivisionName(), domain.getLocale());
            }
        }
        if(selectedState==null){
            String stateName = ((CustomParameter) CustomParameter.findByName(
                    CustomParameter.class, "DEFAULT_STATE", domain.getLocale())).getValue();
            selectedState = State.findByName(State.class, stateName, domain.getLocale());
        }
        states = State.findAll(State.class, "name", "asc",domain.getLocale().toString());
        model.addAttribute("states", states);
        divisions = Division.findAllByFieldName(
                Division.class, "state", selectedState, "name", "asc",
                domain.getLocale().toString());
        model.addAttribute("divisions", divisions);
        //populating districts.
        //There will be some selectedDivision if districts has been populated.Else take first value of divisions and
        //populate districts.
        if(selectedDivision==null){
            if(!divisions.isEmpty()){
                selectedDivision=divisions.get(0);
            }
        }
        if(selectedDivision!=null){
        districts = District.findAllByFieldName(District.class,"division", selectedDivision, "name", "asc", domain.getLocale());
        model.addAttribute("districts",districts);
        }
        model.addAttribute("selectedDivision",selectedDivision.getId());

        //in case of lower house populating reservation select box,railway station select box
        //and airport select box
        if(houseName.equals(ApplicationConstants.LOWER_HOUSE)){
            //populating reservations fields
            List<Reservation> reservations = Reservation.findAll(
                    Reservation.class, "name", "asc", domain.getLocale().toString());
            model.addAttribute("reservations", reservations);
            //populating railway stations
            List<RailwayStation> railwayStations = new ArrayList<RailwayStation>();
            for (District selectedDistrict : domain.getDistricts()) {
                List<RailwayStation> rs = RailwayStation.findAllByFieldName(RailwayStation.class, "district", 
                		selectedDistrict,"name", "asc", domain.getLocale().toString());
                railwayStations.addAll(rs);
            }
            model.addAttribute("railwayStations", railwayStations);
            //populating airports
            List<Airport> airports = new ArrayList<Airport>();
            for (District selectedDistrict : domain.getDistricts()) {
                List<Airport> rs = Airport.findAllByFieldName(Airport.class,
                        "district", selectedDistrict, "name", "asc", domain.getLocale().toString());
                airports.addAll(rs);
            }
            model.addAttribute("airports", airports);
        }else if(houseName.equals(ApplicationConstants.UPPER_HOUSE)){
            //populating name select box with constituency type in case of upperhouse
            //for populating constituency names types.we set a custom parameter "NO_OF_UH_CONSTITUENCY_TYPE"
            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "NO_OF_UH_CONSTITUENCY_TYPE", "");
            model.addAttribute("noOfConstituencyTypes", customParameter.getValue());
            model.addAttribute("upperHouseConstituencyTypes", UpperHouseConstituencyType.findAll(UpperHouseConstituencyType.class,"name",ApplicationConstants.ASC, domain.getLocale()));

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
            final BindingResult result, 
            final HttpServletRequest request) {
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
            final BindingResult result, 
            final HttpServletRequest request) {
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
            final BindingResult result, 
            final HttpServletRequest request) {
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
        if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
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

    }

    @Override
    protected void populateCreateIfErrors(final ModelMap model,
            final Constituency domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "create_failed");
    }

    @Override
    protected void populateUpdateIfErrors(final ModelMap model,
            final Constituency domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "create_failed");
    }
}