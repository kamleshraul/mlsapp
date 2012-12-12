package org.mkcl.els.common.vo;

public class QuestionSequenceVO {

	//=============== ATTRIBUTES ====================
	private Long questionId;
	private Integer number;
	private Integer sequenceNo;
	
	
	//=============== CONSTRUCTORS ==================
	public QuestionSequenceVO() {
		super();
	}
	
	public QuestionSequenceVO(Long questionId, Integer number, Integer sequenceNo) {
		super();
		this.setQuestionId(questionId);
		this.setNumber(number);
		this.setSequenceNo(sequenceNo);
	}
	
	//=============== GETTERS/SETTERS ===============
	public Long getQuestionId() {
		return questionId;
	}
	
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public Integer getSequenceNo() {
		return sequenceNo;
	}
	
	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
}
