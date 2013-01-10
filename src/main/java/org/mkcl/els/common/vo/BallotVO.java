/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.BallotVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;

/**
 * The Class BallotVO.
 *
 * @author amitd
 * @since v1.0.0
 */
public class BallotVO {

	//=============== ATTRIBUTES ====================
	/** The member id. */
	private Long memberId;

	/** The member name. */
	private String memberName;

	/** The question sequence v os. */
	private List<QuestionSequenceVO> questionSequenceVOs;


	//=============== CONSTRUCTORS ==================
	/**
	 * Instantiates a new ballot vo.
	 */
	public BallotVO() {
		super();
	}

	/**
	 * Instantiates a new ballot vo.
	 *
	 * @param memberId the member id
	 * @param memberName the member name
	 */
	public BallotVO(final Long memberId, final String memberName) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
	}

	/**
	 * Instantiates a new ballot vo.
	 *
	 * @param memberId the member id
	 * @param memberName the member name
	 * @param questionSequenceVOs the question sequence v os
	 */
	public BallotVO(final Long memberId, final String memberName,
			final List<QuestionSequenceVO> questionSequenceVOs) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
		this.questionSequenceVOs = questionSequenceVOs;
	}


	//=============== GETTERS/SETTERS ===============
	/**
	 * Gets the member id.
	 *
	 * @return the member id
	 */
	public Long getMemberId() {
		return memberId;
	}

	/**
	 * Sets the member id.
	 *
	 * @param memberId the new member id
	 */
	public void setMemberId(final Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * Gets the member name.
	 *
	 * @return the member name
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * Sets the member name.
	 *
	 * @param memberName the new member name
	 */
	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}

	/**
	 * Gets the question sequence v os.
	 *
	 * @return the question sequence v os
	 */
	public List<QuestionSequenceVO> getQuestionSequenceVOs() {
		return questionSequenceVOs;
	}

	/**
	 * Sets the question sequence v os.
	 *
	 * @param questionSequenceVOs the new question sequence v os
	 */
	public void setQuestionSequenceVOs(final List<QuestionSequenceVO> questionSequenceVOs) {
		this.questionSequenceVOs = questionSequenceVOs;
	}
}
