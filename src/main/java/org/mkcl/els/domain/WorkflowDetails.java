package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.repository.WorkflowDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

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
	
	@Column(length=30000)
	private String text;
	
	private String groupNumber;
	
	private String file;
		
	private String departmentAnswer;
	
	private Long previousWorkflowDetail;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendBackBefore;
	
	/**** Attributes for Non Device Types viz, Committee ****/
	// Comma separated Ids
	@Column(length=1000)
	private String domainIds;
		
	private String nextWorkflowActorId;
	
	@Column(length=1000)
	private String module;
	
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
			final String assigneeLevel) throws ELSException {
		return getWorkflowDetailsRepository().create(question,task,workflowType,assigneeLevel);
	}
	
	public static WorkflowDetails create(final Resolution resolution,final Task task,final String workflowType,
			final String assigneeLevel, final HouseType houseTypeForWorkflow) throws ELSException {
		return getWorkflowDetailsRepository().create(resolution,task,workflowType,
				assigneeLevel, houseTypeForWorkflow);
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
			final String workflowType,final String assigneeLevel) throws ELSException {
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

	public static WorkflowDetails findCurrentWorkflowDetail(final Question question) throws ELSException {
		return getWorkflowDetailsRepository().findCurrentWorkflowDetail(question);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Resolution resolution) throws ELSException {
		return getWorkflowDetailsRepository().findCurrentWorkflowDetail(resolution);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Resolution resolution, final String workflowHouseType) throws ELSException {
		return getWorkflowDetailsRepository().findCurrentWorkflowDetail(resolution, workflowHouseType);
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

	/**** Motion Related 
	 * @throws ELSException ****/
	public static List<WorkflowDetails> create(final Motion domain,final List<Task> tasks,
			final String supportingMemberWorkflow, final String assigneeLevel) throws ELSException {		
		return getWorkflowDetailsRepository().create(domain,tasks,
				supportingMemberWorkflow,assigneeLevel);
	}

	public static WorkflowDetails create(final Motion domain,final Task task,
			final String workflowType,final String level) throws ELSException {
		return getWorkflowDetailsRepository().create(domain,task,
				workflowType,level);
	}

	public static List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,final String strSessionYear,final String strMotionType,
			final String strStatus,final String strWorkflowSubType,final String assignee,
			final String strItemsCount,final String strLocale,final String file) throws ELSException {
		return getWorkflowDetailsRepository().findAll(strHouseType,
				strSessionType,strSessionYear,strMotionType,
				strStatus,strWorkflowSubType,assignee,
				strItemsCount,strLocale,file);
	}
	
	public static List<WorkflowDetails> findPendingWorkflowOfCurrentUser(final Map<String, String> parameters, 
			final String orderBy, 
			final String sortOrder){
		return getWorkflowDetailsRepository().findPendingWorkflowOfCurrentUser(parameters, orderBy, sortOrder);
	}
	//kept to hide errors only method needs to be replaced with actual code
	public static WorkflowDetails findCurrentWorkflowDetail(Device device, String houseTypeName){
		return null;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFile() {
		return file;
	}

	/**
	 * @return the departmentAnswer
	 */
	public String getDepartmentAnswer() {
		return departmentAnswer;
	}

	/**
	 * @param departmentAnswer the departmentAnswer to set
	 */
	public void setDepartmentAnswer(String departmentAnswer) {
		this.departmentAnswer = departmentAnswer;
	}

	/**
	 * @return the sendBackBefore
	 */
	public Date getSendBackBefore() {
		return sendBackBefore;
	}

	/**
	 * @param sendBackBefore the sendBackBefore to set
	 */
	public void setSendBackBefore(Date sendBackBefore) {
		this.sendBackBefore = sendBackBefore;
	}

	/**
	 * @return the previousWorkflowDetail
	 */
	public Long getPreviousWorkflowDetail() {
		return previousWorkflowDetail;
	}

	/**
	 * @param previousWorkflowDetail the previousWorkflowDetail to set
	 */
	public void setPreviousWorkflowDetail(Long previousWorkflowDetail) {
		this.previousWorkflowDetail = previousWorkflowDetail;
	}

	public String getDomainIds() {
		return domainIds;
	}

	public void setDomainIds(final String domainIds) {
		this.domainIds = domainIds;
	}

	public String getNextWorkflowActorId() {
		return nextWorkflowActorId;
	}

	public void setNextWorkflowActorId(String nextWorkflowActorId) {
		this.nextWorkflowActorId = nextWorkflowActorId;
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(
			final UserGroup userGroup, 
			final String domainIds,
			final String workflowType,
			final String status,
			final String locale) {
		return getWorkflowDetailsRepository().findCurrentWorkflowDetail(
				userGroup, domainIds, workflowType, status, locale);
	}

	public String getModule() {
		return module;
	}

	public void setModule(final String module) {
		this.module = module;
	}
}