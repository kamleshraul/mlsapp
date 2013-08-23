/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ConstituencyTest.java
 * Created On: 26 Jul, 2012
 */
package org.mkcl.els;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.State;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstituencyTest2.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
public class ConstituencyTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Constituency constituency = new Constituency();
		constituency.setName("testConstituency1");
		constituency.persist();		
		Assert.assertNotNull("Saved Constituency Data ", constituency);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Constituency constituency = new Constituency();
		constituency.setName("testConstituency2");
		constituency.persist();
		constituency.setName("new Constituency");
		constituency.merge();
		Assert.assertNotNull("Updated Constituency Data ", constituency);

	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Constituency constituency = new Constituency();
		constituency.setName("testConstituency3");
		constituency.persist();
		constituency.remove();
		Assert.assertNotNull("Deleted Constituency Data ", constituency);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Constituency constituency = new Constituency();
		constituency.setName("testConstituency4");
		constituency.persist();
		Constituency constituency2 = Constituency.findById(Constituency.class, constituency.getId());
		Assert.assertNotNull("Getting Constituency Data by ID ", constituency2);
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		Constituency constituency = new Constituency();
		constituency.setName("testConstituency5");
		constituency.persist();
		Constituency constituency2 = Constituency.findByName(Constituency.class, "testConstituency5", constituency.getLocale());
		Assert.assertNotNull("Getting Constituency Data by Field Name ", constituency2);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Constituency constituency = new Constituency();
		constituency.setName("testConstituency");
		constituency.persist();
		List<Constituency> listConstituency = Constituency.findAll(Constituency.class, "name", "desc", "en");
		Assert.assertNotNull("Getting All Constituency Data ", listConstituency);
	}
	
	//commented because native query execution gives sql grammar exception
	
//	/**
//	 * Test find vo by default state and house type.
//	 */
//	@Test
//	@Transactional
//	public void testFindVOByDefaultStateAndHouseType() {		
//		CustomParameter defaultState = new CustomParameter("DEFAULT_STATE", "Maharashtra", true, "");
//		defaultState.setLocale("mr_IN");
//		defaultState.persist();
//		//CustomParameter defaultState = CustomParameter.findByFieldName(CustomParameter.class, "name", "DEFAULT_STATE", "mr_IN");
//		//HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", "lowerhouse", "mr_IN");
//		HouseType houseType = new HouseType("lowerhouse", "Assembly");
//		houseType.setLocale("mr_IN");
//		houseType.persist();
//		State state = new State("Maharashtra");
//		state.setLocale("mr_IN");
//		state.persist();
//		Division division = new Division("division1", state);
//		division.setLocale("mr_IN");
//		division.persist();
//		District district = new District("district1", division);
//		district.setLocale("mr_IN");
//		district.persist();
//		List<District> districts = new ArrayList<District>();
//		districts.add(district);
//		Constituency constituency = new Constituency();
//		constituency.setName("testConstituency");
//		constituency.setDisplayName("c1");
//		constituency.setNumber("21483");
//		constituency.setLocale("mr_IN");
//		constituency.setHouseType(houseType);
//		constituency.setDistricts(districts);
//		constituency.persist();
//		List<MasterVO> result = Constituency.findVOByDefaultStateAndHouseType(defaultState.getValue(), houseType.getType(), "mr_IN", "name", "asc");
//		Assert.assertEquals(true, result.size()>0);
//	}
//	
//	/**
//	 * Test find all by house type.
//	 */
//	@Test
//	@Transactional
//	public void testFindAllByHouseType() {
//		HouseType houseType = new HouseType("lowerhouse", "Assembly");
//		houseType.setLocale("mr_IN");
//		houseType.persist();
//		Constituency constituency = new Constituency();
//		constituency.setName("testConstituency");
//		constituency.setDisplayName("c1");
//		constituency.setNumber("21483");
//		constituency.setLocale("mr_IN");
//		constituency.setHouseType(houseType);
//		constituency.persist();
//		//HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", "lowerhouse", "mr_IN");
//		List<MasterVO> result = Constituency.findAllByHouseType(houseType.getType(), "mr_IN");
//		Assert.assertEquals(true, result.size()>0);
//	}

}
