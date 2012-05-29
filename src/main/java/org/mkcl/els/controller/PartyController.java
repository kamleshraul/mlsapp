/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.PartyController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.DateFormater;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.PartySymbol;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class PartyController.
 *
 * @author dhananjay
 * @since v1.0.0
 */
@Controller
@RequestMapping("/party")
public class PartyController extends GenericController<Party> {

	/**
	 * New form.
	 *
	 * @param model
	 *            the model
	 * @param party
	 *            the party
	 * @param locale
	 *            the locale
	 * @param request
	 *            the request
	 * @author meenalw
	 * @since v1.0.0
	 */
	@Override
	protected void populateNew(final ModelMap model, final Party party,
			final String locale, final HttpServletRequest request) {

		party.setLocale(locale.toString());
		model.addAttribute(party);
		String stateName = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_STATE", locale.toString()))
				.getValue();
		List<State> states = State.findAll(State.class, "name", "asc",
				locale.toString());
		List<State> newStates = new ArrayList<State>();
		State selectedState = State.findByName(State.class, stateName,
				locale.toString());
		newStates.add(selectedState);
		states.remove(selectedState);
		newStates.addAll(states);
		model.addAttribute("states", newStates);
		List<District> districts = District.findDistrictsByStateId(selectedState.getId(), "name", "asc", locale.toString());
		model.addAttribute("districts", districts);
		List<Tehsil> tehsils = Tehsil.findAllByFieldName(Tehsil.class, "district", districts.get(0), "name", "asc",
				locale.toString());
		model.addAttribute("tehsils", tehsils);
		model.addAttribute("symbolCount",0);
		CustomParameter customParameter = CustomParameter.findByName(
				CustomParameter.class, "PARTY_FLAG_EXTENSION", null);
		model.addAttribute("photoExt", customParameter.getValue());
		CustomParameter customParameter1 = CustomParameter.findByName(
				CustomParameter.class, "PARTY_FLAG_SIZE", null);
		model.addAttribute("photoSize",
				Long.parseLong(customParameter1.getValue()) * 1024 * 1024);
	}

	/**
	 * Edits the.
	 *
	 * @param model
	 *            the model
	 * @param party
	 *            the party
	 * @param request
	 *            the request
	 * @author meenalw
	 * @since v1.0.0
	 */
	@Override
	protected void populateEdit(final ModelMap model, final Party party,
			final HttpServletRequest request) {
		model.addAttribute(party);

		if(party.getRegisteredOfficeAddress()!=null) {
			String rStateName = "";
	        String rDistrictName = "";

			rStateName = party.getRegisteredOfficeAddress().getState().getName();
			rDistrictName = party.getRegisteredOfficeAddress().getDistrict().getName();

			List<State> states = State.findAll(State.class, "name", "asc",
                    party.getLocale());
            State selectedState = State.findByName(State.class, rStateName,
                    party.getLocale());
            model.addAttribute("statesR", states);

            List<District> districts = District.findDistrictsByStateId(selectedState.getId(), "name", "asc", party.getLocale());
            District selectedDistrict = District.findByName(District.class, rDistrictName,
                    party.getLocale());
            model.addAttribute("districtsR", districts);

            List<Tehsil> tehsils = Tehsil.findAllByFieldName(Tehsil.class, "district", selectedDistrict, "name", "asc",
    				party.getLocale());
            model.addAttribute("tehsilsR", tehsils);
		}

		if(party.getStateOfficeAddress()!=null) {
			String sStateName = "";
	        String sDistrictName = "";

			sStateName = party.getStateOfficeAddress().getState().getName();
			sDistrictName = party.getStateOfficeAddress().getDistrict().getName();

			List<State> states = State.findAll(State.class, "name", "asc",
                    party.getLocale());
            State selectedState = State.findByName(State.class, sStateName,
                    party.getLocale());
            model.addAttribute("statesS", states);

            List<District> districts = District.findDistrictsByStateId(selectedState.getId(), "name", "asc", party.getLocale());
            District selectedDistrict = District.findByName(District.class, sDistrictName,
                    party.getLocale());
            model.addAttribute("districtsS", districts);

            List<Tehsil> tehsils = Tehsil.findAllByFieldName(Tehsil.class, "district", selectedDistrict, "name", "asc",
    				party.getLocale());
            model.addAttribute("tehsilsS", tehsils);
		}

		int symbolCount = party.getPartySymbols().size();
		model.addAttribute("symbolCount", symbolCount);

		//here this is a temporary fix as the custom editor for date is not getting applied in case of PartyController
		SimpleDateFormat formatter=FormaterUtil.getDateFormatter(party.getLocale());
		if(party.getEstablishmentDate()!=null){
		String formattedEstablishmentDate=formatter.format(party.getEstablishmentDate());
		model.addAttribute("formattedEstablishmentDate", formattedEstablishmentDate);
		}
		if(!party.getPartySymbols().isEmpty()) {
			List<String> changeDates = new ArrayList<String>();
			for(PartySymbol partySymbol : party.getPartySymbols()) {
				String formattedChangeDate=formatter.format(partySymbol.getChangeDate());
				changeDates.add(formattedChangeDate);
			}
			model.addAttribute("changeDates", changeDates);
		}
	}

	@Override
    protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
        binder.registerCustomEditor(Tehsil.class, new BaseEditor(new Tehsil()));
        binder.registerCustomEditor(District.class, new BaseEditor(
                new District()));
        binder.registerCustomEditor(State.class, new BaseEditor(new State()));
    }

	/**
	 * Pre validate create.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	@Override
	protected void preValidateCreate(final Party domain,
			final BindingResult result, final HttpServletRequest request) {

		State stateR = State.findById(State.class, domain.getRegisteredOfficeAddress().getState().getId());
        District districtR = District.findById(District.class, domain.getRegisteredOfficeAddress().getDistrict().getId());
        Tehsil tehsilR = Tehsil.findById(Tehsil.class, domain.getRegisteredOfficeAddress().getTehsil().getId());
        domain.getRegisteredOfficeAddress().setState(stateR);
        domain.getRegisteredOfficeAddress().setDistrict(districtR);
        domain.getRegisteredOfficeAddress().setTehsil(tehsilR);
        domain.getRegisteredOfficeAddress().setLocale(domain.getLocale());
        domain.getRegisteredOfficeAddress().persist();

        State stateS = State.findById(State.class, domain.getStateOfficeAddress().getState().getId());
        District districtS = District.findById(District.class, domain.getStateOfficeAddress().getDistrict().getId());
        Tehsil tehsilS = Tehsil.findById(Tehsil.class, domain.getStateOfficeAddress().getTehsil().getId());
        domain.getStateOfficeAddress().setState(stateS);
        domain.getStateOfficeAddress().setDistrict(districtS);
        domain.getStateOfficeAddress().setTehsil(tehsilS);
        domain.getStateOfficeAddress().setLocale(domain.getLocale());
        domain.getStateOfficeAddress().persist();

        domain.getContact().setLocale(domain.getLocale());
        domain.getContact().persist();

        int symbolCount = Integer.parseInt(request.getParameter("symbolCount"));
        List<PartySymbol> partySymbols = new ArrayList<PartySymbol>();
        domain.setPartySymbols(partySymbols);
        for(int i=1; i<=symbolCount; i++) {
        	PartySymbol ps = new PartySymbol();
            ps.setSymbol(request.getParameter("symbol"+i));
            DateFormater toDate = new DateFormater();
            ps.setChangeDate(toDate.formatStringToDate((request.getParameter("changeDate"+i)), "dd/MM/yyyy"));
            ps.setLocale(domain.getLocale());
            partySymbols.add(ps);
            //ps.persist();
        }
	}

	@Override
	protected void preValidateUpdate(final Party domain,
			final BindingResult result, final HttpServletRequest request) {

        int symbolCount = Integer.parseInt(request.getParameter("symbolCount"));

        List<PartySymbol> partySymbols = new ArrayList<PartySymbol>();

        for(int i=1; i<=symbolCount; i++) {
        	if(request.getParameter("symbol"+i)!=null) {
        		PartySymbol ps = new PartySymbol();

        		ps.setSymbol(request.getParameter("symbol"+i));

        		if(!request.getParameter("changeDate"+i).isEmpty()) {
        			DateFormater toDate = new DateFormater();
        			ps.setChangeDate(toDate.formatStringToDate((request.getParameter("changeDate"+i)), "dd/MM/yyyy"));
        		}

        		String id=request.getParameter("partySymbolId"+ i);
            	if(id!=null){
            		if(!id.isEmpty()){
            			ps.setId(Long.parseLong(id));
            		}
            	}

            	String version=request.getParameter("partySymbolVersion"+ i);
            	if(version!=null){
            		if(!version.isEmpty()){
            			ps.setVersion(Long.parseLong(version));
            		}
            	}

            	String locale=request.getParameter("partySymbolLocale"+ i);
     	        if(locale!=null){
     	        	if(!locale.isEmpty()){
     	        		ps.setLocale(locale);
     	        	}
     	        }
        		partySymbols.add(ps);
        	}
        }
        domain.setPartySymbols(partySymbols);
	}
}
