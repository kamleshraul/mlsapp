/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Party.java
 * Created On: Dec 20, 2011
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
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
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.mkcl.els.repository.DeviceTypeRepository;
import org.mkcl.els.repository.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Party.
 *
 * @author dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "parties")
@JsonIgnoreProperties({"registeredOfficeAddress","stateOfficeAddress","contact","partySymbols","memberPartyAssociations","partyType"})
public class Party extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /**
     * The Constant serialVersionUID.
     */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The abbreviation. */
    @Column(length = 100)
    private String shortName;

    /** The establishment date. */
    @Temporal(TemporalType.DATE)
    private Date establishmentDate;

    /** The registered office address. */
    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name = "registered_office_address_id")
    private Address registeredOfficeAddress;

    /** The state office address. */
    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name = "state_office_address_id")
    private Address stateOfficeAddress;

    /** The contact. */
    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    /** The is dissolved. */
    private Boolean isDissolved;

    /** The party symbols. */
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    @JoinColumn(name = "party_id", referencedColumnName = "id")
    private List<PartySymbol> partySymbols;
    
    //@Column(length = 150)
    /** The PartyType. */
    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name = "party_type_id", referencedColumnName = "id")
    private PartyType partyType;

    /** The member party associations. */
    @OneToMany(mappedBy = "party",fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<MemberPartyAssociation> memberPartyAssociations;
    
    @Autowired
    private transient PartyRepository partyRepository;
    

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new party.
     */
    public Party() {
        super();
    }

    /**
     * Instantiates a new party.
     *
     * @param name the name
     * @param shortName the short name
     * @param establishmentDate the establishment date
     */
    public Party(final String name, final String shortName, final Date establishmentDate) {
        super();
        this.name = name;
        this.shortName = shortName;
        this.establishmentDate = establishmentDate;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    
    public static PartyRepository getPartyRepository() {
    	PartyRepository partyRepository = new Party().partyRepository;
        if (partyRepository == null) {
            throw new IllegalStateException(
                    "PartyRepository has not been injected in Party Domain");
        }
        return partyRepository;
    }
    
    public static List<Party> findActiveParties(final String locale) {
    	return Party.findAllByFieldName(Party.class, "isDissolved", false, 
    			"name", ApplicationConstants.ASC, locale);
    }
    
    public static List<Party> findActiveParties(final House house, final String locale){
    	return getPartyRepository().findActiveParties(house, locale);
    }
    
    public static Party findByType(final String type,
    		final String locale) {
    	return Party.findByFieldName(Party.class, "partyType.type", type, locale);
    }
    
    public Boolean isIndependent() {
    	String partyType = this.partyType.getType();
    	String INDEPENDENT = ApplicationConstants.INDEPENDENT_PARTY;
    	
    	if(INDEPENDENT.equals(partyType)) {
    		return true;
    	}
    	
    	return false;
    }

	public String findCurrentPartySymbolPhoto() {
		return getPartyRepository().findCurrentPartySymbolPhoto(this);
	}
    
    // -------------------------------Getters/Setters---------------------------------------------
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
     * Gets the short name.
     *
     * @return the short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name.
     *
     * @param shortName the new short name
     */
    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the establishment date.
     *
     * @return the establishment date
     */
    public Date getEstablishmentDate() {
        return establishmentDate;
    }

    /**
     * Sets the establishment date.
     *
     * @param establishmentDate the new establishment date
     */
    public void setEstablishmentDate(final Date establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    /**
     * Gets the registered office address.
     *
     * @return the registered office address
     */
    public Address getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    /**
     * Sets the registered office address.
     *
     * @param registeredOfficeAddress the new registered office address
     */
    public void setRegisteredOfficeAddress(final Address registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    /**
     * Gets the state office address.
     *
     * @return the state office address
     */
    public Address getStateOfficeAddress() {
        return stateOfficeAddress;
    }

    /**
     * Sets the state office address.
     *
     * @param stateOfficeAddress the new state office address
     */
    public void setStateOfficeAddress(final Address stateOfficeAddress) {
        this.stateOfficeAddress = stateOfficeAddress;
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the contact.
     *
     * @param contact the new contact
     */
    public void setContact(final Contact contact) {
        this.contact = contact;
    }

    /**
     * Gets the checks if is dissolved.
     *
     * @return the checks if is dissolved
     */
    public Boolean getIsDissolved() {
        return isDissolved;
    }

    /**
     * Sets the checks if is dissolved.
     *
     * @param isDissolved the new checks if is dissolved
     */
    public void setIsDissolved(final Boolean isDissolved) {
        this.isDissolved = isDissolved;
    }

    /**
     * Gets the party symbols.
     *
     * @return the party symbols
     */
    public List<PartySymbol> getPartySymbols() {
        return partySymbols;
    }

    /**
     * Sets the party symbols.
     *
     * @param partySymbols the new party symbols
     */
    public void setPartySymbols(final List<PartySymbol> partySymbols) {
        this.partySymbols = partySymbols;
    }

    /**
     * Gets the member party associations.
     *
     * @return the member party associations
     */
    public List<MemberPartyAssociation> getMemberPartyAssociations() {
        return memberPartyAssociations;
    }

    /**
     * Sets the member party associations.
     *
     * @param memberPartyAssociations the new member party associations
     */
    public void setMemberPartyAssociations(
            final List<MemberPartyAssociation> memberPartyAssociations) {
        this.memberPartyAssociations = memberPartyAssociations;
    }

	public PartyType getPartyType() {
		return partyType;
	}

	public void setPartyType(PartyType partyType) {
		this.partyType = partyType;
	}
	
	
	public static List<MasterVO> getPartyWiseCountOfMemberForMobile(final House house , final String locale){
		return getPartyRepository().getPartyWiseCountOfMemberForMobile(house,locale);
	}
	

//	public String getType() {
//		return type;
//	}
//
//	public void setType(final String type) {
//		this.type = type;
//	}
}
