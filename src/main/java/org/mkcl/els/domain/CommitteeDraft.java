package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_drafts")
public class CommitteeDraft extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 4746664535694599878L;

	//=============== ATTRIBUTES ===============
	/* Core Attributes */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committee_drafts_committee_members",
			joinColumns={@JoinColumn(name="committee_id", 
						referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_member_id", 
						referencedColumnName="id")})
	private List<CommitteeMember> members;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committee_drafts_invited_members",
			joinColumns={@JoinColumn(name="committee_draft_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_invited_member_id", 
					referencedColumnName="id")})
	private List<CommitteeMember> invitedMembers;

	/* Workflow Attributes */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_id")
	private Status internalStatus;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_id")
	private Status recommendationStatus;
	
	@Column(length=30000)
	private String remarks;
	
	/* Audit Log */
	@Temporal(TemporalType.TIMESTAMP)
	private Date editedOn;
	
	@Column(length=1000)
	private String editedAs;
	
	@Column(length=1000)
	private String editedBy;
	
	//=============== CONSTRUCTORS =============
	public CommitteeDraft() {
		super();
		this.setMembers(new ArrayList<CommitteeMember>());
		this.setInvitedMembers(new ArrayList<CommitteeMember>());
		
	}

	//=============== VIEW METHODS =============
	
	
	//=============== DOMAIN METHODS ===========
	
	
	//=============== INTERNAL METHODS =========
	
	
	//=============== GETTERS/SETTERS ==========
	public List<CommitteeMember> getMembers() {
		return members;
	}

	public void setMembers(final List<CommitteeMember> members) {
		this.members = members;
	}

	public List<CommitteeMember> getInvitedMembers() {
		return invitedMembers;
	}

	public void setInvitedMembers(final List<CommitteeMember> invitedMembers) {
		this.invitedMembers = invitedMembers;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(final Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(final Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(final Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(final String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(final String editedBy) {
		this.editedBy = editedBy;
	}
	
}