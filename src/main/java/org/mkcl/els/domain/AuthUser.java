/*
******************************************************************
File: org.mkcl.els.domain.AuthUser.java
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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;

/**
 * The Class AuthUser.
 *
 * @author vishals
 * @version 1.0.0
 */
public class AuthUser extends User{

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	// Attributes --------------------------------------------------------------------------
    /** The id. */
	private Long id;
    
    /** The first name. */
    private String firstName;
    
    /** The last name. */
    private String lastName;
    
    /** The code. */
    private String code;
    
    /** The email. */
    private String email;
    
    /** The mobile. */
    private String mobile;
    
    /** The last login time. */
    private Date lastLoginTime;
    
    /** The current login time. */
    private Date currentLoginTime;
    
    // constructor --------------------------------------------------------------------------

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
     * @param id the id
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email
     * @param lastLoginTime the last login time
     * @param code the code
     * @param mobile the mobile
     */
    public AuthUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities, Long id, String firstName, String lastName, String email, Date lastLoginTime, String code, String mobile) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.lastLoginTime = lastLoginTime;
        this.code = code;
        this.mobile=mobile;
        this.setCurrentLoginTime(new Date());
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
      * Gets the current login time.
      *
      * @return the current login time
      */
     public Date getCurrentLoginTime() {
        return currentLoginTime;
    }

    /**
     * Sets the current login time.
     *
     * @param currentLoginTime the new current login time
     */
    public void setCurrentLoginTime(Date currentLoginTime) {
        this.currentLoginTime =currentLoginTime;
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
	 * Sets the mobile.
	 *
	 * @param mobile the new mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	 * Gets the fullname.
	 *
	 * @return the fullname
	 */
	public String getFullName() {
		return this.firstName + " " + this.getLastName();
	}
}
