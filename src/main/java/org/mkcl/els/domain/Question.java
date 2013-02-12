/**
 * See the file LICENSE for redistribution information.
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
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
@JsonIgnoreProperties({"houseType", "session", "originalType", "type", "answeringDate",
	"chartAnsweringDate", "recommendationStatus", "ballotStatus", "supportingMembers",
	"group", "subDepartment", "drafts", "parent", "clubbedEntities", "referencedEntities",
	"halfHourDiscusionFromQuestionReference", "language"})
public class Question extends BaseDomain implements Serializable {

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
   
    /** The remarks. */
    @Column(length=30000)
    private String remarks;
    
    @Column(length=30000)
	private String rejectionReason;
    
    
    //=============== PRIMARY & SUPPORTING MEMBERS ====================
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

    
    //=============== GROUP ATTRIBUTERS ====================
    /** The group. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

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

    
    //=============== DRAFTS ====================
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="questions_drafts_association", 
    		joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="question_draft_id", referencedColumnName="id")})
    private List<QuestionDraft> drafts;    

    
    //=============== Clubbing ====================
    /** The parent. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Question parent;
    
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="questions_clubbingentities", 
    		joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;


    //=============== Referencing ====================
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="questions_referencedentities", 
    		joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
    private List<ReferencedEntity> referencedEntities;

    
    //=============== SHORT NOTICE DEVICE ATTRIBUTES ====================
    /** The reason. */
    @Column(length=30000)
    private String reason;
    
    /** The reason. */
    @Column(length=30000)
    private String revisedReason;

    /** The to be answered by minister. */
    private Boolean toBeAnsweredByMinister=false;

    /** The date of answering by minister. */
    @Temporal(TemporalType.DATE)
    private Date dateOfAnsweringByMinister;
    
    
    //=============== HALF HOUR DEVICE ATTRIBUTES ====================
    @ManyToOne(fetch=FetchType.LAZY)
    private Question halfHourDiscusionFromQuestionReference;

    @Temporal(TemporalType.DATE)
    private Date discussionDate;

    @Column(length=30000)
    private String briefExplanation;
    
    @Column(length=30000)
    private String revisedBriefExplanation;  
    
    @Temporal(TemporalType.DATE)
    private Date lastDateOfAnswerReceiving;    

    
    //=============== REMOVE UNWANTED FIELDS. START ====================
    /** The language. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="language_id")
    private Language language;
    
    /** The prospective clubbings. */
    @Column(length=5000)
    private String prospectiveClubbings;
    
    /** The mark as answered. */
    private Boolean markAsAnswered;
    //=============== REMOVE UNWANTED FIELDS. START ====================
    
    /** The question repository. */
    @Autowired
    private transient QuestionRepository questionRepository;

    
    //=============== CONSTRUCTORS ==================
    /**
     * Instantiates a new question.
     */
    public Question() {
        super();
    }
    
    
    //=============== VIEW METHODS ==================
    /**
     * Gets the revisions.
     *
     * @param questionId the question id
     * @param locale the locale
     * @return the revisions
     */
    public static List<QuestionRevisionVO> getRevisions(final Long questionId, final String locale) {
        return getQuestionRepository().getRevisions(questionId,locale);
    }
    
    public static MemberBallotMemberWiseReportVO findMemberWiseReportVO(final Session session,
    		final DeviceType questionType, final Member member, final String locale){
    	return getQuestionRepository().findMemberWiseReportVO(session, questionType, member, locale);		
   	}
    
    
    //=============== DOMAIN METHODS ================
    @Override
    public Question persist() {
        if(this.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (this) {
                    Integer number = Question.assignQuestionNo(this.getHouseType(),
                            this.getSession(), this.getType(),this.getLocale());
                    this.setNumber(number + 1);
                    addQuestionDraft();
                    return (Question)super.persist();
                }
            }
        }
        return (Question) super.persist();
    }
    
    @Override
    public Question merge() {
        Question question = null;
        if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (this) {
                    Integer number = Question.assignQuestionNo(this.getHouseType(),
                            this.getSession(), this.getType(),this.getLocale());
                    this.setNumber(number + 1);
                    addQuestionDraft();
                    question = (Question) super.merge();
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
        if(question != null) {
            return question;
        }
        else {
            if(this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_COMPLETE)) {
                return (Question) super.merge();
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
                return (Question) super.merge();
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
     * @since v1.0.0
     */
    public static Integer assignQuestionNo(final HouseType houseType, 
    		final Session session, final DeviceType deviceType, final String locale) {
        return getQuestionRepository().assignQuestionNo(houseType, session, deviceType, locale);
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
    public static Question find(final Session session, final Integer number, Long deviceTypeId) {
    	return Question.getQuestionRepository().find(session, number, deviceTypeId);
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
    
	public static List<Question> findAdmittedStarredQuestionsUH(final Session session, 
			final DeviceType questionType, final Member member, final String locale) {
		return getQuestionRepository().findAdmittedStarredQuestionsUH(session,
				questionType, member, locale);
	}
	
	public static List<Question> findAdmittedStarredQuestionsUHByChartDate(final Session session, 
			final DeviceType questionType, final Member member, final String locale) {
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
        List<QuestionDraft> drafts = this.getDrafts();
        if(drafts != null) {
            int size = drafts.size();
            if(size > 1) {
                return drafts.get(size - 1);
            }
        }
        return null;
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
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
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
    
    public List<ClubbedEntity> findClubbedEntitiesByQuestionNumber(final String sortOrder,
    		final String locale) {
    	return getQuestionRepository().findClubbedEntitiesByQuestionNumber(this,sortOrder,
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

    
    //=============== INTERNAL METHODS ===============
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
     * Adds the question draft.
     */
    private void addQuestionDraft() {
        if(! this.getStatus().getType().equals(ApplicationConstants.QUESTION_INCOMPLETE) &&
        		! this.getStatus().getType().equals(ApplicationConstants.QUESTION_COMPLETE)) {
            QuestionDraft draft = new QuestionDraft();
            draft.setType(this.getType());
            draft.setAnsweringDate(this.getAnsweringDate());
            draft.setAnswer(this.getAnswer());
            draft.setRemarks(this.getRemarks());
            
            draft.setClubbedEntities(this.getClubbedEntities());
            draft.setReferencedEntities(this.getReferencedEntities());
            
            draft.setEditedAs(this.getEditedAs());
            draft.setEditedBy(this.getEditedBy());
            draft.setEditedOn(this.getEditedOn());
            
            draft.setGroup(this.getGroup());
            draft.setMinistry(this.getMinistry());
            draft.setDepartment(this.getDepartment());
            draft.setSubDepartment(this.getSubDepartment());
            
            draft.setStatus(this.getStatus());
            draft.setInternalStatus(this.getInternalStatus());
            draft.setRecommendationStatus(this.getRecommendationStatus());
            
            if(this.getType().getType().equals(
            		ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
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
            else {
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
            }
            
            if(this.getId() != null) {
                Question question = Question.findById(Question.class, this.getId());
                List<QuestionDraft> originalDrafts = question.getDrafts();
                if(originalDrafts != null){
                    originalDrafts.add(draft);
                }
                else{
                    originalDrafts = new ArrayList<QuestionDraft>();
                    originalDrafts.add(draft);
                }
                this.setDrafts(originalDrafts);
            }
            else {
                List<QuestionDraft> originalDrafts = new ArrayList<QuestionDraft>();
                originalDrafts.add(draft);
                this.setDrafts(originalDrafts);
            }
        }
    }

    
    //=============== GETTERS/SETTERS ===============
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
		
	public List<QuestionDraft> getDrafts() {
		return drafts;
	}
	
	public void setDrafts(List<QuestionDraft> drafts) {
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
	
	public List<ReferencedEntity> getReferencedEntities() {
		return referencedEntities;
	}
	
	public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
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
	
	public Question getHalfHourDiscusionFromQuestionReference() {
		return halfHourDiscusionFromQuestionReference;
	}
	
	public void setHalfHourDiscusionFromQuestionReference(
			Question halfHourDiscusionFromQuestionReference) {
		this.halfHourDiscusionFromQuestionReference = halfHourDiscusionFromQuestionReference;
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
}