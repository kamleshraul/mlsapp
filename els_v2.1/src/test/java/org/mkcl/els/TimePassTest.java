//package org.mkcl.els;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//import org.mkcl.els.domain.ApplicationLocale;
//import org.mkcl.els.domain.TimePass;
//import org.springframework.transaction.annotation.Transactional;
//
//public class TimePassTest extends AbstractTest {
//
//	@Test
//    @Transactional
//    public final void testPersist() {
//        TimePass tp1 = new TimePass("en", "amit");
//        Assert.assertNull(tp1.getId());
//        tp1.persist();
//        Assert.assertNotNull(tp1.getId());
//        System.out.println(tp1.getId());
//        
//        ApplicationLocale appLocale1 = new ApplicationLocale("mr", "PK","");
//        Assert.assertNull(appLocale1.getId());
//        appLocale1.persist();
//        Assert.assertNotNull(appLocale1.getId());
//        System.out.println(appLocale1.getId());
//
//        TimePass tp2 = new TimePass("en", "sandeep");
//        Assert.assertNull(tp2.getId());
//        tp2.persist();
//        Assert.assertNotNull(tp2.getId());
//        System.out.println(tp2.getId());
//
//        ApplicationLocale appLocale2 = new ApplicationLocale("mr", "IN","");
//        Assert.assertNull(appLocale2.getId());
//        appLocale2.persist();
//        Assert.assertNotNull(appLocale2.getId());
//        System.out.println(appLocale2.getId());
//    }
//	
//}
