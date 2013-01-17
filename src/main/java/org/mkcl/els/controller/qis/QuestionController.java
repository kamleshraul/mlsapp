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
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.common.vo.Reference;
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

			/**** Roles ****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			List<Status> status=new ArrayList<Status>();
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					status=Status.findStartingWith("questions_","priority",ApplicationConstants.ASC, locale);
					model.addAttribute("status",status);
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().contains("CLERK")){
					status=Status.findAllByFieldName(Status.class,"type","questions_submit","priority",ApplicationConstants.ASC, locale);
					model.addAttribute("status",status);
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().startsWith("QIS_")){
					status=Status.findAssistantQuestionStatus("priority",ApplicationConstants.ASC, locale);					
					model.addAttribute("status",status);
					model.addAttribute("role",i.getType());
					break;
				}
			}
			/*** ugparam is used to load data in grid and it is either by username or group ****/
			String strgroups=this.getCurrentUser().getGroupsAllowed();
			if(strgroups!=null){
				if(!strgroups.isEmpty()){
					List<Group> groups=new ArrayList<Group>();
					String[] gr=strgroups.split(",");
					model.addAttribute("ugparam",gr[0]);
					for(String k:gr){
						Group group=Group.findByNumberHouseTypeSessionTypeYear(Integer.parseInt(k),  authUserHouseType, lastSessionCreated.getType(), year);
						if(group!=null){
							groups.add(group);
						}
					}
					model.addAttribute("groups",groups);
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
				try {
					houseType=HouseType.findById(HouseType.class,Long.parseLong(selectedHouseType));
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
					String strRotationOrderPubDate = selectedSession.getParamater("questions_starred_rotationOrderPublishingDate");
					if(strRotationOrderPubDate!=null){
						try {
							rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
							model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
							Date currentDate=new Date();
							if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
								List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
								model.addAttribute("ministries",ministries);
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
		model.addAttribute("role",request.getParameter("role"));
	}

	@Override
	protected String modifyEditUrlPattern(final String newUrlPattern,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		String edit=request.getParameter("edit");
		if(edit!=null){
			if(!Boolean.parseBoolean(edit)){
			return newUrlPattern.replace("edit","editreadonly");
			}
		}
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
		model.addAttribute("priority",domain.getPriority());
		model.addAttribute("formattedPriority",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));


		/**** Ministries ****/
		if(questionType.getType().trim().equals("questions_starred")){
			Date rotationOrderPubDate=null;
			CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strRotationOrderPubDate = selectedSession.getParamater("questions_starred_rotationOrderPublishingDate");
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
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
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

		/**** in case of assistant and other approving QIS actors ****/
		if(role.startsWith("QIS_")&&(!role.contains("CLERK"))){
			/**** Internal Status****/
			List<Status> internalStatuses=Status.findStartingWith("question_workflow_decisionstatus", "name", ApplicationConstants.ASC, domain.getLocale());
			model.addAttribute("internalStatuses",internalStatuses);
			/**** Referenced Questions are collected in refentities****/
			List<Reference> refentities=new ArrayList<Reference>();
			List<ReferencedEntity> referencedEntities=domain.getReferencedEntities();
			for(ReferencedEntity re:referencedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(re.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(re.getQuestion().getNumber()));
				reference.setNumber(String.valueOf(re.getQuestion().getId()));
				refentities.add(reference);			
			}
			model.addAttribute("referencedQuestions",refentities);
			/**** Clubbed Questions are collected in references ****/
			List<Reference> references=new ArrayList<Reference>();
			List<ClubbedEntity> clubbedEntities=Question.findClubbedEntitiesByPosition(domain);
			StringBuffer buffer1=new StringBuffer();
			buffer1.append(memberNames+",");			
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
	}


	@Override
	protected void customValidateCreate(final Question domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);

		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		/*
		 * There are two additional operations that a member can perform.
		 * a.send request to supporting members for approval
		 * b.submit a question for answering
		 */
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				/*
				 * For approval cycle:we first check if operation=approval
				 */
				if(operation.equals("approval")){
					/*
					 * a.First check if these fields have been filled atleast and they are
					 * primary member,subject,question and supporting members                     *
					 */
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
							if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
								count++;
							}
						}
						if(count==0){
							result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
						}
					}
				}else
					/*
					 * For submit cycle:we first check if operation=submit
					 */
					if(operation.equals("submit")){
						/*
						 * a.First check if these fields have been filled atleast and they are
						 * session,primary member,subject,question,ministry,group                     *
						 */
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
		}else{
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
			Status status=Status.findByFieldName(Status.class, "type", "questions_complete", domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
			/*
			 * Here we will further check if this is a submission of question request.If it is then only supporting members
			 * should be updated,submission time should be updated and status will be set.
			 */
			String operation=request.getParameter("operation");
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						/*
						 * submission date is set
						 */
						if(domain.getSubmissionDate()==null){
							domain.setSubmissionDate(new Date());
						}
						/*
						 * only those supporting memebrs will be included who have approved the requests
						 */
						List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
						if(domain.getSupportingMembers()!=null){
							if(!domain.getSupportingMembers().isEmpty()){
								for(SupportingMember i:domain.getSupportingMembers()){
									if(i.getDecisionStatus().getType().trim().equals("supportingmember_approved")){
										supportingMembers.add(i);
									}
								}
								domain.setSupportingMembers(supportingMembers);
							}
						}
						/*
						 *set status
						 */
						Status newstatus=Status.findByFieldName(Status.class, "type", "questions_submit", domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}
				}
			}
		}
		else{
			Status status=Status.findByFieldName(Status.class, "type", "questions_incomplete", domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}

		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strRole=request.getParameter("role");
		Role role=Role.findByFieldName(Role.class,"type",strRole,domain.getLocale());
		if(role!=null){
			domain.setEditedAs(role.getLocalizedName());
		}
	}

	@Override
	protected void populateAfterCreate(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		/**** Supporting Member Workflow ****/
		String operation=request.getParameter("operation");
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("userrole",request.getParameter("userrole"));
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey("Supporting_Members_Approval_Process");
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_locale",domain.getLocale());
					//now for displaying in my task grid
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceType",domain.getType().getName());
					properties.put("pv_primaryMemberFullName",this.getCurrentUser().getTitle()+" "+this.getCurrentUser().getFirstName()+" "+this.getCurrentUser().getMiddleName()+" "+this.getCurrentUser().getLastName());
					properties.put("pv_subject",domain.getSubject());
					processService.createProcessInstance(processDefinition, properties);
					Question question=Question.findById(Question.class,domain.getId());
					List<SupportingMember> supportingMembers=question.getSupportingMembers();
					Status status=Status.findByFieldName(Status.class,"type","supportingmember_pending",domain.getLocale());
					for(SupportingMember i:supportingMembers){
						//we want to send only those members whose status is not supportingmember_pending
						if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
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
		/*
		 * populating supporting members in domain if any present
		 */
		populateSupportingMembers(domain,request);

		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		/*
		 * There are two additional operations that a member can perform.
		 * a.send request to supporting members for approval
		 * b.submit a question for answering
		 */
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				/*
				 * For approval cycle:we first check if operation=approval
				 */
				if(operation.equals("approval")){
					/*
					 * a.First check if these fields have been filled atleast and they are
					 * primary member,subject,question and supporting members                     *
					 */
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
							if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
								count++;
							}
						}
						if(count==0){
							result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
						}
					}
				}else
					/*
					 * For submit cycle:we first check if operation=submit
					 */
					if(operation.equals("submit")){
						/*
						 * a.First check if these fields have been filled atleast and they are
						 * session,primary member,subject,question,ministry,group                     *
						 */
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
		}else{
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
		/*
		 * setting status of question.if all the mandatory fields have been filled it will be complete
		 * else it will be incomplete
		 */
		if(domain.getHouseType()!=null||domain.getType()!=null||domain.getSession()!=null
				||  domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
				domain.getGroup()!=null && (!domain.getSubject().isEmpty())
				&&(!domain.getQuestionText().isEmpty())){
			Status status=Status.findByFieldName(Status.class, "type", "questions_complete", domain.getLocale());
			if(!domain.getStatus().getType().equals("questions_submit")){
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
			/*
			 * Here we will further check if this is a submission of question request.If it is then only supporting members
			 * should be updated,submission time should be updated and status will be set.
			 */
			String operation=request.getParameter("operation");
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						/*
						 * submission date is set
						 */
						if(domain.getSubmissionDate()==null){
							domain.setSubmissionDate(new Date());
						}
						/*
						 * only those supporting memebrs will be included who have approved the requests
						 */
						List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
						if(domain.getSupportingMembers()!=null){
							if(!domain.getSupportingMembers().isEmpty()){
								for(SupportingMember i:domain.getSupportingMembers()){
									if(i.getDecisionStatus().getType().trim().equals("supportingmember_decisionstatus_approve")){
										supportingMembers.add(i);
									}
								}
								domain.setSupportingMembers(supportingMembers);
							}
						}
						/*
						 *set status
						 */
						Status newstatus=Status.findByFieldName(Status.class, "type", "questions_submit", domain.getLocale());
						domain.setStatus(newstatus);
						domain.setInternalStatus(newstatus);
						domain.setRecommendationStatus(newstatus);
					}
				}
			}
		}
		else{
			Status status=Status.findByFieldName(Status.class, "type", "questions_incomplete", domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strRole=request.getParameter("role");
		Role role=Role.findByFieldName(Role.class,"type",strRole,domain.getLocale());
		if(role!=null){
			domain.setEditedAs(role.getLocalizedName());
		}
		if(strRole.contains("ASSISTANT")) {
			Long id = domain.getId();
			Question question = Question.findById(Question.class, id);
			String internalStatus = question.getInternalStatus().getType();
			if(internalStatus.equals("questions_submit")&&domain.getMinistry()!=null&&domain.getGroup()!=null&&domain.getDepartment()!=null) {
				Status ASSISTANT_PROCESSED = Status.findByType("question_assistantprocessed", domain.getLocale());
				domain.setInternalStatus(ASSISTANT_PROCESSED);
				domain.setRecommendationStatus(ASSISTANT_PROCESSED);
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
	}



	@Override
	protected void populateAfterUpdate(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		/*
		 * send for approval:here we will start an activity process with process definition as
		 */
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey("Supporting_Members_Approval_Process");
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_locale",domain.getLocale());
					//now for displaying in my task grid
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceType",domain.getType().getName());
					properties.put("pv_primaryMemberFullName",this.getCurrentUser().getTitle()+" "+this.getCurrentUser().getFirstName()+" "+this.getCurrentUser().getMiddleName()+" "+this.getCurrentUser().getLastName());
					properties.put("pv_subject",domain.getSubject());
					processService.createProcessInstance(processDefinition, properties);
					Question question=Question.findById(Question.class,domain.getId());
					List<SupportingMember> supportingMembers=question.getSupportingMembers();
					Status status=Status.findByFieldName(Status.class,"type","supportingmember_pending",domain.getLocale());
					for(SupportingMember i:supportingMembers){
						if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
							i.setDecisionStatus(status);
							i.merge();
						}
					}
				}else if(operation.equals("startworkflow")){
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey("STARRED_QUESTION_PROCESS");
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_locale",domain.getLocale());
					//now for displaying in my task grid
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceType",domain.getType().getName());
					properties.put("pv_primaryMemberFullName",domain.getPrimaryMember().getFullname());
					properties.put("pv_subject",domain.getSubject());
					//variables needed for finding next actors
					properties.put("pv_sessionId",request.getParameter("sessionId"));
					properties.put("pv_deviceTypeId",request.getParameter("deviceTypeId"));
					properties.put("pv_workflowType",request.getParameter("workflowType"));
					properties.put("pv_groupNumber",request.getParameter("groupNumber"));
					String workflowConfigId=request.getParameter("workflowConfigId");
					properties.put("pv_workflowConfigId",workflowConfigId );
					//another way to end workflow set pv_endflag='end'
					properties.put("pv_endflag", "continue");
					//for traversing the workflow we need two variables pv_nextactor and pv_nextuser
					String actor=request.getParameter("actor");
					Integer level=WorkflowConfig.getLevel(Long.parseLong(workflowConfigId), actor);
					properties.put("pv_level",String.valueOf(level));
					if(actor!=null){
						properties.put("pv_nextactor",actor);
						String nextuser=findNextUser(domain, actor, domain.getLocale());
						properties.put("pv_nextuser",nextuser);
					}
					processService.createProcessInstance(processDefinition, properties);
				}
			}
		}

		Status internalStatus = domain.getInternalStatus();
		String deviceType=domain.getType().getType();
		// If the internalStatus of the Question is ASSISTANT_PROCESSED then
		// add to Chart if applicable
		if(internalStatus.getType().equals("question_assistantprocessed")
				&& deviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Question question = Question.findById(Question.class, domain.getId());
			Chart.addToChart(question);
		}
		// If the internalStatus of the Question is GROUP_CHANGED then do the
		// following
		else if(internalStatus.getType().
				equals("question_workflow_decisionstatus_groupchanged")
				&&deviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Question question = Question.findById(Question.class, domain.getId());
			QuestionDraft draft = question.findPreviousDraft();
			Group affectedGroup = draft.getGroup();
			Chart.groupChange(question, affectedGroup);
		}
	}

	private String findNextUser(final Question domain,final String actor,final String locale){
		UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type",actor, domain.getLocale());
		List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType", userGroupType,"activeFrom",ApplicationConstants.DESC, domain.getLocale());
		Credential credential=null;
		int noOfComparisons=0;
		int noOfSuccess=0;
		if(userGroups!=null){
			if(!userGroups.isEmpty()){
				for(UserGroup i:userGroups){
					if(i.getActiveFrom().before(new Date())||i.getActiveFrom().equals(new Date())){
						String userType=i.getUserGroupType().getType();
						if(userType.equals("member")){
							return i.getCredential().getUsername();
						}
						Map<String,String> map=i.getParameters();
						if(map.get("DEPARTMENT_"+locale)!=null&&domain.getDepartment()!=null){
							noOfComparisons++;
							if(map.get("DEPARTMENT_"+locale).contains(domain.getDepartment().getName())){
								noOfSuccess++;
							}
						}
						if(map.get("DEVICETYPE_"+locale)!=null&&domain.getType()!=null){
							noOfComparisons++;
							if(map.get("DEVICETYPE_"+locale).contains(domain.getType().getName())){
								noOfSuccess++;
							}
						}
						if(map.get("GROUP_"+locale)!=null&&domain.getGroup()!=null){
							noOfComparisons++;
							if(map.get("GROUP_"+locale).contains(String.valueOf(domain.getGroup().getNumber()))){
								noOfSuccess++;
							}
						}
						if(map.get("HOUSETYPE_"+locale)!=null&&domain.getHouseType()!=null){
							noOfComparisons++;
							if(map.get("HOUSETYPE_"+locale).equals("Both House")&&userType.equals("principal_secretary")){
								noOfSuccess++;
							}else if(map.get("HOUSETYPE_"+locale).equals(domain.getHouseType().getName())){
								noOfSuccess++;
							}
						}
						if(map.get("SESSIONTYPE_"+locale)!=null&&domain.getSession()!=null){
							noOfComparisons++;
							if(map.get("SESSIONTYPE_"+locale).equals(domain.getSession().getType().getSessionType())){
								noOfSuccess++;
							}
						}
						if(map.get("YEAR_"+locale)!=null&&domain.getSession()!=null){
							noOfComparisons++;
							if(map.get("YEAR_"+locale).equals(String.valueOf(domain.getSession().getYear()))){
								noOfSuccess++;
							}
						}
						if(map.get("SUBDEPARTMENT_"+locale)!=null&&domain.getSubDepartment()!=null){
							noOfComparisons++;
							if(map.get("SUBDEPARTMENT_"+locale).contains(domain.getSubDepartment().getName())){
								noOfSuccess++;
							}
						}
						if(noOfComparisons!=0&&noOfSuccess!=0&&noOfComparisons==noOfSuccess){
							credential=i.getCredential();
							return credential.getUsername();
						}
					}
				}
			}
		}
		return "";
	}


	private void populateSupportingMembers(final Question domain,final HttpServletRequest request){
		/*
		 * here we are obtaining the supporting members id from the jsp
		 * This method will be called from create,send for approval and submit.status that need to be set is
		 */
		String[] selectedSupportingMembers=request.getParameterValues("selectedSupportingMembers");
		List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
		if(selectedSupportingMembers!=null){
			if(selectedSupportingMembers.length>0){
				List<SupportingMember> members=new ArrayList<SupportingMember>();
				if(domain.getId()!=null){
					Question question=Question.findById(Question.class,domain.getId());
					members=question.getSupportingMembers();
				}
				for(String i:selectedSupportingMembers){
					SupportingMember supportingMember=null;
					Member member=Member.findById(Member.class, Long.parseLong(i));
					/*
					 * first we are iterating over the already present supporting members of domain to find out
					 * if the supporting members already exists.if yes then we add this supporting member to the list without modifing it.
					 */
					for(SupportingMember j:members){
						if(j.getMember().getId()==member.getId()){
							supportingMember=j;
							break;
						}
					}

					/*
					 * if the supporting member is a new supporting member.In that case we will set its member,locale,and status.
					 * Status will be set to assigned when question is first created,will be pending when it is send for approval,
					 * will be approved,rejected when set through my task of supporting members
					 */
					if(supportingMember==null){
						supportingMember=new SupportingMember();
						supportingMember.setMember(member);
						supportingMember.setLocale(domain.getLocale());
						/*
						 * Initially when create is clicked status will be assigned.Also when send for approval is clicked and
						 * the supporting member doesn't exists then its status will be assigned.Now tasks will be created for all
						 * the supporting members whose status is assigned.And once task has been created the status will change to pending.
						 */
						supportingMember.setDecisionStatus((Status) Status.findByFieldName(Status.class, "type","supportingmember_assigned", domain.getLocale()));
					}
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
			if(status.getType().equals("questions_incomplete")||status.getType().equals("questions_complete")){
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

	@RequestMapping(value="/subject/{id}",method=RequestMethod.GET)
	public @ResponseBody MasterVO getSubject(final HttpServletRequest request,final ModelMap model,
			final @PathVariable("id")Long id){
		Question question=Question.findById(Question.class, id);
		MasterVO masterVO=new MasterVO();
		masterVO.setId(question.getId());
		if(question.getRevisedSubject()!=null){
			masterVO.setName(question.getRevisedSubject());
		}else{
			masterVO.setName(question.getSubject());
		}
		return masterVO;
	}



}

