/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Role.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * The Class Role.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Entity
@Table(name = "roles")
public class Role extends BaseDomain implements Serializable {

    // ------------- Attributes
    // --------------------------------------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The role name. */
    // Reason:Lets assume the role to be maximum 50 characters in Latin.so
    // alloting
    // 50*3 characters for Marathi
    @Column(length = 150)
    @NotEmpty
    private String name;

    // ---------------- Constructors
    // ------------------------------------------------------------------------------
    /**
     * Instantiates a new role.
     */
    public Role() {

    }

    /**
     * Instantiates a new role.
     *
     * @param name the name
     */
    public Role(final String name) {
        super();
        this.name = name;
    }

    // -------------- Getters & Setters
    // --------------------------------------------------------------------------
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
    public void setName(final String name) {
        this.name = name;
    }

}
