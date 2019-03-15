/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberBiographyVO.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class MemberDetailsForAccountingVO.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
public class MemberDetailsForAccountingVO {

	// ---------------------------------Attributes----------------------------------
    /** The title. */
    private String title;

    /** The first name. */
    private String firstName;

    /** The middle name. */
    private String middleName;

    /** The last name. */
    private String lastName;

    /** The birth date. */
    private String birthDate;

    /** The alias. */
    private String alias;

    /** The gender. */
    private String gender;
    
    /** The latest active houseType. */
    private String houseType;

    /** The constituency. */
    private String constituency;
    
    /** The constituency display name. */
    private String constituencyDisplayName;

    /** The marital status. */
    private String maritalStatus;

    /** The email. */
    private String email;

    /** The mobile. */
    private String mobile;

    /** The permanent address. */
    private AddressVO permanentAddress;

    /** The permanent address. */
    private AddressVO permanentAddress1;

    /** The permanent address. */
    private AddressVO permanentAddress2;

    /** The present address. */
    private AddressVO presentAddress;

    /** The present address. */
    private AddressVO presentAddress1;

    /** The present address. */
    private AddressVO presentAddress2;

    /** The death date. */
    private String deathDate;
    
    
    // ---------------------------------Getters/Setters------------------------------
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

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getConstituency() {
		return constituency;
	}

	public void setConstituency(String constituency) {
		this.constituency = constituency;
	}

	public String getConstituencyDisplayName() {
		return constituencyDisplayName;
	}

	public void setConstituencyDisplayName(String constituencyDisplayName) {
		this.constituencyDisplayName = constituencyDisplayName;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public AddressVO getPresentAddress() {
		return presentAddress;
	}

	public void setPresentAddress(AddressVO presentAddress) {
		this.presentAddress = presentAddress;
	}

	public AddressVO getPresentAddress1() {
		return presentAddress1;
	}

	public void setPresentAddress1(AddressVO presentAddress1) {
		this.presentAddress1 = presentAddress1;
	}

	public AddressVO getPresentAddress2() {
		return presentAddress2;
	}

	public void setPresentAddress2(AddressVO presentAddress2) {
		this.presentAddress2 = presentAddress2;
	}

	public AddressVO getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(AddressVO permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public AddressVO getPermanentAddress1() {
		return permanentAddress1;
	}

	public void setPermanentAddress1(AddressVO permanentAddress1) {
		this.permanentAddress1 = permanentAddress1;
	}

	public AddressVO getPermanentAddress2() {
		return permanentAddress2;
	}

	public void setPermanentAddress2(AddressVO permanentAddress2) {
		this.permanentAddress2 = permanentAddress2;
	}

	public String getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(String deathDate) {
		this.deathDate = deathDate;
	}
    
}