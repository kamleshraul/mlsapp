/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.UserTest.java
 * Created On: Jan 6, 2012
 */

package org.mkcl.els;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class UserTest.
 */
public class UserTest extends AbstractTest {

    /**
     * Test create user with all param.
     */
    @Transactional
    @Test
    public void testCreateUserWithAllParam() {
        final User user = new User("abc", "abc", true, "fname", "mname",
                "lname", "user@test.com", new Date());
        User.getUserRepository().persist(user);
        Assert.assertNotNull("Saved User Data ", user);
    }

    /**
     * Test find by username where user exists.
     */
    @Test
    public void testFindByUsernameWhereUserExists() {
        final String expectedResult = "vishals";
        final User user = User.getUserRepository().findByUsername("vishals");
        Assert.assertEquals(expectedResult, user.getUsername());
    }

    /**
     * Test change password of user.
     */
    @Transactional
    @Test
    public void testChangePasswordOfUser() {
        final String changedPassword = "changeP";
        final User user = User.getUserRepository().findByUsername("vishals");
        user.setPassword(changedPassword);
        user.update(user);
        Assert.assertNotNull("change password for User Data ", user);
    }
}
