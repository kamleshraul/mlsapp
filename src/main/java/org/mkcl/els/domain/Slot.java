package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "slots")
@JsonIgnoreProperties({"roster"})
public class Slot extends BaseDomain implements Serializable{

	/*********Fields **************/

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private User user;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	
	@Column(length=30000)
	private String remarks;
	
	private Boolean turnedoff;
	
	@ManyToOne
	private Roster roster;

	
	/*********** Constructors ****************/	
	public Slot() {
		super();
	}
	/********** Domain Methods ****************/

	/*********** Setters and Getters ************/
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setRoster(Roster roster) {
		this.roster = roster;
	}

	public Roster getRoster() {
		return roster;
	}

	public void setTurnedoff(Boolean turnedoff) {
		this.turnedoff = turnedoff;
	}

	public Boolean getTurnedoff() {
		return turnedoff;
	}
}
