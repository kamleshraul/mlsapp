package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.MasterVO;

@XmlRootElement(name="ResolutionData")
public class ResolutionIntimationLetterXmlVO extends XmlVO{
	
	private String deviceType;
	
	private String number;
	
	private String houseTypeName;
	
	private String houseType;
	
	private String sessionPlace;
	
	private String sessionNumber;
	
	private String sessionYear;
	
	private String subject;
	
	private String noticeContent;
	
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
	
	private String discussionDate;
	
	private String ballotDate;
	
	private String questionReferenceText;
	
	private String factualPosition;
	
	private String rejectionReason;
	
	private String questionIndexesForClarification;
	
	private List<MasterVO> questionsAskedForClarification;
	
	private String userName;

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getHouseTypeName() {
		return houseTypeName;
	}

	public void setHouseTypeName(String houseTypeName) {
		this.houseTypeName = houseTypeName;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getSessionPlace() {
		return sessionPlace;
	}

	public void setSessionPlace(String sessionPlace) {
		this.sessionPlace = sessionPlace;
	}

	public String getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getPrimaryMemberName() {
		return primaryMemberName;
	}

	public void setPrimaryMemberName(String primaryMemberName) {
		this.primaryMemberName = primaryMemberName;
	}

	public String getPrimaryMemberDesignation() {
		return primaryMemberDesignation;
	}

	public void setPrimaryMemberDesignation(String primaryMemberDesignation) {
		this.primaryMemberDesignation = primaryMemberDesignation;
	}

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

	public String getMemberNames() {
		return memberNames;
	}

	public void setMemberNames(String memberNames) {
		this.memberNames = memberNames;
	}

	public String getHasMoreMembers() {
		return hasMoreMembers;
	}

	public void setHasMoreMembers(String hasMoreMembers) {
		this.hasMoreMembers = hasMoreMembers;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(String subDepartment) {
		this.subDepartment = subDepartment;
	}

	@XmlElement(name = "ministryDisplayName")
	public String getMinistryDisplayName() {
		return ministryDisplayName;
	}

	public void setMinistryDisplayName(String ministryDisplayName) {
		this.ministryDisplayName = ministryDisplayName;
	}

	public Boolean getIsSubDepartmentNameSameAsDepartmentName() {
		return isSubDepartmentNameSameAsDepartmentName;
	}

	public void setIsSubDepartmentNameSameAsDepartmentName(
			Boolean isSubDepartmentNameSameAsDepartmentName) {
		this.isSubDepartmentNameSameAsDepartmentName = isSubDepartmentNameSameAsDepartmentName;
	}

	public String getPreviousDepartment() {
		return previousDepartment;
	}

	public void setPreviousDepartment(String previousDepartment) {
		this.previousDepartment = previousDepartment;
	}

	public String getPreviousSubDepartment() {
		return previousSubDepartment;
	}

	public void setPreviousSubDepartment(String previousSubDepartment) {
		this.previousSubDepartment = previousSubDepartment;
	}

	public String getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(String discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(String ballotDate) {
		this.ballotDate = ballotDate;
	}

	public String getQuestionReferenceText() {
		return questionReferenceText;
	}

	public void setQuestionReferenceText(String questionReferenceText) {
		this.questionReferenceText = questionReferenceText;
	}

	public String getFactualPosition() {
		return factualPosition;
	}

	public void setFactualPosition(String factualPosition) {
		this.factualPosition = factualPosition;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getQuestionIndexesForClarification() {
		return questionIndexesForClarification;
	}

	public void setQuestionIndexesForClarification(
			String questionIndexesForClarification) {
		this.questionIndexesForClarification = questionIndexesForClarification;
	}

	public List<MasterVO> getQuestionsAskedForClarification() {
		return questionsAskedForClarification;
	}

	public void setQuestionsAskedForClarification(
			List<MasterVO> questionsAskedForClarification) {
		this.questionsAskedForClarification = questionsAskedForClarification;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
}
