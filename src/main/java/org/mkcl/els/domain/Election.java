/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Election.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Election.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "elections")
@JsonIgnoreProperties({"electionType"})
public class Election extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The election type. */
    @ManyToOne
    @JoinColumn(name = "electiontype_id")
    private ElectionType electionType;

    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Temporal(TemporalType.DATE)
    private Date toDate;

    @Autowired
    private transient ElectionRepository electionRepository;


    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new election.
     */
    public Election() {
        super();
    }

    /**
     * Instantiates a new election.
     *
     * @param name the name
     * @param electionType the election type
     * @param fromDate the from date
     * @param toDate the to date
     */
    public Election(final String name, final ElectionType electionType, final Date fromDate,
            final Date toDate) {
        super();
        this.name = name;
        this.electionType = electionType;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    // -------------------------------Domain_Methods--------------------------------------
    public static ElectionRepository getElectionRepository() {
    	ElectionRepository electionRepository = new Election().electionRepository;
        if (electionRepository == null) {
            throw new IllegalStateException(
                    "ElectionRepository has not been injected in Election Domain");
        }
        return electionRepository;
    }

    public static List<Election> findByHouseType(final String houseType,final String locale) {
        return getElectionRepository().findByHouseType(houseType,locale);
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
     * Gets the election type.
     *
     * @return the election type
     */
    public ElectionType getElectionType() {
        return electionType;
    }

    /**
     * Sets the election type.
     *
     * @param electionType the new election type
     */
    public void setElectionType(final ElectionType electionType) {
        this.electionType = electionType;
    }

    /**
     * Gets the from date.
     *
     * @return the from date
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Sets the from date.
     *
     * @param fromDate the new from date
     */
    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets the to date.
     *
     * @param toDate the new to date
     */
    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }



}
