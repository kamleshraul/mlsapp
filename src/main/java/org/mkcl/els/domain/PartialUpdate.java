/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.PartialUpdate.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class PartialUpdate.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "partial_updates")
public class PartialUpdate extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The url pattern. */
    @Column(length = 200)
    private String urlPattern;

    /** The fields not to be overwritten. */
    @Column(length = 5000)
    private String fieldsNotToBeOverwritten;

    /**
     * Instantiates a new partial update.
     */
    public PartialUpdate() {
        super();
    }

    /**
     * Instantiates a new partial update.
     *
     * @param urlPattern the url pattern
     * @param fieldsNotToBeOverwritten the fields not to be overwritten
     */
    public PartialUpdate(final String urlPattern, final String fieldsNotToBeOverwritten) {
        super();
        this.urlPattern = urlPattern;
        this.fieldsNotToBeOverwritten = fieldsNotToBeOverwritten;
    }

    /**
     * Gets the url pattern.
     *
     * @return the url pattern
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * Sets the url pattern.
     *
     * @param urlPattern the new url pattern
     */
    public void setUrlPattern(final String urlPattern) {
        this.urlPattern = urlPattern;
    }

    /**
     * Gets the fields not to be overwritten.
     *
     * @return the fields not to be overwritten
     */
    public String getFieldsNotToBeOverwritten() {
        return fieldsNotToBeOverwritten;
    }

    /**
     * Sets the fields not to be overwritten.
     *
     * @param fieldsNotToBeOverwritten the new fields not to be overwritten
     */
    public void setFieldsNotToBeOverwritten(final String fieldsNotToBeOverwritten) {
        this.fieldsNotToBeOverwritten = fieldsNotToBeOverwritten;
    }
}
