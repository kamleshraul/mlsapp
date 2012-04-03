package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberRole;
import org.springframework.transaction.annotation.Transactional;

public class MemberRoleTest extends AbstractTest{

	@Test
	@Transactional
	public void testPersist() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		Assert.assertNotNull("Saving memberrole data ", mr);
	}

	@Test
	@Transactional
	public void testMerge() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		mr.setName("newRole");
		mr.merge();
		Assert.assertNotNull("Updating memberRole data",mr);
	}

	@Test
	@Transactional
	public void testRemove() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		mr.remove();
		Assert.assertNotNull("Removing role data",mr);
	}

	@Test
	@Transactional
	public void testFindById() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		MemberRole mr1=MemberRole.findById(MemberRole.class, mr.getId());
		Assert.assertNotNull("Finding the Role from Id", mr1);
			
	}

	@Test
	@Transactional
	public void testFindByName() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		MemberRole mr1=MemberRole.findByName(MemberRole.class, mr.getName(), mr.getLocale());
		Assert.assertNotNull("Finding Role by Name", mr1);
	}

	@Test
	@Transactional
	public void testFindAll() {
		HouseType ht=new HouseType("testHouseType","test");
		ht.persist();
		MemberRole mr=new MemberRole("testRole",1,ht);
		mr.persist();
		List<MemberRole> memberoles=MemberRole.findAll(MemberRole.class, "name", "desc", mr.getLocale());
		Assert.assertNotNull("Finding All member Roles",memberoles);
	}

}
