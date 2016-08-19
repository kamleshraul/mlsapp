package org.mkcl.els.common.vo;

import java.util.Date;
import java.util.List;


public class ParentVO {
	
	private Long id;
	
	private String name;
	
	private String reporter;
	
	private String languageReporter;
	
	private String startDate;
	
	private String startTime;
	
	private String houseType;
	
	private String nextReporter;
	
	private List<ChildVO> childVOs;

	public ParentVO() {
		super();
	}

	public ParentVO(Long id, String name, String reporter,
			String languageReporter, String startDate, String startTime,
			List<ChildVO> childVOs) {
		super();
		this.id = id;
		this.name = name;
		this.reporter = reporter;
		this.languageReporter = languageReporter;
		this.startDate = startDate;
		this.startTime = startTime;
		this.childVOs = childVOs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public String getLanguageReporter() {
		return languageReporter;
	}

	public void setLanguageReporter(String languageReporter) {
		this.languageReporter = languageReporter;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate2) {
		this.startDate = startDate2;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime2) {
		this.startTime = startTime2;
	}

	public List<ChildVO> getChildVOs() {
		return childVOs;
	}

	public void setChildVOs(List<ChildVO> childVOs) {
		this.childVOs = childVOs;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getNextReporter() {
		return nextReporter;
	}

	public void setNextReporter(String nextReporter) {
		this.nextReporter = nextReporter;
	}
	
	
}
