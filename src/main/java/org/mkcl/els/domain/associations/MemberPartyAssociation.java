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
@Table(name = "members_parties")
@IdClass(value = MemberPartyAssociationPK.class)
@JsonIgnoreProperties({ "member" })
public class MemberPartyAssociation implements Serializable {

    private transient static final long serialVersionUID = 1L;

    // ---------------------------------Attributes-------------------------------------------------

    /** The member. */
    @Id
    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "memberId", referencedColumnName = "id")
    private Member member;

    /** The party. */
    @Id
    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "partyId", referencedColumnName = "id")
    private Party party;

    @Id
    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Id
    @Temporal(TemporalType.DATE)
    private Date toDate;

    private Boolean sitting;

    /** The record index. */
    @NotNull
    private Integer recordIndex;

    /** The version. */
    @Version
    private Long version;

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
    public static MemberPartyRepository getMemberPartyRepository() {
        MemberPartyRepository memberPartyRepository = new MemberPartyAssociation().memberPartyRepository;
        if (memberPartyRepository == null) {
            throw new IllegalStateException(
                    "MemberPartyRepository has not been injected in MemberPartyAssociation Domain");
        }
        return memberPartyRepository;
    }

    @Transactional(readOnly = true)
    public static MemberPartyAssociation findByMemberIdAndId(Long memberId,
            int id) {
        return getMemberPartyRepository().findByMemberIdAndId(memberId, id);
    }

    @Transactional
    public MemberPartyAssociation persist() {
        memberPartyRepository.save(this);
        memberPartyRepository.flush();
        return this;
    }

    @Transactional
    public MemberPartyAssociation merge() {
        memberPartyRepository.merge(this);
        memberPartyRepository.flush();
        return this;
    }

    @Transactional
    public boolean remove() {
        return memberPartyRepository.remove(this);
    }

    @Transactional(readOnly = true)
    public static int findHighestRecordIndex(Long member) {
        return getMemberPartyRepository().findHighestRecordIndex(member);
    }

    public static MemberPartyAssociation findByPK(
            MemberPartyAssociation association) {
        return getMemberPartyRepository().findByPK(association);
    }

    public Boolean isDuplicate() {
        MemberPartyAssociation duplicate = MemberPartyAssociation
                .findByPK(this);
        if (duplicate == null) {
            return false;
        }
        return true;
    }
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
    public void setMember(Member member) {
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
    public void setParty(Party party) {
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
    public void setFromDate(Date fromDate) {
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
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Boolean getSitting() {
        return sitting;
    }

    public void setSitting(Boolean sitting) {
        this.sitting = sitting;
    }

    public Integer getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(Integer recordIndex) {
        this.recordIndex = recordIndex;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
