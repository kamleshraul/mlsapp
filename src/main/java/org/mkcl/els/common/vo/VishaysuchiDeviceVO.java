package org.mkcl.els.common.vo;


public class VishaysuchiDeviceVO {
	
	/**** Attributes ****/
	private String deviceType;
	
	private String deviceName;
	
	private String partID;
	
	private CatchwordHeadingVO catchwordHeading;

	/**** Constructor ****/
	public VishaysuchiDeviceVO(){
		super();
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getPartID() {
		return partID;
	}

	public void setPartID(String partID) {
		this.partID = partID;
	}

	public CatchwordHeadingVO getCatchwordHeading() {
		return catchwordHeading;
	}

	public void setCatchwordHeading(CatchwordHeadingVO catchwordHeading) {
		this.catchwordHeading = catchwordHeading;
	}
}
