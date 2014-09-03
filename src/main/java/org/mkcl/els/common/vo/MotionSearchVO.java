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

/**
 * The Class QuestionSearchVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MotionSearchVO {

	/** The id. */
	private Long id;

	/** The number. */
	private String number;

	/** The subject. */
	private String title;

	/** The question text. */
	private String noticeContent;
	
	/** The status. */
	private String status;
	
	/** The device type. */
	private String deviceType;	
	
	/** * The Session Year ***. */
	private String sessionYear;
	
	/** ** The Session Type ***. */
	private String sessionType;	
	
	private String group;
	
	private String formattedGroup;
	
	private String ministry;
	
	private String department;
	
	private String subDepartment;
	
	private String classification;
	
	private String statusType;
	
	//===========added for portlet proceedings 
	private String primaryMember;
	
	private String formattedPrimaryMember;
	
	private String[] supportingMembers;
	
	private String[] formattedSupportingMembers;
	
	private String chartAnsweringDate;
	
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
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the subject.
	 *
	 * @param subject the new subject
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Gets the question text.
	 *
	 * @return the question text
	 */
	public String getNoticeContent() {
		return noticeContent;
	}

	/**
	 * Sets the question text.
	 *
	 * @param questionText the new question text
	 */
	public void setNoticeContent(final String noticeContent) {
		this.noticeContent = noticeContent;
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

	public void setDepartment(final String department) {
		this.department = department;
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

	

	
}
