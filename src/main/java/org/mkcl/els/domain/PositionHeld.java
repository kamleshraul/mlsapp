/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.PositionHeld.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class PositionHeld.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "positions_held")
public class PositionHeld extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The from date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /** The to date. */
    @Temporal(TemporalType.DATE)
    private Date toDate;

    /** The position. */
    @Column(length = 1000)
    private String position;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new position held.
     */
    public PositionHeld() {
        super();
    }

    /**
     * Instantiates a new position held.
     *
     * @param fromDate the from date
     * @param toDate the to date
     * @param position the position
     */
    public PositionHeld(final Date fromDate, final Date toDate, final String position) {
        super();
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.position = position;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------


    /**
     * Gets the from date.
     *
     * @return the from date
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Sets the from date.
     *
     * @param fromDate the new from date
     */
    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets the to date.
     *
     * @param toDate the new to date
     */
    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(final String position) {
        this.position = position;
    }

}
