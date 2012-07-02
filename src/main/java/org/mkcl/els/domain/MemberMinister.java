/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberMinister.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class MemberMinister.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="members_ministries")
@JsonIgnoreProperties({"member", "designation", "memberDepartments"})
public class MemberMinister extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    /** The designation. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="designation_id")
    private Designation designation;

    /** The oath date. */
    @Temporal(TemporalType.DATE)
    private Date oathDate;

    /** The resignation date. */
    @Temporal(TemporalType.DATE)
    private Date resignationDate;

    /** The minister. */
    @ManyToOne
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    /** The ministry assignment date*/
    @Temporal(TemporalType.DATE)
    private Date ministryAssignmentDate;

    /** The ministry from date. */
    @Temporal(TemporalType.DATE)
    private Date ministryFromDate;

    /** The ministry to date. */
    @Temporal(TemporalType.DATE)
    private Date ministryToDate;

    /** The member departments. */
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="member_ministry_id", referencedColumnName="id")
    private List<MemberDepartment> memberDepartments;

    //------------------ Constructor ----------------------//
    /**
     * Instantiates a new member minister.
     */
    public MemberMinister() {
        super();
    }


    //------------------ Getters & Setters ----------------------//
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
     * Gets the designation.
     *
     * @return the designation
     */
    public Designation getDesignation() {
        return designation;
    }


    /**
     * Sets the designation.
     *
     * @param designation the new designation
     */
    public void setDesignation(final Designation designation) {
        this.designation = designation;
    }


    /**
     * Gets the oath date.
     *
     * @return the oath date
     */
    public Date getOathDate() {
        return oathDate;
    }


    /**
     * Sets the oath date.
     *
     * @param oathDate the new oath date
     */
    public void setOathDate(final Date oathDate) {
        this.oathDate = oathDate;
    }


    /**
     * Gets the resignation date.
     *
     * @return the resignation date
     */
    public Date getResignationDate() {
        return resignationDate;
    }


    /**
     * Sets the resignation date.
     *
     * @param resignationDate the new resignation date
     */
    public void setResignationDate(final Date resignationDate) {
        this.resignationDate = resignationDate;
    }


        /**
     * Gets the ministry from date.
     *
     * @return the ministry from date
     */
    public Date getMinistryFromDate() {
        return ministryFromDate;
    }


    /**
     * Sets the ministry from date.
     *
     * @param ministryFromDate the new ministry from date
     */
    public void setMinistryFromDate(final Date ministryFromDate) {
        this.ministryFromDate = ministryFromDate;
    }


    /**
     * Gets the ministry to date.
     *
     * @return the ministry to date
     */
    public Date getMinistryToDate() {
        return ministryToDate;
    }


    /**
     * Sets the ministry to date.
     *
     * @param ministryToDate the new ministry to date
     */
    public void setMinistryToDate(final Date ministryToDate) {
        this.ministryToDate = ministryToDate;
    }


	/**
	 * Gets the member departments.
	 *
	 * @return the member departments
	 */
	public List<MemberDepartment> getMemberDepartments() {
		return memberDepartments;
	}


	/**
	 * Sets the member departments.
	 *
	 * @param memberDepartments the new member departments
	 */
	public void setMemberDepartments(final List<MemberDepartment> memberDepartments) {
		this.memberDepartments = memberDepartments;
	}

	public Ministry getMinistry() {
		return ministry;
	}


	public void setMinistry(final Ministry ministry) {
		this.ministry = ministry;
	}


	public Date getMinistryAssignmentDate() {
		return ministryAssignmentDate;
	}


	public void setMinistryAssignmentDate(final Date ministryAssignmentDate) {
		this.ministryAssignmentDate = ministryAssignmentDate;
	}
}
