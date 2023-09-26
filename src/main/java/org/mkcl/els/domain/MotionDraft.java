/*
 * 
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

// TODO: Auto-generated Javadoc
/**
 * The Class MotionDraft.
 */
@Configurable
@Entity
@Table(name = "motion_drafts")
public class MotionDraft extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;    

	/**** Attributes ****/
	
    /** The subject. */
    @Column(length=30000)
    private String subject;

    /** The details. */
    @Column(length=30000)
    private String details;    

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

    /** 
     * If a motion is discussed then its discussion status is set to discussed
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="discussionstatus_id")
    private Status discussionStatus;
    
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
    
    /** The edited by actual name.
     * (full name of the actual person who logged in as editedBy at the time of update)
     */
    @Column(length=1000)
    private String editedByActualName;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
   
    /** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    /** The department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="department_id")
    private Department department;

    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;
        
    /** The clubbed entities. */
    /** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private Motion parent;

	/**** Reply(Nivedan) ****/
	@Column(length=30000)
	private String reply;
    
    /**
     * To keep the referring motion in order to preserve its all motion drafts details
     */
    private Long motionId;
    
    /** The in charge member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="incharge_member_id")
    private Member inchargeMember;
    
    @Column(name="actual_edited_by_username")
    private String actualEditedByUserName;
	
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="motionsdrafts_clubbingentities", joinColumns={@JoinColumn(name="motiondraft_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;
    
    //--------------------------Referenced Entities------------------------------------------
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="motiondrafts_referencedunits", joinColumns={@JoinColumn(name="motiondraft_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="referenced_unit_id", referencedColumnName="id")})
    List<ReferenceUnit> referencedUnits;

    
    /**** Fields for storing the confirmation of Group change ****/
    private Boolean transferToDepartmentAccepted = false;
    
    private Boolean mlsBranchNotifiedOfTransfer = false;
    
    
    /******Advance Copy *******/
    private Boolean advanceCopySent = false;
    
    private Boolean advanceCopyPrinted = false;
    
	private String advanceCopyActor;
    /**
     * ** Setters and Getters ***.
     *
     * @return the subject
     */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 *
	 * @param subject the new subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Sets the details.
	 *
	 * @param details the new details
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Gets the internal status.
	 *
	 * @return the internal status
	 */
	public Status getInternalStatus() {
		return internalStatus;
	}

	/**
	 * Sets the internal status.
	 *
	 * @param internalStatus the new internal status
	 */
	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	/**
	 * Gets the recommendation status.
	 *
	 * @return the recommendation status
	 */
	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	/**
	 * Sets the recommendation status.
	 *
	 * @param recommendationStatus the new recommendation status
	 */
	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public Status getDiscussionStatus() {
		return discussionStatus;
	}

	public void setDiscussionStatus(Status discussionStatus) {
		this.discussionStatus = discussionStatus;
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
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Gets the edited on.
	 *
	 * @return the edited on
	 */
	public Date getEditedOn() {
		return editedOn;
	}

	/**
	 * Sets the edited on.
	 *
	 * @param editedOn the new edited on
	 */
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	/**
	 * Gets the edited by.
	 *
	 * @return the edited by
	 */
	public String getEditedBy() {
		return editedBy;
	}

	/**
	 * Sets the edited by.
	 *
	 * @param editedBy the new edited by
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
//		if(editedBy!=null && !editedBy.isEmpty()) {
//			try {
//				this.editedByActualName = User.findFullNameByUserName(this.getEditedBy(), this.getLocale());
//			} catch (ELSException e) {
//				//e.printStackTrace();
//				this.setEditedByActualName("");
//			}
//		} else {
//			this.setEditedBy("");
//			this.setEditedByActualName("");
//		}
	}

	public String getEditedByActualName() {
		return editedByActualName;
	}

	public void setEditedByActualName(String editedByActualName) {
		this.editedByActualName = editedByActualName;
	}

	/**
	 * Gets the edited as.
	 *
	 * @return the edited as
	 */
	public String getEditedAs() {
		return editedAs;
	}

	/**
	 * Sets the edited as.
	 *
	 * @param editedAs the new edited as
	 */
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	/**
	 * Gets the ministry.
	 *
	 * @return the ministry
	 */
	public Ministry getMinistry() {
		return ministry;
	}

	/**
	 * Sets the ministry.
	 *
	 * @param ministry the new ministry
	 */
	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
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
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * Gets the sub department.
	 *
	 * @return the sub department
	 */
	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	/**
	 * Sets the sub department.
	 *
	 * @param subDepartment the new sub department
	 */
	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	/**
	 * Gets the clubbed entities.
	 *
	 * @return the clubbed entities
	 */
	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	/**
	 * Sets the clubbed entities.
	 *
	 * @param clubbedEntities the new clubbed entities
	 */
	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public void setReferencedUnits(List<ReferenceUnit> referencedUnits) {
		this.referencedUnits = referencedUnits;
	}

	public List<ReferenceUnit> getReferencedUnits() {
		return referencedUnits;
	}

	public void setParent(Motion parent) {
		this.parent = parent;
	}

	public Motion getParent() {
		return parent;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Long getMotionId() {
		return motionId;
	}

	public void setMotionId(Long motionId) {
		this.motionId = motionId;
	}

	public Boolean getTransferToDepartmentAccepted() {
		return transferToDepartmentAccepted;
	}

	public void setTransferToDepartmentAccepted(Boolean transferToDepartmentAccepted) {
		this.transferToDepartmentAccepted = transferToDepartmentAccepted;
	}

	public Boolean getMlsBranchNotifiedOfTransfer() {
		return mlsBranchNotifiedOfTransfer;
	}

	public void setMlsBranchNotifiedOfTransfer(Boolean mlsBranchNotifiedOfTransfer) {
		this.mlsBranchNotifiedOfTransfer = mlsBranchNotifiedOfTransfer;
	}

	public Boolean getAdvanceCopySent() {
		return advanceCopySent;
	}

	public void setAdvanceCopySent(Boolean advanceCopySent) {
		this.advanceCopySent = advanceCopySent;
	}

	public Boolean getAdvanceCopyPrinted() {
		return advanceCopyPrinted;
	}

	public void setAdvanceCopyPrinted(Boolean advanceCopyPrinted) {
		this.advanceCopyPrinted = advanceCopyPrinted;
	}

	public String getAdvanceCopyActor() {
		return advanceCopyActor;
	}

	public void setAdvanceCopyActor(String advanceCopyActor) {
		this.advanceCopyActor = advanceCopyActor;
	}

	public String getActualEditedByUserName() {
		return actualEditedByUserName;
	}

	public void setActualEditedByUserName(String actualEditedByUserName) {
		this.actualEditedByUserName = actualEditedByUserName;
	}

	public Member getInchargeMember() {
		return inchargeMember;
	}

	public void setInchargeMember(Member inchargeMember) {
		this.inchargeMember = inchargeMember;
	}
	
}
