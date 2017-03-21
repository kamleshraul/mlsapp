/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberChildrenWiseVO.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class MemberChildrenWiseVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberChildrenWiseVO {

	/** The children. */
	private String children;

    /** The total member. */
    private String totalMember;

    /** The total female. */
    private String totalFemale;

    /** The total male. */
    private String totalMale;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public String getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(final String children) {
		this.children = children;
	}

	/**
	 * Gets the total member.
	 *
	 * @return the total member
	 */
	public String getTotalMember() {
		return totalMember;
	}

	/**
	 * Sets the total member.
	 *
	 * @param totalMember the new total member
	 */
	public void setTotalMember(final String totalMember) {
		this.totalMember = totalMember;
	}

	/**
	 * Gets the total female.
	 *
	 * @return the total female
	 */
	public String getTotalFemale() {
		return totalFemale;
	}

	/**
	 * Sets the total female.
	 *
	 * @param totalFemale the new total female
	 */
	public void setTotalFemale(final String totalFemale) {
		this.totalFemale = totalFemale;
	}

	/**
	 * Gets the total male.
	 *
	 * @return the total male
	 */
	public String getTotalMale() {
		return totalMale;
	}

	/**
	 * Sets the total male.
	 *
	 * @param totalMale the new total male
	 */
	public void setTotalMale(final String totalMale) {
		this.totalMale = totalMale;
	}
}
