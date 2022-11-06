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
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BulkApprovalVO;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.mois.AdjournmentMotionController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
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
@RequestMapping("/workflow/adjournmentmotion")
public class AdjournmentMotionWorkflowController  extends BaseController {
	
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
							approvedAdjourningDate = AdjournmentMotion.findDefaultAdjourningDateForSession(adjournmentMotion.getSession(), true);
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
		/** Set Last Date of Answer Receiving from Department **/
		Date lastDateOfReplyReceiving = null;
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)
				&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)
				&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_SENDTOSECTIONOFFICER)) {
			
			String daysCountForReceivingReplyFromDepartment = "2";
			CustomParameter csptDaysCountForReceivingReplyFromDepartment = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+ApplicationConstants.DAYS_COUNT_FOR_RECEIVING_ANSWER_FROM_DEPARTMENT, "");
			if(csptDaysCountForReceivingReplyFromDepartment!=null
					&& csptDaysCountForReceivingReplyFromDepartment.getValue()!=null) {
				daysCountForReceivingReplyFromDepartment = csptDaysCountForReceivingReplyFromDepartment.getValue();
			}
			if(domain.getReplyRequestedDate()!=null) {
				lastDateOfReplyReceiving = Holiday.getNextWorkingDateFrom(domain.getReplyRequestedDate(), Integer.parseInt(daysCountForReceivingReplyFromDepartment), locale);
			} else {
				lastDateOfReplyReceiving = Holiday.getNextWorkingDateFrom(new Date(), Integer.parseInt(daysCountForReceivingReplyFromDepartment), locale);
			}
			domain.setLastDateOfReplyReceiving(lastDateOfReplyReceiving);
		} else {
			if(domain.getLastDateOfReplyReceiving()!=null) {
				lastDateOfReplyReceiving = domain.getLastDateOfReplyReceiving();
			}
		}
		if(lastDateOfReplyReceiving!=null) {
			model.addAttribute("lastDateOfReplyReceiving", lastDateOfReplyReceiving);
			model.addAttribute("formattedLastReplyReceivingDate", FormaterUtil.formatDateToString(lastDateOfReplyReceiving, ApplicationConstants.SERVER_DATEFORMAT, locale));
		}
		/**** To have the task creation date and lateReplyFillingFlag if userGroup is department related ***/
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
				|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			boolean canAdd = false;

			try{	
				/** validation for restricting late Reply filling **/				
				if(domain.getStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)) {
					//check validation flag
					CustomParameter csptValidationFlagForLastReceivingDateFromDepartment = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_"+domain.getHouseType().getType().toUpperCase()+"_"+ApplicationConstants.VALIDATION_FLAG_FOR_LAST_RECEIVING_DATE_FROM_DEPARTMENT, "");
					if(csptValidationFlagForLastReceivingDateFromDepartment!=null) {
						String validationFlagForLastReceivingDateFromDepartment = csptValidationFlagForLastReceivingDateFromDepartment.getValue();
						if(validationFlagForLastReceivingDateFromDepartment!=null
								&& !validationFlagForLastReceivingDateFromDepartment.isEmpty()) {
							if(Boolean.valueOf(validationFlagForLastReceivingDateFromDepartment)) {
								//perform validation
								if(DateUtil.compareDatePartOnly(domain.getLastDateOfReplyReceiving(), new Date())<0) {
									model.addAttribute("lateReplyFillingFlag", "set");
								}
							}
						}
					}
				}

				CustomParameter serverTimeStamp = 
						CustomParameter.findByName(CustomParameter.class,"SERVER_TIMESTAMP","");
				if(serverTimeStamp != null){
					if(workflowDetails.getAssignmentTime() != null){	
						String formattedTaskCreationDate = FormaterUtil.
								getDateFormatter(serverTimeStamp.getValue(),locale).
								format(workflowDetails.getAssignmentTime());
						model.addAttribute("taskCreationDate", formattedTaskCreationDate);
					}
				}
				canAdd = true;
			}catch(Exception e){
				logger.error("task creation date is missing.: " + e.getMessage());
			}
			if(!canAdd){
				model.addAttribute("taskCreationDate", "");
			}
		}
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
		/**** Reply related Dates ****/
		if(domain.getReplyRequestedDate()!=null) {
			model.addAttribute("formattedReplyRequestedDate", FormaterUtil.formatDateToString(domain.getReplyRequestedDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		if(domain.getReplyReceivedDate()!=null) {
			model.addAttribute("formattedReplyReceivedDate", FormaterUtil.formatDateToString(domain.getReplyReceivedDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void findLatestRemarksByUserGroup(final AdjournmentMotion domain, final ModelMap model,
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
			customParameter = CustomParameter.findByName(CustomParameter.class, "AMOIS_LATESTREVISION_STARTINGACTOR_"+userGroupType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase(), "");
			if(customParameter != null){
				String strUsergroupType = customParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}else{
				CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "AMOIS_LATESTREVISION_STARTINGACTOR_DEFAULT"+"_"+houseType.getType().toUpperCase(), "");
				if(defaultCustomParameter != null){
					String strUsergroupType = defaultCustomParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}
			}
		} else {
			CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "AMOIS_LATESTREVISION_STARTINGACTOR_DEFAULT"+"_"+houseType.getType().toUpperCase(), "");
			if(defaultCustomParameter != null){
				String strUsergroupType = defaultCustomParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}
		}
		Map<String, String[]> requestMap=new HashMap<String, String[]>();			
		requestMap.put("adjournmentMotionId",new String[]{String.valueOf(domain.getId())});
		requestMap.put("locale",new String[]{domain.getLocale()});
		List result=Query.findReport("AMOIS_LATEST_REVISIONS", requestMap);
		model.addAttribute("latestRevisions",result);
		model.addAttribute("startingActor", userGroupType.getName());
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
			AdjournmentMotion adjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class,domain.getId());
			Ministry prevMinistry = adjournmentMotion.getMinistry();
			SubDepartment prevSubDepartment = adjournmentMotion.getSubDepartment();
			if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
				/**** display message ****/
				model.addAttribute("type","taskalreadycompleted");
				return "workflow/info";
			}
			/**** Updating domain ****/
			/**** Binding Supporting Members ****/
			String[] strSupportingMembers=request.getParameterValues("selectedSupportingMembers");
			List<SupportingMember> members=new ArrayList<SupportingMember>();
//			if(domain.getId()!=null){
//				if(adjournmentMotion==null) {
//					adjournmentMotion=AdjournmentMotion.findById(AdjournmentMotion.class,domain.getId());
//				}			
//				members=adjournmentMotion.getSupportingMembers();
//			}
			members=adjournmentMotion.getSupportingMembers();
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
			/**** workflow started on date ****/
			String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
			if(strWorkflowStartedOnDate != null && !strWorkflowStartedOnDate.isEmpty()){
				domain.setWorkflowStartedOn(FormaterUtil.formatStringToDate(strWorkflowStartedOnDate, ApplicationConstants.SERVER_DATETIMEFORMAT));
			}
			/**** task received on date ****/
			String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
			if(strTaskReceivedOnDate != null && !strTaskReceivedOnDate.isEmpty()){
				domain.setWorkflowStartedOn(FormaterUtil.formatStringToDate(strTaskReceivedOnDate, ApplicationConstants.SERVER_DATETIMEFORMAT));
			}
			/**** reply related processing and dates ****/
			String operation = request.getParameter("operation");
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)
					&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_SENDTOSECTIONOFFICER)){
				if(operation!=null && operation.equals("workflowsubmit")){
					if(domain.getTransferToDepartmentAccepted()==null || domain.getTransferToDepartmentAccepted().equals(false)) {
						if(domain.getReply() == null || domain.getReply().isEmpty()){
							result.rejectValue("reply", "ReplyEmpty");
							if(!model.containsAttribute("errorcode")){
								model.addAttribute("errorcode","no_reply_provided_department");								
							}
							return "workflow/myTasks/error";
						}
					}
				}
			}
			String strReplyRequestedDate = request.getParameter("setReplyRequestedDate");
			if(strReplyRequestedDate != null && !strReplyRequestedDate.isEmpty()) {
				domain.setReplyRequestedDate(FormaterUtil.formatStringToDate(strReplyRequestedDate, ApplicationConstants.SERVER_DATETIMEFORMAT));					
			}
			String strReplyReceivedDate = request.getParameter("setReplyReceivedDate");
			if(strReplyReceivedDate !=null && !strReplyReceivedDate.isEmpty()) {
				domain.setReplyReceivedDate(FormaterUtil.formatStringToDate(strReplyReceivedDate, ApplicationConstants.SERVER_DATETIMEFORMAT));
			}
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)
					&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)
					&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_SENDTODEPARTMENT)
					&& (domain.getReply()==null || domain.getReply().isEmpty())) {
				if(strReplyRequestedDate == null || strReplyRequestedDate.isEmpty()) {
					domain.setReplyRequestedDate(new Date());
				}				
			}
			
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)
					&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)
					&& domain.getReply()!=null && !domain.getReply().isEmpty() && domain.getReplyReceivedDate()==null) {					
				domain.setReplyReceivedDate(new Date());
				domain.setReplyReceivedMode(ApplicationConstants.REPLY_RECEIVED_MODE_ONLINE);
			}
			
			String strLastDateOfReplyReceiving = request.getParameter("setLastDateOfReplyReceiving");
			if(strLastDateOfReplyReceiving!=null && !strLastDateOfReplyReceiving.isEmpty()) {
				Date lastDateOfReplyReceiving = FormaterUtil.getDateFormatter("en_US").parse(strLastDateOfReplyReceiving);
				//Added the above code as the following code was giving exception of unparseble date
				//Date lastDateOfReplyReceiving = FormaterUtil.formatStringToDate(strLastDateOfReplyReceiving, ApplicationConstants.DB_DATEFORMAT, locale.toString());
				domain.setLastDateOfReplyReceiving(lastDateOfReplyReceiving);
			}
			/**** Edited On,Edited By and Edited As is set ****/
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
			
			/**** added by dhananjayb.. required in case when domain is updated with start of new workflow before completion of current workflow ****/
			String endFlagForCurrentWorkflow = domain.getEndFlag();				
			
//			if(domain.getDrafts()==null) {
//				if(adjournmentMotion==null) {
//					adjournmentMotion=AdjournmentMotion.findById(AdjournmentMotion.class,domain.getId());
//				}
//				domain.setDrafts(adjournmentMotion.getDrafts());
//			}
			if(domain.getDrafts()==null) {
				domain.setDrafts(adjournmentMotion.getDrafts());
			}
			
			String currentDeviceTypeWorkflowType = null;
			Workflow workflowFromUpdatedStatus = domain.findWorkflowFromStatus();
			
			//String sendbackactor=request.getParameter("sendbackactor");
			if(workflowFromUpdatedStatus!=null) {
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}			
			
			performAction(domain, request);	
			
			boolean isMinistryChanged = false;
			boolean isSubDepartmentChanged = false;
			if(domain.getMinistry()!=null && !domain.getMinistry().equals(prevMinistry)){
				isMinistryChanged = true;
			}else if(domain.getSubDepartment()!=null && !domain.getSubDepartment().equals(prevSubDepartment)){
				isSubDepartmentChanged = true;
			}			
			
			if(isMinistryChanged || isSubDepartmentChanged){
				Status sendToDepartmentStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_SENDTODEPARTMENT, locale.toString());
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
						|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
					domain.setRecommendationStatus(sendToDepartmentStatus);
				} else {
					domain.setRecommendationStatus(domain.getInternalStatus());
				}
				List<ClubbedEntity> clubbedEntities = AdjournmentMotion.findClubbedEntitiesByPosition(domain);
				if(clubbedEntities!=null) {
					for(ClubbedEntity ce: clubbedEntities) {
						AdjournmentMotion clubbedAdjournmentMotion = ce.getAdjournmentMotion();
						clubbedAdjournmentMotion.setMinistry(domain.getMinistry());
						clubbedAdjournmentMotion.setSubDepartment(domain.getSubDepartment());
						clubbedAdjournmentMotion.merge();
					}
				}			
				domain.merge();
				adjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class,domain.getId()); //added in order to avoid optimistic lock exception
				adjournmentMotion.removeExistingWorkflowAttributes();								
				// Before ending wfDetails process collect information
				// which will be useful for creating a new process later.
				int assigneeLevel = 
					Integer.parseInt(workflowDetails.getAssigneeLevel());
				UserGroupType ugt = UserGroupType.findByType(userGroupType, locale.toString());				
				WorkflowDetails.endProcess(workflowDetails);
				if(userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
					ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale.toString());	
					assigneeLevel = assigneeLevel - 1;
				}
				Workflow workflow = Workflow.findByStatus(adjournmentMotion.getInternalStatus(), locale.toString());
				//Motion in Post final status and pre ballot state can be group changed by Department 
				//as well as assistant of Secretariat
				WorkflowDetails.startProcessAtGivenLevel(adjournmentMotion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, ugt, assigneeLevel, locale.toString());
				
				/**** display message ****/
				
				model.addAttribute("type","taskcompleted");
				return "workflow/info";
			}
			
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
						findByName(CustomParameter.class,"AMOIS_ALLOWED_USERGROUPTYPES", "");
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
				if(request.getSession().getAttribute("adjourningDate") != null){
					strAdjourningDate = request.getSession().getAttribute("adjourningDate").toString();
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
					 * a. AMOIS roles starts with AMOIS_, MEMBER_
					 * b. Any user will have single role per device type
					 * c. Any user can have multiple roles limited to one role per device type
					 */
					Set<Role> roles = this.getCurrentUser().getRoles();
					for(Role i : roles) {
						if(i.getType().startsWith("MEMBER_")) {
							model.addAttribute("role", i.getType());
							break;
						}
						else if(i.getType().startsWith("AMOIS_")) {
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
							findByName(CustomParameter.class, "ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null 
							&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.
								findByName(CustomParameter.class,"ADJOURNMENTMOTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus != null){
							internalStatuses = Status.
									findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** ADJOURNMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** ADJOURNMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE + INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)****/
					else if(deviceTypeHouseTypeInternalStatusUsergroup !=null ){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
					}
					/**** ADJOURNMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** ADJOURNMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses", internalStatuses);
					model.addAttribute("selectedWorkflowStatus", internalStatus.getName());
					model.addAttribute("workflowSubType", strWorkflowSubType);
					Date adjourningDate = null;
					if(strAdjourningDate != null && !strAdjourningDate.isEmpty()){
						 adjourningDate=FormaterUtil.
								 formatStringToDate(strAdjourningDate, ApplicationConstants.DB_DATEFORMAT);
						 model.addAttribute("adjourningDate", strAdjourningDate);
					}
					/**** Workflow Details ****/
					List<WorkflowDetails> workflowDetails = WorkflowDetails.
								findAllForAdjournmentMotions(strHouseType, strSessionType, strSessionYear,
										strMotionType, ApplicationConstants.MYTASK_PENDING, strWorkflowSubType,
										adjourningDate, assignee, strItemsCount, strLocale);
					/**** Populating Bulk Approval VOs ****/
					List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
					NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
					int counter = 0;
					for(WorkflowDetails i:workflowDetails){
						BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
						AdjournmentMotion motion=AdjournmentMotion.findById(AdjournmentMotion.class,Long.parseLong(i.getDeviceId()));
						{
							bulkApprovalVO.setId(String.valueOf(i.getId()));
							bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));	
							
							bulkApprovalVO.setFormattedAdjourningDate(FormaterUtil.formatDateToStringUsingCustomParameterFormat(motion.getAdjourningDate(), "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", motion.getLocale()));
							
							Map<String, String[]> parameters = new HashMap<String, String[]>();
							parameters.put("locale", new String[]{locale.toString()});
							parameters.put("motionId", new String[]{motion.getId().toString()});
							List clubbedNumbers = org.mkcl.els.domain.Query.findReport("AMOIS_GET_CLUBBEDNUMBERS", parameters);
							if(clubbedNumbers != null && !clubbedNumbers.isEmpty() && clubbedNumbers.get(0) != null){
								bulkApprovalVO.setFormattedClubbedNumbers(clubbedNumbers.get(0).toString());
							}
							
//							List referencedNumbers = org.mkcl.els.domain.Query.findReport("MOIS_GET_REFERENCEDNUMBERS", parameters);
//							if(referencedNumbers != null && !referencedNumbers.isEmpty() && referencedNumbers.get(0) != null){
//								bulkApprovalVO.setFormattedReferencedNumbers(referencedNumbers.get(0).toString());
//							}
							
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
			return "workflow/adjournmentmotion/advancedbulkapproval";	
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
		AdjournmentMotion tempMotion  = null;
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
					AdjournmentMotion motion = AdjournmentMotion.findById(AdjournmentMotion.class,Long.parseLong(wfDetails.getDeviceId()));
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
						if(!intStatus.getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_DISCUSS) 
								&& !intStatus.getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_SENDBACK)
								/*&& !intStatus.getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_SENDTODEPARTMENT)
								&& !intStatus.getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_SENDTOSECTIONOFFICER)*/
						)
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
						wfDetails.setAdjourningDate(motion.getAdjourningDate());
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
							List<Reference> actors = WorkflowConfig.findAdjournmentMotionActorsVO(motion,motion.getInternalStatus(),userGroup,Integer.parseInt(motion.getLevel()),locale.toString());
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
						wfDetails.setAdjourningDate(motion.getAdjourningDate());
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
        String returnUrl = "redirect:/workflow/adjournmentmotion/advancedbulkapproval";
        return returnUrl;
//		getAdvancedBulkApproval(request, locale, model);
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
		domain.setAdmissionNumber(AdjournmentMotion.assignAdmissionNumber(domain.getSession(), domain.getLocale()));
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
