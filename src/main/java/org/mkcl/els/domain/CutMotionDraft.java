/*
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.CutMotionDraft.java
 * Created On: Mar 27, 2012
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


/**
 * The Class CutMotionDraft.
 *
 * @author vikasg
 * @since v1.0.0
 */

@Configurable
@Entity
@Table(name = "cutmotion_drafts")
public class CutMotionDraft extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /**** Attributes ****/
    
    /** The type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType deviceType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date answeringDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date discussionDate;

    /** The subject. */
    @Column(length=30000)
    private String mainTitle;
    
    @Column(length=30000)
    private String secondaryTitle;
    
    @Column(length=30000)
    private String subTitle;

    /** The notice content */
    @Column(length=30000)
    private String noticeContent;

    /** The answer. */
    @Column(length=30000)
    private String reply;

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
        
    /**** Clubbing Entities ****/
    /** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private CutMotion parent;
	
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="cutmotionsdrafts_clubbingentities", 
    joinColumns={@JoinColumn(name="cutmotiondraft_id", referencedColumnName="id")}, 
    inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;
    
    //--------------------------Referenced Entities------------------------------------------
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="cutmotiondrafts_referencedentities", 
    joinColumns={@JoinColumn(name="cutmotiondraft_id", referencedColumnName="id")}, 
    inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
    private List<ReferencedEntity> referencedEntities;
        
    /**** For half hour discussion from question ****/
    /** The reason. */
    @Column(length=30000)
    private String rejectionReason;
        
    /**** Constructors ****/

	/**
	 * Instantiates a new CutMotionDraft.
	 */
	public CutMotionDraft() {
		super();
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}

	public String getSecondaryTitle() {
		return secondaryTitle;
	}

	public void setSecondaryTitle(String secondaryTitle) {
		this.secondaryTitle = secondaryTitle;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
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

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
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

	public CutMotion getParent() {
		return parent;
	}

	public void setParent(CutMotion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}
	
	public List<ReferencedEntity> getReferencedEntities() {
		return referencedEntities;
	}

	public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
}