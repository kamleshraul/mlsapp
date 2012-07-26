/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.UserTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.User;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class UserTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class UserTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		User user=new User();
		user.setFirstName("test");
		user.persist();
		Assert.assertNotNull("Saving User data",user);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		User user=new User();
		user.setFirstName("test");
		user.persist();
		user.setLastName("test last");
		user.merge();
		Assert.assertNotNull("Updating User data",user);
		
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		User user=new User();
		user.setFirstName("test");
		user.persist();
		user.remove();
		Assert.assertNotNull("Removing User data",user);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		User user=new User();
		user.setFirstName("test");
		user.persist();
		User user1=User.findById(User.class, user.getId());
		Assert.assertNotNull("Finding User data",user1);
	}

	/**
	 * Test find by field name class string string string.
	 */
	@Test
	@Transactional
	public void testFindByFieldNameClassStringStringString() {
		User user=new User();
		user.setFirstName("test");
		user.persist();
		User user1=User.findByFieldName(User.class, "firstName", "test", user.getLocale());
		Assert.assertNotNull("Finding User data",user1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		User user=new User();
		user.setFirstName("test");
		user.persist();
		List<User> users=User.findAll(User.class, "firstName", "desc", user.getLocale());
		Assert.assertNotNull("Finding User data",users);
	}

	
	/**
	 * Test assign member id.
	 */
	@Transactional
	@Test
	public void testAssignMemberId(){
		Member m=new Member();
		m.persist();
		User user=new User();
		user.persist();
		User.assignMemberId(m.getId(), user.getId());
		user.persist();
		Assert.assertNotNull("assigning member id to user id",user);
		
		
	}
}
