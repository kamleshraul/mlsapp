package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.HouseType;
import org.springframework.transaction.annotation.Transactional;

public class HouseTypeTest extends AbstractTest {

	@Test
	@Transactional
	public void testPersist() {
		 HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		 assemblycounciltype.persist();
       Assert.assertNotNull("Saved assemblyCouncilType Data ", assemblycounciltype);
	}

	@Test
	@Transactional
	public void testRemove() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		assemblycounciltype.persist();
		assemblycounciltype.remove();
		Assert.assertNotNull("Removed assemblyRole Data ", assemblycounciltype);
	}

	@Test
	@Transactional
	public void testFindById() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		assemblycounciltype.persist();
		assemblycounciltype = HouseType.findById(HouseType.class,assemblycounciltype.getId());
	     Assert.assertNotNull("testFindByFieldName assemblycounciltype Data ", assemblycounciltype);
	}

	@Test
	@Transactional
	public void testFindByFieldName() {
	HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
	assemblycounciltype.persist();
	assemblycounciltype = HouseType.findByFieldName(HouseType.class, "type","testAssemblyCouncilType", assemblycounciltype.getLocale());
      Assert.assertNotNull("testFindByFieldName assemblycounciltype Data ", assemblycounciltype);
	}

	
	@Test
	@Transactional
	public void testFindAll() {
		HouseType assemblycounciltype = new HouseType("testAssemblyCouncilType","testhouse");
		assemblycounciltype.persist();
       List<HouseType> listAssemblyCouncilType = HouseType.findAll(HouseType.class,"type", "desc", "en");
       Assert.assertNotNull("testFindAllSorted AssemblyRole Data ", listAssemblyCouncilType);
	}

}
