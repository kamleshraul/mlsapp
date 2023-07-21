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

import org.mkcl.els.repository.CutMotionRepository;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "discussionmotion_drafts")
public class DiscussionMotionDraft extends BaseDomain implements Serializable{

	 /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /**** Attributes ****/
    
    /** The type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType type;

    /** The subject. */
    @Column(length=30000)
    private String subject;

    /** The Notice Content. */
    @Column(length=30000)
    private String noticeContent;

    /** The status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    /** The internal status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;

    /** The remarks. */
    @Column(length=30000)
    private String remarks;

    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;

    /*** The ministries. ***/
	  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	  @JoinTable(name="discussionmotion_draft_ministries",
	          joinColumns={@JoinColumn(name="discussionmotion_draft_id", referencedColumnName="id")},
	          inverseJoinColumns={@JoinColumn(name="ministry_id", referencedColumnName="id")})
	  private List<Ministry> ministries;
	
	  /*** The department. ***/
	  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	  @JoinTable(name="discussionmotion_draft_departments",
	          joinColumns={@JoinColumn(name="discussionmotion_draft_id", referencedColumnName="id")},
	          inverseJoinColumns={@JoinColumn(name="department_id", referencedColumnName="id")})
	  private List<Department> departments;
	
	  /*** The sub department. ***/
	  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	  @JoinTable(name="discussionmotion_draft_subdepartments",
	          joinColumns={@JoinColumn(name="discussionmotion_draft_id", referencedColumnName="id")},
	          inverseJoinColumns={@JoinColumn(name="subdepartment_id", referencedColumnName="id")})
	  private List<SubDepartment> subDepartments;
        
    /**** Clubbing Entities ****/
    /** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private DiscussionMotion parent;
	
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="discussionmotion_drafts_clubbingentities", joinColumns={@JoinColumn(name="discussionmotiondraft_id", referencedColumnName="id")}, 
    inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;

    /**** Referenced Entities ****/
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="discussionmotion_drafts_referencedunits", joinColumns={@JoinColumn(name="discussionmotiondraft_id", referencedColumnName="id")}, 
    inverseJoinColumns={@JoinColumn(name="referenced_unit_id", referencedColumnName="id")})
    private List<ReferenceUnit> referencedEntities;
    
    @Column(name="brief_explanation" ,length =1000)
    private String briefExplanation;
    
    @Temporal(TemporalType.DATE)
	private Date discussionDate;
    
    

	public String getBriefExplanation() {
		return briefExplanation;
	}

	public void setBriefExplanation(String briefExplanation) {
		this.briefExplanation = briefExplanation;
	}
	
	

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	/**** Constructors and Domain Methods****/
    public DiscussionMotionDraft() {
		super();
	}

	public DiscussionMotionDraft(String locale, DeviceType type,
			String subject, String noticeContent, Status status,
			Status internalStatus, Status recommendationStatus, String remarks,
			Date editedOn, String editedBy, String editedAs,
			List<Ministry> ministry, List<Department> department,
			List<SubDepartment> subDepartment, DiscussionMotion parent,
			List<ClubbedEntity> clubbedEntities,
			List<ReferenceUnit> referencedEntities) {
		super(locale);
		this.type = type;
		this.subject = subject;
		this.noticeContent = noticeContent;
		this.status = status;
		this.internalStatus = internalStatus;
		this.recommendationStatus = recommendationStatus;
		this.remarks = remarks;
		this.editedOn = editedOn;
		this.editedBy = editedBy;
		this.editedAs = editedAs;
		this.ministries = ministry;
		this.departments = department;
		this.subDepartments = subDepartment;
		this.parent = parent;
		this.clubbedEntities = clubbedEntities;
		this.referencedEntities = referencedEntities;
	}
	
	/**** Getters and Setters ****/
	
	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
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

	public List<Ministry> getMinistries() {
		return ministries;
	}

	public void setMinistries(List<Ministry> ministries) {
		this.ministries = ministries;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> department) {
		this.departments = department;
	}

	public List<SubDepartment> getSubDepartments() {
		return subDepartments;
	}

	public void setSubDepartments(List<SubDepartment> subDepartment) {
		this.subDepartments = subDepartment;
	}

	public DiscussionMotion getParent() {
		return parent;
	}

	public void setParent(DiscussionMotion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public List<ReferenceUnit> getReferencedEntities() {
		return referencedEntities;
	}

	public void setReferencedEntities(List<ReferenceUnit> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}
}
