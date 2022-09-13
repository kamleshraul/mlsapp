package org.mkcl.els.common.vo;

import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.MemberDepartment;

public class MemberMinisterVO {

	private String designation;
	
	private String ministry;
	
    private String ministryFromDate;
	 
    private String ministryToDate;
    
    private String oathDate;
    
	 
    private List<MemberDepartment> memberDepartments;

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getMinistry() {
		return ministry;
	}

	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}

	public String getMinistryFromDate() {
		return ministryFromDate;
	}

	public void setMinistryFromDate(String ministryFromDate) {
		this.ministryFromDate = ministryFromDate;
	}

	public String getMinistryToDate() {
		return ministryToDate;
	}

	public void setMinistryToDate(String ministryToDate) {
		this.ministryToDate = ministryToDate;
	}
	
	public String getOathDate() {
		return oathDate;
	}

	public void setOathDate(String oathDate) {
		this.oathDate = oathDate;
	}

	public List<MemberDepartment> getMemberDepartments() {
		return memberDepartments;
	}

	public void setMemberDepartments(List<MemberDepartment> memberDepartments) {
		this.memberDepartments = memberDepartments;
	}
    
    
	
}
