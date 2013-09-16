package org.mkcl.els.common.vo;

import java.util.List;

public class CommitteeVO {

	//=============== ATTRIBUTES ====================
	private Long committeeId;
	
	private String committeeName;
	
	private String committeeDisplayName;
	
	private String committeeType;
	
	private Integer maxCommitteeMembers;
	
	private CommitteeMemberVO committeeChairman;
	
	private List<CommitteeMemberVO> committeeMembers;
	
	/**
	 * Comma separated names of committeeMembers
	 */
	private String committeeMembersName;
	
	private List<CommitteeMemberVO> invitedCommitteeMembers;
	
	/**
	 * Comma separated names of invitedCommitteeMembers
	 */
	private String invitedCommitteeMembersName;
	
	private List<PartyVO> rulingParties;
	
	private Integer rulingPartyCommitteeMembersCount;
	
	private List<PartyVO> oppositionParties;
	
	private Integer oppositionPartyCommitteeMembersCount;
	
	//=============== CONSTRUCTORS ==================
	public CommitteeVO() {
		super();
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

	public CommitteeMemberVO getCommitteeChairman() {
		return committeeChairman;
	}

	public void setCommitteeChairman(final CommitteeMemberVO committeeChairman) {
		this.committeeChairman = committeeChairman;
	}

	public List<CommitteeMemberVO> getCommitteeMembers() {
		return committeeMembers;
	}

	public void setCommitteeMembers(
			final List<CommitteeMemberVO> committeeMembers) {
		this.committeeMembers = committeeMembers;
	}

	public String getCommitteeMembersName() {
		return committeeMembersName;
	}

	public void setCommitteeMembersName(final String committeeMembersName) {
		this.committeeMembersName = committeeMembersName;
	}

	public List<CommitteeMemberVO> getInvitedCommitteeMembers() {
		return invitedCommitteeMembers;
	}

	public void setInvitedCommitteeMembers(
			final List<CommitteeMemberVO> invitedCommitteeMembers) {
		this.invitedCommitteeMembers = invitedCommitteeMembers;
	}

	public String getInvitedCommitteeMembersName() {
		return invitedCommitteeMembersName;
	}

	public void setInvitedCommitteeMembersName(
			final String invitedCommitteeMembersName) {
		this.invitedCommitteeMembersName = invitedCommitteeMembersName;
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