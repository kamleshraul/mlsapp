/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ChartEntry.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain.chart;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.Member;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class ChartEntry.
 *
 * @author amitd
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="chart_entries")
public class ChartEntry extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2144161593499538742L;


	//=============== ATTRIBUTES ===============
	/** The member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;

	/** The devices. */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="chart_entries_devices",
			joinColumns={ @JoinColumn(name="chart_entry_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="device_id", referencedColumnName="id") })
	private List<Device> devices;
    
    /** The created by. */
	@Column(length = 100)
    private String createdBy;
    
    /** The edited by. */
	@Column(length = 100)
    private String editedBy;
    
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date editedOn;


	//=============== CONSTRUCTORS ===============
	/**
	 * Instantiates a new chart entry.
	 */
	public ChartEntry() {
		super();
	}

	/**
	 * Instantiates a new chart entry.
	 *
	 * @param member the member
	 * @param devices the devices
	 * @param locale the locale
	 */
	public ChartEntry(final Member member,
			final List<Device> devices,
			final String locale) {
		super(locale);
		this.member = member;
		this.devices = devices;
	}
	
	//=============== DOMAIN METHODS ===============
	@Override
	@Transactional
	public ChartEntry persist() {
		String username = this.getCurrentUser().getActualUsername();
		this.setCreatedBy(username);
		this.setEditedOn(new Date());
		ChartEntry ce = (ChartEntry) super.persist();
		return ce;
	}
	
	@Override
	@Transactional
	public BaseDomain merge() {
		String username = this.getCurrentUser().getActualUsername();
		this.setEditedBy(username);
		this.setEditedOn(new Date());
		ChartEntry ce = (ChartEntry) super.merge();
		return ce;
	}


	//=============== GETTERS/SETTERS ===============
	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * Sets the member.
	 *
	 * @param member the new member
	 */
	public void setMember(final Member member) {
		this.member = member;
	}

	/**
	 * Gets the devices.
	 *
	 * @return the devices
	 */
	public List<Device> getDevices() {
		return devices;
	}

	/**
	 * Sets the devices.
	 *
	 * @param devices the new devices
	 */
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

}
