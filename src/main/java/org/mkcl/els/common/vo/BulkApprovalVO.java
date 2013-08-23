package org.mkcl.els.common.vo;

public class BulkApprovalVO {
	
	private String id;

	private String deviceId;
	
	private String deviceNumber;
	
	private String deviceType;
	
	private String member;
	
	private String subject;
	
	private String lastRemark;
	
	private String lastRemarkBy;
	
	private String lastDecision;
	
	private String supportingMemberId;
	
	/**** PENDING<COMPLETED<TIMEOUT ****/
	private String currentStatus;
	

	public String getSupportingMemberId() {
		return supportingMemberId;
	}

	public void setSupportingMemberId(final String supportingMemberId) {
		this.supportingMemberId = supportingMemberId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(final String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(final String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getMember() {
		return member;
	}

	public void setMember(final String member) {
		this.member = member;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getLastRemark() {
		return lastRemark;
	}

	public void setLastRemark(final String lastRemark) {
		this.lastRemark = lastRemark;
	}

	public String getLastRemarkBy() {
		return lastRemarkBy;
	}

	public void setLastRemarkBy(final String lastRemarkBy) {
		this.lastRemarkBy = lastRemarkBy;
	}

	public String getLastDecision() {
		return lastDecision;
	}

	public void setLastDecision(final String lastDecision) {
		this.lastDecision = lastDecision;
	}

	public BulkApprovalVO(final String deviceId, 
			final String deviceNumber, 
			final String member,
			final String subject, 
			final String lastRemark, 
			final String lastRemarkBy,
			final String lastDecision) {
		super();
		this.deviceId = deviceId;
		this.deviceNumber = deviceNumber;
		this.member = member;
		this.subject = subject;
		this.lastRemark = lastRemark;
		this.lastRemarkBy = lastRemarkBy;
		this.lastDecision = lastDecision;
	}

	public BulkApprovalVO() {
		super();
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setCurrentStatus(final String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setDeviceType(final String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceType() {
		return deviceType;
	}
		
}
