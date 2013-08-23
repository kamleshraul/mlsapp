/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.DepartmentTest.java
 * Created On: Apr 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Department;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class DepartmentTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class DepartmentTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Department department=new Department("testDepartment", false, null);
		department.persist();
		Assert.assertNotNull("Saved Department data", department);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Department department=new Department("testDepartment", false, null);
		department.persist();
		department.setName("HomeDepartment");
		department.merge();
		Assert.assertNotNull("Updated Department data", department);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Department department=new Department("testDepartment", false, null);
		department.persist();
		department.remove();
		Assert.assertNotNull("Deleted Department data", department);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Department department=new Department("testDepartment", false, null);
		department.persist();
		Department department2=Department.findById(Department.class, department.getId());
		Assert.assertNotNull("Searching Department data with ID", department2);
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		Department department=new Department("testDepartment", false, null);
		department.persist();
		Department department2=Department.findByName(Department.class, department.getName(), department.getLocale());
		Assert.assertNotNull("Searching Department data with Name", department2);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Department department=new Department("testDepartment", false, null);
		department.persist();
		List<Department> departments=Department.findAll(Department.class, "name", "desc", department.getLocale());
		Assert.assertNotNull("Searching Department data ", departments);
	}

}
