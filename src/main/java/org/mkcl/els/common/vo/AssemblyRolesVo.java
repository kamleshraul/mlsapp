/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.AssemblyRolesVo.java
 * Created On: Jan 30, 2012
 */
package org.mkcl.els.common.vo;

import java.util.List;

import org.mkcl.els.domain.AssemblyRole;

/**
 * The Class AssemblyRolesVo.
 *
 * @author sandeeps
 * @since v1.0.0
 */
public class AssemblyRolesVo {

    /** The from date. */
    private String fromDate;

    /** The to date. */
    private String toDate;

    /** The roles. */
    private List<AssemblyRole> roles;

    /**
     * Gets the from date.
     *
     * @return the from date
     */
    public String getFromDate() {
        return fromDate;
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
     * Gets the roles.
     *
     * @return the roles
     */
    public List<AssemblyRole> getRoles() {
        return roles;
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
     * Sets the to date.
     *
     * @param toDate the new to date
     */
    public void setToDate(final String toDate) {
        this.toDate = toDate;
    }

    /**
     * Sets the roles.
     *
     * @param roles the new roles
     */
    public void setRoles(final List<AssemblyRole> roles) {
        this.roles = roles;
    }

}
