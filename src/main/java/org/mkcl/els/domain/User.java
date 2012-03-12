/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.User.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.common.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class User.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "users")
public class User extends BaseDomain implements Serializable {

    // Attributes
    // --------------------------------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The code. */
    @Column(length = 20)
    private String code;

    /** The username. */
    @Column(length = 20)
    @NotEmpty
    private String username;

    /** The password. */
    @Column(length = 20)
    @NotEmpty
    private String password;

    /** The enabled. */
    private boolean enabled = true;

    /** The first name. */
    @Column(length = 50)
    @NotEmpty
    private String firstName;

    /** The first name. */
    @Column(length = 50)
    @NotEmpty
    private String middleName;

    /** The last name. */
    @Column(length = 50)
    @NotEmpty
    private String lastName;

    /** The email. */
    @Column(length = 50)
    @NotEmpty
    @Email
    private String email;

    /** The email. */
    @Column(length = 15)
    private String mobile;

    /** The last login time. */
    private Date lastLoginTime;

    /** The roles. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_membership", joinColumns = @JoinColumn(
            name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",
                    referencedColumnName = "id"))
    private Set<Role> roles;

    // constructor
    // --------------------------------------------------------------------------

    /**
     * Instantiates a new user.
     */
    public User() {
    }

    /**
     * Instantiates a new user.
     *
     * @param username the username
     * @param password the password
     * @param enabled the enabled
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email
     */
    public User(final String username,
            final String password,
            final boolean enabled,
            final String firstName,
            final String lastName,
            final String email) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // ******************Getters & Setters ******************************/

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
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
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middle name.
     *
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name.
     *
     * @param middleName the new middle name
     */
    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
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

    /**
     * Gets the mobile.
     *
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets the mobile.
     *
     * @param mobile the new mobile
     */
    public void setMobile(final String mobile) {
        this.mobile = mobile;
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
     * Sets the roles.
     *
     * @param roles the new roles
     */
    public void setRoles(final Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public Set<Role> getRoles() {
        return roles;
    }

    // Domain Methods
    // --------------------------------------------------------------------------
    /**
     * Find by username.
     *
     * @param username the username
     * @return the user
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static User findByUsername(final String username) {
        final User user = User.findByFieldName(
                User.class, "username", username, null);
        if (user == null) {
            throw new RecordNotFoundException(
                    "Error:Record was not found for user in findByUserName:"
                            + username);
        }
        return user;
    }

    /**
     * Find by email.
     *
     * @param eMail the e mail
     * @return the user
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static User findByEmail(final String eMail) {
        final User user = User
                .findByFieldName(User.class, "email", eMail, null);
        if (user == null) {
            throw new RecordNotFoundException(
                    "Error:Record was not found for user in findByEmail:"
                            + eMail);
        }
        return user;
    }

    /**
     * Find by first name.
     *
     * @param firstName the first name
     * @return the user
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static User findByFirstName(final String firstName) {
        final User user = User.findByFieldName(
                User.class, "firstName", firstName, null);
        if (user == null) {
            throw new RecordNotFoundException(
                    "Error:Record was not found for user in findByFirstName:"
                            + firstName);
        }
        return user;
    }

    /**
     * Find by last name.
     *
     * @param lastName the last name
     * @return the user
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static User findByLastName(final String lastName) {
        final User user = User.findByFieldName(
                User.class, "lastName", lastName, null);
        if (user == null) {
            throw new RecordNotFoundException(
                    "Error:Record was not found for user in findByLastName() :"
                            + lastName);
        }
        return user;
    }

    /**
     * Change password.
     *
     * @param username the username
     * @param newpassword the newpassword
     * @author nileshp
     * @since v1.0.0
     * Change password.
     */
    @Transactional
    public void changePassword(final String username, final String newpassword) {
        final User user = User.findByFieldName(
                User.class, "username", username, this.getLocale());
        user.setPassword(newpassword);
        user.merge();
    }
}
