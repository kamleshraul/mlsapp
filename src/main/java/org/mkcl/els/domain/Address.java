/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Address.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Address.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "addresses")
public class Address extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 255)
    private String details; // for Address field in class diagram

    /** The city. */
    @Column(length = 100)
    private String city;

    /** The pincode. */
    @Column(length = 30)
    private String pincode;

    /** The tehsil. */
    @OneToOne
    @JoinColumn(name = "tehsil_id")
    private Tehsil tehsil;

    /** The district. */
    @OneToOne
    @JoinColumn(name = "district_id")
    private District district;

    /** The state. */
    @OneToOne
    @JoinColumn(name = "state_id")
    private State state;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new address.
     */
    public Address() {
        super();
    }

    /**
     * Instantiates a new address.
     *
     * @param details the details
     * @param city the city
     * @param pincode the pincode
     * @param tehsil the tehsil
     * @param district the district
     * @param state the state
     */
    public Address(
            final String details,
            final String city,
            final String pincode,
            final Tehsil tehsil,
            final District district,
            final State state) {
        super();
        this.details = details;
        this.city = city;
        this.pincode = pincode;
        this.tehsil = tehsil;
        this.district = district;
        this.state = state;
    }

    // -------------------------------Domain_Methods--------------------------

    // ------------------------------------------Getters/Setters-----------------------------------

    /**
     * Gets the details.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the details.
     *
     * @param details the new details
     */
    public void setDetails(final String details) {
        this.details = details;
    }

    /**
     * Gets the city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the new city
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * Gets the pincode.
     *
     * @return the pincode
     */
    public String getPincode() {
        return pincode;
    }

    /**
     * Sets the pincode.
     *
     * @param pincode the new pincode
     */
    public void setPincode(final String pincode) {
        this.pincode = pincode;
    }

    /**
     * Gets the tehsil.
     *
     * @return the tehsil
     */
    public Tehsil getTehsil() {
        return tehsil;
    }

    /**
     * Sets the tehsil.
     *
     * @param tehsil the new tehsil
     */
    public void setTehsil(final Tehsil tehsil) {
        this.tehsil = tehsil;
    }

    /**
     * Gets the district.
     *
     * @return the district
     */
    public District getDistrict() {
        return district;
    }

    /**
     * Sets the district.
     *
     * @param district the new district
     */
    public void setDistrict(final District district) {
        this.district = district;
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
