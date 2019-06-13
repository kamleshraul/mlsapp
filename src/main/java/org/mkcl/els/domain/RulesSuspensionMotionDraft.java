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
@Table(name = "rules_suspension_motion_drafts")
public class RulesSuspensionMotionDraft extends BaseDomain implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The subject. */
    @Column(length=30000)
    private String subject;

    /** The notice content. */
    @Column(length=30000)
    private String noticeContent;
    
    /** The authority Draft. */
    @Column(length=30000)
    private String authorityDraft;
    
    /** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private RulesSuspensionMotion parent;
	
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="rules_suspension_motiondrafts_clubbingentities", 
	joinColumns={@JoinColumn(name="rules_suspension_motiondraft_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;
    
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
	  @JoinTable(name="rulessuspensionmotions_draft_ministries",
	          joinColumns={@JoinColumn(name="rulessuspensionmotion_draft_id", referencedColumnName="id")},
	          inverseJoinColumns={@JoinColumn(name="ministry_id", referencedColumnName="id")})
	  private List<Ministry> ministries;
	
	  /*** The department. ***/
	  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	  @JoinTable(name="rulessuspensionmotions_draft_departments",
	          joinColumns={@JoinColumn(name="rulessuspensionmotion_draft_id", referencedColumnName="id")},
	          inverseJoinColumns={@JoinColumn(name="department_id", referencedColumnName="id")})
	  private List<Department> departments;
	
	  /*** The sub department. ***/
	  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	  @JoinTable(name="rulessuspensionmotions_draft_subdepartments",
	          joinColumns={@JoinColumn(name="rulessuspensionmotion_draft_id", referencedColumnName="id")},
	          inverseJoinColumns={@JoinColumn(name="subdepartment_id", referencedColumnName="id")})
	  private List<SubDepartment> subDepartments;

    /**** Constructors and Methods ****/
	public RulesSuspensionMotionDraft() {
		super();
	}

	/**** Getters and Setter ****/

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


	public RulesSuspensionMotion getParent() {
		return parent;
	}


	public void setParent(RulesSuspensionMotion parent) {
		this.parent = parent;
	}


	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}


	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
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

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<SubDepartment> getSubDepartments() {
		return subDepartments;
	}

	public void setSubDepartments(List<SubDepartment> subDepartments) {
		this.subDepartments = subDepartments;
	}

	public String getAuthorityDraft() {
		return authorityDraft;
	}

	public void setAuthorityDraft(String authorityDraft) {
		this.authorityDraft = authorityDraft;
	}
	
	

}
