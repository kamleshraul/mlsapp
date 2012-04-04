/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Title.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Title.
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_titles")
public class Title extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    // Reason:Some titles can be very large e.g Padmashree Padmavibhushan
    // PadamBhusan Shanti jee Maharaj.So,to accomodate
    // extreme situations we need to allow 200 characters in english and 200*3
    // in marathi.
    /** The name. */
    @Column(length = 600)
    private String name;

    // ---------------------------------Constructors----------------------//

    /**
     * Instantiates a new title.
     */
    public Title() {
        super();
    }

    /**
     * Instantiates a new title.
     * @param name the name
     * @param locale the locale
     */
    public Title(final String name, final String locale) {
        super();
        this.name = name;
    }

    // ----------------------------Domain Methods-------------------------//

    // ----------------------------Getters/Setters------------------------//
    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }
}
