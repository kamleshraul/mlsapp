/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberSearchPage.java
 * Created On: Apr 17, 2012
 */

package org.mkcl.els.common.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MemberSearchPage.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberSearchPage {

    /** The total records. */
    private int totalRecords;

    /** The page items. */
    private List<MemberInfo> pageItems = new ArrayList<MemberInfo>();

    /**
     * Gets the total records.
     *
     * @return the total records
     */
    public int getTotalRecords() {
        return totalRecords;
    }

    /**
     * Sets the total records.
     *
     * @param totalRecords the new total records
     */
    public void setTotalRecords(final int totalRecords) {
        this.totalRecords = totalRecords;
    }

    /**
     * Gets the page items.
     *
     * @return the page items
     */
    public List<MemberInfo> getPageItems() {
        return pageItems;
    }

    /**
     * Sets the page items.
     *
     * @param pageItems the new page items
     */
    public void setPageItems(final List<MemberInfo> pageItems) {
        this.pageItems = pageItems;
    }

}
