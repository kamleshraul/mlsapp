/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionDates.java
 * Created On: 19 Jun, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionDates.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Configurable
@Entity
@Table(name = "question_dates")
public class QuestionDates extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The final submission date. */
    @Temporal(TemporalType.DATE)
    private Date finalSubmissionDate;

    /** The answering date. */
    @Temporal(TemporalType.DATE)
    private Date answeringDate;

      
    // ---------------------------------Constructors----------------------//

    /**
     * Instantiates a new question dates.
     */
    public QuestionDates() {
        super();
    }    

    // ----------------------------Domain Methods-------------------------//

    // ----------------------------Getters/Setters------------------------//    
    /**
     * Gets the final submission date.
     *
     * @return the final submission date
     */
    public Date getFinalSubmissionDate() {
        return finalSubmissionDate;
    }

    /**
     * Sets the final submission date.
     *
     * @param finalSubmissionDate the new final submission date
     */
    public void setFinalSubmissionDate(Date finalSubmissionDate) {
        this.finalSubmissionDate = finalSubmissionDate;
    }

    /**
     * Gets the answering date.
     *
     * @return the answering date
     */
    public Date getAnsweringDate() {
        return answeringDate;
    }

    /**
     * Sets the answering date.
     *
     * @param answeringDate the new answering date
     */
    public void setAnsweringDate(Date answeringDate) {
        this.answeringDate = answeringDate;
    }


    
}
