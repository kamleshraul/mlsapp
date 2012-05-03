/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.House.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.AutoPopulatingList;

/**
 * The Class House.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "houses")
public class House extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 300)
    private String name;

    /** The number. */
    private Integer number;

    /** The type. */
    @OneToOne
    @JoinColumn(name = "housetype_id")
    private HouseType type;

    /** The formation date. */
    @Temporal(TemporalType.DATE)
    private Date formationDate;

    /** The dissolve date. */
    @Temporal(TemporalType.DATE)
    private Date dissolveDate;

    /** The first date. */
    @Temporal(TemporalType.DATE)
    private Date firstDate;

    /** The last date. */
    @Temporal(TemporalType.DATE)
    private Date lastDate;

    /** The governor address date. */
    @Temporal(TemporalType.DATE)
    private Date governorAddressDate;

    /** The total members. */
    private Integer totalMembers;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The sessions. */
    @OneToMany
    @JoinColumn(name = "house_id", referencedColumnName = "id")
    private List<Session> sessions = new ArrayList<Session>();

    @Autowired
    private transient HouseRepository houseRepository;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new house.
     */
    public House() {
        super();
        AutoPopulatingList<Session> sessionsAuto = new AutoPopulatingList<Session>(
                this.sessions, Session.class);
    }

    /**
     * Instantiates a new house.
     *
     * @param name the name
     * @param number the number
     * @param type the type
     * @param formationDate the formation date
     */
    public House(final String name, final Integer number, final HouseType type, final Date formationDate) {
        super();
        this.name = name;
        this.number = number;
        this.type = type;
        this.formationDate = formationDate;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    public static HouseRepository getHouseRepository() {
        HouseRepository houseRepository = new House().houseRepository;
        if (houseRepository == null) {
            throw new IllegalStateException(
                    "HouseRepository has not been injected in House Domain");
        }
        return houseRepository;
    }

    public static House findCurrentHouse(final String locale) {
        return getHouseRepository().findCurrentHouse(locale);
    }

    public static House findHouseByToFromDate(final Date fromDate, final Date toDate,
            final String locale) {
        return getHouseRepository().findHouseByToFromDate(fromDate, toDate,
                locale);
    }

    public static List<House> findByHouseType(final String houseType, final String locale) {
    	return getHouseRepository().findByHouseType(houseType, locale);
	}

    // ------------------------------------------Getters/Setters-----------------------------------

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
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
     * Gets the type.
     *
     * @return the type
     */
    public HouseType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(final HouseType type) {
        this.type = type;
    }

    /**
     * Gets the formation date.
     *
     * @return the formation date
     */
    public Date getFormationDate() {
        return formationDate;
    }

    /**
     * Sets the formation date.
     *
     * @param formationDate the new formation date
     */
    public void setFormationDate(final Date formationDate) {
        this.formationDate = formationDate;
    }

    /**
     * Gets the dissolve date.
     *
     * @return the dissolve date
     */
    public Date getDissolveDate() {
        return dissolveDate;
    }

    /**
     * Sets the dissolve date.
     *
     * @param dissolveDate the new dissolve date
     */
    public void setDissolveDate(final Date dissolveDate) {
        this.dissolveDate = dissolveDate;
    }

    /**
     * Gets the first date.
     *
     * @return the first date
     */
    public Date getFirstDate() {
        return firstDate;
    }

    /**
     * Sets the first date.
     *
     * @param firstDate the new first date
     */
    public void setFirstDate(final Date firstDate) {
        this.firstDate = firstDate;
    }

    /**
     * Gets the last date.
     *
     * @return the last date
     */
    public Date getLastDate() {
        return lastDate;
    }

    /**
     * Sets the last date.
     *
     * @param lastDate the new last date
     */
    public void setLastDate(final Date lastDate) {
        this.lastDate = lastDate;
    }

    /**
     * Gets the governor address date.
     *
     * @return the governor address date
     */
    public Date getGovernorAddressDate() {
        return governorAddressDate;
    }

    /**
     * Sets the governor address date.
     *
     * @param governorAddressDate the new governor address date
     */
    public void setGovernorAddressDate(final Date governorAddressDate) {
        this.governorAddressDate = governorAddressDate;
    }

    /**
     * Gets the total members.
     *
     * @return the total members
     */
    public Integer getTotalMembers() {
        return totalMembers;
    }

    /**
     * Sets the total members.
     *
     * @param totalMembers the new total members
     */
    public void setTotalMembers(final Integer totalMembers) {
        this.totalMembers = totalMembers;
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
     * Gets the sessions.
     *
     * @return the sessions
     */
    public List<Session> getSessions() {
        return sessions;
    }

    /**
     * Sets the sessions.
     *
     * @param sessions the new sessions
     */
    public void setSessions(final List<Session> sessions) {
        this.sessions = sessions;
    }
}
