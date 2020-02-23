package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.MasterVO;

@XmlRootElement(name="DiscussionMotionData")
public class DiscussionMotionIntimationLetterXmlVO extends XmlVO {
	
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
	
	private String discussionmotionText;
	
	private String primaryMemberName;
	
	private String primaryMemberDesignation;
	
	private String previousMinistryDesignation;
	
	private String memberNames;
	
	private String hasMoreMembers;
	
	private String department;
	
	private String subDepartment;
	
	private Boolean isSubDepartmentNameSameAsDepartmentName;
	
	private String previousDepartment;
	
	private String previousSubDepartment;
	
	private String answeringDate;
	
	private String nextAnsweringDate;
	
	private String discussionDate;
	
	private String ballotDate;
	
	private String discussionmotionReferenceText;
	
	private String factualPosition;
	
	private String lastSendingDateToDepartment;
	
	private String lastReceivingDateFromDepartment;
	
	private String rejectionReason;
	
	private String discussionmotionIndexesForClarification;
	
	private List<MasterVO> discussionmotionsAskedForClarification;
	
	private String userName;
	
	private String referredDiscussionMotionNumber;
	
	private String referredDiscussionMotionDeviceType;
	
	private String referredDiscussionMotionDeviceName;
	
	private String referredDiscussionMotionMemberName;
	
	private String referredDiscussionMotionAnsweringDate;
	
	private String reason;
	
	private String briefExplanation;
	
	private String referredDiscussionMotionYaadiNumber;
	
	private String referredDiscussionMotionYaadiLayingDate;
	
	private String daysCountForReceivingClarificationFromDepartment;
	
	private String daysCountForReceivingClarificationFromMember;
	
	private String daysCountForReceivingAnswerFromDepartment;
	
	private String remarksForClarification;
	
	private Boolean isRevisedDiscussionMotionTextWorkflow;
	
	private String referredDiscussionMotionYaadiPosition;

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

	@XmlElement(name = "discussionmotionText")
	public String getDiscussionMotionText() {
		return discussionmotionText;
	}

	public void setDiscussionMotionText(String discussionmotionText) {
		this.discussionmotionText = discussionmotionText;
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

	@XmlElement(name = "discussionmotionReferenceText")
	public String getDiscussionMotionReferenceText() {
		return discussionmotionReferenceText;
	}

	public void setDiscussionMotionReferenceText(String discussionmotionReferenceText) {
		this.discussionmotionReferenceText = discussionmotionReferenceText;
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

	@XmlElement(name = "discussionmotionIndexesForClarification")
	public String getDiscussionMotionIndexesForClarification() {
		return discussionmotionIndexesForClarification;
	}

	public void setDiscussionMotionIndexesForClarification(
			String discussionmotionIndexesForClarification) {
		this.discussionmotionIndexesForClarification = discussionmotionIndexesForClarification;
	}

	@XmlElementWrapper(name = "discussionmotionsAskedForClarification")
	@XmlElement(name = "discussionmotionAskedForClarification")
	public List<MasterVO> getDiscussionMotionsAskedForClarification() {
		return discussionmotionsAskedForClarification;
	}

	public void setDiscussionMotionsAskedForClarification(
			List<MasterVO> discussionmotionsAskedForClarification) {
		this.discussionmotionsAskedForClarification = discussionmotionsAskedForClarification;
	}

	@XmlElement(name = "userName")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name = "referredDiscussionMotionNumber")
	public String getReferredDiscussionMotionNumber() {
		return referredDiscussionMotionNumber;
	}

	public void setReferredDiscussionMotionNumber(String referredDiscussionMotionNumber) {
		this.referredDiscussionMotionNumber = referredDiscussionMotionNumber;
	}

	@XmlElement(name = "referredDiscussionMotionDeviceType")
	public String getReferredDiscussionMotionDeviceType() {
		return referredDiscussionMotionDeviceType;
	}

	public void setReferredDiscussionMotionDeviceType(String referredDiscussionMotionDeviceType) {
		this.referredDiscussionMotionDeviceType = referredDiscussionMotionDeviceType;
	}

	@XmlElement(name = "referredDiscussionMotionMemberName")
	public String getReferredDiscussionMotionMemberName() {
		return referredDiscussionMotionMemberName;
	}

	public void setReferredDiscussionMotionMemberName(String referredDiscussionMotionMemberName) {
		this.referredDiscussionMotionMemberName = referredDiscussionMotionMemberName;
	}

	@XmlElement(name = "referredDiscussionMotionAnsweringDate")
	public String getReferredDiscussionMotionAnsweringDate() {
		return referredDiscussionMotionAnsweringDate;
	}

	public void setReferredDiscussionMotionAnsweringDate(
			String referredDiscussionMotionAnsweringDate) {
		this.referredDiscussionMotionAnsweringDate = referredDiscussionMotionAnsweringDate;
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

	@XmlElement(name = "referredDiscussionMotionYaadiNumber")
	public String getReferredDiscussionMotionYaadiNumber() {
		return referredDiscussionMotionYaadiNumber;
	}

	public void setReferredDiscussionMotionYaadiNumber(String referredDiscussionMotionYaadiNumber) {
		this.referredDiscussionMotionYaadiNumber = referredDiscussionMotionYaadiNumber;
	}

	@XmlElement(name = "referredDiscussionMotionYaadiLayingDate")
	public String getReferredDiscussionMotionYaadiLayingDate() {
		return referredDiscussionMotionYaadiLayingDate;
	}

	public void setReferredDiscussionMotionYaadiLayingDate(String referredDiscussionMotionYaadiLayingDate) {
		this.referredDiscussionMotionYaadiLayingDate = referredDiscussionMotionYaadiLayingDate;
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

	@XmlElement(name = "isRevisedDiscussionMotionTextWorkflow")
	public Boolean getIsRevisedDiscussionMotionTextWorkflow() {
		return isRevisedDiscussionMotionTextWorkflow;
	}

	public void setIsRevisedDiscussionMotionTextWorkflow(Boolean isRevisedDiscussionMotionTextWorkflow) {
		this.isRevisedDiscussionMotionTextWorkflow = isRevisedDiscussionMotionTextWorkflow;
	}

	@XmlElement(name = "referredDiscussionMotionYaadiPosition")
	public String getReferredDiscussionMotionYaadiPosition() {
		return referredDiscussionMotionYaadiPosition;
	}

	public void setReferredDiscussionMotionYaadiPosition(String referredDiscussionMotionYaadiPosition) {
		this.referredDiscussionMotionYaadiPosition = referredDiscussionMotionYaadiPosition;
	}

	@XmlElement(name = "referredDiscussionMotionDeviceName")
	public String getReferredDiscussionMotionDeviceName() {
		return referredDiscussionMotionDeviceName;
	}

	public void setReferredDiscussionMotionDeviceName(String referredDiscussionMotionDeviceName) {
		this.referredDiscussionMotionDeviceName = referredDiscussionMotionDeviceName;
	}
	
	
	
}
