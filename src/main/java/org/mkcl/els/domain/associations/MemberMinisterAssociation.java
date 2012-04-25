/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberMinisterAssociations.java
 * Created On: Apr 23, 2012
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
import org.mkcl.els.domain.Minister;
import org.mkcl.els.repository.MemberMinisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberMinisterAssociations.
 *
 * @author Anand
 * @since v1.0.0
 */
@Entity
@Configurable
@Table(name = "members_ministers")
@IdClass(value = MemberMinisterAssociationPK.class)
@JsonIgnoreProperties({"member"})
public class MemberMinisterAssociation implements Serializable{
	 // ---------------------------------Attributes-------------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Temporal(TemporalType.DATE)
    private Date toDate;
    
    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The member. */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;
    
    /** The minister. */
    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "minister_id", referencedColumnName = "id")
    private Minister minister;
    
    /** The version. */
    @Version
    private Long version;

    /** The locale. */
    @Column(length=10)
    private String locale;
    /** The record index. */
    @NotNull
    private Integer recordIndex;

    /** The member minister repository. */
    @Autowired
    private transient MemberMinisterRepository memberMinisterRepository;

	// ---------------------------------Constructors-------------------------------------------------
	/**
	 * Instantiates a new member minister associations.
	 */
	public MemberMinisterAssociation() {
		super();
	}
	
	/**
	 * Instantiates a new member minister associations.
	 *
	 * @param fromDate the from date
	 * @param toDate the to date
	 * @param remarks the remarks
	 * @param member the member
	 * @param version the version
	 * @param locale the locale
	 * @param minister the minister
	 */
	public MemberMinisterAssociation(Date fromDate, Date toDate,
			String remarks, Member member, Long version, String locale,Minister minister) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.remarks = remarks;
		this.member = member;
		this.version = version;
		this.locale = locale;
		this.minister=minister;
	}
	// ---------------------------------Domain Method-------------------------------------------------
	
	 /**
	 * Gets the member minister repository.
	 *
	 * @return the member minister repository
	 */
	public static MemberMinisterRepository getMemberMinisterRepository() {
		 MemberMinisterRepository memberMinisterRepository = new MemberMinisterAssociation().memberMinisterRepository;
	        if (memberMinisterRepository == null) {
	            throw new IllegalStateException(
	                    "MemberMinistryRepository has not been injected in MemberMinistryAssociation Domain");
	        }
	        return memberMinisterRepository;
	    }
	 
	/**
	 * Find by member id and id.
	 *
	 * @param memberId the member id
	 * @param id the id
	 * @return the member minister associations
	 * @author compaq
	 * @since v1.0.0
	 */
	@Transactional(readOnly = true)
    public static MemberMinisterAssociation findByMemberIdAndId(final Long memberId,
            final int id) {
        return getMemberMinisterRepository().findByMemberIdAndId(memberId, id);
    }

    /**
     * Persist.
     *
     * @return the member minister associations
     * @author compaq
     * @since v1.0.0
     */
    @Transactional
    public MemberMinisterAssociation persist() {
    	memberMinisterRepository.save(this);
        memberMinisterRepository.flush();
        return this;
    }

    /**
     * Merge.
     *
     * @return the member minister associations
     * @author compaq
     * @since v1.0.0
     */
    @Transactional
    public MemberMinisterAssociation merge() {
    	memberMinisterRepository.merge(this);
    	memberMinisterRepository.flush();
        return this;
    }

    /**
     * Removes the.
     *
     * @return true, if successful
     * @author compaq
     * @since v1.0.0
     */
    @Transactional
    public boolean remove() {
        return memberMinisterRepository.remove(this);
    }

    /**
     * Find by pk.
     *
     * @param association the association
     * @return the member minister associations
     * @author compaq
     * @since v1.0.0
     */
    public static MemberMinisterAssociation findByPK(
            final MemberMinisterAssociation association) {
        return getMemberMinisterRepository().findByPK(association);
    }

    /**
     * Find highest record index.
     *
     * @param member the member
     * @return the int
     * @author compaq
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static int findHighestRecordIndex(final Long member) {
        return getMemberMinisterRepository().findHighestRecordIndex(member);
    }

    /**
     * Checks if is duplicate.
     *
     * @return the boolean
     * @author compaq
     * @since v1.0.0
     */
    public Boolean isDuplicate() {
    	MemberMinisterAssociation duplicate = MemberMinisterAssociation.findByPK(this);
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
        MemberMinisterAssociation domain = getMemberMinisterRepository().findByPK(this);
        retVal = (!domain.getVersion().equals(this.version));
        return retVal;
    }
    
 // ---------------------------------getters and setters-------------------------------------------------

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
	public void setRemarks(String remarks) {
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
	public void setMember(Member member) {
		this.member = member;
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
	public void setVersion(Long version) {
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
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Gets the minister.
	 *
	 * @return the minister
	 */
	public Minister getMinister() {
		return minister;
	}

	/**
	 * Sets the minister.
	 *
	 * @param minister the new minister
	 */
	public void setMinister(Minister minister) {
		this.minister = minister;
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
	public void setRecordIndex(Integer recordIndex) {
		this.recordIndex = recordIndex;
	}
    
    
}
