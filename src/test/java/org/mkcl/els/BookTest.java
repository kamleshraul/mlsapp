/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.BookTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Book;
import org.mkcl.els.domain.Member;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class BookTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class BookTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Member author=new Member();
		author.persist();
		List<Member> authors=Member.findAll(Member.class,"firstName", "desc",author.getLocale());
		Book book=new Book("testname", authors, "12345", "test");
		book.persist();
		Assert.assertNotNull("Saved Book Data ", book);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Member author=new Member();
		author.persist();
		List<Member> authors=Member.findAll(Member.class,"firstName", "desc",author.getLocale());
		Book book=new Book("testname", authors, "12345", "test");
		book.persist();
		book.setName("new name");
		book.merge();
		Assert.assertNotNull("Updated Book Data ", book);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Member author=new Member();
		author.persist();
		List<Member> authors=Member.findAll(Member.class,"firstName", "desc",author.getLocale());
		Book book=new Book("testname", authors, "12345", "test");
		book.persist();
		book.remove();
		Assert.assertNotNull("Deleted Book Data ", book);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Member author=new Member();
		author.persist();
		List<Member> authors=Member.findAll(Member.class,"firstName", "desc",author.getLocale());
		Book book=new Book("testname", authors, "12345", "test");
		book.persist();
		Book book1=Book.findById(Book.class, book.getId());
		Assert.assertNotNull("Finding Book Data ", book1);
		
	}

	/**
	 * Test find by name.
	 */
	@Test
	@Transactional
	public void testFindByName() {
		Member author=new Member();
		author.persist();
		List<Member> authors=Member.findAll(Member.class,"firstName", "desc",author.getLocale());
		Book book=new Book("testname", authors, "12345", "test");
		book.persist();
		Book book1=Book.findByName(Book.class, "testname", book.getLocale());
		Assert.assertNotNull("Finding Book Data ", book1);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		Member author=new Member();
		author.persist();
		List<Member> authors=Member.findAll(Member.class,"firstName", "desc",author.getLocale());
		Book book=new Book("testname", authors, "12345", "test");
		book.persist();
		List<Book> books=Book.findAll(Book.class, "name", "desc", book.getLocale());
		Assert.assertEquals(true,books.size()>0);
	}

}
