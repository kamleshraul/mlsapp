/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.ElectionResultVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;

/**
 * The Class ElectionResultVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class ElectionResultVO {

	/** The election. */
	private String election;

	/** The election type. */
	private String electionType;

	/** The constituency. */
	private String constituency;

	/** The voting date. */
	private String votingDate;

	/** The no of voters. */
	private String noOfVoters;

    /** The valid votes. */
    private String validVotes;

    /** The votes received. */
    private String votesReceived;

    /** The election result date. */
    private String electionResultDate;

    /** The rival members. */
    private List<RivalMemberVO> rivalMembers;

	/**
	 * Gets the no of voters.
	 *
	 * @return the no of voters
	 */
	public String getNoOfVoters() {
		return noOfVoters;
	}

	/**
	 * Sets the no of voters.
	 *
	 * @param noOfVoters the new no of voters
	 */
	public void setNoOfVoters(final String noOfVoters) {
		this.noOfVoters = noOfVoters;
	}

	/**
	 * Gets the valid votes.
	 *
	 * @return the valid votes
	 */
	public String getValidVotes() {
		return validVotes;
	}

	/**
	 * Sets the valid votes.
	 *
	 * @param validVotes the new valid votes
	 */
	public void setValidVotes(final String validVotes) {
		this.validVotes = validVotes;
	}

	/**
	 * Gets the votes received.
	 *
	 * @return the votes received
	 */
	public String getVotesReceived() {
		return votesReceived;
	}

	/**
	 * Sets the votes received.
	 *
	 * @param votesReceived the new votes received
	 */
	public void setVotesReceived(final String votesReceived) {
		this.votesReceived = votesReceived;
	}

	/**
	 * Gets the rival members.
	 *
	 * @return the rival members
	 */
	public List<RivalMemberVO> getRivalMembers() {
		return rivalMembers;
	}

	/**
	 * Sets the rival members.
	 *
	 * @param rivalMembers the new rival members
	 */
	public void setRivalMembers(final List<RivalMemberVO> rivalMembers) {
		this.rivalMembers = rivalMembers;
	}

	/**
	 * Gets the election.
	 *
	 * @return the election
	 */
	public String getElection() {
		return election;
	}

	/**
	 * Sets the election.
	 *
	 * @param election the new election
	 */
	public void setElection(final String election) {
		this.election = election;
	}

	/**
	 * Gets the constituency.
	 *
	 * @return the constituency
	 */
	public String getConstituency() {
		return constituency;
	}

	/**
	 * Sets the constituency.
	 *
	 * @param constituency the new constituency
	 */
	public void setConstituency(final String constituency) {
		this.constituency = constituency;
	}

	/**
	 * Gets the voting date.
	 *
	 * @return the voting date
	 */
	public String getVotingDate() {
		return votingDate;
	}

	/**
	 * Sets the voting date.
	 *
	 * @param votingDate the new voting date
	 */
	public void setVotingDate(final String votingDate) {
		this.votingDate = votingDate;
	}

	/**
	 * Gets the election result date.
	 *
	 * @return the election result date
	 */
	public String getElectionResultDate() {
		return electionResultDate;
	}

	/**
	 * Sets the election result date.
	 *
	 * @param electionResultDate the new election result date
	 */
	public void setElectionResultDate(final String electionResultDate) {
		this.electionResultDate = electionResultDate;
	}

	/**
	 * Gets the election type.
	 *
	 * @return the election type
	 */
	public String getElectionType() {
		return electionType;
	}

	/**
	 * Sets the election type.
	 *
	 * @param electionType the new election type
	 */
	public void setElectionType(final String electionType) {
		this.electionType = electionType;
	}
}
