package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "session_dates")
public class SessionDates extends BaseDomain implements Serializable {
	
	/** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    //============= Attributes =============//    
    /** The session date. */
    @Temporal(TemporalType.DATE)
    private Date sessionDate;
    
    /** The session start time. */
    @Temporal(TemporalType.TIME)
    private Date startTime;
    
    /** The session end time. */
    @Temporal(TemporalType.TIME)
    private Date endTime;
    
    
    //============= Constructors =============//    
    public SessionDates() {
		super();
	}

	public SessionDates(Date sessionDate) {
		super();
		this.sessionDate = sessionDate;
	}
	

	//============= Getters & Setters =============//    
    public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
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

}