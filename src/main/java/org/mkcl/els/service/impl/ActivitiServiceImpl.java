package org.mkcl.els.service.impl;

import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.mkcl.els.service.IActivitiService;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ActivitiServiceImpl implements IActivitiService {
	
	/** The process service. */
	@Autowired
	protected IProcessService processService;
	
	@Override
	public void execute(ActivityExecution execution) throws Exception {
		System.out.println("in service task");		
	}

}
