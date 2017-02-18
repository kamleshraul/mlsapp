/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MasterVO.java
 * Created On: May 13 2015
 */
package org.mkcl.els.common.vo;

/**
 * The Class MasterVO.
 * @author dhananjayb
 * @since v1.0.0
 */
public class SessionVO {
	
	/** The id. */
	private Long id;
	
	/** The description. */
	private String description;
	
	/** The order. */
	private Long order;
	
	//=============== CONSTRUCTORS ==================
	public SessionVO() {
		
	}

	/**
	 * @param id
	 * @param description
	 */
	public SessionVO(Long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}	
	
	//=============== GETTERS/SETTERS ===============
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the order
	 */
	public Long getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Long order) {
		this.order = order;
	}
	
}