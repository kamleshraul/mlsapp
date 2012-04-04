/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Session.java
 * Created On: Mar 13, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class SessionType.
 *
 * @author anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_sessiontypes")
public class SessionType extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 300)
    @NotEmpty
    private String sessionType;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new session type.
     */
    public SessionType() {
        super();
    }

    /**
     * Instantiates a new session type.
     *
     * @param sessionType the session type
     */
    public SessionType(final String sessionType) {
        super();
        this.sessionType = sessionType;
    }

    // -------------------------------Domain_Methods-----------------------------------

    // ------------------------------------------Getters/Setters-----------------------
    /**
     * Gets the session type.
     *
     * @return the session type
     */
    public String getSessionType() {
        return sessionType;
    }

    /**
     * Sets the session type.
     *
     * @param sessiontype the new session type
     */
    public void setSessionType(final String sessiontype) {
        this.sessionType = sessiontype;
    }

}
