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

    public GridData getData(Long gridId, Integer rows, Integer page,
            String sidx, String order, String sQl, Locale locale,
            Map<String, String[]> requestMap);

    public GridData getData(Long gridId, Integer rows, Integer page,
            String sidx, String order, Locale locale,
            Map<String, String[]> requestMap);

    /**
     * Returns GridData for Process Deployments.
     */
    public GridData getDeploymentsData(Long gridId, Integer rows, Integer page,
            String sidx, String order, Locale locale);
    
    /**
     * Returns GridData for My Tasks.
     */
    public GridData getMyTasksData(Long gridId, String username, Integer rows, Integer page,
            String sidx, String order, Locale locale);
    
    /**
     * Returns GridData for Group Tasks.
     */
    public GridData getGroupTasksData(Long gridId, String username, Integer rows, Integer page,
            String sidx, String order, Locale locale);
    
//    public GridData getDeployments(Long gridId, Integer rows, Integer page,
//            String sidx, String order, String searchField, String searchValue, Locale locale,
//            Map<String, String[]> requestMap);
//
//    public GridData getDeployments(Long gridId, Integer rows, Integer page,
//            String sidx, String order, Locale locale,
//            Map<String, String[]> requestMap);
//
//    public GridData getMembers(Long gridId, Integer rows, Integer page,
//            String sidx, String order, String sQl, Locale locale,
//            Map<String, String[]> requestMap);
//
//    public GridData getMembers(Long gridId, Integer rows, Integer page,
//            String sidx, String order, Locale locale,
//            Map<String, String[]> requestMap);

}