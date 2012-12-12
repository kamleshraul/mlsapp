package org.mkcl.els.common.vo;

import java.util.List;


public class QuestionClubbingResultVO {

    private String parentQuestion;

    private String childQuestion;

    private List<Reference> clubbedQuestions;

    private List<Reference> questionsToClubTo;

    private String flag;


    public String getParentQuestion() {
        return parentQuestion;
    }


    public void setParentQuestion(final String parentQuestion) {
        this.parentQuestion = parentQuestion;
    }


    public String getChildQuestion() {
        return childQuestion;
    }


    public void setChildQuestion(final String childQuestion) {
        this.childQuestion = childQuestion;
    }


    public List<Reference> getClubbedQuestions() {
        return clubbedQuestions;
    }


    public void setClubbedQuestions(final List<Reference> clubbedQuestions) {
        this.clubbedQuestions = clubbedQuestions;
    }


    public List<Reference> getQuestionsToClubTo() {
        return questionsToClubTo;
    }


    public void setQuestionsToClubTo(final List<Reference> questionsToClubTo) {
        this.questionsToClubTo = questionsToClubTo;
    }


    public String getFlag() {
        return flag;
    }


    public void setFlag(final String flag) {
        this.flag = flag;
    }

}
