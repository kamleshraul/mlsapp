package org.mkcl.els.domain;

/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.CutMotion.java
 * Created On: Mar 27, 2012
 */

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.repository.CutMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="cutmotions")
@JsonIgnoreProperties({"houseType", "session", "deviceType", "answeringDate",
	"recommendationStatus", "supportingMembers", "department",
	"drafts", "parent", "clubbedEntities","referencedEntities","discussionDate",
	"noticeContent", "secondaryTitle", "subTitle", "creationDate"})
public class CutMotion extends Device implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/** houseType **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "housetype_id")
	private HouseType houseType;

	/** session **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private Session session;

	/** deviceType **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType deviceType;

	/** Number ***/
	private Integer number;
	
	/** Internal Number ***/
	private Integer internalNumber;

	/**amount to be deducted **/
	private Double amountToBeDeducted;

	/** Total Mount **/
	private Double totalAmoutDemanded;

	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;

	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	/**demandNumber **/
	private String demandNumber;

	/** itemNumber **/
	private Integer itemNumber; // ---- for supplementary

	/** pageNumber **/
	private String pageNumber;

	/** The created by. */
	@Column(length = 1000)
	private String createdBy;
	
	/** The dataEnteredBy by. */
	@Column(length = 1000)
	private String dataEnteredBy;

	/** The edited on. */
	@Temporal(TemporalType.TIMESTAMP)
	@JoinColumn(name = "editedon")
	private Date editedOn;

	/** The edited by. */
	@Column(length = 1000)
	private String editedBy;

	/** The edited as. */
	@Column(length = 1000)
	private String editedAs;

	/** The mainTitle */
	@Column(length = 2000)
	private String mainTitle;

	/** The revisedMainTitle */
	@Column(length = 1000)
	private String revisedMainTitle;

	/** The secondaryTitle */
	@Column(length = 1000)
	private String secondaryTitle; // ----------for budgetary only

	/** The revisedSecondaryTitle */
	@Column(length = 1000)
	private String revisedSecondaryTitle; // --------for budgetary only

	/** The subTitle */
	@Column(length = 1000)
	private String subTitle;

	/** The revisedSubTitle */
	@Column(length = 1000)
	private String revisedSubTitle;

	/** The noticeContent */
	@Column(length = 30000)
	private String noticeContent;

	/** The revisedNoticeContent */
	@Column(length = 30000)
	private String revisedNoticeContent;

	/** The rejectionReason */
	@Column(length = 10000)
	private String rejectionReason; //----will come from citation

	/** status **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private Status status;

	/** internalStatus **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "internalstatus_id")
	private Status internalStatus;

	/** recommendationStatus **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recommendationstatus_id")
	private Status recommendationStatus;

	/** The remarks. */
	@Column(length = 30000)
	private String remarks;

	/** primaryMember **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "cutmotions_supportingmembers", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "supportingmember_id", referencedColumnName = "id") })
	private List<SupportingMember> supportingMembers;

	/** The ministry. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ministry_id")
	private Ministry ministry;

	/** The department. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	/** The sub department. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subdepartment_id")
	private SubDepartment subDepartment;

	/**** DRAFTS ****/
	/** The drafts. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "cutmotions_drafts_association", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "cutmotion_draft_id", referencedColumnName = "id") })
	private List<CutMotionDraft> drafts;

	/**** Clubbing ****/
	/** The parent. */
	@ManyToOne(fetch = FetchType.LAZY)
	private CutMotion parent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "cutmotions_clubbingentities", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") },
	inverseJoinColumns = { @JoinColumn(name = "clubbed_entity_id", referencedColumnName = "id") })
	private List<ClubbedEntity> clubbedEntities;
	
	// =============== Referencing ====================//
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "cutmotions_referencedentities", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "referenced_entity_id", referencedColumnName = "id") })
	private List<ReferencedEntity> referencedEntities;

	/** The reply */
	@Column(length = 30000)
	private String reply;

	/** answering date **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date answeringDate;

	@Temporal(TemporalType.DATE)
	private Date discussionDate;
	
	
	private String workflowStarted;

	private String actor;

	private String localizedActorName;

	private String endFlag;

	private String level;

	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOn;

	private boolean bulkSubmitted = false;

	private Long workflowDetailsId;

	private Integer file;

	private Integer fileIndex;

	private Boolean fileSent;

	@Autowired
	private transient CutMotionRepository cutMotionRepository;
	
	/**** Constructor ****/
	public CutMotion() {
		super();
	}

	/**** Domain Methods ****/

	private static CutMotionRepository getCutMotionRepository() {
		CutMotionRepository cutMotionRepository = new CutMotion().cutMotionRepository;
		if (cutMotionRepository == null) {
			throw new IllegalStateException(
			"CutMotionRepository has not been injected in CutMotion Domain");
		}
		return cutMotionRepository;
	}

	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final CutMotion motion) {
		return getCutMotionRepository().findClubbedEntitiesByPosition(motion);
	}

	@Override
	public CutMotion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = CutMotion.assignCutMotionNo(this.getHouseType(),
							this.getSession(), this.getDeviceType(),this.getLocale());
					this.setNumber(number + 1);
					addCutMotionDraft();
					return (CutMotion)super.persist();
				}
			}
		}
		return (CutMotion) super.persist();
	}

	private static Integer assignCutMotionNo(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) {
		return getCutMotionRepository().assignCutMotionNo(houseType,session,type,locale);
	}

	private void addCutMotionDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_COMPLETE)) {
			CutMotionDraft draft = new CutMotionDraft();
			draft.setLocale(this.getLocale());
			draft.setRemarks(this.getRemarks());
			draft.setClubbedEntities(this.getClubbedEntities());
			draft.setReferencedEntities(this.getReferencedEntities());
			draft.setEditedAs(this.getEditedAs());
			draft.setEditedBy(this.getEditedBy());
			draft.setEditedOn(this.getEditedOn());	            
			draft.setMinistry(this.getMinistry());
			draft.setDepartment(this.getDepartment());
			draft.setSubDepartment(this.getSubDepartment());	            
			draft.setStatus(this.getStatus());
			draft.setInternalStatus(this.getInternalStatus());
			draft.setRecommendationStatus(this.getRecommendationStatus());
			
			if(this.getRevisedNoticeContent()!= null 
					&& this.getRevisedMainTitle() != null){
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setMainTitle(this.getRevisedMainTitle());
			}else if(this.getRevisedNoticeContent() != null){
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setMainTitle(this.getMainTitle());
			}else if(this.getRevisedMainTitle() != null){
				draft.setNoticeContent(this.getNoticeContent());
				draft.setMainTitle(this.getRevisedMainTitle());
			}else{
				draft.setNoticeContent(this.getNoticeContent());
				draft.setMainTitle(this.getMainTitle());
			}	  
			
			if(this.getDeviceType().equals(ApplicationConstants.CUTMOTIONS_BUDGETARY)){
				if(this.getRevisedSecondaryTitle() != null){
					draft.setSecondaryTitle(this.getRevisedSecondaryTitle());
				}else{
					draft.setSecondaryTitle(this.getSecondaryTitle());
				}
			}
			
			if(this.getRevisedSubTitle() != null){
				draft.setSubTitle(this.getRevisedSubTitle());
			}else{
				draft.setSubTitle(this.getSubTitle());
			}
			
			if(this.getId() != null) {
				CutMotion motion = CutMotion.findById(CutMotion.class, this.getId());
				List<CutMotionDraft> originalDrafts = motion.getDrafts();
				if(originalDrafts != null){
					originalDrafts.add(draft);
				}
				else{
					originalDrafts = new ArrayList<CutMotionDraft>();
					originalDrafts.add(draft);
				}
				this.setDrafts(originalDrafts);
			}
			else {
				List<CutMotionDraft> originalDrafts = new ArrayList<CutMotionDraft>();
				originalDrafts.add(draft);
				this.setDrafts(originalDrafts);
			}
		}
	}

	@Override
	public CutMotion merge() {
		CutMotion motion = null;
		if(this.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = CutMotion.assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(),this.getLocale());
					this.setNumber(number + 1);
					addCutMotionDraft();
					motion = (CutMotion) super.merge();
				}
			}else {
				CutMotion oldMotion = CutMotion.findById(CutMotion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				if(this.getReferencedEntities() == null){
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addCutMotionDraft();
				motion = (CutMotion) super.merge();
			}
		}
		
		if(motion != null) {
			return motion;
		}else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_INCOMPLETE) 
					|| this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_COMPLETE)) {
				return (CutMotion) super.merge();
			}else {
				CutMotion oldMotion = CutMotion.findById(CutMotion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}	
				if(this.getReferencedEntities() == null){
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addCutMotionDraft();
				return (CutMotion) super.merge();
			}
		}
	}

	public CutMotion simpleMerge() {
		CutMotion m = (CutMotion) super.merge();
		return m;
	}

	public static List<RevisionHistoryVO> getRevisions(final Long cutMotionId, final String locale) {
		return getCutMotionRepository().getRevisions(cutMotionId, locale);
	}

	public static List<CutMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType cutMotionType,
			final Integer itemsCount,
			final String locale) {
		return getCutMotionRepository().findAllByMember(session, primaryMember, cutMotionType,itemsCount, locale);
	}

	public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}	

	public static List<CutMotion> findAllByStatus(final Session session,
			final DeviceType cutMotionType,
			final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		return getCutMotionRepository().findAllByStatus(session, cutMotionType, internalStatus, itemsCount, locale);
	}	

	public CutMotionDraft findLatestDraft() {
		List<CutMotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			return drafts.get(size);            
		}
		return null;
	}

	public CutMotionDraft findPreviousDraft() {
		List<CutMotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			if(size > 1) {
				return drafts.get(size-1);
			}
		}
		return null;
	}

	public static Reference findCurrentFile(final CutMotion domain) {
		return getCutMotionRepository().findCurrentFile(domain);
	}
	
	public static List<CutMotion> findAllByFile(final Session session,
			final DeviceType cutMotionType,
			final Integer file,
			final String locale) {
		return getCutMotionRepository().findAllByFile(session, cutMotionType, file, locale);
	}

	public static List<CutMotion> findBySessionDeviceTypeSubdepartment(final Session session,
			final DeviceType cutMotionType,
			final SubDepartment subDepartment,
			final String locale) {
		return getCutMotionRepository().findBySessionDeviceTypeSubdepartment(session, cutMotionType, subDepartment, locale);
	}
	
	public static Integer findMaxNumberBySubdepartment(final Session session,
			final DeviceType deviceType, final SubDepartment subDepartment, final String locale) {
		
		return getCutMotionRepository().findMaxNumberBySubdepartment(session, deviceType, subDepartment, locale);
	}	
	
	public static int findHighestFileNo(final Session session,
			final DeviceType cutMotionType,
			final String locale) {
		return getCutMotionRepository().findHighestFileNo(session, cutMotionType, locale);
	}
	
	public static CutMotion getMotion(final Long sessionId, final Long deviceTypeId, final Integer dNumber, final String locale) {
		return getCutMotionRepository().getMotion(sessionId,deviceTypeId,dNumber,locale);
	}
	
	public static List<CutMotion> findFinalizedCutMotions(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment,
			final Status status,
			final String sortOrder,
			final String locale) {
		return getCutMotionRepository().findFinalizedCutMotionsByDepartment(session, deviceType, subDepartment, status, sortOrder, locale);
	}
	
	public static Boolean assignCutMotionNumberByDepartment(final Session session,
			final DeviceType deviceType,
			final SubDepartment subDepartment,
			final String locale) {
		
		/**** Assign number to admitted cutmotions ****/
		boolean admittedMotionUpdated = false;
		Status admitted = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_ADMISSION, locale);
		int currentAdmissionCount = 0;
		Integer intCurrentAdmissionCount = CutMotion.findHighestNumberByStatusDepartment(session, deviceType, subDepartment, admitted, locale);
		if(intCurrentAdmissionCount != null){
			currentAdmissionCount = intCurrentAdmissionCount.intValue();
		}
		
		List<CutMotion> admittedCutMotions = CutMotion.findFinalizedCutMotions(session, deviceType, subDepartment, admitted, ApplicationConstants.ASC, locale);
		int admissionCounter = 0;
		for(CutMotion cm : admittedCutMotions){
			if(cm.getInternalNumber() == null){
				++admissionCounter;
				cm.setInternalNumber(currentAdmissionCount + admissionCounter);
				cm.simpleMerge();
				admittedMotionUpdated = true;
			}
		}
		
		/**** Assign number to rejected cutmotions ****/
		boolean rejectedMotionUpdated = false;
		int currentRejectionCount = 0;
		String reassign = null;
		Status rejected = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_REJECTION, locale);
		CustomParameter csptReassignRejectionNumbers = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_REASSIGN_REJECTION_NUMBER, "");
		if(csptReassignRejectionNumbers != null && csptReassignRejectionNumbers.getValue() != null && !csptReassignRejectionNumbers.getValue().isEmpty()){
			reassign = csptReassignRejectionNumbers.getValue();
		}
		List<CutMotion> rejectedCutMotions = CutMotion.findFinalizedCutMotions(session, deviceType, subDepartment, rejected, ApplicationConstants.ASC, locale);
		Integer intCurrentRejectionCount = null;
		if(reassign != null && !reassign.isEmpty() && reassign.equals("yes")){
			currentRejectionCount = currentAdmissionCount + admissionCounter;
		}else{
			intCurrentRejectionCount = CutMotion.findHighestNumberByStatusDepartment(session, deviceType, subDepartment, rejected, locale);
			if(intCurrentRejectionCount != null){
				currentRejectionCount = intCurrentRejectionCount.intValue();
			}
		}
		
		int rejectionCounter = 0;		
		for(CutMotion cm : rejectedCutMotions){
			++rejectionCounter;
			cm.setInternalNumber(currentRejectionCount + rejectionCounter);
			cm.simpleMerge();
			rejectedMotionUpdated = true;
		}
		
		if(admittedMotionUpdated && rejectedMotionUpdated){
			return true;
		}
		
		return false;
	}
	
	private static Integer findHighestNumberByStatusDepartment(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment,
			final Status status, final 
			String locale) {
		return getCutMotionRepository().findHighestNumberByStatusDepartment(session, deviceType, subDepartment, status, locale);
	}

	/**** Getter Setters ****/
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

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getInternalNumber() {
		return internalNumber;
	}

	public void setInternalNumber(Integer internalNumber) {
		this.internalNumber = internalNumber;
	}

	public Double getAmountToBeDeducted() {
		return amountToBeDeducted;
	}

	public void setAmountToBeDeducted(Double amountToBeDeducted) {
		this.amountToBeDeducted = amountToBeDeducted;
	}

	public Double getTotalAmoutDemanded() {
		return totalAmoutDemanded;
	}

	public void setTotalAmoutDemanded(Double totalAmoutDemanded) {
		this.totalAmoutDemanded = totalAmoutDemanded;
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

	public String getDemandNumber() {
		return demandNumber;
	}

	public void setDemandNumber(String demandNumber) {
		this.demandNumber = demandNumber;
	}

	public Integer getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(Integer itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public String getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}

	public String getRevisedMainTitle() {
		return revisedMainTitle;
	}

	public void setRevisedMainTitle(String revisedMainTitle) {
		this.revisedMainTitle = revisedMainTitle;
	}

	public String getSecondaryTitle() {
		return secondaryTitle;
	}

	public void setSecondaryTitle(String secondaryTitle) {
		this.secondaryTitle = secondaryTitle;
	}

	public String getRevisedSecondaryTitle() {
		return revisedSecondaryTitle;
	}

	public void setRevisedSecondaryTitle(String revisedSecondaryTitle) {
		this.revisedSecondaryTitle = revisedSecondaryTitle;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getRevisedSubTitle() {
		return revisedSubTitle;
	}

	public void setRevisedSubTitle(String revisedSubTitle) {
		this.revisedSubTitle = revisedSubTitle;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getRevisedNoticeContent() {
		return revisedNoticeContent;
	}

	public void setRevisedNoticeContent(String revisedNoticeContent) {
		this.revisedNoticeContent = revisedNoticeContent;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public List<CutMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<CutMotionDraft> drafts) {
		this.drafts = drafts;
	}

	public CutMotion getParent() {
		return parent;
	}

	public void setParent(CutMotion parent) {
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

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}
}