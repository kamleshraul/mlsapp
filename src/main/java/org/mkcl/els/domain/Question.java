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
import java.util.Date;
import java.util.List;
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
import org.mkcl.els.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Question.
 *
 * @author Sandeep
 * @author Amit
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="questions")
@JsonIgnoreProperties({"houseType", "session","language","type", "supportingMembers", "ministry", "department", "subDepartment", "referencedQuestions", "drafts"})
public class Question extends BaseDomain
  implements Serializable
{
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The house type. */
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="houseType_id")
  private HouseType houseType;

  /** The session. */
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="session_id")
  private Session session;

  /** The type. */
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="questionType_id")
  private DeviceType type;
  
  /** The number. */
  private Integer number;

  /** The submission date. */
  @Temporal(TemporalType.TIMESTAMP)
  private Date submissionDate;

  /** The primary member. */
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="member_id")
  private Member primaryMember;

  /** The supporting members. */
  @ManyToMany(fetch=FetchType.LAZY)
  @JoinTable(name="questions_supportingmembers", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
  private List<Member> supportingMembers;

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

  /** The answering date. */
  @Temporal(TemporalType.TIMESTAMP)
  private Date answeringDate;
  
  /** The language. */
  // Added to Capture the language of the question entered
  @ManyToOne
  @JoinColumn(name="language_id")
  private Language language;

  /** The subject. */
  @Column(length=3000)
  private String subject;

  /** The question text. */
  @Column(length=30000)
  private String questionText;

  /** The answer. */
  @Column(length=30000)
  private String answer;
  
  /** The priority. */
  private Integer priority;

  /** The referenced questions. */
  @ManyToMany(fetch=FetchType.LAZY)
  @JoinTable(name="questions_references", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="reference_id", referencedColumnName="id")})
  private List<Question> referencedQuestions;

  /** The status. */
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="status_id")
  private Status status;

 

/** The drafts. */
@ManyToMany(fetch=FetchType.LAZY)
  @JoinTable(name="questions_drafts_association", joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="question_draft_id", referencedColumnName="id")})
  private List<QuestionDraft> drafts;

  /** The question repository. */
  @Autowired
  private transient QuestionRepository questionRepository;

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
   * Find last starred unstarred short notice question no.
   *
   * @param house the house
   * @param currentSession the current session
   * @return the integer
   * @author compaq
   * @since v1.0.0
   */
  public static Integer findLastStarredUnstarredShortNoticeQuestionNo(House house, Session currentSession) {
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
  public static Integer findLastHalfHourDiscussionQuestionNo(House house, Session currentSession) {
    return getQuestionRepository().findLastHalfHourDiscussionQuestionNo(house, currentSession);
  }

  /**
   * Assign question no.
   *
   * @param houseType the house type
   * @param session the session
   * @param questionType the question type
   * @return the integer
   * @author compaq
   * @since v1.0.0
   */
  public static Integer assignQuestionNo(HouseType houseType, Session session, DeviceType deviceType)
  {
    return getQuestionRepository().assignQuestionNo(houseType, 
      session, deviceType);
  }

  /**
   * Gets the house type.
   *
   * @return the house type
   */
  public HouseType getHouseType()
  {
    return this.houseType;
  }

  /**
   * Sets the house type.
   *
   * @param houseType the new house type
   */
  public void setHouseType(HouseType houseType) {
    this.houseType = houseType;
  }

  /**
   * Gets the session.
   *
   * @return the session
   */
  public Session getSession() {
    return this.session;
  }

  /**
   * Sets the session.
   *
   * @param session the new session
   */
  public void setSession(Session session) {
    this.session = session;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public DeviceType getType() {
    DeviceType qt = this.type;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      qt = draft.getType();
    }
    return qt;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(DeviceType type) {
    this.type = type;
  }

  /**
   * Gets the number.
   *
   * @return the number
   */
  public Integer getNumber() {
    return this.number;
  }

  /**
   * Sets the number.
   *
   * @param number the new number
   */
  public void setNumber(Integer number) {
    this.number = number;
  }

  /**
   * Gets the submission date.
   *
   * @return the submission date
   */
  public Date getSubmissionDate() {
    return this.submissionDate;
  }

  /**
   * Sets the submission date.
   *
   * @param submissionDate the new submission date
   */
  public void setSubmissionDate(Date submissionDate) {
    this.submissionDate = submissionDate;
  }

  /**
   * Gets the primary member.
   *
   * @return the primary member
   */
  public Member getPrimaryMember() {
    return this.primaryMember;
  }

  /**
   * Sets the primary member.
   *
   * @param primaryMember the new primary member
   */
  public void setPrimaryMember(Member primaryMember) {
    this.primaryMember = primaryMember;
  }

  /**
   * Gets the supporting members.
   *
   * @return the supporting members
   */
  public List<Member> getSupportingMembers() {
    List sm = this.supportingMembers;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      sm = draft.getSupportingMembers();
    }
    return sm;
  }

  /**
   * Sets the supporting members.
   *
   * @param supportingMembers the new supporting members
   */
  public void setSupportingMembers(List<Member> supportingMembers) {
    this.supportingMembers = supportingMembers;
  }

  /**
   * Gets the group.
   *
   * @return the group
   */
  public Group getGroup() {
    Group g = this.group;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      g = draft.getGroup();
    }
    return g;
  }

  /**
   * Sets the group.
   *
   * @param group the new group
   */
  public void setGroup(Group group) {
    this.group = group;
  }

  /**
   * Gets the ministry.
   *
   * @return the ministry
   */
  public Ministry getMinistry() {
    Ministry m = this.ministry;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      m = draft.getMinistry();
    }
    return m;
  }

  /**
   * Sets the ministry.
   *
   * @param ministry the new ministry
   */
  public void setMinistry(Ministry ministry) {
    this.ministry = ministry;
  }

  /**
   * Gets the department.
   *
   * @return the department
   */
  public Department getDepartment() {
    Department d = this.department;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      d = draft.getDepartment();
    }
    return d;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(Department department) {
    this.department = department;
  }

  /**
   * Gets the answering date.
   *
   * @return the answering date
   */
  public Date getAnsweringDate() {
    Date date = this.answeringDate;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      date = draft.getAnsweringDate();
    }
    return date;
  }

  /**
   * Sets the answering date.
   *
   * @param answeringDate the new answering date
   */
  public void setAnsweringDate(Date answeringDate) {
    this.answeringDate = answeringDate;
  }

  /**
   * Gets the subject.
   *
   * @return the subject
   */
  public String getSubject() {
    return this.subject;
  }

  /**
   * Sets the subject.
   *
   * @param subject the new subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Gets the question text.
   *
   * @return the question text
   */
  public String getQuestionText() {
    return this.questionText;
  }

  /**
   * Sets the question text.
   *
   * @param questionText the new question text
   */
  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  /**
   * Gets the priority.
   *
   * @return the priority
   */
  public Integer getPriority() {
    Integer p = this.priority;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      p = draft.getPriority();
    }
    return p;
  }

  /**
   * Sets the priority.
   *
   * @param priority the new priority
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  /**
   * Gets the referenced questions.
   *
   * @return the referenced questions
   */
  public List<Question> getReferencedQuestions() {
    List rq = this.referencedQuestions;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      rq = draft.getReferencedQuestions();
    }
    return rq;
  }

  /**
   * Sets the referenced questions.
   *
   * @param referencedQuestions the new referenced questions
   */
  public void setReferencedQuestions(List<Question> referencedQuestions) {
    this.referencedQuestions = referencedQuestions;
  }
  
  /**
   * Gets the status.
   *
   * @return the status
   */
  public Status getStatus() {
	  Status s=this.status;
	  QuestionDraft draft =getLatestDraft();
	  if(draft!=null){
		  s=draft.getStatus();
	  }
		return s;
	}
  
  /**
   * Sets the status.
   *
   * @param status the new status
   */
  public void setStatus(Status status) {
		this.status = status;
	}

  /**
   * Gets the sub department.
   *
   * @return the sub department
   */
  public SubDepartment getSubDepartment() {
    SubDepartment sd = this.subDepartment;
    QuestionDraft draft = getLatestDraft();
    if (draft != null) {
      sd = draft.getSubDepartment();
    }
    return sd;
  }

  

/**
 * Sets the sub department.
 *
 * @param subDepartment the new sub department
 */
public void setSubDepartment(SubDepartment subDepartment) {
    this.subDepartment = subDepartment;
  }

  /**
   * Gets the drafts.
   *
   * @return the drafts
   */
  public List<QuestionDraft> getDrafts() {
    return this.drafts;
  }

  /**
   * Sets the drafts.
   *
   * @param drafts the new drafts
   */
  public void setDrafts(List<QuestionDraft> drafts) {
    this.drafts = drafts;
  }

  /**
   * Gets the answer.
   *
   * @return the answer
   */
  public String getAnswer() {
    return this.answer;
  }

  /**
   * Sets the answer.
   *
   * @param answer the new answer
   */
  public void setAnswer(String answer) {
    this.answer = answer;
  }
  
   /**
    * Gets the language.
    *
    * @return the language
    */
   public Language getLanguage() {
	   Language l = this.language;
	   QuestionDraft latestDraft = getLatestDraft();
	   if(latestDraft != null){
		   l=latestDraft.getLanguage();
	   }
	return l;
}

/**
 * Sets the language.
 *
 * @param language the new language
 */
public void setLanguage(Language language) {
	this.language = language;
}

/**
 * Gets the revised subject.
 *
 * @return the revised subject
 */
public String getRevisedSubject()
  {
    String revisedSubject = getSubject();
    QuestionDraft latestDraft = getLatestDraft();
    if (latestDraft != null) {
      revisedSubject = latestDraft.getSubject();
    }
    return revisedSubject;
  }

  /**
   * Gets the revised question text.
   *
   * @return the revised question text
   */
  public String getRevisedQuestionText() {
    String revisedQuestionText = getQuestionText();
    QuestionDraft latestDraft = getLatestDraft();
    if (latestDraft != null) {
      revisedQuestionText = latestDraft.getQuestionText();
    }
    return revisedQuestionText;
  }

  /**
   * Gets the latest draft.
   *
   * @return the latest draft
   */
  private QuestionDraft getLatestDraft()
  {
    QuestionDraft draft = null;
    Integer size =0;
    if(getDrafts()!=null){
    	size=Integer.valueOf(getDrafts().size());
    }
    if (size.intValue() != 0) {
         draft = (QuestionDraft)getDrafts().get(size.intValue() - 1);
    }
    return draft;
  }
}