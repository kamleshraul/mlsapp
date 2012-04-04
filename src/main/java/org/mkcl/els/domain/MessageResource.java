/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MessageResource.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class MessageResource.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "message_resources")
public class MessageResource extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The code. */
    @NotEmpty
    private String code;

    /** The value. */
    @NotEmpty
    private String value;

    /** The description. */
    private String description;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new message resource.
     */
    public MessageResource() {
        super();
    }

    /**
     * Instantiates a new message resource.
     *
     * @param locale the locale
     * @param code the code
     * @param value the value
     * @param description the description
     */
    public MessageResource(final String locale, final String code,
            final String value, final String description) {
        super();
        this.code = code;
        this.value = value;
        this.description = description;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------

    /**
     * Gets the message code.
     *
     * @return the message code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the message code.
     *
     * @param code the new code
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(final String description) {
        this.description = description;
    }
}
