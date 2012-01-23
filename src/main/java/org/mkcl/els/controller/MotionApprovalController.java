/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MotionApprovalController.java
 * Created On: Jan 23, 2012
 */
package org.mkcl.els.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MotionApproval;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class MotionApprovalController.
 *
 * @author samiksham
 * @since v1.0.0
 */
@Controller
@RequestMapping("/motion_approval")
public class MotionApprovalController extends BaseController {

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
    public String index(final ModelMap model,  final Locale locale) {
        Grid grid = Grid.findByName("MOIS_APPROVAL_GRID", locale.toString());
        model.addAttribute("gridId", grid.getId());
        return "motion_information/approval/list";
    }

    /**
     * New form.
     *
     * @param model the model
     * @param errors the errors
     * @param locale the locale
     * @return the string
     * @author samiksham
     * @since v1.0.0
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newForm(final ModelMap model,
                          final Error errors,
                          final Locale locale) {
        // MotionInformation motionInformation = new MotionInformation();
        // model.addAttribute("motionInformation", motionInformation);
        MotionApproval motionApproval = new MotionApproval();
        model.addAttribute("motionApproval", motionApproval);

        return "motion_information/approval/edit";
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
        // MotionInformation motionInformation = new MotionInformation();
        // motionInformation.setId(1L);
        // model.addAttribute("motionApproval", motionApproval);
        MotionApproval motionApproval = new MotionApproval();
        motionApproval.setId(1L);
        model.addAttribute("motionApproval", motionApproval);

        return "motion_information/approval/edit";
    }

    /**
     * Creates the.
     *
     * @param motionApproval the motion approval
     * @param result the result
     * @param model the model
     * @param request the request
     * @return the string
     * @author meenalw
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("motionApproval")
    final MotionApproval motionApproval,
                         final BindingResult result,
                         final ModelMap model,
                         final HttpServletRequest request) {

        motionApproval.setId(1L);
        model.addAttribute("motionApproval", motionApproval);

        return "redirect:motion_approval/" + motionApproval.getId()
                + "/edit?type=success&msg=create_success";
    }

}
