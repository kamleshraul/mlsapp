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
    @Column(length = 1000)
    private String email1;

    /** The email2. */
    @Column(length = 1000)
    private String email2;

    /** The website1. */
    @Column(length = 1000)
    private String website1;

    /** The website2. */
    @Column(length = 1000)
    private String website2;

    /** The fax1. */
    @Column(length = 1000)
    private String mobile1;

    /** The fax1. */
    @Column(length = 1000)
    private String mobile2;

    //permanentAddress
    /** The telephone1. */
    @Column(length = 1000)
    private String telephone1;

    //present address
    /** The telephone2. */
    @Column(length = 1000)
    private String telephone2;

    //office address
    /** The telephone3. */
    @Column(length = 1000)
    private String telephone3;

    //aamdar niwas 1(mumbai)
    /** The telephone4. */
    @Column(length = 1000)
    private String telephone4;

    //aamdar niwas 2(nagpur)
    /** The telephone5. */
    @Column(length = 1000)
    private String telephone5;

    //permanentAddress1
    /** The telephone1. */
    @Column(length = 1000)
    private String telephone6;

    //permanentAddress2
    /** The telephone1. */
    @Column(length = 1000)
    private String telephone7;

    //present address1
    /** The telephone2. */
    @Column(length = 1000)
    private String telephone8;

    //present address2
    /** The telephone2. */
    @Column(length = 1000)
    private String telephone9;

    //office address1
    /** The telephone10. */
    @Column(length = 1000)
    private String telephone10;

    //office address2
    /** The telephone11. */
    @Column(length = 1000)
    private String telephone11;

    //correspondence address
    /** The telephone12. */
    @Column(length = 1000)
    private String telephone12;

    //permanent address
    /** The fax1. */
    @Column(length = 1000)
    private String fax1;

    //present address
    /** The fax2. */
    @Column(length = 1000)
    private String fax2;

    //office address
    /** The fax3. */
    @Column(length = 1000)
    private String fax3;

    //aamdar niwas 1
    /** The fax4. */
    @Column(length = 1000)
    private String fax4;

    //aamdar niwas 2
    /** The fax5. */
    @Column(length = 1000)
    private String fax5;

    //permanent address1
    /** The fax1. */
    @Column(length = 1000)
    private String fax6;

    //permanent address2
    /** The fax1. */
    @Column(length = 1000)
    private String fax7;

    //present address1
    /** The fax2. */
    @Column(length = 1000)
    private String fax8;

    //present address2
    /** The fax2. */
    @Column(length = 1000)
    private String fax9;

    //office address1
    /** The fax10. */
    @Column(length = 1000)
    private String fax10;

    //office address2
    /** The fax11. */
    @Column(length = 1000)
    private String fax11;

    //correspondence address2
    /** The fax12. */
    @Column(length = 1000)
    private String fax12;

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
    public void setEmail2(final String email2) {
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
    public void setWebsite1(final String website1) {
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
    public void setWebsite2(final String website2) {
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
    public void setTelephone1(final String telephone1) {
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
    public void setTelephone2(final String telephone2) {
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
    public void setFax1(final String fax1) {
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
    public void setFax2(final String fax2) {
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
    public void setMobile1(final String mobile1) {
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
    public void setMobile2(final String mobile2) {
        this.mobile2 = mobile2;
    }

    /**
     * Gets the email1.
     *
     * @return the email1
     */
    public String getEmail1() {
        return email1;
    }

    /**
     * Gets the telephone3.
     *
     * @return the telephone3
     */
    public String getTelephone3() {
        return telephone3;
    }

    /**
     * Sets the telephone3.
     *
     * @param telephone3 the new telephone3
     */
    public void setTelephone3(final String telephone3) {
        this.telephone3 = telephone3;
    }

    /**
     * Gets the telephone4.
     *
     * @return the telephone4
     */
    public String getTelephone4() {
        return telephone4;
    }

    /**
     * Sets the telephone4.
     *
     * @param telephone4 the new telephone4
     */
    public void setTelephone4(final String telephone4) {
        this.telephone4 = telephone4;
    }

    /**
     * Gets the telephone5.
     *
     * @return the telephone5
     */
    public String getTelephone5() {
        return telephone5;
    }

    /**
     * Sets the telephone5.
     *
     * @param telephone5 the new telephone5
     */
    public void setTelephone5(final String telephone5) {
        this.telephone5 = telephone5;
    }

    /**
     * Gets the fax3.
     *
     * @return the fax3
     */
    public String getFax3() {
        return fax3;
    }

    /**
     * Sets the fax3.
     *
     * @param fax3 the new fax3
     */
    public void setFax3(final String fax3) {
        this.fax3 = fax3;
    }

    /**
     * Gets the fax4.
     *
     * @return the fax4
     */
    public String getFax4() {
        return fax4;
    }

    /**
     * Sets the fax4.
     *
     * @param fax4 the new fax4
     */
    public void setFax4(final String fax4) {
        this.fax4 = fax4;
    }

    /**
     * Gets the fax5.
     *
     * @return the fax5
     */
    public String getFax5() {
        return fax5;
    }

    /**
     * Sets the fax5.
     *
     * @param fax5 the new fax5
     */
    public void setFax5(final String fax5) {
        this.fax5 = fax5;
    }

    /**
     * Gets the telephone6.
     *
     * @return the telephone6
     */
    public String getTelephone6() {
        return telephone6;
    }

    /**
     * Sets the telephone6.
     *
     * @param telephone6 the new telephone6
     */
    public void setTelephone6(final String telephone6) {
        this.telephone6 = telephone6;
    }

    /**
     * Gets the telephone7.
     *
     * @return the telephone7
     */
    public String getTelephone7() {
        return telephone7;
    }

    /**
     * Sets the telephone7.
     *
     * @param telephone7 the new telephone7
     */
    public void setTelephone7(final String telephone7) {
        this.telephone7 = telephone7;
    }

    /**
     * Gets the telephone8.
     *
     * @return the telephone8
     */
    public String getTelephone8() {
        return telephone8;
    }

    /**
     * Sets the telephone8.
     *
     * @param telephone8 the new telephone8
     */
    public void setTelephone8(final String telephone8) {
        this.telephone8 = telephone8;
    }

    /**
     * Gets the telephone9.
     *
     * @return the telephone9
     */
    public String getTelephone9() {
        return telephone9;
    }

    /**
     * Sets the telephone9.
     *
     * @param telephone9 the new telephone9
     */
    public void setTelephone9(final String telephone9) {
        this.telephone9 = telephone9;
    }

    /**
     * Gets the telephone10.
     *
     * @return the telephone10
     */
    public String getTelephone10() {
        return telephone10;
    }

    /**
     * Sets the telephone10.
     *
     * @param telephone10 the new telephone10
     */
    public void setTelephone10(final String telephone10) {
        this.telephone10 = telephone10;
    }

    /**
     * Gets the telephone11.
     *
     * @return the telephone11
     */
    public String getTelephone11() {
        return telephone11;
    }

    /**
     * Sets the telephone11.
     *
     * @param telephone11 the new telephone11
     */
    public void setTelephone11(final String telephone11) {
        this.telephone11 = telephone11;
    }

    /**
     * Gets the fax6.
     *
     * @return the fax6
     */
    public String getFax6() {
        return fax6;
    }

    /**
     * Sets the fax6.
     *
     * @param fax6 the new fax6
     */
    public void setFax6(final String fax6) {
        this.fax6 = fax6;
    }

    /**
     * Gets the fax7.
     *
     * @return the fax7
     */
    public String getFax7() {
        return fax7;
    }

    /**
     * Sets the fax7.
     *
     * @param fax7 the new fax7
     */
    public void setFax7(final String fax7) {
        this.fax7 = fax7;
    }

    /**
     * Gets the fax8.
     *
     * @return the fax8
     */
    public String getFax8() {
        return fax8;
    }

    /**
     * Sets the fax8.
     *
     * @param fax8 the new fax8
     */
    public void setFax8(final String fax8) {
        this.fax8 = fax8;
    }

    /**
     * Gets the fax9.
     *
     * @return the fax9
     */
    public String getFax9() {
        return fax9;
    }

    /**
     * Sets the fax9.
     *
     * @param fax9 the new fax9
     */
    public void setFax9(final String fax9) {
        this.fax9 = fax9;
    }

    /**
     * Gets the fax10.
     *
     * @return the fax10
     */
    public String getFax10() {
        return fax10;
    }

    /**
     * Sets the fax10.
     *
     * @param fax10 the new fax10
     */
    public void setFax10(final String fax10) {
        this.fax10 = fax10;
    }

    /**
     * Gets the fax11.
     *
     * @return the fax11
     */
    public String getFax11() {
        return fax11;
    }

    /**
     * Sets the fax11.
     *
     * @param fax11 the new fax11
     */
    public void setFax11(final String fax11) {
        this.fax11 = fax11;
    }


    /**
     * Gets the telephone12.
     *
     * @return the telephone12
     */
    public String getTelephone12() {
        return telephone12;
    }


    /**
     * Sets the telephone12.
     *
     * @param telephone12 the new telephone12
     */
    public void setTelephone12(final String telephone12) {
        this.telephone12 = telephone12;
    }


    /**
     * Gets the fax12.
     *
     * @return the fax12
     */
    public String getFax12() {
        return fax12;
    }


    /**
     * Sets the fax12.
     *
     * @param fax12 the new fax12
     */
    public void setFax12(final String fax12) {
        this.fax12 = fax12;
    }
}
