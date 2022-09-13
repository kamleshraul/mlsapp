/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberCompleteDetailVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;

import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberCompleteDetailVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberCompleteDetailVO {

	//Personal Details
	/** The photo. */
	private String photo;

	/** The party flag. */
	private String partyFlag;

	/** The party. */
	private String party;

	/** The constituency. */
	private String constituency;

	/** The specimen signature. */
	private String specimenSignature;

	/** The title. */
	private String title;

	/** The first name. */
	private String firstName;

	/** The middle name. */
	private String middleName;

	/** The last name. */
	private String lastName;

	/** The alias. */
	private String alias;

	/** The birth date. */
	private String birthDate;

	/** The birth place. */
	private String birthPlace;

	/** The nationality. */
	private String nationality;

	/** The gender. */
	private String gender;

	/** The qualification. */
	private String qualification;

	/** The religion. */
	private String religion;

	/** The category. */
	private String category;

	/** The caste. */
	private String caste;

	/** The marital status. */
	private String maritalStatus;

	/** The spouse. */
	private String spouse;

	/** The spouse relation. */
	private String spouseRelation;

	/** The no of daughter. */
	private String noOfDaughter;

	/** The no of sons. */
	private String noOfSons;

	/** The no of children. */
	private String noOfChildren;

	/** The languages. */
	private String languages;

	/** The professions. */
	private String professions;

	/** The death date. */
	private String deathDate;

	/** The condolence date. */
	private String condolenceDate;

	/** The pa name. */
	private String paName;

	/** The pa contact no. */
	private String paContactNo;

	/** The pa address. */
	private String paAddress;

	//Contact Details

    /** The present address. */
	private String presentAddress;

    /** The present address1. */
    private String presentAddress1;

    /** The present address2. */
    private String presentAddress2;

    /** The permanent address. */
    private String permanentAddress;

    /** The permanent address1. */
    private String permanentAddress1;

    /** The permanent address2. */
    private String permanentAddress2;

    /** The office address. */
    private String officeAddress;

    /** The office address1. */
    private String officeAddress1;

    /** The office address2. */
    private String officeAddress2;

    /** The temp address1. */
    private String tempAddress1;

    /** The temp address2. */
    private String tempAddress2;

    /** The fax1. */
    private String fax1;

    /** The fax2. */
    private String fax2;

    /** The fax3. */
    private String fax3;

    /** The fax4. */
    private String fax4;

    /** The fax5. */
    private String fax5;

    /** The fax6. */
    private String fax6;

    /** The fax7. */
    private String fax7;

    /** The fax8. */
    private String fax8;

    /** The fax9. */
    private String fax9;

    /** The fax10. */
    private String fax10;

    /** The fax11. */
    private String fax11;

    /** The telephone1. */
    private String telephone1;

    /** The telephone2. */
    private String telephone2;

    /** The telephone3. */
    private String telephone3;

    /** The telephone4. */
    private String telephone4;

    /** The telephone5. */
    private String telephone5;

    /** The telephone6. */
    private String telephone6;

    /** The telephone7. */
    private String telephone7;

    /** The telephone8. */
    private String telephone8;

    /** The telephone9. */
    private String telephone9;

    /** The telephone10. */
    private String telephone10;

    /** The telephone11. */
    private String telephone11;

    /** The website1. */
    private String website1;

    /** The website2. */
    private String website2;

    /** The mobile1. */
    private String mobile1;

    /** The mobile2. */
    private String mobile2;

    /** The email1. */
    private String email1;

    /** The email2. */
    private String email2;

	//Other Details
	/** The positions held. */
	private List<PositionHeldVO> positionsHeld;

	/** The other information. */
	private String otherInformation;

	/** The countries visited. */
	private String countriesVisited;

	/** The publications. */
	private String publications;

	/** The special interest. */
	private String specialInterest;

	//House Details
    /** The house member role associations. */
	private List<HouseMemberRoleAssociation> houseMemberRoleAssociations;

	//Party Details
    /** The member party associations. */
	private List<MemberPartyAssociation> memberPartyAssociations;

	//Election Details
    /** The election results. */
	private List<ElectionResultVO> electionResults;
	
	//Member Minister Details
    /** member minister. */
	private List<MemberMinisterVO> memberMinisters;

	/**
	 * Gets the photo.
	 *
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * Sets the photo.
	 *
	 * @param photo the new photo
	 */
	public void setPhoto(final String photo) {
		this.photo = photo;
	}

	/**
	 * Gets the party flag.
	 *
	 * @return the party flag
	 */
	public String getPartyFlag() {
		return partyFlag;
	}

	/**
	 * Sets the party flag.
	 *
	 * @param partyFlag the new party flag
	 */
	public void setPartyFlag(final String partyFlag) {
		this.partyFlag = partyFlag;
	}

	/**
	 * Gets the specimen signature.
	 *
	 * @return the specimen signature
	 */
	public String getSpecimenSignature() {
		return specimenSignature;
	}

	/**
	 * Sets the specimen signature.
	 *
	 * @param specimenSignature the new specimen signature
	 */
	public void setSpecimenSignature(final String specimenSignature) {
		this.specimenSignature = specimenSignature;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(final String title) {
		this.title = title;
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
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 *
	 * @param alias the new alias
	 */
	public void setAlias(final String alias) {
		this.alias = alias;
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

	/**
	 * Gets the birth place.
	 *
	 * @return the birth place
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * Sets the birth place.
	 *
	 * @param birthPlace the new birth place
	 */
	public void setBirthPlace(final String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * Gets the nationality.
	 *
	 * @return the nationality
	 */
	public String getNationality() {
		return nationality;
	}

	/**
	 * Sets the nationality.
	 *
	 * @param nationality the new nationality
	 */
	public void setNationality(final String nationality) {
		this.nationality = nationality;
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

	/**
	 * Gets the qualification.
	 *
	 * @return the qualification
	 */
	public String getQualification() {
		return qualification;
	}

	/**
	 * Sets the qualification.
	 *
	 * @param qualification the new qualification
	 */
	public void setQualification(final String qualification) {
		this.qualification = qualification;
	}

	/**
	 * Gets the religion.
	 *
	 * @return the religion
	 */
	public String getReligion() {
		return religion;
	}

	/**
	 * Sets the religion.
	 *
	 * @param religion the new religion
	 */
	public void setReligion(final String religion) {
		this.religion = religion;
	}

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(final String category) {
		this.category = category;
	}

	/**
	 * Gets the caste.
	 *
	 * @return the caste
	 */
	public String getCaste() {
		return caste;
	}

	/**
	 * Sets the caste.
	 *
	 * @param caste the new caste
	 */
	public void setCaste(final String caste) {
		this.caste = caste;
	}

	/**
	 * Gets the marital status.
	 *
	 * @return the marital status
	 */
	public String getMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * Sets the marital status.
	 *
	 * @param maritalStatus the new marital status
	 */
	public void setMaritalStatus(final String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
	 * Gets the spouse.
	 *
	 * @return the spouse
	 */
	public String getSpouse() {
		return spouse;
	}

	/**
	 * Sets the spouse.
	 *
	 * @param spouse the new spouse
	 */
	public void setSpouse(final String spouse) {
		this.spouse = spouse;
	}

	/**
	 * Gets the no of daughter.
	 *
	 * @return the no of daughter
	 */
	public String getNoOfDaughter() {
		return noOfDaughter;
	}

	/**
	 * Sets the no of daughter.
	 *
	 * @param noOfDaughter the new no of daughter
	 */
	public void setNoOfDaughter(final String noOfDaughter) {
		this.noOfDaughter = noOfDaughter;
	}

	/**
	 * Gets the no of sons.
	 *
	 * @return the no of sons
	 */
	public String getNoOfSons() {
		return noOfSons;
	}

	/**
	 * Sets the no of sons.
	 *
	 * @param noOfSons the new no of sons
	 */
	public void setNoOfSons(final String noOfSons) {
		this.noOfSons = noOfSons;
	}

	/**
	 * Gets the languages.
	 *
	 * @return the languages
	 */
	public String getLanguages() {
		return languages;
	}

	/**
	 * Sets the languages.
	 *
	 * @param languages the new languages
	 */
	public void setLanguages(final String languages) {
		this.languages = languages;
	}

	/**
	 * Gets the professions.
	 *
	 * @return the professions
	 */
	public String getProfessions() {
		return professions;
	}

	/**
	 * Sets the professions.
	 *
	 * @param professions the new professions
	 */
	public void setProfessions(final String professions) {
		this.professions = professions;
	}

	/**
	 * Gets the death date.
	 *
	 * @return the death date
	 */
	public String getDeathDate() {
		return deathDate;
	}

	/**
	 * Sets the death date.
	 *
	 * @param deathDate the new death date
	 */
	public void setDeathDate(final String deathDate) {
		this.deathDate = deathDate;
	}

	/**
	 * Gets the condolence date.
	 *
	 * @return the condolence date
	 */
	public String getCondolenceDate() {
		return condolenceDate;
	}

	/**
	 * Sets the condolence date.
	 *
	 * @param condolenceDate the new condolence date
	 */
	public void setCondolenceDate(final String condolenceDate) {
		this.condolenceDate = condolenceDate;
	}

	/**
	 * Gets the pa name.
	 *
	 * @return the pa name
	 */
	public String getPaName() {
		return paName;
	}

	/**
	 * Sets the pa name.
	 *
	 * @param paName the new pa name
	 */
	public void setPaName(final String paName) {
		this.paName = paName;
	}

	/**
	 * Gets the pa contact no.
	 *
	 * @return the pa contact no
	 */
	public String getPaContactNo() {
		return paContactNo;
	}

	/**
	 * Sets the pa contact no.
	 *
	 * @param paContactNo the new pa contact no
	 */
	public void setPaContactNo(final String paContactNo) {
		this.paContactNo = paContactNo;
	}

	/**
	 * Gets the pa address.
	 *
	 * @return the pa address
	 */
	public String getPaAddress() {
		return paAddress;
	}

	/**
	 * Sets the pa address.
	 *
	 * @param paAddress the new pa address
	 */
	public void setPaAddress(final String paAddress) {
		this.paAddress = paAddress;
	}

	/**
	 * Gets the present address.
	 *
	 * @return the present address
	 */
	public String getPresentAddress() {
		return presentAddress;
	}

	/**
	 * Sets the present address.
	 *
	 * @param presentAddress the new present address
	 */
	public void setPresentAddress(final String presentAddress) {
		this.presentAddress = presentAddress;
	}

	/**
	 * Gets the present address1.
	 *
	 * @return the present address1
	 */
	public String getPresentAddress1() {
		return presentAddress1;
	}

	/**
	 * Sets the present address1.
	 *
	 * @param presentAddress1 the new present address1
	 */
	public void setPresentAddress1(final String presentAddress1) {
		this.presentAddress1 = presentAddress1;
	}

	/**
	 * Gets the present address2.
	 *
	 * @return the present address2
	 */
	public String getPresentAddress2() {
		return presentAddress2;
	}

	/**
	 * Sets the present address2.
	 *
	 * @param presentAddress2 the new present address2
	 */
	public void setPresentAddress2(final String presentAddress2) {
		this.presentAddress2 = presentAddress2;
	}

	/**
	 * Gets the permanent address.
	 *
	 * @return the permanent address
	 */
	public String getPermanentAddress() {
		return permanentAddress;
	}

	/**
	 * Sets the permanent address.
	 *
	 * @param permanentAddress the new permanent address
	 */
	public void setPermanentAddress(final String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	/**
	 * Gets the permanent address1.
	 *
	 * @return the permanent address1
	 */
	public String getPermanentAddress1() {
		return permanentAddress1;
	}

	/**
	 * Sets the permanent address1.
	 *
	 * @param permanentAddress1 the new permanent address1
	 */
	public void setPermanentAddress1(final String permanentAddress1) {
		this.permanentAddress1 = permanentAddress1;
	}

	/**
	 * Gets the permanent address2.
	 *
	 * @return the permanent address2
	 */
	public String getPermanentAddress2() {
		return permanentAddress2;
	}

	/**
	 * Sets the permanent address2.
	 *
	 * @param permanentAddress2 the new permanent address2
	 */
	public void setPermanentAddress2(final String permanentAddress2) {
		this.permanentAddress2 = permanentAddress2;
	}

	/**
	 * Gets the office address.
	 *
	 * @return the office address
	 */
	public String getOfficeAddress() {
		return officeAddress;
	}

	/**
	 * Sets the office address.
	 *
	 * @param officeAddress the new office address
	 */
	public void setOfficeAddress(final String officeAddress) {
		this.officeAddress = officeAddress;
	}

	/**
	 * Gets the office address1.
	 *
	 * @return the office address1
	 */
	public String getOfficeAddress1() {
		return officeAddress1;
	}

	/**
	 * Sets the office address1.
	 *
	 * @param officeAddress1 the new office address1
	 */
	public void setOfficeAddress1(final String officeAddress1) {
		this.officeAddress1 = officeAddress1;
	}

	/**
	 * Gets the office address2.
	 *
	 * @return the office address2
	 */
	public String getOfficeAddress2() {
		return officeAddress2;
	}

	/**
	 * Sets the office address2.
	 *
	 * @param officeAddress2 the new office address2
	 */
	public void setOfficeAddress2(final String officeAddress2) {
		this.officeAddress2 = officeAddress2;
	}

	/**
	 * Gets the temp address1.
	 *
	 * @return the temp address1
	 */
	public String getTempAddress1() {
		return tempAddress1;
	}

	/**
	 * Sets the temp address1.
	 *
	 * @param tempAddress1 the new temp address1
	 */
	public void setTempAddress1(final String tempAddress1) {
		this.tempAddress1 = tempAddress1;
	}

	/**
	 * Gets the temp address2.
	 *
	 * @return the temp address2
	 */
	public String getTempAddress2() {
		return tempAddress2;
	}

	/**
	 * Sets the temp address2.
	 *
	 * @param tempAddress2 the new temp address2
	 */
	public void setTempAddress2(final String tempAddress2) {
		this.tempAddress2 = tempAddress2;
	}

	/**
	 * Gets the fax1.
	 *
	 * @return the fax1
	 */
	public String getFax1() {
		return fax1;
	}

	/**
	 * Sets the fax1.
	 *
	 * @param fax1 the new fax1
	 */
	public void setFax1(final String fax1) {
		this.fax1 = fax1;
	}

	/**
	 * Gets the fax2.
	 *
	 * @return the fax2
	 */
	public String getFax2() {
		return fax2;
	}

	/**
	 * Sets the fax2.
	 *
	 * @param fax2 the new fax2
	 */
	public void setFax2(final String fax2) {
		this.fax2 = fax2;
	}

	/**
	 * Gets the fax3.
	 *
	 * @return the fax3
	 */
	public String getFax3() {
		return fax3;
	}

	/**
	 * Sets the fax3.
	 *
	 * @param fax3 the new fax3
	 */
	public void setFax3(final String fax3) {
		this.fax3 = fax3;
	}

	/**
	 * Gets the fax4.
	 *
	 * @return the fax4
	 */
	public String getFax4() {
		return fax4;
	}

	/**
	 * Sets the fax4.
	 *
	 * @param fax4 the new fax4
	 */
	public void setFax4(final String fax4) {
		this.fax4 = fax4;
	}

	/**
	 * Gets the fax5.
	 *
	 * @return the fax5
	 */
	public String getFax5() {
		return fax5;
	}

	/**
	 * Sets the fax5.
	 *
	 * @param fax5 the new fax5
	 */
	public void setFax5(final String fax5) {
		this.fax5 = fax5;
	}

	/**
	 * Gets the fax6.
	 *
	 * @return the fax6
	 */
	public String getFax6() {
		return fax6;
	}

	/**
	 * Sets the fax6.
	 *
	 * @param fax6 the new fax6
	 */
	public void setFax6(final String fax6) {
		this.fax6 = fax6;
	}

	/**
	 * Gets the fax7.
	 *
	 * @return the fax7
	 */
	public String getFax7() {
		return fax7;
	}

	/**
	 * Sets the fax7.
	 *
	 * @param fax7 the new fax7
	 */
	public void setFax7(final String fax7) {
		this.fax7 = fax7;
	}

	/**
	 * Gets the fax8.
	 *
	 * @return the fax8
	 */
	public String getFax8() {
		return fax8;
	}

	/**
	 * Sets the fax8.
	 *
	 * @param fax8 the new fax8
	 */
	public void setFax8(final String fax8) {
		this.fax8 = fax8;
	}

	/**
	 * Gets the fax9.
	 *
	 * @return the fax9
	 */
	public String getFax9() {
		return fax9;
	}

	/**
	 * Sets the fax9.
	 *
	 * @param fax9 the new fax9
	 */
	public void setFax9(final String fax9) {
		this.fax9 = fax9;
	}

	/**
	 * Gets the fax10.
	 *
	 * @return the fax10
	 */
	public String getFax10() {
		return fax10;
	}

	/**
	 * Sets the fax10.
	 *
	 * @param fax10 the new fax10
	 */
	public void setFax10(final String fax10) {
		this.fax10 = fax10;
	}

	/**
	 * Gets the fax11.
	 *
	 * @return the fax11
	 */
	public String getFax11() {
		return fax11;
	}

	/**
	 * Sets the fax11.
	 *
	 * @param fax11 the new fax11
	 */
	public void setFax11(final String fax11) {
		this.fax11 = fax11;
	}

	/**
	 * Gets the telephone1.
	 *
	 * @return the telephone1
	 */
	public String getTelephone1() {
		return telephone1;
	}

	/**
	 * Sets the telephone1.
	 *
	 * @param telephone1 the new telephone1
	 */
	public void setTelephone1(final String telephone1) {
		this.telephone1 = telephone1;
	}

	/**
	 * Gets the telephone2.
	 *
	 * @return the telephone2
	 */
	public String getTelephone2() {
		return telephone2;
	}

	/**
	 * Sets the telephone2.
	 *
	 * @param telephone2 the new telephone2
	 */
	public void setTelephone2(final String telephone2) {
		this.telephone2 = telephone2;
	}

	/**
	 * Gets the telephone3.
	 *
	 * @return the telephone3
	 */
	public String getTelephone3() {
		return telephone3;
	}

	/**
	 * Sets the telephone3.
	 *
	 * @param telephone3 the new telephone3
	 */
	public void setTelephone3(final String telephone3) {
		this.telephone3 = telephone3;
	}

	/**
	 * Gets the telephone4.
	 *
	 * @return the telephone4
	 */
	public String getTelephone4() {
		return telephone4;
	}

	/**
	 * Sets the telephone4.
	 *
	 * @param telephone4 the new telephone4
	 */
	public void setTelephone4(final String telephone4) {
		this.telephone4 = telephone4;
	}

	/**
	 * Gets the telephone5.
	 *
	 * @return the telephone5
	 */
	public String getTelephone5() {
		return telephone5;
	}

	/**
	 * Sets the telephone5.
	 *
	 * @param telephone5 the new telephone5
	 */
	public void setTelephone5(final String telephone5) {
		this.telephone5 = telephone5;
	}

	/**
	 * Gets the telephone6.
	 *
	 * @return the telephone6
	 */
	public String getTelephone6() {
		return telephone6;
	}

	/**
	 * Sets the telephone6.
	 *
	 * @param telephone6 the new telephone6
	 */
	public void setTelephone6(final String telephone6) {
		this.telephone6 = telephone6;
	}

	/**
	 * Gets the telephone7.
	 *
	 * @return the telephone7
	 */
	public String getTelephone7() {
		return telephone7;
	}

	/**
	 * Sets the telephone7.
	 *
	 * @param telephone7 the new telephone7
	 */
	public void setTelephone7(final String telephone7) {
		this.telephone7 = telephone7;
	}

	/**
	 * Gets the telephone8.
	 *
	 * @return the telephone8
	 */
	public String getTelephone8() {
		return telephone8;
	}

	/**
	 * Sets the telephone8.
	 *
	 * @param telephone8 the new telephone8
	 */
	public void setTelephone8(final String telephone8) {
		this.telephone8 = telephone8;
	}

	/**
	 * Gets the telephone9.
	 *
	 * @return the telephone9
	 */
	public String getTelephone9() {
		return telephone9;
	}

	/**
	 * Sets the telephone9.
	 *
	 * @param telephone9 the new telephone9
	 */
	public void setTelephone9(final String telephone9) {
		this.telephone9 = telephone9;
	}

	/**
	 * Gets the telephone10.
	 *
	 * @return the telephone10
	 */
	public String getTelephone10() {
		return telephone10;
	}

	/**
	 * Sets the telephone10.
	 *
	 * @param telephone10 the new telephone10
	 */
	public void setTelephone10(final String telephone10) {
		this.telephone10 = telephone10;
	}

	/**
	 * Gets the telephone11.
	 *
	 * @return the telephone11
	 */
	public String getTelephone11() {
		return telephone11;
	}

	/**
	 * Sets the telephone11.
	 *
	 * @param telephone11 the new telephone11
	 */
	public void setTelephone11(final String telephone11) {
		this.telephone11 = telephone11;
	}

	/**
	 * Gets the website1.
	 *
	 * @return the website1
	 */
	public String getWebsite1() {
		return website1;
	}

	/**
	 * Sets the website1.
	 *
	 * @param website1 the new website1
	 */
	public void setWebsite1(final String website1) {
		this.website1 = website1;
	}

	/**
	 * Gets the website2.
	 *
	 * @return the website2
	 */
	public String getWebsite2() {
		return website2;
	}

	/**
	 * Sets the website2.
	 *
	 * @param website2 the new website2
	 */
	public void setWebsite2(final String website2) {
		this.website2 = website2;
	}

	/**
	 * Gets the mobile1.
	 *
	 * @return the mobile1
	 */
	public String getMobile1() {
		return mobile1;
	}

	/**
	 * Sets the mobile1.
	 *
	 * @param mobile1 the new mobile1
	 */
	public void setMobile1(final String mobile1) {
		this.mobile1 = mobile1;
	}

	/**
	 * Gets the mobile2.
	 *
	 * @return the mobile2
	 */
	public String getMobile2() {
		return mobile2;
	}

	/**
	 * Sets the mobile2.
	 *
	 * @param mobile2 the new mobile2
	 */
	public void setMobile2(final String mobile2) {
		this.mobile2 = mobile2;
	}

	/**
	 * Gets the email1.
	 *
	 * @return the email1
	 */
	public String getEmail1() {
		return email1;
	}

	/**
	 * Sets the email1.
	 *
	 * @param email1 the new email1
	 */
	public void setEmail1(final String email1) {
		this.email1 = email1;
	}

	/**
	 * Gets the email2.
	 *
	 * @return the email2
	 */
	public String getEmail2() {
		return email2;
	}

	/**
	 * Sets the email2.
	 *
	 * @param email2 the new email2
	 */
	public void setEmail2(final String email2) {
		this.email2 = email2;
	}

	/**
	 * Gets the positions held.
	 *
	 * @return the positions held
	 */
	public List<PositionHeldVO> getPositionsHeld() {
		return positionsHeld;
	}

	/**
	 * Sets the positions held.
	 *
	 * @param positionsHeld the new positions held
	 */
	public void setPositionsHeld(final List<PositionHeldVO> positionsHeld) {
		this.positionsHeld = positionsHeld;
	}

	/**
	 * Gets the other information.
	 *
	 * @return the other information
	 */
	public String getOtherInformation() {
		return otherInformation;
	}

	/**
	 * Sets the other information.
	 *
	 * @param otherInformation the new other information
	 */
	public void setOtherInformation(final String otherInformation) {
		this.otherInformation = otherInformation;
	}

	/**
	 * Gets the countries visited.
	 *
	 * @return the countries visited
	 */
	public String getCountriesVisited() {
		return countriesVisited;
	}

	/**
	 * Sets the countries visited.
	 *
	 * @param countriesVisited the new countries visited
	 */
	public void setCountriesVisited(final String countriesVisited) {
		this.countriesVisited = countriesVisited;
	}

	/**
	 * Gets the publications.
	 *
	 * @return the publications
	 */
	public String getPublications() {
		return publications;
	}

	/**
	 * Sets the publications.
	 *
	 * @param publications the new publications
	 */
	public void setPublications(final String publications) {
		this.publications = publications;
	}

	/**
	 * Gets the house member role associations.
	 *
	 * @return the house member role associations
	 */
	public List<HouseMemberRoleAssociation> getHouseMemberRoleAssociations() {
		return houseMemberRoleAssociations;
	}

	/**
	 * Sets the house member role associations.
	 *
	 * @param houseMemberRoleAssociations the new house member role associations
	 */
	public void setHouseMemberRoleAssociations(
			final List<HouseMemberRoleAssociation> houseMemberRoleAssociations) {
		this.houseMemberRoleAssociations = houseMemberRoleAssociations;
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


	/**
	 * Gets the election results.
	 *
	 * @return the election results
	 */
	public List<ElectionResultVO> getElectionResults() {
		return electionResults;
	}

	/**
	 * Sets the election results.
	 *
	 * @param electionResults the new election results
	 */
	public void setElectionResults(final List<ElectionResultVO> electionResults) {
		this.electionResults = electionResults;
	}
	
	
	/**
	 * Sets the Member Minister.
	 *
	 * @return the election results
	 */
	public List<MemberMinisterVO> getMemberMinisters() {
		return memberMinisters;
	}

	/**
	 * Gets the Member Minister.
	 *
	 * @return the election results
	 */
	public void setMemberMinister(List<MemberMinisterVO> memberMinisters) {
		this.memberMinisters = memberMinisters;
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
	 * Gets the no of children.
	 *
	 * @return the no of children
	 */
	public String getNoOfChildren() {
		return noOfChildren;
	}

	/**
	 * Sets the no of children.
	 *
	 * @param noOfChildren the new no of children
	 */
	public void setNoOfChildren(final String noOfChildren) {
		this.noOfChildren = noOfChildren;
	}

	/**
	 * Gets the spouse relation.
	 *
	 * @return the spouse relation
	 */
	public String getSpouseRelation() {
		return spouseRelation;
	}

	/**
	 * Sets the spouse relation.
	 *
	 * @param spouseRelation the new spouse relation
	 */
	public void setSpouseRelation(final String spouseRelation) {
		this.spouseRelation = spouseRelation;
	}

	/**
	 * Gets the special interest.
	 *
	 * @return the special interest
	 */
	public String getSpecialInterest() {
		return specialInterest;
	}

	/**
	 * Sets the special interest.
	 *
	 * @param specialInterest the new special interest
	 */
	public void setSpecialInterest(final String specialInterest) {
		this.specialInterest = specialInterest;
	}


}
