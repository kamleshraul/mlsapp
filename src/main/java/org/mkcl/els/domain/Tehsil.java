/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Tehsil.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.TehsilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class Tehsil.
 *
 * @author amitb
 * @version v1.0.0
 */
@Configurable
@Entity
@Table(name = "tehsils")
public class Tehsil extends BaseDomain implements Serializable {

    // --------------------Attributes-------------------------------------------

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 100, nullable = false)
    @NotEmpty
    private String name;

    /** The district. */
    @ManyToOne
    private District district;

    // --------------------Constructors-------------------------------------------
    /**
     * Instantiates a new tehsil.
     */
    public Tehsil() {
        super();
    }

    /**
     * Instantiates a new tehsil.
     *
     * @param name the name
     * @param district the district
     */
    public Tehsil(final String name, final District district) {
        super();
        this.name = name;
        this.district = district;
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

    /** The tehsil repository. */
    @Autowired
    private transient TehsilRepository tehsilRepository;

    // -------------------------------Domain_Methods----------------------------------------------
    /**
     * Gets the repository.
     *
     * @return the repository
     */
    public static TehsilRepository getTehsilRepository() {
        TehsilRepository tehsilRepository = new Tehsil().tehsilRepository;
        if (tehsilRepository == null) {
            throw new IllegalStateException(
                    "TehsilRepository has not been injected in Tehsil Domail");
        }
        return tehsilRepository;
    }

    /**
     * Find tehsils by district name.
     *
     * @param name the name
     * @return the list< tehsil>
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional
    public static List<Tehsil> findTehsilsByDistrictName(final String name) {
        return getTehsilRepository().findTehsilsByDistrictName(name);
    }

    /**
     * Find tehsils by district id.
     *
     * @param districtId the district id
     * @return the list< tehsil>
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional
    public static List<Tehsil> findTehsilsByDistrictId(final Long districtId) {
        return getTehsilRepository().findTehsilsByDistrictId(districtId);
    }
}
