package org.mkcl.els.controller;

import java.util.Locale;

import org.mkcl.els.domain.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());


    protected AuthUser getCurrentUser(){
       return (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    protected Locale getUserLocale(){
    	Locale locale = LocaleContextHolder.getLocale();
    	return locale;
    }

    protected boolean isSessionValid(){
       if(null==SecurityContextHolder.getContext().getAuthentication().getPrincipal())
           return false;
        else
           return true;
    }

}
