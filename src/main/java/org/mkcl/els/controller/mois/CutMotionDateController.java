package org.mkcl.els.controller.mois;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.CutMotionDepartmentDatePriority;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/cutmotiondate")
public class CutMotionDateController extends GenericController<CutMotionDate> {
	
	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateModule(final ModelMap model,
			final HttpServletRequest request, final String locale,
			final AuthUser currentUser) {
		try{
			
			/**** Roles ****/
			Set<Role> roles = this.getCurrentUser().getRoles();
			for(Role r : roles){
				if(r.getType().startsWith("MEMBER_")){
					model.addAttribute("role",r.getType());
					break;
				}else if(r.getType().startsWith("CMOIS_")){
					model.addAttribute("role",r.getType());
					break;
				}
			}
			
			CustomParameter csptAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "CMOIS_ALLOWED_USERGROUPTYPES", "");
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			String userGroupType = null;
			if(csptAllowedUserGroups != null && csptAllowedUserGroups.getValue() != null && !csptAllowedUserGroups.getValue().isEmpty()){
				for(UserGroup i : userGroups){
					if(csptAllowedUserGroups.getValue().contains(i.getUserGroupType().getType())){
						model.addAttribute("usergroup", i.getId());
						userGroupType = i.getUserGroupType().getType();
						model.addAttribute("usergroupType",userGroupType);
						
						CustomParameter allowedStatus = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_GRID_STATUS_ALLOWED_"+ userGroupType.toUpperCase(),"");
						List<Status> status = new ArrayList<Status>();
						if (allowedStatus != null) {
							status = Status.findStatusContainedIn(allowedStatus.getValue(),locale);
						} else {
							CustomParameter defaultAllowedStatus = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_GRID_STATUS_ALLOWED_BY_DEFAULT","");
							if (defaultAllowedStatus != null) {
								status = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
							} else {
								model.addAttribute("errorcode","cutmotiondate_status_allowed_by_default_not_set");
							}
						}
						model.addAttribute("status", status);
						break;
					}
				}
			}else{
				model.addAttribute("errorcode", "cmois_allowed_usergroups_notset");
			}
			
			if(userGroupType == null){
				model.addAttribute("errorcode", "cmois_not_authorized");
			}

			/**** Device Types ****/
			List<DeviceType> deviceTypes = DeviceType.findDeviceTypesStartingWith("motions_cutmotion_", locale);
			model.addAttribute("deviceTypes", deviceTypes);
			
			/**** House Types ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			String houseType = this.getCurrentUser().getHouseType();
			
			if (houseType.equals("lowerhouse")) {
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type",houseType, "name", ApplicationConstants.ASC, locale);
			} else if (houseType.equals("upperhouse")) {
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type", houseType, "name", ApplicationConstants.ASC, locale);
			} else if (houseType.equals("bothhouse")) {
				houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}
			model.addAttribute("houseTypes", houseTypes);
			if (houseType.equals("bothhouse")) {
				houseType = "lowerhouse";
			}
			model.addAttribute("houseType", houseType);
						
			/**** Session Types Filter Starts ****/
			List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
			model.addAttribute("sessionTypes",sessionTypes);		
			HouseType authUserHouseType = HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
			Session lastSessionCreated = null;
			Integer year=new GregorianCalendar().get(Calendar.YEAR);
			try {
				lastSessionCreated = Session.findLatestSession(authUserHouseType);
				if(lastSessionCreated.getId()!=null){
					year=lastSessionCreated.getYear();
					model.addAttribute("sessionType",lastSessionCreated.getType().getId());
				}else{
					model.addAttribute("errorcode","nosessionentriesfound");
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
			/**** Session Types Filter Ends ****/

			/*** Session Year Filter Starts  ****/
			CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			List<Integer> years = new ArrayList<Integer>();
			if(houseFormationYear != null){
				Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
				for(int i = year; i >= formationYear; i--){
					years.add(i);
				}
			}else{
				model.addAttribute("errorcode", "houseformationyearnotset");
			}
			model.addAttribute("years",years);
			model.addAttribute("sessionYear",year);	
		}catch (Exception e) {
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}

	@Override
	protected void populateNew(final ModelMap model,
			final CutMotionDate domain, final String locale,
			final HttpServletRequest request) {
		
		try{
			/**** locale ****/
			domain.setLocale(locale);
			model.addAttribute("pageLocale", locale);
			model.addAttribute("formater", new FormaterUtil());
			
			/**** HouseType ****/
			String strHouseType = request.getParameter("houseType");
			HouseType houseType = null;
			if (strHouseType != null) {
				if (!strHouseType.isEmpty()) {
					houseType = HouseType.findByType(strHouseType, locale);
					if(houseType == null){
						houseType = HouseType.findById(HouseType.class, new Long(strHouseType));
					}
					model.addAttribute("houseTypeType", strHouseType);
					model.addAttribute("houseType", houseType.getId());
				}
			}
			
			/**** Selected devicetype ****/
			String strDeviceType = request.getParameter("deviceType");
			DeviceType deviceType = null; 
			if(strDeviceType != null && !strDeviceType.isEmpty()){
				deviceType= DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				model.addAttribute("selectedDeviceType", deviceType.getId());
				model.addAttribute("selectedDeviceTypeType", deviceType.getType());
				model.addAttribute("selectedDeviceTypeName", deviceType.getName());
				
				domain.setDeviceType(deviceType);
			}
			
			/**** User Group Starts ****/
			String usergroupType = request.getParameter("usergroupType");
			if(usergroupType != null){
				model.addAttribute("usergroupType",usergroupType);
			}
			
			String strUsergroup = request.getParameter("usergroup");
			if(strUsergroup != null){
				model.addAttribute("usergroup",strUsergroup);
			}
			
			String strRole = request.getParameter("role");
			if(strUsergroup != null){
				model.addAttribute("role",strRole);
			}
			
			/**** sub-department ****/
			List<SubDepartment> subdepartments = SubDepartment.findAll(SubDepartment.class, "name", ApplicationConstants.ASC, locale);
			model.addAttribute("subdepartments", subdepartments);
						
			/**** device types ****/
			List<DeviceType> deviceTypes = DeviceType.findDeviceTypesStartingWith("motions_cutmotion_", locale);
			model.addAttribute("deviceTypes", deviceTypes);
			
			/**** Load the tentative discussion dates ****/
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			Integer sessionYear = null;
			SessionType sessionType = null;
			if(strSessionYear != null && !strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()){
				sessionYear = new Integer(strSessionYear);
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				model.addAttribute("sessionYear", sessionYear.intValue());
				model.addAttribute("sessionType", sessionType.getId());
			}
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			if(session != null){
				model.addAttribute("session", session.getId());
				domain.setSession(session);
			}
			
			try{
				CutMotionDate cutMotionDate = CutMotionDate.findCutMotionDateSessionDeviceType(session, deviceType, locale);
				if(cutMotionDate != null/* && cutMotionDate.getStatus().getType().equals(ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION)*/){
					model.addAttribute("errorcode", "date_exists_for_cutmotion");
					return;
				}
			}catch(Exception e){
				logger.error("error", e);
			}
			
			populateDiscussionDates(deviceType, session, model, locale.toString());
			
			/**** department actor count ****/
			model.addAttribute("departmentCount", 0);
			/**** Created On ****/
			model.addAttribute("createdOn", FormaterUtil.getDateFormatter("en_US").format(new Date()));
			
			model.addAttribute("isDeviceTypeEmpty", true);
		}catch (Exception e) {
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}

	@Override
	protected void populateEdit(final ModelMap model,
			final CutMotionDate domain, final HttpServletRequest request) {
		try{
			String locale = domain.getLocale();
			model.addAttribute("pageLocale", locale);
			model.addAttribute("formater", new FormaterUtil());
			
			/**** User Group Starts ****/
			String usergroupType = request.getParameter("usergroupType");
			if(usergroupType != null){
				model.addAttribute("usergroupType",usergroupType);
			}else{
				usergroupType = (String) request.getSession().getAttribute("usergroupType");
				model.addAttribute("usergroupType",usergroupType);
				request.getSession().removeAttribute("usergroupType");
			}
			String strUsergroup = request.getParameter("usergroup");
			if(strUsergroup != null){
				model.addAttribute("usergroup",strUsergroup);
			}else{
				strUsergroup = (String) request.getSession().getAttribute("usergroup");
				model.addAttribute("usergroup",strUsergroup);
				request.getSession().removeAttribute("userGroup");
			}
			
			String strRole = request.getParameter("role");
			if(strRole != null){
				model.addAttribute("role",strRole);
			}else{
				strRole = (String) request.getSession().getAttribute("role");
				model.addAttribute("role",strRole);
				request.getSession().removeAttribute("role");
			}
			/**** User Group Ends ****/

			/**** Created By Starts ****/
			model.addAttribute("createdBy",domain.getCreatedBy());
			if(domain.getCreatedOn() != null){
				model.addAttribute("createdOn", FormaterUtil.formatDateToString(domain.getCreatedOn(), ApplicationConstants.SERVER_DATETIMEFORMAT, "en_US"));
			}
			if(domain.getSubmissionDate() != null){
				model.addAttribute("submissionDate", FormaterUtil.formatDateToString(domain.getSubmissionDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, "en_US"));
			}
			
			/**** HouseTypes ****/
			model.addAttribute("houseType", domain.getSession().getHouse().getType().getId());
			model.addAttribute("houseTypeType", domain.getSession().getHouse().getType());
			model.addAttribute("formattedHouseType", domain.getSession().getHouse().getName());
			
			model.addAttribute("deviceType", domain.getDeviceType().getId());
			model.addAttribute("deviceTypeType", domain.getDeviceType().getType());
			model.addAttribute("formattedDeviceType", domain.getDeviceType().getName());
			
			model.addAttribute("formattedSessionYear", FormaterUtil.formatNumberNoGrouping(domain.getSession().getYear(), domain.getLocale()));
			model.addAttribute("sessionYear", domain.getSession().getYear());
			model.addAttribute("sessionType", domain.getSession().getType().getId());
			model.addAttribute("formattedSessionType", domain.getSession().getType().getSessionType());
			model.addAttribute("sessionTypeType", domain.getSession().getType().getType());
			model.addAttribute("session", domain.getSession().getId());
			
			
			if(domain.getStatus() != null){
				model.addAttribute("status", domain.getStatus().getId());
				model.addAttribute("statusType", domain.getStatus().getType());
			}
			
			if(domain.getInternalStatus() != null){
				model.addAttribute("internalstatus", domain.getInternalStatus().getId());
				model.addAttribute("internalstatusType", domain.getInternalStatus().getType());
				model.addAttribute("formattedInternalStatus", domain.getInternalStatus().getName());
			}
			
			if(domain.getRecommendationStatus() != null){
				model.addAttribute("recommendationstatus", domain.getRecommendationStatus().getId());
				model.addAttribute("recommendationstatusType", domain.getRecommendationStatus().getType());
			}
			
			if(domain.getWorkflowStartedOn() != null){
				model.addAttribute("workflowStartedOnDate", FormaterUtil.formatDateToString(domain.getWorkflowStartedOn(), ApplicationConstants.SERVER_DATETIMEFORMAT, "en_US"));
			}			
			if(domain.getTaskReceivedOn() != null){
				model.addAttribute("taskReceivedOnDate", FormaterUtil.formatDateToString(domain.getTaskReceivedOn(), ApplicationConstants.SERVER_DATETIMEFORMAT, "en_US"));
			}
			
			/**** Subdepartment ****/
			List<SubDepartment> subdepartments = SubDepartment.findAll(SubDepartment.class, "name", ApplicationConstants.ASC, locale);
			model.addAttribute("subdepartments", subdepartments);
			
			/**** device types ****/
			List<DeviceType> deviceTypes = DeviceType.findDeviceTypesStartingWith("motions_cutmotion_", locale);
			model.addAttribute("deviceTypes", deviceTypes);
			
			/**** department actors ****/
			model.addAttribute("domainsubdepartments", domain.getDepartmentDates());
			model.addAttribute("departmentCount", domain.getDepartmentDates().size());
			
			DeviceType deviceType = domain.getDeviceType();
	        if(deviceType == null) {
	        	model.addAttribute("isDeviceTypeEmpty", true);
	        }else {
	        	model.addAttribute("isDeviceTypeEmpty", false);
	        }
	        populateDiscussionDates(domain.getDeviceType(), domain.getSession(), model, locale);
	        if(domain.getInternalStatus() != null && (domain.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTIONDATE_SYSTEM_ASSISTANT_DATE_PROCESSED)
	        		|| domain.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTIONDATE_RECOMMEND_DATE_ADMISSION)
	        		|| domain.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTIONDATE_RECOMMEND_DATE_REJECTION))){
	        	String strReqDeviceType = request.getParameter("deviceType");
	        	if(strReqDeviceType != null && !strReqDeviceType.isEmpty()){
		        	List<MasterVO> statuses = CutMotionDateControllerUtility.getStatusesForActor(request, domain.getInternalStatus(), new Locale(locale));
		        	model.addAttribute("internalStatuses", statuses);
	        	}else{
	        		List<MasterVO> statuses = CutMotionDateControllerUtility.getStatusesForActor(request, domain, new Locale(locale));
		        	model.addAttribute("internalStatuses", statuses);
	        	}
	        	if(usergroupType.equals(ApplicationConstants.ASSISTANT)){
	        		/*
	        		 * TODO have to check if current workflow level is assistant 
	        		 */
	        		model.addAttribute("level", 1);
	        	}
	        }
	        
		}catch (Exception e) {
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}
	
	private void populateDiscussionDates(DeviceType deviceType, Session session, ModelMap model, String locale){
		try{
			String strBudgetLayDate = session.getParameter(deviceType.getType() + "_budgetLayDate");
			String strSubmissionendDateFactor = session.getParameter(deviceType.getType() + "_submissionEndDateFactor");
			Date budgetLayDate = null;
			Integer submissionEndDateFactor = null;
			
			if(strBudgetLayDate != null && !strBudgetLayDate.isEmpty()){
				budgetLayDate = FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, ApplicationConstants.SYSTEM_LOCALE).parse(strBudgetLayDate);
			}
			
			if(strSubmissionendDateFactor != null && !strSubmissionendDateFactor.isEmpty()){
				submissionEndDateFactor = new Integer(strSubmissionendDateFactor);
			}
			
			if(budgetLayDate == null){
				model.addAttribute("errorcode", "budgetlaydat_setting_error");
				return;
			}
			
			Date sessionStartDate = session.getStartDate();
			Date sessionEndDate = session.getEndDate();
			List<Reference> references = new ArrayList<Reference>();
	
			if((sessionStartDate != null) && (sessionStartDate != null)){
				
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
					
					if(current.after(budgetLayDate) 
							&& !Holiday.isHolidayOnDate(current, locale)){
						dates.add(current);
					}
				}
	
				Collections.sort(dates);
	
				for(Date date: dates){
	
					Reference reference = new Reference();
	
					reference.setId(dateFormat.format(date));
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

	private void populateDepartmentDates(final CutMotionDate domain,
			final HttpServletRequest request, final BindingResult result) {
		try{
			List<CutMotionDepartmentDatePriority> cutMotionDapertmentDatePriorities = new ArrayList<CutMotionDepartmentDatePriority>();
			Integer departmentCount = Integer.parseInt(request.getParameter("departmentCount"));
			for (int i = 1; i <= departmentCount; i++) {
				CutMotionDepartmentDatePriority departmentDatePriority = null;
	
				String strDepartment = request.getParameter("departmentName" + i);
				String strDepartmentDate = request.getParameter("departmentDate" + i);
				if (strDepartment != null) {
					departmentDatePriority = new CutMotionDepartmentDatePriority();
					SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strDepartment));
					departmentDatePriority.setSubDepartment(subDepartment);
					departmentDatePriority.setDepartment(subDepartment.getDepartment());
					departmentDatePriority.setDiscussionDate(FormaterUtil.formatStringToDate(strDepartmentDate, ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale()));
					departmentDatePriority.setPriority(i);
				}
	
				String id = request.getParameter("departmentId" + i);
				if (id != null) {
					if (!id.isEmpty()) {
						departmentDatePriority.setId(Long.parseLong(id));
					}
				}
	
				String version = request.getParameter("departmentVersion" + i);
				if (version != null) {
					if (!version.isEmpty()) {
						departmentDatePriority.setVersion(Long.parseLong(version));
					}
				}
	
				String locale = request.getParameter("departmentLocale" + i);
				if (locale != null) {
					if (!locale.isEmpty()) {
						departmentDatePriority.setLocale(locale);
					}
				}
				cutMotionDapertmentDatePriorities.add(departmentDatePriority);
			}
			domain.setDepartmentDates(cutMotionDapertmentDatePriorities);
		}catch (Exception e) {
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

	@Override
	protected void preValidateCreate(final CutMotionDate domain,
			final BindingResult result, final HttpServletRequest request) {
		populateDepartmentDates(domain, request, result);
	}

	@Override
	protected void preValidateUpdate(final CutMotionDate domain,
			final BindingResult result, final HttpServletRequest request) {
		populateDepartmentDates(domain, request, result);
	}

	@Transactional
	@RequestMapping(value = "/{cutmotionDateId}/{cutmotionDepartmentDatePriorityId}/delete", method = RequestMethod.DELETE)
	public @ResponseBody String deleteCutMotionDepartmentDatePriority(
			final @PathVariable("cutmotionDateId") Long cutmotionDateId,
			final @PathVariable("cutmotionDepartmentDatePriorityId") Long cutmotionDepartmentDatePriorityId,
			final ModelMap model, final HttpServletRequest request) {
		CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, cutmotionDateId);
		
		Boolean status = false;
		if(canDelete(cutMotionDate)){
			status = CutMotionDate.removeDepartmentDatePriority(cutmotionDateId, cutmotionDepartmentDatePriorityId);
		}
		
		if (status) {
			return "SUCCESS";
		} else {
			return "FAILED";
		}
	}
	
	@Transactional
	@RequestMapping(value = "/cmd/{cutMotionDateId}/delete", method = RequestMethod.DELETE)
	public @ResponseBody
	String deleteCutMotionDate(@PathVariable(value="cutMotionDateId") Long cutMotionDateId,
			final ModelMap model, final HttpServletRequest request) {
		Role role = null;
		Boolean status = false;
		for(Role r : this.getCurrentUser().getRoles()){
			if(r.getType().equals("SUPER_ADMIN")){
				role = r;
				break;
			}
		}
		if(role.getType().equals("SUPER_ADMIN")){
			CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, cutMotionDateId);
			status = cutMotionDate.remove();
		}
		if (status) {
			return "SUCCESS";
		} else {
			return "FAILED";
		}
	}

	@Override
	public String delete(final Long id, final ModelMap model,
			final HttpServletRequest request) {
		CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, id);
		try{
			cutMotionDate.remove();
			model.addAttribute("flag", "SUCCESS");
		} catch(Exception e) {
			model.addAttribute("flag", "SUCCESS");
		}
		return "deleteinfo";
	}

	@Override
	protected void customValidateCreate(final CutMotionDate domain,
			final BindingResult result, final HttpServletRequest request) {

	}

	@Override
	protected void customValidateUpdate(final CutMotionDate domain,
			final BindingResult result, final HttpServletRequest request) {
		
	}

	@Override
	protected void populateCreateIfNoErrors(ModelMap model,
			CutMotionDate domain, HttpServletRequest request) {
		
		/**** Status ****/		
		/**** In case of submission ****/
		String operation = request.getParameter("operation");
		boolean canGoAhead = true;
		String strUserGroupType = request.getParameter("usergroupType");
		UserGroupType userGroupType = null;
		if(strUserGroupType != null){
			userGroupType = UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		/**** Status Update Starts ****/
		
		
		/**** canGoAhead=true parameter is used to check if all mandatory fields have been set(condition
		 * for complete or submit status)****/
		if(canGoAhead){
			if(operation != null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						/**** a.canGoAhead=true all mandatory fields have been field
						 * b.operation=submit ****/
						/****  submission date is set ****/
						if(domain.getSubmissionDate() == null){
							domain.setSubmissionDate(new Date());
						}
						
						/**** Status,Internal Status and recommendation Status is set ****/
						Status newstatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}else{
						/**** case of complete status ****/
						Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}else{
					/**** case of complete status ****/
					Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}else{
				/**** case of complete status ****/
				Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_COMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}
		/**** Drafts case or incomplete status case.canGo Ahead=false all mandatory fields
		 * have not been filled ****/
		else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** Status,Internal Status,Recommendation Status Update Ends ****/

		/**** creation date,created by,edited on,edited by Starts ****/
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedAs("");
		/**** creation date,created by,edited on,edited by Ends ****/		
	}

	@Override
	protected void populateAfterCreate(ModelMap model, CutMotionDate domain,
			HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
	}

	@Override
	protected void populateUpdateIfNoErrors(ModelMap model,
			CutMotionDate domain, HttpServletRequest request) {
		/**** Checking if its submission request or normal update ****/
		String operation = request.getParameter("operation");
		UserGroupType userGroupType = null;
		String strUserGroupType = request.getParameter("usergroupType");
		if(strUserGroupType != null){
			userGroupType = UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		boolean canGoAhead = true;
		/**** Question status will be complete if all mandatory fields have been filled ****/
		
		if(canGoAhead){			
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						if(domain.getSubmissionDate() == null){
							domain.setSubmissionDate(new Date());
						}
						
						Status newstatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}else{
						Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_COMPLETE, domain.getLocale());
						if(!domain.getStatus().getType().equals(ApplicationConstants.CUTMOTIONDATE_DATE_SUBMIT) && !operation.trim().equals("startworkflow")){
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}						
					}
				}else{
					Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_COMPLETE, domain.getLocale());
					if(!domain.getStatus().getType().equals(ApplicationConstants.CUTMOTIONDATE_DATE_SUBMIT)){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}
			}else{
				Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_COMPLETE, domain.getLocale());
				if(!domain.getStatus().getType().equals(ApplicationConstants.CUTMOTIONDATE_DATE_SUBMIT)){
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}
		}else{
			Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.CUTMOTIONDATE_DATE_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** Edited On,Edited By Starts ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());


		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		if(strUserGroupType != null){
			CustomParameter assistantProcessedAllowed = CustomParameter.findByName(CustomParameter.class,"CMOIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
			if(assistantProcessedAllowed != null && assistantProcessedAllowed.getValue().contains(strUserGroupType)){
				Long id = domain.getId();
				CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, id);
				String internalStatus = cutMotionDate.getInternalStatus().getType();
				
				if(internalStatus.equals(ApplicationConstants.CUTMOTIONDATE_DATE_SUBMIT)) {
					Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.CUTMOTIONDATE_SYSTEM_ASSISTANT_DATE_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}
				
				if(operation == null){
					
				}else if(operation.isEmpty()){
					
				}
			}
		}		
		/**** updating submission date and creation date ****/
		String strCreationDate = request.getParameter("setCreationDate");
		String strSubmissionDate = request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
			SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate != null){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate != null){
					domain.setCreatedOn(format.parse(strCreationDate));
				}
				if(strWorkflowStartedOnDate != null && ! strWorkflowStartedOnDate.isEmpty()){
					domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
				}
				if(strTaskReceivedOnDate != null && !strTaskReceivedOnDate.isEmpty()){
					domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void populateAfterUpdate(ModelMap model, CutMotionDate domain,
			HttpServletRequest request) {
		try{
			/**** Parameters which are read from request in populate edit needs to be saved in session Starts ****/
			request.getSession().setAttribute("role",request.getParameter("role"));
			request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
			request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
			request.getSession().setAttribute("bulkedit",request.getParameter("bulkedit"));
			/**** Parameters which are read from request in populate edit needs to be saved in session Starts ****/
			
			String strRole = request.getParameter("role");
			String strUserGroup = request.getParameter("usergroup");
			String strUserGroupType = request.getParameter("usergroupType");
			String strActor = request.getParameter("actor");
			
			String strLevel = request.getParameter("level");
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strDeviceType = request.getParameter("deviceType");
			
			HouseType houseType = null; 
			SessionType sessionType = null;
			Session session = null;
			DeviceType deviceType = null;
			Integer level = null;
			
			UserGroup userGroup = null;
			UserGroupType userGroupType = null;
			
			if(strUserGroup != null && !strUserGroup.isEmpty()){
				userGroup = UserGroup.findById(UserGroup.class, new Long(strUserGroup));
				userGroupType = userGroup.getUserGroupType();
			}
			
			if(strLevel != null && !strLevel.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()
					&& strDeviceType != null && !strDeviceType.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()){
				houseType = HouseType.findByType(strHouseType, domain.getLocale());
				if(houseType == null){
					houseType = HouseType.findById(HouseType.class, new Long(strHouseType));
				}
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, new Integer(strSessionYear));
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				level = new Integer(strLevel);
			}
					
			/**** Supporting Member Workflow/Put Up Workflow ****/
			String operation = request.getParameter("operation");
			if (operation != null) {
				if (!operation.isEmpty()) {
					if (operation.equals("startworkflow")) {
	
						ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						Map<String, String> properties = new HashMap<String, String>();
						WorkflowActor wfActor = null;
						if(strActor != null && !strActor.isEmpty()){
							wfActor = WorkflowActor.findById(WorkflowActor.class, new Long(strActor.split(";")[0]));
						}else{
							wfActor = CutMotionDateControllerUtility.getNextActor(request, userGroup, houseType, deviceType, level, domain.getLocale());
						} 
						
						if (wfActor != null) {
							User user = CutMotionDateControllerUtility.getUser(wfActor, houseType, deviceType, domain.getLocale());
							Credential credential = user.getCredential();
									
							properties.put("pv_user", credential.getUsername());
							properties.put("deviceType", domain.getDeviceType().getType());
							properties.put("deviceId", domain.getId().toString());
							properties.put("pv_endflag", "continue");
						} else {
							properties.put("pv_user", "");
							properties.put("deviceType", domain.getDeviceType().getType());
							properties.put("deviceId", domain.getId().toString());
							properties.put("pv_endflag", "end");
						}
						
						ProcessInstance processInstance = processService.createProcessInstance(processDefinition,properties);
						Task task = processService.getCurrentTask(processInstance);
						CutMotionDateControllerUtility.create(domain, this.getCurrentUser(), deviceType, session, domain.getStatus(), task, ApplicationConstants.APPROVAL_WORKFLOW, null, strActor.split(";")[1], domain.getLocale());
						
						CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, domain.getId());
						cutMotionDate.setWorkflowStarted("YES");
						cutMotionDate.setWorkflowStartedOn(new Date());
						cutMotionDate.setTaskReceivedOn(new Date());
						cutMotionDate.simpleMerge();
					}
	
				}
			}
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "cannot_populateafterupdate");
		}
	}

	@Override
	protected String modifyEditUrlPattern(String newUrlPattern,
			HttpServletRequest request, ModelMap model, String locale) {
		/**** Edit Page Starts ****/
		String edit = request.getParameter("edit");
		if(edit != null){
			if(!Boolean.parseBoolean(edit)){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}
		CustomParameter editPage = CustomParameter.findByName(CustomParameter.class, "CMOIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.findByName(CustomParameter.class, "CMOIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles = this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return newUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return newUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("CMOIS_")) {
				return newUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "cutmoriondate/error";
		/**** Edit Page Ends ****/
	}
	
	private boolean canDelete(CutMotionDate cutMotionDate){
		boolean retVal = false;
		try{
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("assignee", this.getCurrentUser().getActualUsername());
			parameters.put("locale", cutMotionDate.getLocale());
			parameters.put("status", ApplicationConstants.MYTASK_PENDING);
			parameters.put("deviceId", cutMotionDate.getId().toString());
			List<WorkflowDetails> wfDetails = WorkflowDetails.findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.ASC);
			if(wfDetails != null && wfDetails.isEmpty()){
				retVal = true;
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		return retVal;
	} 
}
