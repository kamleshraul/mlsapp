/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.GroupVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class GroupVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class GroupVO {
	
	/** The number. */
	private Integer number;
	
	/** The formatted number. */
	private String formattedNumber;
	
	/** 
	 * Whether this group has questions by given member. 
	 * Used for member ballot member-wise report. 
	 */
	private boolean hasQuestionsForGivenMember;

	/** The ministries. */
	private List<MasterVO> ministries;

	/** The departments. */
	private List<MasterVO> departments;

	/** The sub departments. */
	private List<MasterVO> subDepartments;

	/** The answering dates. */
	private List<Reference> answeringDates;	
	
	private List<MemberBallotMemberWiseQuestionVO> starredQuestionVOs;
	
	private List<MemberBallotMemberWiseQuestionVO> unstarredQuestionVOs;
	
	private List<MemberBallotMemberWiseQuestionVO> clarificationQuestionVOs;
	
	private List<MemberBallotMemberWiseQuestionVO> rejectedQuestionVOs;

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
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * Gets the formatted number.
	 *
	 * @return the formatted number
	 */
	public String getFormattedNumber() {
		return formattedNumber;
	}

	/**
	 * Sets the formatted number.
	 *
	 * @param formattedNumber the new formatted number
	 */
	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	/**
	 * Gets the checks for questions by given member.
	 *
	 * @return the checks for questions by given member
	 */
	public boolean getHasQuestionsForGivenMember() {
		return hasQuestionsForGivenMember;
	}

	/**
	 * Sets the checks for questions for given member.
	 *
	 * @param hasQuestionsForGivenMember the new checks for questions for given member
	 */
	public void setHasQuestionsForGivenMember(boolean hasQuestionsForGivenMember) {
		this.hasQuestionsForGivenMember = hasQuestionsForGivenMember;
	}

	/**
	 * Gets the ministries.
	 *
	 * @return the ministries
	 */
	public List<MasterVO> getMinistries() {
		return ministries;
	}

	/**
	 * Sets the ministries.
	 *
	 * @param ministries the new ministries
	 */
	public void setMinistries(final List<MasterVO> ministries) {
		this.ministries = ministries;
	}

	/**
	 * Gets the departments.
	 *
	 * @return the departments
	 */
	public List<MasterVO> getDepartments() {
		return departments;
	}

	/**
	 * Sets the departments.
	 *
	 * @param departments the new departments
	 */
	public void setDepartments(final List<MasterVO> departments) {
		this.departments = departments;
	}


    /**
     * Gets the answering dates.
     *
     * @return the answering dates
     */
    public List<Reference> getAnsweringDates() {
        return answeringDates;
    }


    /**
     * Sets the answering dates.
     *
     * @param answeringDates the new answering dates
     */
    public void setAnsweringDates(final List<Reference> answeringDates) {
        this.answeringDates = answeringDates;
    }


    /**
     * Gets the sub departments.
     *
     * @return the sub departments
     */
    public List<MasterVO> getSubDepartments() {
        return subDepartments;
    }


    /**
     * Sets the sub departments.
     *
     * @param subDepartments the new sub departments
     */
    public void setSubDepartments(final List<MasterVO> subDepartments) {
        this.subDepartments = subDepartments;
    }

	public List<MemberBallotMemberWiseQuestionVO> getStarredQuestionVOs() {
		return starredQuestionVOs;
	}

	public void setStarredQuestionVOs(
			List<MemberBallotMemberWiseQuestionVO> starredQuestionVOs) {
		this.starredQuestionVOs = starredQuestionVOs;
	}

	public List<MemberBallotMemberWiseQuestionVO> getUnstarredQuestionVOs() {
		return unstarredQuestionVOs;
	}

	public void setUnstarredQuestionVOs(
			List<MemberBallotMemberWiseQuestionVO> unstarredQuestionVOs) {
		this.unstarredQuestionVOs = unstarredQuestionVOs;
	}

	public List<MemberBallotMemberWiseQuestionVO> getClarificationQuestionVOs() {
		return clarificationQuestionVOs;
	}

	public void setClarificationQuestionVOs(
			List<MemberBallotMemberWiseQuestionVO> clarificationQuestionVOs) {
		this.clarificationQuestionVOs = clarificationQuestionVOs;
	}

	public List<MemberBallotMemberWiseQuestionVO> getRejectedQuestionVOs() {
		return rejectedQuestionVOs;
	}

	public void setRejectedQuestionVOs(
			List<MemberBallotMemberWiseQuestionVO> rejectedQuestionVOs) {
		this.rejectedQuestionVOs = rejectedQuestionVOs;
	}
}
