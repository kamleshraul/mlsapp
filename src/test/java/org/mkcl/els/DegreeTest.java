/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.DegreeTest.java
 * Created On: May 11, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.mkcl.els.domain.Degree;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class DegreeTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class DegreeTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Degree degree=new Degree("TestDegree");
		degree.persist();
		Assert.assertNotNull("Saved Degree Data ", degree);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Degree degree=new Degree("testDegree");
		degree.persist();
		degree.setName("new Degree");
		degree.merge();
		Assert.assertNotNull("Updated Degree Data ", degree);

	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Degree degree=new Degree("testDegree");
		degree.persist();
		degree.remove();
		Assert.assertNotNull("Deleted Degree Data ", degree);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Degree degree=new Degree("testDegree");
		degree.persist();
		Degree degree2=Degree.findById(Degree.class,degree.getId());
		Assert.assertNotNull("Getting Degree Data by ID ", degree2);

				
	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		Degree degree=new Degree("testDegree");
		degree.persist();
		Degree degree2=Degree.findByFieldName(Degree.class, "name", "testDegree", degree.getLocale());
		Assert.assertNotNull("Getting Degree Data by Field Name ", degree2);

	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Degree degree=new Degree("testDegree");
		degree.persist();
	    List<Degree> listdegree = Degree.findAll(Degree.class,"name", "desc", "en");
		Assert.assertNotNull("Getting All Degree Data ", listdegree);


	}

}
