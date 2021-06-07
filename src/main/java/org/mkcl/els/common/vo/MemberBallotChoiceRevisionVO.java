/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberBallotChoiceVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;


/**
 * The Class MemberBallotChoiceRevisionVO.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
public class MemberBallotChoiceRevisionVO {
	
	//=============== ATTRIBUTES ====================
	/** The question number. */
    private String questionNumber;

    /** The round. */
    private String round;

    /** The choice. */
    private String choice;

    /** The answering date. */
    private String answeringDate;
    
    /** The flag for auto filled value. */
    private String isAutoFilled;
    
    /** The reason for choices update. */
    private String reasonForChoiceUpdate;
    
    /** The edited as. */
    private String editedAs;

	/** The edited by. */
	private String editedBY;

	/** The edited on. */
	private String editedOn;

    /** The revisions count. */
    private int revisionsCount;


	//---------------------------------Constructors----------------------------------------	
    public MemberBallotChoiceRevisionVO() {
		super();
	}

    //---------------------------------Getters & Setters----------------------------------------
	public String getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(String answeringDate) {
		this.answeringDate = answeringDate;
	}

	public String getIsAutoFilled() {
		return isAutoFilled;
	}

	public void setIsAutoFilled(String isAutoFilled) {
		this.isAutoFilled = isAutoFilled;
	}

	public String getReasonForChoiceUpdate() {
		return reasonForChoiceUpdate;
	}

	public void setReasonForChoiceUpdate(String reasonForChoiceUpdate) {
		this.reasonForChoiceUpdate = reasonForChoiceUpdate;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedBY() {
		return editedBY;
	}

	public void setEditedBY(String editedBY) {
		this.editedBY = editedBY;
	}

	public String getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(String editedOn) {
		this.editedOn = editedOn;
	}

	public int getRevisionsCount() {
		return revisionsCount;
	}

	public void setRevisionsCount(int revisionsCount) {
		this.revisionsCount = revisionsCount;
	}
    
}