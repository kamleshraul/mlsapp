/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.RailwayStation.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class RailwayStation.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_railwaystations")
public class RailwayStation extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    @NotEmpty
    private String name;

    /** The district. */
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new railway station.
     */
    public RailwayStation() {
        super();
    }

    /**
     * Instantiates a new railway station.
     * 
     * @param name the name
     * @param district the district
     */
    public RailwayStation(final String name, final District district) {
        super();
        this.name = name;
        this.district = district;
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
    public void setName(final String name) {
        this.name = name;
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

}
