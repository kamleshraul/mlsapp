/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Relation.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Relation.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "relations")
public class Relation extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /**** Attributes ****/
    
    /** The reservation_type. */
    @Column(length = 300)
    private String name;
    
    private String type;

    /**** Constructors ****/

    /**
     * Instantiates a new relation.
     */
    public Relation() {
        super();
    }

    /**
     * Instantiates a new relation.
     *
     * @param name the name
     */
    public Relation(final String name) {
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}   

}
