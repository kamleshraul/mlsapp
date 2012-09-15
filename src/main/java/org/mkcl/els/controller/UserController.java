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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.PartialUpdate;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// TODO: Auto-generated Javadoc
/**
 * The Class UserController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/user")
public class UserController extends GenericController<User>{

	

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void customValidateCreate(final User domain,
            final BindingResult result, final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
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
		  * @see org.mkcl.els.controller.GenericController#populateCreateIfNoErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
		  */
		 @Override
 	  protected void populateCreateIfNoErrors(final ModelMap model,
 	            final User domain, final HttpServletRequest request) {
 		Credential existingCredential=Credential.findByFieldName(Credential.class, "email", request.getParameter("email"), null);
 	    if(existingCredential!=null){
 	    	domain.setCredential(existingCredential);
 	    }
 	    else
 	    {
 	    	  Credential credential=new Credential();
 	 	      credential.setEmail(request.getParameter("email"));
 	 	      credential.setUsername(request.getParameter("email"));
 	 	      credential.setEnabled(Boolean.parseBoolean(request.getParameter("isEnabled")));
 	 	      credential.setLocale(null);
 	 	      SecureRandom random = new SecureRandom();  
 	 	      String str = new BigInteger(60, random).toString(32);
 			  credential.setPassword(str);
 			  credential.persist();
 			  domain.setCredential(credential);
	
 	    }
 		  
 	    }
 		
 		
		 /* (non-Javadoc)
 		 * @see org.mkcl.els.controller.GenericController#populateUpdateIfNoErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
 		 */
 		@Override
 	 	  protected void populateUpdateIfNoErrors(final ModelMap model,
 	 	            final User domain, final HttpServletRequest request) {
 			User user= User.findById(User.class, domain.getId());
 			domain.setCredential(user.getCredential());
 			if(!domain.getCredential().getEmail().equals(request.getParameter("email"))){
 				domain.getCredential().setEmail(request.getParameter("email"));
 				domain.getCredential().setUsername(request.getParameter("email"));
 			}
 			if(!domain.getCredential().isEnabled()==Boolean.parseBoolean(request.getParameter("isEnabled"))) {
 				domain.getCredential().setEnabled(Boolean.parseBoolean(request.getParameter("isEnabled")));
 			}
 			
 		}
 		
 		
 		/**
		  * Populate role.
		  *
		  * @param model the model
		  * @param domain the domain
		  * @param request the request
		  * @param Locale the locale
		  */
		 @RequestMapping(value="/role",method=RequestMethod.GET)
		protected void populateRole(final ModelMap model, final User domain,
				final HttpServletRequest request, final Locale locale) {
			User user=User.findById(User.class, Long.parseLong(request.getParameter("user")));
			List<Role> roles=Role.findAll(Role.class, "name", "desc",locale.toString());
			model.addAttribute("roles",roles);
			model.addAttribute("domain", user);
			if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
	        }else{
	            request.getSession().removeAttribute("type");
	        }
		}
	
	/**
	 * Update role.
	 *
	 * @param model the model
	 * @param redirectAttributes the redirect attributes
	 * @param domain the domain
	 * @param request the request
	 * @param Locale the locale
	 * @return the string
	 * @author compaq
	 * @since v1.0.0
	 */
	@RequestMapping(value="/role",method=RequestMethod.PUT)
		protected String UpdateRole(final ModelMap model,
				final RedirectAttributes redirectAttributes, final @Valid @ModelAttribute("domain") User domain,
				final HttpServletRequest request, final String Locale){
				
			final String servletPath = request.getServletPath().replaceFirst("\\/","");
	        String messagePattern=servletPath.replaceAll("\\/",".");
	        model.addAttribute("messagePattern", messagePattern);
	        model.addAttribute("urlPattern", servletPath);
			model.addAttribute("domain", domain);
			User user=User.findById(User.class, domain.getId());
			domain.getCredential().setUserGroups(user.getCredential().getUserGroups());
			domain.getCredential().setLastLoginTime(user.getCredential().getLastLoginTime());
			String[] roleTypes=request.getParameterValues("roles");
			if(roleTypes!=null){		
			Set<Role> roles= new HashSet<Role>();
			for(int i=0;i<roleTypes.length;i++){
				List<Role> rolesByType= Role.findRolesByRoleType(Role.class, "type", roleTypes[i], "name", "desc");
				roles.addAll(rolesByType);
			}
			domain.getCredential().setRoles(roles);
			}
			domain.merge();
			redirectAttributes.addFlashAttribute("type", "success");
	        request.getSession().setAttribute("type","success");
	        redirectAttributes.addFlashAttribute("msg", "update_success");
	        String returnUrl = "redirect:/" + servletPath+"?userId="+domain.getId(); 
	        return returnUrl;
			
		}
	
	/**
	 * Populate user group.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 * @param Locale the locale
	 */
	@RequestMapping(value="/usergroup",method=RequestMethod.GET)
	protected void populateUserGroup(final ModelMap model, final User domain,
			final HttpServletRequest request, final String Locale) {
		User user=User.findById(User.class, Long.parseLong(request.getParameter("userId")));
		List<UserGroup> userGroups=UserGroup.findAll(UserGroup.class, "name", "desc", user.getLocale());
		model.addAttribute("userGroups",userGroups);
		model.addAttribute("domain", user);
		if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
            request.getSession().removeAttribute("type");
        }
	}


/**
 * Update user group.
 *
 * @param model the model
 * @param redirectAttributes the redirect attributes
 * @param domain the domain
 * @param request the request
 * @param Locale the locale
 * @return the string
 * @author compaq
 * @since v1.0.0
 */
@RequestMapping(value="/usergroup",method=RequestMethod.PUT)
	protected String UpdateUserGroup(final ModelMap model,final RedirectAttributes redirectAttributes, final  @Valid @ModelAttribute("domain") User domain,
			final HttpServletRequest request, final String Locale){
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String messagePattern=servletPath.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", servletPath);
        model.addAttribute("domain", domain);
        User user=User.findById(User.class, domain.getId());
		domain.getCredential().setRoles(user.getCredential().getRoles());
		domain.getCredential().setLastLoginTime(user.getCredential().getLastLoginTime());
		domain.merge();
		redirectAttributes.addFlashAttribute("type", "success");
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "update_success");
        String returnUrl = "redirect:/" + servletPath+"?userId="+domain.getId(); 
        return returnUrl;
		
	}
 		
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customInitBinderSuperClass(java.lang.Class, org.springframework.web.bind.WebDataBinder)
	 */
	@Override	
    protected <E extends BaseDomain> void customInitBinderSuperClass(
	            final Class clazz, final WebDataBinder binder) {
		         binder.registerCustomEditor(Set.class,"credential.roles",
						new CustomCollectionEditor(Set.class) {
					@Override
					protected Object convertElement(
							final Object element) {
						String id = null;
						if (element instanceof String) {
							id = (String) element;
						}
						return id != null ? BaseDomain
								.findById(Role.class,
										Long.valueOf(id))
										: null;
					}
				});
		   
	 
		       binder.registerCustomEditor(Set.class,"credential.userGroups",
				new CustomCollectionEditor(Set.class) {
		    	   @Override
		    	   protected Object convertElement(
						final Object element) {
			    		   String id = null;
			    		   if (element instanceof String) {
			    			   	id = (String) element;
			    		   }
			    		   return id != null ? BaseDomain
							.findById(UserGroup.class,
									Long.valueOf(id))
									: null;
				}
		       });
		       
		 }
	
}

 		
 		
 		
 		
 		
 		

