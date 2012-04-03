/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.GridRepository.java
 * Created On: Mar 26, 2012
 */
package org.mkcl.els.repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.Query;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class GridRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class GridRepository extends BaseRepository<Grid, Long> {

    /**
     * Find by detail view.
     *
     * @param urlPattern the url pattern
     * @param locale the locale
     * @return the grid
     */
    public Grid findByDetailView(final String urlPattern, final String locale) {
        Search search = new Search();
        search.addFilterEqual("detailView", urlPattern);
        search.addFilterEqual("locale", locale);
        return this.searchUnique(search);
    }

    // new methods

    /**
     * Gets the data.
     *
     * @param gridId the grid id
     * @param limit the limit
     * @param page the page
     * @param sidx the sidx
     * @param order the order
     * @param locale the locale
     * @param requestMap the request map
     * @return the data
     */
    public GridData getData(final Long gridId, final Integer limit, Integer page,
            final String sidx, final String order, final Locale locale,
            final Map<String, String[]> requestMap) {
        Grid grid = Grid.findById(Grid.class, gridId);
        String countSelect = null;
        Query countQuery = null;
        String select = null;
        Query query = null;
        
        //provision for native query
        
        //added by sandeeps
        //there is no need for order by clause in count query as count is independent of sort order
        //also it creates problem in case select statement contains joins and count statement don't.
        
        if (!sidx.contains(".")) {
            countSelect = grid.getCountQuery();
            select = grid.getQuery() + " ORDER BY m." + sidx + " " + order;
        }
        else {
            countSelect = grid.getCountQuery();
            select = grid.getQuery() + " ORDER BY " + sidx + " " + order;
        }
        countQuery = this.em().createQuery(countSelect);
        query = this.em().createQuery(select);

        String defaultLocale = ((CustomParameter) CustomParameter
                .findByFieldName(CustomParameter.class, "name",
                        "DEFAULT_LOCALE", "")).getValue();
        if (countSelect.contains("=:locale")) {
            if (grid.getLocalized()) {
                countQuery.setParameter("locale", locale.toString());
            }
            else {
                countQuery.setParameter("locale", defaultLocale);
            }
        }

        if (select.contains("=:locale")) {
            if (grid.getLocalized()) {
                query.setParameter("locale", locale.toString());
            }
            else {
                query.setParameter("locale", defaultLocale);
            }
        }
        // support for dynamic parameters setting in query
        Set<Parameter<?>> selectQueryParameters = query.getParameters();
        for (Parameter i : selectQueryParameters) {
            if (!i.getName().equals("locale")) {
                if (i.getParameterType().getSimpleName().equals("String")) {
                    query.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
                else if (i.getParameterType().getSimpleName().equals("Long")) {
                    query.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    query.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    query.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Float")) {
                    query.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Double")) {
                    query.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    query.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
            }
        }
        Set<Parameter<?>> countQueryParameters = countQuery.getParameters();
        for (Parameter i : countQueryParameters) {
            if (!i.getName().equals("locale")) {
                if (i.getParameterType().getSimpleName().equals("String")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
                else if (i.getParameterType().getSimpleName().equals("Long")) {
                    countQuery.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    countQuery.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    countQuery.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Float")) {
                    countQuery.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Double")) {
                    countQuery.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
            }
        }
        //

        Long count = (Long) countQuery.getSingleResult();
        Integer totalPages = 0;
        if (count > 0) {
            totalPages = (int) Math.ceil((float) count / limit);
        }
        if (page > totalPages) {
            page = totalPages;
        }
        int start = (limit * page - limit);
        if (start < 0) {
            start = 0;
        }
        query.setFirstResult(start);
        query.setMaxResults((int) (count > limit ? count : limit));
       // System.out.println(query.getParameterValue("locale").toString());
        //System.out.println(query.getParameterValue("housetype").toString());
        List<Map<String, Object>> records = query.getResultList();
        GridData gridVO = new GridData(page, totalPages, count, records);
        return gridVO;
    }

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
     * @param requestMap the request map
     * @return the data
     */
    public GridData getData(final Long gridId, final Integer limit, Integer page,
            final String sidx, final String order, final String filterSql, final Locale locale,
            final Map<String, String[]> requestMap) {
        Grid grid = Grid.findById(Grid.class, gridId);
        String countSelect = null;
        Query countQuery = null;
        String select = null;
        Query query = null;
        
        if (!sidx.contains(".")) {
            countSelect = grid.getCountQuery() + filterSql;
            select = grid.getQuery() + filterSql + " ORDER BY m." + sidx + " "
                    + order;
        }
        else {
            countSelect = grid.getCountQuery() + filterSql;
            select = grid.getQuery() + filterSql + " ORDER BY " + sidx + " "
                    + order;
        }
        countQuery = this.em().createQuery(countSelect);
        query = this.em().createQuery(select);

        String defaultLocale = ((CustomParameter) CustomParameter
                .findByFieldName(CustomParameter.class, "name",
                        "DEFAULT_LOCALE", "")).getValue();
        if (countSelect.contains("=:locale")) {
            if (grid.getLocalized()) {
                countQuery.setParameter("locale", locale.toString());
            }
            else {
                countQuery.setParameter("locale", defaultLocale);
            }
        }

        if (select.contains("=:locale")) {
            if (grid.getLocalized()) {
                query.setParameter("locale", locale.toString());
            }
            else {
                query.setParameter("locale", defaultLocale);
            }
        }
        // support for dynamic parameters setting in query
        Set<Parameter<?>> selectQueryParameters = query.getParameters();
        for (Parameter i : selectQueryParameters) {
            if (!i.getName().equals("locale")) {
                if (i.getParameterType().getSimpleName().equals("String")) {
                    query.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
                else if (i.getParameterType().getSimpleName().equals("Long")) {
                    query.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    query.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    query.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Float")) {
                    query.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Double")) {
                    query.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    query.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
            }
        }
        Set<Parameter<?>> countQueryParameters = countQuery.getParameters();
        for (Parameter i : countQueryParameters) {
            if (!i.getName().equals("locale")) {
                if (i.getParameterType().getSimpleName().equals("String")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
                else if (i.getParameterType().getSimpleName().equals("Long")) {
                    countQuery.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    countQuery.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    countQuery.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Float")) {
                    countQuery.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName().equals("Double")) {
                    countQuery.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                }
                else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }
            }
        }
        //

        Long count = (Long) countQuery.getSingleResult();
        Integer totalPages = 0;
        if (count > 0) {
            totalPages = (int) Math.ceil((float) count / limit);
        }
        if (page > totalPages) {
            page = totalPages;
        }
        int start = (limit * page - limit);
        if (start < 0) {
            start = 0;
        }
        query.setFirstResult(start);
        query.setMaxResults((int) (count > limit ? count : limit));

        List<Map<String, Object>> records = query.getResultList();
        GridData gridVO = new GridData(page, totalPages, count, records);
        return gridVO;
    }
}
