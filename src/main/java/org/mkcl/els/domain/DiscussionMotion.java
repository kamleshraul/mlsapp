package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.repository.DiscussionMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "discussionmotion")
@JsonIgnoreProperties({ "houseType", "session", "type", "recommendationStatus", "supportingMembers", "ballotStatus",
		"subDepartments", "departments", "drafts", "parent", "clubbedEntities", "referencedEntities","ballotStatus" })
public class DiscussionMotion extends Device implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**** Attributes ****/

	/*** The house type. ***/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "housetype_id")
	private HouseType houseType;

	/*** The session. ***/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private Session session;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "originaldevicetype_id")
	private DeviceType originalType;

	/*** The type. ***/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType type;

	/*** The number. ***/
	private Integer number;

	/*** The submission date. ***/
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;

	/*** The creation date. ***/
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	/*** The created by. ***/
	@Column(length = 1000)
	private String createdBy;

	/*** The clerk name ***/
	private String dataEnteredBy;

	/*** The edited on. ***/
	@Temporal(TemporalType.TIMESTAMP)
	@JoinColumn(name = "editedon")
	private Date editedOn;

	/*** The edited by. ***/
	@Column(length = 1000)
	private String editedBy;

	/*** The edited as. ***/
	@Column(length = 1000)
	private String editedAs;

	/*** The subject. ***/
	@Column(length = 30000)
	private String subject;

	/*** The subject. ***/
	@Column(length = 30000)
	private String revisedSubject;

	/*** The Notice Content. ***/
	@Column(length = 30000)
	private String noticeContent;

	/*** The Revised Notice Content. ***/
	@Column(length = 30000)
	private String revisedNoticeContent;

	/*** The Brief Explanation. ***/
	@Column(length = 30000)
	private String briefExplanation;

	/*** The Revised Brief Explanation. ***/
	@Column(length = 30000)
	private String revisedBriefExplanation;

	/*** The priority. ***/
	private Integer priority;

	/**
	 * The status. Refers to various final status viz, SUBMITTED, ADMITTED,
	 * REJECTED, CONVERTED_TO_UNSTARRED
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private Status status;

	/**
	 * The internal status. Refers to status assigned to a DiscussionMotion
	 * during the Workflow
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "internalstatus_id")
	private Status internalStatus;

	/** The recommendation status. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recommendationstatus_id")
	private Status recommendationStatus;

	/**
	 * If a short duration discussions is balloted then its balloted status is
	 * set to balloted
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ballotstatus_id")
	private Status ballotStatus;

	/*** The remarks. ***/
	@Column(length = 30000)
	private String remarks;

	/*** The Rejection reason. ***/
	@Column(length = 30000)
	private String rejectionReason;

	/**** PRIMARY & SUPPORTING MEMBERS ****/
	/** The primary member. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member primaryMember;

	/*** The supporting members. ***/
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name = "discussionmotion_supportingmembers", joinColumns = {
			@JoinColumn(name = "discussionmotion_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "supportingmember_id", referencedColumnName = "id") })
	private List<SupportingMember> supportingMembers;

	/*** The ministry. ***/
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "discussionmotion_ministries", joinColumns = {
			@JoinColumn(name = "discussionmotion_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "ministry_id", referencedColumnName = "id") })
	private List<Ministry> ministries;

	/*** The department. ***/
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "discussionmotion_departments", joinColumns = {
			@JoinColumn(name = "discussionmotion_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "department_id", referencedColumnName = "id") })
	private List<Department> departments;

	/*** The sub department. ***/
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "discussionmotion_subdepartments", joinColumns = {
			@JoinColumn(name = "discussionmotion_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "subdepartment_id", referencedColumnName = "id") })
	private List<SubDepartment> subDepartments;

	/**** DRAFTS ****/
	/*** The drafts. ***/
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "discussionmotion_drafts_association", joinColumns = {
			@JoinColumn(name = "discussionmotion_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "discussionmotion_draft_id", referencedColumnName = "id") })
	private List<DiscussionMotionDraft> drafts;

	/**** Clubbing ****/
	/*** The parent. ***/
	@ManyToOne(fetch = FetchType.LAZY)
	private DiscussionMotion parent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "discussionmotion_clubbingentities", joinColumns = {
			@JoinColumn(name = "discussionmotion_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "clubbed_entity_id", referencedColumnName = "id") })
	private List<ClubbedEntity> clubbedEntities;

	/**** Referencing ****/
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "discussionmotion_referencedunits", joinColumns = {
			@JoinColumn(name = "discussionmotion_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "referenced_unit_id", referencedColumnName = "id") })
	private List<ReferenceUnit> referencedEntities;

	@Temporal(TemporalType.DATE)
	private Date discussionDate;

	/**** To be used in case of bulk submission and workflows ****/
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
	
	/**** The Disucssion Status. ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="discussionstatus_id")
	private Status discussionStatus;

	/**** Clarification Related Fields ****/
	@Column(length = 30000)
	private String clarification;

	/** The date of Clarification receiving. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastDateOfClarificationReceiving;

	private Integer numberOfDaysForClarificationReceiving;

	@Autowired
	private transient DiscussionMotionRepository discussionMotionRepository;

	// For Numbering of the discussion Motions in synchronized manner
	private static transient volatile Integer SHORTNOTICE_MOTION_CUR_NUM_LOWER_HOUSE = 0;
	private static transient volatile Integer SHORTNOTICE_MOTION_CUR_NUM_UPPER_HOUSE = 0;

	private static transient volatile Integer LASTWEEK_MOTION_CUR_NUM_LOWER_HOUSE = 0;
	private static transient volatile Integer LASTWEEK_MOTION_CUR_NUM_UPPER_HOUSE = 0;

	private static transient volatile Integer PUBLICIMPORTANCE_MOTION_CUR_NUM_LOWER_HOUSE = 0;
	private static transient volatile Integer PUBLICIMPORTANCE_MOTION_CUR_NUM_UPPER_HOUSE = 0;

	/**** Constructors and Domain Methods ****/

	public DiscussionMotion() {
		super();
	}

	public DiscussionMotion(HouseType houseType, Session session, DeviceType type, Integer number, Date submissionDate,
			Date creationDate, String createdBy, String dataEnteredBy, Date editedOn, String editedBy, String editedAs,
			String subject, String revisedSubject, String noticeContent, String revisedNoticeContent, Integer priority,
			Status status, Status internalStatus, Status recommendationStatus, Status ballotStatus, String remarks,
			String rejectionReason, Member primaryMember, List<SupportingMember> supportingMembers,
			List<Ministry> ministry, List<Department> department, List<SubDepartment> subDepartment,
			List<DiscussionMotionDraft> drafts, DiscussionMotion parent, List<ClubbedEntity> clubbedEntities,
			List<ReferenceUnit> referencedEntities, Date discussionDate, String workflowStarted, String actor,
			String localizedActorName, String endFlag, String level, Date workflowStartedOn, Date taskReceivedOn,
			boolean bulkSubmitted, Long workflowDetailsId, Integer file, Integer fileIndex, Boolean fileSent,
			String clarification, Date lastDateOfClarificationReceiving,
			Integer numberOfDaysForClarificationReceiving) {
		super();
		this.houseType = houseType;
		this.session = session;
		this.type = type;
		this.number = number;
		this.submissionDate = submissionDate;
		this.creationDate = creationDate;
		this.createdBy = createdBy;
		this.dataEnteredBy = dataEnteredBy;
		this.editedOn = editedOn;
		this.editedBy = editedBy;
		this.editedAs = editedAs;
		this.subject = subject;
		this.revisedSubject = revisedSubject;
		this.noticeContent = noticeContent;
		this.revisedNoticeContent = revisedNoticeContent;
		this.priority = priority;
		this.status = status;
		this.internalStatus = internalStatus;
		this.recommendationStatus = recommendationStatus;
		this.ballotStatus = ballotStatus;
		this.remarks = remarks;
		this.rejectionReason = rejectionReason;
		this.primaryMember = primaryMember;
		this.supportingMembers = supportingMembers;
		this.ministries = ministry;
		this.departments = department;
		this.subDepartments = subDepartment;
		this.drafts = drafts;
		this.parent = parent;
		this.clubbedEntities = clubbedEntities;
		this.referencedEntities = referencedEntities;
		this.discussionDate = discussionDate;
		this.workflowStarted = workflowStarted;
		this.actor = actor;
		this.localizedActorName = localizedActorName;
		this.endFlag = endFlag;
		this.level = level;
		this.workflowStartedOn = workflowStartedOn;
		this.taskReceivedOn = taskReceivedOn;
		this.bulkSubmitted = bulkSubmitted;
		this.workflowDetailsId = workflowDetailsId;
		this.file = file;
		this.fileIndex = fileIndex;
		this.fileSent = fileSent;
		this.clarification = clarification;
		this.lastDateOfClarificationReceiving = lastDateOfClarificationReceiving;
		this.numberOfDaysForClarificationReceiving = numberOfDaysForClarificationReceiving;
	}

	private static DiscussionMotionRepository getDiscussionMotionRepository() {
		DiscussionMotionRepository discussionMotionRepository = new DiscussionMotion().discussionMotionRepository;
		if (discussionMotionRepository == null) {
			throw new IllegalStateException(
					"DiscussionMotionRepository has not been injected in DiscussionMotion Domain");
		}
		return discussionMotionRepository;
	}

	public static int findHighestFileNo(final Session session, final DeviceType discussionMotionType,
			final String locale) {
		return getDiscussionMotionRepository().findHighestFileNo(session, discussionMotionType, locale);
	}

	@Override
	public DiscussionMotion persist() {
		if (this.getStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)) {
			if (this.getNumber() == null) {
				synchronized (DiscussionMotion.class) {
					// Integer number =
					// DiscussionMotion.assignDiscussionMotionNo(this.getHouseType(),
					// this.getSession(), this.getType(),this.getLocale());
					// this.setNumber(number + 1);
					// addDiscussionMotionDraft();
					// return (DiscussionMotion)super.persist();
					Integer number = null;
					try {
						number = DiscussionMotion.assignDiscussionMotionNo(this.getHouseType(), this.getSession(),
								this.getType(), this.getLocale());
						String houseType = this.getHouseType().getType();
						String deviceType = this.getType().getType();
						if (deviceType.equals(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION)) {
							if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
								if (DiscussionMotion.getShortDurationMotionCurrentNumberLowerHouse() == 0) {
									DiscussionMotion.updateShortDurationMotionCurrentNumberLowerHouse(number);
								}
							} else if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
								if (DiscussionMotion.getShortDurationMotionCurrentNumberUpperHouse() == 0) {
									DiscussionMotion.updateShortDurationMotionCurrentNumberUpperHouse(number);
								}
							}

							if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
								this.setNumber(DiscussionMotion.getShortDurationMotionCurrentNumberLowerHouse() + 1);
								DiscussionMotion.updateShortDurationMotionCurrentNumberLowerHouse(
										DiscussionMotion.getShortDurationMotionCurrentNumberLowerHouse() + 1);
							} else if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
								this.setNumber(DiscussionMotion.getShortDurationMotionCurrentNumberUpperHouse() + 1);
								DiscussionMotion.updateShortDurationMotionCurrentNumberUpperHouse(
										DiscussionMotion.getShortDurationMotionCurrentNumberUpperHouse() + 1);
							}
						} else if (deviceType.equals(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE)) {
							if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
								if (DiscussionMotion.getPublicImportanceMotionCurrentNumberLowerHouse() == 0) {
									DiscussionMotion.updatePublicImportanceMotionCurrentNumberLowerHouse(number);
								}
							} else if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
								if (DiscussionMotion.getPublicImportanceMotionCurrentNumberUpperHouse() == 0) {
									DiscussionMotion.updatePublicImportanceMotionCurrentNumberUpperHouse(number);
								}
							}

							if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
								this.setNumber(DiscussionMotion.getPublicImportanceMotionCurrentNumberLowerHouse() + 1);
								DiscussionMotion.updatePublicImportanceMotionCurrentNumberLowerHouse(
										DiscussionMotion.getPublicImportanceMotionCurrentNumberLowerHouse() + 1);
							} else if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
								this.setNumber(DiscussionMotion.getPublicImportanceMotionCurrentNumberUpperHouse() + 1);
								DiscussionMotion.updatePublicImportanceMotionCurrentNumberUpperHouse(
										DiscussionMotion.getPublicImportanceMotionCurrentNumberUpperHouse() + 1);
							}
						} else if (deviceType.equals(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK)) {
							if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
								if (DiscussionMotion.getLastWeekMotionCurrentNumberLowerHouse() == 0) {
									DiscussionMotion.updateLastWeekMotionCurrentNumberLowerHouse(number);
								}
							} else if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
								if (DiscussionMotion.getLastWeekMotionCurrentNumberUpperHouse() == 0) {
									DiscussionMotion.updateLastWeekMotionCurrentNumberUpperHouse(number);
								}
							}

							if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
								this.setNumber(DiscussionMotion.getLastWeekMotionCurrentNumberLowerHouse() + 1);
								DiscussionMotion.updateLastWeekMotionCurrentNumberLowerHouse(
										DiscussionMotion.getLastWeekMotionCurrentNumberLowerHouse() + 1);
							} else if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
								this.setNumber(DiscussionMotion.getLastWeekMotionCurrentNumberUpperHouse() + 1);
								DiscussionMotion.updateLastWeekMotionCurrentNumberUpperHouse(
										DiscussionMotion.getLastWeekMotionCurrentNumberUpperHouse() + 1);
							}
						}

						addDiscussionMotionDraft();
						return (DiscussionMotion) super.persist();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {

					}
				}
			} else if (this.getNumber() != null) {
				addDiscussionMotionDraft();
			}
		}
		return (DiscussionMotion) super.persist();
	}

	private static Integer assignDiscussionMotionNo(final HouseType houseType, final Session session,
			final DeviceType type, final String locale) {
		return getDiscussionMotionRepository().assignDiscussionMotionNo(houseType, session, type, locale);
	}

	public static Boolean isExist(final Integer number, final DeviceType deviceType, final Session session,
			final String locale) {
		return getDiscussionMotionRepository().isExist(number, deviceType, session, locale);
	}

	
	//Shubham
	private void addDiscussionMotionDraft() {
		if (!this.getStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE)
				&& !this.getStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_COMPLETE)) {
			DiscussionMotionDraft draft = new DiscussionMotionDraft();
			draft.setLocale(this.getLocale());
			draft.setRemarks(this.getRemarks());
			draft.setParent(this.getParent());
			draft.setClubbedEntities(this.getClubbedEntities());
			draft.setReferencedEntities(this.getReferencedEntities());
			draft.setEditedAs(this.getEditedAs());
			draft.setEditedBy(this.getEditedBy());
			draft.setEditedOn(this.getEditedOn());
			draft.setMinistries(this.getMinistries());
			draft.setDepartments(this.getDepartments());
			draft.setSubDepartments(this.getSubDepartments());
			draft.setType(this.getType());
			draft.setDiscussionDate(this.getDiscussionDate());
			draft.setStatus(this.getStatus());
			draft.setInternalStatus(this.getInternalStatus());
			draft.setRecommendationStatus(this.getRecommendationStatus());
			if (this.getRevisedNoticeContent() != null && this.getRevisedSubject() != null && this.getRevisedBriefExplanation() != null) {
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setSubject(this.getRevisedSubject());
				draft.setBriefExplanation(this.getRevisedBriefExplanation());
			} else if (this.getRevisedNoticeContent() != null) {
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setSubject(this.getSubject());
				draft.setBriefExplanation(this.getRevisedBriefExplanation());
			} else if (this.getRevisedSubject() != null) {
				draft.setNoticeContent(this.getNoticeContent());
				draft.setSubject(this.getRevisedSubject());
				draft.setBriefExplanation(this.getRevisedBriefExplanation());
			}
			else if(this.getRevisedBriefExplanation() != null) {
				draft.setBriefExplanation(this.getRevisedBriefExplanation());
			}
			else {
				draft.setNoticeContent(this.getNoticeContent());
				draft.setSubject(this.getSubject());
				draft.setBriefExplanation(this.getBriefExplanation());
			}
			if (this.getId() != null) {
				DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, this.getId());
				List<DiscussionMotionDraft> originalDrafts = motion.getDrafts();
				if (originalDrafts != null) {
					originalDrafts.add(draft);
				} else {
					originalDrafts = new ArrayList<DiscussionMotionDraft>();
					originalDrafts.add(draft);
				}
				this.setDrafts(originalDrafts);
			} else {
				List<DiscussionMotionDraft> originalDrafts = new ArrayList<DiscussionMotionDraft>();
				originalDrafts.add(draft);
				this.setDrafts(originalDrafts);
			}
		}
	}

	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final DiscussionMotion motion,
			final String sortOrder) {
		return getDiscussionMotionRepository().findClubbedEntitiesByPosition(motion, sortOrder);
	}

	@Override
	public DiscussionMotion merge() {
		DiscussionMotion motion = null;
		if (this.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)) {
			if (this.getNumber() == null) {
				synchronized (this) {
					Integer number = DiscussionMotion.assignDiscussionMotionNo(this.getHouseType(), this.getSession(),
							this.getType(), this.getLocale());
					this.setNumber(number + 1);
					// TODO: may needed for maintaining postBallotNumber in
					// other batch motions
					/*
					 * Integer mergePostBallotNumber =
					 * Motion.findMaxPostBallotNo(this.getHouseType(),
					 * this.getSession(), this.getType(), this.getLocale());
					 * if(mergePostBallotNumber > 0){
					 * this.setPostBallotNumber(mergePostBallotNumber + 1); }
					 */
					addDiscussionMotionDraft();
					motion = (DiscussionMotion) super.merge();
				}
			} else {
				DiscussionMotion oldMotion = DiscussionMotion.findById(DiscussionMotion.class, this.getId());
				if (this.getClubbedEntities() == null) {
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				if (this.getReferencedEntities() == null) {
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addDiscussionMotionDraft();
				motion = (DiscussionMotion) super.merge();
			}
		}
		if (motion != null) {
			return motion;
		} else {
			if (this.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE)
					|| this.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_COMPLETE)) {
				return (DiscussionMotion) super.merge();
			} else {
				DiscussionMotion oldMotion = DiscussionMotion.findById(DiscussionMotion.class, this.getId());
				if (this.getClubbedEntities() == null) {
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				if (this.getReferencedEntities() == null) {
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addDiscussionMotionDraft();
				return (DiscussionMotion) super.merge();
			}
		}
	}

	public DiscussionMotion simpleMerge() {
		DiscussionMotion m = (DiscussionMotion) super.merge();
		return m;
	}

	public static Reference findCurrentFile(DiscussionMotion domain) {
		return getDiscussionMotionRepository().findCurrentFile(domain);
	}

	public static List<DiscussionMotion> findAllByFile(final Session session, final DeviceType discussionMotionType,
			final Integer file, final String locale) {
		return getDiscussionMotionRepository().findAllByFile(session, discussionMotionType, file, locale);
	}

	public static List<DiscussionMotion> findAllByStatus(final Session session, final DeviceType discussionMotionType,
			final Status internalStatus, final Integer itemsCount, final String locale) {
		return getDiscussionMotionRepository().findAllByStatus(session, discussionMotionType, internalStatus,
				itemsCount, locale);
	}

	public static List<RevisionHistoryVO> getRevisions(Long discussionMotionId, String locale) {
		return getDiscussionMotionRepository().getRevisions(discussionMotionId, locale);
	}

	public static List<DiscussionMotion> findAllByMember(final Session session, final Member primaryMember,
			final DeviceType discussionMotionType, final Integer itemsCount, final String locale) {
		return getDiscussionMotionRepository().findAllByMember(session, primaryMember, discussionMotionType, itemsCount,
				locale);
	}

	public static List<DiscussionMotion> findAllEnteredBy(final Session session, final String user,
			final DeviceType discussionMotionType, final Integer itemsCount, final String locale) {
		return getDiscussionMotionRepository().findAllEnteredBy(session, user, discussionMotionType, itemsCount,
				locale);
	}

	public String formatNumber() {
		return FormaterUtil.formatNumberNoGrouping(this.getNumber(), this.getLocale());
	}

	// ************************Clubbing**********************
	public static boolean club(final Long primary, final Long clubbing, final String locale) throws ELSException {

		DiscussionMotion m1 = DiscussionMotion.findById(DiscussionMotion.class, primary);
		DiscussionMotion m2 = DiscussionMotion.findById(DiscussionMotion.class, clubbing);

		return club(m1, m2, locale);

	}

	public static boolean club(final DiscussionMotion q1, final DiscussionMotion q2, final String locale)
			throws ELSException {
		boolean clubbingStatus = false;
		try {
			if (q1.getParent() != null || q2.getParent() != null) {
				throw new ELSException("error", "MOTION_ALREADY_CLUBBED");
			} else {
				if ((q1.getType().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION)
						&& q2.getType().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION))
						|| (q1.getType().getType().equals(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK)
								&& q2.getType().getType().equals(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK))
						|| (q1.getType().getType().equals(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE) && q2
								.getType().getType().equals(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE))) {

					clubbingStatus = clubMotions(q1, q2, locale);
				} else {
					return false;
				}
			}
		} catch (ELSException ex) {
			throw ex;
		} catch (Exception ex) {
			// logger.error("CLUBBING_FAILED",ex);
			clubbingStatus = false;
			return clubbingStatus;
		}
		return clubbingStatus;
	}

	private static boolean clubMotions(DiscussionMotion q1, DiscussionMotion q2, String locale) throws ELSException {
		boolean clubbingStatus = false;
		clubbingStatus = clubbingRulesForMotion(q1, q2, locale);
		if (clubbingStatus) {

			clubbingStatus = clubMotion(q1, q2, locale);

		}
		return clubbingStatus;
	}

	private static boolean clubbingRulesForMotion(DiscussionMotion q1, DiscussionMotion q2, String locale)
			throws ELSException {
		boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class,
				ApplicationConstants.DISCUSSIONMOTION_CLUBBING_MODE, "");

		if (csptClubbingMode != null && csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()
				&& csptClubbingMode.getValue().equals("workflow")) {
			if (clubbingStatus) {

				WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
				if (q1_workflowDetails != null
						&& q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
					throw new ELSException("error", "MOTION_FLOW_PENDING");
				}

				WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
				if (q2_workflowDetails != null
						&& q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
					throw new ELSException("error", "MOTION_FLOW_PENDING");
				}
			}
		}
		return clubbingStatus;

	}

	private static boolean clubMotion(DiscussionMotion q1, DiscussionMotion q2, String locale) throws ELSException {
		// =============cases common to both houses============//
		boolean clubbingStatus = clubMotionsBH(q1, q2, locale);

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class,
				ApplicationConstants.DISCUSSIONMOTION_CLUBBING_MODE, "");

		if (csptClubbingMode != null && csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()
				&& csptClubbingMode.getValue().equals("workflow")) {
			if (!clubbingStatus) {
				// =============cases specific to lowerhouse============//
				/** get discusison dates for motions **/
				Date q1_AnsweringDate = q1.getDiscussionDate();
				Date q2_AnsweringDate = q2.getDiscussionDate();

				Status yaadiLaidStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_YAADILAID,
						locale);

				// Case 7: Both questions are admitted and balloted
				if (q1.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)
						&& q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority()) < 0
						&& q2.getInternalStatus().getType()
								.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)
						&& (q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority()) < 0)) {
					Status clubbbingPostAdmissionPutupStatus = Status
							.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
					if (q1_AnsweringDate.compareTo(q2_AnsweringDate) == 0) {
						if (q1.getNumber().compareTo(q2.getNumber()) < 0) {
							actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
									locale);
							clubbingStatus = true;
						} else if (q1.getNumber().compareTo(q2.getNumber()) > 0) {
							actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
									locale);
							clubbingStatus = true;
						} else {
							clubbingStatus = true;
						}
					} else if (q1_AnsweringDate.compareTo(q2_AnsweringDate) < 0) {
						actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
								locale);
						clubbingStatus = true;
					} else if (q1_AnsweringDate.compareTo(q2_AnsweringDate) > 0) {
						actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
								locale);
						clubbingStatus = true;
					}
				}
			}
		}

		return clubbingStatus;
	}

	private static boolean clubbingRulesCommon(DiscussionMotion q1, DiscussionMotion q2, String locale)
			throws ELSException {
		if (q1.getSession().equals(q2.getSession()) && !q1.getType().getType().equals(q2.getType().getType())) {
			throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_DEVICETYPE");
		} /*
			 * else if(!q1.getMin) { throw new ELSException("error",
			 * "MOTIONS_FROM_DIFFERENT_MINISTRY"); } else
			 * if(!q1.getSubDepartment().getName().equals(q2.getSubDepartment().
			 * getName())) { throw new ELSException("error",
			 * "MOTIONS_FROM_DIFFERENT_DEPARTMENT"); }
			 */else {
			// clubbing rules succeeded
			return true;
		}
	}

	private static boolean clubMotionsBH(DiscussionMotion q1, DiscussionMotion q2, String locale) throws ELSException {

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class,
				ApplicationConstants.DISCUSSIONMOTION_CLUBBING_MODE, "");

		if (csptClubbingMode != null && csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()
				&& csptClubbingMode.getValue().equals("normal")) {
			if (q1.getNumber().compareTo(q2.getNumber()) < 0) {
				actualClubbingMotions(q1, q2, q1.getInternalStatus(), q1.getRecommendationStatus(), locale);
				WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(q2);
				if (wfOfChild != null) {
					WorkflowDetails.endProcess(wfOfChild);
				}
				q2.removeExistingWorkflowAttributes();
				return true;
			} else if (q1.getNumber().compareTo(q2.getNumber()) > 0) {
				actualClubbingMotions(q2, q1, q2.getInternalStatus(), q2.getRecommendationStatus(), locale);

				WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(q1);
				if (wfOfChild != null) {
					WorkflowDetails.endProcess(wfOfChild);
				}
				q1.removeExistingWorkflowAttributes();

				return true;
			} else {
				return false;
			}
		} else {
			/** get answering dates for motions **/
			Date q1_DiscussionDate = q1.getDiscussionDate();
			Date q2_DiscussionDate = q2.getDiscussionDate();

			Status putupStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED,
					locale);
			Status approvalStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION, locale);

			// Case 1: Both motions are just ready to be put up
			if (q1.getInternalStatus().getType()
					.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& q2.getInternalStatus().getType()
							.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)) {

				Status clubbedStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_CLUBBED, locale);
				if (q1_DiscussionDate != null && q2_DiscussionDate != null) {
					if (q1_DiscussionDate.compareTo(q2_DiscussionDate) == 0) {
						if (q1.getNumber().compareTo(q2.getNumber()) < 0) {
							actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
							return true;
						} else if (q1.getNumber().compareTo(q2.getNumber()) > 0) {
							actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
							return true;
						} else {
							return false;
						}
					} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) < 0) {
						actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
						return true;
					} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) > 0) {
						actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
						return true;
					} else {
						return false;
					}
				} else {

					if (q1.getNumber().compareTo(q2.getNumber()) < 0) {
						actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
						return true;
					} else if (q1.getNumber().compareTo(q2.getNumber()) > 0) {
						actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
						return true;
					} else {
						return false;
					}
				}
			}
			// Case 2A: One motion is pending in approval workflow while other
			// is ready to be put up
			else if (q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
					&& q2.getInternalStatus().getType()
							.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
				Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING,
						locale);
				actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
				return true;
			}
			// Case 2B: One motion is pending in approval workflow while other
			// is ready to be put up
			else if (q1.getInternalStatus().getType()
					.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
				Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING,
						locale);
				actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
				return true;
			}
			// Case 3: Both motions are pending in approval workflow
			else if (q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
					&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
				Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING,
						locale);
				WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
				WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
				int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
				int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
				if (q1_approvalLevel == q2_approvalLevel) {
					if (q1_DiscussionDate.compareTo(q2_DiscussionDate) == 0) {
						if (q1.getNumber().compareTo(q2.getNumber()) < 0) {
							WorkflowDetails.endProcess(q2_workflowDetails);
							q2.removeExistingWorkflowAttributes();
							actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
							return true;
						} else if (q1.getNumber().compareTo(q2.getNumber()) > 0) {
							WorkflowDetails.endProcess(q1_workflowDetails);
							q1.removeExistingWorkflowAttributes();
							actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
							return true;
						} else {
							return false;
						}
					} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) < 0) {
						WorkflowDetails.endProcess(q2_workflowDetails);
						q2.removeExistingWorkflowAttributes();
						actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
						return true;
					} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) > 0) {
						WorkflowDetails.endProcess(q1_workflowDetails);
						q1.removeExistingWorkflowAttributes();
						actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
						return true;
					} else {
						return false;
					}
				} else if (q1_approvalLevel > q2_approvalLevel) {
					WorkflowDetails.endProcess(q2_workflowDetails);
					q2.removeExistingWorkflowAttributes();
					actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
					return true;
				} else if (q1_approvalLevel < q2_approvalLevel) {
					WorkflowDetails.endProcess(q1_workflowDetails);
					q1.removeExistingWorkflowAttributes();
					actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
					return true;
				} else {
					return false;
				}
			}
			// Case 4A: One motion is admitted but not balloted yet while other
			// motion is ready to be put up (Nameclubbing Case)
			else if (q1.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)
					&& q2.getInternalStatus().getType()
							.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
				Status nameclubbbingPutupStatus = Status
						.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_NAMECLUBBING, locale);
				actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
				return true;
			}
			// Case 4B: One motion is admitted but not balloted yet while other
			// motion is ready to be put up (Nameclubbing Case)
			else if (q1.getInternalStatus().getType()
					.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)) {
				Status nameclubbbingPutupStatus = Status
						.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_NAMECLUBBING, locale);
				actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
				return true;
			}
			// Case 5A: One motion is admitted but not balloted yet while other
			// motion is pending in approval workflow (Nameclubbing Case)
			else if (q1.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)
					&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
				Status nameclubbbingPutupStatus = Status
						.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_NAMECLUBBING, locale);
				WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
				WorkflowDetails.endProcess(q2_workflowDetails);
				q2.removeExistingWorkflowAttributes();
				actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
				return true;
			}
			// Case 5B: One motion is admitted but not balloted yet while other
			// motion is pending in approval workflow (Nameclubbing Case)
			else if (q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)) {
				Status nameclubbbingPutupStatus = Status
						.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_NAMECLUBBING, locale);
				WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
				WorkflowDetails.endProcess(q1_workflowDetails);
				q1.removeExistingWorkflowAttributes();
				actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
				return true;
			}
			// Case 6: Both motions are admitted but not balloted
			else if (q1.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)) {
				Status clubbbingPostAdmissionPutupStatus = Status
						.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
				WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
				WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
				if (q1_workflowDetails == null && q2_workflowDetails == null) {
					if (q1_DiscussionDate.compareTo(q2_DiscussionDate) == 0) {
						if (q1.getNumber().compareTo(q2.getNumber()) < 0) {
							actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
									locale);
							return true;
						} else if (q1.getNumber().compareTo(q2.getNumber()) > 0) {
							actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
									locale);
							return true;
						} else {
							return false;
						}
					} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) < 0) {
						actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
								locale);
						return true;
					} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) > 0) {
						actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
								locale);
						return true;
					} else {
						return false;
					}
				} else if (q1_workflowDetails != null && q2_workflowDetails != null) {
					int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
					int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
					if (q1_approvalLevel == q2_approvalLevel) {
						if (q1_DiscussionDate.compareTo(q2_DiscussionDate) == 0) {
							if (q1.getNumber().compareTo(q2.getNumber()) < 0) {
								WorkflowDetails.endProcess(q2_workflowDetails);
								q2.removeExistingWorkflowAttributes();
								actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
										locale);
								return true;
							} else if (q1.getNumber().compareTo(q2.getNumber()) > 0) {
								WorkflowDetails.endProcess(q1_workflowDetails);
								q1.removeExistingWorkflowAttributes();
								actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
										locale);
								return true;
							} else {
								return false;
							}
						} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) < 0) {
							WorkflowDetails.endProcess(q2_workflowDetails);
							q2.removeExistingWorkflowAttributes();
							actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
									locale);
							return true;
						} else if (q1_DiscussionDate.compareTo(q2_DiscussionDate) > 0) {
							WorkflowDetails.endProcess(q1_workflowDetails);
							q1.removeExistingWorkflowAttributes();
							actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
									locale);
							return true;
						} else {
							return false;
						}
					} else if (q1_approvalLevel > q2_approvalLevel) {
						WorkflowDetails.endProcess(q2_workflowDetails);
						q2.removeExistingWorkflowAttributes();
						actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
								locale);
						return true;
					} else if (q1_approvalLevel < q2_approvalLevel) {
						WorkflowDetails.endProcess(q1_workflowDetails);
						q1.removeExistingWorkflowAttributes();
						actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus,
								locale);
						return true;
					} else {
						return false;
					}
				} else if (q1_workflowDetails == null && q2_workflowDetails != null) {
					WorkflowDetails.endProcess(q2_workflowDetails);
					q2.removeExistingWorkflowAttributes();
					actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else if (q1_workflowDetails != null && q2_workflowDetails == null) {
					WorkflowDetails.endProcess(q1_workflowDetails);
					q1.removeExistingWorkflowAttributes();
					actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	private static void actualClubbingMotions(DiscussionMotion parent, DiscussionMotion child, Status newInternalStatus,
			Status newRecommendationStatus, String locale) throws ELSException {
		/****
		 * a.Clubbed entities of parent motion are obtained b.Clubbed entities
		 * of child motion are obtained c.Child motion is
		 * updated(parent,internal status,recommendation status) d.Child Motion
		 * entry is made in Clubbed Entity and child motion clubbed entity is
		 * added to parent clubbed entity e.Clubbed entities of child motions
		 * are updated(parent,internal status,recommendation status) f.Clubbed
		 * entities of parent(child motion clubbed entities,other clubbed
		 * entities of child question and clubbed entities of parent motion is
		 * updated) g.Position of all clubbed entities of parent are updated in
		 * order of their answering date and number
		 ****/
		List<ClubbedEntity> parentClubbedEntities = new ArrayList<ClubbedEntity>();
		if (parent.getClubbedEntities() != null && !parent.getClubbedEntities().isEmpty()) {
			for (ClubbedEntity i : parent.getClubbedEntities()) {
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMnId = child.getId();
				DiscussionMotion clubbedMn = i.getDiscussionMotion();
				Long clubbedMnId = clubbedMn.getId();
				if (!childMnId.equals(clubbedMnId)) {
					parentClubbedEntities.add(i);
				}
			}
		}

		List<ClubbedEntity> childClubbedEntities = new ArrayList<ClubbedEntity>();
		if (child.getClubbedEntities() != null && !child.getClubbedEntities().isEmpty()) {
			for (ClubbedEntity i : child.getClubbedEntities()) {
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMnId = parent.getId();
				DiscussionMotion clubbedMn = i.getDiscussionMotion();
				Long clubbedMnId = clubbedMn.getId();
				if (!parentMnId.equals(clubbedMnId)) {
					childClubbedEntities.add(i);
				}
			}
		}

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.merge();

		ClubbedEntity clubbedEntity = new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setDiscussionMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class,
				ApplicationConstants.DISCUSSIONMOTION_CLUBBING_MODE, "");
		if (csptClubbingMode != null) {
			if (csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()) {

				if (csptClubbingMode.getValue().equals("normal")) {
					Status submitted = Status.findByType(ApplicationConstants.CUTMOTION_SUBMIT, locale);

					if (childClubbedEntities != null && !childClubbedEntities.isEmpty()) {
						for (ClubbedEntity k : childClubbedEntities) {
							DiscussionMotion motion = k.getDiscussionMotion();

							WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(motion);
							if (wd != null) {
								WorkflowDetails.endProcess(wd);
								motion.removeExistingWorkflowAttributes();
							}

							motion.setInternalStatus(newInternalStatus);
							motion.setRecommendationStatus(newRecommendationStatus);
							motion.setStatus(submitted);
							motion.setParent(parent);
							motion.merge();
							parentClubbedEntities.add(k);
						}
					}

				} else if (csptClubbingMode.getValue().equals("workflow")) {
					if (childClubbedEntities != null && !childClubbedEntities.isEmpty()) {
						for (ClubbedEntity k : childClubbedEntities) {
							DiscussionMotion motion = k.getDiscussionMotion();
							/** find current clubbing workflow if pending **/
							String pendingWorkflowTypeForMotion = "";

							if (motion.getInternalStatus().getType()
									.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_CLUBBING)
									|| motion.getInternalStatus().getType()
											.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
							} else if (motion.getInternalStatus().getType()
									.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_NAME_CLUBBING)
									|| motion.getInternalStatus().getType()
											.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
							} else if (motion.getRecommendationStatus().getType()
									.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
									|| motion.getRecommendationStatus().getType().equals(
											ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
							}

							if (pendingWorkflowTypeForMotion != null && !pendingWorkflowTypeForMotion.isEmpty()) {
								/** end current clubbing workflow **/
								WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion,
										pendingWorkflowTypeForMotion);
								WorkflowDetails.endProcess(wfDetails);
								motion.removeExistingWorkflowAttributes();

								/**
								 * put up for proper clubbing workflow as per
								 * updated parent
								 **/
								Status finalAdmitStatus = Status
										.findByType(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION, locale);
								Integer parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
								Integer motion_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();

								if (parent.getStatus().getPriority()
										.compareTo(parent_finalAdmissionStatusPriority) < 0) {
									Status putupForClubbingStatus = Status
											.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING, locale);
									motion.setInternalStatus(putupForClubbingStatus);
									motion.setRecommendationStatus(putupForClubbingStatus);
								} else {
									if (motion.getStatus().getPriority()
											.compareTo(motion_finalAdmissionStatusPriority) < 0) {
										Status putupForNameClubbingStatus = Status.findByType(
												ApplicationConstants.DISCUSSIONMOTION_PUTUP_NAMECLUBBING, locale);
										motion.setInternalStatus(putupForNameClubbingStatus);
										motion.setRecommendationStatus(putupForNameClubbingStatus);
									} else {
										Status putupForClubbingPostAdmissionStatus = Status.findByType(
												ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION,
												locale);
										motion.setInternalStatus(putupForClubbingPostAdmissionStatus);
										motion.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
									}
								}
							}
							motion.setParent(parent);
							motion.merge();
							parentClubbedEntities.add(k);
						}
					}
				}
			}

			boolean isChildBecomingParentCase = false;
			if (parent.getParent() != null) {
				isChildBecomingParentCase = true;
				parent.setParent(null);
			}
			parent.setClubbedEntities(parentClubbedEntities);
			if (isChildBecomingParentCase) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
				parent.simpleMerge();
			} else {
				parent.merge();
			}

			List<ClubbedEntity> clubbedEntities = parent
					.findClubbedEntitiesByDiscussionDateMotionNumber(ApplicationConstants.ASC, locale);
			Integer position = 1;
			for (ClubbedEntity i : clubbedEntities) {
				i.setPosition(position);
				position++;
				i.merge();
			}
		}
	}

	public String findAllMemberNames(String nameFormat) {
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Member member = null;
		String memberName = "";
		/** primary member **/
		member = this.getPrimaryMember();
		if (member == null) {
			return allMemberNamesBuffer.toString();
		}
		memberName = member.findNameInGivenFormat(nameFormat);
		if (memberName != null && !memberName.isEmpty()) {
			if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
				allMemberNamesBuffer.append(memberName);
			}
		} else {
			return allMemberNamesBuffer.toString();
		}
		/** supporting members **/
		List<SupportingMember> supportingMembers = this.getSupportingMembers();
		if (supportingMembers != null) {
			for (SupportingMember sm : supportingMembers) {
				member = sm.getMember();
				Status approvalStatus = sm.getDecisionStatus();
				if (member != null && approvalStatus != null
						&& approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if (memberName != null && !memberName.isEmpty()
							&& !allMemberNamesBuffer.toString().contains(memberName)) {
						if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
							if (allMemberNamesBuffer.length() > 0) {
								allMemberNamesBuffer.append(", " + memberName);
							} else {
								allMemberNamesBuffer.append(memberName);
							}
						}
					}
				}
			}
		}
		/** clubbed questions members **/
		List<ClubbedEntity> clubbedEntities = DiscussionMotion.findClubbedEntitiesByPosition(this,
				ApplicationConstants.DESC);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getDiscussionMotion().getInternalStatus().getType()
						.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
						|| ce.getDiscussionMotion().getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)) {
					member = ce.getDiscussionMotion().getPrimaryMember();
					if (member != null) {
						memberName = member.findNameInGivenFormat(nameFormat);
						if (memberName != null && !memberName.isEmpty()
								&& !allMemberNamesBuffer.toString().contains(memberName)) {
							if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
								if (allMemberNamesBuffer.length() > 0) {
									allMemberNamesBuffer.append(", " + memberName);
								} else {
									allMemberNamesBuffer.append(memberName);
								}
							}
						}
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getDiscussionMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							Status approvalStatus = csm.getDecisionStatus();
							if (member != null && approvalStatus != null && approvalStatus.getType()
									.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								memberName = member.findNameInGivenFormat(nameFormat);
								if (memberName != null && !memberName.isEmpty()
										&& !allMemberNamesBuffer.toString().contains(memberName)) {
									if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
										if (allMemberNamesBuffer.length() > 0) {
											allMemberNamesBuffer.append(", " + memberName);
										} else {
											allMemberNamesBuffer.append(memberName);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return allMemberNamesBuffer.toString();
	}

	public String findAllMemberNamesWithConstituencies(String nameFormat) {
		Session session = this.getSession();
		House questionHouse = session.getHouse();
		Date currentDate = new Date();
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Member member = null;
		String memberName = "";
		String constituencyName = "";

		/** primary member **/
		member = this.getPrimaryMember();
		if (member == null) {
			return allMemberNamesBuffer.toString();
		}
		memberName = member.findNameInGivenFormat(nameFormat);
		if (memberName != null && !memberName.isEmpty()) {
			if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
				allMemberNamesBuffer.append(memberName);
				constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate,
						currentDate);
				if (!constituencyName.isEmpty()) {
					allMemberNamesBuffer.append(" (" + constituencyName + ")");
				}
			}
		} else {
			return allMemberNamesBuffer.toString();
		}

		/** supporting members **/
		List<SupportingMember> supportingMembers = this.getSupportingMembers();
		if (supportingMembers != null) {
			for (SupportingMember sm : supportingMembers) {
				member = sm.getMember();
				Status approvalStatus = sm.getDecisionStatus();
				if (member != null && approvalStatus != null
						&& approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if (memberName != null && !memberName.isEmpty()
							&& !allMemberNamesBuffer.toString().contains(memberName)) {
						if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
							if (allMemberNamesBuffer.length() > 0) {
								allMemberNamesBuffer.append(", " + memberName);
							} else {
								allMemberNamesBuffer.append(memberName);
							}
							constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE",
									currentDate, currentDate);
							if (!constituencyName.isEmpty()) {
								allMemberNamesBuffer.append(" (" + constituencyName + ")");
							}
						}
					}
				}
			}
		}

		/** clubbed questions members **/
		List<ClubbedEntity> clubbedEntities = DiscussionMotion.findClubbedEntitiesByPosition(this,
				ApplicationConstants.DESC);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getDiscussionMotion().getInternalStatus().getType()
						.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
						|| ce.getDiscussionMotion().getInternalStatus().getType()
								.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
						|| ce.getDiscussionMotion().getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)) {
					member = ce.getDiscussionMotion().getPrimaryMember();
					if (member != null) {
						memberName = member.findNameInGivenFormat(nameFormat);
						if (memberName != null && !memberName.isEmpty()
								&& !allMemberNamesBuffer.toString().contains(memberName)) {
							if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
								if (allMemberNamesBuffer.length() > 0) {
									allMemberNamesBuffer.append(", " + memberName);
								} else {
									allMemberNamesBuffer.append(memberName);
								}
								constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE",
										currentDate, currentDate);
								if (!constituencyName.isEmpty()) {
									allMemberNamesBuffer.append(" (" + constituencyName + ")");
								}
							}
						}
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getDiscussionMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							Status approvalStatus = csm.getDecisionStatus();
							if (member != null && approvalStatus != null && approvalStatus.getType()
									.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								memberName = member.findNameInGivenFormat(nameFormat);
								if (memberName != null && !memberName.isEmpty()
										&& !allMemberNamesBuffer.toString().contains(memberName)) {
									if (member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
										if (allMemberNamesBuffer.length() > 0) {
											allMemberNamesBuffer.append(", " + memberName);
										} else {
											allMemberNamesBuffer.append(memberName);
										}
										constituencyName = member.findConstituencyNameForYadiReport(questionHouse,
												"DATE", currentDate, currentDate);
										if (!constituencyName.isEmpty()) {
											allMemberNamesBuffer.append(" (" + constituencyName + ")");
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return allMemberNamesBuffer.toString();
	}

	public Workflow findWorkflowFromStatus() throws ELSException {
		Workflow workflow = Workflow.findByStatus(this.getInternalStatus(), this.getLocale());
		return workflow;
	}

	public void removeExistingWorkflowAttributes() {
		// Update question so as to remove existing workflow
		// based attributes
		this.setEndFlag(null);
		this.setLevel("1");
		this.setTaskReceivedOn(null);
		this.setWorkflowDetailsId(null);
		this.setWorkflowStarted("NO");
		this.setWorkflowStartedOn(null);
		this.setActor(null);
		this.setLocalizedActorName("");
		this.simpleMerge();
	}

	public void startWorkflow(final DiscussionMotion discussionMotion, final Status status,
			final UserGroupType userGroupType, final Integer level, final String workflowHouseType,
			final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
		// end current workflow if exists
		discussionMotion.endWorkflow(discussionMotion, workflowHouseType, locale);
		// update motion statuses as per the workflow status
		discussionMotion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
		// find required workflow from the status
		Workflow workflow = Workflow.findByStatus(status, locale);
		// start required workflow
		WorkflowDetails.startProcessAtGivenLevel(discussionMotion, ApplicationConstants.APPROVAL_WORKFLOW, workflow,
				userGroupType, level, locale);
	}

	public void endWorkflow(final DiscussionMotion discussionMotion, final String workflowHouseType,
			final String locale) throws ELSException {
		WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(discussionMotion);
		if (wfDetails != null && wfDetails.getId() != null) {
			try {
				WorkflowDetails.endProcess(wfDetails);
			} catch (Exception e) {
				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
				wfDetails.setCompletionTime(new Date());
				wfDetails.merge();
			} finally {
				discussionMotion.removeExistingWorkflowAttributes();
			}
		} else {
			discussionMotion.removeExistingWorkflowAttributes();
		}
	}

	public void updateForInitFlow(final Status status, final UserGroupType userGroupType,
			final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) {
		/** update statuses for the required flow **/
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("locale", new String[] { locale });
		parameterMap.put("flowStatusType", new String[] { status.getType() });
		parameterMap.put("isAfterFinalDecision", new String[] { isFlowOnRecomStatusAfterFinalDecision.toString() });
		parameterMap.put("userGroupType", new String[] { userGroupType.getType() });
		List statusRecommendations = Query
				.findReport(ApplicationConstants.QUERYNAME_STATUS_RECOMMENDATIONS_FOR_INIT_FLOW, parameterMap);
		if (statusRecommendations != null && !statusRecommendations.isEmpty()) {
			Object[] statuses = (Object[]) statusRecommendations.get(0);
			if (statuses[0] != null && !statuses[0].toString().isEmpty()) {
				Status mainStatus = Status.findByType(statuses[0].toString(), locale);
				this.setStatus(mainStatus);
			}
			if (statuses[1] != null && !statuses[1].toString().isEmpty()) {
				Status internalStatus = Status.findByType(statuses[1].toString(), locale);
				this.setInternalStatus(internalStatus);
			}
			if (statuses[2] != null && !statuses[2].toString().isEmpty()) {
				Status recommendationStatus = Status.findByType(statuses[2].toString(), locale);
				this.setRecommendationStatus(recommendationStatus);
			}
			this.simpleMerge();
		}
	}

	public List<ClubbedEntity> findClubbedEntitiesByDiscussionDateMotionNumber(final String sortOrder,
			final String locale) {
		return getDiscussionMotionRepository().findClubbedEntitiesByDiscussionDateMotionNumber(this, sortOrder, locale);
	}

	// ************************Clubbing**********************

	// ************************Unclubbing********************
	public static boolean unclub(final Long m1, final Long m2, String locale) throws ELSException {
		DiscussionMotion motion1 = DiscussionMotion.findById(DiscussionMotion.class, m1);
		DiscussionMotion motion2 = DiscussionMotion.findById(DiscussionMotion.class, m2);
		return unclub(motion1, motion2, locale);
	}

	public static boolean unclub(final DiscussionMotion q1, final DiscussionMotion q2, String locale)
			throws ELSException {
		boolean clubbingStatus = false;
		if (q1.getParent() == null && q2.getParent() == null) {
			throw new ELSException("error", "CLUBBED_MOTION_NOT_FOUND");
		}
		if (q2.getParent() != null && q2.getParent().equals(q1)) {
			clubbingStatus = actualUnclubbing(q1, q2, locale);
		} else if (q1.getParent() != null && q1.getParent().equals(q2)) {
			clubbingStatus = actualUnclubbing(q2, q1, locale);
		} else {
			throw new ELSException("error", "NO_CLUBBING_BETWEEN_GIVEN_MOTIONS");
		}
		return clubbingStatus;
	}

	public static boolean unclub(final DiscussionMotion motion, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if (motion.getParent() == null) {
			throw new ELSException("error", "MOTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(motion.getParent(), motion, locale);
		return clubbingStatus;
	}

	public static boolean actualUnclubbing(final DiscussionMotion parent, final DiscussionMotion child, String locale)
			throws ELSException {
		boolean clubbingStatus = false;
		clubbingStatus = actualUnclubbingMotions(parent, child, locale);
		return clubbingStatus;
	}

	public static boolean actualUnclubbingMotions(final DiscussionMotion parent, final DiscussionMotion child,
			String locale) throws ELSException {

		boolean retVal = false;
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class,
				ApplicationConstants.DISCUSSIONMOTION_CLUBBING_MODE, "");

		if (csptClubbingMode != null && csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()
				&& csptClubbingMode.getValue().equals("normal")) {
			Status putupStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED,
					locale);
			Status submitStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SUBMIT, locale);

			/**
			 * remove child's clubbing entitiy from parent & update parent's
			 * clubbing entities
			 **/
			List<ClubbedEntity> oldClubbedMotions = parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedMotions = new ArrayList<ClubbedEntity>();
			Integer position = 0;
			boolean found = false;

			for (ClubbedEntity i : oldClubbedMotions) {
				if (!i.getDiscussionMotion().getId().equals(child.getId())) {
					if (found) {
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedMotions.add(i);
					} else {
						newClubbedMotions.add(i);
					}
				} else {
					found = true;
					position = i.getPosition();
				}
			}
			if (!newClubbedMotions.isEmpty()) {
				parent.setClubbedEntities(newClubbedMotions);
			} else {
				parent.setClubbedEntities(null);
			}
			parent.simpleMerge();

			/** break child's clubbing **/
			child.setParent(null);
			child.setInternalStatus(putupStatus);
			child.setRecommendationStatus(putupStatus);
			child.setStatus(submitStatus);
			child.merge();
			retVal = true;
		} else {
			/**
			 * if child was clubbed with speaker/chairman approval then put up
			 * for unclubbing workflow
			 **/
			// TODO: write condition for above case & initiate code to send for
			// unclubbing workflow
			Status approvedStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION, locale);
			if (child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority()) >= 0
					&& !child.getRecommendationStatus()
							.equals(ApplicationConstants.DISCUSSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus()
							.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus()
							.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				Status putupUnclubStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_UNCLUBBING,
						locale);
				child.setRecommendationStatus(putupUnclubStatus);
				child.merge();
				retVal = true;
			} else {
				/**
				 * remove child's clubbing entitiy from parent & update parent's
				 * clubbing entities
				 **/
				List<ClubbedEntity> oldClubbedMotions = parent.getClubbedEntities();
				List<ClubbedEntity> newClubbedMotions = new ArrayList<ClubbedEntity>();
				Integer position = 0;
				boolean found = false;
				for (ClubbedEntity i : oldClubbedMotions) {
					if (!i.getDiscussionMotion().getId().equals(child.getId())) {
						if (found) {
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedMotions.add(i);
						} else {
							newClubbedMotions.add(i);
						}
					} else {
						found = true;
						position = i.getPosition();
					}
				}
				if (!newClubbedMotions.isEmpty()) {
					parent.setClubbedEntities(newClubbedMotions);
				} else {
					parent.setClubbedEntities(null);
				}
				parent.simpleMerge();
				/** break child's clubbing **/
				child.setParent(null);
				/** find & end current clubbing workflow of child if pending **/
				String pendingWorkflowTypeForMotion = "";
				if (child.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_CLUBBING)
						|| child.getInternalStatus().getType()
								.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if (child.getInternalStatus().getType()
						.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_NAME_CLUBBING)
						|| child.getInternalStatus().getType()
								.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if (child.getRecommendationStatus().getType()
						.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| child.getRecommendationStatus().getType()
								.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if (pendingWorkflowTypeForMotion != null && !pendingWorkflowTypeForMotion.isEmpty()) {
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child,
							pendingWorkflowTypeForMotion);
					WorkflowDetails.endProcess(wfDetails);
					child.removeExistingWorkflowAttributes();
				}
				/** update child status **/
				Status putupStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED,
						locale);
				Status admitStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION, locale);
				if (child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority()) < 0) {
					child.setInternalStatus(putupStatus);
					child.setRecommendationStatus(putupStatus);
				} else {
					/*
					 * if(child.getReply()==null || child.getReply().isEmpty())
					 * { child.setInternalStatus(admitStatus);
					 * child.setRecommendationStatus(admitStatus); Workflow
					 * processWorkflow = Workflow.findByStatus(admitStatus,
					 * locale); UserGroupType assistantUGT =
					 * UserGroupType.findByType(ApplicationConstants.ASSISTANT,
					 * locale); WorkflowDetails.startProcessAtGivenLevel(child,
					 * ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow,
					 * assistantUGT, 6, locale); } else {
					 */
					child.setInternalStatus(admitStatus);
					Status answerReceivedStatus = Status
							.findByType(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_ANSWER_RECEIVED, locale);
					child.setRecommendationStatus(answerReceivedStatus);
					// }
				}
			}
			child.merge();
			retVal = true;
		}

		return retVal;
	}
	// ************************Unclubbing********************

	// ************************Clubbing unclubbing update*********************
	/**** Motion Update Clubbing Starts ****/

	public static boolean isAdmittedThroughClubbing(final DiscussionMotion discussionmotion) {
		return getDiscussionMotionRepository().isAdmittedThroughClubbing(discussionmotion);
	}

	public static void updateClubbing(DiscussionMotion motion) throws ELSException {
		// case 1: motion is child
		if (motion.getParent() != null) {
			DiscussionMotion.updateClubbingForChild(motion);
		}
		// case 2: motion is parent
		else if (motion.getParent() == null && motion.getClubbedEntities() != null
				&& !motion.getClubbedEntities().isEmpty()) {
			DiscussionMotion.updateClubbingForParent(motion);
		}
	}

	private static void updateClubbingForChild(DiscussionMotion motion) throws ELSException {
		updateClubbingForChildMotion(motion);
	}

	private static void updateClubbingForChildMotion(DiscussionMotion motion) throws ELSException {
		String locale = motion.getLocale();
		DiscussionMotion parentMotion = motion.getParent();

		Status putupStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED,
				motion.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION, motion.getLocale());

		// if(motion.isFromDifferentBatch(parentMotion)) {
		//
		// if(parentMotion.getNumber().compareTo(motion.getNumber())<0) {
		//
		// updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);
		//
		// if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0)
		// {
		// Status clubbedStatus =
		// Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED,
		// motion.getLocale());
		// motion.setInternalStatus(clubbedStatus);
		// motion.setRecommendationStatus(clubbedStatus);
		// } else {
		// motion.setStatus(parentMotion.getInternalStatus());
		// motion.setInternalStatus(parentMotion.getInternalStatus());
		// motion.setRecommendationStatus(parentMotion.getInternalStatus());
		// }
		// motion.simpleMerge();
		//
		// } else if(parentMotion.getNumber().compareTo(motion.getNumber())>0) {
		//
		// WorkflowDetails parentMoion_workflowDetails =
		// WorkflowDetails.findCurrentWorkflowDetail(parentMotion);
		// if(parentMoion_workflowDetails!=null) {
		// parentMoion_workflowDetails.endProcess();
		// parentMotion.removeExistingWorkflowAttributes();
		// }
		// if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0)
		// {
		// motion.setInternalStatus(putupStatus);
		// motion.setRecommendationStatus(putupStatus);
		//
		// //updateDomainFieldsOnClubbingFinalisation(question,
		// parentDiscussionMotion);
		//
		// Status clubbedStatus =
		// Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED,
		// motion.getLocale());
		// actualClubbingMotions(motion, parentMotion, clubbedStatus,
		// clubbedStatus, locale);
		// } else {
		// motion.setStatus(parentMotion.getInternalStatus());
		// motion.setInternalStatus(parentMotion.getInternalStatus());
		// if(parentMotion.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION))
		// {
		// Status admitDueToReverseClubbingStatus =
		// Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING,
		// motion.getLocale());
		// motion.setRecommendationStatus(admitDueToReverseClubbingStatus);
		// Workflow admitDueToReverseClubbingWorkflow =
		// Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
		// WorkflowDetails.startProcess(motion,
		// ApplicationConstants.APPROVAL_WORKFLOW,
		// admitDueToReverseClubbingWorkflow, locale);
		// } else {
		// //TODO:handle case when parent is already rejected.. below is
		// temporary fix
		// //clarification from ketkip remaining
		// motion.setRecommendationStatus(parentMotion.getInternalStatus());
		//
		// }
		// if(parentMotion.getReply()!=null && (motion.getReply()==null ||
		// motion.getReply().isEmpty())) {
		// motion.setReply(parentMotion.getReply());
		// }
		// updateDomainFieldsOnClubbingFinalisation(motion, parentMotion);
		//
		// actualClubbingMotions(motion, parentMotion,
		// parentMotion.getInternalStatus(), parentMotion.getInternalStatus(),
		// locale);
		// }
		// }
		// } else
		{

			updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);

			if (parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_CLUBBED,
						motion.getLocale());
				motion.setInternalStatus(clubbedStatus);
				motion.setRecommendationStatus(clubbedStatus);
			} else {
				motion.setStatus(parentMotion.getInternalStatus());
				motion.setInternalStatus(parentMotion.getInternalStatus());
				motion.setRecommendationStatus(parentMotion.getInternalStatus());
			}
			motion.simpleMerge();
		}
	}

	public static void updateDomainFieldsOnClubbingFinalisation(DiscussionMotion parent, DiscussionMotion child) {
		updateDomainFieldsOnClubbingFinalisationForMotion(parent, child);
	}

	private static void updateDomainFieldsOnClubbingFinalisationForMotion(DiscussionMotion parent,
			DiscussionMotion child) {
		updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
	}

	private static void updateDomainFieldsOnClubbingFinalisationCommon(DiscussionMotion parent,
			DiscussionMotion child) {
		/** copy latest subject of parent to revised subject of child **/
		if (parent.getRevisedSubject() != null && !parent.getRevisedSubject().isEmpty()) {
			child.setRevisedSubject(parent.getRevisedSubject());
		} else {
			child.setRevisedSubject(parent.getSubject());
		}

		/**
		 * copy latest details text of parent to revised details text of child
		 **/
		if (parent.getRevisedNoticeContent() != null && !parent.getRevisedNoticeContent().isEmpty()) {
			child.setRevisedNoticeContent(parent.getRevisedNoticeContent());
		} else {
			child.setRevisedNoticeContent(parent.getNoticeContent());
		}
	}

	private static void updateClubbingForParent(DiscussionMotion motion) {
		updateClubbingForParentMotion(motion);
	}

	private static void updateClubbingForParentMotion(DiscussionMotion motion) {
		for (ClubbedEntity ce : motion.getClubbedEntities()) {
			DiscussionMotion clubbedMotion = ce.getDiscussionMotion();
			if (clubbedMotion.getInternalStatus().getType()
					.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_CLUBBED)) {

				updateDomainFieldsOnClubbingFinalisation(motion, clubbedMotion);

				clubbedMotion.setStatus(motion.getInternalStatus());
				clubbedMotion.setInternalStatus(motion.getInternalStatus());
				clubbedMotion.setRecommendationStatus(motion.getInternalStatus());
				clubbedMotion.merge();
			}
		}
	}

	public static Reference getCurNumber(final Session session, final DeviceType deviceType) {

		Reference ref = new Reference();
		String strHouseType = session.getHouse().getType().getType();
		String strDeviceType = deviceType.getType();

		if (strHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			if (strDeviceType.equals(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION)) {

				ref.setName(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION);
				ref.setNumber(DiscussionMotion.getShortDurationMotionCurrentNumberLowerHouse().toString());
				ref.setId(ApplicationConstants.LOWER_HOUSE);

			} else if (strDeviceType.equals(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE)) {

				ref.setName(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE);
				ref.setNumber(DiscussionMotion.getPublicImportanceMotionCurrentNumberLowerHouse().toString());
				ref.setId(ApplicationConstants.LOWER_HOUSE);

			} else if (strDeviceType.equals(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK)) {

				ref.setName(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK);
				ref.setNumber(DiscussionMotion.getLastWeekMotionCurrentNumberLowerHouse().toString());
				ref.setId(ApplicationConstants.LOWER_HOUSE);

			}
		} else if (strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {

			if (strDeviceType.equals(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION)) {

				ref.setName(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION);
				ref.setNumber(DiscussionMotion.getShortDurationMotionCurrentNumberUpperHouse().toString());
				ref.setId(ApplicationConstants.UPPER_HOUSE);

			} else if (strDeviceType.equals(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE)) {

				ref.setName(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE);
				ref.setNumber(DiscussionMotion.getPublicImportanceMotionCurrentNumberUpperHouse().toString());
				ref.setId(ApplicationConstants.UPPER_HOUSE);

			} else if (strDeviceType.equals(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK)) {

				ref.setName(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK);
				ref.setNumber(DiscussionMotion.getLastWeekMotionCurrentNumberUpperHouse().toString());
				ref.setId(ApplicationConstants.UPPER_HOUSE);

			}
		}

		return ref;
	}

	public static void updateCurNumber(Integer num, String houseType, String device) {

		if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			if (device.equals(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION)) {

				DiscussionMotion.updateShortDurationMotionCurrentNumberLowerHouse(num);

			} else if (device.equals(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE)) {

				DiscussionMotion.updatePublicImportanceMotionCurrentNumberLowerHouse(num);

			} else if (device.equals(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK)) {

				DiscussionMotion.updateLastWeekMotionCurrentNumberLowerHouse(num);

			}
		} else if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			if (device.equals(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION)) {

				DiscussionMotion.updateShortDurationMotionCurrentNumberUpperHouse(num);

			} else if (device.equals(ApplicationConstants.DISCUSSIONMOTION_PUBLICIMPORTANCE)) {

				DiscussionMotion.updatePublicImportanceMotionCurrentNumberUpperHouse(num);

			} else if (device.equals(ApplicationConstants.DISCUSSIONMOTION_LASTWEEK)) {

				DiscussionMotion.updateLastWeekMotionCurrentNumberUpperHouse(num);

			}
		}
	}

	/**** Starred atomic value ****/
	public static void updateShortDurationMotionCurrentNumberLowerHouse(Integer num) {
		synchronized (DiscussionMotion.SHORTNOTICE_MOTION_CUR_NUM_LOWER_HOUSE) {
			DiscussionMotion.SHORTNOTICE_MOTION_CUR_NUM_LOWER_HOUSE = num;
		}
	}

	public static synchronized Integer getShortDurationMotionCurrentNumberLowerHouse() {
		return DiscussionMotion.SHORTNOTICE_MOTION_CUR_NUM_LOWER_HOUSE;
	}

	public static void updateShortDurationMotionCurrentNumberUpperHouse(Integer num) {
		synchronized (DiscussionMotion.SHORTNOTICE_MOTION_CUR_NUM_UPPER_HOUSE) {
			DiscussionMotion.SHORTNOTICE_MOTION_CUR_NUM_UPPER_HOUSE = num;
		}
	}

	public static synchronized Integer getShortDurationMotionCurrentNumberUpperHouse() {
		return DiscussionMotion.SHORTNOTICE_MOTION_CUR_NUM_UPPER_HOUSE;
	}

	/**** Public Importance atomic value ****/
	public static void updatePublicImportanceMotionCurrentNumberLowerHouse(Integer num) {
		synchronized (DiscussionMotion.PUBLICIMPORTANCE_MOTION_CUR_NUM_LOWER_HOUSE) {
			DiscussionMotion.PUBLICIMPORTANCE_MOTION_CUR_NUM_LOWER_HOUSE = num;
		}
	}

	public static synchronized Integer getPublicImportanceMotionCurrentNumberLowerHouse() {
		return DiscussionMotion.PUBLICIMPORTANCE_MOTION_CUR_NUM_LOWER_HOUSE;
	}

	public static void updatePublicImportanceMotionCurrentNumberUpperHouse(Integer num) {
		synchronized (DiscussionMotion.PUBLICIMPORTANCE_MOTION_CUR_NUM_UPPER_HOUSE) {
			DiscussionMotion.PUBLICIMPORTANCE_MOTION_CUR_NUM_UPPER_HOUSE = num;
		}
	}

	public static synchronized Integer getPublicImportanceMotionCurrentNumberUpperHouse() {
		return DiscussionMotion.PUBLICIMPORTANCE_MOTION_CUR_NUM_UPPER_HOUSE;
	}

	/**** Last Week Motion atomic value ****/
	public static void updateLastWeekMotionCurrentNumberLowerHouse(Integer num) {
		synchronized (DiscussionMotion.LASTWEEK_MOTION_CUR_NUM_LOWER_HOUSE) {
			DiscussionMotion.LASTWEEK_MOTION_CUR_NUM_LOWER_HOUSE = num;
		}
	}

	public static synchronized Integer getLastWeekMotionCurrentNumberLowerHouse() {
		return DiscussionMotion.LASTWEEK_MOTION_CUR_NUM_LOWER_HOUSE;
	}

	public static void updateLastWeekMotionCurrentNumberUpperHouse(Integer num) {
		synchronized (DiscussionMotion.LASTWEEK_MOTION_CUR_NUM_UPPER_HOUSE) {
			DiscussionMotion.LASTWEEK_MOTION_CUR_NUM_UPPER_HOUSE = num;
		}
	}

	public static synchronized Integer getLastWeekMotionCurrentNumberUpperHouse() {
		return DiscussionMotion.LASTWEEK_MOTION_CUR_NUM_UPPER_HOUSE;
	}

	// ************************Clubbing unclubbing update**********************

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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
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

	public Status getBallotStatus() {
		return ballotStatus;
	}

	public void setBallotStatus(Status ballotStatus) {
		this.ballotStatus = ballotStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
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

	public List<Ministry> getMinistries() {
		return ministries;
	}

	public void setMinistries(List<Ministry> ministry) {
		this.ministries = ministry;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> department) {
		this.departments = department;
	}

	public List<SubDepartment> getSubDepartments() {
		return subDepartments;
	}

	public void setSubDepartments(List<SubDepartment> subDepartment) {
		this.subDepartments = subDepartment;
	}

	public List<DiscussionMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<DiscussionMotionDraft> drafts) {
		this.drafts = drafts;
	}

	public DiscussionMotion getParent() {
		return parent;
	}

	public void setParent(DiscussionMotion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public List<ReferenceUnit> getReferencedEntities() {
		return referencedEntities;
	}

	public void setReferencedEntities(List<ReferenceUnit> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public DeviceType getOriginalType() {
		return originalType;
	}

	public void setOriginalType(DeviceType originalType) {
		this.originalType = originalType;
	}

	public String getBriefExplanation() {
		return briefExplanation;
	}

	public void setBriefExplanation(String briefExplanation) {
		this.briefExplanation = briefExplanation;
	}

	public String getRevisedBriefExplanation() {
		return revisedBriefExplanation;
	}

	public void setRevisedBriefExplanation(String revisedBriefExplanation) {
		this.revisedBriefExplanation = revisedBriefExplanation;
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

	public String getClarification() {
		return clarification;
	}

	public void setClarification(String clarification) {
		this.clarification = clarification;
	}

	public Date getLastDateOfClarificationReceiving() {
		return lastDateOfClarificationReceiving;
	}
	
	
	public Status getDiscussionStatus() {
		return discussionStatus;
	}

	public void setDiscussionStatus(Status discussionStatus) {
		this.discussionStatus = discussionStatus;
	}

	public void setLastDateOfClarificationReceiving(Date lastDateOfClarificationReceiving) {
		this.lastDateOfClarificationReceiving = lastDateOfClarificationReceiving;
	}

	public Integer getNumberOfDaysForClarificationReceiving() {
		return numberOfDaysForClarificationReceiving;
	}

	public void setNumberOfDaysForClarificationReceiving(Integer numberOfDaysForClarificationReceiving) {
		this.numberOfDaysForClarificationReceiving = numberOfDaysForClarificationReceiving;
	}

	public static MemberMinister findMemberMinisterIfExists(final DiscussionMotion discussionmotion)
			throws ELSException {
		return getDiscussionMotionRepository().findMemberMinisterIfExists(discussionmotion);
	}

	public static MemberMinister findMemberMinisterIfExists(final DiscussionMotion discussionmotion,
			final Ministry ministry) throws ELSException {
		return getDiscussionMotionRepository().findMemberMinisterIfExists(discussionmotion, ministry);
	}

	public static void supportingMemberWorkflowDeletion(final DiscussionMotion discussionMotion) {
		if (discussionMotion != null && discussionMotion.getId() > 0) {
			if (anySupportingMembersWorkflows(discussionMotion)) {
				deleteSupportingMembersWorkflows(discussionMotion);
			}
		}
	}

	public static boolean anySupportingMembersWorkflows(final DiscussionMotion discussionMotion) {
		List<SupportingMember> supportingMembers = discussionMotion.getSupportingMembers();
		if (supportingMembers != null && supportingMembers.size() > 0) {
			for (SupportingMember sm : supportingMembers) {
				if (sm.getWorkflowDetailsId() != null && sm.getWorkflowDetailsId().trim().length() > 0)
					return true;
			}
		}
		return false;
	}

	public static boolean deleteSupportingMembersWorkflows(final DiscussionMotion discussionMotion) {
		List<Long> workflowDetailsList = new ArrayList<Long>();
		if (discussionMotion != null && discussionMotion.getId() > 0 && discussionMotion.getSupportingMembers() != null
				&& discussionMotion.getSupportingMembers().size() > 0) {
			List<SupportingMember> supportingMembers = discussionMotion.getSupportingMembers();
			for (SupportingMember sm : supportingMembers) {
				if (sm.getWorkflowDetailsId() != null && sm.getWorkflowDetailsId().trim().length() > 0)
					workflowDetailsList.add(Long.valueOf(sm.getWorkflowDetailsId()));
			}
		}

		int deleteCount = 0;
		for (Long workFlowDetailsId : workflowDetailsList) {
			BaseDomain workFlowdetails = WorkflowDetails.findById(WorkflowDetails.class, workFlowDetailsId);
			boolean isDeleted = WorkflowDetails.getBaseRepository().remove(workFlowdetails);
			if (isDeleted)
				deleteCount++;
		}

		return workflowDetailsList != null && deleteCount == workflowDetailsList.size();
	}
	
	
	public static List<DiscussionMotion> findAllAdmittedUndisccussed(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale){
		return  getDiscussionMotionRepository().findAllAdmittedUndisccussed(session, motionType, status, locale);
	}
	
	
	public static List<Object> getDiscussionMotionDetailsMemberStatsReport(final Session session,final DeviceType deviceType,final Member memberId){
		return getDiscussionMotionRepository().getDiscussionMotionDetailsMemberStatsReport(session, deviceType, memberId);
	}
	
	 public static List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
				Map<String, String[]> requestMap) {
			return getDiscussionMotionRepository().fullTextSearchForSearching(param,start,noOfRecords, locale, requestMap);
		}
}
