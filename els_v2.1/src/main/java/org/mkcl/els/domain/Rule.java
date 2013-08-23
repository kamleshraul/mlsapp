/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Rule.java
 * Created On: May 8, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Rule.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "rules")
public class Rule extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The number. */
    @Column(length = 600)
    private Integer number;
    
    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;

    // ---------------------------------Constructors----------------------//

    /**
     * Instantiates a new rule.
     */
    public Rule() {
        super();
    }

    /**
     * Instantiates a new rule.
     * 
     * @param number the number
     * @param locale the locale
     * @param version the version
     */
    public Rule(final Integer number, final String locale, final Long version) {
        super();
        this.number = number;

    }

    // ----------------------------Domain Methods-------------------------//

    // ----------------------------Getters/Setters------------------------//
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
     * @param name the new number
     */
    public void setNumber(final Integer number) {
        this.number = number;
    }

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}
}
