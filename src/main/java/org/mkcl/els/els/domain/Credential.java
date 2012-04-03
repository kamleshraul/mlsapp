/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Credential.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Credential.
 * 
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Configurable
public class Credential extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The username. */
    @Column(length = 50)
    private String username;

    /** The password. */
    @Column(length = 20)
    private String password;

    /** The enabled. */
    private boolean enabled;

    /** The member type. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "associations_credential_housetype", joinColumns = @JoinColumn(
            name = "credential_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "housetype_id",
                    referencedColumnName = "id"))
    private Set<HouseType> houseTypes;
    //This field needs to be stored to the usertype of the Person object.This will be used to instantiate 
    //appropriate class type in SecurityServiceImpl.
    @Column(length=50)
    private String userType;

    /** The roles. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "associations_role_membership", joinColumns = @JoinColumn(
            name = "credential_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",
                    referencedColumnName = "id"))
    private Set<Role> roles;

    /** The members. */
    @OneToMany(mappedBy = "credential")
    private List<Member> members;
    
    //This field will store the locales in which the user can see his information
    @Column(length=50)
    private String allowedLocales;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
    
    //This will just refer to lowerhouse and upperhouse and will be independent of locale specific names
    private String defaultHouseType;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new credential.
     */
    public Credential() {
        super();
    }

    

    public Credential(String username, String password, boolean enabled,
			Set<HouseType> houseTypes, String userType, Set<Role> roles,
			List<Member> members, String allowedLocales, Date lastLoginTime,
			String defaultHouseType) {
		super();
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.houseTypes = houseTypes;
		this.userType = userType;
		this.roles = roles;
		this.members = members;
		this.allowedLocales = allowedLocales;
		this.lastLoginTime = lastLoginTime;
		this.defaultHouseType = defaultHouseType;
	}



	// -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------

    public String getDefaultHouseType() {
		return defaultHouseType;
	}



	public void setDefaultHouseType(String defaultHouseType) {
		this.defaultHouseType = defaultHouseType;
	}



	/**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if is enabled.
     * 
     * @return true, if is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     * 
     * @param enabled the new enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the roles.
     * 
     * @return the roles
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the roles.
     * 
     * @param roles the new roles
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

   
    public Set<HouseType> getHouseTypes() {
		return houseTypes;
	}

	public void setHouseTypes(Set<HouseType> houseTypes) {
		this.houseTypes = houseTypes;
	}

	/**
     * Gets the members.
     * 
     * @return the members
     */
    public List<Member> getMembers() {
        return members;
    }

    /**
     * Sets the members.
     * 
     * @param members the new members
     */
    public void setMembers(List<Member> members) {
        this.members = members;
    }

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getAllowedLocales() {
		return allowedLocales;
	}

	public void setAllowedLocales(String allowedLocales) {
		this.allowedLocales = allowedLocales;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

}
