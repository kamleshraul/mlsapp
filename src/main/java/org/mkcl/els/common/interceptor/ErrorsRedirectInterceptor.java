/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.interceptor.ErrorsRedirectInterceptor.java
 * Created On: Feb 1, 2012
 */

package org.mkcl.els.common.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * The Class ErrorsRedirectInterceptor.
 *
 * @author vishals
 * @since v1.0.0
 */
public class ErrorsRedirectInterceptor extends HandlerInterceptorAdapter {

    /** The Constant log. */
    private final static Logger log = LoggerFactory
            .getLogger(ErrorsRedirectInterceptor.class);

    /** The Constant ERRORS_MAP_KEY. */
    private final static String ERRORS_MAP_KEY = ErrorsRedirectInterceptor.class
            .getName() + "-errorsMapKey";

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle
     * (javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object,
     * org.springframework.web.servlet.ModelAndView)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void postHandle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final Object handler,
                           final ModelAndView mav) throws Exception {
        if (mav == null) {
            return;
        }

        if (request.getMethod().equalsIgnoreCase(HttpMethod.POST.toString())
                || request.getMethod().equalsIgnoreCase(
                        HttpMethod.PUT.toString())) {
            // POST
            Map<String, Errors> sessionErrorsMap = new HashMap<String, Errors>();
            // If there are any Errors in the model, store them in the session
            for (Map.Entry<String, Object> entry : mav.getModel().entrySet()) {
                Object obj = entry.getValue();
                if (obj instanceof Errors) {
                    Errors errors = (Errors) obj;
                    sessionErrorsMap.put(entry.getKey(), errors);
                }
            }
            if (!sessionErrorsMap.isEmpty()) {
                request.getSession().setAttribute(
                        ERRORS_MAP_KEY, sessionErrorsMap);
            }
        } else if (request.getMethod()
                .equalsIgnoreCase(HttpMethod.GET.toString())) {
            // GET
            Map<String, Errors> sessionErrorsMap = (Map<String, Errors>) request
                    .getSession().getAttribute(ERRORS_MAP_KEY);
            if (sessionErrorsMap != null) {
                // Added By sandeep singh
                // Issue can be reproduced by first creating new,than post with
                // some validation message
                // and when errors have appeared click new again.Instead of all
                // errors getting cleared in case of new ,
                // we still see errors attached to the jsp.for edit we need to
                // produce validation messages in edit(put)
                // and then click refresh in grid.Error messages doesn't seem to
                // disaapear

                if (request.getRequestURI().contains("/new")
                        || request.getRequestURI().contains("/edit")) {
                    request.getSession().removeAttribute(ERRORS_MAP_KEY);
                } else {
                    mav.addAllObjects(sessionErrorsMap);
                    request.getSession().removeAttribute(ERRORS_MAP_KEY);
                }

            }
        }
    }
}
