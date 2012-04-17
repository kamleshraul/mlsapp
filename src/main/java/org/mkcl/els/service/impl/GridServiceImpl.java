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
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Service
public class GridServiceImpl implements IGridService {

    /** The grid repository. */
    @Autowired
    private GridRepository gridRepository;

    /* (non-Javadoc)
     * @see org.mkcl.els.service.IGridService#getData(java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.util.Locale, java.util.Map)
     */
    @Override
    public GridData getData(final Long gridId, final Integer rows, final Integer page,
            final String sidx, final String order, final String filterSql, final Locale locale,
            final Map<String, String[]> requestMap) {
        return gridRepository.getData(gridId, rows, page, sidx, order,
                filterSql, locale, requestMap);
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.service.IGridService#getData(java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.util.Locale, java.util.Map)
     */
    @Override
    public GridData getData(final Long gridId, final Integer rows, final Integer page,
            final String sidx, final String order, final Locale locale,
            final Map<String, String[]> requestMap) {
        return gridRepository.getData(gridId, rows, page, sidx, order, locale,
                requestMap);
    }
}
