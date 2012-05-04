//
//package org.mkcl.els;
//import junit.framework.Assert;
//import org.junit.Test;
//import org.mkcl.els.domain.User;
//import org.springframework.transaction.annotation.Transactional;
//
//
//public class UserTest extends AbstractTest {
//   
//    @Transactional
//    @Test
//    public void testCreateUserWithAllParam() {
//        User user = new User("abc", "abc", true, "fname", "mname", "lname");
//        user = (User) user.persist();
//        Assert.assertNotNull("Saved User Data ", user);
//    }
//   
//    @Transactional
//    @Test
//    public void testFindByUsernameWhereUserExists() {
//        User user = new User("abc", "abc", true, "fname", "mname", "lname");
//        user = (User) user.persist();
//        User user1 = User.findByUsername(user.getUsername());
//        Assert.assertNotNull("find User Data by username", user1);
//    }
//
//   
//    @Transactional
//    @Test
//    public void testChangePasswordOfUser() {
//        final String changedPassword = "changeP";
//        User user = new User("abc", "abc", true, "fname", "mname", "lname");
//        user = (User) user.persist();
//        User user1 = User.findByUsername(user.getUsername());
//        user1.setPassword(changedPassword);
//        user1.merge();
//        Assert.assertNotNull("change password for User Data ", user);
//    }
//
//   
//    @Transactional
//    @Test
//    public void testFindUserByEmail() {
//        String expectedResult = "user@test.com";
//        User user = new User("abc", "abc", true, "fname", "mname", "lname");
//        user = (User) user.persist();
//        User user1 = User.findByEmail(user.getEmail());
//        Assert.assertEquals(expectedResult, user1.getEmail());
//    }
//
//   
//    @Transactional
//    @Test
//    public void testFindUserByFirstName() {
//        User user = new User("abc", "abc", true, "fname", "mname", "lname");
//        user = (User) user.persist();
//        User user1 = User.findByFirstName(user.getFirstName());
//        Assert.assertNotNull("find user by first name", user1);
//    }
//   
//    @Transactional
//    @Test
//    public void testFindUserByLastName() {
//        User user = new User("abc", "abc", true, "fname", "mname", "lname");
//        user = (User) user.persist();
//        User user1 = User.findByLastName(user.getLastName());
//        Assert.assertNotNull("find user by last name", user1);
//
//    }
//}
