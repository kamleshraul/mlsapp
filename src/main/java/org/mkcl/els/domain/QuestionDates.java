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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.util.FormaterUtil;
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

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /**** Attributes ****/

    /** The final submission date. */
    @Temporal(TemporalType.DATE)
    private Date finalSubmissionDate;

    /** The answering date. */
    @Temporal(TemporalType.DATE)
    private Date answeringDate;

    @Temporal(TemporalType.DATE)
    private Date lastSendingDateToDepartment;

    @Temporal(TemporalType.DATE)
    private Date lastReceivingDateFromDepartment;
    
    @Temporal(TemporalType.DATE)
    private Date lastDateForChangingDepartment;

    @Temporal(TemporalType.DATE)
    private Date yaadiPrintingDate;

    @Temporal(TemporalType.DATE)
    private Date yaadiReceivingDate;

    @Temporal(TemporalType.DATE)
    private Date suchhiPrintingDate;

    @Temporal(TemporalType.DATE)
    private Date suchhiReceivingDate;

    @Temporal(TemporalType.DATE)
    private Date suchhiDistributionDate;
    
    @Temporal(TemporalType.DATE)
    private Date speakerSendingDate;
    
    /** The suchi publishing date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date suchiPublishingDate;

    /** flag for suchi publishing. */
    private Boolean suchiPublished;
    
    /** The time of ballot for questions with the answering date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date ballotTime;
    
    /** In exceptional cases to be shown as postponed date.. otherwise same as answering date **/
    @Temporal(TemporalType.DATE)
    private Date displayAnsweringDate;
    
    /** Yaadi Generation Allowed. */
    @Column(columnDefinition = "bit(1) default false")
    private boolean yaadiGenerationAllowed;

    /**** Constructors ****/

	/**
     * Instantiates a new question dates.
     */
    public QuestionDates() {
        super();
    }
    
    public QuestionDates(Date answeringDate){
    	this.answeringDate = answeringDate;
    }

    /**** Domain methods ****/
	/**** Added By Sandeep Singh (Jan 30 2013) ****/
	public String findFormattedAnsweringDate(){
		SimpleDateFormat format=FormaterUtil.getDateFormatter(this.getLocale());
		if(this.displayAnsweringDate!=null) {
			return format.format(getDisplayAnsweringDate());
		} else {
			return format.format(getAnsweringDate());
		}
	}
	
    public Date findAnsweringDateForReport() {
    	if(this.displayAnsweringDate!=null) {
    		return getDisplayAnsweringDate();
    	} else {
    		return getAnsweringDate();
    	}
    }
    
    
    /**** Getters and Setters ****/
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
    public void setFinalSubmissionDate(final Date finalSubmissionDate) {
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
    public void setAnsweringDate(final Date answeringDate) {
        this.answeringDate = answeringDate;
    }


    public Date getLastSendingDateToDepartment() {
        return lastSendingDateToDepartment;
    }


    public void setLastSendingDateToDepartment(final Date lastSendingDateToDepartment) {
        this.lastSendingDateToDepartment = lastSendingDateToDepartment;
    }


    public Date getLastReceivingDateFromDepartment() {
        return lastReceivingDateFromDepartment;
    }


    public void setLastReceivingDateFromDepartment(
            final Date lastReceivingDateFromDepartment) {
        this.lastReceivingDateFromDepartment = lastReceivingDateFromDepartment;
    }


    public Date getLastDateForChangingDepartment() {
		return lastDateForChangingDepartment;
	}

	public void setLastDateForChangingDepartment(Date lastDateForChangingDepartment) {
		this.lastDateForChangingDepartment = lastDateForChangingDepartment;
	}

	public Date getYaadiPrintingDate() {
        return yaadiPrintingDate;
    }


    public void setYaadiPrintingDate(final Date yaadiPrintingDate) {
        this.yaadiPrintingDate = yaadiPrintingDate;
    }


    public Date getYaadiReceivingDate() {
        return yaadiReceivingDate;
    }


    public void setYaadiReceivingDate(final Date yaadiReceivingDate) {
        this.yaadiReceivingDate = yaadiReceivingDate;
    }


    public Date getSuchhiPrintingDate() {
        return suchhiPrintingDate;
    }


    public void setSuchhiPrintingDate(final Date suchhiPrintingDate) {
        this.suchhiPrintingDate = suchhiPrintingDate;
    }


    public Date getSuchhiReceivingDate() {
        return suchhiReceivingDate;
    }


    public void setSuchhiReceivingDate(final Date suchhiReceivingDate) {
        this.suchhiReceivingDate = suchhiReceivingDate;
    }


    public Date getSuchhiDistributionDate() {
        return suchhiDistributionDate;
    }


    public void setSuchhiDistributionDate(final Date suchhiDistributionDate) {
        this.suchhiDistributionDate = suchhiDistributionDate;
    }

	public Date getSpeakerSendingDate() {
		return speakerSendingDate;
	}

	public void setSpeakerSendingDate(Date speakerSendingDate) {
		this.speakerSendingDate = speakerSendingDate;
	}
	
	
	public Boolean getSuchiPublished() {
		return suchiPublished;
	}

	public void setSuchiPublished(Boolean suchiPublished) {
		this.suchiPublished = suchiPublished;
	}
	
	public Date getSuchiPublishingDate() {
		return suchiPublishingDate;
	}

	public void setSuchiPublishingDate(Date suchiPublishingDate) {
		this.suchiPublishingDate = suchiPublishingDate;
	}

	public Date getBallotTime() {
		return ballotTime;
	}

	public void setBallotTime(Date ballotTime) {
		this.ballotTime = ballotTime;
	}

	public Date getDisplayAnsweringDate() {
		return displayAnsweringDate;
	}

	public void setDisplayAnsweringDate(Date displayAnsweringDate) {
		this.displayAnsweringDate = displayAnsweringDate;
	}
	
	
	public boolean getYaadiGenerationAllowed() {
			return yaadiGenerationAllowed;
	}

	public void setYaadiGenerationAllowed(boolean yaadiGenerationAllowed) {
			this.yaadiGenerationAllowed = yaadiGenerationAllowed;
	}
	
}