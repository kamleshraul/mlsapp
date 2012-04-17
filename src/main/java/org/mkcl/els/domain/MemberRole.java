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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.MemberRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberRole.
 * 
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "memberroles")
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

    @ManyToOne
    @JoinColumn(name = "housetype_id")
    private HouseType houseType;

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
     */

    public MemberRole(String name, Integer priority, HouseType houseType) {
        super();
        this.name = name;
        this.priority = priority;
        this.houseType = houseType;
    }

    // -------------------------------Domain_Methods--------------------------
    public static MemberRoleRepository getMemberRoleRepository() {
        MemberRoleRepository memberRoleRepository = new MemberRole().memberRoleRepository;
        if (memberRoleRepository == null) {
            throw new IllegalStateException(
                    "MemberRoleRepository has not been injected in MemberRole Domain");
        }
        return memberRoleRepository;
    }

    public static MemberRole findByNameHouseTypeLocale(String roleName,
            Long houseTypeId, String locale) {
        return getMemberRoleRepository().findByNameHouseTypeLocale(roleName,
                houseTypeId, locale);
    }
    
    public static List<MemberRole> findByHouseType(String houseType,
			String locale) {
		return getMemberRoleRepository().findByHouseType(houseType,
				locale);
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
    public void setName(String name) {
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
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

}
