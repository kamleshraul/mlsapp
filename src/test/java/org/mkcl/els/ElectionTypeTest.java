/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ElectionTypeTest.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.ElectionType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ElectionTypeTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class ElectionTypeTest extends AbstractTest {

//	/**
//	 * Test find election type by assembly council type id.
//	 */
//	@Test
//	@Transactional
//	public void testFindElectionTypeByAssemblyCouncilTypeId() {
//		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
//		assemblycounciltype.persist();
//		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);
//		electiontype.persist();
//		List<ElectionType> listelectiontype=ElectionType.findElectionTypeByAssemblyCouncilTypeId(assemblycounciltype.getId(),"electionType","desc");
//	    Assert.assertNotNull("FInding Election Type  Data  from AssemblyCouncilType Id", listelectiontype);
//
//	}
//
//	/**
//	 * Test find election type by assembly council type.
//	 */
//	@Test
//	@Transactional
//	public void testFindElectionTypeByAssemblyCouncilType() {
//		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
//		assemblycounciltype.persist();
//		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);
//		electiontype.persist();
//		List<ElectionType> listelectiontype=ElectionType.findElectionTypeByAssemblyCouncilType(assemblycounciltype.getType(),"electionType","desc");
//	    Assert.assertNotNull("FInding Election Type  Data  from AssemblyCouncilType Type", listelectiontype);
//	}

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
		assemblycounciltype.persist();
		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);
		electiontype.persist();
	    Assert.assertNotNull("Saving ElectionType Data", electiontype);

	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
		assemblycounciltype.persist();
		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);
		electiontype.persist();
		electiontype.setName("NEw Election Type");
		electiontype.merge();
	    Assert.assertNotNull("Updating ElectionType Data", electiontype);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
		assemblycounciltype.persist();
		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);
		electiontype.persist();
		electiontype.remove();
	    Assert.assertNotNull("Deleting ElectionType Data", electiontype);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
		assemblycounciltype.persist();
		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);	
		electiontype.persist();
		ElectionType electiontype1=ElectionType.findById(ElectionType.class,electiontype.getId());
	    Assert.assertNotNull("Finding ElectionType Data from Id", electiontype1);

	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
		assemblycounciltype.persist();
		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);	
		electiontype.persist();
		ElectionType electiontype1=ElectionType.findByFieldName(ElectionType.class, "name", "TestElectionType", electiontype.getLocale());
	    Assert.assertNotNull("Finding ElectionType Data from Field Names", electiontype1);
	
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType");
		assemblycounciltype.persist();
		ElectionType electiontype=new ElectionType("TestElectionType",assemblycounciltype);	
		electiontype.persist();
		List<ElectionType> listElectionType = ElectionType.findAll(ElectionType.class,"name", "desc", "en");
	    Assert.assertNotNull("testFindAllSorted Election Type Data ", listElectionType);
	}

}
