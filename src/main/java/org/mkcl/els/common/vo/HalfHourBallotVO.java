package org.mkcl.els.common.vo;

public class HalfHourBallotVO {

	//=============== ATTRIBUTES ====================
	private String memberName;
	private Integer questionNumber;
	private String questionSubject;
	

	//=============== CONSTRUCTORS ==================
	public HalfHourBallotVO() {
		super();
	}
	
	public HalfHourBallotVO(String memberName, Integer questionNumber,
			String questionSubject) {
		super();
		this.memberName = memberName;
		this.questionNumber = questionNumber;
		this.questionSubject = questionSubject;
	}
	
	
	//=============== GETTERS/SETTERS ===============
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Integer getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(Integer questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getQuestionSubject() {
		return questionSubject;
	}

	public void setQuestionSubject(String questionSubject) {
		this.questionSubject = questionSubject;
	}

}
