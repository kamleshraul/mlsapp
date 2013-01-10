/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionSequence.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * A simple POJO which holds Question object and an integer sequence number.
 * This POJO is used for question balloting.
 *
 * @author amitd
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="question_sequences")
public class QuestionSequence extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5344796318972599026L;

	//=============== ATTRIBUTES ====================
	/** The question. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="question_id")
	private Question question;

	/** The sequence no. */
	private Integer sequenceNo;


	//=============== CONSTRUCTORS ==================
	/**
	 * Instantiates a new question sequence.
	 */
	public QuestionSequence() {
		super();
	}

	/**
	 * Instantiates a new question sequence.
	 *
	 * @param locale the locale
	 */
	public QuestionSequence(final String locale) {
		super(locale);
	}

	/**
	 * Instantiates a new question sequence.
	 *
	 * @param question the question
	 * @param sequenceNo the sequence no
	 */
	public QuestionSequence(final Question question, final Integer sequenceNo) {
		super();
		this.question = question;
		this.sequenceNo = sequenceNo;
	}

	//=============== GETTERS/SETTERS ===============
	/**
	 * Gets the question.
	 *
	 * @return the question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets the question.
	 *
	 * @param question the new question
	 */
	public void setQuestion(final Question question) {
		this.question = question;
	}

	/**
	 * Gets the sequence no.
	 *
	 * @return the sequence no
	 */
	public Integer getSequenceNo() {
		return sequenceNo;
	}

	/**
	 * Sets the sequence no.
	 *
	 * @param sequenceNo the new sequence no
	 */
	public void setSequenceNo(final Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

}
