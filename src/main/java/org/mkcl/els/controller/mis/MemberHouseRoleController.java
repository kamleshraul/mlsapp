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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.User;
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
        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
        model.addAttribute("gridId", grid.getId());
        model.addAttribute("urlPattern", urlPattern);
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
    protected void populateNew(final ModelMap model,
            final HouseMemberRoleAssociation domain, final String locale,
            final HttpServletRequest request) {
        domain.setIsSitting(true);
        String houseType = this.getCurrentUser().getHouseType();
        model.addAttribute("houseType",houseType);
        List<House> houses=House.findByHouseType(houseType,locale.toString());
        model.addAttribute("houses",houses);
        List<MemberRole> memberRoles=MemberRole.findByHouseType(houseType,locale.toString());
        model.addAttribute("roles",memberRoles);
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
        //since we are arranging the houses by formation date in descending order so houses[0] will
        //refer to the current house provided info for current house is set.
//        if(!houses.isEmpty()){
//        domain.setFromDate(houses.get(0).getFirstDate());
//        domain.setToDate(houses.get(0).getLastDate());
//        }
        String defaultState=((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DEFAULT_STATE",locale.toString())).getValue();
        model.addAttribute("constituencies",Constituency.findVOByDefaultStateAndHouseType(defaultState, houseType, locale.toString(), "name",ApplicationConstants.ASC));
        // setting the value of the recordIndex field
        int index = HouseMemberRoleAssociation.findHighestRecordIndex(member);
        domain.setRecordIndex(index + 1);
        domain.setLocale(locale.toString());
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
    	String houseType = this.getCurrentUser().getHouseType();
        model.addAttribute("houseType",houseType);
        List<House> houses=House.findByHouseType(houseType,locale.toString());
        model.addAttribute("houses",houses);
        List<MemberRole> memberRoles=MemberRole.findByHouseType(houseType,locale.toString());
        model.addAttribute("roles",memberRoles);
        Long member = (Long) request.getSession()
                .getAttribute("member");
        request.getSession().removeAttribute("member");
        model.addAttribute("member", member);
        //since we are arranging the houses by formation date in descending order so houses[0] will
        //refer to the current house provided info for current house is set.
//        if(!houses.isEmpty()) {
//            domain.setFromDate(houses.get(0).getFirstDate());
//            domain.setToDate(houses.get(0).getLastDate());
//
//        }
        String defaultState=((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DEFAULT_STATE",locale.toString())).getValue();
        model.addAttribute("constituencies",Constituency.findVOByDefaultStateAndHouseType(defaultState, houseType, locale.toString(), "name",ApplicationConstants.ASC));
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
        populateNew(model, domain,locale,request);
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
        if (domain.isDuplicate()) {
            Object[] params = new Object[4];
            params[0] = domain.getHouse().getName();
            params[1] = domain.getRole().getName();
            params[2] = domain.getFromDate();
            params[3] = domain.getToDate();
            errors.rejectValue("recordIndex", "Duplicate", params,
                    "Entry with House:" + params[0] + ",Role:" + params[1]
                            + "From Date:" + params[2] + ",To Date:"
                            + params[3] + " already exists");
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
    	if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
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
        request.getSession().setAttribute("member",
                Long.parseLong(request.getParameter("member")));
        Member member=domain.getMember();
        if(domain.getIsSitting()==true){
  		  Credential credential1=null;
  		  if(member.getContact()!=null)
  			  if(! member.getContact().getEmail1().isEmpty())
  				  credential1=Credential.findByFieldName(Credential.class, "username", member.getContact().getEmail1(),null);
  		  if(credential1==null){
  			if(! member.getContact().getEmail1().isEmpty()){
  				credential1=new Credential();
  				credential1.setUsername(member.getContact().getEmail1());
  				SecureRandom random = new SecureRandom();  
  				String str = new BigInteger(60, random).toString(32);
  				credential1.setPassword(str);
  				credential1.setEnabled(false);
  				credential1.setEmail(member.getContact().getEmail1());
  				credential1.persist();
  			}
  		 }else{
  			 credential1.setUsername(member.getContact().getEmail1());
  			 credential1.setEmail(member.getContact().getEmail1());
  			 credential1.merge();
  		 }
  		  	User user1=User.findById(User.class, domain.getMember().getId());
  		  	if(user1==null){
  		  		if(! member.getContact().getEmail1().isEmpty()){
  		  			User user=new User();
  		  			user.setFirstName(member.getFirstName());
  		  			user.setMiddleName(member.getMiddleName());
  		  			user.setLastName(member.getLastName());
  		  			user.setTitle(member.getTitle().getName());
  		  			user.setCredential(credential1);
  		  			user.setLocale(domain.getLocale());    		  			
  		  			user.persist();
  		  			User.assignMemberId(domain.getMember().getId(), user.getId());
  		  		}
  		   	}
      }
  		  
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
    	request.getSession().setAttribute("member",
                Long.parseLong(request.getParameter("member")));
    	 Member member=domain.getMember();
    	 if(domain.getIsSitting()==true){
     		  Credential credential1=null;
     		  if(member.getContact()!=null)
     			  if(!member.getContact().getEmail1().isEmpty())
     				  credential1=Credential.findByFieldName(Credential.class, "username", member.getContact().getEmail1(),null);
     		  if(credential1==null){
     			if(! member.getContact().getEmail1().isEmpty()){
     				credential1=new Credential();
     				credential1.setUsername(member.getContact().getEmail1());
     				SecureRandom random = new SecureRandom();  
     				String str = new BigInteger(60, random).toString(32);
     				credential1.setPassword(str);
     				credential1.setEnabled(false);
     				credential1.setEmail(member.getContact().getEmail1());
     				credential1.persist();
     			}
     		 }else{
     			 credential1.setUsername(member.getContact().getEmail1());
     			 credential1.setEmail(member.getContact().getEmail1());
     			 credential1.merge();
     		 }
     		  	User user1=User.findById(User.class, domain.getMember().getId());
     		  	if(user1==null){
     		  		if(! member.getContact().getEmail1().isEmpty()){
     		  			User user=new User();
     		  			user.setFirstName(member.getFirstName());
     		  			user.setMiddleName(member.getMiddleName());
     		  			user.setLastName(member.getLastName());
     		  			user.setTitle(member.getTitle().getName());
     		  			user.setCredential(credential1);
     		  			user.setLocale(domain.getLocale());    		  			
     		  			user.persist();
     		  			User.assignMemberId(domain.getMember().getId(), user.getId());
     		  		}
     		   	}
         }
     		  
    	
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

        binder.registerCustomEditor(Constituency.class, new BaseEditor(
                new Constituency()));
        binder.registerCustomEditor(House.class, new BaseEditor(new House()));
        binder.registerCustomEditor(MemberRole.class, new BaseEditor(
                new MemberRole()));
        binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));
    }
}
