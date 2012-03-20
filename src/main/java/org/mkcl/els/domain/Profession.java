/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Profession.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;
// TODO: Auto-generated Javadoc

/**
 * The Class Profession.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_professions")
public class Profession extends BaseDomain implements Serializable{
	
	// ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    
    /** The profession. */
    @Column(length = 150, nullable = false)
    @NotEmpty
    private String name;

    
 // ---------------------------------Constructors----------------------------------------------
    
    /**
  * Instantiates a new profession.
  */
 public Profession() {
		super();
	}


    /**
     * Instantiates a new profession.
     *
     * @param profession the profession
     */
    public Profession(final String name) {
	super();
	this.name = name;
    }

 // -------------------------------Domain_Methods----------------------------------------------

 // ------------------------------------------Getters/Setters-----------------------------------
    /**
  * Gets the profession.
  *
  * @return the profession
  */
 public String getName() {
		return name;
	}


	/**
	 * Sets the profession.
	 *
	 * @param profession the new profession
	 */
	public void setName(final String name) {
		this.name = name;
	}
}
