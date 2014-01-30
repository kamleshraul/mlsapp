package org.mkcl.els.common.vo;

public class CatchwordHeadingVO {
	
	/**** Attributes ****/
	private String catchWord;
	
	private String heading;
	
	private String deviceName;
	
	private String deviceType;
	
	
	/**** Constructor ****/
	public CatchwordHeadingVO(){
		super();
	}


	public String getCatchWord() {
		return catchWord;
	}


	public void setCatchWord(String catchWord) {
		this.catchWord = catchWord;
	}


	public String getHeading() {
		return heading;
	}


	public void setHeading(String headings) {
		this.heading = headings;
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
}
