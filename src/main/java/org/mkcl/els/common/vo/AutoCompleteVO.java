/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.AutoCompleteVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;


/**
 * The Class AutoCompleteVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class AutoCompleteVO {

    /** The id. */
    private Long id;

    /** The value. */
    private String value;


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
}
