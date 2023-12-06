package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    /** Question Hour Included On that Day */
    private boolean isQuestionHourIncluded;
    
    /** Whether Propriety Points included for that Day */
    private boolean proprietyPointsIncluded;
    
    /** The order of the day document. */
    @Column(length=100)
    private String orderOfDayDoc;
    
    @Column(length=1000)
    private String curmotionOrderOfTheDay;
    
    @Column(length=1000)
    private String prevmotionOrderOfTheDay;

	/** The session repository. */
    @Autowired
    private transient SessionRepository sessionRepository;    
    
    
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

	public boolean getIsQuestionHourIncluded() {
		return isQuestionHourIncluded;
	}
	
	public void setIsQuestionHourIncluded(boolean isQuestionHourIncluded) {
		this.isQuestionHourIncluded = isQuestionHourIncluded;
	}

	public boolean getProprietyPointsIncluded() {
		return proprietyPointsIncluded;
	}

	public void setProprietyPointsIncluded(boolean proprietyPointsIncluded) {
		this.proprietyPointsIncluded = proprietyPointsIncluded;
	}

	public String getOrderOfDayDoc() {
		return orderOfDayDoc;
	}

	public void setOrderOfDayDoc(String orderOfDayDoc) {
		this.orderOfDayDoc = orderOfDayDoc;
	}

	public String getCurmotionOrderOfTheDay() {
		return curmotionOrderOfTheDay;
	}

	public void setCurmotionOrderOfTheDay(String curmotionOrderOfTheDay) {
		this.curmotionOrderOfTheDay = curmotionOrderOfTheDay;
	}

	public String getPrevmotionOrderOfTheDay() {
		return prevmotionOrderOfTheDay;
	}

	public void setPrevmotionOrderOfTheDay(String prevmotionOrderOfTheDay) {
		this.prevmotionOrderOfTheDay = prevmotionOrderOfTheDay;
	}

	/**
     * Gets the session repository.
     *
     * @return the session repository
     */
    public static SessionRepository getSessionRepository() {
        SessionRepository sessionRepository = new SessionDates().sessionRepository;
        if (sessionRepository == null) {
            throw new IllegalStateException(
                    "SessionRepository has not been injected in Session Domain");
        }
        return sessionRepository;
    }
    
    
    //============= Domain Methods =============//    
    public static List<SessionDates> findSessionDates(final Session session,final String sessionDates) throws ELSException {
        return getSessionRepository().findSessionDates(session,sessionDates);
    }
	
	/*
	 * public static List findSessionSpecificDates(final Session session) throws
	 * ELSException { return
	 * getSessionRepository().findSessionSpecificDates(session); }
	 */
}