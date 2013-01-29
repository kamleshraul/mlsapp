/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.SupportingMember.java
 * Created On: Sep 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class SupportingMember.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="supportingmembers")
@JsonIgnoreProperties({"member","decisionStatus"})
public class SupportingMember extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The member. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Member member;

    /** The status. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Status decisionStatus;

    /** The approval date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvalDate;

    /** The request received on. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestReceivedOn;

    /** The approved subject. */
    @Column(length=30000)
    private String approvedSubject;

    /** The approved question text. */
    @Column(length=30000)
    private String approvedQuestionText;

    /** The remarks. */
    @Column(length=30000)
    private String remarks;

    /**
     * Instantiates a new supporting member.
     */
    public SupportingMember() {
        super();
    }


    /**
     * Gets the member.
     *
     * @return the member
     */
    public Member getMember() {
        return member;
    }


    /**
     * Sets the member.
     *
     * @param member the new member
     */
    public void setMember(final Member member) {
        this.member = member;
    }

    /**
     * Gets the decision status.
     *
     * @return the decision status
     */
    public Status getDecisionStatus() {
        return decisionStatus;
    }



    /**
     * Sets the decision status.
     *
     * @param decisionStatus the new decision status
     */
    public void setDecisionStatus(final Status decisionStatus) {
        this.decisionStatus = decisionStatus;
    }


    /**
     * Gets the approval date.
     *
     * @return the approval date
     */
    public Date getApprovalDate() {
        return approvalDate;
    }


    /**
     * Sets the approval date.
     *
     * @param approvalDate the new approval date
     */
    public void setApprovalDate(final Date approvalDate) {
        this.approvalDate = approvalDate;
    }



    /**
     * Gets the approved subject.
     *
     * @return the approved subject
     */
    public String getApprovedSubject() {
        return approvedSubject;
    }



    /**
     * Sets the approved subject.
     *
     * @param approvedSubject the new approved subject
     */
    public void setApprovedSubject(final String approvedSubject) {
        this.approvedSubject = approvedSubject;
    }



    /**
     * Gets the approved question text.
     *
     * @return the approved question text
     */
    public String getApprovedQuestionText() {
        return approvedQuestionText;
    }


    /**
     * Sets the approved question text.
     *
     * @param approvedQuestionText the new approved question text
     */
    public void setApprovedQuestionText(final String approvedQuestionText) {
        this.approvedQuestionText = approvedQuestionText;
    }



    /**
     * Gets the request received on.
     *
     * @return the request received on
     */
    public Date getRequestReceivedOn() {
        return requestReceivedOn;
    }

    /**
     * Sets the request received on.
     *
     * @param requestReceivedOn the new request received on
     */
    public void setRequestReceivedOn(final Date requestReceivedOn) {
        this.requestReceivedOn = requestReceivedOn;
    }

    /**
     * Gets the remarks.
     *
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks.
     *
     * @param remarks the new remarks
     */
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
}
