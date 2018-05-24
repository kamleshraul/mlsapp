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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<HouseType> findAll(final Class persistenceClass,
            final String sortBy, final String sortOrder, final String locale) {
    return getHouseTypeRepository().findAll(persistenceClass, sortBy, sortOrder, locale);
    }

    public static HouseType findByType(final String houseTypeType,
    		final String locale) {
    	HouseType houseType = HouseType.findByFieldName(HouseType.class, 
    			"type", houseTypeType, locale);
    	return houseType;
    }
    
    public static HouseType findByName(final String houseTypeName,
    		final String locale) {
    	HouseType houseType = 
    		HouseType.findByName(HouseType.class, houseTypeName, locale);
    	return houseType;
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
    
    public void setType(String type) {
    	this.type = type;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
