/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.HomeController.java
 * Created On: Jan 2, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
public class HomeController extends BaseController {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory
            .getLogger(HomeController.class);

    /**
     * Gets the Login page.
     *
     * @param lang the lang
     * @param model the model
     * @return the string
     * @author sujitas
     * @since v1.0.0
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false) final String lang,
                        final Model model) {
        List<String> locales = new ArrayList<String>();

        if (lang != null) {
            if (lang.equals("en") || lang.isEmpty()) {
                locales.add("en#English");
                locales.add("hi_IN#Hindi");
                locales.add("mr_IN#Marathi");
            } else if (lang.equals("hi_IN")) {
                locales.add("hi_IN#Hindi");
                locales.add("en#English");
                locales.add("mr_IN#Marathi");
            } else if (lang.equals("mr_IN")) {
                locales.add("mr_IN#Marathi");
                locales.add("hi_IN#Hindi");
                locales.add("en#English");
            }
        } else {
            locales.add("en#English");
            locales.add("hi_IN#Hindi");
            locales.add("mr_IN#Marathi");
        }
        model.addAttribute("locales", locales);
        return "login";
    }

    /**
     * Simply selects the home view to render by returning its name.
     *
     * @param model the model
     * @param request the request
     * @param locale the locale
     * @return the string
     * @author sujitas
     * @since v1.0.0
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(final ModelMap model,
                       final HttpServletRequest request,
                       final Locale locale) {
        String menuXml = MenuItem.getMenuXml(locale);
        HttpSession session = request.getSession();
        session.setAttribute("locale_els", locale.toString());
        model.addAttribute("menu_xml", menuXml);
        // used by datepicker to read the date,time format
        CustomParameter customParameter = CustomParameter.findByName("DATEPICKER_DATEFORMAT");
        String value = customParameter.getValue();
        model.addAttribute(
                "dateFormat", value
                );
        model.addAttribute(
                "timeFormat",
                CustomParameter.findByName("DATEPICKER_TIMEFORMAT").getValue());
        return "home2";
    }

}
