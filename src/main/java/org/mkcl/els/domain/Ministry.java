/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Ministry.java
 * Created On: Jun 2, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Ministry.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "ministries")
public class Ministry extends BaseDomain implements Serializable{
	  // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 900)
    private String name;
    
    /** The is expired. */
    @Column
    private  Boolean isExpired;
    
    /** The remarks. */
    @Column(length = 1000)
    private String remarks;
    // ---------------------------------Constructors----------------------------------------------

	/**
     * Instantiates a new ministry.
     */
    public Ministry() {
		super();
	}
	
	/**
	 * Instantiates a new ministry.
	 *
	 * @param name the name
	 * @param isExpired the is expired
	 * @param remarks the remarks
	 */
	public Ministry(String name, Boolean isExpired, String remarks) {
		super();
		this.name = name;
		this.isExpired = isExpired;
		this.remarks = remarks;
	}
	// ---------------------------------getters and setters----------------------------------------------
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
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the checks if is expired.
	 *
	 * @return the checks if is expired
	 */
	public Boolean getIsExpired() {
		return isExpired;
	}
	
	/**
	 * Sets the checks if is expired.
	 *
	 * @param isExpired the new checks if is expired
	 */
	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}
	
	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}
	
	/**
	 * Sets the remarks.
	 *
	 * @param remarks the new remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
