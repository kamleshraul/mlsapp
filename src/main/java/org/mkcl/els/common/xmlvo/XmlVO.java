/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.xmlvo.XmlVO.java
 * Created On: Sep 17, 2013
 * @since 1.0
 */
package org.mkcl.els.common.xmlvo;

import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlVO.
 *
 * @author dhananjayb
 * @since 1.0
 */
@XmlRootElement
public class XmlVO {
	
	/** The house type. */
	private String houseType;
	
	/** The house type name. */
	private String houseTypeName;
	
	/** The session type name. */
	private String sessionTypeName;
	
	/** The session year name. */
	private String sessionYearName;
	
	/** The session number. */
	private String sessionNumber;
	
	/** The locale. */
	private String locale;
	
	/** The output format. */
	private String outputFormat;
	
	/** The report date. */
	private String reportDate;
	
	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * Gets the output format.
	 *
	 * @return the output format
	 */
	public String getOutputFormat() {
		return outputFormat;
	}

	/**
	 * Sets the output format.
	 *
	 * @param outputFormat the new output format
	 */
	public void setOutputFormat(final String outputFormat) {
		this.outputFormat = outputFormat;
	}

	/**
	 * Gets the report date.
	 *
	 * @return the report date
	 */
	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	/**
	 * Gets the house type.
	 *
	 * @return the houseType
	 */
	public String getHouseType() {
		return houseType;
	}

	/**
	 * Sets the house type.
	 *
	 * @param houseType the houseType to set
	 */
	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	/**
	 * Gets the house type name.
	 *
	 * @return the house type name
	 */
	public String getHouseTypeName() {
		return houseTypeName;
	}

	/**
	 * Sets the house type name.
	 *
	 * @param houseTypeName the new house type name
	 */
	public void setHouseTypeName(String houseTypeName) {
		this.houseTypeName = houseTypeName;
	}

	/**
	 * Gets the session type name.
	 *
	 * @return the session type name
	 */
	public String getSessionTypeName() {
		return sessionTypeName;
	}

	/**
	 * Sets the session type name.
	 *
	 * @param sessionTypeName the new session type name
	 */
	public void setSessionTypeName(String sessionTypeName) {
		this.sessionTypeName = sessionTypeName;
	}

	/**
	 * Gets the session year name.
	 *
	 * @return the session year name
	 */
	public String getSessionYearName() {
		return sessionYearName;
	}

	/**
	 * Sets the session year name.
	 *
	 * @param sessionYearName the new session year name
	 */
	public void setSessionYearName(String sessionYearName) {
		this.sessionYearName = sessionYearName;
	}

	/**
	 * Gets the session number.
	 *
	 * @return the session number
	 */
	public String getSessionNumber() {
		return sessionNumber;
	}

	/**
	 * Sets the session number.
	 *
	 * @param sessionNumber the new session number
	 */
	public void setSessionNumber(String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}	
	
	
	
}
