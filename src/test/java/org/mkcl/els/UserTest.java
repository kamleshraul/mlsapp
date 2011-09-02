/*
******************************************************************
File: org.mkcl.els.UserTest.java
Copyright (c) 2011, vishals, MKCL
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
******************************************************************
 */
package org.mkcl.els;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.common.exception.RecordNotFoundException;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class UserTest.
 *
 * @author vishals
 * @version v1.0.0
 */
public class UserTest extends AbstractTest{
	
	/** The user repository. */
	@Autowired
	private IUserService userService;
	
	@Transactional
	@Before
	public void init(){
		User user = new User("abc","abc",true,"fname","mname", "lname","user@test.com",new Date());
		userService.create(user);
	}
	
	/**
	 * Test find by username where user exists.
	 */
	@Transactional
	@Test
	public void testFindByUsernameWhereUserExists() {
		String expectedResult = "abc";
		User user  = userService.findByUsername("abc");
		Assert.assertEquals(expectedResult, user.getUsername());
	}
	
	
	/**
	 * Test find by username where user does not exists.
	 */
	@Transactional
	@Test(expected=RecordNotFoundException.class)
	public void testFindByUsernameWhereUserDoesNotExists() {
		userService.findByUsername("vishals1");
	}

}
