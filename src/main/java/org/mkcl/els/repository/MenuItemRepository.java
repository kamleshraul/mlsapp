/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MenuItemRepository.java
 * Created On: Jan 6, 2012
 *//*
package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.MenuItem;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

*//**
 * The Class MenuItemRepository.
 *
 * @author vishals
 * @version v1.0.0
 *//*
@Repository
public class MenuItemRepository extends BaseRepository<MenuItem, Long> {

	*//**
	 * List.
	 *
	 * @param parent the parent
	 * @return the list< menu item>
	 * @author meenalw
	 * @since v1.0.0
	 *//*
	public List<MenuItem> findMenuItemsByParent(final MenuItem parent) {
		Search search = new Search();
		if (parent == null) {
			search.addFilterNull("parent").addSort("position", false);
		} else {
			search.addFilterEqual("parent", parent).addSort("position", false);
		}
		return this.search(search);
	}


	*//**
	 * Menu item.
	 *
	 * @param textKey the text key
	 * @return the menu item
	 * @author meenalw
	 * @since v1.0.0
	 *//*
    public MenuItem findMenuItemByTextKey(final String textKey,
                                          final String locale) {
		Search search = new Search();
		search.addFilterEqual("textKey", textKey);
		search.addFilterEqual("locale", locale);
		MenuItem menuItem = this.searchUnique(search);
		return menuItem;
	}

	*//**
	 * List.
	 *
	 * @param locale the locale
	 * @return the list< menu item>
	 * @author meenalw
	 * @since v1.0.0
	 *//*
	public List<MenuItem> findAllByLocale(final String locale) {
		Search search = new Search();
		search.addFilterEqual("locale", locale);
		return this.search(search);
	}

	 *//**
     * Find all records sorted.
     *
     * @param property the property
     * @param locale the locale
     * @param desc the desc
     * @return the list
     * @author sujitas
     * @since v1.0.0
     *//*
	 public List<MenuItem> findAllSorted(final String property,
                                 final String locale,
                                 final boolean desc) {
        final Search search = new Search();
        search.addSort(property, desc);
        search.addFilterEqual("locale", locale);
        final List<MenuItem> records = this.search(search);
        return records;
    }
}
*/


package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.MenuItem;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MenuItemRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MenuItemRepository extends BaseRepository<MenuItem, Long> {

	/**
	 * Find menu items by parent.
	 *
	 * @param parent the parent
	 * @return the list
	 */
	public List<MenuItem> findMenuItemsByParent(final MenuItem parent) {
		Search search = new Search();
		if (parent == null) {
			search.addFilterNull("parent").addSort("position", false);
		} else {
			search.addFilterEqual("parent", parent).addSort("position", false);
		}
		return this.search(search);
	}
}
