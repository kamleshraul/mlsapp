/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.DbLoggerService.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.service.impl;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.mkcl.els.repository.DbLoggerRepsoitory;
import org.mkcl.els.service.IDbLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class DbLoggerService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Service
public class DbLoggerService implements IDbLoggerService{

    /** The db logger repsoitory. */
    @Autowired
    private DbLoggerRepsoitory dbLoggerRepsoitory;

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IDbLoggerService#execute(org.activiti.engine.delegate.DelegateExecution, java.lang.String)
	 */
	@Override
    @Transactional
	public void execute(final DelegateExecution execution,final String storedProcedureName) {
		Map variables=execution.getVariables();
		StringBuffer buffer=new StringBuffer();
		for(Object key : variables.keySet()) {
			buffer.append((String)key+"##"+variables.get(key)+"~");
		}
		dbLoggerRepsoitory.log(buffer.toString(),execution.getProcessInstanceId(),storedProcedureName);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IDbLoggerService#executeDBLogger(java.util.Map, java.lang.String)
	 */
	@Override
    @Transactional
	public void executeDBLogger(final Map<String,String> map,final String storedProcedureName)
	{
		StringBuffer buffer=new StringBuffer();
		for(String key : map.keySet()) {
			buffer.append(key+"##"+map.get(key)+"~");
		}
		dbLoggerRepsoitory.log(buffer.toString(),map.get("proc_id"),storedProcedureName);

	}



}
