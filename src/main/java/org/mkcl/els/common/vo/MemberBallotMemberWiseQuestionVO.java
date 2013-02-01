package org.mkcl.els.common.vo;

public class MemberBallotMemberWiseQuestionVO {

	private String sno;
	
	private String questionNumber;
	
	private String questionSubject;
	
	private String questionReason;	
	
	private String statusType;
	
	private String groupNumber;
	
	private String groupFormattedNumber;

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getSno() {
		return sno;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionSubject(String questionSubject) {
		this.questionSubject = questionSubject;
	}

	public String getQuestionSubject() {
		return questionSubject;
	}

	public void setQuestionReason(String questionReason) {
		this.questionReason = questionReason;
	}

	public String getQuestionReason() {
		return questionReason;
	}	

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupFormattedNumber(String groupFormattedNumber) {
		this.groupFormattedNumber = groupFormattedNumber;
	}

	public String getGroupFormattedNumber() {
		return groupFormattedNumber;
	}
	
}
