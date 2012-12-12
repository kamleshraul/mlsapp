package org.mkcl.els.common.vo;

import java.util.List;

public class BallotVO {

	//=============== ATTRIBUTES ====================
	private Long memberId;
	private String memberName;
	private List<QuestionSequenceVO> questionSequenceVOs;
	
	
	//=============== CONSTRUCTORS ==================
	public BallotVO() {
		super();
	}
	
	public BallotVO(Long memberId, String memberName) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
	}

	public BallotVO(Long memberId, String memberName,
			List<QuestionSequenceVO> questionSequenceVOs) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
		this.questionSequenceVOs = questionSequenceVOs;
	}


	//=============== GETTERS/SETTERS ===============
	public Long getMemberId() {
		return memberId;
	}
	
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	public String getMemberName() {
		return memberName;
	}
	
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	public List<QuestionSequenceVO> getQuestionSequenceVOs() {
		return questionSequenceVOs;
	}
	
	public void setQuestionSequenceVOs(List<QuestionSequenceVO> questionSequenceVOs) {
		this.questionSequenceVOs = questionSequenceVOs;
	}
}
