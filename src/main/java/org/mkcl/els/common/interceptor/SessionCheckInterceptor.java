/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.interceptor.SessionCheckInterceptor.java
 * Created On: May 4, 2012
 */

package org.mkcl.els.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.SessionExpiredException;
import org.mkcl.els.domain.CustomParameter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * The Class SessionCheckInterceptor.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class SessionCheckInterceptor extends HandlerInterceptorAdapter {

    /**The redirect.*/
    private String redirect;

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.handler.
     * HandlerInterceptorAdapter#preHandle
     * (javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(final HttpServletRequest request,
            final HttpServletResponse response, 
            final Object handler)
    throws Exception {
        String redirectUrl = this.createRedirectUrl(request);
        if (request.getRequestURI().contains("login")) {
        	checkAndSetCaptchRquiredAttribute(request);
            return true;
        }
        if (request.getRequestURI().contains("/ws/")) {
            return true;
        }
        if(request.getRequestURI().contains("/ref/") && request.getRequestURI().endsWith("/isMemberActiveInSession")) {
        	return true;
        }
        if (request.getRequestURI().contains("home") && request.getUserPrincipal() == null) {
            throw new SessionExpiredException("The user session has expired, please login again");
        }
        if (request.getRequestURI().contains("upload")) {
            return true;
        }
        if (request.getUserPrincipal() != null) {
            if (request.getMethod().equals("DELETE")) {
                request.getSession().setAttribute("delete", "delete");
            }
            if (request.getRequestURI().contains("/new")
                    || request.getRequestURI().contains("/edit")) {
                if (request.getSession().getAttribute("delete") != null) {
                    if (request.getSession().getAttribute("delete").equals("delete")) {
                        request.setAttribute("type", "success");
                        request.setAttribute("msg", "delete_success");
                    }
                    request.getSession().setAttribute("delete", "");
                }
            }
            request.getSession().setAttribute("refresh", "refresh");
            return true;
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SESSION_TIMED_OUT");
            return false;
        }

    }

    private void checkAndSetCaptchRquiredAttribute(HttpServletRequest request) {
    	CustomParameter captchaCust = CustomParameter.findByName(CustomParameter.class, "CAPTCHA_REQUIRED", "");
	    String captchaRequired="0";
		if(captchaCust!=null && captchaCust.getValue()!=null 
	    		&& captchaCust.getValue().trim().length()>0 
	    		&& captchaCust.getValue().trim().equals("1")){
	    	captchaRequired="1";
	    }else{
	    	captchaRequired="0";
	    }
		request.setAttribute("captchaRequired",captchaRequired);
		//request.getSession().setAttribute("captchaRequired",captchaRequired);
		
	}

	/**
     * Creates the redirect url.
     *
     * @param request the request
     * @return the string
     */
    private String createRedirectUrl(final HttpServletRequest request) {
        if (redirect.startsWith("/")) {
            return request.getContextPath() + redirect;
        } else {
            return redirect;
        }
    }

    /**
     * Sets the redirect.
     *
     * @param redirect the new redirect
     */
    public void setRedirect(final String redirect) {
        this.redirect = redirect;
    }
}
