/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.AssemblyCouncilType.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.repository.HouseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class AssemblyCouncilType.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "housetypes")
public class HouseType extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 150)
    private String type;

    @Column(length=600)
    private String name;

    @Autowired
    private transient HouseTypeRepository houseTypeRepository;

    // ---------------------------------Constructors----------------------------------------------

    public HouseType() {
        super();
    }

    public HouseType(final String type, final String name) {
		super();
		this.type = type;
		this.name = name;
	}

    public static HouseTypeRepository getHouseTypeRepository() {
        HouseTypeRepository houseTypeRepository = new HouseType().houseTypeRepository;
        if (houseTypeRepository == null) {
            throw new IllegalStateException(
                    "HouseTypeRepository has not been injected in HouseType Domain");
        }
        return houseTypeRepository;
    }

    public static List<HouseType> findAllNoExclude(final String sortBy, final String sortOrder, final String locale){
        return getHouseTypeRepository().findAllNoExclude(sortBy, sortOrder, locale);
    }

    @SuppressWarnings("unchecked")
    public static List<HouseType> findAll(final Class persistenceClass,
            final String sortBy, final String sortOrder, final String locale) {
    return getHouseTypeRepository().findAll(persistenceClass, sortBy, sortOrder, locale);
    }

	// -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

	public String getName() {
		return name;
	}
}
