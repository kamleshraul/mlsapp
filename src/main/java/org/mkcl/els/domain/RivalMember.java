/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.RivalMember.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.common.util.FormaterUtil;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class RivalMember.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "rival_members")
public class RivalMember extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The votes received. */
    private Integer votesReceived;

    /** The party. */
    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

    @ManyToOne
    @JoinTable(name = "electionresults_rivalmembers",
            joinColumns = { @JoinColumn(name = "rivalmember_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "electionresult_id",
                    referencedColumnName = "id") })
    private ElectionResult electionResult;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new rival member.
     */
    public RivalMember() {
        super();
    }

    /**
     * Instantiates a new rival member.
     *
     * @param name the name
     * @param votesReceived the votes received
     * @param party the party
     */
    public RivalMember(final String name, final Integer votesReceived, final Party party) {
        super();
        this.name = name;
        this.votesReceived = votesReceived;
        this.party = party;
    }

    // -------------------------------Domain_Methods--------------------------------------
    public String formatVotesReceived(){
    	if(this.votesReceived!=null){
            return FormaterUtil.formatNumberNoGrouping(this.votesReceived, this.getLocale());
        } else{
        	return "";
        }
    }
    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
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
     * Gets the party.
     *
     * @return the party
     */
    public Party getParty() {
        return party;
    }

    /**
     * Sets the party.
     *
     * @param party the new party
     */
    public void setParty(final Party party) {
        this.party = party;
    }


    public ElectionResult getElectionResult() {
        return electionResult;
    }
}
