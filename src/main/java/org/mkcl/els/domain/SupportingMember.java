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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    private String approvedText;
    
  //--------------------bill specific fields---------------------//
    /** The position. */
    private Integer position;
    
    /** The bill type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="billtype_id")
    private BillType approvedBillType;
    
    /** The bill kind. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="billkind_id")
    private BillKind approvedBillKind;
    
    /** The title. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="supportingmembers_titles",
    joinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="approved_title_id", referencedColumnName="id")})
    private List<TextDraft> approvedTitles;
    
    /** The content. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name="supportingmembers_contentdrafts",
    joinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="content_draft_id", referencedColumnName="id")})
    private List<TextDraft> approvedContentDrafts;
    
    /** The statement of object and reason. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name="supportingmembers_statementofobjectandreasondrafts",
    joinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="statement_of_object_and_reason_draft_id", referencedColumnName="id")})
    private List<TextDraft> approvedStatementOfObjectAndReasonDrafts;
    
    /** The financial memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name="supportingmembers_financialmemorandumdrafts",
    joinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="financial_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> approvedFinancialMemorandumDrafts;
    
    /** The statutory memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name="supportingmembers_statutorymemorandumdrafts",
    joinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="statutory_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> approvedStatutoryMemorandumDrafts;
    
    /** The housetype where bill will be introduced first. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="introducing_housetype_id")
    private HouseType approvedIntroducingHouseType;

    /** The remarks. */
    @Column(length=30000)
    private String remarks;
    
    private String approvalType;
    
    /**** Workflow Related ****/
     private String workflowDetailsId;   

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
    /**
     * Gets the request received on.
     *
     * @return the request received on
     */
    public Date getRequestReceivedOn() {
        return requestReceivedOn;
    }

    public String getApprovedText() {
		return approvedText;
	}


	public void setApprovedText(String approvedText) {
		this.approvedText = approvedText;
	}


	/**
     * Sets the request received on.
     *
     * @param requestReceivedOn the new request received on
     */
    public void setRequestReceivedOn(final Date requestReceivedOn) {
        this.requestReceivedOn = requestReceivedOn;
    }

    public Integer getPosition() {
		return position;
	}


	public void setPosition(Integer position) {
		this.position = position;
	}


	public BillType getApprovedBillType() {
		return approvedBillType;
	}


	public void setApprovedBillType(BillType approvedBillType) {
		this.approvedBillType = approvedBillType;
	}


	public BillKind getApprovedBillKind() {
		return approvedBillKind;
	}


	public void setApprovedBillKind(BillKind approvedBillKind) {
		this.approvedBillKind = approvedBillKind;
	}


	public List<TextDraft> getApprovedTitles() {
		return approvedTitles;
	}


	public void setApprovedTitles(List<TextDraft> approvedTitles) {
		this.approvedTitles = approvedTitles;
	}


	public List<TextDraft> getApprovedContentDrafts() {
		return approvedContentDrafts;
	}


	public void setApprovedContentDrafts(List<TextDraft> approvedContentDrafts) {
		this.approvedContentDrafts = approvedContentDrafts;
	}


	public List<TextDraft> getApprovedStatementOfObjectAndReasonDrafts() {
		return approvedStatementOfObjectAndReasonDrafts;
	}


	public void setApprovedStatementOfObjectAndReasonDrafts(
			List<TextDraft> approvedStatementOfObjectAndReasonDrafts) {
		this.approvedStatementOfObjectAndReasonDrafts = approvedStatementOfObjectAndReasonDrafts;
	}


	public List<TextDraft> getApprovedFinancialMemorandumDrafts() {
		return approvedFinancialMemorandumDrafts;
	}


	public void setApprovedFinancialMemorandumDrafts(
			List<TextDraft> approvedFinancialMemorandumDrafts) {
		this.approvedFinancialMemorandumDrafts = approvedFinancialMemorandumDrafts;
	}


	public List<TextDraft> getApprovedStatutoryMemorandumDrafts() {
		return approvedStatutoryMemorandumDrafts;
	}


	public void setApprovedStatutoryMemorandumDrafts(
			List<TextDraft> approvedStatutoryMemorandumDrafts) {
		this.approvedStatutoryMemorandumDrafts = approvedStatutoryMemorandumDrafts;
	}


	public HouseType getApprovedIntroducingHouseType() {
		return approvedIntroducingHouseType;
	}


	public void setApprovedIntroducingHouseType(
			HouseType approvedIntroducingHouseType) {
		this.approvedIntroducingHouseType = approvedIntroducingHouseType;
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


	public void setApprovalType(String approvalType) {
		this.approvalType = approvalType;
	}


	public String getApprovalType() {
		return approvalType;
	}


	public void setWorkflowDetailsId(String workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}


	public String getWorkflowDetailsId() {
		return workflowDetailsId;
	}
}
