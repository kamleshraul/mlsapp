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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.CutMotionDateRepository;
import org.mkcl.els.repository.WorkflowConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="cutmotiondates")
@JsonIgnoreProperties(value={
		"houseType",
		"session",
		"createdOn",
		"editedOn",
		"submissionDate",
		"workflowStartedOn",
		"taskReceivedOn",
		"departmentDates",
		"createdBy",
		"editedBy",
		"editedAs",
		"workflowStarted",
		 "remarks"})
public class CutMotionDate extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	/**** deviceType ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType deviceType;
	
	/**** houseType ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="housetype_id")
	private HouseType houseType;
	
	/**** session ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	/**** status ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;
	
	/**** status ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internalstatus_id")
	private Status internalStatus;
	
	/**** status ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendationstatus_id")
	private Status recommendationStatus;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	
	@Temporal(TemporalType.DATE)
	private Date editedOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOn;	
	
	/**** departmentDates ****/
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="cutmotions_departments_date_priority",
	joinColumns={@JoinColumn(name="cutmotiondate_id",referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="cutmotion_department_date_priority_id",referencedColumnName="id")})
	private List<CutMotionDepartmentDatePriority> departmentDates;
	
	/**** Created By ****/
	private String createdBy;
	
	/**** Edited By ****/
	private String editedBy;
	
	/**** Edited By ****/
	private String editedAs;	
	
	/**** Workflow started ****/
	private String workflowStarted;
	
	 /** The remarks. */
    @Column(length=10000)
	private String remarks;
	
	/** The CutMotionDate repository. */
	@Autowired
    private transient CutMotionDateRepository cutMotionDateRepository;
	

	/***** Constructor ****/
	public CutMotionDate() {
		super();
	}

	public CutMotionDate(DeviceType deviceType, Session session, Status status,
			List<CutMotionDepartmentDatePriority> departmentDates) {
		super();
		this.deviceType = deviceType;
		this.session = session;
		this.status = status;
		this.departmentDates = departmentDates;
	}
	/***** Constructor ****/
	
	/**** Repository ****/
	public static CutMotionDateRepository getCutMotionDateRepository() {
		CutMotionDateRepository cutMotionDateRepository = new CutMotionDate().cutMotionDateRepository;
        if (cutMotionDateRepository == null) {
            throw new IllegalStateException(
                    "CutMotionDateRepository has not been injected in CutMotionDate Domain");
        }
        return cutMotionDateRepository;
    }
	/**** Repository ****/
		
	/**** Static Methods ****/
	public static Boolean removeDepartmentDatePriority(final Long cutMotionDateId, final Long cutMotionDepartmentDatePriorityId){
		return getCutMotionDateRepository().removeDepartmentDatePriority(cutMotionDateId, cutMotionDepartmentDatePriorityId);
	}
	
	public static CutMotionDate findCutMotionDateSessionDeviceType(final Session session, final DeviceType deviceType, final String locale) throws Exception{
		return getCutMotionDateRepository().findCutMotionDateSessionDeviceType(session, deviceType, locale);
	}
	/**** Static Methods ****/	
	
	/**** Method ****/
	public CutMotionDate simpleMerge() {
        CutMotionDate d = (CutMotionDate) super.merge();
        return d;
    }
	/**** Method ****/
	
	/**** Getters and Setters ****/
	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Status getStatus() {
		return status;
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

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public List<CutMotionDepartmentDatePriority> getDepartmentDates() {
		return departmentDates;
	}

	public void setDepartmentDates(List<CutMotionDepartmentDatePriority> departmentDates) {
		this.departmentDates = departmentDates;
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

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getWorkflowStartedOn() {
		return workflowStartedOn;
	}

	public void setWorkflowStartedOn(Date workflowStartedOn) {
		this.workflowStartedOn = workflowStartedOn;
	}

	public Date getTaskReceivedOn() {
		return taskReceivedOn;
	}

	public void setTaskReceivedOn(Date taskReceivedOn) {
		this.taskReceivedOn = taskReceivedOn;
	}

	public String getWorkflowStarted() {
		return workflowStarted;
	}

	public void setWorkflowStarted(String workflowStarted) {
		this.workflowStarted = workflowStarted;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	/**** Getters & Setters ****/
}
