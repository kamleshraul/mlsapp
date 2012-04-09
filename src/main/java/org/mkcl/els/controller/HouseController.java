/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.HouseController.java
 * Created On: Apr 5, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class HouseController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/house")
public class HouseController extends GenericController<House>{
	
	/** The Constant ASC. */
	private static final String ASC = "asc";
	
	
	protected void customValidateCreate(final House domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	
	protected void customValidateUpdate(final House domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	/**
	 * Custom validate.
	 *
	 * @param house the house
	 * @param result the result
	 * @param request the request
	 */
	private void customValidate(final House house,
			final BindingResult result, final HttpServletRequest request) {
		new HashMap<String, String>();
			if(house.getType().equals("lowerhouse"))
			{
				Boolean isDuplicateParameter = house.isDuplicate("name",house.getName());
				Object[] params = new Object[1];
				params[0] = house.getName();
				if (isDuplicateParameter) {
					result.rejectValue("name", "NonUnique", params,
							"Duplicate Parameter");
				}
			
				Date formationDate=house.getFormationDate();
				Date dissolveDate=house.getDissolveDate();
				if(formationDate.after(dissolveDate)){
					result.rejectValue("formationDate","FormationDateLtDissolveDate","Invalid Date");
				}
			
			
				Date firstDate=house.getFirstDate();
				Date endDate=house.getLastDate();
				if(firstDate.after(endDate)){
					result.rejectValue("firstDate","FirstDateLtLastDate","Invalid Date");
				}
			}
			if (house.isVersionMismatch()) {
				result.rejectValue("VersionMismatch", "version");
			}
			
			
			
			if (result.hasErrors()) {
			System.out.println("error");
		}
	}
	
	
	@Override
	protected void populateNew(final ModelMap model, final House house,
			final String locale, final HttpServletRequest request) {
		house.setLocale(locale);
		String housetype = ((CustomParameter) CustomParameter.findByName(
                 CustomParameter.class, "DEFAULT_HOUSETYPE", locale)).getValue();
		HouseType defaultHouseType=HouseType.findByFieldName(HouseType.class, "type", housetype, locale);
		house.setType(defaultHouseType);
		List<HouseType> housetypeList = HouseType.findAll(
	    		   HouseType.class, "type", "asc", locale);
		model.addAttribute("assemblycounciltype", housetypeList);
        
	}
	
	
	@Override
	protected void populateEdit(final ModelMap model, final House house,
			final HttpServletRequest request) {
		house.setLocale(house.getLocale());
		String assemblycounciltype = house.getType().getType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", assemblycounciltype, house.getLocale());
		model.addAttribute("houseType", houseType.getType());
		model.addAttribute("housetype",houseType.getId());
	}
	
	@Override
	 protected void preValidateCreate(final House domain,
				final BindingResult result, final HttpServletRequest request) {
		 HouseType housetype=HouseType.findByFieldName(HouseType.class, "type", domain.getType().getType(), domain.getLocale());
		 domain.setType(housetype);
		}
	
}
