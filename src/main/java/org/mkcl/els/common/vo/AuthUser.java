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

// TODO: Auto-generated Javadoc
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
    
    private String userType;

    /** The house type. */
    private  String houseType;
    
    private String photographTag;
    
    private Set<Role> roles;
    
    private Long credentialId;

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
    public AuthUser(String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities,
            String title,String firstName,String middleName, String lastName, String houseType,String photo,Set<Role> roles,Long credentialId) {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);
        this.title=title;
        this.firstName = firstName;
        this.middleName=middleName;
        this.lastName = lastName;
        this.houseType = houseType;
        this.photographTag=photo;
        this.roles=roles;
        this.credentialId=credentialId;
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
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the house type.
     * 
     * @return the house type
     */
    public String getHouseType() {
        return houseType;
    }

	public String getTitle() {
		return title;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getUserType() {
		return userType;
	}

	public String getPhotographTag() {
		return photographTag;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public Long getCredentialId() {
		return credentialId;
	}	
}
