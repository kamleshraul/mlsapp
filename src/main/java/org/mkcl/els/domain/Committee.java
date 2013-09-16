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
	"internalStatusPAMWf", "recommendationStatusPAMWf", "remarksPAMWf",
	"internalStatusLOPWf", "recommendationStatusLOPWf", "remarksLOPWf",
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
	
	/**
	 * The Chairman of the Committee (CommitteeMember with the 
	 * designation of Committee Chairman) is stored as the first
	 * entry in members list. This entry is followed by the members
	 * in the committee with designation = Committee Member
	 */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committees_committee_members",
			joinColumns={@JoinColumn(name="committee_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_member_id", 
					referencedColumnName="id")})
	private List<CommitteeMember> members;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committees_committee_members",
			joinColumns={@JoinColumn(name="committee_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_member_id", 
					referencedColumnName="id")})
	private List<CommitteeMember> invitedMembers;
	
	/* Work flow Attributes */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;
	
	// "REQUEST TO PARLIAMENTARY AFFAIRS MINISTER FOR ADDITION
	// OF MEMBERS TO COMITTEE" work flow attributes 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_pamwf_id")
	private Status internalStatusPAMWf;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_pamwf_id")
	private Status recommendationStatusPAMWf;
	
	@Column(length=30000)
	private String remarksPAMWf;
	
	// "REQUEST TO LEADER OF OPPOSITION FOR ADDITION
	// OF MEMBERS TO COMITTEE" work flow attributes 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_lop_id")
	private Status internalStatusLOPWf;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_lop_id")
	private Status recommendationStatusLOPWf;
	
	@Column(length=30000)
	private String remarksLOPWf;
	
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
			final String locale) {
		CommitteeCompositeVO compositeVO = new CommitteeCompositeVO();
		
		List<CommitteeVO> committeeVOs = new ArrayList<CommitteeVO>();
		for(Committee c : committees) {
			CommitteeVO committeeVO = 
				Committee.createCommitteeVO(c, houseType, locale);
			committeeVOs.add(committeeVO);
		}
		compositeVO.setCommitteeVOs(committeeVOs);
		
		Committee.addToCommitteeCompositeVO(compositeVO, houseType, locale);
		
		return compositeVO;
	}

	//=============== DOMAIN METHODS ===========
	public static Committee find(final CommitteeName committeeName,
			final Date formationDate, 
			final String locale) {
		return Committee.getRepository().find(committeeName, 
				formationDate, locale);
	}
	
	public static List<Committee> findCommitteesToBeProcessed(
			final String locale) {
		Status status = 
			Status.findByType(ApplicationConstants.COMMITTEE_CREATED, locale);
		Date currentDate = new Date();
		return Committee.getRepository().find(status,currentDate, locale);
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
			Committee.createCommitteeMembersVO(c);
		StringBuffer committeeMembersName = new StringBuffer();
		for(CommitteeMemberVO cm : committeeMembers) {
			committeeMembersName.append(cm.getMemberName());
			committeeMembersName.append(", ");
		}
		
		List<CommitteeMemberVO> invitedCommitteeMembers = 
			Committee.createInvitedCommitteeMembersVO(c);
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
	
	private static CommitteeMemberVO createChairmanVO(final Committee c) {
		List<CommitteeMember> members = c.getMembers();
		CommitteeMember chairman = members.get(0);
		if(chairman != null) {
			Member member = chairman.getMember();
			Long memberId = member.getId();
			String memberName = member.getFullname();
			
			CommitteeMemberVO chairmanVO = new CommitteeMemberVO();
			chairmanVO.setMemberId(memberId);
			chairmanVO.setMemberName(memberName);
			
			return chairmanVO;
		}
		
		return null;
	}

	private static List<CommitteeMemberVO> createCommitteeMembersVO(
			final Committee c) {
		List<CommitteeMemberVO> memberVOs = new ArrayList<CommitteeMemberVO>();
		
		List<CommitteeMember> members = new ArrayList<CommitteeMember>();
		try {
			int size = c.getMembers().size();
			members = c.getMembers().subList(1, size - 1);
		}
		catch(Exception e) {}
		
		for(CommitteeMember cm : members) {
			Member member = cm.getMember();
			Long memberId = member.getId();
			String memberName = member.getFullname();
			
			CommitteeMemberVO memberVO = new CommitteeMemberVO();
			memberVO.setMemberId(memberId);
			memberVO.setMemberName(memberName);
			memberVOs.add(memberVO);
		}
		
		return memberVOs;
	}
	
	private static List<CommitteeMemberVO> createInvitedCommitteeMembersVO(
			final Committee c) {
		List<CommitteeMemberVO> invitedMemberVOs = 
			new ArrayList<CommitteeMemberVO>();
		
		List<CommitteeMember> invitedMembers = c.getInvitedMembers();		
		for(CommitteeMember cm : invitedMembers) {
			Member member = cm.getMember();
			Long memberId = member.getId();
			String memberName = member.getFullname();
			
			CommitteeMemberVO invitedMemberVO = new CommitteeMemberVO();
			invitedMemberVO.setMemberId(memberId);
			invitedMemberVO.setMemberName(memberName);
			invitedMemberVOs.add(invitedMemberVO);
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
				partyVO.setType(p.getType());
				
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
			
			String type = p.getType();
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

	private static void addToCommitteeCompositeVO(
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
			partyVO.setType(p.getType());
			
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
			partyVO.setType(p.getType());
			
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

	public Status getInternalStatusPAMWf() {
		return internalStatusPAMWf;
	}

	public void setInternalStatusPAMWf(final Status internalStatusPAMWf) {
		this.internalStatusPAMWf = internalStatusPAMWf;
	}

	public Status getRecommendationStatusPAMWf() {
		return recommendationStatusPAMWf;
	}

	public void setRecommendationStatusPAMWf(
			final Status recommendationStatusPAMWf) {
		this.recommendationStatusPAMWf = recommendationStatusPAMWf;
	}

	public String getRemarksPAMWf() {
		return remarksPAMWf;
	}

	public void setRemarksPAMWf(final String remarksPAMWf) {
		this.remarksPAMWf = remarksPAMWf;
	}

	public Status getInternalStatusLOPWf() {
		return internalStatusLOPWf;
	}

	public void setInternalStatusLOPWf(final Status internalStatusLOPWf) {
		this.internalStatusLOPWf = internalStatusLOPWf;
	}

	public Status getRecommendationStatusLOPWf() {
		return recommendationStatusLOPWf;
	}

	public void setRecommendationStatusLOPWf(
			final Status recommendationStatusLOPWf) {
		this.recommendationStatusLOPWf = recommendationStatusLOPWf;
	}

	public String getRemarksLOPWf() {
		return remarksLOPWf;
	}

	public void setRemarksLOPWf(final String remarksLOPWf) {
		this.remarksLOPWf = remarksLOPWf;
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