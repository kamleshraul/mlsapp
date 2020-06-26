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
import org.mkcl.els.common.vo.AuthUser;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workflow/committee")
public class CommitteeWorkflowController extends BaseController {
	
	@Autowired
	private IProcessService processService;
	
	//===========================================================
	//	Request to Department of Parliamentary Affairs Minister
	//  and Leader of Opposition for adding members to the 
	//  committees.
	//===========================================================
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
		Long wfDetailsId = (Long) request.getAttribute("workflowdetails");
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, wfDetailsId);
		
		String locale = localeObj.toString();
		String wfName = wfDetails.getWorkflowType();
		HouseType houseType = CommitteeWFUtility.getHouseType(wfDetails);
		PartyType partyType = CommitteeWFUtility.getPartyType(wfName, locale);
		UserGroup userGroup = CommitteeWFUtility.getUserGroup(wfDetails);
		
		// STEP 1: Populate Committee Composite VO
		List<Committee> committees = 
			CommitteeWFUtility.getCommittees(wfDetails);
		CommitteeWFUtility.populateCommitteeCompositeVO(model, committees, 
				houseType, partyType, locale);
		
		// STEP 2: Populate Statuses & Status
		Status status = CommitteeWFUtility.getStatus(wfDetails);
		CommitteeWFUtility.populateStatuses(model, 
				userGroup, houseType, status, locale);
		CommitteeWFUtility.populateStatus(model, status);
		
		// STEP 3: Populate Actors & Actor
		Boolean isHideNextActors = 
			CommitteeWFUtility.
				isThisUserExcludedFromSelectingNextActorInWorkflow(
						userGroup, locale);
		Integer assigneeLevel = 
			Integer.valueOf(wfDetails.getAssigneeLevel());
		if(isHideNextActors) {
			CommitteeWFUtility.hideNextActors(model);
		}
		else {
			String fullWFName = 
				CommitteeWFUtility.getFullWorkflowName(status);
			List<WorkflowActor> wfActors = 
				CommitteeWFUtility.populateNextActors(model, userGroup, 
					houseType, status, fullWFName, assigneeLevel, locale);
			
			// wfActors can be empty if the current actor is the last
			// actor in the workflow
			if(! wfActors.isEmpty()) {
				WorkflowActor wfActor = wfActors.get(0);
				CommitteeWFUtility.populateNextActor(model, wfActor);
			}
			else {
				CommitteeWFUtility.hideNextActors(model);
			}
		}
		
		// STEP 4: Populate Workflow attributes
		Boolean isWorkflowInit = false;
		CommitteeWFUtility.populateWorkflowAttributes(
				model, wfName, isWorkflowInit, assigneeLevel);
		
		// STEP 5: Populate PartyType, HouseType, UserGroup
		CommitteeWFUtility.populatePartyType(model, partyType);
		CommitteeWFUtility.populateHouseType(model, houseType);
		CommitteeWFUtility.populateUserGroup(model, userGroup);
		
		// STEP 6: Return View
		 if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
        }else{
        	model.addAttribute("type",request.getSession().getAttribute("type"));
            request.getSession().removeAttribute("type");
        }
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
			final RedirectAttributes redirectAttributes,
			final Locale localeObj) {
		String locale = localeObj.toString();
		
		// Determine the houseType
		List<Committee> committees = CommitteeWFUtility.getCommittees(request);
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		
		UserGroup userGroup = 
			CommitteeWFUtility.getUserGroup(request, userGroups, CommitteeWFUtility.getCredential(this.getCurrentUser(), locale), locale);
		HouseType houseType = 
			CommitteeWFUtility.getHouseType(committees, userGroup, locale);
		
		// Determine the workflowName
		String wfName = CommitteeWFUtility.getWorkflowName(request);
		
		// STEP 1
		this.memberAdditionRequestSaveInformation(request, houseType, locale);
		
		WorkflowDetails wfDetails = null;
		String wfInit = CommitteeWFUtility.getWorkflowInit(request);
		if(wfInit != null && wfInit.equals("true")) {
			// STEP 2
			AuthUser authUser = this.getCurrentUser();
			wfDetails =	this.startMemberAdditionProcess(request, authUser, 
					userGroup, houseType, locale);
		}
		else {
			// STEP 3
			wfDetails = this.proceedMemberAdditionProcess(request, 
					userGroup, houseType, wfName, locale);
		}
		
		 redirectAttributes.addFlashAttribute("type", "success");
	        //this is done so as to remove the bug due to which update message appears even though there
	        //is a fresh new/edit request i.e after creating/updating records if we click on
	        //new /edit then success message appears
	        request.getSession().setAttribute("type","success");
		String returnURL = "redirect:memberAddition/processed/" 
			+ wfDetails.getId();
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
		String locale = localeObj.toString();
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, workflowDetailsId);
		
		String wfName = wfDetails.getWorkflowType();
		HouseType houseType = CommitteeWFUtility.getHouseType(wfDetails);
		PartyType partyType = CommitteeWFUtility.getPartyType(wfName, locale);
		UserGroup userGroup = CommitteeWFUtility.getUserGroup(wfDetails);
		
		// STEP 1: Populate CommitteeCompositeVO
		List<Committee> committees = 
			CommitteeWFUtility.getCommittees(wfDetails);
		CommitteeWFUtility.populateCommitteeCompositeVO(model, committees, 
				houseType, partyType, locale);
		
		// STEP 2: Populate Status
		Status status = CommitteeWFUtility.getStatus(wfDetails);
		CommitteeWFUtility.populateStatus(model, status);
		
		// STEP 3: Populate Actor
		Boolean isHideNextActors = 
			CommitteeWFUtility.
				isThisUserExcludedFromSelectingNextActorInWorkflow(
						userGroup, locale);
		if(isHideNextActors) {
			CommitteeWFUtility.hideNextActors(model);
		}
		else {
			WorkflowActor wfActor = CommitteeWFUtility.getNextActor(wfDetails);
			CommitteeWFUtility.populateNextActor(model, wfActor);
		}
		
		// STEP 4: Populate Remarks
		String remarks = CommitteeWFUtility.getRemarks(wfDetails);
		CommitteeWFUtility.populateRemarks(model, remarks);
		
		// STEP 5: Populate Workflow attributes
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		Boolean isWorkflowInit = false;
		CommitteeWFUtility.populateWorkflowAttributes(
				model, wfName, isWorkflowInit, assigneeLevel);
		
		// STEP 6: Render as Read Only. Since the task is completed,
		// 		   the User must not be allowed to perform any modifications
		CommitteeWFUtility.renderAsReadOnly(model);
		
		// STEP 7: Return View
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		return urlPattern + "/" + ugtType;
	}
	
	//===========================================================
	//	Speaker, Chairman initiated workflows for adding invited
	//  members to the committees.
	//===========================================================
	/**
	 * The initial GET request for "ADDITION OF INVITED MEMBERS TO 
	 * COMMITTEES" workflow will land here.
	 */
	@RequestMapping(value="init/invitedMemberAddition",
			method=RequestMethod.GET)
	public String initAdditionOfInvitedMembers(
			final ModelMap model,
			final Locale localeObj) {
		AuthUser authUser = this.getCurrentUser();
		String wfName = 
			ApplicationConstants.COMMITTEE_ADDITION_OF_INVITED_MEMBERS;
		String locale = localeObj.toString();
		
		UserGroup userGroup = CommitteeWFUtility.getWorkflowInitiator(
				authUser, wfName, locale);
		if(userGroup != null) {
			HouseType houseType = 
				CommitteeWFUtility.getHouseType(userGroup, locale);
			
			// STEP 1: Populate CommitteeCompositeVO
			List<Committee> committees = 
				Committee.findCommitteesForInvitedMembersToBeAdded(
						houseType, true, locale);
			CommitteeWFUtility.populateCommitteeCompositeVO(model, committees, 
					houseType, locale);
			
			// STEP 2: Populate Statuses & Status
			List<Status> statuses = CommitteeWFUtility.populateStatuses(
					model, userGroup, houseType, wfName, locale);
			Status status = statuses.get(0);
			CommitteeWFUtility.populateStatus(model, status);
			
			// STEP 3: Populate Actors & Actor
			Boolean isHideNextActors = 
				CommitteeWFUtility.
					isThisUserExcludedFromSelectingNextActorInWorkflow(
						userGroup, locale);
			Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
			if(isHideNextActors) {
				CommitteeWFUtility.hideNextActors(model);
			}
			else {
				String fullWFName = 
					CommitteeWFUtility.getFullWorkflowName(status);
				List<WorkflowActor> wfActors = 
					CommitteeWFUtility.populateNextActors(model, userGroup, 
							houseType, status, fullWFName, 
							assigneeLevel, locale);
				WorkflowActor wfActor = wfActors.get(0);
				CommitteeWFUtility.populateNextActor(model, wfActor);
			}
			
			// STEP 4: Populate Workflow attributes
			Boolean isWorkflowInit = true;
			CommitteeWFUtility.populateWorkflowAttributes(
					model, wfName, isWorkflowInit, assigneeLevel);
			
			// STEP 5: Populate HouseType
			CommitteeWFUtility.populateHouseType(model, houseType);
			
			// STEP 5: Return View
			String ugtType = userGroup.getUserGroupType().getType();
			return "workflow/committee/invitedMemberAddition/" + ugtType;
		}
		else {
			CommitteeWFUtility.error(model, 
					"current_user_is_not_authorized_to_initiate_workflow");
			return "workflow/committee/error";
		}
	}
	
	/**
	 * The intermediate (workflow) GET request for "ADDITION OF INVITED 
	 * MEMBERS TO COMMITTEES" workflow, will land here.
	 */
	@RequestMapping(value="invitedMemberAddition", method=RequestMethod.GET)
	public String intermediateInvitedMemberAdditionRequest(
			final ModelMap model,
			final HttpServletRequest request,
			final Locale localeObj) {		
		Long wfDetailsId = (Long) request.getAttribute("workflowdetails");
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, wfDetailsId);
		
		String locale = localeObj.toString();
		String wfName = wfDetails.getWorkflowType();
		HouseType houseType = CommitteeWFUtility.getHouseType(wfDetails);
		UserGroup userGroup = CommitteeWFUtility.getUserGroup(wfDetails);
		
		// STEP 1: Populate Committee Composite VO
		List<Committee> committees = 
			CommitteeWFUtility.getCommittees(wfDetails);
		CommitteeWFUtility.populateCommitteeCompositeVO(model, committees, 
				houseType, locale);
		
		// STEP 2: Populate Statuses & Status
		Status status = CommitteeWFUtility.getStatus(wfDetails);
		CommitteeWFUtility.populateStatuses(model, 
				userGroup, houseType, status, locale);
		CommitteeWFUtility.populateStatus(model, status);
		
		// STEP 3: Populate Actors & Actor
		Boolean isHideNextActors = 
			CommitteeWFUtility.
				isThisUserExcludedFromSelectingNextActorInWorkflow(
						userGroup, locale);
		if(isHideNextActors) {
			CommitteeWFUtility.hideNextActors(model);
		}
		else {
			String fullWFName = 
				CommitteeWFUtility.getFullWorkflowName(status);
			Integer assigneeLevel = 
				Integer.valueOf(wfDetails.getAssigneeLevel());
			List<WorkflowActor> wfActors = 
				CommitteeWFUtility.populateNextActors(model, userGroup, 
					houseType, status, fullWFName, assigneeLevel, locale);
			
			// wfActors can be empty if the current actor is the last
			// actor in the workflow
			if(! wfActors.isEmpty()) {
				WorkflowActor wfActor = wfActors.get(0);
				CommitteeWFUtility.populateNextActor(model, wfActor);
			}
			else {
				CommitteeWFUtility.hideNextActors(model);
			}
		}
		
		// STEP 4: Populate Workflow attributes
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		Boolean isWorkflowInit = false;
		CommitteeWFUtility.populateWorkflowAttributes(
				model, wfName, isWorkflowInit, assigneeLevel);
		
		// STEP 5: Populate HouseType
		CommitteeWFUtility.populateHouseType(model, houseType);
		
		// STEP 6: Return View
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		return urlPattern + "/" + ugtType;
	}
	
	/**
	 * All the PUT requests (initial or intermediate) for "ADDITION OF 
	 * INVITED MEMBERS TO COMMITTEES" workflow will land here.
	 * 
	 * STEPS:
	 * 1. Save the incoming information.
	 * 
	 * 2. If this is workflow initiation request (workflowInit == true),
	 * 	  start invited member addition process
	 *
	 * 3. Else proceed invited member addition process
	 * 	
	 */
	@RequestMapping(value="invitedMemberAddition", method=RequestMethod.PUT)
	public String processInvitedMemberAdditionRequest(
			final HttpServletRequest request,
			final Locale localeObj) {
		String locale = localeObj.toString();
		
		// Determine the houseType
		List<Committee> committees = CommitteeWFUtility.getCommittees(request);
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		UserGroup userGroup =
			CommitteeWFUtility.getUserGroup(request, userGroups, 
					CommitteeWFUtility.getCredential(this.getCurrentUser(), locale), locale);
		HouseType houseType =
			CommitteeWFUtility.getHouseType(committees, userGroup, locale);
		
		// Determine the workflowName
		String wfName = CommitteeWFUtility.getWorkflowName(request);
		
		// STEP 1
		this.invitedMemberAdditionRequestSaveInformation(request, 
				houseType, locale);
		/*
		WorkflowDetails wfDetails = null;
		String wfInit = CommitteeWFUtility.getWorkflowInit(request);
		if(wfInit != null && wfInit.equals("true")) {
			// STEP 2
			AuthUser authUser = this.getCurrentUser();
			wfDetails = this.startInvitedMemberAdditionProcess(request, 
					authUser, userGroup, houseType, locale);
		}
		else {
			// STEP 3
			wfDetails = this.proceedInvitedMemberAdditionProcess(request, 
					userGroup, houseType, wfName, locale);
		}
		
		String returnURL = "redirect:invitedMemberAddition/processed/" 
			+ wfDetails.getId();
		return returnURL;
		*/
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/committee/invitedMemberAddition/" + ugtType;
	}
	
	/**
	 * Once the PUT request is processed for "ADDITION OF 
	 * INVITED MEMBERS TO COMMITTEES" workflow, this is the 
	 * client redirect URL.
	 */
	@RequestMapping(
			value="invitedMemberAddition/processed/{workflowDetailsId}", 
			method=RequestMethod.GET)
	public String rendererForInvitedMemberAdditionRequest(final ModelMap model,
			@PathVariable("workflowDetailsId") final Long workflowDetailsId,
			final Locale localeObj) {
		String locale = localeObj.toString();
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, workflowDetailsId);
		
		String wfName = wfDetails.getWorkflowType();
		HouseType houseType = CommitteeWFUtility.getHouseType(wfDetails);
		UserGroup userGroup = CommitteeWFUtility.getUserGroup(wfDetails);
		
		// STEP 1: Populate CommitteeCompositeVO
		List<Committee> committees = 
			CommitteeWFUtility.getCommittees(wfDetails);
		CommitteeWFUtility.populateCommitteeCompositeVO(model, committees, 
				houseType, locale);
		
		// STEP 2: Populate Status
		Status status = CommitteeWFUtility.getStatus(wfDetails);
		CommitteeWFUtility.populateStatus(model, status);
		
		// STEP 3: Populate Actor
		Boolean isHideNextActors = 
			CommitteeWFUtility.
				isThisUserExcludedFromSelectingNextActorInWorkflow(
						userGroup, locale);
		if(isHideNextActors) {
			CommitteeWFUtility.hideNextActors(model);
		}
		else {
			WorkflowActor wfActor = CommitteeWFUtility.getNextActor(wfDetails);
			CommitteeWFUtility.populateNextActor(model, wfActor);
		}
		
		// STEP 4: Populate Remarks
		String remarks = CommitteeWFUtility.getRemarks(wfDetails);
		CommitteeWFUtility.populateRemarks(model, remarks);
		
		// STEP 5: Populate Workflow attributes
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		Boolean isWorkflowInit = false;
		CommitteeWFUtility.populateWorkflowAttributes(
				model, wfName, isWorkflowInit, assigneeLevel);
		
		// STEP 6: Render as Read Only. Since the task is completed,
		// 		   the User must not be allowed to perform any modifications
		CommitteeWFUtility.renderAsReadOnly(model);
		
		// STEP 7: Return View
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
		AuthUser authUser = this.getCurrentUser();
		
		UserGroup userGroup = CommitteeWFUtility.getWorkflowInitiator(
				authUser, workflowName, locale);
		if(userGroup != null) {
			HouseType houseType = 
				CommitteeWFUtility.getHouseType(userGroup, locale);
			
			// STEP 1: Populate Statuses & Status
			List<Status> statuses = CommitteeWFUtility.populateStatuses(
					model, userGroup, houseType, workflowName, locale);
			Status status = statuses.get(0);
			CommitteeWFUtility.populateStatus(model, status);
			
			// STEP 2: Populate CommitteeCompositeVO
			String wfName = CommitteeWFUtility.getWorkflowName(status);
			PartyType partyType = 
				CommitteeWFUtility.getPartyType(wfName, locale);
			List<Committee> committees = 
				Committee.findCommitteesToBeProcessed(houseType, 
						partyType, true, locale);
			CommitteeWFUtility.populateCommitteeCompositeVO(model, committees, 
					houseType, partyType, locale);
			
			// STEP 3: Populate Actors & Actor
			Boolean isHideNextActors = 
				CommitteeWFUtility.
					isThisUserExcludedFromSelectingNextActorInWorkflow(
						userGroup, locale);
			Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
			if(isHideNextActors) {
				CommitteeWFUtility.hideNextActors(model);
			}
			else {
				String fullWFName = 
					CommitteeWFUtility.getFullWorkflowName(status);
				List<WorkflowActor> wfActors = 
					CommitteeWFUtility.populateNextActors(model, userGroup, 
							houseType, status, fullWFName, 
							assigneeLevel, locale);
				WorkflowActor wfActor = wfActors.get(0);
				CommitteeWFUtility.populateNextActor(model, wfActor);
			}
			
			// STEP 4: Populate Workflow attributes
			Boolean isWorkflowInit = true;
			CommitteeWFUtility.populateWorkflowAttributes(
					model, wfName, isWorkflowInit, assigneeLevel);
			
			// STEP 5: Populate PartyType, HouseType, UserGroup
			CommitteeWFUtility.populatePartyType(model, partyType);
			CommitteeWFUtility.populateHouseType(model, houseType);
			CommitteeWFUtility.populateUserGroup(model, userGroup);
			
			// STEP 6: Return View
			String ugtType = userGroup.getUserGroupType().getType();
			return "workflow/committee/memberAddition/" + ugtType;
		}
		else {
			CommitteeWFUtility.error(model, 
					"current_user_is_not_authorized_to_initiate_workflow");
			return "workflow/committee/error";
		}
	}
	
	private void memberAdditionRequestSaveInformation(
			final HttpServletRequest request,
			final HouseType houseType,
			final String locale) {
		List<Committee> committees = CommitteeWFUtility.getCommittees(request);
		Status status = CommitteeWFUtility.getStatus(request);
		String remarks = CommitteeWFUtility.getRemarks(request);
		
		// Set the statuses & remarks as per the houseType & workflowName
		String wfName = CommitteeWFUtility.getWorkflowName(status);
		String houseTypeType = houseType.getType();
		if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {			
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				for(Committee c : committees) {
					c.setInternalStatusPAMLH(status);
					c.setRemarksPAMLH(remarks);
					CommitteeWFUtility.setCommitteeMembers(request, c, locale);
					c.merge();
				}
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				for(Committee c : committees) {
					c.setInternalStatusPAMUH(status);
					c.setRemarksPAMUH(remarks);
					CommitteeWFUtility.setCommitteeMembers(request, c, locale);
					c.merge();
				}
			}
		}
		else if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				for(Committee c : committees) {
					c.setInternalStatusLOPLH(status);
					c.setRemarksLOPLH(remarks);
					CommitteeWFUtility.setCommitteeMembers(request, c, locale);
					c.merge();
				}
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				for(Committee c : committees) {
					c.setInternalStatusLOPUH(status);
					c.setRemarksLOPUH(remarks);
					CommitteeWFUtility.setCommitteeMembers(request, c, locale);
					c.merge();
				}
			}
		}
	}
	
	private void invitedMemberAdditionRequestSaveInformation(
			final HttpServletRequest request,
			final HouseType houseType,
			final String locale) {
		List<Committee> committees = CommitteeWFUtility.getCommittees(request);
		Status status = CommitteeWFUtility.getStatus(request);
		String remarks = CommitteeWFUtility.getRemarks(request);
		
		// Set the statuses & remarks as per the houseType
		String houseTypeType = houseType.getType();
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			for(Committee c : committees) {
				c.setInternalStatusIMLH(status);
				c.setRemarksIMLH(remarks);
				CommitteeWFUtility.setCommitteeInvitedMembers(
						request, c, locale);
				c.merge();
			}
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			for(Committee c : committees) {
				c.setInternalStatusIMUH(status);
				c.setRemarksIMUH(remarks);
				CommitteeWFUtility.setCommitteeInvitedMembers(
						request, c, locale);
				c.merge();
			}
		}
	}

	/**
	 * STEPS:
	 * 1. Create WorkflowDetails for current actor
	 * 2. Start Workflow
	 * 3. Create WorkflowDetails for next actor
	 */
	private WorkflowDetails startMemberAdditionProcess(
			final HttpServletRequest request,
			final AuthUser authUser,
			final UserGroup userGroup,
			final HouseType houseType,
			final String locale) {
		Status status = CommitteeWFUtility.getStatus(request);
		Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
		String urlPattern = ApplicationConstants.COMMITTEE_MEMBER_ADDITION_URL;
		
		WorkflowDetails wfDetails = this.createInitWorkflowDetails(request, 
				authUser, userGroup, houseType, status, assigneeLevel, 
				urlPattern, locale);
		
		Task task = this.startProcess(request, userGroup, houseType, 
				assigneeLevel, locale);
		
		this.createNextActorWorkflowDetails(request, task, 
				userGroup, houseType, status, assigneeLevel, 
				urlPattern, locale);
		
		return wfDetails;
	}
	
	/**
	 * STEPS:
	 * 1. Create WorkflowDetails for current actor
	 * 2. Start Workflow
	 * 3. Create WorkflowDetails for next actor
	 */
	private WorkflowDetails startInvitedMemberAdditionProcess(
			final HttpServletRequest request,
			final AuthUser authUser,
			final UserGroup userGroup,
			final HouseType houseType,
			final String locale) {
		Status status = CommitteeWFUtility.getStatus(request);
		Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
		String urlPattern = 
			ApplicationConstants.COMMITTEE_INVITED_MEMBER_ADDITION_URL;
		
		WorkflowDetails wfDetails = this.createInitWorkflowDetails(request, 
				authUser, userGroup, houseType, status, assigneeLevel, 
				urlPattern, locale);
		
		Task task = this.startProcess(request, userGroup, houseType, 
				assigneeLevel, locale);
		
		this.createNextActorWorkflowDetails(request, task, 
				userGroup, houseType, status, assigneeLevel, 
				urlPattern, locale);
		
		return wfDetails;
	}
	
	/**
	 * STEPS:
	 * 1. Update current actor's WorkflowDetails
	 * 2. Proceed the Workflow for the next actor by completing 
	 * 	  current actor's task & setting the next actors parameters.
	 * 3. Create WorkflowDetails for next actor 
	 */
	private WorkflowDetails proceedMemberAdditionProcess(
			final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final String workflowName,
			final String locale) {
		// STEP 1: Get current actors' WorkflowDetails
		Long[] committeeIds = CommitteeWFUtility.getCommitteeIds(request);
		String strCommitteeIds = 
			CommitteeWFUtility.convertToString(committeeIds, ",");
		
		WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(
				userGroup, strCommitteeIds, workflowName,
				ApplicationConstants.MYTASK_PENDING, locale);
		
		// STEP 2: Update current actors' WorkflowDetails
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		WorkflowActor nextWFActor = CommitteeWFUtility.getNextActor(request, 
				userGroup, houseType, assigneeLevel, locale);
		this.updateWorkflowDetails(wfDetails, nextWFActor);
		
		// STEP 3: Proceed the Workflow
		String taskId = wfDetails.getTaskId();
		Task newTask = 
			this.proceedProcess(nextWFActor, houseType, taskId, locale);
		
		// STEP 4: If there happens to be a nextActor, create WorkflowDetails
		// for him by passing the above acquired task
		if(newTask != null) {
			Status status = CommitteeWFUtility.getStatus(request);
			String urlPattern = 
				ApplicationConstants.COMMITTEE_MEMBER_ADDITION_URL;
			this.createNextActorWorkflowDetails(request, newTask, 
					userGroup, houseType, status, assigneeLevel, 
					urlPattern, locale);
		}
		
		return wfDetails;
	}
	
	/**
	 * STEPS:
	 * 1. Update current actor's WorkflowDetails
	 * 2. Proceed the Workflow for the next actor by completing 
	 * 	  current actor's task & setting the next actors parameters.
	 * 3. Create WorkflowDetails for next actor 
	 */
	private WorkflowDetails proceedInvitedMemberAdditionProcess(
			final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final String workflowName,
			final String locale) {
		// STEP 1: Get current actors' WorkflowDetails
		Long[] committeeIds = CommitteeWFUtility.getCommitteeIds(request);
		String strCommitteeIds = 
			CommitteeWFUtility.convertToString(committeeIds, ",");
		
		WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(
				userGroup, strCommitteeIds, workflowName,
				ApplicationConstants.MYTASK_PENDING, locale);
		
		// STEP 2: Update current actors' WorkflowDetails
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		WorkflowActor nextWFActor = CommitteeWFUtility.getNextActor(request, 
				userGroup, houseType, assigneeLevel, locale);
		this.updateWorkflowDetails(wfDetails, nextWFActor);
		
		// STEP 3: Proceed the Workflow
		String taskId = wfDetails.getTaskId();
		Task newTask = 
			this.proceedProcess(nextWFActor, houseType, taskId, locale);
		
		// STEP 4: If there happens to be a nextActor, create WorkflowDetails
		// for him by passing the above acquired task
		if(newTask != null) {
			Status status = CommitteeWFUtility.getStatus(request);
			String urlPattern = 
				ApplicationConstants.COMMITTEE_INVITED_MEMBER_ADDITION_URL;
			this.createNextActorWorkflowDetails(request, newTask, 
					userGroup, houseType, status, assigneeLevel, 
					urlPattern, locale);
		}
		
		return wfDetails;
	}

	private Task startProcess(final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final Integer assigneeLevel,
			final String locale) {
		String key = ApplicationConstants.APPROVAL_WORKFLOW; 
		ProcessDefinition definition = 
			processService.findProcessDefinitionByKey(key);
		Map<String, String> properties = new HashMap<String, String>();
		
		WorkflowActor wfActor = CommitteeWFUtility.getNextActor(request, 
				userGroup, houseType, assigneeLevel, locale);
		if(wfActor != null) {
			User user = CommitteeWFUtility.getUser(wfActor, houseType, locale);
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
			final HouseType houseType,
			final String currentTaskId,
			final String locale) {
		Task currentTask = processService.findTaskById(currentTaskId);
		Map<String, String> properties = new HashMap<String, String>();
		
		if(nextWorkflowActor != null) {
			User user = CommitteeWFUtility.getUser(nextWorkflowActor, 
					houseType, locale);
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
		
		// This condition will arise when the process has completed
		if(process != null) {
			Task newTask = processService.getCurrentTask(process);
			return newTask;
		}
		
		return null;
	}
	
	private WorkflowDetails createInitWorkflowDetails(
			final HttpServletRequest request,
			final AuthUser authUser,
			final UserGroup userGroup,
			final HouseType houseType,
			final Status status,
			final Integer assigneeLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		String wfName = CommitteeWFUtility.getWorkflowName(status);
		String wfSubType =  CommitteeWFUtility.getWorkflowSubType(status);
		Date completionTime = new Date();
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setCompletionTime(completionTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
		
		// User parameters
		String assignee = authUser.getUsername();
		UserGroupType ugt = userGroup.getUserGroupType();
		wfDetails.setAssignee(assignee);
		wfDetails.setAssigneeUserGroupType(ugt.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(userGroup.getId()));
		wfDetails.setAssigneeUserGroupName(ugt.getName());
		wfDetails.setAssigneeLevel(String.valueOf(assigneeLevel));
		
		WorkflowActor wfActor = CommitteeWFUtility.getNextActor(request, 
				userGroup, houseType, assigneeLevel, locale);
		if(wfActor != null) {
			wfDetails.setNextWorkflowActorId(String.valueOf(wfActor.getId()));
		}
		
		// Domain parameters
		// Not applicable parameters: deviceId, deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		Long[] committeeIds = CommitteeWFUtility.getCommitteeIds(request);
		String strCommitteeIds = 
			CommitteeWFUtility.convertToString(committeeIds, ","); 
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
			final HouseType houseType,
			final Status status,
			final Integer currentActorLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		String wfName = CommitteeWFUtility.getWorkflowName(status);
		String wfSubType =  CommitteeWFUtility.getWorkflowSubType(status);
		Date assignmentTime = new Date();
		wfDetails.setProcessId(task.getProcessInstanceId());
		wfDetails.setTaskId(task.getId());
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
		
		// User parameters
		// Not applicable parameters: nextWorkflowActorId
		WorkflowActor nextActor = CommitteeWFUtility.getNextActor(request, 
				currentActorUserGroup, houseType, 
				currentActorLevel, locale);
		UserGroup nextUserGroup = 
			CommitteeWFUtility.getUserGroup(nextActor, houseType, locale);
		UserGroupType nextUGT = nextUserGroup.getUserGroupType();
		wfDetails.setAssignee(task.getAssignee());
		wfDetails.setAssigneeUserGroupType(nextUGT.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(nextUserGroup.getId()));
		wfDetails.setAssigneeUserGroupName(nextUGT.getName());
		wfDetails.setAssigneeLevel(String.valueOf(nextActor.getLevel()));
		
		// Domain parameters
		// Not applicable parameters: deviceId, deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		Long[] committeeIds = CommitteeWFUtility.getCommitteeIds(request);
		String strCommitteeIds = 
			CommitteeWFUtility.convertToString(committeeIds, ",");
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
	
}

class CommitteeWFUtility {
	
	private static Logger logger = 
		LoggerFactory.getLogger(CommitteeWFUtility.class);

	//=============== "GET" API =======================
	public static Long[] getCommitteeIds(final HttpServletRequest request) {
		List<Long> committeeIds = new ArrayList<Long>();
		
		String[] values = getBeginningWith(request, "committeeId");
		for(String s : values) {
			Long committeeId = Long.valueOf(s);
			committeeIds.add(committeeId);
		}
		Collections.sort(committeeIds);
		
		return committeeIds.toArray(new Long[]{});
	}
	
	public static Long[] getCommitteeIds(
			final WorkflowDetails workflowDetails) {
		String strCommitteeIds = workflowDetails.getDomainIds();
		Long[] committeeIds = convertToLongArray(strCommitteeIds, ",");
		return committeeIds;
	}

	public static List<Committee> getCommittees(
			final HttpServletRequest request) {
		Long[] committeeIds = getCommitteeIds(request);
		List<Committee> committees = getCommittees(committeeIds);
		return committees;
	}
	
	public static List<Committee> getCommittees(
			final WorkflowDetails workflowDetails) {
		Long[] committeeIds = getCommitteeIds(workflowDetails);
		List<Committee> committees = getCommittees(committeeIds);
		return committees;
	}
	
	public static HouseType getHouseType(final UserGroup userGroup, 
			final String locale) {
		String strHouseType = 
			userGroup.getParameterValue("HOUSETYPE_" + locale);
		if(strHouseType != null && ! strHouseType.trim().isEmpty()) {
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			return houseType;
		}
		
		return null;
	}
	
	/**
	 * Iterate the committees & return the first instance of 
	 * LOWERHOUSE or UPPERHOUSE that you come across. 
	 * 
	 * If you don't come across either LOWERHOUSE or UPPERHOUSE, 
	 * then return the houseType configured for the @param userGroup
	 * given it is not BOTHHOUSE.
	 * 
	 * If the @param userGroup does not have any houseType defined,
	 * then return LOWERHOUSE.
	 */
	public static HouseType getHouseType(final List<Committee> committees,
			final UserGroup userGroup,
			final String locale) {
		String BOTH_HOUSE = ApplicationConstants.BOTH_HOUSE;
		for(Committee c : committees) {
			CommitteeName committeeName = c.getCommitteeName();
			CommitteeType committeeType = committeeName.getCommitteeType();
			HouseType committeeHouseType = committeeType.getHouseType();
			if(! committeeHouseType.getType().equalsIgnoreCase(BOTH_HOUSE)) {
				return committeeHouseType;
			}
		}
		
		HouseType configuredHouseType = getHouseType(userGroup, locale);
		if(configuredHouseType != null && 
				! configuredHouseType.getType().equals(BOTH_HOUSE)) {
			 return configuredHouseType;
		}
		
		HouseType lowerHouseType = 
			HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
		return lowerHouseType;
	}
	
	public static HouseType getHouseType(
			final WorkflowDetails workflowDetails) {
		String houseTypeName = workflowDetails.getHouseType();
		String locale = workflowDetails.getLocale();
		HouseType houseType = HouseType.findByName(houseTypeName, locale);
		return houseType;
	}
	
	public static PartyType getPartyType(final String workflowName,
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
	
	public static Status getStatus(final HttpServletRequest request) {
		String strStatusId = request.getParameter("status");
		Long statusId = Long.valueOf(strStatusId);
		Status status = Status.findById(Status.class, statusId); 
		return status;
	}
	
	public static Status getStatus(final WorkflowDetails workflowDetails) {
//		Committee committee = getFirstCommittee(workflowDetails);		
//		HouseType houseType = getHouseType(workflowDetails);
//		
//		String wfName = workflowDetails.getWorkflowType();
//		String houseTypeType = houseType.getType();
//		
//		Status status = null;
//		if(wfName.equals(ApplicationConstants.
//				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
//			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
//				status = committee.getInternalStatusPAMLH();
//			}
//			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
//				status = committee.getInternalStatusPAMUH();
//			}
//		}
//		else if(wfName.equals(ApplicationConstants.
//				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
//			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
//				status = committee.getInternalStatusLOPLH();
//			}
//			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
//				status = committee.getInternalStatusLOPUH();
//			}
//		}
//		else if(wfName.equals(
//				ApplicationConstants.COMMITTEE_ADDITION_OF_INVITED_MEMBERS)) {
//			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
//				status = committee.getInternalStatusIMLH();
//			}
//			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
//				status = committee.getInternalStatusIMUH();
//			}
//		}
//		
//		return status;
		
		String statusType = workflowDetails.getWorkflowSubType();
		String locale = workflowDetails.getLocale();
		Status status = Status.findByType(statusType, locale);
		return status;
	}
	
	/**
	 * Some privileged users such as Speaker, Chairman,
	 * Department of Parliamentary Affairs Minister, 
	 * Leader of Opposition are not shown the "Next Actors" drop down.
	 * 
	 * If this user's usergrouptype is in {list of usergrouptypes 
	 * for whom next authority is not shown} then query WorkflowConfig
	 * to find next actor (that too back actor if status is SEND_BACK).
	 * 
	 * Else retrieve nextActor from request object. This is the nextActor 
	 * as selected by this user.
	 */
	public static WorkflowActor getNextActor(final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final Integer assigneeLevel,
			final String locale) {
		WorkflowActor wfActor = null;
		
		Boolean isExcluded = 
			isThisUserExcludedFromSelectingNextActorInWorkflow(
					userGroup, locale);
		String strWFActorId = request.getParameter("actor");
		
		if(isExcluded) {
			Status status = getStatus(request);
			String wfName = getFullWorkflowName(status);
			
			wfActor = WorkflowConfig.findNextCommitteeActor(houseType, 
					userGroup, status, wfName, assigneeLevel, locale);
		}
		else if(strWFActorId != null) {
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
	public static WorkflowActor getNextActor(
			final WorkflowDetails workflowDetails) {
		String strWFActorId = workflowDetails.getNextWorkflowActorId();
		if(strWFActorId != null) {
			Long wfActorId = Long.valueOf(strWFActorId);
			WorkflowActor wfActor = 
				WorkflowActor.findById(WorkflowActor.class, wfActorId);
			return wfActor;
		}
		
		return null;
	}
	
	public static String getRemarks(final HttpServletRequest request) {
		return request.getParameter("remarks");
	}
	
	public static String getRemarks(final WorkflowDetails workflowDetails) {
		Committee committee = getFirstCommittee(workflowDetails);		
		HouseType houseType = getHouseType(workflowDetails);
		
		String wfName = workflowDetails.getWorkflowType();
		String houseTypeType = houseType.getType();
		
		String remarks = null;
		if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER)) {
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				remarks = committee.getRemarksPAMLH();
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				remarks = committee.getRemarksPAMUH();
			}
		}
		else if(wfName.equals(ApplicationConstants.
				COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION)) {
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				remarks = committee.getRemarksLOPLH();
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				remarks = committee.getRemarksLOPUH();
			}
		}
		else if(wfName.equals(
				ApplicationConstants.COMMITTEE_ADDITION_OF_INVITED_MEMBERS)) {
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				remarks = committee.getRemarksIMLH();
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				remarks = committee.getRemarksIMUH();
			}
		}
		
		return remarks;
	}
	
	public static User getUser(final WorkflowActor wfActor, 
			final HouseType houseType,
			final String locale) {
		UserGroup userGroup = getUserGroup(wfActor, houseType, locale);
		if(userGroup != null) {
			User user = getUser(userGroup, locale);
			return user;
		}
		
		return null;
	}
	 
	/**
	 * If @param authUser is configured as (one of) the initiator 
	 * of the workflow named @param workflowName then return the 
	 * @param authUser's UserGroup.
	 * 
	 * Else return null.
	 */
	public static UserGroup getWorkflowInitiator(final AuthUser authUser,
			final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase(); 
		String name = "COMMITTEE_" + wfName + "_INITIATOR";
		
		// ugtType could be comma separated userGroupTypes
		// in cases where there could be more than one initiator
		String ugtType = getCustomParameterValue(name, locale);
		String[] ugtTypes = tokenize(ugtType, ",");
	
			
		List<UserGroup> userGroups = authUser.getUserGroups();
		UserGroup userGroup = getUserGroup(userGroups, ugtTypes, getCredential(authUser, locale));
		return userGroup;
	}

	public static UserGroup getUserGroup(final HttpServletRequest request,
			final List<UserGroup> userGroups,
			Credential credential,
			final String locale) {
		Status status = getStatus(request);
		String wfName = getWorkflowName(status);
		String[] ugtTypes = getUserGroupTypeTypes(wfName, locale);
		return getUserGroup(userGroups, ugtTypes, credential);
	}
	
	public static UserGroup getUserGroup(final WorkflowActor wfActor, 
			final HouseType houseType,
			final String locale) {
		List<UserGroup> userGroups = getUserGroups(wfActor, locale);		
		UserGroup userGroup = 
			getEligibleUserGroup(userGroups, houseType, true, locale);
		if(userGroup != null) {
			return userGroup;
		}
		
		return null;
	}
	
	public static UserGroup getUserGroup(
			final WorkflowDetails workflowDetails) {
		String strUserGroupId = workflowDetails.getAssigneeUserGroupId();
		Long userGroupId = Long.valueOf(strUserGroupId);
		UserGroup userGroup = UserGroup.findById(UserGroup.class, userGroupId);
		return userGroup;
	}
	
	public static String getWorkflowInit(final HttpServletRequest request) {
		return request.getParameter("workflowInit");
	}
	
	public static String getFullWorkflowName(final Status status) {
		String wfName = getWorkflowName(status);
		String fullWfName = wfName + "_workflow";
		return fullWfName;
	}
	
	public static String getWorkflowName(final HttpServletRequest request) {
		String fullWFName = request.getParameter("workflowName");
		String[] tokens = tokenize(fullWFName, "_");
		String wfName = tokens[0];
		return wfName;
	}
	
	public static String getWorkflowName(final Status status) {
		String statusType = status.getType();
		String[] tokens = tokenize(statusType, "_");
		int length = tokens.length;
		return tokens[length - 1];
	}
	
	public static String getWorkflowSubType(final Status status) {
		return status.getType();
	}
	
	/**
	 * Some privileged users such as Speaker, Chairman,
	 * Department of Parliamentary Affairs Minister, 
	 * Leader of Opposition are not shown the "Next Actors" drop down.
	 * 
	 * This method determines whether the current user is
	 * one of those configured privileged users.
	 */
	public static Boolean isThisUserExcludedFromSelectingNextActorInWorkflow(
			final UserGroup userGroup,
			final String locale) {
		String name = ApplicationConstants.
			COMMITTEE_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW;
		String value = getCustomParameterValue(name, locale);
		
		String[] ugtTypes = tokenize(value, ","); 
		String ugtType = userGroup.getUserGroupType().getType();
		for(String ugtt : ugtTypes) {
			if(ugtt.equals(ugtType)) {
				return true;
			}
		}
		
		return false;
	}
	
	//=============== "SET" API =======================
	public static Committee setCommitteeMembers(
			final HttpServletRequest request,
			final Committee committee,
			final String locale) {
		Committee c1 = setCommitteeChairman(request, committee, locale);
		Committee c2 = setCommitteeCoreMembers(request, c1, locale);
		return c2;
	}
	
	public static Committee setCommitteeInvitedMembers(
			final HttpServletRequest request,
			final Committee committee,
			final String locale) {
		Long committeeId = committee.getId();
		String key = "invitedMembers_" + committeeId;
		String[] values = getValues(request, key);
		
		CommitteeDesignation designation = CommitteeDesignation.findByType(
				ApplicationConstants.COMMITTEE_INVITED_MEMBER, locale);
		Date joiningDate = new Date();
		
		for(String value : values) {
			Long memberId = Long.valueOf(value);
			Member member = Member.findById(Member.class, memberId);
			
			CommitteeMember committeeMember = 
				new CommitteeMember(member, designation, joiningDate, locale);
			committee.getInvitedMembers().add(committeeMember);
		}
			
		return committee;
	}
	
	//=============== "POPULATE MODEL" API ============
	public static void populateCommitteeCompositeVO(final ModelMap model,
			final List<Committee> committees,
			final HouseType houseType,
			final PartyType partyType,
			final String locale) {	
		CommitteeCompositeVO committeeCompositeVO = Committee.findCommitteeVOs(
						committees, houseType, partyType, locale);		
		model.addAttribute("committeeCompositeVO", committeeCompositeVO);
	}
	
	public static void populateCommitteeCompositeVO(final ModelMap model,
			final List<Committee> committees,
			final HouseType houseType,
			final String locale) {	
		CommitteeCompositeVO committeeCompositeVO = Committee.findCommitteeVOs(
						committees, houseType, locale);		
		model.addAttribute("committeeCompositeVO", committeeCompositeVO);
	}
	
	/**
	 * The put up options are configured as key-value pairs in CustomParameter.
	 * The template for key formation is:
	 * 		"COMMITTEE_PUT_UP_OPTIONS_" + toUpperCase(STATUSTYPE)
	 * 		+ "_" + toUpperCase(HOUSETYPE) + "_" 
	 * 		+ "_" + toUpperCase(USERGROUPTYPE)
	 * 
	 * If the aforementioned key-value pair is not configured for this user,
	 * try the next generalized configuration. The template is:
	 * 		"COMMITTEE_PUT_UP_OPTIONS_" + toUpperCase(WORKFLOWNAME)
	 * 		+ "_DEFAULT"
	 * 
	 * If the aforementioned key-value pair is not configured for this user,
	 * call populateStatuses(final ModelMap model,
	 *		final UserGroup userGroup,
	 *		final HouseType houseType,
	 *		final String workflowName,
	 *		final String locale)
	 */
	public static List<Status> populateStatuses(final ModelMap model,
			final UserGroup userGroup,
			final HouseType houseType,
			final Status status,
			final String locale) {
		// Prepare the parameters
		String statusType = status.getType().toUpperCase();
		String houseTypeType = houseType.getType().toUpperCase();
		String ugtType = userGroup.getUserGroupType().getType().toUpperCase();
		
		// Retrieve statuses as comma separated string
		String name = "COMMITTEE_PUT_UP_OPTIONS_" + statusType + "_"
			+ houseTypeType + "_" + ugtType;
		String options = getCustomParameterValue(name, locale);
		if(options == null) {
			name = "COMMITTEE_PUT_UP_OPTIONS_" + statusType + "_DEFAULT";
			options = getCustomParameterValue(name, locale);
			if(options == null) {
				String wfName = getWorkflowName(status).toUpperCase();
				return populateStatuses(model, userGroup, 
						houseType, wfName, locale);
			}
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
	public static List<Status> populateStatuses(final ModelMap model,
			final UserGroup userGroup,
			final HouseType houseType,
			final String workflowName,
			final String locale) {
		// Prepare the parameters
		String wfName = workflowName.toUpperCase();
		String houseTypeType = houseType.getType().toUpperCase();
		String ugtType = userGroup.getUserGroupType().getType().toUpperCase();
		
		// Retrieve statuses as comma separated string
		String name = "COMMITTEE_PUT_UP_OPTIONS_" + wfName + "_" 
			+ houseTypeType + "_" + ugtType;
		String options = getCustomParameterValue(name, locale); 
		if(options == null) {
			name = "COMMITTEE_PUT_UP_OPTIONS_" + wfName + "_" 
				+ houseTypeType + "_DEFAULT";
			options = getCustomParameterValue(name, locale);
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
	
	public static void populateStatus(final ModelMap model,
			final Status status) {
		model.addAttribute("status", status);
	}
	
	public static List<WorkflowActor> populateNextActors(final ModelMap model,
			final UserGroup userGroup,
			final HouseType houseType,
			final Status status,
			final String workflowName,
			final int assigneeLevel,
			final String locale) {
		List<Reference> actors = new ArrayList<Reference>();
		
		List<WorkflowActor> wfActors = WorkflowConfig.findCommitteeActors(
				houseType, userGroup, status, 
				workflowName, assigneeLevel, locale);
		for(WorkflowActor wfa : wfActors) {
			String id = String.valueOf(wfa.getId());
			String name = wfa.getUserGroupType().getName();
			Reference actor = new Reference(id, name);
			actors.add(actor);
		}
		model.addAttribute("actors", actors);
		
		return wfActors;
	}
	
	public static void populateNextActor(final ModelMap model,
			final WorkflowActor workflowActor) {
		if(workflowActor != null) {
			String id = String.valueOf(workflowActor.getId());
			String name = workflowActor.getUserGroupType().getName();
			Reference actor = new Reference(id, name);
			model.addAttribute("actor", actor);
		}
	}
	
	public static void populateUserGroup(final ModelMap model,
			final UserGroup userGroup) {
		model.addAttribute("userGroup", userGroup);
	}
	
	public static void populateWorkflowAttributes(final ModelMap model,
			final String workflowName,
			final Boolean isWorkflowInit,
			final Integer assigneeLevel) {
		String fullWFName = workflowName + "_workflow";
		model.addAttribute("workflowName", fullWFName);
		model.addAttribute("workflowInit", isWorkflowInit);
		model.addAttribute("assigneeLevel", assigneeLevel);
	}
	
	public static void populatePartyType(final ModelMap model,
			final PartyType partyType) {
		model.addAttribute("partyType", partyType);
	}
	
	public static void populateHouseType(final ModelMap model,
			final HouseType houseType) {
		model.addAttribute("houseType", houseType);
	}
	
	public static void populateRemarks(final ModelMap model,
			final String remarks) {
		model.addAttribute("remarks", remarks);
	}
	
	public static void error(final ModelMap model, 
			final String errorcode) {
		model.addAttribute("errorcode", errorcode);
	}
	
	/**
	 * Some privileged users such as Speaker, Chairman,
	 * Department of Parliamentary Affairs Minister, 
	 * Leader of Opposition are not shown the "Next Actors" drop down.
	 * 
	 * Note that "Next Actors" dropdown is also not shown in the
	 * case when the current actor is the last actor in the
	 * workflow.
	 * 
	 * This method sets the attribute which will be used by
	 * the View to hide the "Next Actors" drop down.
	 */
	public static void hideNextActors(final ModelMap model) {
		model.addAttribute("hideNextActors", true);
	}
	
	public static void renderAsReadOnly(ModelMap model) {
		model.addAttribute("renderAsReadOnly", true);
	}
	
	//=============== "UTILITY" API ===================
	public static String convertToString(final Long[] ids, 
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
	
	//=============== INTERNAL METHODS ================
	private static Committee getFirstCommittee(
			final WorkflowDetails workflowDetails) {
		Long[] committeeIds = getCommitteeIds(workflowDetails);
		Long committeeId = committeeIds[0];
		Committee committee = Committee.findById(Committee.class, committeeId);
		return committee;
	}
	
	private static List<Committee> getCommittees(final Long[] committeeIds) {
		List<Committee> committees = new ArrayList<Committee>();
		
		for(Long cid : committeeIds) {
			Committee committee = Committee.findById(Committee.class, cid);
			committees.add(committee);
		}
		
		return committees;
	}
	
	private static Committee setCommitteeChairman(
			final HttpServletRequest request,
			final Committee committee,
			final String locale) {
		Long committeeId = committee.getId();
		String key = "chairman_" + committeeId;
		String value = getValue(request, key);
		
		if(value != null) {
			Long memberId = Long.valueOf(value);
			Member member = Member.findById(Member.class, memberId);
			
			CommitteeDesignation designation = 
				CommitteeDesignation.findByType(
						ApplicationConstants.COMMITTEE_CHAIRMAN, locale);
			
			Date joiningDate = new Date();
			
			CommitteeMember committeeMember = 
				new CommitteeMember(member, designation, joiningDate, locale);
			// Chairman should be the first member in the list
			committee.getMembers().add(0, committeeMember);
		}

		return committee;
	}
	
	private static Committee setCommitteeCoreMembers(
			final HttpServletRequest request,
			final Committee committee,
			final String locale) {
		Long committeeId = committee.getId();
		String key = "members_" + committeeId;
		String[] values = getValues(request, key);
		
		CommitteeDesignation designation = CommitteeDesignation.findByType(
				ApplicationConstants.COMMITTEE_MEMBER, locale);
		Date joiningDate = new Date();
		
		for(String value : values) {
			Long memberId = Long.valueOf(value);
			Member member = Member.findById(Member.class, memberId);
			
			CommitteeMember committeeMember = 
				new CommitteeMember(member, designation, joiningDate, locale);
			committee.getMembers().add(committeeMember);
		}
			
		return committee;
	}
	
	private static Boolean isCommitteeNamesConfigured(
			final UserGroup userGroup,
			final String locale) {
		String strCommitteeNames = 
			userGroup.getParameterValue("COMMITTEENAME_" + locale);
		if(strCommitteeNames != null && ! strCommitteeNames.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	private static User getUser(final UserGroup userGroup,
			final String locale) {
		Credential credential = userGroup.getCredential();
		User user = User.findByFieldName(User.class, 
				"credential", credential, locale);
		return user;
	}
	
	private static UserGroup getUserGroup(final List<UserGroup> userGroups,
			final String[] userGroupTypeTypes,Credential credential) {
		for(String ugtt : userGroupTypeTypes) {
			UserGroup userGroup = getUserGroup(userGroups, ugtt, credential);
			if(userGroup != null) {
				return userGroup;
			}
		}
		
		return null;
	}
	
	private static UserGroup getUserGroup(final List<UserGroup> userGroups, 
			final String userGroupTypeType, Credential credential) {
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
					UserGroup.findActive(credential, ug.getUserGroupType(), new Date(), ug.getLocale());
				return userGroup;
			}
		}
		
		return null;
	}
	
	/**
	 * For any of the UserGroup (hereon refered as ug) in the list 
	 * @param userGroups to be an eligible userGroup, it must satisfy 
	 * 3 cases:
	 * 1. If @param isIncludeBothHouseType is true then ug's houseType 
	 * must be same as @param houseType, else ug's houseType must be 
	 * same as @param houseType or BOTHHOUSE.
	 * 2. ug must be configured to handle committees
	 * 3. As on the current date, ug must be active.
	 */
	private static UserGroup getEligibleUserGroup(List<UserGroup> userGroups,
			final HouseType houseType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		for(UserGroup ug : userGroups) {
			// ug's houseType should be same as @param houseType
			boolean flag1 = false;
			String houseTypeType = houseType.getType();
			HouseType usersHouseType = getHouseType(ug, locale);
			if(isIncludeBothHouseType) {
				if(usersHouseType != null &&
						(usersHouseType.getType().equals(houseTypeType)
						|| usersHouseType.getType().equals(
								ApplicationConstants.BOTH_HOUSE))) {
					flag1 = true;
				}
			}
			else {
				if(usersHouseType != null &&
						usersHouseType.getType().equals(houseTypeType)) {
					flag1 = true;
				}
			}
			
			// ug must have commitees configured
			boolean flag2 = isCommitteeNamesConfigured(ug, locale);
			
			// ug must be active
			boolean flag3 = false;
			Date fromDate = ug.getActiveFrom();
			Date toDate = ug.getActiveTo();
			Date currentDate = new Date();
			if((fromDate == null || currentDate.after(fromDate) 
					||currentDate.equals(fromDate))
					&& (toDate == null || currentDate.before(toDate)
							|| currentDate.equals(toDate))) {
				flag3 = true;
			}
			
			// if all 3 cases are met then return user
			if(flag1 && flag2 && flag3) {
				return ug;
			}
		}
		
		return null;
	}
	
	private static List<UserGroup> getUserGroups(
			final WorkflowActor workflowActor,
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
	private static String[] getUserGroupTypeTypes(final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase();
		String name = "COMMITTEE_" + wfName + "_ALLOWED_USERGROUPTYPES";
		String value = getCustomParameterValue(name, locale);
		if(value == null) {
			name = "COMMITTEE_ALLOWED_USERGROUPTYPES";
			value = getCustomParameterValue(name, locale);
		}
		String[] ugtTypes = tokenize(value, ",");
		return ugtTypes;
	}
	
	@SuppressWarnings("unchecked")
	private static String getValue(final HttpServletRequest request,
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
	
	private static String[] getValues(final HttpServletRequest request,
			final String key) {
		String[] values = request.getParameterValues(key);
		if(values == null) {
			values = new String[]{};
		}
		return values;
	}
	
	@SuppressWarnings("unchecked")
	private static String[] getBeginningWith(final HttpServletRequest request,
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
	
	private static String getCustomParameterValue(final String name,
			final String locale) {
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, name, locale);
		if(parameter != null) {
			return parameter.getValue();
		}
		
		return null;
	}
	
	private static Long[] convertToLongArray(final String str,
			final String delimiter) {
		List<Long> ids = new ArrayList<Long>();
		
		String[] tokens = tokenize(str, delimiter);
		for(String s : tokens) {
			Long id = Long.valueOf(s);
			ids.add(id);
		}
		
		return ids.toArray(new Long[]{});
	}
	
	private static String[] tokenize(final String str,
			final String token) {
		String[] tokens = str.split(token);
		
		int length = tokens.length;
		for(int i = 0; i < length; i++) {
			tokens[i] = tokens[i].trim();
		}

		return tokens;
	}
	
	public static Credential getCredential(AuthUser authUser, String locale){
		Credential cr = null;
		
		try{
			User usr = User.findById(User.class, authUser.getUserId());
			cr = usr.getCredential();
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return cr;
	}

}