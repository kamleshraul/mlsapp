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
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.repository.ConstituencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Constituency.
 *
 * @author dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "constituencies")
@JsonIgnoreProperties({ "houseType", "districts", "reservedFor", "nearestRailwayStation",
"nearestAirport" })
public class Constituency extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The assembly council type. */
    @ManyToOne
    @JoinColumn(name = "housetype_id")
    private HouseType houseType;

    /** The districts. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "constituencies_districts",
    joinColumns = @JoinColumn(name = "constituency_id",
    referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "district_id",
    referencedColumnName = "id"))
    private List<District> districts;

    /** The division. */
    @Column(length = 10000)
    private String displayName;

    /** The voters. */
    private Integer voters;

    /** The is reserved. */

    private  Boolean isReserved;

    /** The reserved for. */
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservedFor;

    /** The nearest railway station. */
    @ManyToOne
    @JoinColumn(name = "railwaystation_id")
    private RailwayStation nearestRailwayStation;

    /** The nearest airport. */
    @ManyToOne
    @JoinColumn(name = "airport_id")
    private Airport nearestAirport;

    /** The is retired. */
    @Column
    private  Boolean isRetired;

    /** The number. */
    @Column(length = 300)
    private String number;

    @Column(length=500)
    private String divisionName;

    @ManyToOne
    @JoinColumn(name = "upperhouseconstituencytype_id")
    private UpperHouseConstituencyType upperHouseConstituencyType;

    /** The constituency repository. */
    @Autowired
    private transient ConstituencyRepository constituencyRepository;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new constituency.
     */
    public Constituency() {
        super();
        this.isReserved=false;
        this.isRetired=false;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    /**
     * Gets the constituency repository.
     *
     * @return the constituency repository
     */
    public static ConstituencyRepository getConstituencyRepository() {
        ConstituencyRepository constituencyRepository = new Constituency().constituencyRepository;
        if (constituencyRepository == null) {
            throw new IllegalStateException(
                    "ConstituencyRepository has not been injected in Constituency Domain");
        }
        return constituencyRepository;
    }

    // This is used in MemberElectionController
    /**
     * Find by default state and house type.
     *
     * @param defaultState the default state
     * @param houseType the house type
     * @param locale the locale
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the list
     */
    public static List<MasterVO> findVOByDefaultStateAndHouseType(
            final String defaultState, final String houseType, final String locale,
            final String sortBy, final String sortOrder) {
        return getConstituencyRepository().findVOByDefaultStateAndHouseType(
                defaultState, houseType, locale, sortBy, sortOrder);
    }

    public static List<MasterVO> findAllByHouseType(final String houseType, final String locale) {
		return getConstituencyRepository().findAllByHouseType(houseType,locale) ;
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
    public void setHouseType(final HouseType houseType) {
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
    public void setDistricts(final List<District> districts) {
        this.districts = districts;
    }

    public String getDisplayName() {
        return displayName;
    }


    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
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
    public void setVoters(final Integer voters) {
        this.voters = voters;
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
    public void setReservedFor(final Reservation reservedFor) {
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
    public void setNearestRailwayStation(final RailwayStation nearestRailwayStation) {
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
    public void setNearestAirport(final Airport nearestAirport) {
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
    public void setNumber(final String number) {
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

    public void setIsReserved(final Boolean isReserved) {
		this.isReserved = isReserved;
	}

	public void setIsRetired(final Boolean isRetired) {
		this.isRetired = isRetired;
	}

	/**
     * Gets the checks if is retired.
     *
     * @return the checks if is retired
     */
    public Boolean getIsRetired() {
        return isRetired;
    }

	public static List<Constituency> findAllByDisplayName(final HouseType houseType,
			final String displayName, final String locale) {
		return getConstituencyRepository().findAllByDisplayName(houseType,
				displayName, locale);
	}


    public String getDivisionName() {
        return divisionName;
    }


    public void setDivisionName(final String divisionName) {
        this.divisionName = divisionName;
    }


    public UpperHouseConstituencyType getUpperHouseConstituencyType() {
        return upperHouseConstituencyType;
    }


    public void setUpperHouseConstituencyType(
            final UpperHouseConstituencyType upperHouseConstituencyType) {
        this.upperHouseConstituencyType = upperHouseConstituencyType;
    }
}
