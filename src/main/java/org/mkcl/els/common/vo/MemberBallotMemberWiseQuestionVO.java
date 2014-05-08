package org.mkcl.els.common.vo;

public class MemberBallotMemberWiseQuestionVO {

	private String sno;
	
	private String questionNumber;
	
	private String questionSubject;
	
	private String questionReason;	
	
	private String statusType;
	
	private String statusTypeType;
	
	private String groupNumber;
	
	private String groupFormattedNumber;
	
	private String originalDeviceType;
	
	private String currentDeviceType;

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getSno() {
		return sno;
	}

	public void setQuestionNumber(final String questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionSubject(final String questionSubject) {
		this.questionSubject = questionSubject;
	}

	public String getQuestionSubject() {
		return questionSubject;
	}

	public void setQuestionReason(final String questionReason) {
		this.questionReason = questionReason;
	}

	public String getQuestionReason() {
		return questionReason;
	}	

	public void setStatusType(final String statusType) {
		this.statusType = statusType;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setGroupNumber(final String groupNumber) {
		this.groupNumber = groupNumber;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupFormattedNumber(final String groupFormattedNumber) {
		this.groupFormattedNumber = groupFormattedNumber;
	}

	public String getGroupFormattedNumber() {
		return groupFormattedNumber;
	}

	public void setStatusTypeType(final String statusTypeType) {
		this.statusTypeType = statusTypeType;
	}

	public String getStatusTypeType() {
		return statusTypeType;
	}

	public String getOriginalDeviceType() {
		return originalDeviceType;
	}

	public void setOriginalDeviceType(String originalDeviceType) {
		this.originalDeviceType = originalDeviceType;
	}

	public String getCurrentDeviceType() {
		return currentDeviceType;
	}

	public void setCurrentDeviceType(String currentDeviceType) {
		this.currentDeviceType = currentDeviceType;
	}
	
	
	
}
