package org.mkcl.els.controller.mois;


import java.math.BigDecimal;
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
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.AppropriationBillMotion;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.AppropriationBillMotion;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.ReferenceUnit;
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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("appropriationbillmotion")
public class AppropriationBillMotionController extends GenericController<AppropriationBillMotion>{

	@Autowired
	private IProcessService processService;

	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {
		
		model.addAttribute("moduleLocale", locale.toString());
		
		/**** Selected Motion Type ****/
		DeviceType deviceType = null;
		try {
			deviceType = AppropriationBillMotionController.getDeviceType(request, locale);
		} catch (ELSException e) {
			model.addAttribute("AppropriationBillMotionController", e.getParameter());
		}
		if(deviceType!=null){
			/**** Available Motion Types ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			try {
				deviceTypes = DeviceType.findDeviceTypesStartingWith(ApplicationConstants.DEVICE_APPROPRIATIONBILLMOTIONS, locale);

				model.addAttribute("motionTypes", deviceTypes);
				/**** Default Value ****/
				model.addAttribute("motionType", deviceType.getId());
				/**** Access Control Based on Motion Type ****/
				model.addAttribute("motionTypeType", deviceType.getType());

				/**** House Types ****/
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
				} else {
					try {
						authUserHouseType = QuestionController.getHouseType(currentUser, locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
				String houseType = authUserHouseType.getType();
				model.addAttribute("houseType", houseType);

				/**** Session Types. ****/
				List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType",ApplicationConstants.ASC, locale);
				/**** Latest Session of a House Type ****/
				Session lastSessionCreated;

				lastSessionCreated = Session.findLatestSession(authUserHouseType);

				/***
				 * Session Year and Session Type.Default is the type and year of
				 * last created session in a particular housetype
				 ****/
				Integer year = new GregorianCalendar().get(Calendar.YEAR);
				if (lastSessionCreated.getId() != null) {
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

				// Populate User group & User Group type
				/**
				 * Rules:
				 * a. Any user can have only one user group per device type.
				 * b. Any user can have multiple user groups limited to one user group per device type.
				 * c. Custom parameter "ABMOIS_ALLOWED_USERGROUPTYPES" control which user groups can access ABMOIS.
				 * d. Custom parameter "ABMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR" controls which user group can see 
				 * Sub department filter.
				 * e. Custom parameter "APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_USERGROUPTYPE" controls which status will 
				 * be seen in status filter
				 * f. Custom parameter "APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT" controls which status 
				 * will be seen by default if above filter is not set.
				 */
				UserGroup userGroup = null;
				UserGroupType userGroupType = null;
				List<UserGroup> userGroups = currentUser.getUserGroups();
				if(userGroups != null && ! userGroups.isEmpty()) {
					CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "ABMOIS_ALLOWED_USERGROUPTYPES", "");
					if(cp != null) {
						List<UserGroupType> configuredUserGroupTypes = 
								AppropriationBillMotionController.delimitedStringToUGTList(cp.getValue(), ",", locale);
						
						userGroup = AppropriationBillMotionController.getUserGroup(userGroups, configuredUserGroupTypes, lastSessionCreated, deviceType, locale);
						userGroupType = userGroup.getUserGroupType();
						
						model.addAttribute("usergroup", userGroup.getId());
						model.addAttribute("usergroupType", userGroupType.getType());
					}
					else {
						throw new ELSException("AppropriationBillMotionController.populateModule/4", 
								"ABMOIS_ALLOWED_USERGROUPTYPES key is not set as CustomParameter");
					}
				}
				if(userGroup == null || userGroupType == null) {
//					throw new ELSException("AppropriationBillMotionController.populateModule/4", 
//							"User group or User group type is not set for Username: " + currentUser.getUsername());
					
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
				
				// Populate Sub Departments configured for this User's user group type
				Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
				CustomParameter subDepartmentFilterAllowedFor = 
						CustomParameter.findByName(CustomParameter.class, "ABMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
				if(subDepartmentFilterAllowedFor != null) {
					List<UserGroupType> ugtConfiguredForSubdepartments = 
							AppropriationBillMotionController.delimitedStringToUGTList(
									subDepartmentFilterAllowedFor.getValue(), ",", locale);
					boolean isUGTConfiguredForSubdepartments = 
							AppropriationBillMotionController.isUserGroupTypeExists(ugtConfiguredForSubdepartments, userGroupType);
					if(isUGTConfiguredForSubdepartments) {
						String subDepartmentParam = parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale);
						if(subDepartmentParam != null && ! subDepartmentParam.equals("")) {
							List<SubDepartment> subDepartments =
									AppropriationBillMotionController.getSubDepartments(subDepartmentParam, "##", locale);
							model.addAttribute("subDepartments", subDepartments);
						}
						else {
							throw new ELSException("AppropriationBillMotionController.populateModule/4", 
									"SUBDEPARTMENT parameter is not set for Username: " + currentUser.getUsername());
						}
					}
				}
				else {
					throw new ELSException("AppropriationBillMotionController.populateModule/4", 
							"ABMOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR key is not set as CustomParameter");
				}
				
				// Populate Statuses configured for this User's user group type
				/**
				 * Rules:
				 * Search for the Custom parameters in following order in order to get the statuses configured
				 * for this User's user group type
				 * a. CustomParameter "APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_" + deviceTypeType + userGroupTypeType
				 * b. CustomParameter "APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT" + deviceTypeType
				 * c. CustomParameter "APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT"
				 */
				String strAllowedStatus = null;
				CustomParameter allowedStatusParam = 
						CustomParameter.findByName(CustomParameter.class, 
								"APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_" + deviceType.getType().toUpperCase() + "_" + 
										userGroupType.getType().toUpperCase(), "");
				if(allowedStatusParam != null) {
					strAllowedStatus = allowedStatusParam.getValue();
				}
				else {
					CustomParameter defaultAllowedStatusParamDeviceType =
							CustomParameter.findByName(CustomParameter.class,
									"APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT" + deviceType.getType().toUpperCase(), "");
					if(defaultAllowedStatusParamDeviceType != null) {
						strAllowedStatus = defaultAllowedStatusParamDeviceType.getValue();
					}
					else {
						CustomParameter defaultAllowedStatusParam =
								CustomParameter.findByName(CustomParameter.class,
										"APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
						if(defaultAllowedStatusParam != null) {
							strAllowedStatus = defaultAllowedStatusParam.getValue();
						}
						else {							
							throw new ELSException("AppropriationBillMotionController.populateModule/4", 
									"APPROPRIATIONBILLMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT_ + deviceTypeType " +
									"key is not set as CustomParameter");
						}
					}
				}
				
				if(strAllowedStatus != null) {
					List<Status> statuses = Status.findStatusContainedIn(strAllowedStatus, locale);
					model.addAttribute("status", statuses);
				}
				
				// Populate Roles
				/**
				 * Rules:
				 * a. ABMOIS roles starts with ABMOIS_, MEMBER_
				 * b. Any user will have single role per device type
				 * c. Any user can have multiple roles limited to one role per device type
				 */
				Set<Role> roles = this.getCurrentUser().getRoles();
				for (Role i : roles) {
					if (i.getType().startsWith("MEMBER_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().contains("ABMOIS_CLERK")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().startsWith("ABMOIS_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					}
				}
				/**** File Options(Obtain Dynamically) ****/
				if (userGroupType != null
						&& userGroupType.getType().equals("assistant")) {
					int highestFileNo = 0; //AppropriationBillMotion.findHighestFileNo(lastSessionCreated, deviceType, locale);
					model.addAttribute("highestFileNo", highestFileNo);
				}
			} catch (ELSException e) {
				model.addAttribute("AppropriationBillMotionController", e.getParameter());
			}
		}else{
			model.addAttribute("errorcode","workunderprogress");
		}		

	}

	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {		
		
		 /**** Controlling Grids Starts ****/
		String role = request.getParameter("role");
		String houseType = request.getParameter("houseType");
		String newUrlPattern = urlPattern;
		CustomParameter assistantGridAllowedFor = CustomParameter.findByName(CustomParameter.class, "ABMOIS_ASSISTANTGRID_ALLOWED_FOR","");
		CustomParameter memberGridAllowedFor = CustomParameter.findByName(CustomParameter.class,"ABMOIS_MEMBERGRID_ALLOWED_FOR","");
		CustomParameter typistGridAllowedFor = CustomParameter.findByName(CustomParameter.class,"ABMOIS_TYPISTGRID_ALLOWED_FOR","");
		if(memberGridAllowedFor != null
				&& role != null && !role.isEmpty()
				&& houseType != null && !houseType.isEmpty()
				&& memberGridAllowedFor.getValue().contains(role)){
			newUrlPattern = urlPattern + "?usergroup=member&houseType=" + houseType;
		}else if(typistGridAllowedFor != null && role != null && !role.isEmpty() 
				&& houseType != null && !houseType.isEmpty()
				&& typistGridAllowedFor.getValue().contains(role)){
			newUrlPattern = urlPattern + "?usergroup=typist&houseType=" + houseType;
		}else if(assistantGridAllowedFor != null && role != null && !role.isEmpty()
				&& houseType != null && !houseType.isEmpty()
				&& assistantGridAllowedFor.getValue().contains(role)){
			newUrlPattern = urlPattern + "?usergroup=assistant&houseType=" + houseType;	
		}
		/**** Controlling Grids Ends ****/
		return newUrlPattern;		
	}

	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		
		/**** New Operations Allowed For Starts ****/
		String role = request.getParameter("role");	
		CustomParameter newOperationAllowedTo = CustomParameter.findByName(CustomParameter.class, "ABMOIS_NEW_OPERATION_ALLOWED_TO", "");
		if(newOperationAllowedTo != null && role != null && !role.isEmpty() && newOperationAllowedTo.getValue().contains(role)){
			return servletPath;			
		}		
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
		/**** New Operations Allowed For Starts ****/ 		
	}

	@Override
	protected void populateNew(final ModelMap model, AppropriationBillMotion domain, final String locale,
			final HttpServletRequest request) {

		/**** Locale ****/
		domain.setLocale(locale);

		/**** Subject and Details ****/
		String mainTitle = request.getParameter("mainTitle");
		if(mainTitle != null){
			domain.setMainTitle(mainTitle);
		}
		String noticeContent = request.getParameter("noticeContent");
		if(noticeContent != null){
			domain.setNoticeContent(noticeContent);
		}	

		/**** House Type ****/
		String selectedHouseType = request.getParameter("houseType");
		HouseType houseType = null;
		if(selectedHouseType != null){
			if(!selectedHouseType.isEmpty()){
				/*In case of a validation exception selectedHouseType is id ****/
				try {
					Long houseTypeId = Long.parseLong(selectedHouseType);
					houseType = HouseType.findById(HouseType.class,houseTypeId);
				} catch (NumberFormatException e) {
					houseType = HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
				}
				model.addAttribute("formattedHouseType",houseType.getName());
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
		String selectedYear = request.getParameter("sessionYear");
		Integer sessionYear = 0;
		if(selectedYear != null){
			if(!selectedYear.isEmpty()){
				sessionYear = Integer.parseInt(selectedYear);
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
		String selectedSessionType = request.getParameter("sessionType");
		SessionType sessionType = null;
		if (selectedSessionType != null) {
			if (!selectedSessionType.isEmpty()) {
				sessionType = SessionType.findById(SessionType.class,
						Long.parseLong(selectedSessionType));
				model.addAttribute("formattedSessionType", sessionType.getSessionType());
				model.addAttribute("sessionType", sessionType.getId());
			} else {
				logger.error("**** Check request parameter 'sessionType' for no value ****");
				model.addAttribute("errorcode", "sessionType_isempty");
			}
		} else {
			logger.error("**** Check request parameter 'sessionType' for null value ****");
			model.addAttribute("errorcode", "sessionType_isnull");
		}

		/**** AppropriationBillMotion Type ****/
		String selectedMotionType = request.getParameter("appropriationBillMotionType");
		if (selectedMotionType == null) {
			selectedMotionType = request.getParameter("motionType");
		}
		DeviceType motionType = null;
		if (selectedMotionType != null) {
			if (!selectedMotionType.isEmpty()) {
				motionType = DeviceType.findById(DeviceType.class, Long.parseLong(selectedMotionType));
				model.addAttribute("formattedMotionType", motionType.getName());
				model.addAttribute("motionType", motionType.getId());
				model.addAttribute("selectedMotionType", motionType.getType());
			} else {
				logger.error("**** Check request parameter 'motionType' for no value ****");
				model.addAttribute("errorcode", "motionType_isempty");
			}
		} else {
			logger.error("**** Check request parameter 'motionType' for null value ****");
			model.addAttribute("errorcode", "motionType_isnull");
		}		
		/**** role ****/
		String role = request.getParameter("role");
		if (role != null) {
			model.addAttribute("role", role);
		} else {
			role = (String) request.getSession().getAttribute("role");
			model.addAttribute("role", role);
			request.getSession().removeAttribute("role");
		}
		
		/**** usergroup and usergroupType ****/
		String usergroupType = request.getParameter("usergroupType");
		if (usergroupType != null) {
			model.addAttribute("usergroupType", usergroupType);
		} else {
			usergroupType = (String) request.getSession().getAttribute(
					"usergroupType");
			model.addAttribute("usergroupType", usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		String usergroup = request.getParameter("usergroup");
		if (usergroup != null) {
			model.addAttribute("usergroup", usergroup);
		} else {
			usergroup = (String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup", usergroup);
			request.getSession().removeAttribute("usergroup");
		}				
		
		/**** Session,Primary Member,Supporting Members,Constituency,Ministries,Sub-Departments****/
		Session selectedSession = null;
		String memberNames = null;
		String primaryMemberName = null;
		if(houseType != null && selectedYear != null && sessionType != null){
			try {
				selectedSession = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				if(selectedSession != null){
					
					/**** Session ****/
					model.addAttribute("session",selectedSession.getId());
					if(role.startsWith("MEMBER")){
						/**** Primary Member ****/
						Member member  =Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale);
						if(member != null){
							model.addAttribute("primaryMember",member.getId());
							primaryMemberName = member.getFullname();
							memberNames = primaryMemberName;
							model.addAttribute("formattedPrimaryMember",primaryMemberName);
						}else{
							logger.error("**** Authenticated user is not a member ****");
							model.addAttribute("errorcode","member_isnull");
						}
						
						/**** Supporting Members ****/
						List<SupportingMember> selectedSupportingMembers = domain.getSupportingMembers();
						List<Member> supportingMembers = new ArrayList<Member>();
						if (selectedSupportingMembers != null) {
							if (!selectedSupportingMembers.isEmpty()) {
								StringBuffer bufferFirstNamesFirst = new StringBuffer();
								for (SupportingMember i : selectedSupportingMembers) {
									Member m = i.getMember();
									bufferFirstNamesFirst.append(m.getFullname() + ",");
									supportingMembers.add(m);
								}
								bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length() - 1);
								model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
								model.addAttribute("supportingMembers", supportingMembers);
								memberNames = primaryMemberName + "," + bufferFirstNamesFirst.toString();
								model.addAttribute("memberNames", memberNames);
							} else {
								model.addAttribute("memberNames", memberNames);
							}
						} else {
							model.addAttribute("memberNames", memberNames);
						}
						
						/**** Constituency ****/
						Long houseId = selectedSession.getHouse().getId();
						MasterVO constituency = null;
						if (houseType.getType().equals("lowerhouse")) {
							if (member != null) {
								constituency = Member.findConstituencyByAssemblyId(member.getId(), houseId);
								model.addAttribute("constituency", constituency.getName());
							}
						} else if (houseType.getType().equals("upperhouse")) {
							Date currentDate = new Date();
							String date = FormaterUtil .getDateFormatter("en_US").format(currentDate);
							if (member != null) {
								constituency = Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
								model.addAttribute("constituency", constituency.getName());
							}
						}
					}
					
					/**** Ministries ****/
					Date rotationOrderPubDate = null;
					CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					String strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
					if(strRotationOrderPubDate != null){
						try {
							rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
							model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
							Date currentDate = new Date();
							if (currentDate.equals(rotationOrderPubDate)
									|| currentDate.after(rotationOrderPubDate)) {
								List<Ministry> ministries = Ministry
										.findMinistriesAssignedToGroups(
												houseType, sessionYear,
												sessionType, locale);
								model.addAttribute("ministries", ministries);
								Ministry ministry = domain.getMinistry();
								if (ministry != null) {
									model.addAttribute("ministrySelected", ministry.getId());
									/**** Sub Departments ****/
									List<SubDepartment> subDepartments = MemberMinister.
											findAssignedSubDepartments(ministry, selectedSession.getEndDate(), locale);
									model.addAttribute("subDepartments", subDepartments);
									SubDepartment subDepartment = domain.getSubDepartment();
									if (subDepartment != null) {
										model.addAttribute("subDepartmentSelected", subDepartment.getId());
									}
								}
							}
						}
						catch (ParseException e) {
							logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
							model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
						}
					}else{
						logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
						model.addAttribute("errorcode", "rotationorderpubdate_notset");
					}
				}else{
					logger.error("**** Session doesnot exists ****");
					model.addAttribute("errorcode","session_isnull");	
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				String message = e.getMessage();
				if(message == null){
					message = "** There is som problem, request may not complete successfully.";
				}
				model.addAttribute("error", message);
				e.printStackTrace();
			}
		}else{
			logger.error("**** Check request parameters 'houseType,sessionYear and sessionType for null values' ****");
			model.addAttribute("errorcode","requestparams_isnull");
		} 
	}

	@Override
	protected String modifyEditUrlPattern(String newUrlPattern,
			final HttpServletRequest request, 
			final ModelMap model,
			final String locale) {
		
		/**** Edit Page Starts ****/
		String edit = request.getParameter("edit");
		if(edit != null){
			if(!Boolean.parseBoolean(edit)){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}
		CustomParameter editPage = CustomParameter.findByName(CustomParameter.class, "ABMOIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.findByName(CustomParameter.class, "ABMOIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles = this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return newUrlPattern;
			}else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return newUrlPattern.replace("edit", "assistant");
			}else if(i.getType().startsWith("ABMOIS_")) {
				return newUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "appropriationbillmotion/error";
		/**** Edit Page Ends ****/
	}

	@Override
	protected void populateEdit(final ModelMap model, AppropriationBillMotion domain,
			final HttpServletRequest request) {
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit",request.getParameter("bulkedit"));

		/**** Locale ****/
		String locale = domain.getLocale();
		model.addAttribute("formater", new FormaterUtil());
		
		try {
			Integer curNumber = AppropriationBillMotion.
					assignAppropriationBillMotionNo(domain.getHouseType(), domain.getSession(), domain.getDeviceType(), domain.getLocale());
			if(curNumber!=null) {
				System.out.println("curNumber: " + curNumber);
			}
		} catch (ELSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/**** House Type ****/
		HouseType houseType = domain.getHouseType();
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseType",houseType.getId());

		/**** Session ****/
		Session selectedSession = domain.getSession();
		model.addAttribute("session",selectedSession.getId());

		/**** Session Year ****/
		Integer sessionYear = 0;
		sessionYear = selectedSession.getYear();
		model.addAttribute("formattedSessionYear",FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear",sessionYear);

		/**** Session Type ****/
		SessionType  sessionType = selectedSession.getType();
		model.addAttribute("formattedSessionType",sessionType.getSessionType());
		model.addAttribute("sessionType",sessionType.getId());        

		/**** Motion Type ****/
		DeviceType motionType = domain.getDeviceType();
		model.addAttribute("formattedMotionType",motionType.getName());
		model.addAttribute("motionType",motionType.getId());
		model.addAttribute("selectedMotionType",motionType.getType());	

		/**** Bulk Edit ****/
		String bulkedit = request.getParameter("bulkedit");
		if (bulkedit != null) {
			model.addAttribute("bulkedit", bulkedit);
		} else {
			bulkedit = (String) request.getSession().getAttribute("bulkedit");
			if (bulkedit != null && !bulkedit.isEmpty()) {
				model.addAttribute("bulkedit", bulkedit);
				request.getSession().removeAttribute("bulkedit");
			}
		}
		
		/**** role ****/
		String role = request.getParameter("role");
		if (role != null) {
			model.addAttribute("role", role);
		} else {
			role = (String) request.getSession().getAttribute("role");
			model.addAttribute("role", role);
			request.getSession().removeAttribute("role");
		}
		
		/**** usergroup and usergroupType ****/
		String usergroupType = request.getParameter("usergroupType");
		if (usergroupType != null) {
			model.addAttribute("usergroupType", usergroupType);
		} else {
			usergroupType = (String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType", usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		String strUsergroup = request.getParameter("usergroup");
		if (strUsergroup != null) {
			model.addAttribute("usergroup", strUsergroup);
		} else {
			strUsergroup = (String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup", strUsergroup);
			request.getSession().removeAttribute("userGroup");
		}
		
		/**** Primary Member ****/
		String memberNames = null;
		String primaryMemberName = null;
		Member member = domain.getPrimaryMember();
		if (member != null) {
			model.addAttribute("primaryMember", member.getId());
			primaryMemberName = member.getFullname();
			memberNames = primaryMemberName;
			model.addAttribute("formattedPrimaryMember", primaryMemberName);
		}
		
		/**** Constituency ****/
		Long houseId = selectedSession.getHouse().getId();
		MasterVO constituency = null;
		if (houseType.getType().equals("lowerhouse")) {
			constituency = Member.findConstituencyByAssemblyId(member.getId(), houseId);
			model.addAttribute("constituency", constituency.getName());
		} else if (houseType.getType().equals("upperhouse")) {
			Date currentDate = new Date();
			String date = FormaterUtil.getDateFormatter("en_US").format(currentDate);
			constituency = Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
			model.addAttribute("constituency", constituency.getName());
		}

		/**** Supporting Members ****/
		List<SupportingMember> selectedSupportingMembers = domain.getSupportingMembers();
		List<Member> supportingMembers = new ArrayList<Member>();
		if(selectedSupportingMembers != null){
			if(!selectedSupportingMembers.isEmpty()){
				StringBuffer bufferFirstNamesFirst = new StringBuffer();
				for(SupportingMember i:selectedSupportingMembers){
					/**** All Supporting Members Are Preserved.But the names that appear in supporting 
					 * members list will vary. ****/
					Member m = i.getMember();
					supportingMembers.add(m);
					if(usergroupType.equals("clerk")){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if((usergroupType.equals("member"))
							&&domain.getInternalStatus() != null
							&&domain.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)
							&&i.getDecisionStatus() != null
							&&i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if((usergroupType.equals("member"))
							&&domain.getInternalStatus() != null
							&&(domain.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_INCOMPLETE)
									||domain.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE))){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if(usergroupType.equals("member")
							&&domain.getInternalStatus() == null){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if(!(usergroupType.equals("member"))
							&&i.getDecisionStatus() != null
							&&i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}
				}
				model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
				model.addAttribute("supportingMembers",supportingMembers);
				model.addAttribute("proxy", supportingMembers.get(0).getFullname());
				memberNames = primaryMemberName+","+bufferFirstNamesFirst.toString();
				model.addAttribute("memberNames",memberNames);
			}else{
				model.addAttribute("memberNames",memberNames);
			}
		}else{
			model.addAttribute("memberNames",memberNames);
		}
		/**** Ministries And Sub Departments ****/
		Date rotationOrderPubDate = null;
		CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
		if(strRotationOrderPubDate != null){
			try {
				rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
				model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
				Date currentDate = new Date();
				if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
					List<Ministry> ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
					model.addAttribute("ministries",ministries);
					Ministry ministry = domain.getMinistry();
					if(ministry != null){
						model.addAttribute("ministrySelected",ministry.getId());						
						/**** Sub Departments ****/
						List<SubDepartment> subDepartments=MemberMinister.
								findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
						model.addAttribute("subDepartments",subDepartments);
						SubDepartment subDepartment = domain.getSubDepartment();
						if(subDepartment != null){
							model.addAttribute("subDepartmentSelected",subDepartment.getId());
						}
					}							
				}
			}
			catch (ParseException e) {
				logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
				model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
		}else{
			logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
			model.addAttribute("errorcode", "rotationorderpubdate_notset");
		}
		/**** Submission Date,Creation date,WorkflowStartedOn date,TaskReceivedOn date****/ 
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat != null){            
			if(domain.getSubmissionDate() != null){
				model.addAttribute("submissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(domain.getCreationDate() != null){
				model.addAttribute("creationDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
			}
			if(domain.getWorkflowStartedOn() != null){
				model.addAttribute("workflowStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOn()));
			}
			if(domain.getTaskReceivedOn() != null){
				model.addAttribute("taskReceivedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOn()));
			}
		}
		/**** Number ****/
		if(domain.getNumber() != null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		
		/**** Amount demanded ****/
		if(domain.getAmountDemanded() != null){
			model.addAttribute("formattedAmountDemanded",FormaterUtil.formatNumberForIndianCurrencyWithSymbol(domain.getAmountDemanded(), locale));
		}
		
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());
		model.addAttribute("dataEnteredBy",domain.getDataEnteredBy());
		
		/**** Status,Internal Status and recommendation Status ****/
		Status status = domain.getStatus();
		Status internalStatus = domain.getInternalStatus();
		Status recommendationStatus = domain.getRecommendationStatus();
		if(status!=null){
			model.addAttribute("status",status.getId());
			model.addAttribute("memberStatusType",status.getType());
		}
		if(internalStatus != null){
			model.addAttribute("internalStatus",internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			/**** list of put up options available ****/
			if(usergroupType != null&&!(usergroupType.isEmpty())&&usergroupType.equals("assistant")){
				populateInternalStatus(model,domain,usergroupType,locale);
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

			}
		}
		if(recommendationStatus != null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
		}	
		
		/**** Referenced Entities are collected in refentities****/		
//		List<ReferenceUnit> referencedEntities=domain.getReferencedEntities();
//		if(referencedEntities != null && !referencedEntities.isEmpty()){
//			List<Reference> refentities = new ArrayList<Reference>();
//			List<Reference> refmotionentities = new ArrayList<Reference>();
//			List<Reference> refquestionentities = new ArrayList<Reference>();
//			List<Reference> refresolutionentities = new ArrayList<Reference>();
//			for(ReferenceUnit re:referencedEntities){
//				if(re.getDeviceType() != null){
//					if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_MOTIONS)){
//						Reference reference = new Reference();
//						reference.setId(String.valueOf(re.getId()));
//						reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(re.getNumber()));
//						reference.setNumber(String.valueOf(re.getId()));
//						refentities.add(reference);	
//						refmotionentities.add(reference);
//					}else if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
//						Reference reference = new Reference();
//						reference.setId(String.valueOf(re.getId()));
//						reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(re.getNumber()));
//						reference.setNumber(String.valueOf(re.getDevice()));
//						refentities.add(reference);	
//						refresolutionentities.add(reference);
//					}else if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_APPROPRIATIONBILLMOTIONS)){
//						Reference reference = new Reference();
//						reference.setId(String.valueOf(re.getId()));
//						reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(re.getNumber()));
//						reference.setNumber(String.valueOf(re.getDevice()));
//						refentities.add(reference);	
//						refquestionentities.add(reference);
//					}
//				}
//			}
//			model.addAttribute("referencedMotions",refmotionentities);
//			model.addAttribute("referencedQuestions",refquestionentities);
//			model.addAttribute("referencedResolutions",refresolutionentities);
//			model.addAttribute("referencedEntities",refentities);
//		}	
//		/**** Clubbed motions are collected in references ****/
//		List<ClubbedEntity> clubbedEntities = AppropriationBillMotion.findClubbedEntitiesByPosition(domain);
//		if (clubbedEntities != null && !clubbedEntities.isEmpty()) {
//			List<Reference> references = new ArrayList<Reference>();
//			StringBuffer buffer1 = new StringBuffer();
//			buffer1.append(memberNames + ",");
//			for (ClubbedEntity ce : clubbedEntities) {
//				Reference reference = new Reference();
//				reference.setId(String.valueOf(ce.getId()));
//				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getAppropriationBillMotion().getNumber()));
//				reference.setNumber(String.valueOf(ce.getAppropriationBillMotion().getId()));
//				references.add(reference);
//				String tempPrimary = ce.getAppropriationBillMotion().getPrimaryMember().getFullname();
//				if (!buffer1.toString().contains(tempPrimary)) {
//					buffer1.append(ce.getAppropriationBillMotion().getPrimaryMember().getFullname()+ ",");
//				}
//				List<SupportingMember> clubbedSupportingMember = ce.getAppropriationBillMotion().getSupportingMembers();
//				if (clubbedSupportingMember != null) {
//					if (!clubbedSupportingMember.isEmpty()) {
//						for (SupportingMember l : clubbedSupportingMember) {
//							String tempSupporting = l.getMember().getFullname();
//							if (!buffer1.toString().contains(tempSupporting)) {
//								buffer1.append(tempSupporting + ",");
//							}
//						}
//					}
//				}
//			}
//			if (!buffer1.toString().isEmpty()) {
//				buffer1.deleteCharAt(buffer1.length() - 1);
//			}
//			String allMembersNames = buffer1.toString();
//			model.addAttribute("memberNames", allMembersNames);
//			if (!references.isEmpty()) {
//				model.addAttribute("clubbedEntities", references);
//			} else {
//				if (domain.getParent() != null) {
//					model.addAttribute("formattedParentNumber",
//							FormaterUtil.getNumberFormatterNoGrouping(locale)
//									.format(domain.getParent().getNumber()));
//					model.addAttribute("parent", domain.getParent().getId());
//				}
//			}
//		}		
		/**** Populating Put up otions and Actors ****/
		if(domain.getInternalStatus() != null){
			String internalStatusType = domain.getInternalStatus().getType();			
			if(usergroupType != null && !usergroupType.isEmpty() && usergroupType.equals("assistant")){
				
				CustomParameter csptAssistantAllowedToStartWorkflows = CustomParameter.findByName(CustomParameter.class, "ABMOIS_ASSISTANT_ALLOWED_TO_START_WORKFLOWS", "");
				if(csptAssistantAllowedToStartWorkflows != null && csptAssistantAllowedToStartWorkflows.getValue() != null
						&& !csptAssistantAllowedToStartWorkflows.getValue().isEmpty()
						&& csptAssistantAllowedToStartWorkflows.getValue().contains(internalStatusType)){
				
					UserGroup userGroup = UserGroup.findById(UserGroup.class,Long.parseLong(strUsergroup));
					List<Reference> actors = WorkflowConfig.findAppropriationBillMotionActorsVO(domain, internalStatus, userGroup, 1, locale);
					model.addAttribute("internalStatusSelected",internalStatus.getId());
					model.addAttribute("actors",actors);
					if(actors != null && !actors.isEmpty()){
						String nextActor = actors.get(0).getId().toString();
						String[] actorArr = nextActor.split("#");
						domain.setLevel(actorArr[2]);
						domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
					}
				}
			}	
		}
		/**** Reply related Dates ****/
		if(domain.getReplyRequestedDate() != null) {
			model.addAttribute("formattedReplyRequestedDate",
					FormaterUtil.formatDateToString(domain.getReplyRequestedDate(), 
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		if(domain.getReplyReceivedDate()!=null) {
			model.addAttribute("formattedReplyReceivedDate", 
					FormaterUtil.formatDateToString(domain.getReplyReceivedDate(),
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
	}

	private void populateInternalStatus(final ModelMap model,final AppropriationBillMotion domain,final String usergroupType,final String locale) {
		try {
			List<Status> internalStatuses=new ArrayList<Status>();
			DeviceType deviceType=domain.getDeviceType();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseType=domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				}
			}/**** APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			}/**** APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			}	
			/**** APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
			}	
			/**** Internal Status****/
			model.addAttribute("internalStatuses",internalStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}		
	}

	private void populateSupportingMembers(final AppropriationBillMotion domain,final HttpServletRequest request){
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
			AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class,domain.getId());
			members = motion.getSupportingMembers();
		}		
		/**** New Status ****/
		Status notsendStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_NOTSEND, domain.getLocale());
		Status approvedStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, domain.getLocale());
		/**** New Supporting Members+Already present Supporting Members ****/
		List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
		/**** Offline-Online Supporting Members Approval ****/
		String dataEntryType=request.getParameter("dataEntryType");
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
						if(dataEntryType!=null&&!(dataEntryType.isEmpty())){
							supportingMember.setDecisionStatus(approvedStatus);
							supportingMember.setApprovalType("OFFLINE");
							supportingMember.setApprovedSubject(domain.getMainTitle());
							supportingMember.setApprovedText(domain.getNoticeContent());						
						}else{
							supportingMember.setDecisionStatus(notsendStatus);
							supportingMember.setApprovalType("ONLINE");
						}
					}
					/*** List is updated ****/
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
	}	

	@Override
	protected void preValidateCreate(AppropriationBillMotion domain, BindingResult result,
			HttpServletRequest request) {
		/**** Populate Supporting Members ****/
		populateSupportingMembers(domain,request);
		
		/**** Set Amount demanded ****/
		String setAmountDemanded = request.getParameter("setAmountDemanded");
		if(setAmountDemanded!=null && !setAmountDemanded.isEmpty()) {
			BigDecimal amountDemanded = FormaterUtil.parseNumberForIndianCurrencyWithSymbol(setAmountDemanded, domain.getLocale());
			if(amountDemanded==null) {
				amountDemanded = FormaterUtil.parseNumberForIndianCurrency(setAmountDemanded, domain.getLocale());
			}
			domain.setAmountDemanded(amountDemanded);
			System.out.println("amountDemanded: " + domain.getAmountDemanded());
		}
		
		/**** Set department ****/
		if(domain.getSubDepartment() != null){
			domain.setDepartment(domain.getSubDepartment().getDepartment());
		}
	}

	@Override
	protected void customValidateCreate(final AppropriationBillMotion domain, final BindingResult result,
			final HttpServletRequest request) {		
		String role = request.getParameter("role");
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
			return;
		}
		/**** Minimum Field Validations to be filled or autofilled ****/
		if(domain.getHouseType()==null){
			result.rejectValue("houseType","HousetypeEmpty");
			return;
		}
		if(domain.getDeviceType()==null){
			result.rejectValue("type","MotionTypeEmpty");
			return;
		}
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
			return;
		}
		if(domain.getPrimaryMember()==null){
			result.rejectValue("primaryMember","PrimaryMemberEmpty");
			return;
		}
		if(domain.getMainTitle().isEmpty()){
			result.rejectValue("mainTitle", "AppropriationBillMotion.MainTitleEmpty");
			return;
		}
		if(domain.getNoticeContent().isEmpty()){
			result.rejectValue("noticeContent","AppropriationBillMotion.NoticeContentEmpty");
			return;
		}
//		String usergroupType=request.getParameter("usergroupType");
//		if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("assistant")){
//			if(domain.getMinistry()==null){
//				result.rejectValue("ministry","AppropriationBillMotion.MinistryEmpty");
//				return;
//			}	
//			if(domain.getSubDepartment()==null){
//				result.rejectValue("subDepartment","AppropriationBillMotion.SubDepartmentEmpty");
//				return;
//			}	
//		}
		/**** Number Validation ****/
		if(role.equals("ABMOIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ABMOIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");			
						return;
					}
					//check for duplicate motion
					Boolean flag = AppropriationBillMotion.isExist(domain.getNumber(),null,domain.getDeviceType(), domain.getSession(), domain.getLocale());
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
						return;
					}
				}
			}
		}
		
		if(domain!=null && domain.getPrimaryMember()!=null 
				&& domain.getPrimaryMember().isSuspendedMember()) {
			result.rejectValue("version", "suspension.user");
			return;
		}
		
		/**** Operation Based Validations ****/
		String operation = request.getParameter("operation");
		if(operation != null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")) { /**** Supporting Member Approval Related Validations ****/							
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");
						return;
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");	
						return;
					} else {
						//check if request is already sent for approval
						int count=0;
						for(SupportingMember i:domain.getSupportingMembers()){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
								count++;
							}
						}
						if(count==0){
							result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
							return;
						}
					}
				} else if(operation.equals("submit")) { /**** Submission Related Validations ****/	
					if(domain.getAmountDemanded()==null) {
						result.rejectValue("amountDemanded","AppropriationBillMotion.AmountDemandedEmpty");
						return;
					}
					if(domain.getDemandNumber()==null || domain.getDemandNumber().isEmpty()) {
						result.rejectValue("demandNumber","AppropriationBillMotion.DemandNumberEmpty");
						return;
					}
					if(domain.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {
						if(domain.getItemNumber()==null) {
							result.rejectValue("itemNumber","AppropriationBillMotion.ItemNumberEmpty");
							return;
						}
					}
					if(domain.getMinistry()==null){
						result.rejectValue("ministry","AppropriationBillMotion.MinistryEmpty");
						return;
					}		
					if(domain.getSubDepartment()==null) {
						result.rejectValue("subDepartment","AppropriationBillMotion.SubDepartmentEmpty");
						return;
					}
//					if(AppropriationBillMotion.isDepartmentwiseMaximumLimitForMemberReached(domain.getDeviceType(), domain.getSession(), domain.getPrimaryMember(), domain.getDepartment(), domain.getLocale())) {
//						result.rejectValue("version","AppropriationBillMotion.DepartmentwiseMaximumLimitForMemberReached");
//						return;
//					}
//					CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getDeviceType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG", "");
//					if(csptOfflineSubmissionAllowedFlag!=null 
//							&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
//							&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
//						if(!role.equals("ABMOIS_TYPIST")){
//							if(!isDateAdmitted(domain, domain.getLocale())){
//								result.rejectValue("version","AppropriationBillMotion.DateExpired");
//								return;
//							}
//						}
//					} else {
//						if(!isDateAdmitted(domain, domain.getLocale())){
//							result.rejectValue("version","AppropriationBillMotion.DateExpired");
//							return;
//						}
//					}	
					//submission date limit validations (configurable through custom parameters and session parameters)
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
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Appropriation Bill Motion cannot be submitted before " + submissionStartLimitDateStr);
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
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Appropriation Bill Motion cannot be submitted after " + submissionEndLimitDateStr);
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
			}
		}
	}
	
	@Override
	protected void preValidateUpdate(AppropriationBillMotion domain, BindingResult result,
			HttpServletRequest request) {
		/**** Populate Supporting Members ****/
		populateSupportingMembers(domain,request);
		
		/**** Set Amount demanded ****/
		String setAmountDemanded = request.getParameter("setAmountDemanded");
		if(setAmountDemanded!=null && !setAmountDemanded.isEmpty()) {
			BigDecimal amountDemanded = FormaterUtil.parseNumberForIndianCurrencyWithSymbol(setAmountDemanded, domain.getLocale());
			if(amountDemanded==null) {
				amountDemanded = FormaterUtil.parseNumberForIndianCurrency(setAmountDemanded, domain.getLocale());
			}
			domain.setAmountDemanded(amountDemanded);
			System.out.println("amountDemanded: " + domain.getAmountDemanded());
		}
		
		/**** Set department ****/
		if(domain.getSubDepartment() != null){
			domain.setDepartment(domain.getSubDepartment().getDepartment());
		}
	}

	@Override
	protected void customValidateUpdate(final AppropriationBillMotion domain, final BindingResult result,
			final HttpServletRequest request) {		
		String role = request.getParameter("role");
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
			return;
		}
		/**** Minimum Field Validations to be filled or autofilled ****/
		if(domain.getHouseType()==null){
			result.rejectValue("houseType","HousetypeEmpty");
			return;
		}
		if(domain.getDeviceType()==null){
			result.rejectValue("type","MotionTypeEmpty");
			return;
		}
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
			return;
		}
		if(domain.getPrimaryMember()==null){
			result.rejectValue("primaryMember","PrimaryMemberEmpty");
			return;
		}
		if(domain.getMainTitle().isEmpty()){
			result.rejectValue("mainTitle", "AppropriationBillMotion.MainTitleEmpty");
			return;
		}
		if(domain.getNoticeContent().isEmpty()){
			result.rejectValue("noticeContent","AppropriationBillMotion.NoticeContentEmpty");
			return;
		}
//		String usergroupType=request.getParameter("usergroupType");
//		if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("assistant")){
//			if(domain.getMinistry()==null){
//				result.rejectValue("ministry","AppropriationBillMotion.MinistryEmpty");
//				return;
//			}	
//			if(domain.getSubDepartment()==null){
//				result.rejectValue("subDepartment","AppropriationBillMotion.SubDepartmentEmpty");
//				return;
//			}	
//		}
		/**** Number Validation ****/
		if(role.equals("ABMOIS_TYPIST")){							
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ABMOIS_TYPIST_AUTO_NUMBER_GENERATION_REQUIRED", "");
			if(customParameter != null){
				String value = customParameter.getValue();
				if(!value.equals("yes")){
					if(domain.getNumber()==null){
						result.rejectValue("number","NumberEmpty");		
						return;
					}
					//check for duplicate motion
					Boolean flag = AppropriationBillMotion.isExist(domain.getNumber(),domain.getId(),domain.getDeviceType(), domain.getSession(), domain.getLocale());
					if(flag){
						result.rejectValue("number", "NonUnique","Duplicate Parameter");
						return;
					}
				}
			}
		}
		
		if(domain!=null && domain.getPrimaryMember()!=null 
				&& domain.getPrimaryMember().isSuspendedMember()) {
			result.rejectValue("version", "suspension.user");
			return;
		}
		
		/**** Operation Based Validations ****/
		String operation=request.getParameter("operation");
		if(operation!=null && !operation.isEmpty()) {
			if(operation.equals("approval")) { /**** Supporting Member Approval Related Validations ****/							
				if(domain.getSupportingMembers()==null){
					result.rejectValue("supportingMembers","SupportingMembersEmpty");
					return;
				} else if(domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers","SupportingMembersEmpty");	
					return;
				} else {
					//check if request is already sent for approval
					int count=0;
					for(SupportingMember i:domain.getSupportingMembers()){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							count++;
						}
					}
					if(count==0){
						result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
						return;
					}
				}
			} else if(operation.equals("submit")) { /**** Submission Related Validations ****/			
				if(domain.getAmountDemanded()==null) {
					result.rejectValue("amountDemanded","AppropriationBillMotion.AmountDemandedEmpty");
					return;
				}
				if(domain.getDemandNumber()==null || domain.getDemandNumber().isEmpty()) {
					result.rejectValue("demandNumber","AppropriationBillMotion.DemandNumberEmpty");
					return;
				}
				if(domain.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {
					if(domain.getItemNumber()==null) {
						result.rejectValue("itemNumber","AppropriationBillMotion.ItemNumberEmpty");
						return;
					}
				}
				if(domain.getMinistry()==null){
					result.rejectValue("ministry","AppropriationBillMotion.MinistryEmpty");
					return;
				}		
				if(domain.getSubDepartment()==null) {
					result.rejectValue("subDepartment","AppropriationBillMotion.SubDepartmentEmpty");
					return;
				}
//				if(AppropriationBillMotion.isDepartmentwiseMaximumLimitForMemberReached(domain.getDeviceType(), domain.getSession(), domain.getPrimaryMember(), domain.getDepartment(), domain.getLocale())) {
//					result.rejectValue("version","AppropriationBillMotion.DepartmentwiseMaximumLimitForMemberReached");
//					return;
//				}
//				CustomParameter csptOfflineSubmissionAllowedFlag = CustomParameter.findByName(CustomParameter.class, domain.getDeviceType().getType().toUpperCase()+"_OFFLINE_SUBMISSION_ALLOWED_FLAG", "");
//				if(csptOfflineSubmissionAllowedFlag!=null 
//						&& csptOfflineSubmissionAllowedFlag.getValue()!=null 
//						&& csptOfflineSubmissionAllowedFlag.getValue().equals("YES")) {
//					if(!role.equals("ABMOIS_TYPIST")){
//						if(!isDateAdmitted(domain, domain.getLocale())){
//							result.rejectValue("version","AppropriationBillMotion.DateExpired");
//							return;
//						}
//					}
//				} else {
//					if(!isDateAdmitted(domain, domain.getLocale())){
//						result.rejectValue("version","AppropriationBillMotion.DateExpired");
//						return;
//					}
//				}
				//submission date limit validations (configurable through custom parameters and session parameters)
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
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Appropriation Bill Motion cannot be submitted before " + submissionStartLimitDateStr);
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
											result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Appropriation Bill Motion cannot be submitted after " + submissionEndLimitDateStr);
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
			} else if(operation.equals("startworkflow")) { /**** Start Workflow Related Validations ****/
				String internalStatusType=domain.getInternalStatus().getType();
				if(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)){
					result.rejectValue("internalStatus","PutUpOptionEmpty");
					return;
				}						
				if(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SYSTEM_ASSISTANT_PROCESSED)){
					result.rejectValue("internalStatus","PutUpOptionEmpty");
					return;
				}
				if(!(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT))
						&&!(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SYSTEM_ASSISTANT_PROCESSED))
						&&(domain.getActor()==null||domain.getActor().isEmpty())){
					result.rejectValue("internalStatus","ActorEmpty");
					return;
				}
			}			
		}
	}

	@Override
	protected void populateCreateIfErrors(final ModelMap model, AppropriationBillMotion domain,
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
		
		/**** Set Amount demanded ****/
		String setAmountDemanded = request.getParameter("setAmountDemanded");
		if(setAmountDemanded!=null && !setAmountDemanded.isEmpty()) {
			BigDecimal amountDemanded = FormaterUtil.parseNumberForIndianCurrencyWithSymbol(setAmountDemanded, domain.getLocale());
			if(amountDemanded==null) {
				amountDemanded = FormaterUtil.parseNumberForIndianCurrency(setAmountDemanded, domain.getLocale());
			}
			domain.setAmountDemanded(amountDemanded);
			System.out.println("amountDemanded: " + domain.getAmountDemanded());
		}
		
		super.populateCreateIfErrors(model, domain, request);
	}

	@Override
	protected void populateUpdateIfErrors(final ModelMap model, AppropriationBillMotion domain,
			final HttpServletRequest request) {
		
		/**** Set Amount demanded ****/
		String setAmountDemanded = request.getParameter("setAmountDemanded");
		if(setAmountDemanded!=null && !setAmountDemanded.isEmpty()) {
			BigDecimal amountDemanded = FormaterUtil.parseNumberForIndianCurrencyWithSymbol(setAmountDemanded, domain.getLocale());
			if(amountDemanded==null) {
				amountDemanded = FormaterUtil.parseNumberForIndianCurrency(setAmountDemanded, domain.getLocale());
			}
			domain.setAmountDemanded(amountDemanded);
			System.out.println("amountDemanded: " + domain.getAmountDemanded());
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
		/**** Reply related Dates ****/
		if(domain.getReplyRequestedDate() != null) {
			model.addAttribute("formattedReplyRequestedDate",
					FormaterUtil.formatDateToString(domain.getReplyRequestedDate(), 
							ApplicationConstants.SERVER_DATETIMEFORMAT, domain.getLocale()));
		}
		if(domain.getReplyReceivedDate()!=null) {
			model.addAttribute("formattedReplyReceivedDate", 
					FormaterUtil.formatDateToString(domain.getReplyReceivedDate(),
							ApplicationConstants.SERVER_DATETIMEFORMAT, domain.getLocale()));
		}
		model.addAttribute("formater", new FormaterUtil());
		super.populateUpdateIfErrors(model, domain, request);
	}

	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, AppropriationBillMotion domain,
			final HttpServletRequest request) {			
		
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation = request.getParameter("operation");
		String usergroupType = request.getParameter("usergroupType");
		if(domain.getHouseType() != null && domain.getSession()!=null
				&&  domain.getDeviceType() != null && domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
				(!domain.getMainTitle().isEmpty())
				&&(!domain.getNoticeContent().isEmpty())){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						CustomParameter csptAllowedUserGroupsToSubmit = CustomParameter.findByName(CustomParameter.class, "ABMOIS_ALLOWED_USERGROUPTYPES_TO_SUBMIT", "");
						if(usergroupType != null && !(usergroupType.isEmpty()) && csptAllowedUserGroupsToSubmit != null && csptAllowedUserGroupsToSubmit.getValue() != null && csptAllowedUserGroupsToSubmit.getValue().contains(usergroupType)){
							/****  submission date is set ****/
							if(domain.getSubmissionDate()==null){
								domain.setSubmissionDate(new Date());
							}
							/**** only those supporting memebrs will be included who have approved the requests ****/
							List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
							Status timeoutStatus=Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, domain.getLocale());
							if(domain.getSupportingMembers()!=null){
								if(!domain.getSupportingMembers().isEmpty()){
									for(SupportingMember i:domain.getSupportingMembers()){
										if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)||
												i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)){
											/**** Update Supporting Member ****/
											i.setDecisionStatus(timeoutStatus);
											i.setApprovalDate(new Date());	
											i.setApprovedText(domain.getNoticeContent());
											i.setApprovedSubject(domain.getMainTitle());
											i.setApprovalType("ONLINE");
											/**** Update Workflow Details ****/
											String strWorkflowdetails=i.getWorkflowDetailsId();
											if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
												WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
												workflowDetails.setStatus("TIMEOUT");
												workflowDetails.setCompletionTime(new Date());
												workflowDetails.merge();
												/**** Complete Task ****/
												String strTaskId=workflowDetails.getTaskId();
												Task task=processService.findTaskById(strTaskId);
												processService.completeTask(task);
											}		
										}
										if(!i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
											supportingMembers.add(i);
										}		
									}
									domain.setSupportingMembers(supportingMembers);
								}
							}
							/**** Status,Internal Status and recommendation Status is set ****/
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT, domain.getLocale());
							domain.setStatus(newstatus);
							domain.setInternalStatus(newstatus);
							domain.setRecommendationStatus(newstatus);
							domain.setWorkflowStarted("NO");
						}
					}else{
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE, domain.getLocale());
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
							domain.setWorkflowStarted("NO");
						}
					}
				}else{
					if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
						domain.setWorkflowStarted("NO");
					}
				}
			}else{
				if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
					domain.setWorkflowStarted("NO");
				}
			}
		}
		/**** Drafts ****/
		else{
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_INCOMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setDataEnteredBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		/**** add department ****/
		if(domain.getSubDepartment()!=null){
			domain.setDepartment(domain.getSubDepartment().getDepartment());
		}
	}

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, AppropriationBillMotion domain,
			final HttpServletRequest request) throws ELSException {
		
		/**** Checking if its submission request or normal update ****/
		String locale = domain.getLocale();
		String operation=request.getParameter("operation");		
		String role = request.getParameter("role");
		String usergroupType=request.getParameter("usergroupType");
		UserGroupType userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		if(userGroupType==null) {
			throw new ELSException("AppropriationBillMotionController.populateUpdateIfNoErrors/3", "request parameter 'usergroupType' not set");
		}
		/**** Question status will be complete if all mandatory fields have been filled ****/
		if(domain.getHouseType()!=null&&domain.getDeviceType()!=null&&domain.getSession()!=null
				&& domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
				(!domain.getMainTitle().isEmpty())
				&&(!domain.getNoticeContent().isEmpty())){			
			if(operation!=null){
				if(!operation.isEmpty()){
					/**** Submission request ****/
					if(operation.trim().equals("submit")){
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&(usergroupType.equals("member")||usergroupType.equals("clerk"))){
							/**** Submission date is set ****/
							if(domain.getSubmissionDate()==null){
								domain.setSubmissionDate(new Date());
							}
							/**** Supporting Members who have approved request are included ****/
							List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
							Status timeoutStatus=Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, domain.getLocale());
							if(domain.getSupportingMembers()!=null){
								if(!domain.getSupportingMembers().isEmpty()){
									for(SupportingMember i:domain.getSupportingMembers()){
										if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)||
												i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)){
											/**** Update Supporting Member ****/
											i.setDecisionStatus(timeoutStatus);
											i.setApprovalDate(new Date());	
											i.setApprovedText(domain.getNoticeContent());
											i.setApprovedSubject(domain.getMainTitle());
											i.setApprovalType("ONLINE");
											/**** Update Workflow Details ****/
											String strWorkflowdetails=i.getWorkflowDetailsId();
											if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
												WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
												workflowDetails.setStatus("TIMEOUT");
												workflowDetails.setCompletionTime(new Date());
												workflowDetails.setInternalStatus(timeoutStatus.getName());
												workflowDetails.setRecommendationStatus(timeoutStatus.getName());
												workflowDetails.merge();
												/**** Complete Task ****/
												String strTaskId=workflowDetails.getTaskId();
												Task task=processService.findTaskById(strTaskId);
												processService.completeTask(task);
											}											
										}
										if(!i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
											supportingMembers.add(i);
										}									
									}
									domain.setSupportingMembers(supportingMembers);
								}
							}
							/**** Status,Internal status and recommendation status is set to complete ****/
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT, domain.getLocale());
							domain.setStatus(newstatus);
							domain.setInternalStatus(newstatus);
							domain.setRecommendationStatus(newstatus);
							domain.setWorkflowStarted("NO");
						}
					}else{
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE, domain.getLocale());
							/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
							if(!domain.getStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)){
								domain.setStatus(status);
								domain.setInternalStatus(status);
								domain.setRecommendationStatus(status);
								domain.setWorkflowStarted("NO");
							}	
						}
					}
				}else{
					if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE, domain.getLocale());
						/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
						if(!domain.getStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)){
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
							domain.setWorkflowStarted("NO");
						}
					}
				}
			}else{
				if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE, domain.getLocale());
					/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
					if(!domain.getStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
						domain.setWorkflowStarted("NO");
					}
				}
			}
		}
		/**** If all mandatory fields have not been set then status,internal status and recommendation status is set to incomplete ****/
		else{
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_INCOMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
				domain.setWorkflowStarted("NO");
			}
		}
		/**** reply related dates ****/
		String strReplyRequestedDate = request.getParameter("setReplyRequestedDate");
		if(strReplyRequestedDate != null && !strReplyRequestedDate.isEmpty()) {
			domain.setReplyRequestedDate(FormaterUtil.formatStringToDate(strReplyRequestedDate, ApplicationConstants.SERVER_DATETIMEFORMAT));					
		}
		String strReplyReceivedDate = request.getParameter("setReplyReceivedDate");
		if(strReplyReceivedDate !=null && !strReplyReceivedDate.isEmpty()) {
			domain.setReplyReceivedDate(FormaterUtil.formatStringToDate(strReplyReceivedDate, ApplicationConstants.SERVER_DATETIMEFORMAT));					
		}
		/**** Edited On,Edited By and Edited As is set ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			//UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
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
		/**** add department ****/
		if(domain.getSubDepartment()!=null){
			domain.setDepartment(domain.getSubDepartment().getDepartment());
		}	
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		CustomParameter assistantProcessedAllowed = CustomParameter.
				findByName(CustomParameter.class,"ABMOIS_ASSISTANT_PROCESSED_ALLOWED_FOR","");
		if(assistantProcessedAllowed != null){
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedFieldName(UserGroupType.class, "type", assistantProcessedAllowed.getValue(), ",", locale);
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
			if(isUserGroupAllowed){
				String internalStatus = domain.getInternalStatus().getType();
				if(internalStatus.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT) 
						&& domain.getMinistry()!=null 
						&& domain.getSubDepartment()!=null) {
					Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.APPROPRIATIONBILLMOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}
			}
		}
//		if(strUserGroupType!=null){
//			if(strUserGroupType.equals("assistant")){				
//				String internalStatus = domain.getInternalStatus().getType();
//				if(internalStatus.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)&&domain.getMinistry()!=null&&domain.getSubDepartment()!=null) {
//					Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.APPROPRIATIONBILLMOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
//					domain.setInternalStatus(ASSISTANT_PROCESSED);
//					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
//				}
//			}
//		}		
	}

	@Override
	protected void populateAfterCreate(final ModelMap model,AppropriationBillMotion domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));

		/**** Supporting Member Workflow ****/
		String operation = request.getParameter("operation");
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("userrole",request.getParameter("userrole"));
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** process Started ****/
					ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties = new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",domain.getDeviceType().getType());
					ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);
					List<Task> tasks = processService.getCurrentTasks(processInstance);
					List<WorkflowDetails> workflowDetails;
					try {
						workflowDetails = WorkflowDetails.create(domain, tasks, ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"0");
						
						/**** Supporting members status changed to pending ****/
						AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class,domain.getId());
						List<SupportingMember> supportingMembers = motion.getSupportingMembers();
						Status status = Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
						for(SupportingMember i:supportingMembers){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
								i.setDecisionStatus(status);
								i.setRequestReceivedOn(new Date());
								i.setApprovalType("ONLINE");
								User user=User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
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
						
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
					}
				}
			}
		}
	}

	@Transactional
	@Override
	protected void populateAfterUpdate(final ModelMap model, AppropriationBillMotion domain,
			final HttpServletRequest request) {
		
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		
		if(request.getParameter("bulkedit")!=null&&!request.getParameter("bulkedit").isEmpty()){
			request.getSession().setAttribute("bulkedit",request.getParameter("bulkedit"));
		}
		
		/**** Approval Workflow ****/
		String operation = request.getParameter("operation");
		if(operation != null){
			if(!operation.isEmpty()){
				/**** Supporting Member Workflow ****/
				if(operation.equals("approval")){
					/**** Added by Sandeep Singh ****/
					/**** Supporting Member Workflow is started ****/
					try {
						ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
						Map<String,String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId",String.valueOf(domain.getId()));
						properties.put("pv_deviceTypeId",domain.getDeviceType().getType());
						ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
						
						/**** Workflow Details Entries are created ****/
						List<Task> tasks = processService.getCurrentTasks(processInstance);					
						List<WorkflowDetails> workflowDetails = WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"");
						
						/**** Not Send supporting members status are changed to pending ****/
						AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class,domain.getId());
						List<SupportingMember> supportingMembers = motion.getSupportingMembers();
						Status status = Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
						for(SupportingMember i:supportingMembers){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
								i.setDecisionStatus(status);
								i.setRequestReceivedOn(new Date());
								i.setApprovalType("ONLINE");
								User user = User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
								Credential credential = user.getCredential();
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
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
					}
				}else if(operation.equals("startworkflow")){
					try {
						ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						Map<String,String> properties = new HashMap<String, String>();					
						/**** Next user and usergroup ****/
						String nextuser = domain.getActor();
						String level = "";
						UserGroupType usergroupType = null;
						if(nextuser != null){
							if(!nextuser.isEmpty()){
								String[] temp = nextuser.split("#");
								properties.put("pv_user",temp[0]);
								usergroupType = UserGroupType.findByType(temp[1], domain.getLocale());
								level = temp[2];
							}
						}
						String endflag = domain.getEndFlag();
						properties.put("pv_endflag",endflag);	
						properties.put("pv_deviceId",String.valueOf(domain.getId()));
						properties.put("pv_deviceTypeId",String.valueOf(domain.getDeviceType().getId()));
						ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);
						
						/**** Stale State Exception ****/
						AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class,domain.getId());
						/**** Process Started and task created ****/
						Task task = processService.getCurrentTask(processInstance);
						if(endflag != null){
							if(!endflag.isEmpty()){
								if(endflag.equals("continue")){
									/**** Workflow Detail entry made only if its not the end of workflow ****/
									//WorkflowDetails workflowDetails = WorkflowDetails.create(domain, task, ApplicationConstants.APPROVAL_WORKFLOW, level);
									
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

//									if(recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING_POST_ADMISSION)
//											|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING_POST_ADMISSION)
//											|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_UNCLUBBING)
//											|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
//										workflow = Workflow.findByStatus(recommendationStatus, domain.getLocale());
//									} 
//									else if(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING)
//											|| internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_NAME_CLUBBING)
//											|| (internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
//												&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
//											||(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
//												&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_RECEIVED))
//											||(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
//												&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
//											||(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
//												&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
//										workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
//									} 
//									else {
//										workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
//									}
									workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
									
									WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
									motion.setWorkflowDetailsId(workflowDetails.getId());
									//*****
								}
							}
						}
						/**** Workflow Started ****/
						motion.setWorkflowStarted("YES");
						motion.setWorkflowStartedOn(new Date());
						motion.setTaskReceivedOn(new Date());
						motion.simpleMerge();
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
					}
				}
			}
		}
		
	}

	/**** Supporting Members View Status ****/
	@RequestMapping(value="/status/{motion}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("motion") final String motion){
		AppropriationBillMotion motionTemp = AppropriationBillMotion.findById(AppropriationBillMotion.class,Long.parseLong(motion));
		List<SupportingMember> supportingMembers = motionTemp.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "appropriationbillmotion/supportingmember";
	}

	/**** Member-Supporting Members Contacts ****/
	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers = request.getParameter("members");
		String[] members = strMembers.split(",");
		List<MemberContactVO> memberContactVOs = Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "appropriationbillmotion/contacts";
	}

	/**** revision History ****/
	@RequestMapping(value="/revisions/{motionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("motionId")  final Long motionId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts = AppropriationBillMotion.getRevisions(motionId,locale.toString());
		AppropriationBillMotion m = AppropriationBillMotion.findById(AppropriationBillMotion.class, motionId);
		if(m != null){
			if(m.getDeviceType() != null){
				if(m.getDeviceType().getType() != null){
					model.addAttribute("selectedDeviceType", m.getDeviceType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);
		return "appropriationbillmotion/revisions";
	}

	/**** Citations ****/
	@RequestMapping(value="/citations/{deviceType}",method=RequestMethod.GET)
	public String getCitations(final HttpServletRequest request, final Locale locale,@PathVariable("deviceType")  final Long type,
			final ModelMap model){
		DeviceType deviceType = DeviceType.findById(DeviceType.class,type);
		List<Citation> deviceTypeBasedcitations = Citation.findAllByFieldName(Citation.class,"deviceType",deviceType, "text",ApplicationConstants.ASC, locale.toString());
		Status status=null;
		if(request.getParameter("status") != null){
			status=Status.findById(Status.class, Long.parseLong(request.getParameter("status")));
		}
		List<Citation> citations = new ArrayList<Citation>();
		if(status != null){
			for(Citation i:deviceTypeBasedcitations){
				if(i.getStatus() != null){
					if(i.getStatus().equals(status.getType())){
						citations.add(i);
					}
				}
			}
		}
		model.addAttribute("citations",citations);
		return "appropriationbillmotion/citation";
	}

	/**** Bulk Submission ****/
	@RequestMapping(value = "/bulksubmission", method = RequestMethod.GET)
	public String getBulkSubmissionView(final HttpServletRequest request,
			final Locale locale, final ModelMap model) {
		try {
			Member primaryMember = Member.findMember(this.getCurrentUser().getFirstName(), this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(), this.getCurrentUser().getBirthDate(), locale.toString());
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strMotionType = request.getParameter("appropriationBillMotionType");
			String strLocale = locale.toString();
			String strItemsCount = request.getParameter("itemscount");
			
			if (strHouseType != null && !(strHouseType.isEmpty())
					&& strSessionType != null && !(strSessionType.isEmpty())
					&& strSessionYear != null && !(strSessionYear.isEmpty())
					&& strMotionType != null && !(strMotionType.isEmpty())
					&& strItemsCount != null && !(strItemsCount.isEmpty())) {
				
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Integer sessionYear = Integer.parseInt(strSessionYear);
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				DeviceType motionType = DeviceType.findById(DeviceType.class, Long.parseLong(strMotionType));
				Integer itemsCount = Integer.parseInt(strItemsCount);
				List<AppropriationBillMotion> motions = new ArrayList<AppropriationBillMotion>();
				if (primaryMember != null) {
					motions = AppropriationBillMotion.findAllByMember(session, primaryMember, motionType, itemsCount, strLocale);
				}
				model.addAttribute("motions", motions);
				model.addAttribute("size", motions.size());
				String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupType", userGroupType);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
		return "appropriationbillmotion/bulksubmission";
	}

	/**
	 * We want to provide a guarantee that all the motions submitted by a 
	 * particular member will get numbers assigned sequentially. Hence, the
	 * use of synchronized method.
	 * 
	 * @param request
	 * @param locale
	 * @param model
	 * @return
	 */
	@Transactional
	@RequestMapping(value="bulksubmission", method=RequestMethod.POST)
	public synchronized String bulkSubmission(final HttpServletRequest request,
			final Locale locale, final ModelMap model) {
		String selectedItems = request.getParameter("items");
		if (selectedItems != null && !selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");

			List<AppropriationBillMotion> motions = new ArrayList<AppropriationBillMotion>();
			for (String i : items) {
				Long id = Long.parseLong(i);
				AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class, id);

				/**** Update Supporting Member ****/
				List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
				Status timeoutStatus = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
				if (motion.getSupportingMembers() != null) {
					if (!motion.getSupportingMembers().isEmpty()) {
						for (SupportingMember sm : motion.getSupportingMembers()) {
							if (sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)
									|| sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
								/**** Update Supporting Member ****/
								sm.setDecisionStatus(timeoutStatus);
								sm.setApprovalDate(new Date());
								sm.setApprovedText(motion.getNoticeContent());
								sm.setApprovedSubject(motion.getMainTitle());
								sm.setApprovalType("ONLINE");
								/**** Update Workflow Details ****/
								String strWorkflowdetails = sm.getWorkflowDetailsId();
								if (strWorkflowdetails != null
										&& !strWorkflowdetails.isEmpty()) {
									WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
									workflowDetails.setStatus("TIMEOUT");
									workflowDetails.setCompletionTime(new Date());
									workflowDetails.merge();
									/**** Complete Task ****/
									String strTaskId = workflowDetails.getTaskId();
									Task task = processService.findTaskById(strTaskId);
									processService.completeTask(task);
								}
							}
							if (!sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
								supportingMembers.add(sm);
							}
						}
						motion.setSupportingMembers(supportingMembers);
					}
				}

				/**** Update Status(es) ****/
				Status newstatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT, motion.getLocale());
				motion.setStatus(newstatus);
				motion.setInternalStatus(newstatus);
				motion.setRecommendationStatus(newstatus);
				motion.setWorkflowStarted("NO");

				/**** Edited On,Edited By and Edited As is set ****/
				motion.setSubmissionDate(new Date());
				motion.setEditedOn(new Date());
				motion.setEditedBy(this.getCurrentUser().getActualUsername());
				String strUserGroupType = request.getParameter("usergroupType");
				if (strUserGroupType != null) {
					UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", strUserGroupType, motion.getLocale());
					motion.setEditedAs(userGroupType.getName());
				}
				/**** Bulk Submitted ****/
				motion.setBulkSubmitted(true);
				/**** Update the Motion object ****/
				motion = motion.merge();
				motions.add(motion);
			}

			model.addAttribute("motions", motions);
		}
		return "appropriationbillmotion/bulksubmissionack";
	}

	/**** Bulk Submission(Assistant)****/
	@RequestMapping(value="/bulksubmission/assistant/int",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantInt(
			final HttpServletRequest request, final Locale locale,
			final ModelMap model) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strMotionType = request.getParameter("appropriationBillMotionType");
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strItemsCount = request.getParameter("itemscount");
		String strFile = request.getParameter("file");
		/**** Locale ****/
		String strLocale = locale.toString();
		
		/**** Null and Empty Check ****/
		if (strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strMotionType != null && !(strMotionType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroup != null && !(strUsergroup.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())
				&& strFile != null && !(strFile.isEmpty())) {
			
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			DeviceType motionType = DeviceType.findById(DeviceType.class, Long.parseLong(strMotionType));
			/****
			 * Decision Status Available To Assistant(At this stage)
			 * MOTION_PUT_UP_OPTIONS_+MOTION_TYPE+HOUSE_TYPE+USERGROUP_TYPE
			 ****/
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class, "APPROPRIATIONBILLMOTION_PUT_UP_OPTIONS_" 
							+ motionType.getType().toUpperCase() + "_"
							+ houseType.getType().toUpperCase() + "_"
							+ strUsergroupType.toUpperCase(), "");
			List<Status> internalStatuses;
			try {
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale.toString());
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				return "appropriationbillmotion/bulksubmission";
			}

			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("motionType", strMotionType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("itemscount", strItemsCount);
			model.addAttribute("file", strFile);
		}
		return "appropriationbillmotion/bulksubmissionassistantint";
	}
	
	@RequestMapping(value="/bulksubmission/assistant/view",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantView(final HttpServletRequest request,final Locale locale,
			final Model model){	
		getBulkSubmissionMotions(model, request, locale.toString());
		return "appropriationbillmotion/bulksubmissionassistantview";		
	}
	
	@Transactional
	@RequestMapping(value="/bulksubmission/assistant/update",method=RequestMethod.POST)
	public String bulkSubmissionAssistant(final HttpServletRequest request,
			final Locale locale, final Model model) {
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus = request.getParameter("status");
		StringBuffer assistantProcessed = new StringBuffer();
		StringBuffer recommendAdmission = new StringBuffer();
		StringBuffer recommendRejection = new StringBuffer();
		if (selectedItems != null && selectedItems.length > 0
				&& strStatus != null && !strStatus.isEmpty()) {
			/**** As It Is Condition ****/
			if (strStatus.equals("-")) {
				for (String i : selectedItems) {
					Long id = Long.parseLong(i);
					AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class, id);
					if (!motion.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
						/**** Create Process ****/
						try {
							ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
							Map<String, String> properties = new HashMap<String, String>();
							String actor = motion.getActor();
							String[] temp = actor.split("#");
							UserGroupType usergroupType = null;
							WorkflowDetails workflowDetails = null;
							properties.put("pv_user", temp[0]);
							properties.put("pv_endflag", motion.getEndFlag());
							properties.put("pv_deviceId", String.valueOf(motion.getId()));
							properties.put("pv_deviceTypeId", String.valueOf(motion.getDeviceType().getId()));
							
							ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);
							/**** Create Workdetails Entry ****/
							Task task = processService.getCurrentTask(processInstance);
							if (motion.getEndFlag() != null
									&& !motion.getEndFlag().isEmpty()
									&& motion.getEndFlag().equals("continue")) {
								usergroupType = UserGroupType.findByType(temp[1], locale.toString());								
								

								/*
								 * Added by Amit Desai 2 Dec 2014
								 * START...
								 */
								// workflowDetails = WorkflowDetails.
								//		create(question,task,usergroupType, ApplicationConstants.APPROVAL_WORKFLOW, 
								//				question.getLevel());
								Workflow workflow = null;

								Status internalStatus = motion.getInternalStatus();
								String internalStatusType = internalStatus.getType();
								Status recommendationStatus = motion.getRecommendationStatus();
								String recommendationStatusType = recommendationStatus.getType();

//								if(recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING_POST_ADMISSION)
//										|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING_POST_ADMISSION)
//										|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_UNCLUBBING)
//										|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
//									workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
//								} 
//								else if(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING)
//										|| internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_NAME_CLUBBING)
//										|| (internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
//											&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
//										||(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
//											&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_RECEIVED))
//										||(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
//											&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
//										||(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
//											&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
//									workflow = Workflow.findByStatus(internalStatus, locale.toString());
//								}
//								else {
//									workflow = Workflow.findByStatus(internalStatus, locale.toString());
//								}
								workflow = Workflow.findByStatus(internalStatus, locale.toString());

								String workflowType = workflow.getType();
								String assigneeLevel = motion.getLevel();
								workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, assigneeLevel);
								/*
								 * Added by Amit Desai 2 Dec 2014
								 * ... END
								 */
								/**** Workflow Started ****/
								motion.setWorkflowDetailsId(workflowDetails.getId());
								motion.setWorkflowStarted("YES");
								motion.setWorkflowStartedOn(new Date());
								motion.setTaskReceivedOn(new Date());
								motion.simpleMerge();
								motion.setWorkflowDetailsId(workflowDetails.getId());
								/**** Workflow Started ****/
								motion.setWorkflowStarted("YES");
								motion.setWorkflowStartedOn(new Date());
								motion.setTaskReceivedOn(new Date());
								motion.simpleMerge();
							}
							if (motion.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_RECOMMEND_ADMISSION)) {
								recommendAdmission.append(motion.formatNumber() + ",");
							} else if (motion.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_RECOMMEND_REJECTION)) {
								recommendRejection.append(motion.formatNumber() + ",");
							}
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
						}
					} else {
						assistantProcessed.append(motion.formatNumber() + ",");
					}
				}
				model.addAttribute("assistantProcessed", assistantProcessed.toString());
			} else {
				Long statusId = Long.parseLong(strStatus);
				Status status = Status.findById(Status.class, statusId);
				for (String i : selectedItems) {
					Long id = Long.parseLong(i);
					AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class, id);
					String actor = request.getParameter("actor");
					String level = request.getParameter("level");
					if (actor != null && !actor.isEmpty() && level != null && !level.isEmpty()) {
						Reference reference;
						try {
							reference = UserGroup.findAppropriationBillMotionActor(motion, actor, level, locale.toString());

							if (reference != null && reference.getId() != null
									&& !reference.getId().isEmpty()
									&& reference.getName() != null
									&& !reference.getName().isEmpty()) {
								/**** Update Actor ****/
								String[] temp = reference.getId().split("#");
								motion.setActor(reference.getId());
								motion.setLocalizedActorName(temp[3] + "("+ temp[4] + ")");
								motion.setLevel(temp[2]);
								/****
								 * Update Internal Status and Recommendation
								 * Status
								 ****/
								motion.setInternalStatus(status);
								motion.setRecommendationStatus(status);
								motion.setEndFlag("continue");
								/**** Create Process ****/
								ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
								Map<String, String> properties = new HashMap<String, String>();
								properties.put("pv_user", temp[0]);
								properties.put("pv_endflag", motion.getEndFlag());
								properties.put("pv_deviceId", String.valueOf(motion.getId()));
								properties.put("pv_deviceTypeId", String.valueOf(motion.getDeviceType().getId()));
								ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);
								/**** Create Workdetails Entry ****/
								Task task = processService.getCurrentTask(processInstance);
								if (motion.getEndFlag() != null
										&& !motion.getEndFlag().isEmpty()
										&& motion.getEndFlag().equals("continue")) {
									UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
									/*
									 * Added by Amit Desai 2 Dec 2014
									 * START...
									 */
									 // WorkflowDetails workflowDetails = WorkflowDetails.create(question, 
									//		task, ApplicationConstants.APPROVAL_WORKFLOW, question.getLevel());
									Workflow workflow = null;

									Status internalStatus = motion.getInternalStatus();
									String internalStatusType = internalStatus.getType();
									Status recommendationStatus = motion.getRecommendationStatus();
									String recommendationStatusType = recommendationStatus.getType();

//									if(recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING_POST_ADMISSION)
//											|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING_POST_ADMISSION)
//											|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_UNCLUBBING)
//											|| recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
//										workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
//									} 
//									else if(internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLUBBING)
//											|| internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_NAME_CLUBBING)
//											|| (internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
//												&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
//											|| (internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
//												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))
//											|| (internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
//												&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
//											|| (internalStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
//												&& recommendationStatusType.equals(ApplicationConstants.APPROPRIATIONBILLMOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
//										workflow = Workflow.findByStatus(internalStatus, locale.toString());
//									} 
//									else {
//										workflow = Workflow.findByStatus(internalStatus, locale.toString());
//									}
									workflow = Workflow.findByStatus(internalStatus, locale.toString());

									String workflowType = workflow.getType();
									String assigneeLevel = motion.getLevel();
									WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, assigneeLevel); 
									//workflowDetails = WorkflowDetails.create(motion, task, workflowType, assigneeLevel);
									/*
									 * Added by Amit Desai 2 Dec 2014
									 * ... END
									 */
									
									motion.setWorkflowDetailsId(workflowDetails.getId());
									/**** Workflow Started ****/
									motion.setWorkflowStarted("YES");
									motion.setWorkflowStartedOn(new Date());
									motion.setTaskReceivedOn(new Date());
									motion.simpleMerge();
								}
								if (motion.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_RECOMMEND_ADMISSION)) {
									recommendAdmission.append(motion.formatNumber() + ",");
								} else if (motion.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_RECOMMEND_REJECTION)) {
									recommendRejection.append(motion.formatNumber() + ",");
								}
							}

						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
						}
					}
				}
			}
			model.addAttribute("recommendAdmission", recommendAdmission.toString());
			model.addAttribute("recommendRejection", recommendRejection.toString());
		}
		getBulkSubmissionMotions(model, request, locale.toString());
		return "appropriationbillmotion/bulksubmissionassistantview";
	}
	
	public void getBulkSubmissionMotions(final Model model,final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strMotionType=request.getParameter("appropriationBillMotionType");			
		String strStatus=request.getParameter("status");
		String strRole=request.getParameter("role");
		String strUsergroup=request.getParameter("usergroup");
		String strUsergroupType=request.getParameter("usergroupType");
		String strItemsCount=request.getParameter("itemscount");
		String strFile=request.getParameter("file");
		/**** Locale ****/
		String strLocale=locale;
		/**** Null and Empty Check ****/
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strMotionType!=null&&!(strMotionType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUsergroupType!=null&&!(strUsergroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strFile!=null&&!(strFile.isEmpty())){
			List<AppropriationBillMotion> motions=new ArrayList<AppropriationBillMotion>();
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
				DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
//				if(strFile!=null&&!strFile.isEmpty()&&!strFile.equals("-")){
//					Integer file=Integer.parseInt(strFile);
//					motions=AppropriationBillMotion.findAllByFile(session,motionType,file,strLocale);
//				}else 
				if(strItemsCount!=null&&!strItemsCount.isEmpty()){
					Integer itemsCount=Integer.parseInt(strItemsCount);
					Status internalStatus=Status.findById(Status.class,Long.parseLong(strStatus));
					motions=AppropriationBillMotion.findAllByStatus(session,motionType,internalStatus,itemsCount,strLocale);
				}				
				model.addAttribute("motions",motions);
				if(motions!=null&&!motions.isEmpty()){
					model.addAttribute("motionId",motions.get(0).getId());
				}
				
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
		}
	}
	
	/**** Used in bulk approval of supporting members to fetch motion details ****/
	@RequestMapping(value="/{id}/details",method=RequestMethod.GET)
	public String getDetails(@PathVariable("id")final Long id,
			final Model model){
		AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class, id);
		model.addAttribute("details",motion.getNoticeContent());
		return "appropriationbillmotion/details";
	}	
	
	private boolean isDateAdmitted(final AppropriationBillMotion appropriationBillMotion, final String locale){
		boolean retVal = true;
//		try{
//			Status dateAdmitted = Status.findByType(ApplicationConstants.APPROPRIATIONBILLMOTIONDATE_FINAL_DATE_ADMISSION, locale);
//			Status dateAdmissionProcessed = Status.findByType(ApplicationConstants.APPROPRIATIONBILLMOTIONDATE_PROCESSED_DATE_ADMISSION, locale);
//			AppropriationBillMotionDate appropriationBillMotionDate = AppropriationBillMotionDate.findAppropriationBillMotionDateSessionDeviceType(appropriationBillMotion.getSession(), appropriationBillMotion.getDeviceType(), locale);
//			if(appropriationBillMotionDate != null){
//				if(appropriationBillMotionDate.getStatus().getType().equals(dateAdmitted.getType())){
//					for(AppropriationBillMotionDepartmentDatePriority p : appropriationBillMotionDate.getDepartmentDates()){
//						if(appropriationBillMotion.getSubDepartment() != null){
//							if(p.getSubDepartment().getName().equals(appropriationBillMotion.getSubDepartment().getName())/* && p.getDepartment().getName().equals(appropriationBillMotion.getSubDepartment().getDepartment().getName())*/){
//								if(appropriationBillMotion.getSubmissionDate() != null){
//									if(appropriationBillMotion.getSubmissionDate().before(p.getSubmissionEndDate())){
//										retVal = true;
//										break;
//									}
//								}else{
//									if((new Date()).before(p.getSubmissionEndDate())){
//										retVal = true;
//										break;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}catch(Exception e){
//			logger.error("error", e);
//		}
		return retVal;
	}
	
	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {
		AppropriationBillMotion motion=AppropriationBillMotion.findById(AppropriationBillMotion.class, id);
		if(motion!=null){
			Status status=motion.getStatus();
			if(status.getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_INCOMPLETE)||status.getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE)){
				AppropriationBillMotion.supportingMemberWorkflowDeletion(motion);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	//=================UTILITY METHODS==============================
	public static DeviceType getDeviceType(final HttpServletRequest request,
			final String locale) throws ELSException {
		String deviceTypeType = request.getParameter("type");
		
		if(deviceTypeType == null || deviceTypeType.isEmpty()) {
			throw new ELSException("AppropriationBillMotionController.getDeviceType/2", "Device type is not set in the Request");
		}
		
		DeviceType deviceType = DeviceType.findByType(deviceTypeType, locale);
		return deviceType;
	}
	
	public static List<UserGroupType> delimitedStringToUGTList(final String delimitedUserGroups,
			final String delimiter,
			final String locale) {
		List<UserGroupType> userGroupTypes = new ArrayList<UserGroupType>();
		
		String[] strUserGroupTypes = delimitedUserGroups.split(delimiter);
		for(String strUserGroupType : strUserGroupTypes) {
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
			final DeviceType deviceType,
			final String locale) throws ELSException {		
		for(UserGroup ug : userGroups) {
			if(UserGroup.isActiveInSession(session,ug,locale)){
				for(UserGroupType ugt : userGroupTypes) {
					UserGroupType userGroupType = ug.getUserGroupType();
					if(ugt.getId().equals(userGroupType.getId())) {
						Map<String, String> parameters = UserGroup.findParametersByUserGroup(ug);
						String deviceTypesEnabled = parameters.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale);
						//String deviceTypesEnabled = ug.getParameterValue(ApplicationConstants.DEVICETYPE_KEY);
						if(deviceTypesEnabled!=null && !deviceTypesEnabled.isEmpty()) {
							for(String deviceTypeEnabled: deviceTypesEnabled.split("##")) {
								if(deviceTypeEnabled!=null && deviceTypeEnabled.trim().equals(deviceType.getName().trim())) {
									return ug;
								}
							}
						}												
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
}
