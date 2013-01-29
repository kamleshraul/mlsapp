package org.mkcl.els.controller.qis;

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

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
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
@RequestMapping("question")
public class QuestionController extends GenericController<Question>{

	@Autowired
	private IProcessService processService;

	@Override
	protected void populateModule(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Question Types ****/
			List<DeviceType> deviceTypes = DeviceType.findDeviceTypesStartingWith("questions", locale);
			model.addAttribute("questionTypes", deviceTypes);
			/**** Default Value ****/
			model.addAttribute("questionType",deviceType.getId());
			/**** Access Control Based on Question Type ****/
			model.addAttribute("questionTypeType",deviceType.getType());
			/**** House Types ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			String houseType=this.getCurrentUser().getHouseType();
			if(houseType.equals("lowerhouse")){
				houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals("upperhouse")){
				houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals("bothhouse")){
				houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}
			model.addAttribute("houseTypes", houseTypes);
			if(houseType.equals("bothhouse")){
				houseType="lowerhouse";
			}
			model.addAttribute("houseType",houseType);
			/**** Session Types ****/
			List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
			/**** Latest Session of a House Type ****/
			HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
			Session lastSessionCreated=Session.findLatestSession(authUserHouseType);
			/*** Session Year and Session Type ****/
			Integer year=new GregorianCalendar().get(Calendar.YEAR);
			if(lastSessionCreated.getId()!=null){
				year=lastSessionCreated.getYear();
				model.addAttribute("sessionType",lastSessionCreated.getType().getId());
			}else{
				model.addAttribute("errorcode","nosessionentriesfound");
			}
			model.addAttribute("sessionTypes",sessionTypes);
			/**** Years ****/
			CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			List<Integer> years=new ArrayList<Integer>();
			if(houseFormationYear!=null){
				Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
				for(int i=year;i>=formationYear;i--){
					years.add(i);
				}
			}else{
				model.addAttribute("errorcode", "houseformationyearnotset");
			}
			model.addAttribute("years",years);
			model.addAttribute("sessionYear",year);	
			/**** added by sandeep singh(jan 27 2013) ****/
			/**** Custom Parameter To Determine The Usergroup and usergrouptype of qis users ****/			
			List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
			if(userGroups!=null){
				if(!userGroups.isEmpty()){
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"QIS_ALLOWED_USERGROUPTYPES", "");
					if(customParameter!=null){
						String allowedUserGroups=customParameter.getValue(); 
						for(UserGroup i:userGroups){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								/**** Authenticated User's usergroup and usergroupType ****/
								model.addAttribute("usergroup",i.getId());
								String userGroupType=i.getUserGroupType().getType();
								model.addAttribute("usergroupType",userGroupType);
								/**** Question Status Allowed ****/
								CustomParameter allowedStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_STATUS_ALLOWED_FOR_"+userGroupType.toUpperCase(), "");
								List<Status> status=new ArrayList<Status>();
								if(allowedStatus!=null){
									status=Status.findStatusContainedIn(allowedStatus.getValue(),locale);
								}else{
									CustomParameter defaultAllowedStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_STATUS_ALLOWED_BY_DEFAULT", "");
									if(defaultAllowedStatus!=null){
										status=Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
									}else{
										model.addAttribute("errorcode","question_status_allowed_by_default_not_set");
									}
								}
								model.addAttribute("status",status);
								break;
							}
						}
					}else{
						model.addAttribute("errorcode","qis_allowed_usergroups_notset");
					}
				}else{
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
			}else{
				model.addAttribute("errorcode","current_user_has_no_usergroups");
			}
			/**** Roles ****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().contains("CLERK")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().startsWith("QIS_")){
					model.addAttribute("role",i.getType());
					break;
				}
			}
			/*** ugparam is used to load data in grid and it is either by username or group ****/
			String strgroups=this.getCurrentUser().getGroupsAllowed();
			model.addAttribute("allowedGroups",strgroups);
			if(strgroups!=null){
				if(!strgroups.isEmpty()){
					List<Group> groups=new ArrayList<Group>();
					String[] gr=strgroups.split(",");
					for(String k:gr){
						Group group=Group.findByNumberHouseTypeSessionTypeYear(Integer.parseInt(k),  authUserHouseType, lastSessionCreated.getType(), year);
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

		}else{
			model.addAttribute("errorcode","workunderprogress");
		}		
	}

	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		/**** For Clerk and other QIS roles assistant grid is visible ****/
		String role=request.getParameter("role");
		String newUrlPattern=urlPattern;
		if(role.contains("QIS_")&& (!role.contains("CLERK"))){
			newUrlPattern=urlPattern+"?usergroup=assistant";
		}
		return newUrlPattern;
	}


	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		/**** Member and Clerk can only create new questions ****/
		String role=request.getParameter("role");		
		if(role!=null){
			if(!role.isEmpty()){
				if(role.startsWith("MEMBER_")){
					return servletPath;
				}else if(role.contains("CLERK")){
					return servletPath.replace("new","newclerk");
				}
			}
		}			
		/**** For others permission denied ****/
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
	}
	@Override
	protected void populateNew(final ModelMap model, final Question domain, final String locale,
			final HttpServletRequest request) {
		/**** Locale ****/
		domain.setLocale(locale);

		/**** Subject and Question Text ****/
		String subject=request.getParameter("subject");
		if(subject!=null){
			domain.setSubject(subject);
		}
		String questionText=request.getParameter("questionText");
		if(questionText!=null){
			domain.setQuestionText(questionText);
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
		/**** Question Type ****/
		String selectedQuestionType=request.getParameter("questionType");
		if(selectedQuestionType==null){
			selectedQuestionType=request.getParameter("type");
		}
		DeviceType questionType=null;
		if(selectedQuestionType!=null){
			if(!selectedQuestionType.isEmpty()){
				questionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedQuestionType));
				model.addAttribute("formattedQuestionType", questionType.getName());
				model.addAttribute("questionType", questionType.getId());
				model.addAttribute("selectedQuestionType", questionType.getType());
			}else{
				logger.error("**** Check request parameter 'questionType' for no value ****");
				model.addAttribute("errorcode","questionType_isempty");		
			}
		}else{
			logger.error("**** Check request parameter 'questionType' for null value ****");
			model.addAttribute("errorcode","questionType_isnull");
		}

		/**** Primary Member****/
		String memberNames=null;
		String primaryMemberName=null;
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

		/**** Session ****/
		Session selectedSession=null;
		if(houseType!=null&&selectedYear!=null&&sessionType!=null){
			selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			if(selectedSession!=null){
				model.addAttribute("session",selectedSession.getId());
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
				/**** Ministries ****/
				if(questionType.getType().trim().equals("questions_starred")){
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
									/**** Group ****/
									Group group=domain.getGroup();
									model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
									model.addAttribute("group",domain.getGroup().getId());

									/**** Departments ****/
									List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
									model.addAttribute("departments",departments);
									Department department=domain.getDepartment();
									if(department!=null){                            	
										model.addAttribute("departmentSelected",department.getId());
										/**** Sub Departments ****/
										List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,department, locale);
										model.addAttribute("subDepartments",subDepartments);
										SubDepartment subDepartment=domain.getSubDepartment();
										if(subDepartment!=null){
											model.addAttribute("subDepartmentSelected",subDepartment.getId());
										}
									}

									/**** Answering Dates ****/
									if(group!=null){
										List<QuestionDates> answeringDates=group.getQuestionDates();
										List<MasterVO> masterVOs=new ArrayList<MasterVO>();
										for(QuestionDates i:answeringDates){
											MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getDateFormatter(locale).format(i.getAnsweringDate()));
											masterVOs.add(masterVO);
										}
										model.addAttribute("answeringDates",masterVOs);
										if(domain.getAnsweringDate()!=null){
											model.addAttribute("answeringDate",domain.getAnsweringDate().getId());
											model.addAttribute("formattedAnsweringDate",FormaterUtil.getDateFormatter(locale).format(domain.getAnsweringDate().getAnsweringDate()));
											model.addAttribute("answeringDateSelected",domain.getAnsweringDate().getId());

										}
									}

								}							
							}}
						catch (ParseException e) {
							logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
							model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
						}
					}else{
						logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
						model.addAttribute("errorcode", "rotationorderpubdate_notset");
					}
				}else{
					List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
					model.addAttribute("ministries",ministries);
				}
			}else{
				logger.error("**** Session doesnot exists ****");
				model.addAttribute("errorcode","session_isnull");	
			}
		}else{
			logger.error("**** Check request parameters 'houseType,sessionYear and sessionType for null values' ****");
			model.addAttribute("errorcode","requestparams_isnull");
		}  
		/**** Priorities ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter!=null){
			List<MasterVO> priorities=new ArrayList<MasterVO>();
			for(int i=1;i<=Integer.parseInt(customParameter.getValue());i++){
				priorities.add(new MasterVO(i,FormaterUtil.getNumberFormatterNoGrouping(locale).format(i)));
			}
			model.addAttribute("priorities",priorities);
		}else{
			logger.error("**** Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set ****");
			model.addAttribute("errorcode","highestquestionprioritynotset");
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
		/**** role ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		//added by sandeep singh(Jan 27 2013)
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
		//---------------------------Added by vikas & dhananjay-------------------------------------
		if(questionType.getType().equals("questions_halfhourdiscussion_from_question") || questionType.getType().equals("questions_halfhourdiscussion_standalone")){
			populateForHalfHourDiscussionNew(model, domain, selectedSession, questionType, request);
		}
		//---------------------------Added by vikas & dhananjay-------------------------------------
	}

	@Override
	protected String modifyEditUrlPattern(final String newUrlPattern,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		/**** if request parameter contains edit=false then editreadonly page is displayed ****/
		String edit=request.getParameter("edit");
		if(edit!=null){
			if(!Boolean.parseBoolean(edit)){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}
		/**** for Member and Clerk edit page is displayed ****/
		/**** for assistant assistant page ****/
		/**** for other qis usergroupTypes editreadonly page ****/
		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(i.getType().startsWith("MEMBER_")||i.getType().contains("CLERK")){
				return newUrlPattern;
			}else if(i.getType().contains("ASSISTANT")||i.getType().contains("SECTION_OFFICER")){
				return newUrlPattern.replace("edit","assistant");
			}else if(i.getType().startsWith("QIS_")){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}	
		/**** for others permission denied ****/
		model.addAttribute("errorcode","permissiondenied");
		return "questions/error";
	}


	@Override
	protected void populateEdit(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
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

		/**** Question Type ****/
		DeviceType questionType=domain.getType();
		model.addAttribute("formattedQuestionType",questionType.getName());
		model.addAttribute("questionType",questionType.getId());
		model.addAttribute("selectedQuestionType",questionType.getType());

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

		/**** Priorities ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter!=null){
			List<MasterVO> priorities=new ArrayList<MasterVO>();
			for(int i=1;i<=Integer.parseInt(customParameter.getValue());i++){
				priorities.add(new MasterVO(i,FormaterUtil.getNumberFormatterNoGrouping(locale).format(i)));
			}
			model.addAttribute("priorities",priorities);
		}else{
			logger.error("**** Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set ****");
			model.addAttribute("errorcode","highestquestionprioritynotset");
		}
		if(domain.getPriority()!=null){
			model.addAttribute("priority",domain.getPriority());
			model.addAttribute("formattedPriority",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getPriority()));
		}
		/**** Ministries ****/
		if(questionType.getType().trim().equals("questions_starred")){
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
							/**** Group ****/
							Group group=domain.getGroup();
							model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
							model.addAttribute("group",domain.getGroup().getId());

							/**** Departments ****/
							List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
							model.addAttribute("departments",departments);
							Department department=domain.getDepartment();
							if(department!=null){                            	
								model.addAttribute("departmentSelected",department.getId());
								/**** Sub Departments ****/
								List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,department, locale);
								model.addAttribute("subDepartments",subDepartments);
								SubDepartment subDepartment=domain.getSubDepartment();
								if(subDepartment!=null){
									model.addAttribute("subDepartmentSelected",subDepartment.getId());
								}
							}

							/**** Answering Dates ****/
							if(group!=null){
								List<QuestionDates> answeringDates=group.getQuestionDates();
								List<MasterVO> masterVOs=new ArrayList<MasterVO>();
								for(QuestionDates i:answeringDates){
									MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getDateFormatter(locale).format(i.getAnsweringDate()));
									masterVOs.add(masterVO);
								}
								model.addAttribute("answeringDates",masterVOs);
								if(domain.getAnsweringDate()!=null){
									model.addAttribute("answeringDate",domain.getAnsweringDate().getId());
									model.addAttribute("formattedAnsweringDate",FormaterUtil.getDateFormatter(locale).format(domain.getAnsweringDate().getAnsweringDate()));
									model.addAttribute("answeringDateSelected",domain.getAnsweringDate().getId());

								}
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
		}else{
			List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
			model.addAttribute("ministries",ministries);
			Ministry ministry=domain.getMinistry();
			if(ministry!=null){
				model.addAttribute("ministrySelected",ministry.getId());
				/**** Group ****/
				Group group=domain.getGroup();
				model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
				model.addAttribute("group",domain.getGroup().getId());

				/**** Departments ****/
				List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
				model.addAttribute("departments",departments);
				Department department=domain.getDepartment();
				if(department!=null){  
					model.addAttribute("departmentSelected",department.getId());
					/**** Sub Departments ****/
					List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,department, locale);
					model.addAttribute("subDepartments",subDepartments); 
					SubDepartment subDepartment=domain.getSubDepartment();
					if(subDepartment!=null){
						model.addAttribute("subDepartmentSelected",subDepartment.getId());
					}
				}

				/**** Answering Dates ****/
				if(group!=null){
					List<QuestionDates> answeringDates=group.getQuestionDates();
					List<MasterVO> masterVOs=new ArrayList<MasterVO>();
					for(QuestionDates i:answeringDates){
						MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getDateFormatter(locale).format(i.getAnsweringDate()));
						masterVOs.add(masterVO);
					}
					model.addAttribute("answeringDates",masterVOs);
					if(domain.getAnsweringDate()!=null){
						model.addAttribute("answeringDateSelected",domain.getAnsweringDate().getId());
					}
				}
			}
		} 
		/**** Submission Date and Creation date****/ 
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){            
			if(domain.getSubmissionDate()!=null){
				model.addAttribute("submissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(domain.getCreationDate()!=null){
				model.addAttribute("creationDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
			}
		}
		/**** Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
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
		//added by sandeep singh(Jan 27 2013)
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
			request.getSession().removeAttribute("userGroup");
		}
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());
		/**** Status,Internal Status and recommendation Status ****/
		Status status=domain.getStatus();
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		if(status!=null){
			model.addAttribute("status",status.getId());
		}
		if(internalStatus!=null){
			model.addAttribute("internalStatus",internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			/**** list of put up options available ****/
			/**** added by sandeep singh(jan 28 2013) ****/
			populateInternalStatus(model,internalStatus.getType(),usergroupType,locale);
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
		}

		/**** in case of assistant and other approving QIS actors ****/
		if(usergroupType.equals("assistant")){
			/**** level of current usergroup ****/
			model.addAttribute("level",1);				
			/**** Referenced Questions are collected in refentities****/
			List<Reference> refentities=new ArrayList<Reference>();
			List<ReferencedEntity> referencedEntities=domain.getReferencedEntities();
			if(referencedEntities!=null){
				for(ReferencedEntity re:referencedEntities){
					Reference reference=new Reference();
					reference.setId(String.valueOf(re.getId()));
					reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(re.getQuestion().getNumber()));
					reference.setNumber(String.valueOf(re.getQuestion().getId()));
					refentities.add(reference);			
				}
			}
			model.addAttribute("referencedQuestions",refentities);
			/**** Clubbed Questions are collected in references ****/
			List<Reference> references=new ArrayList<Reference>();
			List<ClubbedEntity> clubbedEntities=Question.findClubbedEntitiesByPosition(domain);
			StringBuffer buffer1=new StringBuffer();
			buffer1.append(memberNames+",");	
			if(clubbedEntities!=null){
				for(ClubbedEntity ce:clubbedEntities){
					Reference reference=new Reference();
					reference.setId(String.valueOf(ce.getId()));
					reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getQuestion().getNumber()));
					reference.setNumber(String.valueOf(ce.getQuestion().getId()));
					references.add(reference);
					String tempPrimary=ce.getQuestion().getPrimaryMember().getFullname();
					if(!buffer1.toString().contains(tempPrimary)){
						buffer1.append(ce.getQuestion().getPrimaryMember().getFullname()+",");
					}
					List<SupportingMember> clubbedSupportingMember=ce.getQuestion().getSupportingMembers();
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
			}
			if(!buffer1.toString().isEmpty()){
				buffer1.deleteCharAt(buffer1.length()-1);
			}
			String allMembersNames=buffer1.toString();
			model.addAttribute("memberNames",allMembersNames);
			if(!references.isEmpty()){
				model.addAttribute("clubbedQuestions",references);
			}else{
				if(domain.getParent()!=null){
					model.addAttribute("formattedParentNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
					model.addAttribute("parent",domain.getParent().getId());
				}
			}
		}
		//---------------------------Added by vikas & dhananjay-------------------------------------
		if(questionType.getType().equals("questions_halfhourdiscussion_from_question") || questionType.getType().equals("questions_halfhourdiscussion_standalone")){

			populateForHalfHourDiscussionEdit(model, domain, request);
		}
		//---------------------------Added by vikas & dhananjay-------------------------------------
	}


	private void populateInternalStatus(ModelMap model, String type,String userGroupType,String locale) {
		List<Status> internalStatuses=new ArrayList<Status>();
		/**** Since in case of send back and discuss internal status doesnot change.so it will come in case 1****/
		if(type.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				||type.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)
				||type.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
		){
			if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_FINAL","");
				if(customParameter!=null){
					internalStatuses=Status.findStatusContainedIn(customParameter.getValue(), locale);
				}else{
					model.addAttribute("errorcode","question_putup_options_final_notset");
				}		
			}else{
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(customParameter!=null){
					internalStatuses=Status.findStatusContainedIn(customParameter.getValue(), locale);
				}else{
					model.addAttribute("errorcode","question_putup_options_recommend_notset");
				}		
			}
		}
		/**** In case of put up status ****/
		/**** Internal Status****/
		model.addAttribute("internalStatuses",internalStatuses);
	}

	@Override
	protected void customValidateCreate(final Question domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}		
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** Approval ****/
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
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");
					}else{
						if(domain.getSupportingMembers().isEmpty()){
							result.rejectValue("supportingMembers","SupportingMembersEmpty");
						}
					}
					if(domain.getSupportingMembers()!=null){
						int count=0;
						for(SupportingMember i:domain.getSupportingMembers()){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.QUESTION_SUPPORTING_MEMBER_NOTSEND)){
								count++;
							}
						}
						if(count==0){
							result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
						}
					}
				}else
					if(operation.equals("submit")){
						/**** Submission ****/
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
						if(domain.getMinistry()==null){
							result.rejectValue("ministry","MinistryEmpty");
						}
					}
			}
		}/**** Drafts ****/
		else{
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
		}
	}

	@Override
	protected void populateCreateIfErrors(ModelMap model, Question domain,
			HttpServletRequest request) {
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}

	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
				domain.getGroup()!=null && (!domain.getSubject().isEmpty())
				&&(!domain.getQuestionText().isEmpty())){
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
			/**** In case of submission ****/
			String operation=request.getParameter("operation");
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						/****  submission date is set ****/
						if(domain.getSubmissionDate()==null){
							domain.setSubmissionDate(new Date());
						}
						/**** only those supporting memebrs will be included who have approved the requests ****/
						List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
						if(domain.getSupportingMembers()!=null){
							if(!domain.getSupportingMembers().isEmpty()){
								for(SupportingMember i:domain.getSupportingMembers()){
									if(i.getDecisionStatus().getType().trim().equals(ApplicationConstants.QUESTION_SUPPORTING_MEMBER_APPROVED)){
										supportingMembers.add(i);
									}
								}
								domain.setSupportingMembers(supportingMembers);
							}
						}
						/**** Status,Internal Status and recommendation Status is set ****/
						Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}
				}
			}
		}
		/**** Drafts ****/
		else{
			Status status=Status.findByFieldName(Status.class, "type", "ApplicationConstants.QUESTION_INCOMPLETE", domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}

		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		/**** Added by vikas & dhananjay ****/
		/**** to find referred question for half hour discussion from question ****/
		if(domain!=null && domain.getType() != null){
			Question refQuestion = null;

			if(domain.getType().getType().equalsIgnoreCase("questions_halfhourdiscussion_from_question")){
				String strQuestionId = request.getParameter("halfHourDiscussionReference_questionId_H");
				String strQuestionNumber = request.getParameter("halfHourDiscussionReference_questionNumber"); 

				if(strQuestionId!=null && !strQuestionId.isEmpty()){
					Long questionId = new Long(strQuestionId);
					refQuestion = Question.findById(Question.class, questionId);
				}else if(strQuestionNumber != null && !strQuestionNumber.isEmpty()){

					Integer qNumber = null;

					try {
						qNumber=new Integer(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).parse(strQuestionNumber).intValue());
					} catch (ParseException e) {
						logger.error("Number parse exception.");							
					}

					Session currentSession = Session.findById(Session.class, new Long(domain.getSession().getId()));
					Session prevSession = Session.findPreviousSession(currentSession);

					refQuestion = Question.find(currentSession, qNumber);
					if(refQuestion == null){
						refQuestion = Question.find(prevSession, qNumber);
					}
				}
				domain.setHalfHourDiscusionFromQuestionReference(refQuestion);
			}
		}
	}

	@Override
	protected void populateAfterCreate(final ModelMap model, final Question domain,
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
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					List<Task> tasks=processService.getCurrentTasks(processInstance);
					String strUserGroupType=request.getParameter("usergroupType");
					if(strUserGroupType!=null){
						UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType,domain.getLocale());
						WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,strUserGroupType,userGroupType.getName(),"");
					}else{
						WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"","","");
					}
					/**** Supporting members status changed to pending ****/
					Question question=Question.findById(Question.class,domain.getId());
					List<SupportingMember> supportingMembers=question.getSupportingMembers();
					Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SUPPORTING_MEMBER_PENDING,domain.getLocale());
					for(SupportingMember i:supportingMembers){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.QUESTION_SUPPORTING_MEMBER_NOTSEND)){
							i.setDecisionStatus(status);
							i.merge();
						}
					}
				}
			}
		}
	}

	@Override
	protected void customValidateUpdate(final Question domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** populate supporting members ****/
		populateSupportingMembers(domain,request);
		/**** Version mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		/**** Checking for mandatory fields during submit,normal create,approval ****/
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				/**** For Approval ****/
				if(operation.equals("approval")){
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
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");
					}else{
						if(domain.getSupportingMembers().isEmpty()){
							result.rejectValue("supportingMembers","SupportingMembersEmpty");
						}
					}
					if(domain.getSupportingMembers()!=null){
						int count=0;
						for(SupportingMember i:domain.getSupportingMembers()){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.QUESTION_SUPPORTING_MEMBER_NOTSEND)){
								count++;
							}
						}
						if(count==0){
							result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
						}
					}
				}else
					/**** Submission ****/
					if(operation.equals("submit")){
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
						if(domain.getMinistry()==null){
							result.rejectValue("ministry","MinistryEmpty");
						}

					}
			}
		}/**** Drafts ****/
		else{
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
		}
	}

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		/**** Question status will be complete if all mandatory fields have been filled ****/
		if(domain.getHouseType()!=null||domain.getType()!=null||domain.getSession()!=null
				||  domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
				domain.getGroup()!=null && (!domain.getSubject().isEmpty())
				&&(!domain.getQuestionText().isEmpty())){
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
			/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
			if(!domain.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)){
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
			/**** Checking if its submission request or normal update ****/
			String operation=request.getParameter("operation");
			if(operation!=null){
				if(!operation.isEmpty()){
					/**** Submission request ****/
					if(operation.trim().equals("submit")){
						/**** Submission date is set ****/
						if(domain.getSubmissionDate()==null){
							domain.setSubmissionDate(new Date());
						}
						/**** Supporting Members who have approved request are included ****/
						List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
						if(domain.getSupportingMembers()!=null){
							if(!domain.getSupportingMembers().isEmpty()){
								for(SupportingMember i:domain.getSupportingMembers()){
									if(i.getDecisionStatus().getType().trim().equals(ApplicationConstants.QUESTION_SUPPORTING_MEMBER_APPROVED)){
										supportingMembers.add(i);
									}
								}
								domain.setSupportingMembers(supportingMembers);
							}
						}
						/**** Status,Internal status and recommendation status is set to complete ****/
						Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}
				}
			}
		}
		/**** If all mandatory fields have not been set then status,internal status and recommendation status is set to incomplete ****/
		else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** Edited On,Edited By and Edited As is set ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		if(strUserGroupType!=null){
			if(strUserGroupType.equals("assistant")){
				Long id = domain.getId();
				Question question = Question.findById(Question.class, id);
				String internalStatus = question.getInternalStatus().getType();
				if(internalStatus.equals(ApplicationConstants.QUESTION_SUBMIT)&&domain.getMinistry()!=null&&domain.getGroup()!=null&&domain.getDepartment()!=null) {
					Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}	
			}
		}		
		/**** updating submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate!=null){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate!=null){
					domain.setCreationDate(format.parse(strCreationDate));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		/**** Added by vikas & dhananjay ****/
		/**** TO FIND REFERRED QUESTION FOR HalfHourDiscussionFromQuestion ****/
		if(domain!=null && domain.getType() != null){

			Question refQuestion = null;

			if(domain.getType().getType().equalsIgnoreCase("questions_halfhourdiscussion_from_question")){
				String strQuestionId = request.getParameter("halfHourDiscussionReference_questionId_H");
				String strQuestionNumber = request.getParameter("halfHourDiscussionReference_questionNumber"); 

				if(strQuestionId!=null && !strQuestionId.isEmpty()){
					Long questionId = new Long(strQuestionId);
					refQuestion = Question.findById(Question.class, questionId);
				}else if(strQuestionNumber != null && !strQuestionNumber.isEmpty()){   			

					Integer qNumber = null;

					try {
						qNumber=new Integer(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).parse(strQuestionNumber).intValue());
					} catch (ParseException e) {
						logger.error("Number parse exception.");							
					}

					Session currentSession = Session.findById(Session.class, new Long(domain.getSession().getId()));
					Session prevSession = Session.findPreviousSession(currentSession);

					refQuestion = Question.find(currentSession, qNumber);
					if(refQuestion == null){
						refQuestion = Question.find(prevSession, qNumber);
					}
				}
				domain.setHalfHourDiscusionFromQuestionReference(refQuestion);
			}
		}
	}

	@Override
	protected void populateAfterUpdate(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Approval Workflow ****/
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				/**** Supporting Member Workflow ****/
				if(operation.equals("approval")){
					/**** Added by Sandeep Singh ****/
					/**** Supporting Member Workflow is started ****/
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Workflow Details Entries are created ****/
					List<Task> tasks=processService.getCurrentTasks(processInstance);
					String strUserGroupType=request.getParameter("usergroupType");
					if(strUserGroupType!=null){
						UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType,domain.getLocale());
						WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,strUserGroupType,userGroupType.getName(),"");
					}else{
						WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"","","");
					}
					/**** Not Send supporting members status are changed to pending ****/
					Question question=Question.findById(Question.class,domain.getId());
					List<SupportingMember> supportingMembers=question.getSupportingMembers();
					Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SUPPORTING_MEMBER_PENDING,domain.getLocale());
					for(SupportingMember i:supportingMembers){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.QUESTION_SUPPORTING_MEMBER_NOTSEND)){
							i.setDecisionStatus(status);
							i.merge();
						}
					}
				}else if(operation.equals("startworkflow")){
					/**** Added by Sandeep Singh ****/
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actor");
					String nextUserGroupType="";
					String level="";
					if(nextuser!=null){
						if(!nextuser.isEmpty()){
							String[] temp=nextuser.split("#");
							properties.put("pv_user",temp[0]);
							nextUserGroupType=temp[1];
							level=temp[2];
						}
					}
					String endflag=request.getParameter("endflag");
					properties.put("pv_endflag",endflag);	
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",nextUserGroupType, domain.getLocale());
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails.create(domain,task,ApplicationConstants.APPROVAL_WORKFLOW,userGroupType.getType(),userGroupType.getName(),level);
							}
						}
					}
				}
			}
		}

		Status internalStatus = domain.getInternalStatus();
		String deviceType=domain.getType().getType();
		/**** Add to chart in case of starred question if internal and recommendation status is already
		 * assistant processed ****/
		if(internalStatus.getType().equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				&& deviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Question question = Question.findById(Question.class, domain.getId());
			Chart.addToChart(question);
		}
		/**** In case internal status and recommendation status is group changed ****/
		else if(internalStatus.getType().
				equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)
				&&deviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Question question = Question.findById(Question.class, domain.getId());
			QuestionDraft draft = question.findPreviousDraft();
			Group affectedGroup = draft.getGroup();
			Chart.groupChange(question, affectedGroup);
		}
	}	
	private void populateSupportingMembers(final Question domain,final HttpServletRequest request){
		/**** Supporting Members selected by Member in new/edit ****/
		String[] selectedSupportingMembers=request.getParameterValues("selectedSupportingMembers");
		/**** Supporting Members which are already present in domain ****/
		List<SupportingMember> members=new ArrayList<SupportingMember>();
		if(domain.getId()!=null){
			Question question=Question.findById(Question.class,domain.getId());
			members=question.getSupportingMembers();
		}		
		/**** New Status ****/
		Status notsendStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.QUESTION_SUPPORTING_MEMBER_NOTSEND, domain.getLocale());
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
					}
					/*** List is updated ****/
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
	}	

	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {
		Question question=Question.findById(Question.class, id);
		if(question!=null){
			Status status=question.getStatus();
			if(status.getType().equals(ApplicationConstants.QUESTION_INCOMPLETE)||status.getType().equals(ApplicationConstants.QUESTION_COMPLETE)){
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
	@RequestMapping(value="/status/{question}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("question") final String question){
		Question questionTemp=Question.findById(Question.class,Long.parseLong(question));
		List<SupportingMember> supportingMembers=questionTemp.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "question/supportingmember";
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
		return "question/citation";
	}

	@RequestMapping(value="/revisions/{questionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("questionId")  final Long questionId,
			final ModelMap model){
		List<QuestionRevisionVO> drafts=Question.getRevisions(questionId,locale.toString());
		model.addAttribute("drafts",drafts);
		return "question/revisions";
	}

	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "question/contacts";
	}

	//	@RequestMapping(value="/subject/{id}",method=RequestMethod.GET)
	//	public @ResponseBody MasterVO getSubject(final HttpServletRequest request,final ModelMap model,
	//			final @PathVariable("id")Long id){
	//		Question question=Question.findById(Question.class, id);
	//		MasterVO masterVO=new MasterVO();
	//		masterVO.setId(question.getId());
	//		if(question.getRevisedSubject()!=null){
	//			masterVO.setName(question.getRevisedSubject());
	//		}else{
	//			masterVO.setName(question.getSubject());
	//		}
	//		return masterVO;
	//	}


	//---------------------------Added by vikas & dhananjay----------------------------------------------
	/**
	 * To add parameters for new half hour discussion
	 * @param model  
	 * @param domain
	 * @param request
	 */
	private void populateForHalfHourDiscussionNew(final ModelMap model, final Question domain, final Session selectedSession, final DeviceType questionType, final HttpServletRequest request){


		if (selectedSession != null) {
			if (questionType.getType().equals("questions_halfhourdiscussion_from_question")) {

				Integer selYear = selectedSession.getYear();
				List<Reference> halfhourdiscussion_sessionYears = new ArrayList<Reference> ();

				Reference reference = new Reference();

				reference.setId(selYear.toString());
				reference.setName(FormaterUtil.formatNumberNoGrouping(new Integer(selYear), "mr_IN"));
				halfhourdiscussion_sessionYears.add(reference);

				reference = null;
				reference = new Reference();

				reference.setId((new Integer(selYear.intValue()-1)).toString());
				reference.setName(FormaterUtil.formatNumberNoGrouping(new Integer(selYear-1), "mr_IN"));
				halfhourdiscussion_sessionYears.add(reference);
				model.addAttribute("halfhourdiscussion_sessionYears",halfhourdiscussion_sessionYears);

				Session session = Session.findById(Session.class, selectedSession.getId());

				if (session != null) {

					String[] dates = session
					.getParameter("questions_halfhourdiscussion_from_question_discussionDates").split("#");
					List<String> discussionDates = new ArrayList<String>();

					try {
						SimpleDateFormat sdf = FormaterUtil.getDBDateParser(session.getLocale());
						for (int i = 0; i < dates.length; i++) {
							discussionDates.add(FormaterUtil.getDateFormatter("dd/MM/yyyy", session.getLocale()).format(sdf.parse(dates[i])));
						}
						model.addAttribute("discussionDates",discussionDates);
					} catch (ParseException e) {

						e.printStackTrace();
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

					CustomParameter dateFormatS = CustomParameter.findByFieldName(CustomParameter.class, "name", "SERVER_DATETIMEFORMAT", null);
					CustomParameter dateFormatDB = CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATETIMEFORMAT", null);

					if(dateFormatS != null && dateFormatDB != null ){
						Date startDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("questions_halfhourdiscussion_from_question_submissionStartDate"),dateFormatDB.getValue());
						Date endDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("questions_halfhourdiscussion_from_question_submissionEndDate"), dateFormatDB.getValue());

						model.addAttribute("startDate",FormaterUtil.formatDateToString(startDate, "yyyy/MM/dd hh:mm:ss"));
						model.addAttribute("endDate",FormaterUtil.formatDateToString(endDate, "yyyy/MM/dd hh:mm:ss"));
					}
				}
			}
		}         
	}

	//---------------------------Added by vikas & dhananjay-------------------------------------------------
	/**
	 * To add required parameters for half hour discussion when edit mode 
	 * @param model
	 * @param domain
	 * @param request
	 */
	private void populateForHalfHourDiscussionEdit(final ModelMap model, final Question domain, final HttpServletRequest request) {
		Session selectedSession = domain.getSession();
		DeviceType questionType = domain.getType();

		if (selectedSession != null) {

			Integer selYear = selectedSession.getYear();
			List<Reference> halfhourdiscussion_sessionYears = new ArrayList<Reference> ();

			Reference reference = new Reference();

			reference.setId(selYear.toString());
			reference.setName(FormaterUtil.formatNumberNoGrouping(new Integer(selYear), "mr_IN"));
			halfhourdiscussion_sessionYears.add(reference);

			reference = null;
			reference = new Reference();

			reference.setId((new Integer(selYear.intValue()-1)).toString());
			reference.setName(FormaterUtil.formatNumberNoGrouping(new Integer(selYear-1), "mr_IN"));
			halfhourdiscussion_sessionYears.add(reference);				

			model.addAttribute("halfhourdiscussion_sessionYears", halfhourdiscussion_sessionYears);

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

				if ((domain.getType().getType().equalsIgnoreCase("questions_halfhourdiscussion_from_question"))|| (domain.getType().getType().equalsIgnoreCase("questions_halfhourdiscussion_standalone"))) {

					Session session = domain.getSession();
					List<String> discussionDates = new ArrayList<String>();
					SimpleDateFormat sdf = null;

					if (session != null) {

						String[] dates = session.getParameter("questions_halfhourdiscussion_from_question_discussionDates").split("#");

						try {
							sdf = FormaterUtil.getDBDateParser(session.getLocale());
							for (int i = 0; i < dates.length; i++) {
								discussionDates.add(FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(sdf.parse(dates[i])));
							}
							model.addAttribute("discussionDates", discussionDates);
						} catch (ParseException e) {

							e.printStackTrace();
						}

						if (domain.getDiscussionDate() != null) {
							model.addAttribute("discussionDateSelected",FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
						}
						if (domain.getHalfHourDiscusionFromQuestionReference() != null) {
							if (domain.getHalfHourDiscusionFromQuestionReference()!= null) {
								model.addAttribute("referredQuestionNumber", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getHalfHourDiscusionFromQuestionReference().getNumber()));
								model.addAttribute("refQuestionId", domain.getHalfHourDiscusionFromQuestionReference().getId());
							}
						}
					}
				}
			}
		}
	}	

	//---------------------------Added by vikas & dhananjay----------------------------------------------
	@RequestMapping(value="/viewquestion",method=RequestMethod.GET)
	public String viewQuestion(final HttpServletRequest request,final ModelMap model,final Locale locale){

		String strQuestionId = request.getParameter("qid");
		if(strQuestionId != null && !strQuestionId.isEmpty()){

			Long id = new Long(strQuestionId);
			Question q = Question.findById(Question.class, id);
			model.addAttribute("referredQuestion",q);

			Member member=  q.getPrimaryMember();
			if(member.getId()!=null){          
				model.addAttribute("primaryMemberName",member.getFullname());
			}
			return "question/viewquestion";
		}else{
			return "question/viewquestion";
		}
	}


}

