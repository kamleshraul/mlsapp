/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MemberRoleController.java
 * Created On: Apr 9, 2012
 */
package org.mkcl.els.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberRole;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberRoleController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/memberrole")
public class MemberRoleController extends GenericController<MemberRole>{
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
		protected void populateNew(final ModelMap model,
		                               final MemberRole domain,
		                               final String locale,
		                               final HttpServletRequest request) {
		        domain.setLocale(locale);
		    	 String houseType = ((CustomParameter) CustomParameter.findByName(
		                 CustomParameter.class, "DEFAULT_HOUSETYPE", locale)).getValue();
		    	 HouseType defaultHousetype = HouseType.findByFieldName(
			    		   HouseType.class, "type", houseType, locale);
		    	 domain.setHouseType(defaultHousetype);
		    	 List<HouseType> housetypelist = HouseType.findAll(
			    		   HouseType.class, "type", "asc", locale.toString());
		    	 model.addAttribute("assemblycounciltype", housetypelist);
		        
		    }
				 
		 /* (non-Javadoc)
 		 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
 		 */
 		@Override
		 protected void populateEdit(final ModelMap model,
		                                 final MemberRole domain,
		                                 final HttpServletRequest request) {
		 	domain.setLocale(domain.getLocale());
		 	List<HouseType> housetypelist = HouseType.findAll(
		    		   HouseType.class, "type", "asc",domain.getLocale());
	    	 model.addAttribute("assemblycounciltype", housetypelist);
		     }
	

}
