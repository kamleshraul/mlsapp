/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.domain.Ministry
 * Created On: Apr 4, 2012
 */

package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Ministry.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_ministries")
public class Ministry extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    @NotEmpty
    private String department;

    /** The alias. */
    @Column(length = 200)
    private String alias;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new ministry.
     */
    public Ministry() {
        super();
    }

    /**
     * Instantiates a new ministry.
     *
     * @param department the department
     * @param alias the alias
     */
    public Ministry(final String department, final String alias) {
        super();
        this.department = department;
        this.alias = alias;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the department.
     *
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the department.
     *
     * @param department the new department
     */
    public void setDepartment(final String department) {
        this.department = department;
    }

    /**
     * Gets the alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     *
     * @param alias the new alias
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

}
