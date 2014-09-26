package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "activitylog")
public class ActivityLog extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**** Attributes ****/
	private String eventClass;
	
	private String linkClicked;
	
	private String classId;
	
	@Temporal(TemporalType.TIME)
	private Date timeOfAction;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "credential_id", referencedColumnName = "id")
	private Credential credetial;
	
	/**** Attributes ****/
	
	/**** Constructor ****/
	public ActivityLog() {
		super();
	}

	/**** Constructor ****/
	
	/**** Getter Setter ****/
	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}

	public String getLinkClicked() {
		return linkClicked;
	}

	public void setLinkClicked(String linkClicked) {
		this.linkClicked = linkClicked;
	}

	public String getClassId(){
		return classId;
	}
	
	public void setClassId(String classId){
		this.classId = classId;
	}
	
	public Date getTimeOfAction() {
		return timeOfAction;
	}

	public void setTimeOfAction(Date timeOfAction) {
		this.timeOfAction = timeOfAction;
	}

	public Credential getCredetial() {
		return credetial;
	}

	public void setCredetial(Credential credetial) {
		this.credetial = credetial;
	}
	
	/**** Getter Setter ****/
}
