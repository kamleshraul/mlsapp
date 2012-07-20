package org.mkcl.els;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class TehsilTest.
 */
public class TehsilTest extends AbstractTest {

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
		Tehsil tehsil = new Tehsil("testTehsil1", district);
		tehsil.persist();
		Assert.assertNotNull("Saved Tehsil Data ", tehsil);
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
		Tehsil tehsil = new Tehsil("testTehsil2", district);
		tehsil.persist();
		tehsil.setName("testNewName");
		tehsil.merge();
		Assert.assertNotNull("Updated Tehsil Data", tehsil);
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
		Tehsil tehsil = new Tehsil("testTehsil3", district);
		tehsil.persist();
		tehsil.remove();
		Assert.assertNotNull("Deleted Tehsil Data", tehsil);
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
		Tehsil tehsil = new Tehsil("testTehsil4", district);
		tehsil.persist();
		Tehsil tehsil2 = Tehsil
				.findById(Tehsil.class, tehsil.getId());
		Assert.assertNotNull("Getting Tehsil Data by ID ", tehsil2);
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
		Tehsil tehsil = new Tehsil("testTehsil5", district);
		tehsil.persist();
		Tehsil tehsil2 = Tehsil.findByName(
				Tehsil.class, tehsil.getName(), tehsil.getLocale());
		Assert.assertNotNull("Getting Tehsil Data by Name ", tehsil2);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {		
		List<Tehsil> tehsils = Tehsil.findAll(District.class, "name", "asc", "en_US");
		Assert.assertNotNull("Getting All Tehsils Data", tehsils);
	}

}
