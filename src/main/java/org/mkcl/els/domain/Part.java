package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;


@Entity
@Configurable
@Table(name="parts")
@JsonIgnoreProperties({"primaryMember","primaryMemberDesignation","substituteMember","primaryMemberMinistry"
	,"reporter","chairPersonRole","proceeding","substituteMemberMinistry","substituteMemberDesignation","deviceType"})
public class Part  extends BaseDomain implements Serializable{
	
	/****Attributes****/
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

	private Integer orderNo;
	
	@OneToOne
	private Member	primaryMember;
	
	@OneToOne
	private Designation primaryMemberDesignation;
	
	@OneToOne
	private Ministry primaryMemberMinistry;
		
	@OneToOne
	private Member substituteMember;
	
	@OneToOne
	private Designation substituteMemberDesignation;
	
	@OneToOne
	private Ministry substituteMemberMinistry;
	
	@Column(length=30000)
	private String publicRepresentative	;
	
	@Column(length=30000)
	private String publicRepresentativeDetail;
	
	@OneToOne
	private Reporter reporter;
	
	private String mainHeading;
	
	private String pageHeading;
	
	private Date entryDate;
	
	private String chairPerson;
	
	@OneToOne
	private MemberRole chairPersonRole;
	
	@Column(length=30000)
	private String proceedingContent;
	
	@Column(length=30000)
	private String revisedContent;
	
	@ManyToOne
	private Proceeding proceeding;
	
	@ManyToOne
	private DeviceType deviceType;
	
	private Long deviceId;

	/****Constructors****/
	public Part() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public Part(Integer orderNo, Member primaryMember,
			Designation primaryMemberDesignation,
			Ministry primaryMemberMinistry, Member substituteMember,
			Designation substituteMemberDesignation,
			Ministry substituteMemberMinistry, String publicRepresentative,
			String publicRepresentativeDetail, Reporter reporter,
			String mainHeading, String pageHeading, Date entryDate,
			String chairPerson, MemberRole chairPersonRole,
			String proceedingContent, String revisedContent,
			Proceeding proceeding, DeviceType deviceType,Long deviceId) {
		super();
		this.orderNo = orderNo;
		this.primaryMember = primaryMember;
		this.primaryMemberDesignation = primaryMemberDesignation;
		this.primaryMemberMinistry = primaryMemberMinistry;
		this.substituteMember = substituteMember;
		this.substituteMemberDesignation = substituteMemberDesignation;
		this.substituteMemberMinistry = substituteMemberMinistry;
		this.publicRepresentative = publicRepresentative;
		this.publicRepresentativeDetail = publicRepresentativeDetail;
		this.reporter = reporter;
		this.mainHeading = mainHeading;
		this.pageHeading = pageHeading;
		this.entryDate = entryDate;
		this.chairPerson = chairPerson;
		this.chairPersonRole = chairPersonRole;
		this.proceedingContent = proceedingContent;
		this.revisedContent = revisedContent;
		this.proceeding = proceeding;
		this.deviceType=deviceType;
		this.deviceId=deviceId;
	}
	

	/****Domain Methods****/
	

	/****Getters and Setters****/
	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	public String getMainHeading() {
		return mainHeading;
	}

	public void setMainHeading(String mainHeading) {
		this.mainHeading = mainHeading;
	}

	public String getPageHeading() {
		return pageHeading;
	}

	public void setPageHeading(String pageHeading) {
		this.pageHeading = pageHeading;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getChairPerson() {
		return chairPerson;
	}

	public void setChairPerson(String chairPerson) {
		this.chairPerson = chairPerson;
	}

	public MemberRole getChairPersonRole() {
		return chairPersonRole;
	}

	public void setChairPersonRole(MemberRole chairPersonRole) {
		this.chairPersonRole = chairPersonRole;
	}

	public String getProceedingContent() {
		return proceedingContent;
	}

	public void setProceedingContent(String content) {
		this.proceedingContent = content;
	}

	public String getRevisedContent() {
		return revisedContent;
	}

	public void setRevisedContent(String revisedContent) {
		this.revisedContent = revisedContent;
	}

	public Proceeding getProceeding() {
		return proceeding;
	}

	public void setProceeding(Proceeding proceeding) {
		this.proceeding = proceeding;
	}


	public Member getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(Member primaryMember) {
		this.primaryMember = primaryMember;
	}

	public Designation getPrimaryMemberDesignation() {
		return primaryMemberDesignation;
	}

	public void setPrimaryMemberDesignation(Designation primaryMemberDesignation) {
		this.primaryMemberDesignation = primaryMemberDesignation;
	}

	public Ministry getPrimaryMemberMinistry() {
		return primaryMemberMinistry;
	}

	public void setPrimaryMemberMinistry(Ministry primaryMemberMinistry) {
		this.primaryMemberMinistry = primaryMemberMinistry;
	}

	public Member getSubstituteMember() {
		return substituteMember;
	}

	public void setSubstituteMember(Member substituteMember) {
		this.substituteMember = substituteMember;
	}

	public Designation getSubstituteMemberDesignation() {
		return substituteMemberDesignation;
	}

	public void setSubstituteMemberDesignation(
			Designation substituteMemberDesignation) {
		this.substituteMemberDesignation = substituteMemberDesignation;
	}

	public Ministry getSubstituteMemberMinistry() {
		return substituteMemberMinistry;
	}

	public void setSubstituteMemberMinistry(Ministry substituteMemberMinistry) {
		this.substituteMemberMinistry = substituteMemberMinistry;
	}

	public String getPublicRepresentative() {
		return publicRepresentative;
	}

	public void setPublicRepresentative(String publicRepresentative) {
		this.publicRepresentative = publicRepresentative;
	}

	public String getPublicRepresentativeDetail() {
		return publicRepresentativeDetail;
	}

	public void setPublicRepresentativeDetail(String publicRepresentativeDetail) {
		this.publicRepresentativeDetail = publicRepresentativeDetail;
	}



	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	
	

}
