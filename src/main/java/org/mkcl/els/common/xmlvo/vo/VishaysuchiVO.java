package org.mkcl.els.common.vo;

import java.util.List;

public class VishaysuchiVO {
	/**** Attributes ****/
	private String type;
	
	private String value;
	
	private String catchWordIndex;
	
	private String memberID;
	
	private String deviceName;
	
	private String deviceType;
	
	private List<CatchwordHeadingVO> headings;
	
	private List<VishaysuchiDeviceVO> vishaysuchiDevices;
	
	/**** Constructor ****/
	public VishaysuchiVO(){
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<VishaysuchiDeviceVO> getVishaysuchiDevices() {
		return vishaysuchiDevices;
	}

	public void setVishaysuchiDevices(List<VishaysuchiDeviceVO> vishaysuchiDevices) {
		this.vishaysuchiDevices = vishaysuchiDevices;
	}

	public List<CatchwordHeadingVO> getHeadings() {
		return headings;
	}

	public void setHeadings(List<CatchwordHeadingVO> headings) {
		this.headings = headings;
	}

	public String getMemberID() {
		return memberID;
	}

	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getCatchWordIndex() {
		return catchWordIndex;
	}

	public void setCatchWordIndex(String catchWordIndex) {
		this.catchWordIndex = catchWordIndex;
	}
	
}
