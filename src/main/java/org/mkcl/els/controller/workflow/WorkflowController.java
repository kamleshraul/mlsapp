///**
// * See the file LICENSE for redistribution information.
// *
// * Copyright (c) 2012 MKCL.  All rights reserved.
// *
// * Project: e-Legislature
// * File: org.mkcl.els.controller.workflow.WorkflowController.java
// * Created On: May 15, 2012
// */
//package org.mkcl.els.controller.workflow;
//
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Locale;
//import java.util.Set;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.mkcl.els.controller.BaseController;
//import org.mkcl.els.domain.Grid;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//
//@Controller
//@RequestMapping("/wf")
//public class WorkflowController extends BaseController {
//
//    @RequestMapping(value = "/deploy/module", method = RequestMethod.GET)
//    public String deployProcessModule() {
//        return "workflows/deploy/module";
//    }
//
//    @RequestMapping(value = "/deploy/list", method = RequestMethod.GET)
//    public String deployProcessList(final Locale locale,final ModelMap model) {
//        Grid grid=Grid.findByDetailView("wf/deploy", locale.toString());
//        model.addAttribute("gridId",grid.getId());
//        return "workflows/deploy/list";
//    }
//
//    @RequestMapping(value = "/deploy/new", method = RequestMethod.GET)
//    public String deployProcessNew(final Locale locale,final ModelMap model) {
//        return "workflows/deploy/new";
//    }
//
//    /**
//     * Gets the listof processes.
//     *
//     * @param model the model
//     * @return the listof processes
//     * This is to list all the deployed processes.
//     */
////    @RequestMapping(value = "/list", method = RequestMethod.GET)
////    public String getListofProcesses(final ModelMap model) {
////        List<Process> processes = processService.getDeployedProcesses();
////        model.addAttribute("processes", processes);
////        return "process_list";
////    }
//
//    /**
//     * Inits the process.
//     * @param map the map
//     * @param request the request
//     * @param module the module
//     * @param form the form
//     * @return the string
//     * This is to display the initiator/requestor form.Here we will be using
//     * custom form and not the generic form. For this we can follow a
//     * convention where in we will pass module name,form name to represent the directory
//     * which contains the forms needed in various workflows.
//     *
//     */
////    @RequestMapping(value = "/initform", method = RequestMethod.GET)
////    public String initProcess(final ModelMap map ,
////            final HttpServletRequest request ,
////            @RequestParam("module")final String module ,
////            @RequestParam("form")final String form) {
////        /*
////         * We create the process definition fom the Process name passed as a request parameter.
////         */
////        ProcessDefinition processDefinition =
////            repositoryService.createProcessDefinitionQuery()
////            .processDefinitionKey(request.getParameter("pv_proc_key"))
////            .latestVersion().singleResult();
////        String processDefinitionId = processDefinition.getId();
////        map.addAttribute("pv_proc_key", processDefinition.getKey());
////        return module + "/" + form;
////
////    }
//
//    /**
//     * Inits the submit.
//     *
//     * @param map the map
//     * @param request the request
//     * @return the string
//     */
////    @RequestMapping(value = "/init", method = RequestMethod.POST)
////    public String initSubmit(final ModelMap map, final HttpServletRequest request) {
////        ProcessDefinition processDefinition =
////            repositoryService.createProcessDefinitionQuery()
////            .processDefinitionKey(request.getParameter("pv_proc_key"))
////            .latestVersion().singleResult();
////        String processDefinitionId = processDefinition.getId();
////        HashMap<String, String> formProperties = this.getProcessProperties(request);
////        identityService.setAuthenticatedUserId(getCurrentUser().getUsername());
////        ProcessInstance processInstance = formService
////        .submitStartFormData(processDefinitionId, formProperties);
////        map.addAttribute("proc_id" , processInstance.getId());
////        return "success";
////    }
//
//    /**
//     * Gets the tasklist for the current user.
//     *
//     * @param map the map
//     * @param request the request
//     * @return the tasklist
//     * This is used to display the list
//     * of Tasks  assigned to the authenticated
//     * user or the groups the authenticated user belongs to.
//     */
////    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
////    public String getTasklist(final ModelMap map, final HttpServletRequest request){
////        String returnval = "";
////        String userId = this.getCurrentUser().getUsername();
////        String assignee = request.getParameter("assignee");
////        List<Task> tasks = new ArrayList<Task>();
////        if (assignee.equals("user")) {
////            tasks = taskService.createTaskQuery()
////            .taskAssignee(userId).orderByTaskCreateTime()
////            .desc().list();
////            map.addAttribute("tasks", tasks);
////            returnval = "task_list";
////        } else {
////            tasks = taskService.createTaskQuery()
////            .taskCandidateUser(userId).orderByTaskCreateTime()
////            .desc().list();
////            map.addAttribute("tasks", tasks);
////            returnval = "inbox_list";
////        }
////        return returnval;
////    }
//
//    /**
//     * Gets the pending task count.
//     *
//     * @param map the map
//     * @param request the request
//     * @return the pending task count
//     * This is used to calculate the number of tasks
//     * assigned to the authenticated users which are
//     * pending.Also number of tasks assigned to the
//     * groups wo which the authenticated user belong
//     * and are pending.
//     */
////    @RequestMapping(value = "/pendingtasks", method = RequestMethod.GET)
////    public @ResponseBody Reference getPendingTaskCount(
////            final ModelMap map, final HttpServletRequest request){
////        String userId = this.getCurrentUser().getUsername();
////        long myTasksCount = 0;
////        long groupTasksCount = 0;
////        myTasksCount = taskService.createTaskQuery().taskAssignee(userId).count();
////        groupTasksCount = taskService.createTaskQuery()
////        .taskCandidateUser(userId).count();
////        Reference ref = new Reference();
////        ref.setId(String.valueOf(myTasksCount));
////        ref.setName(String.valueOf(groupTasksCount));
////        return ref;
////    }
//
//
//    /**
//     * Inits the process.
//     *
//     * @param map the map
//     * @param request the request
//     * @param module the module
//     * @param form the form
//     * @return the string
//     * This is used to initiate the task assigned to the authenticated user.
//     */
////    @RequestMapping(value = "/taskform", method = RequestMethod.GET)
////    public String initUserTask(final ModelMap map ,
////            final HttpServletRequest request ,
////            @RequestParam("module") final String module ,
////            @RequestParam("form") final String form) {
////        TaskFormData formData =
////            formService.getTaskFormData(
////                    request.getParameter("task_id"));
////        Map<String, Object> proc_vars =
////            runtimeService.getVariables(formData.getTask()
////                    .getExecutionId());
////        map.addAttribute("proc_vars" , proc_vars);
////        return module + "/" + form;
////    }
//
//    /**
//     * Submit user task.
//     *
//     * @param map the map
//     * @param request the request
//     * @return the string
//     * This is used to submit a task form
//     */
//
////    @RequestMapping(value = "/task", method = RequestMethod.POST)
////    public String submitUserTask(final ModelMap map ,
////            final HttpServletRequest request) {
////        Enumeration<String> paramNames = request.getParameterNames();
////        Map<String, String> formProperties = this.getProcessProperties(request);
////        identityService.setAuthenticatedUserId(getCurrentUser().getUsername());
////        formService.submitTaskFormData(request.getParameter("task_id"), formProperties);
////        map.addAttribute("proc_id", request.getParameter("proc_id"));
////        return "success";
////    }
//
//    /**
//     * Delete task.
//     *
//     * @param request the request
//     * @param task_id the task_id
//     * @return the string
//     */
////    @Transactional
////    @RequestMapping(value = "/task/delete", method = RequestMethod.GET)
////    public String deleteTask(final HttpServletRequest request ,
////            @RequestParam final String task_id){
////        Task task = taskService.createTaskQuery()
////        .taskId(task_id).singleResult();
////        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "Cleaned");
////        return "redirect:/wf/tasks?assignee=user";
////    }
//    /**
//     * Gets the form properties.
//     *
//     * @param request the request
//     * @return the form properties
//     */
//    @SuppressWarnings("unchecked")
//    protected HashMap<String, String> getProcessProperties(
//            final HttpServletRequest request) {
//        Enumeration<String> paramNames = request.getParameterNames();
//        HashMap<String, String> formProperties = new HashMap<String , String>();
//        while (paramNames.hasMoreElements()) {
//            String paramName = paramNames.nextElement();
//            if (paramName.startsWith("pv_") || paramName.startsWith("code_pv_")) {
//                /*
//                 * The below code block is used receive the data sent
//                 * in the dynamicselect control.The array of string is
//                 * converted to a 'set' first so as to avoid any duplicacy
//                 * and then converted to a comma separated string
//                 * which is stored in the process variable.
//                 */
//                if (paramName.endsWith("[]")) {
//                    String[] temp = request.getParameterValues(paramName);
//                    Set<String> tempSet = new HashSet<String>();
//                    for (String i:temp) {
//                        tempSet.add(i);
//                    }
//                    StringBuffer buffer = new StringBuffer();
//                    for (String i:tempSet)  {
//                        buffer.append(i+",");
//                    }
//
//                    if (buffer != null) {
//                        String paramValue = buffer.toString()
//                        .trim().length() == 0 ? null : buffer.toString();
//                        formProperties.put(paramName.split("\\[\\]")[0]
//                                                                     , paramValue);
//                    }
//                } else {
//                    String paramValue = request.getParameter(paramName);
//                    if (paramValue != null) {
//                        paramValue = paramValue.trim().length()
//                        == 0 ? null : paramValue;
//                    }
//                    formProperties.put(paramName, paramValue);
//                }
//            }
//        }
//        return formProperties;
//    }
//
//
//}
