/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.StandaloneMotion.java
 * Created On: Dec 27, 2012
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.Question.CLUBBING_STATE;
import org.mkcl.els.domain.Question.STARRED_STATE;
import org.mkcl.els.repository.StandaloneMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class StandaloneMotion.
 *
 * @author vikasg
 * @since v1.0.0
 */ 

@Configurable
@Entity
@Table(name="standalone_motions")
@JsonIgnoreProperties(value={"houseType", "session", "type","creationDate",
	"dataEnteredBy","editedOn","editedBy", "revisedSubject",
	"questionText","revisedQuestionText","answer","priority",
	"ballotStatus","discussionStatus","remarks","rejectionReason", "supportingMembers",
	"group","department", "drafts", "parent", "clubbedEntities", "referencedEntities",
	"referencedHDS","fileSent","fileIndex",
	"file","workflowDetailsId","bulkSubmitted","taskReceivedOn","workflowStartedOn","level",
	"endFlag","actor","workflowStarted","answeringAttemptsByDepartment"
	,"markAsAnswered","prospectiveClubbings","lastDateOfAnswerReceiving","revisedBriefExplanation",
	"briefExplanation","discussionDate","dateOfAnsweringByMinister","toBeAnsweredByMinister"
	,"revisedReason","reason","numberOfDaysForFactualPositionReceiving",
	"lastDateOfFactualPositionReceiving","factualPosition","questionsAskedInFactualPosition"
	,"locale","version","versionMismatch","editedAs","rejectionReason","refText"},ignoreUnknown=true)
public class StandaloneMotion extends Device implements Serializable {
		
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

    /** The subject. */
    @Column(length=30000)
    private String revisedSubject;

    /** The question text. */
    @Column(length=30000)
    private String questionText;
    
    /** The question text. */
    @Column(length=30000)
    private String revisedQuestionText;

    /** The answer. */
    @Column(length=30000)
    private String answer;

    /** The priority. */
    private Integer priority;

    /** 
     * The status. Refers to various final status viz, SUBMITTED,
     * ADMITTED, REJECTED, CONVERTED_TO_UNSTARRED 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    /** 
     * The internal status. Refers to status assigned to a Question
     * during the Workflow
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;
    
    /** 
     * If a question is balloted then its balloted status is set to balloted 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ballotstatus_id")
    private Status ballotStatus;

	/**** The Disucssion Status. ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="discussionstatus_id")
	private Status discussionStatus;
   
    /** The remarks. */
    @Column(length=30000)
    private String remarks;
    
    @Column(length=30000)
	private String rejectionReason;
    
    
    /**** PRIMARY & SUPPORTING MEMBERS ****/
    /** The primary member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member primaryMember;

    /** The supporting members. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="standalone_supportingmembers",
            joinColumns={@JoinColumn(name="standalonemotion_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
    private List<SupportingMember> supportingMembers;

    
    /**** GROUP ATTRIBUTERS ****/
    /** The group. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

    /** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;

    
    /**** DRAFTS ****/
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="standalonemotions_drafts_association", 
    		joinColumns={@JoinColumn(name="standalonemotion_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="standalonemotion_draft_id", referencedColumnName="id")})
    private List<StandaloneMotionDraft> drafts;    

    
    /**** Clubbing ****/
    /** The parent. */
    @ManyToOne(fetch=FetchType.LAZY)
    private StandaloneMotion parent;
    
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="standalones_clubbingentities", 
    		joinColumns={@JoinColumn(name="standalone_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;

    /**** Referencing ****/
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="standalones_referencedunits", 
    		joinColumns={@JoinColumn(name="standalone_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="reference_unit_id", referencedColumnName="id")})
    private List<ReferenceUnit> referencedEntities;
    
    @ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    private ReferencedEntity referencedHDS;
        
    /**** SHORT NOTICE DEVICE ATTRIBUTES ****/
    /** The reason. */
    @Column(length=30000)
    private String reason;
    
    /** The reason. */
    @Column(length=30000)
    private String revisedReason;

    /** The to be answered by minister. */
    private Boolean toBeAnsweredByMinister=false;

    /** The date of answering by minister. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfAnsweringByMinister;
       
    @Temporal(TemporalType.DATE)
    private Date discussionDate;

    @Column(length=30000)
    private String briefExplanation;
    
    @Column(length=30000)
    private String revisedBriefExplanation;  
    
    @Temporal(TemporalType.DATE)
    private Date lastDateOfAnswerReceiving;    
    
    /** The mark as answered. */
    private Boolean markAsAnswered;
          
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
	
	/** The questions asked in factual position. */
    @Column(length=30000)
    private String questionsAskedInFactualPosition;
    
    
    @Column(length=30000)
    private String factualPosition;
    
    /** The date of factual position receiving. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDateOfFactualPositionReceiving;
    
    private Integer numberOfDaysForFactualPositionReceiving;
    
    /** The date of answer requested to department. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date answerRequestedDate;
    
    /** The date of answer received from department. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date answerReceivedDate;
    	
    private Integer file;
    
    @Column(length=3000)
    private String refText;
    
    /**** Fields for storing the confirmation of Group change ****/
    private Boolean transferToDepartmentAccepted = false;
    
    private Boolean mlsBranchNotifiedOfTransfer = false;
    
    private transient volatile static Integer HDS_CUR_NUM_LOWER_HOUSE = 0;
    
    private transient volatile static Integer HDS_CUR_NUM_UPPER_HOUSE = 0;
    
    /** The question repository. */
    @Autowired
    private transient StandaloneMotionRepository standaloneMotionRepository;
    
	
	/**** Constructors ****/
	
    /**
     * Instantiates a new motion.
     */
    public StandaloneMotion() {
        super();
    }
    
    
    /**** Domain methods ****/
    
    /**
     * Gets the revisions.
     *
     * @param questionId the question id
     * @param locale the locale
     * @return the revisions
     */
    public static List<RevisionHistoryVO> getRevisions(final Long questionId, final String locale) {
        return getStandaloneMotionRepository().getRevisions(questionId,locale);
    }
    
    public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}	
    
    @Override
    public StandaloneMotion persist() {
        if(this.getStatus().getType().equals(ApplicationConstants.STANDALONE_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (StandaloneMotion.class) {
                	if(this.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
                			&& this.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){                		
                		String key = "NO_OF_HALFHOURDISCUSSIONSTANDALONE_MEMBER_PUTUP_COUNT_LH";    		
                		
                		CustomParameter halfhourDiscussionStandalonePutupCount_CP;
                		Integer memberHalfHourPutupCount = null;
                		Integer halfhourDiscussionStandalonePutupCount =  null;
                		try{
                			halfhourDiscussionStandalonePutupCount_CP = CustomParameter.findByFieldName(CustomParameter.class, 
                					"name", key, "");
                			
	                		if(halfhourDiscussionStandalonePutupCount_CP != null){
	                			halfhourDiscussionStandalonePutupCount = 
	                					new Integer(halfhourDiscussionStandalonePutupCount_CP.getValue());
	                		}
                		
                			memberHalfHourPutupCount = StandaloneMotion.getMemberPutupCount(this.getPrimaryMember(), 
                					this.getSession(), this.getType(), this.getLocale());
                		}catch (ELSException e) {
                			e.printStackTrace();
						}
                		if(memberHalfHourPutupCount != null){
                			if(memberHalfHourPutupCount < halfhourDiscussionStandalonePutupCount){
                				Integer number = null;
								try {									
									if(StandaloneMotion.getHDSCurrentNumberLowerHouse() == 0){
										number = StandaloneMotion.assignStandaloneMotionNo(this.getHouseType(),
												this.getSession(), this.getType(),this.getLocale());
										StandaloneMotion.updateHDSCurrentNumberLowerHouse(number);
									}
									
									
									/*Integer persistPostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
									if(persistPostBallotNumber > 0){
										this.setPostBallotNumber(persistPostBallotNumber + 1);
									}*/
									
									
			            			this.setNumber(StandaloneMotion.getHDSCurrentNumberLowerHouse() + 1);
			            			StandaloneMotion.updateHDSCurrentNumberLowerHouse(StandaloneMotion.getHDSCurrentNumberLowerHouse() + 1);				            		
									
								} catch (ELSException e) {
									e.printStackTrace();
								}                				
                			}
                		}
                	}else{
                		         			
                		Integer number = null;
						try {							
							if(StandaloneMotion.getHDSCurrentNumberUpperHouse() == 0){
								number = StandaloneMotion.assignStandaloneMotionNo(this.getHouseType(),
										this.getSession(), this.getType(),this.getLocale());
								StandaloneMotion.updateHDSCurrentNumberUpperHouse(number);
							}
							
							this.setNumber(StandaloneMotion.getHDSCurrentNumberUpperHouse() + 1);
	            			StandaloneMotion.updateHDSCurrentNumberUpperHouse(StandaloneMotion.getHDSCurrentNumberUpperHouse() + 1);
	            			
						} catch (ELSException e) {
							e.printStackTrace();
						}              		
                	}
                    addStandaloneMotionDraft();
                    return (StandaloneMotion)super.persist();
                }
            }/**** This is for typist.See if role check can be done. ****/            
            else if(this.getNumber() != null){
            	addStandaloneMotionDraft();
            }
        }
        return (StandaloneMotion) super.persist();
    }
    
    @Override
    public StandaloneMotion merge() {
        StandaloneMotion motion = null;
        if((this.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SUBMIT)) || (this.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED))){
            if(this.getNumber() == null) {
                synchronized (StandaloneMotion.class) {
                	if(this.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
                			&& this.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
                		
                		CustomParameter halfhourDiscussionStandalonePutupCount_CP = null;
                		Integer halfhourDiscussionStandalonePutupCount =  null;
                		Integer memberHalfHourPutupCount = null;
                		try{
                			halfhourDiscussionStandalonePutupCount_CP = CustomParameter.findByFieldName(CustomParameter.class, 
                					"name", ApplicationConstants.HALFHOURDISCUSSIONSTANDALONE_MEMBER_MAX_PUTUP_COUNT_LH, "");
                			
	                		if(halfhourDiscussionStandalonePutupCount_CP != null){
	                			halfhourDiscussionStandalonePutupCount = 
	                					new Integer(halfhourDiscussionStandalonePutupCount_CP.getValue());
	                		}
                			memberHalfHourPutupCount = StandaloneMotion.getMemberPutupCount(this.getPrimaryMember(), 
                					this.getSession(), this.getType(), this.getLocale());
                		}catch (ELSException e) {
							e.printStackTrace();
						}
                		
                		if(memberHalfHourPutupCount != null){
                			if(memberHalfHourPutupCount < halfhourDiscussionStandalonePutupCount){
                				Integer number = null;
								try {
									if(StandaloneMotion.getHDSCurrentNumberLowerHouse() == 0){
										number = StandaloneMotion.assignStandaloneMotionNo(this.getHouseType(),
												this.getSession(), this.getType(),this.getLocale());
										StandaloneMotion.updateHDSCurrentNumberLowerHouse(number);
									}
									
									
									/*Integer persistPostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
									if(persistPostBallotNumber > 0){
										this.setPostBallotNumber(persistPostBallotNumber + 1);
									}*/
									
									
			            			this.setNumber(StandaloneMotion.getHDSCurrentNumberLowerHouse() + 1);
			            			StandaloneMotion.updateHDSCurrentNumberLowerHouse(StandaloneMotion.getHDSCurrentNumberLowerHouse() + 1);
								} catch (ELSException e) {
									e.printStackTrace();
								}
                			}
                		}
                	}else{
                		//for HDS UPPERHOUSE
                		//if needed we can control the upper limit of HDS UPPERHOUSE to be put up
                		//key = "NO_OF_HALFHOURDISCUSSIONSTANDALONE_MEMBER_PUTUP_COUNT_UH";
                		Integer number = null;
						try {
							if(StandaloneMotion.getHDSCurrentNumberUpperHouse() == 0){
								number = StandaloneMotion.assignStandaloneMotionNo(this.getHouseType(),
										this.getSession(), this.getType(),this.getLocale());
								StandaloneMotion.updateHDSCurrentNumberUpperHouse(number);
							}
							
							
							/*Integer persistPostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
							if(persistPostBallotNumber > 0){
								this.setPostBallotNumber(persistPostBallotNumber + 1);
							}*/
							
							
	            			this.setNumber(StandaloneMotion.getHDSCurrentNumberUpperHouse() + 1);
	            			StandaloneMotion.updateHDSCurrentNumberUpperHouse(StandaloneMotion.getHDSCurrentNumberUpperHouse() + 1);
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}          		
                	}
                    addStandaloneMotionDraft();
                    motion = (StandaloneMotion) super.merge();
                }
            }
            else {
            	StandaloneMotion oldMotion = StandaloneMotion.findById(StandaloneMotion.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldMotion.getClubbedEntities());
            	}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldMotion.getReferencedEntities());
            	}
            	this.addStandaloneMotionDraft();
            	motion = (StandaloneMotion) super.merge();
            }
        }
        if(motion != null) {
            return motion;
        }
        else {
            if(this.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_COMPLETE)) {
                return (StandaloneMotion) super.merge();
            }
            else {
            	StandaloneMotion oldMotion = StandaloneMotion.findById(StandaloneMotion.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldMotion.getClubbedEntities());
            	}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldMotion.getReferencedEntities());
            	}
                this.addStandaloneMotionDraft();
                return (StandaloneMotion) super.merge();
            }
        }
    }
    
    /**
     * The merge function, besides updating StandaloneMotion, performs various actions
     * based on StandaloneMotion's status. What if we need just the simple functionality
     * of updation? Use this method.
     *
     * @return the question
     */
    public StandaloneMotion simpleMerge() {
        StandaloneMotion q = (StandaloneMotion) super.merge();
        return q;
    }
    
    /**
     * Assign StandaloneMotion no.
     *
     * @param houseType the house type
     * @param session the session
     * @param deviceType the device type
     * @param locale the locale
     * @return the integer
     * @author compaq
     * @throws ELSException 
     * @since v1.0.0
     */
    public static Integer assignStandaloneMotionNo(final HouseType houseType, 
    		final Session session, final DeviceType deviceType, final String locale) throws ELSException {
        return getStandaloneMotionRepository().assignStandaloneMotionNo(houseType, session, deviceType, locale);
    }
    
    /**
     * Find.
     *
     * @param session the session
     * @param number the number
     * @return the question
     * @throws ELSException 
     */
    public static StandaloneMotion find(final Member member, 
    		final Session session, 
    		final DeviceType deviceType, 
    		final String locale) throws ELSException {
        return StandaloneMotion.getStandaloneMotionRepository().find(member, session, deviceType, locale);
    }
    
    /**
     * Find.
     *
     * @param session the session
     * @param number the number
     * @return the question
     */
    public static StandaloneMotion find(final Session session, final Integer number) {
        return StandaloneMotion.getStandaloneMotionRepository().find(session, number);
    }
    
    /**
     * This method finds all the StandaloneMotions of a member of a particular device type,
     * belonging to a particular session and having internal status as specified
     * 
     * @param currentMember the current member
     * @param session the session
     * @param deviceType the device type
     * @param internalStatus the internal status
     * @return the list
     */
    public static List<StandaloneMotion> findAll(final Member currentMember,
            final Session session, 
            final DeviceType deviceType, 
            final Status internalStatus) {
        return getStandaloneMotionRepository().findAll(currentMember, session, deviceType, internalStatus);
    }
      
	
    /**
     * Find @param maxNoOfStandaloneMotions StandaloneMotiona given @param discussionDate.
     * The StandaloneMotion should have been submitted on or before
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param discussionDate the discussion date
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param excludeStandaloneMotions the exclude StandaloneMotions
     * @param maxNoOfStandaloneMotions the max no of StandaloneMotions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> find(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date discussionDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final StandaloneMotion[] excludeStandaloneMotions,
            final Integer maxNoOfStandaloneMotions,
            final String sortOrder,
            final String locale) {
        List<StandaloneMotion> motions = StandaloneMotion.getStandaloneMotionRepository().find(session, member, deviceType,
                group, discussionDate, finalSubmissionDate, internalStatuses, excludeStandaloneMotions,
                maxNoOfStandaloneMotions, sortOrder, locale);

        if(motions == null) {
            motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }
    
    /**
    * Find a list of StandaloneMotions for the given @param session
    * of a given @param deviceType submitted between @param
    * startTime & @param endTime (both date inclusive) having
    * either of the @param internalStatuses. The Questions
    * should have discussionDate = null OR 
    * discussionDate <= @param discussionDate
    * 
    * Sort the resulting list of StandaloneMotions by number according
    * to the @param sortOrder.
    * 
    * Returns an empty list if there are no StandaloneMotions.
    */
    public static List<StandaloneMotion> find(final Session session,
    	final DeviceType deviceType,
    	final Date discussionDate,
    	final Status[] internalStatuses,
    	final Boolean hasParent,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    
    	return StandaloneMotion.getStandaloneMotionRepository().find(session, deviceType, discussionDate, 
    		internalStatuses, hasParent, startTime, endTime, sortOrder, locale);
    }

    /**
     * Find the StandaloneMotions based on ballot status
     * @param session
     * @param deviceType
     * @param discussionDate
     * @param internalStatuses
     * @param hasParent
     * @param isBalloted
     * @param startTime
     * @param endTime
     * @param sortOrder
     * @param locale
     * @return
     */
    public static List<StandaloneMotion> findByBallot(final Session session,
			final DeviceType deviceType,
			final Date discussionDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
    	
    	return getStandaloneMotionRepository().findByBallot(session, deviceType, discussionDate, 
    			internalStatuses, hasParent, isBalloted, 
    			isMandatoryUnique, isPreBallot, startTime, 
    			endTime, sortOrder, locale);
    }
    
    public static String findBallotedMembers(final Session session, final String memberNotice, final DeviceType deviceType){
    	return getStandaloneMotionRepository().findBallotedMembers(session, memberNotice, deviceType);
    }
    
    public static String findBallotedSubjects(final Session session, final DeviceType deviceType){
    	return getStandaloneMotionRepository().findBallotedSubjects(session, deviceType);
    }
    
    /**
     * @param session
     * @param deviceType
     * @param discussionDate
     * @param internalStatuses
     * @param hasParent
     * @param isBalloted
     * @param startTime
     * @param endTime
     * @param sortOrder
     * @param locale
     * @return
     */
    public static List<Member> findPrimaryMembersByBallot(final Session session,
			final DeviceType deviceType,
			final Date discussionDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
    	return getStandaloneMotionRepository().findPrimaryMembersByBallot(session, deviceType, discussionDate, 
    			internalStatuses, hasParent, isBalloted, 
    			startTime, endTime, sortOrder, locale);
    }
    
    /**
     * @param session
     * @param deviceType
     * @param discussionDate
     * @param memberID
     * @param subjects
     * @param locale
     * @return
     */
    public static StandaloneMotion findStandaloneMotionForMemberOfUniqueSubject(final Session session, 
    		final DeviceType deviceType, 
    		final Date discussionDate,  
    		final Long memberID, 
    		final List<String> subjects, 
    		final String locale){
    	return getStandaloneMotionRepository().findStandaloneMotionForMemberOfUniqueSubject(session, deviceType, 
    			discussionDate, memberID, subjects, locale);
    			
    }
    
    /**
     * @param session
     * @param deviceType
     * @param memberId
     * @param answeringDate
     * @param internalStatuses
     * @param startTime
     * @param endTime
     * @param sortOrder
     * @param locale
     * @return
     */
    public static List<StandaloneMotion> findStandaloneMotionsByDiscussionDateAndMember(final Session session,
			final DeviceType deviceType,
			final Long memberId,
			final Date discussionDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		
		return getStandaloneMotionRepository().findStandaloneMotionsByDiscussionDateAndMember(session, deviceType, memberId, 
				discussionDate, internalStatuses, startTime, endTime, sortOrder, locale);
	}
    
    
    /**
     * Find @param maxNoOfStandaloneMotions StandaloneMotions of a @param member for a
     * given @param session having @param group. All this standaloneMotions should
     * have an discussion date mentioned. The discussion date should be less than
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param discussionDate the discussion date
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param excludeStandaloneMotions the exclude standaloneMotions
     * @param maxNoOfStandaloneMotions the max no of standaloneMotions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findBeforeDiscussionDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date discussionDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final StandaloneMotion[] excludeQuestions,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<StandaloneMotion> motions = StandaloneMotion.getStandaloneMotionRepository().findBeforeDiscussionDate(session,
                member, deviceType, group, discussionDate, finalSubmissionDate, internalStatuses,
                excludeQuestions, maxNoOfQuestions, sortOrder, locale);

        if(motions == null) {
            motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }

    /**
     * Find @param maxNoOfStandaloneMotions StandaloneMotions of a @param member for a
     * given @param session having @param group without an discussion date.
     * The StandaloneMotion should have been submitted on or before
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param excludeStandaloneMotions the exclude standaloneMotions
     * @param maxNoOfStandaloneMotions the max no of standaloneMotions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findNonDiscussionDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final StandaloneMotion[] excludeStandaloneMotions,
            final Integer maxNoOfStandaloneMotions,
            final String sortOrder,
            final String locale) {
        List<StandaloneMotion> motions = StandaloneMotion.getStandaloneMotionRepository().findNonDiscussionDate(session,
                member, deviceType, group, finalSubmissionDate, internalStatuses,
                excludeStandaloneMotions, maxNoOfStandaloneMotions, sortOrder, locale);

        if(motions == null) {
            motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }

    /**
     * Find @param maxNoOfStandaloneMotions StandaloneMotions of a @param member for a
     * given @param session having @param group without an answering date.
     * The StandaloneMotion should have been submitted on or before
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param maxNoOfStandaloneMotions the max no of standaloneMotions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findNonDiscussionDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfStandaloneMotions,
            final String sortOrder,
            final String locale) {
        List<StandaloneMotion> motions = StandaloneMotion.getStandaloneMotionRepository().findNonDiscussionDate(session, 
        		member, deviceType, group, finalSubmissionDate, 
        		internalStatuses, maxNoOfStandaloneMotions, sortOrder, locale);
        if(motions == null) {
            motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }

    /**
     * Find @param maxNoOfStandaloneMotions StandaloneMotions of a @param member for a
     * given @param session having @param group. All this questions should
     * have an answering date mentioned. The answering date should be equal to
     * or less than @param discussionDate. The StandaloneMotion should have been submitted on or
     * before @param finalSubmissionDate.
     *
     * The StandaloneMotions with discussiondate = @param discussionDate should take
     * precedence over discussiondate < @param discussionDate. Break the ties using
     * question number.
     *
     * Returns an empty list (if there are no questions for the specified criteria)
     * OR
     * Returns a list of StandaloneMotions with size <= @param maxNoOfStandaloneMotions
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param answeringDate the answering date
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param maxNoOfStandaloneMotions the max no of standaloneMotions
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findDatedStandaloneMotions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date discussionDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfStandaloneMotions,
            final String locale) {
        List<StandaloneMotion> questions = StandaloneMotion.getStandaloneMotionRepository().findDatedStandalones(session, 
        		member, deviceType, group, discussionDate, finalSubmissionDate, internalStatuses, 
        		maxNoOfStandaloneMotions, locale);

        if(questions == null) {
            questions = new ArrayList<StandaloneMotion>();
        }

        return questions;
    }
    
    /**
     * Find @param maxNoOfStandaloneMotions StandaloneMotions of a @param member for a
     * given @param session having @param group. All this standaloneMotions should
     * have an discussion date mentioned. The discussion date should be equal to
     * or less than @param answeringDate. The StandaloneMotion should have been submitted
     * between @param startTime and @param endTime (both time inclusive).
     *
     * The StandaloneMotions with discussiondate = @param discussionDate should take
     * precedence over discussiondate < @param discussionDate. Break the ties using
     * question number.
     *
     * Returns an empty list (if there are no standaloneMotions for the specified criteria)
     * OR
     * Returns a list of StandaloneMotions with size <= @param maxNoOfStandaloneMotions
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param discussionDate the answering date
     * @param startTime the start time
     * @param endTime the end time
     * @param internalStatuses the internal statuses
     * @param maxNoOfStandaloneMotions the max no of standaloneMotions
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findDatedStandaloneMotions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date discussionDate,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final Integer maxNoOfStandaloneMotions,
            final String locale) {
        List<StandaloneMotion> motions = 
        		StandaloneMotion.getStandaloneMotionRepository().findDatedStandaloneMotions(session, member, deviceType, 
        				group, discussionDate, startTime, endTime, internalStatuses, maxNoOfStandaloneMotions, locale);

        if(motions == null) {
        	motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }

    /**
     * Find @param maxNoOfStandaloneMotions StandaloneMotions of a @param member for a
     * given @param session having @param group without an discussion date.
     * The StandaloneMotion should have been submitted between @param startTime
     * and @param endTime (both time inclusive).
     *
     * StandaloneMotions should be sorted as per @param sortOrder according to
     * StandaloneMotion number.
     *
     * Returns an empty list (if there are no standaloneMotions for the specified
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param startTime the start time
     * @param endTime the end time
     * @param internalStatuses the internal statuses
     * @param maxNoOfStandaloneMotions the max no of standaloneMotions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findNonDiscussionDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final Integer maxNoOfStandaloneMotions,
            final String sortOrder,
            final String locale) {
        List<StandaloneMotion> motions = 
        		StandaloneMotion.getStandaloneMotionRepository().findNonDiscussionDate(session, member, deviceType, 
        				group, startTime, endTime, internalStatuses, maxNoOfStandaloneMotions, sortOrder, locale);

        if(motions == null) {
        	motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }

    /**
     * Find all the StandaloneMotions of a @param member for a given @param session having.
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param answeringDate the answering date
     * @param startTime the start time
     * @param endTime the end time
     * @param internalStatuses the internal statuses
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findDatedStandaloneMotions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final String locale) {
        List<StandaloneMotion> motions = 
        		StandaloneMotion.getStandaloneMotionRepository().findDatedStandaloneMotions(session, member, deviceType, 
        				group, answeringDate, startTime, endTime, internalStatuses, locale);

        if(motions == null) {
        	motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }

    /**
     * Find all the StandaloneMotions of a @param member for a given @param session
     * having @param group without an discussion date. The StandaloneMotion should
     * have been submitted between @param startTime and @param endTime
     * (both time inclusive).
     *
     * StandaloneMotions should be sorted as per @param sortOrder according to
     * StandaloneMotion number.
     *
     * Returns an empty list (if there are no standaloneMotions for the specified
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param startTime the start time
     * @param endTime the end time
     * @param internalStatuses the internal statuses
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<StandaloneMotion> findNonDiscussionDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final String sortOrder,
            final String locale) {
        List<StandaloneMotion> motions = StandaloneMotion.getStandaloneMotionRepository().findNonDiscussionDate(session,
                member, deviceType, group, startTime, endTime, internalStatuses, sortOrder, locale);

        if(motions == null) {
            motions = new ArrayList<StandaloneMotion>();
        }

        return motions;
    }
    
    /**
     * Find previous draft.
     *
     * @return the question draft
     */
    public StandaloneMotionDraft findPreviousDraft() {
    	Long id = this.getId();
    	return StandaloneMotion.getStandaloneMotionRepository().findPreviousDraft(id);
    }
    
    /**
     * Sort the StandaloneMotions as per @param sortOrder by number. If multiple StandaloneMotions
     * have same number, then there order is preserved.
     *
     * @param standaloneMotions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<StandaloneMotion> sortByNumber(final List<StandaloneMotion> standaloneMotions,
            final String sortOrder) {
        List<StandaloneMotion> newMList = new ArrayList<StandaloneMotion>();
        newMList.addAll(standaloneMotions);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<StandaloneMotion> c = new Comparator<StandaloneMotion>() {

                @Override
                public int compare(final StandaloneMotion q1, final StandaloneMotion q2) {
                    return q1.getNumber().compareTo(q2.getNumber());
                }
            };
            Collections.sort(newMList, c);
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<StandaloneMotion> c = new Comparator<StandaloneMotion>() {

                @Override
                public int compare(final StandaloneMotion q1, final StandaloneMotion q2) {
                    return q2.getNumber().compareTo(q1.getNumber());
                }
            };
            Collections.sort(newMList, c);
        }

        return newMList;
    }
    
    /**
     * Sort the StandaloneMotions as per @param sortOrder by priority. If multiple Questions
     * have same priority, then break the tie by StandaloneMotion number.
     *
     * @param standaloneMotions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<StandaloneMotion> sortByPriority(final List<StandaloneMotion> motions,
            final String sortOrder) {
        List<StandaloneMotion> newMList = new ArrayList<StandaloneMotion>();
        newMList.addAll(motions);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<StandaloneMotion> c = new Comparator<StandaloneMotion>() {

                @Override
                public int compare(final StandaloneMotion q1, final StandaloneMotion q2) {
                    int i = q1.getPriority().compareTo(q2.getPriority());
                    if(i == 0) {
                        int j = q1.getNumber().compareTo(q2.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newMList, c);
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<StandaloneMotion> c = new Comparator<StandaloneMotion>() {

                @Override
                public int compare(final StandaloneMotion q1, final StandaloneMotion q2) {
                    int i = q2.getPriority().compareTo(q1.getPriority());
                    if(i == 0) {
                        int j = q2.getNumber().compareTo(q1.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newMList, c);
        }

        return newMList;
    }
    
    /**
     * Sort the StandaloneMotions as per @param sortOrder by answeringDate. If multiple StandaloneMotions
     * have same answeringDate, then break the tie by Question number.
     *
     * @param standaloneMotions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<StandaloneMotion> sortByDiscussionDate(final List<StandaloneMotion> mos,
            final String sortOrder) {
        List<StandaloneMotion> newMList = new ArrayList<StandaloneMotion>();
        newMList.addAll(mos);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<StandaloneMotion> c = new Comparator<StandaloneMotion>() {

                @Override
                public int compare(final StandaloneMotion q1, final StandaloneMotion q2) {
                    int i = q1.getDiscussionDate().compareTo(q2.getDiscussionDate());
                    if(i == 0) {
                        int j = q1.getNumber().compareTo(q2.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newMList, c);
        }else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<StandaloneMotion> c = new Comparator<StandaloneMotion>() {

                @Override
                public int compare(final StandaloneMotion q1, final StandaloneMotion q2) {
                    int i = q2.getDiscussionDate().compareTo(q1.getDiscussionDate());
                    if(i == 0) {
                        int j = q2.getNumber().compareTo(q1.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newMList, c);
        }

        return newMList;
    }
    
    public static List<ClubbedEntity> findClubbedEntitiesByPosition(final StandaloneMotion mo) {
    	return getStandaloneMotionRepository().findClubbedEntitiesByPosition(mo);
    }
    
    public static List<ClubbedEntity> findClubbedEntitiesByPosition(final StandaloneMotion mo, final String sortOrder) {
    	return getStandaloneMotionRepository().findClubbedEntitiesByPosition(mo, sortOrder);
    }
    
    public static List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final StandaloneMotion mo, final String sortOrder,
    		final String locale) {
    	return getStandaloneMotionRepository().findClubbedEntitiesByMotionNumber(mo, sortOrder, locale);
    }
    
    public static List<ClubbedEntity> findClubbedEntitiesByDiscussionDateMotionNumber(final StandaloneMotion mo, final String sortOrder,
    		final String locale) {
    	return getStandaloneMotionRepository().findClubbedEntitiesByDiscussionDateMotionNumber(mo, sortOrder, locale);
    }
    
    /**
    * Find a list (without repetitions) of Primary Members who
    * have submitted StandaloneMotion(s) between @param startTime & 
    * @param endTime (both date inclusive) for the given @param 
    * session of a given @param deviceType submitted  having
    * either of the @param internalStatuses. The StandaloneMotions
    * should have discussionDate = null OR 
    * discussionDate <= @param answeringDate
    * 
    * Sort the resulting list of Members by StandaloneMotion number according
    * to the @param sortOrder.
    * 
    * Returns an empty list if there are no Members.
    */
    public static List<Member> findPrimaryMembers(final Session session,
    	final DeviceType deviceType,
    	final Date discussionDate,
    	final Status[] internalStatuses,
    	final Boolean hasParent,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    	return StandaloneMotion.getStandaloneMotionRepository().findPrimaryMembers(session, deviceType, 
    		discussionDate, internalStatuses, hasParent, startTime, 
    		endTime, sortOrder, locale);
    }
    
    public static List<Member> findPrimaryMembersForBallot(final Session session,
        	final DeviceType deviceType,
        	final Date discussionDate,
        	final Status[] internalStatuses,
        	final Boolean hasParent,
        	final Date startTime,
        	final Date endTime,
        	final String sortOrder,
        	final String locale) {
        	return StandaloneMotion.getStandaloneMotionRepository().findPrimaryMembersForBallot(session, deviceType, 
        		discussionDate, internalStatuses, hasParent, startTime, 
        		endTime, sortOrder, locale);
        }

    public static List<Member> findActiveMembersWithStandaloneMotions(final Session session,
    	final Date activeOn,
    	final DeviceType deviceType,
    	final Group group,
    	final Status[] internalStatuses,
    	final Date discussionDate,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    	MemberRole role = MemberRole.find(session.getHouse().getType(), "MEMBER", locale);
    	return StandaloneMotion.getStandaloneMotionRepository().findActiveMembersWithStandaloneMotions(session, role, activeOn, deviceType, group, internalStatuses, discussionDate, startTime, endTime, sortOrder, locale);
    }

    public static List<Member> findActiveMembersWithoutStandalolneMotions(final Session session,
    	final Date activeOn,
    	final DeviceType deviceType,
    	final Group group,
    	final Status[] internalStatuses,
    	final Date discussionDate,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    	MemberRole role = MemberRole.find(session.getHouse().getType(), "MEMBER", locale);
    	return StandaloneMotion.getStandaloneMotionRepository().findActiveMembersWithoutStandaloneMotions(session, role, 
    			activeOn, deviceType, group, internalStatuses, discussionDate, startTime, endTime, sortOrder, locale);
    }

    public String findFormattedNumber() {
    	NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
    	return format.format(this.getNumber());
    }
    
    /**
    * Find supporting members.
    *
    * @param strMotionId the str question id
    * @return the list
    */
    public static List<SupportingMember> findSupportingMembers(final String strMotionId) {
    	Long motionId = Long.parseLong(strMotionId);
    	StandaloneMotion motion = findById(StandaloneMotion.class, motionId);
    	return motion.getSupportingMembers();
    }

    
    /**
     * @param member
     * @param session
     * @param deviceType
     * @param locale
     * @return
     * @throws ELSException
     */
    public static Integer getMemberPutupCount(final Member member, 
    		final Session session,
    		final DeviceType deviceType, 
    		final String locale) throws ELSException{
    	return StandaloneMotion.getStandaloneMotionRepository().getMemberPutupCount(member, session, deviceType, locale);
    }
    
    /**** INTERNAL METHODS ****/
    /**
     * Gets the standaloneMotion repository.
     *
     * @return the standaloneMotion repository
     */
    private static StandaloneMotionRepository getStandaloneMotionRepository() {
        StandaloneMotionRepository standaloneMotionRepository = new StandaloneMotion().standaloneMotionRepository;
        if (standaloneMotionRepository == null) {
            throw new IllegalStateException(
            	"StandaloneMotionRepository has not been injected in StandaloneMotion Domain");
        }
        return standaloneMotionRepository;
    }
    
    /**
     * Adds the question draft.
     */
    private void addStandaloneMotionDraft() {
        if(! this.getStatus().getType().equals(ApplicationConstants.STANDALONE_INCOMPLETE) &&
        		! this.getStatus().getType().equals(ApplicationConstants.STANDALONE_COMPLETE)) {
            StandaloneMotionDraft draft = new StandaloneMotionDraft();
            draft.setType(this.getType());
            draft.setAnswer(this.getAnswer());
            draft.setRemarks(this.getRemarks());
            
            draft.setParent(this.getParent());
            draft.setClubbedEntities(this.getClubbedEntities());
            draft.setReferencedEntities(this.getReferencedEntities());
            
            draft.setEditedAs(this.getEditedAs());
            draft.setEditedBy(this.getEditedBy());
            draft.setEditedOn(this.getEditedOn());
            
            draft.setGroup(this.getGroup());
            draft.setMinistry(this.getMinistry());
            if(this.getSubDepartment()!=null) {
            	draft.setDepartment(this.getSubDepartment().getDepartment());
            }      
            draft.setSubDepartment(this.getSubDepartment());
            
            draft.setStatus(this.getStatus());
            draft.setInternalStatus(this.getInternalStatus());
            draft.setRecommendationStatus(this.getRecommendationStatus());
            
            if(this.getMlsBranchNotifiedOfTransfer() != null){
            	draft.setMlsBranchNotifiedOfTransfer(this.getMlsBranchNotifiedOfTransfer());
            }
            if(this.getTransferToDepartmentAccepted() != null){
            	draft.setTransferToDepartmentAccepted(this.getTransferToDepartmentAccepted());
            }
            
            if(this.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
            	if(this.getRevisedReason() != null && this.getRevisedBriefExplanation() != null){
        		    draft.setReason(this.getRevisedReason());
	                draft.setBriefExplanation(this.getRevisedBriefExplanation());
	            } 
            	else if(this.getRevisedBriefExplanation() != null){
            		draft.setBriefExplanation(this.getRevisedBriefExplanation());
            		draft.setReason(this.getReason());
	            }
            	else if(this.getRevisedReason() != null){
            		draft.setBriefExplanation(this.getBriefExplanation());
            		draft.setReason(this.getRevisedReason());
	            }
            	else {
	            	draft.setReason(this.getReason());
	                draft.setBriefExplanation(this.getBriefExplanation());
	            }
            	draft.setSubject(this.getSubject());
            	draft.setQuestionText(this.getQuestionText());
            }
            
            if(this.getId() != null) {
                StandaloneMotion mo = StandaloneMotion.findById(StandaloneMotion.class, this.getId());
                List<StandaloneMotionDraft> originalDrafts = mo.getDrafts();
                if(originalDrafts != null){
                    originalDrafts.add(draft);
                }
                else{
                    originalDrafts = new ArrayList<StandaloneMotionDraft>();
                    originalDrafts.add(draft);
                }
                this.setDrafts(originalDrafts);
            }
            else {
                List<StandaloneMotionDraft> originalDrafts = new ArrayList<StandaloneMotionDraft>();
                originalDrafts.add(draft);
                this.setDrafts(originalDrafts);
            }
        }
    }
    
    public static StandaloneMotionDraft getLatestStandaloneMotionDraftOfUser(Long questionId, String username) throws ELSException{
    	return getStandaloneMotionRepository().getLatestStandaloneMotionDraftOfUser(questionId, username);
    }
    
    public static Integer findStandaloneMotionWithoutNumber(final Member member, 
    		final DeviceType deviceType, 
    		final Session session,
    		final String locale) throws ELSException{
    	return getStandaloneMotionRepository().findStandaloneMotionWithoutNumber(member, deviceType, session, locale);
    }

    public static Integer findStandaloneMotionWithNumber(final Member member, 
			final DeviceType deviceType, 
			final Session session,
			final String locale) throws ELSException{
    	return getStandaloneMotionRepository().findStandaloneMotionWithNumber(member, deviceType, session, locale);
    }
    
    public static Integer findStandaloneMotionWithNumberExcludingRejected(final Member member, 
			final DeviceType deviceType, 
			final Session session,
			final String locale) throws ELSException{
    	
    	return getStandaloneMotionRepository().findStandaloneMotionWithNumberExcludingRejected(member, deviceType, session, locale);
    			
    }
    public static List<StandaloneMotion> findRejectedStandaloneMotions(final Member member, 
    		final Session session,
    		final DeviceType deviceType, 
    		final String locale) throws ELSException{
    	return getStandaloneMotionRepository().findRejectedStandaloneMotions(member, session, deviceType, locale);
    }
    
	
	public static String findRejectedStandaloneMotionsAsString(List<StandaloneMotion> motions, 
			final String locale) throws ELSException{
		return getStandaloneMotionRepository().findRejectedStandaloneMotionsAsString(motions, locale);
	}
    
    
    public static StandaloneMotionDraft findLatestStandaloneMotionDraftOfUser(Long motionId, String username) throws ELSException{
    	return getStandaloneMotionRepository().findLatestStandaloneMotionDraftOfUser(motionId, username);
    }
    
	public static List<StandaloneMotion> findAllByStatus(final Session session,
			final DeviceType deviceType, 
			final Status internalStatus,
			final Group group,
			final Integer itemsCount,
			final Integer file,
			final String locale) throws ELSException {
		return getStandaloneMotionRepository().findAllByStatus(session, deviceType, internalStatus, 
				group,itemsCount, file, locale);
	}
	
	public static List<StandaloneMotion> findAllByRecommendationStatus(final Session session,
			final DeviceType deviceType, 
			final Status internalStatus,
			final Group group,
			final String locale) throws ELSException {
		return getStandaloneMotionRepository().findAllByRecommendationStatus(session, deviceType, internalStatus, 
				group, locale);
	}
    	
	public static List<StandaloneMotion> findByDeviceAndStatus(final DeviceType deviceType, final Status status){
		return getStandaloneMotionRepository().findByDeviceAndStatus(deviceType, status);
	}
	
	public static List<StandaloneMotion> findAllByMember(final Session session,
			final Member primaryMember,final DeviceType deviceType,final Integer itemsCount,
			final String locale) throws ELSException {
		return getStandaloneMotionRepository().findAllByMember(session, primaryMember, deviceType, itemsCount, locale);
	}
	
	public static StandaloneMotion getStandaloneMotion(final Long sessionId,final Long deviceTypeId, final Integer number,final String locale){
		return getStandaloneMotionRepository().getStandaloneMotion(sessionId, deviceTypeId,number, locale);
	}
	 
	public static StandaloneMotion getStandaloneMotion(final Long sessionId, final Integer number,final String locale){
	   	return getStandaloneMotionRepository().getStandaloneMotion(sessionId, number, locale);
	}
	
	public StandaloneMotionDraft findSecondPreviousDraft() {
		Long id = this.getId();
		return getStandaloneMotionRepository().findSecondPreviousDraft(id);
	}
	 
	 public static MemberMinister findMemberMinisterIfExists(StandaloneMotion question) throws ELSException {
		 return getStandaloneMotionRepository().findMemberMinisterIfExists(question);		 
	 }
	 
	 public static Boolean isExist(Integer number, DeviceType deviceType, Session session,String locale) {
		 return getStandaloneMotionRepository().isExist(number,deviceType,session,locale);
	 }
	 
	 public static StandaloneMotion findExisting(Integer number, DeviceType deviceType, Session session, String locale) {
		 return getStandaloneMotionRepository().findExisting(number, deviceType, session, locale);
	 }
	 
	 public static StandaloneMotionDraft findPutupDraft(final Long id, final String putupStatus, final String putupActorUsergroupName) {
		 return getStandaloneMotionRepository().findPutupDraft(id, putupStatus, putupActorUsergroupName); 
	 }
	 
	 public String findAllSupportingMemberNames(String nameFormat) {
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Member member = null;
		String memberName = "";	
		/** supporting members **/
		List<SupportingMember> supportingMembers = this.getSupportingMembers();
		if (supportingMembers != null) {
			int count = 1;
			for (SupportingMember sm : supportingMembers) {
				member = sm.getMember();
				Status approvalStatus = sm.getDecisionStatus();
				if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
						if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
							if(count==1) {
								allMemberNamesBuffer.append(memberName);
								count++;
							} else {
								allMemberNamesBuffer.append(", " + memberName);
							}
						}																								
					}									
				}				
			}
		}		
		/** clubbed standaloneMotions members **/
		List<ClubbedEntity> clubbedEntities = StandaloneMotion.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed standaloneMotions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED)
						|| ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
					member = ce.getStandaloneMotion().getPrimaryMember();
					if(member!=null) {
						memberName = member.findNameInGivenFormat(nameFormat);
						if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
								if(!allMemberNamesBuffer.toString().isEmpty()){
									allMemberNamesBuffer.append(", " + memberName);
								}else{
									allMemberNamesBuffer.append(memberName);
								}
							}														
						}												
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getStandaloneMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							Status approvalStatus = csm.getDecisionStatus();
							if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								memberName = member.findNameInGivenFormat(nameFormat);
								if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
									if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
										if(!allMemberNamesBuffer.toString().isEmpty()){
											allMemberNamesBuffer.append(", " + memberName);
										}else{
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
	
	public String findAllMemberNames(String nameFormat) {
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Member member = null;
		String memberName = "";				
		/** primary member **/
		member = this.getPrimaryMember();		
		if(member==null) {
			return allMemberNamesBuffer.toString();
		}		
		memberName = member.findNameInGivenFormat(nameFormat);
		if(memberName!=null && !memberName.isEmpty()) {
			if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
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
				if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {				
						if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
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
		List<ClubbedEntity> clubbedEntities = StandaloneMotion.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED)
						|| ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
					member = ce.getStandaloneMotion().getPrimaryMember();
					if(member!=null) {
						memberName = member.findNameInGivenFormat(nameFormat);
						if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
								if(allMemberNamesBuffer.length()>0) {
									allMemberNamesBuffer.append(", " + memberName);
								} else {
									allMemberNamesBuffer.append(memberName);
								}
							}							
						}												
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getStandaloneMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							Status approvalStatus = csm.getDecisionStatus();
							if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								memberName = member.findNameInGivenFormat(nameFormat);
								if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
									if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
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
		if(member==null) {
			return allMemberNamesBuffer.toString();
		}		
		memberName = member.findNameInGivenFormat(nameFormat);
		if(memberName!=null && !memberName.isEmpty()) {
			if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
				allMemberNamesBuffer.append(memberName);
				constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
				if(!constituencyName.isEmpty()) {
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
				if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
						if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
							if(allMemberNamesBuffer.length()>0) {
								allMemberNamesBuffer.append(", " + memberName);
							} else {
								allMemberNamesBuffer.append(memberName);
							}
							constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
							if(!constituencyName.isEmpty()) {
								allMemberNamesBuffer.append(" (" + constituencyName + ")");						
							}
						}						
					}									
				}				
			}
		}
		
		/** clubbed questions members **/
		List<ClubbedEntity> clubbedEntities = StandaloneMotion.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED)
						|| ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
					member = ce.getStandaloneMotion().getPrimaryMember();
					if(member!=null) {
						memberName = member.findNameInGivenFormat(nameFormat);
						if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
								if(allMemberNamesBuffer.length()>0) {
									allMemberNamesBuffer.append(", " + memberName);
								} else {
									allMemberNamesBuffer.append(memberName);
								}
								constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
								if(!constituencyName.isEmpty()) {
									allMemberNamesBuffer.append(" (" + constituencyName + ")");							
								}
							}							
						}												
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getStandaloneMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							Status approvalStatus = csm.getDecisionStatus();
							if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								memberName = member.findNameInGivenFormat(nameFormat);
								if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
									if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
										if(allMemberNamesBuffer.length()>0) {
											allMemberNamesBuffer.append(", " + memberName);
										} else {
											allMemberNamesBuffer.append(memberName);
										}
										constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
										if(!constituencyName.isEmpty()) {
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
	
	
	public static boolean isAdmittedThroughNameClubbing(final StandaloneMotion question) {
		return getStandaloneMotionRepository().isAdmittedThroughNameClubbing(question);		
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

	public String getQuestionText() {
		return questionText;
	}	
	
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
			
	public String getRevisedQuestionText() {
		return revisedQuestionText;
	}	
	
	public void setRevisedQuestionText(String revisedQuestionText) {
		this.revisedQuestionText = revisedQuestionText;
	}
		
	public String getAnswer() {
		return answer;
	}	
	
	public void setAnswer(String answer) {
		this.answer = answer;
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
	
	public Status getDiscussionStatus() {
		return discussionStatus;
	}

	public void setDiscussionStatus(Status discussionStatus) {
		this.discussionStatus = discussionStatus;
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
		
	public Group getGroup() {
		return group;
	}	
	
	public void setGroup(Group group) {
		this.group = group;
	}
		
	public Ministry getMinistry() {
		return ministry;
	}	
	
	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}
		
	public SubDepartment getSubDepartment() {
		return subDepartment;
	}	
	
	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}
		
	public List<StandaloneMotionDraft> getDrafts() {
		return drafts;
	}
	
	public void setDrafts(List<StandaloneMotionDraft> drafts) {
		this.drafts = drafts;
	}	
	
	public StandaloneMotion getParent() {
		return parent;
	}	
	
	public void setParent(StandaloneMotion parent) {
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
		
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getRevisedReason() {
		return revisedReason;
	}

	public void setRevisedReason(String revisedReason) {
		this.revisedReason = revisedReason;
	}
	
	public Boolean getToBeAnsweredByMinister() {
		return toBeAnsweredByMinister;
	}
	
	public void setToBeAnsweredByMinister(Boolean toBeAnsweredByMinister) {
		this.toBeAnsweredByMinister = toBeAnsweredByMinister;
	}
	
	public Date getDateOfAnsweringByMinister() {
		return dateOfAnsweringByMinister;
	}
	
	public void setDateOfAnsweringByMinister(Date dateOfAnsweringByMinister) {
		this.dateOfAnsweringByMinister = dateOfAnsweringByMinister;
	}
		
	public Date getDiscussionDate() {
		return discussionDate;
	}
	
	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
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

	public Date getLastDateOfAnswerReceiving() {
		return lastDateOfAnswerReceiving;
	}
	
	public void setLastDateOfAnswerReceiving(Date lastDateOfAnswerReceiving) {
		this.lastDateOfAnswerReceiving = lastDateOfAnswerReceiving;
	}
	
	public Boolean getMarkAsAnswered() {
		return markAsAnswered;
	}

	public void setMarkAsAnswered(Boolean markAsAnswered) {
		this.markAsAnswered = markAsAnswered;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}


	public String getDataEnteredBy() {
		return dataEnteredBy;
	}


	public String getQuestionsAskedInFactualPosition() {
		return questionsAskedInFactualPosition;
	}


	public void setQuestionsAskedInFactualPosition(
			String questionsAskedInFactualPosition) {
		this.questionsAskedInFactualPosition = questionsAskedInFactualPosition;
	}


	public String getFactualPosition() {
		return factualPosition;
	}


	public void setFactualPosition(String factualPosition) {
		this.factualPosition = factualPosition;
	}


	public Date getLastDateOfFactualPositionReceiving() {
		return lastDateOfFactualPositionReceiving;
	}
	
	
	public void setLastDateOfFactualPositionReceiving(Date lastDateOfFactualPositionReceiving) {
		this.lastDateOfFactualPositionReceiving = lastDateOfFactualPositionReceiving;
	}	
	

	public Integer getNumberOfDaysForFactualPositionReceiving() {
		return numberOfDaysForFactualPositionReceiving;
	}


	public void setNumberOfDaysForFactualPositionReceiving(
			Integer numberOfDaysForFactualPositionReceiving) {
		this.numberOfDaysForFactualPositionReceiving = numberOfDaysForFactualPositionReceiving;
	}
	
	
	public Date getAnswerRequestedDate() {
		return answerRequestedDate;
	}
	

	public void setAnswerRequestedDate(Date answerRequestedDate) {
		this.answerRequestedDate = answerRequestedDate;
	}
	

	public Date getAnswerReceivedDate() {
		return answerReceivedDate;
	}
	

	public void setAnswerReceivedDate(Date answerReceivedDate) {
		this.answerReceivedDate = answerReceivedDate;
	}
	

	public ReferencedEntity getReferencedHDS() {
		return referencedHDS;
	}


	public void setReferencedHDS(ReferencedEntity referencedHDS) {
		this.referencedHDS = referencedHDS;
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


	public static List<StandaloneMotion> findBySessionNumber(final Session session, final Integer number, final String locale){
		return getStandaloneMotionRepository().findBySessionNumber(session, number, locale);
	}
		
	public static List<QuestionSearchVO> searchByNumber(final Session session, final Integer number, final String locale){
		List<QuestionSearchVO> result = new ArrayList<QuestionSearchVO>();
		try{
			List<StandaloneMotion> data = StandaloneMotion.findBySessionNumber(session, number, locale);
			
			if(data != null){
				for(StandaloneMotion i : data){
					
					QuestionSearchVO questionSearchVO = new QuestionSearchVO();
					questionSearchVO.setId(i.getId());
											
					if(i.getNumber() != null){
						questionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(i.getNumber()));
					}
					
					if(i.getRevisedSubject() != null && !i.getRevisedSubject().isEmpty()){
						questionSearchVO.setSubject(i.getRevisedSubject());
					}else{
						questionSearchVO.setSubject(i.getSubject());
						
					}				
					
					if(i.getRevisedQuestionText() != null && !i.getRevisedQuestionText().isEmpty()){
						questionSearchVO.setQuestionText(i.getRevisedQuestionText());
					}else{
						questionSearchVO.setQuestionText(i.getQuestionText());
					}
					
					questionSearchVO.setStatus(i.getInternalStatus().getName());
					questionSearchVO.setDeviceType(i.getType().getName());
					questionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(i.getSession().getYear()));
					questionSearchVO.setSessionType(i.getSession().getType().getSessionType());
					
					questionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(i.getGroup().getNumber()));
					questionSearchVO.setGroup(i.getGroup().getId().toString());
					
						
					questionSearchVO.setMinistry(i.getMinistry().getName());					
					questionSearchVO.setDepartment(i.getSubDepartment().getDepartment().getName());
					questionSearchVO.setSubDepartment(i.getSubDepartment().getName());
					
					questionSearchVO.setStatusType(i.getStatus().getType());
					
					questionSearchVO.setFormattedPrimaryMember(i.getPrimaryMember().getFullnameLastNameFirst());
					
					questionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(i.getDiscussionDate(), ApplicationConstants.SERVER_DATEFORMAT, locale));
					
					result.add(questionSearchVO);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}	
	
	//*********************Clubbing************************
	public static boolean club(final Long primary, final Long clubbing, final String locale) throws ELSException{
		
		StandaloneMotion q1 = StandaloneMotion.findById(StandaloneMotion.class, primary);
		StandaloneMotion q2 = StandaloneMotion.findById(StandaloneMotion.class, clubbing);
		
		return club(q1, q2, locale); 
		
	}
	
	public static boolean club(final StandaloneMotion q1,final StandaloneMotion q2,final String locale) throws ELSException{    	
    	boolean clubbingStatus = false;
    	try {    		
    		if(q1.getParent()!=null || q2.getParent()!=null) {
    			throw new ELSException("error", "STANDALONE_ALREADY_CLUBBED");
    		} else {
    			
    			clubbingStatus = clubHDQ(q1, q2, locale);
        		    			
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
	
	 private static boolean clubHDQ(StandaloneMotion q1, StandaloneMotion q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForHDQ(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
    			clubbingStatus = clubHDQAssembly(q1, q2, locale);
        	} else if(q1.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
        		clubbingStatus = clubHDQCouncil(q1, q2, locale);
        	}
    	}    	 
    	return clubbingStatus;
    }
	
	private static boolean clubbingRulesForHDQ(StandaloneMotion q1, StandaloneMotion q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);    	
    	return clubbingStatus;    	
    }
    
    private static boolean clubHDQAssembly(StandaloneMotion q1, StandaloneMotion q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubHDQCommonForAssemblyAndCouncil(q1, q2, locale);
    	
    	if(!clubbingStatus) {
    		//=============cases specific to lowerhouse============//
        	Status yaadiLaidStatus = Status.findByType(ApplicationConstants.STANDALONE_PROCESSED_YAADILAID, locale);
        	
        	//Case 7: Both questions are admitted and balloted
        	if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
    				&& q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0
    				&& (q1.getBallotStatus()!=null && q1.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
    				&& (q2.getBallotStatus()!=null && q2.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))) {
        		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING_POST_ADMISSION, locale);
        		if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				actualClubbingStandaloneMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    				clubbingStatus = true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				actualClubbingStandaloneMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    				clubbingStatus = true;
    			} else {
    				clubbingStatus = true;
    			}
        	}
    	}
    	
    	return clubbingStatus;
    }
    
    private static boolean clubHDQCouncil(StandaloneMotion q1, StandaloneMotion q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubHDQCommonForAssemblyAndCouncil(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubHDQCommonForAssemblyAndCouncil(StandaloneMotion q1, StandaloneMotion q2, String locale) throws ELSException {
    	
    	boolean retVal = false;    	
    	
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);
		Status clubbedStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED, locale);
		
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.STANDALONE_CLUBBING_MODE, "");
		
		if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("normal")){
			
			if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
	    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)) {
	    		
	    		if(q1.getNumber().compareTo(q2.getNumber()) < 0) {
					actualClubbingHDQ(q1, q2, q1.getInternalStatus(), q1.getRecommendationStatus(), locale);
					retVal = true;
				} else if(q1.getNumber().compareTo(q2.getNumber()) > 0) {
					actualClubbingHDQ(q2, q1, q2.getInternalStatus(), q2.getRecommendationStatus(), locale);
					retVal = true;
				} else {
					retVal = false;
				}
	    	}
			
		}else if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("workflow")){
			//Case 1: Both questions are just ready to be put up
	    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
	    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)) {
	    		
	    		if(q1.getNumber().compareTo(q2.getNumber())<0) {
					actualClubbingHDQ(q1, q2, clubbedStatus, clubbedStatus, locale);
					retVal = true;
				} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
					actualClubbingHDQ(q2, q1, clubbedStatus, clubbedStatus, locale);
					retVal = true;
				} else {
					retVal = false;
				}
	    	}
	    	//Case 2A: One question is pending in approval workflow while other is ready to be put up
	    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
	    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING, locale);
	    		actualClubbingHDQ(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		retVal = true;
	    	}
	    	//Case 2B: One question is pending in approval workflow while other is ready to be put up
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
	    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING, locale);
	    		actualClubbingHDQ(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		retVal = true;
	    	}
	    	//Case 3: Both questions are pending in approval workflow
	    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
	    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
	    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	    		if(q1_approvalLevel==q2_approvalLevel) {
	    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	    				WorkflowDetails.endProcess(q2_workflowDetails);
	    				q2.removeExistingWorkflowAttributes();
	    				actualClubbingHDQ(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
	    				retVal = true;
	    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	    				WorkflowDetails.endProcess(q1_workflowDetails);
	    				q1.removeExistingWorkflowAttributes();
	    				actualClubbingHDQ(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    				retVal = true;
	    			} else {
	    				retVal = false;
	    			}
	    		} else if(q1_approvalLevel>q2_approvalLevel) {
	    			WorkflowDetails.endProcess(q2_workflowDetails);
	    			q2.removeExistingWorkflowAttributes();
	    			actualClubbingHDQ(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    			retVal = true;
	    		} else if(q1_approvalLevel<q2_approvalLevel) {
	    			WorkflowDetails.endProcess(q1_workflowDetails);
	    			q1.removeExistingWorkflowAttributes();
	    			actualClubbingHDQ(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    			retVal = true;
	    		} else {
	    			retVal = false;
	    		}    		
	    	}
	    	//Case 4A: One question is admitted but not balloted yet while other question is ready to be put up (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)				
	    			&& (q1.getBallotStatus()==null || !q1.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))
	    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_NAMECLUBBING, locale);
	    		actualClubbingHDQ(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		retVal = true;
	    	}
	    	//Case 4B: One question is admitted but not balloted yet while other question is ready to be put up (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
					&& (q2.getBallotStatus()==null || !q2.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_NAMECLUBBING, locale);
	    		actualClubbingHDQ(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		retVal = true;
	    	}
	    	//Case 5A: One question is admitted but not balloted yet while other question is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
	    			&& (q1.getBallotStatus()==null || !q1.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))
	    			&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
					&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_NAMECLUBBING, locale);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		WorkflowDetails.endProcess(q2_workflowDetails);
	    		q2.removeExistingWorkflowAttributes();
	    		actualClubbingHDQ(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		retVal = true;
	    	}
	    	//Case 5B: One question is admitted but not balloted yet while other question is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
					&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
					&& (q2.getBallotStatus()==null || !q2.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_NAMECLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails.endProcess(q1_workflowDetails);
	    		q1.removeExistingWorkflowAttributes();
	    		actualClubbingHDQ(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		retVal = true;
	    	}
	    	//Case 6: Both questions are admitted but not balloted yet
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
	    			&& (q1.getBallotStatus()==null || !q1.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))	
	    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
					&& (q2.getBallotStatus()==null || !q2.getBallotStatus().equals(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED))) {
	    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING_POST_ADMISSION, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
	    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	    				actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	    				retVal = true;
	    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	    				actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	    				retVal = true;
	    			} else {
	    				retVal = false;
	    			}
	    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
	    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	        		if(q1_approvalLevel==q2_approvalLevel) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				WorkflowDetails.endProcess(q2_workflowDetails);
	        				q2.removeExistingWorkflowAttributes();
	        				actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	        				retVal = true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				WorkflowDetails.endProcess(q1_workflowDetails);
	        				q1.removeExistingWorkflowAttributes();
	        				actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        				retVal = true;
	        			} else {
	        				retVal = false;
	        			}
	        		} else if(q1_approvalLevel>q2_approvalLevel) {
	        			WorkflowDetails.endProcess(q2_workflowDetails);
	        			q2.removeExistingWorkflowAttributes();
	        			actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			retVal = true;
	        		} else if(q1_approvalLevel<q2_approvalLevel) {
	        			WorkflowDetails.endProcess(q1_workflowDetails);
	        			q1.removeExistingWorkflowAttributes();
	        			actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			retVal = true;
	        		} else {
	        			retVal = false;
	        		}
	    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
	    			WorkflowDetails.endProcess(q2_workflowDetails);
	    			q2.removeExistingWorkflowAttributes();
					actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
					retVal = true;
	    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
	    			WorkflowDetails.endProcess(q1_workflowDetails);
	    			q1.removeExistingWorkflowAttributes();
	    			actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	    			retVal = true;
	    		} else {
	    			retVal = false;
	    		}
	    	}
	    	else {
	    		retVal = false;
	    	}
		}
		
		return retVal;
    }
    
    private static void actualClubbingHDQ(StandaloneMotion parent,StandaloneMotion child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
    	/**** a.Clubbed entities of parent standalone are obtained 
		 * b.Clubbed entities of child standalone are obtained
		 * c.Child standalone is updated(parent,internal status,recommendation status) 
		 * d.Child StandaloneMotion entry is made in Clubbed Entity and child standalone clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child standalones are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child standalone clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMnId = child.getId();
				StandaloneMotion clubbedMn = i.getStandaloneMotion();
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
				StandaloneMotion clubbedMn = i.getStandaloneMotion();
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

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setStandaloneMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.STANDALONE_CLUBBING_MODE, "");
		if(csptClubbingMode != null){
			if(csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()){
			
				if(csptClubbingMode.getValue().equals("normal")){
					
					Status submitted = Status.findByType(ApplicationConstants.STANDALONE_SUBMIT, locale);
					
					if(childClubbedEntities != null && !childClubbedEntities.isEmpty()){
						for(ClubbedEntity k : childClubbedEntities){
							StandaloneMotion motion = k.getStandaloneMotion();					
							
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
							StandaloneMotion motion=k.getStandaloneMotion();				
							/** find current clubbing workflow if pending **/
							String pendingWorkflowTypeForMotion = "";
							if(motion.getInternalStatus().getType().endsWith(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING)
									|| motion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
							} else if(motion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_NAMECLUBBING)
									|| motion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
							} else if(motion.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING_POST_ADMISSION)
									|| motion.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
							}
							if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
								/** end current clubbing workflow **/
								WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion, pendingWorkflowTypeForMotion);	
								WorkflowDetails.endProcess(wfDetails);
								motion.removeExistingWorkflowAttributes();
								/** put up for proper clubbing workflow as per updated parent **/
								Status finalAdmitStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION , locale);
								if(parent.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
									Status putupForClubbingStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING , locale);
									motion.setInternalStatus(putupForClubbingStatus);
									motion.setRecommendationStatus(putupForClubbingStatus);
								} else {
									if(motion.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
										Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_NAMECLUBBING , locale);
										motion.setInternalStatus(putupForNameClubbingStatus);
										motion.setRecommendationStatus(putupForNameClubbingStatus);
									} else {
										Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING_POST_ADMISSION , locale);
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
				parent.simpleMerge();
			} else {
				parent.merge();
			}
			
			List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByStandaloneMotionNumber(ApplicationConstants.ASC,locale);
			Integer position=1;
			for(ClubbedEntity i:clubbedEntities){
				i.setPosition(position);
				position++;
				i.merge();
			}
		}			
    }
    
    private static boolean clubbingRulesCommon(StandaloneMotion q1, StandaloneMotion q2, String locale) throws ELSException {
    	if(q1.getSession().equals(q2.getSession()) && !q1.getType().getType().equals(q2.getType().getType())) {
    		throw new ELSException("error", "STANDAONES_FROM_DIFFERENT_DEVICETYPE");    		
    	} else if(!q1.getMinistry().getName().equals(q2.getMinistry().getName())) {
    		throw new ELSException("error", "STANDALONES_FROM_DIFFERENT_MINISTRY");    		
    	} else if(!q1.getSubDepartment().getName().equals(q2.getSubDepartment().getName())) {
    		throw new ELSException("error", "STANDALONES_FROM_DIFFERENT_DEPARTMENT");    		
    	} else {
    		//clubbing rules succeeded
    		return true;
    	}  	
    }
    /**** Standalone Clubbing Ends ****/
    
    /**** standalone Update Clubbing Starts ****/
    public static void updateClubbing(StandaloneMotion motion) throws ELSException {
		//case 1: standalone is child
		if(motion.getParent()!=null) {
			StandaloneMotion.updateClubbingForChild(motion);
		} 
		//case 2: standalone is parent
		else if(motion.getParent()==null && motion.getClubbedEntities()!=null && !motion.getClubbedEntities().isEmpty()) {
			StandaloneMotion.updateClubbingForParent(motion);
		}
	}
    
    private static void updateClubbingForChild(StandaloneMotion motion) throws ELSException {
		if(motion.getParent().getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				& motion.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			updateClubbingForChildHDQ(motion);
		}
	}
    
    private static void actualClubbingStandaloneMotions(StandaloneMotion parent, StandaloneMotion child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent standalonemotion are obtained 
		 * b.Clubbed entities of child standalonemotion are obtained
		 * c.Child StandaloneMotion is updated(parent,internal status,recommendation status) 
		 * d.Child StandaloneMotion entry is made in Clubbed Entity and child standalonemotion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child standalonemotions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child standalonemotion clubbed entities,other clubbed entities of child standalonemotion and 
		 * clubbed entities of parent standalonemotion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities = new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities() != null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMnId = child.getId();
				StandaloneMotion clubbedMn = i.getStandaloneMotion();
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
				StandaloneMotion clubbedMn = i.getStandaloneMotion();
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

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setStandaloneMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				StandaloneMotion motion=k.getStandaloneMotion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForMotion = "";
				
				if(motion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING)
						|| motion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(motion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_NAMECLUBBING)
						|| motion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(motion.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| motion.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				
				
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion, pendingWorkflowTypeForMotion);	
					WorkflowDetails.endProcess(wfDetails);
					motion.removeExistingWorkflowAttributes();
					
					/** put up for proper clubbing workflow as per updated parent **/
					Status finalAdmitStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION , locale);
					Integer parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					Integer question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					
					if(parent.getStatus().getPriority().compareTo(parent_finalAdmissionStatusPriority)<0) {
						Status putupForClubbingStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING , locale);
												
						motion.setInternalStatus(putupForClubbingStatus);
						motion.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(motion.getStatus().getPriority().compareTo(question_finalAdmissionStatusPriority)<0) {
							Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_NAMECLUBBING , locale);
							motion.setInternalStatus(putupForNameClubbingStatus);
							motion.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_CLUBBING_POST_ADMISSION , locale);							
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

		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByStandaloneMotionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
    
    public void removeExistingWorkflowAttributes() {
		// Update standalone so as to remove existing workflow
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
    
    public List<ClubbedEntity> findClubbedEntitiesByStandaloneMotionNumber(final String sortOrder,
    		final String locale) {
    	return getStandaloneMotionRepository().findClubbedEntitiesByStandaloneMotionNumber(this,sortOrder,
    			locale);
    }
    
    private static void updateClubbingForParent(StandaloneMotion motion) {
		updateClubbingForParentHDQ(motion);
		
	}
    private static void updateClubbingForChildHDQ(StandaloneMotion motion) throws ELSException {
		String locale = motion.getLocale();
		StandaloneMotion parentMotion = motion.getParent();
		
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, motion.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, motion.getLocale());
		
		if(parentMotion.getNumber().compareTo(motion.getNumber())<0) {
			
			updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);
			
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED, motion.getLocale());
				motion.setInternalStatus(clubbedStatus);
				motion.setRecommendationStatus(clubbedStatus);
			} else {
				motion.setStatus(parentMotion.getInternalStatus());
				motion.setInternalStatus(parentMotion.getInternalStatus());
				motion.setRecommendationStatus(parentMotion.getInternalStatus());
			}				
			motion.simpleMerge();
			
		} else if(parentMotion.getNumber().compareTo(motion.getNumber())>0) {				
			
			WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentMotion);
			if(parentQuestion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentQuestion_workflowDetails);
				parentMotion.removeExistingWorkflowAttributes();
			}
			
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				motion.setInternalStatus(assistantProcessedStatus);
				motion.setRecommendationStatus(assistantProcessedStatus);
				//TODO:handle chart related changes if any for question question now again ready to be put up
				
				updateDomainFieldsOnClubbingFinalisation(motion, parentMotion);
				
				Status clubbedStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED, motion.getLocale());
				actualClubbingStandaloneMotions(motion, parentMotion, clubbedStatus, clubbedStatus, locale);
			} else {
				motion.setStatus(parentMotion.getInternalStatus());
				motion.setInternalStatus(parentMotion.getInternalStatus());
				if(parentMotion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
					Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING, motion.getLocale());
					motion.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(motion, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					motion.setRecommendationStatus(parentMotion.getInternalStatus());	
					
				}					
				if(parentMotion.getAnswer()!=null && (motion.getAnswer()==null || motion.getAnswer().isEmpty())) {
					motion.setAnswer(parentMotion.getAnswer());
				}
				if(parentMotion.getRejectionReason()!=null && (motion.getRejectionReason()==null || motion.getRejectionReason().isEmpty())) {
					motion.setRejectionReason(parentMotion.getRejectionReason());
				}
				
				updateDomainFieldsOnClubbingFinalisation(motion, parentMotion);					
				
				actualClubbingStandaloneMotions(motion, parentMotion, parentMotion.getInternalStatus(), parentMotion.getInternalStatus(), locale);
			}
		}
	}
    
    private static void updateClubbingForParentHDQ(StandaloneMotion motion) {
		for(ClubbedEntity ce: motion.getClubbedEntities()) {
			StandaloneMotion clubbedMotion = ce.getStandaloneMotion();
			if(clubbedMotion.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(motion, clubbedMotion);
				
				clubbedMotion.setStatus(motion.getInternalStatus());
				clubbedMotion.setInternalStatus(motion.getInternalStatus());
				clubbedMotion.setRecommendationStatus(motion.getInternalStatus());
				
				clubbedMotion.merge();
			}
		}
	}
    
    public static void updateDomainFieldsOnClubbingFinalisation(StandaloneMotion parent, StandaloneMotion child) {
    	if(parent.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				& child.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			updateDomainFieldsOnClubbingFinalisationForHDQ(parent, child);
		}
    }
    
    private static void updateDomainFieldsOnClubbingFinalisationForHDQ(StandaloneMotion parent, StandaloneMotion child) {
		updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
		/** copy latest reason of parent to revised reason of child **/
		if(parent.getRevisedReason()!=null && !parent.getRevisedReason().isEmpty()) {
			child.setRevisedReason(parent.getRevisedReason());
		} else {
			child.setRevisedReason(parent.getReason());
		}
		/** copy latest brief explanation of parent to revised brief explanation of child **/
		if(parent.getRevisedBriefExplanation()!=null && !parent.getRevisedBriefExplanation().isEmpty()) {
			child.setRevisedBriefExplanation(parent.getRevisedBriefExplanation());
		} else {
			child.setRevisedBriefExplanation(parent.getBriefExplanation());
		}		
	}
	
	private static void updateDomainFieldsOnClubbingFinalisationCommon(StandaloneMotion parent, StandaloneMotion child) {
		/** copy latest subject of parent to revised subject of child **/
		if(parent.getRevisedSubject()!=null && !parent.getRevisedSubject().isEmpty()) {
			child.setRevisedSubject(parent.getRevisedSubject());
		} else {
			child.setRevisedSubject(parent.getSubject());
		}
		/** copy latest question text of parent to revised question text of child **/
		if(parent.getRevisedQuestionText()!=null && !parent.getRevisedQuestionText().isEmpty()) {
			child.setRevisedQuestionText(parent.getRevisedQuestionText());
		} else {
			child.setRevisedQuestionText(parent.getQuestionText());
		}
		/** copy latest answer of parent to revised answer of child **/
		child.setAnswer(parent.getAnswer());
		/** copy latest rejection reason of parent to revised rejection reason of child **/
		child.setRejectionReason(parent.getRejectionReason());
	}


	 public static Group isGroupChanged(
    		final StandaloneMotion motion) throws ELSException {
    	/*
    	 * Refer to second previous draft because the first previous
    	 * because the first previous draft and @param question
    	 * have similar attributes.
    	 */
    	StandaloneMotionDraft draft = motion.findSecondPreviousDraft();
    	Group group = motion.getGroup();
    	
    	if(group != null && draft != null) {
    		Group previousGroup = draft.getGroup();
    		if(previousGroup != null && ! previousGroup.getId().equals(group.getId())) {
    			return previousGroup;
    		}
    	}
    	return null;
    }

    public static void onGroupChange(final StandaloneMotion motion,
    		final Group fromGroup) throws ELSException {
    	DeviceType deviceType = motion.getType();
    	String deviceTypeType = deviceType.getType();
    	
    	if(deviceTypeType.equals(
    			ApplicationConstants
    				.HALF_HOUR_DISCUSSION_STANDALONE)) {
    		StandaloneMotion.onHalfHourGroupChange(motion, fromGroup);
    	}
    	else {
    		throw new ELSException("StandaloneMotion.onGroupChange/2", 
    				"Method invoked for inappropriate device type.");
    	}
    }
    
    private static void onHalfHourGroupChange(final StandaloneMotion motion, 
    		final Group fromGroup) throws ELSException {
    	Session session = motion.getSession();
    	House house = session.getHouse();
    	HouseType houseType = house.getType();
    	
    	String houseTypeType = houseType.getType();
    	if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
    		//StandaloneMotion.onHalfHourGroupChangeLH(motion, fromGroup);
    	}
    	else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
    		StandaloneMotion.onHalfHourGroupChangeUH(motion, fromGroup);
    	}
    	else {
    		throw new ELSException("StandaloneMotion.onHalfHourGroupChange/2", 
				"StandaloneMotion has inappropriate house type.");
    	}
    }
	
	private static void onHalfHourGroupChangeUH(final StandaloneMotion motion,
			final Group fromGroup) throws ELSException {
    	String locale = motion.getLocale();
    	Status GROUP_CHANGED = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_GROUPCHANGED, locale);
    	
    	CLUBBING_STATE clubbingState = StandaloneMotion.findClubbingState(motion);
    	HALF_HOUR_STATE qnState = StandaloneMotion.findHalfHourState(motion);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("StandaloneMotion.onHalfHourGroupChangeCommon/2", 
    				"Clubbed StandaloneMotion group cannot be changed." +
    				" Unclub the standalonemotion and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    			// Change status to "GROUP_CHANGED"
    			motion.setInternalStatus(GROUP_CHANGED);
    			motion.setRecommendationStatus(GROUP_CHANGED);
    			motion.merge();
    		}
    		else if(qnState == HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(motion);
    			if(wfDetails != null){
    				WorkflowDetails.endProcess(wfDetails);
    			}
    			motion.removeExistingWorkflowAttributes();
    			
    			// Change status to "GROUP_CHANGED"
    			motion.setInternalStatus(GROUP_CHANGED);
    			motion.setRecommendationStatus(GROUP_CHANGED);
    			motion.merge();
    		}
    		else if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(motion);
    			
    			Integer assigneeLevel = null;
    			String userGroupType = null;
    			if(wfDetails != null){
	    			// Before ending wfDetails process collect information
	    			// which will be useful for creating a new process later.
	    			String workflowType = wfDetails.getWorkflowType();
	    			assigneeLevel = Integer.parseInt(wfDetails.getAssigneeLevel());
	    			userGroupType = wfDetails.getAssigneeUserGroupType();
	    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
	    			WorkflowDetails.endProcess(wfDetails);
    			}
    			motion.removeExistingWorkflowAttributes();
    			/*
    			 * Change recommendation status to final (internal) status.
    			 */
    			Status internalStatus = motion.getInternalStatus();
    			motion.setRecommendationStatus(internalStatus);
    			motion.merge();
    			
    			if(assigneeLevel != null){
	    			/*
	    			 * Start the workflow at Assistant (after Speaker) level. it creates null pointer exception so removed
	    			 */
	    			WorkflowDetails.startProcessAtGivenLevel(motion, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    		}else if(qnState == HALF_HOUR_STATE.POST_BALLOT){
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(motion);
    			String userGroupType = null;
    			Integer assigneeLevel = null;
    			if(wfDetails != null){
	    			// Before ending wfDetails process collect information
	    			// which will be useful for creating a new process later.
	    			String workflowType = wfDetails.getWorkflowType();
	    			userGroupType = wfDetails.getAssigneeUserGroupType();
	    			assigneeLevel = 
	    				Integer.parseInt(wfDetails.getAssigneeLevel());
	    			
	    			WorkflowDetails.endProcess(wfDetails);
    			}
    			motion.removeExistingWorkflowAttributes();
    			Status internalStatus = motion.getInternalStatus();
    			motion.setRecommendationStatus(internalStatus);
    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
    				userGroupType = ApplicationConstants.DEPARTMENT;
    				assigneeLevel = assigneeLevel - 1;
    			}
				
    			//Question in Post final status and pre ballot state can be group changed by Department 
    			//as well as assistant of Secretariat
    			WorkflowDetails.startProcessAtGivenLevel(motion, 
    					ApplicationConstants.APPROVAL_WORKFLOW, motion.getInternalStatus(), 
    					userGroupType, assigneeLevel, 
    					locale);
    		}
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = StandaloneMotion.isHavingIllegalChild(motion);
    		if(isHavingIllegalChild) {
    			throw new ELSException(
    					"StandaloneMotion.onHalfHourGroupChangeCommon/2", 
        				"StandaloneMotion has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    				/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				motion.setInternalStatus(GROUP_CHANGED);
        			motion.setRecommendationStatus(GROUP_CHANGED);
        			motion.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = motion.getGroup();
        			Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			
        			List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
        			for(StandaloneMotion kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        		}
        		else if(qnState == HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
        			/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
    				if(wfDetails != null){
    					WorkflowDetails.endProcess(wfDetails);
    				}
        			motion.removeExistingWorkflowAttributes();
        			
        			/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				motion.setInternalStatus(GROUP_CHANGED);
        			motion.setRecommendationStatus(GROUP_CHANGED);
        			motion.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = motion.getGroup();
        			Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			
        			List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
        			for(StandaloneMotion kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        		}
        		else if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT
        				|| qnState == HALF_HOUR_STATE.POST_BALLOT) {
        			/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(motion);
    				Integer assigneeLevel = null;
    				if(wfDetails != null){
	        			// Before ending wfDetails process collect information
	        			// which will be useful for creating a new process later.
	        			String workflowType = wfDetails.getWorkflowType();
	        			assigneeLevel = 
	        				Integer.parseInt(wfDetails.getAssigneeLevel());
	        			
	        			WorkflowDetails.endProcess(wfDetails);
    				}
        			motion.removeExistingWorkflowAttributes();
        			
        			/*
        			 * Change recommendation status to final (internal) status.
        			 */
        			Status internalStatus = motion.getInternalStatus();
        			motion.setRecommendationStatus(internalStatus);
        			motion.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = motion.getGroup();
        			Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			
        			List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
        			for(StandaloneMotion kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        			
        			if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT){
        				if(assigneeLevel != null){
		        			/*
		    				 * Start the workflow at Assistant (after Speaker) level.
		    				 */
		        			WorkflowDetails.startProcessAtGivenLevel(motion, 
		        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
		        					ApplicationConstants.ASSISTANT, assigneeLevel, 
		        					locale);
        				}
        			}
        		}
    		}
    	}
	}
	
	public static CLUBBING_STATE findClubbingState(final StandaloneMotion motion) {
    	if(motion.getParent() != null) {
    		return StandaloneMotion.CLUBBING_STATE.CLUBBED;
    	}
    	else {
    		List<ClubbedEntity> clubbings = StandaloneMotion.findClubbedEntitiesByPosition(motion);
    		if(clubbings.isEmpty()) {
    			return StandaloneMotion.CLUBBING_STATE.STANDALONE;
    		}
    		else {
    			return StandaloneMotion.CLUBBING_STATE.PARENT;
    		}
    	}
    }
	
	public static enum HALF_HOUR_STATE {
    	PRE_WORKFLOW,
    	IN_WORKFLOW_AND_PRE_FINAL,
    	POST_FINAL_AND_PRE_BALLOT,
    	POST_BALLOT
    }
	
	public static enum CLUBBING_STATE {
    	STANDALONE, 
    	PARENT, 
    	CLUBBED
    }
	
	private static List<StandaloneMotion> findClubbings(final StandaloneMotion motion) {
		List<StandaloneMotion> motions = new ArrayList<StandaloneMotion>();
		
		List<ClubbedEntity> clubbings = StandaloneMotion.findClubbedEntitiesByPosition(motion);
		for(ClubbedEntity ce : clubbings) {
			StandaloneMotion q = ce.getStandaloneMotion();
			motions.add(q);
		}
		
		return motions;
	}
	
	 private static HALF_HOUR_STATE findHalfHourState(
	    		final StandaloneMotion motion) throws ELSException {
	    	Status internalStatus = motion.getInternalStatus();
	    	String internalStatusType = internalStatus.getType();
	    	
	    	Status ballotStatus = motion.getBallotStatus();
	    	
	    	if(internalStatusType.equals(ApplicationConstants.STANDALONE_SUBMIT) 
	    			|| internalStatusType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
	    			|| internalStatusType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) {
	    		return HALF_HOUR_STATE.PRE_WORKFLOW;
	    	}
	    	else if(internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_ADMISSION)
	    			|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_REPEATADMISSION)
	    			|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_REJECTION)
	    			|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_REPEATREJECTION)
	    	    	|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER)
	    	    	|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
	    	    	|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_GOVT)
	    	    	|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
	    	    	|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
	    	        || internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
	    	        || internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_FROM_GOVT)
	    	        || internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT)) {
	    		return HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL;
	    	}
	    	else if(ballotStatus == null
	    			&& (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
	    				|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_REPEATADMISSION)
	    				|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)
	    				|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION))) {
	    		return HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT;
	    	}
	    	else if(ballotStatus != null) {
	    		return HALF_HOUR_STATE.POST_BALLOT;
	    	}
	    	else {
	    		throw new ELSException("StandaloneMotion.findHalfHourState/1", 
					"Unhandled status type.");
	    	}
	    }
	 
	 private static boolean isHavingIllegalChild(
				final StandaloneMotion motion) throws ELSException {
		DeviceType deviceType = motion.getType();
		String deviceTypeType = deviceType.getType();
		
		if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return StandaloneMotion.isHalfHourHavingIllegalChild(motion);
		}
		else {
			throw new ELSException("StandaloneMotion.isHavingIllegalChild/1", 
					"Illegal deviceType set in @param standalone.");
		}
	}
	 
	private static boolean isHalfHourHavingIllegalChild(StandaloneMotion motion) {
		List<ClubbedEntity> clubbings = StandaloneMotion.findClubbedEntitiesByPosition(motion); 
		for(ClubbedEntity ce : clubbings) {
			StandaloneMotion q = ce.getStandaloneMotion();
			
			Status internalStatus = q.getInternalStatus();
			String internalStatusType = internalStatus.getType();
				
			Status recommendationStatus = q.getRecommendationStatus();
			String recommendationStatusType = recommendationStatus.getType();
				
			if(internalStatusType.equals(ApplicationConstants.STANDALONE_PUTUP_CLUBBING)
					|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING)
					|| internalStatusType.equals(ApplicationConstants.STANDALONE_PUTUP_NAMECLUBBING)
					|| internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_NAMECLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_PUTUP_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING_POST_ADMISSION)) {
				return true;
			}
		}
		
		return false;
	}
	//*********************Clubbing************************
	
	//*********************Unclubbing***********************
	public static boolean unclub(final Long m1, final Long m2, String locale) throws ELSException {
		StandaloneMotion motion1 = StandaloneMotion.findById(StandaloneMotion.class, m1);
		StandaloneMotion motion2 = StandaloneMotion.findById(StandaloneMotion.class, m2);
		return unclub(motion1, motion2, locale);
		
	}
	
	public static boolean unclub(final StandaloneMotion q1, final StandaloneMotion q2, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(q1.getParent()==null && q2.getParent()==null) {
			throw new ELSException("error", "CLUBBED_STANDALONE_NOT_FOUND");
		}
		if(q2.getParent()!=null && q2.getParent().equals(q1)) {
			clubbingStatus = actualUnclubbing(q1, q2, locale);
		} else if(q1.getParent()!=null && q1.getParent().equals(q2)) {
			clubbingStatus = actualUnclubbing(q2, q1, locale);
		} else {
			throw new ELSException("error", "NO_CLUBBING_BETWEEN_GIVEN_STANDALONES");
		}
		return clubbingStatus;
	}
	
	public static boolean unclub(final StandaloneMotion motion, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(motion.getParent()==null) {
			throw new ELSException("error", "STANDALONE_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(motion.getParent(), motion, locale);
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbing(final StandaloneMotion parent, final StandaloneMotion child, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(parent.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				& child.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			clubbingStatus = actualUnclubbingHDQ(parent, child, locale);
		}
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbingHDQ(final StandaloneMotion parent, final StandaloneMotion child, String locale) throws ELSException {
		boolean retVal = false;
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.STANDALONE_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("normal")){
    		Status putupStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, locale);
    		Status submitStatus = Status.findByType(ApplicationConstants.STANDALONE_SUBMIT, locale);
    		
    		/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedMotions = parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedMotions = new ArrayList<ClubbedEntity>();
			Integer position = 0;
			boolean found = false;
			
			for(ClubbedEntity i : oldClubbedMotions){
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
			Status approvedStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);		
			if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0) {
				Status putupUnclubStatus = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_UNCLUBBING, locale);
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
					if(! i.getStandaloneMotion().getId().equals(child.getId())){
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
						// clubbedEntityToRemove=i;
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
				if(child.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(child.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_NAMECLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| child.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForMotion);
					if(wfDetails != null){
						WorkflowDetails.endProcess(wfDetails);
						child.removeExistingWorkflowAttributes();
					}
				}
				/** update child status **/
				Status assistantProcessedStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, locale);
				child.setInternalStatus(assistantProcessedStatus);
				child.setRecommendationStatus(assistantProcessedStatus);
			}	
			child.simpleMerge();
			retVal = true;
		}
    	
		return retVal;
	}
	//*********************Unclubbing***********************
	
	public static int updateUnBallot(final Member member, final Session session, 
			final DeviceType deviceType, final Status internalStatus, final Date discussionDate){
		return getStandaloneMotionRepository().updateUnBallot(member, session, deviceType, internalStatus, discussionDate);
	}
	
	
	/****Calling attention atomic value ****/
	public static void updateHDSCurrentNumberLowerHouse(Integer num){
		synchronized (StandaloneMotion.HDS_CUR_NUM_LOWER_HOUSE) {
			StandaloneMotion.HDS_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getHDSCurrentNumberLowerHouse(){
		return StandaloneMotion.HDS_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateHDSCurrentNumberUpperHouse(Integer num){
		synchronized (StandaloneMotion.HDS_CUR_NUM_UPPER_HOUSE) {
			StandaloneMotion.HDS_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getHDSCurrentNumberUpperHouse(){
		return StandaloneMotion.HDS_CUR_NUM_UPPER_HOUSE;
	}
	
	public static org.mkcl.els.common.vo.Reference getCurNumber(final Session session, final DeviceType deviceType){
    	
    	org.mkcl.els.common.vo.Reference ref = new org.mkcl.els.common.vo.Reference();
    	String strHouseType = session.getHouse().getType().getType();
    	
    	if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
    		
			ref.setName(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE);
			ref.setNumber(StandaloneMotion.getHDSCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
    		
    		ref.setName(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE);
			ref.setNumber(StandaloneMotion.getHDSCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    	}
    	
    	return ref;
    }
    
    public static void updateCurNumber(final Integer num, final String houseType,final String device){
    	
    	if(device.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
				StandaloneMotion.updateHDSCurrentNumberLowerHouse(num);
			}
			if (houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
				StandaloneMotion.updateHDSCurrentNumberUpperHouse(num);
			}
    	}
    }
    
    public static int findHighestFileNo(final Session session,
			final DeviceType motionType,final String locale) {
		return getStandaloneMotionRepository().findHighestFileNo(session,
				motionType,locale);
	}
    
    public static boolean isAllowedForSubmission(final StandaloneMotion motion, final Date date){
    	
    	Session session = motion.getSession();
    	
    	if(motion != null){
    		if(session != null){
    			Date startDate = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_submissionStartDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    			Date endDate = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_submissionEndDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    			
    			if(date.compareTo(startDate)>=0 && date.compareTo(endDate)<=0){
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    public void startWorkflow(final StandaloneMotion motion, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
    	//end current workflow if exists
		motion.endWorkflow(motion, workflowHouseType, locale);
    	//update motion statuses as per the workflow status
    	motion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
    	//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(motion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
    
    public void endWorkflow(final StandaloneMotion motion, final String workflowHouseType, final String locale) throws ELSException {
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


	public static List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		return getStandaloneMotionRepository().fullTextSearchForSearching(param,start,noOfRecords, locale, requestMap);
	}


	public static void onSubDepartmentChange(StandaloneMotion motion, SubDepartment prevSubDepartment) throws ELSException {
		String locale = motion.getLocale();
		CLUBBING_STATE clubbingState = StandaloneMotion.findClubbingState(motion);
		HALF_HOUR_STATE qnState = StandaloneMotion.findHalfHourState(motion);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onStarredGroupChangeLH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    			motion.setRecommendationStatus(motion.getInternalStatus());
    			motion.merge();
    		}else if(qnState == HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL 
    				|| qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT
    				|| qnState == HALF_HOUR_STATE.POST_BALLOT) {
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(motion);
    			motion.removeExistingWorkflowAttributes();
    			motion.setRecommendationStatus(motion.getInternalStatus());
    			motion.merge();
    			if(wfDetails != null){
	    			// Before ending wfDetails process collect information
	    			// which will be useful for creating a new process later.
	    			String workflowType = wfDetails.getWorkflowType();
	    			Integer assigneeLevel = 
	    				Integer.parseInt(wfDetails.getAssigneeLevel());
	    			String userGroupType = wfDetails.getAssigneeUserGroupType();
	    			WorkflowDetails.endProcess(wfDetails);
	    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
	    			//Question in Post final status and pre ballot state can be group changed by Department 
	    			//as well as assistant of Secretariat
	    			if(motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}else{
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}
    			}
    		}
    	}else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			StandaloneMotion.isHavingIllegalChild(motion);
    		if(isHavingIllegalChild) {
    			throw new ELSException("Question.onStarredMinistryChangeLH/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    				List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
    				// Unclub all the Questions
        			for(StandaloneMotion child : clubbings) {
        				StandaloneMotion.unclub(motion, child, locale);
        			}
        			motion.setRecommendationStatus(motion.getInternalStatus());
        			motion.merge();
        			Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			for(StandaloneMotion kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
       			}
    			else if(qnState == HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(motion);
        			WorkflowDetails.endProcess(wfDetails);
        			motion.removeExistingWorkflowAttributes();
    				List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
        			/*
    				 * Unclub all the Questions
    				 */
        			for(StandaloneMotion child : clubbings) {
        				StandaloneMotion.unclub(motion, child, locale);
        			}
        			motion.setRecommendationStatus(motion.getInternalStatus());
        			motion.merge();
      				Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			for(StandaloneMotion kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
    			}
    			else if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(motion);
        			
    				String workflowType = null;
        			Integer assigneeLevel = null;
        			String userGroupType = null;
        					
    				if(wfDetails != null){
	        			// Before ending wfDetails process collect information
	        			// which will be useful for creating a new process later.
	        			workflowType = wfDetails.getWorkflowType();
	        			assigneeLevel = 
	        				Integer.parseInt(wfDetails.getAssigneeLevel());
	        			userGroupType = wfDetails.getAssigneeUserGroupType();
	        			WorkflowDetails.endProcess(wfDetails);
    				}
    				motion.removeExistingWorkflowAttributes();
    				motion.setRecommendationStatus(motion.getInternalStatus());
    				motion.merge();
        			
        			Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			
        			List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
        			for(StandaloneMotion kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
            			kid.merge();
        			}
        				  
        			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
	    			  			
	    			//Question in Post final status and pre ballot state can be group changed by Department 
	    			//as well as assistant of Secretariat
	    			if(motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}else{
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}
    			}
     		}
    	}
		
		
	}


	public static void onMinistryChange(StandaloneMotion motion, Ministry prevMinistry) throws ELSException {
		String locale = motion.getLocale();
		CLUBBING_STATE clubbingState = StandaloneMotion.findClubbingState(motion);
		HALF_HOUR_STATE qnState = StandaloneMotion.findHalfHourState(motion);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onStarredGroupChangeLH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    			motion.setRecommendationStatus(motion.getInternalStatus());
    			motion.merge();
    		}else if(qnState == HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL 
    				|| qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT
    				|| qnState == HALF_HOUR_STATE.POST_BALLOT) {
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(motion);
    			motion.removeExistingWorkflowAttributes();
    			motion.setRecommendationStatus(motion.getInternalStatus());
    			motion.merge();
    			if(wfDetails != null){
	    			// Before ending wfDetails process collect information
	    			// which will be useful for creating a new process later.
	    			String workflowType = wfDetails.getWorkflowType();
	    			Integer assigneeLevel = 
	    				Integer.parseInt(wfDetails.getAssigneeLevel());
	    			String userGroupType = wfDetails.getAssigneeUserGroupType();
	    			WorkflowDetails.endProcess(wfDetails);
	    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
	    			//Question in Post final status and pre ballot state can be group changed by Department 
	    			//as well as assistant of Secretariat
	    			if(motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}else{
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}

    			}
    		}
    	}else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			StandaloneMotion.isHavingIllegalChild(motion);
    		if(isHavingIllegalChild) {
    			throw new ELSException("Question.onStarredMinistryChangeLH/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    				List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
    				// Unclub all the Questions
        			for(StandaloneMotion child : clubbings) {
        				StandaloneMotion.unclub(motion, child, locale);
        			}
        			motion.setRecommendationStatus(motion.getInternalStatus());
        			motion.merge();
        			Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			for(StandaloneMotion kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
       			}
    			else if(qnState == HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(motion);
        			WorkflowDetails.endProcess(wfDetails);
        			motion.removeExistingWorkflowAttributes();
    				List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
        			/*
    				 * Unclub all the Questions
    				 */
        			for(StandaloneMotion child : clubbings) {
        				StandaloneMotion.unclub(motion, child, locale);
        			}
        			motion.setRecommendationStatus(motion.getInternalStatus());
        			motion.merge();
      				Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			for(StandaloneMotion kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
    			}
    			else if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(motion);
        			
    				String workflowType = null;
        			Integer assigneeLevel = null;
        			String userGroupType = null;
        					
    				if(wfDetails != null){
	        			// Before ending wfDetails process collect information
	        			// which will be useful for creating a new process later.
	        			workflowType = wfDetails.getWorkflowType();
	        			assigneeLevel = 
	        				Integer.parseInt(wfDetails.getAssigneeLevel());
	        			userGroupType = wfDetails.getAssigneeUserGroupType();
	        			WorkflowDetails.endProcess(wfDetails);
    				}
    				motion.removeExistingWorkflowAttributes();
    				motion.setRecommendationStatus(motion.getInternalStatus());
    				motion.merge();
        			
        			Ministry ministry = motion.getMinistry();
        			SubDepartment subDepartment = motion.getSubDepartment();
        			
        			List<StandaloneMotion> clubbings = StandaloneMotion.findClubbings(motion);
        			for(StandaloneMotion kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
            			kid.merge();
        			}
        				  
        			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
	    			  			
	    			if(motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}else{
		    			WorkflowDetails.startProcessAtGivenLevel(motion, 
		    					ApplicationConstants.APPROVAL_WORKFLOW, motion.getInternalStatus(), 
		    					userGroupType, assigneeLevel, 
		    					locale);
	    			}
    			}
     		}
    	}
		
	}
	
	public static void supportingMemberWorkflowDeletion(final StandaloneMotion standaloneMotion) {
    	if(standaloneMotion!=null && standaloneMotion.getId()>0) {
    		if(anySupportingMembersWorkflows(standaloneMotion)) {
    			deleteSupportingMembersWorkflows(standaloneMotion);
    		}
    	}
    }
    
    public static boolean anySupportingMembersWorkflows(final StandaloneMotion standaloneMotion) {
		List<SupportingMember> supportingMembers = standaloneMotion.getSupportingMembers();
		if(supportingMembers!=null && supportingMembers.size()>0) {
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean deleteSupportingMembersWorkflows(final StandaloneMotion standaloneMotion) {
		List<Long> workflowDetailsList=new ArrayList<Long>();
		if(standaloneMotion!=null && standaloneMotion.getId()>0 && standaloneMotion.getSupportingMembers()!=null 
				&& standaloneMotion.getSupportingMembers().size()>0) {
			List<SupportingMember> supportingMembers = standaloneMotion.getSupportingMembers();
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
	
	public static List<StandaloneMotion> findAllAdmittedUndisccussed(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale){
		return getStandaloneMotionRepository().findAllAdmittedUndisccussed(session, motionType, status, locale);
	}
	
}