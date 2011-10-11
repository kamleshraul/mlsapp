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

import java.util.Date;
import java.util.List;
import java.util.Set;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberPersonalDetails.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name="member_details")
@JsonIgnoreProperties({"memberPositions"})
public class MemberDetails {

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
	
	@Column(length=6)
	private String gender;
	
	/** The constituency. */
	@ManyToOne
	private Constituency constituency;
	
	/** The party name. */
	@OneToOne
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
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	
	/** The marital status. */
	private boolean maritalStatus;
	
	/** The marriage date. */
	@Temporal(TemporalType.DATE)
	private Date marriageDate;
	
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
	private boolean addressSameAsAbove;
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

	/**
	 * Instantiates a new member details.
	 *
	 * @param photo the photo
	 * @param title the title
	 * @param firstName the first name
	 * @param middleName the middle name
	 * @param lastName the last name
	 * @param constituency the constituency
	 * @param partyName the party name
	 * @param fatherTitle the father title
	 * @param fatherName the father name
	 * @param motherTitle the mother title
	 * @param motherName the mother name
	 * @param birthDate the birth date
	 * @param maritalStatus the marital status
	 * @param marriageDate the marriage date
	 * @param spouseName the spouse name
	 * @param noOfSons the no of sons
	 * @param noOfDaughter the no of daughter
	 * @param educationalQualification the educational qualification
	 * @param profession the profession
	 * @param email the email
	 * @param presentAddress the present address
	 * @param presentState the present state
	 * @param presentDistrict the present district
	 * @param presentTehsil the present tehsil
	 * @param presentCity the present city
	 * @param presentPinCode the present pin code
	 * @param presentTelephone the present telephone
	 * @param presentFax the present fax
	 * @param presentMobile the present mobile
	 * @param addressSameAsAbove the address same as above
	 * @param permanentAddress the permanent address
	 * @param permanentState the permanent state
	 * @param permanentDistrict the permanent district
	 * @param permanentTehsil the permanent tehsil
	 * @param permanentCity the permanent city
	 * @param permanentPinCode the permanent pin code
	 * @param permanentTelephone the permanent telephone
	 * @param permanentFax the permanent fax
	 * @param permanentMobile the permanent mobile
	 * @param noOfTerms the no of terms
	 * @param memberPositions the member positions
	 * @param socioCulturalActivities the socio cultural activities
	 * @param literaryArtisticScAccomplishment the literary artistic sc accomplishment
	 * @param specialInterests the special interests
	 * @param pastimeRecreation the pastime recreation
	 * @param sportsClubs the sports clubs
	 * @param countriesVisited the countries visited
	 * @param experience the experience
	 * @param otherInfo the other info
	 * @param version the version
	 * @param locale the locale
	 */
	public MemberDetails(String photo, String title, String firstName,
			String middleName, String lastName, Constituency constituency,
			Party partyName, String fatherTitle, String fatherName,
			String motherTitle, String motherName, Date birthDate,
			boolean maritalStatus, Date marriageDate, String spouseName,
			Integer noOfSons, Integer noOfDaughter,
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
			String literaryArtisticScAccomplishment, String specialInterests,
			String pastimeRecreation, String sportsClubs,
			String countriesVisited, String experience, String otherInfo,
			Long version, String locale,String gender) {
		super();
		this.photo = photo;
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.gender=gender;
		this.constituency = constituency;
		this.partyName = partyName;
		this.fatherTitle = fatherTitle;
		this.fatherName = fatherName;
		this.motherTitle = motherTitle;
		this.motherName = motherName;
		this.birthDate = birthDate;
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
		this.specialInterests = specialInterests;
		this.pastimeRecreation = pastimeRecreation;
		this.sportsClubs = sportsClubs;
		this.countriesVisited = countriesVisited;
		this.experience = experience;
		this.otherInfo = otherInfo;
		this.version = version;
		this.locale = locale;
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
	public void setId(Long id) {
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
	public void setPhoto(String photo) {
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
	public void setTitle(String title) {
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
	public void setFirstName(String firstName) {
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
	public void setMiddleName(String middleName) {
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
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the constituency.
	 *
	 * @return the constituency
	 */
	public Constituency getConstituency() {
		return constituency;
	}

	/**
	 * Sets the constituency.
	 *
	 * @param constituency the new constituency
	 */
	public void setConstituency(Constituency constituency) {
		this.constituency = constituency;
	}

	/**
	 * Gets the party name.
	 *
	 * @return the party name
	 */
	public Party getPartyName() {
		return partyName;
	}

	/**
	 * Sets the party name.
	 *
	 * @param partyName the new party name
	 */
	public void setPartyName(Party partyName) {
		this.partyName = partyName;
	}

	/**
	 * Gets the father title.
	 *
	 * @return the father title
	 */
	public String getFatherTitle() {
		return fatherTitle;
	}

	/**
	 * Sets the father title.
	 *
	 * @param fatherTitle the new father title
	 */
	public void setFatherTitle(String fatherTitle) {
		this.fatherTitle = fatherTitle;
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
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	/**
	 * Gets the mother title.
	 *
	 * @return the mother title
	 */
	public String getMotherTitle() {
		return motherTitle;
	}

	/**
	 * Sets the mother title.
	 *
	 * @param motherTitle the new mother title
	 */
	public void setMotherTitle(String motherTitle) {
		this.motherTitle = motherTitle;
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
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	/**
	 * Gets the birth date.
	 *
	 * @return the birth date
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * Sets the birth date.
	 *
	 * @param birthDate the new birth date
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Checks if is marital status.
	 *
	 * @return true, if is marital status
	 */
	public boolean isMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * Sets the marital status.
	 *
	 * @param maritalStatus the new marital status
	 */
	public void setMaritalStatus(boolean maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
	 * Gets the marriage date.
	 *
	 * @return the marriage date
	 */
	public Date getMarriageDate() {
		return marriageDate;
	}

	/**
	 * Sets the marriage date.
	 *
	 * @param marriageDate the new marriage date
	 */
	public void setMarriageDate(Date marriageDate) {
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
	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	/**
	 * Gets the no of sons.
	 *
	 * @return the no of sons
	 */
	public Integer getNoOfSons() {
		return noOfSons;
	}

	/**
	 * Sets the no of sons.
	 *
	 * @param noOfSons the new no of sons
	 */
	public void setNoOfSons(Integer noOfSons) {
		this.noOfSons = noOfSons;
	}

	/**
	 * Gets the no of daughter.
	 *
	 * @return the no of daughter
	 */
	public Integer getNoOfDaughter() {
		return noOfDaughter;
	}

	/**
	 * Sets the no of daughter.
	 *
	 * @param noOfDaughter the new no of daughter
	 */
	public void setNoOfDaughter(Integer noOfDaughter) {
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
	public void setEducationalQualification(String educationalQualification) {
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
	public void setProfession(String profession) {
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
	public void setEmail(String email) {
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
	public void setPresentAddress(String presentAddress) {
		this.presentAddress = presentAddress;
	}

	/**
	 * Gets the present state.
	 *
	 * @return the present state
	 */
	public State getPresentState() {
		return presentState;
	}

	/**
	 * Sets the present state.
	 *
	 * @param presentState the new present state
	 */
	public void setPresentState(State presentState) {
		this.presentState = presentState;
	}

	/**
	 * Gets the present district.
	 *
	 * @return the present district
	 */
	public District getPresentDistrict() {
		return presentDistrict;
	}

	/**
	 * Sets the present district.
	 *
	 * @param presentDistrict the new present district
	 */
	public void setPresentDistrict(District presentDistrict) {
		this.presentDistrict = presentDistrict;
	}

	/**
	 * Gets the present tehsil.
	 *
	 * @return the present tehsil
	 */
	public Tehsil getPresentTehsil() {
		return presentTehsil;
	}

	/**
	 * Sets the present tehsil.
	 *
	 * @param presentTehsil the new present tehsil
	 */
	public void setPresentTehsil(Tehsil presentTehsil) {
		this.presentTehsil = presentTehsil;
	}

	/**
	 * Gets the present city.
	 *
	 * @return the present city
	 */
	public String getPresentCity() {
		return presentCity;
	}

	/**
	 * Sets the present city.
	 *
	 * @param presentCity the new present city
	 */
	public void setPresentCity(String presentCity) {
		this.presentCity = presentCity;
	}

	/**
	 * Gets the present pin code.
	 *
	 * @return the present pin code
	 */
	public String getPresentPinCode() {
		return presentPinCode;
	}

	/**
	 * Sets the present pin code.
	 *
	 * @param presentPinCode the new present pin code
	 */
	public void setPresentPinCode(String presentPinCode) {
		this.presentPinCode = presentPinCode;
	}

	/**
	 * Gets the present telephone.
	 *
	 * @return the present telephone
	 */
	public String getPresentTelephone() {
		return presentTelephone;
	}

	/**
	 * Sets the present telephone.
	 *
	 * @param presentTelephone the new present telephone
	 */
	public void setPresentTelephone(String presentTelephone) {
		this.presentTelephone = presentTelephone;
	}

	/**
	 * Gets the present fax.
	 *
	 * @return the present fax
	 */
	public String getPresentFax() {
		return presentFax;
	}

	/**
	 * Sets the present fax.
	 *
	 * @param presentFax the new present fax
	 */
	public void setPresentFax(String presentFax) {
		this.presentFax = presentFax;
	}

	/**
	 * Gets the present mobile.
	 *
	 * @return the present mobile
	 */
	public String getPresentMobile() {
		return presentMobile;
	}

	/**
	 * Sets the present mobile.
	 *
	 * @param presentMobile the new present mobile
	 */
	public void setPresentMobile(String presentMobile) {
		this.presentMobile = presentMobile;
	}

	/**
	 * Checks if is address same as above.
	 *
	 * @return true, if is address same as above
	 */
	public boolean isAddressSameAsAbove() {
		return addressSameAsAbove;
	}

	/**
	 * Sets the address same as above.
	 *
	 * @param addressSameAsAbove the new address same as above
	 */
	public void setAddressSameAsAbove(boolean addressSameAsAbove) {
		this.addressSameAsAbove = addressSameAsAbove;
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
	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	/**
	 * Gets the permanent state.
	 *
	 * @return the permanent state
	 */
	public State getPermanentState() {
		return permanentState;
	}

	/**
	 * Sets the permanent state.
	 *
	 * @param permanentState the new permanent state
	 */
	public void setPermanentState(State permanentState) {
		this.permanentState = permanentState;
	}

	/**
	 * Gets the permanent district.
	 *
	 * @return the permanent district
	 */
	public District getPermanentDistrict() {
		return permanentDistrict;
	}

	/**
	 * Sets the permanent district.
	 *
	 * @param permanentDistrict the new permanent district
	 */
	public void setPermanentDistrict(District permanentDistrict) {
		this.permanentDistrict = permanentDistrict;
	}

	/**
	 * Gets the permanent tehsil.
	 *
	 * @return the permanent tehsil
	 */
	public Tehsil getPermanentTehsil() {
		return permanentTehsil;
	}

	/**
	 * Sets the permanent tehsil.
	 *
	 * @param permanentTehsil the new permanent tehsil
	 */
	public void setPermanentTehsil(Tehsil permanentTehsil) {
		this.permanentTehsil = permanentTehsil;
	}

	/**
	 * Gets the permanent city.
	 *
	 * @return the permanent city
	 */
	public String getPermanentCity() {
		return permanentCity;
	}

	/**
	 * Sets the permanent city.
	 *
	 * @param permanentCity the new permanent city
	 */
	public void setPermanentCity(String permanentCity) {
		this.permanentCity = permanentCity;
	}

	/**
	 * Gets the permanent pin code.
	 *
	 * @return the permanent pin code
	 */
	public String getPermanentPinCode() {
		return permanentPinCode;
	}

	/**
	 * Sets the permanent pin code.
	 *
	 * @param permanentPinCode the new permanent pin code
	 */
	public void setPermanentPinCode(String permanentPinCode) {
		this.permanentPinCode = permanentPinCode;
	}

	/**
	 * Gets the permanent telephone.
	 *
	 * @return the permanent telephone
	 */
	public String getPermanentTelephone() {
		return permanentTelephone;
	}

	/**
	 * Sets the permanent telephone.
	 *
	 * @param permanentTelephone the new permanent telephone
	 */
	public void setPermanentTelephone(String permanentTelephone) {
		this.permanentTelephone = permanentTelephone;
	}

	/**
	 * Gets the permanent fax.
	 *
	 * @return the permanent fax
	 */
	public String getPermanentFax() {
		return permanentFax;
	}

	/**
	 * Sets the permanent fax.
	 *
	 * @param permanentFax the new permanent fax
	 */
	public void setPermanentFax(String permanentFax) {
		this.permanentFax = permanentFax;
	}

	/**
	 * Gets the permanent mobile.
	 *
	 * @return the permanent mobile
	 */
	public String getPermanentMobile() {
		return permanentMobile;
	}

	/**
	 * Sets the permanent mobile.
	 *
	 * @param permanentMobile the new permanent mobile
	 */
	public void setPermanentMobile(String permanentMobile) {
		this.permanentMobile = permanentMobile;
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
	public void setNoOfTerms(Integer noOfTerms) {
		this.noOfTerms = noOfTerms;
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
	public void setSocioCulturalActivities(String socioCulturalActivities) {
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
	public void setLiteraryArtisticScAccomplishment(
			String literaryArtisticScAccomplishment) {
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
	public void setSpecialInterests(String specialInterests) {
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
	public void setPastimeRecreation(String pastimeRecreation) {
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
	public void setSportsClubs(String sportsClubs) {
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
	public void setCountriesVisited(String countriesVisited) {
		this.countriesVisited = countriesVisited;
	}

	/**
	 * Gets the experience.
	 *
	 * @return the experience
	 */
	public String getExperience() {
		return experience;
	}

	/**
	 * Sets the experience.
	 *
	 * @param experience the new experience
	 */
	public void setExperience(String experience) {
		this.experience = experience;
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
	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(Long version) {
		this.version = version;
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
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	
}
