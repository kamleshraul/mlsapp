package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.BillAmendmentMotionRepository;
import org.mkcl.els.repository.MotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="billamendmentmotions")
@JsonIgnoreProperties(value={"houseType", "session", "type", "supportingMembers",
		"amendedBill", "sectionAmendments", "recommendationStatus", 
		"parent", "clubbedEntities", "referencedEntities"})
public class BillAmendmentMotion extends Device implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//=============== BASIC ATTRIBUTES ====================
	/** The house type. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="housetype_id")
	private HouseType houseType;

	/** The session. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;

	/** The type. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType type; 

	/** The number. */
	private Integer number;
	
	/** The primary member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="billamendmentmotions_supportingmembers",
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
	private List<SupportingMember> supportingMembers;
	
	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;

	/** The creation date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	/** The created by. */
	@Column(length=1000)
	private String createdBy;

	/**** The clerk name ****/
	private String dataEnteredBy;

	/** The edited on. */
	@Temporal(TemporalType.TIMESTAMP)
	@JoinColumn(name="editedon")
	private Date editedOn; 

	/** The edited by. */
	@Column(length=1000)
	private String editedBy;

	/** The edited as. */
	@Column(length=1000)
	private String editedAs;
	
	/** The subject. */
	@Column(length=30000)
	private String subject;

	/**** The subject. ****/
	@Column(length=30000)
	private String revisedSubject;
	
	/** The amended bill. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="amendedbill_id")
	private Bill amendedBill;
	
	/** The amendment. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billamendmentmotions_sectionamendments",
    joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="sectionamendment_id", referencedColumnName="id")})
    private List<SectionAmendment> sectionAmendments;
    
    /** ** The Status ***. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;

	/** ** The Internal Status ***. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internalstatus_id")
	private Status internalStatus;

	/**** The Recommendation Status. ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendationstatus_id")
	private Status recommendationStatus;
	
	/** ** Remarks ***. */
	@Column(length=30000)
	private String remarks;
	
	/** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private BillAmendmentMotion parent;

	/** The clubbed entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="billamendmentmotions_clubbingentities", 
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;

	/** The referenced entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="billamendmentmotions_referencedentities", 
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
	private List<ReferencedEntity> referencedEntities;
	
	/**** To be used in case of bulk submission and workflows****/
	private String workflowStarted;

	private String actor;

	private String localizedActorName;

	private String endFlag;

	private String level;

	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOn;	

	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOn;

	private boolean bulkSubmitted=false;

	private Long workflowDetailsId;

	private Integer file;

	private Integer fileIndex;

	private Boolean fileSent;
	
	/** The drafts. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="billamendmentmotions_drafts_association", 
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="billamendmentmotion_draft_id", referencedColumnName="id")})
	private List<BillAmendmentMotionDraft> drafts;
	
	/** The bill amendment motion repository. */
	@Autowired
	private transient BillAmendmentMotionRepository billAmendmentMotionRepository;

	/**** Constructors ****/
	public BillAmendmentMotion(HouseType houseType, Session session,
			DeviceType type, Integer number, Member primaryMember,
			List<SupportingMember> supportingMembers, Date submissionDate,
			Date creationDate, String createdBy, String dataEnteredBy,
			Date editedOn, String editedBy, String editedAs, String subject,
			String revisedSubject, Bill amendedBill,
			List<SectionAmendment> sectionAmendments, Status status,
			Status internalStatus, Status recommendationStatus, String remarks,
			BillAmendmentMotion parent, List<ClubbedEntity> clubbedEntities,
			List<ReferencedEntity> referencedEntities,
			List<BillAmendmentMotionDraft> drafts) {
		super();
		this.houseType = houseType;
		this.session = session;
		this.type = type;
		this.number = number;
		this.primaryMember = primaryMember;
		this.supportingMembers = supportingMembers;
		this.submissionDate = submissionDate;
		this.creationDate = creationDate;
		this.createdBy = createdBy;
		this.dataEnteredBy = dataEnteredBy;
		this.editedOn = editedOn;
		this.editedBy = editedBy;
		this.editedAs = editedAs;
		this.subject = subject;
		this.revisedSubject = revisedSubject;
		this.amendedBill = amendedBill;
		this.sectionAmendments = sectionAmendments;
		this.status = status;
		this.internalStatus = internalStatus;
		this.recommendationStatus = recommendationStatus;
		this.remarks = remarks;
		this.parent = parent;
		this.clubbedEntities = clubbedEntities;
		this.referencedEntities = referencedEntities;
		this.drafts = drafts;
	}

	public BillAmendmentMotion() {
		super();		
	}
	
	/**** Domain Methods ****/
	private static BillAmendmentMotionRepository getMotionRepository() {
		BillAmendmentMotionRepository motionRepository = new BillAmendmentMotion().billAmendmentMotionRepository;
		if (motionRepository == null) {
			throw new IllegalStateException(
			"MotionRepository has not been injected in Motion Domain");
		}
		return motionRepository;
	}
	
	public Motion simpleMerge() {
		Motion m = (Motion) super.merge();
		return m;
	}

	/**** Getters and Setters ****/
	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Member getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(Member primaryMember) {
		this.primaryMember = primaryMember;
	}

	public List<SupportingMember> getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(List<SupportingMember> supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRevisedSubject() {
		return revisedSubject;
	}

	public void setRevisedSubject(String revisedSubject) {
		this.revisedSubject = revisedSubject;
	}

	public Bill getAmendedBill() {
		return amendedBill;
	}

	public void setAmendedBill(Bill amendedBill) {
		this.amendedBill = amendedBill;
	}

	public List<SectionAmendment> getSectionAmendments() {
		return sectionAmendments;
	}

	public void setSectionAmendments(List<SectionAmendment> sectionAmendments) {
		this.sectionAmendments = sectionAmendments;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BillAmendmentMotion getParent() {
		return parent;
	}

	public void setParent(BillAmendmentMotion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public List<ReferencedEntity> getReferencedEntities() {
		return referencedEntities;
	}

	public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	public String getWorkflowStarted() {
		return workflowStarted;
	}

	public void setWorkflowStarted(String workflowStarted) {
		this.workflowStarted = workflowStarted;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getLocalizedActorName() {
		return localizedActorName;
	}

	public void setLocalizedActorName(String localizedActorName) {
		this.localizedActorName = localizedActorName;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Date getWorkflowStartedOn() {
		return workflowStartedOn;
	}

	public void setWorkflowStartedOn(Date workflowStartedOn) {
		this.workflowStartedOn = workflowStartedOn;
	}

	public Date getTaskReceivedOn() {
		return taskReceivedOn;
	}

	public void setTaskReceivedOn(Date taskReceivedOn) {
		this.taskReceivedOn = taskReceivedOn;
	}

	public boolean isBulkSubmitted() {
		return bulkSubmitted;
	}

	public void setBulkSubmitted(boolean bulkSubmitted) {
		this.bulkSubmitted = bulkSubmitted;
	}

	public Long getWorkflowDetailsId() {
		return workflowDetailsId;
	}

	public void setWorkflowDetailsId(Long workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}

	public Integer getFile() {
		return file;
	}

	public void setFile(Integer file) {
		this.file = file;
	}

	public Integer getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(Integer fileIndex) {
		this.fileIndex = fileIndex;
	}

	public Boolean getFileSent() {
		return fileSent;
	}

	public void setFileSent(Boolean fileSent) {
		this.fileSent = fileSent;
	}

	public List<BillAmendmentMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<BillAmendmentMotionDraft> drafts) {
		this.drafts = drafts;
	}
	
}
