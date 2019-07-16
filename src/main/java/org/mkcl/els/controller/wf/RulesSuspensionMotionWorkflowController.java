package org.mkcl.els.controller.wf;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BulkApprovalVO;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.mois.RulesSuspensionMotionController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.RulesSuspensionMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workflow/rulessuspensionmotion")
public class RulesSuspensionMotionWorkflowController  extends BaseController {
	
	/** The process service. */
	@Autowired
	private IProcessService processService;
	
	@SuppressWarnings("unused")
	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		/**** Date ****/
		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		if(this.getUserLocale().equals(new Locale("mr","IN")))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
					dateFormat, true));
		}
		else
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),this.getUserLocale());
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
					dateFormat, true));
		}
		/**** Member ****/
		binder.registerCustomEditor(Member.class, new BaseEditor(
				new Member()));		
		/**** Status ****/
		binder.registerCustomEditor(Status.class, new BaseEditor(
				new Status()));
		/**** House Type ****/
		binder.registerCustomEditor(HouseType.class, new BaseEditor(
				new HouseType()));
		/**** Session ****/
		binder.registerCustomEditor(Session.class, new BaseEditor(
				new Session()));
		/**** Device Type ****/
		binder.registerCustomEditor(DeviceType.class, new BaseEditor(
				new DeviceType()));
		/**** Ministry ****/
		binder.registerCustomEditor(Ministry.class, new BaseEditor(
				new Ministry()));
		/**** Sub Department ****/
		binder.registerCustomEditor(SubDepartment.class, new BaseEditor(
				new SubDepartment()));
	}
	
	@RequestMapping(value="supportingmember", method = RequestMethod.GET)
	public String initSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) throws ELSException {
		/**** Workflowdetails ****/
		Long longWorkflowdetails = (Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** RulesSuspensionMotion ****/
		String rulesSuspensionMotionId = workflowDetails.getDeviceId();
		model.addAttribute("rulesSuspensionMotion", rulesSuspensionMotionId);
		RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,Long.parseLong(rulesSuspensionMotionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers = rulesSuspensionMotion.getSupportingMembers();
		Member member = Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					if(i.getApprovedSubject()==null) {
						i.setApprovedSubject(rulesSuspensionMotion.getSubject());
					}
					if(i.getApprovedText()==null) {
						i.setApprovedText(rulesSuspensionMotion.getNoticeContent());
					}		
					// Rules Suspension Date in Supporting Member
					Date approvedAdjourningDate = i.getApprovedAdjourningDate();
					if(approvedAdjourningDate==null) {
						approvedAdjourningDate = rulesSuspensionMotion.getRuleSuspensionDate();
						if(approvedAdjourningDate==null) {
							approvedAdjourningDate = RulesSuspensionMotion.findDefaultRuleSuspensionDateForSession(rulesSuspensionMotion.getSession(), true);
						}
					}
					if(approvedAdjourningDate!=null) {
						model.addAttribute("approvedAdjourningDate", FormaterUtil.formatDateToString(approvedAdjourningDate, ApplicationConstants.SERVER_DATEFORMAT));
						model.addAttribute("formattedApprovedAdjourningDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(approvedAdjourningDate, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", rulesSuspensionMotion.getLocale()));
					}
					model.addAttribute("currentSupportingMember", i.getMember().getId());
					model.addAttribute("domain", i);
					if (i.getDecisionStatus() != null) {
						model.addAttribute("decisionStatus", i.getDecisionStatus().getId());
						model.addAttribute("formattedDecisionStatus", i.getDecisionStatus().getName());
					}
					CustomParameter customParameter = CustomParameter
							.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
					if (customParameter != null) {
						SimpleDateFormat format = new SimpleDateFormat(
								customParameter.getValue());
						model.addAttribute("requestReceivedOnDate", format.format(i.getRequestReceivedOn()));
					}
					break;
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, rulesSuspensionMotion, supportingMembers, locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task", workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId", workflowDetails.getId());
		model.addAttribute("status", workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,
			final RulesSuspensionMotion rulesSuspensionMotion, 
			final List<SupportingMember> supportingMembers,
			final String locale){
		/**** RulesSuspensionMotion Type ****/
		DeviceType rulesSuspensionMotionType = rulesSuspensionMotion.getType();
		if(rulesSuspensionMotionType != null){
			model.addAttribute("rulesSuspensionMotionType", rulesSuspensionMotionType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session = rulesSuspensionMotion.getSession();
		if(session != null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName", rulesSuspensionMotion.getHouseType().getName());
		model.addAttribute("houseType", rulesSuspensionMotion.getHouseType().getType());
		/**** Supporting Members ****/
		List<Member> members = new ArrayList<Member>();
		if(supportingMembers != null){
			for(SupportingMember i : supportingMembers){
				Member selectedMember = i.getMember();
				members.add(selectedMember);
			}
			if(!members.isEmpty()){
				StringBuffer buffer = new StringBuffer();
				for(Member i : members){
					buffer.append(i.getFullnameLastNameFirst() + ",");
				}
				buffer.deleteCharAt(buffer.length()-1);
				model.addAttribute("supportingMembersName", buffer.toString());
			}
		}
		/**** Decision Status ****/
		Status approveStatus =Status
				.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale);
		Status rejectStatus = Status
				.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale);
		List<Status> decisionStatus = new ArrayList<Status>();
		decisionStatus.add(approveStatus);
		decisionStatus.add(rejectStatus);
		model.addAttribute("decisionStatus", decisionStatus);
		/**** Primary Member ****/
		model.addAttribute("primaryMemberName", rulesSuspensionMotion.getPrimaryMember().getFullnameLastNameFirst());
	}

	@RequestMapping(value="supportingmember",method=RequestMethod.PUT)
	public String updateSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,
			@Valid @ModelAttribute("domain") final SupportingMember domain) {
		/**** update supporting member */
		String strMember = request.getParameter("currentSupportingMember");
		String requestReceivedOn = request.getParameter("requestReceivedOnDate");
		if(requestReceivedOn != null && !(requestReceivedOn.isEmpty())){
			CustomParameter customParameter = 
					CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
			if(customParameter != null){
				SimpleDateFormat format = new SimpleDateFormat(customParameter.getValue());
				try {
					domain.setRequestReceivedOn(format.parse(requestReceivedOn));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		Member member = Member.findById(Member.class, Long.parseLong(strMember));
		domain.setMember(member);
		domain.setApprovalDate(new Date());
		domain.merge();
		/**** update workflow details ****/
		String strWorkflowdetails = domain.getWorkflowDetailsId();
		if(strWorkflowdetails != null && !strWorkflowdetails.isEmpty()){
			WorkflowDetails workflowDetails = 
					WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();
			/**** complete the task ****/		 
			String strTaskId = workflowDetails.getTaskId();
			Task task = processService.findTaskById(strTaskId);
			processService.completeTask(task);
			model.addAttribute("task", strTaskId);		
		}
		/**** display message ****/
		model.addAttribute("type", "taskcompleted");
		return "workflow/info";
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String initMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		try {
			/**** Workflowdetails ****/
			Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
			if(longWorkflowdetails==null){
				longWorkflowdetails=Long.parseLong(request.getParameter("workflowdetails"));
			}
			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
			/**** Adding workflowdetails and task to model ****/
			model.addAttribute("workflowdetails",workflowDetails.getId());
			model.addAttribute("workflowstatus",workflowDetails.getStatus());
			model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
			model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
			RulesSuspensionMotion domain = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,Long.parseLong(workflowDetails.getDeviceId()));
			/**** Populate Model ****/
			populateModel(domain,model,request,workflowDetails);	
			/**** Find Latest Remarks ****/
			findLatestRemarksByUserGroup(domain, model, request, workflowDetails);
			return workflowDetails.getForm();
		} catch (ELSException e1) {
			model.addAttribute("error", e1.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
		return "workflow/myTasks/error";
	}
	
	private void populateModel(final RulesSuspensionMotion domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) throws ELSException, ParseException {
		/** getting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
		UserGroupType userGroupTypeObj = UserGroupType.findByType(workflowDetails.getAssigneeUserGroupType(), domain.getLocale());
		CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"RSMOIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
		if(remarksForDecisionAllowed!=null) {
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", remarksForDecisionAllowed.getValue(), ",", domain.getLocale());
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupTypeObj);
			if(isUserGroupAllowed){
				domain.setRemarks(domain.getRemarksAboutDecision());
			} else {
				domain.setRemarks("");
			}
		} else {
			domain.setRemarks("");
		}
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit", request.getParameter("bulkedit"));
		/**** Locale ****/
		String locale=domain.getLocale();
		/**** House Type ****/
		HouseType houseType = domain.getHouseType();
		if(houseType != null) {
			model.addAttribute("formattedHouseType", houseType.getName());
			model.addAttribute("houseTypeType", houseType.getType());
			model.addAttribute("houseType", houseType.getId());
		}		
		/**** Device Type ****/
		DeviceType motionType = domain.getType();
		String formattedMotionType = "";
		if(houseType!=null && houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			formattedMotionType = motionType.getName_lowerhouse();
		} else if(houseType!=null && houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			formattedMotionType = motionType.getName_upperhouse();
		} else {
			formattedMotionType = motionType.getName();
		}
		model.addAttribute("formattedMotionType", formattedMotionType);
		model.addAttribute("motionType", motionType.getId());
		model.addAttribute("selectedMotionType", motionType.getType());
		/**** Session ****/
		Session selectedSession = domain.getSession();
		if(selectedSession != null){
			model.addAttribute("session", selectedSession.getId());
		}else{
			throw new ELSException("RulesSuspensionMotionController.populateCreateIfErrors/3", 
					"session_isnull");
		}
		/**** Session Year ****/
		Integer sessionYear = selectedSession.getYear();
		model.addAttribute("sessionYear", sessionYear);
		/**** Session Type ****/
		SessionType sessionType = selectedSession.getType();
		model.addAttribute("sessionType", sessionType.getId());
		/**** Rotation Order Publishing Date ****/
		Date rotationOrderPubDate = QuestionController.getRotationOrderPublishingDate(selectedSession);
		model.addAttribute("rotationOrderPublishDate", 
				FormaterUtil.getDateFormatter(domain.getLocale()).format(rotationOrderPubDate));		
		if(rotationOrderPubDate == null) {
			throw new ELSException("RulesSuspensionMotionController.populateCreateIfErrors/3", 
					"rotationOrderPubDate is null");
		}
		/**** role ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		/**** UserGroup and UserGroup Type ****/
		String usergroup = workflowDetails.getAssigneeUserGroupId();
		model.addAttribute("usergroup",usergroup);
		UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(usergroup));
		String usergroupType = workflowDetails.getAssigneeUserGroupType();
		model.addAttribute("usergroupType",workflowDetails.getAssigneeUserGroupType());
		model.addAttribute("userGroupName", workflowDetails.getAssigneeUserGroupName());
		model.addAttribute("userName", this.getCurrentUser().getActualUsername());
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		/**** Ministries & SubDepartments ****/
		Date currentDate = new Date();
		if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
			/**** Ministries ****/
			List<Ministry> ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
			model.addAttribute("ministries",ministries);
			List<Ministry> selectedministries=domain.getMinistries();
			if(selectedministries!=null && !selectedministries.isEmpty()){
				model.addAttribute("selectedministries",selectedministries);						
				/**** Sub Departments ****/
				List<SubDepartment> subDepartments=new ArrayList<SubDepartment>();
				for(Ministry m:selectedministries){
					List<SubDepartment> assignedSubDepartments = 
							MemberMinister.findAssignedSubDepartments(m, selectedSession.getEndDate(), locale);
					subDepartments.addAll(assignedSubDepartments);
				}
				model.addAttribute("subDepartments",subDepartments);
				List<SubDepartment> selectedSubDepartments=domain.getSubDepartments();
				if(!selectedSubDepartments.isEmpty()){
					model.addAttribute("selectedSubDepartments",selectedSubDepartments);
				}
			}				
		}
		/**** Member Related Details ****/
		Member primaryMember = domain.getPrimaryMember();
		if(primaryMember != null) {
			model.addAttribute("formattedPrimaryMember", primaryMember.getFullname());
			model.addAttribute("primaryMember", primaryMember.getId());
			Constituency constituency = Member.findConstituency(primaryMember, new Date());
			if(constituency != null){
				model.addAttribute("constituency", constituency.getDisplayName());
			}
		}
		/**** Supporting Members ****/
		/*** Populate Immediate Supporting member***/
		//Populate Supporting Member Names
		String supportingMemberNames = this.populateDelimitedSupportingMemberNames(domain.getSupportingMembers(), domain.getLocale());
		model.addAttribute("supportingMembersName", supportingMemberNames);
		
		//Populate Supporting Members 
		List<SupportingMember> suppMembers = domain.getSupportingMembers();
		List<Member> supportingMembers = new ArrayList<Member>();
		for(SupportingMember sm : suppMembers){
			Member supportingMember = sm.getMember();
			if(supportingMember.isActiveMemberOn(new Date(), locale)){
				supportingMembers.add(supportingMember);
			}
		}
		model.addAttribute("supportingMembers", supportingMembers);			
		//Populate PrimaryMemberName + supportingMemberNames
		String memberNames = domain.getPrimaryMember().getFullname() + "," + supportingMemberNames;
		model.addAttribute("memberNames",memberNames);
		/**** Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		/** populate session dates as possible adjourning dates **/
		if(selectedSession!=null && selectedSession.getId()!=null) {
			List<Date> sessionDates = selectedSession.findAllSessionDates();
			model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", domain.getLocale()));				
		}
		/**** populate adjourning date ****/
		model.addAttribute("selectedRuleSuspensionDate", FormaterUtil.formatDateToString(domain.getRuleSuspensionDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
		model.addAttribute("formattedRuleSuspensionDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getRuleSuspensionDate(), "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", domain.getLocale()));				
		/**** populate Submission Date and Creation date****/
		if(domain.getSubmissionDate()!=null) {
			model.addAttribute("submissionDate", FormaterUtil.formatDateToString(domain.getSubmissionDate(), ApplicationConstants.SERVER_DATETIMEFORMAT));
			model.addAttribute("formattedSubmissionDate", FormaterUtil.formatDateToString(domain.getSubmissionDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, domain.getLocale()));
		}
		if(domain.getCreationDate()!=null) {
			model.addAttribute("creationDate", FormaterUtil.formatDateToString(domain.getCreationDate(), ApplicationConstants.SERVER_DATETIMEFORMAT));
		}
		//Populate createdby
		model.addAttribute("createdBy",domain.getCreatedBy());
		/**** Referenced Motions Starts ****/
		CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
				findByName(CustomParameter.class, "RSMOIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
		if(clubbedReferencedEntitiesVisibleUserGroups != null){
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(UserGroupType.class, clubbedReferencedEntitiesVisibleUserGroups.getValue(), ",", locale);
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
			if(isUserGroupAllowed){
				//populate parent
				if(domain.getParent()!=null){
					model.addAttribute("formattedParentNumber",FormaterUtil.formatNumberNoGrouping(domain.getParent().getNumber(), locale));
					model.addAttribute("parent",domain.getParent().getId());
				}
				
				// Populate clubbed entities
				List<Reference> clubEntityReferences = RulesSuspensionMotionController.populateClubbedEntityReferences(domain, locale);
				model.addAttribute("clubbedMotions",clubEntityReferences);
			}
		}
		/**** Status,Internal Status and recommendation Status ****/
		Status status=domain.getStatus();
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		if(status==null) {
			logger.error("status is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "status_null");
			return;
		}
		model.addAttribute("status",status.getId());
		model.addAttribute("memberStatusType",status.getType());
		if(internalStatus==null) {
			logger.error("internal status is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "internalStatus_null");
			return;
		}
		model.addAttribute("internalStatus",internalStatus.getId());
		model.addAttribute("internalStatusType", internalStatus.getType());
		model.addAttribute("internalStatusPriority", internalStatus.getPriority());
		model.addAttribute("formattedInternalStatus", internalStatus.getName());
		if(recommendationStatus==null) {
			logger.error("recommendation status is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "recommendationStatus_null");
			return;
		}
		model.addAttribute("recommendationStatus",recommendationStatus.getId());
		model.addAttribute("recommendationStatusType",recommendationStatus.getType());
		model.addAttribute("recommendationStatusPriority", recommendationStatus.getPriority());
		model.addAttribute("formattedRecommendationStatus", recommendationStatus.getName());
		if(workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
				|| workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
				|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
			populateInternalStatus(model,domain,domain.getRecommendationStatus(),domain.getLocale());
		} else {
			populateInternalStatus(model,domain,domain.getInternalStatus(),domain.getLocale());
		}
		//Populate Actors
		List<Reference> actors = new ArrayList<Reference>();
		if(workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
				|| workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
				|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
			actors = WorkflowConfig.findRulesSuspensionMotionActorsVO(domain, recommendationStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
		}else{
			actors = WorkflowConfig.findRulesSuspensionMotionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
		}
		model.addAttribute("actors",actors);
		model.addAttribute("internalStatusSelected", internalStatus.getId());
		/**** Bulk Edit ****/
		String bulkedit=request.getParameter("bulkedit");
		if(bulkedit!=null){
			model.addAttribute("bulkedit",bulkedit);
		}else{
			bulkedit=(String) request.getSession().getAttribute("bulkedit");
			if(bulkedit!=null&&!bulkedit.isEmpty()){
				model.addAttribute("bulkedit",bulkedit);
				request.getSession().removeAttribute("bulkedit");
			}
		}
		/**** Start workflow related things ****/
		if(domain.getWorkflowStartedOn()!=null){
			model.addAttribute("workflowStartedOnDate", FormaterUtil.formatDateToString(domain.getWorkflowStartedOn(), ApplicationConstants.SERVER_DATETIMEFORMAT));
		}
		if(domain.getTaskReceivedOn()!=null){
			model.addAttribute("taskReceivedOnDate", FormaterUtil.formatDateToString(domain.getTaskReceivedOn(), ApplicationConstants.SERVER_DATETIMEFORMAT));
		}
		/**** level ****/
		model.addAttribute("level",workflowDetails.getAssigneeLevel());			
		/**** add domain to model ****/
		model.addAttribute("domain",domain);

	}
	
	@SuppressWarnings("rawtypes")
	private void findLatestRemarksByUserGroup(final RulesSuspensionMotion domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails)throws ELSException {
		UserGroupType userGroupType = null;
		String username = this.getCurrentUser().getUsername();
		Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
		List<UserGroup> ugroups = this.getCurrentUser().getUserGroups();
		for(UserGroup ug : ugroups){
			UserGroup usergroup = UserGroup.findActive(credential, ug.getUserGroupType(), domain.getSubmissionDate(), domain.getLocale());
			if(usergroup != null){
				userGroupType = usergroup.getUserGroupType();
				break;
			}
		}
		HouseType houseType = domain.getHouseType();
		CustomParameter customParameter = null;
		if(userGroupType!=null) {
			customParameter = CustomParameter.findByName(CustomParameter.class, "RSMOIS_LATESTREVISION_STARTINGACTOR_"+userGroupType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase(), "");
			if(customParameter != null){
				String strUsergroupType = customParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}else{
				CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "RSMOIS_LATESTREVISION_STARTINGACTOR_DEFAULT"+"_"+houseType.getType().toUpperCase(), "");
				if(defaultCustomParameter != null){
					String strUsergroupType = defaultCustomParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}
			}
		} else {
			CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "RSMOIS_LATESTREVISION_STARTINGACTOR_DEFAULT"+"_"+houseType.getType().toUpperCase(), "");
			if(defaultCustomParameter != null){
				String strUsergroupType = defaultCustomParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}
		}
		Map<String, String[]> requestMap=new HashMap<String, String[]>();			
		requestMap.put("rulesSuspensionMotionId",new String[]{String.valueOf(domain.getId())});
		requestMap.put("locale",new String[]{domain.getLocale()});
		List result=Query.findReport("RSMOIS_LATEST_REVISIONS", requestMap);
		model.addAttribute("latestRevisions",result);
		model.addAttribute("startingActor", userGroupType.getName());
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final RulesSuspensionMotion domain,final BindingResult result) {
		/**** Workflowdetails ****/
		String strWorkflowdetails = (String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails = 
				WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		try {
			RulesSuspensionMotion rulesSuspensionMotion = null;
			if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
				/**** display message ****/
				model.addAttribute("type","taskalreadycompleted");
				return "workflow/info";
			}
			/**** Updating domain ****/
			/**** Binding Supporting Members ****/
			String[] strSupportingMembers=request.getParameterValues("selectedSupportingMembers");
			List<SupportingMember> members=new ArrayList<SupportingMember>();
			if(domain.getId()!=null){
				if(rulesSuspensionMotion==null) {
					rulesSuspensionMotion=RulesSuspensionMotion.findById(RulesSuspensionMotion.class,domain.getId());
				}			
				members=rulesSuspensionMotion.getSupportingMembers();
			}
			if(strSupportingMembers!=null){
				if(strSupportingMembers.length>0){
					List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
					for(String i:strSupportingMembers){
						SupportingMember supportingMember=null;
						Member member=Member.findById(Member.class, Long.parseLong(i));
						/**** If supporting member is already present then do nothing ****/
						for(SupportingMember j:members){
							if(j.getMember().getId().equals(member.getId())){
								supportingMember=j;
								supportingMembers.add(supportingMember);
								break;
							}
						}					
					}
					domain.setSupportingMembers(supportingMembers);
				}
			}
			/***** To retain the clubbed rules suspension motions when moving through workflow ****/
			String[] strClubbedEntities= request.getParameterValues("clubbedEntities");
			if(strClubbedEntities!=null){
				if(strClubbedEntities.length>0){
					List<ClubbedEntity> clubbedEntities=new ArrayList<ClubbedEntity>();
					for(String i:strClubbedEntities){
						ClubbedEntity clubbedEntity=ClubbedEntity.findById(ClubbedEntity.class, Long.parseLong(i));
						clubbedEntities.add(clubbedEntity);
					}
					domain.setClubbedEntities(clubbedEntities);
				}
			}
			/***** To retain the parent rules suspension motion when moving through workflow ****/
			String strParentRulesSuspensionMotion = request.getParameter("parent");
			if(strParentRulesSuspensionMotion!=null) {
				if(!strParentRulesSuspensionMotion.isEmpty()) {
					RulesSuspensionMotion parentRulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, Long.parseLong(strParentRulesSuspensionMotion));
					domain.setParent(parentRulesSuspensionMotion);
				}
			}

			/** setting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
			UserGroupType userGroupTypeObj = UserGroupType.findByType(userGroupType, domain.getLocale());
			CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"RSMOIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
			if(remarksForDecisionAllowed!=null) {
				List<UserGroupType> userGroupTypes = 
						this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", remarksForDecisionAllowed.getValue(), ",", domain.getLocale());
				Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupTypeObj);
				if(isUserGroupAllowed){
					domain.setRemarksAboutDecision(domain.getRemarks());
				}
			}
			/**** updating various dates including submission date and creation date ****/
			/**** creation date ****/
			String strCreationDate=request.getParameter("setCreationDate");
			if(strCreationDate!=null){
				domain.setCreationDate(FormaterUtil.formatStringToDate(strCreationDate, ApplicationConstants.SERVER_DATETIMEFORMAT));
			}
			/**** submission date ****/
			String strSubmissionDate=request.getParameter("setSubmissionDate");		
			if(strSubmissionDate!=null){
				domain.setSubmissionDate(FormaterUtil.formatStringToDate(strSubmissionDate, ApplicationConstants.SERVER_DATETIMEFORMAT));
			}
			/**** Edited On,Edited By and Edited As is set ****/
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
			
			/**** required in case when domain is updated with start of new workflow before completion of current workflow ****/
			String endFlagForCurrentWorkflow = domain.getEndFlag();				
			
			if(domain.getDrafts()==null) {
				if(rulesSuspensionMotion==null) {
					rulesSuspensionMotion=RulesSuspensionMotion.findById(RulesSuspensionMotion.class,domain.getId());
				}
				domain.setDrafts(rulesSuspensionMotion.getDrafts());
			}
			
			String currentDeviceTypeWorkflowType = null;
			Workflow workflowFromUpdatedStatus = domain.findWorkflowFromStatus();
			
			//String sendbackactor=request.getParameter("sendbackactor");
			if(workflowFromUpdatedStatus!=null) {
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}			
			
			performAction(domain, request);	
			domain.merge();
			
			/**** Complete Task ****/
			String endflag="";
			Map<String,String> properties=new HashMap<String, String>();
			String level="";
			String nextUserGroupType="";
			String nextuser = request.getParameter("actor");
			if(nextuser!=null){
				if(!nextuser.isEmpty()){
					String[] temp=nextuser.split("#");
					nextUserGroupType=temp[1];
					properties.put("pv_user",temp[0]);
					level=temp[2];
				}
			}
			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
				endflag = endFlagForCurrentWorkflow;
			} else {
				endflag = domain.getEndFlag();
			}	
			properties.put("pv_deviceId",String.valueOf(domain.getId()));
			properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));		
			properties.put("pv_endflag", endflag);
			String strTaskId=workflowDetails.getTaskId();
			Task task=processService.findTaskById(strTaskId);
			processService.completeTask(task,properties);
			if(endflag!=null && !endflag.isEmpty()){
				if(endflag.equals("continue")){					
					ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
					Task newtask=processService.getCurrentTask(processInstance);
					/**** Workflow Detail entry made only if its not the end of workflow ****/
					UserGroupType nextUserGroupTypeObj = UserGroupType.findByType(nextUserGroupType, locale.toString());
					try {
						WorkflowDetails.create(domain, newtask, nextUserGroupTypeObj, currentDeviceTypeWorkflowType, level);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();		
			/**** display message ****/
			model.addAttribute("type","taskcompleted");
			return "workflow/info";
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		} catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "** There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
		return "workflow/rulessuspensionmotion/"+userGroupType;				
	}	
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/advancedbulkapproval",method=RequestMethod.GET)
	public String getAdvancedBulkApproval(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		try{
			/**** Request Params ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strMotionType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strRuleSuspensionDate = request.getParameter("ruleSuspensionDate");
			String strLocale = locale.toString();
			String assignee = this.getCurrentUser().getActualUsername();
			String strItemsCount = null;
			CustomParameter itemsCountParameter = CustomParameter.findByName(CustomParameter.class, "ADVANCED_BULKAPPROVAL_ITEM_COUNT", "");
			if(itemsCountParameter != null){
				strItemsCount = itemsCountParameter.getValue();
			}
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
			String strUserGroupType = null;
			String strUsergroup = null;
			UserGroup usergroup = null;
			if(userGroups != null && !userGroups.isEmpty()){
				CustomParameter customParameter = CustomParameter.
						findByName(CustomParameter.class,"RSMOIS_ALLOWED_USERGROUPTYPES", "");
				if(customParameter != null){
					String allowedUserGroups = customParameter.getValue(); 
					for(UserGroup i : userGroups){
						UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
						if(ug != null){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								strUsergroup = String.valueOf(i.getId());
								strUserGroupType = i.getUserGroupType().getType();
								usergroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUsergroup));
								break;
							}
						}
					}
				}
			}	
			
			if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
	        }else{
	        	model.addAttribute("type",request.getSession().getAttribute("type"));
	            request.getSession().removeAttribute("type");
	        }
			
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strMotionType!=null&&!(strMotionType.isEmpty())){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(customParameter!=null){
					String server=customParameter.getValue();
					if(server.equals("TOMCAT")){
						try {
							strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
							strMotionType = new String(strMotionType.getBytes("ISO-8859-1"),"UTF-8");							
						}
						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				strHouseType = request.getSession().getAttribute("houseType").toString();
				strSessionType = request.getSession().getAttribute("sessionType").toString();
				strSessionYear = request.getSession().getAttribute("sessionYear").toString();
				strMotionType = request.getSession().getAttribute("deviceType").toString();
				strWorkflowSubType = request.getSession().getAttribute("workflowSubType").toString();
				strStatus = request.getSession().getAttribute("status").toString();
				if(request.getSession().getAttribute("ruleSuspensionDate") != null){
					strRuleSuspensionDate = request.getSession().getAttribute("ruleSuspensionDate").toString();
				}
			}
	
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strMotionType!=null&&!(strMotionType.isEmpty())
					&&strStatus!=null&&!(strStatus.isEmpty())
					&&strUsergroup!=null&&!(strUsergroup.isEmpty())
					&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
					&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){
					
					model.addAttribute("status", strStatus);
					model.addAttribute("usergroup", usergroup.getId());
					// Populate Roles
					/**
					 * Rules:
					 * a. RSMOIS roles starts with RSMOIS_, MEMBER_
					 * b. Any user will have single role per device type
					 * c. Any user can have multiple roles limited to one role per device type
					 */
					Set<Role> roles = this.getCurrentUser().getRoles();
					for(Role i : roles) {
						if(i.getType().startsWith("MEMBER_")) {
							model.addAttribute("role", i.getType());
							break;
						}
						else if(i.getType().startsWith("RSMOIS_")) {
							model.addAttribute("role", i.getType());
							break;
						}
					}
					/**** List of Statuses ****/
					List<Status> internalStatuses = new ArrayList<Status>();
					HouseType houseType = HouseType.
							findByFieldName(HouseType.class,"name",strHouseType, strLocale);
					DeviceType motionType = DeviceType.
							findByFieldName(DeviceType.class,"name",strMotionType,strLocale);
					Status internalStatus = Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority = CustomParameter.
							findByName(CustomParameter.class,motionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null 
							&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.
								findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus != null){
							internalStatuses = Status.
									findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE + INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)****/
					else if(deviceTypeHouseTypeInternalStatusUsergroup !=null ){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
					}
					/**** RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses", internalStatuses);
					model.addAttribute("selectedWorkflowStatus", internalStatus.getName());
					model.addAttribute("workflowSubType", strWorkflowSubType);
					Date ruleSuspensionDate = null;
					if(strRuleSuspensionDate != null && !strRuleSuspensionDate.isEmpty()){
						ruleSuspensionDate=FormaterUtil.
								 formatStringToDate(strRuleSuspensionDate, ApplicationConstants.DB_DATEFORMAT);
						 model.addAttribute("ruleSuspensionDate", ruleSuspensionDate);
					}
					/**** Workflow Details ****/
					List<WorkflowDetails> workflowDetails = WorkflowDetails.
								findAllForRulesSuspensionMotions(strHouseType, strSessionType, strSessionYear,
										strMotionType, ApplicationConstants.MYTASK_PENDING, strWorkflowSubType,
										ruleSuspensionDate, assignee, strItemsCount, strLocale);
					/**** Populating Bulk Approval VOs ****/
					List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
					NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
					int counter = 0;
					for(WorkflowDetails i:workflowDetails){
						BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
						RulesSuspensionMotion motion=RulesSuspensionMotion.findById(RulesSuspensionMotion.class,Long.parseLong(i.getDeviceId()));
						{
							bulkApprovalVO.setId(String.valueOf(i.getId()));
							bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));	
							
							bulkApprovalVO.setFormattedAdjourningDate(FormaterUtil.formatDateToStringUsingCustomParameterFormat(motion.getRuleSuspensionDate(), "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", motion.getLocale()));
							
							Map<String, String[]> parameters = new HashMap<String, String[]>();
							parameters.put("locale", new String[]{locale.toString()});
							parameters.put("motionId", new String[]{motion.getId().toString()});
							List clubbedNumbers = org.mkcl.els.domain.Query.findReport("RSMOIS_GET_CLUBBEDNUMBERS", parameters);
							if(clubbedNumbers != null && !clubbedNumbers.isEmpty() && clubbedNumbers.get(0) != null){
								bulkApprovalVO.setFormattedClubbedNumbers(clubbedNumbers.get(0).toString());
							}
							
							if(motion.getNumber()!=null){
								bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
							}else{
								bulkApprovalVO.setDeviceNumber("-");
							}
							bulkApprovalVO.setDeviceType(motion.getType().getName());
							bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
							if(motion.getRevisedSubject() != null && !motion.getRevisedSubject().equals("")){
								bulkApprovalVO.setSubject(motion.getRevisedSubject());
							}else{
								bulkApprovalVO.setSubject(motion.getSubject());
							}
							if(motion.getRevisedNoticeContent() != null && !motion.getRevisedNoticeContent().isEmpty()){
								bulkApprovalVO.setBriefExpanation(motion.getRevisedNoticeContent());
							}else{
								bulkApprovalVO.setBriefExpanation(motion.getNoticeContent());
							}
							
							if(motion.getRemarks()!=null&&!motion.getRemarks().isEmpty()){
								bulkApprovalVO.setLastRemark(motion.getRemarks());
							}else{
								bulkApprovalVO.setLastRemark("-");
							}
							bulkApprovalVO.setLastDecision(motion.getInternalStatus().getName());
							bulkApprovalVO.setLastRemarkBy(motion.getEditedAs());	
							bulkApprovalVO.setCurrentStatus(i.getStatus());
							bulkapprovals.add(bulkApprovalVO);
							
						
						if(counter == 0){
							model.addAttribute("level", motion.getLevel());
							counter++;
						}
					}
					
					model.addAttribute("bulkapprovals", bulkapprovals);
					if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
						model.addAttribute("motionId",bulkapprovals.get(0).getDeviceId());				
					}
				}
					model.addAttribute("deviceType", motionType.getId());
			}
			return "workflow/rulessuspensionmotion/advancedbulkapproval";	
		}catch (ELSException ee) {
			model.addAttribute("error", ee.getParameter());
			model.addAttribute("type", "error");
			return "workflow/info";
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
			model.addAttribute("type", "error");
			return "workflow/info";
		}
	}
	
	@RequestMapping(value="/advancedbulkapproval",method=RequestMethod.POST)
	public String advancedBulkApproval(final HttpServletRequest request,
			final Locale locale,
			final RedirectAttributes redirectAttributes,
			final ModelMap model) throws ELSException{
		String listSize = request.getParameter("motionlistSize");
		RulesSuspensionMotion tempMotion  = null;
		if(listSize != null && !listSize.isEmpty()){
			for(int i =0; i<Integer.parseInt(listSize);i++){
				String id = request.getParameter("motionId"+i);
				String subject = request.getParameter("subject"+i);
				String motionText = request.getParameter("motionText"+i);
				String actor = request.getParameter("actor"+i);
				String internalStatus = request.getParameter("internalStatus"+i);
				String remark = request.getParameter("remark"+i);
				String workflowDetailsId = request.getParameter("workflowDetailsId"+i);
				Long wrkflowId = Long.parseLong(workflowDetailsId);
				WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class,wrkflowId);
				String strChecked = request.getParameter("chk"+workflowDetailsId);
				if(strChecked != null && !strChecked.isEmpty() && Boolean.parseBoolean(strChecked)){
					RulesSuspensionMotion motion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,Long.parseLong(wfDetails.getDeviceId()));
					tempMotion = motion;
					
					if(subject != null && !subject.isEmpty()){
						motion.setRevisedSubject(subject);
					}
					if(motionText != null && !motionText.isEmpty()){
						motion.setRevisedNoticeContent(motionText);
					}
					
					if(remark != null && !remark.isEmpty()){
						motion.setRemarks(remark);
						motion.setRemarksAboutDecision(motion.getRemarks());
					}					
					
					if(internalStatus!=null && !internalStatus.isEmpty()) {	//decision is selected meaning that statuses and flow should be updated accordingly
						/**** Update Internal Status and Recommendation Status ****/
						Status intStatus = Status.findById(Status.class, Long.parseLong(internalStatus));
						if(!intStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_DISCUSS) 
								&& !intStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_SENDBACK))
						{
							motion.setInternalStatus(intStatus);
							motion.setRecommendationStatus(intStatus);
							motion.setEndFlag("continue");
						}else{
							motion.setRecommendationStatus(intStatus);
							motion.setEndFlag("continue");
						}
						/**** Update Actor ****/
						if(actor == null || actor.isEmpty()){
							actor = motion.getActor();
							String[] temp = actor.split("#");
							actor = temp[1];
						}
						String level = request.getParameter("motionLevel");
						if(level == null || level.isEmpty()){
							level = motion.getLevel();
						}
						String[] temp = actor.split("#");
						motion.setActor(actor);
						motion.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						motion.setLevel(temp[2]);
						/**** Complete Task ****/
						Map<String,String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId", String.valueOf(motion.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
						properties.put("pv_user", temp[0]);
						properties.put("pv_endflag", motion.getEndFlag());
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task, properties);	
						if(motion.getEndFlag() != null && !motion.getEndFlag().isEmpty()
								&& motion.getEndFlag().equals("continue")){
							/**** Create New Workflow Details ****/
							ProcessInstance processInstance = 
									processService.findProcessInstanceById(task.getProcessInstanceId());
							Workflow workflowFromUpdatedStatus = null;
							try {
								Status iStatus = motion.getInternalStatus();								
								workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
					
							} catch(ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
								model.addAttribute("type", "error");
								
							}
							Task newtask = processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = null;
							try {
								workflowDetails2 = WorkflowDetails.create(motion,newtask,usergroupType,workflowFromUpdatedStatus.getType(),level);
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
							motion.setWorkflowDetailsId(workflowDetails2.getId());
							motion.setTaskReceivedOn(new Date());								
						}
						/**** Update Old Workflow Details ****/
						wfDetails.setStatus("COMPLETED");
						wfDetails.setInternalStatus(motion.getInternalStatus().getName());
						wfDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
						wfDetails.setCompletionTime(new Date());
						wfDetails.setAdjourningDate(motion.getRuleSuspensionDate());
						wfDetails.setDecisionInternalStatus(motion.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(motion.getRecommendationStatus().getName());
						wfDetails.merge();																
						performAction(motion, request);		
						
					} else if(request.getParameter("preserveDecisions")!=null 
								&& Boolean.parseBoolean(request.getParameter("preserveDecisions"))) { //decision is preserved meaning that statuses and flow should be preserved accordingly
						
						motion.setEndFlag("continue");
						/**** Find next actor for the preserved decision ****/
						String strUserGroup = request.getParameter("usergroup");
						if(strUserGroup!=null) {
							UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
							List<Reference> actors = WorkflowConfig.findRulesSuspensionMotionActorsVO(motion,motion.getInternalStatus(),userGroup,Integer.parseInt(motion.getLevel()),locale.toString());
							if(actors!=null && !actors.isEmpty()) {
								actor = actors.get(0).getId();
								if(actor==null || actor.isEmpty()) {
									actor = motion.getActor();
									motion.setEndFlag("end"); //as further no actor is available
								}
							}
						}					
						/**** Update Actor ****/
						String[] temp = actor.split("#");
						motion.setActor(actor);
						motion.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						String level = temp[2];
						motion.setLevel(level);						
						motion.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						
						/**** Complete Task ****/
						Map<String,String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId", String.valueOf(motion.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
						properties.put("pv_user", temp[0]);
						properties.put("pv_endflag", motion.getEndFlag());
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task, properties);	
						if(motion.getEndFlag() != null && !motion.getEndFlag().isEmpty()
								&& motion.getEndFlag().equals("continue")){
							/**** Create New Workflow Details ****/
							ProcessInstance processInstance = 
									processService.findProcessInstanceById(task.getProcessInstanceId());
							Workflow workflowFromUpdatedStatus = null;
							try {
								Status iStatus = motion.getInternalStatus();								
								workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
					
							} catch(ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
								model.addAttribute("type", "error");
								
							}
							Task newtask = processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = null;
							try {
								workflowDetails2 = WorkflowDetails.create(motion,newtask,usergroupType,workflowFromUpdatedStatus.getType(),level);
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
							motion.setWorkflowDetailsId(workflowDetails2.getId());
							motion.setTaskReceivedOn(new Date());								
						}
						/**** Update Old Workflow Details ****/
						wfDetails.setStatus("COMPLETED");
						wfDetails.setInternalStatus(motion.getInternalStatus().getName());
						wfDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
						wfDetails.setCompletionTime(new Date());
						wfDetails.setAdjourningDate(motion.getRuleSuspensionDate());
						wfDetails.setDecisionInternalStatus(motion.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(motion.getRecommendationStatus().getName());
						wfDetails.merge();																
						performAction(motion, request);
					}
						
					/**** Update Motion ****/
					motion.setEditedOn(new Date());
					motion.setEditedBy(this.getCurrentUser().getActualUsername());
					motion.setEditedAs(wfDetails.getAssigneeUserGroupName());
					motion.merge();
				}					
			}
		}
		
		if(tempMotion != null){
			request.getSession().setAttribute("houseType", tempMotion.getHouseType().getName());
			request.getSession().setAttribute("sessionType", tempMotion.getSession().getType().getSessionType());
			request.getSession().setAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(tempMotion.getSession().getYear(), locale.toString()));
			request.getSession().setAttribute("deviceType", tempMotion.getType().getName());
			request.getSession().setAttribute("workflowSubType", tempMotion.getInternalStatus().getType());
			
		}
		String ruleSuspensionDate = request.getParameter("ruleSuspensionDate");
		if(ruleSuspensionDate != null && !ruleSuspensionDate.isEmpty()){
			request.getSession().setAttribute("strRuleSuspensionDate", ruleSuspensionDate);
		}
		String status = request.getParameter("status");
		if(status != null && !status.isEmpty()){
			request.getSession().setAttribute("status", status);
		}
		redirectAttributes.addFlashAttribute("type", "success");
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/workflow/rulessuspensionmotion/advancedbulkapproval";
        return returnUrl;
	}
	
	private void populateInternalStatus(final ModelMap model,
			final RulesSuspensionMotion domain, final Status putupOptionsStatus,
			final String locale) {
		try{
			List<Status> internalStatuses = new ArrayList<Status>();
			DeviceType deviceType = domain.getType();
			HouseType houseType = domain.getHouseType();
			String actor = domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus = CustomParameter.
						findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else{
				String usergroupType = actor.split("#")[1];
				/**** Final Approving Authority(Final Status) ****/
				CustomParameter finalApprovingAuthority = null;
				Workflow workflow = Workflow.findByStatus(putupOptionsStatus, locale);
				if(workflow!=null) {
					finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					if(finalApprovingAuthority==null) {
						finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					}					
				}				
				if(finalApprovingAuthority==null) {
					finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
				}	
				CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority != null && finalApprovingAuthority.getValue().contains(usergroupType)) {
					CustomParameter finalApprovingAuthorityStatus = null;
					if(workflow!=null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(finalApprovingAuthorityStatus==null) {
							finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						}						
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
					}
					if(finalApprovingAuthorityStatus != null){
						internalStatuses=Status.
								findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					}
				}/**** RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				}/**** RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				}	
				/**** RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
				else if(deviceTypeUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
				}		
			}		
			/**** Internal Status****/
			model.addAttribute("internalStatuses", internalStatuses);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void performAction(final RulesSuspensionMotion domain, HttpServletRequest request) throws ELSException {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION)){
			performActionOnAdmission(domain, request);
		} 
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECTION)){
			performActionOnRejection(domain, request);
		} 
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING)){
			performActionOnClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_CLUBBING)){
			performActionOnClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_NAMECLUBBING)){
			performActionOnNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_NAMECLUBBING)){
			performActionOnNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingRejectionPostAdmission(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_UNCLUBBING)){
			performActionOnUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_UNCLUBBING)){
			performActionOnUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnAdmissionDueToReverseClubbing(domain);
		}
	}
	
	private void performActionOnAdmission(RulesSuspensionMotion domain, HttpServletRequest request) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject());	
		}		
		if(domain.getRevisedNoticeContent() == null 
				|| domain.getRevisedNoticeContent().isEmpty()){			
			domain.setRevisedNoticeContent(domain.getNoticeContent());
		}
		domain.simpleMerge(); 
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		RulesSuspensionMotion.updateClubbing(domain);
	}
	
	private void performActionOnRejection(RulesSuspensionMotion domain, HttpServletRequest request) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject());	
		}		
		if(domain.getRevisedNoticeContent() == null 
				|| domain.getRevisedNoticeContent().isEmpty()){			
			domain.setRevisedNoticeContent(domain.getNoticeContent());
		}
		domain.simpleMerge(); 
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);		
		RulesSuspensionMotion.updateClubbing(domain);
	}
	
	private void performActionOnClubbing(RulesSuspensionMotion domain) throws ELSException {
		
		RulesSuspensionMotion.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnClubbingRejection(RulesSuspensionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		RulesSuspensionMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}

	private void performActionOnNameClubbing(RulesSuspensionMotion domain) throws ELSException {
		
		RulesSuspensionMotion.updateClubbing(domain);

		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			domain.setActor(null);
			domain.setLocalizedActorName("");
			domain.setWorkflowDetailsId(null);
			domain.setLevel("1");
			domain.setWorkflowStarted("NO");
			domain.setEndFlag(null);		
		}		
	}
	
	private void performActionOnNameClubbingRejection(RulesSuspensionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		RulesSuspensionMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);	
	}

	private void performActionOnClubbingPostAdmission(RulesSuspensionMotion domain) throws ELSException {
		
		RulesSuspensionMotion.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnClubbingRejectionPostAdmission(RulesSuspensionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		RulesSuspensionMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnclubbing(RulesSuspensionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		RulesSuspensionMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnclubbingRejection(RulesSuspensionMotion domain) throws ELSException {
		/** Back to clubbed state as it was before sending for unclubbing **/
		domain.setInternalStatus(domain.getParent().getInternalStatus());
		domain.setRecommendationStatus(domain.getParent().getInternalStatus());
		domain.simpleMerge();
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnAdmissionDueToReverseClubbing(RulesSuspensionMotion domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION, domain.getLocale());
		Workflow processWorkflow = Workflow.findByStatus(admitStatus, domain.getLocale());
		UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, domain.getLocale());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		WorkflowDetails.startProcessAtGivenLevel(domain, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, domain.getLocale());
	}

}