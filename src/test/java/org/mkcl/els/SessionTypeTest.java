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
import org.mkcl.els.domain.SessionType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class SessionTypeTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		SessionType sessiontype=new SessionType("testSession");
		sessiontype.persist();
	   Assert.assertNotNull("Saved session Data ", sessiontype);

	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		SessionType sessiontype=new SessionType("testSession");
		sessiontype.persist();
		sessiontype.setSessionType("new Session type");
		sessiontype.merge();
	   Assert.assertNotNull("Updated session Data ", sessiontype);

	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		SessionType sessiontype=new SessionType("testSession");
		sessiontype.persist();
		sessiontype.remove();
		Assert.assertNotNull("Deleted session Data ", sessiontype);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		SessionType sessiontype=new SessionType("testSession");
		sessiontype.persist();
		SessionType sessiontype1=SessionType.findById(SessionType.class, sessiontype.getId());
		Assert.assertNotNull("Finding session Data by Id ", sessiontype1);

	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		SessionType sessiontype=new SessionType("testSession");
		sessiontype.persist();
		SessionType sessiontype1=SessionType.findByFieldName(SessionType.class, "sessionType","testSession",sessiontype.getLocale());
		Assert.assertNotNull("Finding session Data by Field Name ", sessiontype1);

	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		SessionType sessiontype=new SessionType("testSession");
		sessiontype.persist();
		List<SessionType> listsessiontypes=SessionType.findAll(SessionType.class, "sessionType", "desc", sessiontype.getLocale());
		Assert.assertNotNull("Finding session Data by Field Name ", listsessiontypes);

	}

}
