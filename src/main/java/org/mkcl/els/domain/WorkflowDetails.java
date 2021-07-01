package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.DepartmentDashboardVo;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.repository.WorkflowDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name="workflow_details")
public class WorkflowDetails extends BaseDomain implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String processId;
	
	private String taskId;	
	
	private String assigner;
	
	@Column(length=100)
	private String assignee;
	
	private String assignerUserGroupType;
	
	private String assigneeUserGroupType;
	
	private String assignerUserGroupId;
	
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
	@Column(length=60)
	private String deviceId;
	
	@Column(length=100)
	private String assignerDraftId;
	
	@Column(length=100)
	private String assigneeDraftId;
	
	@Column(length=150)
	private String deviceType;
	
	@Column(length=100)
	private String deviceNumber;
	
	@Column(length=1000)
	private String deviceOwner;
	
	@Column(length=1000)
	private String internalStatus;
	
	@Column(length=1000)
	private String recommendationStatus;
	
	@Column(length=1000)
	private String customStatus;
	
	@Column(length=150)
	private String houseType;
	
	@Column(length=150)
	private String sessionType;
	
	@Column(length=30)
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
	
	/**** For print requisition & send for endorsement & transmit press copies workflow ****/
	@Column(length=100)
	private String printRequisitionId;	
	
	private String houseRound;
	
	private String isHardCopyReceived;
	
	private String dateOfHardCopyReceived;
	
	/**** acknowledgement decision ****/
	private String acknowledgementDecision;
		
	private String departmentAnswer;
	
	private Long previousWorkflowDetail;
	
	private Integer numericalDevice;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendBackBefore;
	
	/**** Attributes for bill amendment motion ****/
	@Column(length=1000)
	private String amendedBillInfo;
	
	@Column(length=1000)
	private String defaultAmendedSectionNumberInfo;
	
	/**** Attributes for adjournment motion ****/
	@Temporal(TemporalType.DATE)
	private Date adjourningDate;
	
	@Transient
	private String formattedAdjourningDate;
	
	/**** Attributes for Non Device Types viz, Committee. Comma separated Ids ****/
	@Column(length=1000)
	private String domainIds;
		
	private String nextWorkflowActorId;
	
	@Column(length=1000)
	private String module;
	
	/****Chart Answering Dates****/
	private Date answeringDate;
	
	/****Decision Status****/
	private String decisionInternalStatus;
	
	private String decisionRecommendStatus;
	
	@Transient
	private String decisionStatusForMyTaskGrid;
	
	/****Ministry****/
	@Column(length = 900)
	private String ministry;
	
	/****Subdepartment*****/
	@Column(length = 900)
	private String subdepartment;
	
	/**** Replies of Devices ****/
	@Column(length=30000)
	private String reply;
	
	/**** Reference Number *****/
	private String referenceNumber;
	
	/**** Referred Number ****/
	private String referredNumber;
	
	
	
	@Autowired
    private transient WorkflowDetailsRepository workflowDetailsRepository;
	
	private static WorkflowDetailsRepository getRepository() {
		WorkflowDetailsRepository workflowDetailsRepository = new WorkflowDetails().workflowDetailsRepository;
        if (workflowDetailsRepository == null) {
            throw new IllegalStateException(
                    "WorkflowDetailsrepository has not been injected in WorkflowDetails Domain");
        }
        return workflowDetailsRepository;
    }
	
	// Creation methods
	public static WorkflowDetails create(final Question question, 
			final Task task,
			final String workflowType,
			final String assigneeLevel) throws ELSException {
		return getRepository().create(question, task, workflowType, assigneeLevel);
	}
	
	public static WorkflowDetails create(final Question question, 
			final Task newtask,
			final UserGroupType usergroupType, 
			final String currentDeviceTypeWorkflowType,
			final String level) throws ELSException {
		return getRepository().create(question, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
	}
	
	public static List<WorkflowDetails> create(final Question question,
			final List<Task> tasks,
			final String workflowType,
			final String assigneeLevel) throws ELSException {
		return getRepository().create(question, tasks, workflowType, assigneeLevel);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final Resolution resolution,
			final Task task,
			final String workflowType,
			final String assigneeLevel, 
			final HouseType houseTypeForWorkflow) throws ELSException {
		return getRepository().create(resolution, task, workflowType, assigneeLevel, houseTypeForWorkflow);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.BILL_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	/**** Bill Related @param customStatus TODO****/
	public static WorkflowDetails create(final Bill bill,
			final Task task,
			final String workflowType,
			final String customStatus, 
			final String userGroupType, 
			final String assigneeLevel) {
		return getRepository().create(bill, task, workflowType, customStatus, userGroupType, assigneeLevel);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.BILL_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final Bill bill,
			final HouseType houseType,
			final Boolean isActorAcrossHouse,
			final PrintRequisition printRequisition,
			final Task task,
			final String workflowType,
			final String userGroupType, 
			final String assigneeLevel) {
		return getRepository().create(bill, houseType, isActorAcrossHouse, printRequisition, task, workflowType, userGroupType, assigneeLevel);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.BILL_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static List<WorkflowDetails> create(final Bill bill,
			final List<Task> tasks,
			final String workflowType,
			final String customStatus, 
			final String assigneeLevel) {
		return getRepository().create(bill, tasks, workflowType, customStatus, assigneeLevel);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.BILL_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final BillAmendmentMotion bill,
			final Task task,
			final String workflowType,
			final String customStatus, 
			final String userGroupType, 
			final String assigneeLevel) {
		return getRepository().create(bill, task, workflowType, customStatus, userGroupType, assigneeLevel);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.BILL_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static List<WorkflowDetails> create(final BillAmendmentMotion billAmendmentMotion,
			final List<Task> tasks,
			final String workflowType,
			final String customStatus, 
			final String assigneeLevel) {
		return getRepository().create(billAmendmentMotion, tasks, workflowType, customStatus, assigneeLevel);
	}
	
	public static WorkflowDetails create(final BillAmendmentMotion billAmendmentMotion, 
			 final Task newtask,
			 final UserGroupType usergroupType, 
			 final String currentDeviceTypeWorkflowType,
			 final String level) 
			 throws ELSException {
		return getRepository().create(billAmendmentMotion, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
	}
	
	public static WorkflowDetails create(final AdjournmentMotion adjournmentMotion, 
										 final Task newtask,
										 final UserGroupType usergroupType, 
										 final String currentDeviceTypeWorkflowType,
										 final String level) 
										 throws ELSException {
		return getRepository().create(adjournmentMotion, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
	}
	
	public static List<WorkflowDetails> create(final AdjournmentMotion motion,
											   final List<Task> tasks,
											   final String workflowType,
											   final String assigneeLevel) 
											   throws ELSException, ParseException {
		return getRepository().create(motion, tasks, workflowType, assigneeLevel);
	}
	
	public static WorkflowDetails create(final ProprietyPoint proprietyPoint, 
				 final Task newtask,
				 final UserGroupType usergroupType, 
				 final String currentDeviceTypeWorkflowType,
				 final String level) 
				 throws ELSException {
	return getRepository().create(proprietyPoint, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
	}
	
	public static List<WorkflowDetails> create(final ProprietyPoint proprietyPoint,
					   final List<Task> tasks,
					   final String workflowType,
					   final String assigneeLevel) 
					   throws ELSException, ParseException {
	return getRepository().create(proprietyPoint, tasks, workflowType, assigneeLevel);
	}
	
	public static WorkflowDetails create(final SpecialMentionNotice specialMentionNotice, 
			 final Task newtask,
			 final UserGroupType usergroupType, 
			 final String currentDeviceTypeWorkflowType,
			 final String level) 
			 throws ELSException {
	return getRepository().create(specialMentionNotice, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
	}
	
	public static List<WorkflowDetails> create(final SpecialMentionNotice motion,
					   final List<Task> tasks,
					   final String workflowType,
					   final String assigneeLevel) 
					   throws ELSException, ParseException {
	return getRepository().create(motion, tasks, workflowType, assigneeLevel);
	}
	
	// Start Process
	public static WorkflowDetails startProcess(final Question question, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(question, processDefinitionKey, processWorkflow, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final Question question, 
			final String processDefinitionKey, 
			final Status status, 
			final String usergroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(question, processDefinitionKey, status, usergroupType, level, locale);
	}

	public static WorkflowDetails startProcessAtGivenLevel(final Question question, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(question, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenAssignee(final Question question, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String assignee,
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenAssignee(question, processDefinitionKey, processWorkflow, userGroupType, level, assignee, locale);
	}
			
	//Complete task 
	public static WorkflowDetails completeTask(final Question question) throws ELSException {
		return getRepository().completeTask(question);
	}
		
	public static Long findRevisedQuestionTextWorkflowCount(Question question, Status resendRevisedQuestionTextStatus, WorkflowDetails wfDetails) {
		return getRepository().findRevisedQuestionTextWorkflowCount(question, resendRevisedQuestionTextStatus, wfDetails);
		
	}
	// End Process
//	public void endProcess() {
//		getRepository().endProcess(this);
//	}
	
	public static void endProcess(final WorkflowDetails wf) {
		endProcess(wf, "COMPLETED");
	}
	
	public static void endProcess(final WorkflowDetails wf, final String wfStatus) {
		if(wf!=null && wf.getId()!=null) {
			try {
				getRepository().endProcess(wf, wfStatus);
			} catch(Exception e) {
				// Update WorkflowDetails
				wf.setStatus(wfStatus);
				wf.setCompletionTime(new Date());
				wf.merge();
			}			
		}		
	}
	
	// Retrieval methods
	@SuppressWarnings("rawtypes")
	public static List findCompleteness(final Session session,
			final HouseType houseType,
			final String deviceId,
			final String locale){
		return getRepository().findCompleteness(session, houseType, deviceId, locale);
	}
	
	public static Integer findIfWorkflowExists(final Session session,
			final HouseType houseType,
			final String deviceId,
			final String workflowSubTypeInitial,
			final String locale){
		return getRepository().findIfWorkflowExists(session, houseType, deviceId, workflowSubTypeInitial, locale);				
	}
	
	public static List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strMotionType,
			final String strStatus,
			final String strWorkflowSubType,
			final String assignee,
			final String strItemsCount,
			final String strLocale) throws ELSException {
		return getRepository().findAll(strHouseType, strSessionType, strSessionYear, strMotionType,
				strStatus, strWorkflowSubType, assignee, strItemsCount, strLocale);
	}
	
	public static List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strMotionType,
			final String strStatus,
			final String strWorkflowSubType,
			final String assignee,
			final String strItemsCount,
			final String strLocale,
			final String file) throws ELSException {
		return getRepository().findAll(strHouseType, strSessionType, strSessionYear, strMotionType,
				strStatus, strWorkflowSubType, assignee, strItemsCount, strLocale, file);
	}
	
	public static List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strMotionType,
			final String strStatus,
			final String strWorkflowSubType,
			final String strSubDepartment,
			final String assignee,
			final String strItemsCount,
			final String strLocale,
			final String file) throws ELSException {
		return getRepository().findAll(strHouseType, strSessionType, strSessionYear, strMotionType,
				strStatus, strWorkflowSubType, strSubDepartment, assignee, strItemsCount, strLocale, file);
	}
	
	public static List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strMotionType,
			final String strStatus,
			final String strWorkflowSubType,
			final String assignee,
			final String strItemsCount,
			final String strLocale,
			final String file,
			final String group,
			final Date answeringDate) throws ELSException {
		return getRepository().findAll(strHouseType, strSessionType, strSessionYear, strMotionType,
				strStatus, strWorkflowSubType, assignee, strItemsCount, strLocale, file, group, answeringDate);
	}
	
	public static List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strMotionType,
			final String strStatus,
			final String strWorkflowSubType,
			final String assignee,
			final String strItemsCount,
			final String strLocale,
			final String file,
			final String group,
			final String subdepartment,
			final Date answeringDate) throws ELSException {
		return getRepository().findAll(strHouseType, strSessionType, strSessionYear, strMotionType,
				strStatus, strWorkflowSubType, assignee, strItemsCount, strLocale, file, group, subdepartment, answeringDate);
	}
	
	public static List<WorkflowDetails> findAllForAdjournmentMotions(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strMotionType,
			final String strStatus,
			final String strWorkflowSubType,
			final Date adjourningDate,
			final String assignee,
			final String strItemsCount,
			final String strLocale) throws ELSException {
		return getRepository().findAllForAdjournmentMotions(strHouseType, strSessionType, strSessionYear, strMotionType,
				strStatus, strWorkflowSubType, adjourningDate, assignee, strItemsCount, strLocale);
	}
	
	public static List<WorkflowDetails> findAllForSpecialMentionNotices(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strMotionType,
			final String strStatus,
			final String strWorkflowSubType,
			final Date specialmentionNoticeDate,
			final String assignee,
			final String strItemsCount,
			final String strLocale) throws ELSException {
		return getRepository().findAllForSpecialMentionNotices(strHouseType, strSessionType, strSessionYear, strMotionType,
				strStatus, strWorkflowSubType, specialmentionNoticeDate, assignee, strItemsCount, strLocale);
	}
	
	public static List<WorkflowDetails> findAllForProprietyPoints(final String strHouseType,
			final String strSessionType,
			final String strSessionYear,
			final String strDeviceType,
			final String strStatus,
			final String strWorkflowSubType,
			final Date proprietyPointDate,
			final String assignee,
			final String strItemsCount,
			final String strLocale) throws ELSException {
		return getRepository().findAllForProprietyPoints(strHouseType, strSessionType, strSessionYear, strDeviceType,
				strStatus, strWorkflowSubType, proprietyPointDate, assignee, strItemsCount, strLocale);
	}
	
	public static List<WorkflowDetails> findPendingWorkflowOfCurrentUser(final Map<String, String> parameters, 
			final String orderBy,
			final String sortOrder) {
		return getRepository().findPendingWorkflowOfCurrentUser(parameters, orderBy, sortOrder);
	}
	
	public static List<WorkflowDetails> findPendingWorkflowOfCurrentUserByAssignmentTimeRange(final Map<String, String> parameters,
			final Date toDate,
			final Date fromDate,
			final String orderBy, 
			final String sortOrder) {
		return getRepository().findPendingWorkflowOfCurrentUserByAssignmentTimeRange(parameters, toDate, fromDate, orderBy, sortOrder);
	}
	
	public static WorkflowDetails find(final Map<String, Object[]> fieldValuePair, 
			final String locale) {
		return getRepository().find(fieldValuePair, locale);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final UserGroup userGroup, 
			final String domainIds,
			final String workflowType,
			final String status,
			final String locale) {
		return getRepository().findCurrentWorkflowDetail(userGroup, domainIds, workflowType, status, locale);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final UserGroup userGroup, 
			final String deviceId,
			final String domainIds,
			final String workflowType,
			final String status,
			final String locale) {
		return getRepository().findCurrentWorkflowDetail(userGroup, deviceId, domainIds, workflowType, status, locale);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Device device, 
			final DeviceType deviceType, 
			final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(device, deviceType, workflowType);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Question question) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(question);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Question question, 
			final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(question, workflowType);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Resolution resolution) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(resolution);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Resolution resolution, 
			final String workflowHouseType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(resolution, workflowHouseType);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Bill bill, 
			final String workflowType) {
		return getRepository().findCurrentWorkflowDetail(bill, workflowType);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final BillAmendmentMotion billAmendmentMotion, 
			final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(billAmendmentMotion, workflowType);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final Resolution resolution, 
			final String workflowHouseType, 
			final String processDefinitionKey, 
			final Status status, 
			final String usergroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(resolution, workflowHouseType, processDefinitionKey, status, usergroupType, level, locale);
	}
	
	public static List<WorkflowDetails> findAllSupplementaryWorkflow(String strHouseType, String strSessionType,
			String strSessionYear, String strQuestionType, String strStatus, String strWorkflowSubType,
			String assignee, String strItemsCount, String strLocale) {
		return getRepository().findAllSupplementaryWorkflow(strHouseType, strSessionType, strSessionYear, strQuestionType, strStatus, strWorkflowSubType, assignee, strItemsCount, strLocale);
	}

	public static List<WorkflowDetails> findPendingWorkflowDetails(final Question question, 
			final String workflowType) throws ELSException {
		return getRepository().findPendingWorkflowDetails(question, workflowType);
	}
	
	public static List<WorkflowDetails> findPendingWorkflowDetails(final Resolution resolution, 
			final String workflowType) throws ELSException {
		return getRepository().findPendingWorkflowDetails(resolution, workflowType);
	}

	/********************Motion*********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final Motion motion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final Motion motion, final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion, workflowType);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final CutMotionDate cutMotionDate) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(cutMotionDate);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final Motion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level,
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(motion, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final Motion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level,
			final String referenceNumber,
			final String referredNumber,
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(motion, processDefinitionKey, processWorkflow, userGroupType, level, referenceNumber, referredNumber, locale);
	}
	
	public static WorkflowDetails startProcess(final Motion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(motion, processDefinitionKey, processWorkflow, locale);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.MOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static List<WorkflowDetails> create(final Motion domain, 
			final List<Task> tasks,
			final String supportingMemberWorkflow, 
			final String assigneeLevel) throws ELSException {		
		return getRepository().create(domain, tasks, supportingMemberWorkflow, assigneeLevel);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.MOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final Motion domain,
			final Task task,
			final String workflowType,
			final String level) throws ELSException {
		return getRepository().create(domain, task, workflowType, level);
	}
	
	public static WorkflowDetails create(final Motion motion, 
			final Task newtask,
			final UserGroupType usergroupType, 
			final String currentDeviceTypeWorkflowType,
			final String level) throws ELSException {
		return getRepository().create(motion, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
	}
	
	public static WorkflowDetails create(Motion motion, Task newtask, UserGroupType usergroupType,
			String currentDeviceTypeWorkflowType, String level, String referenceNumber, String referredNumber) throws ELSException {
		return getRepository().create(motion, newtask, usergroupType, currentDeviceTypeWorkflowType, level, referenceNumber, referredNumber);
	}
	/********************Motion*********************/	
	
	/********************StandaloneMotion*********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final StandaloneMotion motion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final StandaloneMotion motion, 
			final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion, workflowType);
	}
	
	public static WorkflowDetails startProcess(final StandaloneMotion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(motion, processDefinitionKey, processWorkflow, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final StandaloneMotion motion, 
			final String processDefinitionKey, 
			final Status status, 
			final String usergroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(motion, processDefinitionKey, status, usergroupType, level, locale);
	}

	public static WorkflowDetails startProcessAtGivenLevel(final StandaloneMotion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(motion, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.STANDALONEMOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final StandaloneMotion motion,
			final Task task,
			final String workflowType,
			final String assigneeLevel) throws ELSException {
		return getRepository().create(motion, task, workflowType, assigneeLevel);
	}
		
	public static List<WorkflowDetails> create(final StandaloneMotion motion,
			final List<Task> tasks,
			final String workflowType,
			final String assigneeLevel) throws ELSException {
		return getRepository().create(motion, tasks, workflowType, assigneeLevel);
	}
	public static WorkflowDetails create(final StandaloneMotion motion, 
			final Task newtask,
			final UserGroupType usergroupType, 
			final String currentDeviceTypeWorkflowType,
			final String level) throws ELSException {
		return getRepository().create(motion, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
	}
	/**********************************StandaloneMotion*****************/
	
	/**********************************CutMotion***********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final CutMotion motion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final CutMotion motion, final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion, workflowType);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final CutMotion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(motion, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final DiscussionMotion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(motion, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	public static WorkflowDetails startProcess(final CutMotion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(motion, processDefinitionKey, processWorkflow, locale);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.CUTMOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static List<WorkflowDetails> create(final CutMotion domain,
			final List<Task> tasks,
			final String supportingMemberWorkflow, 
			final String assigneeLevel) throws ELSException {		
		return getRepository().create(domain, tasks, supportingMemberWorkflow, assigneeLevel);
	}

	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.CUTMOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final CutMotion motion,
			final Task task,
			final UserGroupType usergroupType,
			final String workflowType,
			final String level) throws ELSException {
		return getRepository().create(motion, task, usergroupType, workflowType, level);
	}
	
	
	public static WorkflowDetails create(final CutMotion motion,
			final Task task,
			final String workflowType,
			final String level) throws ELSException {
		return getRepository().create(motion, task, workflowType, level);
	}
	/******************************CutMotion**************************/
	
	/******************************EventMotion***********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final EventMotion motion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final EventMotion motion, final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion, workflowType);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final EventMotion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(motion, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	public static WorkflowDetails startProcess(final EventMotion motion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(motion, processDefinitionKey, processWorkflow, locale);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.CUTMOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static List<WorkflowDetails> create(final EventMotion domain,
			final List<Task> tasks,
			final String supportingMemberWorkflow, 
			final String assigneeLevel) throws ELSException {		
		return getRepository().create(domain, tasks, supportingMemberWorkflow, assigneeLevel);
	}

	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.CUTMOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final EventMotion motion,
			final Task task,
			final UserGroupType usergroupType,
			final String workflowType,
			final String level) throws ELSException {
		return getRepository().create(motion, task, usergroupType, workflowType, level);
	}
	
	
	public static WorkflowDetails create(final EventMotion motion,
			final Task task,
			final String workflowType,
			final String level) throws ELSException {
		return getRepository().create(motion, task, workflowType, level);
	}
	/******************************EventMotion***********************/
	
	/******************************DiscussionMotion***********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final DiscussionMotion motion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final DiscussionMotion motion, final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(motion, workflowType);
	}
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.DISCUSSIONMOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static WorkflowDetails create(final DiscussionMotion domain, 
			final Task newtask,
			final String approvalWorkflow, 
			final String level) throws ELSException {
		return getRepository().create(domain, newtask, approvalWorkflow, level);
	}
	
	/*
	 * TODO: Open Call hierarchy on this method and wherever workflowType is passed as 
	 * ApplicationConstants.DISCUSSIONMOTION_APPROVAL_WORKFLOW, change it to use Workflow.getType().
	 * You can use Workflow.findByStatus(Status status, String locale) to get the workflow.
	 */
	public static List<WorkflowDetails> create(final DiscussionMotion domain,
			final List<Task> tasks, 
			final String supportingMemberWorkflow,
			final String assigneeLevel2) throws ELSException {
		return getRepository().create(domain, tasks, supportingMemberWorkflow,assigneeLevel2);
	}
	
	public static WorkflowDetails create(final DiscussionMotion motion,
			final Task task,
			final UserGroupType usergroupType,
			final String workflowType,
			final String level) throws ELSException {
		return getRepository().create(motion, task, usergroupType, workflowType, level);
	}
	/******************************DiscussionMotion***********************/
	
	/********************Adjournment Motion*********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final AdjournmentMotion adjournmentMotion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(adjournmentMotion);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final AdjournmentMotion adjournmentMotion, 
			final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(adjournmentMotion, workflowType);
	}
	
	public static WorkflowDetails startProcess(final AdjournmentMotion adjournmentMotion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(adjournmentMotion, processDefinitionKey, processWorkflow, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final AdjournmentMotion adjournmentMotion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(adjournmentMotion, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	/********************Special Mention Notice*********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final SpecialMentionNotice specialMentionNotice) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(specialMentionNotice);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final SpecialMentionNotice specialMentionNotice, 
			final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(specialMentionNotice, workflowType);
	}
	
	public static WorkflowDetails startProcess(final SpecialMentionNotice specialMentionNotice, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(specialMentionNotice, processDefinitionKey, processWorkflow, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final SpecialMentionNotice specialMentionNotice, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(specialMentionNotice, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	/****************************** BillAmendment Motion ****************************/
	public static WorkflowDetails findCurrentWorkflowDetail(final BillAmendmentMotion billAmendmentMotion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(billAmendmentMotion);
	}
	
	public static WorkflowDetails startProcess(final BillAmendmentMotion billAmendmentMotion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(billAmendmentMotion, processDefinitionKey, processWorkflow, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final BillAmendmentMotion billAmendmentMotion, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(billAmendmentMotion, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	/********************Propriety Point*********************/
	public static WorkflowDetails findCurrentWorkflowDetail(final ProprietyPoint proprietyPoint) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(proprietyPoint);
	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(final ProprietyPoint proprietyPoint, 
			final String workflowType) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(proprietyPoint, workflowType);
	}
	
	public static WorkflowDetails startProcess(final ProprietyPoint proprietyPoint, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final String locale) throws ELSException {
		return getRepository().startProcess(proprietyPoint, processDefinitionKey, processWorkflow, locale);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(final ProprietyPoint proprietyPoint, 
			final String processDefinitionKey, 
			final Workflow processWorkflow, 
			final UserGroupType userGroupType, 
			final int level, 
			final String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(proprietyPoint, processDefinitionKey, processWorkflow, userGroupType, level, locale);
	}
	
	// TODO: Incomplete
	// kept to hide errors only method needs to be replaced with actual code
	public static WorkflowDetails findCurrentWorkflowDetail(final Device device, final String houseTypeName){
		return null;
	}
	
	//DepartmentDashboard 
	public static List<DepartmentDashboardVo> findDepartmentDeviceCountFromWorkflowDetails(String strSessionType, String strSessionYear,
			String strHouseType,String strDeviceType,String strSubdepartment,String strLocale){
			return getRepository().findDepartmentDeviceCountFromWorkflowDetails(strSessionType,strSessionYear,strHouseType,strDeviceType,strSubdepartment,strLocale );
	}
	
	public static List<DepartmentDashboardVo> findDepartmentDeviceCountsByHouseTypeFromWorkflowDetails(String strSessionType, String strSessionYear,
			String strHouseType,String strDeviceType,String strSubdepartment,String strStatus,String strLocale){
				return getRepository().findDepartmentDeviceCountsByHouseTypeFromWorkflowDetails(strSessionType,strSessionYear,strHouseType,strDeviceType,strSubdepartment,strStatus,strLocale );
	}
	
	public static List<DepartmentDashboardVo> findDepartmentAssemblyDeviceCountsByDeviceTypeFromWorkflowDetails(String strHouseType, String strSessionType,  String strSessionYear, String strDeviceType, String strSubdeartment, String strStatus, String strLocale){
		return getRepository().findDepartmentAssemblyDeviceCountsByDeviceTypeFromWorkflowDetails(strHouseType, strSessionType, strSessionYear, strDeviceType, strSubdeartment, strStatus, strLocale);
	}
	
	public static Long findRevisedMotionTextWorkflowCount(Motion motion, List<String> resendStatus,
			WorkflowDetails workflowDetails) {
		return getRepository().findRevisedMotionTextWorkflowCount(motion, resendStatus, workflowDetails);
	}
	
	public static List<WorkflowDetails> findPendingWorkflowDetails(Motion motion, String workflowType) throws ELSException {
		return getRepository().findPendingWorkflowDetails(motion, workflowType);
	}
	
	public static WorkflowDetails startProcessAtGivenLevel(RulesSuspensionMotion rulesSuspensionMotion, String approvalWorkflow,
			Workflow workflow, UserGroupType userGroupType, Integer level, String locale) throws ELSException {
		return getRepository().startProcessAtGivenLevel(rulesSuspensionMotion, approvalWorkflow, workflow, userGroupType, level, locale);

	}
	
	public static WorkflowDetails findCurrentWorkflowDetail(RulesSuspensionMotion rulesSuspensionMotion) throws ELSException {
		return getRepository().findCurrentWorkflowDetail(rulesSuspensionMotion);
	}
	
	public static WorkflowDetails create(RulesSuspensionMotion rulesSuspensionMotion, Task task,
			UserGroupType usergroupType, String workflowType, String assigneelevel) throws ELSException {
		return getRepository().create(rulesSuspensionMotion, task, usergroupType, workflowType, assigneelevel);
	}
	
	public static List<WorkflowDetails> create(RulesSuspensionMotion domain, List<Task> tasks,
			String supportingMemberWorkflow, String assigneeLevel) throws ParseException, ELSException {
		return getRepository().create(domain, tasks, supportingMemberWorkflow, assigneeLevel);
	}
	
	public static List<WorkflowDetails> findAllForRulesSuspensionMotions(String strHouseType, String strSessionType,
			String strSessionYear, String strMotionType, String mytaskPending, String strWorkflowSubType,
			Date ruleSuspensionDate, String assignee, String strItemsCount, String strLocale) throws ELSException {
		return getRepository().findAllForRulesSuspensionMotions(strHouseType, strSessionType, strSessionYear, strMotionType,
				mytaskPending, strWorkflowSubType, ruleSuspensionDate, assignee, strItemsCount, strLocale);
	}
	
	public static WorkflowDetails startProcess(RulesSuspensionMotion domain, String approvalWorkflow,
			Workflow admitDueToReverseClubbingWorkflow, String locale) throws ELSException {
		return getRepository().startProcess(domain, approvalWorkflow, admitDueToReverseClubbingWorkflow, locale);
		
	}
	
	public static WorkflowDetails findByDeviceAssignee(Motion motion, WorkflowDetails workflowDetails, String strUserGroupType, String locale) {
		return getRepository().findByDeviceAssignee(motion, workflowDetails, strUserGroupType, locale);
	}
	// Getters and Setters
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(final String processId) {
		this.processId = processId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(final String taskId) {
		this.taskId = taskId;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(final String assignee) {
		this.assignee = assignee;
	}	
	
	public void setStatus(final String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setCompletionTime(final Date completionTime) {
		this.completionTime = completionTime;
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setWorkflowType(final String workflowType) {
		this.workflowType = workflowType;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public void setDeviceId(final String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceType(final String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceNumber(final String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public Integer getNumericalDevice() {
		return numericalDevice;
	}

	public void setNumericalDevice(final Integer numericalDevice) {
		this.numericalDevice = numericalDevice;
	}

	public void setDeviceOwner(final String deviceOwner) {
		this.deviceOwner = deviceOwner;
	}

	public String getDeviceOwner() {
		return deviceOwner;
	}

	public void setInternalStatus(final String internalStatus) {
		this.internalStatus = internalStatus;
	}

	public String getInternalStatus() {
		return internalStatus;
	}

	public void setRecommendationStatus(final String recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public String getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setHouseType(final String houseType) {
		this.houseType = houseType;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setSessionType(final String sessionType) {
		this.sessionType = sessionType;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionYear(final String sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getSessionYear() {
		return sessionYear;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setUrlPattern(final String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setForm(final String form) {
		this.form = form;
	}

	public String getForm() {
		return form;
	}

	public void setAssigneeUserGroupName(final String assigneeUserGroupName) {
		this.assigneeUserGroupName = assigneeUserGroupName;
	}

	public String getAssigneeUserGroupName() {
		return assigneeUserGroupName;
	}

	public void setAssigneeLevel(final String assigneeLevel) {
		this.assigneeLevel = assigneeLevel;
	}

	public String getAssigneeLevel() {
		return assigneeLevel;
	}

	public void setAssignmentTime(final Date assignmentTime) {
		this.assignmentTime = assignmentTime;
	}

	public Date getAssignmentTime() {
		return assignmentTime;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setWorkflowSubType(final String workflowSubType) {
		this.workflowSubType = workflowSubType;
	}

	public String getWorkflowSubType() {
		return workflowSubType;
	}

	public void setAssigneeUserGroupId(final String assigneeUserGroupId) {
		this.assigneeUserGroupId = assigneeUserGroupId;
	}

	public String getAssigneeUserGroupId() {
		return assigneeUserGroupId;
	}

	public void setAssigneeUserGroupType(final String assigneeUserGroupType) {
		this.assigneeUserGroupType = assigneeUserGroupType;
	}

	public String getAssigneeUserGroupType() {
		return assigneeUserGroupType;
	}	

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(final String groupNumber) {
		this.groupNumber = groupNumber;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setFile(final String file) {
		this.file = file;
	}

	public String getFile() {
		return file;
	}

	public String getDepartmentAnswer() {
		return departmentAnswer;
	}

	public void setDepartmentAnswer(final String departmentAnswer) {
		this.departmentAnswer = departmentAnswer;
	}

	public Date getSendBackBefore() {
		return sendBackBefore;
	}

	public void setSendBackBefore(final Date sendBackBefore) {
		this.sendBackBefore = sendBackBefore;
	}

	public Long getPreviousWorkflowDetail() {
		return previousWorkflowDetail;
	}

	public void setPreviousWorkflowDetail(final Long previousWorkflowDetail) {
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

	public void setNextWorkflowActorId(final String nextWorkflowActorId) {
		this.nextWorkflowActorId = nextWorkflowActorId;
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(final String module) {
		this.module = module;
	}

	public String getAssigner() {
		return assigner;
	}

	public void setAssigner(final String assigner) {
		this.assigner = assigner;
	}

	public String getAssignerUserGroupType() {
		return assignerUserGroupType;
	}

	public void setAssignerUserGroupType(final String assignerUserGroupType) {
		this.assignerUserGroupType = assignerUserGroupType;
	}

	public String getAssignerUserGroupId() {
		return assignerUserGroupId;
	}

	public void setAssignerUserGroupId(final String assignerUserGroupId) {
		this.assignerUserGroupId = assignerUserGroupId;
	}

	public String getAssignerDraftId() {
		return assignerDraftId;
	}

	public void setAssignerDraftId(final String assignerDraftId) {
		this.assignerDraftId = assignerDraftId;
	}

	public String getAssigneeDraftId() {
		return assigneeDraftId;
	}

	public void setAssigneeDraftId(final String assigneeDraftId) {
		this.assigneeDraftId = assigneeDraftId;
	}

	public String getCustomStatus() {
		return customStatus;
	}

	public void setCustomStatus(final String customStatus) {
		this.customStatus = customStatus;
	}

	public String getPrintRequisitionId() {
		return printRequisitionId;
	}

	public void setPrintRequisitionId(final String printRequisitionId) {
		this.printRequisitionId = printRequisitionId;
	}

	public String getHouseRound() {
		return houseRound;
	}

	public void setHouseRound(final String houseRound) {
		this.houseRound = houseRound;
	}

	public String getIsHardCopyReceived() {
		return isHardCopyReceived;
	}

	public void setIsHardCopyReceived(final String isHardCopyReceived) {
		this.isHardCopyReceived = isHardCopyReceived;
	}

	public String getDateOfHardCopyReceived() {
		return dateOfHardCopyReceived;
	}

	public void setDateOfHardCopyReceived(final String dateOfHardCopyReceived) {
		this.dateOfHardCopyReceived = dateOfHardCopyReceived;
	}

	public String getAcknowledgementDecision() {
		return acknowledgementDecision;
	}

	public void setAcknowledgementDecision(final String acknowledgementDecision) {
		this.acknowledgementDecision = acknowledgementDecision;
	}

	public String getAmendedBillInfo() {
		return amendedBillInfo;
	}

	public void setAmendedBillInfo(final String amendedBillInfo) {
		this.amendedBillInfo = amendedBillInfo;
	}

	public String getDefaultAmendedSectionNumberInfo() {
		return defaultAmendedSectionNumberInfo;
	}

	public void setDefaultAmendedSectionNumberInfo(
			final String defaultAmendedSectionNumberInfo) {
		this.defaultAmendedSectionNumberInfo = defaultAmendedSectionNumberInfo;
	}

	/**
	 * @return the adjourningDate
	 */
	public Date getAdjourningDate() {
		return adjourningDate;
	}

	/**
	 * @param adjourningDate the adjourningDate to set
	 */
	public void setAdjourningDate(Date adjourningDate) {
		this.adjourningDate = adjourningDate;
	}

	/**
	 * @return the formattedAdjourningDate
	 */
	public String getFormattedAdjourningDate() {
		if(this.adjourningDate!=null) {
			try {
				formattedAdjourningDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(this.adjourningDate, "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", this.getLocale());
			} catch (ELSException e) {
				formattedAdjourningDate = "";
			}
		} else {
			formattedAdjourningDate = "";
		}
		return formattedAdjourningDate;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(final Date chartAnsweringDate) {
		this.answeringDate = chartAnsweringDate;
	}

	public String getDecisionInternalStatus() {
		return decisionInternalStatus;
	}

	public void setDecisionInternalStatus(final String decisionInternalStatus) {
		this.decisionInternalStatus = decisionInternalStatus;
	}

	public String getDecisionRecommendStatus() {
		return decisionRecommendStatus;
	}

	public void setDecisionRecommendStatus(final String decisionRecommendStatus) {
		this.decisionRecommendStatus = decisionRecommendStatus;
	}

	public String getDecisionStatusForMyTaskGrid() {
		if(this.getStatus()!=null && this.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
			decisionStatusForMyTaskGrid = recommendationStatus;
		} else {
			decisionStatusForMyTaskGrid = decisionRecommendStatus;
		}
		return decisionStatusForMyTaskGrid;
	}

	public String getMinistry() {
		return ministry;
	}

	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}

	public String getSubdepartment() {
		return subdepartment;
	}

	public void setSubdepartment(String subdepartment) {
		this.subdepartment = subdepartment;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getReferredNumber() {
		return referredNumber;
	}

	public void setReferredNumber(String referredNumber) {
		this.referredNumber = referredNumber;
	}
		
}