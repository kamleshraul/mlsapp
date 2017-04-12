package org.mkcl.els.controller.cis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeReporter;
import org.mkcl.els.domain.CommitteeTour;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.TourItinerary;
import org.mkcl.els.domain.Town;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/committeetour")
public class CommitteeTourController extends GenericController<CommitteeTour> {

	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateModule(final ModelMap model,
			final HttpServletRequest request, final String locale,
			final AuthUser currentUser) {
		try {
			this.populateCommitteeTypesAndNames(model, locale);
		} catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void populateNew(final ModelMap model, 
			final CommitteeTour domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		
		//String strCommitteeType = request.getParameter("committeeType");
		//CommitteeType committeeType = CommitteeType.findById(CommitteeType.class, Long.parseLong(strCommitteeType));
		String strCommitteeName = request.getParameter("committeeName");
		CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, Long.parseLong(strCommitteeName));
		if(committeeName != null){
			model.addAttribute("committeeDisplayName", committeeName.getDisplayName());
			model.addAttribute("committeName",committeeName.getId());
		}
		
		this.populateCommitteeNames(model, locale);
		
		List<State> states = this.populateStates(model, locale);
		
		State state = states.get(0);
		List<District> districts = this.populateDistricts(model, state, locale);
		
		District district = districts.get(0);
		this.populateTowns(model, district, locale);
		
		this.populateItinerariesCount(model, 0);
		this.populateLanguages(model, locale);
		this.populateReportersCount(model, 0);
	}

	@Override
	protected void preValidateCreate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.setCommittee(domain, request);
		this.setTourItineraries(domain, request);
		this.setCommitteeReporters(domain, request);
	}

	@Override
	protected void customValidateCreate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceCreationUniqueness(domain, result);
	}
	
	@Override
	protected void populateEdit(final ModelMap model, 
			final CommitteeTour domain,
			final HttpServletRequest request) {
		this.commonPopulateEdit(model, domain, request);
		
		// If the status is equal to COMMITTEETOUR_CREATED, 
		// then show the workflow specific attributes
		// (viz, Put up for, Next actor, Remarks, etc)
		Status status = domain.getStatus();
		if(status != null) {
			Integer statusPriority = status.getPriority();
			model.addAttribute("currentStatusPriority", statusPriority);
			
			this.populateInternalStatus(model, status);
			
			if(status.getType().equals(
					ApplicationConstants.COMMITTEETOUR_CREATED)) {
				this.initRequestForTour(model, domain);
			}
		}
	}
	
	@Override
	protected String modifyEditUrlPattern(final String newUrlPattern,
			final HttpServletRequest request, 
			final ModelMap model, 
			final String locale) {
		Status recommendAdmission = 
			Status.findByType(
					ApplicationConstants.COMMITTEETOUR_RECOMMEND_ADMISSION, 
					locale);
		Integer recommendAdmissionPriority = recommendAdmission.getPriority();
		
		Integer currentStatusPriority = 
			(Integer) model.get("currentStatusPriority");
		
		// If the currentStatusPriority is greater than or equal to 
		// recommendAdmissionPriority then it means that the request is in
		// workflow. Hence, render workflow specific pages for the 
		// actors instead of showing edit page.
		if(currentStatusPriority.compareTo(recommendAdmissionPriority) >= 0) {
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			UserGroup userGroup = 
				this.getUserGroup(request, userGroups, locale);
			String ugt = userGroup.getUserGroupType().getType();
			String returnURL = 
				ApplicationConstants.COMMITTEETOUR_RETURN_URL + "/" + ugt;
			return returnURL;
		}
		
		// By default show edit page
		return newUrlPattern;
	}

	@Override
	protected void preValidateUpdate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.setCommittee(domain, request);
		this.setTourItineraries(domain, request);
		this.setCommitteeReporters(domain, request);
		
		// Determine the houseType
		String locale = domain.getLocale();
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		UserGroup userGroup = this.getUserGroup(request, userGroups, locale);
		HouseType houseType = this.getHouseType(domain, userGroup, locale);
		
		this.setStatus(domain, houseType, request);
		this.setRemarks(domain, houseType, request);
	}
	
	@Override
	protected void customValidateUpdate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUpdationUniqueness(domain, result);
		this.valVersionMismatch(domain, result);
	}
	
	@Override
	protected void populateAfterUpdate(final ModelMap model, 
			final CommitteeTour domain,
			final HttpServletRequest request) {		
		Status status = this.getStatus(request);		
		if(status != null) {
			// Determine the houseType
			String locale = domain.getLocale();
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			UserGroup userGroup = 
				this.getUserGroup(request, userGroups, locale);
			HouseType houseType = this.getHouseType(domain, userGroup, locale);
			
			String wfInit = this.getWorkflowInit(request);
			
			// If wfInit is 'true' then start the workflow
			if(wfInit != null && wfInit.equals("true")) {
				AuthUser authUser = this.getCurrentUser();
				this.startTourRequestProcess(domain, 
						request, authUser, userGroup, houseType, locale);
			}
			// Else If wfInit is 'false' then proceed the workflow
			else if(wfInit != null && wfInit.equals("false")) {
				String wfName = this.getWorkflowName(status);
				this.proceedTourRequestProcess(domain, 
						request, userGroup, houseType, wfName, locale);
			}
		}
	}
	
	@RequestMapping(value="{id}/view", method=RequestMethod.GET)
	public String view(final ModelMap model, 
			@PathVariable("id") final Long id,
			final Locale localeObj) {
		String locale = localeObj.toString();
		
		CommitteeTour tour = CommitteeTour.findById(CommitteeTour.class, id);
		Committee committee = tour.getCommittee();
		CommitteeName committeeName = committee.getCommitteeName();
		Town town = tour.getTown();
		District district = District.find(town, locale);
		State state = State.find(district, locale);
		
		model.addAttribute("id", tour.getId());
		model.addAttribute("committeeName", committeeName.getDisplayName());
		model.addAttribute("state", state.getName());
		model.addAttribute("district", district.getName());
		model.addAttribute("town", town.getName());
		model.addAttribute("venueName", tour.getVenueName());
		
		String dateFormatKey = ApplicationConstants.SERVER_DATETIMEFORMAT;
		//String dateFormatVal = this.getCustomParameterValue(dateFormatKey, "");
		String fromDate = FormaterUtil.formatDateToString(
				tour.getFromDate(), 
				dateFormatKey,
				locale.toString());
		model.addAttribute("fromDate", fromDate);
		
		String toDate = FormaterUtil.formatDateToString(
				tour.getToDate(), 
				dateFormatKey,
				locale.toString());
		model.addAttribute("toDate", toDate);

		List<TourItinerary> itineraries = tour.getItineraries();
		this.populateItineraries(model, itineraries);
		
		List<CommitteeReporter> reporters = tour.getReporters();
		this.populateReporters(model, reporters);

		return "committeetour/view";
	}
	
	@RequestMapping(value="/touritinerary/{id}/delete", 
			method=RequestMethod.DELETE)
	public String deleteTourItinerary(final @PathVariable("id") Long id) {
	    TourItinerary tourItinerary = 
	    	TourItinerary.findById(TourItinerary.class, id);
	    tourItinerary.remove();
	    return "info";
	}
	
	@RequestMapping(value="/committeereporter/{id}/delete", 
			method=RequestMethod.DELETE)
	public String deleteCommitteeReporter(final @PathVariable("id") Long id) {
		CommitteeReporter committeeReporter = 
			CommitteeReporter.findById(CommitteeReporter.class, id);
		committeeReporter.remove();
	    return "info";
	}
	
	//==========================================
	//	WORKFLOW METHODS
	//==========================================
	@SuppressWarnings("rawtypes")
	@Override
	protected <E extends BaseDomain> void customInitBinder(Class clazz,
			WebDataBinder binder) {
		super.customInitBinder(CommitteeTour.class, binder);
		
		// the time part of fromDate & toDate is getting lost
		// due to the default implementation of customInitBinder
		// in GenericController, wherein, dates are formatted
		// with formatType "SERVER_DATEFORMAT". Hence, the need
		// to register the date custom editor with formatType
		// "SERVER_DATETIMEFORMAT"
		String format = ApplicationConstants.SERVER_DATETIMEFORMAT;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, 
				this.getUserLocale());
        dateFormat.setLenient(true);
        binder.registerCustomEditor(java.util.Date.class, 
        		new CustomDateEditor(dateFormat, true));
	}
	
	/**
	 * The intermediate (workflow) GET request for "REQUEST FOR TOUR" 
	 * workflows, will land here.
	 */
	@RequestMapping(value="requestForTour", method=RequestMethod.GET)
	public String intermediateRequestForTour(final ModelMap model,
			final HttpServletRequest request,
			final Locale localeObj) {	
		Long wfDetailsId = (Long) request.getAttribute("workflowdetails");
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, wfDetailsId);
		
		String locale = localeObj.toString();
		String wfName = wfDetails.getWorkflowType();
		String wfSubtype = wfDetails.getWorkflowSubType();
		HouseType houseType = this.getHouseType(wfDetails);
		UserGroup userGroup = this.getUserGroup(wfDetails);
		
		// STEP 1: Populate domain
		String domainId = wfDetails.getDomainIds();
		CommitteeTour domain = CommitteeTour.findById(CommitteeTour.class, 
				Long.valueOf(domainId));
		model.addAttribute("domain", domain);
		
		// STEP 2: Populate domain specific atributes
		this.commonPopulateEdit(model, domain, request);
		
		// STEP 3: Populate Statuses & Status
		List<Status> statuses = this.populateStatuses(model, 
				userGroup, wfSubtype, locale);
		Status status = statuses.get(0);
		this.populateStatus(model, status);
		
		// STEP 4: Populate Actors & Actor
		Boolean isHideNextActors = 
			this.isThisUserExcludedFromSelectingNextActorInWorkflow(
					userGroup, locale);
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		if(isHideNextActors) {
			this.hideNextActors(model);
		}
		else {
			String fullWFName = this.getFullWorkflowName(status);
			List<WorkflowActor> wfActors = 
				this.populateNextActors(model, userGroup, 
						houseType, status, fullWFName, 
						assigneeLevel, locale);
			// wfActors can be empty if the current actor is the last
			// actor in the workflow
			if(! wfActors.isEmpty()) {
				WorkflowActor wfActor = wfActors.get(0);
				this.populateNextActor(model, wfActor);
			}
			else {
				this.hideNextActors(model);
			}
		}

		// STEP 5: Populate Workflow attributes
		String fullWFName = wfName + "_workflow";
		Boolean isWorkflowInit = false;
		this.populateWorkflowAttributes(model, fullWFName, isWorkflowInit, 
				assigneeLevel);
		
		// STEP 6: Populate HouseType, UserGroup
		this.populateHouseType(model, houseType);
		this.populateUserGroup(model, userGroup);
		
		// STEP 5: Return View
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/" + urlPattern + "/" + ugtType;
	}
	
	/**
	 * Once the PUT request is processed for "REQUEST FOR TOUR" 
	 * workflow, this is the client redirect URL.
	 */
	@RequestMapping(
			value="requestForTour/processed/{workflowDetailsId}", 
			method=RequestMethod.GET)
	public String rendererForRequestForTour(final ModelMap model,
			@PathVariable("workflowDetailsId") final Long workflowDetailsId,
			final Locale localeObj) {
		String locale = localeObj.toString();
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, workflowDetailsId);
		
		HouseType houseType = this.getHouseType(wfDetails);
		UserGroup userGroup = this.getUserGroup(wfDetails);
		
		// STEP 1: Populate domain
		String domainId = wfDetails.getDomainIds();
		CommitteeTour domain = CommitteeTour.findById(CommitteeTour.class, 
				Long.valueOf(domainId));
		model.addAttribute("domain", domain);
		
		// STEP 2: Populate domain specific atributes
		Committee committee = domain.getCommittee();
		this.populateCommitteeName(model, committee);
		
		Town town = domain.getTown();
		District district = District.find(town, locale);
		State state = State.find(district, locale);
		this.populateState(model, state);		
		this.populateDistrict(model, district);

		List<TourItinerary> itineraries = domain.getItineraries();
		this.populateItineraries(model, itineraries);
		this.populateItinerariesCount(model, itineraries.size());
		
		List<CommitteeReporter> reporters = domain.getReporters();
		this.populateReporters(model, reporters);
		this.populateReportersCount(model, reporters.size());
		
		// STEP 3: Populate Status
		Status status = this.getStatus(wfDetails);
		this.populateStatus(model, status);
		
		// STEP 4: Populate Actor
		Boolean isHideNextActors = 
			this.isThisUserExcludedFromSelectingNextActorInWorkflow(
					userGroup, locale);
		if(isHideNextActors) {
			this.hideNextActors(model);
		}
		else {
			WorkflowActor wfActor = this.getNextActor(wfDetails);
			this.populateNextActor(model, wfActor);
		}
		
		// STEP 5: Populate Remarks
		if(houseType.getType().equals(
				ApplicationConstants.LOWER_HOUSE)) {
			this.populateRemarks(model, domain.getRemarksLH());
		}
		else if(houseType.getType().equals(
				ApplicationConstants.UPPER_HOUSE)) {
			this.populateRemarks(model, domain.getRemarksUH());
		}

		// STEP 6: Render as Read Only. Since the task is completed,
		// 		   the User must not be allowed to perform any modifications
		this.renderAsReadOnly(model);
		
		// STEP 7: Return View
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/" + urlPattern + "/" + ugtType;
	}

	//=============== INTERNAL METHODS ================
	/**
	 * If status.type == COMMITTEETOUR_CREATED, 
	 * then populate workflow initiation specific attributes
	 */
	private void initRequestForTour(final ModelMap model, 
			final CommitteeTour domain) {
		String locale = domain.getLocale();
		if(domain.getStatus().getType().equals(
				ApplicationConstants.COMMITTEETOUR_CREATED)) {
			AuthUser authUser = this.getCurrentUser();
			String workflowName = 
				ApplicationConstants.COMMITTEETOUR_RECOMMEND_ADMISSION;
			
			UserGroup userGroup = this.getWorkflowInitiator(authUser, 
					workflowName, locale);
			HouseType houseType = this.getCurrentUsersHouseType(locale);
			if(userGroup != null) {
				// STEP 1: Populate Statuses & Status
				List<Status> statuses = this.populateStatuses(model, 
						userGroup, workflowName, locale);
				Status status = statuses.get(0);
				this.populateStatus(model, status);
				
				// STEP 2: Populate Actors & Actor
				Integer assigneeLevel = 
					ApplicationConstants.WORKFLOW_START_LEVEL;
				String fullWFName = this.getFullWorkflowName(status);
				List<WorkflowActor> wfActors = 
					this.populateNextActors(model, userGroup, 
							houseType, status, fullWFName, 
							assigneeLevel, locale);
				WorkflowActor wfActor = wfActors.get(0);
				this.populateNextActor(model, wfActor);
				
				// STEP 3: Populate Workflow attributes
				Boolean isWorkflowInit = true;
				this.populateWorkflowAttributes(
						model, fullWFName, isWorkflowInit, assigneeLevel);
				
				// STEP 4: Populate HouseType, UserGroup
				this.populateHouseType(model, houseType);
				this.populateUserGroup(model, userGroup);
			}
		}
	}
	
	/**
	 * STEPS:
	 * 1. Create WorkflowDetails for current actor
	 * 2. Start Workflow
	 * 3. Create WorkflowDetails for next actor
	 */
	private WorkflowDetails startTourRequestProcess(
			final CommitteeTour tour,
			final HttpServletRequest request,
			final AuthUser authUser,
			final UserGroup userGroup,
			final HouseType houseType,
			final String locale) {
		Status status = this.getStatus(request);
		
		Committee committee = tour.getCommittee();
		Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
		String urlPattern = 
			ApplicationConstants.COMMITTEETOUR_REQUEST_FOR_TOUR_URL;
		
		WorkflowDetails wfDetails = this.createInitWorkflowDetails(tour, 
				request, authUser, userGroup, houseType, status, 
				assigneeLevel, urlPattern, locale);
		
		Task task = this.startProcess(request, userGroup, houseType, 
				assigneeLevel,committee, locale);
		
		this.createNextActorWorkflowDetails(tour, request, task, 
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
	private WorkflowDetails proceedTourRequestProcess(
			final CommitteeTour tour,
			final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final String workflowName,
			final String locale) {
		// STEP 1: Get current actors' WorkflowDetails
		Committee committee = tour.getCommittee();
		String deviceId = String.valueOf(committee.getId());
		String domainIds = String.valueOf(tour.getId());
		
		WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(
				userGroup, deviceId, domainIds, workflowName,
				ApplicationConstants.MYTASK_PENDING, locale);
		
		// STEP 2: Update current actors' WorkflowDetails
		Integer assigneeLevel = Integer.valueOf(wfDetails.getAssigneeLevel());
		WorkflowActor nextWFActor = this.getNextActor(request, 
				userGroup, houseType, assigneeLevel, locale);
		this.updateWorkflowDetails(wfDetails, nextWFActor);
		
		// STEP 3: Proceed the Workflow
		String taskId = wfDetails.getTaskId();
		Task newTask = 
			this.proceedProcess(nextWFActor, houseType, taskId, committee, locale);
		
		// STEP 4: If there happens to be a nextActor, create WorkflowDetails
		// for him by passing the above acquired task
		if(newTask != null) {
			Status status = this.getStatus(request);
			String urlPattern = 
				ApplicationConstants.COMMITTEETOUR_REQUEST_FOR_TOUR_URL;
			this.createNextActorWorkflowDetails(tour, request, newTask, 
					userGroup, houseType, status, assigneeLevel, 
					urlPattern, locale);
		}
		
		return wfDetails;
	}
	
	private WorkflowDetails createInitWorkflowDetails(
			final CommitteeTour tour,
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
		String wfName = this.getWorkflowName(status);
		String wfSubType =  this.getWorkflowSubType(status);
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
		
		WorkflowActor wfActor = this.getNextActor(request, 
				userGroup, houseType, assigneeLevel, locale);
		if(wfActor != null) {
			wfDetails.setNextWorkflowActorId(String.valueOf(wfActor.getId()));
		}
		
		// Domain parameters
		// CommitteeTour can be arranged for multiple committees.
		// Besides one committee can have multiple tours. Hence,
		// committeeId as well as committeeTourId must be 
		// captured so as to uniquely identify a WorkflowDetail.
		// Not applicable parameters: deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		Committee committee = tour.getCommittee();
		wfDetails.setDeviceId(String.valueOf(committee.getId()));
		wfDetails.setDomainIds(String.valueOf(tour.getId()));
		wfDetails.setHouseType(houseType.getName());
		
		// Misc parameters
		// TODO Not set as yet: form
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.COMMITTEE);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	private WorkflowDetails createNextActorWorkflowDetails(
			final CommitteeTour tour,
			final HttpServletRequest request,
			final Task task,
			final UserGroup currentActorUserGroup,
			final HouseType houseType,
			final Status status,
			final Integer currentActorLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		Committee committee = tour.getCommittee();
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
		WorkflowActor nextActor = this.getNextActor(request, 
				currentActorUserGroup, houseType, 
				currentActorLevel, locale);
		UserGroup nextUserGroup = 
			this.getUserGroup(nextActor, houseType,committee, locale);
		UserGroupType nextUGT = nextUserGroup.getUserGroupType();
		wfDetails.setAssignee(task.getAssignee());
		wfDetails.setAssigneeUserGroupType(nextUGT.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(nextUserGroup.getId()));
		wfDetails.setAssigneeUserGroupName(nextUGT.getName());
		wfDetails.setAssigneeLevel(String.valueOf(nextActor.getLevel()));
		
		// Domain parameters
		// CommitteeTour can be arranged for multiple committees.
		// Besides one committee can have multiple tours. Hence,
		// committeeId as well as committeeTourId must be 
		// captured so as to uniquely identify a WorkflowDetail.
		// Not applicable parameters: deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		
		wfDetails.setDeviceId(String.valueOf(committee.getId()));
		wfDetails.setDomainIds(String.valueOf(tour.getId()));
		wfDetails.setHouseType(houseType.getName());
		
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
	
	private Task startProcess(final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final Integer assigneeLevel,
			final Committee committee,
			final String locale) {
		String key = ApplicationConstants.APPROVAL_WORKFLOW; 
		ProcessDefinition definition = 
			processService.findProcessDefinitionByKey(key);
		Map<String, String> properties = new HashMap<String, String>();
		
		WorkflowActor wfActor = this.getNextActor(request, 
				userGroup, houseType, assigneeLevel, locale);
		if(wfActor != null) {
			User user = this.getUser(wfActor, houseType, committee, locale);
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
			final Committee committee,
			final String locale) {
		Task currentTask = processService.findTaskById(currentTaskId);
		Map<String, String> properties = new HashMap<String, String>();
		
		if(nextWorkflowActor != null) {
			User user = this.getUser(nextWorkflowActor, 
					houseType,committee, locale);
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

	//=============== "GET" METHODS ============
	private String getFullWorkflowName(final Status status) {
		
		String wfName = getWorkflowName(status);
		String fullWfName = wfName + "_workflow";
		return fullWfName;
	}
	
	private String getWorkflowName(final Status status) {
		String statusType = status.getType();
		String[] tokens = tokenize(statusType, "_");
		int length = tokens.length;
		
		String workflowName = tokens[length - 1];
		if(workflowName.equals(ApplicationConstants.SENDBACK)) {
			return ApplicationConstants.ADMISSIONTOUR;
		}
		
		return workflowName;
	}
	
	private String getWorkflowSubType(final Status status) {
		return status.getType();
	}
	
	private List<UserGroup> getUserGroups(
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
	 * If @param authUser is configured as (one of) the initiator 
	 * of the workflow named @param workflowName then return the 
	 * @param authUser's UserGroup.
	 * 
	 * Else return null.
	 */
	private UserGroup getWorkflowInitiator(final AuthUser authUser,
			final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase(); 
		String name = "COMMITTEETOUR_" + wfName + "_INITIATOR";
		
		// ugtType could be comma separated userGroupTypes
		// in cases where there could be more than one initiator
		String ugtType = getCustomParameterValue(name, "");
		String[] ugtTypes = tokenize(ugtType, ",");
		
		List<UserGroup> userGroups = authUser.getUserGroups();
		UserGroup userGroup = this.getUserGroup(userGroups, ugtTypes);
		return userGroup;
	}
	
	private UserGroup getUserGroup(final HttpServletRequest request,
			final List<UserGroup> userGroups,
			final String locale) {
		Status status = getStatus(request);
		if(status != null) {
			String wfName = getWorkflowName(status);
			String[] ugtTypes = getUserGroupTypeTypes(wfName, locale);
			return getUserGroup(userGroups, ugtTypes);
		}
		else {
			String[] ugtTypes = getDefaultUserGroupTypeTypes(locale);
			return getUserGroup(userGroups, ugtTypes);
		}
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
	
	private UserGroup getUserGroup(final WorkflowActor wfActor, 
			final HouseType houseType,
			final Committee committee,
			final String locale) {
		List<UserGroup> userGroups = getUserGroups(wfActor, locale);		
		UserGroup userGroup = 
			getEligibleUserGroup(userGroups, houseType, committee, true, locale);
		if(userGroup != null) {
			return userGroup;
		}
		
		return null;
	}
	
	/**
	 * For any of the UserGroup (here on refered as ug) in the list 
	 * @param userGroups to be an eligible userGroup, it must satisfy 
	 * 3 cases:
	 * 1. If @param isIncludeBothHouseType is true then ug's houseType 
	 * must be same as @param houseType, else ug's houseType must be 
	 * same as @param houseType or BOTHHOUSE.
	 * 2. ug must be configured to handle committees
	 * 3. As on the current date, ug must be active.
	 */
	private UserGroup getEligibleUserGroup(List<UserGroup> userGroups,
			final HouseType houseType,
			final Committee committee,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		for(UserGroup ug : userGroups) {
			// ug's houseType should be same as @param houseType
			boolean flag1 = false;
			String houseTypeType = houseType.getType();
			HouseType usersHouseType = this.getHouseType(ug, locale);
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
			boolean flag2 = this.isCommitteeNamesConfigured(ug,committee, locale);
			
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
	
	private UserGroup getUserGroup(
			final WorkflowDetails workflowDetails) {
		String strUserGroupId = workflowDetails.getAssigneeUserGroupId();
		Long userGroupId = Long.valueOf(strUserGroupId);
		UserGroup userGroup = UserGroup.findById(UserGroup.class, userGroupId);
		return userGroup;
	}
	
	/**
	 * The template for default configured usergrouptypes is:
	 * 		"COMMITTEETOUR_ALLOWED_USERGROUPTYPES"
	 */
	private String[] getDefaultUserGroupTypeTypes(final String locale) {
		String name = "COMMITTEETOUR_ALLOWED_USERGROUPTYPES";
		String value = getCustomParameterValue(name, locale);
		String[] ugtTypes = tokenize(value, ",");
		return ugtTypes;
	}
	
	/**
	 * The usergrouptypes allowed for a workflow are configured as key-value
	 * pairs in CustomParameter. The template for key formation is:
	 * 		"COMMITTEETOUR_" + toUpperCase(WORKFLOWNAME) + 
	 * 		"_ALLOWED_USERGROUPTYPES"
	 * 
	 * If the aforementioned key-value pair is not configured for this workflow,
	 * then return the default configured usergrouptypes.
	 */
	private String[] getUserGroupTypeTypes(final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase();
		String name = "COMMITTEETOUR_" + wfName + "_ALLOWED_USERGROUPTYPES";
		String value = getCustomParameterValue(name, locale);
		if(value == null) {
			return getDefaultUserGroupTypeTypes(locale);
		}
		String[] ugtTypes = tokenize(value, ",");
		return ugtTypes;
	}
	
	private HouseType getCurrentUsersHouseType(final String locale) {
		AuthUser authUser = this.getCurrentUser();
		String houseTypeType = authUser.getHouseType();
		HouseType houseType = HouseType.findByType(houseTypeType, locale);
		return houseType;
	}
	
	/**
	 * Determine if the houseType of Committee is either LOWERHOUSE 
	 * or UPPERHOUSE. 
	 * 
	 * If not, then return the houseType configured for the 
	 * @param userGroup given it is not BOTHHOUSE.
	 * 
	 * If the @param userGroup does not have any houseType defined,
	 * then return LOWERHOUSE.
	 */
	private HouseType getHouseType(final CommitteeTour tour,
			final UserGroup userGroup,
			final String locale) {
		String BOTH_HOUSE = ApplicationConstants.BOTH_HOUSE;

		Committee committee = tour.getCommittee();
		CommitteeName committeeName = committee.getCommitteeName();
		CommitteeType committeeType = committeeName.getCommitteeType();
		HouseType committeeHouseType = committeeType.getHouseType();
		if(! committeeHouseType.getType().equalsIgnoreCase(BOTH_HOUSE)) {
			return committeeHouseType;
		}
		
		HouseType configuredHouseType = this.getHouseType(userGroup, locale);
		if(configuredHouseType != null && 
				! configuredHouseType.getType().equals(BOTH_HOUSE)) {
			 return configuredHouseType;
		}
		
		HouseType lowerHouseType = 
			HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
		return lowerHouseType;
	}
	
	private HouseType getHouseType(final UserGroup userGroup, 
			final String locale) {
		String strHouseType = 
			userGroup.getParameterValue("HOUSETYPE_" + locale);
		if(strHouseType != null && ! strHouseType.trim().isEmpty()) {
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			return houseType;
		}
		
		return null;
	}
	
	private Status getStatus(final HttpServletRequest request) {
		String strStatusId = request.getParameter("status");
		if(strStatusId != null) {
			Long statusId = Long.valueOf(strStatusId);
			Status status = Status.findById(Status.class, statusId); 
			return status;
		}
		return null;
	}
	
	private String getRemarks(final HttpServletRequest request) {
		return request.getParameter("remarks");
	}
	
	/**
	 * Some privileged users such as Speaker & Chairman,
	 * are not shown the "Next Actors" drop down.
	 * 
	 * If this user's usergrouptype is in {list of usergrouptypes 
	 * for whom next authority is not shown} then query WorkflowConfig
	 * to find next actor (that too back actor if status is SEND_BACK).
	 * 
	 * Else retrieve nextActor from request object. This is the nextActor 
	 * as selected by this user.
	 */
	private WorkflowActor getNextActor(final HttpServletRequest request,
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
			
			wfActor = WorkflowConfig.findNextCommitteeTourActor(houseType, 
					userGroup, status, wfName, assigneeLevel, locale);
		}
		else if(strWFActorId != null) {
			Long wfActorId = Long.valueOf(request.getParameter("actor"));
			wfActor = WorkflowActor.findById(WorkflowActor.class, wfActorId);
		}
		
		return wfActor;
	}
	
	/**
	 * Some privileged users such as Speaker & Chairman
	 * are not shown the "Next Actors" drop down.
	 * 
	 * This method determines whether the current user is
	 * one of those configured privileged users.
	 */
	private Boolean isThisUserExcludedFromSelectingNextActorInWorkflow(
			final UserGroup userGroup,
			final String locale) {
		String name = ApplicationConstants.
			COMMITTEETOUR_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW;
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
	
	private User getUser(final WorkflowActor wfActor, 
			final HouseType houseType,
			final Committee committee,
			final String locale) {
		UserGroup userGroup = getUserGroup(wfActor, houseType,committee, locale);
		if(userGroup != null) {
			User user = getUser(userGroup, locale);
			return user;
		}
		
		return null;
	}
	
	private User getUser(final UserGroup userGroup,
			final String locale) {
		Credential credential = userGroup.getCredential();
		User user = User.findByFieldName(User.class, 
				"credential", credential, locale);
		return user;
	}
	
	private Boolean isCommitteeNamesConfigured(
			final UserGroup userGroup,
			final Committee committee,
			final String locale) {
		String strCommitteeNames = 
			userGroup.getParameterValue("COMMITTEENAME_" + locale);
		CommitteeName committeeName = committee.getCommitteeName();
		if(committeeName != null){
			if(strCommitteeNames != null && ! strCommitteeNames.isEmpty()) {
				String[] committeeNames = strCommitteeNames.split("#");
				for(String cName: committeeNames){
					if(cName.equals(committeeName.getName())){
						return true;
					}
				}
			}
		}
		
		
		return false;
	}
	
	private HouseType getHouseType(
			final WorkflowDetails workflowDetails) {
		String houseTypeName = workflowDetails.getHouseType();
		String locale = workflowDetails.getLocale();
		HouseType houseType = HouseType.findByName(houseTypeName, locale);
		return houseType;
	}
	
	private Status getStatus(final WorkflowDetails workflowDetails) {
		String statusType = workflowDetails.getWorkflowSubType();
		String locale = workflowDetails.getLocale();
		Status status = Status.findByType(statusType, locale);
		return status;
	}
	
	/**
	 * USE THIS METHOD WITH LOTS OF CARE. This method must be used only
	 * after @param workflowDetails is updated. If used after 
	 * @param workflowDetails is created, then it will always return null
	 * because next actor is only available after the current actor
	 * has completed his task.
	 */
	private WorkflowActor getNextActor(
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
	
	private String getWorkflowInit(final HttpServletRequest request) {
		return request.getParameter("workflowInit");
	}
	
	//=============== "SET" METHODS ============
	private void setCommittee(final CommitteeTour domain,
			final HttpServletRequest request) {
		String strCommitteeNameId = request.getParameter("committeeName");
		if(strCommitteeNameId != null && ! strCommitteeNameId.isEmpty()) {
			Long committeeNameId = Long.parseLong(strCommitteeNameId);
			CommitteeName committeeName = 
				CommitteeName.findById(CommitteeName.class, committeeNameId);
			
			Date currentDate = new Date();
			String locale = domain.getLocale();
			Committee committee = Committee.findActiveCommittee(
					committeeName, currentDate, locale);
			domain.setCommittee(committee);
		}
	}
	
	private void setCommitteeReporters(final CommitteeTour domain,
			final HttpServletRequest request) {
		List<CommitteeReporter> reporters = new ArrayList<CommitteeReporter>();
		
		Integer committeeReporterCount =
			Integer.parseInt(request.getParameter("committeeReporterCount"));
		for(int i = 1; i <= committeeReporterCount; i++) {
			String strLanguage = 
				request.getParameter("committeeReporterLanguage" + i);
			if(strLanguage != null && ! strLanguage.isEmpty()) {
				CommitteeReporter reporter = new CommitteeReporter();
				
				Long languageId = Long.parseLong(strLanguage);
				Language language = 
					Language.findById(Language.class, languageId);
				reporter.setLanguage(language);
				
				String strId = request.getParameter("committeeReporterId" + i);
				if(strId != null && ! strId.isEmpty()){
					Long id = Long.parseLong(strId);
					reporter.setId(id);
				}

				String locale = 
					request.getParameter("committeeReporterLocale" + i);
				if(locale != null && ! locale.isEmpty()){
					reporter.setLocale(locale);
				}
				
				String strVersion = 
					request.getParameter("committeeReporterVersion" + i);
				if(strVersion != null && ! strVersion.isEmpty()){
					Long version = Long.parseLong(strVersion);
					reporter.setVersion(version);
				}
	
				String strNoOfReporters = 
					request.getParameter("committeeReporterNoOfReporters" + i);
				if(strNoOfReporters != null && ! strNoOfReporters.isEmpty()){
					Integer noOfReporters = Integer.parseInt(strNoOfReporters);
					reporter.setNoOfReporters(noOfReporters);
				}
				
				reporters.add(reporter);
			}
		}
		
		domain.setReporters(reporters);
	}

	private void setTourItineraries(final CommitteeTour domain,
			final HttpServletRequest request) {
		List<TourItinerary> itineraries = new ArrayList<TourItinerary>();
		
		Integer tourItineraryCount =
			Integer.parseInt(request.getParameter("tourItineraryCount"));
		for(int i = 1; i <= tourItineraryCount; i++) {
			String strDate = 
				request.getParameter("tourItineraryDate" + i);
			if(strDate != null && ! strDate.isEmpty()) {
				TourItinerary itinerary = new TourItinerary();

				String format = ApplicationConstants.SERVER_DATEFORMAT;
				Date date = FormaterUtil.formatStringToDate(strDate, format);
				itinerary.setDate(date);				 
				
				String strId = request.getParameter("tourItineraryId" + i);
				if(strId != null && ! strId.isEmpty()){
					Long id = Long.parseLong(strId);
					itinerary.setId(id);
				}

				String locale = request.getParameter("tourItineraryLocale" + i);
				if(locale != null && ! locale.isEmpty()){
					itinerary.setLocale(locale);
				}
				
				String strVersion = 
					request.getParameter("tourItineraryVersion" + i);
				if(strVersion != null && ! strVersion.isEmpty()){
					Long version = Long.parseLong(strVersion);
					itinerary.setVersion(version);
				}
				
				String fromTime = 
					request.getParameter("tourItineraryFromTime" + i);
				if(fromTime != null && ! fromTime.isEmpty()){
					itinerary.setFromTime(fromTime);
				}
				
				String toTime = 
					request.getParameter("tourItineraryToTime" + i);
				if(toTime != null && ! toTime.isEmpty()){
					itinerary.setToTime(toTime);
				}
				
				String details = 
					request.getParameter("tourItineraryDetails" + i);
				if(details != null && ! details.isEmpty()){
					itinerary.setDetails(details);
				}
				
				String stayOver = 
					request.getParameter("tourItineraryStayover" + i);
				if(stayOver != null && ! stayOver.isEmpty()){
					itinerary.setStayOver(stayOver);
				}
				
				itineraries.add(itinerary);
			}
		}
		
		domain.setItineraries(itineraries);
	}
	
	private void setStatus(final CommitteeTour domain,
			final HouseType houseType,
			final HttpServletRequest request) {
		Status status = this.getStatus(request);
		
		if(status != null) {
			if(houseType.getType().equals(
					ApplicationConstants.LOWER_HOUSE)) {
				domain.setInternalStatusLH(status);
			}
			else if(houseType.getType().equals(
					ApplicationConstants.UPPER_HOUSE)) {
				domain.setInternalStatusUH(status);
			}
		}
	}
	
	private void setRemarks(final CommitteeTour domain,
			final HouseType houseType,
			final HttpServletRequest request) {
		String remarks = this.getRemarks(request);

		if(remarks != null) {
			if(houseType.getType().equals(
					ApplicationConstants.LOWER_HOUSE)) {
				domain.setRemarksLH(remarks);
			}
			else if(houseType.getType().equals(
					ApplicationConstants.UPPER_HOUSE)) {
				domain.setRemarksUH(remarks);
			}
		}
	}
	
	//=============== "POPULATE MODEL" METHODS =
	private void commonPopulateEdit(final ModelMap model, 
			final CommitteeTour domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		
		Committee committee = domain.getCommittee();
		this.populateCommitteeName(model, committee);
		this.populateCommitteeNames(model, locale);
		
		Town town = domain.getTown();
		
		District district = null;
		if(town != null) {
			district = District.find(town, locale);
		}
		else {
			// Populate the district as chosen by the User
			String strDistrictId = request.getParameter("district");
			if(strDistrictId != null) {
				Long districtId = Long.valueOf(strDistrictId);
				district = District.findById(District.class, districtId);
			}
		}
		
		State state = State.find(district, locale);
		this.populateStates(model, locale);
		this.populateState(model, state);		
		this.populateDistricts(model, state, locale);
		this.populateDistrict(model, district);
		this.populateTowns(model, district, locale);
		
		List<TourItinerary> itineraries = domain.getItineraries();
		this.populateItineraries(model, itineraries);
		this.populateItinerariesCount(model, itineraries.size());
		
		this.populateLanguages(model, locale);
		
		List<CommitteeReporter> reporters = domain.getReporters();
		this.populateReporters(model, reporters);
		this.populateReportersCount(model, reporters.size());
	}
	
	private void populateCommitteeNames(final ModelMap model, 
			final String locale) {		
		HouseType houseType = this.getCurrentUsersHouseType(locale);
		Date currentDate = new Date();
		List<Committee> committees = 
			Committee.findActiveCommittees(houseType, 
					true, currentDate, locale);
		
		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
		for(Committee c : committees) {
			CommitteeName cn = c.getCommitteeName();
			committeeNames.add(cn);
		}
		
		List<CommitteeName> sortedCNList = 
			CommitteeName.sortByName(committeeNames, ApplicationConstants.ASC);
		model.addAttribute("committeeNames", sortedCNList);
	}
	
	private void populateCommitteeName(final ModelMap model, 
			final Committee committee) {
		CommitteeName committeeName = committee.getCommitteeName();
		model.addAttribute("committeeName", committeeName);
	}
	
	private List<State> populateStates(final ModelMap model, 
			final String locale) {
		List<State> states = State.find(locale);
		model.addAttribute("states", states);
		return states;
	}
	
	private void populateState(final ModelMap model, 
			final State state) {
		model.addAttribute("state", state);		
	}
	
	private List<District> populateDistricts(final ModelMap model, 
			final State state, 
			final String locale) {
		Long stateId = state.getId();
		
		try {
			List<District> districts = 
				District.findDistrictsByStateId(stateId, "name", 
						ApplicationConstants.ASC, locale);
			model.addAttribute("districts", districts);
			return districts;
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<District>();
		
	}
	
	private void populateDistrict(final ModelMap model, 
			final District district) {
		model.addAttribute("district", district);		
	}
	
	private void populateTowns(final ModelMap model, 
			final District district, 
			final String locale) {
		List<Town> towns = Town.find(district, locale);
		model.addAttribute("towns", towns);
	}
	
	private void populateItineraries(final ModelMap model,
			final List<TourItinerary> itineraries) {
		model.addAttribute("itineraries", itineraries);
	}
	
	private void populateItinerariesCount(final ModelMap model,
			final Integer tourItineraryCount) {
		model.addAttribute("tourItineraryCount", tourItineraryCount);
	}
	
	private void populateReporters(final ModelMap model,
			final List<CommitteeReporter> reporters) {
		model.addAttribute("reporters", reporters);
	}
	
	private void populateReportersCount(final ModelMap model,
			final Integer committeeReporterCount) {
		model.addAttribute("committeeReporterCount", committeeReporterCount);
	}
	
	private void populateLanguages(final ModelMap model,
			final String locale) {
		List<Language> languages = new ArrayList<Language>();
		try {
			languages = Language.findAllSortedByPriorityAndName(locale);
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
		model.addAttribute("languages", languages);
	}
	
	/**
	 * The put up options are configured as key-value pairs in CustomParameter.
	 * The template for key formation is:
	 * 		"COMMITTEETOUR_PUT_UP_OPTIONS_" + toUpperCase(workflowName)
	 * 		+ "_" + toUpperCase(USERGROUPTYPE)
	 * 
	 * If the aforementioned key-value pair is not configured for this user,
	 * then return the default configured options. The template is:
	 * 		"COMMITTEETOUR_PUT_UP_OPTIONS_" + toUpperCase(workflowName)
	 * 		+ "_" + "DEFAULT"
	 */
	private List<Status> populateStatuses(final ModelMap model,
			final UserGroup userGroup,
			final String workflowName,
			final String locale) {
		// Prepare the parameters
		String wfName = workflowName.toUpperCase();
		String ugtType = userGroup.getUserGroupType().getType().toUpperCase();
		
		// Retrieve statuses as comma separated string
		String name = "COMMITTEETOUR_PUT_UP_OPTIONS_" 
			+ wfName + "_" + ugtType;
		String options = getCustomParameterValue(name, locale); 
		if(options == null) {
			name = "COMMITTEETOUR_PUT_UP_OPTIONS_DEFAULT";
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
	
	private void populateStatus(final ModelMap model,
			final Status status) {
		model.addAttribute("status", status);
	}
	
	private List<WorkflowActor> populateNextActors(final ModelMap model,
			final UserGroup userGroup, 
			final HouseType houseType, 
			final Status status,
			final String workflowName, 
			final Integer assigneeLevel, 
			final String locale) {
		List<Reference> actors = new ArrayList<Reference>();
		String strHouseType = userGroup.getParameterValue(ApplicationConstants.HOUSETYPE_KEY+"_"+locale);
		HouseType houseType1 = null;
		if(strHouseType != null && !strHouseType.isEmpty()){
			 houseType1 = HouseType.findByName(HouseType.class, strHouseType, locale);
		}else{
			houseType1 = this.getCurrentUsersHouseType(locale);
		}
		List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
		if(houseType1.getType().equals(ApplicationConstants.BOTH_HOUSE)){
			wfActors = WorkflowConfig.findCommitteeTourActors(
					houseType1, userGroup, status, workflowName, 
					assigneeLevel, locale);
		}else{
			 wfActors = WorkflowConfig.findCommitteeTourActors(
					houseType, userGroup, status, workflowName, 
					assigneeLevel, locale);
		}
		
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
			final WorkflowActor wfActor) {
		if(wfActor != null) {
			String id = String.valueOf(wfActor.getId());
			String name = wfActor.getUserGroupType().getName();
			Reference actor = new Reference(id, name);
			model.addAttribute("actor", actor);
		}
	}
	
	/**
	 * Some privileged users such as Speaker & Chairman
	 * are not shown the "Next Actors" drop down.
	 * 
	 * Note that "Next Actors" dropdown is also not shown in the
	 * case when the current actor is the last actor in the
	 * workflow.
	 * 
	 * This method sets the attribute which will be used by
	 * the View to hide the "Next Actors" drop down.
	 */
	private void hideNextActors(final ModelMap model) {
		model.addAttribute("hideNextActors", true);
	}
	
	private void populateRemarks(final ModelMap model, 
			final String remarks) {
		model.addAttribute("remarks", remarks);
	}
	
	private void renderAsReadOnly(ModelMap model) {
		model.addAttribute("renderAsReadOnly", true);
	}
	
	private void populateWorkflowAttributes(final ModelMap model,
			final String fullWorkflowName,
			final Boolean isWorkflowInit,
			final Integer assigneeLevel) {
		model.addAttribute("workflowName", fullWorkflowName);
		model.addAttribute("workflowInit", isWorkflowInit);
		model.addAttribute("assigneeLevel", assigneeLevel);
	}
	
	private void populateHouseType(final ModelMap model,
			final HouseType houseType) {
		model.addAttribute("houseType", houseType);
	}
	
	private void populateUserGroup(final ModelMap model,
			final UserGroup userGroup) {
		model.addAttribute("userGroup", userGroup);
	}
	
	private void populateInternalStatus(final ModelMap model, 
			final Status status) {
		model.addAttribute("internalStatus", status);
	}
	
	//=============== VALIDATIONS ==============
	private void valEmptyAndNull(final CommitteeTour domain, 
			final BindingResult result) {
		// 'committee' SHOULD NOT BE NULL
		if(domain.getCommittee() == null) {
			result.rejectValue("committee", "NotEmpty", 
					"Committee should not be empty");
		}
		
		// 'town' SHOULD NOT BE NULL
		if(domain.getTown() == null) {
			result.rejectValue("town", "NotEmpty", 
					"Town should not be empty");
		}
		
		// 'venueName' SHOULD NOT BE NULL OR EMPTY
		if(domain.getVenueName() == null || domain.getVenueName().isEmpty()) {
			result.rejectValue("venueName", "NotEmpty", 
					"Venue Name should not be empty");
		}
		
		// 'fromDate' SHOULD NOT BE NULL OR EMPTY
		if(domain.getFromDate() == null) {
			result.rejectValue("fromDate", "NotEmpty", 
					"From Date should not be empty");
		}

		// 'toDate' SHOULD NOT BE NULL OR EMPTY
		if(domain.getToDate() == null) {
			result.rejectValue("toDate", "NotEmpty", 
					"To Date should not be empty");
		}
		
		// 'subject' SHOULD NOT BE NULL OR EMPTY
		if(domain.getSubject() == null || domain.getSubject().isEmpty()) {
			result.rejectValue("subject", "NotEmpty", 
					"Subject should not be empty");
		}
	}
	
	/**
	 * 'committee' + 'fromDate' MUST UNIQUELY REPRESENT AN 
	 * 'CommitteeTour' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while creating an instance.
	 */
	private void valInstanceCreationUniqueness(final CommitteeTour domain,
			final BindingResult result) {
		Committee committee = domain.getCommittee();
		Date fromDate = domain.getFromDate();
		String locale = domain.getLocale();
		
		if(committee != null && fromDate != null) {
			CommitteeTour tour = CommitteeTour.find(committee, fromDate, locale);
			if(tour != null) {
				Object[] errorArgs = new Object[] {
						committee.getCommitteeName().getDisplayName(), 
						fromDate};
				
				StringBuffer defaultMessage = new StringBuffer();
				defaultMessage.append("Committee Tour ");
				defaultMessage.append(" already exists for Committee: ");
				defaultMessage.append(errorArgs[0]);
				defaultMessage.append(", From Date: ");
				defaultMessage.append(errorArgs[1]);
				
				result.rejectValue("committee", "DuplicateCommitteeTour", 
						errorArgs, defaultMessage.toString());
			}
		}
	}
	
	/**
	 * 'committee' + 'fromDate' MUST UNIQUELY REPRESENT AN 
	 * 'CommitteeTour' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while updating an instance.
	 */
	private void valInstanceUpdationUniqueness(final CommitteeTour domain,
			final BindingResult result) {
		Committee committee = domain.getCommittee();
		Date fromDate = domain.getFromDate();
		String locale = domain.getLocale();
		
		if(committee != null && fromDate != null) {
			CommitteeTour tour = CommitteeTour.find(committee, fromDate, locale);
			if(tour != null) {
				Long domainId = domain.getId();
				Long tourId = tour.getId();
				if(! domainId.equals(tourId)) {
					Object[] errorArgs = new Object[] {
							committee.getCommitteeName().getDisplayName(), 
							fromDate};
					
					StringBuffer defaultMessage = new StringBuffer();
					defaultMessage.append("Committee Tour ");
					defaultMessage.append(" already exists for Committee: ");
					defaultMessage.append(errorArgs[0]);
					defaultMessage.append(", From Date: ");
					defaultMessage.append(errorArgs[1]);
					
					result.rejectValue("fromDate", "DuplicateCommitteeTour", 
							errorArgs, defaultMessage.toString());
				}
			}
		}
	}
	
	private void valVersionMismatch(final CommitteeTour domain, 
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
	//=============== "UTILITY" METHODS ===============
	private String getCustomParameterValue(final String name,
			final String locale) {
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, name, locale);
		if(parameter != null) {
			return parameter.getValue();
		}
		
		return null;
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
	
	//============= INTERNAL METHODS FOR POPULATING COMMITTEE NAMES AND TYPES
	
	private void populateCommitteeTypesAndNames(ModelMap model, String locale) throws ELSException {
		UserGroup userGroup = null;
		UserGroupType userGroupType = null;
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		if(userGroups != null && ! userGroups.isEmpty()) {
			CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "CIS_ALLOWED_USERGROUPTYPES", "");
			if(cp != null) {
				List<UserGroupType> configuredUserGroupTypes = 
						CommitteeTourController.delimitedStringToUGTList(cp.getValue(), ",", locale);
				
				userGroup = CommitteeTourController.getUserGroup(userGroups, configuredUserGroupTypes, locale);
				userGroupType = userGroup.getUserGroupType();
				model.addAttribute("usergroup", userGroup.getId());
				model.addAttribute("usergroupType", userGroupType.getType());
			}
			else {
				throw new ELSException("CommitteeTourController.populateModule/4", 
						"CIS_ALLOWED_USERGROUPTYPES key is not set as CustomParameter");
			}
		}
		if(userGroup == null || userGroupType == null) {
			model.addAttribute("errorcode","current_user_has_no_usergroups");
		}
		
		// Populate CommitteeTypes and CommitteNames
		Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
		String committeeNameParam = parameters.get(ApplicationConstants.COMMITTEENAME_KEY + "_" + locale);
		if(committeeNameParam != null && ! committeeNameParam.equals("")) {
			List<CommitteeName> committeeNames =
					CommitteeTourController.getCommitteeNames(committeeNameParam, "##", locale);
			List<CommitteeType> committeeTypes = new ArrayList<CommitteeType>();
			for(CommitteeName cn : committeeNames){
				if(!committeeTypes.contains(cn.getCommitteeType())){
					committeeTypes.add(cn.getCommitteeType());
				}
				
			}
			model.addAttribute("committeeNames", committeeNames);
			model.addAttribute("committeeTypes", committeeTypes);
		}
		else {
			throw new ELSException("CommitteeTourController.populateModule/4", 
					"CommitteeName parameter is not set for Username: " + this.getCurrentUser().getUsername());
		}
	}


	private static List<CommitteeName> getCommitteeNames(
			String committeeNameParam, String delimiter, String locale) {
		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
		String cNames[] = committeeNameParam.split(delimiter);
		for(String cName : cNames){
			List<CommitteeName> comNames = 
					CommitteeName.findAllByFieldName(CommitteeName.class, "displayName", cName, "displayName", "asc", locale);
			if(comNames != null && !comNames.isEmpty()){
				committeeNames.addAll(comNames);
			}
		}
		return committeeNames;
	}

	private static UserGroup getUserGroup(List<UserGroup> userGroups,
			List<UserGroupType> configuredUserGroupTypes, String locale) {
		for(UserGroup ug : userGroups) {
			Date todaysDate = new Date();
			if(ug.getActiveFrom().before(todaysDate) && ug.getActiveTo().after(todaysDate)){
				for(UserGroupType ugt : configuredUserGroupTypes) {
					UserGroupType userGroupType = ug.getUserGroupType();
					if(ugt.getId().equals(userGroupType.getId())) {
						return ug;
					}
				}
			}
		}
		return null;
	}

	private static List<UserGroupType> delimitedStringToUGTList(String delimitedUserGroups,
			String delimiter, String locale) {
		List<UserGroupType> userGroupTypes = new ArrayList<UserGroupType>();
		
		String[] strUserGroupTypes = delimitedUserGroups.split(delimiter);
		for(String strUserGroupType : strUserGroupTypes) {
			UserGroupType ugt = UserGroupType.findByType(strUserGroupType, locale);
			userGroupTypes.add(ugt);
		}
		
		return userGroupTypes;
	}
	
}