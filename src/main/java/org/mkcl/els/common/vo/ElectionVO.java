/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.ElectionVO.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class ElectionVO.
 *
 * @author amitd
 * @author sandeeps   Shubham
 * @since v1.0.0
 */
public class ElectionVO {

/** The id. */
private Long id;

/** The name. */
private String name;

/**
 * Instantiates a new election vo.
 *
 * @param id the id
 * @param name the name
 */
public ElectionVO(final Long id, final String name) {
	super();
	this.id = id;
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
