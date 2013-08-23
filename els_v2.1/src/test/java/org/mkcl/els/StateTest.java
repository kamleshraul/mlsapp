/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.StateTest.java
 * Created On: Dec 19, 2011
 */
package org.mkcl.els;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.State;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class StateTest.
 *
 * @author sujitas
 * @since v1.0.0
 */
public class StateTest extends AbstractTest {

    /**
     * Test persist.
     */
    @Test
    @Transactional
    public final void testPersist() {
        State state = new State("Karnataka");
        state.persist();
        Assert.assertNotNull("Saved state Data ", state);
    }

    /**
     * Test find all.
     */
    @Test
    @Transactional
    public final void testFindAll() {
    	 State state = new State("Karnataka");
         state.persist();
         List<State> lstState = State.findAll(State.class,"name","desc","mr_IN");
        Assert.assertNotNull(lstState);
    }

    /**
     * Test find by id.
     */
    @Test
    @Transactional
    public final void testFindById() {
         State statePersist = new State("Karnataka");
        statePersist.persist();
         State state = State.findById(State.class,statePersist.getId());
        Assert.assertEquals("Karnataka", state.getName());
    }

    /**
     * Test find by name.
     */
    @Test
    @Transactional
    public final void testFindByName() {
        State statePersist = new State("Karnataka");
        statePersist.persist();
        State state = State.findByName(State.class,statePersist.getName(),statePersist.getLocale());
        Assert.assertNotNull("Finding state Data ",state);
    }

    /**
     * Test update state.
     */
    @Test
    @Transactional
    public final void testUpdateState() {
        State statePersist = new State("Karnataka");
        statePersist.persist();
        State state = State.findById(State.class,statePersist.getId());
        state.setName("KarnatakaState");
        state.merge();
        Assert.assertNotNull("updated state Data ", state);
    }

    /**
     * Test remove state.
     */
    @Test
    @Transactional
    public final void testRemoveState() {
        State statePresist = new State("Karnataka");
        statePresist.persist();
        statePresist.remove();
        Assert.assertNotNull("removed state Data ",statePresist);
    }

}
