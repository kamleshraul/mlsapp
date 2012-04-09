/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.DepartmentTest.java
 * Created On: Apr 9, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Ministry;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class DepartmentTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class DepartmentTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Ministry ministry=new Ministry("testMinistry", "tm");
		ministry.persist();
		Department department=new Department("testDept", ministry);
		department.persist();
		Assert.assertNotNull("Saved Department Data ", department);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Ministry ministry=new Ministry("testMinistry", "tm");
		ministry.persist();
		Department department=new Department("testDept", ministry);
		department.persist();
		department.setName("newName");
		department.merge();
		Assert.assertNotNull("Updated Department Data ", department);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Ministry ministry=new Ministry("testMinistry", "tm");
		ministry.persist();
		Department department=new Department("testDept", ministry);
		department.persist();
		department.remove();
		Assert.assertNotNull("Deleted Department Data ", department);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Ministry ministry=new Ministry("testMinistry", "tm");
		ministry.persist();
		Department department=new Department("testDept", ministry);
		department.persist();
		Department dept=Department.findById(Department.class, department.getId());
		Assert.assertNotNull("searching Department Data wrt id ", dept);
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		Ministry ministry=new Ministry("testMinistry", "tm");
		ministry.persist();
		Department department=new Department("testDept", ministry);
		department.persist();
		Department dept=Department.findByName(Department.class, department.getName(), department.getLocale());
		Assert.assertNotNull("searching Department Data wrt name ", dept);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Ministry ministry=new Ministry("testMinistry", "tm");
		ministry.persist();
		Department department=new Department("testDept", ministry);
		department.persist();
		List<Department> departments=Department.findAll(Department.class, "name", "desc", department.getLocale());
		Assert.assertNotNull("searching All Department Data ", departments);
	}

}
