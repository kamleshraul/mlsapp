/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberSuspension.java
 * Created On: Jan 22, 2015
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.MemberSuspensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class MemberSuspension.
 *
 * @author rajeshs
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="members_suspensions")
@JsonIgnoreProperties({"member"})
public class MemberSuspension extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

     /** The Start Date of Suspension. */
    @Temporal(TemporalType.DATE)
    private Date startDateOfSuspension;

    /** The Estimated End Date of Suspension. */
    @Temporal(TemporalType.DATE)
    private Date estimatedEndDateOfSuspension;

    /** The Actual End date of Suspension. */
    @Temporal(TemporalType.DATE)
    private Date actualEndDateOfSuspension;
   
 
    /** The Reason of Suspension. */
	@Column(length = 30000)
	private String reasonOfSuspension;
    
    @Autowired
    private transient MemberSuspensionRepository repository;

    /**** Constructor ****/
    
    /**
     * Instantiates a new member minister.
     */
    public MemberSuspension() {
        super();
    }

    /**** Domain Methods ****/
    
    public static MemberSuspensionRepository getMemberSuspensionRepository() {
    	MemberSuspensionRepository repository = new MemberSuspension().repository;
    	if (repository == null) {
    		throw new IllegalStateException(
                  "MemberSuspensionRepository has not been injected in MemberSuspension Domain");
    	}
    	return repository;
    }

    @Transactional(readOnly = true)
    public static MemberSuspension findByMemberIdAndId(final Long memberId,
            final Long id) {
        return getMemberSuspensionRepository().findByMemberIdAndId(memberId, id);
    }
    /**** Getters & Setters ****/

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Date getStartDateOfSuspension() {
		return startDateOfSuspension;
	}

	public void setStartDateOfSuspension(Date startDateOfSuspension) {
		this.startDateOfSuspension = startDateOfSuspension;
	}

	public Date getEstimatedEndDateOfSuspension() {
		return estimatedEndDateOfSuspension;
	}

	public void setEstimatedEndDateOfSuspension(Date estimatedEndDateOfSuspension) {
		this.estimatedEndDateOfSuspension = estimatedEndDateOfSuspension;
	}

	public Date getActualEndDateOfSuspension() {
		return actualEndDateOfSuspension;
	}

	public void setActualEndDateOfSuspension(Date actualEndDateOfSuspension) {
		this.actualEndDateOfSuspension = actualEndDateOfSuspension;
	}

	public String getReasonOfSuspension() {
		return reasonOfSuspension;
	}

	public void setReasonOfSuspension(String reasonOfSuspension) {
		this.reasonOfSuspension = reasonOfSuspension;
	}
  

	
}
