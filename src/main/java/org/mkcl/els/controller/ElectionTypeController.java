/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ElectionTypeController.java
 * Created On: Mar 17, 2012
 */
package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.HouseType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class ElectionTypeController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/electiontype")
public class ElectionTypeController extends GenericController<ElectionType> {

    /*
     * (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap,
     * org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model,
            final ElectionType domain,
            final String locale,
            final HttpServletRequest request) {
        String strHouseTypeName = ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DEFAULT_HOUSETYPE", locale)).getValue();
        HouseType defaultHouseType = HouseType.findByFieldName(HouseType.class, "type",
                strHouseTypeName, locale);
        domain.setHouseType(defaultHouseType);
        List<HouseType> houseTypes = HouseType.findAll(
                HouseType.class, "type", "asc", locale);
        model.addAttribute("assemblycounciltype", houseTypes);
    }


    /*
     * (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap,
     * org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateEdit(final ModelMap model,
            final ElectionType domain,
            final HttpServletRequest request) {
        List<HouseType> houseTypes = HouseType.findAll(
                HouseType.class, "type", "asc", domain.getLocale());
        model.addAttribute("assemblycounciltype", houseTypes);
    }
}
