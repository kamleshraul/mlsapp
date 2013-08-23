/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Role.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Role.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "roles")
@JsonIgnoreProperties("credentials")
public class Role extends BaseDomain implements Serializable {

    // Attributes-----------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 500)
    private String localizedName;
    
    private String name;

    @Column(length=250)
    private String type;

    @ManyToMany
    @JoinTable(name = "credentials_roles", joinColumns =
    @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "credential_id",
                    referencedColumnName = "id"))
    private Set<Credential> credentials = new HashSet<Credential>();
    
    @Column(length=5000)
    private String menusAllowed;

    @Autowired
    private transient RoleRepository roleRepository;
    // ---------------- Constructors
    // ------------------------------------------------------------------------------
    /**
     * Instantiates a new role.
     */
    public Role() {

    }

    /**
     * Instantiates a new role.
     *
     * @param name the name
     */

    public Role(final String name, final String type, final Set<Credential> credentials) {
		super();
		this.name = name;
		this.type = type;
		this.credentials = credentials;
	}

    // -------------- Domain Methods --------------------------------------------------------------------------


    public static RoleRepository getRoleRepository() {
    	RoleRepository roleRepository = new Role().roleRepository;
        if (roleRepository == null) {
            throw new IllegalStateException(
                    "RoleRepository has not been injected in Role Domain");
        }
        return roleRepository;
    }

    public static List<Role> findRolesByRoleType(
            final Class persistenceClass, final String fieldName, final String fieldValue,
            final String sortBy, final String sortOrder) throws ELSException {
        return getRoleRepository().findRolesByRoleType(persistenceClass,fieldName,fieldValue,sortBy, sortOrder);
    }

    // -------------- Getters & Setters
    // --------------------------------------------------------------------------
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

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public Set<Credential> getCredentials() {
		return credentials;
	}

	public void setCredentials(final Set<Credential> credentials) {
		this.credentials = credentials;
	}

	public void setMenusAllowed(String menusAllowed) {
		this.menusAllowed = menusAllowed;
	}

	public String getMenusAllowed() {
		return menusAllowed;
	}

	public void setLocalizedName(String localizedName) {
		this.localizedName = localizedName;
	}

	public String getLocalizedName() {
		return localizedName;
	}

	public static String findDelimitedQISRoles(final String locale) throws ELSException {
		return getRoleRepository().findDelimitedQISRoles(locale);
	}





}
