/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.DynamicSelectVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;


/**
 * The Class DynamicSelectVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class DynamicSelectVO {

    /** The key. */
    private Long key;

    /** The value. */
    private String value;


    /**
     * Gets the key.
     *
     * @return the key
     */
    public Long getKey() {
        return key;
    }


    /**
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(final Long key) {
        this.key = key;
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
