/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Citation.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class Citation.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Entity
@Table(name="citations")
public class Citation extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The device type. */
    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;

    /** The text. */
    @Column(length=10000)
    private String text;

    /** The status. */
    @Column(length=10000)
    private String status;
    
   
    @Column(length=10000)
    private String type;
    /**
     * Instantiates a new citation.
     */
    public Citation() {
        super();
    }

    /**
     * Instantiates a new citation.
     *
     * @param deviceType the device type
     * @param text the text
     * @param status the status
     */
    public Citation(final DeviceType deviceType, final String text,final String status,final String type) {
        super();
        this.deviceType = deviceType;
        this.text = text;
        this.status=status;
        this.type=type;
    }


    /**
     * Gets the device type.
     *
     * @return the device type
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }


    /**
     * Sets the device type.
     *
     * @param deviceType the new device type
     */
    public void setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
    }


    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }


    /**
     * Sets the text.
     *
     * @param text the new text
     */
    public void setText(final String text) {
        this.text = text;
    }

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	


}
