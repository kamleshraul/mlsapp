/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IErrorNotificationService.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service;

import java.util.Map;

/**
 * The Interface IErrorNotificationService.
 *
 * @author sandeeps
 * @since v1.0.0
 */
public interface IErrorNotificationService {

    /**
     * Send notification.
     *
     * @param message the message
     * @param exception the exception
     * @author sandeeps
     * @since v1.0.0
     * Send notification.
     */
    public void sendNotification(String message, Exception exception);

    /**
     * Send notification.
     *
     * @param message the message
     * @param AuthUser the auth user
     * @param requestParams the request params
     * @param exception the exception
     * @author sandeeps
     * @since v1.0.0
     * Send notification.
     */
    public void sendNotification(String message,
                                 String AuthUser,
                                 //Map<String, String> requestParams,  //before upgrade to java EE 7
                                 Map<String, String[]> requestParams,  //after upgrade to java EE 7
                                 Exception exception);
}
