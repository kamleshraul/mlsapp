/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionSequence.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain.ballot;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Device;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * A simple POJO which holds Device object and an integer sequence number.
 * This POJO is used for balloting.
 *
 * @author amitd
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="device_sequences")
public class DeviceSequence extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -5344796318972599026L;

	//=============== ATTRIBUTES ====================
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="device_id")
	private Device device;
	
	private Integer round;
	
	private Integer sequenceNo;
	
	
	//=============== CONSTRUCTORS ==================
	public DeviceSequence() {
		super();
	}
	
	public DeviceSequence(final Device device, 
			final String locale) {
		super(locale);
		this.setDevice(device);
	}

	
	//=============== GETTERS/SETTERS ===============
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
}
