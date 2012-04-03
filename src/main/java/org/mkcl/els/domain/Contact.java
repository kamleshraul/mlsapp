/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Contact.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Contact.
 * 
 * @author dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "contacts")
public class Contact extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The email1. */
    @Column(length = 100)
    private String email1;

    /** The email2. */
    @Column(length = 100)
    private String email2;

    /** The website1. */
    @Column(length = 100)
    private String website1;

    /** The website2. */
    @Column(length = 100)
    private String website2;

    /** The telephone1. */
    @Column(length = 20)
    private String telephone1;

    /** The telephone2. */
    @Column(length = 20)
    private String telephone2;

    /** The fax1. */
    @Column(length = 20)
    private String fax1;

    /** The fax2. */
    @Column(length = 20)
    private String fax2;

    /** The fax1. */
    @Column(length = 20)
    private String mobile1;

    /** The fax1. */
    @Column(length = 20)
    private String mobile2;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new contact.
     */
    public Contact() {
        super();
    }

    // -------------------------------Domain_Methods--------------------------

    // ------------------------------------------Getters/Setters-----------------------------------

    /**
     * Gets the email1.
     * 
     * @return the email1
     */
    public String getEmail11() {
        return email1;
    }

    /**
     * Sets the email1.
     * 
     * @param email1 the new email1
     */
    public void setEmail1(final String email1) {
        this.email1 = email1;
    }

    /**
     * Gets the email2.
     * 
     * @return the email2
     */
    public String getEmail2() {
        return email2;
    }

    /**
     * Sets the email2.
     * 
     * @param email2 the new email2
     */
    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    /**
     * Gets the website1.
     * 
     * @return the website1
     */
    public String getWebsite1() {
        return website1;
    }

    /**
     * Sets the website1.
     * 
     * @param website1 the new website1
     */
    public void setWebsite1(String website1) {
        this.website1 = website1;
    }

    /**
     * Gets the website2.
     * 
     * @return the website2
     */
    public String getWebsite2() {
        return website2;
    }

    /**
     * Sets the website2.
     * 
     * @param website2 the new website2
     */
    public void setWebsite2(String website2) {
        this.website2 = website2;
    }

    /**
     * Gets the telephone1.
     * 
     * @return the telephone1
     */
    public String getTelephone1() {
        return telephone1;
    }

    /**
     * Sets the telephone1.
     * 
     * @param telephone1 the new telephone1
     */
    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }

    /**
     * Gets the telephone2.
     * 
     * @return the telephone2
     */
    public String getTelephone2() {
        return telephone2;
    }

    /**
     * Sets the telephone2.
     * 
     * @param telephone2 the new telephone2
     */
    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    /**
     * Gets the fax1.
     * 
     * @return the fax1
     */
    public String getFax1() {
        return fax1;
    }

    /**
     * Sets the fax1.
     * 
     * @param fax1 the new fax1
     */
    public void setFax1(String fax1) {
        this.fax1 = fax1;
    }

    /**
     * Gets the fax2.
     * 
     * @return the fax2
     */
    public String getFax2() {
        return fax2;
    }

    /**
     * Sets the fax2.
     * 
     * @param fax2 the new fax2
     */
    public void setFax2(String fax2) {
        this.fax2 = fax2;
    }

    /**
     * Gets the mobile1.
     * 
     * @return the mobile1
     */
    public String getMobile1() {
        return mobile1;
    }

    /**
     * Sets the mobile1.
     * 
     * @param mobile1 the new mobile1
     */
    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    /**
     * Gets the mobile2.
     * 
     * @return the mobile2
     */
    public String getMobile2() {
        return mobile2;
    }

    /**
     * Sets the mobile2.
     * 
     * @param mobile2 the new mobile2
     */
    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

	public String getEmail1() {
		return email1;
	}

}
