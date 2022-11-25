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
import org.mkcl.els.controller.prois.ProprietyPointController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
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
@RequestMapping("/workflow/proprietypoint")
public class ProprietyPointWorkflowController  extends BaseController {
	
	/** The process service. */
	@Autowired
	private IProcessService processService;
	
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
		/**** ProprietyPoint ****/
		String proprietyPointId = workflowDetails.getDeviceId();
		model.addAttribute("proprietyPoint", proprietyPointId);
		ProprietyPoint proprietyPoint = ProprietyPoint.findById(ProprietyPoint.class,Long.parseLong(proprietyPointId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers = proprietyPoint.getSupportingMembers();
		Member member = Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					if(i.getApprovedSubject()==null) {
						i.setApprovedSubject(proprietyPoint.getSubject());
					}
					if(i.getApprovedText()==null) {
						i.setApprovedText(proprietyPoint.getPointsOfPropriety());
					}	
					Date approvedAdjourningDate = i.getApprovedAdjourningDate();
					if(approvedAdjourningDate==null) {
						approvedAdjourningDate = proprietyPoint.getProprietyPointDate();
						if(approvedAdjourningDate==null) {
							approvedAdjourningDate = ProprietyPoint.findDefaultProprietyPointDateForSession(proprietyPoint.getSession(), true);
						}
					}
					if(approvedAdjourningDate!=null) {
						model.addAttribute("approvedAdjourningDate", FormaterUtil.formatDateToString(approvedAdjourningDate, ApplicationConstants.SERVER_DATEFORMAT));
						model.addAttribute("formattedApprovedAdjourningDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(approvedAdjourningDate, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", proprietyPoint.getLocale()));
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
		populateSupportingMember(model, proprietyPoint, supportingMembers, locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task", workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId", workflowDetails.getId());
		model.addAttribute("status", workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,
			final ProprietyPoint proprietyPoint, 
			final List<SupportingMember> supportingMembers,
			final String locale){
		/**** ProprietyPoint Type ****/
		DeviceType proprietyPointType = proprietyPoint.getDeviceType();
		if(proprietyPointType != null){
			model.addAttribute("proprietyPointType", proprietyPointType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session = proprietyPoint.getSession();
		if(session != null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName", proprietyPoint.getHouseType().getName());
		model.addAttribute("houseType", proprietyPoint.getHouseType().getType());
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
		model.addAttribute("primaryMemberName", proprietyPoint.getPrimaryMember().getFullnameLastNameFirst());
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
			ProprietyPoint domain=ProprietyPoint.findById(ProprietyPoint.class,Long.parseLong(workflowDetails.getDeviceId()));
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
	
	private void populateModel(final ProprietyPoint domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) throws ELSException, ParseException {
		/**** clear remarks ****/
		domain.setRemarks("");
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
		DeviceType deviceType = domain.getDeviceType();
		String formattedDeviceType = "";
		if(houseType!=null && houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			formattedDeviceType = deviceType.getName_lowerhouse();
		} else if(houseType!=null && houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			formattedDeviceType = deviceType.getName_upperhouse();
		} else {
			formattedDeviceType = deviceType.getName();
		}
		model.addAttribute("formattedDeviceType", formattedDeviceType);
		model.addAttribute("deviceType", deviceType.getId());
		model.addAttribute("selectedDeviceType", deviceType.getType());
		/**** Session ****/
		Session selectedSession = domain.getSession();
		if(selectedSession != null){
			model.addAttribute("session", selectedSession.getId());
		}else{
			throw new ELSException("ProprietyPointController.populateCreateIfErrors/3", 
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
			throw new ELSException("ProprietyPointController.populateCreateIfErrors/3", 
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
				model.addAttribute("formattedMinistry", ministry.getDropdownDisplayName());
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
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
				|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
			if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getAdmissionNumber()));
			} else {
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
			}
		} else {
			if(domain.getNumber()!=null){
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
			}
		}		
		/**** Admission Number ****/
		if(domain.getAdmissionNumber()!=null){
			model.addAttribute("formattedAdmissionNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getAdmissionNumber()));
		}			
		/** populate session dates as possible propriety point dates **/
		if(selectedSession!=null && selectedSession.getId()!=null) {
			List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
			model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));				
		}
		/**** populate propriety point date ****/
		model.addAttribute("selectedProprietyPointDate", FormaterUtil.formatDateToString(domain.getProprietyPointDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
		model.addAttribute("formattedProprietyPointDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getProprietyPointDate(), "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));
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
		/**** Referenced Propriety Points Starts ****/
		CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
				findByName(CustomParameter.class, "PROIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
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
//				if(domain.getReferencedProprietyPoint()!=null){
//					Reference referencedEntityReference = ProprietyPointController.populateReferencedEntityAsReference(domain, locale);
//					model.addAttribute("referencedProprietyPoint",referencedEntityReference);
//				}
				// Populate clubbed entities
				List<Reference> clubEntityReferences = ProprietyPointController.populateClubbedEntityReferences(domain, locale);
				model.addAttribute("clubbedProprietyPoints",clubEntityReferences);
			}
		}
		/**** Status,Internal Status and recommendation Status ****/
		Status status=domain.getStatus();
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		if(status==null) {
			logger.error("status is not set for this propriety point having id="+domain.getId()+".");
			model.addAttribute("errorcode", "status_null");
			return;
		}
		model.addAttribute("status",status.getId());
		model.addAttribute("memberStatusType",status.getType());
		if(internalStatus==null) {
			logger.error("internal status is not set for this propriety point having id="+domain.getId()+".");
			model.addAttribute("errorcode", "internalStatus_null");
			return;
		}
		model.addAttribute("internalStatus",internalStatus.getId());
		model.addAttribute("internalStatusType", internalStatus.getType());
		model.addAttribute("internalStatusPriority", internalStatus.getPriority());
		model.addAttribute("formattedInternalStatus", internalStatus.getName());
		if(recommendationStatus==null) {
			logger.error("recommendation status is not set for this propriety point having id="+domain.getId()+".");
			model.addAttribute("errorcode", "recommendationStatus_null");
			return;
		}
		model.addAttribute("recommendationStatus",recommendationStatus.getId());
		model.addAttribute("recommendationStatusType",recommendationStatus.getType());
		model.addAttribute("recommendationStatusPriority", recommendationStatus.getPriority());
		model.addAttribute("formattedRecommendationStatus", recommendationStatus.getName());
		populateInternalStatus(model,domain,domain.getInternalStatus(),domain.getLocale());
		//Populate Actors
		List<Reference> actors = WorkflowConfig.findProprietyPointActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
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
	
	@SuppressWarnings("rawtypes")
	private void findLatestRemarksByUserGroup(final ProprietyPoint domain, final ModelMap model,
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
		CustomParameter customParameter = null;
		if(userGroupType!=null) {
			customParameter = CustomParameter.findByName(CustomParameter.class, "PROIS_LATESTREVISION_STARTINGACTOR_"+userGroupType.getType().toUpperCase(), "");
			if(customParameter != null){
				String strUsergroupType = customParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}else{
				CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "PROIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
				if(defaultCustomParameter != null){
					String strUsergroupType = defaultCustomParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}
			}
		} else {
			CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "PROIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
			if(defaultCustomParameter != null){
				String strUsergroupType = defaultCustomParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}
		}
		Map<String, String[]> requestMap=new HashMap<String, String[]>();			
		requestMap.put("proprietyPointId",new String[]{String.valueOf(domain.getId())});
		requestMap.put("locale",new String[]{domain.getLocale()});
		List result=Query.findReport("PROIS_LATEST_REVISIONS", requestMap);
		model.addAttribute("latestRevisions",result);
		model.addAttribute("startingActor", userGroupType.getName());
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final ProprietyPoint domain,final BindingResult result) {
		/**** Workflowdetails ****/
		String strWorkflowdetails = (String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails = 
				WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		try {
			ProprietyPoint proprietyPoint = null;
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
				if(proprietyPoint==null) {
					proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,domain.getId());
				}			
				members=proprietyPoint.getSupportingMembers();
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
			/**** discussion date ****/
			String strDiscussionDate = request.getParameter("discussionDate");		
			if(strDiscussionDate!=null){
				domain.setDiscussionDate(FormaterUtil.formatStringToDate(strDiscussionDate, ApplicationConstants.SERVER_DATETIMEFORMAT));
			}
			/**** Edited On,Edited By and Edited As is set ****/
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
			
			if(domain.getDrafts()==null) {
				if(proprietyPoint==null) {
					proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,domain.getId());
				}
				domain.setDrafts(proprietyPoint.getDrafts());
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
			String endflag=domain.getEndFlag();
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
			properties.put("pv_deviceId",String.valueOf(domain.getId()));
			properties.put("pv_deviceTypeId",String.valueOf(domain.getDeviceType().getId()));		
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
		return "workflow/proprietypoint/"+userGroupType;				
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
			String strDeviceType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strAdjourningDate = request.getParameter("adjourningDate");
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
						findByName(CustomParameter.class,"PROIS_ALLOWED_USERGROUPTYPES", "");
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
					&&strDeviceType!=null&&!(strDeviceType.isEmpty())){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(customParameter!=null){
					String server=customParameter.getValue();
					if(server.equals("TOMCAT")){
						try {
							strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
							strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");							
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
				strDeviceType = request.getSession().getAttribute("deviceType").toString();
				strWorkflowSubType = request.getSession().getAttribute("workflowSubType").toString();
				strStatus = request.getSession().getAttribute("status").toString();
				if(request.getSession().getAttribute("proprietyPointDate") != null){
					strAdjourningDate = request.getSession().getAttribute("adjourningDate").toString();
				}
			}
	
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strDeviceType!=null&&!(strDeviceType.isEmpty())
					&&strStatus!=null&&!(strStatus.isEmpty())
					&&strUsergroup!=null&&!(strUsergroup.isEmpty())
					&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
					&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){
					
					model.addAttribute("status", strStatus);
					model.addAttribute("usergroup", usergroup.getId());
					// Populate Roles
					/**
					 * Rules:
					 * a. PROIS roles starts with PROIS_, MEMBER_
					 * b. Any user will have single role per device type
					 * c. Any user can have multiple roles limited to one role per device type
					 */
					Set<Role> roles = this.getCurrentUser().getRoles();
					for(Role i : roles) {
						if(i.getType().startsWith("MEMBER_")) {
							model.addAttribute("role", i.getType());
							break;
						}
						else if(i.getType().startsWith("PROIS_")) {
							model.addAttribute("role", i.getType());
							break;
						}
					}
					/**** List of Statuses ****/
					List<Status> internalStatuses = new ArrayList<Status>();
					HouseType houseType = HouseType.
							findByFieldName(HouseType.class,"name",strHouseType, strLocale);
					DeviceType deviceType = DeviceType.
							findByFieldName(DeviceType.class,"name",strDeviceType,strLocale);
					Status internalStatus = Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority = CustomParameter.
							findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null 
							&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.
								findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus != null){
							internalStatuses = Status.
									findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** PROPRIETYPOINT_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** PROPRIETYPOINT_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE + INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)****/
					else if(deviceTypeHouseTypeInternalStatusUsergroup !=null ){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
					}
					/**** PROPRIETYPOINT_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** PROPRIETYPOINT_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses", internalStatuses);
					model.addAttribute("selectedWorkflowStatus", internalStatus.getName());
					model.addAttribute("workflowSubType", strWorkflowSubType);
					Date proprietyPointDate = null;
					if(strAdjourningDate != null && !strAdjourningDate.isEmpty()){
						 proprietyPointDate=FormaterUtil.
								 formatStringToDate(strAdjourningDate, ApplicationConstants.DB_DATEFORMAT);
						 model.addAttribute("adjourningDate", strAdjourningDate);
					}
					/**** Workflow Details ****/
					List<WorkflowDetails> workflowDetails = new ArrayList<WorkflowDetails>();
					workflowDetails = WorkflowDetails.
							findAllForProprietyPoints(strHouseType, strSessionType, strSessionYear,
									strDeviceType, ApplicationConstants.MYTASK_PENDING, strWorkflowSubType,
									proprietyPointDate, assignee, strItemsCount, strLocale);
					/**** Populating Bulk Approval VOs ****/
					List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
					NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
					int counter = 0;
					for(WorkflowDetails i:workflowDetails){
						BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
						ProprietyPoint proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,Long.parseLong(i.getDeviceId()));
						{
							bulkApprovalVO.setId(String.valueOf(i.getId()));
							bulkApprovalVO.setDeviceId(String.valueOf(proprietyPoint.getId()));	
							
							bulkApprovalVO.setFormattedAdjourningDate(FormaterUtil.formatDateToStringUsingCustomParameterFormat(proprietyPoint.getProprietyPointDate(), "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", proprietyPoint.getLocale()));						
							
							Map<String, String[]> parameters = new HashMap<String, String[]>();
							parameters.put("locale", new String[]{locale.toString()});
							parameters.put("deviceId", new String[]{proprietyPoint.getId().toString()});
														
							if(proprietyPoint.getNumber()!=null){
								bulkApprovalVO.setDeviceNumber(format.format(proprietyPoint.getNumber()));
							}else{
								bulkApprovalVO.setDeviceNumber("-");
							}
							bulkApprovalVO.setDeviceType(proprietyPoint.getDeviceType().getName());
							bulkApprovalVO.setMember(proprietyPoint.getPrimaryMember().getFullname());
							if(proprietyPoint.getRevisedSubject() != null && !proprietyPoint.getRevisedSubject().equals("")){
								bulkApprovalVO.setSubject(proprietyPoint.getRevisedSubject());
							}else{
								bulkApprovalVO.setSubject(proprietyPoint.getSubject());
							}
							if(proprietyPoint.getRevisedPointsOfPropriety() != null && !proprietyPoint.getRevisedPointsOfPropriety().isEmpty()){
								bulkApprovalVO.setBriefExpanation(proprietyPoint.getRevisedPointsOfPropriety());
							}else{
								bulkApprovalVO.setBriefExpanation(proprietyPoint.getPointsOfPropriety());
							}
							
							if(proprietyPoint.getRemarks()!=null&&!proprietyPoint.getRemarks().isEmpty()){
								bulkApprovalVO.setLastRemark(proprietyPoint.getRemarks());
							}else{
								bulkApprovalVO.setLastRemark("-");
							}
							bulkApprovalVO.setLastDecision(proprietyPoint.getInternalStatus().getName());
							bulkApprovalVO.setLastRemarkBy(proprietyPoint.getEditedAs());	
							bulkApprovalVO.setCurrentStatus(i.getStatus());
							bulkapprovals.add(bulkApprovalVO);
							
						
						if(counter == 0){
							model.addAttribute("level", proprietyPoint.getLevel());
							counter++;
						}
					}
					
					model.addAttribute("bulkapprovals", bulkapprovals);
					if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
						model.addAttribute("deviceId",bulkapprovals.get(0).getDeviceId());				
					}
				}
				model.addAttribute("deviceType", deviceType.getId());
			}
			return "workflow/proprietypoint/advancedbulkapproval";	
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
		String listSize = request.getParameter("proprietyPointlistSize");
		ProprietyPoint tempProprietyPoint  = null;
		if(listSize != null && !listSize.isEmpty()){
			for(int i =0; i<Integer.parseInt(listSize);i++){
				String subject = request.getParameter("subject"+i);
				String proprietyPointText = request.getParameter("proprietyPointText"+i);
				String actor = request.getParameter("actor"+i);
				String internalStatus = request.getParameter("internalStatus"+i);
				String remark = request.getParameter("remark"+i);
				String workflowDetailsId = request.getParameter("workflowDetailsId"+i);
				Long wrkflowId = Long.parseLong(workflowDetailsId);
				WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class,wrkflowId);
				String strChecked = request.getParameter("chk"+workflowDetailsId);
				if(strChecked != null && !strChecked.isEmpty() && Boolean.parseBoolean(strChecked)){
					ProprietyPoint proprietyPoint = ProprietyPoint.findById(ProprietyPoint.class,Long.parseLong(wfDetails.getDeviceId()));
					tempProprietyPoint = proprietyPoint;
					
					if(subject != null && !subject.isEmpty()){
						proprietyPoint.setRevisedSubject(subject);
					}
					if(proprietyPointText != null && !proprietyPointText.isEmpty()){
						proprietyPoint.setRevisedPointsOfPropriety(proprietyPointText);
					}
					
					if(remark != null && !remark.isEmpty()){
						proprietyPoint.setRemarks(remark);
					}					
					
					if(internalStatus!=null && !internalStatus.isEmpty()) {	//decision is selected meaning that statuses and flow should be updated accordingly
						/**** Update Internal Status and Recommendation Status ****/
						Status intStatus = Status.findById(Status.class, Long.parseLong(internalStatus));
						if(!intStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_DISCUSS) 
								&& !intStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_SENDBACK)
								/*&& !intStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_PROCESSED_SENDTODEPARTMENT)
								&& !intStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_PROCESSED_SENDTOSECTIONOFFICER)*/
						)
						{
							proprietyPoint.setInternalStatus(intStatus);
							proprietyPoint.setRecommendationStatus(intStatus);
							proprietyPoint.setEndFlag("continue");
						}else{
							proprietyPoint.setRecommendationStatus(intStatus);
							proprietyPoint.setEndFlag("continue");
						}
						/**** Update Actor ****/
						if(actor == null || actor.isEmpty()){
							actor = proprietyPoint.getActor();
							String[] temp = actor.split("#");
							actor = temp[1];
						}
						String level = request.getParameter("proprietyPointLevel");
						if(level == null || level.isEmpty()){
							level = proprietyPoint.getLevel();
						}
						String[] temp = actor.split("#");
						proprietyPoint.setActor(actor);
						proprietyPoint.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						proprietyPoint.setLevel(temp[2]);
						/**** Complete Task ****/
						Map<String,String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId", String.valueOf(proprietyPoint.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(proprietyPoint.getDeviceType().getId()));
						properties.put("pv_user", temp[0]);
						properties.put("pv_endflag", proprietyPoint.getEndFlag());
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task, properties);	
						if(proprietyPoint.getEndFlag() != null && !proprietyPoint.getEndFlag().isEmpty()
								&& proprietyPoint.getEndFlag().equals("continue")){
							/**** Create New Workflow Details ****/
							ProcessInstance processInstance = 
									processService.findProcessInstanceById(task.getProcessInstanceId());
							Workflow workflowFromUpdatedStatus = null;
							try {
								Status iStatus = proprietyPoint.getInternalStatus();								
								workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
					
							} catch(ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
								model.addAttribute("type", "error");
								
							}
							Task newtask = processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = null;
							try {
								workflowDetails2 = WorkflowDetails.create(proprietyPoint,newtask,usergroupType,workflowFromUpdatedStatus.getType(),level);
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
							proprietyPoint.setWorkflowDetailsId(workflowDetails2.getId());
							proprietyPoint.setTaskReceivedOn(new Date());								
						}
						/**** Update Old Workflow Details ****/
						wfDetails.setStatus("COMPLETED");
						wfDetails.setInternalStatus(proprietyPoint.getInternalStatus().getName());
						wfDetails.setRecommendationStatus(proprietyPoint.getRecommendationStatus().getName());
						wfDetails.setCompletionTime(new Date());
						wfDetails.setAdjourningDate(proprietyPoint.getProprietyPointDate());
						wfDetails.setDecisionInternalStatus(proprietyPoint.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(proprietyPoint.getRecommendationStatus().getName());
						wfDetails.merge();																
						performAction(proprietyPoint, request);		
						
					} else if(request.getParameter("preserveDecisions")!=null 
								&& Boolean.parseBoolean(request.getParameter("preserveDecisions"))) { //decision is preserved meaning that statuses and flow should be preserved accordingly
						
						proprietyPoint.setEndFlag("continue");
						/**** Find next actor for the preserved decision ****/
						String strUserGroup = request.getParameter("usergroup");
						if(strUserGroup!=null) {
							UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
							List<Reference> actors = WorkflowConfig.findProprietyPointActorsVO(proprietyPoint,proprietyPoint.getInternalStatus(),userGroup,Integer.parseInt(proprietyPoint.getLevel()),locale.toString());
							if(actors!=null && !actors.isEmpty()) {
								actor = actors.get(0).getId();
								if(actor==null || actor.isEmpty()) {
									actor = proprietyPoint.getActor();
									proprietyPoint.setEndFlag("end"); //as further no actor is available
								}
							}
						}					
						/**** Update Actor ****/
						String[] temp = actor.split("#");
						proprietyPoint.setActor(actor);
						proprietyPoint.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						String level = temp[2];
						proprietyPoint.setLevel(level);						
						proprietyPoint.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						
						/**** Complete Task ****/
						Map<String,String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId", String.valueOf(proprietyPoint.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(proprietyPoint.getDeviceType().getId()));
						properties.put("pv_user", temp[0]);
						properties.put("pv_endflag", proprietyPoint.getEndFlag());
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task, properties);	
						if(proprietyPoint.getEndFlag() != null && !proprietyPoint.getEndFlag().isEmpty()
								&& proprietyPoint.getEndFlag().equals("continue")){
							/**** Create New Workflow Details ****/
							ProcessInstance processInstance = 
									processService.findProcessInstanceById(task.getProcessInstanceId());
							Workflow workflowFromUpdatedStatus = null;
							try {
								Status iStatus = proprietyPoint.getInternalStatus();								
								workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
					
							} catch(ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
								model.addAttribute("type", "error");
								
							}
							Task newtask = processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = null;
							try {
								workflowDetails2 = WorkflowDetails.create(proprietyPoint,newtask,usergroupType,workflowFromUpdatedStatus.getType(),level);
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
							proprietyPoint.setWorkflowDetailsId(workflowDetails2.getId());
							proprietyPoint.setTaskReceivedOn(new Date());								
						}
						/**** Update Old Workflow Details ****/
						wfDetails.setStatus("COMPLETED");
						wfDetails.setInternalStatus(proprietyPoint.getInternalStatus().getName());
						wfDetails.setRecommendationStatus(proprietyPoint.getRecommendationStatus().getName());
						wfDetails.setCompletionTime(new Date());
						wfDetails.setAdjourningDate(proprietyPoint.getProprietyPointDate());
						wfDetails.setDecisionInternalStatus(proprietyPoint.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(proprietyPoint.getRecommendationStatus().getName());
						wfDetails.merge();																
						performAction(proprietyPoint, request);
					}
						
					/**** Update Propriety Point ****/
					proprietyPoint.setEditedOn(new Date());
					proprietyPoint.setEditedBy(this.getCurrentUser().getActualUsername());
					proprietyPoint.setEditedAs(wfDetails.getAssigneeUserGroupName());
					proprietyPoint.merge();
				}					
			}
		}
		
		if(tempProprietyPoint != null){
			request.getSession().setAttribute("houseType", tempProprietyPoint.getHouseType().getName());
			request.getSession().setAttribute("sessionType", tempProprietyPoint.getSession().getType().getSessionType());
			request.getSession().setAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(tempProprietyPoint.getSession().getYear(), locale.toString()));
			request.getSession().setAttribute("deviceType", tempProprietyPoint.getDeviceType().getName());
			request.getSession().setAttribute("workflowSubType", tempProprietyPoint.getInternalStatus().getType());
			
		}
		String adjourningDate = request.getParameter("adjourningDate");
		if(adjourningDate != null && !adjourningDate.isEmpty()){
			request.getSession().setAttribute("strAdjourningDate", adjourningDate);
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
        String returnUrl = "redirect:/workflow/proprietypoint/advancedbulkapproval";
        return returnUrl;
//		getAdvancedBulkApproval(request, locale, model);
	}
	
	private void populateInternalStatus(final ModelMap model,
			final ProprietyPoint domain, final Status putupOptionsStatus,
			final String locale) {
		try{
			List<Status> internalStatuses = new ArrayList<Status>();
			DeviceType deviceType = domain.getDeviceType();
			HouseType houseType = domain.getHouseType();
			String actor = domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus = CustomParameter.
						findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
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
				CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class, "PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority != null && finalApprovingAuthority.getValue().contains(usergroupType)) {
					CustomParameter finalApprovingAuthorityStatus = null;
					if(workflow!=null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(finalApprovingAuthorityStatus==null) {
							finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						}						
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
					}
					if(finalApprovingAuthorityStatus != null){
						internalStatuses=Status.
								findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					}
				}/**** PROPRIETYPOINT_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				}/**** PROPRIETYPOINT_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				}	
				/**** PROPRIETYPOINT_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
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
	
	private void performAction(final ProprietyPoint domain, HttpServletRequest request) throws ELSException {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION)){
			performActionOnAdmission(domain, request);
		} 
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.PROPRIETYPOINT_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.PROPRIETYPOINT_FINAL_REJECTION)){
			performActionOnRejection(domain, request);
		}
	}
	
	private void performActionOnAdmission(ProprietyPoint domain, HttpServletRequest request) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject());	
		}		
		if(domain.getRevisedPointsOfPropriety() == null 
				|| domain.getRevisedPointsOfPropriety().isEmpty()){			
			domain.setRevisedPointsOfPropriety(domain.getPointsOfPropriety());
		}
		domain.setAdmissionNumber(ProprietyPoint.assignAdmissionNumber(domain.getSession(), domain.getLocale()));
		domain.simpleMerge();
		domain.setVersion(domain.getVersion() + 1); //hack for handling OptimisticLockException due to above commit
	}
	
	private void performActionOnRejection(ProprietyPoint domain, HttpServletRequest request) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject());	
		}		
		if(domain.getRevisedPointsOfPropriety() == null 
				|| domain.getRevisedPointsOfPropriety().isEmpty()){			
			domain.setRevisedPointsOfPropriety(domain.getPointsOfPropriety());
		}
		domain.simpleMerge();
		domain.setVersion(domain.getVersion() + 1); //hack for handling OptimisticLockException due to above commit
	}

}