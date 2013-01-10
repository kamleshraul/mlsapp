/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Query.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Query.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="queries")
public class Query extends BaseDomain{

    /** The query. */
    @Column(length=30000)
    private String query;

    /** The key field. */
    @Column(length=100)
    private String keyField;

    /**
     * Instantiates a new query.
     */
    public Query() {
        super();
    }

    /**
     * Instantiates a new query.
     *
     * @param keyField the key field
     * @param query the query
     */
    public Query(final String keyField, final String query) {
        super();
        this.query = query;
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query.
     *
     * @param query the new query
     */
    public void setQuery(final String query) {
        this.query = query;
    }


    /**
     * Gets the key field.
     *
     * @return the key field
     */
    public String getKeyField() {
        return keyField;
    }


    /**
     * Sets the key field.
     *
     * @param keyField the new key field
     */
    public void setKeyField(final String keyField) {
        this.keyField = keyField;
    }
}
