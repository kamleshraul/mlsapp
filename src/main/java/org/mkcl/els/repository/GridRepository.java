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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.Query;

//import org.activiti.engine.RepositoryService;
//import org.activiti.engine.repository.Deployment;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Autowired
//    private RepositoryService repositoryService;
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
        } else {
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
            } else {
                countQuery.setParameter("locale", defaultLocale);
            }
        }

        if (select.contains("=:locale")) {
            if (grid.getLocalized()) {
                query.setParameter("locale", locale.toString());
            } else {
                query.setParameter("locale", defaultLocale);
            }
        }
        // support for dynamic parameters setting in query

        //here since we are reading values from request map and hence to support unicode
        //we need to first read it in ISO-8859-1 format and then get its bytes using utf-8
        //format.
        Set<Parameter<?>> selectQueryParameters = query.getParameters();
        for (Parameter i : selectQueryParameters) {
            if (!i.getName().equals("locale")) {
                if (i.getParameterType().getSimpleName().equals("String")) {
                    query.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                } else if (i.getParameterType().getSimpleName().equals("Long")) {
                    query.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    query.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    query.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Float")) {
                    query.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Double")) {
                    query.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    query.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                } else if (i.getParameterType().getSimpleName()
                        .equals("Date")) {                	
                    try {
						query.setParameter(i.getName(),
						      new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT).parse(requestMap.get(i.getName())[0]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
                }
            }
        }
        Set<Parameter<?>> countQueryParameters = countQuery.getParameters();
        for (Parameter i : countQueryParameters) {
            if (!i.getName().equals("locale")) {
                if (i.getParameterType().getSimpleName().equals("String")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                } else if (i.getParameterType().getSimpleName().equals("Long")) {
                    countQuery.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    countQuery.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    countQuery.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Float")) {
                    countQuery.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Double")) {
                    countQuery.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }else if (i.getParameterType().getSimpleName()
                        .equals("Date")) {                	
                    try {
                    	countQuery.setParameter(i.getName(),
						      new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT).parse(requestMap.get(i.getName())[0]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
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
        } else {
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
            } else {
                countQuery.setParameter("locale", defaultLocale);
            }
        }

        if (select.contains("=:locale")) {
            if (grid.getLocalized()) {
                query.setParameter("locale", locale.toString());
            } else {
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
                } else if (i.getParameterType().getSimpleName().equals("Long")) {
                    query.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    query.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    query.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Float")) {
                    query.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Double")) {
                    query.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    query.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }else if (i.getParameterType().getSimpleName()
                        .equals("Date")) {                	
                    try {
						query.setParameter(i.getName(),
						      new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT).parse(requestMap.get(i.getName())[0]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
                }
            }
        }
        Set<Parameter<?>> countQueryParameters = countQuery.getParameters();
        for (Parameter i : countQueryParameters) {
            if (!i.getName().equals("locale")) {
                if (i.getParameterType().getSimpleName().equals("String")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                } else if (i.getParameterType().getSimpleName().equals("Long")) {
                    countQuery.setParameter(i.getName(),
                            Long.parseLong(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Integer")) {
                    countQuery.setParameter(i.getName(),
                            Integer.parseInt(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Boolean")) {
                    countQuery.setParameter(i.getName(), Boolean
                            .parseBoolean(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Float")) {
                    countQuery.setParameter(i.getName(),
                            Float.parseFloat(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName().equals("Double")) {
                    countQuery.setParameter(i.getName(),
                            Double.parseDouble(requestMap.get(i.getName())[0]));
                } else if (i.getParameterType().getSimpleName()
                        .equals("Character")) {
                    countQuery.setParameter(i.getName(),
                            requestMap.get(i.getName())[0]);
                }else if (i.getParameterType().getSimpleName()
                        .equals("Date")) {                	
                    try {
                    	countQuery.setParameter(i.getName(),
						      new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT).parse(requestMap.get(i.getName())[0]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
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

//    /*
//     * This method is used to get list of workflow deployments
//     */
//    public GridData getDeployments(final Long gridId, final Integer rows, Integer page,
//            final String sidx, final String order, final Locale locale,
//            final Map<String, String[]> requestMap) {
//        List<Deployment> deployments=null;
//        int lastResult=page*rows;
//        long totalDeployments=repositoryService.createDeploymentQuery().count();
//        int totalPages=0;
//        if (totalDeployments > 0) {
//            totalPages = (int) Math.ceil((float) totalDeployments / rows);
//        }
//        if (page > totalPages) {
//            page = totalPages;
//        }
//        int firstResult=rows * page - rows;
//        int maxResults=(int) (totalDeployments > rows ? totalDeployments : rows);
//
//        //Depending on the sidx and order
//        if(sidx.equals("id")){
//            if(order.equals(ApplicationConstants.ASC)){
//                deployments=repositoryService.createDeploymentQuery().orderByDeploymentId().asc().listPage(firstResult,maxResults);
//            }else{
//                deployments=repositoryService.createDeploymentQuery().orderByDeploymentId().desc().listPage(firstResult,maxResults);
//            }
//        }else if(sidx.equals("name")){
//            if(order.equals(ApplicationConstants.ASC)){
//                deployments=repositoryService.createDeploymentQuery().orderByDeploymentName().asc().listPage(firstResult,maxResults);
//            }else{
//                deployments=repositoryService.createDeploymentQuery().orderByDeploymentName().desc().listPage(firstResult,maxResults);
//            }
//        }else{
//            if(order.equals(ApplicationConstants.ASC)){
//                deployments=repositoryService.createDeploymentQuery().orderByDeploymenTime().asc().listPage(firstResult,maxResults);
//            }else{
//                deployments=repositoryService.createDeploymentQuery().orderByDeploymenTime().desc().listPage(firstResult,maxResults);
//            }
//        }
//        //converting List<Deployment> into List<Map<String,Object>>
//        List<Map<String,Object>> records=new ArrayList<Map<String,Object>>();
//        for(Deployment i:deployments){
//            Map<String,String> eachRecord=new HashMap<String, String>();
//            eachRecord.put("id", i.getId());
//            eachRecord.put("name", i.getName());
//            eachRecord.put("time", FormaterUtil.getDateFormatter(locale.toString()).format(i.getDeploymentTime()));
//        }
//        return new GridData(page, totalPages, totalDeployments, records);
//    }

//    public GridData getData(final Long gridId, final Integer rows, Integer page,
//            final String sidx, final String order, final String searchField, final String searchValue,
//            final Locale locale, final Map<String, String[]> requestMap) {
//        List<Deployment> deployments=null;
//        int lastResult=page*rows;
//        long totalDeployments=repositoryService.createDeploymentQuery().count();
//        int totalPages=0;
//        if (totalDeployments > 0) {
//            totalPages = (int) Math.ceil((float) totalDeployments / rows);
//        }
//        if (page > totalPages) {
//            page = totalPages;
//        }
//        int firstResult=rows * page - rows;
//        int maxResults=(int) (totalDeployments > rows ? totalDeployments : rows);
//
//        //Depending on the sidx and order
//        if(searchField.equals("id")){
//            if(sidx.equals("id")){
//                if(order.equals(ApplicationConstants.ASC)){
//                    deployments=repositoryService.createDeploymentQuery().deploymentId(searchValue).orderByDeploymentId().asc().listPage(firstResult,maxResults);
//                }else{
//                    deployments=repositoryService.createDeploymentQuery().deploymentId(searchValue).orderByDeploymentId().desc().listPage(firstResult,maxResults);
//                }
//            }else if(sidx.equals("name")){
//                if(order.equals(ApplicationConstants.ASC)){
//                    deployments=repositoryService.createDeploymentQuery().deploymentId(searchValue).orderByDeploymentName().asc().listPage(firstResult,maxResults);
//                }else{
//                    deployments=repositoryService.createDeploymentQuery().deploymentId(searchValue).orderByDeploymentName().desc().listPage(firstResult,maxResults);
//                }
//            }else{
//                if(order.equals(ApplicationConstants.ASC)){
//                    deployments=repositoryService.createDeploymentQuery().deploymentId(searchValue).orderByDeploymenTime().asc().listPage(firstResult,maxResults);
//                }else{
//                    deployments=repositoryService.createDeploymentQuery().deploymentId(searchValue).orderByDeploymenTime().desc().listPage(firstResult,maxResults);
//                }
//            }
//        }else if(searchField.equals("name")){
//            if(sidx.equals("id")){
//                if(order.equals(ApplicationConstants.ASC)){
//                    deployments=repositoryService.createDeploymentQuery().deploymentName(searchValue).orderByDeploymentId().asc().listPage(firstResult,maxResults);
//                }else{
//                    deployments=repositoryService.createDeploymentQuery().deploymentName(searchValue).orderByDeploymentId().desc().listPage(firstResult,maxResults);
//                }
//            }else if(sidx.equals("name")){
//                if(order.equals(ApplicationConstants.ASC)){
//                    deployments=repositoryService.createDeploymentQuery().deploymentName(searchValue).orderByDeploymentName().asc().listPage(firstResult,maxResults);
//                }else{
//                    deployments=repositoryService.createDeploymentQuery().deploymentName(searchValue).orderByDeploymentName().desc().listPage(firstResult,maxResults);
//                }
//            }else{
//                if(order.equals(ApplicationConstants.ASC)){
//                    deployments=repositoryService.createDeploymentQuery().deploymentName(searchValue).orderByDeploymenTime().asc().listPage(firstResult,maxResults);
//                }else{
//                    deployments=repositoryService.createDeploymentQuery().deploymentName(searchValue).orderByDeploymenTime().desc().listPage(firstResult,maxResults);
//                }
//            }
//        }
//        //converting List<Deployment> into List<Map<String,Object>>
//        List<Map<String,Object>> records=new ArrayList<Map<String,Object>>();
//        for(Deployment i:deployments){
//            Map<String,String> eachRecord=new HashMap<String, String>();
//            eachRecord.put("id", i.getId());
//            eachRecord.put("name", i.getName());
//            eachRecord.put("time", FormaterUtil.getDateFormatter(locale.toString()).format(i.getDeploymentTime()));
//        }
//        return new GridData(page, totalPages, totalDeployments, records);
//
//    }

//    public GridData getMembers(final Long gridId, final Integer rows, final Integer page,
//            final String sidx, final String order, final String filterSql, final Locale locale,
//            final Map<String, String[]> requestMap) {
//        return null;
//    }
//
//    public GridData getMembers(final Long gridId, final Integer rows, final Integer page,
//            final String sidx, final String order, final Locale locale,
//            final Map<String, String[]> requestMap) {
//
//      return null;
//    }
    }
