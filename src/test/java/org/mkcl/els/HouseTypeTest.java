/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.HouseTypeTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.HouseType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class HouseTypeTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class HouseTypeTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		 HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		 assemblycounciltype.persist();
       Assert.assertNotNull("Saved assemblyCouncilType Data ", assemblycounciltype);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		assemblycounciltype.persist();
		assemblycounciltype.remove();
		Assert.assertNotNull("Removed assemblyRole Data ", assemblycounciltype);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		assemblycounciltype.persist();
		assemblycounciltype = HouseType.findById(HouseType.class,assemblycounciltype.getId());
	     Assert.assertNotNull("testFindByFieldName assemblycounciltype Data ", assemblycounciltype);
	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
	HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
	assemblycounciltype.persist();
	assemblycounciltype = HouseType.findByFieldName(HouseType.class, "type","testAssemblyCouncilType", assemblycounciltype.getLocale());
      Assert.assertNotNull("testFindByFieldName assemblycounciltype Data ", assemblycounciltype);
	}

	
	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		assemblycounciltype.persist();
       List<HouseType> listAssemblyCouncilType = HouseType.findAll(HouseType.class,"type", "desc", "en");
       Assert.assertNotNull("testFindAllSorted AssemblyRole Data ", listAssemblyCouncilType);
	}

}
