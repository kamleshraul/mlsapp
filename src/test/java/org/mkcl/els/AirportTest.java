/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.AirportTest.java
 * Created On: 26 Jul, 2012
 */
package org.mkcl.els;

import org.junit.Test;
import java.util.List;
import org.junit.Assert;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Airport;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class AirportTest.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
public class AirportTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		State state = new State("testState1");
		state.persist();
		Division division = new Division("testDivision1",state);
		division.persist();
		District district = new District("testDistrict1", division);
		district.persist();
		Airport airport = new Airport("testAirport1", district);
		airport.persist();
		Assert.assertNotNull("Saved Airport Data ", airport);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		State state = new State("testState2");
		state.persist();
		Division division = new Division("testDivision2", state);
		division.persist();
		District district = new District("testDistrict2", division);
		district.persist();
		Airport airport = new Airport("testAirport2", district);
		airport.persist();
		airport.setName("testNewName");
		airport.merge();
		Assert.assertNotNull("Updated Airport Data", airport);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		State state = new State("testState3");
		state.persist();
		Division division = new Division("testDivision3", state);
		division.persist();
		District district = new District("testDistrict3", division);
		district.persist();
		Airport airport = new Airport("testAirport3", district);
		airport.persist();
		airport.remove();
		Assert.assertNotNull("Deleted Airport Data", airport);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		State state = new State("testState4");
		state.persist();
		Division division = new Division("testDivision4", state);
		division.persist();
		District district = new District("testDistrict4", division);
		district.persist();
		Airport airport = new Airport("testAirport4", district);
		airport.persist();
		Airport airport2 = Airport
				.findById(Airport.class, airport.getId());
		Assert.assertNotNull("Getting Airport Data by ID ", airport2);
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		State state = new State("testState5");
		state.persist();
		Division division = new Division("uniqueDivision1", state); 
		division.persist();
		District district = new District("uniqueDistrict1", division); // update
																	// with
																	// unique
																	// number
																	// each time
																	// you run
		district.persist();
		Airport airport = new Airport("testAirport5", district);
		airport.persist();
		Airport airport2 = Airport.findByName(
				Airport.class, airport.getName(), airport.getLocale());
		Assert.assertNotNull("Getting Airport Data by Name ", airport2);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {		
		List<Airport> airports = Airport.findAll(District.class, "name", "asc", "en_US");
		Assert.assertNotNull("Getting All Airports Data", airports);
	}

}
