package org.mkcl.els.service;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;

public interface IDbLoggerService {
	public void execute(DelegateExecution execution,String storedProcedureName);
	public void executeDBLogger(Map<String,String> map,String storedProcedureName);
}
