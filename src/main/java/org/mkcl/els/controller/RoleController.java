/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.RoleController.java
 * Created On: May 11, 2012
 */
package org.mkcl.els.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.User;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The Class RoleController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/role")
public class RoleController extends GenericController<Role> {
	
	@RequestMapping(value="/user",method=RequestMethod.GET)
	protected void populateUser(final ModelMap model, final Role domain,
			final HttpServletRequest request, final Locale locale) {
		Role role=Role.findById(Role.class, Long.parseLong(request.getParameter("roleId")));
		List<User> users=User.findAll(User.class, "firstName", "desc", locale.toString());
		model.addAttribute("users",users);
		model.addAttribute("domain", role);
		if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
            request.getSession().removeAttribute("type");
        }
	}
	
	@RequestMapping(value="/user",method=RequestMethod.PUT)
	protected String UpdateUser(final ModelMap model,
			final RedirectAttributes redirectAttributes, final @Valid @ModelAttribute("domain") Role domain,
			final HttpServletRequest request, final Locale locale){
			
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String messagePattern=servletPath.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", servletPath);
		model.addAttribute("domain", domain);	
		domain.merge();
		redirectAttributes.addFlashAttribute("type", "success");
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "update_success");
        String returnUrl = "redirect:/" + servletPath+"?roleId="+domain.getId(); 
        return returnUrl;
		
	}
	
	@Override	
    protected <E extends BaseDomain> void customInitBinderSuperClass(
	            final Class clazz, final WebDataBinder binder) {
		         binder.registerCustomEditor(Set.class,"credentials",
						new CustomCollectionEditor(Set.class) {
					@Override
					protected Object convertElement(
							final Object element) {
						String id = null;
						if (element instanceof String) {
							id = (String) element;
						}
						return id != null ? BaseDomain
								.findById(Credential.class,
										Long.valueOf(id))
										: null;
					}
				});
		       
	 }
}
