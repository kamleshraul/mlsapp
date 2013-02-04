/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionDraft.java
 * Created On: Dec 27, 2012
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
 * The Class QuestionDraft.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "question_drafts")
public class QuestionDraft extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType type;

    /** The answering date. */
    @ManyToOne(fetch=FetchType.LAZY)
    private QuestionDates answeringDate;

    /** The subject. */
    @Column(length=30000)
    private String subject;

    /** The question text. */
    @Column(length=30000)
    private String questionText;

    /** The answer. */
    @Column(length=30000)
    private String answer;

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

    /** The clarification needed from. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="clarification_neededfrom_id")
    private ClarificationNeededFrom clarificationNeededFrom;

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

    /** The mark as answered. */
    private Boolean markAsAnswered;

    //------------------------Group Information--------------------------------
    /** The group. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

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
        
    //--------------------------Clubbing Entities------------------------------------------
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="questionsdrafts_clubbingentities", joinColumns={@JoinColumn(name="questiondraft_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;

    //--------------------------Referenced Entities------------------------------------------
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="questiondrafts_referencedentities", joinColumns={@JoinColumn(name="questiondraft_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
    private List<ReferencedEntity> referencedEntities;
        
    //-------------------------For half hour discussion from question-
    /** The reason. */
    @Column(length=30000)
    private String reason;
    
    @Column(length=30000)
    private String briefExplanation;


	/**
	 * Instantiates a new question draft.
	 */
	public QuestionDraft() {
		super();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public DeviceType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(final DeviceType type) {
		this.type = type;
	}

	/**
	 * Gets the answering date.
	 *
	 * @return the answering date
	 */
	public QuestionDates getAnsweringDate() {
		return answeringDate;
	}

	/**
	 * Sets the answering date.
	 *
	 * @param answeringDate the new answering date
	 */
	public void setAnsweringDate(final QuestionDates answeringDate) {
		this.answeringDate = answeringDate;
	}
	
	/**
	 * Gets the subject.
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
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the question text.
	 *
	 * @return the question text
	 */
	public String getQuestionText() {
		return questionText;
	}

	/**
	 * Sets the question text.
	 *
	 * @param questionText the new question text
	 */
	public void setQuestionText(final String questionText) {
		this.questionText = questionText;
	}

	/**
	 * Gets the answer.
	 *
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * Sets the answer.
	 *
	 * @param answer the new answer
	 */
	public void setAnswer(final String answer) {
		this.answer = answer;
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
	public void setStatus(final Status status) {
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
	public void setInternalStatus(final Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	/**
	 * Gets the clarification needed from.
	 *
	 * @return the clarification needed from
	 */
	public ClarificationNeededFrom getClarificationNeededFrom() {
		return clarificationNeededFrom;
	}

	/**
	 * Sets the clarification needed from.
	 *
	 * @param clarificationNeededFrom the new clarification needed from
	 */
	public void setClarificationNeededFrom(
			final ClarificationNeededFrom clarificationNeededFrom) {
		this.clarificationNeededFrom = clarificationNeededFrom;
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
	public void setRemarks(final String remarks) {
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
	public void setEditedOn(final Date editedOn) {
		this.editedOn = editedOn;
	}
	
	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Sets the group.
	 *
	 * @param group the new group
	 */
	public void setGroup(final Group group) {
		this.group = group;
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
	public void setMinistry(final Ministry ministry) {
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
	public void setDepartment(final Department department) {
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
	public void setSubDepartment(final SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
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
	public void setRecommendationStatus(final Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}


    /**
     * Gets the mark as answered.
     *
     * @return the mark as answered
     */
    public Boolean getMarkAsAnswered() {
        return markAsAnswered;
    }


    /**
     * Sets the mark as answered.
     *
     * @param markAsAnswered the new mark as answered
     */
    public void setMarkAsAnswered(final Boolean markAsAnswered) {
        this.markAsAnswered = markAsAnswered;
    }

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	public List<ReferencedEntity> getReferencedEntities() {
		return referencedEntities;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBriefExplanation() {
		return briefExplanation;
	}

	public void setBriefExplanation(String briefExplanation) {
		this.briefExplanation = briefExplanation;
	}
}
