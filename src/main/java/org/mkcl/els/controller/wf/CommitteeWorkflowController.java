package org.mkcl.els.controller.wf;

import java.util.ArrayList;
import java.util.Collections;
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
import org.mkcl.els.domain.CommitteeDesignation;
import org.mkcl.els.domain.CommitteeMember;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.PartyType;
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
		Long wfDetailsId = (Long) request.getAttribute("workflowdetails");
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, wfDetailsId); 
		
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
		Boolean isHideNextActors = 
			this.isThisActorExcludedFromSelectingNextActorInWorkflow(userGroup, 
					locale);
		if(isHideNextActors) {
			this.hideNextActors(model);
		}
		else {
			Integer assigneeLevel = 
				Integer.valueOf(wfDetails.getAssigneeLevel());
			
			List<WorkflowActor> wfActors = this.populateNextActors(model, 
					userGroup, status, assigneeLevel, locale);
			
			WorkflowActor wfActor = wfActors.get(0);
			this.populateNextActor(model, wfActor);
		}
			
		// STEP 4: Populate Workflow attributes
		this.populateWorkflowAttributes(model, wfName, false);
		
		// STEP 5: Populate PartyType
		PartyType partyType = this.getPartyType(wfName, locale);
		this.populatePartyType(model, partyType);
		
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		return urlPattern + "/" + ugtType;
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
//		String locale = localeObj.toString();
//		
//		this.memberAdditionRequestSaveInformation(request, locale);
//		
//		WorkflowDetails wfDetails = null;
//		String wfInit = this.getWorkflowInit(request);
//		if(wfInit != null && wfInit.equals("true")) {
//			wfDetails =	this.startMemberAdditionProcess(request, locale);
//		}
//		else {
//			wfDetails = this.proceedMemberAdditionProcess(request, locale);
//		}
//		
//		String returnURL = "redirect:memberAddition/processed/" 
//			+ wfDetails.getId();
//		return returnURL;
		
		
		System.out.println("Uncomment the above statements & " +
				"Remove the following");
		this.memberAdditionRequestSaveInformation(request, 
				localeObj.toString());
		return "redirect:memberAddition/processed/5603";
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
		Boolean isHideNextActors = 
			this.isThisActorExcludedFromSelectingNextActorInWorkflow(userGroup, 
					locale);
		if(isHideNextActors) {
			this.hideNextActors(model);
		}
		else {
			WorkflowActor wfActor = this.getNextActor(wfDetails);
			this.populateNextActor(model, wfActor);
		}
		
		// STEP 4: Populate Remarks
		String remarks = this.getRemarks(wfDetails);
		this.populateRemarks(model, remarks);
		
		// STEP 4: Populate Workflow attributes
		this.populateWorkflowAttributes(model, wfName, false);
		
		// STEP 5: Render as Read Only. Since the task is completed,
		// 		   the User must not be allowed to perform any modifications.
		this.renderAsReadOnly(model);
		
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		return urlPattern + "/" + ugtType;
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
			Boolean isHideNextActors = 
				this.isThisActorExcludedFromSelectingNextActorInWorkflow(
						userGroup, locale);
			if(isHideNextActors) {
				this.hideNextActors(model);
			}
			else {
				List<WorkflowActor> wfActors = 
					this.populateNextActors(model, userGroup, status, 
							ApplicationConstants.WORKFLOW_START_LEVEL, locale);
				
				WorkflowActor wfActor = wfActors.get(0);
				this.populateNextActor(model, wfActor);
			}
			
			// STEP 4: Populate Workflow attributes
			this.populateWorkflowAttributes(model, workflowName, true);	
			
			// STEP 5: Populate PartyType
			PartyType partyType = this.getPartyType(workflowName, locale);
			this.populatePartyType(model, partyType);
		}
		else {
			this.error(model, 
					"current_user_is_not_authorized_to_initiate_workflow");
		}
		
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/committee/memberAddition/" + ugtType;
	}
	
	private void memberAdditionRequestSaveInformation(
			final HttpServletRequest request,
			final String locale) {
		List<Committee> committees = this.getCommittees(request);
		Status status = this.getStatus(request);
		String remarks = this.getRemarks(request);
		
		// Set the statuses & remarks as per the workflowName
		String wfName = this.getWorkflowName(status);
		
		if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
			for(Committee c : committees) {
				// Set status
				c.setInternalStatusPAMWf(status);
				c.setRemarksPAMWf(remarks);
				
				// Set members
				this.setCommitteeMembers(c, request, locale);
				
				c.merge();
			}
		}
		else if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			for(Committee c : committees) {
				// Set status
				c.setInternalStatusLOPWf(status);
				c.setRemarksLOPWf(remarks);
				
				// Set members
				this.setCommitteeMembers(c, request, locale);
				
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
		Status status = this.getStatus(request);
		String wfName = this.getWorkflowName(status);
		
		UserGroup userGroup = this.getWorkflowInitiator(wfName, locale);
		Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
		String urlPattern = ApplicationConstants.COMMITTEE_MEMBER_ADDITION_URL;
		
		// As this user is the initiator of the Workflow, his entry
		// is added in the WorkflowDetails.NOT SURE WHETHER THIS ENTRY 
		// SHOULD BE MADE BUT STILL MAKING IT		
		WorkflowDetails wfDetails = this.createInitWorkflowDetails(
				request, userGroup, status, assigneeLevel, urlPattern, locale);
		
		Task task = this.startInitProcess(request, userGroup, wfName, 
				assigneeLevel, locale);
		
		this.createNextActorWorkflowDetails(request, task, userGroup, 
				status, assigneeLevel, urlPattern, locale);
		
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
		Status status = this.getStatus(request);
		String wfName = this.getWorkflowName(status);
		
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
			String urlPattern = 
				ApplicationConstants.COMMITTEE_MEMBER_ADDITION_URL;
			this.createNextActorWorkflowDetails(request, newTask, 
					userGroup, status, assigneeLevel, urlPattern, locale);
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
			final Status status,
			final Integer assigneeLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();

		String wfName = this.getWorkflowName(status);
		String wfSubType =  this.getWorkflowSubType(status);
		Date completionTime = new Date();
		
		wfDetails.setWorkflowType(wfName);
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
		User user = this.getUser(wfActor, wfName, locale);
		wfDetails.setNextAssignee(user.getCredential().getUsername());
		wfDetails.setNextWorkflowActorId(String.valueOf(wfActor.getId()));
		
		// Domain parameters
		// Not applicable parameters: deviceId, deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		HouseType houseType = this.getHouseTypeForWorkflow(userGroup, locale);
		Long[] committeeIds = this.getCommitteeIds(request);
		String strCommitteeIds = this.convertToString(committeeIds, ","); 
		
		wfDetails.setHouseType(houseType.getName());
		wfDetails.setDomainIds(strCommitteeIds);
		
		// Misc parameters
		// TODO Not set as yet: form
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.COMMITTEE);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	private WorkflowDetails createNextActorWorkflowDetails(
			final HttpServletRequest request,
			final Task task,
			final UserGroup currentActorUserGroup,
			final Status status,
			final Integer currentActorLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
	
		// Workflow parameters
		String wfName = this.getWorkflowName(status);
		String wfSubType =  this.getWorkflowSubType(status);
		Date assignmentTime = new Date();
		
		wfDetails.setProcessId(task.getProcessInstanceId());
		wfDetails.setTaskId(task.getId());
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
		
		// User parameters
		// Not applicable parameters: nextWorkflowActorId
		wfDetails.setAssignee(task.getAssignee());
		
		WorkflowActor nextActor = this.getNextActor(request, 
				currentActorUserGroup, currentActorLevel, locale);
		UserGroup nextUserGroup = 
			this.getUserGroup(nextActor, wfName, locale);
		UserGroupType nextUGT = nextUserGroup.getUserGroupType();
		wfDetails.setAssigneeUserGroupType(nextUGT.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(nextUserGroup.getId()));
		wfDetails.setAssigneeUserGroupName(nextUGT.getName());
		wfDetails.setAssigneeLevel(String.valueOf(nextActor.getLevel()));
		
		// Domain parameters
		// Not applicable parameters: deviceId, deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		HouseType houseType = 
			this.getHouseTypeForWorkflow(nextUserGroup, locale);
		Long[] committeeIds = this.getCommitteeIds(request);
		String strCommitteeIds = this.convertToString(committeeIds, ","); 
		
		wfDetails.setHouseType(houseType.getName());
		wfDetails.setDomainIds(strCommitteeIds);
		
		// Misc parameters
		// TODO Not set as yet: form
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.COMMITTEE);
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
	private String get(final HttpServletRequest request,
			final String key) {
		Enumeration<String> keys = request.getParameterNames();
		while(keys.hasMoreElements()) {
			String k = keys.nextElement();
			if(k.equals(key)) {
				return request.getParameter(k);
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private String[] getBeginningWith(final HttpServletRequest request,
			final String key) {
		List<String> values = new ArrayList<String>();
		
		Enumeration<String> keys = request.getParameterNames();
		while(keys.hasMoreElements()) {
			String k = keys.nextElement();
			if(k.startsWith(key)) {
				values.add(request.getParameter(k));
			}
		}
		
		return values.toArray(new String[]{});
	}
	
	private Long[] getCommitteeIds(final HttpServletRequest request) {
		List<Long> committeeIds = new ArrayList<Long>();
		
		String[] values = this.getBeginningWith(request, "committeeId");
		for(String s : values) {
			Long committeeId = Long.valueOf(s);
			committeeIds.add(committeeId);
		}
		Collections.sort(committeeIds);
		
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
		WorkflowActor wfActor = null;
		
		Boolean isExcluded = 
			this.isThisActorExcludedFromSelectingNextActorInWorkflow(
					userGroup, locale);
		if(isExcluded) {
			HouseType houseType = this.getHouseType(userGroup, locale);
			Status status = this.getStatus(request);
			String wfName = this.getWorkflowName(status);
			
			wfActor = WorkflowConfig.findNextCommitteeActor(houseType, 
					userGroup, status, wfName, assigneeLevel, locale);
		}
		else {
			Long wfActorId = Long.valueOf(request.getParameter("actor"));
			wfActor = WorkflowActor.findById(WorkflowActor.class, wfActorId);
		}
		
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
	
	private String getFullWorkflowName(final Status status) {
		String wfName = this.getWorkflowName(status);
		String fullWfName = wfName + "_workflow";
		return fullWfName;
	}
	
	private String getWorkflowName(final Status status) {
		String statusType = status.getType();
		String[] tokens = this.tokenize(statusType, "_");
		int length = tokens.length;
		return tokens[length - 1];
	}
	
	private String getWorkflowSubType(final Status status) {
		return status.getType();
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
		String strHouseType = 
			userGroup.getParameterValue("HOUSETYPE_" + locale);
		if(strHouseType != null) {
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			return houseType;
		}
		
		return null;
	}
	
	/**
	 * If the houseType configured for the @param userGroup is either
	 * LOWERHOUSE or UPPERHOUSE, then return that.
	 * 
	 * Else, iterate the committees & return the first instance of 
	 * LOWERHOUSE or UPPERHOUSE that you come across. If you don't
	 * come across either LOWERHOUSE or UPPERHOUSE, then return
	 * BOTHHOUSE. 
	 */
	private HouseType getHouseType(final UserGroup userGroup,
			final List<Committee> committees,
			final String locale) {
		String BOTH_HOUSE = ApplicationConstants.BOTH_HOUSE;
		
		HouseType houseType = this.getHouseType(userGroup, locale);
		if(houseType != null && ! houseType.getType().equals(BOTH_HOUSE)) {
			 return houseType;
		}
		
		for(Committee c : committees) {
			CommitteeName committeeName = c.getCommitteeName();
			CommitteeType committeeType = committeeName.getCommitteeType();
			houseType = committeeType.getHouseType();
			if(! houseType.getType().equalsIgnoreCase(BOTH_HOUSE)) {
				return houseType;
			}
		}
		
		return houseType;
	}
	
	/**
	 * Note that in WorkflowDetails, the value of houseType is either
	 * LOWERHOUSE or UPPERHOUSE. In case if the houseType is BOTHHOUSE,
	 * then it is stored as LOWERHOUSE.
	 * 
	 * This method facilitates this behavior. If the type of the house
	 * is BOTHHOUSE, then return LOWERHOUSE. Else return the 
	 * configured houseType.
	 */
	private HouseType getHouseTypeForWorkflow(final UserGroup userGroup,
			final String locale) {
		String strHouseType = userGroup.getParameterValue("HOUSETYPE_" + locale);
		if(strHouseType != null) {
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			if(houseType.getType().equals(ApplicationConstants.BOTH_HOUSE)) {
				houseType = HouseType.findByType(
						ApplicationConstants.LOWER_HOUSE, locale);
			}
			return houseType;
		}
		
		return null;
	}
	
	private PartyType getPartyType(final String workflowName,
			final String locale) {
		PartyType partyType = null;
		
		if(workflowName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
			partyType = PartyType.findByType(
					ApplicationConstants.RULING_PARTY, locale);
		}
		else if(workflowName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			partyType = PartyType.findByType(
					ApplicationConstants.OPPOSITION_PARTY, locale);
		}
		
		return partyType;
	}
	
	/**
	 * Some privileged users such as Speaker, Chairman,
	 * Parliamentary Affairs Minister, Leader of Opposition
	 * are not shown the "Next Actors" drop down.
	 * 
	 * This method determines whether the current user is
	 * one of those configured privileged users.
	 */
	private Boolean isThisActorExcludedFromSelectingNextActorInWorkflow(
			final UserGroup userGroup,
			final String locale) {
		String name = ApplicationConstants.
			COMMITTEE_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW;
		String value = this.getCustomParameterValue(name, locale);
		
		String[] ugtTypes = this.tokenize(value, ","); 
		String ugtType = userGroup.getUserGroupType().getType();
		for(String ugtt : ugtTypes) {
			if(ugtt.equals(ugtType)) {
				return true;
			}
		}
		
		return false;
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
	
	//=============== "POPULATE MODEL" METHODS ========
	private void populateCommitteeCompositeVO(final ModelMap model,
			final UserGroup userGroup,
			final List<Committee> committees,
			final String locale) {
		HouseType houseType = this.getHouseType(userGroup, committees, locale);
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
	
	private List<WorkflowActor> populateNextActors(final ModelMap model,
			final UserGroup userGroup,
			final Status status,
			final int assigneeLevel,
			final String locale) {
		List<Reference> actors = new ArrayList<Reference>();
		
		String fullWFName = this.getFullWorkflowName(status);
		HouseType houseType = this.getHouseTypeForWorkflow(userGroup, locale);
		List<WorkflowActor> wfActors = WorkflowConfig.findCommitteeActors(
				houseType, userGroup, status, 
				fullWFName, assigneeLevel, locale);
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
		model.addAttribute("workflowName", workflowName);
		model.addAttribute("workflowInit", isWorkflowInit);
	}
	
	private void populatePartyType(final ModelMap model,
			final PartyType partyType) {
		model.addAttribute("partyType", partyType);
	}
	
	private void error(final ModelMap model, 
			final String errorcode) {
		model.addAttribute("errorcode", errorcode);
	}
	
	private void renderAsReadOnly(ModelMap model) {
		model.addAttribute("renderAsReadOnly", true);
	}
	
	/**
	 * Some privileged users such as Speaker, Chairman,
	 * Parliamentary Affairs Minister, Leader of Opposition
	 * are not shown the "Next Actors" drop down.
	 * 
	 * This method sets the attribute which will be used by
	 * the View to hide the "Next Actors" drop down.
	 */
	private void hideNextActors(final ModelMap model) {
		model.addAttribute("hideNextActors", true);
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
	
	//=============== EXPERIMENTAL ====================
	private Committee setCommitteeMembers(final Committee committee,
			final HttpServletRequest request,
			final String locale) {
		Committee c1 = this.setCommitteeChairman(committee, request, locale);
		Committee c2 = this.setCommitteeCoreMembers(c1, request, locale);
		Committee c3 = this.setCommitteeInvitedMembers(c2, request, locale);
		return c3;
	}
	
	private Committee setCommitteeChairman(final Committee committee,
			final HttpServletRequest request,
			final String locale) {
		Long committeeId = committee.getId();
		String key = "chairman_" + committeeId;
		String value = this.get(request, key);
		
		if(value != null) {
			Long memberId = Long.valueOf(value);
			Member member = Member.findById(Member.class, memberId);
			
			CommitteeDesignation designation = 
				CommitteeDesignation.findByType(
						ApplicationConstants.COMMITTEE_CHAIRMAN, locale);
			
			Date joiningDate = new Date();
			
			CommitteeMember committeeMember = 
				new CommitteeMember(member, designation, joiningDate, locale);
			committee.getMembers().add(committeeMember);
		}

		return committee;
	}
	
	private Committee setCommitteeCoreMembers(final Committee committee,
			final HttpServletRequest request,
			final String locale) {
		Long committeeId = committee.getId();
		String key = "members_" + committeeId;
		String[] values = this.getBeginningWith(request, key);
		
		for(String value : values) {
			Long memberId = Long.valueOf(value);
			Member member = Member.findById(Member.class, memberId);
			
			CommitteeDesignation designation = 
				CommitteeDesignation.findByType(
						ApplicationConstants.COMMITTEE_MEMBER, locale);
			
			Date joiningDate = new Date();
			
			CommitteeMember committeeMember = 
				new CommitteeMember(member, designation, joiningDate, locale);
			committee.getMembers().add(committeeMember);
		}
			
		return committee;
	}
	
	private Committee setCommitteeInvitedMembers(final Committee committee,
			final HttpServletRequest request,
			final String locale) {
		Long committeeId = committee.getId();
		String key = "invitedMembers_" + committeeId;
		String[] values = this.getBeginningWith(request, key);
		
		for(String value : values) {
			Long memberId = Long.valueOf(value);
			Member member = Member.findById(Member.class, memberId);
			
			CommitteeDesignation designation = 
				CommitteeDesignation.findByType(
						ApplicationConstants.COMMITTEE_INVITED_MEMBER, locale);
			
			Date joiningDate = new Date();
			
			CommitteeMember committeeMember = 
				new CommitteeMember(member, designation, joiningDate, locale);
			committee.getInvitedMembers().add(committeeMember);
		}
			
		return committee;
	}
}