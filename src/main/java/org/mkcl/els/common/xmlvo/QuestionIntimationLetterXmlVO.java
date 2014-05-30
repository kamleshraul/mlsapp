package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.MasterVO;

@XmlRootElement(name="QuestionData")
public class QuestionIntimationLetterXmlVO extends XmlVO {
	
	private String deviceType;
	
	private String number;
	
	private String houseTypeName;
	
	private String houseType;
	
	private String sessionPlace;
	
	private String sessionNumber;
	
	private String sessionYear;
	
	private String groupNumber;
	
	private String subject;
	
	private String questionText;
	
	private String primaryMemberName;
	
	private String primaryMemberDesignation;
	
	private String memberNames;
	
	private String hasMoreMembers;
	
	private String department;
	
	private String subDepartment;
	
	private String answeringDate;
	
	private String discussionDate;
	
	private String questionReferenceText;
	
	private String lastSendingDateToDepartment;
	
	private String rejectionReason;
	
	private String questionIndexesForClarification;
	
	private List<MasterVO> questionsAskedForClarification;
	
	private String userName;
	
	private String referredQuestionNumber;
	
	private String referredQuestionDeviceType;
	
	private String referredQuestionMemberName;
	
	private String referredQuestionAnsweringDate;
	
	private String reason;
	
	private String briefExplanation;	

	@XmlElement(name = "deviceType")
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@XmlElement(name = "number")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@XmlElement(name = "houseTypeName")
	public String getHouseTypeName() {
		return houseTypeName;
	}

	public void setHouseTypeName(String houseTypeName) {
		this.houseTypeName = houseTypeName;
	}

	@XmlElement(name = "houseType")
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	@XmlElement(name = "sessionPlace")
	public String getSessionPlace() {
		return sessionPlace;
	}

	public void setSessionPlace(String sessionPlace) {
		this.sessionPlace = sessionPlace;
	}

	@XmlElement(name = "sessionNumber")
	public String getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	@XmlElement(name = "sessionYear")
	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}

	@XmlElement(name = "groupNumber")
	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	@XmlElement(name = "subject")
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@XmlElement(name = "questionText")
	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	@XmlElement(name = "primaryMemberName")
	public String getPrimaryMemberName() {
		return primaryMemberName;
	}

	public void setPrimaryMemberName(String primaryMemberName) {
		this.primaryMemberName = primaryMemberName;
	}

	@XmlElement(name = "primaryMemberDesignation")
	public String getPrimaryMemberDesignation() {
		return primaryMemberDesignation;
	}

	public void setPrimaryMemberDesignation(String primaryMemberDesignation) {
		this.primaryMemberDesignation = primaryMemberDesignation;
	}

	@XmlElement(name = "memberNames")
	public String getMemberNames() {
		return memberNames;
	}

	public void setMemberNames(String memberNames) {
		this.memberNames = memberNames;
	}

	@XmlElement(name = "hasMoreMembers")
	public String getHasMoreMembers() {
		return hasMoreMembers;
	}

	public void setHasMoreMembers(String hasMoreMembers) {
		this.hasMoreMembers = hasMoreMembers;
	}

	@XmlElement(name = "department")
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@XmlElement(name = "subDepartment")
	public String getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(String subDepartment) {
		this.subDepartment = subDepartment;
	}

	@XmlElement(name = "answeringDate")
	public String getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(String answeringDate) {
		this.answeringDate = answeringDate;
	}

	@XmlElement(name = "discussionDate")
	public String getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(String discussionDate) {
		this.discussionDate = discussionDate;
	}

	@XmlElement(name = "questionReferenceText")
	public String getQuestionReferenceText() {
		return questionReferenceText;
	}

	public void setQuestionReferenceText(String questionReferenceText) {
		this.questionReferenceText = questionReferenceText;
	}

	@XmlElement(name = "lastSendingDateToDepartment")
	public String getLastSendingDateToDepartment() {
		return lastSendingDateToDepartment;
	}

	public void setLastSendingDateToDepartment(String lastSendingDateToDepartment) {
		this.lastSendingDateToDepartment = lastSendingDateToDepartment;
	}

	@XmlElement(name = "rejectionReason")
	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	@XmlElement(name = "questionIndexesForClarification")
	public String getQuestionIndexesForClarification() {
		return questionIndexesForClarification;
	}

	public void setQuestionIndexesForClarification(
			String questionIndexesForClarification) {
		this.questionIndexesForClarification = questionIndexesForClarification;
	}

	@XmlElementWrapper(name = "questionsAskedForClarification")
	@XmlElement(name = "questionAskedForClarification")
	public List<MasterVO> getQuestionsAskedForClarification() {
		return questionsAskedForClarification;
	}

	public void setQuestionsAskedForClarification(
			List<MasterVO> questionsAskedForClarification) {
		this.questionsAskedForClarification = questionsAskedForClarification;
	}

	@XmlElement(name = "userName")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name = "referredQuestionNumber")
	public String getReferredQuestionNumber() {
		return referredQuestionNumber;
	}

	public void setReferredQuestionNumber(String referredQuestionNumber) {
		this.referredQuestionNumber = referredQuestionNumber;
	}

	@XmlElement(name = "referredQuestionDeviceType")
	public String getReferredQuestionDeviceType() {
		return referredQuestionDeviceType;
	}

	public void setReferredQuestionDeviceType(String referredQuestionDeviceType) {
		this.referredQuestionDeviceType = referredQuestionDeviceType;
	}

	@XmlElement(name = "referredQuestionMemberName")
	public String getReferredQuestionMemberName() {
		return referredQuestionMemberName;
	}

	public void setReferredQuestionMemberName(String referredQuestionMemberName) {
		this.referredQuestionMemberName = referredQuestionMemberName;
	}

	@XmlElement(name = "referredQuestionAnsweringDate")
	public String getReferredQuestionAnsweringDate() {
		return referredQuestionAnsweringDate;
	}

	public void setReferredQuestionAnsweringDate(
			String referredQuestionAnsweringDate) {
		this.referredQuestionAnsweringDate = referredQuestionAnsweringDate;
	}

	@XmlElement(name = "reason")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@XmlElement(name = "bExplanation")
	public String getBriefExplanation() {
		return briefExplanation;
	}

	public void setBriefExplanation(String briefExplanation) {
		this.briefExplanation = briefExplanation;
	}	

}
