/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.User.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class User.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="users")

public class User extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;
    
    /** The title. */
    @Column(length=300)
    private String title;

    /** The first name. */
    @Column(length=300)
    private String firstName;

    /** The middle name. */
    @Column(length=300)
    private String middleName;

    /** The last name. */
    @Column(length=300)
    private String lastName;

    /** The credential. */
    @ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name="credential_id")
    private Credential credential;

    /** The user repository. */
    @Autowired
    private transient UserRepository userRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new user.
     */
    public User() {
        super();
      //  credential=new Credential();
    }
    // ----------------Domain_Methods------------------------------------------
    /**
     * Gets the user repository.
     *
     * @return the user repository
     */
    public static UserRepository getUserRepository() {
        UserRepository userRepository = new User().userRepository;
        if (userRepository == null) {
            throw new IllegalStateException(
                    "UserRepository has not been injected in User Domain");
        }
        return userRepository;
    }

    /**
     * Find by user name.
     *
     * @param username the username
     * @param locale the locale
     * @return the user
     */
    public static User findByUserName(final String username,final String locale){
    	return getUserRepository().findByUserName(username, locale);
    }
    
    public static void assignMemberId(final Long memberId,final Long userId){
    	getUserRepository().assignMemberId(memberId, userId);
    }
    // ------------------------------------------Getters/Setters-----------------------------------

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
		return title;
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
	 * Gets the credential.
	 *
	 * @return the credential
	 */
	public Credential getCredential() {
		return credential;
	}

	/**
	 * Sets the credential.
	 *
	 * @param credential the new credential
	 */
	public void setCredential(final Credential credential) {
		this.credential = credential;
	}
	
	
	
}