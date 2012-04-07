/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.domain.Division
 * Created On: Apr 4, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Division.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_divisions")
@org.hibernate.annotations.Cache
(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
public class Division extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    @NotEmpty
    private String name;

    /** The state. */
    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new division.
     */
    public Division() {
        super();
    }

    /**
     * Instantiates a new division.
     *
     * @param name the name
     * @param state the state
     */
    public Division(final String name, final State state) {
        super();
        this.name = name;
        this.state = state;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // TO BE IMPLEMENTED
    /**
     * Find divisions by state id.
     *
     * @param stateId the state id
     * @param orderBy the order by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    public static List<Division> findDivisionsByStateId(final Long stateId,
            final String orderBy, final String sortOrder, final String locale) {
        // TODO Auto-generated method stub
        return null;
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
     * Gets the state.
     *
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    public void setState(final State state) {
        this.state = state;
    }

}
