package org.mkcl.els;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mkcl.els.domain.Role;
import org.springframework.transaction.annotation.Transactional;

public class RoleTester extends AbstractTest{

		
	@Transactional
	@Test
	public void testFindRolesByRoleType() {
		try{
			assertNotNull(Role.findRolesByRoleType(Role.class, "type", "QIS_ASSISTANT", "name", "asc"));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	@Test
	public void testFindDelimitedByQIS() {
		try{
			assertNotNull(Role.findDelimitedQISRoles("mr_IN"));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
