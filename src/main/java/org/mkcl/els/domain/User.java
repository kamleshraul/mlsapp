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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
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
@JsonIgnoreProperties()
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

	@Temporal(TemporalType.DATE)
	private Date birthDate;

	@Column(length=100)
	private String birthPlace;

	/** The credential. */
	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.REMOVE)
	@JoinColumn(name="credential_id")
	private Credential credential;

	@ManyToOne(fetch=FetchType.LAZY)
	private HouseType houseType;

	@Column(length=1000)
	private String startURL;

	private String groupsAllowed;  

	/**** In case of Reporters ****/
	private String language;

	@Temporal(TemporalType.DATE)
	private Date joiningDate;

	/** The user repository. */

	@Autowired
	private transient UserRepository userRepository;

	// ---------------------------------Constructors----------------------------------------------

	/**
	 * Instantiates a new user.
	 */
	public User() {
		super();
		credential=new Credential();
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
	 * @throws ELSException 
	 */
	public static User findByUserName(final String username,final String locale) throws ELSException{
		return getUserRepository().findByUserName(username, locale);
	}

	public static User find(final Member member) throws ELSException{
		return getUserRepository().find(member);
	}

	//    public static void assignMemberId(final Long memberId,final Long userId){
	//    	getUserRepository().assignMemberId(memberId, userId);
	//    }
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

	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(final Date birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getBirthPlace() {
		return birthPlace;
	}
	
	public void setBirthPlace(final String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(final HouseType houseType) {
		this.houseType = houseType;
	}
	
	public void setStartURL(String startURL) {
		this.startURL = startURL;
	}
	
	public String getStartURL() {
		return startURL;
	}
	
	public void setGroupsAllowed(String groupsAllowed) {
		this.groupsAllowed = groupsAllowed;
	}
	
	public String getGroupsAllowed() {
		return groupsAllowed;
	}
	
	public static User findByNameBirthDate(final String firstName,final String middleName,
			final String lastName,final Date birthDate, final String locale) throws ELSException {
		return getUserRepository().findByNameBirthDate(firstName,middleName,lastName,birthDate,locale);
	}
	
	public static User findbyNameBirthDate(final String firstName,final String middleName,
			final String lastName,final Date birthDate) throws ELSException {
		return getUserRepository().findbyNameBirthDate(firstName,middleName,lastName,birthDate);
	}
	
	public static List<User> findByRole(final boolean roleStartingWith,final String roles,final String language,
			final String orderBy,final String sortOrder,final String locale,final String houseType) {
		/**** if roleStartingWith=true then roles will be just the prefix to search roles with.
		 * If roleStartingWith=false then roles will be a comma separated list of roles 
		 * Users will be fetched accoring to the locale and language ****/
		return getUserRepository().findByRole(roleStartingWith,roles,language,orderBy,sortOrder,locale,houseType);
	}

	public String findFullName(){
		StringBuilder sb = new StringBuilder();
		if(this.getTitle() != null ) {
			sb.append(this.getTitle()+' ');
		}
		if(this.getFirstName() != null ) {
			sb.append(this.getFirstName()+' ');
		}
		if(this.getMiddleName() != null ) {
			sb.append(this.getMiddleName()+' ');
		}
		if(this.getLastName() != null ) {
			sb.append(this.getLastName());
		}
		return sb.toString();
	}
	
	public String findFullNameForRis(){
		return this.getTitle()+" "+this.getMiddleName()+" "+this.getLastName()+" "+this.getFirstName();
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}
	
	public Date getJoiningDate() {
		return joiningDate;
	}
	
	public String findFirstLastName(){
		return this.getTitle()+" "+this.getFirstName()+" "+this.getLastName();
	}
	
	public String findFirstMiddleBeginLetterLastName(){
		return this.getFirstName()+" "+this.getMiddleName().substring(0, 1)+". "+this.getLastName();
	}
	
	public static List<User> findByRole(final boolean roleStartingWith,final String roles,final String locale) {
		/**** if roleStartingWith=true then roles will be just the prefix to search roles with.
		 * If roleStartingWith=false then roles will be a comma separated list of roles 
		 * Users will be fetched accoring to the locale ****/
		return getUserRepository().findByRole(roleStartingWith,roles,locale);
	}
}