/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IDbLoggerService.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.service;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;

// TODO: Auto-generated Javadoc
/**
 * The Interface IDbLoggerService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public interface IDbLoggerService {

	/**
	 * Execute.
	 *
	 * @param execution the execution
	 * @param storedProcedureName the stored procedure name
	 */
	public void execute(DelegateExecution execution,String storedProcedureName);

	/**
	 * Execute db logger.
	 *
	 * @param map the map
	 * @param storedProcedureName the stored procedure name
	 */
	public void executeDBLogger(Map<String,String> map,String storedProcedureName);
}
