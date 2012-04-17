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


/**
 * The Class MemberInfo.
 *
 * @author sandeeps
 * @version v1.0.0
 */
public class MemberInfo {

    /** The id. */
    private Long id;

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

    /** The gender. */
    private String gender;

    /** The marital status. */
    private String maritalStatus;

    /** The no of terms. */
    private Integer noOfTerms;

    /** The birth date. */
    private String birthDate;

    private String title;

    /**
     * Instantiates a new member info.
     */
    public MemberInfo() {
        super();
    }

    public MemberInfo(final Long id, final String firstName, final String middleName,
            final String lastName, final String constituency, final String party, final String gender,
            final String maritalStatus, final Integer noOfTerms, final String birthDate) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.constituency = constituency;
        this.party = party;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.noOfTerms = noOfTerms;
        this.birthDate = birthDate;
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

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middle name.
     *
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name.
     *
     * @param middleName the new middle name
     */
    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(final String lastName) {
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

    /**
     * Gets the gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender the new gender
     */
    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }


    public void setMaritalStatus(final String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Gets the no of terms.
     *
     * @return the no of terms
     */
    public Integer getNoOfTerms() {
        return noOfTerms;
    }

    /**
     * Sets the no of terms.
     *
     * @param noOfTerms the new no of terms
     */
    public void setNoOfTerms(final Integer noOfTerms) {
        this.noOfTerms = noOfTerms;
    }

    /**
     * Gets the birth date.
     *
     * @return the birth date
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date.
     *
     * @param birthDate the new birth date
     */
    public void setBirthDate(final String birthDate) {
        this.birthDate = birthDate;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(final String title) {
        this.title = title;
    }

}
