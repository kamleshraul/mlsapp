/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.DistrictTest.java
 * Created On: Dec 20, 2011
 */
package org.mkcl.els;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.State;
import org.springframework.transaction.annotation.Transactional;


/**
 * The Class DistrictTest.
 *
 * @author nileshp
 */
public class DistrictTest extends AbstractTest {

    /**
     * Test persist.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test persist.
     */
    @Test
    @Transactional
    public final void testPersist() {
        State state = State.findById(1L);
        final District district = new District("Nasik", state);
        district.persist();
        Assert.assertNotNull("Saved District Data ", district);
    }


    /**
     * Test find by name.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test find by name.
     */
    @Test
    public final void testFindByName() {
        final District district = District.findByName("Mumbai");
        Assert.assertNotNull(district);
    }

    /**
     * Test find by id.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test find by id.
     */
    @Test
    public final void testFindById() {
        final District district = District.findById(1L);
        Assert.assertEquals("Mumbai", district.getName());
    }

    /**
     * Test update state.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test update district.
     */
    @Test
    @Transactional
    public final void testUpdateDistrict() {
        final District district = District.findById(1L);
        district.setName("Mumbaia");
        district.update();
        Assert.assertNotNull("updated District data is :-  ", district);
    }

    /**
     * Test remove state.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test remove district.
     */
    @Test
    @Transactional
    public final void testRemoveDistrict() {

        final District district = new District();
        district.setName("testDistrict");
        district.setLocale("en");
        district.setVersion(0L);
        State state = State.findById(1L);
        district.setState(state);
        district.persist();
        District district2 = District.findByName("testDistrict");
        district2.remove();
        Assert.assertNotNull("removed district Data ", district);
    }


    /**
     * Test find district by state id.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test find district by state id.
     */
    @Test
    public final void testFindDistrictByStateId() {
        final List<District> districtList = District.findDistrictsByStateId(1L);
        Assert.assertNotNull("District List is :- ", districtList);
    }


    /**
     * Test find sort districts by state id.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test find sort districts by state id.
     */
    @Test
    public final void testFindSortDistrictsByStateId() {
        final List<District> districtList = District.findDistrictsByStateId(1L, "name", false);
        Assert.assertNotNull("District List is :- ", districtList);
    }


    /**
     * Test find sort districts by state name.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test find sort districts by state name.
     */
    @Test
    public final void testFindSortDistrictsByStateName() {
        final List<District> districtList = District.findDistrictsByStateName("Maharashtra");
        Assert.assertNotNull("District List is :- ", districtList);
    }

    /**
     * Test find districts by constituency id.
     *
     * @author nileshp
     * @since v1.0.0
     * * Test find districts by constituency id.
     */
    @Test
    public final void testFindDistrictsByConstituencyId() {
        final List<District> districtList =
                District.findDistrictsByConstituencyId(5L, "name", false);
        Assert.assertNotNull("District List is :- ", districtList);
    }
}
