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
@Table(name = "adjournmentmotion_drafts")
public class AdjournmentMotionDraft extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The subject. */
    @Column(length=30000)
    private String subject;

    /** The notice content. */
    @Column(length=30000)
    private String noticeContent;
    
    /** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;
    
    /** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private AdjournmentMotion parent;
	
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="adjournmentmotiondrafts_clubbingentities", 
	joinColumns={@JoinColumn(name="adjournmentmotiondraft_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;
    
	/** The referenced adjournment motion. */
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private ReferencedEntity referencedAdjournmentMotion;

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
    
    private Integer admissionNumber;

    /**** Constructors ****/
	public AdjournmentMotionDraft() {
		super();		
	}



	/**** Getters and Setters ****/
	/**
	 * @return the subject
	 */
	
	public Integer getAdmissionNumber() {
		return admissionNumber;
	}
	
	
	
	public void setAdmissionNumber(Integer admissionNumber) {
		this.admissionNumber = admissionNumber;
	}
	

	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the noticeContent
	 */
	public String getNoticeContent() {
		return noticeContent;
	}

	/**
	 * @param noticeContent the noticeContent to set
	 */
	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	/**
	 * @return the ministry
	 */
	public Ministry getMinistry() {
		return ministry;
	}

	/**
	 * @param ministry the ministry to set
	 */
	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	/**
	 * @return the subDepartment
	 */
	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	/**
	 * @param subDepartment the subDepartment to set
	 */
	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	/**
	 * @return the parent
	 */
	public AdjournmentMotion getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(AdjournmentMotion parent) {
		this.parent = parent;
	}

	/**
	 * @return the clubbedEntities
	 */
	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	/**
	 * @param clubbedEntities the clubbedEntities to set
	 */
	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	/**
	 * @return the referencedAdjournmentMotion
	 */
	public ReferencedEntity getReferencedAdjournmentMotion() {
		return referencedAdjournmentMotion;
	}

	/**
	 * @param referencedAdjournmentMotion the referencedAdjournmentMotion to set
	 */
	public void setReferencedAdjournmentMotion(
			ReferencedEntity referencedAdjournmentMotion) {
		this.referencedAdjournmentMotion = referencedAdjournmentMotion;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the internalStatus
	 */
	public Status getInternalStatus() {
		return internalStatus;
	}

	/**
	 * @param internalStatus the internalStatus to set
	 */
	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	/**
	 * @return the recommendationStatus
	 */
	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	/**
	 * @param recommendationStatus the recommendationStatus to set
	 */
	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the editedOn
	 */
	public Date getEditedOn() {
		return editedOn;
	}

	/**
	 * @param editedOn the editedOn to set
	 */
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	/**
	 * @return the editedBy
	 */
	public String getEditedBy() {
		return editedBy;
	}

	/**
	 * @param editedBy the editedBy to set
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	/**
	 * @return the editedAs
	 */
	public String getEditedAs() {
		return editedAs;
	}

	/**
	 * @param editedAs the editedAs to set
	 */
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

}
