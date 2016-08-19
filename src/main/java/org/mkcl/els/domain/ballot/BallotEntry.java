/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.BallotEntry.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain.ballot;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Member;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class BallotEntry
 * 
 * @author amitd
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="ballot_entries")
public class BallotEntry extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 4999119120588598051L;

	//=============== ATTRIBUTES ====================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	private Integer position;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="ballot_entries_device_sequences",
			joinColumns={ @JoinColumn(name="ballot_entry_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="device_sequence_id", referencedColumnName="id") })
	private List<DeviceSequence> deviceSequences;
	
	
	//=============== CONSTRUCTORS ===============
	public BallotEntry() {
		super();
	}
	
	public BallotEntry(final Member member,
			final String locale) {
		super(locale);
		this.setMember(member);
	}
	
	public BallotEntry(final Member member,
			final List<DeviceSequence> deviceSequences,
			final String locale) {
		super(locale);
		this.setMember(member);
		this.setDeviceSequences(deviceSequences);
	}
	
	//=============== GETTERS/SETTERS ===============
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public List<DeviceSequence> getDeviceSequences() {
		return deviceSequences;
	}

	public void setDeviceSequences(List<DeviceSequence> deviceSequences) {
		this.deviceSequences = deviceSequences;
	}
}
