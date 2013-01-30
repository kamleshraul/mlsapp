/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.QuestionVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class QuestionVO {

	//=============== ATTRIBUTES ====================
	/** The id. */
	private Long id;

	/** The number. */
	private Integer number;

	/** The status. */
	private String status;

	private Boolean hasParent;

	//=============== CONSTRUCTORS ==================
	/**
	 * Instantiates a new question vo.
	 */
	public QuestionVO() {
		super();
	}

	/**
	 * Instantiates a new question vo.
	 *
	 * @param id the id
	 * @param number the number
	 * @param status the status
	 */
	public QuestionVO(final Long id, final Integer number, final String status) {
		super();
		this.setId(id);
		this.setNumber(number);
		this.setStatus(status);
	}


	//=============== GETTERS/SETTERS ===============
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
	 * @param number the new number
	 */
	public void setNumber(final Integer number) {
		this.number = number;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	public Boolean getHasParent() {
		return hasParent;
	}

	public void setHasParent(Boolean hasParent) {
		this.hasParent = hasParent;
	}
}

