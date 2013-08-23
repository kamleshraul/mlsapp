package org.mkcl.els;

import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.test.IndianCaledarDemo;

public class IndianCalendarDemoTest extends AbstractTest{

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDemonstrate() {
		IndianCaledarDemo icDemo = new IndianCaledarDemo();
		icDemo.demonstrate();
	}

}
