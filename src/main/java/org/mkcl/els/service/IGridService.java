/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IGridService.java
 * Created On: Mar 26, 2012
 */
package org.mkcl.els.service;

import java.util.Locale;
import java.util.Map;

import org.mkcl.els.common.vo.GridData;

/**
 * The Interface IGridService.
 * 
 * @author amitd
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
     * @param sQl the s ql
     * @param locale the locale
     * @param requestMap the request map
     * @return the data
     */
    public GridData getData(Long gridId, Integer rows, Integer page,
            String sidx, String order, String sQl, Locale locale,
            Map<String, String[]> requestMap);

    /**
     * Gets the data.
     * 
     * @param gridId the grid id
     * @param rows the rows
     * @param page the page
     * @param sidx the sidx
     * @param order the order
     * @param locale the locale
     * @param requestMap the request map
     * @return the data
     */
    public GridData getData(Long gridId, Integer rows, Integer page,
            String sidx, String order, Locale locale,
            Map<String, String[]> requestMap);

}