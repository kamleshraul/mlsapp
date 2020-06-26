/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.CustomParameterController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.CustomParameter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class CustomParameterController.
 *
 * @author anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/customparam")
public class CustomParameterController extends
        GenericController<CustomParameter> {
	@Override
	protected void populateModule(final ModelMap model,
            final HttpServletRequest request, 
            final String locale,
            final AuthUser currentUser) {
    CustomParameter customParameter= CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CATEGORY_MASTER, locale);
    List<MasterVO> categoriesVO=new ArrayList<MasterVO>();
    if(customParameter!=null){
    	String category=customParameter.getValue();
    	String categories[]=category.split("#");
    	for(int i=0;i<categories.length;i++){
    		String strCategories[]=categories[i].split("~");
    		MasterVO categoryVO=new MasterVO();
    		categoryVO.setName(strCategories[0]);
    		categoryVO.setValue(strCategories[1]);
    		categoriesVO.add(categoryVO);
    	}
    }
    model.addAttribute("categories", categoriesVO);
	
	}
	
	/*
     * (non-Javadoc)
     *
     * @see
     * org.mkcl.els.controller.GenericController#populateNew(org.springframework
     * .ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.util.Locale,
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model,
                               final CustomParameter domain,
                               final String locale,
                               final HttpServletRequest request) {
       
    	populateCategories(model,domain,request);
    	try {
			model.addAttribute("availableLocales", ApplicationLocale.findAllLocales());
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    protected void populateEdit(final ModelMap model, 
    		final CustomParameter domain,
            final HttpServletRequest request) {
       	populateCategories(model,domain,request);
       	model.addAttribute("category", domain.getCategory());
       	try {
			model.addAttribute("availableLocales", ApplicationLocale.findAllLocales());
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void populateCategories(final ModelMap model, 
    		final CustomParameter domain,
            final HttpServletRequest request){
    	CustomParameter customParameter= CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CATEGORY_MASTER, this.getUserLocale().toString());
        List<MasterVO> categoriesVO=new ArrayList<MasterVO>();
        if(customParameter!=null){
        	String category=customParameter.getValue();
        	String categories[]=category.split("#");
        	for(int i=0;i<categories.length;i++){
        		String strCategories[]=categories[i].split("~");
        		MasterVO categoryVO=new MasterVO();
        		categoryVO.setName(strCategories[0]);
        		categoryVO.setValue(strCategories[1]);
        		categoriesVO.add(categoryVO);
        	}
        }
        model.addAttribute("categories", categoriesVO);
   }
    
 }
