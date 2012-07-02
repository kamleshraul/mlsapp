package org.mkcl.els.common.vo;

import java.util.List;

import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.PositionHeld;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;

public class MemberCompleteDetailVO {
    
	//Personal Details
	private String photo;
	
	private String partyFlag;
	
	private String party;
	
	private String constituency;
	
	private String specimenSignature;
	
	private String title;
	
	private String firstName;
	
	private String middleName;
	
	private String lastName;
	
	private String alias;
	
	private String birthDate;
	
	private String birthPlace;

	private String nationality;

	private String gender;

	private String qualification;

	private String religion;

	private String category;

	private String caste;

	private String maritalStatus;

	private String spouse;
	
	private String spouseRelation;

	private String noOfDaughter;

	private String noOfSons;
	
	private String noOfChildren;
	
	private String languages;

	private String professions;

	private String deathDate;

	private String condolenceDate;

	private String paName;

	private String paContactNo;

	private String paAddress;
	
	//Contact Details
	
    private String presentAddress;
    
    private String presentAddress1;
    
    private String presentAddress2;

    private String permanentAddress;

    private String permanentAddress1;
    
    private String permanentAddress2;
    
    private String officeAddress;
    
    private String officeAddress1;
    
    private String officeAddress2;
    
    private String tempAddress1;

    private String tempAddress2;
    
    private String fax1;

    private String fax2;

    private String fax3;

    private String fax4;

    private String fax5;

    private String fax6;

    private String fax7;

    private String fax8;

    private String fax9;

    private String fax10;
    
    private String fax11;

    private String telephone1;

    private String telephone2;

    private String telephone3;

    private String telephone4;

    private String telephone5;

    private String telephone6;

    private String telephone7;

    private String telephone8;

    private String telephone9;

    private String telephone10;
    
    private String telephone11;
    
    private String website1;

    private String website2;
    
    private String mobile1;
    
    private String mobile2;
    
    private String email1;
    
    private String email2;
	
	//Other Details
	private List<PositionHeldVO> positionsHeld;
	
	private String otherInformation;
	
	private String countriesVisited;
	
	private String publications;
	
	private String specialInterest;
	
	//House Details
    private List<HouseMemberRoleAssociation> houseMemberRoleAssociations;
	
	//Party Details
    private List<MemberPartyAssociation> memberPartyAssociations;
	
	//Election Details
    private List<ElectionResultVO> electionResults;	

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPartyFlag() {
		return partyFlag;
	}

	public void setPartyFlag(String partyFlag) {
		this.partyFlag = partyFlag;
	}

	public String getSpecimenSignature() {
		return specimenSignature;
	}

	public void setSpecimenSignature(String specimenSignature) {
		this.specimenSignature = specimenSignature;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCaste() {
		return caste;
	}

	public void setCaste(String caste) {
		this.caste = caste;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getSpouse() {
		return spouse;
	}

	public void setSpouse(String spouse) {
		this.spouse = spouse;
	}

	public String getNoOfDaughter() {
		return noOfDaughter;
	}

	public void setNoOfDaughter(String noOfDaughter) {
		this.noOfDaughter = noOfDaughter;
	}

	public String getNoOfSons() {
		return noOfSons;
	}

	public void setNoOfSons(String noOfSons) {
		this.noOfSons = noOfSons;
	}
	
	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public String getProfessions() {
		return professions;
	}

	public void setProfessions(String professions) {
		this.professions = professions;
	}

	public String getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(String deathDate) {
		this.deathDate = deathDate;
	}

	public String getCondolenceDate() {
		return condolenceDate;
	}

	public void setCondolenceDate(String condolenceDate) {
		this.condolenceDate = condolenceDate;
	}

	public String getPaName() {
		return paName;
	}

	public void setPaName(String paName) {
		this.paName = paName;
	}

	public String getPaContactNo() {
		return paContactNo;
	}

	public void setPaContactNo(String paContactNo) {
		this.paContactNo = paContactNo;
	}

	public String getPaAddress() {
		return paAddress;
	}

	public void setPaAddress(String paAddress) {
		this.paAddress = paAddress;
	}

	public String getPresentAddress() {
		return presentAddress;
	}

	public void setPresentAddress(String presentAddress) {
		this.presentAddress = presentAddress;
	}

	public String getPresentAddress1() {
		return presentAddress1;
	}

	public void setPresentAddress1(String presentAddress1) {
		this.presentAddress1 = presentAddress1;
	}

	public String getPresentAddress2() {
		return presentAddress2;
	}

	public void setPresentAddress2(String presentAddress2) {
		this.presentAddress2 = presentAddress2;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public String getPermanentAddress1() {
		return permanentAddress1;
	}

	public void setPermanentAddress1(String permanentAddress1) {
		this.permanentAddress1 = permanentAddress1;
	}

	public String getPermanentAddress2() {
		return permanentAddress2;
	}

	public void setPermanentAddress2(String permanentAddress2) {
		this.permanentAddress2 = permanentAddress2;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getOfficeAddress1() {
		return officeAddress1;
	}

	public void setOfficeAddress1(String officeAddress1) {
		this.officeAddress1 = officeAddress1;
	}

	public String getOfficeAddress2() {
		return officeAddress2;
	}

	public void setOfficeAddress2(String officeAddress2) {
		this.officeAddress2 = officeAddress2;
	}

	public String getTempAddress1() {
		return tempAddress1;
	}

	public void setTempAddress1(String tempAddress1) {
		this.tempAddress1 = tempAddress1;
	}

	public String getTempAddress2() {
		return tempAddress2;
	}

	public void setTempAddress2(String tempAddress2) {
		this.tempAddress2 = tempAddress2;
	}

	public String getFax1() {
		return fax1;
	}

	public void setFax1(String fax1) {
		this.fax1 = fax1;
	}

	public String getFax2() {
		return fax2;
	}

	public void setFax2(String fax2) {
		this.fax2 = fax2;
	}

	public String getFax3() {
		return fax3;
	}

	public void setFax3(String fax3) {
		this.fax3 = fax3;
	}

	public String getFax4() {
		return fax4;
	}

	public void setFax4(String fax4) {
		this.fax4 = fax4;
	}

	public String getFax5() {
		return fax5;
	}

	public void setFax5(String fax5) {
		this.fax5 = fax5;
	}

	public String getFax6() {
		return fax6;
	}

	public void setFax6(String fax6) {
		this.fax6 = fax6;
	}

	public String getFax7() {
		return fax7;
	}

	public void setFax7(String fax7) {
		this.fax7 = fax7;
	}

	public String getFax8() {
		return fax8;
	}

	public void setFax8(String fax8) {
		this.fax8 = fax8;
	}

	public String getFax9() {
		return fax9;
	}

	public void setFax9(String fax9) {
		this.fax9 = fax9;
	}

	public String getFax10() {
		return fax10;
	}

	public void setFax10(String fax10) {
		this.fax10 = fax10;
	}

	public String getFax11() {
		return fax11;
	}

	public void setFax11(String fax11) {
		this.fax11 = fax11;
	}

	public String getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	public String getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	public String getTelephone3() {
		return telephone3;
	}

	public void setTelephone3(String telephone3) {
		this.telephone3 = telephone3;
	}

	public String getTelephone4() {
		return telephone4;
	}

	public void setTelephone4(String telephone4) {
		this.telephone4 = telephone4;
	}

	public String getTelephone5() {
		return telephone5;
	}

	public void setTelephone5(String telephone5) {
		this.telephone5 = telephone5;
	}

	public String getTelephone6() {
		return telephone6;
	}

	public void setTelephone6(String telephone6) {
		this.telephone6 = telephone6;
	}

	public String getTelephone7() {
		return telephone7;
	}

	public void setTelephone7(String telephone7) {
		this.telephone7 = telephone7;
	}

	public String getTelephone8() {
		return telephone8;
	}

	public void setTelephone8(String telephone8) {
		this.telephone8 = telephone8;
	}

	public String getTelephone9() {
		return telephone9;
	}

	public void setTelephone9(String telephone9) {
		this.telephone9 = telephone9;
	}

	public String getTelephone10() {
		return telephone10;
	}

	public void setTelephone10(String telephone10) {
		this.telephone10 = telephone10;
	}

	public String getTelephone11() {
		return telephone11;
	}

	public void setTelephone11(String telephone11) {
		this.telephone11 = telephone11;
	}

	public String getWebsite1() {
		return website1;
	}

	public void setWebsite1(String website1) {
		this.website1 = website1;
	}

	public String getWebsite2() {
		return website2;
	}

	public void setWebsite2(String website2) {
		this.website2 = website2;
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}	
	
	public List<PositionHeldVO> getPositionsHeld() {
		return positionsHeld;
	}

	public void setPositionsHeld(List<PositionHeldVO> positionsHeld) {
		this.positionsHeld = positionsHeld;
	}

	public String getOtherInformation() {
		return otherInformation;
	}

	public void setOtherInformation(String otherInformation) {
		this.otherInformation = otherInformation;
	}

	public String getCountriesVisited() {
		return countriesVisited;
	}

	public void setCountriesVisited(String countriesVisited) {
		this.countriesVisited = countriesVisited;
	}

	public String getPublications() {
		return publications;
	}

	public void setPublications(String publications) {
		this.publications = publications;
	}

	public List<HouseMemberRoleAssociation> getHouseMemberRoleAssociations() {
		return houseMemberRoleAssociations;
	}

	public void setHouseMemberRoleAssociations(
			List<HouseMemberRoleAssociation> houseMemberRoleAssociations) {
		this.houseMemberRoleAssociations = houseMemberRoleAssociations;
	}

	public List<MemberPartyAssociation> getMemberPartyAssociations() {
		return memberPartyAssociations;
	}

	public void setMemberPartyAssociations(
			List<MemberPartyAssociation> memberPartyAssociations) {
		this.memberPartyAssociations = memberPartyAssociations;
	}

	
	public List<ElectionResultVO> getElectionResults() {
		return electionResults;
	}

	public void setElectionResults(List<ElectionResultVO> electionResults) {
		this.electionResults = electionResults;
	}

	public String getParty() {
		return party;
	}

	public void setParty(String party) {
		this.party = party;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getConstituency() {
		return constituency;
	}

	public void setConstituency(String constituency) {
		this.constituency = constituency;
	}

	public String getNoOfChildren() {
		return noOfChildren;
	}

	public void setNoOfChildren(String noOfChildren) {
		this.noOfChildren = noOfChildren;
	}

	public String getSpouseRelation() {
		return spouseRelation;
	}

	public void setSpouseRelation(String spouseRelation) {
		this.spouseRelation = spouseRelation;
	}

	public String getSpecialInterest() {
		return specialInterest;
	}

	public void setSpecialInterest(String specialInterest) {
		this.specialInterest = specialInterest;
	}	
	
	
}
