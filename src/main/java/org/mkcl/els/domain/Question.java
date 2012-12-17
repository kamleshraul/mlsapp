/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Question.java
 * Created On: Sep 14, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
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
@JsonIgnoreProperties({"answeringDate","recommendationStatus","houseType", "session","language","type","supportingMembers", "subDepartment", 

"referencedQuestions", "drafts","clubbings","group","editedBy","editedAs"})
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

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column(length=1000)
	private String createdBy;

	/** The answering date. */
	@ManyToOne(fetch=FetchType.LAZY)
	private QuestionDates answeringDate;

	/** The language. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="language_id")
	private Language language;

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
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internalstatus_id")
	private Status internalStatus;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendationstatus_id")
	private Status recommendationStatus;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="clarification_neededfrom_id")
	private ClarificationNeededFrom clarificationNeededFrom;

	@Column(length=30000)
	private String remarks;

	/** The edited by. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="editedby_id")
	private User editedBy;

	/** The edited as. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="editedastype_id")
	private UserGroupType editedAs;

	/** The edited on. */
	@Temporal(TemporalType.TIMESTAMP)
	@JoinColumn(name="editedon")
	private Date editedOn;

	private Boolean markAsAnswered;

	/*
	 * a flag to indicate if question has been processed by assistant
	 */
	private Boolean assistantProcessed=false;


	//---------------------------Primary and supporting members-----------------
	/** The primary member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
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

	//---------------------------Referenced Questions---------------------------
	/** The referenced questions. */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="questions_references", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, inverseJoinColumns=

{@JoinColumn(name="reference_id", referencedColumnName="id")})
	private List<Question> referencedQuestions;

	//--------------------------Drafts------------------------------------------
	/** The drafts. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="questions_drafts_association", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, 

inverseJoinColumns={@JoinColumn(name="question_draft_id", referencedColumnName="id")})
	private List<QuestionDraft> drafts;

	//--------------------------Clubbing------------------------------------------
	@ManyToOne(fetch=FetchType.LAZY)
	private Question parent;

	/** The drafts. */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="questions_clubbing", joinColumns={@JoinColumn(name="primary_question_id", referencedColumnName="id")}, inverseJoinColumns=
	{@JoinColumn(name="clubbed_question_id", referencedColumnName="id")})
	private List<Question> clubbings;

	@Column(length=5000)
	private String prospectiveClubbings;

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

	public static List<SupportingMember> findSupportingMembers(final String strQuestionId){
		Long questionId=Long.parseLong(strQuestionId);
		Question question=findById(Question.class, questionId);
		return question.getSupportingMembers();
	}

	@Override
	public Question merge() {
		if(internalStatus.getType().equals("questions_submit")) {
			// Question numbers will be generated in a highly concurrent
			// environment. Running a Question number generation
			// algorithm in a non thread safe manner will result in errors.
			if(this.getNumber()==null){
				synchronized (this) {
					Integer number = Question.assignQuestionNo(this.getHouseType(),
							this.getSession(), this.getType());
					this.setNumber(number+1);
					//updating clubbing
					Question oldQuestion=Question.findById(Question.class,getId());
					if(getClubbings()==null){
						this.clubbings=oldQuestion.getClubbings();
					}
					//updating referencing
					if(getReferencedQuestions()==null){
						this.referencedQuestions=oldQuestion.getReferencedQuestions();
					}
					addQuestionDraft();
					return (Question) super.merge();
				}
			}
		}
       // else {
            // Add this Question to chart if applicable
			//Chart.addToLatestChartIfApplicable(this);
       // }
		//}
		//updating clubbing
		Question oldQuestion=Question.findById(Question.class,getId());
		if(getClubbings()==null){
			this.clubbings=oldQuestion.getClubbings();
		}
		//updating referencing
		if(getReferencedQuestions()==null){
			this.referencedQuestions=oldQuestion.getReferencedQuestions();
		}
		addQuestionDraft();
		return (Question) super.merge();
	}

	@Override
	public Question persist() {
		Question question=null;
		if(status.getType().equals("questions_submit")) {
			// Question numbers will be generated in a highly concurrent
			// environment. Running a Question number generation
			// algorithm in a non thread safe manner will result in errors.
			if(this.getNumber()==null){
				synchronized (this) {
					Integer number = Question.assignQuestionNo(this.getHouseType(),
							this.getSession(), this.getType());
					this.setNumber(number+1);
					question=(Question) super.persist();
					addQuestionDraft();
				}
			}else{
			addQuestionDraft();
			}
		}
		if(question!=null){
			return question;
		}else{
			return (Question) super.persist();
		}
	}

	//    public  List<SupportingMember> findSupportingMembers(final String strQuestionId){
	//        Long questionId=Long.parseLong(strQuestionId);
	//        Question question=findById(Question.class, questionId);
	//        return question.getSupportingMembers();
	//    }

	/**
	 * Find last starred unstarred short notice question no.
	 *
	 * @param house the house
	 * @param currentSession the current session
	 * @return the integer
	 * @author compaq
	 * @since v1.0.0
	 */
	public static Integer findLastStarredUnstarredShortNoticeQuestionNo(final House house, final Session currentSession) {
		return getQuestionRepository().findLastStarredUnstarredShortNoticeQuestionNo(house, currentSession);
	}

	/**
	 * Find last half hour discussion question no.
	 *
	 * @param house the house
	 * @param currentSession the current session
	 * @return the integer
	 * @author compaq
	 * @since v1.0.0
	 */
	public static Integer findLastHalfHourDiscussionQuestionNo(final House house, final Session currentSession) {
		return getQuestionRepository().findLastHalfHourDiscussionQuestionNo(house, currentSession);
	}

	/**
	 * Assign question no.
	 *
	 * @param houseType the house type
	 * @param session the session
	 * @param deviceType the device type
	 * @return the integer
	 * @author compaq
	 * @since v1.0.0
	 */
	public static Integer assignQuestionNo(final HouseType houseType, final Session session, final DeviceType deviceType)
	{
		return getQuestionRepository().assignQuestionNo(houseType,
				session, deviceType);
	}

	private void addQuestionDraft() {
		if(!status.getType().equals("questions_incomplete") && !status.getType().equals("questions_complete")) {
			QuestionDraft draft=new QuestionDraft();
			draft.setAnswer(getAnswer());
			draft.setAnsweringDate(getAnsweringDate());
			draft.setClarificationNeededFrom(getClarificationNeededFrom());
			draft.setClubbings(getClubbings());
			draft.setDepartment(getDepartment());
			draft.setEditedAs(getEditedAs());
			draft.setEditedBy(getEditedBy());
			draft.setEditedOn(getEditedOn());
			draft.setGroup(getGroup());
			draft.setInternalStatus(getInternalStatus());
			draft.setLanguage(getLanguage());
			draft.setLocale(getLocale());
			draft.setMinistry(getMinistry());
			if(getEditedAs()!=null){
				draft.setQuestionText(getRevisedQuestionText());
				draft.setSubject(getRevisedSubject());
			}else{
				draft.setQuestionText(getQuestionText());
				draft.setSubject(getSubject());
			}
			//draft.setQuestionText(getRevisedQuestionText());
			draft.setReferencedQuestions(getReferencedQuestions());
			draft.setRemarks(getRemarks());
			draft.setRecommendationStatus(getRecommendationStatus());
			draft.setStatus(getStatus());
			draft.setSubDepartment(getSubDepartment());
			draft.setSupportingMembers(getSupportingMembers());
			draft.setType(getType());
			draft.setMarkAsAnswered(getMarkAsAnswered());
			Question question=Question.findById(Question.class,getId());
			List<QuestionDraft> originalDrafts=question.getDrafts();
			if(originalDrafts!=null){
				originalDrafts.add(draft);
			}else{
				originalDrafts=new ArrayList<QuestionDraft>();
				originalDrafts.add(draft);
			}
			this.setDrafts(originalDrafts);
		}
	}

	/**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group for a given @param answeringDate.
     * The Question should have been submitted on or before
     * @param finalSubmissionDate. Exclude @param excludeQuestions from the
     * final result.
     *
     * Questions should be sorted as per @param sortOrder according to Question number.
     *
     * Returns an empty list (if there are no questions for the specified @param answering date)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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
     * @param answeringDate. The Question should have been submitted on or
     * before @param finalSubmissionDate. Exclude @param excludeQuestions from the
     * final result.
     *
     * The Questions should be sorted as per @param sortOrder
     * according to answeringDate.
     *
     * Returns an empty list (if there are no questions for the specified criteria)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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
     * @param finalSubmissionDate. Exclude @param excludeQuestions from the
     * final result.
     *
     * Questions should be sorted as per @param sortOrder according to Question number.
     *
     * Returns an empty list (if there are no questions for the specified @param answering date)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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

	public QuestionDates getAnsweringDate() {
		return answeringDate;
	}

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
	 * Gets the referenced questions.
	 *
	 * @return the referenced questions
	 */
	public List<Question> getReferencedQuestions() {
		return referencedQuestions;
	}

	/**
	 * Sets the referenced questions.
	 *
	 * @param referencedQuestions the new referenced questions
	 */
	public void setReferencedQuestions(final List<Question> referencedQuestions) {
		this.referencedQuestions = referencedQuestions;
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
	 * Gets the clubbings.
	 *
	 * @return the clubbings
	 */
	public List<Question> getClubbings() {
		return clubbings;
	}

	/**
	 * Sets the clubbings.
	 *
	 * @param clubbings the new clubbings
	 */
	public void setClubbings(final List<Question> clubbings) {
		this.clubbings = clubbings;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public String getRevisedSubject() {
		return revisedSubject;
	}

	public void setRevisedSubject(final String revisedSubject) {
		this.revisedSubject = revisedSubject;
	}

	public String getRevisedQuestionText() {
		return revisedQuestionText;
	}

	public void setRevisedQuestionText(final String revisedQuestionText) {
		this.revisedQuestionText = revisedQuestionText;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(final Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public ClarificationNeededFrom getClarificationNeededFrom() {
		return clarificationNeededFrom;
	}

	public void setClarificationNeededFrom(
			final ClarificationNeededFrom clarificationNeededFrom) {
		this.clarificationNeededFrom = clarificationNeededFrom;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	public User getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(final User editedBy) {
		this.editedBy = editedBy;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(final Date editedOn) {
		this.editedOn = editedOn;
	}
	public UserGroupType getEditedAs() {
		return editedAs;
	}
	public void setEditedAs(final UserGroupType editedAs) {
		this.editedAs = editedAs;
	}

	public static List<QuestionRevisionVO> getRevisions(final Long questionId,final String locale) {
		return getQuestionRepository().getRevisions(questionId,locale);
	}

	public static List<QuestionSearchVO> fullTextSearchClubbing(final String textToSearch,final Long sessionToSearchOn,final Long 
				groupToSearchOn,final Long currentChartId, final Long questionId, final String locale) {
		return getQuestionRepository().fullTextSearchClubbing(textToSearch,sessionToSearchOn,groupToSearchOn,
				currentChartId,questionId,locale);
	}
	/*
	 * This method is used to obtain all the questions of a member of a particular device type ,
	 * belonging to a particular session and having internal status as specified
	 */
    public static List<Question> findAll(final Member currentMember,
            final Session session, final DeviceType deviceType, final Status internalStatus) {
        return getQuestionRepository().findAll(currentMember,
                session,deviceType,internalStatus);
    }

    public static List<Question> findAllFirstBatch(final Member currentMember,
            final Session session, final DeviceType deviceType, final Status internalStatus) {
        return getQuestionRepository().findAllFirstBatch(currentMember,
                session,deviceType,internalStatus);
    }

    public static List<Question> findAllSecondBatch(final Member currentMember,
            final Session session, final DeviceType deviceType, final Status internalStatus) {
        return getQuestionRepository().findAllSecondBatch(currentMember,
                session,deviceType,internalStatus);
    }

    public Boolean getAssistantProcessed() {
        return assistantProcessed;
    }

    public void setAssistantProcessed(final Boolean assistantProcessed) {
        this.assistantProcessed = assistantProcessed;
    }


    public Question getParent() {
        return parent;
    }

    public void setParent(final Question parent) {
        this.parent = parent;
    }

    public static Boolean club(final Long questionBeingProcessed,final Long questionBeingClubbed,final String locale){
        return getQuestionRepository().club(questionBeingProcessed,questionBeingClubbed,locale);
    }

    public static Boolean unclub(final Long questionBeingProcessed,final Long questionBeingClubbed,final String locale){
        return getQuestionRepository().unclub(questionBeingProcessed,questionBeingClubbed,locale);
    }

    public String getProspectiveClubbings() {
        return prospectiveClubbings;
    }

    public void setProspectiveClubbings(final String prospectiveClubbings) {
        this.prospectiveClubbings = prospectiveClubbings;
    }

	/**
     * Find @param maxNoOfQuestions Questions of a @param member for a
     * given @param session having @param group for a given @param answeringDate.
     * The Question should have been submitted on or before
     * @param finalSubmissionDate.
     *
     * Questions should be sorted as per @param sortOrder according to Question number.
     *
     * Returns an empty list (if there are no questions for the specified @param answering date)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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
     * @param answeringDate. The Question should have been submitted on or
     * before @param finalSubmissionDate.
     *
     * The Questions should be sorted as per @param sortOrder
     * according to answeringDate.
     *
     * Returns an empty list (if there are no questions for the specified criteria)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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
     * @param finalSubmissionDate.
     *
     * Questions should be sorted as per @param sortOrder according to Question number.
     *
     * Returns an empty list (if there are no questions for the specified @param answering date)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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
	 */
	public Question simpleMerge() {
		Question q = (Question) super.merge();
		return q;
	}
	public Status getRecommendationStatus() {
		return recommendationStatus;
	}
	public void setRecommendationStatus(final Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

    public Boolean getMarkAsAnswered() {
        return markAsAnswered;
    }

    public void setMarkAsAnswered(final Boolean markAsAnswered) {
        this.markAsAnswered = markAsAnswered;
    }

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
     * @param answering date)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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
     * Find all the Questions of a @param member for a given @param session having
     * @param group. All this questions should have an answering date mentioned.
     * The answering date should be equal to or less than @param answeringDate.
     * The Question should have been submitted between @param startTime and @param
     * endTime (both time inclusive).
     *
     * The Questions with answeringdate = @param answeringDate should take
     * precedence over answeringdate < @param answeringDate. Break the ties using
     * question number.
     *
     * Returns an empty list (if there are no questions for the specified criteria)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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
     * @param answering date)
     * OR
     * Returns a list of Questions with size <= @param maxNoOfQuestions
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

}