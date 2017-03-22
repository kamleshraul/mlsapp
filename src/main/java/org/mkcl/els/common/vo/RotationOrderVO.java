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
	public String rotationOrderCover;
	public String rotationOrderFooter;
	private int rowId;
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
	public String getRotationOrderHeader() {
		return rotationOrderHeader;
	}
	public void setRotationOrderHeader(final String rotationOrderHeader) {
		this.rotationOrderHeader = rotationOrderHeader;
	}
	public String getRotationOrderFooter() {
		return rotationOrderFooter;
	}
	public void setRotationOrderFooter(final String rotationOrderFooter) {
		this.rotationOrderFooter = rotationOrderFooter;
	}
	public String getRotationOrderCover() {
		return rotationOrderCover;
	}
	public void setRotationOrderCover(final String rotationOrderCover) {
		this.rotationOrderCover = rotationOrderCover;
	}
		
	
}