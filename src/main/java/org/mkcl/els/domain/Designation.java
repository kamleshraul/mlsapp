/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Designation.java
 * Created On: Apr 30, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Designation.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "designations")
public class Designation extends BaseDomain {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    private Integer priority;
    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new designation.
     */
    public Designation() {
		super();
	}

	/**
	 * Instantiates a new designation.
	 *
	 * @param name the name
	 */
	public Designation(final String name) {
		super();
		this.name = name;
	}
    //---------------------------------Getters and Setters-----------------
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


    public Integer getPriority() {
        return priority;
    }


    public void setPriority(final Integer priority) {
        this.priority = priority;
    }
}
