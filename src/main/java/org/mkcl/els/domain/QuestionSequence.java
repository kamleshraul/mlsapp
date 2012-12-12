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
	
	private Integer sequenceNo;

	
	//=============== CONSTRUCTORS ==================
	public QuestionSequence() {
		super();
	}
	
	public QuestionSequence(final String locale) {
		super(locale);
	}

	public QuestionSequence(final Question question, final Integer sequenceNo) {
		super();
		this.question = question;
		this.sequenceNo = sequenceNo;
	}	
	
	//=============== GETTERS/SETTERS ===============
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(final Question question) {
		this.question = question;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(final Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
}
