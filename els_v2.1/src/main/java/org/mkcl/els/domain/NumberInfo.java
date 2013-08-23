/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.NumberInfo.java
 * Created On: May 22, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class NumberInfo.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "number_information")
public class NumberInfo extends BaseDomain implements Serializable {

	/**** Attributes ****/
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    // Reason:Some number text can be very large e.g daha laksha navyannav hajaar 
    // navushe navyannav.So,to accomodate
    // extreme situations we need to allow 200 characters in english and 200*3
    // in marathi.
    /** The name. */
    @Column(length = 600)
    private String numberText;    
    
    /** The value. */
    @Column
    private Long number;

    /**** Constructors ****/

    /**
     * Instantiates a new number information.
     */
    public NumberInfo() {
        super();
    }

    /**
     * Instantiates a new number information.
     * 
     * @param numberText the numberText
     * @param number the number
     * @param locale the locale
     * @param version the version
     */
    public NumberInfo(final String numberText, final Long number, final String locale, final Long version) {
        super();
        this.numberText = numberText;
        this.number = number;
    }

    /**** Domain methods ****/
    
    /**
	 * finds locale specific text of number.
	 * e.g. ek for 1, don for 2, chalis for 40 etc in marathi locale
	 * 
	 * @param number the number whose text is to be obtained
	 * @param locale the locale in which text of number is to be obtained
	 * @return the text of given number in given locale
	 */
	public static String findNumberText(Long number, String locale) {
		String numberText = null;
		NumberInfo numberInfo = getBaseRepository().findByFieldName(NumberInfo.class, "number", number, locale);
		if(numberInfo != null) {
			numberText = numberInfo.getNumberText();
		}
		return numberText;
	}
	

    /**** Getters and Setters ****/
    /**
     * Gets the number text.
     * 
     * @return the number text
     */
    public String getNumberText() {
        return numberText;
    }

    /**
     * Sets the number text.
     * 
     * @param name the new number text
     */
    public void setNumberText(final String numberText) {
        this.numberText = numberText;
    }

	/**
	 * Gets the number
	 * 
	 * @return the number
	 */
	public Long getNumber() {
		return number;
	}

	/**
	 * Sets the number
	 * 
	 * @param number the new number
	 */
	public void setNumber(Long number) {
		this.number = number;
	}
}
