package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.util.FormaterUtil;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "members_departments")	//The name is kept such so as to avoid collision
									// with a prior existing table with similar name
@JsonIgnoreProperties({"department"})
public class MemberDepartment extends BaseDomain implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Department department;
	
	@Temporal(TemporalType.DATE)
	private Date fromDate;
	
	@Temporal(TemporalType.DATE)
	private Date toDate;
	
	private Boolean isIndependentCharge;

	public MemberDepartment() {
		super();
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Boolean getIsIndependentCharge() {
		return isIndependentCharge;
	}

	public void setIsIndependentCharge(Boolean isIndependentCharge) {
		this.isIndependentCharge = isIndependentCharge;
	}

	//---------------------------
	public String formatToDate(){
		return FormaterUtil.getDateFormatter(this.getLocale()).format(this.getToDate());
	}
	
	public String formatFromDate(){
		return FormaterUtil.getDateFormatter(this.getLocale()).format(this.getFromDate());
	}
}
