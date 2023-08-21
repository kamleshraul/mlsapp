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
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.PartyType;
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
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
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

@Controller
@RequestMapping("discussionmotion")
public class DiscussionMotionController extends GenericController<DiscussionMotion>{

	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {
		
		model.addAttribute("moduleLocale", locale.toString());
		
		/**** Selected Motion Type ****/
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Available Motion Types ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			try {
				deviceTypes = DeviceType.findDeviceTypesStartingWith("motions_discussionmotion_", locale);

				model.addAttribute("motionTypes", deviceTypes);
				/**** Default Value ****/
				model.addAttribute("motionType", deviceType.getId());
				/**** Access Control Based on Motion Type ****/
				model.addAttribute("motionTypeType", deviceType.getType());

				/**** House Types ****/
				// Populate House types configured for the current user
				List<HouseType> houseTypes=null;
				try {
					houseTypes = DiscussionMotionController.getHouseTypes(currentUser, deviceType, locale);
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
						authUserHouseType = DiscussionMotionController.getHouseType(currentUser, locale);
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
				Session lastSessionCreated  = Session.findLatestSession(authUserHouseType);

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

				/****
				 * Custom Parameter To Determine The Usergroup and usergrouptype
				 * of mois users . here we are determining what status will be
				 * shown to a particular user.
				 ****/
				UserGroup userGroup = null;
				UserGroupType userGroupType = null;
				List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
				if (userGroups != null) {
					if (!userGroups.isEmpty()) {
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DMOIS_ALLOWED_USERGROUPTYPES", "");
						if (customParameter != null) {
							String allowedUserGroups = customParameter.getValue();
							List<UserGroupType> configuredUserGroupTypes = 
									QuestionController.delimitedStringToUGTList(allowedUserGroups, ",", locale);
							
							userGroup = QuestionController.getUserGroup(userGroups, configuredUserGroupTypes, lastSessionCreated, locale);
							if(userGroup==null) {
								lastSessionCreated = Session.findPreviousSessionInSameHouseForGivenDeviceTypeEnabled(lastSessionCreated, deviceType);
								if(lastSessionCreated!=null) {
									userGroup = QuestionController.getUserGroup(userGroups, configuredUserGroupTypes, lastSessionCreated, locale);
								}
							}
							userGroupType = userGroup.getUserGroupType();							
							model.addAttribute("usergroup", userGroup.getId());
							model.addAttribute("usergroupType", userGroupType.getType());
							
							CustomParameter allowedStatus = CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_GRID_STATUS_ALLOWED_"+ userGroupType.getType().toUpperCase(),"");
							List<Status> status = new ArrayList<Status>();
							if (allowedStatus != null) {
								status = Status.findStatusContainedIn(allowedStatus.getValue(),locale);
							} else {
								CustomParameter defaultAllowedStatus = CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT","");
								if (defaultAllowedStatus != null) {
									status = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
								} else {
									model.addAttribute("errorcode","motion_status_allowed_by_default_not_set");
								}
							}
							model.addAttribute("status", status);
						} else {
							model.addAttribute("errorcode","dmois_allowed_usergroups_notset");
						}
					} else {
						model.addAttribute("errorcode","current_user_has_no_usergroups");
					}
				} else {
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}									
				
				/****
				 * Roles and ugparam.Role will be used to decide who can create
				 * new motions(member and typist).for member and clerk only those
				 * motions will be visible which are created by them.For other
				 * mois users all motions will be visible.
				 ****/
				Set<Role> roles = this.getCurrentUser().getRoles();
				for (Role i : roles) {
					if (i.getType().startsWith("MEMBER_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().contains("DMOIS_TYPIST")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().startsWith("DMOIS_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					}
				}
				/**** File Options(Obtain Dynamically) ****/
				if (userGroupType != null && userGroupType.getType().equals("assistant")) {
					int highestFileNo = DiscussionMotion.findHighestFileNo(lastSessionCreated, deviceType, locale);
					model.addAttribute("highestFileNo", highestFileNo);
				}
			} catch (ELSException e) {
				model.addAttribute("DiscussionMotionController", e.getParameter());
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
		CustomParameter assistantGridAllowedFor=CustomParameter.findByName(CustomParameter.class,"DMOIS_ASSISTANTGRID_ALLOWED_FOR","");
		CustomParameter memberGridAllowedFor=CustomParameter.findByName(CustomParameter.class,"DMOIS_MEMBERGRID_ALLOWED_FOR","");
		CustomParameter typistGridAllowedFor=CustomParameter.findByName(CustomParameter.class,"DMOIS_TYPISTGRID_ALLOWED_FOR","");
		if(memberGridAllowedFor != null
				&& role != null && !role.isEmpty() && houseType != null
				&& !houseType.isEmpty() && memberGridAllowedFor.getValue().contains(role)){
			newUrlPattern = urlPattern+"?usergroup=member"; //&houseType="+houseType;
		}else if(typistGridAllowedFor != null && role != null 
				&& !role.isEmpty() && houseType != null && !houseType.isEmpty()
				&& typistGridAllowedFor.getValue().contains(role)){
			newUrlPattern = urlPattern+"?usergroup=typist"; //&houseType="+houseType;
		}else if(assistantGridAllowedFor != null && role != null && !role.isEmpty()
				&& houseType != null && !houseType.isEmpty() && assistantGridAllowedFor.getValue().contains(role)){
			
			newUrlPattern = urlPattern+"?usergroup=assistant"; //&houseType="+houseType;
			
		}	
		/**** Controlling Grids Ends ****/
		return newUrlPattern;
	}
	
	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		/**** New Operations Allowed For Starts ****/
		String role = request.getParameter("role");	
		CustomParameter newOperationAllowedTo = CustomParameter.findByName(CustomParameter.class,"DMOIS_NEW_OPERATION_ALLOWED_TO","");
		if(newOperationAllowedTo != null 
				&& role != null && !role.isEmpty()
				&& newOperationAllowedTo.getValue().contains(role)){
			return servletPath;			
		}		
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
		/**** New Operations Allowed For Starts ****/
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
		
		CustomParameter editPage = CustomParameter.findByName(CustomParameter.class, "DMOIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.findByName(CustomParameter.class, "DMOIS_EDIT_OPERATION_ASSISTANT_PAGE", "");
		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return newUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return newUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("DMOIS_")) {
				return newUrlPattern.replace("edit", "editreadonly");
			}
		}		
		model.addAttribute("errorcode","permissiondenied");
		return "discussionmotion/error";
		/**** Edit Page Ends ****/
	}
	
	
	@Override
	protected void populateNew(final ModelMap model, DiscussionMotion domain, final String locale,
			final HttpServletRequest request) {

		/**** Locale ****/
		domain.setLocale(locale);

		/**** Subject and Details ****/
		String subject=request.getParameter("subject");
		if(subject!=null){
			domain.setSubject(subject);
		}
		String noticeContent=request.getParameter("noticeContent");
		if(noticeContent!=null){
			domain.setNoticeContent(noticeContent);
		}	

		/**** House Type ****/
		String selectedHouseType=request.getParameter("houseType");
		HouseType houseType=null;
		if(selectedHouseType!=null){
			if(!selectedHouseType.isEmpty()){
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
		String selectedDiscussionMotionType=request.getParameter("discussionMotionType");
		if(selectedDiscussionMotionType==null){
			selectedDiscussionMotionType=request.getParameter("type");
		}
		DeviceType discussionMotionType=null;
		if(selectedDiscussionMotionType!=null){
			if(!selectedDiscussionMotionType.isEmpty()){
				discussionMotionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedDiscussionMotionType));
				model.addAttribute("formattedDiscussionMotionType", discussionMotionType.getName());
				model.addAttribute("discussionMotionType", discussionMotionType.getId());
				model.addAttribute("selectedDiscussionMotionType", discussionMotionType.getType());
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
						/**** Opposition or Ruiling Members ****/
						
						//Session selectedSession = Session.findById(Session.class, session);
						House house=House.find(houseType, new Date(), locale.toString());
						
						MemberPartyAssociation mpa = null;
						for(MemberPartyAssociation MPA : member.getMemberPartyAssociations())
						{
							if(MPA.getHouse().getId().equals(house.getId())) {
								mpa = MPA;
							}
						}
						
						List<MasterVO> membersbyPartyType = Member.getMemberRepository().findMembersByIsRulingParty(house, selectedSession, locale, mpa, member.getId());
						
						//PartyType partytype=Member.getMemberRepository().findPartyType(member.getId(), house.getId(), locale);
						//boolean mpa = member.getMemberPartyAssociations().get(0).getIsMemberOfRulingParty();
						//List<MasterVO> membersbyPartyType=Member.getMemberRepository().findActiveMembersByPartyType(house, selectedSession, locale.toString(), ,member.getId());
						//List<Member> supportingMembers=new ArrayList<Member>();
						//membersbyPartyType.remove(member);
						if(membersbyPartyType!=null){
							
							model.addAttribute("membersbyPartyType",membersbyPartyType);
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
					
					//Populate Supporting Members Validation Message
					String numberOfSupportingMembers = selectedSession.
							getParameter(discussionMotionType.getType()+"_numberOfSupportingMembers");
					String numberOfSupportingMembersComparator = selectedSession.
							getParameter(discussionMotionType.getType()+"_numberOfSupportingMembersComparator");

					if((numberOfSupportingMembers != null) && (numberOfSupportingMembersComparator != null)){
						model.addAttribute("numberOfSupportingMembers", numberOfSupportingMembers);            
						model.addAttribute("numberOfSupportingMembersComparator", 
								numberOfSupportingMembersComparator);

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

						model.addAttribute("numberOfSupportingMembersComparatorHTML",
								numberOfSupportingMembersComparator);
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
	protected void populateEdit(final ModelMap model, DiscussionMotion domain,
			final HttpServletRequest request) {
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit",request.getParameter("bulkedit"));

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
		DeviceType discussionMotionType=domain.getType();
		model.addAttribute("formattedDiscussionMotionType",discussionMotionType.getName());
		model.addAttribute("discussionMotionType",discussionMotionType.getId());
		model.addAttribute("selectedDiscussionMotionType",discussionMotionType.getType());	

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
		
		/*** Parent  ***/
		if(domain.getParent()!=null){
			model.addAttribute("formattedParentNumber",FormaterUtil.formatNumberNoGrouping(domain.getParent().getNumber(), locale));
			model.addAttribute("parent",domain.getParent().getId());
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
					if(usergroupType.equals("clerk")){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if((usergroupType.equals("member"))
							&&domain.getInternalStatus()!=null
							&&domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)
							&&i.getDecisionStatus()!=null
							&&i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}else if((usergroupType.equals("member"))
							&&domain.getInternalStatus()!=null
							&&(domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE)
									||domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_COMPLETE))){
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
		
		//Populate Supporting Members Validation Message
		String numberOfSupportingMembers = selectedSession.
				getParameter(discussionMotionType.getType()+"_numberOfSupportingMembers");
		String numberOfSupportingMembersComparator = selectedSession.
				getParameter(discussionMotionType.getType()+"_numberOfSupportingMembersComparator");

		if((numberOfSupportingMembers != null) && (numberOfSupportingMembersComparator != null)){
			model.addAttribute("numberOfSupportingMembers", numberOfSupportingMembers);            
			model.addAttribute("numberOfSupportingMembersComparator", 
					numberOfSupportingMembersComparator);

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

			model.addAttribute("numberOfSupportingMembersComparatorHTML",
					numberOfSupportingMembersComparator);
		}

		/**** Opposition or Ruiling Members ****/
		
		
		House house=House.find(houseType, new Date(), locale.toString());
		PartyType partytype=Member.getMemberRepository().findPartyType(member.getId(), house.getId(), locale);
		List<MasterVO> membersbyPartyType=Member.getMemberRepository().findActiveMembersByPartyType(house, selectedSession, locale.toString(), partytype,member.getId());
		//List<Member> supportingMembers=new ArrayList<Member>();
		//membersbyPartyType.remove(member);
		if(membersbyPartyType!=null){
			
			model.addAttribute("membersbyPartyType",membersbyPartyType);
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
			if(domain.getDiscussionDate()!=null){
				model.addAttribute("discussionDate",FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT,"en_US").format(domain.getDiscussionDate()));
				model.addAttribute("formattedDiscussionDate",FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT,locale).format(domain.getDiscussionDate()));
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
		
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());
		model.addAttribute("dataEnteredBy",domain.getDataEnteredBy());
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
		/**** Referenced Entities are collected in refentities****/		
		List<ReferenceUnit> referencedEntities=domain.getReferencedEntities();
		if(referencedEntities!=null&&!referencedEntities.isEmpty()){
			List<Reference> refentities=new ArrayList<Reference>();
			List<Reference> refdiscussionmotionentities=new ArrayList<Reference>();
			List<Reference> refmotionentities=new ArrayList<Reference>();
			List<Reference> refquestionentities=new ArrayList<Reference>();
			List<Reference> refresolutionentities=new ArrayList<Reference>();
			for(ReferenceUnit re:referencedEntities){
				if(re.getDeviceType() != null){
					if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_DISCUSSIONMOTIONS)){
						Reference reference=new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.formatNumberNoGrouping(re.getNumber(), locale));
						reference.setNumber(String.valueOf(re.getDevice()));
						refentities.add(reference);	
						refdiscussionmotionentities.add(reference);
					}else if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_MOTIONS)){
						Reference reference=new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.formatNumberNoGrouping(re.getNumber(), locale));
						reference.setNumber(String.valueOf(re.getDevice()));
						refentities.add(reference);	
						refmotionentities.add(reference);
					}else if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						Reference reference=new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.formatNumberNoGrouping(re.getNumber(), locale));
						reference.setNumber(String.valueOf(re.getDevice()));
						refentities.add(reference);	
						refresolutionentities.add(reference);
					}else if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
						Reference reference=new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.formatNumberNoGrouping(re.getNumber(), locale));
						reference.setNumber(String.valueOf(re.getDevice()));
						refentities.add(reference);	
						refquestionentities.add(reference);
					}
				}
			}
			model.addAttribute("referencedMotions",refmotionentities);
			model.addAttribute("referencedDiscussionMotions",refdiscussionmotionentities);
			model.addAttribute("referencedQuestions",refquestionentities);
			model.addAttribute("referencedResolutions",refresolutionentities);
			model.addAttribute("referencedEntities",refentities);
		}	
		/**** Clubbed motions are collected in references ****/
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null&&!clubbedEntities.isEmpty()){
			List<Reference> references=new ArrayList<Reference>();
			StringBuffer buffer1=new StringBuffer();
			buffer1.append(memberNames+",");
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getDiscussionMotion().getNumber()));
				reference.setNumber(String.valueOf(ce.getDiscussionMotion().getId()));
				references.add(reference);
				String tempPrimary=ce.getDiscussionMotion().getPrimaryMember().getFullname();
				if(!buffer1.toString().contains(tempPrimary)){
					buffer1.append(ce.getDiscussionMotion().getPrimaryMember().getFullname()+",");
				}
				List<SupportingMember> clubbedSupportingMember=ce.getDiscussionMotion().getSupportingMembers();
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
		/**** Populating Put up otions and Actors ****/
//		if(domain.getInternalStatus()!=null){
//			String internalStatusType=domain.getInternalStatus().getType();			
//			if(usergroupType!=null&&!usergroupType.isEmpty()&&usergroupType.equals("assistant")
//					&&(internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION)
//							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
//							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
//							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
//							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
//							||internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_REJECTION)
//					)){
//				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUsergroup));
//				List<Reference> actors=WorkflowConfig.findDiscussionMotionActorsVO(domain, internalStatus, userGroup, 1, locale);
//				model.addAttribute("internalStatusSelected",internalStatus.getId());
//				model.addAttribute("actors",actors);
//				if(actors!=null&&!actors.isEmpty()){
//					String nextActor=actors.get(0).getId();
//					String[] actorArr=nextActor.split("#");
//					domain.setLevel(actorArr[2]);
//					domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
//				}
//			}	
//		}
	}

	private void populateInternalStatus(final ModelMap model,final DiscussionMotion domain,final String usergroupType,final String locale) {
		try {
			List<Status> internalStatuses=new ArrayList<Status>();
			DeviceType deviceType=domain.getType();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseType=domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				}
			}/**** DISCUSSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			}/**** DISCUSSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			}	
			/**** DISCUSSIONMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
			}	
			/**** Internal Status****/
			model.addAttribute("internalStatuses",internalStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}		
	}
	
	private void populateSupportingMembers(final DiscussionMotion domain,final HttpServletRequest request){
		/**** Supporting Members selected by Member in new/edit ****/
		DeviceType deviceType=domain.getType();
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
			DiscussionMotion discussionMotion=DiscussionMotion.findById(DiscussionMotion.class,domain.getId());
			members=discussionMotion.getSupportingMembers();
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
					
				//	DeviceType deviceType=DeviceType.findById(DeviceType.class,deviceType.getId());
					
					/**** New Supporting Member ****/
					if(supportingMember==null){
						supportingMember=new SupportingMember();
						supportingMember.setMember(member);
						supportingMember.setLocale(domain.getLocale());
						if(dataEntryType!=null&&!(dataEntryType.isEmpty())){
							supportingMember.setDecisionStatus(approvedStatus);
							supportingMember.setApprovalType("OFFLINE");
							supportingMember.setApprovedSubject(domain.getSubject());
							supportingMember.setApprovedText(domain.getNoticeContent());						
						}else if(deviceType.getType().toUpperCase().equals("MOTIONS_DISCUSSIONMOTION_PUBLICIMPORTANCE") || deviceType.getType().toUpperCase().equals("MOTIONS_DISCUSSIONMOTION_LASTWEEK")){
							supportingMember.setDecisionStatus(approvedStatus);
							supportingMember.setApprovalType("OFFLINE");
							supportingMember.setApprovedSubject(domain.getSubject());
							supportingMember.setApprovedText(domain.getNoticeContent());	
						}
						else{
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
	protected void customValidateCreate(final DiscussionMotion domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}
		
		
		Session session = domain.getSession();								
		if(session != null) {
			String noOFSupportingMembersToCheck = 
					session.getParameter(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION_NO_OF_SUPPORTING_MEMBERS);
			String noOFSupportingMembersComparator = 
					session.getParameter(ApplicationConstants.DISCUSSIONMOTION_SHORTDURATION_NO_OF_SUPPORTING_MEMBERS_COMPARATOR);
			if(noOFSupportingMembersToCheck!=null && !noOFSupportingMembersToCheck.isEmpty() 
				&& noOFSupportingMembersComparator!=null && !noOFSupportingMembersComparator.isEmpty()){										
				int numberOFSupportingMembersToCheck = Integer.parseInt(noOFSupportingMembersToCheck);
				int numberOFSupportingMembersReceived = 0;
				if(domain.getSupportingMembers()!=null) {
					numberOFSupportingMembersReceived = domain.getSupportingMembers().size();
				}
				if(noOFSupportingMembersComparator.equalsIgnoreCase("eq")) {
					if(!(numberOFSupportingMembersReceived == numberOFSupportingMembersToCheck)) {
						result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
					}
				}else if(noOFSupportingMembersComparator.equalsIgnoreCase("le")) {
					if(!(numberOFSupportingMembersReceived <= numberOFSupportingMembersToCheck)) {
						result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
					}
				}else if(noOFSupportingMembersComparator.equalsIgnoreCase("lt")) {
					if(!(numberOFSupportingMembersReceived < numberOFSupportingMembersToCheck)) {
						result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
					}
				}else if(noOFSupportingMembersComparator.equalsIgnoreCase("ge")) {
					if(!(numberOFSupportingMembersReceived >= numberOFSupportingMembersToCheck)) {
						result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
					}
				}else if(noOFSupportingMembersComparator.equalsIgnoreCase("gt")) {
					if(!(numberOFSupportingMembersReceived > numberOFSupportingMembersToCheck)) {
						result.rejectValue("supportingMembers","noOfSupportingMembersInvalid");
					}
				}
			}
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
					if(domain.getPrimaryMember()==null){
						result.rejectValue("primaryMember","PrimaryMemberEmpty");
					}
					if(domain.getSubject().isEmpty()){
						result.rejectValue("subject","SubjectEmpty");
					}
					if(domain.getNoticeContent().isEmpty()){
						result.rejectValue("noticeContent","NoticeContentEmpty");
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
						if(domain.getPrimaryMember()==null){
							result.rejectValue("primaryMember","PrimaryMemberEmpty");
						}
						if(domain.getSubject().isEmpty()){
							result.rejectValue("subject","SubjectEmpty");
						}
						if(domain.getNoticeContent().isEmpty()){
							result.rejectValue("noticeContent","NoticeContentEmpty");
						}
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
													result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Motion cannot be submitted before " + submissionStartLimitDateStr);
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
													result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Motion cannot be submitted after " + submissionEndLimitDateStr);
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
			if(domain.getPrimaryMember()==null){
				result.rejectValue("primaryMember","PrimaryMemberEmpty");
			}
			if(domain.getSubject().isEmpty()){
				result.rejectValue("subject","SubjectEmpty");
			}
			if(domain.getNoticeContent().isEmpty()){
				result.rejectValue("noticeContent","NoticeContentEmpty");
			}
		}
	}
	
	@Override
	protected void preValidateUpdate(final DiscussionMotion domain,
	            final BindingResult result, 
	            final HttpServletRequest request) {
		DiscussionMotion discussionMotion = DiscussionMotion.findById(DiscussionMotion.class, domain.getId());
		if(domain.getDepartments()!=null && !domain.getDepartments().isEmpty()){
			domain.setDepartments(discussionMotion.getDepartments());
		}
		String strDiscussionDate = request.getParameter("formattedDiscussionDate");
		
		if(strDiscussionDate != null && !strDiscussionDate.isEmpty()){
			domain.setDiscussionDate(FormaterUtil.formatStringToDate(strDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT));
		}


	 }
	
	@Override
	protected void customValidateUpdate(final DiscussionMotion domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);
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
				if(domain.getPrimaryMember()==null){
					result.rejectValue("primaryMember","PrimaryMemberEmpty");
				}
				if(domain.getSubject().isEmpty()){
					result.rejectValue("subject","SubjectEmpty");
				}
				if(domain.getNoticeContent().isEmpty()){
					result.rejectValue("noticeContent","NoticeContentEmpty");
				}	
				if(domain.getBriefExplanation().isEmpty()){
					result.rejectValue("briefExplanation","NoticeContentEmpty");
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
					if(domain.getPrimaryMember()==null){
						result.rejectValue("primaryMember","PrimaryMemberEmpty");
					}
					if(domain.getSubject().isEmpty()){
						result.rejectValue("subject","SubjectEmpty");
					}
					if(domain.getNoticeContent().isEmpty()){
						result.rejectValue("noticeContent","NoticeContentEmpty");
					}
					if(domain.getBriefExplanation().isEmpty()){
						result.rejectValue("briefExplanation","NoticeContentEmpty");
					}
					if (result.getFieldErrorCount("supportingMembers") == 0) {
						// check if request is already
						// sent for approval
						int count = 0;
						if (domain.getSupportingMembers() != null) {
							if (domain.getSupportingMembers().size() > 0) {
								for (SupportingMember i : domain.getSupportingMembers()) {
									if (i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
										count++;
									}
								}
								if (count < 2) {
									result.rejectValue("supportingMembers","supportingMembersRequestNotSent");
								}
							}
						}
						if(domain.getSupportingMembers() == null) {
							result.rejectValue("supportingMembers","supportingMembersRequestNotSent");
						}
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
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Motion cannot be submitted before " + submissionStartLimitDateStr);
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
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Motion cannot be submitted after " + submissionEndLimitDateStr);
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
						if(domain.getPrimaryMember()==null){
							result.rejectValue("primaryMember","PrimaryMemberEmpty");
						}
						if(domain.getSubject().isEmpty()){
							result.rejectValue("subject","SubjectEmpty");
						}
						if(domain.getNoticeContent().isEmpty()){
							result.rejectValue("noticeContent","NoticeContentEmpty");
						}
						if(domain.getBriefExplanation().isEmpty()){
							result.rejectValue("briefExplanation","NoticeContentEmpty");
						}
						String internalStatusType=domain.getInternalStatus().getType();
						if(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)){
							result.rejectValue("internalStatus","PutUpOptionEmpty");
						}						
						if(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)){
							result.rejectValue("internalStatus","PutUpOptionEmpty");
						}
						if(!(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT))
								&&!(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED))
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
			if(domain.getPrimaryMember()==null){
				result.rejectValue("primaryMember","PrimaryMemberEmpty");
			}
			if(domain.getSubject().isEmpty()){
				result.rejectValue("subject","SubjectEmpty");
			}
			if(domain.getNoticeContent().isEmpty()){
				result.rejectValue("noticeContent","NoticeContentEmpty");
			}
			if(domain.getBriefExplanation().isEmpty()){
				result.rejectValue("briefExplanation","NoticeContentEmpty");
			}
			String usergroupType=request.getParameter("usergroupType");
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("assistant")){
				if(domain.getMinistries()==null){
					result.rejectValue("ministry","MinistryEmpty");
				}	
				if(domain.getSubDepartments()==null){
					result.rejectValue("subDepartment","SubDepartmentEmpty");
				}	
			}
		}
	}
	
	@Override
	protected void populateCreateIfErrors(final ModelMap model, DiscussionMotion domain,
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
		super.populateCreateIfErrors(model, domain, request);
	}
	
	
	@Override
	protected void populateUpdateIfErrors(final ModelMap model, DiscussionMotion domain,
			final HttpServletRequest request) {
		/**** updating submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate=request.getParameter("taskReceivedOnDate");
		String strDiscussionDate=request.getParameter("setDiscussionDate");
		
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate!=null&&!strSubmissionDate.isEmpty()){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strDiscussionDate!=null&&!strDiscussionDate.isEmpty()){
					domain.setDiscussionDate(format.parse(strDiscussionDate));
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
		super.populateUpdateIfErrors(model, domain, request);
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, DiscussionMotion domain,
			final HttpServletRequest request) {			
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		String usergroupType=request.getParameter("usergroupType");
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getPrimaryMember()!=null 
				&& (!domain.getSubject().isEmpty())
				&&(!domain.getNoticeContent().isEmpty())){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&(usergroupType.equals("member")||usergroupType.equals("typist"))){
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
											i.setApprovedSubject(domain.getSubject());
											i.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);
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
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_SUBMIT, domain.getLocale());
							domain.setStatus(newstatus);
							domain.setInternalStatus(newstatus);
							domain.setRecommendationStatus(newstatus);
						}
					}else{
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&(usergroupType.equals("member") || usergroupType.equals("typist"))){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_COMPLETE, domain.getLocale());
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}
					}
				}else{
					if(usergroupType!=null&&!(usergroupType.isEmpty()) && (usergroupType.equals("member") || usergroupType.equals("typist"))){
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}
			}else{
				if(usergroupType!=null&&!(usergroupType.isEmpty()) && (usergroupType.equals("member") || usergroupType.equals("typist"))){
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}
		}
		/**** Drafts ****/
		else{
			if(usergroupType!=null&&!(usergroupType.isEmpty()) && (usergroupType.equals("member")|| usergroupType.equals("typist"))){
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("typist")){
			try {
				Member member=domain.getPrimaryMember();
				User user=User.findbyNameBirthDate(member.getFirstName(),member.getMiddleName(),member.getLastName(),member.getBirthDate());
				domain.setCreatedBy(user.getCredential().getUsername());
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
		}else{
			domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		}
		domain.setDataEnteredBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		/**** add department ****/
		if(domain.getSubDepartments()!=null && !domain.getSubDepartments().isEmpty()){
			List<SubDepartment> subDepartments = domain.getSubDepartments();
			List<Department> departments = new ArrayList<Department>();
			for(SubDepartment sd:subDepartments){
				departments.add(sd.getDepartment());
			}
			domain.setDepartments(departments);
		}
	}
	
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, DiscussionMotion domain,
			final HttpServletRequest request) {
		/**** Checking if its submission request or normal update ****/
		String operation=request.getParameter("operation");		
		String usergroupType=request.getParameter("usergroupType");
		/**** DiscussionMotion status will be complete if all mandatory fields have been filled ****/
		if(domain.getHouseType()!=null&&domain.getType()!=null&&domain.getSession()!=null
				&& domain.getPrimaryMember()!=null &&
				(!domain.getSubject().isEmpty())
				&&(!domain.getNoticeContent().isEmpty())){			
			if(operation!=null){
				if(!operation.isEmpty()){
					/**** Submission request ****/
					if(operation.trim().equals("submit")){
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&(usergroupType.equals("member")||usergroupType.equals("typist"))){
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
							}
							/**** Status,Internal status and recommendation status is set to complete ****/
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_SUBMIT, domain.getLocale());
							domain.setStatus(newstatus);
							domain.setInternalStatus(newstatus);
							domain.setRecommendationStatus(newstatus);
						}
					}else{
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_COMPLETE, domain.getLocale());
							/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
							if(!domain.getStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)){
								domain.setStatus(status);
								domain.setInternalStatus(status);
								domain.setRecommendationStatus(status);
							}	
						}
					}
				}else{
					if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_COMPLETE, domain.getLocale());
						/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
						if(!domain.getStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)){
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}
					}
				}
			}else{
				if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_COMPLETE, domain.getLocale());
					/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
					if(!domain.getStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}
			}
		}
		/**** If all mandatory fields have not been set then status,internal status and recommendation status is set to incomplete ****/
		else{
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")){
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE, domain.getLocale());
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
		/**** updating submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate=request.getParameter("taskReceivedOnDate");
		String strDiscussionDate=request.getParameter("setDiscussionDate");
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate!=null&&!strSubmissionDate.isEmpty()){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strDiscussionDate!=null&&!strDiscussionDate.isEmpty()){
					domain.setDiscussionDate(format.parse(strDiscussionDate));
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
		List<SubDepartment> subDepartments = domain.getSubDepartments();
		
		if(subDepartments!=null && !subDepartments.isEmpty()){
			List<Department> departments = new ArrayList<Department>();
			for(SubDepartment sd:subDepartments){
				departments.add(sd.getDepartment());
			}
			domain.setDepartments(departments);
		}	
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		if(strUserGroupType!=null){
			if(strUserGroupType.equals("assistant")
				|| strUserGroupType.equals("clerk")){				
				String internalStatus = domain.getInternalStatus().getType();
				if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT) 
						&& domain.getMinistries()!=null
						&& domain.getSubDepartments()!=null) {
					Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}	
				/**** File parameters are set when internal status is something other than 
				 * submit,complete and incomplete and file is null .Then only the motion gets attached to a file.*/
				String currentStatus=domain.getInternalStatus().getType();
//				if(operation==null){
//					if(!(currentStatus.equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)
//							||currentStatus.equals(ApplicationConstants.DISCUSSIONMOTION_COMPLETE)
//							||currentStatus.equals(ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE))							
//							&&domain.getFile()==null){
//						/**** Add motion to file ****/
//						Reference reference=DiscussionMotion.findCurrentFile(domain);
//						domain.setFile(Integer.parseInt(reference.getId()));
//						domain.setFileIndex(Integer.parseInt(reference.getName()));
//						domain.setFileSent(false);
//					}
//				}else if(operation.isEmpty()){
//					if(!(currentStatus.equals(ApplicationConstants.DISCUSSIONMOTION_SUBMIT)
//							||currentStatus.equals(ApplicationConstants.DISCUSSIONMOTION_COMPLETE)
//							||currentStatus.equals(ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE))
//							&&domain.getFile()==null){
//						/**** Add motion to file ****/
//						Reference reference=DiscussionMotion.findCurrentFile(domain);
//						domain.setFile(Integer.parseInt(reference.getId()));
//						domain.setFileIndex(Integer.parseInt(reference.getName()));
//						domain.setFileSent(false);
//					}
//				}
			}
		}		
	}
	
	@Override
	protected void populateAfterCreate(final ModelMap model, DiscussionMotion domain,
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
						DiscussionMotion motion=DiscussionMotion.findById(DiscussionMotion.class,domain.getId());
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
	protected void populateAfterUpdate(final ModelMap model, DiscussionMotion domain,
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
						DiscussionMotion motion=DiscussionMotion.findById(DiscussionMotion.class,domain.getId());
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
						DiscussionMotion motion=DiscussionMotion.findById(DiscussionMotion.class,domain.getId());
						/**** Process Started and task created ****/
						Task task=processService.getCurrentTask(processInstance);
						if(endflag!=null){
							if(!endflag.isEmpty()){
								if(endflag.equals("continue")){
									/**** Workflow Detail entry made only if its not the end of workflow ****/
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

									if(recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflow = Workflow.findByStatus(recommendationStatus, domain.getLocale());
									} 
									else if(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)
											|| internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)
											|| (internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
										workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
									} 
									else {
										workflow = Workflow.findByStatus(internalStatus, domain.getLocale());
									}
									
									WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,usergroupType,workflow.getType(),level);
									motion.setWorkflowDetailsId(workflowDetails.getId());
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
					}
				}
			}
		}
		
	}
	
	
	public static List<HouseType> getHouseTypes(final AuthUser user, final DeviceType deviceType,
			final String locale) throws ELSException {
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		
		String strHouseType = user.getHouseType();
		if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)
				|| strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			houseTypes = HouseType.findAllByFieldName(HouseType.class, 
					"type", strHouseType, "name", ApplicationConstants.ASC, locale);
		}
		else if(strHouseType.equals(ApplicationConstants.BOTH_HOUSE)) {
			//check for lower house in the active usergroup having selected device type
			HouseType lowerHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
			List<UserGroup> currentUserGroupsWithDeviceTypeForLowerHouse = UserGroup.findActiveUserGroupsOfGivenUser(user.getActualUsername(), lowerHouseType.getName(), deviceType.getName(), locale);
			if(currentUserGroupsWithDeviceTypeForLowerHouse!=null && !currentUserGroupsWithDeviceTypeForLowerHouse.isEmpty()) {
				houseTypes.add(lowerHouseType);
			}
			//check for upper house in the active usergroup having selected device type
			HouseType upperHouseType = HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale);
			List<UserGroup> currentUserGroupsWithDeviceTypeForUpperHouse = UserGroup.findActiveUserGroupsOfGivenUser(user.getActualUsername(), upperHouseType.getName(), deviceType.getName(), locale);
			if(currentUserGroupsWithDeviceTypeForUpperHouse!=null && !currentUserGroupsWithDeviceTypeForUpperHouse.isEmpty()) {
				houseTypes.add(upperHouseType);
			}
			if(houseTypes.isEmpty()) { //no active usergroup for the user or no need for having usergroup for the user
				houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}			
		}
		else {
			throw new ELSException("DiscussionMotionController.getHouseTypes/2", 
					"Inappropriate house type is set in AuthUser.");
		}
		
		return houseTypes;
	}
	
	public static HouseType getHouseType(final AuthUser user,
			final String locale) throws ELSException {
		// Assumption: LOWER_HOUSE is the default house type
		HouseType houseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
		
		String strHouseType = user.getHouseType();
		if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			houseType = HouseType.findByType(strHouseType, locale);
		}
		
		// In case strHouseType = "BOTH_HOUSE", return the default houseType i.e LOWER_HOUSE
		return houseType;
	}
	
	
	/**** Supporting Members View Status ****/
	@RequestMapping(value="/status/{motion}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("motion") final String dMotion){
		DiscussionMotion discussionMotion = DiscussionMotion.findById(DiscussionMotion.class,Long.parseLong(dMotion));
		List<SupportingMember> supportingMembers=discussionMotion.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "discussionmotion/supportingmember";
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
		return "discussionmotion/citation";
	}

	/**** Member-Supporting Members Contacts ****/
	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "discussionmotion/contacts";
	}
	
	
	/**** revision History ****/
	@RequestMapping(value="/revisions/{motionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("motionId")  final Long discussionMotionId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=DiscussionMotion.getRevisions(discussionMotionId,locale.toString());
		DiscussionMotion m = DiscussionMotion.findById(DiscussionMotion.class, discussionMotionId);
		if(m != null){
			if(m.getType() != null){
				if(m.getType().getType() != null){
					model.addAttribute("selectedDeviceType", m.getType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);
		return "discussionmotion/revisions";
	}
	
	/**** Bulk Submission ****/
	@RequestMapping(value="/bulksubmission",method=RequestMethod.GET)
	public String getBulkSubmissionView(final HttpServletRequest request,final Locale locale,
			final ModelMap model){	
		try {
			Member primaryMember=Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale.toString());
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			String strMotionType=request.getParameter("discussionMotionType");
			String strLocale=locale.toString();
			String strItemsCount=request.getParameter("itemscount");
			String strUserGroupType = request.getParameter("usergroupType");
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strMotionType!=null&&!(strMotionType.isEmpty())
					&&strItemsCount!=null&&!(strItemsCount.isEmpty())){
				HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
				SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
				Integer sessionYear=Integer.parseInt(strSessionYear);
				Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
				Integer itemsCount=Integer.parseInt(strItemsCount);
				List<DiscussionMotion> motions= new ArrayList<DiscussionMotion>();
				
				if(strUserGroupType.equals(ApplicationConstants.TYPIST)){
					String userName = this.getCurrentUser().getActualUsername();
					motions = DiscussionMotion.findAllEnteredBy(session,userName,motionType,itemsCount,strLocale);
				}else if(strUserGroupType.equals(ApplicationConstants.MEMBER)){
					if(primaryMember != null){
						motions = DiscussionMotion.findAllByMember(session,primaryMember,motionType,itemsCount,strLocale);
					}
				}
				
				model.addAttribute("motions",motions);
				model.addAttribute("size",motions.size());
				//String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupType", strUserGroupType);
			}			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}	
		return "discussionmotion/bulksubmission";
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

			List<DiscussionMotion> motions = new ArrayList<DiscussionMotion>();
			for(String i : items) {
				Long id = Long.parseLong(i);
				DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, id);

				/**** Update Supporting Member ****/
				List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
				Status timeoutStatus=Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
				if(motion.getSupportingMembers() != null) {
					if(! motion.getSupportingMembers().isEmpty()) {
						for(SupportingMember sm : motion.getSupportingMembers()) {
							if(sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)||
									sm.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_PENDING)){
								/**** Update Supporting Member ****/
								sm.setDecisionStatus(timeoutStatus);
								sm.setApprovalDate(new Date());	
								sm.setApprovedText(motion.getNoticeContent());
								sm.setApprovedSubject(motion.getSubject());
								sm.setApprovalType("ONLINE");
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
				Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.DISCUSSIONMOTION_SUBMIT, motion.getLocale());
				motion.setStatus(newstatus);
				motion.setInternalStatus(newstatus);
				motion.setRecommendationStatus(newstatus);

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
		}
		return "discussionmotion/bulksubmissionack";
	}

	/**** Bulk Submission(Assistant)****/
	@RequestMapping(value="/bulksubmission/assistant/int",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantInt(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strMotionType=request.getParameter("discussionMotionType");			
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
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
			DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
			/**** Decision Status Available To Assistant(At this stage) 
			 * MOTION_PUT_UP_OPTIONS_+MOTION_TYPE+HOUSE_TYPE+USERGROUP_TYPE ****/
			CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUsergroupType.toUpperCase(), "");
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
			model.addAttribute("file", "-");
		}		
		return "discussionmotion/bulksubmissionassistantint";		
	}
	@RequestMapping(value="/bulksubmission/assistant/view",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantView(final HttpServletRequest request,final Locale locale,
			final Model model){	
		getBulkSubmissionMotions(model,request,locale.toString());
		return "discussionmotion/bulksubmissionassistantview";		
	}
	
	@Transactional
	@RequestMapping(value="/bulksubmission/assistant/update",method=RequestMethod.POST)
	public String bulkSubmissionAssistant(final HttpServletRequest request,final Locale locale,
			final Model model){	
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus=request.getParameter("aprstatus");
		StringBuffer assistantProcessed=new StringBuffer();
		StringBuffer recommendAdmission=new StringBuffer();
		StringBuffer recommendRejection=new StringBuffer();
		if(selectedItems != null && selectedItems.length>0
				&&strStatus!=null&&!strStatus.isEmpty()) {
			/**** As It Is Condition ****/
			if(strStatus.equals("-")){
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, id);
					if(!motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)){
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

									if(recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
									} 
									else if(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)
											|| internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)
											|| (internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											||(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
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
							if(motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_ADMISSION)){
								recommendAdmission.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale())+",");
							}else if(motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_REJECTION)){
								recommendRejection.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale())+",");
							}
						} catch (Exception e) {
							model.addAttribute("error", e);
						}
					}else{
						assistantProcessed.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale())+",");
					}
				}	
				model.addAttribute("assistantProcessed", assistantProcessed.toString());
			}else{
				Long statusId=Long.parseLong(strStatus);
				Status status=Status.findById(Status.class,statusId);
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, id);
					String actor=request.getParameter("actor");
					String level=request.getParameter("level");
					if(actor!=null&&!actor.isEmpty()
							&&level!=null&&!level.isEmpty()){
						Reference reference;
						try {
							reference = UserGroup.findDiscussionMotionActor(motion,actor,level,locale.toString());
							
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

									if(recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflow = Workflow.findByStatus(recommendationStatus, locale.toString());
									} 
									else if(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)
											|| internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)
											|| (internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
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
									
									motion.setWorkflowDetailsId(workflowDetails.getId());
									/**** Workflow Started ****/
									motion.setWorkflowStarted("YES");
									motion.setWorkflowStartedOn(new Date());
									motion.setTaskReceivedOn(new Date());
									motion.setFileSent(true);
									motion.simpleMerge();
								}	
								if(motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_ADMISSION)){
									recommendAdmission.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale())+",");
								}else if(motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_REJECTION)){
									recommendRejection.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale())+",");
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
		return "discussionmotion/bulksubmissionassistantview";
	}
	
	public void getBulkSubmissionMotions(final Model model,final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strMotionType=request.getParameter("discussionMotionType");			
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
			List<DiscussionMotion> motions=new ArrayList<DiscussionMotion>();
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
				DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
				if(strFile!=null&&!strFile.isEmpty()&&!strFile.equals("-")){
					Integer file=Integer.parseInt(strFile);
					motions=DiscussionMotion.findAllByFile(session,motionType,file,strLocale);
				}else if(strItemsCount!=null&&!strItemsCount.isEmpty()){
					Integer itemsCount=Integer.parseInt(strItemsCount);
					Status internalStatus=Status.findById(Status.class,Long.parseLong(strStatus));
					motions=DiscussionMotion.findAllByStatus(session,motionType,internalStatus,itemsCount,strLocale);
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
	
	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {		
		DiscussionMotion discussionMotion = DiscussionMotion.findById(DiscussionMotion.class, id);
		if(discussionMotion!=null){
			Status status=discussionMotion.getStatus();
			if(status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_INCOMPLETE)
					||status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_COMPLETE)){
				DiscussionMotion.supportingMemberWorkflowDeletion(discussionMotion);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	
	/**** Yaadi to discuss update ****/
	@RequestMapping(value="/statusupdate/assistant/init", method=RequestMethod.GET)
	public String getStatusUpdateInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Request Params ****/
		String retVal = "discussionmotion/error";
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
			
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class, "MOTION_STATUS_UPDATE_" + deviceType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() , "");

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
			
			retVal = "discussionmotion/statusupdateinit";
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
		return "discussionmotion/statusupdateassistantview";
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
					List<DiscussionMotion> motions = new ArrayList<DiscussionMotion>();
				
					HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale);
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					Session session;
					try {
						session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
				
				
						DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));				
						
						Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));
						motions = DiscussionMotion.findAllAdmittedUndisccussed(session, deviceType, internalStatus, locale);
				
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
		String page = "discussionmotion/error";
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
						DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, id);
						Status status = Status.findById(Status.class, new Long(strDecisionStatus));
						if(status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_ANSWERRECEIVED)){
						/*	if(strDate!= null && !strDate.isEmpty()){
								Date replyReceivedDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								motion.set
								motion.setReplyReceivedDate(replyReceivedDate);
							}
							motion.setRecommendationStatus(status);*/
						}else{
							if(strDate!= null && !strDate.isEmpty()){
								Date discussionDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								if(status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_DISCUSSED)) {
									motion.setDiscussionDate(discussionDate);
									motion.setDiscussionStatus(status);
								} else if(status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_UNDISCUSSED)) {
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
			this.getStatusUpdateMotions(model, request, locale.toString());
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "discussionmotion/statusupdateview";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	
	
}
