package org.mkcl.els.common.vo;

import java.util.List;

public class ElectionResultVO {
	
	private String election;
	
	private String electionType;
	
	private String constituency;
	
	private String votingDate;
	
	private String noOfVoters;
	
    private String validVotes;

    private String votesReceived;
    
    private String electionResultDate;

    private List<RivalMemberVO> rivalMembers;

	public String getNoOfVoters() {
		return noOfVoters;
	}

	public void setNoOfVoters(String noOfVoters) {
		this.noOfVoters = noOfVoters;
	}

	public String getValidVotes() {
		return validVotes;
	}

	public void setValidVotes(String validVotes) {
		this.validVotes = validVotes;
	}

	public String getVotesReceived() {
		return votesReceived;
	}

	public void setVotesReceived(String votesReceived) {
		this.votesReceived = votesReceived;
	}

	public List<RivalMemberVO> getRivalMembers() {
		return rivalMembers;
	}

	public void setRivalMembers(List<RivalMemberVO> rivalMembers) {
		this.rivalMembers = rivalMembers;
	}

	public String getElection() {
		return election;
	}

	public void setElection(String election) {
		this.election = election;
	}

	public String getConstituency() {
		return constituency;
	}

	public void setConstituency(String constituency) {
		this.constituency = constituency;
	}

	public String getVotingDate() {
		return votingDate;
	}

	public void setVotingDate(String votingDate) {
		this.votingDate = votingDate;
	}

	public String getElectionResultDate() {
		return electionResultDate;
	}

	public void setElectionResultDate(String electionResultDate) {
		this.electionResultDate = electionResultDate;
	}

	public String getElectionType() {
		return electionType;
	}

	public void setElectionType(String electionType) {
		this.electionType = electionType;
	}		
}
