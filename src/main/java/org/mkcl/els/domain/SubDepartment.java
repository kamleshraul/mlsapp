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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.ApplicationLocaleRepository;
import org.mkcl.els.repository.SubDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    /** The display name. */
    @Column(length = 600)
    private String displayName;

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
    
    /** The ministry display name.(optional used for reports like yaadi) */
    @Column(length = 600)
    private String ministryDisplayName;
    
	/** The SubDepartment repository. */
	@Autowired
	private transient SubDepartmentRepository subDepartmentRepository;
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
	
	// ---------------------------------Domain Methods-------------------------------------------------
	public Ministry findMinistry(Date onDate) {
		return getSubDepartmentRepository().findMinistry(this.getId(), onDate);
	}
	
	public static List<SubDepartment> findAllCurrentSubDepartments(final String locale) throws ELSException {
    	return getSubDepartmentRepository().findAllCurrentSubDepartments(locale);
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	
    public String getMinistryDisplayName() {
		return ministryDisplayName;
	}

	public void setMinistryDisplayName(String ministryDisplayName) {
		this.ministryDisplayName = ministryDisplayName;
	}

    public static SubDepartmentRepository getSubDepartmentRepository() {
    	SubDepartmentRepository subDepartmentRepository = new SubDepartment().subDepartmentRepository;
		if (subDepartmentRepository == null) {
			throw new IllegalStateException(
					"MemberRepository has not been injected in Member Domain");
		}
		return subDepartmentRepository;
	}
	
	  public static List<SubDepartment> findAllSubDepartments(final String locale) throws ELSException {
	    	return getSubDepartmentRepository().findAllSubDepartment(locale);
		}


}
