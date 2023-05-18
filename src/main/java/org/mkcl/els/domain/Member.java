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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberCompleteDetailVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.MemberDetailsForAccountingVO;
import org.mkcl.els.common.vo.MemberIdentityVO;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.common.vo.MemberMobileVO;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
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
@JsonIgnoreProperties(value={"title","qualifications", "religion", "languages",
	"familyMembers", "positionsHeld", "reservation", "electionResults",
	"memberPartyAssociations", "memberMinisters","memberSupportingMember", "books",
	"credential", "title", "maritalStatus", "gender", "professions",
	"nationality", "permanentAddress","permanentAddress1","permanentAddress2","presentAddress","presentAddress1","presentAddress2"
	,"tempAddress1","tempAddress2", "contact",
	"officeAddress","officeAddress1","officeAddress2","correspondenceAddress","houseMemberRoleAssociations"
	,"deathRemarks","deathHouseDismissed","obituary","condolenceDate","deathDate","status","paAddress",
	"paContactNo","paName","publications","educationalCulturalActivities","otherInformation",
	"countriesVisited","sportsClubs","favoritePastimeRecreation","hobbySpecialInterests","literaryArtisticScientificAccomplishments"
	,"socialCulturalActivities","caste","alias","aliasEnabled","aliasEnglish","specimenSignature","photo",
	"marriageDate","fullnameLastNameFirst","fullname","birthPlace","birthPlaceAddress","locale",
	"version","versionMismatch"},ignoreUnknown=true)
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
	
	/** The first name in english. */
	@Column(length = 100)
	private String firstNameEnglish;

	/** The middle name in english. */
	@Column(length = 100)
	private String middleNameEnglish;

	/** The last name in english. */
	@Column(length = 100)
	private String lastNameEnglish;
	

	/** The birth date. */
	@Temporal(TemporalType.DATE)
	private Date birthDate;

	/** The birth place. */
	@Column(length = 300)
	private String birthPlace;
	
    //---------------------- Shubham Amande edit---------------//
	
	/** The birth Place address. */
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name = "birthplace_address_id")
	protected Address birthPlaceAddress;
	
	
	
	
	//---------------------------------------------------------//

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
	
	/** The alias in english. */
	@Column(length = 200)
	private String aliasEnglish;

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
	
	/** The member ministers. */
	@OneToMany(mappedBy="member", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<MemberSupportingMember> memberSupportingMember;

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
	public static List<MemberInfo> search(final String housetype, final Long house, final String criteria1,
			final Long criteria2, final String locale, final String[] councilCriteria) {
		return getMemberRepository().search(housetype,house,criteria1,
				criteria2, locale,councilCriteria);
	}
	
	public static List<MemberIdentityVO> searchForAccounting(final String housetype, final Long house, final String criteria1,
			final Long criteria2, final String locale, final String[] councilCriteria) {
		return getMemberRepository().searchForAccounting(housetype,house,criteria1,
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
	public static MemberBiographyVO findBiography(final long id, final String locale) {
		return getMemberRepository().findBiography(id,locale);
	}
	
	public static MemberDetailsForAccountingVO findDetailsForAccounting(final String username, final String locale) {
		return getMemberRepository().findDetailsForAccounting(username, locale);
	}
	
	public static Member findByNameBirthDate(final String firstName,final String middleName,
			final String lastName,final Date birthDate,final String locale) throws ELSException {
		return getMemberRepository().findByNameBirthDate(firstName,middleName,lastName,birthDate,locale);
	}
	
	public String getFirstNameAliasLastName(){
		StringBuffer sb = new StringBuffer();
    	if(this.getTitle()!=null){
    		sb.append(this.getTitle().getName().trim());
    		sb.append(" ");
    	}
    	sb.append(this.getFirstName().trim());
    	sb.append(" ");
    	if(this.getAliasEnabled() && this.getAlias().trim().length() > 0){
    		sb.append("(");
    		sb.append(this.getAlias().trim());
    		sb.append(") ");
    	}    	
    	sb.append(this.getLastName().trim());
    	return sb.toString().trim();
	}
	
	/**
	 * Gets the fullname.
	 *
	 * @return the fullname
	 */
	public String getFullname(){
    	StringBuffer sb = new StringBuffer();
    	if(this.getTitle()!=null){
    		sb.append(this.getTitle().getName().trim());
    		sb.append(" ");
    	}
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
		if(this.getTitle()!=null){
		sb.append(this.getTitle().getName().trim());
		sb.append(" ");
		}		
		sb.append(this.getFirstName().trim());
		sb.append(" ");
		if(this.getMiddleName() != null){
			if(! this.getMiddleName().equals("")){
				sb.append(this.getMiddleName().trim());
			}
		}
		return sb.toString().trim();
	}

	public String findFirstLastName(){
		StringBuffer sb = new StringBuffer();
		if(this.getTitle()!=null){
			sb.append(this.getTitle().getName().trim());
		}
		sb.append(" ");
		if(this.getAliasEnabled()!=null && this.getAliasEnabled().equals(true) && this.getAlias()!=null && !this.getAlias().isEmpty()) {			
			sb.append(this.getAlias().trim());
		} else {
			sb.append(this.getFirstName().trim());
			sb.append(" ");
			sb.append(this.getLastName().trim());
		}		
		return sb.toString().trim();
	}
	
	public String findNameInGivenFormat(String nameFormat) {
		StringBuffer sb = new StringBuffer();
		if(nameFormat!=null && !nameFormat.isEmpty()) {
			if(nameFormat.equals(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME)) {
				sb.append(this.findFirstLastName());
			} else if(nameFormat.equals(ApplicationConstants.FORMAT_MEMBERNAME_FULLNAME)) {
				sb.append(this.getFullname());
			} else if(nameFormat.equals(ApplicationConstants.FORMAT_MEMBERNAME_FULLNAMELASTNAMEFIRST)) {
				sb.append(this.getFullnameLastNameFirst());
			}
		}
		return sb.toString();
	}
	
	public String findNameWithConstituencyInGivenFormat(House house, String nameFormat) {
		StringBuffer sb = new StringBuffer();
		if(nameFormat!=null && !nameFormat.isEmpty()) {
			if(nameFormat.equals(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME)) {
				sb.append(this.findFirstLastName());
			} else if(nameFormat.equals(ApplicationConstants.FORMAT_MEMBERNAME_FULLNAME)) {
				sb.append(this.getFullname());
			} else if(nameFormat.equals(ApplicationConstants.FORMAT_MEMBERNAME_FULLNAMELASTNAMEFIRST)) {
				sb.append(this.getFullnameLastNameFirst());
			}
			String constituencyName = this.findConstituencyNameForYadiReport(house, "DATE", new Date(), new Date());
			if(!constituencyName.isEmpty()) {
				sb.append(" (" + constituencyName + ")");			
			}
		}
		return sb.toString();
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
	
	public static Member findMember(final String firstName, final String middleName,
			final String lastName, final Date birthDate, final String locale) {
		return getMemberRepository().findMember(firstName,middleName,
				lastName,birthDate,locale);
	}
	
	public static Member findMember(final String firstName,
			final String lastName, final Date birthDate, final String locale) {
		return getMemberRepository().findMember(firstName, lastName, birthDate, locale);
	}
	
	public static Member findDuplicateMember(final Long existingMemberId, final String firstName, final String middleName,
			final String lastName, final Date birthDate, final String locale) {
		return getMemberRepository().findDuplicateMember(existingMemberId,firstName,middleName,
				lastName,birthDate,locale);
	}
	
	public static Member findDuplicateMember(final Long existingMemberId, final String firstName,
			final String lastName, final Date birthDate, final String locale) {
		return getMemberRepository().findDuplicateMember(existingMemberId,firstName, lastName, birthDate, locale);
	}
	
	public static List<MemberContactVO> getContactDetails(final String[] members) {
		return getMemberRepository().getContactDetails(members);
	}
	
	public Constituency findConstituency() {
		return getMemberRepository().findConstituency(this.getId());
	}

	public static Constituency findConstituency(Member member, Date onDate) {
		return getMemberRepository().findConstituency(member, onDate);
	}
	
	public Party findParty() {
		return getMemberRepository().findParty(this.getId());
	}
	public PartyType findPartyType(final Long memberId,final Long house,
			final String locale) {
		return getMemberRepository().findPartyType(memberId,house,
				locale);
	}
	
	public static List<Member> findByMemberRole(final Long house,final Long memberrole,
			final String locale) {
		return getMemberRepository().findByMemberRole(house,memberrole,
				locale);
	}

	public static List<Member> findActiveMembersByParty(Party party,House house, String locale) {
		return getMemberRepository().findActiveMembersByParty(party,house,locale);
	}
	
	public static List<MasterVO> findActiveMembersByPartyType(final House house,
			final Session session, final String locale,final PartyType partytype, final Long primaryMemberId) {
		return getMemberRepository().findActiveMembersByPartyType(house,session,locale,partytype,primaryMemberId);
	}
	 /**
//   * Find all the active members in the house for the given date
//   * whose name begins with @param nameBeginningWith.
//   * An active member is defined as a user who has a role "MEMBER"
//   * on a given date.
//   *
//   * Returns an empty list if there are no active Members.
//   */
	public static List<Member> findActiveMembers(final House house,
	        final Date date,
	  		final String nameBeginningWith,
	        final String sortOrder,
	        final String locale) {
	      MemberRole role = MemberRole.find(house.getType(), "MEMBER", locale);
	      return getMemberRepository().findActiveMembers(
	      		house, role, date, nameBeginningWith, sortOrder, locale);
	}
	
	/**
	 * Find all the members whose tenure expired between @param fromDate
	 * and @param toDate.
	 */
	public static List<Member> findInactiveMembers(
			final House house,
			final Date fromDate,
			final Date toDate,
			final String locale) {
		MemberRole role = MemberRole.find(house.getType(), "MEMBER", locale);
		 return getMemberRepository().findInactiveMembers(
		      		house, role, fromDate, toDate, locale);
	}
	
	public static List<MasterVO> findAllMembersVOsWithGivenIdsAndWithNameContainingParam(final String memberIds, final String param) {
		return getMemberRepository().findAllMembersVOsWithGivenIdsAndWithNameContainingParam(memberIds, param);
	}
	
	public String findConstituencyNameForYadiReport(final House house, final String criteria, final Date fromDate, final Date toDate) {		
		String constituencyName = "";
		if(house!=null) {			
			if(house.getType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				constituencyName = this.findConstituencyNameForAssemblyYadiReport(house);
			} else if(house.getType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				constituencyName = this.findConstituencyNameForCouncilYadiReport(house, criteria, fromDate, toDate);
			}
		}
		return constituencyName;
	}
	
	@SuppressWarnings("rawtypes")
	public String findConstituencyNameForAssemblyYadiReport(final House house) {
		String constituencyName = "";
		if(house!=null) {
			Map<String, String[]> reportParameters = new HashMap<String, String[]>();
			reportParameters.put("houseId", new String[]{house.getId().toString()});
			reportParameters.put("memberId", new String[]{this.getId().toString()});
			reportParameters.put("locale", new String[]{this.getLocale()});
			List constituencyNames= Query.findReport("YADI_LOWERHOUSE_MEMBER_CONSTITUENCY_NAME", reportParameters);
			if(constituencyNames!=null && !constituencyNames.isEmpty()) {
				if(constituencyNames.get(0)!=null) {
					constituencyName = constituencyNames.get(0).toString();
				}					
			}
		}		
		return constituencyName;
	}
	
	@SuppressWarnings("rawtypes")
	public String findConstituencyNameForCouncilYadiReport(final House house, final String criteria, final Date fromDate, final Date toDate) {
		String constituencyName = "";
		if(house!=null) {
			Map<String, String[]> reportParameters = new HashMap<String, String[]>();
			reportParameters.put("houseId", new String[]{house.getId().toString()});
			reportParameters.put("memberId", new String[]{this.getId().toString()});
			reportParameters.put("locale", new String[]{this.getLocale()});
			if(criteria!=null && !criteria.isEmpty()) {
				reportParameters.put("criteria", new String[]{criteria});
				if(fromDate!=null) {
					String fromDateDBFormat=FormaterUtil.formatDateToString(fromDate, ApplicationConstants.DB_DATEFORMAT);
					reportParameters.put("fromDateDBFormat", new String[]{fromDateDBFormat});
				} else {
					reportParameters.put("fromDateDBFormat", new String[]{""});
				}
				if(toDate!=null) {
					String toDateDBFormat=FormaterUtil.formatDateToString(toDate, ApplicationConstants.DB_DATEFORMAT);
					reportParameters.put("toDateDBFormat", new String[]{toDateDBFormat});
				} else {
					reportParameters.put("toDateDBFormat", new String[]{""});
				}
			} else {
				reportParameters.put("criteria", new String[]{""});				
			}			
			List constituencyNames= Query.findReport("YADI_UPPERHOUSE_MEMBER_CONSTITUENCY_NAME", reportParameters);
			if(constituencyNames!=null && !constituencyNames.isEmpty()) {
				if(constituencyNames.get(0)!=null) {
					constituencyName = constituencyNames.get(0).toString();
				}					
			}
		}		
		return constituencyName;
	}
	
	public boolean isActiveMemberOn(final Date date,final String locale){
		return getMemberRepository().isActiveMemberOn(this,date,locale);
	}
	
	public boolean isActiveMemberInAnyOfGivenRolesOn(final String[] memberRoles, final Date date, final String locale){
		return getMemberRepository().isActiveMemberInAnyOfGivenRolesOn(this, memberRoles, date, locale);
	}
	
	public boolean isActiveMinisterOn(final Date date,final String locale){
		return getMemberRepository().isActiveMinisterOn(this,date,locale);
	}
	
	public boolean isActiveOnlyAsMember(final Date onDate, final String locale) {
		String[] memberPresidingOfficerRoles = new String[] {"SPEAKER", "DEPUTY_SPEAKER", "CHAIRMAN", "DEPUTY_CHAIRMAN"};
		
		boolean isActiveMinister = this.isActiveMinisterOn(onDate, locale);
		boolean isActivePresidingOfficer = this.isActiveMemberInAnyOfGivenRolesOn(memberPresidingOfficerRoles, onDate, locale);
		boolean isActiveMember = this.isActiveMemberOn(onDate, locale);
		
		if(isActiveMember &&
				! isActiveMinister &&
				! isActivePresidingOfficer) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPresentInMemberBallotAttendanceUH(final Session session,final DeviceType deviceType,final String locale){
		return getMemberRepository().isPresentInMemberBallotAttendanceUH(session,deviceType,this,locale);
	}
	
	public boolean isSupportingOrClubbedMemberToBeAddedForDevice(Device device) {
		boolean isSupportingOrClubbedMemberToBeAddedForDevice = false;
//		HouseType houseType = null;
//		DeviceType deviceType = null;
		Session session = null;		
		if(device!=null) {
			if(device instanceof Question) {
				Question question = (Question) device;
//				houseType = question.getHouseType();
//				deviceType = question.getType();
				session = question.getSession();
				/** parameter for date on which to check if member was/is active as per session **/
				Date activeOnCheckDate = null;
				Date currentDate = new Date();
				if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					activeOnCheckDate = currentDate;
				} else {
					if(currentDate.compareTo(session.getStartDate())<=0) {
						activeOnCheckDate = session.getStartDate();
					} else if(currentDate.compareTo(session.getStartDate())>0
							&& currentDate.compareTo(session.getEndDate())<0) {
						activeOnCheckDate = currentDate;
					} else {
						activeOnCheckDate = session.getEndDate();
					}
				}				
				/** parameter for date on which to check if member was/is active at the submission time of device **/
				Date activeOnCheckDateAtSubmission = question.getSubmissionDate();
				
				String locale=question.getLocale();
//				if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE) 
//						&& deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
//						if(!this.isPresentInMemberBallotAttendanceUH(session,deviceType,question.getLocale())
//							&& this.isActiveMemberOn(currentDate, question.getLocale())
//							&& !this.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","), new Date(), question.getLocale())
//							&& !this.isActiveMinisterOn(currentDate, question.getLocale())
//							&& question.containsClubbingFromSecondBatch(question.getSession(),this,question.getLocale())
//							){
//							isSupportingOrClubbedMemberToBeAddedForDevice = true;
//						}else if(!this.isPresentInMemberBallotAttendanceUH(session,deviceType,question.getLocale())
//								&& this.isActiveMemberOn(currentDate, question.getLocale())
//								&& !this.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","), new Date(), question.getLocale())
//								&& !this.isActiveMinisterOn(currentDate, question.getLocale())
//								&& !question.containsClubbingFromSecondBatch(question.getSession(),this,question.getLocale())
//								){
//								isSupportingOrClubbedMemberToBeAddedForDevice = false;
//						}else if(this.isActiveMemberOn(currentDate, question.getLocale())
//								&& !this.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","), new Date(), question.getLocale())
//								&& !this.isActiveMinisterOn(currentDate, question.getLocale())){
//							isSupportingOrClubbedMemberToBeAddedForDevice = true;
//						}
						
						MemberRole memberRole=MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
						HouseMemberRoleAssociation hmra=Member.find(this,memberRole,activeOnCheckDate,locale);
						HouseMemberRoleAssociation hmraAtSubmission=Member.find(this,memberRole,activeOnCheckDateAtSubmission,locale);						
						if(hmra!=null && hmraAtSubmission!=null){
							boolean isMemberAllowed=isMemberAllowed(hmraAtSubmission,question);
							if(isMemberAllowed){
								boolean isActivePresidingOfficer=this.isActiveMemberInAnyOfGivenRolesOn(
										ApplicationConstants.NON_MEMBER_ROLES.split(","),activeOnCheckDate, locale);
								if(!isActivePresidingOfficer){
									boolean isMinister=this.isActiveMinisterOn(activeOnCheckDate, locale);
									if(!isMinister){
										isSupportingOrClubbedMemberToBeAddedForDevice=true;
									}
								}
							}
						}
						
//				} 
			}else if(device instanceof StandaloneMotion) {
				StandaloneMotion question = (StandaloneMotion) device;
				session = question.getSession();
				/** parameter for date on which to check if member was/is active as per session **/
				Date activeOnCheckDate = null;
				Date currentDate = new Date();
				if(currentDate.compareTo(session.getStartDate())<=0) {
					activeOnCheckDate = session.getStartDate();
				} else if(currentDate.compareTo(session.getStartDate())>0
						&& currentDate.compareTo(session.getEndDate())<0) {
					activeOnCheckDate = new Date();
				} else {
					activeOnCheckDate = session.getEndDate();
				}
				String locale = question.getLocale();
				MemberRole memberRole = MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
				HouseMemberRoleAssociation hmra = Member.find(this,memberRole,activeOnCheckDate,locale);
				if(hmra!=null){
					boolean isMemberAllowed = isMemberAllowed(hmra,question);
					if(isMemberAllowed){
						boolean isActivePresidingOfficer = this.isActiveMemberInAnyOfGivenRolesOn(
								ApplicationConstants.NON_MEMBER_ROLES.split(","),activeOnCheckDate, locale);
						if(!isActivePresidingOfficer){
							boolean isMinister=this.isActiveMinisterOn(activeOnCheckDate, locale);
							if(!isMinister){
								isSupportingOrClubbedMemberToBeAddedForDevice=true;
							}
						}
					}
				}
			}else if(device instanceof Motion) {
				Motion question = (Motion) device;
				session = question.getSession();
				/** parameter for date on which to check if member was/is active as per session **/
				Date activeOnCheckDate = null;
				Date currentDate = new Date();
				if(currentDate.compareTo(session.getStartDate())<=0) {
					activeOnCheckDate = session.getStartDate();
				} else if(currentDate.compareTo(session.getStartDate())>0
						&& currentDate.compareTo(session.getEndDate())<0) {
					activeOnCheckDate = new Date();
				} else {
					activeOnCheckDate = session.getEndDate();
				}
				String locale = question.getLocale();
				MemberRole memberRole = MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
				HouseMemberRoleAssociation hmra = Member.find(this,memberRole,activeOnCheckDate,locale);
				if(hmra!=null){
					boolean isMemberAllowed = isMemberAllowed(hmra,question);
					if(isMemberAllowed){
						boolean isActivePresidingOfficer = this.isActiveMemberInAnyOfGivenRolesOn(
								ApplicationConstants.NON_MEMBER_ROLES.split(","),activeOnCheckDate, locale);
						if(!isActivePresidingOfficer){
							boolean isMinister=this.isActiveMinisterOn(activeOnCheckDate, locale);
							if(!isMinister){
								isSupportingOrClubbedMemberToBeAddedForDevice=true;
							}
						}
					}
				}
			}else if(device instanceof CutMotion) {
				CutMotion question = (CutMotion) device;
				session = question.getSession();
				/** parameter for date on which to check if member was/is active as per session **/
				Date activeOnCheckDate = null;
				Date currentDate = new Date();
				if(currentDate.compareTo(session.getStartDate())<=0) {
					activeOnCheckDate = session.getStartDate();
				} else if(currentDate.compareTo(session.getStartDate())>0
						&& currentDate.compareTo(session.getEndDate())<0) {
					activeOnCheckDate = new Date();
				} else {
					activeOnCheckDate = session.getEndDate();
				}
				String locale = question.getLocale();
				MemberRole memberRole = MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
				HouseMemberRoleAssociation hmra = Member.find(this,memberRole,activeOnCheckDate,locale);
				if(hmra!=null){
					boolean isMemberAllowed = isMemberAllowed(hmra,question);
					if(isMemberAllowed){
						boolean isActivePresidingOfficer = this.isActiveMemberInAnyOfGivenRolesOn(
								ApplicationConstants.NON_MEMBER_ROLES.split(","),activeOnCheckDate, locale);
						if(!isActivePresidingOfficer){
							boolean isMinister=this.isActiveMinisterOn(activeOnCheckDate, locale);
							if(!isMinister){
								isSupportingOrClubbedMemberToBeAddedForDevice=true;
							}
						}
					}
				}
			}else if(device instanceof AdjournmentMotion) {
				AdjournmentMotion question = (AdjournmentMotion) device;
				session = question.getSession();
				/** parameter for date on which to check if member was/is active as per session **/
				Date activeOnCheckDate = null;
				Date currentDate = new Date();
				if(currentDate.compareTo(session.getStartDate())<=0) {
					activeOnCheckDate = session.getStartDate();
				} else if(currentDate.compareTo(session.getStartDate())>0
						&& currentDate.compareTo(session.getEndDate())<0) {
					activeOnCheckDate = new Date();
				} else {
					activeOnCheckDate = session.getEndDate();
				}
				String locale = question.getLocale();
				MemberRole memberRole = MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
				HouseMemberRoleAssociation hmra = Member.find(this,memberRole,activeOnCheckDate,locale);
				if(hmra!=null){
					boolean isMemberAllowed = isMemberAllowed(hmra,question);
					if(isMemberAllowed){
						boolean isActivePresidingOfficer = this.isActiveMemberInAnyOfGivenRolesOn(
								ApplicationConstants.NON_MEMBER_ROLES.split(","),activeOnCheckDate, locale);
						if(!isActivePresidingOfficer){
							boolean isMinister=this.isActiveMinisterOn(activeOnCheckDate, locale);
							if(!isMinister){
								isSupportingOrClubbedMemberToBeAddedForDevice=true;
							}
						}
					}
				}
			}else if(device instanceof ProprietyPoint) {
				ProprietyPoint question = (ProprietyPoint) device;
				session = question.getSession();
				/** parameter for date on which to check if member was/is active as per session **/
				Date activeOnCheckDate = null;
				Date currentDate = new Date();
				if(currentDate.compareTo(session.getStartDate())<=0) {
					activeOnCheckDate = session.getStartDate();
				} else if(currentDate.compareTo(session.getStartDate())>0
						&& currentDate.compareTo(session.getEndDate())<0) {
					activeOnCheckDate = new Date();
				} else {
					activeOnCheckDate = session.getEndDate();
				}
				String locale = question.getLocale();
				MemberRole memberRole = MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
				HouseMemberRoleAssociation hmra = Member.find(this,memberRole,activeOnCheckDate,locale);
				if(hmra!=null){
					boolean isMemberAllowed = isMemberAllowed(hmra,question);
					if(isMemberAllowed){
						boolean isActivePresidingOfficer = this.isActiveMemberInAnyOfGivenRolesOn(
								ApplicationConstants.NON_MEMBER_ROLES.split(","),activeOnCheckDate, locale);
						if(!isActivePresidingOfficer){
							boolean isMinister=this.isActiveMinisterOn(activeOnCheckDate, locale);
							if(!isMinister){
								isSupportingOrClubbedMemberToBeAddedForDevice=true;
							}
						}
					}
				}
			}
		}
		return isSupportingOrClubbedMemberToBeAddedForDevice;
	}
	
	private Boolean isMemberAllowed(HouseMemberRoleAssociation hmra, Device device){
		if(hmra!=null && device!=null){
			if(device instanceof Question){
				Question q = (Question) device;
				if(hmra.getFromDate().before(q.getSubmissionDate())
						&& hmra.getToDate().after(q.getSubmissionDate())){
					return true;
				}
			}else if(device instanceof Motion){
				Motion q = (Motion) device;
				if(hmra.getFromDate().before(q.getSubmissionDate())
						&& hmra.getToDate().after(q.getSubmissionDate())){
					return true;
				}
			}else if(device instanceof StandaloneMotion){
				StandaloneMotion q = (StandaloneMotion) device;
				if(hmra.getFromDate().before(q.getSubmissionDate())
						&& hmra.getToDate().after(q.getSubmissionDate())){
					return true;
				}
			}else if(device instanceof CutMotion){
				CutMotion q = (CutMotion) device;
				if(hmra.getFromDate().before(q.getSubmissionDate())
						&& hmra.getToDate().after(q.getSubmissionDate())){
					return true;
				}
			}else if(device instanceof AdjournmentMotion){
				AdjournmentMotion q = (AdjournmentMotion) device;
				if(hmra.getFromDate().before(q.getSubmissionDate())
						&& hmra.getToDate().after(q.getSubmissionDate())){
					return true;
				}
			}else if(device instanceof ProprietyPoint){
				ProprietyPoint q = (ProprietyPoint) device;
				if(hmra.getFromDate().before(q.getSubmissionDate())
						&& hmra.getToDate().after(q.getSubmissionDate())){
					return true;
				}
			}
		}
		return false;
	}
	
	private Boolean isMemberAllowed(HouseMemberRoleAssociation hmra, StandaloneMotion motion){
		if(hmra!=null && motion!=null){
			if(hmra.getFromDate().before(motion.getSubmissionDate())
				&& hmra.getToDate().after(motion.getSubmissionDate())){
				return true;
			}
		}
		return false;
	}
	
	public static HouseMemberRoleAssociation find(Member member,
			MemberRole memberRole, Date date, String locale) {
		return getMemberRepository().find(member,memberRole,date,locale);
	}
	
	//For Grav website 
	public static MemberBiographyVO findBiographyForGrav(final long id,final String strHouseType, final String locale) {
		return getMemberRepository().findBiographyForGrav(id,strHouseType,locale);
	}
		
	//For Mobile Application
	public static MemberBiographyVO findBiographyForMobileApp(final long id,final String strHouseType, final String locale) {
		return getMemberRepository().findBiographyForMobileApp(id,strHouseType,locale);
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
	 * Gets the first name in english.
	 *
	 * @return the first name in english
	 */
	public String getFirstNameEnglish() {
		return firstNameEnglish;
	}

	/**
	 * Sets the first name in english.
	 *
	 * @param firstNameEnglish the new first name in english
	 */
	public void setFirstNameEnglish(final String firstNameEnglish) {
		this.firstNameEnglish = firstNameEnglish;
	}

	/**
	 * Gets the middle name in english.
	 *
	 * @return the middle name in english
	 */
	public String getMiddleNameEnglish() {
		return middleNameEnglish;
	}

	/**
	 * Sets the middle name in english.
	 *
	 * @param middleNameEnglish the new middle name in english
	 */
	public void setMiddleNameEnglish(final String middleNameEnglish) {
		this.middleNameEnglish = middleNameEnglish;
	}

	/**
	 * Gets the last name in english.
	 *
	 * @return the last name in english
	 */
	public String getLastNameEnglish() {
		return lastNameEnglish;
	}

	/**
	 * Sets the last name in english.
	 *
	 * @param lastNameEnglish the new last name in english
	 */
	public void setLastNameEnglish(final String lastNameEnglish) {
		this.lastNameEnglish = lastNameEnglish;
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
	 * Gets the birth place address.
	 *
	 * @return the birth place address
	 */
	public Address getBirthPlaceAddress() {
		return birthPlaceAddress;
	}

	/**
	 * Sets the birth place address.
	 *
	 * @param birthPlaceAddress the new birth place address
	 */
	public void setBirthPlaceAddress(final Address birthPlaceAddress) {
		this.birthPlaceAddress = birthPlaceAddress;
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
	 * Gets the alias in english.
	 *
	 * @return the alias in english
	 */
	public String getAliasEnglish() {
		return aliasEnglish;
	}

	/**
	 * Sets the alias in english.
	 *
	 * @param alias in english the new alias in english
	 */
	public void setAliasEnglish(final String aliasEnglish) {
		this.aliasEnglish = aliasEnglish;
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
	public void setMemberMinister(final List<MemberMinister> memberMinister) {
		this.memberMinisters = memberMinister;
	}
	
	public List<MemberSupportingMember> getMemberSupportingMember() {
		return memberSupportingMember;
	}
	
	public void setMemberSupportingMember(List<MemberSupportingMember> memberSupportingMember) {
		this.memberSupportingMember = memberSupportingMember;
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
	
	public static List<Member> findActiveMinisters(final Date onDate,
			final String locale) {
		List<Member> members = getMemberRepository().findActiveMinisters(onDate, locale);
		
		// A member can be assigned multiple ministries, hence members may contain
		// duplicate names. Remove the duplicates.
		Map<Long, Member> map = new HashMap<Long, Member>();
		for(Member m : members) {
			Long id = m.getId();
			if(map.get(id) == null) {
				map.put(id, m);
			}
		}
		
		List<Member> uniqueMembers = new ArrayList<Member>();
		Set<Long> keys = map.keySet();
		for(Long k : keys) {
			Member m = map.get(k);
			uniqueMembers.add(m);
		}
		
		return uniqueMembers;
	}
	public String findCurrentHouseType() {
				return getMemberRepository().findHouseTypeByDate(this.getId(),new Date());
	}
	
	public static List<Member> findMembersWithHousetype(final String houseType,
			final String locale) {
		List<Member> members = getMemberRepository().findMembersWithHousetype(houseType, locale);
		
		
		return members;
	}
	
	public static List<Member> findMembersWithHousetype(final String houseType, final String sortBy, final String sortOrder, 
			final String locale) {
		List<Member> members = getMemberRepository().findMembersWithHousetype(houseType, sortBy, sortOrder, locale);
		
		
		return members;
	}
	
	public static List<Member> findMembersWithconstituency(final String houseType,final Long constituency			) {
		List<Member> members = getMemberRepository().findMembersWithConstituency(houseType,constituency);
		
		
		return members;
	}
	
	public static List<String> findMemberByHouseDates(final Long houseType,String fromDate,String toDate,String locale){
		List<String> members=getMemberRepository().findMembersByHouseDates(houseType,fromDate,toDate,locale);
		return members;
	}
	public boolean isSuspendedMember() {
		return getMemberRepository().isMemberSuspendedOnDate(this.getId(),new Date());	
	}
	
	
	/**
	 * For Mobile Api.
	 *
	 * @param id the id
	 * @param locale the locale
	 * @return the member Mobile  vo
	 * @author Shubham A
	 * @param data
	 * @since v1.0.0
	 */
	
	public static MemberMobileVO getMemberDataForMobileVo(final long id, final String locale) {
		return getMemberRepository().getMemberDataForMobileVo(id,locale);
		
	}
	
}