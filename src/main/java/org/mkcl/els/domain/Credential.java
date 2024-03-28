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
import java.util.List;
import java.util.Random;
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
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.repository.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Column(length = 200)
    private String password;

    /** The enabled. */
    private boolean enabled;
    
    /** The is allowed for multi login. default is false. */
    private boolean allowedForMultiLogin = false;

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
    
    private String otpCode;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date otpExpiryTime;    
    
    @Autowired
	private transient CredentialRepository repository;
    
    private int passwordChangeCount;    

    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordChangeDateTime;    
    
    /** The high security password. */
    @Column(length = 20)
    private String highSecurityPassword;
 

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
    
    private static CredentialRepository getRepository() {
    	CredentialRepository repository = new Credential().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"CredentialRepository has not been injected in" +
				" Credential Domain");
		}
		
		return repository;
	}
    
    public static String generatePassword(int length){
    	return (new Credential()).genPassword(length);
    }
    
    private String genPassword(int length){
    	StringBuffer[] charType = {new StringBuffer("alpha"), new StringBuffer("numeral"), new StringBuffer("special")};
    	int charTypeLength = charType.length;
    	
    	StringBuffer[] charCase = {new StringBuffer("upper"),new StringBuffer("lower")};
    	int charCaseLength = charCase.length;
    	
    	Random rnd = new Random();
    	
    	StringBuffer retVal = new StringBuffer();
    	for(int i = 0; i < length; i++){
    		int indexCharType = (int)(Math.abs(rnd.nextLong()) % charTypeLength);
    		
    		while(indexCharType < 0 || indexCharType > charTypeLength){
    			indexCharType = (int)(Math.abs(rnd.nextLong()) % charTypeLength);
    		}
    		
    		String strCharType = charType[indexCharType].toString();
    		
    		if(strCharType.equals("alpha")){
    			int indexCharCase = (int)(Math.abs(rnd.nextLong()) % charCaseLength);
    			
    			while(indexCharCase < 0 || indexCharCase > charCaseLength){
        			indexCharCase = (int)(Math.abs(rnd.nextLong()) % charCaseLength);
        		}
    			
    			retVal.append(genAlphas(charCase[indexCharCase].toString()));
    		}else if(strCharType.equals("numeral")){
    			retVal.append(genNumerals());    			
    		}else if(strCharType.equals("special")){
    			retVal.append(genSpecials());
    		}  		
    		
    	}
    	return retVal.toString();
    }
    
    private String genAlphas(String letterCase){
    	String alphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    	StringBuffer retVal = new StringBuffer();
    	
    	if(letterCase.equals("upper")){
    		Random rnd = new Random();
    		int index = Math.abs(rnd.nextInt()) % 25;
    		while(index < 0 || index > 25){
    			index = Math.abs(rnd.nextInt()) % 25;
    		}
    		
    		//char c = (char)(((int)alphas.charAt(index)) + Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
    	    retVal.append(alphas.charAt(index));
    		
    	}else if(letterCase.equals("lower")){
    		Random rnd = new Random();
    		int index = (int)Math.abs(rnd.nextLong()) % 51;
    		while(index < 25 || index > 51){
    			index = (int)Math.abs(rnd.nextLong()) % 51;
    		}
    		
    		//char c = (char)(((int)alphas.charAt(index)) + Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
    		retVal.append(alphas.charAt(index));
    	}
    	
    	return retVal.toString();
    }
    
    private String genNumerals(){
    	String nums = "0123456789";
    	StringBuffer retVal = new StringBuffer();
    	Random rnd = new Random();
    	int length = nums.length() - 1;
    	int index = (int)(Math.abs(rnd.nextLong()) % length);
		while(index < 0 || index > (length - 1)){
			index = (int)(Math.abs(rnd.nextLong()) % length);
		}
		//char c = (char)(((int)nums.charAt(index)) + Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
		retVal.append(nums.charAt(index));
    	
    	return retVal.toString();
    }
    
    private String genSpecials(){
    	//String specials = "!@#$%^&*()";
    	String specials = "!@#$%&*";
    	StringBuffer retVal = new StringBuffer();
    	Random rnd = new Random();
    	int length = specials.length() - 1;
    	int index = (int)(Math.abs(rnd.nextLong()) % length);
		while(index < 0 || index > (length - 1)){
			index = (int)(Math.abs(rnd.nextLong()) % length);
		}
		
		//char c = (char)(((int)specials.charAt(index)) + Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
		retVal.append(specials.charAt(index));
    	
    	return retVal.toString();
    }
    
    public static List<Credential> findAllCredentialsByRole(final String role){
    	return getRepository().findAllCredentialsByRole(role);
    }
    
    public static List<Credential> findAllActiveByUserGroupType(final String userGroupType, final Date onDate, final String locale) {
    	return getRepository().findAllActiveByUserGroupType(userGroupType, onDate, locale);
    }
    
    public static List<String> findAllActiveUsernamesByUserGroupType(final String userGroupType, final Date onDate, final String locale) {
    	return getRepository().findAllActiveUsernamesByUserGroupType(userGroupType, onDate, locale);
    }
    
    public static String findAllActiveUsernamesAsCommaSeparatedString(final String locale) {
    	return getRepository().findAllActiveUsernamesAsCommaSeparatedString(locale);
    }
    
    public static Long findUserIdByUsername(final String username) {
    	return getRepository().findUserIdByUsername(username);
    }
   
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
     * Checks if is allowed for multi login.
     *
     * @return true, if is allowed for multi login
     */
    public boolean isAllowedForMultiLogin() {
		return allowedForMultiLogin;
	}

    /**
     * Sets the allowed for multi login.
     *
     * @param allowed for multi login the new allowed for multi login
     */
	public void setAllowedForMultiLogin(boolean allowedForMultiLogin) {
		this.allowedForMultiLogin = allowedForMultiLogin;
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

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public Date getOtpExpiryTime() {
		return otpExpiryTime;
	}

	public void setOtpExpiryTime(Date otpExpiryTime) {
		this.otpExpiryTime = otpExpiryTime;
	}

	public Date getPasswordChangeDateTime() {
		return passwordChangeDateTime;
	}

	public void setPasswordChangeDateTime(Date passwordChangeDateTime) {
		this.passwordChangeDateTime = passwordChangeDateTime;
	}

   public int getPasswordChangeCount() {
		return passwordChangeCount;
	}

	public void setPasswordChangeCount(int passwordChangeCount) {
		this.passwordChangeCount = passwordChangeCount;
	}

	public String getHighSecurityPassword() {
		return highSecurityPassword;
	}

	public void setHighSecurityPassword(String highSecurityPassword) {
		this.highSecurityPassword = highSecurityPassword;
	}
}
