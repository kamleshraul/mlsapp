package org.mkcl.els.common.vo;

import java.util.List;

public class CountsUsingGroupByReportVO {
	
	//=======================Attributes===========================
	String groupByFieldName;
	
	String groupByFieldValue;
	
	Long groupByFieldCount;
	
	List<CountsUsingGroupByReportVO> groupByInnerVOs;
	
	//=======================Constructors===========================
	public CountsUsingGroupByReportVO() {
		
	}

	//=======================Getters and Setters===========================
	public String getGroupByFieldName() {
		return groupByFieldName;
	}

	public void setGroupByFieldName(String groupByFieldName) {
		this.groupByFieldName = groupByFieldName;
	}

	public String getGroupByFieldValue() {
		return groupByFieldValue;
	}

	public void setGroupByFieldValue(String groupByFieldValue) {
		this.groupByFieldValue = groupByFieldValue;
	}

	public Long getGroupByFieldCount() {
		return groupByFieldCount;
	}

	public void setGroupByFieldCount(Long groupByFieldCount) {
		this.groupByFieldCount = groupByFieldCount;
	}

	public List<CountsUsingGroupByReportVO> getGroupByInnerVOs() {
		return groupByInnerVOs;
	}

	public void setGroupByInnerVOs(List<CountsUsingGroupByReportVO> groupByInnerVOs) {
		this.groupByInnerVOs = groupByInnerVOs;
	}
	
}