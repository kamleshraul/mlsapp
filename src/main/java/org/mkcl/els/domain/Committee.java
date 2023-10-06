package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.CommitteeCompositeVO;
import org.mkcl.els.common.vo.CommitteeMemberVO;
import org.mkcl.els.common.vo.CommitteeVO;
import org.mkcl.els.common.vo.PartyVO;
import org.mkcl.els.repository.CommitteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committees")
@JsonIgnoreProperties({"members", "invitedMembers", "status", 
	"internalStatusPAMLH", "recommendationStatusPAMLH", "remarksPAMLH",
	"internalStatusPAMUH", "recommendationStatusPAMUH", "remarksPAMUH",
	"internalStatusLOPLH", "recommendationStatusLOPLH", "remarksLOPLH",
	"internalStatusLOPUH", "recommendationStatusLOPUH", "remarksLOPUH",
	"internalStatusIMLH", "recommendationStatusIMLH", "remarksIMLH",
	"internalStatusIMUH", "recommendationStatusIMUH", "remarksIMUH",
	"creationDate", "createdBy", "editedOn", "editedAs", "editedBy", "drafts"})
public class Committee extends BaseDomain implements Serializable {
	
	private static final long serialVersionUID = 5384539811329848614L;
	
	//=============== ATTRIBUTES ===============
	/* Core Attributes */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committee_name_id")
	private CommitteeName committeeName;
	
	@Temporal(TemporalType.DATE)
	private Date formationDate;
	
	@Temporal(TemporalType.DATE)
	private Date dissolutionDate;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committees_committee_members",
			joinColumns={@JoinColumn(name="committee_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_member_id",
					referencedColumnName="id")})
	private List<CommitteeMember> members;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committees_invited_members",
			joinColumns={@JoinColumn(name="committee_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_invited_member_id",
					referencedColumnName="id")})
	private List<CommitteeMember> invitedMembers;
	
	/* Work flow Attributes */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;
	
	// "REQUEST TO PARLIAMENTARY AFFAIRS MINISTER FOR ADDITION
	// OF MEMBERS TO COMITTEE" work flow attributes 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_pam_lh_id")
	private Status internalStatusPAMLH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_pam_lh_id")
	private Status recommendationStatusPAMLH;
	
	@Column(name="remarks_pam_lh", length=30000)
	private String remarksPAMLH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_pam_uh_id")
	private Status internalStatusPAMUH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_pam_uh_id")
	private Status recommendationStatusPAMUH;
	
	@Column(name="remarks_pam_uh", length=30000)
	private String remarksPAMUH;
	
	// "REQUEST TO LEADER OF OPPOSITION FOR ADDITION
	// OF MEMBERS TO COMITTEE" work flow attributes
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_lop_lh_id")
	private Status internalStatusLOPLH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_lop_lh_id")
	private Status recommendationStatusLOPLH;
	
	@Column(name="remarks_lop_lh", length=30000)
	private String remarksLOPLH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_lop_uh_id")
	private Status internalStatusLOPUH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_lop_uh_id")
	private Status recommendationStatusLOPUH;
	
	@Column(name="remarks_lop_uh", length=30000)
	private String remarksLOPUH;
	
	// "ADDITION OF INVITED MEMBERS TO COMMITTEES"
	// work flow attributes
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_im_lh_id")
	private Status internalStatusIMLH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_im_lh_id")
	private Status recommendationStatusIMLH;
	
	@Column(name="remarks_im_lh", length=30000)
	private String remarksIMLH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_im_uh_id")
	private Status internalStatusIMUH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_im_uh_id")
	private Status recommendationStatusIMUH;
	
	@Column(name="remarks_im_uh", length=30000)
	private String remarksIMUH;
	
	/* Audit Log */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(length=1000)
	private String createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date editedOn;
	
	@Column(length=1000)
	private String editedAs;
	
	@Column(length=1000)
	private String editedBy;
	
	/* Drafts */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name="committees_drafts_association", 
    		joinColumns={@JoinColumn(name="committee_id", 
    				referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="committee_draft_id", 
    				referencedColumnName="id")})
	private List<CommitteeDraft> drafts;
	
	@Autowired
	private transient CommitteeRepository repository;
	
	//=============== CONSTRUCTORS =============
	/**
	 * Not to be used. Kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
	public Committee() {
		super();
		this.setMembers(new ArrayList<CommitteeMember>());
		this.setInvitedMembers(new ArrayList<CommitteeMember>());
		this.setDrafts(new ArrayList<CommitteeDraft>());
	}
	
	public Committee(final CommitteeName committeeName,
			final Date formationDate,
			final Date dissolutionDate,
			final String locale) {
		super(locale);
		this.setCommitteeName(committeeName);
		this.setFormationDate(formationDate);
		this.setDissolutionDate(dissolutionDate);
		this.setMembers(new ArrayList<CommitteeMember>());
		this.setInvitedMembers(new ArrayList<CommitteeMember>());
		this.setDrafts(new ArrayList<CommitteeDraft>());
	}
	
	//=============== VIEW METHODS =============
	// TODO: Optimize this method vis-a-vis DB traffic generated
	public static CommitteeCompositeVO findCommitteeVOs(
			final List<Committee> committees,
			final HouseType houseType,
			final PartyType partyType,
			final String locale) {
		CommitteeCompositeVO compositeVO = new CommitteeCompositeVO();
		
		List<CommitteeVO> committeeVOs = new ArrayList<CommitteeVO>();
		for(Committee c : committees) {
			CommitteeVO committeeVO = 
				Committee.createCommitteeVO(c, houseType, partyType, locale);
			committeeVOs.add(committeeVO);
		}
		compositeVO.setCommitteeVOs(committeeVOs);
		
		Committee.addPartiesToCommitteeCompositeVO(compositeVO, 
				houseType, locale);
		
		return compositeVO;
	}
	
	// TODO: Optimize this method vis-a-vis DB traffic generated
	public static CommitteeCompositeVO findCommitteeVOs(
			final List<Committee> committees, 
			final HouseType houseType, 
			final String locale) {
		CommitteeCompositeVO compositeVO = new CommitteeCompositeVO();
		
		List<CommitteeVO> committeeVOs = new ArrayList<CommitteeVO>();
		for(Committee c : committees) {
			CommitteeVO committeeVO = 
				Committee.createCommitteeVO(c, houseType, locale);
			committeeVOs.add(committeeVO);
		}
		compositeVO.setCommitteeVOs(committeeVOs);
		
		return compositeVO;
	}

	//=============== DOMAIN METHODS ===========
	public static Committee find(final CommitteeName committeeName,
			final Date formationDate, 
			final String locale) {
		return Committee.getRepository().find(committeeName, 
				formationDate, locale);
	}

	/**
	 * On any given date, only one Committee instance is active
	 * for the given @param committeeName. Besides a Committee
	 * is said to be active on a given date only if 
	 * 1) the given date lies between committee's formationDate 
	 * & dissolutionDate.
	 * 2) committee's status >= "COMMITTEE_MEMBERS_ADDED"
	 */
	public static Committee findActiveCommittee(
			final CommitteeName committeeName,
			final Date onDate, 
			final String locale) {
		Status status = Status.findByType(
				ApplicationConstants.COMMITTEE_MEMBERS_ADDED, locale);
		return Committee.getRepository().findActiveCommittee(committeeName, 
				status, onDate, locale);
	}
	
	/**
	 * A Committee is said to be active on a given date only if 
	 * 1) the given date lies between committee's formationDate 
	 * & dissolutionDate.
	 * 2) committee's status >= "COMMITTEE_MEMBERS_ADDED"
	 */
	final public static List<Committee> findActiveCommittees(
			final Date onDate, 
			final String locale) {
		Status status = Status.findByType(
				ApplicationConstants.COMMITTEE_MEMBERS_ADDED, locale);
		return Committee.getRepository().findActiveCommittees(status, 
				onDate, locale);
	}
	
	/**
	 * If @param isIncludeBothHouseType is true then search for 
	 * all active Committees (status >= "COMMITTEE_MEMBERS_ADDED") 
	 * and houseType is either @param houseType OR BOTHHOUSE.
	 * 
	 * If @param isIncludeBothHouseType is false then search for 
	 * all active Committees (status >= "COMMITTEE_MEMBERS_ADDED") 
	 * and houseType is @param houseType.
	 */
	public static List<Committee> findActiveCommittees(
			final HouseType houseType,
			final Boolean isIncludeBothHouseType,
			final Date onDate, 
			final String locale) {
		Status status = Status.findByType(
				ApplicationConstants.COMMITTEE_MEMBERS_ADDED, locale);
		return Committee.getRepository().findActiveCommittees(houseType, 
				status, isIncludeBothHouseType,	onDate, locale);
	}
	
	/**
	 * If @param isIncludeBothHouseType is true then search for 
	 * all Committees with status == "COMMITTEE_CREATED" and 
	 * houseType is either @param houseType OR BOTHHOUSE.
	 * 
	 * If @param isIncludeBothHouseType is false then search for 
	 * all Committees with status == "COMMITTEE_CREATED" and 
	 * houseType is @param houseType.
	 */
	public static List<Committee> findCommitteesToBeProcessed(
			final HouseType houseType,
			final PartyType partyType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		return Committee.getRepository().findCommitteesToBeProcessed(
				houseType, partyType, isIncludeBothHouseType, locale);
	}
	
	public static List<Committee> findCommitteesForInvitedMembersToBeAdded(
			final HouseType houseType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		return Committee.getRepository().
			findCommitteesForInvitedMembersToBeAdded(houseType, 
					isIncludeBothHouseType, locale);
	}
	
	public static List<Committee> findByCommitteeNames(
			final String[] committeeNames, 
			final HouseType houseType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		HouseType[] houseTypes = new HouseType[]{houseType};
		if(isIncludeBothHouseType) {
			HouseType bothHouseType = 
				HouseType.findByType(ApplicationConstants.BOTH_HOUSE, locale);
			
			houseTypes = new HouseType[]{houseType, bothHouseType};
		}
		return Committee.getRepository().findByCommitteeNames(committeeNames,
				houseTypes, locale);
	}
	
	public static Date dissolutionDate(final CommitteeName committeeName,
			final Date formationDate,
			final String locale) {
		Date dissolutionDate = null;
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(formationDate);
		if(committeeName.getDurationInYears() != null) {
			Integer year = calendar.get(Calendar.YEAR);
			Integer durationInYears = committeeName.getDurationInYears();
			calendar.set(Calendar.YEAR, year + durationInYears);
			dissolutionDate = calendar.getTime();
		}
		else if(committeeName.getDurationInMonths() != null) {
			Integer month = calendar.get(Calendar.MONTH);
			Integer durationInMonths = committeeName.getDurationInMonths();
			calendar.set(Calendar.MONTH, month + durationInMonths);
			dissolutionDate = calendar.getTime();
		}
		else if(committeeName.getDurationInDays() != null) {
			Integer date = calendar.get(Calendar.DATE);
			Integer durationInDays = committeeName.getDurationInDays();
			calendar.set(Calendar.DATE, date + durationInDays);
			dissolutionDate = calendar.getTime();
		}
		
		return dissolutionDate;
	}
	
	@Override
	public BaseDomain persist() {
		Status CREATED = Status.findByType(
				ApplicationConstants.COMMITTEE_CREATED, this.getLocale());
		this.setStatus(CREATED);
		return super.persist();
	}
	
	@Override
	public BaseDomain merge() {
		// Set the status attribute based on internal & recommendation
		// status as per the different workflows
		CommitteeType committeeType = 
			this.getCommitteeName().getCommitteeType(); 
		HouseType houseType = committeeType.getHouseType();
		String houseTypeType = houseType.getType();
		String locale = this.getLocale();
		
		// If the workflow is "Member addition"
		Status intStatusPAMLH = this.getInternalStatusPAMLH();
		Status intStatusLOPLH = this.getInternalStatusLOPLH();
		Status intStatusPAMUH = this.getInternalStatusPAMUH();
		Status intStatusLOPUH = this.getInternalStatusLOPUH();
		
		String pamProcessed = ApplicationConstants.
			COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER_PROCESSED;
		String lopProcessed = ApplicationConstants.
			COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION_PROCESSED;
		
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			if(intStatusPAMLH != null && intStatusLOPLH != null) {
				String intStatusPAMLHType = intStatusPAMLH.getType();
				String intStatusLOPLHType = intStatusLOPLH.getType();
				
				if(intStatusPAMLHType.equals(pamProcessed)
						&& intStatusLOPLHType.equals(lopProcessed)) {
					Status status = Status.findByType(
							ApplicationConstants.COMMITTEE_MEMBERS_ADDED, 
							locale);
					this.setStatus(status);
				}
			}
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			if(intStatusPAMUH != null && intStatusLOPUH != null) {
				String intStatusPAMUHType = intStatusPAMUH.getType();
				String intStatusLOPUHType = intStatusLOPUH.getType();
				
				if(intStatusPAMUHType.equals(pamProcessed)
						&& intStatusLOPUHType.equals(lopProcessed)) {
					Status status = Status.findByType(
							ApplicationConstants.COMMITTEE_MEMBERS_ADDED, 
							locale);
					this.setStatus(status);
				}
			}
		}
		else {
			if(intStatusPAMLH != null && intStatusLOPLH != null
					&& intStatusPAMUH != null && intStatusLOPUH != null) {
				String intStatusPAMLHType = intStatusPAMLH.getType();
				String intStatusPAMUHType = intStatusPAMUH.getType();
				String intStatusLOPLHType = intStatusLOPLH.getType();
				String intStatusLOPUHType = intStatusLOPUH.getType();
				
				if(intStatusPAMLHType.equals(pamProcessed)
						&& intStatusPAMUHType.equals(pamProcessed)
						&& intStatusLOPLHType.equals(lopProcessed)
						&& intStatusLOPUHType.equals(lopProcessed)) {
					Status status = Status.findByType(
							ApplicationConstants.COMMITTEE_MEMBERS_ADDED, 
							locale);
					this.setStatus(status);
				}
			}
		}
		
		// If the workflow is "Invited Member addition"
		Status intStatusIMLH = this.getInternalStatusIMLH();
		Status intStatusIMUH = this.getInternalStatusIMUH();
		
		String imProcessed = ApplicationConstants.
			COMMITTEE_INVITED_MEMBER_ADDITION_PROCESSED;
		
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			if(intStatusIMLH != null) {
				String intStatusIMLHType = intStatusIMLH.getType();
				if(intStatusIMLHType.equals(imProcessed)) {
					Status status = Status.findByType(
							ApplicationConstants.
								COMMITTEE_INVITED_MEMBERS_ADDED, 
							locale);
					this.setStatus(status);
				}
			}
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			if(intStatusIMUH != null) {
				String intStatusIMUHType = intStatusIMUH.getType();
				if(intStatusIMUHType.equals(imProcessed)) {
					Status status = Status.findByType(
							ApplicationConstants.
								COMMITTEE_INVITED_MEMBERS_ADDED, 
							locale);
					this.setStatus(status);
				}
			}
		}
		else {
			if(intStatusIMLH != null && intStatusIMUH != null) {
				String intStatusIMLHType = intStatusIMLH.getType();
				String intStatusIMUHType = intStatusIMUH.getType();
				if(intStatusIMLHType.equals(imProcessed)
						&& intStatusIMUHType.equals(imProcessed)) {
					Status status = Status.findByType(
							ApplicationConstants.
								COMMITTEE_INVITED_MEMBERS_ADDED, 
							locale);
					this.setStatus(status);
				}
			}
		}
		
		return super.merge();
	}
	
	/**
	 * If this method returns 0 then a wrong @param houseType has been
	 * passed.
	 */
	public Integer maxCommitteeMembers(final HouseType houseType) {
		Integer maxCommitteeMembers = 0;
		
		String houseTypeType = houseType.getType();
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			Integer noOfLowerHouseMembers = 
				this.getCommitteeName().getNoOfLowerHouseMembers();
			maxCommitteeMembers = noOfLowerHouseMembers;
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			Integer noOfUpperHouseMembers = 
				this.getCommitteeName().getNoOfUpperHouseMembers();
			maxCommitteeMembers = noOfUpperHouseMembers;
		}
		else if(houseTypeType.equals(ApplicationConstants.BOTH_HOUSE)) {
			Integer noOfLowerHouseMembers = 
				this.getCommitteeName().getNoOfLowerHouseMembers();
			
			Integer noOfUpperHouseMembers = 
				this.getCommitteeName().getNoOfUpperHouseMembers();
			
			maxCommitteeMembers = noOfLowerHouseMembers + noOfUpperHouseMembers;
		}
		
		return maxCommitteeMembers;
	}
	
	public Integer committeeMembersCount(final PartyType partyType, 
			final HouseType houseType) {
		String locale = this.getLocale();
		PartyType rulingPartyType = 
			PartyType.findByType(ApplicationConstants.RULING_PARTY, locale);
		
		Integer maxCommitteeMembers = this.maxCommitteeMembers(houseType);
		
		Integer maxMembersInHouse = 
			Committee.findActiveMembersInHouse(houseType, locale);
		
		Integer maxRulingPartyMembersInHouse = 
			Committee.findActiveMembersInHouse(houseType, 
					rulingPartyType, locale);
		
		Integer rulingPartyCommitteeMembersCount = 
			Committee.rulingPartyCommitteeMembersCount(maxCommitteeMembers,
					maxRulingPartyMembersInHouse, maxMembersInHouse);
		
		if(partyType.getType().equals(ApplicationConstants.RULING_PARTY)) {
			return rulingPartyCommitteeMembersCount;
		}
		else {
			Integer oppositionPartyCommitteeMembersCount = 
				maxCommitteeMembers - rulingPartyCommitteeMembersCount;
			return oppositionPartyCommitteeMembersCount;
		}
	}
	
	public static Committee findLatestCommitteeByCommitteeName(CommitteeName committeeName) {
		if(committeeName!=null && committeeName.getId()!=null && committeeName.getIsExpired()==false) {
			return Committee.findLatestCommitteeByCommitteeNameId(committeeName);
		}
		return null;
	}

	//=============== INTERNAL METHODS =========
	private static CommitteeRepository getRepository() {
		CommitteeRepository repository = new Committee().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"CommitteeRepository has not been injected in Committee Domain");
		}
		
		return repository;
	}
	
	private static CommitteeVO createCommitteeVO(final Committee c,
			final HouseType houseType,
			final PartyType partyType,
			final String locale) {
		// Committee Details
		Long committeeId = c.getId();
		
		CommitteeName cn = c.getCommitteeName();
		String committeeName = cn.getName();		
		String committeeDisplayName = cn.getDisplayName();
		
		CommitteeType ct = cn.getCommitteeType();	
		String committeeType = ct.getName();
		
		Integer maxCommitteeMembers = c.maxCommitteeMembers(houseType);
		
		// Committee Members		
		CommitteeMemberVO committeeChairman = Committee.createChairmanVO(c);
		
		List<CommitteeMemberVO> committeeMembers = 
			Committee.createCommitteeMembersVO(c, houseType, partyType, locale);
		StringBuffer committeeMembersName = new StringBuffer();
		for(CommitteeMemberVO cm : committeeMembers) {
			committeeMembersName.append(cm.getMemberName());
			committeeMembersName.append(", ");
		}
		
		List<CommitteeMemberVO> invitedCommitteeMembers = 
			Committee.createInvitedCommitteeMembersVO(c, houseType, 
					partyType, locale);
		StringBuffer invitedCommitteeMembersName = new StringBuffer();
		for(CommitteeMemberVO cm : invitedCommitteeMembers) {
			invitedCommitteeMembersName.append(cm.getMemberName());
			invitedCommitteeMembersName.append(", ");
		}
		
		// Ruling PartyVO
		PartyType rulingPartyType = 
			PartyType.findByType(ApplicationConstants.RULING_PARTY, locale);
		Integer rulingPartyCommitteeMembersCount = 
			c.committeeMembersCount(rulingPartyType, houseType);
		List<PartyVO> rulingParties = 
			Committee.createPartyVOs(houseType, rulingPartyType, 
					rulingPartyCommitteeMembersCount, locale);
		
		// Opposition PartyVO
		PartyType oppositionPartyType = 
			PartyType.findByType(ApplicationConstants.OPPOSITION_PARTY, locale);
		Integer oppositionPartyCommitteeMembersCount = 
			c.committeeMembersCount(oppositionPartyType, houseType);
		List<PartyVO> oppositionParties = 
			Committee.createPartyVOs(houseType, oppositionPartyType, 
					oppositionPartyCommitteeMembersCount, locale);
		
		CommitteeVO committeeVO = new CommitteeVO();
		committeeVO.setCommitteeId(committeeId);
		committeeVO.setCommitteeName(committeeName);
		committeeVO.setCommitteeDisplayName(committeeDisplayName);
		committeeVO.setCommitteeType(committeeType);
		committeeVO.setMaxCommitteeMembers(maxCommitteeMembers);
		committeeVO.setCommitteeChairman(committeeChairman);
		committeeVO.setCommitteeMembers(committeeMembers);
		committeeVO.setCommitteeMembersName(committeeMembersName.toString());
		committeeVO.setInvitedCommitteeMembers(invitedCommitteeMembers);
		committeeVO.setInvitedCommitteeMembersName(
				invitedCommitteeMembersName.toString());
		committeeVO.setRulingParties(rulingParties);
		committeeVO.setRulingPartyCommitteeMembersCount(
				rulingPartyCommitteeMembersCount);
		committeeVO.setOppositionParties(oppositionParties);
		committeeVO.setOppositionPartyCommitteeMembersCount(
				oppositionPartyCommitteeMembersCount);
		
		return committeeVO;
	}
	
	private static CommitteeVO createCommitteeVO(final Committee c,
			final HouseType houseType,
			final String locale) {
		// Committee Details
		Long committeeId = c.getId();
		
		CommitteeName cn = c.getCommitteeName();
		String committeeName = cn.getName();		
		String committeeDisplayName = cn.getDisplayName();
		
		CommitteeType ct = cn.getCommitteeType();	
		String committeeType = ct.getName();
		
		Integer maxCommitteeMembers = c.maxCommitteeMembers(houseType);
		
		// Committee Members		
		CommitteeMemberVO committeeChairman = Committee.createChairmanVO(c);
		
		List<CommitteeMemberVO> committeeMembers = 
			Committee.createCommitteeMembersVO(c, houseType, locale);
		StringBuffer committeeMembersName = new StringBuffer();
		for(CommitteeMemberVO cm : committeeMembers) {
			committeeMembersName.append(cm.getMemberName());
			committeeMembersName.append(", ");
		}
		
		List<CommitteeMemberVO> invitedCommitteeMembers = 
			Committee.createInvitedCommitteeMembersVO(c, houseType, locale);
		StringBuffer invitedCommitteeMembersName = new StringBuffer();
		for(CommitteeMemberVO cm : invitedCommitteeMembers) {
			invitedCommitteeMembersName.append(cm.getMemberName());
			invitedCommitteeMembersName.append(", ");
		}
		
		CommitteeVO committeeVO = new CommitteeVO();
		committeeVO.setCommitteeId(committeeId);
		committeeVO.setCommitteeName(committeeName);
		committeeVO.setCommitteeDisplayName(committeeDisplayName);
		committeeVO.setCommitteeType(committeeType);
		committeeVO.setMaxCommitteeMembers(maxCommitteeMembers);
		committeeVO.setCommitteeChairman(committeeChairman);
		committeeVO.setCommitteeMembers(committeeMembers);
		committeeVO.setCommitteeMembersName(committeeMembersName.toString());
		committeeVO.setInvitedCommitteeMembers(invitedCommitteeMembers);
		committeeVO.setInvitedCommitteeMembersName(
				invitedCommitteeMembersName.toString());
		
		return committeeVO;
	}
	
	private static CommitteeMemberVO createChairmanVO(final Committee c) {
		List<CommitteeMember> members = c.getMembers();
		
		if(members.size() > 0) {
			CommitteeMember cMember = members.get(0);
			CommitteeDesignation designation = cMember.getDesignation();
			String designationType = designation.getType();
			if(designationType.equals(
					ApplicationConstants.COMMITTEE_CHAIRMAN)) {
				Member member = cMember.getMember();
				Long memberId = member.getId();
				String memberName = member.getFullname();
				
				CommitteeMemberVO chairmanVO = new CommitteeMemberVO();
				chairmanVO.setMemberId(memberId);
				chairmanVO.setMemberName(memberName);
				
				return chairmanVO;
			}
		}
		
		return null;
	}

	private static List<CommitteeMemberVO> createCommitteeMembersVO(
			final Committee c,
			final HouseType houseType,
			final PartyType partyType,
			final String locale) {
		List<CommitteeMemberVO> memberVOs = new ArrayList<CommitteeMemberVO>();
		
		List<CommitteeMember> members = c.getMembers();
		for(CommitteeMember cm : members) {
			CommitteeDesignation designation = cm.getDesignation();
			String designationType = designation.getType();
			Boolean flag1 = 
				designationType.equals(ApplicationConstants.COMMITTEE_CHAIRMAN);
			
			Member member = cm.getMember();
			// Add member to membersVOs only if the houseType of the
			// house to which the member belongs is same as @param
			// houseType & members partyType is equal to @param partyType
			Date currentDate = new Date();
			House house = House.find(houseType, currentDate, locale);
			Boolean flag2 = 
				House.isMemberBelongsTo(member, house, partyType, locale);
			
			if(! flag1 && flag2) {
				Long memberId = member.getId();
				String memberName = member.getFullname();
				
				CommitteeMemberVO memberVO = new CommitteeMemberVO();
				memberVO.setMemberId(memberId);
				memberVO.setMemberName(memberName);
				memberVOs.add(memberVO);
			}
		}
		
		return memberVOs;
	}
	
	private static List<CommitteeMemberVO> createCommitteeMembersVO(
			final Committee c,
			final HouseType houseType,
			final String locale) {
		List<CommitteeMemberVO> memberVOs = new ArrayList<CommitteeMemberVO>();
		
		List<CommitteeMember> members = c.getMembers();		
		for(CommitteeMember cm : members) {
			CommitteeDesignation designation = cm.getDesignation();
			String designationType = designation.getType();
			Boolean flag = 
				designationType.equals(ApplicationConstants.COMMITTEE_CHAIRMAN);
			
			if(! flag) {
				Member member = cm.getMember();
				Long memberId = member.getId();
				String memberName = member.getFullname();
				
				CommitteeMemberVO memberVO = new CommitteeMemberVO();
				memberVO.setMemberId(memberId);
				memberVO.setMemberName(memberName);
				memberVOs.add(memberVO);
			}
		}
		
		return memberVOs;
	}
	
	private static List<CommitteeMemberVO> createInvitedCommitteeMembersVO(
			final Committee c,
			final HouseType houseType,
			final PartyType partyType,
			final String locale) {
		List<CommitteeMemberVO> invitedMemberVOs = 
			new ArrayList<CommitteeMemberVO>();
		
		List<CommitteeMember> invitedMembers = c.getInvitedMembers();		
		for(CommitteeMember cm : invitedMembers) {
			Member member = cm.getMember();
			
			// Add member to membersVOs only if the houseType of the
			// house to which the member belongs is same as @param
			// houseType & members partyType is equal to @param partyType
			Date currentDate = new Date();
			House house = House.find(houseType, currentDate, locale);
			Boolean flag = 
				House.isMemberBelongsTo(member, house, partyType, locale);
			
			if(flag) {
				Long memberId = member.getId();
				String memberName = member.getFullname();
				
				CommitteeMemberVO invitedMemberVO = new CommitteeMemberVO();
				invitedMemberVO.setMemberId(memberId);
				invitedMemberVO.setMemberName(memberName);
				invitedMemberVOs.add(invitedMemberVO);
			}
		}
		
		return invitedMemberVOs;
	}
	
	private static List<CommitteeMemberVO> createInvitedCommitteeMembersVO(
			final Committee c,
			final HouseType houseType,
			final String locale) {
		List<CommitteeMemberVO> invitedMemberVOs = 
			new ArrayList<CommitteeMemberVO>();
		
		List<CommitteeMember> invitedMembers = c.getInvitedMembers();		
		for(CommitteeMember cm : invitedMembers) {
			Member member = cm.getMember();
			
			// Add member to membersVOs only if the houseType of the
			// house to which the member belongs is same as @param
			// houseType & members partyType is equal to @param partyType
			Date currentDate = new Date();
			House house = House.find(houseType, currentDate, locale);
			Boolean flag = 
				House.isMemberBelongsTo(member, house, locale);
			
			if(flag) {
				Long memberId = member.getId();
				String memberName = member.getFullname();
				
				CommitteeMemberVO invitedMemberVO = new CommitteeMemberVO();
				invitedMemberVO.setMemberId(memberId);
				invitedMemberVO.setMemberName(memberName);
				invitedMemberVOs.add(invitedMemberVO);
			}
		}
		
		return invitedMemberVOs;
	}
	
	private static List<PartyVO> createPartyVOs(final HouseType houseType,
			final PartyType partyType, 
			final Integer maxPartyTypeWiseCommitteeMembers,
			final String locale) {
		List<PartyVO> partyVOs = new ArrayList<PartyVO>();
		
		Integer maxSeats = maxPartyTypeWiseCommitteeMembers;
		
		Map<Long, Integer> partyStrengthMap = 
			Committee.computePartyStrength(houseType, partyType, locale);
		
		Integer maxPartyTypeWiseMembers = 
			Committee.findActiveMembersInHouse(houseType, partyType, locale);
		
		Date currentDate = new Date();
		List<Party> parties = House.find(houseType, partyType, currentDate, 
				ApplicationConstants.DESC, locale);
		int length = parties.size();
		for(int i = 0; i < length; i++) {
			Party p = parties.get(i);
			if(maxSeats > 0) {
				PartyVO partyVO = new PartyVO();
				partyVO.setPartyId(p.getId());
				partyVO.setShortName(p.getShortName());
				partyVO.setName(p.getName());
				partyVO.setType(p.getPartyType().getType());
				
				// If this party is the last party in the list
				// then assign all the remaining seats to this party
				if(i == length - 1) {
					Integer partyWiseCommitteeMembersCount = maxSeats;
					partyVO.setNoOfMembers(partyWiseCommitteeMembersCount);
					maxSeats = maxSeats - partyWiseCommitteeMembersCount;
				}
				else {
					Integer maxPartyMembers = partyStrengthMap.get(p.getId());
					Integer partyWiseCommitteeMembersCount = 
						Committee.partyWiseCommitteeMembersCount(
								maxPartyTypeWiseCommitteeMembers, 
								maxPartyMembers, 
								maxPartyTypeWiseMembers);
					
					partyVO.setNoOfMembers(partyWiseCommitteeMembersCount);
					maxSeats = maxSeats - partyWiseCommitteeMembersCount;
				}
				
				partyVOs.add(partyVO);
			}
		}
		
		return partyVOs;
	}
	
	/**
	 * The key is the partyId and the Value is the party strength.
	 */
	private static Map<Long, Integer> computePartyStrength(
			final HouseType houseType,
			final PartyType partyType,
			final String locale) {
		Map<Long, Integer> partyStrengthMap = new HashMap<Long, Integer>();
		
		Date currentDate = new Date();
		List<Party> parties = House.find(houseType, partyType, currentDate, 
				ApplicationConstants.DESC, locale);
		for(Party p : parties) {
			long partyStrength = 0;
			
			String type = p.getPartyType().getType();
			if(type != null && 
					ApplicationConstants.INDEPENDENT_PARTY.equals(type)) {
				partyStrength = House.findIndependentMembersCount(houseType, 
						partyType, currentDate, locale);
			}
			else {
				partyStrength = House.findActiveMembersCount(houseType, 
						p, currentDate, locale);
				
			}
			
			partyStrengthMap.put(p.getId(), (int) partyStrength);
		}
		
		return partyStrengthMap;
	}

	private static void addPartiesToCommitteeCompositeVO(
			final CommitteeCompositeVO compositeVO,
			final HouseType houseType,
			final String locale) {
		Date currentDate = new Date();
		
		// Add Ruling Parties to CommitteeCompositeVO
		PartyType rulingPartyType = 
			PartyType.findByType(ApplicationConstants.RULING_PARTY, locale);
		
		List<Party> rulingParties = House.find(houseType, rulingPartyType, 
				currentDate, ApplicationConstants.DESC, locale);
		
		List<PartyVO> rulingPartyVO = new ArrayList<PartyVO>();
		for(Party p : rulingParties) {
			PartyVO partyVO = new PartyVO();
			partyVO.setPartyId(p.getId());
			partyVO.setShortName(p.getShortName());
			partyVO.setName(p.getName());
			partyVO.setType(p.getPartyType().getType());
			
			rulingPartyVO.add(partyVO);
		}
		compositeVO.setRulingParties(rulingPartyVO);
		
		// Add Opposition Parties to CommitteeCompositeVO
		PartyType oppositionPartyType = 
			PartyType.findByType(ApplicationConstants.OPPOSITION_PARTY, locale);
		
		List<Party> oppositionParties = House.find(houseType, 
				oppositionPartyType, currentDate, ApplicationConstants.DESC, 
				locale);
		
		List<PartyVO> oppositionPartyVO = new ArrayList<PartyVO>();
		for(Party p : oppositionParties) {
			PartyVO partyVO = new PartyVO();
			partyVO.setPartyId(p.getId());
			partyVO.setShortName(p.getShortName());
			partyVO.setName(p.getName());
			partyVO.setType(p.getPartyType().getType());
			
			oppositionPartyVO.add(partyVO);
		}
		compositeVO.setOppositionParties(oppositionPartyVO);
	}
	
	private static Integer findActiveMembersInHouse(HouseType houseType,
			String locale) {
		Date currentDate = new Date();
		long activeMembersInHouse = 
			House.findActiveMembersCount(houseType, currentDate, locale);
		return (int) activeMembersInHouse;
	}
	
	private static Integer findActiveMembersInHouse(HouseType houseType,
			PartyType partyType,
			String locale) {
		Date currentDate = new Date();
		long activeMembersInHouse = House.findActiveMembersCount(houseType, 
				partyType, currentDate, locale);
		return (int) activeMembersInHouse;
	}
	
	private static Integer rulingPartyCommitteeMembersCount(
			final int maxCommitteeMembers,
			final int maxRulingPartyMembersInHouse,
			final int maxMembersInHouse) {
		double d1 = (double) maxCommitteeMembers * 
					(double) maxRulingPartyMembersInHouse;
		double d2 = d1 / (double) maxMembersInHouse;
		double d3 = Math.round(d2);
		int rulingPartyCommitteeMembersCount = (int) d3;
		return rulingPartyCommitteeMembersCount;
	}
	
	private static Integer partyWiseCommitteeMembersCount(
			final int maxPartyTypeWiseCommitteeMembers,
			final int maxPartyMembers,
			final int maxPartyTypeWiseMembers) {
		double d1 = (double) maxPartyTypeWiseCommitteeMembers * 
					(double) maxPartyMembers;
		double d2 = d1 / (double) maxPartyTypeWiseMembers;
		double d3 = Math.round(d2);
		int partyWiseCommitteeMembersCount = (int) d3;
		return partyWiseCommitteeMembersCount;
	}
	
	private static Committee findLatestCommitteeByCommitteeNameId(CommitteeName committeeName) {
		if(committeeName!=null && committeeName.getId()>0) {
			List<Committee> committees = Committee.findAllByFieldName(Committee.class, "committeeName", committeeName, "id","desc", "mr_IN");
			if(committees!=null && committees.size()>0)
				return committees.get(0);
		}
		return null;
	}
	
	
	//=============== GETTERS/SETTERS ==========
	public CommitteeName getCommitteeName() {
		return committeeName;
	}

	public void setCommitteeName(final CommitteeName committeeName) {
		this.committeeName = committeeName;
	}

	public Date getFormationDate() {
		return formationDate;
	}

	public void setFormationDate(final Date formationDate) {
		this.formationDate = formationDate;
	}

	public Date getDissolutionDate() {
		return dissolutionDate;
	}

	public void setDissolutionDate(final Date dissolutionDate) {
		this.dissolutionDate = dissolutionDate;
	}

	public List<CommitteeMember> getMembers() {
		return members;
	}

	public void setMembers(final List<CommitteeMember> members) {
		this.members = members;
	}

	public List<CommitteeMember> getInvitedMembers() {
		return invitedMembers;
	}

	public void setInvitedMembers(final List<CommitteeMember> invitedMembers) {
		this.invitedMembers = invitedMembers;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}
	
	public Status getInternalStatusPAMLH() {
		return internalStatusPAMLH;
	}

	public void setInternalStatusPAMLH(final Status internalStatusPAMLH) {
		this.internalStatusPAMLH = internalStatusPAMLH;
	}

	public Status getRecommendationStatusPAMLH() {
		return recommendationStatusPAMLH;
	}

	public void setRecommendationStatusPAMLH(
			final Status recommendationStatusPAMLH) {
		this.recommendationStatusPAMLH = recommendationStatusPAMLH;
	}

	public String getRemarksPAMLH() {
		return remarksPAMLH;
	}

	public void setRemarksPAMLH(final String remarksPAMLH) {
		this.remarksPAMLH = remarksPAMLH;
	}

	public Status getInternalStatusPAMUH() {
		return internalStatusPAMUH;
	}

	public void setInternalStatusPAMUH(final Status internalStatusPAMUH) {
		this.internalStatusPAMUH = internalStatusPAMUH;
	}

	public Status getRecommendationStatusPAMUH() {
		return recommendationStatusPAMUH;
	}

	public void setRecommendationStatusPAMUH(
			final Status recommendationStatusPAMUH) {
		this.recommendationStatusPAMUH = recommendationStatusPAMUH;
	}

	public String getRemarksPAMUH() {
		return remarksPAMUH;
	}

	public void setRemarksPAMUH(final String remarksPAMUH) {
		this.remarksPAMUH = remarksPAMUH;
	}

	public Status getInternalStatusLOPLH() {
		return internalStatusLOPLH;
	}

	public void setInternalStatusLOPLH(final Status internalStatusLOPLH) {
		this.internalStatusLOPLH = internalStatusLOPLH;
	}

	public Status getRecommendationStatusLOPLH() {
		return recommendationStatusLOPLH;
	}

	public void setRecommendationStatusLOPLH(
			final Status recommendationStatusLOPLH) {
		this.recommendationStatusLOPLH = recommendationStatusLOPLH;
	}

	public String getRemarksLOPLH() {
		return remarksLOPLH;
	}

	public void setRemarksLOPLH(final String remarksLOPLH) {
		this.remarksLOPLH = remarksLOPLH;
	}

	public Status getInternalStatusLOPUH() {
		return internalStatusLOPUH;
	}

	public void setInternalStatusLOPUH(final Status internalStatusLOPUH) {
		this.internalStatusLOPUH = internalStatusLOPUH;
	}

	public Status getRecommendationStatusLOPUH() {
		return recommendationStatusLOPUH;
	}

	public void setRecommendationStatusLOPUH(
			final Status recommendationStatusLOPUH) {
		this.recommendationStatusLOPUH = recommendationStatusLOPUH;
	}

	public String getRemarksLOPUH() {
		return remarksLOPUH;
	}

	public void setRemarksLOPUH(final String remarksLOPUH) {
		this.remarksLOPUH = remarksLOPUH;
	}
	
	public Status getInternalStatusIMLH() {
		return internalStatusIMLH;
	}

	public void setInternalStatusIMLH(final Status internalStatusIMLH) {
		this.internalStatusIMLH = internalStatusIMLH;
	}

	public Status getRecommendationStatusIMLH() {
		return recommendationStatusIMLH;
	}

	public void setRecommendationStatusIMLH(
			final Status recommendationStatusIMLH) {
		this.recommendationStatusIMLH = recommendationStatusIMLH;
	}

	public String getRemarksIMLH() {
		return remarksIMLH;
	}

	public void setRemarksIMLH(final String remarksIMLH) {
		this.remarksIMLH = remarksIMLH;
	}

	public Status getInternalStatusIMUH() {
		return internalStatusIMUH;
	}

	public void setInternalStatusIMUH(final Status internalStatusIMUH) {
		this.internalStatusIMUH = internalStatusIMUH;
	}

	public Status getRecommendationStatusIMUH() {
		return recommendationStatusIMUH;
	}

	public void setRecommendationStatusIMUH(
			final Status recommendationStatusIMUH) {
		this.recommendationStatusIMUH = recommendationStatusIMUH;
	}

	public String getRemarksIMUH() {
		return remarksIMUH;
	}

	public void setRemarksIMUH(final String remarksIMUH) {
		this.remarksIMUH = remarksIMUH;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(final Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(final String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(final String editedBy) {
		this.editedBy = editedBy;
	}

	public List<CommitteeDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(final List<CommitteeDraft> drafts) {
		this.drafts = drafts;
	}

}