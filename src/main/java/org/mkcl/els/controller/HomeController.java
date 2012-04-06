/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.HomeController.java
 * Created On: Mar 30, 2012
 */
package org.mkcl.els.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// TODO: Auto-generated Javadoc
/**
 * The Class HomeController.
 * 
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
public class HomeController extends BaseController {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory
            .getLogger(HomeController.class);

    /** The Constant DEFAULT_LOCALE. */
    private static final String DEFAULT_LOCALE = "mr_IN";

    /** The Constant ASC. */
    private static final String ASC = "asc";

    /** The Constant DESC. */
    private static final String DESC = "desc";

    /**
     * Login.
     * 
     * @param lang the lang
     * @param model the model
     * @param request the request
     * @param response the response
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false) final String lang,
            final Model model, final HttpServletRequest request,
            final HttpServletResponse response, final Locale locale) {
        List<ApplicationLocale> supportedLocales = ApplicationLocale.findAll(
                ApplicationLocale.class, "language", ASC, "");
        if (lang != null) {
            model.addAttribute("selectedLocale", lang);
        }
        else {
            model.addAttribute("selectedLocale", DEFAULT_LOCALE);
        }
        model.addAttribute("locales", supportedLocales);
        return "login";
    }

    /**
     * Home.
     * 
     * @param model the model
     * @param request the request
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(final ModelMap model, final HttpServletRequest request,
            final Locale locale) {
    	String menuXml = MenuItem.getMenuXml(locale.toString());
        model.addAttribute("menu_xml", menuXml);
        model.addAttribute("dateFormat",
                ((CustomParameter) CustomParameter.findByName(
                        CustomParameter.class, "DATEPICKER_DATEFORMAT", ""))
                        .getValue());
        model.addAttribute("timeFormat",
                ((CustomParameter) CustomParameter.findByName(
                        CustomParameter.class, "DATEPICKER_TIMEFORMAT", ""))
                        .getValue());
        model.addAttribute("authusername", this.getCurrentUser().getUsername());
        model.addAttribute("authtitle", this.getCurrentUser().getTitle());
        model.addAttribute("authfirstname", this.getCurrentUser()
                .getFirstName());
        model.addAttribute("authmiddlename", this.getCurrentUser()
                .getMiddleName());
        model.addAttribute("authlastname", this.getCurrentUser().getLastName());
        model.addAttribute("logintime", new Date());
        model.addAttribute("authhousetype",this.getCurrentUser().getHouseType());
        return "home";
    }    
}
