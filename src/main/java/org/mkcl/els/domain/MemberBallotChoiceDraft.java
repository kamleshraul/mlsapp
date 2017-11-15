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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name="memberballot_choice_draft")
public class MemberBallotChoiceDraft extends BaseDomain implements Serializable{

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
    
    private Long memberballotId;
    
    private Long memberballotChoiceId;


    /**** Constructor ****/
    
	/**
	 * Instantiates a new member ballot choice draft.
	 */
	public MemberBallotChoiceDraft() {
		super();
	}

    /**** Getter and Setters for the MemberBallotChoice ****/

	public Question getQuestion() {
		return question;
	}


	public void setQuestion(Question question) {
		this.question = question;
	}


	public QuestionDates getNewAnsweringDate() {
		return newAnsweringDate;
	}


	public void setNewAnsweringDate(QuestionDates newAnsweringDate) {
		this.newAnsweringDate = newAnsweringDate;
	}


	public Integer getChoice() {
		return choice;
	}


	public void setChoice(Integer choice) {
		this.choice = choice;
	}


	public Boolean getClubbingUpdated() {
		return clubbingUpdated;
	}


	public void setClubbingUpdated(Boolean clubbingUpdated) {
		this.clubbingUpdated = clubbingUpdated;
	}


	public Boolean getProcessed() {
		return processed;
	}


	public void setProcessed(Boolean processed) {
		this.processed = processed;
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

	public Long getMemberballotId() {
		return memberballotId;
	}

	public void setMemberballotId(Long memberballotId) {
		this.memberballotId = memberballotId;
	}

	public Long getMemberballotChoiceId() {
		return memberballotChoiceId;
	}

	public void setMemberballotChoiceId(Long memberballotChoiceId) {
		this.memberballotChoiceId = memberballotChoiceId;
	}
	
	
	
}
