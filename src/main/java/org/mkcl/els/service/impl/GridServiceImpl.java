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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.repository.GridRepository;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IProcessService;
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

    @Autowired
    private IProcessService processService;

    @Override
    public GridData getData(final Long gridId, final Integer rows, final Integer page,
            final String sidx, final String order, final String filterSql, final Locale locale,
            final Map<String, String[]> requestMap) {
        return gridRepository.getData(gridId, rows, page, sidx, order,
                filterSql, locale, requestMap);
    }

    @Override
    public GridData getData(final Long gridId, final Integer rows, final Integer page,
            final String sidx, final String order, final Locale locale,
            final Map<String, String[]> requestMap) {
        return gridRepository.getData(gridId, rows, page, sidx, order, locale,
                requestMap);
    }

	@Override
	public GridData getDeploymentsData(Long gridId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {		
		List<ProcessDefinition> processDefns = processService.getDeployedProcesses();
		List<Map<String, Object>> records = new ArrayList<Map<String,Object>>();
		for(ProcessDefinition p : processDefns) {
			Map<String, Object> tuple = new HashMap<String, Object>();
			tuple.put("id", p.getId());
			tuple.put("key", p.getKey());
			tuple.put("name", p.getName());
			tuple.put("category", p.getCategory());
			tuple.put("processImage", p.getProcessImage());
			tuple.put("version", p.getVersion());
			tuple.put("deploymentTime", p.getDeploymentTime());
			records.add(tuple);
		}
		
		Integer totalPages = 0;
		Integer count = processDefns.size();
		if(count > 0) {
			totalPages = (int) Math.ceil((float) count / rows);
		}
		if(page > totalPages) {
			page = totalPages;
		}
		return new GridData(page, totalPages, count, records);
		
//		List processDefns = processService.getDeployedProcesses();
//		List<Map<String, Object>> records = processDefns;
//		Integer totalPages = 0;
//		Integer count = processDefns.size();
//		if(count > 0) {
//			totalPages = (int) Math.ceil((float) count / rows);
//		}
//		if(page > totalPages) {
//			page = totalPages;
//		}
//		return new GridData(page, totalPages, count, records);
	}

	@Override
	public GridData getMyTasksData(Long gridId, String username, Integer rows,
			Integer page, String sidx, String order, Locale locale) {
		return this.getTasksData(gridId, username, rows, page, sidx, order, locale);
	}

	@Override
	public GridData getGroupTasksData(Long gridId, String username,
			Integer rows, Integer page, String sidx, String order, Locale locale) {
		return this.getTasksData(gridId, username, rows, page, sidx, order, locale);
	}
	
	private GridData getTasksData(Long gridId, String username,
			Integer rows, Integer page, String sidx, String order, Locale locale) {
		List<Task> tasks = processService.getMyTasks(username);
		List<Map<String, Object>> records = new ArrayList<Map<String,Object>>();
		for(Task t : tasks) {
			Map<String, Object> tuple = new HashMap<String, Object>();
			tuple.put("id", t.getId());
			tuple.put("name", t.getName());
			tuple.put("description", t.getDescription());
			tuple.put("createTime", t.getCreateTime());
			tuple.put("dueDate", t.getDueDate());
			records.add(tuple);
		}
		
		Integer totalPages = 0;
		Integer count = tasks.size();
		if(count > 0) {
			totalPages = (int) Math.ceil((float) count / rows);
		}
		if(page > totalPages) {
			page = totalPages;
		}
		return new GridData(page, totalPages, count, records);
	}
	
//    @Override
//    public GridData getDeployments(final Long gridId, final Integer rows, final Integer page,
//            final String sidx, final String order, final Locale locale,
//            final Map<String, String[]> requestMap) {
//        return gridRepository.getDeployments(gridId, rows, page, sidx, order, locale,
//                requestMap);
//    }
//
//    @Override
//    public GridData getDeployments(final Long gridId, final Integer rows, final Integer page,
//            final String sidx, final String order, final String searchField, final String searchValue,
//            final Locale locale, final Map<String, String[]> requestMap) {
//        return gridRepository.getData(gridId, rows, page, sidx, order, searchField, searchValue, locale,
//                requestMap);
//    }
//
//    @Override
//    public GridData getMembers(final Long gridId, final Integer rows, final Integer page,
//            final String sidx, final String order, final String filterSql, final Locale locale,
//            final Map<String, String[]> requestMap) {
//        return gridRepository.getMembers(gridId, rows, page, sidx, order,
//                filterSql, locale, requestMap);
//    }
//
//    @Override
//    public GridData getMembers(final Long gridId, final Integer rows, final Integer page,
//            final String sidx, final String order, final Locale locale,
//            final Map<String, String[]> requestMap) {
//        return gridRepository.getMembers(gridId, rows, page, sidx, order, locale,
//                requestMap);
//    }
}
