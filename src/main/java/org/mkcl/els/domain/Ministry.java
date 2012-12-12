/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Ministry.java
 * Created On: Jun 2, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.MinistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Ministry.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "ministries")
@JsonIgnoreProperties({"groups"})
public class Ministry extends BaseDomain implements Serializable{
	  // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 900)
    private String name;

    /** The is expired. */
    @Column
    private  Boolean isExpired;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    @Autowired
    private transient MinistryRepository ministryRepository;
    // ---------------------------------Constructors----------------------------------------------

	/**
     * Instantiates a new ministry.
     */
    public Ministry() {
		super();
	}

	/**
	 * Instantiates a new ministry.
	 *
	 * @param name the name
	 * @param isExpired the is expired
	 * @param remarks the remarks
	 */
	public Ministry(final String name, final Boolean isExpired, final String remarks) {
		super();
		this.name = name;
		this.isExpired = isExpired;
		this.remarks = remarks;
	}

	// ---------------------------------Domain Methods----------------------------------------------
    public static MinistryRepository getMinistryRepository() {
        MinistryRepository repository = new Ministry().ministryRepository;
        if (repository == null) {
            throw new IllegalStateException(
                    "MinistryRepository has not been injected in Ministry Domain");
        }
        return repository;
    }

    public static List<Ministry> findUnassignedMinistries(final String locale) {
        return getMinistryRepository().findUnassignedMinistries(locale);
    }

    public static List<Ministry> findMinistriesAssignedToGroups(final HouseType houseType,final Integer sessionYear,final SessionType sessionType,final String locale){
        return getMinistryRepository().findMinistriesAssignedToGroups(houseType,sessionYear,sessionType,locale);

    }

	// ---------------------------------getters and setters----------------------------------------------
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

	/**
	 * Gets the checks if is expired.
	 *
	 * @return the checks if is expired
	 */
	public Boolean getIsExpired() {
		return isExpired;
	}

	/**
	 * Sets the checks if is expired.
	 *
	 * @param isExpired the new checks if is expired
	 */
	public void setIsExpired(final Boolean isExpired) {
		this.isExpired = isExpired;
	}

	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 *
	 * @param remarks the new remarks
	 */
	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

}
