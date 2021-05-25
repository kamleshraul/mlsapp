/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberBallotChoice.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
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
import org.mkcl.els.repository.MemberBallotChoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberBallotChoice.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="memberballot_choice")
/*@JsonIgnoreProperties({"drafts"})*/
public class MemberBallotChoice extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The question. */
	@ManyToOne(fetch=FetchType.LAZY)
	private Question question;

	/** The new answering date. */
	@ManyToOne(fetch=FetchType.LAZY)
	private QuestionDates newAnsweringDate;

	/** The choice. */
	private Integer choice;

    /** The clubbing updated.To distinguish if clubbing has been updated using member ballot **/
    private Boolean clubbingUpdated;
    
    private Boolean processed=false;
    
    /** To distinguish choices which were manually filled and which were filled automatically **/
    private Boolean autoFilled=false;
    
    private Boolean blankFormAutoFilled=false;
    
/*	*//** The drafts. *//*
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="memberballot_choices_drafts_association", 
			joinColumns={@JoinColumn(name="memberballot_choice_id", referencedColumnName="id")}, 
			inverseJoinColumns={@JoinColumn(name="memberballot_choice_draft_id", referencedColumnName="id")})
	private List<MemberBallotChoiceDraft> drafts;  */
    
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


    /** The member ballot choice repository. */
    @Autowired
    private transient MemberBallotChoiceRepository memberBallotChoiceRepository;

    
    /**** Constructor ****/
    
	/**
	 * Instantiates a new member ballot choice.
	 */
	public MemberBallotChoice() {
		super();
	}

	/**** Domain methods ****/
    /**
     * Find by member.
     *
     * @param session the session
     * @param deviceType the device type
     * @param member the member
     * @param locale the locale
     * @return the list
     * @throws ELSException 
     */
    public static List<MemberBallotChoice> findByMember(final Session session,
            final DeviceType deviceType, final Member member, final String locale) throws ELSException {
        return getMemberBallotChoiceRepository().findByMember(session,deviceType,member,locale);
    }

    /**
     * Gets the member ballot choice repository.
     *
     * @return the member ballot choice repository
     */
    public static MemberBallotChoiceRepository getMemberBallotChoiceRepository() {
        MemberBallotChoiceRepository memberBallotChoiceRepository = new MemberBallotChoice().memberBallotChoiceRepository;
        if (memberBallotChoiceRepository == null) {
            throw new IllegalStateException(
                    "MemberBallotChoiceRepository has not been injected in MemberBallotChoice Domain");
        }
        return memberBallotChoiceRepository;
    }
    
    /**
     * @param session
     * @param questionType
     * @param member
     * @param pattern
     * @param orderby
     * @param sortorder
     * @param locale
     * @return
     * @throws ELSException
     */
    public static List<Question> findFirstBatchQuestions(final Session session,
			final DeviceType questionType,final Member member,final String pattern,
			final String orderby,final String sortorder,final String locale) throws ELSException {
		return getMemberBallotChoiceRepository().findFirstBatchQuestions(session,
				questionType,member,pattern,
				orderby,sortorder,locale);
	}
    
    public MemberBallot findCorrespondingMemberBallot() {
    	return getMemberBallotChoiceRepository().findCorrespondingMemberBallot(this);
    }
    

    /**** Getter and Setters for the MemberBallotChoice ****/
    
    /**
	 * Gets the question.
	 *
	 * @return the question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets the question.
	 *
	 * @param question the new question
	 */
	public void setQuestion(final Question question) {
		this.question = question;
	}


    /**
     * Gets the new answering date.
     *
     * @return the new answering date
     */
    public QuestionDates getNewAnsweringDate() {
        return newAnsweringDate;
    }


    /**
     * Sets the new answering date.
     *
     * @param newAnsweringDate the new new answering date
     */
    public void setNewAnsweringDate(final QuestionDates newAnsweringDate) {
        this.newAnsweringDate = newAnsweringDate;
    }
    
    
    /**
	 * Gets the choice.
	 *
	 * @return the choice
	 */
	public Integer getChoice() {
		return choice;
	}

	/**
	 * Sets the choice.
	 *
	 * @param choice the new choice
	 */
	public void setChoice(final Integer choice) {
		this.choice = choice;
	}
	
    
    /**
     * Sets the clubbing updated.
     *
     * @param clubbingUpdated the new clubbing updated
     */
    public void setClubbingUpdated(final Boolean clubbingUpdated) {
        this.clubbingUpdated = clubbingUpdated;
    }

    /**
     * Gets the clubbing updated.
     *
     * @return the clubbing updated
     */
    public Boolean getClubbingUpdated() {
        return clubbingUpdated;
    }

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public Boolean getAutoFilled() {
		return autoFilled;
	}

	public void setAutoFilled(Boolean autoFilled) {
		this.autoFilled = autoFilled;
	}

	public Boolean getBlankFormAutoFilled() {
		return blankFormAutoFilled;
	}

	public void setBlankFormAutoFilled(Boolean blankFormAutoFilled) {
		this.blankFormAutoFilled = blankFormAutoFilled;
	}
	
/*	public List<MemberBallotChoiceDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<MemberBallotChoiceDraft> drafts) {
		this.drafts = drafts;
	}*/

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

	/**** find member ballot choice at a particular round and choice ****/
	public static MemberBallotChoice findMemberBallotChoice(final Session session,final DeviceType deviceType,
			final Member member,final int round,final int choice){
		return getMemberBallotChoiceRepository().findMemberBallotChoice(session,deviceType,
				member,round,choice);
	}
	
	public static Boolean isQuestiongivenForChoice(final Question question){
		return getMemberBallotChoiceRepository().isQuestiongivenForChoice(question);
	}
}
