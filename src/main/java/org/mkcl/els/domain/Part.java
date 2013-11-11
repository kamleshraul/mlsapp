package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Column(length=30000)
	private String editedContent;
	
	@ManyToOne
	private Proceeding proceeding;
	
	@ManyToOne
	private DeviceType deviceType;
	
	private Long deviceId;
	
	private Boolean isInterrupted;
	
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="parts_drafts_association", 
    		joinColumns={@JoinColumn(name="part_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="part_draft_id", referencedColumnName="id")})
	private List<PartDraft> partDrafts;	
	
	

	/****Constructors****/
	public Part() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Autowired
	private transient PartRepository partRepository;

	
	

	public Part(Integer orderNo, Member primaryMember,
			Designation primaryMemberDesignation,
			Ministry primaryMemberMinistry, Member substituteMember,
			Designation substituteMemberDesignation,
			Ministry substituteMemberMinistry, String publicRepresentative,
			String publicRepresentativeDetail, Reporter reporter,
			String mainHeading, String pageHeading, Date entryDate,
			String chairPerson, MemberRole chairPersonRole,
			String proceedingContent, String revisedContent,
			String editedContent, Proceeding proceeding, DeviceType deviceType,
			Long deviceId, Boolean isInterrupted, List<PartDraft> partDrafts) {
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
		this.editedContent = editedContent;
		this.proceeding = proceeding;
		this.deviceType = deviceType;
		this.deviceId = deviceId;
		this.isInterrupted = isInterrupted;
		this.partDrafts = partDrafts;
	}

	/****Domain Methods****/
	public static PartRepository getPartRepository(){
		PartRepository partRepo = new Part().partRepository;
		if (partRepo == null) {
            throw new IllegalStateException(
                    "PartRepository has not been injected in Part Domain");
        }
        return partRepo;
	}
		
	public static List<PartDraft> findRevision(final Long partId, final String locale){
		return getPartRepository().findRevision(partId, locale);
	}
	
	public static List<Part> findInterruptedProceedingInRoster(Roster roster,
			Locale locale) {
		return getPartRepository().findInterruptedProceedingInRoster(roster,locale);
	}
	
	public static List<Part> findAllPartOfProceedingOfRoster(final Roster roster, final Boolean usePrimaryMember, final String locale) throws ELSException{
		return getPartRepository().findAllPartOfProceedingOfRoster(roster, usePrimaryMember, locale);
	}
	
	public static List<Part> findAllPartRosterSearchTerm(final Roster roster, String searchTerm, final String locale) throws ELSException{
		return getPartRepository().findAllPartRosterSearchTerm(roster, searchTerm, locale);
	}
	
	public static List findAllEligibleForReplacement(final Roster roster, String searchTerm, String replaceTerm, String locale){
		return getPartRepository().findAllEligibleForReplacement(roster, searchTerm, replaceTerm, locale);
	}

	public static List<Member> findAllProceedingMembersOfRoster(final Roster roster, final String locale){
		return getPartRepository().findAllProceedingMembersOfRoster(roster, locale);
	}
	
	public static List<Part> findAllPartsOfMemberOfRoster(final Roster roster, final String locale){
		return getPartRepository().findAllPartsOfMemberOfRoster(roster, locale);
	}
		
	public static List<PartDraft> findAllNonWorkflowDraftsOfPart(final Part part, final String locale){
		return getPartRepository().findAllNonWorkflowDraftsOfPart(part, locale);
	}
	
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



	/**
	 * @return the partDrafts
	 */
	public List<PartDraft> getPartDrafts() {
		return partDrafts;
	}



	/**
	 * @param partDrafts the partDrafts to set
	 */
	public void setPartDrafts(List<PartDraft> partDrafts) {
		this.partDrafts = partDrafts;
	}



	/**
	 * @return the editedContent
	 */
	public String getEditedContent() {
		return editedContent;
	}
	
	
	/**
	 * @param editedContent the editedContent to set
	 */
	public void setEditedContent(String editedContent) {
		this.editedContent = editedContent;
	}

	public Boolean getIsInterrupted() {
		return isInterrupted;
	}

	public void setIsInterrupted(Boolean isInterrupted) {
		this.isInterrupted = isInterrupted;
	}
	
	
}
