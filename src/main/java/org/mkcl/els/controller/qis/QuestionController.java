package org.mkcl.els.controller.qis;

import java.io.File;
import java.io.IOException;
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
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.MinistryVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.RoundVO;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.common.xmlvo.QuestionYaadiSuchiXmlVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Ballot;
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
import org.mkcl.els.domain.MessageResource;
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
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
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
@RequestMapping("question")
public class QuestionController extends GenericController<Question>{

	@Autowired
	private IProcessService processService;

	@Override
	protected void populateModule(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {

		/**** Question Types ****/
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){			
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				deviceTypes.add(deviceType);
			}else{
				try{
					deviceTypes = DeviceType.findDeviceTypesStartingWith("questions", locale);				
					DeviceType HDS = DeviceType.findByFieldName(DeviceType.class, "type", ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE, locale);
					if(HDS != null){
						deviceTypes.remove(HDS);
					}
				}catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
				}
			}			
			model.addAttribute("questionTypes", deviceTypes);
			/**** Default Value ****/
			model.addAttribute("questionType",deviceType.getId());
			/**** Access Control Based on Question Type(fields hide/show) ****/
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
			Session lastSessionCreated = null;
			try {
				lastSessionCreated = Session.findLatestSession(authUserHouseType);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}

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
			/*** In a device type a single user can have only one user group type..But across devices he/she can have multiple
			 * user group types with one user group type per device type****/
			List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
			String userGroupType=null;
			if(userGroups!=null){
				if(!userGroups.isEmpty()){
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"QIS_ALLOWED_USERGROUPTYPES", "");
					if(customParameter!=null){
						String allowedUserGroups=customParameter.getValue(); 
						for(UserGroup i:userGroups){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								/**** Authenticated User's usergroup and usergroupType ****/
								model.addAttribute("usergroup",i.getId());
								userGroupType=i.getUserGroupType().getType();
								model.addAttribute("usergroupType",userGroupType);
								Map<String,String> parameters=UserGroup.findParametersByUserGroup(i);

								/**** Sub department filter on grid should be visible only to user groups which have permission ****/
								CustomParameter subDepartmentFilterAllowedFor=CustomParameter.findByName(CustomParameter.class,"QIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
								if(subDepartmentFilterAllowedFor!=null && subDepartmentFilterAllowedFor.getValue().contains(userGroupType)){
									if(parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).equals(" ")){
										String strSubDepartments=parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale);
										String subDepartments[]=strSubDepartments.split("##");
										List<SubDepartment> subDepts=new ArrayList<SubDepartment>();
										for(int j=0;j<subDepartments.length;j++){
											SubDepartment subDepartment=SubDepartment.findByName(SubDepartment.class, subDepartments[j], locale);
											subDepts.add(subDepartment);
										}
										model.addAttribute("subDepartments", subDepts);
									}
								}


								/**** Question Status Allowed(statuses visible on grid) ****/
								CustomParameter allowedStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_GRID_STATUS_ALLOWED_"+userGroupType.toUpperCase(), "");
								List<Status> status=new ArrayList<Status>();
								if(allowedStatus!=null){
									try {
										status=Status.findStatusContainedIn(allowedStatus.getValue(),locale);
									} catch (ELSException e) {
										model.addAttribute("error", e.getParameter());
										e.printStackTrace();
									}
								}else{
									CustomParameter defaultAllowedStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
									if(defaultAllowedStatus!=null){
										try {
											status=Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
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

			/**** Roles(All roles in QIS will start with QIS_,MEMBER_,HDS_) 
			 * In a single device type there will be one role per user *****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().startsWith("QIS_")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().startsWith("HDS_")&&deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					model.addAttribute("role",i.getType());
					break;
				}
			}

			/*** ugparam is used to load data in grid and it is either by username or group ****/
			/*** While creating user we define what groups are allowed for a particular user  
			 * Groups allowed field will be empty for members and typists****/
			String strgroups=this.getCurrentUser().getGroupsAllowed();
			model.addAttribute("allowedGroups",strgroups);
			if(strgroups!=null){
				if(!strgroups.isEmpty()){
					List<Group> groups=new ArrayList<Group>();
					String[] gr=strgroups.split(",");
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

			/**** File Options(Obtain Dynamically)-Left to be tested****/
			if(userGroupType!=null&&!userGroupType.isEmpty()&&userGroupType.equals("assistant")){
				int highestFileNo;
				try {
					highestFileNo = Question.findHighestFileNo(lastSessionCreated,deviceType,locale);
					model.addAttribute("highestFileNo",highestFileNo);
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}
			}
		}else{
			model.addAttribute("errorcode","workunderprogress");
		}		
	}

	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		/**** Controlling visibility of assistant grid through usergroup query parameter in grid ****/
		String role=request.getParameter("role");
		String newUrlPattern=urlPattern;
		CustomParameter assistantGridNotAllowedFor=CustomParameter.findByName(CustomParameter.class,"QIS_ASSISTANTGRID_NOT_ALLOWED_FOR","");
		if(assistantGridNotAllowedFor!=null){
			if(assistantGridNotAllowedFor.getValue().contains(role)){
				newUrlPattern=urlPattern+"?usergroup=typist";
			}else{
				if(role.contains("HDS_")){
					newUrlPattern=ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE.toUpperCase();
				}else{
					newUrlPattern=urlPattern+"?usergroup=assistant";
				}
			}
		}		
		return newUrlPattern;
	}


	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		/**** Member and Clerk can only create new questions ****/
		String role=request.getParameter("role");	
		CustomParameter newOperationAllowedTo=CustomParameter.findByName(CustomParameter.class,"QIS_NEW_OPERATION_ALLOWED_TO","");
		if(newOperationAllowedTo!=null){
			if(newOperationAllowedTo.getValue().contains(role)){
				return servletPath;
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

		String memberNames=null;
		String primaryMemberName=null;

		/**** Session ****/
		Session selectedSession=null;
		if(houseType!=null&&selectedYear!=null&&sessionType!=null){
			try {
				selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			} catch (ELSException e1) {
				model.addAttribute("error", e1.getParameter());
				e1.printStackTrace();
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

		/**** Primary Member****/		
		if(role.startsWith("MEMBER")){
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

			/**** Constituency(visible only to members)****/
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
		if(questionType.getType().trim().equals("questions_starred")){

			Date rotationOrderPubDate=null;
			CustomParameter serverDateFormat = null;
			String strRotationOrderPubDate = null;
			try{
				serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
				if(strRotationOrderPubDate!=null){

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
							if(domain.getType() != null){
								if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
										&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
									model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
									model.addAttribute("group",domain.getGroup().getId());
								}
							}

							/**** Departments ****/
							List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
							model.addAttribute("departments",departments);
							//Department department=domain.getDepartment();
							//if(department!=null){                            	
							//model.addAttribute("departmentSelected",department.getId());
							/**** Sub Departments ****/
							List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry, locale);
							model.addAttribute("subDepartments",subDepartments);
							SubDepartment subDepartment=domain.getSubDepartment();
							if(subDepartment!=null){
								model.addAttribute("subDepartmentSelected",subDepartment.getId());
							}
							//}

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
				}else{
					logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
					model.addAttribute("errorcode", "rotationorderpubdate_notset");
				}
			}catch (ParseException e) {
				logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
				model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				String message = e.getMessage();
				if(message == null){
					message = "** There is some problem, request may not complete successfully.";
				}
				model.addAttribute("error", message);
				e.printStackTrace();
			}
		}else{
			List<Ministry> ministries = null;;
			try {
				ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addAttribute("ministries",ministries);
			Ministry ministry=domain.getMinistry();
			if(ministry!=null){
				model.addAttribute("ministrySelected",ministry.getId());

				/**** Group ****/
				Group group=domain.getGroup();
				if(group!=null) {
					model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
					model.addAttribute("group",domain.getGroup().getId());
				}

				/**** Departments ****/
				List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
				//model.addAttribute("departments",departments);
				//Department department=domain.getDepartment();
				//if(department!=null){                            	
				//model.addAttribute("departmentSelected",department.getId());
				/**** Sub Departments ****/
				List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry, locale);
				model.addAttribute("subDepartments",subDepartments);
				SubDepartment subDepartment=domain.getSubDepartment();
				if(subDepartment!=null){
					model.addAttribute("subDepartmentSelected",subDepartment.getId());
				}
				//}
			}
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

		//---------------------------Added by vikas & dhananjay-------------------------------------
		if(questionType != null){
			if(questionType.getType().equals("questions_halfhourdiscussion_from_question") || questionType.getType().equals("questions_halfhourdiscussion_standalone")){
				populateForHalfHourDiscussionNew(model, domain, selectedSession, questionType, request);
			}
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

		CustomParameter editPage = CustomParameter.findByName(CustomParameter.class, "QIS_EDIT_OPERATION_EDIT_PAGE", "");
		CustomParameter assistantPage = CustomParameter.findByName(CustomParameter.class, "QIS_EDIT_OPERATION_ASSISTANT_PAGE", "");

		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(editPage != null && editPage.getValue().contains(i.getType())) {
				return newUrlPattern;
			}
			else if(assistantPage != null && assistantPage.getValue().contains(i.getType())) {
				return newUrlPattern.replace("edit", "assistant");
			}
			else if(i.getType().startsWith("QIS_")) {
				return newUrlPattern.replace("edit", "editreadonly");
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
		model.addAttribute("houseTypeType", houseType.getType());
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

		/**** Original Question Type ****/		
		if(domain.getOriginalType()!=null) {
			model.addAttribute("originalType",domain.getOriginalType().getId());
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
				/**** Dhananjay Borkar ****/
				//bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
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
			CustomParameter serverDateFormat = null;
			String strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
			if(strRotationOrderPubDate!=null){
				try {
					serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
					model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
					Date currentDate=new Date();
					if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
						List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
						model.addAttribute("ministries",ministries);
						Ministry ministry=domain.getMinistry();
						if(ministry!=null){
							model.addAttribute("ministrySelected",ministry.getId());
							model.addAttribute("formattedMinistry",ministry.getName());
							/**** Group ****/
							Group group=domain.getGroup();
							if(domain.getType() != null){
								if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
										&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
									model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
									model.addAttribute("group",domain.getGroup().getId());
								}
							}

							/**** Departments ****/
							List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
							model.addAttribute("departments",departments);

							if(domain.getDepartment()!=null){
								Department department=domain.getDepartment();
								if(department!=null){
									model.addAttribute("departmentSelected",department.getId());
								}
							}


							/**** Sub Departments ****/
							//List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,department, locale);
							List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry, locale);
							model.addAttribute("subDepartments",subDepartments);
							SubDepartment subDepartment=domain.getSubDepartment();
							if(subDepartment!=null){
								model.addAttribute("subDepartmentSelected",subDepartment.getId());
							}
							//}

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

							if(domain.getChartAnsweringDate() != null){
								model.addAttribute("formattedChartAnsweringDate",FormaterUtil.getDateFormatter(locale).format(domain.getChartAnsweringDate().getAnsweringDate()));
								model.addAttribute("chartAnsweringDate",domain.getChartAnsweringDate().getId());
							}
						}
					}							
				} catch (ParseException e) {
					logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
					model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}catch (Exception e) {
					String message = e.getMessage();
					if(e.getMessage() == null){
						message = "**There is some problem, request may not complete successfully.";
					}
					model.addAttribute("error", message);
					e.printStackTrace();					
				}
			}else{
				logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
				model.addAttribute("errorcode", "rotationorderpubdate_notset");
			}
		}else{
			List<Ministry> ministries = null;
			try {
				ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
			model.addAttribute("ministries",ministries);
			Ministry ministry=domain.getMinistry();
			if(ministry!=null){
				model.addAttribute("ministrySelected",ministry.getId());
				/**** Group ****/
				Group group=domain.getGroup();
				if(domain.getType() != null){
					if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
							&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
						model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
						model.addAttribute("group",domain.getGroup().getId());
					}
				}

				/**** Departments ****/
				List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
				model.addAttribute("departments",departments);
				if(domain.getDepartment()!=null){  
					model.addAttribute("departmentSelected",domain.getDepartment().getId());
				}
				/**** Sub Departments ****/
				List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry, locale);
				//MemberMinister.findAssignedSubDepartments(ministry,department, locale);
				model.addAttribute("subDepartments",subDepartments); 
				SubDepartment subDepartment=domain.getSubDepartment();
				if(subDepartment!=null){
					model.addAttribute("subDepartmentSelected",subDepartment.getId());
				}
				//}

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
		/**** role ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}

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
		String strUsergroup=request.getParameter("usergroup");
		if(strUsergroup!=null){
			model.addAttribute("usergroup",strUsergroup);
		}else{
			strUsergroup=(String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",strUsergroup);
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
			model.addAttribute("memberStatusType",status.getType());
		}
		if(internalStatus!=null){
			model.addAttribute("internalStatus",internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			/**** list of put up options available ****/
			/**** added by sandeep singh(jan 28 2013) ****/
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("assistant")){
				populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,questionType.getType());
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

		/**** in case of assistant and other approving QIS actors ****/
		if(usergroupType.equals("assistant")){
			/**** level of current usergroup ****/
			model.addAttribute("level",1);				
			/**** Referenced Questions are collected in refentities****/
			List<Reference> refentities=new ArrayList<Reference>();
			List<String> refentitiesSessionDevice = new ArrayList<String>();
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				if(domain.getReferencedHDS() != null){

					ReferencedEntity refEntity = domain.getReferencedHDS();

					Reference reference=new Reference();
					reference.setId(String.valueOf(refEntity.getId()));
					Question refQuestion = (Question)refEntity.getDevice();
					reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(refQuestion.getNumber()));
					reference.setNumber(String.valueOf(refQuestion.getId()));
					refentities.add(reference);

					Session referencedQuestionSession = refQuestion.getSession();
					refentitiesSessionDevice.add("[" + referencedQuestionSession.getType().getSessionType()+", "+
							FormaterUtil.formatNumberNoGrouping(referencedQuestionSession.getYear(), locale) + "], " + 
							refQuestion.getType().getName());

					model.addAttribute("referencedQuestions",refentities);
					model.addAttribute("referencedHDS", refEntity.getId());
					model.addAttribute("referencedQuestionsSessionAndDevice", refentitiesSessionDevice);

				}
			}else{
				List<ReferencedEntity> referencedEntities=domain.getReferencedEntities();
				if(referencedEntities!=null){
					for(ReferencedEntity re:referencedEntities){
						if(re.getDeviceType() != null){
							if(re.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
								Reference reference=new Reference();
								reference.setId(String.valueOf(re.getId()));
								reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(((Question)re.getDevice()).getNumber()));
								reference.setNumber(String.valueOf(((Question)re.getDevice()).getId()));
								refentities.add(reference);

								model.addAttribute("referencedQuestions",refentities);
							}
						}
					}
				}
			}

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
		Status tempStatus = Status.findByFieldName(Status.class, "type", "question_final_rejection", domain.getLocale());
		boolean canRemark = false;	
		String errorMessagePossible="";

		try{
			errorMessagePossible = "domain_not_found";
			if (domain.getInternalStatus().getType().equals(tempStatus.getType())) {

				errorMessagePossible = "questiondraft_not_found_for_remark";
				QuestionDraft qDraft = domain.findPreviousDraft();					
				model.addAttribute("sectionofficer_remark",qDraft.getRemarks());

				canRemark = true;
			}
		}catch(Exception e){
			model.addAttribute("errorcode",errorMessagePossible);
			logger.error("Remark not found."+e.getMessage());
		}

		if(!canRemark){
			model.addAttribute("sectionofficer_remark","");
		}
		if(questionType != null){
			if(questionType.getType().equals("questions_halfhourdiscussion_from_question") || questionType.getType().equals("questions_halfhourdiscussion_standalone")){

				populateForHalfHourDiscussionEdit(model, domain, request);
			}
		}
		/**** Populating Put up options and Actors ****/
		if(domain.getInternalStatus()!=null){
			String internalStatusType=domain.getInternalStatus().getType();			
			if(usergroupType!=null&&!usergroupType.isEmpty()&&usergroupType.equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)
							||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
							||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
							||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
							)){
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUsergroup));
				List<Reference> actors=WorkflowConfig.findQuestionActorsVO(domain, internalStatus, userGroup, 1, locale);
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
		//---------------------------Added by vikas & dhananjay-------------------------------------
	}


	private void populateInternalStatus(final ModelMap model, final String type,final String userGroupType,final String locale, final String questionType) {
		List<Status> internalStatuses=new ArrayList<Status>();
		try{
			/**** First we will check if custom parameter for device type,internal status and usergroupType has been set ****/
			CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			if(specificDeviceStatusUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			}else if(specificStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)){
				CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus!=null){
						internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter!=null){
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode","question_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus!=null){
					internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode","question_putup_options_final_notset");
					}		
				}
			}	
			/**** Internal Status****/
			model.addAttribute("internalStatuses",internalStatuses);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void customValidateCreate(final Question domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** In case of typist we need to check if number has been added****/
		String role=request.getParameter("role");

		/**** Supporting Members and various Validations(approval allowed through auto approval(typist) and through workflow) ****/
		populateSupportingMembers(domain,request);

		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
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
					if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
							&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
							&& domain.getQuestionText().isEmpty()){
						result.rejectValue("questionText","QuestionTextEmpty");
					}
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");						
					} else {
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
							validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);
						}else if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
							validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
						}
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
				}else
					if(operation.equals("submit")){
						/**** Submission ****/
						/**** Temorary code for checking number validation in case of typists ****/
						if(role.equals("QIS_TYPIST")){
							if(domain.getNumber()==null){
								result.rejectValue("number","NumberEmpty");
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
						if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
								&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
								&& domain.getQuestionText().isEmpty()){
							result.rejectValue("questionText","QuestionTextEmpty");
						}
						if(domain.getMinistry()==null){
							result.rejectValue("ministry","MinistryEmpty");
						}
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
							validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);
						}else if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
							validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
						}
					}
			}
		}/**** Drafts ****/
		else{
			/**** Temorary code for checking number validation in case of typists ****/
			if(role.equals("QIS_TYPIST")){
				if(domain.getNumber()==null){
					result.rejectValue("number","NumberEmpty");
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
			if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
					&& domain.getQuestionText().isEmpty()){
				result.rejectValue("questionText","QuestionTextEmpty");
			}
			validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);
		}
	}

	@Override
	protected void populateCreateIfErrors(final ModelMap model, final Question domain,
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
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");

		if(domain.getPrimaryMember()!=null){
			model.addAttribute("formattedPrimaryMember", domain.getPrimaryMember().getFullname());
			model.addAttribute("primaryMember", domain.getPrimaryMember().getId());
		}

		if(domain.getMinistry()!=null){
			model.addAttribute("formattedMinistry", domain.getMinistry().getName());
			model.addAttribute("ministrySelected", domain.getMinistry().getId());
		}
	}

	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final Question domain,
			final HttpServletRequest request) {

		if(domain.getSubDepartment()!=null ){
			domain.setDepartment(domain.getSubDepartment().getDepartment());
		}

		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		boolean canGoAhead = false;
		String strUserGroupType=request.getParameter("usergroupType");
		UserGroupType userGroupType=null;
		if(strUserGroupType!=null){
			userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		if(domain.getType() != null){
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				if(domain.getHouseType() != null){
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						if(domain.getSession()!=null &&  domain.getType()!=null 
								&& domain.getPrimaryMember()!=null && domain.getMinistry()!=null && (!domain.getSubject().isEmpty()) 
								&& (!domain.getQuestionText().isEmpty())){
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
			}else{
				if(domain.getHouseType()!=null && domain.getSession()!=null &&  domain.getType()!=null 
						&& domain.getPrimaryMember()!=null && domain.getMinistry()!=null && domain.getGroup()!=null 
						&& (!domain.getSubject().isEmpty()) && (!domain.getQuestionText().isEmpty())){
					canGoAhead = true;
				}else{
					canGoAhead = false;
				}
			}
		}
		if(canGoAhead){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						/****  submission date is set ****/
						if(domain.getSubmissionDate()==null){
							domain.setSubmissionDate(new Date());
						}
						/**** only those supporting members will be included who have approved the requests ****/
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
						/**** Status,Internal Status and recommendation Status is set ****/
						Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}else{
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}else{
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		}
		/**** Drafts ****/
		else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}

		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());

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

					try {
						Session currentSession = Session.findById(Session.class, new Long(domain.getSession().getId()));
						DeviceType halfHourDiscussionStandAlone = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE, domain.getLocale());
						Session prevSession = Session.findPreviousSession(currentSession);

						//---------------------21012013
						if(halfHourDiscussionStandAlone != null){

							refQuestion = Question.findQuestionExcludingGivenDeviceTypes(currentSession, qNumber, domain.getLocale(), domain.getType().getId(), halfHourDiscussionStandAlone.getId());
							if(refQuestion == null){
								refQuestion = Question.findQuestionExcludingGivenDeviceTypes(prevSession, qNumber, domain.getLocale(), domain.getType().getId(), halfHourDiscussionStandAlone.getId());
							}
						}
						//-------------------------------------------------
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
						e.printStackTrace();
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
					properties.put("pv_deviceTypeId",domain.getType().getType());
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					List<Task> tasks=processService.getCurrentTasks(processInstance);
					List<WorkflowDetails> workflowDetails;
					try {
						workflowDetails = WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"0");

						/**** Supporting members status changed to pending ****/
						Question question=Question.findById(Question.class,domain.getId());
						List<SupportingMember> supportingMembers=question.getSupportingMembers();
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
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void customValidateUpdate(final Question domain, final BindingResult result,
			final HttpServletRequest request) {

		String role=request.getParameter("role");
		String userGroupType=request.getParameter("usergroupType");
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
					if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
							&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
							&& domain.getQuestionText().isEmpty()){

						result.rejectValue("questionText","QuestionTextEmpty");
					}
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers","SupportingMembersEmpty");						
					} else {
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
							validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);
						}else if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
							validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
						}

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
				}else
					/**** Submission ****/
					if(operation.equals("submit")){
						/**** Temorary code for checking number validation in case of typists ****/
						if(role.equals("QIS_TYPIST")){
							if(domain.getNumber()==null){
								result.rejectValue("number","NumberEmpty");
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
						if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
								&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
								&& domain.getQuestionText().isEmpty()){
							result.rejectValue("questionText","QuestionTextEmpty");
						}
						if(domain.getMinistry()==null){
							result.rejectValue("ministry","MinistryEmpty");
						}
						//						if(domain.getDepartment()==null){
						//							result.rejectValue("department","DepartmentEmpty");
						//						}
						//						if(domain.getSubDepartment()==null){
						//							result.rejectValue("subDepartment","SubDepartmentEmpty");
						//						}
						validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);

					}
			}
		}/**** Drafts ****/
		else{
			/**** Temorary code for checking number validation in case of typists ****/
			if(role.equals("QIS_TYPIST")){
				if(domain.getNumber()==null){
					result.rejectValue("number","NumberEmpty");
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
			if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE))
					&& domain.getQuestionText().isEmpty()){
				result.rejectValue("questionText","QuestionTextEmpty");
			}
			//String userGroupType=request.getParameter("usergroupType");
			if(userGroupType != null){
				if(!userGroupType.isEmpty()){
					if(userGroupType.equals(ApplicationConstants.MEMBER)){
						validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);
					}
				}
			}
		}
	}

	@Override
	protected void populateUpdateIfErrors(final ModelMap model, final Question domain,
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
		super.populateUpdateIfErrors(model, domain, request);
	}

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final Question domain,
			final HttpServletRequest request) {

		if(domain.getSubDepartment()!=null ){
			domain.setDepartment(domain.getSubDepartment().getDepartment());
		}
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
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				if (domain.getHouseType() != null) {
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						if (domain.getHouseType() != null
								&& domain.getType() != null
								&& domain.getSession() != null
								&& domain.getPrimaryMember() != null
								&& domain.getMinistry() != null
								&& (!domain.getSubject().isEmpty())
								&& (!domain.getQuestionText().isEmpty())) {
							canGoAhead = true;
						} else {
							canGoAhead = false;
						}
					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						if (domain.getHouseType() != null
								&& domain.getType() != null
								&& domain.getSession() != null
								&& domain.getPrimaryMember() != null
								&& domain.getMinistry() != null
								&& domain.getGroup() != null
								&& (!domain.getSubject().isEmpty())) {
							domain.setQuestionText("");
							canGoAhead = true;
						} else {
							canGoAhead = false;
						}
					}
				}
			}else{
				if(domain.getHouseType()!=null&&domain.getType()!=null&&domain.getSession()!=null
						&& domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
						domain.getGroup()!=null && (!domain.getSubject().isEmpty())
						&&(!domain.getQuestionText().isEmpty())){
					canGoAhead = true;
				}else{
					canGoAhead = false;
				}
			}
		}
		if(canGoAhead){			
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
									if(userGroupType.getType().equals(ApplicationConstants.CLERK)){
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
						/**** Status,Internal status and recommendation status is set to complete ****/
						Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_SUBMIT, domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}else{
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
						/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
						if(!domain.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT) && !operation.trim().equals("startworkflow")){
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}

						/**
						 * Added by Amit
						 * Set answeringdate = chartAnsweringDate whenever a Question is put up. 
						 */
						if(operation.trim().equals("startworkflow") && 
								domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
							domain.setAnsweringDate(domain.getChartAnsweringDate());
						}
					}
				}else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
					/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
					if(!domain.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)){
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}
			}else{
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_COMPLETE, domain.getLocale());
				/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
				if(!domain.getStatus().getType().equals(ApplicationConstants.QUESTION_SUBMIT)){
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
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


		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		if(strUserGroupType!=null){
			if(strUserGroupType.equals("assistant")){
				Long id = domain.getId();
				Question question = Question.findById(Question.class, id);
				String internalStatus = question.getInternalStatus().getType();
				if(domain.getType() != null){
					if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
							&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						if(internalStatus.equals(ApplicationConstants.QUESTION_SUBMIT) && domain.getMinistry()!=null) {
							Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
							domain.setInternalStatus(ASSISTANT_PROCESSED);
							domain.setRecommendationStatus(ASSISTANT_PROCESSED);
						}else{
							if(internalStatus.equals(ApplicationConstants.QUESTION_SUBMIT) && domain.getMinistry()!=null && domain.getSubDepartment() != null) {
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatus(ASSISTANT_PROCESSED);
								domain.setRecommendationStatus(ASSISTANT_PROCESSED);
							}
						}
					}else{
						/*
						 * Modified by Amit
						 * In case of Group Change, set the internal & recommendation
						 * status to GROUP_CHANGED
						 */
						Group group = domain.getGroup();
						if((internalStatus.equals(ApplicationConstants.QUESTION_SUBMIT)||internalStatus.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)) && domain.getMinistry()!=null && group!=null && domain.getSubDepartment()!=null) {
							Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
							domain.setInternalStatus(ASSISTANT_PROCESSED);
							domain.setRecommendationStatus(ASSISTANT_PROCESSED);
						}

						QuestionDraft draft = domain.findPreviousDraft();				        
						if(group != null && draft != null) {
							Group prevGroup = draft.getGroup();
							if(prevGroup != null && ! prevGroup.getNumber().equals(group.getNumber())) {
								Status GROUP_CHANGED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED, domain.getLocale());
								domain.setInternalStatus(GROUP_CHANGED);
								domain.setRecommendationStatus(GROUP_CHANGED);
							}
						}
					}
				}
				/**** File parameters are set when internal status is something other than 
				 * submit,complete and incomplete and file is null .Then only the motion gets attached to a file.*/
				String currentStatus=domain.getInternalStatus().getType();
				if(operation==null){
					if(!domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
							|| !(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
									&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))
									&&!(currentStatus.equals(ApplicationConstants.QUESTION_SUBMIT)
											||currentStatus.equals(ApplicationConstants.QUESTION_COMPLETE)
											||currentStatus.equals(ApplicationConstants.QUESTION_INCOMPLETE))
											&& (domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
													||domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
													||domain.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION))							
													&&domain.getFile()==null){
						/**** Add Question to file ****/
						Reference reference;
						try {
							reference = Question.findCurrentFile(domain);
							domain.setFile(Integer.parseInt(reference.getId()));
							domain.setFileIndex(Integer.parseInt(reference.getName()));
							domain.setFileSent(false);
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
							e.printStackTrace();
						}
					}
				}else if(operation.isEmpty()){
					if(!domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
							|| !(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
									&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))
									&&!(currentStatus.equals(ApplicationConstants.QUESTION_SUBMIT)
											||currentStatus.equals(ApplicationConstants.QUESTION_COMPLETE)
											||currentStatus.equals(ApplicationConstants.QUESTION_INCOMPLETE))
											&& (domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
													||domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
													||domain.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION))							
													&&domain.getFile()==null){
						/**** Add Question to file ****/
						Reference reference;
						try {
							reference = Question.findCurrentFile(domain);
							domain.setFile(Integer.parseInt(reference.getId()));
							domain.setFileIndex(Integer.parseInt(reference.getName()));
							domain.setFileSent(false);
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
							e.printStackTrace();
						}

					}
				}
			}
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
				if(strSubmissionDate!=null){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate!=null){
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

					try {
						Session currentSession = Session.findById(Session.class, new Long(domain.getSession().getId()));
						DeviceType halfHourDiscussionStandAlone = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE, domain.getLocale());
						Session prevSession = Session.findPreviousSession(currentSession);

						//---------------------21012013
						if(halfHourDiscussionStandAlone != null){
							refQuestion = Question.findQuestionExcludingGivenDeviceTypes(currentSession, qNumber, domain.getLocale(), domain.getType().getId(), halfHourDiscussionStandAlone.getId());
							if(refQuestion == null){
								refQuestion = Question.findQuestionExcludingGivenDeviceTypes(prevSession, qNumber, domain.getLocale(), domain.getType().getId(), halfHourDiscussionStandAlone.getId());
							}
						}
						//-------------------------------------------------
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
					}
				}				
				domain.setHalfHourDiscusionFromQuestionReference(refQuestion);
			}
		}
	}

	@Override
	protected void populateAfterUpdate(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		/**** In case of error when request gets re-directed to .../edit then these parameters are read from session****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		request.getSession().setAttribute("bulkedit",request.getParameter("bulkedit"));

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
					properties.put("pv_deviceTypeId",domain.getType().getType());
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Workflow Details Entries are created ****/
					List<Task> tasks=processService.getCurrentTasks(processInstance);					
					List<WorkflowDetails> workflowDetails;
					try {
						workflowDetails = WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"");

						/**** Not Send supporting members status are changed to pending ****/
						Question question=Question.findById(Question.class,domain.getId());
						List<SupportingMember> supportingMembers=question.getSupportingMembers();
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
						e.printStackTrace();
					}

				}else if(operation.equals("startworkflow")){
					if(domain.getType() != null){
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
								&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

							ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
							Map<String,String> properties=new HashMap<String, String>();					
							/**** Next user and usergroup ****/
							String nextuser=request.getParameter("actor");
							String level="";
							String username = "";
							if(nextuser!=null){
								if(!nextuser.isEmpty()){
									String[] temp=nextuser.split("#");
									username = temp[0];
									properties.put("pv_user",username);
									level=temp[2];
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

											//String remindersubject=request.getParameter("remindersubject");
											String remindersubject=domain.getSubject();
											properties.put("pv_remindersubject", remindersubject);

											//String remindercontent=request.getParameter("remindercontent");
											String remindercontent=domain.getQuestionText();
											properties.put("pv_remindercontent", remindercontent);
										}
									}
								}
							}
							ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
							/**** Stale State Exception ****/
							Question question=Question.findById(Question.class,domain.getId());
							/**** Process Started and task created ****/
							Task task=processService.getCurrentTask(processInstance);
							if(endflag!=null){
								if(!endflag.isEmpty()){
									if(endflag.equals("continue")){
										/**** Workflow Detail entry made only if its not the end of workflow ****/								
										WorkflowDetails workflowDetails;
										try {
											workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,level);
											question.setWorkflowDetailsId(workflowDetails.getId());
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}

									}
								}
							}
						}else{
							/**** Added by Sandeep Singh ****/
							ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
							Map<String,String> properties=new HashMap<String, String>();					
							/**** Next user and usergroup ****/
							String nextuser=request.getParameter("actor");
							String level="";
							if(nextuser!=null){
								if(!nextuser.isEmpty()){
									String[] temp=nextuser.split("#");
									properties.put("pv_user",temp[0]);
									level=temp[2];
								}
							}
							String endflag=domain.getEndFlag();
							properties.put("pv_endflag",endflag);	
							properties.put("pv_deviceId",String.valueOf(domain.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
							ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
							/**** Stale State Exception ****/
							Question question=Question.findById(Question.class,domain.getId());
							/**** Process Started and task created ****/
							Task task=processService.getCurrentTask(processInstance);
							if(endflag!=null){
								if(!endflag.isEmpty()){
									if(endflag.equals("continue")){
										/**** Workflow Detail entry made only if its not the end of workflow ****/
										WorkflowDetails workflowDetails;
										try {
											workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.APPROVAL_WORKFLOW,level);
											question.setWorkflowDetailsId(workflowDetails.getId());
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}

									}
								}
							}
							/**** Workflow Started ****/
							question.setWorkflowStarted("YES");
							question.setWorkflowStartedOn(new Date());
							question.setTaskReceivedOn(new Date());
							/**** If question is sent individually then its file's parameters is set to null i.e 
							 * it is removed from file ****/
							question.setFile(null);
							question.setFileIndex(null);
							question.setFileSent(false);
							question.simpleMerge();
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
			try{
				Chart.addToChart(question);
			}catch (ELSException e) {
				model.addAttribute("QuestionController", e.getParameter());
			}
		}
		/**** In case internal status and recommendation status is group changed ****/
		else if(internalStatus.getType().
				equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)
				&&deviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Question question = Question.findById(Question.class, domain.getId());
			QuestionDraft draft = question.findSecondPreviousDraft();
			Group affectedGroup = draft.getGroup();
			try{
				Chart.groupChange(question, affectedGroup);
			}catch (ELSException e) {
				model.addAttribute("QuestionController", e.getParameter());
			}
		}

		/**** Add to chart if internal and recommendation status is already
		 * assistant processed ****/
		if(internalStatus.getType().equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				&& deviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			Question question = Question.findById(Question.class, domain.getId());

			if(question.getNumber()!= null){

				try{
					Chart.addToChart(question);
				}catch (ELSException e) {
					model.addAttribute(this.getClass().getName(), e.getParameter());
				}
			}
		}
	}	

	private void populateSupportingMembers(final Question domain,final HttpServletRequest request){
		/**** Role of user creating question ****/
		String role=request.getParameter("role");

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
			Question question=Question.findById(Question.class,domain.getId());
			members=question.getSupportingMembers();
		}		
		/**** New Status ****/
		Status notsendStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_NOTSEND, domain.getLocale());
		Status approvedStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, domain.getLocale());
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
						if(role!=null){
							CustomParameter supportingMemberAutoApprovalAllowedTo=CustomParameter.findByName(CustomParameter.class,"QIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO","");
							if(supportingMemberAutoApprovalAllowedTo!=null){
								if(!role.isEmpty()&&supportingMemberAutoApprovalAllowedTo.getValue().contains(role)){
									supportingMember.setDecisionStatus(approvedStatus);
									supportingMember.setApprovedSubject(domain.getSubject());
									supportingMember.setApprovedText(domain.getQuestionText());
									supportingMember.setApprovalType("AUTO_APPROVAL");
								}else{
									supportingMember.setDecisionStatus(notsendStatus);
								}
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

	private void validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(final Question domain, final BindingResult result, final HttpServletRequest request) {
		if(domain.getType()!=null) {
			if(domain.getType().getType()!=null) {
				if(domain.getType().getType().equalsIgnoreCase(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					Session session = domain.getSession();								
					if(session != null) {
						String noOFSupportingMembersToCheck = session.getParameter(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_NO_OF_SUPPORTING_MEMBERS);
						String noOFSupportingMembersComparator = session.getParameter(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_NO_OF_SUPPORTING_MEMBERS_COMPARATOR);
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
															if (i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
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
	}

	private void validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(final Question domain, final BindingResult result, final HttpServletRequest request) {
		if(domain.getType()!=null) {
			if(domain.getType().getType()!=null) {
				if(domain.getType().getType().equalsIgnoreCase(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
					Session session = domain.getSession();								
					if(session != null) {
						String noOFSupportingMembersToCheck = session.getParameter(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS);
						String noOFSupportingMembersComparator = session.getParameter(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS_COMPARATOR);
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
															if (i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
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
		List<RevisionHistoryVO> drafts=Question.getRevisions(questionId,locale.toString());
		Question q = Question.findById(Question.class, questionId);
		if(q != null){
			if(q.getType() != null){
				if(q.getType().getType() != null){
					model.addAttribute("selectedDeviceType", q.getType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);
		model.addAttribute("questions_halfhourdiscussion_from_question", ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION);
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
					//----------changed 21012013
					String strDates = session.getParameter("questions_halfhourdiscussion_from_question_discussionDates");

					if(strDates!=null && !strDates.isEmpty()){
						String[] dates = strDates.split("#");

						List<String> discussionDates = new ArrayList<String>();

						try {
							SimpleDateFormat sdf = FormaterUtil.getDBDateParser(session.getLocale());
							for (int i = 0; i < dates.length; i++) {
								discussionDates.add(FormaterUtil.getDateFormatter("dd/MM/yyyy", session.getLocale()).format(sdf.parse(dates[i])));
							}
							model.addAttribute("discussionDates",discussionDates);
							if (domain.getDiscussionDate() != null) {
								model.addAttribute("discussionDateSelected",FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
							}
						} catch (ParseException e) {

							e.printStackTrace();
						}
					}
				}

				String strRefQuestionNumber = request.getParameter("halfHourDiscussionReference_questionNumber");				
				if(strRefQuestionNumber != null){
					if(!strRefQuestionNumber.isEmpty()){
						try {
							Integer qNumber = new Integer(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).parse(strRefQuestionNumber).intValue());							
							model.addAttribute("referredQuestionNumber", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(qNumber));

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
						Date startDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("questions_halfhourdiscussion_from_question_submissionStartDate"),dateFormatDB.getValue());
						Date endDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("questions_halfhourdiscussion_from_question_submissionEndDate"), dateFormatDB.getValue());

						model.addAttribute("startDate",FormaterUtil.formatDateToString(startDate, "yyyy/MM/dd hh:mm:ss"));
						model.addAttribute("endDate",FormaterUtil.formatDateToString(endDate, "yyyy/MM/dd hh:mm:ss"));
					}
				}
			}

			if(questionType.getType().equals("questions_halfhourdiscussion_standalone")){
				populateForHalfHourDiscussionStandaloneNew(model, domain, selectedSession, questionType, request);
			}
		}         
	}

	private void populateForHalfHourDiscussionStandaloneNew(final ModelMap model, final Question domain, final Session selectedSession, final DeviceType questionType, final HttpServletRequest request){

		Session session = Session.findById(Session.class, selectedSession.getId());

		if (session != null) {
			//----------changed 21012013
			String strDates = session.getParameter("questions_halfhourdiscussion_standalone_discussionDates");

			if(strDates!=null && !strDates.isEmpty()){
				String[] dates = strDates.split("#");

				List<String> discussionDates = new ArrayList<String>();

				try {
					SimpleDateFormat sdf = FormaterUtil.getDBDateParser(session.getLocale());
					for (int i = 0; i < dates.length; i++) {
						discussionDates.add(FormaterUtil.getDateFormatter("dd/MM/yyyy", session.getLocale()).format(sdf.parse(dates[i])));
					}
					model.addAttribute("discussionDates",discussionDates);
					if (domain.getDiscussionDate() != null) {
						model.addAttribute("discussionDateSelected",FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
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
				Date startDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("questions_halfhourdiscussion_standalone_submissionStartDate"),dateFormatDB.getValue());
				Date endDate = FormaterUtil.formatStringToDate(selectedSession.getParameter("questions_halfhourdiscussion_standalone_submissionEndDate"), dateFormatDB.getValue());

				model.addAttribute("startDate",FormaterUtil.formatDateToString(startDate, "yyyy/MM/dd hh:mm:ss"));
				model.addAttribute("endDate",FormaterUtil.formatDateToString(endDate, "yyyy/MM/dd hh:mm:ss"));
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
			if(questionType.getType().equals("questions_halfhourdiscussion_from_question")){
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
				}

				List<String> discussionDates = new ArrayList<String>();
				SimpleDateFormat sdf = null;

				if (selectedSession != null) {

					//------changed 21012013-----------------
					String strDates = selectedSession.getParameter("questions_halfhourdiscussion_from_question_discussionDates");
					//-----------21012013
					if(strDates != null && !strDates.isEmpty()){

						String[] dates = strDates.split("#");

						try {
							sdf = FormaterUtil.getDBDateParser(selectedSession.getLocale());
							for (int i = 0; i < dates.length; i++) {
								discussionDates.add(FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(sdf.parse(dates[i])));
							}
							model.addAttribute("discussionDates", discussionDates);
						} catch (ParseException e) {

							e.printStackTrace();
						}
					}
				}

				if (domain.getDiscussionDate() != null) {
					model.addAttribute("discussionDateSelected",FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
				}else{
					model.addAttribute("discussionDateSelected",null);
				}
				if (domain.getHalfHourDiscusionFromQuestionReference() != null) {
					if (domain.getHalfHourDiscusionFromQuestionReference()!= null) {
						model.addAttribute("referredQuestionNumber", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getHalfHourDiscusionFromQuestionReference().getNumber()));
						model.addAttribute("refQuestionId", domain.getHalfHourDiscusionFromQuestionReference().getId());
					}
				}
			}

			if(questionType.getType().equals("questions_halfhourdiscussion_standalone")){
				populateForHalfHourDiscussionStandaloneEdit(model, domain, request);
			}
		}
	}	

	private void populateForHalfHourDiscussionStandaloneEdit(final ModelMap model, final Question domain, final HttpServletRequest request){

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

		List<String> discussionDates = new ArrayList<String>();
		SimpleDateFormat sdf = null;

		if (selectedSession != null) {

			//------changed 21012013-----------------
			String strDates = selectedSession.getParameter("questions_halfhourdiscussion_standalone_discussionDates");
			//-----------21012013
			if(strDates != null && !strDates.isEmpty()){

				String[] dates = strDates.split("#");

				try {
					sdf = FormaterUtil.getDBDateParser(selectedSession.getLocale());
					for (int i = 0; i < dates.length; i++) {
						discussionDates.add(FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(sdf.parse(dates[i])));
					}
					model.addAttribute("discussionDates", discussionDates);
				} catch (ParseException e) {

					e.printStackTrace();
				}
			}
		}

		if (domain.getDiscussionDate() != null) {
			model.addAttribute("discussionDateSelected",FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
		}else{
			model.addAttribute("discussionDateSelected",null);
		}
	}

	//---------------------------Added by vikas & dhananjay----------------------------------------------
	@RequestMapping(value="/viewquestion",method=RequestMethod.GET)
	public String viewQuestion(final HttpServletRequest request,final ModelMap model,final Locale locale){

		String strQuestionId = request.getParameter("qid");

		if(strQuestionId != null && !strQuestionId.isEmpty()){

			Long id = new Long(strQuestionId);
			Question q = Question.findById(Question.class, id);

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

				if(q.getAnsweringDate() != null){
					if(q.getAnsweringDate().getAnsweringDate() != null){
						model.addAttribute("answerDate",FormaterUtil.getDateFormatter("dd/MM/yyyy", q.getLocale().toString()).format(q.getAnsweringDate().getAnsweringDate()));
					}else{
						model.addAttribute("answerDate","");
					}
				}

				model.addAttribute("subject", q.getSubject());
				model.addAttribute("qText", q.getQuestionText());
				model.addAttribute("qReason", q.getReason());
				model.addAttribute("qAnswer", q.getAnswer());


				Member member=  q.getPrimaryMember();
				if(member.getId()!=null){          
					model.addAttribute("primaryMemberName",member.getFullname());
				}
			}
		}
		return "question/viewquestion";
	}

	@RequestMapping(value="/getsubject",method=RequestMethod.GET)
	public @ResponseBody MasterVO getSubjectAndQuestion(final HttpServletRequest request,final ModelMap model,final Locale locale){

		String strQuestionId = request.getParameter("qid");
		String text = request.getParameter("text");
		MasterVO masterVO = new MasterVO();

		if(strQuestionId != null){
			if(!strQuestionId.isEmpty()){

				Long id = new Long(strQuestionId);
				Question q = Question.findById(Question.class, id);

				if(text != null){
					if(!text.isEmpty()){
						if(text.equals("1")){

							masterVO.setId(q.getId());
							masterVO.setName(q.getSubject());
							masterVO.setValue(q.getQuestionText());

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


				List<Question> questions = new ArrayList<Question>();
				if(primaryMember != null){
					questions = Question.findAllByMember(session, primaryMember,deviceType, itemsCount, strLocale);	
				}	
				model.addAttribute("questions", questions);
				model.addAttribute("size", questions.size());

				String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupType", userGroupType);
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "question/bulksubmission";
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
							sm.setApprovalType("ONLINE");

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
						ApplicationConstants.QUESTION_SUBMIT, question.getLocale());
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

		return "question/bulksubmissionack";
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
		String strDeviceType = request.getParameter("questionType");			
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
					"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" +
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
			model.addAttribute("questionType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("itemscount", strItemsCount);
			model.addAttribute("file", strFile);
			model.addAttribute("group", strGroup);

			retVal = "question/bulksubmissionassistantint";
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
		return "question/bulksubmissionassistantview";
	}

	@Transactional
	@RequestMapping(value="/bulksubmission/assistant/update", method=RequestMethod.POST)
	public String bulkSubmissionAssistant(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {

		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus = request.getParameter("status");
		StringBuffer assistantProcessed = new StringBuffer();
		StringBuffer recommendAdmission = new StringBuffer();
		StringBuffer recommendRejection = new StringBuffer();
		StringBuffer recommendRepeatRejection = new StringBuffer();
		StringBuffer recommendRepeatAdmission = new StringBuffer();
		StringBuffer recommendClarificationFromMember = new StringBuffer();
		StringBuffer recommendClarificationFromDept = new StringBuffer();
		StringBuffer recommendClarificationFromGovt = new StringBuffer();
		StringBuffer recommendClarificationFromMemberDept = new StringBuffer();



		if(selectedItems != null && selectedItems.length > 0
				&& strStatus != null && !strStatus.isEmpty()) {
			/**** As It Is Condition ****/
			if(strStatus.equals("-")) {
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					Question question = Question.findById(Question.class, id);

					if(!question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)){
						/**** Create Process ****/
						ProcessDefinition processDefinition = null;
						Map<String,String> properties = new HashMap<String, String>();
						String actor = question.getActor();
						String[] temp = actor.split("#");
						if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
								&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

							String usergroupType = request.getParameter("usergroupType");
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
											if(usergroupType.equals(ApplicationConstants.SECTION_OFFICER) 
													&& (question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
															||question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))){
												Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
												reminderto = recepient.getEmail();								
											} else {
												reminderto=request.getParameter("reminderto");								
											}						
											properties.put("pv_reminderto", reminderto);

											String remindersubject=request.getParameter("remindersubject");						
											properties.put("pv_remindersubject", remindersubject);

											String remindercontent = "";
											if(usergroupType.equals(ApplicationConstants.SECTION_OFFICER) 
													&& (question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
															|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
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

							WorkflowDetails workflowDetails = null;
							try {
								if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
										&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

									workflowDetails = WorkflowDetails.create(question,task, ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,question.getLevel());
								}else{
									workflowDetails = WorkflowDetails.create(question,task, ApplicationConstants.APPROVAL_WORKFLOW, question.getLevel());
								}

								question.setWorkflowDetailsId(workflowDetails.getId());
								/**** Workflow Started ****/
								question.setWorkflowStarted("YES");
								question.setWorkflowStartedOn(new Date());
								question.setTaskReceivedOn(new Date());
								question.setFileSent(true);
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
					Question question = Question.findById(Question.class, id);

					String actor = request.getParameter("actor");
					String level = request.getParameter("level");
					if(actor != null && !actor.isEmpty() && level != null && !level.isEmpty()) {
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
								question.setInternalStatus(status);
								question.setRecommendationStatus(status);	
								question.setEndFlag("continue");

								/**** Create Process ****/
								ProcessDefinition processDefinition = null;
								Map<String, String> properties = new HashMap<String, String>();
								if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
										&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

									String usergroupType = request.getParameter("usergroupType");
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
													if(usergroupType.equals(ApplicationConstants.SECTION_OFFICER) 
															&& (question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
																	||question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))){
														Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
														reminderto = recepient.getEmail();								
													} else {
														reminderto=request.getParameter("reminderto");								
													}						
													properties.put("pv_reminderto", reminderto);

													String remindersubject=request.getParameter("remindersubject");						
													properties.put("pv_remindersubject", remindersubject);

													String remindercontent = "";
													if(usergroupType.equals(ApplicationConstants.SECTION_OFFICER) 
															&& (question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
																	|| question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
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
										&& question.getEndFlag().equals("continue")) {

									WorkflowDetails workflowDetails = null;

									if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
											&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

										workflowDetails = WorkflowDetails.create(question,task, ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,question.getLevel());
									}else{
										workflowDetails = WorkflowDetails.create(question,task, ApplicationConstants.APPROVAL_WORKFLOW,question.getLevel());
									}
									question.setWorkflowDetailsId(workflowDetails.getId());
									/**** Workflow Started ****/
									question.setWorkflowStarted("YES");
									question.setWorkflowStartedOn(new Date());
									question.setTaskReceivedOn(new Date());
									question.setFileSent(true);
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
		return "question/bulksubmissionassistantview";
	}

	/**** Used in bulk approval of supporting members to fetch question details ****/
	@RequestMapping(value="/{id}/details", method=RequestMethod.GET)
	public String getDetails(@PathVariable("id")final Long id,
			final Model model){
		Question question = Question.findById(Question.class, id);
		model.addAttribute("details", question.getQuestionText());
		return "question/details";
	}	

	private void getBulkSubmissionQuestions(final ModelMap model,
			final HttpServletRequest request, 
			final String locale) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("questionType");			
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
			List<Question> questions = new ArrayList<Question>();

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
				if(strGroup!=null && strGroup !=""){
					group=Group.findById(Group.class, Long.parseLong(strGroup));
				}

				if(! strFile.equals("-")){
					Integer file = Integer.parseInt(strFile);
					questions = Question.findAllByFile(session, deviceType,group,file, locale);
				}
				else {
					Integer itemsCount = Integer.parseInt(strItemsCount);
					Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));
					questions = Question.findAllByStatus(session, deviceType, internalStatus,group ,
							itemsCount, locale);
				}

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

	@RequestMapping(value="/viewYaadi" ,method=RequestMethod.GET)
	public @ResponseBody void generateYaadiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 

		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");	    
		String strDeviceType=request.getParameter("questionType");
		String reportFormat=request.getParameter("outputFormat");
		if(strDeviceType == null){
			strDeviceType = request.getParameter("deviceType");
		}
		String strAnsweringDate = request.getParameter("answeringDate");

		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null && strAnsweringDate!=null && reportFormat!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty() && !strAnsweringDate.isEmpty() && !reportFormat.isEmpty()) {
				HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
				SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
				Integer sessionYear=Integer.parseInt(strSessionYear);
				Session session = null;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e1) {					
					e1.printStackTrace();
				}
				DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				Date answeringDate = null;
				if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					QuestionDates questionDates = 
							QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
					answeringDate = questionDates.getAnsweringDate();
				}
				else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
						deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
					CustomParameter dbDateFormat = 
							CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
					CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				}
				List<DeviceVO> ballotedDeviceVOs = null;
				try {
					ballotedDeviceVOs = Ballot.findBallotedQuestionVOs(session, ApplicationConstants.STARRED_QUESTION, answeringDate, "mr_IN");
				} catch (ELSException e1) {
					e1.printStackTrace();
				}
				if(ballotedDeviceVOs == null) {
					try {
						//response.sendError(404, "Report cannot be generated at this stage.");
						MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
						if(message != null) {
							if(!message.getValue().isEmpty()) {
								response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
							}
						} else {
							response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
						}

						return;
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}
				if(ballotedDeviceVOs.isEmpty()) {
					try {
						//response.sendError(404, "Report cannot be generated at this stage.");
						MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
						if(message != null) {
							if(!message.getValue().isEmpty()) {	            				
								response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
							}
						} else {
							response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
						}

						return;
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}
				QuestionYaadiSuchiXmlVO data = new QuestionYaadiSuchiXmlVO();
				data.setHouseType(houseType.getName());
				data.setSessionNumber(session.getNumber().toString());
				data.setSessionType(sessionType.getSessionType());
				data.setSessionYear(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
				data.setSessionPlace(session.getPlace().getPlace());
				Role role = Role.findByFieldName(Role.class, "type", "QIS_PRINCIPAL_SECRETARY", locale.toString());
				List<User> users = User.findByRole(false, role.getName(), locale.toString());
				//as principal secretary for starred question is only one, so user is obviously first element of the list.
				data.setUserName(users.get(0).findFirstLastName());
				Group group = null;
				try {
					group = Group.find(session, answeringDate, locale.toString());
				} catch (ELSException e) {					
					e.printStackTrace();
				}
				List<MinistryVO> ministryVOs = new ArrayList<MinistryVO>();
				int count = 0;

				try {
					for(Ministry mi: Group.findMinistriesByPriority(group)) { //group.getMinistries()) {
						count++;
						String ministryNumber = FormaterUtil.formatNumberNoGrouping(count, locale.toString());
						MinistryVO ministryVO = new MinistryVO(mi.getId(), ministryNumber, mi.getName());
						ministryVOs.add(ministryVO);	            	
					}
				} catch (ELSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				data.setMinistryVOs(ministryVOs);
				SimpleDateFormat dbFormat = null;
				CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
				if(dbDateFormat!=null){
					dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
				}
				//Added the following code to solve the marathi month and day issue
				String[] strAnsweringDates=dbFormat.format(answeringDate).split(",");
				String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
				data.setAnsweringDay(answeringDay);
				String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
				String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
				String formattedAnsweringDate = strAnsweringMonth[0] + " " + answeringMonth + " " + strAnsweringDates[2];
				data.setAnsweringDate(formattedAnsweringDate);

				String answeringDateInIndianCalendar = FormaterUtil.getIndianDate(answeringDate, locale);
				data.setAnsweringDateInIndianCalendar(answeringDateInIndianCalendar);

				data.setDeviceVOs(ballotedDeviceVOs);
				data.setTotalNumberOfDevices(FormaterUtil.formatNumberNoGrouping(ballotedDeviceVOs.size(), locale.toString()));
				//generate report
				try {
					reportFile = generateReportUsingFOP(data, "template_questionYaadi_report", reportFormat, "starred_question_yaadi", locale.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Question Yaadi Report generated successfully in " + reportFormat + " format!");

				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			} else{
				logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		} else{
			logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}
	}

	@RequestMapping(value="/viewSuchi" ,method=RequestMethod.GET)
	public @ResponseBody void generateSuchiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		try{
			File reportFile = null; 

			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");	    
			String strDeviceType=request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");
			}
			String strAnsweringDate = request.getParameter("answeringDate");
			String reportFormat=request.getParameter("outputFormat");

			if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null && strAnsweringDate!=null && reportFormat!=null){
				if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty() && !strAnsweringDate.isEmpty() && !reportFormat.isEmpty()) {
					HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
					SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
					Integer sessionYear=Integer.parseInt(strSessionYear);
					Session session = null;
					try {
						session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					} catch (ELSException e3) {
						e3.printStackTrace();
					}
					DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					Date answeringDate = null;
					if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						QuestionDates questionDates = 
								QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
						answeringDate = questionDates.getAnsweringDate();
					}
					else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
							deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
						CustomParameter dbDateFormat = 
								CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
						answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
						answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					}				
					List<RoundVO> roundVOs = null;
					try {
						roundVOs = Ballot.findBallotedRoundVOsForSuchi(session, deviceType.getType(), answeringDate, locale.toString());
					} catch (ELSException e2) {
						e2.printStackTrace();
					}				
					if(roundVOs == null) {
						try {
							//response.sendError(404, "Report cannot be generated at this stage.");
							MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
							if(message != null) {
								if(!message.getValue().isEmpty()) {
									response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
								} else {
									response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
								}
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
							}

							return;
						} catch (IOException e) {						
							e.printStackTrace();
						}
					}
					if(roundVOs.isEmpty()) {
						try {
							//response.sendError(404, "Report cannot be generated at this stage.");
							MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
							if(message != null) {
								if(!message.getValue().isEmpty()) {	            				
									response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
								} else {
									response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
								}
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
							}

							return;
						} catch (IOException e) {						
							e.printStackTrace();
						}
					}
					QuestionYaadiSuchiXmlVO data = new QuestionYaadiSuchiXmlVO();
					data.setHouseType(houseType.getName());
					data.setSessionNumber(session.getNumber().toString());
					data.setSessionType(sessionType.getSessionType());
					data.setSessionYear(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
					data.setSessionPlace(session.getPlace().getPlace());
					Role role = Role.findByFieldName(Role.class, "type", "QIS_PRINCIPAL_SECRETARY", locale.toString());
					List<User> users = User.findByRole(false, role.getName(), locale.toString());
					//as principal secretary for starred question is only one, so user is obviously first element of the list.
					data.setUserName(users.get(0).findFirstLastName());
					Group group = null;
					try {
						group = Group.find(session, answeringDate, locale.toString());
					} catch (ELSException e1) {
						e1.printStackTrace();
					}
					List<MinistryVO> ministryVOs = new ArrayList<MinistryVO>();
					int count = 0;
					try {
						for(Ministry mi: Group.findMinistriesByPriority(group)) { //group.getMinistries()) {
							count++;
							String ministryNumber = FormaterUtil.formatNumberNoGrouping(count, locale.toString());
							MinistryVO ministryVO = new MinistryVO(mi.getId(), ministryNumber, mi.getName());
							ministryVOs.add(ministryVO);	            	
						}
					} catch (ELSException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					data.setMinistryVOs(ministryVOs);
					SimpleDateFormat dbFormat = null;
					CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
					if(dbDateFormat!=null){
						dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
					}
					//Added the following code to solve the marathi month and day issue
					String[] strAnsweringDates=dbFormat.format(answeringDate).split(",");
					String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
					data.setAnsweringDay(answeringDay);
					String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
					String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
					String formattedAnsweringDate = strAnsweringMonth[0] + " " + answeringMonth + " " + strAnsweringDates[2];
					data.setAnsweringDate(formattedAnsweringDate);
					String answeringDateInIndianCalendar = FormaterUtil.getIndianDate(answeringDate, locale);
					data.setAnsweringDateInIndianCalendar(answeringDateInIndianCalendar);
					int totalNumberOfDevices = 0;
					for(RoundVO r: roundVOs) {
						totalNumberOfDevices += r.getDeviceVOs().size();
					}
					data.setTotalNumberOfDevices(FormaterUtil.formatNumberNoGrouping(totalNumberOfDevices, locale.toString()));
					data.setRoundVOs(roundVOs);

					//generate report
					try {
						reportFile = generateReportUsingFOP(data, "template_questionSuchi_report", reportFormat, "starred_question_suchi", locale.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Question Suchi Report generated successfully in " + reportFormat + " format!");

					openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
				} else{
					logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values ****");
					try {
						response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values</h3>");
					} catch (IOException e) {
						e.printStackTrace();
					}				
				}
			} else{
				logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}catch (Exception e) {
			logger.debug("viewsuchi", e);
			e.printStackTrace();
			try {
				response.getWriter().println("<h3>Can not create Suchi</h3>");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}

