/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.TitleTest.java
 * Created On: Dec 20, 2011
 */
package org.mkcl.els;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Title;
import org.springframework.transaction.annotation.Transactional;


/**
 * The Class TitleTest.
 *
 * @author samiksham
 */
public class TitleTest extends AbstractTest {

    /**
     * Test persist.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Transactional
    @Test
    public final void testPersist() {
        Title title = new Title("Mr", "en", 0L);
        title.persist();
    }

    /**
     * Test update.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Transactional
    @Test
    public final void testUpdate() {
        Title titlePersist = new Title("Mr", "en", 0L);
        titlePersist.persist();
        Title title = Title.findById(Title.class,titlePersist.getId());
        title.setName("Miss Test");
        Title updated = (Title) title.merge();
        Assert.assertEquals("Miss Test", updated.getName());
    }

    /**
     * Test delete.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Transactional
    @Test
    public final void testDelete() {
        Title titlePersist = new Title("Mrs", "en", 0L);
        titlePersist.persist();
        Title title = Title.findById(Title.class,titlePersist.getId());
        title.remove();
        Assert.assertNull(Title.findById(Title.class,titlePersist.getId()));
    }

    /**
     * Test findby id.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    @Transactional
    public final void testFindbyId() {
        Title titlePersist = new Title("Mrs", "en", 0L);
        titlePersist.persist();
        Title title = Title.findById(Title.class,titlePersist.getId());
        Assert.assertEquals("Mrs", title.getName());
    }

    /**
     * Test findby name.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    @Transactional
    public final void testFindbyName() {
        Title titlePersist = new Title("Mrs",null, 0L);
        titlePersist.persist();
        Title title = Title.findByName(Title.class, "Mrs", null);
        Assert.assertEquals("Mrs", title.getName());
    }

     
  
    @Test
    @Transactional
    public final void testFindAll() {
    	  Title titlePersist = new Title("Mrs", null, 0L);
          titlePersist.persist();
          List<Title> titles=Title.findAll(Title.class, "name", "desc", null);
          Assert.assertEquals(true,	titles.size()>0);
    }

}
