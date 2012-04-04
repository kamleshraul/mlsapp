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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Session.
 *
 * @author anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "sessions")
@JsonIgnoreProperties({ "house" })
public class Session extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The number. */
    private Integer number;

    /** The start date. */
    @Temporal(TemporalType.DATE)
    private Date startDate;

    /** The end date. */
    @Temporal(TemporalType.DATE)
    private Date endDate;

    /** The type. */
    @OneToOne
    @JoinColumn(name = "sessiontype_id")
    private SessionType type;

    /** The place. */
    @OneToOne
    @JoinColumn(name = "sessionplace_id")
    private SessionPlace place;

    /** The year. */
    private Integer year;

    /** The duration in days. */
    private Integer durationInDays;

    /** The duration in hrs. */
    private Integer durationInHrs;

    /** The duration in mins. */
    private Integer durationInMins;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The house. */
    @ManyToOne
    @JoinColumn(name = "house_id")
    private House house;


    // -------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new session.
     */
    public Session() {
        super();
    }

    /**
     * Instantiates a new session.
     *
     * @param number the number
     * @param startDate the start date
     * @param endDate the end date
     * @param type the type
     * @param place the place
     * @param year the year
     * @param house the house
     */
    public Session(
            final Integer number,
            final Date startDate,
            final Date endDate,
            final SessionType type,
            final SessionPlace place,
            final Integer year,
            final House house) {
        super();
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.place = place;
        this.year = year;
        this.house = house;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------Getters/Setters-----------------------
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

}
