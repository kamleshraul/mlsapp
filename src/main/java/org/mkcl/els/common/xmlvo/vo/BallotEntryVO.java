package org.mkcl.els.common.vo;

import java.util.Date;

public class BallotEntryVO {

	private Long memberId;
	
	private boolean attendance;
	
	private Integer round;
	
	private Integer position;
	
	private Integer choice;
	
	private Integer priority;
	
	private Integer deviceNumber;
	
	private Long deviceId;
	
	private Integer sequence;
	
	private Date submissionDate;
	
	private Date chartAnsweringDate;

	public BallotEntryVO() {
		super();
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public boolean isAttendance() {
		return attendance;
	}

	public void setAttendance(boolean attendance) {
		this.attendance = attendance;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getChoice() {
		return choice;
	}

	public void setChoice(Integer choice) {
		this.choice = choice;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	
	public Integer getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(Integer deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Date getChartAnsweringDate() {
		return chartAnsweringDate;
	}

	public void setChartAnsweringDate(Date chartAnsweringDate) {
		this.chartAnsweringDate = chartAnsweringDate;
	}		
}
