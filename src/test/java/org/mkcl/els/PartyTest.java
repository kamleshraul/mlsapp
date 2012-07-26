/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.PartyTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Party;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class PartyTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class PartyTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Party party=new Party("testname", "tt", new Date(12-10-2000));
		party.persist();
		Assert.assertNotNull("Saving Party data ", party);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Party party=new Party("testname", "tt", new Date(12-10-2000));
		party.persist();
		party.setName("new name");
		party.merge();
		Assert.assertNotNull("Updating Party data ", party);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Party party=new Party("testname", "tt", new Date(12-10-2000));
		party.persist();
		party.remove();
		Assert.assertNotNull("Removing Party data ", party);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Party party=new Party("testname", "tt", new Date(12-10-2000));
		party.persist();
		Party party1=Party.findById(Party.class, party.getId());
		Assert.assertNotNull("Finding Party data ", party1);
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		Party party=new Party("testname", "tt", new Date(12-10-2000));
		party.persist();
		Party party1=Party.findByName(Party.class, "testname", party.getLocale());
		Assert.assertNotNull("Finding Party data ", party1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Party party=new Party("testname", "tt", new Date(12-10-2000));
		party.persist();
		List<Party> parties=Party.findAll(Party.class, "name", "desc", party.getLocale());
		 Assert.assertEquals(true,parties.size()>0);
	}

}
