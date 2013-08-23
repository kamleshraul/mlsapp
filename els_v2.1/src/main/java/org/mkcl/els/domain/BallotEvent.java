/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.BallotEvent.java
 * Created On: Mar 04, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class BallotEvent.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "ballotevents")
public class BallotEvent extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 150)
    private String type;

    @Column(length=600)
    private String name;

    // ---------------------------------Constructors----------------------------------------------

    public BallotEvent() {
        super();
    }

    public BallotEvent(final String type, final String name) {
		super();
		this.type = type;
		this.name = name;
	}   

	// ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
    	this.type = type;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
    	this.name = name;
    }
}
