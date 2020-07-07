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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.repository.MessageResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

	/**** Attributes ****/
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The code. */
    private String code;

    /** The value. */
    @Column(length = 1500)
    private String value;

    /** The description. */
    private String description;

    @Autowired
    private transient MessageResourceRepository messageResourceRepository;

    /**** Constructors ****/

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

    /**** Domain methods ****/
    
    public static MessageResourceRepository getMessageResourceRepository() {
        MessageResourceRepository messageResourceRepository = new MessageResource().messageResourceRepository;
        if (messageResourceRepository == null) {
            throw new IllegalStateException(
                    "MessageResourceRepository has not been injected in MessageResource Domain");
        }
        return messageResourceRepository;
    }

    public String findByLocaleAndCode(final String locale,
            final String code) {
        return getMessageResourceRepository().findByLocaleAndCode(locale, code).getValue();
    }

    /**** Getters and Setters ****/
    
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
