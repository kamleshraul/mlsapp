package org.mkcl.els.controller.mois;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import org.mkcl.els.common.vo.BulkApprovalVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.controller.mis.MemberOtherController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionDateDevice;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberSupportingMember;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.Query;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("motion")
public class MotionController extends GenericController<Motion>{

	@Autowired
	private IProcessService processService;

	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {
		/**** Selected Motion Type ****/
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Available Motion Types ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			try {
				deviceTypes = DeviceType.findDeviceTypesStartingWith("motions", locale);

				model.addAttribute("motionTypes", deviceTypes);
				/**** Default Value ****/
				model.addAttribute("motionType", deviceType.getId());
				/**** Access Control Based on Motion Type ****/
				model.addAttribute("motionTypeType", deviceType.getType());

				model.addAttribute("moduleLocale", locale);
				
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

				/**** added by sandeep singh(jan 27 2013) ****/
				/****
				 * Custom Parameter To Determine The Usergroup and usergrouptype
				 * of mois users . here we are determining what status will be
				 * shown to a particular user.
				 ****/
				List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
				String userGroupType = null;
				if (userGroups != null) {
					if (!userGroups.isEmpty()) {
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"MOIS_ALLOWED_USERGROUPTYPES", "");
						if (customParameter != null) {
							String allowedUserGroups = customParameter.getValue();
							for (UserGroup i : userGroups) {
								if(i.getActiveTo().compareTo(new Date()) > 0){
									if (allowedUserGroups.contains(i.getUserGroupType().getType())) {
																			
										/****
										 * Authenticated User's usergroup and
										 * usergroupType
										 ****/
										model.addAttribute("usergroup", i.getId());
										userGroupType = i.getUserGroupType().getType();
										model.addAttribute("usergroupType",userGroupType);
										
										Map<String, String> parameters = UserGroup.findParametersByUserGroup(i);
										/**** Sub department Filter Starts ****/
										CustomParameter subDepartmentFilterAllowedFor = CustomParameter
												.findByName(CustomParameter.class, "MOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
										if (subDepartmentFilterAllowedFor != null){
											
											model.addAttribute("usersAllowedForDepartmentFilter", subDepartmentFilterAllowedFor.getValue());
										
											if(subDepartmentFilterAllowedFor.getValue().contains(userGroupType)) {
										
												if (parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale) != null
														&& !parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale).equals(" ")) {
													String strSubDepartments = parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale);
													String subDepartments[] = strSubDepartments.split("##");
													List<SubDepartment> subDepts = new ArrayList<SubDepartment>();
													for (int j = 0; j < subDepartments.length; j++) {
														SubDepartment subDepartment = SubDepartment.findByName(SubDepartment.class, subDepartments[j], locale);
														subDepts.add(subDepartment);
													}
													model.addAttribute("subDepartments", subDepts);
												}
											}
										}
										/**** Sub department Filter Ends ****/
										/**** Question Status Allowed ****/
										CustomParameter allowedStatus = CustomParameter.findByName(CustomParameter.class,
														"MOTION_GRID_STATUS_ALLOWED_"+ userGroupType.toUpperCase(),"");
										List<Status> status = new ArrayList<Status>();
										if (allowedStatus != null) {
											status = Status.findStatusContainedIn(allowedStatus.getValue(),locale);
										} else {
											CustomParameter defaultAllowedStatus = CustomParameter.findByName(CustomParameter.class,
															"MOTION_GRID_STATUS_ALLOWED_BY_DEFAULT","");
											if (defaultAllowedStatus != null) {
												status = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
											} else {
												model.addAttribute("errorcode","motion_status_allowed_by_default_not_set");
											}
										}
										model.addAttribute("status", status);
										break;
									}
								}
							}
						} else {
							model.addAttribute("errorcode","mois_allowed_usergroups_notset");
						}
					} else {
						model.addAttribute("errorcode","current_user_has_no_usergroups");
					}
				} else {
					model.addAttribute("errorcode","current_user_has_no_usergroups");
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
					} else if (i.getType().startsWith("MOIS_CLERK")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().startsWith("MOIS_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					}
				}
				/**** File Options(Obtain Dynamically) ****/
				if (userGroupType != null && !userGroupType.isEmpty()
						&& userGroupType.equals("assistant")) {
					int highestFileNo = Motion.findHighestFileNo(lastSessionCreated, deviceType, locale);
					model.addAttribute("highestFileNo", highestFileNo);
				}
				
				
				
			} catch (ELSException e) {
				model.addAttribute("MotionController", e.getParameter());
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
		
		CustomParameter assistantGridAllowedFor = CustomParameter.findByName(
				CustomParameter.class, "MOIS_ASSISTANTGRID_ALLOWED_FOR", "");
		
		CustomParameter memberGridAllowedFor = CustomParameter.findByName(
				CustomParameter.class, "MOIS_MEMBERGRID_ALLOWED_FOR", "");
		
		CustomParameter typistGridAllowedFor = CustomParameter.findByName(
				CustomParameter.class, "MOIS_TYPISTGRID_ALLOWED_FOR", "");
		
		if (memberGridAllowedFor != null && role != null && !role.isEmpty()
				&& houseType != null && !houseType.isEmpty()
				&& memberGridAllowedFor.getValue().contains(role)) {
			
			newUrlPattern = urlPattern + "?usergroup=member&houseType=" + houseType;
			String selectedStatusId = request.getParameter("status");
		    if(selectedStatusId!=null && !selectedStatusId.isEmpty()) {
		    	Status selectedStatus = Status.findById(Status.class, Long.parseLong(selectedStatusId));
			    if(selectedStatus!=null && selectedStatus.getType().equals(ApplicationConstants.MOTION_COMPLETE)) {
			    	newUrlPattern=urlPattern+"_readyToSubmit?usergroup=member&houseType="+houseType;
			    }
		    }
			
		} else if (typistGridAllowedFor != null && role != null
				&& !role.isEmpty() && houseType != null && !houseType.isEmpty()
				&& typistGridAllowedFor.getValue().contains(role)) {
			
			newUrlPattern = urlPattern + "?usergroup=typist&houseType=" + houseType;
			
		} else if (assistantGridAllowedFor != null && role != null
				&& !role.isEmpty() && houseType != null && !houseType.isEmpty()
				&& assistantGridAllowedFor.getValue().contains(role)) {
							
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
		CustomParameter newOperationAllowedTo = CustomParameter.findByName(CustomParameter.class,"MOIS_NEW_OPERATION_ALLOWED_TO","");
		if(newOperationAllowedTo != null && role != null 
				&& !role.isEmpty() 
				&& newOperationAllowedTo.getValue().contains(role)){
			return servletPath;			
		}		
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
		/**** New Operations Allowed For Starts ****/
	}

	@Override
	protected void populateNew(final ModelMap model, Motion domain, final String locale,
			final HttpServletRequest request) {

		/**** Locale ****/
		domain.setLocale(locale);

		/**** Subject and Details ****/
		String subject=request.getParameter("subject");
		if(subject!=null){
			domain.setSubject(subject);
		}
		String details=request.getParameter("details");
		if(details!=null){
			domain.setDetails(details);
		}	

		/**** House Type ****/
		String selectedHouseType=request.getParameter("houseType");
		HouseType houseType=null;
		if(selectedHouseType!=null){
			if(!selectedHouseType.isEmpty()){
				//**** Modified By Sandeep Singh ****/
				/*In case of a validation exception selectedHouseType is id ****/
				try {
					Long houseTypeId=Long.parseLong(selectedHouseType);
					houseType=HouseType.findById(HouseType.class,houseTypeId);
				} catch (NumberFormatException e) {
					houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
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

		/**** Motion Type ****/
		String selectedMotionType=request.getParameter("motionType");
		if(selectedMotionType==null){
			selectedMotionType=request.getParameter("type");
		}
		DeviceType motionType=null;
		if(selectedMotionType!=null){
			if(!selectedMotionType.isEmpty()){
				motionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedMotionType));
				model.addAttribute("formattedMotionType", motionType.getName());
				model.addAttribute("motionType", motionType.getId());
				model.addAttribute("deviceType", motionType.getId());
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
		
		/** For Upper house members motion_calling_attention max text length need to be checked*/
		
		populateMaxLengthParameters(houseType,role,motionType,model);
		
		
		/**** usergroup and usergroupType ****/
		String usergroupType=request.getParameter("usergroupType");
		if(usergroupType!=null){
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType=(String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		String usergroup=request.getParameter("usergroup");
		if(usergroup!=null){
			model.addAttribute("usergroup",usergroup);
		}else{
			usergroup=(String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",usergroup);
			request.getSession().removeAttribute("usergroup");
		}				
		/**** Session,Primary Member,Supporting Members,Constituency,Ministries,Sub-Departments****/
		Session selectedSession=null;
		String memberNames=null;
		String primaryMemberName=null;
		if(houseType!=null&&selectedYear!=null&&sessionType!=null){
			try {
				selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				if(selectedSession!=null){
					/**** Session ****/
					model.addAttribute("session",selectedSession.getId());
					if(role.startsWith("MEMBER")){
						/**** Primary Member ****/
						Member member=Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale);
						if(member!=null){
							model.addAttribute("primaryMember",member.getId());
							primaryMemberName=member.getFullname();
							memberNames=primaryMemberName;
							model.addAttribute("formattedPrimaryMember",primaryMemberName);
						}else{
							logger.error("**** Authenticated user is not a member ****");
							model.addAttribute("errorcode","member_isnull");
						}
						/**** Supporting Members ****/
						List<SupportingMember> selectedSupportingMembers=domain.getSupportingMembers();
						List<Member> supportingMembers=new ArrayList<Member>();
						if(selectedSupportingMembers!=null){
							if(!selectedSupportingMembers.isEmpty()){
								StringBuffer bufferFirstNamesFirst=new StringBuffer();
								for(SupportingMember i:selectedSupportingMembers){
									Member m=i.getMember();
									bufferFirstNamesFirst.append(m.getFullname()+",");
									supportingMembers.add(m);
								}
								bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
								model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
								model.addAttribute("supportingMembers",supportingMembers);
								memberNames=primaryMemberName+","+bufferFirstNamesFirst.toString();
								model.addAttribute("memberNames",memberNames);
							}else{
								model.addAttribute("memberNames",memberNames);
							}
						}else{
							model.addAttribute("memberNames",memberNames);
						}
						
						/*** Populate Saved Supporting member***/
						//Populate Supporting Member Names
//						Member member = Member.findMember(authUser.getFirstName(), authUser.getMiddleName(),
//								authUser.getLastName(), authUser.getBirthDate(), locale);
						String supportingMemberNames = MemberOtherController.getDelimitedMemberSupportingMembers(motionType, member, selectedSession, locale, usergroupType);
						model.addAttribute("supportingMembersName", supportingMemberNames);
						
						//Populate Supporting Members 
						List<MemberSupportingMember> suppMembers = MemberSupportingMember.getMemberSupportingMemberRepository().findMemberSupportingMember(motionType, member, selectedSession, locale);

						List<Member> supportingMembers1 = new ArrayList<Member>();
						for(MemberSupportingMember sm : suppMembers){
					
								Member supportingMember = sm.getSupportingMember();
								if(supportingMember.isActiveMemberOn(new Date(), locale)){
									supportingMembers1.add(supportingMember);
								}
						
							
						}
						model.addAttribute("supportingMembers", supportingMembers1);
						model.addAttribute("savedMemberSupportingMembers", supportingMembers1);

						//Populate PrimaryMemberName + supportingMemberNames
						String memberNames1 = member.getFullname() + "," + supportingMemberNames;
						model.addAttribute("memberNames",memberNames1);
						
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
						//Submission Priority
						model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
						int currentReadyToSubmitCount = Motion.findReadyToSubmitCount(selectedSession, member, motionType, locale);
						model.addAttribute("submissionPriorityMaximum", currentReadyToSubmitCount+1);
						model.addAttribute("formater", new FormaterUtil());
						model.addAttribute("locale", locale);
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
									List<SubDepartment> subDepartments=MemberMinister.
											findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
									model.addAttribute("subDepartments",subDepartments);
									SubDepartment subDepartment=domain.getSubDepartment();
									if(subDepartment!=null){
										model.addAttribute("subDepartmentSelected",subDepartment.getId());
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
			final HttpServletRequest request, final ModelMap model, final String locale) {
		
		/**** Edit Page Starts ****/
		String edit = request.getParameter("edit");
		if(edit != null){
			if(!Boolean.parseBoolean(edit)){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}
		
		/**** for printing ****/
		String editPrint = request.getParameter("editPrint");
		if(editPrint != null){
			if(Boolean.parseBoolean(editPrint)){
				return newUrlPattern.replace("edit","editprint");
			}
		}
		
		CustomParameter editPage = CustomParameter.findByName(CustomParameter.class, "MOIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.findByName(CustomParameter.class, "MOIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return newUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return newUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("MOIS_")) {
				return newUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "motion/error";
		/**** Edit Page Ends ****/
	}

	@Override
	protected void populateEdit(final ModelMap model, Motion domain,
			final HttpServletRequest request) {
		
		List<DeviceType> allDevices = DeviceType.findAll(DeviceType.class, "priority", ApplicationConstants.ASC, domain.getLocale());
		model.addAttribute("allDevices", allDevices);
		
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit",request.getParameter("bulkedit"));

		model.addAttribute("olevel", domain.getLevel());
		/**** Locale ****/
		String locale=domain.getLocale();		

		/**** House Type ****/
		HouseType houseType=domain.getHouseType();
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseType",houseType.getId());

		/**** Session ****/
		Session selectedSession=domain.getSession();
		model.addAttribute("session",selectedSession.getId());

		/**** Session Year ****/
		Integer sessionYear=0;
		sessionYear=selectedSession.getYear();
		model.addAttribute("formattedSessionYear",FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear",sessionYear);

		/**** Session Type ****/
		SessionType  sessionType=selectedSession.getType();
		model.addAttribute("formattedSessionType",sessionType.getSessionType());
		model.addAttribute("sessionType",sessionType.getId());        

		/**** Motion Type ****/
		DeviceType motionType=domain.getType();
		model.addAttribute("formattedMotionType",motionType.getName());
		model.addAttribute("motionType",motionType.getId());
		model.addAttribute("deviceType", motionType.getId());
		model.addAttribute("selectedMotionType",motionType.getType());	

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
		/**** role ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		
		populateMaxLengthParameters(houseType,role,motionType,model);
		/**** usergroup and usergroupType ****/
		String usergroupType=request.getParameter("usergroupType");
		if(usergroupType!=null){
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType=(String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		String strUsergroup=request.getParameter("usergroup");
		if(strUsergroup!=null){
			model.addAttribute("usergroup",strUsergroup);
		}else{
			strUsergroup=(String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",strUsergroup);
			request.getSession().removeAttribute("userGroup");
		}
		/**** Primary Member ****/
		String memberNames=null;
		String primaryMemberName=null;
		Member member=domain.getPrimaryMember();
		if(member!=null){
			model.addAttribute("primaryMember",member.getId());
			primaryMemberName=member.getFullname();
			memberNames=primaryMemberName;
			model.addAttribute("formattedPrimaryMember",primaryMemberName);
		}
		/**** Constituency ****/
		Long houseId=selectedSession.getHouse().getId();
		MasterVO constituency=null;
		if(houseType.getType().equals("lowerhouse")){
			constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
			model.addAttribute("constituency",constituency.getName());
		}else if(houseType.getType().equals("upperhouse")){
			Date currentDate=new Date();
			String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
			constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
			model.addAttribute("constituency",constituency.getName());
		}
		/**** Supporting Members ****/
		List<SupportingMember> selectedSupportingMembers=domain.getSupportingMembers();
		List<Member> supportingMembers=new ArrayList<Member>();
		if(selectedSupportingMembers!=null){
			if(!selectedSupportingMembers.isEmpty()){
				StringBuffer bufferFirstNamesFirst=new StringBuffer();
				for(SupportingMember i:selectedSupportingMembers){
					/**** All Supporting Members Are Preserved.But the names that appear in supporting 
					 * members list will vary. ****/
					Member m=i.getMember();
					supportingMembers.add(m);
					if(usergroupType.equals("clerk") || usergroupType.equals("assistant")){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if((usergroupType.equals("member"))
							&&domain.getInternalStatus()!=null
							&&domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SUBMIT)
							&&i.getDecisionStatus()!=null
							&&i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if((usergroupType.equals("member"))
							&&domain.getInternalStatus()!=null
							&&(domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_INCOMPLETE)
									||domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_COMPLETE))){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if(usergroupType.equals("member")
							&&domain.getInternalStatus()==null){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if(!(usergroupType.equals("member"))
							&&i.getDecisionStatus()!=null
							&&i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}
				}
				model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
				model.addAttribute("supportingMembers",supportingMembers);
				model.addAttribute("proxy", supportingMembers.get(0).getFullname());
				memberNames=primaryMemberName+","+bufferFirstNamesFirst.toString();
				model.addAttribute("memberNames",memberNames);
			}else{
				model.addAttribute("memberNames",memberNames);
			}
		}else{
			model.addAttribute("memberNames",memberNames);
		}
		/**** Ministries And Sub Departments ****/
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
						List<SubDepartment> subDepartments=MemberMinister.
								findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
						model.addAttribute("subDepartments",subDepartments);
						SubDepartment subDepartment=domain.getSubDepartment();
						if(subDepartment!=null){
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
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){            
			if(domain.getSubmissionDate()!=null){
				model.addAttribute("submissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(domain.getCreationDate()!=null){
				model.addAttribute("creationDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
			}
			if(domain.getWorkflowStartedOn()!=null){
				model.addAttribute("workflowStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOn()));
			}
			if(domain.getTaskReceivedOn()!=null){
				model.addAttribute("taskReceivedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOn()));
			}
		}
		/**** Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		if(domain.getPostBallotNumber() != null){
			model.addAttribute("formattedPostBallotNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getPostBallotNumber()));
		}
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());
		model.addAttribute("dataEnteredBy",domain.getDataEnteredBy());
		//populate member status name
		if(usergroupType !=null && !(usergroupType.isEmpty()) && usergroupType.equals("member")){
			Status memberStatus = domain.findMemberStatus();
			if(memberStatus!=null){				
				model.addAttribute("formattedMemberStatus", memberStatus.getName());
			}
			//Submission Priority
			model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
			int currentReadyToSubmitCount = Motion.findReadyToSubmitCount(domain.getSession(), domain.getPrimaryMember(), domain.getType(), domain.getLocale());
			model.addAttribute("submissionPriorityMaximum", currentReadyToSubmitCount+1);
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", domain.getLocale());
		}
		/**** Status,Internal Status and recommendation Status ****/
		Status status=domain.getStatus();
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		if(status!=null){
			model.addAttribute("status",status.getId());
			model.addAttribute("memberStatusType",status.getType());
		}
		if(internalStatus!=null){
			model.addAttribute("internalStatus",internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			/**** list of put up options available ****/
			/**** added by sandeep singh(jan 28 2013) ****/
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("assistant")){
				populateInternalStatus(model,domain,usergroupType,locale);
				if(domain.getWorkflowStarted()==null){
					domain.setWorkflowStarted("NO");
				}else if(domain.getWorkflowStarted().isEmpty()){
					domain.setWorkflowStarted("NO");
				}
				if(domain.getEndFlag()==null){
					domain.setEndFlag("continue");
				}else if(domain.getEndFlag().isEmpty()){
					domain.setEndFlag("continue");
				}
				if(domain.getLevel()==null){
					domain.setLevel("1");
				}else if(domain.getLevel().isEmpty()){
					domain.setLevel("1");
				}

			}
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
		}	
		/** Populate whether admitted motion is discussed or not  **/
		if(internalStatus!=null && internalStatus.getType().trim().equalsIgnoreCase(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			if(recommendationStatus!=null && recommendationStatus.getType().trim().equalsIgnoreCase(ApplicationConstants.MOTION_PROCESSED_DISCUSSED)) {
				model.addAttribute("discussionStatus",recommendationStatus.getName());
			} else if(recommendationStatus!=null && recommendationStatus.getType().trim().equalsIgnoreCase(ApplicationConstants.MOTION_PROCESSED_UNDISCUSSED)) {
				model.addAttribute("discussionStatus",recommendationStatus.getName());
			} else {
				//if there comes status which is post discussed, then need to check discussed on drafts here..
				model.addAttribute("discussionStatus","");
			}
			/** populate discussion details text if question is discussed **/
			String discussionDetailsText = domain.findDiscussionDetailsText();
			model.addAttribute("discussionDetailsText", discussionDetailsText);
		}
		
		/**** Referenced Entities are collected in refentities****/		
		List<ReferenceUnit> referencedEntities=domain.getReferencedUnits();
		if(referencedEntities!=null&&!referencedEntities.isEmpty()){
			List<ReferenceUnit> refmotionentities=new ArrayList<ReferenceUnit>();
			List<ReferenceUnit> refquestionentities=new ArrayList<ReferenceUnit>();
			List<ReferenceUnit> refresolutionentities=new ArrayList<ReferenceUnit>();
			for(ReferenceUnit re : referencedEntities){
				if(re.getDeviceType() != null){
					if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_MOTIONS)){
						refmotionentities.add(re);
					}else if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						refresolutionentities.add(re);
					}else if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){	
						refquestionentities.add(re);
					}
				}
			}
			model.addAttribute("referencedMotions",refmotionentities);
			model.addAttribute("referencedQuestions",refquestionentities);
			model.addAttribute("referencedResolutions",refresolutionentities);
			model.addAttribute("referencedEntities",referencedEntities);
		}	
		
		/**** Clubbed motions are collected in references ****/
		List<ClubbedEntity> clubbedEntities=Motion.findClubbedEntitiesByPosition(domain);
		if(clubbedEntities!=null&&!clubbedEntities.isEmpty()){
			List<Reference> references=new ArrayList<Reference>();
			StringBuffer buffer1=new StringBuffer();
			buffer1.append(memberNames+",");
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getMotion().getNumber()));
				reference.setNumber(String.valueOf(ce.getMotion().getId()));
				references.add(reference);
				String tempPrimary=ce.getMotion().getPrimaryMember().getFullname();
				if(!buffer1.toString().contains(tempPrimary)){
					buffer1.append(ce.getMotion().getPrimaryMember().getFullname()+",");
				}
				List<SupportingMember> clubbedSupportingMember=ce.getMotion().getSupportingMembers();
				if(clubbedSupportingMember!=null){
					if(!clubbedSupportingMember.isEmpty()){
						for(SupportingMember l:clubbedSupportingMember){
							String tempSupporting=l.getMember().getFullname();
							if(!buffer1.toString().contains(tempSupporting)){
								buffer1.append(tempSupporting+",");
							}
						}
					}
				}
			}
			if(!buffer1.toString().isEmpty()){
				buffer1.deleteCharAt(buffer1.length()-1);
			}
			String allMembersNames=buffer1.toString();
			model.addAttribute("memberNames",allMembersNames);
			if(!references.isEmpty()){
				model.addAttribute("clubbedEntities",references);
			}else{
				if(domain.getParent()!=null){
					model.addAttribute("formattedParentNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
					model.addAttribute("parent",domain.getParent().getId());
				}
			}
		}	
		if(domain.getParent()!=null){
			model.addAttribute("formattedParentNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
			model.addAttribute("parent",domain.getParent().getId());
		}
		/**** Populating Put up otions and Actors ****/
		if(domain.getInternalStatus()!=null){
			String internalStatusType=domain.getInternalStatus().getType();			
			if(usergroupType!=null&&!usergroupType.isEmpty()&&usergroupType.equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION)
							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_REJECTION)
					)){
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUsergroup));
				List<Reference> actors=WorkflowConfig.findMotionActorsVO(domain, internalStatus, userGroup, 1, locale);
				model.addAttribute("internalStatusSelected",internalStatus.getId());
				model.addAttribute("actors",actors);
				if(actors!=null&&!actors.isEmpty()){
					String nextActor=actors.get(0).getId();
					String[] actorArr=nextActor.split("#");
					domain.setLevel(actorArr[2]);
					domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
				}
			}	
		}
	}

	private void populateInternalStatus(final ModelMap model,final Motion domain,final String usergroupType,final String locale) {
		try {
			List<Status> internalStatuses=new ArrayList<Status>();
			DeviceType deviceType=domain.getType();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseType=domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"MOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				}
			}/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			}/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			}	
			/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
			}	
			/**** Internal Status****/
			model.addAttribute("internalStatuses",internalStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}		
	}

	private void populateSupportingMembers(final Motion domain,final HttpServletRequest request){
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
			Motion motion=Motion.findById(Motion.class,domain.getId());
			members=motion.getSupportingMembers();
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
					
					String locale = domain.getLocale();
					if(locale == null){
						locale = ApplicationLocale.findDefaultLocale();
					}
					
					CustomParameter csptAllowedForAutoApproval = CustomParameter.findByName(CustomParameter.class, "MOIS_SUPPORT_MEMBERAUTO_APPROVAL_ALLOWED", "");
					Set<Role> roles = this.getCurrentUser().getRoles();
					Role role = null;
					for(Role r : roles){
						if(r != null){
							Credential cr = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), "");
							UserGroup ug = UserGroup.findActive(cr, new Date(), locale);
							if(ug != null){
								role = r;
								break;
							}
						}
					}
					if(csptAllowedForAutoApproval != null && csptAllowedForAutoApproval.getValue() != null 
							&& !csptAllowedForAutoApproval.getValue().isEmpty() 
							&& csptAllowedForAutoApproval.getValue().contains(role.getType())){
						
						if(supportingMember==null){
							supportingMember=new SupportingMember();
							supportingMember.setMember(member);
							supportingMember.setLocale(domain.getLocale());							
							supportingMember.setDecisionStatus(approvedStatus);
							supportingMember.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
							supportingMember.setApprovedSubject(domain.getSubject());
							supportingMember.setApprovedText(domain.getDetails());
						}
						
					}else{
						/**** New Supporting Member ****/
						if(supportingMember==null){
							supportingMember=new SupportingMember();
							supportingMember.setMember(member);
							supportingMember.setLocale(domain.getLocale());
							if(dataEntryType!=null&&!(dataEntryType.isEmpty())){
								supportingMember.setDecisionStatus(approvedStatus);
								supportingMember.setApprovalType("OFFLINE");
								supportingMember.setApprovedSubject(domain.getSubject());
								supportingMember.setApprovedText(domain.getDetails());						
							}else{
								supportingMember.setDecisionStatus(notsendStatus);
								supportingMember.setApprovalType("ONLINE");
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

	@Override
	protected void customValidateCreate(final Motion domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);
		
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_OPTIONAL_FIELDS_IN_VALIDATION, "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}
		
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){/**** Approval ****/
					/**** Approval ****/
					if(domain.getHouseType()==null){
						result.rejectValue("houseType","HousetypeEmpty");
					}
					if(domain.getType()==null){
						result.rejectValue("type","MotionTypeEmpty");
					}
					if(domain.getSession()==null){
						result.rejectValue("session","SessionEmpty");
					}
					
					if(optionalFields != null && !optionalFields.contains("primaryMember")){
						if(domain.getPrimaryMember()==null){
							result.rejectValue("primaryMember","PrimaryMemberEmpty");
						}
					}
					
					if(optionalFields != null && !optionalFields.contains("subject")){
						if(domain.getSubject().isEmpty()){
							result.rejectValue("subject","SubjectEmpty");
						}
					}
					
					if(optionalFields != null && !optionalFields.contains("details")){
						if(domain.getDetails().isEmpty()){
							result.rejectValue("details","DetailsEmpty");
						}	
					}
					
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");						
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
						}
					}
					
				}else /**** Submission ****/
					if(operation.equals("submit")){
						String role = request.getParameter("role");
						CustomParameter csptAutoGenForTypist = CustomParameter
								.findByName(CustomParameter.class, domain.getType()
										.getType().toUpperCase() + "_TYPIST_AUTO_GEN", "");
						if(csptAutoGenForTypist != null
								&& csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
							if(role.equals("MOIS_TYPIST")){
								if(domain.getNumber() == null){
									result.rejectValue("number", "NumberEmpty");
									//check for duplicate questions
								}
								Boolean flag = Motion.isExist(domain.getNumber(), domain.getType(), domain.getSession(), domain.getLocale());
								
								if(flag){
									result.rejectValue("number", "NonUnique","Duplicate Parameter");
								}
							}
						}
						/**** Submission ****/
						if(domain.getHouseType()==null){
							result.rejectValue("houseType","HousetypeEmpty");
						}
						if(domain.getType()==null){
							result.rejectValue("type","MotionTypeEmpty");
						}
						if(domain.getSession()==null){
							result.rejectValue("session","SessionEmpty");
						}
						
						//submission date limit validations (configurable through custom parameters)
						if(domain.getSession()!=null && domain.getType()!=null) {						
							CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
							if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
								String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
								if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
									String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
									for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
										if(dt.trim().equals(domain.getType().getType().trim())) {
											if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
												Integer batch = Motion.findBatch(domain, new Date());
												String strSubDate = null;
												
												if(batch.equals(1)){
													if(!Motion.allowedInFirstBatch(domain, new Date())){
														strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "firstBatchStartTime");
														result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Motion cannot be submitted before " + strSubDate);
													}
												}
												
												if(batch.equals(2)){
													if(!Motion.allowedInSecondBatch(domain, new Date())){
														strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "secondBatchStartTime");
														result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Motion cannot be submitted before " + strSubDate);
													}
												}
												
												if(batch.equals(0)){
													result.rejectValue("version","SubmissionNotAllowed_batch","Motion cannot be submitted.");													
												}
											}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){												
												
												if(!Motion.isAllowedForSubmission(domain, new Date(), "")){
													String strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_submissionStartTime");
													result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Motion cannot be submitted before " + strSubDate);
												}
											}
											break;
										}
									}								
								}
							}
						}						
						
						if(optionalFields != null && !optionalFields.contains("primaryMember")){
							if(domain.getPrimaryMember()==null){
								result.rejectValue("primaryMember","PrimaryMemberEmpty");
							}
						}
						
						if(optionalFields != null && !optionalFields.contains("subject")){
							if(domain.getSubject().isEmpty()){
								result.rejectValue("subject","SubjectEmpty");
							}
						}
						
						if(optionalFields != null && !optionalFields.contains("details")){
							if(domain.getDetails().isEmpty()){
								result.rejectValue("details","DetailsEmpty");
							}	
						}
						
						if(optionalFields != null && !optionalFields.contains("ministry")){
							if(domain.getMinistry()==null){
								result.rejectValue("ministry","MinistryEmpty");
							}		
						}					
					}
			}
		}/**** Drafts ****/
		else{
			if(domain.getHouseType()==null){
				result.rejectValue("houseType","HousetypeEmpty");
			}
			if(domain.getType()==null){
				result.rejectValue("type","MotionTypeEmpty");
			}
			if(domain.getSession()==null){
				result.rejectValue("session","SessionEmpty");
			}
			
			if(optionalFields != null && !optionalFields.contains("primaryMember")){
				if(domain.getPrimaryMember()==null){
					result.rejectValue("primaryMember","PrimaryMemberEmpty");
				}
			}
			
			if(optionalFields != null && !optionalFields.contains("subject")){
				if(domain.getSubject().isEmpty()){
					result.rejectValue("subject","SubjectEmpty");
				}
			}
			
			if(optionalFields != null && !optionalFields.contains("details")){
				if(domain.getDetails().isEmpty()){
					result.rejectValue("details","DetailsEmpty");
				}	
			}
		}
	}

	@Override
	protected void customValidateUpdate(final Motion domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);
		
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_OPTIONAL_FIELDS_IN_VALIDATION, "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}
		
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}
		String operation=request.getParameter("operation");
		if(operation!=null&&!operation.isEmpty()){
			/****Supporting Member Approval By Member ****/
			if(operation.equals("approval")){/**** Approval ****/
				if(domain.getHouseType()==null){
					result.rejectValue("houseType","HousetypeEmpty");
				}
				if(domain.getType()==null){
					result.rejectValue("type","MotionTypeEmpty");
				}
				if(domain.getSession()==null){
					result.rejectValue("session","SessionEmpty");
				}

				if(optionalFields != null && !optionalFields.contains("primaryMember")){
					if(domain.getPrimaryMember()==null){
						result.rejectValue("primaryMember","PrimaryMemberEmpty");
					}
				}
				
				if(optionalFields != null && !optionalFields.contains("subject")){
					if(domain.getSubject().isEmpty()){
						result.rejectValue("subject","SubjectEmpty");
					}
				}
				
				if(optionalFields != null && !optionalFields.contains("details")){
					if(domain.getDetails().isEmpty()){
						result.rejectValue("details","DetailsEmpty");
					}	
				}
				
				if(optionalFields != null && !optionalFields.contains("ministry")){
					if(domain.getMinistry()==null){
						result.rejectValue("ministry","MinistryEmpty");
					}		
				}	
				
				if(domain.getSupportingMembers()==null){
					result.rejectValue("supportingMembers","SupportingMembersEmpty");
				} else if(domain.getSupportingMembers().isEmpty()){
					result.rejectValue("supportingMembers","SupportingMembersEmpty");						
				} else {
					int count=0;
					for(SupportingMember i:domain.getSupportingMembers()){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							count++;
						}
					}
					if(count==0){
						result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
					}
				}
			}else /**** Submission By Member/Clerk****/
				if(operation.equals("submit")){
					
					String role = request.getParameter("role");
					
					CustomParameter csptAutoGenForTypist = CustomParameter.findByName(CustomParameter.class, domain.getType()
									.getType().toUpperCase() + "_TYPIST_AUTO_GEN", "");
					if(csptAutoGenForTypist != null
							&& csptAutoGenForTypist.getValue().equalsIgnoreCase("no")){
						if(role.equals("MOIS_TYPIST")){
							//Empty check for number
							if(domain.getNumber() == null){
								result.rejectValue("number", "NumberEmpty");
							}
							// Check duplicate entry for question Number
							Boolean flag = Motion.
									isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
							Motion motion = Motion.findById(Motion.class, domain.getId());
							if(!motion.getNumber().equals(domain.getNumber())){
								if(flag){
									result.rejectValue("number", "NonUnique","Duplicate Parameter");
								}
							}
						}
					}
					/**** Submission ****/
					if(domain.getHouseType()==null){
						result.rejectValue("houseType","HousetypeEmpty");
					}
					if(domain.getType()==null){
						result.rejectValue("type","MotionTypeEmpty");
					}
					if(domain.getSession()==null){
						result.rejectValue("session","SessionEmpty");
					}
					
					//submission date limit validations (configurable through custom parameters)
					if(domain.getSession()!=null && domain.getType()!=null) {
						CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
						if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
							String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
							if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
								String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
								for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
									if(dt.trim().equals(domain.getType().getType().trim())) {										
										if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
											Integer batch = Motion.findBatch(domain, new Date());
											String strSubDate = null;
											
											if(batch.equals(1)){
												if(!Motion.allowedInFirstBatch(domain, new Date())){
													strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "firstBatchStartTime");
													result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Motion cannot be submitted before " + strSubDate);
												}
											}
											
											if(batch.equals(2)){
												if(!Motion.allowedInSecondBatch(domain, new Date())){
													strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_" + "secondBatchStartTime");
													result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Motion cannot be submitted before " + strSubDate);
												}
											}
											
											if(batch.equals(0)){
												result.rejectValue("version","SubmissionNotAllowed_batch","Motion cannot be submitted.");
											}
										}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){												
											
											if(!Motion.isAllowedForSubmission(domain, new Date(), "")){
												String strSubDate = domain.getSession().getParameter(domain.getType().getType() + "_submissionStartTime");
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate_before","Motion cannot be submitted before " + strSubDate);
											}
										}
										break;
									}
								}								
							}
						}
					}

					if(optionalFields != null && !optionalFields.contains("primaryMember")){
						if(domain.getPrimaryMember()==null){
							result.rejectValue("primaryMember","PrimaryMemberEmpty");
						}
					}
					
					if(optionalFields != null && !optionalFields.contains("subject")){
						if(domain.getSubject().isEmpty()){
							result.rejectValue("subject","SubjectEmpty");
						}
					}
					
					if(optionalFields != null && !optionalFields.contains("details")){
						if(domain.getDetails().isEmpty()){
							result.rejectValue("details","DetailsEmpty");
						}	
					}
					
					if(optionalFields != null && !optionalFields.contains("ministry")){
						if(domain.getMinistry()==null){
							result.rejectValue("ministry","MinistryEmpty");
						}		
					}	
					
				}else /**** Start Workflow By assistant ****/
					if(operation.equals("startworkflow")){
						if(domain.getHouseType()==null){
							result.rejectValue("houseType","HousetypeEmpty");
						}
						if(domain.getType()==null){
							result.rejectValue("type","MotionTypeEmpty");
						}
						if(domain.getSession()==null){
							result.rejectValue("session","SessionEmpty");
						}


						if(optionalFields != null && !optionalFields.contains("primaryMember")){
							if(domain.getPrimaryMember()==null){
								result.rejectValue("primaryMember","PrimaryMemberEmpty");
							}
						}
						
						if(optionalFields != null && !optionalFields.contains("subject")){
							if(domain.getSubject().isEmpty()){
								result.rejectValue("subject","SubjectEmpty");
							}
						}
						
						if(optionalFields != null && !optionalFields.contains("details")){
							if(domain.getDetails().isEmpty()){
								result.rejectValue("details","DetailsEmpty");
							}	
						}
						
						if(optionalFields != null && !optionalFields.contains("ministry")){
							if(domain.getMinistry()==null){
								result.rejectValue("ministry","MinistryEmpty");
							}		
						}
						
						if(optionalFields != null && !optionalFields.contains("subDepartment")){
							if(domain.getSubDepartment()==null){
								result.rejectValue("subDepartment","SubDepartmentEmpty");
							}		
						}	
							
						String internalStatusType=domain.getInternalStatus().getType();
						if(internalStatusType.equals(ApplicationConstants.MOTION_SUBMIT)){
							result.rejectValue("internalStatus","PutUpOptionEmpty");
						}						
						if(internalStatusType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)){
							result.rejectValue("internalStatus","PutUpOptionEmpty");
						}
						if(!(internalStatusType.equals(ApplicationConstants.MOTION_SUBMIT))
								&&!(internalStatusType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED))
								&&(domain.getActor()==null||domain.getActor().isEmpty())){
							result.rejectValue("internalStatus","ActorEmpty");
						}
					}			
		}/**** Drafts Creation By Member/Proof Reading By Assistant ****/
		else{
			if(domain.getHouseType()==null){
				result.rejectValue("houseType","HousetypeEmpty");
			}
			if(domain.getType()==null){
				result.rejectValue("type","MotionTypeEmpty");
			}
			if(domain.getSession()==null){
				result.rejectValue("session","SessionEmpty");
			}

			if(optionalFields != null && !optionalFields.contains("primaryMember")){
				if(domain.getPrimaryMember()==null){
					result.rejectValue("primaryMember","PrimaryMemberEmpty");
				}
			}
			
			if(optionalFields != null && !optionalFields.contains("subject")){
				if(domain.getSubject().isEmpty()){
					result.rejectValue("subject","SubjectEmpty");
				}
			}
			
			if(optionalFields != null && !optionalFields.contains("details")){
				if(domain.getDetails().isEmpty()){
					result.rejectValue("details","DetailsEmpty");
				}	
			}
			
			String usergroupType=request.getParameter("usergroupType");
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("assistant")){
				if(optionalFields != null && !optionalFields.contains("ministry")){
					if(domain.getMinistry()==null){
						result.rejectValue("ministry","MinistryEmpty");
					}		
				}
				
				if(optionalFields != null && !optionalFields.contains("subDepartment")){
					if(domain.getSubDepartment()==null){
						result.rejectValue("subDepartment","SubDepartmentEmpty");
					}		
				}	
			}
		}
	}

	@Override
	protected void populateCreateIfErrors(final ModelMap model, Motion domain,
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
		//Submission Priority
		model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		int currentReadyToSubmitCount = Motion.findReadyToSubmitCount(domain.getSession(), domain.getPrimaryMember(), domain.getType(), domain.getLocale());
		model.addAttribute("submissionPriorityMaximum", currentReadyToSubmitCount+1);
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", domain.getLocale());
		String role=(String) request.getSession().getAttribute("role");
		populateMaxLengthParameters(domain.getHouseType(),role,domain.getType(),model);
		super.populateCreateIfErrors(model, domain, request);
	}

	@Override
	protected void populateUpdateIfErrors(final ModelMap model, Motion domain,
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
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		/** Populate whether admitted motion is discussed or not  **/
		if(internalStatus!=null && internalStatus.getType().trim().equalsIgnoreCase(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			if(recommendationStatus!=null && recommendationStatus.getType().trim().equalsIgnoreCase(ApplicationConstants.MOTION_PROCESSED_DISCUSSED)) {
				model.addAttribute("discussionStatus",recommendationStatus.getName());
			} else if(recommendationStatus!=null && recommendationStatus.getType().trim().equalsIgnoreCase(ApplicationConstants.MOTION_PROCESSED_UNDISCUSSED)) {
				model.addAttribute("discussionStatus",recommendationStatus.getName());
			} else {
				//if there comes status which is post discussed, then need to check discussed on drafts here..
				model.addAttribute("discussionStatus","");
			}
			/** populate discussion details text if question is discussed **/
			String discussionDetailsText = domain.findDiscussionDetailsText();
			model.addAttribute("discussionDetailsText", discussionDetailsText);			
		}
		super.populateUpdateIfErrors(model, domain, request);
	}

	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, Motion domain,
			final HttpServletRequest request) {			
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		String usergroupType=request.getParameter("usergroupType");
		boolean isValid = false;
		
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_OPTIONAL_FIELDS_IN_VALIDATION, "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}
		
		if(optionalFields != null){
			if(domain.getHouseType()!=null && domain.getSession()!=null
					&& domain.getType()!=null){
				boolean pmFlag = false;
				boolean subFlag = false;
				boolean detFlag = false;
				boolean minFlag = false;
				
				if(!optionalFields.contains("primaryMember")){
					if(domain.getPrimaryMember() != null){
						pmFlag = true;
					}
				}else{
					pmFlag = true;
				}
				
				if(!optionalFields.contains("subject")){
					if(domain.getSubject() != null){
						subFlag = true;
					}
				}else{
					subFlag = true;
				}
				
				if(!optionalFields.contains("details")){
					if(domain.getDetails() != null){
						detFlag = true;
					}
				}else{
					detFlag = true;
				}
				
				if(!optionalFields.contains("ministry")){
					if(domain.getMinistry() != null){
						minFlag = true;
					}
				}else{
					minFlag = true;
				}
				
				isValid = (pmFlag && subFlag && detFlag && minFlag);
			}	
		}else{
			if(domain.getHouseType()!=null && domain.getSession()!=null
					&& domain.getType()!=null && domain.getPrimaryMember() != null 
					&& domain.getSubject() != null && domain.getDetails() != null 
					&& domain.getMinistry() != null){
				isValid = true;
			}
		}
		
		if(isValid){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						CustomParameter csptUserGroupsAllowedToSubmit = CustomParameter.findByName(CustomParameter.class, "MOIS_USSERGROUPS_ALLOWED_TO_SUBMIT", "");
						
						if(csptUserGroupsAllowedToSubmit != null){
							if(csptUserGroupsAllowedToSubmit.getValue() != null){
								if(!csptUserGroupsAllowedToSubmit.getValue().isEmpty()){
									if(usergroupType != null && !(usergroupType.isEmpty()) && (csptUserGroupsAllowedToSubmit.getValue().contains(usergroupType))){
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
														i.setApprovedText(domain.getDetails());
														i.setApprovedSubject(domain.getSubject());
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
										Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_SUBMIT, domain.getLocale());
										domain.setStatus(newstatus);
										domain.setInternalStatus(newstatus);
										domain.setRecommendationStatus(newstatus);
									}
								}
							}
							
						}
					}else{
						CustomParameter csptUserGroupsAllowedToSubmit = CustomParameter.findByName(CustomParameter.class, "MOIS_USSERGROUPS_ALLOWED_TO_SUBMIT", "");
						
						if(csptUserGroupsAllowedToSubmit != null){
							if(csptUserGroupsAllowedToSubmit.getValue() != null){
								if(!csptUserGroupsAllowedToSubmit.getValue().isEmpty()){
									if(usergroupType != null && !(usergroupType.isEmpty()) && (csptUserGroupsAllowedToSubmit.getValue().contains(usergroupType))){
										Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_COMPLETE, domain.getLocale());
										domain.setStatus(status);
										domain.setInternalStatus(status);
										domain.setRecommendationStatus(status);
									}
								}
							}
						}
					}
				}else{
					CustomParameter csptUserGroupsAllowedToSubmit = CustomParameter.findByName(CustomParameter.class, "MOIS_USSERGROUPS_ALLOWED_TO_SUBMIT", "");
					
					if(csptUserGroupsAllowedToSubmit != null){
						if(csptUserGroupsAllowedToSubmit.getValue() != null){
							if(!csptUserGroupsAllowedToSubmit.getValue().isEmpty()){
								if(usergroupType != null && !(usergroupType.isEmpty()) && (csptUserGroupsAllowedToSubmit.getValue().contains(usergroupType))){
									Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_COMPLETE, domain.getLocale());
									domain.setStatus(status);
									domain.setInternalStatus(status);
									domain.setRecommendationStatus(status);
								}
							}
						}
					}
				}
			}else{
				CustomParameter csptUserGroupsAllowedToSubmit = CustomParameter.findByName(CustomParameter.class, "MOIS_USSERGROUPS_ALLOWED_TO_SUBMIT", "");
				
				if(csptUserGroupsAllowedToSubmit != null){
					if(csptUserGroupsAllowedToSubmit.getValue() != null){
						if(!csptUserGroupsAllowedToSubmit.getValue().isEmpty()){
							if(usergroupType != null && !(usergroupType.isEmpty()) && (csptUserGroupsAllowedToSubmit.getValue().contains(usergroupType))){
								Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_COMPLETE, domain.getLocale());
								domain.setStatus(status);
								domain.setInternalStatus(status);
								domain.setRecommendationStatus(status);
							}
						}
					}
				}
			}
		}
		/**** Drafts ****/
		else{			
			CustomParameter csptUserGroupsAllowedToSubmit = CustomParameter.findByName(CustomParameter.class, "MOIS_USSERGROUPS_ALLOWED_TO_SUBMIT", "");
			
			if(csptUserGroupsAllowedToSubmit != null){
				if(csptUserGroupsAllowedToSubmit.getValue() != null){
					if(!csptUserGroupsAllowedToSubmit.getValue().isEmpty()){
						if(usergroupType != null && !(usergroupType.isEmpty()) && (csptUserGroupsAllowedToSubmit.getValue().contains(usergroupType))){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_INCOMPLETE, domain.getLocale());
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}
					}
				}
			}
		}
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		
//		CustomParameter csptUserGroupsAllowedToSubmit = CustomParameter.findByName(CustomParameter.class, "MOIS_USSERGROUPS_ALLOWED_TO_SUBMIT", "");
//		
//		if(csptUserGroupsAllowedToSubmit != null){
//			if(csptUserGroupsAllowedToSubmit.getValue() != null){
//				if(!csptUserGroupsAllowedToSubmit.getValue().isEmpty()){
//					if(usergroupType != null && !(usergroupType.isEmpty()) && (csptUserGroupsAllowedToSubmit.getValue().contains(usergroupType))){
//						try {
//							Member member=domain.getPrimaryMember();
//							User user=User.findbyNameBirthDate(member.getFirstName(),member.getMiddleName(),member.getLastName(),member.getBirthDate());
//							domain.setCreatedBy(user.getCredential().getUsername());
//						} catch (ELSException e) {
//							model.addAttribute("error", e.getParameter());
//						}
//					}else{
//						domain.setCreatedBy(this.getCurrentUser().getActualUsername());
//					}
//				}
//			}
//		}
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
		//set submission priority to default value if not set explicitly
		if(domain.getSubmissionPriority()==null) {
			domain.setSubmissionPriority(ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
		}
	}

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, Motion domain,
			final HttpServletRequest request) {
		/**** Checking if its submission request or normal update ****/
		String operation=request.getParameter("operation");		
		String usergroupType=request.getParameter("usergroupType");
		
		/**** Motion status will be complete if all mandatory fields have been filled ****/
		boolean isValid = false;
		
		/**** To skip the optional fields ****/
		String optionalFields = null;		
		CustomParameter csptOptionalFields = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.MOTION_OPTIONAL_FIELDS_IN_VALIDATION, "");		
		if(csptOptionalFields != null && csptOptionalFields.getValue() != null && !csptOptionalFields.getValue().isEmpty()){
			optionalFields = csptOptionalFields.getValue();
		}
		
		if(optionalFields != null){
			if(domain.getHouseType()!=null && domain.getSession()!=null
					&& domain.getType()!=null){
				boolean pmFlag = false;
				boolean subFlag = false;
				boolean detFlag = false;
				boolean minFlag = false;
				
				if(!optionalFields.contains("primaryMember")){
					if(domain.getPrimaryMember() != null){
						pmFlag = true;
					}
				}else{
					pmFlag = true;
				}
				
				if(!optionalFields.contains("subject")){
					if(domain.getSubject() != null && !domain.getSubject().isEmpty()){
						subFlag = true;
					}
				}else{
					subFlag = true;
				}
				
				if(!optionalFields.contains("details")){
					if(domain.getDetails() != null && !domain.getDetails().isEmpty()){
						detFlag = true;
					}
				}else{
					detFlag = true;
				}
				
				if(!optionalFields.contains("ministry")){
					if(domain.getMinistry() != null){
						minFlag = true;
					}
				}else{
					minFlag = true;
				}
				
				isValid = (pmFlag && subFlag && detFlag && minFlag);
			}	
		}else{
			if(domain.getHouseType()!=null && domain.getSession()!=null
					&& domain.getType()!=null && domain.getPrimaryMember() != null
					&& domain.getMinistry() != null
					&& (domain.getSubject() != null && !domain.getSubject().isEmpty())
					&& (domain.getDetails() != null && !domain.getDetails().isEmpty())){
				isValid = true;
			}
		}
		
		CustomParameter csptAllowedToSubmit = CustomParameter.findByName(CustomParameter.class, "MOIS_USSERGROUPS_ALLOWED_TO_SUBMIT", "");
		
		if(isValid){			
			if(operation!=null){
				if(!operation.isEmpty()){
					/**** Submission request ****/
					if(operation.trim().equals("submit")){
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&(csptAllowedToSubmit.getValue().contains(usergroupType))){
							/**** Submission date is set ****/
							if(domain.getSubmissionDate()==null){
								domain.setSubmissionDate(new Date());
							}
							/**** Update Timed Out Supporting Members (can be disabled for starting hour of submission start time using custom parameter) ****/
							Status timeoutStatus = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, domain.getLocale());					
							CustomParameter csptTimeoutOfSupportingMembersDisabled = CustomParameter.findByName(CustomParameter.class, "MOIS_SUPPORTINGMEMBERS_TIMEOUT_DISABLED", "");
							if(csptTimeoutOfSupportingMembersDisabled!=null 
									&& csptTimeoutOfSupportingMembersDisabled.getValue()!=null
									&& csptTimeoutOfSupportingMembersDisabled.getValue().equals("YES")) {
								System.out.println("Timeout of Pending/Unsent Supporting Members Disabled");
							} else {
								/**** Supporting Members who have approved request are included ****/
								List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
								List<SupportingMember> existingSupportingMembers = domain.getSupportingMembers();
								if(existingSupportingMembers != null && ! existingSupportingMembers.isEmpty()) {
									for(SupportingMember i: existingSupportingMembers){
										if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)||
												i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)){
											/**** Update Supporting Member ****/
											i.setDecisionStatus(timeoutStatus);
											i.setApprovalDate(new Date());	
											i.setApprovedText(domain.getDetails());
											i.setApprovedSubject(domain.getSubject());
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
								//end pending supporting member tasks if removed manually by member
								Motion.updateTimeoutSupportingMemberTasksForDevice(domain.getId(), new Date());
							}							
							/**** Status,Internal status and recommendation status is set to submit ****/
							Status submitStatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_SUBMIT, domain.getLocale());
							domain.setStatus(submitStatus);
							domain.setInternalStatus(submitStatus);
							domain.setRecommendationStatus(submitStatus);
						}
					}else{
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_COMPLETE, domain.getLocale());
							/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
							if(!domain.getStatus().getType().equals(ApplicationConstants.MOTION_SUBMIT)){
								domain.setStatus(status);
								domain.setInternalStatus(status);
								domain.setRecommendationStatus(status);
							}	
						}
					}
				}else{
					if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_COMPLETE, domain.getLocale());
						/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
						if(!domain.getStatus().getType().equals(ApplicationConstants.MOTION_SUBMIT)){
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}
					}
				}
			}else{
				if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_COMPLETE, domain.getLocale());
					/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
					if(!domain.getStatus().getType().equals(ApplicationConstants.MOTION_SUBMIT)){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}
//				if(domain.getActor() != null && !domain.getActor().isEmpty() && usergroupType.equals(ApplicationConstants.ASSISTANT)){
//					Status status = Status.findByType(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
//					domain.setInternalStatus(status);
//					domain.setRecommendationStatus(status);
//				}
			}
		}
		/**** If all mandatory fields have not been set then status,internal status and recommendation status is set to incomplete ****/
		else{
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_INCOMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}
		/**** Edited On,Edited By and Edited As is set ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		//set submission priority to default value if not set explicitly
		if(domain.getSubmissionPriority()==null) {
			domain.setSubmissionPriority(ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
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
		
		if(strUserGroupType!=null){
			String internalStatus = domain.getInternalStatus().getType();
			if(strUserGroupType.equals("assistant") ||strUserGroupType.equals("clerk")){				
				if(internalStatus.equals(ApplicationConstants.MOTION_SUBMIT)) {
					if(optionalFields != null && !optionalFields.contains("ministry")
							&& !optionalFields.contains("subDepartment")){
						if(domain.getMinistry()!=null&&domain.getSubDepartment()!=null){
							Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
							domain.setInternalStatus(ASSISTANT_PROCESSED);
							domain.setRecommendationStatus(ASSISTANT_PROCESSED);
						}
					}else{
						Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
						domain.setInternalStatus(ASSISTANT_PROCESSED);
						domain.setRecommendationStatus(ASSISTANT_PROCESSED);
					}
				}	
				
				if(internalStatus.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)){
					if(optionalFields != null && !optionalFields.contains("ministry")
							&& !optionalFields.contains("subDepartment")){
						if(domain.getMinistry()!=null&&domain.getSubDepartment()!=null){
							Status TOBE_PUTUP = Status.findByType(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
							domain.setInternalStatus(TOBE_PUTUP);
							domain.setRecommendationStatus(TOBE_PUTUP);
						}
					}else{
						Status TOBE_PUTUP = Status.findByType(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
						domain.setInternalStatus(TOBE_PUTUP);
						domain.setRecommendationStatus(TOBE_PUTUP);
					}
				}
				
				/**** File parameters are set when internal status is something other than 
				 * submit,complete and incomplete and file is null .Then only the motion gets attached to a file.*/
				String currentStatus=domain.getInternalStatus().getType();
				if(operation==null){
					if(!(currentStatus.equals(ApplicationConstants.MOTION_SUBMIT)
							||currentStatus.equals(ApplicationConstants.MOTION_COMPLETE)
							||currentStatus.equals(ApplicationConstants.MOTION_INCOMPLETE))							
							&&domain.getFile()==null){
						/**** Add motion to file ****/
						Reference reference=Motion.findCurrentFile(domain);
						domain.setFile(Integer.parseInt(reference.getId()));
						domain.setFileIndex(Integer.parseInt(reference.getName()));
						domain.setFileSent(false);
					}
				}else if(operation.isEmpty()){
					if(!(currentStatus.equals(ApplicationConstants.MOTION_SUBMIT)
							||currentStatus.equals(ApplicationConstants.MOTION_COMPLETE)
							||currentStatus.equals(ApplicationConstants.MOTION_INCOMPLETE))
							&&domain.getFile()==null){
						/**** Add motion to file ****/
						Reference reference=Motion.findCurrentFile(domain);
						domain.setFile(Integer.parseInt(reference.getId()));
						domain.setFileIndex(Integer.parseInt(reference.getName()));
						domain.setFileSent(false);
					}
				}
			}
			
			//check for sending advance copy to department
			Motion motion=Motion.findById(Motion.class,domain.getId()); //for checking if advance copy is not already sent
			if(internalStatus.equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION)
					&& (strUserGroupType.equals(ApplicationConstants.ASSISTANT) || strUserGroupType.equals(ApplicationConstants.SECTION_OFFICER))
					&& (motion.getAdvanceCopySent()==null || motion.getAdvanceCopySent().booleanValue()==false)
					&& domain.getAdvanceCopySent().booleanValue()==true){
				String usergroupTypesForAdvanceCopyNotification = "";
				if(strUserGroupType.equals(ApplicationConstants.ASSISTANT)) {
					usergroupTypesForAdvanceCopyNotification = "section_officer,department";
				} else {
					usergroupTypesForAdvanceCopyNotification = "assistant,department";
				}
				NotificationController.sendDepartmentProcessNotificationIncludingBranchForMotion(domain,
						usergroupTypesForAdvanceCopyNotification, "advanceCopy", domain.getLocale());
			}
		}
	}

	@Override
	protected void populateAfterCreate(final ModelMap model, Motion domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));

		/**** Supporting Member Workflow ****/
		String operation=request.getParameter("operation");
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("userrole",request.getParameter("userrole"));
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** Added By Sandeep Singh ****/
					/**** process Started ****/
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",domain.getType().getType());
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					List<Task> tasks=processService.getCurrentTasks(processInstance);
					List<WorkflowDetails> workflowDetails;
					try {
						workflowDetails = WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"0");
						
						/**** Supporting members status changed to pending ****/
						Motion motion=Motion.findById(Motion.class,domain.getId());
						List<SupportingMember> supportingMembers=motion.getSupportingMembers();
						Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
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
	protected void populateAfterUpdate(final ModelMap model, Motion domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		if(request.getParameter("bulkedit")!=null&&!request.getParameter("bulkedit").isEmpty()){
			request.getSession().setAttribute("bulkedit",request.getParameter("bulkedit"));
		}
		/**** Approval Workflow ****/
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				/**** Supporting Member Workflow ****/
				if(operation.equals("approval")){
					/**** Added by Sandeep Singh ****/
					/**** Supporting Member Workflow is started ****/
					try {
						ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
						Map<String,String> properties=new HashMap<String, String>();
						properties.put("pv_deviceId",String.valueOf(domain.getId()));
						properties.put("pv_deviceTypeId",domain.getType().getType());
						ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
						/**** Workflow Details Entries are created ****/
						List<Task> tasks=processService.getCurrentTasks(processInstance);					
						List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"");
						/**** Not Send supporting members status are changed to pending ****/
						Motion motion=Motion.findById(Motion.class,domain.getId());
						List<SupportingMember> supportingMembers=motion.getSupportingMembers();
						Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
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
				}else if(operation.equals("startworkflow")){
					/**** Added by Sandeep Singh ****/
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
						Motion motion=Motion.findById(Motion.class,domain.getId());
						/**** Process Started and task created ****/
						Task task=processService.getCurrentTask(processInstance);
						if(endflag!=null){
							if(!endflag.isEmpty()){
								if(endflag.equals("continue")){
									/**** Workflow Detail entry made only if its not the end of workflow ****/
									//WorkflowDetails workflowDetails=WorkflowDetails.create(domain,task,ApplicationConstants.APPROVAL_WORKFLOW,level);
									//motion.setWorkflowDetailsId(workflowDetails.getId());
									
									
										
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

									if(recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflow = Workflow.findByStatus(recommendationStatus, domain.getLocale());
									} 
									else if(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING)
											|| internalStatusType.equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)
											|| (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
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
								}
							}
						}
						/**** Workflow Started ****/
						motion.setWorkflowStarted("YES");
						motion.setWorkflowStartedOn(new Date());
						motion.setTaskReceivedOn(new Date());
						/**** If motion is sent individually then its file's parameters is set to null i.e 
						 * it is removed from file ****/
						motion.setFile(null);
						motion.setFileIndex(null);
						motion.setFileSent(false);
						motion.simpleMerge();
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
					} catch(Exception e){
						model.addAttribute("error", e.getMessage());
					}
				}
			}
		}
		
	}

	/**** Supporting Members View Status ****/
	@RequestMapping(value="/status/{motion}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("motion") final String motion){
		Motion motionTemp=Motion.findById(Motion.class,Long.parseLong(motion));
		List<SupportingMember> supportingMembers=motionTemp.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "motion/supportingmember";
	}

	/**** Member-Supporting Members Contacts ****/
	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "motion/contacts";
	}

	/**** revision History ****/
	@RequestMapping(value="/revisions/{motionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("motionId")  final Long motionId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=Motion.getRevisions(motionId,locale.toString());
		Motion m = Motion.findById(Motion.class, motionId);
		if(m != null){
			if(m.getType() != null){
				if(m.getType().getType() != null){
					model.addAttribute("selectedDeviceType", m.getType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);
		return "motion/revisions";
	}

	/**** Citations ****/
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
		return "motion/citation";
	}
	
	@RequestMapping(value="/determine_ordering_for_submission", method=RequestMethod.GET)
	public String determineOrderingForSubmissionInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		AuthUser authUser = this.getCurrentUser();
		try {
			HouseType houseType = MotionController.getHouseType(request, locale.toString());
			SessionType sessionType = MotionController.getSessionType(request, locale.toString());
			Integer sessionYear = MotionController.stringToIntegerYear(request, locale.toString());
			DeviceType deviceType = MotionController.getDeviceTypeById(request, locale.toString());
			Member primaryMember = Member.findMember(authUser.getFirstName(),
					authUser.getMiddleName(), authUser.getLastName(), authUser.getBirthDate(), locale.toString());
			
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			List<Motion> motions = new ArrayList<Motion>();
			if(session != null){
				if(primaryMember != null){
					motions = Motion.findReadyToSubmitMotions(session, primaryMember, deviceType, locale.toString());
				}
			}
			
			model.addAttribute("houseType", houseType.getId());
			model.addAttribute("motionType", deviceType.getId());
			model.addAttribute("deviceType", deviceType.getId());
			model.addAttribute("motions", motions);
			model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
			model.addAttribute("locale", locale.toString());
			model.addAttribute("formater", new FormaterUtil());
			
			return "motion/orderingforsubmission";
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}	
	
	@Transactional
	@RequestMapping(value="determine_ordering_for_submission", method=RequestMethod.POST)
	public String determineOrderingForSubmission(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		AuthUser authUser = this.getCurrentUser();
		try {
			String retVal = "motion/error";
			String selectedItems = request.getParameter("items");
			if(selectedItems != null && ! selectedItems.isEmpty()) {
				String[] items = selectedItems.split(",");
				List<Motion> motions = new ArrayList<Motion>();
				for(String i : items) {				
					Long id = Long.parseLong(i.split("_")[0]);
					Motion motion = Motion.findById(Motion.class, id);
					if(motion!=null) {
						motion.setSubmissionPriority(Integer.parseInt(i.split("_")[1]));
						motion.simpleMerge();
						motions.add(motion);
					}
				}
				motions = Motion.sortBySubmissionPriority(motions, ApplicationConstants.ASC);
				model.addAttribute("motions", motions);
				model.addAttribute("defaultSubmissionPriority", ApplicationConstants.DEFAULT_SUBMISSION_PRIORITY);
				model.addAttribute("formater", new FormaterUtil());
				model.addAttribute("locale", locale.toString());
				model.addAttribute("type","success");
				
				retVal = "motion/orderingforsubmissionack";
			}
			return retVal;
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**** Bulk Submission ****/
	@RequestMapping(value="/bulksubmission",method=RequestMethod.GET)
	public String getBulkSubmissionView(final HttpServletRequest request,final Locale locale,
			final ModelMap model){	
		try {
			String userGroupType = request.getParameter("usergroupType");
			List<Motion> motions= new ArrayList<Motion>();
			if(userGroupType != null && !userGroupType.equals("")){
				String strHouseType=request.getParameter("houseType");
				String strSessionType=request.getParameter("sessionType");
				String strSessionYear=request.getParameter("sessionYear");
				String strMotionType=request.getParameter("motionType");
				String strLocale=locale.toString();
				String strFile = request.getParameter("file");
				String strItemsCount=request.getParameter("itemscount");
				if(strHouseType!=null&&!(strHouseType.isEmpty())
						&&strSessionType!=null&&!(strSessionType.isEmpty())
						&&strSessionYear!=null&&!(strSessionYear.isEmpty())
						&&strMotionType!=null&&!(strMotionType.isEmpty())
						&&strItemsCount!=null&&!(strItemsCount.isEmpty())){
					HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
					model.addAttribute("houseType", houseType.getId());
					SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
					Integer sessionYear=Integer.parseInt(strSessionYear);
					Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
					model.addAttribute("motionType",motionType.getId());
					model.addAttribute("deviceType", motionType.getId());
					Integer itemsCount=Integer.parseInt(strItemsCount);
					if(userGroupType.equals(ApplicationConstants.TYPIST)){
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_BULKPUTUP_FOR_TYPIST", "");
						if(customParameter != null){
							if(customParameter.getValue().equals("YES")){
								motions = Motion.findAllCompleteByCreator(session, this.getCurrentUser().getUsername(),motionType,itemsCount,strLocale);
							}
						}
					}else{
						Member primaryMember=Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale.toString());
						if(primaryMember != null){
							//motions = Motion.findAllByMember(session,primaryMember,motionType,itemsCount,strLocale);
							motions = Motion.findAllCompleteByCreator(session, this.getCurrentUser().getUsername(),motionType,itemsCount,strLocale);
						}
					}
				}
				
			}
			
			
			
			model.addAttribute("motions",motions);
			model.addAttribute("size",motions.size());
			model.addAttribute("locale", locale.toString());
			
			model.addAttribute("usergroupType", userGroupType);
						
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}	
		return "motion/bulksubmission";
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
	public synchronized String bulkSubmission(final HttpServletRequest request, final Locale locale,
			final ModelMap model) {
		String selectedItems = request.getParameter("items");
		if(selectedItems != null && ! selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");
			Motion domain = Motion.findById(Motion.class, new Long(items[0]));
			Session session = domain.getSession();
			boolean validationForSubmissionDate = false;
			//submission date limit validations (configurable through custom parameters)
			if(domain.getSession()!=null && domain.getType()!=null) {						
				CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
				if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
					String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
					if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
						String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
						for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
							if(dt.trim().equals(domain.getType().getType().trim())) {
								if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									
									if(session.getParameter(domain.getType().getType() + "_" + "firstBatchStartTime")!=null && !session.getParameter(domain.getType().getType() + "_" + "firstBatchStartTime").isEmpty()
											&& session.getParameter(domain.getType().getType() + "_" + "firstBatchEndTime")!=null && !session.getParameter(domain.getType().getType() + "_" + "firstBatchEndTime").isEmpty()
											&& session.getParameter(domain.getType().getType() + "_" + "secondBatchStartTime")!=null && !session.getParameter(domain.getType().getType() + "_" + "secondBatchStartTime").isEmpty()
											&& session.getParameter(domain.getType().getType() + "_" + "secondBatchEndTime")!=null && !session.getParameter(domain.getType().getType() + "_" + "secondBatchEndTime").isEmpty()) {
										
										Integer batch = Motion.findBatch(domain, new Date());	
										
										if(batch.equals(1)){
											if(!Motion.allowedInFirstBatch(domain, new Date())){
												validationForSubmissionDate = true;
											}
										}
										
										else if(batch.equals(2)){
											if(!Motion.allowedInSecondBatch(domain, new Date())){
												validationForSubmissionDate = true;
											}
										}
										
										else if(batch.equals(0)){
											validationForSubmissionDate = true;												
										}
									} else {
										validationForSubmissionDate = true; //submission limit dates not set for the session
									}	
									
								}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){												
									
									if(session.getParameter(domain.getType().getType() + "_" + "submissionStartTime")!=null && !session.getParameter(domain.getType().getType() + "_" + "submissionStartTime").isEmpty()
											&& session.getParameter(domain.getType().getType() + "_" + "submissionEndTime")!=null && !session.getParameter(domain.getType().getType() + "_" + "submissionEndTime").isEmpty()) {
										
										if(!Motion.isAllowedForSubmission(domain, new Date(), "")){
											validationForSubmissionDate = true;
										}
									} else {
										validationForSubmissionDate = true; //submission limit dates not set for the session
									}
									
								}
								break;
							}
						}								
					}
				}
			}
			
			if(!validationForSubmissionDate) {
				List<Motion> motions = new ArrayList<Motion>();
				Long firstId = null;
				if(items != null && items.length > 0){
					firstId = new Long(items[0]);
				}
				
				try{
//					Motion firstMotion = Motion.findById(Motion.class, firstId);
//					String strSubDate = null;
//					
//					if(firstMotion != null){
//						//submission date limit validations (configurable through custom parameters)
//						if(firstMotion.getSession()!=null && firstMotion.getType()!=null) {
//						
//							//submission start date limit validation
//							CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
//							if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
//								String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
//								if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
//									String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
//									for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
//										if(dt.trim().equals(firstMotion.getType().getType().trim())) {
//											
//											if(firstMotion.getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
//												Integer batch = Motion.findBatch(firstMotion, new Date());
//												
//												if(batch.equals(1) || batch.equals(2)){
//													if(Motion.allowedInFirstBatch(firstMotion, new Date()) 
//															|| Motion.allowedInSecondBatch(firstMotion, new Date())){
//														
//														for(String i : items) {
//															Long id = Long.parseLong(i);
//															Motion motion = Motion.findById(Motion.class, id);
	//
//															/**** Update Supporting Member ****/
//															List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
//															Status timeoutStatus=Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
//															if(motion.getSupportingMembers() != null) {
//																if(! motion.getSupportingMembers().isEmpty()) {
//																	for(SupportingMember sm : motion.getSupportingMembers()) {
//																		if(sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)||
//																				sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)){
//																			/**** Update Supporting Member ****/
//																			sm.setDecisionStatus(timeoutStatus);
//																			sm.setApprovalDate(new Date());	
//																			sm.setApprovedText(motion.getDetails());
//																			sm.setApprovedSubject(motion.getSubject());
//																			sm.setApprovalType("ONLINE");
//																			/**** Update Workflow Details ****/
//																			String strWorkflowdetails=sm.getWorkflowDetailsId();
//																			if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
//																				WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
//																				workflowDetails.setStatus("TIMEOUT");
//																				workflowDetails.setCompletionTime(new Date());
//																				workflowDetails.merge();
//																				/**** Complete Task ****/
//																				String strTaskId=workflowDetails.getTaskId();
//																				Task task=processService.findTaskById(strTaskId);
//																				processService.completeTask(task);
//																			}		
//																		}
//																		if(!sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
//																			supportingMembers.add(sm);
//																		}
//																	}
//																	motion.setSupportingMembers(supportingMembers);
//																}
//															}
	//
//															/**** Update Status(es) ****/
//															Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_SUBMIT, motion.getLocale());
//															motion.setStatus(newstatus);
//															motion.setInternalStatus(newstatus);
//															motion.setRecommendationStatus(newstatus);
	//
//															/**** Edited On,Edited By and Edited As is set ****/
//															motion.setSubmissionDate(new Date());
//															motion.setEditedOn(new Date());
//															motion.setEditedBy(this.getCurrentUser().getActualUsername());
//															String strUserGroupType=request.getParameter("usergroupType");
//															if(strUserGroupType!=null){
//																UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, motion.getLocale());
//																motion.setEditedAs(userGroupType.getName());
//															}				
//															/**** Bulk Submitted ****/
//															motion.setBulkSubmitted(true);
//															/**** Update the Motion object ****/
//															motion = motion.merge();
//															motions.add(motion);
//														}
	//
//														model.addAttribute("motions", motions);
//													}
//												}
//											}else if(firstMotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){												
//												
//												if(Motion.isAllowedForSubmission(firstMotion, new Date(), "")){
//													for(String i : items) {
//														Long id = Long.parseLong(i);
//														Motion motion = Motion.findById(Motion.class, id);
	//
//														/**** Update Supporting Member ****/
//														List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
//														Status timeoutStatus=Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
//														if(motion.getSupportingMembers() != null) {
//															if(! motion.getSupportingMembers().isEmpty()) {
//																for(SupportingMember sm : motion.getSupportingMembers()) {
//																	if(sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)||
//																			sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)){
//																		/**** Update Supporting Member ****/
//																		sm.setDecisionStatus(timeoutStatus);
//																		sm.setApprovalDate(new Date());	
//																		sm.setApprovedText(motion.getDetails());
//																		sm.setApprovedSubject(motion.getSubject());
//																		sm.setApprovalType("ONLINE");
//																		/**** Update Workflow Details ****/
//																		String strWorkflowdetails=sm.getWorkflowDetailsId();
//																		if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
//																			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
//																			workflowDetails.setStatus("TIMEOUT");
//																			workflowDetails.setCompletionTime(new Date());
//																			workflowDetails.merge();
//																			/**** Complete Task ****/
//																			String strTaskId=workflowDetails.getTaskId();
//																			Task task=processService.findTaskById(strTaskId);
//																			processService.completeTask(task);
//																		}		
//																	}
//																	if(!sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
//																		supportingMembers.add(sm);
//																	}
//																}
//																motion.setSupportingMembers(supportingMembers);
//															}
//														}
	//
//														/**** Update Status(es) ****/
//														Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_SUBMIT, motion.getLocale());
//														motion.setStatus(newstatus);
//														motion.setInternalStatus(newstatus);
//														motion.setRecommendationStatus(newstatus);
	//
//														/**** Edited On,Edited By and Edited As is set ****/
//														motion.setSubmissionDate(new Date());
//														motion.setEditedOn(new Date());
//														motion.setEditedBy(this.getCurrentUser().getActualUsername());
//														String strUserGroupType=request.getParameter("usergroupType");
//														if(strUserGroupType!=null){
//															UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, motion.getLocale());
//															motion.setEditedAs(userGroupType.getName());
//														}				
//														/**** Bulk Submitted ****/
//														motion.setBulkSubmitted(true);
//														/**** Update the Motion object ****/
//														motion = motion.merge();
//														motions.add(motion);
//													}
	//
//													model.addAttribute("motions", motions);
//												}
//											}
//										}
//									}								
//								}
//							}
//						}
//					}
					
					Status submitStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.MOTION_SUBMIT, domain.getLocale());
					CustomParameter csptTimeoutOfSupportingMembersDisabled = CustomParameter.findByName(CustomParameter.class, "MOIS_SUPPORTINGMEMBERS_TIMEOUT_DISABLED", "");
					Status timeoutStatus = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
					for(String i : items) {
						Long id = Long.parseLong(i);
						Motion motion = Motion.findById(Motion.class, id);

						/**** Update Timed Out Supporting Members (can be disabled for starting hour of submission start time using custom parameter) ****/						
						if(csptTimeoutOfSupportingMembersDisabled!=null 
								&& csptTimeoutOfSupportingMembersDisabled.getValue()!=null
								&& csptTimeoutOfSupportingMembersDisabled.getValue().equals("YES")) {
							System.out.println("Timeout of Pending/Unsent Supporting Members Disabled");
						} else {
							List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
							List<SupportingMember> existingSupportingMembers = motion.getSupportingMembers();
							if(existingSupportingMembers != null && ! existingSupportingMembers.isEmpty()) {
								for(SupportingMember sm : existingSupportingMembers) {
									if(sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)||
											sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)){
										/**** Update Supporting Member ****/
										sm.setDecisionStatus(timeoutStatus);
										sm.setApprovalDate(new Date());	
										sm.setApprovedText(motion.getDetails());
										sm.setApprovedSubject(motion.getSubject());
										sm.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
										/**** Update Workflow Details ****/
										String strWorkflowdetails=sm.getWorkflowDetailsId();
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
									if(!sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
										supportingMembers.add(sm);
									}
								}
								motion.setSupportingMembers(supportingMembers);
							}
						}						

						/**** Update Status(es) ****/
						motion.setStatus(submitStatus);
						motion.setInternalStatus(submitStatus);
						motion.setRecommendationStatus(submitStatus);

						/**** Edited On,Edited By and Edited As is set ****/
						motion.setSubmissionDate(new Date());
						motion.setEditedOn(new Date());
						motion.setEditedBy(this.getCurrentUser().getActualUsername());
						String strUserGroupType=request.getParameter("usergroupType");
						if(strUserGroupType!=null){
							UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, motion.getLocale());
							motion.setEditedAs(userGroupType.getName());
						}				
						/**** Bulk Submitted ****/
						motion.setBulkSubmitted(true);
						/**** Update the Motion object ****/
						motion = motion.merge();
						motions.add(motion);
					}

					model.addAttribute("motions", motions);
				}catch(Exception e){
					
				}
			}			
		}
		return "motion/bulksubmissionack";
	}

	/**** Bulk Submission(Assistant)****/
	@RequestMapping(value="/bulksubmission/assistant/int",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantInt(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strMotionType=request.getParameter("motionType");			
		String strStatus=request.getParameter("status");
		String strRole=request.getParameter("role");
		String strUsergroup=request.getParameter("usergroup");
		String strUsergroupType=request.getParameter("usergroupType");
		String strItemsCount=request.getParameter("itemscount");
		String strFile=request.getParameter("file");
		/**** Locale ****/
		String strLocale=locale.toString();
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
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
			DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
			/**** Decision Status Available To Assistant(At this stage) 
			 * MOTION_PUT_UP_OPTIONS_+MOTION_TYPE+HOUSE_TYPE+USERGROUP_TYPE ****/
			CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"MOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUsergroupType.toUpperCase(), "");
			List<Status> internalStatuses;
			try {
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale.toString());
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				return "motion/bulksubmission";
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
		return "motion/bulksubmissionassistantint";		
	}
	@RequestMapping(value="/bulksubmission/assistant/view",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantView(final HttpServletRequest request,final Locale locale,
			final Model model){	
		getBulkSubmissionMotions(model,request,locale.toString());
		return "motion/bulksubmissionassistantview";		
	}
	
	@Transactional
	@RequestMapping(value="/bulksubmission/assistant/update",method=RequestMethod.POST)
	public String bulkSubmissionAssistant(final HttpServletRequest request,final Locale locale,
			final Model model){	
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus=request.getParameter("aprstatus");
		String refText = request.getParameter("reftext");
		String remarks = request.getParameter("remarks");
		StringBuffer assistantProcessed=new StringBuffer();
		StringBuffer recommendAdmission=new StringBuffer();
		StringBuffer recommendRejection=new StringBuffer();
		if(selectedItems != null && selectedItems.length>0
				&&strStatus!=null&&!strStatus.isEmpty()) {
			
			List<ReferenceUnit> refs = null;
			for(String i : selectedItems){
				Motion question = Motion.findById(Motion.class, new Long(i));
				if(question != null){
					if(question.getReferencedUnits() != null){
						refs = question.getReferencedUnits();
						break;
					}
				}
			}
			/**** As It Is Condition ****/
			if(strStatus.equals("-")){
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					Motion motion = Motion.findById(Motion.class, id);
					if(!motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)){
						/**** Create Process ****/
						try {
							ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
							Map<String,String> properties=new HashMap<String, String>();
							String actor=motion.getActor();
							String[] temp=actor.split("#");
							properties.put("pv_user",temp[0]);						
							properties.put("pv_endflag",motion.getEndFlag());	
							properties.put("pv_deviceId",String.valueOf(motion.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(motion.getType().getId()));
							ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
							/**** Create Workdetails Entry ****/
							Task task=processService.getCurrentTask(processInstance);
							if(motion.getEndFlag()!=null&&!motion.getEndFlag().isEmpty()&&motion.getEndFlag().equals("continue")){
								
								UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());								
								try {

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

									if(recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
									} 
									else if(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING)
											|| internalStatusType.equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)
											|| (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
										workflow = Workflow.findByStatus(internalStatus, locale.toString());
									}
									else {
										workflow = Workflow.findByStatus(internalStatus, locale.toString());
									}

									String workflowType = workflow.getType();
									String assigneeLevel = motion.getLevel();
									WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, assigneeLevel);
									/*
									 * Added by Amit Desai 2 Dec 2014
									 * ... END
									 */
									
									if(refs != null && !refs.isEmpty()) {
										motion.setReferencedUnits(refs);
									}
									if(remarks != null){
										motion.setRemarks(remarks);
									}
									if(refText != null){
										motion.setRefText(refText);
									}
									
									String strFile = request.getParameter("file");
									if(strFile != null && !strFile.isEmpty()){
										if(motion.getFile() == null){
											motion.setFile(new Integer(strFile));
										}
									}
									/**** Workflow Started ****/
									motion.setWorkflowDetailsId(workflowDetails.getId());
									motion.setWorkflowStarted("YES");
									motion.setWorkflowStartedOn(new Date());
									motion.setTaskReceivedOn(new Date());
									motion.setFileSent(true);
									motion.simpleMerge();
								}catch(ELSException e){
									model.addAttribute("error", e.getParameter());
								}
							}
							if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION)){
								recommendAdmission.append(motion.formatNumber()+",");
							}else if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_REJECTION)){
								recommendRejection.append(motion.formatNumber()+",");
							}
						} catch (Exception e) {
							model.addAttribute("error", e);
						}
					}else{
						assistantProcessed.append(motion.formatNumber()+",");
					}
				}	
				model.addAttribute("assistantProcessed", assistantProcessed.toString());
			}else{
				Long statusId=Long.parseLong(strStatus);
				Status status=Status.findById(Status.class,statusId);
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					Motion motion = Motion.findById(Motion.class, id);
					String actor=request.getParameter("actor");
					String level=request.getParameter("level");
					if(actor!=null&&!actor.isEmpty()
							&&level!=null&&!level.isEmpty()){
						Reference reference;
						try {
							reference = UserGroup.findMotionActor(motion,actor,level,locale.toString());
							
							if(reference!=null
									&&reference.getId()!=null&&!reference.getId().isEmpty()
									&&reference.getName()!=null&&!reference.getName().isEmpty()){
								/**** Update Actor ****/
								String[] temp=reference.getId().split("#");
								motion.setActor(reference.getId());
								motion.setLocalizedActorName(temp[3]+"("+temp[4]+")");
								motion.setLevel(temp[2]);
								/**** Update Internal Status and Recommendation Status ****/
								motion.setInternalStatus(status);
								motion.setRecommendationStatus(status);	
								motion.setEndFlag("continue");
								/**** Create Process ****/
								ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
								Map<String,String> properties=new HashMap<String, String>();					
								properties.put("pv_user",temp[0]);						
								properties.put("pv_endflag",motion.getEndFlag());	
								properties.put("pv_deviceId",String.valueOf(motion.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(motion.getType().getId()));
								ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
								/**** Create Workdetails Entry ****/
								Task task=processService.getCurrentTask(processInstance);
								if(motion.getEndFlag()!=null&&!motion.getEndFlag().isEmpty()&&motion.getEndFlag().equals("continue")){
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

									if(recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
									} 
									else if(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING)
											|| internalStatusType.equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)
											|| (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
										workflow = Workflow.findByStatus(internalStatus, locale.toString());
									} 
									else {
										workflow = Workflow.findByStatus(internalStatus, locale.toString());
									}

									String workflowType = workflow.getType();
									String assigneeLevel = motion.getLevel();
									WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, assigneeLevel); 
									//workflowDetails = WorkflowDetails.create(motion, task, workflowType, assigneeLevel);
									/*
									 * Added by Amit Desai 2 Dec 2014
									 * ... END
									 */
									
									
									if(refs != null && !refs.isEmpty()) {
										motion.setReferencedUnits(refs);
									}
									if(remarks != null){
										motion.setRemarks(remarks);
									}
									if(refText != null){
										motion.setRefText(refText);
									}
									
									String strFile = request.getParameter("file");
									if(strFile != null && !strFile.isEmpty()){
										if(motion.getFile() == null){
											motion.setFile(new Integer(strFile));
										}
									}
									
									motion.setWorkflowDetailsId(workflowDetails.getId());
									/**** Workflow Started ****/
									motion.setWorkflowStarted("YES");
									motion.setWorkflowStartedOn(new Date());
									motion.setTaskReceivedOn(new Date());
									motion.setFileSent(true);
									motion.simpleMerge();
								}	
								if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION)){
									recommendAdmission.append(motion.formatNumber()+",");
								}else if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_REJECTION)){
									recommendRejection.append(motion.formatNumber()+",");
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
		getBulkSubmissionMotions(model,request,locale.toString());
		return "motion/bulksubmissionassistantview";
	}
	
	public void getBulkSubmissionMotions(final Model model,final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strMotionType=request.getParameter("motionType");			
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
			List<Motion> motions=new ArrayList<Motion>();
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
				DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
				if(strFile!=null&&!strFile.isEmpty()&&!strFile.equals("-")){
					Integer file=Integer.parseInt(strFile);
					motions=Motion.findAllByFile(session,motionType,file,strLocale);
				}else if(strItemsCount!=null&&!strItemsCount.isEmpty()){
					Integer itemsCount=Integer.parseInt(strItemsCount);
					Status internalStatus=Status.findById(Status.class,Long.parseLong(strStatus));
					motions=Motion.findAllByStatus(session,motionType,internalStatus,itemsCount,strLocale);
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
		Motion motion=Motion.findById(Motion.class, id);
		model.addAttribute("details",motion.getDetails());
		return "motion/details";
	}	
	
	/**** Assign Number to motion after ballot ****/
	/**
	 * @param request
	 * @param locale
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/assignpostballotnumber", method=RequestMethod.GET)
	public @ResponseBody String assignPostBallotNumber(final HttpServletRequest request, final Locale locale){
		String retVal = "failure";
		try{
			String strDeviceType = request.getParameter("deviceType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strHouseType = request.getParameter("houseType");
			
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
			SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			Integer sessionYear = new Integer(strSessionYear);
			
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			
			Motion.assignPostBallotNumber(session, deviceType, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		return retVal;
	}
	
	
	@Transactional
	@RequestMapping(value="/assignnumber", method=RequestMethod.GET)
	public String assignNumber(HttpServletRequest request, ModelMap model, Locale locale){
		String retVal = "motion/error";
		model.addAttribute("errorcode", "info");
		try{
			String strSession = request.getParameter("session");
			String strDeviceType = request.getParameter("deviceType");
			
			if(strSession != null && !strSession.isEmpty()
					&& strDeviceType != null && !strDeviceType.isEmpty()){
				
				Session session = Session.findById(Session.class, new Long(strSession));
				DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				/**** Get member by their position from ballot ****/
				List<MemberBallot> memBallots = MemberBallot.findBySessionDeviceType(session, deviceType, locale.toString());
				
				/****  find first batch and second batch start and end time of submission ****/
				Date firstBatchStartTime = FormaterUtil.formatStringToDate(session.getParameter(ApplicationConstants.MOTION_FIRST_BATCH_START_TIME), ApplicationConstants.SERVER_DATETIMEFORMAT);
				Date firstBatchEndTime = FormaterUtil.formatStringToDate(session.getParameter(ApplicationConstants.MOTION_FIRST_BATCH_END_TIME), ApplicationConstants.SERVER_DATETIMEFORMAT);
				
				Date secondBatchStartTime = FormaterUtil.formatStringToDate(session.getParameter(ApplicationConstants.MOTION_SECOND_BATCH_START_TIME), ApplicationConstants.SERVER_DATETIMEFORMAT);
				Date secondBatchEndTime = FormaterUtil.formatStringToDate(session.getParameter(ApplicationConstants.MOTION_SECOND_BATCH_END_TIME), ApplicationConstants.SERVER_DATETIMEFORMAT);
				/****  find first batch and second batch start and end time of submission ****/
				
				/****reset counter to 1 and start assigning the postBallotNumber as per ballot position ****/
				int counter = 0;
						//Motion.findMaxPostBallotNo(session.getHouse().getType(), session, deviceType, locale.toString());				
				for (MemberBallot mbal : memBallots) {
					if (mbal.getMember() != null) {
						List<Motion> motions = Motion.findAllByMemberBatchWise(session, mbal.getMember(), deviceType, firstBatchStartTime, firstBatchEndTime, locale.toString());

						for (Motion m : motions) {
							
							counter++;
							m.setPostBallotNumber(counter);
							m.simpleMerge();
							
						}
					}
				}
				
				if(counter >= ApplicationConstants.MOTION_FIRST_BATCH_START_COUNTER){
					/**** find all second batch motions and assign the incremental postBallotNumbers to them ****/
					List<Motion> allMotions = Motion.findAllByBatch(session, deviceType, secondBatchStartTime, secondBatchEndTime, locale.toString());
					for(Motion m : allMotions){
						
							counter++;
							m.setPostBallotNumber(counter);
							m.simpleMerge();
						
					}	
				}
				
				/****Show success or failure message****/
				if(counter >= ApplicationConstants.MOTION_FIRST_BATCH_START_COUNTER){
					model.addAttribute("errorcode", "numberassignment_success");
				}else{
					model.addAttribute("errorcode", "numberassignment_failure");
				}
			}
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "general_error");
		}
		return retVal;
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/discussionselection", method = RequestMethod.GET)
	public String getDiscussionSelection(final HttpServletRequest request, final ModelMap model, final Locale locale){
		String retVal = "motion/error";
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strMotionType = request.getParameter("motionType");
			String strUser = request.getParameter("ugparam");
			String strStatus = request.getParameter("status");
			String strRole = request.getParameter("role");
			String strUsergroup = request.getParameter("usergroup");
			String strUsergroupType = request.getParameter("usergroupType");
			
			Session session = null;
			if(strHouseType != null && !strHouseType.isEmpty()
					&& strSessionYear != null && ! strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()){
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Integer sessionYear = new Integer(strSessionYear);
				SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				
				if(houseType != null && sessionYear != null && sessionType != null){
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					
					if(session != null){
						model.addAttribute("session", session.getId());
					}
				}				
			}
			
			String userHouseType = this.getCurrentUser().getHouseType();
			
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			if (userHouseType.equals("lowerhouse")) {
				houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", userHouseType, "name",ApplicationConstants.ASC, locale.toString());
			} else if (userHouseType.equals("upperhouse")) {
				houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", userHouseType, "name",ApplicationConstants.ASC, locale.toString());
			} else if (userHouseType.equals("bothhouse")) {
				houseTypes = HouseType.findAll(HouseType.class, "type",ApplicationConstants.ASC, locale.toString());
			}
			model.addAttribute("houseTypes", houseTypes);
			if (userHouseType.equals("bothhouse")) {
				userHouseType = "lowerhouse";
			}
			model.addAttribute("houseType", userHouseType);

			/**** Session Types. ****/
			List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType",ApplicationConstants.ASC, locale.toString());
			/**** Latest Session of a House Type ****/
			HouseType authUserHouseType = HouseType.findByFieldName(HouseType.class, "type", userHouseType, locale.toString());
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
			List<MasterVO> years = new ArrayList<MasterVO>();
			if (houseFormationYear != null) {
				Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
				for (int i = year; i >= formationYear; i--) {
					MasterVO yearVO = new MasterVO();
					yearVO.setNumber(i);
					yearVO.setValue(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
					years.add(yearVO);
				}
			} else {
				model.addAttribute("errorcode", "houseformationyearnotset");
			}
			model.addAttribute("years", years);
			model.addAttribute("sessionYear", year);
			
			/**** Load Parties Start ****/
			List<Party> parties = Party.findActiveParties(locale.toString());
			model.addAttribute("parties", parties);
			
			/**** Load Parties End ****/
			Date sessionStartDate = session.getStartDate();
			Date sessionEndDate = session.getEndDate();
			List<Reference> references = new ArrayList<Reference>();

			if((sessionStartDate != null) && (sessionStartDate != null)){
				Calendar start = Calendar.getInstance();

				Calendar end = Calendar.getInstance();

				List<Date> dates = new ArrayList<Date>();

				start.setTime(sessionStartDate);
				end.setTime(sessionEndDate);

				for (; !start.after(end); start.add(Calendar.DATE, 1)) {
					Date current = start.getTime();
					if(!Holiday.isHolidayOnDate(current, locale.toString())){
						dates.add(current);
					}
				}
				//--------------------------------------------------------

				Collections.sort(dates);

				for(Date date: dates){

					Reference reference = new Reference();

					reference.setId(FormaterUtil.formatDateToString(date, ApplicationConstants.SERVER_DATEFORMAT));
					reference.setName(FormaterUtil.formatDateToString(date, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));

					references.add(reference);    
				}
			}
						
			model.addAttribute("currDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT));
			
			model.addAttribute("sessionStartDate", FormaterUtil.formatDateToString(sessionStartDate, ApplicationConstants.SERVER_DATEFORMAT));
			model.addAttribute("formattedSessionStartDate", FormaterUtil.formatDateToString(sessionStartDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			
			model.addAttribute("sessionEndDate", FormaterUtil.formatDateToString(sessionEndDate, ApplicationConstants.SERVER_DATEFORMAT));
			model.addAttribute("formattedSessionEndDate", FormaterUtil.formatDateToString(sessionEndDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			model.addAttribute("sessionDates", references);
			/**** Load Session Dates ****/
			
			DeviceType deviceType = null;
			if(strMotionType != null && !strMotionType.isEmpty()){
				deviceType = DeviceType.findById(DeviceType.class, new Long(strMotionType));
			}
			
			model.addAttribute("deviceType", deviceType.getId());
			
			List<DiscussionDateDevice> discussionDateDevices = DiscussionDateDevice.findBySessionDeviceType(session, deviceType, ApplicationConstants.DESC, locale.toString());
			Map<String, String> discussionDates = new HashMap<String, String>();
			for(DiscussionDateDevice d : discussionDateDevices){
				if(d.getDiscussionDate() != null){
					discussionDates.put(FormaterUtil.formatDateToString(d.getDiscussionDate(), ApplicationConstants.SERVER_DATEFORMAT), d.getDevices());
				}
			}
			model.addAttribute("discussionDateMap", discussionDates);
			
			/**** Load Motions ****/
			Status admitted = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale.toString());
			List<Motion> motions = Motion.findAllForDiscussion(lastSessionCreated, deviceType, admitted, locale.toString());
			List<Reference> motionRefs = new ArrayList<Reference>();
			for(Motion m : motions){
				Reference ref = new Reference();
				ref.setId(m.getId().toString());
				if(m.getNumber() != null){
					ref.setName(m.getNumber().toString());
					ref.setName(FormaterUtil.formatNumberNoGrouping(m.getNumber(), locale.toString()));
				}else if(m.getPostBallotNumber() != null){
					ref.setName(m.getPostBallotNumber().toString());
					ref.setName(FormaterUtil.formatNumberNoGrouping(m.getPostBallotNumber(), locale.toString()));
				}
				motionRefs.add(ref);
			}
			model.addAttribute("motions", motionRefs);
			/**** Load Motions ****/
						
			retVal = "motion/discussionselection";
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode","GENERAL_ERROR");
		}
		
		return retVal;
	}
	
	@Transactional
	@RequestMapping(value = "/discussionselection", method=RequestMethod.POST)
	public String postDiscussionSelection(final HttpServletRequest request, final ModelMap model, final Locale locale){
		String retVal = "motion/error";
		try{
			/*String strHouseType = request.getParameter("houseType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");*/
			
			String strSession = request.getParameter("session");
			String strDeviceType = request.getParameter("deviceType");
			String strData = request.getParameter("devices");
			String strDate = request.getParameter("discussDate");
			
			Session session = null;
			/*if(strHouseType != null && !strHouseType.isEmpty()
					&& strSessionYear != null && ! strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()){
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Integer sessionYear = new Integer(strSessionYear);
				SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				
				if(houseType != null && sessionYear != null && sessionType != null){
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					
					if(session != null){
						model.addAttribute("session", session.getId());
					}
				}				
			}*/
			Date discussDate = null;
			if(strDate != null){
				discussDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT);
			}
			session = Session.findById(Session.class, new Long(strSession));
			
			DeviceType deviceType = null;
			if(strDeviceType != null && !strDeviceType.isEmpty()){
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
			}
			
			DiscussionDateDevice discussionDateDevice = DiscussionDateDevice.findBySessionDeviceTypeDate(session, deviceType, new Date(), locale.toString());
			if(discussionDateDevice == null){
				discussionDateDevice = new DiscussionDateDevice();
				discussionDateDevice.setLocale(locale.toString());
				discussionDateDevice.setDeviceType(deviceType);
				discussionDateDevice.setSession(session);
				discussionDateDevice.setDevices(strData);
				discussionDateDevice.setDiscussionDate(discussDate);
				discussionDateDevice.persist();
			}else if(discussionDateDevice != null){
								
				if(discussionDateDevice.getLocale() == null){
					discussionDateDevice.setLocale(locale.toString());
				}
				if(discussionDateDevice.getDeviceType() == null){
					discussionDateDevice.setDeviceType(deviceType);
				}
				if(discussionDateDevice.getSession() == null){
					discussionDateDevice.setSession(session);
				}
				if(strData.isEmpty()){
					discussionDateDevice.setDevices(null);
				}else{
					
					String strDev = discussionDateDevice.getDevices();
					for(String s : strDev.split("~")){
						if(s != null){
							if(!strData.contains(s)){
								String strId = s.split(":")[0];
								Motion m = Motion.findById(Motion.class, new Long(strId));
								if(m != null){
									m.setDiscussionDate(null);
									m.simpleMerge();
								}
							}
						}
					}
					
					discussionDateDevice.setDevices(strData);
				}
				if(discussionDateDevice.getDiscussionDate() == null){
					discussionDateDevice.setDiscussionDate(discussDate);
				}
				discussionDateDevice.merge();
			}
			
			if(strData != null && !strData.isEmpty()){
				String[] strDevs = strData.split("~");
				for(String s : strDevs){
					String strId = s.split(":")[0];
					Motion m = Motion.findById(Motion.class, new Long(strId));
					if(m != null){
						m.setDiscussionDate(discussDate);
						m.simpleMerge();
					}
				}
			}
			
			retVal = "redirect:/motion/discussionselection";
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode","GENERAL_ERROR");
		}
		
		return retVal;
	}
	
	//---------------------------Status update------------------
	/**** Yaadi to discuss update ****/
	@RequestMapping(value="/statusupdate/assistant/init", method=RequestMethod.GET)
	public String getStatusUpdateInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Request Params ****/
		String retVal = "motion/error";
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
			
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class, "MOTION_STATUS_UPDATE_" + deviceType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() + "_" + strUsergroupType.toUpperCase(), "");

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

			retVal = "motion/statusupdateinit";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}
	
	@RequestMapping(value="/statusupdate/assistant/view", method=RequestMethod.GET)
	public String getStatusUpdateAssistantView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		this.getStatusUpdateMotions(model, request, locale.toString());
		return "motion/statusupdateassistantview";
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
						Motion motion = Motion.findById(Motion.class, id);
						Status status = Status.findById(Status.class, new Long(strDecisionStatus));
						if(status.getType().equals(ApplicationConstants.MOTION_PROCESSED_ANSWER_RECEIVED)){
							if(strDate!= null && !strDate.isEmpty()){
								Date replyReceivedDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								motion.setReplyReceivedDate(replyReceivedDate);
							}
						}else{
							if(strDate!= null && !strDate.isEmpty()){
								Date discussionDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								if(status.getType().equals(ApplicationConstants.MOTION_PROCESSED_DISCUSSED)) {
									motion.setDiscussionDate(discussionDate);
								} else {
									motion.setAnsweringDate(discussionDate);
								}
							}
						}
						motion.setRecommendationStatus(status);
						
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
			this.getStatusUpdateMotions(model, request, locale.toString());
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "motion/statusupdateview";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	
	private void getStatusUpdateMotions(final ModelMap model,
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
			List<Motion> motions = new ArrayList<Motion>();
		
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
		
		
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));				
				
				Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));
				motions = Motion.findAllAdmittedUndisccussed(session, deviceType, internalStatus, locale);
		
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
	//---------------------------Status update------------------
	
	//--Similar nature submission
	@RequestMapping(value = "filing/{id}/{file}/enter", method = RequestMethod.GET)
	public @ResponseBody String filing(@PathVariable("id") Long id, @PathVariable("file") Integer file, HttpServletRequest request, Locale locale){
		
		String retVal = "FAILURE";
		
		try{
			Motion sm = Motion.findById(Motion.class, id);
			
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
	
	@RequestMapping(value = "advancecopy", method = RequestMethod.GET)
	public String getAdvanceCopyMotions(final ModelMap model,
	final HttpServletRequest request, 
	final Locale locale) {
		if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
        	model.addAttribute("type",request.getSession().getAttribute("type"));
            request.getSession().removeAttribute("type");
        }
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strAssignee = request.getParameter("assignee");	
		try {
			CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(strHouseType == null || strHouseType.isEmpty()
				|| strSessionType == null ||strSessionType.isEmpty()
				|| strSessionYear == null || strSessionYear.isEmpty()
				|| strAssignee == null || strAssignee.isEmpty()){
				strHouseType = request.getSession().getAttribute("houseType").toString();
				strSessionType = request.getSession().getAttribute("sessionType").toString();
				strSessionYear = request.getSession().getAttribute("sessionYear").toString();
				strAssignee = request.getSession().getAttribute("assignee").toString();
			}
			if(csptDeployment!=null){
				if(csptDeployment.getValue().equals("TOMCAT")){
					strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
					strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
					strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
				}
			}
			if(strHouseType != null && !(strHouseType.isEmpty())
					&& strSessionType != null && !(strSessionType.isEmpty())
					&& strSessionYear != null && !(strSessionYear.isEmpty())) {
					SessionType sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
					HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
					Integer year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
					model.addAttribute("session",session.getId());
					List<Ministry> ministries = Ministry.findAssignedMinistriesInSession(session.getStartDate(), locale.toString());
					model.addAttribute("ministries", ministries);
					List<SubDepartment> subdepartmentList = SubDepartment.findAllSubDepartments(locale.toString());
					model.addAttribute("subdepartments", subdepartmentList);
					Credential credential = Credential.findByFieldName(Credential.class, "username", strAssignee, null);
					UserGroup userGroup = UserGroup.findActive(credential, new Date(), locale.toString());
					Map<String, String[]> parameterMap = new HashMap<String, String[]>();
					parameterMap.put("locale", new String[]{locale.toString()});
					parameterMap.put("sessionId", new String[]{session.getId().toString()});
					UserGroupType ugt = null;
					if(userGroup != null){
						model.addAttribute("usergroup", userGroup.getId());
						ugt =  userGroup.getUserGroupType();
						if(ugt.getType().equals(ApplicationConstants.DEPARTMENT)){
							parameterMap.put("actor", new String[]{""});
							Status admissionStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale.toString());
							model.addAttribute("admissionStatus",admissionStatus.getId());
						}else{
							parameterMap.put("actor", new String[]{credential.getUsername()});
							Status admissionStatus = Status.findByType(ApplicationConstants.MOTION_PROCESSED_SENDTODESKOFFICER, locale.toString());
							model.addAttribute("admissionStatus",admissionStatus.getId());
						}
						
						//Statuses to populate
						CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.
								findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_ADVANCE_COPY_"+ugt.getType().toUpperCase(), "");
						List<Status> internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale.toString());
						model.addAttribute("internalStatuses", internalStatuses);
						
						
						
						model.addAttribute("level", 10);
						
						Map<String, String> usergroupParameters = userGroup.getParameters();
						String strSubdepartment = usergroupParameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_"+locale.toString());
						String subdepartmentIds = new String();
						if(strSubdepartment != null && !strSubdepartment.isEmpty()){
							String subdepartments[] = strSubdepartment.split("##");
							for(int i = 0;i<subdepartments.length;i++){
								SubDepartment subdepartment = SubDepartment.findByName(SubDepartment.class, subdepartments[i], locale.toString());
								if(subdepartment != null){
									subdepartmentIds+=subdepartment.getId();
									if(i+1<subdepartments.length){
										subdepartmentIds+=",";
									}
								}
							}
							parameterMap.put("subdepartments", new String[]{subdepartmentIds});
						}
					}
					List<Object[]> result = Query.findReport("MOIS_ADVANCE_COPY_MOTIONS", parameterMap, true);
					/**** Populating Bulk Approval VOs ****/
					List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
					if(result != null){
						for(Object[] o: result) {
							BulkApprovalVO bulkApprovalVO = new BulkApprovalVO();
							if(o[0] != null){
								bulkApprovalVO.setDeviceId(o[0].toString());
							}
							if(o[1]!= null){
								bulkApprovalVO.setDeviceNumber(o[1].toString());
							}
							if(o[2]!= null){
								bulkApprovalVO.setSubject(o[2].toString());
							}
							if(o[3]!= null){
								bulkApprovalVO.setSubdepartmentId(Long.parseLong(o[3].toString()));
							}
							if(o[4]!= null){
								bulkApprovalVO.setMinistryId(Long.parseLong(o[4].toString()));
							}
							if(o[5] != null){
								bulkApprovalVO.setMlsBranchNotified(Boolean.parseBoolean(o[5].toString()));
							}
							if(o[6] != null){
								bulkApprovalVO.setTransferDepartmentAccepted(Boolean.parseBoolean(o[6].toString()));
							}
							if(o[7] != null){
								bulkApprovalVO.setAdvanceCopySent(Boolean.parseBoolean(o[7].toString()));
							}
							if(o[8] != null){
								bulkApprovalVO.setAdvanceCopyPrinted(Boolean.parseBoolean(o[8].toString()));
							}
							if(ugt.getType().equals(ApplicationConstants.DEPARTMENT)){
								if(o[9] != null){
									bulkApprovalVO.setAdvanceCopyActor(o[9].toString());
								}
							}else{
								if(o[9] != null && o[8] != null){
									bulkApprovalVO.setAdvanceCopyActor(o[9].toString());
								}else{
									bulkApprovalVO.setAdvanceCopyActor("");
								}
							}
							bulkapprovals.add(bulkApprovalVO);
						}
						
					}
					model.addAttribute("bulkapprovals",bulkapprovals);
				
			} 
		
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			e.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		} catch (ParseException ex2) {
			ex2.printStackTrace();
		}
		return "motion/approval";
	}
	
	
	
	@RequestMapping(value = "advancecopy", method = RequestMethod.POST)
	public String saveAdvanceCopyMotions(final ModelMap model,
	final HttpServletRequest request, 
	final RedirectAttributes redirectAttributes,
	final Locale locale) {
		String listSize = request.getParameter("motionlistSize");
		Motion tempMotion  = null;
		if(listSize != null && !listSize.isEmpty()){
			for(int i =0; i<Integer.parseInt(listSize);i++){
				String strMotionId = request.getParameter("motionId"+i);
				String actor = request.getParameter("actor"+i);
				String remark = request.getParameter("remark"+i);
				String strChecked = request.getParameter("chk"+i);
				String strUsergroup = request.getParameter("usergroup");
				String strMinistry = request.getParameter("ministry"+i);
				String strSubdepartment = request.getParameter("subdepartment"+i);
				String strTransferAccepted = request.getParameter("transferToDepartmentAccepted"+i);
				String strMlsNotified = request.getParameter("mlsBranchNotifiedOfTransfer"+i);
				String strStatus = request.getParameter("internalStatus"+i);
				
				if(strChecked != null && !strChecked.isEmpty() && Boolean.parseBoolean(strChecked)){
					Motion motion = Motion.findById(Motion.class,Long.parseLong(strMotionId));
					tempMotion = motion;
					if(remark != null && !remark.isEmpty()){
						motion.setRemarks(remark);
					}
					
					/***Ministry and Subdepartment ****/
					if(strMinistry != null && !strMinistry.isEmpty()){
						Ministry ministry = Ministry.findById(Ministry.class, Long.parseLong(strMinistry));
						motion.setMinistry(ministry);
						if(strSubdepartment != null && !strSubdepartment.isEmpty()){
							SubDepartment subdepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubdepartment));
							motion.setSubDepartment(subdepartment);
						}
					}
					if(strTransferAccepted != null && !strTransferAccepted.isEmpty()){
						motion.setTransferToDepartmentAccepted(true);
					}
					if(strMlsNotified != null && !strMlsNotified.isEmpty()){
						motion.setMlsBranchNotifiedOfTransfer(true);
						motion.setAdvanceCopyActor(null);
						motion.setAdvanceCopyPrinted(false);
					}
					/**** Update Advance Copy Actor ****/
					if(actor != null && !actor.isEmpty() && (strMlsNotified == null || strMlsNotified.equals(""))){
						motion.setAdvanceCopyActor(actor);	
						NotificationController.sendDepartmentProcessNotificationForMotion(motion, actor, "advanceCopy", motion.getLocale());
					}
					if(strStatus != null && !strStatus.isEmpty()){
						Status advanceStatus = Status.findById(Status.class, Long.parseLong(strStatus));
						if(advanceStatus.getType().equals(ApplicationConstants.MOTION_SYSTEM_ADVANCECOPYRECEIVED)){
							motion.setAdvanceCopyPrinted(true);
						}
					}
					
					/**** Update Motion ****/
					motion.setEditedOn(new Date());
					motion.setEditedBy(this.getCurrentUser().getActualUsername());
					UserGroup usergroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUsergroup));
					motion.setEditedAs(usergroup.getUserGroupType().getType());				

					motion.merge();
				}

			}
		}
		
		if(tempMotion != null){
			request.getSession().setAttribute("houseType", tempMotion.getHouseType().getName());
			request.getSession().setAttribute("sessionType", tempMotion.getSession().getType().getSessionType());
			request.getSession().setAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(tempMotion.getSession().getYear(), locale.toString()));
			request.getSession().setAttribute("assignee", this.getCurrentUser().getActualUsername());
		}
		redirectAttributes.addFlashAttribute("type", "success");
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/motion/advancecopy";
        return returnUrl;
	}
	
	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {
		Motion motion=Motion.findById(Motion.class, id);
		if(motion!=null){
			Status status=motion.getStatus();
			if(status.getType().equals(ApplicationConstants.MOTION_INCOMPLETE)||status.getType().equals(ApplicationConstants.MOTION_COMPLETE)){
				Motion.supportingMemberWorkflowDeletion(motion);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public static HouseType getHouseType(final HttpServletRequest request,
			final String locale) throws ELSException{
		String strHouseType = request.getParameter("houseType");
		
		if(strHouseType == null || strHouseType.isEmpty()) {
			throw new ELSException("MotionController.getHouseType/2", "HouseType is not set in the Request");
		}
		HouseType houseType = HouseType.findByType(strHouseType, locale);
		
		return houseType;
	}
	
	public static SessionType getSessionType(final HttpServletRequest request,
			final String locale) throws ELSException {
		String strSessionType = request.getParameter("sessionType");
		
		if(strSessionType == null || strSessionType.isEmpty()) {
			throw new ELSException("MotionController.getSessionType/2", "sessionType is not set in the Request");
		}
		SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
		
		return sessionType;
	}
	
	public static Integer stringToIntegerYear(final HttpServletRequest request,
			final String locale) throws ELSException{
		Integer sessionYear = null;
		String selectedYear = request.getParameter("sessionYear");
		if(selectedYear != null && !selectedYear.isEmpty()) {
			sessionYear = Integer.parseInt(selectedYear);
			return sessionYear;
		}else{
			throw new ELSException("MotionController.stringToIntegerYear/2", 
					"Session Year is not set in request "); 
		}
		
	}
	
	public static DeviceType getDeviceTypeById(HttpServletRequest request,
			String locale) throws ELSException {
		String deviceTypeId = request.getParameter("motionType");
		
		if(deviceTypeId == null){
			deviceTypeId = (String)request.getSession().getAttribute("motionType");
		}
		
		if(deviceTypeId == null || deviceTypeId.isEmpty()) {
			throw new ELSException("MotionController.getDeviceType/2", 
					"Device type is not set in the Request");
		}
		
		DeviceType deviceType = DeviceType.findById(DeviceType.class,Long.parseLong(deviceTypeId));
		return deviceType;
	}
	
	public void populateMaxLengthParameters(HouseType houseType,String role,DeviceType motionType,ModelMap model) {
		StringBuffer userRoleMultipleBuff=new StringBuffer();
		String userRoleStr ="";
		if(role==null || role.trim().length()<=0) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(authentication!=null && authentication.getAuthorities()!=null) {
				Collection<? extends GrantedAuthority> userRoles = authentication.getAuthorities();
				for(GrantedAuthority ur:userRoles) {
					if(ur!=null && ur.getAuthority()!=null && ur.getAuthority().trim().length()>0) {
						userRoleMultipleBuff=userRoleMultipleBuff.append(ur.getAuthority().trim()).append(",");
					}
				}
				
				userRoleStr=userRoleMultipleBuff.substring(0,
				userRoleMultipleBuff.lastIndexOf(","));
			}
		}
		
		if(houseType!=null && ApplicationConstants.UPPER_HOUSE.equalsIgnoreCase(houseType.getType())
				&& ((role!=null && role.trim().length()>0 && ApplicationConstants.MEMBER_UPPERHOUSE.equalsIgnoreCase(role))
						|| userRoleStr.contains(ApplicationConstants.MEMBER_UPPERHOUSE))
				&& ApplicationConstants.MOTION_CALLING_ATTENTION.equalsIgnoreCase(motionType.getType())) {
			CustomParameter maxAllowedTextSizeObj = CustomParameter.findByName(CustomParameter.class, "MOTIONS_CALLING_ATTENTION_MAX_TEXT_LENGTH", "");
			CustomParameter externalLinkObj = CustomParameter.findByName(CustomParameter.class, "MOTIONS_PATRAK_EXTERNAL_LINK", "");
			if(maxAllowedTextSizeObj!=null && maxAllowedTextSizeObj.getValue()!=null 
					&& maxAllowedTextSizeObj.getValue().trim().length()>0) {
				model.addAttribute("maxAllowedTextSize",Integer.valueOf(maxAllowedTextSizeObj.getValue().trim()));
				if(externalLinkObj!=null && externalLinkObj.getValue()!=null) {
					model.addAttribute("patrakExternalLink", externalLinkObj.getValue().trim());
				}
			}else {
				model.addAttribute("maxAllowedTextSize",-1);
			}
		}else {
			model.addAttribute("maxAllowedTextSize",-1);
		}
	}

}
