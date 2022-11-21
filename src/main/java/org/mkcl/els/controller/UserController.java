/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.UserController.java
 * Created On: May 19, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.IProcessService;
import org.mkcl.els.service.ISecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// TODO: Auto-generated Javadoc

/**
 * The Class UserController.
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/user")
public class UserController extends GenericController<User>{
	
	@Autowired
	private IProcessService processService;
	
	@Autowired 
	private ISecurityService securityService;

	@Override
	protected void populateNew(final ModelMap model, final User domain, final String locale,
			final HttpServletRequest request) {
		
		List<Language> languages = new ArrayList<Language>();
		
		try{
			domain.setLocale(locale);
			List<HouseType> houseTypes=HouseType.findAllNoExclude("type",ApplicationConstants.ASC, locale);
			model.addAttribute("houseTypes",houseTypes);
			List<Title> titles=Title.findAll(Title.class,"name",ApplicationConstants.ASC, locale);
			model.addAttribute("titles",titles);
			List<Role> roles=Role.findAll(Role.class, "name", "desc",locale.toString());
			model.addAttribute("roles",roles);
			List<MenuItem> menus=MenuItem.findAll(MenuItem.class,"text",ApplicationConstants.ASC, locale.toString());
			model.addAttribute("menus",menus);
			languages = new ArrayList<Language>();
			languages = Language.findAllLanguagesByModule("RIS",locale);
			
			model.addAttribute("default_email_hostname",ApplicationConstants.DEFAULT_EMAIL_HOSTNAME);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
		model.addAttribute("languages",languages);
		model.addAttribute("selectedLanguage",domain.getLanguage());
	}
	@Override
	protected void populateEdit(final ModelMap model, final User domain,
			final HttpServletRequest request) {
		List<Language> languages = new ArrayList<Language>();
		try{
			List<HouseType> houseTypes=HouseType.findAllNoExclude("type",ApplicationConstants.ASC, domain.getLocale());
			model.addAttribute("houseTypes",houseTypes);
			List<Title> titles=Title.findAll(Title.class,"name",ApplicationConstants.ASC, domain.getLocale());
			model.addAttribute("titles",titles);
			List<Role> roles=Role.findAll(Role.class, "name", "desc",domain.getLocale());
			model.addAttribute("roles",roles);
			StringBuffer buffer=new StringBuffer();
			Credential credential=domain.getCredential();
			if(credential!=null){
			model.addAttribute("username",credential.getUsername());
			for(Role i:credential.getRoles()){
				buffer.append(i.getId()+",");
			}
			if(!buffer.toString().isEmpty()){
			buffer.deleteCharAt(buffer.length()-1);
			}
			model.addAttribute("selectedRoles",buffer.toString());
			model.addAttribute("isEnabled",credential.isEnabled());
			}
			List<MenuItem> menus=MenuItem.findAll(MenuItem.class,"text",ApplicationConstants.ASC, domain.getLocale());
			model.addAttribute("menus",menus);
			languages = new ArrayList<Language>();
			languages = Language.findAllLanguagesByModule("RIS",domain.getLocale());
			
			model.addAttribute("default_email_hostname",ApplicationConstants.DEFAULT_EMAIL_HOSTNAME);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());			
		}
		model.addAttribute("languages",languages);
		model.addAttribute("selectedLanguage",domain.getLanguage());
	}
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final User domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(final User domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}


	/**
	 * Custom validate.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void customValidate(final User domain,
			final BindingResult result, final HttpServletRequest request) {

	}


	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateCreateIfNoErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, 

javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model,
			final User domain, final HttpServletRequest request) {
		
		String username=request.getParameter("username");
		Credential credential = null;
		try{
			if(username!=null){
				credential=new Credential();
				credential.setEmail(request.getParameter("email"));
				credential.setUsername(username);
				credential.setEnabled(Boolean.parseBoolean(request.getParameter("isEnabled")));
				credential.setLocale(null);
				
				String defaultPassword = ApplicationConstants.DEFAULT_PASSWORD;
				
				CustomParameter csptUserDefaultPassword = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.USE_DEFAULT_PASSWORD, "");
				if(csptUserDefaultPassword != null){
					if(csptUserDefaultPassword.getValue() != null && !csptUserDefaultPassword.getValue().isEmpty()){
						if(csptUserDefaultPassword.getValue().equals("no")){
							defaultPassword = Credential.generatePassword(Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
						}
					}
				}
					
				String encodedPassword = securityService.getEncodedPassword(defaultPassword);
				credential.setPassword(encodedPassword);
				credential.setPasswordChangeCount(1);
				credential.setPasswordChangeDateTime(new Date());
				String[] selectedRoles=request.getParameterValues("roles");
				Set<Role> roles=new HashSet<Role>();
				if(selectedRoles!=null){
				for(String i:selectedRoles){
					Role role=Role.findById(Role.class, Long.parseLong(i));
					roles.add(role);
				}
				credential.setRoles(roles);
				}
				credential.persist();
				domain.setCredential(credential);
				String[] languagesArr=request.getParameterValues("languages");
				if(languagesArr!=null&&(languagesArr.length>0)){
					StringBuffer buffer=new StringBuffer();
					for(String i:languagesArr){
						buffer.append(i+",");
					}
					buffer.deleteCharAt(buffer.length()-1);
					domain.setLanguage(buffer.toString());
				}			
				processService.createUser(domain);
			}
		}catch (Exception e) {
			if(e instanceof RuntimeException){
				credential.remove();
			}
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complatye successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}
	
	
	


	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateUpdateIfNoErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, 

javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model,
			final User domain, final HttpServletRequest request) {
		User user= User.findById(User.class, domain.getId());
		Credential oldcredential=user.getCredential();
		String username=request.getParameter("username");
		String email=request.getParameter("email");
		Boolean isEnabled=Boolean.parseBoolean(request.getParameter("isEnabled"));
		if(oldcredential==null){
			if(username!=null){
				Credential credential=new Credential();
				credential.setEmail(email); 	 	    	  
				credential.setUsername(username);
				String defaultPassword = ApplicationConstants.DEFAULT_PASSWORD;
				CustomParameter csptUserDefaultPassword = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.USE_DEFAULT_PASSWORD, "");
				if(csptUserDefaultPassword != null){
					if(csptUserDefaultPassword.getValue() != null && !csptUserDefaultPassword.getValue().isEmpty()){
						if(csptUserDefaultPassword.getValue().equals("no")){
							defaultPassword = Credential.generatePassword(Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
						}
					}
				}
				String encodedPassword = securityService.getEncodedPassword(defaultPassword);
				credential.setPassword(encodedPassword);
				credential.setPasswordChangeCount(1);
				credential.setPasswordChangeDateTime(new Date());
				credential.setEnabled(isEnabled); 	 	 	      
				credential.setLocale(null);				
				String[] selectedRoles=request.getParameterValues("roles");
				Set<Role> roles=new HashSet<Role>();
				for(String i:selectedRoles){
					Role role=Role.findById(Role.class, Long.parseLong(i));
					roles.add(role);
				}
				credential.setRoles(roles);
				credential.persist();
				domain.setCredential(credential);
				processService.createUser(domain);
			}
		}else{
			if(oldcredential.getUsername()!=username){
				processService.deleteUser(user);
			}
			oldcredential.setEmail(email); 	 	    	  
			oldcredential.setUsername(username);			
			oldcredential.setEnabled(isEnabled);
			String[] selectedRoles=request.getParameterValues("roles");
			Set<Role> roles=new HashSet<Role>();
			if(selectedRoles!=null){
			for(String i:selectedRoles){
				Role role=Role.findById(Role.class, Long.parseLong(i));
				roles.add(role);
			}
			oldcredential.setRoles(roles);
			}
			oldcredential.merge();
			domain.setCredential(oldcredential);
			processService.updateUser(domain);
		} 	
		
		String[] languagesArr=request.getParameterValues("languages");
		if(languagesArr!=null&&(languagesArr.length>0)){
			StringBuffer buffer=new StringBuffer();
			for(String i:languagesArr){
				buffer.append(i+",");
			}
			buffer.deleteCharAt(buffer.length()-1);
			domain.setLanguage(buffer.toString());
		}			
	}	
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public String resetPasswordInit(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        /** username **/
        String username = request.getParameter("username");
    	User user = null;
    	try {
			user = User.findByUserName(username, locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
			//error
	
			request.getSession().setAttribute("type","error");
	      	
		}
    	
    	model.addAttribute("userFirstName", user.findFirstLastName());
    	model.addAttribute("username", username);
        if(username==null || username.isEmpty()) {
        	username = (String) request.getSession().getAttribute("selectedUsername"); 
        	if(username!=null) {
        		request.getSession().removeAttribute("selectedUsername");
        	}
        }
        if(username!=null && !username.isEmpty()) {
        	model.addAttribute("username", username);
            /** new password **/
            if(request.getSession().getAttribute("newPassword")==null){
                model.addAttribute("newPassword",ApplicationConstants.DEFAULT_PASSWORD);
            }else{
            	model.addAttribute("newPassword",request.getSession().getAttribute("newPassword"));
                request.getSession().removeAttribute("newPassword");
            }
            /** confirmed password **/
            if(request.getSession().getAttribute("confirmedPassword")==null){
                model.addAttribute("confirmedPassword",ApplicationConstants.DEFAULT_PASSWORD);
            }else{
            	model.addAttribute("confirmedPassword",request.getSession().getAttribute("confirmedPassword"));
                request.getSession().removeAttribute("confirmedPassword");
            }
            //this is done so as to remove the bug due to which update message appears even though there
            //is a fresh request
            if(request.getSession().getAttribute("type")==null){
                model.addAttribute("type","");
            }else{
            	model.addAttribute("type",request.getSession().getAttribute("type"));
                request.getSession().removeAttribute("type");
            }
        } else {
        	model.addAttribute("errorcode", "USERNAME_NOTGIVEN");
        }        
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("password","error");
        }else{
            return servletPath;
        }
    }
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public String resetPasswordUpdate(final ModelMap model, 
    		final HttpServletRequest request,
    		final RedirectAttributes redirectAttributes,
            final Locale locale) {
		String username = request.getParameter("username");
		String newPassword = request.getParameter("newPassword");
		String confirmedPassword = request.getParameter("confirmedPassword");
		if(username!=null && !username.isEmpty() && newPassword!=null && !newPassword.isEmpty() 
				&& confirmedPassword!=null && !confirmedPassword.isEmpty() 
				&& newPassword.equals(confirmedPassword)) {
			User user = null;
			try {
				user = User.findByUserName(username, locale.toString());
			} catch (ELSException e) {
				e.printStackTrace();
				//error
				redirectAttributes.addFlashAttribute("type", "error");
				request.getSession().setAttribute("type","error");
		        redirectAttributes.addFlashAttribute("msg", "update_error");	
			}
			model.addAttribute("userFirstName", user.findFirstLastName());
	    	model.addAttribute("username", username);
	    	
			if(user!=null && user.getId()!=null) {
				Credential credential = user.getCredential();
				if(credential!=null) {
					String encodedPassword = securityService.getEncodedPassword(newPassword);
					credential.setPassword(encodedPassword);
					credential.setPasswordChangeCount(1);
					credential.setPasswordChangeDateTime(new Date());
					
					credential.merge();						
					redirectAttributes.addFlashAttribute("type", "success");
			        //this is done so as to remove the bug due to which update message appears even though there
			        //is a fresh request
			        request.getSession().setAttribute("type","success");
			        redirectAttributes.addFlashAttribute("msg", "update_success");
				}
			}
		} else {
			//error
			redirectAttributes.addFlashAttribute("type", "error");
			request.getSession().setAttribute("type","error");
	        redirectAttributes.addFlashAttribute("msg", "update_error");
		} 
		request.getSession().setAttribute("selectedUsername", username);
		request.getSession().setAttribute("newPassword", newPassword);	
		request.getSession().setAttribute("confirmedPassword", confirmedPassword);
        String returnUrl = "redirect:/" + request.getServletPath().replaceFirst("\\/","");
        return returnUrl;
    }
	
	@RequestMapping(value = "/credential/updateAllowedForMultiLogin", method = RequestMethod.POST)
    public @ResponseBody int updateCredentialAllowedForMultiLogin(final HttpServletRequest request,final Locale locale) {
		int updateStatus = 0;
		String credentialId = request.getParameter("credentialId");
		if(credentialId!=null && !credentialId.isEmpty() && !credentialId.equals("false")) {
			try {
				Credential credential = Credential.findById(Credential.class, Long.parseLong(credentialId));
				if(credential!=null) {
					if(credential.isAllowedForMultiLogin()) {
						credential.setAllowedForMultiLogin(false);
					} else {
						credential.setAllowedForMultiLogin(true);
					}
					credential.merge();
					updateStatus = 1;
				}
			} catch(Exception e) {
				e.printStackTrace();
				updateStatus = 0;
			}			
		}
		return updateStatus;
	}
	
	@RequestMapping(value = "/sendNotification", method = RequestMethod.GET)
    public String sendNotificationInit(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        
        /** usernames **/
        String usernames = request.getParameter("usernames");
        if(usernames!=null && !usernames.isEmpty()) {
        	model.addAttribute("usernames", usernames);
        } else if(request.getSession().getAttribute("usernames")!=null){
        	model.addAttribute("usernames",request.getSession().getAttribute("usernames"));
            request.getSession().removeAttribute("usernames");
        } else {
        	model.addAttribute("usernames", "");
        }        
        
        /** is volatile notification **/
        if(request.getSession().getAttribute("isVolatile")!=null){
        	model.addAttribute("isVolatile",request.getSession().getAttribute("isVolatile"));
            request.getSession().removeAttribute("isVolatile");
        }else{        	
            if(usernames!=null && !usernames.isEmpty()) {
            	model.addAttribute("isVolatile", false);
            } else {
            	model.addAttribute("isVolatile", true);
            }
        }
        
        /** notification title **/
        if(request.getSession().getAttribute("notificationTitle")!=null){
        	model.addAttribute("notificationTitle",request.getSession().getAttribute("notificationTitle"));
            request.getSession().removeAttribute("notificationTitle");
        }
        
        /** notification message **/
        if(request.getSession().getAttribute("notificationMessage")!=null){
        	model.addAttribute("notificationMessage",request.getSession().getAttribute("notificationMessage"));
            request.getSession().removeAttribute("notificationMessage");
        }
        
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh request
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
        	model.addAttribute("type",request.getSession().getAttribute("type"));
            request.getSession().removeAttribute("type");
        }
        
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("sendNotification","error");
        }else{
            return servletPath;
        }
    }
	
	@RequestMapping(value = "/sendNotification", method = RequestMethod.POST)
    public String sendNotification(final ModelMap model, 
    		final HttpServletRequest request,
    		final RedirectAttributes redirectAttributes,
            final Locale locale) {
		String usernames = request.getParameter("usernames");
		String notificationTitle = request.getParameter("notificationTitle");
		String notificationMessage = request.getParameter("notificationMessage");
		String isVolatile = request.getParameter("isVolatile");
		if(usernames==null || usernames.isEmpty()) {
			usernames = "all";
		}
		if(notificationTitle!=null && !notificationTitle.isEmpty()
				&& isVolatile!=null && !isVolatile.isEmpty()) {
			try {
				NotificationController.sendNotificationFromAdminPage(notificationTitle, notificationMessage, Boolean.parseBoolean(isVolatile), usernames, locale.toString());
				
		        //this is done so as to remove the bug due to which update message appears even though there
		        //is a fresh request
		        request.getSession().setAttribute("type","success");
		        redirectAttributes.addFlashAttribute("msg", "update_success");
			} catch (Exception e) {
				e.printStackTrace();
				//error
				redirectAttributes.addFlashAttribute("type", "error");
				request.getSession().setAttribute("type","error");
		        redirectAttributes.addFlashAttribute("msg", "update_error");	
			}
		} else {
			//error
			redirectAttributes.addFlashAttribute("type", "error");
			request.getSession().setAttribute("type","error");
	        redirectAttributes.addFlashAttribute("msg", "update_error");
		} 
		request.getSession().setAttribute("usernames", usernames);
		request.getSession().setAttribute("isVolatile", isVolatile);
		request.getSession().setAttribute("notificationTitle", notificationTitle);	
		request.getSession().setAttribute("notificationMessage", notificationMessage);
        String returnUrl = "redirect:/" + request.getServletPath().replaceFirst("\\/","");
        return returnUrl;
    }
	
	@RequestMapping(value = "/resetHighSecurityPassword", method = RequestMethod.GET)
    public String resetHighSecurityPasswordInit(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        /** username **/
        String username = request.getParameter("username");
     	User user = null;
    	try {
			user = User.findByUserName(username, locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
			//error
	
			request.getSession().setAttribute("type","error");
	      	
		}
    	
    	model.addAttribute("userFirstName", user.findFirstLastName());
    	model.addAttribute("username", username);
        if(username==null || username.isEmpty()) {
        	username = (String) request.getSession().getAttribute("selectedUsername"); 
        	if(username!=null) {
        		request.getSession().removeAttribute("selectedUsername");
        	}
        }
        if(username!=null && !username.isEmpty()) {
        	model.addAttribute("username", username);
        	StringBuffer defaultHighSecurityPassword = new StringBuffer(ApplicationConstants.DEFAULT_HIGH_SECURITY_PASSWORD_INITIAL);
        	String currentDateMonth = FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT_DDMM);
        	defaultHighSecurityPassword.append(currentDateMonth);
            /** new password **/
            if(request.getSession().getAttribute("newHighSecurityPassword")==null){            	
                model.addAttribute("newHighSecurityPassword",defaultHighSecurityPassword.toString());
            }else{
            	model.addAttribute("newHighSecurityPassword",request.getSession().getAttribute("newHighSecurityPassword"));
                request.getSession().removeAttribute("newHighSecurityPassword");
            }
            /** confirmed password **/
            if(request.getSession().getAttribute("confirmedHighSecurityPassword")==null){
                model.addAttribute("confirmedHighSecurityPassword",defaultHighSecurityPassword.toString());
            }else{
            	model.addAttribute("confirmedHighSecurityPassword",request.getSession().getAttribute("confirmedHighSecurityPassword"));
                request.getSession().removeAttribute("confirmedHighSecurityPassword");
            }
            //this is done so as to remove the bug due to which update message appears even though there
            //is a fresh request
            if(request.getSession().getAttribute("type")==null){
                model.addAttribute("type","");
            }else{
            	model.addAttribute("type",request.getSession().getAttribute("type"));
                request.getSession().removeAttribute("type");
            }
        } else {
        	model.addAttribute("errorcode", "USERNAME_NOTGIVEN");
        }        
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("password","error");
        }else{
            return servletPath;
        }
    }
	
	@RequestMapping(value = "/resetHighSecurityPassword", method = RequestMethod.POST)
    public String resetHighSecurityPasswordUpdate(final ModelMap model, 
    		final HttpServletRequest request,
    		final RedirectAttributes redirectAttributes,
            final Locale locale) {
		String username = request.getParameter("username");
		String newHighSecurityPassword = request.getParameter("newHighSecurityPassword");
		String confirmedHighSecurityPassword = request.getParameter("confirmedHighSecurityPassword");
		if(username!=null && !username.isEmpty() && newHighSecurityPassword!=null && !newHighSecurityPassword.isEmpty() 
				&& confirmedHighSecurityPassword!=null && !confirmedHighSecurityPassword.isEmpty() 
				&& newHighSecurityPassword.equals(confirmedHighSecurityPassword)) {
			User user = null;
			try {
				user = User.findByUserName(username, locale.toString());
			} catch (ELSException e) {
				e.printStackTrace();
				//error
				redirectAttributes.addFlashAttribute("type", "error");
				request.getSession().setAttribute("type","error");
		        redirectAttributes.addFlashAttribute("msg", "update_error");	
			}
			model.addAttribute("userFirstName", user.findFirstLastName());
	    	model.addAttribute("username", username);
			if(user!=null && user.getId()!=null) {
				Credential credential = user.getCredential();
				if(credential!=null) {
					String encodedHighSecurityPassword = securityService.getEncodedPassword(newHighSecurityPassword);
					credential.setHighSecurityPassword(encodedHighSecurityPassword);
					//credential.setHighSecurityPasswordChangeCount(1);
					//credential.setHighSecurityPasswordChangeDateTime(new Date());
					
					credential.merge();						
					redirectAttributes.addFlashAttribute("type", "success");
			        //this is done so as to remove the bug due to which update message appears even though there
			        //is a fresh request
			        request.getSession().setAttribute("type","success");
			        redirectAttributes.addFlashAttribute("msg", "update_success");
				}
			}
		} else {
			//error
			redirectAttributes.addFlashAttribute("type", "error");
			request.getSession().setAttribute("type","error");
	        redirectAttributes.addFlashAttribute("msg", "update_error");
		} 
		request.getSession().setAttribute("selectedUsername", username);
		request.getSession().setAttribute("newHighSecurityPassword", newHighSecurityPassword);	
		request.getSession().setAttribute("confirmedHighSecurityPassword", confirmedHighSecurityPassword);
        String returnUrl = "redirect:/" + request.getServletPath().replaceFirst("\\/","");
        return returnUrl;
    }
}