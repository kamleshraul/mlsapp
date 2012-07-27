/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.DistrictTest.java
 * Created On: 26 Jul, 2012
 */
package org.mkcl.els;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class DistrictTest.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
public class DistrictTest extends AbstractTest {

	/**
	 * Test find districts by division id.
	 */
	@Test
	public void testFindDistrictsByDivisionId() {
		Division division = new Division();
		division.setName("div1");
		division.persist();
		District district = new District("testDistrict", division);
		district.persist();
		List<District> districts = District.getDistrictRepository().findDistrictsByDivisionId(division.getId(), "name", "asc", division.getLocale());
		Assert.assertNotNull("Districts by Division ID is :- ", districts);
	}

	/**
	 * Test find districts by division name.
	 */
	@Test
	public void testFindDistrictsByDivisionName() {
		Division division = new Division();
		division.setName("div1");
		division.persist();
		District district = new District("testDistrict", division);
		district.persist();
		List<District> districts = District.getDistrictRepository().findDistrictsByDivisionName(division.getName(), "name", "asc", division.getLocale());
		Assert.assertNotNull("Districts by Division ID is :- ", districts);
	}

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Division division = new Division();
		division.setName("div1");
		division.persist();
		District district = new District("testDistrict", division);
		district.persist();
		Assert.assertNotNull("Saved District Data ", district);
	}

	/**
	 * Test merge.
	 */
	@Test
	public void testMerge() {
		Division division = new Division();
		division.setName("div1");
		division.persist();
		District district = new District("testDistrict", division);
		district.persist();
		district.setName("testNewName");
		district.merge();
		Assert.assertNotNull("Updated District Data", district);
	}

	/**
	 * Test remove.
	 */
	@Test
	public void testRemove() {
		Division division = new Division();
		division.setName("div1");
		division.persist();
		District district = new District("testDistrict", division);
		district.persist();
		District.getBaseRepository().remove();
		Assert.assertNotNull("Deleted District Data", district);
	}

	/**
	 * Test find by id.
	 */
	@Test
	public void testFindById() {
		Division division = new Division();
		division.setName("div1");
		division.persist();
		District district = new District("testDistrict", division);
		district.persist();
		District district2 = District.findById(District.class, district.getId());
		Assert.assertNotNull("Getting District Data by ID ", district2);
	}

	/**
	 * Test find by name.
	 */
	@Test
	public void testFindByName() {
		Division division = new Division(); // update with unique number each time you run
		division.setName("div1");
		division.persist();
		District district = new District("uniqueDistrict8", division); 
		district.persist();
		District district2 = District.getBaseRepository().findByName(District.class, district.getName(), district.getLocale());
		Assert.assertNotNull("Getting District Data by Name ", district2);
	}

	/**
	 * Test find by field name.
	 */
	@Test
	public void testFindByFieldName() {
		Division division = new Division(); // // update with unique number each time you run
		division.setName("div4");
		division.persist();
		District district = new District("uniqueDistrict9", division); // update with unique number each time you run
		district.persist();
		District district2 = District.findByFieldName(District.class,"division.name", district.getDivision().getName(), district.getLocale());
		Assert.assertNotNull("Getting District Data by Field Division ", district2);
	}
	
	/**
	 * Test find districts by state id.
	 */
	@Test
	@Transactional
	public void testFindDistrictsByStateId() {
		State state = new State("state1");
		state.setLocale("mr_IN");
		state.persist();
		Division division = new Division("division1", state);
		division.setLocale("mr_IN");
		division.persist();
		District district = new District("district1", division);
		district.setLocale("mr_IN");
		district.persist();
		List<District> result = District.getDistrictRepository().findDistrictsByStateId(state.getId(), "name", "asc", "mr_IN");
		Assert.assertEquals(true, result.size()>0);
	}

}
