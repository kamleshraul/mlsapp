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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

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
    protected void populateNew(final ModelMap model,
                               final Constituency domain,
                               final Locale locale,
                               final HttpServletRequest request) {
        String stateName = ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DEFAULT_STATE", locale.toString())).getValue();
        populate(model, domain, locale, request, stateName);
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
        String stateName = null;
        if (domain.getDistricts() != null) {
            List<District> districts = domain.getDistricts();
            if (!districts.isEmpty()) {
                stateName = districts.iterator().next().getState().getName();
            }
        }
        populate(
                model, domain, new Locale(domain.getLocale()), request,
                stateName);
    }

    /**
     * Populate.
     *
     * @param model the model
     * @param domain the domain
     * @param locale the locale
     * @param request the request
     * @param stateName the state name
     * @author nileshp
     * @since v1.0.0 Populate.
     */
    private void populate(final ModelMap model,
                          final Constituency domain,
                          final Locale locale,
                          final HttpServletRequest request,
                          String stateName) {
        domain.setLocale(locale.toString());
        List<State> states = State.findAll(
                State.class, "name", "asc", locale.toString());
        List<State> newStates = new ArrayList<State>();
        if (stateName != null) {
            State selectedState = State.findByName(
                    State.class, stateName, locale.toString());
            newStates.add(selectedState);
            states.remove(selectedState);
            newStates.addAll(states);
            model.addAttribute("states", newStates);
            List<District> districts = District.findDistrictsByStateId(
                    selectedState.getId(), "name", "asc");
            model.addAttribute("districts", districts);
        } else {
            stateName = ((CustomParameter) CustomParameter.findByName(
                    CustomParameter.class, "DEFAULT_STATE", locale.toString())).getValue();
            State selectedState = State.findByName(
                    State.class, stateName, locale.toString());
            newStates.add(selectedState);
            states.remove(selectedState);
            newStates.addAll(states);
            model.addAttribute("states", newStates);
            List<District> districts = District.findDistrictsByStateId(
                    selectedState.getId(), "name", "asc");
            model.addAttribute("districts", districts);
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
     * @param domain the domain
     * @param result the result
     * @param request the request
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

        // check for null districts
        if (domain.getDistricts() != null) {
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
                boolean duplicateParameter = domain.isDuplicate(
                        "name", nameValue);
                Object[] params = new Object[1];
                params[0] = nameValue;
                if (duplicateParameter) {
                    result.rejectValue(
                            "name", "NonUnique", params, "Duplicate Parameter");
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
}
