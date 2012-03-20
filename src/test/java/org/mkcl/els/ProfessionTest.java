/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ProfessionTest.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Profession;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ProfessionTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class ProfessionTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Profession profession=new Profession("testProfession");
		profession.persist();
	    Assert.assertNotNull("Saved Profession Data ", profession);

	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Profession profession=new Profession("testProfession");
		profession.persist();
		profession.setName("New Profession");
	    Assert.assertNotNull("Updated Profession Data ", profession);
	    
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Profession profession=new Profession("testProfession");
		profession.persist();
		profession.remove();
	    Assert.assertNotNull("Deleted Profession Data ", profession);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Profession profession=new Profession("testProfession");
		profession.persist();
		Profession profession1=Profession.findById(Profession.class,profession.getId());
	    Assert.assertNotNull("Finding Profession Data by ID", profession1);


	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		Profession profession=new Profession("testProfession");
		profession.persist();
		Profession profession1=Profession.findByName(Profession.class,profession.getName(),profession.getLocale());
	    Assert.assertNotNull("Finding Profession Data by Name", profession1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Profession profession=new Profession("testProfession");
		profession.persist();
		List<Profession> listprofession=Profession.findAll(Profession.class, "name","desc",profession.getLocale() );
	    Assert.assertNotNull("Finding Profession Data", listprofession);

	}

}
