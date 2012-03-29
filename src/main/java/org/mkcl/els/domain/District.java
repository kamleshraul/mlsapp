/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.District.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class District.
 * 
 * @author dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_districts")
public class District extends BaseDomain implements Serializable {

	// ---------------------------------Attributes-------------------------------------------------
	/** The Constant serialVersionUID. */
	private static final transient long serialVersionUID = 1L;

	/** The name. */
	@Column(length = 600)
	@NotEmpty
	private String name;

	/** The total constituencies. */
	@NotNull
	private Integer totalConstituencies = 0;

	/** The division. */
	@ManyToOne
	@JoinColumn(name = "division_id")
	private Division division;

	@Autowired
	private transient DistrictRepository districtRepository;

	// ---------------------------------Constructors----------------------------------------------
	/**
	 * Instantiates a new district.
	 */
	public District() {
		super();
	}

	/**
	 * Instantiates a new district.
	 * 
	 * @param name
	 *            the name
	 * @param division
	 *            the division
	 */
	public District(final String name, final Division division) {
		super();
		this.name = name;
		this.division = division;
	}

	// -------------------------------Domain_Methods----------------------------------------------
	public static DistrictRepository getDistrictRepository() {
		DistrictRepository districtRepository = new District().districtRepository;
		if (districtRepository == null) {
			throw new IllegalStateException(
					"DistrictRepository has not been injected in District Domain");
		}
		return districtRepository;
	}

	@Transactional(readOnly = true)
	public static List<District> findDistrictsByStateId(final Long stateid,
			final String sortBy, final String sortOrder, final String locale) {
		return getDistrictRepository().findDistrictsByStateId(stateid, sortBy,
				sortOrder, locale);
	}

	// ------------------------------------------Getters/Setters-----------------------------------

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

	/**
	 * Gets the total constituencies.
	 * 
	 * @return the total constituencies
	 */
	public int getTotalConstituencies() {
		return totalConstituencies;
	}

	/**
	 * Sets the total constituencies.
	 * 
	 * @param totalConstituencies
	 *            the new total constituencies
	 */
	public void setTotalConstituencies(int totalConstituencies) {
		this.totalConstituencies = totalConstituencies;
	}

	/**
	 * Gets the division.
	 * 
	 * @return the division
	 */
	public Division getDivision() {
		return division;
	}

	/**
	 * Sets the division.
	 * 
	 * @param division
	 *            the new division
	 */
	public void setDivision(final Division division) {
		this.division = division;
	}
}
