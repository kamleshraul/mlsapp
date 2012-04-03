/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.ErrorNotificationServiceImpl.java
 * Created On: Mar 8, 2012
 */

package org.mkcl.els.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.mkcl.els.service.IErrorNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * The Class ErrorNotificationServiceImpl.
 *
 * @author vishals
 */
@Service
public class ErrorNotificationServiceImpl implements IErrorNotificationService {

    /** The els message. */
    @Autowired
    private SimpleMailMessage elsMessage;

    /** The velocity engine. */
    @Autowired
    private VelocityEngine velocityEngine;

    /** The log. */
    protected Logger log = LoggerFactory
            .getLogger(ErrorNotificationServiceImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mkcl.insyncflow.services.IErrorNotificationService#sendNotification
     * (java.lang.String, java.lang.Exception)
     */
    @Override
    public void sendNotification(final String message, final Exception exception) {
        SimpleMailMessage msg = new SimpleMailMessage(this.elsMessage);
        msg.setText("Application Message - " + message + " : " + exception);

        try {
            // this.mailSender.send(msg);
        } catch (MailException ex) {
            log.error("Email Notification message could not sent", ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mkcl.insyncflow.services.IErrorNotificationService#sendNotification
     * (java.lang.String, java.lang.String, java.util.Map, java.lang.Exception)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void sendNotification(final String message,
                                 final String user,
                                 final Map requestParams,
                                 final Exception exception) {
        SimpleMailMessage msg = new SimpleMailMessage(this.elsMessage);
        Map model = new HashMap();
        model.put("app_name", "ELS");
        model.put("implementation", "ELS");
        model.put("occurred_on", new Date().toString());
        model.put("user", user);
        model.put("requestParams", getParameterToString(requestParams));
        model.put("stackTraceElement", exception.getStackTrace());
        String text = VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "exception_mail.vm", model);
        msg.setText(text);
        // this.mailSender.send(msg);
    }

    /**
     * Gets the parameter to string.
     *
     * @param requestParams the request params
     * @return the parameter to string
     */
    public String getParameterToString(final Map requestParams) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = requestParams.keySet().iterator();
        while (it.hasNext()) {
            String keyname = it.next();
            String value = ((String[]) requestParams.get(keyname))[0];
            sb.append(keyname).append(":").append(value).append("\n");
        }
        return sb.toString();
    }

}
