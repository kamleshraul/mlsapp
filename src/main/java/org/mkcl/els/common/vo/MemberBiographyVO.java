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

    /** The marital status. */
    private String maritalStatus;

    /** The marriage date. */
    private String marriageDate;

    /** The spouse name. */
    private String spouseName;

    /** The no of sons. */
    private String noOfSons;

    /** The no of daughter. */
    private String noOfDaughter;

    /** The educational qualification. */
    private String educationalQualification;

    /** The profession. */
    private String profession;

    /** The email. */
    private String email;

    /** The present address. */
    private String presentAddress;

    /** The permanent address. */
    private String permanentAddress;

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
    private String fax;

    /** The mobile. */
    private String mobile;

    /** The telephone. */
    private String telephone;

    /** The office address. */
    private String officeAddress;

    /** The valid votes. */
    private String validVotes;

    /** The votes received. */
    private String votesReceived;

    /** The rival members. */
    private List<RivalMemberVO> rivalMembers;

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
	 * Gets the telephone.
	 *
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * Sets the telephone.
	 *
	 * @param telephone the new telephone
	 */
	public void setTelephone(final String telephone) {
		this.telephone = telephone;
	}

	/**
	 * Gets the fax.
	 *
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * Sets the fax.
	 *
	 * @param fax the new fax
	 */
	public void setFax(final String fax) {
		this.fax = fax;
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

}
