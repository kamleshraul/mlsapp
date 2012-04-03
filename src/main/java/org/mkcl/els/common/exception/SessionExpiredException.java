/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.exception.SessionExpiredException.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.common.exception;

/**
 * The Class SessionExpiredException.
 * 
 * @author vishals
 * @since v1.0.0
 */
public class SessionExpiredException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The message. */
    private final String message;

    /**
     * Instantiates a new session expired exception.
     */
    public SessionExpiredException() {
        message = "Unknown";
    }

    /**
     * Instantiates a new session expired exception.
     * 
     * @param message the message
     */
    public SessionExpiredException(String message) {
        this.message = message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return message;
    }

}
