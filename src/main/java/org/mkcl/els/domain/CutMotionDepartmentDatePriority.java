package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="cutmotiondepartmentdatepriority")
@JsonIgnoreProperties({"department","subDepartment","discussionDate", "submissionEndDate"})
public class CutMotionDepartmentDatePriority extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**** Department ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="department_id")
	private Department department;
	
	/**** SubDepartment ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subdepartment_id")
	private SubDepartment subDepartment;
	
	/**** discussionDate ****/
	@Temporal(TemporalType.DATE)
	private Date discussionDate;
	
	/**** submissionEndDate ****/
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionEndDate;	
	
	private Integer priority;
	
	/**
     * To keep the referring cutmotiondate in order to preserve its all cutmotiondepartmentdatepriority details
     */
    @Column(length=45, name="cutmotiondate_id")    
    private String cutMotionDateId;

	public CutMotionDepartmentDatePriority() {
		super();
	}

	public CutMotionDepartmentDatePriority(Department department,
			SubDepartment subDepartment, Date discussionDate,
			Date submissionEndDate) {
		super();
		this.department = department;
		this.subDepartment = subDepartment;
		this.discussionDate = discussionDate;
		this.submissionEndDate = submissionEndDate;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public Date getSubmissionEndDate() {
		return submissionEndDate;
	}

	public void setSubmissionEndDate(Date submissionEndDate) {
		this.submissionEndDate = submissionEndDate;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCutMotionDateId() {
		return cutMotionDateId;
	}

	public void setCutMotionDateId(String cutMotionDateId) {
		this.cutMotionDateId = cutMotionDateId;
	}
}
