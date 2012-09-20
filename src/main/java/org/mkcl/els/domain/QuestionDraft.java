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

    /** The type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="questionType_id")
    private DeviceType type;

    /** The supporting members. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questionsdrafts_supportingmembers",
            joinColumns = { @JoinColumn(name = "questiondraft_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "supportingmember_id", referencedColumnName = "id") })
    private List<Member> supportingMembers;

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
    @JoinColumn(name="department")
    private Department department;
    
    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;
    
    /** The answering date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date answeringDate;

    /** The subject. */
    @Column(length=3000)
    private String subject;

    /** The question text. */
    @Column(length=30000)
    private String questionText;
    
    /** The priority. */
    private Integer priority;

    /** The referenced questions. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questionsdrafts_references",
            joinColumns = { @JoinColumn(name = "questiondraft_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "reference_id", referencedColumnName = "id") })
    private List<Question> referencedQuestions;

    // The status of a Question should be tracked throughout it's lifecycle
    /** The status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;
    
    /** The edited by. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="editedby_id")
    private User editedBy;

    /** The edited as. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="editedas_id")
    private Role editedAs;

    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn;
    
    /** The language. */
    @ManyToOne
    @JoinColumn(name="language_id")
    private Language language;

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
     * Gets the supporting members.
     *
     * @return the supporting members
     */
    public List<Member> getSupportingMembers() {
        return supportingMembers;
    }

    /**
     * Sets the supporting members.
     *
     * @param supportingMembers the new supporting members
     */
    public void setSupportingMembers(final List<Member> supportingMembers) {
        this.supportingMembers = supportingMembers;
    }

    /**
     * Gets the answering date.
     *
     * @return the answering date
     */
    public Date getAnsweringDate() {
        return answeringDate;
    }

    /**
     * Sets the answering date.
     *
     * @param answeringDate the new answering date
     */
    public void setAnsweringDate(final Date answeringDate) {
        this.answeringDate = answeringDate;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(final Integer priority) {
        this.priority = priority;
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
	public void setLanguage(Language language) {
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
    public Role getEditedAs() {
        return editedAs;
    }

    /**
     * Sets the edited as.
     *
     * @param editedAs the new edited as
     */
    public void setEditedAs(final Role editedAs) {
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
 
}
