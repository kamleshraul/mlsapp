/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.SessionTest.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Session;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class SessionTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Session session=new Session("testSession");
		session.persist();
	   Assert.assertNotNull("Saved session Data ", session);

	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Session session=new Session("testSession");
		session.persist();
		session.setSessionType("new Session type");
		session.merge();
	   Assert.assertNotNull("Updated session Data ", session);

	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Session session=new Session("testSession");
		session.persist();
		session.remove();
		Assert.assertNotNull("Deleted session Data ", session);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Session session=new Session("testSession");
		session.persist();
		Session session1=Session.findById(Session.class, session.getId());
		Assert.assertNotNull("Finding session Data by Id ", session1);

	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		Session session=new Session("testSession");
		session.persist();
		Session session1=Session.findByFieldName(Session.class, "sessionType","testSession",session.getLocale());
		Assert.assertNotNull("Finding session Data by Field Name ", session1);

	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Session session=new Session("testSession");
		session.persist();
		List<Session> listsession=Session.findAll(Session.class, "sessionType", "desc", session.getLocale());
		Assert.assertNotNull("Finding session Data by Field Name ", listsession);

	}

}
