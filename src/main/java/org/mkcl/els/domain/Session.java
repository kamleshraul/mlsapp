/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Session.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Session.
 *
 * @author anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "sessions")
public class Session extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The house. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    /** The year. */
    @Column(name="session_year")
    private Integer year;

    /** The type. */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "sessiontype_id")
    private SessionType type;

    /** The place. */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "sessionplace_id")
    private SessionPlace place;

    /** The number. */
    private Integer number;

    /** The start date. */
    @Temporal(TemporalType.DATE)
    private Date startDate;

    /** The end date. */
    @Temporal(TemporalType.DATE)
    private Date endDate;

    /** The tentative start date. */
    @Temporal(TemporalType.DATE)
    private Date tentativeStartDate;

    /** The tentative end date. */
    @Temporal(TemporalType.DATE)
    private Date tentativeEndDate;

    /** The rotation order publishing date. */
    @Temporal(TemporalType.DATE)
    private Date rotationOrderPublishingDate;


    /** The question submission start date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date questionSubmissionStartDateLH;

    /** The question submission end date lh. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date questionSubmissionEndDateLH;

    /** The question submission first batch start date uh. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date questionSubmissionFirstBatchStartDateUH;

   /** The question submission first batch end date uh. */
   @Temporal(TemporalType.TIMESTAMP)
    private Date questionSubmissionFirstBatchEndDateUH;

    /** The question submission second batch start date uh. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date questionSubmissionSecondBatchStartDateUH;

    /** The question submission second batch end date uh. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date questionSubmissionSecondBatchEndDateUH;


    /** The first ballot date. */
    @Temporal(TemporalType.DATE)
    private Date firstBallotDate;


    /** The question submission first batch date. */

    /** The number of question in first batch. */
    private Integer numberOfQuestionInFirstBatchUH;

    /*
     * devices enabled for a session.This will be a list of enabled device type separated
     * by comma
     */
    /** The device types enabled. */
    @Column(length=1000)
    private String deviceTypesEnabled;
    /*
     * devices which require balloting.This will be a lsit of enabled device type separated
     * by comma.
     */
    /** The device types need ballot. */
    @Column(length=1000)
    private String deviceTypesNeedBallot;

    /** The duration in days. */
    private Integer durationInDays;

    /** The duration in hrs. */
    private Integer durationInHrs;

    /** The duration in mins. */
    private Integer durationInMins;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The rotation order text. */
    @Column(length = 30000)
    private String rotationOrderText;

    /** The session repository. */
    @Autowired
    private transient SessionRepository sessionRepository;

    // -------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new session.
     */
    public Session() {
        super();
    }


    // -------------------------------Domain_Methods----------------------------------------------

    /**
     * Gets the session repository.
     *
     * @return the session repository
     */
    public static SessionRepository getSessionRepository() {
        SessionRepository sessionRepository = new Session().sessionRepository;
        if (sessionRepository == null) {
            throw new IllegalStateException(
                    "SessionRepository has not been injected in Session Domain");
        }
        return sessionRepository;
    }

    /**
     * Find latest session.
     *
     * @param houseType the house type
     * @param sessionYear the session year
     * @return the session
     * @author compaq
     * @since v1.0.0
     */
    public static Session findLatestSession(final HouseType houseType,final Integer sessionYear) {
        return getSessionRepository().findLatestSession(houseType,sessionYear);
    }

    /**
     * Find latest session.
     *
     * @param houseType the house type
     * @return the session
     * @author compaq
     * @since v1.0.0
     */
    public static Session findLatestSession(final HouseType houseType) {
        return getSessionRepository().findLatestSession(houseType);
    }

    /**
     * Find sessions by house and year.
     *
     * @param house the house
     * @param year the year
     * @return the list< session>
     * @author compaq
     * @since v1.0.0
     */
    public static List<Session> findSessionsByHouseAndYear(final House house,final Integer year){
        return getSessionRepository().findSessionsByHouseAndYear(house, year);
    }

    /**
     * Find session by house session type year.
     *
     * @param house the house
     * @param sessionType the session type
     * @param sessionYear the session year
     * @return the session
     * @author compaq
     * @since v1.0.0
     */
    public static Session findSessionByHouseSessionTypeYear(final House house,
			final SessionType sessionType, final Integer sessionYear) {
        return getSessionRepository().findSessionByHouseSessionTypeYear(house, sessionType, sessionYear);
    }

    /**
     * Find session by house type session type year.
     *
     * @param houseType the house type
     * @param sessionType the session type
     * @param sessionYear the session year
     * @return the session
     * @author compaq
     * @since v1.0.0
     */
    public static Session findSessionByHouseTypeSessionTypeYear(final HouseType houseType,
            final SessionType sessionType, final Integer sessionYear) {
        return getSessionRepository().findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
    }

    public static List<Session> findSessionsByHouseTypeAndYear(
			final HouseType houseType, final Integer sessionYear) {
		return getSessionRepository().findSessionsByHouseTypeAndYear(houseType,sessionYear);
	}
    // ------------------------------Getters/Setters-----------------------


	/**
     * Gets the house.
     *
     * @return the house
     */
    public House getHouse() {
		return house;
	}


	/**
	 * Sets the house.
	 *
	 * @param house the new house
	 */
	public void setHouse(final House house) {
		this.house = house;
	}


	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}


	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	public void setYear(final Integer year) {
		this.year = year;
	}


	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public SessionType getType() {
		return type;
	}


	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(final SessionType type) {
		this.type = type;
	}


	/**
	 * Gets the place.
	 *
	 * @return the place
	 */
	public SessionPlace getPlace() {
		return place;
	}


	/**
	 * Sets the place.
	 *
	 * @param place the new place
	 */
	public void setPlace(final SessionPlace place) {
		this.place = place;
	}


	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}


	/**
	 * Sets the number.
	 *
	 * @param number the new number
	 */
	public void setNumber(final Integer number) {
		this.number = number;
	}


	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}


	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}


	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}


	/**
	 * Sets the end date.
	 *
	 * @param endDate the new end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}


	/**
	 * Gets the tentative start date.
	 *
	 * @return the tentative start date
	 */
	public Date getTentativeStartDate() {
		return tentativeStartDate;
	}


	/**
	 * Sets the tentative start date.
	 *
	 * @param tentativeStartDate the new tentative start date
	 */
	public void setTentativeStartDate(final Date tentativeStartDate) {
		this.tentativeStartDate = tentativeStartDate;
	}


	/**
	 * Gets the tentative end date.
	 *
	 * @return the tentative end date
	 */
	public Date getTentativeEndDate() {
		return tentativeEndDate;
	}


	/**
	 * Sets the tentative end date.
	 *
	 * @param tentativeEndDate the new tentative end date
	 */
	public void setTentativeEndDate(final Date tentativeEndDate) {
		this.tentativeEndDate = tentativeEndDate;
	}




	/**
	 * Gets the question submission start date lh.
	 *
	 * @return the question submission start date lh
	 */
	public Date getQuestionSubmissionStartDateLH() {
		return questionSubmissionStartDateLH;
	}


	/**
	 * Sets the question submission start date lh.
	 *
	 * @param questionSubmissionStartDateLH the new question submission start date lh
	 */
	public void setQuestionSubmissionStartDateLH(final Date questionSubmissionStartDateLH) {
		this.questionSubmissionStartDateLH = questionSubmissionStartDateLH;
	}


	/**
	 * Gets the question submission end date lh.
	 *
	 * @return the question submission end date lh
	 */
	public Date getQuestionSubmissionEndDateLH() {
		return questionSubmissionEndDateLH;
	}


	/**
	 * Sets the question submission end date lh.
	 *
	 * @param questionSubmissionEndDateLH the new question submission end date lh
	 */
	public void setQuestionSubmissionEndDateLH(final Date questionSubmissionEndDateLH) {
		this.questionSubmissionEndDateLH = questionSubmissionEndDateLH;
	}


	/**
	 * Gets the question submission first batch start date uh.
	 *
	 * @return the question submission first batch start date uh
	 */
	public Date getQuestionSubmissionFirstBatchStartDateUH() {
		return questionSubmissionFirstBatchStartDateUH;
	}


	/**
	 * Sets the question submission first batch start date uh.
	 *
	 * @param questionSubmissionFirstBatchStartDateUH the new question submission first batch start date uh
	 */
	public void setQuestionSubmissionFirstBatchStartDateUH(
			final Date questionSubmissionFirstBatchStartDateUH) {
		this.questionSubmissionFirstBatchStartDateUH = questionSubmissionFirstBatchStartDateUH;
	}


	/**
	 * Gets the question submission first batch end date uh.
	 *
	 * @return the question submission first batch end date uh
	 */
	public Date getQuestionSubmissionFirstBatchEndDateUH() {
		return questionSubmissionFirstBatchEndDateUH;
	}


	/**
	 * Sets the question submission first batch end date uh.
	 *
	 * @param questionSubmissionFirstBatchEndDateUH the new question submission first batch end date uh
	 */
	public void setQuestionSubmissionFirstBatchEndDateUH(
			final Date questionSubmissionFirstBatchEndDateUH) {
		this.questionSubmissionFirstBatchEndDateUH = questionSubmissionFirstBatchEndDateUH;
	}


	/**
	 * Gets the question submission second batch start date uh.
	 *
	 * @return the question submission second batch start date uh
	 */
	public Date getQuestionSubmissionSecondBatchStartDateUH() {
		return questionSubmissionSecondBatchStartDateUH;
	}


	/**
	 * Sets the question submission second batch start date uh.
	 *
	 * @param questionSubmissionSecondBatchStartDateUH the new question submission second batch start date uh
	 */
	public void setQuestionSubmissionSecondBatchStartDateUH(
			final Date questionSubmissionSecondBatchStartDateUH) {
		this.questionSubmissionSecondBatchStartDateUH = questionSubmissionSecondBatchStartDateUH;
	}


	/**
	 * Gets the question submission second batch end date uh.
	 *
	 * @return the question submission second batch end date uh
	 */
	public Date getQuestionSubmissionSecondBatchEndDateUH() {
		return questionSubmissionSecondBatchEndDateUH;
	}


	/**
	 * Sets the question submission second batch end date uh.
	 *
	 * @param questionSubmissionSecondBatchEndDateUH the new question submission second batch end date uh
	 */
	public void setQuestionSubmissionSecondBatchEndDateUH(
			final Date questionSubmissionSecondBatchEndDateUH) {
		this.questionSubmissionSecondBatchEndDateUH = questionSubmissionSecondBatchEndDateUH;
	}


	/**
	 * Gets the number of question in first batch uh.
	 *
	 * @return the number of question in first batch uh
	 */
	public Integer getNumberOfQuestionInFirstBatchUH() {
		return numberOfQuestionInFirstBatchUH;
	}


	/**
	 * Sets the number of question in first batch uh.
	 *
	 * @param numberOfQuestionInFirstBatchUH the new number of question in first batch uh
	 */
	public void setNumberOfQuestionInFirstBatchUH(
			final Integer numberOfQuestionInFirstBatchUH) {
		this.numberOfQuestionInFirstBatchUH = numberOfQuestionInFirstBatchUH;
	}


	/**
	 * Gets the device types enabled.
	 *
	 * @return the device types enabled
	 */
	public String getDeviceTypesEnabled() {
		return deviceTypesEnabled;
	}


	/**
	 * Sets the device types enabled.
	 *
	 * @param deviceTypesEnabled the new device types enabled
	 */
	public void setDeviceTypesEnabled(final String deviceTypesEnabled) {
		this.deviceTypesEnabled = deviceTypesEnabled;
	}


	/**
	 * Gets the device types need ballot.
	 *
	 * @return the device types need ballot
	 */
	public String getDeviceTypesNeedBallot() {
		return deviceTypesNeedBallot;
	}


	/**
	 * Sets the device types need ballot.
	 *
	 * @param deviceTypesNeedBallot the new device types need ballot
	 */
	public void setDeviceTypesNeedBallot(final String deviceTypesNeedBallot) {
		this.deviceTypesNeedBallot = deviceTypesNeedBallot;
	}


	/**
	 * Gets the duration in days.
	 *
	 * @return the duration in days
	 */
	public Integer getDurationInDays() {
		return durationInDays;
	}


	/**
	 * Sets the duration in days.
	 *
	 * @param durationInDays the new duration in days
	 */
	public void setDurationInDays(final Integer durationInDays) {
		this.durationInDays = durationInDays;
	}


	/**
	 * Gets the duration in hrs.
	 *
	 * @return the duration in hrs
	 */
	public Integer getDurationInHrs() {
		return durationInHrs;
	}


	/**
	 * Sets the duration in hrs.
	 *
	 * @param durationInHrs the new duration in hrs
	 */
	public void setDurationInHrs(final Integer durationInHrs) {
		this.durationInHrs = durationInHrs;
	}


	/**
	 * Gets the duration in mins.
	 *
	 * @return the duration in mins
	 */
	public Integer getDurationInMins() {
		return durationInMins;
	}


	/**
	 * Sets the duration in mins.
	 *
	 * @param durationInMins the new duration in mins
	 */
	public void setDurationInMins(final Integer durationInMins) {
		this.durationInMins = durationInMins;
	}


	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}


	/**
	 * Sets the remarks.
	 *
	 * @param remarks the new remarks
	 */
	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}


	/**
	 * Sets the session repository.
	 *
	 * @param sessionRepository the new session repository
	 */
	public void setSessionRepository(final SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}


	/**
	 * Gets the rotation order publishing date.
	 *
	 * @return the rotation order publishing date
	 */
	public Date getRotationOrderPublishingDate() {
		return rotationOrderPublishingDate;
	}


	/**
	 * Sets the rotation order publishing date.
	 *
	 * @param rotationOrderPublishingDate the new rotation order publishing date
	 */
	public void setRotationOrderPublishingDate(final Date rotationOrderPublishingDate) {
		this.rotationOrderPublishingDate = rotationOrderPublishingDate;
	}


	/**
	 * Gets the first ballot date.
	 *
	 * @return the first ballot date
	 */
	public Date getFirstBallotDate() {
		return firstBallotDate;
	}


	/**
	 * Sets the first ballot date.
	 *
	 * @param firstBallotDate the new first ballot date
	 */
	public void setFirstBallotDate(final Date firstBallotDate) {
		this.firstBallotDate = firstBallotDate;
	}


	/**
	 * Gets the rotation order text.
	 *
	 * @return the rotation order text
	 */
	public String getRotationOrderText() {
		return rotationOrderText;
	}


	/**
	 * Sets the rotation order text.
	 *
	 * @param rotationOrderText the new rotation order text
	 */
	public void setRotationOrderText(final String rotationOrderText) {
		this.rotationOrderText = rotationOrderText;
	}


    public static Session find(final Integer sessionyear, final String sessiontype, final String housetype) {
     return getSessionRepository().find(sessionyear,sessiontype,housetype);
    }

    }
