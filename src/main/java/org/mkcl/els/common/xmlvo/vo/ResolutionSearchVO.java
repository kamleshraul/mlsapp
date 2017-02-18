package org.mkcl.els.common.vo;

public class ResolutionSearchVO{
	/** The id. */
	private Long id;

	/** The number. */
	private String number;

	/** The subject. */
	private String subject;

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
	
	private String ministry;
	
	private String department;
	
	private String subDepartment;
	
	private String classification;
	
	private String statusType;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(final String number) {
		this.number = number;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(final String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(final String deviceType) {
		this.deviceType = deviceType;
	}

	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(final String sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(final String sessionType) {
		this.sessionType = sessionType;
	}

	public String getMinistry() {
		return ministry;
	}

	public void setMinistry(final String ministry) {
		this.ministry = ministry;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(final String department) {
		this.department = department;
	}

	public String getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(final String subDepartment) {
		this.subDepartment = subDepartment;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(final String classification) {
		this.classification = classification;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(final String statusType) {
		this.statusType = statusType;
	}
}
