/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.MemberPositionsDetailsTest.java
 * Created On: Jan 9, 2012
 */
package org.mkcl.els;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.MemberPositionsDetails;
import org.springframework.transaction.annotation.Transactional;


/**
 * The Class MemberPositionsDetailsTest.
 *
 * @author nileshp
 */
public class MemberPositionsDetailsTest extends AbstractTest {

    /**
     * Persist.
     *
     * @author nileshp
     * @since v1.0.0
     * Persist.
     */
    @Test
    @Transactional
    public final void persist() {

        MemberDetails memberDetails = MemberDetails.findById(17L);
        MemberPositionsDetails memberPositionsDetails = new MemberPositionsDetails();
        memberPositionsDetails.setDetails("testDetails");
        memberPositionsDetails.setMember(memberDetails);
        memberPositionsDetails.persist();
        Assert.assertNotNull("Saved memberPositionsDetails Data ", memberPositionsDetails);
    }

    /**
     * Update.
     *
     * @author nileshp
     * @since v1.0.0
     * Update.
     */
    @Test
    @Transactional
    public final void update() {
        MemberDetails memberDetails = MemberDetails.findById(17L);
        MemberPositionsDetails memberPositionsDetails = new MemberPositionsDetails();
        memberPositionsDetails.setDetails("testDetails");
        memberPositionsDetails.setMember(memberDetails);
        memberPositionsDetails.persist();

        memberPositionsDetails.setDetails("testDetails1");
        MemberPositionsDetails memberPositionsDetails1 = memberPositionsDetails.update();
        Assert.assertNotNull("Update memberPositionsDetails Data ", memberPositionsDetails1);
    }

    /**
     * Removes the.
     *
     * @author nileshp
     * @since v1.0.0
     * Removes the.
     */
    @Test
    @Transactional
    public final void remove() {
        MemberDetails memberDetails = MemberDetails.findById(17L);
        MemberPositionsDetails memberPositionsDetails = new MemberPositionsDetails();
        memberPositionsDetails.setDetails("testDetails");
        memberPositionsDetails.setMember(memberDetails);
        memberPositionsDetails.persist();

        memberPositionsDetails.remove();
        Assert.assertNotNull("Remove memberPositionsDetails Data ", memberPositionsDetails);
    }
}
