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
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
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
@JsonIgnoreProperties({"halfHourDiscusionFromQuestionReference","answeringDate","recommendationStatus","houseType", "session",
    "language","type","supportingMembers", "subDepartment", "referencedQuestions",
    "drafts","clubbings","group","editedBy","editedAs","clubbedEntities","referencedEntities","parent","parentReferencing",
    "clarificationNeededFrom"})
public class Question extends BaseDomain
implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    //---------------------------Basic Characteristics--------------------------
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

    //this refers to various final status.submitted,admitted,rejected,convert to unstarred
    /** The status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    //this refers to the various status assigned to a question by an assistant
    /** The internal status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;
   

    /** The remarks. */
    @Column(length=30000)
    private String remarks;
    
    //---------------------------Primary and supporting members-----------------
    /** The primary member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member primaryMember;

    /**** Added By Sandeep Singh ****/
    /**** Changed cascade type to all ****/
    /** The supporting members. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="questions_supportingmembers",
            joinColumns={@JoinColumn(name="question_id",
                    referencedColumnName="id")},
                    inverseJoinColumns={@JoinColumn(name="supportingmember_id",
                            referencedColumnName="id")})
                            private List<SupportingMember> supportingMembers;

    //------------------------Group Information--------------------------------
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

    //--------------------------Drafts------------------------------------------
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="questions_drafts_association", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="question_draft_id", referencedColumnName="id")})
    private List<QuestionDraft> drafts;    

    //--------------------------Clubbing Entities------------------------------------------
    /** The parent. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Question parent;
    
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="questions_clubbingentities", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;

    //--------------------------Referenced Entities------------------------------------------
    
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="questions_referencedentities", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
    private List<ReferencedEntity> referencedEntities;
    /***************************Common Fields End ****************************************/
    
    /**** Remove Fields ****/
    
    /** The language. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="language_id")
    private Language language;
    
    /** The prospective clubbings. */
    @Column(length=5000)
    private String prospectiveClubbings;
    
    /** The mark as answered. */
    private Boolean markAsAnswered;

    /**** Short Notice Fields ****/
    
    /** The reason. */
    private String reason;

    /** The to be answered by minister. */
    private Boolean toBeAnsweredByMinister=false;

    /** The date of answering by minister. */
    @Temporal(TemporalType.DATE)
    private Date dateOfAnsweringByMinister;
    
    /**** Half Hour Discussion Fields ****/
    @Temporal(TemporalType.DATE)
    private Date discussionDate;

    @Column(length=30000)
    private String briefExplanation;

    @ManyToOne(fetch=FetchType.LAZY)
    private Question halfHourDiscusionFromQuestionReference;

    /**** last date of receive of answer ****/
    @Temporal(TemporalType.DATE)
    private Date lastDateOfAnswerReceiving;    

    /** The question repository. */
    @Autowired
    private transient QuestionRepository questionRepository;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new question.
     */
    public Question() {
        super();
    }
    // ---------------------------------Domain Methods----------------------------------------------
    /**
     * Gets the question repository.
     *
     * @return the question repository
     */
    public static QuestionRepository getQuestionRepository()
    {
        QuestionRepository questionRepository = new Question().questionRepository;
        if (questionRepository == null) {
            throw new IllegalStateException(
            "QuestionRepository has not been injected in Question Domain");
        }
        return questionRepository;
    }

    /**
     * Find supporting members.
     *
     * @param strQuestionId the str question id
     * @return the list
     */
    public static List<SupportingMember> findSupportingMembers(final String strQuestionId){
        Long questionId=Long.parseLong(strQuestionId);
        Question question=findById(Question.class, questionId);
        return question.getSupportingMembers();
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.domain.BaseDomain#merge()
     */
    @Override
    public Question merge() {
        Question question=null;
        if(internalStatus.getType().equals(ApplicationConstants.QUESTION_SUBMIT)) {
            if(this.getNumber()==null){
                synchronized (this) {
                    Integer number = Question.assignQuestionNo(this.getHouseType(),
                            this.getSession(), this.getType(),this.getLocale());
                    this.setNumber(number+1);
                    addQuestionDraft();
                    question=(Question) super.merge();
                }
            }else{
            Question oldQuestion=Question.findById(Question.class,getId());
            if(getClubbedEntities()==null){
                this.clubbedEntities=oldQuestion.getClubbedEntities();
            }
            if(getReferencedEntities()==null){
                this.referencedEntities=oldQuestion.getReferencedEntities();
            }
            addQuestionDraft();
            question=(Question) super.merge();
            }
        }
        if(question!=null){
            return question;
        }else{
            if(internalStatus.getType().equals(ApplicationConstants.QUESTION_INCOMPLETE)||
            		internalStatus.getType().equals(ApplicationConstants.QUESTION_COMPLETE)){
                return (Question) super.merge();
            }else{
            	Question oldQuestion=Question.findById(Question.class,getId());
                if(getClubbedEntities()==null){
                    this.clubbedEntities=oldQuestion.getClubbedEntities();
                }
                if(getReferencedEntities()==null){
                    this.referencedEntities=oldQuestion.getReferencedEntities();
                }
                addQuestionDraft();
                return (Question) super.merge();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.domain.BaseDomain#persist()
     */
    @Override
    public Question persist() {
        if(status.getType().equals(ApplicationConstants.QUESTION_SUBMIT)) {
            if(this.getNumber()==null){
                synchronized (this) {
                    Integer number = Question.assignQuestionNo(this.getHouseType(),
                            this.getSession(), this.getType(),this.getLocale());
                    this.setNumber(number+1);
                    addQuestionDraft();
                    return (Question)super.persist();
                }
            }
        }
        return (Question) super.persist();
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
    public static Integer assignQuestionNo(final HouseType houseType, final Session session, final DeviceType deviceType,final String locale){
        return getQuestionRepository().assignQuestionNo(houseType,
                session, deviceType,locale);
    }

    /**
     * Adds the question draft.
     */
    private void addQuestionDraft() {
        if(!status.getType().equals(ApplicationConstants.QUESTION_INCOMPLETE) &&
        		!status.getType().equals(ApplicationConstants.QUESTION_COMPLETE)) {
            QuestionDraft draft=new QuestionDraft();
            draft.setAnswer(getAnswer());
            draft.setAnsweringDate(getAnsweringDate());
            draft.setClubbedEntities(getClubbedEntities());
            draft.setDepartment(getDepartment());
            draft.setEditedAs(getEditedAs());
            draft.setEditedBy(getEditedBy());
            draft.setEditedOn(getEditedOn());
            draft.setGroup(getGroup());
            draft.setInternalStatus(getInternalStatus());
            draft.setMinistry(getMinistry());
            if(getRevisedQuestionText()!=null&&getRevisedSubject()!=null){
                draft.setQuestionText(getRevisedQuestionText());
                draft.setSubject(getRevisedSubject());
            }else if(getRevisedQuestionText()!=null){
            	draft.setQuestionText(getRevisedQuestionText());
                draft.setSubject(getSubject());
            }else if(getRevisedSubject()!=null){
                draft.setQuestionText(getQuestionText());
                draft.setSubject(getRevisedSubject());
            }else{
            	draft.setQuestionText(getQuestionText());
                draft.setSubject(getSubject());
            }
            draft.setReferencedEntities(getReferencedEntities());
            draft.setRemarks(getRemarks());
            draft.setRecommendationStatus(getRecommendationStatus());
            draft.setStatus(getStatus());
            draft.setSubDepartment(getSubDepartment());
            draft.setType(getType());
            if(getId()!=null){
                Question question=Question.findById(Question.class,getId());
                List<QuestionDraft> originalDrafts=question.getDrafts();
                if(originalDrafts!=null){
                    originalDrafts.add(draft);
                }else{
                    originalDrafts=new ArrayList<QuestionDraft>();
                    originalDrafts.add(draft);
                }
                this.setDrafts(originalDrafts);
            }else{
                List<QuestionDraft> originalDrafts=new ArrayList<QuestionDraft>();
                originalDrafts.add(draft);
                this.setDrafts(originalDrafts);
            }
        }
    }

    /**
     * Find last starred unstarred short notice question no.
     *
     * @param house the house
     * @param currentSession the current session
     * @return the integer
     * @author compaq
     * @since v1.0.0
     */
//    public static Integer findLastStarredUnstarredShortNoticeQuestionNo(final House house, final Session currentSession) {
//        return getQuestionRepository().findLastStarredUnstarredShortNoticeQuestionNo(house, currentSession);
//    }
//
//    /**
//     * Find last half hour discussion question no.
//     *
//     * @param house the house
//     * @param currentSession the current session
//     * @return the integer
//     * @author compaq
//     * @since v1.0.0
//     */
//    public static Integer findLastHalfHourDiscussionQuestionNo(final House house, final Session currentSession) {
//        return getQuestionRepository().findLastHalfHourDiscussionQuestionNo(house, currentSession);
//    }

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

    //-----------------------Getters and Setters--------------------------------

    /**
     * Gets the house type.
     *
     * @return the house type
     */
    public HouseType getHouseType() {
        return houseType;
    }

    /**
     * Sets the house type.
     *
     * @param houseType the new house type
     */
    public void setHouseType(final HouseType houseType) {
        this.houseType = houseType;
    }

    /**
     * Gets the session.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sets the session.
     *
     * @param session the new session
     */
    public void setSession(final Session session) {
        this.session = session;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public DeviceType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(final DeviceType type) {
        this.type = type;
    }

    /**
     * Gets the number.
     *
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the number.
     *
     * @param number the new number
     */
    public void setNumber(final Integer number) {
        this.number = number;
    }

    /**
     * Gets the submission date.
     *
     * @return the submission date
     */
    public Date getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Sets the submission date.
     *
     * @param submissionDate the new submission date
     */
    public void setSubmissionDate(final Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * Gets the answering date.
     *
     * @return the answering date
     */
    public QuestionDates getAnsweringDate() {
        return answeringDate;
    }

    /**
     * Sets the answering date.
     *
     * @param answeringDate the new answering date
     */
    public void setAnsweringDate(final QuestionDates answeringDate) {
        this.answeringDate = answeringDate;
    }
    /**
     * Gets the language.
     *
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(final Language language) {
        this.language = language;
    }

    /**
     * Gets the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject the new subject
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Gets the question text.
     *
     * @return the question text
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Sets the question text.
     *
     * @param questionText the new question text
     */
    public void setQuestionText(final String questionText) {
        this.questionText = questionText;
    }

    /**
     * Gets the answer.
     *
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the answer.
     *
     * @param answer the new answer
     */
    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(final Status status) {
        this.status = status;
    }

    /**
     * Gets the primary member.
     *
     * @return the primary member
     */
    public Member getPrimaryMember() {
        return primaryMember;
    }

    /**
     * Sets the primary member.
     *
     * @param primaryMember the new primary member
     */
    public void setPrimaryMember(final Member primaryMember) {
        this.primaryMember = primaryMember;
    }

    /**
     * Gets the supporting members.
     *
     * @return the supporting members
     */
    public List<SupportingMember> getSupportingMembers() {
        return supportingMembers;
    }

    /**
     * Sets the supporting members.
     *
     * @param supportingMembers the new supporting members
     */
    public void setSupportingMembers(final List<SupportingMember> supportingMembers) {
        this.supportingMembers = supportingMembers;
    }

    /**
     * Gets the group.
     *
     * @return the group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Sets the group.
     *
     * @param group the new group
     */
    public void setGroup(final Group group) {
        this.group = group;
    }

    /**
     * Gets the ministry.
     *
     * @return the ministry
     */
    public Ministry getMinistry() {
        return ministry;
    }

    /**
     * Sets the ministry.
     *
     * @param ministry the new ministry
     */
    public void setMinistry(final Ministry ministry) {
        this.ministry = ministry;
    }

    /**
     * Gets the department.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Sets the department.
     *
     * @param department the new department
     */
    public void setDepartment(final Department department) {
        this.department = department;
    }

    /**
     * Gets the sub department.
     *
     * @return the sub department
     */
    public SubDepartment getSubDepartment() {
        return subDepartment;
    }

    /**
     * Sets the sub department.
     *
     * @param subDepartment the new sub department
     */
    public void setSubDepartment(final SubDepartment subDepartment) {
        this.subDepartment = subDepartment;
    }   
    /**
     * Gets the drafts.
     *
     * @return the drafts
     */
    public List<QuestionDraft> getDrafts() {
        return drafts;
    }

    /**
     * Sets the drafts.
     *
     * @param drafts the new drafts
     */
    public void setDrafts(final List<QuestionDraft> drafts) {
        this.drafts = drafts;
    }    
    /**
     * Gets the creation date.
     *
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date.
     *
     * @param creationDate the new creation date
     */
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy the new created by
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the revised subject.
     *
     * @return the revised subject
     */
    public String getRevisedSubject() {
        return revisedSubject;
    }

    /**
     * Sets the revised subject.
     *
     * @param revisedSubject the new revised subject
     */
    public void setRevisedSubject(final String revisedSubject) {
        this.revisedSubject = revisedSubject;
    }

    /**
     * Gets the revised question text.
     *
     * @return the revised question text
     */
    public String getRevisedQuestionText() {
        return revisedQuestionText;
    }

    /**
     * Sets the revised question text.
     *
     * @param revisedQuestionText the new revised question text
     */
    public void setRevisedQuestionText(final String revisedQuestionText) {
        this.revisedQuestionText = revisedQuestionText;
    }

    /**
     * Gets the internal status.
     *
     * @return the internal status
     */
    public Status getInternalStatus() {
        return internalStatus;
    }

    /**
     * Sets the internal status.
     *
     * @param internalStatus the new internal status
     */
    public void setInternalStatus(final Status internalStatus) {
        this.internalStatus = internalStatus;
    }

     /**
     * Gets the remarks.
     *
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks.
     *
     * @param remarks the new remarks
     */
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

    
    /**
     * Gets the edited on.
     *
     * @return the edited on
     */
    public Date getEditedOn() {
        return editedOn;
    }

    /**
     * Sets the edited on.
     *
     * @param editedOn the new edited on
     */
    public void setEditedOn(final Date editedOn) {
        this.editedOn = editedOn;
    }

    
    /**
     * Gets the revisions.
     *
     * @param questionId the question id
     * @param locale the locale
     * @return the revisions
     */
    public static List<QuestionRevisionVO> getRevisions(final Long questionId,final String locale) {
        return getQuestionRepository().getRevisions(questionId,locale);
    }

    /**
     * Full text search clubbing.
     *
     * @param textToSearch the text to search
     * @param sessionToSearchOn the session to search on
     * @param groupToSearchOn the group to search on
     * @param currentChartId the current chart id
     * @param questionId the question id
     * @param locale the locale
     * @return the list
     */
    
    /*
     * This method is used to obtain all the questions of a member of a particular device type ,
     * belonging to a particular session and having internal status as specified
     */
    /**
     * Find all.
     *
     * @param currentMember the current member
     * @param session the session
     * @param deviceType the device type
     * @param internalStatus the internal status
     * @return the list
     */
    public static List<Question> findAll(final Member currentMember,
            final Session session, final DeviceType deviceType, final Status internalStatus) {
        return getQuestionRepository().findAll(currentMember,
                session,deviceType,internalStatus);
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
    public static List<Question> findAllFirstBatch(final Member currentMember,
            final Session session, final DeviceType deviceType, final Status internalStatus) {
        return getQuestionRepository().findAllFirstBatch(currentMember,
                session,deviceType,internalStatus);
    }

    /**
     * Find all second batch.
     *
     * @param currentMember the current member
     * @param session the session
     * @param deviceType the device type
     * @param internalStatus the internal status
     * @return the list
     */
    public static List<Question> findAllSecondBatch(final Member currentMember,
            final Session session, final DeviceType deviceType, final Status internalStatus) {
        return getQuestionRepository().findAllSecondBatch(currentMember,
                session,deviceType,internalStatus);
    }   

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Question getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(final Question parent) {
        this.parent = parent;
    }

    /**
     * Club.
     *
     * @param questionBeingProcessed the question being processed
     * @param questionBeingClubbed the question being clubbed
     * @param locale the locale
     * @return the boolean
     */
    

    /**
     * Unclub.
     *
     * @param questionBeingProcessed the question being processed
     * @param questionBeingClubbed the question being clubbed
     * @param locale the locale
     * @return the boolean
     */
    

    /**
     * Gets the prospective clubbings.
     *
     * @return the prospective clubbings
     */
    public String getProspectiveClubbings() {
        return prospectiveClubbings;
    }

    /**
     * Sets the prospective clubbings.
     *
     * @param prospectiveClubbings the new prospective clubbings
     */
    public void setProspectiveClubbings(final String prospectiveClubbings) {
        this.prospectiveClubbings = prospectiveClubbings;
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
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().find(session, member, deviceType,
                group, answeringDate, finalSubmissionDate, internalStatuses, maxNoOfQuestions,
                sortOrder, locale);

        if(questions == null) {
            questions = new ArrayList<Question>();
        }

        return questions;
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
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        List<Question> questions = Question.getQuestionRepository().findBeforeAnsweringDate(session,
                member, deviceType, group, answeringDate, finalSubmissionDate, internalStatuses,
                maxNoOfQuestions, sortOrder, locale);

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
     * Gets the recommendation status.
     *
     * @return the recommendation status
     */
    public Status getRecommendationStatus() {
        return recommendationStatus;
    }

    /**
     * Sets the recommendation status.
     *
     * @param recommendationStatus the new recommendation status
     */
    public void setRecommendationStatus(final Status recommendationStatus) {
        this.recommendationStatus = recommendationStatus;
    }

    /**
     * Gets the mark as answered.
     *
     * @return the mark as answered
     */
    public Boolean getMarkAsAnswered() {
        return markAsAnswered;
    }

    /**
     * Sets the mark as answered.
     *
     * @param markAsAnswered the new mark as answered
     */
    public void setMarkAsAnswered(final Boolean markAsAnswered) {
        this.markAsAnswered = markAsAnswered;
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
     * Gets the reason.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason.
     *
     * @param reason the new reason
     */
    public void setReason(final String reason) {
        this.reason = reason;
    }

    /**
     * Gets the to be answered by minister.
     *
     * @return the to be answered by minister
     */
    public Boolean getToBeAnsweredByMinister() {
        return toBeAnsweredByMinister;
    }

    /**
     * Sets the to be answered by minister.
     *
     * @param toBeAnsweredByMinister the new to be answered by minister
     */
    public void setToBeAnsweredByMinister(final Boolean toBeAnsweredByMinister) {
        this.toBeAnsweredByMinister = toBeAnsweredByMinister;
    }

    /**
     * Gets the date of answering by minister.
     *
     * @return the date of answering by minister
     */
    public Date getDateOfAnsweringByMinister() {
        return dateOfAnsweringByMinister;
    }

    /**
     * Sets the date of answering by minister.
     *
     * @param dateOfAnsweringByMinister the new date of answering by minister
     */
    public void setDateOfAnsweringByMinister(final Date dateOfAnsweringByMinister) {
        this.dateOfAnsweringByMinister = dateOfAnsweringByMinister;
    }

    /**
     * Creates the member ballot attendance.
     *
     * @param session the session
     * @param questionType the question type
     * @param locale the locale
     * @return the list
     */
    
    public static List<Question> findAdmittedStarredQuestionsUH(
            final Session session, final DeviceType questionType, final Member member,
            final String locale) {
        return getQuestionRepository().findAdmittedStarredQuestionsUH(
                session,questionType,member,
                locale);
    }

    public String findFormattedNumber(){
        NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
        return format.format(this.getNumber());
    }

    public List<ClubbedEntity> getClubbedEntities() {
        return clubbedEntities;
    }

    public void setClubbedEntities(final List<ClubbedEntity> clubbedEntities) {
        this.clubbedEntities = clubbedEntities;
    }
    public void setReferencedEntities(final List<ReferencedEntity> referencedEntities) {
        this.referencedEntities = referencedEntities;
    }
    public List<ReferencedEntity> getReferencedEntities() {
        return referencedEntities;
    }
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}
	public String getEditedBy() {
		return editedBy;
	}
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}
	public String getEditedAs() {
		return editedAs;
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
	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}
	public Date getDiscussionDate() {
		return discussionDate;
	}
	public void setBriefExplanation(String briefExplanation) {
		this.briefExplanation = briefExplanation;
	}
	public String getBriefExplanation() {
		return briefExplanation;
	}
	public void setHalfHourDiscusionFromQuestionReference(
			Question halfHourDiscusionFromQuestionReference) {
		this.halfHourDiscusionFromQuestionReference = halfHourDiscusionFromQuestionReference;
	}
	public Question getHalfHourDiscusionFromQuestionReference() {
		return halfHourDiscusionFromQuestionReference;
	}
	
	//------------------------------added by vikas & dhananjay 20012013------------------------------------
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
	public void setLastDateOfAnswerReceiving(Date lastDateOfAnswerReceiving) {
		this.lastDateOfAnswerReceiving = lastDateOfAnswerReceiving;
	}
	public Date getLastDateOfAnswerReceiving() {
		return lastDateOfAnswerReceiving;
	}
	
	

}