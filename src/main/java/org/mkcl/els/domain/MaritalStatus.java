/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MaritalStatus.java
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
 * The Class MaritalStatus.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_maritalstatus")
public class MaritalStatus extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The maritalStatus. */
    @Column(length = 300)
    @NotEmpty
    private String maritalStatus;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new marital status.
     */
    public MaritalStatus() {
        super();
    }

    /**
     * Instantiates a new marital status.
     * 
     * @param maritalStatus the maritalStatus
     */
    public MaritalStatus(String maritalStatus) {
        super();
        this.maritalStatus = maritalStatus;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the maritalStatus.
     * 
     * @return the maritalStatus
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the maritalStatus.
     * 
     * @param maritalStatus the new maritalStatus
     */
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

}
