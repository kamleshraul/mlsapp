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
    @JoinColumn(name="session_id")
    private Session session;

    /** The number. */
    private Integer number;

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

    /** The mark as answered. */
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
	 * Gets the language.
	 *
	 * @return the language
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language the new language
	 */
	public void setLanguage(final Language language) {
		this.language = language;
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
	 * Gets the edited by.
	 *
	 * @return the edited by
	 */
	public User getEditedBy() {
		return editedBy;
	}

	/**
	 * Sets the edited by.
	 *
	 * @param editedBy the new edited by
	 */
	public void setEditedBy(final User editedBy) {
		this.editedBy = editedBy;
	}

	/**
	 * Gets the edited as.
	 *
	 * @return the edited as
	 */
	public UserGroupType getEditedAs() {
		return editedAs;
	}

	/**
	 * Sets the edited as.
	 *
	 * @param editedAs the new edited as
	 */
	public void setEditedAs(final UserGroupType editedAs) {
		this.editedAs = editedAs;
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
	 * Gets the supporting members.
	 *
	 * @return the supporting members
	 */
	public List<SupportingMember> getSupportingMembers() {
		return supportingMembers;
	}

	/**
	 * Sets the supporting members.
	 *
	 * @param supportingMembers the new supporting members
	 */
	public void setSupportingMembers(final List<SupportingMember> supportingMembers) {
		this.supportingMembers = supportingMembers;
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
	 * Gets the referenced questions.
	 *
	 * @return the referenced questions
	 */
	public List<Question> getReferencedQuestions() {
		return referencedQuestions;
	}

	/**
	 * Sets the referenced questions.
	 *
	 * @param referencedQuestions the new referenced questions
	 */
	public void setReferencedQuestions(final List<Question> referencedQuestions) {
		this.referencedQuestions = referencedQuestions;
	}

	/**
	 * Gets the clubbings.
	 *
	 * @return the clubbings
	 */
	public List<Question> getClubbings() {
		return clubbings;
	}

	/**
	 * Sets the clubbings.
	 *
	 * @param clubbings the new clubbings
	 */
	public void setClubbings(final List<Question> clubbings) {
		this.clubbings = clubbings;
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


    /**
     * Gets the session.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }


    /**
     * Sets the session.
     *
     * @param session the new session
     */
    public void setSession(final Session session) {
        this.session = session;
    }


    /**
     * Gets the number.
     *
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }


    /**
     * Sets the number.
     *
     * @param number the new number
     */
    public void setNumber(final Integer number) {
        this.number = number;
    }

}
