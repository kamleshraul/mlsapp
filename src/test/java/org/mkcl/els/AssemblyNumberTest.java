/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.AssemblyNumberTest.java
 * Created On: Dec 28, 2011
 */
package org.mkcl.els;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.AssemblyNumber;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class AssemblyNumberTest.
 *
 * @author nileshp
 * @since v1.0.0
 */
public class AssemblyNumberTest extends AbstractTest {

    /**
     * Test persist.
     *
     * @author nileshp
     * @since v1.0.0
     * Test persist.
     */
    @Test
    @Transactional
    public final void testPersist() {
        AssemblyNumber assemblyNumber = new AssemblyNumber("10", 0L, "en");
        assemblyNumber.persist();
        Assert.assertNotNull("Saved assemblyNumber Data ", assemblyNumber);
    }

    /**
     * Test update.
     *
     * @author nileshp
     * @since v1.0.0
     * Test update.
     */
    @Test
    @Transactional
    public final void testUpdate() {
        AssemblyNumber assemblyNumber = AssemblyNumber.findByAssemblyNumber("1");
        assemblyNumber.setAssemblyNo("11");
        assemblyNumber.update();
        Assert.assertNotNull("Updated assemblyNumber Data ", assemblyNumber);
    }

    /**
     * Test remove.
     *
     * @author nileshp
     * @since v1.0.0
     * Test remove.
     */
    @Test
    @Transactional
    public final void testRemove() {
        AssemblyNumber assemblyNumber = AssemblyNumber.findByAssemblyNumber("1");
        assemblyNumber.remove();
        Assert.assertNotNull("Removed assemblyNumber Data ", assemblyNumber);
    }

    /**
     * Find by assembly number.
     *
     * @author nileshp
     * @since v1.0.0
     * Find by assembly number.
     */
    @Test
    public final void testFindByAssemblyNumber() {
        AssemblyNumber assemblyNumber = AssemblyNumber.findByAssemblyNumber("1");
        Assert.assertNotNull("assemblyNumber is :- ", assemblyNumber);
    }

    /**
     * Test find by id.
     *
     * @author nileshp
     * @since v1.0.0
     * Test find by id.
     */
    @Test
    public final void testFindById() {
        AssemblyNumber assemblyNumber = AssemblyNumber.findById(1L);
        Assert.assertNotNull("assemblyNumber is :- ", assemblyNumber);
    }
}
