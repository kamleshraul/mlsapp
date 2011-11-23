/*
******************************************************************
File: org.mkcl.els.domain.MemberPersonalDetails.java
Copyright (c) 2011, sandeeps, MKCL
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */

package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberPersonalDetails.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name="member_details")
@JsonIgnoreProperties({"memberPositions","partyName","constituency"})
public class MemberDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;	
	
	/** The photo. */
	@Column(length=50)
	private String photo;
	
	/** The title. */
	@Column(length=5)
	private String title;
	
	/** The first name. */
	@Column(length=100)	
	private String firstName;
	
	/** The middle name. */
	@Column(length=100)
	private String middleName;
	
	/** The last name. */
	@Column(length=100)
	private String lastName;
	
	/** The alias. */
	@Column(length=100)
	private String alias;
	
	/** The enable aliasing. */
	private boolean enableAliasing=false;
	
	/** The gender. */
	@Column(length=6)
	private String gender;
	
	/** The constituency. */
	@ManyToOne(fetch=FetchType.LAZY)
	private Constituency constituency;
	
	/** The party name. */
	@OneToOne(fetch=FetchType.LAZY)
	private Party partyName;
	
	/** The father title. */
	@Column(length=10)
	private String fatherTitle;
	
	/** The father name. */
	@Column(length=200)
	private String fatherName;
	
	/** The mother title. */
	@Column(length=10)
	private String motherTitle;
	
	/** The mother name. */
	@Column(length=200)
	private String motherName;
	
	/** The birth date. */
	@Column(length=50)
	private String birthDate;
	
	/** The place of birth. */
	@Column(length=100)
	private String placeOfBirth;
	
	/** The marital status. */
	private boolean maritalStatus;
	
	/** The marriage date. */
	@Column(length=50)
	private String marriageDate;
	
	/** The spouse name. */
	@Column(length=200)
	private String spouseName;
	
	/** The no of sons. */
	private Integer noOfSons;
	
	/** The no of daughter. */
	private Integer noOfDaughter;
	
	/** The educational qualification. */
	@Column(length=1000)
	private String educationalQualification;
	
	/** The profession. */
	@Column(length=1000)
	private String profession;
	
	/** The email. */
	@Column(length=200)
	private String email;
	
	/** The present address. */
	@Column(length=1000)
	private String presentAddress;
	
	/** The present state. */
	@ManyToOne
	private State presentState;
	
	/** The present district. */
	@ManyToOne
	private District presentDistrict;
	
	/** The present tehsil. */
	@ManyToOne
	private Tehsil presentTehsil;
	
	/** The present city. */
	@Column(length=100)
	private String presentCity;	
	
	/** The present pin code. */
	@Column(length=7)
	private String presentPinCode;
	
	/** The present telephone. */
	@Column(length=500)
	private String presentTelephone;
	
	/** The present fax. */
	@Column(length=500)
	private String presentFax;
	
	/** The present mobile. */
	@Column(length=500)
	private String presentMobile;
	
	/** The address same as above. */
	private boolean addressSameAsAbove=false;
	/** The permanent address. */
	@Column(length=1000)
	private String permanentAddress;
	
	/** The permanent state. */
	@ManyToOne
	private State permanentState;
	
	/** The permanent district. */
	@ManyToOne
	private District permanentDistrict;
	
	/** The permanent tehsil. */
	@ManyToOne
	private Tehsil permanentTehsil;
	
	/** The permanent city. */
	@Column(length=100)
	private String permanentCity;
	
	/** The permanent pin code. */
	@Column(length=7)
	private String permanentPinCode;
	
	/** The permanent telephone. */
	@Column(length=500)
	private String permanentTelephone;
	
	/** The permanent fax. */
	@Column(length=500)
	private String permanentFax;
	
	/** The permanent mobile. */
	@Column(length=500)
	private String permanentMobile;
	
	/** The no of terms. */
	private Integer noOfTerms;
	
	
	/** The member positions. */
	@OneToMany(mappedBy="member",cascade=CascadeType.REMOVE,fetch=FetchType.LAZY)
	private List<MemberPositionsDetails> memberPositions;
	



	/**
	 * Gets the member positions.
	 *
	 * @return the member positions
	 */
	public List<MemberPositionsDetails> getMemberPositions() {
		return memberPositions;
	}

	/**
	 * Sets the member positions.
	 *
	 * @param memberPositions the new member positions
	 */
	public void setMemberPositions(List<MemberPositionsDetails> memberPositions) {
		this.memberPositions = memberPositions;
	}



	/** The socio cultural activities. */
	@Column(length=10000)
	private String socioCulturalActivities;
	
	/** The literary artistic sc accomplishment. */
	@Column(length=10000)
	private String literaryArtisticScAccomplishment;
	
	/** The books published. */
	@Column(length=10000)
	private String booksPublished;
	
	/** The special interests. */
	@Column(length=10000)
	private String specialInterests;
	
	/** The pastime recreation. */
	@Column(length=10000)
	private String pastimeRecreation;
	
	/** The sports clubs. */
	@Column(length=10000)
	private String sportsClubs;
	
	/** The countries visited. */
	@Column(length=10000)
	private String countriesVisited;
	
	/** The experience. */
	@Column(length=10000)
	private String experience;
	
	/** The other info. */
	@Column(length=10000)
	private String otherInfo;
	
	
	/** The version. */
	@Version
	private Long version;

	/** The locale. */
	@Column(length=50)
	private String locale;

	/**
	 * Instantiates a new member personal details.
	 */
	public MemberDetails() {
		super();
	}

	public MemberDetails(String photo, String title, String firstName,
			String middleName, String lastName, String alias,
			boolean enableAliasing, String gender, Constituency constituency,
			Party partyName, String fatherTitle, String fatherName,
			String motherTitle, String motherName, String birthDate,
			String placeOfBirth, boolean maritalStatus, String marriageDate,
			String spouseName, Integer noOfSons, Integer noOfDaughter,
			String educationalQualification, String profession, String email,
			String presentAddress, State presentState,
			District presentDistrict, Tehsil presentTehsil, String presentCity,
			String presentPinCode, String presentTelephone, String presentFax,
			String presentMobile, boolean addressSameAsAbove,
			String permanentAddress, State permanentState,
			District permanentDistrict, Tehsil permanentTehsil,
			String permanentCity, String permanentPinCode,
			String permanentTelephone, String permanentFax,
			String permanentMobile, Integer noOfTerms,
			List<MemberPositionsDetails> memberPositions,
			String socioCulturalActivities,
			String literaryArtisticScAccomplishment, String booksPublished,
			String specialInterests, String pastimeRecreation,
			String sportsClubs, String countriesVisited, String experience,
			String otherInfo, Long version, String locale) {
		super();
		this.photo = photo;
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.alias = alias;
		this.enableAliasing = enableAliasing;
		this.gender = gender;
		this.constituency = constituency;
		this.partyName = partyName;
		this.fatherTitle = fatherTitle;
		this.fatherName = fatherName;
		this.motherTitle = motherTitle;
		this.motherName = motherName;
		this.birthDate = birthDate;
		this.placeOfBirth = placeOfBirth;
		this.maritalStatus = maritalStatus;
		this.marriageDate = marriageDate;
		this.spouseName = spouseName;
		this.noOfSons = noOfSons;
		this.noOfDaughter = noOfDaughter;
		this.educationalQualification = educationalQualification;
		this.profession = profession;
		this.email = email;
		this.presentAddress = presentAddress;
		this.presentState = presentState;
		this.presentDistrict = presentDistrict;
		this.presentTehsil = presentTehsil;
		this.presentCity = presentCity;
		this.presentPinCode = presentPinCode;
		this.presentTelephone = presentTelephone;
		this.presentFax = presentFax;
		this.presentMobile = presentMobile;
		this.addressSameAsAbove = addressSameAsAbove;
		this.permanentAddress = permanentAddress;
		this.permanentState = permanentState;
		this.permanentDistrict = permanentDistrict;
		this.permanentTehsil = permanentTehsil;
		this.permanentCity = permanentCity;
		this.permanentPinCode = permanentPinCode;
		this.permanentTelephone = permanentTelephone;
		this.permanentFax = permanentFax;
		this.permanentMobile = permanentMobile;
		this.noOfTerms = noOfTerms;
		this.memberPositions = memberPositions;
		this.socioCulturalActivities = socioCulturalActivities;
		this.literaryArtisticScAccomplishment = literaryArtisticScAccomplishment;
		this.booksPublished = booksPublished;
		this.specialInterests = specialInterests;
		this.pastimeRecreation = pastimeRecreation;
		this.sportsClubs = sportsClubs;
		this.countriesVisited = countriesVisited;
		this.experience = experience;
		this.otherInfo = otherInfo;
		this.version = version;
		this.locale = locale;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isEnableAliasing() {
		return enableAliasing;
	}

	public void setEnableAliasing(boolean enableAliasing) {
		this.enableAliasing = enableAliasing;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Constituency getConstituency() {
		return constituency;
	}

	public void setConstituency(Constituency constituency) {
		this.constituency = constituency;
	}

	public Party getPartyName() {
		return partyName;
	}

	public void setPartyName(Party partyName) {
		this.partyName = partyName;
	}

	public String getFatherTitle() {
		return fatherTitle;
	}

	public void setFatherTitle(String fatherTitle) {
		this.fatherTitle = fatherTitle;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherTitle() {
		return motherTitle;
	}

	public void setMotherTitle(String motherTitle) {
		this.motherTitle = motherTitle;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public boolean isMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(boolean maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getMarriageDate() {
		return marriageDate;
	}

	public void setMarriageDate(String marriageDate) {
		this.marriageDate = marriageDate;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	public Integer getNoOfSons() {
		return noOfSons;
	}

	public void setNoOfSons(Integer noOfSons) {
		this.noOfSons = noOfSons;
	}

	public Integer getNoOfDaughter() {
		return noOfDaughter;
	}

	public void setNoOfDaughter(Integer noOfDaughter) {
		this.noOfDaughter = noOfDaughter;
	}

	public String getEducationalQualification() {
		return educationalQualification;
	}

	public void setEducationalQualification(String educationalQualification) {
		this.educationalQualification = educationalQualification;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPresentAddress() {
		return presentAddress;
	}

	public void setPresentAddress(String presentAddress) {
		this.presentAddress = presentAddress;
	}

	public State getPresentState() {
		return presentState;
	}

	public void setPresentState(State presentState) {
		this.presentState = presentState;
	}

	public District getPresentDistrict() {
		return presentDistrict;
	}

	public void setPresentDistrict(District presentDistrict) {
		this.presentDistrict = presentDistrict;
	}

	public Tehsil getPresentTehsil() {
		return presentTehsil;
	}

	public void setPresentTehsil(Tehsil presentTehsil) {
		this.presentTehsil = presentTehsil;
	}

	public String getPresentCity() {
		return presentCity;
	}

	public void setPresentCity(String presentCity) {
		this.presentCity = presentCity;
	}

	public String getPresentPinCode() {
		return presentPinCode;
	}

	public void setPresentPinCode(String presentPinCode) {
		this.presentPinCode = presentPinCode;
	}

	public String getPresentTelephone() {
		return presentTelephone;
	}

	public void setPresentTelephone(String presentTelephone) {
		this.presentTelephone = presentTelephone;
	}

	public String getPresentFax() {
		return presentFax;
	}

	public void setPresentFax(String presentFax) {
		this.presentFax = presentFax;
	}

	public String getPresentMobile() {
		return presentMobile;
	}

	public void setPresentMobile(String presentMobile) {
		this.presentMobile = presentMobile;
	}

	public boolean isAddressSameAsAbove() {
		return addressSameAsAbove;
	}

	public void setAddressSameAsAbove(boolean addressSameAsAbove) {
		this.addressSameAsAbove = addressSameAsAbove;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public State getPermanentState() {
		return permanentState;
	}

	public void setPermanentState(State permanentState) {
		this.permanentState = permanentState;
	}

	public District getPermanentDistrict() {
		return permanentDistrict;
	}

	public void setPermanentDistrict(District permanentDistrict) {
		this.permanentDistrict = permanentDistrict;
	}

	public Tehsil getPermanentTehsil() {
		return permanentTehsil;
	}

	public void setPermanentTehsil(Tehsil permanentTehsil) {
		this.permanentTehsil = permanentTehsil;
	}

	public String getPermanentCity() {
		return permanentCity;
	}

	public void setPermanentCity(String permanentCity) {
		this.permanentCity = permanentCity;
	}

	public String getPermanentPinCode() {
		return permanentPinCode;
	}

	public void setPermanentPinCode(String permanentPinCode) {
		this.permanentPinCode = permanentPinCode;
	}

	public String getPermanentTelephone() {
		return permanentTelephone;
	}

	public void setPermanentTelephone(String permanentTelephone) {
		this.permanentTelephone = permanentTelephone;
	}

	public String getPermanentFax() {
		return permanentFax;
	}

	public void setPermanentFax(String permanentFax) {
		this.permanentFax = permanentFax;
	}

	public String getPermanentMobile() {
		return permanentMobile;
	}

	public void setPermanentMobile(String permanentMobile) {
		this.permanentMobile = permanentMobile;
	}

	public Integer getNoOfTerms() {
		return noOfTerms;
	}

	public void setNoOfTerms(Integer noOfTerms) {
		this.noOfTerms = noOfTerms;
	}

	public String getSocioCulturalActivities() {
		return socioCulturalActivities;
	}

	public void setSocioCulturalActivities(String socioCulturalActivities) {
		this.socioCulturalActivities = socioCulturalActivities;
	}

	public String getLiteraryArtisticScAccomplishment() {
		return literaryArtisticScAccomplishment;
	}

	public void setLiteraryArtisticScAccomplishment(
			String literaryArtisticScAccomplishment) {
		this.literaryArtisticScAccomplishment = literaryArtisticScAccomplishment;
	}

	public String getBooksPublished() {
		return booksPublished;
	}

	public void setBooksPublished(String booksPublished) {
		this.booksPublished = booksPublished;
	}

	public String getSpecialInterests() {
		return specialInterests;
	}

	public void setSpecialInterests(String specialInterests) {
		this.specialInterests = specialInterests;
	}

	public String getPastimeRecreation() {
		return pastimeRecreation;
	}

	public void setPastimeRecreation(String pastimeRecreation) {
		this.pastimeRecreation = pastimeRecreation;
	}

	public String getSportsClubs() {
		return sportsClubs;
	}

	public void setSportsClubs(String sportsClubs) {
		this.sportsClubs = sportsClubs;
	}

	public String getCountriesVisited() {
		return countriesVisited;
	}

	public void setCountriesVisited(String countriesVisited) {
		this.countriesVisited = countriesVisited;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
}
