/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.BillKind.java
 * Created On: July 09, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class BillKind. Each Bill Type can be one of the Bill Kinds
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "billkinds")
public class BillKind extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 150)
    private String type;

    /** The name. */
    @Column(length=600)
    private String name;

    // ---------------------------------Constructors----------------------------------------------

    public BillKind() {
        super();
    }

    public BillKind(final String type, final String name) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
