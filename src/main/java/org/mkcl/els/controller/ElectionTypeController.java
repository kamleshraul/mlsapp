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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.ElectionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class ElectionTypeController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_electiontypes")
public class ElectionTypeController extends GenericController<ElectionType> {
	/** The Constant ASC. */
    private static final String ASC = "asc";
    
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final ElectionType domain,
                                        final BindingResult result,
                                        final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateUpdate(final ElectionType domain,
                                        final BindingResult result,
                                        final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * Custom validate.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     * @author nileshp
     * @since v1.0.0
     * Custom validate.
     */
    private void customValidate(final ElectionType domain,
                                final BindingResult result,
                                final HttpServletRequest request) {
        Map<String, String> names = new HashMap<String, String>();
        names.put("electionType", domain.getElectionType());
       
        // Check for duplicate instances
        Boolean duplicateParameter = domain.isDuplicate(names);
        Object[] params = new Object[1];
        params[0] = domain.getLocale().toString();
        if (duplicateParameter) {
            result.rejectValue(
                    "electionType", "NonUnique", params, "Duplicate Parameter");
        }
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.util.Locale, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model,
                               final ElectionType domain,
                               final Locale locale,
                               final HttpServletRequest request) {
        /*domain.setLocale(locale.toString());
        List<AssemblyCouncilType> assemblycounciltype = AssemblyCouncilType.findAll(
        		AssemblyCouncilType.class, "type", ASC, locale.toString());
        model.addAttribute("AssemblyCouncilType", assemblycounciltype);
        String type = ((CustomParameter) CustomParameter.findByName(CustomParameter.class, "DEFAULT_ASSEMBLYCOUNCILTYPE", locale.toString())).getValue();
        AssemblyCouncilType defaultAssemblycounciltype = AssemblyCouncilType.findByFieldName(AssemblyCouncilType.class, "type", type, locale.toString());
        domain.setAssemblycounciltype(defaultAssemblycounciltype);*/
    	
    	 String assemblycounciltype = ((CustomParameter) CustomParameter.findByName(
                 CustomParameter.class, "DEFAULT_HOUSETYPE", locale.toString())).getValue();
         populate(model, domain, locale, request, assemblycounciltype);
    }

   /**
    * Populate.
    *
    * @param model the model
    * @param domain the domain
    * @param locale the locale
    * @param request the request
    * @param assemblycounciltype the assemblycounciltype
    */
   protected void populate(final ModelMap model, final ElectionType domain, final Locale locale,
			final HttpServletRequest request, final String assemblycounciltype) {
		// TODO Auto-generated method stub
	   domain.setLocale(locale.toString());
       List<HouseType> assemblycounciltypelist = HouseType.findAll(
    		   HouseType.class, "type", "asc", locale.toString());
       HouseType selectedAssemblycounciltype = HouseType.findByFieldName(
    		   HouseType.class, "type", assemblycounciltype, locale.toString());
       List<HouseType> newassemblycounciltype = new ArrayList<HouseType>();
       newassemblycounciltype.add(selectedAssemblycounciltype);
       assemblycounciltypelist.remove(selectedAssemblycounciltype);
       newassemblycounciltype.addAll(assemblycounciltypelist);
       model.addAttribute("assemblycounciltype", newassemblycounciltype);
       domain.setHouseType(selectedAssemblycounciltype);
     }

/* (non-Javadoc)
 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
 */
@Override
    protected void populateEdit(final ModelMap model,
                                final ElectionType domain,
                                final HttpServletRequest request) {
        /*List<AssemblyCouncilType> assemblycounciltype = AssemblyCouncilType.findAll(
        		AssemblyCouncilType.class, "name", ASC, domain.getLocale());
        model.addAttribute("assemblycounciltype", assemblycounciltype);*/
	
	String assemblycounciltype = domain.getHouseType().getType();
    populate(
            model, domain, new Locale(domain.getLocale()), request,
            assemblycounciltype);
    model.addAttribute("electionType", domain);
    }
}
