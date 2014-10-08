package org.mkcl.els.domain;

/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.EventMotion.java
 * Created On: Sep 22, 2014
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
import org.mkcl.els.repository.EventMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author vikasg
 *
 */
@Configurable
@Entity
@Table(name = "eventmotions")
@JsonIgnoreProperties({"houseType", "session", "deviceType", 
	"recommendationStatus", "member", "supportingMembers", 
	"drafts", "parent", "clubbedEntities", "referencedEntities", 
	"discussionDate", "eventDescription"})
public class EventMotion extends Device implements Serializable {

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
	
	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;

	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

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

	/** The eventTitle as. */
	@Column(length = 1000)
	private String eventTitle;
	
	/** The revisedEventTitle as. */
	@Column(length = 1000)
	private String revisedEventTitle;
	
	/** The noticeContent */
	@Column(length = 30000)
	private String description;

	/** The revisedNoticeContent */
	@Column(length = 30000)
	private String revisedDescription;

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
	private Member member;
	
	private String designationOfPerson;
	
	private String tenureOfPerson;
	
	private String constituencyOfPerson;
	
	private String collectorReport;
	
	@Temporal(TemporalType.DATE)
	private Date eventDate;
	
	@Column(length=10000)
	private String eventReason;
	
	private Boolean isHouseAdjourned;

	/** The supporting members. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "eventmotions_supportingmembers", 
	joinColumns = { @JoinColumn(name = "eventmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "supportingmember_id", referencedColumnName = "id") })
	private List<SupportingMember> supportingMembers;

	/**** DRAFTS ****/
	/** The drafts. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "eventmotions_drafts_association", 
	joinColumns = { @JoinColumn(name = "eventmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "eventmotion_draft_id", referencedColumnName = "id") })
	private List<EventMotionDraft> drafts;

	/**** Clubbing ****/
	/** The parent. */
	@ManyToOne(fetch = FetchType.LAZY)
	private EventMotion parent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "eventmotions_clubbingentities", 
	joinColumns = { @JoinColumn(name = "eventmotion_id", referencedColumnName = "id") },
	inverseJoinColumns = { @JoinColumn(name = "clubbed_entity_id", referencedColumnName = "id") })
	private List<ClubbedEntity> clubbedEntities;
	
	// =============== Referencing ====================//
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "eventmotions_referencedentities", 
	joinColumns = { @JoinColumn(name = "eventmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "referenced_entity_id", referencedColumnName = "id") })
	private List<ReferencedEntity> referencedEntities;

	
	@Temporal(TemporalType.DATE)
	private Date discussionDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOn;
	
	private String workflowStarted;

	private String actor;

	private String localizedActorName;

	private String endFlag;

	private String level;

	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOn;

	private boolean bulkSubmitted = false;

	private Long workflowDetailsId;

	private Integer file;

	private Integer fileIndex;

	private Boolean fileSent;

	@Autowired
	private transient EventMotionRepository eventMotionRepository;
	
	/**** Constructor ****/
	public EventMotion() {
		super();
	}

	/**** Domain Methods ****/

	private static EventMotionRepository getEventMotionRepository() {
		EventMotionRepository eventMotionRepository = new EventMotion().eventMotionRepository;
		if (eventMotionRepository == null) {
			throw new IllegalStateException(
			"EventMotionRepository has not been injected in EvetMotion Domain");
		}
		return eventMotionRepository;
	}

	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final EventMotion motion) {
		return null;
	}

	@Override
	public EventMotion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = EventMotion.assignEventMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(),this.getLocale());
					this.setNumber(number + 1);
					addEventMotionDraft();
					return (EventMotion)super.persist();
				}
			}
		}
		return (EventMotion) super.persist();
	}

	 public static Boolean isExist(final Integer number, final DeviceType deviceType, final Session session, final String locale) {
		 return getEventMotionRepository().isExist(number, deviceType, session, locale);
	 }
	
	private static Integer assignEventMotionNo(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) {
		return getEventMotionRepository().assignEventMotionNo(houseType,session,type,locale);
	}

	private void addEventMotionDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_COMPLETE)) {
			EventMotionDraft draft = new EventMotionDraft();
			draft.setLocale(this.getLocale());
			draft.setRemarks(this.getRemarks());
			draft.setClubbedEntities(this.getClubbedEntities());
			draft.setEditedAs(this.getEditedAs());
			draft.setEditedBy(this.getEditedBy());
			draft.setEditedOn(this.getEditedOn());	                     
			draft.setStatus(this.getStatus());
			draft.setInternalStatus(this.getInternalStatus());
			draft.setRecommendationStatus(this.getRecommendationStatus());
			
			if(this.getRevisedDescription()!= null 
					&& this.getRevisedEventTitle() != null){
				draft.setDescription(this.getRevisedDescription());
				draft.setEventTitle(this.getRevisedEventTitle());
			}else if(this.getRevisedDescription() != null){
				draft.setDescription(this.getRevisedDescription());
				draft.setEventTitle(this.getEventTitle());
			}else if(this.getRevisedEventTitle() != null){
				draft.setDescription(this.getDescription());
				draft.setEventTitle(this.getRevisedEventTitle());
			}else{
				draft.setDescription(this.getDescription());
				draft.setEventTitle(this.getEventTitle());
			}	  
			
					
			
			if(this.getId() != null) {
				EventMotion motion = EventMotion.findById(EventMotion.class, this.getId());
				List<EventMotionDraft> originalDrafts = motion.getDrafts();
				if(originalDrafts != null){
					originalDrafts.add(draft);
				}
				else{
					originalDrafts = new ArrayList<EventMotionDraft>();
					originalDrafts.add(draft);
				}
				this.setDrafts(originalDrafts);
			}
			else {
				List<EventMotionDraft> originalDrafts = new ArrayList<EventMotionDraft>();
				originalDrafts.add(draft);
				this.setDrafts(originalDrafts);
			}
		}
	}

	@Override
	public EventMotion merge() {
		EventMotion motion = null;
		if(this.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = EventMotion.assignEventMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(),this.getLocale());
					this.setNumber(number + 1);
					addEventMotionDraft();
					motion = (EventMotion) super.merge();
				}
			}else {
				EventMotion oldMotion = EventMotion.findById(EventMotion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				this.addEventMotionDraft();
				motion = (EventMotion) super.merge();
			}
		}
		
		if(motion != null) {
			return motion;
		}else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_INCOMPLETE) 
					|| this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_COMPLETE)) {
				return (EventMotion) super.merge();
			}else {
				EventMotion oldMotion = EventMotion.findById(EventMotion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				this.addEventMotionDraft();
				return (EventMotion) super.merge();
			}
		}
	}

	public EventMotion simpleMerge() {
		EventMotion m = (EventMotion) super.merge();
		return m;
	}

	public static List<RevisionHistoryVO> getRevisions(final Long eventMotionId, final String locale) {
		return getEventMotionRepository().getRevisions(eventMotionId, locale);
	}

	public String formatNumber() {
		if(getNumber() != null){
			NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}	

	public static List<EventMotion> findAllByStatus(final Session session,
			final DeviceType eventMotionType,
			final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		return null;
	}	

	public EventMotionDraft findLatestDraft() {
		List<EventMotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			return drafts.get(size);            
		}
		return null;
	}

	public EventMotionDraft findPreviousDraft() {
		List<EventMotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			if(size > 1) {
				return drafts.get(size-1);
			}
		}
		return null;
	}

	public static Reference findCurrentFile(final EventMotion domain) {
		return getEventMotionRepository().findCurrentFile(domain);
	}
	
	public static List<EventMotion> findAllByFile(final Session session,
			final DeviceType cutMotionType,
			final Integer file,
			final String locale) {
		return null;//getCutMotionRepository().findAllByFile(session, cutMotionType, file, locale);
	}

	public static List<EventMotion> findBySessionDeviceTypeSubdepartment(final Session session,
			final DeviceType cutMotionType,
			final SubDepartment subDepartment,
			final String locale) {
		return null;//getCutMotionRepository().findBySessionDeviceTypeSubdepartment(session, cutMotionType, subDepartment, locale);
	}
	
	public static Integer findMaxNumberBySubdepartment(final Session session,
			final DeviceType deviceType, final SubDepartment subDepartment, final String locale) {
		
		return null;//getCutMotionRepository().findMaxNumberBySubdepartment(session, deviceType, subDepartment, locale);
	}	
	
	public static int findHighestFileNo(final Session session,
			final DeviceType eventMotionType,
			final String locale) {
		return getEventMotionRepository().findHighestFileNo(session, eventMotionType, locale);
	}
	
	public static EventMotion getMotion(final Long sessionId, final Long deviceTypeId, final Integer dNumber, final String locale) {
		return null;//getCutMotionRepository().getMotion(sessionId,deviceTypeId,dNumber,locale);
	}
	
	public static List<EventMotion> findFinalizedCutMotions(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment,
			final Status status,
			final String sortOrder,
			final String locale) {
		return null;//getCutMotionRepository().findFinalizedCutMotionsByDepartment(session, deviceType, subDepartment, status, sortOrder, locale);
	}
	
	public static List<EventMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType cutMotionType,
			final Integer itemsCount,
			final String locale) {
		return getEventMotionRepository().findAllByMember(session, primaryMember, cutMotionType,itemsCount, locale);
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

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getRevisedEventTitle() {
		return revisedEventTitle;
	}

	public void setRevisedEventTitle(String revisedEventTitle) {
		this.revisedEventTitle = revisedEventTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRevisedDescription() {
		return revisedDescription;
	}

	public void setRevisedDescription(String revisedDescription) {
		this.revisedDescription = revisedDescription;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<SupportingMember> getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(List<SupportingMember> supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public List<EventMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<EventMotionDraft> drafts) {
		this.drafts = drafts;
	}

	public EventMotion getParent() {
		return parent;
	}

	public void setParent(EventMotion parent) {
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

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
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

	public String getDesignationOfPerson() {
		return designationOfPerson;
	}

	public void setDesignationOfPerson(String designationOfPerson) {
		this.designationOfPerson = designationOfPerson;
	}

	public String getTenureOfPerson() {
		return tenureOfPerson;
	}

	public void setTenureOfPerson(String tenureOfPerson) {
		this.tenureOfPerson = tenureOfPerson;
	}

	public String getConstituencyOfPerson() {
		return constituencyOfPerson;
	}

	public void setConstituencyOfPerson(String constituencyOfPerson) {
		this.constituencyOfPerson = constituencyOfPerson;
	}

	public String getCollectorReport() {
		return collectorReport;
	}

	public void setCollectorReport(String collectorReport) {
		this.collectorReport = collectorReport;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventReason() {
		return eventReason;
	}

	public void setEventReason(String eventReason) {
		this.eventReason = eventReason;
	}

	public Boolean getIsHouseAdjourned() {
		return isHouseAdjourned;
	}

	public void setIsHouseAdjourned(Boolean isHouseAdjourned) {
		this.isHouseAdjourned = isHouseAdjourned;
	}
	/**** Getter Setters ****/
}