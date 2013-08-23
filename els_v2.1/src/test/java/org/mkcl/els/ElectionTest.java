/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ElectionTest.java
 * Created On: 25 Jul, 2012
 */
package org.mkcl.els;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.HouseType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ElectionTest.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
public class ElectionTest extends AbstractTest {	
	
	/** The id. */
	static Long id;
	
	/**
	 * Sets the test data.
	 *
	 * @throws ParseException the parse exception
	 */
	@Transactional
	public void setTestData() throws ParseException {		
		HouseType houseType=new HouseType("lowerhouse","Lowerhouse");
		houseType.setLocale("mr_IN");
		houseType.persist();
		ElectionType electionType = new ElectionType("electionType1", houseType);
		electionType.setLocale("mr_IN");
		electionType.persist();
		Election election = new Election();
		election.setLocale("mr_IN");
		election.setName("abc");
		election.setElectionType(electionType);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		election.setFromDate(df.parse("01/01/2012"));
		election.setToDate(df.parse("05/01/2012"));
		election.persist();
		id = election.getId();
	}

	/**
	 * Test persist.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	@Transactional	
	public void testPersist() throws ParseException {			
		HouseType houseType=new HouseType("lowerhouse","Lowerhouse");
		houseType.setLocale("mr_IN");
		houseType.persist();
		ElectionType electionType = new ElectionType("electionType1", houseType);
		electionType.setLocale("mr_IN");
		electionType.persist();
		Election election = new Election();
		election.setLocale("mr_IN");
		election.setName("abc");
		election.setElectionType(electionType);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		election.setFromDate(df.parse("01/01/2012"));
		election.setToDate(df.parse("05/01/2012"));			
		Assert.assertNotNull("Persistence of  Election Failed ", election.persist());
		id = election.getId();			
	}
	
	/**
	 * Test remove.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	@Transactional	
	public void testRemove() throws ParseException {
		setTestData();
		Assert.assertTrue(Election.findById(Election.class, id).remove());
	}
	
	/**
	 * Test find by id.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	@Transactional	
	public void testFindById() throws ParseException {
		setTestData();		
		Assert.assertNotNull("Election Find By ID Failed ", Election.findById(Election.class, id));
	}
	
	/**
	 * Test merge.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	@Transactional	
	public void testMerge() throws ParseException {
		setTestData();
		Election addrBeforeMerge = Election.findById(Election.class, id);
		addrBeforeMerge.setName("pqr");
		addrBeforeMerge.merge();
		Election addrAfterMerge = Election.findById(Election.class, addrBeforeMerge.getId());
		Assert.assertTrue("Updation of Election Failed ", addrAfterMerge.getName().equals("pqr"));
	}	
	
	/**
	 * Test find all.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	@Transactional	
	public void testFindAll() throws ParseException {
		setTestData();		
		List<Election> elections = Election.findAll(Election.class, "name", "asc", "mr_IN");		
		Assert.assertEquals(true, elections.size()>0);		
	}	
	
	/**
	 * Test find by field name.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	@Transactional	
	public void testFindByFieldName() throws ParseException {
		setTestData();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");		
		Election election = Election.findByFieldName(Election.class, "fromDate", df.parse("01/01/2012"), "mr_IN");
		Assert.assertEquals(df.parse("01/01/2012"), election.getFromDate());		
	}	
	
	/**
	 * Test find all by object field name.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test	
	@Transactional
	public void testFindAllByObjectFieldName() throws ParseException {
		setTestData();			
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
		List<Election> elections = Election.findAllByFieldName(Election.class, "fromDate", df.parse("01/01/2012"), "name", "asc", "mr_IN");
		Assert.assertEquals(true, elections.size()>0);		
	}
	
	/**
	 * Test find by house type.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	@Transactional	
	public void testFindByHouseType() throws ParseException {
		try {
			setTestData();		
			List<Election> elections = Election.findByHouseType("lowerhouse", "mr_IN");
			Assert.assertEquals(true, elections.size()>0);
		} catch (ELSException e) {
			e.printStackTrace();
		}
	}

}
