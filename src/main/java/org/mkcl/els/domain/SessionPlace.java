/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.SessionPlace.java
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
 * The Class SessionPlace.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_sessionPlace")
public class SessionPlace extends BaseDomain implements Serializable{
	// ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 100, nullable = false)
    @NotEmpty
    private String place;
 // -------------------------------Constructors----------------------------------------------

	/**
  * Instantiates a new session place.
  */
 public SessionPlace() {
		super();
	}
	
	/**
	 * Instantiates a new session place.
	 *
	 * @param place the place
	 */
	public SessionPlace(final String place) {
	super();
	this.place = place;
	}
// -------------------------------Domain_Methods----------------------------------------------
 // -------------------------------Getters and Setters----------------------------------------------
	/**
 * Gets the place.
 *
 * @return the place
 */
public String getPlace() {
		return place;
	}
	
	/**
	 * Sets the place.
	 *
	 * @param place the new place
	 */
	public void setPlace(final String place) {
		this.place = place;
	}
	
}
