/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Constituency.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.ConstituencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class Constituency.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "constituencies")
public class Constituency extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 100, nullable = false)
    @NotEmpty
    private String name;

    /** The number. */
    @Column(length = 100)
    private String number;

    /** The districts. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "constituency_district", joinColumns = @JoinColumn(
            name = "constituency_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "district_id",
                    referencedColumnName = "id"))
    @NotNull
    private List<District> districts;

    /** The reserved. */
    private Boolean reserved = false;

    /** The constituency repository. */
    @Autowired
    private transient ConstituencyRepository constituencyRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new constituency.
     */
    public Constituency() {
        super();
    }

    /**
     * Instantiates a new constituency.
     *
     * @param name the name
     * @param number the number
     * @param districts the districts
     * @param reserved the reserved
     */
    public Constituency(final String name,
            final String number,
            final List<District> districts,
            final boolean reserved) {
        super();
        this.name = name;
        this.number = number;
        this.districts = districts;
        this.reserved = reserved;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    /**
     * Gets the constituency repository.
     *
     * @return the constituency repository
     */
    public static ConstituencyRepository getConstituencyRepository() {
        final ConstituencyRepository repository = new Constituency().constituencyRepository;
        if (repository == null) {
            throw new IllegalStateException(
                    "ConstituencyRepository has not been injected");
        }
        return repository;
    }

    /**
     * Find constituencies starting with.
     *
     * @param param the param
     * @param locale the locale
     * @return the list
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static List<Reference> findConstituenciesStartingWith(final String param,
                                                                 final String locale) {
        return getConstituencyRepository().findConstituenciesRefStartingWith(
                param, locale);
    }

    /**
     * Find constituencies by district id.
     *
     * @param districtId the district id
     * @return the list
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static List<Constituency> findConstituenciesByDistrictId(final Long districtId) {
        return getConstituencyRepository().findConstituenciesByDistrictId(
                districtId);
    }

    /**
     * Find constituencies by district name.
     *
     * @param name the name
     * @param locale the locale
     * @return the list
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static List<Constituency> findConstituenciesByDistrictName(final String name,
                                                                      final String locale) {
        return getConstituencyRepository().findConstituenciesByDistrictName(
                name, locale);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getName();
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
     * Gets the number.
     *
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number.
     *
     * @param number the new number
     */
    public void setNumber(final String number) {
        this.number = number;
    }

    /**
     * Gets the districts.
     *
     * @return the districts
     */
    public List<District> getDistricts() {
        return districts;
    }

    /**
     * Sets the districts.
     *
     * @param districts the new districts
     */
    public void setDistricts(final List<District> districts) {
        this.districts = districts;
    }

    /**
     * Checks if is reserved.
     *
     * @return true, if is reserved
     */
    public boolean isReserved() {
        return reserved;
    }

    /**
     * Sets the reserved.
     *
     * @param reserved the new reserved
     */
    public void setReserved(final boolean reserved) {
        this.reserved = reserved;
    }
}
