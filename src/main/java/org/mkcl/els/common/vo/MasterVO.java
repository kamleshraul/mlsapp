/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MasterVO.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class MasterVO.
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MasterVO {

	private Long id;

    /** The name. */
    private String name;


	public MasterVO(final Long id, final String name) {
		super();
		this.id = id;
		this.name = name;
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

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}
}
