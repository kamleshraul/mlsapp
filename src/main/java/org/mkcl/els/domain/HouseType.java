/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.AssemblyCouncilType.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class AssemblyCouncilType.
 * 
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_housetype")
public class HouseType extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 150, nullable = false)
    @NotEmpty
    private String type;

    // ---------------------------------Constructors----------------------------------------------

    public HouseType() {
        super();
    }

    public HouseType(String type) {
        super();
        this.type = type;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

}
