/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.UserGroupType.java
 * Created On: Sep 17, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class UserGroupType.
 *
 * @author Sandeep
 * @since v1.0.0
 */
@Entity
@Table(name="usergroups_types")
public class UserGroupType extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length=1000)
    private String name;

    /** The type. */
    @Column(length=1000)
    private String type;

    /**
     * Instantiates a new user group type.
     */
    public UserGroupType() {
        super();
    }

    /**
     * Instantiates a new user group type.
     *
     * @param name the name
     * @param type the type
     */
    public UserGroupType(final String name, final String type) {
        super();
        this.name = name;
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


    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }


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
    public void setType(final String type) {
        this.type = type;
    }
}
