package org.mkcl.els.common.vo;

import java.util.List;

public class ClubbingResultVO {
	
	//=============== ATTRIBUTES ====================
	/** The result (true for Success & false for Failure or Error). */
	private boolean result;
	
	/** The clubbing success details. */
	private String clubSuccessDetails;
	
	/** The clubbing failure details. */
	private String clubFailureDetails;
	
	/** The applicable group of device types for clubbing. */
	private String whichDevice;
	
	private DeviceVO parentDevice;
	
	private List<DeviceVO> childDevices;
	

	//=============== GETTERS/SETTERS ===============
	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
	public String getClubSuccessDetails() {
		return clubSuccessDetails;
	}

	public void setClubSuccessDetails(String clubSuccessDetails) {
		this.clubSuccessDetails = clubSuccessDetails;
	}

	public String getClubFailureDetails() {
		return clubFailureDetails;
	}

	public void setClubFailureDetails(String clubFailureDetails) {
		this.clubFailureDetails = clubFailureDetails;
	}

	public String getWhichDevice() {
		return whichDevice;
	}

	public void setWhichDevice(String whichDevice) {
		this.whichDevice = whichDevice;
	}

	public DeviceVO getParentDevice() {
		return parentDevice;
	}

	public void setParentDevice(DeviceVO parentDevice) {
		this.parentDevice = parentDevice;
	}

	public List<DeviceVO> getChildDevices() {
		return childDevices;
	}

	public void setChildDevices(List<DeviceVO> childDevices) {
		this.childDevices = childDevices;
	}

}
