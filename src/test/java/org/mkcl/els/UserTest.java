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
import java.util.List;

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
        final User user = new User("abcFive", "abcFive", true, "fnameFive",
                "mnameFive", "lnameFive", "userFive@test.com", new Date());
        user.persist();
        Assert.assertNotNull("Saved User Data ", user);
    }

    /**
     * Test find by username where user exists.
     */
    @Transactional
    @Test
    public void testFindByUsernameWhereUserExists() {
        final User user = new User("abcFour", "abcFour", true, "fnameFour",
                "mnameFour", "lnameFour", "userFour@test.com", new Date());
        user.persist();
        final User user1 = User.findByUsername(user.getUsername());
        Assert.assertNotNull("find User Data by username", user1);
    }

    /**
     * Test change password of user.
     */
    @Transactional
    @Test
    public void testChangePasswordOfUser() {
        final User user = new User("abcthree", "abcThree", true, "fnameThree",
                "mnameThree", "lnameThree", "userThree@test.com", new Date());
        user.persist();
        final User user1 = User.findByUsername(user.getUsername());
        user1.setPassword("changeP");
        user.update(user1);
        Assert.assertNotNull("change password for User Data ", user);
    }

    /**
     * Test find user by email.
     */
    @Transactional
    @Test
    public void testFindUserByEmail() {
        final User user = new User("abcTwo", "abcTwo", true, "fnameTwo",
                "mnameTwo", "lnameTwo", "userTwo@test.com", new Date());
        user.persist();
        final User user1 = User.findByEmail(user.getEmail());
        Assert.assertEquals("userTwo@test.com", user1.getEmail());
    }

    /**
     * Test find user by first name.
     */
    @Transactional
    @Test
    public void testFindUserByFirstName() {
        final User user1 = new User("abc1", "abc1", true, "fname", "mname1",
                "lname1", "user1@test.com", new Date());
        final User user2 = new User("abc2", "abc2", true, "fname", "mname2",
                "lname2", "user2@test.com", new Date());
        final User user3 = new User("abc3", "abc3", true, "fname", "mname3",
                "lname3", "user3@test.com", new Date());
        user1.persist();
        user2.persist();
        user3.persist();
        final List<User> allUser = User.findByFirstName("fname");
        Assert.assertNotNull("find user by first name", allUser);
    }

    /**
     * Test find user by middle name.
     */
    @Transactional
    @Test
    public void testFindUserByMiddleName() {
        final User user1 = new User("abc1", "abc1", true, "fname1", "mname",
                "lname1", "user1@test.com", new Date());
        final User user2 = new User("abc2", "abc2", true, "fname3", "mname",
                "lname2", "user2@test.com", new Date());
        final User user3 = new User("abc3", "abc3", true, "fname3", "mname",
                "lname3", "user3@test.com", new Date());
        user1.persist();
        user2.persist();
        user3.persist();
        final List<User> allUser = User.findByMiddleName("mname");
        Assert.assertNotNull("find user by last name", allUser);

    }

    /**
     * Test find user by last name.
     */
    @Transactional
    @Test
    public void testFindUserByLastName() {
        final User user1 = new User("abc11", "abc11", true, "fname11",
                "mname11", "lname", "user1@test.com", new Date());
        final User user2 = new User("abc22", "abc22", true, "fname22",
                "mname22", "lname", "user2@test.com", new Date());
        final User user3 = new User("abc33", "abc33", true, "fname33",
                "mname33", "lname", "user3@test.com", new Date());
        user1.persist();
        user2.persist();
        user3.persist();
        final List<User> allUser = User.findByLastName("lname");
        Assert.assertNotNull("find user by last name", allUser);

    }

    /**
     * Test find user by user id.
     */
    @Transactional
    @Test
    public void testFindUserByUserId() {
        final User user = new User("abcOne", "abcOne", true, "fnameOne",
                "mnameOne", "lnameOne", "userOne@test.com", new Date());
        user.persist();
        final Long uid = user.getUid();
        final User user1 = User.findByUserId(uid);
        Assert.assertNotNull("find user by user id", user1);
    }
}
