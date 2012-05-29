package org.mkcl.els;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.Constituency;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstituencyTest2.
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
		Constituency constituency2 = Constituency.findByName(Constituency.class, "testConstituency5",
				constituency.getLocale());
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
		List<Constituency> listConstituency = Constituency
				.findAll(Constituency.class, "name", "desc", "en");
		Assert.assertNotNull("Getting All Constituency Data ", listConstituency);
	}

}
