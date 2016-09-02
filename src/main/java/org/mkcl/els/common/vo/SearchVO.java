package org.mkcl.els.common.vo;

public class SearchVO {
	
	/** The id. */
	private Long id;

	/** The number. */
	private String number;
	
	/** The submission date. */
	private String submissionDate;

	/** The title. */
	private String title;
	
	/** The subject. */
	private String subject;

	/** The question text. */
	private String noticeContent;
	
	/** The content. **/
	private String content;
	
	/** The revised content. **/
	private String revisedContent;
	
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
	
	private String subDepartment;
	
	private String statusType;
	
	private String device;
	
	//===========added for portlet proceedings 
	private String primaryMember;
	
	private String formattedPrimaryMember;
	
	private String[] supportingMembers;
	
	private String[] formattedSupportingMembers;
	
	private String chartAnsweringDate;
	
	private String actor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRevisedContent() {
		return revisedContent;
	}

	public void setRevisedContent(String revisedContent) {
		this.revisedContent = revisedContent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getFormattedGroup() {
		return formattedGroup;
	}

	public void setFormattedGroup(String formattedGroup) {
		this.formattedGroup = formattedGroup;
	}

	public String getMinistry() {
		return ministry;
	}

	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}

	public String getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(String subDepartment) {
		this.subDepartment = subDepartment;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(String primaryMember) {
		this.primaryMember = primaryMember;
	}

	public String getFormattedPrimaryMember() {
		return formattedPrimaryMember;
	}

	public void setFormattedPrimaryMember(String formattedPrimaryMember) {
		this.formattedPrimaryMember = formattedPrimaryMember;
	}

	public String[] getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(String[] supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public String[] getFormattedSupportingMembers() {
		return formattedSupportingMembers;
	}

	public void setFormattedSupportingMembers(String[] formattedSupportingMembers) {
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

	

}