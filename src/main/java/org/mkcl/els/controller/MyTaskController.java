/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MyTaskController.java
 * Created On: Jan 21, 2012
 */
package org.mkcl.els.controller;

import java.util.Locale;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MotionApproval;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class MyTaskController.
 *
 * @author meenalw
 * @since v1.0.0
 */
@Controller
@RequestMapping("/mytask")
public class MyTaskController extends BaseController {


    /**
     * Index.
     *
     * @param model the model
     * @param locale the locale
     * @return the string
     * @author meenalw
     * @since v1.0.0
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String index(final ModelMap model , final Locale locale) {
        Grid grid = Grid.findByName("MYTASK_GRID" , locale.toString());
        model.addAttribute("gridId", grid.getId());
        return "motion_information/approval/myTask";
    }

    /**
     * New form.
     *
     * @param model the model
     * @param errors the errors
     * @param locale the locale
     * @return the string
     * @author meenalw
     * @since v1.0.0
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newForm(final ModelMap model,
                          final Error errors,
                          final Locale locale) {
        MotionApproval motionApproval = new MotionApproval();
        model.addAttribute("motionApproval", motionApproval);

        return "motion_information/approval/process";
    }


    /**
     * Edits the form.
     *
     * @param model the model
     * @param errors the errors
     * @param locale the locale
     * @return the string
     * @author meenalw
     * @since v1.0.0
     */
    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String editForm(final ModelMap model,
                           final Error errors,
                           final Locale locale) {


        return "motion_information/approval/process";
    }
}
