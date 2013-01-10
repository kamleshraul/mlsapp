/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.ProcessServiceImpl.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.mkcl.els.common.util.FileUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * Activiti BPMN specific implementation of the IProcessService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Service("processService")
public class ProcessServiceImpl implements IProcessService {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** The repository service. */
	@Autowired
	private RepositoryService repositoryService;

	/** The task service. */
	@Autowired
	private TaskService taskService;

	/** The identity service. */
	@Autowired
	private IdentityService identityService;

	/** The form service. */
	@Autowired
	private FormService formService;

	/** The runtime service. */
	@Autowired
	private RuntimeService runtimeService;

	/** The management service. */
	@SuppressWarnings("unused")
	@Autowired
	private ManagementService managementService;

	/** The history service. */
	@SuppressWarnings("unused")
	@Autowired
	private HistoryService historyService;


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
	@Override
	public void deploy(final String name, final InputStream is) {
		DeploymentBuilder builder = this.repositoryService.createDeployment();
		builder.name(name);

		ZipFile zf = this.getZipFile(is);
		if(zf != null) {
			Enumeration<? extends ZipEntry> zipEnum = zf.entries();
			while(zipEnum.hasMoreElements()) {
				ZipEntry item = zipEnum.nextElement();
				String fileExt = FileUtil.fileExtension(item.getName());
				if(fileExt.equals("png") || fileExt.equals("jpg")) {
					this.addFileItem(zf, item, builder);
				}
				if(fileExt.equals("zip")) {
					this.addZipItem(zf, item, builder);
				}
			}
		}

		builder.deploy();
	}

	/**
	 * Deletes the given deployment.
	 *
	 * @param processDefinition the process definition
	 */
	@Override
	public void undeploy(
			final org.mkcl.els.common.vo.ProcessDefinition processDefinition) {
		this.undeploy(processDefinition, false);
	}

	/**
	 * Deletes the given deployment and cascade deletion to process instances,
	 * history process instances and jobs.
	 *
	 * @param processDefinition the process definition
	 * @param cascade the cascade
	 */
	@Override
	public void undeploy(
			final org.mkcl.els.common.vo.ProcessDefinition processDefinition,
			final Boolean cascade) {
		this.repositoryService.deleteDeployment(processDefinition.getDeploymentId(), cascade);
	}

	//==================== ProcessDefinition Queries ==================
	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IProcessService#getDeployedProcesses()
	 */
	@Override
	public List<org.mkcl.els.common.vo.ProcessDefinition> getDeployedProcesses() {
		List<org.mkcl.els.common.vo.ProcessDefinition> processDefinitions =
			new ArrayList<org.mkcl.els.common.vo.ProcessDefinition>();
		ProcessDefinitionQuery query = this.repositoryService.createProcessDefinitionQuery();

		List<ProcessDefinition> actProcDefs = query.orderByProcessDefinitionName()
		.orderByProcessDefinitionKey().asc().list();

		for(ProcessDefinition actProcDef : actProcDefs) {
			org.mkcl.els.common.vo.ProcessDefinition process =
				this.createProcessDefinition(actProcDef);

			processDefinitions.add(process);
		}
		return processDefinitions;
	}

	/**
	 * Returns null if their exists no Process Definition with the given id.
	 *
	 * @param id the id
	 * @return the org.mkcl.els.common.vo. process definition
	 */
	@Override
	public org.mkcl.els.common.vo.ProcessDefinition findProcessDefinitionById(final String id) {
		org.mkcl.els.common.vo.ProcessDefinition processDefinition = null;
		ProcessDefinitionQuery query = this.repositoryService.createProcessDefinitionQuery();
		ProcessDefinition actProcDef = query.processDefinitionId(id).singleResult();

		if(actProcDef != null) {
			processDefinition = this.createProcessDefinition(actProcDef);
		}
		return processDefinition;
	}

	/**
	 * Returns null if their exists no Process Definition with the given key.
	 *
	 * @param key the key
	 * @return the org.mkcl.els.common.vo. process definition
	 */
	@Override
	public org.mkcl.els.common.vo.ProcessDefinition findProcessDefinitionByKey(final String key) {
		org.mkcl.els.common.vo.ProcessDefinition processDefinition = null;
		ProcessDefinitionQuery query = this.repositoryService.createProcessDefinitionQuery();
		ProcessDefinition actProcDef = query.processDefinitionKey(key)
		.latestVersion().singleResult();

		if(actProcDef != null) {
			processDefinition = this.createProcessDefinition(actProcDef);
		}
		return processDefinition;
	}

	/**
	 * Returns null if their exists no form key for the given Process Definition.
	 *
	 * @param process the process
	 * @return the form key
	 */
	@Override
	public String getFormKey(final org.mkcl.els.common.vo.ProcessDefinition process) {
		String formKey = null;
		StartFormData sfd = this.formService.getStartFormData(process.getId());
		if(sfd != null) {
			formKey = sfd.getFormKey();
		}
		return formKey;
	}

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
	 * @return the org.mkcl.els.common.vo. process instance
	 */
	@Override
	public org.mkcl.els.common.vo.ProcessInstance createProcessInstance(
			final org.mkcl.els.common.vo.ProcessDefinition process,
			final Map<String, String> properties) {
		ProcessInstance actProcInst = this.formService
		.submitStartFormData(process.getId(), properties);
		return this.createProcessInstance(actProcInst);
	}

	/**
	 * Deletes an existing runtime process instance.
	 *
	 * @param process the process
	 * @param reason the reason
	 */
	@Override
	public void deleteProcessInstance(final org.mkcl.els.common.vo.ProcessInstance process,
			final String reason) {
		this.runtimeService.deleteProcessInstance(process.getId(), reason);
	}

	/**
	 * Returns null if their exists no Process Instance with the given id.
	 *
	 * @param processInstanceId the process instance id
	 * @return the org.mkcl.els.common.vo. process instance
	 */
	@Override
	public org.mkcl.els.common.vo.ProcessInstance findProcessInstanceById(final String processInstanceId) {
		org.mkcl.els.common.vo.ProcessInstance processInstance = null;
		ProcessInstanceQuery query = this.runtimeService.createProcessInstanceQuery();
		ProcessInstance actProcInst = query.processInstanceId(processInstanceId).singleResult();
		if(actProcInst != null) {
			processInstance = this.createProcessInstance(actProcInst);
		}
		return processInstance;
	}

	//==================== Task Queries ===============================
	/**
	 * Returns null if their exists no Task for the given taskId.
	 *
	 * @param taskId the task id
	 * @return the org.mkcl.els.common.vo. task
	 */
	@Override
	public org.mkcl.els.common.vo.Task findTaskById(final String taskId) {
		org.mkcl.els.common.vo.Task task = null;
		Task actTask = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		if(actTask != null) {
			task = this.createTask(actTask);
		}
		return task;
	}

	/**
	 * Gets the list of (pending) tasks of the user with the given userId.
	 *
	 * @param userId the user id
	 * @return the my tasks
	 */
	@Override
	public List<org.mkcl.els.common.vo.Task> getMyTasks(final String userId) {
		List<org.mkcl.els.common.vo.Task> tasks = new ArrayList<org.mkcl.els.common.vo.Task>();
		List<Task> actTasks = this.taskService.createTaskQuery()
		.taskAssignee(userId).orderByTaskCreateTime().desc().list();

		for(Task actTask : actTasks) {
			org.mkcl.els.common.vo.Task task = this.createTask(actTask);
			tasks.add(task);
		}

		return tasks;
	}

	/**
	 * Gets the list of (pending) tasks of the group to which the user
	 * with the given userId belongs.
	 *
	 * @param userId the user id
	 * @return the group tasks
	 */
	@Override
	public List<org.mkcl.els.common.vo.Task> getGroupTasks(final String userId) {
		List<org.mkcl.els.common.vo.Task> tasks = new ArrayList<org.mkcl.els.common.vo.Task>();
		List<Task> actTasks = this.taskService.createTaskQuery()
		.taskCandidateUser(userId).orderByTaskCreateTime().desc().list();

		for(Task actTask : actTasks) {
			org.mkcl.els.common.vo.Task task = this.createTask(actTask);
			tasks.add(task);
		}

		return tasks;
	}

	/**
	 * Gets the count of pending tasks of the user with the given userId.
	 *
	 * @param userId the user id
	 * @return the pending my task count
	 */
	@Override
	public Long getPendingMyTaskCount(final String userId) {
		return this.taskService.createTaskQuery().taskAssignee(userId).count();
	}

	/**
	 * Gets the count of pending tasks of the group to which the user
	 * with the given userId belongs.
	 *
	 * @param userId the user id
	 * @return the pending group task count
	 */
	@Override
	public Long getPendingGroupTaskCount(final String userId) {
		return this.taskService.createTaskQuery().taskCandidateUser(userId).count();
	}

	/**
	 * The given user is made assignee for the task. Note that a user can
	 * assign task to himself/herself (task claim) or a higher authority
	 * could assign a task to a user (task assign).
	 *
	 * @param task the task
	 * @param userId the user id
	 */
	@Override
	public void assignTask(final org.mkcl.els.common.vo.Task task,
			final String userId) {
		this.taskService.claim(task.getId(), userId);
	}

	/**
	 * Unassign (Unclaim) an assigned (claimed) task.
	 *
	 * @param task the task
	 * @param userId the user id
	 */
	@Override
	public void unassignTask(final org.mkcl.els.common.vo.Task task, final String userId) {
		this.taskService.deleteUserIdentityLink(task.getId(), userId, IdentityLinkType.ASSIGNEE);
		this.deleteTask(task);
	}

	/**
	 * Delegates the task to another user.
	 *
	 * @param task the task
	 * @param userId the user id
	 */
	// TODO: Note that this implementation will work only for Unclaimed tasks.
	//
	// What if a claimed task is to be delegated? We will need to overload the
	// delegateTask method. What will be the parameters to this method?
	// Refer DelegatedTaskRepository.delegateTask() method from Insync.
	//
	// We can look at the problem other way around. Write an unassignTask method
	// complementary to the assignTask method above. This way, the implementation
	// of delegateTask need not be changed, neither will there be any need to
	// overload delegateTask method. Refer ClaimedTaskRepository.unclaimTask()
	// I suppose, i will need to delete the task, create a new task & restore the
	// previous IdentityLinks from ProcessDefinition. You can study the implementation
	// of taskService.claim and then write a complementary method undoing what it does.

	// TODO: An assigner could delegate a task to multiple users. The following signature
	// is insufficient for that. I will need to overload the following method with an
	// array of userIds.
	@Override
	public void delegateTask(final org.mkcl.els.common.vo.Task task,
			final String userId) {
		this.taskService.delegateTask(task.getId(), userId);
	}

	/**
	 * Completes the task.
	 *
	 * @param task the task
	 */
	@Override
	public void completeTask(final org.mkcl.els.common.vo.Task task) {
		this.taskService.complete(task.getId());
	}

	/**
	 * Completes the task & adds properties to the Process instance.
	 * Note that this properties can be later retrieved using the methods:
	 * getVariables(Task task)
	 * getVariablesLocal(Task task)
	 *
	 * @param task the task
	 * @param properties the properties
	 */
	@Override
	public void completeTask(final org.mkcl.els.common.vo.Task task,
			final Map<String, String> properties) {
		this.formService.submitTaskFormData(task.getId(), properties);
	}

	/**
	 * Deletes the given task.
	 *
	 * @param task the task
	 */
	@Override
	public void deleteTask(final org.mkcl.els.common.vo.Task task) {
		this.taskService.deleteTask(task.getId());
	}

	/**
	 * Returns null if their exists no form key for the given Task.
	 *
	 * @param task the task
	 * @return the form key
	 */
	@Override
	public String getFormKey(final org.mkcl.els.common.vo.Task task) {
		String formKey = null;
		TaskFormData tfd = this.formService.getTaskFormData(task.getId());
		if(tfd != null) {
			formKey = tfd.getFormKey();
		}
		return formKey;
	}

	/**
	 * Returns a value of the specified process variable visible from the
	 * given execution scope (including parent scope). Returns null if their
	 * exists no key in the given execution scope (including parent scope).
	 *
	 * @param task the task
	 * @param key the key
	 * @return the variable
	 */
	@Override
	public String getVariable(final org.mkcl.els.common.vo.Task task, final String key) {
		String value = (String) this.runtimeService.getVariable(task.getId(), key);
		return value;
	}

	/**
	 * Returns a value of the specified process variable visible from the
	 * execution scope without taking outer scopes into account. Returns null
	 * if their exists no key in the given execution scope.
	 *
	 * @param task the task
	 * @param key the key
	 * @return the variable local
	 */
	@Override
	public String getVariableLocal(final org.mkcl.els.common.vo.Task task, final String key) {
		String value = (String) this.runtimeService.getVariableLocal(task.getId(), key);
		return value;
	}

	/**
	 * Returns a map of all the variables visible from the given execution
	 * scope (including parent scope).
	 *
	 * @param task the task
	 * @return the variables
	 */
	@Override
	public Map<String, Object> getVariables(final org.mkcl.els.common.vo.Task task) {
		return this.runtimeService.getVariables(task.getExecutionId());
	}

	/**
	 * Returns a map of all the variables defined in the execution scope
	 * without taking outer scopes into account.
	 *
	 * @param task the task
	 * @return the variables local
	 */
	@Override
	public Map<String, Object> getVariablesLocal(final org.mkcl.els.common.vo.Task task) {
		return this.runtimeService.getVariablesLocal(task.getExecutionId());
	}

	//==================== Identity Queries ===============================
	/**
	 * Creates User as well as Group.
	 *
	 * @param user the user
	 */
	@Override
	public void createUser(final User user) {
		String username = user.getCredential().getUsername();

		// Create a User
		org.activiti.engine.identity.User actUser = this.identityService.newUser(username);
		actUser.setPassword(user.getCredential().getPassword());
		actUser.setFirstName(user.getFirstName());
		actUser.setLastName(user.getLastName());
		actUser.setEmail(user.getCredential().getEmail());
		this.identityService.saveUser(actUser);
	}

	/**
	 * Updates User as well as Group.
	 *
	 * @param user the user
	 */
	@Override
	public void updateUser(final User user) {
		String username = user.getCredential().getUsername();
		UserQuery userQuery = this.identityService.createUserQuery();
		org.activiti.engine.identity.User actUser = userQuery.userId(username).singleResult();
		if(actUser != null) {
			// Update the User
			actUser.setPassword(user.getCredential().getPassword());
			actUser.setFirstName(user.getFirstName());
			actUser.setLastName(user.getLastName());
			actUser.setEmail(user.getCredential().getEmail());
			this.identityService.saveUser(actUser);
		}else{
			// Create a User
			org.activiti.engine.identity.User actUserNew = this.identityService.newUser(username);
			actUserNew.setPassword(user.getCredential().getPassword());
			actUserNew.setFirstName(user.getFirstName());
			actUserNew.setLastName(user.getLastName());
			actUserNew.setEmail(user.getCredential().getEmail());
			this.identityService.saveUser(actUserNew);
		}
	}

	/**
	 * Deletes User.
	 *
	 * @param user the user
	 */
	@Override
	public void deleteUser(final User user) {
		UserQuery userQuery = this.identityService.createUserQuery();
		org.activiti.engine.identity.User actUser =
			userQuery.userId(user.getCredential().getUsername()).singleResult();
		if(actUser != null) {
			// Delete the User
			this.identityService.deleteUser(actUser.getId());
		}
	}

	//========================= INTERNAL METHODS =========================
	/**
	 * Creates & returns an instance of ZipFile from an InputStream.
	 * Returns null if the aforementioned operation could not be performed.
	 *
	 * @param is the is
	 * @return the zip file
	 */
	private ZipFile getZipFile(final InputStream is) {
		ZipFile zipFile = null;
		String filePrefix = this.generateUUID();
		OutputStream out = null;
		try {
			// If we keep the filePrefix a static string then in a
			// concurrent environment, multiple threads invoking
			// the deploy method will render the file corrupt.
			File file = File.createTempFile(filePrefix, "zip");
			out = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			zipFile = new ZipFile(file);
		}
		catch (IOException e) {
			this.logger.error(e.getMessage());
		}
		finally {
			if(out != null) {
				try {
					out.close();
				}
				catch (IOException e) {
					this.logger.error("The OutputStream failed to close. This may result " +
					"in resource (file handle) leak.");
				}
			}
		}
		return zipFile;
	}

	/**
	 * Adds the file item.
	 *
	 * @param zf the zf
	 * @param item the item
	 * @param builder the builder
	 */
	private void addFileItem(final ZipFile zf, final ZipEntry item, final DeploymentBuilder builder) {
		InputStream is = null;
		try {
			is = zf.getInputStream(item);
			builder.addInputStream(item.getName(), is);
		}
		catch (IOException e) {
			this.logger.error(e.getMessage());
		}
		finally {
			if(is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
					this.logger.error("The InputStream failed to close. This may result " +
					"in resource (file handle) leak.");
				}
			}
		}
	}

	/**
	 * Adds the zip item.
	 *
	 * @param zf the zf
	 * @param item the item
	 * @param builder the builder
	 */
	private void addZipItem(final ZipFile zf, final ZipEntry item, final DeploymentBuilder builder) {
		InputStream is = null;
		ZipInputStream zis = null;
		try {
			is = zf.getInputStream(item);
			zis = new ZipInputStream(is);
			builder.addZipInputStream(zis);

		}
		catch (IOException e) {
			this.logger.error(e.getMessage());
		}
		finally {
			if(zis != null) {
				try {
					zis.close();
				}
				catch (IOException e) {
					this.logger.error("The ZipInputStream failed to close. This may result " +
					"in resource (file handle) leak.");
				}
			}
			if(is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
					this.logger.error("The InputStream failed to close. This may result " +
					"in resource (file handle) leak.");
				}
			}
		}
	}

	/**
	 * Creates the process definition.
	 *
	 * @param p the p
	 * @return the org.mkcl.els.common.vo. process definition
	 */
	private org.mkcl.els.common.vo.ProcessDefinition createProcessDefinition(final ProcessDefinition p) {
		DeploymentQuery query = this.repositoryService.createDeploymentQuery();
		Deployment deployment = query.deploymentId(p.getDeploymentId()).singleResult();
		Date deploymentTime = deployment.getDeploymentTime();

		CustomParameter cp = CustomParameter.findByName(CustomParameter.class,
				"SERVER_DATETIMEFORMAT", "");
		String strDeploymentTime = FormaterUtil.formatDateToString(deploymentTime, cp.getValue());

		return new org.mkcl.els.common.vo.ProcessDefinition(p.getId(), p.getKey(), p.getName(),
				p.getCategory(), p.getDiagramResourceName(), p.getDeploymentId(),
				strDeploymentTime, p.getVersion());
	}

	/**
	 * Creates the process instance.
	 *
	 * @param p the p
	 * @return the org.mkcl.els.common.vo. process instance
	 */
	private org.mkcl.els.common.vo.ProcessInstance createProcessInstance(final ProcessInstance p) {
		return new org.mkcl.els.common.vo.ProcessInstance(p.getProcessInstanceId(),
				p.getProcessDefinitionId());
	}

	/**
	 * Creates the task.
	 *
	 * @param t the t
	 * @return the org.mkcl.els.common.vo. task
	 */
	private org.mkcl.els.common.vo.Task createTask(final Task t) {
		org.mkcl.els.common.vo.Task task = new org.mkcl.els.common.vo.Task(t.getId(),
				t.getExecutionId(),
				t.getProcessInstanceId());
		task.setName(t.getName());
		task.setProcessDefinitionId(t.getProcessDefinitionId());
		task.setAssignee(t.getAssignee());

		//CustomParameter cp1 = CustomParameter.findByName(CustomParameter.class,
				//"yyyy-MM-dd HH:mm:ss", "");
		String strCreateTime = FormaterUtil.formatDateToString(t.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
		task.setCreateTime(strCreateTime);

		CustomParameter cp2 = CustomParameter.findByName(CustomParameter.class,
				"SERVER_DATETIMEFORMAT", "");
		String strDueDate = FormaterUtil.formatDateToString(t.getCreateTime(), cp2.getValue());
		task.setDueDate(strDueDate);

		task.setPriority(t.getPriority());
		task.setOwner(t.getOwner());
		task.setDescription(t.getDescription());

		return task;
	}

	/**
	 * The method is written with the assumption that hashCode()
	 * of UUID is always unique.
	 *
	 * @return the string
	 */
	private String generateUUID() {
		UUID uuid = UUID.randomUUID();
		return String.valueOf(uuid.hashCode());
	}

}