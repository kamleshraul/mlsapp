package org.mkcl.els.common.vo;


/**
 * Captures the attributes of a Process' task in execution.
 */
public class Task {

	private String id;

	private String name;

	private String executionId;

	private String processDefinitionId;

	private String processInstanceId;

	private String assignee;

	private String createTime;

	private String dueDate;

	private Integer priority;

	private String owner;

	private String description;

	private String deviceId;

	private String deviceType;

	private String userGroup;

	public Task(final String id, final String executionId, final String processInstanceId) {
		super();
		this.setId(id);
		this.setExecutionId(executionId);
		this.setProcessInstanceId(processInstanceId);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(final String executionId) {
		this.executionId = executionId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(final String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(final String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(final String assignee) {
		this.assignee = assignee;
	}

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final String createTime) {
        this.createTime = createTime;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(final String dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
		return priority;
	}

	public void setPriority(final Integer priority) {
		this.priority = priority;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(final String owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}


    public String getDeviceId() {
        return deviceId;
    }


    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }


    public String getDeviceType() {
        return deviceType;
    }


    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }


    public String getUserGroup() {
        return userGroup;
    }


    public void setUserGroup(final String userGroup) {
        this.userGroup = userGroup;
    }
}
