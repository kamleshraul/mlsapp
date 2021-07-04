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
@Table(name="proprietypoint_drafts")
public class ProprietyPointDraft extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
	
    /********************************************* Attributes *******************************************/
    /** The subject. */
    @Column(length=30000)
    private String subject;

    /** The points of propriety. */
    @Column(length=30000)
    private String pointsOfPropriety;
    
    @Temporal(TemporalType.DATE)
    private Date discussionDate;

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
	private ProprietyPoint parent;
	
	/** The clubbed entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="proprietypointdrafts_clubbingentities",
	joinColumns={@JoinColumn(name="proprietypointdraft_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;
    
    /** The reply. */
    @Column(length=30000)
    private String reply;
    
    /** The rejection reason. */
    @Column(length=30000)
    private String rejectionReason;
    
    /** 
     * The status. 
     * Refers to various final status viz, SUBMITTED, ADMITTED, REJECTED 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    /** 
     * The internal status. 
     * Refers to status assigned to a Propriety Point during the Workflow
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;
    
    /** 
     * If a propriety point is balloted then its balloted status is set to balloted 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ballotstatus_id")
    private Status ballotStatus;
    
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
	
	/**
     * To keep the referring propriety point in order to preserve its all drafts details
     */
	@Column(name="proprietypoint_id")
    private Long proprietyPointId;
	
	/********************************************* Constructors *******************************************/	
    /**
     * Instantiates a new propriety point draft.
     */
    public ProprietyPointDraft() {
        super();
    }
    
    /********************************************* Domain methods *******************************************/

    /********************************************* Getters & Setters *******************************************/
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPointsOfPropriety() {
		return pointsOfPropriety;
	}

	public void setPointsOfPropriety(String pointsOfPropriety) {
		this.pointsOfPropriety = pointsOfPropriety;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public ProprietyPoint getParent() {
		return parent;
	}

	public void setParent(ProprietyPoint parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
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

	public Status getBallotStatus() {
		return ballotStatus;
	}

	public void setBallotStatus(Status ballotStatus) {
		this.ballotStatus = ballotStatus;
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

	public Long getProprietyPointId() {
		return proprietyPointId;
	}

	public void setProprietyPointId(Long proprietyPointId) {
		this.proprietyPointId = proprietyPointId;
	}
	
}