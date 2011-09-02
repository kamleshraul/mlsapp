package org.mkcl.els;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.service.IGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class GridServiceTest extends AbstractTest{
	
	@Autowired
	IGridService gridService;
	
	@Transactional
	@Test
	public void testGetData() {
		int expectedResult = 2;
		GridData vo  = gridService.getData(1l, 2, 0, "id", "asc");
		Assert.assertEquals(expectedResult, vo.getTotal());
	}

}
