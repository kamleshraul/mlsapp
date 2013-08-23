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
 * The Class MemberBallotChoiceVO.
 *
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberBallotChoiceVO {

    /** The round. */
    private Integer round;

    /** The choice. */
    private Integer choice;

    /** The answering date. */
    private Long answeringDate;

    /** The question date. */
    private Long questionDate;


    /**
     * Gets the round.
     *
     * @return the round
     */
    public Integer getRound() {
        return round;
    }


    /**
     * Sets the round.
     *
     * @param round the new round
     */
    public void setRound(final Integer round) {
        this.round = round;
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
     * Gets the answering date.
     *
     * @return the answering date
     */
    public Long getAnsweringDate() {
        return answeringDate;
    }


    /**
     * Sets the answering date.
     *
     * @param answeringDate the new answering date
     */
    public void setAnsweringDate(final Long answeringDate) {
        this.answeringDate = answeringDate;
    }



    /**
     * Gets the question date.
     *
     * @return the question date
     */
    public Long getQuestionDate() {
        return questionDate;
    }



    /**
     * Sets the question date.
     *
     * @param questionDate the new question date
     */
    public void setQuestionDate(final Long questionDate) {
        this.questionDate = questionDate;
    }


}
