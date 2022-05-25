/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Bill.java
 * Created On: Sep 20, 2013
 * @since 1.0
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ActSearchVO;
import org.mkcl.els.common.vo.OrdinanceSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="bills")
@JsonIgnoreProperties({"houseType", "introducingHouseType", "session", "originalType", "type", "billType", "billKind",
	"titles", "contentDrafts", "statementOfObjectAndReasonDrafts", "revisedStatementOfObjectAndReasonDrafts", 
	"financialMemorandumDrafts", "revisedFinancialMemorandumDrafts", "statutoryMemorandumDrafts", "revisedStatutoryMemorandumDrafts", 
	"revisedContentDrafts", "referredAct", "referredOrdinance", "ballotStatus", "discussionStatus", "supportingMembers", 
	"drafts", "parent", "clubbedEntities", "referencedBill", "lapsedBill", "introducedBy", "votingDetails", "currentHouseType"})
public class Bill extends Device implements Serializable {
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    //=============== BASIC ATTRIBUTES ====================
    
    /** The priority. */
    private Integer priority;
    
    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;
    
    /** The introducing house type for government bill.
     *  For government bill, Minister decides the housetype where bill will be introduced first. 
     *  It is captured here.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="introducing_housetype_id")
    private HouseType introducingHouseType;

    /** The session. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="originaldevicetype_id")
    private DeviceType originalType;
    
    /** The type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType type;
    
    /** The bill type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="billtype_id")
    private BillType billType;
    
    /** The bill kind. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="billkind_id")
    private BillKind billKind;

    /** The number. */
    private Integer number;
    
    /** The submission date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;

    /** The creation date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    /** The created by. */
    @Column(length=1000)
    private String createdBy;
    
    /**** The clerk name ****/
	private String dataEnteredBy;
    
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)    
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
    
    /** The date of opinion sought from law and judiciary department. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfOpinionSoughtFromLawAndJD;
    
    /** The opinion sought from law and judiciary department. */
    @Column(length=30000)
    private String opinionSoughtFromLawAndJD;
    
    /** The file having the text. */
	@Column(length = 100)
	private String opinionSoughtFromLawAndJDFile;
    
    /** The date of recommendation received from governor. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfRecommendationFromGovernor;
    
    /** The recommendation from governor. */
    @Column(length=30000)
    private String recommendationFromGovernor;
    
    /** The date of recommendation received from president. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfRecommendationFromPresident;
    
    /** The recommendation from president. */
    @Column(length=30000)
    private String recommendationFromPresident;
    
    /** The admission date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date admissionDate;
    
    /** The rejection date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date rejectionDate;
    
    /** The current house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="current_housetype_id")
    private HouseType currentHouseType;
    
    /** The expected date of setting status or getting response from the status for the bill. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedStatusDate;
    
    /** The date of setting status for the bill. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;
    
    /** The house round for bill. */
    private Integer houseRound;

    /** The introduction date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedIntroductionDate;
    
    /** The date of moving for consideration. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfMovingForConsideration;
    
    /** The discussion date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedDiscussionDate; 
    
    @Transient
    private String defaultTitle;
    
    /** The title. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_titles",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="title_id", referencedColumnName="id")})
    private List<TextDraft> titles;
    
    /** The revised title. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_revisedtitles",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="revised_title_id", referencedColumnName="id")})
    private List<TextDraft> revisedTitles;
    
    /** The content. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_contentdrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="content_draft_id", referencedColumnName="id")})
    private List<TextDraft> contentDrafts;
    
    /** The revised content. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_revisedcontentdrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="revised_content_draft_id", referencedColumnName="id")})
    private List<TextDraft> revisedContentDrafts;
    
    /** The sections. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_sections",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="section_id", referencedColumnName="id")})
    private List<Section> sections;

    /** The statement of object and reason. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_statementofobjectandreasondrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="statement_of_object_and_reason_draft_id", referencedColumnName="id")})
    private List<TextDraft> statementOfObjectAndReasonDrafts;
    
    /** The revised statement of object and reason. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_revisedstatementofobjectandreasondrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="revised_statement_of_object_and_reason_draft_id", referencedColumnName="id")})
    private List<TextDraft> revisedStatementOfObjectAndReasonDrafts;
    
    /** The financial memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_financialmemorandumdrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="financial_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> financialMemorandumDrafts;
    
    /** The revised financial memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_revisedfinancialmemorandumdrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="revised_financial_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> revisedFinancialMemorandumDrafts;
    
    /** The statutory memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_statutorymemorandumdrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="statutory_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> statutoryMemorandumDrafts;
    
    /** The revised statutory memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_revisedstatutorymemorandumdrafts",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="revised_statutory_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> revisedStatutoryMemorandumDrafts;
    
    /** The referred act if any. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="referred_act_id")
    private Act referredAct;
    
    /** The referred ordinance if any. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="referred_ordinance_id")
    private Ordinance referredOrdinance;
    
    /** The annexure for amending bill. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_annexuresforamendingbill",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="annexure_for_amending_bill_id", referencedColumnName="id")})
    private List<TextDraft> annexuresForAmendingBill;
    
    /** The revised annexure for amending bill. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="bills_revisedannexuresforamendingbill",
    joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="revised_annexure_for_amending_bill_id", referencedColumnName="id")})
    private List<TextDraft> revisedAnnexuresForAmendingBill;
    
    /** The checklist. */
	@ElementCollection
	@JoinColumn(name="bill_id")
    @MapKeyColumn(name="checklist_key")
    @Column(name="checklist_value",length=10000)
    @CollectionTable(name="bill_checklist")
	private Map<String,String> checklist;
    
    /** The remarks. */
    @Column(length=30000)
    private String remarks;
    
    /** The remarks for translation. */
    @Column(length=30000)
    private String remarksForTranslation;
    
    /** The rejection reason **/
    @Column(length=30000)
	private String rejectionReason;
    
    /** 
     * The status. Refers to various final status viz, SUBMITTED,
     * ADMITTED, REJECTED, INTRODUCED etc. 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    /** 
     * The internal status. Refers to status assigned to a Bill
     * during the Workflow
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;
    
    /** 
     * If a bill is balloted then its balloted status is set to balloted 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ballotstatus_id")
    private Status ballotStatus;
    
    /** If bill is selected for discussion *. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="discussionstatus_id")
    private Status discussionStatus;
    
    /** is bill lapsed? **/
    private Boolean isLapsed;    
    
    /** is bill incomplete? **/
    private Boolean isIncomplete;
    
  //=============== PRIMARY & SUPPORTING MEMBERS ====================
    /** The primary member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member primaryMember;

    /** The supporting members. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="bills_supportingmembers",
            joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
    private List<SupportingMember> supportingMembers;

    
    //=============== MINISTRY ATTRIBUTERS ====================
    /** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;
    
    //=============== DRAFTS ====================
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="bills_drafts_association", 
    		joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")})
    private List<BillDraft> drafts;
    
  //=============== Clubbing ====================
    /** The parent. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Bill parent;
    
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="bills_clubbingentities", 
    		joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;
    
    /** The referenced bill. */
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private ReferencedEntity referencedBill;
    
    /** The referenced bill. */
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private LapsedEntity lapsedBill;
    
    /** The member who actually introduced the bill. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="introduced_by_member_id")
    private Member introducedBy;
    
    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="bills_votingdetails", 
    		joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="voting_detail_id", referencedColumnName="id")})
    private List<VotingDetail> votingDetails;    
    
    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="bills_layingletters", 
    		joinColumns={@JoinColumn(name="bill_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="laying_letter_id", referencedColumnName="id")})
    private List<LayingLetter> layingLetters;
    
    private Integer file;

    private Integer fileIndex;

    private Boolean fileSent;
    
    /** The bill repository. */
    @Autowired
    private transient BillRepository billRepository;
    
  //-----------------------------Constructors--------------------------------
    /**
     * Instantiates a new bill.
     */
    public Bill() {
		super();
	}

  //-----------------------------Domain Methods--------------------------------
    /**
     * Gets the bill repository.
     *
     * @return the bill repository
     */
    private static BillRepository getBillRepository() {
    	BillRepository billRepository = new Bill().billRepository;
        if (billRepository == null) {
            throw new IllegalStateException(
            	"BillRepository has not been injected in Bill Domain");
        }
        return billRepository;
    }
    
    
    @Override
    public Bill persist() {
    	if(this.getStatus().getType().equals(ApplicationConstants.BILL_SUBMIT)) {
    		if(this.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
    			if(this.getNumber() == null) {
    				synchronized (this) {
    					String billSubmissionYear = FormaterUtil.formatDateToString(this.getSubmissionDate(), "yyyy");
    					Integer number = Bill.assignBillNo(billSubmissionYear, this.getIntroducingHouseType(), this.getLocale());
    					this.setNumber(number + 1);
    				}
    			}
    		}
    		addBillDraft();
    	}
    	return (Bill)super.persist();
    }
    
    @Override
    public Bill merge() {    	
    	Bill bill = null;
    	if(this.getStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION) 
    			&& this.getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)
    			&& this.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
    		if(this.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
    			if(this.getNumber() == null) {
    				synchronized (this) {
    					String billAdmissionYear = FormaterUtil.formatDateToString(this.getAdmissionDate(), "yyyy");
    					Integer number = Bill.assignBillNo(billAdmissionYear, this.getHouseType(), this.getLocale());
    					this.setNumber(number + 1);
    				}
    			}
    		}
    		addBillDraft();
    		bill = (Bill)super.merge();
    	} else if(this.getInternalStatus().getType().equals(ApplicationConstants.BILL_SUBMIT)) {
    		if(this.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
    			if(this.getNumber() == null) {
    				synchronized (this) {
    					String billSubmissionYear = FormaterUtil.formatDateToString(this.getSubmissionDate(), "yyyy");
    					Integer number = Bill.assignBillNo(billSubmissionYear, this.getIntroducingHouseType(), this.getLocale());
    					this.setNumber(number + 1);
    				}
    			}
    		}
    		addBillDraft();
    		bill = (Bill)super.merge();
    	} else if(this.getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED)) {
    		if(this.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
    			if(this.getClubbedEntities() == null || this.getReferencedBill()==null) {
    				Bill oldBill = Bill.findById(Bill.class, this.getId());   
    				if(this.getClubbedEntities() == null) {
    					this.setClubbedEntities(oldBill.getClubbedEntities());
    				}
    				if(this.getReferencedBill()==null) {
    					this.setReferencedBill(oldBill.getReferencedBill());
    				}    				
    			}
    		}    		
    		addBillDraft();
    		bill = (Bill)super.merge();
    	}
    	if(bill != null) {
    		return bill;
    	} else {
    		if(this.getInternalStatus().getType().equals(ApplicationConstants.BILL_INCOMPLETE) 
                	|| 
                	this.getInternalStatus().getType().equals(ApplicationConstants.BILL_COMPLETE)) {
                    return (Bill) super.merge();
                }
                else {
                	if(this.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
            			if(this.getClubbedEntities() == null || this.getReferencedBill()==null) {
            				Bill oldBill = Bill.findById(Bill.class, this.getId());   
            				if(this.getClubbedEntities() == null) {
            					this.setClubbedEntities(oldBill.getClubbedEntities());
            				}
            				if(this.getReferencedBill()==null) {
            					this.setReferencedBill(oldBill.getReferencedBill());
            				}    				
            			}
            		}
                	addBillDraft();
                	return (Bill)super.merge();
                }
    	}    	
    }
    
    /**
     * The merge function, besides updating Bill, performs various actions
     * based on Bill's status. What if we need just the simple functionality
     * of updation? Use this method.
     *
     * @return the bill
     */
    public Bill simpleMerge() {
        Bill bill = (Bill) super.merge();
        return bill;
    }    
    
//    private void 
    		
    /**
     * Adds the bill draft.
     */
    private void addBillDraft() {
        if(! this.getStatus().getType().equals(ApplicationConstants.BILL_INCOMPLETE) 
        		&& ! this.getStatus().getType().equals(ApplicationConstants.BILL_COMPLETE)) {
            BillDraft draft = new BillDraft();
            draft.setType(this.getType());            
            draft.setRemarks(this.getRemarks());           
            
            draft.setEditedAs(this.getEditedAs());
            draft.setEditedBy(this.getEditedBy());
            draft.setEditedOn(this.getEditedOn());
            
            draft.setMinistry(this.getMinistry());
            draft.setSubDepartment(this.getSubDepartment());
            
            draft.setStatus(this.getStatus());
            draft.setInternalStatus(this.getInternalStatus());
            draft.setRecommendationStatus(this.getRecommendationStatus());  
            
            if(this.getCurrentHouseType()!=null) {
            	draft.setHouseType(this.getCurrentHouseType());
            }
            
            if(this.getHouseRound()!=null) {
            	draft.setHouseRound(this.getHouseRound());
            }
            
            if(this.getExpectedStatusDate()!=null) {
            	draft.setExpectedStatusDate(this.getExpectedStatusDate());
            }
            
            if(this.getStatusDate()!=null) {
            	draft.setStatusDate(this.getStatusDate());
            }
            
            if(this.getRevisedTitles()!= null && !this.getRevisedTitles().isEmpty()) {            	
            	draft.setTitles(this.addDraftsOfGivenTypeForBillDraft("titles", this.getRevisedTitles()));
            } else {
            	draft.setTitles(this.addDraftsOfGivenTypeForBillDraft(this.getTitles()));
            }
            
            if(this.getRevisedContentDrafts()!= null && !this.getRevisedContentDrafts().isEmpty()) {            	
            	draft.setContentDrafts(this.addDraftsOfGivenTypeForBillDraft("contentDrafts", this.getRevisedContentDrafts()));
            } else {
            	draft.setContentDrafts(this.addDraftsOfGivenTypeForBillDraft(this.getContentDrafts()));
            }
            
//            if(this.getSections()!= null) {            	
//            	draft.setSections(this.addSectionsForBillDraft(this.getSections()));
//            }
            
            if(this.getRevisedStatementOfObjectAndReasonDrafts()!= null && !this.getRevisedStatementOfObjectAndReasonDrafts().isEmpty()) {
            	draft.setStatementOfObjectAndReasonDrafts(this.addDraftsOfGivenTypeForBillDraft("statementOfObjectAndReasonDrafts", this.getRevisedStatementOfObjectAndReasonDrafts()));
            } else {
            	draft.setStatementOfObjectAndReasonDrafts(this.addDraftsOfGivenTypeForBillDraft(this.getStatementOfObjectAndReasonDrafts()));
            }
            
            if(this.getRevisedFinancialMemorandumDrafts()!= null && !this.getRevisedFinancialMemorandumDrafts().isEmpty()) {
            	draft.setFinancialMemorandumDrafts(this.addDraftsOfGivenTypeForBillDraft("financialMemorandumDrafts", this.getRevisedFinancialMemorandumDrafts()));
            } else {
            	draft.setFinancialMemorandumDrafts(this.addDraftsOfGivenTypeForBillDraft(this.getFinancialMemorandumDrafts()));
            }
            
            if(this.getRevisedStatutoryMemorandumDrafts()!= null && !this.getRevisedStatutoryMemorandumDrafts().isEmpty()) {
            	draft.setStatutoryMemorandumDrafts(this.addDraftsOfGivenTypeForBillDraft("statutoryMemorandumDrafts", this.getRevisedStatutoryMemorandumDrafts()));
            } else {
            	draft.setStatutoryMemorandumDrafts(this.addDraftsOfGivenTypeForBillDraft(this.getStatutoryMemorandumDrafts()));
            }
            
            if(this.getRevisedAnnexuresForAmendingBill()!= null && !this.getRevisedAnnexuresForAmendingBill().isEmpty()) {            	
            	draft.setAnnexuresForAmendingBill(this.addDraftsOfGivenTypeForBillDraft("annexuresForAmendingBill", this.getRevisedAnnexuresForAmendingBill()));
            } else {
            	draft.setAnnexuresForAmendingBill(this.addDraftsOfGivenTypeForBillDraft(this.getAnnexuresForAmendingBill()));
            }
            
            draft.setChecklist(this.getChecklist());
            
            draft.setReferredAct(this.getReferredAct());
            
            draft.setParent(this.getParent());
            draft.setClubbedEntities(this.getClubbedEntities());
            
            draft.setReferencedBill(this.getReferencedBill());            
            
            if(this.getId() != null) {
                Bill bill = Bill.findById(Bill.class, this.getId());
                List<BillDraft> originalDrafts = bill.getDrafts();
                if(originalDrafts != null){
                    originalDrafts.add(draft);
                }
                else{
                    originalDrafts = new ArrayList<BillDraft>();
                    originalDrafts.add(draft);
                }
                this.setDrafts(originalDrafts);
            }
            else {
                List<BillDraft> originalDrafts = new ArrayList<BillDraft>();
                originalDrafts.add(draft);
                this.setDrafts(originalDrafts);
            }
        }
    }
    
    public static Boolean isExist(final Bill bill) {
    	return getBillRepository().isExist(bill);
    }
    
    private static Integer assignBillNo(final String year, final HouseType houseType, final String locale) {		
		return getBillRepository().assignBillNo(year, houseType, locale);
	}

    private List<TextDraft> addDraftsOfGivenTypeForBillDraft(List<TextDraft> draftsOfGivenType) {
    	if(draftsOfGivenType!=null) {
    		List<TextDraft> draftsOfGivenTypeForBillDraft = new ArrayList<TextDraft>();    	
        	for(TextDraft draftOfGivenType : draftsOfGivenType) {
        		TextDraft draftOfGivenTypeForBillDraft = new TextDraft();
        		draftOfGivenTypeForBillDraft.setLanguage(draftOfGivenType.getLanguage());
        		draftOfGivenTypeForBillDraft.setText(draftOfGivenType.getText());
        		draftOfGivenTypeForBillDraft.setFile(draftOfGivenType.getFile());
        		draftOfGivenTypeForBillDraft.setLocale(draftOfGivenType.getLocale());
        		draftsOfGivenTypeForBillDraft.add(draftOfGivenTypeForBillDraft);
        	}
        	return draftsOfGivenTypeForBillDraft;
    	} else {
    		return null;
    	}    	
    }
    
    private TextDraft findDraftOfGivenTypeInGivenLanguage(String typeOfDraft, String languageType) {
    	TextDraft draftOfGivenTypeInGivenLanguage = null;
    	List<TextDraft> draftsOfGivenType = null;     	
    	if(typeOfDraft.equals("titles")) {
    		draftsOfGivenType = this.getTitles();
    	} else if(typeOfDraft.equals("revisedTitles")) {
    		draftsOfGivenType = this.getRevisedTitles();
    	} else if(typeOfDraft.equals("contentDrafts")) {
    		draftsOfGivenType = this.getContentDrafts();
    	} else if(typeOfDraft.equals("revisedContentDrafts")) {
    		draftsOfGivenType = this.getRevisedContentDrafts();
    	} else if(typeOfDraft.equals("statementOfObjectAndReasonDrafts")) {
    		draftsOfGivenType = this.getStatementOfObjectAndReasonDrafts();
    	} else if(typeOfDraft.equals("revisedStatementOfObjectAndReasonDrafts")) {
    		draftsOfGivenType = this.getRevisedStatementOfObjectAndReasonDrafts();
    	} else if(typeOfDraft.equals("financialMemorandumDrafts")) {
    		draftsOfGivenType = this.getFinancialMemorandumDrafts();
    	} else if(typeOfDraft.equals("revisedFinancialMemorandumDrafts")) {
    		draftsOfGivenType = this.getRevisedFinancialMemorandumDrafts();
    	} else if(typeOfDraft.equals("statutoryMemorandumDrafts")) {
    		draftsOfGivenType = this.getStatutoryMemorandumDrafts();
    	} else if(typeOfDraft.equals("revisedStatutoryMemorandumDrafts")) {
    		draftsOfGivenType = this.getRevisedStatutoryMemorandumDrafts();
    	} else if(typeOfDraft.equals("annexuresForAmendingBill")) {
    		draftsOfGivenType = this.getAnnexuresForAmendingBill();
    	} else if(typeOfDraft.equals("revisedAnnexuresForAmendingBill")) {
    		draftsOfGivenType = this.getRevisedAnnexuresForAmendingBill();
    	}  	
    	if(draftsOfGivenType != null) {
    		for(TextDraft td: draftsOfGivenType) {
    			if(td.getLanguage().getType().equals(languageType)) {
    				draftOfGivenTypeInGivenLanguage = td;
    				break;
    			}
    		}
    	}    	
    	return draftOfGivenTypeInGivenLanguage;
    }
    
    private List<TextDraft> addDraftsOfGivenTypeForBillDraft(String typeOfDraft, List<TextDraft> draftsOfGivenType) {
    	List<TextDraft> draftsOfGivenTypeForBillDraft = new ArrayList<TextDraft>();    	
    	String revisedTypeOfDraft = "revised" + Character.toUpperCase(typeOfDraft.charAt(0)) + typeOfDraft.substring(1);
    	String[] languagesForDraft = this.getSession().getParameter(this.getType().getType() + "_languagesAllowed").split("#");
    	for(String languageForDraft : languagesForDraft) {    		
    		TextDraft existingDraftOfGivenType = findDraftOfGivenTypeInGivenLanguage(revisedTypeOfDraft, languageForDraft);
    		if(existingDraftOfGivenType==null) {
    			existingDraftOfGivenType = findDraftOfGivenTypeInGivenLanguage(typeOfDraft, languageForDraft);
    		}
    		if(existingDraftOfGivenType!=null) {
    			TextDraft draftOfGivenTypeForBillDraft = new TextDraft();
        		draftOfGivenTypeForBillDraft.setLanguage(existingDraftOfGivenType.getLanguage());
        		draftOfGivenTypeForBillDraft.setText(existingDraftOfGivenType.getText());
        		draftOfGivenTypeForBillDraft.setFile(existingDraftOfGivenType.getFile());
        		draftOfGivenTypeForBillDraft.setLocale(existingDraftOfGivenType.getLocale());
        		draftsOfGivenTypeForBillDraft.add(draftOfGivenTypeForBillDraft);
    		}
    	}    	
    	return draftsOfGivenTypeForBillDraft;
    }
    
//    private List<Section> addSectionsForBillDraft(List<Section> sections) {
//    	if(sections!=null) {
//    		List<Section> sectionDrafts = new ArrayList<Section>();    	
//        	for(Section section : sections) {
//        		Section sectionDraft = new Section();
//        		sectionDraft.setNumber(section.getNumber());
//        		sectionDraft.setLanguage(section.getLanguage());
//        		sectionDraft.setText(section.getText());
//        		sectionDraft.setLocale(section.getLocale());
//        		sectionDrafts.add(sectionDraft);
//        	}
//        	return sectionDrafts;
//    	} else {
//    		return null;
//    	}    	
//    }
    
    public static List<Bill> findAllByMember(final Session session,
			final Member primaryMember,final DeviceType deviceType,final Integer itemsCount,
			final String locale) {
		return getBillRepository().findAllByMember(session,
				primaryMember,deviceType,itemsCount,
				locale);
	}
    
    public static List<Bill> findAllByYear(final Integer billYear, final String locale) {
    	return getBillRepository().findAllByYear(billYear, locale);
    }
    
    public static List<Bill> findAllByIntroducingHouseType(final String introducingHouseType, final String locale) {
    	return getBillRepository().findAllByIntroducingHouseType(introducingHouseType, locale);
    }
    
    public static List<Bill> findAllInYearByIntroducingHouseType(final Integer billYear, final String introducingHouseType, final String locale) {
    	return getBillRepository().findAllInYearByIntroducingHouseType(billYear, introducingHouseType, locale);
    }
    
    public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}
    
    public String getDefaultTitle() {
    	String defaultTitle = "";
    	String defaultTitleLanguage = this.getSession().getParameter(this.getType().getType()+"_defaultTitleLanguage");
    	if(defaultTitleLanguage!=null&&!defaultTitleLanguage.isEmpty()) {
    		if(this.getRevisedTitles()!=null) {
        		if(!this.getRevisedTitles().isEmpty()) {
        			for(TextDraft td: this.getRevisedTitles()) {
            			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
            				defaultTitle = td.getText();
            				break;
            			}
            		}
        			if(defaultTitle.isEmpty()) {
        				if(this.getTitles()!=null) {
        	        		if(!this.getTitles().isEmpty()) {
        	    				for(TextDraft td: this.getTitles()) {
        	            			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
        	            				defaultTitle = td.getText();
        	            				break;
        	            			}
        	            		}
        	    			}
        	        	}
        			}
        		} else if(this.getTitles()!=null) {
        			if(!this.getTitles().isEmpty()) {
        				for(TextDraft td: this.getTitles()) {
                			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
                				defaultTitle = td.getText();
                				break;
                			}
                		}
        			}        		
            	}
        	} else if(this.getTitles()!=null) {
        		if(!this.getTitles().isEmpty()) {
    				for(TextDraft td: this.getTitles()) {
            			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
            				defaultTitle = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	}    	    	
    	return defaultTitle;
    }
    
    public String getDefaultContentDraft() {
    	String defaultContentDraft = "";
    	String defaultTitleLanguage = this.getSession().getParameter(this.getType().getType()+"_defaultTitleLanguage");
    	if(defaultTitleLanguage!=null&&!defaultTitleLanguage.isEmpty()) {
    		if(this.getRevisedContentDrafts()!=null) {
        		if(!this.getRevisedContentDrafts().isEmpty()) {
        			for(TextDraft td: this.getRevisedContentDrafts()) {
            			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
            				defaultContentDraft = td.getText();
            				break;
            			}
            		}
        			if(defaultContentDraft.isEmpty()) {
        				if(this.getContentDrafts()!=null) {
        	        		if(!this.getContentDrafts().isEmpty()) {
        	    				for(TextDraft td: this.getContentDrafts()) {
        	            			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
        	            				defaultContentDraft = td.getText();
        	            				break;
        	            			}
        	            		}
        	    			}
        	        	}
        			}
        		} else if(this.getContentDrafts()!=null) {
        			if(!this.getContentDrafts().isEmpty()) {
        				for(TextDraft td: this.getContentDrafts()) {
                			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
                				defaultContentDraft = td.getText();
                				break;
                			}
                		}
        			}        		
            	}
        	} else if(this.getContentDrafts()!=null) {
        		if(!this.getContentDrafts().isEmpty()) {
    				for(TextDraft td: this.getContentDrafts()) {
            			if(td.getLanguage().getType().equals(defaultTitleLanguage)) {
            				defaultContentDraft = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	}    	    	
    	return defaultContentDraft;
    }
    
    public String findTextOfGivenDraftTypeInGivenLanguage(final String draftType, final String language) {
    	String text = "";
    	if(draftType.equals("title")) {
    		if(this.getTitles()!=null) {
        		if(!this.getTitles().isEmpty()) {
    				for(TextDraft td: this.getTitles()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("revised_title")) {
    		if(this.getRevisedTitles()!=null) {
        		if(!this.getRevisedTitles().isEmpty()) {
    				for(TextDraft td: this.getRevisedTitles()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("contentDraft")) {
    		if(this.getContentDrafts()!=null) {
        		if(!this.getContentDrafts().isEmpty()) {
    				for(TextDraft td: this.getContentDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("revised_contentDraft")) {
    		if(this.getRevisedContentDrafts()!=null) {
        		if(!this.getRevisedContentDrafts().isEmpty()) {
    				for(TextDraft td: this.getRevisedContentDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("statementOfObjectAndReasonDraft")) {
    		if(this.getStatementOfObjectAndReasonDrafts()!=null) {
        		if(!this.getStatementOfObjectAndReasonDrafts().isEmpty()) {
    				for(TextDraft td: this.getStatementOfObjectAndReasonDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("revised_statementOfObjectAndReasonDraft")) {
    		if(this.getRevisedStatementOfObjectAndReasonDrafts()!=null) {
        		if(!this.getRevisedStatementOfObjectAndReasonDrafts().isEmpty()) {
    				for(TextDraft td: this.getRevisedStatementOfObjectAndReasonDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("financialMemorandumDraft")) {
    		if(this.getFinancialMemorandumDrafts()!=null) {
        		if(!this.getFinancialMemorandumDrafts().isEmpty()) {
    				for(TextDraft td: this.getFinancialMemorandumDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("revised_financialMemorandumDraft")) {
    		if(this.getRevisedFinancialMemorandumDrafts()!=null) {
        		if(!this.getRevisedFinancialMemorandumDrafts().isEmpty()) {
    				for(TextDraft td: this.getRevisedFinancialMemorandumDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("statutoryMemorandumDraft")) {
    		if(this.getStatutoryMemorandumDrafts()!=null) {
        		if(!this.getStatutoryMemorandumDrafts().isEmpty()) {
    				for(TextDraft td: this.getStatutoryMemorandumDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("revised_statutoryMemorandumDraft")) {
    		if(this.getRevisedStatutoryMemorandumDrafts()!=null) {
        		if(!this.getRevisedStatutoryMemorandumDrafts().isEmpty()) {
    				for(TextDraft td: this.getRevisedStatutoryMemorandumDrafts()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("annexureForAmendingBill")) {
    		if(this.getAnnexuresForAmendingBill()!=null) {
        		if(!this.getAnnexuresForAmendingBill().isEmpty()) {
    				for(TextDraft td: this.getAnnexuresForAmendingBill()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	} else if(draftType.equals("revised_annexureForAmendingBill")) {
    		if(this.getRevisedAnnexuresForAmendingBill()!=null) {
        		if(!this.getRevisedAnnexuresForAmendingBill().isEmpty()) {
    				for(TextDraft td: this.getRevisedAnnexuresForAmendingBill()) {
            			if(td.getLanguage().getType().equals(language)) {
            				text = td.getText();
            				break;
            			}
            		}
    			}
        	}
    	}
    	return text;
    }
    
    public String findChecklistValue(final String key){
        Map<String,String> checklist=this.getChecklist();
        if(checklist!=null){
        if(checklist.containsKey(key)){
            return checklist.get(key);
        }else{
            return "";
        }
        }else{
            return "";
        }
    }
    
    public static HouseType findHouseTypeForWorkflow(Bill bill) {
    	try {
	    	HouseType houseTypeForWorkflow = null;		
			if(bill.getStatus().getType().equals(ApplicationConstants.BILL_SUBMIT) 
					|| bill.getStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
				if(bill.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
					houseTypeForWorkflow = bill.getIntroducingHouseType(); 
				} else {
					houseTypeForWorkflow = bill.getHouseType();
				}
			} else {
				String firstHouseType;
				if(bill.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
					firstHouseType = bill.getIntroducingHouseType().getType();								
				} else {
					firstHouseType = bill.getHouseType().getType();
				}
				if(firstHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
					houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.UPPER_HOUSE, bill.getLocale()); 
				} else if(firstHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
					houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.LOWER_HOUSE, bill.getLocale());
				}
			}
			return houseTypeForWorkflow;
    	} catch(Exception e) {
    		return null;
    	}
    }
    
    public static List<Object[]> getRevisions(final Long billId, final String thingToBeRevised, final String locale) {
        return getBillRepository().getRevisions(billId,thingToBeRevised,locale);
    }
    
    public static String findLatestRemarksOfActor(final Long billId, final String userGroupTypeName, final String username, final String locale) {
    	return getBillRepository().getLatestRemarksOfActor(billId, userGroupTypeName, username, locale);
    }
    
    public static List<Bill> findBillsByPriority(final Session session, 
			final DeviceType deviceType,
			final Status status,
			final Boolean useDiscussionDate,
			final String orderField,
			final String sortOrder,
			final String locale){
    
    	return getBillRepository().findBillsByPriority(session, deviceType, status, useDiscussionDate, orderField, sortOrder, locale);
    }
    
    public static List<Bill> findBillsEligibleForDiscussionPriority(final Session session, 
			final DeviceType deviceType,
			final Boolean useDiscussionDate, 
			final String orderField,
			final String sortOrder,
			final String locale){
    	
    	return getBillRepository().findBillsEligibleForDiscussionPriority(session, deviceType, useDiscussionDate, orderField, sortOrder, locale);    	
    }

    /**
	 * Find.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param internalStatuses the internal statuses
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list< bill>
	 * @author dhananjayb
     * @throws ELSException 
	 * @since v1.0.0
	 */
	public static List<Bill> findBillsForItroduction(final Session session,
			final DeviceType deviceType,
			final Status[] internalStatuses,
			final Status admitted,
			final Boolean useIntroductionDate,
			final String sortOrder,
			final String locale) throws ELSException {
		return getBillRepository().findBillsForItroduction(session, deviceType, internalStatuses, admitted, useIntroductionDate, sortOrder, locale);
	}
		
    /**
	 * Find.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param answeringDate the answering date
	 * @param internalStatuses the internal statuses
	 * @param hasParent the has parent
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list< bill>
	 * @author anandk
     * @throws ELSException 
	 * @since v1.0.0
	 */
	public static List<Bill> find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final String sortOrder,
			final String locale) throws ELSException {
		return getBillRepository().find(session, deviceType, answeringDate, internalStatuses, hasParent, sortOrder, locale);
	}
	
	public static List<Bill> findForBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Status[] recommendationstatuses,
			final Boolean isPreballot,
			final Boolean hasParent,
			final String sortOrder,
			final String locale) throws ELSException {
		return getBillRepository().findForBallot(session, deviceType, answeringDate, internalStatuses, recommendationstatuses, isPreballot, hasParent, sortOrder, locale);
	}
	
	/**
     * Find members all.
     *
     * @param session the session
     * @param deviceType the device type
     * @param answeringDate the answering date
     * @param internalStatuses the internal statuses
     * @param isPreBallot the is pre ballot
     * @param startTime the start time
     * @param endTime the end time
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list< member>
     * @author anandk
	 * @throws ELSException 
     * @since v1.0.0
     */
    public static List<Member> findMembersAll(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean isPreBallot,
			final String sortOrder,
			final String locale) throws ELSException {
    	return getBillRepository().findMembersAll(session, deviceType, answeringDate, internalStatuses,isPreBallot, sortOrder, locale);
    }
    
    public static List<Member> findMembersAllForBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Status[] recommendationStatuses,
			final Boolean isPreBallot,
			final String sortOrder,
			final String locale) throws ELSException {
    	return getBillRepository().findMembersAllForBallot(session, deviceType, answeringDate, internalStatuses, recommendationStatuses, isPreBallot, sortOrder, locale);
    }
    
    /**
     * Gets the bill for member of unique subject.
     *
     * @param session the session
     * @param deviceType the device type
     * @param answeringDate the answering date
     * @param memberID the member id
     * @param subjects the subjects
     * @param locale the locale
     * @return the bill for member of unique subject
     * @throws ELSException 
     */
    public static Bill getBillForMemberOfUniqueSubject(final Session session, final DeviceType deviceType, final Date answeringDate, final Long memberID, final List<String> subjects, final String locale) throws ELSException{
    	return getBillRepository().findBillForMemberOfUniqueSubject(session, deviceType, answeringDate, memberID, subjects, locale);
    }

	public static List<Bill> findPendingBillsBeforeBalloting(final Session session,
			final DeviceType deviceType, final Date answeringDate, final String locale) throws ELSException {		
		return getBillRepository().findPendingBillsBeforeBalloting(session, deviceType, answeringDate, locale);
	}

	public static List<ActSearchVO> fullTextSearchActForReferring(final String param,
			final String actYear, final String actDefaultLanguage, final String start, final String noOfRecords) {		
		return getBillRepository().fullTextSearchActForReferring(param, actYear, actDefaultLanguage, start, noOfRecords);
	}
	
	public static List<OrdinanceSearchVO> fullTextSearchOrdinanceForReferring(final String param,
			final String actYear, final String actDefaultLanguage, final String start, final String noOfRecords) {		
		return getBillRepository().fullTextSearchOrdinanceForReferring(param, actYear, actDefaultLanguage, start, noOfRecords);
	}
	
	public static List<Object> findBillDataForDocketReport(final String billId, final String language) {
		return getBillRepository().findBillDataForDocketReport(billId, language);
	}
	
	public Date findIntroductionDate() {
		return getBillRepository().findIntroductionDate(this);
	}
	
	public static BillDraft findByStatus(final Bill bill,final Status recommendationStatus) {
		return getBillRepository().findStatusDate(bill, recommendationStatus);
	}
	
	public Date findDiscussionDate(final String currentPosition) {
		return getBillRepository().findDiscussionDate(this, currentPosition);
	}
	
	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final Bill bill) {
    	return getBillRepository().findClubbedEntitiesByPosition(bill);
    }

	public List<ClubbedEntity> findClubbedEntitiesByBillSubmissionDate(final String sortOrder, final String locale) {
		return getBillRepository().findClubbedEntitiesInAscendingOrder(this, sortOrder, locale);
	}
	
	public static Reference findCurrentFile(Bill domain) {
		return getBillRepository().findCurrentFile(domain);
	}

	/**
	 * @return the referredOrdinance
	 */
	public Ordinance getReferredOrdinance() {
		return referredOrdinance;
	}

	/**
	 * @param referredOrdinance the referredOrdinance to set
	 */
	public void setReferredOrdinance(Ordinance referredOrdinance) {
		this.referredOrdinance = referredOrdinance;
	}
	
	public static Boolean isAnyBillSubmittedEarierThanCurrentBillToBePutup(final Bill bill) {
		return getBillRepository().isAnyBillSubmittedEarierThanCurrentBillToBePutup(bill);
	}
	
	public String findFirstHouseType() {
		String firstHouseType = null;
		if(this.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
			if(this.getHouseType()!=null) {
				firstHouseType = this.getHouseType().getType();
			}
		} else if(this.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
			if(this.getIntroducingHouseType()!=null) {
				firstHouseType = this.getIntroducingHouseType().getType();
			}
		}
		return firstHouseType;
	}
	
	public String findSecondHouseType() {
		String secondHouseType = null;
		String firstHouseType = this.findFirstHouseType();
		if(firstHouseType!=null) {
			if(firstHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
				secondHouseType = ApplicationConstants.UPPER_HOUSE;
			} else if(firstHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
				secondHouseType = ApplicationConstants.LOWER_HOUSE;
			}
		}
		return secondHouseType;
	}
	
	public static String findHouseOrderOfGivenHouseForBill(final Bill bill, final String houseType) {		
		if(bill!=null&&houseType!=null) {
			if(!houseType.equals(ApplicationConstants.BOTH_HOUSE)) {
				String firstHouseType = bill.findFirstHouseType();
				if(firstHouseType!=null) {
					if(houseType.equals(firstHouseType)) {
						return ApplicationConstants.BILL_FIRST_HOUSE;
					} else {
						return ApplicationConstants.BILL_SECOND_HOUSE;
					}
				}
			}			
		}		
		return null;
	}
	
	public BillDraft findLatestDraft() {
		List<BillDraft> drafts = this.getDrafts();
		if(drafts!=null && !drafts.isEmpty()) {
			return drafts.get(drafts.size()-1);
		} else {
			return null;
		}
	}
	
	public BillDraft findLatestDraftOnOrBeforeGivenTime(final Date givenTime) {		
		return getBillRepository().findLatestDraftOnOrBeforeGivenTime(this, givenTime);
	}

	public static BillDraft findDraftByRecommendationStatus(final Bill bill, final Status recommendationStatus) {
		return getBillRepository().findDraftByRecommendationStatus(bill, recommendationStatus);
	}
	
	public static BillDraft findDraftByRecommendationStatusAndHouseRound(final Bill bill, final Status recommendationStatus, final Integer houseRound) {
		return getBillRepository().findDraftByRecommendationStatusAndHouseRound(bill, recommendationStatus, houseRound);
	}
	
	public static List<BillDraft> findStatusUpdationDraftsForGivenHouse(final Bill bill, final HouseType houseType) {
		return getBillRepository().findStatusUpdationDraftsForGivenHouse(bill, houseType);
	}
	
	public static Integer findYear(final Bill bill) {	
		Integer billYear = null;
		if(bill!=null) {
			if(bill.getType()!=null) {
				if(bill.getType().getType()!=null) {
					if(bill.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
						if(bill.getAdmissionDate()!=null) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(bill.getAdmissionDate());
							billYear =  calendar.get(Calendar.YEAR);
						}
					} else if(bill.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
						if(bill.getSubmissionDate()!=null) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(bill.getSubmissionDate());
							billYear = calendar.get(Calendar.YEAR);
						}
					}
				}
			}
		}
		return billYear;
	}
	
	public static Bill findByNumberYearAndHouseType(final int billNumber, final int billYear, final Long houseTypeId, final String locale) {
		return getBillRepository().findByNumberYearAndHouseType(billNumber, billYear, houseTypeId, locale);
	}
	
	public static List<Object[]> findStatusDatesForBill(final Bill bill) {
		return getBillRepository().findStatusDatesForBill(bill);
	}
	
	public Status findAuxiliaryWorkflowStatus(String workflowType) throws ELSException {
		if(workflowType==null) {
			ELSException elsException=new ELSException();
			elsException.setParameter("Bill_findAuxillaryWorkflowStatus", "workflow type is null");
			throw elsException;
		}
		Status auxiliaryWorkflowStatus = null;
		WorkflowDetails workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(this, this.getType(), workflowType);
		if(workflowDetails!=null) {
			
			if(workflowDetails.getCustomStatus()==null) {
				ELSException elsException=new ELSException();
				elsException.setParameter("Bill_findAuxillaryWorkflowStatus", "custom status not set for recommendation from president workflow.");
				throw elsException;
			} else {
				auxiliaryWorkflowStatus = Status.findByType(workflowDetails.getCustomStatus(), this.getLocale());
				if(auxiliaryWorkflowStatus==null) {
					ELSException elsException=new ELSException();
					elsException.setParameter("Bill_findAuxillaryWorkflowStatus", "status with type '" + workflowDetails.getCustomStatus() + "' not found");
					throw elsException;
				}
			}
		}				
		return auxiliaryWorkflowStatus;
	}
	
	public static List<Section> findAllSectionsInGivenLanguage(final Long billId, final String language) throws ELSException {
		return getBillRepository().findAllSectionsInGivenLanguage(billId, language);
	}
	
	public static List<Section> findAllSectionsInGivenLanguageForGivenHierarchyLevel(final Long billId, final String language, final int hierarchyLevel) throws ELSException {
		return getBillRepository().findAllSectionsInGivenLanguageForGivenHierarchyLevel(billId, language, hierarchyLevel);
	}
	
	public static List<Section> findAllSiblingSectionsForGivenSection(final Long billId, final String language, final String sectionNumber) throws ELSException {
		return getBillRepository().findAllSiblingSectionsForGivenSection(billId, language, sectionNumber);
	}
	
	public static List<Section> findAllSectionsAtHierarchyLevelOfGivenSection(final Long billId, final String language, final String sectionNumber) throws ELSException {
		return getBillRepository().findAllSectionsAtHierarchyLevelOfGivenSection(billId, language, sectionNumber);
	}
	
	public static Section findSection(final Long billId, final String language, final String sectionNumber) throws ELSException {
		return getBillRepository().findSection(billId, language, sectionNumber);
	}
	
	public static Section findSectionByHierarchyOrder(final Long billId, final String language, final String sectionHierarchyOrder) throws ELSException {
		return getBillRepository().findSectionByHierarchyOrder(billId, language, sectionHierarchyOrder);
	}
	
	public static List<Section> findAllInternalSections(final Long billId, final String language, final String sectionHierarchyOrder) throws ELSException {
		return getBillRepository().findAllInternalSections(billId, language, sectionHierarchyOrder);
	}
	
	public String findLanguagesOfContentDrafts() {
		return getBillRepository().findLanguagesOfContentDrafts(this);
	}
	
	//-----------------------------Getters And Setters--------------------------------
    
    public void setPriority(Integer priority){
    	this.priority = priority;
    }
    
    public Integer getPriority(){
    	return this.priority;
    }
    
	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public HouseType getIntroducingHouseType() {
		return introducingHouseType;
	}

	public void setIntroducingHouseType(HouseType introducingHouseType) {
		this.introducingHouseType = introducingHouseType;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public DeviceType getOriginalType() {
		return originalType;
	}

	public void setOriginalType(DeviceType originalType) {
		this.originalType = originalType;
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public BillType getBillType() {
		return billType;
	}

	public void setBillType(BillType billType) {
		this.billType = billType;
	}

	public BillKind getBillKind() {
		return billKind;
	}

	public void setBillKind(BillKind billKind) {
		this.billKind = billKind;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public Date getDateOfOpinionSoughtFromLawAndJD() {
		return dateOfOpinionSoughtFromLawAndJD;
	}

	public void setDateOfOpinionSoughtFromLawAndJD(
			Date dateOfOpinionSoughtFromLawAndJD) {
		this.dateOfOpinionSoughtFromLawAndJD = dateOfOpinionSoughtFromLawAndJD;
	}

	public String getOpinionSoughtFromLawAndJD() {
		return opinionSoughtFromLawAndJD;
	}

	public void setOpinionSoughtFromLawAndJD(String opinionSoughtFromLawAndJD) {
		this.opinionSoughtFromLawAndJD = opinionSoughtFromLawAndJD;
	}

	public String getOpinionSoughtFromLawAndJDFile() {
		return opinionSoughtFromLawAndJDFile;
	}

	public void setOpinionSoughtFromLawAndJDFile(
			String opinionSoughtFromLawAndJDFile) {
		this.opinionSoughtFromLawAndJDFile = opinionSoughtFromLawAndJDFile;
	}

	public Date getDateOfRecommendationFromGovernor() {
		return dateOfRecommendationFromGovernor;
	}

	public void setDateOfRecommendationFromGovernor(
			Date dateOfRecommendationFromGovernor) {
		this.dateOfRecommendationFromGovernor = dateOfRecommendationFromGovernor;
	}

	public String getRecommendationFromGovernor() {
		return recommendationFromGovernor;
	}

	public void setRecommendationFromGovernor(String recommendationFromGovernor) {
		this.recommendationFromGovernor = recommendationFromGovernor;
	}

	public Date getDateOfRecommendationFromPresident() {
		return dateOfRecommendationFromPresident;
	}

	public void setDateOfRecommendationFromPresident(
			Date dateOfRecommendationFromPresident) {
		this.dateOfRecommendationFromPresident = dateOfRecommendationFromPresident;
	}

	public String getRecommendationFromPresident() {
		return recommendationFromPresident;
	}

	public void setRecommendationFromPresident(String recommendationFromPresident) {
		this.recommendationFromPresident = recommendationFromPresident;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getRejectionDate() {
		return rejectionDate;
	}

	public void setRejectionDate(Date rejectionDate) {
		this.rejectionDate = rejectionDate;
	}

	public HouseType getCurrentHouseType() {
		return currentHouseType;
	}

	public void setCurrentHouseType(HouseType currentHouseType) {
		this.currentHouseType = currentHouseType;
	}

	public Date getExpectedStatusDate() {
		return expectedStatusDate;
	}

	public void setExpectedStatusDate(Date expectedStatusDate) {
		this.expectedStatusDate = expectedStatusDate;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Integer getHouseRound() {
		return houseRound;
	}

	public void setHouseRound(Integer houseRound) {
		this.houseRound = houseRound;
	}

	public Date getExpectedIntroductionDate() {
		return expectedIntroductionDate;
	}

	public void setExpectedIntroductionDate(Date expectedIntroductionDate) {
		this.expectedIntroductionDate = expectedIntroductionDate;
	}

	public Date getDateOfMovingForConsideration() {
		return dateOfMovingForConsideration;
	}

	public void setDateOfMovingForConsideration(Date dateOfMovingForConsideration) {
		this.dateOfMovingForConsideration = dateOfMovingForConsideration;
	}

	public Date getExpectedDiscussionDate() {
		return expectedDiscussionDate;
	}

	public void setExpectedDiscussionDate(Date expectedDiscussionDate) {
		this.expectedDiscussionDate = expectedDiscussionDate;
	}

	public List<TextDraft> getTitles() {
		return titles;
	}

	public void setTitles(List<TextDraft> titles) {
		this.titles = titles;
	}

	public List<TextDraft> getRevisedTitles() {
		return revisedTitles;
	}

	public void setRevisedTitles(List<TextDraft> revisedTitles) {
		this.revisedTitles = revisedTitles;
	}

	public List<TextDraft> getContentDrafts() {
		return contentDrafts;
	}

	public void setContentDrafts(List<TextDraft> contentDrafts) {
		this.contentDrafts = contentDrafts;
	}

	public List<TextDraft> getRevisedContentDrafts() {
		return revisedContentDrafts;
	}

	public void setRevisedContentDrafts(List<TextDraft> revisedContentDrafts) {
		this.revisedContentDrafts = revisedContentDrafts;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public List<TextDraft> getStatementOfObjectAndReasonDrafts() {
		return statementOfObjectAndReasonDrafts;
	}

	public void setStatementOfObjectAndReasonDrafts(
			List<TextDraft> statementOfObjectAndReasonDrafts) {
		this.statementOfObjectAndReasonDrafts = statementOfObjectAndReasonDrafts;
	}

	public List<TextDraft> getRevisedStatementOfObjectAndReasonDrafts() {
		return revisedStatementOfObjectAndReasonDrafts;
	}

	public void setRevisedStatementOfObjectAndReasonDrafts(
			List<TextDraft> revisedStatementOfObjectAndReasonDrafts) {
		this.revisedStatementOfObjectAndReasonDrafts = revisedStatementOfObjectAndReasonDrafts;
	}

	public List<TextDraft> getFinancialMemorandumDrafts() {
		return financialMemorandumDrafts;
	}

	public void setFinancialMemorandumDrafts(
			List<TextDraft> financialMemorandumDrafts) {
		this.financialMemorandumDrafts = financialMemorandumDrafts;
	}

	public List<TextDraft> getRevisedFinancialMemorandumDrafts() {
		return revisedFinancialMemorandumDrafts;
	}

	public void setRevisedFinancialMemorandumDrafts(
			List<TextDraft> revisedFinancialMemorandumDrafts) {
		this.revisedFinancialMemorandumDrafts = revisedFinancialMemorandumDrafts;
	}

	public List<TextDraft> getStatutoryMemorandumDrafts() {
		return statutoryMemorandumDrafts;
	}

	public void setStatutoryMemorandumDrafts(
			List<TextDraft> statutoryMemorandumDrafts) {
		this.statutoryMemorandumDrafts = statutoryMemorandumDrafts;
	}

	public List<TextDraft> getRevisedStatutoryMemorandumDrafts() {
		return revisedStatutoryMemorandumDrafts;
	}

	public void setRevisedStatutoryMemorandumDrafts(
			List<TextDraft> revisedStatutoryMemorandumDrafts) {
		this.revisedStatutoryMemorandumDrafts = revisedStatutoryMemorandumDrafts;
	}

	public Bill getParent() {
		return parent;
	}

	public void setParent(Bill parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public Act getReferredAct() {
		return referredAct;
	}

	public void setReferredAct(Act referredAct) {
		this.referredAct = referredAct;
	}

	public List<TextDraft> getAnnexuresForAmendingBill() {
		return annexuresForAmendingBill;
	}

	public void setAnnexuresForAmendingBill(List<TextDraft> annexuresForAmendingBill) {
		this.annexuresForAmendingBill = annexuresForAmendingBill;
	}

	public List<TextDraft> getRevisedAnnexuresForAmendingBill() {
		return revisedAnnexuresForAmendingBill;
	}

	public void setRevisedAnnexuresForAmendingBill(
			List<TextDraft> revisedAnnexuresForAmendingBill) {
		this.revisedAnnexuresForAmendingBill = revisedAnnexuresForAmendingBill;
	}

	public Map<String, String> getChecklist() {
		return checklist;
	}

	public void setChecklist(Map<String, String> checklist) {
		this.checklist = checklist;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarksForTranslation() {
		return remarksForTranslation;
	}

	public void setRemarksForTranslation(String remarksForTranslation) {
		this.remarksForTranslation = remarksForTranslation;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public Status getBallotStatus() {
		return ballotStatus;
	}

	public void setBallotStatus(Status ballotStatus) {
		this.ballotStatus = ballotStatus;
	}

	public Status getDiscussionStatus() {
		return discussionStatus;
	}

	public void setDiscussionStatus(Status discussionStatus) {
		this.discussionStatus = discussionStatus;
	}

	public Boolean getIsLapsed() {
		return isLapsed;
	}

	public void setIsLapsed(Boolean isLapsed) {
		this.isLapsed = isLapsed;
	}

	public Boolean getIsIncomplete() {
		return isIncomplete;
	}

	public void setIsIncomplete(Boolean isIncomplete) {
		this.isIncomplete = isIncomplete;
	}

	public Member getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(Member primaryMember) {
		this.primaryMember = primaryMember;
	}

	public List<SupportingMember> getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(List<SupportingMember> supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public List<BillDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<BillDraft> drafts) {
		this.drafts = drafts;
	}

	public ReferencedEntity getReferencedBill() {
		return referencedBill;
	}

	public void setReferencedBill(ReferencedEntity referencedBill) {
		this.referencedBill = referencedBill;
	}

	public LapsedEntity getLapsedBill() {
		return lapsedBill;
	}

	public void setLapsedBill(LapsedEntity lapsedBill) {
		this.lapsedBill = lapsedBill;
	}

	public Member getIntroducedBy() {
		return introducedBy;
	}

	public void setIntroducedBy(Member introducedBy) {
		this.introducedBy = introducedBy;
	}

	public List<VotingDetail> getVotingDetails() {
		return votingDetails;
	}

	public void setVotingDetails(List<VotingDetail> votingDetails) {
		this.votingDetails = votingDetails;
	}

	public List<LayingLetter> getLayingLetters() {
		return layingLetters;
	}

	public void setLayingLetters(List<LayingLetter> layingLetters) {
		this.layingLetters = layingLetters;
	}

	public Integer getFile() {
		return file;
	}

	public void setFile(Integer file) {
		this.file = file;
	}

	public Integer getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(Integer fileIndex) {
		this.fileIndex = fileIndex;
	}

	public Boolean getFileSent() {
		return fileSent;
	}

	public void setFileSent(Boolean fileSent) {
		this.fileSent = fileSent;
	}
	
	public Integer findYear() {
		Integer billYear = null;
		if(this.getOriginalType()!=null) {
			if(this.getOriginalType().getType()!=null) {
				if(this.getOriginalType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					if(this.getAdmissionDate()!=null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(this.getAdmissionDate());
						billYear =  calendar.get(Calendar.YEAR);
					}
				} else if(this.getOriginalType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
					if(this.getSubmissionDate()!=null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(this.getSubmissionDate());
						billYear = calendar.get(Calendar.YEAR);
					}
				}
			}
		}
		return billYear;
	}

	public String findNumberingHouseType() {
		String numberingHouseTypeName = null;
		if(this.getType()!=null) {
			if(this.getType().getType()!=null) {
				HouseType numberingHouseType = null;
				if(this.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					numberingHouseType = this.getHouseType();					
				} else if(this.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
					numberingHouseType = this.getIntroducingHouseType();					
				}
				if(numberingHouseType!=null) {
					numberingHouseTypeName =  numberingHouseType.getName();
				}
			}
		}
		return numberingHouseTypeName;
	}
	
	public static void supportingMemberWorkflowDeletion(final Bill bill) {
    	if(bill!=null && bill.getId()>0) {
    		if(anySupportingMembersWorkflows(bill)) {
    			deleteSupportingMembersWorkflows(bill);
    		}
    	}
    }
    
    public static boolean anySupportingMembersWorkflows(final Bill bill) {
		List<SupportingMember> supportingMembers = bill.getSupportingMembers();
		if(supportingMembers!=null && supportingMembers.size()>0) {
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean deleteSupportingMembersWorkflows(final Bill bill) {
		List<Long> workflowDetailsList=new ArrayList<Long>();
		if(bill!=null && bill.getId()>0 && bill.getSupportingMembers()!=null 
				&& bill.getSupportingMembers().size()>0) {
			List<SupportingMember> supportingMembers = bill.getSupportingMembers();
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					workflowDetailsList.add(Long.valueOf(sm.getWorkflowDetailsId()));
			}
		}
		
		int deleteCount=0;
		for(Long workFlowDetailsId : workflowDetailsList) {
			BaseDomain workFlowdetails = WorkflowDetails.findById(WorkflowDetails.class, workFlowDetailsId);
			boolean isDeleted = WorkflowDetails.getBaseRepository().remove(workFlowdetails);
			if(isDeleted)deleteCount++;
		}
		
		return workflowDetailsList!=null && deleteCount== workflowDetailsList.size();
	}
	
}
