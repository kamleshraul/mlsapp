/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.domain.House;
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
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "partyId", referencedColumnName = "id")
    private Party party;


    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */

    @Temporal(TemporalType.DATE)
    private Date toDate;

    /** The record index. */
    @NotNull
    private Integer recordIndex;

    /** The version. */
    @Version
    private Long version;

    private String locale;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "house_id")
    private House house;
    
    /**
     * If member's party.type = 'INDEPENDENT', and member belongs to
     * ruling party then set this attribute to true.
     * 
     * If member's party.type = 'INDEPENDENT', and member belongs to
     * opposition party then set this attribute to false.
     */
    private Boolean isMemberOfRulingParty;

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
    public static MemberPartyAssociation findByMemberIdAndId(final Long memberId,
            final int id) {
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
    public static int findHighestRecordIndex(final Long member) {
        return getMemberPartyRepository().findHighestRecordIndex(member);
    }

    public static MemberPartyAssociation findByPK(
            final MemberPartyAssociation association) {
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
        if(domain!=null){
            retVal = (!domain.getVersion().equals(this.version));
        }
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


    public House getHouse() {
        return house;
    }


    public void setHouse(final House house) {
        this.house = house;
    }

	public Boolean getIsMemberOfRulingParty() {
		return isMemberOfRulingParty;
	}

	public void setIsMemberOfRulingParty(final Boolean isMemberOfRulingParty) {
		this.isMemberOfRulingParty = isMemberOfRulingParty;
	}

}
