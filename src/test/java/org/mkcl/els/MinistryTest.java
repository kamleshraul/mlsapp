package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Ministry;
import org.springframework.transaction.annotation.Transactional;

public class MinistryTest extends AbstractTest {

	@Test
	@Transactional
	public void testPersist() {
		Ministry ministry=new Ministry("testMinistry","tm");
		ministry.persist();
	    Assert.assertNotNull("Saved Ministry Data ", ministry);
	}

	@Test
	@Transactional
	public void testMerge() {
		Ministry ministry=new Ministry("testMinistry","tm");
		ministry.persist();
		ministry.setDepartment("new Ministry");
		ministry.merge();
	    Assert.assertNotNull("Updated Ministry Data ", ministry);
	}

	@Test
	@Transactional
	public void testRemove() {
		Ministry ministry=new Ministry("testMinistry","tm");
		ministry.persist();
		ministry.remove();
	    Assert.assertNotNull("Deleted Ministry Data ", ministry);
	}

	@Test
	@Transactional
	public void testFindById() {
		Ministry ministry=new Ministry("testMinistry","tm");
		ministry.persist();
		Ministry ministry1=Ministry.findById(Ministry.class,ministry.getId());
	    Assert.assertNotNull("Finding Ministry Data  by Id", ministry1);

	}

	@Test
	@Transactional
	public void testFindByFieldName() {
		Ministry ministry=new Ministry("testMinistry","tm");
		ministry.persist();
		Ministry ministry1=Ministry.findByFieldName(Ministry.class, "department","testMinistry" , ministry.getLocale());
		Assert.assertNotNull("Finding Ministry Data  by Field Name", ministry1);
	}

	@Test
	@Transactional
	public void testFindAll() {
		Ministry ministry=new Ministry("testMinistry","tm");
		ministry.persist();
		List<Ministry> listMinistry=Ministry.findAll(Ministry.class, "department", "desc",ministry.getLocale());
		Assert.assertNotNull("Finding Ministry Data ", listMinistry);

	}

}
