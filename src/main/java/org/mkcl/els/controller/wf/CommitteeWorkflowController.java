package org.mkcl.els.controller.wf;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.CommitteeCompositeVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * NOTES:
 * 1. The name of the workflow is mapped to workflowType in WorkflowDetails.
 * 
 * 2. The subtype of the workflow is mapped to workflowSubType in 
 * 	  WorkflowDetails. 
 */
@Controller
@RequestMapping("/workflow/committee")
public class CommitteeWorkflowController extends BaseController {

	@Autowired
	IProcessService processService;
	
	/**
	 * The initial GET request for "REQUEST TO PARLIAMENTARY AFFAIRS MINISTER 
	 * FOR ADDITION OF MEMBERS TO COMMITTEE" workflow will land here.
	 */
	@RequestMapping(value="init/requestToParliamentaryMinister", 
			method=RequestMethod.GET)
	public String initRequestToParliamentaryAffairsMinister(
			final ModelMap model, 
			final Locale localeObj) {
		String locale = localeObj.toString();
		String wfName = 
			ApplicationConstants.COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER;
		return this.commonInitRequestToPAMAndLOP(model, wfName, locale);
	}
	
	/**
	 * The initial GET request for "REQUEST TO LEADER OF OPPOSITION FOR 
	 * ADDITION OF MEMBERS TO COMMITTEE" workflow will land here.
	 */
	@RequestMapping(value="init/requestToLeaderOfOpposition", 
			method=RequestMethod.GET)
	public String initRequestToLeaderOfOpposition(final ModelMap model, 
			final Locale localeObj) {
		String locale = localeObj.toString();
		String wfName = 
			ApplicationConstants.COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION;
		return this.commonInitRequestToPAMAndLOP(model, wfName, locale);
	}
	
	/**
	 * The intermediate (workflow) GET request for "REQUEST TO PARLIAMENTARY 
	 * AFFAIRS MINISTER FOR ADDITION OF MEMBERS TO COMMITTEE" AND 
	 * "REQUEST TO LEADER OF OPPOSITION FOR ADDITION OF MEMBERS TO 
	 * COMMITTEE" workflows, will land here.
	 */
	@RequestMapping(value="memberAddition", method=RequestMethod.GET)
	public String intermediateMemberAdditionRequest(final ModelMap model,
			final HttpServletRequest request,
			final Locale localeObj) {
		String locale = localeObj.toString();
		WorkflowDetails wfDetails = 
			(WorkflowDetails) request.getAttribute("workflowdetails");
		
		// STEP 1: Populate Committee Composite VO
		UserGroup userGroup = this.getUserGroup(wfDetails);
		List<Committee> committees = this.getCommittees(wfDetails);		
		this.populateCommitteeCompositeVO(model, userGroup, committees, locale);
		
		// STEP 2: Populate Statuses & Status
		String wfName = wfDetails.getWorkflowType();
		Status status = this.getStatus(wfDetails);
		this.populateStatuses(model, userGroup, wfName, locale);
		this.populateStatus(model, status);
		
		// STEP 3: Populate Actors & Actor
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		List<WorkflowActor> wfActors = 
			this.populateNextActors(model, userGroup, status, wfName, 
					assigneeLevel, locale);
		WorkflowActor wfActor = wfActors.get(0);
		this.populateNextActor(model, wfActor);
			
		// STEP 4: Populate Workflow attributes
		this.populateWorkflowAttributes(model, wfName, false);
		
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/committee/memberAddition/" + ugtType;
	}
	
	/**
	 * All the PUT requests (initial or intermediate) for "REQUEST TO 
	 * PARLIAMENTARY AFFAIRS MINISTER FOR ADDITION OF MEMBERS TO COMMITTEE"
	 * AND "REQUEST TO LEADER OF OPPOSITION FOR ADDITION OF MEMBERS TO 
	 * COMMITTEE" workflows will land here.
	 * 
	 * STEPS:
	 * 1. Save the incoming information.
	 * 
	 * 2. If this is workflow initiation request (workflowInit == true),
	 * 	  start member addition process
	 *
	 * 3. Else proceed member addition process
	 * 	
	 */
	@RequestMapping(value="memberAddition", method=RequestMethod.PUT)
	public String processMemberAdditionRequest(
			final HttpServletRequest request,
			final Locale localeObj) {
		String locale = localeObj.toString();
		
		this.memberAdditionRequestSaveInformation(request);
		
		WorkflowDetails wfDetails = null;
		String wfInit = this.getWorkflowInit(request);
		if(wfInit != null && wfInit.equals("true")) {
			wfDetails =	this.startMemberAdditionProcess(request, locale);
		}
		else {
			wfDetails = this.proceedMemberAdditionProcess(request, locale);
		}
		
		String returnURL = "redirect:memberAddition/processed/" 
			+ wfDetails.getId();;
		return returnURL;
	}
	
	/**
	 * Once the PUT request is processed for "REQUEST TO PARLIAMENTARY 
	 * AFFAIRS MINISTER FOR ADDITION OF MEMBERS TO COMMITTEE" AND 
	 * "REQUEST TO LEADER OF OPPOSITION FOR ADDITION OF MEMBERS TO 
	 * COMMITTEE" workflows, this is the client redirect URL.
	 */
	@RequestMapping(value="memberAddition/processed/{workflowDetailsId}", 
			method=RequestMethod.GET)
	public String rendererForMemberAdditionRequest(final ModelMap model,
			@PathVariable("workflowDetailsId") final Long workflowDetailsId,
			final Locale localeObj) {
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, workflowDetailsId);
		String wfName = wfDetails.getWorkflowType();
		String locale = localeObj.toString();
		
		// STEP 1: Populate Committee Composite VO
		UserGroup userGroup = this.getUserGroup(wfDetails);
		
		String strCommitteeIds = wfDetails.getDomainIds();
		Long[] committeeIds = this.convertToLongArray(strCommitteeIds, ",");
		List<Committee> committees = this.getCommittees(committeeIds);
		
		this.populateCommitteeCompositeVO(model, userGroup, committees, locale);
		
		// STEP 2: Populate Status
		Status status = this.getStatus(wfDetails);
		this.populateStatus(model, status);
		
		// STEP 3: Populate Actor
		WorkflowActor wfActor = this.getNextActor(wfDetails);
		this.populateNextActor(model, wfActor);
		
		// STEP 4: Populate Remarks
		String remarks = this.getRemarks(wfDetails);
		this.populateRemarks(model, remarks);
		
		// STEP 4: Populate Workflow attributes
		this.populateWorkflowAttributes(model, wfName);
		
		// STEP 5: Render as Read Only. Since the task is completed,
		// 		   the User must not be allowed to perform any modifications.
		this.renderAsReadOnly(model);
		
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/committee/memberAddition/" + ugtType;
	}

	//=============== INTERNAL METHODS ================
	/**
	 * Common functionality to initialize request to
	 * Parliamentary Affairs Minister & Leader Of Opposition.
	 */
	private String commonInitRequestToPAMAndLOP(final ModelMap model,
			final String workflowName,
			final String locale) {
		UserGroup userGroup = this.getWorkflowInitiator(workflowName, locale);

		if(userGroup != null) {
			// STEP 1: Populate CommitteeCompositeVO
			List<Committee> committees = 
				Committee.findCommitteesToBeProcessed(locale);
			this.populateCommitteeCompositeVO(model, userGroup, 
					committees, locale);
			
			// STEP 2: Populate Statuses & Status
			List<Status> statuses = 
				this.populateStatuses(model, userGroup, workflowName, locale);
			Status status = statuses.get(0);
			this.populateStatus(model, status);
			
			// STEP 3: Populate Actors & Actor
			List<WorkflowActor> wfActors = 
				this.populateNextActors(model, userGroup, status, workflowName, 
						ApplicationConstants.WORKFLOW_START_LEVEL, locale);			 
			WorkflowActor wfActor = wfActors.get(0);
			this.populateNextActor(model, wfActor);
			
			// STEP 4: Populate Workflow attributes
			this.populateWorkflowAttributes(model, workflowName, true);			
		}
		else {
			this.error(model, 
					"current_user_is_not_authorized_to_initiate_workflow");
		}
		
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/committee/memberAddition/" + ugtType;
	}
	
	private void memberAdditionRequestSaveInformation(
			final HttpServletRequest request) {
		List<Committee> committees = this.getCommittees(request);
		Status status = this.getStatus(request);
		String remarks = this.getRemarks(request);
		
		// Set the statuses & remarks as per the workflowName
		String wfName = this.getWorkflowName(request);
		if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
			for(Committee c : committees) {
				c.setInternalStatusPAMWf(status);
				c.setRemarksPAMWf(remarks);
				c.merge();
			}
		}
		else if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			for(Committee c : committees) {
				c.setInternalStatusLOPWf(status);
				c.setRemarksLOPWf(remarks);
				c.merge();
			}
		}
	}
	
	/**
	 * STEPS:
	 * 1. Create WorkflowDetails for self
	 * 2. Start Workflow
	 * 3. Create WorkflowDetails for next (selected) actor
	 */
	private WorkflowDetails startMemberAdditionProcess(
			final HttpServletRequest request,
			final String locale) {
		String wfName = this.getWorkflowName(request);
		UserGroup userGroup = this.getWorkflowInitiator(wfName, locale);
		Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
		
		// As this user is the initiator of the Workflow, his entry
		// is added in the WorkflowDetails.NOT SURE WHETHER THIS ENTRY 
		// SHOULD BE MADE BUT STILL MAKING IT		
		WorkflowDetails wfDetails = this.createInitWorkflowDetails(
				request, userGroup, wfName, assigneeLevel, locale);
		
		Task task = this.startInitProcess(request, userGroup, wfName, 
				assigneeLevel, locale);
		
		this.createNextActorWorkflowDetails(request, task, userGroup, 
				wfName, assigneeLevel, locale);
		
		return wfDetails;
	}
	
	/**
	 * STEPS:
	 * 1. Update current actor's WorkflowDetails
	 * 2. Proceed the Workflow for the next (selected) actor by completing 
	 * 	  current actor's task & setting the next (selected) actors parameters.
	 * 3. Create WorkflowDetails for next (selected) actor 
	 */
	private WorkflowDetails proceedMemberAdditionProcess(
			final HttpServletRequest request,
			final String locale) {
		// STEP 1: Get current actors' WorkflowDetails
		String wfName = this.getWorkflowName(request);
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		UserGroup userGroup = this.getUserGroup(userGroups, wfName, locale);
		
		Long[] committeeIds = this.getCommitteeIds(request);
		String strCommitteeIds = this.convertToString(committeeIds, ",");
		
		WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(
				userGroup, strCommitteeIds, 
				ApplicationConstants.MYTASK_PENDING, locale);
		
		// STEP 2: Update current actors' WorkflowDetails.
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		WorkflowActor nextWFActor = 
			this.getNextActor(request, userGroup, assigneeLevel, locale);
		this.updateWorkflowDetails(wfDetails, nextWFActor);
		
		// STEP 3: Proceed the Workflow.
		String taskId = wfDetails.getTaskId();
		Task newTask = this.proceedProcess(nextWFActor, taskId, wfName, locale);

		// STEP 4: If there happens to be a nextActor, create WorkflowDetails
		// for him by passing the above acquired task.
		if(newTask != null) {
			this.createNextActorWorkflowDetails(request, newTask, 
					userGroup, wfName, assigneeLevel, locale);
		}
		
		return wfDetails;
	}
	
	private Task startInitProcess(final HttpServletRequest request,
			final UserGroup userGroup,
			final String workflowName,
			final Integer assigneeLevel,
			final String locale) {
		String key = ApplicationConstants.APPROVAL_WORKFLOW; 
		ProcessDefinition definition = 
			processService.findProcessDefinitionByKey(key);
		Map<String, String> properties = new HashMap<String, String>();
		
		WorkflowActor wfActor = 
			this.getNextActor(request, userGroup, assigneeLevel, locale);
		if(wfActor != null) {
			User user = this.getUser(wfActor, workflowName, locale);
			properties.put("pv_user", user.getCredential().getUsername());
			properties.put("pv_endflag", "continue");
		}
		else {
			properties.put("pv_user", "");
			properties.put("pv_endflag", "end");
		}
		
		ProcessInstance process = 
			processService.createProcessInstance(definition, properties);
		Task task = processService.getCurrentTask(process);
		
		return task;
	}
	
	/**
	 * @param nextWorkflowActor could be null
	 * 
	 * Returns null if the task with id @param currentTaskId is the
	 * last task
	 */
	private Task proceedProcess(final WorkflowActor nextWorkflowActor,
			final String currentTaskId,
			final String workflowName,
			final String locale) {
		Task currentTask = processService.findTaskById(currentTaskId);
		Map<String, String> properties = new HashMap<String, String>();
		
		if(nextWorkflowActor != null) {			
			User user = this.getUser(nextWorkflowActor, workflowName, locale);
			properties.put("pv_user", user.getCredential().getUsername());
			properties.put("pv_endflag", "continue");
		}
		else {
			properties.put("pv_user", "");
			properties.put("pv_endflag", "end");
		}
		
		processService.completeTask(currentTask, properties);
		
		ProcessInstance process = processService.findProcessInstanceById(
				currentTask.getProcessInstanceId());
		Task newTask = processService.getCurrentTask(process);
		return newTask;
	}
	
	private WorkflowDetails createInitWorkflowDetails(
			final HttpServletRequest request,
			final UserGroup userGroup,
			final String workflowName,
			final Integer assigneeLevel,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		// Not applicable parameters: processId, taskId
		String wfSubType =  this.getWorkflowSubType(workflowName);
		Date completionTime = new Date();
		
		wfDetails.setWorkflowType(workflowName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setCompletionTime(completionTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
		
		// User parameters
		String assignee = this.getCurrentUser().getUsername();
		wfDetails.setAssignee(assignee);
		
		UserGroupType ugt = userGroup.getUserGroupType();
		wfDetails.setAssigneeUserGroupType(ugt.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(userGroup.getId()));
		wfDetails.setAssigneeUserGroupName(ugt.getName());
		wfDetails.setAssigneeLevel(String.valueOf(assigneeLevel));
		
		WorkflowActor wfActor = 
			this.getNextActor(request, userGroup, assigneeLevel, locale);
		User user = this.getUser(wfActor, workflowName, locale);
		wfDetails.setNextAssignee(user.getCredential().getUsername());
		wfDetails.setNextWorkflowActorId(String.valueOf(wfActor.getId()));
		
		// Domain parameters
		// Not applicable parameters: deviceId, deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		HouseType houseType = this.getHouseType(userGroup, locale);
		Long[] committeeIds = this.getCommitteeIds(request);
		String strCommitteeIds = this.convertToString(committeeIds, ","); 
		
		wfDetails.setHouseType(houseType.getName());
		wfDetails.setDomainIds(strCommitteeIds);
		
		// Misc parameters
		// TODO Not set as yet: urlPattern, form
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	private WorkflowDetails createNextActorWorkflowDetails(
			final HttpServletRequest request,
			final Task task,
			final UserGroup currentActorUserGroup,
			final String workflowName,
			final Integer currentActorLevel,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
	
		// Workflow parameters
		String wfSubType =  this.getWorkflowSubType(workflowName);
		Date assignmentTime = new Date();
		
		wfDetails.setProcessId(task.getProcessInstanceId());
		wfDetails.setTaskId(task.getId());
		wfDetails.setWorkflowType(workflowName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
		
		// User parameters
		// Not applicable parameters: nextWorkflowActorId
		wfDetails.setAssignee(task.getAssignee());
		
		WorkflowActor nextActor = this.getNextActor(request, 
				currentActorUserGroup, currentActorLevel, locale);
		UserGroup nextUserGroup = 
			this.getUserGroup(nextActor, workflowName, locale);
		UserGroupType nextUGT = nextUserGroup.getUserGroupType();
		wfDetails.setAssigneeUserGroupType(nextUGT.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(nextUserGroup.getId()));
		wfDetails.setAssigneeUserGroupName(nextUGT.getName());
		wfDetails.setAssigneeLevel(String.valueOf(nextActor.getLevel()));
		
		// Domain parameters
		// Not applicable parameters: deviceId, deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		HouseType houseType = this.getHouseType(nextUserGroup, locale);
		Long[] committeeIds = this.getCommitteeIds(request);
		String strCommitteeIds = this.convertToString(committeeIds, ","); 
		
		wfDetails.setHouseType(houseType.getName());
		wfDetails.setDomainIds(strCommitteeIds);
		
		// Misc parameters
		// TODO Not set as yet: urlPattern, form
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	/**
	 * @param nextWorkflowActor could be null
	 */
	private void updateWorkflowDetails(final WorkflowDetails workflowDetails,
			final WorkflowActor nextWorkflowActor) {
		Date completionTime = new Date();
		workflowDetails.setCompletionTime(completionTime);
		workflowDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
		
		if(nextWorkflowActor != null) {
			String wfActorId = String.valueOf(nextWorkflowActor.getId());
			workflowDetails.setNextWorkflowActorId(wfActorId);
		}
		workflowDetails.merge();
	}
		
	//=============== "GET" METHODS ===================
	@SuppressWarnings("unchecked")
	private Long[] getCommitteeIds(final HttpServletRequest request) {
		List<Long> committeeIds = new ArrayList<Long>();
		
		Enumeration<String> keys = request.getParameterNames();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.startsWith("committeeId")) {
				Long committeeId = Long.valueOf(request.getParameter(key));
				committeeIds.add(committeeId);
			}
		}
		
		return committeeIds.toArray(new Long[]{});
	}
	
	private Long[] getCommitteeIds(final WorkflowDetails workflowDetails) {
		String strCommitteeIds = workflowDetails.getDomainIds();
		Long[] committeeIds = this.convertToLongArray(strCommitteeIds, ",");
		return committeeIds;
	}
	
	private Committee getFirstCommittee(final WorkflowDetails workflowDetails) {
		Long[] committeeIds = this.getCommitteeIds(workflowDetails);
		Long committeeId = committeeIds[0];
		Committee committee = Committee.findById(Committee.class, committeeId);
		return committee;
	}
	
	private List<Committee> getCommittees(final HttpServletRequest request) {
		Long[] committeeIds = this.getCommitteeIds(request);
		List<Committee> committees = this.getCommittees(committeeIds);
		return committees;
	}
	
	private List<Committee> getCommittees(
			final WorkflowDetails workflowDetails) {
		Long[] committeeIds = this.getCommitteeIds(workflowDetails);
		List<Committee> committees = this.getCommittees(committeeIds);
		return committees;
	}
	
	private List<Committee> getCommittees(final Long[] committeeIds) {
		List<Committee> committees = new ArrayList<Committee>();
		
		for(Long cid : committeeIds) {
			Committee committee = Committee.findById(Committee.class, cid);
			committees.add(committee);
		}
		
		return committees;
	}
	
	private Status getStatus(final HttpServletRequest request) {
		String strStatusId = request.getParameter("status");
		Long statusId = Long.valueOf(strStatusId);
		Status status = Status.findById(Status.class, statusId); 
		return status;
	}
	
	private Status getStatus(final WorkflowDetails workflowDetails) {
		Committee committee = this.getFirstCommittee(workflowDetails);		
		String wfName = workflowDetails.getWorkflowType();
		
		Status status = null;
		if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
			status = committee.getInternalStatusPAMWf();
		}
		else if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			status = committee.getInternalStatusLOPWf();
		}
		
		return status;
	}
	
	/**
	 * If this user's usergrouptype is in {list of usergrouptypes 
	 * for whom next authority is not shown} then query WorkflowConfig
	 * to find next actor (that too back actor if status is SEND_BACK).
	 * 
	 * Else retrieve nextActor from request object. This is the nextActor 
	 * as selected by this user.
	 */
	private WorkflowActor getNextActor(final HttpServletRequest request,
			final UserGroup userGroup,
			final Integer assigneeLevel,
			final String locale) {
		String name = ApplicationConstants.
			COMMITTEE_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW;
		String value = this.getCustomParameterValue(name, locale);
		
		String[] ugtTypes = this.tokenize(value, ","); 
		String ugtType = userGroup.getUserGroupType().getType();
		for(String ugtt : ugtTypes) {
			if(ugtt.equals(ugtType)) {
				HouseType houseType = this.getHouseType(userGroup, locale);
				Status status = this.getStatus(request);
				String wfName = this.getWorkflowName(request);
				
				WorkflowActor wfActor = WorkflowConfig.findNextCommitteeActor(
						houseType, userGroup, status, 
						wfName, assigneeLevel, locale);
				return wfActor;
			}
		}
		
		Long wfActorId = Long.valueOf(request.getParameter("actor"));
		WorkflowActor wfActor = 
			WorkflowActor.findById(WorkflowActor.class, wfActorId);
		return wfActor;
	}
	
	/**
	 * USE THIS METHOD WITH LOTS OF CARE. This method must be used only
	 * after @param workflowDetails is updated. If used after 
	 * @param workflowDetails is created, then it will always return null
	 * because next actor is only available after the current actor
	 * has completed his task.
	 */
	private WorkflowActor getNextActor(final WorkflowDetails workflowDetails) {
		String strWFActorId = workflowDetails.getNextWorkflowActorId();
		if(strWFActorId != null) {
			Long wfActorId = Long.valueOf(strWFActorId);
			WorkflowActor wfActor = 
				WorkflowActor.findById(WorkflowActor.class, wfActorId);
			return wfActor;
		}
		return null;
	}
	
	private String getRemarks(final HttpServletRequest request) {
		return request.getParameter("remarks");
	}
	
	private String getRemarks(final WorkflowDetails workflowDetails) {
		Committee committee = this.getFirstCommittee(workflowDetails);		
		String wfName = workflowDetails.getWorkflowType();
		
		String remarks = null;
		if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
			remarks = committee.getRemarksPAMWf();
		}
		else if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			remarks = committee.getRemarksLOPWf();
		}
		
		return remarks;
	}
	
	private String getWorkflowInit(final HttpServletRequest request) {
		return request.getParameter("workflowInit");
	}
	
	private String getWorkflowName(final HttpServletRequest request) {
		return request.getParameter("workflowName");
	}
	
	private String getWorkflowSubType(final String workflowName) {
		String wfSubType = "";
		
		if(workflowName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
			wfSubType = "PARLIAMENTARY_MINISTER";
		}
		else if(workflowName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			wfSubType = "LEADER_OF_OPPOSITION";
		}
		
		return wfSubType;
	}
	
	/**
	 * If the current user is configured as the initiator of the
	 * work flow named @param workflowName then return the current user's 
	 * UserGroup.
	 * 
	 * Else return null.
	 */
	private UserGroup getWorkflowInitiator(final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase(); 
		String name = "COMMITTEE_" + wfName + "_INITIATOR";
		String ugtType = this.getCustomParameterValue(name, locale);
		
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		UserGroup userGroup = this.getUserGroup(userGroups, ugtType);
		return userGroup;
	}
	
	private UserGroup getUserGroup(final List<UserGroup> userGroups,
			final String workflowName,
			final String locale) {
		String[] ugtTypes =	this.getUserGroupTypeTypes(workflowName, locale);
		return this.getUserGroup(userGroups, ugtTypes);
	}
	
	private UserGroup getUserGroup(final WorkflowDetails workflowDetails) {
		String strUserGroupId = workflowDetails.getAssigneeUserGroupId();
		Long userGroupId = Long.valueOf(strUserGroupId);
		UserGroup userGroup = UserGroup.findById(UserGroup.class, userGroupId);
		return userGroup;
	}
	
	private UserGroup getUserGroup(final WorkflowActor workflowActor,
			final String workflowName,
			final String locale) {
		List<UserGroup> userGroups = this.getUserGroups(workflowActor, locale);
		String[] ugtTypes =	this.getUserGroupTypeTypes(workflowName, locale);
		return this.getUserGroup(userGroups, ugtTypes);
	}
	
	private UserGroup getUserGroup(final List<UserGroup> userGroups,
			final String[] userGroupTypeTypes) {
		for(String ugtt : userGroupTypeTypes) {
			UserGroup userGroup = this.getUserGroup(userGroups, ugtt);
			if(userGroup != null) {
				return userGroup;
			}
		}
		
		return null;
	}
	
	private UserGroup getUserGroup(final List<UserGroup> userGroups,
			final String userGroupTypeType) {
		for(UserGroup ug : userGroups) {
			String ugtType = ug.getUserGroupType().getType();
			if(ugtType.equals(userGroupTypeType)) {
				// Returning ug is the right thing to do. But it throws the
				// following exception:
				// org.hibernate.LazyInitializationException: failed to lazily
				// initialize a collection of role: 
				// org.mkcl.els.domain.UserGroup.parameters, no session or 
				// session was closed
				
				// return ug;
				
				// As a way around following piece of code is added
				UserGroup userGroup = 
					UserGroup.findById(UserGroup.class, ug.getId());
				return userGroup;
			}
		}
		
		return null;
	}
	
	private List<UserGroup> getUserGroups(final WorkflowActor workflowActor,
			final String locale) {
		UserGroupType userGroupType = workflowActor.getUserGroupType();
		
		List<UserGroup> userGroups = 
			UserGroup.findAllByFieldName(UserGroup.class, "userGroupType", 
					userGroupType, "activeFrom", ApplicationConstants.DESC, 
					locale);
		return userGroups;
	}
	
	/**
	 * The usergrouptypes allowed for a workflow are configured as key-value
	 * pairs in CustomParameter. The template for key formation is:
	 * 		"COMMITTEE_" + toUpperCase(WORKFLOWNAME) + "_ALLOWED_USERGROUPTYPES"
	 * 
	 * If the aforementioned key-value pair is not configured for this workflow,
	 * then return the default configured usergrouptypes. The template is:
	 * 		"COMMITTEE_ALLOWED_USERGROUPTYPES"
	 */
	private String[] getUserGroupTypeTypes(final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase();
		String name = "COMMITTEE_" + wfName + "_ALLOWED_USERGROUPTYPES";
		String value = this.getCustomParameterValue(name, locale);
		if(value == null) {
			name = "COMMITTEE_ALLOWED_USERGROUPTYPES";
			value = this.getCustomParameterValue(name, locale);
		}
		String[] ugtTypes = this.tokenize(value, ",");
		return ugtTypes;
	}
	
	private User getUser(final WorkflowActor workflowActor,
			final String workflowName,
			final String locale) {
		List<UserGroup> userGroups = this.getUserGroups(workflowActor, locale);		
		String[] ugtTypes = this.getUserGroupTypeTypes(workflowName, locale);
		
		UserGroup userGroup = this.getUserGroup(userGroups, ugtTypes);
		if(userGroup != null) {
			Credential credential = userGroup.getCredential();
			User user = User.findByFieldName(User.class, 
					"credential", credential, locale);
			return user;
		}

		return null;
	}
	
	private HouseType getHouseType(final UserGroup userGroup,
			final String locale) {
		String strHouseType = userGroup.getParameterValue("HOUSETYPE_" + locale);
		if(strHouseType != null) {
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			return houseType;
		}
		
		return null;
	}
	
	private String getCustomParameterValue(final String name,
			final String locale) {
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, name, locale);
		if(parameter != null) {
			return parameter.getValue();
		}
		
		return null;
	}
	
	//=============== "POPULATE" METHODS ==============
	private void populateCommitteeCompositeVO(final ModelMap model,
			final UserGroup userGroup,
			final List<Committee> committees,
			final String locale) {
		HouseType houseType = this.getHouseType(userGroup, locale);
		CommitteeCompositeVO committeeCompositeVO = 
			Committee.findCommitteeVOs(committees, houseType, locale);
		
		model.addAttribute("committeeCompositeVO", committeeCompositeVO);
	}
	
	/**
	 * The put up options are configured as key-value pairs in CustomParameter.
	 * The template for key formation is:
	 * 		"COMMITTEE_PUT_UP_OPTIONS_" + toUpperCase(WORKFLOWNAME)
	 * 		+ "_" + toUpperCase(HOUSETYPE) + "_" 
	 * 		+ "_" + toUpperCase(USERGROUPTYPE)
	 * 
	 * If the aforementioned key-value pair is not configured for this user,
	 * then return the default configured options. The template is:
	 * 		"COMMITTEE_PUT_UP_OPTIONS_" + toUpperCase(WORKFLOWNAME)
	 * 		+ "_" + toUpperCase(HOUSETYPE) + "_" + "DEFAULT"
	 */
	private List<Status> populateStatuses(final ModelMap model,
			final UserGroup userGroup,
			final String workflowName,
			final String locale) {
		// Prepare the parameters
		String wfName = workflowName.toUpperCase();
		
		HouseType houseType = this.getHouseType(userGroup, locale);
		String houseTypeType = houseType.getType().toUpperCase();
		
		String ugtType = userGroup.getUserGroupType().getType().toUpperCase();
		
		// Retrieve statuses as comma separated string
		String name = "COMMITTEE_PUT_UP_OPTIONS_" + wfName + "_" 
			+ houseTypeType + "_" + ugtType;
		String options = this.getCustomParameterValue(name, locale); 
		if(options == null) {
			name = "COMMITTEE_PUT_UP_OPTIONS_" + wfName + "_" 
				+ houseTypeType + "_DEFAULT";
			options = this.getCustomParameterValue(name, locale);
		}
		
		// Retrieve & Populate Statuses
		List<Status> statuses = new ArrayList<Status>();
		try {
			statuses = Status.findStatusContainedIn(options, locale);
		} 
		catch (ELSException e) {
			logger.error(e.getMessage());
		}
		model.addAttribute("statuses", statuses);
		
		return statuses;
	}
	
	private void populateStatus(final ModelMap model,
			final Status status) {
		model.addAttribute("status", status);
	}
	
	/**
	 * Pass @param level as ApplicationConstants.WORKFLOW_START_LEVEL
	 * in case of init request, else pass the appropriate level.
	 */
	private List<WorkflowActor> populateNextActors(final ModelMap model,
			final UserGroup userGroup,
			final Status status,
			final String workflowName,
			final int assigneeLevel,
			final String locale) {
		List<Reference> actors = new ArrayList<Reference>();
		
		String wfName = workflowName + "_workflow";
		HouseType houseType = this.getHouseType(userGroup, locale);
		List<WorkflowActor> wfActors = WorkflowConfig.findCommitteeActors(
				houseType, userGroup, status, wfName, assigneeLevel, locale);
		for(WorkflowActor wfa : wfActors) {
			String id = String.valueOf(wfa.getId());
			String name = wfa.getUserGroupType().getName();
			Reference actor = new Reference(id, name);
			actors.add(actor);
		}
		model.addAttribute("actors", actors);
		
		return wfActors;
	}
	
	private void populateNextActor(final ModelMap model,
			final WorkflowActor workflowActor) {
		String id = String.valueOf(workflowActor.getId());
		String name = workflowActor.getUserGroupType().getName();
		Reference actor = new Reference(id, name);
		model.addAttribute("actor", actor);
	}
	
	private void populateRemarks(final ModelMap model,
			final String remarks) {
		model.addAttribute("remarks", remarks);
	}
	
	private void populateWorkflowAttributes(final ModelMap model,
			final String workflowName,
			final Boolean isWorkflowInit) {
		model.addAttribute("workflowInit", isWorkflowInit);
		this.populateWorkflowAttributes(model, workflowName);
	}
	
	private void populateWorkflowAttributes(final ModelMap model,
			final String workflowName) {
		model.addAttribute("workflowName", workflowName);
		model.addAttribute("workflowSubType", 
				this.getWorkflowSubType(workflowName));
	}
	
	private void error(final ModelMap model, 
			final String errorcode) {
		model.addAttribute("errorcode", errorcode);
	}
	
	private void renderAsReadOnly(ModelMap model) {
		model.addAttribute("renderAsReadOnly", true);
	}
	
	//=============== UTILITY METHODS =================
	private Long[] convertToLongArray(final String str,
			final String delimiter) {
		List<Long> ids = new ArrayList<Long>();
		
		String[] tokens = this.tokenize(str, delimiter);
		for(String s : tokens) {
			Long id = Long.valueOf(s);
			ids.add(id);
		}
		
		return ids.toArray(new Long[]{});
	}
	
	private String convertToString(final Long[] ids, 
			final String delimiter) {
		StringBuffer sb = new StringBuffer();
		
		int length = ids.length;
		for(int i = 0; i < length; i++) {
			sb.append(ids[i]);
			if(i < length - 1) {
				sb.append(delimiter);
			}
		}
		
		return sb.toString();
	}
	
	private String[] tokenize(final String str,
			final String token) {
		String[] tokens = str.split(token);
		
		int length = tokens.length;
		for(int i = 0; i < length; i++) {
			tokens[i] = tokens[i].trim();
		}

		return tokens;
	}
}