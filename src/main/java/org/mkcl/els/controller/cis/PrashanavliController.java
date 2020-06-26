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
import org.mkcl.els.common.vo.MasterVO;
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
import org.mkcl.els.domain.Prashnavali;
import org.mkcl.els.domain.PrashnavaliInformation;
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
@RequestMapping("/prashnavali")
public class PrashanavliController extends GenericController<Prashnavali> {

	
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
			final Prashnavali domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		this.commonPopulateEdit(model, domain, request);	
	}

	@Override
	protected void preValidateCreate(final Prashnavali domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.setCommittee(domain, request);
		this.setPrashnavaliInfos(domain, request);
		
		this.preValidate(domain, result, request);
	}

	@Override
	protected void customValidateCreate(final Prashnavali domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		
	}
	
	@Override
	protected void populateEdit(final ModelMap model, 
			final Prashnavali domain,
			final HttpServletRequest request) {
		this.commonPopulateEdit(model, domain, request);
		String strUserGroup = request.getParameter("userGroup");
		UserGroup userGroup = null;
		if(strUserGroup == null){
			Long userGroupId = (Long)request.getSession().getAttribute("userGroup");
			if(userGroupId != null){
				userGroup = UserGroup.findById(UserGroup.class, userGroupId);
				request.getSession().removeAttribute("userGroup");
				
				model.addAttribute("userGroup", userGroup);
			}else{
				model.addAttribute("userGroup", this.getUserGroup(request, this.getCurrentUser().getUserGroups(), domain.getLocale()));
			}
		}else{
			userGroup = UserGroup.findById(UserGroup.class, new Long(strUserGroup));
			model.addAttribute("userGroup", userGroup);
		}
		
		String strHouseType = request.getParameter("houseType");
		HouseType houseType = null;
		if(strHouseType == null){
			Long houseTypeId = (Long)request.getSession().getAttribute("houseType");
			if(houseTypeId != null){
				houseType = UserGroup.findById(UserGroup.class, houseTypeId);
				request.getSession().removeAttribute("houseType");
				model.addAttribute("houseType", houseType);
			}else{
				model.addAttribute("houseType", domain.getHouseType());
			}
		}else{
			houseType = HouseType.findById(HouseType.class, new Long(strHouseType));
			model.addAttribute("houseType", houseType);		
		}
		
		// If the status is equal to COMMITTEETOUR_CREATED, 
		// then show the workflow specific attributes
		// (viz, Put up for, Next actor, Remarks, etc)
		Status status = domain.getStatus();
		if(status != null) {
			Integer statusPriority = status.getPriority();
			model.addAttribute("currentStatusPriority", statusPriority);
			
			this.populateInternalStatus(model, status);
			this.populateStatus(model, domain.getStatus());
			
			if(status.getType().equals(
					ApplicationConstants.PRASHNAVALI_CREATED)) {
				this.initWorkflow(model, domain);
			}
		}
	}
	
	//----------------------------internal helpers-------------------------------
	private void populateInternalStatus(final ModelMap model, 
			final Status status) {
		model.addAttribute("internalStatus", status);
	}
	
	@Override
	protected String modifyEditUrlPattern(final String newUrlPattern,
			final HttpServletRequest request, 
			final ModelMap model, 
			final String locale) {
		Status recommendAdmission = 
			Status.findByType(
					ApplicationConstants.PRASHNAVALI_RECOMMEND_ADMISSION, 
					locale);
		Integer recommendAdmissionPriority = recommendAdmission.getPriority();
		
		Integer currentStatusPriority = (Integer) model.get("currentStatusPriority");
		
		// If the currentStatusPriority is greater than or equal to 
		// recommendAdmissionPriority then it means that the request is in
		// workflow. Hence, render workflow specific pages for the 
		// actors instead of showing edit page.
		if(currentStatusPriority.compareTo(recommendAdmissionPriority) >= 0) {
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			UserGroup userGroup = 
				this.getUserGroup(request, userGroups, locale);
			String ugt = userGroup.getUserGroupType().getType();
			String returnURL = null;
			String strStatus = request.getParameter("status");
			Status status = null;
			if(strStatus == null){
				status = (Status)model.get("internalStatus");
			}else{
				status = Status.findByType(strStatus, locale);
			}
			Status admissionStatus = Status.findByType(ApplicationConstants.PRASHNAVALI_FINAL_ADMISSION, locale);
			Status rejectionStatus = Status.findByType(ApplicationConstants.PRASHNAVALI_FINAL_REJECTION, locale);
			
			if(status != null && (!status.getType().equals(ApplicationConstants.PRASHNAVALI_CREATED)
					&& (status.getPriority() < admissionStatus.getPriority()
							|| status.getPriority() < rejectionStatus.getPriority())
							&& ugt.equals(ApplicationConstants.ASSISTANT))){
				returnURL = newUrlPattern;
			}else{
				
				@SuppressWarnings("unchecked")
				List<Status> statuses = (List<Status>) model.get("statuses");
				if(statuses == null || (statuses != null && statuses.isEmpty())){
					statuses = new ArrayList<Status>();
					model.addAttribute("statuses", statuses);
				}
				returnURL = ApplicationConstants.PRASHNAVALI_WORKFLOW_URL + "/" + ugt;
			}			
			return returnURL;
		}
		
		// By default show edit page
		return newUrlPattern;
	}

	@Override
	protected void preValidateUpdate(final Prashnavali domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.setCommittee(domain, request);
		this.setPrashnavaliInfos(domain, request);
		this.preValidate(domain, result, request);
	}
	
	private void preValidate(final Prashnavali domain,
			final BindingResult result, 
			final HttpServletRequest request){
		request.getSession().setAttribute("userGroup", this.getUserGroup(request, this.getCurrentUser().getUserGroups(), domain.getLocale()).getId());
		request.getSession().setAttribute("houseType", domain.getHouseType().getId());
	}
	@Override
	protected void customValidateUpdate(final Prashnavali domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		
	}
	
	@Override
	protected void populateAfterUpdate(final ModelMap model, 
			final Prashnavali domain,
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
				this.startPrashnavaliProcess(domain, 
						request, authUser, userGroup, houseType, locale);
			}
			// Else If wfInit is 'false' then proceed the workflow
			else if(wfInit != null && wfInit.equals("false")) {
				String wfName = this.getWorkflowName(status);
				this.proceedPrashnavaliProcess(domain, 
						request, userGroup, houseType, wfName, locale);
			}
		}
	}	
	
	@RequestMapping(value="{id}/view", method=RequestMethod.GET)
	public String view(final ModelMap model, 
			@PathVariable("id") final Long id,
			final Locale localeObj) {
		String locale = localeObj.toString();
		
		Prashnavali prashnavali = Prashnavali.findById(Prashnavali.class, id);
		model.addAttribute("id", prashnavali.getId());
		model.addAttribute("prashnavaliName", prashnavali.getPrashnavaliName());
		model.addAttribute("committeeMember", prashnavali.getCommitteeMember());
		model.addAttribute("questions", prashnavali.getQuestionAnswers());
		model.addAttribute("createDate", FormaterUtil.formatDateToString(prashnavali.getCreateDate(), ApplicationConstants.SERVER_DATEFORMAT, locale));

		return "prashnavali/view";
	}

	
	@RequestMapping(value="/info/{id}/delete", method=RequestMethod.DELETE)
	public String deleteTourItinerary(final @PathVariable("id") Long id) {
	    PrashnavaliInformation pInfo = PrashnavaliInformation.findById(PrashnavaliInformation.class, id);
	    pInfo.remove();
	    return "info";
	}
	
	private void setPrashnavaliInfos(final Prashnavali domain,
			final HttpServletRequest request) {
		
		String strCreateDate = request.getParameter("createDateHid");
		Date createDate = null;
		if(strCreateDate != null && !strCreateDate.isEmpty()){
			createDate = FormaterUtil.formatStringToDate(strCreateDate, ApplicationConstants.SERVER_DATEFORMAT, ApplicationConstants.STANDARD_LOCALE);
		}
		
		
		List<PrashnavaliInformation> questionInformation = new ArrayList<PrashnavaliInformation>();
		
		Integer questionSize =
			Integer.parseInt(request.getParameter("questionSize"));
		for(int i = 1; i <= questionSize; i++) {
			String strQuestion = request.getParameter("question" + i);
			String strAnswer = request.getParameter("answer" + i);
			
			if(strQuestion != null && ! strQuestion.isEmpty()) {
				PrashnavaliInformation qAns = new PrashnavaliInformation();

				qAns.setQuestion(strQuestion);				 
				qAns.setAnswer(strAnswer);
				
				String strId = request.getParameter("prasInfoId" + i);
				if(strId != null && ! strId.isEmpty()){
					Long id = Long.parseLong(strId);
					qAns.setId(id);
				}

				String locale = request.getParameter("prasInfoLocale" + i);
				if(locale != null && ! locale.isEmpty()){
					qAns.setLocale(locale);
				}
				
				String strVersion = 
					request.getParameter("prasInfoVersion" + i);
				if(strVersion != null && ! strVersion.isEmpty()){
					Long version = Long.parseLong(strVersion);
					qAns.setVersion(version);
				}
				
				questionInformation.add(qAns);
			}
		}
		
		if(domain.getCreateDate() == null){
			if(createDate != null){
				domain.setCreateDate(createDate);
			}else{
				domain.setCreateDate(new Date());
			}
		}
		
		domain.setQuestionAnswers(questionInformation);
	}
	
	
	//==========================================
	// Common Internal Methods
	//==========================================
	
	private WorkflowDetails proceedPrashnavaliProcess(
			final Prashnavali domain,
			final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final String workflowName,
			final String locale) {
		// STEP 1: Get current actors' WorkflowDetails
		String deviceId = String.valueOf(domain.getId());
		String domainIds = String.valueOf(domain.getId());
		Committee committee = domain.getCommittee();
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
			this.proceedProcess(nextWFActor, houseType, taskId, committee,  locale);
		
		// STEP 4: If there happens to be a nextActor, create WorkflowDetails
		// for him by passing the above acquired task
		if(newTask != null) {
			Status status = this.getStatus(request);
			String urlPattern = 
				ApplicationConstants.PRASHNAVALI_WORKFLOW_START_URL;
			this.createNextActorWorkflowDetails(domain, request, newTask, 
					userGroup, houseType, status, assigneeLevel, 
					urlPattern, locale);
		}
		
		return wfDetails;
	}
	
	private WorkflowDetails startPrashnavaliProcess(
			final Prashnavali domain,
			final HttpServletRequest request,
			final AuthUser authUser,
			final UserGroup userGroup,
			final HouseType houseType,
			final String locale) {
		Status status = this.getStatus(request);
		Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
		String urlPattern = 
			ApplicationConstants.PRASHNAVALI_WORKFLOW_START_URL;
		Committee committee = domain.getCommittee();
		WorkflowDetails wfDetails = this.createInitWorkflowDetails(domain, 
				request, authUser, userGroup, houseType, status, 
				assigneeLevel, urlPattern, locale);
		
		Task task = this.startProcess(request, userGroup, houseType, committee, assigneeLevel, locale);
		
		this.createNextActorWorkflowDetails(domain, request, task, 
				userGroup, houseType, status, assigneeLevel, 
				urlPattern, locale);
		
		return wfDetails;
	}
	
	private WorkflowDetails createInitWorkflowDetails(
			final Prashnavali domain,
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
		wfDetails.setDeviceId(domain.getId().toString());
		wfDetails.setHouseType(houseType.getName());
		
		// Misc parameters
		// TODO Not set as yet: form
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.COMMITTEE);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	private WorkflowActor getNextActor(final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final Integer assigneeLevel,
			final String locale) {
		WorkflowActor wfActor = null;
		
		Boolean isExcluded = isThisUserExcludedFromSelectingNextActorInWorkflow(userGroup, locale);
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
			PRASHNAVALI_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW;
		String value = getCustomParameterValue(name, "");
		
		String[] ugtTypes = tokenize(value, ","); 
		String ugtType = userGroup.getUserGroupType().getType();
		for(String ugtt : ugtTypes) {
			if(ugtt.equals(ugtType)) {
				return true;
			}
		}
		
		return false;
	}
	
	private WorkflowDetails createNextActorWorkflowDetails(
			final Prashnavali domain,
			final HttpServletRequest request,
			final Task task,
			final UserGroup currentActorUserGroup,
			final HouseType houseType,
			final Status status,
			final Integer currentActorLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		Committee committee = domain.getCommittee();
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
			this.getUserGroup(nextActor, houseType, committee, locale);
		UserGroupType nextUGT = nextUserGroup.getUserGroupType();
		wfDetails.setAssignee(task.getAssignee());
		wfDetails.setAssigneeUserGroupType(nextUGT.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(nextUserGroup.getId()));
		wfDetails.setAssigneeUserGroupName(nextUGT.getName());
		wfDetails.setAssigneeLevel(String.valueOf(nextActor.getLevel()));
		if(domain.getPrashnavaliName() != null){
			wfDetails.setSubject(domain.getPrashnavaliName());
		}
		// Domain parameters
		// CommitteeTour can be arranged for multiple committees.
		// Besides one committee can have multiple tours. Hence,
		// committeeId as well as committeeTourId must be 
		// captured so as to uniquely identify a WorkflowDetail.
		// Not applicable parameters: deviceType, deviceNumber
		// deviceOwner, internalStatus, recommendationStatus, sessionType
		// sessionYear, remarks, subject, text, groupNumber, file
		wfDetails.setDeviceId(String.valueOf(domain.getId()));
		wfDetails.setDomainIds(String.valueOf(domain.getId()));
		wfDetails.setHouseType(houseType.getName());
		
		// Misc parameters
		// TODO Not set as yet: form
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.COMMITTEE);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	private UserGroup getUserGroup(final WorkflowActor wfActor, 
			final HouseType houseType,
			final Committee committee,
			final String locale) {
		List<UserGroup> userGroups = getUserGroups(wfActor, locale);		
		UserGroup userGroup = getEligibleUserGroup(userGroups, houseType, committee, true, locale);
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
			final Committee committee,
			final Integer assigneeLevel,
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
	
	private User getUser(final WorkflowActor wfActor, 
			final HouseType houseType,
			final Committee committee,
			final String locale) {
		UserGroup userGroup = getUserGroup(wfActor, houseType, committee, locale);
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
					houseType, committee, locale);
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
	
	private String getWorkflowInit(final HttpServletRequest request) {
		return request.getParameter("workflowInit");
	}
	
	private HouseType getHouseType(final Prashnavali domain,
			final UserGroup userGroup,
			final String locale) {
		String BOTH_HOUSE = ApplicationConstants.BOTH_HOUSE;

		HouseType pHouseType = domain.getHouseType();
		if(pHouseType != null){
			if(!pHouseType.getType().equalsIgnoreCase(BOTH_HOUSE)) {
				return pHouseType;
			}
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
	
	private void commonPopulateEdit(final ModelMap model, 
			final Prashnavali domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		
		if(domain.getQuestionAnswers() != null){
			model.addAttribute("questionSize", domain.getQuestionAnswers().size());
		}else{
			model.addAttribute("questionSize", 0);
		}
		
		HouseType hsType = null;
		String strHouseType = this.getCurrentUser().getHouseType();
		if(strHouseType != null && !strHouseType.isEmpty()){
			try{
				hsType = HouseType.findByType(strHouseType, locale);
			}catch(Exception e){
				logger.error("error", e);
			}
			
			if(hsType == null){
				hsType = HouseType.findById(HouseType.class, new Long(strHouseType));
			}
		}
		
		domain.setHouseType(hsType);		
		model.addAttribute("houseTypeId", hsType.getId());
		model.addAttribute("houseTypeType", hsType.getType());
		
		model.addAttribute("questions", domain.getQuestionAnswers());
		
		if(domain.getCreateDate() != null){
			model.addAttribute("crDate", FormaterUtil.formatDateToString(domain.getCreateDate(), 
					ApplicationConstants.SERVER_DATEFORMAT));
		}else{
			model.addAttribute("crDate", FormaterUtil.formatDateToString(new Date(), 
					ApplicationConstants.SERVER_DATEFORMAT));
		}
		
		if(domain.getStatus() != null){
			model.addAttribute("status", domain.getStatus().getId());
			model.addAttribute("statusType", domain.getStatus().getType());
		}
		
		if(domain.getInternalStatus() != null){
			model.addAttribute("internalStatus", domain.getInternalStatus().getId());
			model.addAttribute("internalStatusType", domain.getInternalStatus().getType());
		}

		if(domain.getRecommendationStatus() != null){
			model.addAttribute("recommendationStatus", domain.getRecommendationStatus().getId());
			model.addAttribute("recommendationStatusType", domain.getRecommendationStatus().getType());
		}
		
		
		String strCommitteeName = request.getParameter("committeeName");
		CommitteeName committeeName = null;
		if(strCommitteeName != null && !strCommitteeName.isEmpty()){
			 committeeName = CommitteeName.findById(CommitteeName.class, Long.parseLong(strCommitteeName));
			 Committee committee=Committee.findByFieldName(Committee.class, "committeeName", committeeName, locale);
			 domain.setCommittee(committee);
		}else{
			 Committee committee = domain.getCommittee();
			 committeeName = committee.getCommitteeName();
			 
		}
		
		if(committeeName != null){
			model.addAttribute("committeeDisplayName", committeeName.getDisplayName());
			model.addAttribute("committeName",committeeName.getId());
		}
		
		this.populateCommitteeNames(model, locale);
		
	}
	
	private void initWorkflow(final ModelMap model, 
			final Prashnavali domain) {
		String locale = domain.getLocale();
		if(domain.getStatus().getType().equals(
				ApplicationConstants.PRASHNAVALI_CREATED)) {
			AuthUser authUser = this.getCurrentUser();
			String workflowName = ApplicationConstants.PRASHNAVALI_RECOMMEND_ADMISSION;
			
			UserGroup userGroup = this.getWorkflowInitiator(authUser, workflowName, locale);
			HouseType houseType = this.getCurrentUsersHouseType(locale);
			if(userGroup != null) {
				// STEP 1: Populate Statuses & Status
				List<Status> statuses = this.populateStatuses(model, userGroup, workflowName, locale);
				Status status = statuses.get(0);
				this.populateStatus(model, status);
				
				// STEP 2: Populate Actors & Actor
				Integer assigneeLevel = ApplicationConstants.WORKFLOW_START_LEVEL;
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
	
	private void populateHouseType(final ModelMap model,
			final HouseType houseType) {
		model.addAttribute("houseType", houseType);
	}
	
	private void populateUserGroup(final ModelMap model,
			final UserGroup userGroup) {
		model.addAttribute("userGroup", userGroup);
	}
	
	private void populateWorkflowAttributes(final ModelMap model,
			final String fullWorkflowName,
			final Boolean isWorkflowInit,
			final Integer assigneeLevel) {
		model.addAttribute("workflowName", fullWorkflowName);
		model.addAttribute("workflowInit", isWorkflowInit);
		model.addAttribute("assigneeLevel", assigneeLevel);
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
	
	private List<WorkflowActor> populateNextActors(final ModelMap model,
			final UserGroup userGroup, 
			final HouseType houseType, 
			final Status status,
			final String workflowName, 
			final Integer assigneeLevel, 
			final String locale) {
		List<Reference> actors = new ArrayList<Reference>();
		List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
		/*** As the committees flow may contain cross housetype users like undersecretary of assembly
		 *  can be under secretary of council committee ***/
		String strHouseType = userGroup.getParameterValue(ApplicationConstants.HOUSETYPE_KEY+"_"+locale);
		HouseType userHouseType = HouseType.findByName(strHouseType, locale);
		if(userHouseType != null && userHouseType.getType().equals(ApplicationConstants.BOTH_HOUSE)){
			 wfActors = WorkflowConfig.findPrashnavaliActors(
					userHouseType, userGroup, status, workflowName, 
					assigneeLevel, locale);
		}else{
			 wfActors = WorkflowConfig.findPrashnavaliActors(
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
	private String getFullWorkflowName(final Status status) {
		String wfName = getWorkflowName(status);
		String fullWfName = wfName + "_workflow";
		return "prashnavali" + fullWfName;
	}
	
	private String getWorkflowName(final Status status) {
		String statusType = status.getType();
		String[] tokens = tokenize(statusType, "_");
		int length = tokens.length;
		
		String workflowName = tokens[length - 1];
		if(workflowName.equals(ApplicationConstants.PRASHNAVALI_SENDBACK)) {
			return ApplicationConstants.ADMISSIONPRASHNAVALI;
		}
		
		return workflowName;
	}
	
	private String getWorkflowSubType(final Status status) {
		return status.getType();
	}
	
	private void populateStatus(final ModelMap model,
			final Status status) {
		model.addAttribute("status", status);
	}
	private List<Status> populateStatuses(final ModelMap model,
			final UserGroup userGroup,
			final String workflowName,
			final String locale) {
		// Prepare the parameters
		String wfName = workflowName.toUpperCase();
		String ugtType = userGroup.getUserGroupType().getType().toUpperCase();
		
		// Retrieve statuses as comma separated string
		String name = "PRASHNAVALI_PUT_UP_OPTIONS_" + wfName + "_" + ugtType;
		String options = getCustomParameterValue(name, ""); 
		if(options == null) {
			name = "PRASHNAVALI_PUT_UP_OPTIONS_" + wfName + "_DEFAULT";
			options = getCustomParameterValue(name, "");
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
	
	private HouseType getCurrentUsersHouseType(final String locale) {
		AuthUser authUser = this.getCurrentUser();
		String houseTypeType = authUser.getHouseType();
		HouseType houseType = HouseType.findByType(houseTypeType, locale);
		return houseType;
	}
	
	private UserGroup getWorkflowInitiator(final AuthUser authUser,
			final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase(); 
		String name = "PRASHANAVALI_" + wfName + "_INITIATOR";
		
		// ugtType could be comma separated userGroupTypes
		// in cases where there could be more than one initiator
		String ugtType = getCustomParameterValue(name, "");
		String[] ugtTypes = tokenize(ugtType, ",");
		
		List<UserGroup> userGroups = authUser.getUserGroups();
		UserGroup userGroup = this.getUserGroup(userGroups, ugtTypes);
		return userGroup;
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
				UserGroup userGroup = UserGroup.findById(UserGroup.class, ug.getId());
				return userGroup;
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
	
	@RequestMapping(value="prashnavaliprocess", method=RequestMethod.GET)
	public String prashnavaliProcessor(final ModelMap model,
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
		Prashnavali domain = Prashnavali.findById(Prashnavali.class, 
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
		if(wfDetails != null){
			this.populateWorkflowStatus(model, wfDetails.getStatus());
		}
		
		// STEP 5: Return View
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		
		return "workflow/" + urlPattern + "/" + ugtType;
	}
	
	private void populateWorkflowStatus(ModelMap model, String status){
		model.addAttribute("workflowStatus", status);
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
	
	/**
	 * Once the PUT request is processed for "REQUEST FOR TOUR" 
	 * workflow, this is the client redirect URL.
	 */
	@RequestMapping(
			value="prashnavaliprocess/processed/{workflowDetailsId}", 
			method=RequestMethod.GET)
	public String rendererForRequestForTour(HttpServletRequest request, final ModelMap model,
			@PathVariable("workflowDetailsId") final Long workflowDetailsId,
			final Locale localeObj) {
		String locale = localeObj.toString();
		WorkflowDetails wfDetails = 
			WorkflowDetails.findById(WorkflowDetails.class, workflowDetailsId);
		
		HouseType houseType = this.getHouseType(wfDetails);
		UserGroup userGroup = this.getUserGroup(wfDetails);
		
		// STEP 1: Populate domain
		String domainId = wfDetails.getDomainIds();
		Prashnavali domain = Prashnavali.findById(Prashnavali.class, 
				Long.valueOf(domainId));
		model.addAttribute("domain", domain);
		this.commonPopulateEdit(model, domain, request);
		
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
		
		
		// STEP 6: Render as Read Only. Since the task is completed,
		// 		   the User must not be allowed to perform any modifications
		this.renderAsReadOnly(model);
		
		// STEP 7: Return View
		String urlPattern = wfDetails.getUrlPattern();
		String ugtType = userGroup.getUserGroupType().getType();
		return "workflow/" + urlPattern + "/" + ugtType;
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
	
	
	private void renderAsReadOnly(ModelMap model) {
		model.addAttribute("renderAsReadOnly", true);
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
	//==========================================
	//	WORKFLOW METHODS
	//==========================================
	
	@SuppressWarnings("rawtypes")
	@Override
	protected <E extends BaseDomain> void customInitBinder(Class clazz,
			WebDataBinder binder) {
		super.customInitBinder(Prashnavali.class, binder);
		
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
	
	private Status getStatus(final HttpServletRequest request) {
		String strStatusId = request.getParameter("status");
		if(strStatusId != null) {
			Long statusId = Long.valueOf(strStatusId);
			Status status = Status.findById(Status.class, statusId); 
			return status;
		}
		return null;
	}
	
	/**
	 * The usergrouptypes allowed for a workflow are configured as key-value
	 * pairs in CustomParameter. The template for key formation is:
	 * 		"PRASHNAVALI_" + toUpperCase(WORKFLOWNAME) + 
	 * 		"_ALLOWED_USERGROUPTYPES"
	 * 
	 * If the aforementioned key-value pair is not configured for this workflow,
	 * then return the default configured usergrouptypes.
	 */
	private String[] getUserGroupTypeTypes(final String workflowName,
			final String locale) {
		String wfName = workflowName.toUpperCase();
		String name = "PRASHNAVALI_" + wfName + "_ALLOWED_USERGROUPTYPES";
		String value = getCustomParameterValue(name, "");
		if(value == null) {
			return getDefaultUserGroupTypeTypes(locale);
		}
		String[] ugtTypes = tokenize(value, ",");
		return ugtTypes;
	}
	
	private String[] getDefaultUserGroupTypeTypes(final String locale) {
		String name = "PRASHNAVALI_ALLOWED_USERGROUPTYPES";
		String value = getCustomParameterValue(name, "");
		String[] ugtTypes = tokenize(value, ",");
		return ugtTypes;
	}
	//=============== "UTILITY" METHODS ===============
		private String getCustomParameterValue(final String name,
				final String locale) {
			CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, name, locale);
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
		
		
		private void populateCommitteeTypesAndNames(ModelMap model, String locale) throws ELSException {
			UserGroup userGroup = null;
			UserGroupType userGroupType = null;
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			if(userGroups != null && ! userGroups.isEmpty()) {
				CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "CIS_ALLOWED_USERGROUPTYPES", "");
				if(cp != null) {
					List<UserGroupType> configuredUserGroupTypes = 
							PrashanavliController.delimitedStringToUGTList(cp.getValue(), ",", locale);
					
					userGroup = PrashanavliController.getUserGroup(userGroups, configuredUserGroupTypes, locale);
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
						PrashanavliController.getCommitteeNames(committeeNameParam, "##", locale);
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
		
		private void setCommittee(final Prashnavali domain,
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
}