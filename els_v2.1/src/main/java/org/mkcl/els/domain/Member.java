/**
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Member.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberAgeWiseReportVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberChildrenWiseReportVO;
import org.mkcl.els.common.vo.MemberCompleteDetailVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.MemberGeneralVO;
import org.mkcl.els.common.vo.MemberPartyDistrictWiseVO;
import org.mkcl.els.common.vo.MemberPartyWiseReportVO;
import org.mkcl.els.common.vo.MemberProfessionWiseReportVO;
import org.mkcl.els.common.vo.MemberQualificationWiseReportVO;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
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
    "memberPartyAssociations", "memberMinisters", "books",
    "credential", "title", "maritalStatus", "gender", "professions",
    "nationality", "permanentAddress","permanentAddress1","permanentAddress2","presentAddress","presentAddress1","presentAddress2"
    ,"tempAddress1","tempAddress2", "contact",
    "officeAddress","officeAddress1","officeAddress2","correspondenceAddress","houseMemberRoleAssociations"})
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
    @OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "permanentaddress_id")
    protected Address permanentAddress;

    /** The permanent address1. */
    @OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "permanentaddress1_id")
    protected Address permanentAddress1;

    /** The permanent address2. */
    @OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "permanentaddress2_id")
    protected Address permanentAddress2;

    /** The present address. */
    @OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "presentaddress_id")
    protected Address presentAddress;

    /** The present address. */
    @OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "presentaddress1_id")
    protected Address presentAddress1;

    /** The present address. */
    @OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "presentaddress2_id")
    protected Address presentAddress2;


    /** The office address. */
    @OneToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "officeaddress_id")
    private Address officeAddress;

    /** The office address. */
    @OneToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "officeaddress1_id")
    private Address officeAddress1;

    /** The office address. */
    @OneToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "officeaddress2_id")
    private Address officeAddress2;

    /** The temp address1. */
    @OneToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "tempAddress1_id")
    private Address tempAddress1;

    /** The temp address2. */
    @OneToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "tempAddress2_id")
    private Address tempAddress2;

    /** The correspondence address. */
    @OneToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "correspondence_id")
    private Address correspondenceAddress;

    /** The contact. */
    @OneToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "contactdetails_id")
    protected Contact contact;

    // ----------------------------------Other_Informations----------------------------------
    /** The positions held. */
    @ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
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

    /** The publications. */
    @Column(length=30000)
    private String publications;

    // ----------------------------------Election_Results_Informations----------------------------------
    /** The election results. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<ElectionResult> electionResults;

    // ----------------------------------Party_Informations----------------------------------
    /** The member party associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<MemberPartyAssociation> memberPartyAssociations;

    // ----------------------------------House_Role_Informations----------------------------------
    /** The house member role associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<HouseMemberRoleAssociation> houseMemberRoleAssociations;

    // ----------------------------------House_Role_Informations----------------------------------
    /** The member ministers. */
    @OneToMany(mappedBy="member", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<MemberMinister> memberMinisters;

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

    // ----------------------------------Member_Death_Informations----------------------------------

    /** The death date. */
    @Temporal(TemporalType.DATE)
    private Date deathDate;

    /** The condolence date. */
    @Temporal(TemporalType.DATE)
    private Date condolenceDate;

    /** The obituary. */
    @Column(length=30000)
    private String obituary;

    @Column(length=10000)
    private String deathHouseDismissed;

    @Column(length=10000)
    private String deathRemarks;
    // ----------------------------------------------------------------------------------------


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
     * @param house the house
     * @param criteria1 the criteria1
     * @param criteria2 the criteria2
     * @param locale the locale
     * @return the member search page
     * @author sandeep
     * @param councilCriteria
     * @since v1.0.0
     */
    public static MemberSearchPage search(final String housetype, final Long house, final String criteria1,
            final Long criteria2, final String locale, final String[] councilCriteria) {
        return getMemberRepository().search(housetype,house,criteria1,
                criteria2, locale,councilCriteria);
    }


    /**
     * Find biography.
     *
     * @param id the id
     * @param locale the locale
     * @return the member biography vo
     * @author sandeep
     * @param data
     * @since v1.0.0
     */
    public static MemberBiographyVO findBiography(final long id, final String locale, final String[] data) {
        return getMemberRepository().findBiography(id,locale,data);
    }

    /**
     * Find members by age.
     *
     * @param locale the locale
     * @return the member age wise report vo
     * @author sandeep
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
     * @author sandeep
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
     * @author sandeep
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
     * @author sandeep
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
     * @author sandeep
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
     * @author sandeep
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
     * @author sandeep
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
     * @author sandeep
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
     * @author sandeep
     * @since v1.0.0
     */
    public static List<MemberGeneralVO> findMembersByDistrict(final String locale){
        return getMemberRepository().findMembersByDistrict(locale);
    }

    /**
     * Gets the fullname.
     *
     * @return the fullname
     */
    public String getFullname(){
    	StringBuffer sb = new StringBuffer();
    	sb.append(this.getTitle().getName().trim());
    	sb.append(" ");
    	sb.append(this.getFirstName().trim());
    	sb.append(" ");
    	if(this.getMiddleName() != null){
    		if(! this.getMiddleName().equals("")){
    			sb.append(this.getMiddleName().trim());
    	    	sb.append(" ");
    		}
    	}
    	sb.append(this.getLastName().trim());
    	return sb.toString().trim();
    }

    public String getFullnameLastNameFirst(){
        StringBuffer sb = new StringBuffer();
        sb.append(this.getLastName().trim());
        sb.append(", ");
        sb.append(this.getTitle().getName().trim());
        sb.append(" ");
        sb.append(this.getFirstName().trim());
        sb.append(" ");
        if(this.getMiddleName() != null){
            if(! this.getMiddleName().equals("")){
                sb.append(this.getMiddleName().trim());
            }
        }
        return sb.toString().trim();
    }

    /**
     * Gets the complete detail.
     *
     * @param member the member
     * @param locale the locale
     * @return the complete detail
     */
    public static MemberCompleteDetailVO getCompleteDetail(final Long member, final String locale) {
		return getMemberRepository().getCompleteDetail(member,locale);
	}

    /**
     * Find all the active members in the house for the given date.
     * An active member is defined as a user who has a role "MEMBER"
     * on a given date.
     *
     * Returns an empty list if there are no active Members.
     */
    public static List<Member> findActiveMembers(final House house,
            final Date date,
            final String sortOrder,
            final String locale) {
//      List<Member> activeMembers = new ArrayList<Member>();
//
//      MemberRole role = MemberRole.find(house.getType(), "MEMBER", locale);
//      List<HouseMemberRoleAssociation> associations =
//          HouseMemberRoleAssociation.findActiveHouseMemberRoles(house, role, date, locale);
//
//      for(HouseMemberRoleAssociation hmra : associations) {
//          activeMembers.add(hmra.getMember());
//      }
//      return Member.sortByLastname(activeMembers, sortOrder);
        MemberRole role = MemberRole.find(house.getType(), "MEMBER", locale);
        return getMemberRepository().findActiveMembers(house, role, date, sortOrder, locale);
    }

    /**
	 * Sort the Members as per @param sortOrder by lastName. If multiple Members
	 * have same lastName, then break the tie by firstName.
	 *
	 * @param members SHOULD NOT BE NULL
	 *
	 * Does not sort in place, returns a new list.
	 */
    public static List<Member> sortByLastname(final List<Member> members,
    		final String sortOrder) {
    	List<Member> newMList = new ArrayList<Member>();
    	newMList.addAll(members);

    	if(sortOrder.equals(ApplicationConstants.ASC)) {
    		Comparator<Member> c = new Comparator<Member>() {

				@Override
				public int compare(final Member m1, final Member m2) {
					int i = m1.getLastName().compareTo(m2.getLastName());
					if(i == 0) {
						int j = m1.getFirstName().compareTo(m2.getFirstName());
						return j;
					}
					return i;
				}
			};
			Collections.sort(newMList, c);
    	}
    	else if(sortOrder.equals(ApplicationConstants.DESC)) {
    		Comparator<Member> c = new Comparator<Member>() {

				@Override
				public int compare(final Member m1, final Member m2) {
					int i = m2.getLastName().compareTo(m1.getLastName());
					if(i == 0) {
						int j = m2.getFirstName().compareTo(m1.getFirstName());
						return j;
					}
					return i;
				}
			};
			Collections.sort(newMList, c);
    	}

    	return newMList;
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

    /**
     * Gets the member ministers.
     *
     * @return the member ministers
     */
    public List<MemberMinister> getMemberMinisters() {
        return memberMinisters;
    }

    /**
     * Sets the member ministers.
     *
     * @param memberMinisters the new member ministers
     */
    public void setMemberMinisters(final List<MemberMinister> memberMinisters) {
        this.memberMinisters = memberMinisters;
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
    public Date getDeathDate() {
        return deathDate;
    }

    /**
     * Sets the death date.
     *
     * @param deathDate the new death date
     */
    public void setDeathDate(final Date deathDate) {
        this.deathDate = deathDate;
    }

    /**
     * Gets the condolence date.
     *
     * @return the condolence date
     */
    public Date getCondolenceDate() {
        return condolenceDate;
    }

    /**
     * Sets the condolence date.
     *
     * @param condolenceDate the new condolence date
     */
    public void setCondolenceDate(final Date condolenceDate) {
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
	 * Gets the temp address1.
	 *
	 * @return the temp address1
	 */
	public Address getTempAddress1() {
		return tempAddress1;
	}

	/**
	 * Sets the temp address1.
	 *
	 * @param tempAddress1 the new temp address1
	 */
	public void setTempAddress1(final Address tempAddress1) {
		this.tempAddress1 = tempAddress1;
	}

	/**
	 * Gets the temp address2.
	 *
	 * @return the temp address2
	 */
	public Address getTempAddress2() {
		return tempAddress2;
	}

	/**
	 * Sets the temp address2.
	 *
	 * @param tempAddress2 the new temp address2
	 */
	public void setTempAddress2(final Address tempAddress2) {
		this.tempAddress2 = tempAddress2;
	}

	/**
	 * Gets the permanent address1.
	 *
	 * @return the permanent address1
	 */
	public Address getPermanentAddress1() {
		return permanentAddress1;
	}

	/**
	 * Sets the permanent address1.
	 *
	 * @param permanentAddress1 the new permanent address1
	 */
	public void setPermanentAddress1(final Address permanentAddress1) {
		this.permanentAddress1 = permanentAddress1;
	}

	/**
	 * Gets the permanent address2.
	 *
	 * @return the permanent address2
	 */
	public Address getPermanentAddress2() {
		return permanentAddress2;
	}

	/**
	 * Sets the permanent address2.
	 *
	 * @param permanentAddress2 the new permanent address2
	 */
	public void setPermanentAddress2(final Address permanentAddress2) {
		this.permanentAddress2 = permanentAddress2;
	}

	/**
	 * Gets the present address1.
	 *
	 * @return the present address1
	 */
	public Address getPresentAddress1() {
		return presentAddress1;
	}

	/**
	 * Sets the present address1.
	 *
	 * @param presentAddress1 the new present address1
	 */
	public void setPresentAddress1(final Address presentAddress1) {
		this.presentAddress1 = presentAddress1;
	}

	/**
	 * Gets the present address2.
	 *
	 * @return the present address2
	 */
	public Address getPresentAddress2() {
		return presentAddress2;
	}

	/**
	 * Sets the present address2.
	 *
	 * @param presentAddress2 the new present address2
	 */
	public void setPresentAddress2(final Address presentAddress2) {
		this.presentAddress2 = presentAddress2;
	}

	/**
	 * Gets the office address1.
	 *
	 * @return the office address1
	 */
	public Address getOfficeAddress1() {
		return officeAddress1;
	}

	/**
	 * Sets the office address1.
	 *
	 * @param officeAddress1 the new office address1
	 */
	public void setOfficeAddress1(final Address officeAddress1) {
		this.officeAddress1 = officeAddress1;
	}

	/**
	 * Gets the office address2.
	 *
	 * @return the office address2
	 */
	public Address getOfficeAddress2() {
		return officeAddress2;
	}

	/**
	 * Sets the office address2.
	 *
	 * @param officeAddress2 the new office address2
	 */
	public void setOfficeAddress2(final Address officeAddress2) {
		this.officeAddress2 = officeAddress2;
	}

    /**
     * Gets the correspondence address.
     *
     * @return the correspondence address
     */
    public Address getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    /**
     * Sets the correspondence address.
     *
     * @param correspondenceAddress the new correspondence address
     */
    public void setCorrespondenceAddress(final Address correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public static MasterVO findConstituencyByAssemblyId(final Long member,
             final Long house) {
        return getMemberRepository().findConstituencyByAssemblyId(member,
                house);
    }

    public  static MasterVO findConstituencyByCouncilDates(final Long member,final Long house,final String criteria,
            final String fromDate,final String toDate) {
       return getMemberRepository().findConstituencyByCouncilDates(member,house,
               criteria,fromDate,toDate);
   }

    public static MasterVO findPartyByAssemblyId(final Long member,
            final Long house) {
       return getMemberRepository().findPartyByAssemblyId(member,
               house);
   }

   public  static MasterVO findPartyByCouncilDates(final Long member,final Long house,final String criteria,
           final String fromDate,final String toDate) {
      return getMemberRepository().findPartyByCouncilDates(member,house,
              criteria,fromDate,toDate);
  }
public String getDeathHouseDismissed() {
	return deathHouseDismissed;
}
public void setDeathHouseDismissed(final String deathHouseDismissed) {
	this.deathHouseDismissed = deathHouseDismissed;
}
public String getDeathRemarks() {
	return deathRemarks;
}
public void setDeathRemarks(final String deathRemarks) {
	this.deathRemarks = deathRemarks;
}
public static Member findMember(final String firstName, final String middleName,
        final String lastName, final Date birthDate, final String locale) {
    return getMemberRepository().findMember(firstName,middleName,
            lastName,birthDate,locale);
}
public static List<MemberContactVO> getContactDetails(final String[] members) {
	return getMemberRepository().getContactDetails(members);
}

public String findFirstLastName(){
	StringBuffer sb = new StringBuffer();
	if(this.getTitle()!=null){
	sb.append(this.getTitle().getName().trim());
	}
	sb.append(" ");
	sb.append(this.getFirstName().trim());
	sb.append(" ");
	sb.append(this.getLastName().trim());
	return sb.toString().trim();
}

}
