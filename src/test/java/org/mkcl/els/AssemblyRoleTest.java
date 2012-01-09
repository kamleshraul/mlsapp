/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.AssemblyRoleTest.java
 * Created On: Jan 9, 2012
 */
package org.mkcl.els;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.AssemblyRole;
import org.springframework.transaction.annotation.Transactional;


/**
 * The Class AssemblyRoleTest.
 *
 * @author nileshp
 */
public class AssemblyRoleTest extends AbstractTest {

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
        AssemblyRole assemblyRole = new AssemblyRole("testAssemblyRole", "en");
        assemblyRole.persist();
        Assert.assertNotNull("Saved assemblyRole Data ", assemblyRole);
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
        AssemblyRole assemblyRole = new AssemblyRole("testAssemblyRole", "en");
        assemblyRole.persist();
        assemblyRole.setName("testAssemblyRole1");
        assemblyRole.update();
        Assert.assertNotNull("Saved assemblyRole Data ", assemblyRole);
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
        AssemblyRole assemblyRole = new AssemblyRole("testAssemblyRole", "en");
        assemblyRole.persist();
        assemblyRole.remove();
        Assert.assertNotNull("Removed assemblyRole Data ", assemblyRole);
    }

    /**
     * Test find by name.
     *
     * @author nileshp
     * @since v1.0.0
     * Test find by name.
     */
    @Test
    @Transactional
    public final void testFindByName() {
        AssemblyRole assemblyRole = new AssemblyRole("testAssemblyRole", "en");
        assemblyRole.persist();
        assemblyRole = AssemblyRole.findByName("testAssemblyRole");
        Assert.assertNotNull("testFindByName assemblyRole Data ", assemblyRole);
    }

    /**
     * Test find all sorted.
     *
     * @author nileshp
     * @since v1.0.0
     * Test find all sorted.
     */
    @Test
    @Transactional
    public final void testFindAllSorted() {
        AssemblyRole assemblyRole = new AssemblyRole("testAssemblyRole", "en");
        assemblyRole.persist();
        List<AssemblyRole> listAssemblyRole = AssemblyRole.findAllSorted("name", "en", false);
        Assert.assertNotNull("testFindAllSorted AssemblyRole Data ", listAssemblyRole);
    }

    /**
     * Find unassigned roles.
     *
     * @author nileshp
     * @since v1.0.0
     * Find unassigned roles.
     */
    @Test
    @Transactional
    public final void findUnassignedRoles() {
        List<AssemblyRole> listAssemblyRole = AssemblyRole.findUnassignedRoles("en", 1L);
        Assert.assertNotNull("findUnassignedRoles AssemblyRole Data ", listAssemblyRole);
    }

    /**
     * Find by name and locale.
     *
     * @author nileshp
     * @since v1.0.0
     * Find by name and locale.
     */
    @Test
    @Transactional
    public final void findByNameAndLocale() {
        AssemblyRole assemblyRole = new AssemblyRole("testAssemblyRole", "en");
        assemblyRole.persist();
        AssemblyRole assemblyRole1 = AssemblyRole.findByNameAndLocale(assemblyRole.getName(), "en");
        Assert.assertNotNull("findByNameAndLocale AssemblyRole Data ", assemblyRole1);
    }

    /**
     * Find by id.
     *
     * @author nileshp
     * @since v1.0.0
     * Find by id.
     */
    @Test
    @Transactional
    public final void findById() {
        AssemblyRole assemblyRole = new AssemblyRole("testAssemblyRole", "en");
        assemblyRole.persist();
        AssemblyRole assemblyRole1 = AssemblyRole.findById(assemblyRole.getId());
        Assert.assertNotNull("findById AssemblyRole Data ", assemblyRole1);
    }
}
