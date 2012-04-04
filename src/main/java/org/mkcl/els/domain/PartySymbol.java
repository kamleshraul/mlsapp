/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.PartySymbol.java
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
 * The Class PartySymbol.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_partysymbols")
public class PartySymbol extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The symbol. */
    @Column(length = 200)
    private String symbol;

    /** The change date. */
    @Temporal(TemporalType.DATE)
    private Date changeDate;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new party symbol.
     */
    public PartySymbol() {
        super();
    }

    /**
     * Instantiates a new party symbol.
     *
     * @param symbol the symbol
     * @param changeDate the change date
     */
    public PartySymbol(final String symbol, final Date changeDate) {
        super();
        this.symbol = symbol;
        this.changeDate = changeDate;
    }

    // -------------------------------Getters/Setters---------------------------------------------

    /**
     * Gets the symbol.
     *
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol.
     *
     * @param symbol the new symbol
     */
    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the change date.
     *
     * @return the change date
     */
    public Date getChangeDate() {
        return changeDate;
    }

    /**
     * Sets the change date.
     *
     * @param changeDate the new change date
     */
    public void setChangeDate(final Date changeDate) {
        this.changeDate = changeDate;
    }

}
