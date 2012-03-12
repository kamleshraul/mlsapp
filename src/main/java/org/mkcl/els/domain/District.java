/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.District.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class District.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "districts")
public class District extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 100)
    @NotEmpty
    private String name;

    /** The state. */
    @ManyToOne
    private State state;

    /** The district repository. */
    @Autowired
    private transient DistrictRepository districtRepository;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new district.
     */
    public District() {
        super();
    }

    /**
     * Instantiates a new district.
     *
     * @param name the name
     * @param state the state
     */
    public District(final String name, final State state) {
        super();
        this.name = name;
        this.state = state;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    /**
     * Gets the district repository.
     *
     * @return the district repository
     */
    public static DistrictRepository getDistrictRepository() {
        DistrictRepository districtRepository = new District().districtRepository;
        if (districtRepository == null) {
            throw new IllegalStateException(
                    "AssemblyRepository has not been injected in Assembly Domain");
        }
        return districtRepository;
    }

    /**
     * Find districts by state id.
     *
     * @param stateid the stateid
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the list
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static List<District> findDistrictsByStateId(final Long stateid,
                                                        final String sortBy,
                                                        final String sortOrder) {
        return getDistrictRepository().findDistrictsByStateId(
                stateid, sortBy, sortOrder);
    }

    /**
     * Find districts by state name.
     *
     * @param stateName the state name
     * @param orderBy the order by
     * @param sortOrder the sort order
     * @return the list
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static List<District> findDistrictsByStateName(final String stateName,
                                                          final String orderBy,
                                                          final String sortOrder) {
        return getDistrictRepository().findDistrictsByStateName(
                stateName, orderBy, sortOrder);
    }

    /**
     * Find districts by constituency id.
     *
     * @param constituencyId the constituency id
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the list
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static List<District> findDistrictsByConstituencyId(final Long constituencyId,
                                                               final String sortBy,
                                                               final String sortOrder) {
        return getDistrictRepository().findDistrictsByConstituencyId(
                constituencyId, sortBy, sortOrder);
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
