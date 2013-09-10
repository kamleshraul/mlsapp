package org.mkcl.els.common.vo;

import java.util.List;

public class CommitteeVO {

	//=============== ATTRIBUTES ====================
	private Long committeeId;
	
	private String committeeName;
	
	private String committeeDisplayName;
	
	private String committeeType;
	
	private Integer maxCommitteeMembers;
	
	private List<PartyVO> rulingParties;
	
	private Integer rulingPartyCommitteeMembersCount;
	
	private List<PartyVO> oppositionParties;
	
	private Integer oppositionPartyCommitteeMembersCount;
	
	//=============== CONSTRUCTORS ==================
	public CommitteeVO() {
		super();
	}
	
	public CommitteeVO(final Long committeeId,
			final String committeeName,
			final String committeeDisplayName,
			final String committeeType,
			final Integer maxCommitteeMembers,
			final List<PartyVO> rulingParties,
			final Integer rulingPartyCommitteeMembersCount,
			final List<PartyVO> oppositionParties,
			final Integer oppositionPartyCommitteeMembersCount) {
		this.setCommitteeId(committeeId);
		this.setCommitteeName(committeeName);
		this.setCommitteeDisplayName(committeeDisplayName);
		this.setCommitteeType(committeeType);
		this.setMaxCommitteeMembers(maxCommitteeMembers);
		this.setRulingParties(rulingParties);
		this.setRulingPartyCommitteeMembersCount(
				rulingPartyCommitteeMembersCount);
		this.setOppositionParties(oppositionParties);
		this.setOppositionPartyCommitteeMembersCount(
				oppositionPartyCommitteeMembersCount);
	}
	
	//=============== GETTERS SETTERS ===============
	public Long getCommitteeId() {
		return committeeId;
	}

	public void setCommitteeId(final Long committeeId) {
		this.committeeId = committeeId;
	}

	public String getCommitteeName() {
		return committeeName;
	}

	public void setCommitteeName(final String committeeName) {
		this.committeeName = committeeName;
	}
	
	public String getCommitteeDisplayName() {
		return committeeDisplayName;
	}

	public void setCommitteeDisplayName(final String committeeDisplayName) {
		this.committeeDisplayName = committeeDisplayName;
	}

	public String getCommitteeType() {
		return committeeType;
	}

	public void setCommitteeType(final String committeeType) {
		this.committeeType = committeeType;
	}

	public Integer getMaxCommitteeMembers() {
		return maxCommitteeMembers;
	}

	public void setMaxCommitteeMembers(final Integer maxCommitteeMembers) {
		this.maxCommitteeMembers = maxCommitteeMembers;
	}

	public List<PartyVO> getRulingParties() {
		return rulingParties;
	}

	public void setRulingParties(final List<PartyVO> rulingParties) {
		this.rulingParties = rulingParties;
	}

	public Integer getRulingPartyCommitteeMembersCount() {
		return rulingPartyCommitteeMembersCount;
	}

	public void setRulingPartyCommitteeMembersCount(
			final Integer rulingPartyCommitteeMembersCount) {
		this.rulingPartyCommitteeMembersCount = rulingPartyCommitteeMembersCount;
	}

	public List<PartyVO> getOppositionParties() {
		return oppositionParties;
	}

	public void setOppositionParties(final List<PartyVO> oppositionParties) {
		this.oppositionParties = oppositionParties;
	}

	public Integer getOppositionPartyCommitteeMembersCount() {
		return oppositionPartyCommitteeMembersCount;
	}

	public void setOppositionPartyCommitteeMembersCount(
			final Integer oppositionPartyCommitteeMembersCount) {
		this.oppositionPartyCommitteeMembersCount = 
			oppositionPartyCommitteeMembersCount;
	}
}