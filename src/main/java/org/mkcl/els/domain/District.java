/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.District.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class District.
 * 
 * @author nileshp
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
	@Column(length = 100)
	@NotEmpty
	private String name;

	/** The total constituencies. */
	@Column(name = "total_constituencies")
	@NotNull
	private int totalConstituencies;

	/** The division. */
	@ManyToOne
	private Division division;

	/** The state. */
	/*
	 * @ManyToOne private State state;
	 */

	/** The district repository. */
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
	/**
	 * Gets the district repository.
	 * 
	 * @return the district repository
	 */
	public static DistrictRepository getDistrictRepository() {
		DistrictRepository districtRepository = new District().districtRepository;
		if (districtRepository == null) {
			throw new IllegalStateException(
					"DistrictRepository has not been injected in District Domain");
		}
		return districtRepository;
	}

	/**
	 * Find districts by division id.
	 * 
	 * @param divisionid
	 *            the divisionid
	 * @param sortBy
	 *            the sort by
	 * @param sortOrder
	 *            the sort order
	 * @param locale
	 *            the locale
	 * @return the list
	 * @author nileshp
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
	public static List<District> findDistrictsByDivisionId(
			final Long divisionid, final String sortBy, final String sortOrder,
			final String locale) {
		return getDistrictRepository().findDistrictsByDivisionId(divisionid,
				sortBy, sortOrder, locale);
	}

	/**
	 * Find districts by division name.
	 * 
	 * @param divisionName
	 *            the division name
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @param locale
	 *            the locale
	 * @return the list
	 * @author nileshp
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
	public static List<District> findDistrictsByDivisionName(
			final String divisionName, final String orderBy,
			final String sortOrder, final String locale) {
		return getDistrictRepository().findDistrictsByDivisionName(
				divisionName, orderBy, sortOrder, locale);
	}

	/**
	 * Find districts by state id.
	 * 
	 * @param stateid
	 *            the stateid
	 * @param sortBy
	 *            the sort by
	 * @param sortOrder
	 *            the sort order
	 * @param locale
	 *            the locale
	 * @return the list
	 * @author nileshp
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
	public static List<District> findDistrictsBystateId(final Long stateid,
			final String sortBy, final String sortOrder, final String locale) {
		return getDistrictRepository().findDistrictsByStateId(stateid, sortBy,
				sortOrder, locale);
	}

	/**
	 * Find districts by state name.
	 * 
	 * @param stateName
	 *            the state name
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author nileshp
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
	public static List<District> findDistrictsBystateName(
			final String stateName, final String orderBy, final String sortOrder) {
		return getDistrictRepository().findDistrictsByStateName(stateName,
				orderBy, sortOrder);
	}

	/**
	 * Find districts by constituency id.
	 * 
	 * @param constituencyId
	 *            the constituency id
	 * @param sortBy
	 *            the sort by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author nileshp
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
	public static List<District> findDistrictsByConstituencyId(
			final Long constituencyId, final String sortBy,
			final String sortOrder) {
		return getDistrictRepository().findDistrictsByConstituencyId(
				constituencyId, sortBy, sortOrder);
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

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	/*
	 * public State getState() { return state; }
	 *//**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	/*
	 * public void setState(final State state) { this.state = state; }
	 */
}
