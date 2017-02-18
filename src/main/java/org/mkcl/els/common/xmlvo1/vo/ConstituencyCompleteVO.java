/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.ConstituencyCompleteVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;

/**
 * The Class ConstituencyCompleteVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class ConstituencyCompleteVO {

/** The division. */
private String division;

/** The districts. */
private List<MasterVO> districts;

/**
 * Gets the division.
 *
 * @return the division
 */
public String getDivision() {
	return division;
}

/**
 * Sets the division.
 *
 * @param division the new division
 */
public void setDivision(final String division) {
	this.division = division;
}

/**
 * Gets the districts.
 *
 * @return the districts
 */
public List<MasterVO> getDistricts() {
	return districts;
}

/**
 * Sets the districts.
 *
 * @param districts the new districts
 */
public void setDistricts(final List<MasterVO> districts) {
	this.districts = districts;
}

}
