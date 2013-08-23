package org.mkcl.els;

import org.junit.Test;
import java.util.List;
import org.junit.Assert;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.RailwayStation;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class RailwayStationTest.
 */
public class RailwayStationTest extends AbstractTest {

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
		RailwayStation railwayStation = new RailwayStation("testRailwayStation1", district);
		railwayStation.persist();
		Assert.assertNotNull("Saved RailwayStation Data ", railwayStation);
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
		RailwayStation railwayStation = new RailwayStation("testRailwayStation2", district);
		railwayStation.persist();
		railwayStation.setName("testNewName");
		railwayStation.merge();
		Assert.assertNotNull("Updated RailwayStation Data", railwayStation);
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
		RailwayStation railwayStation = new RailwayStation("testRailwayStation3", district);
		railwayStation.persist();
		railwayStation.remove();
		Assert.assertNotNull("Deleted RailwayStation Data", railwayStation);
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
		RailwayStation railwayStation = new RailwayStation("testRailwayStation4", district);
		railwayStation.persist();
		RailwayStation railwayStation2 = RailwayStation
				.findById(RailwayStation.class, railwayStation.getId());
		Assert.assertNotNull("Getting RailwayStation Data by ID ", railwayStation2);
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
		RailwayStation railwayStation = new RailwayStation("testRailwayStation5", district);
		railwayStation.persist();
		RailwayStation railwayStation2 = RailwayStation.findByName(
				RailwayStation.class, railwayStation.getName(), railwayStation.getLocale());
		Assert.assertNotNull("Getting RailwayStation Data by Name ", railwayStation2);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {		
		List<RailwayStation> railwayStations = RailwayStation.findAll(District.class, "name", "asc", "en_US");
		Assert.assertNotNull("Getting All RailwayStations Data", railwayStations);
	}

}
