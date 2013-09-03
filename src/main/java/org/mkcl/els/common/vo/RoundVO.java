package org.mkcl.els.common.vo;

import java.util.List;

public class RoundVO {

	//=============== ATTRIBUTES ====================
	private int numberOfQuestionsInGivenRound;
	
	private String formattedNumberOfQuestionsInGivenRound;
	
	private String firstElementInGivenRound;
	
	private String lastElementInGivenRound;	
	
	private List<DeviceVO> deviceVOs;
	
	//=============== CONSTRUCTORS ==================
	public RoundVO() {
		super();
	}

	//=============== GETTERS/SETTERS ===============
	public int getNumberOfQuestionsInGivenRound() {
		return numberOfQuestionsInGivenRound;
	}

	public void setNumberOfQuestionsInGivenRound(
			int numberOfQuestionsInGivenRound) {
		this.numberOfQuestionsInGivenRound = numberOfQuestionsInGivenRound;
	}

	public String getFormattedNumberOfQuestionsInGivenRound() {
		return formattedNumberOfQuestionsInGivenRound;
	}

	public void setFormattedNumberOfQuestionsInGivenRound(
			String formattedNumberOfQuestionsInGivenRound) {
		this.formattedNumberOfQuestionsInGivenRound = formattedNumberOfQuestionsInGivenRound;
	}

	public String getFirstElementInGivenRound() {
		return firstElementInGivenRound;
	}

	public void setFirstElementInGivenRound(String firstElementInGivenRound) {
		this.firstElementInGivenRound = firstElementInGivenRound;
	}

	public String getLastElementInGivenRound() {
		return lastElementInGivenRound;
	}

	public void setLastElementInGivenRound(String lastElementInGivenRound) {
		this.lastElementInGivenRound = lastElementInGivenRound;
	}

	public List<DeviceVO> getDeviceVOs() {
		return deviceVOs;
	}

	public void setDeviceVOs(List<DeviceVO> deviceVOs) {
		this.deviceVOs = deviceVOs;
	}
	
}
