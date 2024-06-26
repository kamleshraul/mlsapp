/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.GridController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.Filter;
import org.mkcl.els.common.vo.GridConfig;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class GridController.
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/grid")
public class GridController extends GenericController<Grid> {

	/** The grid service. */
	@Autowired
	private IGridService gridService;

	/**
	 * Gets the config.
	 *
	 * @param gridId the grid id
	 * @param model the model
	 * @param request the request
	 * @return the config
	 * @throws ClassNotFoundException the class not found exception
	 */
	@RequestMapping(value = "/{gridId}/meta", method = RequestMethod.GET)
	public @ResponseBody
	GridConfig getConfig(@PathVariable final Long gridId,
			final ModelMap model,
			final HttpServletRequest request)
	throws ClassNotFoundException {
		return Grid.getConfig(gridId);
	}

	/**
	 * Gets the.
	 *
	 * @param gridId the grid id
	 * @param page the page
	 * @param rows the rows
	 * @param sidx the sidx
	 * @param order the order
	 * @param search the search
	 * @param searchField the search field
	 * @param searchString the search string
	 * @param searchOper the search oper
	 * @param filtersData the filters data
	 * @param baseFilters the base filters
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the grid data
	 * @throws ClassNotFoundException the class not found exception
	 * @author nileshp
	 * @since v1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/data/{gridId}", method = RequestMethod.GET)
	public @ResponseBody
	GridData get(@PathVariable final Long gridId,
			@RequestParam(value = "page", required = false) final Integer page,
			@RequestParam(value = "rows", required = false) final Integer rows,
			@RequestParam(value = "sidx", required = false) final String sidx,
			@RequestParam(value = "sord", required = false) final String order,
			@RequestParam(value = "_search", required = false) final Boolean search,
			@RequestParam(value = "searchField", required = false) final String searchField,
			@RequestParam(value = "searchString", required = false) final String searchString,
			@RequestParam(value = "searchOper", required = false) final String searchOper,
			@RequestParam(value = "baseFilters", required = false) final String baseFilters,
			@RequestParam(value = "houseType", required = false) final String houseType,
			final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) throws ClassNotFoundException {
		//Adding support for dynamic parameters in where clause of grid count and select query
		//Here search is a get request and hence the parameters are encoded using ISO-8859-1 scheme .This
		//needs to be converted to utf-8 before any search operations can be performed.
		Map<String,String[]> requestMap=request.getParameterMap();
		GridData gridData=new GridData();
		if (search) {
			//this for deployment on tomcat
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
			if(customParameter!=null){
				if(customParameter.getValue().equals("TOMCAT")){
					try {
						String param=request.getParameter("filters");
						String decodedFiltersData=null;
						decodedFiltersData = new String(param.getBytes("ISO-8859-1"), "UTF-8");
						Filter filter = Filter.create(decodedFiltersData);
						gridData=gridService.getData(
								gridId, rows, page, sidx, order, filter.toSQl(), locale,requestMap);
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}else {
					//this is for deployment on glassfish
					String param=request.getParameter("filters");
					Filter filter = Filter.create(param);
					gridData=gridService.getData(
							gridId, rows, page, sidx, order, filter.toSQl(), locale,requestMap);
				}
			}
		} else {
			gridData= gridService.getData(gridId, rows, page, sidx, order, locale,requestMap);
		}
		return gridData;
	}


	/**
	 * Handles process deployments' grid request.
	 */
	@RequestMapping(value="deployment/data/{gridId}", method=RequestMethod.GET)
	public @ResponseBody GridData getDeploymentData(@PathVariable final Long gridId,
			@RequestParam(value = "page", required = false) final Integer page,
			@RequestParam(value = "rows", required = false) final Integer rows,
			@RequestParam(value = "sidx", required = false) final String sidx,
			@RequestParam(value = "sord", required = false) final String order,
			@RequestParam(value = "_search", required = false) final Boolean search,
			@RequestParam(value = "searchField", required = false) final String searchField,
			@RequestParam(value = "searchString", required = false) final String searchString,
			@RequestParam(value = "searchOper", required = false) final String searchOper,
			@RequestParam(value = "baseFilters", required = false) final String baseFilters,
			final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) throws ClassNotFoundException {
		return this.gridService.getDeploymentsData(gridId, rows, page, sidx, order, locale);
	}

	/**
	 * Handles My Tasks' grid request.
	 */
	@RequestMapping(value="myTasks/data/{gridId}", method=RequestMethod.GET)
	public @ResponseBody GridData getMyTasksData(@PathVariable final Long gridId,
			@RequestParam(value = "page", required = false) final Integer page,
			@RequestParam(value = "rows", required = false) final Integer rows,
			@RequestParam(value = "sidx", required = false) final String sidx,
			@RequestParam(value = "sord", required = false) final String order,
			@RequestParam(value = "_search", required = false) final Boolean search,
			@RequestParam(value = "searchField", required = false) final String searchField,
			@RequestParam(value = "searchString", required = false) final String searchString,
			@RequestParam(value = "searchOper", required = false) final String searchOper,
			@RequestParam(value = "baseFilters", required = false) final String baseFilters,
			final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) throws ClassNotFoundException {
		AuthUser user = this.getCurrentUser();
		return this.gridService.getMyTasksData(gridId, user.getUsername(),
				rows, page, sidx, order, locale);
	}

	/**
	 * Handles Group Tasks' grid request.
	 */
	@RequestMapping(value="groupTasks/data/{gridId}", method=RequestMethod.GET)
	public @ResponseBody GridData getGroupTasksData(@PathVariable final Long gridId,
			@RequestParam(value = "page", required = false) final Integer page,
			@RequestParam(value = "rows", required = false) final Integer rows,
			@RequestParam(value = "sidx", required = false) final String sidx,
			@RequestParam(value = "sord", required = false) final String order,
			@RequestParam(value = "_search", required = false) final Boolean search,
			@RequestParam(value = "searchField", required = false) final String searchField,
			@RequestParam(value = "searchString", required = false) final String searchString,
			@RequestParam(value = "searchOper", required = false) final String searchOper,
			@RequestParam(value = "baseFilters", required = false) final String baseFilters,
			final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) throws ClassNotFoundException {
		AuthUser user = this.getCurrentUser();
		return this.gridService.getGroupTasksData(gridId, user.getUsername(),
				rows, page, sidx, order, locale);
	}

	//    @RequestMapping(value = "/member/{gridId}", method = RequestMethod.GET)
	//    public @ResponseBody
	//    GridData getMembers(@PathVariable final Long gridId,
	//            @RequestParam(value = "page", required = false) final Integer page,
	//            @RequestParam(value = "rows", required = false) final Integer rows,
	//            @RequestParam(value = "sidx", required = false) final String sidx,
	//            @RequestParam(value = "sord", required = false) final String order,
	//            @RequestParam(value = "_search", required = false) final Boolean search,
	//            @RequestParam(value = "searchField", required = false) final String searchField,
	//            @RequestParam(value = "searchString", required = false) final String searchString,
	//            @RequestParam(value = "searchOper", required = false) final String searchOper,
	//            @RequestParam(value = "baseFilters", required = false) final String baseFilters,
	//            final ModelMap model,
	//            final HttpServletRequest request,
	//            final Locale locale) throws ClassNotFoundException {
	//        //Adding support for dynamic parameters in where clause of grid count and select query
	//        //Here search is a get request and hence the parameters are encoded using ISO-8859-1 scheme .This
	//        //needs to be converted to utf-8 before any search operations can be performed.
	//        Map<String,String[]> requestMap=request.getParameterMap();
	//        GridData gridData=new GridData();
	//        if (search) {
	//            //this for deployment on tomcat
	//            //            try {
	//            //                String param=request.getParameter("filters");
	//            //                String decodedFiltersData=null;
	//            //                decodedFiltersData = new String(param.getBytes("ISO-8859-1"), "UTF-8");
	//            //                Filter filter = Filter.create(decodedFiltersData);
	//            //                 gridData=gridService.getData(
	//            //                        gridId, rows, page, sidx, order, filter.toSQl(), locale,requestMap);
	//            //            }
	//            //            catch (UnsupportedEncodingException e) {
	//            //                e.printStackTrace();
	//            //            }
	//            //this is for deployment on glassfish
	//            String param=request.getParameter("filters");
	//            Filter filter = Filter.create(param);
	//            gridData=gridService.getMembers(
	//                    gridId, rows, page, sidx, order, filter.toSQl(), locale,requestMap);
	//        } else {
	//            gridData= gridService.getMembers(gridId, rows, page, sidx, order, locale,requestMap);
	//        }
	//        return gridData;
	//    }
	//
	//
	//    @RequestMapping(value = "/data/deployments/{gridId}", method = RequestMethod.GET)
	//    public @ResponseBody
	//    GridData getDeployments(@PathVariable final Long gridId,
	//            @RequestParam(value = "page", required = false) final Integer page,
	//            @RequestParam(value = "rows", required = false) final Integer rows,
	//            @RequestParam(value = "sidx", required = false) final String sidx,
	//            @RequestParam(value = "sord", required = false) final String order,
	//            @RequestParam(value = "_search", required = false) final Boolean search,
	//            @RequestParam(value = "searchField", required = false) final String searchField,
	//            @RequestParam(value = "searchString", required = false) final String searchString,
	//            @RequestParam(value = "searchOper", required = false) final String searchOper,
	//            @RequestParam(value = "baseFilters", required = false) final String baseFilters,
	//            final ModelMap model,
	//            final HttpServletRequest request,
	//            final Locale locale) throws ClassNotFoundException {
	//        Map<String,String[]> requestMap=request.getParameterMap();
	//        GridData gridData=new GridData();
	//        if (search) {
	//            //this for deployment on tomcat
	//            try {
	//                String param=request.getParameter("filters");
	//                String decodedFiltersData=null;
	//                decodedFiltersData = new String(param.getBytes("ISO-8859-1"), "UTF-8");
	//                Filter filter = Filter.create(decodedFiltersData);
	//                Rule[] rules=filter.getRules();
	//                gridData=gridService.getDeployments(
	//                        gridId, rows, page, sidx, order, rules[0].getField(), rules[0].getData(), locale,requestMap);
	//            }
	//            catch (UnsupportedEncodingException e) {
	//                e.printStackTrace();
	//            }
	//            //this is for deployment on glassfish
	//            //            String param=request.getParameter("filters");
	//            //            Filter filter = Filter.create(param);
	//            //            Rule[] rules=filter.getRules();
	//            //            gridData=gridService.getData(
	//            //                 gridId, rows, page, sidx, order, rules[0].getField(), rules[0].getData(), locale,requestMap);
	//        } else {
	//            gridData= gridService.getDeployments(gridId, rows, page, sidx, order, locale,requestMap);
	//        }
	//        return gridData;
	//    }

	
}
