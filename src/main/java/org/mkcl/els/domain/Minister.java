/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Minister.java
 * Created On: Apr 23, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Minister.
 *
 * @author compaq
 * @since v1.0.0
 */

@Configurable
@Entity
@Table(name = "ministers")
public class Minister extends BaseDomain{
	 // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @NotEmpty
    @Column(length = 1000)
    private String name;

	// ---------------------------------Constructors----------------------------------------------
	
	/**
	 * Instantiates a new minister.
	 *
	 * @param name the name
	 */
	public Minister(String name) {
		super();
		this.name = name;
	}

	/**
	 * Instantiates a new minister.
	 */
	public Minister() {
		super();
	}
	
	// ---------------------------------Getters and setters----------------------------------------------
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
