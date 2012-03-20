package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.DivisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class Division.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_divisions")
public class Division extends BaseDomain implements Serializable {

	// ---------------------------------Attributes-------------------------------------------------
	/** The Constant serialVersionUID. */
	private transient static final long serialVersionUID = 1L;

	/** The name. */
	@Column(length = 100)
	@NotEmpty
	private String name;

	/** The state. */
	@ManyToOne
	private State state;

	/** The division repository. */
	@Autowired
	private transient DivisionRepository divisionRepository;

	// ---------------------------------Constructors----------------------------------------------
	/**
	 * Instantiates a new division.
	 */
	public Division() {
		super();
	}

	/**
	 * Instantiates a new division.
	 * 
	 * @param name
	 *            the name
	 * @param state
	 *            the state
	 */
	public Division(final String name, final State state) {
		super();
		this.name = name;
		this.state = state;
	}

	// -------------------------------Domain_Methods----------------------------------------------
	/**
	 * Gets the division repository.
	 * 
	 * @return the division repository
	 */
	public static DivisionRepository getDivisionRepository() {
		DivisionRepository divisionRepository = new Division().divisionRepository;
		if (divisionRepository == null) {
			throw new IllegalStateException(
					"DivisionRepository has not been injected in Division Domain");
		}
		return divisionRepository;
	}

	/**
	 * Find divisions by state id.
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
	 * @author Dhananjay
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
	public static List<Division> findDivisionsByStateId(final Long stateid,
			final String sortBy, final String sortOrder, final String locale) {
		return getDivisionRepository().findDivisionsByStateId(stateid, sortBy,
				sortOrder, locale);
	}

	/**
	 * Find divisions by state name.
	 * 
	 * @param stateName
	 *            the state name
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author Dhananjay
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
	public static List<Division> findDivisionsByStateName(
			final String stateName, final String orderBy, final String sortOrder) {
		return getDivisionRepository().findDivisionsByStateName(stateName,
				orderBy, sortOrder);
	}

	/*
	 * @Transactional(readOnly = true) public static List<Division>
	 * findDivisionsByConstituencyId( final Long constituencyId, final String
	 * sortBy, final String sortOrder) { return
	 * getDivisionRepository().findDivisionsByConstituencyId( constituencyId,
	 * sortBy, sortOrder); }
	 */

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
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	public void setState(final State state) {
		this.state = state;
	}

}
