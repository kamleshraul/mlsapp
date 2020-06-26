package org.mkcl.els.controller.wf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.CutMotionDepartmentDateVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.mois.CutMotionDateControllerUtility;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.CutMotionDepartmentDatePriority;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
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
@RequestMapping("/workflow/cutmotiondate")
public class CutMotionDateWorkflowController extends BaseController {

	/** The process service. */
	@Autowired
	private IProcessService processService;

	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		
		/**** Date ****/
		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
		if (this.getUserLocale().equals(new Locale("mr", "IN"))) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(), new Locale("hi", "IN"));
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(), this.getUserLocale());
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class,new CustomDateEditor(dateFormat, true));
		}
		/**** Member ****/
		binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));
		/**** Status ****/
		binder.registerCustomEditor(Status.class, new BaseEditor(new Status()));
		/**** House Type ****/
		binder.registerCustomEditor(HouseType.class, new BaseEditor(new HouseType()));
		/**** Session ****/
		binder.registerCustomEditor(Session.class, new BaseEditor(new Session()));
		/**** Device Type ****/
		binder.registerCustomEditor(DeviceType.class, new BaseEditor(new DeviceType()));
		/**** Ministry ****/
		binder.registerCustomEditor(Ministry.class, new BaseEditor(new Ministry()));
		/**** Department ****/
		binder.registerCustomEditor(Department.class, new BaseEditor(new Department()));
		/**** Sub Department ****/
		binder.registerCustomEditor(SubDepartment.class, new BaseEditor(new SubDepartment()));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String initMyTask(final ModelMap model,
			final HttpServletRequest request, final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails = (Long) request.getAttribute("workflowdetails");
		if (longWorkflowdetails == null) {
			longWorkflowdetails = Long.parseLong(request.getParameter("workflowdetails"));
		}
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, longWorkflowdetails);
		/**** Adding workflowdetails and task to model ****/
		model.addAttribute("workflowdetails", workflowDetails.getId());
		model.addAttribute("workflowstatus", workflowDetails.getStatus());
		CutMotionDate domain = CutMotionDate.findById(CutMotionDate.class, Long.parseLong(workflowDetails.getDeviceId()));
		/**** Populate Model ****/
		populateModel(domain, model, request, workflowDetails);
		return workflowDetails.getForm();
	}

	private void populateModel(final CutMotionDate domain, final ModelMap model,
			final HttpServletRequest request,
			final WorkflowDetails workflowDetails) {
		try{
			/**** formater ****/
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("pageLocale", domain.getLocale());
			/**** clear remarks ****/
			domain.setRemarks("");
	
			/**** Locale ****/
			String locale = domain.getLocale();
	
			/**** House Type ****/
			HouseType houseType = domain.getHouseType();
			model.addAttribute("formattedHouseType", houseType.getName());
			model.addAttribute("houseType", houseType.getId());
	
			/**** Session ****/
			Session session = domain.getSession();
			model.addAttribute("session", session.getId());
	
			/**** Session Year ****/
			Integer sessionYear = 0;
			sessionYear = session.getYear();
			model.addAttribute("formattedSessionYear", FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
			model.addAttribute("sessionYear", sessionYear);
	
			/**** Session Type ****/
			SessionType sessionType = session.getType();
			model.addAttribute("formattedSessionType", sessionType.getSessionType());
			model.addAttribute("sessionType", sessionType.getId());
	
			/**** Motion Type ****/
			DeviceType motionType = domain.getDeviceType();
			model.addAttribute("formattedDeviceType", motionType.getName());
			model.addAttribute("deviceType", motionType.getId());
			model.addAttribute("selectedDeviceType", motionType.getType());
	
			/**** Ministries And Sub Departments ****/
			
			/****
			 * Submission Date,Creation date,WorkflowStartedOn date,TaskReceivedOn
			 * date
			 ****/
			CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
			if (dateTimeFormat != null) {
				if (domain.getSubmissionDate() != null) {
					model.addAttribute("submissionDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US").format(domain.getSubmissionDate()));
					model.addAttribute("formattedSubmissionDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), locale).format(domain.getSubmissionDate()));
				}
				if (domain.getCreatedOn() != null) {
					model.addAttribute("creationDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US").format(domain.getCreatedOn()));
				}
				if (domain.getWorkflowStartedOn() != null) {
					model.addAttribute("workflowStartedOnDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US").format(domain.getWorkflowStartedOn()));
				}
				if (domain.getTaskReceivedOn() != null) {
					model.addAttribute("taskReceivedOnDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US").format(domain.getTaskReceivedOn()));
				}
			}
			
			/**** Created By ****/
			model.addAttribute("createdBy", domain.getCreatedBy());
	
			/**** UserGroup and UserGroup Type ****/
			String usergroupType = workflowDetails.getAssigneeUserGroupType();
			String userGroupId = workflowDetails.getAssigneeUserGroupId();
			model.addAttribute("usergroup", workflowDetails.getAssigneeUserGroupId());
			model.addAttribute("usergroupType", workflowDetails.getAssigneeUserGroupType());
	
			/**** Status,Internal Status and recommendation Status ****/
			Status status = domain.getStatus();
			Status internalStatus = domain.getInternalStatus();
			Status recommendationStatus = domain.getRecommendationStatus();
			if (status != null) {
				model.addAttribute("status", status.getId());
				model.addAttribute("memberStatusType", status.getType());
			}
			if (internalStatus != null) {
				model.addAttribute("internalStatus", internalStatus.getId());
				model.addAttribute("internalStatusType", internalStatus.getType());
				model.addAttribute("formattedInternalStatus", internalStatus.getName());
				/**** list of put up options available ****/
				populateInternalStatus(model, domain, usergroupType, locale);
			}
			if (recommendationStatus != null) {
				model.addAttribute("recommendationStatus", recommendationStatus.getId());
				model.addAttribute("recommendationStatusType", recommendationStatus.getType());
			}
			
			/**** Populating Put up otions and Actors ****/
			if (userGroupId != null && !userGroupId.isEmpty()) {
				UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(userGroupId));
				List<MasterVO> actors = new ArrayList<MasterVO>();
				
				actors = CutMotionDateControllerUtility.getActors(houseType, motionType, userGroup, recommendationStatus, workflowDetails, locale);
				
				model.addAttribute("internalStatusSelected", internalStatus.getId());
				model.addAttribute("actors", actors);			
			}
			/**** add domain to model ****/
			model.addAttribute("domain", domain);
			
			/**** active departments as on session related date ****/
			Date onDate = new Date();
			if(onDate.compareTo(session.getStartDate())<=0) {
				onDate = session.getStartDate();
			} else if(onDate.compareTo(session.getEndDate())>=0) {
				onDate = session.getEndDate();
			}
			List<Department> departments = MemberDepartment.findActiveDepartmentsOnDate(onDate, locale);
			model.addAttribute("departments", departments);
			
			/**** device types ****/
			List<DeviceType> deviceTypes = DeviceType.findDeviceTypesStartingWith("motions_cutmotion_", locale);
			model.addAttribute("deviceTypes", deviceTypes);
			
			/**** Load the tentative discussion dates ****/
			populateDiscussionDates(domain.getDeviceType(), domain.getSession(), model, locale);
			
			/**** Load the tentative submission end dates for populated discussion dates ****/
			populateSubmissionEndDatesForDiscussionDates(domain.getDeviceType(), domain.getSession(), model, locale);
			
			/**** Load the department dates ****/
			populateDepartmentDatesInModel(domain, model, locale);
		}catch(Exception e){
			logger.error("error", e);
		}
	}

	private void populateInternalStatus(final ModelMap model,
			final CutMotionDate domain, final String usergroupType, final String locale) {
		try {
			List<Status> internalStatuses = new ArrayList<Status>();
			DeviceType deviceType = domain.getDeviceType();
			Status internaStatus = domain.getInternalStatus();
			HouseType houseType = domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class, deviceType.getType() .toUpperCase() + "_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + internaStatus.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
			if (finalApprovingAuthority != null
					&& finalApprovingAuthority.getValue().contains(usergroupType)) {
				CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUT_UP_OPTIONS_" + usergroupType.toUpperCase(), "");
				if (finalApprovingAuthorityStatus != null) {
					internalStatuses = Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				}
			}/****
			 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+
			 * USERGROUP(Post Final Status)
			 ****/
			else if (deviceTypeInternalStatusUsergroup != null) {
				internalStatuses = Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			}/****
			 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre
			 * Final Status-House Type Basis)
			 ****/
			else if (deviceTypeHouseTypeUsergroup != null) {
				internalStatuses = Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			}
			/****
			 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final
			 * Status)
			 ****/
			else if (deviceTypeUsergroup != null) {
				internalStatuses = Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
			}
			/**** Internal Status ****/
			model.addAttribute("internalStatuses", internalStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
	}

	@Transactional
	@RequestMapping(method = RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request, final Locale locale,
			@Valid @ModelAttribute("domain") final CutMotionDate domain,
			final BindingResult result) {
		
		String strOperation = request.getParameter("operation");
		
		/**** Workflowdetails ****/
		String strWfDetails = request.getParameter("workflowDetails");
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfDetails));
		String strUserGroup = workflowDetails.getAssigneeUserGroupId();
		String strUserGroupType = workflowDetails.getAssigneeUserGroupType();
		/**** Updating domain ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
		String strCreationDate = request.getParameter("setCreatedOn");
		String strSubmissionDate = request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
		if (dateTimeFormat != null) {
			SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US");
			try {
				if (strSubmissionDate != null && !strSubmissionDate.isEmpty()) {
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if (strCreationDate != null && !strCreationDate.isEmpty()) {
					domain.setCreatedOn(format.parse(strCreationDate));
				}
				if (strWorkflowStartedOnDate != null
						&& !strWorkflowStartedOnDate.isEmpty()) {
					domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
				}
				if (strTaskReceivedOnDate != null
						&& !strTaskReceivedOnDate.isEmpty()) {
					domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		UserGroup userGroup = null;
		UserGroupType userGroupType = null;
		
		if(strUserGroup != null && !strUserGroup.isEmpty()){
			userGroup = UserGroup.findById(UserGroup.class, new Long(strUserGroup));
			userGroupType = userGroup.getUserGroupType();
		}
		
		populateDepartmentDatesInDomain(domain, request, result);	
		performAction(domain);
		domain.merge();
		if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)){
			String bulkEdit = request.getParameter("bulkedit");
			if (bulkEdit == null || !bulkEdit.equals("yes")) {
				/**** Complete Task ****/
				String endflag = null;
				WorkflowActor nextActor = null;
				String strActor = request.getParameter("actor");
				Map<String, String> properties = new HashMap<String, String>();
				properties.put("pv_deviceId", String.valueOf(domain.getId()));
				properties.put("pv_deviceTypeId", String.valueOf(domain.getDeviceType().getId()));
				
				if(strActor != null && !strActor.isEmpty()){
					nextActor = WorkflowActor.findById(WorkflowActor.class, new Long(strActor.split(";")[0]));
				}else{
					nextActor = CutMotionDateControllerUtility.getNextActor(request, userGroup, domain.getHouseType(), domain.getDeviceType(), new Integer(workflowDetails.getAssigneeLevel()), domain.getLocale());
				} 
				
				if (nextActor != null) {
					User user = CutMotionDateControllerUtility.getUser(nextActor, domain.getHouseType(), domain.getDeviceType(), domain.getLocale());
					Credential credential = user.getCredential();
							
					properties.put("pv_user", credential.getUsername());
					properties.put("deviceType", domain.getDeviceType().getType());
					properties.put("deviceId", domain.getId().toString());
					properties.put("pv_endflag", "continue");
					endflag = "continue";
				} else {
					properties.put("pv_user", "");
					properties.put("deviceType", domain.getDeviceType().getType());
					properties.put("deviceId", domain.getId().toString());
					properties.put("pv_endflag", "end");
					endflag = "end";
				}
				String strTaskId = workflowDetails.getTaskId();
				Task task = processService.findTaskById(strTaskId);
				processService.completeTask(task, properties);
				
				if (endflag != null) {
					if (!endflag.isEmpty()) {
						if (endflag.equals("continue")) {
							ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
							Task newtask = processService.getCurrentTask(processInstance);
							try {
								CutMotionDateControllerUtility.create(domain, getCurrentUser(), domain.getDeviceType(), domain.getSession(), domain.getInternalStatus(), newtask, ApplicationConstants.APPROVAL_WORKFLOW, null, nextActor.getLevel().toString(), domain.getLocale());
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
							domain.setTaskReceivedOn(new Date());
						}
					}
				}
				workflowDetails.setStatus("COMPLETED");
				workflowDetails.setCompletionTime(new Date());
				workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
				workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
				workflowDetails.merge();
				/**** display message ****/
				model.addAttribute("type", "taskcompleted");
				return "workflow/info";
			}
		}
		domain.merge();
		model.addAttribute("type", "success");
		populateModel(domain, model, request, workflowDetails);
		return "workflow/cutmotion/" + userGroupType;
	}

	private void performAction(final CutMotionDate domain) {
		String internalStatus = domain.getInternalStatus().getType();
		String recommendationStatus = domain.getRecommendationStatus().getType();
		if (internalStatus.equals(ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION)
				&& recommendationStatus.equals(ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION)) {
			performActionOnAdmission(domain);
		}
	}

	private void performActionOnAdmission(final CutMotionDate domain) {
		try{
			Status finalStatus = Status.findByType(ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION, domain.getLocale());
			domain.setStatus(finalStatus);
			
//			String strBudgetLayDate = domain.getSession().getParameter(domain.getDeviceType().getType() + "_budgetLayDate");
//			String strSubmissionendDateFactor = domain.getSession().getParameter(domain.getDeviceType().getType() + "_submissionEndDateFactor");
//			Date budgetLayDate = null;
//			Integer submissionEndDateFactor = null;
//			
//			if(strBudgetLayDate != null && !strBudgetLayDate.isEmpty()){
//				budgetLayDate = FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, ApplicationConstants.SYSTEM_LOCALE).parse(strBudgetLayDate);
//			}
//			
//			if(strSubmissionendDateFactor != null && !strSubmissionendDateFactor.isEmpty()){
//				submissionEndDateFactor = new Integer(strSubmissionendDateFactor);
//			}		
//			
//			List<CutMotionDepartmentDatePriority> oldDates = domain.getDepartmentDates();
//			List<CutMotionDepartmentDatePriority> newDates = new ArrayList<CutMotionDepartmentDatePriority>();
//						
//			for(CutMotionDepartmentDatePriority dd : oldDates){
//				Date discussionDate = dd.getDiscussionDate();
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(discussionDate);
//				cal.add(Calendar.DATE, submissionEndDateFactor * (-1));
//				Date submissionEndDate = cal.getTime();			
//				dd.setSubmissionEndDate(submissionEndDate);
//				newDates.add(dd);
//			}
//			
//			domain.setDepartmentDates(newDates);
		}catch(Exception e){
			logger.error("error", e);
		}			
	}	
	
	private void populateDiscussionDates(DeviceType deviceType, Session session, ModelMap model, String locale){
		try{			
			Date budgetLayDate = null;			
			String strBudgetLayDate = session.getParameter(deviceType.getType() + "_budgetLayDate");
			if(strBudgetLayDate != null && !strBudgetLayDate.isEmpty()){
				budgetLayDate = FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, ApplicationConstants.STANDARD_LOCALE).parse(strBudgetLayDate);
			}			
			if(deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY) && budgetLayDate == null){
				model.addAttribute("errorcode", "budgetLayDate_setting_error");
				return;
			}
			
			Date sessionStartDate = session.getStartDate();
			Date sessionEndDate = session.getEndDate();
			List<Reference> references = new ArrayList<Reference>();
	
			if((sessionStartDate != null) && (sessionEndDate != null)){
				
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
	
				List<Date> dates = new ArrayList<Date>();
				CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
				SimpleDateFormat dateFormat = null;
				
				if(parameter != null){
					dateFormat = FormaterUtil.getDateFormatter(parameter.getValue(), session.getLocale());
				}
				
				start.setTime(sessionStartDate);
				end.setTime(sessionEndDate);
				for (; !start.after(end); start.add(Calendar.DATE, 1)) {
					Date current = start.getTime();
					
					if(!Holiday.isHolidayOnDate(current, locale)){
						if(deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)
								|| (deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY) && current.after(budgetLayDate))) {
							dates.add(current);
						}						
					}
				}
	
				Collections.sort(dates);
	
				for(Date date: dates){
	
					Reference reference = new Reference();
	
					reference.setId(FormaterUtil.formatDateToString(date, ApplicationConstants.DB_DATEFORMAT));
					reference.setName(dateFormat.format(date));
	
					references.add(reference);    
				}
				
				model.addAttribute("discussionDates", references);
			}
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "discussiondate_setting_error");
		}
	}
	
	private void populateSubmissionEndDatesForDiscussionDates(DeviceType deviceType, Session session, ModelMap model, String locale){
		try{
			Date budgetLayDate = null;			
			String strBudgetLayDate = session.getParameter(deviceType.getType() + "_budgetLayDate");
			if(strBudgetLayDate != null && !strBudgetLayDate.isEmpty()){
				budgetLayDate = FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, ApplicationConstants.STANDARD_LOCALE).parse(strBudgetLayDate);
			}			
			if(deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY) && budgetLayDate == null){
				model.addAttribute("errorcode", "budgetLayDate_setting_error");
				return;
			}
			
			Integer submissionEndDateFactor = null;
			String strSubmissionendDateFactor = session.getParameter(deviceType.getType() + "_submissionEndDateFactor");
			if(strSubmissionendDateFactor != null && !strSubmissionendDateFactor.isEmpty()){
				submissionEndDateFactor = new Integer(strSubmissionendDateFactor);
			}			
			if(submissionEndDateFactor == null){
				model.addAttribute("errorcode", "submissionEndDateFactor_setting_error");
				return;
			}
			
			Date sessionStartDate = session.getStartDate();
			Date sessionEndDate = session.getEndDate();
			List<Reference> references = new ArrayList<Reference>();
	
			if((sessionStartDate != null) && (sessionEndDate != null)){
				
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				
				List<Date> dates = new ArrayList<Date>();
				CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
				SimpleDateFormat dateFormat = null;
				
				if(parameter != null){
					dateFormat = FormaterUtil.getDateFormatter(parameter.getValue(), session.getLocale());
				}
				
				start.setTime(sessionStartDate);
				end.setTime(sessionEndDate);
				for (; !start.after(end); start.add(Calendar.DATE, 1)) {
					Date current = start.getTime();
					
					if(!Holiday.isHolidayOnDate(current, locale)){
						if(deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)
								|| (deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY) && current.after(budgetLayDate))) {
							
							Date submissionEndDateForCurrentDiscussionDate = Holiday.getLastWorkingDateFrom(current, submissionEndDateFactor, locale);
							
							if(submissionEndDateForCurrentDiscussionDate!=null) {
								dates.add(submissionEndDateForCurrentDiscussionDate);
							}							
						}						
					}
				}
	
				Collections.sort(dates);
	
				for(Date date: dates){
	
					Reference reference = new Reference();
	
					reference.setId(FormaterUtil.formatDateToString(date, ApplicationConstants.DB_DATEFORMAT));
					reference.setName(dateFormat.format(date));
	
					references.add(reference);    
				}
				
				model.addAttribute("submissionEndDates", references);
			}
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "discussiondate_setting_error");
		}
	}
	
	private void populateDepartmentDatesInModel(CutMotionDate domain, ModelMap model, String locale) {
		List<CutMotionDepartmentDateVO> departmentDateVOs = new ArrayList<CutMotionDepartmentDateVO>();
		if(domain.getId()!=null) {
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale});
			queryParameters.put("cutMotionDateId", new String[]{domain.getId().toString()});
			@SuppressWarnings("unchecked")
			List<Object[]> queryResult = Query.findReport("LOAD_CUTMOTIONDATE_DEPARTMENT_DATES", queryParameters);
			if(queryResult!=null && !queryResult.isEmpty()) {
				for(Object[] result: queryResult) {
					if(result!=null && result.length==5) {
						CutMotionDepartmentDateVO departmentDateVO = new CutMotionDepartmentDateVO();
						departmentDateVO.setDiscussionDate(result[0].toString());
						departmentDateVO.setFormattedDiscussionDate(result[1].toString());
						departmentDateVO.setSubmissionEndDate(result[2].toString());
						departmentDateVO.setFormattedSubmissionEndDate(result[3].toString());
						List<String[]> departments = new ArrayList<String[]>();
						if(result[4]!=null && !result[4].toString().isEmpty()) {
							String departmentsData = result[4].toString();
							for(String i: departmentsData.split("_;_")) {
								String[] departmentData = i.split("~");
								departmentData[2] = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(departmentData[2]), locale);
								departments.add(departmentData);
							}				
						}					
						departmentDateVO.setDepartments(departments);
						departmentDateVOs.add(departmentDateVO);
					}
				}
			}
		}		
		model.addAttribute("departmentDateVOs", departmentDateVOs);
	}
	
	private void populateDepartmentDatesInDomain(final CutMotionDate domain,
			final HttpServletRequest request, final BindingResult result) {
		try{
			List<CutMotionDepartmentDatePriority> departmentDates = new ArrayList<CutMotionDepartmentDatePriority>();
			Integer discussionDatesCount = Integer.parseInt(request.getParameter("discussionDatesCount"));
			for (int i = 1; i <= discussionDatesCount; i++) {
				Date discussionDate = FormaterUtil.formatStringToDate(request.getParameter("discussionDate"+i), ApplicationConstants.DB_DATEFORMAT);
				Date submissionEndDate = FormaterUtil.formatStringToDate(request.getParameter("submissionEndDate"+i), ApplicationConstants.DB_DATEFORMAT);
				Integer departmentsCount = Integer.parseInt(request.getParameter("discussionDate"+i+"_departmentsCount"));
				if(departmentsCount.intValue()==0) { //user left departments unselected
					CutMotionDepartmentDatePriority departmentDateForDiscussionDate = null;
					List<CutMotionDepartmentDatePriority> departmentDatesForDiscussionDate = domain.findDepartmentDatesForDiscussionDate(discussionDate);					
					if(departmentDatesForDiscussionDate!=null && departmentDatesForDiscussionDate.size()==1) {
						departmentDateForDiscussionDate = departmentDatesForDiscussionDate.get(0);
					} else {
						departmentDateForDiscussionDate = new CutMotionDepartmentDatePriority();
						departmentDateForDiscussionDate.setLocale(domain.getLocale());						
					}			
					departmentDateForDiscussionDate.setDiscussionDate(discussionDate);
					departmentDateForDiscussionDate.setSubmissionEndDate(submissionEndDate);
					departmentDateForDiscussionDate.setDepartment(null);
					departmentDateForDiscussionDate.setSubDepartment(null);
					departmentDateForDiscussionDate.setPriority(1);
					
					departmentDates.add(departmentDateForDiscussionDate);
				} else {
					for (int j = 1; j <= departmentsCount; j++) {
						String departmentIdStr = request.getParameter("discussionDate"+i+"_department"+j);
						String departmentPriorityStr = request.getParameter("discussionDate"+i+"_department"+j+"_priority");
						Department department = Department.findById(Department.class, Long.parseLong(departmentIdStr));
						List<CutMotionDepartmentDatePriority> departmentDatesForDepartment = domain.findDepartmentDatesForDepartment(department);
						/** load active subdepartments for the department as on session related date **/
						Date onDate = new Date();
						if(onDate.compareTo(domain.getSession().getStartDate())<=0) {
							onDate = domain.getSession().getStartDate();
						} else if(onDate.compareTo(domain.getSession().getEndDate())>=0) {
							onDate = domain.getSession().getEndDate();
						}
						Map<String, String[]> queryParameters = new HashMap<String, String[]>();
						queryParameters.put("locale", new String[]{domain.getLocale()});
						queryParameters.put("departmentId", new String[]{department.getId().toString()});
						queryParameters.put("onDate", new String[]{FormaterUtil.formatDateToString(onDate, ApplicationConstants.DB_DATEFORMAT)});
						@SuppressWarnings("unchecked")
						List<SubDepartment> subDepartments = Query.findResultListOfGivenClass("LOAD_ACTIVE_SUBDEPARTMENTS_HAVING_GIVEN_DEPARTMENT_ON_GIVEN_DATE", queryParameters, SubDepartment.class);
						for(SubDepartment sd: subDepartments) {
							CutMotionDepartmentDatePriority departmentDate = null;							
							for(CutMotionDepartmentDatePriority existingDepartmentDate: departmentDatesForDepartment) {
								if(existingDepartmentDate.getSubDepartment().getId().equals(sd.getId())) {
									departmentDate = existingDepartmentDate;
									break;
								}
							}
							if(departmentDate==null) {
								departmentDate = new CutMotionDepartmentDatePriority();
								departmentDate.setLocale(domain.getLocale());
							}
							departmentDate.setDiscussionDate(discussionDate);
							departmentDate.setSubmissionEndDate(submissionEndDate);
							departmentDate.setDepartment(department);
							departmentDate.setSubDepartment(sd);
							departmentDate.setPriority(Integer.parseInt(departmentPriorityStr));
							
							departmentDates.add(departmentDate);
						}
					}
				}				
			}
			domain.setDepartmentDates(departmentDates);
		} catch (Exception e) {
			//set existing departmentDates
			CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, domain.getId());
			if(cutMotionDate!=null) {
				domain.setDepartmentDates(cutMotionDate.getDepartmentDates());
			}
			//exception message handling
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				//model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			//model.addAttribute("error", message);
			e.printStackTrace();		
		}
	}
}
