/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Zillaparishad.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.ZillaparishadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class Zillaparishad.
 *
 * @author dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "zillaparishads")
public class Zillaparishad extends BaseDomain implements Serializable {

    // --------------------Attributes-------------------------------------------

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The district. */
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @Autowired
    private transient ZillaparishadRepository zillaparishadRepository;

    // --------------------Constructors-------------------------------------------

    /**
     * Instantiates a new Zillaparishad.
     */
    public Zillaparishad() {
        super();
    }

    /**
     * Instantiates a new Zillaparishad.
     *
     * @param name the name
     * @param district the district
     */
    public Zillaparishad(final String name, final District district) {
        super();
        this.name = name;
        this.district = district;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    public static ZillaparishadRepository getZillaparishadRepository() {
    	ZillaparishadRepository zillaparishadRepository = new Zillaparishad().zillaparishadRepository;
        if (zillaparishadRepository == null) {
            throw new IllegalStateException(
                    "ZillaparishadRepository has not been injected in Zillaparishad Domain");
        }
        return zillaparishadRepository;
    }

    public static List<org.mkcl.els.common.vo.Reference> findZillaparishadsRefByDistrictId(final Long districtId,
			final String sortBy, final String sortOrder, final String locale) throws ELSException {
		return getZillaparishadRepository().findZillaparishadsRefByDistrictId(districtId,sortBy,sortOrder,locale);
	}

	@Transactional(readOnly = true)
	public static List<Zillaparishad> findZillaparishadsByDistrictId(final Long districtId,
			final String sortBy, final String sortOrder, final String locale) throws ELSException {
		return getZillaparishadRepository().findZillaparishadsByDistrictId(districtId, sortBy,
				sortOrder, locale);
	}
    public static List<Zillaparishad> findByState(final Long stateId, final String locale) throws ELSException {
		return getZillaparishadRepository().findByState(stateId,locale);
	}
    
    public static List<Zillaparishad> find(final District district, 
			final String locale) {
		return getZillaparishadRepository().find(district, locale);
	}
    
    public static List<Zillaparishad> findZillaparishadsbyDistricts(
			final String[] districts, final String locale) {
		return getZillaparishadRepository().findZillaparishadsbyDistricts(districts, locale);
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
