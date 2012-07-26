/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ContactTest.java
 * Created On: 25 Jul, 2012
 */
package org.mkcl.els;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.Contact;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ContactTest.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Configurable
public class ContactTest extends AbstractTest{	
	
	/** The id. */
	static Long id;	
	
	/**
	 * Sets the test data.
	 */
	@Transactional
	public void setTestData() {		
		Contact contact = new Contact();
		contact.setEmail1("abc.xyz@gmail.com");
		contact.setEmail2("pqr_tuv@yahoo.co.in");
		contact.setMobile1("9999999999");
		contact.setMobile2("8888888888");
		contact.setWebsite1("www.abc.com");		
		contact.persist();
		id = contact.getId();
	}	
	
	/**
	 * Test persist.
	 */
	@Test
	@Transactional	
	public void testPersist() {			
		Contact contact = new Contact();
		contact.setEmail1("abc.xyz@gmail.com");
		contact.setEmail2("pqr_tuv@yahoo.co.in");
		contact.setMobile1("9999999999");
		contact.setMobile2("8888888888");
		contact.setWebsite1("www.abc.com");		
		contact.persist();
		id = contact.getId();
	}
	
	/**
	 * Test remove.
	 */
	@Test
	@Transactional	
	public void testRemove() {
		setTestData();
		Assert.assertTrue(Contact.findById(Contact.class, id).remove());
	}
	
	/**
	 * Test find by id.
	 */
	@Test
	@Transactional	
	public void testFindById() {
		setTestData();		
		Assert.assertNotNull("Contact Find By ID Failed ", Contact.findById(Contact.class, id));
	}
	
	/**
	 * Test merge.
	 */
	@Test
	@Transactional	
	public void testMerge() {
		setTestData();
		Contact addrBeforeMerge = Contact.findById(Contact.class, id);
		addrBeforeMerge.setMobile1("9090909090");
		addrBeforeMerge.merge();
		Contact addrAfterMerge = Contact.findById(Contact.class, addrBeforeMerge.getId());
		Assert.assertTrue("Updation of Contact Failed ", addrAfterMerge.getMobile1().equals("9090909090"));
	}
	
	
	
	/**
	 * Test find all.
	 */
	@Test
	@Transactional	
	public void testFindAll() {
		setTestData();		
		List<Contact> contacts = Contact.findAll(Contact.class, "email1", "asc", null);		
		Assert.assertEquals(true, contacts.size()>0);		
	}	
	
	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional	
	public void testFindByFieldName() {
		setTestData();
		Contact addr = Contact.findByFieldName(Contact.class, "website1", "www.abc.com", null);
		Assert.assertEquals("www.abc.com", addr.getWebsite1());		
	}
	
	/**
	 * Test find all by field name.
	 */
	@Test
	@Transactional	
	public void testFindAllByFieldName() {
		setTestData();		
		List<Contact> contacts = Contact.findAllByFieldName(Contact.class, "website1", "www.abc.com", "email1", "asc", null);
		Assert.assertEquals(true, contacts.size()>0);		
	}	

}
