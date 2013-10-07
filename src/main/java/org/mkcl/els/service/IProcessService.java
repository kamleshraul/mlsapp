/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IProcessService.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.User;

// TODO: Auto-generated Javadoc
/**
 * Interface to the Processes (Workflows) in the application.
 * All the interactions with the Process Engine must happen through
 * this interface.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public interface IProcessService {

	//==================== Deployment Queries ===================
	/**
	 * The process file to be deployed (& on which the InputStream is created)
	 * should have the following structure:
	 *
	 * .bar(a zip file with bar extension)
	 * |- .png OR .jpg (an image file)
	 * |- .zip
	 * |- .xml (the process definition file)
	 *
	 * @param name the name
	 * @param is the is
	 */
	public void deploy(String name, InputStream is);

	/**
	 * Deletes the given deployment.
	 *
	 * @param processDefinition the process definition
	 */
	public void undeploy(ProcessDefinition processDefinition);

	/**
	 * Deletes the given deployment and cascade deletion to process instances,
	 * history process instances and jobs.
	 *
	 * @param processDefinition the process definition
	 * @param cascade the cascade
	 */
	public void undeploy(ProcessDefinition processDefinition, Boolean cascade);


	//==================== ProcessDefinition Queries ==================
	/**
	 * Gets the deployed processes.
	 *
	 * @return the deployed processes
	 */
	public List<ProcessDefinition> getDeployedProcesses();

	/**
	 * Returns null if their exists no Process Definition with the given id.
	 *
	 * @param id the id
	 * @return the process definition
	 */
	public ProcessDefinition findProcessDefinitionById(String id);

	/**
	 * Returns null if their exists no Process Definition with the given key.
	 *
	 * @param key the key
	 * @return the process definition
	 */
	public ProcessDefinition findProcessDefinitionByKey(String key);

	/**
	 * Returns null if their exists no form key for the given Process Definition.
	 *
	 * @param process the process
	 * @return the form key
	 */
	public String getFormKey(ProcessDefinition process);


	//==================== ProcessInstance Queries ====================
	/**
	 * Creates an instance of the Process defined by the given ProcessDefinition
	 * & adds properties to the Process instance. Note that this properties can
	 * be later retrieved using the methods:
	 * getVariables(Task task)
	 * getVariablesLocal(Task task)
	 *
	 * @param process the process
	 * @param properties the properties
	 * @return the process instance
	 */
	public ProcessInstance createProcessInstance(ProcessDefinition process,
			Map<String, String> properties);

	/**
	 * Deletes an existing runtime process instance.
	 *
	 * @param process the process
	 * @param reason the reason
	 */
	public void deleteProcessInstance(ProcessInstance process, String reason);

	/**
	 * Returns null if their exists no Process Instance with the given id.
	 *
	 * @param processInstanceId the process instance id
	 * @return the process instance
	 */
	public ProcessInstance findProcessInstanceById(String processInstanceId);


	//==================== Task Queries ===============================
	/**
	 * Returns null if their exists no Task for the given taskId.
	 *
	 * @param taskId the task id
	 * @return the task
	 */
	public Task findTaskById(String taskId);

	/**
	 * Gets the list of (pending) tasks of the user with the given userId.
	 *
	 * @param userId the user id
	 * @return the my tasks
	 */
	public List<Task> getMyTasks(String userId);

	/**
	 * Gets the list of (pending) tasks of the group to which the user
	 * with the given userId belongs.
	 *
	 * @param userId the user id
	 * @return the group tasks
	 */
	public List<Task> getGroupTasks(String userId);

	/**
	 * Gets the count of pending tasks of the user with the given userId.
	 *
	 * @param userId the user id
	 * @return the pending my task count
	 */
	public Long getPendingMyTaskCount(String userId);

	/**
	 * Gets the count of pending tasks of the group to which the user
	 * with the given userId belongs.
	 *
	 * @param userId the user id
	 * @return the pending group task count
	 */
	public Long getPendingGroupTaskCount(String userId);

	/**
	 * The given user is made assignee for the task. Note that a user can
	 * assign task to himself/herself (task claim) or a higher authority
	 * could assign a task to a user (task assign).
	 *
	 * @param task the task
	 * @param userId the user id
	 */
	public void assignTask(Task task, String userId);

	/**
	 * Unassign (Unclaim) an assigned (claimed) task.
	 *
	 * @param task the task
	 * @param userId the user id
	 */
	public void unassignTask(Task task, String userId);

	/**
	 * Delegates the task to another user.
	 *
	 * @param task the task
	 * @param userId the user id
	 */
	public void delegateTask(Task task, String userId);

	/**
	 * Completes the task.
	 *
	 * @param task the task
	 */
	public void completeTask(Task task);

	/**
	 * Completes the task & adds properties to the Process instance.
	 * Note that this properties can be later retrieved using the methods:
	 * getVariables(Task task)
	 * getVariablesLocal(Task task)
	 *
	 * @param task the task
	 * @param properties the properties
	 */
	public void completeTask(Task task, Map<String, String> properties);

	/**
	 * Deletes the given task.
	 *
	 * @param task the task
	 */
	public void deleteTask(Task task);

	/**
	 * Returns null if their exists no form key for the given Task.
	 *
	 * @param task the task
	 * @return the form key
	 */
	public String getFormKey(Task task);

	/**
	 * Returns a value of the specified process variable visible from the
	 * given execution scope (including parent scope).
	 *
	 * @param task the task
	 * @param key the key
	 * @return the variable
	 */
	public String getVariable(Task task, String key);

	/**
	 * Returns a value of the specified process variable visible from the
	 * execution scope without taking outer scopes into account.
	 *
	 * @param task the task
	 * @param key the key
	 * @return the variable local
	 */
	public String getVariableLocal(Task task, String key);

	/**
	 * Returns a map of all the variables visible from the given execution
	 * scope (including parent scope).
	 *
	 * @param task the task
	 * @return the variables
	 */
	public Map<String, Object> getVariables(Task task);

	/**
	 * Returns a map of all the variables defined in the execution scope
	 * without taking outer scopes into account.
	 *
	 * @param task the task
	 * @return the variables local
	 */
	public Map<String, Object> getVariablesLocal(Task task);


	//==================== Identity Queries ===============================
	/**
	 * Creates User as well as Group.
	 *
	 * @param user the user
	 */
	public void createUser(User user);

	/**
	 * Updates User as well as Group.
	 *
	 * @param user the user
	 */
	public void updateUser(User user);

	/**
	 * Deletes User.
	 *
	 * @param user the user
	 */
	public void deleteUser(User user);
	
	/**** Added By Sandeep Singh ****/
	public Task getCurrentTask(final ProcessInstance processInstance);

	public List<Task> getCurrentTasks(final ProcessInstance processInstance);

	public boolean isTaskActive(final ProcessInstance processInstance);

}
