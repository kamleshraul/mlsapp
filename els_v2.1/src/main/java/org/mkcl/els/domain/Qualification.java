/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Qualification.java
 * Created On: Apr 17, 2012
 */

package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;


/**
 * The Class Qualification.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="qualifications")
public class Qualification extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
	private transient static final long serialVersionUID = 1L;

	/**** Attributes ****/
	
	/** The degree. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="degree_id")
	private Degree degree;

	/** The details. */
	@Column(length=1000)
	private String details;

	/** The members. */
	@ManyToMany
    @JoinTable(name = "members_qualifications",
            joinColumns = { @JoinColumn(name = "qualification_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") })
    private List<Member> members;
    

	/**** Constructors ****/
	
	/**
	 * Instantiates a new qualification.
	 */
	public Qualification() {
		super();
	}

	/**
	 * Instantiates a new qualification.
	 *
	 * @param degree the degree
	 */
	public Qualification(final Degree degree) {
		super();
		this.degree = degree;
	}


	/**** Domain methods ****/
	

	/**** Getters and Setters ****/
	
	/**
	 * Gets the degree.
	 *
	 * @return the degree
	 */
	public Degree getDegree() {
		return degree;
	}

	/**
	 * Sets the degree.
	 *
	 * @param degree the new degree
	 */
	public void setDegree(final Degree degree) {
		this.degree = degree;
	}

	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Sets the details.
	 *
	 * @param details the new details
	 */
	public void setDetails(final String details) {
		this.details = details;
	}


    /**
     * Gets the members.
     *
     * @return the members
     */
    public List<Member> getMembers() {
        return members;
    }

}
