/**
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberSuspensionController.java
 * Created On: Jan 24, 2015
 */
package org.mkcl.els.controller.mis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberSuspension;
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
 * @author rajeshs
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/suspension")
public class MemberSuspensionController extends BaseController {

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
		return "member/suspension/list";
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
		MemberSuspension domain = new MemberSuspension();
		populateNew(model, domain, locale.toString(), request);
		//THIS IS USED TO REMOVE THE BUG WHERE IN RECORD UPDATED MESSAGE
		//APPEARS WHEN CLICKED ON NEW REOCRD
		model.addAttribute("type", "");
		model.addAttribute("domain", domain);
		return "member/suspension/new";
	}
	/**
	 * Populate Model.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param locale the locale
	 * @param request the request
	 */
	protected void populateNew(final ModelMap model,
			final MemberSuspension domain, final String locale,
			final HttpServletRequest request) {
	
		Long member = Long.parseLong(request.getParameter("member"));
		model.addAttribute("member", member);
		domain.setLocale(locale);
		populateModel(model, domain, locale, request, member);
}
	/**
	 * Populate Model.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param locale the locale
	 * @param request the request
	 */

	private void  populateModel(final ModelMap model,
			final MemberSuspension domain, final String locale,final HttpServletRequest request,final Long member){
			Member selectedMember=Member.findById(Member.class,member);
			model.addAttribute("fullname", selectedMember.getFullname());
	}
	/**
	 * Edits the.
	 *
	 * @param Id the record index
	 * @param model the model
	 * @param request the request
	 * @param member the member
	 * @param locale the locale
	 * @return the string
	 */

	  @SuppressWarnings("unchecked")
	    @RequestMapping(value = "/{Id}/edit", method = RequestMethod.GET)
	    public String edit(final @PathVariable("Id") Long Id,
	            final ModelMap model, final HttpServletRequest request,
	            final @RequestParam("member") Long member, final Locale locale) {
	        MemberSuspension domain = MemberSuspension
	                .findByMemberIdAndId(member, Id);
	        populateEdit(model, domain, request, locale.toString());
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
	        return "member/suspension/edit";
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
				final MemberSuspension domain,
				final HttpServletRequest request, final String locale) {
			Long member=domain.getMember().getId();
			model.addAttribute("member",member);
			populateModel(model,domain,locale,request,member);
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
			@Valid @ModelAttribute("domain") final MemberSuspension domain,
			final BindingResult result) {
		validateCreate(domain, result, request);
		model.addAttribute("domain", domain);
		if (result.hasErrors()) {
			poulateCreateIfErrors(model, domain, request, locale.toString());
			model.addAttribute("type", "error");
			return "member/suspension/new";
		}
		populateCreateIfNoErrors(model, domain, request);
		domain.persist();
		request.getSession().setAttribute("refresh", "");
		redirectAttributes.addFlashAttribute("type", "success");
		request.getSession().setAttribute("type","success");
		redirectAttributes.addFlashAttribute("msg", "create_success");
		 String returnUrl = "redirect:/member/suspension/"
	                + domain.getId() + "/edit?member="
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
			final @Valid @ModelAttribute("domain") MemberSuspension domain,
			final BindingResult result, final ModelMap model,
			final RedirectAttributes redirectAttributes,
			final HttpServletRequest request, final Locale locale) {
		validateUpdate(domain, result, request);
		model.addAttribute("domain", domain);
		if (result.hasErrors()) {
			poulateUpdateIfErrors(model, domain, request, locale.toString());
			model.addAttribute("type", "error");
			return "member/suspension/edit";
		}
		populateUpdateIfNoErrors(model, domain, request);
		domain.merge();
		request.getSession().setAttribute("refresh", "");
		redirectAttributes.addFlashAttribute("type", "success");
		redirectAttributes.addFlashAttribute("msg", "update_success");
	    request.getSession().setAttribute("type","success");
	    String returnUrl = "redirect:/member/suspension/"
                + domain.getId() + "/edit?member="
                + request.getParameter("member");
        return returnUrl;
	}

	/**
	 * Delete.
	 *
	 * @param Id the record index
	 * @param member the member
	 * @param model the model
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value = "/{Id}/delete",
			method = RequestMethod.DELETE)
			public String delete(final @PathVariable("Id") Long Id,
					final @RequestParam("member") Long member, final ModelMap model,
					final HttpServletRequest request) {
	    MemberSuspension domain = MemberSuspension
                .findByMemberIdAndId(member, Id);

		if (domain.getMember() != null) {
			domain.remove();
		}
		return "info";
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
			final MemberSuspension domain, final HttpServletRequest request,
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
			final MemberSuspension domain, final HttpServletRequest request,
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
	private void validateCreate(final MemberSuspension domain,
			final Errors errors, final HttpServletRequest request) {
		 Object[] params = new Object[4];
    
       
         if(domain.getStartDateOfSuspension()==null){
             params[0]="";
         }else{
             params[0] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getStartDateOfSuspension());
         }
         if(domain.getEstimatedEndDateOfSuspension()==null){
             params[1]="";
         }else{
             params[1] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getEstimatedEndDateOfSuspension());
         }
         if(domain.getActualEndDateOfSuspension()==null){
             params[2]="";
         }else{
             params[2] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getActualEndDateOfSuspension());
         }
         params[3] = domain.getReasonOfSuspension();
         
         if(((String)params[0]).isEmpty()){
             errors.rejectValue("startDateOfSuspension","NotEmpty",
                     "Member Suspension start date can not be null");
         }else if(((String)params[1]).isEmpty()){
             errors.rejectValue("estimatedEndDateOfSuspension","NotEmpty","Member Suspension estimated end date can not be null");
         }else if(((String)params[3]).isEmpty()){
             errors.rejectValue("reasonOfSuspension","NotEmpty","Reason for Suspension can not be null");
         }else{
        	 Date startDateOfSuspension = domain.getStartDateOfSuspension();
          	 Date estimatedEndDateOfSuspension = domain.getEstimatedEndDateOfSuspension();
          	 Date actualEndDateOfSuspension = domain.getActualEndDateOfSuspension();
        	 if(estimatedEndDateOfSuspension.before(startDateOfSuspension)) 
        	 {
        		 errors.rejectValue("estimatedEndDateOfSuspension",
 						"EndDateBeforeStartDate", 
 				"Estimated End Date Of Suspension cannot be set prior to Start date od suspension");
 			 }
        	 if(actualEndDateOfSuspension!=null)
        	 {
        	 if(actualEndDateOfSuspension.before(startDateOfSuspension)||actualEndDateOfSuspension.after(estimatedEndDateOfSuspension))
        	 {
        		 errors.rejectValue("actualEndDateOfSuspension",
  						"ActualDateBetweenStartANDEndDate", 
  				"Actual End date cannot be set prior to Formation date or after Estimated end date");
        	 }
        	 }
         }
	
	}

	/**
	 * Validate update.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void validateUpdate(final MemberSuspension domain,
			final BindingResult result, final HttpServletRequest request) {
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		
		 Object[] params = new Object[4];
		    
	       
         if(domain.getStartDateOfSuspension()==null){
             params[0]="";
         }else{
             params[0] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getStartDateOfSuspension());
         }
         if(domain.getEstimatedEndDateOfSuspension()==null){
             params[1]="";
         }else{
             params[1] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getEstimatedEndDateOfSuspension());
         }
         if(domain.getActualEndDateOfSuspension()==null){
             params[2]="";
         }else{
             params[2] = FormaterUtil.getDateFormatter(domain.getLocale()).format(domain.getActualEndDateOfSuspension());
         }
         params[3] = domain.getReasonOfSuspension();
         
         if(((String)params[0]).isEmpty()){
        	 result.rejectValue("startDateOfSuspension","NotEmpty",
                     "Member Suspension start date can not be null");
         }else if(((String)params[1]).isEmpty()){
        	 result.rejectValue("estimatedEndDateOfSuspension","NotEmpty","Member Suspension estimated end date can not be null");
         }
         else if(((String)params[3]).isEmpty()){
        	 result.rejectValue("reasonOfSuspension","NotEmpty","Reason for Suspension can not be null");
         }else{
        	 Date startDateOfSuspension = domain.getStartDateOfSuspension();
          	 Date estimatedEndDateOfSuspension = domain.getEstimatedEndDateOfSuspension();
          	 Date actualEndDateOfSuspension = domain.getActualEndDateOfSuspension();
        	 if(estimatedEndDateOfSuspension.before(startDateOfSuspension)) 
        	 {
        		 result.rejectValue("estimatedEndDateOfSuspension",
 						"EndDateBeforeStartDate", 
 				"Estimated End Date Of Suspension cannot be set prior to Start date od suspension");
 			 }
        	 if(actualEndDateOfSuspension!=null)
        	 {
        	 if(actualEndDateOfSuspension.before(startDateOfSuspension)||actualEndDateOfSuspension.after(estimatedEndDateOfSuspension))
        	 {
        		 result.rejectValue("actualEndDateOfSuspension",
  						"ActualDateBetweenStartANDEndDate", 
  				"Actual End date cannot be set prior to Formation date or after Estimated end date");
        	 }
        	 }
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
			final MemberSuspension domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("member",
				Long.parseLong(request.getParameter("member")));
	}

	/**
	 * Populate update if no errors.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 */
	private void populateUpdateIfNoErrors(final ModelMap model,
			final MemberSuspension domain, final HttpServletRequest request) {
		request.getSession().setAttribute("member",
				Long.parseLong(request.getParameter("member")));
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
		
		SimpleDateFormat dateFormat = FormaterUtil.getDateFormatter(parameter.getValue(), locale);
        dateFormat.setLenient(true);        
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));
        
		binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));
	}
}
