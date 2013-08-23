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
@Table(name="adjournments")
@JsonIgnoreProperties({""})
public class Adjournment extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	
	@ManyToOne
	private AdjournmentReason adjournmentReason;
	
	@Column(length=30000)
	private String remarks;
	
	private String action;
	
	@ManyToOne
	private Roster roster;
	
	/**** Constructors ****/
	public Adjournment() {
		super();
	}
	
	/**** Domain Methods ****/
	
	/**** Getters and Setters ****/
	
	public Roster getRoster() {
		return roster;
	}	

	public void setRoster(Roster roster) {
		this.roster = roster;
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

	public AdjournmentReason getAdjournmentReason() {
		return adjournmentReason;
	}

	public void setAdjournmentReason(AdjournmentReason adjournmentReason) {
		this.adjournmentReason = adjournmentReason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}
}
