package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="workflow_details")
public class WorkflowDetails extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String processId;
	
	private String taskId;	
	
	private String assignee;
	
	private String assigneeUserGroup;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date assinmentTime;

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getAssigneeUserGroup() {
		return assigneeUserGroup;
	}

	public void setAssigneeUserGroup(String assigneeUserGroup) {
		this.assigneeUserGroup = assigneeUserGroup;
	}

	public Date getAssinmentTime() {
		return assinmentTime;
	}

	public void setAssinmentTime(Date assinmentTime) {
		this.assinmentTime = assinmentTime;
	}
	
}
