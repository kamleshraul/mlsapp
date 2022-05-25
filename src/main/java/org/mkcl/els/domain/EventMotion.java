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
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
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
	
	
	private String exMember;
	
	private Boolean exMemberEnabled;
	
	
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
	@JoinTable(name = "eventmotions_referencedunits", 
	joinColumns = { @JoinColumn(name = "eventmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "referenced_unit_id", referencedColumnName = "id") })
	private List<ReferenceUnit> referencedEntities;

	
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
		return getEventMotionRepository().findClubbedEntitiesByPosition(motion);
	}

	@Override
	public EventMotion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.EVENTMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = EventMotion.assignEventMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(),this.getLocale());
					this.setNumber(number + 1);
					addEventMotionDraft();
					return (EventMotion)super.persist();
				}
			}else if(this.getNumber() != null){
				addEventMotionDraft();
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
		if(! this.getStatus().getType().equals(ApplicationConstants.EVENTMOTION_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.EVENTMOTION_COMPLETE)) {
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
		if(this.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SUBMIT)) {
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
			if(this.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_INCOMPLETE) 
					|| this.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_COMPLETE)) {
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
		return getEventMotionRepository().findAllByStatus(session, eventMotionType, internalStatus, itemsCount, locale);
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
			final DeviceType eventMotionType,
			final Integer file,
			final String locale) {
		return getEventMotionRepository().findAllByFile(session, eventMotionType, file, locale);
	}

	public static List<EventMotion> findBySessionDeviceTypeSubdepartment(final Session session,
			final DeviceType eventMotionType,
			final SubDepartment subDepartment,
			final String locale) {
		return getEventMotionRepository().findBySessionDeviceTypeSubdepartment(session, eventMotionType, subDepartment, locale);
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
	
	public static List<EventMotion> findFinalizedEventMotions(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment,
			final Status status,
			final String sortOrder,
			final String locale) {
		return null;//getCutMotionRepository().findFinalizedCutMotionsByDepartment(session, deviceType, subDepartment, status, sortOrder, locale);
	}
	
	public static List<EventMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType eventMotionType,
			final Integer itemsCount,
			final String locale) {
		return getEventMotionRepository().findAllByMember(session, primaryMember, eventMotionType,itemsCount, locale);
	}
	
	public static List<EventMotion> findAllByCreator(final Session session,
			final String creator,
			final DeviceType eventMotionType,
			final Integer itemsCount,
			final String locale) {
		return getEventMotionRepository().findAllByCreator(session, creator, eventMotionType, itemsCount, locale);
	}
	
	//************************Clubbing**********************
	public static boolean club(final Long primary, final Long clubbing, final String locale) throws ELSException{
		
		EventMotion m1 = EventMotion.findById(EventMotion.class, primary);
		EventMotion m2 = EventMotion.findById(EventMotion.class, clubbing);
		
		return club(m1, m2, locale); 
		
	}

	public static boolean club(final EventMotion q1,final EventMotion q2,final String locale) throws ELSException{    	
    	boolean clubbingStatus = false;
    	try {    		
    		if(q1.getParent()!=null || q2.getParent()!=null) {
    			throw new ELSException("error", "MOTION_ALREADY_CLUBBED");
    		} else {
    			if((q1.getDeviceType().getType().equals(ApplicationConstants.EVENTMOTION_CONDOLENCE)
        				&& q2.getDeviceType().getType().equals(ApplicationConstants.EVENTMOTION_CONDOLENCE))
        				|| (q1.getDeviceType().getType().equals(ApplicationConstants.EVENTMOTION_CONGRATULATORY)
                				&& q2.getDeviceType().getType().equals(ApplicationConstants.EVENTMOTION_CONGRATULATORY))) {
    				
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
	
	private static boolean clubMotions(EventMotion q1, EventMotion q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForMotion(q1, q2, locale);
    	if(clubbingStatus) {
    		
    		clubbingStatus = clubMotion(q1, q2, locale);

    	}    	 
    	return clubbingStatus;
    }
	
	private static boolean clubbingRulesForMotion(EventMotion q1, EventMotion q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.EVENTMOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("workflow")){
	    	if(clubbingStatus) {
	    		
				WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
				if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
					throw new ELSException("error", "MOTION_FLOW_PENDING");
				}
	    		
				WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
				if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
					throw new ELSException("error", "MOTION_FLOW_PENDING");
				}    		
	    	}
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubMotion(EventMotion q1, EventMotion q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubMotionsBH(q1, q2, locale);
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.EVENTMOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("workflow")){
	    	if(!clubbingStatus) {
	    		//=============cases specific to lowerhouse============//
	        	/** get discusison dates for motions **/
	        	Date q1_AnsweringDate = q1.getDiscussionDate();
	        	Date q2_AnsweringDate = q2.getDiscussionDate();
	        	
	        	Status yaadiLaidStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PROCESSED_YAADILAID, locale);
	        	
	        	//Case 7: Both questions are admitted and balloted
	        	if(q1.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)
	    				&& q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)
	    				&& (q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0)) {
	        		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
	        		if(q1_AnsweringDate.compareTo(q2_AnsweringDate)==0) {
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
    
    private static boolean clubbingRulesCommon(EventMotion q1, EventMotion q2, String locale) throws ELSException {
    	if(q1.getSession().equals(q2.getSession()) && !q1.getDeviceType().getType().equals(q2.getDeviceType().getType())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_DEVICETYPE");    		
    	}/* else if(!q1.getMin) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_MINISTRY");    		
    	} else if(!q1.getSubDepartment().getName().equals(q2.getSubDepartment().getName())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_DEPARTMENT");    		
    	} */else {
    		//clubbing rules succeeded
    		return true;
    	}  	
    }
    
    private static boolean clubMotionsBH(EventMotion q1, EventMotion q2, String locale) throws ELSException {
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.EVENTMOTION_CLUBBING_MODE, "");
    	
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
	    	Date q1_DiscussionDate = q1.getDiscussionDate();
	    	Date q2_DiscussionDate = q2.getDiscussionDate();
	    	
	    	Status putupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status approvalStatus = Status.findByType(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION, locale);
	    	
	    	//Case 1: Both motions are just ready to be put up
	    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED)
	    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		
	    		Status clubbedStatus = Status.findByType(ApplicationConstants.EVENTMOTION_SYSTEM_CLUBBED, locale);
	    		if(q1_DiscussionDate != null && q2_DiscussionDate != null){
	    			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {
	        				actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	        			actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	        			return true;
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
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
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING, locale);
	    		actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 2B: One motion is pending in approval workflow while other is ready to be put up
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED)
	    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING, locale);
	    		actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 3: Both motions are pending in approval workflow
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
	    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	    		if(q1_approvalLevel==q2_approvalLevel) {
	    			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0) {
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
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	        			WorkflowDetails.endProcess(q2_workflowDetails);
	        			q2.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	        			return true;
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
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
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_NAMECLUBBING, locale);
	    		actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 4B: One motion is admitted but not balloted yet while other motion is ready to be put up (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_NAMECLUBBING, locale);
	    		actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 5A: One motion is admitted but not balloted yet while other motion is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)
					&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_NAMECLUBBING, locale);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		WorkflowDetails.endProcess(q2_workflowDetails);
	    		q2.removeExistingWorkflowAttributes();
	    		actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 5B: One motion is admitted but not balloted yet while other motion is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_NAMECLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails.endProcess(q1_workflowDetails);
	    		q1.removeExistingWorkflowAttributes();
	    		actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 6: Both motions are admitted but not balloted
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)) {
	    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
	    			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	        			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
	        			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
	    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	        		if(q1_approvalLevel==q2_approvalLevel) {
	        			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0) {
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
	            		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	            			WorkflowDetails.endProcess(q2_workflowDetails);
	            			q2.removeExistingWorkflowAttributes();
	            			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	            			return true;
	            		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
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
    	    
    private static void actualClubbingMotions(EventMotion parent, EventMotion child,
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
				EventMotion clubbedMn = i.getEventMotion();
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
				EventMotion clubbedMn = i.getEventMotion();
				Long clubbedMnId = clubbedMn.getId();
				if(!parentMnId.equals(clubbedMnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getDeviceType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setEventMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.EVENTMOTION_CLUBBING_MODE, "");
		if(csptClubbingMode != null){
			if(csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()){
			
				if(csptClubbingMode.getValue().equals("normal")){
					Status submitted = Status.findByType(ApplicationConstants.EVENTMOTION_SUBMIT, locale);
					
					if(childClubbedEntities != null && !childClubbedEntities.isEmpty()){
						for(ClubbedEntity k : childClubbedEntities){
							EventMotion motion = k.getEventMotion();					
							
							WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(motion);
							if(wd != null){
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
					
				}else if(csptClubbingMode.getValue().equals("workflow")){
					if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
						for(ClubbedEntity k:childClubbedEntities){
							EventMotion motion=k.getEventMotion();					
							/** find current clubbing workflow if pending **/
							String pendingWorkflowTypeForMotion = "";
							
							if(motion.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_CLUBBING)
									|| motion.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_CLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
							} else if(motion.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_NAME_CLUBBING)
									|| motion.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_NAME_CLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
							} else if(motion.getRecommendationStatus().getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
									|| motion.getRecommendationStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
							}
							
							
							if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
								/** end current clubbing workflow **/
								WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion, pendingWorkflowTypeForMotion);	
								WorkflowDetails.endProcess(wfDetails);
								motion.removeExistingWorkflowAttributes();
								
								/** put up for proper clubbing workflow as per updated parent **/
								Status finalAdmitStatus = Status.findByType(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION , locale);
								Integer parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
								Integer motion_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();					
								
								if(parent.getStatus().getPriority().compareTo(parent_finalAdmissionStatusPriority)<0) {
									Status putupForClubbingStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING , locale);
									motion.setInternalStatus(putupForClubbingStatus);
									motion.setRecommendationStatus(putupForClubbingStatus);
								} else {
									if(motion.getStatus().getPriority().compareTo(motion_finalAdmissionStatusPriority)<0) {
										Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_NAMECLUBBING , locale);
										motion.setInternalStatus(putupForNameClubbingStatus);
										motion.setRecommendationStatus(putupForNameClubbingStatus);
									} else {
										Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
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
	
			List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByDiscussionDateMotionNumber(ApplicationConstants.ASC,locale);
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
    
    public List<ClubbedEntity> findClubbedEntitiesByDiscussionDateMotionNumber(final String sortOrder, final String locale) {
    	return getEventMotionRepository().findClubbedEntitiesByDiscussionDateMotionNumber(this,sortOrder, locale);
    }
    
	//************************Clubbing**********************
    
    //************************Unclubbing********************
    public static boolean unclub(final Long m1, final Long m2, String locale) throws ELSException {
		EventMotion motion1 = EventMotion.findById(EventMotion.class, m1);
		EventMotion motion2 = EventMotion.findById(EventMotion.class, m2);
		return unclub(motion1, motion2, locale);
	}
    
    public static boolean unclub(final EventMotion q1, final EventMotion q2, String locale) throws ELSException {
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
	
	public static boolean unclub(final EventMotion motion, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(motion.getParent()==null) {
			throw new ELSException("error", "MOTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(motion.getParent(), motion, locale);
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbing(final EventMotion parent, final EventMotion child, String locale) throws ELSException {
		boolean clubbingStatus = false;
		clubbingStatus = actualUnclubbingMotions(parent, child, locale);		
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbingMotions(final EventMotion parent, final EventMotion child, String locale) throws ELSException {
		
		boolean retVal = false;
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.EVENTMOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("normal")){
    		Status putupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
    		Status submitStatus = Status.findByType(ApplicationConstants.EVENTMOTION_SUBMIT, locale);
    		
    		/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedMotions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedMotions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			
			for(ClubbedEntity i:oldClubbedMotions){
				if(!i.getEventMotion().getId().equals(child.getId())){
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
			Status approvedStatus = Status.findByType(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION, locale);		
			if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
					&& !child.getRecommendationStatus().equals(ApplicationConstants.EVENTMOTION_PUTUP_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus().equals(ApplicationConstants.EVENTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				Status putupUnclubStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PUTUP_UNCLUBBING, locale);
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
					if(! i.getEventMotion().getId().equals(child.getId())){
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
				if(child.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_CLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(child.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_NAME_CLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_NAME_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| child.getRecommendationStatus().getType().equals(ApplicationConstants.EVENTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForMotion);	
					WorkflowDetails.endProcess(wfDetails);
					child.removeExistingWorkflowAttributes();
				}
				/** update child status **/
				Status putupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				Status admitStatus = Status.findByType(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION, locale);
				if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
					child.setInternalStatus(putupStatus);
					child.setRecommendationStatus(putupStatus);
				} else {
					/*if(child.getReply()==null || child.getReply().isEmpty()) {
						child.setInternalStatus(admitStatus);
						child.setRecommendationStatus(admitStatus);
						Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
						UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
						WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
					} else {*/
						child.setInternalStatus(admitStatus);
						Status answerReceivedStatus = Status.findByType(ApplicationConstants.EVENTMOTION_PROCESSED_ANSWER_RECEIVED, locale);
						child.setRecommendationStatus(answerReceivedStatus);
					//}
				}
			}	
			child.merge();
			retVal = true;
		}
    	
    	return retVal;
	}
    //************************Unclubbing********************
	//************************Clubbing unclubbing update*********************
		/**** Motion Update Clubbing Starts ****/
	    public static void updateClubbing(EventMotion motion) throws ELSException {
			//case 1: motion is child
			if(motion.getParent()!=null) {
				EventMotion.updateClubbingForChild(motion);
			} 
			//case 2: motion is parent
			else if(motion.getParent()==null && motion.getClubbedEntities()!=null && !motion.getClubbedEntities().isEmpty()) {
				EventMotion.updateClubbingForParent(motion);
			}
		}
	    
	    private static void updateClubbingForChild(EventMotion motion) throws ELSException {
			updateClubbingForChildMotion(motion);
		}
	    
	    private static void updateClubbingForChildMotion(EventMotion motion) throws ELSException {
			String locale = motion.getLocale();
			EventMotion parentMotion = motion.getParent();
			    	
	    	Status putupStatus = Status.findByType(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED, motion.getLocale());
			Status approvalStatus = Status.findByType(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION, motion.getLocale());
			
			{					
				updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);
				
				if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					Status clubbedStatus = Status.findByType(ApplicationConstants.EVENTMOTION_SYSTEM_CLUBBED, motion.getLocale());
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
	    
	    public static void updateDomainFieldsOnClubbingFinalisation(EventMotion parent, EventMotion child) {
	    	updateDomainFieldsOnClubbingFinalisationForMotion(parent, child);		
	    }
	    
	    private static void updateDomainFieldsOnClubbingFinalisationForMotion(EventMotion parent, EventMotion child) {
	    	updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
	    }
	    
	    private static void updateDomainFieldsOnClubbingFinalisationCommon(EventMotion parent, EventMotion child) {
			/** copy latest subject of parent to revised subject of child **/
			if(parent.getRevisedEventTitle()!=null && !parent.getRevisedEventTitle().isEmpty()) {
				child.setRevisedEventTitle(parent.getRevisedEventTitle());
			} else {
				child.setRevisedEventTitle(parent.getEventTitle());
			}
			/** copy latest details text of parent to revised details text of child **/
			if(parent.getRevisedDescription()!=null && !parent.getRevisedDescription().isEmpty()) {
				child.setRevisedDescription(parent.getRevisedDescription());
			} else {
				child.setRevisedDescription(parent.getDescription());
			}
		}
	    
	    private static void updateClubbingForParent(EventMotion motion) {
	    	updateClubbingForParentMotion(motion);		
		}

		private static void updateClubbingForParentMotion(EventMotion motion) {
			for(ClubbedEntity ce: motion.getClubbedEntities()) {
				EventMotion clubbedMotion = ce.getEventMotion();
				if(clubbedMotion.getInternalStatus().getType().equals(ApplicationConstants.EVENTMOTION_SYSTEM_CLUBBED)) {
					
					updateDomainFieldsOnClubbingFinalisation(motion, clubbedMotion);
					
					clubbedMotion.setStatus(motion.getInternalStatus());
					clubbedMotion.setInternalStatus(motion.getInternalStatus());
					clubbedMotion.setRecommendationStatus(motion.getInternalStatus());				
					clubbedMotion.merge();
				}
			}
		}
		//************************Clubbing unclubbing update**********************

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

	public String getExMember() {
		return exMember;
	}

	public void setExMember(String exMember) {
		this.exMember = exMember;
	}

	public Boolean getExMemberEnabled() {
		return exMemberEnabled;
	}

	public void setExMemberEnabled(Boolean exMemberEnabled) {
		this.exMemberEnabled = exMemberEnabled;
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
	

	public static void supportingMemberWorkflowDeletion(final EventMotion eventMotion) {
    	if(eventMotion!=null && eventMotion.getId()>0) {
    		if(anySupportingMembersWorkflows(eventMotion)) {
    			deleteSupportingMembersWorkflows(eventMotion);
    		}
    	}
    }
    
    public static boolean anySupportingMembersWorkflows(final EventMotion eventMotion) {
		List<SupportingMember> supportingMembers = eventMotion.getSupportingMembers();
		if(supportingMembers!=null && supportingMembers.size()>0) {
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean deleteSupportingMembersWorkflows(final EventMotion eventMotion) {
		List<Long> workflowDetailsList=new ArrayList<Long>();
		if(eventMotion!=null && eventMotion.getId()>0 && eventMotion.getSupportingMembers()!=null 
				&& eventMotion.getSupportingMembers().size()>0) {
			List<SupportingMember> supportingMembers = eventMotion.getSupportingMembers();
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					workflowDetailsList.add(Long.valueOf(sm.getWorkflowDetailsId()));
			}
		}
		
		int deleteCount=0;
		for(Long workFlowDetailsId : workflowDetailsList) {
			BaseDomain workFlowdetails = WorkflowDetails.findById(WorkflowDetails.class, workFlowDetailsId);
			boolean isDeleted = WorkflowDetails.getBaseRepository().remove(workFlowdetails);
			if(isDeleted)deleteCount++;
		}
		
		return workflowDetailsList!=null && deleteCount== workflowDetailsList.size();
	}
}