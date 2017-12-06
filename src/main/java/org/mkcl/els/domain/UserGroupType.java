/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.UserGroupType.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class UserGroupType.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
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
    
    @Column(length=3000)
    private String displayName;

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

    public static UserGroupType findByType(final String type,
    		final String locale) {
    	UserGroupType ugt = UserGroupType.findByFieldName(UserGroupType.class, "type", type, locale);
    	return ugt;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
    
    
}
