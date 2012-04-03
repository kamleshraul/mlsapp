/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.GridServiceImpl.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service.impl;

import java.util.Locale;
import java.util.Map;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.repository.GridRepository;
import org.mkcl.els.service.IGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class GridServiceImpl.
 * 
 * @author vishals
 * @since v1.0.0
 */
@Service
public class GridServiceImpl implements IGridService {

    /** The grid repository. */
    @Autowired
    private GridRepository gridRepository;

    @Override
    public GridData getData(Long gridId, Integer rows, Integer page,
            String sidx, String order, String filterSql, Locale locale,
            Map<String, String[]> requestMap) {
        return gridRepository.getData(gridId, rows, page, sidx, order,
                filterSql, locale, requestMap);
    }

    @Override
    public GridData getData(Long gridId, Integer rows, Integer page,
            String sidx, String order, Locale locale,
            Map<String, String[]> requestMap) {
        return gridRepository.getData(gridId, rows, page, sidx, order, locale,
                requestMap);
    }
}
