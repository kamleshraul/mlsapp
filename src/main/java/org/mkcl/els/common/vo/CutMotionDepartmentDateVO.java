package org.mkcl.els.common.vo;

import java.util.ArrayList;
import java.util.List;

public class CutMotionDepartmentDateVO {
	
	String discussionDate;
	
	String formattedDiscussionDate;
	
	String submissionEndDate;
	
	String formattedSubmissionEndDate;
	
	List<String[]> departments; //index_0: departmentId, index_1: departmentName, index_2: departmentPriority,

	public CutMotionDepartmentDateVO() {
		departments = new ArrayList<String[]>();
	}

	public String getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(String discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getFormattedDiscussionDate() {
		return formattedDiscussionDate;
	}

	public void setFormattedDiscussionDate(String formattedDiscussionDate) {
		this.formattedDiscussionDate = formattedDiscussionDate;
	}

	public String getSubmissionEndDate() {
		return submissionEndDate;
	}

	public void setSubmissionEndDate(String submissionEndDate) {
		this.submissionEndDate = submissionEndDate;
	}

	public String getFormattedSubmissionEndDate() {
		return formattedSubmissionEndDate;
	}

	public void setFormattedSubmissionEndDate(String formattedSubmissionEndDate) {
		this.formattedSubmissionEndDate = formattedSubmissionEndDate;
	}

	public List<String[]> getDepartments() {
		return departments;
	}

	public void setDepartments(List<String[]> departments) {
		this.departments = departments;
	}

}