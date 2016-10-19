/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ProceedingAutofill.java
 * Created On: Oct 05, 2016
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class ProceedingAutofill.
 *
 * @author anandku
 * @since  v1.0.0
 */
@Configurable
@Entity
@Table(name = "proceeding_autofill")
public class ProceedingAutofill extends BaseDomain implements Serializable{
	
	 // ---------------------------------Attributes-------------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String shortName;
    
    @Column(length=30000)
    private String autoFillContent;
    
    
    @Column(length=30000)
    private String username;

	public ProceedingAutofill() {
		super();
		// TODO Auto-generated constructor stub
	}


    // ---------------------Constructors and Domain Methods------------------------------------------
    public ProceedingAutofill(String locale) {
		super(locale);
		// TODO Auto-generated constructor stub
	}


	public ProceedingAutofill(String shortName, String autoFillContent) {
		super();
		this.shortName = shortName;
		this.autoFillContent = autoFillContent;
	}

	//------------------------ Getters and Setters----------------------------------------------------

	public String getShortName() {
		return shortName;
	}


	public void setShortName(String shortName) {
		this.shortName = shortName;
	}


	public String getAutoFillContent() {
		return autoFillContent;
	}


	public void setAutoFillContent(String autoFillContent) {
		this.autoFillContent = autoFillContent;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	
}
