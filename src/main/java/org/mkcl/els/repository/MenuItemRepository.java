/*
******************************************************************
File: org.mkcl.els.repository.MenuItemRepository.java
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
package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.MenuItem;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MenuItemRepository.
 *
 * @author vishals
 * @version v1.0.0
 */
@Repository
public class MenuItemRepository extends BaseRepository<MenuItem, Long> {
	
	
	/**
	 * Finds the menu items by parent.
	 *
	 * @param parent the parent
	 * @return the menu items by parent
	 */
	public List<MenuItem> findMenuItemsByParent(MenuItem parent){
		Search search = new Search();
		if(parent==null){
			search.addFilterNull("parent").addSort("position",false);
		}
		else{
			search.addFilterEqual("parent", parent).addSort("position",false);
		}
		return this.search(search);
	}

	/**
	 * Search a MenuItem instance based on it's textKey.
	 * The textKey attribute of MenuItem is UNIQUE, hence 
	 * the return type is a simple type.
	 */
	public MenuItem findMenuItemByTextKey(String textKey)
	{
		Search search = new Search();
		search.addFilterEqual("textKey", textKey);
		MenuItem menuItem = this.searchUnique(search);
		System.out.println(">>>>>>>Repository: " + menuItem);
		return menuItem;
	}
}
