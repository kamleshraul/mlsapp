/**
l * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Question.java
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OptimisticLockException;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.domain.ballot.BallotEntry;
import org.mkcl.els.domain.ballot.DeviceSequence;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class Question.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */ 

@Configurable
@Entity
@Table(name="questions")
@JsonIgnoreProperties(value={"houseType", "session", "originalType", "type","creationDate","createdBy",
	"dataEnteredBy","editedOn","editedBy","answeringDate","chartAnsweringDate",
	"subject","revisedSubject","questionText","revisedQuestionText","answer","priority",
	"ballotStatus", "remarks","rejectionReason", "supportingMembers",
	"group", "drafts", "parent", "clubbedEntities", "referencedEntities",
	"halfHourDiscusionFromQuestionReference", "language", "referencedHDS","workflowDetailsId","bulkSubmitted","taskReceivedOn","workflowStartedOn","level",
	"endFlag","localizedActorName","actor","workflowStarted","answeringAttemptsByDepartment"
	,"markAsAnswered","prospectiveClubbings","lastDateOfAnswerReceiving","revisedBriefExplanation",
	"briefExplanation","discussionDate","dateOfAnsweringByMinister","toBeAnsweredByMinister"
	,"revisedReason","reason","numberOfDaysForFactualPositionReceiving",
	"lastDateOfFactualPositionReceiving","factualPosition","questionsAskedInFactualPosition"
	,"locale","version","versionMismatch","editedAs","questionreferenceText","rejectionReason","referenceDeviceType","referenceDeviceMember","referenceDeviceAnswerDate"},ignoreUnknown=true)
public class Question extends Device implements Serializable {
		
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    //added by dhananjayb
    /** The Constant logger. */
    //private static final Logger logger = LoggerFactory.getLogger(Question.class);
    
    /**** Attributes ****/    
    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;

    /** The session. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="originaldevicetype_id")
    private DeviceType originalType;
    
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

    /** The answering date. */
    @ManyToOne(fetch=FetchType.LAZY)
    private QuestionDates answeringDate;    

    @ManyToOne(fetch=FetchType.LAZY)
    private QuestionDates chartAnsweringDate;
    
    /** The subject. */
    @Column(length=30000)
    private String subject;

    /** The subject. */
    @Column(length=30000)
    private String revisedSubject;

    /** The question text. */
    @Column(length=30000)
    private String questionText;
    
    /**** Question Referencing Text ****/
    @Column(length=30000)
    private String questionreferenceText;

    /** The question text. */
    @Column(length=30000)
    private String revisedQuestionText;

    /** The answer. */
    @Column(length=30000)
    private String answer;

    /** The priority. */
    private Integer priority;
    
    /** The submission priority 
     *  To be used for bulk submission by member
     */
    private Integer submissionPriority;

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
    @JoinTable(name="questions_supportingmembers",
            joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")},
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
    @JoinTable(name="questions_drafts_association", 
    		joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="question_draft_id", referencedColumnName="id")})
    private Set<QuestionDraft> drafts;    

    
    /**** Clubbing ****/
    /** The parent. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Question parent;
    
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="questions_clubbingentities", 
    		joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;


    /**** Referencing ****/
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="questions_referencedunits", 
    		joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="referenced_unit_id", referencedColumnName="id")})
    private List<ReferenceUnit> referencedEntities;
    
    @ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    private ReferencedEntity referencedHDS;
    
    /**** UNSTARRED DEVICE ATTRIBUTES ****/
    private Integer yaadiNumber; 
    
    @Temporal(TemporalType.DATE)
    private Date yaadiLayingDate;
    
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
    
    
    /**** HALF HOUR DEVICE ATTRIBUTES ****/
    @ManyToOne(fetch=FetchType.LAZY)
    private Question halfHourDiscusionFromQuestionReference;
    
    private String halfHourDiscusionFromQuestionReferenceNumber;
    
    private String referenceDeviceType;
    
	private String referenceDeviceMember;
	
	@Temporal(TemporalType.DATE)
	private Date referenceDeviceAnswerDate;

    @Temporal(TemporalType.DATE)
    private Date discussionDate;

    @Column(length=30000)
    private String briefExplanation;
    
    @Column(length=30000)
    private String revisedBriefExplanation;  
    
    @Temporal(TemporalType.DATE)
    private Date lastDateOfAnswerReceiving;    

    
    /**** REMOVE UNWANTED FIELDS. START ****/
    /** The language. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="language_id")
    private Language language;
    
    /** The prospective clubbings. */
    @Column(length=5000)
    private String prospectiveClubbings;
    
    /** The mark as answered. */
    private Boolean markAsAnswered;
    
    /**** REMOVE UNWANTED FIELDS. START ****/
    
    /**** To be used in case of bulk submission and workflows****/
    private Integer answeringAttemptsByDepartment;
    
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
    private String questionsAskedInFactualPositionForMember;
    
    // for Clarification From Department 
    @Column(length=30000)
    private String factualPosition;
    
    // for Clarification From Member
    @Column(length=30000)
    private String factualPositionFromMember;
    
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
    
    /** The answer received mode (ONLINE/OFFLINE). */
    @Column(name="answer_received_mode", length=50)
    private String answerReceivedMode;
    
    /**** Fields for storing the confirmation of Group change ****/
    private Boolean transferToDepartmentAccepted = false;
    
    private Boolean mlsBranchNotifiedOfTransfer = false;
    
    /**** Reason for Late Reply ****/
    @Column(name="reason_for_late_reply",length=30000)
    private String reasonForLateReply;
    
    /**** Processed by Authorities ****/
    private Boolean processed = false;
    	       
    private static transient volatile Integer STARRED_CUR_NUM_LOWER_HOUSE = 0;
    private static transient volatile Integer STARRED_CUR_NUM_UPPER_HOUSE = 0;
    
    private static transient volatile Integer UNSTARRED_CUR_NUM_LOWER_HOUSE = 0;
    private static transient volatile Integer UNSTARRED_CUR_NUM_UPPER_HOUSE = 0;
    
    private static transient volatile Integer SHORTNOTICE_CUR_NUM_LOWER_HOUSE = 0;
    private static transient volatile Integer SHORTNOTICE_CUR_NUM_UPPER_HOUSE = 0;
    
    private static transient volatile Integer HDQ_CUR_NUM_LOWER_HOUSE = 0;
    private static transient volatile Integer HDQ_CUR_NUM_UPPER_HOUSE = 0;
    
    /** The question repository. */
    @Autowired
    private transient QuestionRepository questionRepository;
    
    
	
	/**** Constructors ****/
	
    /**
     * Instantiates a new question.
     */
    public Question() {
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
        return getQuestionRepository().getRevisions(questionId,locale);
    }
    
    public static MemberBallotMemberWiseReportVO findMemberWiseReportVO(final Session session,
    		final DeviceType questionType, final Member member, final String locale) throws ELSException{
    	return getQuestionRepository().findMemberWiseReportVO(session, questionType, member, locale);		
   	}
    
    /*public static boolean getState(Question q){
    	return getQuestionRepository().getState(q);
    }*/
    
    public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}	
    
    @Override
    public Question persist() {
    	
    	if(this.getType() != null){
    		String deviceTypeType = this.getType().getType();
    		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)){
    			return persistStarredQuestion();
    		}
    		else if(deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)){
    			return persistUnstarredQuestion();
    		}
    		else if(deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
    			return persistShortNoticeQuestion();
    		}
    		else if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
    			return persistHalfHourDiscussionQuestionFromQuestion();
    		}
    	}
    	
		return null;
    }
    
    private synchronized Question persistHalfHourDiscussionQuestionFromQuestion() {
    	if(this.getStatus().getType().
    			equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SUBMIT)) {
            if(this.getNumber() == null) {
            	synchronized (Question.class) {
                	Integer number = null;
					try {
						
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getHDQCurrentNumberLowerHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateHDQCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getHDQCurrentNumberUpperHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateHDQCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getHDQCurrentNumberLowerHouse() + 1);
	            			Question.updateHDQCurrentNumberLowerHouse(Question.getHDQCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getHDQCurrentNumberUpperHouse() + 1);
	            			Question.updateHDQCurrentNumberUpperHouse(Question.getHDQCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		//addQuestionDraft();
	                    return (Question)super.persist();
					} catch (ELSException e) {
						e.printStackTrace();
					}
                }
            }
            else if(this.getNumber()!=null){
            	addQuestionDraft();
            }
		}
		return (Question) super.persist();
	}


	private Question persistShortNoticeQuestion() {
		if(this.getStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SUBMIT)) {
            if(this.getNumber() == null) {
            	synchronized (Question.class) {
                	Integer number = null;
					try {
						
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getStarredCurrentNumberLowerHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getStarredCurrentNumberUpperHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberLowerHouse() + 1);
	            			Question.updateStarredCurrentNumberLowerHouse(Question.getStarredCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberUpperHouse() + 1);
	            			Question.updateStarredCurrentNumberUpperHouse(Question.getStarredCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		//addQuestionDraft();
	                    return (Question)super.persist();
					} catch (ELSException e) {
						e.printStackTrace();
					}
                }
            }
            else if(this.getNumber()!=null){
            	addQuestionDraft();
            }
		}
		 return (Question) super.persist();
	}


	private Question persistUnstarredQuestion() {
		if(this.getStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (Question.class) {
                	Integer number = null;
					try {
						
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getStarredCurrentNumberLowerHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getStarredCurrentNumberUpperHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberLowerHouse() + 1);
	            			Question.updateStarredCurrentNumberLowerHouse(Question.getStarredCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberUpperHouse() + 1);
	            			Question.updateStarredCurrentNumberUpperHouse(Question.getStarredCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		//addQuestionDraft();
	                    return (Question)super.persist();
					} catch (ELSException e) {
						e.printStackTrace();
					}
                }
            }
            else if(this.getNumber()!=null){
            	addQuestionDraft();
            }
		}
		 return (Question) super.persist();
	}


	private Question persistStarredQuestion() {
		if(this.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (Question.class) {
                	
                	Integer number = null;
					try {
						String houseType = this.getHouseType().getType();
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getStarredCurrentNumberLowerHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getStarredCurrentNumberUpperHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberLowerHouse() + 1);
		            		this.setSubmissionDate(new Date());
	            			Question.updateStarredCurrentNumberLowerHouse(Question.getStarredCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberUpperHouse() + 1);
		            		this.setSubmissionDate(new Date());
	            			Question.updateStarredCurrentNumberUpperHouse(Question.getStarredCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		//addQuestionDraft();
	            		Question q = (Question)super.persist();
	            		
	            		return q;
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
					}
                }
            }
            else if(this.getNumber()!=null){
            	addQuestionDraft();
            }
		}
		
		return (Question) super.persist();
	}


	@Override
    public Question merge() {
		if(this.getType() != null){
    		String deviceTypeType = this.getType().getType();
    		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)){
    			return mergeStarredQuestion();
    		}
    		else if(deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)){
    			return mergeUnstarredQuestion();
    		}
    		else if(deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
    			return mergeShortNoticeQuestion();
    		}
    		else if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
    			return mergeHalfHourDiscussionQuestionFromQuestion();
    		}
    	}
		return null;
    }
    
	private Question mergeStarredQuestion() {
		Question question = null;
		if ((this.getInternalStatus().getType()
				.equals(ApplicationConstants.QUESTION_SUBMIT))
				|| (this.getInternalStatus().getType()
						.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED))) {
			if (this.getNumber() == null) {
				synchronized (Question.class) {

					Integer number = null;
					try {

						String houseType = this.getHouseType().getType();
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getStarredCurrentNumberLowerHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getStarredCurrentNumberUpperHouse() == 0) {
								number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberLowerHouse() + 1);
		            		this.setSubmissionDate(new Date());
	            			Question.updateStarredCurrentNumberLowerHouse(Question.getStarredCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberUpperHouse() + 1);
		            		this.setSubmissionDate(new Date());
	            			Question.updateStarredCurrentNumberUpperHouse(Question.getStarredCurrentNumberUpperHouse() + 1);
	            		}
						//addQuestionDraft();
						question = (Question) super.merge();
						
					}catch (ELSException e) {
						e.printStackTrace();
					}
				}
			} else {
				Question oldQuestion = Question.findById(Question.class,
						this.getId());
				if (this.getClubbedEntities() == null) {
					this.setClubbedEntities(oldQuestion.getClubbedEntities());
				}
				if (this.getReferencedEntities() == null) {
					this.setReferencedEntities(oldQuestion
							.getReferencedEntities());
				}
				this.addQuestionDraft();
				question = (Question) super.merge();
			}
		} else if (this.getInternalStatus().getType()
				.equals(ApplicationConstants.QUESTION_INCOMPLETE)
				|| this.getInternalStatus().getType()
						.equals(ApplicationConstants.QUESTION_COMPLETE)) {
			// 22-12-2014: added by dhananjayb to retain drafts in case of
			// question getting this status as result of updation error in
			// workflow
			Question oldQuestion = Question.findById(Question.class,
					this.getId());
			Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
			this.setDrafts(originalDrafts);
			// -----------------------------------------------------------------------------
			question = (Question) super.merge();
		}
		if (question != null) {
			return question;
		} else {
			if (this.getInternalStatus().getType()
					.equals(ApplicationConstants.QUESTION_INCOMPLETE)
					|| this.getInternalStatus().getType()
							.equals(ApplicationConstants.QUESTION_COMPLETE)) {
				// 22-12-2014: added by dhananjayb to retain drafts in case of
				// question getting this status as result of updation error in
				// workflow
				Question oldQuestion = Question.findById(Question.class,
						this.getId());
				Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
				this.setDrafts(originalDrafts);
				// ----------------------------------------------------------------------------
				return (Question) super.merge();
			} else {
				Question oldQuestion = Question.findById(Question.class,
						this.getId());
				if (this.getClubbedEntities() == null) {
					this.setClubbedEntities(oldQuestion.getClubbedEntities());
				}
				/** update parent's fields to its final children, post parent's final decision excluding group change case **/
				if(this.getClubbedEntities()!=null && !this.getClubbedEntities().isEmpty()
						&& this.getGroup()!=null && this.getGroup().equals(oldQuestion.getGroup())) {
					this.updateChildrenPostFinalDecision();
				}
				if (this.getReferencedEntities() == null) {
					this.setReferencedEntities(oldQuestion
							.getReferencedEntities());
				}
				this.addQuestionDraft();
				return (Question) super.merge();
			}
		}
	}
	

	private Question mergeUnstarredQuestion() {
		Question question = null;
        if((this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)) 
        		|| (this.getInternalStatus().getType().
        				equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED))){
            if(this.getNumber() == null) {
                synchronized (Question.class) {

                	try {
						
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getStarredCurrentNumberLowerHouse() == 0) {
								Integer number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getStarredCurrentNumberUpperHouse() == 0) {
								Integer number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberLowerHouse() + 1);
	            			Question.updateStarredCurrentNumberLowerHouse(Question.getStarredCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberUpperHouse() + 1);
	            			Question.updateStarredCurrentNumberUpperHouse(Question.getStarredCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		//addQuestionDraft();
	                    question = (Question) super.merge();
					} catch (ELSException e) {
						e.printStackTrace();
					}
                }
            }
            else {
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldQuestion.getClubbedEntities());
            	}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldQuestion.getReferencedEntities());
            	}
            	this.addQuestionDraft();
            	question = (Question) super.merge();
            }
        }
        else if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_COMPLETE)){
        	//22-12-2014: added by dhananjayb to retain drafts in case of question getting this status as result of updation error in workflow
        	Question oldQuestion = Question.findById(Question.class, this.getId());
        	Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
        	this.setDrafts(originalDrafts);
        	//----------------------------------------------------------------------------
        	return (Question) super.merge();
        }
        if(question != null) {
            return question;
        }
        else {
            if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_COMPLETE)) {
            	//22-12-2014: added by dhananjayb to retain drafts in case of question getting this status as result of updation error in workflow
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
            	this.setDrafts(originalDrafts);
            	//----------------------------------------------------------------------------
                return (Question) super.merge();
            }
            else {
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldQuestion.getClubbedEntities());
            	}
            	/** update parent's fields to its final children, post parent's final decision excluding group change case **/
            	if(this.getClubbedEntities()!=null && !this.getClubbedEntities().isEmpty()
						&& this.getGroup()!=null && this.getGroup().equals(oldQuestion.getGroup())) {
					this.updateChildrenPostFinalDecision();
				}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldQuestion.getReferencedEntities());
            	}
                this.addQuestionDraft();
                return (Question) super.merge();
            }
        }
	}


	private Question mergeShortNoticeQuestion() {
		Question question = null;
        if((this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SUBMIT)) 
        		|| (this.getInternalStatus().getType().
        				equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED))){
            if(this.getNumber() == null) {
                synchronized (Question.class) {

                	try {
						
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getStarredCurrentNumberLowerHouse() == 0) {
								Integer number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getStarredCurrentNumberUpperHouse() == 0) {
								Integer number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateStarredCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberLowerHouse() + 1);
	            			Question.updateStarredCurrentNumberLowerHouse(Question.getStarredCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getStarredCurrentNumberUpperHouse() + 1);
	            			Question.updateStarredCurrentNumberUpperHouse(Question.getStarredCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		//addQuestionDraft();
	                    question = (Question) super.merge();
					} catch (ELSException e) {
						e.printStackTrace();
					}
                }
            }
            else {
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldQuestion.getClubbedEntities());
            	}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldQuestion.getReferencedEntities());
            	}
            	this.addQuestionDraft();
            	question = (Question) super.merge();
            }
        }
        else if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_COMPLETE)){
        	//22-12-2014: added by dhananjayb to retain drafts in case of question getting this status as result of updation error in workflow
        	Question oldQuestion = Question.findById(Question.class, this.getId());
        	Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
        	this.setDrafts(originalDrafts);
        	//----------------------------------------------------------------------------
        	return (Question) super.merge();
        }
        if(question != null) {
            return question;
        }
        else {
            if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_COMPLETE)) {
            	//22-12-2014: added by dhananjayb to retain drafts in case of question getting this status as result of updation error in workflow
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
            	this.setDrafts(originalDrafts);
            	//----------------------------------------------------------------------------
                return (Question) super.merge();
            }
            else {
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldQuestion.getClubbedEntities());
            	}
            	/** update parent's fields to its final children, post parent's final decision excluding group change case **/
            	if(this.getClubbedEntities()!=null && !this.getClubbedEntities().isEmpty()
						&& this.getGroup()!=null && this.getGroup().equals(oldQuestion.getGroup())) {
					this.updateChildrenPostFinalDecision();
				}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldQuestion.getReferencedEntities());
            	}
                this.addQuestionDraft();
                return (Question) super.merge();
            }
        }
	}


	private Question mergeHalfHourDiscussionQuestionFromQuestion() {
		Question question = null;
        if((this.getInternalStatus().getType().
        		equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SUBMIT)) 
        		|| (this.getInternalStatus().getType().
        				equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED))){
            if(this.getNumber() == null) {
                synchronized (Question.class) {

                	try {
						
                		String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {							
							if (Question.getHDQCurrentNumberLowerHouse() == 0) {
								Integer number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateHDQCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
							if (Question.getHDQCurrentNumberUpperHouse() == 0) {
								Integer number = Question.
										assignQuestionNo(this.getHouseType(),this.getSession(), 
												this.getType(),this.getLocale());
								Question.updateHDQCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	            			this.setNumber(Question.getHDQCurrentNumberLowerHouse() + 1);
	            			Question.updateHDQCurrentNumberLowerHouse(Question.getHDQCurrentNumberLowerHouse() + 1);
	            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	            			this.setNumber(Question.getHDQCurrentNumberUpperHouse() + 1);
	            			Question.updateHDQCurrentNumberUpperHouse(Question.getHDQCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		//addQuestionDraft();
	                    question = (Question) super.merge();
					} catch (ELSException e) {
						e.printStackTrace();
					}
                }
            }
            else {
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldQuestion.getClubbedEntities());
            	}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldQuestion.getReferencedEntities());
            	}
            	this.addQuestionDraft();
            	question = (Question) super.merge();
            }
        }
        else if(this.getInternalStatus().getType().
        		equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().
            	equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_COMPLETE)){
        	//22-12-2014: added by dhananjayb to retain drafts in case of question getting this status as result of updation error in workflow
        	Question oldQuestion = Question.findById(Question.class, this.getId());
        	Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
        	this.setDrafts(originalDrafts);
        	//----------------------------------------------------------------------------
        	return (Question) super.merge();
        }
        if(question != null) {
            return question;
        }
        else {
            if(this.getInternalStatus().getType().
            		equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().
            	equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_COMPLETE)) {
            	//22-12-2014: added by dhananjayb to retain drafts in case of question getting this status as result of updation error in workflow
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	Set<QuestionDraft> originalDrafts = oldQuestion.getDrafts();
            	this.setDrafts(originalDrafts);
            	//----------------------------------------------------------------------------
                return (Question) super.merge();
            }
            else {
            	Question oldQuestion = Question.findById(Question.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldQuestion.getClubbedEntities());
            	}
            	/** update parent's fields to its final children, post parent's final decision excluding group change case **/
            	if(this.getClubbedEntities()!=null && !this.getClubbedEntities().isEmpty()
						&& this.getGroup()!=null && this.getGroup().equals(oldQuestion.getGroup())) {
					this.updateChildrenPostFinalDecision();
				}
            	if(this.getReferencedEntities() == null){
            		this.setReferencedEntities(oldQuestion.getReferencedEntities());
            	}
                this.addQuestionDraft();
                return (Question) super.merge();
            }
        }
	}

	
	private void updateChildrenPostFinalDecision() {
		Status finalAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, this.getLocale());
		if(this.getStatus().getPriority().intValue()>=finalAdmissionStatus.getPriority().intValue()) {
			for(ClubbedEntity ce: this.getClubbedEntities()) {
				Question child = ce.getQuestion();
				if(child.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_ADMISSION)
						|| child.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_REJECTION)) {
													
					child.setRevisedSubject(this.getRevisedSubject());
					child.setRevisedQuestionText(this.getRevisedQuestionText());
					child.setRevisedReason(this.getRevisedReason());
					child.setRevisedBriefExplanation(this.getRevisedBriefExplanation());
					child.setAnswer(this.getAnswer());
					child.setRejectionReason(this.getRejectionReason());								
					
					child.simpleMerge();
				}
			}
		}
	}


	/**
     * The merge function, besides updating Question, performs various actions
     * based on Question's status. What if we need just the simple functionality
     * of updation? Use this method.
     *
     * @return the question
     */
    public Question simpleMerge() {
    	if(!this.getStatus().getType().endsWith(ApplicationConstants.STATUS_INCOMPLETE)
    			|| !this.getStatus().getType().endsWith(ApplicationConstants.STATUS_COMPLETE)) {
    		if(this.getDrafts()==null || this.getDrafts().isEmpty()) {
    			Question dbQuestion = Question.findById(Question.class, this.getId());
    			this.setDrafts(dbQuestion.getDrafts());
    		}
    	}
        Question q = (Question) super.merge();
        return q;
    }
    
    /**
     * Assign question no.
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
    public static Integer assignQuestionNo(final HouseType houseType, 
    		final Session session, final DeviceType deviceType, final String locale) throws ELSException {
        return getQuestionRepository().assignQuestionNo(houseType, session, deviceType, locale);
    }
    
    /**
     * Find.
     *
     * @param session the session
     * @param number the number
     * @return the question
     * @throws ELSException 
     */
    public static Question find(final Member member, final Session session, final DeviceType deviceType, final String locale) throws ELSException {
        return Question.getQuestionRepository().find(member, session, deviceType, locale);
    }
    
    public static List<Question> findAll(final Session session, final DeviceType deviceType, final Integer number, final String locale) throws ELSException {
        return Question.getQuestionRepository().findAll(session, deviceType, number, locale);
    }
    /**
     * Find.
     *
     * @param session the session
     * @param number the number
     * @return the question
     */
    public static Question find(final Session session, final Integer number) {
        return Question.getQuestionRepository().find(session, number);
    }

    /**
    * Find.
    *
    * @param session the session
    * @param number the number
    * @return the question
    */
    public static Question findQuestionExcludingGivenDeviceTypes(final Session session, final Integer number,final String locale, Long...deviceTypeIds) {
    	return Question.getQuestionRepository().findQuestionExcludingGivenDeviceTypes(session, number, locale, deviceTypeIds);
    }
    
    /**
     * This method finds all the questions of a member of a particular device type,
     * belonging to a particular session and having internal status as specified
     * 
     * @param currentMember the current member
     * @param session the session
     * @param deviceType the device type
     * @param internalStatus the internal status
     * @return the list
     */
    public static List<Question> findAll(final Member currentMember,
            final Session session, final DeviceType deviceType, final Status internalStatus) {
        return getQuestionRepository().findAll(currentMember, session, deviceType, internalStatus);
    }
    
    /**
     * Find all first batch.
     *
     * @param currentMember the current member
     * @param session the session
     * @param deviceType the device type
     * @param internalStatus the internal status
     * @return the list
     */
//    public static List<Question> findAllFirstBatch(final Member currentMember,
//            final Session session, final DeviceType deviceType, final Status internalStatus) {
//        return getQuestionRepository().findAllFirstBatch(currentMember, 
//        		session, deviceType, internalStatus);
//    }

    /**
     * Find all second batch.
     *
     * @param currentMember the current member
     * @param session the session
     * @param deviceType the device type
     * @param internalStatus the internal status
     * @return the list
     */
//    public static List<Question> findAllSecondBatch(final Member currentMember,
//            final Session session, final DeviceType deviceType, final Status internalStatus) {
//        return getQuestionRepository().findAllSecondBatch(currentMember,
//                session, deviceType, internalStatus);
//    }   
    
    public static org.mkcl.els.common.vo.Reference getCurNumber(final Session session, final DeviceType deviceType){
    	
    	org.mkcl.els.common.vo.Reference ref = new org.mkcl.els.common.vo.Reference();
    	String strHouseType = session.getHouse().getType().getType();
    	String strDeviceType = deviceType.getType();
    	
    	if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
    		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)){
    			
    			ref.setName(ApplicationConstants.STARRED_QUESTION);
    			ref.setNumber(Question.getStarredCurrentNumberLowerHouse().toString());
    			ref.setId(ApplicationConstants.LOWER_HOUSE);
    			
    		}else if(strDeviceType.equals(ApplicationConstants.UNSTARRED_QUESTION)){
    			
    			ref.setName(ApplicationConstants.UNSTARRED_QUESTION);
    			ref.setNumber(Question.getStarredCurrentNumberLowerHouse().toString());
    			ref.setId(ApplicationConstants.LOWER_HOUSE);
    			
    		}else if(strDeviceType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
    			
    			ref.setName(ApplicationConstants.SHORT_NOTICE_QUESTION);
    			ref.setNumber(Question.getStarredCurrentNumberLowerHouse().toString());
    			ref.setId(ApplicationConstants.LOWER_HOUSE);
    			
    		}else if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){

    			ref.setName(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION);
    			ref.setNumber(Question.getHDQCurrentNumberLowerHouse().toString());
    			ref.setId(ApplicationConstants.LOWER_HOUSE);
    			
    		}
    	}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
    		
    		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)){
    			
    			ref.setName(ApplicationConstants.STARRED_QUESTION);
    			ref.setNumber(Question.getStarredCurrentNumberUpperHouse().toString());
    			ref.setId(ApplicationConstants.UPPER_HOUSE);
    			
    		}else if(strDeviceType.equals(ApplicationConstants.UNSTARRED_QUESTION)){
    			
    			ref.setName(ApplicationConstants.UNSTARRED_QUESTION);
    			ref.setNumber(Question.getStarredCurrentNumberUpperHouse().toString());
    			ref.setId(ApplicationConstants.UPPER_HOUSE);
    			
    		}else if(strDeviceType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
    			
    			ref.setName(ApplicationConstants.SHORT_NOTICE_QUESTION);
    			ref.setNumber(Question.getStarredCurrentNumberUpperHouse().toString());
    			ref.setId(ApplicationConstants.UPPER_HOUSE);
    			
    		}else if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){

    			ref.setName(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION);
    			ref.setNumber(Question.getHDQCurrentNumberUpperHouse().toString());
    			ref.setId(ApplicationConstants.UPPER_HOUSE);
    			
    		}
    	}
    	
    	return ref;
    }
    
    public static void updateCurNumber(Integer num, String houseType, String device){
    	
    	if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    		if (device.equals(ApplicationConstants.STARRED_QUESTION)) {
    			
    			Question.updateStarredCurrentNumberLowerHouse(num);
    			
    		} else if (device.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    			
    			Question.updateStarredCurrentNumberLowerHouse(num);
    			
    		} else if (device.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    			
    			Question.updateStarredCurrentNumberLowerHouse(num);
    			
    		} else if (device.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
    			
    			Question.updateHDQCurrentNumberLowerHouse(num);
    			
    		}
    	}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    		if (device.equals(ApplicationConstants.STARRED_QUESTION)) {
    			
    			Question.updateStarredCurrentNumberUpperHouse(num);
    			
    		} else if (device.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    			
    			Question.updateStarredCurrentNumberUpperHouse(num);
    			
    		} else if (device.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    			
    			Question.updateStarredCurrentNumberUpperHouse(num);
    			
    		} else if (device.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
    			
    			Question.updateHDQCurrentNumberUpperHouse(num);
    			
    		}
    	}
    	
		
    }
    
	public static List<Question> findAdmittedStarredQuestionsUH(final Session session, 
			final DeviceType questionType, final Member member, final String locale) {
		return getQuestionRepository().findAdmittedStarredQuestionsUH(session,
				questionType, member, locale);
	}
	
	public static List<Question> findAdmittedStarredQuestionsUHByChartDate(final Session session, 
			final DeviceType questionType, final Member member, final String locale) throws ELSException {
		return getQuestionRepository().findAdmittedStarredQuestionsUHByChartDate(session,
				questionType, member, locale);
	}
    
    /**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group for a given @param answeringDate.
     * The Question should have been submitted on or before
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param answeringDate the answering date
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param excludeQuestions the exclude questions
     * @param maxNoOfQuestions the max no of questions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<Question> find(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Question[] excludeQuestions,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().find(session, member, deviceType,
                group, answeringDate, finalSubmissionDate, internalStatuses, excludeQuestions,
                maxNoOfQuestions, sortOrder, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }
    
    /**
    * Find a list of Questions for the given @param session
    * of a given @param deviceType submitted between @param
    * startTime & @param endTime (both date inclusive) having
    * either of the @param internalStatuses. The Questions
    * should have discussionDate = null OR 
    * discussionDate <= @param answeringDate
    * 
    * Sort the resulting list of Questions by number according
    * to the @param sortOrder.
    * 
    * Returns an empty list if there are no Questions.
    */
    public static List<Question> find(final Session session,
    	final DeviceType deviceType,
    	final Date answeringDate,
    	final Status[] internalStatuses,
    	final Boolean hasParent,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    return Question.getQuestionRepository().find(session, deviceType, answeringDate, 
    		internalStatuses, hasParent, startTime, endTime, sortOrder, locale);
    }

    /**
     * Find the questions based on ballot status
     * @param session
     * @param deviceType
     * @param answeringDate
     * @param internalStatuses
     * @param hasParent
     * @param isBalloted
     * @param startTime
     * @param endTime
     * @param sortOrder
     * @param locale
     * @return
     */
    public static List<Question> findByBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
    	
    	return getQuestionRepository().findByBallot(session, deviceType, answeringDate, internalStatuses, hasParent, isBalloted, isMandatoryUnique, isPreBallot, startTime, endTime, sortOrder, locale);
    }
    
    public static String findBallotedMembers(final Session session, final String memberNotice, final DeviceType deviceType){
    	return getQuestionRepository().findBallotedMembers(session, memberNotice, deviceType);
    }
    
    public static String findBallotedSubjects(final Session session, final DeviceType deviceType){
    	return getQuestionRepository().findBallotedSubjects(session, deviceType);
    }
    
    /**
     * @param session
     * @param deviceType
     * @param answeringDate
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
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
    	return getQuestionRepository().findPrimaryMembersByBallot(session, deviceType, answeringDate, internalStatuses, hasParent, isBalloted, startTime, endTime, sortOrder, locale);
    }
    
    /**
     * @param session
     * @param deviceType
     * @param answeringDate
     * @param memberID
     * @param subjects
     * @param locale
     * @return
     * @throws ELSException 
     */
    public static Question getQuestionForMemberOfUniqueSubject(final Session session, final DeviceType deviceType, final Date answeringDate,  final Long memberID, final List<String> subjects, final String locale) throws ELSException{
    	return getQuestionRepository().getQuestionForMemberOfUniqueSubject(session, deviceType, answeringDate, memberID, subjects, locale);
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
    public static List<Question> findQuestionsByDiscussionDateAndMember(final Session session,
			final DeviceType deviceType,
			final Long memberId,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		
		return getQuestionRepository().findQuestionsByDiscussionDateAndMember(session, deviceType, memberId, answeringDate, internalStatuses, startTime, endTime, sortOrder, locale);
	}
    
    
    /**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group. All this questions should
     * have an answering date mentioned. The answering date should be less than
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param answeringDate the answering date
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param excludeQuestions the exclude questions
     * @param maxNoOfQuestions the max no of questions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<Question> findBeforeAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Question[] excludeQuestions,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findBeforeAnsweringDate(session,
                member, deviceType, group, answeringDate, finalSubmissionDate, internalStatuses,
                excludeQuestions, maxNoOfQuestions, sortOrder, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }

    /**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group without an answering date.
     * The Question should have been submitted on or before
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param excludeQuestions the exclude questions
     * @param maxNoOfQuestions the max no of questions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Question[] excludeQuestions,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findNonAnsweringDate(session,
                member, deviceType, group, finalSubmissionDate, internalStatuses,
                excludeQuestions, maxNoOfQuestions, sortOrder, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }

    /**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group without an answering date.
     * The Question should have been submitted on or before
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param maxNoOfQuestions the max no of questions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findNonAnsweringDate(session,
                member, deviceType, group, finalSubmissionDate, internalStatuses, maxNoOfQuestions,
                sortOrder, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }

    /**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group. All this questions should
     * have an answering date mentioned. The answering date should be equal to
     * or less than @param answeringDate. The Question should have been submitted on or
     * before @param finalSubmissionDate.
     *
     * The Questions with answeringdate = @param answeringDate should take
     * precedence over answeringdate < @param answeringDate. Break the ties using
     * question number.
     *
     * Returns an empty list (if there are no questions for the specified criteria)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param answeringDate the answering date
     * @param finalSubmissionDate the final submission date
     * @param internalStatuses the internal statuses
     * @param maxNoOfQuestions the max no of questions
     * @param locale the locale
     * @return the list
     */
    public static List<Question> findDatedQuestions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findDatedQuestions(session,
                member, deviceType, group, answeringDate, finalSubmissionDate, internalStatuses,
                maxNoOfQuestions, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }
    
    /**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group. All this questions should
     * have an answering date mentioned. The answering date should be equal to
     * or less than @param answeringDate. The Question should have been submitted
     * between @param startTime and @param endTime (both time inclusive).
     *
     * The Questions with answeringdate = @param answeringDate should take
     * precedence over answeringdate < @param answeringDate. Break the ties using
     * question number.
     *
     * Returns an empty list (if there are no questions for the specified criteria)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param answeringDate the answering date
     * @param startTime the start time
     * @param endTime the end time
     * @param internalStatuses the internal statuses
     * @param maxNoOfQuestions the max no of questions
     * @param locale the locale
     * @return the list
     */
    public static List<Question> findDatedQuestions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findDatedQuestions(session,
                member, deviceType, group, answeringDate, startTime, endTime, internalStatuses,
                maxNoOfQuestions, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }

    /**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group without an answering date.
     * The Question should have been submitted between @param startTime
     * and @param endTime (both time inclusive).
     *
     * Questions should be sorted as per @param sortOrder according to
     * Question number.
     *
     * Returns an empty list (if there are no questions for the specified
     *
     * @param session the session
     * @param member the member
     * @param deviceType the device type
     * @param group the group
     * @param startTime the start time
     * @param endTime the end time
     * @param internalStatuses the internal statuses
     * @param maxNoOfQuestions the max no of questions
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findNonAnsweringDate(session,
                member, deviceType, group, startTime, endTime, internalStatuses, maxNoOfQuestions,
                sortOrder, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }

    /**
     * Find all the Questions of a @param member for a given @param session having.
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
    public static List<Question> findDatedQuestions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findDatedQuestions(session,
                member, deviceType, group, answeringDate, startTime, endTime, internalStatuses,
                locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }

    /**
     * Find all the Questions of a @param member for a given @param session
     * having @param group without an answering date. The Question should
     * have been submitted between @param startTime and @param endTime
     * (both time inclusive).
     *
     * Questions should be sorted as per @param sortOrder according to
     * Question number.
     *
     * Returns an empty list (if there are no questions for the specified
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
    public static List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findNonAnsweringDate(session,
                member, deviceType, group, startTime, endTime, internalStatuses, sortOrder, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
    }
    
    /**
     * Find previous draft.
     *
     * @return the question draft
     */
    public QuestionDraft findPreviousDraft() {
//        Set<QuestionDraft> drafts = this.getDrafts();
//        if(drafts != null) {
//            int size = drafts.size();
//            if(size > 1) {
//                return drafts.get(size - 1);
//            }
//        }
//        return null;
    	Long id = this.getId();
    	return Question.getQuestionRepository().findPreviousDraft(id);
    }
    
    /**
     * Sort the Questions as per @param sortOrder by number. If multiple Questions
     * have same number, then there order is preserved.
     *
     * @param questions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<Question> sortByNumber(final List<Question> questions,
            final String sortOrder) {
        List<Question> newQList = new ArrayList<Question>();
        newQList.addAll(questions);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    return q1.getNumber().compareTo(q2.getNumber());
                }
            };
            Collections.sort(newQList, c);
        } else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    return q2.getNumber().compareTo(q1.getNumber());
                }
            };
            Collections.sort(newQList, c);
        }

        return newQList;
    }
    
    /**
     * Sort the Questions as per @param sortOrder by priority. If multiple Questions
     * have same priority, then break the tie by Question number.
     *
     * @param questions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<Question> sortByPriority(final List<Question> questions,
            final String sortOrder) {
        List<Question> newQList = new ArrayList<Question>();
        newQList.addAll(questions);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    int i = q1.getPriority().compareTo(q2.getPriority());
                    if(i == 0) {
                        int j = q1.getNumber().compareTo(q2.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newQList, c);
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    int i = q2.getPriority().compareTo(q1.getPriority());
                    if(i == 0) {
                        int j = q2.getNumber().compareTo(q1.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newQList, c);
        }

        return newQList;
    }
    
    /**
     * Sort the Questions as per @param sortOrder by submission priority. If multiple Questions
     * have same submission priority, then their order is preserved.
     *
     * @param questions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<Question> sortBySubmissionPriority(final List<Question> questions,
            final String sortOrder) {
        List<Question> newQList = new ArrayList<Question>();
        newQList.addAll(questions);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    return q1.getSubmissionPriority().compareTo(q2.getSubmissionPriority());
                }
            };
            Collections.sort(newQList, c);
        } else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    return q2.getSubmissionPriority().compareTo(q1.getSubmissionPriority());
                }
            };
            Collections.sort(newQList, c);
        }

        return newQList;
    }
    
    /**
     * Sort the Questions as per @param sortOrder by answeringDate. If multiple Questions
     * have same answeringDate, then break the tie by Question number.
     *
     * @param questions SHOULD NOT BE NULL
     *
     * Does not sort in place, returns a new list.
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<Question> sortByAnsweringDate(final List<Question> questions,
            final String sortOrder) {
        List<Question> newQList = new ArrayList<Question>();
        newQList.addAll(questions);

        if(sortOrder.equals(ApplicationConstants.ASC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    int i = q1.getAnsweringDate().getAnsweringDate().
                    compareTo(q2.getAnsweringDate().getAnsweringDate());
                    if(i == 0) {
                        int j = q1.getNumber().compareTo(q2.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newQList, c);
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            Comparator<Question> c = new Comparator<Question>() {

                @Override
                public int compare(final Question q1, final Question q2) {
                    int i = q2.getAnsweringDate().getAnsweringDate().
                    compareTo(q1.getAnsweringDate().getAnsweringDate());
                    if(i == 0) {
                        int j = q2.getNumber().compareTo(q1.getNumber());
                        return j;
                    }
                    return i;
                }
            };
            Collections.sort(newQList, c);
        }

        return newQList;
    }
    
    public static List<ClubbedEntity> findClubbedEntitiesByPosition(final Question question) {
    	return getQuestionRepository().findClubbedEntitiesByPosition(question);
    }
    
    public static List<ClubbedEntity> findClubbedEntitiesByPosition(final Question question, final String sortOrder) {
    	return getQuestionRepository().findClubbedEntitiesByPosition(question, sortOrder);
    }
    
    public List<ClubbedEntity> findClubbedEntitiesByQuestionNumber(final String sortOrder,
    		final String locale) {
    	return getQuestionRepository().findClubbedEntitiesByQuestionNumber(this,sortOrder,
    			locale);
    }
    
    public List<ClubbedEntity> findClubbedEntitiesByChartAnsweringDateQuestionNumber(final String sortOrder,
    		final String locale) {
    	return getQuestionRepository().findClubbedEntitiesByChartAnsweringDateQuestionNumber(this,sortOrder,
    			locale);
    }
    
    /**
    * Find a list (without repetitions) of Primary Members who
    * have submitted Question(s) between @param startTime & 
    * @param endTime (both date inclusive) for the given @param 
    * session of a given @param deviceType submitted  having
    * either of the @param internalStatuses. The Questions
    * should have discussionDate = null OR 
    * discussionDate <= @param answeringDate
    * 
    * Sort the resulting list of Members by Question number according
    * to the @param sortOrder.
    * 
    * Returns an empty list if there are no Members.
    */
    public static List<Member> findPrimaryMembers(final Session session,
    	final DeviceType deviceType,
    	final Date answeringDate,
    	final Status[] internalStatuses,
    	final Boolean hasParent,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    	return Question.getQuestionRepository().findPrimaryMembers(session, deviceType, 
    		answeringDate, internalStatuses, hasParent, startTime, 
    		endTime, sortOrder, locale);
    }
    
    public static List<Member> findPrimaryMembersForBallot(final Session session,
        	final DeviceType deviceType,
        	final Date answeringDate,
        	final Status[] internalStatuses,
        	final Boolean hasParent,
        	final Date startTime,
        	final Date endTime,
        	final String sortOrder,
        	final String locale) {
        	return Question.getQuestionRepository().findPrimaryMembersForBallot(session, deviceType, 
        		answeringDate, internalStatuses, hasParent, startTime, 
        		endTime, sortOrder, locale);
        }

    public static List<Member> findActiveMembersWithQuestions(final Session session,
    	final Date activeOn,
    	final DeviceType deviceType,
    	final Group group,
    	final Status[] internalStatuses,
    	final Date answeringDate,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    	MemberRole role = MemberRole.find(session.getHouse().getType(), "MEMBER", locale);
    	return Question.getQuestionRepository().findActiveMembersWithQuestions(session, 
    		role, activeOn, deviceType, group, internalStatuses, answeringDate, 
    		startTime, endTime, sortOrder, locale);
    }

    public static List<Member> findActiveMembersWithoutQuestions(final Session session,
    	final Date activeOn,
    	final DeviceType deviceType,
    	final Group group,
    	final Status[] internalStatuses,
    	final Date answeringDate,
    	final Date startTime,
    	final Date endTime,
    	final String sortOrder,
    	final String locale) {
    	MemberRole role = MemberRole.find(session.getHouse().getType(), "MEMBER", locale);
    	return Question.getQuestionRepository().findActiveMembersWithoutQuestions(session, 
    		role, activeOn, deviceType, group, internalStatuses, answeringDate, 
    		startTime, endTime, sortOrder, locale);
    }

    public String findFormattedNumber() {
    	NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
    	return format.format(this.getNumber());
    }
    
    public static Member findDeviceOwner(final Question question) {
    	Member deviceOwner = null;
    	if(question!=null) {    		
        	if(question.getPrimaryMember().isSupportingOrClubbedMemberToBeAddedForDevice(question)) {
        	    deviceOwner = question.getPrimaryMember();
        	} else {
        	    if(question.getSupportingMembers()!=null) {
        	        for(SupportingMember sm: question.getSupportingMembers()) {
        	            Member supportingMember = sm.getMember();
        	            Status approvalStatus = sm.getDecisionStatus();
        	            if(supportingMember!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
        	                if(supportingMember.isSupportingOrClubbedMemberToBeAddedForDevice(question)) {
        	                    deviceOwner = supportingMember;
        	                    break;
        	                }
        	            }
        	        }
        	    }
        	    if(deviceOwner==null) {
        	        List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(question);
        	        if (clubbedEntities != null) {
        	            for (ClubbedEntity ce : clubbedEntities) {
        	            	if (ce.getQuestion().getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_SYSTEM_CLUBBED)
        							|| ce.getQuestion().getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_ADMISSION)
        							|| ce.getQuestion().getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_REJECTION)) {
        						Member clubbedQuestionMember = ce.getQuestion().getPrimaryMember();
        						if(clubbedQuestionMember!=null) {
        							if(clubbedQuestionMember.isSupportingOrClubbedMemberToBeAddedForDevice(question)) {
        								deviceOwner = clubbedQuestionMember;
        	    	                    break;
        							}
        						}
        						if(deviceOwner==null) {
        							List<SupportingMember> clubbedSupportingMembers = ce.getQuestion().getSupportingMembers();
            						if (clubbedSupportingMembers != null) {
            							for (SupportingMember csm : clubbedSupportingMembers) {
            								Member clubbedQuestionSupportingMember = csm.getMember();
            								Status approvalStatus = csm.getDecisionStatus();
            								if(clubbedQuestionSupportingMember!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
            									if(clubbedQuestionSupportingMember.isSupportingOrClubbedMemberToBeAddedForDevice(question)) {
            										deviceOwner = clubbedQuestionSupportingMember;
            	    	    	                    break;
        										}								
            								}
            							}
            							if(deviceOwner!=null) {
            								break;
            							}
            						}
        						}    						
        					}
        	            }
        	        }
        	    }
        	}
    	}    	
    	return deviceOwner;
    }
    
    /**
    * Find supporting members.
    *
    * @param strQuestionId the str question id
    * @return the list
    */
    public static List<SupportingMember> findSupportingMembers(final String strQuestionId) {
    	Long questionId = Long.parseLong(strQuestionId);
    	Question question = findById(Question.class, questionId);
    	return question.getSupportingMembers();
    }

    
    /**
     * @param member
     * @param session
     * @param deviceType
     * @param locale
     * @return
     * @throws ELSException
     */
    public static Integer getMemberPutupCount(final Member member, final Session session,final DeviceType deviceType, final String locale) throws ELSException{
    	return Question.getQuestionRepository().getMemberPutupCount(member, session, deviceType, locale);
    }
    
    public Status findMemberStatus() {	
		Status memberStatus = null;
		try {		
			if(this.getStatus()!=null) {
				Status submitStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, this.getLocale());
				submitStatus = Question.findCorrespondingStatusForGivenQuestionType(submitStatus, this.getOriginalType());
				if(this.getStatus().getPriority()>=submitStatus.getPriority()) {
					memberStatus = submitStatus;
				} else {
					memberStatus = this.getStatus();
				}
			}
		} catch (ELSException e) {
			return null;
		}		
		
		return memberStatus;
	}
    
    /**** INTERNAL METHODS ****/
    /**
     * Gets the question repository.
     *
     * @return the question repository
     */
    private static QuestionRepository getQuestionRepository() {
        QuestionRepository questionRepository = new Question().questionRepository;
        if (questionRepository == null) {
            throw new IllegalStateException(
            	"QuestionRepository has not been injected in Question Domain");
        }
        return questionRepository;
    }
    
    /**
     * Adds the question draft for Memberside Submission.
     * Will be removed post archival of drafts so that performance issue is solved.
     */
    public void addQuestionDraftForMembersideSubmission() {
    	this.addQuestionDraft();
    }
    
    /**
     * Adds the question draft.
     */
    private void addQuestionDraft() {
    	String deviceType = this.getType().getType();
    	if(deviceType.equals(ApplicationConstants.STARRED_QUESTION)){
    		addStarredQuestionDraft();
    	}else if(deviceType.equals(ApplicationConstants.UNSTARRED_QUESTION)){
    		addUnstarredQuestionDraft();
    	}else if(deviceType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
    		addShortNoticeQuestionDraft();
    	}else if(deviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
    		addHalfHourFromQuestionDraft();
    	}
        
    }
    
    private void addHalfHourFromQuestionDraft() {
    	if(! this.getStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_INCOMPLETE) &&
        		! this.getStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_COMPLETE)) {
            QuestionDraft draft = new QuestionDraft();
            draft.setQuestionId(this.getId());
            draft.setLocale(this.getLocale());
            draft.setType(this.getType());
            draft.setAnsweringDate(this.getAnsweringDate());
            draft.setAnswer(this.getAnswer());
            draft.setRemarks(this.getRemarks());
            
            draft.setMlsBranchNotifiedOfTransfer(this.getMlsBranchNotifiedOfTransfer());
            draft.setTransferToDepartmentAccepted(this.getTransferToDepartmentAccepted());
            
            draft.setParent(this.getParent());
            draft.setClubbedEntities(this.getClubbedEntities());
            draft.setReferencedEntities(this.getReferencedEntities());
            
            draft.setEditedAs(this.getEditedAs());
            draft.setEditedBy(this.getEditedBy());
            draft.setEditedOn(this.getEditedOn());
            
            draft.setGroup(this.getGroup());
            draft.setMinistry(this.getMinistry());
            draft.setSubDepartment(this.getSubDepartment());
            
            draft.setStatus(this.getStatus());
            draft.setInternalStatus(this.getInternalStatus());
            draft.setRecommendationStatus(this.getRecommendationStatus());

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
                        
            if(this.getId() != null) {
                Question question = Question.findById(Question.class, this.getId());
                Set<QuestionDraft> originalDrafts = question.getDrafts();
                if(originalDrafts != null){
                    originalDrafts.add(draft);
                }
                else{
                    originalDrafts = new HashSet<QuestionDraft>();
                    originalDrafts.add(draft);
                }
                this.setDrafts(originalDrafts);
            }
            else {
                Set<QuestionDraft> originalDrafts = new HashSet<QuestionDraft>();
                originalDrafts.add(draft);
                this.setDrafts(originalDrafts);
            }
        }
		
	}


	private void addShortNoticeQuestionDraft() {
		 if(! this.getStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_INCOMPLETE) &&
	        		! this.getStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_COMPLETE)) {
	            QuestionDraft draft = new QuestionDraft();
	            draft.setQuestionId(this.getId());
	            draft.setLocale(this.getLocale());
	            draft.setType(this.getType());
	            draft.setAnsweringDate(this.getAnsweringDate());
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
	            draft.setSubDepartment(this.getSubDepartment());
	            
	            draft.setStatus(this.getStatus());
	            draft.setInternalStatus(this.getInternalStatus());
	            draft.setRecommendationStatus(this.getRecommendationStatus());
	              
	        	if(this.getRevisedReason() != null){
	    		    draft.setReason(this.getRevisedReason());
	            } 
	        	if(this.getRevisedQuestionText()!= null && this.getRevisedSubject() != null){
	                draft.setQuestionText(this.getRevisedQuestionText());
	                draft.setSubject(this.getRevisedSubject());                
	            }
	        	else if(this.getRevisedQuestionText() != null){
	            	draft.setQuestionText(this.getRevisedQuestionText());
	                draft.setSubject(this.getSubject());
	            }
	        	else if(this.getRevisedSubject()!=null){
	                draft.setQuestionText(this.getQuestionText());
	                draft.setSubject(this.getRevisedSubject());
	            }
	              
	            if(this.getId() != null) {
	                Question question = Question.findById(Question.class, this.getId());
	                Set<QuestionDraft> originalDrafts = question.getDrafts();
	                if(originalDrafts != null){
	                    originalDrafts.add(draft);
	                }
	                else{
	                    originalDrafts = new HashSet<QuestionDraft>();
	                    originalDrafts.add(draft);
	                }
	                this.setDrafts(originalDrafts);
	            }
	            else {
	                Set<QuestionDraft> originalDrafts = new HashSet<QuestionDraft>();
	                originalDrafts.add(draft);
	                this.setDrafts(originalDrafts);
	            }
	        }
		}



	private void addUnstarredQuestionDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_INCOMPLETE) &&
        		! this.getStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_COMPLETE)) {
            QuestionDraft draft = new QuestionDraft();
            draft.setQuestionId(this.getId());
            draft.setLocale(this.getLocale());
            draft.setType(this.getType());
            draft.setAnsweringDate(this.getAnsweringDate());
            draft.setAnswer(this.getAnswer());
            draft.setRemarks(this.getRemarks());
            
            draft.setMlsBranchNotifiedOfTransfer(this.getMlsBranchNotifiedOfTransfer());
            draft.setTransferToDepartmentAccepted(this.getTransferToDepartmentAccepted());
            
            draft.setParent(this.getParent());
            draft.setClubbedEntities(this.getClubbedEntities());
            draft.setReferencedEntities(this.getReferencedEntities());
            
            draft.setEditedAs(this.getEditedAs());
            draft.setEditedBy(this.getEditedBy());
            draft.setEditedOn(this.getEditedOn());
            
            draft.setGroup(this.getGroup());
            draft.setMinistry(this.getMinistry());
            draft.setSubDepartment(this.getSubDepartment());
            
            draft.setStatus(this.getStatus());
            draft.setInternalStatus(this.getInternalStatus());
            draft.setRecommendationStatus(this.getRecommendationStatus());
                        
        	if(this.getRevisedQuestionText()!= null && this.getRevisedSubject() != null){
                draft.setQuestionText(this.getRevisedQuestionText());
                draft.setSubject(this.getRevisedSubject());                
            }
        	else if(this.getRevisedQuestionText() != null){
            	draft.setQuestionText(this.getRevisedQuestionText());
                draft.setSubject(this.getSubject());
            }
        	else if(this.getRevisedSubject()!=null){
                draft.setQuestionText(this.getQuestionText());
                draft.setSubject(this.getRevisedSubject());
            }
        	else{
            	draft.setQuestionText(this.getQuestionText());
                draft.setSubject(this.getSubject());
            }
            
            if(this.getId() != null) {
            	/**** for submission draft avoid query for fetching drafts ****/
            	UserGroupType ugt = UserGroupType.findByName(UserGroupType.class, this.getEditedAs(), this.getLocale());
                if(ugt!=null && (ugt.getType().equalsIgnoreCase(ApplicationConstants.MEMBER) 
                		|| ugt.getType().equalsIgnoreCase(ApplicationConstants.TYPIST))) {
                	if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)) {
                		Set<QuestionDraft> originalDrafts = new HashSet<QuestionDraft>();
                		originalDrafts.add(draft);
                		this.setDrafts(originalDrafts);
                	}
                } else {
                	Question question = Question.findById(Question.class, this.getId());
                	Set<QuestionDraft> originalDrafts = question.getDrafts();
                    if(originalDrafts != null){
                        originalDrafts.add(draft);
                    }
                    else{
                        originalDrafts = new HashSet<QuestionDraft>();
                        originalDrafts.add(draft);
                    }
                    this.setDrafts(originalDrafts);
                }                
            }
            else {
                Set<QuestionDraft> originalDrafts = new HashSet<QuestionDraft>();
                originalDrafts.add(draft);
                this.setDrafts(originalDrafts);
            }
        }
		
	}


	private void addStarredQuestionDraft() {
	  if(! this.getStatus().getType().equals(ApplicationConstants.QUESTION_INCOMPLETE) &&
        		! this.getStatus().getType().equals(ApplicationConstants.QUESTION_COMPLETE)) {
            QuestionDraft draft = new QuestionDraft();
            draft.setQuestionId(this.getId());
            draft.setLocale(this.getLocale());
            draft.setType(this.getType());
            draft.setAnsweringDate(this.getAnsweringDate());
            draft.setAnswer(this.getAnswer());
            draft.setRemarks(this.getRemarks());
            
            draft.setMlsBranchNotifiedOfTransfer(this.getMlsBranchNotifiedOfTransfer());
            draft.setTransferToDepartmentAccepted(this.getTransferToDepartmentAccepted());
            
            draft.setParent(this.getParent());
            draft.setClubbedEntities(this.getClubbedEntities());
            draft.setReferencedEntities(this.getReferencedEntities());
            
            draft.setEditedAs(this.getEditedAs());
            draft.setEditedBy(this.getEditedBy());
            draft.setEditedOn(this.getEditedOn());
            
            draft.setGroup(this.getGroup());
            draft.setMinistry(this.getMinistry());
            draft.setSubDepartment(this.getSubDepartment());
            
            draft.setStatus(this.getStatus());
            draft.setInternalStatus(this.getInternalStatus());
            draft.setRecommendationStatus(this.getRecommendationStatus());
           
        	if(this.getRevisedQuestionText()!= null && this.getRevisedSubject() != null){
                draft.setQuestionText(this.getRevisedQuestionText());
                draft.setSubject(this.getRevisedSubject());                
            }
        	else if(this.getRevisedQuestionText() != null){
            	draft.setQuestionText(this.getRevisedQuestionText());
                draft.setSubject(this.getSubject());
            }
        	else if(this.getRevisedSubject()!=null){
                draft.setQuestionText(this.getQuestionText());
                draft.setSubject(this.getRevisedSubject());
            }
        	else{
            	draft.setQuestionText(this.getQuestionText());
                draft.setSubject(this.getSubject());
            }
        	
        	if(this.getChartAnsweringDate() != null){
        		draft.setChartAnsweringDate(this.getChartAnsweringDate());
        	}
        	
        	if(this.getRejectionReason() != null && !this.getRejectionReason().isEmpty()){
        		draft.setRejectionReason(this.getRejectionReason());
        	}
        	
        	draft.setPriority(this.getPriority());
        	
        	/**** for submission draft avoid query for fetching drafts ****/
        	UserGroupType ugt = UserGroupType.findByName(UserGroupType.class, this.getEditedAs(), this.getLocale());
            if(ugt!=null && (ugt.getType().equalsIgnoreCase(ApplicationConstants.MEMBER) 
            		|| ugt.getType().equalsIgnoreCase(ApplicationConstants.TYPIST))) {
            	if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)) {
            		Set<QuestionDraft> originalDrafts = new HashSet<QuestionDraft>();
            		originalDrafts.add(draft);
            		this.setDrafts(originalDrafts);
            	}
            } else {
            	if(this.getDrafts() != null){
            		Set<QuestionDraft> domainDrafts = this.getDrafts();
    	    		 Question question = Question.findById(Question.class, this.getId());
    	    		 Set<QuestionDraft> originalDrafts = question.getDrafts();
    	             if(originalDrafts != null){
    	                 originalDrafts.add(draft);
    	                 if(domainDrafts != null){
    	                 	originalDrafts.addAll(domainDrafts);
    	                 }
    	             }
    	             else{
    	                 originalDrafts = new HashSet<QuestionDraft>();
    	                 originalDrafts.add(draft);
    	                 if(domainDrafts != null){
    	                 	originalDrafts.addAll(domainDrafts);
    	                 }
    	             }                 
                     this.setDrafts(originalDrafts);
                     
            	}else if(this.getId() != null) {      		
            		Question question = Question.findById(Question.class, this.getId());
            		Set<QuestionDraft> originalDrafts = question.getDrafts();
                    if(originalDrafts != null){
                        originalDrafts.add(draft);
                    }
                    else{
                        originalDrafts = new HashSet<QuestionDraft>();
                        originalDrafts.add(draft);
                    }
                    this.setDrafts(originalDrafts);
                }else {
                	Set<QuestionDraft> originalDrafts = new HashSet<QuestionDraft>();
                    originalDrafts.add(draft);
                    this.setDrafts(originalDrafts);
                }
            }       	
        }
		
	}


	public static Integer getQuestionWithoutNumber(final Member member, final DeviceType deviceType, final Session session,String locale) throws ELSException{
    	return getQuestionRepository().getQuestionWithoutNumber(member, deviceType, session, locale);
    }

    public static List<Question> getRejectedQuestions(final Member member, final Session session, final DeviceType deviceType, final String locale) throws ELSException{
    	return getQuestionRepository().findRejectedQuestions(member, session, deviceType, locale);
    }
    
	
	public static String getRejectedQuestionsAsString(List<Question> questions, 
			final String locale) throws ELSException{
		return getQuestionRepository().findRejectedQuestionsAsString(questions, locale);
	}
    
    
    public static QuestionDraft getLatestQuestionDraftOfUser(Long questionId, String username) throws ELSException{
    	return getQuestionRepository().getLatestQuestionDraftOfUser(questionId, username);
    }
    
    public static List<Question> findAllByFile(final Session session,
			final DeviceType deviceType,final Group group, 
			final Integer file, 
			final String locale) throws ELSException {
		return getQuestionRepository().findAllByFile(session, deviceType,group, file, locale);
	}

	public static List<Question> findAllByStatus(final Session session,
			final DeviceType deviceType, 
			final Status internalStatus,
			final Group group,
			final SubDepartment subdepartment,
			final Integer itemsCount,
			final String locale) throws ELSException {
		return getQuestionRepository().findAllByStatus(session, deviceType, internalStatus, group, subdepartment, itemsCount, locale);
	}
	
	public static List<Question> findAllByRecommendationStatus(final Session session,
			final DeviceType deviceType, 
			final Status internalStatus,
			final Group group,
			final String locale) throws ELSException {
		return getQuestionRepository().findAllByRecommendationStatus(session, deviceType, internalStatus, group, locale);
	}
    
	
	
	public static List<Question> findByDeviceAndStatus(final DeviceType deviceType, final Status status){
		return getQuestionRepository().findByDeviceAndStatus(deviceType, status);
	}
	
	public static int findReadyToSubmitCount(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {
		return getQuestionRepository().findReadyToSubmitCount(session, primaryMember, deviceType, locale);
	}
	
	public static List<Question> findReadyToSubmitQuestions(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {
		return getQuestionRepository().findReadyToSubmitQuestions(session, primaryMember, deviceType, locale);
	}
	
	public static List<Question> findReadyToSubmitQuestions(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final Integer itemsCount,
			final String locale) {
		return getQuestionRepository().findReadyToSubmitQuestions(session, primaryMember, deviceType, itemsCount, locale);
	}
	
	public static List<Question> findAllByMember(final Session session,
			final Member primaryMember,final DeviceType questionType,final Integer itemsCount,
			final String locale) throws ELSException {
		return getQuestionRepository().findAllByMember(session,
				primaryMember,questionType,itemsCount,
				locale);
	}
	
	/*public static List<MasterVO> getMemberQuestionStatistics(final Member member, final Session session, final String locale){
		return getQuestionRepository().getMemberQuestionStatistics(member, session, locale);
	}*/
	
	 //todos 1
    public static List<Question> findAdmittedQuestionsOfGivenTypeWithoutListNumberInSession(final Long sessionId, final Long deviceTypeId) {
    	return getQuestionRepository().findAdmittedQuestionsOfGivenTypeWithoutListNumberInSession(sessionId, deviceTypeId);
    }
    
    public static Integer findHighestListNumberForAdmittedQuestionsOfGivenTypeInSession(final Long sessionId, final Long deviceTypeId) {
    	return getQuestionRepository().findHighestListNumberForAdmittedQuestionsOfGivenTypeInSession(sessionId, deviceTypeId);
    }
    
    public static Boolean isAdmittedQuestionOfGivenTypeWithListNumberInNextSessions(final Long sessionId, final String houseType, final Long deviceTypeId) {
    	return getQuestionRepository().isAdmittedQuestionOfGivenTypeWithListNumberInNextSessions(sessionId, houseType, deviceTypeId);
    }
	
	 public static Question getQuestion(final Long sessionId,final Long deviceTypeId, final Integer number,final String locale){
	    	return getQuestionRepository().getQuestion(sessionId, deviceTypeId,number, locale);
	 }
	 
	 public static Question getQuestion(final Long sessionId, final Integer number,final String locale){
	    	return getQuestionRepository().getQuestion(sessionId, number, locale);
	 }
	
	 public QuestionDraft findSecondPreviousDraft() {
		 Long id = this.getId();
		 return getQuestionRepository().findSecondPreviousDraft(id);
	 }
	 
	 public QuestionDraft findLatestPreviousGroupDraft() {
		 return getQuestionRepository().findLatestPreviousGroupDraft(this);
	 }
	 
	 public static MemberMinister findMemberMinisterIfExists(final Question question) throws ELSException {
		 return getQuestionRepository().findMemberMinisterIfExists(question);		 
	 }
	 
	 public static MemberMinister findMemberMinisterIfExists(final Question question, final Ministry ministry) throws ELSException {
		 return getQuestionRepository().findMemberMinisterIfExists(question, ministry);		 
	 }
	 
	 public static Boolean isExist(Integer number, DeviceType deviceType, Session session,String locale) {
		 return getQuestionRepository().isExist(number,deviceType,session,locale);
	 }
	 
	 public static QuestionDraft findPutupDraft(final Long id, final String putupStatus, final String putupActorUsergroupName) {
		 return getQuestionRepository().findPutupDraft(id, putupStatus, putupActorUsergroupName); 
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
				if(member!=null && approvalStatus!=null 
						&& approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if(memberName!=null && !memberName.isEmpty() 
							&& !allMemberNamesBuffer.toString().contains(memberName)) {
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
		/** clubbed questions members **/
		List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)) {
					member = ce.getQuestion().getPrimaryMember();
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
					List<SupportingMember> clubbedSupportingMembers = ce.getQuestion().getSupportingMembers();
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
		List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)) {
					member = ce.getQuestion().getPrimaryMember();
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
					List<SupportingMember> clubbedSupportingMembers = ce.getQuestion().getSupportingMembers();
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
		List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)) {
					member = ce.getQuestion().getPrimaryMember();
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
					List<SupportingMember> clubbedSupportingMembers = ce.getQuestion().getSupportingMembers();
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
	
	public static QuestionDates findQuestionDatesForStarredQuestion(final Question question) {
		if(question==null) {
			return null;
		}
		QuestionDates questionDates = null;
		Session session = question.getSession();
		DeviceType deviceType = question.getType();
		String processingMode = session.getParameter(deviceType.getType()+"_"+ApplicationConstants.PROCESSINGMODE);
		if(processingMode.equals(ApplicationConstants.UPPER_HOUSE)
				&& question.getStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
			String firstBatchStartDateParameter=question.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
			String firstBatchEndDateParameter=question.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
			if(firstBatchStartDateParameter!=null&&firstBatchEndDateParameter!=null){
				if((!firstBatchStartDateParameter.isEmpty())&&(!firstBatchEndDateParameter.isEmpty())){
					Date firstBatchStartDate = FormaterUtil.formatStringToDate(firstBatchStartDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					Date firstBatchEndDate = FormaterUtil.formatStringToDate(firstBatchEndDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					if(question.getSubmissionDate().compareTo(firstBatchStartDate)>=0
							|| question.getSubmissionDate().compareTo(firstBatchEndDate)<=0) {
						MemberBallotChoice mbc = MemberBallotChoice.findByFieldName(MemberBallotChoice.class, "question", question, question.getLocale());
						if(mbc!=null) {
							questionDates = mbc.getNewAnsweringDate();
						} else {
							questionDates = question.getChartAnsweringDate();
						}
					} else {
						/** 03-01-2017 may be hack and should be removed if found incorrect **/
						if(question.getAnsweringDate()!=null && question.getChartAnsweringDate()!=null) {
							if(question.getAnsweringDate().getAnsweringDate().after(question.getChartAnsweringDate().getAnsweringDate())) {
								questionDates = question.getAnsweringDate();
							} else {
								questionDates = question.getChartAnsweringDate();
							}
						} else {
							questionDates = question.getChartAnsweringDate();
						}						
						//questionDates = question.getChartAnsweringDate();
					}
				}
			}
		} else {
			/** 03-01-2017 may be hack and should be removed if found incorrect **/
			if(question.getAnsweringDate()!=null && question.getChartAnsweringDate()!=null) {
				if(question.getAnsweringDate().getAnsweringDate().after(question.getChartAnsweringDate().getAnsweringDate())) {
					questionDates = question.getAnsweringDate();
				} else {
					questionDates = question.getChartAnsweringDate();
				}
			} else {
				questionDates = question.getChartAnsweringDate();
			}						
			//questionDates = question.getChartAnsweringDate();
		}		
		return questionDates;
	}
	
	public QuestionDates findNextAnsweringDate() {
		return getQuestionRepository().findNextAnsweringDate(this);
	}
	
	public static boolean isAdmittedThroughClubbing(final Question question) {
		return getQuestionRepository().isAdmittedThroughClubbing(question);		
	}
	
	public static Integer findHighestYaadiNumber(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		return getQuestionRepository().findHighestYaadiNumber(deviceType, session, locale);
	}
	
	public static List<Question> findQuestionsInNumberedYaadi(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final Date yaadiLayingDate, final String locale) {
		return getQuestionRepository().findQuestionsInNumberedYaadi(deviceType, session, yaadiNumber, yaadiLayingDate, locale);
	}
	
	public static Date findYaadiLayingDateForYaadi(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		return getQuestionRepository().findYaadiLayingDateForYaadi(deviceType, session, yaadiNumber, locale);
	}
	
	public static List<Question> findQuestionsEligibleForNumberedYaadi(final DeviceType deviceType, final Session session, final Integer numberOfQuestionsSetInYaadi, final String locale) throws ELSException {
		return getQuestionRepository().findQuestionsEligibleForNumberedYaadi(deviceType, session, numberOfQuestionsSetInYaadi, locale);
	}
	
	public static Boolean isNumberedYaadiFinalized(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final Date yaadiLayingDate, final String locale) throws ELSException {
		return getQuestionRepository().isNumberedYaadiFinalized(deviceType, session, yaadiNumber, yaadiLayingDate, locale);
	}
	
	public static boolean isYaadiOfGivenNumberExistingInSession(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		return getQuestionRepository().isYaadiOfGivenNumberExistingInSession(deviceType, session, yaadiNumber, locale);
	}
	
	public static boolean isNumberedYaadiFilled(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		return getQuestionRepository().isNumberedYaadiFilled(deviceType, session, yaadiNumber, locale);
	}
	
	public static List<Date> findAvailableYaadiLayingDatesForSession(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		return getQuestionRepository().findAvailableYaadiLayingDatesForSession(deviceType, session, locale);
	}
	
	public Boolean checkWhetherIsRemovedFromYaadiDetails() throws ELSException {
		Boolean isRemovedFromYaadiDetails = false;
		List<YaadiDetails> yaadiDetailsList = YaadiDetails.findAll(this.getType(), this.getSession(), this.getLocale());
		if(yaadiDetailsList!=null && !yaadiDetailsList.isEmpty()) {			
			for(YaadiDetails yd: yaadiDetailsList) {
				List<Device> devicesRemovedFromYaadiDetails = yd.getRemovedDevices();
				if(devicesRemovedFromYaadiDetails!=null && !devicesRemovedFromYaadiDetails.isEmpty()) {
					for(Device d: devicesRemovedFromYaadiDetails) {
						if(d.getId().equals(this.getId())) {
							isRemovedFromYaadiDetails = true;
							break;
						}
					}
				}
				if(isRemovedFromYaadiDetails) {
					break;
				}
			}
		}
		return isRemovedFromYaadiDetails;
	}
	
	public static Question find(Session session, DeviceType deviceType,
			Integer qNumber, String locale) {
		return getQuestionRepository().find(session, deviceType, qNumber, locale);
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
	
	public DeviceType getOriginalType() {
		return originalType;
	}

	public void setOriginalType(DeviceType originalType) {
		this.originalType = originalType;
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
		
	public QuestionDates getAnsweringDate() {
		return answeringDate;
	}
		
	public void setAnsweringDate(QuestionDates answeringDate) {
		this.answeringDate = answeringDate;
	}
		
	public QuestionDates getChartAnsweringDate() {
		return chartAnsweringDate;
	}
		
	public void setChartAnsweringDate(QuestionDates chartAnsweringDate) {
		this.chartAnsweringDate = chartAnsweringDate;
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
		
	public String getQuestionreferenceText() {
		return questionreferenceText;
	}


	public void setQuestionreferenceText(String questionreferenceText) {
		this.questionreferenceText = questionreferenceText;
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
		
	public Set<QuestionDraft> getDrafts() {
		return drafts;
	}
	
	public void setDrafts(Set<QuestionDraft> drafts) {
		this.drafts = drafts;
	}	
	
	public Question getParent() {
		return parent;
	}	
	
	public void setParent(Question parent) {
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
	
	public Integer getYaadiNumber() {
		return yaadiNumber;
	}

	public void setYaadiNumber(Integer yaadiNumber) {
		this.yaadiNumber = yaadiNumber;
	}

	public Date getYaadiLayingDate() {
		return yaadiLayingDate;
	}

	public void setYaadiLayingDate(Date yaadiLayingDate) {
		this.yaadiLayingDate = yaadiLayingDate;
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
	
	public Question getHalfHourDiscusionFromQuestionReference() {
		return halfHourDiscusionFromQuestionReference;
	}
	
	public void setHalfHourDiscusionFromQuestionReference(
			Question halfHourDiscusionFromQuestionReference) {
		this.halfHourDiscusionFromQuestionReference = halfHourDiscusionFromQuestionReference;
	}
	
	
	
	public String getHalfHourDiscusionFromQuestionReferenceNumber() {
		return halfHourDiscusionFromQuestionReferenceNumber;
	}


	public void setHalfHourDiscusionFromQuestionReferenceNumber(
			String halfHourDiscusionFromQuestionReferenceNumber) {
		this.halfHourDiscusionFromQuestionReferenceNumber = halfHourDiscusionFromQuestionReferenceNumber;
	}


	public String getReferenceDeviceType() {
		return referenceDeviceType;
	}


	public void setReferenceDeviceType(String referenceDeviceType) {
		this.referenceDeviceType = referenceDeviceType;
	}


	public String getReferenceDeviceMember() {
		return referenceDeviceMember;
	}


	public void setReferenceDeviceMember(String referenceDeviceMember) {
		this.referenceDeviceMember = referenceDeviceMember;
	}


	public Date getReferenceDeviceAnswerDate() {
		return referenceDeviceAnswerDate;
	}


	public void setReferenceDeviceAnswerDate(Date referenceDeviceAnswerDate) {
		this.referenceDeviceAnswerDate = referenceDeviceAnswerDate;
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
	
	public Language getLanguage() {
		return language;
	}
	
	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public String getProspectiveClubbings() {
		return prospectiveClubbings;
	}
	
	public void setProspectiveClubbings(String prospectiveClubbings) {
		this.prospectiveClubbings = prospectiveClubbings;
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
	

	public String getAnswerReceivedMode() {
		return answerReceivedMode;
	}


	public void setAnswerReceivedMode(String answerReceivedMode) {
		this.answerReceivedMode = answerReceivedMode;
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


	/**
	 * @return the answeringAttemptsByDepartment
	 */
	public Integer getAnsweringAttemptsByDepartment() {
		return answeringAttemptsByDepartment;
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

	public String getReasonForLateReply() {
		return reasonForLateReply;
	}


	public void setReasonForLateReply(String reasonForLateReply) {
		this.reasonForLateReply = reasonForLateReply;
	}

	public String getQuestionsAskedInFactualPositionForMember() {
		return questionsAskedInFactualPositionForMember;
	}


	public void setQuestionsAskedInFactualPositionForMember(String questionsAskedInFactualPositionForMember) {
		this.questionsAskedInFactualPositionForMember = questionsAskedInFactualPositionForMember;
	}


	/****Starred atomic value ****/
	public static void updateStarredCurrentNumberLowerHouse(Integer num){
		synchronized (Question.STARRED_CUR_NUM_LOWER_HOUSE) {
			Question.STARRED_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getStarredCurrentNumberLowerHouse(){
		return Question.STARRED_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateStarredCurrentNumberUpperHouse(Integer num){
		synchronized (Question.STARRED_CUR_NUM_UPPER_HOUSE) {
			Question.STARRED_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getStarredCurrentNumberUpperHouse(){
		return Question.STARRED_CUR_NUM_UPPER_HOUSE;
	}
	
	
	/****UNStarred atomic value ****/
	public static void updateUnStarredCurrentNumberLowerHouse(Integer num){
		synchronized (Question.UNSTARRED_CUR_NUM_LOWER_HOUSE) {
			Question.UNSTARRED_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getUnStarredCurrentNumberLowerHouse(){
		return Question.UNSTARRED_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateUnStarredCurrentNumberUpperHouse(Integer num){
		synchronized (Question.UNSTARRED_CUR_NUM_UPPER_HOUSE) {
			Question.UNSTARRED_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getUnStarredCurrentNumberUpperHouse(){
		return Question.UNSTARRED_CUR_NUM_UPPER_HOUSE;
	}
	
	/****Shortnotice atomic value ****/
	public static void updateShortnoticeCurrentNumberLowerHouse(Integer num){
		synchronized (Question.SHORTNOTICE_CUR_NUM_LOWER_HOUSE) {
			Question.SHORTNOTICE_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getShortnoticeCurrentNumberLowerHouse(){
		return Question.SHORTNOTICE_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateShortnoticeCurrentNumberUpperHouse(Integer num){
		synchronized (Question.SHORTNOTICE_CUR_NUM_UPPER_HOUSE) {
			Question.SHORTNOTICE_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getShortnoticeCurrentNumberUpperHouse(){
		return Question.SHORTNOTICE_CUR_NUM_UPPER_HOUSE;
	}	
	
	/****Shortnotice atomic value ****/
	public static void updateHDQCurrentNumberLowerHouse(Integer num){
		synchronized (Question.HDQ_CUR_NUM_LOWER_HOUSE) {
			Question.HDQ_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getHDQCurrentNumberLowerHouse(){
		return Question.HDQ_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateHDQCurrentNumberUpperHouse(Integer num){
		synchronized (Question.HDQ_CUR_NUM_UPPER_HOUSE) {
			Question.HDQ_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getHDQCurrentNumberUpperHouse(){
		return Question.HDQ_CUR_NUM_UPPER_HOUSE;
	}
	/**
	 * @param answeringAttemptsByDepartment the answeringAttemptsByDepartment to set
	 */
	public void setAnsweringAttemptsByDepartment(
			Integer answeringAttemptsByDepartment) {
		this.answeringAttemptsByDepartment = answeringAttemptsByDepartment;
	}
	
	
	public String getFactualPositionFromMember() {
		return factualPositionFromMember;
	}


	public void setFactualPositionFromMember(String factualPositionFromMember) {
		this.factualPositionFromMember = factualPositionFromMember;
	}

	public Boolean getProcessed() {
		return processed;
	}


	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}


	public static List<ClubbedEntity> findClubbedEntitiesByChartAnsDateNumber(
			final Question question,final String locale) {
		return getQuestionRepository().findClubbedEntitiesByChartAnsDateNumber(
				question,locale);
	}

	public static List<Question> findBySessionNumber(final Session session, final Integer number, final String locale){
		return getQuestionRepository().findBySessionNumber(session, number, locale);
	}
	
	public boolean containsClubbingFromSecondBatch(final Session session,final Member member,
			String locale) throws ELSException {
		return getQuestionRepository().containsClubbingFromSecondBatch(session,member,this,
				locale);
	}
	
	public static List<QuestionSearchVO> searchByNumber(final Session session, final Integer number, final String locale){
		List<QuestionSearchVO> result = new ArrayList<QuestionSearchVO>();
		try{
			List<Question> data = Question.findBySessionNumber(session, number, locale);
			
			if(data != null){
				for(Question i : data){
					
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
					questionSearchVO.setSubDepartment(i.getSubDepartment().getName());
					
					questionSearchVO.setStatusType(i.getStatus().getType());
					
					questionSearchVO.setFormattedPrimaryMember(i.getPrimaryMember().getFullnameLastNameFirst());
					
					questionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(i.getChartAnsweringDate().getAnsweringDate(), ApplicationConstants.SERVER_DATEFORMAT, locale));
					
					result.add(questionSearchVO);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	//added by dhananjayb
	/**** Question Clubbing Begins ****/	
    public static boolean club(final Question q1,final Question q2,final String locale) throws ELSException{    	
    	boolean clubbingStatus = false;
    	boolean q1_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = false;
    	boolean q2_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = false;
    	String questionTypesAllowedForClubbingWithPreviousSessionUnstarred = "";
    	CustomParameter cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred = CustomParameter.findByName(CustomParameter.class, "QUESTIONTYPES_ALLOWED_FOR_CLUBBING_WITH_PREVIOUSSESSION_UNSTARRED", "");
    	if(cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred!=null
    			&& cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred.getValue()!=null) {
    		questionTypesAllowedForClubbingWithPreviousSessionUnstarred = cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred.getValue();
    	} else {
    		questionTypesAllowedForClubbingWithPreviousSessionUnstarred = ApplicationConstants.STARRED_QUESTION + "," + ApplicationConstants.UNSTARRED_QUESTION;
    	}
    	for(String eligibleQuestionType: questionTypesAllowedForClubbingWithPreviousSessionUnstarred.split(",")) {
    		if(q1.getType().getType().equals(eligibleQuestionType)) {
    			q1_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = true;    			
    		}
    		if(q2.getType().getType().equals(eligibleQuestionType)) {
    			q2_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = true;    			
    		}
    	}    	
    	try {    		
    		if(q1.getParent()!=null || q2.getParent()!=null) {
    			throw new ELSException("error", "QUESTION_ALREADY_CLUBBED");
    			
    		} 
    		//special case 1: q1 is eligible to be clubbed with q2 which is previous session unstarred
    		else if(q1_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred
    				&& q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
    				&& q2.getSession().getStartDate().before(q1.getSession().getStartDate())) {    				
    			
    			clubbingStatus = clubQuestionWithPreviousSessionUnstarredQuestion(q1, q2, locale);
    		} 
    		//special case 2: q2 is eligible to be clubbed with q1 which is previous session unstarred
    		else if(q2_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred
    				&& q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
    				&& q1.getSession().getStartDate().before(q2.getSession().getStartDate())) {    				
    			
    			clubbingStatus = clubQuestionWithPreviousSessionUnstarredQuestion(q2, q1, locale);
    		}
    		//==============================================================================
    		/**** All normal cases ****/
    		else {
    			if(q1.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
        				&& q2.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    				//both questions are starred
    				if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
    						&& q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    					clubbingStatus = clubStarredQuestions(q1, q2, locale);
    				} 
    				//either or both of questions are converted to unstarred and admitted
    				else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
    						|| q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					clubbingStatus = clubStarredConvertedToUnstarredQuestions(q1, q2, locale);
    				}    				
        		} else if(q1.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
        				&& q2.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    				clubbingStatus = clubUnstarredQuestions(q1, q2, locale);
        		} else if(q1.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
        				&& q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
        				&& q2.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    				clubbingStatus = clubUnstarredQuestions(q1, q2, locale);
        		} else if(q1.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
        				&& q2.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
        				&& q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    				clubbingStatus = clubUnstarredQuestions(q1, q2, locale);
        		} else if(q1.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
        				&& q2.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
        			//both questions are short notice
    				if(q1.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
    						&& q2.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    					clubbingStatus = clubShortNoticeQuestions(q1, q2, locale);
    				} 
    				//either or both of questions are converted to unstarred and admitted
    				else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
    						|| q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					clubbingStatus = clubShortNoticeConvertedToUnstarredQuestions(q1, q2, locale);
    				}        			
        		} else if(q1.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
        				&& q2.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
    				clubbingStatus = clubHDQ(q1, q2, locale);
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
    
    private static boolean clubQuestionWithPreviousSessionUnstarredQuestion(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	Status clubbingWithUnstarredFromPreviousSessionPutupStatus = null;
    	if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    		clubbingWithUnstarredFromPreviousSessionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION, locale);
    	} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    		clubbingWithUnstarredFromPreviousSessionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION, locale);
    	} else if(q1.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    		clubbingWithUnstarredFromPreviousSessionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION, locale);
    	}    	
    	WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
		if(q1_workflowDetails!=null) {
			WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
		}			
//		actualClubbingWithPreviousSessionUnstarredQuestion(q2, q1, q1.getInternalStatus(), clubbingWithUnstarredFromPreviousSessionPutupStatus, locale);
		Status admissionStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		if(q1.getInternalStatus().getPriority()<admissionStatus.getPriority()) {
			actualClubbingWithPreviousSessionUnstarredQuestion(q2, q1, clubbingWithUnstarredFromPreviousSessionPutupStatus, clubbingWithUnstarredFromPreviousSessionPutupStatus, locale);
		} else {
			actualClubbingWithPreviousSessionUnstarredQuestion(q2, q1, q1.getInternalStatus(), clubbingWithUnstarredFromPreviousSessionPutupStatus, locale);
		}
		clubbingStatus = true;
		return clubbingStatus;
    }
    
    @Transactional(noRollbackFor={OptimisticLockException.class})
    private static void actualClubbingWithPreviousSessionUnstarredQuestion(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent question are obtained.. also fetch latest question text from one of them
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}					
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}		
		
		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForQuestion = "";
				if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				} else if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				}
				
				if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question, pendingWorkflowTypeForQuestion);	
					WorkflowDetails.endProcess(wfDetails);
					question.removeExistingWorkflowAttributes();
					Status clubbingWithUnstarredFromPreviousSessionPutupStatus = null;
			    	if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			    		clubbingWithUnstarredFromPreviousSessionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION, locale);
			    	} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			    		clubbingWithUnstarredFromPreviousSessionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION, locale);
			    	} else if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			    		clubbingWithUnstarredFromPreviousSessionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION, locale);
			    	}
			    	Integer question_finalAdmissionStatusPriority = 0;				
					if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					
					} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					} else if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					}
					
					if(question.getStatus().getPriority().compareTo(question_finalAdmissionStatusPriority)<0) {
						question.setInternalStatus(clubbingWithUnstarredFromPreviousSessionPutupStatus);
						question.setRecommendationStatus(clubbingWithUnstarredFromPreviousSessionPutupStatus);
					} else {
						question.setRecommendationStatus(clubbingWithUnstarredFromPreviousSessionPutupStatus);
					}
				}
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setRevisedQuestionText(latestQuestionText);
				question.merge();
				parentClubbedEntities.add(k);
			}			
		}
		parent.setClubbedEntities(parentClubbedEntities);
		parent.simpleMerge();		

		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByChartAnsweringDateQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
    
    private static boolean clubStarredQuestions(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForStarred(q1, q2, locale);
    	if(clubbingStatus) {
    		String q1_sessionProcessingMode = q1.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
    		String q2_sessionProcessingMode = q2.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
    		
    		if(q1_sessionProcessingMode==null || q2_sessionProcessingMode==null) {
    			throw new ELSException("Question_clubStarredQuestions", "session device config parameter not set");
    		}
    		
    		if(q1_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE)
        			&& q2_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE)) {
    			
    			clubbingStatus = clubStarredQuestionsLH(q1, q2, locale);
    			
        	} else if(q1_sessionProcessingMode.equals(ApplicationConstants.UPPER_HOUSE)
        			&& q2_sessionProcessingMode.equals(ApplicationConstants.UPPER_HOUSE)) {
        		
        		clubbingStatus = clubStarredQuestionsUH(q1, q2, locale);
        	}
    	}    	 
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesForStarred(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    			if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}
    		if(q2.getAnswer()!=null && !q2.getAnswer().isEmpty()) {
    			WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    			if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}    		
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubStarredQuestionsLH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubStarredQuestionsBH(q1, q2, locale);
    	
    	if(!clubbingStatus) {
    		//=============cases specific to lowerhouse============//
        	/** get chart answering dates for questions **/
        	Date q1_chartAnsweringDate = q1.getChartAnsweringDate().getAnsweringDate();
        	Date q2_chartAnsweringDate = q2.getChartAnsweringDate().getAnsweringDate();
        	
        	Status yaadiLaidStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale);
        	
        	//Case 10: Both questions are admitted and balloted
        	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    				&& q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0
    				&& (q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    				&& (q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))) {
        		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
        		if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
        				clubbingStatus = true;
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        				clubbingStatus = true;
        			} else {
        				clubbingStatus = true;
        			}
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)<0) {
        			actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			clubbingStatus = true;
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)>0) {
        			actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			clubbingStatus = true;
        		}
        	}
    	}    	
    	
    	return clubbingStatus;
    }
    
    private static boolean clubStarredQuestionsUH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubStarredQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubStarredQuestionsBH(Question q1, Question q2, String locale) throws ELSException {
    	/** get chart answering dates for questions **/
    	Date q1_chartAnsweringDate = q1.getChartAnsweringDate().getAnsweringDate();
    	Date q2_chartAnsweringDate = q2.getChartAnsweringDate().getAnsweringDate();
    	
    	Status putupStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
    	
    	//Case 1: Both questions are just ready to be put up
    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) {
    		Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, locale);
    		if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {
    				actualClubbingStarredQuestions(q1, q2, clubbedStatus, clubbedStatus, locale);
    				return true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				actualClubbingStarredQuestions(q2, q1, clubbedStatus, clubbedStatus, locale);
    				return true;
    			} else {
    				return false;
    			}
    		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)<0) {
    			actualClubbingStarredQuestions(q1, q2, clubbedStatus, clubbedStatus, locale);
    			return true;
    		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)>0) {
    			actualClubbingStarredQuestions(q2, q1, clubbedStatus, clubbedStatus, locale);
    			return true;
    		} else {
    			return false;
    		}
    	} 
    	//Case 2A: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING, locale);
    		actualClubbingStarredQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 2B: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING, locale);
    		actualClubbingStarredQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 3: Both questions are pending in approval workflow
    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
    		if(q1_approvalLevel==q2_approvalLevel) {
    			if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				WorkflowDetails.endProcess(q2_workflowDetails);
        				q2.removeExistingWorkflowAttributes();
        				actualClubbingStarredQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
        				return true;
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				WorkflowDetails.endProcess(q1_workflowDetails);
        				q1.removeExistingWorkflowAttributes();
        				actualClubbingStarredQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
        				return true;
        			} else {
        				return false;
        			}
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)<0) {
        			WorkflowDetails.endProcess(q2_workflowDetails);;
        			q2.removeExistingWorkflowAttributes();
        			actualClubbingStarredQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
        			return true;
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)>0) {
        			WorkflowDetails.endProcess(q1_workflowDetails);
        			q1.removeExistingWorkflowAttributes();
        			actualClubbingStarredQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
        			return true;
        		} else {
        			return false;
        		}
    		} else if(q1_approvalLevel>q2_approvalLevel) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
    			actualClubbingStarredQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else if(q1_approvalLevel<q2_approvalLevel) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingStarredQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}    		
    	}
    	//Case 4A: One question is admitted but not balloted yet while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 4B: One question is admitted but not balloted yet while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5A: One question is admitted but not balloted yet while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);;
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingStarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5B: One question is admitted but not balloted yet while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingStarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 6: Both questions are admitted but not balloted
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    				&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
    			if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
        				return true;
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        				return true;
        			} else {
        				return false;
        			}
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)<0) {
        			actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)>0) {
        			actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
        		if(q1_approvalLevel==q2_approvalLevel) {
        			if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
            			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
            				WorkflowDetails.endProcess(q2_workflowDetails);;
            				q2.removeExistingWorkflowAttributes();
            				actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
            				return true;
            			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
            				WorkflowDetails.endProcess(q1_workflowDetails);
            				q1.removeExistingWorkflowAttributes();
            				actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
            				return true;
            			} else {
            				return false;
            			}
            		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)<0) {
            			WorkflowDetails.endProcess(q2_workflowDetails);;
            			q2.removeExistingWorkflowAttributes();
            			actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
            			return true;
            		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)>0) {
            			WorkflowDetails.endProcess(q1_workflowDetails);
            			q1.removeExistingWorkflowAttributes();
            			actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
            			return true;
            		} else {
            			return false;
            		}
        		} else if(q1_approvalLevel>q2_approvalLevel) {
        			WorkflowDetails.endProcess(q2_workflowDetails);;
        			q2.removeExistingWorkflowAttributes();
        			actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else if(q1_approvalLevel<q2_approvalLevel) {
        			WorkflowDetails.endProcess(q1_workflowDetails);
        			q1.removeExistingWorkflowAttributes();
        			actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
				actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
				return true;
    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}
    	} 
    	//Case 7A: One question is admitted & also balloted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 7B: One question is admitted & also balloted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 8A: One question is admitted & also balloted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    			&& q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)
				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingStarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 8B: One question is admitted & also balloted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingStarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 9A: One question is admitted & also balloted while other question is admitted but not balloted yet (clubbing post admission case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    			&& q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    			&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);
        		q2.removeExistingWorkflowAttributes();
    		}    		
    		actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		return true;
    	}
    	//Case 9B: One question is admitted & also balloted while other question is admitted but not balloted yet (clubbing post admission case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    			&& (q1.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		if(q1_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
        		q1.removeExistingWorkflowAttributes();
    		}    		
    		actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		return true;
    	}
    	else {
    		return false;
    	}    	
    }   
    
    @Transactional(noRollbackFor={OptimisticLockException.class})
    private static void actualClubbingStarredQuestions(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForQuestion = "";
				if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				}
				
				if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question, pendingWorkflowTypeForQuestion);	
					WorkflowDetails.endProcess(wfDetails);
					question.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Integer parent_finalAdmissionStatusPriority = 0;
					
					if(parent.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION , locale);
						parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					
					} else if(parent.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION , locale);
						parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					}
					
					Integer question_finalAdmissionStatusPriority = 0;
					
					if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					
					} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					}
					
					if(parent.getStatus().getPriority().compareTo(parent_finalAdmissionStatusPriority)<0) {
						Status putupForClubbingStatus = null;
						if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
							putupForClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING , locale);
						} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
							putupForClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING , locale);
						}						
						question.setInternalStatus(putupForClubbingStatus);
						question.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(question.getStatus().getPriority().compareTo(question_finalAdmissionStatusPriority)<0) {
							Status putupForNameClubbingStatus = null;
							if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
								putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING , locale);
							} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
								putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING , locale);
							}							
							question.setInternalStatus(putupForNameClubbingStatus);
							question.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = null;
							if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
								putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
							} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
								putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION , locale);
							}							
							question.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setRevisedQuestionText(latestQuestionText);
				question.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);				
			} 
			parent.merge();
		} else {
			parent.simpleMerge();
		}		

		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByChartAnsweringDateQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
    
    private static boolean clubStarredConvertedToUnstarredQuestions(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForStarredConvertedToUnstarred(q1, q2, locale);
    	if(clubbingStatus) {
    		String q1_sessionProcessingMode = q1.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
    		String q2_sessionProcessingMode = q2.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
    		
    		if(q1_sessionProcessingMode==null || q2_sessionProcessingMode==null) {
    			throw new ELSException("Question_clubStarredQuestions", "session device config parameter not set");
    		}
    		
    		if(q1_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE)
        			&& q2_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE)) {
    			
    			clubbingStatus = clubStarredConvertedToUnstarredQuestionsLH(q1, q2, locale);
    			
        	} else if(q1_sessionProcessingMode.equals(ApplicationConstants.UPPER_HOUSE)
        			&& q2_sessionProcessingMode.equals(ApplicationConstants.UPPER_HOUSE)) {
        		
        		clubbingStatus = clubStarredConvertedToUnstarredQuestionsUH(q1, q2, locale);
        	}
    	}    	 
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesForStarredConvertedToUnstarred(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    			if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}
    		if(q2.getAnswer()!=null && !q2.getAnswer().isEmpty()) {
    			WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    			if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}    		
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubStarredConvertedToUnstarredQuestionsLH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubStarredConvertedToUnstarredQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubStarredConvertedToUnstarredQuestionsUH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubStarredConvertedToUnstarredQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubStarredConvertedToUnstarredQuestionsBH(Question q1, Question q2, String locale) throws ELSException {
    	/** get chart answering dates for questions **/
    	Date q1_chartAnsweringDate = q1.getChartAnsweringDate().getAnsweringDate(); 
    	Date q2_chartAnsweringDate = q2.getChartAnsweringDate().getAnsweringDate();
    	
    	Status starred_putupStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
    	Status starred_approvalStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
    	
    	/*
    	 * Case 1A: One question is admitted (this must be converted to unstarred)
    	 * while other question is ready to be put up (this must be starred) (Nameclubbing Case)
    	 */
    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	} 
    	/*
    	 * Case 1B: One question is admitted (this must be converted to unstarred)
    	 * while other question is ready to be put up (this must be starred) (Nameclubbing Case) 
    	 */
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	/*
    	 * Case 2A: One question is admitted (this must be converted to unstarred) 
    	 * while other question is pending in approval workflow (this must be starred) (Nameclubbing Case) 
    	 */
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
    			&& q2.getInternalStatus().getPriority().compareTo(starred_putupStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(starred_approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);;
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	/*
    	 * Case 2B: One question is admitted (this must be converted to unstarred)
    	 * while other question is pending in approval workflow (this must be starred) (Nameclubbing Case) 
    	 */
    	else if(q1.getInternalStatus().getPriority().compareTo(starred_putupStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(starred_approvalStatus.getPriority()) < 0
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 3: Both questions are admitted but not balloted
    	else if((q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    					|| q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION))
    				&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
    				&& (q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    						|| q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION))
    				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))) {
    		Status starred_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		Status unstarred_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
    			if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				if(q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else {
        					return false;
        				}        				
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else {
        					return false;
        				}
        			} else {
        				return false;
        			}
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)<0) {
        			if(q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}
        		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)>0) {
        			if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
        		if(q1_approvalLevel==q2_approvalLevel) {
        			if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
            			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
            				WorkflowDetails.endProcess(q2_workflowDetails);;
            				q2.removeExistingWorkflowAttributes();
            				if(q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
            					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
            					return true;
            				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
            					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
            					return true;
            				} else {
            					return false;
            				}
            			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
            				WorkflowDetails.endProcess(q1_workflowDetails);
            				q1.removeExistingWorkflowAttributes();
            				if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
            					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
            					return true;
            				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
            					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
            					return true;
            				} else {
            					return false;
            				}
            			} else {
            				return false;
            			}
            		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)<0) {
            			WorkflowDetails.endProcess(q2_workflowDetails);;
            			q2.removeExistingWorkflowAttributes();
            			if(q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else {
        					return false;
        				}
            		} else if(q1_chartAnsweringDate.compareTo(q2_chartAnsweringDate)>0) {
            			WorkflowDetails.endProcess(q1_workflowDetails);
            			q1.removeExistingWorkflowAttributes();
            			if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
        					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else {
        					return false;
        				}
            		} else {
            			return false;
            		}
        		} else if(q1_approvalLevel>q2_approvalLevel) {
        			WorkflowDetails.endProcess(q2_workflowDetails);;
        			q2.removeExistingWorkflowAttributes();
        			if(q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}
        		} else if(q1_approvalLevel<q2_approvalLevel) {
        			WorkflowDetails.endProcess(q1_workflowDetails);
        			q1.removeExistingWorkflowAttributes();
        			if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
    			if(q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else {
					return false;
				}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else {
					return false;
				}
    		} else {
    			return false;
    		}
    	} 
    	//Case 4A: One question is admitted & also balloted while other question is converted to unstarred & admitted (clubbing post admission case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
    			&& q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);
        		q2.removeExistingWorkflowAttributes();
    		} 
    		Status starred_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		Status unstarred_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		if(q2.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
				return true;
			} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				actualClubbingStarredConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
				return true;
			} else {
				return false;
			}    		
    	}
    	//Case 4B: One question is admitted & also balloted while other question is converted to unstarred & admitted (clubbing post admission case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		if(q1_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
        		q1.removeExistingWorkflowAttributes();
    		} 
    		Status starred_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		Status unstarred_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		if(q1.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), starred_clubbbingPostAdmissionPutupStatus, locale);
				return true;
			} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				actualClubbingStarredConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
				return true;
			} else {
				return false;
			}
    	}
    	else {
    		return false;
    	}   	
    }
    
    private static void actualClubbingStarredConvertedToUnstarredQuestions(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForQuestion = "";
				if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				}
				
				if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question, pendingWorkflowTypeForQuestion);	
					WorkflowDetails.endProcess(wfDetails);
					question.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Integer question_finalAdmissionStatusPriority = 0;
					
					if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					
					} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					}
					
					if(question.getStatus().getPriority().compareTo(question_finalAdmissionStatusPriority)<0) {
						Status putupForNameClubbingStatus = null;
						if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
							putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING , locale);
						} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
							putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING , locale);
						}							
						question.setInternalStatus(putupForNameClubbingStatus);
						question.setRecommendationStatus(putupForNameClubbingStatus);
					} else {
						Status putupForClubbingPostAdmissionStatus = null;
						if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
							putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
						} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
							putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION , locale);
						}							
						question.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
					}
				}
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setRevisedQuestionText(latestQuestionText);
				question.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
					|| parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
			}
			parent.merge();
		} else {
			parent.simpleMerge();
		}

		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByChartAnsweringDateQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
    
    private static boolean clubUnstarredQuestions(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForUnstarred(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
    			clubbingStatus = clubUnstarredQuestionsLH(q1, q2, locale);
        	} else if(q1.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
        		clubbingStatus = clubUnstarredQuestionsUH(q1, q2, locale);
        	}
    	}    	 
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesForUnstarred(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    			if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    			if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}    		
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubUnstarredQuestionsLH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubUnstarredQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubUnstarredQuestionsUH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubUnstarredQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubUnstarredQuestionsBH(Question q1, Question q2, String locale) throws ELSException {
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale);
		//Case 1: Both questions are just ready to be put up
    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED)
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED, locale);
    		if(q1.getNumber().compareTo(q2.getNumber())<0) {
				actualClubbingUnstarredQuestions(q1, q2, clubbedStatus, clubbedStatus, locale);
				return true;
			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
				actualClubbingUnstarredQuestions(q2, q1, clubbedStatus, clubbedStatus, locale);
				return true;
			} else {
				return false;
			}
    	}
    	//Case 2A: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING, locale);
    		actualClubbingUnstarredQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 2B: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED)
    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING, locale);
    		actualClubbingUnstarredQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 3: Both questions are pending in approval workflow
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
    		if(q1_approvalLevel==q2_approvalLevel) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				WorkflowDetails.endProcess(q2_workflowDetails);;
    				q2.removeExistingWorkflowAttributes();
    				actualClubbingUnstarredQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
    				return true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				WorkflowDetails.endProcess(q1_workflowDetails);
    				q1.removeExistingWorkflowAttributes();
    				actualClubbingUnstarredQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    				return true;
    			} else {
    				return false;
    			}
    		} else if(q1_approvalLevel>q2_approvalLevel) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
    			actualClubbingUnstarredQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else if(q1_approvalLevel<q2_approvalLevel) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingUnstarredQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}    		
    	}
    	//Case 4A: One question is admitted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)				
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, locale);
    		actualClubbingUnstarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 4B: One question is admitted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, locale);
    		actualClubbingUnstarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5A: One question is admitted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);;
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingUnstarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5B: One question is admitted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingUnstarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 6: Both questions are admitted
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				actualClubbingUnstarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    				return true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				actualClubbingUnstarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    				return true;
    			} else {
    				return false;
    			}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
        		if(q1_approvalLevel==q2_approvalLevel) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				WorkflowDetails.endProcess(q2_workflowDetails);;
        				q2.removeExistingWorkflowAttributes();
        				actualClubbingUnstarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
        				return true;
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				WorkflowDetails.endProcess(q1_workflowDetails);
        				q1.removeExistingWorkflowAttributes();
        				actualClubbingUnstarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        				return true;
        			} else {
        				return false;
        			}
        		} else if(q1_approvalLevel>q2_approvalLevel) {
        			WorkflowDetails.endProcess(q2_workflowDetails);;
        			q2.removeExistingWorkflowAttributes();
        			actualClubbingUnstarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else if(q1_approvalLevel<q2_approvalLevel) {
        			WorkflowDetails.endProcess(q1_workflowDetails);
        			q1.removeExistingWorkflowAttributes();
        			actualClubbingUnstarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
				actualClubbingUnstarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
				return true;
    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingUnstarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}
    	}
    	else {
    		return false;
    	}
    }
    
    private static void actualClubbingUnstarredQuestions(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
    	/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForQuestion = "";
				if(question.getInternalStatus().getType().endsWith(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
						|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
						|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question, pendingWorkflowTypeForQuestion);	
					WorkflowDetails.endProcess(wfDetails);
					question.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION , locale);
					if(parent.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
						Status putupForClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING , locale);
						question.setInternalStatus(putupForClubbingStatus);
						question.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(question.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
							Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING , locale);
							question.setInternalStatus(putupForNameClubbingStatus);
							question.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION , locale);
							question.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				question.setType(parent.getType());
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setRevisedQuestionText(latestQuestionText);
				question.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
			}
			parent.merge();
		} else {
			parent.simpleMerge();
		}
		
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
    }
    
    @Transactional
    public static void actualClubbingStarredOriginalUnstarredQuestions(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child);
		if(wfDetails != null) {
			WorkflowDetails.endProcess(wfDetails);
		}
		child.removeExistingWorkflowAttributes();
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}
		
		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setType(parent.getType());
		child.setStatus(newInternalStatus);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
		updateDomainFieldsOnClubbingFinalisation(parent, child);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** end current clubbing workflow if pending **/
				wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question);
				if(wfDetails != null) {
					WorkflowDetails.endProcess(wfDetails);
				}
				question.removeExistingWorkflowAttributes();				
				
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setType(parent.getType());
				question.setStatus(newInternalStatus);
				question.setInternalStatus(newInternalStatus);
				question.setRecommendationStatus(newRecommendationStatus);
				question.setRevisedQuestionText(latestQuestionText);
				updateDomainFieldsOnClubbingFinalisation(parent, question);
				question.merge();
				parentClubbedEntities.add(k);
			}			
		}
		parent.setClubbedEntities(parentClubbedEntities);
		parent.simpleMerge();

		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
    
    private static boolean clubShortNoticeQuestions(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForShortNotice(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
    			clubbingStatus = clubShortNoticeQuestionsLH(q1, q2, locale);
        	} else if(q1.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
        		clubbingStatus = clubShortNoticeQuestionsUH(q1, q2, locale);
        	}
    	}    	 
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesForShortNotice(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    			if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    			if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}    		
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubShortNoticeQuestionsLH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubShortNoticeQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubShortNoticeQuestionsUH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubShortNoticeQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubShortNoticeQuestionsBH(Question q1, Question q2, String locale) throws ELSException {
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, locale);
		//Case 1: Both questions are just ready to be put up
    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED, locale);
    		if(q1.getNumber().compareTo(q2.getNumber())<0) {
				actualClubbingShortNoticeQuestions(q1, q2, clubbedStatus, clubbedStatus, locale);
				return true;
			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
				actualClubbingShortNoticeQuestions(q2, q1, clubbedStatus, clubbedStatus, locale);
				return true;
			} else {
				return false;
			}
    	}
    	//Case 2A: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING, locale);
    		actualClubbingShortNoticeQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 2B: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)
    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING, locale);
    		actualClubbingShortNoticeQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 3: Both questions are pending in approval workflow
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
    		if(q1_approvalLevel==q2_approvalLevel) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				WorkflowDetails.endProcess(q2_workflowDetails);;
    				q2.removeExistingWorkflowAttributes();
    				actualClubbingShortNoticeQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
    				return true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				WorkflowDetails.endProcess(q1_workflowDetails);
    				q1.removeExistingWorkflowAttributes();
    				actualClubbingShortNoticeQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    				return true;
    			} else {
    				return false;
    			}
    		} else if(q1_approvalLevel>q2_approvalLevel) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
    			actualClubbingShortNoticeQuestions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else if(q1_approvalLevel<q2_approvalLevel) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingShortNoticeQuestions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}    		
    	}
    	//Case 4A: One question is admitted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)				
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		actualClubbingShortNoticeQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 4B: One question is admitted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		actualClubbingShortNoticeQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5A: One question is admitted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);;
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingShortNoticeQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5B: One question is admitted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingShortNoticeQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 6: Both questions are admitted
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				actualClubbingShortNoticeQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    				return true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				actualClubbingShortNoticeQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    				return true;
    			} else {
    				return false;
    			}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
        		if(q1_approvalLevel==q2_approvalLevel) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				WorkflowDetails.endProcess(q2_workflowDetails);;
        				q2.removeExistingWorkflowAttributes();
        				actualClubbingShortNoticeQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
        				return true;
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				WorkflowDetails.endProcess(q1_workflowDetails);
        				q1.removeExistingWorkflowAttributes();
        				actualClubbingShortNoticeQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        				return true;
        			} else {
        				return false;
        			}
        		} else if(q1_approvalLevel>q2_approvalLevel) {
        			WorkflowDetails.endProcess(q2_workflowDetails);;
        			q2.removeExistingWorkflowAttributes();
        			actualClubbingShortNoticeQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else if(q1_approvalLevel<q2_approvalLevel) {
        			WorkflowDetails.endProcess(q1_workflowDetails);
        			q1.removeExistingWorkflowAttributes();
        			actualClubbingShortNoticeQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
				actualClubbingShortNoticeQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
				return true;
    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingShortNoticeQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}
    	}
    	else {
    		return false;
    	}
    }
    
    private static void actualClubbingShortNoticeQuestions(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
    	/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForQuestion = "";
				if(question.getInternalStatus().getType().endsWith(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING)
						|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_NAMECLUBBING)
						|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question, pendingWorkflowTypeForQuestion);	
					WorkflowDetails.endProcess(wfDetails);
					question.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION , locale);
					if(parent.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
						Status putupForClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING , locale);
						question.setInternalStatus(putupForClubbingStatus);
						question.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(question.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
							Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING , locale);
							question.setInternalStatus(putupForNameClubbingStatus);
							question.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION , locale);
							question.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setRevisedQuestionText(latestQuestionText);
				question.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
			}
			parent.merge();
		} else {
			parent.simpleMerge();
		}
		
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
    }
    
    private static boolean clubShortNoticeConvertedToUnstarredQuestions(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForShortNoticeConvertedToUnstarred(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
    			clubbingStatus = clubShortNoticeConvertedToUnstarredQuestionsLH(q1, q2, locale);
        	} else if(q1.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
        			&& q2.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
        		clubbingStatus = clubShortNoticeConvertedToUnstarredQuestionsUH(q1, q2, locale);
        	}
    	}    	 
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesForShortNoticeConvertedToUnstarred(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	if(clubbingStatus) {
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    			if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}
    		if(q1.getAnswer()!=null && !q1.getAnswer().isEmpty()) {
    			WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    			if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "QUESTION_ANSWERED_BUT_FLOW_PENDING");
    			}
    		}    		
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubShortNoticeConvertedToUnstarredQuestionsLH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubShortNoticeConvertedToUnstarredQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubShortNoticeConvertedToUnstarredQuestionsUH(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubShortNoticeConvertedToUnstarredQuestionsBH(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubShortNoticeConvertedToUnstarredQuestionsBH(Question q1, Question q2, String locale) throws ELSException {
    	
    	Status shortnotice_assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status shortnotice_approvalStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, locale);
		
		/*
    	 * Case 1A: One question is admitted but not balloted yet (this must be converted to unstarred) 
    	 * while other question is ready to be put up (this must be short notice) (Nameclubbing Case) 
    	 */
    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	} 
    	/*
    	 * Case 1B: One question is admitted but not balloted yet (this must be converted to unstarred) 
    	 * while other question is ready to be put up (this must be short notice) (Nameclubbing Case) 
    	 */
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	/*
    	 * Case 2A: One question is admitted but not balloted yet (this must be converted to unstarred) 
    	 * while other question is pending in approval workflow (this must be short notice) (Nameclubbing Case) 
    	 */
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
    			&& q2.getInternalStatus().getPriority().compareTo(shortnotice_assistantProcessedStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(shortnotice_approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);;
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	/*
    	 * Case 2B: One question is admitted but not balloted yet (this must be converted to unstarred) 
    	 * while other question is pending in approval workflow (this must be short notice) (Nameclubbing Case) 
    	 */
    	else if(q1.getInternalStatus().getPriority().compareTo(shortnotice_assistantProcessedStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(shortnotice_approvalStatus.getPriority()) < 0
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 3: Both questions are admitted but not balloted
    	else if((q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
    					|| q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION))
    				&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_BALLOTED))
    				&& (q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
    						|| q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION))
    				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_BALLOTED))) {
    		Status shortnotice_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		Status unstarred_clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				if(q2.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}        				
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				if(q1.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}
    			} else {
    				return false;
    			}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
        		if(q1_approvalLevel==q2_approvalLevel) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				WorkflowDetails.endProcess(q2_workflowDetails);;
        				q2.removeExistingWorkflowAttributes();
        				if(q2.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
        					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
        					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else {
        					return false;
        				}
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				WorkflowDetails.endProcess(q1_workflowDetails);
        				q1.removeExistingWorkflowAttributes();
        				if(q1.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
        					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
        					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
        					return true;
        				} else {
        					return false;
        				}
        			} else {
        				return false;
        			}
        		} else if(q1_approvalLevel>q2_approvalLevel) {
        			WorkflowDetails.endProcess(q2_workflowDetails);;
        			q2.removeExistingWorkflowAttributes();
        			if(q2.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}
        		} else if(q1_approvalLevel<q2_approvalLevel) {
        			WorkflowDetails.endProcess(q1_workflowDetails);
        			q1.removeExistingWorkflowAttributes();
        			if(q1.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
    					return true;
    				} else {
    					return false;
    				}
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
    			if(q2.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else if(q2.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					actualClubbingShortNoticeConvertedToUnstarredQuestions(q1, q2, q2.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else {
					return false;
				}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			if(q1.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), shortnotice_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else if(q1.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					actualClubbingShortNoticeConvertedToUnstarredQuestions(q2, q1, q1.getInternalStatus(), unstarred_clubbbingPostAdmissionPutupStatus, locale);
					return true;
				} else {
					return false;
				}
    		} else {
    			return false;
    		}
    	}    	
    	else {
    		return false;
    	}
    }
    
    private static void actualClubbingShortNoticeConvertedToUnstarredQuestions(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForQuestion = "";
				if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
							|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
					} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
							|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
						pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
					}
				}
				
				if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question, pendingWorkflowTypeForQuestion);	
					WorkflowDetails.endProcess(wfDetails);
					question.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Integer parent_finalAdmissionStatusPriority = 0;
					
					if(parent.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION , locale);
						parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					
					} else if(parent.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION , locale);
						parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					}
					
					Integer question_finalAdmissionStatusPriority = 0;
					
					if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					
					} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION , locale);
						question_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
					}
					
					if(parent.getStatus().getPriority().compareTo(parent_finalAdmissionStatusPriority)<0) {
						Status putupForClubbingStatus = null;
						if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
							putupForClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING , locale);
						} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
							putupForClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING , locale);
						}						
						question.setInternalStatus(putupForClubbingStatus);
						question.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(question.getStatus().getPriority().compareTo(question_finalAdmissionStatusPriority)<0) {
							Status putupForNameClubbingStatus = null;
							if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
								putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING , locale);
							} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
								putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING , locale);
							}							
							question.setInternalStatus(putupForNameClubbingStatus);
							question.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = null;
							if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
								putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION , locale);
							} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
								putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION , locale);
							}							
							question.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setRevisedQuestionText(latestQuestionText);
				question.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
					|| parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
			}
			parent.merge();
		} else {
			parent.simpleMerge();
		}

		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByChartAnsweringDateQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
    
    private static boolean clubHDQ(Question q1, Question q2, String locale) throws ELSException {
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
    
    private static boolean clubbingRulesForHDQ(Question q1, Question q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);    	
    	return clubbingStatus;    	
    }
    
    private static boolean clubHDQAssembly(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubHDQCommonForAssemblyAndCouncil(q1, q2, locale);
    	
    	if(!clubbingStatus) {
    		//=============cases specific to lowerhouse============//
        	Status yaadiLaidStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_YAADILAID, locale);
        	
        	//Case 10: Both questions are admitted and balloted
        	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    				&& q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0
    				&& (q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    				&& (q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))) {
        		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
        		if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    				clubbingStatus = true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    				clubbingStatus = true;
    			} else {
    				clubbingStatus = true;
    			}
        	}
    	}
    	
    	return clubbingStatus;
    }
    
    private static boolean clubHDQCouncil(Question q1, Question q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubHDQCommonForAssemblyAndCouncil(q1, q2, locale);
    	
    	return clubbingStatus;
    }
    
    private static boolean clubHDQCommonForAssemblyAndCouncil(Question q1, Question q2, String locale) throws ELSException {
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, locale);
		//Case 1: Both questions are just ready to be put up
    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED)
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED, locale);
    		if(q1.getNumber().compareTo(q2.getNumber())<0) {
				actualClubbingHDQ(q1, q2, clubbedStatus, clubbedStatus, locale);
				return true;
			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
				actualClubbingHDQ(q2, q1, clubbedStatus, clubbedStatus, locale);
				return true;
			} else {
				return false;
			}
    	}
    	//Case 2A: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING, locale);
    		actualClubbingHDQ(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 2B: One question is pending in approval workflow while other is ready to be put up
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED)
    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING, locale);
    		actualClubbingHDQ(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 3: Both questions are pending in approval workflow
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
    		if(q1_approvalLevel==q2_approvalLevel) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				WorkflowDetails.endProcess(q2_workflowDetails);;
    				q2.removeExistingWorkflowAttributes();
    				actualClubbingHDQ(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
    				return true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				WorkflowDetails.endProcess(q1_workflowDetails);
    				q1.removeExistingWorkflowAttributes();
    				actualClubbingHDQ(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    				return true;
    			} else {
    				return false;
    			}
    		} else if(q1_approvalLevel>q2_approvalLevel) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
    			actualClubbingHDQ(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else if(q1_approvalLevel<q2_approvalLevel) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingHDQ(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}    		
    	}
    	//Case 4A: One question is admitted but not balloted yet while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)				
    			&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingHDQ(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 4B: One question is admitted but not balloted yet while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingHDQ(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5A: One question is admitted but not balloted yet while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    			&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))
    			&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);;
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingHDQ(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 5B: One question is admitted but not balloted yet while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingHDQ(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 6: Both questions are admitted but not balloted yet
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    			&& (q1.getBallotStatus()==null || !q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))	
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
    			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
    				actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    				return true;
    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
    				actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    				return true;
    			} else {
    				return false;
    			}
    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
        		if(q1_approvalLevel==q2_approvalLevel) {
        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
        				WorkflowDetails.endProcess(q2_workflowDetails);;
        				q2.removeExistingWorkflowAttributes();
        				actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
        				return true;
        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
        				WorkflowDetails.endProcess(q1_workflowDetails);
        				q1.removeExistingWorkflowAttributes();
        				actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        				return true;
        			} else {
        				return false;
        			}
        		} else if(q1_approvalLevel>q2_approvalLevel) {
        			WorkflowDetails.endProcess(q2_workflowDetails);;
        			q2.removeExistingWorkflowAttributes();
        			actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else if(q1_approvalLevel<q2_approvalLevel) {
        			WorkflowDetails.endProcess(q1_workflowDetails);
        			q1.removeExistingWorkflowAttributes();
        			actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
        			return true;
        		} else {
        			return false;
        		}
    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);;
    			q2.removeExistingWorkflowAttributes();
				actualClubbingHDQ(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
				return true;
    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
    			q1.removeExistingWorkflowAttributes();
    			actualClubbingHDQ(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    			return true;
    		} else {
    			return false;
    		}
    	}
    	//Case 7A: One question is admitted & also balloted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_TO_BE_PUTUP)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 7B: One question is admitted & also balloted while other question is ready to be put up (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_TO_BE_PUTUP)
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		actualClubbingStarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 8A: One question is admitted & also balloted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    			&& q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED)
				&& q2.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		WorkflowDetails.endProcess(q2_workflowDetails);
    		q2.removeExistingWorkflowAttributes();
    		actualClubbingStarredQuestions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 8B: One question is admitted & also balloted while other question is pending in approval workflow (Nameclubbing Case)
    	else if(q1.getInternalStatus().getPriority().compareTo(assistantProcessedStatus.getPriority()) > 0
				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED)) {
    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		WorkflowDetails.endProcess(q1_workflowDetails);
    		q1.removeExistingWorkflowAttributes();
    		actualClubbingStarredQuestions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		return true;
    	}
    	//Case 9A: One question is admitted & also balloted while other question is admitted but not balloted yet (clubbing post admission case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    			&& q1.getBallotStatus()!=null && q1.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED)
				&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    			&& (q2.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
    		if(q2_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q2_workflowDetails);
        		q2.removeExistingWorkflowAttributes();
    		}    		
    		actualClubbingStarredQuestions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		return true;
    	}
    	//Case 9B: One question is admitted & also balloted while other question is admitted but not balloted yet (clubbing post admission case)
    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    			&& (q1.getBallotStatus()==null || !q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED))
    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& q2.getBallotStatus()!=null && q2.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED)) {
    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
    		if(q1_workflowDetails!=null) {
    			WorkflowDetails.endProcess(q1_workflowDetails);
        		q1.removeExistingWorkflowAttributes();
    		}    		
    		actualClubbingStarredQuestions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    private static void actualClubbingHDQ(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
    	/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		String latestQuestionText = null;
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					/** fetch parent's latest question text from first of its children **/
					if(latestQuestionText==null) {
						latestQuestionText = clubbedQn.getRevisedQuestionText();
						if(latestQuestionText==null || latestQuestionText.isEmpty()) {
							latestQuestionText = clubbedQn.getQuestionText();
						}
					}
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	
		
		/** fetch parent's latest question text **/
		if(latestQuestionText==null) {
			latestQuestionText = parent.getRevisedQuestionText();
			if(latestQuestionText==null || latestQuestionText.isEmpty()) {
				latestQuestionText = parent.getQuestionText();
			}
		}

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.setRevisedQuestionText(latestQuestionText);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();					
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForQuestion = "";
				if(question.getInternalStatus().getType().endsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING)
						|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_NAMECLUBBING)
						|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question, pendingWorkflowTypeForQuestion);	
					WorkflowDetails.endProcess(wfDetails);
					question.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION , locale);
					if(parent.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
						Status putupForClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING , locale);
						question.setInternalStatus(putupForClubbingStatus);
						question.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(question.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
							Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING , locale);
							question.setInternalStatus(putupForNameClubbingStatus);
							question.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
							question.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				question.setEditedAs(child.getEditedAs());
				question.setEditedBy(child.getEditedBy());
				question.setEditedOn(child.getEditedOn());
				question.setParent(parent);
				question.setRevisedQuestionText(latestQuestionText);
				question.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
			}
			parent.merge();
		} else {
			parent.simpleMerge();
		}
		
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
    }
    
    private static boolean clubbingRulesCommon(Question q1, Question q2, String locale) throws ELSException {
    	boolean isClubbingAllowedAsPerCommonRules = true;
    	if(!q1.getSession().equals(q2.getSession())) {
    		isClubbingAllowedAsPerCommonRules = false;
    		throw new ELSException("error", "QUESTIONS_FROM_DIFFERENT_SESSION");
    	}  
    	if(isClubbingAllowedAsPerCommonRules) {
    		if(!q1.getOriginalType().getType().equals(q2.getOriginalType().getType())) {
        		if(!q1.getType().getType().equals(q2.getType().getType())) {
        			isClubbingAllowedAsPerCommonRules = false;
        			throw new ELSException("error", "QUESTIONS_FROM_DIFFERENT_DEVICETYPE");
        		} else {   
        			//check if children of any of the questions are of different devicetype
        			boolean isChildFromDifferentDeviceType = false;
        			if(q1.getClubbedEntities()!=null && !q1.getClubbedEntities().isEmpty()) {
        				for(ClubbedEntity ce: q1.getClubbedEntities()) {
        					if(!ce.getQuestion().getType().getType().equals(q1.getType().getType())) {
        						isChildFromDifferentDeviceType = true;
        						break;
        					}
        				}
        			}
        			if(!isChildFromDifferentDeviceType) {
        				if(q2.getClubbedEntities()!=null && !q2.getClubbedEntities().isEmpty()) {
            				for(ClubbedEntity ce: q2.getClubbedEntities()) {
            					if(!ce.getQuestion().getType().getType().equals(q2.getType().getType())) {
            						isChildFromDifferentDeviceType = true;
            						break;
            					}
            				}
            			}
        			}
        			if(isChildFromDifferentDeviceType) {
        				isClubbingAllowedAsPerCommonRules = false;
        			} else {
        				isClubbingAllowedAsPerCommonRules = true;  
        			}    			 			
        		}
        	}
    	}
    	if(isClubbingAllowedAsPerCommonRules) {
    		if(!q1.getMinistry().getName().equals(q2.getMinistry().getName())) {
    			isClubbingAllowedAsPerCommonRules = false;
        		throw new ELSException("error", "QUESTIONS_FROM_DIFFERENT_MINISTRY");    		
        	}
    	}
    	if(isClubbingAllowedAsPerCommonRules) {
    		if(!q1.getSubDepartment().getName().equals(q2.getSubDepartment().getName())) {
    			isClubbingAllowedAsPerCommonRules = false;
    			throw new ELSException("error", "QUESTIONS_FROM_DIFFERENT_DEPARTMENT");    		
        	}
    	}    	  
    	return isClubbingAllowedAsPerCommonRules;
    }
    /**** Question Clubbing Ends ****/
    
    /**** Question Update Clubbing Starts ****/
    public static void updateClubbing(Question question) throws ELSException {
		//case 1: question is child
		if(question.getParent()!=null) {
			Question.updateClubbingForChild(question);
		} 
		//case 2: question is parent
		else if(question.getParent()==null && question.getClubbedEntities()!=null && !question.getClubbedEntities().isEmpty()) {
			Question.updateClubbingForParent(question);
		}
	}
    
    private static void updateClubbingForChild(Question question) throws ELSException {
    	/**** Special case for clubbing with previous session unstarred ****/
    	boolean isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = false;
    	String questionTypesAllowedForClubbingWithPreviousSessionUnstarred = "";
    	CustomParameter cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred = CustomParameter.findByName(CustomParameter.class, "QUESTIONTYPES_ALLOWED_FOR_CLUBBING_WITH_PREVIOUSSESSION_UNSTARRED", "");
    	if(cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred!=null
    			&& cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred.getValue()!=null) {
    		questionTypesAllowedForClubbingWithPreviousSessionUnstarred = cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred.getValue();
    	} else {
    		questionTypesAllowedForClubbingWithPreviousSessionUnstarred = ApplicationConstants.STARRED_QUESTION + "," + ApplicationConstants.UNSTARRED_QUESTION;
    	}
    	for(String eligibleQuestionType: questionTypesAllowedForClubbingWithPreviousSessionUnstarred.split(",")) {
    		if(question.getType().getType().equals(eligibleQuestionType)) {
    			isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = true;
    			break;
    		}    		
    	} 
		if(isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred
				&& question.getParent().getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				&& question.getParent().getSession().getStartDate().before(question.getSession().getStartDate())) {
			
			updateDomainFieldsOnClubbingFinalisation(question.getParent(), question);
			
			question.setStatus(question.getParent().getInternalStatus());
			question.setInternalStatus(question.getParent().getInternalStatus());
			question.setRecommendationStatus(question.getParent().getInternalStatus());

			question.setType(question.getParent().getType());
			
			question.simpleMerge();
			return;
		}
		/**** =============================================================== ****/
		/**** For all other cases ****/
    	if(question.getParent().getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
				& question.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			//both questions are starred
			if(question.getParent().getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
					&& question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				updateClubbingForChildStarredQuestion(question);
			} 
			//either or both of questions are converted to unstarred and admitted
			else if(question.getParent().getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
					|| question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				updateClubbingForChildStarredConvertedToUnstarredQuestion(question);
			}			
		} else if(question.getParent().getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				&& question.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			updateClubbingForChildUnStarredQuestion(question);
		} else if(question.getParent().getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				&& question.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
				&& question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			updateClubbingForChildUnStarredQuestion(question);
		} else if(question.getParent().getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
				&& question.getParent().getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				&& question.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			updateClubbingForChildUnStarredQuestion(question);
		} else if(question.getParent().getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
				&& question.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			//both questions are short notice
			if(question.getParent().getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
					&& question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				updateClubbingForChildShortNoticeQuestion(question);
			} 
			//either or both of questions are converted to unstarred and admitted
			else if(question.getParent().getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
					|| question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				updateClubbingForChildShortNoticeConvertedToUnstarredQuestion(question);
			}			
		} else if(question.getParent().getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
				&& question.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			updateClubbingForChildHDQ(question);
		}
	}
	
	private static void updateClubbingForChildStarredQuestion(Question question) throws ELSException {
		String locale = question.getLocale();
		Question parentQuestion = question.getParent();
		String question_sessionProcessingMode = question.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
		String parentQuestion_sessionProcessingMode = parentQuestion.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
		/** get chart answering dates for questions **/
    	Date pq_chartAnsweringDate = parentQuestion.getChartAnsweringDate().getAnsweringDate();
    	Date cq_chartAnsweringDate = question.getChartAnsweringDate().getAnsweringDate();
    	
    	Status putupStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, question.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, question.getLocale());
		
		if((question.getBallotStatus()==null || !question.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
				&& parentQuestion.getBallotStatus()!=null && parentQuestion.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
			
			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
			
			question.setStatus(parentQuestion.getInternalStatus());
			question.setInternalStatus(parentQuestion.getInternalStatus());
			question.setRecommendationStatus(parentQuestion.getInternalStatus());
			
			if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				question.setType(parentQuestion.getType());		
			}
			question.simpleMerge();
			return;
		}
		//all other cases
		if(pq_chartAnsweringDate.compareTo(cq_chartAnsweringDate)==0 || question.isFromDifferentBatch(parentQuestion)) {
			
			if(parentQuestion.getNumber().compareTo(question.getNumber())<0) {
				
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
				
				if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, question.getLocale());
					question.setInternalStatus(clubbedStatus);
					question.setRecommendationStatus(clubbedStatus);
				} else {
					question.setStatus(parentQuestion.getInternalStatus());
					question.setInternalStatus(parentQuestion.getInternalStatus());
					question.setRecommendationStatus(parentQuestion.getInternalStatus());
					if(question_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE) 
							&& question.getBallotStatus()!=null && question.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
						Ballot.remove(question, true);
						question.setBallotStatus(null);
					}
				}				
				if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					question.setType(parentQuestion.getType());					
				}
				question.simpleMerge();
				
			} else if(parentQuestion.getNumber().compareTo(question.getNumber())>0) {				
				
				WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
				if(parentQuestion_workflowDetails!=null) {
					WorkflowDetails.endProcess(parentQuestion_workflowDetails);
					parentQuestion.removeExistingWorkflowAttributes();
				}
				if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					question.setInternalStatus(putupStatus);
					question.setRecommendationStatus(putupStatus);
					//TODO:handle chart related changes if any for question now again ready to be put up
					
					updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
					
					Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, question.getLocale());
					actualClubbingStarredQuestions(question, parentQuestion, clubbedStatus, clubbedStatus, locale);
				} else {
					question.setStatus(parentQuestion.getInternalStatus());
					question.setInternalStatus(parentQuestion.getInternalStatus());
					if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
						Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
						question.setRecommendationStatus(admitDueToReverseClubbingStatus);
						Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
						WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
						//Send Notification of reverse clubbing
						NotificationController.sendReverseClubbingNotification(parentQuestion.getHouseType().getName(), parentQuestion.getNumber().toString(), question.getNumber().toString(), parentQuestion.getType().getName(), question.getType().getName(), parentQuestion.getSubDepartment().getName(), locale);
					} else {
						//TODO:handle case when parent is already rejected.. below is temporary fix
						//clarification from ketkip remaining
						question.setRecommendationStatus(parentQuestion.getInternalStatus());	
						
					}				
					updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
					if(parentQuestion_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE) 
							&& parentQuestion.getBallotStatus()!=null && parentQuestion.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
						Ballot.remove(parentQuestion, true);
						parentQuestion.setBallotStatus(null);
					}				
					actualClubbingStarredQuestions(question, parentQuestion, parentQuestion.getInternalStatus(), parentQuestion.getInternalStatus(), locale);
				}
			}
		} else {
			
			if(pq_chartAnsweringDate.compareTo(cq_chartAnsweringDate)<0) {
				
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
				
				if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, question.getLocale());
					question.setInternalStatus(clubbedStatus);
					question.setRecommendationStatus(clubbedStatus);
				} else {
					question.setStatus(parentQuestion.getInternalStatus());
					question.setInternalStatus(parentQuestion.getInternalStatus());
					question.setRecommendationStatus(parentQuestion.getInternalStatus());
					if(question_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE) 
							&& question.getBallotStatus()!=null && question.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
						Ballot.remove(question, true);
						question.setBallotStatus(null);
					}
				}				
				if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
					DeviceType originalDeviceType=DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, question.getLocale());
					DeviceType newDeviceType=DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, question.getLocale());					
					question.setOriginalType(originalDeviceType);
					question.setType(newDeviceType);					
				}
				question.simpleMerge();
				
			} else if(pq_chartAnsweringDate.compareTo(cq_chartAnsweringDate)>0) {			
				
				WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
				if(parentQuestion_workflowDetails!=null) {
					WorkflowDetails.endProcess(parentQuestion_workflowDetails);
					parentQuestion.removeExistingWorkflowAttributes();
				}
				if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						&& parentQuestion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					//TODO: handle this case when parent is starred & child is already converted to unstarred & now child becomes parent
					//clarification from ketkip remaining.. should starred child devicetype be changed to unstarred?
				}
				if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					question.setInternalStatus(putupStatus);
					question.setRecommendationStatus(putupStatus);
					//TODO:handle chart related changes if any for question question now again ready to be put up
					
					updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
					
					Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, question.getLocale());
					actualClubbingStarredQuestions(question, parentQuestion, clubbedStatus, clubbedStatus, locale);
				} else {
					question.setStatus(parentQuestion.getInternalStatus());
					question.setInternalStatus(parentQuestion.getInternalStatus());
					if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
						Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
						question.setRecommendationStatus(admitDueToReverseClubbingStatus);
						Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
						WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
						//Send Notification of reverse clubbing
						NotificationController.sendReverseClubbingNotification(parentQuestion.getHouseType().getName(), parentQuestion.getNumber().toString(), question.getNumber().toString(), parentQuestion.getType().getName(), question.getType().getName(), parentQuestion.getSubDepartment().getName(), locale);
					} else {
						//TODO:handle case when parent is already rejected.. below is temporary fix
						//clarification from ketkip remaining
						question.setRecommendationStatus(parentQuestion.getInternalStatus());	
						
					}					
					updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);		
					
					if(parentQuestion_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE)  
							&& parentQuestion.getBallotStatus()!=null && parentQuestion.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
						Ballot.remove(parentQuestion, true);
						parentQuestion.setBallotStatus(null);
					}
					actualClubbingStarredQuestions(question, parentQuestion, parentQuestion.getInternalStatus(), parentQuestion.getInternalStatus(), locale);
				}
			}
		}
	}
	
	private static void updateClubbingForChildStarredConvertedToUnstarredQuestion(Question question) throws ELSException {
		String locale = question.getLocale();
		Question parentQuestion = question.getParent();
		
		String question_sessionProcessingMode = question.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
		String parentQuestion_sessionProcessingMode = parentQuestion.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
		
		/** get chart answering dates for questions **/
		Date pq_chartAnsweringDate = parentQuestion.getChartAnsweringDate().getAnsweringDate();
    	Date cq_chartAnsweringDate = question.getChartAnsweringDate().getAnsweringDate();
    	
    	if((question.getBallotStatus()==null || !question.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED))
    			&& parentQuestion.getBallotStatus()!=null && parentQuestion.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
			
			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
			
			Status admittedStatus = null;
			if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				admittedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale);
			} else {
				admittedStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
			}
			
			question.setStatus(admittedStatus);
			question.setInternalStatus(admittedStatus);
			question.setRecommendationStatus(admittedStatus);
			
			if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				question.setType(parentQuestion.getType());		
			}
			question.simpleMerge();
			return;
		}
		//all other cases
    	if(pq_chartAnsweringDate.compareTo(cq_chartAnsweringDate)==0 || question.isFromDifferentBatch(parentQuestion)) {
    		
    		if(parentQuestion.getNumber().compareTo(question.getNumber())<0) {
    			
    			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
    			
    			question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				question.setRecommendationStatus(parentQuestion.getInternalStatus());
				
				if(question_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE) 
						&& question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
						&& question.getBallotStatus()!=null && question.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
					Ballot.remove(question, true);
					question.setBallotStatus(null);
				}
				
				if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					question.setType(parentQuestion.getType());					
				}
				
				question.simpleMerge();
    		
    		} else if(parentQuestion.getNumber().compareTo(question.getNumber())>0) {
    			
    			WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
				if(parentQuestion_workflowDetails!=null) {
					WorkflowDetails.endProcess(parentQuestion_workflowDetails);
					parentQuestion.removeExistingWorkflowAttributes();
				}
				/** bug fix **/				
				if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					question.setType(parentQuestion.getType());
					question.setStatus(parentQuestion.getInternalStatus());
					question.setInternalStatus(parentQuestion.getInternalStatus());
				} else {
					question.setStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));
					question.setInternalStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));
				}				
				if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
						|| parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
					
					Status admitDueToReverseClubbingStatus = null;
					if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
					
					} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
					}					
					question.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
					//Send Notification of reverse clubbing
					NotificationController.sendReverseClubbingNotification(parentQuestion.getHouseType().getName(), parentQuestion.getNumber().toString(), question.getNumber().toString(), parentQuestion.getType().getName(), question.getType().getName(), parentQuestion.getSubDepartment().getName(), locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					question.setRecommendationStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));
					
				}
				
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
				
				if(parentQuestion_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE) 
						&& parentQuestion.getType().equals(ApplicationConstants.STARRED_QUESTION)
						&& parentQuestion.getBallotStatus()!=null && parentQuestion.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
					Ballot.remove(parentQuestion, true);
					parentQuestion.setBallotStatus(null);
				}
				
				if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					parentQuestion.setType(question.getType());	
				}
				
				actualClubbingStarredConvertedToUnstarredQuestions(question, parentQuestion, question.getInternalStatus(), question.getInternalStatus(), locale);
    		}
    		
    	} else {
    		
    		if(pq_chartAnsweringDate.compareTo(cq_chartAnsweringDate)<0) {
    			
    			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
    			
    			question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				question.setRecommendationStatus(parentQuestion.getInternalStatus());
				
				if(question_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE) 
						&& question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
						&& question.getBallotStatus()!=null && question.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
					Ballot.remove(question, true);
					question.setBallotStatus(null);
				}
				
				if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					question.setType(parentQuestion.getType());					
				}
				
				question.simpleMerge();
    			
    		} else if(pq_chartAnsweringDate.compareTo(cq_chartAnsweringDate)>0) {
    			
    			WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
				if(parentQuestion_workflowDetails!=null) {
					WorkflowDetails.endProcess(parentQuestion_workflowDetails);
					parentQuestion.removeExistingWorkflowAttributes();
				}				
				/** bug fix **/				
				if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					question.setType(parentQuestion.getType());
					question.setStatus(parentQuestion.getInternalStatus());
					question.setInternalStatus(parentQuestion.getInternalStatus());
				} else {
					question.setStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));
					question.setInternalStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));
				}
				if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
						|| parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
					
					Status admitDueToReverseClubbingStatus = null;
					if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
					
					} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
					}					
					question.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
					//TODO: Notification of reverse clubbing
					
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					question.setRecommendationStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));	
					
				}
				
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
				
				if(parentQuestion_sessionProcessingMode.equals(ApplicationConstants.LOWER_HOUSE) 
						&& parentQuestion.getType().equals(ApplicationConstants.STARRED_QUESTION)
						&& parentQuestion.getBallotStatus()!=null && parentQuestion.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
					Ballot.remove(parentQuestion, true);
					parentQuestion.setBallotStatus(null);
				}
				
				if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					parentQuestion.setType(question.getType());
				}
				
				actualClubbingStarredConvertedToUnstarredQuestions(question, parentQuestion, question.getInternalStatus(), question.getInternalStatus(), locale);
    		}
    	}
	}
	
	private static void updateClubbingForChildUnStarredQuestion(Question question) throws ELSException {
		String locale = question.getLocale();
		Question parentQuestion = question.getParent();
		
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED, question.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, question.getLocale());
		
		if(parentQuestion.getNumber().compareTo(question.getNumber())<0) {
			
			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
			
			if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED, question.getLocale());
				question.setInternalStatus(clubbedStatus);
				question.setRecommendationStatus(clubbedStatus);
			} else {
				question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				question.setRecommendationStatus(parentQuestion.getInternalStatus());
			}				
			question.simpleMerge();
			
		} else if(parentQuestion.getNumber().compareTo(question.getNumber())>0) {				
			
			WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
			if(parentQuestion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentQuestion_workflowDetails);
				parentQuestion.removeExistingWorkflowAttributes();
			}
			if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
					&& parentQuestion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				//TODO: handle this case when parent is starred & child is already converted to unstarred & now child becomes parent
				//clarification from ketkip remaining.. should starred child devicetype be changed to unstarred?
			}
			if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				question.setInternalStatus(assistantProcessedStatus);
				question.setRecommendationStatus(assistantProcessedStatus);
				//TODO:handle chart related changes if any for question question now again ready to be put up
				
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
				
				Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED, question.getLocale());
				actualClubbingUnstarredQuestions(question, parentQuestion, clubbedStatus, clubbedStatus, locale);
			} else {
				question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
					Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
					question.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					question.setRecommendationStatus(parentQuestion.getInternalStatus());	
					
				}					
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);					
				
				actualClubbingUnstarredQuestions(question, parentQuestion, parentQuestion.getInternalStatus(), parentQuestion.getInternalStatus(), locale);
			}
		}
	}
	
	private static void updateClubbingForChildShortNoticeQuestion(Question question) throws ELSException {
		String locale = question.getLocale();
		Question parentQuestion = question.getParent();
		
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED, question.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, question.getLocale());
		
		if(parentQuestion.getNumber().compareTo(question.getNumber())<0) {
			
			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
			
			if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED, question.getLocale());
				question.setInternalStatus(clubbedStatus);
				question.setRecommendationStatus(clubbedStatus);
			} else {
				question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				question.setRecommendationStatus(parentQuestion.getInternalStatus());
			}	
			if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
				DeviceType originalDeviceType=DeviceType.findByType(ApplicationConstants.SHORT_NOTICE_QUESTION, question.getLocale());
				DeviceType newDeviceType=DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, question.getLocale());					
				question.setOriginalType(originalDeviceType);
				question.setType(newDeviceType);					
			}
			question.simpleMerge();
			
		} else if(parentQuestion.getNumber().compareTo(question.getNumber())>0) {				
			
			WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
			if(parentQuestion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentQuestion_workflowDetails);
				parentQuestion.removeExistingWorkflowAttributes();
			}
			if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
					&& parentQuestion.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				//TODO: handle this case when parent is starred & child is already converted to unstarred & now child becomes parent
				//clarification from ketkip remaining.. should starred child devicetype be changed to unstarred?
			}
			if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				question.setInternalStatus(assistantProcessedStatus);
				question.setRecommendationStatus(assistantProcessedStatus);
				//TODO:handle chart related changes if any for question question now again ready to be put up
				
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
				
				Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED, question.getLocale());
				actualClubbingStarredQuestions(question, parentQuestion, clubbedStatus, clubbedStatus, locale);
			} else {
				question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)) {
					Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
					question.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					question.setRecommendationStatus(parentQuestion.getInternalStatus());	
					
				}					
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);					
				
				actualClubbingStarredQuestions(question, parentQuestion, parentQuestion.getInternalStatus(), parentQuestion.getInternalStatus(), locale);
			}
		}
	}
	
	private static void updateClubbingForChildShortNoticeConvertedToUnstarredQuestion(Question question) throws ELSException {
		String locale = question.getLocale();
		Question parentQuestion = question.getParent();
		
		if(parentQuestion.getNumber().compareTo(question.getNumber())<0) {
			
			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
			
			question.setStatus(parentQuestion.getInternalStatus());
			question.setInternalStatus(parentQuestion.getInternalStatus());
			question.setRecommendationStatus(parentQuestion.getInternalStatus());
			
			if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				question.setType(parentQuestion.getType());					
			}
			
			question.simpleMerge();
		
		} else if(parentQuestion.getNumber().compareTo(question.getNumber())>0) {
			
			WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
			if(parentQuestion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentQuestion_workflowDetails);
				parentQuestion.removeExistingWorkflowAttributes();
			}			
			/** bug fix **/				
			if(parentQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				question.setType(parentQuestion.getType());
				question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
			} else {
				question.setStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));
				question.setInternalStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));
			}			
			if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
					|| parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)) {
				
				Status admitDueToReverseClubbingStatus = null;
				if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
				
				} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
				}					
				question.setRecommendationStatus(admitDueToReverseClubbingStatus);
				Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
				WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				
			} else {
				//TODO:handle case when parent is already rejected.. below is temporary fix
				//clarification from ketkip remaining
				question.setRecommendationStatus(Question.findCorrespondingStatusForGivenQuestionType(parentQuestion.getInternalStatus(), question.getType()));	
				
			}			
			
			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
			
			if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				parentQuestion.setType(question.getType());
			}
			
			actualClubbingShortNoticeConvertedToUnstarredQuestions(question, parentQuestion, question.getInternalStatus(), question.getInternalStatus(), locale);
		}
	}
	
	private static void updateClubbingForChildHDQ(Question question) throws ELSException {
		String locale = question.getLocale();
		Question parentQuestion = question.getParent();
		
    	Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED, question.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, question.getLocale());
		
		if(parentQuestion.getNumber().compareTo(question.getNumber())<0) {
			
			updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
			
			if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED, question.getLocale());
				question.setInternalStatus(clubbedStatus);
				question.setRecommendationStatus(clubbedStatus);
			} else {
				question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				question.setRecommendationStatus(parentQuestion.getInternalStatus());
			}				
			question.simpleMerge();
			
		} else if(parentQuestion.getNumber().compareTo(question.getNumber())>0) {				
			
			WorkflowDetails parentQuestion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentQuestion);
			if(parentQuestion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentQuestion_workflowDetails);
				parentQuestion.removeExistingWorkflowAttributes();
			}
			if(parentQuestion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				question.setInternalStatus(assistantProcessedStatus);
				question.setRecommendationStatus(assistantProcessedStatus);
				//TODO:handle chart related changes if any for question question now again ready to be put up
				
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
				
				Status clubbedStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED, question.getLocale());
				actualClubbingStarredQuestions(question, parentQuestion, clubbedStatus, clubbedStatus, locale);
			} else {
				question.setStatus(parentQuestion.getInternalStatus());
				question.setInternalStatus(parentQuestion.getInternalStatus());
				if(parentQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)) {
					Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, question.getLocale());
					question.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(question, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					question.setRecommendationStatus(parentQuestion.getInternalStatus());	
					
				}					
				updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);			
				
				actualClubbingStarredQuestions(question, parentQuestion, parentQuestion.getInternalStatus(), parentQuestion.getInternalStatus(), locale);
			}
		}
	}
	
	private static void updateClubbingForParent(Question question) {
		if(question.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				updateClubbingForParentStarredQuestion(question);
			} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				updateClubbingForParentStarredConvertedToUnstarredQuestion(question);
			}			
		} else if(question.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			updateClubbingForParentUnStarredQuestion(question);
		} else if(question.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				updateClubbingForParentShortNoticeQuestion(question);
			} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				updateClubbingForParentShortNoticeConvertedToUnstarredQuestion(question);
			}			
		} else if(question.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			updateClubbingForParentHDQ(question);
		}
	}

	private static void updateClubbingForParentStarredQuestion(Question question) {
		for(ClubbedEntity ce: question.getClubbedEntities()) {
			Question clubbedQuestion = ce.getQuestion();
			if(clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
					|| clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(question, clubbedQuestion);
				
				clubbedQuestion.setStatus(question.getInternalStatus());
				clubbedQuestion.setInternalStatus(question.getInternalStatus());
				clubbedQuestion.setRecommendationStatus(question.getInternalStatus());
				
				if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					clubbedQuestion.setType(question.getType());			
				}
				
				clubbedQuestion.merge();
			}
		}
	}
	
	private static void updateClubbingForParentStarredConvertedToUnstarredQuestion(Question question) {
		for(ClubbedEntity ce: question.getClubbedEntities()) {
			Question clubbedQuestion = ce.getQuestion();
			if(clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
					|| clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(question, clubbedQuestion);
				
				clubbedQuestion.setStatus(question.getInternalStatus());
				clubbedQuestion.setInternalStatus(question.getInternalStatus());
				clubbedQuestion.setRecommendationStatus(question.getInternalStatus());
				clubbedQuestion.setType(question.getType());		
				
				clubbedQuestion.merge();
			}
		}
	}
	
	private static void updateClubbingForParentUnStarredQuestion(Question question) {
		for(ClubbedEntity ce: question.getClubbedEntities()) {
			Question clubbedQuestion = ce.getQuestion();
			if(clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(question, clubbedQuestion);
				
				clubbedQuestion.setStatus(question.getInternalStatus());
				clubbedQuestion.setInternalStatus(question.getInternalStatus());
				clubbedQuestion.setRecommendationStatus(question.getInternalStatus());				
				
				clubbedQuestion.merge();
			}
		}
	}
	
	private static void updateClubbingForParentShortNoticeQuestion(Question question) {
		for(ClubbedEntity ce: question.getClubbedEntities()) {
			Question clubbedQuestion = ce.getQuestion();
			if(clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(question, clubbedQuestion);
				
				clubbedQuestion.setStatus(question.getInternalStatus());
				clubbedQuestion.setInternalStatus(question.getInternalStatus());
				clubbedQuestion.setRecommendationStatus(question.getInternalStatus());
				
				if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					clubbedQuestion.setType(question.getType());	
				}
				
				clubbedQuestion.merge();
			}
		}
	}
	
	private static void updateClubbingForParentShortNoticeConvertedToUnstarredQuestion(Question question) {
		for(ClubbedEntity ce: question.getClubbedEntities()) {
			Question clubbedQuestion = ce.getQuestion();
			if(clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)
					|| clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(question, clubbedQuestion);
				
				clubbedQuestion.setStatus(question.getInternalStatus());
				clubbedQuestion.setInternalStatus(question.getInternalStatus());
				clubbedQuestion.setRecommendationStatus(question.getInternalStatus());
				clubbedQuestion.setType(question.getType());		
				
				clubbedQuestion.merge();
			}
		}
	}
	
	private static void updateClubbingForParentHDQ(Question question) {
		for(ClubbedEntity ce: question.getClubbedEntities()) {
			Question clubbedQuestion = ce.getQuestion();
			if(clubbedQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(question, clubbedQuestion);
				
				clubbedQuestion.setStatus(question.getInternalStatus());
				clubbedQuestion.setInternalStatus(question.getInternalStatus());
				clubbedQuestion.setRecommendationStatus(question.getInternalStatus());
				
				clubbedQuestion.merge();
			}
		}
	}
	
	public static void updateDomainFieldsOnClubbingFinalisation(Question parent, Question child) {
    	if(parent.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
				& child.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
    		updateDomainFieldsOnClubbingFinalisationForStarredQuestion(parent, child);
		} else if(parent.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				& child.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			updateDomainFieldsOnClubbingFinalisationForUnStarredQuestion(parent, child);
		} else if(parent.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				& child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {		
			updateDomainFieldsOnClubbingFinalisationForUnStarredQuestion(parent, child);
		} else if(parent.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
				& child.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			updateDomainFieldsOnClubbingFinalisationForShortNoticeQuestion(parent, child);
		} else if(parent.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
				& child.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			updateDomainFieldsOnClubbingFinalisationForHDQ(parent, child);
		}
    }
    
    private static void updateDomainFieldsOnClubbingFinalisationForStarredQuestion(Question parent, Question child) {
    	updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
    }
    
	private static void updateDomainFieldsOnClubbingFinalisationForUnStarredQuestion(Question parent, Question child) {
		updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
	}
	
	private static void updateDomainFieldsOnClubbingFinalisationForShortNoticeQuestion(Question parent, Question child) {
		updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
		/** copy latest reason of parent to revised reason of child **/
		if(parent.getRevisedReason()!=null && !parent.getRevisedReason().isEmpty()) {
			child.setRevisedReason(parent.getRevisedReason());
		} else {
			child.setRevisedReason(parent.getReason());
		}		
	}
	
	private static void updateDomainFieldsOnClubbingFinalisationForHDQ(Question parent, Question child) {
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
	
	private static void updateDomainFieldsOnClubbingFinalisationCommon(Question parent, Question child) {
		/** copy latest subject of parent to revised subject of child **/
		if(parent.getRevisedSubject()!=null && !parent.getRevisedSubject().isEmpty()) {
			child.setRevisedSubject(parent.getRevisedSubject());
		} else {
			child.setRevisedSubject(parent.getSubject());
		}	
		/*** update revised question text accordingly ***/
		if(child.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_SYSTEM_CLUBBED)) {
			/** copy latest question text of parent to revised question text of child **/
			if(parent.getRevisedQuestionText()!=null && !parent.getRevisedQuestionText().isEmpty()) {
				child.setRevisedQuestionText(parent.getRevisedQuestionText());
			} else {
				child.setRevisedQuestionText(parent.getQuestionText());
			}
		} else {
			/** fetch child's latest question text **/
			String childQuestionText = child.getRevisedQuestionText();
			if(childQuestionText==null || childQuestionText.isEmpty()) {
				childQuestionText = child.getQuestionText();
			}
			/** copy latest question text of child to revised question text of parent and its other clubbed questions if any **/
			parent.setRevisedQuestionText(childQuestionText);
			parent.simpleMerge();
			for(ClubbedEntity ce: parent.getClubbedEntities()) {
				Question clubbedQuestion = ce.getQuestion();
				if(!clubbedQuestion.getId().equals(child.getId())) {
					clubbedQuestion.setRevisedQuestionText(childQuestionText);
					clubbedQuestion.simpleMerge();
				}
			}
		}		
		/** copy latest answer of parent to revised answer of child **/
		child.setAnswer(parent.getAnswer());
		/** copy latest rejection reason of parent to revised rejection reason of child **/
		child.setRejectionReason(parent.getRejectionReason());
	}
	/**** Question Update Clubbing Ends ****/
	
	/**** Question Unclubbing Begins ****/	
	public static boolean unclub(final Question q1, final Question q2, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(q1.getParent()==null && q2.getParent()==null) {
			throw new ELSException("error", "CLUBBED_QUESTION_NOT_FOUND");
		}
		if(q2.getParent()!=null && q2.getParent().equals(q1)) {
			clubbingStatus = actualUnclubbing(q1, q2, locale);
		} else if(q1.getParent()!=null && q1.getParent().equals(q2)) {
			clubbingStatus = actualUnclubbing(q2, q1, locale);
		} else {
			throw new ELSException("error", "NO_CLUBBING_BETWEEN_GIVEN_QUESTIONS");
		}
		return clubbingStatus;
	}
	
	public static boolean unclub(final Question question, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(question.getParent()==null) {
			throw new ELSException("error", "QUESTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(question.getParent(), question, locale);
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbing(final Question parent, final Question child, String locale) throws ELSException {
		boolean clubbingStatus = false;
		/**** handle unclubbing for clubbed with previous session unstarred ****/
		if(parent.getSession().getStartDate().before(child.getSession().getStartDate())
				&& parent.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			//child question is starred
			if(child.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				clubbingStatus = actualUnclubbingStarredQuestions(parent, child, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				clubbingStatus = actualUnclubbingUnStarredQuestions(parent, child, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				clubbingStatus = actualUnclubbingShortNoticeQuestions(parent, child, locale);
			}		
		} else {
			if(parent.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
					&& child.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				//both questions are starred
				if(parent.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
						&& child.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					clubbingStatus = actualUnclubbingStarredQuestions(parent, child, locale);
				} 
				//either or both of questions are converted to unstarred and admitted
				else if(parent.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						|| child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					clubbingStatus = actualUnclubbingStarredConvertedToUnstarredQuestions(parent, child, locale);
				}
			} else if(parent.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
					& child.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				clubbingStatus = actualUnclubbingUnStarredQuestions(parent, child, locale);
			} else if(parent.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
					&& child.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
					&& child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				clubbingStatus = actualUnclubbingUnStarredQuestions(parent, child, locale);
			} else if(parent.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)
					&& parent.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
					&& child.getOriginalType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				clubbingStatus = actualUnclubbingUnStarredQuestions(parent, child, locale);
			} else if(parent.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
					&& child.getOriginalType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				clubbingStatus = actualUnclubbingShortNoticeQuestions(parent, child, locale);
			} else if(parent.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
					&& child.getOriginalType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				clubbingStatus = actualUnclubbingHDQ(parent, child, locale);
			}
		}		
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbingStarredQuestions(final Question parent, final Question child, String locale) throws ELSException {
		/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_UNCLUBBING, locale);
			child.setRecommendationStatus(putupUnclubStatus);
			child.merge();
			return true;
		} else {
			/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedQuestions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			for(ClubbedEntity i:oldClubbedQuestions){
				if(! i.getQuestion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedQuestions.add(i);
					}else{
						newClubbedQuestions.add(i);                		
					}
				}else{
					found=true;
					position=i.getPosition();
					// clubbedEntityToRemove=i;
				}
			}
			if(!newClubbedQuestions.isEmpty()){
				parent.setClubbedEntities(newClubbedQuestions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			child.setRevisedQuestionText(child.restoreQuestionTextBeforeClubbing());
			/**break child's clubbing **/
			child.setParent(null);
			/** find & end current clubbing workflow of child if pending **/
			String pendingWorkflowTypeForQuestion = "";
			if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForQuestion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status & devicetype **/
			Status putupStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
			if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
				child.setInternalStatus(putupStatus);
				child.setRecommendationStatus(putupStatus);
				child.setType(child.getOriginalType());
			} else {
				if(child.getAnswer()==null || child.getAnswer().isEmpty()
						|| child.getAnswer().equals(parent.getAnswer())) {
					child.setStatus(admitStatus);
					child.setInternalStatus(admitStatus);
					child.setRecommendationStatus(admitStatus);
					if(child.getAnswer()!=null && child.getAnswer().equals(parent.getAnswer())) {
						child.setAnswer(null);
					}
					if(isOptimisticLockExceptionPossible) {
						Long child_currentVersion = child.getVersion();
						child_currentVersion++;
						child.setVersion(child_currentVersion);
					}
					Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
					UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
					WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
				} else {
					child.setStatus(admitStatus);
					child.setInternalStatus(admitStatus);
					Status answerReceivedStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_ANSWER_RECEIVED, locale);
					child.setRecommendationStatus(answerReceivedStatus);
				}
			}
		}	
		if(isOptimisticLockExceptionPossible) {
			Long child_currentVersion = child.getVersion();
			child_currentVersion++;
			child.setVersion(child_currentVersion);
		}		
		child.merge();
		return true;
	}
	
	public static boolean actualUnclubbingStarredConvertedToUnstarredQuestions(final Question parent, final Question child, String locale) throws ELSException {
		/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)
				|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = null;
			if(child.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_PUTUP_UNCLUBBING, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_UNCLUBBING, locale);
			}
			child.setRecommendationStatus(putupUnclubStatus);
			child.merge();
			return true;
		} else {
			/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedQuestions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			for(ClubbedEntity i:oldClubbedQuestions){
				if(! i.getQuestion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedQuestions.add(i);
					}else{
						newClubbedQuestions.add(i);                		
					}
				}else{
					found=true;
					position=i.getPosition();
					// clubbedEntityToRemove=i;
				}
			}
			if(!newClubbedQuestions.isEmpty()){
				parent.setClubbedEntities(newClubbedQuestions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			child.setRevisedQuestionText(child.restoreQuestionTextBeforeClubbing());
			/**break child's clubbing **/
			child.setParent(null);
			/** find & end current clubbing workflow of child if pending **/
			String pendingWorkflowTypeForQuestion = "";
			if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForQuestion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status **/
			Status putupStatus = null;
			if(child.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				putupStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				putupStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED, locale);
			}
			Status admitStatus = null;
			if(child.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				admitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				admitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale);
			}
			if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
				child.setInternalStatus(putupStatus);
				child.setRecommendationStatus(putupStatus);
			} else {
				if(child.getAnswer()==null || child.getAnswer().isEmpty()
						|| child.getAnswer().equals(parent.getAnswer())) {
					child.setInternalStatus(admitStatus);
					child.setRecommendationStatus(admitStatus);
					if(child.getAnswer()!=null && child.getAnswer().equals(parent.getAnswer())) {
						child.setAnswer(null);
					}
					Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
					UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
					WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
				} else {
					child.setInternalStatus(admitStatus);
					Status answerReceivedStatus = null;
					if(child.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						answerReceivedStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_ANSWER_RECEIVED, locale);
					} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						answerReceivedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_ANSWER_RECEIVED, locale);
					}
					child.setRecommendationStatus(answerReceivedStatus);
				}
			}
		}	
		if(isOptimisticLockExceptionPossible) {
			Long child_currentVersion = child.getVersion();
			child_currentVersion++;
			child.setVersion(child_currentVersion);
		}		
		child.merge();
		return true;
	}
	
	public static boolean actualUnclubbingUnStarredQuestions(final Question parent, final Question child, String locale) throws ELSException {
		/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_UNCLUBBING, locale);
			child.setRecommendationStatus(putupUnclubStatus);
			child.merge();
			return true;
		} else {
			/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedQuestions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			for(ClubbedEntity i:oldClubbedQuestions){
				if(! i.getQuestion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedQuestions.add(i);
					}else{
						newClubbedQuestions.add(i);                		
					}
				}else{
					found=true;
					position=i.getPosition();
					// clubbedEntityToRemove=i;
				}
			}
			if(!newClubbedQuestions.isEmpty()){
				parent.setClubbedEntities(newClubbedQuestions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			child.setRevisedQuestionText(child.restoreQuestionTextBeforeClubbing());
			/**break child's clubbing **/
			child.setParent(null);
			/** find & end current clubbing workflow of child if pending **/
			String pendingWorkflowTypeForQuestion = "";
			if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForQuestion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status **/
			Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale);
			if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
				child.setInternalStatus(assistantProcessedStatus);
				child.setRecommendationStatus(assistantProcessedStatus);
			} else {
				if(child.getAnswer()==null || child.getAnswer().isEmpty()
						|| child.getAnswer().equals(parent.getAnswer())) {
					child.setInternalStatus(admitStatus);
					child.setRecommendationStatus(admitStatus);
					if(child.getAnswer()!=null && child.getAnswer().equals(parent.getAnswer())) {
						child.setAnswer(null);
					}
					Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
					UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
					WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
				} else {
					child.setInternalStatus(admitStatus);
					Status answerReceivedStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_ANSWER_RECEIVED, locale);
					child.setRecommendationStatus(answerReceivedStatus);
				}
			}
		}	
		if(isOptimisticLockExceptionPossible) {
			Long child_currentVersion = child.getVersion();
			child_currentVersion++;
			child.setVersion(child_currentVersion);
		}
		child.merge();
		return true;
	}
	
	public static boolean actualUnclubbingShortNoticeQuestions(final Question parent, final Question child, String locale) throws ELSException {
		/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_UNCLUBBING, locale);
			child.setRecommendationStatus(putupUnclubStatus);
			child.merge();
			return true;
		} else {
			/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedQuestions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			for(ClubbedEntity i:oldClubbedQuestions){
				if(! i.getQuestion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedQuestions.add(i);
					}else{
						newClubbedQuestions.add(i);                		
					}
				}else{
					found=true;
					position=i.getPosition();
					// clubbedEntityToRemove=i;
				}
			}
			if(!newClubbedQuestions.isEmpty()){
				parent.setClubbedEntities(newClubbedQuestions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			child.setRevisedQuestionText(child.restoreQuestionTextBeforeClubbing());
			/**break child's clubbing **/
			child.setParent(null);
			/** find & end current clubbing workflow of child if pending **/
			String pendingWorkflowTypeForQuestion = "";
			if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForQuestion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status & device type **/
			Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, locale);
			if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
				child.setInternalStatus(assistantProcessedStatus);
				child.setRecommendationStatus(assistantProcessedStatus);
				if(child.getOriginalType()!=null) {
					child.setType(child.getOriginalType());
				}				
			} else {
				child.setStatus(admitStatus);
				child.setInternalStatus(admitStatus);
				child.setRecommendationStatus(admitStatus);
				if(child.getAnswer()!=null && child.getAnswer().equals(parent.getAnswer())) {
					child.setAnswer(null);
				}
				Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
				UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
				WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
			}
		}	
		if(isOptimisticLockExceptionPossible) {
			Long child_currentVersion = child.getVersion();
			child_currentVersion++;
			child.setVersion(child_currentVersion);
		}
		child.merge();
		return true;
	}
	
	public static boolean actualUnclubbingShortNoticeConvertedToUnstarredQuestions(final Question parent, final Question child, String locale) throws ELSException {
		/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)
				|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = null;
			if(child.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_UNCLUBBING, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_UNCLUBBING, locale);
			}
			child.setRecommendationStatus(putupUnclubStatus);
			child.merge();
			return true;
		} else {
			/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedQuestions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			for(ClubbedEntity i:oldClubbedQuestions){
				if(! i.getQuestion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedQuestions.add(i);
					}else{
						newClubbedQuestions.add(i);                		
					}
				}else{
					found=true;
					position=i.getPosition();
					// clubbedEntityToRemove=i;
				}
			}
			if(!newClubbedQuestions.isEmpty()){
				parent.setClubbedEntities(newClubbedQuestions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			child.setRevisedQuestionText(child.restoreQuestionTextBeforeClubbing());
			/**break child's clubbing **/
			child.setParent(null);
			/** find & end current clubbing workflow of child if pending **/
			String pendingWorkflowTypeForQuestion = "";
			if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForQuestion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status **/
			Status assistantProcessedStatus = null;
			if(child.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED, locale);
			}
			Status admitStatus = null;
			if(child.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				admitStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, locale);
			} else if(child.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				admitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale);
			}
			if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
				child.setInternalStatus(assistantProcessedStatus);
				child.setRecommendationStatus(assistantProcessedStatus);
			} else {
				child.setInternalStatus(admitStatus);
				child.setRecommendationStatus(admitStatus);
				if(child.getAnswer()!=null && child.getAnswer().equals(parent.getAnswer())) {
					child.setAnswer(null);
				}
				Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
				UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
				WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
			}
		}	
		if(isOptimisticLockExceptionPossible) {
			Long child_currentVersion = child.getVersion();
			child_currentVersion++;
			child.setVersion(child_currentVersion);
		}		
		child.merge();
		return true;
	}
	
	public static boolean actualUnclubbingHDQ(final Question parent, final Question child, String locale) throws ELSException {
		/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_UNCLUBBING, locale);
			child.setRecommendationStatus(putupUnclubStatus);
			child.merge();
			return true;
		} else {
			/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedQuestions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			for(ClubbedEntity i:oldClubbedQuestions){
				if(! i.getQuestion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedQuestions.add(i);
					}else{
						newClubbedQuestions.add(i);                		
					}
				}else{
					found=true;
					position=i.getPosition();
					// clubbedEntityToRemove=i;
				}
			}
			if(!newClubbedQuestions.isEmpty()){
				parent.setClubbedEntities(newClubbedQuestions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			child.setRevisedQuestionText(child.restoreQuestionTextBeforeClubbing());
			/**break child's clubbing **/
			child.setParent(null);
			/** find & end current clubbing workflow of child if pending **/
			String pendingWorkflowTypeForQuestion = "";
			if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForQuestion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForQuestion!=null && !pendingWorkflowTypeForQuestion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForQuestion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status **/
			Status assistantProcessedStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			child.setInternalStatus(assistantProcessedStatus);
			child.setRecommendationStatus(assistantProcessedStatus);
		}	
		if(isOptimisticLockExceptionPossible) {
			Long child_currentVersion = child.getVersion();
			child_currentVersion++;
			child.setVersion(child_currentVersion);
		}
		child.merge();
		return true;
	}
	/**** Question Unclubbing Ends ****/
	
	public Boolean isFromDifferentBatch(Question q) {
		Boolean isFromDifferentBatch = false;
		if(q!=null && this.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
				&& q.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
				&& this.getSession().getId().equals(q.getSession().getId())) {
			String firstBatchStartDateParameter=this.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
			String firstBatchEndDateParameter=this.getSession().getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
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
    //-----------------------------------------------------
	
    public static enum CLUBBING_STATE {
    	STANDALONE, 
    	PARENT, 
    	CLUBBED
    }
    
    public static enum STARRED_STATE {
    	PRE_CHART, 
    	ON_CHART, 
    	IN_WORKFLOW_AND_PRE_FINAL,
    	POST_FINAL_AND_PRE_BALLOT,
    	POST_BALLOT_AND_PRE_YAADI_LAID,
    	YAADI_LAID
    }
    
    public static enum UNSTARRED_STATE {
    	PRE_WORKFLOW,
    	IN_WORKFLOW_AND_PRE_FINAL,
    	POST_FINAL_AND_PRE_YAADI_LAID,
    	YAADI_LAID
    }
    
    public static enum SHORT_NOTICE_STATE {
    	PRE_WORKFLOW,
    	IN_WORKFLOW_AND_PRE_FINAL,
    	POST_FINAL_AND_PRE_DATE_SUBMISSION,
    	POST_DATE_SUBMISSION_AND_PRE_DATE_ADMISSION,
    	FINAL_DATE_ADMISSION
    }
    
    public static enum HALF_HOUR_STATE {
    	PRE_WORKFLOW,
    	IN_WORKFLOW_AND_PRE_FINAL,
    	POST_FINAL_AND_PRE_BALLOT,
    	POST_BALLOT
    }
    
    /**
     * Determine if @param question is STANDALONE, PARENT, or CLUBBED.
     */
    public static CLUBBING_STATE findClubbingState(final Question question) {
    	if(question.getParent() != null) {
    		return Question.CLUBBING_STATE.CLUBBED;
    	}
    	else {
    		List<ClubbedEntity> clubbings = 
    			Question.findClubbedEntitiesByPosition(question);
    		if(clubbings.isEmpty()) {
    			return Question.CLUBBING_STATE.STANDALONE;
    		}
    		else {
    			return Question.CLUBBING_STATE.PARENT;
    		}
    	}
    }
    
    /**
     * Note that the statuses:
     * 	QUESTION_FINAL_CLARIFICATION_FROM_MEMBER,
     * 	QUESTION_FINAL_CLARIFICATION_FROM_DEPARTMENT,
     * 	QUESTION_FINAL_CLARIFICATION_FROM_GOVT,
     * 	QUESTION_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT
     * are final statuses of a workflow but are not the ultimate
     * status of @param question.
     * 
     * Admission & Rejection are the only ultimate (internalStatus) 
     * states of a Question, rest all are intermediate states.
     * 
     * IMP Note: The implementation is written keeping a standalone
     * or parent question in mind. For Clubbed questions add the
     * necessary cases.
     */
    private static STARRED_STATE findStarredState(
    		final Question question) throws ELSException {
    	Status internalStatus = question.getInternalStatus();
    	String internalStatusType = internalStatus.getType();
    	
    	Status ballotStatus = question.getBallotStatus();
    	
    	if(internalStatusType.equals(
    			ApplicationConstants.QUESTION_SUBMIT) 
    			|| internalStatusType.equals(
    					ApplicationConstants
    						.QUESTION_SYSTEM_ASSISTANT_PROCESSED)) {
    		return STARRED_STATE.PRE_CHART;
    	}
    	else if(internalStatusType.equals(
    			ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) {
    		return STARRED_STATE.ON_CHART;
    	}
    	else if(internalStatusType.equals(
    			ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)
    			|| internalStatusType.equals(
    	    			ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)) {
    		return STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL;
    	}
    	else if(ballotStatus == null
    			&& (internalStatusType.equals(
    					ApplicationConstants.QUESTION_FINAL_ADMISSION)
    					|| internalStatusType.equals(
    							ApplicationConstants
    							.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
    					|| internalStatusType.equals(
    							ApplicationConstants
    								.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
    					|| internalStatusType.equals(
    							ApplicationConstants
    								.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
    					|| internalStatusType.equals(
    							ApplicationConstants
    								.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT))
    					|| internalStatusType.equals(ApplicationConstants
    								.QUESTION_FINAL_REJECTION)) {
    		return STARRED_STATE.POST_FINAL_AND_PRE_BALLOT;

    	}
    	else if(ballotStatus != null) {
    		String ballotStatusType = ballotStatus.getType();
    		
    		Status recommendationStatus = question.getRecommendationStatus();
    		Integer recommendationStatusPriority = 
    			recommendationStatus.getPriority();
    		
    		String locale = question.getLocale();
    		Status YAADI_LAID = Status.findByType(
    				ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale);
    		Integer yaadiLaidPriority = YAADI_LAID.getPriority();
    		
    		if(ballotStatusType.equals(
    				ApplicationConstants.QUESTION_PROCESSED_BALLOTED)
    			&& recommendationStatusPriority.compareTo(
    					yaadiLaidPriority) < 0) {
    			return STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID;
    		}
    		else {
    			return STARRED_STATE.YAADI_LAID;
    		}
    	}
    	else {
    		throw new ELSException("Question.findStarredState/1", 
				"Unhandled status type.");
    	}
		
    }
    
    /**
     * Note that the statuses:
     * 	QUESTION_UNSTARRED_FINAL_CLARIFICATION_FROM_MEMBER,
     * 	QUESTION_UNSTARRED_FINAL_CLARIFICATION_FROM_DEPARTMENT,
     * 	QUESTION_UNSTARRED_FINAL_CLARIFICATION_FROM_GOVT,
     * 	QUESTION_UNSTARRED_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT
     * are final statuses of a workflow but are not the ultimate
     * status of @param question.
     * 
     * Admission & Rejection are the only ultimate (internalStatus) 
     * states of a Question, rest all are intermediate states.
     * 
     * IMP Note: The implementation is written keeping a standalone
     * or parent question in mind. For Clubbed questions add the
     * necessary cases.
     */
    private static UNSTARRED_STATE findUnstarredState(
    		final Question question) throws ELSException {
    	Status internalStatus = question.getInternalStatus();
    	String internalStatusType = internalStatus.getType();
    	
    	if(internalStatusType.equals(
    			ApplicationConstants.QUESTION_UNSTARRED_SUBMIT) 
    			|| internalStatusType.equals(
    					ApplicationConstants
    						.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED)) {
    		return UNSTARRED_STATE.PRE_WORKFLOW;
    	}
    	else if(internalStatusType.equals(
    			ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMISSION)
    			|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_UNSTARRED_RECOMMEND_REJECTION)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_GOVT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
    	    	|| internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT)) {
    		return UNSTARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL;
    	}
    	else if(internalStatusType.equals(
				ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				|| internalStatusType.equals(
						ApplicationConstants
							.QUESTION_UNSTARRED_FINAL_REJECTION)) {
    		Status recommendationStatus = question.getRecommendationStatus();
    		Integer recommendationStatusPriority = 
    			recommendationStatus.getPriority();
    		
    		String locale = question.getLocale();
    		Status YAADI_LAID = Status.findByType(
    				ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_YAADILAID, 
    				locale);
    		Integer yaadiLaidPriority = YAADI_LAID.getPriority();
    		
    		if(recommendationStatusPriority.compareTo(
        					yaadiLaidPriority) < 0) {
    			return UNSTARRED_STATE.POST_FINAL_AND_PRE_YAADI_LAID;
    		}
    		else {
    			return UNSTARRED_STATE.YAADI_LAID;
    		}
    	}
    	else {
    		throw new ELSException("Question.findUnstarredState/1", 
				"Unhandled status type.");
    	}

    }
    
    /**
     * Note that the statuses:
     * 	QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_FROM_MEMBER,
     * 	QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_FROM_DEPARTMENT,
     * 	QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_FROM_GOVT,
     * 	QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT
     * are final statuses of a workflow but are not the ultimate
     * status of @param question.
     * 
     * Admission & Rejection are the only ultimate (internalStatus) 
     * states of a Question, rest all are intermediate states.
     * 
     * IMP Note: The implementation is written keeping a standalone
     * or parent question in mind. For Clubbed questions add the
     * necessary cases.
     */
    private static SHORT_NOTICE_STATE findShortNoticeState(
    		final Question question) throws ELSException {

    	Status internalStatus = question.getInternalStatus();
    	String internalStatusType = internalStatus.getType();
    	
    	if(internalStatusType.equals(
    			ApplicationConstants.QUESTION_SHORTNOTICE_SUBMIT) 
    			|| internalStatusType.equals(
    					ApplicationConstants
    						.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED)) {
    		return SHORT_NOTICE_STATE.PRE_WORKFLOW;
    	}
    	else if(internalStatusType.equals(
    			ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMISSION)
    			|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_SHORTNOTICE_RECOMMEND_REJECTION)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_MEMBER)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_GOVT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
    	    	|| internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_FROM_GOVT)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT)) {
    		return SHORT_NOTICE_STATE.IN_WORKFLOW_AND_PRE_FINAL;
    	}
    	else if(internalStatusType.equals(
				ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
				|| internalStatusType.equals(
						ApplicationConstants
							.QUESTION_SHORTNOTICE_FINAL_REJECTION)) {
    		// recommendation status priority
    		Status recStatus = question.getRecommendationStatus();
    		Integer recStatusPriority = recStatus.getPriority();
    		
    		String locale = question.getLocale();
    		
    		// date and answer received priority
    		Status DATE_AND_ANSWER_RECEIVED = Status.findByType(
    				ApplicationConstants
    					.QUESTION_SHORTNOTICE_PROCESSED_DATEANDANSWERRECEIVED, 
    				locale);
    		Integer dateAnswerReceivedPriority = 
    			DATE_AND_ANSWER_RECEIVED.getPriority();
    		
    		// final date admitted priority
    		Status DATE_ADMITTED = Status.findByType(
    				ApplicationConstants
    					.QUESTION_SHORTNOTICE_PROCESSED_FINAL_DATEADMITTED, 
    				locale);
    		Integer dateAdmittedPriority = DATE_ADMITTED.getPriority();
    		
    		// final date resubmit priority
    		Status DATE_RESUBMIT = Status.findByType(
    				ApplicationConstants
    					.QUESTION_SHORTNOTICE_PROCESSED_FINAL_DATERESUBMIT, 
    				locale);
    		Integer dateResubmitPriority = DATE_RESUBMIT.getPriority();
    		
    		if(recStatusPriority.compareTo(dateAnswerReceivedPriority) < 0) {
    			return SHORT_NOTICE_STATE.POST_FINAL_AND_PRE_DATE_SUBMISSION;
    		}
    		else if(recStatusPriority.compareTo(dateAnswerReceivedPriority) >= 0
    				&& recStatusPriority.compareTo(dateAdmittedPriority) < 0
    				&& recStatusPriority.compareTo(dateResubmitPriority) < 0) {
    			return SHORT_NOTICE_STATE
    				.POST_DATE_SUBMISSION_AND_PRE_DATE_ADMISSION;
    		}
    		else if(recStatusPriority.compareTo(dateAdmittedPriority) >= 0) {
    			return SHORT_NOTICE_STATE.FINAL_DATE_ADMISSION;
    		}
    		else {
    			throw new ELSException("Question.findShortNoticeState/1", 
					"Unhandled status type.");
    		}
    	}
    	else {
    		throw new ELSException("Question.findShortNoticeState/1", 
				"Unhandled status type.");
    	}
    }
   
    
				
    /**
     * Note that the statuses:
     * 	QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_FROM_MEMBER,
     * 	QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_FROM_DEPARTMENT,
     * 	QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_FROM_GOVT,
     * 	QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT
     * are final statuses of a workflow but are not the ultimate
     * status of @param question.
     * 
     * Admission & Rejection are the only ultimate (internalStatus) 
     * states of a Question, rest all are intermediate states.
     * 
     * IMP Note: The implementation is written keeping a standalone
     * or parent question in mind. For Clubbed questions add the
     * necessary cases.
     */
    private static HALF_HOUR_STATE findHalfHourState(
    		final Question question) throws ELSException {
    	Status internalStatus = question.getInternalStatus();
    	String internalStatusType = internalStatus.getType();
    	
    	Status ballotStatus = question.getBallotStatus();
    	
    	if(internalStatusType.equals(
    			ApplicationConstants
    				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SUBMIT) 
    			|| internalStatusType.equals(
    					ApplicationConstants
    						.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED)) {
    		return HALF_HOUR_STATE.PRE_WORKFLOW;
    	}
    	else if(internalStatusType.equals(
    			ApplicationConstants
    				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMISSION)
    			|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECTION)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_GOVT)
    	    	|| internalStatusType.equals(
    	    			ApplicationConstants
    	    				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
    	    	|| internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_FROM_GOVT)
    	        || internalStatusType.equals(
    	        		ApplicationConstants
    	        			.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT)) {
    		return HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL;
    	}
    	else if(ballotStatus == null
    			&& (internalStatusType.equals(
    					ApplicationConstants
    						.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
    				|| internalStatusType.equals(
    						ApplicationConstants
    							.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION))) {
    		return HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT;
    	}
    	else if(ballotStatus != null) {
    		return HALF_HOUR_STATE.POST_BALLOT;
    	}
    	else {
    		throw new ELSException("Question.findHalfHourState/1", 
				"Unhandled status type.");
    	}

    }
    
    /**
	 * Processing mode provides a configuration mechanism
	 * which allows the Chart to behave in the particular
	 * processing mode independent of the houseType.
	 */
    public static enum PROCESSING_MODE {LOWERHOUSE, UPPERHOUSE};
    
    public static PROCESSING_MODE getProcessingMode(
			final Session session) throws ELSException {
		String parameter = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
		
		try {
			if(parameter.equals(ApplicationConstants.UPPER_HOUSE)) {
				return PROCESSING_MODE.UPPERHOUSE;
			}
			else { // parameter.equals(ApplicationConstants.LOWER_HOUSE)
				return PROCESSING_MODE.LOWERHOUSE;
			}
		}
		catch(Exception e) {
			throw new ELSException("Question.getProcessingMode/1", 
					"Processing mode for Starred Question is not configured" +
					" in the session.");
		}
	}

	//===============================================
	//
	//===== GROUP CHANGE API & SUPPORTING METHODS ===
	//
	//===============================================
    /**
     * Using the drafts of @param question, determine if it's group
     * has changed. 
     * IF yes, return previous Group.
     * ELSE return null.
     */
    public static Group isGroupChanged(
    		final Question question) throws ELSException {
    	/*
    	 * Refer to second previous draft because the first previous
    	 * because the first previous draft and @param question
    	 * have similar attributes.
    	 */
    	QuestionDraft draft = question.findSecondPreviousDraft();
    	Group group = question.getGroup();
    	
    	if(group != null && draft != null) {
    		Group previousGroup = draft.getGroup();
    		if(previousGroup != null
    				&& ! previousGroup.getId().equals(group.getId())) {
    			return previousGroup;
    		}
    	}
    	
    	return null;
    }

    public static void onGroupChange(final Question question,
    		final Group fromGroup) throws ELSException {
    	DeviceType deviceType = question.getType();
    	String deviceTypeType = deviceType.getType();
    	
    	if(deviceTypeType.equals(
    			ApplicationConstants.STARRED_QUESTION)) {
    		Question.onStarredGroupChange(question, fromGroup);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants.UNSTARRED_QUESTION)) {
    		Question.onUnstarredGroupChange(question, fromGroup);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    		Question.onShortNoticeGroupChange(question, fromGroup);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants
    				.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
    		Question.onHalfHourGroupChange(question, fromGroup);
    	}
    	else {
    		throw new ELSException("Question.onGroupChange/2", 
    				"Method invoked for inappropriate device type.");
    	}
    }

    private static void onStarredGroupChange(final Question question, 
    		final Group fromGroup) throws ELSException {
    	Session session = question.getSession();
		PROCESSING_MODE processingMode = Question.getProcessingMode(session);
		
		try {
			if(processingMode == PROCESSING_MODE.LOWERHOUSE) {
				Question.onStarredGroupChangeLH(question, fromGroup);
			}
			else {// processingMode == PROCESSING_MODE.UPPERHOUSE)
				Question.onStarredGroupChangeUH(question, fromGroup);
			}
		}
		catch(ELSException e) {
			throw e;
		}
    }

	private static void onUnstarredGroupChange(final Question question,
    		final Group fromGroup) throws ELSException {
    	Session session = question.getSession();
    	House house = session.getHouse();
    	HouseType houseType = house.getType();
    	
    	String houseTypeType = houseType.getType();
    	if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
    		Question.onUnstarredGroupChangeLH(question, fromGroup);
    	}
    	else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
    		Question.onUnstarredGroupChangeUH(question, fromGroup);
    	}
    	else {
    		throw new ELSException("Question.onUnstarredGroupChange/2", 
				"Question has inappropriate house type.");
    	}
    }

	private static void onHalfHourGroupChange(final Question question, 
    		final Group fromGroup) throws ELSException {
    	Session session = question.getSession();
    	House house = session.getHouse();
    	HouseType houseType = house.getType();
    	
    	String houseTypeType = houseType.getType();
    	if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
    		Question.onHalfHourGroupChangeLH(question, fromGroup);
    	}
    	else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
    		Question.onHalfHourGroupChangeUH(question, fromGroup);
    	}
    	else {
    		throw new ELSException("Question.onHalfHourGroupChange/2", 
				"Question has inappropriate house type.");
    	}
    }

    private static void onShortNoticeGroupChange(final Question question,
    		final Group fromGroup) throws ELSException {
    	Session session = question.getSession();
    	House house = session.getHouse();
    	HouseType houseType = house.getType();
    	
    	String houseTypeType = houseType.getType();
    	if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
    		Question.onShortNoticeGroupChangeLH(question, fromGroup);
    	}
    	else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
    		Question.onShortNoticeGroupChangeUH(question, fromGroup);
    	}
    	else {
    		throw new ELSException("Question.onShortNoticeGroupChange/2", 
				"Question has inappropriate house type.");
    	}
    }

	/**
     * @param question could be STANDALONE, PARENT, or KID.
     * 
     * 1. IF @param question is KID
     * 		Raise an Exception. Only STANDALONE or PARENT question's
     * 		group can be changed.
     * 
     * 2. IF @param question is STANDALONE
     * 	A. IF status is less than FINAL then
     * 		-- group, ministry, subdepartment is already set 
     * 		-- stop its workflow
     * 		-- change status to GROUP_CHANGED
     * 		-- invoke Chart.groupChange(question, fromGroup, false)
     *  
     *  B. IF status is FINAL but less than BALLOTED then
     *  	-- group, ministry, subdepartment is already set
     *  	-- stop its workflow
     *     	-- change status to FINAL status
     *     	-- invoke Chart.groupChange(question, fromGroup, true)
     *     	-- start workflow at Assistant (after Speaker) level
     *     
     *  C. IF status is BALLOTED then 
     *  	-- group, ministry, subdepartment is already set
     * 
     * 3. IF @param question is PARENT
     * 	IF any KID is in a "Clubbing Approval" workflow then raise an
     * 	Exception. The parent is supposed to have only legal KIDs before
     * 	its Group is changed.
     * 
     *  ELSE
     * 		A. IF status is less than FINAL then
     * 			-- group, ministry, subdepartment is already set in parent 
     * 			-- stop its workflow
     * 		   	-- unclub all the clubbings
     * 			-- change everyone's (parent as well as kids) status to 
     * 			   GROUP_CHANGED
     * 			-- set kids' group, ministry, subdepartment from parent
     * 			-- invoke Chart.groupChange(question, fromGroup, false) on 
     * 			   each question
     * 
     * 		B. IF status is FINAL but less than BALLOTED then 
     * 			-- group, ministry, subdepartment is already set in parent
     * 			-- stop its workflow
     * 			-- change parent's status to final status 
     * 			-- set kids' group, ministry, subdepartment from parent
     * 			-- invoke Chart.groupChange(question, fromGroup, true) on 
     * 			   @param question. 
     * 		   	-- start workflow at Assistant (after Speaker) level.
     * 
     * 		C. IF status is BALLOTED then 
     * 			-- group, ministry, subdepartment is already set in parent
     * 			-- set kids' group, ministry, subdepartment from parent
     */
    private static void onStarredGroupChangeLH(final Question question,
			final Group fromGroup) throws ELSException {
    	String locale = question.getLocale();
    	Status GROUP_CHANGED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED, 
				locale);
    	
    	CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	STARRED_STATE qnState = Question.findStarredState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onStarredGroupChangeLH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == STARRED_STATE.PRE_CHART) {
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.setProcessed(false);
    			question.merge();
    		}
    		else if(qnState == STARRED_STATE.ON_CHART) {
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.setProcessed(false);
    			question.merge();
    			
    			// Invoke Chart.groupChange/3
    			Chart.groupChange(question, fromGroup, false);
    		}
    		else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.setProcessed(false);
    			question.merge();
    			
    			// Invoke Chart.groupChange/3
    			Chart.groupChange(question, fromGroup, false);
    		}
    		else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			
    			question.removeExistingWorkflowAttributes();
    			
    			/*
    			 * Change recommendation status to final (internal) status.
    			 */
    			Status internalStatus = question.getInternalStatus();
//    			question.setRecommendationStatus(internalStatus);
//    			question.merge();
    			
    			/*
    			 * Invoke Chart.groupChange/3
    			 */
    			Chart.groupChange(question, fromGroup, true);
    			
    			if(wfDetails != null){
	    			// Before ending wfDetails process collect information
	    			// which will be useful for creating a new process later.
	    			Integer assigneeLevel = 
	    				Integer.parseInt(wfDetails.getAssigneeLevel());
	    			String userGroupType = wfDetails.getAssigneeUserGroupType();
	    			
	    			WorkflowDetails.endProcess(wfDetails);
	    			question.removeExistingWorkflowAttributes();
	    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
	    			/*
	    			 * Start the workflow at Assistant (after Speaker) level.
	    			 */
	    			
//	    			WorkflowDetails.startProcessAtGivenLevel(question, 
//	    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
//	    					ApplicationConstants.ASSISTANT, assigneeLevel, 
//	    					locale);
	    			
	    			//Question in Post final status and pre ballot state can be group changed by Department 
	    			//as well as assistant of Secretariat
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    		}/*
    		 * else if qnState == QUESTION_STATE.POST_BALLOT_AND_PRE_YAADI_LAID
    		 * then reset the group to its previous group number.
    		 */
    		else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID){
    			QuestionDraft questionDraft = Question.findLatestGroupChangedDraft(question);
    			if(questionDraft != null){
    				Long draftGroupId = questionDraft.getGroup().getId();
    				WorkflowDetails wfDetails = 
    	    				WorkflowDetails.findCurrentWorkflowDetail(question);
    	    		if(wfDetails != null){
    	    			question.removeExistingWorkflowAttributes();
    	    			// Before ending wfDetails process collect information
    	    			// which will be useful for creating a new process later.
    	    			String workflowType = wfDetails.getWorkflowType();
    	    			Integer assigneeLevel = 
    	    				Integer.parseInt(wfDetails.getAssigneeLevel());
    	    			WorkflowDetails.completeTask(question);
        				if(!draftGroupId.equals(question.getGroup().getId())){
        					question.setGroup(questionDraft.getGroup());
        					question.simpleMerge();
        					String userGroupType = wfDetails.getAssigneeUserGroupType();
        					if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
        	    				userGroupType = ApplicationConstants.DEPARTMENT;
        	    				assigneeLevel = assigneeLevel - 1;
        	    			}
        					
        	    			//Question in Post final status and pre ballot state can be group changed by Department 
        	    			//as well as assistant of Secretariat
        	    			WorkflowDetails.startProcessAtGivenLevel(question, 
        	    					ApplicationConstants.APPROVAL_WORKFLOW, question.getInternalStatus(), 
        	    					userGroupType, assigneeLevel, 
        	    					locale);
        				}
    	    		}
    	    		
    				//WorkflowDetails.completeTask(question);

    			}
    		}
    		
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			Question.isHavingIllegalChild(question);
    		if(isHavingIllegalChild) {
    			throw new ELSException("Question.onStarredGroupChangeLH/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == STARRED_STATE.ON_CHART) {
    				List<Question> clubbings = Question.findClubbings(question);
    				
    				// Unclub all the Questions
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
    				
    				/*
    				 * Change everyone's (parent as well as kids) status to 
    				 * GROUP_CHANGED. Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
    				question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				
        				kid.setInternalStatus(GROUP_CHANGED);
            			kid.setRecommendationStatus(GROUP_CHANGED);
            			
            			kid.merge();
        			}
    				
    				// Invoke Chart.groupChange/3 on each of the Questions
        			Chart.groupChange(question, fromGroup, false);
        			for(Question offspring : clubbings) {
        				Chart.groupChange(offspring, fromGroup, false);
        			}
    			}
    			else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
    				
        			List<Question> clubbings = Question.findClubbings(question);
        			
        			/*
    				 * Unclub all the Questions
    				 */
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
        			
        			/*
        			 * Change everyone's (parent as well as kids) status to 
        			 * GROUP_CHANGED. Parent's group & related information 
        			 * has already changed. Perform the same on Kids.
        			 */
        			question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				
        				kid.setInternalStatus(GROUP_CHANGED);
        				kid.setRecommendationStatus(GROUP_CHANGED);
        				
        				kid.merge();
        			}
        			
    				/*
    				 * Invoke Chart.groupChange/3 on each of the Questions
    				 */
        			Chart.groupChange(question, fromGroup, false);
        			for(Question offspring : clubbings) {
        				Chart.groupChange(offspring, fromGroup, false);
        			}
    			}
    			else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			
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
        			question.removeExistingWorkflowAttributes();
    			    
    			    /*
        			 * Change parent's recommendation status to final 
        			 * (internal) status. Additionally, Parent's group & 
        			 * related information has already changed. Perform 
        			 * the same on Kids.
        			 */
        			Status internalStatus = question.getInternalStatus();
        			question.setRecommendationStatus(internalStatus);
        			question.merge();
        			
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
            			kid.merge();
        			}
    			    
    			    /*
    			     * Invoke Chart.groupChange/3 on the Question
    			     */
        			Chart.groupChange(question, fromGroup, true);
        			
        			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
	    			/*
	    			 * Start the workflow at Assistant (after Speaker) level.
	    			 */
	    			
//	    			WorkflowDetails.startProcessAtGivenLevel(question, 
//	    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
//	    					ApplicationConstants.ASSISTANT, assigneeLevel, 
//	    					locale);
	    			
	    			//Question in Post final status and pre ballot state can be group changed by Department 
	    			//as well as assistant of Secretariat
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			    
    			}
    			else if(qnState == 
    				STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
    				// Parent's group & related information has already
    				// changed. Perform the same on Kids.
    				Group group = question.getGroup();
    				Ministry ministry = question.getMinistry();
    				SubDepartment subDepartment = question.getSubDepartment();
    				
    				List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
        			
        			WorkflowDetails wfDetails = 
            				WorkflowDetails.findCurrentWorkflowDetail(question);
        			Integer assigneeLevel = null;
        			String userGroupType = null;
            		if(wfDetails != null){
	        			// Before ending wfDetails process collect information
	        			// which will be useful for creating a new process later.
	        			assigneeLevel = 
	        				Integer.parseInt(wfDetails.getAssigneeLevel());
	        			userGroupType = wfDetails.getAssigneeUserGroupType();
	        			
	        			WorkflowDetails.endProcess(wfDetails);
    				}
            			question.removeExistingWorkflowAttributes();
        			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	    				userGroupType = ApplicationConstants.DEPARTMENT;
	    				assigneeLevel = assigneeLevel - 1;
	    			}
        			Status internalStatus = question.getInternalStatus();
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    		}
    	}
	}
    
	/**
     * @param question could be STANDALONE, PARENT, or KID.
     * 
     * 1. IF @param question is KID
     * 		Raise an Exception. Only STANDALONE or PARENT question's
     * 		group can be changed.
     * 
     * 2. IF @param question is STANDALONE
     * 	A. IF status is less than FINAL then
     * 		-- group, ministry, subdepartment is already set 
     * 		-- stop its workflow
     * 		-- change status to GROUP_CHANGED
     * 		-- invoke Chart.groupChange(question, fromGroup, false)
     *  
     *  B. IF status is FINAL but less than BALLOTED then
     *  	-- group, ministry, subdepartment is already set
     *  	-- stop its workflow
     *     	-- change status to FINAL status
     *     	-- invoke Chart.groupChange(question, fromGroup, true)
     *     	-- Start workflow at Assistant (after Chairman) level
     *     
     *  C. IF status is BALLOTED then 
     *  	-- group, ministry, subdepartment is already set
     *  	-- recreate the ballot
     * 
     * 3. IF @param question is PARENT
     * 	IF any KID is in a "Clubbing Approval" workflow then raise an
     * 	Exception. The parent is supposed to have only legal KIDs before
     * 	its Group is changed.
     * 
     *  ELSE
     * 		A. IF status is less than FINAL then
     * 			-- group, ministry, subdepartment is already set in parent 
     * 			-- stop its workflow
     * 		   	-- unclub all the clubbings
     * 			-- change everyone's (parent as well as kids) status to 
     * 			   GROUP_CHANGED
     * 			-- set kids' group, ministry, subdepartment from parent
     * 			-- invoke Chart.groupChange(question, fromGroup, false) on 
     * 			   each question
     * 
     * 		B. IF status is FINAL but less than BALLOTED then 
     * 			-- group, ministry, subdepartment is already set in parent
     * 			-- stop its workflow
     * 			-- change parent's status to final status 
     * 			-- set kids' group, ministry, subdepartment from parent
     * 			-- invoke Chart.groupChange(question, fromGroup, true) on 
     * 			   @param question. 
     * 		   	-- start workflow at Assistant (after Chairman) level.
     * 
     * 		C. IF status is BALLOTED then 
     * 			-- group, ministry, subdepartment is already set in parent
     * 			-- set kids' group, ministry, subdepartment from parent
     * 			-- recreate the ballot
     */
    private static void onStarredGroupChangeUH(final Question question,
			final Group fromGroup) throws ELSException {
    	String locale = question.getLocale();
    	Status GROUP_CHANGED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED, 
				locale);
    	
    	CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	STARRED_STATE qnState = Question.findStarredState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onStarredGroupChangeUH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {    		
    		if(qnState == STARRED_STATE.PRE_CHART) {
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    		}
    		else if(qnState == STARRED_STATE.ON_CHART) {
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    			
    			// Invoke Chart.groupChange/3
    			Chart.groupChange(question, fromGroup, false);
    		}
    		else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    			
    			// Invoke Chart.groupChange/3
    			Chart.groupChange(question, fromGroup, false);
    		}
    		else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			String workflowType = null;
    			Integer assigneeLevel = null;
    			Status internalStatus = question.getInternalStatus();
    			
    			if(wfDetails != null){
	    			// Before ending wfDetails process collect information
	    			// which will be useful for creating a new process later.
	    			workflowType = wfDetails.getWorkflowType();
	    			assigneeLevel = 
	    				Integer.parseInt(wfDetails.getAssigneeLevel());
	    			
	    			WorkflowDetails.endProcess(wfDetails);
	    			
	    			if(!wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
	    					&& !wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
	    				/*
	        			 * Change recommendation status to final (internal) status.    			 
	        			 */	        			
	        			question.setRecommendationStatus(internalStatus);
	    			}
    			}
    			question.removeExistingWorkflowAttributes();    			
    			question.merge();
    			
    			/*
    			 * Invoke Chart.groupChange/3
    			 */
    			Chart.groupChange(question, fromGroup, true);
    			
    			/*
    			 * Start the workflow at Appropriate Level (after Speaker) level.
    			 */
    			if(question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODEPARTMENT)
    					|| question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODESKOFFICER)) {
    				WorkflowDetails.startProcessAtGivenLevel(question, 
        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
        					ApplicationConstants.DEPARTMENT, assigneeLevel, 
        					locale);
    			} else {
    				WorkflowDetails.startProcessAtGivenLevel(question, 
        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
        					ApplicationConstants.ASSISTANT, assigneeLevel, 
        					locale);
    			}   			
    		}
    		else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			String workflowType = null;
    			Integer assigneeLevel = null;    					
    			Status internalStatus = question.getInternalStatus();
    			
    			if(wfDetails != null){
	    			// Before ending wfDetails process collect information
	    			// which will be useful for creating a new process later.
	    			workflowType = wfDetails.getWorkflowType();
	    			assigneeLevel = 
	    				Integer.parseInt(wfDetails.getAssigneeLevel());
	    			
	    			WorkflowDetails.endProcess(wfDetails);
	    			
	    			if(!wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
	    					&& !wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
	    				/*
	        			 * Change recommendation status to final (internal) status.    			 
	        			 */	        			
	        			question.setRecommendationStatus(internalStatus);
	    			}
    			}
    			question.removeExistingWorkflowAttributes();    			
    			question.merge();
    			
    			/*
    			 * Invoke Chart.groupChange/3
    			 */
    			Chart.groupChange(question, fromGroup, true);
    			
    			/*
    			 * Start the workflow at Appropriate Level (after Speaker) level.
    			 */
    			if(question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODEPARTMENT)
    					|| question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODESKOFFICER)) {
    				WorkflowDetails.startProcessAtGivenLevel(question, 
        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
        					ApplicationConstants.DEPARTMENT, assigneeLevel, 
        					locale);
    			} else {
    				WorkflowDetails.startProcessAtGivenLevel(question, 
        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
        					ApplicationConstants.ASSISTANT, assigneeLevel, 
        					locale);
    			}
    			
    			/*
    			 * Start the workflow at Assistant (after Speaker) level.
    			 */
//    			WorkflowDetails.startProcessAtGivenLevel(question, 
//    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
//    					ApplicationConstants.ASSISTANT, assigneeLevel, 
//    					locale);
//    			Ballot ballot = Ballot.find(question);
//    			Ballot.regenerate(ballot);
    		}
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			Question.isHavingIllegalChild(question);
    		if(isHavingIllegalChild) {
    			throw new ELSException("Question.onStarredGroupChangeUH/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == STARRED_STATE.ON_CHART) {
    				List<Question> clubbings = Question.findClubbings(question);
    				
    				// Unclub all the Questions
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
    				
        			/*
        			 * Change everyone's (parent as well as kids) status to 
        			 * GROUP_CHANGED. Parent's group & related information 
        			 * has already changed. Perform the same on Kids.
        			 */
        			question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				
        				kid.setInternalStatus(GROUP_CHANGED);
        				kid.setRecommendationStatus(GROUP_CHANGED);
        				
        				kid.merge();
        			}
    				
    				// Invoke Chart.groupChange/3 on each of the Questions
        			Chart.groupChange(question, fromGroup, false);
        			for(Question offspring : clubbings) {
        				Chart.groupChange(offspring, fromGroup, false);
        			}
        		}
        		else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
        			/*
        			 * Stop the question's workflow
        			 */
        			WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();

        			List<Question> clubbings = Question.findClubbings(question);

        			/*
        			 * Unclub all the Questions
        			 */
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
        			
        			/*
        			 * Change everyone's (parent as well as kids) status to 
        			 * GROUP_CHANGED. Parent's group & related information 
        			 * has already changed. Perform the same on Kids.
        			 */
        			question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				
        				kid.setInternalStatus(GROUP_CHANGED);
        				kid.setRecommendationStatus(GROUP_CHANGED);
        				
        				kid.merge();
        			}
        			
        			/*
        			 * Invoke Chart.groupChange/3 on each of the Questions
        			 */
    				Chart.groupChange(question, fromGroup, false);
    				for(Question offspring : clubbings) {
    					Chart.groupChange(offspring, fromGroup, false);
    				}
        		}
        		else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
        			/*
        			 * Stop the question's workflow
        			 */
        			WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			String workflowType = null;
        			Integer assigneeLevel = null;        			
        			Status internalStatus = question.getInternalStatus();
        			
        			if(wfDetails != null){
    	    			// Before ending wfDetails process collect information
    	    			// which will be useful for creating a new process later.
    	    			workflowType = wfDetails.getWorkflowType();
    	    			assigneeLevel = 
    	    				Integer.parseInt(wfDetails.getAssigneeLevel());
    	    			
    	    			WorkflowDetails.endProcess(wfDetails);
    	    			
    	    			if(!wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
    	    					&& !wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
    	    				/*
    	        			 * Change recommendation status to final (internal) status.    			 
    	        			 */	        			
    	        			question.setRecommendationStatus(internalStatus);
    	    			}
        			}
        			question.removeExistingWorkflowAttributes();    			
        			question.merge();

        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}

        			/*
        			 * Invoke Chart.groupChange/3 on the Question
        			 */
        			Chart.groupChange(question, fromGroup, true);

        			/*
        			 * Start the workflow at Appropriate Level (after Speaker) level.
        			 */
        			if(question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODEPARTMENT)
        					|| question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODESKOFFICER)) {
        				WorkflowDetails.startProcessAtGivenLevel(question, 
            					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
            					ApplicationConstants.DEPARTMENT, assigneeLevel, 
            					locale);
        			} else {
        				WorkflowDetails.startProcessAtGivenLevel(question, 
            					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
            					ApplicationConstants.ASSISTANT, assigneeLevel, 
            					locale);
        			}
        		}
        		else if(qnState == 
        			STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
        			/*
        			 * Stop the question's workflow
        			 */
        			WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			String workflowType = null;
        			Integer assigneeLevel = null;        			
        			Status internalStatus = question.getInternalStatus();
        			
        			if(wfDetails != null){
    	    			// Before ending wfDetails process collect information
    	    			// which will be useful for creating a new process later.
    	    			workflowType = wfDetails.getWorkflowType();
    	    			assigneeLevel = 
    	    				Integer.parseInt(wfDetails.getAssigneeLevel());
    	    			
    	    			WorkflowDetails.endProcess(wfDetails);
    	    			
    	    			if(!wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
    	    					&& !wfDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
    	    				/*
    	        			 * Change recommendation status to final (internal) status.    			 
    	        			 */	        			
    	        			question.setRecommendationStatus(internalStatus);
    	    			}
        			}
        			question.removeExistingWorkflowAttributes();    			
        			question.merge();
        			
        			// Parent's group & related information has already
        			// changed. Perform the same on Kids.
        			Group group = question.getGroup();
    				Ministry ministry = question.getMinistry();
    				SubDepartment subDepartment = question.getSubDepartment();
    				
    				List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
        			
        			/*
        			 * Invoke Chart.groupChange/3
        			 */
        			Chart.groupChange(question, fromGroup, true);
        			
        			/*
        			 * Start the workflow at Appropriate Level (after Speaker) level.
        			 */
        			if(question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODEPARTMENT)
        					|| question.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODESKOFFICER)) {
        				WorkflowDetails.startProcessAtGivenLevel(question, 
            					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
            					ApplicationConstants.DEPARTMENT, assigneeLevel, 
            					locale);
        			} else {
        				WorkflowDetails.startProcessAtGivenLevel(question, 
            					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
            					ApplicationConstants.ASSISTANT, assigneeLevel, 
            					locale);
        			}
        			
//        			// Regenerate the Ballot
//        			Ballot ballot = Ballot.find(question);
//        			Ballot.regenerate(ballot);
        		}
    		}
    	}
	}

    private static void onUnstarredGroupChangeLH(final Question question,
			final Group fromGroup) throws ELSException {
		Question.onUnstarredGroupChangeCommon(question, fromGroup);
	}
    
    private static void onUnstarredGroupChangeUH(final Question question,
			final Group fromGroup) throws ELSException {
    	Question.onUnstarredGroupChangeCommon(question, fromGroup);
	}
    
    private static void onHalfHourGroupChangeLH(final Question question,
    		final Group fromGroup) throws ELSException {
		Question.onHalfHourGroupChangeCommon(question, fromGroup);
	}

	private static void onHalfHourGroupChangeUH(final Question question,
    		final Group fromGroup) throws ELSException {
    	Question.onHalfHourGroupChangeCommon(question, fromGroup);
	}
    
    private static void onShortNoticeGroupChangeLH(final Question question,
    		final Group fromGroup) throws ELSException {
		Question.onShortNoticeGroupChangeCommon(question, fromGroup);
	}

	private static void onShortNoticeGroupChangeUH(final Question question,
    		final Group fromGroup) throws ELSException {
    	Question.onShortNoticeGroupChangeCommon(question, fromGroup);
	}
    
    /**
     * @param question could be STANDALONE, PARENT, or KID.
     * 
     * 1. IF @param question is KID
     * 		Raise an Exception. Only STANDALONE or PARENT question's
     * 		group can be changed.
     * 
     * 2. IF @param question is STANDALONE
     * 	A. IF status is less than FINAL then
     * 		-- group, ministry, subdepartment is already set 
     * 		-- stop its workflow
     * 		-- change status to GROUP_CHANGED
     *  
     *  B. IF status is FINAL but less than YAADI_LAID then
     *  	-- group, ministry, subdepartment is already set
     *  	-- stop its workflow
     *     	-- change status to FINAL status
     *     	-- IF originalDeviceType == STARRED then invoke 
     *     	   Chart.groupChange(question, fromGroup, true)
     *     	-- Start workflow at Assistant (after Speaker) level
     * 
     * 3. IF @param question is PARENT
     * 	IF any KID is in a "Clubbing Approval" workflow then raise an
     * 	Exception. The parent is supposed to have only legal KIDs before
     * 	its Group is changed.
     * 
     *  ELSE
     * 		A. IF status is less than FINAL then
     * 			-- group, ministry, subdepartment is already set in parent 
     * 			-- stop its workflow
     * 			-- change parent's status to GROUP_CHANGED
     * 			-- set kids' group, ministry, subdepartment from parent
     * 
     * 		B. IF status is FINAL but less than YAADI_LAID then 
     * 			-- group, ministry, subdepartment is already set in parent
     * 			-- stop its workflow
     * 			-- change parent's status to final status 
     * 			-- set kids' group, ministry, subdepartment from parent
     * 			-- IF originalDeviceType == STARRED then invoke 
     * 			   Chart.groupChange(question, fromGroup, true) on 
     * 			   @param question. 
     * 		   	-- start workflow at Assistant (after Speaker) level.
     */
    private static void onUnstarredGroupChangeCommon(final Question question,
			final Group fromGroup) throws ELSException {
    	String locale = question.getLocale();
    	Status GROUP_CHANGED = Status.findByType(
				ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_GROUPCHANGED, 
				locale);
    	
    	CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	UNSTARRED_STATE qnState = Question.findUnstarredState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onUnstarredGroupChangeCommon/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == UNSTARRED_STATE.PRE_WORKFLOW) {
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    		}
    		else if(qnState == 
    			UNSTARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    		}
    		else if(qnState == 
    			UNSTARRED_STATE.POST_FINAL_AND_PRE_YAADI_LAID) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			
    			// Before ending wfDetails process collect information
    			// which will be useful for creating a new process later.
    			String workflowType = wfDetails.getWorkflowType();
    			Integer assigneeLevel = 
    				Integer.parseInt(wfDetails.getAssigneeLevel());
    			String userGroupType = wfDetails.getAssigneeUserGroupType();
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			/*
    			 * Change recommendation status to final (internal) status.
    			 */
    			Status internalStatus = question.getInternalStatus();
//    			question.setRecommendationStatus(internalStatus);
//    			question.merge();
    			
    			/*
    			 * Conditional invocation of Chart.groupChange/3
    			 */
    			DeviceType originalDeviceType = question.getOriginalType();
    			if(originalDeviceType != null) {
    				String originalDeviceTypeType = 
    					originalDeviceType.getType();
    				if(originalDeviceTypeType.equals(
    						ApplicationConstants.STARRED_QUESTION)) {
    					Chart.groupChange(question, fromGroup, true);
    				}
    			}
    			
    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
    				userGroupType = ApplicationConstants.DEPARTMENT;
    				assigneeLevel = assigneeLevel - 1;
    			}
    			/*
    			 * Start the workflow at Assistant (after Speaker) level.
    			 */
    			WorkflowDetails.startProcessAtGivenLevel(question, 
    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
    					userGroupType, assigneeLevel, 
    					locale);
    		}
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			Question.isHavingIllegalChild(question);
    		if(isHavingIllegalChild) {
    			throw new ELSException(
    					"Question.onUnstarredGroupChangeCommon/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == UNSTARRED_STATE.PRE_WORKFLOW) {
    				/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        		}
        		else if(qnState == 
        			UNSTARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
        			/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
        			
        			/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        		}
        		else if(qnState == 
        			UNSTARRED_STATE.POST_FINAL_AND_PRE_YAADI_LAID) {
        			/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
    				String userGroupType = wfDetails.getAssigneeUserGroupType();
        			// Before ending wfDetails process collect information
        			// which will be useful for creating a new process later.
        			String workflowType = wfDetails.getWorkflowType();
        			Integer assigneeLevel = 
        				Integer.parseInt(wfDetails.getAssigneeLevel());
        			
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
        			
        			/*
        			 * Change recommendation status to final (internal) status.
        			 */
        			Status internalStatus = question.getInternalStatus();
        			question.setRecommendationStatus(internalStatus);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        			
        			/*
        			 * Conditional invocation of Chart.groupChange/3
        			 */
        			DeviceType originalDeviceType = question.getOriginalType();
        			if(originalDeviceType != null) {
        				String originalDeviceTypeType = 
        					originalDeviceType.getType();
        				if(originalDeviceTypeType.equals(
        						ApplicationConstants.STARRED_QUESTION)) {
        					Chart.groupChange(question, fromGroup, true);
        				}
        			}
        			
        			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
        				userGroupType = ApplicationConstants.DEPARTMENT;
        				assigneeLevel = assigneeLevel - 1;
        			}
        			/*
    				 * Start the workflow at Assistant (after Speaker) level.
    				 */
        			WorkflowDetails.startProcessAtGivenLevel(question, 
        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
        					userGroupType, assigneeLevel, 
        					locale);
        		}
    		}
    	}
	}
    
    /**
     * @param question could be STANDALONE, PARENT, or KID.
     * 
     * 1. IF @param question is KID
     * 		Raise an Exception. Only STANDALONE or PARENT question's
     * 		group can be changed.
     * 
     * 2. IF @param question is STANDALONE
     * 	A. IF status is less than FINAL then
     * 		-- group, ministry, subdepartment is already set 
     * 		-- stop its workflow
     * 		-- change status to GROUP_CHANGED
     *  
     *  B. IF status is FINAL but less than DATE_AND_ANSWER_RECEIVED then
     *  	-- group, ministry, subdepartment is already set
     *  	-- stop its workflow
     *     	-- change status to FINAL status
     *     	-- Start workflow at Assistant (after Speaker) level
     * 
     * 3. IF @param question is PARENT
     * 	IF any KID is in a "Clubbing Approval" workflow then raise an
     * 	Exception. The parent is supposed to have only legal KIDs before
     * 	its Group is changed.
     * 
     *  ELSE
     * 		A. IF status is less than FINAL then
     * 			-- group, ministry, subdepartment is already set in parent 
     * 			-- stop its workflow
     * 			-- change parent's status to GROUP_CHANGED
     * 			-- set kids' group, ministry, subdepartment from parent
     * 
     * 		B. IF status is FINAL but less than DATE_AND_ANSWER_RECEIVED then 
     * 			-- group, ministry, subdepartment is already set in parent
     * 			-- stop its workflow
     * 			-- change parent's status to final status 
     * 			-- set kids' group, ministry, subdepartment from parent
     * 		   	-- start workflow at Assistant (after Speaker) level.
     */
    private static void onShortNoticeGroupChangeCommon(final Question question,
			final Group fromGroup) throws ELSException {
    	String locale = question.getLocale();
    	Status GROUP_CHANGED = Status.findByType(
				ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_GROUPCHANGED, 
				locale);
    	
    	CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	SHORT_NOTICE_STATE qnState = Question.findShortNoticeState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onShortNoticeGroupChangeCommon/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == SHORT_NOTICE_STATE.PRE_WORKFLOW) {
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    		}
    		else if(qnState == SHORT_NOTICE_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    		}
    		else if(qnState == 
    			SHORT_NOTICE_STATE.POST_FINAL_AND_PRE_DATE_SUBMISSION) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			
    			// Before ending wfDetails process collect information
    			// which will be useful for creating a new process later.
    			String workflowType = wfDetails.getWorkflowType();
    			Integer assigneeLevel = 
    				Integer.parseInt(wfDetails.getAssigneeLevel());
    			String userGroupType = wfDetails.getAssigneeUserGroupType();
    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
    				userGroupType = ApplicationConstants.DEPARTMENT;
    				assigneeLevel = assigneeLevel - 1;
    			}
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			/*
    			 * Change recommendation status to final (internal) status.
    			 */
    			Status internalStatus = question.getInternalStatus();
    			question.setRecommendationStatus(internalStatus);
    			question.merge();
    			
    			/*
    			 * Start the workflow at Assistant (after Speaker) level.
    			 */
    			WorkflowDetails.startProcessAtGivenLevel(question, 
    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
    					userGroupType, assigneeLevel, 
    					locale);
    		}
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			Question.isHavingIllegalChild(question);
    		if(isHavingIllegalChild) {
    			throw new ELSException(
    					"Question.onShortNoticeGroupChangeCommon/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == SHORT_NOTICE_STATE.PRE_WORKFLOW) {
    				/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        		}
        		else if(qnState == 
        			SHORT_NOTICE_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
        			/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
        			
        			/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        		}
        		else if(qnState == 
        			SHORT_NOTICE_STATE.POST_FINAL_AND_PRE_DATE_SUBMISSION) {
        			/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			
        			// Before ending wfDetails process collect information
        			// which will be useful for creating a new process later.
        			String workflowType = wfDetails.getWorkflowType();
        			Integer assigneeLevel = 
        				Integer.parseInt(wfDetails.getAssigneeLevel());
        			
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
        			
        			/*
        			 * Change recommendation status to final (internal) status.
        			 */
        			Status internalStatus = question.getInternalStatus();
        			question.setRecommendationStatus(internalStatus);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        			
        			/*
    				 * Start the workflow at Assistant (after Speaker) level.
    				 */
        			WorkflowDetails.startProcessAtGivenLevel(question, 
        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
        					ApplicationConstants.ASSISTANT, assigneeLevel, 
        					locale);
        		}
    		}
    	}
	}
    
    /**
     * @param question could be STANDALONE, PARENT, or KID.
     * 
     * 1. IF @param question is KID
     * 		Raise an Exception. Only STANDALONE or PARENT question's
     * 		group can be changed.
     * 
     * 2. IF @param question is STANDALONE
     * 	A. IF status is less than FINAL then
     * 		-- group, ministry, subdepartment is already set 
     * 		-- stop its workflow
     * 		-- change status to GROUP_CHANGED
     *  
     *  B. IF status is FINAL but less than BALLOTED then
     *  	-- group, ministry, subdepartment is already set
     *  	-- stop its workflow
     *     	-- change status to FINAL status
     *     	-- Start workflow at Assistant (after Speaker) level
     * 
     * 3. IF @param question is PARENT
     * 	IF any KID is in a "Clubbing Approval" workflow then raise an
     * 	Exception. The parent is supposed to have only legal KIDs before
     * 	its Group is changed.
     * 
     *  ELSE
     * 		A. IF status is less than FINAL then
     * 			-- group, ministry, subdepartment is already set in parent 
     * 			-- stop its workflow
     * 			-- change parent's status to GROUP_CHANGED
     * 			-- set kids' group, ministry, subdepartment from parent
     * 
     * 		B. IF status is FINAL but less than BALLOTED then 
     * 			-- group, ministry, subdepartment is already set in parent
     * 			-- stop its workflow
     * 			-- change parent's status to final status 
     * 			-- set kids' group, ministry, subdepartment from parent
     * 		   	-- start workflow at Assistant (after Speaker) level.
     */
    private static void onHalfHourGroupChangeCommon(final Question question,
			final Group fromGroup) throws ELSException {
    	String locale = question.getLocale();
    	Status GROUP_CHANGED = Status.findByType(
			ApplicationConstants
				.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_GROUPCHANGED, 
			locale);
    	
    	CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	HALF_HOUR_STATE qnState = Question.findHalfHourState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onHalfHourGroupChangeCommon/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    		}
    		else if(qnState == HALF_HOUR_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			// Change status to "GROUP_CHANGED"
    			question.setInternalStatus(GROUP_CHANGED);
    			question.setRecommendationStatus(GROUP_CHANGED);
    			question.merge();
    		}
    		else if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT
    				|| qnState == HALF_HOUR_STATE.POST_BALLOT) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			
    			// Before ending wfDetails process collect information
    			// which will be useful for creating a new process later.
    			String workflowType = wfDetails.getWorkflowType();
    			Integer assigneeLevel = 
    				Integer.parseInt(wfDetails.getAssigneeLevel());
    			String userGroupType = wfDetails.getAssigneeUserGroupType();
    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
    				userGroupType = ApplicationConstants.DEPARTMENT;
    				assigneeLevel = assigneeLevel - 1;
    			}
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			/*
    			 * Change recommendation status to final (internal) status.
    			 */
    			Status internalStatus = question.getInternalStatus();
    			question.setRecommendationStatus(internalStatus);
    			question.merge();
    			
    			if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT){
	    			/*
	    			 * Start the workflow at Assistant (after Speaker) level.
	    			 */
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    		}
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			Question.isHavingIllegalChild(question);
    		if(isHavingIllegalChild) {
    			throw new ELSException(
    					"Question.onHalfHourGroupChangeCommon/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == HALF_HOUR_STATE.PRE_WORKFLOW) {
    				/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
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
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
        			
        			/*
    				 * Change parent's status to GROUP_CHANGED.
    				 */
    				question.setInternalStatus(GROUP_CHANGED);
        			question.setRecommendationStatus(GROUP_CHANGED);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
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
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			
        			// Before ending wfDetails process collect information
        			// which will be useful for creating a new process later.
        			String workflowType = wfDetails.getWorkflowType();
        			Integer assigneeLevel = 
        				Integer.parseInt(wfDetails.getAssigneeLevel());
        			String userGroupType = wfDetails.getAssigneeUserGroupType();
        			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
        				userGroupType = ApplicationConstants.DEPARTMENT;
        				assigneeLevel = assigneeLevel - 1;
        			}
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
        			
        			/*
        			 * Change recommendation status to final (internal) status.
        			 */
        			Status internalStatus = question.getInternalStatus();
        			question.setRecommendationStatus(internalStatus);
        			question.merge();
        			
        			/* Parent's group & related information 
    				 * has already changed. Perform the same on Kids.
    				 */
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);           			
            			kid.merge();
        			}
        			
        			if(qnState == HALF_HOUR_STATE.POST_FINAL_AND_PRE_BALLOT){
	        			/*
	    				 * Start the workflow at Assistant (after Speaker) level.
	    				 */
	        			WorkflowDetails.startProcessAtGivenLevel(question, 
	        					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
	        					userGroupType, assigneeLevel, 
	        					locale);
        			}
        		}
    		}
    	}
	}
    
    /**
     * A question is said to have an illegal child if any of its
     * child is not an approved child. This is a partially inconsistent 
     * state where the approval of the child might reverse the clubbing or 
     * disapproval of the child will result in unclubbing. 
     * 
     * This case arises when a child question is in a "Clubbing Approval" 
     * workflow.
     */
	private static boolean isHavingIllegalChild(
			final Question question) throws ELSException {
		DeviceType deviceType = question.getType();
		String deviceTypeType = deviceType.getType();
		
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return Question.isStarredHavingIllegalChild(question);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.UNSTARRED_QUESTION)) {
			return Question.isUnstarredHavingIllegalChild(question);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			return Question.isShortNoticeHavingIllegalChild(question);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants
					.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			return Question.isHalfHourHavingIllegalChild(question);
		}
		else {
			throw new ELSException("Question.isHavingIllegalChild/1", 
					"Illegal deviceType set in @param question.");
		}
	}
	
	private static boolean isStarredHavingIllegalChild(Question question) {
		List<ClubbedEntity> clubbings = 
			Question.findClubbedEntitiesByPosition(question); 
		for(ClubbedEntity ce : clubbings) {
			Question q = ce.getQuestion();
			
			Status internalStatus = q.getInternalStatus();
			String internalStatusType = internalStatus.getType();
			
			Status recommendationStatus = q.getRecommendationStatus();
			String recommendationStatusType = recommendationStatus.getType();
			
			if(internalStatusType.equals(
					ApplicationConstants.QUESTION_PUTUP_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_RECOMMEND_NAMECLUBBING)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_PUTUP_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)) {
				return true;
			}
			
		}
		
		return false;
	}
	
	private static boolean isUnstarredHavingIllegalChild(Question question) {
		List<ClubbedEntity> clubbings = 
			Question.findClubbedEntitiesByPosition(question); 
		for(ClubbedEntity ce : clubbings) {
			Question q = ce.getQuestion();
			
			Status internalStatus = q.getInternalStatus();
			String internalStatusType = internalStatus.getType();
			
			Status recommendationStatus = q.getRecommendationStatus();
			String recommendationStatusType = recommendationStatus.getType();
			
			if(internalStatusType.equals(
					ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)) {
				return true;
			}
			
		}
		
		return false;
	}
	
	private static boolean isShortNoticeHavingIllegalChild(Question question) {
		List<ClubbedEntity> clubbings = 
			Question.findClubbedEntitiesByPosition(question); 
		for(ClubbedEntity ce : clubbings) {
			Question q = ce.getQuestion();
			
			Status internalStatus = q.getInternalStatus();
			String internalStatusType = internalStatus.getType();
			
			Status recommendationStatus = q.getRecommendationStatus();
			String recommendationStatusType = recommendationStatus.getType();
			
			if(internalStatusType.equals(
					ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_SHORTNOTICE_RECOMMEND_NAMECLUBBING)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)) {
				return true;
			}
			
		}
		
		return false;
	}
	
	private static boolean isHalfHourHavingIllegalChild(Question question) {
		List<ClubbedEntity> clubbings = 
			Question.findClubbedEntitiesByPosition(question); 
		for(ClubbedEntity ce : clubbings) {
			Question q = ce.getQuestion();
			
			Status internalStatus = q.getInternalStatus();
			String internalStatusType = internalStatus.getType();
			
			Status recommendationStatus = q.getRecommendationStatus();
			String recommendationStatusType = recommendationStatus.getType();
			
			if(internalStatusType.equals(
					ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING)
					|| internalStatusType.equals(
							ApplicationConstants
								.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_NAMECLUBBING)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(
							ApplicationConstants
								.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the list of kids of @param question.
	 */
	private static List<Question> findClubbings(final Question question) {
		List<Question> questions = new ArrayList<Question>();
		
		List<ClubbedEntity> clubbings = 
			Question.findClubbedEntitiesByPosition(question);
		for(ClubbedEntity ce : clubbings) {
			Question q = ce.getQuestion();
			questions.add(q);
		}
		
		return questions;
	}
	
	//===============================================
	//
	//===== GROUP RESHUFFLE API & SUPPORTING METHODS 
	//
	//===============================================
	/**
	 * 3 cases arise in Cabint Reshuffle:
	 * 1. Subdepartment does not split/combine. Ministry is added or updated.
	 * 2. Existing subdepartment is split. Ministry is added or updated.
	 * 3. Existing subdepartments are combined. Ministry is added or updated.
	 * 
	 * Router. Routes the reshuffle message to the concerned deviceTypes of Question device. 
	 */
	public static void onGroupReshuffle(final Group group) throws ELSException {
		Question.groupReshuffleInStarred(group);
		Question.groupReshuffleInUnstarred(group);
		Question.groupReshuffleInShortNotice(group);
		Question.groupReshuffleInHalfHourFromQuestion(group);
	}

	// TODO: Implement case 2 and case 3
	private static void groupReshuffleInStarred(final Group group) throws ELSException {
		String locale = group.getLocale();
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		
		/*
		 * Get all the Starred questions of @param session whose internalStatus >= "SUBMITTED"
		 * and recommendation status < "YAADI_LAID"
		 */
		Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, locale);
		Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale);
		
		List<Question> questions = Question.find(group, deviceType, GTEQinternalStatus, LTrecommendationStatus, locale);
		
		// Case 1
		case1(questions, group, locale);
	}

	// TODO: Implement case 1, case 2 and case 3
	private static void groupReshuffleInUnstarred(final Group group) throws ELSException {
		/*
		 * case 1 is complicated because the processing of Unstarred Questions happens throughout
		 * the year. If questions asked in Session number 1 are yet to be processed and cabinet
		 * reshuffle happens in Session number 2 then thoe questions have to point to the new
		 *  cabinet. This might create problems when past data is to be referenced.
		 */
		
		String locale = group.getLocale();
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, locale);
		
		/*
		 * Get all the Unstarred questions of @param session whose internalStatus >= "SUBMITTED"
		 * and recommendation status < "YAADI_LAID"
		 */
		Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT, locale);
		Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_YAADILAID, locale);
		List<Question> questions = Question.find(group, deviceType, GTEQinternalStatus, LTrecommendationStatus, locale);
		
		// Case 1
		case1(questions, group, locale);
	}

	// TODO: Implement case 2 and case 3
	private static void groupReshuffleInShortNotice(final Group group) throws ELSException {
		String locale = group.getLocale();
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.SHORT_NOTICE_QUESTION, locale);
		
		/*
		 * Get all the Short Notice questions of @param session whose internalStatus >= "SUBMITTED"
		 * and recommendation status < "DISCUSSED"
		 */
		Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SUBMIT, locale);
		Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_DISCUSSED, locale);
		List<Question> questions = Question.find(group, deviceType, GTEQinternalStatus, LTrecommendationStatus, locale);
		
		// Case 1
		case1(questions, group, locale);
	}

	// TODO: Implement case 2 and case 3
	private static void groupReshuffleInHalfHourFromQuestion(final Group group) throws ELSException {
		String locale = group.getLocale();
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION, locale);
		
		/*
		 * Get all the Half Hour From Questions of @param session whose internalStatus >= "SUBMITTED"
		 * and recommendation status < "DISCUSSED"
		 */
		Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SUBMIT, locale);
		Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_DISCUSSED, locale);
		List<Question> questions = Question.find(group, deviceType, GTEQinternalStatus, LTrecommendationStatus, locale);
		
		// Case 1
		case1(questions, group, locale);
	}
	
	/**
	 * Find all the Questions of type @param deviceType belonging to @param session
	 * having internalStatus priority greater than equal to @param GTEQinternalStatus
	 * recommendationStatus priority less than @param LTrecommendationStatus
	 */
	private static List<Question> find(final Group group, 
			final DeviceType deviceType, 
			final Status GTEQinternalStatus,
			final Status LTrecommendationStatus,
			final String locale) {
		return Question.getQuestionRepository().find(group, deviceType, GTEQinternalStatus, LTrecommendationStatus, locale);
	}
	
	private static void case1(final List<Question> questions,
			final Group group,
			final String locale) throws ELSException {
		for(Question q : questions) {
			try {
				SubDepartment subDepartment = q.getSubDepartment();
				Ministry ministry = q.getMinistry();
				
				Ministry newMinistry = Ministry.findActiveNewMinistry(subDepartment, new Date(), locale); 
				if(! ministry.getId().equals(newMinistry.getId())) { // Ministry has changed
					q.setMinistry(newMinistry);
					q.simpleMerge();
				}
				
				// Commented for now. Uncomment post winter session 2014.
				/*
				Group newGroup = Group.find(newMinistry, session, locale);
				if(! group.getId().equals(newGroup.getId())) { // Group has changed
					q.setGroup(newGroup);
					q.simpleMerge();
					Question.onGroupChange(q, group);
				}
				*/
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//================================================
	//
	//===== QUESTION HANDOVER API & SUPPORTING METHODS
	//
	//================================================
	/**
	 * Handover the Question devices of @param member to the next
	 * eligible member. This method is to be used when a member
	 * becomes minister/speaker/chairman. The triggering point
	 * for this method will be MIS (MemberInformation System).
	 */
	public static void handover(final Member member, final Date onDate) throws ELSException {				
		String locale = member.getLocale();
		
		House house = Question.find(member, onDate, locale);
		if(house != null) {
			HouseType houseType = house.getType();
			Session session = Session.findLatestSession(houseType);
					
			handoverStarred(member, session, onDate, locale);
			handoverUnstarred(member, session, onDate, locale);
			handoverShortNotice(member, session, onDate, locale);
			handoverHalfFourFromQuestion(member, session, onDate, locale);
		}
	}

	/**
	 * For Starred Questions @param onDate should be between submissionStartTime and sessionEndDate for the session.
	 * @throws IOException 
	 */
	private static void handoverStarred(final Member member, final Session session, final Date onDate, 
			final String locale) throws ELSException {
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		
		Date startDate = Question.getSubmissionStartTime(session, deviceType, locale);
		Date endDate = session.getEndDate();
		boolean isDateInBetween = Question.isDateInBetween(onDate, startDate, endDate);
		
		if(isDateInBetween) {
			// Remove @param member from all these questions as supporting member. Save a draft.
			removeAsSupportingMember(member, session, deviceType);
			
			// For @param session, get all the Questions of type "STARRED" of @param member whose status is 
			// less than "YAADI_LAID".
			Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, locale);
			Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale);
			
			List<Question> questions = Question.find(member, session, deviceType, GTEQinternalStatus, LTrecommendationStatus, locale); 
			for(Question q : questions) {
				try {					
					SupportingMember supportingMember = Question.hasActiveSupportingMember(q, onDate, locale);
					if(supportingMember == null) {						
						handoverStarredNoSupportingMember(q, onDate, locale);
					}
					else {
						Member suppMember = supportingMember.getMember();
						handoverStarredToSupportingMember(q, suppMember, onDate, locale);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			// Only remove those ballotEntries which are empty
			removeStaleBallotEntries(member, session, deviceType, locale);
		}
	}

	private static void removeStaleBallotEntries(final Member member, 
			final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		List<Ballot> ballots = Ballot.find(session, deviceType, locale);
		for(Ballot b : ballots) {
			List<BallotEntry> ballotEntries = b.getBallotEntries();
			int counter = 0;
			for(BallotEntry be : ballotEntries) {
				Member m = be.getMember();
				if(m.getId().equals(member.getId())) {
					List<DeviceSequence> devSeqs = be.getDeviceSequences();
					if(devSeqs == null || devSeqs.isEmpty()) {
						ballotEntries.remove(counter);
						be.remove();
						break;
					}
				}
				counter++;
			}
			b.setBallotEntries(ballotEntries);
			b.merge();
		}
	}

	private static void handoverStarredNoSupportingMember(final Question q,
			final Date onDate,
			final String locale) throws ELSException {
		CLUBBING_STATE clubbingState = Question.findClubbingState(q);
		STARRED_STATE qnState = Question.findStarredState(q);
		
		if(clubbingState == CLUBBING_STATE.STANDALONE) {
			if(qnState == STARRED_STATE.PRE_CHART) {
				/*
				 * Since question q is standalone and has no supporting member, it is lapsed.
				 */
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
			}
			else if(qnState == STARRED_STATE.ON_CHART) {
				/*
				 * Remove the question from Chart.
				 */
				Chart.removeFromChart(q);
				
				/*
				 * Since question q is standalone and has no supporting member, it is lapsed.
				 */
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
			}
			else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				if(wfDetails != null) {
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart.removeFromChart(q);
				
				/*
				 * Since question q is standalone and has no supporting member, it is lapsed.
				 */
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
			}
			else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				if(wfDetails != null) {
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart.removeFromChart(q);
				
				/*
				 * Since question q is standalone and has no supporting member, it is lapsed.
				 */
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
			}
			else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				if(wfDetails != null) {
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart.removeFromChart(q);
				
				/*
				 * Remove the question from Ballot and PreBallot.
				 */
				boolean isResequenceDevices = true;
				Ballot.remove(q, isResequenceDevices);
				
				/*
				 *  Since question q is standalone and has no supporting member, it is lapsed.
				 */
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
			}
		}
		else if(clubbingState == CLUBBING_STATE.PARENT) {
			// if(qnState == STARRED_STATE.PRE_CHART) { } This case will never arise.
			if(qnState == STARRED_STATE.ON_CHART) {
				/*
				 * Remove the question from Chart.
				 */
				Chart.removeFromChart(q);
				
				/*
				 * Set status as lapsed.
				 */
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
			}
			else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				
				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 * Since this is a Parent question there are certain actions that needs to
				 * be performed first.
				 */
				// Get the new parent
				List<ClubbedEntity> clubbedEntities = q.getClubbedEntities();
				ClubbedEntity immediateClubbedEntity = clubbedEntities.get(0);
				Question newParent = immediateClubbedEntity.getQuestion();
				
				// End its workflow (if any)
				WorkflowDetails newParentWFDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(newParent);
				if(newParentWFDetails != null) {
					WorkflowDetails.endProcess(wfDetails);
					newParent.removeExistingWorkflowAttributes();
				}
				
				// Set its status as same as q
				Status status = q.getStatus();
				Status internalStatus = q.getInternalStatus();
				Status recommedationStatus = q.getRecommendationStatus();
				newParent.setStatus(status);
				newParent.setInternalStatus(internalStatus);
				newParent.setRecommendationStatus(recommedationStatus);
				newParent.simpleMerge();
				
				// Remove q from Chart
				Chart.removeFromChart(q);
				
				/*
				 * Set status as lapsed.
				 */				
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
				
				/*
				 * Start workflow for Question newParent at exactly the same point where 
				 * Question q's workflow ended.
				 */
				if(wfDetails != null) {
					WorkflowDetails.startProcessAtGivenLevel(newParent, 
							ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
				}
			}
			else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				
				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 * Since this is a Parent question there are certain actions that needs to
				 * be performed first.
				 */
				// Get the new parent
				List<ClubbedEntity> clubbedEntities = q.getClubbedEntities();
				ClubbedEntity immediateClubbedEntity = clubbedEntities.get(0);
				Question newParent = immediateClubbedEntity.getQuestion();
				
				// End its workflow (if any)
				WorkflowDetails newParentWFDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(newParent);
				if(newParentWFDetails != null) {
					WorkflowDetails.endProcess(wfDetails);
					newParent.removeExistingWorkflowAttributes();
				}
				
				// Set its status as same as q
				Status status = q.getStatus();
				Status internalStatus = q.getInternalStatus();
				Status recommedationStatus = q.getRecommendationStatus();
				newParent.setStatus(status);
				newParent.setInternalStatus(internalStatus);
				newParent.setRecommendationStatus(recommedationStatus);
				newParent.simpleMerge();
				
				// Remove q from Chart
				Chart.removeFromChart(q);
				
				/*
				 * Set status as lapsed.
				 */				
				Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
				q.setInternalStatus(LAPSED);
				q.setRecommendationStatus(LAPSED);
				q.setStatus(LAPSED);
				q.merge();
				
				/*
				 * Start workflow for Question newParent at exactly the same point where 
				 * Question q's workflow ended.
				 */
				if(wfDetails != null) {
					WorkflowDetails.startProcessAtGivenLevel(newParent, 
							ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
				}
			}
			else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				
				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart
				 */
				Chart chart = Chart.find(q);
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Remove q from Ballot and PreBallot.
				 */
				boolean isResequenceDevices = true;
				Ballot revisedBallot = Ballot.remove(q, isResequenceDevices);
				
				/*
				 * Handover. Hardcoding noOfRounds for now. Read it from Ballot.
				 */
				List<BallotEntry> ballotEntries = revisedBallot.getBallotEntries();
				Member handoveredToMember = 
						Question.questionHandover(q, ballotEntries, onDate, 3, locale);
				if(handoveredToMember != null) {
					revisedBallot.setBallotEntries(ballotEntries);
					revisedBallot.merge();
					
					q.setPrimaryMember(handoveredToMember);
					q.simpleMerge();
					
					// Forcefully add to Chart
					Chart.forcefullyAddToChart(chart, q);
					
					/*
					 * Start workflow for Question q at exactly the same point where 
					 * Question q's workflow ended.
					 */
					Status internalStatus = q.getInternalStatus();
					if(wfDetails != null) {
						WorkflowDetails.startProcessAtGivenLevel(q, 
								ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
					}
				}
				else {
					/*
					 * Set status as lapsed.
					 */
					Status LAPSED = Status.findByType(
							ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
					q.setInternalStatus(LAPSED);
					q.setRecommendationStatus(LAPSED);
					q.setStatus(LAPSED);
					q.merge();
				}
			}
		}
		else { // clubbingState == CLUBBING_STATE.CLUBBED
			/*
			 * Stop the workflow.
			 */
			WorkflowDetails wfDetails = 
					WorkflowDetails.findCurrentWorkflowDetail(q);	
			if(wfDetails != null) {
				try {
					WorkflowDetails.endProcess(wfDetails);
				}
				catch(Exception e) {
					
				}
				q.removeExistingWorkflowAttributes();
			}
			
			/*
			 * Remove the question from Chart.
			 */
			Chart.removeFromChart(q);
			
			/*
			 * Set status as lapsed.
			 */			
			Status LAPSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
			q.setInternalStatus(LAPSED);
			q.setRecommendationStatus(LAPSED);
			q.setStatus(LAPSED);
			q.merge();
		}
	}
	
	// TODO: Under implementation
	private static void handoverStarredToSupportingMember(final Question q,
			final Member member,
			final Date onDate,
			final String locale) throws ELSException {		
		CLUBBING_STATE clubbingState = Question.findClubbingState(q);
		STARRED_STATE qnState = Question.findStarredState(q);
		
		if(clubbingState == CLUBBING_STATE.STANDALONE) {
			if(qnState == STARRED_STATE.PRE_CHART) {
				/*
				 * Handover the question to the immediate supporting member.
				 */
				q.setPrimaryMember(member);
				q.merge();
				
				/*
				 * Since the supporting member has become primary member, he/she 
				 * is no longer a supporting member.
				 */
				Question.removeAsSupportingMember(q, member);
			}
			else if(qnState == STARRED_STATE.ON_CHART) {
				/*
				 * Remove the question from Chart.
				 */
				Chart.removeFromChart(q);
				
				/*
				 * Handover the question to the immediate supporting member.
				 * Set status as ASSISTANT_PROCESSED.
				 */
				Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				q.setPrimaryMember(member);
				q.setInternalStatus(ASSISTANT_PROCESSED);
				q.merge();
				
				/*
				 * Since the supporting member has become primary member, he/she 
				 * is no longer a supporting member.
				 */
				Question.removeAsSupportingMember(q, member);
				
				/*
				 * Add to chart if applicable
				 */
				Chart.addToChart(q);
			}
			else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				
				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart chart = Chart.find(q);
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Handover the question to the immediate supporting member.
				 */
				q.setPrimaryMember(member);
				q.merge();
				
				/*
				 * Since the supporting member has become primary member, he/she 
				 * is no longer a supporting member.
				 */
				Question.removeAsSupportingMember(q, member);
				
				/*
				 * Since the question is already in the processing cycle, forcefully
				 * add it to Chart
				 */
				Chart.forcefullyAddToChart(chart, q);
				
				/*
				 * Start workflow for Question q at exactly the same point where 
				 * it was previously terminated.
				 */				
				Status internalStatus = q.getInternalStatus();
				if(wfDetails != null) {
					WorkflowDetails.startProcessAtGivenLevel(q, 
							ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
				}
			}
			else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				
				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart chart = Chart.find(q);
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Handover the question to the immediate supporting member.
				 */
				q.setPrimaryMember(member);
				q.merge();
				
				/*
				 * Since the supporting member has become primary member, he/she 
				 * is no longer a supporting member.
				 */
				Question.removeAsSupportingMember(q, member);
				
				/*
				 * Since the question is already in the processing cycle, forcefully
				 * add it to Chart
				 */
				Chart.forcefullyAddToChart(chart, q);
				
				/*
				 * Start workflow for Question q at exactly the same point where 
				 * it was previously terminated.
				 */				
				Status internalStatus = q.getInternalStatus();
				if(wfDetails != null) {
					WorkflowDetails.startProcessAtGivenLevel(q, 
							ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
				}
			}
			else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);
				
				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart
				 */
				Chart chart = Chart.find(q);
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Remove q from Ballot and PreBallot.
				 */
				boolean isResequenceDevices = true;
				Ballot revisedBallot = Ballot.remove(q, isResequenceDevices);
				
				/*
				 * Handover. Hardcoding noOfRounds for now. Read it from Ballot.
				 */
				List<BallotEntry> ballotEntries = revisedBallot.getBallotEntries();
				Member handoveredToMember = 
						Question.questionHandover(q, ballotEntries, onDate, 3, locale);
				if(handoveredToMember != null) {
					revisedBallot.setBallotEntries(ballotEntries);
					revisedBallot.merge();
					
					q.setPrimaryMember(handoveredToMember);
					q.simpleMerge();
					
					/*
					 * Since the supporting member has become primary member, he/she 
					 * is no longer a supporting member.
					 */
					Question.removeAsSupportingMember(q, member);
					
					// Forcefully add to Chart
					Chart.forcefullyAddToChart(chart, q);
					
					/*
					 * Start workflow for Question q at exactly the same point where 
					 * Question q's workflow ended.
					 */
					Status internalStatus = q.getInternalStatus();
					if(wfDetails != null) {
						WorkflowDetails.startProcessAtGivenLevel(q, 
								ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
					}
				}
				else {
					/*
					 * Set status as lapsed.
					 */
					Status LAPSED = Status.findByType(
							ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
					q.setInternalStatus(LAPSED);
					q.setRecommendationStatus(LAPSED);
					q.setStatus(LAPSED);
					q.merge();
				}			
			}
		}
		else if(clubbingState == CLUBBING_STATE.PARENT) { 
			// if(qnState == STARRED_STATE.PRE_CHART) { } This case will never arise.
			if(qnState == STARRED_STATE.ON_CHART) {
				/*
				 * Remove the question from Chart.
				 */
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Handover the question to the immediate supporting member.
				 * Set status as ASSISTANT_PROCESSED.
				 */
				Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				q.setPrimaryMember(member);
				q.setInternalStatus(ASSISTANT_PROCESSED);
				q.merge();
				
				/*
				 * Since the supporting member has become primary member, he/she 
				 * is no longer a supporting member.
				 */
				Question.removeAsSupportingMember(q, member);
								
				/*
				 * Add to chart. If it stays on Chart then the clubbed entities assigned to it are fine.
				 * If it leaves the Chart then set its clubbings as empty and make the immediate clubbed 
				 * entity as new parent.
				 */
				boolean isAddedToChart = Chart.addToChart(q);
				if(! isAddedToChart) {
					List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(q);
					
					// Set clubbedEntities as empty
					q.setClubbedEntities(new ArrayList<ClubbedEntity>());
					q.simpleMerge();
					
					// Immediate clubbed question will become parent question.
					ClubbedEntity immediateClubbedEntity = clubbedEntities.get(0);
					Question newParent = immediateClubbedEntity.getQuestion();
					clubbedEntities.remove(0);
					
					newParent.setClubbedEntities(clubbedEntities);
					newParent.merge();
				}
			}
			else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);

				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart chart = Chart.find(q);
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Handover the question to the immediate supporting member.
				 */
				q.setPrimaryMember(member);
				q.merge();
				
				/*
				 * Since the supporting member has become primary member, he/she 
				 * is no longer a supporting member.
				 */
				Question.removeAsSupportingMember(q, member);
				
				/*
				 * Since the question is already in the processing cycle, forcefully
				 * add it to Chart
				 */
				Chart.forcefullyAddToChart(chart, q);
				
				/*
				 * Start workflow for Question q at exactly the same point where 
				 * it was previously terminated.
				 */				
				Status internalStatus = q.getInternalStatus();
				if(wfDetails != null) {
					WorkflowDetails.startProcessAtGivenLevel(q, 
							ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
				}
			}
			else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);

				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart chart = Chart.find(q);
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Handover the question to the immediate supporting member.
				 */
				q.setPrimaryMember(member);
				q.merge();
				
				/*
				 * Since the supporting member has become primary member, he/she 
				 * is no longer a supporting member.
				 */
				Question.removeAsSupportingMember(q, member);
				
				/*
				 * Since the question is already in the processing cycle, forcefully
				 * add it to Chart
				 */
				Chart.forcefullyAddToChart(chart, q);
				
				/*
				 * Start workflow for Question q at exactly the same point where 
				 * it was previously terminated.
				 */				
				Status internalStatus = q.getInternalStatus();
				if(wfDetails != null) {
					WorkflowDetails.startProcessAtGivenLevel(q, 
							ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
				}
			}
			else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
				/*
				 * Stop the workflow.
				 */
				WorkflowDetails wfDetails = 
						WorkflowDetails.findCurrentWorkflowDetail(q);

				String workflowType = null;
				String assignee = null;
				Integer assigneeLevel = null;
				if(wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					workflowType = wfDetails.getWorkflowType();
					assignee = wfDetails.getAssignee();
					assigneeLevel = 
						Integer.parseInt(wfDetails.getAssigneeLevel());
					
					try {
						WorkflowDetails.endProcess(wfDetails);
					}
					catch(Exception e) {
						
					}
					q.removeExistingWorkflowAttributes();
				}
				
				/*
				 * Remove the question from Chart.
				 */
				Chart chart = Chart.find(q);
				Chart.simpleRemoveFromChart(q);
				
				/*
				 * Remove q from Ballot and PreBallot.
				 */
				boolean isResequenceDevices = true;
				Ballot revisedBallot = Ballot.remove(q, isResequenceDevices);
				
				/*
				 * Handover. Hardcoding noOfRounds for now. Read it from Ballot.
				 */
				List<BallotEntry> ballotEntries = revisedBallot.getBallotEntries();
				Member handoveredToMember = 
						Question.questionHandover(q, ballotEntries, onDate, 3, locale);
				if(handoveredToMember != null) {
					revisedBallot.setBallotEntries(ballotEntries);
					revisedBallot.merge();
					
					q.setPrimaryMember(handoveredToMember);
					q.simpleMerge();
					
					/*
					 * Since the supporting member has become primary member, he/she 
					 * is no longer a supporting member.
					 */
					Question.removeAsSupportingMember(q, member);
					
					// Forcefully add to Chart
					Chart.forcefullyAddToChart(chart, q);
					
					/*
					 * Start workflow for Question q at exactly the same point where 
					 * Question q's workflow ended.
					 */
					Status internalStatus = q.getInternalStatus();
					if(wfDetails != null) {
						WorkflowDetails.startProcessAtGivenLevel(q, 
								ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, assignee, assigneeLevel, locale);
					}
				}
				else {
					/*
					 * Set status as lapsed.
					 */
					Status LAPSED = Status.findByType(
							ApplicationConstants.QUESTION_SYSTEM_LAPSED, locale);
					q.setInternalStatus(LAPSED);
					q.setRecommendationStatus(LAPSED);
					q.setStatus(LAPSED);
					q.merge();
				}	
			}
		}
		else  {  // clubbingState == CLUBBING_STATE.CLUBBED			
			// TODO
		}
	}
	
	private static Member questionHandover(final Question q,
			final List<BallotEntry> ballotEntries,
			final Date onDate,
			final Integer noOfRounds,
			final String locale) {
		List<Member> supportingMembers = Question.eligibleSupportingMembers(q, onDate, locale);
		
		for(Member m : supportingMembers) {
			boolean isHandovered = 
				Question.handoverQuestionToMember(q, m, ballotEntries, 
						noOfRounds, locale);
			if(isHandovered) {
				return m;
			}
		}
		
		return null;
	}
	
	private static List<Member> eligibleSupportingMembers(final Question question,
			final Date onDate,
			final String locale) {
		List<Member> members = new ArrayList<Member>();
		
		List<Member> immediateSupportingMembers = 
			Question.immediateSupportingMembers(question, onDate, locale);
		members.addAll(immediateSupportingMembers);
		
		List<ClubbedEntity> clubbings = Question.findClubbedEntitiesByPosition(question);
		if(clubbings != null) {
			for(ClubbedEntity ce : clubbings) {
				Question q = ce.getQuestion();
				
				String internalStatus = q.getInternalStatus().getType();
				if(internalStatus.equals(
						ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
					Member primaryMember = q.getPrimaryMember();
					members.add(primaryMember);
					
					List<Member> sms = Question.immediateSupportingMembers(q, onDate, locale);
					members.addAll(sms);
				}
			}
		}
		
		return members;
	}
	
	private static List<Member> immediateSupportingMembers(final Question question,
			final Date onDate,
			final String locale) {
		List<Member> members = new ArrayList<Member>();
		
		List<SupportingMember> supportingMembers = question.getSupportingMembers();
		if(supportingMembers != null) {
			for(SupportingMember sm : supportingMembers) {
				boolean isApprovedSupportingMember = 
					sm.getDecisionStatus().getType().equals(
						ApplicationConstants.SUPPORTING_MEMBER_APPROVED);
				if(isApprovedSupportingMember) {
					Member member = sm.getMember();
					
					boolean isActiveOnlyAsMember = 
							Question.isActiveOnlyAsMember(member, onDate, locale);
					if(isActiveOnlyAsMember) {
						members.add(member);
					}
				}
			}
		}
		
		return members;
	}
	
	private static boolean handoverQuestionToMember(final Question question,
			final Member member,
			final List<BallotEntry> ballotEntries,
			final Integer noOfRounds,
			final String locale) {
		boolean isHandovered = false;
		
		BallotEntry ballotEntry = Ballot.findBallotEntry(ballotEntries, member);
		if(ballotEntry == null) {
			List<DeviceSequence> deviceSequences = 
				Ballot.createDeviceSequences(question, locale);
			ballotEntry = new BallotEntry(member, deviceSequences, locale);
			ballotEntries.add(ballotEntry);
			isHandovered = true;
		}
		else {
			List<DeviceSequence> deviceSequences = ballotEntry.getDeviceSequences();
			int size = deviceSequences.size();
			if(size < noOfRounds) {
				DeviceSequence sequence = new DeviceSequence(question, locale);
				deviceSequences.add(sequence);
				isHandovered = true;
			}
		}
		
		return isHandovered;
	}

	/**
	 * For Unstarred Questions consider the Questions from latest session whose yaadi is not laid.
	 */
	private static void handoverUnstarred(final Member member, final Session session, final Date onDate, 
			final String locale) throws ELSException {
		// TODO
	}
	
	/**
	 * For Short Notices @param onDate should be between submissionStartTime and sessionEndDate for the session.
	 */
	private static void handoverShortNotice(final Member member, final Session session, final Date onDate, 
			final String locale) throws ELSException {
		// TODO
	}
	
	/**
	 * For Half hour from Questions @param onDate should be between submissionStartTime and sessionEndDate for the session.
	 */
	private static void handoverHalfFourFromQuestion(final Member member, final Session session, 
			final Date onDate, final String locale) throws ELSException {
		// TODO
	}
	
	// TODO: Test and Move this method to Member.
	private static House find(final Member member, final Date onDate, final String locale) {
		House house = null;
		
		// First check in Lowerhouse, then check in Upperhouse
		HouseType lowerHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
		MemberRole memberRole = MemberRole.find(lowerHouseType, "MEMBER", locale);
		
		HouseMemberRoleAssociation hmra = Member.find(member, memberRole, onDate, locale);
		if(hmra != null) {
			house = hmra.getHouse();
		}
		else {
			HouseType upperHouseType = HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale);
			memberRole = MemberRole.find(upperHouseType, "MEMBER", locale);
			
			hmra = Member.find(member, memberRole, onDate, locale);
			if(hmra != null) {
				house = hmra.getHouse();
			}
		}
		
		return house;
	}
	
	public static void removeAsSupportingMember(final Member member,
			final Session session, 
			final DeviceType deviceType) {
		String locale = session.getLocale();
		List<Question> questions = new ArrayList<Question>();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			/*
			 * Get all the Starred questions of @param session of @param deviceType whose 
			 * internalStatus >= "SUBMITTED" and recommendation status < "YAADI_LAID" where
			 * @param member is one of the supporting member
			 */
			Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, locale);
			Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale);
			
			questions = Question.findWhereMemberIsSupportingMember(member, session, deviceType, 
					GTEQinternalStatus, LTrecommendationStatus, locale);
		}
		else if(deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			/*
			 * Get all the Unstarred questions of @param session of @param deviceType whose 
			 * internalStatus >= "SUBMITTED" and recommendation status < "YAADI_LAID" where
			 * @param member is one of the supporting member
			 */
			Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT, locale);
			Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_YAADILAID, locale);
			
			questions = Question.findWhereMemberIsSupportingMember(member, session, deviceType, 
					GTEQinternalStatus, LTrecommendationStatus, locale);
		}
		else if(deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			/*
			 * Get all the Short Notice questions of @param session of @param deviceType whose 
			 * internalStatus >= "SUBMITTED" and recommendation status < "DISCUSSED" where
			 * @param member is one of the supporting member
			 */
			Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SUBMIT, locale);
			Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_DISCUSSED, locale);
			
			questions = Question.findWhereMemberIsSupportingMember(member, session, deviceType, 
					GTEQinternalStatus, LTrecommendationStatus, locale);
		}
		else if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			/*
			 * Get all the Unstarred questions of @param session of @param deviceType whose 
			 * internalStatus >= "SUBMITTED" and recommendation status < "DISCUSSED" where
			 * @param member is one of the supporting member
			 */
			Status GTEQinternalStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SUBMIT, locale);
			Status LTrecommendationStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_DISCUSSED, locale);
			
			questions = Question.findWhereMemberIsSupportingMember(member, session, deviceType, 
					GTEQinternalStatus, LTrecommendationStatus, locale);
		}
		
		// Remove @param member from all these questions as supporting member. Save a draft.		
		for(Question q : questions) {
			try {
				SupportingMember supportingMember = null;
				
				List<SupportingMember> supportingMembers = q.getSupportingMembers();
				int counter = -1;
				for(SupportingMember sm : supportingMembers) {
					++counter;
					Member m = sm.getMember();
					if(m.getId().equals(member.getId())) {
						supportingMember = sm;
					}
				}
				
				if(counter >= 0) {
					supportingMembers.remove(counter);
					q.setSupportingMembers(supportingMembers);
					q.merge();
				}
				
				if(supportingMember != null) {
					supportingMember.remove();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void removeAsSupportingMember(final Question question,
			final Member member) {
		List<SupportingMember> supportingMembers = question.getSupportingMembers();
		
		int counter = 0;
		for(SupportingMember sm : supportingMembers) {
			Member m = sm.getMember();
			if(m.getId().equals(member.getId())) {
				supportingMembers.remove(counter);
				sm.remove();
				break;
			}
			++counter;
		}
		
		question.setSupportingMembers(supportingMembers);
		question.merge();
	}
	
	private static Date getSubmissionStartTime(final Session session,
			final DeviceType deviceType, 
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(ApplicationConstants.QUESTION_STARRED_SUBMISSION_STARTTIME);
		String value = session.getParameter(key.toString());
		
		return Question.getFormattedTime(value, locale);
	}
	
	private static Date getFormattedTime(final String strTime,
			final String locale) {
		CustomParameter datePattern = 
			CustomParameter.findByName(CustomParameter.class, 
					"DB_TIMESTAMP", "");
		String datePatternValue = datePattern.getValue();
		
		Date formattedTime = FormaterUtil.formatStringToDate(strTime, 
				datePatternValue, locale);
		return formattedTime;
	}
	
	private static boolean isDateInBetween(final Date onDate, final Date startDate, final Date endDate) {
		if(onDate.compareTo(startDate) >= 0
				&& onDate.compareTo(endDate) <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Find all the Questions of @param member of type @param deviceType belonging to 
	 * @param session having internalStatus priority greater than equal to @param GTEQinternalStatus
	 * recommendationStatus priority less than @param LTrecommendationStatus
	 */
	private static List<Question> find(final Member member,
			final Session session, 
			final DeviceType deviceType,
			final Status GTEQinternalStatus,
			final Status LTrecommendationStatus,
			final String locale) {
		return Question.getQuestionRepository().find(member, session, deviceType, GTEQinternalStatus, LTrecommendationStatus, locale);
	}
	
	private static List<Question> findWhereMemberIsSupportingMember(final Member member, 
			final Session session, 
			final DeviceType deviceType,
			final Status GTEQinternalStatus, 
			final Status LTrecommendationStatus,
			final String locale) {
		return Question.getQuestionRepository().findWhereMemberIsSupportingMember(member, session, deviceType, 
				GTEQinternalStatus, LTrecommendationStatus, locale);
	}
	
	/**
	 * If @param question has supporingMember(s) then return the first active and approved supportingMember.
	 * If no such members could be found then return null.
	 */
	private static SupportingMember hasActiveSupportingMember(final Question question, 
			final Date onDate,
			final String locale) {
		List<SupportingMember> supportingMembers = question.getSupportingMembers();
		
		for(SupportingMember sm : supportingMembers) {
			String decisionStatusType = sm.getDecisionStatus().getType();
			if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
				Member member = sm.getMember();
				
				boolean isActiveMember = Question.isActiveOnlyAsMember(member, onDate, locale);
				if(isActiveMember) {
					return sm;
				}
			}
		}
		
		return null;
	}
	
	private static boolean isActiveOnlyAsMember(final Member member,
			final Date onDate,
			final String locale) {
		String[] memberRoles = new String[] {"SPEAKER", "DEPUTY_SPEAKER", "CHAIRMAN", "DEPUTY_CHAIRMAN"};
		
		boolean isActiveMinister = member.isActiveMinisterOn(onDate, locale);
		boolean isActivePresidingOfficer = member.isActiveMemberInAnyOfGivenRolesOn(memberRoles, onDate, locale);
		boolean isActiveMember = member.isActiveMemberOn(onDate, locale);
		
		if(isActiveMember &&
				! isActiveMinister &&
				! isActivePresidingOfficer) {
			return true;
		}
		else {
			return false;
		}
	}


	public static QuestionDraft findLatestGroupChangedDraft(final Question question) {
		return Question.getQuestionRepository().findLatestGroupChangedDraft(question);
	}


	public static QuestionDraft findGroupChangedDraft(Question question) {
		return Question.getQuestionRepository().findGroupChangedDraft(question);
	}
	
	public static List<Member> findMembersHavingQuestionSubmittedInFirstBatch(final Session session, final DeviceType deviceType, final String locale) throws ELSException {
		return getQuestionRepository().findMembersHavingQuestionSubmittedInFirstBatch(session, deviceType, locale);
	}
	
	public Workflow findWorkflowFromStatus() throws ELSException {
		if(this.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			return this.findWorkflowFromStatusForStarredQuestion();
		} else if(this.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			return this.findWorkflowFromStatusForUnstarredQuestion();
		} else if(this.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			return this.findWorkflowFromStatusForShortNoticeQuestion();
		} else if(this.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			return this.findWorkflowFromStatusForHDQ();
		}
		return null;
	}
	
	private Workflow findWorkflowFromStatusForStarredQuestion() throws ELSException {
		Workflow workflow = null;
		
		Status internalStatus = this.getInternalStatus();
		Status recommendationStatus = this.getRecommendationStatus();
		String recommendationStatusType = recommendationStatus.getType();

		if(recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
			workflow = Workflow.findByStatus(recommendationStatus, this.getLocale());
		
		} else {
			workflow = Workflow.findByStatus(internalStatus, this.getLocale());											
		}
		
		return workflow;
	}
	
	private Workflow findWorkflowFromStatusForUnstarredQuestion() throws ELSException {
		Workflow workflow = null;
		
		Status internalStatus = this.getInternalStatus();
		Status recommendationStatus = this.getRecommendationStatus();
		String recommendationStatusType = recommendationStatus.getType();

		if(recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
			workflow = Workflow.findByStatus(recommendationStatus, this.getLocale());
		
		} else {
			workflow = Workflow.findByStatus(internalStatus, this.getLocale());											
		}
		
		return workflow;
	}
	
	private Workflow findWorkflowFromStatusForShortNoticeQuestion() throws ELSException {
		Workflow workflow = null;
		
		Status internalStatus = this.getInternalStatus();
		Status recommendationStatus = this.getRecommendationStatus();
		String recommendationStatusType = recommendationStatus.getType();

		if(recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
			workflow = Workflow.findByStatus(recommendationStatus, this.getLocale());
		
		} else {
			workflow = Workflow.findByStatus(internalStatus, this.getLocale());											
		}
		
		return workflow;
	}
	
	private Workflow findWorkflowFromStatusForHDQ() throws ELSException {
		Workflow workflow = null;
		
		Status internalStatus = this.getInternalStatus();
		Status recommendationStatus = this.getRecommendationStatus();
		String recommendationStatusType = recommendationStatus.getType();

		if(recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
			workflow = Workflow.findByStatus(recommendationStatus, this.getLocale());
		
		} else {
			workflow = Workflow.findByStatus(internalStatus, this.getLocale());											
		}
		
		return workflow;
	}
	
	public static Status findCorrespondingStatusForGivenQuestionType(final Status inputStatus, final DeviceType questionType) throws ELSException {
		if(inputStatus==null || questionType==null) {
			throw new ELSException("Question.findStatusForGivenQuestionType/2", "input status or device type is null");
		}
		String requiredStatusType = inputStatus.getType();
		String requiredQuestionType = questionType.getType();
		if(requiredStatusType.startsWith(ApplicationConstants.STATUS_INITIAL_UNSTARRED_QUESTION)) {
			requiredStatusType = requiredStatusType.split(ApplicationConstants.STATUS_INITIAL_UNSTARRED_QUESTION)[1];
		} else if(requiredStatusType.startsWith(ApplicationConstants.STATUS_INITIAL_SHORTNOTICE_QUESTION)) {
			requiredStatusType = requiredStatusType.split(ApplicationConstants.STATUS_INITIAL_SHORTNOTICE_QUESTION)[1];
		} else if(requiredStatusType.startsWith(ApplicationConstants.STATUS_INITIAL_HDQ_QUESTION)) {
			requiredStatusType = requiredStatusType.split(ApplicationConstants.STATUS_INITIAL_HDQ_QUESTION)[1];
		} else {
			requiredStatusType = requiredStatusType.split(ApplicationConstants.STATUS_INITIAL_STARRED_QUESTION)[1];
		}
		if(requiredQuestionType.equals(ApplicationConstants.STARRED_QUESTION)) {
			requiredStatusType = ApplicationConstants.STATUS_INITIAL_STARRED_QUESTION + requiredStatusType;
		} else if(requiredQuestionType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			requiredStatusType = ApplicationConstants.STATUS_INITIAL_UNSTARRED_QUESTION + requiredStatusType;
		} else if(requiredQuestionType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			requiredStatusType = ApplicationConstants.STATUS_INITIAL_SHORTNOTICE_QUESTION + requiredStatusType;
		} else if(requiredQuestionType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			requiredStatusType = ApplicationConstants.STATUS_INITIAL_HDQ_QUESTION + requiredStatusType;
		}
		return Status.findByType(requiredStatusType, inputStatus.getLocale());
	}
	
	public static List<Object[]> findUnstarredAcrossSessionDepartmentwiseQuestions(final String sessionIds, final String locale) {
		return getQuestionRepository().findUnstarredAcrossSessionDepartmentwiseQuestions(sessionIds, locale);
	}
	
	public static int updateUnBallot(final Member member, final Session session, 
			final DeviceType deviceType, final Status internalStatus, final Date discussionDate){
		return getQuestionRepository().updateUnBallot(member, session, deviceType, internalStatus, discussionDate);
	}
	
	public String findYaadiDetailsText() {
		String yaadiDetailsText = "";
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{this.getLocale()});
		parametersMap.put("questionId", new String[]{this.getId().toString()});
		@SuppressWarnings("rawtypes")
		List yaadiDetailsTextResult = org.mkcl.els.domain.Query.findReport("QUESTION_YADI_DETAILS_TEXT", parametersMap);
		if(yaadiDetailsTextResult!=null && !yaadiDetailsTextResult.isEmpty()) {
			if(yaadiDetailsTextResult.get(0)!=null) {
				yaadiDetailsText = yaadiDetailsTextResult.get(0).toString();
			}
		}
		return yaadiDetailsText;
	}
	
	public String findDiscussionDetailsText() {
		String discussionDetailsText = "";
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{this.getLocale()});
		parametersMap.put("questionId", new String[]{this.getId().toString()});
		@SuppressWarnings("rawtypes")
		List discussionDetailsTextResult = org.mkcl.els.domain.Query.findReport("QUESTION_DISCUSSION_DETAILS_TEXT", parametersMap);
		if(discussionDetailsTextResult!=null && !discussionDetailsTextResult.isEmpty()) {
			if(discussionDetailsTextResult.get(0)!=null) {
				discussionDetailsText = discussionDetailsTextResult.get(0).toString();
			}
		}
		return discussionDetailsText;
	}
	
	public String findPreviousSessionUnstarredParentDetailsText() {
		String previousSessionUnstarredParentDetailsText = "";
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{this.getLocale()});
		parametersMap.put("questionId", new String[]{this.getId().toString()});
		@SuppressWarnings("rawtypes")
		List previousSessionUnstarredParentDetailsTextResult = org.mkcl.els.domain.Query.findReport("QUESTION_PREVIOUS_SESSION_UNSTARRED_PARENT_DETAILS_TEXT", parametersMap);
		if(previousSessionUnstarredParentDetailsTextResult!=null && !previousSessionUnstarredParentDetailsTextResult.isEmpty()) {
			if(previousSessionUnstarredParentDetailsTextResult.get(0)!=null) {
				previousSessionUnstarredParentDetailsText = previousSessionUnstarredParentDetailsTextResult.get(0).toString();
			}
		}
		return previousSessionUnstarredParentDetailsText;
	}
	
	public String findReferencingDetailsText() {
		String referencingDetailsText = "";
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{this.getLocale()});
		parametersMap.put("questionId", new String[]{this.getId().toString()});
		@SuppressWarnings("rawtypes")
		List referencingDetailsTextResult = org.mkcl.els.domain.Query.findReport("QUESTION_REFERENCING_DETAILS_TEXT", parametersMap);
		if(referencingDetailsTextResult!=null && !referencingDetailsTextResult.isEmpty()) {
			if(referencingDetailsTextResult.get(0)!=null) {
				referencingDetailsText = referencingDetailsTextResult.get(0).toString();
			}
		}
		return referencingDetailsText;
	}
	
	public static List<Question> sort(final List<Question> questions, final String sortField, final String sortOrder) {
		List<Question> sortedQuestions = null;
		if(questions!=null) {
			sortedQuestions = new ArrayList<Question>();
			if(!questions.isEmpty()) {
				sortedQuestions.addAll(questions);
				if(sortField!=null && !sortField.isEmpty()) {
					if(sortField.equals("number")) {
						Comparator<Question> c = new Comparator<Question>() {
							@Override
							public int compare(final Question q1, final Question q2) {
								if(sortOrder.equals(ApplicationConstants.DESC)) {
									return q2.getNumber().compareTo(q1.getNumber());
								} else {
									return q1.getNumber().compareTo(q2.getNumber());
								}
							}
						};
						Collections.sort(sortedQuestions, c);					
					}
				}
			}			
		}			
		return sortedQuestions;
	}
	
	public String findShortDetailsTextForYaadi(Boolean isFilteredOnGroup) {
		String shortDetailsText = "";
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{this.getLocale()});
		parametersMap.put("questionId", new String[]{this.getId().toString()});
		parametersMap.put("isFilteredOnGroup", new String[]{isFilteredOnGroup.toString()});
		List shortDetailsTextResult = org.mkcl.els.domain.Query.findReport("QUESTION_SHORT_DETAILS_TEXT_FOR_YAADI", parametersMap);
		if(shortDetailsTextResult!=null && !shortDetailsTextResult.isEmpty()) {
			if(shortDetailsTextResult.get(0)!=null) {
				shortDetailsText = shortDetailsTextResult.get(0).toString();
			}
		}
		return shortDetailsText;
	}
	
	public String restoreQuestionTextBeforeClubbing() {
		return getQuestionRepository().restoreQuestionTextBeforeClubbing(this);
	}
	
	public Question copyQuestion(){
		Question q = new Question();
		
		q.setId(q.getId());
		q.setLocale(this.getLocale());
		q.setNumber(this.getNumber());
		q.setLevel(this.getLevel());
		
		q.setOriginalType(this.getOriginalType());
		q.setType(this.getType());
		
		q.setPrimaryMember(this.getPrimaryMember());
		q.setAnswer(this.getAnswer());
		
		q.setSubject(this.getSubject());
		q.setQuestionText(this.getQuestionText());
		q.setRevisedSubject(this.getRevisedSubject());
		q.setRevisedQuestionText(this.getRevisedQuestionText());
		
		q.setAnsweringDate(this.getAnsweringDate());
		q.setChartAnsweringDate(this.getChartAnsweringDate());
		
		q.setGroup(this.getGroup());
		q.setParent(this.getParent());
		q.setClubbedEntities(this.getClubbedEntities());
		
		q.setSubDepartment(this.getSubDepartment());
		q.setMinistry(this.getMinistry());
		
		q.setCreatedBy(this.getCreatedBy());
		q.setCreationDate(this.getCreationDate());
		
		q.setDataEnteredBy(this.getDataEnteredBy());
		q.setDrafts(this.getDrafts());
		
		q.setInternalStatus(this.getInternalStatus());
		q.setRecommendationStatus(this.getRecommendationStatus());
		q.setStatus(this.getStatus());
		
		return q;
	}
	
	public static QuestionDraft addDraft(Question q, String editedBy, String editedAs, String remark) {

		QuestionDraft draft = new QuestionDraft();
		draft.setQuestionId(q.getId());
		draft.setLocale(q.getLocale());
		draft.setType(q.getType());
		draft.setAnsweringDate(q.getAnsweringDate());
		draft.setAnswer(q.getAnswer());
		draft.setRemarks(remark);

		draft.setParent(q.getParent());
		draft.setClubbedEntities(q.getClubbedEntities());
		draft.setReferencedEntities(q.getReferencedEntities());

		draft.setEditedAs(editedAs);
		draft.setEditedBy(editedBy);
		draft.setEditedOn(new Date());

		draft.setGroup(q.getGroup());
		draft.setMinistry(q.getMinistry());
		draft.setSubDepartment(q.getSubDepartment());

		draft.setStatus(q.getStatus());
		draft.setInternalStatus(q.getInternalStatus());
		draft.setRecommendationStatus(q.getRecommendationStatus());

		if (q.getRevisedQuestionText() != null
				&& q.getRevisedSubject() != null) {
			draft.setQuestionText(q.getRevisedQuestionText());
			draft.setSubject(q.getRevisedSubject());
		} else if (q.getRevisedQuestionText() != null) {
			draft.setQuestionText(q.getRevisedQuestionText());
			draft.setSubject(q.getSubject());
		} else if (q.getRevisedSubject() != null) {
			draft.setQuestionText(q.getQuestionText());
			draft.setSubject(q.getRevisedSubject());
		} else {
			draft.setQuestionText(q.getQuestionText());
			draft.setSubject(q.getSubject());
		}		
		
		if(q.getProcessed() != null){
			draft.setProcessed(q.getProcessed());
		}
		return draft;
	}
	
	private static boolean findAllowedInBatch(final Question question, final Date date, final Date startDate, final Date endDate){
    	boolean retVal = false; 
    	
    	if((date.compareTo(startDate) > 0 || date.compareTo(startDate) == 0) && (date.compareTo(endDate) < 0 || date.compareTo(endDate) == 0)){
    		retVal = true;
    	}
    	
    	return retVal;
    }
    
    
    public static boolean allowedInFirstBatch(final Question question, final Date date){
    	    	
    	Session session = question.getSession();
    	Date submissionFirstBatchStartDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionFirstBatchStartDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    	Date submissionFirstBatchEndDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionFirstBatchEndDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    	if(submissionFirstBatchEndDate != null && submissionFirstBatchStartDate != null){
    		return findAllowedInBatch(question, date, submissionFirstBatchStartDate, submissionFirstBatchEndDate);
    	}else{
    		return false;
    	}
    	
    	
    }
    
    public static boolean allowedInFirstBatchForMaxCountPerMember(final Question question){
    	
    	return getQuestionRepository().isQuestionAllowedInFirstBatchForMaxCountPerMember(question);
    	
    }
    
    public static boolean allowedInSecondBatch(final Question question, final Date date){
    	
    	Session session = question.getSession();
    	Date submissionSecondBatchStartDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionSecondBatchStartDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    	Date submissionSecondBatchEndDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionSecondBatchEndDate"), ApplicationConstants.DB_DATETIME_FORMAT);;
    	if(submissionSecondBatchStartDate != null && submissionSecondBatchEndDate != null){
    		return findAllowedInBatch(question, date, submissionSecondBatchStartDate, submissionSecondBatchEndDate);
    	}else{
    		return false;
    	}
    	
    }
    
    public static Integer findBatch(final Question question, final Date date){
    	
    	Integer batch = 0;
    	
    	Session session = question.getSession();
    	
    	Date submissionFirstBatchStartDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionFirstBatchStartDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    	Date submissionFirstBatchEndDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionFirstBatchEndDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    	
    	Date submissionSecondBatchStartDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionSecondBatchStartDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    	Date submissionSecondBatchEndDate = FormaterUtil.formatStringToDate(session.getParameter(question.getOriginalType().getType() + "_" + "submissionSecondBatchEndDate"), ApplicationConstants.DB_DATETIME_FORMAT);
    	
    	if((date.compareTo(submissionFirstBatchStartDate) > 0 || date.compareTo(submissionFirstBatchStartDate) == 0) && (date.compareTo(submissionFirstBatchEndDate) < 0 || date.compareTo(submissionFirstBatchEndDate) == 0)){
    		batch = 1;
    	}
    	
    	if(!(batch > 0)){
    		if((date.compareTo(submissionSecondBatchStartDate) > 0 || date.compareTo(submissionSecondBatchStartDate) == 0) && (date.compareTo(submissionSecondBatchEndDate) < 0 || date.compareTo(submissionSecondBatchEndDate) == 0)){
    			batch = 2;
    		}
    	}
    	
    	return batch;
    }
    
    public void startWorkflow(final Question question, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
    	//end current workflow if exists
		question.endWorkflow(question, workflowHouseType, locale);		
    	//update question statuses & devicetype as per the workflow status
    	question.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
		//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(question, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
    
    public void endWorkflow(final Question question, final String workflowHouseType, final String locale) throws ELSException {
    	WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question);
		if(wfDetails != null) {
			try {
				WorkflowDetails.endProcess(wfDetails);
			} catch(Exception e) {
				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
				wfDetails.setCompletionTime(new Date());
				wfDetails.merge();
			} finally {
				question.removeExistingWorkflowAttributes();
			}
		} else {
			question.removeExistingWorkflowAttributes();
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
    	}
    	/** update devicetype for the required flow **/
		if(status.getType().startsWith(ApplicationConstants.STATUS_INITIAL_UNSTARRED_QUESTION)) {
			DeviceType deviceType = DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, locale);
			this.setType(deviceType);
		} else if(status.getType().startsWith(ApplicationConstants.STATUS_INITIAL_SHORTNOTICE_QUESTION)) {
			DeviceType deviceType = DeviceType.findByType(ApplicationConstants.SHORT_NOTICE_QUESTION, locale);
			this.setType(deviceType);
		} else if(status.getType().startsWith(ApplicationConstants.STATUS_INITIAL_HDQ_QUESTION)) {
			DeviceType deviceType = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION, locale);
			this.setType(deviceType);
		} else {
			DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
			this.setType(deviceType);
		}
		this.simpleMerge();
    }
    
    
    public static void onSubdepartmentChange(final Question question,
    		final SubDepartment subdepartment) throws ELSException {
    	DeviceType deviceType = question.getType();
    	String deviceTypeType = deviceType.getType();
    	
    	if(deviceTypeType.equals(
    			ApplicationConstants.STARRED_QUESTION)) {
    		Question.onStarredSubdepartmentChange(question, subdepartment);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants.UNSTARRED_QUESTION)) {
    		Question.onUnstarredSubdepartmentChange(question, subdepartment);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    		Question.onShortSubdepartmentChange(question, subdepartment);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants
    				.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
    		Question.onHalfHourSubdepartmentChange(question, subdepartment);
    	}
    	else {
    		throw new ELSException("Question.onGroupChange/2", 
    				"Method invoked for inappropriate device type.");
    	}
    }


	private static void onHalfHourSubdepartmentChange(Question question,
			SubDepartment subdepartment2) {
		// TODO Auto-generated method stub
		
	}


	private static void onShortSubdepartmentChange(Question question,
			SubDepartment subdepartment2) {
		// TODO Auto-generated method stub
		
	}


	private static void onUnstarredSubdepartmentChange(Question question,
			SubDepartment subdepartment2) throws ELSException {
		String locale = question.getLocale();
		CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	UNSTARRED_STATE qnState = Question.findUnstarredState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onUnStarredGroupChangeLH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == UNSTARRED_STATE.PRE_WORKFLOW) {
    			// Change status to "GROUP_CHANGED"
//    			question.setInternalStatus(GROUP_CHANGED);
//    			question.setRecommendationStatus(GROUP_CHANGED);
//    			question.merge();
    		}
    		else if(qnState == UNSTARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
//    			WorkflowDetails wfDetails = 
//    				WorkflowDetails.findCurrentWorkflowDetail(question);
//    			WorkflowDetails.endProcess(wfDetails);
//    			question.removeExistingWorkflowAttributes();
//    			
//    			// Change status to "GROUP_CHANGED"
//    			question.setInternalStatus(GROUP_CHANGED);
//    			question.setRecommendationStatus(GROUP_CHANGED);
//    			question.merge();
    		}
    		else if(qnState == UNSTARRED_STATE.POST_FINAL_AND_PRE_YAADI_LAID) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			
    			// Before ending wfDetails process collect information
    			// which will be useful for creating a new process later.
    			String workflowType = wfDetails.getWorkflowType();
    			Integer assigneeLevel = 
    				Integer.parseInt(wfDetails.getAssigneeLevel());
    			String userGroupType = wfDetails.getAssigneeUserGroupType();
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			/*
    			 * Change recommendation status to final (internal) status.
    			 */
    			Status internalStatus = question.getInternalStatus();

    			/*
    			 * Conditional invocation of Chart.groupChange/3
    			 */
    			
    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
    				userGroupType = ApplicationConstants.DEPARTMENT;
    				assigneeLevel = assigneeLevel - 1;
    			}
    			/*
    			 * Start the workflow at Assistant (after Speaker) level.
    			 */
    			WorkflowDetails.startProcessAtGivenLevel(question, 
    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
    					userGroupType, assigneeLevel, 
    					locale);
    		}
    	}else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
        			Question.isHavingIllegalChild(question);
        		if(isHavingIllegalChild) {
        			throw new ELSException(
        					"Question.onUnstarredMinistryChangeCommon/2", 
            				"Question has clubbings which are still in the" +
            				" approval workflow. Group change is not allowed" +
            				" in such an inconsistent state.");
        		}
        		else {
        			if(qnState == UNSTARRED_STATE.PRE_WORKFLOW) {
        				/*
//        				 * Change parent's status to GROUP_CHANGED.
//        				 */
//        				question.setInternalStatus(GROUP_CHANGED);
//            			question.setRecommendationStatus(GROUP_CHANGED);
//            			question.merge();
//            			
//            			/* Parent's group & related information 
//        				 * has already changed. Perform the same on Kids.
//        				 */
//            			Group group = question.getGroup();
            			Ministry ministry = question.getMinistry();
            			SubDepartment subDepartment = question.getSubDepartment();
            			
            			List<Question> clubbings = Question.findClubbings(question);
            			for(Question kid : clubbings) {
//            				kid.setGroup(group);
            				kid.setMinistry(ministry);
            				kid.setSubDepartment(subDepartment);           			
                			kid.merge();
            			}
            		}
            		else if(qnState == UNSTARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
            			/*
        				 * Stop the question's workflow
        				 */
        				WorkflowDetails wfDetails = 
            				WorkflowDetails.findCurrentWorkflowDetail(question);
            			WorkflowDetails.endProcess(wfDetails);
            			question.removeExistingWorkflowAttributes();
            			
            			/*
        				 * Change parent's status to GROUP_CHANGED.
        				 */
//        				question.setInternalStatus(GROUP_CHANGED);
//            			question.setRecommendationStatus(GROUP_CHANGED);
//            			question.merge();
//            			
//            			/* Parent's group & related information 
//        				 * has already changed. Perform the same on Kids.
//        				 */
//            			Group group = question.getGroup();
            			Ministry ministry = question.getMinistry();
            			SubDepartment subDepartment = question.getSubDepartment();
            			
            			List<Question> clubbings = Question.findClubbings(question);
            			for(Question kid : clubbings) {
//            				kid.setGroup(group);
            				kid.setMinistry(ministry);
            				kid.setSubDepartment(subDepartment);           			
                			kid.merge();
            			}
            		}
            		else if(qnState == 
            			UNSTARRED_STATE.POST_FINAL_AND_PRE_YAADI_LAID) {
            			/*
        				 * Stop the question's workflow
        				 */
        				WorkflowDetails wfDetails = 
            				WorkflowDetails.findCurrentWorkflowDetail(question);
        				String userGroupType = wfDetails.getAssigneeUserGroupType();
            			// Before ending wfDetails process collect information
            			// which will be useful for creating a new process later.
            			String workflowType = wfDetails.getWorkflowType();
            			Integer assigneeLevel = 
            				Integer.parseInt(wfDetails.getAssigneeLevel());
            			
            			WorkflowDetails.endProcess(wfDetails);
            			question.removeExistingWorkflowAttributes();
            			
            			/*
            			 * Change recommendation status to final (internal) status.
            			 */
            			Status internalStatus = question.getInternalStatus();
            			question.setRecommendationStatus(internalStatus);
            			question.merge();
            			
            			/* Parent's group & related information 
        				 * has already changed. Perform the same on Kids.
        				 */
            			Group group = question.getGroup();
            			Ministry ministry = question.getMinistry();
            			SubDepartment subDepartment = question.getSubDepartment();
            			
            			List<Question> clubbings = Question.findClubbings(question);
            			for(Question kid : clubbings) {
            				kid.setGroup(group);
            				kid.setMinistry(ministry);
            				kid.setSubDepartment(subDepartment);           			
                			kid.merge();
            			}
            			
   			
            			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
            				userGroupType = ApplicationConstants.DEPARTMENT;
            				assigneeLevel = assigneeLevel - 1;
            			}
            			/*
        				 * Start the workflow at Assistant (after Speaker) level.
        				 */
            			WorkflowDetails.startProcessAtGivenLevel(question, 
            					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
            					userGroupType, assigneeLevel, 
            					locale);
            		}
        		}
    	}
		
	}


	private static void onStarredSubdepartmentChange(Question question,
			SubDepartment subdepartment) throws ELSException {
		String locale = question.getLocale();
		CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	STARRED_STATE qnState = Question.findStarredState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onStarredGroupChangeLH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == STARRED_STATE.PRE_CHART || qnState == STARRED_STATE.ON_CHART) {
    					question.setRecommendationStatus(question.getInternalStatus());
    		 			question.merge();
    		}else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL || qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			question.removeExistingWorkflowAttributes();
    			question.setRecommendationStatus(question.getInternalStatus());
    			question.merge();
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
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, question.getInternalStatus(), 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    		}/*
    		 * else if qnState == QUESTION_STATE.POST_BALLOT_AND_PRE_YAADI_LAID
    		 * then reset the group to its previous group number.
    		 */
    		else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID){
    			WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			question.removeExistingWorkflowAttributes();
        			question.setRecommendationStatus(question.getInternalStatus());
        			question.merge();
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
    	    			WorkflowDetails.startProcessAtGivenLevel(question, 
    	    					ApplicationConstants.APPROVAL_WORKFLOW, question.getInternalStatus(), 
    	    					userGroupType, assigneeLevel, 
    	    					locale);
        			}
    		}
    		
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			Question.isHavingIllegalChild(question);
    		if(isHavingIllegalChild) {
    			throw new ELSException("Question.onStarredGroupChangeLH/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == STARRED_STATE.ON_CHART) {
    				List<Question> clubbings = Question.findClubbings(question);
    				
    				// Unclub all the Questions
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
        			question.setRecommendationStatus(question.getInternalStatus());
    				question.merge();
        			
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
       			}
    			else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
    				
        			List<Question> clubbings = Question.findClubbings(question);
        			
        			/*
    				 * Unclub all the Questions
    				 */
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
        			question.setRecommendationStatus(question.getInternalStatus());
        			question.merge();
      			
        			Group group = question.getGroup();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setGroup(group);
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
    			}
    			else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			
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
    				question.setRecommendationStatus(question.getInternalStatus());
        			question.removeExistingWorkflowAttributes();
    			   	question.merge();
        			
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
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
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, question.getInternalStatus(), 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    			else if(qnState == 
    				STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			
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
    				question.setRecommendationStatus(question.getInternalStatus());
        			question.removeExistingWorkflowAttributes();
    			   	question.merge();
        			
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
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
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, question.getInternalStatus(), 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    		}
    	}
	}


	public static void onMinistryChange(Question question, Ministry prevMinistry) throws ELSException {
		DeviceType deviceType = question.getType();
    	String deviceTypeType = deviceType.getType();
    	
    	if(deviceTypeType.equals(
    			ApplicationConstants.STARRED_QUESTION)) {
    		Question.onStarredMinistryChange(question, prevMinistry);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants.UNSTARRED_QUESTION)) {
    		Question.onUnstarredMinistryChange(question, prevMinistry);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants.SHORT_NOTICE_QUESTION)) {
    		Question.onShortNoticeMinistryChange(question, prevMinistry);
    	}
    	else if(deviceTypeType.equals(
    			ApplicationConstants
    				.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
    		Question.onHalfHourMinistryChange(question, prevMinistry);
    	}
    	else {
    		throw new ELSException("Question.onMinistryChange/2", 
    				"Method invoked for inappropriate device type.");
    	}
		
	}


	private static void onHalfHourMinistryChange(Question question,
			Ministry prevMinistry) {
		// TODO Auto-generated method stub
		
	}


	private static void onShortNoticeMinistryChange(Question question,
			Ministry prevMinistry) {
		// TODO Auto-generated method stub
		
	}


	private static void onUnstarredMinistryChange(Question question,
			Ministry prevMinistry) throws ELSException {
		String locale = question.getLocale();
		CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	UNSTARRED_STATE qnState = Question.findUnstarredState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onUnStarredGroupChangeLH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == UNSTARRED_STATE.PRE_WORKFLOW) {
    			// Change status to "GROUP_CHANGED"
//    			question.setInternalStatus(GROUP_CHANGED);
//    			question.setRecommendationStatus(GROUP_CHANGED);
//    			question.merge();
    		}
    		else if(qnState == UNSTARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    			// Stop the workflow
//    			WorkflowDetails wfDetails = 
//    				WorkflowDetails.findCurrentWorkflowDetail(question);
//    			WorkflowDetails.endProcess(wfDetails);
//    			question.removeExistingWorkflowAttributes();
//    			
//    			// Change status to "GROUP_CHANGED"
//    			question.setInternalStatus(GROUP_CHANGED);
//    			question.setRecommendationStatus(GROUP_CHANGED);
//    			question.merge();
    		}
    		else if(qnState == UNSTARRED_STATE.POST_FINAL_AND_PRE_YAADI_LAID) {
    			/*
    			 * Stop the workflow
    			 */
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			
    			// Before ending wfDetails process collect information
    			// which will be useful for creating a new process later.
    			String workflowType = wfDetails.getWorkflowType();
    			Integer assigneeLevel = 
    				Integer.parseInt(wfDetails.getAssigneeLevel());
    			String userGroupType = wfDetails.getAssigneeUserGroupType();
    			WorkflowDetails.endProcess(wfDetails);
    			question.removeExistingWorkflowAttributes();
    			
    			/*
    			 * Change recommendation status to final (internal) status.
    			 */
    			Status internalStatus = question.getInternalStatus();

    			/*
    			 * Conditional invocation of Chart.groupChange/3
    			 */
    			
    			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
    				userGroupType = ApplicationConstants.DEPARTMENT;
    				assigneeLevel = assigneeLevel - 1;
    			}
    			/*
    			 * Start the workflow at Assistant (after Speaker) level.
    			 */
    			WorkflowDetails.startProcessAtGivenLevel(question, 
    					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
    					userGroupType, assigneeLevel, 
    					locale);
    		}
    	}else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
        			Question.isHavingIllegalChild(question);
        		if(isHavingIllegalChild) {
        			throw new ELSException(
        					"Question.onUnstarredMinistryChangeCommon/2", 
            				"Question has clubbings which are still in the" +
            				" approval workflow. Group change is not allowed" +
            				" in such an inconsistent state.");
        		}
        		else {
        			if(qnState == UNSTARRED_STATE.PRE_WORKFLOW) {
        				/*
//        				 * Change parent's status to GROUP_CHANGED.
//        				 */
//        				question.setInternalStatus(GROUP_CHANGED);
//            			question.setRecommendationStatus(GROUP_CHANGED);
//            			question.merge();
//            			
//            			/* Parent's group & related information 
//        				 * has already changed. Perform the same on Kids.
//        				 */
//            			Group group = question.getGroup();
            			Ministry ministry = question.getMinistry();
            			SubDepartment subDepartment = question.getSubDepartment();
            			
            			List<Question> clubbings = Question.findClubbings(question);
            			for(Question kid : clubbings) {
//            				kid.setGroup(group);
            				kid.setMinistry(ministry);
            				kid.setSubDepartment(subDepartment);           			
                			kid.merge();
            			}
            		}
            		else if(qnState == UNSTARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
            			/*
        				 * Stop the question's workflow
        				 */
        				WorkflowDetails wfDetails = 
            				WorkflowDetails.findCurrentWorkflowDetail(question);
            			WorkflowDetails.endProcess(wfDetails);
            			question.removeExistingWorkflowAttributes();
            			
            			/*
        				 * Change parent's status to GROUP_CHANGED.
        				 */
//        				question.setInternalStatus(GROUP_CHANGED);
//            			question.setRecommendationStatus(GROUP_CHANGED);
//            			question.merge();
//            			
//            			/* Parent's group & related information 
//        				 * has already changed. Perform the same on Kids.
//        				 */
//            			Group group = question.getGroup();
            			Ministry ministry = question.getMinistry();
            			SubDepartment subDepartment = question.getSubDepartment();
            			
            			List<Question> clubbings = Question.findClubbings(question);
            			for(Question kid : clubbings) {
//            				kid.setGroup(group);
            				kid.setMinistry(ministry);
            				kid.setSubDepartment(subDepartment);           			
                			kid.merge();
            			}
            		}
            		else if(qnState == 
            			UNSTARRED_STATE.POST_FINAL_AND_PRE_YAADI_LAID) {
            			/*
        				 * Stop the question's workflow
        				 */
        				WorkflowDetails wfDetails = 
            				WorkflowDetails.findCurrentWorkflowDetail(question);
        				String userGroupType = wfDetails.getAssigneeUserGroupType();
            			// Before ending wfDetails process collect information
            			// which will be useful for creating a new process later.
            			String workflowType = wfDetails.getWorkflowType();
            			Integer assigneeLevel = 
            				Integer.parseInt(wfDetails.getAssigneeLevel());
            			
            			WorkflowDetails.endProcess(wfDetails);
            			question.removeExistingWorkflowAttributes();
            			
            			/*
            			 * Change recommendation status to final (internal) status.
            			 */
            			Status internalStatus = question.getInternalStatus();
            			question.setRecommendationStatus(internalStatus);
            			question.merge();
            			
            			/* Parent's group & related information 
        				 * has already changed. Perform the same on Kids.
        				 */
            			Group group = question.getGroup();
            			Ministry ministry = question.getMinistry();
            			SubDepartment subDepartment = question.getSubDepartment();
            			
            			List<Question> clubbings = Question.findClubbings(question);
            			for(Question kid : clubbings) {
            				kid.setGroup(group);
            				kid.setMinistry(ministry);
            				kid.setSubDepartment(subDepartment);           			
                			kid.merge();
            			}
            			
   			
            			if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
            				userGroupType = ApplicationConstants.DEPARTMENT;
            				assigneeLevel = assigneeLevel - 1;
            			}
            			/*
        				 * Start the workflow at Assistant (after Speaker) level.
        				 */
            			WorkflowDetails.startProcessAtGivenLevel(question, 
            					ApplicationConstants.APPROVAL_WORKFLOW, internalStatus, 
            					userGroupType, assigneeLevel, 
            					locale);
            		}
        		}
    	}
	}


	private static void onStarredMinistryChange(Question question,
			Ministry prevMinistry) throws ELSException {
		String locale = question.getLocale();
		CLUBBING_STATE clubbingState = Question.findClubbingState(question);
    	STARRED_STATE qnState = Question.findStarredState(question);
    	
    	if(clubbingState == CLUBBING_STATE.CLUBBED) {
    		throw new ELSException("Question.onStarredGroupChangeLH/2", 
    				"Clubbed Question's group cannot be changed." +
    				" Unclub the question and then change the group.");
    	}
    	else if(clubbingState == CLUBBING_STATE.STANDALONE) {
    		if(qnState == STARRED_STATE.PRE_CHART || qnState == STARRED_STATE.ON_CHART) {
    			question.setRecommendationStatus(question.getInternalStatus());
    		 	question.merge();
    		}else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL || qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
    			WorkflowDetails wfDetails = 
    				WorkflowDetails.findCurrentWorkflowDetail(question);
    			question.removeExistingWorkflowAttributes();
    			question.setRecommendationStatus(question.getInternalStatus());
    			question.merge();
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
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, question.getInternalStatus(), 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    		}/*
    		 * else if qnState == QUESTION_STATE.POST_BALLOT_AND_PRE_YAADI_LAID
    		 * then reset the group to its previous group number.
    		 */
    		else if(qnState == STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID){
    			// TODO
    		}
    		
    	}
    	else { // clubbingState == CLUBBING_STATE.PARENT
    		boolean isHavingIllegalChild = 
    			Question.isHavingIllegalChild(question);
    		if(isHavingIllegalChild) {
    			throw new ELSException("Question.onStarredMinistryChangeLH/2", 
        				"Question has clubbings which are still in the" +
        				" approval workflow. Group change is not allowed" +
        				" in such an inconsistent state.");
    		}
    		else {
    			if(qnState == STARRED_STATE.ON_CHART) {
    				List<Question> clubbings = Question.findClubbings(question);
    				// Unclub all the Questions
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
        			question.setRecommendationStatus(question.getInternalStatus());
    				question.merge();
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
       			}
    			else if(qnState == STARRED_STATE.IN_WORKFLOW_AND_PRE_FINAL) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			WorkflowDetails.endProcess(wfDetails);
        			question.removeExistingWorkflowAttributes();
    				List<Question> clubbings = Question.findClubbings(question);
        			/*
    				 * Unclub all the Questions
    				 */
        			for(Question child : clubbings) {
        				Question.unclub(question, child, locale);
        			}
        			question.setRecommendationStatus(question.getInternalStatus());
        			question.merge();
      				Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			for(Question kid : clubbings) {
        				kid.setMinistry(ministry);
        				kid.setSubDepartment(subDepartment);
        				kid.merge();
        			}
    			}
    			else if(qnState == STARRED_STATE.POST_FINAL_AND_PRE_BALLOT) {
    				/*
    				 * Stop the question's workflow
    				 */
    				WorkflowDetails wfDetails = 
        				WorkflowDetails.findCurrentWorkflowDetail(question);
        			
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
        			question.removeExistingWorkflowAttributes();
        			question.setRecommendationStatus(question.getInternalStatus());
    			   	question.merge();
        			
        			Ministry ministry = question.getMinistry();
        			SubDepartment subDepartment = question.getSubDepartment();
        			
        			List<Question> clubbings = Question.findClubbings(question);
        			for(Question kid : clubbings) {
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
	    			WorkflowDetails.startProcessAtGivenLevel(question, 
	    					ApplicationConstants.APPROVAL_WORKFLOW, question.getInternalStatus(), 
	    					userGroupType, assigneeLevel, 
	    					locale);
    			}
    			else if(qnState == 
    				STARRED_STATE.POST_BALLOT_AND_PRE_YAADI_LAID) {
    				//TODOs
    			}
    		}
    	}
		
	}
	
	public static int updateTimeoutSupportingMemberTasksForDevice(final Long deviceId, final Date submissionDate) {
		return getQuestionRepository().updateTimeoutSupportingMemberTasksForDevice(deviceId, submissionDate);
	}
}