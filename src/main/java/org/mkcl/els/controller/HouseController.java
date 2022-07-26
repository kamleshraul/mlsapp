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

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class HouseController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/house")
public class HouseController extends GenericController<House>{

	@Override
    protected void customValidateCreate(final House domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}


	@Override
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
			final BindingResult result, 
			final HttpServletRequest request) {
		if(house.getType().getType().equals("lowerhouse"))
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
				if(endDate!=null){
					if(firstDate.after(endDate)){
						result.rejectValue("firstDate","FirstDateLtLastDate","Invalid Date");
					}
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
	protected void populateNew(final ModelMap model, 
			final House house,
			final String locale, 
			final HttpServletRequest request) {
		house.setLocale(locale);
		String housetype = ((CustomParameter) CustomParameter.findByName(
                 CustomParameter.class, "DEFAULT_HOUSETYPE", locale)).getValue();
		HouseType defaultHouseType=HouseType.findByFieldName(HouseType.class, "name", housetype, locale);
		house.setType(defaultHouseType);
		List<HouseType> housetypeList = HouseType.findAll(
	    		   HouseType.class, "type", "asc", locale);
		model.addAttribute("assemblycounciltype", housetypeList);

	}


	@Override
	protected void populateEdit(final ModelMap model, 
			final House house,
			final HttpServletRequest request) {
		house.setLocale(house.getLocale());
		String assemblycounciltype = house.getType().getType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", assemblycounciltype, house.getLocale());
		model.addAttribute("houseType", houseType.getType());
		model.addAttribute("housetype",houseType.getId());
	}

	@Override
	 protected void preValidateCreate(final House domain,
				final BindingResult result, 
				final HttpServletRequest request) {
		String htype=request.getParameter("houseType");
		 HouseType housetype=HouseType.findByFieldName(HouseType.class, "type", htype, domain.getLocale());
		 domain.setType(housetype);
	}
	
	@Override
	protected void populateCreateIfErrors(final ModelMap model,
			final House domain,
			final HttpServletRequest request) {
		String htype=request.getParameter("houseType");
		HouseType housetype=HouseType.findByFieldName(HouseType.class, "type", htype, domain.getLocale());
		List<HouseType> housetypeList = HouseType.findAll(
  		   HouseType.class, "type", "asc", domain.getLocale());
		List<HouseType> newassemblycounciltype = new ArrayList<HouseType>();
		newassemblycounciltype.add(housetype);
		housetypeList.remove(housetype);
		newassemblycounciltype.addAll(housetypeList);
		model.addAttribute("assemblycounciltype", newassemblycounciltype);
		domain.setType(housetype);
		populateEdit(model, domain, request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}

	@Override
	protected void populateAfterCreate(ModelMap model, House domain,
			HttpServletRequest request) throws Exception 
	{
		if(domain.getType().getType().equals(ApplicationConstants.LOWER_HOUSE))
		{
			CustomParameter csptLatestAssemblyHouseFormationDate = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE, "");
			if(csptLatestAssemblyHouseFormationDate!=null && csptLatestAssemblyHouseFormationDate.getValue()!=null) {
				Date latestHouseFormationDateInDB = FormaterUtil.formatStringToDate(csptLatestAssemblyHouseFormationDate.getValue(), ApplicationConstants.DB_DATEFORMAT);
				if(latestHouseFormationDateInDB.before(domain.getFormationDate())) {
					String updatedLatestHouseFormationDate = FormaterUtil.formatDateToString(domain.getFormationDate(), ApplicationConstants.DB_DATEFORMAT);
					csptLatestAssemblyHouseFormationDate.setValue(updatedLatestHouseFormationDate);
					csptLatestAssemblyHouseFormationDate.merge();
					ApplicationConstants.LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE = updatedLatestHouseFormationDate;
				}
			}
		}
	}

	@Override
	protected void populateUpdateIfNoErrors(ModelMap model, House domain,
			HttpServletRequest request) throws Exception 
	{
		if(domain.getType().getType().equals(ApplicationConstants.LOWER_HOUSE))
		{
			CustomParameter csptLatestAssemblyHouseFormationDate = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE, "");
			if(csptLatestAssemblyHouseFormationDate!=null && csptLatestAssemblyHouseFormationDate.getValue()!=null) {
				Date latestHouseFormationDateInDB = FormaterUtil.formatStringToDate(csptLatestAssemblyHouseFormationDate.getValue(), ApplicationConstants.DB_DATEFORMAT);
				House house = House.findById(House.class, domain.getId());
				if(latestHouseFormationDateInDB.equals(house.getFormationDate())) {
					String updatedLatestHouseFormationDate = FormaterUtil.formatDateToString(domain.getFormationDate(), ApplicationConstants.DB_DATEFORMAT);
					csptLatestAssemblyHouseFormationDate.setValue(updatedLatestHouseFormationDate);
					csptLatestAssemblyHouseFormationDate.merge();
					ApplicationConstants.LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE = updatedLatestHouseFormationDate;
				}
			}
		}
	}
	
}