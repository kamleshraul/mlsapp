package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_members")
public class CommitteeMember extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 7620834065751237404L;

	//=============== ATTRIBUTES ===============
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="designation_id")
	private CommitteeDesignation designation;
	
	@Temporal(TemporalType.DATE)
	private Date joiningDate;
	
	@Temporal(TemporalType.DATE)
	private Date retiringDate;
	
	@Temporal(TemporalType.DATE)
	private Date resignationDate;

	//=============== CONSTRUCTORS =============
	public CommitteeMember() {
		super();
	}
	
	public CommitteeMember(final Member member, 
			final CommitteeDesignation designation, 
			final String locale) {
		super(locale);
		this.setMember(member);
		this.setDesignation(designation);
	}
	
	public CommitteeMember(final Member member, 
			final CommitteeDesignation designation, 
			final Date joiningDate,
			final String locale) {
		super(locale);
		this.setMember(member);
		this.setDesignation(designation);
		this.setJoiningDate(joiningDate);
	}
	
	//=============== VIEW METHODS =============
	
	
	//=============== DOMAIN METHODS ===========
	
	
	//=============== INTERNAL METHODS =========
	
	
	//=============== GETTERS/SETTERS ==========
	public Member getMember() {
		return member;
	}

	public void setMember(final Member member) {
		this.member = member;
	}

	public CommitteeDesignation getDesignation() {
		return designation;
	}

	public void setDesignation(final CommitteeDesignation designation) {
		this.designation = designation;
	}
	
	public Date getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(final Date joiningDate) {
		this.joiningDate = joiningDate;
	}
	
	public Date getRetiringDate() {
		return retiringDate;
	}

	public void setRetiringDate(final Date retiringDate) {
		this.retiringDate = retiringDate;
	}

	public Date getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(final Date resignationDate) {
		this.resignationDate = resignationDate;
	}
	
}