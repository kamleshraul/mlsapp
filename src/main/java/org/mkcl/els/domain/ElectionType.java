/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ElectionType.java
 * Created On: Mar 15, 2012
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

/**
 * The Class ElectionType.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_electiontypes")
public class ElectionType extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 600)
    @NotEmpty
    private String name;

    @ManyToOne
    @JoinColumn(name = "housetype_id")
    private HouseType houseType;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new election type.
     */
    public ElectionType() {
        super();
    }

    /**
     * Instantiates a new election type.
     * 
     * @param electionType the election type
     * @param assemblycounciltype the assemblycounciltype
     */
    public ElectionType(final String name, final HouseType houseType) {
        super();
        this.name = name;
        this.houseType = houseType;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

}
