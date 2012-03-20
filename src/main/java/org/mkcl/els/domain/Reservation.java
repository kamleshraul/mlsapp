/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Reservation.java
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
 * The Class Reservation.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_reservations")
public class Reservation extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------------------------
	/** The Constant serialVersionUID. */
	private static final transient long serialVersionUID = 1L;

	/** The reservation_type. */
	@Column(length = 150, nullable = false)
	@NotEmpty
	private String reservationType;

	/** The short_name. */
	@Column(length = 75, nullable = false)
	@NotEmpty
	private String shortName;

	// ---------------------------------Constructors----------------------------------------------

	/**
	 * Instantiates a new reservation.
	 */
	public Reservation() {
		super();
	}

	/**
	 * Instantiates a new reservation.
	 *
	 * @param reservationType the reservation type
	 * @param short_name the short_name
	 */
	public Reservation(final String reservationType, final String short_name) {
		super();
		this.reservationType = reservationType;
		this.shortName = short_name;
	}

	

	// -------------------------------Domain_Methods----------------------------------------------

	// ------------------------------------------Getters/Setters-----------------------------------
	/**
	 * Gets the reservation type.
	 *
	 * @return the reservation type
	 */
	public String getReservationType() {
		return reservationType;
	}

	/**
	 * Sets the reservation type.
	 *
	 * @param reservationType the new reservation type
	 */
	public void setReservationType(final String reservationType) {
		this.reservationType = reservationType;
	}

	/**
	 * Gets the short name.
	 *
	 * @return the short name
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Sets the short name.
	 *
	 * @param shortName the new short name
	 */
	public void setShortName(final String shortName) {
		this.shortName = shortName;
	}
}
