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

    @Column(length = 100)
    private String city;

    @Column(length = 30)
    private String pincode;

    @OneToOne
    @JoinColumn(name = "tehsil_id")
    private Tehsil tehsil;

    @OneToOne
    @JoinColumn(name = "district_id")
    private District district;

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
    public Address(String details, String city, String pincode, Tehsil tehsil,
            District district, State state) {
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Tehsil getTehsil() {
        return tehsil;
    }

    public void setTehsil(Tehsil tehsil) {
        this.tehsil = tehsil;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
