package org.mkcl.els.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.User;

/**
 * Interface to the Processes (Workflows) in the application.
 * All the interactions with the Process Engine must happen through
 * this interface.
 */
public interface IProcessService {
	
	//==================== Deployment Queries ===================
	/**
	 * The process file to be deployed (& on which the InputStream is created) 
	 * should have the following structure:
	 * 
	 * 		.bar(a zip file with bar extension)
	 * 		|- .png OR .jpg (an image file)
	 * 		|- .zip
	 * 			|- .xml (the process definition file)
	 * 		
	 */
	public void deploy(String name, InputStream is);
	
	/**
	 * Deletes the given deployment.
	 */
	public void undeploy(ProcessDefinition processDefinition);
	
	/**
	 * Deletes the given deployment and cascade deletion to process instances, 
	 * history process instances and jobs.
	 */
	public void undeploy(ProcessDefinition processDefinition, Boolean cascade);
	
	
	//==================== ProcessDefinition Queries ==================
	public List<ProcessDefinition> getDeployedProcesses();
	
	/**
	 * Returns null if their exists no Process Definition with the given id.
	 */
	public ProcessDefinition findProcessDefinitionById(String id);
	
	/**
	 * Returns null if their exists no Process Definition with the given key.
	 */
	public ProcessDefinition findProcessDefinitionByKey(String key);
	
	/**
	 * Returns null if their exists no form key for the given Process Definition. 
	 */
	public String getFormKey(ProcessDefinition process);
	
	
	//==================== ProcessInstance Queries ====================
	/**
	 * Creates an instance of the Process defined by the given ProcessDefinition
	 * & adds properties to the Process instance. Note that this properties can
	 * be later retrieved using the methods:
	 * 	getVariables(Task task) 
	 * 	getVariablesLocal(Task task)
	 */
	public ProcessInstance createProcessInstance(ProcessDefinition process,
			Map<String, String> properties);
	
	/**
	 * Deletes an existing runtime process instance.
	 */
	public void deleteProcessInstance(ProcessInstance process, String reason);
	
	/**
	 * Returns null if their exists no Process Instance with the given id.
	 */
	public ProcessInstance findProcessInstanceById(String processInstanceId);
	
	
	//==================== Task Queries ===============================
	/**
	 * Returns null if their exists no Task for the given taskId. 
	 */
	public Task findTaskById(String taskId);
	
	/**
	 * Gets the list of (pending) tasks of the user with the given userId.
	 */
	public List<Task> getMyTasks(String userId);
	
	/**
	 * Gets the list of (pending) tasks of the group to which the user 
	 * with the given userId belongs.
	 */
	public List<Task> getGroupTasks(String userId);
	
	/**
	 * Gets the count of pending tasks of the user with the given userId.
	 */
	public Long getPendingMyTaskCount(String userId);
	
	/**
	 * Gets the count of pending tasks of the group to which the user 
	 * with the given userId belongs.
	 */
	public Long getPendingGroupTaskCount(String userId);
	
	/**
	 * The given user is made assignee for the task. Note that a user can 
	 * assign task to himself/herself (task claim) or a higher authority
	 * could assign a task to a user (task assign).
	 */
	public void assignTask(Task task, String userId);
	
	/**
	 * Unassign (Unclaim) an assigned (claimed) task.
	 */
	public void unassignTask(Task task, String userId);
	
	/**
	 * Delegates the task to another user.
	 */
	public void delegateTask(Task task, String userId);
	
	/**
	 * Completes the task. 
	 */
	public void completeTask(Task task);
	
	/**
	 * Completes the task & adds properties to the Process instance. 
	 * Note that this properties can be later retrieved using the methods:
	 * 	getVariables(Task task) 
	 * 	getVariablesLocal(Task task)
	 */
	public void completeTask(Task task, Map<String, String> properties);
	
	/**
	 * Deletes the given task.
	 */
	public void deleteTask(Task task);
	
	/**
	 * Returns null if their exists no form key for the given Task.
	 */
	public String getFormKey(Task task);
	
	/**
	 * Returns a value of the specified process variable visible from the 
	 * given execution scope (including parent scope).
	 */
	public String getVariable(Task task, String key);
	
	/**
	 * Returns a value of the specified process variable visible from the
	 * execution scope without taking outer scopes into account.
	 */
	public String getVariableLocal(Task task, String key);
	
	/**
	 * Returns a map of all the variables visible from the given execution
	 * scope (including parent scope).
	 */
	public Map<String, Object> getVariables(Task task);
	
	/**
	 * Returns a map of all the variables defined in the execution scope 
	 * without taking outer scopes into account.
	 */
	public Map<String, Object> getVariablesLocal(Task task);
	
	
	//==================== Identity Queries ===============================
	/**
	 * Creates User as well as Group.
	 */
	public void createUser(User user);
	
	/**
	 * Updates User as well as Group.
	 */
	public void updateUser(User user);
	
	/**
	 * Deletes User.
	 */
	public void deleteUser(User user);
	
}
