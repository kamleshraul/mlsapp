package org.mkcl.els.service.impl;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.mkcl.els.repository.DbLoggerRepsoitory;
import org.mkcl.els.service.IDbLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DbLoggerService implements IDbLoggerService{
    @Autowired
    private DbLoggerRepsoitory dbLoggerRepsoitory;
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
