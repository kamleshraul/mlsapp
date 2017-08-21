/*
 * 
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//import javax.persistence.CascadeType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
//import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class MotionDraft.
 */
@Configurable
@Entity
@Table(name = "billamendmentmotion_drafts")
public class BillAmendmentMotionDraft extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;    

	//=============== BASIC ATTRIBUTES ====================	
    /** The amendment. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billamendmentmotiondrafts_sectionamendments",
    joinColumns={@JoinColumn(name="billamendmentmotiondraft_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="sectionamendment_id", referencedColumnName="id")})
    private List<SectionAmendment> sectionAmendments;

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
    
    /** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private BillAmendmentMotion parent;
	
	/** The clubbed entities. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="billamendmentmotiondrafts_clubbingentities", joinColumns={@JoinColumn(name="billamendmentmotiondraft_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
    private List<ClubbedEntity> clubbedEntities;
    
    /** The referenced entities. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="billamendmentmotiondrafts_referencedentities", joinColumns={@JoinColumn(name="billamendmentmotiondraft_id", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
    private List<ReferencedEntity> referencedEntities;

    public List<SectionAmendment> getSectionAmendments() {
		return sectionAmendments;
	}

	public void setSectionAmendments(List<SectionAmendment> sectionAmendments) {
		this.sectionAmendments = sectionAmendments;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Gets the internal status.
	 *
	 * @return the internal status
	 */
	public Status getInternalStatus() {
		return internalStatus;
	}

	/**
	 * Sets the internal status.
	 *
	 * @param internalStatus the new internal status
	 */
	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	/**
	 * Gets the recommendation status.
	 *
	 * @return the recommendation status
	 */
	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	/**
	 * Sets the recommendation status.
	 *
	 * @param recommendationStatus the new recommendation status
	 */
	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
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
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Gets the edited on.
	 *
	 * @return the edited on
	 */
	public Date getEditedOn() {
		return editedOn;
	}

	/**
	 * Sets the edited on.
	 *
	 * @param editedOn the new edited on
	 */
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	/**
	 * Gets the edited by.
	 *
	 * @return the edited by
	 */
	public String getEditedBy() {
		return editedBy;
	}

	/**
	 * Sets the edited by.
	 *
	 * @param editedBy the new edited by
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	/**
	 * Gets the edited as.
	 *
	 * @return the edited as
	 */
	public String getEditedAs() {
		return editedAs;
	}

	/**
	 * Sets the edited as.
	 *
	 * @param editedAs the new edited as
	 */
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	/**
	 * Gets the clubbed entities.
	 *
	 * @return the clubbed entities
	 */
	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	/**
	 * Sets the clubbed entities.
	 *
	 * @param clubbedEntities the new clubbed entities
	 */
	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	public List<ReferencedEntity> getReferencedEntities() {
		return referencedEntities;
	}

	public void setParent(BillAmendmentMotion parent) {
		this.parent = parent;
	}

	public BillAmendmentMotion getParent() {
		return parent;
	}  
}
