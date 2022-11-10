/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberPartyController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

// TODO: Auto-generated Javadoc
/**
 * The Class MemberPartyController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/party")
public class MemberPartyController extends BaseController{

    private static final Logger logger = LoggerFactory
    .getLogger(MemberPartyController.class);
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
        Long member = Long.parseLong(request.getParameter("member"));
		populateNames(model,request,locale.toString(),member);
        Grid grid;
		try {
			grid = Grid.findByDetailView(urlPattern, locale.toString());
			model.addAttribute("gridId", grid.getId());
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
        
        return "member/party/list";
    }
    
	/**
	 * Populate edit.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 */
	private void populateNames(final ModelMap model,final HttpServletRequest request, final String locale, final Long member) {
		Member selectedMember=Member.findById(Member.class,member);
		model.addAttribute("fullname", selectedMember.getFullname());
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
        MemberPartyAssociation domain = new MemberPartyAssociation();
        populateNew(model, domain, locale, request);
        model.addAttribute("domain", domain);
        //THIS IS USED TO REMOVE THE BUG WHERE IN RECORD UPDATED MESSAGE
        //APPEARS WHEN CLICKED ON NEW REOCRD
        model.addAttribute("type", "");
        return "member/party/new";
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
        MemberPartyAssociation domain = MemberPartyAssociation
                .findByMemberIdAndId(member, recordIndex);
        populateEdit(model, domain, request, locale);
        model.addAttribute("domain", domain);
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
          model.addAttribute("type","success");
          request.getSession().removeAttribute("type");
        }
        return "member/party/edit";
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
            @Valid @ModelAttribute("domain") final MemberPartyAssociation domain,
            final BindingResult result) {
        validateCreate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            model.addAttribute("type","error");
            poulateCreateIfErrors(model, domain, request, locale);
            return "member/party/new";
        }
        populateCreateIfNoErrors(model, domain, request);
        domain.persist();
        request.getSession().setAttribute("refresh", "");
        redirectAttributes.addFlashAttribute("type", "success");
        request.getSession().setAttribute("type", "success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/member/party/"
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
            final @Valid @ModelAttribute("domain") MemberPartyAssociation domain,
            final BindingResult result, final ModelMap model,
            final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Locale locale) {
        validateUpdate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            model.addAttribute("type","error");
            poulateUpdateIfErrors(model, domain, request, locale);
            return "member/party/edit";
        }
        populateUpdateIfNoErrors(model, domain, request);
        domain.merge();
        redirectAttributes.addFlashAttribute("type", "success");
        request.getSession().setAttribute("type", "success");
        redirectAttributes.addFlashAttribute("msg", "update_success");
        String returnUrl = "redirect:/member/party/"
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
        MemberPartyAssociation association = MemberPartyAssociation
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
    private void populateNew(final ModelMap model,
            final MemberPartyAssociation domain, final Locale locale,
            final HttpServletRequest request) {
        //this will be used to display the image of assembly/council
        String houseType=request.getParameter("houseType");
        model.addAttribute("houseType",houseType);
        //populating house types
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "name",ApplicationConstants.ASC, locale.toString());
        model.addAttribute("houseTypes",houseTypes);
        //populating houses.initially all display will be on the basis of selected house type
        //but user can change the house type as per requirement.
        List<House> houses;
		try {
			houses = House.findByHouseType(houseType,locale.toString());
			model.addAttribute("houses",houses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
       
        //locale is set
        domain.setLocale(locale.toString());
        //parties are populated sorted by name
        model.addAttribute("parties", Party.findAll(Party.class, "name",
                ApplicationConstants.ASC, locale.toString()));
        //member is added to model
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member",
                Long.parseLong(request.getParameter("member")));
        //full name will be displayed in the title of the page
        Member selectedMember=Member.findById(Member.class,member);
        model.addAttribute("fullname", selectedMember.getFullname());
        //index value is set to 0 or latest value in db+1
        int index = MemberPartyAssociation.findHighestRecordIndex(member);
        domain.setRecordIndex(index + 1);
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
            final MemberPartyAssociation domain,
            final HttpServletRequest request, final Locale locale) {
        //adding member to model.If its a request arising by double clicking /clicking edit link then
        //member will be read from request.else if it is by redirection from post/put it will be read from
        //session.
        Long member=(long)0;
        if(request.getParameter("member")!=null){
            member= Long.parseLong(request.getParameter("member"));
        }else{
        	member=(Long) request.getSession()
            .getAttribute("member");
            request.getSession().removeAttribute("member");
        }
        model.addAttribute("member",member);
        //this will be used to display the image of assembly/council
        String houseType=null;
        if(request.getParameter("houseType")!=null){
            houseType= request.getParameter("houseType");
        }else{
            houseType=(String) request.getSession()
            .getAttribute("houseType");
            request.getSession().removeAttribute("houseType");
        }
        model.addAttribute("houseType",houseType);
        //adding house information
        if(domain.getHouse()!=null){
        House house=domain.getHouse();
        model.addAttribute("houseName",house.getDisplayName());
        model.addAttribute("houseId",house.getId());
        }
        //populating full name to be displayed as title
        Member selectedMember=Member.findById(Member.class,member);
        model.addAttribute("fullname", selectedMember.getFullname());
        //populating party and party name as now party will not be a select box but a text box
        Party selectedParty=domain.getParty();
        model.addAttribute("party",selectedParty.getId());
        model.addAttribute("partyName",selectedParty.getName());
        //parties are populated sorted by name
        model.addAttribute("parties", Party.findAll(Party.class, "name",
                ApplicationConstants.ASC, locale.toString()));
    }

    /**
     * Validate create.
     *
     * @param domain the domain
     * @param errors the errors
     * @param request the request
     */
    private void validateCreate(final MemberPartyAssociation domain, final Errors errors,
            final HttpServletRequest request) {
        //checking for duplicate entry
    	
    
        if (domain.isDuplicate()) {
            Object[] params = new Object[3];
            params[0] = domain.getParty().getName();
            if(domain.getFromDate()==null){
                params[1]="";
            }else{
                params[1] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getFromDate());
            }
            if(domain.getToDate()==null){
                params[2]="";
            }else{
                params[2] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getToDate());
            }
            if(((String)params[1]).isEmpty()&&((String)params[2]).isEmpty()){
                errors.rejectValue("version", "PARTYDUPLICATEFRMTODEFAULT", params,
                        "Party:" + params[0] +" already exists");
            }else if(((String)params[1]).isEmpty()){
                errors.rejectValue("version", "PARTYDUPLICATETODEFAULT", params,
                        "Party:" + params[0] + ", From Date:" + params[1]
                                +  " already exists");
            }else if(((String)params[2]).isEmpty()){
                errors.rejectValue("version", "PARTYDUPLICATEFRMDEFAULT", params,
                        "Party:" + params[0] +", To Date:" + params[2] + " already exists");
            }else{
            errors.rejectValue("version", "PARTYDUPLICATE", params,
                    "Party:" + params[0] + ", From Date:" + params[1]
                            + ", To Date:" + params[2] + " already exists");
            }
        }
    }

    /**
     * Validate update.
     *
     * @param domain the domain
     * @param errors the errors
     * @param request the request
     */
    private void validateUpdate(final MemberPartyAssociation domain, final Errors errors,
            final HttpServletRequest request) {
        //checking for version mismatch
    	if (domain.isVersionMismatch()) {
            errors.rejectValue("VersionMismatch", "version");
        }
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
            final MemberPartyAssociation domain, final HttpServletRequest request,
            final Locale locale) {
        //if there is validation error
        populateNew(model, domain, locale, request);
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
            final MemberPartyAssociation domain, final HttpServletRequest request,
            final Locale locale) {
        //if there is validation error
        populateEdit(model, domain, request, locale);
    }

    /**
     * Populate update if no errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    private void populateUpdateIfNoErrors(final ModelMap model,
            final MemberPartyAssociation domain, final HttpServletRequest request) {
        //member is added to session so as to allow for redirection
    	if(domain.getFromDate()==null){
    		domain.setFromDate(new Date());
    	}
    	if(domain.getToDate()==null){
    		domain.setToDate(new Date());
    	}
        request.getSession().setAttribute("member",request.getParameter("member"));
        request.getSession().setAttribute("houseType",request.getParameter("houseType"));
    }

    private void populateCreateIfNoErrors(final ModelMap model,
            final MemberPartyAssociation domain, final HttpServletRequest request) {
        //member is added to session so as to allow for redirection
    	if(domain.getFromDate()==null){
    		domain.setFromDate(new Date());
    	}
    	if(domain.getToDate()==null){
    		domain.setToDate(new Date());
    	}
        request.getSession().setAttribute("member",request.getParameter("member"));
        request.getSession().setAttribute("houseType",request.getParameter("houseType"));
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
        
        binder.registerCustomEditor(Party.class, new BaseEditor(
                new Party()));
        
        binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));
        
        binder.registerCustomEditor(House.class, new BaseEditor(new House()));
    }
}
