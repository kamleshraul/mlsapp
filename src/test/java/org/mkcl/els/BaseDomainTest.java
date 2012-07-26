/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.BaseDomainTest.java
 * Created On: 24 Jul, 2012
 */
package org.mkcl.els;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.HouseType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseDomainTest.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
public class BaseDomainTest extends AbstractTest {	

	/**
	 * Test get base repository.
	 */
	@Test
	@Transactional
	public void testGetBaseRepository() {		
		Assert.assertNotNull("Base Repository is NOT injected into Base Domain", HouseType.getBaseRepository());
	}	

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		HouseType houseType = new HouseType("lowerhouse","Assembly");		
		Assert.assertNotNull(houseType.persist());	
	}
	
	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		HouseType houseType = new HouseType("upperhouse","Council");
		houseType.persist();			
		Assert.assertNotNull(HouseType.findById(HouseType.class,houseType.getId()));
	}
	
	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		HouseType houseType = new HouseType("bothhouse","House");
		houseType.persist();
		houseType.remove();
		Assert.assertNull(HouseType.findById(HouseType.class,houseType.getId()));
	}	

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		HouseType houseType = new HouseType("lowerhouse","LowerHouse");
		houseType.setLocale("default");
		houseType.persist();
		houseType.setLocale("en_US");
		houseType.merge();
		HouseType houseTypeAfterMerge = HouseType.findById(HouseType.class,houseType.getId());
		Assert.assertEquals("en_US", houseTypeAfterMerge.getLocale());
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		HouseType houseType = new HouseType("upperhouse","UpperHouse");
		houseType.setLocale("en_US");
		houseType.persist();
		Assert.assertNotNull(HouseType.findByName(HouseType.class,"UpperHouse","en_US"));
	}

	/**
	 * Test find by field name class string string string.
	 */
	@Test
	@Transactional
	public void testFindByFieldNameClassStringStringString() {
		HouseType houseType = new HouseType("bothhouse","BothHouse");
		houseType.setLocale("en_US");
		houseType.persist();
		Assert.assertNotNull(HouseType.findByFieldName(HouseType.class,"name","BothHouse","en_US"));
	}

	/**
	 * Test find by field name class string u string.
	 */
	@Test
	@Transactional
	public void testFindByFieldNameClassStringUString() {
		HouseType houseType = new HouseType("lowerhouse","Assembly");
		houseType.setLocale("en_US");
		houseType.persist();		
		ElectionType electionType = new ElectionType("Assembly Election", houseType);
		electionType.setLocale("en_US");
		electionType.persist();
		Assert.assertNotNull(ElectionType.findByFieldName(ElectionType.class,"houseType",houseType,"en_US"));
	}

	/**
	 * Test find by field names.
	 */
	@Test
	@Transactional
	public void testFindByFieldNames() {
		HouseType houseType = new HouseType("lowerhouse","Assembly");
		houseType.setLocale("en_US");
		houseType.persist();
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("locale", "en_US");
		fields.put("type", "lowerhouse");		
		fields.put("name", "Assembly");
		Assert.assertNotNull(HouseType.findByFieldNames(HouseType.class, fields, "en_US"));
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		setUp();
		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", "asc", "en_US");
		Assert.assertEquals(true, houseTypes.size()>0);
	}

	/**
	 * Test find all by field name class string string string string string.
	 */
	@Test
	@Transactional
	public void testFindAllByFieldNameClassStringStringStringStringString() {
		setUp();
		List<HouseType> houseTypes = HouseType.findAllByFieldName(HouseType.class, "type", "lowerhouse", "name", "asc", "en_US");
		Assert.assertEquals(true, houseTypes.size()>0);
	}

	/**
	 * Test find all by field name class string u string string string.
	 */
	@Test
	@Transactional
	public void testFindAllByFieldNameClassStringUStringStringString() {
		HouseType houseType = (HouseType) HouseType.findByName(HouseType.class, "Assembly", "en_US");
		ElectionType electionType1 = new ElectionType("Special Election", houseType);
		electionType1.setLocale("en_US");
		electionType1.persist();
		ElectionType electionType2 = new ElectionType("General Election", houseType);
		electionType2.setLocale("en_US");
		electionType2.persist();
		List<ElectionType> electionTypes = ElectionType.findAllByFieldName(ElectionType.class, "houseType", houseType, "name", "asc", "en_US");
		Assert.assertEquals(true, electionTypes.size()>0);
	}

	/**
	 * Test is duplicate string string.
	 */
	@Test
	@Transactional
	public void testIsDuplicateStringString() {
		HouseType houseType = new HouseType("lower","lowerh");
		houseType.setLocale("en_US");
		houseType.persist();	
		HouseType houseTypeDuplicate = new HouseType("lower","lowerh");		
		houseTypeDuplicate.setLocale("en_US");
		Assert.assertTrue("House Type already exists", houseTypeDuplicate.isDuplicate("name", houseTypeDuplicate.getName()));
	}

	/**
	 * Test is duplicate map of string string.
	 */
	@Test
	@Transactional
	public void testIsDuplicateMapOfStringString() {
		HouseType houseType = new HouseType("lowerhouse","lowerh1");
		houseType.setLocale("en_US");
		houseType.persist();
		HouseType houseTypeDuplicate = new HouseType("lowerhouse","lowerh1");		
		houseTypeDuplicate.setLocale("en_US");
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("locale", houseTypeDuplicate.getLocale());
		fields.put("type", houseTypeDuplicate.getType());		
		fields.put("name", houseTypeDuplicate.getName());
		Assert.assertTrue("House Type already exists", houseTypeDuplicate.isDuplicate(fields));
	}

	/**
	 * Test is version mismatch.
	 */
	@Test
	@Transactional
	public void testIsVersionMismatch() {
		HouseType houseType1 = new HouseType("lowerhouse","Assembly1");
		houseType1.setLocale("en_US");		
		houseType1.persist();		
		HouseType houseType2 = new HouseType();
		houseType2.setId(houseType1.getId());
		houseType2.setLocale("mr_IN");
		houseType2.setVersion(new Long(1));		
		Assert.assertTrue(houseType2.isVersionMismatch());
	}
	
	/**
	 * Sets the up.
	 */
	@Transactional
	public void setUp() {
		HouseType houseType1 = new HouseType("lowerhouse","Assembly");
		houseType1.setLocale("en_US");
		houseType1.persist();
		HouseType houseType2 = new HouseType("upperhouse","Council");
		houseType2.setLocale("en_US");
		houseType2.persist();
		HouseType houseType3 = new HouseType("bothhouse","BothHouse");
		houseType3.setLocale("en_US");
		houseType3.persist();
	}

}
