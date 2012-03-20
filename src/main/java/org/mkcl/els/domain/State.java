/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.State.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class State.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_states")
public class State extends BaseDomain implements Serializable {

	// ---------------------------------Attributes-------------------------------------------------
	/** The Constant serialVersionUID. */
	private static final transient long serialVersionUID = 1L;

	// Reason:The longest state in terms of name is Arunachal Pradesh(17
	// characters).so,name is taken to be 17*3
	/** The name. */
	@Column(length = 51)
	@NotEmpty
	private String name;

	// ---------------------------------Constructors----------------------------------------------
	/**
	 * Instantiates a new state.
	 */
	public State() {
		super();
	}

	/**
	 * Instantiates a new state.
	 * 
	 * @param name
	 *            the name
	 */
	public State(final String name) {
		super();
		this.name = name;
	}

	// -------------------------------------Domain_Methods----------------------------------------------

	// -------------------------------------Getters/Setters-----------------------------------
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
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

}
