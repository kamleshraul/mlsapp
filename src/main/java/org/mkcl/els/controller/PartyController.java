/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.PartyController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Party;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class PartyController.
 *
 * @author meenalw
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_parties")
public class PartyController extends GenericController<Party> {

    /**
     * New form.
     *
     * @param model the model
     * @param party the party
     * @param locale the locale
     * @param request the request
     * @author meenalw
     * @since v1.0.0
     */
    @Override
    protected void populateNew(final ModelMap model,
                               final Party party,
                               final Locale locale,
                               final HttpServletRequest request) {

        party.setLocale(locale.toString());
        model.addAttribute(party);
        CustomParameter customParameter = CustomParameter.findByName(
                CustomParameter.class, "PARTY_FLAG_EXTENSION", null);
        model.addAttribute("photoExt", customParameter.getValue());
        CustomParameter customParameter1 = CustomParameter.findByName(
                CustomParameter.class, "PARTY_FLAG_SIZE", null);
        model.addAttribute(
                "photoSize",
                Long.parseLong(customParameter1.getValue()) * 1024 * 1024);
    }

    /**
     * Edits the.
     *
     * @param model the model
     * @param party the party
     * @param request the request
     * @author meenalw
     * @since v1.0.0
     */
    @Override
    protected void populateEdit(final ModelMap model,
                                final Party party,
                                final HttpServletRequest request) {
        model.addAttribute(party);
        CustomParameter customParameter = CustomParameter.findByName(
                CustomParameter.class, "PHOTO_EXTENSION", null);
        model.addAttribute("photoExt", customParameter.getValue());
        CustomParameter customParameter1 = CustomParameter.findByName(
                CustomParameter.class, "PHOTO_SIZE", null);
        model.addAttribute(
                "photoSize",
                Long.parseLong(customParameter1.getValue()) * 1024 * 1024);
        if (party.getPhoto() != null && (!party.getPhoto().isEmpty())) {
            final Document document = Document.findByTag(party.getPhoto());
            if (document != null) {
                model.addAttribute(
                        "photoOriginalName", document.getOriginalFileName());
                model.addAttribute("party", party);
            }
        }
    }

}
