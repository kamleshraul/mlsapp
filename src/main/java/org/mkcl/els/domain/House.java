/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.House.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class House.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "houses")
@JsonIgnoreProperties({"sessions"})
public class House extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    //=============== ATTRIBUTES ===============
    /** The name. */
    @Column(length = 300)
    private String name;
    
    @Column(length=1000)
    private String displayName;

    /** The number. */
    private Integer number;

    /** The type. */
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "housetype_id")
    private HouseType type;

    /** The formation date. */
    @Temporal(TemporalType.DATE)
    private Date formationDate;

    /** The dissolve date. */
    @Temporal(TemporalType.DATE)
    private Date dissolveDate;

    /** The first date. */
    @Temporal(TemporalType.DATE)
    private Date firstDate;

    /** The last date. */
    @Temporal(TemporalType.DATE)
    private Date lastDate;

    /** The governor address date. */
    @Temporal(TemporalType.DATE)
    private Date governorAddressDate;

    /** The total members. */
    private Integer totalMembers;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The sessions. */
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "house_id", referencedColumnName = "id")
    private List<Session> sessions = new ArrayList<Session>();

    @Autowired
    private transient HouseRepository houseRepository;

    //=============== CONSTRUCTORS =============
    /**
     * Instantiates a new house.
     */
    public House() {
        super();
    }

    /**
     * Instantiates a new house.
     *
     * @param name the name
     * @param number the number
     * @param type the type
     * @param formationDate the formation date
     */
    public House(final String name, 
    		final Integer number, 
    		final HouseType type, 
    		final Date formationDate) {
        super();
        this.name = name;
        this.number = number;
        this.type = type;
        this.formationDate = formationDate;
    }

    //=============== VIEW METHODS =============
	
	
	//=============== DOMAIN METHODS ===========	
    private static HouseRepository getHouseRepository() {
        HouseRepository houseRepository = new House().houseRepository;
        if (houseRepository == null) {
            throw new IllegalStateException(
                    "HouseRepository has not been injected in House Domain");
        }
        return houseRepository;
    }

    public static House findCurrentHouse(final String locale) throws ELSException {
        return getHouseRepository().findCurrentHouse(locale);
    }

    public static House findHouseByToFromDate(final Date fromDate, 
    		final Date toDate,
            final String locale) throws ELSException {
        return getHouseRepository().findHouseByToFromDate(fromDate, toDate,
                locale);
    }

    public static List<House> findByHouseType(final String houseType, 
    		final String locale) throws ELSException {
    	return getHouseRepository().findByHouseType(houseType, locale);
	}
    
    /**
     * If @param houseType.type == "upperhouse"
     * then ignore @param date and return the house
     * instance corresponding to @param houseType
     * 
     * Else return an active house instance when
     * houseType = @param houseType
     * firstDate <= @param date <= lastDate
     * 
     */
    public static House find(final HouseType houseType,
    		final Date date,
    		final String locale) {
    	House house = null;
    	
    	String houseTypeType = houseType.getType();
    	if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
    		house = House.findByFieldName(House.class, 
    				"houseType", houseType, locale);
    	}
    	else {
    		house = House.getHouseRepository().find(houseType, date, locale);
    	}
    	
    	return house;
    }
    
    public static Long findActiveMembersCount(final HouseType houseType,
    		final Date date,
    		final String locale) {
    	House house = House.find(houseType, date, locale);
    	MemberRole role = MemberRole.find(houseType, "MEMBER", locale);
    	return House.getHouseRepository().findActiveMembersCount(house, 
    			role, date, locale);
    }
    
    public static Long findActiveMembersCount(final HouseType houseType,
    		final PartyType partyType,
    		final Date date,
    		final String locale) {
    	House house = House.find(houseType, date, locale);
    	MemberRole role = MemberRole.find(houseType, "MEMBER", locale);
    	return House.getHouseRepository().findActiveMembersCount(house, 
    			partyType, role, date, locale);
    }

    public static Long findActiveMembersCount(final HouseType houseType,
    		final Party party,
    		final Date date,
    		final String locale) {
    	House house = House.find(houseType, date, locale);
    	MemberRole role = MemberRole.find(houseType, "MEMBER", locale);
    	return House.getHouseRepository().findActiveMembersCount(house, 
    			party, role, date, locale);
    }
    
    public static Long findIndependentMembersCount(final HouseType houseType,
    		final PartyType partyType,
    		final Date date,
    		final String locale) {
    	House house = House.find(houseType, date, locale);
    	MemberRole role = MemberRole.find(houseType, "MEMBER", locale);
    	Party independentParty = 
    		Party.findByType(ApplicationConstants.INDEPENDENT_PARTY, locale);
    	return House.getHouseRepository().findIndependentMembersCount(house, 
    			partyType, independentParty, role, date, locale);
    }
    
    /**
     * Sorts the result in the @param sortOrder of the party's
     * member strength in the house.
     */
    public static List<Party> find(final HouseType houseType,
    		final PartyType partyType,
    		final Date date,
    		final String sortOrder,
    		final String locale) {
    	List<Party> parties = new ArrayList<Party>();
    	
    	Map<Long, Long> partyStrengthMap = new HashMap<Long, Long>();  
    	
    	House house = House.find(houseType, date, locale);
    	HouseParty houseParty = 
    		HouseParty.findInBetween(house, partyType, date, locale);
    	if(houseParty != null) {
    		// List of parties belonging to @param partyType
    		List<Party> houseParties = houseParty.getParties();
    		for(Party p : houseParties) {
    			Long strength = 
    				House.findActiveMembersCount(houseType, p, date, locale);
    			partyStrengthMap.put(p.getId(), strength);
    			parties.add(p);
    		}
    	}
    	
    	// Independent members belonging to @param partyType
		Long independentStrength = House.findIndependentMembersCount(
				houseType, partyType, date, locale);
		if(independentStrength > 0) {
			Party independent = Party.findByType(
					ApplicationConstants.INDEPENDENT_PARTY, locale);
			partyStrengthMap.put(independent.getId(), independentStrength);
			parties.add(independent);
		}
    	
    	parties = House.sortByStrength(parties, partyStrengthMap, sortOrder);
    	return parties;
    }
    
    /**
     * If @param houseType is BOTHHOUSE then return union of the members
     * of LOWERHOUSE as well as UPPERHOUSE.
     */
    public static List<Member> findActiveMembers(final HouseType houseType,
    		final PartyType partyType,
    		final Date date,
    		final String sortOrder,
    		final String locale) {
    	List<Member> members = new ArrayList<Member>();
    	
    	MemberRole role = MemberRole.find(houseType, "MEMBER", locale);
    	if(houseType.getType().equals(ApplicationConstants.BOTH_HOUSE)) {
    		// Fetch Lower House Members
    		HouseType lowerHouseType = 
    			HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
    		House lowerHouse = House.find(lowerHouseType, date, locale);
    		List<Member> lowerHouseMembers = 
    			House.getHouseRepository().findActiveMembers(lowerHouse, 
    					partyType, role, date, sortOrder, locale);
    		members.addAll(lowerHouseMembers);
    		
    		// Fetch Upper House Members
    		HouseType upperHouseType = 
    			HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale);
    		House upperHouse = House.find(upperHouseType, date, locale);
    		List<Member> upperHouseMembers = 
    			House.getHouseRepository().findActiveMembers(upperHouse, 
    					partyType, role, date, sortOrder, locale);
    		members.addAll(upperHouseMembers);
    		
    		// Sort as per the sortOrder
    		members = Member.sortByFirstname(members, sortOrder);    		
    	}
    	else {
    		House house = House.find(houseType, date, locale);
    		members = House.getHouseRepository().findActiveMembers(house, 
        			partyType, role, date, sortOrder, locale);
    	}
    	
    	return members;
    }
    
    /**
     * If @param houseType is BOTHHOUSE then return union of the members
     * of LOWERHOUSE as well as UPPERHOUSE.
     */
    public static List<Member> findActiveMembers(final HouseType houseType,
    		final PartyType partyType,
    		final Date date,
    		final String nameBeginningWith,
    		final String sortOrder,
    		final String locale) {
    	List<Member> members = new ArrayList<Member>();
    	
    	MemberRole role = MemberRole.find(houseType, "MEMBER", locale);
    	if(houseType.getType().equals(ApplicationConstants.BOTH_HOUSE)) {
    		// Fetch Lower House Members
    		HouseType lowerHouseType = 
    			HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
    		House lowerHouse = House.find(lowerHouseType, date, locale);
    		List<Member> lowerHouseMembers = 
    			House.getHouseRepository().findActiveMembers(lowerHouse, 
    					partyType, role, date, nameBeginningWith, 
    					sortOrder, locale);
    		members.addAll(lowerHouseMembers);
    		
    		// Fetch Upper House Members
    		HouseType upperHouseType = 
    			HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale);
    		House upperHouse = House.find(upperHouseType, date, locale);
    		List<Member> upperHouseMembers = 
    			House.getHouseRepository().findActiveMembers(upperHouse, 
    					partyType, role, date, nameBeginningWith, 
    					sortOrder, locale);
    		members.addAll(upperHouseMembers);
    		
    		// Sort as per the sortOrder
   			members = Member.sortByFirstname(members, sortOrder);    		
    	}
    	else {
    		House house = House.find(houseType, date, locale);
    		members = House.getHouseRepository().findActiveMembers(house, 
        			partyType, role, date, nameBeginningWith, sortOrder, locale);
    	}
    	
    	return members;
    }
    
    private static List<Party> sortByStrength(final List<Party> parties,
    		final Map<Long, Long> partyStrengthMap,
    		final String sortOrder) {
    	List<Party> newPList = new ArrayList<Party>();
    	newPList.addAll(parties);
    	
    	Comparator<Party> ascComparator = new Comparator<Party>() {
    		
			@Override
			public int compare(Party p1, Party p2) {
				Long p1Strength = partyStrengthMap.get(p1.getId());
				Long p2Strength = partyStrengthMap.get(p2.getId());
				return p1Strength.compareTo(p2Strength);
			}
		};
		
		Comparator<Party> descComparator = new Comparator<Party>() {
    		
			@Override
			public int compare(Party p1, Party p2) {
				Long p1Strength = partyStrengthMap.get(p1.getId());
				Long p2Strength = partyStrengthMap.get(p2.getId());
				return p2Strength.compareTo(p1Strength);
			}
		};
		
		if(sortOrder.equals(ApplicationConstants.DESC)) {
			Collections.sort(newPList, descComparator);
		}
		else {
			Collections.sort(newPList, ascComparator);
		}
		
		return newPList;
    }
    
    //=============== GETTERS/SETTERS ==========
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the number.
     *
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the number.
     *
     * @param number the new number
     */
    public void setNumber(final Integer number) {
        this.number = number;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public HouseType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(final HouseType type) {
        this.type = type;
    }

    /**
     * Gets the formation date.
     *
     * @return the formation date
     */
    public Date getFormationDate() {
        return formationDate;
    }

    /**
     * Sets the formation date.
     *
     * @param formationDate the new formation date
     */
    public void setFormationDate(final Date formationDate) {
        this.formationDate = formationDate;
    }

    /**
     * Gets the dissolve date.
     *
     * @return the dissolve date
     */
    public Date getDissolveDate() {
        return dissolveDate;
    }

    /**
     * Sets the dissolve date.
     *
     * @param dissolveDate the new dissolve date
     */
    public void setDissolveDate(final Date dissolveDate) {
        this.dissolveDate = dissolveDate;
    }

    /**
     * Gets the first date.
     *
     * @return the first date
     */
    public Date getFirstDate() {
        return firstDate;
    }

    /**
     * Sets the first date.
     *
     * @param firstDate the new first date
     */
    public void setFirstDate(final Date firstDate) {
        this.firstDate = firstDate;
    }

    /**
     * Gets the last date.
     *
     * @return the last date
     */
    public Date getLastDate() {
        return lastDate;
    }

    /**
     * Sets the last date.
     *
     * @param lastDate the new last date
     */
    public void setLastDate(final Date lastDate) {
        this.lastDate = lastDate;
    }

    /**
     * Gets the governor address date.
     *
     * @return the governor address date
     */
    public Date getGovernorAddressDate() {
        return governorAddressDate;
    }

    /**
     * Sets the governor address date.
     *
     * @param governorAddressDate the new governor address date
     */
    public void setGovernorAddressDate(final Date governorAddressDate) {
        this.governorAddressDate = governorAddressDate;
    }

    /**
     * Gets the total members.
     *
     * @return the total members
     */
    public Integer getTotalMembers() {
        return totalMembers;
    }

    /**
     * Sets the total members.
     *
     * @param totalMembers the new total members
     */
    public void setTotalMembers(final Integer totalMembers) {
        this.totalMembers = totalMembers;
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
     * Gets the sessions.
     *
     * @return the sessions
     */
    public List<Session> getSessions() {
        return sessions;
    }

    /**
     * Sets the sessions.
     *
     * @param sessions the new sessions
     */
    public void setSessions(final List<Session> sessions) {
        this.sessions = sessions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
}