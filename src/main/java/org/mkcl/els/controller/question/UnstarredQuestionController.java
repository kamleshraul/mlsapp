package org.mkcl.els.controller.question;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
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
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.YaadiDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

class UnstarredQuestionController {

	public static void populateModule(final ModelMap model, 
			final HttpServletRequest request,
			final String locale, 
			final AuthUser currentUser) throws ELSException {
		// Populate locale
		model.addAttribute("moduleLocale", locale);
		
		/**** Populate filters for the grid ****/
		// Populate Device types
		List<DeviceType> deviceTypes = QuestionController.getQuestionDeviceTypes(locale);
		model.addAttribute("questionTypes", deviceTypes);
		
		// Populate default Device type
		DeviceType deviceType = QuestionController.getDeviceType(request, locale);
		model.addAttribute("questionType", deviceType.getId());
		model.addAttribute("questionTypeType", deviceType.getType());	
		
		// Populate original devicetypes
		List<DeviceType> originalDeviceTypes = DeviceType.findOriginalDeviceTypesForGivenDeviceType(deviceType);
		if(originalDeviceTypes!=null && !originalDeviceTypes.isEmpty()) {
			model.addAttribute("originalDeviceTypes", originalDeviceTypes);
		} else {
			model.addAttribute("errorcode", "nodevicetypesfound");
		}
		
		// Populate House types configured for the current user
		List<HouseType> houseTypes = QuestionController.getHouseTypes(currentUser, locale);
		model.addAttribute("houseTypes", houseTypes);
		
		// Populate default House type
		HouseType houseType = QuestionController.getHouseType(currentUser, locale);
		model.addAttribute("houseType", houseType.getType());
		
		// Populate Session types
		List<SessionType> sessionTypes = QuestionController.getSessionTypes(locale);
		model.addAttribute("sessionTypes", sessionTypes);
		
		// Populate latest Session type
		Session latestSession = Session.findLatestSession(houseType);
		if(latestSession != null) {
			model.addAttribute("sessionType", latestSession.getType().getId());
		}
		else {
			model.addAttribute("errorcode", "nosessionentriesfound");
		}
		
		// Populate latest Session year
		Integer latestYear = new GregorianCalendar().get(Calendar.YEAR);
		if(latestSession != null) {
			latestYear = latestSession.getYear();
		}
		model.addAttribute("sessionYear", latestYear);
		
		// Populate Session years
		List<Integer> sessionYears = QuestionController.getSessionYears(latestYear);
		model.addAttribute("years", sessionYears);
		
		// Populate User group & User Group type
		/**
		 * Rules:
		 * a. Any user can have only one user group per device type.
		 * b. Any user can have multiple user groups limited to one user group per device type.
		 * c. Custom parameter "QIS_ALLOWED_USERGROUPTYPES" control which user groups can access QIS.
		 * d. Custom parameter "QIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR" controls which user group can see 
		 * Sub department filter.
		 * e. Custom parameter "QUESTION_GRID_STATUS_ALLOWED_USERGROUPTYPE" controls which status will 
		 * be seen in status filter
		 * f. Custom parameter "QUESTION_GRID_STATUS_ALLOWED_BY_DEFAULT" controls which status 
		 * will be seen by default if above filter is not set.
		 */
		UserGroup userGroup = null;
		UserGroupType userGroupType = null;
		List<UserGroup> userGroups = currentUser.getUserGroups();
		if(userGroups != null && ! userGroups.isEmpty()) {
			CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "QIS_ALLOWED_USERGROUPTYPES", "");
			if(cp != null) {
				List<UserGroupType> configuredUserGroupTypes = 
						QuestionController.delimitedStringToUGTList(cp.getValue(), ",", locale);
				
				userGroup = QuestionController.getUserGroup(userGroups, configuredUserGroupTypes, latestSession, locale);
				userGroupType = userGroup.getUserGroupType();
				
				model.addAttribute("usergroup", userGroup.getId());
				model.addAttribute("usergroupType", userGroupType.getType());
			}
			else {
				throw new ELSException("UnstarredQuestionController.populateModule/4", 
						"QIS_ALLOWED_USERGROUPTYPES key is not set as CustomParameter");
			}
		}
		if(userGroup == null || userGroupType == null) {
//			throw new ELSException("StarredQuestionController.populateModule/4", 
//			"User group or User group type is not set for Username: " + currentUser.getUsername());
	
			model.addAttribute("errorcode","current_user_has_no_usergroups");
		}
		
		// Populate Sub Departments configured for this User's user group type
		Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
		CustomParameter subDepartmentFilterAllowedFor = 
				CustomParameter.findByName(CustomParameter.class, "QIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
		if(subDepartmentFilterAllowedFor != null) {
			List<UserGroupType> ugtConfiguredForSubdepartments = 
					QuestionController.delimitedStringToUGTList(
							subDepartmentFilterAllowedFor.getValue(), ",", locale);
			boolean isUGTConfiguredForSubdepartments = 
					QuestionController.isUserGroupTypeExists(ugtConfiguredForSubdepartments, userGroupType);
			if(isUGTConfiguredForSubdepartments) {
				String subDepartmentParam = parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale);
				if(subDepartmentParam != null && ! subDepartmentParam.equals("")) {
					List<SubDepartment> subDepartments =
							QuestionController.getSubDepartments(subDepartmentParam, "##", locale);
					model.addAttribute("subDepartments", subDepartments);
				}
				else {
					throw new ELSException("UnstarredQuestionController.populateModule/4", 
							"SUBDEPARTMENT parameter is not set for Username: " + currentUser.getUsername());
				}
			}
		}
		else {
			throw new ELSException("UnstarredQuestionController.populateModule/4", 
					"QIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR key is not set as CustomParameter");
		}
		
		// Populate Statuses configured for this User's user group type
		/**
		 * Rules:
		 * Search for the Custom parameters in following order in order to get the statuses configured
		 * for this User's user group type
		 * a. CustomParameter "QUESTION_GRID_STATUS_ALLOWED_" + deviceTypeType + userGroupTypeType
		 * b. CustomParameter "QUESTION_GRID_STATUS_ALLOWED_BY_DEFAULT" + deviceTypeType
		 */
		String strAllowedStatus = null;
		CustomParameter allowedStatusParam = 
				CustomParameter.findByName(CustomParameter.class, 
						"QUESTION_GRID_STATUS_ALLOWED_" + deviceType.getType().toUpperCase() + "_" + 
								userGroupType.getType().toUpperCase(), "");
		if(allowedStatusParam != null) {
			strAllowedStatus = allowedStatusParam.getValue();
		}
		else {
			CustomParameter defaultAllowedStatusParam =
					CustomParameter.findByName(CustomParameter.class,
							"QUESTION_GRID_STATUS_ALLOWED_BY_DEFAULT" + deviceType.getType().toUpperCase(), "");
			if(defaultAllowedStatusParam != null) {
				strAllowedStatus = defaultAllowedStatusParam.getValue();
			}
			else {
				throw new ELSException("UnstarredQuestionController.populateModule/4", 
						"QUESTION_GRID_STATUS_ALLOWED_BY_DEFAULT + deviceTypeType " +
						"key is not set as CustomParameter");
			}
		}
		
		if(strAllowedStatus != null) {
			List<Status> statuses = Status.findStatusContainedIn(strAllowedStatus, locale);
			model.addAttribute("status", statuses);
		}
		
		// Populate Roles
		/**
		 * Rules:
		 * a. QIS roles starts with QIS_, MEMBER_
		 * b. Any user will have single role per device type
		 * c. Any user can have multiple roles limited to one role per device type
		 */
		Set<Role> roles = currentUser.getRoles();
		for(Role i : roles) {
			if(i.getType().startsWith("MEMBER_")) {
				model.addAttribute("role", i.getType());
				break;
			}
			else if(i.getType().startsWith("QIS_")) {
				model.addAttribute("role", i.getType());
				break;
			}
		}
		
		// Populate Group parameters (ugparams)
		/**
		 * Rules:
		 * a. ugparam controls what data will be visible in grid
		 * b. Member will see data created by them
		 * c. Typists will see data created off line
		 * d. Others will see data according to the groups allowed
		 */
		String strGroups = currentUser.getGroupsAllowed();
		if(strGroups != null && ! strGroups.isEmpty()) {
			List<Integer> groupNumbers = QuestionController.delimitedStringToIntegerList(strGroups, ",");
			List<Group> groups = new ArrayList<Group>();
			for(Integer groupNumber : groupNumbers) {
				Group group = Group.findByNumberHouseTypeSessionTypeYear(groupNumber,  
						houseType, latestSession.getType(), latestYear);
				groups.add(group);
			}
			model.addAttribute("groups", groups);
			model.addAttribute("ugparam", groups.get(0).getId());
		}
		else {
			model.addAttribute("ugparam", currentUser.getActualUsername());
		}
		
		/****Session Parameter for Mode****/
		String mode = latestSession.getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
		if(mode !=null && !mode.isEmpty()){
			model.addAttribute("processMode",mode);
		}
	}
	
	public static String modifyURLPattern(final String urlPattern,
			final HttpServletRequest request,
			final ModelMap model,
			final AuthUser authUser, 
			final String locale) throws ELSException {
		String newUrlPattern = urlPattern;
		/**** Controlling Grids Starts ****/
		 Role role = QuestionController.getRole(request, locale);
		 if(role != null){
			 String houseType = request.getParameter("houseType");
			 CustomParameter memberGridAllowedFor = 
						CustomParameter.findByName(CustomParameter.class,"QIS_MEMBERGRID_ALLOWED_FOR", "");
			 if(memberGridAllowedFor != null){
				 List<Role> configuredMemberGridAllowedForRoles = 
						 QuestionController.delimitedStringToRoleList(memberGridAllowedFor.getValue(), ",", locale);
				 boolean isRoleConfiguredForMemberGrid = 
							QuestionController.isRoleExists(configuredMemberGridAllowedForRoles, role);
				 if(isRoleConfiguredForMemberGrid){
					   newUrlPattern=urlPattern+"?usergroup=member&houseType="+houseType;
					   return newUrlPattern;
				 }
			 }
			 
			 CustomParameter typistGridAllowedFor = 
						CustomParameter.findByName(CustomParameter.class,"QIS_TYPISTGRID_ALLOWED_FOR", "");
			 if(typistGridAllowedFor != null){
				 List<Role> configuredMemberGridAllowedForRoles = 
						QuestionController.delimitedStringToRoleList(typistGridAllowedFor.getValue(), ",", locale);
				 boolean isRoleConfiguredForMemberGrid = 
						QuestionController.isRoleExists(configuredMemberGridAllowedForRoles, role);
				 if(isRoleConfiguredForMemberGrid){
					 newUrlPattern=urlPattern+"?usergroup=typist&houseType="+houseType;
					 return newUrlPattern;
				 }
			 }
			 
			 CustomParameter assistantGridAllowedFor = 
						CustomParameter.findByName(CustomParameter.class,"QIS_ASSISTANTGRID_ALLOWED_FOR", "");
			 if(assistantGridAllowedFor != null){
				 List<Role> configuredMemberGridAllowedForRoles = 
					QuestionController.delimitedStringToRoleList(assistantGridAllowedFor.getValue(), ",", locale);
				 boolean isRoleConfiguredForMemberGrid = 
					QuestionController.isRoleExists(configuredMemberGridAllowedForRoles, role);
				 if(isRoleConfiguredForMemberGrid){
					 newUrlPattern=urlPattern+"?usergroup=assistant&houseType="+houseType;
					 return newUrlPattern;
				 }
			 }else{
				 throw new ELSException("UnstarredQuestionController.modifyURLPattern/4", 
							"QIS_GRID_ALLOWED_FOR key is not set as CustomParameter");
			 }
		 }
		 else{
			 throw new ELSException("UnstarredQuestionController.modifyURLPattern/4", 
						"Role is not set in request"); 
		 }

		return newUrlPattern;
		
	}
	
	public static String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, 
			final ModelMap model, 
			final AuthUser authUser, 
			final String locale) throws ELSException {
		/**** New Operations Allowed For Starts ****/
		Role role = QuestionController.getRole(request, locale);
		if(role != null){
			CustomParameter newOperationAllowedTo = 
					CustomParameter.findByName(CustomParameter.class,"QIS_NEW_OPERATION_ALLOWED_TO","");
			if(newOperationAllowedTo != null){
				List<Role> configuredNewOperationAllowedForRoles = 
						 QuestionController.delimitedStringToRoleList(newOperationAllowedTo.getValue(), ",", locale);
				 boolean isRoleConfiguredForMemberGrid = 
							QuestionController.isRoleExists(configuredNewOperationAllowedForRoles, role);
				if(isRoleConfiguredForMemberGrid){
					return servletPath;
				}			
			}
		}
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
		
	}

	public static void populateNew(final ModelMap model,
			final Question domain,
			final String locale,
			final AuthUser authUser,
			final HttpServletRequest request) throws ELSException, ParseException {
		//set Locale
		domain.setLocale(locale);
		
		//Populate HouseType
		HouseType houseType = QuestionController.getHouseType(request, locale);
		model.addAttribute("formattedHouseType", houseType.getName());
		model.addAttribute("houseTypeType", houseType.getType());
		model.addAttribute("houseType", houseType.getId());
		
		//populate Session Year
		Integer sessionYear = QuestionController.stringToIntegerYear(request, locale);
		model.addAttribute("sessionYear", sessionYear);
		model.addAttribute("formattedSessionYear",
				FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
				
		//populate sessionType
		SessionType sessionType = QuestionController.getSessionType(request, locale);
		model.addAttribute("formattedSessionType",sessionType.getSessionType());
		model.addAttribute("sessionType",sessionType.getId());
		
		//populate DeviceType
		DeviceType questionType = QuestionController.getDeviceTypeById(request, locale);
		model.addAttribute("formattedQuestionType", questionType.getName());
		model.addAttribute("questionType", questionType.getId());
		model.addAttribute("deviceType", questionType.getId());
		model.addAttribute("selectedQuestionType", questionType.getType());
		
		//Populate Session
		Session selectedSession = 
				Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
		if(selectedSession != null){
			model.addAttribute("session", selectedSession.getId());
		}else{
			throw new ELSException("UnstarredQuestionController.populatenew/5", 
					"session_isnull");
		}
		
		//Populate Role
		Role role = QuestionController.getRole(request, locale);
		String strRole = null;
		if(role != null){
			strRole = role.getType();
			model.addAttribute("role", strRole);
		}else{
			strRole = (String) request.getSession().getAttribute("role");
			if(strRole != null && !strRole.isEmpty()) {
				model.addAttribute("role", role);
				request.getSession().removeAttribute("role");
			}else{
				throw new ELSException("UnstarredQuestionController.populatenew/5", 
						"Role is Not set");
			}
		}
		
		//Populate UserGroupType
		String usergroupType = request.getParameter("usergroupType");
		if(usergroupType != null && !usergroupType.isEmpty()){
			model.addAttribute("usergroupType", usergroupType);
		}else{
			usergroupType = (String) request.getSession().getAttribute("usergroupType");
			if(usergroupType != null && !usergroupType.isEmpty()){
				model.addAttribute("usergroupType", usergroupType);
				request.getSession().removeAttribute("usergroupType");
			}
			else{
				throw new ELSException("UnstarredQuestionController.populatenew/5", 
						"UserGroupType is Not set");
			}
		}
		
		//Populate usergroup
		String usergroup = request.getParameter("usergroup");
		if(usergroup != null && !usergroup.isEmpty()){
			model.addAttribute("usergroup",usergroup);
		}else{
			usergroup = (String) request.getSession().getAttribute("usergroup");
			if(usergroup != null && !usergroup.isEmpty()){
				model.addAttribute("usergroup", usergroup);
				request.getSession().removeAttribute("usergroup");
			}
			else{
				throw new ELSException("UnstarredQuestionController.populatenew/5", 
						"UserGroup is Not set");
			}
		}
		
		//Populate Primary Member
		String primaryMember = null;
		if(strRole.startsWith("MEMBER")) {
			Member member = QuestionController.populateMember(model, authUser, locale);
			if(member == null){
				throw new ELSException("UnstarredQuestionController.populatenew/5", 
						"The Current User is Not member");
			}
			primaryMember = member.getFullname();
			Constituency constituency = Member.findConstituency(member, new Date());
			if(constituency != null){
				model.addAttribute("constituency", constituency.getDisplayName());
			}
		}
		
		//Populate Ministries
		List<Ministry> ministries = Ministry.
				findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
		model.addAttribute("ministries",ministries);
	}

	public static void preValidateCreate(Question domain, BindingResult result,
			HttpServletRequest request, AuthUser authUser) throws ELSException {
		String locale = domain.getLocale();
		Role role = QuestionController.getRole(request, locale);
		List<SupportingMember> supportingMembers = 
				QuestionController.getSupportingMembers(request, domain, role, locale);
		domain.setSupportingMembers(supportingMembers);
		
	}
	
	public static void customValidateCreate(final Question domain,
			final BindingResult result,
			final HttpServletRequest request,
			final AuthUser authUser) {
		String role=request.getParameter("role");
		/**** Validation Starts ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
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
		if(domain.getQuestionText().isEmpty()){
			result.rejectValue("questionText","QuestionTextEmpty");
		}
		
		if(role.equals("QIS_TYPIST")){
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "QIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");
						//check for duplicate questions
					}
					Boolean flag=Question.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
					}
				}
			}
		}
		String operation=request.getParameter("operation");
		if(operation!=null && ! operation.isEmpty()){
			if(operation.equals("approval") && 
					(role.equals("MEMBER_LOWERHOUSE") || role.equals("MEMBER_UPPERHOUSE"))) {
				if(domain.getSupportingMembers()==null && domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers","SupportingMembersEmpty");
				} 
			}
			else if(operation.equals("submit") && (role.equals("MEMBER_LOWERHOUSE") 
					|| role.equals("MEMBER_UPPERHOUSE") || role.equals("QIS_TYPIST"))){
				if(domain.getMinistry()==null){
					result.rejectValue("ministry","MinistryEmpty");
				}
				if(domain.getSubDepartment()==null){
					result.rejectValue("subDepartment","SubDepartmentEmpty");
				}
				if(domain.getGroup()==null){
					result.rejectValue("group","GroupEmpty");
				}
				//submission date limit validations (configurable through custom parameters)
				if(domain.getSession()!=null && domain.getType()!=null) {
					//submission start date limit validation
					CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
					if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
						String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
						if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
							String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
							for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
								if(dt.trim().equals(domain.getType().getType().trim())) {
									String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
									if(submissionStartLimitDateStr!=null && !submissionStartLimitDateStr.isEmpty()) {
										Date submissionStartLimitDate = FormaterUtil.formatStringToDate(submissionStartLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
										if(submissionStartLimitDate!=null
												&& submissionStartLimitDate.after(new Date())) {
											submissionStartLimitDateStr = FormaterUtil.formatDateToString(submissionStartLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Question cannot be submitted before " + submissionStartLimitDateStr);
										}
									}
									break;
								}
							}								
						}
					}
					//submission end date limit validation
					CustomParameter deviceTypesHavingSubmissionEndDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_END_DATE_VALIDATION, "");
					if(deviceTypesHavingSubmissionEndDateValidationCP!=null) {
						String deviceTypesHavingSubmissionEndDateValidationValue = deviceTypesHavingSubmissionEndDateValidationCP.getValue();
						if(deviceTypesHavingSubmissionEndDateValidationValue!=null) {
							String[] deviceTypesHavingSubmissionEndDateValidation = deviceTypesHavingSubmissionEndDateValidationValue.split(",");
							for(String dt: deviceTypesHavingSubmissionEndDateValidation) {
								if(dt.trim().equals(domain.getType().getType().trim())) {
									String submissionEndLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
									if(submissionEndLimitDateStr!=null && !submissionEndLimitDateStr.isEmpty()) {
										Date submissionEndLimitDate = FormaterUtil.formatStringToDate(submissionEndLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
										if(submissionEndLimitDate!=null
												&& submissionEndLimitDate.before(new Date())) {
											submissionEndLimitDateStr = FormaterUtil.formatDateToString(submissionEndLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Question cannot be submitted after " + submissionEndLimitDateStr);
										}
									}
									break;
								}
							}								
						}
					}
				}
			}
		}
	}

	

	public static void populateCreateIfErrors(final ModelMap model,
			final Question domain,
			final HttpServletRequest request,
			final AuthUser authUser) throws ELSException {
		request.getSession().setAttribute("questionType", request.getParameter("questionType"));
		//Populate Supporting Member Names
		
		String supportingMemberNames = QuestionController.getDelimitedSupportingMembers(model, domain, null);
		model.addAttribute("supportingMembersName", supportingMemberNames);
		
		//Populate Supporting Members 
		List<SupportingMember> suppMembers = domain.getSupportingMembers();
		List<Member> supportingMembers = new ArrayList<Member>();
		for(SupportingMember sm : suppMembers){
			Member member = sm.getMember();
			supportingMembers.add(member);
		}
		model.addAttribute("supportingMembers", supportingMembers);
		
		//Populate PrimaryMemberName + supportingMemberNames
		String memberNames = domain.getPrimaryMember().getFullname() + "," + supportingMemberNames;
		model.addAttribute("memberNames",memberNames);
		
		//Populate HouseType
		HouseType houseType = QuestionController.getHouseType(request, domain.getLocale());
		model.addAttribute("formattedHouseType", houseType.getName());
		model.addAttribute("houseTypeType", houseType.getType());
		model.addAttribute("houseType", houseType.getId());
		
		//populate DeviceType
		DeviceType questionType = domain.getType();
		model.addAttribute("formattedQuestionType", questionType.getName());
		model.addAttribute("questionType", questionType.getId());
		model.addAttribute("deviceType", questionType.getId());
		model.addAttribute("selectedQuestionType", questionType.getType());
		
		//populate Session Year
		Integer sessionYear = QuestionController.stringToIntegerYear(request, domain.getLocale());

		//populate sessionType
		SessionType sessionType = QuestionController.getSessionType(request, domain.getLocale());
	
		//Populate Session
		Session selectedSession = 
				Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
		if(selectedSession != null){
			model.addAttribute("session", selectedSession.getId());
		}else{
			throw new ELSException("UnstarredQuestionController.populatenew/5", 
					"session_isnull");
		}
		
				
		//Populate Ministries

		List<Ministry> ministries = Ministry.
				findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, domain.getLocale());
		model.addAttribute("ministries",ministries);
			
		//Populate Ministry
		Ministry ministry = domain.getMinistry();
		if(ministry != null){
			model.addAttribute("ministrySelected", ministry.getId());
		}
				
		//Populate Group
		Group group = domain.getGroup();
		if(group != null) {
			model.addAttribute("formattedGroup",
					FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(group.getNumber()));
			model.addAttribute("group",group.getId());
		}
		
		//Populate SubDepartments
		if(ministry != null) {
			List<SubDepartment> subDepartments = MemberMinister.
					findAssignedSubDepartments(ministry, selectedSession.getEndDate(), domain.getLocale());
			model.addAttribute("subDepartments",subDepartments);
		}
		
		//populate subdepartment
		SubDepartment subDepartment = domain.getSubDepartment();
		if(subDepartment != null) {
			model.addAttribute("subDepartmentSelected",subDepartment.getId());
		}
		
		//populate Answering Dates
		if(group != null){
			List<QuestionDates> answeringDates = group.getQuestionDates();
			List<MasterVO> masterVOs = new ArrayList<MasterVO>();
			for(QuestionDates i:answeringDates){
				MasterVO masterVO = new MasterVO(i.getId(),
						FormaterUtil.getDateFormatter(domain.getLocale()).format(i.getAnsweringDate()));
				masterVOs.add(masterVO);
			}
			model.addAttribute("answeringDates",masterVOs);
		}
		
		//Populate Answering Date
		QuestionDates questionDate = domain.getAnsweringDate();
		if(questionDate != null){
			model.addAttribute("answeringDate",questionDate.getId());
			model.addAttribute("formattedAnsweringDate",FormaterUtil.
					getDateFormatter(domain.getLocale()).format(questionDate.getAnsweringDate()));
			model.addAttribute("answeringDateSelected",questionDate.getId());
		}
		
		
		//Populate Priorities
		CustomParameter customParameter = CustomParameter.
				findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter != null) {
			List<MasterVO> priorities = new ArrayList<MasterVO>();
			Integer priority = Integer.parseInt(customParameter.getValue());
			for(int i = 1;i <= priority;i++){
				String localizedPriority = FormaterUtil.
						getNumberFormatterNoGrouping(domain.getLocale()).format(i);
				priorities.add(new MasterVO(i,localizedPriority));
			}
			model.addAttribute("priorities",priorities);
		}else{
			throw new ELSException("UnstarredQuestionController.populateNew/5", 
					"highestquestionprioritynotset");
		}
		
	}

	public static void populateCreateIfNoErrors(final Question domain,
			final ModelMap model, 
			final HttpServletRequest request,
			final AuthUser authUser) {
		String operation = request.getParameter("operation");
		//set Edited As
		UserGroupType userGroupType = QuestionController.getUserGroupType(request,domain.getLocale());
		if(userGroupType !=null ) {
			domain.setEditedAs(userGroupType.getName());
		}
		// Check for the operation and the required fields
		if(domain.getHouseType()!=  null && domain.getSession()!= null &&  domain.getType() != null 
			&& domain.getPrimaryMember() != null && domain.getMinistry() != null && domain.getGroup() != null 
			&& (!domain.getSubject().isEmpty()) && (!domain.getQuestionText().isEmpty())) {
			if(operation != null && !operation.isEmpty() 
				&& operation.trim().equals("submit")){

				//set submission Date
				if(domain.getSubmissionDate() == null) {
					domain.setSubmissionDate(new Date());
				}
				
				//set Supporting member			
				if(domain.getSupportingMembers() != null && !domain.getSupportingMembers().isEmpty()) {
					List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
					for(SupportingMember i:domain.getSupportingMembers()){
						String decisionStatusType = i.getDecisionStatus().getType().trim();
						if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
							supportingMembers.add(i);
						}
					}
					domain.setSupportingMembers(supportingMembers);
				}
			
				// set internalstatus, recommendation status, status
				Status newstatus = Status.
						findByType(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT, domain.getLocale());
				domain.setStatus(newstatus);
				domain.setInternalStatus(newstatus);
				domain.setRecommendationStatus(newstatus);
				
			}
			else{
				//set internalstatus, recommendation status, status
				Status status = Status.
						findByType(ApplicationConstants.QUESTION_UNSTARRED_COMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}else{
			//set internalstatus, recommendation status, status
			Status status = Status.
					findByType(ApplicationConstants.QUESTION_UNSTARRED_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}

		//set Creation Date, created by, edited on , edited by
		domain.setCreationDate(new Date());
		domain.setCreatedBy(authUser.getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(authUser.getActualUsername());
		
	}

	public static void populateAfterCreate(final Question domain,
			final ModelMap model,
			final HttpServletRequest request,
			final AuthUser authUser,
			final IProcessService processService) throws ELSException {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		request.getSession().setAttribute("questionType", request.getParameter("questionType"));
		/**** Parameters which will be read from request in populate new Ends ****/

		/**** Supporting Member Workflow Starts ****/
		String operation = request.getParameter("operation");
		if(operation != null && !operation.isEmpty()){
			if(operation.equals("approval")){
				ProcessDefinition processDefinition = processService.
						findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
				Map<String,String> properties = new HashMap<String, String>();
				properties.put("pv_deviceId",String.valueOf(domain.getId()));
				properties.put("pv_deviceTypeId",domain.getType().getType());
				ProcessInstance processInstance = processService.
						createProcessInstance(processDefinition, properties);
				List<Task> tasks = processService.getCurrentTasks(processInstance);
				List<WorkflowDetails> workflowDetails = new ArrayList<WorkflowDetails>();
				
				workflowDetails = WorkflowDetails.
						create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"0");
				Question question = Question.
						findById(Question.class,domain.getId());
				List<SupportingMember> supportingMembers = question.getSupportingMembers();
				Status status = Status.
						findByType(ApplicationConstants.SUPPORTING_MEMBER_PENDING, domain.getLocale());
				for(SupportingMember i:supportingMembers){
					String decisionStatus = i.getDecisionStatus().getType();
					if(decisionStatus.equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
						i.setDecisionStatus(status);
						i.setRequestReceivedOn(new Date());
						i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
						User user = User.
								findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),
										i.getMember().getLastName(),i.getMember().getBirthDate());
						Credential credential = user.getCredential();							
						for(WorkflowDetails j:workflowDetails){
							if(j.getAssignee().equals(credential.getUsername())){
								i.setWorkflowDetailsId(String.valueOf(j.getId()));
								break;
							}
						}							
						i.merge();
					}
				}
			}
		}
	}
	
	

	public static void populateEdit(final Question domain,
			final ModelMap model,
			final HttpServletRequest request,
			final AuthUser authUser) throws ELSException {
		//locale
		String locale=domain.getLocale();
		
		//populate houseType
		HouseType houseType=domain.getHouseType();
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseTypeType", houseType.getType());
		model.addAttribute("houseType",houseType.getId());

		//populate session
		Session selectedSession=domain.getSession();
		if(selectedSession == null){
			throw new ELSException("UnstarredQuestionController.populateEdit/4", 
					"highestquestionprioritynotset");
		}
		model.addAttribute("session",selectedSession.getId());
		
		// Populate Session Year
		Integer sessionYear=0;
		sessionYear=selectedSession.getYear();
		model.addAttribute("formattedSessionYear",FormaterUtil.
				getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear",sessionYear);
		
		// Populate SessionType
		SessionType  sessionType=selectedSession.getType();
		model.addAttribute("formattedSessionType",sessionType.getSessionType());
		model.addAttribute("sessionType",sessionType.getId());
		 
		//populate devicetype
		DeviceType deviceType=domain.getType();
		model.addAttribute("formattedQuestionType",deviceType.getName());
		model.addAttribute("questionType",deviceType.getId());
		model.addAttribute("deviceType", deviceType.getId());
		model.addAttribute("selectedQuestionType",deviceType.getType());
		if(domain.getOriginalType()!=null) {
			model.addAttribute("originalType",domain.getOriginalType().getId());
		}
		 
		//populate primary member And constituency
		Member member = domain.getPrimaryMember();
		if(member != null){
			String primaryMember=member.getFullname();
			model.addAttribute("primaryMember",member.getId());
			model.addAttribute("formattedPrimaryMember",primaryMember);
			Constituency constituency = Member.findConstituency(member, new Date());
			if(constituency != null){
				model.addAttribute("constituency", constituency.getDisplayName());
			}
		}

		//Populate Priorities
		CustomParameter customParameter = CustomParameter.
				findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter != null) {
			List<MasterVO> priorities = new ArrayList<MasterVO>();
			Integer priority = Integer.parseInt(customParameter.getValue());
			for(int i = 1;i <= priority;i++){
				String localizedPriority = FormaterUtil.getNumberFormatterNoGrouping(locale).format(i);
				priorities.add(new MasterVO(i,localizedPriority));
			}
			model.addAttribute("priorities",priorities);
		}else{
			throw new ELSException("UnstarredQuestionController.populateNew/5", 
					"highestquestionprioritynotset");
		}
		
		//populate selected Priority
		if(domain.getPriority()!=null){
			model.addAttribute("priority",domain.getPriority());
			model.addAttribute("formattedPriority",FormaterUtil.
					getNumberFormatterNoGrouping(locale).format(domain.getPriority()));
		}
		/**** Priorities Ends  ****/

		//Populate Ministries

		List<Ministry> ministries = Ministry.
				findMinistriesAssignedToGroups(houseType, selectedSession.getYear(), 
						selectedSession.getType(), locale);
		model.addAttribute("ministries",ministries);
		
		//Populate Ministry
		Ministry ministry = domain.getMinistry();
		if(ministry !=null ){
			model.addAttribute("ministrySelected", ministry.getId());
			model.addAttribute("formattedMinistry",ministry.getName());
		}
		
		//Populate Group
		Group group = domain.getGroup();
		if(group != null) {
			model.addAttribute("formattedGroup",
					FormaterUtil.getNumberFormatterNoGrouping(locale).format(group.getNumber()));
			model.addAttribute("group",group.getId());
		}
		
		//Populate SubDepartments
		if(ministry != null) {
			Date onDate = selectedSession.getEndDate();
			if(onDate.before(new Date())) {
				onDate = new Date();
			}
			List<SubDepartment> subDepartments = 
					MemberMinister.findAssignedSubDepartments(ministry, onDate, locale);
			model.addAttribute("subDepartments",subDepartments);
		}
		
		//populate subdepartment
		SubDepartment subDepartment = domain.getSubDepartment();
		if(subDepartment != null) {
			model.addAttribute("subDepartmentSelected",subDepartment.getId());
		}
		
		//populate Answering Dates
		if(group != null){
			List<QuestionDates> answeringDates = group.getQuestionDates();
			List<MasterVO> masterVOs = new ArrayList<MasterVO>();
			for(QuestionDates i:answeringDates){
				MasterVO masterVO = new MasterVO(i.getId(),
						FormaterUtil.getDateFormatter(locale).format(i.getAnsweringDate()));
				masterVOs.add(masterVO);
			}
			model.addAttribute("answeringDates",masterVOs);
		}
		
		//Populate Answering Date
		QuestionDates questionDate = domain.getAnsweringDate();
		if(questionDate != null){
			model.addAttribute("answeringDate",questionDate.getId());
			model.addAttribute("formattedAnsweringDate",FormaterUtil.
					getDateFormatter(locale).format(questionDate.getAnsweringDate()));
			model.addAttribute("answeringDateSelected",questionDate.getId());
		}
		
	
		// populate Charting Answering Date
		if(domain.getChartAnsweringDate() != null){
			QuestionDates chartAnsweringDate = domain.getChartAnsweringDate();
			model.addAttribute("formattedChartAnsweringDate",FormaterUtil.
					getDateFormatter(locale).format(chartAnsweringDate.getAnsweringDate()));
			model.addAttribute("chartAnsweringDate",chartAnsweringDate.getId());
		}
		
		//Populate SubmissionDate, creationDate, workflowStartedOnDate, taskReceivedOnDate
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){            
			if(domain.getSubmissionDate()!=null){
				model.addAttribute("submissionDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(domain.getCreationDate()!=null){
				model.addAttribute("creationDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
			}
			if(domain.getWorkflowStartedOn()!=null){
				model.addAttribute("workflowStartedOnDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOn()));
			}
			if(domain.getTaskReceivedOn()!=null){
				model.addAttribute("taskReceivedOnDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOn()));
			}
		}
		 
		//Populate formatted Number
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.
					getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		
		//populate role
		Role role = QuestionController.getRole(request, locale);
		String strRole = null;
		if(role != null){
			strRole = role.getType(); 
			model.addAttribute("role",strRole);
		}else{
			strRole=(String) request.getSession().getAttribute("role");
			role = Role.findByType(strRole, locale);
			model.addAttribute("role",strRole);
			request.getSession().removeAttribute("role");
		}
		
		//populate usegroupType
		UserGroupType userGroupType = QuestionController.getUserGroupType(request, locale);
		String usergroupType = null;
		if(userGroupType != null){
			usergroupType = userGroupType.getType();
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType=(String) request.getSession().getAttribute("usergroupType");
			userGroupType = UserGroupType.findByType(usergroupType, locale);
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		
		//populate usergroup
		UserGroup userGroup = QuestionController.getUserGroup(request,locale);
		String strUsergroup = null;
		if(userGroup!=null){
			model.addAttribute("usergroup",userGroup.getId());
		}else{
			strUsergroup=(String) request.getSession().getAttribute("usergroup");
			userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUsergroup));
			model.addAttribute("usergroup",strUsergroup);
			request.getSession().removeAttribute("userGroup");
		}

		//Populate createdby
		model.addAttribute("createdBy",domain.getCreatedBy());
		
		/**** Add Memberside Submission Draft for case of populating just after submission ****/
		if(domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)
				&& !usergroupType.equals(ApplicationConstants.MEMBER)
				&& !usergroupType.equals(ApplicationConstants.TYPIST)
				&& (domain.getDrafts()==null || domain.getDrafts().isEmpty())) {
			domain.addQuestionDraftForMembersideSubmission();
			domain.simpleMerge();
		}
		
		//populate bulkedit
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
		//populate member status name and devicetype
		if(usergroupType !=null && !(usergroupType.isEmpty()) && usergroupType.equals("member")){
			Status memberStatus = domain.findMemberStatus();
			if(memberStatus!=null){				
				model.addAttribute("formattedMemberStatus", memberStatus.getName());
			}
			if(domain.getOriginalType()!=null) {
				model.addAttribute("formattedQuestionType",domain.getOriginalType().getName());
			}
		}
		//populate Statuses
		Status status=domain.getStatus();
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		if(status!=null){
			//populate status
			model.addAttribute("status",status.getId());
			model.addAttribute("memberStatusType",status.getType());
			model.addAttribute("formattedStatus", status.getName());
		}
		
		if(internalStatus!=null){
			//populate internal status
			model.addAttribute("internalStatus",internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			
			
			
			// set End Flag and Level incase of assistant
			if(usergroupType !=null && !(usergroupType.isEmpty()) && usergroupType.equals("assistant")){
				//populate PUT UP OPTIONS
				if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)){
					if(recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
						QuestionController.
						populateInternalStatus(model,recommendationStatus.getType(),usergroupType,locale,deviceType.getType());
					} else {
						QuestionController.
						populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
					}
					if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
						domain.setWorkflowStarted("NO");
					}
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
			}else if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("clerk")){
				if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)){
//					QuestionController.
//					populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
					//set workflow started Flag
					if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
						domain.setWorkflowStarted("NO");
					}
				}
			}
		}
		
		//populate recommendationStatus
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
		}
		

		/**** Referenced Questions Starts ****/
		CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
				findByName(CustomParameter.class, "QIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
		if(clubbedReferencedEntitiesVisibleUserGroups != null){
			List<UserGroupType> userGroupTypes = QuestionController.
					delimitedStringToUGTList(clubbedReferencedEntitiesVisibleUserGroups.getValue(), ",", locale);
			Boolean isUserGroupAllowed = QuestionController.isUserGroupTypeExists(userGroupTypes, userGroupType);
			//populate referencedEntities
			if(isUserGroupAllowed){
				List<Reference> refentities = QuestionController.getReferencedEntityReferences(domain,locale);
				model.addAttribute("referencedQuestions",refentities);
				
				// Populate clubbed entities
				List<Reference> clubEntityReferences = QuestionController.getClubbedEntityReferences(domain, locale);
				model.addAttribute("clubbedQuestions",clubEntityReferences);
				
				// Populate latest revised question text from clubbed questions
				String latestRevisedQuestionTextFromClubbedQuestions = "";
				List<ClubbedEntity> clubbedEntities=Question.findClubbedEntitiesByPosition(domain);
				if(clubbedEntities!=null & !clubbedEntities.isEmpty()){
					ClubbedEntity ce = clubbedEntities.get(0); //first position clubbed question
					latestRevisedQuestionTextFromClubbedQuestions = ce.getQuestion().getRevisedQuestionText();					
				}
				model.addAttribute("latestRevisedQuestionTextFromClubbedQuestions",latestRevisedQuestionTextFromClubbedQuestions);
			}
		}
	
		/*** Populate Immediate Supporting member***/
		//Populate Supporting Member Names
		String supportingMemberNames = QuestionController.getDelimitedSupportingMembers(model, domain, usergroupType);
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
		
		//Populate Parent
		if(domain.getParent()!=null){
			model.addAttribute("formattedParentNumber",FormaterUtil.
					getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
			model.addAttribute("parent",domain.getParent().getId());
		}
		

		/**** Populating Put up options and Actors Starts ****/
		if(domain.getInternalStatus() != null){
			String internalStatusType = domain.getInternalStatus().getType();			
			if(usergroupType!=null && !usergroupType.isEmpty() && usergroupType.equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
						||internalStatusType.equals(ApplicationConstants.QUESTION_PUTUP_REJECTION))){
				List<Reference> actors=WorkflowConfig.
						findQuestionActorsVO(domain, internalStatus, userGroup, 1, locale);
				model.addAttribute("actors",actors);
				if(actors!=null && !actors.isEmpty()){
					String nextActor=actors.get(0).getId();
					String[] actorArr=nextActor.split("#");
					domain.setLevel(actorArr[2]);
					domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
				}
			}	
		}
		
		
		Status tempStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_REJECTION, locale);
		boolean canRemark = false;	
		String errorMessagePossible="";
		try{
			errorMessagePossible = "domain_not_found";
			if (internalStatus.getType().equals(tempStatus.getType())) {
				errorMessagePossible = "questiondraft_not_found_for_remark";
				QuestionDraft qDraft = domain.findPreviousDraft();					
				model.addAttribute("sectionofficer_remark",qDraft.getRemarks());
				canRemark = true;
			}
		}catch(Exception e){
			model.addAttribute("errorcode",errorMessagePossible);
		}
		if(!canRemark){
			model.addAttribute("sectionofficer_remark","");
		}
		
		/**** Answer related Dates ****/
		String allowedDeviceTypesForAnswerRelatedDates = "";
		CustomParameter deviceTypesForAnswerRelatedDates = 
				CustomParameter.findByName(CustomParameter.class, 
						"DEVICETYPES_FOR_ANSWER_RELATED_DATES", locale.toString());
		if(deviceTypesForAnswerRelatedDates != null) {
			allowedDeviceTypesForAnswerRelatedDates = deviceTypesForAnswerRelatedDates.getValue();
		} else {
			allowedDeviceTypesForAnswerRelatedDates = 
					ApplicationConstants.STARRED_QUESTION + ", " + ApplicationConstants.UNSTARRED_QUESTION;
		}
		model.addAttribute("allowedDeviceTypesForAnswerRelatedDates", allowedDeviceTypesForAnswerRelatedDates);
		if(domain.getAnswerRequestedDate() != null) {
			model.addAttribute("formattedAnswerRequestedDate",
					FormaterUtil.formatDateToString(domain.getAnswerRequestedDate(), 
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		if(domain.getAnswerReceivedDate()!=null) {
			model.addAttribute("formattedAnswerReceivedDate", 
					FormaterUtil.formatDateToString(domain.getAnswerReceivedDate(),
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		
		/**** Yaadi related things ****/
		/** populate yaadi laying date **/
		if(domain.getYaadiLayingDate()!=null) {
			model.addAttribute("yaadiLayingDate", 
					FormaterUtil.formatDateToString(domain.getYaadiLayingDate(),
							ApplicationConstants.SERVER_DATEFORMAT, locale));
		}
		/** populate yaadi details text if question is present in yaadi **/
		String yaadiDetailsText = domain.findYaadiDetailsText();
		model.addAttribute("yaadiDetailsText", yaadiDetailsText);	
		/** find if previous session unstarred question is parent and show its details including yaadi if present **/
		Question parentQuestion = domain.getParent();
		if(parentQuestion!=null
				&& parentQuestion.getType().getType().trim().equalsIgnoreCase(ApplicationConstants.UNSTARRED_QUESTION)
				&& domain.getSession().getStartDate().after(parentQuestion.getSession().getStartDate())) {
			String previousSessionUnstarredParentDetailsText = domain.findPreviousSessionUnstarredParentDetailsText();
			model.addAttribute("previousSessionUnstarredParentDetailsText", previousSessionUnstarredParentDetailsText);
		}		
		/** check whether question was removed from unstarred yaadi purposely  **/
		if(domain.getParent()==null 
				&& domain.getStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				&& domain.getAnswer()!=null && !domain.getAnswer().isEmpty() 
				&& !domain.getAnswer().equals("<p></p>") && !domain.getAnswer().equals("<br><p></p>")) {
			Boolean isRemovedFromYaadiDetails = domain.checkWhetherIsRemovedFromYaadiDetails();
			if(isRemovedFromYaadiDetails!=null && isRemovedFromYaadiDetails.equals(true)) {
				model.addAttribute("isRemovedFromYaadiDetails", true);
			}
		}	
	}
	
	public static String modifyEditUrlPattern(final String editUrlPattern,
			final HttpServletRequest request,
			final ModelMap model,
			final AuthUser authUser,
			final String locale) {
		//Edit Parameter
		String edit = request.getParameter("edit");
		if(edit != null){
			if(!Boolean.parseBoolean(edit)){
				return editUrlPattern.replace("edit", "editreadonly");
			}
		}

		//Print Parameter
		String editPrint = request.getParameter("editPrint");
		if(editPrint != null){
			if(Boolean.parseBoolean(editPrint)){
				return editUrlPattern.replace("edit", "editprint");
			}
		}
		
		CustomParameter editPage = CustomParameter.
				findByName(CustomParameter.class, "QIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.
				findByName(CustomParameter.class, "QIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles=authUser.getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return editUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return editUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("QIS_")) {
				return editUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "questions/error";
	}

	public static void preValidateUpdate(final Question domain,
			final BindingResult result,
			final HttpServletRequest request,
			final AuthUser authUser) throws ELSException {
		String locale = domain.getLocale();
		Role role = QuestionController.getRole(request, locale);
		List<SupportingMember> supportingMembers = 
				QuestionController.getSupportingMembers(request, domain, role, locale);
		domain.setSupportingMembers(supportingMembers);
	}

	public static void customValidateUpdate(final Question domain,
			final BindingResult result, 
			final HttpServletRequest request,
			final AuthUser authUser) {
		String role = request.getParameter("role");
		
		//check version mismatch
		if(domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		// Empty check for HouseType
		if(domain.getHouseType() == null){
			result.rejectValue("houseType", "HousetypeEmpty");
		}
		//Empty check for DeviceType
		if(domain.getType() == null){
			result.rejectValue("type", "QuestionTypeEmpty");
		}
		//Empty check for session
		if(domain.getSession() == null){
			result.rejectValue("session", "SessionEmpty");
		}
		//Empty check for PrimaryMember
		if(domain.getPrimaryMember() == null){
			result.rejectValue("primaryMember", "PrimaryMemberEmpty");
		}
		//Empty check for Subject
		if(domain.getSubject().isEmpty()){
			result.rejectValue("subject", "SubjectEmpty");
		}
		//Empty check for Question text
		if(domain.getQuestionText().isEmpty()){
			result.rejectValue("questionText", "QuestionTextEmpty");
		}
		String operation = request.getParameter("operation");
		if(operation != null && !operation.isEmpty()){
			//Operation is supporting member approval
			if(operation.equals("approval")){
				// Empty check the supporting members
				if(domain.getSupportingMembers() == null || domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers", "SupportingMembersEmpty");
				} else {
					//if the request for Supporting Member is already send then raise the error
					int count=0;
					for(SupportingMember i : domain.getSupportingMembers()){
						if(i.getDecisionStatus().getType().
								equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							count++;
						}
					}
					if(count == 0){
						result.rejectValue("supportingMembers", "SupportingMembersRequestAlreadySent");
					}
				}
			// Operation is submit or startworkflow	
			}else if(operation.equals("submit")){
				if(role.equals("QIS_TYPIST")){
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "QIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
					if(customParameter != null){
						String value = customParameter.getValue();
						if(!value.equals("yes")){
							if(domain.getNumber()==null){
								result.rejectValue("number","NumberEmpty");
								//check for duplicate questions
							}
							Boolean flag=Question.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
							if(flag){
								result.rejectValue("number", "NonUnique","Duplicate Parameter");
							}
						}
					}
				}
				// Empty check for Ministry
				if(domain.getMinistry() == null){
					result.rejectValue("ministry", "MinistryEmpty");
				}
				// Empty check for Subdepartment
				if(domain.getSubDepartment() == null){
					result.rejectValue("subDepartment", "SubDepartmentEmpty");
				}
				// Empty check for Group
				if(domain.getGroup() == null){
					result.rejectValue("group", "GroupEmpty");
				}
				//submission date limit validations (configurable through custom parameters)
				if(domain.getSession()!=null && domain.getType()!=null) {
					//submission start date limit validation
					CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
					if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
						String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
						if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
							String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
							for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
								if(dt.trim().equals(domain.getType().getType().trim())) {
									String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
									if(submissionStartLimitDateStr!=null && !submissionStartLimitDateStr.isEmpty()) {
										Date submissionStartLimitDate = FormaterUtil.formatStringToDate(submissionStartLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
										if(submissionStartLimitDate!=null
												&& submissionStartLimitDate.after(new Date())) {
											submissionStartLimitDateStr = FormaterUtil.formatDateToString(submissionStartLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Question cannot be submitted before " + submissionStartLimitDateStr);
										}
									}
									break;
								}
							}								
						}
					}
					//submission end date limit validation
					CustomParameter deviceTypesHavingSubmissionEndDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_END_DATE_VALIDATION, "");
					if(deviceTypesHavingSubmissionEndDateValidationCP!=null) {
						String deviceTypesHavingSubmissionEndDateValidationValue = deviceTypesHavingSubmissionEndDateValidationCP.getValue();
						if(deviceTypesHavingSubmissionEndDateValidationValue!=null) {
							String[] deviceTypesHavingSubmissionEndDateValidation = deviceTypesHavingSubmissionEndDateValidationValue.split(",");
							for(String dt: deviceTypesHavingSubmissionEndDateValidation) {
								if(dt.trim().equals(domain.getType().getType().trim())) {
									String submissionEndLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
									if(submissionEndLimitDateStr!=null && !submissionEndLimitDateStr.isEmpty()) {
										Date submissionEndLimitDate = FormaterUtil.formatStringToDate(submissionEndLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
										if(submissionEndLimitDate!=null
												&& submissionEndLimitDate.before(new Date())) {
											submissionEndLimitDateStr = FormaterUtil.formatDateToString(submissionEndLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Question cannot be submitted after " + submissionEndLimitDateStr);
										}
									}
									break;
								}
							}								
						}
					}
				}
			}else if(operation.equals("startworkflow")){
				// Empty check for Ministry
				if(domain.getMinistry() == null){
					result.rejectValue("ministry", "MinistryEmpty");
				}
				// Empty check for Subdepartment
				if(domain.getSubDepartment() == null){
					result.rejectValue("subDepartment", "SubDepartmentEmpty");
				}
				// Empty check for Group
				if(domain.getGroup() == null){
					result.rejectValue("group", "GroupEmpty");
				}
			}
		}
		else {
			if(role.equals("QIS_TYPIST")){
				//Empty check for Number
				if(domain.getNumber()==null){
					result.rejectValue("number", "NumberEmpty");
				}
				// Check duplicate entry for question Number
				Boolean flag = Question.
						isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
				Question question = Question.findById(Question.class, domain.getId());
				if(!question.getNumber().equals(domain.getNumber())){
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
					}
				}
			}
		}
		
	}

	public static void populateUpdateIfErrors(final Question domain,
			final ModelMap model,
			final HttpServletRequest request,
			final AuthUser authUser) throws ELSException, ParseException {
		request.getSession().setAttribute("questionType", domain.getType().getType().toString());
		//locale
		String locale=domain.getLocale();
		
		//populate houseType
		HouseType houseType=domain.getHouseType();
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseTypeType", houseType.getType());
		model.addAttribute("houseType",houseType.getId());

		//populate session
		Session selectedSession=domain.getSession();
		if(selectedSession == null){
			throw new ELSException("StarredQuestionController.populateEdit/4", 
					"highestquestionprioritynotset");
		}
		model.addAttribute("session",selectedSession.getId());
		
		 
		//populate devicetype
		DeviceType deviceType=domain.getType();
		model.addAttribute("formattedQuestionType",deviceType.getName());
		model.addAttribute("questionType",deviceType.getId());
		model.addAttribute("deviceType", deviceType.getId());
		model.addAttribute("selectedQuestionType",deviceType.getType());
		if(domain.getOriginalType()!=null) {
			model.addAttribute("originalType",domain.getOriginalType().getId());
		}
		 
		//populate primary member And constituency
		Member member = domain.getPrimaryMember();
		if(member != null){
			String primaryMember = member.getFullname();
			model.addAttribute("primaryMember", member.getId());
			model.addAttribute("formattedPrimaryMember", primaryMember);
			Constituency constituency = Member.findConstituency(member, new Date());
			if(constituency != null){
				model.addAttribute("constituency", constituency.getDisplayName());
			}
		}

		//Populate Priorities
		CustomParameter customParameter = CustomParameter.
				findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter != null) {
			List<MasterVO> priorities = new ArrayList<MasterVO>();
			Integer priority = Integer.parseInt(customParameter.getValue());
			for(int i = 1;i <= priority;i++){
				String localizedPriority = FormaterUtil.
						getNumberFormatterNoGrouping(locale).format(i);
				priorities.add(new MasterVO(i,localizedPriority));
			}
			model.addAttribute("priorities",priorities);
		}else{
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"highestquestionprioritynotset");
		}
		
		//populate selected Priority
		if(domain.getPriority()!=null){
			model.addAttribute("priority", domain.getPriority());
			model.addAttribute("formattedPriority",FormaterUtil.
					getNumberFormatterNoGrouping(locale).format(domain.getPriority()));
		}
		/**** Priorities Ends  ****/

						
		//Populate Ministries
		
		List<Ministry> ministries = Ministry.
				findMinistriesAssignedToGroups(houseType, selectedSession.getYear(), 
						selectedSession.getType(), locale);
		model.addAttribute("ministries", ministries);
		
		//Populate Ministry
		Ministry ministry = domain.getMinistry();
		model.addAttribute("ministrySelected", ministry.getId());
		
		//Populate Group
		Group group = domain.getGroup();
		if(group != null) {
			model.addAttribute("formattedGroup",
					FormaterUtil.getNumberFormatterNoGrouping(locale).format(group.getNumber()));
			model.addAttribute("group", group.getId());
		}
		
		//Populate SubDepartments
		if(ministry != null) {
			Date onDate = selectedSession.getEndDate();
			if(onDate.before(new Date())) {
				onDate = new Date();
			}
			List<SubDepartment> subDepartments = 
					MemberMinister.findAssignedSubDepartments(ministry, onDate, locale);
			model.addAttribute("subDepartments", subDepartments);
		}
		
		//populate subdepartment
		SubDepartment subDepartment = domain.getSubDepartment();
		if(subDepartment != null) {
			model.addAttribute("subDepartmentSelected", subDepartment.getId());
		}
		
		//populate Answering Dates
		if(group != null){
			List<QuestionDates> answeringDates = group.getQuestionDates();
			List<MasterVO> masterVOs = new ArrayList<MasterVO>();
			for(QuestionDates i:answeringDates){
				MasterVO masterVO = new MasterVO(i.getId(),
						FormaterUtil.getDateFormatter(locale).format(i.getAnsweringDate()));
				masterVOs.add(masterVO);
			}
			model.addAttribute("answeringDates", masterVOs);
		}
		
		//Populate Answering Date
		QuestionDates questionDate = domain.getAnsweringDate();
		if(questionDate != null){
			model.addAttribute("answeringDate",questionDate.getId());
			model.addAttribute("formattedAnsweringDate", FormaterUtil.
					getDateFormatter(locale).format(questionDate.getAnsweringDate()));
			model.addAttribute("answeringDateSelected", questionDate.getId());
		}
		
	
		// populate Charting Answering Date
		if(domain.getChartAnsweringDate() != null){
			QuestionDates chartAnsweringDate = domain.getChartAnsweringDate();
			model.addAttribute("formattedChartAnsweringDate",FormaterUtil.
					getDateFormatter(locale).format(chartAnsweringDate.getAnsweringDate()));
			model.addAttribute("chartAnsweringDate", chartAnsweringDate.getId());
		}
		
		
		String strCreationDate = request.getParameter("setCreationDate");
		String strSubmissionDate = request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
		CustomParameter dateTimeFormat = 
				CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat != null){
			SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			if(strSubmissionDate != null && !strSubmissionDate.isEmpty()){
				domain.setSubmissionDate(format.parse(strSubmissionDate));
				
				model.addAttribute("submissionDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(strCreationDate != null && !strCreationDate.isEmpty()){
				domain.setCreationDate(format.parse(strCreationDate));
			}
			if(strWorkflowStartedOnDate != null && !strWorkflowStartedOnDate.isEmpty()){
				domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
			}
			if(strTaskReceivedOnDate != null && !strTaskReceivedOnDate.isEmpty()){
				domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
			}
			/**** answer related dates ****/
			String allowedDeviceTypes = "";
			CustomParameter deviceTypesForAnswerRelatedDates = 
					CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_FOR_ANSWER_RELATED_DATES", domain.getLocale());
			if(deviceTypesForAnswerRelatedDates!=null) {
				allowedDeviceTypes = deviceTypesForAnswerRelatedDates.getValue();
			} else {
				allowedDeviceTypes = 
						ApplicationConstants.STARRED_QUESTION + ", " + ApplicationConstants.UNSTARRED_QUESTION;
			}
			if(allowedDeviceTypes.contains(domain.getType().getType())) {
				String strAnswerRequestedDate = request.getParameter("setAnswerRequestedDate");
				if(strAnswerRequestedDate != null && !strAnswerRequestedDate.isEmpty()) {
					if(dateTimeFormat != null) {						
						domain.setAnswerRequestedDate(format.parse(strAnswerRequestedDate));
					}					
				}
				String strAnswerReceivedDate = request.getParameter("setAnswerReceivedDate");
				if(strAnswerReceivedDate !=null && !strAnswerReceivedDate.isEmpty()) {
					if(dateTimeFormat != null) {
						domain.setAnswerReceivedDate(format.parse(strAnswerReceivedDate));
					}					
				}			
			}
		}
		
		//Populate formatted Number
		if(domain.getNumber() != null){
			model.addAttribute("formattedNumber",FormaterUtil.
					getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		
		//populate role
		Role role = QuestionController.getRole(request, locale);
		String strRole = null;
		if(role != null){
			strRole = role.getType(); 
		}else{
			strRole=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",strRole);
			request.getSession().removeAttribute("role");
		}

		//populate usegroupType
		UserGroupType userGroupType = QuestionController.getUserGroupType(request, locale);
		String usergroupType = null;
		if(userGroupType != null){
			usergroupType = userGroupType.getType();
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType = (String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		
		//populate usergroup
		UserGroup userGroup = QuestionController.getUserGroup(request,locale);
		String strUsergroup = null;
		if(userGroup!=null){
			model.addAttribute("usergroup",userGroup.getId());
		}else{
			strUsergroup = (String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup", strUsergroup);
			request.getSession().removeAttribute("userGroup");
		}

		//Populate createdby
		model.addAttribute("createdBy",domain.getCreatedBy());
		

		//populate bulkedit
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
		//populate Statuses
		Status status = domain.getStatus();
		Status internalStatus = domain.getInternalStatus();
		Status recommendationStatus = domain.getRecommendationStatus();
		if(status != null){
			//populate status
			model.addAttribute("status", status.getId());
			model.addAttribute("memberStatusType", status.getType());
		}
		
		if(internalStatus != null){
			//populate internal status
			model.addAttribute("internalStatus", internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			
			if(usergroupType !=null && !(usergroupType.isEmpty()) && usergroupType.equals("assistant")){
				//populate PUT UP OPTIONS
				if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)){
					if(recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
						QuestionController.
						populateInternalStatus(model,recommendationStatus.getType(),usergroupType,locale,deviceType.getType());
					} else {
						QuestionController.
						populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
					}
					if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
						domain.setWorkflowStarted("NO");
					}
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
			}else if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("clerk")){
				if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)){
//					QuestionController.
//					populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
					//set workflow started Flag
					if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
						domain.setWorkflowStarted("NO");
					}
				}
			}
		}
		
		//populate recommendationStatus
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus", recommendationStatus.getId());
			model.addAttribute("recommendationStatusType", recommendationStatus.getType());
		}
		

		/**** Referenced Questions Starts ****/
		CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
				findByName(CustomParameter.class, "QIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
		if(clubbedReferencedEntitiesVisibleUserGroups != null){
			List<UserGroupType> userGroupTypes = QuestionController.
					delimitedStringToUGTList(clubbedReferencedEntitiesVisibleUserGroups.getValue(), ",", locale);
			Boolean isUserGroupAllowed = QuestionController.isUserGroupTypeExists(userGroupTypes, userGroupType);
			//populate referencedEntities
			if(isUserGroupAllowed){
				List<Reference> refentities = QuestionController.getReferencedEntityReferences(domain,locale);
				model.addAttribute("referencedQuestions",refentities);
				
				// Populate clubbed entities
				List<Reference> clubEntityReferences = QuestionController.getClubbedEntityReferences(domain, locale);
				model.addAttribute("clubbedQuestions",clubEntityReferences);
				
				// Populate latest revised question text from clubbed questions
				String latestRevisedQuestionTextFromClubbedQuestions = "";
				List<ClubbedEntity> clubbedEntities=Question.findClubbedEntitiesByPosition(domain);
				if(clubbedEntities!=null & !clubbedEntities.isEmpty()){
					ClubbedEntity ce = clubbedEntities.get(0); //first position clubbed question
					latestRevisedQuestionTextFromClubbedQuestions = ce.getQuestion().getRevisedQuestionText();					
				}
				model.addAttribute("latestRevisedQuestionTextFromClubbedQuestions",latestRevisedQuestionTextFromClubbedQuestions);
			}
		}
	
		/*** Populate Immediate Supporting member***/
		//Populate Supporting Member Names
		String supportingMemberNames = QuestionController.getDelimitedSupportingMembers(model, domain, usergroupType);
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
		
		//Populate Parent
		if(domain.getParent()!=null){
			model.addAttribute("formattedParentNumber",FormaterUtil.
					getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
			model.addAttribute("parent",domain.getParent().getId());
		}
		

		//Populate Actors
		if(domain.getInternalStatus() != null){
			String internalStatusType = domain.getInternalStatus().getType();			
			if(usergroupType!=null && !usergroupType.isEmpty() && usergroupType.equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
						||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
						||internalStatusType.equals(ApplicationConstants.QUESTION_PUTUP_REJECTION))){
				List<Reference> actors=WorkflowConfig.
						findQuestionActorsVO(domain, internalStatus, userGroup, 1, locale);
				model.addAttribute("actors", actors);
				if(actors!=null && !actors.isEmpty()){
					String nextActor = actors.get(0).getId();
					String[] actorArr = nextActor.split("#");
					domain.setLevel(actorArr[2]);
					domain.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
				}
			}	
		}
		
		
		Status tempStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_REJECTION, locale);
		boolean canRemark = false;	
		String errorMessagePossible="";
		try{
			errorMessagePossible = "domain_not_found";
			if (internalStatus.getType().equals(tempStatus.getType())) {
				errorMessagePossible = "questiondraft_not_found_for_remark";
				QuestionDraft qDraft = domain.findPreviousDraft();					
				model.addAttribute("sectionofficer_remark",qDraft.getRemarks());
				canRemark = true;
			}
		}catch(Exception e){
			model.addAttribute("errorcode",errorMessagePossible);
		}
		if(!canRemark){
			model.addAttribute("sectionofficer_remark","");
		}
		/**** Answer related Dates ****/
		String allowedDeviceTypesForAnswerRelatedDates = "";
		CustomParameter deviceTypesForAnswerRelatedDates = 
				CustomParameter.findByName(CustomParameter.class, 
						"DEVICETYPES_FOR_ANSWER_RELATED_DATES", locale.toString());
		if(deviceTypesForAnswerRelatedDates != null) {
			allowedDeviceTypesForAnswerRelatedDates = deviceTypesForAnswerRelatedDates.getValue();
		} else {
			allowedDeviceTypesForAnswerRelatedDates = 
					ApplicationConstants.STARRED_QUESTION + ", " + ApplicationConstants.UNSTARRED_QUESTION;
		}
		model.addAttribute("allowedDeviceTypesForAnswerRelatedDates", allowedDeviceTypesForAnswerRelatedDates);
		if(domain.getAnswerRequestedDate() != null) {
			model.addAttribute("formattedAnswerRequestedDate",
					FormaterUtil.formatDateToString(domain.getAnswerRequestedDate(), 
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		if(domain.getAnswerReceivedDate()!=null) {
			model.addAttribute("formattedAnswerReceivedDate", 
					FormaterUtil.formatDateToString(domain.getAnswerReceivedDate(),
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		
		/**** Yaadi related things ****/
		/** populate yaadi laying date **/
		if(domain.getYaadiLayingDate()!=null) {
			model.addAttribute("yaadiLayingDate", 
					FormaterUtil.formatDateToString(domain.getYaadiLayingDate(),
							ApplicationConstants.SERVER_DATEFORMAT, locale));
		}
		/** populate yaadi details text if question is present in yaadi **/
		String yaadiDetailsText = domain.findYaadiDetailsText();
		model.addAttribute("yaadiDetailsText", yaadiDetailsText);	
		/** find if previous session unstarred question is parent and show its details including yaadi if present **/
		Question parentQuestion = domain.getParent();
		if(parentQuestion!=null
				&& parentQuestion.getType().getType().trim().equalsIgnoreCase(ApplicationConstants.UNSTARRED_QUESTION)
				&& domain.getSession().getStartDate().after(parentQuestion.getSession().getStartDate())) {
			String previousSessionUnstarredParentDetailsText = domain.findPreviousSessionUnstarredParentDetailsText();
			model.addAttribute("previousSessionUnstarredParentDetailsText", previousSessionUnstarredParentDetailsText);
		}
		/** check whether question was removed from unstarred yaadi purposely  **/
		if(domain.getParent()==null 
				&& domain.getStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				&& domain.getAnswer()!=null && !domain.getAnswer().isEmpty() 
				&& !domain.getAnswer().equals("<p></p>") && !domain.getAnswer().equals("<br><p></p>")) {
			Boolean isRemovedFromYaadiDetails = domain.checkWhetherIsRemovedFromYaadiDetails();
			if(isRemovedFromYaadiDetails!=null && isRemovedFromYaadiDetails.equals(true)) {
				model.addAttribute("isRemovedFromYaadiDetails", true);
			}
		}
	}

	public static void populateUpdateIfNoErrors(Question domain,
			ModelMap model, HttpServletRequest request, AuthUser authUser) {
		String operation=request.getParameter("operation");
		UserGroupType userGroupType = QuestionController.getUserGroupType(request, domain.getLocale());
		//set EditedAs
		if(userGroupType != null){
			domain.setEditedAs(userGroupType.getName());
		}
		
		//Check for required fields
		if(domain.getHouseType() != null && domain.getType() != null && domain.getSession() != null
				&& domain.getPrimaryMember() != null && domain.getMinistry() != null
				&& domain.getGroup()!=null && (!domain.getSubject().isEmpty())
				&& (!domain.getQuestionText().isEmpty())){
			if(operation != null && !operation.isEmpty()){
				// operation is submit
				if(operation.trim().equals("submit")){
					// set submission date
					if(domain.getSubmissionDate() == null){
						domain.setSubmissionDate(new Date());
					}
					// set Supporting Members
					List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
					if(domain.getSupportingMembers() != null && !domain.getSupportingMembers().isEmpty()){
						for(SupportingMember i:domain.getSupportingMembers()){
							if(userGroupType.getType().equals("typist")){
								supportingMembers.add(i);
							}else{
								String decisionStatusType =i.getDecisionStatus().getType().trim();
								if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
									supportingMembers.add(i);
								}
							}
						}
						domain.setSupportingMembers(supportingMembers);
					}
					// Set status, internalStatus, recommendationstatus
					Status newstatus=Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);
				}else if(operation.trim().equals("startworkflow")){
					{
						/* Find if next actors are not active then create a draft for them if draft is 
						 * not existing for that actors.
						 */
						try{
							String strNextuser = request.getParameter("actor");
							String[] nextuser = null;
							int nextUserLevel = 0;
							if(strNextuser != null && !strNextuser.isEmpty()){
									nextuser = strNextuser.split("#");
									nextUserLevel = Integer.parseInt(nextuser[2]);
							} 
													
							Question q = null;
							
							if(domain.getId() != null){
								q = Question.findById(Question.class, domain.getId());
							}else{
								q = domain.copyQuestion();
							}
							
							
							Map<String, String[]> params = new HashMap<String, String[]>();
							params.put("locale", new String[]{domain.getLocale().toString()});
							params.put("sessionId", new String[]{domain.getSession().getId().toString()});
							params.put("ugType", new String[]{ApplicationConstants.ASSISTANT});
							params.put("qId", new String[]{domain.getId().toString()});
							List data = Query.findReport("ACTIVE_USER", params);
							String strUsername = null;
							if(data != null && !data.isEmpty()){
								Object[] obj = (Object[])data.get(0);
								strUsername = obj[1].toString();
							}
						
							Credential cr = null;
							if(strUsername != null){
								cr = Credential.findByFieldName(Credential.class, "username", strUsername, null);
							}
							
							if(cr != null){
								UserGroup assistant = UserGroup.findActive(cr, new Date(), domain.getLocale().toString());
								List<Reference> refs = WorkflowConfig.
										findQuestionActorsVO(q,domain.getInternalStatus(),
												assistant,1,q.getLocale());
								
								Set<QuestionDraft> ogDrafts = q.getDrafts();
								Set<QuestionDraft> drafts = new HashSet<QuestionDraft>();
							
								for(Reference ref : refs){
									
									String[] user = ref.getId().split("#");
									
									if(!user[1].equals(ApplicationConstants.MEMBER) && !user[1].equals(ApplicationConstants.DEPARTMENT) && !user[1].equals(ApplicationConstants.DEPARTMENT_DESKOFFICER) && !ref.getState().equals(ApplicationConstants.ACTOR_ACTIVE)){
										
										int refLevel = Integer.parseInt(user[2]);
										
										if(refLevel < nextUserLevel){
											boolean foundUsersDraft = false;
											if(ogDrafts != null && !ogDrafts.isEmpty()){
												for(QuestionDraft qd : ogDrafts){
													if(qd.getEditedAs().equals(user[3]) 
															&& qd.getEditedBy().equals(user[0])){
														foundUsersDraft = true;
														break;
													}
												}
												
												if(!foundUsersDraft){
													QuestionDraft qdn = Question.addDraft(q, user[0], user[3], ref.getRemark());
													drafts.add(qdn);
												}
											}
										}
									}
								}
								if(drafts != null && !drafts.isEmpty()){
									domain.setDrafts(drafts);
								}
							}
						} catch (ELSException e) {
							e.printStackTrace();
							//return "redirect:question/"+domain.getId()+"/edit";
						}
					}
					
				}
			}
			// operation is Null OR Empty
			else{
				// Set status, internalStatus, recommendationstatus
				if(!userGroupType.getType().equals(ApplicationConstants.ASSISTANT)
					&& !userGroupType.getType().equals(ApplicationConstants.CLERK)){
					Status status=Status.
							findByType(ApplicationConstants.QUESTION_UNSTARRED_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
				
			}
		}
		// Required Fields are not entered
		else{
			// Set status, internalStatus, recommendationstatus
			Status status=Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}

		// set Edited On and EditedBy
		domain.setEditedOn(new Date());
		domain.setEditedBy(authUser.getActualUsername());


		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		
		CustomParameter assistantProcessedAllowed = CustomParameter.
				findByName(CustomParameter.class,"QIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
		if(assistantProcessedAllowed != null){
			List<UserGroupType> userGroupTypes = QuestionController.
					delimitedStringToUGTList(assistantProcessedAllowed.getValue(), ",", domain.getLocale());
			Boolean isUserGroupAllowed = QuestionController.isUserGroupTypeExists(userGroupTypes, userGroupType);
			if(isUserGroupAllowed){
				Question question = Question.findById(Question.class, domain.getId());
				String internalStatusType = question.getInternalStatus().getType();
				Group group = question.getGroup();
				if((internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_GROUPCHANGED)) 
					&& domain.getMinistry()!=null 
					&& group!=null 
					&& domain.getSubDepartment()!=null) {
					Status ASSISTANT_PROCESSED = Status.
							findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED,
									domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}
				
				QuestionDraft draft = domain.findPreviousDraft();				        
				if(group != null && draft != null) {
					Group prevGroup = draft.getGroup();
					if(prevGroup != null && ! prevGroup.getNumber().equals(group.getNumber())) {
						Status GROUP_CHANGED = Status.
								findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_GROUPCHANGED, domain.getLocale());
						domain.setInternalStatus(GROUP_CHANGED);
						domain.setRecommendationStatus(GROUP_CHANGED);
					}
				}
			}
		}

		/** copy updated revised question text of parent to its all clubbed questions if any **/
		if(domain.getParent()==null 
				&& domain.getClubbedEntities()!=null 
				&& !domain.getClubbedEntities().isEmpty()) {	
			String updatedRevisedQuestionText = domain.getRevisedQuestionText();
			if(updatedRevisedQuestionText!=null && !updatedRevisedQuestionText.isEmpty()) {
				Question qt = Question.findById(Question.class, domain.getId());
				if(qt.getRevisedQuestionText()==null || qt.getRevisedQuestionText().isEmpty() || !qt.getRevisedQuestionText().equals(updatedRevisedQuestionText)) {
					for(ClubbedEntity ce: domain.getClubbedEntities()) {
						Question clubbedQuestion = ce.getQuestion();
						clubbedQuestion.setRevisedQuestionText(updatedRevisedQuestionText);
						clubbedQuestion.simpleMerge();
					}
				}				
			}			
		}
			
		/**** updating submission date and creation date ****/
		String strCreationDate = request.getParameter("setCreationDate");
		String strSubmissionDate = request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
		CustomParameter dateTimeFormat = CustomParameter.
				findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat != null){
			SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate != null){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate != null){
					domain.setCreationDate(format.parse(strCreationDate));
				}
				if(strWorkflowStartedOnDate != null && !strWorkflowStartedOnDate.isEmpty()){
					domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
				}
				if(strTaskReceivedOnDate != null&&!strTaskReceivedOnDate.isEmpty()){
					domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
				}
				/**** answer related dates ****/
				String allowedDeviceTypes = "";
				CustomParameter deviceTypesForAnswerRelatedDates = 
						CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_FOR_ANSWER_RELATED_DATES", domain.getLocale());
				if(deviceTypesForAnswerRelatedDates!=null) {
					allowedDeviceTypes = deviceTypesForAnswerRelatedDates.getValue();
				} else {
					allowedDeviceTypes = 
							ApplicationConstants.STARRED_QUESTION + ", " + ApplicationConstants.UNSTARRED_QUESTION;
				}
				if(allowedDeviceTypes.contains(domain.getType().getType())) {
					String strAnswerRequestedDate = request.getParameter("setAnswerRequestedDate");
					if(strAnswerRequestedDate != null && !strAnswerRequestedDate.isEmpty()) {
						if(dateTimeFormat != null) {						
							domain.setAnswerRequestedDate(format.parse(strAnswerRequestedDate));
						}					
					}
					String strAnswerReceivedDate = request.getParameter("setAnswerReceivedDate");
					if(strAnswerReceivedDate !=null && !strAnswerReceivedDate.isEmpty()) {
						if(dateTimeFormat != null) {
							domain.setAnswerReceivedDate(format.parse(strAnswerReceivedDate));
						}					
					}			
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		/** allow question to be eligible for unstarred yaadi if it was removed before & now allowed **/
		String isAllowedInYaadiStr = request.getParameter("isAllowedInYaadi");
		if(isAllowedInYaadiStr!=null && !isAllowedInYaadiStr.isEmpty()) {
			Boolean isAllowedInYaadi = Boolean.parseBoolean(isAllowedInYaadiStr);
			if(isAllowedInYaadi!=null && isAllowedInYaadi.equals(true)) {
				YaadiDetails.allowDeviceInYaadiDetails(domain);
			}
		}
	}

	public static void populateAfterUpdate(final Question domain,
			final ModelMap model,
			final HttpServletRequest request,
			final AuthUser authUser,
			final IProcessService processService) throws ELSException {
		/**** Parameters which are read from request in populate edit needs to be saved in session Starts ****/
		request.getSession().setAttribute("role", request.getParameter("role"));
		request.getSession().setAttribute("usergroup", request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType", request.getParameter("usergroupType"));
		request.getSession().setAttribute("bulkedit", request.getParameter("bulkedit"));
		request.getSession().setAttribute("questionType", request.getParameter("questionType"));
		/**** Parameters which are read from request in populate edit needs to be saved in session Starts ****/
		Question question = Question.findById(Question.class, domain.getId());

		/**** Supporting Member Workflow/Put Up Workflow ****/
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					ProcessDefinition processDefinition = processService.
							findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties = new HashMap<String, String>();
					properties.put("pv_deviceId", String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId", domain.getType().getType());
					ProcessInstance processInstance = processService.
							createProcessInstance(processDefinition, properties);
					List<Task> tasks = processService.getCurrentTasks(processInstance);					
					List<WorkflowDetails> workflowDetails = WorkflowDetails.
							create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"");
					List<SupportingMember> supportingMembers = question.getSupportingMembers();
					Status status=Status.
							findByType(ApplicationConstants.SUPPORTING_MEMBER_PENDING, domain.getLocale());
					for(SupportingMember i:supportingMembers){
						String decisionStatusType = i.getDecisionStatus().getType();
						if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							i.setDecisionStatus(status);
							i.setRequestReceivedOn(new Date());
							i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
							User user = User.
									findbyNameBirthDate(i.getMember().getFirstName(),
											i.getMember().getMiddleName(),i.getMember().getLastName(),
											i.getMember().getBirthDate());
							Credential credential=user.getCredential();
							for(WorkflowDetails j:workflowDetails){
								if(j.getAssignee().equals(credential.getUsername())){
									i.setWorkflowDetailsId(String.valueOf(j.getId()));
									break;
								}
							}
							i.merge();
						}
					}
				}else if(operation.equals("startworkflow")){
						/** copy latest question text of child question to revised question text of its parent's other clubbed questions if any **/
						if(question.getParent()!=null) {
							/** fetch question's latest question text **/
							String latestQuestionText = question.getRevisedQuestionText();
							if(latestQuestionText==null || latestQuestionText.isEmpty()) {
								latestQuestionText = question.getQuestionText();
							}							
							for(ClubbedEntity ce: question.getParent().getClubbedEntities()) {
								Question clubbedQuestion = ce.getQuestion();
								if(!clubbedQuestion.getId().equals(question.getId())) {
									clubbedQuestion.setRevisedQuestionText(latestQuestionText);
									clubbedQuestion.simpleMerge();
								}
							}
						}
						
						ProcessDefinition processDefinition = processService.
								findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						Map<String,String> properties = new HashMap<String, String>();					
						String nextuser = request.getParameter("actor");
						String level = "";
						UserGroupType usergroupType = null;
						if(nextuser != null){
							if(!nextuser.isEmpty()){
								String[] temp = nextuser.split("#");
								properties.put("pv_user",temp[0]);
								level = temp[2];
								usergroupType = UserGroupType.findByType(temp[1], domain.getLocale());
							}
						}
						String endflag = domain.getEndFlag();
						properties.put("pv_endflag", endflag);	
						properties.put("pv_deviceId", String.valueOf(domain.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(domain.getType().getId()));
						ProcessInstance processInstance= processService.
								createProcessInstance(processDefinition, properties);
						Task task= processService.getCurrentTask(processInstance);
						if(endflag!=null){
							if(!endflag.isEmpty()){
								if(endflag.equals("continue")){
									Workflow workflow = question.findWorkflowFromStatus();
									
									WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
									question.setWorkflowDetailsId(workflowDetails.getId());
								}
							}
						}
						question.setWorkflowStarted("YES");
						question.setWorkflowStartedOn(new Date());
						question.setTaskReceivedOn(new Date());
						question.simpleMerge();
				}
			}
		}
	}

	public static Boolean preDelete(final Question question,
			final ModelMap model,
			final HttpServletRequest request,
			final AuthUser authUser) {
		if(question!=null){
			Status status=question.getStatus();
			if(status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_INCOMPLETE)
					||status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_COMPLETE)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
	}

	public static String getBulkSubmissionView(final HttpServletRequest request,
			final ModelMap model,
			final AuthUser authUser,
			final Locale locale) throws ELSException {
		HouseType houseType = QuestionController.getHouseType(request, locale.toString());
		SessionType sessionType = QuestionController.getSessionType(request, locale.toString());
		Integer sessionYear = QuestionController.stringToIntegerYear(request, locale.toString());
		DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
		Integer itemCount = QuestionController.stringToIntegerItemCount(request, locale.toString());
		Member primaryMember = Member.findMember(authUser.getFirstName(),
				authUser.getMiddleName(), authUser.getLastName(), authUser.getBirthDate(), locale.toString());
		Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
		List<Question> questions = new ArrayList<Question>();
		if(session != null){
			if(primaryMember != null){
				questions = Question.
						findAllByMember(session, primaryMember, deviceType, itemCount, locale.toString());	
			}
		}
		model.addAttribute("houseType", houseType.getId());
		model.addAttribute("questionType", deviceType.getId());
		model.addAttribute("deviceType", deviceType.getId());
		model.addAttribute("questions", questions);
		model.addAttribute("size", questions.size());
		model.addAttribute("locale", locale.toString());

		String userGroupType = request.getParameter("usergroupType");
		model.addAttribute("usergroupType", userGroupType);
		return "question/bulksubmission";
	
	}

	public static String bulkSubmission(final HttpServletRequest request,
			final ModelMap model,
			final AuthUser authUser,
			final IProcessService processService,
			final Locale locale) {
		String selectedItems = request.getParameter("items");
		if(selectedItems != null && ! selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");

			List<Question> questions = new ArrayList<Question>();
			for(String i : items) {
				Long id = Long.parseLong(i);
				Question question = Question.findById(Question.class, id);

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
							sm.setApprovedText(question.getQuestionText());
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
						ApplicationConstants.QUESTION_UNSTARRED_SUBMIT, question.getLocale());
				question.setStatus(newstatus);
				question.setInternalStatus(newstatus);
				question.setRecommendationStatus(newstatus);

				/**** Edited On, Edited By and Edited As is set ****/
				question.setSubmissionDate(new Date());
				question.setEditedOn(new Date());
				question.setEditedBy(authUser.getActualUsername());

				String strUserGroupType = request.getParameter("usergroupType");
				if(strUserGroupType != null) {
					UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class,
							"type", strUserGroupType, question.getLocale());
					question.setEditedAs(userGroupType.getName());
				}

				/**** Bulk Submitted ****/
				question.setBulkSubmitted(true);
				
				/**** Update the Question object ****/
				question = question.merge();
				questions.add(question);
			}

			model.addAttribute("questions", questions);
		}

		return "question/bulksubmissionack";
	}

	public static String getBulkSubmissionAssistantInt(
			final HttpServletRequest request, 
			final ModelMap model, 
			final AuthUser authUser,
			final Locale locale) throws ELSException {
		/**** Request Params ****/
		String retVal = "question/error";
		HouseType houseType = QuestionController.getHouseType(request, locale.toString());
		DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strItemsCount = request.getParameter("itemscount");
		String strGroup = request.getParameter("group");

		if( strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())) {
			
			/**** Decision Status Available To Assistant(At this stage) ****/			
			/**** QUESTION_PUT_UP_OPTIONS_ + QUESTION_TYPE + STATUS_TYPE + USERGROUP_TYPE ****/
			CustomParameter statusBasedStatuses = null;
			Status currentStatus = Status.findById(Status.class, new Long(strStatus));
			if(currentStatus!=null) {
				statusBasedStatuses =  CustomParameter.findByName(CustomParameter.class,
								"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" +
								currentStatus.getType() + "_" + 
								strUsergroupType.toUpperCase(), "");
			}			 
			/**** QUESTION_PUT_UP_OPTIONS_ + QUESTION_TYPE + HOUSE_TYPE + USERGROUP_TYPE ****/
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,
					"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" +
							 strUsergroupType.toUpperCase(), "");

			List<Status> internalStatuses;
			try {
				if(statusBasedStatuses!=null) {
					internalStatuses = Status.findStatusContainedIn(statusBasedStatuses.getValue(),locale.toString());
				} else {
					internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
				}				
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", houseType.getType());
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("questionType", deviceType.getType());
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("itemscount", strItemsCount);
			model.addAttribute("group", strGroup);

			retVal = "question/bulksubmissionassistantint";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}

	public static String bulkSubmissionAssistant(final HttpServletRequest request,
			final ModelMap model, 
			final AuthUser authUser,
			final IProcessService processService,
			final Locale locale) throws ELSException {
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus = request.getParameter("currentStatus");
		StringBuffer submitted = new StringBuffer();
		StringBuffer recommendAdmission = new StringBuffer();
		StringBuffer recommendRejection = new StringBuffer();
		StringBuffer recommendRepeatRejection = new StringBuffer();
		StringBuffer recommendRepeatAdmission = new StringBuffer();
		StringBuffer recommendClarificationFromMember = new StringBuffer();
		StringBuffer recommendClarificationFromDept = new StringBuffer();
		StringBuffer recommendClarificationFromGovt = new StringBuffer();
		StringBuffer recommendClarificationFromMemberDept = new StringBuffer();
		StringBuffer recommendClubbing = new StringBuffer();
		StringBuffer recommendNameClubbing = new StringBuffer();
		StringBuffer recommendClubbingPostAdmission = new StringBuffer();
		StringBuffer recommendAdmitDueToReverseClubbing = new StringBuffer();
		StringBuffer recommendUnClubbing = new StringBuffer();


		if(selectedItems != null && selectedItems.length > 0
				&& strStatus != null && !strStatus.isEmpty()) {
			/**** As It Is Condition ****/
			if(strStatus.equals("-")) {
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					Question question = Question.findById(Question.class, id);

					if(!question.getInternalStatus().getType().
							equals(ApplicationConstants.QUESTION_UNSTARRED_SUBMIT)){
						/**** Create Process ****/
						ProcessDefinition processDefinition = null;
						Map<String,String> properties = new HashMap<String, String>();
						String actor = question.getActor();
						String[] temp = actor.split("#");
						processDefinition = processService.
								findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						properties.put("pv_user", temp[0]);						
						properties.put("pv_endflag", question.getEndFlag());	
						properties.put("pv_deviceId", String.valueOf(question.getId()));
						properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
						ProcessInstance processInstance = processService.
								createProcessInstance(processDefinition, properties);

						/**** Create Workdetails Entry ****/
						Task task = processService.getCurrentTask(processInstance);
						if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
								&& question.getEndFlag().equals("continue")){
							UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
							WorkflowDetails workflowDetails = null;
							try {
								
								/*
								 * Added by Amit Desai 2 Dec 2014
								 * START...
								 */
								//workflowDetails = WorkflowDetails.
								//		create(question,task,usergroupType, ApplicationConstants.APPROVAL_WORKFLOW, 
								//				question.getLevel());
								Workflow workflow = null;

								Status internalStatus = question.getInternalStatus();
								String internalStatusType = internalStatus.getType();
								Status recommendationStatus = question.getRecommendationStatus();
								String recommendationStatusType = recommendationStatus.getType();

								if(recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)										
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_UNCLUBBING)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)) {
									workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
								} 
								else {
									workflow = Workflow.findByStatus(internalStatus, locale.toString());
								}

								String workflowType = workflow.getType();
								String assigneeLevel = question.getLevel();
								workflowDetails = WorkflowDetails.create(question, task, usergroupType, workflowType, assigneeLevel);
								/*
								 * Added by Amit Desai 2 Dec 2014
								 * ... END
								 */
								
								question.setWorkflowDetailsId(workflowDetails.getId());
								/**** Workflow Started ****/
								question.setWorkflowStarted("YES");
								question.setWorkflowStartedOn(new Date());
								question.setTaskReceivedOn(new Date());
								question.simpleMerge();
							} catch (ELSException e) {
								model.addAttribute("error", e.getParameter());
							}

						}

						if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMISSION)){
							recommendAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECTION)){
							recommendRejection.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REPEATREJECTION)){
							recommendRepeatRejection.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REPEATADMISSION)){
							recommendRepeatAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
							recommendClarificationFromMember.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
							recommendClarificationFromDept.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_GOVT)){
							recommendClarificationFromGovt.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
							recommendClarificationFromMemberDept.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)){
							recommendClubbing.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)){
							recommendNameClubbing.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)){
							recommendClubbingPostAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)){
							recommendAdmitDueToReverseClubbing.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_UNCLUBBING)){
							recommendUnClubbing.append(question.formatNumber() + ",");
						}
					}
					else {
						submitted.append(question.formatNumber() + ",");
					}
				}

				model.addAttribute("submitted", submitted.toString());
			}else {
				Long statusId = Long.parseLong(strStatus);
				Status status = Status.findById(Status.class, statusId);

				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					Question question = Question.findById(Question.class, id);

					String actor = request.getParameter("actor");
					String level = request.getParameter("level");
					if(actor != null && !actor.isEmpty()
						&& level != null && !level.isEmpty()) {
						Reference reference;
						try {
							reference = UserGroup.findQuestionActor(question, actor, level, locale.toString());

							if(reference != null
									&& reference.getId() != null && !reference.getId().isEmpty()
									&& reference.getName() != null && !reference.getName().isEmpty()) {

								/**** Update Actor ****/
								String[] temp = reference.getId().split("#");
								question.setActor(reference.getId());
								question.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
								question.setLevel(temp[2]);

								/**** Update Internal Status and Recommendation Status ****/
								if(!status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)
										&& !status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
										&& !status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_UNCLUBBING)) {
									question.setInternalStatus(status); 
								}								
								question.setRecommendationStatus(status);	
								question.setEndFlag("continue");

								/**** Create Process ****/
								ProcessDefinition processDefinition = null;
								Map<String, String> properties = new HashMap<String, String>();
								processDefinition = processService.
										findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
								properties.put("pv_user", temp[0]);						
								properties.put("pv_endflag", question.getEndFlag());	
								properties.put("pv_deviceId", String.valueOf(question.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
								

								ProcessInstance processInstance = processService.
										createProcessInstance(processDefinition, properties);

								/**** Create Workdetails Entry ****/
								Task task = processService.getCurrentTask(processInstance);
								if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
										&& question.getEndFlag().equals("continue")) {
									UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
									Workflow workflow = Workflow.findByStatus(status, locale.toString());
									WorkflowDetails workflowDetails = WorkflowDetails.
											create(question,task,usergroupType, workflow.getType(), question.getLevel());
							
									question.setWorkflowDetailsId(workflowDetails.getId());
									/**** Workflow Started ****/
									question.setWorkflowStarted("YES");
									question.setWorkflowStartedOn(new Date());
									question.setTaskReceivedOn(new Date());
									question.simpleMerge();
								}

								if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMISSION)){
									recommendAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECTION)){
									recommendRejection.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REPEATREJECTION)){
									recommendRepeatRejection.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REPEATADMISSION)){
									recommendRepeatAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
									recommendClarificationFromMember.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
									recommendClarificationFromDept.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_GOVT)){
									recommendClarificationFromGovt.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
									recommendClarificationFromMemberDept.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING)){
									recommendClubbing.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING)){
									recommendNameClubbing.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION)){
									recommendClubbingPostAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)){
									recommendAdmitDueToReverseClubbing.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_UNCLUBBING)){
									recommendUnClubbing.append(question.formatNumber() + ",");
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
				model.addAttribute("recommendClubbing", recommendClubbing.toString());
				model.addAttribute("recommendNameClubbing", recommendNameClubbing.toString());
				model.addAttribute("recommendClubbingPostAdmission", recommendClubbingPostAdmission.toString());
				model.addAttribute("recommendAdmitDueToReverseClubbing", recommendAdmitDueToReverseClubbing.toString());
				model.addAttribute("recommendUnClubbing", recommendUnClubbing.toString());
			}				
		}

		QuestionController.getBulkSubmissionQuestions(model, request, locale.toString());
		return "question/bulksubmissionassistantview";
	}
}
