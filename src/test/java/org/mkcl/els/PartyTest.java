/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.PartyTest.java
 * Created On: Dec 20, 2011
 */
package org.mkcl.els;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Party;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class PartyTest.
 *
 * @author meenalw
 * @since v1.0.0
 */
public class PartyTest extends AbstractTest {

    /**
     * Test persist.
     */
    @Test
    @Transactional
    public final void testPersist() {
        Party party = new Party("Indian National Congress", "INC", new Date());
        party.persist();
        Assert.assertNotNull("Saved state Data ", party);
    }

    /**
     * Test update party.
     */
    @Test
    @Transactional
    public final void testUpdateParty() {
        Party list = new Party("Indian National", "Ind Nat", new Date());
        list.persist();
        Party party = Party.findById(Party.class, list.getId());
        party.setName("Indian National Congress");
        party.setShortName("INC");
        party.merge();
        Assert.assertNotNull("Party data updated", party);
    }

    /**
     * Test remove party.
     */
    @Test
    @Transactional
    public final void testRemoveParty() {
        Party party = new Party("Indian National", "INC", new Date());
        party.persist();
        Party id = Party.findById(Party.class, party.getId());
        id.remove();
        Assert.assertNotNull(id);

    }

    /**
     * Test find by id.
     */
    @Test
    @Transactional
    public final void testFindById() {
    	Party party = new Party("Indian National", "INC", new Date());
        party.persist();
        final Party party1 = Party.findById(Party.class, party.getId());
        Assert.assertNotNull("Indian National Congress", party1.getName());
    }

    /**
     * Test find by name.
     */
    @Test
    @Transactional
    public final void testFindByName() {
    	Party party = new Party("Kandriye Samaj Mandal", "KSM", new Date());        
        party.persist();
        Party lst = Party.findByName(Party.class, party.getName(), null);
        Assert.assertEquals("Kandriye Samaj Mandal", lst.getName());
    }

    /**
     * Test find all.
     */
    @Test
    @Transactional
    public final void testFindAll() {
    	Party party = new Party("Kandriye Samaj Mandal", "KSM", new Date());  
    	Party party1 = new Party("Indian National", "INC", new Date());
        party.persist();
        party1.persist();
        List<Party> list = Party.findAll(Party.class, "name", "asc", null);
        Assert.assertEquals(true, list.size() > 0);
    }
    
}
