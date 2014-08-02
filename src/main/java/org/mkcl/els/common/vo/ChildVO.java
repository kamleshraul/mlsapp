package org.mkcl.els.common.vo;

public class ChildVO {

	private Long id;
	
	private Integer orderNo;
	
	private Long version;
	
	private Long reporter;
	
	private Long proceeding;
	
	private String proceedingContent;
	
	private String primaryMember;
	
	private String primaryMemberName;
	
	private String primaryMemberDesignation;
	
	private String primaryMemberMinistry;
	
	private String primaryMemberSubDepartment;
	
	private String substituteMember;
	
	private String substituteMemberDesignation;
	
	private String substituteMemberMinistry;
	
	private String substituteMemberSubDepartment;
	
	private String pageHeading;
	
	private String mainHeading;
	
	private String chairperson;
	
	private String memberrole;
	
	private String publicRepresentative;
	
	private String publicRepresentativeDetails;
	
	private String constituency;
	
	private String deviceType;
	
	private String deviceId;
	
	private boolean isInterrupted;
	
	private boolean isConstituencyRequired;

	
	public ChildVO() {
		super();
		
	}


	public ChildVO(Long id, Integer orderNo, String proceedingContent,
			String primaryMember, String primaryMemberDesignation,
			String primaryMemberMinistry, String primaryMemberSubDepartment,
			String substituteMember, String substituteMemberDesignation,
			String substituteMemberMinistry,
			String substituteMemberSubDepartment, String pageHeading,
			String mainHeading, String chairperson, String memberrole,
			String publicRepresentative, String publicRepresentativeDetails,
			String constituency) {
		super();
		this.id = id;
		this.orderNo = orderNo;
		this.proceedingContent = proceedingContent;
		this.primaryMember = primaryMember;
		this.primaryMemberDesignation = primaryMemberDesignation;
		this.primaryMemberMinistry = primaryMemberMinistry;
		this.primaryMemberSubDepartment = primaryMemberSubDepartment;
		this.substituteMember = substituteMember;
		this.substituteMemberDesignation = substituteMemberDesignation;
		this.substituteMemberMinistry = substituteMemberMinistry;
		this.substituteMemberSubDepartment = substituteMemberSubDepartment;
		this.pageHeading = pageHeading;
		this.mainHeading = mainHeading;
		this.chairperson = chairperson;
		this.memberrole = memberrole;
		this.publicRepresentative = publicRepresentative;
		this.publicRepresentativeDetails = publicRepresentativeDetails;
		this.constituency = constituency;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Integer getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}


	public String getProceedingContent() {
		return proceedingContent;
	}


	public void setProceedingContent(String proceedingContent) {
		this.proceedingContent = proceedingContent;
	}


	public String getPrimaryMember() {
		return primaryMember;
	}


	public void setPrimaryMember(String primaryMember) {
		this.primaryMember = primaryMember;
	}


	public String getPrimaryMemberDesignation() {
		return primaryMemberDesignation;
	}


	public void setPrimaryMemberDesignation(String primaryMemberDesignation) {
		this.primaryMemberDesignation = primaryMemberDesignation;
	}


	public String getPrimaryMemberMinistry() {
		return primaryMemberMinistry;
	}


	public void setPrimaryMemberMinistry(String primaryMemberMinistry) {
		this.primaryMemberMinistry = primaryMemberMinistry;
	}


	public String getPrimaryMemberSubDepartment() {
		return primaryMemberSubDepartment;
	}


	public void setPrimaryMemberSubDepartment(String primaryMemberSubDepartment) {
		this.primaryMemberSubDepartment = primaryMemberSubDepartment;
	}


	public String getSubstituteMember() {
		return substituteMember;
	}


	public void setSubstituteMember(String substituteMember) {
		this.substituteMember = substituteMember;
	}


	public String getSubstituteMemberDesignation() {
		return substituteMemberDesignation;
	}


	public void setSubstituteMemberDesignation(String substituteMemberDesignation) {
		this.substituteMemberDesignation = substituteMemberDesignation;
	}


	public String getSubstituteMemberMinistry() {
		return substituteMemberMinistry;
	}


	public void setSubstituteMemberMinistry(String substituteMemberMinistry) {
		this.substituteMemberMinistry = substituteMemberMinistry;
	}


	public String getSubstituteMemberSubDepartment() {
		return substituteMemberSubDepartment;
	}


	public void setSubstituteMemberSubDepartment(
			String substituteMemberSubDepartment) {
		this.substituteMemberSubDepartment = substituteMemberSubDepartment;
	}


	public String getPageHeading() {
		return pageHeading;
	}


	public void setPageHeading(String pageHeading) {
		this.pageHeading = pageHeading;
	}


	public String getMainHeading() {
		return mainHeading;
	}


	public void setMainHeading(String mainHeading) {
		this.mainHeading = mainHeading;
	}


	public String getChairperson() {
		return chairperson;
	}


	public void setChairperson(String chairperson) {
		this.chairperson = chairperson;
	}


	public String getMemberrole() {
		return memberrole;
	}


	public void setMemberrole(String memberrole) {
		this.memberrole = memberrole;
	}


	public String getPublicRepresentative() {
		return publicRepresentative;
	}


	public void setPublicRepresentative(String publicRepresentative) {
		this.publicRepresentative = publicRepresentative;
	}


	public String getPublicRepresentativeDetails() {
		return publicRepresentativeDetails;
	}


	public void setPublicRepresentativeDetails(String publicRepresentativeDetails) {
		this.publicRepresentativeDetails = publicRepresentativeDetails;
	}


	public String getConstituency() {
		return constituency;
	}


	public void setConstituency(String constituency) {
		this.constituency = constituency;
	}


	public Long getReporter() {
		return reporter;
	}


	public void setReporter(Long reporter) {
		this.reporter = reporter;
	}


	public Long getProceeding() {
		return proceeding;
	}


	public void setProceeding(Long proceeding) {
		this.proceeding = proceeding;
	}


	public Long getVersion() {
		return version;
	}


	public void setVersion(Long version) {
		this.version = version;
	}


	public String getPrimaryMemberName() {
		return primaryMemberName;
	}


	public void setPrimaryMemberName(String primaryMemberName) {
		this.primaryMemberName = primaryMemberName;
	}


	public String getDeviceType() {
		return deviceType;
	}


	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}


	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	public boolean isInterrupted() {
		return isInterrupted;
	}


	public void setInterrupted(boolean isInterrupted) {
		this.isInterrupted = isInterrupted;
	}


	public boolean isConstituencyRequired() {
		return isConstituencyRequired;
	}


	public void setConstituencyRequired(boolean isConstituencyRequired) {
		this.isConstituencyRequired = isConstituencyRequired;
	}
	
	
	
	
}
