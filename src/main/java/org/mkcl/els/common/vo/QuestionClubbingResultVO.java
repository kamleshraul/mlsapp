/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.QuestionClubbingResultVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class QuestionClubbingResultVO.
 *
 * @author sandeeps
 * @since v1.0.0
 */
public class QuestionClubbingResultVO {

    /** The parent question. */
    private String parentQuestion;

    /** The child question. */
    private String childQuestion;

    /** The clubbed questions. */
    private List<Reference> clubbedQuestions;

    /** The questions to club to. */
    private List<Reference> questionsToClubTo;

    /** The flag. */
    private String flag;


    /**
     * Gets the parent question.
     *
     * @return the parent question
     */
    public String getParentQuestion() {
        return parentQuestion;
    }


    /**
     * Sets the parent question.
     *
     * @param parentQuestion the new parent question
     */
    public void setParentQuestion(final String parentQuestion) {
        this.parentQuestion = parentQuestion;
    }


    /**
     * Gets the child question.
     *
     * @return the child question
     */
    public String getChildQuestion() {
        return childQuestion;
    }


    /**
     * Sets the child question.
     *
     * @param childQuestion the new child question
     */
    public void setChildQuestion(final String childQuestion) {
        this.childQuestion = childQuestion;
    }


    /**
     * Gets the clubbed questions.
     *
     * @return the clubbed questions
     */
    public List<Reference> getClubbedQuestions() {
        return clubbedQuestions;
    }


    /**
     * Sets the clubbed questions.
     *
     * @param clubbedQuestions the new clubbed questions
     */
    public void setClubbedQuestions(final List<Reference> clubbedQuestions) {
        this.clubbedQuestions = clubbedQuestions;
    }


    /**
     * Gets the questions to club to.
     *
     * @return the questions to club to
     */
    public List<Reference> getQuestionsToClubTo() {
        return questionsToClubTo;
    }


    /**
     * Sets the questions to club to.
     *
     * @param questionsToClubTo the new questions to club to
     */
    public void setQuestionsToClubTo(final List<Reference> questionsToClubTo) {
        this.questionsToClubTo = questionsToClubTo;
    }


    /**
     * Gets the flag.
     *
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }


    /**
     * Sets the flag.
     *
     * @param flag the new flag
     */
    public void setFlag(final String flag) {
        this.flag = flag;
    }

}
