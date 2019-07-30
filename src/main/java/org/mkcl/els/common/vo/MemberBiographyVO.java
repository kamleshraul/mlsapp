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

import java.util.List;

/**
 * The Class MemberBiographyVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberBiographyVO {

    /** The id. */
    private Long id;

    /** The photo. */
    private String photo;

    /** The title. */
    private String title;

    /** The first name. */
    private String firstName;

    /** The middle name. */
    private String middleName;

    /** The last name. */
    private String lastName;

    /** The enable aliasing. */
    private boolean enableAliasing;

    /** The alias. */
    private String alias;

    /** The gender. */
    private String gender;

    /** The constituency. */
    private String constituency;

    /** The party name. */
    private String partyName;

    /** The party flag. */
    private String partyFlag;

    /** The father name. */
    private String fatherName;

    /** The mother name. */
    private String motherName;

    /** The birth date. */
    private String birthDate;

    /** The birth date. */
    private String placeOfBirth;

    /** The death date. */
    private String deathDate;

    /** The condolence date. */
    private String condolenceDate;

    /** The obituary. */
    private String obituary;

    /** The marital status. */
    private String maritalStatus;

    /** The marriage date. */
    private String marriageDate;

    /** The spouse name. */
    private String spouseName;

    /** The spouse relation. */
    private String spouseRelation;

    /** The no of sons. */
    private String noOfSons;

    /** The no of daughter. */
    private String noOfDaughter;

    /** The no of children. */
    private String noOfChildren;

    /** The educational qualification. */
    private String educationalQualification;

    /** The profession. */
    private String profession;

    /** The email. */
    private String email;

    /** The present address. */
    private String presentAddress;

    /** The present address. */
    private String presentAddress1;

    /** The present address. */
    private String presentAddress2;

    /** The permanent address. */
    private String permanentAddress;

    /** The permanent address. */
    private String permanentAddress1;

    /** The permanent address. */
    private String permanentAddress2;

    /** The office address. */
    private String officeAddress;

    /** The office address. */
    private String officeAddress1;

    /** The office address. */
    private String officeAddress2;

    /** The aamdar address. */
    private String tempAddress1;

    /** The aamdar address. */
    private String tempAddress2;

    /** The aamdar address. */
    private String correspondenceAddress;

    /** The member positions. */
    private String positionDetails;

    /** The socio cultural activities. */
    private String socioCulturalActivities;

    /** The literary artistic sc accomplishment. */
    private String literaryArtisticScAccomplishment;

    /** The publications. */
    private String publications;

    /** The special interests. */
    private String specialInterests;

    /** The pastime recreation. */
    private String pastimeRecreation;

    /** The sports clubs. */
    private String sportsClubs;

    /** The countries visited. */
    private String countriesVisited;

    /** The other info. */
    private String otherInfo;

    /** The educational cul act. */
    private String educationalCulAct;
    /** The locale. */
    private String locale;

    /** The languages known. */
    private String languagesKnown;

    /** The website. */
    private String website;

    /** The fax. */
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

    /** The fax12. */
    private String fax12;

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

    /** The telephone12. */
    private String telephone12;

    /** The mobile. */
    private String mobile;

    /** The no of voters. */
    private String noOfVoters;
    /** The valid votes. */
    private String validVotes;

    /** The votes received. */
    private String votesReceived;

    /** The rival members. */
    private List<RivalMemberVO> rivalMembers;

    //for controlling the labels displayed against sons/daughters
    private Integer sonCount;

    private Integer daughterCount;

    private String votingDate;

    private String electionResultDate;
    
    private String memberRole;
    
    private String ministries;

    /**
     * Gets the educational cul act.
     *
     * @return the educational cul act
     */
    public String getEducationalCulAct() {
		return educationalCulAct;
	}

	/**
	 * Sets the educational cul act.
	 *
	 * @param educationalCulAct the new educational cul act
	 */
	public void setEducationalCulAct(final String educationalCulAct) {
		this.educationalCulAct = educationalCulAct;
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
	 * Gets the mobile.
	 *
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * Sets the mobile.
	 *
	 * @param mobile the new mobile
	 */
	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}
	/**
	 * Gets the website.
	 *
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the website.
	 *
	 * @param website the new website
	 */
	public void setWebsite(final String website) {
		this.website = website;
	}

	/**
	 * Gets the languages known.
	 *
	 * @return the languages known
	 */
	public String getLanguagesKnown() {
		return languagesKnown;
	}

	/**
	 * Sets the languages known.
	 *
	 * @param languagesKnown the new languages known
	 */
	public void setLanguagesKnown(final String languagesKnown) {
		this.languagesKnown = languagesKnown;
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
     * Checks if is enable aliasing.
     *
     * @return true, if is enable aliasing
     */
    public boolean isEnableAliasing() {
        return enableAliasing;
    }

    /**
     * Sets the enable aliasing.
     *
     * @param enableAliasing the new enable aliasing
     */
    public void setEnableAliasing(final boolean enableAliasing) {
        this.enableAliasing = enableAliasing;
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
     * Gets the party name.
     *
     * @return the party name
     */
    public String getPartyName() {
        return partyName;
    }

    /**
     * Sets the party name.
     *
     * @param partyName the new party name
     */
    public void setPartyName(final String partyName) {
        this.partyName = partyName;
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
     * Gets the father name.
     *
     * @return the father name
     */
    public String getFatherName() {
        return fatherName;
    }

    /**
     * Sets the father name.
     *
     * @param fatherName the new father name
     */
    public void setFatherName(final String fatherName) {
        this.fatherName = fatherName;
    }

    /**
     * Gets the mother name.
     *
     * @return the mother name
     */
    public String getMotherName() {
        return motherName;
    }

    /**
     * Sets the mother name.
     *
     * @param motherName the new mother name
     */
    public void setMotherName(final String motherName) {
        this.motherName = motherName;
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
     * Gets the marriage date.
     *
     * @return the marriage date
     */
    public String getMarriageDate() {
        return marriageDate;
    }

    /**
     * Sets the marriage date.
     *
     * @param marriageDate the new marriage date
     */
    public void setMarriageDate(final String marriageDate) {
        this.marriageDate = marriageDate;
    }

    /**
     * Gets the spouse name.
     *
     * @return the spouse name
     */
    public String getSpouseName() {
        return spouseName;
    }

    /**
     * Sets the spouse name.
     *
     * @param spouseName the new spouse name
     */
    public void setSpouseName(final String spouseName) {
        this.spouseName = spouseName;
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
     * Gets the educational qualification.
     *
     * @return the educational qualification
     */
    public String getEducationalQualification() {
        return educationalQualification;
    }

    /**
     * Sets the educational qualification.
     *
     * @param educationalQualification the new educational qualification
     */
    public void setEducationalQualification(final String educationalQualification) {
        this.educationalQualification = educationalQualification;
    }

    /**
     * Gets the profession.
     *
     * @return the profession
     */
    public String getProfession() {
        return profession;
    }

    /**
     * Sets the profession.
     *
     * @param profession the new profession
     */
    public void setProfession(final String profession) {
        this.profession = profession;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(final String email) {
        this.email = email;
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
     * Gets the position details.
     *
     * @return the position details
     */
    public String getPositionDetails() {
        return positionDetails;
    }

    /**
     * Sets the position details.
     *
     * @param positionDetails the new position details
     */
    public void setPositionDetails(final String positionDetails) {
        this.positionDetails = positionDetails;
    }

    /**
     * Gets the socio cultural activities.
     *
     * @return the socio cultural activities
     */
    public String getSocioCulturalActivities() {
        return socioCulturalActivities;
    }

    /**
     * Sets the socio cultural activities.
     *
     * @param socioCulturalActivities the new socio cultural activities
     */
    public void setSocioCulturalActivities(final String socioCulturalActivities) {
        this.socioCulturalActivities = socioCulturalActivities;
    }

    /**
     * Gets the literary artistic sc accomplishment.
     *
     * @return the literary artistic sc accomplishment
     */
    public String getLiteraryArtisticScAccomplishment() {
        return literaryArtisticScAccomplishment;
    }

    /**
     * Sets the literary artistic sc accomplishment.
     *
     * @param literaryArtisticScAccomplishment the new literary artistic sc accomplishment
     */
    public void setLiteraryArtisticScAccomplishment(final String literaryArtisticScAccomplishment) {
        this.literaryArtisticScAccomplishment = literaryArtisticScAccomplishment;
    }

    /**
     * Gets the special interests.
     *
     * @return the special interests
     */
    public String getSpecialInterests() {
        return specialInterests;
    }

    /**
     * Sets the special interests.
     *
     * @param specialInterests the new special interests
     */
    public void setSpecialInterests(final String specialInterests) {
        this.specialInterests = specialInterests;
    }

    /**
     * Gets the pastime recreation.
     *
     * @return the pastime recreation
     */
    public String getPastimeRecreation() {
        return pastimeRecreation;
    }

    /**
     * Sets the pastime recreation.
     *
     * @param pastimeRecreation the new pastime recreation
     */
    public void setPastimeRecreation(final String pastimeRecreation) {
        this.pastimeRecreation = pastimeRecreation;
    }

    /**
     * Gets the sports clubs.
     *
     * @return the sports clubs
     */
    public String getSportsClubs() {
        return sportsClubs;
    }

    /**
     * Sets the sports clubs.
     *
     * @param sportsClubs the new sports clubs
     */
    public void setSportsClubs(final String sportsClubs) {
        this.sportsClubs = sportsClubs;
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
     * Gets the other info.
     *
     * @return the other info
     */
    public String getOtherInfo() {
        return otherInfo;
    }

    /**
     * Sets the other info.
     *
     * @param otherInfo the new other info
     */
    public void setOtherInfo(final String otherInfo) {
        this.otherInfo = otherInfo;
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
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    /**
     * Gets the place of birth.
     *
     * @return the place of birth
     */
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    /**
     * Sets the place of birth.
     *
     * @param placeOfBirth the new place of birth
     */
    public void setPlaceOfBirth(final String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

	/**
	 * Gets the valid votes.
	 *
	 * @return the valid votes
	 */
	public String getValidVotes() {
		return validVotes;
	}

	/**
	 * Sets the valid votes.
	 *
	 * @param validVotes the new valid votes
	 */
	public void setValidVotes(final String validVotes) {
		this.validVotes = validVotes;
	}

	/**
	 * Gets the votes received.
	 *
	 * @return the votes received
	 */
	public String getVotesReceived() {
		return votesReceived;
	}

	/**
	 * Sets the votes received.
	 *
	 * @param votesReceived the new votes received
	 */
	public void setVotesReceived(final String votesReceived) {
		this.votesReceived = votesReceived;
	}

	/**
	 * Gets the rival members.
	 *
	 * @return the rival members
	 */
	public List<RivalMemberVO> getRivalMembers() {
		return rivalMembers;
	}

	/**
	 * Sets the rival members.
	 *
	 * @param rivalMembers the new rival members
	 */
	public void setRivalMembers(final List<RivalMemberVO> rivalMembers) {
		this.rivalMembers = rivalMembers;
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
     * Gets the obituary.
     *
     * @return the obituary
     */
    public String getObituary() {
        return obituary;
    }

    /**
     * Sets the obituary.
     *
     * @param obituary the new obituary
     */
    public void setObituary(final String obituary) {
        this.obituary = obituary;
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
     * Gets the no of voters.
     *
     * @return the no of voters
     */
    public String getNoOfVoters() {
        return noOfVoters;
    }


    /**
     * Sets the no of voters.
     *
     * @param noOfVoters the new no of voters
     */
    public void setNoOfVoters(final String noOfVoters) {
        this.noOfVoters = noOfVoters;
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
     * Gets the correspondence address.
     *
     * @return the correspondence address
     */
    public String getCorrespondenceAddress() {
        return correspondenceAddress;
    }


    /**
     * Sets the correspondence address.
     *
     * @param correspondenceAddress the new correspondence address
     */
    public void setCorrespondenceAddress(final String correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }


    /**
     * Gets the fax12.
     *
     * @return the fax12
     */
    public String getFax12() {
        return fax12;
    }


    /**
     * Sets the fax12.
     *
     * @param fax12 the new fax12
     */
    public void setFax12(final String fax12) {
        this.fax12 = fax12;
    }


    /**
     * Gets the telephone12.
     *
     * @return the telephone12
     */
    public String getTelephone12() {
        return telephone12;
    }


    /**
     * Sets the telephone12.
     *
     * @param telephone12 the new telephone12
     */
    public void setTelephone12(final String telephone12) {
        this.telephone12 = telephone12;
    }


    public Integer getSonCount() {
        return sonCount;
    }


    public void setSonCount(final Integer sonCount) {
        this.sonCount = sonCount;
    }


    public Integer getDaughterCount() {
        return daughterCount;
    }


    public void setDaughterCount(final Integer daughterCount) {
        this.daughterCount = daughterCount;
    }


    public String getVotingDate() {
        return votingDate;
    }


    public void setVotingDate(final String votingDate) {
        this.votingDate = votingDate;
    }


    public String getElectionResultDate() {
        return electionResultDate;
    }


    public void setElectionResultDate(final String electionResultDate) {
        this.electionResultDate = electionResultDate;
    }
    
    public String getMemberRole() {
		return memberRole;
	}

	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}

	public String getMinistries() {
		return ministries;
	}

	public void setMinistries(String ministries) {
		this.ministries = ministries;
	}
	}
