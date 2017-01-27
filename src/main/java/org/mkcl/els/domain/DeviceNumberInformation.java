/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.DeviceNumberInformation.java
 * Created On: January 25, 2017
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.DeviceNumberInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class DeviceNumberInformation.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "device_number_information")
public class DeviceNumberInformation extends BaseDomain implements Serializable {

	/**** Attributes ****/
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The deviceType. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType deviceType;
    
    /** The houseType. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;
    
    /** The session. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;
    
    /** The number. */
    @Column
    private Integer number;
    
    @Autowired
    private transient DeviceNumberInformationRepository deviceNumberInformationRepository;

    /**** Constructors ****/
    /**
     * Instantiates a new number information.
     */
    public DeviceNumberInformation() {
        super();
    }

	/**
	 * @return the deviceType
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * @return the houseType
	 */
	public HouseType getHouseType() {
		return houseType;
	}

	/**
	 * @param houseType the houseType to set
	 */
	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	// ----------------------------Domain Methods-------------------------//
    public static DeviceNumberInformationRepository getDeviceNumberInformationRepository() {
        DeviceNumberInformationRepository deviceNumberInformationRepository = new DeviceNumberInformation().deviceNumberInformationRepository;
        if (deviceNumberInformationRepository == null) {
            throw new IllegalStateException(
                    "DeviceNumberInformationRepository has not been injected in DeviceNumberInformation Domain");
        }
        return deviceNumberInformationRepository;
    }
    
    public static DeviceNumberInformation find(final DeviceType deviceType, final String locale) throws ELSException {
    	return getDeviceNumberInformationRepository().find(deviceType, locale);
    }
    
    public static DeviceNumberInformation find(final DeviceType deviceType, final HouseType houseType, final String locale) throws ELSException {
    	return getDeviceNumberInformationRepository().find(deviceType, houseType, locale);
    }
    
    public static DeviceNumberInformation find(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
    	return getDeviceNumberInformationRepository().find(deviceType, session, locale);
    }
    
    public static DeviceNumberInformation find(final DeviceType deviceType, final HouseType houseType, final Session session, final String locale) throws ELSException {
    	return getDeviceNumberInformationRepository().find(deviceType, houseType, session, locale);
    }
	
}