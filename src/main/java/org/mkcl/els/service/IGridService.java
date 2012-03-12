/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IGridService.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service;

import java.util.Locale;

import org.mkcl.els.common.vo.GridData;

/**
 * The Interface IGridService.
 *
 * @author sandeeps
 * @since v1.0.0
 */
public interface IGridService {

    /**
     * Gets the data.
     *
     * @param gridId the grid id
     * @param rows the rows
     * @param page the page
     * @param sidx the sidx
     * @param order the order
     * @return the data
     */
    public GridData getData(final Long gridId,
                            final Integer rows,
                            final Integer page,
                            final String sidx,
                            final String order);

    /**
     * Gets the data.
     *
     * @param gridId the grid id
     * @param limit the limit
     * @param page the page
     * @param sidx the sidx
     * @param order the order
     * @param locale the locale
     * @return the data
     */
    public GridData getData(final Long gridId,
                            final Integer limit,
                            final Integer page,
                            final String sidx,
                            final String order,
                            final Locale locale);

    /**
     * Gets the data.
     *
     * @param gridId the grid id
     * @param limit the limit
     * @param page the page
     * @param sidx the sidx
     * @param order the order
     * @param filterSql the filter sql
     * @param locale the locale
     * @return the data
     */
    public GridData getData(final Long gridId,
                            final Integer limit,
                            final Integer page,
                            final String sidx,
                            final String order,
                            final String filterSql,
                            final Locale locale);

}