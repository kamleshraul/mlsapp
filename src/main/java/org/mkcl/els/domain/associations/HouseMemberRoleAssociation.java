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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Session;
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
@Table(name = "members_houses_roles")
@IdClass(value = HouseMemberRoleAssociationPK.class)
@JsonIgnoreProperties({"member","constituency"})
public class HouseMemberRoleAssociation implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Temporal(TemporalType.DATE)
    private Date toDate;

    /** The is sitting. */
    private Boolean isSitting;

    /** The oath date. */
    @Temporal(TemporalType.DATE)
    private Date oathDate;

    /** The resignation date*/
    @Temporal(TemporalType.DATE)
    private Date resignationDate;

    /** The constituency. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;

    /** In Council, each constituency has a subtype.
     * For eg: "Nominated by Teachers" Constituency may have
     * "Nominated from Pune-Beed" as a subtype.
     */
    //@Column(length = 600)
    //private String constituencySubtype;

    /** The internal poll date. */
    @Temporal(TemporalType.DATE)
    private Date internalPollDate;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The member. */
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    /** The role. */
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "role_id", referencedColumnName = "id")
    private MemberRole role;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "house_id")
    private House house;

    // Now this field will keep track of the index of the record updated/created
    /** The record index. */
    @NotNull
    private Integer recordIndex;

    @Version
    private Long version;

    @Column(length=10)
    private String locale;

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
     * @param fromDate the from date
     * @param toDate the to date
     * @param isSitting the is sitting
     * @param oathDate the oath date
     * @param constituency the constituency
     * @param member the member
     * @param role the role
     * @param house the house
     */


    public static MemberHouseRoleRepository getMemberHouseRoleRepository() {
        MemberHouseRoleRepository memberHouseRoleRepository = new HouseMemberRoleAssociation().memberHouseRoleRepository;
        if (memberHouseRoleRepository == null) {
            throw new IllegalStateException(
                    "HouseMemberRoleAssociationRepository has not been injected in HouseMemberRoleAssociation Domain");
        }
        return memberHouseRoleRepository;
    }

    public HouseMemberRoleAssociation(final Date fromDate, final Date toDate,
			final Member member, final MemberRole role, final House house, final String locale) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.member = member;
		this.role = role;
		this.house = house;
		this.locale = locale;
	}

	@Transactional(readOnly = true)
    public static HouseMemberRoleAssociation findByMemberIdAndId(final Long memberId,
            final int id) {
        return getMemberHouseRoleRepository().findByMemberIdAndId(memberId, id);
    }

    @Transactional
    public HouseMemberRoleAssociation persist() {
        memberHouseRoleRepository.save(this);
        memberHouseRoleRepository.flush();
        return this;
    }

    @Transactional
    public HouseMemberRoleAssociation merge() {
        memberHouseRoleRepository.merge(this);
        memberHouseRoleRepository.flush();
        return this;
    }

    @Transactional
    public boolean remove() {
        return memberHouseRoleRepository.remove(this);
    }

    @Transactional(readOnly = true)
    public static int findHighestRecordIndex(final Long member) throws ELSException {
        return getMemberHouseRoleRepository().findHighestRecordIndex(member);
    }

    public static HouseMemberRoleAssociation findByPK(
            final HouseMemberRoleAssociation association) throws ELSException {
        return getMemberHouseRoleRepository().findByPK(association);
    }

    public Boolean isDuplicate() throws ELSException {
        HouseMemberRoleAssociation duplicate = HouseMemberRoleAssociation.findByPK(this);
        if (duplicate == null) {
            return false;
        }
        return true;
    }
    @Transactional(readOnly = true)
    public boolean isVersionMismatch() throws ELSException {
        Boolean retVal = false;
        HouseMemberRoleAssociation storedDomain=HouseMemberRoleAssociation.findByMemberIdAndId(this.getMember().getId(),this.getRecordIndex());
        HouseMemberRoleAssociation domain = getMemberHouseRoleRepository().findByPK(storedDomain);
        if(domain!=null){
            retVal = (!domain.getVersion().equals(this.version));
        }
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
     * @param isSitting the new checks if is sitting
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
     * @param oathDate the new oath date
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
     * @param constituency the new constituency
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
     * @param internalPollDate the new internal poll date
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
     * @param remarks the new remarks
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
     * @param member the new member
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
     * @param role the new role
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
     * @param house the new house
     */
    public void setHouse(final House house) {
        this.house = house;
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

	public Date getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(final Date resignationDate) {
		this.resignationDate = resignationDate;
	}

	public static List<HouseMemberRoleAssociation> findByMemberIdRolePriorityHouseId(
			final Long member, final int rolepriority, final Long house, final String locale) throws ELSException {
		return getMemberHouseRoleRepository().findByMemberIdRolePriorityHouseId(
				member,rolepriority,house,locale);
	}

	public static List<MasterVO> findAllActiveMemberVOSInSession(final House house,
			final Session session, final String locale) {
		return getMemberHouseRoleRepository().findAllActiveMemberVOSInSession(house,
				session,locale);
	}
	
	public static List<MasterVO> findAllActiveMemberVOSInSession(
			final Session session, final String locale) {
		return getMemberHouseRoleRepository().findAllActiveMemberVOSInSession(
				session,locale);
	}
	
	public static List<Member> findAllActiveMembersInSession(
			final Session session, final String locale) throws ELSException {
		return getMemberHouseRoleRepository().findAllActiveMembersInSession(
				session,locale);
	}

	public static List<MasterVO> findAllActiveMemberVOSInSession(final House house,
            final Session session, final String locale,final String param) {
        return getMemberHouseRoleRepository().findAllActiveMemberVOSInSession(house,
                session,locale,param);
    }

	//public String getConstituencySubtype() {
	//	return constituencySubtype;
	//}

	//public void setConstituencySubtype(String constituencySubtype) {
	//	this.constituencySubtype = constituencySubtype;
	//}
	
	/**
	 * Find all the HouseMemberRoles that have House as @param house, Memberole 
	 * as @param role and @param date is between fromDate and toDate (both dates
	 * are inclusive) 
	 * 
	 * Returns an empty list if there are no active Members.
	 * @throws ELSException 
	 */
	public static List<HouseMemberRoleAssociation> findActiveHouseMemberRoles(final House house,
			final MemberRole role, 
			final Date date,
			final String locale) throws ELSException {
		List<HouseMemberRoleAssociation> associations = HouseMemberRoleAssociation.
			getMemberHouseRoleRepository().findActiveHouseMemberRoles(house, role, date, locale);
		if(associations == null) {
			associations = new ArrayList<HouseMemberRoleAssociation>();
		}
		return associations;
	}
	
	/**** Anand Kulkarni ****/
	public static List<MasterVO> findAllActiveSupportingMemberVOSInSession(final House house,
            final Session session, final String locale,final String param,final Long primaryMemberId) {
        return getMemberHouseRoleRepository().findAllActiveSupportingMemberVOSInSession(house,
                session,locale,param,primaryMemberId);
    }
	
	
	/**** Update By Shubham A ****/
	
	public static List<MasterVO> findAllActiveSupportingMemberVOSInSessionUpdated(final House house,
            final Session session, final String locale,final String param,final Long primaryMemberId) {
        return getMemberHouseRoleRepository().findAllActiveSupportingMemberVOSInSessionUpdated(house,
        		session, locale, param, primaryMemberId);
    }

	
	public static List<MemberRole> findAllActiveRolesOfMemberInSession(Member member, Session session, String locale) {
		return getMemberHouseRoleRepository().findAllActiveRolesOfMemberInSession(member, session, locale);
	}
	
	public static List<Member> findAllActiveMembersInHouse(final House house, final String locale) {
		return getMemberHouseRoleRepository().findAllActiveMembersInHouse(house,locale);
	}

	public static List<MasterVO> findActiveMembersInHouseByTerm(House house, String strParam, String locale) {
		return getMemberHouseRoleRepository().findActiveMembersInHouseByTerm(house,
				strParam, locale);
	}
}
