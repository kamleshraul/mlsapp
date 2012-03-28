package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.springframework.transaction.annotation.Transactional;

public class HouseTest extends AbstractTest{

	@Test
	@Transactional
	public void testPersist() {
		HouseType housetype=new HouseType("testHouseType");
		housetype.persist();
		Date d=new Date(1929-10-12);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		Assert.assertNotNull("Saved House Data ",house);
	}

	@Test
	@Transactional
	public void testMerge() {
		HouseType housetype=new HouseType("testHouseType");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		house.setName("testname2");
		house.merge();
		Assert.assertNotNull("Updated House Data ",house);
	}

	@Test
	@Transactional
	public void testRemove() {
		HouseType housetype=new HouseType("testHouseType");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		house.remove();
		Assert.assertNotNull("Removed House Data ",house);
	}

	@Test
	@Transactional
	public void testFindById() {
		HouseType housetype=new HouseType("testHouseType");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		House house1=House.findById(House.class, house.getId());
		Assert.assertNotNull("Finding  House Data by id ",house1);
	}

	@Test
	@Transactional
	public void testFindByFieldNameClassStringStringString() {
		HouseType housetype=new HouseType("testHouseType");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		House house1=House.findByFieldName(House.class, "name", "testname", house.getLocale());
		Assert.assertNotNull("Finding  House Data by field name ",house1);
	}

	@Test
	@Transactional
	public void testFindAll() {
		HouseType housetype=new HouseType("testHouseType");
		housetype.persist();
		Date d=new Date(12/10/1929);
		House house=new House("testname", 2, housetype, d);
		house.persist();
		List<House> houses=House.findAll(House.class,"name","desc",house.getLocale());
		Assert.assertNotNull("Finding  House Data by field name ",houses);
	}

}
