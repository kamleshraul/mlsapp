/*
 * 
 */
package org.mkcl.els.domain;

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
import org.mkcl.els.repository.MotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Motion.
 */
@Configurable
@Entity
@Table(name="motions")
@JsonIgnoreProperties({"houseType", "session", "type", 
	"recommendationStatus", "supportingMembers",
	"department", "drafts", "parent", "clubbedEntities"})
	public class Motion extends Device implements Serializable{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**** Attributes ****/
	
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

	/**** The question text. ****/
	@Column(length=30000)
	private String details;

	/**** The question text. ****/
	@Column(length=30000)
	private String revisedDetails;

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

	/** ** Remarks ***. */
	@Column(length=30000)
	private String remarks;

	/** The primary member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="motions_supportingmembers",
			joinColumns={@JoinColumn(name="motion_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
			private List<SupportingMember> supportingMembers;

	/** The ministry. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ministry_id")
	private Ministry ministry;

	/** The department. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="department_id")
	private Department department;

	/** The sub department. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subdepartment_id")
	private SubDepartment subDepartment;

	/** The drafts. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="motions_drafts_association", 
			joinColumns={@JoinColumn(name="motion_id", referencedColumnName="id")}, 
			inverseJoinColumns={@JoinColumn(name="motion_draft_id", referencedColumnName="id")})
			private List<MotionDraft> drafts;  

	/** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private Motion parent;

	/** The clubbed entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="motions_clubbingentities", 
			joinColumns={@JoinColumn(name="motion_id", referencedColumnName="id")}, 
			inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
			private List<ClubbedEntity> clubbedEntities;

	//=============== Referencing ====================//
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="motions_referencedentities", 
			joinColumns={@JoinColumn(name="motion_id", referencedColumnName="id")}, 
			inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
			private List<ReferencedEntity> referencedEntities;

	/**** Reply(Nivedan) ****/
	@Column(length=30000)
	private String reply;

	/**** Answering Date ****/
	private Date answeringDate;	

	/** The question repository. */
	@Autowired
	private transient MotionRepository motionRepository;

	/**** Constructors ****/   
	public Motion(final HouseType houseType, final Session session, final DeviceType type,
			final Integer number, final Date submissionDate, final Date creationDate,
			final String createdBy, final Date editedOn, final String editedBy, final String editedAs,
			final String subject, final String revisedSubject, final String details,
			final String revisedDetails, final Status status, final Status internalStatus,
			final Status recommendationStatus, final String remarks, final Member primaryMember,
			final List<SupportingMember> supportingMembers, final Ministry ministry,
			final Department department, final SubDepartment subDepartment,
			final List<MotionDraft> drafts, final Motion parent,
			final List<ClubbedEntity> clubbedEntities) {
		super();
		this.houseType = houseType;
		this.session = session;
		this.type = type;
		this.number = number;
		this.submissionDate = submissionDate;
		this.creationDate = creationDate;
		this.createdBy = createdBy;
		this.editedOn = editedOn;
		this.editedBy = editedBy;
		this.editedAs = editedAs;
		this.subject = subject;
		this.revisedSubject = revisedSubject;
		this.details = details;
		this.revisedDetails = revisedDetails;
		this.status = status;
		this.internalStatus = internalStatus;
		this.recommendationStatus = recommendationStatus;
		this.remarks = remarks;
		this.primaryMember = primaryMember;
		this.supportingMembers = supportingMembers;
		this.ministry = ministry;
		this.department = department;
		this.subDepartment = subDepartment;
		this.drafts = drafts;
		this.parent = parent;
		this.clubbedEntities = clubbedEntities;
	}	

	public Motion() {
		super();
	}

	/**** Domain Methods ****/

	private static MotionRepository getMotionRepository() {
		MotionRepository motionRepository = new Motion().motionRepository;
		if (motionRepository == null) {
			throw new IllegalStateException(
			"MotionRepository has not been injected in Motion Domain");
		}
		return motionRepository;
	}

	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final Motion motion) {
		return getMotionRepository().findClubbedEntitiesByPosition(motion);
	}

	@Override
	public Motion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.MOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = Motion.assignMotionNo(this.getHouseType(),
							this.getSession(), this.getType(),this.getLocale());
					this.setNumber(number + 1);
					addMotionDraft();
					return (Motion)super.persist();
				}
			}
		}
		return (Motion) super.persist();
	}

	private static Integer assignMotionNo(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) {
		return getMotionRepository().assignMotionNo(houseType,
				session,type,locale);
	}

	private void addMotionDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.MOTION_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.MOTION_COMPLETE)) {
			MotionDraft draft = new MotionDraft();
			draft.setLocale(this.getLocale());
			draft.setRemarks(this.getRemarks());
			draft.setParent(this.getParent());
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
			if(this.getRevisedDetails()!= null && this.getRevisedSubject() != null){
				draft.setDetails(this.getRevisedDetails());
				draft.setSubject(this.getRevisedSubject());                
			}
			else if(this.getRevisedDetails() != null){
				draft.setDetails(this.getRevisedDetails());
				draft.setSubject(this.getSubject());
			}
			else if(this.getRevisedSubject()!=null){
				draft.setDetails(this.getDetails());
				draft.setSubject(this.getRevisedSubject());
			}
			else{
				draft.setDetails(this.getDetails());
				draft.setSubject(this.getSubject());
			}	            
			if(this.getId() != null) {
				Motion motion = Motion.findById(Motion.class, this.getId());
				List<MotionDraft> originalDrafts = motion.getDrafts();
				if(originalDrafts != null){
					originalDrafts.add(draft);
				}
				else{
					originalDrafts = new ArrayList<MotionDraft>();
					originalDrafts.add(draft);
				}
				this.setDrafts(originalDrafts);
			}
			else {
				List<MotionDraft> originalDrafts = new ArrayList<MotionDraft>();
				originalDrafts.add(draft);
				this.setDrafts(originalDrafts);
			}
		}
	}

	@Override
	public Motion merge() {
		Motion motion = null;
		if(this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = Motion.assignMotionNo(this.getHouseType(),
							this.getSession(), this.getType(),this.getLocale());
					this.setNumber(number + 1);
					addMotionDraft();
					motion = (Motion) super.merge();
				}
			}
			else {
				Motion oldMotion = Motion.findById(Motion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				if(this.getReferencedEntities() == null){
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addMotionDraft();
				motion = (Motion) super.merge();
			}
		}
		if(motion != null) {
			return motion;
		}
		else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_INCOMPLETE) 
					|| 
					this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_COMPLETE)) {
				return (Motion) super.merge();
			}
			else {
				Motion oldMotion = Motion.findById(Motion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}	
				if(this.getReferencedEntities() == null){
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addMotionDraft();
				return (Motion) super.merge();
			}
		}
	}

	public Motion simpleMerge() {
		Motion m = (Motion) super.merge();
		return m;
	}

	public static List<RevisionHistoryVO> getRevisions(final Long motionId,
			final String locale) {
		return getMotionRepository().getRevisions(motionId,
				locale);
	}

	public static List<Motion> findAllByMember(final Session session,
			final Member primaryMember,final DeviceType motionType,final Integer itemsCount,
			final String locale) {
		return getMotionRepository().findAllByMember(session,
				primaryMember,motionType,itemsCount,
				locale);
	}

	public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}	

	public static List<Motion> findAllByStatus(final Session session,
			final DeviceType motionType,final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		return getMotionRepository().findAllByStatus(session,
				motionType,internalStatus,
				itemsCount,
				locale);
	}	

	public MotionDraft findLatestDraft() {
		List<MotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			return drafts.get(size);            
		}
		return null;
	}

	public MotionDraft findPreviousDraft() {
		List<MotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			if(size > 1) {
				return drafts.get(size-1);
			}
		}
		return null;
	}

	public static Reference findCurrentFile(final Motion domain) {
		return getMotionRepository().findCurrentFile(domain);
	}
	
	public static List<Motion> findAllByFile(final Session session,
			final DeviceType motionType,final Integer file,final String locale) {
		return getMotionRepository().findAllByFile(session,
				motionType,file,locale);
	}

	public static int findHighestFileNo(final Session session,
			final DeviceType motionType,final String locale) {
		return getMotionRepository().findHighestFileNo(session,
				motionType,locale);
	}
	
	public static Motion getMotion(Long sessionId, Long deviceTypeId, Integer dNumber,
			String locale) {
		return getMotionRepository().getMotion(sessionId,deviceTypeId,dNumber,locale);
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getRevisedDetails() {
		return revisedDetails;
	}

	public void setRevisedDetails(String revisedDetails) {
		this.revisedDetails = revisedDetails;
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

	public List<MotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<MotionDraft> drafts) {
		this.drafts = drafts;
	}

	public Motion getParent() {
		return parent;
	}

	public void setParent(Motion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	public List<ReferencedEntity> getReferencedEntities() {
		return referencedEntities;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getActor() {
		return actor;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setWorkflowStarted(String workflowStarted) {
		this.workflowStarted = workflowStarted;
	}

	public String getWorkflowStarted() {
		return workflowStarted;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLevel() {
		return level;
	}

	public void setLocalizedActorName(String localizedActorName) {
		this.localizedActorName = localizedActorName;
	}

	public String getLocalizedActorName() {
		return localizedActorName;
	}

	public void setWorkflowStartedOn(Date workflowStartedOn) {
		this.workflowStartedOn = workflowStartedOn;
	}

	public Date getWorkflowStartedOn() {
		return workflowStartedOn;
	}

	public void setTaskReceivedOn(Date taskReceivedOn) {
		this.taskReceivedOn = taskReceivedOn;
	}

	public Date getTaskReceivedOn() {
		return taskReceivedOn;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}

	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	public void setBulkSubmitted(boolean bulkSubmitted) {
		this.bulkSubmitted = bulkSubmitted;
	}

	public boolean isBulkSubmitted() {
		return bulkSubmitted;
	}

	public void setWorkflowDetailsId(Long workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}

	public Long getWorkflowDetailsId() {
		return workflowDetailsId;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getReply() {
		return reply;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setFile(Integer file) {
		this.file = file;
	}

	public Integer getFile() {
		return file;
	}

	public void setFileIndex(Integer fileIndex) {
		this.fileIndex = fileIndex;
	}

	public Integer getFileIndex() {
		return fileIndex;
	}

	public void setFileSent(Boolean fileSent) {
		this.fileSent = fileSent;
	}

	public Boolean getFileSent() {
		return fileSent;
	}

	
}
