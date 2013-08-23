/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.UpperHouseConstituencyType.java
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
 * The Class UpperHouseConstituencyType.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "upperhouse_constituencytype")
public class UpperHouseConstituencyType extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The quota. */
    private Integer quota;

    /** The display name. */
    @Column(length=1000)
    private String displayName;

    /**
     * Instantiates a new upper house constituency type.
     */
    public UpperHouseConstituencyType() {
        super();
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
     * Gets the quota.
     *
     * @return the quota
     */
    public Integer getQuota() {
        return quota;
    }


    /**
     * Sets the quota.
     *
     * @param quota the new quota
     */
    public void setQuota(final Integer quota) {
        this.quota = quota;
    }



    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }



    /**
     * Sets the display name.
     *
     * @param displayName the new display name
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }



}
