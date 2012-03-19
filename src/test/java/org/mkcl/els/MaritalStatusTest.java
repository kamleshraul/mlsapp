package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.MaritalStatus;
import org.springframework.transaction.annotation.Transactional;

public class MaritalStatusTest extends AbstractTest{

	@Test
	@Transactional
	public void testPersist() {
		MaritalStatus maritalstatus=new MaritalStatus("TestStatus");
		maritalstatus.persist();
	    Assert.assertNotNull("Saved Marital status Data ", maritalstatus);

	}

	@Test
	@Transactional
	public void testMerge() {
		MaritalStatus maritalstatus=new MaritalStatus("TestStatus");
		maritalstatus.persist();
		maritalstatus.setMarital_status("new Status");
		maritalstatus.merge();
	    Assert.assertNotNull("Updated Marital status Data ", maritalstatus);
	}

	@Test
	@Transactional
	public void testRemove() {
		MaritalStatus maritalstatus=new MaritalStatus("TestStatus");
		maritalstatus.persist();
		maritalstatus.remove();
	    Assert.assertNotNull("Deleted Marital status Data ", maritalstatus);
	}

	@Test
	@Transactional
	public void testFindById() {
		MaritalStatus maritalstatus=new MaritalStatus("TestStatus");
		maritalstatus.persist();
		MaritalStatus maritalstatus1=MaritalStatus.findById(MaritalStatus.class,maritalstatus.getId());
	    Assert.assertNotNull("Finding Marital status Data by Id ", maritalstatus1);

	}

	@Test
	@Transactional
	public void testFindByFieldName() {
		MaritalStatus maritalstatus=new MaritalStatus("TestStatus");
		maritalstatus.persist();
		MaritalStatus maritalstatus1=MaritalStatus.findByFieldName(MaritalStatus.class,"marital_status","TestStatus",maritalstatus.getLocale());
	    Assert.assertNotNull("Finding Marital status Data by Name ", maritalstatus1);

	}

	@Test
	@Transactional
	public void testFindAll() {
		MaritalStatus maritalstatus=new MaritalStatus("TestStatus");
		maritalstatus.persist();
		List<MaritalStatus> listStatus=MaritalStatus.findAll(MaritalStatus.class, "marital_status", "desc",maritalstatus.getLocale());
	    Assert.assertNotNull("Finding Marital status Data by Name ", listStatus);

	}

}
