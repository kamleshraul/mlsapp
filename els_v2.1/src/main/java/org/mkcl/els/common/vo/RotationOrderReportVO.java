package org.mkcl.els.common.vo;

import java.util.List;

public class RotationOrderReportVO {
	
	//=============== ATTRIBUTES ====================
	private String group;
	private List<String> answeringDates;
	private List<String> finalSubmissionDates;
	private List<String> ministries;
	private List<String> numberOfMinisteries;
	private int rowId;
	
	//=============== CONSTRUCTOR ====================
	public RotationOrderReportVO() {
		
	}
	
	//=============== GETTERS & SETTERS ====================
	public String getGroup() {
		return group;
	}
	public void setGroup(final String group) {
		this.group = group;
	}
	public List<String> getAnsweringDates() {
		return answeringDates;
	}
	public void setAnsweringDates(final List<String> answeringDates) {
		this.answeringDates = answeringDates;
	}
	public List<String> getFinalSubmissionDates() {
		return finalSubmissionDates;
	}
	public void setFinalSubmissionDates(final List<String> finalSubmissionDates) {
		this.finalSubmissionDates = finalSubmissionDates;
	}
	public List<String> getMinistries() {
		return ministries;
	}
	public List<String> getNumberOfMinisteries() {
		return numberOfMinisteries;
	}
	public void setNumberOfMinisteries(final List<String> numberOfMinisteries) {
		this.numberOfMinisteries = numberOfMinisteries;
	}
	public void setMinistries(final List<String> ministries) {
		this.ministries = ministries;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(final int rowId) {	
		this.rowId = rowId;
	}	
	
}
