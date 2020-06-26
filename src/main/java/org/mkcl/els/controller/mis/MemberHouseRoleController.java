/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberHouseRoleController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The Class MemberHouseRoleController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/house")
public class MemberHouseRoleController extends BaseController {

	/**
	 * List.
	 *
	 * @param formtype the formtype
	 * @param model the model
	 * @param locale the locale
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(@RequestParam(required = false) final String formtype,
			final ModelMap model, final Locale locale,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String urlPattern=servletPath.split("\\/list")[0];
		Grid grid;
		try {
			grid = Grid.findByDetailView(urlPattern, locale.toString());
			model.addAttribute("gridId", grid.getId());
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
		
		model.addAttribute("urlPattern", urlPattern);
		model.addAttribute("houseType",request.getParameter("houseType"));
		model.addAttribute("house",request.getParameter("house"));
		return "member/house/list";
	}

	/**
	 * New form.
	 *
	 * @param model the model
	 * @param locale the locale
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newForm(final ModelMap model, final Locale locale,
			final HttpServletRequest request) {
		final String urlPattern = request.getServletPath().split("\\/")[1];
		HouseMemberRoleAssociation domain = new HouseMemberRoleAssociation();
		populateNew(model, domain, locale.toString(), request);
		//THIS IS USED TO REMOVE THE BUG WHERE IN RECORD UPDATED MESSAGE
		//APPEARS WHEN CLICKED ON NEW REOCRD
		model.addAttribute("type", "");
		model.addAttribute("domain", domain);
		model.addAttribute("urlPattern", urlPattern);
		return "member/house/new";
	}

	/**
	 * Edits the.
	 *
	 * @param recordIndex the record index
	 * @param model the model
	 * @param request the request
	 * @param member the member
	 * @param locale the locale
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{recordIndex}/edit", method = RequestMethod.GET)
	public String edit(final @PathVariable("recordIndex") int recordIndex,
			final ModelMap model, final HttpServletRequest request,
			final @RequestParam("member") Long member, final Locale locale) {
		final String urlPattern = request.getServletPath().split("\\/")[1];
		HouseMemberRoleAssociation domain = HouseMemberRoleAssociation
		.findByMemberIdAndId(member, recordIndex);
		//this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
          request.getSession().removeAttribute("type");
        }
		populateEdit(model, domain, request, locale.toString());
		model.addAttribute("domain", domain);
		model.addAttribute("urlPattern", urlPattern);
		return "member/house/edit";
	}

	/**
	 * Creates the.
	 *
	 * @param model the model
	 * @param request the request
	 * @param redirectAttributes the redirect attributes
	 * @param locale the locale
	 * @param domain the domain
	 * @param result the result
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(
			final ModelMap model,
			final HttpServletRequest request,
			final RedirectAttributes redirectAttributes,
			final Locale locale,
			@Valid @ModelAttribute("domain") final HouseMemberRoleAssociation domain,
			final BindingResult result) {
		final String urlPattern = request.getServletPath().split("\\/")[1];
		validateCreate(domain, result, request);
		model.addAttribute("domain", domain);
		if (result.hasErrors()) {
			poulateCreateIfErrors(model, domain, request, locale.toString());
			model.addAttribute("type", "error");
			return "member/house/new";
		}
		populateCreateIfNoErrors(model, domain, request);
		domain.persist();
		request.getSession().setAttribute("refresh", "");
		redirectAttributes.addFlashAttribute("type", "success");
		request.getSession().setAttribute("type","success");
		redirectAttributes.addFlashAttribute("msg", "create_success");
		String returnUrl = "redirect:/member/house/"
			+ domain.getRecordIndex() + "/edit?member="
			+ request.getParameter("member");
		return returnUrl;
	}

	/**
	 * Update.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param model the model
	 * @param redirectAttributes the redirect attributes
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(
			final @Valid @ModelAttribute("domain") HouseMemberRoleAssociation domain,
			final BindingResult result, final ModelMap model,
			final RedirectAttributes redirectAttributes,
			final HttpServletRequest request, final Locale locale) {
		final String urlPattern = request.getServletPath().split("\\/")[1];
		validateUpdate(domain, result, request);
		model.addAttribute("domain", domain);
		if (result.hasErrors()) {
			poulateUpdateIfErrors(model, domain, request, locale.toString());
			model.addAttribute("type", "error");
			return "member/house/edit";
		}
		populateUpdateIfNoErrors(model, domain, request);
		domain.merge();
		redirectAttributes.addFlashAttribute("type", "success");
		redirectAttributes.addFlashAttribute("msg", "update_success");
	    request.getSession().setAttribute("type","success");
		String returnUrl = "redirect:/member/house/"
			+ domain.getRecordIndex() + "/edit?member="
			+ request.getParameter("member");
		return returnUrl;
	}

	/**
	 * Delete.
	 *
	 * @param recordIndex the record index
	 * @param member the member
	 * @param model the model
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value = "/{recordIndex}/delete",
			method = RequestMethod.DELETE)
			public String delete(final @PathVariable("recordIndex") int recordIndex,
					final @RequestParam("member") Long member, final ModelMap model,
					final HttpServletRequest request) {
		HouseMemberRoleAssociation association = HouseMemberRoleAssociation
		.findByMemberIdAndId(member, recordIndex);
		if (association.getMember() != null) {
			association.remove();
		}
		return "info";
	}

	/**
	 * Populate new.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param locale the locale
	 * @param request the request
	 */

	private void  populateModel(final ModelMap model,
			final HouseMemberRoleAssociation domain, final String locale,final HttpServletRequest request,final String houseType,final Long member){
		try {
			//populating house types
			List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "name",ApplicationConstants.ASC, locale);
			model.addAttribute("houseTypes",houseTypes);

			//populating houses.initially all display will be on the basis of selected house type
			//but user can change the house type as per requirement.
			List<House> houses=House.findByHouseType(houseType,locale);
			model.addAttribute("houses",houses);

			//populating roles
			List<MemberRole> memberRoles=MemberRole.findByHouseType(houseType,locale.toString());
			model.addAttribute("roles",memberRoles);

			//display death date
			Member selectedMember=Member.findById(Member.class,member);
			model.addAttribute("fullname", selectedMember.getFullname());
			if(selectedMember.getDeathDate()!=null){
			model.addAttribute("deathdate",FormaterUtil.getDateFormatter(locale).format(selectedMember.getDeathDate()));
			}

			//populating constituencies
			//constituencies are selected depending on locale and housetype
			List<MasterVO> constituencies=Constituency.findAllByHouseType(houseType, locale);
			model.addAttribute("constituencies",constituencies );

			//if a particular house is selected and election results data has been filled then initialize constituencies with
			//data  filled in selected member election result.
			Constituency currentConstituency=null;
			if(domain.getConstituency()!=null){
				currentConstituency=domain.getConstituency();
			}else {
				if(request.getParameter("house")!=null){
					List<ElectionResult> electionResults=ElectionResult.findByHouseAndMember(Long.parseLong(request.getParameter("house")),member,locale);
					if(!electionResults.isEmpty()){
					    //say for a particular house and member there are more than one election results.say first
					    //got selected through general election and then for reasons went for byelection.
					    //then clearly in that case we should always read constituency of byelection as it should have
					    //more precedence than the general election.
						currentConstituency=electionResults.get(0).getConstituency();
						if(currentConstituency!=null){
							domain.setConstituency(currentConstituency);
						}
					}
				}
			}
			if(currentConstituency==null){
				if(!constituencies.isEmpty()){
					currentConstituency=Constituency.findById(Constituency.class, constituencies.get(0).getId());
				}
			}

			//populate districts and divisions which will be read only fields
			if(currentConstituency!=null){
				if(!currentConstituency.getDistricts().isEmpty()){
					model.addAttribute("districts",currentConstituency.getDistricts());
					if(currentConstituency.getDistricts().get(0).getDivision()!=null){
						model.addAttribute("division",currentConstituency.getDistricts().get(0).getDivision().getName());
					}
				}
			}
			//populate currentconstituency
			if(currentConstituency!=null){
			model.addAttribute("currentConstituency",currentConstituency.getId());
			}
		} catch (NumberFormatException e) {
			model.addAttribute("error", "Invalid number provided.");
			e.printStackTrace();
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}


	}

	protected void populateNew(final ModelMap model,
			final HouseMemberRoleAssociation domain, final String locale,
			final HttpServletRequest request) {
		//populating house type
		String houseType=request.getParameter("houseType");
		model.addAttribute("houseType",houseType);

		//populating member
		Long member = Long.parseLong(request.getParameter("member"));
		model.addAttribute("member", member);

		populateModel(model, domain, locale, request, houseType, member);

		//populating dates to first and last date of assembly/council in case house is selected.This can be changed.
		String selectedAssembly=request.getParameter("house");
		if(selectedAssembly!=null){
			if(!selectedAssembly.isEmpty()){
				Long selectedHouseid=Long.parseLong(selectedAssembly);
				House selectedHouse=House.findById(House.class,selectedHouseid);
				domain.setFromDate(selectedHouse.getFirstDate());
				domain.setToDate(selectedHouse.getLastDate());
				//populating sitting/ex
				if(domain.getToDate()!=null){
		        if(domain.getToDate().after(new Date())){
		            domain.setIsSitting(true);
		        }else{
		            domain.setIsSitting(false);
		        }
				}
			}
		}
		try {
			// setting the value of the recordIndex field
			int index = HouseMemberRoleAssociation.findHighestRecordIndex(member);
			domain.setRecordIndex(index + 1);
			domain.setLocale(locale.toString());
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
	}

	/**
	 * Populate edit.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 * @param locale the locale
	 */
	private void populateEdit(final ModelMap model,
			final HouseMemberRoleAssociation domain,
			final HttpServletRequest request, final String locale) {
		String houseType=domain.getHouse().getType().getType();
		model.addAttribute("houseType",houseType);
		model.addAttribute("houseTypeName",domain.getHouse().getType().getName());
		model.addAttribute("houseId", domain.getHouse().getId());
	    model.addAttribute("houseName", domain.getHouse().getDisplayName());
	    model.addAttribute("roleId",domain.getRole().getId());
	    model.addAttribute("roleName",domain.getRole().getName());
		Long member=domain.getMember().getId();
		model.addAttribute("member",member);
		populateModel(model, domain, locale, request,houseType ,member);
	}

	/**
	 * Poulate create if errors.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 * @param locale the locale
	 */
	private void poulateCreateIfErrors(final ModelMap model,
			final HouseMemberRoleAssociation domain, final HttpServletRequest request,
			final String locale) {
		populateEdit(model, domain,request,locale);
	}

	/**
	 * Poulate update if errors.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 * @param locale the locale
	 */
	private void poulateUpdateIfErrors(final ModelMap model,
			final HouseMemberRoleAssociation domain, final HttpServletRequest request,
			final String locale) {
		populateEdit(model, domain, request,locale);
	}

	/**
	 * Validate create.
	 *
	 * @param domain the domain
	 * @param errors the errors
	 * @param request the request
	 */
	private void validateCreate(final HouseMemberRoleAssociation domain,
			final Errors errors, final HttpServletRequest request) {
		try {
			Boolean isDuplicate=domain.isDuplicate();
			if (isDuplicate) {
				Object[] params = new Object[4];
				params[0] = domain.getHouse().getName();
				params[1] = domain.getRole().getName();
				params[2]=domain.getMember().getFullname();
				//params[2] = domain.getFromDate();
				//params[3] = domain.getToDate();
				errors.rejectValue("recordIndex", "Duplicate", params,
						"Member:"+params[2]+", House:" + params[0] + ", Role:" + params[1]
						                                                     + " already exists");
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		}
		if(domain.getConstituency()==null){
			errors.rejectValue("constituency","NotEmpty");
		}
		if(domain.getRole()==null){
			errors.rejectValue("role","NotEmpty");
		}
	}

	/**
	 * Validate update.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void validateUpdate(final HouseMemberRoleAssociation domain,
			final BindingResult result, final HttpServletRequest request) {
		try {
			if (domain.isVersionMismatch()) {
				result.rejectValue("VersionMismatch", "version");
			}
		} catch (ELSException e) {
			result.reject("error", e.getParameter());
		}
	}

	/**
	 * Populate create if no errors.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 */
	private void populateCreateIfNoErrors(final ModelMap model,
			final HouseMemberRoleAssociation domain,
			final HttpServletRequest request) {
//		request.getSession().setAttribute("member",
//				Long.parseLong(request.getParameter("member")));
//		request.getSession().setAttribute("houseType",
//				(request.getParameter("houseType")));
//		request.getSession().setAttribute("house",
//				Long.parseLong(request.getParameter("house")));
	}

	/**
	 * Populate update if no errors.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 */
	private void populateUpdateIfNoErrors(final ModelMap model,
			final HouseMemberRoleAssociation domain, final HttpServletRequest request) {
//		request.getSession().setAttribute("member",
//				Long.parseLong(request.getParameter("member")));
//		request.getSession().setAttribute("houseType",
//				request.getParameter("houseType"));
//		request.getSession().setAttribute("house",
//				Long.parseLong(request.getParameter("house")));
	}

	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		String locale=this.getUserLocale().toString();		
		
		SimpleDateFormat dateFormat = FormaterUtil.getDateFormatter(parameter.getValue(), locale);
        dateFormat.setLenient(true);        
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));

		binder.registerCustomEditor(Constituency.class, new BaseEditor(
				new Constituency()));
		
		binder.registerCustomEditor(House.class, new BaseEditor(new House()));
		
		binder.registerCustomEditor(MemberRole.class, new BaseEditor(
				new MemberRole()));
		
		binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));
	}
}
