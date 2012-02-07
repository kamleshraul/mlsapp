/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.StateController.java
 * Created On: Feb 7, 2012
 */
package org.mkcl.els.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class StateController.
 *
 * @author sandeeps
 * @version v1.0.0
 */

@Controller
@RequestMapping("/states")
public class StateController extends BaseController {

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
    public final String list(final ModelMap model, final Locale locale) {
        final Grid grid = Grid.findByName("STATE_GRID", locale.toString());
        model.addAttribute("gridId", grid.getId());
        return "masters/states/list";
    }

    /**
     * _new.
     *
     * @param model the model
     * @param locale the locale
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public final String newForm(final ModelMap model, final Locale locale) {
        final State state = new State();
        state.setLocale(locale.toString());
        model.addAttribute("state", state);
        return "masters/states/new";
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
        final State state = State.findById(id);
        model.addAttribute("state", state);
        return "masters/states/edit";
    }

    /**
     * Creates the.
     *
     * @param state the state
     * @param result the result
     * @param model the model
     * @param request the request
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.POST)
    public final String create(@Valid @ModelAttribute("state") final State state,
                               final BindingResult result,
                               final ModelMap model,
                               final HttpServletRequest request) {
        this.validate(state, result);
        if (result.hasErrors()) {
            model.addAttribute("state", state);
            model.addAttribute("type", "error");
            model.addAttribute("msg", "create_failed");
            return "masters/states/new";
        } else {
            state.persist();
            return "redirect:states/" + state.getId() + "/edit?type=success&msg=create_success";
        }

    }

    /**
     * Edits the.
     *
     * @param state the state
     * @param result the result
     * @param model the model
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.PUT)
    public final String edit(@Valid @ModelAttribute("state") final State state,
                             final BindingResult result,
                             final ModelMap model) {
        this.validate(state, result);
        if (result.hasErrors()) {
            model.addAttribute("state", state);
            model.addAttribute("type", "error");
            model.addAttribute("msg", "update_failed");
            return "masters/states/edit";
        }
        state.update();
        return "redirect:states/" + state.getId() + "/edit?type=success&msg=update_success";
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
        final State state = State.findById(id);
        state.remove();
        model.addAttribute("type", "success");
        model.addAttribute("msg", "delete_success");
        return "info";
    }

    /**
     * Validate.
     *
     * @param state the state
     * @param errors the errors
     * @author nileshp
     * @since v1.0.0
     * Validate.
     */
    private void validate(final State state, final Errors errors) {
        final State duplicateParameter = State.findByName(state.getName());
        if (duplicateParameter != null
                && !duplicateParameter.getId().equals(state.getId())) {
            errors.rejectValue("name", "NonUnique");
        }

        if (state.getId() != null
                && !state.checkVersion()) {
            errors.reject("name", "Version_Mismatch");
        }
    }

}
