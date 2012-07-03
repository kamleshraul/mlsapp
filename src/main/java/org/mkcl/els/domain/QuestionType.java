/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionType.java
 * Created On: 19 Jun, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionType.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Configurable
@Entity
@Table(name = "questiontypes")
public class QuestionType extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The name. */
    @Column(length = 150)
    @NotEmpty
    private String name;    
    
    /** The has question limit. */
    @Column
    private Boolean hasQuestionLimit;
    
    /** The question limit. */
    @Column
    private Integer questionLimit;
    
    /** The question limiting action. */
    @ManyToOne
    @JoinColumn(name = "question_limiting_action__id")
    private QuestionLimitingAction questionLimitingAction;
    
    /** The warning message. */
    @Column(length=600)
    private String warningMessage;

    // ---------------------------------Constructors----------------------//

    /**
     * Instantiates a new question type.
     */
    public QuestionType() {
        super();
    }

    /**
     * Instantiates a new question type.
     *
     * @param name the name
     * @param locale the locale
     * @param version the version
     */
    public QuestionType(final String name, final String locale, final Long version) {
        super();
        this.name = name;

    }

    // ----------------------------Domain Methods-------------------------//

    // ----------------------------Getters/Setters------------------------//
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
	this.name = name;
    }

    /**
     * Gets the checks for question limit.
     *
     * @return the checks for question limit
     */
    public Boolean getHasQuestionLimit() {
	return hasQuestionLimit;
    }

    /**
     * Sets the checks for question limit.
     *
     * @param hasQuestionLimit the new checks for question limit
     */
    public void setHasQuestionLimit(Boolean hasQuestionLimit) {
	this.hasQuestionLimit = hasQuestionLimit;
    }

    /**
     * Gets the question limit.
     *
     * @return the question limit
     */
    public Integer getQuestionLimit() {
	return questionLimit;
    }

    /**
     * Sets the question limit.
     *
     * @param questionLimit the new question limit
     */
    public void setQuestionLimit(Integer questionLimit) {
	this.questionLimit = questionLimit;
    }

    /**
     * Gets the question limiting action.
     *
     * @return the question limiting action
     */
    public QuestionLimitingAction getQuestionLimitingAction() {
	return questionLimitingAction;
    }

    /**
     * Sets the question limiting action.
     *
     * @param questionLimitingAction the new question limiting action
     */
    public void setQuestionLimitingAction(
	    QuestionLimitingAction questionLimitingAction) {
	this.questionLimitingAction = questionLimitingAction;
    }

    /**
     * Gets the warning message.
     *
     * @return the warning message
     */
    public String getWarningMessage() {
	return warningMessage;
    }

    /**
     * Sets the warning message.
     *
     * @param warningMessage the new warning message
     */
    public void setWarningMessage(String warningMessage) {
	this.warningMessage = warningMessage;
    }
}
