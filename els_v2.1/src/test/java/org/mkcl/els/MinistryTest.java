///**
// * See the file LICENSE for redistribution information.
// *
// * Copyright (c) 2012 MKCL.  All rights reserved.
// *
// * Project: e-Legislature
// * File: org.mkcl.els.MinistryTest.java
// * Created On: Jul 25, 2012
// */
//package org.mkcl.els;
//
//import java.util.List;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//import org.mkcl.els.domain.CustomParameter;
//import org.mkcl.els.domain.ElectionType;
//import org.mkcl.els.domain.HouseType;
//import org.mkcl.els.domain.Member;
//import org.mkcl.els.domain.MemberMinister;
//import org.mkcl.els.domain.Ministry;
//import org.springframework.transaction.annotation.Transactional;
//
//// TODO: Auto-generated Javadoc
///**
// * The Class MinistryTest.
// *
// * @author Anand
// * @since v1.0.0
// */
//public class MinistryTest extends AbstractTest{
//
//	/**
//	 * Test persist.
//	 */
//	@Test
//	@Transactional
//	public void testPersist() {
//		Ministry ministry=new Ministry("testMinistry", false, null);
//		ministry.persist();
//	    Assert.assertNotNull("Saving Ministry Data", ministry);
//
//	}
//
//	/**
//	 * Test merge.
//	 */
//	@Test
//	@Transactional
//	public void testMerge() {
//		Ministry ministry=new Ministry("testMinistry", false, null);
//		ministry.persist();
//		ministry.setName("NEw Ministry");
//		ministry.merge();
//	    Assert.assertNotNull("Updating ministry Data", ministry);
//	}
//
//	/**
//	 * Test remove.
//	 */
//	@Test
//	@Transactional
//	public void testRemove() {
//		Ministry ministry=new Ministry("testMinistry", false, null);
//		ministry.persist();
//		ministry.remove();
//	    Assert.assertNotNull("Deleting ministry Data", ministry);
//
//	}
//
//	/**
//	 * Test find by id.
//	 */
//	@Test
//	@Transactional
//	public void testFindById() {
//		Ministry ministry=new Ministry("testMinistry", false, null);
//		ministry.persist();
//		Ministry ministry1=Ministry.findById(Ministry.class,ministry.getId());
//	    Assert.assertNotNull("Finding ElectionType Data from Id", ministry1);
//
//	}
//
//	/**
//	 * Test find by field name.
//	 */
//	@Test
//	@Transactional
//	public void testFindByFieldName() {
//		Ministry ministry=new Ministry("testMinistry", false, null);
//		ministry.persist();
//		Ministry ministry1=Ministry.findByFieldName(Ministry.class, "name", "testMinistry", ministry.getLocale());
//	    Assert.assertNotNull("Finding Ministry Data from Field Names", ministry1);
//	
//	}
//
//	/**
//	 * Test find all.
//	 */
//	@Test
//	@Transactional
//	public void testFindAll() {
//		Ministry ministry=new Ministry("testMinistry", false, null);
//		ministry.persist();
//		List<Ministry> listministry = Ministry.findAll(Ministry.class,"name", "desc", "en");
//	    Assert.assertNotNull("testFindAllSorted Ministry Data ", listministry);
//	}
//
//	/**
//	 * Test find unassigned ministries.
//	 */
//	@Test
//	@Transactional
//	public void testFindUnassignedMinistries() {
//		CustomParameter customParameter=new CustomParameter("DB_DATEFORMAT", "yyyy-MM-dd", true, "");
//		customParameter.persist();
//		Ministry ministry=new Ministry("testMinistry", false, null);
//		ministry.setLocale("mr_IN");
//		ministry.persist();
//		Ministry ministry1=new Ministry("testMinistry1", false, null);
//		ministry1.setLocale("mr_IN");
//		ministry1.persist();
//		Member member=new Member();
//		member.setLocale("mr_IN");
//		member.persist();
//		MemberMinister m=new MemberMinister();
//		m.setMinistry(ministry1);
//		m.setMember(member);
//		m.setLocale("mr_IN");
//		m.persist();
//		List<Ministry> ministries=Ministry.findUnassignedMinistries("mr_IN");
//		Assert.assertEquals(true,ministries.size()>0);
//	}
//
//}
