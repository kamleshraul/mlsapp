package org.mkcl.els.controller.smis;

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
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.SpecialMentionNoticeDraft;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("specialmentionnotice")
public class SpecialMentionNoticeController extends GenericController<SpecialMentionNotice> {	
	
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
					/** populate session dates as possible specialMntionNotice dates **/
					session = Session.find(year, sessionType, houseType);
					if(session!=null && session.getId()!=null) {
						List<Date> sessionDates = session.findAllSessionDatesHavingNoHoliday();
						model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", locale));
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
				deviceTypes = DeviceType.findDeviceTypesStartingWith("notices_specialmention", locale);

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
				
				/**** added by sandeep singh(jan 27 2013) ****/
				/****
				 * Custom Parameter To Determine The Usergroup and usergrouptype
				 * of amois users . here we are determining what status will be
				 * shown to a particular user.
				 ****/
				List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
				UserGroup userGroup = null;
				UserGroupType userGroupType = null;
				if (userGroups != null && !userGroups.isEmpty()) {
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"SMIS_ALLOWED_USERGROUPTYPES", "");
					if (customParameter != null) {
						List<UserGroupType> configuredUserGroupTypes = 
								QuestionController.delimitedStringToUGTList(customParameter.getValue(), ",", locale);
						
						userGroup = QuestionController.getUserGroup(userGroups, configuredUserGroupTypes, session, locale);
						userGroupType = userGroup.getUserGroupType();
						
						model.addAttribute("usergroup", userGroup.getId());
						model.addAttribute("usergroupType", userGroupType.getType());
					} else {
						model.addAttribute("errorcode","smis_allowed_usergroups_notset");
					}
				} else {
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
				List<Date> sessionDates = session.findAllSessionDatesHavingNoHoliday();
				model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", locale));				
				Date defaultSpecialMentionNoticeDate = null;
				if(userGroupType.getType().equals(ApplicationConstants.MEMBER)) {
					defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(session, true);
				} else {
					defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(session, false);
				}
				model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(defaultSpecialMentionNoticeDate, ApplicationConstants.SERVER_DATEFORMAT));
				
				
				/*Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, 1); 
				Date currentDatePlusOne = c.getTime();
				
				 if (currentDatePlusOne.compareTo(defaultSpecialMentionNoticeDate) < 0){
				model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(defaultSpecialMentionNoticeDate, ApplicationConstants.SERVER_DATEFORMAT));
				 }
				 else{
				model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(currentDatePlusOne, ApplicationConstants.SERVER_DATEFORMAT));
				 }*/
				/**** Motion Status Allowed ****/
				CustomParameter allowedStatus = CustomParameter.findByName(CustomParameter.class,
								"SPECIALMENTIONNOTICE_GRID_STATUS_ALLOWED_"+ userGroupType.getType().toUpperCase(),"");
				List<Status> status = new ArrayList<Status>();
				if (allowedStatus != null) {
					status = Status.findStatusContainedIn(allowedStatus.getValue(),locale);
				} else {
					CustomParameter defaultAllowedStatus = CustomParameter.findByName(CustomParameter.class,
									"SPECIALMENTIONNOTICE_GRID_STATUS_ALLOWED_BY_DEFAULT","");
					if (defaultAllowedStatus != null) {
						status = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
					} else {
						model.addAttribute("errorcode","specialmentionnotice_status_allowed_by_default_not_set");
					}
				}
				model.addAttribute("status", status);
				/**** Motion Departments Allowed ****/
				Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
				CustomParameter subDepartmentFilterAllowedFor = 
						CustomParameter.findByName(CustomParameter.class, "SMIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
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
							throw new ELSException("StarredQuestionController.populateModule/4", 
									"SUBDEPARTMENT parameter is not set for Username: " + currentUser.getUsername());
						}
					}
				}
				else {
					throw new ELSException("StarredQuestionController.populateModule/4", 
							"QIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR key is not set as CustomParameter");
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
					} else if (i.getType().contains("SMIS_CLERK")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().startsWith("SMIS_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					}
				}
			} catch (ELSException e) {
				model.addAttribute("SpecialMentionNoticeController", e.getParameter());
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
							CustomParameter.findByName(CustomParameter.class,"SMIS_MEMBERGRID_ALLOWED_FOR", "");
				 if(memberGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
							 this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(Role.class, memberGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
								this.isObjectExtendingBaseDomainAvailableInList(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){						   
						   newUrlPattern=urlPattern+"?usergroup=member";
						   String selectedSpecialMentionNoticeDate = request.getParameter("specialMentionNoticeDate");
						   if(selectedSpecialMentionNoticeDate!=null && !selectedSpecialMentionNoticeDate.isEmpty()) {
							   newUrlPattern=newUrlPattern+"&specialMentionNoticeDate=selected";
						   }
						   return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("SpecialMentionNoticeController.modifyURLPattern/4", 
								"SMIS_MEMBERGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
				 
				 CustomParameter typistGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"SMIS_TYPISTGRID_ALLOWED_FOR", "");
				 if(typistGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
							QuestionController.delimitedStringToRoleList(typistGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
							QuestionController.isRoleExists(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){
						 newUrlPattern=urlPattern+"?usergroup=typist";
						 String selectedSpecialMentionNoticeDate = request.getParameter("specialMentionNoticeDate");
					     if(selectedSpecialMentionNoticeDate!=null && !selectedSpecialMentionNoticeDate.isEmpty()) {
						     newUrlPattern=newUrlPattern+"&specialMentionNoticeDate=selected";
					     }
					     return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("SpecialMentionNoticeDateController.modifyURLPattern/4", 
								"SMIS_TYPISTGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
				 
				 CustomParameter assistantGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"SMIS_ASSISTANTGRID_ALLOWED_FOR", "");
				 if(assistantGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
						QuestionController.delimitedStringToRoleList(assistantGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
						QuestionController.isRoleExists(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){
						 newUrlPattern=urlPattern+"?usergroup=assistant";
						 String selectedSpecialMentionNoticeDate = request.getParameter("specialMentionNoticeDate");
						 if(selectedSpecialMentionNoticeDate!=null && !selectedSpecialMentionNoticeDate.isEmpty()) {
							 newUrlPattern=newUrlPattern+"&specialMentionNoticeDate=selected";
						 }
						 return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("SpecialMentionNoticeController.modifyURLPattern/4", 
								"SMIS_ASSISTANTGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
			 }
			 else{
				 throw new ELSException("SpecialMentionNoticeController.modifyURLPattern/4",
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
						CustomParameter.findByName(CustomParameter.class,"SMIS_NEW_OPERATION_ALLOWED_TO","");
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
			final SpecialMentionNotice domain, 
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
//				/**** Constituency ****/
//				Constituency constituency = Member.findConstituency(member, new Date());
//				if(constituency != null){
//					model.addAttribute("constituency", constituency.getDisplayName());
//				}	
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
						Ministry ministry=domain.getMinistry();
						if(ministry!=null){
							model.addAttribute("ministrySelected",ministry.getId());						
							/**** Sub Departments ****/
							List<SubDepartment> subDepartments=
									MemberMinister.findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
							model.addAttribute("subDepartments",subDepartments);
							SubDepartment subDepartment=domain.getSubDepartment();
							if(subDepartment!=null){
								model.addAttribute("subDepartmentSelected",subDepartment.getId());
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
			/** populate session dates as possible specialMentionNotice dates **/
			if(selectedSession!=null && selectedSession.getId()!=null) {
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
				model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", locale));				
				Date defaultSpecialMentionNoticeDate = null;
				if(usergroupType.equals(ApplicationConstants.MEMBER)) {
					defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(selectedSession, true);
				} else {
					defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(selectedSession, false);
				}
				model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(defaultSpecialMentionNoticeDate, ApplicationConstants.SERVER_DATEFORMAT));

				/*	Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, 1); 
				Date currentDatePlusOne = c.getTime();
				Boolean dateFound= false ;
				 if (currentDatePlusOne.compareTo(defaultSpecialMentionNoticeDate) < 0){
					model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(defaultSpecialMentionNoticeDate, ApplicationConstants.SERVER_DATEFORMAT));
				 }
				 else if(!Holiday.isHolidayOnDate(currentDatePlusOne, locale)){
					model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(currentDatePlusOne, ApplicationConstants.SERVER_DATEFORMAT));
				}	
				while (!dateFound)
				{
						Date holidayDatePlusOne = c.getTime();
						model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(holidayDatePlusOne, ApplicationConstants.SERVER_DATEFORMAT));	
					
				 break;
				}	*/
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
				findByName(CustomParameter.class, "SMIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.
				findByName(CustomParameter.class, "SMIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles=authUser.getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return editUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return editUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("SMIS_")) {
				return editUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "specialmentionnotice/error";
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final SpecialMentionNotice domain,
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
//				Constituency constituency = Member.findConstituency(primaryMember, new Date());
//				if(constituency != null){
//					model.addAttribute("constituency", constituency.getDisplayName());
//				}
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
			String memberNames = domain.getPrimaryMember().getFullname() ;
			model.addAttribute("memberNames",memberNames);
			/**** Number ****/
			if(domain.getNumber()!=null){
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
			}
			/** populate session dates as possible specialMentionNotice dates **/
			if(selectedSession!=null && selectedSession.getId()!=null) {
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
				model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", domain.getLocale()));				
			}
			/**** populate specialMentionNotice date ****/

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
			
			if(domain.getDiscussionDate()!=null) {
				model.addAttribute("discussionDate", FormaterUtil.formatDateToString(domain.getDiscussionDate(), ApplicationConstants.SERVER_DATEFORMAT));
			}
			
			//Populate createdby
			model.addAttribute("createdBy",domain.getCreatedBy());
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
				if(!internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_SUBMIT)){
					if(recommendationStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
						SpecialMentionNoticeController.
						populateInternalStatus(model,recommendationStatus.getType(),userGroupType.getType(),locale,motionType.getType());
					} else {
						SpecialMentionNoticeController.
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
				if(internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_ADMISSION)
							||internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_REJECTION)
							||internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_REJECTION)){
					List<Reference> actors=WorkflowConfig.
							findSpecialMentionNoticeActorsVO(domain, internalStatus, userGroup, 1, locale);
					model.addAttribute("actors", actors);
//					if(actors!=null && !actors.isEmpty()){
//					String nextActor = actors.get(0).getId();
//					String[] actorArr = nextActor.split("#");
//					domain.setLevel(actorArr[2]);
//					domain.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
//				}
				}
			}else if(userGroupType !=null && userGroupType.getType().equals("clerk")){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
			}
			/**** remarks for final rejection ****/
			Status rejectionFinalStatus = Status.findByType(ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_REJECTION, locale);
			boolean canRemark = false;	
			String errorMessagePossible="";
			try{
				errorMessagePossible = "domain_not_found";
				if (internalStatus.getType().equals(rejectionFinalStatus.getType())) {
					errorMessagePossible = "questiondraft_not_found_for_remark";
					SpecialMentionNoticeDraft mDraft = domain.findPreviousDraft();
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
		} catch(ELSException e) {
			
		} catch(Exception e) {
			
		}
	}

	public static void populateInternalStatus(final ModelMap model, final String type,final String userGroupType,final String locale, final String motionType) {
		List<Status> internalStatuses=new ArrayList<Status>();
		try{
			CustomParameter specificDeviceStatusUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.toUpperCase()
							+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+motionType.toUpperCase()
							+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses = CustomParameter.
					findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_"+type.toUpperCase()
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
						findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus != null) {
					internalStatuses = Status.
							findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus = CustomParameter.
							findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus != null){
						internalStatuses = Status.
								findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter = CustomParameter.
								findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter != null){
							internalStatuses = Status.
									findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode", "specialmentionnotice_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus = CustomParameter.
						findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus != null) {
					internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.
							findByName(CustomParameter.class,"SPECIALMENTIONNOTICE_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter != null) {
						internalStatuses = Status.
								findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode", "specialmentionnotice_putup_options_final_notset");
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
	protected void customValidateCreate(final SpecialMentionNotice domain,
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
			result.rejectValue("noticeContent","SpecialMentionNotice.NoticeContentEmpty");
		}	
		if(domain.getMinistry()==null){
			result.rejectValue("ministry","MinistryEmpty");
		}
		if(domain.getSubDepartment()==null){
			result.rejectValue("subDepartment","SubDepartmentEmpty.domain.subDepartment");
		}
		/****  submission date is set ****/
		if(domain.getSubmissionDate()==null){
			domain.setSubmissionDate(new Date());
		}	
		/*if(role.equals("AMOIS_TYPIST")){
//			//Empty check for number
//			if(domain.getNumber()==null){
//				result.rejectValue("number","NumberEmpty");
//				return;
//			}
			//check for duplicate motions if number is entered
			if(domain.getNumber()!=null){
				Boolean flag=AdjournmentMotion.isDuplicateNumberExist(domain.getAdjourningDate(), domain.getNumber(), domain.getLocale());
				if(flag){
					result.rejectValue("number", "NonUnique","Duplicate Number");
				}
			}			
		}*/
		/**** Number Validation ****/
		if(role.equals("SMIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "SMIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");			
						return;
					}
					//check for duplicate motion
					Boolean flag=SpecialMentionNotice.isDuplicateNumberExist(domain.getSpecialMentionNoticeDate(), domain.getNumber(), domain.getId(), domain.getLocale());
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
						return;
					}
				}
			}
		}
		String operation=request.getParameter("operation");
		if(operation!=null && !operation.isEmpty()){			
			if(operation.equals("submit")){
				/**** Submission ****/	
				if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					// Empty check for Ministry
					if(domain.getMinistry()==null){
						result.rejectValue("ministry","MinistryEmpty");
					}
					// Empty check for Subdepartment
					if(domain.getSubDepartment()==null){
						result.rejectValue("subDepartment","SubDepartmentEmpty");
					}
				}

				//submission window validations
				CustomParameter submissionWindowValidationSkippedCP = CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_SUBMISSION_WINDOW_VALIDATIONS_SKIPPED"+"_"+domain.getHouseType().getType().toUpperCase(), "");
				if(submissionWindowValidationSkippedCP==null || submissionWindowValidationSkippedCP.getValue()==null
						|| !submissionWindowValidationSkippedCP.getValue().equals("TRUE")) {
					if(!domain.validateSubmissionDate()) {
						result.rejectValue("version","submissionWindowClosed","submission time window is closed for this specialmentionnotice date!");
						return;
					}
					CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG"+"_"+domain.getHouseType().getType().toUpperCase(), "");
					if(csptOfflineSubmissionAllowedFlag!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
						if(!role.equals("SMIS_TYPIST")){
							if(!SpecialMentionNotice.validateSubmissionTime(domain.getSession(), domain.getSpecialMentionNoticeDate(),domain.getSubmissionDate())) {
								result.rejectValue("version","specialmentionnotice.submissionWindowTimeClosed","submission time window is closed for this specialmentionnotice date!");
								return;
							}
						}
					} else {
						if(!SpecialMentionNotice.validateSubmissionTime(domain.getSession(), domain.getSpecialMentionNoticeDate(),domain.getSubmissionDate())) {
							result.rejectValue("version","specialmentionnotice.submissionWindowTimeClosed","submission time window is closed for this specialmentionnotice date!");
							return;
						}
					}					
				}
			}
		}
    }
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, SpecialMentionNotice domain,
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
				throw new ELSException("SpecialMentionNticeController.populateCreateIfErrors/3", 
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
			/** populate session dates as possible specialMentionNotice dates **/
			if(selectedSession!=null && selectedSession.getId()!=null) {
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
				model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", domain.getLocale()));
				
				Date defaultSpecialMentionNoticeDate = null;
				if(usergroupType.equals(ApplicationConstants.MEMBER)) {
					defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(selectedSession, true);
				} else {
					defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(selectedSession, false);
				}
				model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(defaultSpecialMentionNoticeDate, ApplicationConstants.SERVER_DATEFORMAT));

			/*	Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, 1); 
				Date currentDatePlusOne = c.getTime();
				 if (currentDatePlusOne.compareTo(defaultSpecialMentionNoticeDate) < 0 ){
				model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(defaultSpecialMentionNoticeDate, ApplicationConstants.SERVER_DATEFORMAT));
				 }
				 else{
				model.addAttribute("defaultSpecialMentionNoticeDate", FormaterUtil.formatDateToString(currentDatePlusOne, ApplicationConstants.SERVER_DATEFORMAT));
				 }*/
			}
			/**** populate specialMentionNotice date ****/
			model.addAttribute("selectedSpecialMentionNoticeDate", FormaterUtil.formatDateToString(domain.getSpecialMentionNoticeDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
			model.addAttribute("formattedSpecialMentionNoticeDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getSpecialMentionNoticeDate(), "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", domain.getLocale()));
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
	protected void populateCreateIfNoErrors(final ModelMap model, final SpecialMentionNotice domain,
			final HttpServletRequest request) throws ELSException {
		/**** Status ,Internal Status,Recommendation Status,specialMentionNotice date,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String locale = domain.getLocale();
		String operation=request.getParameter("operation");
		String role = request.getParameter("role");
		UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		if(userGroupType==null) {
			throw new ELSException("SpecialMentionNoticeController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
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
										
					/**** Status,Internal status and recommendation status is set to submit ****/
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.SPECIALMENTIONNOTICE_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);							 
				}
			} else {
				Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.SPECIALMENTIONNOTICE_COMPLETE, domain.getLocale());
				domain.setStatus(newstatus);
				domain.setInternalStatus(newstatus);
				domain.setRecommendationStatus(newstatus);
			}
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.SPECIALMENTIONNOTICE_INCOMPLETE, domain.getLocale());
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
	protected void populateAfterCreate(final ModelMap model, final SpecialMentionNotice domain,
			final HttpServletRequest request) throws ELSException, ParseException {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
	}
	
	@Override
	protected void customValidateUpdate(final SpecialMentionNotice domain,
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
			result.rejectValue("noticeContent","SpecialMentionNotice.NoticeContentEmpty");
		}	
		if(domain.getMinistry()==null){
			result.rejectValue("ministry","MinistryEmpty");
		}
		if(domain.getSubDepartment()==null){
			result.rejectValue("subDepartment","SubDepartmentEmpty.domain.subDepartment");
		}
		/*if(role.equals("AMOIS_TYPIST")){
//		//Empty check for number
//		if(domain.getNumber()==null){
//			result.rejectValue("number","NumberEmpty");
//			return;
//		}
		//check for duplicate motions if number is entered
		if(domain.getNumber()!=null){
			Boolean flag=AdjournmentMotion.isDuplicateNumberExist(domain.getAdjourningDate(), domain.getNumber(), domain.getLocale());
			if(flag){
				result.rejectValue("number", "NonUnique","Duplicate Number");
			}
		}			
		}*/
		/**** Number Validation ****/
		if(role.equals("SMIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "SMIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");			
						return;
					}
					//check for duplicate motion
					Boolean flag=SpecialMentionNotice.isDuplicateNumberExist(domain.getSpecialMentionNoticeDate(), domain.getNumber(), domain.getId(), domain.getLocale());
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
				if(operation.equals("submit")){
				/**** Submission ****/	
				//submission window validations
				CustomParameter submissionWindowValidationSkippedCP = CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_SUBMISSION_WINDOW_VALIDATIONS_SKIPPED"+"_"+domain.getHouseType().getType().toUpperCase(), "");
				if(submissionWindowValidationSkippedCP==null || submissionWindowValidationSkippedCP.getValue()==null
						|| !submissionWindowValidationSkippedCP.getValue().equals("TRUE")) {
					if(!domain.validateSubmissionDate()) {
						result.rejectValue("version","submissionWindowClosed","submission time window is closed for this special mention notice!");
						return;
					}
					CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG"+"_"+domain.getHouseType().getType().toUpperCase(), "");
					if(csptOfflineSubmissionAllowedFlag!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
							&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
						if(!role.equals("SMIS_TYPIST")){
							if(!SpecialMentionNotice.validateSubmissionTime(domain.getSession(), domain.getSpecialMentionNoticeDate(),domain.getSubmissionDate())) {
								result.rejectValue("version","specialmentionnotice.submissionWindowTimeClosed","submission time window is closed for this special mention notice!");
								return;
							}
						}
					} else {
						if(!SpecialMentionNotice.validateSubmissionTime(domain.getSession(), domain.getSpecialMentionNoticeDate(),domain.getSubmissionDate())) {
							result.rejectValue("version","specialmentionnotice.submissionWindowTimeClosed","submission time window is closed for this specialMentonNotice date motions!");
							return;
						}
					}
				}
				if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					// Empty check for Ministry
					if(domain.getMinistry()==null){
						result.rejectValue("ministry","MinistryEmpty");
					}
					// Empty check for Subdepartment
					if(domain.getSubDepartment()==null){
						result.rejectValue("subDepartment","SubDepartmentEmpty");
					}
				}
			} else if(operation.equals("startworkflow")){
				// Empty check for Ministry
//				if(domain.getMinistry()==null){
//					result.rejectValue("ministry","MinistryEmpty");
//				}
//				// Empty check for SubDepartment
//				if(domain.getSubDepartment()==null){
//					result.rejectValue("subDepartment","SubDepartmentEmpty");
//				}				
			}
		}
    }
	}
	
	@Override
	protected void populateUpdateIfErrors(ModelMap model, SpecialMentionNotice domain,
			HttpServletRequest request) {		
		try {
			UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", domain.getLocale());
			if(userGroupType==null) {
				throw new ELSException("SpecialMentionNoticeController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
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
			/**** Number ****/
			if(domain.getNumber()!=null){
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
			}
			/** populate session dates as possible specialMentionNotice dates **/
			if(selectedSession!=null && selectedSession.getId()!=null) {
				List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
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
				model.addAttribute("formattedCreationDate", FormaterUtil.formatDateToString(domain.getCreationDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, domain.getLocale()));
			}
			//Populate createdby
			model.addAttribute("createdBy",domain.getCreatedBy());
			/**** Referenced Motions Starts ****/
			CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
					findByName(CustomParameter.class, "SMIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
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
					List<Reference> clubEntityReferences = SpecialMentionNoticeController.populateClubbedEntityReferences(domain, domain.getLocale());
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
				if(!internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_SUBMIT)){
					if(recommendationStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_CLUBBING_POST_ADMISSION)
							|| recommendationStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_UNCLUBBING)
							|| recommendationStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
						SpecialMentionNoticeController.
						populateInternalStatus(model,recommendationStatus.getType(),usergroupType,domain.getLocale(),motionType.getType());
					} else {
						SpecialMentionNoticeController.
						populateInternalStatus(model,internalStatus.getType(),usergroupType,domain.getLocale(),motionType.getType());
					}					
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
				//Populate Actors
				if(internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_ADMISSION)
							||internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_REJECTION)
							||internalStatus.getType().equals(ApplicationConstants.SPECIALMENTIONNOTICE_PUTUP_REJECTION)){
					List<Reference> actors=WorkflowConfig.
							findSpecialMentionNoticeActorsVO(domain, internalStatus, userGroup, 1, domain.getLocale());
					model.addAttribute("actors", actors);
//								if(actors!=null && !actors.isEmpty()){
//								String nextActor = actors.get(0).getId();
//								String[] actorArr = nextActor.split("#");
//								domain.setLevel(actorArr[2]);
//								domain.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
//							}
				}
			}else if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("clerk")){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
			}
			
			/** setting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
			CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"SMIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
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
	protected void populateUpdateIfNoErrors(final ModelMap model, final SpecialMentionNotice domain,
			final HttpServletRequest request) throws ELSException, ParseException {
		/**** Status ,Internal Status,Recommendation Status,specialmentionnotice date,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String locale = domain.getLocale();
		String operation=request.getParameter("operation");
		String role = request.getParameter("role");
		UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		if(userGroupType==null) {
			throw new ELSException("SpecialMentionNoticeController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
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
					/**** Status,Internal status and recommendation status is set to submit ****/
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.SPECIALMENTIONNOTICE_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);
				} else {
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.SPECIALMENTIONNOTICE_COMPLETE, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);
				}
			}			
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.SPECIALMENTIONNOTICE_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}		
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/		
		CustomParameter assistantProcessedAllowed = CustomParameter.
				findByName(CustomParameter.class,"SMIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
		if(assistantProcessedAllowed != null){
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", assistantProcessedAllowed.getValue(), ",", locale);
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
			if(isUserGroupAllowed){
				SpecialMentionNotice specialMentionNotice = SpecialMentionNotice.findById(SpecialMentionNotice.class, domain.getId());
				String internalStatusType = specialMentionNotice.getInternalStatus().getType();
				if(internalStatusType.equals(ApplicationConstants.SPECIALMENTIONNOTICE_SUBMIT)
//					&& domain.getMinistry()!=null 
//					&& domain.getSubDepartment()!=null
				) {
					Status ASSISTANT_PROCESSED = Status.
							findByType(ApplicationConstants.SPECIALMENTIONNOTICE_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}
			}
		}
		
		/** setting remarks as remarks for decision if mentioned by allowed usergrouptypes  **/
		CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"SMIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
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
	protected void populateAfterUpdate(final ModelMap model, final SpecialMentionNotice domain,
			final HttpServletRequest request) throws ELSException, ParseException {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		String operation=request.getParameter("operation");
		if(operation != null && !operation.isEmpty()){
			 if(operation.equals("startworkflow")){
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
					SpecialMentionNotice specialMentionNotice=SpecialMentionNotice.findById(SpecialMentionNotice.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								Workflow workflow = specialMentionNotice.findWorkflowFromStatus();								
								
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
								specialMentionNotice.setWorkflowDetailsId(workflowDetails.getId());							
							}
						}
					}
					/**** Workflow Started ****/
					specialMentionNotice.setWorkflowStarted("YES");
					specialMentionNotice.setWorkflowStartedOn(new Date());
					specialMentionNotice.setTaskReceivedOn(new Date());					
					specialMentionNotice.simpleMerge();
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
				} catch(Exception e){
					model.addAttribute("error", e.getMessage());
				}
			}
		}
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
		return "specialmentionnotice/citation";
	}
	
	
	//extra methods
	public static List<Reference> populateClubbedEntityReferences(SpecialMentionNotice domain, String locale) {
		List<Reference> references = new ArrayList<Reference>();
		List<ClubbedEntity> clubbedEntities=SpecialMentionNotice.findClubbedEntitiesByPosition(domain);
		if(clubbedEntities!=null){
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getSpecialMentionNotice().getNumber()));
				reference.setNumber(String.valueOf(ce.getSpecialMentionNotice().getId()));
				references.add(reference);
			}
		}
		return references;
	}
	
}
