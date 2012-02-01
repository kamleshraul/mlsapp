/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.exception.RecordNotFoundException.java
 * Created On: Feb 1, 2012
 */

package org.mkcl.els.common.exception;

/**
 * The Class RecordExistsException.
 *
 * @author vishals
 * @version v1.0.0
 */
public class RecordNotFoundException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4050482305178810162L;

    /**
     * Constructor for RecordExistsException.
     *
     * @param message exception message
     */
    public RecordNotFoundException(final String message) {
        super(message);
    }
}
