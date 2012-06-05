/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberDepartment.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.criteria.Fetch;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.util.FormaterUtil;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class MemberDepartment.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "members_departments")	//The name is kept such so as to avoid collision
									// with a prior existing table with similar name
@JsonIgnoreProperties({"department"})
public class MemberDepartment extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The department. */
	@ManyToOne(fetch=FetchType.LAZY)
	private Department department;

	@ManyToOne(fetch=FetchType.LAZY)
	private DepartmentDetail subDepartment;
	/** The from date. */
	@Temporal(TemporalType.DATE)
	private Date fromDate;

	/** The to date. */
	@Temporal(TemporalType.DATE)
	private Date toDate;

	/** The is independent charge. */
	private Boolean isIndependentCharge;

	/**
	 * Instantiates a new member department.
	 */
	public MemberDepartment() {
		super();
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
	 * Gets the from date.
	 *
	 * @return the from date
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 *
	 * @param fromDate the new from date
	 */
	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Gets the to date.
	 *
	 * @return the to date
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 *
	 * @param toDate the new to date
	 */
	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}

	/**
	 * Gets the checks if is independent charge.
	 *
	 * @return the checks if is independent charge
	 */
	public Boolean getIsIndependentCharge() {
		return isIndependentCharge;
	}

	/**
	 * Sets the checks if is independent charge.
	 *
	 * @param isIndependentCharge the new checks if is independent charge
	 */
	public void setIsIndependentCharge(final Boolean isIndependentCharge) {
		this.isIndependentCharge = isIndependentCharge;
	}

	//---------------------------
	/**
	 * Format to date.
	 *
	 * @return the string
	 */
	public String formatToDate(){
		return FormaterUtil.getDateFormatter(this.getLocale()).format(this.getToDate());
	}

	/**
	 * Format from date.
	 *
	 * @return the string
	 */
	public String formatFromDate(){
		return FormaterUtil.getDateFormatter(this.getLocale()).format(this.getFromDate());
	}

	public DepartmentDetail getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(DepartmentDetail subDepartment) {
		this.subDepartment = subDepartment;
	}
	
}
