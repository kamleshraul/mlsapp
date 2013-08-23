/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MenuItemController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.MenuItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Class MenuItemController.
 * 
 * @author amitd
 * @version v1.0.0
 */
@Controller
@RequestMapping("/menu")
public class MenuItemController extends GenericController<MenuItem> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.mkcl.els.controller.GenericController#list(org.springframework.ui
     * .ModelMap, java.util.Locale, javax.servlet.http.HttpServletRequest,
     * java.lang.String)
     */
    @Override
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(required = false) final String formtype,
            final ModelMap model, 
            final Locale locale,
            final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        model.addAttribute("urlPattern", urlPattern);
        String menuxml = MenuItem.getMenuXml(locale.toString());
        model.addAttribute("menu_xml", menuxml);
        return getURL(urlPattern) + "list";
    }

    /**
     * Gets the uRL.
     * 
     * @param urlPattern the url pattern
     * @return the uRL
     */
    private String getURL(final String urlPattern) {
        String[] urlParts = urlPattern.split("_");
        StringBuffer buffer = new StringBuffer();
        for (String i : urlParts) {
            buffer.append(i + "/");
        }
        return buffer.toString();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.mkcl.els.controller.GenericController#populateNew(org.springframework
     * .ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.util.Locale,
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model, final MenuItem domain,
            final String locale, final HttpServletRequest request) {
        ((BaseDomain) domain).setLocale(locale.toString());
        String parentId = request.getParameter("parentId");
        if (parentId != null) {
            MenuItem parent = MenuItem.findById(MenuItem.class,
                    Long.parseLong(parentId));
            domain.setParent(parent);
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
    protected void customValidateCreate(final MenuItem domain,
            final BindingResult result, final HttpServletRequest request) {
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
    protected void customValidateUpdate(final MenuItem domain,
            final BindingResult result, final HttpServletRequest request) {
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
    private void customValidate(final MenuItem domain,
            final BindingResult result, final HttpServletRequest request) {
        // Check for version mismatch
        Map<String, String> names = new HashMap<String, String>();
        names.put("textKey", domain.getTextKey());
        names.put("text", domain.getText());
        names.put("url", domain.getUrl());
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
        // Check for duplicate instance if the instance has a field "textkey"
        /*
         * String nameValue=domain.getTextKey(); Boolean duplicateParameter =
         * domain.isDuplicate("textKey",nameValue); Object[] params = new
         * Object[1]; params[0] =nameValue; if(duplicateParameter) {
         * errors.rejectValue("textKey", "NonUnique", params,
         * "Duplicate Parameter"); }
         */
        Boolean duplicateParameter = domain.isDuplicate(names);
        Object[] params = new Object[1];
        params[0] = domain.getLocale().toString();
        if (duplicateParameter) {
            result.rejectValue("textKey", "NonUnique", params,
                    "Duplicate Parameter");
        }

    }

}
