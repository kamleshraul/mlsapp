/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MinistryVO.java
 * Created On: Jun 27, 2013
 */
package org.mkcl.els.common.vo;

/**
 * The Class MinistryVO.
 *
 * @author dhananjayb 
 * @since v1.0.0
 */
public class MinistryVO {

/** The id. */
private Long id;

/** The number. */
private String number;

/** The name. */
private String name;

public MinistryVO() {
	
}

/**
 * Instantiates a new ministry vo.
 *
 * @param id the id
 * @param number the number
 * @param name the name
 */
public MinistryVO(final Long id, final String number, final String name) {
	super();
	this.id = id;
	this.number = number;
	this.name = name;
}

/**
 * Gets the id.
 *
 * @return the id
 */
public Long getId() {
	return id;
}

/**
 * Sets the id.
 *
 * @param id the new id
 */
public void setId(final Long id) {
	this.id = id;
}

public String getNumber() {
	return number;
}

public void setNumber(String number) {
	this.number = number;
}

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

}
