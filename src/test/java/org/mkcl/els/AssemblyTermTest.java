/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.AssemblyTermTest.java
 * Created On: Dec 27, 2011
 */
package org.mkcl.els;


import org.junit.Assert;
import org.junit.Test;
import org.mkcl.els.domain.AssemblyTerm;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class AssemblyTermTest.
 *
 * @author samiksham
 */
public class AssemblyTermTest extends AbstractTest {

    /**
     * Testpersist.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Transactional
    @Test
    public void testpersist() {
        AssemblyTerm assemblyTerm = new AssemblyTerm(3, 0L, "en");
        assemblyTerm.persist();
        Assert.assertNotNull("AssemblyTerm should not null", assemblyTerm);
    }

    /**
     * Testupdate.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Transactional
    @Test
    public void testupdate() {
        AssemblyTerm assemblyTerm = AssemblyTerm.findById(1L);
        assemblyTerm.setTerm(23);
        assemblyTerm.update();
        Assert.assertNotNull("AssemblyTerm should not null", assemblyTerm);

    }

    /**
     * Testremove.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Transactional
    @Test
    public void testremove() {
        AssemblyTerm assemblyTerm = AssemblyTerm.findById(1L);
        assemblyTerm.remove();
    }

    /**
     * Testfind by id.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    public void testfindById() {
        AssemblyTerm.findById(2L);

    }

    /**
     * Testfind by assembly term.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    public void testfindByAssemblyTerm() {
        AssemblyTerm.findByAssemblyTerm(2);
    }

    /**
     * Testfind all.
     *
     * @author samiksham
     * @since v1.0.0
     */
    @Test
    public void testfindAll() {
        AssemblyTerm.findAll();
    }
}
