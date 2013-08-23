/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.GroupVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class GroupVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class GroupVO {

	/** The ministries. */
	private List<MasterVO> ministries;

	/** The departments. */
	private List<MasterVO> departments;

	/** The sub departments. */
	private List<MasterVO> subDepartments;

	/** The answering dates. */
	private List<Reference> answeringDates;

	/**
	 * Gets the ministries.
	 *
	 * @return the ministries
	 */
	public List<MasterVO> getMinistries() {
		return ministries;
	}

	/**
	 * Sets the ministries.
	 *
	 * @param ministries the new ministries
	 */
	public void setMinistries(final List<MasterVO> ministries) {
		this.ministries = ministries;
	}

	/**
	 * Gets the departments.
	 *
	 * @return the departments
	 */
	public List<MasterVO> getDepartments() {
		return departments;
	}

	/**
	 * Sets the departments.
	 *
	 * @param departments the new departments
	 */
	public void setDepartments(final List<MasterVO> departments) {
		this.departments = departments;
	}


    /**
     * Gets the answering dates.
     *
     * @return the answering dates
     */
    public List<Reference> getAnsweringDates() {
        return answeringDates;
    }


    /**
     * Sets the answering dates.
     *
     * @param answeringDates the new answering dates
     */
    public void setAnsweringDates(final List<Reference> answeringDates) {
        this.answeringDates = answeringDates;
    }


    /**
     * Gets the sub departments.
     *
     * @return the sub departments
     */
    public List<MasterVO> getSubDepartments() {
        return subDepartments;
    }


    /**
     * Sets the sub departments.
     *
     * @param subDepartments the new sub departments
     */
    public void setSubDepartments(final List<MasterVO> subDepartments) {
        this.subDepartments = subDepartments;
    }
}
