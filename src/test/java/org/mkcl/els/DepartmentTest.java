///**
// * See the file LICENSE for redistribution information.
// *
// * Copyright (c) 2012 MKCL.  All rights reserved.
// *
// * Project: e-Legislature
// * File: org.mkcl.els.DepartmentTest.java
// * Created On: Apr 20, 2012
// */
//package org.mkcl.els;
//
//import static org.junit.Assert.*;
//
//import java.util.List;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//import org.mkcl.els.domain.Department;
//import org.springframework.transaction.annotation.Transactional;
//
//// TODO: Auto-generated Javadoc
///**
// * The Class DepartmentTest.
// *
// * @author Anand
// * @since v1.0.0
// */
//public class DepartmentTest extends AbstractTest {
//
//	/**
//	 * Test persist.
//	 */
////	@Test
////	@Transactional
////	public void testPersist() {
////		Department department=new Department("testDepartment", null);
////		department.persist();
////		Department department1=new Department("newDepartment",department);
////		department1.persist();
////		Assert.assertNotNull("Saved Department data", department1);
////	}
////
////	/**
////	 * Test merge.
////	 */
////	@Test
////	@Transactional
////	public void testMerge() {
////		Department department=new Department("testDepartment", null);
////		department.persist();
////		Department department1=new Department("newDepartment",department);
////		department1.persist();
////		department1.setName("HomeDepartment");
////		department1.merge();
////		Assert.assertNotNull("Updated Department data", department1);
////	}
////
////	/**
////	 * Test remove.
////	 */
////	@Test
////	@Transactional
////	public void testRemove() {
////		Department department=new Department("testDepartment", null);
////		department.persist();
////		Department department1=new Department("newDepartment",department);
////		department1.persist();
////		department1.remove();
////		Assert.assertNotNull("Deleted Department data", department1);
////
////	}
////
////	/**
////	 * Test find by id.
////	 */
////	@Test
////	@Transactional
////	public void testFindById() {
////		Department department=new Department("testDepartment", null);
////		department.persist();
////		Department department1=new Department("newDepartment",department);
////		department1.persist();
////		Department department2=Department.findById(Department.class, department1.getId());
////		Assert.assertNotNull("Searching Department data with ID", department2);
////	}
////
////	/**
////	 * Test find by name.
////	 */
////	@Test
////	@Transactional
////	public void testFindByName() {
////		Department department=new Department("testDepartment", null);
////		department.persist();
////		Department department1=new Department("newDepartment",department);
////		department1.persist();
////		Department department2=Department.findByName(Department.class, department1.getName(), department1.getLocale());
////		Assert.assertNotNull("Searching Department data with Name", department2);
////	}
////
////	/**
////	 * Test find all.
////	 */
////	@Test
////	@Transactional
////	public void testFindAll() {
////		Department department=new Department("testDepartment", null);
////		department.persist();
////		Department department1=new Department("newDepartment",department);
////		department1.persist();
////		List<Department> departments=Department.findAll(Department.class, "name", "desc", department1.getLocale());
////		Assert.assertNotNull("Searching Department data ", departments);
////	}
//
//}
