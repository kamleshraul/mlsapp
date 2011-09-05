/*
******************************************************************
File: org.mkcl.els.domain.User.java
Copyright (c) 2011, vishals, MKCL
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

// TODO: Auto-generated Javadoc
/**
 * The Class User.
 *
 * @author vishals
 * @version 1.0.0
 */
@Entity
@Table(name = "users")
public class User implements Serializable{

    // Attributes --------------------------------------------------------------------------

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /** The code. */
    @Column(length =20)
    private String code;
    
    /** The username. */
    @Column(length =20)
    @NotEmpty
    private String username;
    
    /** The password. */
    @Column(length =20)
    @NotEmpty
    private String password;
    
    /** The enabled. */
    private boolean enabled=true;
    
    /** The first name. */
    @Column(length =50)
    @NotEmpty
    private String firstName;
    
    /** The first name. */
    @Column(length =50)
    @NotEmpty
    private String middleName;
    
    /** The last name. */
    @Column(length =50)
    @NotEmpty
    private String lastName;
    
    /** The email. */
    @Column(length =50)
    @NotEmpty
    @Email
    private String email;
    
    /** The email. */
    @Column(length =15)
    private String mobile;
    
    /** The last login time. */
    private Date lastLoginTime;
    
    /** The roles. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="role_membership",
            joinColumns=
            @JoinColumn(name="user_id", referencedColumnName="user_id"),
      inverseJoinColumns=
            @JoinColumn(name="role_id", referencedColumnName="role_id"))
    private Set<Role> roles;
    
    
    // constructor --------------------------------------------------------------------------

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
     * @param middleName the middle name
     * @param lastName the last name
     * @param email the email
     * @param lastLoginTime the last login time
     */
    public User(String username, String password, boolean enabled, String firstName, String middleName, String lastName, String email, Date lastLoginTime) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.lastLoginTime = lastLoginTime;
    }

    // Getters & Setters --------------------------------------------------------------------------

	/**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	/**
	 * Sets the code.
	 *
	 * @param code the new code
	 */
	public void setCode(String code) {
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
	public void setFirstName(String firstName) {
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
	public void setMiddleName(String middleName) {
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
	public void setLastName(String lastName) {
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
	public void setEmail(String email) {
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
	public void setMobile(String mobile) {
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
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}


	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(Set<Role> roles) {
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
}
