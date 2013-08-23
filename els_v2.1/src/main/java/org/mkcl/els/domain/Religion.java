/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Religion.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Religion.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "religions")
public class Religion extends BaseDomain implements Serializable {

	/**** Attributes ****/
	
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 300)
    private String name;

	/**** Constructors ****/

    /**
     * Instantiates a new religion.
     */
    public Religion() {
        super();
    }

    /**
     * Instantiates a new religion.
     *
     * @param name the name
     */
    public Religion(final String name) {
        super();
        this.name = name;
    }


	/**** Domain methods ****/
    

	/**** Getters and Setters ****/

    /**
     * Gets the name.
     *
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
