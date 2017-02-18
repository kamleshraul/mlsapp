/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.QuestionRevisionVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import javax.persistence.Column;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionRevisionVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class QuestionRevisionVO {
/*
 * This vo is used to contain the various changes made in a question by various actors
 * during the workflow.
 */
	/** The edited as. */
private String editedAs;

	/** The edited by. */
	private String editedBY;

	/** The edited on. */
	private String editedOn;

	/** The status. */
	private String status;

	/** The subject. */
	private String subject;

	/** The question. */
	private String question;

	/** The remarks. */
	private String remarks;
	
	//-------------------------For half hour discussion from question-
    /** The reason. */
    private String reason;
    
    private String briefExplanation;


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
	public void setEditedAs(final String editedAs) {
		this.editedAs = editedAs;
	}

	/**
	 * Gets the edited by.
	 *
	 * @return the edited by
	 */
	public String getEditedBY() {
		return editedBY;
	}

	/**
	 * Sets the edited by.
	 *
	 * @param editedBY the new edited by
	 */
	public void setEditedBY(final String editedBY) {
		this.editedBY = editedBY;
	}

	/**
	 * Gets the edited on.
	 *
	 * @return the edited on
	 */
	public String getEditedOn() {
		return editedOn;
	}

	/**
	 * Sets the edited on.
	 *
	 * @param editedOn the new edited on
	 */
	public void setEditedOn(final String editedOn) {
		this.editedOn = editedOn;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(final String status) {
		this.status = status;
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
	 * Gets the question.
	 *
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * Sets the question.
	 *
	 * @param question the new question
	 */
	public void setQuestion(final String question) {
		this.question = question;
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

	public String getReason() {
		return reason;
	}

	public void setReason(final String reason) {
		this.reason = reason;
	}

	public String getBriefExplanation() {
		return briefExplanation;
	}

	public void setBriefExplanation(final String briefExplanation) {
		this.briefExplanation = briefExplanation;
	}
}
