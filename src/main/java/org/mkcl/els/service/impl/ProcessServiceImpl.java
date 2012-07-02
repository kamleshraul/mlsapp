///**
// * See the file LICENSE for redistribution information.
// *
// * Copyright (c) 2012 MKCL.  All rights reserved.
// *
// * Project: e-Legislature
// * File: org.mkcl.els.service.impl.ProcessServiceImpl.java
// * Created On: May 15, 2012
// */
//
//
//package org.mkcl.els.service.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.activiti.engine.RepositoryService;
//import org.activiti.engine.repository.ProcessDefinition;
//import org.activiti.engine.repository.ProcessDefinitionQuery;
//import org.mkcl.els.common.vo.Process;
//import org.mkcl.els.service.IProcessService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * The Class ProcessServiceImpl.
// *
// * @author amitd
// * @author sandeeps
// * @since v1.0.0
// */
//@Service("processService")
//public class ProcessServiceImpl implements IProcessService {
//
//    /** The repository service. */
//    @Autowired
//    private RepositoryService repositoryService;
//
//    /* (non-Javadoc)
//     * @see org.mkcl.els.service.IProcessService#getDeployedProcesses()
//     * This method is used to create list of all the processes that is deployed
//     * in the activiti engine.Also this method is used to populate Process VO
//     * which is used to contain the basic info about a process.
//     */
//    @Override
//    public List<Process> getDeployedProcesses() {
//        List<Process> processes = new ArrayList<Process>();
//        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
//        List<ProcessDefinition> processDefns = query.orderByProcessDefinitionName()
//        .orderByProcessDefinitionKey().latestVersion()
//        .asc().list();
//        for (ProcessDefinition p : processDefns) {
//            Process process = new Process(p.getId() , p.getKey() ,
//                    p.getName() , p.getVersion() , p.getCategory().trim() ,
//                    p.getKey().replaceAll("_" , " "));
//            processes.add(process);
//        }
//        return processes;
//    }
//
//}
