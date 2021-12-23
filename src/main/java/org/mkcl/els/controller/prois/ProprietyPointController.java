package org.mkcl.els.controller.prois;

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
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.ProprietyPointDraft;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
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
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("proprietypoint")
public class ProprietyPointController extends GenericController<ProprietyPoint> {
	
	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {
		// Populate locale
		model.addAttribute("moduleLocale", locale);

		/**** Selected Device Type ****/
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Available Device Types ****/
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
				} else {
					Session lastSessionCreated=null;
					/**** House Types ****/
					// Populate House types configured for the current user
					List<HouseType> houseTypes=null;
					HouseType authUserHouseType = null;
					if(houseType==null) {
						try {
							houseTypes = QuestionController.getHouseTypes(currentUser, deviceType, locale);
						} catch (ELSException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						model.addAttribute("houseTypes", houseTypes);
						// Populate default House type						
						if(houseTypes!=null && houseTypes.size()==1) {
							authUserHouseType = houseTypes.get(0);							
						} else {
							try {
								authUserHouseType = QuestionController.getHouseType(currentUser, locale);
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						model.addAttribute("houseType", authUserHouseType.getType());
						houseType = authUserHouseType.getType();
					} else {						
						model.addAttribute("houseType", houseType);
						authUserHouseType = HouseType.findByType(houseType, locale);
					}

					/**** Session Types. ****/
					List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType",ApplicationConstants.ASC, locale);
					/**** Latest Session of a House Type ****/
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
					
					//ProprietyPoint.assignMotionNo(houseType, (Date)model.get("defaultAdjourningDate"), locale);
					session = lastSessionCreated;
				}
				
				/**** Device Types. ****/
				deviceTypes = DeviceType.findDeviceTypesStartingWith("proprietypoint", locale);
				model.addAttribute("deviceTypes", deviceTypes);
				/**** Default Value ****/
				model.addAttribute("deviceType", deviceType.getId());
				/**** Access Control Based on Motion Type ****/
				model.addAttribute("deviceTypeType", deviceType.getType());
				
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
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"PROIS_ALLOWED_USERGROUPTYPES", "");
					if (customParameter != null) {
						List<UserGroupType> configuredUserGroupTypes = 
								QuestionController.delimitedStringToUGTList(customParameter.getValue(), ",", locale);
						
						userGroup = QuestionController.getUserGroup(userGroups, configuredUserGroupTypes, session, locale);
						userGroupType = userGroup.getUserGroupType();
						
						model.addAttribute("usergroup", userGroup.getId());
						model.addAttribute("usergroupType", userGroupType.getType());
					} else {
						model.addAttribute("errorcode","prois_allowed_usergroups_notset");
					}
				} else {
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
				if(session.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					List<Date> sessionDates = session.findAllSessionDatesHavingNoHoliday();
					model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", locale));				
					Date defaultProprietyPointDate = null;
					if(userGroupType.getType().equals(ApplicationConstants.MEMBER)) {
						defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(session, true);
					} else {
						defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(session, false);
					}
					model.addAttribute("defaultProprietyPointDate", FormaterUtil.formatDateToString(defaultProprietyPointDate, ApplicationConstants.SERVER_DATEFORMAT));
				}
				/**** Propriety Point Status Allowed ****/
				CustomParameter allowedStatus = CustomParameter.findByName(CustomParameter.class,
								"PROPRIETYPOINT_GRID_STATUS_ALLOWED_"+ userGroupType.getType().toUpperCase(),"");
				List<Status> status = new ArrayList<Status>();
				if (allowedStatus != null) {
					status = Status.findStatusContainedIn(allowedStatus.getValue(),locale);
				} else {
					CustomParameter defaultAllowedStatus = CustomParameter.findByName(CustomParameter.class,
									"PROPRIETYPOINT_GRID_STATUS_ALLOWED_BY_DEFAULT","");
					if (defaultAllowedStatus != null) {
						status = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
					} else {
						model.addAttribute("errorcode","proprietypoint_status_allowed_by_default_not_set");
					}
				}
				model.addAttribute("status", status);
				/**** Propriety Point Departments Allowed ****/
				Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
				CustomParameter subDepartmentFilterAllowedFor = 
						CustomParameter.findByName(CustomParameter.class, "PROIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
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
					} else if (i.getType().contains("PROIS_CLERK")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().startsWith("PROIS_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					}
				}
			} catch (ELSException e) {
				model.addAttribute("ProprietyPointController", e.getParameter());
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
				 String houseType = request.getParameter("houseType");
				 CustomParameter memberGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"PROIS_MEMBERGRID_ALLOWED_FOR", "");
				 if(memberGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
							 this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(Role.class, memberGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
								this.isObjectExtendingBaseDomainAvailableInList(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){						   
						   newUrlPattern=urlPattern+"?usergroup=member&houseType="+houseType;
						   if(houseType!=null && houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
							   String selectedProprietyPointDate = request.getParameter("proprietyPointDate");
							   if(selectedProprietyPointDate!=null && !selectedProprietyPointDate.isEmpty()) {
								   newUrlPattern=newUrlPattern+"&proprietyPointDate=selected";
							   }
						   }
						   return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("ProprietyPointController.modifyURLPattern/4", 
								"PROIS_MEMBERGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
				 
				 CustomParameter typistGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"PROIS_TYPISTGRID_ALLOWED_FOR", "");
				 if(typistGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
							QuestionController.delimitedStringToRoleList(typistGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
							QuestionController.isRoleExists(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){
						 newUrlPattern=urlPattern+"?usergroup=typist&houseType="+houseType;
						 if(houseType!=null && houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
							String selectedProprietyPointDate = request.getParameter("proprietyPointDate");
							if(selectedProprietyPointDate!=null && !selectedProprietyPointDate.isEmpty()) {
								newUrlPattern=newUrlPattern+"&proprietyPointDate=selected";
							}
						 }
					     return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("ProprietyPointController.modifyURLPattern/4", 
								"PROIS_TYPISTGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
				 
				 CustomParameter assistantGridAllowedFor = 
							CustomParameter.findByName(CustomParameter.class,"PROIS_ASSISTANTGRID_ALLOWED_FOR", "");
				 if(assistantGridAllowedFor != null){
					 List<Role> configuredMemberGridAllowedForRoles = 
						QuestionController.delimitedStringToRoleList(assistantGridAllowedFor.getValue(), ",", locale);
					 boolean isRoleConfiguredForMemberGrid = 
						QuestionController.isRoleExists(configuredMemberGridAllowedForRoles, role);
					 if(isRoleConfiguredForMemberGrid){
						 newUrlPattern=urlPattern+"?usergroup=assistant&houseType="+houseType;
						 if(houseType!=null && houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
							String selectedProprietyPointDate = request.getParameter("proprietyPointDate");
							if(selectedProprietyPointDate!=null && !selectedProprietyPointDate.isEmpty()) {
								newUrlPattern=newUrlPattern+"&proprietyPointDate=selected";
							}
						 }
						 return newUrlPattern;
					 }
				 }else{
					 throw new ELSException("ProprietyPointController.modifyURLPattern/4", 
								"PROIS_ASSISTANTGRID_ALLOWED_FOR key is not set as CustomParameter");
				 }
			 }
			 else{
				 throw new ELSException("ProprietyPointController.modifyURLPattern/4",
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
						CustomParameter.findByName(CustomParameter.class,"PROIS_NEW_OPERATION_ALLOWED_TO","");
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
			final ProprietyPoint domain, 
			final String locale,
			final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		domain.setLocale(locale);
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
			String selectedDeviceType=request.getParameter("deviceType");
			if(selectedDeviceType==null){
				selectedDeviceType=request.getParameter("type");
			}
			DeviceType deviceType=null;
			if(selectedDeviceType!=null){
				if(!selectedDeviceType.isEmpty()){
					deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedDeviceType));
					model.addAttribute("formattedDeviceType", deviceType.getName());
					model.addAttribute("deviceType", deviceType.getId());
					model.addAttribute("selectedDeviceType", deviceType.getType());
				}else{
					logger.error("**** Check request parameter 'deviceType' for no value ****");
					model.addAttribute("errorcode","deviceType_isempty");		
				}
			}else{
				logger.error("**** Check request parameter 'deviceType' for null value ****");
				model.addAttribute("errorcode","deviceType_isnull");
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
				Constituency constituency = Member.findConstituency(member, new Date());
				if(constituency != null){
					model.addAttribute("constituency", constituency.getDisplayName());
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
			/** populate session dates as possible propriety point dates for upperhouse**/
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(selectedSession!=null && selectedSession.getId()!=null) {
					List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
					model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", locale));				
					Date defaultProprietyPointDate = null;
					if(usergroupType.equals(ApplicationConstants.MEMBER)) {
						defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(selectedSession, true);
					} else {
						defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(selectedSession, false);
					}
					model.addAttribute("defaultProprietyPointDate", FormaterUtil.formatDateToString(defaultProprietyPointDate, ApplicationConstants.SERVER_DATEFORMAT));
				}				
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
				findByName(CustomParameter.class, "PROIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.
				findByName(CustomParameter.class, "PROIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles=authUser.getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return editUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return editUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("PROIS_")) {
				return editUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "proprietypoint/error";
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final ProprietyPoint domain,
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
			DeviceType deviceType = domain.getDeviceType();
			model.addAttribute("formattedDeviceType", deviceType.getName());
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
			if(domain.getNumber()!=null){
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
			}	
			/**** Admission Number ****/
			if(domain.getAdmissionNumber()!=null){
				model.addAttribute("formattedAdmissionNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getAdmissionNumber()));
			}			
			/** populate session dates as possible propriety point dates for upperhouse **/
			if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(selectedSession!=null && selectedSession.getId()!=null) {
					List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
					model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));				
				}
				/**** populate propriety point date ****/
				model.addAttribute("selectedProprietyPointDate", FormaterUtil.formatDateToString(domain.getProprietyPointDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
				model.addAttribute("formattedProprietyPointDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getProprietyPointDate(), "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));				
			}
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
			//populate member status name and devicetype
			if(userGroupType!=null && userGroupType.getType().equals("member")){
				Status memberStatus = domain.findMemberStatus();
				if(memberStatus!=null){				
					model.addAttribute("formattedMemberStatus", memberStatus.getName());
				}
			}
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
//					if(domain.getReferencedProprietyPoint()!=null){
//						Reference referencedEntityReference = ProprietyPointController.populateReferencedEntityAsReference(domain, locale);
//						model.addAttribute("referencedProprietyPoint",referencedEntityReference);
//					}
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
			// set End Flag and Level in case of assistant
			if(userGroupType !=null && userGroupType.getType().equals("assistant")){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				//populate PUT UP OPTIONS
				if(!internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_SUBMIT)){
					ProprietyPointController.populateInternalStatus(model,internalStatus.getType(),userGroupType.getType(),locale,deviceType.getType());					
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
				//Populate Actors
				if(internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_ADMISSION)
							||internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_REJECTION)
							||internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_PUTUP_REJECTION)){
					List<Reference> actors=WorkflowConfig.
							findProprietyPointActorsVO(domain, internalStatus, userGroup, 1, locale);
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
				/*if(!internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_SUBMIT)
					&& !internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_SYSTEM_ASSISTANT_PROCESSED)){
					QuestionController.
					populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
				}*/
			}
			/**** remarks for final rejection ****/
			Status rejectionFinalStatus = Status.findByType(ApplicationConstants.PROPRIETYPOINT_FINAL_REJECTION, locale);
			boolean canRemark = false;	
			String errorMessagePossible="";
			try{
				errorMessagePossible = "domain_not_found";
				if (internalStatus.getType().equals(rejectionFinalStatus.getType())) {
					errorMessagePossible = "questiondraft_not_found_for_remark";
					ProprietyPointDraft mDraft = domain.findPreviousDraft();
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
//			UserGroupType userGroupTypeObj = UserGroupType.findByType(userGroupType.getType(), domain.getLocale());
//			CustomParameter remarksForDecisionAllowed = CustomParameter.findByName(CustomParameter.class,"PROIS_REMARKS_FOR_DECISION_ALLOWED_FOR","");
//			if(remarksForDecisionAllowed!=null) {
//				List<UserGroupType> userGroupTypes = 
//						this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", remarksForDecisionAllowed.getValue(), ",", domain.getLocale());
//				Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupTypeObj);
//				if(isUserGroupAllowed){
//					domain.setRemarks(domain.getRemarksAboutDecision());
//				} else {
//					domain.setRemarks("");
//				}
//			} else {
//				domain.setRemarks("");
//			}
		} catch(ELSException e) {
			
		} catch(Exception e) {
			
		}
	}
	
	public static void populateInternalStatus(final ModelMap model, final String type,final String userGroupType,final String locale, final String deviceType) {
		List<Status> internalStatuses=new ArrayList<Status>();
		try{
			CustomParameter specificDeviceStatusUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.toUpperCase()
							+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_"+deviceType.toUpperCase()
							+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses = CustomParameter.
					findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_"+type.toUpperCase()
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
						findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus != null) {
					internalStatuses = Status.
							findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus = CustomParameter.
							findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus != null){
						internalStatuses = Status.
								findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter = CustomParameter.
								findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter != null){
							internalStatuses = Status.
									findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode", "proprietypoint_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus = CustomParameter.
						findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus != null) {
					internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.
							findByName(CustomParameter.class,"PROPRIETYPOINT_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter != null) {
						internalStatuses = Status.
								findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode", "proprietypoint_putup_options_final_notset");
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
	protected void preValidateCreate(final ProprietyPoint domain,
            final BindingResult result, 
            final HttpServletRequest request) {
		/**** populate supporting members before validation ****/
		String role = request.getParameter("role");		
		populateSupportingMembers(domain,role,request);
	}
	
	@Override
	protected void customValidateCreate(final ProprietyPoint domain,
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
		if(domain.getDeviceType()==null){
			result.rejectValue("deviceType","DeviceTypeEmpty");
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
		if(domain.getPointsOfPropriety()==null || domain.getPointsOfPropriety().isEmpty()){
			result.rejectValue("pointsOfPropriety","ProprietyPoint.PointsOfProprietyEmpty");
		}
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PROPRIETYPOINT_OPTIONAL_FIELDS_IN_VALIDATION + "_" + domain.getHouseType().getType().toUpperCase(), "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}
		/**** Number Validation ****/
		if(role.equals("PROIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "PROIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");			
						return;
					}
					//check for duplicate motion
					Boolean flag=ProprietyPoint.isDuplicateNumberExist(domain.getNumber(), domain.getId(), domain.getLocale());
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
						return;
					}
				} else {
					domain.setNumber(null);
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
				if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					// Empty check for Ministry
					if(optionalFields != null && !optionalFields.contains("ministry")){
						if(domain.getMinistry()==null){
							result.rejectValue("ministry","MinistryEmpty");
						}		
					}	
					/*if(domain.getMinistry()==null){
						result.rejectValue("ministry","MinistryEmpty");
					}*/
					// Empty check for Subdepartment
					if(optionalFields != null && !optionalFields.contains("subDepartment")){
						if(domain.getSubDepartment()==null){
							result.rejectValue("subDepartment","SubDepartmentEmpty");
						}		
					}	
					/*if(domain.getSubDepartment()==null){
						result.rejectValue("subDepartment","SubDepartmentEmpty");
					}*/					
				}
				
				//submission date limit validations for lowerhouse only (configurable through custom parameters)
				if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					if(domain.getSession()!=null && domain.getDeviceType()!=null) {
						//submission start date limit validation
						CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
						if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
							String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
							if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
								String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
								for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
									if(dt.trim().equals(domain.getDeviceType().getType().trim())) {
										String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getDeviceType().getType()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
										if(submissionStartLimitDateStr!=null && !submissionStartLimitDateStr.isEmpty()) {
											Date submissionStartLimitDate = FormaterUtil.formatStringToDate(submissionStartLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
											if(submissionStartLimitDate!=null
													&& submissionStartLimitDate.after(new Date())) {
												submissionStartLimitDateStr = FormaterUtil.formatDateToString(submissionStartLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Propriety Point cannot be submitted before " + submissionStartLimitDateStr);
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
									if(dt.trim().equals(domain.getDeviceType().getType().trim())) {
										String submissionEndLimitDateStr = domain.getSession().getParameter(domain.getDeviceType().getType()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
										if(submissionEndLimitDateStr!=null && !submissionEndLimitDateStr.isEmpty()) {
											Date submissionEndLimitDate = FormaterUtil.formatStringToDate(submissionEndLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
											if(submissionEndLimitDate!=null
													&& submissionEndLimitDate.before(new Date())) {
												submissionEndLimitDateStr = FormaterUtil.formatDateToString(submissionEndLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Propriety Point cannot be submitted after " + submissionEndLimitDateStr);
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
				} 				
				//submission window validations for upperhouse only
				else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					CustomParameter submissionWindowValidationSkippedCP = CustomParameter.findByName(CustomParameter.class, "PROPRIETYPOINT_SUBMISSION_WINDOW_VALIDATIONS_SKIPPED"+"_"+domain.getHouseType().getType().toUpperCase(), "");
					if(submissionWindowValidationSkippedCP==null || submissionWindowValidationSkippedCP.getValue()==null
							|| !submissionWindowValidationSkippedCP.getValue().equals("TRUE")) {
						if(!domain.validateSubmissionDate()) {
							result.rejectValue("version","submissionWindowClosed","submission time window is closed for this proprietypoint date!");
							return;
						}
						CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getDeviceType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG"+"_"+domain.getHouseType().getType().toUpperCase(), "");
						if(csptOfflineSubmissionAllowedFlag!=null 
								&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
								&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
							if(!role.equals("PROIS_TYPIST")){
								if(!ProprietyPoint.validateSubmissionTime(domain.getSession(), domain.getProprietyPointDate(),new Date())) {
									result.rejectValue("version","proprietypoint.submissionWindowTimeClosed","submission time window is closed for this proprietypoint date!");
									return;
								}
							}
						} else {
							if(!ProprietyPoint.validateSubmissionTime(domain.getSession(), domain.getProprietyPointDate(),new Date())) {
								result.rejectValue("version","proprietypoint.submissionWindowTimeClosed","submission time window is closed for this proprietypoint date!");
								return;
							}
						}					
					}
				}
			}
		}
    }
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, ProprietyPoint domain,
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
			DeviceType deviceType = domain.getDeviceType();
			model.addAttribute("formattedDeviceType", deviceType.getName());
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
			/** populate session dates as possible propriety point dates for upperhouse**/
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(selectedSession!=null && selectedSession.getId()!=null) {
					List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
					model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));				
					Date defaultProprietyPointDate = null;
					if(usergroupType.equals(ApplicationConstants.MEMBER)) {
						defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(selectedSession, true);
					} else {
						defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(selectedSession, false);
					}
					model.addAttribute("defaultProprietyPointDate", FormaterUtil.formatDateToString(defaultProprietyPointDate, ApplicationConstants.SERVER_DATEFORMAT));
				}
				/**** populate propriety point date ****/
				model.addAttribute("selectedProprietyPointDate", FormaterUtil.formatDateToString(domain.getProprietyPointDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
				model.addAttribute("formattedProprietyPointDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getProprietyPointDate(), "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));		
			}
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
	protected void populateCreateIfNoErrors(final ModelMap model, final ProprietyPoint domain,
			final HttpServletRequest request) throws ELSException {
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String locale = domain.getLocale();
		String operation=request.getParameter("operation");
		String role = request.getParameter("role");
		UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		if(userGroupType==null) {
			throw new ELSException("ProprietyPointController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
		}
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&& domain.getDeviceType()!=null && domain.getPrimaryMember()!=null
				&& domain.getPointsOfPropriety()!=null && !domain.getPointsOfPropriety().isEmpty()){
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
											"PROIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
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
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.PROPRIETYPOINT_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);							 
				}
			} else {
				Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.PROPRIETYPOINT_COMPLETE, domain.getLocale());
				domain.setStatus(newstatus);
				domain.setInternalStatus(newstatus);
				domain.setRecommendationStatus(newstatus);
			}
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.PROPRIETYPOINT_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedAs(userGroupType.getName());
	}
	
	@Override
	protected void populateAfterCreate(final ModelMap model, final ProprietyPoint domain,
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
				properties.put("pv_deviceTypeId",domain.getDeviceType().getType());
				ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
				List<Task> tasks=processService.getCurrentTasks(processInstance);
				List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain, tasks, ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW, "0");
				/**** Supporting members status changed to pending ****/
				ProprietyPoint proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,domain.getId());
				List<SupportingMember> supportingMembers=proprietyPoint.getSupportingMembers();
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
	protected void preValidateUpdate(final ProprietyPoint domain,
            final BindingResult result, 
            final HttpServletRequest request) {
		/**** populate supporting members before validation ****/
		String role = request.getParameter("role");	
		populateSupportingMembers(domain,role,request);
	}
	
	@Override
	protected void customValidateUpdate(final ProprietyPoint domain,
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
		if(domain.getDeviceType()==null){
			result.rejectValue("deviceType","DeviceTypeEmpty");
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
		if(domain.getPointsOfPropriety()==null || domain.getPointsOfPropriety().isEmpty()){
			result.rejectValue("pointsOfPropriety","ProprietyPoint.PointsOfProprietyEmpty");
		}
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PROPRIETYPOINT_OPTIONAL_FIELDS_IN_VALIDATION + "_" + domain.getHouseType().getType().toUpperCase(), "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}
		/**** Number Validation ****/
		if(role.equals("PROIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "PROIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");			
						return;
					}
					//check for duplicate motion
					Boolean flag=ProprietyPoint.isDuplicateNumberExist(domain.getNumber(), domain.getId(), domain.getLocale());
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
				if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					// Empty check for Ministry
					if(optionalFields != null && !optionalFields.contains("ministry")){
						if(domain.getMinistry()==null){
							result.rejectValue("ministry","MinistryEmpty");
						}		
					}	
					/*if(domain.getMinistry()==null){
						result.rejectValue("ministry","MinistryEmpty");
					}*/
					// Empty check for Subdepartment
					if(optionalFields != null && !optionalFields.contains("subDepartment")){
						if(domain.getSubDepartment()==null){
							result.rejectValue("subDepartment","SubDepartmentEmpty");
						}		
					}	
					/*if(domain.getSubDepartment()==null){
						result.rejectValue("subDepartment","SubDepartmentEmpty");
					}*/					
				}
				
				//submission date limit validations for lowerhouse only (configurable through custom parameters)
				if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					if(domain.getSession()!=null && domain.getDeviceType()!=null) {
						//submission start date limit validation
						CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
						if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
							String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
							if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
								String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
								for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
									if(dt.trim().equals(domain.getDeviceType().getType().trim())) {
										String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getDeviceType().getType()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
										if(submissionStartLimitDateStr!=null && !submissionStartLimitDateStr.isEmpty()) {
											Date submissionStartLimitDate = FormaterUtil.formatStringToDate(submissionStartLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
											if(submissionStartLimitDate!=null
													&& submissionStartLimitDate.after(new Date())) {
												submissionStartLimitDateStr = FormaterUtil.formatDateToString(submissionStartLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Propriety Point cannot be submitted before " + submissionStartLimitDateStr);
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
									if(dt.trim().equals(domain.getDeviceType().getType().trim())) {
										String submissionEndLimitDateStr = domain.getSession().getParameter(domain.getDeviceType().getType()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
										if(submissionEndLimitDateStr!=null && !submissionEndLimitDateStr.isEmpty()) {
											Date submissionEndLimitDate = FormaterUtil.formatStringToDate(submissionEndLimitDateStr, ApplicationConstants.DB_DATETIME_FORMAT);
											if(submissionEndLimitDate!=null
													&& submissionEndLimitDate.before(new Date())) {
												submissionEndLimitDateStr = FormaterUtil.formatDateToString(submissionEndLimitDate, ApplicationConstants.SERVER_DATETIMEFORMAT);
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Propriety Point cannot be submitted after " + submissionEndLimitDateStr);
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
				} 				
				//submission window validations for upperhouse only
				else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					CustomParameter submissionWindowValidationSkippedCP = CustomParameter.findByName(CustomParameter.class, "PROPRIETYPOINT_SUBMISSION_WINDOW_VALIDATIONS_SKIPPED"+"_"+domain.getHouseType().getType().toUpperCase(), "");
					if(submissionWindowValidationSkippedCP==null || submissionWindowValidationSkippedCP.getValue()==null
							|| !submissionWindowValidationSkippedCP.getValue().equals("TRUE")) {
						if(!domain.validateSubmissionDate()) {
							result.rejectValue("version","submissionWindowClosed","submission time window is closed for this proprietypoint date!");
							return;
						}
						CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getDeviceType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG"+"_"+domain.getHouseType().getType().toUpperCase(), "");
						if(csptOfflineSubmissionAllowedFlag!=null 
								&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
								&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
							if(!role.equals("PROIS_TYPIST")){
								if(!ProprietyPoint.validateSubmissionTime(domain.getSession(), domain.getProprietyPointDate(),new Date())) {
									result.rejectValue("version","proprietypoint.submissionWindowTimeClosed","submission time window is closed for this proprietypoint date!");
									return;
								}
							}
						} else {
							if(!ProprietyPoint.validateSubmissionTime(domain.getSession(), domain.getProprietyPointDate(),new Date())) {
								result.rejectValue("version","proprietypoint.submissionWindowTimeClosed","submission time window is closed for this proprietypoint date!");
								return;
							}
						}					
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
	
	@Override
	protected void populateUpdateIfErrors(ModelMap model, ProprietyPoint domain,
			HttpServletRequest request) {		
		try {
			UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", domain.getLocale());
			if(userGroupType==null) {
				throw new ELSException("ProprietyPointController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
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
			DeviceType deviceType = domain.getDeviceType();
			model.addAttribute("formattedDeviceType", deviceType.getName());
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
			/** populate session dates as possible propriety point dates for upperhouse **/
			if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(selectedSession!=null && selectedSession.getId()!=null) {
					List<Date> sessionDates = selectedSession.findAllSessionDatesHavingNoHoliday();
					model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));				
				}
				/**** populate propriety point date ****/
				model.addAttribute("selectedProprietyPointDate", FormaterUtil.formatDateToString(domain.getProprietyPointDate(), ApplicationConstants.SERVER_DATEFORMAT, "en_US"));
				model.addAttribute("formattedProprietyPointDate", FormaterUtil.formatDateToStringUsingCustomParameterFormat(domain.getProprietyPointDate(), "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", domain.getLocale()));				
			}
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
			/**** Referenced Propriety Points Starts ****/
			CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
					findByName(CustomParameter.class, "PROIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
			if(clubbedReferencedEntitiesVisibleUserGroups != null){
				List<UserGroupType> userGroupTypes = 
						this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(UserGroupType.class, clubbedReferencedEntitiesVisibleUserGroups.getValue(), ",", domain.getLocale());
				Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
				if(isUserGroupAllowed){
					//populate parent
					if(domain.getParent()!=null){
						model.addAttribute("formattedParentNumber",FormaterUtil.formatNumberNoGrouping(domain.getParent().getNumber(), domain.getLocale()));
						model.addAttribute("parent",domain.getParent().getId());
					}
					//populate referenced entity
//					if(domain.getReferencedProprietyPoint()!=null){
//						Reference referencedEntityReference = ProprietyPointController.populateReferencedEntityAsReference(domain, locale);
//						model.addAttribute("referencedMotion",referencedEntityReference);
//					}
					// Populate clubbed entities
					List<Reference> clubEntityReferences = ProprietyPointController.populateClubbedEntityReferences(domain, domain.getLocale());
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
			/**** Start workflow related things ****/
			// set End Flag and Level in case of assistant
			if(usergroupType !=null && !(usergroupType.isEmpty()) && usergroupType.equals("assistant")){
				if(domain.getWorkflowStarted()==null || domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				//populate PUT UP OPTIONS
				if(!internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_SUBMIT)){
					ProprietyPointController.populateInternalStatus(model,internalStatus.getType(),usergroupType,domain.getLocale(),deviceType.getType());			
					if(domain.getEndFlag()==null || domain.getEndFlag().isEmpty()){
						domain.setEndFlag("continue");
					} 
					if(domain.getLevel()==null || domain.getLevel().isEmpty()){
						domain.setLevel("1");
					}
				}
				//Populate Actors
				if(internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_ADMISSION)
							||internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_REJECTION)
							||internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_PUTUP_REJECTION)){
					List<Reference> actors=WorkflowConfig.
							findProprietyPointActorsVO(domain, internalStatus, userGroup, 1, domain.getLocale());
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
				/*if(!internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_SUBMIT)
					&& !internalStatus.getType().equals(ApplicationConstants.PROPRIETYPOINT_SYSTEM_ASSISTANT_PROCESSED)){
					QuestionController.
					populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
				}*/
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
	protected void populateUpdateIfNoErrors(final ModelMap model, final ProprietyPoint domain,
			final HttpServletRequest request) throws ELSException, ParseException {
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String locale = domain.getLocale();
		String operation=request.getParameter("operation");
		String role = request.getParameter("role");
		UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		if(userGroupType==null) {
			throw new ELSException("ProprietyPointController.populateCreateIfNoErrors/3", "request parameter 'usergroupType' not set");
		}
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&& domain.getDeviceType()!=null && domain.getPrimaryMember()!=null
				&& domain.getPointsOfPropriety()!=null && !domain.getPointsOfPropriety().isEmpty()){
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
											"PROIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
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
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.PROPRIETYPOINT_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);
				} else {
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.PROPRIETYPOINT_COMPLETE, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);
				}
			}			
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.PROPRIETYPOINT_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}		
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/		
		CustomParameter assistantProcessedAllowed = CustomParameter.
				findByName(CustomParameter.class,"AMOIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
		if(assistantProcessedAllowed != null){
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", assistantProcessedAllowed.getValue(), ",", locale);
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
			if(isUserGroupAllowed){
				ProprietyPoint proprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, domain.getId());
				String internalStatusType = proprietyPoint.getInternalStatus().getType();
				if(internalStatusType.equals(ApplicationConstants.PROPRIETYPOINT_SUBMIT)
//					&& domain.getMinistry()!=null 
//					&& domain.getSubDepartment()!=null
				) {
					Status ASSISTANT_PROCESSED = Status.
							findByType(ApplicationConstants.PROPRIETYPOINT_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}
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
	protected void populateAfterUpdate(final ModelMap model, final ProprietyPoint domain,
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
				properties.put("pv_deviceTypeId",domain.getDeviceType().getType());
				ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
				List<Task> tasks=processService.getCurrentTasks(processInstance);
				List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain, tasks, ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW, "0");
				/**** Supporting members status changed to pending ****/
				ProprietyPoint proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,domain.getId());
				List<SupportingMember> supportingMembers=proprietyPoint.getSupportingMembers();
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
					properties.put("pv_deviceTypeId",String.valueOf(domain.getDeviceType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Stale State Exception ****/
					ProprietyPoint proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								Workflow workflow = proprietyPoint.findWorkflowFromStatus();								
								
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
								proprietyPoint.setWorkflowDetailsId(workflowDetails.getId());							
							}
						}
					}
					/**** Workflow Started ****/
					proprietyPoint.setWorkflowStarted("YES");
					proprietyPoint.setWorkflowStartedOn(new Date());
					proprietyPoint.setTaskReceivedOn(new Date());					
					proprietyPoint.simpleMerge();
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
	@RequestMapping(value="/status/{proprietypoint}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("proprietypoint") final String proprietypoint){
		ProprietyPoint proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,Long.parseLong(proprietypoint));
		List<SupportingMember> supportingMembers=proprietyPoint.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "proprietypoint/supportingmember";
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
		return "proprietypoint/citation";
	}
	
	@RequestMapping(value="/revisions/{proprietyPointId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("proprietyPointId")  final Long proprietyPointId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=ProprietyPoint.getRevisions(proprietyPointId,locale.toString());
		ProprietyPoint proprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, proprietyPointId);
		if(proprietyPoint != null){
			if(proprietyPoint.getDeviceType() != null){
				if(proprietyPoint.getDeviceType().getType() != null){
					model.addAttribute("selectedDeviceType", proprietyPoint.getDeviceType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);
		return "proprietypoint/revisions";
	}
	
	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "proprietypoint/contacts";
	}
	
	/**** BULK SUBMISSION (MEMBER) ****/
	@RequestMapping(value="/bulksubmission", method=RequestMethod.GET)
	public String getBulkSubmissionView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
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
				List<ProprietyPoint> proprietyPoints = new ArrayList<ProprietyPoint>();
				if(primaryMember != null){
					proprietyPoints = ProprietyPoint.findAllReadyForSubmissionByMember(session, primaryMember,deviceType, itemsCount, strLocale);	
				}	
				model.addAttribute("proprietyPoints", proprietyPoints);
				model.addAttribute("size", proprietyPoints.size());

				String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupType", userGroupType);
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "proprietypoint/bulksubmission";
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

			List<ProprietyPoint> proprietyPoints = new ArrayList<ProprietyPoint>();
			for(String i : items) {
				Long id = Long.parseLong(i);
				ProprietyPoint proprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, id);

				/**** Update Supporting Member ****/
				List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
				Status timeoutStatus = Status.findByType(
						ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
				if(proprietyPoint.getSupportingMembers() != null
						&& ! proprietyPoint.getSupportingMembers().isEmpty()) {
					for(SupportingMember sm : proprietyPoint.getSupportingMembers()) {
						if(sm.getDecisionStatus().getType().equals(
								ApplicationConstants.SUPPORTING_MEMBER_NOTSEND) ||
								sm.getDecisionStatus().getType().equals(
										ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
							/**** Update Supporting Member ****/
							sm.setDecisionStatus(timeoutStatus);
							sm.setApprovalDate(new Date());	
							sm.setApprovedText(proprietyPoint.getPointsOfPropriety());
							sm.setApprovedSubject(proprietyPoint.getSubject());
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

					proprietyPoint.setSupportingMembers(supportingMembers);
				}
				
				/**** Update Status(es) ****/
				Status newstatus = Status.findByFieldName(Status.class, "type", 
						ApplicationConstants.PROPRIETYPOINT_SUBMIT, proprietyPoint.getLocale());
				proprietyPoint.setStatus(newstatus);
				proprietyPoint.setInternalStatus(newstatus);
				proprietyPoint.setRecommendationStatus(newstatus);

				/**** Edited On, Edited By and Edited As is set ****/
				proprietyPoint.setSubmissionDate(new Date());
				proprietyPoint.setEditedOn(new Date());
				proprietyPoint.setEditedBy(this.getCurrentUser().getActualUsername());

				String strUserGroupType = request.getParameter("usergroupType");
				if(strUserGroupType != null) {
					UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class,
							"type", strUserGroupType, proprietyPoint.getLocale());
					proprietyPoint.setEditedAs(userGroupType.getName());
				}

				/**** Bulk Submitted ****/
				proprietyPoint.setBulkSubmitted(true);

				/**** Update the Propriety Point object ****/
				proprietyPoint = proprietyPoint.merge();
				proprietyPoints.add(proprietyPoint);
			}

			model.addAttribute("proprietyPoints", proprietyPoints);
		}

		return "proprietypoint/bulksubmissionack";
	}
	
	private void populateSupportingMembers(final ProprietyPoint domain,final String role,final HttpServletRequest request){
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
		List<SupportingMember> members=new ArrayList<SupportingMember>();
		if(domain.getId()!=null){
			ProprietyPoint proprietyPoint=ProprietyPoint.findById(ProprietyPoint.class,domain.getId());
			members=proprietyPoint.getSupportingMembers();
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
										"PROIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
						if(supportingMemberAutoApprovalAllowedTo != null) {
							if(supportingMemberAutoApprovalAllowedTo.getValue().contains(role)) {
								Status APPROVED = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_APPROVED, domain.getLocale());
								supportingMember.setDecisionStatus(APPROVED);								
								supportingMember.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
								supportingMember.setApprovalDate(new Date());
								supportingMember.setApprovedSubject(domain.getSubject());
								supportingMember.setApprovedText(domain.getPointsOfPropriety());							
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
	
	public static List<Reference> populateClubbedEntityReferences(ProprietyPoint domain, String locale) {
		List<Reference> references = new ArrayList<Reference>();
		List<ClubbedEntity> clubbedEntities=ProprietyPoint.findClubbedEntitiesByPosition(domain);
		if(clubbedEntities!=null){
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getProprietyPoint().getNumber()));
				reference.setNumber(String.valueOf(ce.getProprietyPoint().getId()));
				references.add(reference);
			}
		}
		return references;
	}	

}
