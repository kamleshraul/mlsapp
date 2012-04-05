/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberMinistryAssociation.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.repository.MemberMinistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class MemberMinistryAssociation.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Configurable
@Table(name = "associations_member_ministry")
@IdClass(value = MemberMinistryAssociationPk.class)
@JsonIgnoreProperties({ "member" })
public class MemberMinistryAssociation implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The member. */
    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    /** The ministry. */
    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "ministry_id", referencedColumnName = "id")
    private Ministry ministry;

    /** The from date. */
    @Id
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Id
    @Temporal(TemporalType.DATE)
    private Date toDate;

    /** The role. */
    @OneToOne
    @JoinColumn(name = "role_id")
    private MemberRole role;

    /** The record index. */
    @NotNull
    private Integer recordIndex;

    /** The version. */
    @Version
    private Long version;

    /** The member ministry repository. */
    @Autowired
    private transient MemberMinistryRepository memberMinistryRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new member ministry association.
     */
    public MemberMinistryAssociation() {
        super();
    }

    // ------------------------------------------Domain_Methods-----------------------------------
    /**
     * Gets the member ministry repository.
     *
     * @return the member ministry repository
     */
    public static MemberMinistryRepository getMemberMinistryRepository() {
        MemberMinistryRepository memberMinistryRepository =
                new MemberMinistryAssociation().memberMinistryRepository;
        if (memberMinistryRepository == null) {
            throw new IllegalStateException(
                    "MemberMinistryRepository has not been "
                            +
                    "injected in MemberMinistryAssociation Domain");
        }
        return memberMinistryRepository;
    }

    /**
     * Find by member id and id.
     *
     * @param memberId the member id
     * @param id the id
     * @return the member ministry association
     */
    @Transactional(readOnly = true)
    public static MemberMinistryAssociation findByMemberIdAndId(final Long memberId,
            final int id) {
        return getMemberMinistryRepository().findByMemberIdAndId(memberId, id);
    }

    /**
     * Persist.
     *
     * @return the member ministry association
     */
    @Transactional
    public MemberMinistryAssociation persist() {
        memberMinistryRepository.save(this);
        memberMinistryRepository.flush();
        return this;
    }

    /**
     * Merge.
     *
     * @return the member ministry association
     */
    @Transactional
    public MemberMinistryAssociation merge() {
        memberMinistryRepository.merge(this);
        memberMinistryRepository.flush();
        return this;
    }

    /**
     * Removes the.
     *
     * @return true, if successful
     */
    @Transactional
    public boolean remove() {
        return memberMinistryRepository.remove(this);
    }

    /**
     * Find highest record index.
     *
     * @param member the member
     * @return the int
     */
    @Transactional(readOnly = true)
    public static int findHighestRecordIndex(final Long member) {
        return getMemberMinistryRepository().findHighestRecordIndex(member);
    }

    /**
     * Find by pk.
     *
     * @param association the association
     * @return the member ministry association
     */
    public static MemberMinistryAssociation findByPK(
            final MemberMinistryAssociation association) {
        return getMemberMinistryRepository().findByPK(association);
    }

    /**
     * Checks if is duplicate.
     *
     * @return the boolean
     */
    public Boolean isDuplicate() {
        MemberMinistryAssociation duplicate = MemberMinistryAssociation
                .findByPK(this);
        if (duplicate == null) {
            return false;
        }
        return true;
    }


    /**
     * Checks if is version mismatch.
     *
     * @return true, if is version mismatch
     */
    @Transactional(readOnly = true)
    public boolean isVersionMismatch() {
        Boolean retVal = false;
        MemberMinistryAssociation domain = getMemberMinistryRepository().findByPK(
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
     * Gets the ministry.
     *
     * @return the ministry
     */
    public Ministry getMinistry() {
        return ministry;
    }

    /**
     * Sets the ministry.
     *
     * @param ministry the new ministry
     */
    public void setMinistry(final Ministry ministry) {
        this.ministry = ministry;
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

    /**
     * Gets the role.
     *
     * @return the role
     */
    public MemberRole getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role the new role
     */
    public void setRole(final MemberRole role) {
        this.role = role;
    }

    /**
     * Gets the record index.
     *
     * @return the record index
     */
    public Integer getRecordIndex() {
        return recordIndex;
    }

    /**
     * Sets the record index.
     *
     * @param recordIndex the new record index
     */
    public void setRecordIndex(final Integer recordIndex) {
        this.recordIndex = recordIndex;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(final Long version) {
        this.version = version;
    }

}
