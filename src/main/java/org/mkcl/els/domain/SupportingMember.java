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

/**
 * The Class SupportingMember.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Table(name="supportingmembers")
@JsonIgnoreProperties({"member","decisionStatus","questions"})
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

    @Temporal(TemporalType.TIMESTAMP)
    private Date requestReceivedOn;

    @Column(length=30000)
    private String approvedSubject;

    @Column(length=30000)
    private String approvedQuestionText;

    @Column(length=30000)
    private String remarks;
    
    private Boolean workflowCreated;

    /**
     * Instantiates a new supporting member.
     */
    public SupportingMember() {
        super();
    }


    public Member getMember() {
        return member;
    }


    public void setMember(final Member member) {
        this.member = member;
    }

    public Status getDecisionStatus() {
        return decisionStatus;
    }



    public void setDecisionStatus(final Status decisionStatus) {
        this.decisionStatus = decisionStatus;
    }


    public Date getApprovalDate() {
        return approvalDate;
    }


    public void setApprovalDate(final Date approvalDate) {
        this.approvalDate = approvalDate;
    }



    public String getApprovedSubject() {
        return approvedSubject;
    }



    public void setApprovedSubject(final String approvedSubject) {
        this.approvedSubject = approvedSubject;
    }



    public String getApprovedQuestionText() {
        return approvedQuestionText;
    }


    public void setApprovedQuestionText(final String approvedQuestionText) {
        this.approvedQuestionText = approvedQuestionText;
    }



    public Date getRequestReceivedOn() {
        return requestReceivedOn;
    }

    public void setRequestReceivedOn(final Date requestReceivedOn) {
        this.requestReceivedOn = requestReceivedOn;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }


	public Boolean getWorkflowCreated() {
		return workflowCreated;
	}


	public void setWorkflowCreated(Boolean workflowCreated) {
		this.workflowCreated = workflowCreated;
	}

    
}
