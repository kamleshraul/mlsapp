/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionDraft.java
 * Created On: Sep 14, 2012
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
 * The Class QuestionDraft.
 *
 * @author Amit
 * @author Sandeep
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "question_drafts")
public class QuestionDraft extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    //---------------------------Basic Characteristics--------------------------
    /** The type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType type;

    /** The answering date. */
    @ManyToOne(fetch=FetchType.LAZY)
    private QuestionDates answeringDate;

    /** The language. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="language_id")
    private Language language;

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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="clarification_neededfrom_id")
    private ClarificationNeededFrom clarificationNeededFrom;

    @Column(length=30000)
    private String remarks;

    /** The edited by. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="editedby_id")
    private User editedBy;

    /** The edited as. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="editedastype_id")
    private UserGroupType editedAs;

    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn;

    private Boolean markAsAnswered;

    //---------------------------Primary and supporting members-----------------
    /** The supporting members. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="questionsdrafts_supportingmembers",
            joinColumns={@JoinColumn(name="questiondraft_id",
                    referencedColumnName="id")},
                    inverseJoinColumns={@JoinColumn(name="supportingmember_id",
                            referencedColumnName="id")})
                            private List<SupportingMember> supportingMembers;

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

    //---------------------------Referenced Questions---------------------------
    /** The referenced questions. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="questionsdrafts_references", joinColumns={@JoinColumn(name="questiondraft_id", referencedColumnName="id")}, inverseJoinColumns=

{@JoinColumn(name="reference_id", referencedColumnName="id")})
    private List<Question> referencedQuestions;

    //--------------------------Clubbing------------------------------------------
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="questionsdrafts_clubbing", joinColumns={@JoinColumn(name="primary_questiondraft_id", referencedColumnName="id")}, 

inverseJoinColumns={@JoinColumn(name="clubbed_question_id", referencedColumnName="id")})
    private List<Question> clubbings;

	public QuestionDraft() {
		super();
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(final DeviceType type) {
		this.type = type;
	}

	public QuestionDates getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(final QuestionDates answeringDate) {
		this.answeringDate = answeringDate;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(final Language language) {
		this.language = language;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(final String questionText) {
		this.questionText = questionText;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(final String answer) {
		this.answer = answer;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(final Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public ClarificationNeededFrom getClarificationNeededFrom() {
		return clarificationNeededFrom;
	}

	public void setClarificationNeededFrom(
			final ClarificationNeededFrom clarificationNeededFrom) {
		this.clarificationNeededFrom = clarificationNeededFrom;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	public User getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(final User editedBy) {
		this.editedBy = editedBy;
	}

	public UserGroupType getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(final UserGroupType editedAs) {
		this.editedAs = editedAs;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(final Date editedOn) {
		this.editedOn = editedOn;
	}

	public List<SupportingMember> getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(final List<SupportingMember> supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(final Group group) {
		this.group = group;
	}

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(final Ministry ministry) {
		this.ministry = ministry;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(final Department department) {
		this.department = department;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(final SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public List<Question> getReferencedQuestions() {
		return referencedQuestions;
	}

	public void setReferencedQuestions(final List<Question> referencedQuestions) {
		this.referencedQuestions = referencedQuestions;
	}

	public List<Question> getClubbings() {
		return clubbings;
	}

	public void setClubbings(final List<Question> clubbings) {
		this.clubbings = clubbings;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(final Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}


    public Boolean getMarkAsAnswered() {
        return markAsAnswered;
    }


    public void setMarkAsAnswered(final Boolean markAsAnswered) {
        this.markAsAnswered = markAsAnswered;
    }



}
