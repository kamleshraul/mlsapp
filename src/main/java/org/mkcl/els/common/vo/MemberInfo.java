/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberInfo.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.common.vo;


// TODO: Auto-generated Javadoc
/**
 * The Class MemberInfo.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberInfo {

    /** The id. */
    private Long id;
    
    private String title;

    /** The member name. */
    private String firstName;

    /** The middle name. */
    private String middleName;

    /** The last name. */
    private String lastName;


    /** The member constituency. */
    private String constituency;

    /** The party. */
    private String party;
    
    private String district;
    
    private Integer recordIndex;
    
    private String partyFD;
    
    private String partyTD;
    
    private String gender;
    
    private String maritalStatus;
    
    private String birthDate;

    /**
     * Instantiates a new member info.
     */
    public MemberInfo() {
        super();
    }


    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }


    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(final Long id) {
        this.id = id;
    }


   

    public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getMiddleName() {
		return middleName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	/**
     * Gets the constituency.
     *
     * @return the constituency
     */
    public String getConstituency() {
        return constituency;
    }


    /**
     * Sets the constituency.
     *
     * @param constituency the new constituency
     */
    public void setConstituency(final String constituency) {
        this.constituency = constituency;
    }


    /**
     * Gets the party.
     *
     * @return the party
     */
    public String getParty() {
        return party;
    }


    /**
     * Sets the party.
     *
     * @param party the new party
     */
    public void setParty(final String party) {
        this.party = party;
    }


	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public Integer getRecordIndex() {
		return recordIndex;
	}


	public void setRecordIndex(Integer recordIndex) {
		this.recordIndex = recordIndex;
	}


	public String getPartyFD() {
		return partyFD;
	}


	public void setPartyFD(String partyFD) {
		this.partyFD = partyFD;
	}


	public String getPartyTD() {
		return partyTD;
	}


	public void setPartyTD(String partyTD) {
		this.partyTD = partyTD;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getMaritalStatus() {
		return maritalStatus;
	}


	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}


	public String getBirthDate() {
		return birthDate;
	}


	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}		
}
