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

import org.junit.Assert;
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
        final Title title = new Title("Mrs", "en", 0L);
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
        final Title title = Title.findById(2L);
        title.setName("Miss test");
        title.update();
        Assert.assertNotNull("updated Title Data ", title);
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
        final Title title = Title.findById(2L);
        title.remove();
    }


    /**
     * Test findby id.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    public final void testFindbyId() {
        Title.findById(2L);
    }

    /**
     * Test findby name.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    public final void testFindbyName() {
        Title.findByName("Miss");
    }

    /**
     * Test find all sorted.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    public final void testFindAllSorted() {
        Title.findAllSorted("name", "en", false);
    }

    /**
     * Test find all.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    public final void testFindAll() {
        Title.findAll();
    }

}
