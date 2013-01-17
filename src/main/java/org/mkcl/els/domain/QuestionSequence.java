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

	private static final long serialVersionUID = -5344796318972599026L;

	//=============== ATTRIBUTES ====================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="question_id")
	private Question question;
	
	private Integer round;
	
	private Integer sequenceNo;
	
	
	//=============== CONSTRUCTORS ==================
	public QuestionSequence() {
		super();
	}
	
	public QuestionSequence(final Question question, 
			final String locale) {
		super(locale);
		this.setQuestion(question);
	}

	
	//=============== GETTERS/SETTERS ===============
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
}
