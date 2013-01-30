package org.mkcl.els.common.vo;

import java.util.List;

public class RotationOrderVO {
	//=============== ATTRIBUTES ====================
	public String group;
	public List<String> answeringDates;
	public List<String> finalSubmissionDates;
	public List<String> ministries;
	public List<String> numberOfMinisteries;
	public String rotationOrderHeader;
	public String rotationOrderFooter;
	private int rowId;
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public List<String> getAnsweringDates() {
		return answeringDates;
	}
	public void setAnsweringDates(List<String> answeringDates) {
		this.answeringDates = answeringDates;
	}
	public List<String> getFinalSubmissionDates() {
		return finalSubmissionDates;
	}
	public void setFinalSubmissionDates(List<String> finalSubmissionDates) {
		this.finalSubmissionDates = finalSubmissionDates;
	}
	public List<String> getMinistries() {
		return ministries;
	}
	public List<String> getNumberOfMinisteries() {
		return numberOfMinisteries;
	}
	public void setNumberOfMinisteries(List<String> numberOfMinisteries) {
		this.numberOfMinisteries = numberOfMinisteries;
	}
	public void setMinistries(List<String> ministries) {
		this.ministries = ministries;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	public String getRotationOrderHeader() {
		return rotationOrderHeader;
	}
	public void setRotationOrderHeader(String rotationOrderHeader) {
		this.rotationOrderHeader = rotationOrderHeader;
	}
	public String getRotationOrderFooter() {
		return rotationOrderFooter;
	}
	public void setRotationOrderFooter(String rotationOrderFooter) {
		this.rotationOrderFooter = rotationOrderFooter;
	}
		
	
}
