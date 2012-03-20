/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ReligionTest.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Religion;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ReligionTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class ReligionTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Religion religion=new Religion("testReligion");
		religion.persist();
	    Assert.assertNotNull("Saved religion Data ", religion);

	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Religion religion=new Religion("testReligion");
		religion.persist();
		religion.setReligion("newReligion");
	    Assert.assertNotNull("Updated religion Data ", religion);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Religion religion=new Religion("testReligion");
		religion.persist();
		religion.remove();
	    Assert.assertNotNull("Deleted religion Data ", religion);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Religion religion=new Religion("testReligion");
		religion.persist();
		Religion religion1=Religion.findById(Religion.class, religion.getId());
	    Assert.assertNotNull("Finding  religion Data by Id", religion1);

	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		Religion religion=new Religion("testReligion");
		religion.persist();
		Religion religion1=Religion.findByFieldName(Religion.class, "religion","testReligion",religion.getLocale());
	    Assert.assertNotNull("Finding  religion Data by Field Name", religion1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Religion religion=new Religion("testReligion");
		religion.persist();
		List<Religion> religion1=Religion.findAll(Religion.class, "religion","desc",religion.getLocale());
	    Assert.assertNotNull("Finding  religion Data by Field Name", religion1);
	}

}
