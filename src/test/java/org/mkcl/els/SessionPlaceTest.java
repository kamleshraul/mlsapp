/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.SessionPlaceTest.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.SessionPlace;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionPlaceTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class SessionPlaceTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		SessionPlace sessionplace=new SessionPlace("testPlace");
		sessionplace.persist();
       Assert.assertNotNull("Saved sessionplace Data ", sessionplace);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		SessionPlace sessionplace=new SessionPlace("testPlace");
		sessionplace.persist();
		sessionplace.setPlace("new Place");
		sessionplace.merge();
       Assert.assertNotNull("Updated sessionplace Data ", sessionplace);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		SessionPlace sessionplace=new SessionPlace("testPlace");
		sessionplace.persist();
		sessionplace.remove();
		Assert.assertNotNull("Deleted sessionplace Data ", sessionplace);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		SessionPlace sessionplace=new SessionPlace("testPlace");
		sessionplace.persist();
		SessionPlace sessionplace1=SessionPlace.findById(SessionPlace.class, sessionplace.getId());
		Assert.assertNotNull("Finding sessionplace Data by Id ", sessionplace1);

	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		SessionPlace sessionplace=new SessionPlace("testPlace");
		sessionplace.persist();
		SessionPlace sessionplace1=SessionPlace.findByFieldName(SessionPlace.class, "place", "testPlace", sessionplace.getLocale());
		Assert.assertNotNull("Finding sessionplace Data by Field Name ", sessionplace1);

	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		SessionPlace sessionplace=new SessionPlace("testPlace");
		sessionplace.persist();
		List<SessionPlace> listSessionplace=SessionPlace.findAll(SessionPlace.class, "place", "desc", sessionplace.getLocale());
		Assert.assertNotNull("Finding sessionplace Data by Field Name ", listSessionplace);
	}

}
