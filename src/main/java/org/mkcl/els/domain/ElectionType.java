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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.ElectionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class ElectionType.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "electiontypes")
@JsonIgnoreProperties({"houseType"})
public class ElectionType extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 600)
    private String name;

    @ManyToOne
    @JoinColumn(name = "housetype_id")
    private HouseType houseType;

    @Autowired
    private transient ElectionTypeRepository electionTypeRepository;

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
    public static ElectionTypeRepository getElectionTypeRepository() {
    	ElectionTypeRepository electionTypeRepository = new ElectionType().electionTypeRepository;
        if (electionTypeRepository == null) {
            throw new IllegalStateException(
                    "ElectionTypeRepository has not been injected in ElectionType Domain");
        }
        return electionTypeRepository;
    }

    public static List<ElectionType> findByHouseType(final String strHouseType, final String locale) {
    	return getElectionTypeRepository().findByHouseType(strHouseType, locale);
    }

    // ------------------------------------------Getters/Setters-------------------------------
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(final HouseType houseType) {
        this.houseType = houseType;
    }

}
