/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.TehsilController.java
 * Created On: Jan 21, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.mkcl.els.common.editors.DistrictEditor;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Class TehsilController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/tehsils")
public class TehsilController extends BaseController {

    /**
     * The tehsil service.
     *
     * @param model the model
     * @param locale the locale
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(final ModelMap model ,  final Locale locale) {
        Grid grid = Grid.findByName("TEHSIL_GRID" , locale.toString());
        model.addAttribute("gridId", grid.getId());
        return "masters/tehsils/list";
    }

    /**
     * New form.
     *
     * @param model the model
     * @param locale the locale
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newForm(final ModelMap model, final Locale locale) {
        Tehsil tehsil = new Tehsil();
        tehsil.setLocale(locale.toString());
        populateModel(model, tehsil,
                CustomParameter.findByName("DEFAULT_STATE").getValue());
        return "masters/tehsils/new";
    }

    /**
     * Edits the.
     *
     * @param id the id
     * @param model the model
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String edit(@PathVariable final Long id, final ModelMap model) {
        final Tehsil tehsil = Tehsil.findById(id);
        populateModel(model, tehsil, tehsil.getDistrict().getState().getName());
        return "masters/tehsils/edit";
    }

    /**
     * Creates the.
     *
     * @param tehsil the tehsil
     * @param result the result
     * @param model the model
     * @param state the state
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("tehsil") final Tehsil tehsil,
            final BindingResult result, final ModelMap model,
            @RequestParam final String state) {
        this.validate(tehsil, result);
        if (result.hasErrors()) {
            populateModel(model, tehsil, state);
            model.addAttribute("type", "error");
            model.addAttribute("msg", "create_failed");
            return "masters/tehsils/new";
        }
        tehsil.persist();
        return "redirect:tehsils/" + tehsil.getId()
                + "/edit?type=success&msg=create_success";

    }

    /**
     * Edits the.
     *
     * @param tehsil the tehsil
     * @param result the result
     * @param model the model
     * @param state the state
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.PUT)
    public String edit(@Valid @ModelAttribute("tehsil") final Tehsil tehsil,
            final BindingResult result, final ModelMap model,
            @RequestParam final String state) {
        this.validate(tehsil, result);
        if (result.hasErrors()) {
            populateModel(model, tehsil, state);
            model.addAttribute("type", "error");
            model.addAttribute("msg", "update_failed");
            return "masters/tehsils/edit";
        }
        tehsil.update(tehsil);
        return "redirect:tehsils/" + tehsil.getId()
                + "/edit?type=success&msg=update_success";

    }

    /**
     * Delete.
     *
     * @param id the id
     * @param model the model
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "{id}/delete", method = RequestMethod.DELETE)
    public String delete(@PathVariable final Long id, final ModelMap model) {
        Tehsil tehsil = Tehsil.findById(id);
        tehsil.remove();
        model.addAttribute("type", "success");
        model.addAttribute("msg", "delete_success");
        return "info";
    }

    /**
     * Validate.
     *
     * @param tehsil the tehsil
     * @param errors the errors
     * @author nileshp
     * @since v1.0.0
     * Validate.
     */
    private void validate(final Tehsil tehsil, final Errors errors) {
        if (tehsil.getName() != null) {
            if (tehsil.getLocale().equals("en")) {
                String name = tehsil.getName();
                Pattern pattern = Pattern.compile("[A-Za-z ]{1,50}");
                Matcher matcher = pattern.matcher(name);
                if (!matcher.matches()) {
                    errors.rejectValue("name", "Pattern");
                }
                if (name.length() > 100 || name.length() < 1) {
                    errors.rejectValue("name", "Size");

                }
            }
        }
        Tehsil duplicateParameter = Tehsil.findByName(tehsil.getName());
        if (duplicateParameter != null) {
            if (!duplicateParameter.getId().equals(tehsil.getId())) {
                errors.rejectValue("name", "NonUnique");
            }
        }
        if (tehsil.getId() != null) {
            if (!tehsil.getVersion().equals(
                    Tehsil.findById(tehsil.getId()).getVersion())) {
                errors.reject("name", "Version_Mismatch");
            }
        }
    }

    /**
     * Inits the binder.
     *
     * @param binder the binder
     * @author nileshp
     * @since v1.0.0
     * Inits the binder.
     */
    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(District.class, new DistrictEditor());
    }

    /**
     * Populate model.
     *
     * @param model the model
     * @param tehsil the tehsil
     * @param stateName the state name
     * @author nileshp
     * @since v1.0.0
     * Populate model.
     */
    private void populateModel(final ModelMap model, final Tehsil tehsil,
            final String stateName) {
        List<State> states = State.findAllSorted("name" , tehsil.getLocale() , false);
        State selectedState = State.findByName(stateName);
        List<State> newStates = new ArrayList<State>();
        newStates.add(selectedState);
        states.remove(selectedState);
        newStates.addAll(states);
        model.addAttribute("tehsil", tehsil);
        model.addAttribute("states", newStates);
        model.addAttribute("districts",
                District.findDistrictsByStateId(selectedState.getId()));
    }

}
