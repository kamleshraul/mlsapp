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

import java.math.BigInteger;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @Override
    protected void populateNew(final ModelMap model, final User domain, final String locale,
            final HttpServletRequest request) {
        domain.setLocale(locale);
        List<HouseType> houseTypes=HouseType.findAllNoExclude("type",ApplicationConstants.ASC, locale);
        model.addAttribute("houseTypes",houseTypes);
        List<Title> titles=Title.findAll(Title.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("titles",titles);
        List<Role> roles=Role.findAll(Role.class, "name", "desc",locale.toString());
        model.addAttribute("roles",roles);
    }
    @Override
    protected void populateEdit(final ModelMap model, final User domain,
            final HttpServletRequest request) {
        List<HouseType> houseTypes=HouseType.findAllNoExclude("type",ApplicationConstants.ASC, domain.getLocale());
        model.addAttribute("houseTypes",houseTypes);
        List<Title> titles=Title.findAll(Title.class,"name",ApplicationConstants.ASC, domain.getLocale());
        model.addAttribute("titles",titles);
        List<Role> roles=Role.findAll(Role.class, "name", "desc",domain.getLocale());
        model.addAttribute("roles",roles);
        StringBuffer buffer=new StringBuffer();
        Credential credential=domain.getCredential();
        if(credential.getEmail()!=null){
        	 model.addAttribute("credential",credential.getId());
        	 if(!credential.getRoles().isEmpty()){
        		 for(Role i:credential.getRoles()){
                     buffer.append(i.getId()+",");
                 }
                 buffer.deleteCharAt(buffer.length()-1);
                 model.addAttribute("selectedRoles",buffer.toString());
        	 }
            
        }
         
    }
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void customValidateCreate(final User domain,
            final BindingResult result, final HttpServletRequest request) {
		User user=User.findByUserName(request.getParameter("email").split("@")[0], domain.getLocale());
		if(user.getId()!=null){
			result.rejectValue("credential.email", "NonUnique", "User already Exist");
		}
        customValidate(domain, result, request);
    }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void customValidateUpdate(final User domain,
            final BindingResult result, final HttpServletRequest request) {
		User user=User.findById(User.class, domain.getId());
		if(!user.getCredential().getEmail().equals(request.getParameter("email"))){
			User duplicateUser=User.findByUserName(request.getParameter("email").split("@")[0], domain.getLocale());
			if(duplicateUser.getId()!=null){
				result.rejectValue("credential.email", "NonUnique", "User already Exist");
			}
		}
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
 		if (domain.isVersionMismatch()) {
    		result.rejectValue("VersionMismatch", "version");
    	}
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
 	 	      credential.setUsername(request.getParameter("email").split("@")[0]);
 	 	      credential.setEnabled(Boolean.parseBoolean(request.getParameter("isEnabled")));
 	 	      credential.setLocale(null);
 	 	      SecureRandom random = new SecureRandom();
 	 	      String str = new BigInteger(60, random).toString(32);
 			  credential.setPassword(str);
 			  String[] selectedRoles=request.getParameterValues("roles");
 			  Set<Role> roles=new HashSet<Role>();
 			 // included the following condition  to avoid null pointer exception
 			  if(selectedRoles!=null){
 				 for(String i:selectedRoles){
 	 			      Role role=Role.findById(Role.class, Long.parseLong(i));
 	 			      roles.add(role);
 	 			  } 
 			  }
 			  credential.setRoles(roles);
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
 			//User user= User.findById(User.class, domain.getId());
 			Credential credential=domain.getCredential();
 			if(!credential.getEmail().equals(request.getParameter("email"))){
 				credential.setEmail(request.getParameter("email"));
 				credential.setUsername(request.getParameter("email").split("@")[0]);
 			}
 			if(!credential.isEnabled()==Boolean.parseBoolean(request.getParameter("isEnabled"))) {
 				credential.setEnabled(Boolean.parseBoolean(request.getParameter("isEnabled")));
 			}
 			String[] selectedRoles=request.getParameterValues("roles");
            Set<Role> roles=new HashSet<Role>();
            // included the following condition  to avoid null pointer exception
            if(selectedRoles!=null){
            	for(String i:selectedRoles){
                    Role role=Role.findById(Role.class, Long.parseLong(i));
                    roles.add(role);
                }
            }
            credential.setLocale(null);
            credential.setRoles(roles);
            credential.merge();
            domain.setCredential(credential);
 		}
 		
 		/*Overiding the Delete method because cannot use the hooks provided in the generics as the domain
 		 * passed in predelete has id=null and the post delete cannot be used
 		 */
 		@Override
 		 @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
 	    public String delete(final @PathVariable("id") Long id,
 	            final ModelMap model, final HttpServletRequest request) {
 	       
 			User user=User.findById(User.class, id);
 			Long credentialId=user.getCredential().getId();
 			List<User> users=User.findUsersByCredential(credentialId);
 			for(User i:users){
 				i.setCredential(null);
 				i.remove();
 			}
 	       Credential credential= Credential.findById(Credential.class, credentialId);
 	       credential.remove();
 			
 	        return "info";
 	    }
 		
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customInitBinderSuperClass(java.lang.Class, org.springframework.web.bind.WebDataBinder)
	 */
//	@Override
//    protected <E extends BaseDomain> void customInitBinderSuperClass(
//	            final Class clazz, final WebDataBinder binder) {
//		         binder.registerCustomEditor(Set.class,"credential.roles",
//						new CustomCollectionEditor(Set.class) {
//					@Override
//					protected Object convertElement(
//							final Object element) {
//						String id = null;
//						if (element instanceof String) {
//							id = (String) element;
//						}
//						return id != null ? BaseDomain
//								.findById(Role.class,
//										Long.valueOf(id))
//										: null;
//					}
//				});
//
//
//		       binder.registerCustomEditor(Set.class,"credential.userGroups",
//				new CustomCollectionEditor(Set.class) {
//		    	   @Override
//		    	   protected Object convertElement(
//						final Object element) {
//			    		   String id = null;
//			    		   if (element instanceof String) {
//			    			   	id = (String) element;
//			    		   }
//			    		   return id != null ? BaseDomain
//							.findById(UserGroup.class,
//									Long.valueOf(id))
//									: null;
//				}
//		       });
//
//		 }

}








