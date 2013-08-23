/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ApplicationLocaleTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.BaseDomain;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationLocaleTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class ApplicationLocaleTest extends AbstractTest {

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		ApplicationLocale applicationLocale=new ApplicationLocale("mr","IN","mr_In");
		applicationLocale.persist();
		Assert.assertNotNull("Saved Locale Data ", applicationLocale);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		ApplicationLocale applicationLocale=new ApplicationLocale("mr","IN","mr_In");
		applicationLocale.persist();
		applicationLocale.setCountry("US");
		applicationLocale.merge();
		Assert.assertNotNull("Updated Locale Data ", applicationLocale);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		ApplicationLocale applicationLocale=new ApplicationLocale("mr","IN","mr_In");
		applicationLocale.persist();
		applicationLocale.remove();
		Assert.assertNotNull("Removed Locale Data ", applicationLocale);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		ApplicationLocale applicationLocale=new ApplicationLocale("mr","IN","mr_In");
		applicationLocale.persist();
		ApplicationLocale applicationLocale1=ApplicationLocale.findById(ApplicationLocale.class, applicationLocale.getId());
		Assert.assertNotNull("finding Locale Data ", applicationLocale1);
	}

	/**
	 * Test find by field name class string string string.
	 */
	@Test
	@Transactional
	public void testFindByFieldNameClassStringStringString() {
		ApplicationLocale applicationLocale=new ApplicationLocale("mr","IN","mr_In");
		applicationLocale.persist();
		ApplicationLocale applicationLocale1=ApplicationLocale.findByFieldName(ApplicationLocale.class, "language", applicationLocale.getLanguage(),applicationLocale.getLocale());
		Assert.assertNotNull("finding Locale Data ", applicationLocale1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		ApplicationLocale applicationLocale=new ApplicationLocale("mr","IN","mr_In");
		applicationLocale.persist();
		List<ApplicationLocale> applicationLocales=ApplicationLocale.findAll(ApplicationLocale.class, "language", "desc", applicationLocale.getLocale());
		Assert.assertEquals(true,applicationLocales.size()>0);
	}

}
