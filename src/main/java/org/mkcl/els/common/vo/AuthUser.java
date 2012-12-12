/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.AuthUser.java
 * Created On: Jan 6, 2012
 */

package org.mkcl.els.common.vo;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.UserGroup;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


// TODO: Auto-generated Javadoc
/**
 * The Class AuthUser.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class AuthUser extends User {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The house type. */
    private  String houseType;

    /** The title. */
    private  String title;

    /** The first name. */
    private  String firstName;

    /** The middle name. */
    private  String middleName;

    /** The last name. */
    private  String lastName;

    /** The roles. */
    private Set<Role> roles;

    /** The user groups. */
    private List<UserGroup> userGroups;

    /** The birth date. */
    private Date birthDate;

    /** The user id. */
    private Long userId;

    /** The actual username. */
    private String actualUsername;

    /** The actual email. */
    private String actualEmail;

    /**
     * Instantiates a new auth user.
     *
     * @param username the username
     * @param password the password
     * @param actualUsername the actual username
     * @param actualEmail the actual email
     * @param enabled the enabled
     * @param accountNonExpired the account non expired
     * @param credentialsNonExpired the credentials non expired
     * @param accountNonLocked the account non locked
     * @param authorities the authorities
     * @param roles the roles
     * @param userGroups the user groups
     */
    public AuthUser(final String username, final String password,final String actualUsername,final String actualEmail, final boolean enabled,
            final boolean accountNonExpired, final boolean credentialsNonExpired,
            final boolean accountNonLocked,
            final Collection<? extends GrantedAuthority> authorities,
            final Set<Role> roles,final List<UserGroup> userGroups
            ) {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);
        this.roles=roles;
        this.actualEmail=actualEmail;
        this.actualUsername=actualUsername;
        this.userGroups=userGroups;
    }

    /**
     * Gets the house type.
     *
     * @return the house type
     */
    public String getHouseType() {
        return houseType;
    }


    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
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
     * Gets the middle name.
     *
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
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
     * Gets the roles.
     *
     * @return the roles
     */
    public Set<Role> getRoles() {
        return roles;
    }



    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(final String title) {
        this.title = title;
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
     * Sets the middle name.
     *
     * @param middleName the new middle name
     */
    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
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
     * Sets the house type.
     *
     * @param houseType the new house type
     */
    public void setHouseType(final String houseType) {
        this.houseType = houseType;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public Long getUserId() {
        return userId;
    }


    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(final Long userId) {
        this.userId = userId;
    }


    /**
     * Gets the birth date.
     *
     * @return the birth date
     */
    public Date getBirthDate() {
        return birthDate;
    }


    /**
     * Sets the birth date.
     *
     * @param birthDate the new birth date
     */
    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }


    /**
     * Gets the actual username.
     *
     * @return the actual username
     */
    public String getActualUsername() {
        return actualUsername;
    }


    /**
     * Gets the actual email.
     *
     * @return the actual email
     */
    public String getActualEmail() {
        return actualEmail;
    }


    /**
     * Gets the user groups.
     *
     * @return the user groups
     */
    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

 }
