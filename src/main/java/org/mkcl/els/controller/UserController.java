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

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.User;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

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
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, final User domain,
			final HttpServletRequest request) {
		List<Role> roles=Role.findAll(Role.class, "name", "desc", null);
		model.addAttribute("roles",roles);
		Member member=Member.findById(Member.class, domain.getId());
		model.addAttribute("memberType",member.getMemberType().getName());
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
 	 * @see org.mkcl.els.controller.GenericController#customInitBinderSuperClass(java.lang.Class, org.springframework.web.bind.WebDataBinder)
 	 */
 	@Override
	    protected <E extends BaseDomain> void customInitBinderSuperClass(
	            final Class clazz, final WebDataBinder binder) {
		       //binder.registerCustomEditor(Role.class, new CustomCollectionEditor(List.class));
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
	            
	    }
	 

}
