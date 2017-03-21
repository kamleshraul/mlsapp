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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	/** The formatted number. */
	private String formattedNumber;

	/** The sequence no. */
	private Integer sequenceNo;
	
	private Long memberId;
	
	private String questionreferenceText;


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
	
	public QuestionSequenceVO(final Long questionId, final Integer number, final String formattedNumber, final Integer sequenceNo) {
		super();
		this.setQuestionId(questionId);
		this.setNumber(number);
		this.setFormattedNumber(formattedNumber);
		this.setSequenceNo(sequenceNo);
	}

	public QuestionSequenceVO(Long questionId, Integer number,
			String formattedNumber, Integer sequenceNo, Long memberId,
			String questionreferenceText) {
		super();
		this.questionId = questionId;
		this.number = number;
		this.formattedNumber = formattedNumber;
		this.sequenceNo = sequenceNo;
		this.memberId = memberId;
		this.questionreferenceText = questionreferenceText;
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

	public String getFormattedNumber() {
		return formattedNumber;
	}

	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
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

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getQuestionreferenceText() {
		return questionreferenceText;
	}

	public void setQuestionreferenceText(String questionreferenceText) {
		this.questionreferenceText = questionreferenceText;
	}

	public static void sortBySequenceNumber(List<QuestionSequenceVO> questionSequenceVOs) {
		class QuestionSequenceVOComparator implements Comparator<QuestionSequenceVO> {
		    @Override
		    public int compare(QuestionSequenceVO qs1, QuestionSequenceVO qs2) {
		       return qs1.getSequenceNo().compareTo(qs2.getSequenceNo());         
		    }
		}
		Collections.sort(questionSequenceVOs, new QuestionSequenceVOComparator());
	}
}