/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.HouseMemberRoleAssociation.java
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.repository.MemberHouseRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import com.sun.istack.NotNull;

/**
 * The Class HouseMemberRoleAssociation.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Configurable
@Table(name = "associations_house_member_role")
@IdClass(value = HouseMemberRoleAssociationPK.class)
// @JsonIgnoreProperties({"role","constituency","house","member"})
public class HouseMemberRoleAssociation implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The from date. */
    @Id
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Id
    @Temporal(TemporalType.DATE)
    private Date toDate;

    /** The is sitting. */
    private Boolean isSitting;

    /** The oath date. */
    @Temporal(TemporalType.DATE)
    private Date oathDate;

    /** The constituency. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;

    /** The internal poll date. */
    @Temporal(TemporalType.DATE)
    private Date internalPollDate;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The member. */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    /** The role. */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "role_id", referencedColumnName = "id")
    private MemberRole role;

    /** The house. */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    // Now this field will keep track of the index of the record updated/created
    /** The record index. */
    @NotNull
    private Integer recordIndex;

    /** The version. */
    @Version
    private Long version;

    /** The locale. */
    @Column(length = 10)
    private String locale;

    /** The member house role repository. */
    @Autowired
    private transient MemberHouseRoleRepository memberHouseRoleRepository;

    /**
     * Instantiates a new house member role association.
     */
    public HouseMemberRoleAssociation() {
        super();

    }

    /**
     * Instantiates a new house member role association.
     *
     * @return the member house role repository
     */
    public static MemberHouseRoleRepository getMemberHouseRoleRepository() {
        MemberHouseRoleRepository memberHouseRoleRepository =
                new HouseMemberRoleAssociation().memberHouseRoleRepository;
        if (memberHouseRoleRepository == null) {
            throw new IllegalStateException(
                    "HouseMemberRoleAssociationRepository has not "
                            +
                    "been injected in HouseMemberRoleAssociation Domain");
        }
        return memberHouseRoleRepository;
    }

    /**
     * Instantiates a new house member role association.
     *
     * @param fromDate
     *            the from date
     * @param toDate
     *            the to date
     * @param member
     *            the member
     * @param role
     *            the role
     * @param house
     *            the house
     * @param locale
     *            the locale
     */
    public HouseMemberRoleAssociation(
            final Date fromDate,
            final Date toDate,
            final Member member,
            final MemberRole role,
            final House house,
            final String locale) {
        super();
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.member = member;
        this.role = role;
        this.house = house;
        this.locale = locale;
    }

    /**
     * Find by member id and id.
     *
     * @param memberId
     *            the member id
     * @param id
     *            the id
     * @return the house member role association
     */
    @Transactional(readOnly = true)
    public static HouseMemberRoleAssociation findByMemberIdAndId(
            final Long memberId,
            final int id) {
        return getMemberHouseRoleRepository().findByMemberIdAndId(memberId, id);
    }

    /**
     * Persist.
     *
     * @return the house member role association
     */
    @Transactional
    public HouseMemberRoleAssociation persist() {
        memberHouseRoleRepository.save(this);
        memberHouseRoleRepository.flush();
        return this;
    }

    /**
     * Merge.
     *
     * @return the house member role association
     */
    @Transactional
    public HouseMemberRoleAssociation merge() {
        memberHouseRoleRepository.merge(this);
        memberHouseRoleRepository.flush();
        return this;
    }

    /**
     * Removes the.
     *
     * @return true, if successful
     */
    @Transactional
    public boolean remove() {
        return memberHouseRoleRepository.remove(this);
    }

    /**
     * Find highest record index.
     *
     * @param member the member
     * @return the int
     */
    @Transactional(readOnly = true)
    public static int findHighestRecordIndex(final Long member) {
        return getMemberHouseRoleRepository().findHighestRecordIndex(member);
    }

    /**
     * Find by pk.
     *
     * @param association the association
     * @return the house member role association
     */
    public static HouseMemberRoleAssociation findByPK(
            final HouseMemberRoleAssociation association) {
        return getMemberHouseRoleRepository().findByPK(association);
    }

    /**
     * Checks if is duplicate.
     *
     * @return the boolean
     */
    public Boolean isDuplicate() {
        HouseMemberRoleAssociation duplicate = HouseMemberRoleAssociation.findByPK(this);
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
        HouseMemberRoleAssociation domain = getMemberHouseRoleRepository().findByPK(this);
        retVal = (!domain.getVersion().equals(this.version));
        return retVal;
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
     * @param fromDate
     *            the new from date
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
     * @param toDate
     *            the new to date
     */
    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    /**
     * Gets the checks if is sitting.
     *
     * @return the checks if is sitting
     */
    public Boolean getIsSitting() {
        return isSitting;
    }

    /**
     * Sets the checks if is sitting.
     *
     * @param isSitting
     *            the new checks if is sitting
     */
    public void setIsSitting(final Boolean isSitting) {
        this.isSitting = isSitting;
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
     * @param oathDate
     *            the new oath date
     */
    public void setOathDate(final Date oathDate) {
        this.oathDate = oathDate;
    }

    /**
     * Gets the constituency.
     *
     * @return the constituency
     */
    public Constituency getConstituency() {
        return constituency;
    }

    /**
     * Sets the constituency.
     *
     * @param constituency
     *            the new constituency
     */
    public void setConstituency(final Constituency constituency) {
        this.constituency = constituency;
    }

    /**
     * Gets the internal poll date.
     *
     * @return the internal poll date
     */
    public Date getInternalPollDate() {
        return internalPollDate;
    }

    /**
     * Sets the internal poll date.
     *
     * @param internalPollDate
     *            the new internal poll date
     */
    public void setInternalPollDate(final Date internalPollDate) {
        this.internalPollDate = internalPollDate;
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
     * @param remarks
     *            the new remarks
     */
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
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
     * @param member
     *            the new member
     */
    public void setMember(final Member member) {
        this.member = member;
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
     * @param role
     *            the new role
     */
    public void setRole(final MemberRole role) {
        this.role = role;
    }

    /**
     * Gets the house.
     *
     * @return the house
     */
    public House getHouse() {
        return house;
    }

    /**
     * Sets the house.
     *
     * @param house
     *            the new house
     */
    public void setHouse(final House house) {
        this.house = house;
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

    /**
     * Gets the locale.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    public void setLocale(final String locale) {
        this.locale = locale;
    }

}
