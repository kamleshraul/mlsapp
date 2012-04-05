/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberRole.java
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
import org.mkcl.els.repository.MemberRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class MemberRole.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_memberroles")
public class MemberRole extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    @NotEmpty
    private String name;

    /*
     * Member can have multiple roles but the role with highest priority only
     * need to be displayed in grid
     */

    /** The priority. */
    private Integer priority;

    /** The house type. */
    @ManyToOne
    @JoinColumn(name = "housetype_id")
    private HouseType houseType;

    /** The member role repository. */
    @Autowired
    private transient MemberRoleRepository memberRoleRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new member role.
     */
    public MemberRole() {
        super();
    }

    /**
     * Instantiates a new member role.
     *
     * @param name the name
     * @param priority the priority
     * @param houseType the house type
     */

    public MemberRole(final String name, final Integer priority, final HouseType houseType) {
        super();
        this.name = name;
        this.priority = priority;
        this.houseType = houseType;
    }

    // -------------------------------Domain_Methods--------------------------
    /**
     * Gets the member role repository.
     *
     * @return the member role repository
     */
    public static MemberRoleRepository getMemberRoleRepository() {
        MemberRoleRepository memberRoleRepository = new MemberRole().memberRoleRepository;
        if (memberRoleRepository == null) {
            throw new IllegalStateException(
                    "MemberRoleRepository has not been injected in MemberRole Domain");
        }
        return memberRoleRepository;
    }

    /**
     * Find by name house type locale.
     *
     * @param roleName the role name
     * @param houseTypeId the house type id
     * @param locale the locale
     * @return the member role
     */
    public static MemberRole findByNameHouseTypeLocale(final String roleName,
            final Long houseTypeId, final String locale) {
        return getMemberRoleRepository().findByNameHouseTypeLocale(roleName,
                houseTypeId, locale);
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
     * Gets the priority.
     *
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(final Integer priority) {
        this.priority = priority;
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

}
