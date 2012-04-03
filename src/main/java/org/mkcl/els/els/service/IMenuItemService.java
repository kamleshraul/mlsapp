/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IMenuItemService.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service;

import org.mkcl.els.domain.MenuItem;

/**
 * The Interface IMenuItemService.
 *
 * @author vishals
 * @version v1.0.0
 */
public interface IMenuItemService extends IGenericService<MenuItem, Long> {

    /**
     * Gets the menu in XML format.
     *
     * @return the menu string in flat xml format
     */
    // public String getMenuXml();

    // public String getMenuXml(String locale);

    /**
     * Search a MenuItem instance based on it's textKey. The textKey attribute
     * of MenuItem is UNIQUE, hence the return type is a simple type.
     */
    // public MenuItem findByTextKey(String textKey);
}
