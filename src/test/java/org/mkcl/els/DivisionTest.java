package org.mkcl.els;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class DivisionTest.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
public class DivisionTest extends AbstractTest {

	/**
	 * Test find divisions by state id.
	 */
	@Test
	public void testFindDivisionsByStateId() {
		State state = new State("testState");
		state.persist();
		Division division = new Division("testDivision", state);
		division.persist();
		List<Division> divisions = Division.getDivisionRepository()
				.findDivisionsByStateId(state.getId(), "name", "asc",
						state.getLocale());
		Assert.assertNotNull("Divisions by State ID is :- ", divisions);
	}

	/**
	 * Test find divisions by state name.
	 */
	@Test
	public void testFindDivisionsByStateName() {
		State state = new State("testState");
		state.persist();
		Division division = new Division("testDivision", state);
		division.persist();
		List<Division> divisions = Division.getDivisionRepository()
				.findDivisionsByStateName(state.getName(), "name", "asc");
		Assert.assertNotNull("Divisions by State ID is :- ", divisions);
	}

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		State state = new State("testState");
		state.persist();
		Division division = new Division("testDivision", state);
		division.persist();
		Assert.assertNotNull("Saved Division Data ", division);
	}

	/**
	 * Test merge.
	 */
	@Test
	public void testMerge() {
		State state = new State("testState");
		state.persist();
		Division division = new Division("testDivision", state);
		division.persist();
		division.setName("testNewName");
		division.merge();
		Assert.assertNotNull("Updated Division Data", division);
	}

	/**
	 * Test remove.
	 */
	@Test
	public void testRemove() {
		State state = new State("testState");
		state.persist();
		Division division = new Division("testDivision", state);
		division.persist();
		Division.getBaseRepository().remove();
		Assert.assertNotNull("Deleted Division Data", division);
	}

	/**
	 * Test find by id.
	 */
	@Test
	public void testFindById() {
		State state = new State("testState");
		state.persist();
		Division division = new Division("testDivision", state);
		division.persist();
		Division division2 = Division
				.findById(Division.class, division.getId());
		Assert.assertNotNull("Getting Division Data by ID ", division2);
	}

	/**
	 * Test find by name.
	 */
	@Test
	public void testFindByName() {
		State state = new State("testState");
		state.persist();
		Division division = new Division("uniqueDivision6", state); // update
																	// with
																	// unique
																	// number
																	// each time
																	// you run
		division.persist();
		Division division2 = Division.getBaseRepository().findByName(
				Division.class, division.getName(), division.getLocale());
		Assert.assertNotNull("Getting Division Data by Name ", division2);
	}

	/**
	 * Test find by field name.
	 */
	@Test
	public void testFindByFieldName() {
		State state = new State("uniqueState2"); // update with unique number
													// each time you
													// run
		state.persist();
		Division division = new Division("uniqueDivision7", state); // update
																	// with
																	// unique
																	// number
																	// each time
																	// you run
		division.persist();
		Division division2 = Division.findByFieldName(Division.class,
				"state.name", division.getState().getName(),
				division.getLocale());
		Assert.assertNotNull("Getting Division Data by Field State ", division2);
	}

}
