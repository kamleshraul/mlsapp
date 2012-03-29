/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Constituency.java
 * Created On: Mar 20, 2012
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Constituency.
 * 
 * @author dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_constituencies")
public class Constituency extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    @NotEmpty
    private String name;

    /** The assembly council type. */
    @ManyToOne
    @JoinColumn(name = "house_id")
    private HouseType houseType;

    /** The districts. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "associations_constituency_district",
            joinColumns = @JoinColumn(name = "constituency_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "district_id",
                    referencedColumnName = "id"))
    private List<District> districts;

    /** The division. */
    @Column(length = 600)    
    private String divisionName;

    /** The voters. */
    private Integer voters;

    /** The is reserved. */
    @Column
    private Boolean isReserved=false;

    /** The reserved for. */
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservedFor;

    /** The nearest railway station. */
    @OneToOne
    @JoinColumn(name = "railwaystation_id")
    private RailwayStation nearestRailwayStation;

    /** The nearest airport. */
    @OneToOne
    @JoinColumn(name = "airport_id")
    private Airport nearestAirport;

    /** The is retired. */
    @Column
    private Boolean isRetired = false;

    /** The number. */
    @Column(length = 300)
    private String number;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new constituency.
     */
    public Constituency() {
        super();
    }

    // -------------------------------Domain_Methods----------------------------------------------

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
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the house type.
     * 
     * @return the house type
     */
    public HouseType getHouseType() {
        return houseType;
    }

    /**
     * Sets the house type.
     * 
     * @param houseType the new house type
     */
    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
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
    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    /**
     * Gets the division name.
     * 
     * @return the division name
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Sets the division name.
     * 
     * @param divisionName the new division name
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * Gets the voters.
     * 
     * @return the voters
     */
    public Integer getVoters() {
        return voters;
    }

    /**
     * Sets the voters.
     * 
     * @param voters the new voters
     */
    public void setVoters(Integer voters) {
        this.voters = voters;
    }
    
    /**
     * Sets the checks if is reserved.
     *
     * @param isReserved the new checks if is reserved
     */
    public void setIsReserved(Boolean isReserved) {
        this.isReserved = isReserved;
    }

    /**
     * Gets the reserved for.
     * 
     * @return the reserved for
     */
    public Reservation getReservedFor() {
        return reservedFor;
    }

    /**
     * Sets the reserved for.
     * 
     * @param reservedFor the new reserved for
     */
    public void setReservedFor(Reservation reservedFor) {
        this.reservedFor = reservedFor;
    }

    /**
     * Gets the nearest railway station.
     * 
     * @return the nearest railway station
     */
    public RailwayStation getNearestRailwayStation() {
        return nearestRailwayStation;
    }

    /**
     * Sets the nearest railway station.
     * 
     * @param nearestRailwayStation the new nearest railway station
     */
    public void setNearestRailwayStation(RailwayStation nearestRailwayStation) {
        this.nearestRailwayStation = nearestRailwayStation;
    }

    /**
     * Gets the nearest airport.
     * 
     * @return the nearest airport
     */
    public Airport getNearestAirport() {
        return nearestAirport;
    }

    /**
     * Sets the nearest airport.
     * 
     * @param nearestAirport the new nearest airport
     */
    public void setNearestAirport(Airport nearestAirport) {
        this.nearestAirport = nearestAirport;
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
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets the checks if is reserved.
     * 
     * @return the checks if is reserved
     */
    public Boolean getIsReserved() {
        return isReserved;
    }

    /**
     * Gets the checks if is retired.
     * 
     * @return the checks if is retired
     */
    public Boolean getIsRetired() {
        return isRetired;
    }

}
