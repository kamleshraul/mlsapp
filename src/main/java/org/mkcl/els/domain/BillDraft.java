package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
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

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "bill_drafts")
public class BillDraft extends BaseDomain implements Serializable{
	 /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType type;
	
    /** The title. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billdrafts_titles",
    joinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="title_id", referencedColumnName="id")})
    private List<TextDraft> titles;

    /** The content. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billdrafts_contentdrafts",
    joinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="content_draft_id", referencedColumnName="id")})
    private List<TextDraft> contentDrafts;
    
//    /** The sections. */
//    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
//    @JoinTable(name="billdrafts_sections",
//    joinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")},
//    inverseJoinColumns={@JoinColumn(name="section_id", referencedColumnName="id")})
//    private List<Section> sections;
    
    /** The statement of object and reason. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billdrafts_statementofobjectandreasondrafts",
    joinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="statement_of_object_and_reason_draft_id", referencedColumnName="id")})
    private List<TextDraft> statementOfObjectAndReasonDrafts;
    
    /** The financial memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billdrafts_financialmemorandumdrafts",
    joinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="financial_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> financialMemorandumDrafts;
    
    /** The statutory memorandum. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billdrafts_statutorymemorandumdrafts",
    joinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="statutory_memorandum_draft_id", referencedColumnName="id")})
    private List<TextDraft> statutoryMemorandumDrafts;
    
    /** The annexure for amending bill. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billdrafts_annexuresforamendingbill",
    joinColumns={@JoinColumn(name="bill_draft_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="annexure_for_amending_bill_draft_id", referencedColumnName="id")})
    private List<TextDraft> annexuresForAmendingBill;
    
    /** The checklist. */
	@ElementCollection
	@JoinColumn(name="billdraft_id")
    @MapKeyColumn(name="checklist_key")
    @Column(name="checklist_value",length=10000)
    @CollectionTable(name="billdraft_checklist")
	private Map<String,String> checklist;
    
    /** The status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    /** The internal status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;
    
    /** The remarks. */
    @Column(length=30000)
    private String remarks;

    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;

    /** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;
    
    /** The referenced bill. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Act referredAct;
    
 	//--------------------------Clubbing Entities------------------------------------------
    /** The parent. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Bill parent;
    
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinTable(name="billdrafts_clubbingentities", 
    		joinColumns={@JoinColumn(name="billdraft_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;
    
    /** The referenced bill. */
    @ManyToOne(fetch=FetchType.LAZY)
    private ReferencedEntity referencedBill;
    
    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "housetype_id")
    private HouseType houseType;
    
    /** The expected date of setting status or getting response from the status for the bill. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedStatusDate;
    
    /** The date of setting status for the bill. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;
    
    /** The house round for bill. */
    private Integer houseRound;
    
    public BillDraft() {
		super();
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}	

	public List<TextDraft> getTitles() {
		return titles;
	}

	public void setTitles(List<TextDraft> titles) {
		this.titles = titles;
	}

	public List<TextDraft> getContentDrafts() {
		return contentDrafts;
	}

	public void setContentDrafts(List<TextDraft> contentDrafts) {
		this.contentDrafts = contentDrafts;
	}

//	public List<Section> getSections() {
//		return sections;
//	}
//
//	public void setSections(List<Section> sections) {
//		this.sections = sections;
//	}

	public List<TextDraft> getStatementOfObjectAndReasonDrafts() {
		return statementOfObjectAndReasonDrafts;
	}

	public void setStatementOfObjectAndReasonDrafts(
			List<TextDraft> statementOfObjectAndReasonDrafts) {
		this.statementOfObjectAndReasonDrafts = statementOfObjectAndReasonDrafts;
	}

	public List<TextDraft> getFinancialMemorandumDrafts() {
		return financialMemorandumDrafts;
	}

	public void setFinancialMemorandumDrafts(
			List<TextDraft> financialMemorandumDrafts) {
		this.financialMemorandumDrafts = financialMemorandumDrafts;
	}

	public List<TextDraft> getStatutoryMemorandumDrafts() {
		return statutoryMemorandumDrafts;
	}

	public void setStatutoryMemorandumDrafts(
			List<TextDraft> statutoryMemorandumDrafts) {
		this.statutoryMemorandumDrafts = statutoryMemorandumDrafts;
	}

	public List<TextDraft> getAnnexuresForAmendingBill() {
		return annexuresForAmendingBill;
	}

	public void setAnnexuresForAmendingBill(List<TextDraft> annexuresForAmendingBill) {
		this.annexuresForAmendingBill = annexuresForAmendingBill;
	}

	public Map<String, String> getChecklist() {
		return this.checklist;
	}

	public void setChecklist(Map<String, String> checklist) {
		this.checklist = checklist;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Act getReferredAct() {
		return referredAct;
	}

	public void setReferredAct(Act referredAct) {
		this.referredAct = referredAct;
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

	public ReferencedEntity getReferencedBill() {
		return referencedBill;
	}

	public void setReferencedBill(ReferencedEntity referencedBill) {
		this.referencedBill = referencedBill;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
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
    
}
