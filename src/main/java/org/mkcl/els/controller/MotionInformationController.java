/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MotionInformationController.java
 * Created On: Jan 11, 2012
 */
package org.mkcl.els.controller;

import java.util.Locale;

import org.mkcl.els.domain.Grid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class MotionInformationController.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/motion_information")
public class MotionInformationController extends BaseController {

    /**
     * Index.
     *
     * @param model the model
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String index(final ModelMap model) {
        Grid grid = Grid.findByName("MEMBER_DETAIL_GRID");
        model.addAttribute("gridId", grid.getId());
        return "motion_information/assembly/list";
    }

    /**
     * New form.
     *
     * @param model the model
     * @param errors the errors
     * @param locale the locale
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newForm(final ModelMap model,
                          final Error errors,
                          final Locale locale) {
       return "motion_information/assembly/new";
    }

}
