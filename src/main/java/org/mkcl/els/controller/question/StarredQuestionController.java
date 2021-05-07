package org.mkcl.els.controller.question;

import java.text.ParseException;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.controller.mis.MemberOtherController;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberSupportingMember;
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
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.service.IProcessService;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/starredQuestion")
class StarredQuestionController {
			
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
		
		// Populate House types configured for the current user
		List<HouseType> houseTypes = QuestionController.getHouseTypes(currentUser, deviceType, locale);
		model.addAttribute("houseTypes", houseTypes);
		
		// Populate default House type
		HouseType houseType = null;
		if(houseTypes!=null && houseTypes.size()==1) {
			houseType = houseTypes.get(0);
			model.addAttribute("houseType", houseType.getType());
		} else {
			houseType = QuestionController.getHouseType(currentUser, locale);
			model.addAttribute("houseType", houseType.getType());
		}	
		
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
				throw new ELSException("StarredQuestionController.populateModule/4", 
						"QIS_ALLOWED_USERGROUPTYPES key is not set as CustomParameter");
			}
		}
		if(userGroup == null || userGroupType == null) {
//			throw new ELSException("StarredQuestionController.populateModule/4", 
//					"User group or User group type is not set for Username: " + currentUser.getUsername());
			
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
					throw new ELSException("StarredQuestionController.populateModule/4", 
							"SUBDEPARTMENT parameter is not set for Username: " + currentUser.getUsername());
				}
			}
		}
		else {
			throw new ELSException("StarredQuestionController.populateModule/4", 
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
				throw new ELSException("StarredQuestionController.populateModule/4", 
						"QUESTION_GRID_STATUS_ALLOWED_BY_DEFAULT_ + deviceTypeType " +
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
		
		/****Member's Questions Views Visibility Parameters****/
		Boolean sessionEndDateFlag = false;
		Date sessionEndDate = latestSession.getEndDate();
		if(sessionEndDate!=null) {
			String sessionEndDateTimeStr = FormaterUtil.formatDateToString(sessionEndDate, ApplicationConstants.DB_DATEFORMAT);
			CustomParameter visibilityStartTimeCP = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_START_TIME_FOR_MEMBER_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
			if(visibilityStartTimeCP!=null && visibilityStartTimeCP.getValue()!=null) {
				sessionEndDateTimeStr = sessionEndDateTimeStr + " " + visibilityStartTimeCP.getValue();
				Date sessionEndDateTime = FormaterUtil.formatStringToDate(sessionEndDateTimeStr, ApplicationConstants.DB_DATETIME_FORMAT);
				if(new Date().compareTo(sessionEndDateTime)>=0) {
					sessionEndDateFlag = true;
				}
			}
		}
		
		Boolean statusFlag = false;		
		CustomParameter statusFlagForMemberQuestionsView = CustomParameter.findByName(CustomParameter.class, "STATUS_FLAG_FOR_MEMBER_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
		if(statusFlagForMemberQuestionsView!=null && statusFlagForMemberQuestionsView.getValue()!=null
				&& statusFlagForMemberQuestionsView.getValue().equals("visible")) {
			statusFlag = true; 
		}
		if(statusFlag.equals(true) && sessionEndDateFlag.equals(true)) {
			model.addAttribute("member_questions_view_status_flag", "status_visible");
		}
		
		Boolean visibilityFlagForAdmitted = false;
		CustomParameter visibilityFlagForMemberAdmittedQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_ADMITTED_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
		if(visibilityFlagForMemberAdmittedQuestionsView!=null && visibilityFlagForMemberAdmittedQuestionsView.getValue()!=null
				&& visibilityFlagForMemberAdmittedQuestionsView.getValue().equals("visible")) {
			visibilityFlagForAdmitted = true; 
		}
		if(visibilityFlagForAdmitted.equals(true) && sessionEndDateFlag.equals(true)) {
			model.addAttribute("member_admitted_questions_view_flag", "admitted_visible");
		}
		
		Boolean visibilityFlagForRejected = false;
		CustomParameter visibilityFlagForMemberRejectedQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_REJECTED_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
		if(visibilityFlagForMemberRejectedQuestionsView!=null && visibilityFlagForMemberRejectedQuestionsView.getValue()!=null
				&& visibilityFlagForMemberRejectedQuestionsView.getValue().equals("visible")) {
			visibilityFlagForRejected = true; 
		}
		if(visibilityFlagForRejected.equals(true) && sessionEndDateFlag.equals(true)) {
			model.addAttribute("member_rejected_questions_view_flag", "rejected_visible");
		}
		
		Boolean visibilityFlagForUnstarred = false;
		CustomParameter visibilityFlagForMemberUnstarredQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_UNSTARRED_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
		if(visibilityFlagForMemberUnstarredQuestionsView!=null && visibilityFlagForMemberUnstarredQuestionsView.getValue()!=null
				&& visibilityFlagForMemberUnstarredQuestionsView.getValue().equals("visible")) {
			visibilityFlagForUnstarred = true; 
		}
		if(visibilityFlagForUnstarred.equals(true) && sessionEndDateFlag.equals(true)) {
			model.addAttribute("member_unstarred_questions_view_flag", "unstarred_visible");
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
					   String selectedStatusId = request.getParameter("status");
					   if(selectedStatusId!=null && !selectedStatusId.isEmpty()) {
						   Status selectedStatus = Status.findById(Status.class, Long.parseLong(selectedStatusId));
						   if(selectedStatus!=null && selectedStatus.getType().equals(ApplicationConstants.QUESTION_COMPLETE)) {
							   newUrlPattern=urlPattern+"_readyToSubmit?usergroup=member&houseType="+houseType;
						   }
					   }
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
				 throw new ELSException("StarredQuestionController.modifyURLPattern/4", 
							"QIS_GRID_ALLOWED_FOR key is not set as CustomParameter");
			 }
		 }
		 else{
			 throw new ELSException("StarredQuestionController.modifyURLPattern/4", 
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
			throw new ELSException("StarredQuestionController.populatenew/5", 
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
				throw new ELSException("StarredQuestionController.populatenew/5", 
						"Role is Not set");
			}
		}
		
		//Populate UserGroup
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
				throw new ELSException("StarredQuestionController.populatenew/5", 
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
				throw new ELSException("StarredQuestionController.populatenew/5", 
						"UserGroup is Not set");
			}
		}
		
		
		//Populate Primary Member
		String primaryMember = null;
		Member member = null;
		if(strRole.startsWith("MEMBER")) {
			member = QuestionController.populateMember(model, authUser, locale);
			if(member == null){
				throw new ELSException("StarredQuestionController.populatenew/5", 
						"The Current User is Not member");
			}
			primaryMember = member.getFullname();
			Constituency constituency = Member.findConstituency(member, new Date());
			if(constituency != null){
				model.addAttribute("constituency", constituency.getDisplayName());
			}
		}
		
		
		/**** Ministries,Departments,Sub Departments,Groups,Answering Dates Starts 
		 * a.Starred questions according to rotation order and will have answering dates
		****/
		
		// Populate Rotation Order Publishing Date
		Date rotationOrderPubDate = QuestionController.getRotationOrderPublishingDate(selectedSession);
		model.addAttribute("rotationOrderPublishDate", 
				FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
	
		if(rotationOrderPubDate == null) {
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"rotationOrderPubDate is null");
		}
		
		//Populate Ministries
		Date currentDate = new Date();
		if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
			List<Ministry> ministries = Ministry.
					findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
			model.addAttribute("ministries",ministries);
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
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"highestquestionprioritynotset");
		}
		
		//Submission Priority
		model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		int currentReadyToSubmitCount = Question.findReadyToSubmitCount(selectedSession, member, questionType, locale);
		model.addAttribute("submissionPriorityMaximum", currentReadyToSubmitCount+1);
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale);
		
		/*** Populate Saved Supporting member***/
		//Populate Supporting Member Names
//		Member member = Member.findMember(authUser.getFirstName(), authUser.getMiddleName(),
//				authUser.getLastName(), authUser.getBirthDate(), locale);
		String supportingMemberNames = MemberOtherController.getDelimitedMemberSupportingMembers(questionType, member, selectedSession, locale, usergroupType);
		model.addAttribute("supportingMembersName", supportingMemberNames);
		
		//Populate Supporting Members 
		List<MemberSupportingMember> suppMembers = MemberSupportingMember.getMemberSupportingMemberRepository().findMemberSupportingMember(questionType, member, selectedSession, locale);

		List<Member> supportingMembers = new ArrayList<Member>();
		for(MemberSupportingMember sm : suppMembers){
	
				Member supportingMember = sm.getSupportingMember();
				if(supportingMember.isActiveMemberOn(new Date(), locale)){
					supportingMembers.add(supportingMember);
				}
		
			
		}
		model.addAttribute("supportingMembers", supportingMembers);
		model.addAttribute("savedMemberSupportingMembers", supportingMembers);

		//Populate PrimaryMemberName + supportingMemberNames
		String memberNames = member.getFullname() + "," + supportingMemberNames;
		model.addAttribute("memberNames",memberNames);
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
		// Check for duplicate numbers if typist is entering the question
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
				if(domain.getSupportingMembers()==null && 
						domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers","SupportingMembersEmpty");
				} 
			}
			else if(operation.equals("submit") 
					&& (role.equals("MEMBER_LOWERHOUSE") || role.equals("MEMBER_UPPERHOUSE") || 
							role.equals("QIS_TYPIST"))){
					
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
										}else if(submissionStartLimitDate == null){
											result.rejectValue("version","SubmissionStartDateNotConfigured","Submission Start Date not Configured by the branch for this session");
										}
									}else{
										result.rejectValue("version","SubmissionStartDateNotConfigured","Submission Start Date not Configured by the branch for this session");
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
										}else if(submissionEndLimitDate == null){
											result.rejectValue("version","SubmissionEndDateNotConfigured","Submission End Date not Configured by the branch for this session");
										}
									}else{
										result.rejectValue("version","SubmissionEndDateNotConfigured","Submission End Date not Configured by the branch for this session");
									}
									break;
								}
							}								
						}
					}
				}				
				//batch submission date limit validations for council (configurable through custom parameters)
				if(domain.getSession()!=null && domain.getType()!=null 
						&& domain.getHouseType()!=null && domain.getSession().getParameter(domain.getType().getType()+"_processingMode").equals(ApplicationConstants.UPPER_HOUSE)) {
				
					//submission start date limit validation
					CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
					if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
						String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
						if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
							String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
							for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
								if(dt.trim().equals(domain.getType().getType().trim())) {
									
									Integer batch = Question.findBatch(domain, new Date());
									String strSubDate = null;
									
									if(batch.equals(1)){
										if(!Question.allowedInFirstBatch(domain, new Date())){
											strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "submissionFirstBatchStartDate");
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Question cannot be submitted before " + strSubDate);
											break;
										}
										if(!Question.allowedInFirstBatchForMaxCountPerMember(domain)){
											String strSubmissionFirstBatchMaxCountPerMember = domain.getSession().getParameter(domain.getType().getType() + "_" + "submissionFirstBatchMaxCountPerMember");
											if(strSubmissionFirstBatchMaxCountPerMember==null || strSubmissionFirstBatchMaxCountPerMember.isEmpty()) {
												strSubmissionFirstBatchMaxCountPerMember = "31";
											}
											result.rejectValue("version","SubmissionNotAllowedPostFirstBatchMaxCountPerMember","Question cannot be submitted as " + strSubmissionFirstBatchMaxCountPerMember + " questions are already submitted by the member for the first batch");
											break;
										}
									}
									
									if(batch.equals(2)){
										if(!Question.allowedInSecondBatch(domain, new Date())){
											strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "submissionSecondBatchStartDate");
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Question cannot be submitted before " + strSubDate);
											break;
										}
									}
									
									if(batch.equals(0)){
										result.rejectValue("version","SubmissionNotAllowed_batch","Question cannot be submitted.");
										break;
									}
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
			final AuthUser authUser) throws ELSException, ParseException {
		request.getSession().setAttribute("questionType", request.getParameter("questionType"));
		//Populate Primary Member
		
		model.addAttribute("role", request.getParameter("role")); 
		
		Member primaryMember = domain.getPrimaryMember();
		if(primaryMember != null) {
			model.addAttribute("formattedPrimaryMember", primaryMember.getFullname());
			model.addAttribute("primaryMember", primaryMember.getId());
			Constituency constituency = Member.findConstituency(primaryMember, new Date());
			if(constituency != null){
				model.addAttribute("constituency", constituency.getDisplayName());
			}
		}
		
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
		HouseType houseType = domain.getHouseType();
		if(houseType != null) {
			model.addAttribute("formattedHouseType", houseType.getName());
			model.addAttribute("houseTypeType", houseType.getType());
			model.addAttribute("houseType", houseType.getId());
		}
		
		//populate DeviceType
		DeviceType questionType = domain.getType();
		model.addAttribute("formattedQuestionType", questionType.getName());
		model.addAttribute("questionType", questionType.getId());
		model.addAttribute("deviceType", questionType.getId());
		model.addAttribute("selectedQuestionType", questionType.getType());
		
		//Populate Session
		Session selectedSession = domain.getSession();
		if(selectedSession != null){
			model.addAttribute("session", selectedSession.getId());
		}else{
			throw new ELSException("StarredQuestionController.populatenew/5", 
					"session_isnull");
		}
		
		//populate Session Year
		Integer sessionYear = selectedSession.getYear();
		model.addAttribute("sessionYear", sessionYear);
		
		//populate sessionType
		SessionType sessionType = selectedSession.getType();
		model.addAttribute("sessionType", sessionType.getId());
		
		
		// Populate Rotation Order Publishing Date
		Date rotationOrderPubDate = QuestionController.getRotationOrderPublishingDate(selectedSession);
		model.addAttribute("rotationOrderPublishDate", 
				FormaterUtil.getDateFormatter(domain.getLocale()).format(rotationOrderPubDate));
	
		if(rotationOrderPubDate == null) {
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"rotationOrderPubDate is null");
		}
		
		//Populate Ministries
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
					
			//Populate Group
			Group group = domain.getGroup();
			if(group != null) {
				model.addAttribute("formattedGroup",
						FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(group.getNumber()));
				model.addAttribute("group",group.getId());
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
			//populate original subdepartment
			SubDepartment originalSubDepartment = domain.getOriginalSubDepartment();
			if(originalSubDepartment != null) {
				model.addAttribute("originalSubDepartment", originalSubDepartment.getId());
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
			//populate original answering date
			QuestionDates originalAnsweringDate = domain.getOriginalAnsweringDate();
			if(originalAnsweringDate != null) {
				model.addAttribute("originalAnsweringDate", originalAnsweringDate.getId());
			}
		}
		
		//Populate Priorities
		CustomParameter customParameter = CustomParameter.
				findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter != null) {
			List<MasterVO> priorities = new ArrayList<MasterVO>();
			Integer priority = Integer.parseInt(customParameter.getValue());
			for(int i = 1;i <= priority;i++){
				String localizedPriority = FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(i);
				priorities.add(new MasterVO(i,localizedPriority));
			}
			model.addAttribute("priorities",priorities);
		}else{
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"highestquestionprioritynotset");
		}
		
		//Submission Priority
		model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		int currentReadyToSubmitCount = Question.findReadyToSubmitCount(selectedSession, domain.getPrimaryMember(), domain.getOriginalType(), domain.getLocale());
		model.addAttribute("submissionPriorityMaximum", currentReadyToSubmitCount+1);
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", domain.getLocale());
	}


	public static void populateCreateIfNoErrors(final Question domain,
			final ModelMap model, 
			final HttpServletRequest request, 
			final AuthUser authUser) {
		
		String operation = request.getParameter("operation");
		//set Edited As
		UserGroupType userGroupType = QuestionController.getUserGroupType(request,domain.getLocale());
		if(userGroupType !=null ){
			domain.setEditedAs(userGroupType.getName());
		}
		// Check for the operation and the required fields
		if(domain.getHouseType()!=  null && domain.getSession()!= null &&  domain.getType() != null 
			&& domain.getPrimaryMember() != null && domain.getMinistry() != null && domain.getGroup() != null 
			&& (!domain.getSubject().isEmpty()) && (!domain.getQuestionText().isEmpty())){
			if(operation != null && !operation.isEmpty() 
				&& operation.trim().equals("submit")){

				//set submission Date
				if(domain.getSubmissionDate() == null){
					domain.setSubmissionDate(new Date());
				}
//				//set original sub-department
//				if(domain.getSubDepartment()!=null){
//					domain.setOriginalSubDepartment(domain.getSubDepartment());
//				}
//				//set original answering date
//				if(domain.getAnsweringDate()!=null){
//					domain.setOriginalAnsweringDate(domain.getAnsweringDate());
//				}
				
				//set Supporting member			
				if(domain.getSupportingMembers() != null && !domain.getSupportingMembers().isEmpty()){
					List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
					Status timeoutStatus = Status.findByType(
							ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, domain.getLocale());
					for(SupportingMember i:domain.getSupportingMembers()){
						String decisionStatusType = i.getDecisionStatus().getType().trim();
						if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
							supportingMembers.add(i);
						}else{
							/**** Update Supporting Member ****/
							if(i.getDecisionStatus().getType().equals(
									ApplicationConstants.SUPPORTING_MEMBER_NOTSEND) ||
									i.getDecisionStatus().getType().equals(
											ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
								i.setDecisionStatus(timeoutStatus);
								i.setApprovalDate(new Date());	
								i.setApprovedText(domain.getQuestionText());
								i.setApprovedSubject(domain.getSubject());
								i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
								
								String strWorkflowdetails = i.getWorkflowDetailsId();
								if(strWorkflowdetails != null && ! strWorkflowdetails.isEmpty()) {
									WorkflowDetails workflowDetails = WorkflowDetails.findById(
											WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
									workflowDetails.setStatus("TIMEOUT");
									workflowDetails.setCompletionTime(new Date());
									workflowDetails.merge();

//									/**** Complete Task ****/
//									String strTaskId = workflowDetails.getTaskId();
//									Task task = processService.findTaskById(strTaskId);
//									processService.completeTask(task);
								}
							}
							
							if(! i.getDecisionStatus().getType().equals(
									ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
								supportingMembers.add(i);
							}
							
						}
					}
					domain.setSupportingMembers(supportingMembers);
				}
			
				// set internalstatus, recommendation status, status
				Status newstatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
				domain.setStatus(newstatus);
				domain.setInternalStatus(newstatus);
				domain.setRecommendationStatus(newstatus);
				
			}
			else{
				//set internalstatus, recommendation status, status
				Status status = Status.findByType(ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}else{
			//set internalstatus, recommendation status, status
			Status status = Status.findByType(ApplicationConstants.QUESTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}

		//set Creation Date, created by, edited on , edited by
		domain.setCreationDate(new Date());
		domain.setCreatedBy(authUser.getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(authUser.getActualUsername());
		
		//set submission priority to default value if not set explicitly
		if(domain.getSubmissionPriority()==null) {
			domain.setSubmissionPriority(ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		}		
	}


	public static void populateAfterCreate(Question domain, ModelMap model,
			HttpServletRequest request, AuthUser authUser, IProcessService processService) {
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
				ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);
				List<Task> tasks = processService.getCurrentTasks(processInstance);
				List<WorkflowDetails> workflowDetails = new ArrayList<WorkflowDetails>();
				try {
					workflowDetails = WorkflowDetails.
							create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"0");
					Question question = Question.
							findById(Question.class,domain.getId());
					List<SupportingMember> supportingMembers = question.getSupportingMembers();
					Status status = Status.
							findByType(ApplicationConstants.SUPPORTING_MEMBER_PENDING, domain.getLocale());
					StringBuffer supportingMembersUserNames = new StringBuffer("");
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
	
	public static String modifyEditUrlPattern(String editUrlPattern,
			HttpServletRequest request, ModelMap model, AuthUser authUser,
			String locale) {
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
	


	public static void populateEdit(final Question domain,
			final ModelMap model,
			final HttpServletRequest request,
			final AuthUser authUser) throws ELSException, ParseException {
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
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"highestquestionprioritynotset");
		}		
		//populate selected Priority
		if(domain.getPriority()!=null){
			model.addAttribute("priority",domain.getPriority());
			model.addAttribute("formattedPriority",FormaterUtil.
					getNumberFormatterNoGrouping(locale).format(domain.getPriority()));
		}
		/**** Priorities Ends  ****/
		
		//Submission Priority
		model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		int currentReadyToSubmitCount = Question.findReadyToSubmitCount(selectedSession, member, domain.getOriginalType(), locale);
		model.addAttribute("submissionPriorityMaximum", currentReadyToSubmitCount+1);
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale);

		// Populate Rotation Order Publishing Date
		Date rotationOrderPubDate = QuestionController.getRotationOrderPublishingDate(selectedSession);
		model.addAttribute("rotationOrderPublishDate", 
				FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
	
		if(rotationOrderPubDate == null) {
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"rotationOrderPubDate is null");
		}
				
		//Populate Ministries
		Date currentDate = new Date();
		if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
			List<Ministry> ministries = Ministry.
					findMinistriesAssignedToGroups(houseType, selectedSession.getYear(), selectedSession.getType(), locale);
			model.addAttribute("ministries",ministries);
			
			//Populate Ministry
			Ministry ministry = domain.getMinistry();
			if(ministry != null){
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
				List<SubDepartment> subDepartments = MemberMinister.
						findAssignedSubDepartments(ministry, selectedSession.getEndDate(), locale);
				model.addAttribute("subDepartments",subDepartments);
			}
			
			//populate subdepartment
			SubDepartment subDepartment = domain.getSubDepartment();
			if(subDepartment != null) {
				model.addAttribute("subDepartmentSelected",subDepartment.getId());
			}
			//populate original subdepartment
			SubDepartment originalSubDepartment = domain.getOriginalSubDepartment();
			if(originalSubDepartment != null) {
				model.addAttribute("originalSubDepartment", originalSubDepartment.getId());
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
			//populate original answering date
			QuestionDates originalAnsweringDate = domain.getOriginalAnsweringDate();
			if(originalAnsweringDate != null) {
				model.addAttribute("originalAnsweringDate", originalAnsweringDate.getId());
			}
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
		
		
		//populate Ballot Status to prevent the ballotstatus from getting empty
		//while typing answers from main page
		if(domain.getBallotStatus()!=null){
			model.addAttribute("ballotStatusId", domain.getBallotStatus().getId());
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
		if(domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)
				&& !usergroupType.equals(ApplicationConstants.MEMBER)
				&& !usergroupType.equals(ApplicationConstants.TYPIST)
				&& (domain.getDrafts()==null || domain.getDrafts().isEmpty())) {
			domain.addQuestionDraftForMembersideSubmission();
			domain.simpleMerge();
		} else if(domain.getNumber()!=null 
				&& (domain.getDrafts()==null || domain.getDrafts().isEmpty())) {
			List<QuestionDraft> drafts = QuestionDraft.findAllByFieldName(QuestionDraft.class, "questionId", domain.getId(), "id", ApplicationConstants.ASC, domain.getLocale());
			if(drafts!=null && !drafts.isEmpty()) {
				domain.setDrafts(new LinkedHashSet<QuestionDraft>(drafts));
				domain.simpleMerge();
			} else {
				//create submission draft from original fields
				domain.addMissingSubmissionDraft();
				domain.simpleMerge();
			}
		} else if(domain.getNumber()!=null 
				&& Question.isSubmissionDraftAbsentForQuestion(domain)) {
			//create submission draft from original fields
			domain.addMissingSubmissionDraft();
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
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				//populate PUT UP OPTIONS
				if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_SUBMIT)
					&& !internalStatus.getType().equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)){
					if(recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
						QuestionController.
						populateInternalStatus(model,recommendationStatus.getType(),usergroupType,locale,deviceType.getType());
					} else if(internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
							&& recommendationStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
						CustomParameter specificClubbedDeviceStatusUserGroupStatuses = CustomParameter.
								findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_CLUBBED_"+deviceType.getType().toUpperCase()
										+"_"+internalStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(),"");
						List<Status> internalStatuses = Status.
								findStatusContainedIn(specificClubbedDeviceStatusUserGroupStatuses.getValue(), locale);
						model.addAttribute("internalStatuses", internalStatuses);
					}else {
						QuestionController.
						populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
					}					
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
			}else if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("clerk")){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				/*if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_SUBMIT)
					&& !internalStatus.getType().equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)){
					QuestionController.
					populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
				}*/
			}else if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("section_officer")){
				if(domain.getOriginalType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					QuestionController.populateInternalStatus(model,ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP,usergroupType,locale,deviceType.getType());
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
				model.addAttribute("referencedQuestions", refentities);
				
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
			if(internalStatus.getType().equals(ApplicationConstants.QUESTION_COMPLETE)
				|| internalStatus.getType().equals(ApplicationConstants.QUESTION_INCOMPLETE)){
				Member supportingMember = sm.getMember();
				if(supportingMember.isActiveMemberOn(new Date(), locale)){
					supportingMembers.add(supportingMember);
				}
			}else{
				if(sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
					Member supportingMember = sm.getMember();
					if(supportingMember.isActiveMemberOn(new Date(), locale)){
						supportingMembers.add(supportingMember);
					}
				}
			}
			
		}
		model.addAttribute("supportingMembers", supportingMembers);
		
		//Populate PrimaryMemberName + supportingMemberNames
		String memberNames = domain.getPrimaryMember().getFullname() + "," + supportingMemberNames;
		model.addAttribute("memberNames",memberNames);
		
		//Populate Parent
		if(domain.getParent()!=null){
			model.addAttribute("formattedParentNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
			model.addAttribute("parent",domain.getParent().getId());
			// Populate latest revised question text from parent question
			String latestRevisedQuestionTextFromParentQuestion = domain.getParent().getRevisedQuestionText();
			model.addAttribute("latestRevisedQuestionTextFromParentQuestion",latestRevisedQuestionTextFromParentQuestion);
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
		/** find if previous session unstarred question is parent and show its details including yaadi if present **/
		Question parentQuestion = domain.getParent();
		if(parentQuestion!=null
				&& parentQuestion.getType().getType().trim().equalsIgnoreCase(ApplicationConstants.UNSTARRED_QUESTION)
				&& domain.getSession().getStartDate().after(parentQuestion.getSession().getStartDate())) {
			String previousSessionUnstarredParentDetailsText = domain.findPreviousSessionUnstarredParentDetailsText();
			model.addAttribute("previousSessionUnstarredParentDetailsText", previousSessionUnstarredParentDetailsText);
		}
		/** populate discussion details text if question is discussed **/
		String discussionDetailsText = domain.findDiscussionDetailsText();
		model.addAttribute("discussionDetailsText", discussionDetailsText);
		
		/**** Populate flag for department change allowed for given actor ****/
		String processingMode = "";
		String sessionProcessingMode = domain.getSession().getParameter(domain.getType().getType()+"_processingMode");
		if(sessionProcessingMode!=null && !sessionProcessingMode.isEmpty()) {
			processingMode = sessionProcessingMode;
		} else {
			processingMode = domain.getHouseType().getType();
		}
		CustomParameter csptDepartmentChangeRestricted = CustomParameter.findByName(CustomParameter.class, domain.getOriginalType().getType().toUpperCase()+"_"+processingMode.toUpperCase()+"_"+userGroupType.getType().toUpperCase()+"_DEPARTMENT_CHANGE_RESTRICTED", locale);
		if(csptDepartmentChangeRestricted!=null && csptDepartmentChangeRestricted.getValue()!=null && csptDepartmentChangeRestricted.getValue().equals("YES")) {
			
			if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION) //allowed for questions converted to unstarred in previous sessions
					&& new Date().after(domain.getSession().getEndDate())) {
				
				model.addAttribute("departmentChangeRestricted", "NO");
			} else {
				model.addAttribute("departmentChangeRestricted", "YES");
			}			
		} else {
			model.addAttribute("departmentChangeRestricted", "NO");
		}
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
		if(domain.getHouseType()==null){
			result.rejectValue("houseType","HousetypeEmpty");
		}
		//Empty check for DeviceType
		if(domain.getType()==null){
			result.rejectValue("type","QuestionTypeEmpty");
		}
		//Empty check for session
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
		}
		//Empty check for PrimaryMember
		if(domain.getPrimaryMember()==null){
			result.rejectValue("primaryMember","PrimaryMemberEmpty");
		}
		//Empty check for Subject
		if(domain.getSubject().isEmpty()){
			result.rejectValue("subject","SubjectEmpty");
		}
		//Empty check for Question text
		if(domain.getQuestionText().isEmpty()){
			result.rejectValue("questionText","QuestionTextEmpty");
		}
		if(!role.equalsIgnoreCase("QIS_TYPIST")){
		// Empty check for Ministry
		if(domain.getMinistry()==null){
			result.rejectValue("ministry","MinistryEmpty");
		}
		// Empty check for Subdepartment
		if(domain.getSubDepartment()==null){
			result.rejectValue("subDepartment","SubDepartmentEmpty");
		}
		// Empty check for Group
		if(domain.getGroup() == null){
			result.rejectValue("group", "GroupEmpty");
		}
		}
		String operation = request.getParameter("operation");
		if(operation != null && !operation.isEmpty()){
			//Operation is supporting member approval
			if(operation.equals("approval")){
				// Empty check the supporting members
				if(domain.getSupportingMembers()==null || domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers","SupportingMembersEmpty");
				} else {
					//if the request for Supporting Member is already send then raise the error
					int count=0;
					for(SupportingMember i:domain.getSupportingMembers()){
						if(i.getDecisionStatus().getType().
								equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							count++;
						}
					}
					if(count==0){
						result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
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
				if(domain.getMinistry()==null){
					result.rejectValue("ministry","MinistryEmpty");
				}
				// Empty check for Subdepartment
				if(domain.getSubDepartment()==null){
					result.rejectValue("subDepartment","SubDepartmentEmpty");
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
										}else if(submissionStartLimitDate == null){
											result.rejectValue("version","SubmissionStartDateNotConfigured","Submission Start Date not Configured by the branch for this session");
										}
									}else{
										result.rejectValue("version","SubmissionStartDateNotConfigured","Submission Start Date not Configured by the branch for this session");
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
										}else if(submissionEndLimitDate == null){
											result.rejectValue("version","SubmissionEndDateNotConfigured","Submission End Date not Configured by the branch for this session");
										}
									}else{
										result.rejectValue("version","SubmissionEndDateNotConfigured","Submission End Date not Configured by the branch for this session");
									}
									break;
								}
							}								
						}
					}
				}
				//batch submission date limit validations for council (configurable through custom parameters)
				if(domain.getSession()!=null && domain.getType()!=null 
						&& domain.getHouseType()!=null && domain.getSession().getParameter(domain.getType().getType()+"_processingMode").equals(ApplicationConstants.UPPER_HOUSE)) {
				
					//submission start date limit validation
					CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
					if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
						String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
						if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
							String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
							for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
								if(dt.trim().equals(domain.getType().getType().trim())) {
									
									Integer batch = Question.findBatch(domain, new Date());
									String strSubDate = null;
									
									if(batch.equals(1)){
										if(!Question.allowedInFirstBatch(domain, new Date())){
											strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "submissionFirstBatchStartDate");
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Question cannot be submitted before " + strSubDate);
											break;
										}
										if(!Question.allowedInFirstBatchForMaxCountPerMember(domain)){
											String strSubmissionFirstBatchMaxCountPerMember = domain.getSession().getParameter(domain.getType().getType() + "_" + "submissionFirstBatchMaxCountPerMember");
											if(strSubmissionFirstBatchMaxCountPerMember==null || strSubmissionFirstBatchMaxCountPerMember.isEmpty()) {
												strSubmissionFirstBatchMaxCountPerMember = "31";
											}
											result.rejectValue("version","SubmissionNotAllowedPostFirstBatchMaxCountPerMember","Question cannot be submitted as " + strSubmissionFirstBatchMaxCountPerMember + " questions are already submitted by the member for the first batch");
											break;
										}
									}
									
									if(batch.equals(2)){
										if(!Question.allowedInSecondBatch(domain, new Date())){
											strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "submissionSecondBatchStartDate");
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Question cannot be submitted before " + strSubDate);
											break;
										}
									}
									
									if(batch.equals(0)){
										result.rejectValue("version","SubmissionNotAllowed_batch","Question cannot be submitted.");
										break;
									}
								}
							}								
						}
					}
				}
			}else if(operation.equals("startworkflow")){
				// Empty check for Ministry
				if(domain.getMinistry()==null){
					result.rejectValue("ministry","MinistryEmpty");
				}
				// Empty check for Subdepartment
				if(domain.getSubDepartment()==null){
					result.rejectValue("subDepartment","SubDepartmentEmpty");
				}
				// Empty check for Group
				if(domain.getGroup() == null){
					result.rejectValue("group", "GroupEmpty");
				}
				//Validation for Empty revisedQuestionText or RevisedSubject for Starred and unstarred
				if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
						||domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
					if(domain.getRevisedSubject() == null || domain.getRevisedSubject().equals("")){
						result.rejectValue("version", "revisedSubjectEmpty");
					}
					if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().equals("")){
						result.rejectValue("version", "revisedQuestionTextEmpty");
					}
				}
				
				/***Validation for Empty rejection reason for starred***/
				if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
					if(domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)){
						if(domain.getRejectionReason() == null || domain.getRejectionReason().equals("")){
							result.rejectValue("version", "rejectionReasonEmpty");
						}
					}
				}
			}
		}
		else {
			if(role.equals("QIS_TYPIST")){
				//Empty check for Number
				if(domain.getNumber()==null){
					result.rejectValue("number","NumberEmpty");
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
		// populate sessionType  and Year
		model.addAttribute("sessionYear", selectedSession.getYear());
		model.addAttribute("sessionType", selectedSession.getType().getId());
		
		 
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

		// Populate Rotation Order Publishing Date
		Date rotationOrderPubDate = QuestionController.getRotationOrderPublishingDate(selectedSession);
		model.addAttribute("rotationOrderPublishDate", 
				FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
	
		if(rotationOrderPubDate == null) {
			throw new ELSException("StarredQuestionController.populateNew/5", 
					"rotationOrderPubDate is null");
		}
				
		//Populate Ministries
		Date currentDate = new Date();
		if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
			List<Ministry> ministries = Ministry.
					findMinistriesAssignedToGroups(houseType, selectedSession.getYear(), 
							selectedSession.getType(), locale);
			model.addAttribute("ministries", ministries);
			
			//Populate Ministry
			Ministry ministry = domain.getMinistry();
			if(ministry != null){
				model.addAttribute("ministrySelected", ministry.getId());
				model.addAttribute("formattedMinistry", ministry.getName());
			}
			
			
			//Populate Group
			Group group = domain.getGroup();
			if(group != null) {
				model.addAttribute("formattedGroup",
						FormaterUtil.getNumberFormatterNoGrouping(locale).format(group.getNumber()));
				model.addAttribute("group", group.getId());
			}
			
			//Populate SubDepartments
			if(ministry != null) {
				List<SubDepartment> subDepartments = MemberMinister.
						findAssignedSubDepartments(ministry, selectedSession.getEndDate(), locale);
				model.addAttribute("subDepartments", subDepartments);
			}
			
			//populate subdepartment
			SubDepartment subDepartment = domain.getSubDepartment();
			if(subDepartment != null) {
				model.addAttribute("subDepartmentSelected", subDepartment.getId());
			}
			//populate original subdepartment
			SubDepartment originalSubDepartment = domain.getOriginalSubDepartment();
			if(originalSubDepartment != null) {
				model.addAttribute("originalSubDepartment", originalSubDepartment.getId());
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
			//populate original answering date
			QuestionDates originalAnsweringDate = domain.getOriginalAnsweringDate();
			if(originalAnsweringDate != null) {
				model.addAttribute("originalAnsweringDate", originalAnsweringDate.getId());
			}
		}
	
		// populate Charting Answering Date
		if(domain.getChartAnsweringDate() != null){
			QuestionDates chartAnsweringDate = domain.getChartAnsweringDate();
			model.addAttribute("formattedChartAnsweringDate",FormaterUtil.
					getDateFormatter(locale).format(chartAnsweringDate.getAnsweringDate()));
			model.addAttribute("chartAnsweringDate", chartAnsweringDate.getId());
		}
		
		
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
					model.addAttribute("submissionDate",FormaterUtil.
							getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
					model.addAttribute("formattedSubmissionDate",FormaterUtil.
							getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
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
		Question question = Question.findById(Question.class, domain.getId());
		Status status = domain.getStatus();
		
		Status internalStatus = domain.getInternalStatus();
		if(!internalStatus.equals(question.getInternalStatus())){
			internalStatus = question.getInternalStatus();
		}
		
		Status recommendationStatus = domain.getRecommendationStatus();
		if(!recommendationStatus.equals(question.getRecommendationStatus())){
			recommendationStatus = question.getRecommendationStatus();
		}
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
				if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_SUBMIT)
					&& !internalStatus.getType().equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)){
					if(recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
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
				if(!internalStatus.getType().equals(ApplicationConstants.QUESTION_SUBMIT)
					&& !internalStatus.getType().equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)){
					//QuestionController.
					//populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
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
			if(internalStatus.getType().equals(ApplicationConstants.QUESTION_COMPLETE)
				|| internalStatus.getType().equals(ApplicationConstants.QUESTION_INCOMPLETE)){
				Member supportingMember = sm.getMember();
				if(supportingMember.isActiveMemberOn(new Date(), locale)){
					supportingMembers.add(supportingMember);
				}
			}else{
				if(sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
					Member supportingMember = sm.getMember();
					if(supportingMember.isActiveMemberOn(new Date(), locale)){
						supportingMembers.add(supportingMember);
					}
				}
			}
			
		}
		model.addAttribute("supportingMembers", supportingMembers);
		
		//Populate PrimaryMemberName + supportingMemberNames
		String memberNames = domain.getPrimaryMember().getFullname() + "," + supportingMemberNames;
		model.addAttribute("memberNames",memberNames);
		
		//Populate Parent
		if(domain.getParent()!=null){
			model.addAttribute("formattedParentNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
			model.addAttribute("parent",domain.getParent().getId());
			// Populate latest revised question text from parent question
			String latestRevisedQuestionTextFromParentQuestion = domain.getParent().getRevisedQuestionText();
			model.addAttribute("latestRevisedQuestionTextFromParentQuestion",latestRevisedQuestionTextFromParentQuestion);
		}
		

		//Populate Actors
		if(internalStatus != null){
			String internalStatusType = internalStatus.getType();			
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
//				if(actors!=null && !actors.isEmpty()){
//					String nextActor = actors.get(0).getId();
//					String[] actorArr = nextActor.split("#");
////					domain.setLevel(actorArr[2]);
////					domain.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
//				}
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
		/** find if previous session unstarred question is parent and show its details including yaadi if present **/
		Question parentQuestion = domain.getParent();
		if(parentQuestion!=null
				&& parentQuestion.getType().getType().trim().equalsIgnoreCase(ApplicationConstants.UNSTARRED_QUESTION)
				&& domain.getSession().getStartDate().after(parentQuestion.getSession().getStartDate())) {
			String previousSessionUnstarredParentDetailsText = domain.findPreviousSessionUnstarredParentDetailsText();
			model.addAttribute("previousSessionUnstarredParentDetailsText", previousSessionUnstarredParentDetailsText);
		}
		/** populate discussion details text if question is discussed **/
		String discussionDetailsText = domain.findDiscussionDetailsText();
		model.addAttribute("discussionDetailsText", discussionDetailsText);
		
		/**** Populate flag for department change allowed for given actor ****/
		String processingMode = "";
		String sessionProcessingMode = domain.getSession().getParameter(domain.getType().getType()+"_processingMode");
		if(sessionProcessingMode!=null && !sessionProcessingMode.isEmpty()) {
			processingMode = sessionProcessingMode;
		} else {
			processingMode = domain.getHouseType().getType();
		}
		CustomParameter csptDepartmentChangeRestricted = CustomParameter.findByName(CustomParameter.class, domain.getOriginalType().getType().toUpperCase()+"_"+processingMode.toUpperCase()+"_"+userGroupType.getType().toUpperCase()+"_DEPARTMENT_CHANGE_RESTRICTED", locale);
		if(csptDepartmentChangeRestricted!=null && csptDepartmentChangeRestricted.getValue()!=null && csptDepartmentChangeRestricted.getValue().equals("YES")) {
			
			if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION) //allowed for questions converted to unstarred in previous sessions
					&& new Date().after(domain.getSession().getEndDate())) {
				
				model.addAttribute("departmentChangeRestricted", "NO");
			} else {
				model.addAttribute("departmentChangeRestricted", "YES");
			}			
		} else {
			model.addAttribute("departmentChangeRestricted", "NO");
		}
	}


	public static void populateUpdateIfNoErrors(final Question domain,
			final ModelMap model,
			final HttpServletRequest request,
			final AuthUser authUser) {

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
//					//set original sub-department
//					if(domain.getSubDepartment()!=null){
//						domain.setOriginalSubDepartment(domain.getSubDepartment());
//					}
//					//set original answering date
//					if(domain.getAnsweringDate()!=null){
//						domain.setOriginalAnsweringDate(domain.getAnsweringDate());
//					}
					/**** Update Timed Out Supporting Members (can be disabled for starting hour of submission start time using custom parameter) ****/
					Status timeoutStatus = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, domain.getLocale());					
					CustomParameter csptTimeoutOfSupportingMembersDisabled = CustomParameter.findByName(CustomParameter.class, "QIS_SUPPORTINGMEMBERS_TIMEOUT_DISABLED", "");
					if(csptTimeoutOfSupportingMembersDisabled!=null 
							&& csptTimeoutOfSupportingMembersDisabled.getValue()!=null
							&& csptTimeoutOfSupportingMembersDisabled.getValue().equals("YES")) {
						System.out.println("Timeout of Pending/Unsent Supporting Members Disabled");
					} else {
						List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
						if(domain.getSupportingMembers() != null && !domain.getSupportingMembers().isEmpty()){						
							for(SupportingMember i:domain.getSupportingMembers()){
								if(userGroupType.getType().equals("typist")){
									supportingMembers.add(i);
								}else{
//									String decisionStatusType =i.getDecisionStatus().getType().trim();
//									if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
//										supportingMembers.add(i);
//									}else{
//										
//									}
									if(i.getDecisionStatus().getType().equals(
											ApplicationConstants.SUPPORTING_MEMBER_NOTSEND) ||
											i.getDecisionStatus().getType().equals(
													ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
										/**** Update Supporting Member ****/
										i.setDecisionStatus(timeoutStatus);
										i.setApprovalDate(new Date());	
										i.setApprovedText(domain.getQuestionText());
										i.setApprovedSubject(domain.getSubject());
										i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);

										/**** Update Workflow Details ****/
										String strWorkflowdetails = i.getWorkflowDetailsId();
										if(strWorkflowdetails != null && ! strWorkflowdetails.isEmpty()) {
											WorkflowDetails workflowDetails = WorkflowDetails.findById(
													WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
											workflowDetails.setStatus("TIMEOUT");
											workflowDetails.setCompletionTime(new Date());
											workflowDetails.merge();

											/**** Complete Task ****/
											//TODO 
											// Uncomment following code when refactoring the method by passing processService
//												String strTaskId = workflowDetails.getTaskId();
//												Task task = processService.findTaskById(strTaskId);
//												processService.completeTask(task);
										}
									}

									if(! i.getDecisionStatus().getType().equals(
											ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
										supportingMembers.add(i);
									}															
								}
							}
							domain.setSupportingMembers(supportingMembers);
						}
						//end pending supporting member tasks if removed manually by member
						Question.updateTimeoutSupportingMemberTasksForDevice(domain.getId(), new Date());
					}					
					// Set status, internalStatus, recommendationstatus
					Status newstatus=Status.findByType(ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
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
													q.setInternalStatus(domain.getInternalStatus());
													q.setRecommendationStatus(domain.getRecommendationStatus());
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
							findByType(ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
				
			}
		}
		// Required Fields are not entered
		else{
			// Set status, internalStatus, recommendationstatus
			Status status=Status.findByType(ApplicationConstants.QUESTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}

		// set Edited On and EditedBy
		domain.setEditedOn(new Date());
		domain.setEditedBy(authUser.getActualUsername());

		//set submission priority to default value if not set explicitly
		if(domain.getSubmissionPriority()==null) {
			domain.setSubmissionPriority(ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		}

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
				if((internalStatusType.equals(ApplicationConstants.QUESTION_SUBMIT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)) 
					&& domain.getMinistry()!=null 
					&& group!=null 
					&& domain.getSubDepartment()!=null) {
					Status ASSISTANT_PROCESSED = Status.
							findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
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
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat != null){
			SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate != null && !strSubmissionDate.isEmpty()){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
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
			catch (ParseException e) {
				e.printStackTrace();
			}
		}		
		
		/**** updating revised question text in related department workflow details when copy pasted by clerk/assistant from notepad ****/
		if(domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_ADMISSION)
				|| domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_CLARIFICATION_FROM_DEPARTMENT)) {
			Question question = Question.findById(Question.class, domain.getId());
			if(!question.getRevisedQuestionText().equals(domain.getRevisedQuestionText())) {
				List<WorkflowDetails> wdList = WorkflowDetails.findAllByFieldName(WorkflowDetails.class, "deviceId", domain.getId().toString(), "id", ApplicationConstants.DESC, domain.getLocale());
				if(wdList!=null && !wdList.isEmpty()) {
					for(WorkflowDetails wd: wdList) {
						if(wd.getWorkflowSubType()!=null && wd.getWorkflowSubType().equals(domain.getInternalStatus().getType())
								&& wd.getText()!=null && wd.getText().equals(question.getRevisedQuestionText())) {
							wd.setText(domain.getRevisedQuestionText());
							wd.merge();
						}
					}
				}
			}			
		}
		
		/**** reset localized actor name and level if the question is just saved by assistant instead of putup ****/
		if(domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_SYSTEM_ASSISTANTPROCESSED)
				|| domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_SYSTEM_PUTUP)) {
			domain.setActor(null);
			domain.setLocalizedActorName("");
			domain.setLevel("1");
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
		Boolean isGroupChanged = false;
		Boolean isMinistryChanged =false;
		Boolean isSubDepartmentChanged = false;
		Status submitStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
		if(domain.getInternalStatus().getPriority()>submitStatus.getPriority()) {
			// On Group Change
			String strGroupId = request.getParameter("oldgroup");
			String strMinistrySelected = request.getParameter("ministrySelected");
			String strSubDepartmentSelected = request.getParameter("subDepartmentSelected");
			Group oldGroup = null;
			Ministry oldMinistry = null;
			SubDepartment oldSubDepartment = null;
			if(strGroupId != null && !strGroupId.isEmpty()){
				oldGroup = Group.findById(Group.class, Long.parseLong(strGroupId));
			}
			if(strMinistrySelected != null & !strMinistrySelected.isEmpty()){
				oldMinistry = Ministry.findById(Ministry.class, Long.parseLong(strMinistrySelected));
			}
			if(strSubDepartmentSelected != null && !strSubDepartmentSelected.isEmpty()){
				oldSubDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubDepartmentSelected));
			}
//			Group fromGroup = Question.isGroupChanged(question);
//			if(fromGroup != null) {
//				Question.onGroupChange(question, fromGroup);
//			}
			if(oldGroup != null && !oldGroup.equals(domain.getGroup())){
				Question.onGroupChange(question, oldGroup);
				isGroupChanged = true;
				//SEND NOTIFICATION FOR DEPARTMENT CHANGE ACROSS GROUPS
				String prevDeptId = request.getParameter("subDepartmentSelected");
				if(prevDeptId!=null) {
					SubDepartment prevDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(prevDeptId));
					SubDepartment currentDepartment = question.getSubDepartment();
					if(prevDepartment!=null	&& currentDepartment!=null
							&& !prevDepartment.getId().equals(currentDepartment.getId())) {
						String usergroupTypes = "assistant,clerk";						
						NotificationController.sendDepartmentChangeNotification(question.getNumber().toString(), question.getType(), question.getHouseType(), prevDepartment.getName(), currentDepartment.getName(), usergroupTypes, question.getLocale());
					}
				}				
			}else if(oldMinistry != null && !oldMinistry.equals(domain.getMinistry())){
				Question.onMinistryChange(question, oldMinistry);
				isMinistryChanged = true;
			}else if(oldSubDepartment != null && !oldSubDepartment.equals(domain.getSubDepartment())){
				Question.onSubdepartmentChange(question, oldSubDepartment);
				isSubDepartmentChanged = true;
			}
			
			// Add to Chart
			Chart.addToChart(question);
		}	
		
		/**** Supporting Member Workflow/Put Up Workflow ****/
		String operation=request.getParameter("operation");
		if(operation!=null && !operation.isEmpty()){
			if(operation.equals("approval")){
				ProcessDefinition processDefinition = processService.
						findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
				Map<String,String> properties = new HashMap<String, String>();
				properties.put("pv_deviceId", String.valueOf(question.getId()));
				properties.put("pv_deviceTypeId", question.getType().getType());
				ProcessInstance processInstance = processService.
						createProcessInstance(processDefinition, properties);
				List<Task> tasks = processService.getCurrentTasks(processInstance);					
				List<WorkflowDetails> workflowDetails = WorkflowDetails.
						create(question,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"");
				//Question question = Question.findById(Question.class,domain.getId());
				List<SupportingMember> supportingMembers = question.getSupportingMembers();
				Status status=Status.
						findByType(ApplicationConstants.SUPPORTING_MEMBER_PENDING, domain.getLocale());
				StringBuffer supportingMembersUserNames = new StringBuffer("");
				for(SupportingMember i:supportingMembers){
					String decisionStatusType = i.getDecisionStatus().getType();
					if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){						
						//Update Supporting Member Domain
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
						/** save supporting member usernames for sending notification **/
						supportingMembersUserNames.append(credential.getUsername());
						supportingMembersUserNames.append(",");
					}
				}
				//SEND NOTIFICATION FOR NEW SUPPORTING MEMBER APPROVAL REQUESTS
				if(!supportingMembersUserNames.toString().isEmpty()) {
					NotificationController.sendSupportingMemberApprovalNotification(domain.getSubject(), domain.getType(), domain.getPrimaryMember(), supportingMembersUserNames.toString(), domain.getLocale());
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
					properties.put("pv_deviceId", String.valueOf(question.getId()));
					properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
					ProcessInstance processInstance= processService.
							createProcessInstance(processDefinition, properties);
					//Question question=Question.findById(Question.class,domain.getId());
					Task task= processService.getCurrentTask(processInstance);
					if(endflag!=null && !endflag.isEmpty() ){
						if(endflag.equals("continue")){
							Workflow workflow = null;//question.findWorkflowFromStatus();	
							if(domain.getParent() != null && domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTOSECTIONOFFICER)){
								 workflow = Workflow.findByType(ApplicationConstants.QUESTION_SUPPLEMENTARY_WORKFLOW, domain.getLocale());	
							}else{
								 workflow = question.findWorkflowFromStatus();	
							}
													
							WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
							question.setWorkflowDetailsId(workflowDetails.getId());
						}
					}
					question.setWorkflowStarted("YES");
					question.setWorkflowStartedOn(new Date());
					question.setTaskReceivedOn(new Date());
					question.simpleMerge();
			}
		}else{
			if(!isGroupChanged && !isSubDepartmentChanged && !isMinistryChanged){
				// If branch users add the answer from their end instead of from department, then the workflow will end of the department side
				if(domain.getAnswer() != null && !domain.getAnswer().isEmpty()){
					WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(domain);
					if(wd != null){
						if(wd.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
								||wd.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
							WorkflowDetails.endProcess(wd);
							Status answerAcceptanceStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_ANSWER_RECEIVED, domain.getLocale());
							question.setRecommendationStatus(answerAcceptanceStatus);
							question.removeExistingWorkflowAttributes();
						}
					}
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
			if(status.getType().equals(ApplicationConstants.QUESTION_INCOMPLETE)
					||status.getType().equals(ApplicationConstants.QUESTION_COMPLETE)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
	}
	
	public static String determineOrderingForSubmissionInit(HttpServletRequest request,
			ModelMap model, AuthUser authUser, Locale locale) throws ELSException {
		
		HouseType houseType = QuestionController.getHouseType(request, locale.toString());
		SessionType sessionType = QuestionController.getSessionType(request, locale.toString());
		Integer sessionYear = QuestionController.stringToIntegerYear(request, locale.toString());
		DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
		Member primaryMember = Member.findMember(authUser.getFirstName(),
				authUser.getMiddleName(), authUser.getLastName(), authUser.getBirthDate(), locale.toString());
		
		Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
		List<Question> questions = new ArrayList<Question>();
		if(session != null){
			if(primaryMember != null){
				questions = Question.findReadyToSubmitQuestions(session, primaryMember, deviceType, locale.toString());
			}
		}
		
		model.addAttribute("houseType", houseType.getId());
		model.addAttribute("questionType", deviceType.getId());
		model.addAttribute("deviceType", deviceType.getId());
		model.addAttribute("questions", questions);
		model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		model.addAttribute("locale", locale.toString());
		model.addAttribute("formater", new FormaterUtil());
		
		return "question/orderingforsubmission";
	}
	
	public static String determineOrderingForSubmission(final HttpServletRequest request,
			final ModelMap model,
			final AuthUser authUser,
			final IProcessService processService, 
			final Locale locale) {
		String retVal = "question/error";
		String selectedItems = request.getParameter("items");
		if(selectedItems != null && ! selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");
			List<Question> questions = new ArrayList<Question>();
			for(String i : items) {				
				Long id = Long.parseLong(i.split("_")[0]);
				Question question = Question.findById(Question.class, id);
				if(question!=null) {
					question.setSubmissionPriority(Integer.parseInt(i.split("_")[1]));
					question.simpleMerge();
					questions.add(question);
				}
			}
			questions = Question.sortBySubmissionPriority(questions, ApplicationConstants.ASC);
			model.addAttribute("questions", questions);
			model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			model.addAttribute("type","success");
			
			retVal = "question/orderingforsubmissionack";
		}
		return retVal;
	}

	public static String getBulkSubmissionView(HttpServletRequest request,
			ModelMap model, AuthUser authUser, Locale locale) throws ELSException {
		
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
				questions = Question.findReadyToSubmitQuestions(session, primaryMember, deviceType, itemCount, locale.toString());
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
		
		if(questions!=null && !questions.isEmpty()) {			
			Question question = questions.get(0);
			if(question.getSession()!=null && question.getType()!=null 
					&& question.getHouseType()!=null && question.getSession().getParameter(question.getType().getType()+"_processingMode").equals(ApplicationConstants.UPPER_HOUSE)) {
				Integer batch = Question.findBatch(question, new Date());
				if(batch.equals(1)){
					if(!Question.allowedInFirstBatchForMaxCountPerMember(question)) {
						model.addAttribute("errorcode", "FIRST_BATCH_MAX_COUNT_OF_SUBMISSION_REACHED");
						return "question/error";
					}
				}				
			}			
		}
		return "question/bulksubmission";
	
	}

			
//	public static String bulkSubmission(final HttpServletRequest request,
//			final ModelMap model,
//			final AuthUser authUser,
//			final IProcessService processService, 
//			final Locale locale) {
//		String selectedItems = request.getParameter("items");
//		if(selectedItems != null && ! selectedItems.isEmpty()) {
//			String[] items = selectedItems.split(",");
//
//			//validation to check if any question is allowed for submission at the moment 
//			{
//				boolean validationBeforeStartDate = false;
//				boolean validationAfterEndDate = false;
//				boolean validationForBatch = false;
//				Question domain = Question.findById(Question.class, new Long(items[0]));
//				
//				if(domain.getSession()!=null && domain.getType()!=null) {
//					//submission start date limit validation
//					CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
//					if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
//						String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
//						if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
//							String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
//							for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
//								if(dt.trim().equals(domain.getType().getType().trim())) {
//									String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
//									if(submissionStartLimitDateStr!=null && !submissionStartLimitDateStr.isEmpty()) {
//										Date submissionStartLimitDate = FormaterUtil.formatStringToDate(submissionStartLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
//										if((submissionStartLimitDate!=null
//												&& (new Date()).before(submissionStartLimitDate))) {
//											validationBeforeStartDate = true;
//										}
//									}
//									break;
//								}
//							}								
//						}
//					}
//					//submission end date limit validation
//					CustomParameter deviceTypesHavingSubmissionEndDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_END_DATE_VALIDATION, "");
//					if(deviceTypesHavingSubmissionEndDateValidationCP!=null) {
//						String deviceTypesHavingSubmissionEndDateValidationValue = deviceTypesHavingSubmissionEndDateValidationCP.getValue();
//						if(deviceTypesHavingSubmissionEndDateValidationValue!=null) {
//							String[] deviceTypesHavingSubmissionEndDateValidation = deviceTypesHavingSubmissionEndDateValidationValue.split(",");
//							for(String dt: deviceTypesHavingSubmissionEndDateValidation) {
//								if(dt.trim().equals(domain.getType().getType().trim())) {
//									String submissionEndLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
//									if(submissionEndLimitDateStr!=null && !submissionEndLimitDateStr.isEmpty()) {
//										Date submissionEndLimitDate = FormaterUtil.formatStringToDate(submissionEndLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
//										if(submissionEndLimitDate!=null
//												&& (new Date()).after(submissionEndLimitDate)) {
//											validationAfterEndDate = true;
//										}
//									}
//									break;
//								}
//							}								
//						}
//					}
//				}				
//				
//				//batch submission date limit validations for council (configurable through custom parameters)
//				if(domain.getSession()!=null && domain.getType()!=null 
//						&& domain.getHouseType()!=null && domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
//				
//					//submission start date limit validation
//					CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
//					if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
//						String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
//						if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
//							String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
//							for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
//								if(dt.trim().equals(domain.getType().getType().trim())) {
//									
//									Integer batch = Question.findBatch(domain, new Date());
//									
//									if(batch.equals(1) || batch.equals(2)){
//										if(Question.allowedInFirstBatch(domain, new Date())
//												|| Question.allowedInSecondBatch(domain, new Date())){
//											validationForBatch = true;
//										}
//									}
//									break;
//								}
//							}								
//						}
//					}
//				}
//				
//				if((!validationBeforeStartDate && !validationAfterEndDate) || validationForBatch){
//					List<Question> questions = new ArrayList<Question>();
//					for(String i : items) {
//						Long id = Long.parseLong(i);
//						Question question = Question.findById(Question.class, id);
//		
//						/**** Update Supporting Member ****/
//						List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
//						Status timeoutStatus = Status.findByType(
//								ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
//						if(question.getSupportingMembers() != null
//								&& ! question.getSupportingMembers().isEmpty()) {
//							for(SupportingMember sm : question.getSupportingMembers()) {
//								if(sm.getDecisionStatus().getType().equals(
//										ApplicationConstants.SUPPORTING_MEMBER_NOTSEND) ||
//										sm.getDecisionStatus().getType().equals(
//												ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
//									/**** Update Supporting Member ****/
//									sm.setDecisionStatus(timeoutStatus);
//									sm.setApprovalDate(new Date());	
//									sm.setApprovedText(question.getQuestionText());
//									sm.setApprovedSubject(question.getSubject());
//									sm.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
//		
//									/**** Update Workflow Details ****/
//									String strWorkflowdetails = sm.getWorkflowDetailsId();
//									if(strWorkflowdetails != null && ! strWorkflowdetails.isEmpty()) {
//										WorkflowDetails workflowDetails = WorkflowDetails.findById(
//												WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
//										workflowDetails.setStatus("TIMEOUT");
//										workflowDetails.setCompletionTime(new Date());
//										workflowDetails.merge();
//		
//										/**** Complete Task ****/
//										String strTaskId = workflowDetails.getTaskId();
//										Task task = processService.findTaskById(strTaskId);
//										processService.completeTask(task);
//									}
//								}
//		
//								if(! sm.getDecisionStatus().getType().equals(
//										ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
//									supportingMembers.add(sm);
//								}
//							}
//		
//							question.setSupportingMembers(supportingMembers);
//						}
//		
//						/**** Update Status(es) ****/
//						Status newstatus = Status.findByFieldName(Status.class, "type", 
//								ApplicationConstants.QUESTION_SUBMIT, question.getLocale());
//						question.setStatus(newstatus);
//						question.setInternalStatus(newstatus);
//						question.setRecommendationStatus(newstatus);
//		
//						/**** Edited On, Edited By and Edited As is set ****/
//						question.setSubmissionDate(new Date());
//						question.setEditedOn(new Date());
//						question.setEditedBy(authUser.getActualUsername());
//		
//						String strUserGroupType = request.getParameter("usergroupType");
//						if(strUserGroupType != null) {
//							UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class,
//									"type", strUserGroupType, question.getLocale());
//							question.setEditedAs(userGroupType.getName());
//						}
//		
//						/**** Bulk Submitted ****/
//						question.setBulkSubmitted(true);
//		
//						/**** Update the Motion object ****/
//						question = question.merge();
//						questions.add(question);
//					}
//		
//					model.addAttribute("questions", questions);
//				}
//			}
//		}
//
//		return "question/bulksubmissionack";
//	}
	
	
	public static String bulkSubmission(final HttpServletRequest request,
			final ModelMap model,
			final AuthUser authUser,
			final IProcessService processService, 
			final Locale locale) {
		String retVal = "question/error";
		String selectedItems = request.getParameter("items");
		if(selectedItems != null && ! selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");
			String strHouseType = null;
			
			//validation to check if any question is allowed for submission at the moment 
			{
				
				boolean validationBeforeStartDate = false;
				boolean validationAfterEndDate = false;
				boolean validationForBatch = false;
				Question domain = Question.findById(Question.class, new Long(items[0]));
				strHouseType = domain.getHouseType().getType();
				
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
										if((submissionStartLimitDate!=null
												&& submissionStartLimitDate.after(new Date()))) {
											validationBeforeStartDate = true;
										}else if(submissionStartLimitDate == null){
											validationBeforeStartDate = true;
										}
									}else{
										validationBeforeStartDate = true;
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
											validationAfterEndDate = true;
										}else if(submissionEndLimitDate == null){
											validationAfterEndDate = true;
										}
									}else{
										validationAfterEndDate = true;
									}
									break;
								}
							}								
						}
					}
				}				
				
				//batch submission date limit validations for council (configurable through custom parameters)
				if(domain.getSession()!=null && domain.getType()!=null 
						&& domain.getHouseType()!=null && domain.getSession().getParameter(domain.getType().getType()+"_processingMode").equals(ApplicationConstants.UPPER_HOUSE)) {
				
					//submission start date limit validation
					CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
					if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
						String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
						if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
							String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
							for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
								if(dt.trim().equals(domain.getType().getType().trim())) {
									
//									Integer batch = Question.findBatch(domain, new Date());
									
//									if(batch.equals(1) || batch.equals(2) ||){
//										if(Question.allowedInFirstBatch(domain, new Date())
//												|| Question.allowedInSecondBatch(domain, new Date())){
//											validationForBatch = true;
//										}
//									}
									if(Question.allowedInFirstBatch(domain, new Date())
											|| Question.allowedInSecondBatch(domain, new Date())){
										validationForBatch = true;
									}
									break;
								}
							}								
						}
					}
				}
				
				Integer batch = null;
				if(domain.getSession()!=null && domain.getType()!=null 
						&& domain.getHouseType()!=null && domain.getSession().getParameter(domain.getType().getType()+"_processingMode").equals(ApplicationConstants.UPPER_HOUSE)) {
					batch = Question.findBatch(domain, new Date());
				}
				
				if((!validationBeforeStartDate 
						&& !validationAfterEndDate 
						&& domain.getSession().getParameter(domain.getType().getType()+"_processingMode").equals(ApplicationConstants.LOWER_HOUSE))
						|| (domain.getSession().getParameter(domain.getType().getType()+"_processingMode").equals(ApplicationConstants.UPPER_HOUSE) && validationForBatch)){
					Status submitStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
					CustomParameter csptTimeoutOfSupportingMembersDisabled = CustomParameter.findByName(CustomParameter.class, "QIS_SUPPORTINGMEMBERS_TIMEOUT_DISABLED", "");
					Status timeoutStatus = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
					List<Question> questions = new ArrayList<Question>();
					for(String i : items) {
						Long id = Long.parseLong(i);
						Question question = Question.findById(Question.class, id);
						
						//Check if individual validation is passed
						if(batch!=null && batch.equals(1)){
							if(!Question.allowedInFirstBatchForMaxCountPerMember(question)) {
								break;
							}
						}
						
						/**** Update Timed Out Supporting Members (can be disabled for starting hour of submission start time using custom parameter) ****/						
						if(csptTimeoutOfSupportingMembersDisabled!=null 
								&& csptTimeoutOfSupportingMembersDisabled.getValue()!=null
								&& csptTimeoutOfSupportingMembersDisabled.getValue().equals("YES")) {
							System.out.println("Timeout of Pending/Unsent Supporting Members Disabled");
						} else {
							List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();							
							List<SupportingMember> existingSupportingMembers = question.getSupportingMembers();
							if(existingSupportingMembers != null && ! existingSupportingMembers.isEmpty()) {
								for(SupportingMember sm : existingSupportingMembers) {
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
						}						
		
						/**** Update Status(es) ****/						
						question.setStatus(submitStatus);
						question.setInternalStatus(submitStatus);
						question.setRecommendationStatus(submitStatus);
		
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
		
//						//set original sub-department
//						if(question.getSubDepartment()!=null){
//							question.setOriginalSubDepartment(question.getSubDepartment());
//						}
//						//set original answering date
//						if(question.getAnsweringDate()!=null){
//							question.setOriginalAnsweringDate(question.getAnsweringDate());
//						}
						
						/**** Bulk Submitted ****/
						question.setBulkSubmitted(true);
		
						/**** Update the Question object ****/
						question = question.merge();
						questions.add(question);
					}
					model.addAttribute("questions", questions);
					retVal = "question/bulksubmissionack";
				}else{
					model.addAttribute("errorcode", "submission_not_allowed");
					retVal = "question/error";
				}
			}
		}

		return retVal;
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
		String strDepartment = request.getParameter("department");

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
								currentStatus.getType().toUpperCase() + "_" + 
								strUsergroupType.toUpperCase(), "");
			}			 
			/**** QUESTION_PUT_UP_OPTIONS_ + QUESTION_TYPE + HOUSE_TYPE + USERGROUP_TYPE ****/
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,
					"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" +
							 strUsergroupType.toUpperCase(), "");
			List<Status> internalStatuses = new ArrayList<Status>(); 
			if(!strUsergroupType.equals(ApplicationConstants.CLERK)){
				try {
					if(statusBasedStatuses!=null) {
						internalStatuses = Status.findStatusContainedIn(statusBasedStatuses.getValue(),locale.toString());
					} else {
						internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
					}				
					
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
				}
			}
			model.addAttribute("internalStatuses", internalStatuses);
			
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
			model.addAttribute("department", strDepartment);

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
		StringBuffer assistantProcessed = new StringBuffer();
		StringBuffer clerkProcessed = new StringBuffer();
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
					// For Clerk Processing 
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)){
						// set Edited On and EditedBy
						question.setEditedOn(new Date());
						question.setEditedBy(authUser.getActualUsername());
						Credential credential = Credential.findByFieldName(Credential.class, "username", authUser.getActualUsername(), null);
						List<UserGroup> userGroups = authUser.getUserGroups();
						UserGroupType userGroupType = null;
						for(UserGroup ug : userGroups){
							UserGroup userGroup = UserGroup.findActive(credential, ug.getUserGroupType(), new Date(), locale.toString());
							if(userGroup != null){
								userGroupType = userGroup.getUserGroupType();
								break;
							}
						}
						
						/**** In case of assistant if internal status=submit,ministry,department,group is set 
						 * then change its internal and recommendstion status to assistant processed ****/
						
						CustomParameter assistantProcessedAllowed = CustomParameter.
								findByName(CustomParameter.class,"QIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
						if(assistantProcessedAllowed != null){
							List<UserGroupType> userGroupTypes = QuestionController.
									delimitedStringToUGTList(assistantProcessedAllowed.getValue(), ",", question.getLocale());
							Boolean isUserGroupAllowed = QuestionController.isUserGroupTypeExists(userGroupTypes, userGroupType);
							if(isUserGroupAllowed){
								String internalStatusType = question.getInternalStatus().getType();
								Group group = question.getGroup();
								if((internalStatusType.equals(ApplicationConstants.QUESTION_SUBMIT)
									||internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)) 
									&& question.getMinistry()!=null 
									&& group!=null 
									&& question.getSubDepartment()!=null) {
									Status ASSISTANT_PROCESSED = Status.
											findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, question.getLocale());
									question.setInternalStatus(ASSISTANT_PROCESSED);
									question.setRecommendationStatus(ASSISTANT_PROCESSED);
								}
							}
						}
						
						question.merge();
						
						Status submitStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, question.getLocale());
						if(question.getInternalStatus().getPriority()>submitStatus.getPriority()) {
							// On Group Change
							Group fromGroup = Question.isGroupChanged(question);
							if(fromGroup != null) {
								Question.onGroupChange(question, fromGroup);
							}							
						}
						// Add to Chart
						Chart.addToChart(question);
						clerkProcessed.append(question.formatNumber() + ",");
						
						
					}else if(!question.getInternalStatus().getType().
							equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)){
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

								if(recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)										
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_UNCLUBBING)
										|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)) {
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
								ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)){
							recommendAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_REJECTION)){
							recommendRejection.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_REPEATREJECTION)){
							recommendRepeatRejection.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_REPEATADMISSION)){
							recommendRepeatAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
							recommendClarificationFromMember.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
							recommendClarificationFromDept.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)){
							recommendClarificationFromGovt.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
							recommendClarificationFromMemberDept.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)){
							recommendClubbing.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_NAMECLUBBING)){
							recommendNameClubbing.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)){
							recommendClubbingPostAdmission.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)){
							recommendAdmitDueToReverseClubbing.append(question.formatNumber() + ",");
						}else if(question.getInternalStatus().getType().equals(
								ApplicationConstants.QUESTION_RECOMMEND_UNCLUBBING)){
							recommendUnClubbing.append(question.formatNumber() + ",");
						}
					}
					else {
						assistantProcessed.append(question.formatNumber() + ",");
					}
				}
				model.addAttribute("clerkProcessed", clerkProcessed.toString());
				model.addAttribute("assistantProcessed", assistantProcessed.toString());
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
								if(!status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
										&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
										&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_UNCLUBBING)) {
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
										ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)){
									recommendAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_REJECTION)){
									recommendRejection.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_REPEATREJECTION)){
									recommendRepeatRejection.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_REPEATADMISSION)){
									recommendRepeatAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
									recommendClarificationFromMember.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
									recommendClarificationFromDept.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)){
									recommendClarificationFromGovt.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
									recommendClarificationFromMemberDept.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_CLUBBING)){
									recommendClubbing.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_NAMECLUBBING)){
									recommendNameClubbing.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)){
									recommendClubbingPostAdmission.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)){
									recommendAdmitDueToReverseClubbing.append(question.formatNumber() + ",");
								}else if(question.getInternalStatus().getType().equals(
										ApplicationConstants.QUESTION_RECOMMEND_UNCLUBBING)){
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
	
	public static String getBulkTimeoutInit(
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
		String strDepartment = request.getParameter("department");

		if( strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())) {
			
			/**** Decision Status Available for Timeout ****/			
			/**** QUESTION_BULK_TIMEOUT_OPTIONS_ + QUESTION_TYPE + STATUS_TYPE ****/
			CustomParameter statusBasedStatuses = null;
			Status currentStatus = Status.findById(Status.class, new Long(strStatus));
			if(currentStatus!=null) {
				statusBasedStatuses =  CustomParameter.findByName(CustomParameter.class,
								"QUESTION_BULK_TIMEOUT_OPTIONS_" + deviceType.getType().toUpperCase() + "_" +
								currentStatus.getType().toUpperCase(), "");
			}			 
			/**** QUESTION_BULK_TIMEOUT_OPTIONS_ + QUESTION_TYPE + USERGROUP_TYPE ****/
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,
					"QUESTION_BULK_TIMEOUT_OPTIONS_" + deviceType.getType().toUpperCase() + "_" +
							 strUsergroupType.toUpperCase(), "");
			List<Status> internalStatuses = new ArrayList<Status>(); 
			try {
				if(statusBasedStatuses!=null) {
					internalStatuses = Status.findStatusContainedIn(statusBasedStatuses.getValue(),locale.toString());
				} else {
					internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
				}				
				
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			model.addAttribute("internalStatuses", internalStatuses);
			
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
			model.addAttribute("department", strDepartment);

			retVal = "question/bulk_timeout_init";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}
	
	public static String bulkTimeout(final HttpServletRequest request,
			final ModelMap model, 
			final AuthUser authUser,
			final IProcessService processService, 
			final Logger logger,
			final Locale locale) throws ELSException {
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus = request.getParameter("currentStatus");
		
		StringBuffer clarificationFromDepartment = new StringBuffer();
		StringBuffer clarificationFromMember = new StringBuffer();
		StringBuffer clarificationFromMemberAndDepartment = new StringBuffer();

		if(selectedItems != null && selectedItems.length > 0
				&& strStatus != null && !strStatus.isEmpty() && !strStatus.equals("-")) {
			Long statusId = Long.parseLong(strStatus);
			Status status = Status.findById(Status.class, statusId);
			Status putupStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale.toString());

			for(String i : selectedItems) {
				try {
					Long id = Long.parseLong(i);
					Question question = Question.findById(Question.class, id);
					
					question.endWorkflow(question, question.getHouseType().getType(), ApplicationConstants.MYTASK_TIMEOUT, locale.toString());
					
					//TODO:
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
						System.out.println("timeout member's task also"); //remove this statement after code added
					}
					
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
						
						question.setInternalStatus(putupStatus);						
						question.setRecommendationStatus(status);
						question.simpleMerge();
						clarificationFromDepartment.append(question.formatNumber() + ",");
						
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
						
						question.setInternalStatus(putupStatus);						
						question.setRecommendationStatus(status);
						question.simpleMerge();
						clarificationFromMember.append(question.formatNumber() + ",");
						
					} else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
						
						question.setInternalStatus(putupStatus);
						question.setRecommendationStatus(status);
						question.simpleMerge();
						clarificationFromMemberAndDepartment.append(question.formatNumber() + ",");
					}				
					
				} catch(ELSException e) {
					e.printStackTrace();
					logger.error(e.getParameter());
					logger.error("Problem in bulk timeout of question with ID = "+i);
					continue;
				} catch(Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
					logger.error("Problem in bulk timeout of question with ID = "+i);
					continue;
				}				
			}

			model.addAttribute("clarificationFromDepartment", clarificationFromDepartment.toString());
			model.addAttribute("clarificationFromMember", clarificationFromMember.toString());
			model.addAttribute("clarificationFromMemberAndDepartment", clarificationFromMemberAndDepartment.toString());
		}

		QuestionController.getBulkTimeoutQuestions(model, request, locale.toString());
		return "question/bulk_timeout_view";
	}

}