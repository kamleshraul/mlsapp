/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MenuItemRepository.java
 * Created On: Jan 6, 2012
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
 * @version 1.0.0
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
