/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Nationality.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Nationality.
 * 
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "nationalities")
public class Nationality extends BaseDomain {

    // ---------------------------------Attributes------------------------------------------

    /** The name. */
    @Column(length = 150)
     private String name;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new nationality.
     */
    public Nationality() {
        super();
    }

    /**
     * Instantiates a new nationality.
     * 
     * @param name the name
     */
    public Nationality(String name) {
        super();
        this.name = name;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
}
