/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberPartyAssociation.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
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
import org.mkcl.els.domain.Party;
import org.mkcl.els.repository.MemberPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class MemberPartyAssociation.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Configurable
@Table(name = "associations_member_party")
@IdClass(value = MemberPartyAssociationPK.class)
@JsonIgnoreProperties({ "member" })
public class MemberPartyAssociation implements Serializable {

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    // ---------------------------------Attributes-------------------------------------------------

    /** The member. */
    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "memberId", referencedColumnName = "id")
    private Member member;

    /** The party. */
    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "partyId", referencedColumnName = "id")
    private Party party;

    /** The from date. */
    @Id
    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Id
    @Temporal(TemporalType.DATE)
    private Date toDate;

    /** The sitting. */
    private Boolean sitting;

    /** The record index. */
    @NotNull
    private Integer recordIndex;

    /** The version. */
    @Version
    private Long version;

    /** The member party repository. */
    @Autowired
    private transient MemberPartyRepository memberPartyRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new member party association.
     */
    public MemberPartyAssociation() {
        super();
    }

    // ---------------------------------Domain_Methods----------------------------------------------
    /**
     * Gets the member party repository.
     *
     * @return the member party repository
     */
    public static MemberPartyRepository getMemberPartyRepository() {
        MemberPartyRepository memberPartyRepository =
                new MemberPartyAssociation().memberPartyRepository;
        if (memberPartyRepository == null) {
            throw new IllegalStateException(
                    "MemberPartyRepository has not been injected in MemberPartyAssociation Domain");
        }
        return memberPartyRepository;
    }

    /**
     * Find by member id and id.
     *
     * @param memberId the member id
     * @param id the id
     * @return the member party association
     */
    @Transactional(readOnly = true)
    public static MemberPartyAssociation findByMemberIdAndId(final Long memberId,
            final int id) {
        return getMemberPartyRepository().findByMemberIdAndId(memberId, id);
    }

    /**
     * Persist.
     *
     * @return the member party association
     */
    @Transactional
    public MemberPartyAssociation persist() {
        memberPartyRepository.save(this);
        memberPartyRepository.flush();
        return this;
    }

    /**
     * Merge.
     *
     * @return the member party association
     */
    @Transactional
    public MemberPartyAssociation merge() {
        memberPartyRepository.merge(this);
        memberPartyRepository.flush();
        return this;
    }

    /**
     * Removes the.
     *
     * @return true, if successful
     */
    @Transactional
    public boolean remove() {
        return memberPartyRepository.remove(this);
    }

    /**
     * Find highest record index.
     *
     * @param member the member
     * @return the int
     */
    @Transactional(readOnly = true)
    public static int findHighestRecordIndex(final Long member) {
        return getMemberPartyRepository().findHighestRecordIndex(member);
    }

    /**
     * Find by pk.
     *
     * @param association the association
     * @return the member party association
     */
    public static MemberPartyAssociation findByPK(
            final MemberPartyAssociation association) {
        return getMemberPartyRepository().findByPK(association);
    }

    /**
     * Checks if is duplicate.
     *
     * @return the boolean
     */
    public Boolean isDuplicate() {
        MemberPartyAssociation duplicate = MemberPartyAssociation
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
        MemberPartyAssociation domain = getMemberPartyRepository().findByPK(
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
     * Gets the party.
     *
     * @return the party
     */
    public Party getParty() {
        return party;
    }

    /**
     * Sets the party.
     *
     * @param party the new party
     */
    public void setParty(final Party party) {
        this.party = party;
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
     * Gets the sitting.
     *
     * @return the sitting
     */
    public Boolean getSitting() {
        return sitting;
    }

    /**
     * Sets the sitting.
     *
     * @param sitting the new sitting
     */
    public void setSitting(final Boolean sitting) {
        this.sitting = sitting;
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
