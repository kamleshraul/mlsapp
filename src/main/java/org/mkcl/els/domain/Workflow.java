/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Workflow.java
 * Created On: Jan 13, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * The Class Workflow.
 *
 * @author samiksham
 * @since v1.0.0
 */

@Entity
@Table(name = "workflows")
public class Workflow implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    /** The workflow. */
    @Column(length = 200)
    @NotEmpty
    private String workflow;

    /** The locale. */
    @Column(length = 50)
    private String locale;

    /** The version. */
    @Version
    private Long version;


    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }


    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(final Long id) {
        this.id = id;
    }


    /**
     * Gets the workflow.
     *
     * @return the workflow
     */
    public String getWorkflow() {
        return workflow;
    }


    /**
     * Sets the workflow.
     *
     * @param workflow the new workflow
     */
    public void setWorkflow(final String workflow) {
        this.workflow = workflow;
    }


    /**
     * Gets the locale.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }


    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    public void setLocale(final String locale) {
        this.locale = locale;
    }


    /**
     * Gets the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }


    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(final Long version) {
        this.version = version;
    }



}
