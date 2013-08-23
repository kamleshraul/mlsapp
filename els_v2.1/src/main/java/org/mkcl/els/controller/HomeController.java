/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.HomeController.java
 * Created On: Mar 30, 2012
 */
package org.mkcl.els.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Class HomeController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
public class HomeController extends BaseController {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory
    .getLogger(HomeController.class);

    /** The Constant DEFAULT_LOCALE. */
    private static final String DEFAULT_LOCALE = "mr_IN";

    /** The Constant ASC. */
    private static final String ASC = "asc";

    /**
     * Login.
     *
     * @param lang the lang
     * @param model the model
     * @param request the request
     * @param response the response
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false) final String lang,
            final Model model, 
            final HttpServletRequest request,
            final HttpServletResponse response, 
            final Locale locale) {
        List<ApplicationLocale> supportedLocales = ApplicationLocale.findAll(
                ApplicationLocale.class, "language", ASC, "");
        if (lang != null) {
            model.addAttribute("selectedLocale", lang);
        }
        else {
            model.addAttribute("selectedLocale", DEFAULT_LOCALE);
        }
        model.addAttribute("locales", supportedLocales);
        return "login";
    }

    /**
     * Home.
     *
     * @param model the model
     * @param request the request
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {
        //here we will initialize authuser wih locale dependent data such as
        //firstname,middlename,lastname,title and housetype.
        //This housetype will be same as the housetype alloted to user while
        //creating user entry.
        AuthUser user=this.getCurrentUser();
        Credential credential=Credential.findByFieldName(Credential.class, "username",user.getUsername(), "");
        User authenticatedUser=User.findByFieldName(User.class,"credential",credential, locale.toString());
        this.getCurrentUser().setFirstName(authenticatedUser.getFirstName());
        this.getCurrentUser().setMiddleName(authenticatedUser.getMiddleName());
        this.getCurrentUser().setLastName(authenticatedUser.getLastName());
        this.getCurrentUser().setTitle(authenticatedUser.getTitle());
        this.getCurrentUser().setHouseType(authenticatedUser.getHouseType().getType());
        this.getCurrentUser().setUserId(authenticatedUser.getId());
        this.getCurrentUser().setBirthDate(authenticatedUser.getBirthDate());
        this.getCurrentUser().setGroupsAllowed(authenticatedUser.getGroupsAllowed());
        this.getCurrentUser().setStartURL(authenticatedUser.getStartURL());
        model.addAttribute("authusername", this.getCurrentUser().getUsername());
        model.addAttribute("authtitle", this.getCurrentUser().getTitle());
        model.addAttribute("authfirstname", this.getCurrentUser().getFirstName());
        model.addAttribute("authmiddlename", this.getCurrentUser().getMiddleName());
        model.addAttribute("authlastname", this.getCurrentUser().getLastName());
        //setting date and time formats to be used.
        String dateFormat= ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DATEPICKER_DATEFORMAT", "")).getValue();
        model.addAttribute("dateFormat",dateFormat);
        String timeFormat=((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DATEPICKER_TIMEFORMAT", "")).getValue();
        model.addAttribute("timeFormat",timeFormat);
        //right now all menus are visible to all.
        Set<Role> roles=this.getCurrentUser().getRoles();
        StringBuffer buffer=new StringBuffer();
        for(Role i:roles){
        	if(i.getMenusAllowed()!=null){
        		if(!i.getMenusAllowed().isEmpty()){
        			buffer.append(i.getMenusAllowed().replaceAll("##",","));
                	buffer.append(",");	
        		}        	
        	}
        }
        String menuIds="";
        if(buffer.length()>0){
        	buffer.deleteCharAt(buffer.length()-1);
        	menuIds=buffer.toString();
        }     
        String menuXml = MenuItem.getMenuXml(menuIds,locale.toString());
        model.addAttribute("menu_xml", menuXml);
        //adding login time
        model.addAttribute("logintime", FormaterUtil.getDateFormatter(timeFormat, locale.toString()).format(new Date()));
        //adding locale
        model.addAttribute("locale",locale.toString());
        //starting url that will be triggered
        model.addAttribute("startURL",this.getCurrentUser().getStartURL());
        return "home";
    }
}
