package org.mkcl.els.common.vo;


public class MemberBallotChoiceVO {

    private Integer round;

    private Integer choice;

    private Long answeringDate;

    private Long questionDate;


    public Integer getRound() {
        return round;
    }


    public void setRound(final Integer round) {
        this.round = round;
    }


    public Integer getChoice() {
        return choice;
    }


    public void setChoice(final Integer choice) {
        this.choice = choice;
    }

    public Long getAnsweringDate() {
        return answeringDate;
    }


    public void setAnsweringDate(final Long answeringDate) {
        this.answeringDate = answeringDate;
    }



    public Long getQuestionDate() {
        return questionDate;
    }



    public void setQuestionDate(final Long questionDate) {
        this.questionDate = questionDate;
    }


}
