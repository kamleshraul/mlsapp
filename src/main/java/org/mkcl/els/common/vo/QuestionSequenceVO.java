/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.QuestionSequenceVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionSequenceVO.
 *
 * @author amitd
 * @since v1.0.0
 */
public class QuestionSequenceVO {

	//=============== ATTRIBUTES ====================
	/** The question id. */
	private Long questionId;

	/** The number. */
	private Integer number;

	/** The sequence no. */
	private Integer sequenceNo;

	private Long memberId;


	//=============== CONSTRUCTORS ==================
	/**
	 * Instantiates a new question sequence vo.
	 */
	public QuestionSequenceVO() {
		super();
	}

	/**
	 * Instantiates a new question sequence vo.
	 *
	 * @param questionId the question id
	 * @param number the number
	 * @param sequenceNo the sequence no
	 */
	public QuestionSequenceVO(final Long questionId, final Integer number, final Integer sequenceNo) {
		super();
		this.setQuestionId(questionId);
		this.setNumber(number);
		this.setSequenceNo(sequenceNo);
	}

	//=============== GETTERS/SETTERS ===============
	/**
	 * Gets the question id.
	 *
	 * @return the question id
	 */
	public Long getQuestionId() {
		return questionId;
	}

	/**
	 * Sets the question id.
	 *
	 * @param questionId the new question id
	 */
	public void setQuestionId(final Long questionId) {
		this.questionId = questionId;
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
	
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(final Long memberId) {
		this.memberId = memberId;
	}
}
