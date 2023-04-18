package org.mkcl.els.controller.mois;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.ReferenceUnit;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("standalonemotion")
public class StandaloneController extends GenericController<StandaloneMotion>{
	@Autowired
	private IProcessService processService;

	private enum VIKAS_DEMO{V, I, K, A, S}
	@Override
	protected void populateModule(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {
		
		/****add locale****/
		model.addAttribute("moduleLocale", locale);
		
		/**** Populating filters on module page(above grid) ****/			
		DeviceType deviceType = DeviceType.findByFieldName(DeviceType.class, "type", request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** StandaloneMotion Types Filter Starts ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			deviceTypes.add(deviceType);				
			model.addAttribute("questionTypes", deviceTypes);
			model.addAttribute("questionType", deviceType.getId());
			model.addAttribute("questionTypeType", deviceType.getType());
			/**** StandaloneMotion Types Filter Ends ****/

			/**** House Types Filter Starts ****/
			// Populate House types configured for the current user
			List<HouseType> houseTypes=null;
			try {
				houseTypes = QuestionController.getHouseTypes(currentUser, deviceType, locale);
			} catch (ELSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			model.addAttribute("houseTypes", houseTypes);			
			// Populate default House type
			HouseType authUserHouseType = null;
			if(houseTypes!=null && houseTypes.size()==1) {
				authUserHouseType = houseTypes.get(0);
				model.addAttribute("houseType", authUserHouseType.getType());
			} else {
				try {
					authUserHouseType = QuestionController.getHouseType(currentUser, locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.addAttribute("houseType", authUserHouseType.getType());
			}	
			String houseType = authUserHouseType.getType();
			/**** House Types Filter Ends ****/

			/**** Session Types Filter Starts ****/
			List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
			model.addAttribute("sessionTypes",sessionTypes);		
			Session lastSessionCreated = null;
			Integer year=new GregorianCalendar().get(Calendar.YEAR);
			try {
				lastSessionCreated = Session.findLatestSession(authUserHouseType);
				if(lastSessionCreated.getId() != null){
					year=lastSessionCreated.getYear();
					model.addAttribute("sessionType", lastSessionCreated.getType().getId());
				}else{
					model.addAttribute("errorcode", "nosessionentriesfound");
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
				Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
				for(int i=year;i>=formationYear;i--){
					years.add(i);
				}
			}else{
				model.addAttribute("errorcode", "houseformationyearnotset");
			}
			model.addAttribute("years",years);
			model.addAttribute("sessionYear",year);	
			/*** Session Year Filter Ends  ****/

			/**** User group Filter Starts 
			 * a.any user can have only one user group per device type
			 * b.any user can have multiple user groups limited to one user group per device type 
			 * c.Custom parameter=SMOIS_ALLOWED_USERGROUPTYPES controls which user groups can access
			 * SMOIS
			 * d.usergroup-id of authenticated user's user group
			 * e.usergroupType-type of authenticated user's user group
			 * f.Custom parameter=SMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR controls which user group can see sub department filter
			 * g.Custom parameterSTANDALONE_GRID_STATUS_ALLOWED_USERGROUPTYPE controls 
			 * which status will be seen in status filter
			 * h.Custom parameter=STANDALONE_GRID_STATUS_ALLOWED_BY_DEFAULT controls which status 
			 * will be seen by default if above filter is not set.
			 * ****/
			UserGroup userGroup = null;
			UserGroupType userGroupType = null;
			List<UserGroup> userGroups = currentUser.getUserGroups();
			if(userGroups != null && ! userGroups.isEmpty()) {
				CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "SMOIS_ALLOWED_USERGROUPTYPES", "");
				if(cp != null) {
					List<UserGroupType> configuredUserGroupTypes = 
							StandaloneController.delimitedStringToUGTList(cp.getValue(), ",", locale);
					
					try {
						userGroup = StandaloneController.getUserGroup(userGroups, configuredUserGroupTypes, lastSessionCreated, locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					userGroupType = userGroup.getUserGroupType();
					
					model.addAttribute("usergroup", userGroup.getId());
					model.addAttribute("usergroupType", userGroupType.getType());
				}
				else {
//					throw new ELSException("StandaloneController.populateModule/4", 
//							"SMOIS_ALLOWED_USERGROUPTYPES key is not set as CustomParameter");
					logger.error("SMOIS_ALLOWED_USERGROUPTYPES key is not set as CustomParameter");
				}
			}
			if(userGroup == null || userGroupType == null) {
//				throw new ELSException("StandaloneController.populateModule/4", 
//						"User group or User group type is not set for Username: " + currentUser.getUsername());
				
				model.addAttribute("errorcode","current_user_has_no_usergroups");
			}
			
			// Populate Sub Departments configured for this User's user group type
			Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
			CustomParameter subDepartmentFilterAllowedFor = 
					CustomParameter.findByName(CustomParameter.class, "SMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
			if(subDepartmentFilterAllowedFor != null) {
				List<UserGroupType> ugtConfiguredForSubdepartments = 
						StandaloneController.delimitedStringToUGTList(
								subDepartmentFilterAllowedFor.getValue(), ",", locale);
				boolean isUGTConfiguredForSubdepartments = 
						StandaloneController.isUserGroupTypeExists(ugtConfiguredForSubdepartments, userGroupType);
				if(isUGTConfiguredForSubdepartments) {
					String subDepartmentParam = parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale);
					if(subDepartmentParam != null && ! subDepartmentParam.equals("")) {
						List<SubDepartment> subDepartments =
								StandaloneController.getSubDepartments(subDepartmentParam, "##", locale);
						model.addAttribute("subDepartments", subDepartments);
					}
					else {
//						throw new ELSException("StandaloneController.populateModule/4", 
//								"SUBDEPARTMENT parameter is not set for Username: " + currentUser.getUsername());
						logger.error("SUBDEPARTMENT parameter is not set for Username: " + currentUser.getUsername());
					}
				}
			}
			else {
//				throw new ELSException("StandaloneController.populateModule/4", 
//						"SMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR key is not set as CustomParameter");
				logger.error("SMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR key is not set as CustomParameter");
			}
			
			// Populate Statuses configured for this User's user group type
			/**
			 * Rules:
			 * Search for the Custom parameters in following order in order to get the statuses configured
			 * for this User's user group type
			 * a. CustomParameter "STANDALONE_GRID_STATUS_ALLOWED_" + deviceTypeType + userGroupTypeType
			 * b. CustomParameter "STANDALONE_GRID_STATUS_ALLOWED_BY_DEFAULT" + deviceTypeType
			 */
			String strAllowedStatus = null;
			CustomParameter allowedStatusParam = 
					CustomParameter.findByName(CustomParameter.class, 
							"STANDALONE_GRID_STATUS_ALLOWED_" + userGroupType.getType().toUpperCase(), "");
			if(allowedStatusParam != null) {
				strAllowedStatus = allowedStatusParam.getValue();
			}
			else {
				CustomParameter defaultAllowedStatusParam =
						CustomParameter.findByName(CustomParameter.class,
								"STANDALONE_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
				if(defaultAllowedStatusParam != null) {
					strAllowedStatus = defaultAllowedStatusParam.getValue();
				}
				else {
//					throw new ELSException("StandaloneController.populateModule/4", 
//							"STANDALONE_GRID_STATUS_ALLOWED_BY_DEFAULT_ + deviceTypeType " +
//							"key is not set as CustomParameter");
					logger.error("STANDALONE_GRID_STATUS_ALLOWED_BY_DEFAULT_ + deviceTypeType " +
							"key is not set as CustomParameter");
				}
			}
			
			if(strAllowedStatus != null) {
				List<Status> statuses = null;
				try {
					statuses = Status.findStatusContainedIn(strAllowedStatus, locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.addAttribute("status", statuses);
			}

			/**** Roles Filter Starts 
			 * a.SMOIS roles starts with SMOIS_,MEMBER_,
			 * b.any user will have single role per device type
			 * c.any user can have multiple roles limited to one role per device type
			 * ****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().startsWith("SMOIS_")){
					model.addAttribute("role",i.getType());
					break;
				}
			}
			
			/**** File Options(Obtain Dynamically) ****/
			if (userGroupType != null && !userGroupType.getType().isEmpty()
					&& userGroupType.getType().equals("assistant")) {
				int highestFileNo = StandaloneMotion.findHighestFileNo(lastSessionCreated, deviceType, locale);
				model.addAttribute("highestFileNo", highestFileNo);
			}
			
			/**** Roles Filter Ends ****/			

			/*** ugparam Filter Starts 
			 * a.ugparam controls what data will be visible in grid
			 * b.member will see data created by them
			 * c.typists will see data created off line
			 * d.others will see data according to the groups allowed
			 *  ****/
			String strgroups = this.getCurrentUser().getGroupsAllowed();
			model.addAttribute("allowedGroups",strgroups);
			if(strgroups != null){
				if(!strgroups.isEmpty()){
					List<Group> groups = new ArrayList<Group>();
					String[] gr = strgroups.split(",");
					for(String k:gr){
						Group group = null;
						try {
							group = Group.findByNumberHouseTypeSessionTypeYear(Integer.parseInt(k),  authUserHouseType, lastSessionCreated.getType(), year);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
							e.printStackTrace();
						}
						if(group!=null){
							groups.add(group);
						}
					}
					model.addAttribute("groups",groups);
					if(!groups.isEmpty()){
						model.addAttribute("ugparam",groups.get(0).getId());
					}
					
					
				}else{
					model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());
				}
			}else{
				model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());
			}
			/*** ugparam Filter Ends ****/	
			
			/****Member's Standalone Motions Views Visibility Parameters****/
			Boolean sessionEndDateFlag = false;
			Date sessionEndDate = lastSessionCreated.getEndDate();
			if(sessionEndDate!=null) {
				String sessionEndDateTimeStr = FormaterUtil.formatDateToString(sessionEndDate, ApplicationConstants.DB_DATEFORMAT);
				CustomParameter visibilityStartTimeCP = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_START_TIME_FOR_MEMBER_STANDALONEMOTIONS_VIEW_"+houseType.toUpperCase(), "");
				if(visibilityStartTimeCP!=null && visibilityStartTimeCP.getValue()!=null) {
					sessionEndDateTimeStr = sessionEndDateTimeStr + " " + visibilityStartTimeCP.getValue();
					Date sessionEndDateTime = FormaterUtil.formatStringToDate(sessionEndDateTimeStr, ApplicationConstants.DB_DATETIME_FORMAT);
					if(new Date().compareTo(sessionEndDateTime)>=0) {
						sessionEndDateFlag = true;
					}
				}
			}
			
			Boolean statusFlag = false;		
			CustomParameter statusFlagForMemberStandaloneMotionsView = CustomParameter.findByName(CustomParameter.class, "STATUS_FLAG_FOR_MEMBER_STANDALONEMOTIONS_VIEW_"+houseType.toUpperCase(), "");
			if(statusFlagForMemberStandaloneMotionsView!=null && statusFlagForMemberStandaloneMotionsView.getValue()!=null
					&& statusFlagForMemberStandaloneMotionsView.getValue().equals("visible")) {
				statusFlag = true; 
			}
			if(statusFlag.equals(true) && sessionEndDateFlag.equals(true)) {
				model.addAttribute("member_standalonemotions_view_status_flag", "status_visible");
			}
			
			Boolean visibilityFlagForAdmitted = false;
			CustomParameter visibilityFlagForMemberAdmittedStandaloneMotionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_ADMITTED_STANDALONEMOTIONS_VIEW_"+houseType.toUpperCase(), "");
			if(visibilityFlagForMemberAdmittedStandaloneMotionsView!=null && visibilityFlagForMemberAdmittedStandaloneMotionsView.getValue()!=null
					&& visibilityFlagForMemberAdmittedStandaloneMotionsView.getValue().equals("visible")) {
				visibilityFlagForAdmitted = true; 
			}
			if(visibilityFlagForAdmitted.equals(true) && sessionEndDateFlag.equals(true)) {
				model.addAttribute("member_admitted_standalonemotions_view_flag", "admitted_visible");
			}
			
			Boolean visibilityFlagForRejected = false;
			CustomParameter visibilityFlagForMemberRejectedStandaloneMotionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_REJECTED_STANDALONEMOTIONS_VIEW_"+houseType.toUpperCase(), "");
			if(visibilityFlagForMemberRejectedStandaloneMotionsView!=null && visibilityFlagForMemberRejectedStandaloneMotionsView.getValue()!=null
					&& visibilityFlagForMemberRejectedStandaloneMotionsView.getValue().equals("visible")) {
				visibilityFlagForRejected = true; 
			}
			if(visibilityFlagForRejected.equals(true) && sessionEndDateFlag.equals(true)) {
				model.addAttribute("member_rejected_standalonemotions_view_flag", "rejected_visible");
			}
		}else{
			model.addAttribute("errorcode","workunderprogress");
		}		
		/**** File Options Filter Ends ****/
	}

	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		/**** Controlling Grids Starts ****/
		String role = request.getParameter("role");
		String houseType = request.getParameter("houseType");
		String newUrlPattern = urlPattern;
		CustomParameter assistantGridAllowedFor = CustomParameter.findByName(CustomParameter.class,"SMOIS_ASSISTANTGRID_ALLOWED_FOR","");
		CustomParameter memberGridAllowedFor = CustomParameter.findByName(CustomParameter.class,"SMOIS_MEMBERGRID_ALLOWED_FOR","");
		CustomParameter typistGridAllowedFor = CustomParameter.findByName(CustomParameter.class,"SMOIS_TYPISTGRID_ALLOWED_FOR","");
		if(memberGridAllowedFor != null
				&& role != null && !role.isEmpty() && houseType != null
				&& !houseType.isEmpty() && memberGridAllowedFor.getValue().contains(role)){
			newUrlPattern = urlPattern+"?usergroup=member&houseType="+houseType;
		}else if(typistGridAllowedFor != null && role != null 
				&& !role.isEmpty() && houseType != null && !houseType.isEmpty()
				&& typistGridAllowedFor.getValue().contains(role)){
			newUrlPattern = urlPattern+"?usergroup=typist&houseType="+houseType;
		}else if(assistantGridAllowedFor != null && role != null && !role.isEmpty()
				&& houseType != null && !houseType.isEmpty() && assistantGridAllowedFor.getValue().contains(role)){
			
			newUrlPattern = urlPattern+"?usergroup=assistant&houseType="+houseType;
			
		}	
		/**** Controlling Grids Ends ****/
		return newUrlPattern;
	}


	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		/**** New Operations Allowed For Starts ****/
		String role=request.getParameter("role");	
		CustomParameter newOperationAllowedTo=CustomParameter.findByName(CustomParameter.class,"SMOIS_NEW_OPERATION_ALLOWED_TO","");
		if(newOperationAllowedTo!=null&&role!=null&&!role.isEmpty()&&newOperationAllowedTo.getValue().contains(role)){
			return servletPath;			
		}		
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
		/**** New Operations Allowed For Starts ****/
	}

	@Override
	protected void populateNew(final ModelMap model, final StandaloneMotion domain, final String locale,
			final HttpServletRequest request) {
		/**** Locale Starts ****/
		domain.setLocale(locale);	
		/**** Locale Ends ****/

		/**** Subject Starts ****/
		String subject = request.getParameter("subject");
		if(subject != null){
			domain.setSubject(subject);
		}
		/**** Subject Ends ****/

		/*** Question Text Starts ****/
		String questionText = request.getParameter("questionText");
		if(questionText != null){
			domain.setQuestionText(questionText);
		}
		/*** Question Text Ends ****/

		/**** House Type Starts ****/
		String selectedHouseType = request.getParameter("houseType");
		HouseType houseType = null;
		if(selectedHouseType != null){
			if(!selectedHouseType.isEmpty()){				
				try {
					Long houseTypeId = Long.parseLong(selectedHouseType);
					houseType=HouseType.findById(HouseType.class,houseTypeId);
				} catch (NumberFormatException e) {
					houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
				}
				model.addAttribute("formattedHouseType",houseType.getName());
				model.addAttribute("houseTypeType", houseType.getType());
				model.addAttribute("houseType",houseType.getId());
			}else{
				logger.error("**** Check request parameter 'houseType' for no value ****");
				model.addAttribute("errorcode","houseType_isempty");	
			}
		}else{
			logger.error("**** Check request parameter 'houseType' for null value ****");
			model.addAttribute("errorcode","houseType_isnull");
		}
		/**** House Type Ends ****/

		/**** Session Year Starts ****/
		String selectedYear = request.getParameter("sessionYear");
		Integer sessionYear = 0;
		if(selectedYear != null){
			if(!selectedYear.isEmpty()){
				sessionYear = Integer.parseInt(selectedYear);
				model.addAttribute("formattedSessionYear",
						FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
				model.addAttribute("sessionYear",sessionYear);
			}else{
				logger.error("**** Check request parameter 'sessionYear' for no value ****");
				model.addAttribute("errorcode","sessionYear_isempty");
			}
		}else{
			logger.error("**** Check request parameter 'sessionYear' for null value ****");
			model.addAttribute("errorcode","sessionyear_isnull");
		}   
		/**** Session Year Ends ****/

		/**** Session Type Starts ****/
		String selectedSessionType = request.getParameter("sessionType");
		SessionType sessionType = null;
		if(selectedSessionType != null){
			if(!selectedSessionType.isEmpty()){
				sessionType = SessionType.findById(SessionType.class,Long.parseLong(selectedSessionType));
				model.addAttribute("formattedSessionType",sessionType.getSessionType());
				model.addAttribute("sessionType",sessionType.getId());
			}else{
				logger.error("**** Check request parameter 'sessionType' for no value ****");
				model.addAttribute("errorcode","sessionType_isempty");	
			}
		}else{
			logger.error("**** Check request parameter 'sessionType' for null value ****");
			model.addAttribute("errorcode","sessionType_isnull");
		}
		/**** Session Type Ends ****/

		/**** Question Type Starts ****/
		String selectedQuestionType = request.getParameter("questionType");
		if(selectedQuestionType == null){
			selectedQuestionType = request.getParameter("type");
		}
		DeviceType questionType = null;
		if(selectedQuestionType != null){
			if(!selectedQuestionType.isEmpty()){
				questionType = DeviceType.findById(DeviceType.class,Long.parseLong(selectedQuestionType));
				model.addAttribute("formattedQuestionType", questionType.getName());
				model.addAttribute("questionType", questionType.getId());
				model.addAttribute("deviceType", questionType.getId());
				model.addAttribute("selectedQuestionType", questionType.getType());
			}else{
				logger.error("**** Check request parameter 'questionType' for no value ****");
				model.addAttribute("errorcode","questionType_isempty");		
			}
		}else{
			logger.error("**** Check request parameter 'questionType' for null value ****");
			model.addAttribute("errorcode","questionType_isnull");
		}
		/**** Question Type Starts ****/

		/**** Role Starts ****/
		String role = request.getParameter("role");
		if(role != null){
			model.addAttribute("role",role);
		}else{
			role = (String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		/**** Role Ends ****/

		/**** User Group Starts ****/
		String usergroupType = request.getParameter("usergroupType");
		if(usergroupType != null){
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType = (String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		String usergroup = request.getParameter("usergroup");
		if(usergroup != null){
			model.addAttribute("usergroup",usergroup);
		}else{
			usergroup = (String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",usergroup);
			request.getSession().removeAttribute("usergroup");
		}
		/**** User Group Ends ****/		

		/**** Session Starts ****/
		Session selectedSession = null;
		if(houseType != null && selectedYear != null && sessionType != null){
			try {
				selectedSession = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			} catch (ELSException e1) {
				model.addAttribute("error", e1.getParameter());
				e1.printStackTrace();
			}
			if(selectedSession != null){
				model.addAttribute("session",selectedSession.getId());
			}else{
				logger.error("**** Session doesnot exists ****");
				model.addAttribute("errorcode","session_isnull");	
			}
		}else{
			logger.error("**** Check request parameters 'houseType,sessionYear and sessionType for null values' ****");
			model.addAttribute("errorcode","requestparams_isnull");
		}  
		/**** Session Ends ****/

		/**** Primary Member Starts ****/	
		String memberNames = null;
		String primaryMemberName = null;
		if(role.startsWith("MEMBER")){
			Member member = Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale);
			if(member != null){
				model.addAttribute("primaryMember",member.getId());
				primaryMemberName = member.getFullname();
				memberNames = primaryMemberName;
				model.addAttribute("formattedPrimaryMember",primaryMemberName);
			}else{
				logger.error("**** Authenticated user is not a member ****");
				model.addAttribute("errorcode","member_isnull");
			}
			Long houseId = selectedSession.getHouse().getId();
			MasterVO constituency = null;
			if(houseType.getType().equals("lowerhouse")){
				if(member != null){
					constituency = Member.findConstituencyByAssemblyId(member.getId(), houseId);
					model.addAttribute("constituency",constituency.getName());
				}
			}else if(houseType.getType().equals("upperhouse")){
				Date currentDate = new Date();
				String date = FormaterUtil.getDateFormatter("en_US").format(currentDate);
				if(member != null){
					constituency = Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
					model.addAttribute("constituency",constituency.getName());
				}
			}
		}
		/**** Primary Member Ends ****/

		/**** Ministries, Sub Departments,Groups, discussion Dates Starts ****/
		List<Ministry> ministries = null;
		try {			
			CustomParameter csptDeviceTypesHavingGroup = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_GROUPS+"_"+houseType.getType(), locale);
			if(csptDeviceTypesHavingGroup!=null && csptDeviceTypesHavingGroup.getValue()!=null && csptDeviceTypesHavingGroup.getValue().contains(questionType.getType())) {
				ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
			} else {
				ministries = Ministry.findAssignedMinistriesInSession(selectedSession.getStartDate(), locale);
			}			
		} catch (ELSException e) {
			e.printStackTrace();
		}
		model.addAttribute("ministries",ministries);
		Ministry ministry = domain.getMinistry();
		if(ministry != null){
			model.addAttribute("ministrySelected",ministry.getId());
			Group group = domain.getGroup();
			if(group != null) {
				model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
				model.addAttribute("group",domain.getGroup().getId());
			}			
			List<SubDepartment> subDepartments = MemberMinister.
					findAssignedSubDepartments(ministry, selectedSession.getEndDate(), locale);
			model.addAttribute("subDepartments",subDepartments);
			SubDepartment subDepartment = domain.getSubDepartment();
			if(subDepartment != null){
				model.addAttribute("subDepartmentSelected",subDepartment.getId());
			}				
		}
		/**** Ministries,Departments,Sub Departments,Groups,Answering Dates Ends ****/

		/**** Priorities Starts ****/
		CustomParameter customParameter = 
				CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter != null){
			List<MasterVO> priorities = new ArrayList<MasterVO>();
			for(int i = 1;i <= Integer.parseInt(customParameter.getValue()); i++){
				priorities.add(new MasterVO(i,FormaterUtil.getNumberFormatterNoGrouping(locale).format(i)));
			}
			model.addAttribute("priorities",priorities);
		}else{
			logger.error("**** Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set ****");
			model.addAttribute("errorcode","highestquestionprioritynotset");
		}
		/**** Priorities Ends ****/

		/**** Supporting Members Starts ****/
		List<SupportingMember> selectedSupportingMembers = domain.getSupportingMembers();
		List<Member> supportingMembers = new ArrayList<Member>();
		if(selectedSupportingMembers != null){
			if(!selectedSupportingMembers.isEmpty()){
				StringBuffer bufferFirstNamesFirst = new StringBuffer();
				for(SupportingMember i : selectedSupportingMembers){
					Member m = i.getMember();
					bufferFirstNamesFirst.append(m.getFullname()+",");
					supportingMembers.add(m);
				}
				bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
				model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
				model.addAttribute("supportingMembers", supportingMembers);
				memberNames = primaryMemberName + "," + bufferFirstNamesFirst.toString();
				model.addAttribute("memberNames", memberNames);
			}else{
				model.addAttribute("memberNames",memberNames);
			}
		}else{
			model.addAttribute("memberNames",memberNames);
		}
		/**** Supporting Members Ends ****/

		if(questionType != null){
			if(questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				populateForHalfHourDiscussionNew(model, domain, selectedSession, questionType, request);
			}
		}
	}

	@Override
	protected String modifyEditUrlPattern(final String newUrlPattern,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		/**** Edit Page Starts ****/
		String edit = request.getParameter("edit");
		if(edit != null){
			if(!Boolean.parseBoolean(edit)){
				return newUrlPattern.replace("edit", "editreadonly");
			}
		}
		/**** for printing ****/
		String editPrint = request.getParameter("editPrint");
		if(editPrint != null){
			if(Boolean.parseBoolean(editPrint)){
				return newUrlPattern.replace("edit", "editprint");
			}
		}
		
		CustomParameter editPage = CustomParameter.findByName(CustomParameter.class, "SMOIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.findByName(CustomParameter.class, "SMOIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return newUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return newUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("SMOIS_")) {
				return newUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "standalonemotion/error";
		/**** Edit Page Ends ****/
	}


	@Override
	protected void populateEdit(final ModelMap model, final StandaloneMotion domain,
			final HttpServletRequest request) {
		/**** Locale Starts ****/
		String locale = domain.getLocale();
		/**** Locale Ends ****/

		/**** House Type Starts ****/
		HouseType houseType = domain.getHouseType();
		model.addAttribute("formattedHouseType", houseType.getName());
		model.addAttribute("houseTypeType", houseType.getType());
		model.addAttribute("houseType", houseType.getId());
		/**** House Type Ends ****/

		/**** Session Starts ****/
		Session selectedSession = domain.getSession();
		model.addAttribute("session",selectedSession.getId());
		/**** Session Ends ****/

		/**** Session Year Starts ****/
		Integer sessionYear = 0;
		sessionYear = selectedSession.getYear();
		model.addAttribute("formattedSessionYear", FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear", sessionYear);
		/**** Session Year Ends ****/

		/**** Session Type Starts ****/
		SessionType  sessionType = selectedSession.getType();
		model.addAttribute("formattedSessionType", sessionType.getSessionType());
		model.addAttribute("sessionType", sessionType.getId());  
		/**** Session Type Ends ****/

		/**** Device Type Starts ****/
		DeviceType questionType = domain.getType();
		model.addAttribute("formattedQuestionType", questionType.getName());
		model.addAttribute("questionType", questionType.getId());
		model.addAttribute("deviceType", questionType.getId());
		model.addAttribute("selectedQuestionType", questionType.getType());
		/**** Device Type Ends ****/		

		/**** Primary Member Starts ****/
		String memberNames = null;
		String primaryMemberName = null;
		Member member = domain.getPrimaryMember();
		if(member != null){
			model.addAttribute("primaryMember", member.getId());
			primaryMemberName = member.getFullname();
			memberNames = primaryMemberName;
			model.addAttribute("formattedPrimaryMember", primaryMemberName);
			Long houseId = selectedSession.getHouse().getId();
			MasterVO constituency = null;
			if(houseType.getType().equals("lowerhouse")){
				constituency = Member.findConstituencyByAssemblyId(member.getId(), houseId);
				model.addAttribute("constituency", constituency.getName());
			}else if(houseType.getType().equals("upperhouse")){
				Date currentDate = new Date();
				String date = FormaterUtil.getDateFormatter("en_US").format(currentDate);
				constituency = Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
				model.addAttribute("constituency",constituency.getName());
			}
		}
		/**** Primary Member Ends ****/

		/**** Supporting Members Starts ****/
		List<SupportingMember> selectedSupportingMembers = domain.getSupportingMembers();
		List<Member> supportingMembers = new ArrayList<Member>();
		Date currentDate = new Date();
		if(selectedSupportingMembers != null){
			if(!selectedSupportingMembers.isEmpty()){
				StringBuffer bufferFirstNamesFirst = new StringBuffer();
				for(SupportingMember i : selectedSupportingMembers){
					Member m = i.getMember();
					if(m.isActiveMemberOn(currentDate, locale)){
						bufferFirstNamesFirst.append(m.getFullname() + ",");
						supportingMembers.add(m);
					}
				}
				model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
				model.addAttribute("supportingMembers", supportingMembers);
				memberNames=primaryMemberName + "," + bufferFirstNamesFirst.toString();
				model.addAttribute("memberNames", memberNames);
			}else{
				model.addAttribute("memberNames", memberNames);
			}
		}else{
			model.addAttribute("memberNames", memberNames);
		}
		/**** Supporting Members Ends ****/

		/**** Priorities Starts ****/
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter != null){
			List<MasterVO> priorities = new ArrayList<MasterVO>();
			for(int i = 1; i <= Integer.parseInt(customParameter.getValue()); i++){
				priorities.add(new MasterVO(i,FormaterUtil.getNumberFormatterNoGrouping(locale).format(i)));
			}
			model.addAttribute("priorities", priorities);
		}else{
			logger.error("**** Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set ****");
			model.addAttribute("errorcode","highestquestionprioritynotset");
		}
		if(domain.getPriority() != null){
			model.addAttribute("priority", domain.getPriority());
			model.addAttribute("formattedPriority", FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getPriority()));
		}
		/**** Priorities Ends  ****/

		/**** Ministries,Departments,Sub departments,Groups,Answering Dates Starts ****/
		List<Ministry> ministries = null;
		try {
			CustomParameter csptDeviceTypesHavingGroup = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_GROUPS+"_"+houseType.getType(), locale);
			if(csptDeviceTypesHavingGroup!=null && csptDeviceTypesHavingGroup.getValue()!=null && csptDeviceTypesHavingGroup.getValue().contains(questionType.getType())) {
				ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
			} else {
				ministries = Ministry.findAssignedMinistriesInSession(selectedSession.getStartDate(), locale);
			}
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			e.printStackTrace();
		}
		model.addAttribute("ministries", ministries);
		Ministry ministry = domain.getMinistry();
		if(ministry != null){
			model.addAttribute("ministrySelected", ministry.getId());
			model.addAttribute("formattedMinistry", ministry.getDropdownDisplayName());
			if(domain.getGroup() != null){
				model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
				model.addAttribute("group", domain.getGroup().getId());
			}
			
			List<Department> departments = MemberMinister.findAssignedDepartments(ministry, selectedSession.getEndDate(), locale);
			model.addAttribute("departments", departments);
			
			List<SubDepartment> subDepartments = MemberMinister.
					findAssignedSubDepartments(ministry,selectedSession.getEndDate(),  locale);
			model.addAttribute("subDepartments",subDepartments); 
			
			SubDepartment subDepartment = domain.getSubDepartment();
			if(subDepartment != null){
				model.addAttribute("subDepartmentSelected", subDepartment.getId());
				model.addAttribute("departmentSelected", domain.getSubDepartment().getDepartment().getId());
			}
		}
		/**** Ministries,Departments,Sub departments,Groups,Answering Dates Ends ****/

		/**** Submission Date and Creation date Starts ****/ 
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat != null){            
			if(domain.getSubmissionDate() != null){
				model.addAttribute("submissionDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(domain.getCreationDate() != null){
				model.addAttribute("creationDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
			}
			if(domain.getWorkflowStartedOn() != null){
				model.addAttribute("workflowStartedOnDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOn()));
			}
			if(domain.getTaskReceivedOn() != null){
				model.addAttribute("taskReceivedOnDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOn()));
			}
		}
		/**** Submission Date and Creation date Ends ****/ 

		/**** Number Starts ****/
		if(domain.getNumber() != null){
			model.addAttribute("formattedNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		/**** Number Ends ****/

		/**** Role Starts ****/
		String role = request.getParameter("role");
		if(role != null){
			model.addAttribute("role", role);
		}else{
			role = (String)request.getSession().getAttribute("role");
			model.addAttribute("role", role);
			request.getSession().removeAttribute("role");
		}
		/**** Role Ends ****/

		/**** User Group Starts ****/
		String usergroupType = request.getParameter("usergroupType");
		if(usergroupType != null){
			model.addAttribute("usergroupType", usergroupType);
		}else{
			usergroupType = (String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType", usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		
		String strUsergroup = request.getParameter("usergroup");
		if(strUsergroup != null){
			model.addAttribute("usergroup", strUsergroup);
		}else{
			strUsergroup = (String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup", strUsergroup);
			request.getSession().removeAttribute("userGroup");
		}
		/**** User Group Ends ****/

		/**** Created By Starts ****/
		model.addAttribute("createdBy", domain.getCreatedBy());
		/**** Created By Ends ****/

		/**** Bulk Edit Starts****/
		String bulkedit = request.getParameter("bulkedit");
		if(bulkedit != null){
			model.addAttribute("bulkedit", bulkedit);
		}else{
			bulkedit = (String) request.getSession().getAttribute("bulkedit");
			if(bulkedit != null&&!bulkedit.isEmpty()){
				model.addAttribute("bulkedit", bulkedit);
				request.getSession().removeAttribute("bulkedit");
			}
		}		
		/**** Bulk Edit Ends****/

		/**** Status,Internal Status and Recommendation Status Starts ****/
		Status status = domain.getStatus();
		Status internalStatus = domain.getInternalStatus();
		Status recommendationStatus = domain.getRecommendationStatus();
		Status discussionStatus=domain.getDiscussionStatus();
		if(status != null){
			model.addAttribute("status", status.getId());
			model.addAttribute("memberStatusType", status.getType());
		}
		if(internalStatus != null){
			model.addAttribute("internalStatus", internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			if(usergroupType != null && !(usergroupType.isEmpty()) && usergroupType.equals("assistant")){
				populateInternalStatus(model, internalStatus.getType(), usergroupType, locale, questionType.getType());
				if(domain.getWorkflowStarted() == null){
					domain.setWorkflowStarted("NO");
				}else if(domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				if(domain.getEndFlag() == null){
					domain.setEndFlag("continue");
				}else if(domain.getEndFlag().isEmpty()){
					domain.setEndFlag("continue");
				}
				if(domain.getLevel() == null){
					domain.setLevel("1");
				}else if(domain.getLevel().isEmpty()){
					domain.setLevel("1");
				}

			}else if(usergroupType != null && !(usergroupType.isEmpty()) && usergroupType.equals("clerk")){
				populateInternalStatus(model, internalStatus.getType(), usergroupType, locale, questionType.getType());
				if(domain.getWorkflowStarted() == null){
					domain.setWorkflowStarted("NO");
				}else if(domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
			}
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus", recommendationStatus.getId());
			model.addAttribute("recommendationStatusType", recommendationStatus.getType());
		}
		if(discussionStatus!=null) {
			model.addAttribute("discussionStatus",discussionStatus.getId());
			model.addAttribute("discussionStatusType", discussionStatus.getType());
		}
		/**** Status,Internal Status and Recommendation Status Starts ****/

		/**** Referenced StandaloneMotion Starts ****/
		CustomParameter clubbedReferencedEntitiesVisibleUserGroups = 
				CustomParameter.findByName(CustomParameter.class, "SMOIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
		if(clubbedReferencedEntitiesVisibleUserGroups != null 
				&& clubbedReferencedEntitiesVisibleUserGroups.getValue().contains(usergroupType)){
			
			model.addAttribute("level", 1);				
			List<Reference> refentities = StandaloneController.getReferencedEntityReferences(domain, locale);
			model.addAttribute("referencedQuestions",refentities);
						
			/**** Referenced StandaloneMotion Ends ****/

			/**** Clubbed StandaloneMotion Starts ****/
			List<Reference> references = new ArrayList<Reference>();
			List<ClubbedEntity> clubbedEntities = StandaloneMotion.findClubbedEntitiesByPosition(domain);
			StringBuffer buffer1 = new StringBuffer();
			buffer1.append(memberNames + ",");	
			if(clubbedEntities != null){
				for(ClubbedEntity ce : clubbedEntities){
					Reference reference = new Reference();
					reference.setId(String.valueOf(ce.getId()));
					reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getStandaloneMotion().getNumber()));
					reference.setNumber(String.valueOf(ce.getStandaloneMotion().getId()));
					references.add(reference);
					Member clubbedQuestionPrimaryMember = ce.getStandaloneMotion().getPrimaryMember();
					String tempPrimary = clubbedQuestionPrimaryMember.getFullname();
					if(!buffer1.toString().contains(tempPrimary)){						
						if(clubbedQuestionPrimaryMember.isActiveMemberOn(currentDate, locale)){
							buffer1.append(clubbedQuestionPrimaryMember.getFullname()+",");
						}
					}
					List<SupportingMember> clubbedSupportingMember = ce.getStandaloneMotion().getSupportingMembers();
					if(clubbedSupportingMember != null){
						if(!clubbedSupportingMember.isEmpty()){
							for(SupportingMember l : clubbedSupportingMember){
								Member clubbedQuestionSupportingMember = l.getMember();
								String tempSupporting = clubbedQuestionSupportingMember.getFullname();
								if(!buffer1.toString().contains(tempSupporting)){
									if(clubbedQuestionSupportingMember.isActiveMemberOn(currentDate, locale)){
										buffer1.append(clubbedQuestionSupportingMember.getFullname()+",");
									}
								}
							}
						}
					}
				}
			}
			
			if(!buffer1.toString().isEmpty()){
				buffer1.deleteCharAt(buffer1.length()-1);
			}
			String allMembersNames = buffer1.toString();
			model.addAttribute("memberNames", allMembersNames);
			if(!references.isEmpty()){
				model.addAttribute("clubbedQuestions", references);
			}else{
				if(domain.getParent()!=null){
					model.addAttribute("formattedParentNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
					model.addAttribute("parent", domain.getParent().getId());
				}
			}
		}
		/**** Clubbed StandaloneMotion Ends ****/			

		/**** Populating Put up motion and Actors Starts ****/
		if(domain.getInternalStatus() != null){
			String internalStatusType = domain.getInternalStatus().getType();			
			if(usergroupType != null && !usergroupType.isEmpty() && usergroupType.equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_ADMISSION)
							||internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_GOVT)
							||internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER)
							||internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_REJECTION)
							||internalStatusType.equals(ApplicationConstants.STANDALONE_PUTUP_REJECTION))){
				UserGroup userGroup = UserGroup.findById(UserGroup.class,Long.parseLong(strUsergroup));
				List<Reference> actors = WorkflowConfig.findStandaloneMotionActorsVO(domain, internalStatus, userGroup, 1, locale);
				model.addAttribute("internalStatusSelected", internalStatus.getId());
				model.addAttribute("actors", actors);
				if(actors != null && !actors.isEmpty()){
					String nextActor = actors.get(0).getId();
					String[] actorArr = nextActor.split("#");
					domain.setLevel(actorArr[2]);
					domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
				}
			}	
		}
		/**** Populating Put up options and Actors Ends ****/		
		
		if(questionType != null){
			if(questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				populateForHalfHourDiscussionEdit(model, domain, request);
			}
		}
	}

	private void populateInternalStatus(final ModelMap model, final String type,final String userGroupType,final String locale, final String questionType) {
		List<Status> internalStatuses = new ArrayList<Status>();
		try{
			CustomParameter specificDeviceStatusUserGroupStatuses = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			if(specificDeviceStatusUserGroupStatuses != null){
				internalStatuses = Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceUserGroupStatuses != null){
				internalStatuses = Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			}else if(specificStatuses != null){
				internalStatuses = Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)){
				CustomParameter finalStatus = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus != null){
					internalStatuses = Status.findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus != null){
						internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter != null){
							internalStatuses = Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode","standalone_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus != null){
					internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter != null){
						internalStatuses = Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode", "standalone_putup_options_final_notset");
					}		
				}
			}	
			model.addAttribute("internalStatuses", internalStatuses);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void customValidateCreate(final StandaloneMotion domain, final BindingResult result,
			final HttpServletRequest request) {		
		String role=request.getParameter("role");
		populateSupportingMembers(domain,request);
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.STANDALONEMOTION_OPTIONAL_FIELDS_IN_VALIDATION+"_"+domain.getHouseType().getType().toUpperCase(), "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}		
		/**** Validation Starts ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}
		String operation = request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					if(domain.getHouseType() == null){
						result.rejectValue("houseType", "HousetypeEmpty");
					}
					if(domain.getType() == null){
						result.rejectValue("type", "QuestionTypeEmpty");
					}
					if(domain.getSession() == null){
						result.rejectValue("session", "SessionEmpty");
					}
					if(domain.getPrimaryMember() == null){
						result.rejectValue("primaryMember", "PrimaryMemberEmpty");
					}
					if(domain.getSubject().isEmpty()){
						result.rejectValue("subject", "SubjectEmpty");
					}
					/*if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
							&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
							&& domain.getQuestionText().isEmpty()){
						result.rejectValue("questionText","QuestionTextEmpty");
					}*/
					if(domain.getSupportingMembers() == null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");						
					} else {
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
							validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
						}
						int count=0;
						for(SupportingMember i : domain.getSupportingMembers()){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
								count++;
							}
						}
						if(count == 0){
							result.rejectValue("supportingMembers", "SupportingMembersRequestAlreadySent");
						}
					}
				}else
					if(operation.equals("submit")){
						if(role.equals("SMOIS_TYPIST")){
							if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								try{
									CustomParameter csptMaxMemberHDS = CustomParameter.findByName(CustomParameter.class, "NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_LH", "");
									int lowerHouseCount = 0;
									if(csptMaxMemberHDS != null && csptMaxMemberHDS.getValue() != null && !csptMaxMemberHDS.getValue().isEmpty()){
										lowerHouseCount = Integer.parseInt(csptMaxMemberHDS.getValue());
									}
									if(StandaloneMotion.findStandaloneMotionWithNumberExcludingRejected(domain.getPrimaryMember(), domain.getType(), domain.getSession(), domain.getLocale()) >= lowerHouseCount){
										if(domain.getNumber() != null){
											result.rejectValue("number", "NumberNotEmpty", "Number should be empty as there is already maximum halfhours with number are submitted for member.");
											//check for duplicate motion
											domain.setNumber(null);
										}
									}else{
										CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, "SMOIS_AUTO_GEN_REQUIERED_FOR_TYPIST", "");
										if(csptAutoGenForTypist != null){
											if(csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
												if(domain.getNumber() == null){
													result.rejectValue("number", "NumberEmpty");
													//check for duplicate motion
												}
												Boolean flag = StandaloneMotion.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
												if(flag){
													result.rejectValue("number", "NonUnique","Duplicate Parameter");
												}
											}
										}else{
											throw new ELSException("typist_auto_gen","custom parameters for typist auto generator of number is not set.");
										}
									}
								}catch(Exception e){
									logger.error("error", e);
								}
							}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
								try{
									CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, "SMOIS_AUTO_GEN_REQUIERED_FOR_TYPIST", "");
									if(csptAutoGenForTypist != null){
										if(csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
											if(domain.getNumber() == null){
												result.rejectValue("number", "NumberEmpty");
												//check for duplicate motion
											}
											Boolean flag = StandaloneMotion.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
											if(flag){
												result.rejectValue("number", "NonUnique","Duplicate Parameter");
											}
										}
									}else{
										throw new ELSException("typist_auto_gen","custom parameters for typist auto generator of number is not set.");
									}
								}catch(Exception e){
									logger.error("error", e);
								}
							}
						}
						if(domain.getHouseType()==null){
							result.rejectValue("houseType","HousetypeEmpty");
						}
						if(domain.getType()==null){
							result.rejectValue("type","QuestionTypeEmpty");
						}
						if(domain.getSession()==null){
							result.rejectValue("session","SessionEmpty");
						}
						if(domain.getPrimaryMember()==null){
							result.rejectValue("primaryMember","PrimaryMemberEmpty");
						}
						if(domain.getSubject().isEmpty()){
							result.rejectValue("subject","SubjectEmpty");
						}
						
						//submission date limit validation
						CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
						if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
							String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
							if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
								String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
								for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
									if(dt.trim().equals(domain.getType().getType().trim())) {
										if(!StandaloneMotion.isAllowedForSubmission(domain, new Date())){
											result.rejectValue("version","SubmissionNotAllowed", "Submission not allowed before " + domain.getSession().getParameter(domain.getType().getType()+"_submissionStartDate"));
										}
									}
								}
							}
						}
						
						/*if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
								&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
								&& domain.getQuestionText().isEmpty()){
							result.rejectValue("questionText","QuestionTextEmpty");
						}*/
						if(optionalFields != null && !optionalFields.contains("ministry")){
							if(domain.getMinistry() == null){
								result.rejectValue("ministry", "MinistryEmpty");
							}
						}
						if(optionalFields != null && !optionalFields.contains("subDepartment")){
							if(domain.getSubDepartment() == null){
								result.rejectValue("subDepartment", "SubDepartmentEmpty");
							}
						}						
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
							validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
						}
					}
				}
			}else{
				if(role.equals("SMOIS_TYPIST")){
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						try{
							CustomParameter csptMaxMemberHDS = CustomParameter.findByName(CustomParameter.class, "NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_LH", "");
							int lowerHouseCount = 0;
							if(csptMaxMemberHDS != null && csptMaxMemberHDS.getValue() != null && !csptMaxMemberHDS.getValue().isEmpty()){
								lowerHouseCount = Integer.parseInt(csptMaxMemberHDS.getValue());
							}
							if(StandaloneMotion.findStandaloneMotionWithNumberExcludingRejected(domain.getPrimaryMember(), domain.getType(), domain.getSession(), domain.getLocale()) >= lowerHouseCount){
								if(domain.getNumber() != null){
									result.rejectValue("number", "NumberNotEmpty", "Number should be empty as there is already maximum halfhours with number are submitted for member.");
									//check for duplicate motion
									domain.setNumber(null);
								}
							}else{
								
								CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, "SMOIS_AUTO_GEN_REQUIERED_FOR_TYPIST", "");
								if(csptAutoGenForTypist != null){
									if(csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
										if(domain.getNumber() == null){
											result.rejectValue("number", "NumberEmpty");
											//check for duplicate motion
										}
										Boolean flag = StandaloneMotion.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
										if(flag){
											result.rejectValue("number", "NonUnique","Duplicate Parameter");
										}
									}
								}else{
									throw new ELSException("typist_auto_gen","custom parameters for typist auto generator of number is not set.");
								}							
							}
						}catch(Exception e){
							logger.error("error", e);
						}
					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						try{
							CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, "SMOIS_AUTO_GEN_REQUIERED_FOR_TYPIST", "");
							if(csptAutoGenForTypist != null){
								if(csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
									if(domain.getNumber() == null){
										result.rejectValue("number", "NumberEmpty");
										//check for duplicate motion
									}
									Boolean flag = StandaloneMotion.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
									if(flag){
										result.rejectValue("number", "NonUnique","Duplicate Parameter");
									}
								}
							}else{
								throw new ELSException("typist_auto_gen","custom parameters for typist auto generator of number is not set.");
							}
						}catch(Exception e){
							logger.error("error", e);
						}
					}
				}
				if(domain.getHouseType() == null){
					result.rejectValue("houseType", "HousetypeEmpty");
				}
				if(domain.getType() == null){
					result.rejectValue("type", "QuestionTypeEmpty");
				}
				if(domain.getSession() == null){
					result.rejectValue("session", "SessionEmpty");
				}
				if(domain.getPrimaryMember() == null){
					result.rejectValue("primaryMember", "PrimaryMemberEmpty");
				}
				if(domain.getSubject().isEmpty()){
					result.rejectValue("subject", "SubjectEmpty");
				}
//				if(domain.getSubDepartment() == null){
//					result.rejectValue("subDepartment", "SubDepartmentEmpty");
//				}
				/*if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
					validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
				}*/
				/*if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
						&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
						&& domain.getQuestionText().isEmpty()){
					result.rejectValue("questionText", "QuestionTextEmpty");
				}*/
			}
		/**** Validation Ends ****/
	}

	@Override
	protected void customValidateUpdate(final StandaloneMotion domain, final BindingResult result,
			final HttpServletRequest request) {		
		String role = request.getParameter("role");
		String userGroupType = request.getParameter("usergroupType");
		populateSupportingMembers(domain, request);
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.STANDALONEMOTION_OPTIONAL_FIELDS_IN_VALIDATION+"_"+domain.getHouseType().getType().toUpperCase(), "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}
		/**** Validation Starts ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		String operation = request.getParameter("operation");
		if(operation != null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					if(domain.getHouseType() == null){
						result.rejectValue("houseType", "HousetypeEmpty");
					}
					if(domain.getType() == null){
						result.rejectValue("type", "QuestionTypeEmpty");
					}
					if(domain.getSession() == null){
						result.rejectValue("session", "SessionEmpty");
					}
					if(domain.getPrimaryMember() == null){
						result.rejectValue("primaryMember", "PrimaryMemberEmpty");
					}
					if(domain.getSubject().isEmpty()){
						result.rejectValue("subject", "SubjectEmpty");
					}
					/*if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
							&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
							&& domain.getQuestionText().isEmpty()){

						result.rejectValue("questionText"," QuestionTextEmpty");
					}*/
					if(domain.getSupportingMembers() == null){
						result.rejectValue("supportingMembers", "SupportingMembersEmpty");
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers", "SupportingMembersEmpty");						
					} else {
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
							validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
						}
						int count = 0;
						for(SupportingMember i : domain.getSupportingMembers()){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
								count++;
							}
						}
						if(count == 0){
							result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
						}
					}
				}else if(operation.equals("submit") ||operation.equals("startworkflow")){
					if(role.equals("SMOIS_TYPIST") ){
						
						if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							try{
								CustomParameter csptMaxMemberHDS = CustomParameter.findByName(CustomParameter.class, "NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_LH", "");
								int lowerHouseCount = 0;
								if(csptMaxMemberHDS != null && csptMaxMemberHDS.getValue() != null && !csptMaxMemberHDS.getValue().isEmpty()){
									lowerHouseCount = Integer.parseInt(csptMaxMemberHDS.getValue());
								}
								if(StandaloneMotion.findStandaloneMotionWithNumberExcludingRejected(domain.getPrimaryMember(), domain.getType(), domain.getSession(), domain.getLocale()) == lowerHouseCount){
									
									StandaloneMotion sm = null;
									boolean sameDevice = false;
									try{
										sm = StandaloneMotion.find(domain.getSession(), domain.getNumber());	
										
										if(sm.getId().intValue() != domain.getId().intValue()){
											sameDevice = true;
										}
									}catch(Exception e){
										logger.error("error", e);
									}
									
									if(!sameDevice){
										if(domain.getNumber() != null){
											result.rejectValue("number", "NumberNotEmpty", "Number should be empty as there is already maximum halfhours with number are submitted for member.");
											//check for duplicate motion
											domain.setNumber(null);
										}
									}
								}else{
									CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, "SMOIS_AUTO_GEN_REQUIERED_FOR_TYPIST", "");
									if(csptAutoGenForTypist != null && csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
										if(domain.getNumber() == null){
											result.rejectValue("number", "NumberEmpty");
											//check for duplicate motion
										}
										Boolean flag = StandaloneMotion.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
										if(flag){
											result.rejectValue("number", "NonUnique","Duplicate Parameter");
										}
									}else{
										throw new ELSException("typist_auto_gen","custom parameters for typist auto generator of number is not set.");
									}
								}
							}catch(Exception e){
								logger.error("error", e);
							}
						}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							try{
								CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, "SMOIS_AUTO_GEN_REQUIERED_FOR_TYPIST", "");
								if(csptAutoGenForTypist != null && csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
									if(domain.getNumber() == null){
										result.rejectValue("number", "NumberEmpty");
										//check for duplicate motion
									}
									Boolean flag = StandaloneMotion.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
									if(flag){
										result.rejectValue("number", "NonUnique","Duplicate Parameter");
									}
								}else{
									throw new ELSException("typist_auto_gen","custom parameters for typist auto generator of number is not set.");
								}
							}catch(Exception e){
								
							}
							
							StandaloneMotion m = StandaloneMotion.findExisting(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
							
							if(m != null && !m.getId().equals(domain.getId())){
								result.rejectValue("number", "NonUnique","Duplicate Parameter");
							}
							
						}
					}
					if(domain.getHouseType() == null){
						result.rejectValue("houseType", "HousetypeEmpty");
					}
					if(domain.getType() == null){
						result.rejectValue("type", "QuestionTypeEmpty");
					}
					if(domain.getSession() == null){
						result.rejectValue("session", "SessionEmpty");
					}
					if(domain.getPrimaryMember() == null){
						result.rejectValue("primaryMember", "PrimaryMemberEmpty");
					}
					if(domain.getSubject().isEmpty()){
						result.rejectValue("subject", "SubjectEmpty");
					}
					/*if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
							&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
							&& domain.getQuestionText().isEmpty()){
						result.rejectValue("questionText", "QuestionTextEmpty");
					}*/
					if(optionalFields != null && !optionalFields.contains("ministry")){
						if(domain.getMinistry() == null){
							result.rejectValue("ministry", "MinistryEmpty");
						}
					}
					if(optionalFields != null && !optionalFields.contains("subDepartment")){
						if(domain.getSubDepartment() == null){
							result.rejectValue("subDepartment", "SubDepartmentEmpty");
						}
					}
					
					if(operation.equals("submit")) {
						//submission date limit validation
						CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
						if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
							String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
							if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
								String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
								for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
									if(dt.trim().equals(domain.getType().getType().trim())) {
										if(!StandaloneMotion.isAllowedForSubmission(domain, new Date())){
											result.rejectValue("version","SubmissionNotAllowed", "Submission not allowed before " + domain.getSession().getParameter(domain.getType().getType()+"_submissionStartDate"));
										}
									}
								}
							}
						}
					}
					if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
							&& !role.equals("SMOIS_ASSISTANT")){
						validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
					}
				}
			}
		}else{
			if(role.equals("SMOIS_TYPIST") || role.equals("SMOIS_ASSISTANT")){
				if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					try{
						CustomParameter csptMaxMemberHDS = CustomParameter.findByName(CustomParameter.class, "NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_LH", "");
						int lowerHouseCount = 0;
						if(csptMaxMemberHDS != null && csptMaxMemberHDS.getValue() != null && !csptMaxMemberHDS.getValue().isEmpty()){
							lowerHouseCount = Integer.parseInt(csptMaxMemberHDS.getValue());
						}
						if(StandaloneMotion.findStandaloneMotionWithNumberExcludingRejected(domain.getPrimaryMember(), domain.getType(), domain.getSession(), domain.getLocale()) >= lowerHouseCount){
							StandaloneMotion sm = null;
							boolean sameDevice = false;
							try{
								sm = StandaloneMotion.find(domain.getSession(), domain.getNumber());	
								
								if(sm.getId().intValue() == domain.getId().intValue()){
									sameDevice = true;
								}
							}catch(Exception e){
								logger.error("error", e);
							}
							
							if(!sameDevice){
								if(domain.getNumber() != null){
									result.rejectValue("number", "NumberNotEmpty", "Number should be empty as there is already maximum halfhours with number are submitted for member.");
									//check for duplicate motion
									domain.setNumber(null);
								}
							}
						}else{
							if(domain.getNumber() == null){
								result.rejectValue("number", "NumberEmpty");
								//check for duplicate motion
							}
							StandaloneMotion m = StandaloneMotion.findExisting(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
							if(m != null && !m.getId().equals(domain.getId())){
								result.rejectValue("number", "NonUnique","Duplicate Parameter");
							}
						}
					}catch(Exception e){
						logger.error("error", e);
					}
				}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
					
					try{
						CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, "SMOIS_AUTO_GEN_REQUIERED_FOR_TYPIST", "");
						if(csptAutoGenForTypist != null && csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
							if(domain.getNumber() == null){
								result.rejectValue("number", "NumberEmpty");
								//check for duplicate motion
							}
							Boolean flag = StandaloneMotion.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
							if(flag){
								result.rejectValue("number", "NonUnique","Duplicate Parameter");
							}
						}else{
							throw new ELSException("typist_auto_gen","custom parameters for typist auto generator of number is not set.");
						}
					}catch(Exception e){
						
					}
				}
			}
			if(domain.getHouseType()==null){
				result.rejectValue("houseType","HousetypeEmpty");
			}
			if(optionalFields != null && !optionalFields.contains("ministry")){
				if(domain.getMinistry() == null){
					result.rejectValue("ministry", "MinistryEmpty");
				}
			}
			if(optionalFields != null && !optionalFields.contains("subDepartment")){
				if(domain.getSubDepartment() == null){
					result.rejectValue("subDepartment", "SubDepartmentEmpty");
				}
			}
			if(domain.getType()==null){
				result.rejectValue("type","QuestionTypeEmpty");
			}
			if(domain.getSession()==null){
				result.rejectValue("session","SessionEmpty");
			}
			if(domain.getPrimaryMember()==null){
				result.rejectValue("primaryMember","PrimaryMemberEmpty");
			}
			if(domain.getSubject().isEmpty()){
				result.rejectValue("subject","SubjectEmpty");
			}
			/*if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
					&& domain.getQuestionText().isEmpty()){
				result.rejectValue("questionText","QuestionTextEmpty");
			}*/
		}
		/**** Validation Ends ****/
	}

	private void populateSupportingMembers(final StandaloneMotion domain,final HttpServletRequest request){
		/**** Supporting Members Starts 
		 * a.selectedSupportingMembers=request parameter containing supporting members ids
		 * b.selectedSupportingMembersIfErrors=request parameter containing supporting members ids
		 * c.selectedSupportingMembers=variable containing supporting members 
		 * d.members=existing supporting members
		 * e.Existing supporting members are added as it is 
		 * f.New supporting members are added with status not send if role is not that of typist
		 * g.New supporting members are added with status approved,approval type auto if role is typist****/
		String role=request.getParameter("role");
		String[] selectedSupportingMembers = request.getParameterValues("selectedSupportingMembers");
		try{
			if(selectedSupportingMembers == null){
				String supportingMembersIfErrors = request.getParameter("selectedSupportingMembersIfErrors");
				if(supportingMembersIfErrors != null){
					if(supportingMembersIfErrors.trim().length() > 0){
						selectedSupportingMembers = request.getParameter("selectedSupportingMembersIfErrors").split(",");
					}
				}
			}
		}catch(NullPointerException npe){
			logger.error("Request Parameter missing: selectedSupportingMembersIfErrors");
		}
		List<SupportingMember> members=new ArrayList<SupportingMember>();
		if(domain.getId()!=null){
			StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class,domain.getId());
			members = motion.getSupportingMembers();
		}		
		Status notsendStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_NOTSEND, domain.getLocale());
		Status approvedStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, domain.getLocale());
		List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
		if(selectedSupportingMembers!=null){
			if(selectedSupportingMembers.length>0){				
				for(String i:selectedSupportingMembers){
					SupportingMember supportingMember=null;
					Member member=Member.findById(Member.class, Long.parseLong(i));
					for(SupportingMember j:members){
						if(j.getMember().getId()==member.getId()){
							supportingMember=j;
							break;
						}
					}
					if(supportingMember==null){
						supportingMember=new SupportingMember();
						supportingMember.setMember(member);
						supportingMember.setLocale(domain.getLocale());
						if(role!=null){
							CustomParameter supportingMemberAutoApprovalAllowedTo=CustomParameter.findByName(CustomParameter.class,"SMOIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO","");
							if(supportingMemberAutoApprovalAllowedTo!=null){
								if(role!=null&&!role.isEmpty()&&supportingMemberAutoApprovalAllowedTo.getValue().contains(role)){
									supportingMember.setDecisionStatus(approvedStatus);
									supportingMember.setApprovedSubject(domain.getSubject());
									//supportingMember.setApprovedText(domain.getQuestionText());
									supportingMember.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
								}else{
									supportingMember.setDecisionStatus(notsendStatus);
								}
							}

						}

					}
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
		/**** Supporting Members Ends ****/
	}	

	private void validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(final StandaloneMotion domain, final BindingResult result, final HttpServletRequest request) {
		/**** No. of Supporting members validation Starts ****/
		if(domain.getType()!=null) {
			if(domain.getType().getType()!=null) {
				if(domain.getType().getType().equalsIgnoreCase(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
					Session session = domain.getSession();								
					if(session != null) {
						String noOFSupportingMembersToCheck = session.getParameter(ApplicationConstants.HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS);
						String noOFSupportingMembersComparator = session.getParameter(ApplicationConstants.HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS_COMPARATOR);
						if(	(noOFSupportingMembersToCheck!=null) && (noOFSupportingMembersComparator!=null) ){										
							if(	(!noOFSupportingMembersToCheck.isEmpty()) && (!noOFSupportingMembersComparator.isEmpty()) ){
								int numberOFSupportingMembersToCheck = Integer.parseInt(noOFSupportingMembersToCheck);
								int numberOFSupportingMembersReceived = 0;
								if(domain.getSupportingMembers()!=null) {
									numberOFSupportingMembersReceived = domain.getSupportingMembers().size();
								}
								if(noOFSupportingMembersComparator.equalsIgnoreCase("eq")) {
									if(!(numberOFSupportingMembersReceived == numberOFSupportingMembersToCheck)) {
										result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
									}
								}else 
									if(noOFSupportingMembersComparator.equalsIgnoreCase("le")) {
										if(!(numberOFSupportingMembersReceived <= numberOFSupportingMembersToCheck)) {
											result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
										}
									}else 
										if(noOFSupportingMembersComparator.equalsIgnoreCase("lt")) {
											if(!(numberOFSupportingMembersReceived < numberOFSupportingMembersToCheck)) {
												result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
											}
										}else 
											if(noOFSupportingMembersComparator.equalsIgnoreCase("ge")) {
												if(!(numberOFSupportingMembersReceived >= numberOFSupportingMembersToCheck)) {
													result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
												}
											}else 
												if(noOFSupportingMembersComparator.equalsIgnoreCase("gt")) {
													if(!(numberOFSupportingMembersReceived > numberOFSupportingMembersToCheck)) {
														result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
													}
												}

								String operation=request.getParameter("operation");

								if (operation != null) {
									if (!operation.isEmpty()) {
										if (operation.equals("submit")) {
											if (result.getFieldErrorCount("supportingMembers") == 0) {
												// check if request is already
												// sent for approval
												int count = 0;
												if (domain.getSupportingMembers() != null) {
													if (domain.getSupportingMembers().size() > 0) {
														for (SupportingMember i : domain.getSupportingMembers()) {
															if (!i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
																count++;
															}
														}
														if (count != 0) {
															result.rejectValue("supportingMembers","supportingMembersRequestNotSent");
														}
													}
												}
											}
										}
									}
								}
							}										
						}
					}
				}							
			}
		}
		/**** No. of Supporting members validation Ends ****/
	}

	@Override
	protected void populateCreateIfErrors(final ModelMap model, final StandaloneMotion domain,
			final HttpServletRequest request) {	
		String selectedSupportingMembersIfErrors = "";
		if(domain.getSupportingMembers() != null){
			for(SupportingMember supportingMember : domain.getSupportingMembers() ){
				if(selectedSupportingMembersIfErrors.trim().length() > 0){
					selectedSupportingMembersIfErrors += "," + supportingMember.getMember().getId().toString();
				}else{
					selectedSupportingMembersIfErrors += supportingMember.getMember().getId().toString();
				}
			}
		}
		model.addAttribute("selectedSupportingMembersIfErrors", selectedSupportingMembersIfErrors);
		if(domain.getPrimaryMember()!=null){
			model.addAttribute("formattedPrimaryMember", domain.getPrimaryMember().getFullname());
			model.addAttribute("primaryMember", domain.getPrimaryMember().getId());
		}
		if(domain.getMinistry()!=null){
			model.addAttribute("formattedMinistry", domain.getMinistry().getName());
			model.addAttribute("ministrySelected", domain.getMinistry().getId());
		}
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}

	@Override
	protected void populateUpdateIfErrors(final ModelMap model, final StandaloneMotion domain,
			final HttpServletRequest request) {
		/**** updating submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate=request.getParameter("taskReceivedOnDate");
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate!=null&&!strSubmissionDate.isEmpty()){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate!=null&&!strCreationDate.isEmpty()){
					domain.setCreationDate(format.parse(strCreationDate));
				}
				if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
					domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
				}
				if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
					domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}	
		Status discussionStatus=domain.getDiscussionStatus();	
		if(discussionStatus!=null) {
			model.addAttribute("discussionStatus",discussionStatus.getId());
			model.addAttribute("discussionStatusType", discussionStatus.getType());
		}
		super.populateUpdateIfErrors(model, domain, request);
	}


	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final StandaloneMotion domain,
			final HttpServletRequest request) {
		
		/**** Status,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation = request.getParameter("operation");
		boolean canGoAhead = false;
		String strUserGroupType = request.getParameter("usergroupType");
		UserGroupType userGroupType = null;
		if(strUserGroupType != null){
			userGroupType = UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		/**** Status,Internal Status,Recommendation Status Update Starts ****/
		/**** canGoAhead =true when all mandatory parameters for a question type has been filled ****/
		if(domain.getType() != null){
			/**** for half hour discussion ****/
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				if(domain.getHouseType() != null){
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						if(domain.getSession()!=null &&  domain.getType()!=null 
								&& domain.getPrimaryMember()!=null && domain.getMinistry()!=null && (!domain.getSubject().isEmpty()) 
								/*&& (!domain.getQuestionText().isEmpty())*/){
							domain.setGroup(null);
							canGoAhead = true;					
						}else{
							canGoAhead = false;
						}
					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						if(domain.getSession()!=null &&  domain.getType()!=null 
								&& domain.getPrimaryMember()!=null && domain.getMinistry()!=null && domain.getGroup()!=null 
								&& (!domain.getSubject().isEmpty())){
							domain.setQuestionText("");
							canGoAhead = true;					
						}else{
							canGoAhead = false;
						}
					}
				}
			}/**** for other question types ****/			
		}
		/**** canGoAhead=true parameter is used to check if all mandatory fields have been set(condition
		 * for complete or submit status)****/
		if(canGoAhead){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						/**** a.canGoAhead=true all mandatory fields have been field
						 * b.operation=submit ****/
						/****  submission date is set ****/
						if(domain.getSubmissionDate()==null){
							domain.setSubmissionDate(new Date());
						}
						/**** Supporting Members is updates(in case of submit) Starts ****/
						List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
						if(domain.getSupportingMembers()!=null){
							if(!domain.getSupportingMembers().isEmpty()){
								for(SupportingMember i:domain.getSupportingMembers()){
									if(i.getDecisionStatus().getType().trim().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
										supportingMembers.add(i);
									}
								}
								domain.setSupportingMembers(supportingMembers);
							}
						}
						/**** Supporting Members is updates(in case of submit) Starts ****/

						/**** Status,Internal Status and recommendation Status is set ****/
						Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}else{
						/**** case of complete status ****/
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}else{
					/**** case of complete status ****/
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}else{
				/**** case of complete status ****/
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_COMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}
		/**** Drafts case or incomplete status case.canGo Ahead=false all mandatory fields
		 * have not been filled ****/
		else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** Status,Internal Status,Recommendation Status Update Ends ****/

		/**** creation date,created by,edited on,edited by Starts ****/
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		/**** creation date,created by,edited on,edited by Ends ****/

	}	

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final StandaloneMotion domain,
			final HttpServletRequest request) {
		
		/**** Checking if its submission request or normal update ****/
		String operation=request.getParameter("operation");
		UserGroupType userGroupType=null;
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		boolean canGoAhead = false;
		/**** Question status will be complete if all mandatory fields have been filled ****/
		if(domain.getType() != null){
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				if (domain.getHouseType() != null) {
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						if (domain.getHouseType() != null
								&& domain.getType() != null
								&& domain.getSession() != null
								&& domain.getPrimaryMember() != null
								//&& domain.getMinistry() != null
								&& (!domain.getSubject().isEmpty())
								/*&& (!domain.getQuestionText().isEmpty())*/) {
							canGoAhead = true;
						} else {
							canGoAhead = false;
						}
					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						if (domain.getHouseType() != null
								&& domain.getType() != null
								&& domain.getSession() != null
								&& domain.getPrimaryMember() != null
								//&& domain.getMinistry() != null
								//&& domain.getGroup() != null
								&& (!domain.getSubject().isEmpty())) {
							domain.setQuestionText("");
							canGoAhead = true;
						} else {
							canGoAhead = false;
						}
					}
				}
			}
		}
		
		if(canGoAhead){			
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						if(domain.getSubmissionDate()==null){
							domain.setSubmissionDate(new Date());
						}
						List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
						if(domain.getSupportingMembers()!=null){
							if(!domain.getSupportingMembers().isEmpty()){
								for(SupportingMember i:domain.getSupportingMembers()){
									if(userGroupType.getType().equals("typist")){
										supportingMembers.add(i);
									}else{
										if(i.getDecisionStatus().getType().trim().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
											supportingMembers.add(i);
										}
									}
								}
								domain.setSupportingMembers(supportingMembers);
							}
						}
						Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}else{
						// Set status, internalStatus, recommendationstatus
						if(!userGroupType.getType().equals(ApplicationConstants.ASSISTANT)
							&& !userGroupType.getType().equals(ApplicationConstants.CLERK)){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_COMPLETE, domain.getLocale());
							if(!domain.getStatus().getType().equals(ApplicationConstants.STANDALONE_SUBMIT) && !operation.trim().equals("startworkflow")){
								domain.setStatus(status);
								domain.setInternalStatus(status);
								domain.setRecommendationStatus(status);
							}
						}
					}
				}else{
					if(!userGroupType.getType().equals(ApplicationConstants.ASSISTANT)
							&& !userGroupType.getType().equals(ApplicationConstants.CLERK)){
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_COMPLETE, domain.getLocale());
						if(!domain.getStatus().getType().equals(ApplicationConstants.STANDALONE_SUBMIT)){
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}
					}
				}
			}else{
				if(!userGroupType.getType().equals(ApplicationConstants.ASSISTANT)
						&& !userGroupType.getType().equals(ApplicationConstants.CLERK)){
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_COMPLETE, domain.getLocale());
	
					/*****Uncomment the following Code when processing of question for council will be 
					 * done online And delete the code following the below code******/
					if(!domain.getStatus().getType().equals(ApplicationConstants.STANDALONE_SUBMIT)){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}
				/*if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					if(!domain.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)&& !domain.getStatus().getType().contains("question_final")){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
					if(!domain.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT) && !domain.getStatus().getType().contains("question_final")){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}*/

			}
		}
		else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** Edited On,Edited By Starts ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());


		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		if(strUserGroupType!=null){
			CustomParameter assistantProcessedAllowed=CustomParameter.findByName(CustomParameter.class,"SMOIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
			if(assistantProcessedAllowed!=null&&assistantProcessedAllowed.getValue().contains(strUserGroupType)){
				Long id = domain.getId();
				StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class, id);
				String internalStatus = question.getInternalStatus().getType();
				if(domain.getType() != null){
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE) 
							&& internalStatus.equals(ApplicationConstants.STANDALONE_SUBMIT)){
						String optionalFields = null;
						CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.STANDALONEMOTION_OPTIONAL_FIELDS_IN_VALIDATION+"_"+domain.getHouseType().getType().toUpperCase(), "");		
						if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
							optionalFields = csptOptionalFields.getValue();
						}
						if(optionalFields!=null && !optionalFields.contains("ministry") && domain.getMinistry()!=null) {
							Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
							domain.setInternalStatus(ASSISTANT_PROCESSED);
							domain.setRecommendationStatus(ASSISTANT_PROCESSED);
						} else {
							Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
							domain.setInternalStatus(ASSISTANT_PROCESSED);
							domain.setRecommendationStatus(ASSISTANT_PROCESSED);
						}
					} else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						
						Group group = domain.getGroup();
						if((internalStatus.equals(ApplicationConstants.STANDALONE_SUBMIT)||internalStatus.equals(ApplicationConstants.STANDALONE_SYSTEM_GROUPCHANGED)) && domain.getMinistry()!=null && group!=null && domain.getSubDepartment()!=null) {
							Status SYSTEM_PUT_UP = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP, domain.getLocale());
							domain.setInternalStatus(SYSTEM_PUT_UP);
							domain.setRecommendationStatus(SYSTEM_PUT_UP);
						}

						/*StandaloneMotionDraft draft = null;
						try{
							draft = domain.findPreviousDraft();				        
						}catch(Exception e){
							logger.error("error",e);
						}
						if(group != null && draft != null) {
							Group prevGroup = draft.getGroup();
							if(prevGroup != null && ! prevGroup.getNumber().equals(group.getNumber())) {
								Status GROUP_CHANGED = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_GROUPCHANGED, domain.getLocale());
								domain.setInternalStatus(GROUP_CHANGED);
								domain.setRecommendationStatus(GROUP_CHANGED);
							}
						}*/
					}
				}				
			}
		}		
		/**** updating submission date and creation date ****/
		String strCreationDate = request.getParameter("setCreationDate");
		String strSubmissionDate = request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat != null){
			SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate != null){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate != null){
					domain.setCreationDate(format.parse(strCreationDate));
				}
				if(strWorkflowStartedOnDate != null&&!strWorkflowStartedOnDate.isEmpty()){
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
	protected void populateAfterCreate(final ModelMap model, final StandaloneMotion domain,
			final HttpServletRequest request) {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Parameters which will be read from request in populate new Ends ****/

		/**** Supporting Member Workflow Starts ****/
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",domain.getType().getType());
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					List<Task> tasks=processService.getCurrentTasks(processInstance);
					List<WorkflowDetails> workflowDetails;
					try {
						workflowDetails = WorkflowDetails.create(domain, tasks, ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW, "0");
						StandaloneMotion motion=StandaloneMotion.findById(StandaloneMotion.class,domain.getId());
						List<SupportingMember> supportingMembers=motion.getSupportingMembers();
						Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
						StringBuffer supportingMembersUserNames = new StringBuffer("");
						for(SupportingMember i:supportingMembers){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
								i.setDecisionStatus(status);
								i.setRequestReceivedOn(new Date());
								i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
								User user=User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
								Credential credential=user.getCredential();							
								for(WorkflowDetails j:workflowDetails){
									if(j.getAssignee().equals(credential.getUsername())){
										i.setWorkflowDetailsId(String.valueOf(j.getId()));
										break;
									}
								}							
								i.merge();
								/** save supporting member usernames for sending notification **/
								supportingMembersUserNames.append(credential.getUsername());
								supportingMembersUserNames.append(",");
							}
						}
						//SEND NOTIFICATION FOR NEW SUPPORTING MEMBER APPROVAL REQUESTS
						if(!supportingMembersUserNames.toString().isEmpty()) {
							NotificationController.sendSupportingMemberApprovalNotification(domain.getSubject(), domain.getType(), domain.getPrimaryMember(), supportingMembersUserNames.toString(), domain.getLocale());
						}
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
						e.printStackTrace();
					}
				}
			}
		}
		/**** Supporting Member Workflow Ends ****/
	}

	@Override
	protected void populateAfterUpdate(final ModelMap model, final StandaloneMotion domain,
			final HttpServletRequest request) {
		try{
			/**** Parameters which are read from request in populate edit needs to be saved in session Starts ****/
			request.getSession().setAttribute("role",request.getParameter("role"));
			request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
			request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
			request.getSession().setAttribute("bulkedit",request.getParameter("bulkedit"));
			/**** Parameters which are read from request in populate edit needs to be saved in session Starts ****/
	
			StandaloneMotion tempMotion = StandaloneMotion.findById(StandaloneMotion.class, domain.getId());
			
			if(domain.getSession().getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				// On Group Change
				Group fromGroup = StandaloneMotion.isGroupChanged(tempMotion);
				if(fromGroup != null) {
					StandaloneMotion.onGroupChange(tempMotion, fromGroup);
				}
			}
			
			if(domain.getSession().getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				// Add to Chart
				Chart.addToChart(tempMotion);
				Chart chart = Chart.find(domain);
				if(chart == null){
					domain.setNumber(null);
				}
			}
			
			
			/**** Supporting Member Workflow/Put Up Workflow ****/
			String operation=request.getParameter("operation");
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.equals("approval")){
						ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
						Map<String,String> properties=new HashMap<String, String>();
						properties.put("pv_deviceId",String.valueOf(domain.getId()));
						properties.put("pv_deviceTypeId",domain.getType().getType());
						ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
						List<Task> tasks=processService.getCurrentTasks(processInstance);					
						List<WorkflowDetails> workflowDetails;
						try {
							workflowDetails = WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"");
							StandaloneMotion motion=StandaloneMotion.findById(StandaloneMotion.class,domain.getId());
							List<SupportingMember> supportingMembers=motion.getSupportingMembers();
							Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
							StringBuffer supportingMembersUserNames = new StringBuffer("");
							for(SupportingMember i:supportingMembers){
								if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
									i.setDecisionStatus(status);
									i.setRequestReceivedOn(new Date());
									i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
									User user=User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
									Credential credential=user.getCredential();
									for(WorkflowDetails j:workflowDetails){
										if(j.getAssignee().equals(credential.getUsername())){
											i.setWorkflowDetailsId(String.valueOf(j.getId()));
											break;
										}
									}
									i.merge();
									/** save supporting member usernames for sending notification **/
									supportingMembersUserNames.append(credential.getUsername());
									supportingMembersUserNames.append(",");
								}
							}
							//SEND NOTIFICATION FOR NEW SUPPORTING MEMBER APPROVAL REQUESTS
							if(!supportingMembersUserNames.toString().isEmpty()) {
								NotificationController.sendSupportingMemberApprovalNotification(domain.getSubject(), domain.getType(), domain.getPrimaryMember(), supportingMembersUserNames.toString(), domain.getLocale());
							}
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
							e.printStackTrace();
						}
					}else if(operation.equals("startworkflow")){
						if(domain.getType() != null){
							
							StandaloneMotion motion = null;
							
							if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
									&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
								Map<String,String> properties=new HashMap<String, String>();					
								String nextuser=request.getParameter("actor");
								String level="";
								UserGroupType usergroupType = null;
								
								if(nextuser!=null){
									if(!nextuser.isEmpty()){
										String[] temp=nextuser.split("#");
										properties.put("pv_user", temp[0]);									
										level=temp[2];
										usergroupType = UserGroupType.findByType(temp[1], domain.getLocale());
									}
								}
								properties.put("pv_deviceId",String.valueOf(domain.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
								String endflag=domain.getEndFlag();
								properties.put("pv_endflag",endflag);	
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
												String remindersubject=domain.getSubject();
												properties.put("pv_remindersubject", remindersubject);
												String remindercontent=domain.getReason();
												properties.put("pv_remindercontent", remindercontent);
											}
										}
									}
								}
								ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
								motion=StandaloneMotion.findById(StandaloneMotion.class,domain.getId());
								Task task=processService.getCurrentTask(processInstance);
								if(endflag!=null){
									if(!endflag.isEmpty()){
										if(endflag.equals("continue")){
											/*WorkflowDetails workflowDetails;
											try {
												workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,level);
												motion.setWorkflowDetailsId(workflowDetails.getId());
											} catch (ELSException e) {
												model.addAttribute("error", e.getParameter());
												e.printStackTrace();
											}*/
											
											
											try{
												//*****
												
												Workflow workflow = null;
												
												/*
												 * START...
												 */
												/*if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION)
													|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
														workflow = Workflow.findByStatus(domain.getRecommendationStatus(), domain.getLocale());
													} else {
														workflow = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
												}*/
												Status internalStatus = motion.getInternalStatus();
												String internalStatusType = internalStatus.getType();
												Status recommendationStatus = motion.getRecommendationStatus();
												String recommendationStatusType = recommendationStatus.getType();
		
												if(recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
														|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
														|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)
														|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
													workflow = Workflow.findByStatus(recommendationStatus, domain.getLocale());
												} 
												else if(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)
														|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)
														|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
														||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))
														||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
														||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))) {
													workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
												} 
												else {
													workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
												}
												/*
												 * Added by Amit Desai 2 Dec 2014
												 * ... END
												 */
												
												WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
												motion.setWorkflowDetailsId(workflowDetails.getId());
												//*****
											}catch(ELSException ex){
												logger.error("error", ex);
												model.addAttribute("error", ex.getParameter());
											}
	
										}
									}
								}
							}else{
								ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
								Map<String,String> properties=new HashMap<String, String>();					
								String nextuser=request.getParameter("actor");
								String level="";
								UserGroupType usergroupType = null;
								
								if(nextuser!=null){
									if(!nextuser.isEmpty()){
										String[] temp=nextuser.split("#");
										properties.put("pv_user",temp[0]);
										level=temp[2];
										usergroupType = UserGroupType.findByType(temp[1], domain.getLocale());
									}
								}
								String endflag=domain.getEndFlag();
								properties.put("pv_endflag",endflag);	
								properties.put("pv_deviceId",String.valueOf(domain.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
								ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
								motion=StandaloneMotion.findById(StandaloneMotion.class,domain.getId());
								Task task=processService.getCurrentTask(processInstance);
								if(endflag!=null){
									if(!endflag.isEmpty()){
										if(endflag.equals("continue")){
											/*WorkflowDetails workflowDetails;
											try {
												workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.APPROVAL_WORKFLOW,level);
												motion.setWorkflowDetailsId(workflowDetails.getId());
											} catch (ELSException e) {
												model.addAttribute("error", e.getParameter());
												e.printStackTrace();
											}*/
											
											try{
												//*****
												
												Workflow workflow = null;
												
												/*
												 *
												 * START...
												 */
												/*if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION)
													|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
														workflow = Workflow.findByStatus(domain.getRecommendationStatus(), domain.getLocale());
													} else {
														workflow = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
												}*/
												Status internalStatus = motion.getInternalStatus();
												String internalStatusType = internalStatus.getType();
												Status recommendationStatus = motion.getRecommendationStatus();
												String recommendationStatusType = recommendationStatus.getType();
		
												if(recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
														|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
														|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)
														|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
													workflow = Workflow.findByStatus(recommendationStatus, domain.getLocale());
												} 
												else if(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)
														|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)
														|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
														||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))
														||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
														||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
															&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))) {
													workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
												} 
												else {
													workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
												}
												
												WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
												motion.setWorkflowDetailsId(workflowDetails.getId());
												//*****
											}catch(ELSException ex){
												logger.error("error", ex);
												model.addAttribute("error", ex.getParameter());
											}
	
										}
									}
								}
							}
							
							motion.setWorkflowStarted("YES");
							motion.setWorkflowStartedOn(new Date());
							motion.setTaskReceivedOn(new Date());
							motion.simpleMerge();
						}
					}
				}
			}
			/**** Supporting Member Workflow/Put Up Workflow ****/
	
			/**** Adding to Chart and Group Changed Code Starts ****/
	//		Status internalStatus = domain.getInternalStatus();
	//		String deviceType=domain.getType().getType();
	//		
	//		
	//		CustomParameter csptGroupChangeAllowed = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase() + "_" + domain.getHouseType().getType().toUpperCase() + "_GROUP_CHANGE_ALLOWED", domain.getLocale());
	//		if(csptGroupChangeAllowed != null){
	//			if(csptGroupChangeAllowed.getValue() != null && !csptGroupChangeAllowed.getValue().isEmpty() && csptGroupChangeAllowed.getValue().equals("yes")){
	//				if(internalStatus.getType().equals(ApplicationConstants.STANDALONE_SYSTEM_GROUPCHANGED)) {
	//					StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class, domain.getId());
	//					StandaloneMotionDraft draft = motion.findSecondPreviousDraft();
	//					Group affectedGroup = draft.getGroup();
	//					try{
	//						Chart.groupChange(motion, affectedGroup);
	//					}catch (ELSException e) {
	//						model.addAttribute("StandaloneMotionController", e.getParameter());
	//					}
	//				}
	//			}
	//		}
	//		
	//		
	//		/**** Half hour discussion standalone,lowerhouse,internal status=assistant processed ****/
	//		if(internalStatus.getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
	//				&& deviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
	//				&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
	//			StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class, domain.getId());
	//			if(motion.getNumber()!= null){
	//				try{
	//					Chart.addToChart(motion);					
	//				}catch (ELSException e) {
	//					model.addAttribute(this.getClass().getName(), e.getParameter());
	//				}
	//			}
	//		}
			/**** Adding to Chart and Group Changed Code Ends ****/
		}catch(Exception e){
			logger.error("error", e);
		}
	}	

	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {
		StandaloneMotion motion=StandaloneMotion.findById(StandaloneMotion.class, id);
		if(motion!=null){
			Status status=motion.getStatus();
			if(status.getType().equals(ApplicationConstants.STANDALONE_INCOMPLETE)||status.getType().equals(ApplicationConstants.STANDALONE_COMPLETE)){
				StandaloneMotion.supportingMemberWorkflowDeletion(motion);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	/*
	 * This method is used to view the approval status of a question from the supporting members
	 */
	@RequestMapping(value="/status/{motion}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("motion") final String motion){
		StandaloneMotion motionTemp=StandaloneMotion.findById(StandaloneMotion.class,Long.parseLong(motion));
		List<SupportingMember> supportingMembers=motionTemp.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "standalonemotion/supportingmember";
	}

	@RequestMapping(value="/citations/{deviceType}",method=RequestMethod.GET)
	public String getCitations(final HttpServletRequest request, final Locale locale,@PathVariable("deviceType")  final Long type,
			final ModelMap model){
		DeviceType deviceType=DeviceType.findById(DeviceType.class,type);
		List<Citation> deviceTypeBasedcitations=Citation.findAllByFieldName(Citation.class,"deviceType",deviceType, "text",ApplicationConstants.ASC, locale.toString());
		Status status=null;
		if(request.getParameter("status")!=null){
			status=Status.findById(Status.class, Long.parseLong(request.getParameter("status")));
		}
		List<Citation> citations=new ArrayList<Citation>();
		if(status!=null){
			for(Citation i:deviceTypeBasedcitations){
				if(i.getStatus()!=null){
					if(i.getStatus().equals(status.getType())){
						citations.add(i);
					}
				}
			}
		}
		model.addAttribute("citations",citations);
		return "standalonemotion/citation";
	}

	@RequestMapping(value="/revisions/{motionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("motionId")  final Long questionId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=StandaloneMotion.getRevisions(questionId,locale.toString());
		StandaloneMotion q = StandaloneMotion.findById(StandaloneMotion.class, questionId);
		if(q != null){
			if(q.getType() != null){
				if(q.getType().getType() != null){
					model.addAttribute("selectedDeviceType", q.getType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);		
		return "standalonemotion/revisions";
	}

	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "standalonemotion/contacts";
	}

	/**
	 * To add parameters for new half hour discussion
	 * @param model  
	 * @param domain
	 * @param request
	 */
	private void populateForHalfHourDiscussionNew(final ModelMap model, final StandaloneMotion domain, final Session selectedSession, final DeviceType questionType, final HttpServletRequest request){
		if (selectedSession != null) {

			if(questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				populateForHalfHourDiscussionStandaloneNew(model, domain, selectedSession, questionType, request);
			}
		}         
	}

	private void populateForHalfHourDiscussionStandaloneNew(final ModelMap model, final StandaloneMotion domain, final Session selectedSession, final DeviceType questionType, final HttpServletRequest request){
		Session session = Session.findById(Session.class, selectedSession.getId());
		if (session != null) {

			String strDates = session.getParameter("motions_standalonemotion_halfhourdiscussion_discussionDates");

			if(strDates != null && !strDates.isEmpty()){
				String[] dates = strDates.split("#");

				List<Reference> discussionDates = new ArrayList<Reference>();

				try {
					SimpleDateFormat sdf = FormaterUtil.getDBDateParser(session.getLocale());
					for (int i = 0; i < dates.length; i++) {
						Reference ref = new Reference();
						ref.setId(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(dates[i], ApplicationConstants.DB_DATEFORMAT, "en_US"), ApplicationConstants.SERVER_DATEFORMAT));
						ref.setName(FormaterUtil.getDateFormatter("dd/MM/yyyy", session.getLocale()).format(sdf.parse(dates[i])));
						discussionDates.add(ref);
					}
					model.addAttribute("discussionDates", discussionDates);
					if (domain.getDiscussionDate() != null) {
						model.addAttribute("discussionDateSelected", FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT, "en_US").format(domain.getDiscussionDate()));
						model.addAttribute("formattedDiscussionDateSelected", FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
					}
					
				} catch (ParseException e) {

					e.printStackTrace();
				}
			}
		}

		/*
		 * adding session.parameters.numberOfSupprtingMembers and
		 * session.parametrs.numberOfSupprtingMembersComparator
		 */
		String numberOfSupportingMembers = selectedSession.getParameter(questionType.getType()+"_numberOfSupportingMembers");
		String numberOfSupportingMembersComparator = selectedSession.getParameter(questionType.getType()+"_numberOfSupportingMembersComparator");

		if((numberOfSupportingMembers != null) && (numberOfSupportingMembersComparator != null)){
			model.addAttribute("numberOfSupportingMembers", numberOfSupportingMembers);            
			model.addAttribute("numberOfSupportingMembersComparator", numberOfSupportingMembersComparator);

			if(numberOfSupportingMembersComparator.equalsIgnoreCase("eq")){

				numberOfSupportingMembersComparator = "&#61;";

			}else if(numberOfSupportingMembersComparator.equalsIgnoreCase("lt")){

				numberOfSupportingMembersComparator = "&lt;";

			}else if(numberOfSupportingMembersComparator.equalsIgnoreCase("gt")){

				numberOfSupportingMembersComparator = "&gt;";

			}else if(numberOfSupportingMembersComparator.equalsIgnoreCase("le")){

				numberOfSupportingMembersComparator = "&le;";

			}else if(numberOfSupportingMembersComparator.equalsIgnoreCase("ge")){

				numberOfSupportingMembersComparator = "&ge;";
			}

			model.addAttribute("numberOfSupportingMembersComparatorHTML", numberOfSupportingMembersComparator);

			CustomParameter dateFormatS = CustomParameter.findByFieldName(CustomParameter.class, "name", "SERVER_DATETIMEFORMAT", "");
			CustomParameter dateFormatDB = CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATETIMEFORMAT", "");

			if(dateFormatS != null && dateFormatDB != null ){
				Date startDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("motions_standalonemotion_halfhourdiscussion_submissionStartDate"), dateFormatDB.getValue());
				Date endDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("motions_standalonemotion_halfhourdiscussion_submissionEndDate"), dateFormatDB.getValue());

				model.addAttribute("startDate",FormaterUtil.formatDateToString(startDate, "yyyy/MM/dd HH:mm:ss"));
				model.addAttribute("endDate",FormaterUtil.formatDateToString(endDate, "yyyy/MM/dd HH:mm:ss"));
			}
		}
	}

	/**
	 * To add required parameters for half hour discussion when edit mode 
	 * @param model
	 * @param domain
	 * @param request
	 */
	private void populateForHalfHourDiscussionEdit(final ModelMap model, final StandaloneMotion domain, final HttpServletRequest request) {
		Session selectedSession = domain.getSession();
		DeviceType questionType = domain.getType();

		if (selectedSession != null) {
			if(questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				populateForHalfHourDiscussionStandaloneEdit(model, domain, request);
			}
		}
	}	

	private void populateForHalfHourDiscussionStandaloneEdit(final ModelMap model, final StandaloneMotion domain, final HttpServletRequest request){
		Session selectedSession = domain.getSession();
		DeviceType questionType = domain.getType();

		/*
		 * adding session.parameters.numberOfSupprtingMembers and
		 * session.parametrs.numberOfSupprtingMembersComparator
		 */
		String numberOfSupportingMembers = selectedSession.getParameter(questionType.getType()+ "_numberOfSupportingMembers");
		String numberOfSupportingMembersComparator = selectedSession.getParameter(questionType.getType()+ "_numberOfSupportingMembersComparator");

		if ((numberOfSupportingMembers != null) && (numberOfSupportingMembersComparator != null)) {
			model.addAttribute("numberOfSupportingMembers", numberOfSupportingMembers);
			model.addAttribute("numberOfSupportingMembersComparator", numberOfSupportingMembersComparator);

			if (numberOfSupportingMembersComparator.equalsIgnoreCase("eq")) {

				numberOfSupportingMembersComparator = "&#61;";

			} else if (numberOfSupportingMembersComparator.equalsIgnoreCase("lt")) {

				numberOfSupportingMembersComparator = "&lt;";

			} else if (numberOfSupportingMembersComparator.equalsIgnoreCase("gt")) {

				numberOfSupportingMembersComparator = "&gt;";

			} else if (numberOfSupportingMembersComparator.equalsIgnoreCase("le")) {

				numberOfSupportingMembersComparator = "&le;";

			} else if (numberOfSupportingMembersComparator.equalsIgnoreCase("ge")) {

				numberOfSupportingMembersComparator = "&ge;";
			}

			model.addAttribute("numberOfSupportingMembersComparatorHTML",numberOfSupportingMembersComparator);
		}

		List<Reference> discussionDates = new ArrayList<Reference>();
		SimpleDateFormat sdf = null;

		if (selectedSession != null) {

			//------changed 21012013-----------------
			String strDates = selectedSession.getParameter("motions_standalonemotion_halfhourdiscussion_discussionDates");
			//-----------21012013
			if(strDates != null && !strDates.isEmpty()){

				String[] dates = strDates.split("#");

				try {
					sdf = FormaterUtil.getDBDateParser(selectedSession.getLocale());
					for (int i = 0; i < dates.length; i++) {
						Reference ref = new Reference();
						ref.setId(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(dates[i], ApplicationConstants.DB_DATEFORMAT, "en_US"), ApplicationConstants.SERVER_DATEFORMAT));
						ref.setName(FormaterUtil.getDateFormatter("dd/MM/yyyy", domain.getLocale()).format(sdf.parse(dates[i])));
						discussionDates.add(ref);
					}
					model.addAttribute("discussionDates", discussionDates);
				} catch (ParseException e) {

					e.printStackTrace();
				}
			}
		}

		if (domain.getDiscussionDate() != null) {
			model.addAttribute("discussionDateSelected", FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT, "en_US").format(domain.getDiscussionDate()));
			model.addAttribute("formattedDiscussionDateSelected", FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
		}else{
			model.addAttribute("discussionDateSelected",null);
			model.addAttribute("formattedDiscussionDateSelected", null);
		}		
	}

	@RequestMapping(value="/viewstandalone",method=RequestMethod.GET)
	public String viewQuestion(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strQuestionId = request.getParameter("qid");
		if(strQuestionId != null && !strQuestionId.isEmpty()){
			Long id = new Long(strQuestionId);
			StandaloneMotion q = StandaloneMotion.findById(StandaloneMotion.class, id);

			if(q != null){

				if(q.getSession() != null){
					if(q.getSession() != null){
						if(q.getSession().getHouse() != null){
							model.addAttribute("sessionName",q.getSession().getHouse().getType().getName());
						}

						model.addAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(q.getSession().getYear(), q.getLocale()));
						model.addAttribute("sessionType", q.getSession().getType().getSessionType());
					}
				}

				if(q.getDiscussionDate() != null){
					if(q.getDiscussionDate() != null){
						model.addAttribute("discussionDate",FormaterUtil.getDateFormatter("dd/MM/yyyy", q.getLocale().toString()).format(q.getDiscussionDate()));
					}else{
						model.addAttribute("discussionDate","");
					}
				}

				model.addAttribute("subject", q.getSubject());
				model.addAttribute("qText", q.getBriefExplanation());
				model.addAttribute("qReason", q.getReason());
				model.addAttribute("qAnswer", q.getAnswer());


				Member member=  q.getPrimaryMember();
				if(member.getId()!=null){          
					model.addAttribute("primaryMemberName",member.getFullname());
				}
			}
		}
		return "standalonemotion/viewquestion";
	}

	@RequestMapping(value="/getsubject",method=RequestMethod.GET)
	public @ResponseBody MasterVO getSubjectAndQuestion(final HttpServletRequest request,final ModelMap model,final Locale locale){

		String strQuestionId = request.getParameter("qid");
		String text = request.getParameter("text");
		MasterVO masterVO = new MasterVO();

		if(strQuestionId != null){
			if(!strQuestionId.isEmpty()){

				Long id = new Long(strQuestionId);
				StandaloneMotion q = StandaloneMotion.findById(StandaloneMotion.class, id);

				if(text != null){
					if(!text.isEmpty()){
						if(text.equals("1")){

							masterVO.setId(q.getId());
							masterVO.setName(q.getSubject());
							masterVO.setValue(q.getReason());
							masterVO.setFormattedNumber(q.getType().getName());

						}
					}
				}
			}
		}
		return masterVO;
	}

	/**** BULK SUBMISSION (MEMBER) ****/

	@RequestMapping(value="/bulksubmission", method=RequestMethod.GET)
	public String getBulkSubmissionView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("questionType");
		String strLocale = locale.toString();
		String strItemsCount = request.getParameter("itemscount");
		String strFileNumber = request.getParameter("fileNumber");

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, 
					"type", strHouseType, strLocale);
			SessionType sessionType = SessionType.findById(SessionType.class,
					Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);


				DeviceType deviceType = DeviceType.findById(DeviceType.class, 
						Long.parseLong(strDeviceType));

				Integer itemsCount = Integer.parseInt(strItemsCount);

				Member primaryMember = Member.findMember(this.getCurrentUser().getFirstName(),
						this.getCurrentUser().getMiddleName(),
						this.getCurrentUser().getLastName(),
						this.getCurrentUser().getBirthDate(),
						strLocale);


				List<StandaloneMotion> questions = new ArrayList<StandaloneMotion>();
				if(primaryMember != null){
					questions = StandaloneMotion.findAllByMember(session, primaryMember,deviceType, itemsCount, strLocale);	
				}	
				model.addAttribute("houseType", houseType.getId());
				model.addAttribute("questionType", deviceType.getId());
				model.addAttribute("deviceType", deviceType.getId());
				model.addAttribute("questions", questions);
				model.addAttribute("size", questions.size());
				model.addAttribute("locale", locale.toString());

				String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupType", userGroupType);
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "standalonemotion/bulksubmission";
	}

	/**
	 * We want to provide a guarantee that all the questions submitted by a 
	 * particular member will get numbers assigned sequentially. Hence, the
	 * use of synchronized method.
	 */
	@Transactional
	@RequestMapping(value="bulksubmission", method=RequestMethod.POST)
	public synchronized String bulkSubmission(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String selectedItems = request.getParameter("items");

		if(selectedItems != null && ! selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");			
			StandaloneMotion domain = StandaloneMotion.findById(StandaloneMotion.class, new Long(items[0]));
			Session session = domain.getSession();
			boolean validationForSubmissionDate = false;
			//submission date limit validation
			CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
			if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
				String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
				if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
					String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
					for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
						if(dt.trim().equals(domain.getType().getType().trim())) {
							
							if(session.getParameter(domain.getType().getType() + "_" + "submissionStartDate")!=null && !session.getParameter(domain.getType().getType() + "_" + "submissionStartDate").isEmpty()
									&& session.getParameter(domain.getType().getType() + "_" + "submissionEndDate")!=null && !session.getParameter(domain.getType().getType() + "_" + "submissionEndDate").isEmpty()) {
								
								if(!StandaloneMotion.isAllowedForSubmission(domain, new Date())) {
									//String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_submissionStartDate");
									validationForSubmissionDate = true;
								}
							} else {
								validationForSubmissionDate = true;
							}
							
							break;
						}
					}
				}
			}

			if(!validationForSubmissionDate) {
				List<StandaloneMotion> questions = new ArrayList<StandaloneMotion>();
				for(String i : items) {
					Long id = Long.parseLong(i);
					StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class, id);

					/**** Update Supporting Member ****/
					List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
					Status timeoutStatus = Status.findByType(
							ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
					if(question.getSupportingMembers() != null
							&& ! question.getSupportingMembers().isEmpty()) {
						for(SupportingMember sm : question.getSupportingMembers()) {
							if(sm.getDecisionStatus().getType().equals(
									ApplicationConstants.SUPPORTING_MEMBER_NOTSEND) ||
									sm.getDecisionStatus().getType().equals(
											ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
								/**** Update Supporting Member ****/
								sm.setDecisionStatus(timeoutStatus);
								sm.setApprovalDate(new Date());	
								sm.setApprovedText(question.getReason());
								sm.setApprovedSubject(question.getSubject());
								sm.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);

								/**** Update Workflow Details ****/
								String strWorkflowdetails = sm.getWorkflowDetailsId();
								if(strWorkflowdetails != null && ! strWorkflowdetails.isEmpty()) {
									WorkflowDetails workflowDetails = WorkflowDetails.findById(
											WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
									workflowDetails.setStatus("TIMEOUT");
									workflowDetails.setCompletionTime(new Date());
									workflowDetails.merge();

									/**** Complete Task ****/
									String strTaskId = workflowDetails.getTaskId();
									Task task = processService.findTaskById(strTaskId);
									processService.completeTask(task);
								}
							}

							if(! sm.getDecisionStatus().getType().equals(
									ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
								supportingMembers.add(sm);
							}
						}

						question.setSupportingMembers(supportingMembers);
					}

					/**** Update Status(es) ****/
					Status newstatus = Status.findByFieldName(Status.class, "type", 
							ApplicationConstants.STANDALONE_SUBMIT, question.getLocale());
					question.setStatus(newstatus);
					question.setInternalStatus(newstatus);
					question.setRecommendationStatus(newstatus);

					/**** Edited On, Edited By and Edited As is set ****/
					question.setSubmissionDate(new Date());
					question.setEditedOn(new Date());
					question.setEditedBy(this.getCurrentUser().getActualUsername());

					String strUserGroupType = request.getParameter("usergroupType");
					if(strUserGroupType != null) {
						UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class,
								"type", strUserGroupType, question.getLocale());
						question.setEditedAs(userGroupType.getName());
					}

					/**** Bulk Submitted ****/
					question.setBulkSubmitted(true);

					/**** Update the Motion object ****/
					question = question.merge();
					questions.add(question);
				}

				model.addAttribute("questions", questions);
			}			
		}

		return "standalonemotion/bulksubmissionack";
	}

	/**** Yaadi to discuss update ****/
	@RequestMapping(value="/yaaditodiscussupdate/assistant/init", method=RequestMethod.GET)
	public String getYaadiToDiscussInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Request Params ****/
		String retVal = "standalonemotion/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strGroup = request.getParameter("group");

		/**** Locale ****/
		String strLocale = locale.toString();

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));

			/**** Decision Status Available To Assistant(At this stage) 
			 * QUESTION_PUT_UP_OPTIONS_ + QUESTION_TYPE + HOUSE_TYPE + USERGROUP_TYPE ****/
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class, "STANDALONE_YAADI_UPDATE_" + deviceType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() + "_" + strUsergroupType.toUpperCase(), "");

			List<Status> internalStatuses;
			try {
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("deviceType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("group", strGroup);

			retVal = "standalonemotion/yaaditoduscussupdateinit";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}
	
	@RequestMapping(value="/yaaditodiscussupdate/assistant/view", method=RequestMethod.GET)
	public String getYaadiToDiscussUpdateAssistantView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		this.getYaadiToDiscussUpdateMotions(model, request, locale.toString());
		return "standalonemotion/bulksubmissionassistantview";
	}
	
	@Transactional
	@RequestMapping(value="/yaaditodiscussupdate/assistant/update", method=RequestMethod.POST)
	public String yaadiToDiscussUpdateAssistant(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		boolean updated = false;
		String page = "standalonemotion/error";
		StringBuffer success = new StringBuffer();
		
		try{
			String[] selectedItems = request.getParameterValues("items[]");
			String strDecisionStatus = request.getParameter("decisionStatus");
			String strStatus = request.getParameter("status");
			
			if(selectedItems != null && selectedItems.length > 0
					&& strDecisionStatus != null && !strDecisionStatus.isEmpty()
					&& strStatus != null && !strStatus.isEmpty()) {
				/**** As It Is Condition ****/
				if(!strStatus.equals("-")) {
					for(String i : selectedItems) {
						Long id = Long.parseLong(i);
						StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class, id);
						Status discussed = Status.findById(Status.class, new Long(strDecisionStatus));
						question.setRecommendationStatus(discussed);
						question.simpleMerge();
						updated = true;
						success.append(FormaterUtil.formatNumberNoGrouping(question.getNumber(), question.getLocale())+",");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			updated = false;
		}
		
		if(updated){
			this.getYaadiToDiscussUpdateMotions(model, request, locale.toString());
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "standalonemotion/yaaditodiscussupdateview";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	
	/**** BULK SUBMISSION (ASSISTANT) ****/

	@RequestMapping(value="/bulksubmission/assistant/int", method=RequestMethod.GET)
	public String getBulkSubmissionAssistantInt(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Request Params ****/
		String retVal = "question/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strItemsCount = request.getParameter("itemscount");
		String strFile = request.getParameter("file");
		String strGroup = request.getParameter("group");

		/**** Locale ****/
		String strLocale = locale.toString();

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())
				&& strFile != null && !(strFile.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", 
					strHouseType, strLocale);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, 
					Long.parseLong(strDeviceType));

			/**** Decision Status Available To Assistant(At this stage) 
			 * QUESTION_PUT_UP_OPTIONS_ + QUESTION_TYPE + HOUSE_TYPE + USERGROUP_TYPE ****/
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,
					"STANDALONE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" +
							houseType.getType().toUpperCase() + "_" + strUsergroupType.toUpperCase(), "");

			List<Status> internalStatuses;
			try {
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("deviceType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("itemscount", strItemsCount);
			model.addAttribute("file", strFile);
			model.addAttribute("group", strGroup);

			retVal = "standalonemotion/bulksubmissionassistantint";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}

	@RequestMapping(value="/bulksubmission/assistant/view", method=RequestMethod.GET)
	public String getBulkSubmissionAssistantView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		this.getBulkSubmissionQuestions(model, request, locale.toString());
		return "standalonemotion/bulksubmissionassistantview";
	}

	@Transactional
	@RequestMapping(value="/bulksubmission/assistant/update", method=RequestMethod.POST)
	public String bulkSubmissionAssistant(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {

		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus = request.getParameter("assistatus");
		String strRemarks = request.getParameter("remarks");
		String strRefText = request.getParameter("refertext");
		String strFile = request.getParameter("file");
		StringBuffer assistantProcessed = new StringBuffer();
		StringBuffer recommendAdmission = new StringBuffer();
		StringBuffer recommendRejection = new StringBuffer();
		StringBuffer recommendRepeatRejection = new StringBuffer();
		StringBuffer recommendRepeatAdmission = new StringBuffer();
		StringBuffer recommendClarificationFromMember = new StringBuffer();
		StringBuffer recommendClarificationFromDept = new StringBuffer();
		StringBuffer recommendClarificationFromGovt = new StringBuffer();
		StringBuffer recommendClarificationFromMemberDept = new StringBuffer();
		UserGroupType usergroupType = null;


		if(selectedItems != null && selectedItems.length > 0
				&& strStatus != null && !strStatus.isEmpty()) {
			
			List<ReferenceUnit> refs = null;
			for(String i : selectedItems){
				StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class, new Long(i));
				if(question != null){
					if(question.getReferencedEntities() != null){
						refs = question.getReferencedEntities();
						break;
					}
				}
			}
			/**** As It Is Condition ****/
			if(strStatus.equals("-")) {
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class, id);

					if(!question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)){
						/**** Create Process ****/
						ProcessDefinition processDefinition = null;
						Map<String,String> properties = new HashMap<String, String>();
						String actor = question.getActor();
						String[] temp = actor.split("#");
						usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						
						if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
								&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

							String userGroupType = request.getParameter("usergroupType");
							processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);


							properties.put("pv_user", temp[0]);						
							properties.put("pv_endflag", question.getEndFlag());								
							properties.put("pv_deviceId",String.valueOf(question.getId()));								
							properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
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

											String reminderto = "";
											String username = getCurrentUser().getUsername();
											if(userGroupType.equals(ApplicationConstants.SECTION_OFFICER) 
													&& (question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
															||question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))){
												Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
												reminderto = recepient.getEmail();								
											} else {
												reminderto=request.getParameter("reminderto");								
											}						
											properties.put("pv_reminderto", reminderto);

											String remindersubject=request.getParameter("remindersubject");						
											properties.put("pv_remindersubject", remindersubject);

											String remindercontent = "";
											if(userGroupType.equals(ApplicationConstants.SECTION_OFFICER) 
													&& (question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
															|| question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
												remindercontent += question.getRevisedQuestionText() + "\n\n";
												if(question.getQuestionsAskedInFactualPosition() !=null 
														&& !question.getQuestionsAskedInFactualPosition().isEmpty()) {
													int count = 1;
													for(String s: question.getQuestionsAskedInFactualPosition().split("##")) {
														remindercontent += FormaterUtil.formatNumberNoGrouping(count, question.getLocale()) + ". " + i + "\n\n";
														count++;
													}
												}								
											} else {
												remindercontent=request.getParameter("remindercontent");								
											}					
											properties.put("pv_remindercontent", remindercontent);						
										}
									}
								}						
							}
						}else{
							processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);

							properties.put("pv_user", temp[0]);						
							properties.put("pv_endflag", question.getEndFlag());	
							properties.put("pv_deviceId", String.valueOf(question.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
						}

						ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);

						/**** Create Workdetails Entry ****/
						Task task = processService.getCurrentTask(processInstance);
						if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
								&& question.getEndFlag().equals("continue")){

							try {
								Workflow workflow = null;

								Status internalStatus = question.getInternalStatus();
								String internalStatusType = internalStatus.getType();
								Status recommendationStatus = question.getRecommendationStatus();
								String recommendationStatusType = recommendationStatus.getType();

								if(recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
										|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
										|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)
										|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
									workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
								} 
								else if(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)
										|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)
										|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
											&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
										||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
											&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))
										||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
											&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
										||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
											&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))) {
									workflow = Workflow.findByStatus(internalStatus, locale.toString());
								}
								else {
									workflow = Workflow.findByStatus(internalStatus, locale.toString());
								}

								String workflowType = workflow.getType();
								String assigneeLevel = question.getLevel();
								WorkflowDetails workflowDetails = WorkflowDetails.create(question, task, usergroupType, workflowType, assigneeLevel);

								if(refs != null && !refs.isEmpty()) {
									question.setReferencedEntities(refs);
								}
								
								if(strRemarks != null){
									question.setRemarks(strRemarks);
								}
								
								if(strRefText != null){
									question.setRefText(strRefText);
								}
								
								if(strFile != null && !strFile.isEmpty()){
									if(question.getFile() == null){
										question.setFile(new Integer(strFile));
									}
								}
								question.setWorkflowDetailsId(workflowDetails.getId());
								question.setWorkflowStarted("YES");
								question.setWorkflowStartedOn(new Date());
								question.setTaskReceivedOn(new Date());
								question.simpleMerge();
							} catch (ELSException e) {
								model.addAttribute("error", e.getParameter());
							}

						}

						if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_ADMISSION)){
							recommendAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_REJECTION)){
							recommendRejection.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_REPEATREJECTION)){
							recommendRepeatRejection.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_REPEATADMISSION)){
							recommendRepeatAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
							recommendClarificationFromMember.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
							recommendClarificationFromDept.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_GOVT)){
							recommendClarificationFromGovt.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
							recommendClarificationFromMemberDept.append(question.formatNumber() + ",");
						}
					}
					else {
						assistantProcessed.append(question.formatNumber() + ",");
					}
				}

				model.addAttribute("assistantProcessed", assistantProcessed.toString());
			}else {
				Long statusId = Long.parseLong(strStatus);
				Status status = Status.findById(Status.class, statusId);

				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class, id);
					
					String actor = request.getParameter("actor");
					String level = request.getParameter("level");
					if(actor != null && !actor.isEmpty() && level != null && !level.isEmpty()) {
						Reference reference;
						try {
							reference = UserGroup.findStandaloneMotionActor(question, actor, level, locale.toString());

							if(reference != null
									&& reference.getId() != null && !reference.getId().isEmpty()
									&& reference.getName() != null && !reference.getName().isEmpty()) {

								/**** Update Actor ****/
								String[] temp = reference.getId().split("#");
								question.setActor(reference.getId());
								question.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
								question.setLevel(temp[2]);
								usergroupType = UserGroupType.findByType(temp[1], locale.toString());

								/**** Update Internal Status and Recommendation Status ****/
								question.setInternalStatus(status);
								question.setRecommendationStatus(status);	
								question.setEndFlag("continue");

								/**** Create Process ****/
								ProcessDefinition processDefinition = null;
								Map<String, String> properties = new HashMap<String, String>();
								if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
										&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

									String userGroupType = request.getParameter("usergroupType");
									processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);


									properties.put("pv_user", temp[0]);						
									properties.put("pv_endflag", question.getEndFlag());								
									properties.put("pv_deviceId",String.valueOf(question.getId()));								
									properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
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

													String reminderto = "";
													String username = getCurrentUser().getUsername();
													if(userGroupType.equals(ApplicationConstants.SECTION_OFFICER) 
															&& (question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
																	||question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))){
														Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
														reminderto = recepient.getEmail();								
													} else {
														reminderto=request.getParameter("reminderto");								
													}						
													properties.put("pv_reminderto", reminderto);

													String remindersubject=request.getParameter("remindersubject");						
													properties.put("pv_remindersubject", remindersubject);

													String remindercontent = "";
													if(userGroupType.equals(ApplicationConstants.SECTION_OFFICER) 
															&& (question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
																	|| question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
														remindercontent += question.getRevisedQuestionText() + "\n\n";
														if(question.getQuestionsAskedInFactualPosition() !=null 
																&& !question.getQuestionsAskedInFactualPosition().isEmpty()) {
															int count = 1;
															for(String s: question.getQuestionsAskedInFactualPosition().split("##")) {
																remindercontent += FormaterUtil.formatNumberNoGrouping(count, question.getLocale()) + ". " + i + "\n\n";
																count++;
															}
														}								
													} else {
														remindercontent=request.getParameter("remindercontent");								
													}					
													properties.put("pv_remindercontent", remindercontent);						
												}
											}
										}						
									}
								}else{
									processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);

									properties.put("pv_user", temp[0]);						
									properties.put("pv_endflag", question.getEndFlag());	
									properties.put("pv_deviceId", String.valueOf(question.getId()));
									properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
									usergroupType = UserGroupType.findByType(temp[1], locale.toString());
								}

								ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);

								/**** Create Workdetails Entry ****/
								Task task = processService.getCurrentTask(processInstance);
								if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
										&& question.getEndFlag().equals("continue")) {

									Workflow workflow = null;

									Status internalStatus = question.getInternalStatus();
									String internalStatusType = internalStatus.getType();
									Status recommendationStatus = question.getRecommendationStatus();
									String recommendationStatusType = recommendationStatus.getType();

									if(recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
									} 
									else if(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)
											|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)
											|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))) {
										workflow = Workflow.findByStatus(internalStatus, locale.toString());
									} 
									else {
										workflow = Workflow.findByStatus(internalStatus, locale.toString());
									}

									String workflowType = workflow.getType();
									String assigneeLevel = question.getLevel();
									WorkflowDetails workflowDetails = WorkflowDetails.create(question, task, usergroupType, workflowType, assigneeLevel); 
									//workflowDetails = WorkflowDetails.create(motion, task, workflowType, assigneeLevel);
									
									if(refs != null && !refs.isEmpty()) {
										question.setReferencedEntities(refs);
									}
									if(strRemarks != null){
										question.setRemarks(strRemarks);
									}
									if(strRefText != null){
										question.setRefText(strRefText);
									}
									if(strFile != null && !strFile.isEmpty()){
										if(question.getFile() == null){
											question.setFile(new Integer(strFile));
										}
									}
									question.setWorkflowDetailsId(workflowDetails.getId());
									/**** Workflow Started ****/
									question.setWorkflowStarted("YES");
									question.setWorkflowStartedOn(new Date());
									question.setTaskReceivedOn(new Date());
									question.simpleMerge();
								}

								if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_ADMISSION)){
									recommendAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_REJECTION)){
									recommendRejection.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_REPEATREJECTION)){
									recommendRepeatRejection.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_REPEATADMISSION)){
									recommendRepeatAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
									recommendClarificationFromMember.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
									recommendClarificationFromDept.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_GOVT)){
									recommendClarificationFromGovt.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
									recommendClarificationFromMemberDept.append(question.formatNumber() + ",");
								}
							}//reference
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
						}
					}
				}

				model.addAttribute("recommendAdmission", recommendAdmission.toString());
				model.addAttribute("recommendRejection", recommendRejection.toString());
				model.addAttribute("recommendRepeatRejection", recommendRepeatRejection.toString());
				model.addAttribute("recommendRepeatAdmission", recommendRepeatAdmission.toString());
				model.addAttribute("recommendClarificationFromMember", recommendClarificationFromMember.toString());
				model.addAttribute("recommendClarificationFromDept", recommendClarificationFromDept.toString());
				model.addAttribute("recommendClarificationFromGovt", recommendClarificationFromGovt.toString());
				model.addAttribute("recommendClarificationFromMemberDept", recommendClarificationFromMemberDept.toString());
			}				
		}

		this.getBulkSubmissionQuestions(model, request, locale.toString());
		return "standalonemotion/bulksubmissionassistantview";
	}

	/**** Used in bulk approval of supporting members to fetch question details ****/
	@RequestMapping(value="/{id}/details", method=RequestMethod.GET)
	public String getDetails(@PathVariable("id")final Long id,
			final Model model){
		StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class, id);
		model.addAttribute("details", question.getReason());
		return "standalonemotion/details";
	}	

	private void getBulkSubmissionQuestions(final ModelMap model,
			final HttpServletRequest request, 
			final String locale) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strItemsCount = request.getParameter("itemscount");
		String strFile = request.getParameter("file");
		String strGroup = request.getParameter("group");

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroup != null && !(strUsergroup.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())
				&& strFile != null && !(strFile.isEmpty())) {
			List<StandaloneMotion> questions = new ArrayList<StandaloneMotion>();

			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", 
					strHouseType, locale);
			SessionType sessionType = SessionType.findById(SessionType.class, 
					Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);


				DeviceType deviceType = DeviceType.findById(DeviceType.class, 
						Long.parseLong(strDeviceType));
				Group group=null;
				if(strGroup!=null && !strGroup.isEmpty()){
					group=Group.findById(Group.class, Long.parseLong(strGroup));
				}

				Integer file = null;
				if(strFile != null && !strFile.isEmpty()){
					file = new Integer(strFile);
				}
				Integer itemsCount = Integer.parseInt(strItemsCount);
				Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));
				questions = StandaloneMotion.findAllByStatus(session, deviceType, internalStatus,group ,
						itemsCount, file, locale);

				model.addAttribute("questions", questions);
				if(questions != null && ! questions.isEmpty()) {
					model.addAttribute("questionId", questions.get(0).getId());
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
		}
	}	
	
	private void getYaadiToDiscussUpdateMotions(final ModelMap model,
			final HttpServletRequest request, 
			final String locale) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strGroup = request.getParameter("group");

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroup != null && !(strUsergroup.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())) {
			List<StandaloneMotion> questions = new ArrayList<StandaloneMotion>();

			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);


				DeviceType deviceType = DeviceType.findById(DeviceType.class, 
						Long.parseLong(strDeviceType));
				Group group=null;
				if(strGroup!=null && strGroup !=""){
					group=Group.findById(Group.class, Long.parseLong(strGroup));
				}
				
				Status recommendationStatus = Status.findById(Status.class,Long.parseLong(strStatus));
				questions = StandaloneMotion.findAllByRecommendationStatus(session, deviceType, recommendationStatus, group , locale);

				model.addAttribute("questions", questions);
				if(questions != null && ! questions.isEmpty()) {
					model.addAttribute("questionId", questions.get(0).getId());
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
		}
	}
	
	private Boolean isMemberAllowedWithMoreNumberedHalfHours(StandaloneMotion domain){
		Boolean retVal = true;
		
		try{
			
			int memberPutUpCount = 0;
			int memberChartCount = 0;
			int lowerHouseCount = 0;
			
			CustomParameter csptMaxMemberHDS = CustomParameter.findByName(CustomParameter.class, "NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_LH", "");
			
			if(csptMaxMemberHDS != null && csptMaxMemberHDS.getValue() != null && !csptMaxMemberHDS.getValue().isEmpty()){
				lowerHouseCount = Integer.parseInt(csptMaxMemberHDS.getValue());
			}
			
			memberPutUpCount = StandaloneMotion.findStandaloneMotionWithNumberExcludingRejected(domain.getPrimaryMember(), domain.getType(), domain.getSession(), domain.getLocale());
			
			Chart chart = Chart.find(new Chart(domain.getSession(),domain.getType(), domain.getLocale()));
			if(chart != null){
				List<Device> devices = Chart.findDevices(domain.getPrimaryMember(), chart);
				
				if(devices != null){
					for(Device d : devices){
						if(d instanceof StandaloneMotion){
							StandaloneMotion sm = ((StandaloneMotion)d);
							if(!sm.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)){
								memberChartCount++;
							}
						}
					}
				}
				
				if(memberChartCount == lowerHouseCount){
					retVal = false;
				}else{
					retVal = true;
				}
				
			}else{
				if(memberPutUpCount < lowerHouseCount){
					retVal = true;
				}else{
					retVal = false;
				}
			}
			
			
			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	public static List<Reference> getReferencedEntityReferences(StandaloneMotion domain,
			String locale) {
		List<Reference> refentities = new ArrayList<Reference>();
		List<ReferenceUnit> referencedEntities = domain.getReferencedEntities();
		if(referencedEntities != null && !referencedEntities.isEmpty()){
			for(ReferenceUnit re : referencedEntities){
				if(re.getDeviceType() != null){
					if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_STANDALONE)){
						StandaloneMotion q = StandaloneMotion.findById(StandaloneMotion.class, re.getDevice());
						if(q != null){
								Reference reference=new Reference();
								reference.setId(String.valueOf(re.getId()));
								Integer number = re.getNumber();
								
								StringBuffer detail = new StringBuffer();						
								
								detail.append(" (" + q.getStatus().getName()
										+ ", "
										+ FormaterUtil.formatNumberNoGrouping(q.getSession().getYear(), locale) + ", "
										+ re.getSessionTypeName());
								
								if(q.getDiscussionDate() != null){
									detail.append(", " + FormaterUtil.formatDateToString(q.getDiscussionDate(), 
											ApplicationConstants.SERVER_DATEFORMAT, locale) + ")");
								}else{
									detail.append(", -)");
								}
								
								reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(number) + detail.toString());
								reference.setNumber(String.valueOf(q.getId()));
								refentities.add(reference);
						}
					}
				}
			}
		}
		return refentities;
	}
	
	@RequestMapping(value = "/similarsubmissioninit", method = RequestMethod.GET)
	public String similarSubmissionInit(final HttpServletRequest request,
			final ModelMap model, final Locale locale) {
		String retVal = "standalonemotion/error";

		CustomParameter csptSessionCount = CustomParameter.findByName(CustomParameter.class, "SESSION_COUNT_FOR_SIMILAR_SUBMISSION", "");
		if(csptSessionCount != null && csptSessionCount.getValue() != null && !csptSessionCount.getValue().isEmpty()){
			List<Reference> references = new ArrayList<Reference>();
			int sessionCount = Integer.parseInt(csptSessionCount.getValue());
			for(int i = 1; i <= sessionCount; i++){
				Reference ref = new Reference();
				ref.setId(String.valueOf(i));
				ref.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
				references.add(ref);
			}
			
			model.addAttribute("sessionCount", references);
		}
		
		/**** Advanced Search Filters ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");

		HouseType houseType = null;
		Integer sessionYear = null;
		SessionType sessionType = null;
		Session session = null;
		try {
			houseType = HouseType.findByType(strHouseType, locale.toString());
			sessionYear = new Integer(strSessionYear);
			sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));

			session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);

			model.addAttribute("houseType", houseType.getType());
		} catch (Exception e) {
			logger.error("error", e);
		}

		try {
			model.addAttribute("deviceTypes", DeviceType.findDeviceTypesStartingWith("motions_standalonemotion_", locale.toString()));
		} catch (ELSException e) {
			model.addAttribute("StandaloneMotionController", "Request can not be completed at the moment.");
		}

		List<Group> allgroups = null;
		try {
			allgroups = Group.findByHouseTypeSessionTypeYear(houseType,
					sessionType, sessionYear);
		} catch (ELSException e) {
			model.addAttribute("StandaloneMotionController",
					"Request can not be completed at the moment.");
		}

		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		for (Group i : allgroups) {
			MasterVO masterVO = new MasterVO(i.getId(), FormaterUtil
					.getNumberFormatterNoGrouping(locale.toString()).format(
							i.getNumber()));
			masterVOs.add(masterVO);
		}
		model.addAttribute("groups", masterVOs);
		int year = sessionYear;
		CustomParameter houseFormationYear = CustomParameter.findByName(
				CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		List<Reference> years = new ArrayList<Reference>();
		if (houseFormationYear != null) {
			Integer formationYear = Integer.parseInt(houseFormationYear
					.getValue());
			for (int i = year; i >= formationYear; i--) {
				Reference reference = new Reference(String.valueOf(i),
						FormaterUtil.getNumberFormatterNoGrouping(
								locale.toString()).format(i));
				years.add(reference);
			}
		} else {
			model.addAttribute("flag", "houseformationyearnotset");
			return "standalonemotion/error";
		}

		DeviceType deviceType = null;
		try {
			String strDevice = request.getParameter("deviceType");
			if (strDevice != null && !strDevice.isEmpty()) {
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDevice));
			}
		} catch (Exception e) {
			logger.error("error", e);
		}

		if (deviceType != null) {
			model.addAttribute("deviceType", deviceType.getId());
		}

		model.addAttribute("years", years);
		model.addAttribute("sessionYear", year);
		List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,
				"sessionType", ApplicationConstants.ASC, locale.toString());
		model.addAttribute("sessionTypes", sessionTypes);
		model.addAttribute("sessionType", session.getType().getId());
		model.addAttribute("whichDevice", "motions_standalonemotion_");

//		CustomParameter csptSearchByFacility = CustomParameter.findByName(
//				CustomParameter.class, "SEARCHFACILITY_SEARCH_BY", "");
//		if (csptSearchByFacility != null
//				&& csptSearchByFacility.getValue() != null
//				&& !csptSearchByFacility.getValue().isEmpty()) {
//			List<MasterVO> searchByData = new ArrayList<MasterVO>();
//			for (String sf : csptSearchByFacility.getValue().split(";")) {
//				String[] data = sf.split(":");
//				MasterVO newVO = new MasterVO();
//				newVO.setValue(data[0]);
//				newVO.setName(data[1]);
//				searchByData.add(newVO);
//			}
//			model.addAttribute("searchBy", searchByData);
//		}
		retVal = "standalonemotion/similarsubmissioninit";
		return retVal;
	}
	
	@RequestMapping(value = "filing/{id}/{file}/enter", method = RequestMethod.GET)
	public @ResponseBody String filing(@PathVariable("id") Long id, @PathVariable("file") Integer file, HttpServletRequest request, Locale locale){
		
		String retVal = "FAILURE";
		
		try{
			StandaloneMotion sm = StandaloneMotion.findById(StandaloneMotion.class, id);
			
			if(sm != null){
				sm.setFile(file);
				sm.simpleMerge();
				
				retVal = "SUCCESS";
			}			
			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	public static List<UserGroupType> delimitedStringToUGTList(final String delimitedUserGroups,
			final String delimiter,
			final String locale) {
		List<UserGroupType> userGroupTypes = new ArrayList<UserGroupType>();
		
		String[] strUserGroupTypes = delimitedUserGroups.split(delimiter);
		for(String strUserGroupType : strUserGroupTypes) {
			strUserGroupType = strUserGroupType.trim();
			UserGroupType ugt = UserGroupType.findByType(strUserGroupType, locale);
			userGroupTypes.add(ugt);
		}
		
		return userGroupTypes;
	}
	
	/**
	 * Return a userGroup from @param userGroups whose userGroupType is 
	 * same as one of the @param userGroupTypes.
	 * 
	 * Return null if no match is found.
	 * @throws ELSException 
	 */
	public static UserGroup getUserGroup(final List<UserGroup> userGroups,
			final List<UserGroupType> userGroupTypes, 
			final Session session,
			final String locale) throws ELSException {		
		for(UserGroup ug : userGroups) {
			if(UserGroup.isActiveInSession(session,ug,locale)){
				for(UserGroupType ugt : userGroupTypes) {
					UserGroupType userGroupType = ug.getUserGroupType();
					if(ugt.getId().equals(userGroupType.getId())) {
						return ug;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Return true if @param userGroupType is present in the collection
	 * @param userGroupTypes 
	 */
	public static boolean isUserGroupTypeExists(final List<UserGroupType> userGroupTypes,
			final UserGroupType userGroupType) {
		if(userGroupType != null){
			for(UserGroupType ugt : userGroupTypes) {
				if(ugt != null){
					if(ugt.getId().equals(userGroupType.getId())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static List<SubDepartment> getSubDepartments(final String delimitedSubDepartmentNames,
			final String delimiter,
			final String locale) {
		List<SubDepartment> subDepartments = new ArrayList<SubDepartment>();
		
		String subDepartmentNames[] = delimitedSubDepartmentNames.split(delimiter);
		for(String subDepartmentName : subDepartmentNames){
			SubDepartment subDepartment = 
					SubDepartment.findByName(SubDepartment.class, subDepartmentName, locale);
			subDepartments.add(subDepartment);
		}
		
		return subDepartments;
	}
	
	public static List<Integer> delimitedStringToIntegerList(final String delimitedInts,
			final String delimiter) {
		List<Integer> ints = new ArrayList<Integer>();
		
		String[] strInts = delimitedInts.split(delimiter);
		for(String strInt : strInts) {
			Integer i = Integer.parseInt(strInt);
			ints.add(i);
		}
		
		return ints;
	}
	
	
	@RequestMapping(value="/statusupdate/assistant/init", method=RequestMethod.GET)
	public String getStatusUpdateInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Request Params ****/
		String retVal = "standalonemotion/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("motionType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		
		
		

		/**** Locale ****/
		String strLocale = locale.toString();

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class, "STANDALONE_STATUS_UPDATE_" + deviceType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() + "_" + strUsergroupType.toUpperCase(), "");

			List<Status> internalStatuses;
			try {
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("motionType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			

			retVal = "standalonemotion/statusupdateinit";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}
	
	
	@RequestMapping(value="/statusupdate/assistant/view", method=RequestMethod.GET)
	public String getStatusUpdateAssistantView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		this.getStatusUpdateStandalonemotion(model, request, locale.toString());
		return "standalonemotion/statusupdateassistantview";
	}
	
	private void getStatusUpdateStandalonemotion(final ModelMap model,
			final HttpServletRequest request, 
			final String locale) {
				/**** Request Params ****/
				String strHouseType = request.getParameter("houseType");
				String strSessionType = request.getParameter("sessionType");
				String strSessionYear = request.getParameter("sessionYear");
				String strDeviceType = request.getParameter("motionType");			
				String strStatus = request.getParameter("status");
				String strRole = request.getParameter("role");
				String strUsergroup = request.getParameter("usergroup");
				String strUsergroupType = request.getParameter("usergroupType");
				
				
				
				if(strHouseType != null && !(strHouseType.isEmpty())
						&& strSessionType != null && !(strSessionType.isEmpty())
						&& strSessionYear != null && !(strSessionYear.isEmpty())
						&& strDeviceType != null && !(strDeviceType.isEmpty())
						&& strStatus != null && !(strStatus.isEmpty())
						&& strRole != null && !(strRole.isEmpty())
						&& strUsergroup != null && !(strUsergroup.isEmpty())
						&& strUsergroupType != null && !(strUsergroupType.isEmpty())) {
					List<StandaloneMotion> motions = new ArrayList<StandaloneMotion>();
				
					HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale);
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					Session session;
					try {
						session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
				
				
						DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));				
						
						Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));
						
						
						motions = StandaloneMotion.findAllAdmittedUndisccussed(session, deviceType, internalStatus, locale);
						
						
						model.addAttribute("motions", motions);
						if(motions != null && ! motions.isEmpty()) {
							model.addAttribute("motionId", motions.get(0).getId());
						}
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
						e.printStackTrace();
					}
				}
			}
	
	@Transactional
	@RequestMapping(value="/statusupdate/assistant/update", method=RequestMethod.POST)
	public String statusUpdateAssistant(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		boolean updated = false;
		String page = "motion/error";
		StringBuffer success = new StringBuffer();
		
		try{
			String[] selectedItems = request.getParameterValues("items[]");
			String strDecisionStatus = request.getParameter("decisionStatus");
			String strStatus = request.getParameter("status");
			String strDate = request.getParameter("discussionDate");
			
			if(selectedItems != null && selectedItems.length > 0
					&& strDecisionStatus != null && !strDecisionStatus.isEmpty()
					&& strStatus != null && !strStatus.isEmpty()) {
				/**** As It Is Condition ****/
				if(!strStatus.equals("-")) {
					for(String i : selectedItems) {
						Long id = Long.parseLong(i);
						StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class, id);
						Status status = Status.findById(Status.class, new Long(strDecisionStatus));
						if(status.getType().equals(ApplicationConstants.STANDALONE_PROCESSED_ANSWER_RECEIVED)){
							if(strDate!= null && !strDate.isEmpty()){
								Date AnswerReceivedDateDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								motion.setAnswerReceivedDate(AnswerReceivedDateDate);
							}
							motion.setRecommendationStatus(status);
						}else{
							if(strDate!= null && !strDate.isEmpty()){
								Date discussionDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								if(status.getType().equals(ApplicationConstants.STANDALONEED)) {
									motion.setDiscussionDate(discussionDate);
									motion.setDiscussionStatus(status);
								} else {
									//motion.setAnsweringDate(discussionDate);
									motion.setRecommendationStatus(status);
								}
							}
						}						
						
						motion.simpleMerge();
						updated = true;
						success.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale())+",");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			updated = false;
		}
		
		if(updated){
			this.getStatusUpdateStandalonemotion(model, request, locale.toString());
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "motion/statusupdateview";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	

}
