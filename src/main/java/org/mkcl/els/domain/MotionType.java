/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MotionType.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class MotionType.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "motion_type")
public class MotionType extends BaseDomain {

    /** The name. */
    @Column(length = 400)
    private String name;

    /**
     * Instantiates a new motion type.
     */
    public MotionType() {
        super();
    }

    /**
     * Instantiates a new motion type.
     *
     * @param locale the locale
     */
    public MotionType(final String locale) {
        super(locale);
    }

    /**
     * Instantiates a new motion type.
     *
     * @param name the name
     * @param locale the locale
     */
    public MotionType(final String name, final String locale) {
        super(locale);
        this.name = name;
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
}
