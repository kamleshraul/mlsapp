/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberDepartmentAssociation.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Department;
import org.mkcl.els.repository.MemberDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class MemberDepartmentAssociation.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Configurable
@Table(name = "members_departments")
@IdClass(value = MemberDepartmentAssociationPK.class)
@JsonIgnoreProperties({ "member"})
public class MemberDepartmentAssociation implements Serializable {

    private transient static final long serialVersionUID = 1L;

    // ---------------------------------Attributes-------------------------------------------------

    /** The member. */
    @Id
    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "memberId", referencedColumnName = "id")
    private Member member;

    /** The department. */
    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "departmentId", referencedColumnName = "id")
    private Department department;


    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */

    @Temporal(TemporalType.DATE)
    private Date toDate;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The record index. */
    @NotNull
    private Integer recordIndex;

    /** The version. */
    @Version
    private Long version;

    private String locale;

    @Autowired
    private transient MemberDepartmentRepository memberDepartmentRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new member department association.
     */
    public MemberDepartmentAssociation() {
        super();
    }

    // ---------------------------------Domain_Methods----------------------------------------------
    public static MemberDepartmentRepository getMemberDepartmentRepository() {
        MemberDepartmentRepository memberDepartmentRepository = new MemberDepartmentAssociation().memberDepartmentRepository;
        if (memberDepartmentRepository == null) {
            throw new IllegalStateException(
                    "MemberDepartmentRepository has not been injected in MemberDepartmentAssociation Domain");
        }
        return memberDepartmentRepository;
    }

    @Transactional(readOnly = true)
    public static MemberDepartmentAssociation findByMemberIdAndId(final Long memberId,
            final int id) {
        return getMemberDepartmentRepository().findByMemberIdAndId(memberId, id);
    }

    @Transactional
    public MemberDepartmentAssociation persist() {
        memberDepartmentRepository.save(this);
        memberDepartmentRepository.flush();
        return this;
    }

    @Transactional
    public MemberDepartmentAssociation merge() {
        memberDepartmentRepository.merge(this);
        memberDepartmentRepository.flush();
        return this;
    }

    @Transactional
    public boolean remove() {
        return memberDepartmentRepository.remove(this);
    }

    @Transactional(readOnly = true)
    public static int findHighestRecordIndex(final Long member) {
        return getMemberDepartmentRepository().findHighestRecordIndex(member);
    }

    public static MemberDepartmentAssociation findByPK(
            final MemberDepartmentAssociation association) {
        return getMemberDepartmentRepository().findByPK(association);
    }

    public Boolean isDuplicate() {
        MemberDepartmentAssociation duplicate = MemberDepartmentAssociation
                .findByPK(this);
        if (duplicate == null) {
            return false;
        }
        return true;
    }
    @Transactional(readOnly = true)
    public boolean isVersionMismatch() {
        Boolean retVal = false;
        MemberDepartmentAssociation domain = getMemberDepartmentRepository().findByPK(
                    this);
        retVal = (!domain.getVersion().equals(this.version));
        return retVal;
    }

    // ------------------------------------------Getters/Setters-----------------------------------

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
     * Gets the department.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Sets the department.
     *
     * @param department the new department
     */
    public void setDepartment(final Department department) {
        this.department = department;
    }

    /**
     * Gets the from date.
     *
     * @return the from date
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Sets the from date.
     *
     * @param fromDate the new from date
     */
    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets the to date.
     *
     * @param toDate the new to date
     */
    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }    

    public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(final Integer recordIndex) {
        this.recordIndex = recordIndex;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }


    public String getLocale() {
        return locale;
    }


    public void setLocale(final String locale) {
        this.locale = locale;
    }

}
