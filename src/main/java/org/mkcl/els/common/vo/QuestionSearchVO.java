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
public class QuestionSearchVO {

	/** The id. */
	private Long id;

	/** The number. */
	private String number;

	/** The subject. */
	private String subject;

	/** The question text. */
	private String questionText;
	
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
	public void setId(Long id) {
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
	public void setNumber(String number) {
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
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the question text.
	 *
	 * @return the question text
	 */
	public String getQuestionText() {
		return questionText;
	}

	/**
	 * Sets the question text.
	 *
	 * @param questionText the new question text
	 */
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
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
	public void setStatus(String status) {
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
	public void setDeviceType(String deviceType) {
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
	public void setSessionYear(String sessionYear) {
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
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}

	public String getMinistry() {
		return ministry;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDepartment() {
		return department;
	}

	public void setSubDepartment(String subDepartment) {
		this.subDepartment = subDepartment;
	}

	public String getSubDepartment() {
		return subDepartment;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getClassification() {
		return classification;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setFormattedGroup(String formattedGroup) {
		this.formattedGroup = formattedGroup;
	}

	public String getFormattedGroup() {
		return formattedGroup;
	}

	
}
