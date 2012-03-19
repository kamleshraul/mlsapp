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

// TODO: Auto-generated Javadoc
/**
 * The Class MaritalStatus.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_maritalStatus")
public class MaritalStatus extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------------------------
	/** The Constant serialVersionUID. */
	private static final transient long serialVersionUID = 1L;

	/** The marital_status. */
	@Column(length = 150, nullable = false)
	@NotEmpty
	private String marital_status;

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
	 * @param marital_status
	 *            the marital_status
	 */
	public MaritalStatus(String marital_status) {
		super();
		this.marital_status = marital_status;
	}

	// -------------------------------Domain_Methods----------------------------------------------

	// ------------------------------------------Getters/Setters-----------------------------------
	/**
	 * Gets the marital_status.
	 * 
	 * @return the marital_status
	 */
	public String getMarital_status() {
		return marital_status;
	}

	/**
	 * Sets the marital_status.
	 * 
	 * @param marital_status
	 *            the new marital_status
	 */
	public void setMarital_status(String marital_status) {
		this.marital_status = marital_status;
	}

}
