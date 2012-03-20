package org.mkcl.els;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.State;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class StateTest1.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
public class StateTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		State state = new State("testState");
		state.persist();
		Assert.assertNotNull("Saved State Data ", state);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		State state = new State("testState");
		state.persist();
		state.setName("new State");
		state.merge();
		Assert.assertNotNull("Updated State Data ", state);

	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		State state = new State("testState");
		state.persist();
		state.remove();
		Assert.assertNotNull("Deleted State Data ", state);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		State state = new State("testState");
		state.persist();
		State state2 = State.findById(State.class, state.getId());
		Assert.assertNotNull("Getting State Data by ID ", state2);

	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		State state = new State("testState");
		state.persist();
		State state2 = State.findByName(State.class, "testState",
				state.getLocale());
		Assert.assertNotNull("Getting State Data by Field Name ", state2);

	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		State state = new State("testState");
		state.persist();
		State state2 = State.findByFieldName(State.class, "name", "testState",
				state.getLocale());
		Assert.assertNotNull("Getting State Data by Field Name ", state2);

	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		State state = new State("testState");
		state.persist();
		List<State> listState = State
				.findAll(State.class, "name", "desc", "en");
		Assert.assertNotNull("Getting All State Data ", listState);

	}

}
