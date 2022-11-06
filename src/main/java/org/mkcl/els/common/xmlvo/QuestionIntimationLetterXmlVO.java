package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.MasterVO;

@XmlRootElement(name="QuestionData")
public class QuestionIntimationLetterXmlVO extends XmlVO {
	
	private String inwardLetterNumber;
	
	private String inwardLetterDate;
	
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
	
	private String previousMinistryDesignation;
	
	private String previousMinistryDisplayName;
	
	private String memberNames;
	
	private String hasMoreMembers;
	
	private String department;
	
	private String subDepartment;
	
	private String ministryDisplayName;
	
	private Boolean isSubDepartmentNameSameAsDepartmentName;
	
	private String previousDepartment;
	
	private String previousSubDepartment;
	
	private String answeringDate;
	
	private String nextAnsweringDate;
	
	private String discussionDate;
	
	private String ballotDate;
	
	private String questionReferenceText;
	
	private String factualPosition;
	
	private String lastSendingDateToDepartment;
	
	private String lastReceivingDateFromDepartment;
	
	private String rejectionReason;
	
	private String questionIndexesForClarification;
	
	private List<MasterVO> questionsAskedForClarification;
	
	private String userName;
	
	private String referredQuestionNumber;
	
	private String referredQuestionDeviceType;
	
	private String referredQuestionDeviceName;
	
	private String referredQuestionMemberName;
	
	private String referredQuestionAnsweringDate;
	
	private String reason;
	
	private String briefExplanation;
	
	private String referredQuestionYaadiNumber;
	
	private String referredQuestionYaadiLayingDate;
	
	private String daysCountForReceivingClarificationFromDepartment;
	
	private String daysCountForReceivingClarificationFromMember;
	
	private String daysCountForReceivingAnswerFromDepartment;
	
	private String remarksForClarification;
	
	private Boolean isRevisedQuestionTextWorkflow;
	
	private String referredQuestionYaadiPosition;

	@XmlElement(name = "inwardLetterNumber")
	public String getInwardLetterNumber() {
		return inwardLetterNumber;
	}

	public void setInwardLetterNumber(String inwardLetterNumber) {
		this.inwardLetterNumber = inwardLetterNumber;
	}

	@XmlElement(name = "inwardLetterDate")
	public String getInwardLetterDate() {
		return inwardLetterDate;
	}

	public void setInwardLetterDate(String inwardLetterDate) {
		this.inwardLetterDate = inwardLetterDate;
	}

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

	@XmlElement(name = "previousMinistryDesignation")
	public String getPreviousMinistryDesignation() {
		return previousMinistryDesignation;
	}

	public void setPreviousMinistryDesignation(String previousMinistryDesignation) {
		this.previousMinistryDesignation = previousMinistryDesignation;
	}

	@XmlElement(name = "previousMinistryDisplayName")
	public String getPreviousMinistryDisplayName() {
		return previousMinistryDisplayName;
	}

	public void setPreviousMinistryDisplayName(String previousMinistryDisplayName) {
		this.previousMinistryDisplayName = previousMinistryDisplayName;
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
	
	@XmlElement(name = "isSubDepartmentNameSameAsDepartmentName")
	public Boolean getIsSubDepartmentNameSameAsDepartmentName() {
		return isSubDepartmentNameSameAsDepartmentName;
	}

	public void setIsSubDepartmentNameSameAsDepartmentName(
			Boolean isSubDepartmentNameSameAsDepartmentName) {
		this.isSubDepartmentNameSameAsDepartmentName = isSubDepartmentNameSameAsDepartmentName;
	}

	@XmlElement(name = "ministryDisplayName")
	public String getMinistryDisplayName() {
		return ministryDisplayName;
	}

	public void setMinistryDisplayName(String ministryDisplayName) {
		this.ministryDisplayName = ministryDisplayName;
	}

	@XmlElement(name = "previousDepartment")
	public String getPreviousDepartment() {
		return previousDepartment;
	}

	public void setPreviousDepartment(String previousDepartment) {
		this.previousDepartment = previousDepartment;
	}

	@XmlElement(name = "previousSubDepartment")
	public String getPreviousSubDepartment() {
		return previousSubDepartment;
	}

	public void setPreviousSubDepartment(String previousSubDepartment) {
		this.previousSubDepartment = previousSubDepartment;
	}

	@XmlElement(name = "answeringDate")
	public String getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(String answeringDate) {
		this.answeringDate = answeringDate;
	}

	@XmlElement(name = "nextAnsweringDate")
	public String getNextAnsweringDate() {
		return nextAnsweringDate;
	}

	public void setNextAnsweringDate(String nextAnsweringDate) {
		this.nextAnsweringDate = nextAnsweringDate;
	}

	@XmlElement(name = "discussionDate")
	public String getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(String discussionDate) {
		this.discussionDate = discussionDate;
	}

	@XmlElement(name = "ballotDate")
	public String getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(String ballotDate) {
		this.ballotDate = ballotDate;
	}

	@XmlElement(name = "questionReferenceText")
	public String getQuestionReferenceText() {
		return questionReferenceText;
	}

	public void setQuestionReferenceText(String questionReferenceText) {
		this.questionReferenceText = questionReferenceText;
	}

	@XmlElement(name = "factualPosition")
	public String getFactualPosition() {
		return factualPosition;
	}

	public void setFactualPosition(String factualPosition) {
		this.factualPosition = factualPosition;
	}

	@XmlElement(name = "lastSendingDateToDepartment")
	public String getLastSendingDateToDepartment() {
		return lastSendingDateToDepartment;
	}

	public void setLastSendingDateToDepartment(String lastSendingDateToDepartment) {
		this.lastSendingDateToDepartment = lastSendingDateToDepartment;
	}

	@XmlElement(name = "lastReceivingDateFromDepartment")
	public String getLastReceivingDateFromDepartment() {
		return lastReceivingDateFromDepartment;
	}

	public void setLastReceivingDateFromDepartment(
			String lastReceivingDateFromDepartment) {
		this.lastReceivingDateFromDepartment = lastReceivingDateFromDepartment;
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

	@XmlElement(name = "referredQuestionYaadiNumber")
	public String getReferredQuestionYaadiNumber() {
		return referredQuestionYaadiNumber;
	}

	public void setReferredQuestionYaadiNumber(String referredQuestionYaadiNumber) {
		this.referredQuestionYaadiNumber = referredQuestionYaadiNumber;
	}

	@XmlElement(name = "referredQuestionYaadiLayingDate")
	public String getReferredQuestionYaadiLayingDate() {
		return referredQuestionYaadiLayingDate;
	}

	public void setReferredQuestionYaadiLayingDate(String referredQuestionYaadiLayingDate) {
		this.referredQuestionYaadiLayingDate = referredQuestionYaadiLayingDate;
	}

	@XmlElement(name = "daysCountForReceivingClarificationFromDepartment")
	public String getDaysCountForReceivingClarificationFromDepartment() {
		return daysCountForReceivingClarificationFromDepartment;
	}

	public void setDaysCountForReceivingClarificationFromDepartment(
			String daysCountForReceivingClarificationFromDepartment) {
		this.daysCountForReceivingClarificationFromDepartment = daysCountForReceivingClarificationFromDepartment;
	}

	@XmlElement(name = "daysCountForReceivingClarificationFromMember")
	public String getDaysCountForReceivingClarificationFromMember() {
		return daysCountForReceivingClarificationFromMember;
	}

	public void setDaysCountForReceivingClarificationFromMember(
			String daysCountForReceivingClarificationFromMember) {
		this.daysCountForReceivingClarificationFromMember = daysCountForReceivingClarificationFromMember;
	}

	@XmlElement(name = "daysCountForReceivingAnswerFromDepartment")
	public String getDaysCountForReceivingAnswerFromDepartment() {
		return daysCountForReceivingAnswerFromDepartment;
	}

	public void setDaysCountForReceivingAnswerFromDepartment(
			String daysCountForReceivingAnswerFromDepartment) {
		this.daysCountForReceivingAnswerFromDepartment = daysCountForReceivingAnswerFromDepartment;
	}

	@XmlElement(name = "remarksForClarification")
	public String getRemarksForClarification() {
		return remarksForClarification;
	}

	public void setRemarksForClarification(String remarks) {
		this.remarksForClarification = remarks;
	}

	@XmlElement(name = "isRevisedQuestionTextWorkflow")
	public Boolean getIsRevisedQuestionTextWorkflow() {
		return isRevisedQuestionTextWorkflow;
	}

	public void setIsRevisedQuestionTextWorkflow(Boolean isRevisedQuestionTextWorkflow) {
		this.isRevisedQuestionTextWorkflow = isRevisedQuestionTextWorkflow;
	}

	@XmlElement(name = "referredQuestionYaadiPosition")
	public String getReferredQuestionYaadiPosition() {
		return referredQuestionYaadiPosition;
	}

	public void setReferredQuestionYaadiPosition(String referredQuestionYaadiPosition) {
		this.referredQuestionYaadiPosition = referredQuestionYaadiPosition;
	}

	@XmlElement(name = "referredQuestionDeviceName")
	public String getReferredQuestionDeviceName() {
		return referredQuestionDeviceName;
	}

	public void setReferredQuestionDeviceName(String referredQuestionDeviceName) {
		this.referredQuestionDeviceName = referredQuestionDeviceName;
	}
	
	
	
}
