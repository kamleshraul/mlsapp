/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.BaseController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.Locale;

import org.mkcl.els.common.vo.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * The Class BaseController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public abstract class BaseController {

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Gets the current user.
     *
     * @return the current user
     */
    protected AuthUser getCurrentUser() {
        return (AuthUser) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    }

    /**
     * Gets the user locale.
     *
     * @return the user locale
     */
    protected Locale getUserLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale;
    }

    /**
     * Checks if is session valid.
     *
     * @return true, if is session valid
     */
    protected boolean isSessionValid() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            return false;
        } else {
            return true;
        }
    }

}
