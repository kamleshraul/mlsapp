package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "cutmotiondate_drafts")
public class CutMotionDateDraft extends BaseDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The type. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType deviceType;

	/** The status. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private Status status;

	/** The internal status. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "internalstatus_id")
	private Status internalStatus;

	/** The recommendation status. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recommendationstatus_id")
	private Status recommendationStatus;

	/** The remarks. */
	@Column(length = 30000)
	private String remarks;

	/** The edited on. */
	@Temporal(TemporalType.TIMESTAMP)
	@JoinColumn(name = "editedon")
	private Date editedOn;

	/** The edited by. */
	@Column(length = 1000)
	private String editedBy;

	/** The edited as. */
	@Column(length = 1000)
	private String editedAs;
	
	/**** departmentDates ****/
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="cutmotiondatedraft_departments_priority",
	joinColumns={@JoinColumn(name="cutmotiondatedraft_id",referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="cutmotiondate_department_priority_id",referencedColumnName="id")})
	private List<CutMotionDepartmentDatePriority> departmentDates;

	public CutMotionDateDraft() {
		super();
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public List<CutMotionDepartmentDatePriority> getDepartmentDates() {
		return departmentDates;
	}

	public void setDepartmentDates(
			List<CutMotionDepartmentDatePriority> departmentDates) {
		this.departmentDates = departmentDates;
	}
}
