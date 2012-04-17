/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Member.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberDepartmentAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.mkcl.els.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Member.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="members")
@JsonIgnoreProperties({ "qualifications", "religion", "languages",
        "familyMembers", "positionsHeld", "reservation", "electionResults",
        "memberPartyAssociations", "memberDepartmentAssociations", "books",
        "credential", "title", "maritalStatus", "gender", "professions",
        "nationality", "permanentAddress", "presentAddress", "contact",
        "officeAddress","houseMemberRoleAssociations"})
public class Member extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    // ----------------------------------Personal_Informations------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /****************Personal_Information*************************/
    /** The title. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="title_id")
    private Title title;

    /** The first name. */
    @Column(length = 300)
    private String firstName;

    /** The middle name. */
    @Column(length = 300)
    private String middleName;

    /** The last name. */
    @Column(length = 300)
    private String lastName;

    /** The birth date. */
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    /** The birth place. */
    @Column(length = 300)
    private String birthPlace;

    /** The marital status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "maritalstatus_id")
    private MaritalStatus maritalStatus;

    /** The marriage date. */
    @Temporal(TemporalType.DATE)
    private Date marriageDate;

    /** The gender. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private Gender gender;

    /** The profession. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "members_professions",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "profession_id",
                    referencedColumnName = "id") })
    private List<Profession> professions;

    /** The nationality. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "nationality_id")
    private Nationality nationality;

    /** The photo. */
    @Column(length = 100)
    private String photo;

    /** The specimen signature. */
    @Column(length = 100)
    private String specimenSignature;

    /** The alias enabled. */
    private Boolean aliasEnabled;

    /** The alias. */
    @Column(length = 300)
    private String alias;

    /** The religion. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "religion_id")
    private Religion religion;

    /** The languages. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "members_languages",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "language_id",
                    referencedColumnName = "id") })
    private List<Language> languages;

    /** The degrees. */
    @ManyToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name = "members_qualifications",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "qualification_id",
                    referencedColumnName = "id") })
    private List<Qualification> qualifications;

    /** The family members. */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "members_familymembers",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "familymember_id",
                    referencedColumnName = "id") })
    private List<FamilyMember> familyMembers;

    /** The reservation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    /** The caste. */
    @Column(length = 600)
    private String caste;

    /******************Contact Information**************************************/
    /** The permanent address. */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "permanentaddress_id")
    protected Address permanentAddress;

    /** The present address. */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "presentaddress_id")
    protected Address presentAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "officeaddress_id")
    private Address officeAddress;

    /** The contact. */
    @OneToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "contactdetails_id")
    protected Contact contact;

    // ----------------------------------Other_Informations----------------------------------
    /** The positions held. */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "members_positionsheld",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "positionheld_id",
                    referencedColumnName = "id") })
    private List<PositionHeld> positionsHeld;

    /** The social cultural activities. */
    @Column(length = 30000)
    private String socialCulturalActivities;

    /** The literary artistic scientific accomplishments. */
    @Column(length = 30000)
    private String literaryArtisticScientificAccomplishments;

    /** The hobby special interests. */
    @Column(length = 30000)
    private String hobbySpecialInterests;

    /** The favorite pastime recreation. */
    @Column(length = 30000)
    private String favoritePastimeRecreation;

    /** The sports clubs. */
    @Column(length = 30000)
    private String sportsClubs;

    /** The countries visited. */
    @Column(length = 30000)
    private String countriesVisited;

    /** The other information. */
    @Column(length = 30000)
    private String otherInformation;

    @Column(length = 30000)
    private String educationalCulturalActivities;

    // ----------------------------------Election_Results_Informations----------------------------------
    /** The election results. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<ElectionResult> electionResults;

    // ----------------------------------Party_Informations----------------------------------
    /** The member party associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<MemberPartyAssociation> memberPartyAssociations;

    // ----------------------------------Ministry_Informations----------------------------------
    /** The member ministry associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<MemberDepartmentAssociation> memberDepartmentAssociations;

    // ----------------------------------House_Role_Informations----------------------------------
    /** The house member role associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<HouseMemberRoleAssociation> houseMemberRoleAssociations;

    // ----------------------------------Book_Informations----------------------------------
    /** The books. */
    @ManyToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name = "members_books", joinColumns = @JoinColumn(
            name = "member_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "book_id",
                    referencedColumnName = "id"))
    private List<Book> books;

    // ----------------------------------Member_Record_Status_Informations----------------------------------
    /** The credential. */
    //this will denote the publication status of a member record.
    @Column(length=100)
    private String status;

    @Autowired
    private transient MemberRepository memberRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new member.
     */
    public Member() {
        super();
        this.aliasEnabled = false;
    }
    // -------------------------------Domain_Methods----------------------------------------------
    public static MemberRepository getMemberRepository() {
        MemberRepository memberRepository = new Member().memberRepository;
        if (memberRepository == null) {
            throw new IllegalStateException(
                    "MemberRepository has not been injected in Member Domain");
        }
        return memberRepository;
    }
//    public static Integer maxNoOfTerms(final String housetype, final String locale) {
//		return getMemberRepository().maxNoOfTerms(housetype, locale);
//	}

    public static MemberSearchPage search(final String housetype, final String criteria1,
            final Long criteria2, final String locale) {
        return getMemberRepository().search(housetype,criteria1,
                criteria2, locale);
    }
    public static MemberBiographyVO findBiography(final long id, final String locale) {
        return getMemberRepository().findBiography(id,locale);
    }

    // ------------------------------------------Getters/Setters-----------------------------------
	public Title getTitle() {
		return title;
	}

	public void setTitle(final Title title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(final String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(final MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Date getMarriageDate() {
		return marriageDate;
	}

	public void setMarriageDate(final Date marriageDate) {
		this.marriageDate = marriageDate;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(final Gender gender) {
		this.gender = gender;
	}

	public List<Profession> getProfessions() {
		return professions;
	}

	public void setProfessions(final List<Profession> professions) {
		this.professions = professions;
	}

	public Nationality getNationality() {
		return nationality;
	}

	public void setNationality(final Nationality nationality) {
		this.nationality = nationality;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(final String photo) {
		this.photo = photo;
	}

	public String getSpecimenSignature() {
		return specimenSignature;
	}

	public void setSpecimenSignature(final String specimenSignature) {
		this.specimenSignature = specimenSignature;
	}

	public Address getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(final Address permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public Address getPresentAddress() {
		return presentAddress;
	}

	public void setPresentAddress(final Address presentAddress) {
		this.presentAddress = presentAddress;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(final Contact contact) {
		this.contact = contact;
	}

	public Boolean getAliasEnabled() {
		return aliasEnabled;
	}

	public void setAliasEnabled(final Boolean aliasEnabled) {
		this.aliasEnabled = aliasEnabled;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public Religion getReligion() {
		return religion;
	}

	public void setReligion(final Religion religion) {
		this.religion = religion;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(final List<Language> languages) {
		this.languages = languages;
	}

	public List<Qualification> getQualifications() {
		return qualifications;
	}

	public void setQualifications(final List<Qualification> qualifications) {
		this.qualifications = qualifications;
	}

	public List<FamilyMember> getFamilyMembers() {
		return familyMembers;
	}

	public void setFamilyMembers(final List<FamilyMember> familyMembers) {
		this.familyMembers = familyMembers;
	}

	public List<PositionHeld> getPositionsHeld() {
		return positionsHeld;
	}

	public void setPositionsHeld(final List<PositionHeld> positionsHeld) {
		this.positionsHeld = positionsHeld;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(final Reservation reservation) {
		this.reservation = reservation;
	}

	public String getCaste() {
		return caste;
	}

	public void setCaste(final String caste) {
		this.caste = caste;
	}

	public Address getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(final Address officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getSocialCulturalActivities() {
		return socialCulturalActivities;
	}

	public void setSocialCulturalActivities(final String socialCulturalActivities) {
		this.socialCulturalActivities = socialCulturalActivities;
	}

	public String getLiteraryArtisticScientificAccomplishments() {
		return literaryArtisticScientificAccomplishments;
	}

	public void setLiteraryArtisticScientificAccomplishments(
			final String literaryArtisticScientificAccomplishments) {
		this.literaryArtisticScientificAccomplishments = literaryArtisticScientificAccomplishments;
	}

	public String getHobbySpecialInterests() {
		return hobbySpecialInterests;
	}

	public void setHobbySpecialInterests(final String hobbySpecialInterests) {
		this.hobbySpecialInterests = hobbySpecialInterests;
	}

	public String getFavoritePastimeRecreation() {
		return favoritePastimeRecreation;
	}

	public void setFavoritePastimeRecreation(final String favoritePastimeRecreation) {
		this.favoritePastimeRecreation = favoritePastimeRecreation;
	}

	public String getSportsClubs() {
		return sportsClubs;
	}

	public void setSportsClubs(final String sportsClubs) {
		this.sportsClubs = sportsClubs;
	}

	public String getCountriesVisited() {
		return countriesVisited;
	}

	public void setCountriesVisited(final String countriesVisited) {
		this.countriesVisited = countriesVisited;
	}

	public String getOtherInformation() {
		return otherInformation;
	}

	public void setOtherInformation(final String otherInformation) {
		this.otherInformation = otherInformation;
	}

	public String getEducationalCulturalActivities() {
		return educationalCulturalActivities;
	}

	public void setEducationalCulturalActivities(
			final String educationalCulturalActivities) {
		this.educationalCulturalActivities = educationalCulturalActivities;
	}

	public List<ElectionResult> getElectionResults() {
		return electionResults;
	}

	public void setElectionResults(final List<ElectionResult> electionResults) {
		this.electionResults = electionResults;
	}

	public List<MemberPartyAssociation> getMemberPartyAssociations() {
		return memberPartyAssociations;
	}

	public void setMemberPartyAssociations(
			final List<MemberPartyAssociation> memberPartyAssociations) {
		this.memberPartyAssociations = memberPartyAssociations;
	}

    public List<MemberDepartmentAssociation> getMemberDepartmentAssociations() {
        return memberDepartmentAssociations;
    }

    public void setMemberDepartmentAssociations(
            final List<MemberDepartmentAssociation> memberDepartmentAssociations) {
        this.memberDepartmentAssociations = memberDepartmentAssociations;
    }
    public List<HouseMemberRoleAssociation> getHouseMemberRoleAssociations() {
		return houseMemberRoleAssociations;
	}

	public void setHouseMemberRoleAssociations(
			final List<HouseMemberRoleAssociation> houseMemberRoleAssociations) {
		this.houseMemberRoleAssociations = houseMemberRoleAssociations;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(final List<Book> books) {
		this.books = books;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

}
