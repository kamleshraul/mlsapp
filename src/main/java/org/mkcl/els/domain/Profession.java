/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Profession.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Profession.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "professions")
public class Profession extends BaseDomain implements Serializable {

	/**** Attributes ****/
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The profession. */
    @Column(length = 600)
    private String name;

    /**** Constructors ****/

    /**
     * Instantiates a new profession.
     */
    public Profession() {
        super();
    }

    /**
     * Instantiates a new profession.
     *
     * @param profession the profession
     */
    public Profession(final String name) {
        super();
        this.name = name;
    }

    /**** Domain methods ****/

    /**** Getters and Setters ****/
    /**
     * Gets the profession.
     *
     * @return the profession
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the profession.
     *
     * @param profession the new profession
     */
    public void setName(final String name) {
        this.name = name;
    }
}
