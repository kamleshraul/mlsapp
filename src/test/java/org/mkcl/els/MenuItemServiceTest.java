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

import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.service.IMenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class UserTest.
 *
 * @author vishals
 * @version v1.0.0
 */
public class MenuItemServiceTest extends AbstractTest{
	
	/** The user repository. */
	@Autowired
	private IMenuItemService menuService;
	
	/**
	 * Inits the.
	 */
	@Transactional
	@Before
	public void init(){
		MenuItem parent1 = new MenuItem("mnu_parent1","Parent-1","","",0,null);
		MenuItem parent2 = new MenuItem("mnu_parent1","Parent-2","","",0,null);
		parent1=menuService.create(parent1);
		parent2=menuService.create(parent2);
		MenuItem sub1_parent1 = new MenuItem("mnu_sub1_parent1","Sub-1-Parent-1","","",0,parent1);
		MenuItem sub2_parent1 = new MenuItem("mnu_sub2_parent1","Sub-2-Parent-1","","",1,parent1);
		MenuItem sub1_parent2 = new MenuItem("mnu_sub1_parent2","Sub-1-Parent-2","","",0,parent2);
		MenuItem sub2_parent2 = new MenuItem("mnu_sub2_parent2","Sub-2-Parent-2","","",1,parent2);
		menuService.create(sub1_parent1);
		menuService.create(sub2_parent1);
		menuService.create(sub1_parent2);
		menuService.create(sub2_parent2);
		MenuItem sub1_sub1_parent1 = new MenuItem("mnu_sub1_sub1_parent1","Sub-1-Sub-1-Parent-1","","",0,sub1_parent1);
		menuService.create(sub1_sub1_parent1);
	}
	
	@Transactional
	@Test
	public void testGetMenuXML(){
		String xml = menuService.getMenuXml();
		System.out.println(xml);
	}
	
}
