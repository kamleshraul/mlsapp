/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.RelationTest.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Relation;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class RelationTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class RelationTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Relation relation=new Relation("testRelation");
		relation.persist();
	    Assert.assertNotNull("Saved Relation Data ", relation);

	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Relation relation=new Relation("testRelation");
		relation.persist();
		relation.setName("new Relation");
		relation.merge();
	    Assert.assertNotNull("Updated Relation Data ", relation);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Relation relation=new Relation("testRelation");
		relation.persist();
		relation.remove();
	    Assert.assertNotNull("Deleted Relation Data ", relation);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Relation relation=new Relation("testRelation");
		relation.persist();
		Relation relation1=Relation.findById(Relation.class, relation.getId());
	    Assert.assertNotNull("Finding Relation Data by Id ", relation1);

	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		Relation relation=new Relation("testRelation");
		relation.persist();
		Relation relation1=Relation.findByName(Relation.class, relation.getName(),relation.getLocale());
	    Assert.assertNotNull("Finding Relation Data by Name ", relation1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Relation relation=new Relation("testRelation");
		relation.persist();
		List<Relation> relation1=Relation.findAll(Relation.class, "name","desc",relation.getLocale());
	    Assert.assertNotNull("Finding Relation Data  ", relation1);
	}

}
