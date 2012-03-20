/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Session.java
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
 * The Class Session.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_sessions")
public class Session extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------------------------
	/** The Constant serialVersionUID. */
	private static final transient long serialVersionUID = 1L;

	/** The name. */
	@Column(length = 150, nullable = false)
	@NotEmpty
	private String sessionType;

	// ---------------------------------Constructors----------------------------------------------

	/**
	 * Instantiates a new session.
	 *
	 * @param session_type the session_type
	 */
	public Session(final String sessiontype) {
		super();
		this.sessionType = sessiontype;
	}

	/**
	 * Instantiates a new session.
	 */
	public Session() {
		super();
	}

	// -------------------------------Domain_Methods-----------------------------------

	// ------------------------------------------Getters/Setters-----------------------
	/**
	 * Gets the session type.
	 *
	 * @return the session type
	 */
	public String getSessionType() {
		return sessionType;
	}

	/**
	 * Sets the session type.
	 *
	 * @param session_type the new session type
	 */
	public void setSessionType(String sessiontype) {
		this.sessionType = sessiontype;
	}

}
