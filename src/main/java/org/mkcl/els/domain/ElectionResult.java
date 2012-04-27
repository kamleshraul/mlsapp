/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ElectionResult.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class ElectionResult.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "election_results")
@JsonIgnoreProperties({"rivalMembers","member"})
public class ElectionResult extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The election. */
    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    /** The constituency. */
    @ManyToOne
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;

    /** The voting date. */
    @Temporal(TemporalType.DATE)
    private Date votingDate;

    /** The total valid votes. */
    private Integer totalValidVotes;

    /** The votes received. */
    private Integer votesReceived;

    /** The rival members. */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "electionresults_rivalmembers",
            joinColumns = { @JoinColumn(name = "electionresult_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "rivalmember_id",
                    referencedColumnName = "id") })
    private List<RivalMember> rivalMembers;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new election result.
     */
    public ElectionResult() {
        super();
    }

    /**
     * Instantiates a new election result.
     *
     * @param election the election
     * @param constituency the constituency
     * @param votingDate the voting date
     * @param totalValidVotes the total valid votes
     * @param votesReceived the votes received
     */
    public ElectionResult(final Election election, final Constituency constituency,
            final Date votingDate, final Integer totalValidVotes, final Integer votesReceived) {
        super();
        this.election = election;
        this.constituency = constituency;
        this.votingDate = votingDate;
        this.totalValidVotes = totalValidVotes;
        this.votesReceived = votesReceived;
    }

    // -------------------------------Domain_Methods--------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the election.
     *
     * @return the election
     */
    public Election getElection() {
        return election;
    }

    /**
     * Sets the election.
     *
     * @param election the new election
     */
    public void setElection(final Election election) {
        this.election = election;
    }

    /**
     * Gets the constituency.
     *
     * @return the constituency
     */
    public Constituency getConstituency() {
        return constituency;
    }

    /**
     * Sets the constituency.
     *
     * @param constituency the new constituency
     */
    public void setConstituency(final Constituency constituency) {
        this.constituency = constituency;
    }

    /**
     * Gets the voting date.
     *
     * @return the voting date
     */
    public Date getVotingDate() {
        return votingDate;
    }

    /**
     * Sets the voting date.
     *
     * @param votingDate the new voting date
     */
    public void setVotingDate(final Date votingDate) {
        this.votingDate = votingDate;
    }

    /**
     * Gets the total valid votes.
     *
     * @return the total valid votes
     */
    public Integer getTotalValidVotes() {
        return totalValidVotes;
    }

    /**
     * Sets the total valid votes.
     *
     * @param totalValidVotes the new total valid votes
     */
    public void setTotalValidVotes(final Integer totalValidVotes) {
        this.totalValidVotes = totalValidVotes;
    }

    /**
     * Gets the votes received.
     *
     * @return the votes received
     */
    public Integer getVotesReceived() {
        return votesReceived;
    }

    /**
     * Sets the votes received.
     *
     * @param votesReceived the new votes received
     */
    public void setVotesReceived(final Integer votesReceived) {
        this.votesReceived = votesReceived;
    }

    /**
     * Gets the rival members.
     *
     * @return the rival members
     */
    public List<RivalMember> getRivalMembers() {
        return rivalMembers;
    }

    /**
     * Sets the rival members.
     *
     * @param rivalMembers the new rival members
     */
    public void setRivalMembers(final List<RivalMember> rivalMembers) {
        this.rivalMembers = rivalMembers;
    }

	public Member getMember() {
		return member;
	}

	public void setMember(final Member member) {
		this.member = member;
	}

}
