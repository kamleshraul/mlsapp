/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.resolvers.NotifyingExceptionResolver.java
 * Created On: Jan 30, 2012
 */

package org.mkcl.els.common.resolvers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.service.IErrorNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * The Class NotifyingExceptionResolver.
 *
 * @author vishals
 * @version 1.0.0
 */
public class NotifyingExceptionResolver extends SimpleMappingExceptionResolver {

    /** The log. */
    private Logger log = LoggerFactory
            .getLogger(NotifyingExceptionResolver.class);

    /** The notification service. */
    @Autowired
    private IErrorNotificationService notificationService;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.web.servlet.handler.SimpleMappingExceptionResolver
     * #doResolveException(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object,
     * java.lang.Exception)
     */
    @Override
    protected ModelAndView doResolveException(final HttpServletRequest request,
                                              final HttpServletResponse response,
                                              final Object handler,
                                              final Exception ex) {
        log.warn("An Exception has occured in the application", ex);
        String user = "UNKNOWN";
        if (request.getUserPrincipal() != null) {
            user = request.getUserPrincipal().getName();
        }
        sendNotification(user, request.getParameterMap(), ex);
        return super.doResolveException(request, response, handler, ex);
    }

    /**
     * Send notification.
     *
     * @param username the username
     * @param requestParams the request params
     * @param ex the ex
     * @author nileshp
     * @since v1.0.0
     * Send notification.
     */
    private void sendNotification(final String username,
                                  final Map<String, String> requestParams,
                                  final Exception ex) {
        String message = " Exception Occured";
        if (notificationService != null) {
            log.debug("notification message was sent");
            notificationService.sendNotification(
                    message, username, requestParams, ex);
        }
    }
}
