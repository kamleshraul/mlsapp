/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.PositionHeldVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

// TODO: Auto-generated Javadoc
/**
 * The Class PositionHeldVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class PositionHeldVO {

	/** The from date. */
	private String fromDate;

    /** The to date. */
    private String toDate;

    /** The position. */
    private String position;

	/**
	 * Gets the from date.
	 *
	 * @return the from date
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 *
	 * @param fromDate the new from date
	 */
	public void setFromDate(final String fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Gets the to date.
	 *
	 * @return the to date
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 *
	 * @param toDate the new to date
	 */
	public void setToDate(final String toDate) {
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
