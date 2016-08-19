package org.mkcl.els.controller.wf;

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
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.mois.AdjournmentMotionController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
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

@Controller
@RequestMapping("/workflow/adjournmentmotion")
public class AdjournmentMotionWorkflowController  extends BaseController {
	
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
		/**** AdjournmentMotion ****/
		String adjournmentMotionId = workflowDetails.getDeviceId();
		model.addAttribute("adjournmentMotion", adjournmentMotionId);
		AdjournmentMotion adjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class,Long.parseLong(adjournmentMotionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers = adjournmentMotion.getSupportingMembers();
		Member member = Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					if(i.getApprovedSubject()==null) {
						i.setApprovedSubject(adjournmentMotion.getSubject());
					}
					if(i.getApprovedText()==null) {
						i.setApprovedText(adjournmentMotion.getNoticeContent());
					}					
					Date approvedAdjourningDate = i.getApprovedAdjourningDate();
					if(approvedAdjourningDate==null) {
						approvedAdjourningDate = adjournmentMotion.getAdjourningDate();
						if(approvedAdjourningDate==null) {
							approvedAdjourningDate = AdjournmentMotion.findDefaultAdjourningDateForSession(adjournmentMotion.getSession());
						}
					}
					if(approvedAdjourningDate!=null) {
						model.addAttribute("approvedAdjourningDate", FormaterUtil.formatDateToString(approvedAdjourningDate, ApplicationConstants.SERVER_DATEFORMAT));
						model.addAttribute("formattedApprovedAdjourningDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(approvedAdjourningDate, "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", adjournmentMotion.getLocale()));
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
		populateSupportingMember(model, adjournmentMotion, supportingMembers, locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task", workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId", workflowDetails.getId());
		model.addAttribute("status", workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,
			final AdjournmentMotion adjournmentMotion, 
			final List<SupportingMember> supportingMembers,
			final String locale){
		/**** AdjournmentMotion Type ****/
		DeviceType adjournmentMotionType = adjournmentMotion.getType();
		if(adjournmentMotionType != null){
			model.addAttribute("adjournmentMotionType", adjournmentMotionType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session = adjournmentMotion.getSession();
		if(session != null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName", adjournmentMotion.getHouseType().getName());
		model.addAttribute("houseType", adjournmentMotion.getHouseType().getType());
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
		model.addAttribute("primaryMemberName", adjournmentMotion.getPrimaryMember().getFullnameLastNameFirst());
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
			AdjournmentMotion domain=AdjournmentMotion.findById(AdjournmentMotion.class,Long.parseLong(workflowDetails.getDeviceId()));
			/**** Populate Model ****/
			populateModel(domain,model,request,workflowDetails);			
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
	
	private void populateModel(final AdjournmentMotion domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) throws ELSException, ParseException {
		/** getting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
		UserGroupType userGroupTypeObj = UserGroupType.findByType(workflowDetails.getAssigneeUserGroupType(), domain.getLocale());
		CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"AMOIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
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
			throw new ELSException("AdjournmentMotionController.populateCreateIfErrors/3", 
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
			throw new ELSException("AdjournmentMotionController.populateCreateIfErrors/3", 
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
		model.addAttribute("usergroupType",usergroupType);
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		/**** Ministries & SubDepartments ****/
		Date currentDate = new Date();
		if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
			List<Ministry> ministries = Ministry.
					findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, domain.getLocale());
			model.addAttribute("ministries",ministries);				
			//Populate Ministry
			Ministry ministry = domain.getMinistry();
			if(ministry != null) {
				model.addAttribute("formattedMinistry", ministry.getName());
				model.addAttribute("ministrySelected", ministry.getId());
			}
			//Populate SubDepartments
			if(ministry != null) {
				List<SubDepartment> subDepartments = 
						MemberMinister.findAssignedSubDepartments(ministry, selectedSession.getEndDate(), domain.getLocale());
				model.addAttribute("subDepartments",subDepartments);
			}			
			//populate subdepartment
			SubDepartment subDepartment = domain.getSubDepartment();
			if(subDepartment != null) {
				model.addAttribute("subDepartmentSelected",subDepartment.getId());
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
			model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", domain.getLocale()));				
		}
		/**** populate adjourning date ****/
		model.addAttribute("selectedAdjourningDate", FormaterUtil.formatDateToString(domain.getAdjourningDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
		model.addAttribute("formattedAdjourningDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getAdjourningDate(), "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", domain.getLocale()));				
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
				findByName(CustomParameter.class, "AMOIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
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
				//populate referenced entity
				if(domain.getReferencedAdjournmentMotion()!=null){
					Reference referencedEntityReference = AdjournmentMotionController.populateReferencedEntityAsReference(domain, locale);
					model.addAttribute("referencedMotion",referencedEntityReference);
				}
				// Populate clubbed entities
				List<Reference> clubEntityReferences = AdjournmentMotionController.populateClubbedEntityReferences(domain, locale);
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
			actors = WorkflowConfig.findAdjournmentMotionActorsVO(domain, recommendationStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
		}else{
			actors = WorkflowConfig.findAdjournmentMotionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
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
		/**** mail & timer process variables ****/
		//default values for process variables. can set conditionally for given actor here.
		model.addAttribute("pv_mailflag", "off");
		model.addAttribute("pv_reminderflag", "off");
		model.addAttribute("pv_timerflag", "off");
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final AdjournmentMotion domain,final BindingResult result) {
		/**** Workflowdetails ****/
		String strWorkflowdetails = (String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails = 
				WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		try {
			AdjournmentMotion adjournmentMotion = null;
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
				if(adjournmentMotion==null) {
					adjournmentMotion=AdjournmentMotion.findById(AdjournmentMotion.class,domain.getId());
				}			
				members=adjournmentMotion.getSupportingMembers();
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
			/***** To retain the clubbed adjournment motions when moving through workflow ****/
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
			/***** To retain the parent adjournment motion when moving through workflow ****/
			String strParentAdjournmentMotion = request.getParameter("parent");
			if(strParentAdjournmentMotion!=null) {
				if(!strParentAdjournmentMotion.isEmpty()) {
					AdjournmentMotion parentAdjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, Long.parseLong(strParentAdjournmentMotion));
					domain.setParent(parentAdjournmentMotion);
				}
			}
			/***** To retain the referenced adjournment motion when moving through workflow ****/
			String refAdjournmentMotion = request.getParameter("referencedAdjournmentMotion");
			if(refAdjournmentMotion != null && !refAdjournmentMotion.isEmpty()){
				ReferencedEntity refAdjournmentMotionEntity = ReferencedEntity.findById(ReferencedEntity.class, Long.parseLong(refAdjournmentMotion));
				domain.setReferencedAdjournmentMotion(refAdjournmentMotionEntity);
			}
			/** setting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
			UserGroupType userGroupTypeObj = UserGroupType.findByType(userGroupType, domain.getLocale());
			CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"AMOIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
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
			
			/**** added by dhananjayb.. required in case when domain is updated with start of new workflow before completion of current workflow ****/
			String endFlagForCurrentWorkflow = domain.getEndFlag();				
			
			if(domain.getDrafts()==null) {
				if(adjournmentMotion==null) {
					adjournmentMotion=AdjournmentMotion.findById(AdjournmentMotion.class,domain.getId());
				}
				domain.setDrafts(adjournmentMotion.getDrafts());
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
			properties.put("pv_timerflag", "off");
			properties.put("pv_mailflag", "off");	
			String strTaskId=workflowDetails.getTaskId();
			Task task=processService.findTaskById(strTaskId);
			processService.completeTask(task,properties);
			if(endflag!=null){
				if(!endflag.isEmpty()){
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
		return "workflow/adjournmentmotion/"+userGroupType;				
	}	
	
	private void populateInternalStatus(final ModelMap model,
			final AdjournmentMotion domain, final Status putupOptionsStatus,
			final String locale) {
		try{
			List<Status> internalStatuses = new ArrayList<Status>();
			DeviceType deviceType = domain.getType();
			HouseType houseType = domain.getHouseType();
			String actor = domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus = CustomParameter.
						findByName(CustomParameter.class,"ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,"ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
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
				CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class, "ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority != null && finalApprovingAuthority.getValue().contains(usergroupType)) {
					CustomParameter finalApprovingAuthorityStatus = null;
					if(workflow!=null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"ADJOURNMENTMOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(finalApprovingAuthorityStatus==null) {
							finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"ADJOURNMENTMOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						}						
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"ADJOURNMENTMOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
					}
					if(finalApprovingAuthorityStatus != null){
						internalStatuses=Status.
								findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					}
				}/**** ADJOURNMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				}/**** ADJOURNMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				}	
				/**** ADJOURNMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
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
	
	private void performAction(final AdjournmentMotion domain, HttpServletRequest request) throws ELSException {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)){
			performActionOnAdmission(domain, request);
		} 
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECTION)){
			performActionOnRejection(domain, request);
		} 
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING)){
			performActionOnClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_CLUBBING)){
			performActionOnClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_NAMECLUBBING)){
			performActionOnNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_NAMECLUBBING)){
			performActionOnNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingRejectionPostAdmission(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_UNCLUBBING)){
			performActionOnUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_UNCLUBBING)){
			performActionOnUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnAdmissionDueToReverseClubbing(domain);
		}
	}
	
	private void performActionOnAdmission(AdjournmentMotion domain, HttpServletRequest request) throws ELSException {
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
		AdjournmentMotion.updateClubbing(domain);
	}
	
	private void performActionOnRejection(AdjournmentMotion domain, HttpServletRequest request) throws ELSException {
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
		AdjournmentMotion.updateClubbing(domain);
	}
	
	private void performActionOnClubbing(AdjournmentMotion domain) throws ELSException {
		
		AdjournmentMotion.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejection(AdjournmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		AdjournmentMotion.unclub(domain, domain.getLocale());
		
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

	private void performActionOnNameClubbing(AdjournmentMotion domain) throws ELSException {
		
		AdjournmentMotion.updateClubbing(domain);

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
	
	private void performActionOnNameClubbingRejection(AdjournmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		AdjournmentMotion.unclub(domain, domain.getLocale());
		
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

	private void performActionOnClubbingPostAdmission(AdjournmentMotion domain) throws ELSException {
		
		AdjournmentMotion.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejectionPostAdmission(AdjournmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		AdjournmentMotion.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbing(AdjournmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		AdjournmentMotion.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbingRejection(AdjournmentMotion domain) throws ELSException {
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
	
	private void performActionOnAdmissionDueToReverseClubbing(AdjournmentMotion domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION, domain.getLocale());
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
