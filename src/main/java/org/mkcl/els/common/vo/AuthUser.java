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
import java.util.Set;

import org.mkcl.els.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * The Class AuthUser.
 *
 * @author vishals
 * @version 1.0.0
 */
public class AuthUser extends User {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private  String title;

    /** The first name. */
    private  String firstName;

    private  String middleName;

    /** The last name. */
    private  String lastName;

    /** The house type. */
    private  String houseType;

    private Set<Role> roles;

    /**
     * Instantiates a new auth user.
     *
     * @param username the username
     * @param password the password
     * @param enabled the enabled
     * @param accountNonExpired the account non expired
     * @param credentialsNonExpired the credentials non expired
     * @param accountNonLocked the account non locked
     * @param authorities the authorities
     * @param firstName the first name
     * @param lastName the last name
     * @param houseType the house type
     */
    public AuthUser(final String username, final String password, final boolean enabled,
            final boolean accountNonExpired, final boolean credentialsNonExpired,
            final boolean accountNonLocked,
            final Collection<? extends GrantedAuthority> authorities,
            final String title,final String firstName,final String middleName, final String lastName, final String houseType,final Set<Role> roles
            ) {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);
        this.title=title;
        this.firstName = firstName;
        this.middleName=middleName;
        this.lastName = lastName;
        this.houseType = houseType;
        this.roles=roles;
    }

	public String getTitle() {
		return title;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getHouseType() {
		return houseType;
	}

	public Set<Role> getRoles() {
		return roles;
	}

 }
