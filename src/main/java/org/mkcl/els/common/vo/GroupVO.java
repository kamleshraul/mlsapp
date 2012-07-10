package org.mkcl.els.common.vo;

import java.util.List;

public class GroupVO {

	private List<MasterVO> ministries;
	
	private List<MasterVO> departments;
	
	private List<MasterVO> answeringDates;

	public List<MasterVO> getMinistries() {
		return ministries;
	}

	public void setMinistries(List<MasterVO> ministries) {
		this.ministries = ministries;
	}

	public List<MasterVO> getDepartments() {
		return departments;
	}

	public void setDepartments(List<MasterVO> departments) {
		this.departments = departments;
	}

	public List<MasterVO> getAnsweringDates() {
		return answeringDates;
	}

	public void setAnsweringDates(List<MasterVO> answeringDates) {
		this.answeringDates = answeringDates;
	}	
}
