/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DistrictController.java
 * Created On: Feb 7, 2012
 */

package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.mkcl.els.common.editors.StateEditor;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.State;
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

// TODO: Auto-generated Javadoc
/**
 * The Class DistrictController.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/districts")
public class DistrictController extends BaseController {

    /**
     * Index.
     *
     * @param model the model
     * @param locale the locale
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public final String list(final ModelMap model , final Locale locale) {
        Grid grid = Grid.findByName("DISTRICT_GRID" , locale.toString());
        model.addAttribute("gridId", grid.getId());
        return "masters/districts/list";
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
    public final String newForm(final ModelMap model, final Locale locale) {
        District district = new District();
        district.setLocale(locale.toString());
        populateModel(
                model, district, CustomParameter.findByName("DEFAULT_STATE")
                        .getValue());
        return "masters/districts/new";
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
    public final String edit(@PathVariable final Long id, final ModelMap model) {
        final District district = District.findById(id);
        populateModel(model, district, district.getState().getName());
        return "masters/districts/edit";
    }

    /**
     * Creates the.
     *
     * @param district the district
     * @param result the result
     * @param model the model
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.POST)
    public final String create(@Valid @ModelAttribute("district") final District district,
                               final BindingResult result,
                               final ModelMap model) {
        this.validate(district, result);
        if (result.hasErrors()) {
            populateModel(model, district, district.getState().getName());
            model.addAttribute("type", "error");
            model.addAttribute("msg", "create_failed");
            return "masters/districts/new";
        }
        district.persist();
        return "redirect:districts/" + district.getId()
                + "/edit?type=success&msg=create_success";

    }

    /**
     * Edits the.
     *
     * @param district the district
     * @param result the result
     * @param model the model
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.PUT)
    public final String edit(@Valid @ModelAttribute("district") final District district,
                             final BindingResult result,
                             final ModelMap model) {
        this.validate(district, result);
        if (result.hasErrors()) {
            populateModel(model, district, district.getState().getName());
            model.addAttribute("type", "error");
            model.addAttribute("msg", "update_failed");
            return "masters/districts/edit";
        }
        district.update();
        return "redirect:districts/" + district.getId()
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
    public final String delete(@PathVariable final Long id, final ModelMap model) {
        final District district = District.findById(id);
        district.remove();
        model.addAttribute("type", "success");
        model.addAttribute("msg", "delete_success");
        return "info";
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
    public final void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(State.class, new StateEditor());
    }

    /**
     * Populate model.
     *
     * @param model the model
     * @param district the district
     * @param stateName the state name
     * @author nileshp
     * @since v1.0.0
     * Populate model.
     */
    private void populateModel(final ModelMap model,
                               final District district,
                               final String stateName) {
        List<State> states = State.findAllSorted(
                "name", district.getLocale(), false);
        State selectedState = State.findByName(stateName);
        List<State> newStates = new ArrayList<State>();
        newStates.add(selectedState);
        states.remove(selectedState);
        newStates.addAll(states);
        model.addAttribute("district", district);
        model.addAttribute("states", newStates);
    }

    /**
     * Validate.
     *
     * @param district the district
     * @param errors the errors
     * @author nileshp
     * @since v1.0.0
     * Validate.
     */
    private void validate(final District district, final Errors errors) {
        District duplicateParameter = District.findByName(district.getName());
        if (duplicateParameter != null) {
            if (!duplicateParameter.getId().equals(district.getId())) {
                errors.rejectValue("name", "NonUnique");
            }
        }
        if (district.getId() != null) {
            /*
             * if (!district.getVersion().equals(
             * District.findById(district.getId()).getVersion())) {
             * errors.reject("name", "Version_Mismatch"); }
             */
            if (!district.checkVersion()) {
                errors.reject("version", "Version_Mismatch");
            }
        }
    }

}
