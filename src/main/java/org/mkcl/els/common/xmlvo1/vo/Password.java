/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.Password.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class Password used for storing the changed password information.
 *
 * @author vishals
 * @version v1.0.0
 */
public class Password {

    /** The old password. */
    private String oldPassword;

    /** The new password. */
    private String newPassword;

    /** The confirm password. */
    private String confirmPassword;

    /**
     * Instantiates a new password.
     */
    public Password() {

    }

    /**
     * Instantiates a new password.
     *
     * @param oldPassword the old password
     * @param newPassword the new password
     * @param confirmPassword the confirm password
     */
    public Password(final String oldPassword,
            final String newPassword,
            final String confirmPassword) {
        super();
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    /**
     * Gets the old password.
     *
     * @return the old password
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Sets the old password.
     *
     * @param oldPassword the new old password
     */
    public void setOldPassword(final String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Gets the new password.
     *
     * @return the new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the new password.
     *
     * @param newPassword the new new password
     */
    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Gets the confirm password.
     *
     * @return the confirm password
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Sets the confirm password.
     *
     * @param confirmPassword the new confirm password
     */
    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
