/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.QuestionSearchVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;

/**
 * The Class QuestionSearchVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class DeviceSearchVO {

	/** The id. */
	private Long id;

	/** The number. */
	private String number;

	/** The subject or title. */
	private String subject;

	/** The device content. */
	private String deviceContent;
	
	/** The status. */
	private String status;
	
	/** The device type. */
	private String deviceType;
	
	/** The device type type. */
	private String deviceTypeType;
	
	/** * The Session ***. */
	private Long sessionId;
	
	/** * The Session Year ***. */
	private String sessionYear;
	
	/** ** The Session Type ***. */
	private String sessionType;	
	
	private String group;
	
	private String formattedGroup;
	
	private String ministry;
	
	private String formattedMinistry;
	
	private String department;
	
	private String subDepartment;
	
	private String formattedSubDepartment;
	
	private String classification;
	
	private String statusType;
	
	private String answer;
	
	private String formattedParentNumber;
	
	private String formattedClubbedNumbers;
	//===========added for portlet proceedings 
	private String primaryMember;
	
	private String formattedPrimaryMember;
	
	private String[] supportingMembers;
	
	private String[] formattedSupportingMembers;
	
	private String chartAnsweringDate;
	
	private String discussionDate;
	
	private String yaadiDate;
	
	private String yaadiNumber;
	
	private String actor;	
	
	private String ballotStatus;
	
	private String onlineStatus;
	
	private List<MasterVO> revisions; 
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 *
	 * @param number the new number
	 */
	public void setNumber(final String number) {
		this.number = number;
	}

	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 *
	 * @param subject the new subject
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the device content.
	 *
	 * @return the device content
	 */
	public String getDeviceContent() {
		return deviceContent;
	}

	/**
	 * Sets the device content.
	 *
	 * @param deviceContent the new device content
	 */
	public void setDeviceContent(final String deviceContent) {
		this.deviceContent = deviceContent;
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(final String status) {
		this.status = status;
	}
	
	/**
	 * Gets the device type.
	 *
	 * @return the device type
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * Sets the device type.
	 *
	 * @param deviceType the new device type
	 */
	public void setDeviceType(final String deviceType) {
		this.deviceType = deviceType;
	}
	
	/**
	 * @return the deviceTypeType
	 */
	public String getDeviceTypeType() {
		return deviceTypeType;
	}

	/**
	 * @param deviceTypeType the deviceTypeType to set
	 */
	public void setDeviceTypeType(String deviceTypeType) {
		this.deviceTypeType = deviceTypeType;
	}

	/**
	 * @return the sessionId
	 */
	public Long getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Gets the session year.
	 *
	 * @return the session year
	 */
	public String getSessionYear() {
		return sessionYear;
	}

	/**
	 * Sets the session year.
	 *
	 * @param sessionYear the new session year
	 */
	public void setSessionYear(final String sessionYear) {
		this.sessionYear = sessionYear;
	}

	/**
	 * Gets the session type.
	 *
	 * @return the session type
	 */
	public String getSessionType() {
		return sessionType;
	}

	/**
	 * Sets the session type.
	 *
	 * @param sessionType the new session type
	 */
	public void setSessionType(final String sessionType) {
		this.sessionType = sessionType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(final String group) {
		this.group = group;
	}

	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}

	public String getMinistry() {
		return ministry;
	}

	public String getFormattedMinistry() {
		return formattedMinistry;
	}

	public void setFormattedMinistry(String formattedMinistry) {
		this.formattedMinistry = formattedMinistry;
	}

	public void setDepartment(final String department) {
		this.department = department;
	}
	
	public String getFormattedSubDepartment() {
		return formattedSubDepartment;
	}

	public void setFormattedSubDepartment(String formattedSubDepartment) {
		this.formattedSubDepartment = formattedSubDepartment;
	}

	public String getDepartment() {
		return department;
	}

	public void setSubDepartment(final String subDepartment) {
		this.subDepartment = subDepartment;
	}

	public String getSubDepartment() {
		return subDepartment;
	}

	public void setClassification(final String classification) {
		this.classification = classification;
	}

	public String getClassification() {
		return classification;
	}

	public void setStatusType(final String statusType) {
		this.statusType = statusType;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setAnswer(String answer){
		this.answer = answer;
	}
	
	public String getAnswer(){
		return answer;
	}
	
	public void setFormattedGroup(final String formattedGroup) {
		this.formattedGroup = formattedGroup;
	}

	public String getFormattedGroup() {
		return formattedGroup;
	}

	public String getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(final String primaryMember) {
		this.primaryMember = primaryMember;
	}

	public String getFormattedPrimaryMember() {
		return formattedPrimaryMember;
	}

	public void setFormattedPrimaryMember(final String formattedPrimaryMember) {
		this.formattedPrimaryMember = formattedPrimaryMember;
	}

	public String[] getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(final String[] supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public String[] getFormattedSupportingMembers() {
		return formattedSupportingMembers;
	}

	public void setFormattedSupportingMembers(final String[] formattedSupportingMembers) {
		this.formattedSupportingMembers = formattedSupportingMembers;
	}

	public String getChartAnsweringDate() {
		return chartAnsweringDate;
	}

	public void setChartAnsweringDate(String chartAnsweringDate) {
		this.chartAnsweringDate = chartAnsweringDate;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getBallotStatus() {
		return ballotStatus;
	}

	public void setBallotStatus(String ballotStatus) {
		this.ballotStatus = ballotStatus;
	}

	public String getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(String discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getYaadiDate() {
		return yaadiDate;
	}

	public void setYaadiDate(String yaadiDate) {
		this.yaadiDate = yaadiDate;
	}

	public String getYaadiNumber() {
		return yaadiNumber;
	}

	public void setYaadiNumber(String yaadiNumber) {
		this.yaadiNumber = yaadiNumber;
	}

	public String getFormattedParentNumber() {
		return formattedParentNumber;
	}

	public void setFormattedParentNumber(String formattedParentNumber) {
		this.formattedParentNumber = formattedParentNumber;
	}

	public String getFormattedClubbedNumbers() {
		return formattedClubbedNumbers;
	}

	public void setFormattedClubbedNumbers(String formattedClubbedNumbers) {
		this.formattedClubbedNumbers = formattedClubbedNumbers;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public List<MasterVO> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<MasterVO> revisions) {
		this.revisions = revisions;
	}	
	
	
	
	
}
