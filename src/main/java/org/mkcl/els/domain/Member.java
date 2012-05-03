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
import org.mkcl.els.common.vo.MemberAgeWiseReportVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberChildrenWiseReportVO;
import org.mkcl.els.common.vo.MemberGeneralVO;
import org.mkcl.els.common.vo.MemberPartyDistrictWiseVO;
import org.mkcl.els.common.vo.MemberPartyWiseReportVO;
import org.mkcl.els.common.vo.MemberProfessionWiseReportVO;
import org.mkcl.els.common.vo.MemberQualificationWiseReportVO;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberDepartmentAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.mkcl.els.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


// TODO: Auto-generated Javadoc
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
    "officeAddress","houseMemberRoleAssociations"/*,"memberMinisterAssociations"*/})
    public class Member extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    // ----------------------------------Personal_Informations------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** **************Personal_Information************************. */
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

    /** ****************Contact Information*************************************. */
    /** The permanent address. */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "permanentaddress_id")
    protected Address permanentAddress;

    /** The present address. */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "presentaddress_id")
    protected Address presentAddress;

    /** The office address. */
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

    /** The educational cultural activities. */
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

    // ----------------------------------Minister_Informations----------------------------------
//    /** The member minister associations. */
//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
//    private List<MemberMinisterAssociation> memberMinisterAssociations;
    // ----------------------------------House_Role_Informations----------------------------------
    /** The house member role associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<HouseMemberRoleAssociation> houseMemberRoleAssociations;

    // ----------------------------------Department_Informations----------------------------------
    /** The member department associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<MemberDepartmentAssociation> memberDepartmentAssociations;

    // ----------------------------------Book_Informations----------------------------------
    /** The books. */
    @ManyToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name = "members_books", joinColumns = @JoinColumn(
            name = "member_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "book_id",
                    referencedColumnName = "id"))
                    private List<Book> books;
 // ----------------------------------Members PA Information---------------------------------
    /** The pa name. */
    @Column(length=900)
    private String paName;

    /** The pa contact no. */
    @Column(length=1500)
    private String paContactNo;

    /** The pa address. */
    @Column(length=3000)
    private String paAddress;
    // ----------------------------------Member_Record_Status_Informations----------------------------------
    /** The credential. */
    //this will denote the publication status of a member record.
    @Column(length=100)
    private String status;

    /** The member repository. */
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
    /**
     * Gets the member repository.
     *
     * @return the member repository
     */
    public static MemberRepository getMemberRepository() {
        MemberRepository memberRepository = new Member().memberRepository;
        if (memberRepository == null) {
            throw new IllegalStateException(
            "MemberRepository has not been injected in Member Domain");
        }
        return memberRepository;
    }

    /**
     * Search.
     *
     * @param housetype the housetype
     * @param criteria1 the criteria1
     * @param criteria2 the criteria2
     * @param locale the locale
     * @return the member search page
     * @author compaq
     * @since v1.0.0
     */
    public static MemberSearchPage search(final String housetype, final String criteria1,
            final Long criteria2, final String locale) {
        return getMemberRepository().search(housetype,criteria1,
                criteria2, locale);
    }


    /**
     * Find biography.
     *
     * @param id the id
     * @param locale the locale
     * @return the member biography vo
     * @author compaq
     * @since v1.0.0
     */
    public static MemberBiographyVO findBiography(final long id, final String locale) {
        return getMemberRepository().findBiography(id,locale);
    }

    /**
     * Find members by age.
     *
     * @param locale the locale
     * @return the member age wise report vo
     * @author compaq
     * @since v1.0.0
     */
    public static MemberAgeWiseReportVO findMembersByAge(final String locale){
        return getMemberRepository().findMembersByAge(locale);
    }

    /**
     * Find members by qualification.
     *
     * @param locale the locale
     * @return the member qualification wise report vo
     * @author compaq
     * @since v1.0.0
     */
    public static MemberQualificationWiseReportVO findMembersByQualification(final String locale){
        return getMemberRepository().findMembersByQualification(locale);
    }

    /**
     * Find members by profession.
     *
     * @param locale the locale
     * @return the member profession wise report vo
     * @author compaq
     * @since v1.0.0
     */
    public static MemberProfessionWiseReportVO findMembersByProfession(final String locale){
        return getMemberRepository().findMembersByProfession(locale);
    }

    /**
     * Find members by children.
     *
     * @param locale the locale
     * @return the member children wise report vo
     * @author compaq
     * @since v1.0.0
     */
    public static MemberChildrenWiseReportVO findMembersByChildren(final String locale){
        return getMemberRepository().findMembersByChildren(locale);
    }

    /**
     * Find members by party.
     *
     * @param locale the locale
     * @return the member party wise report vo
     * @author compaq
     * @since v1.0.0
     */
    public static MemberPartyWiseReportVO findMembersByParty(final String locale){
        return getMemberRepository().findMembersByParty(locale);
    }

    /**
     * Find members by party district.
     *
     * @param locale the locale
     * @return the list
     * @author compaq
     * @since v1.0.0
     */
    public static List<MemberPartyDistrictWiseVO> findMembersByPartyDistrict(final String locale){
        return getMemberRepository().findMembersByPartyDistrict(locale);
    }

    /**
     * Findfemale members.
     *
     * @param locale the locale
     * @return the list
     * @author compaq
     * @since v1.0.0
     */
    public static List<MemberGeneralVO> findfemaleMembers(final String locale){
        return getMemberRepository().findfemaleMembers(locale);
    }

    /**
     * Find members by last name.
     *
     * @param locale the locale
     * @return the list
     * @author compaq
     * @since v1.0.0
     */
    public static List<MemberGeneralVO> findMembersByLastName(final String locale){
        return getMemberRepository().findMembersByLastName(locale);
    }

    /**
     * Find members by district.
     *
     * @param locale the locale
     * @return the list
     * @author compaq
     * @since v1.0.0
     */
    public static List<MemberGeneralVO> findMembersByDistrict(final String locale){
        return getMemberRepository().findMembersByDistrict(locale);
    }
    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the title.
     *
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(final Title title) {
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
    public void setBirthDate(final Date birthDate) {
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
     * Gets the marital status.
     *
     * @return the marital status
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the marital status.
     *
     * @param maritalStatus the new marital status
     */
    public void setMaritalStatus(final MaritalStatus maritalStatus) {
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
    public void setMarriageDate(final Date marriageDate) {
        this.marriageDate = marriageDate;
    }

    /**
     * Gets the gender.
     *
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender the new gender
     */
    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    /**
     * Gets the professions.
     *
     * @return the professions
     */
    public List<Profession> getProfessions() {
        return professions;
    }

    /**
     * Sets the professions.
     *
     * @param professions the new professions
     */
    public void setProfessions(final List<Profession> professions) {
        this.professions = professions;
    }

    /**
     * Gets the nationality.
     *
     * @return the nationality
     */
    public Nationality getNationality() {
        return nationality;
    }

    /**
     * Sets the nationality.
     *
     * @param nationality the new nationality
     */
    public void setNationality(final Nationality nationality) {
        this.nationality = nationality;
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
     * Gets the permanent address.
     *
     * @return the permanent address
     */
    public Address getPermanentAddress() {
        return permanentAddress;
    }

    /**
     * Sets the permanent address.
     *
     * @param permanentAddress the new permanent address
     */
    public void setPermanentAddress(final Address permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    /**
     * Gets the present address.
     *
     * @return the present address
     */
    public Address getPresentAddress() {
        return presentAddress;
    }

    /**
     * Sets the present address.
     *
     * @param presentAddress the new present address
     */
    public void setPresentAddress(final Address presentAddress) {
        this.presentAddress = presentAddress;
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the contact.
     *
     * @param contact the new contact
     */
    public void setContact(final Contact contact) {
        this.contact = contact;
    }

    /**
     * Gets the alias enabled.
     *
     * @return the alias enabled
     */
    public Boolean getAliasEnabled() {
        return aliasEnabled;
    }

    /**
     * Sets the alias enabled.
     *
     * @param aliasEnabled the new alias enabled
     */
    public void setAliasEnabled(final Boolean aliasEnabled) {
        this.aliasEnabled = aliasEnabled;
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
     * Gets the religion.
     *
     * @return the religion
     */
    public Religion getReligion() {
        return religion;
    }

    /**
     * Sets the religion.
     *
     * @param religion the new religion
     */
    public void setReligion(final Religion religion) {
        this.religion = religion;
    }

    /**
     * Gets the languages.
     *
     * @return the languages
     */
    public List<Language> getLanguages() {
        return languages;
    }

    /**
     * Sets the languages.
     *
     * @param languages the new languages
     */
    public void setLanguages(final List<Language> languages) {
        this.languages = languages;
    }

    /**
     * Gets the qualifications.
     *
     * @return the qualifications
     */
    public List<Qualification> getQualifications() {
        return qualifications;
    }

    /**
     * Sets the qualifications.
     *
     * @param qualifications the new qualifications
     */
    public void setQualifications(final List<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    /**
     * Gets the family members.
     *
     * @return the family members
     */
    public List<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    /**
     * Sets the family members.
     *
     * @param familyMembers the new family members
     */
    public void setFamilyMembers(final List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    /**
     * Gets the positions held.
     *
     * @return the positions held
     */
    public List<PositionHeld> getPositionsHeld() {
        return positionsHeld;
    }

    /**
     * Sets the positions held.
     *
     * @param positionsHeld the new positions held
     */
    public void setPositionsHeld(final List<PositionHeld> positionsHeld) {
        this.positionsHeld = positionsHeld;
    }

    /**
     * Gets the reservation.
     *
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Sets the reservation.
     *
     * @param reservation the new reservation
     */
    public void setReservation(final Reservation reservation) {
        this.reservation = reservation;
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
     * Gets the office address.
     *
     * @return the office address
     */
    public Address getOfficeAddress() {
        return officeAddress;
    }

    /**
     * Sets the office address.
     *
     * @param officeAddress the new office address
     */
    public void setOfficeAddress(final Address officeAddress) {
        this.officeAddress = officeAddress;
    }

    /**
     * Gets the social cultural activities.
     *
     * @return the social cultural activities
     */
    public String getSocialCulturalActivities() {
        return socialCulturalActivities;
    }

    /**
     * Sets the social cultural activities.
     *
     * @param socialCulturalActivities the new social cultural activities
     */
    public void setSocialCulturalActivities(final String socialCulturalActivities) {
        this.socialCulturalActivities = socialCulturalActivities;
    }

    /**
     * Gets the literary artistic scientific accomplishments.
     *
     * @return the literary artistic scientific accomplishments
     */
    public String getLiteraryArtisticScientificAccomplishments() {
        return literaryArtisticScientificAccomplishments;
    }

    /**
     * Sets the literary artistic scientific accomplishments.
     *
     * @param literaryArtisticScientificAccomplishments the new literary artistic scientific accomplishments
     */
    public void setLiteraryArtisticScientificAccomplishments(
            final String literaryArtisticScientificAccomplishments) {
        this.literaryArtisticScientificAccomplishments = literaryArtisticScientificAccomplishments;
    }

    /**
     * Gets the hobby special interests.
     *
     * @return the hobby special interests
     */
    public String getHobbySpecialInterests() {
        return hobbySpecialInterests;
    }

    /**
     * Sets the hobby special interests.
     *
     * @param hobbySpecialInterests the new hobby special interests
     */
    public void setHobbySpecialInterests(final String hobbySpecialInterests) {
        this.hobbySpecialInterests = hobbySpecialInterests;
    }

    /**
     * Gets the favorite pastime recreation.
     *
     * @return the favorite pastime recreation
     */
    public String getFavoritePastimeRecreation() {
        return favoritePastimeRecreation;
    }

    /**
     * Sets the favorite pastime recreation.
     *
     * @param favoritePastimeRecreation the new favorite pastime recreation
     */
    public void setFavoritePastimeRecreation(final String favoritePastimeRecreation) {
        this.favoritePastimeRecreation = favoritePastimeRecreation;
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
     * Gets the educational cultural activities.
     *
     * @return the educational cultural activities
     */
    public String getEducationalCulturalActivities() {
        return educationalCulturalActivities;
    }

    /**
     * Sets the educational cultural activities.
     *
     * @param educationalCulturalActivities the new educational cultural activities
     */
    public void setEducationalCulturalActivities(
            final String educationalCulturalActivities) {
        this.educationalCulturalActivities = educationalCulturalActivities;
    }

    /**
     * Gets the election results.
     *
     * @return the election results
     */
    public List<ElectionResult> getElectionResults() {
        return electionResults;
    }

    /**
     * Sets the election results.
     *
     * @param electionResults the new election results
     */
    public void setElectionResults(final List<ElectionResult> electionResults) {
        this.electionResults = electionResults;
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
     * Gets the member department associations.
     *
     * @return the member department associations
     */
    public List<MemberDepartmentAssociation> getMemberDepartmentAssociations() {
		return memberDepartmentAssociations;
	}

	/**
	 * Sets the member department associations.
	 *
	 * @param memberDepartmentAssociations the new member department associations
	 */
	public void setMemberDepartmentAssociations(
			final List<MemberDepartmentAssociation> memberDepartmentAssociations) {
		this.memberDepartmentAssociations = memberDepartmentAssociations;
	}

    /**
     * Gets the books.
     *
     * @return the books
     */
    public List<Book> getBooks() {
        return books;
    }

    /**
     * Sets the books.
     *
     * @param books the new books
     */
    public void setBooks(final List<Book> books) {
        this.books = books;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(final String status) {
        this.status = status;
    }

//	/**
//	 * Gets the member minister associations.
//	 *
//	 * @return the member minister associations
//	 */
//	public List<MemberMinisterAssociation> getMemberMinisterAssociations() {
//		return memberMinisterAssociations;
//	}
//
//	/**
//	 * Sets the member minister associations.
//	 *
//	 * @param memberMinisterAssociations the new member minister associations
//	 */
//	public void setMemberMinisterAssociations(
//			List<MemberMinisterAssociation> memberMinisterAssociations) {
//		this.memberMinisterAssociations = memberMinisterAssociations;
//	}
//
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

}
