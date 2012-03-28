/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Religion.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Religion.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_religions")
public class Religion extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 300)
    @NotEmpty
    private String religion;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new religion.
     */
    public Religion() {
        super();
    }

    /**
     * Instantiates a new religion.
     * 
     * @param religion the religion
     */
    public Religion(final String religion) {
        super();
        this.religion = religion;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------

    /**
     * Gets the religion.
     * 
     * @return the religion
     */
    public String getReligion() {
        return religion;
    }

    /**
     * Sets the religion.
     * 
     * @param religion the new religion
     */
    public void setReligion(final String religion) {
        this.religion = religion;
    }

}
