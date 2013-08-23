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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

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
				credential.setPassword("123");
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
				credential.setEnabled(isEnabled); 	 	 	      
				credential.setLocale(null);
				credential.setPassword("123");
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
}