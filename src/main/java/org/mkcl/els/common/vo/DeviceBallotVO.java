package org.mkcl.els.common.vo;

public class DeviceBallotVO {
	
	// ---------------------------------Attributes-------------------------------------------------
	private Long id;
	private String number;
	private String formattedNumber;
	private String deviceName;
	private String deviceTypeName;
	private String deviceTypType;
	private String answeringDate;
	private String sequenceNumber;
	private String formattedSequenceNumber;	
	private String memberName;
	private String position;
	private String formattedPosition;
	private String discussionStatusName;
	private String discussionStatusType;
	private String subject;
	private String body;
	private String isSelected;
	
	// ---------------------------------Constructors-----------------------------------------------
	public DeviceBallotVO() {
		super();
	}

	public DeviceBallotVO(Long id, String number, String formattedNumber,
			String deviceName, String deviceTypeName, String deviceTypType,
			String answeringDate, String sequenceNumber,
			String formattedSequenceNumber, String memberName, String position,
			String formattedPosition, String discussionStatusName,
			String discussionStatusType, String subject, String body,
			String isSelected) {
		super();
		this.id = id;
		this.number = number;
		this.formattedNumber = formattedNumber;
		this.deviceName = deviceName;
		this.deviceTypeName = deviceTypeName;
		this.deviceTypType = deviceTypType;
		this.answeringDate = answeringDate;
		this.sequenceNumber = sequenceNumber;
		this.formattedSequenceNumber = formattedSequenceNumber;
		this.memberName = memberName;
		this.position = position;
		this.formattedPosition = formattedPosition;
		this.discussionStatusName = discussionStatusName;
		this.discussionStatusType = discussionStatusType;
		this.subject = subject;
		this.body = body;
		this.isSelected = isSelected;
	}

	// ------------------------------------------Getters/Setters-----------------------------------
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the formattedNumber
	 */
	public String getFormattedNumber() {
		return formattedNumber;
	}

	/**
	 * @param formattedNumber the formattedNumber to set
	 */
	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the deviceTypeName
	 */
	public String getDeviceTypeName() {
		return deviceTypeName;
	}

	/**
	 * @param deviceTypeName the deviceTypeName to set
	 */
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}

	/**
	 * @return the deviceTypType
	 */
	public String getDeviceTypType() {
		return deviceTypType;
	}

	/**
	 * @param deviceTypType the deviceTypType to set
	 */
	public void setDeviceTypType(String deviceTypType) {
		this.deviceTypType = deviceTypType;
	}

	/**
	 * @return the answeringDate
	 */
	public String getAnsweringDate() {
		return answeringDate;
	}

	/**
	 * @param answeringDate the answeringDate to set
	 */
	public void setAnsweringDate(String answeringDate) {
		this.answeringDate = answeringDate;
	}

	/**
	 * @return the sequenceNumber
	 */
	public String getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @return the formattedSequenceNumber
	 */
	public String getFormattedSequenceNumber() {
		return formattedSequenceNumber;
	}

	/**
	 * @param formattedSequenceNumber the formattedSequenceNumber to set
	 */
	public void setFormattedSequenceNumber(String formattedSequenceNumber) {
		this.formattedSequenceNumber = formattedSequenceNumber;
	}

	/**
	 * @return the memberName
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * @param memberName the memberName to set
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @return the formattedPosition
	 */
	public String getFormattedPosition() {
		return formattedPosition;
	}

	/**
	 * @param formattedPosition the formattedPosition to set
	 */
	public void setFormattedPosition(String formattedPosition) {
		this.formattedPosition = formattedPosition;
	}

	/**
	 * @return the discussionStatusName
	 */
	public String getDiscussionStatusName() {
		return discussionStatusName;
	}

	/**
	 * @param discussionStatusName the discussionStatusName to set
	 */
	public void setDiscussionStatusName(String discussionStatusName) {
		this.discussionStatusName = discussionStatusName;
	}

	/**
	 * @return the discussionStatusType
	 */
	public String getDiscussionStatusType() {
		return discussionStatusType;
	}

	/**
	 * @param discussionStatusType the discussionStatusType to set
	 */
	public void setDiscussionStatusType(String discussionStatusType) {
		this.discussionStatusType = discussionStatusType;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the isSelected
	 */
	public String getIsSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setIsSelected(String isSelected) {
		this.isSelected = isSelected;
	}
}
