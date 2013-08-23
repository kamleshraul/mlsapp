///**
// * See the file LICENSE for redistribution information.
// *
// * Copyright (c) 2012 MKCL.  All rights reserved.
// *
// * Project: e-Legislature
// * File: org.mkcl.els.CustomParameterTest.java
// * Created On: Jan 2, 2012
// */
//package org.mkcl.els;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//import org.mkcl.els.domain.CustomParameter;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * The Class CustomParameterTest.
// *
// * @author sujitas
// * @since v1.0.0
// */
//
//@Transactional
//public class CustomParameterTest extends AbstractTest {
//
//    /**
//     * Test persist.
//     *
//     * @author sujitas
//     * @since v1.0.0
//     */
//    @Test
//    public void testPersist() {
//        final CustomParameter customParameter = new CustomParameter("TEST_PARAMETER",
//                "Test value ", true, "Testing value for custom parameter");
//        customParameter.persist();
//        Assert.assertNotNull("Saved Data ", customParameter);
//    }
//
//    /**
//     * Test find by name.
//     *
//     * @author sujitas
//     * @since v1.0.0
//     */
//    @Test
//    public void testFindByName() {
//        final CustomParameter customParameterPersist = new CustomParameter(
//                "TEST_PARAMETER", "Test value", true,
//                "Testing values for custom parameter");
//        customParameterPersist.persist();
//        CustomParameter customParameter = CustomParameter
//                .findByName(CustomParameter.class,customParameterPersist.getName(),customParameterPersist.getLocale());
//        Assert.assertEquals("Test value", customParameter.getValue());
//    }
//
//    /**
//     * Test update custom parameter.
//     *
//     * @author sujitas
//     * @since v1.0.0
//     */
//    @Test
//    public void testUpdateCustomParameter() {
//        CustomParameter customParameterPersist = new CustomParameter(
//                "TEST_PARAMETER", "Test value", true,
//                "Testing values for custom parameter");
//        customParameterPersist.persist();
//        customParameterPersist.setValue("Value for test");
//        customParameterPersist.merge();
//        Assert.assertNotNull("Updated Data ", customParameterPersist);
//    }
//
//    /**
//     * Test remove field.
//     *
//     * @author sujitas
//     * @since v1.0.0
//     */
//    @Test
//    @Transactional
//    public void testRemoveCustomParameter() {
//        CustomParameter customParameter=new CustomParameter("testname", "test", false,"");
//        customParameter.persist();
//        customParameter.remove();
//        Assert.assertNotNull("Removed Custom Parameter",  customParameter);
//    }
//
//}
