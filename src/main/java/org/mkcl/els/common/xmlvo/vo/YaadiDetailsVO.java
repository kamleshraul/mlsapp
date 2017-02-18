package org.mkcl.els.common.vo;

import java.util.List;

public class YaadiDetailsVO {
	
	// ---------------------------------Attributes-------------------------------------------------
	private Long id;
	
	private Integer number;
	
	private String formattedNumber;
	
	private String layingDate;
	
	private Long layingStatusId;
	
	private String layingStatusType;
	
	private List<DeviceVO> devices;
	
	
	// ---------------------------------Getters & Setters-------------------------------------------
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getFormattedNumber() {
		return formattedNumber;
	}

	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	public String getLayingDate() {
		return layingDate;
	}

	public void setLayingDate(String layingDate) {
		this.layingDate = layingDate;
	}

	public Long getLayingStatusId() {
		return layingStatusId;
	}

	public void setLayingStatusId(Long layingStatusId) {
		this.layingStatusId = layingStatusId;
	}

	public String getLayingStatusType() {
		return layingStatusType;
	}

	public void setLayingStatusType(String layingStatusType) {
		this.layingStatusType = layingStatusType;
	}

	public List<DeviceVO> getDevices() {
		return devices;
	}

	public void setDevices(List<DeviceVO> devices) {
		this.devices = devices;
	}
	
}
