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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
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
        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
        model.addAttribute("gridId", grid.getId());
        return "member/party/list";
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
            poulateCreateIfErrors(model, domain, request, locale);
            return "member/party/new";
        }
        domain.persist();
        request.getSession().setAttribute("refresh", "");
        redirectAttributes.addFlashAttribute("type", "success");
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
            poulateUpdateIfErrors(model, domain, request, locale);
            return "member/party/edit";
        }
        populateUpdateIfNoErrors(model, domain, request);
        domain.merge();
        redirectAttributes.addFlashAttribute("type", "success");
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
        domain.setLocale(locale.toString());
        model.addAttribute("parties", Party.findAll(Party.class, "name",
                ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member",
                Long.parseLong(request.getParameter("member")));
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
        model.addAttribute("parties", Party.findAll(Party.class, "name",
                ApplicationConstants.ASC, locale.toString()));
        //this is the case when u r editing a record by double clicking a row in grid.
        if(request.getParameter("member")!=null){
            model.addAttribute("member", Long.parseLong(request.getParameter("member")));
        }//this is the case when we create/update a record and there is a redirection
        else{
            model.addAttribute("member", request.getSession()
                    .getAttribute("member"));
            request.getSession().removeAttribute("member");
        }


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
        if (domain.isDuplicate()) {
            Object[] params = new Object[3];
            params[0] = domain.getParty().getName();
            params[1] = domain.getFromDate();
            params[2] = domain.getToDate();
            errors.rejectValue("version", "Duplicate", params,
                    "Entry with Party:" + params[0] + "From Date:" + params[1]
                            + ",To Date:" + params[2] + " already exists");
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

    }

    /**
     * Inits the binder.
     *
     * @param binder the binder
     */
    @SuppressWarnings("unused")
    @InitBinder(value = "domain")
    private void initBinder(final WebDataBinder binder) {
        CustomParameter parameter = CustomParameter.findByName(
                CustomParameter.class, "SERVER_DATEFORMAT", "");
        String locale=this.getUserLocale().toString();
        SimpleDateFormat dateFormat=null;
        if(locale!=null){
            if(locale.equals("mr_IN")){
                dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
            }else{
                dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("en","US"));
            }
        }
        dateFormat.setLenient(true);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));
        binder.registerCustomEditor(Party.class, new BaseEditor(
                new Party()));
        binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));
    }
}
