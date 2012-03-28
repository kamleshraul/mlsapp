/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Degree.java
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
 * The Class Degree.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_degrees")
public class Degree extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 300)
    @NotEmpty
    private String degree;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new degree.
     */
    public Degree() {
        super();
    }

    /**
     * Instantiates a new degree.
     * 
     * @param degree the degree
     */
    public Degree(final String degree) {
        super();
        this.degree = degree;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the degree.
     * 
     * @return the degree
     */
    public String getDegree() {
        return degree;
    }

    /**
     * Sets the degree.
     * 
     * @param degree the new degree
     */
    public void setDegree(final String degree) {
        this.degree = degree;
    }

}
