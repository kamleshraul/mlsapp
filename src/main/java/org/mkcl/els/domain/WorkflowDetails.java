package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.vo.Task;
import org.mkcl.els.repository.WorkflowDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
	
	private String assigneeUserGroupType;
	
	private String assigneeUserGroupId;
		
	private String assigneeUserGroupName;
	
	private String assigneeLevel;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date assignmentTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date completionTime;
	
	/**** This will denote the kind of request received ****/
	@Column(length=1000)
	private String workflowType;
	
	@Column(length=1000)
	private String workflowSubType;
	
	/**** This will have two status PENDING/COMPLETED ****/
	private String status;
	
	/**** For question's approval workflow and supporting member workflow ****/
	@Column(length=100)
	private String deviceId;
	
	@Column(length=200)
	private String deviceType;
	
	@Column(length=100)
	private String deviceNumber;
	
	@Column(length=1000)
	private String deviceOwner;
	
	@Column(length=1000)
	private String internalStatus;
	
	@Column(length=1000)
	private String recommendationStatus;
	
	private String houseType;
	
	private String sessionType;
	
	private String sessionYear;	
	
	@Column(length=10000)
	private String remarks;
	
	private String urlPattern;
	
	private String form;
	
	@Column(length=30000)
	private String subject;
	
	private String groupNumber;
	
	@Autowired
    private transient WorkflowDetailsRepository workflowDetailsRepository;
	
	public static WorkflowDetailsRepository getWorkflowDetailsRepository() {
		WorkflowDetailsRepository workflowDetailsRepository = new WorkflowDetails().workflowDetailsRepository;
        if (workflowDetailsRepository == null) {
            throw new IllegalStateException(
                    "WorkflowDetailsrepository has not been injected in WorkflowDetails Domain");
        }
        return workflowDetailsRepository;
    }

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
	
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceOwner(String deviceOwner) {
		this.deviceOwner = deviceOwner;
	}

	public String getDeviceOwner() {
		return deviceOwner;
	}

	public void setInternalStatus(String internalStatus) {
		this.internalStatus = internalStatus;
	}

	public String getInternalStatus() {
		return internalStatus;
	}

	public void setRecommendationStatus(String recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public String getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getSessionYear() {
		return sessionYear;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getForm() {
		return form;
	}

	public static WorkflowDetails create(final Question question,final Task task,final String workflowType,
			final String assigneeLevel) {
		return getWorkflowDetailsRepository().create(question,task,workflowType,
				assigneeLevel);
	}

	public void setAssigneeUserGroupName(String assigneeUserGroupName) {
		this.assigneeUserGroupName = assigneeUserGroupName;
	}

	public String getAssigneeUserGroupName() {
		return assigneeUserGroupName;
	}

	public void setAssigneeLevel(String assigneeLevel) {
		this.assigneeLevel = assigneeLevel;
	}

	public String getAssigneeLevel() {
		return assigneeLevel;
	}

	public static List<WorkflowDetails> create(final Question question,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) {
		return getWorkflowDetailsRepository().create(question,tasks,
				workflowType,assigneeLevel);
	}

	public void setAssignmentTime(Date assignmentTime) {
		this.assignmentTime = assignmentTime;
	}

	public Date getAssignmentTime() {
		return assignmentTime;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public static WorkflowDetails findCurrentWorkflowDetail(final Question question) {
		return getWorkflowDetailsRepository().findCurrentWorkflowDetail(question);
	}

	public void setWorkflowSubType(String workflowSubType) {
		this.workflowSubType = workflowSubType;
	}

	public String getWorkflowSubType() {
		return workflowSubType;
	}

	public void setAssigneeUserGroupId(String assigneeUserGroupId) {
		this.assigneeUserGroupId = assigneeUserGroupId;
	}

	public String getAssigneeUserGroupId() {
		return assigneeUserGroupId;
	}

	public void setAssigneeUserGroupType(String assigneeUserGroupType) {
		this.assigneeUserGroupType = assigneeUserGroupType;
	}

	public String getAssigneeUserGroupType() {
		return assigneeUserGroupType;
	}	

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}		
	
}
