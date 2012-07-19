package org.mkcl.els.common.vo;

import java.util.List;

public class GroupVO {

	private List<MasterVO> ministries;

	private List<MasterVO> departments;

	private List<MasterVO> subDepartments;

	private List<Reference> answeringDates;

	public List<MasterVO> getMinistries() {
		return ministries;
	}

	public void setMinistries(final List<MasterVO> ministries) {
		this.ministries = ministries;
	}

	public List<MasterVO> getDepartments() {
		return departments;
	}

	public void setDepartments(final List<MasterVO> departments) {
		this.departments = departments;
	}


    public List<Reference> getAnsweringDates() {
        return answeringDates;
    }


    public void setAnsweringDates(final List<Reference> answeringDates) {
        this.answeringDates = answeringDates;
    }


    public List<MasterVO> getSubDepartments() {
        return subDepartments;
    }


    public void setSubDepartments(final List<MasterVO> subDepartments) {
        this.subDepartments = subDepartments;
    }
}
