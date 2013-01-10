/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.Task.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;


// TODO: Auto-generated Javadoc
/**
 * Captures the attributes of a Process' task in execution.
 *
 * @author amitd
 * @since v1.0.0
 */
public class Task {

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	/** The execution id. */
	private String executionId;

	/** The process definition id. */
	private String processDefinitionId;

	/** The process instance id. */
	private String processInstanceId;

	/** The assignee. */
	private String assignee;

	/** The create time. */
	private String createTime;

	/** The due date. */
	private String dueDate;

	/** The priority. */
	private Integer priority;

	/** The owner. */
	private String owner;

	/** The description. */
	private String description;

	/** The device id. */
	private String deviceId;

	/** The device type. */
	private String deviceType;

	/** The user group. */
	private String userGroup;

	/**
	 * Instantiates a new task.
	 *
	 * @param id the id
	 * @param executionId the execution id
	 * @param processInstanceId the process instance id
	 */
	public Task(final String id, final String executionId, final String processInstanceId) {
		super();
		this.setId(id);
		this.setExecutionId(executionId);
		this.setProcessInstanceId(processInstanceId);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the execution id.
	 *
	 * @return the execution id
	 */
	public String getExecutionId() {
		return executionId;
	}

	/**
	 * Sets the execution id.
	 *
	 * @param executionId the new execution id
	 */
	public void setExecutionId(final String executionId) {
		this.executionId = executionId;
	}

	/**
	 * Gets the process definition id.
	 *
	 * @return the process definition id
	 */
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	/**
	 * Sets the process definition id.
	 *
	 * @param processDefinitionId the new process definition id
	 */
	public void setProcessDefinitionId(final String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	/**
	 * Gets the process instance id.
	 *
	 * @return the process instance id
	 */
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	/**
	 * Sets the process instance id.
	 *
	 * @param processInstanceId the new process instance id
	 */
	public void setProcessInstanceId(final String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	/**
	 * Gets the assignee.
	 *
	 * @return the assignee
	 */
	public String getAssignee() {
		return assignee;
	}

	/**
	 * Sets the assignee.
	 *
	 * @param assignee the new assignee
	 */
	public void setAssignee(final String assignee) {
		this.assignee = assignee;
	}

    /**
     * Gets the creates the time.
     *
     * @return the creates the time
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * Sets the creates the time.
     *
     * @param createTime the new creates the time
     */
    public void setCreateTime(final String createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets the due date.
     *
     * @return the due date
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the new due date
     */
    public void setDueDate(final String dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public Integer getPriority() {
		return priority;
	}

	/**
	 * Sets the priority.
	 *
	 * @param priority the new priority
	 */
	public void setPriority(final Integer priority) {
		this.priority = priority;
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(final String owner) {
		this.owner = owner;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}


    /**
     * Gets the device id.
     *
     * @return the device id
     */
    public String getDeviceId() {
        return deviceId;
    }


    /**
     * Sets the device id.
     *
     * @param deviceId the new device id
     */
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }


    /**
     * Gets the device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }


    /**
     * Sets the device type.
     *
     * @param deviceType the new device type
     */
    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }


    /**
     * Gets the user group.
     *
     * @return the user group
     */
    public String getUserGroup() {
        return userGroup;
    }


    /**
     * Sets the user group.
     *
     * @param userGroup the new user group
     */
    public void setUserGroup(final String userGroup) {
        this.userGroup = userGroup;
    }
}
