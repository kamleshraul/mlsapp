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
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.controller.smis.SpecialMentionNoticeController;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workflow/specialmentionnotice")
public class SpecialMentionNoticeWorkflowController  extends BaseController {
	
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
			SpecialMentionNotice domain=SpecialMentionNotice.findById(SpecialMentionNotice.class,Long.parseLong(workflowDetails.getDeviceId()));
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
	
	private void populateModel(final SpecialMentionNotice domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) throws ELSException, ParseException {
		/** getting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
		UserGroupType userGroupTypeObj = UserGroupType.findByType(workflowDetails.getAssigneeUserGroupType(), domain.getLocale());
		CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"SMIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
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
			throw new ELSException("SpecialMentionNoticeController.populateCreateIfErrors/3", 
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
			throw new ELSException("SpecialMentionNoticeController.populateCreateIfErrors/3", 
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
		model.addAttribute("userName",this.getCurrentUser().getActualUsername());
		
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		model.addAttribute("userGroupName",userGroupType.getName());
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
//			Constituency constituency = Member.findConstituency(primaryMember, new Date());
//			if(constituency != null){
//				model.addAttribute("constituency", constituency.getDisplayName());
//			}
			/**** Constituency ****/
			Long houseId=selectedSession.getHouse().getId();
			MasterVO constituency=null;
			if(houseType.getType().equals("lowerhouse")){
				constituency=Member.findConstituencyByAssemblyId(primaryMember.getId(), houseId);
				model.addAttribute("constituency",constituency.getName());
			}else if(houseType.getType().equals("upperhouse")){
				String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
				constituency=Member.findConstituencyByCouncilDates(primaryMember.getId(), houseId, "DATE", date, date);
				model.addAttribute("constituency",constituency.getName());
			}
		}
				
		//Populate PrimaryMemberName 
		String memberNames = domain.getPrimaryMember().getFullname();
		model.addAttribute("memberNames",memberNames);
		/**** Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		/** populate session dates as possible special mention notice dates **/
		if(selectedSession!=null && selectedSession.getId()!=null) {
			List<Date> sessionDates = selectedSession.findAllSessionDates();
			model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", domain.getLocale()));				
		}
		/**** populate special mention notice date ****/
		model.addAttribute("selectedSpecialMentionNoticeDate", FormaterUtil.formatDateToString(domain.getSpecialMentionNoticeDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
		model.addAttribute("formattedSpecialMentionNoticeDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getSpecialMentionNoticeDate(), "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", domain.getLocale()));				
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
				&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION)
				&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PROCESSED_SENDTOSECTIONOFFICER)) {
			
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
				findByName(CustomParameter.class, "SMIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING", "");   
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
				List<Reference> clubEntityReferences = SpecialMentionNoticeController.populateClubbedEntityReferences(domain, locale);
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
			actors = WorkflowConfig.findSpecialMentionNoticeActorsVO(domain, recommendationStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
		}else{
			actors = WorkflowConfig.findSpecialMentionNoticeActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
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
	private void findLatestRemarksByUserGroup(final SpecialMentionNotice domain, final ModelMap model,
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
			customParameter = CustomParameter.findByName(CustomParameter.class, "SMIS_LATESTREVISION_STARTINGACTOR_"+userGroupType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase(), "");
			if(customParameter != null){
				String strUsergroupType = customParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}else{
				CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "SMIS_LATESTREVISION_STARTINGACTOR_DEFAULT"+"_"+houseType.getType().toUpperCase(), "");
				if(defaultCustomParameter != null){
					String strUsergroupType = defaultCustomParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}
			}
		} else {
			CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "SMIS_LATESTREVISION_STARTINGACTOR_DEFAULT"+"_"+houseType.getType().toUpperCase(), "");
			if(defaultCustomParameter != null){
				String strUsergroupType = defaultCustomParameter.getValue();
				userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
			}
		}
		Map<String, String[]> requestMap=new HashMap<String, String[]>();			
		requestMap.put("specialMentionNoticeId",new String[]{String.valueOf(domain.getId())});
		requestMap.put("locale",new String[]{domain.getLocale()});
		List result=Query.findReport("SMIS_LATEST_REVISIONS", requestMap);
		model.addAttribute("latestRevisions",result);
		model.addAttribute("startingActor", userGroupType.getName());
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final SpecialMentionNotice domain,final BindingResult result) {
		/**** Workflowdetails ****/
		String strWorkflowdetails = (String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails = 
				WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		try {
			SpecialMentionNotice specialMentionNotice = SpecialMentionNotice.findById(SpecialMentionNotice.class,domain.getId());
			Ministry prevMinistry = specialMentionNotice.getMinistry();
			SubDepartment prevSubDepartment = specialMentionNotice.getSubDepartment();
			//SpecialMentionNotice specialMentionNotice = null;
			if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
				/**** display message ****/
				model.addAttribute("type","taskalreadycompleted");
				return "workflow/info";
			}
			/**** Updating domain ****/
			/***** To retain the clubbed special mention notices when moving through workflow ****/
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
			/***** To retain the parent pecial mention notices  when moving through workflow ****/
			String strParentSpecialMentionNotice = request.getParameter("parent");
			if(strParentSpecialMentionNotice!=null) {
				if(!strParentSpecialMentionNotice.isEmpty()) {
					SpecialMentionNotice parentSpecialMentionNotice = SpecialMentionNotice.findById(SpecialMentionNotice.class, Long.parseLong(strParentSpecialMentionNotice));
					domain.setParent(parentSpecialMentionNotice);
				}
			}
			/** setting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
			UserGroupType userGroupTypeObj = UserGroupType.findByType(userGroupType, domain.getLocale());
			CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"SMIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
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
					&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PROCESSED_SENDTOSECTIONOFFICER)){
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
					&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION)
					&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PROCESSED_SENDTODEPARTMENT)
					&& (domain.getReply()==null || domain.getReply().isEmpty())) {
				if(strReplyRequestedDate == null || strReplyRequestedDate.isEmpty()) {
					domain.setReplyRequestedDate(new Date());
				}				
			}
			
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)
					&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION)
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
			
			if(domain.getDrafts()==null) {
				//if(specialMentionNotice==null) {
					domain.setDrafts(specialMentionNotice.getDrafts());
				//	specialMentionNotice=SpecialMentionNotice.findById(SpecialMentionNotice.class,domain.getId());
				//}
				//domain.setDrafts(specialMentionNotice.getDrafts());
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
				Status sendToDepartmentStatus = Status.findByType(ApplicationConstants.SPECIALMENTIONNOTICE_PROCESSED_SENDTODEPARTMENT, locale.toString());
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
						|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
					domain.setRecommendationStatus(sendToDepartmentStatus);
				} else {
					domain.setRecommendationStatus(domain.getInternalStatus());
				}
				List<ClubbedEntity> clubbedEntities = SpecialMentionNotice.findClubbedEntitiesByPosition(domain);
				if(clubbedEntities!=null) {
					for(ClubbedEntity ce: clubbedEntities) {
						AdjournmentMotion clubbedAdjournmentMotion = ce.getAdjournmentMotion();
						clubbedAdjournmentMotion.setMinistry(domain.getMinistry());
						clubbedAdjournmentMotion.setSubDepartment(domain.getSubDepartment());
						clubbedAdjournmentMotion.merge();
					}
				}			
				domain.merge();
				specialMentionNotice = SpecialMentionNotice.findById(SpecialMentionNotice.class,domain.getId()); //added in order to avoid optimistic lock exception
				specialMentionNotice.removeExistingWorkflowAttributes();								
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
				Workflow workflow = Workflow.findByStatus(specialMentionNotice.getInternalStatus(), locale.toString());
				//Motion in Post final status and pre ballot state can be group changed by Department 
				//as well as assistant of Secretariat
				WorkflowDetails.startProcessAtGivenLevel(specialMentionNotice, ApplicationConstants.APPROVAL_WORKFLOW, workflow, ugt, assigneeLevel, locale.toString());
				
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
		return "workflow/specialmentionnotice/"+userGroupType;				
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
			String strAdjourningDate = request.getParameter("specialMentionNoticeDate");
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
						findByName(CustomParameter.class,"SMIS_ALLOWED_USERGROUPTYPES", "");
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
				if(request.getSession().getAttribute("specialMentionNoticeDate") != null){
					strAdjourningDate = request.getSession().getAttribute("specialMentionNoticeDate").toString();
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
					 * a. SMIS roles starts with SMIS_, MEMBER_
					 * b. Any user will have single role per device type
					 * c. Any user can have multiple roles limited to one role per device type
					 */
					Set<Role> roles = this.getCurrentUser().getRoles();
					for(Role i : roles) {
						if(i.getType().startsWith("MEMBER_")) {
							model.addAttribute("role", i.getType());
							break;
						}
						else if(i.getType().startsWith("SMIS_")) {
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
							findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null 
							&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.
								findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus != null){
							internalStatuses = Status.
									findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE + INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)****/
					else if(deviceTypeHouseTypeInternalStatusUsergroup !=null ){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
					}
					/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses", internalStatuses);
					model.addAttribute("selectedWorkflowStatus", internalStatus.getName());
					model.addAttribute("workflowSubType", strWorkflowSubType);
					Date specialMentionNoticeDate = null;
					if(strAdjourningDate != null && !strAdjourningDate.isEmpty()){
						 specialMentionNoticeDate=FormaterUtil.
								 formatStringToDate(strAdjourningDate, ApplicationConstants.DB_DATEFORMAT);
						 model.addAttribute("specialMentionNoticeDate", strAdjourningDate);
					}
					/**** Workflow Details ****/
					List<WorkflowDetails> workflowDetails = WorkflowDetails.
								findAllForSpecialMentionNotices(strHouseType, strSessionType, strSessionYear,
										strMotionType, ApplicationConstants.MYTASK_PENDING, strWorkflowSubType,
										specialMentionNoticeDate, assignee, strItemsCount, strLocale);
					/**** Populating Bulk Approval VOs ****/
					List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
					NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
					int counter = 0;
					for(WorkflowDetails i:workflowDetails){
						BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
						SpecialMentionNotice motion=SpecialMentionNotice.findById(SpecialMentionNotice.class,Long.parseLong(i.getDeviceId()));
						{
							bulkApprovalVO.setId(String.valueOf(i.getId()));
							bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));	
							
							bulkApprovalVO.setFormattedAdjourningDate(FormaterUtil.formatDateToStringUsingCustomParameterFormat(motion.getSpecialMentionNoticeDate(), "SPECIALMENTIONNOTICE_ADJOURNINGDATEFORMAT", motion.getLocale()));
							
							Map<String, String[]> parameters = new HashMap<String, String[]>();
							parameters.put("locale", new String[]{locale.toString()});
							parameters.put("motionId", new String[]{motion.getId().toString()});
							List clubbedNumbers = org.mkcl.els.domain.Query.findReport("SMIS_GET_CLUBBEDNUMBERS", parameters);
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
			return "workflow/specialmentionnotice/advancedbulkapproval";	
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
		SpecialMentionNotice tempMotion  = null;
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
					SpecialMentionNotice motion = SpecialMentionNotice.findById(SpecialMentionNotice.class,Long.parseLong(wfDetails.getDeviceId()));
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
						if(!intStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_DISCUSS) 
								&& !intStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_SENDBACK)
								/*&& !intStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PROCESSED_SENDTODEPARTMENT)
								&& !intStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PROCESSED_SENDTOSECTIONOFFICER)*/
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
						wfDetails.setAdjourningDate(motion.getSpecialMentionNoticeDate());
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
							List<Reference> actors = WorkflowConfig.findSpecialMentionNoticeActorsVO(motion,motion.getInternalStatus(),userGroup,Integer.parseInt(motion.getLevel()),locale.toString());
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
						wfDetails.setAdjourningDate(motion.getSpecialMentionNoticeDate());
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
		String specialMentionNoticeDate = request.getParameter("specialMentionNoticeDate");
		if(specialMentionNoticeDate != null && !specialMentionNoticeDate.isEmpty()){
			request.getSession().setAttribute("strSpecialMentionNoticeDate", specialMentionNoticeDate);
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
        String returnUrl = "redirect:/workflow/specialmentionnotice/advancedbulkapproval";
        return returnUrl;
//		getAdvancedBulkApproval(request, locale, model);
	}
	
	private void populateInternalStatus(final ModelMap model,
			final SpecialMentionNotice domain, final Status putupOptionsStatus,
			final String locale) {
		try{
			List<Status> internalStatuses = new ArrayList<Status>();
			DeviceType deviceType = domain.getType();
			HouseType houseType = domain.getHouseType();
			String actor = domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus = CustomParameter.
						findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
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
				CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority != null && finalApprovingAuthority.getValue().contains(usergroupType)) {
					CustomParameter finalApprovingAuthorityStatus = null;
					if(workflow!=null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(finalApprovingAuthorityStatus==null) {
							finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						}						
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
					}
					if(finalApprovingAuthorityStatus != null){
						internalStatuses=Status.
								findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					}
				}/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				}/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				}	
				/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
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
	
	private void performAction(final SpecialMentionNotice domain, HttpServletRequest request) throws ELSException {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION)){
			performActionOnAdmission(domain, request);
		} 
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECTION)){
			performActionOnRejection(domain, request);
		} 
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_CLUBBING)){
			performActionOnClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECT_CLUBBING)){
			performActionOnClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_NAMECLUBBING)){
			performActionOnNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECT_NAMECLUBBING)){
			performActionOnNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingRejectionPostAdmission(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_UNCLUBBING)){
			performActionOnUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECT_UNCLUBBING)){
			performActionOnUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnAdmissionDueToReverseClubbing(domain);
		}
	}
	
	private void performActionOnAdmission(SpecialMentionNotice domain, HttpServletRequest request) throws ELSException {
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
		SpecialMentionNotice.updateClubbing(domain);
	}
	
	private void performActionOnRejection(SpecialMentionNotice domain, HttpServletRequest request) throws ELSException {
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
		SpecialMentionNotice.updateClubbing(domain);
	}
	
	private void performActionOnClubbing(SpecialMentionNotice domain) throws ELSException {
		
		SpecialMentionNotice.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejection(SpecialMentionNotice domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		SpecialMentionNotice.unclub(domain, domain.getLocale());
		
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

	private void performActionOnNameClubbing(SpecialMentionNotice domain) throws ELSException {
		
		SpecialMentionNotice.updateClubbing(domain);

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
	
	private void performActionOnNameClubbingRejection(SpecialMentionNotice domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		SpecialMentionNotice.unclub(domain, domain.getLocale());
		
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

	private void performActionOnClubbingPostAdmission(SpecialMentionNotice domain) throws ELSException {
		
		SpecialMentionNotice.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejectionPostAdmission(SpecialMentionNotice domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		SpecialMentionNotice.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbing(SpecialMentionNotice domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		SpecialMentionNotice.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbingRejection(SpecialMentionNotice domain) throws ELSException {
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
	
	private void performActionOnAdmissionDueToReverseClubbing(SpecialMentionNotice domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION, domain.getLocale());
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
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/bulkapproval/init",method=RequestMethod.POST)
	public String getBulkApprovalInit(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDeviceType=request.getParameter("deviceType");
		String strStatus=request.getParameter("status");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String strItemsCount=request.getParameter("itemsCount");
		String strLocale=locale.toString();
		/**** usergroup,usergroupType,role *****/
		List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
		Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
		String strUserGroupType=null;
		String strUsergroup=null;
		if(userGroups!=null){
			if(!userGroups.isEmpty()){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SMIS_ALLOWED_USERGROUPTYPES", "");
				if(customParameter!=null){
					String allowedUserGroups=customParameter.getValue(); 
					for(UserGroup i:userGroups){
						if(allowedUserGroups.contains(i.getUserGroupType().getType())){
							UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
							if(ug != null){
								strUsergroup=String.valueOf(i.getId());
								strUserGroupType=i.getUserGroupType().getType();
								break;
							}
						}
					}
				}								
			}
		}		
		Set<Role> roles=this.getCurrentUser().getRoles();
		String strRole=null;
		for(Role i:roles){
			if(i.getType().startsWith("MEMBER_")){
				strRole=i.getType();
				break;
			}else if(i.getType().contains("SMIS_CLERK")){
				strRole=i.getType();
				break;
			}else if(i.getType().startsWith("SMIS_")){
				strRole=i.getType();
				break;
			}
		}		
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strDeviceType!=null&&!(strDeviceType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
			/**** List of Statuses ****/
				List<Status> internalStatuses=new ArrayList<Status>();
				HouseType houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, strLocale);
				DeviceType motionType=DeviceType.findByFieldName(DeviceType.class,"name",strDeviceType,strLocale);
				Status internalStatus=Status.findByType(strWorkflowSubType, strLocale);
				CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,motionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
				CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
				CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
				if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
					CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
					if(finalApprovingAuthorityStatus!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						} catch (ELSException e) {							
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}
				}/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					} catch (ELSException e) {						
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					} catch (ELSException e) {
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}	
				/**** SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
				else if(deviceTypeUsergroup!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					} catch (ELSException e) {
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}	
				model.addAttribute("internalStatuses",internalStatuses);
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("deviceType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUserGroupType);
			model.addAttribute("itemscount", strItemsCount);
			model.addAttribute("workflowSubType",strWorkflowSubType);
	
	
		return "workflow/specialmentionnotice/bulkapprovalinit";		
	}	

	@RequestMapping(value="/bulkapproval/view",method=RequestMethod.POST)
	public String getBulkApprovalView(final HttpServletRequest request,final Locale locale,
			final Model model){
		populateBulkApprovalView(model,request,locale.toString());
		return "workflow/specialmentionnotice/bulkapprovalview";		
	}	
	
	@Transactional
	@RequestMapping(value="/bulkapproval/update",method=RequestMethod.POST)
	public String bulkApproval(final HttpServletRequest request,final Locale locale,
			final Model model,
			final RedirectAttributes redirectAttributes){			
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus=request.getParameter("status");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String remark = request.getParameter("remarks");
		StringBuffer recommendAdmissionMsg=new StringBuffer();
		StringBuffer recommendRejectionMsg=new StringBuffer();
		StringBuffer recommendClarificationFromDepartmentMsg=new StringBuffer();
		StringBuffer recommendClarificationFromMemberMsg=new StringBuffer();
		StringBuffer admittedMsg=new StringBuffer();
		StringBuffer rejectedMsg=new StringBuffer();
		StringBuffer clarificationNeededFromDepartmentMsg=new StringBuffer();
		StringBuffer clarificationNeededFromMemberMsg=new StringBuffer();
		Status internalStatus=null;
		if(selectedItems != null && (selectedItems.length >0)
				&&strStatus!=null&&!strStatus.isEmpty()
				&&strWorkflowSubType!=null&&!strWorkflowSubType.isEmpty()) {
			Status status=null;
			if(!strStatus.equals("-")){
				status=Status.findById(Status.class,Long.parseLong(strStatus));
			}
			for(String i : selectedItems) {
					HouseType houseType=null;
					Long id = Long.parseLong(i);
					WorkflowDetails wfDetails=WorkflowDetails.findById(WorkflowDetails.class,id);
					
					SpecialMentionNotice specialMentionNotice = SpecialMentionNotice.findById(SpecialMentionNotice.class,Long.parseLong(wfDetails.getDeviceId()));
					houseType=specialMentionNotice.getHouseType();
					String actor=request.getParameter("actor");
					if(actor==null||actor.isEmpty()){
						actor=specialMentionNotice.getActor();
						String[] temp=actor.split("#");
						actor=temp[1];
					}
					String level=request.getParameter("level");
					if(level==null||level.isEmpty()){
						level=specialMentionNotice.getLevel();
					}
					if(actor!=null&&!actor.isEmpty()
							&&level!=null&&!level.isEmpty()){
						Reference reference=null;
						try {
							reference=UserGroup.findSpecialMentionNoticeActor(specialMentionNotice,actor,level,locale.toString());
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
						if(reference!=null
								&&reference.getId()!=null&&!reference.getId().isEmpty()
								&&reference.getName()!=null&&!reference.getName().isEmpty()){
							//**** Update Actor ****//
							String[] temp=reference.getId().split("#");
							specialMentionNotice.setActor(reference.getId());
							specialMentionNotice.setLocalizedActorName(temp[3]+"("+temp[4]+")");
							specialMentionNotice.setLevel(temp[2]);
							
							//**** Update Internal Status and Recommendation Status ****//
							if(status!=null){
								specialMentionNotice.setInternalStatus(status);
								specialMentionNotice.setRecommendationStatus(status);
							}
							String endFlag=null;
							specialMentionNotice.setEndFlag("continue");
							endFlag=specialMentionNotice.getEndFlag();
							
							//**** Complete Task ****//
							Map<String,String> properties=new HashMap<String, String>();
							properties.put("pv_deviceId",String.valueOf(specialMentionNotice.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(specialMentionNotice.getType().getId()));
							properties.put("pv_user",temp[0]);
							properties.put("pv_endflag",endFlag);
							
							CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "SERVERCONFIGURED", "");
							String isServerConfigured=customParameter.getValue();
							if(isServerConfigured!=null && !isServerConfigured.equals("")){
								if(isServerConfigured.equals("yes")){
									String mailflag=request.getParameter("mailflag");				
									properties.put("pv_mailflag", mailflag);
									if(mailflag!=null) {
										if(mailflag.equals("set")) {
											String mailfrom=request.getParameter("mailfrom");
											properties.put("pv_mailfrom", mailfrom);
											
											String mailto=request.getParameter("mailto");
											properties.put("pv_mailto", mailto);
											
											String mailsubject=request.getParameter("mailsubject");
											properties.put("pv_mailsubject", mailsubject);
											
											String mailcontent=request.getParameter("mailcontent");
											properties.put("pv_mailcontent", mailcontent);
										}
									}
									
									String timerflag=request.getParameter("timerflag");
									properties.put("pv_timerflag", timerflag);
									
									if(timerflag!=null) {
										if(timerflag.equals("set")) {
											String timerduration=request.getParameter("timerduration");
											properties.put("pv_timerduration", timerduration);
											
											String lasttimerduration=request.getParameter("lasttimerduration");
											properties.put("pv_lasttimerduration", lasttimerduration);
											
											String reminderflag=request.getParameter("reminderflag");
											properties.put("pv_reminderflag", reminderflag);
											
											if(reminderflag!=null) {
												if(reminderflag.equals("set")) {
													String reminderfrom=request.getParameter("reminderfrom");
													properties.put("pv_reminderfrom", reminderfrom);
													
													String reminderto=request.getParameter("reminderto");
													properties.put("pv_reminderto", reminderto);
													
													//String remindersubject=request.getParameter("remindersubject");
													String remindersubject=specialMentionNotice.getSubject();
													properties.put("pv_remindersubject", remindersubject);
													
													//String remindercontent=request.getParameter("remindercontent");
													String remindercontent=specialMentionNotice.getNoticeContent();
													properties.put("pv_remindercontent", remindercontent);
												}
											}
										}
									}
								}else{
									properties.put("pv_mailflag", "off");
									properties.put("pv_timerflag", "off");
								}
							}

							String strTaskId=wfDetails.getTaskId();
							Task task=processService.findTaskById(strTaskId);
							processService.completeTask(task,properties);	
							UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
							if(endFlag!=null&&!endFlag.isEmpty()
									&&endFlag.equals("continue")){
								//**** Create New Workflow Details ****//
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask=processService.getCurrentTask(processInstance);
								WorkflowDetails workflowDetails2;
								try {
									workflowDetails2 = WorkflowDetails.create(specialMentionNotice,newtask,usergroupType,ApplicationConstants.SPECIALMENTIONNOTICE_APPROVAL_WORKFLOW,level);
									specialMentionNotice.setWorkflowDetailsId(workflowDetails2.getId());
									specialMentionNotice.setTaskReceivedOn(new Date());
								} catch (ELSException e) {
									e.printStackTrace();
									model.addAttribute("error", e.getParameter());
								}
																
							}
							//**** Update Old Workflow Details ****//
							wfDetails.setStatus("COMPLETED");
							wfDetails.setInternalStatus(specialMentionNotice.getInternalStatus().getName());
							wfDetails.setRecommendationStatus(specialMentionNotice.getRecommendationStatus().getName());;	

							wfDetails.setCompletionTime(new Date());
							wfDetails.merge();
							//**** Update Resolution ****//
							specialMentionNotice.setEditedOn(new Date());
							specialMentionNotice.setEditedBy(this.getCurrentUser().getActualUsername());
							specialMentionNotice.setEditedAs(wfDetails.getAssigneeUserGroupName());	
							
							if(remark != null && !remark.isEmpty()){
								specialMentionNotice.setRemarks(remark);
							}
													
							try {
								performAction(specialMentionNotice, request);
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
//							/***Setting the edited On , Edited By and Edited As**
//							List<UserGroup> usergroups = this.getCurrentUser().getUserGroups();
//							Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getUsername(), locale.toString());
//							specialMentionNotice.setEditedBy(credential.getUsername());
//							for(UserGroup u : usergroups){
//								UserGroup userGroup = UserGroup.findActive(credential, u.getUserGroupType(), new Date(), locale.toString());
//								if(userGroup != null){
//									UserGroupType userGroupType = userGroup.getUserGroupType();
//									if(userGroupType != null){
//										specialMentionNotice.setEditedAs(userGroupType.getName());
//									}
//								}
//							}
							specialMentionNotice.setEditedOn(new Date());
							specialMentionNotice.merge();
							internalStatus=specialMentionNotice.getInternalStatus();	
							if(internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_ADMISSION)){
								recommendAdmissionMsg.append(specialMentionNotice.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_REJECTION)){
								recommendRejectionMsg.append(specialMentionNotice.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION)){
								admittedMsg.append(specialMentionNotice.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECTION)){
								rejectedMsg.append(specialMentionNotice.formatNumber()+",");
						}
					}					
				}			
			}			
		model.addAttribute("recommendAdmission", recommendAdmissionMsg.toString());
		model.addAttribute("recommendRejection", recommendRejectionMsg.toString());
		model.addAttribute("admitted", admittedMsg.toString());
		model.addAttribute("rejected", rejectedMsg.toString());
		model.addAttribute("recommendClarificationFromDepartment", recommendClarificationFromDepartmentMsg.toString());
		model.addAttribute("recommendClarificationFromMember", recommendClarificationFromMemberMsg.toString());
		model.addAttribute("clarificationNeededFromDepartment", clarificationNeededFromDepartmentMsg.toString());
		model.addAttribute("clarificationNeededFromMember", clarificationNeededFromMemberMsg.toString());
		populateBulkApprovalView(model,request,locale.toString());		
		}
		return "workflow/specialMentionNotice/bulkapprovalview";
	}
	
	private void populateBulkApprovalView(final Model model,
			final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDeviceType=request.getParameter("deviceType");			
		String strStatus=request.getParameter("status");
		String strRole=request.getParameter("role");
		String strUsergroup=request.getParameter("usergroup");
		String strUsergroupType=request.getParameter("usergroupType");
		String strItemsCount=request.getParameter("itemscount");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String strLocale=locale.toString();	
		String assignee=this.getCurrentUser().getActualUsername();
				
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strDeviceType!=null&&!(strDeviceType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUsergroupType!=null&&!(strUsergroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
			model.addAttribute("workflowSubType", strWorkflowSubType);
			/**** Workflow Details ****/
			List<WorkflowDetails> workflowDetails = null;
			try {
				workflowDetails = WorkflowDetails.
				findAll(strHouseType,strSessionType,strSessionYear,
						strDeviceType,strStatus,strWorkflowSubType,
						assignee,strItemsCount,strLocale);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals=new ArrayList<BulkApprovalVO>();
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			for(WorkflowDetails i:workflowDetails){
				BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
				SpecialMentionNotice specialMentionNotice=SpecialMentionNotice.findById(SpecialMentionNotice.class,Long.parseLong(i.getDeviceId()));
				/**** Bulk Submission For Workflows ****/
					/**** Status Wise Bulk Submission ****/
					
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(specialMentionNotice.getId()));				
						if(specialMentionNotice.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(specialMentionNotice.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(specialMentionNotice.getType().getName());
						bulkApprovalVO.setMember(specialMentionNotice.getPrimaryMember().getFullname());
						if(specialMentionNotice.getRevisedNoticeContent() != null && !specialMentionNotice.getRevisedNoticeContent().isEmpty()){
							bulkApprovalVO.setSubject(specialMentionNotice.getRevisedNoticeContent());
						}else{
							bulkApprovalVO.setSubject(specialMentionNotice.getNoticeContent());
						}
						if(specialMentionNotice.getRemarks()!=null&&!specialMentionNotice.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(specialMentionNotice.getRemarks());
						}else{
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(specialMentionNotice.getInternalStatus().getName());	
						Map<String, String[]> requestMap=new HashMap<String, String[]>();			
						requestMap.put("resolutionId",new String[]{String.valueOf(specialMentionNotice.getId())});
						requestMap.put("locale",new String[]{specialMentionNotice.getLocale()});
						List result=Query.findReport("SMIS_GET_REVISION", requestMap);
						bulkApprovalVO.setRevisions(result);
						bulkApprovalVO.setLastRemarkBy(specialMentionNotice.getEditedAs());	
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					
				}		
			model.addAttribute("bulkapprovals", bulkapprovals);
			if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
				model.addAttribute("resolutionId",bulkapprovals.get(0).getDeviceId());
			}
		}
	}

}
