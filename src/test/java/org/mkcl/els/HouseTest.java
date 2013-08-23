/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.HouseTest.java
 * Created On: Apr 9, 2012
 */
package org.mkcl.els;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class HouseTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class HouseTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		HouseType housetype=new HouseType("testHouseType","testhouse");
		housetype.persist();
		Date d=new Date(1929-10-12);
		House house=new House("testname", 2, housetype, d);
		NumberFormat nf = NumberFormat.getInstance();
 		DecimalFormat df = (DecimalFormat) nf;
 		DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
 		dfs.setZeroDigit('\u0966');
 		df.setDecimalFormatSymbols(dfs);
 		house.persist();
		Assert.assertNotNull("Saved House Data ",house);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		HouseType housetype=new HouseType("testHouseType","testhouse");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		house.setName("testname2");
		house.merge();
		Assert.assertNotNull("Updated House Data ",house);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		HouseType housetype=new HouseType("testHouseType","testhouse");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		house.remove();
		Assert.assertNotNull("Removed House Data ",house);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		HouseType housetype=new HouseType("testHouseType","testhouse");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		House house1=House.findById(House.class, house.getId());
		Assert.assertNotNull("Finding  House Data by id ",house1);
	}

	/**
	 * Test find by field name class string string string.
	 */
	@Test
	@Transactional
	public void testFindByFieldNameClassStringStringString() {
		HouseType housetype=new HouseType("testHouseType","testhouse");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		House house1=House.findByFieldName(House.class, "name", "testname", house.getLocale());
		Assert.assertNotNull("Finding  House Data by field name ",house1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		HouseType housetype=new HouseType("testHouseType","testhouse");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		List<House> houses=House.findAll(House.class,"name","desc",house.getLocale());
		Assert.assertNotNull("Finding  House Data by field name ",houses);
	}

	/**
	 * Test find current house.
	 * @throws ParseException 
	 */
	@Test
	@Transactional
	public void testFindCurrentHouse() throws ParseException{
		try {
			HouseType housetype=new HouseType("testHouseType","testhouse");
			housetype.setLocale("mr_IN");
			housetype.persist();
			DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
			House house=new House("testname", 2, housetype, df.parse("12/01/2012"));
			house.setLastDate(df.parse("12/10/2012"));
			house.setFirstDate(df.parse("12/01/2012"));
			house.setLocale("mr_IN");
			house.persist();
			House h=House.findCurrentHouse(house.getLocale());
			Assert.assertNotNull("Finding  House Data ",h);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test find house by to from date.
	 * @throws ParseException 
	 */
	@Transactional
	@Test
	public void testFindHouseByToFromDate() throws ParseException{
		try {
			HouseType housetype=new HouseType("testHouseType","testhouse");
			housetype.persist();
			DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
			House house=new House("testname", 2, housetype, df.parse("12/01/2012"));
			house.setLastDate(df.parse("12/10/2012"));
			house.setFirstDate(df.parse("12/01/2012"));
			house.persist();
			House h=House.findHouseByToFromDate(df.parse("12/01/2012"),df.parse("12/01/2012"), house.getLocale());
			Assert.assertNotNull("Finding  House Data ",h);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test find by house type.
	 */
	@Transactional
	@Test
	public void testFindByHouseType(){
		try {
			HouseType housetype=new HouseType("testHouseType","testhouse");
			housetype.setLocale("mr_IN");
			housetype.persist();
			Date d=new Date(12/01/2012);
			Date d1=new Date(12/10/2012);
			House house=new House("testname", 2, housetype, d);
			house.setFirstDate(d);
			house.setLastDate(d1);
			house.setLocale("mr_IN");
			house.persist();
			List<House> houses=House.findByHouseType(housetype.getType(), house.getLocale());
			Assert.assertEquals(true,houses.size()>0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
