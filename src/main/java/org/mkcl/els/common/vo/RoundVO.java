package org.mkcl.els.common.vo;

import java.util.List;

public class RoundVO {

	//=============== ATTRIBUTES ====================
	private int numberOfQuestionsInGivenRound;
	
	private String formattedNumberOfQuestionsInGivenRound;
	
	private String firstElementInGivenRound;
	
	private int firstElementInGivenRoundInt;
	
	private String lastElementInGivenRound;	
	
	private int lastElementInGivenRoundInt;
	
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

	public int getFirstElementInGivenRoundInt() {
		return firstElementInGivenRoundInt;
	}

	public void setFirstElementInGivenRoundInt(int firstElementInGivenRoundInt) {
		this.firstElementInGivenRoundInt = firstElementInGivenRoundInt;
	}

	public String getLastElementInGivenRound() {
		return lastElementInGivenRound;
	}

	public void setLastElementInGivenRound(String lastElementInGivenRound) {
		this.lastElementInGivenRound = lastElementInGivenRound;
	}

	public int getLastElementInGivenRoundInt() {
		return lastElementInGivenRoundInt;
	}

	public void setLastElementInGivenRoundInt(int lastElementInGivenRoundInt) {
		this.lastElementInGivenRoundInt = lastElementInGivenRoundInt;
	}

	public List<DeviceVO> getDeviceVOs() {
		return deviceVOs;
	}

	public void setDeviceVOs(List<DeviceVO> deviceVOs) {
		this.deviceVOs = deviceVOs;
	}
	
}
