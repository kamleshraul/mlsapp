/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.RailwayStationController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.RailwayStation;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class RailwayStationController.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Controller
@RequestMapping("/railwaystation")
public class RailwayStationController extends GenericController<RailwayStation> {


    /*
     * (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap,
     * org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model,
            final RailwayStation domain,
            final String locale,
            final HttpServletRequest request) {
        String stateName = ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DEFAULT_STATE", locale.toString()))
                .getValue();
        State defState = State.findByName(State.class, stateName, locale);
        List<State> states = State.findAll(State.class, "name", ApplicationConstants.ASC, locale);
        List<District> districts = new ArrayList<District>();
        try{
        	districts = District.findDistrictsByStateId(defState.getId(), "name",ApplicationConstants.ASC, locale);
        }catch (ELSException e) {
        	model.addAttribute("RailwayStationController", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, rquest may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
        //following two lines added to make maharashtra default state
        states.remove(defState);
        states.add(0, defState);
        domain.setDistrict(districts.get(0));
        domain.setLocale(locale);
        model.addAttribute("states", states);
        model.addAttribute("districts", districts);
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
            final RailwayStation domain, 
            final HttpServletRequest request) {
        State state = domain.getDistrict().getDivision().getState();
        List<State> states = State.findAll(State.class, "name", ApplicationConstants.ASC,
                domain.getLocale());
        List<District> districts = new ArrayList<District>();
        try{
        	districts = District.findDistrictsByStateId(state.getId(), "name",ApplicationConstants.ASC, domain.getLocale());
        }catch (ELSException e) {
        	model.addAttribute("RailwayStationController", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, rquest may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
        //following two lines added to make maharashtra default state
        states.remove(state);
        states.add(0, state);
        model.addAttribute("states", states);
        model.addAttribute("districts", districts);
    }

}
