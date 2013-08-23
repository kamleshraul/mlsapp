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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Credential.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Configurable
@Table(name = "credentials")
@JsonIgnoreProperties({"roles","userGroups"})
public class Credential extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The username. */
    @Column(length = 100)
    private String username;

    /** The password. */
    @Column(length = 20)
    private String password;

    /** The enabled. */
    private boolean enabled;

    /** The email. */
    @Column(length = 200)
    private String email;

    /** The roles. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "credentials_roles", joinColumns = @JoinColumn(
            name = "credential_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",
            referencedColumnName = "id"))
    private Set<Role> roles=new HashSet<Role>();

    /** The last login time. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
    

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new credential.
     */
    public Credential() {
        super();
    }

    /**
     * Instantiates a new credential.
     *
     * @param username the username
     * @param password the password
     * @param enabled the enabled
     * @param roles the roles
     * @param lastLoginTime the last login time
     */
    public Credential(final String username, final String password, final boolean enabled,
            final Set<Role> roles, final Date lastLoginTime,final Set<UserGroup> userGroups) {
        super();
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
        this.lastLoginTime = lastLoginTime;
    }
    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------
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
    public void setUsername(final String username) {
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
    public void setPassword(final String password) {
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
    public void setEnabled(final boolean enabled) {
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
    public void setRoles(final Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Gets the last login time.
     *
     * @return the last login time
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * Sets the last login time.
     *
     * @param lastLoginTime the new last login time
     */
    public void setLastLoginTime(final Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

}
