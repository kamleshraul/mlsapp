/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.DepartmentDetail.java
 * Created On: Jun 2, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class SubDepartment.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "subdepartments")
@JsonIgnoreProperties({"department","remarks","isExpired","locale"
	,"version","versionMismatch"})
public class SubDepartment extends BaseDomain implements Serializable{
	 // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 600)
    private String name;

    /** The department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /** The remarks. */
    @Column(length=1000)
    private String remarks;

    /** The is expired. */
    @Column
    private Boolean isExpired;
    // ---------------------------------Constructors-------------------------------------------------

	/**
     * Instantiates a new department detail.
     */
    public SubDepartment() {
		super();
	}

	/**
	 * Instantiates a new department detail.
	 *
	 * @param name the name
	 * @param department the department
	 * @param remarks the remarks
	 * @param isExpired the is expired
	 */
	public SubDepartment(final String name, final Department department, final String remarks,
			final Boolean isExpired) {
		super();
		this.name = name;
		this.department = department;
		this.remarks = remarks;
		this.isExpired = isExpired;
	}
	// ---------------------------------Getters and Setters-------------------------------------------------
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
	 * Gets the department.
	 *
	 * @return the department
	 */
	public Department getDepartment() {
		return department;
	}

	/**
	 * Sets the department.
	 *
	 * @param department the new department
	 */
	public void setDepartment(final Department department) {
		this.department = department;
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


}
