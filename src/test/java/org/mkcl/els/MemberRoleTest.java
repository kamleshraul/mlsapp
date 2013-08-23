/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.MemberRoleTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberRole;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberRoleTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class MemberRoleTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		Assert.assertNotNull("Saving memberrole data ", mr);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		mr.setName("newRole");
		mr.merge();
		Assert.assertNotNull("Updating memberRole data",mr);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		mr.remove();
		Assert.assertNotNull("Removing role data",mr);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		MemberRole mr1=MemberRole.findById(MemberRole.class, mr.getId());
		Assert.assertNotNull("Finding the Role from Id", mr1);
			
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		MemberRole mr1=MemberRole.findByName(MemberRole.class, mr.getName(), mr.getLocale());
		Assert.assertNotNull("Finding Role by Name", mr1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		List<MemberRole> memberoles=MemberRole.findAll(MemberRole.class, "name", "desc", mr.getLocale());
		Assert.assertNotNull("Finding All member Roles",memberoles);
	}

	/**
	 * Test find by name house type locale.
	 */
	@Test
	@Transactional
	public void testFindByNameHouseTypeLocale(){
		HouseType houseType=new HouseType("lowerhouse","test");
		houseType.setLocale("mr_IN");
		houseType.persist();
		MemberRole mr=new MemberRole("testName", 1, houseType);
		mr.setLocale("mr_IN");
		mr.persist();
		MemberRole memberRole=MemberRole.findByNameHouseTypeLocale(mr.getName(), houseType.getId(), mr.getLocale());
		Assert.assertNotNull("Finding member Roles",memberRole);
	}
	
	/**
	 * Test find by house type.
	 */
	@Test
	@Transactional
	public void testFindByHouseType(){
		HouseType houseType=new HouseType("lowerhouse","test");
		houseType.setLocale("mr_IN");
		houseType.persist();
		MemberRole mr=new MemberRole("testName", 1, houseType);
		mr.setLocale("mr_IN");
		mr.persist();
		List<MemberRole> memberRoles=MemberRole.findByHouseType(houseType.getType(), mr.getLocale());
		Assert.assertEquals(true,memberRoles.size()>0);
	}
}
