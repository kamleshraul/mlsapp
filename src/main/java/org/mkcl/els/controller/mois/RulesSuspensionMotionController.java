package org.mkcl.els.controller.mois;

import java.text.ParseException;
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
import org.mkcl.els.common.util.DateUtil;
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
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.RulesSuspensionMotion;
import org.mkcl.els.domain.RulesSuspensionMotionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserCitation;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
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
@RequestMapping("rulessuspensionmotion")
public class RulesSuspensionMotionController extends GenericController<RulesSuspensionMotion>{
	
	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {
		// Populate locale
		model.addAttribute("moduleLocale", locale);

		/**** Selected Motion Type ****/
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Available Motion Types ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			try {			
				Session session = null;
				
				String houseType = request.getParameter("houseType");
				String sessionType = request.getParameter("sessionType");
				String sessionYear = request.getParameter("sessionYear");
				
				if(houseType!=null&&!houseType.isEmpty()&&sessionType!=null
						&&!sessionType.isEmpty()&&sessionYear!=null&&!sessionYear.isEmpty()) {
					/****
					 * House Types .If housetype=bothhouse then lowerhouse will be
					 * selected by default
					 ****/
					List<HouseType> houseTypes = new ArrayList<HouseType>();								
					if (houseType.equals("lowerhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("upperhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("bothhouse")) {
						houseTypes = HouseType.findAll(HouseType.class, "type",ApplicationConstants.ASC, locale);
					}
					model.addAttribute("houseTypes", houseTypes);
					if (houseType.equals("bothhouse")) {
						houseType = "lowerhouse";
					}
					model.addAttribute("houseType", houseType);
					
					/**** Session Types. ****/
					List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType",ApplicationConstants.ASC, locale);
					model.addAttribute("sessionTypes", sessionTypes);
					model.addAttribute("sessionType", sessionType);
					
					/**** Years ****/
					Integer year = Integer.parseInt(sessionYear);
					CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class,"HOUSE_FORMATION_YEAR", "");
					List<Integer> years = new ArrayList<Integer>();
					if (houseFormationYear != null) {
						Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
						for (int i = year; i >= formationYear; i--) {
							years.add(i);
						}
					} else {
						model.addAttribute("errorcode", "houseformationyearnotset");
					}
					model.addAttribute("years", years);
					model.addAttribute("sessionYear", year);
					/** populate session dates as possible rule suspension dates **/
					session = Session.find(year, sessionType, houseType);
					if(session!=null && session.getId()!=null) {
						List<Date> sessionDates = session.findAllSessionDatesHavingNoHoliday();
						model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", locale));
					} else {
						model.addAttribute("errorcode", "nosessionentriesfound");
					}
				} else {
					Session lastSessionCreated=null;
					/****
					 * House Types .If housetype=bothhouse then lowerhouse will be
					 * selected by default
					 ****/
					List<HouseType> houseTypes = new ArrayList<HouseType>();				
					if(houseType==null) {
						houseType = this.getCurrentUser().getHouseType();
					}				
					if (houseType.equals("lowerhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("upperhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("bothhouse")) {
						houseTypes = HouseType.findAll(HouseType.class, "type",ApplicationConstants.ASC, locale);
					}
					model.addAttribute("houseTypes", houseTypes);
					if (houseType.equals("bothhouse")) {
						houseType = "lowerhouse";
					}
					model.addAttribute("houseType", houseType);

					/**** Session Types. ****/
					List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType",ApplicationConstants.ASC, locale);
					/**** Latest Session of a House Type ****/
					HouseType authUserHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, locale);
					
					lastSessionCreated = Session.findLatestSession(authUserHouseType);

					/***
					 * Session Year and Session Type.Default is the type and year of
					 * last created session in a particular housetype
					 ****/
					Integer year = new GregorianCalendar().get(Calendar.YEAR);
					if (lastSessionCreated!=null && lastSessionCreated.getId() != null) {
						session = lastSessionCreated;
						year = lastSessionCreated.getYear();
						model.addAttribute("sessionType", lastSessionCreated.getType().getId());
					} else {
						model.addAttribute("errorcode", "nosessionentriesfound");
					}
					model.addAttribute("sessionTypes", sessionTypes);
					/**** Years ****/
					CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class,"HOUSE_FORMATION_YEAR", "");
					List<Integer> years = new ArrayList<Integer>();
					if (houseFormationYear != null) {
						Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
						for (int i = year; i >= formationYear; i--) {
							years.add(i);
						}
					} else {
						model.addAttribute("errorcode", "houseformationyearnotset");
					}
					model.addAttribute("years", years);
					model.addAttribute("sessionYear", year);		
					session = lastSessionCreated;
				}
				
				/**** Device Types. ****/
				deviceTypes = DeviceType.findDeviceTypesStartingWith("motions_rules_suspension", locale);

				model.addAttribute("motionTypes", deviceTypes);
				List<MasterVO> motionTypeVOs = new ArrayList<MasterVO>();
				for(DeviceType motionType: deviceTypes) {
					MasterVO motionTypeVO = new MasterVO();
					motionTypeVO.setId(motionType.getId());
					motionTypeVO.setType(motionType.getType());
					motionTypeVO.setName(motionType.getName());
					if(houseType!=null && houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
						motionTypeVO.setDisplayName(motionType.getName_lowerhouse());
					} else if(houseType!=null && houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
						motionTypeVO.setDisplayName(motionType.getName_upperhouse());
					} else {
						motionTypeVO.setDisplayName(motionType.getName());
					}					
					motionTypeVOs.add(motionTypeVO);
				}
				model.addAttribute("motionTypeVOs", motionTypeVOs);
				/**** Default Value ****/
				model.addAttribute("motionType", deviceType.getId());
				/**** Access Control Based on Motion Type ****/
				model.addAttribute("motionTypeType", deviceType.getType());

				/****
				 * Custom Parameter To Determine The Usergroup and usergrouptype
				 * of rsmois users . here we are determining what status will be
				 * shown to a particular user.
				 ****/
				List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
				UserGroup userGroup = null;
				UserGroupType userGroupType = null;
				if (userGroups != null && !userGroups.isEmpty()) {
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"RSMOIS_ALLOWED_USERGROUPTYPES", "");
					if (customParameter != null) {
						List<UserGroupType> configuredUserGroupTypes = 
								QuestionController.delimitedStringToUGTList(customParameter.getValue(), ",", locale);
						
						userGroup = QuestionController.getUserGroup(userGroups, configuredUserGroupTypes, session, locale);
						userGroupType = userGroup.getUserGroupType();
						
						model.addAttribute("usergroup", userGroup.getId());
						model.addAttribute("usergroupType", userGroupType.getType());
					} else {
						model.addAttribute("errorcode","rsmois_allowed_usergroups_notset");
					}
				} else {
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
				List<Date> sessionDates = session.findAllSessionDatesHavingNoHoliday();
				model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", locale));				
				Date defaultRuleSuspensionDate = null;
				if(userGroupType.getType().equals(ApplicationConstants.MEMBER)) {
					defaultRuleSuspensionDate = RulesSuspensionMotion.findDefaultRuleSuspensionDateForSession(session, true);
				} else {
					defaultRuleSuspensionDate = RulesSuspensionMotion.findDefaultRuleSuspensionDateForSession(session, false);
				}
				model.addAttribute("defaultRuleSuspensionDate", FormaterUtil.formatDateToString(defaultRuleSuspensionDate, ApplicationConstants.SERVER_DATEFORMAT));
				/****Rules Suspension Motion Status Allowed ****/
				CustomParameter allowedStatus = CustomParameter.findByName(CustomParameter.class,
								"RULESSUSPENSIONMOTION_GRID_STATUS_ALLOWED_"+ userGroupType.getType().toUpperCase(),"");
				List<Status> status = new ArrayList<Status>();
				if (allowedStatus != null) {
					status = Status.findStatusContainedIn(allowedStatus.getValue(),locale);
				} else {
					CustomParameter defaultAllowedStatus = CustomParameter.findByName(CustomParameter.class,
									"RULESSUSPENSIONMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT","");
					if (defaultAllowedStatus != null) {
						status = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
					} else {
						model.addAttribute("errorcode","rulessuspensionmotion_status_allowed_by_default_not_set");
					}
				}
				model.addAttribute("status", status);
				/****Rules Suspension Motion Departments Allowed ****/
				Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
				CustomParameter subDepartmentFilterAllowedFor = 
						CustomParameter.findByName(CustomParameter.class, "RSMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
				if(subDepartmentFilterAllowedFor != null) {
					List<UserGroupType> ugtConfiguredForSubdepartments = 
							this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(UserGroupType.class, subDepartmentFilterAllowedFor.getValue(), ",", locale);
					boolean isUGTConfiguredForSubdepartments = 
							this.isObjectExtendingBaseDomainAvailableInList(ugtConfiguredForSubdepartments, userGroupType);
					if(isUGTConfiguredForSubdepartments) {
						String subDepartmentParam = parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale);
						if(subDepartmentParam != null && ! subDepartmentParam.equals("")) {
							List<SubDepartment> subDepartments =
									this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(SubDepartment.class, "name", subDepartmentParam, "##", locale);
							model.addAttribute("subDepartments", subDepartments);
						}
						else {
							throw new ELSException("RulesSuspensionMotionController.populateModule/4", 
									"SUBDEPARTMENT parameter is not set for Username: " + currentUser.getUsername());
						}
					}
				}
				else {
					throw new ELSException("RulesSuspensionMotionController.populateModule/4", 
							"RSMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR key is not set as CustomParameter");
				}
				/****
				 * Roles and ugparam.Role will be used to decide who can create
				 * new motions(member and clerk).for member and clerk only those
				 * motions will be visible which are created by them.For other
				 * mois users all motions will be visible.
				 ****/
				Set<Role> roles = this.getCurrentUser().getRoles();
				for (Role i : roles) {
					if (i.getType().startsWith("MEMBER_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().contains("RSMOIS_CLERK")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().startsWith("RSMOIS_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					}
				}
			} catch (ELSException e) {
				model.addAttribute("RulesSuspensionMotionController", e.getParameter());
			}
		}
	}
	
	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		String newUrlPattern = urlPattern;		
		/**** Controlling Grids Starts ****/
		try {
			Role role = this.populateObjectExtendingBaseDomainByStringFieldName(request, "role", Role.class, "type", locale);
			if(role != null){
				 CustomParameter memberGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"RSMOIS_MEMBERGRID_ALLOWED_FOR", "");
				 if(memberGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
							 this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(Role.class, memberGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
								this.isObjectExtendingBaseDomainAvailableInList(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){						   
						   newUrlPattern=urlPattern+"?usergroup=member";
						   String selectedRulesSuspensionDate = request.getParameter("rulesSuspensionDate");
						   if(selectedRulesSuspensionDate  != null && !selectedRulesSuspensionDate.isEmpty()) {
							   newUrlPattern=newUrlPattern+"&rulesSuspensionDate=selected";
						   }
						   return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("RulesSubmissionMotionController.modifyURLPattern/4", 
								"RSMOIS_MEMBERGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
				 
				 CustomParameter typistGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"RSMOIS_TYPISTGRID_ALLOWED_FOR", "");
				 if(typistGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
							this.delimitedStringToRoleList(typistGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
							this.isRoleExists(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){
						 newUrlPattern=urlPattern+"?usergroup=typist";
						 String selectedRulesSuspensionDate = request.getParameter("rulesSuspensionDate");
					     if(selectedRulesSuspensionDate!=null && !selectedRulesSuspensionDate.isEmpty()) {
						     newUrlPattern=newUrlPattern+"&rulesSuspensionDate=selected";
					     }
					     return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("RulesSubmissionMotionController.modifyURLPattern/4", 
								"RSMOIS_TYPISTGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
				 
				 CustomParameter assistantGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"RSMOIS_ASSISTANTGRID_ALLOWED_FOR", "");
				 if(assistantGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
						this.delimitedStringToRoleList(assistantGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
						this.isRoleExists(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){
						 newUrlPattern=urlPattern+"?usergroup=assistant";
						 String selectedRulesSuspensionDate = request.getParameter("rulesSuspensionDate");
						 if(selectedRulesSuspensionDate!=null && !selectedRulesSuspensionDate.isEmpty()) {
							 newUrlPattern=newUrlPattern+"&rulesSuspensionDate=selected";
						 }
						 return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("RulesSuspensionMotionController.modifyURLPattern/4", 
								"RSMOIS_ASSISTANTGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
			 }
			 else{
				 throw new ELSException("RulesSuspensionMotionController.modifyURLPattern/4",
							"Role is not found");
			 }
		} catch(ELSException e) {
			model.addAttribute("error", e.getParameter());
		}		 

		return newUrlPattern;
	}
	
	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		/**** New Operations Allowed For Starts ****/
		Role role;
		try {
			role = this.populateObjectExtendingBaseDomainByStringFieldName(request, "role", Role.class, "type", locale);
			if(role != null){
				CustomParameter newOperationAllowedTo = 
						CustomParameter.findByName(CustomParameter.class,"RSMOIS_NEW_OPERATION_ALLOWED_TO","");
				if(newOperationAllowedTo != null){
					List<Role> configuredNewOperationAllowedForRoles = 
							 this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(Role.class, newOperationAllowedTo.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
								this.isObjectExtendingBaseDomainAvailableInList(configuredNewOperationAllowedForRoles, role);
					if(isRoleConfiguredForMemberGrid){
						return servletPath;
					}			
				}
			}			
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}		
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
	}
	
	@Override
	protected void populateNew(final ModelMap model, 
			final RulesSuspensionMotion domain, 
			final String locale,
			final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		domain.setLocale("mr_IN");
		try {
			/**** House Type ****/
			String selectedHouseType=authUser.getHouseType();
	    	HouseType houseType=null;
	 		if(selectedHouseType!=null){
	 			if(!selectedHouseType.isEmpty()){
	  				try {
	 					Long houseTypeId=Long.parseLong(selectedHouseType);
	 					houseType=HouseType.findById(HouseType.class,houseTypeId);
	 				} catch (NumberFormatException e) {
	 					houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
	 				}
	 				model.addAttribute("formattedHouseType",houseType.getName());
	 				model.addAttribute("houseTypeType",houseType.getType());
	 				model.addAttribute("houseType",houseType.getId());
	 			}else{
	 				logger.error("**** Check request parameter 'houseType' for no value ****");
	 				model.addAttribute("errorcode","houseType_isempty");	
	 			}
	 		}else{
	 			logger.error("**** Check request parameter 'houseType' for null value ****");
	 			model.addAttribute("errorcode","houseType_isnull");
	 		}	 		 
	 		/**** Session Year ****/
	 		String selectedYear=request.getParameter("sessionYear");
			Integer sessionYear=0;
			if(selectedYear!=null){
				if(!selectedYear.isEmpty()){
					sessionYear=Integer.parseInt(selectedYear);
					model.addAttribute("formattedSessionYear",FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
					model.addAttribute("sessionYear",sessionYear);
				}else{
					logger.error("**** Check request parameter 'sessionYear' for no value ****");
					model.addAttribute("errorcode","sessionYear_isempty");
				}
			}else{
				logger.error("**** Check request parameter 'sessionYear' for null value ****");
				model.addAttribute("errorcode","sessionyear_isnull");
			}  			
			/**** Session Type ****/
			String selectedSessionType=request.getParameter("sessionType");
			SessionType sessionType=null;
			if(selectedSessionType!=null){
				if(!selectedSessionType.isEmpty()){
					sessionType=SessionType.findById(SessionType.class,Long.parseLong(selectedSessionType));
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
			/**** Device Type ****/
			String selectedMotionType=request.getParameter("motionType");
			if(selectedMotionType==null){
				selectedMotionType=request.getParameter("type");
			}
			DeviceType motionType=null;
			if(selectedMotionType!=null){
				if(!selectedMotionType.isEmpty()){
					motionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedMotionType));
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
				}else{
					logger.error("**** Check request parameter 'motionType' for no value ****");
					model.addAttribute("errorcode","motionType_isempty");		
				}
			}else{
				logger.error("**** Check request parameter 'motionType' for null value ****");
				model.addAttribute("errorcode","motionType_isnull");
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
			/****UserGroupType****/
			String usergroupType=request.getParameter("usergroupType");
			if(usergroupType!=null){
				model.addAttribute("usergroupType",usergroupType);
			}else{
				usergroupType=(String) request.getSession().getAttribute("usergroupType");
				model.addAttribute("usergroupType",usergroupType);
				request.getSession().removeAttribute("usergroupType");
			}
			/*****UserGroup*******/
			String usergroup=request.getParameter("usergroup");
			if(usergroup!=null){
				model.addAttribute("usergroup",usergroup);
			}else{
				usergroup=(String) request.getSession().getAttribute("usergroup");
				model.addAttribute("usergroup",usergroup);
				request.getSession().removeAttribute("usergroup");
			}
			/**** Session Details ****/
			Session selectedSession=null;
			String memberName=null;
			Member member=null;
			if(houseType!=null&&selectedYear!=null&&sessionType!=null){
				try {
					selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}
				if(selectedSession!=null){
					model.addAttribute("session",selectedSession.getId());
					
				}else{
					logger.error("**** Session doesnot exists ****");
					model.addAttribute("errorcode","session_isnull");	
				}
			}else{
				logger.error("**** Check request parameters 'houseType,sessionYear and sessionType for null values' ****");
				model.addAttribute("errorcode","requestparams_isnull");
			}
			/**** Member Details ****/
			if(role.startsWith("MEMBER")){
				member=Member.findMember(authUser.getFirstName(),authUser.getMiddleName(),authUser.getLastName(),authUser.getBirthDate(),locale);
				if(member!=null){
					model.addAttribute("primaryMember",member.getId());
					memberName=member.getFullname();
					model.addAttribute("formattedPrimaryMember",memberName);
				}else{
					logger.error("**** Authenticated user is not a member ****");
					model.addAttribute("errorcode","member_isnull");
				}				
				/**** Constituency ****/
				Long houseId=selectedSession.getHouse().getId();
				MasterVO constituency=null;
				if(houseType.getType().equals("lowerhouse")){
					if(member != null){
						constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
						model.addAttribute("constituency",constituency.getName());
					}
				}else if(houseType.getType().equals("upperhouse")){
					Date currentDate=new Date();
					String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
					if(member != null){
						constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
						model.addAttribute("constituency",constituency.getName());
					}
				}
			}
			/**** Ministries ****/
			Date rotationOrderPubDate=null;
			CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
			if(strRotationOrderPubDate!=null){
				try {
					rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
					model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
					Date currentDate=new Date();
					if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
						List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
						model.addAttribute("ministries",ministries);
						List<Ministry> selectedministries=domain.getMinistries();
						if(selectedministries!=null && !selectedministries.isEmpty()){
							model.addAttribute("selectedministries",selectedministries);						
							/**** Sub Departments ****/
							List<SubDepartment> subDepartments=new ArrayList<SubDepartment>();
							for(Ministry m:selectedministries){
								List<SubDepartment> assignedSubDepartments = MemberMinister.
										findAssignedSubDepartments(m, selectedSession.getEndDate(), locale);
								subDepartments.addAll(assignedSubDepartments);
							}
							model.addAttribute("subDepartments",subDepartments);
							List<SubDepartment> selectedSubDepartments=domain.getSubDepartments();
							if(!selectedSubDepartments.isEmpty()){
								model.addAttribute("selectedSubDepartments",selectedSubDepartments);
							}
						}
					}
				} catch (ParseException e) {
					logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
					model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
				} 
			}else{
				logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
				model.addAttribute("errorcode", "rotationorderpubdate_notset");
			}
			/** populate session dates as possible adjourning dates **/
			if(selectedSession!=null && selectedSession.getId()!=null) {
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
				model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", locale));				
				Date defaultRuleSuspensionDate = null;
				if(usergroupType.equals(ApplicationConstants.MEMBER)) {
					defaultRuleSuspensionDate = RulesSuspensionMotion.findDefaultRuleSuspensionDateForSession(selectedSession, true);
				} else {
					defaultRuleSuspensionDate = RulesSuspensionMotion.findDefaultRuleSuspensionDateForSession(selectedSession, false);
				}
				model.addAttribute("defaultRuleSuspensionDate", FormaterUtil.formatDateToString(defaultRuleSuspensionDate, ApplicationConstants.SERVER_DATEFORMAT));
			}
		} catch(ELSException elsx) {
			elsx.printStackTrace();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected String modifyEditUrlPattern(final String editUrlPattern,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		AuthUser authUser = this.getCurrentUser();
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
				findByName(CustomParameter.class, "RSMOIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.
				findByName(CustomParameter.class, "RSMOIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles=authUser.getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return editUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return editUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("RSMOIS_")) {
				return editUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "rulessuspensionmotion/error";
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final RulesSuspensionMotion domain,
			final HttpServletRequest request) {
		try{
			/**** In case of bulk edit we can update only few parameters ****/
			model.addAttribute("bulkedit",request.getParameter("bulkedit"));
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
			/**** UserGroupType ****/
			UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
			if(userGroupType!=null){
				model.addAttribute("usergroupType",userGroupType.getType());
			}else{
				String strUserGroupType=(String) request.getSession().getAttribute("usergroupType");
				userGroupType = UserGroupType.findByType(strUserGroupType, locale);
				model.addAttribute("usergroupType",strUserGroupType);
				request.getSession().removeAttribute("usergroupType");
			}
			/***** UserGroup *******/
			UserGroup userGroup = this.populateObjectExtendingBaseDomainByID(request, "usergroup", UserGroup.class, locale);
			if(userGroup!=null){
				model.addAttribute("usergroup",userGroup.getId());
			}else{
				String strUserGroup=(String) request.getSession().getAttribute("usergroup");
				userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
				model.addAttribute("usergroup",strUserGroup);
				request.getSession().removeAttribute("usergroup");
			}			
			/**** Ministries & SubDepartments ****/
			Date currentDate = new Date();
			if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
				List<Ministry> ministries = Ministry.
						findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, domain.getLocale());
				model.addAttribute("ministries",ministries);				
				List<Ministry> selectedministries=domain.getMinistries();
				if(selectedministries!=null && !selectedministries.isEmpty()){
					model.addAttribute("selectedministries",selectedministries);						
					/**** Sub Departments ****/
					List<SubDepartment> subDepartments=new ArrayList<SubDepartment>();
					for(Ministry m:selectedministries){
						List<SubDepartment> assignedSubDepartments = MemberMinister.
								findAssignedSubDepartments(m, selectedSession.getEndDate(), locale);
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
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
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
			//populate member status name and devicetype
			if(userGroupType!=null && userGroupType.getType().equals("member")){
				Status memberStatus = domain.findMemberStatus();
				if(memberStatus!=null){				
					model.addAttribute("formattedMemberStatus", memberStatus.getName());
				}
			}
			/**** Status,Internal Status and recommendation Status ****/
			Status status=domain.getStatus();
			Status internalStatus=domain.getInternalStatus();
			Status recommendationStatus=domain.getRecommendationStatus();
			if(status==null) {
				logger.error("status is not set for this motion having id="+domain.getId()+".");
				model.addAttribute("errorcode", "status_null");
				return;
			}
			model.addAttribute("status",status.getId());
			model.addAttribute("memberStatusType",status.getType());
			model.addAttribute("formattedStatus", status.getName());
			if(internalStatus==null) {
				logger.error("internal status is not set for this motion having id="+domain.getId()+".");
				model.addAttribute("errorcode", "internalStatus_null");
				return;
			}
			model.addAttribute("internalStatus",internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("internalStatusPriority", internalStatus.getPriority());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			if(recommendationStatus==null) {
				logger.error("recommendation status is not set for this motion having id="+domain.getId()+".");
				model.addAttribute("errorcode", "recommendationStatus_null");
				return;
			}
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
			model.addAttribute("recommendationStatusPriority", recommendationStatus.getPriority());
			model.addAttribute("formattedRecommendationStatus", recommendationStatus.getName());
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
			
			// set End Flag and Level in case of assistant/section officer
			if(userGroupType !=null 
					&& (userGroupType.getType().equals("assistant") || userGroupType.getType().equals("section_officer"))
			){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				//populate PUT UP OPTIONS
				if(!internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT)){
					if(recommendationStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
						RulesSuspensionMotionController.
						populateInternalStatus(model,recommendationStatus.getType(),userGroupType.getType(),locale,motionType.getType());
					} else {
						RulesSuspensionMotionController.
						populateInternalStatus(model,internalStatus.getType(),userGroupType.getType(),locale,motionType.getType());
					}					
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
				//Populate Actors
				if(internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_ADMISSION)
							||internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_REJECTION)
							||internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_REJECTION)){
					List<Reference> actors=WorkflowConfig.
							findRulesSuspensionMotionActorsVO(domain, internalStatus, userGroup, 1, locale);
					model.addAttribute("actors", actors);
				}
			}else if(userGroupType !=null && userGroupType.getType().equals("clerk")){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
			}
			/**** remarks for final rejection ****/
			Status rejectionFinalStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECTION, locale);
			boolean canRemark = false;	
			String errorMessagePossible="";
			try{
				errorMessagePossible = "domain_not_found";
				if (internalStatus.getType().equals(rejectionFinalStatus.getType())) {
					errorMessagePossible = "rulessuspensiondraft_not_found_for_remark";
					RulesSuspensionMotionDraft mDraft = domain.findPreviousDraft();
					model.addAttribute("sectionofficer_remark",mDraft.getRemarks());
					canRemark = true;
				}
			}catch(Exception e){
				model.addAttribute("errorcode",errorMessagePossible);
			}
			if(!canRemark){
				model.addAttribute("sectionofficer_remark","");
			}
			/** getting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
			UserGroupType userGroupTypeObj = UserGroupType.findByType(userGroupType.getType(), domain.getLocale());
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
		} catch(ELSException e) {
			
		} catch(Exception e) {
			
		}
	}
	
	public static void populateInternalStatus(final ModelMap model, final String type,final String userGroupType,final String locale, final String motionType) {
		List<Status> internalStatuses=new ArrayList<Status>();
		try{
			CustomParameter specificDeviceStatusUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+motionType.toUpperCase()
							+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+motionType.toUpperCase()
							+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses = CustomParameter.
					findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_"+type.toUpperCase()
							+"_"+userGroupType.toUpperCase(),"");
			if(specificDeviceStatusUserGroupStatuses != null) {
				internalStatuses = Status.
						findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			} else if(specificDeviceUserGroupStatuses != null) {
				internalStatuses = Status.
						findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			} else if(specificStatuses != null) {
				internalStatuses = Status.
						findStatusContainedIn(specificStatuses.getValue(), locale);
			} else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					|| userGroupType.equals(ApplicationConstants.SPEAKER)) {
				CustomParameter finalStatus = CustomParameter.
						findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus != null) {
					internalStatuses = Status.
							findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus = CustomParameter.
							findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus != null){
						internalStatuses = Status.
								findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter = CustomParameter.
								findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter != null){
							internalStatuses = Status.
									findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode", "rulessuspensionmotion_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus = CustomParameter.
						findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus != null) {
					internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.
							findByName(CustomParameter.class,"RULESSUSPENSIONMOTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter != null) {
						internalStatuses = Status.
								findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode", "rulessuspensionmotion_putup_options_final_notset");
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
	protected void preValidateCreate(final RulesSuspensionMotion domain,
            final BindingResult result, 
            final HttpServletRequest request) {
		/**** populate supporting members before validation ****/
		String role = request.getParameter("role");		
		populateSupportingMembers(domain,role,request);
	}
	
	@Override
	protected void customValidateCreate(final RulesSuspensionMotion domain,
            final BindingResult result, 
            final HttpServletRequest request) {	
		String role = request.getParameter("role");
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
		}
		/** Basic Fields Validation **/
		if(domain.getHouseType()==null){
			result.rejectValue("houseType","HousetypeEmpty");
			return;
		}
		if(domain.getType()==null){
			result.rejectValue("type","MotionTypeEmpty");
		}
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
		}
		if(domain.getPrimaryMember()==null){
			result.rejectValue("primaryMember","PrimaryMemberEmpty");
		}
		if(domain.getSubject()==null || domain.getSubject().isEmpty()){
			result.rejectValue("subject", "SubjectEmpty");
		}
		if(domain.getNoticeContent()==null || domain.getNoticeContent().isEmpty()){
			result.rejectValue("noticeContent","RulesSuspensionMotion.NoticeContentEmpty");
		}		
		/**** Number Validation ****/
		if(role.equals("RSMOIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "RSMOIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");			
						return;
					}
					//check for duplicate motion
					Boolean flag = RulesSuspensionMotion.isDuplicateNumberExist(domain.getRuleSuspensionDate(), domain.getNumber(), domain.getId(), domain.getLocale());
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
						return;
					}
				}
			}
		}
		String operation=request.getParameter("operation");
		if(operation!=null && !operation.isEmpty()){			
			if(operation.equals("approval")){
				/**** Approval ****/	
				if(domain.getSupportingMembers()==null){
					result.rejectValue("supportingMembers","SupportingMembersEmpty","there are no supporting members for approval.");
				} else if(domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers","there are no supporting members for approval.");						
				} else {
					//check if request is already sent for approval
					int count=0;
					for(SupportingMember i:domain.getSupportingMembers()){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							count++;
						}
					}
					if(count==0){
						result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent","request already sent to selected supporting members.");
					}
				}
			} else if(operation.equals("submit")){
				/**** Submission ****/	
//				if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
//					// Empty check for Ministry
//					if(domain.getMinistry()==null){
//						result.rejectValue("ministry","MinistryEmpty");
//					}
//					// Empty check for Subdepartment
//					if(domain.getSubDepartment()==null){
//						result.rejectValue("subDepartment","SubDepartmentEmpty");
//					}
//				}
				
				//submission window validations
				CustomParameter submissionWindowValidationSkippedCP = CustomParameter.findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_SUBMISSION_WINDOW_VALIDATIONS_SKIPPED"+"_"+domain.getHouseType().getType().toUpperCase(), "");
				if(submissionWindowValidationSkippedCP==null || submissionWindowValidationSkippedCP.getValue()==null
						|| !submissionWindowValidationSkippedCP.getValue().equals("TRUE")) {
					if(!domain.validateSubmissionDate()) {
						result.rejectValue("version","submissionWindowClosed","submission time window is closed for this rule suspension date motions!");
						return;
					}
					CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG"+"_"+domain.getHouseType().getType().toUpperCase(), "");
					if(csptOfflineSubmissionAllowedFlag!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
						if(!role.equals("RSMOIS_TYPIST")){
							if(!RulesSuspensionMotion.validateSubmissionTime(domain.getSession(), domain.getRuleSuspensionDate())) {
								result.rejectValue("version","rulessuspensionmotion.submissionWindowTimeClosed","submission time window is closed for this rules suspension date motions!");
								return;
							}
						}
					} else {
						if(!RulesSuspensionMotion.validateSubmissionTime(domain.getSession(), domain.getRuleSuspensionDate())) {
							result.rejectValue("version","rulessuspensionmotion.submissionWindowTimeClosed","submission time window is closed for this rules suspension date motions!");
							return;
						}
					}					
				}
			}
		}
    }
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, RulesSuspensionMotion domain,
			HttpServletRequest request) {		
		try {
			/**** House Type ****/
			HouseType houseType = domain.getHouseType();
			if(houseType != null) {
				model.addAttribute("formattedHouseType", houseType.getName());
				model.addAttribute("houseTypeType", houseType.getType());
				model.addAttribute("houseType", houseType.getId());
			}		
			/**** Device Type ****/
			DeviceType motionType = domain.getType();
			model.addAttribute("formattedMotionType", motionType.getName());
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
			/**** UserGroupType ****/
			String usergroupType=request.getParameter("usergroupType");
			if(usergroupType!=null){
				model.addAttribute("usergroupType",usergroupType);
			}else{
				usergroupType=(String) request.getSession().getAttribute("usergroupType");
				model.addAttribute("usergroupType",usergroupType);
				request.getSession().removeAttribute("usergroupType");
			}
			/***** UserGroup *******/
			UserGroup userGroup = this.populateObjectExtendingBaseDomainByID(request, "usergroup", UserGroup.class, domain.getLocale());
			if(userGroup!=null){
				model.addAttribute("usergroup",userGroup.getId());
			}else{
				String strUserGroup=(String) request.getSession().getAttribute("usergroup");
				userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
				model.addAttribute("usergroup",strUserGroup);
				request.getSession().removeAttribute("usergroup");
			}
			/**** Ministries & SubDepartments ****/
			Date currentDate = new Date();
			if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
				List<Ministry> ministries = Ministry.
						findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, domain.getLocale());
				model.addAttribute("ministries",ministries);				
				//Populate Ministry
				List<Ministry> selectedministries=domain.getMinistries();
				if(selectedministries!=null && !selectedministries.isEmpty()){
					model.addAttribute("selectedministries",selectedministries);						
					/**** Sub Departments ****/
					List<SubDepartment> subDepartments=new ArrayList<SubDepartment>();
					for(Ministry m:selectedministries){
						List<SubDepartment> assignedSubDepartments = MemberMinister.
								findAssignedSubDepartments(m, selectedSession.getEndDate(), domain.getLocale());
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
			List<SupportingMember> suppMembers = domain.getSupportingMembers();
			if(suppMembers!=null && !suppMembers.isEmpty()) {
				List<Member> supportingMembers = new ArrayList<Member>();
				for(SupportingMember sm : suppMembers){
					Member member = sm.getMember();
					supportingMembers.add(member);
				}
				model.addAttribute("supportingMembers", supportingMembers);
				model.addAttribute("supportingMembersName", this.populateDelimitedSupportingMemberNames(domain.getSupportingMembers(), domain.getLocale()));
			}			
			/** populate session dates as possible adjourning dates **/
			if(selectedSession!=null && selectedSession.getId()!=null) {
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
				model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", domain.getLocale()));
			}
			/**** populate adjourning date ****/
			model.addAttribute("selectedAdjourningDate", FormaterUtil.formatDateToString(domain.getRuleSuspensionDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
			model.addAttribute("formattedAdjourningDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getRuleSuspensionDate(), "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", domain.getLocale()));
			/** error notification **/
			model.addAttribute("type", "error");
			model.addAttribute("msg", "create_failed");
		} catch(ELSException e) {
			model.addAttribute("error", e.getParameter());
		} catch(Exception e) {
			model.addAttribute("error", e.getMessage());
		}		
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final RulesSuspensionMotion domain,
			final HttpServletRequest request) throws ELSException {
		/**** Status ,Internal Status,Recommendation Status,rule suspension date,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String locale = domain.getLocale();
		String operation=request.getParameter("operation");
		String role = request.getParameter("role");
		UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		if(userGroupType==null) {
			throw new ELSException("RulesSuspensionMotionController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
		}
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&& domain.getType()!=null && domain.getPrimaryMember()!=null
				&& domain.getNoticeContent()!=null && !domain.getNoticeContent().isEmpty()){
			if(operation!=null && !operation.isEmpty() && operation.trim().equals("submit")){
				if(userGroupType.getType()!=null&&!(userGroupType.getType().isEmpty())
						&&(userGroupType.getType().equals("member")||userGroupType.getType().equals("typist"))){
					/****  submission date is set ****/
					if(domain.getSubmissionDate()==null){
						domain.setSubmissionDate(new Date());
					}
					/**** only those supporting memebrs will be included who have approved the requests ****/
					List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
					if(domain.getSupportingMembers()!=null){
						if(!domain.getSupportingMembers().isEmpty()){
							CustomParameter supportingMemberAutoApprovalAllowedTo = 
									CustomParameter.findByName(CustomParameter.class, 
											"RSMOIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
							for(SupportingMember i:domain.getSupportingMembers()){								
								if(supportingMemberAutoApprovalAllowedTo != null) {
									if(supportingMemberAutoApprovalAllowedTo.getValue().contains(role)) {
										supportingMembers.add(i);
									} else {
										String decisionStatusType =i.getDecisionStatus().getType().trim();
										if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
											supportingMembers.add(i);
										}
									}
								} else {
									String decisionStatusType =i.getDecisionStatus().getType().trim();
									if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
										supportingMembers.add(i);
									}
								}								
							}
							domain.setSupportingMembers(supportingMembers);
						}
					}								
					/**** Status,Internal status and recommendation status is set to submit ****/
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);							 
				}
			} else {
				Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.RULESSUSPENSIONMOTION_COMPLETE, domain.getLocale());
				domain.setStatus(newstatus);
				domain.setInternalStatus(newstatus);
				domain.setRecommendationStatus(newstatus);
			}
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RULESSUSPENSIONMOTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setDataEnteredBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedAs(userGroupType.getName());
	}
	
	@Override
	protected void populateAfterCreate(final ModelMap model, final RulesSuspensionMotion domain,
			final HttpServletRequest request) throws ELSException, ParseException {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Supporting Member Workflow ****/
		String operation=request.getParameter("operation");
		if(operation != null && !operation.isEmpty()){
			if(operation.equals("approval")){
				/**** start approval process ****/
				ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
				Map<String,String> properties = new HashMap<String, String>();
				properties.put("pv_deviceId",String.valueOf(domain.getId()));
				properties.put("pv_deviceTypeId",domain.getType().getType());
				ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);
				List<Task> tasks = processService.getCurrentTasks(processInstance);
				List<WorkflowDetails> workflowDetails = WorkflowDetails.create(domain, tasks, ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW, "0");
				/**** Supporting members status changed to pending ****/
				RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,domain.getId());
				List<SupportingMember> supportingMembers = rulesSuspensionMotion.getSupportingMembers();
				Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
				for(SupportingMember i:supportingMembers){
					if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
						i.setDecisionStatus(status);
						i.setRequestReceivedOn(new Date());
						i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
						User user = null;
						try {
							user = User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							logger.debug("populateNew", e);
							model.addAttribute("error",e.getParameter());
							e.printStackTrace();
						}
						Credential credential=user.getCredential();							
						/**** Updating WorkflowDetails ****/
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
	
	@Override
	protected void preValidateUpdate(final RulesSuspensionMotion domain,
            final BindingResult result, 
            final HttpServletRequest request) {
		/**** populate supporting members before validation ****/
		String role = request.getParameter("role");	
		populateSupportingMembers(domain,role,request);
	}
	
	@Override
	protected void customValidateUpdate(final RulesSuspensionMotion domain,
            final BindingResult result, 
            final HttpServletRequest request) {		
		String role = request.getParameter("role");	
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
		}
		/** Basic Fields Validation **/
		if(domain.getHouseType()==null){
			result.rejectValue("houseType","HousetypeEmpty");
		}
		if(domain.getType()==null){
			result.rejectValue("type","MotionTypeEmpty");
		}
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
		}
		if(domain.getPrimaryMember()==null){
			result.rejectValue("primaryMember","PrimaryMemberEmpty");
		}
		if(domain.getSubject()==null || domain.getSubject().isEmpty()){
			result.rejectValue("subject", "SubjectEmpty");
		}
		if(domain.getNoticeContent()==null || domain.getNoticeContent().isEmpty()){
			result.rejectValue("noticeContent","RulesSuspensionMotion.NoticeContentEmpty");
		}		
		/**** Number Validation ****/
		if(role.equals("RSMOIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "RSMOIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber() == null){
						result.rejectValue("number","NumberEmpty");			
						return;
					}
					//check for duplicate motion
					Boolean flag = RulesSuspensionMotion.isDuplicateNumberExist(domain.getRuleSuspensionDate(), domain.getNumber(), domain.getId(), domain.getLocale());
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
						return;
					}
				}
			}
		}
		String operation = request.getParameter("operation");
		if(operation != null && !operation.isEmpty()){			
			if(operation.equals("approval")){
				/**** Approval ****/	
				if(domain.getSupportingMembers() == null){
					result.rejectValue("supportingMembers","SupportingMembersEmpty","there are no supporting members for approval.");
				} else if(domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers","there are no supporting members for approval.");						
				} else {
					//check if request is already sent for approval
					int count=0;
					for(SupportingMember i:domain.getSupportingMembers()){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							count++;
						}
					}
					if(count==0){
						result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent","request already sent to selected supporting members.");
					}
				}
			} else if(operation.equals("submit")){
				/**** Submission ****/	
				//submission window validations
				CustomParameter submissionWindowValidationSkippedCP = CustomParameter.findByName(CustomParameter.class, "RULESSUSPENSIONMOTION_SUBMISSION_WINDOW_VALIDATIONS_SKIPPED"+"_"+domain.getHouseType().getType().toUpperCase(), "");
				if(submissionWindowValidationSkippedCP==null || submissionWindowValidationSkippedCP.getValue()==null
						|| !submissionWindowValidationSkippedCP.getValue().equals("TRUE")) {
					if(!domain.validateSubmissionDate()) {
						result.rejectValue("version","submissionWindowClosed","submission time window is closed for this rule suspension date motions!");
						return;
					}
					CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG"+"_"+domain.getHouseType().getType().toUpperCase(), "");
					if(csptOfflineSubmissionAllowedFlag!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
						if(!role.equals("RSMOIS_TYPIST")){
							if(!RulesSuspensionMotion.validateSubmissionTime(domain.getSession(), domain.getRuleSuspensionDate())) {
								result.rejectValue("version","rulessuspensionmotion.submissionWindowTimeClosed","submission time window is closed for this rules suspensionmotion date motions!");
								return;
							}
						}
					} else {
						if(!RulesSuspensionMotion.validateSubmissionTime(domain.getSession(), domain.getRuleSuspensionDate())) {
							result.rejectValue("version","rulessuspensionmotion.submissionWindowTimeClosed","submission time window is closed for this rules suspensionmotion date motions!");
							return;
						}
					}
				}
			} 
		}
    }
	
	@Override
	protected void populateUpdateIfErrors(ModelMap model, RulesSuspensionMotion domain,
			HttpServletRequest request) {		
		try {
			UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", domain.getLocale());
			if(userGroupType==null) {
				throw new ELSException("RulesSuspensionMotionController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
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
			/**** House Type ****/
			HouseType houseType = domain.getHouseType();
			if(houseType != null) {
				model.addAttribute("formattedHouseType", houseType.getName());
				model.addAttribute("houseTypeType", houseType.getType());
				model.addAttribute("houseType", houseType.getId());
			}		
			/**** Device Type ****/
			DeviceType motionType = domain.getType();
			model.addAttribute("formattedMotionType", motionType.getName());
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
			/**** UserGroupType ****/
			String usergroupType=request.getParameter("usergroupType");
			if(usergroupType!=null){
				model.addAttribute("usergroupType",usergroupType);
			}else{
				usergroupType=(String) request.getSession().getAttribute("usergroupType");
				model.addAttribute("usergroupType",usergroupType);
				request.getSession().removeAttribute("usergroupType");
			}
			/***** UserGroup *******/
			UserGroup userGroup = this.populateObjectExtendingBaseDomainByID(request, "usergroup", UserGroup.class, domain.getLocale());
			if(userGroup!=null){
				model.addAttribute("usergroup",userGroup.getId());
			}else{
				String strUserGroup=(String) request.getSession().getAttribute("usergroup");
				userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
				model.addAttribute("usergroup",strUserGroup);
				request.getSession().removeAttribute("usergroup");
			}
			/**** Ministries & SubDepartments ****/
			Date currentDate = new Date();
			if(currentDate.equals(rotationOrderPubDate) || currentDate.after(rotationOrderPubDate)) {
				List<Ministry> ministries = Ministry.
						findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, domain.getLocale());
				model.addAttribute("ministries",ministries);				
				//Populate Ministry
				List<Ministry> selectedministries=domain.getMinistries();
				if(selectedministries!=null && !selectedministries.isEmpty()){
					model.addAttribute("selectedministries",selectedministries);						
					/**** Sub Departments ****/
					List<SubDepartment> subDepartments=new ArrayList<SubDepartment>();
					for(Ministry m:selectedministries){
						List<SubDepartment> assignedSubDepartments = MemberMinister.
								findAssignedSubDepartments(m, selectedSession.getEndDate(), domain.getLocale());
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
				if(supportingMember.isActiveMemberOn(new Date(), domain.getLocale())){
					supportingMembers.add(supportingMember);
				}
			}
			model.addAttribute("supportingMembers", supportingMembers);			
			//Populate PrimaryMemberName + supportingMemberNames
			String memberNames = domain.getPrimaryMember().getFullname() + "," + supportingMemberNames;
			model.addAttribute("memberNames",memberNames);		
			/**** Number ****/
			if(domain.getNumber()!=null){
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
			}
			/** populate session dates as possible adjourning dates **/
			if(selectedSession!=null && selectedSession.getId()!=null) {
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
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
				model.addAttribute("formattedCreationDate", FormaterUtil.formatDateToString(domain.getCreationDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, domain.getLocale()));
			}
			//Populate createdby
			model.addAttribute("createdBy",domain.getCreatedBy());
			/**** Referenced Motions Starts ****/
			CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
					findByName(CustomParameter.class, "RSMOIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
			if(clubbedReferencedEntitiesVisibleUserGroups != null){
				List<UserGroupType> userGroupTypes = 
						this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(UserGroupType.class, clubbedReferencedEntitiesVisibleUserGroups.getValue(), ",", domain.getLocale());
				Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
				//populate referencedEntities
				if(isUserGroupAllowed){
					if(domain.getParent()!=null){
						model.addAttribute("formattedParentNumber",FormaterUtil.formatNumberNoGrouping(domain.getParent(), domain.getLocale()));
						model.addAttribute("parent",domain.getParent().getId());
					}
					
					// Populate clubbed entities
					List<Reference> clubEntityReferences = RulesSuspensionMotionController.populateClubbedEntityReferences(domain, domain.getLocale());
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
			/**** Start workflow related things ****/
			// set End Flag and Level in case of assistant/section officer
			if(userGroupType !=null 
					&& (userGroupType.getType().equals("assistant") || userGroupType.getType().equals("section_officer"))
			){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				//populate PUT UP OPTIONS
				if(!internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT)){
					if(recommendationStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
						RulesSuspensionMotionController.populateInternalStatus(model,recommendationStatus.getType(),usergroupType,domain.getLocale(),motionType.getType());
					} else {
						RulesSuspensionMotionController.populateInternalStatus(model,internalStatus.getType(),usergroupType,domain.getLocale(),motionType.getType());
					}					
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
				//Populate Actors
				if(internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_ADMISSION)
							||internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_REJECTION)
							||internalStatus.getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_REJECTION)){
					List<Reference> actors=WorkflowConfig.
							findRulesSuspensionMotionActorsVO(domain, internalStatus, userGroup, 1, domain.getLocale());
					model.addAttribute("actors", actors);
				}
			}else if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("clerk")){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
			}
			
			/** setting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
			CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"RSMOIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
			if(remarksForDecisionAllowed!=null) {
				List<UserGroupType> userGroupTypes = 
						this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", remarksForDecisionAllowed.getValue(), ",", domain.getLocale());
				Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
				if(isUserGroupAllowed){
					domain.setRemarksAboutDecision(domain.getRemarks());
				}
			}			
			
			/** error notification **/
			model.addAttribute("type", "error");
			model.addAttribute("msg", "update_failed");
		} catch(ELSException e) {
			model.addAttribute("error", e.getParameter());
		} catch(Exception e) {
			model.addAttribute("error", e.getMessage());
		}		
	}
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final RulesSuspensionMotion domain,
			final HttpServletRequest request) throws ELSException, ParseException {
		/**** Status ,Internal Status,Recommendation Status,adjourning date,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String locale = domain.getLocale();
		String operation=request.getParameter("operation");
		String role = request.getParameter("role");
		UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		if(userGroupType==null) {
			throw new ELSException("RulesSuspensionMotionController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
		}
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&& domain.getType()!=null && domain.getPrimaryMember()!=null
				&& domain.getNoticeContent()!=null && !domain.getNoticeContent().isEmpty()){
			if(userGroupType.getType()!=null&&!(userGroupType.getType().isEmpty())
					&&(userGroupType.getType().equals("member")||userGroupType.getType().equals("typist"))){
				if(operation!=null && !operation.isEmpty() && operation.trim().equals("submit")){
					/****  submission date is set ****/
					if(domain.getSubmissionDate()==null){
						domain.setSubmissionDate(new Date());
					}
					/**** only those supporting memebrs will be included who have approved the requests ****/
					List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
					if(domain.getSupportingMembers()!=null){
						if(!domain.getSupportingMembers().isEmpty()){
							CustomParameter supportingMemberAutoApprovalAllowedTo = 
									CustomParameter.findByName(CustomParameter.class, 
											"RSMOIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
							for(SupportingMember i:domain.getSupportingMembers()){								
								if(supportingMemberAutoApprovalAllowedTo != null) {
									if(supportingMemberAutoApprovalAllowedTo.getValue().contains(role)) {
										supportingMembers.add(i);
									} else {
										String decisionStatusType =i.getDecisionStatus().getType().trim();
										if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
											supportingMembers.add(i);
										}
									}
								} else {
									String decisionStatusType =i.getDecisionStatus().getType().trim();
									if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
										supportingMembers.add(i);
									}
								}								
							}
							domain.setSupportingMembers(supportingMembers);
						}
					}								
					/**** Status,Internal status and recommendation status is set to submit ****/
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);
				} else {
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.RULESSUSPENSIONMOTION_COMPLETE, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);
				}
			}			
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RULESSUSPENSIONMOTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}		
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/		
		CustomParameter assistantProcessedAllowed = CustomParameter.
				findByName(CustomParameter.class,"RSMOIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
		if(assistantProcessedAllowed != null){
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", assistantProcessedAllowed.getValue(), ",", locale);
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
			if(isUserGroupAllowed){
				RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, domain.getId());
				String internalStatusType = rulesSuspensionMotion.getInternalStatus().getType();
				if(internalStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT)) {
					Status ASSISTANT_PROCESSED = Status.
							findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}
			}
		}
		
		/** setting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
		CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"RSMOIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
		if(remarksForDecisionAllowed!=null) {
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", remarksForDecisionAllowed.getValue(), ",", locale);
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
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
		domain.setEditedAs(userGroupType.getName());
	}
	
	@Override
	protected void populateAfterUpdate(final ModelMap model, final RulesSuspensionMotion domain,
			final HttpServletRequest request) throws ELSException, ParseException {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Supporting Member Workflow ****/
		String operation=request.getParameter("operation");
		if(operation != null && !operation.isEmpty()){
			if(operation.equals("approval")){
				/**** start approval process ****/
				ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
				Map<String,String> properties = new HashMap<String, String>();
				properties.put("pv_deviceId",String.valueOf(domain.getId()));
				properties.put("pv_deviceTypeId",domain.getType().getType());
				ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
				List<Task> tasks=processService.getCurrentTasks(processInstance);
				List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain, tasks, ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW, "0");
				/**** Supporting members status changed to pending ****/
				RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,domain.getId());
				List<SupportingMember> supportingMembers=rulesSuspensionMotion.getSupportingMembers();
				Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
				for(SupportingMember i:supportingMembers){
					if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
						i.setDecisionStatus(status);
						i.setRequestReceivedOn(new Date());
						i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
						User user = null;
						try {
							user = User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							logger.debug("populateNew", e);
							model.addAttribute("error",e.getParameter());
							e.printStackTrace();
						}
						Credential credential=user.getCredential();							
						/**** Updating WorkflowDetails ****/
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
				try {
					UserGroupType usergroupType = null;					
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=domain.getActor();
					String level="";
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
					/**** Stale State Exception ****/
					RulesSuspensionMotion rulesSuspensionMotion=RulesSuspensionMotion.findById(RulesSuspensionMotion.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								Workflow workflow = rulesSuspensionMotion.findWorkflowFromStatus();								
								
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
								rulesSuspensionMotion.setWorkflowDetailsId(workflowDetails.getId());							
							}
						}
					}
					/**** Workflow Started ****/
					rulesSuspensionMotion.setWorkflowStarted("YES");
					rulesSuspensionMotion.setWorkflowStartedOn(new Date());
					rulesSuspensionMotion.setTaskReceivedOn(new Date());					
					rulesSuspensionMotion.simpleMerge();
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
				} catch(Exception e){
					model.addAttribute("error", e.getMessage());
				}
			}
		}
	}
	
	/*
	 * This method is used to view the approval status of a bill from the supporting members
	 */
	@RequestMapping(value="/status/{rulessuspensionmotion}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("rulessuspensionmotion") final String rulessuspensionmotion){
		RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,Long.parseLong(rulessuspensionmotion));
		List<SupportingMember> supportingMembers = rulesSuspensionMotion.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "rulessuspensionmotion/supportingmember";
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
		return "rulessuspensionmotion/citation";
	}
	
	@RequestMapping(value="/usercitations/{deviceType}",method=RequestMethod.GET)
	public String getUserCitations(final HttpServletRequest request, final Locale locale,
			@PathVariable("deviceType")  final Long type,
			final ModelMap model){
		DeviceType deviceType = DeviceType.findById(DeviceType.class,type);
		Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), "");
		List<UserCitation> citations = UserCitation.findByDeviceTypeAndCredential(deviceType, credential);
		model.addAttribute("citations",citations);
		return "rulessuspensionmotion/citation";
	}
	
	@RequestMapping(value="/revisions/{rulesSuspensionMotionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("rulesSuspensionMotionId")  final Long rulesSuspensionMotionId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=RulesSuspensionMotion.getRevisions(rulesSuspensionMotionId,locale.toString());
		RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, rulesSuspensionMotionId);
		model.addAttribute("selectedDeviceType", rulesSuspensionMotion.getType().getType());
		model.addAttribute("drafts",drafts);
		return "rulessuspensionmotion/revisions";
	}
	
	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "rulessuspensionmotion/contacts";
	}
	
	/**** BULK SUBMISSION (MEMBER) ****/
	@RequestMapping(value="/bulksubmission", method=RequestMethod.GET)
	public String getBulkSubmissionView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("motionType");
		String strLocale = locale.toString();
		String strItemsCount = request.getParameter("itemscount");

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				Integer itemsCount = Integer.parseInt(strItemsCount);
				Member primaryMember = Member.findMember(this.getCurrentUser().getFirstName(),
						this.getCurrentUser().getMiddleName(),
						this.getCurrentUser().getLastName(),
						this.getCurrentUser().getBirthDate(),
						strLocale);
				List<RulesSuspensionMotion> rulesSuspensionMotions = new ArrayList<RulesSuspensionMotion>();
				if(primaryMember != null){
					rulesSuspensionMotions = RulesSuspensionMotion.findAllReadyForSubmissionByMember(session, primaryMember,deviceType, itemsCount, strLocale);	
				}	
				model.addAttribute("rulesSuspensionMotions", rulesSuspensionMotions);
				model.addAttribute("size", rulesSuspensionMotions.size());

				String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupType", userGroupType);
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "rulessuspensionmotion/bulksubmission";
	}
	
	/**
	 * We want to provide a guarantee that all the motions submitted by a 
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

			List<RulesSuspensionMotion> rulesSuspensionMotions = new ArrayList<RulesSuspensionMotion>();
			for(String i : items) {
				Long id = Long.parseLong(i);
				RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, id);

				/**** Update Supporting Member ****/
				List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
				Status timeoutStatus = Status.findByType(
						ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
				if(rulesSuspensionMotion.getSupportingMembers() != null
						&& ! rulesSuspensionMotion.getSupportingMembers().isEmpty()) {
					for(SupportingMember sm : rulesSuspensionMotion.getSupportingMembers()) {
						if(sm.getDecisionStatus().getType().equals(
								ApplicationConstants.SUPPORTING_MEMBER_NOTSEND) ||
								sm.getDecisionStatus().getType().equals(
										ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
							/**** Update Supporting Member ****/
							sm.setDecisionStatus(timeoutStatus);
							sm.setApprovalDate(new Date());	
							sm.setApprovedAdjourningDate(rulesSuspensionMotion.getRuleSuspensionDate());
							sm.setApprovedText(rulesSuspensionMotion.getNoticeContent());
							sm.setApprovedSubject(rulesSuspensionMotion.getSubject());
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

					rulesSuspensionMotion.setSupportingMembers(supportingMembers);
				}
				
				/**** Update Status(es) ****/
				Status newstatus = Status.findByFieldName(Status.class, "type", 
						ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT, rulesSuspensionMotion.getLocale());
				rulesSuspensionMotion.setStatus(newstatus);
				rulesSuspensionMotion.setInternalStatus(newstatus);
				rulesSuspensionMotion.setRecommendationStatus(newstatus);

				/**** Edited On, Edited By and Edited As is set ****/
				rulesSuspensionMotion.setSubmissionDate(new Date());
				rulesSuspensionMotion.setEditedOn(new Date());
				rulesSuspensionMotion.setEditedBy(this.getCurrentUser().getActualUsername());

				String strUserGroupType = request.getParameter("usergroupType");
				if(strUserGroupType != null) {
					UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class,
							"type", strUserGroupType, rulesSuspensionMotion.getLocale());
					rulesSuspensionMotion.setEditedAs(userGroupType.getName());
				}

				/**** Bulk Submitted ****/
				rulesSuspensionMotion.setBulkSubmitted(true);
				rulesSuspensionMotion = rulesSuspensionMotion.merge();
				rulesSuspensionMotions.add(rulesSuspensionMotion);
			}

			model.addAttribute("rulesSuspensionMotions", rulesSuspensionMotions);
		}

		return "rulessuspensionmotion/bulksubmissionack";
	}
	
	/**** Set Submission Window (Section Officer) ****/
	@RequestMapping(value="/submissionwindow", method=RequestMethod.GET)
	public String getSubmissionWindow(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("motionType");
		String strRuleSuspensionDate = request.getParameter("ruleSuspensionDate");
		String strLocale = locale.toString();		

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strRuleSuspensionDate != null && !(strRuleSuspensionDate.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
				model.addAttribute("sessionForSubmissionWindow", session.getId());
				
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				model.addAttribute("motionTypeForSubmissionWindow", deviceType.getId());

				Date ruleSuspensionDate = FormaterUtil.formatStringToDate(strRuleSuspensionDate, ApplicationConstants.SERVER_DATEFORMAT, strLocale);
				model.addAttribute("ruleSuspensionDateForSubmissionWindow", strRuleSuspensionDate);
				String formattedRuleSuspensionDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(ruleSuspensionDate, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", strLocale);
				model.addAttribute("formattedRuleSuspensionDate", formattedRuleSuspensionDate);
				
				/** restriction flag for update on next date & onwards from adjourning date **/
				if(DateUtil.compareDatePartOnly(ruleSuspensionDate, new Date())<0) {
					model.addAttribute("isRuleSuspensionDateInPast", "yes");
				} else {
					model.addAttribute("isRuleSuspensionDateInPast", "no");
				}
				
				/** Submission start time parameter **/
				String submissionStartTimeParameter = session.getParameter(deviceType.getType()+"_submissionStartTime_"+strRuleSuspensionDate);
				if(submissionStartTimeParameter!=null && !submissionStartTimeParameter.isEmpty()
						&& submissionStartTimeParameter.contains(":")) {
					String startTimeHour = submissionStartTimeParameter.split(":")[0];
					model.addAttribute("startTimeHour", FormaterUtil.formatNumbersInGivenText(startTimeHour, strLocale));
					String startTimeMinute = submissionStartTimeParameter.split(":")[1];
					model.addAttribute("startTimeMinute", FormaterUtil.formatNumbersInGivenText(startTimeMinute, strLocale));
				} else {
					String submissionStartTimeDefaultSessionParameter = session.getParameter(deviceType.getType()+"_submissionStartTime");
					if(submissionStartTimeDefaultSessionParameter!=null && !submissionStartTimeDefaultSessionParameter.isEmpty()
							&& submissionStartTimeDefaultSessionParameter.contains(":")) {
						String startTime[] = submissionStartTimeDefaultSessionParameter.split(" ");
						String startTimeHour = startTime[1].split(":")[0];
						model.addAttribute("startTimeHour", FormaterUtil.formatNumbersInGivenText(startTimeHour, strLocale));
						String startTimeMinute = startTime[1].split(":")[1];
						model.addAttribute("startTimeMinute", FormaterUtil.formatNumbersInGivenText(startTimeMinute, strLocale));
					} else {
						CustomParameter csptsubmissionStartTime = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_SUBMISSIONSTARTTIME_"+houseType.getType().toUpperCase(), "");
						if(csptsubmissionStartTime!=null) {
							String submissionStartTimeDefaultCustomParameter = csptsubmissionStartTime.getValue();
							if(submissionStartTimeDefaultCustomParameter!=null && !submissionStartTimeDefaultCustomParameter.isEmpty()
									&& submissionStartTimeDefaultCustomParameter.contains(":")) {
								String startTimeHour = submissionStartTimeDefaultCustomParameter.split(":")[0];
								model.addAttribute("startTimeHour", FormaterUtil.formatNumbersInGivenText(startTimeHour, strLocale));
								String startTimeMinute = submissionStartTimeDefaultCustomParameter.split(":")[1];
								model.addAttribute("startTimeMinute", FormaterUtil.formatNumbersInGivenText(startTimeMinute, strLocale));
							}
						}						
					}
				}
				
				/** Submission end time parameter **/
				String submissionEndTimeParameter = session.getParameter(deviceType.getType()+"_submissionEndTime_"+strRuleSuspensionDate);
				if(submissionEndTimeParameter!=null && !submissionEndTimeParameter.isEmpty()
						&& submissionEndTimeParameter.contains(":")) {
					String endTimeHour = submissionEndTimeParameter.split(":")[0];
					model.addAttribute("endTimeHour", FormaterUtil.formatNumbersInGivenText(endTimeHour, strLocale));
					String endTimeMinute = submissionEndTimeParameter.split(":")[1];
					model.addAttribute("endTimeMinute", FormaterUtil.formatNumbersInGivenText(endTimeMinute, strLocale));
				} else {
					String submissionEndTimeDefaultSessionParameter = session.getParameter(deviceType.getType()+"_submissionEndTime");
					if(submissionEndTimeDefaultSessionParameter!=null && !submissionEndTimeDefaultSessionParameter.isEmpty()
							&& submissionEndTimeDefaultSessionParameter.contains(":")) {
						String endTime[] = submissionEndTimeDefaultSessionParameter.split(" ");
						String endTimeHour = endTime[1].split(":")[0];
						model.addAttribute("endTimeHour", FormaterUtil.formatNumbersInGivenText(endTimeHour, strLocale));
						String endTimeMinute = endTime[1].split(":")[1];
						model.addAttribute("endTimeMinute", FormaterUtil.formatNumbersInGivenText(endTimeMinute, strLocale));
					} else {
						CustomParameter csptsubmissionEndTime = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_SUBMISSIONENDTIME_"+houseType.getType().toUpperCase(), "");
						if(csptsubmissionEndTime!=null) {
							String submissionEndTimeDefaultCustomParameter = csptsubmissionEndTime.getValue();
							if(submissionEndTimeDefaultCustomParameter!=null && !submissionEndTimeDefaultCustomParameter.isEmpty()
									&& submissionEndTimeDefaultCustomParameter.contains(":")) {
								String endTimeHour = submissionEndTimeDefaultCustomParameter.split(":")[0];
								model.addAttribute("endTimeHour", FormaterUtil.formatNumbersInGivenText(endTimeHour, strLocale));
								String endTimeMinute = submissionEndTimeDefaultCustomParameter.split(":")[1];
								model.addAttribute("endTimeMinute", FormaterUtil.formatNumbersInGivenText(endTimeMinute, strLocale));
							}
						}						
					}
				}			
				
				String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupTypeForSubmissionWindow", userGroupType);
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("error", "SOME_ERROR");
			}
		} else {
			model.addAttribute("error", "REQUEST_PARAM_EMPTY");
		}

		return "rulessuspensionmotion/submissionwindow";
	}
	
	@Transactional
	@RequestMapping(value="/submissionwindow", method=RequestMethod.POST)
	public @ResponseBody String updateSubmissionWindow(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String updateStatus = "error";
		
		String strSession = request.getParameter("session");
		String strDeviceType = request.getParameter("motionType");
		String strRuleSuspensionDate = request.getParameter("ruleSuspensionDate");
		String strStartTimeHour = request.getParameter("startTimeHour");
		String strStartTimeMinute = request.getParameter("startTimeMinute");
		String strEndTimeHour = request.getParameter("endTimeHour");
		String strEndTimeMinute = request.getParameter("endTimeMinute");
		String usergroupType = request.getParameter("usergroupTypeForSubmissionWindow");
		String strLocale = locale.toString();	
		
		/** restrict update for member **/
		boolean isAllowedToUpdate = true;
		if(usergroupType!=null && usergroupType.equals(ApplicationConstants.MEMBER)) {
			isAllowedToUpdate = false;
		}		
		if(isAllowedToUpdate==false) {
			return "error";
		}

		if(strSession != null && !(strSession.isEmpty())		
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strStartTimeHour != null && !(strStartTimeHour.isEmpty())
				&& strStartTimeMinute != null && !(strStartTimeMinute.isEmpty())
				&& strEndTimeHour != null && !(strEndTimeHour.isEmpty())
				&& strEndTimeMinute != null && !(strEndTimeMinute.isEmpty())
				&& strRuleSuspensionDate != null && !(strRuleSuspensionDate.isEmpty())) {
			
			try {
				Session session = Session.findById(Session.class, Long.parseLong(strSession));				
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));				
				//Date adjourningDate = FormaterUtil.formatStringToDate(strAdjourningDate, ApplicationConstants.SERVER_DATEFORMAT, strLocale);
				
				CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(deploymentServerCP!=null && deploymentServerCP.getValue().equals("TOMCAT")){
					strStartTimeHour = new String(strStartTimeHour.getBytes("ISO-8859-1"),"UTF-8");	
					strStartTimeMinute = new String(strStartTimeMinute.getBytes("ISO-8859-1"),"UTF-8");	
					strEndTimeHour = new String(strEndTimeHour.getBytes("ISO-8859-1"),"UTF-8");	
					strEndTimeMinute = new String(strEndTimeMinute.getBytes("ISO-8859-1"),"UTF-8");	
				}
				
				Integer startTimeHour = Integer.parseInt(strStartTimeHour);
				Integer startTimeMinute = Integer.parseInt(strStartTimeMinute);		
				Integer endTimeHour = Integer.parseInt(strEndTimeHour);
				Integer endTimeMinute = Integer.parseInt(strEndTimeMinute);
				
				if(startTimeHour.toString().length()==1) {
					strStartTimeHour = "0" + startTimeHour;
				} else {
					strStartTimeHour = startTimeHour.toString();
				}
				
				if(startTimeMinute.toString().length()==1) {
					strStartTimeMinute = "0" + startTimeMinute;
				} else {
					strStartTimeMinute = startTimeMinute.toString();
				}
				
				if(endTimeHour.toString().length()==1) {
					strEndTimeHour = "0" + endTimeHour;
				} else {
					strEndTimeHour = endTimeHour.toString();
				}
				
				if(endTimeMinute.toString().length()==1) {
					strEndTimeMinute = "0" + endTimeMinute;
				} else {
					strEndTimeMinute = endTimeMinute.toString();
				}				
				
				Map<String, String> parameters = session.getParameters();
				
				/** Submission start time parameter **/
				String submissionStartTimeForAdjourningDate = strStartTimeHour + ":" + strStartTimeMinute;
				parameters.put(deviceType.getType()+"_submissionStartTime_"+strRuleSuspensionDate, submissionStartTimeForAdjourningDate);
				
				/** Submission end time parameter **/
				String submissionEndTimeForAdjourningDate = strEndTimeHour + ":" + strEndTimeMinute;
				parameters.put(deviceType.getType()+"_submissionEndTime_"+strRuleSuspensionDate, submissionEndTimeForAdjourningDate);
			
				/** Update parameters in the session **/
				session.setParameters(parameters);
				session.merge();	
				
				updateStatus = "success";
			} catch (Exception e) {
				e.printStackTrace();
				updateStatus = "error";
			}
		}

		return updateStatus;
	}
	
	private void populateSupportingMembers(final RulesSuspensionMotion domain,final String role,final HttpServletRequest request){
		/**** Supporting Members selected by Member in new/edit ****/
		String[] selectedSupportingMembers=request.getParameterValues("selectedSupportingMembers");
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
		/**** Supporting Members which are already present in domain ****/
		List<SupportingMember> members = new ArrayList<SupportingMember>();
		if(domain.getId()!=null){
			RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,domain.getId());
			members = rulesSuspensionMotion.getSupportingMembers();
		}		
		/**** New Status ****/
		Status notsendStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_NOTSEND, domain.getLocale());
		/**** New Supporting Members+Already present Supporting Members ****/
		List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
		if(selectedSupportingMembers!=null){
			if(selectedSupportingMembers.length>0){				
				for(String i:selectedSupportingMembers){
					SupportingMember supportingMember=null;
					Member member=Member.findById(Member.class, Long.parseLong(i));
					/**** If supporting member is already present then do nothing ****/
					for(SupportingMember j:members){
						if(j.getMember().getId()==member.getId()){
							supportingMember=j;
							break;
						}
					}
					/**** New Supporting Member ****/
					if(supportingMember==null){
						supportingMember=new SupportingMember();
						supportingMember.setMember(member);
						supportingMember.setLocale(domain.getLocale());
						supportingMember.setDecisionStatus(notsendStatus);
						/** Auto approval for submission by typist etc. roles **/
						CustomParameter supportingMemberAutoApprovalAllowedTo = 
								CustomParameter.findByName(CustomParameter.class, 
										"RSMOIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
						if(supportingMemberAutoApprovalAllowedTo != null) {
							if(supportingMemberAutoApprovalAllowedTo.getValue().contains(role)) {
								Status APPROVED = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_APPROVED, domain.getLocale());
								supportingMember.setDecisionStatus(APPROVED);								
								supportingMember.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
								supportingMember.setApprovalDate(new Date());
								supportingMember.setApprovedSubject(domain.getSubject());
								supportingMember.setApprovedText(domain.getNoticeContent());							
							}
						}
					}
					/*** List is updated ****/
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
	}
	
	public static List<Reference> populateClubbedEntityReferences(RulesSuspensionMotion domain, String locale) {
		List<Reference> references = new ArrayList<Reference>();
		List<ClubbedEntity> clubbedEntities=RulesSuspensionMotion.findClubbedEntitiesByPosition(domain);
		if(clubbedEntities!=null){
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getRulesSuspensionMotion().getNumber()));
				reference.setNumber(String.valueOf(ce.getRulesSuspensionMotion().getId()));
				references.add(reference);
			}
		}
		return references;
	}	
	
	private List<Role> delimitedStringToRoleList(final String delimitedRoles,
			final String delimiter,
			final String locale) {
		List<Role> roles = new ArrayList<Role>();
		
		String[] strRoles = delimitedRoles.split(delimiter);
		for(String strRole : strRoles) {
			Role role = Role.findByType(strRole, locale);
			roles.add(role);
		}
		
		return roles;
	}
	
	/**
	 * Return true if @param role is present in the collection
	 * @param roles 
	 */
	private boolean isRoleExists(final List<Role> roles,
			final Role role) {
		for(Role r : roles) {
			if(r != null && role != null){
				if(role.getId().equals(r.getId())) {
					return true;
				}
			}
		}
		
		return false;
	}

}