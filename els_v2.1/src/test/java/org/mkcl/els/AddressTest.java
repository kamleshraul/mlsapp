/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.AddressTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class AddressTest.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
public class AddressTest extends AbstractTest{	
	
	/** The id. */
	static Long id;
	
	/**
	 * Sets the test data.
	 */
	@Transactional
	public void setTestData() {
		State state = new State("State1");
		state.setLocale("mr_IN");
		state.persist();
		Division division = new Division("Division1", state);
		division.setLocale("mr_IN");
		division.persist();
		District district = new District("District1", division);
		district.setLocale("mr_IN");
		district.persist();
		Tehsil tehsil = new Tehsil("Tehsil1", district);
		tehsil.setLocale("mr_IN");
		tehsil.persist();
		Address address = new Address();
		address.setLocale("mr_IN");
		address.setDetails("Details1");
		address.setCity("City1");
		address.setTehsil(tehsil);
		address.setDistrict(district);
		address.setState(state);
		address.setPincode("Pincode1");
		address.persist();
		id = address.getId();
	}	
	
	/**
	 * Test persist.
	 */
	@Test
	@Transactional	
	public void testPersist() {			
		State state = new State("State2");
		state.setLocale("mr_IN");
		state.persist();
		Division division = new Division("Division2", state);
		division.setLocale("mr_IN");
		division.persist();
		District district = new District("District2", division);
		district.setLocale("mr_IN");
		district.persist();
		Tehsil tehsil = new Tehsil("Tehsil2", district);
		tehsil.setLocale("mr_IN");
		tehsil.persist();
		Address address = new Address();
		address.setLocale("mr_IN");
		address.setDetails("Details2");
		address.setCity("City2");
		address.setTehsil(tehsil);
		address.setDistrict(district);
		address.setState(state);
		address.setPincode("Pincode2");			
		Assert.assertNotNull("Persistence of Address Failed ", address.persist());
		id = address.getId();		
	}
	
	/**
	 * Test remove.
	 */
	@Test
	@Transactional	
	public void testRemove() {
		setTestData();
		Assert.assertTrue(Address.findById(Address.class, id).remove());
	}
	
	/**
	 * Test find by id.
	 */
	@Test
	@Transactional	
	public void testFindById() {
		setTestData();		
		Assert.assertNotNull("Address Find By ID Failed ", Address.findById(Address.class, id));
	}
	
	/**
	 * Test merge.
	 */
	@Test
	@Transactional	
	public void testMerge() {
		setTestData();
		Address addrBeforeMerge = Address.findById(Address.class, id);
		addrBeforeMerge.setPincode("Pincode3");
		addrBeforeMerge.merge();
		Address addrAfterMerge = Address.findById(Address.class, addrBeforeMerge.getId());
		Assert.assertTrue("Updation of Address Failed ", addrAfterMerge.getPincode().equals("Pincode3"));
	}
	
	
	
	/**
	 * Test find all.
	 */
	@Test
	@Transactional	
	public void testFindAll() {
		setTestData();		
		List<Address> addresses = Address.findAll(Address.class, "city", "asc", "mr_IN");		
		Assert.assertEquals(true, addresses.size()>0);		
	}	
	
	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional	
	public void testFindByFieldName() {
		setTestData();
		Address addr = Address.findByFieldName(Address.class, "details", "Details1", "mr_IN");
		Assert.assertEquals("Details1", addr.getDetails());		
	}
	
	/**
	 * Test find all by field name.
	 */
	@Test
	@Transactional	
	public void testFindAllByFieldName() {
		setTestData();		
		List<Address> addresses = Address.findAllByFieldName(Address.class, "city", "City1", "city", "asc", "mr_IN");
		Assert.assertEquals(true, addresses.size()>0);		
	}
	
	/**
	 * Test find all by object field name.
	 */
	@Test
	@Transactional
	public void testFindAllByObjectFieldName() {
		setTestData();	
		State state = State.findByName(State.class, "State1", "mr_IN");
		List<Address> addresses = Address.findAllByFieldName(Address.class, "state", state, "city", "asc", "mr_IN");
		Assert.assertEquals(true, addresses.size()>0);		
	}

}
