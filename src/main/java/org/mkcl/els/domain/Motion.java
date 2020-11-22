/*
 * 
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.persistence.OneToMany;
import javax.persistence.OptimisticLockException;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.repository.MotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class Motion.
 */
@Configurable
@Entity
@Table(name="motions")
@JsonIgnoreProperties({"houseType", "session", "type", 
	"recommendationStatus", "supportingMembers","ballotStatus",
	"department", "drafts", "parent", "clubbedEntities", "referencedUnits"})
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
	
	/** numner assigned afer ballot **/
	private Integer postBallotNumber;

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
	
	/** The submission priority 
     *  To be used for bulk submission by member
     */
    private Integer submissionPriority;
	
//	/** The sections. */
//    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
//    @JoinTable(name="motions_amendments",
//    joinColumns={@JoinColumn(name="motion_id", referencedColumnName="id")},
//    inverseJoinColumns={@JoinColumn(name="amendment_id", referencedColumnName="id")})
//    private List<Amendment> amendments;

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

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ballotstatus_id")
	private Status ballotStatus;
	
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
	@JoinTable(name="motions_referencedunits", 
			joinColumns={@JoinColumn(name="motion_id", referencedColumnName="id")}, 
			inverseJoinColumns={@JoinColumn(name="referenced_unit_id", referencedColumnName="id")})
			private List<ReferenceUnit> referencedUnits;

	/**** Reply(Nivedan) ****/
	@Column(length=30000)
	private String reply;

	/**** Answering Date ****/
	private Date answeringDate;
	
	/**** Discussion Date ****/
	private Date discussionDate; 
	
	@Column(length = 3000)	
	private String refText;
	
	/**** Motion Reply(Nivedan) Date ****/
	private Date replyReceivedDate;
	
    /**** Fields for storing the confirmation of Group change ****/
    private Boolean transferToDepartmentAccepted = false;
    
    private Boolean mlsBranchNotifiedOfTransfer = false;
    
    /**** Advance Copy for Department Related Field  ****/
    private Boolean advanceCopySent = false;
    
    private Boolean advanceCopyPrinted = false;
    
	private String advanceCopyActor;
    
	/****Factual Position****/
	private String factualPositionFromDepartment;
	
	private String factualPositionFromMember;
	
	/**** Synch variables for motion lower house****/
	private transient volatile static Integer CALLING_ATTENTION_CUR_NUM_LOWER_HOUSE = 0;
	
	/**** Synch variables for motion upper house****/
	private transient volatile static Integer CALLING_ATTENTION_CUR_NUM_UPPER_HOUSE = 0;

	/** The question repository. */
	@Autowired
	private transient MotionRepository motionRepository;

	/**** Constructors ****/   
	public Motion(final HouseType houseType, final Session session, final DeviceType type,
			final Integer number, final Date submissionDate, final Date creationDate,
			final String createdBy, final Date editedOn, final String editedBy, final String editedAs,
			final String subject, final String revisedSubject, final String details,
			final String revisedDetails, /*final List<Amendment> amendments,*/ final Status status, final Status internalStatus,
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
//		this.amendments = amendments;
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
				synchronized (Motion.class) {
					
					Integer number = null;
					String houseType = this.getHouseType().getType();
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
						if(Motion.getCallingAttentionCurrentNumberLowerHouse() == 0){
							number = Motion.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							Motion.updateCallingAttentionCurrentNumberLowerHouse(number);
						}
					}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(Motion.getCallingAttentionCurrentNumberUpperHouse() == 0){
							number = Motion.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							Motion.updateCallingAttentionCurrentNumberUpperHouse(number);
						}
					}
					
					/*Integer persistPostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
					if(persistPostBallotNumber > 0){
						this.setPostBallotNumber(persistPostBallotNumber + 1);
					}*/
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(Motion.getCallingAttentionCurrentNumberLowerHouse() + 1);
            			this.setSubmissionDate(new Date());
            			Motion.updateCallingAttentionCurrentNumberLowerHouse(Motion.getCallingAttentionCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(Motion.getCallingAttentionCurrentNumberUpperHouse() + 1);
            			this.setSubmissionDate(new Date());
            			Motion.updateCallingAttentionCurrentNumberUpperHouse(Motion.getCallingAttentionCurrentNumberUpperHouse() + 1);
            		}
					addMotionDraft();
					return (Motion)super.persist();
				}
			}else if(this.getNumber()!=null){
            	addMotionDraft();
            }
		}
		return (Motion) super.persist();
	}

	public static Integer assignMotionNo(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) {
		return getMotionRepository().assignMotionNo(houseType,
				session,type,locale);
	}
	
	public static int findReadyToSubmitCount(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {
		return getMotionRepository().findReadyToSubmitCount(session, primaryMember, deviceType, locale);
	}
	
	public static List<Motion> findReadyToSubmitMotions(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {
		return getMotionRepository().findReadyToSubmitMotions(session, primaryMember, deviceType, locale);
	}

	public static Integer findMaxPostBallotNo(final HouseType houseType,
			final Session session,
			final DeviceType type,
			final String locale) {
		return getMotionRepository().findMaxPostBallotNo(houseType, session, type, locale);
	}
	
	public static Boolean isExist(Integer number, DeviceType deviceType, Session session,String locale) {
		 return getMotionRepository().isExist(number, deviceType, session, locale);
	 }
	
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final String sortOrder,
    		final String locale) {
    	return getMotionRepository().findClubbedEntitiesByMotionNumber(this,sortOrder, locale);
    }
	
	private void addMotionDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.MOTION_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.MOTION_COMPLETE)) {
			MotionDraft draft = new MotionDraft();
			draft.setLocale(this.getLocale());
			draft.setRemarks(this.getRemarks());
			draft.setParent(this.getParent());
			draft.setClubbedEntities(this.getClubbedEntities());
			draft.setReferencedUnits(referencedUnits);
			draft.setEditedAs(this.getEditedAs());
			draft.setEditedBy(this.getEditedBy());
			draft.setEditedOn(this.getEditedOn());	            
			draft.setMinistry(this.getMinistry());
			draft.setDepartment(this.getDepartment());
			draft.setSubDepartment(this.getSubDepartment());	            
			draft.setStatus(this.getStatus());
			draft.setInternalStatus(this.getInternalStatus());
			draft.setRecommendationStatus(this.getRecommendationStatus());
			draft.setMlsBranchNotifiedOfTransfer(this.getMlsBranchNotifiedOfTransfer());
			draft.setTransferToDepartmentAccepted(this.getTransferToDepartmentAccepted());
			draft.setAdvanceCopyActor(this.getAdvanceCopyActor());
			draft.setAdvanceCopySent(this.getAdvanceCopySent());
			draft.setAdvanceCopyPrinted(this.getAdvanceCopyPrinted());
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
		if(this.getInternalStatus().getType().equals(
				ApplicationConstants.MOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (Motion.class) {
					Integer number = null;
					//TODO: may needed for maintaining postBallotNumber in other batch motions  
					/*Integer mergePostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
					if(mergePostBallotNumber > 0){
						this.setPostBallotNumber(mergePostBallotNumber + 1);
					}*/
					
					String houseType = this.getHouseType().getType();
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
						if(Motion.getCallingAttentionCurrentNumberLowerHouse() == 0){
							number = Motion.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							Motion.updateCallingAttentionCurrentNumberLowerHouse(number);
						}
					}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(Motion.getCallingAttentionCurrentNumberUpperHouse() == 0){
							number = Motion.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							Motion.updateCallingAttentionCurrentNumberUpperHouse(number);
						}
					}
					
					/*Integer persistPostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
					if(persistPostBallotNumber > 0){
						this.setPostBallotNumber(persistPostBallotNumber + 1);
					}*/
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(Motion.getCallingAttentionCurrentNumberLowerHouse() + 1);
            			this.setSubmissionDate(new Date());
            			Motion.updateCallingAttentionCurrentNumberLowerHouse(Motion.getCallingAttentionCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(Motion.getCallingAttentionCurrentNumberUpperHouse() + 1);
            			this.setSubmissionDate(new Date());
            			Motion.updateCallingAttentionCurrentNumberUpperHouse(Motion.getCallingAttentionCurrentNumberUpperHouse() + 1);
            		}
					addMotionDraft();
					motion = (Motion) super.merge();
				}
			}else {
				Motion oldMotion = Motion.findById(Motion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				if(this.getReferencedUnits() == null){
					this.setReferencedUnits(oldMotion.getReferencedUnits());
				}
				this.addMotionDraft();
				motion = (Motion) super.merge();
			}
		}else if(this.getInternalStatus().getType().
        		equals(ApplicationConstants.MOTION_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().
            	equals(ApplicationConstants.MOTION_COMPLETE)){
        	return (Motion) super.merge();
        }
		
		if(motion != null) {
			return motion;
		}else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_INCOMPLETE) 
					|| 
					this.getInternalStatus().getType().equals(ApplicationConstants.MOTION_COMPLETE)) {
				return (Motion) super.merge();
			}else {
				Motion oldMotion = Motion.findById(Motion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}	
				if(this.getReferencedUnits() == null){
					this.setReferencedUnits(oldMotion.getReferencedUnits());
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
	
	public String findDiscussionDetailsText() {
		String discussionDetailsText = "";
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{this.getLocale()});
		parametersMap.put("motionId", new String[]{this.getId().toString()});
		@SuppressWarnings("rawtypes")
		List discussionDetailsTextResult = org.mkcl.els.domain.Query.findReport("MOTION_DISCUSSION_DETAILS_TEXT", parametersMap);
		if(discussionDetailsTextResult!=null && !discussionDetailsTextResult.isEmpty()) {
			if(discussionDetailsTextResult.get(0)!=null) {
				discussionDetailsText = discussionDetailsTextResult.get(0).toString();
			}
		}
		return discussionDetailsText;
	}

	public static List<Motion> findAllByMember(final Session session,
			final Member primaryMember,final DeviceType motionType,final Integer itemsCount,
			final String locale) {
		return getMotionRepository().findAllByMember(session,
				primaryMember,motionType,itemsCount,
				locale);
	}
	
	public static List<Motion> findAllByMemberBatchWise(final Session session,
			final Member primaryMember,
			final DeviceType motionType, 
			final Date startTime,
			final Date endTime,
			final String locale) {
		return getMotionRepository().findAllByMemberBatchWise(session, primaryMember, motionType, startTime, endTime, locale);
	}
	
	public static List<Motion> findAllByBatch(final Session session,
			final DeviceType motionType, 
			final Date startTime,
			final Date endTime,
			final String locale) {
		
		return getMotionRepository().findAllByBatch(session, motionType, startTime, endTime, locale);
	}
	
	public static List<Motion> findAllUndiscussed(final Session session,
			final DeviceType motionType,
			final Status status,
			final String locale) {
		
		return getMotionRepository().findAllUndiscussed(session, motionType, status, locale);
	}

	public static List<Motion> findAllUndiscussedByMember(final Session session,
			final DeviceType motionType,
			final Status status,
			final Member primaryMember,
			final String locale) {
		
		return getMotionRepository().findAllUndiscussedByMember(session, motionType, status, primaryMember, locale);
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

	public static List<Motion> findAllAdmittedUndisccussed(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale){
		return getMotionRepository().findAllAdmittedUndisccussed(session, motionType, status, locale);
	}
	
	public static List<Motion> findAllByStatus(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale){
		return getMotionRepository().findAllByStatus(session, motionType, status, locale);
	}
	
	public static List<Motion> findAllForDiscussion(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale){
		return getMotionRepository().findAllForDiscussion(session, motionType, status, locale);
	}
			
	public static List<Motion> findAllByMember(final Session session,
			final DeviceType motionType, 
			final Status status,
			final Member primaryMember,
			final String locale){
		return getMotionRepository().findAllByMember(session, motionType, status, primaryMember, locale);
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
	/*
	 * TODO change and confirm then proceed
	 */
	public static boolean assignPostBallotNumber(final Session session,
			final DeviceType deviceType, final String locale) {
//		List<MemberBallot> memberBallots = MemberBallot.findBySessionDeviceType(session, deviceType, locale);
//		for(MemberBallot mb : memberBallots){
//			List<Motion> motions = Motion.findAllByMember(session, mb.getMember(), deviceType, 100, locale);
//		}
		return false;
	}
	
	public static List<Motion> findAllCompleteByCreator(Session session,
			String username, DeviceType motionType, Integer itemsCount,
			String strLocale) {
		return getMotionRepository().findAllCompleteByCreator(session, username, motionType, itemsCount, strLocale);
	}
	
	public Status findMemberStatus() {	
		Status memberStatus = null;
		if(this.getStatus()!=null) {
			Status submitStatus = Status.findByType(ApplicationConstants.MOTION_SUBMIT, this.getLocale());
			if(this.getStatus().getPriority()>=submitStatus.getPriority()) {
				memberStatus = submitStatus;
			} else {
				memberStatus = this.getStatus();
			}
		}		
		
		return memberStatus;
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

	public Integer getPostBallotNumber() {
		return postBallotNumber;
	}

	public void setPostBallotNumber(Integer postBallotNumber) {
		this.postBallotNumber = postBallotNumber;
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

//	public List<Amendment> getAmendments() {
//		return amendments;
//	}
//
//	public void setAmendments(List<Amendment> amendments) {
//		this.amendments = amendments;
//	}

	public Integer getSubmissionPriority() {
		return submissionPriority;
	}

	public void setSubmissionPriority(Integer submissionPriority) {
		this.submissionPriority = submissionPriority;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getBallotStatus() {
		return ballotStatus;
	}

	public void setBallotStatus(Status ballotStatus) {
		this.ballotStatus = ballotStatus;
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

	public List<ReferenceUnit> getReferencedUnits() {
		return referencedUnits;
	}

	public void setReferencedUnits(List<ReferenceUnit> referencedUnits) {
		this.referencedUnits = referencedUnits;
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
	
	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public Date getDiscussionDate() {
		return discussionDate;
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
		
	public String getRefText() {
		return refText;
	}

	public void setRefText(String refText) {
		this.refText = refText;
	}
	
	public Boolean getTransferToDepartmentAccepted() {
		return transferToDepartmentAccepted;
	}

	public void setTransferToDepartmentAccepted(Boolean transferToDepartmentAccepted) {
		this.transferToDepartmentAccepted = transferToDepartmentAccepted;
	}

	public Boolean getMlsBranchNotifiedOfTransfer() {
		return mlsBranchNotifiedOfTransfer;
	}

	public void setMlsBranchNotifiedOfTransfer(Boolean mlsBranchNotifiedOfTransfer) {
		this.mlsBranchNotifiedOfTransfer = mlsBranchNotifiedOfTransfer;
	}
	
	public Boolean getAdvanceCopySent() {
		return advanceCopySent;
	}

	public void setAdvanceCopySent(Boolean advanceCopySent) {
		this.advanceCopySent = advanceCopySent;
	}

	public Boolean getAdvanceCopyPrinted() {
		return advanceCopyPrinted;
	}

	public void setAdvanceCopyPrinted(Boolean advanceCopyPrinted) {
		this.advanceCopyPrinted = advanceCopyPrinted;
	}
	
	public String getAdvanceCopyActor() {
		return advanceCopyActor;
	}

	public void setAdvanceCopyActor(String advanceCopyActor) {
		this.advanceCopyActor = advanceCopyActor;
	}

	
	public Date getReplyReceivedDate() {
		return replyReceivedDate;
	}

	public void setReplyReceivedDate(Date replyReceivedDate) {
		this.replyReceivedDate = replyReceivedDate;
	}
	
	
	public String getFactualPositionFromDepartment() {
		return factualPositionFromDepartment;
	}

	public void setFactualPositionFromDepartment(String factualPositionFromDepartment) {
		this.factualPositionFromDepartment = factualPositionFromDepartment;
	}

	public String getFactualPositionFromMember() {
		return factualPositionFromMember;
	}

	public void setFactualPositionFromMember(String factualPositionFromMember) {
		this.factualPositionFromMember = factualPositionFromMember;
	}
	
	/**
     * Sort the Motions as per @param sortOrder by submission priority. If multiple Motions
     * have same submission priority, then their order is preserved.
     *
     * @param motions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<Motion> sortBySubmissionPriority(final List<Motion> motions,
            final String sortOrder) {
        List<Motion> newMotionsList = new ArrayList<Motion>();
        newMotionsList.addAll(motions);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<Motion> c = new Comparator<Motion>() {

                @Override
                public int compare(final Motion m1, final Motion m2) {
                    return m1.getSubmissionPriority().compareTo(m2.getSubmissionPriority());
                }
            };
            Collections.sort(newMotionsList, c);
        } else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<Motion> c = new Comparator<Motion>() {

                @Override
                public int compare(final Motion m1, final Motion m2) {
                    return m2.getSubmissionPriority().compareTo(m1.getSubmissionPriority());
                }
            };
            Collections.sort(newMotionsList, c);
        }

        return newMotionsList;
    }

	//************************Clubbing**********************
	public static boolean club(final Long primary, final Long clubbing, final String locale) throws ELSException{
		
		Motion m1 = Motion.findById(Motion.class, primary);
		Motion m2 = Motion.findById(Motion.class, clubbing);
		
		return club(m1, m2, locale); 
		
	}

	public static boolean club(final Motion q1,final Motion q2,final String locale) throws ELSException{    	
    	boolean clubbingStatus = false;
    	try {    		
    		if(q1.getParent()!=null || q2.getParent()!=null) {
    			throw new ELSException("error", "MOTION_ALREADY_CLUBBED");
    		} else {
    			if(q1.getType().getType().equals(ApplicationConstants.MOTION_CALLING_ATTENTION)
        				&& q2.getType().getType().equals(ApplicationConstants.MOTION_CALLING_ATTENTION)) {
    				
    					clubbingStatus = clubMotions(q1, q2, locale);    				
        		} else {
        			return false;
        		}    			
    		}    		
    	} catch(ELSException ex){
    		throw ex;
		} catch(Exception ex){
    		//logger.error("CLUBBING_FAILED",ex);
			clubbingStatus = false;
			return clubbingStatus;
		}        
        return clubbingStatus;
    }
	
	private static boolean clubMotions(Motion q1, Motion q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForMotion(q1, q2, locale);
    	if(clubbingStatus) {
    		
    		clubbingStatus = clubMotion(q1, q2, locale);

    	}    	 
    	return clubbingStatus;
    }
	
	private static boolean clubbingRulesForMotion(Motion q1, Motion q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("workflow")){ 
	    	if(clubbingStatus) {
	    		if(q1.getReply()!=null && !q1.getReply().isEmpty()) {
	    			WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    			if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
	    				throw new ELSException("error", "MOTION_ANSWERED_BUT_FLOW_PENDING");
	    			}
	    		}
	    		if(q2.getReply()!=null && !q2.getReply().isEmpty()) {
	    			WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    			if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
	    				throw new ELSException("error", "MOTION_ANSWERED_BUT_FLOW_PENDING");
	    			}
	    		}    		
	    	}
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubMotion(Motion q1, Motion q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubMotionsBH(q1, q2, locale);
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("workflow")){
	    	if(!clubbingStatus) {
	    		//=============cases specific to lowerhouse============//
	        	/** get chart answering dates for questions **/
	        	Date q1_AnsweringDate = q1.getAnsweringDate();
	        	Date q2_AnsweringDate = q2.getAnsweringDate();
	        	
	        	Status yaadiLaidStatus = Status.findByType(ApplicationConstants.MOTION_PROCESSED_YAADILAID, locale);
	        	
	        	//Case 7: Both questions are admitted and balloted
	        	if(q1.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
	    				&& q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0
	    				&& (q1.getBallotStatus()!=null && q1.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
	    				&& (q2.getBallotStatus()!=null && q2.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))) {
	        		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
	        		if(q1_AnsweringDate.compareTo(q2_AnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	        				clubbingStatus = true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        				clubbingStatus = true;
	        			} else {
	        				clubbingStatus = true;
	        			}
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)<0) {
	        			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			clubbingStatus = true;
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)>0) {
	        			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			clubbingStatus = true;
	        		}
	        	}
	    	}    	
    	}
    	
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesCommon(Motion q1, Motion q2, String locale) throws ELSException {
    	CustomParameter csptEscapeCheckingofFieldsForClubbing = CustomParameter.findByName(CustomParameter.class, "MOTION_CLUBBING_ESCAPE_FIELDS", "");
    	if(csptEscapeCheckingofFieldsForClubbing == null){
    		throw new ELSException("error", "MOTION_CLUBBING_ESCAPE_FIELDS not set");
    	}
    	
    	if(csptEscapeCheckingofFieldsForClubbing.getValue() == null ||
    			csptEscapeCheckingofFieldsForClubbing.getValue().isEmpty()){
    		throw new ELSException("error", "MOTION_CLUBBING_ESCAPE_FIELDS not set");
    	}
    	
    	boolean flagSession = false;    	
    	if(q1.getSession().equals(q2.getSession()) && !q1.getType().getType().equals(q2.getType().getType())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_DEVICETYPE");    		
    	}else{
    		flagSession = true;
    	}
    	
    	boolean flagMinistry = false;
    	if(csptEscapeCheckingofFieldsForClubbing.getValue().contains("ministry")){
    		flagMinistry = true;
    	}else if(!q1.getMinistry().getName().equals(q2.getMinistry().getName())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_MINISTRY");    		
    	}
    	 
    	boolean flagSubDepartment = false;
    	if(csptEscapeCheckingofFieldsForClubbing.getValue().contains("subDepartment")){
    		flagSubDepartment = true;
    	}else if(!q1.getSubDepartment().getName().equals(q2.getSubDepartment().getName())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_DEPARTMENT");    		
    	}   	
    	
    	return (flagSession && flagMinistry && flagSubDepartment);
    }
    
    private static boolean clubMotionsBH(Motion q1, Motion q2, String locale) throws ELSException {
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("normal")){
    		if(q1.getNumber().compareTo(q2.getNumber())<0) {
				actualClubbingMotions(q1, q2, q1.getInternalStatus(), q1.getRecommendationStatus(), locale);
				WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(q2);
				if(wfOfChild != null){
					WorkflowDetails.endProcess(wfOfChild);
				}
				q2.removeExistingWorkflowAttributes();
				return true;
			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
				actualClubbingMotions(q2, q1, q2.getInternalStatus(), q2.getRecommendationStatus(), locale);
				
				WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(q1);
				if(wfOfChild != null){
					WorkflowDetails.endProcess(wfOfChild);
				}
				q1.removeExistingWorkflowAttributes();
				
				return true;
			} else {
				return false;
			}
    	}else{
	    	/** get answering dates for motions **/
	    	Date q1_AnsweringDate = q1.getAnsweringDate();
	    	Date q2_AnsweringDate = q2.getAnsweringDate();
	    	
	    	Status putupStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status approvalStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);
	    	
	    	//Case 1: Both motions are just ready to be put up
	    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
	    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		
	    		Status clubbedStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, locale);
	    		if(q1_AnsweringDate != null && q2_AnsweringDate != null){
	    			if(q1_AnsweringDate.compareTo(q2_AnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {
	        				actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)<0) {
	        			actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	        			return true;
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)>0) {
	        			actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		}else{
	    			
	    			if(q1.getNumber().compareTo(q2.getNumber())<0) {
	    				actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	    				return true;
	    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	    				actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
	    				return true;
	    			} else {
	    				return false;
	    			}
	    		}
	    	} 
	    	//Case 2A: One motion is pending in approval workflow while other is ready to be put up
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_CLUBBING, locale);
	    		actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 2B: One motion is pending in approval workflow while other is ready to be put up
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
	    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_CLUBBING, locale);
	    		actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 3: Both motions are pending in approval workflow
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
	    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_CLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	    		if(q1_approvalLevel==q2_approvalLevel) {
	    			if(q1_AnsweringDate.compareTo(q2_AnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				WorkflowDetails.endProcess(q2_workflowDetails);
	        				q2.removeExistingWorkflowAttributes();
	        				actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				WorkflowDetails.endProcess(q1_workflowDetails);
	        				q1.removeExistingWorkflowAttributes();
	        				actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)<0) {
	        			WorkflowDetails.endProcess(q2_workflowDetails);
	        			q2.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	        			return true;
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)>0) {
	        			WorkflowDetails.endProcess(q1_workflowDetails);
	        			q1.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		} else if(q1_approvalLevel>q2_approvalLevel) {
	    			WorkflowDetails.endProcess(q2_workflowDetails);
	    			q2.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    			return true;
	    		} else if(q1_approvalLevel<q2_approvalLevel) {
	    			WorkflowDetails.endProcess(q1_workflowDetails);
	    			q1.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    			return true;
	    		} else {
	    			return false;
	    		}    		
	    	}
	    	//Case 4A: One motion is admitted but not balloted yet while other motion is ready to be put up (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
					&& (q1.getBallotStatus()==null || !q1.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_NAME_CLUBBING, locale);
	    		actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 4B: One motion is admitted but not balloted yet while other motion is ready to be put up (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
					&& (q2.getBallotStatus()==null || !q2.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_NAME_CLUBBING, locale);
	    		actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 5A: One motion is admitted but not balloted yet while other motion is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
					&& (q1.getBallotStatus()==null || !q1.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))
					&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_NAME_CLUBBING, locale);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		WorkflowDetails.endProcess(q2_workflowDetails);
	    		q2.removeExistingWorkflowAttributes();
	    		actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 5B: One motion is admitted but not balloted yet while other motion is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
					&& (q2.getBallotStatus()==null || !q2.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_NAME_CLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails.endProcess(q1_workflowDetails);
	    		q1.removeExistingWorkflowAttributes();
	    		actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 6: Both motions are admitted but not balloted
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
	    				&& (q1.getBallotStatus()==null || !q1.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
	    				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().equals(ApplicationConstants.MOTION_PROCESSED_BALLOTED))) {
	    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
	    			if(q1_AnsweringDate.compareTo(q2_AnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)<0) {
	        			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)>0) {
	        			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
	    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	        		if(q1_approvalLevel==q2_approvalLevel) {
	        			if(q1_AnsweringDate.compareTo(q2_AnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
	            			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	            				WorkflowDetails.endProcess(q2_workflowDetails);
	            				q2.removeExistingWorkflowAttributes();
	            				actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	            				return true;
	            			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	            				WorkflowDetails.endProcess(q1_workflowDetails);
	            				q1.removeExistingWorkflowAttributes();
	            				actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	            				return true;
	            			} else {
	            				return false;
	            			}
	            		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)<0) {
	            			WorkflowDetails.endProcess(q2_workflowDetails);
	            			q2.removeExistingWorkflowAttributes();
	            			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	            			return true;
	            		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)>0) {
	            			WorkflowDetails.endProcess(q1_workflowDetails);
	            			q1.removeExistingWorkflowAttributes();
	            			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	            			return true;
	            		} else {
	            			return false;
	            		}
	        		} else if(q1_approvalLevel>q2_approvalLevel) {
	        			WorkflowDetails.endProcess(q2_workflowDetails);
	        			q2.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else if(q1_approvalLevel<q2_approvalLevel) {
	        			WorkflowDetails.endProcess(q1_workflowDetails);
	        			q1.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
	    			WorkflowDetails.endProcess(q2_workflowDetails);
	    			q2.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
					return true;
	    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
	    			WorkflowDetails.endProcess(q1_workflowDetails);
	    			q1.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	}    	
	    	else {
	    		return false;
	    	}
    	}
    }  
    
    public Boolean isFromDifferentBatch(Motion q) {
		Boolean isFromDifferentBatch = false;
		if(q!=null && this.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
				&& q.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
				&& this.getSession().getId().equals(q.getSession().getId())) {
			String firstBatchStartDateParameter=this.getSession().getParameter(ApplicationConstants.MOTION_FIRST_BATCH_START_TIME);
			String firstBatchEndDateParameter=this.getSession().getParameter(ApplicationConstants.MOTION_FIRST_BATCH_END_TIME);
			if(firstBatchStartDateParameter!=null&&firstBatchEndDateParameter!=null){
				if((!firstBatchStartDateParameter.isEmpty())&&(!firstBatchEndDateParameter.isEmpty())){
					Date firstBatchStartDate = FormaterUtil.formatStringToDate(firstBatchStartDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					Date firstBatchEndDate = FormaterUtil.formatStringToDate(firstBatchEndDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					String this_batch = "";
					if(this.getSubmissionDate().compareTo(firstBatchStartDate)>=0
							&& this.getSubmissionDate().compareTo(firstBatchEndDate)<=0) {
						this_batch = "FIRST_BATCH";
					} else if(this.getSubmissionDate().compareTo(firstBatchEndDate)>0) {
						this_batch = "SECOND_BATCH";
					}
					String q_batch = "";
					if(q.getSubmissionDate().compareTo(firstBatchStartDate)>=0
							&& q.getSubmissionDate().compareTo(firstBatchEndDate)<=0) {
						q_batch = "FIRST_BATCH";
					} else if(this.getSubmissionDate().compareTo(firstBatchEndDate)>0) {
						q_batch = "SECOND_BATCH";
					}
					if(!this_batch.isEmpty() && !q_batch.isEmpty() && !this_batch.equals(q_batch)) {
						isFromDifferentBatch = true;
					}
				}
			}
		}
		return isFromDifferentBatch;
	}
    
    private static void actualClubbingMotions(Motion parent, Motion child,
			Status newInternalStatus, Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent motion are obtained 
		 * b.Clubbed entities of child motion are obtained
		 * c.Child motion is updated(parent,internal status,recommendation status) 
		 * d.Child Motion entry is made in Clubbed Entity and child motion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child motions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child motion clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent motion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMnId = child.getId();
				Motion clubbedMn = i.getMotion();
				Long clubbedMnId = clubbedMn.getId();
				if(! childMnId.equals(clubbedMnId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMnId = parent.getId();
				Motion clubbedMn = i.getMotion();
				Long clubbedMnId = clubbedMn.getId();
				if(! parentMnId.equals(clubbedMnId)) {
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
		clubbedEntity.setMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_CLUBBING_MODE, "");
		if(csptClubbingMode != null){
			if(csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()){
			
				if(csptClubbingMode.getValue().equals("normal")){
					//Status clubbed = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, locale);
					
					if(childClubbedEntities != null && !childClubbedEntities.isEmpty()){
						for(ClubbedEntity k : childClubbedEntities){
							Motion motion = k.getMotion();					
							
							WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(motion);
							if(wd != null){
								WorkflowDetails.endProcess(wd);
								motion.removeExistingWorkflowAttributes();
							}
							
							motion.setInternalStatus(parent.getInternalStatus());
							motion.setRecommendationStatus(parent.getRecommendationStatus());
							motion.setStatus(parent.getStatus());
							motion.setParent(parent);
							motion.merge();
							parentClubbedEntities.add(k);
						}			
					}
					
				}else if(csptClubbingMode.getValue().equals("workflow")){
					if(childClubbedEntities != null && !childClubbedEntities.isEmpty()){
						for(ClubbedEntity k:childClubbedEntities){
							Motion motion=k.getMotion();					
							/** find current clubbing workflow if pending **/
							String pendingWorkflowTypeForMotion = "";
							if(motion.getType().getType().equals(ApplicationConstants.MOTION_CALLING_ATTENTION)) {
								if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_CLUBBING)
										|| motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLUBBING)) {
									pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
								} else if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_NAME_CLUBBING)
										|| motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)) {
									pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
								} else if(motion.getRecommendationStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
										|| motion.getRecommendationStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)) {
									pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
								}
							}
							
							if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
								/** end current clubbing workflow **/
								WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion, pendingWorkflowTypeForMotion);
								if(wfDetails != null){
									WorkflowDetails.endProcess(wfDetails);
									motion.removeExistingWorkflowAttributes();
								}
								/** put up for proper clubbing workflow as per updated parent **/
								Status finalAdmitStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION , locale);
								Integer parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();					
								Integer motion_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
								
								
								if(parent.getStatus().getPriority().compareTo(parent_finalAdmissionStatusPriority)<0) {
									Status putupForClubbingStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_CLUBBING , locale);
									motion.setInternalStatus(putupForClubbingStatus);
									motion.setRecommendationStatus(putupForClubbingStatus);
								} else {
									if(motion.getStatus().getPriority().compareTo(motion_finalAdmissionStatusPriority)<0) {
										Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_NAME_CLUBBING , locale);
										motion.setInternalStatus(putupForNameClubbingStatus);
										motion.setRecommendationStatus(putupForNameClubbingStatus);
									} else {
										Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
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
			if(parent.getParent()!=null) {
				isChildBecomingParentCase = true;
				parent.setParent(null);
			}		
			parent.setClubbedEntities(parentClubbedEntities);
			if(isChildBecomingParentCase) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
				parent.simpleMerge();
			} else {
				parent.merge();
			}		
	
			List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByAnsweringDateMotionNumber(ApplicationConstants.ASC,locale);
			Integer position=1;
			for(ClubbedEntity i:clubbedEntities){
				i.setPosition(position);
				position++;
				i.merge();
			}
		}
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
    
    public List<ClubbedEntity> findClubbedEntitiesByAnsweringDateMotionNumber(final String sortOrder, final String locale) {
    	return getMotionRepository().findClubbedEntitiesByAnsweringDateMotionNumber(this,sortOrder, locale);
    }
    
	//************************Clubbing**********************
    
    //************************Unclubbing********************
    public static boolean unclub(final Long m1, final Long m2, String locale) throws ELSException {
		Motion motion1 = Motion.findById(Motion.class, m1);
		Motion motion2 = Motion.findById(Motion.class, m2);
		return unclub(motion1, motion2, locale);
	}
    
    public static boolean unclub(final Motion q1, final Motion q2, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(q1.getParent()==null && q2.getParent()==null) {
			throw new ELSException("error", "CLUBBED_MOTION_NOT_FOUND");
		}
		if(q2.getParent()!=null && q2.getParent().equals(q1)) {
			clubbingStatus = actualUnclubbing(q1, q2, locale);
		} else if(q1.getParent()!=null && q1.getParent().equals(q2)) {
			clubbingStatus = actualUnclubbing(q2, q1, locale);
		} else {
			throw new ELSException("error", "NO_CLUBBING_BETWEEN_GIVEN_MOTIONS");
		}
		return clubbingStatus;
	}
	
	public static boolean unclub(final Motion motion, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(motion.getParent()==null) {
			throw new ELSException("error", "MOTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(motion.getParent(), motion, locale);
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbing(final Motion parent, final Motion child, String locale) throws ELSException {
		boolean clubbingStatus = false;
		clubbingStatus = actualUnclubbingMotions(parent, child, locale);		
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbingMotions(final Motion parent, final Motion child, String locale) throws ELSException {
		
		boolean retVal = false;
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("normal")){
    		Status putupStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
    		Status submitStatus = Status.findByType(ApplicationConstants.MOTION_SUBMIT, locale);
    		
    		/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedMotions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedMotions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			
			for(ClubbedEntity i:oldClubbedMotions){
				if(!i.getMotion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedMotions.add(i);
					}else{
						newClubbedMotions.add(i);                		
					}
				}else{
					found = true;
					position = i.getPosition();
				}
			}
			if(!newClubbedMotions.isEmpty()){
				parent.setClubbedEntities(newClubbedMotions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			
			/**break child's clubbing **/
			child.setParent(null);
			child.setInternalStatus(putupStatus);
			child.setRecommendationStatus(putupStatus);
			child.setStatus(submitStatus);
			child.merge();
			retVal = true;
		}else{	
			/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
			//TODO: write condition for above case & initiate code to send for unclubbing workflow
			Status approvedStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);		
			if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
					&& !child.getRecommendationStatus().equals(ApplicationConstants.MOTION_PUTUP_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus().equals(ApplicationConstants.MOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus().equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				Status putupUnclubStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_UNCLUBBING, locale);
				child.setRecommendationStatus(putupUnclubStatus);
				child.merge();
				retVal = true;
			} else {
				/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
				List<ClubbedEntity> oldClubbedMotions=parent.getClubbedEntities();
				List<ClubbedEntity> newClubbedMotions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedMotions){
					if(! i.getMotion().getId().equals(child.getId())){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedMotions.add(i);
						}else{
							newClubbedMotions.add(i);                		
						}
					}else{
						found=true;
						position=i.getPosition();
					}
				}
				if(!newClubbedMotions.isEmpty()){
					parent.setClubbedEntities(newClubbedMotions);
				}else{
					parent.setClubbedEntities(null);
				}            
				parent.simpleMerge();
				/**break child's clubbing **/
				child.setParent(null);
				/** find & end current clubbing workflow of child if pending **/
				String pendingWorkflowTypeForMotion = "";
				if(child.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_CLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(child.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_NAME_CLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| child.getRecommendationStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForMotion);	
					WorkflowDetails.endProcess(wfDetails);
					child.removeExistingWorkflowAttributes();
				}
				/** update child status **/
				Status putupStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				Status admitStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);
				if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
					child.setInternalStatus(putupStatus);
					child.setRecommendationStatus(putupStatus);
				} else {
					if(child.getReply()==null || child.getReply().isEmpty()) {
						child.setInternalStatus(admitStatus);
						child.setRecommendationStatus(admitStatus);
						Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
						UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
						WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 1, locale);//6
					} else {
						child.setInternalStatus(admitStatus);
						Status answerReceivedStatus = Status.findByType(ApplicationConstants.MOTION_PROCESSED_ANSWER_RECEIVED, locale);
						child.setRecommendationStatus(answerReceivedStatus);
					}
				}
			}	
			child.merge();
			retVal = true;
		}
    	
    	return retVal;
	}
    //************************Unclubbing********************
	
	//************************Reverse Clubbing********************
	public static boolean reverseClub(final Motion motion) throws ELSException {
		boolean reverseClubStatus = false;
		if(motion.getParent()!=null) {
			System.out.println("reverse clubbing in progress..");
			CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_CLUBBING_MODE, "");
	    	if(csptClubbingMode!=null && csptClubbingMode.getValue()!=null && csptClubbingMode.getValue().equalsIgnoreCase(ApplicationConstants.CLUBBING_MODE_APPROVAL_WORKFLOW)) {
	    		//reverse clubbing with approval workflow
	    		reverseClubStatus = reverseClubWithApprovalWorkflow(motion);
	    	} else {
	    		//normal reverse clubbing
	    		reverseClubStatus = reverseClubWithoutApprovalWorkflow(motion);
	    	}
		}
		return reverseClubStatus;
	}
	
	public static boolean reverseClubWithApprovalWorkflow(final Motion motion) throws ELSException {
		boolean reverseClubStatus = false;
		//TODO: process reverse clubbing with approval workflow
		return reverseClubStatus;
	}
	
	public static boolean reverseClubWithoutApprovalWorkflow(final Motion motion) throws ELSException {
		boolean reverseClubStatus = false;
		Status approvalStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, motion.getLocale());
		WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(motion.getParent());
		WorkflowDetails.endProcess(wfOfChild);	
		motion.getParent().removeExistingWorkflowAttributes();
		if(motion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) >= 0) {
			actualClubbingWithoutApprovalWorkflow(motion, motion.getParent(), motion.getInternalStatus(), motion.getInternalStatus(), motion.getInternalStatus(), motion.getLocale());						
		} else {
			//TODO: confirm the action below for parent pending while child having final decision
			Status submitStatus = Status.findByType(ApplicationConstants.MOTION_SUBMIT, motion.getLocale());
			Status clubbedStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, motion.getLocale());
			actualClubbingWithoutApprovalWorkflow(motion, motion.getParent(), submitStatus, clubbedStatus, clubbedStatus, motion.getLocale());
		}
		reverseClubStatus = true;
		return reverseClubStatus;
	}
	
	@Transactional(noRollbackFor={OptimisticLockException.class})
    private static void actualClubbingWithoutApprovalWorkflow(Motion parent,Motion child,
    		Status newStatus,Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
    	/**** a.Clubbed entities of parent motion are obtained 
		 * b.Clubbed entities of child motion are obtained
		 * c.Child motion is updated(parent,internal status,recommendation status) 
		 * d.Child Motion entry is made in Clubbed Entity and child motion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child motions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child motion clubbed entities,other clubbed entities of child motion and 
		 * clubbed entities of parent motion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their number ****/
    	List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMotionId = child.getId();
				Motion clubbedMotion = i.getMotion();
				Long clubbedMotionId = clubbedMotion.getId();
				if(! childMotionId.equals(clubbedMotionId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMotionId = parent.getId();
				Motion clubbedMotion = i.getMotion();
				Long clubbedMotionId = clubbedMotion.getId();
				if(! parentMotionId.equals(clubbedMotionId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setStatus(newStatus);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		Status approvalStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);
		if(newStatus.getPriority().compareTo(approvalStatus.getPriority()) >= 0) {
			updateDomainFieldsOnClubbingFinalisation(parent, child);
		}		
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);
		
		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Motion motion = k.getMotion();
				WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(motion);
				WorkflowDetails.endProcess(wd);		
				motion.removeExistingWorkflowAttributes();
				motion.setStatus(newStatus);
				motion.setInternalStatus(newInternalStatus);
				motion.setRecommendationStatus(newRecommendationStatus);
				if(newStatus.getPriority().compareTo(approvalStatus.getPriority()) >= 0) {
					updateDomainFieldsOnClubbingFinalisation(parent, motion);
				}
				motion.setEditedAs(child.getEditedAs());
				motion.setEditedBy(child.getEditedBy());
				motion.setEditedOn(child.getEditedOn());
				motion.setParent(parent);
				motion.merge();
				parentClubbedEntities.add(k);
			}
		}
		parent.setParent(null); //applicable in case of reverse clubbing
		parent.setClubbedEntities(parentClubbedEntities);
		parent.simpleMerge();		
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByMotionNumber(ApplicationConstants.ASC, locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
    }
	
	/**** Motion Update Clubbing Starts ****/
    public static void updateClubbing(Motion motion) throws ELSException {
		//case 1: motion is child
		if(motion.getParent()!=null) {
			Motion.updateClubbingForChild(motion);
		} 
		//case 2: motion is parent
		else if(motion.getParent()==null && motion.getClubbedEntities()!=null && !motion.getClubbedEntities().isEmpty()) {
			Motion.updateClubbingForParent(motion);
		}
	}
    
    private static void updateClubbingForChild(Motion motion) throws ELSException {
		updateClubbingForChildMotion(motion);
	}
    
    private static void updateClubbingForChildMotion(Motion motion) throws ELSException {
		String locale = motion.getLocale();
		Motion parentMotion = motion.getParent();
		    	
    	Status putupStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, motion.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, motion.getLocale());
		
		if(motion.isFromDifferentBatch(parentMotion)) {
			
			if(parentMotion.getNumber().compareTo(motion.getNumber())<0) {
				
				updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);
				
				if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					Status clubbedStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, motion.getLocale());
					motion.setInternalStatus(clubbedStatus);
					motion.setRecommendationStatus(clubbedStatus);
				} else {
					motion.setStatus(parentMotion.getInternalStatus());
					motion.setInternalStatus(parentMotion.getInternalStatus());
					motion.setRecommendationStatus(parentMotion.getInternalStatus());
				}				
				motion.simpleMerge();
				
			} else if(parentMotion.getNumber().compareTo(motion.getNumber())>0) {				
				
				WorkflowDetails parentMoion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentMotion);
				if(parentMoion_workflowDetails!=null) {
					WorkflowDetails.endProcess(parentMoion_workflowDetails);					
					parentMotion.removeExistingWorkflowAttributes();
				}
				if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					motion.setInternalStatus(putupStatus);
					motion.setRecommendationStatus(putupStatus);
					
					//updateDomainFieldsOnClubbingFinalisation(question, parentQuestion);
					
					Status clubbedStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, motion.getLocale());
					actualClubbingMotions(motion, parentMotion, clubbedStatus, clubbedStatus, locale);
				} else {
					motion.setStatus(parentMotion.getInternalStatus());
					motion.setInternalStatus(parentMotion.getInternalStatus());
					if(parentMotion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
						Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.MOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING, motion.getLocale());
						motion.setRecommendationStatus(admitDueToReverseClubbingStatus);
						Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
						WorkflowDetails.startProcess(motion, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
					} else {
						//TODO:handle case when parent is already rejected.. below is temporary fix
						//clarification from ketkip remaining
						motion.setRecommendationStatus(parentMotion.getInternalStatus());	
						
					}					
					if(parentMotion.getReply()!=null && (motion.getReply()==null || motion.getReply().isEmpty())) {
						motion.setReply(parentMotion.getReply());
					}
					updateDomainFieldsOnClubbingFinalisation(motion, parentMotion);
									
					actualClubbingMotions(motion, parentMotion, parentMotion.getInternalStatus(), parentMotion.getInternalStatus(), locale);
				}
			}
		} else {
			
				
			updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);
			
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, motion.getLocale());
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
    
    public static void updateDomainFieldsOnClubbingFinalisation(Motion parent, Motion child) {
    	updateDomainFieldsOnClubbingFinalisationForMotion(parent, child);		
    }
    
    private static void updateDomainFieldsOnClubbingFinalisationForMotion(Motion parent, Motion child) {
    	updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
    }
    
    private static void updateDomainFieldsOnClubbingFinalisationCommon(Motion parent, Motion child) {
		/** copy latest subject of parent to revised subject of child **/
		if(parent.getRevisedSubject()!=null && !parent.getRevisedSubject().isEmpty()) {
			child.setRevisedSubject(parent.getRevisedSubject());
		} else {
			child.setRevisedSubject(parent.getSubject());
		}
		/** copy latest details text of parent to revised details text of child **/
		if(parent.getRevisedDetails()!=null && !parent.getRevisedDetails().isEmpty()) {
			child.setRevisedDetails(parent.getRevisedDetails());
		} else {
			child.setRevisedDetails(parent.getDetails());
		}
		/** copy latest answer of parent to revised answer of child **/
		child.setReply(parent.getReply());
	}
    
    private static void updateClubbingForParent(Motion motion) {
    	updateClubbingForParentMotion(motion);		
	}

	private static void updateClubbingForParentMotion(Motion motion) {
		for(ClubbedEntity ce: motion.getClubbedEntities()) {
			Motion clubbedMotion = ce.getMotion();
			if(clubbedMotion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(motion, clubbedMotion);
				
				clubbedMotion.setStatus(motion.getInternalStatus());
				clubbedMotion.setInternalStatus(motion.getInternalStatus());
				clubbedMotion.setRecommendationStatus(motion.getInternalStatus());				
				clubbedMotion.merge();
			}
		}
	}
	
	public static String findAllMemberNames(Long id, String nameFormat) {
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Motion motion = Motion.findById(Motion.class, id);
		Member member = null;
		String memberName = "";				
		/** primary member **/
		member = motion.getPrimaryMember();		
		if(member==null) {
			return allMemberNamesBuffer.toString();
		}	
		memberName = member.findNameInGivenFormat(nameFormat);
		if(memberName!=null && !memberName.isEmpty()) {
			if(member.isSupportingOrClubbedMemberToBeAddedForDevice(motion)) {
				allMemberNamesBuffer.append(memberName);
			}						
		} else {
			return allMemberNamesBuffer.toString();
		}						
		/** supporting members **/
		List<SupportingMember> supportingMembers = motion.getSupportingMembers();
		if (supportingMembers != null) {
			for (SupportingMember sm : supportingMembers) {
				member = sm.getMember();
				Status approvalStatus = sm.getDecisionStatus();
				if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {				
						if(member.isSupportingOrClubbedMemberToBeAddedForDevice(motion)) {
							if(allMemberNamesBuffer.length()>0) {
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
		List<ClubbedEntity> clubbedEntities = Motion.findClubbedEntitiesByPosition(motion);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getMotion().getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_CLUBBED)
						|| ce.getMotion().getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
					member = ce.getMotion().getPrimaryMember();
					if(member!=null) {
						memberName = member.findNameInGivenFormat(nameFormat);
						if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(member.isSupportingOrClubbedMemberToBeAddedForDevice(motion)) {
								if(allMemberNamesBuffer.length()>0) {
									allMemberNamesBuffer.append(", " + memberName);
								} else {
									allMemberNamesBuffer.append(memberName);
								}
							}							
						}												
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							Status approvalStatus = csm.getDecisionStatus();
							if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								memberName = member.findNameInGivenFormat(nameFormat);
								if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
									if(member.isSupportingOrClubbedMemberToBeAddedForDevice(motion)) {
										if(allMemberNamesBuffer.length()>0) {
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
	
	
	/****Calling attention atomic value ****/
	public static void updateCallingAttentionCurrentNumberLowerHouse(Integer num){
		synchronized (Motion.CALLING_ATTENTION_CUR_NUM_LOWER_HOUSE) {
			Motion.CALLING_ATTENTION_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getCallingAttentionCurrentNumberLowerHouse(){
		return Motion.CALLING_ATTENTION_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateCallingAttentionCurrentNumberUpperHouse(Integer num){
		synchronized (Motion.CALLING_ATTENTION_CUR_NUM_UPPER_HOUSE) {
			Motion.CALLING_ATTENTION_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getCallingAttentionCurrentNumberUpperHouse(){
		return Motion.CALLING_ATTENTION_CUR_NUM_UPPER_HOUSE;
	}
	
	public static org.mkcl.els.common.vo.Reference getCurNumber(final Session session, final DeviceType deviceType){
    	
    	org.mkcl.els.common.vo.Reference ref = new org.mkcl.els.common.vo.Reference();
    	String strHouseType = session.getHouse().getType().getType();
    	
    	if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
    		
			ref.setName(ApplicationConstants.MOTION_CALLING_ATTENTION);
			ref.setNumber(Motion.getCallingAttentionCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
    		
    		ref.setName(ApplicationConstants.MOTION_CALLING_ATTENTION);
			ref.setNumber(Motion.getCallingAttentionCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    	}
    	
    	return ref;
    }
    
    public static void updateCurNumber(final Integer num, final String houseType, final String device){
    	
    	if(device.equals(ApplicationConstants.MOTION_CALLING_ATTENTION)){
    		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    			Motion.updateCallingAttentionCurrentNumberLowerHouse(num);
    		}
    		
    		if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    			Motion.updateCallingAttentionCurrentNumberUpperHouse(num);
    		}
	    	
	    	
    	}
    }
    
    private static boolean findAllowedInBatch(final Motion motion, final Date date, final Date startDate, final Date endDate){
    	boolean retVal = false; 
    	
    	if((date.compareTo(startDate) > 0 || date.compareTo(startDate) == 0) && (date.compareTo(endDate) < 0 || date.compareTo(endDate) == 0)){
    		retVal = true;
    	}
    	
    	return retVal;
    }
    
    
    public static boolean allowedInFirstBatch(final Motion motion, final Date date){
    	    	
    	Session session = motion.getSession();
    	Date firstBatchStartTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "firstBatchStartTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
    	Date firstBatchEndTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "firstBatchEndTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
    	
    	return findAllowedInBatch(motion, date, firstBatchStartTime, firstBatchEndTime);
    	
    }
    
    public static boolean allowedInSecondBatch(final Motion motion, final Date date){
    	
    	Session session = motion.getSession();
    	Date secondBatchStartTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "secondBatchStartTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
    	Date secondBatchEndTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "secondBatchEndTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);;
    	
    	return findAllowedInBatch(motion, date, secondBatchStartTime, secondBatchEndTime);
    }
    
    public static Integer findBatch(final Motion motion, final Date date){
    	
    	Integer batch = 0;
    	
    	Session session = motion.getSession();
    	
    	Date firstBatchStartTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "firstBatchStartTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
    	Date firstBatchEndTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "firstBatchEndTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
    	
    	Date secondBatchStartTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "secondBatchStartTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
    	Date secondBatchEndTime = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_" + "secondBatchEndTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);;
    	
    	if((date.compareTo(firstBatchStartTime) > 0 || date.compareTo(firstBatchStartTime) == 0) && (date.compareTo(firstBatchEndTime) < 0 || date.compareTo(firstBatchEndTime) == 0)){
    		batch = 1;
    	}
    	
    	if(!(batch > 0)){
    		if((date.compareTo(secondBatchStartTime) > 0 || date.compareTo(secondBatchStartTime) == 0) && (date.compareTo(secondBatchEndTime) < 0 || date.compareTo(secondBatchEndTime) == 0)){
    			batch = 2;
    		}
    	}
    	
    	return batch;
    }
    
    public static boolean isAllowedForSubmission(final Date submissionDate, final Date startDate, final Date endDate, final String locale){
    	boolean retVal = false;
    	if(submissionDate.compareTo(startDate)>=0 && submissionDate.compareTo(endDate)<=0){
    		retVal = true;
    	}
    	return retVal;
    }
    
    public static boolean isAllowedForSubmission(final Motion motion, final Date submissionDate, final String locale){
    	try{
	    	String strStartTime = motion.getSession().getParameter(motion.getType().getType()+"_submissionStartTime");
			String strEndTime = motion.getSession().getParameter(motion.getType().getType()+"_submissionEndTime");
			
			Date startDate = FormaterUtil.formatStringToDate(strStartTime, ApplicationConstants.SERVER_DATETIMEFORMAT);
			Date endDate = FormaterUtil.formatStringToDate(strEndTime, ApplicationConstants.SERVER_DATETIMEFORMAT);
			
			return isAllowedForSubmission(submissionDate, startDate, endDate, locale);
    	}catch(Exception e){
    		
    	}
		return false;
    }

	public static List<SearchVO> fullTextSearchForSearching(String param,
			int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		return getMotionRepository().fullTextSearchForSearching(param,start,noOfRecords, locale, requestMap);
	}
	
	public Workflow findWorkflowFromStatus() throws ELSException {
		Workflow workflow = null;
		
		Status internalStatus = this.getInternalStatus();
		Status recommendationStatus = this.getRecommendationStatus();
		String recommendationStatusType = recommendationStatus.getType();

		if(recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
			workflow = Workflow.findByStatus(recommendationStatus, this.getLocale());
		
		} else {
			workflow = Workflow.findByStatus(internalStatus, this.getLocale());											
		}
		
		return workflow;
	}
	
	public void startWorkflow(final Motion motion, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
    	//end current workflow if exists
		motion.endWorkflow(motion, workflowHouseType, locale);
    	//update motion statuses as per the workflow status
    	motion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
    	//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(motion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
    
    public void endWorkflow(final Motion motion, final String workflowHouseType, final String locale) throws ELSException {
    	WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
		if(wfDetails != null) {
			try {
				WorkflowDetails.endProcess(wfDetails);
			} catch(Exception e) {
				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
				wfDetails.setCompletionTime(new Date());
				wfDetails.merge();
			} finally {
				motion.removeExistingWorkflowAttributes();
			}
		} else {
			motion.removeExistingWorkflowAttributes();
		}
	}
    
    public void updateForInitFlow(final Status status, final UserGroupType userGroupType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) {
    	/** update statuses for the required flow **/
    	Map<String, String[]> parameterMap = new HashMap<String, String[]>();
    	parameterMap.put("locale", new String[]{locale});
    	parameterMap.put("flowStatusType", new String[]{status.getType()});
    	parameterMap.put("isAfterFinalDecision", new String[]{isFlowOnRecomStatusAfterFinalDecision.toString()});
    	parameterMap.put("userGroupType", new String[]{userGroupType.getType()});
    	List statusRecommendations = Query.findReport(ApplicationConstants.QUERYNAME_STATUS_RECOMMENDATIONS_FOR_INIT_FLOW, parameterMap);
    	if(statusRecommendations!=null && !statusRecommendations.isEmpty()) {
    		Object[] statuses = (Object[]) statusRecommendations.get(0);
    		if(statuses[0]!=null && !statuses[0].toString().isEmpty()) {
    			Status mainStatus = Status.findByType(statuses[0].toString(), locale);
    			this.setStatus(mainStatus);
    		}
    		if(statuses[1]!=null && !statuses[1].toString().isEmpty()) {
    			Status internalStatus = Status.findByType(statuses[1].toString(), locale);
    			this.setInternalStatus(internalStatus);
    		}
    		if(statuses[2]!=null && !statuses[2].toString().isEmpty()) {
    			Status recommendationStatus = Status.findByType(statuses[2].toString(), locale);
    			this.setRecommendationStatus(recommendationStatus);
    		}   
    		this.simpleMerge();
    	}		
    }
    
    public static int updateTimeoutSupportingMemberTasksForDevice(final Long deviceId, final Date submissionDate) {
		return getMotionRepository().updateTimeoutSupportingMemberTasksForDevice(deviceId, submissionDate);
	}
}
