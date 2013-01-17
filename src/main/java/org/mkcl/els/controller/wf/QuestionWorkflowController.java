/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.wf.QuestionWorkflowController.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.controller.wf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.ClarificationNeededFrom;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


// TODO: Auto-generated Javadoc
/**
 * The Class QuestionWorkflowController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/workflow/question")
public class QuestionWorkflowController  extends BaseController{

	/** The process service. */
	@Autowired
	private IProcessService processService;

	/**
	 * Inits the supporting member.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="supportingmember",method=RequestMethod.GET)
	public String initSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/*
		 * obtain taskid from request and get the process variables associated
		 * with the task id
		 */
		String strTaskId=(String) request.getAttribute("taskId");
		Task task=processService.findTaskById(strTaskId);
		model.addAttribute("task",strTaskId);
		Map<String,Object> variables=processService.getVariables(task);
		/*
		 * get the question id process variable and obtain the question from it.
		 * Then obtain the supporting members of the questions.Then find the supporting member whose member
		 * is same as the authenticated member.
		 */
		String questionId=variables.get("pv_deviceId").toString();
		model.addAttribute("question",questionId);
		Question question=Question.findById(Question.class,Long.parseLong(questionId));
		/*
		 * here we are finding the supporting members of the question
		 */
		List<SupportingMember> supportingMembers=question.getSupportingMembers();
		/*
		 * Here we want to make the supporting member whose member is same as
		 * the authenticated user as the modelattribute of the jsp.for this we
		 * iterate over the supporting member list and select only that
		 * supporting member whose member is same as the authenticated user.
		 */
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		for(SupportingMember i:supportingMembers){
			if(i.getMember().getId()==member.getId()){
				/*
				 * once the supporting member is found we set its approvedSubject and approved
				 * text to that of the question and add it to domain
				 */
				i.setApprovedQuestionText(question.getQuestionText());
				i.setApprovedSubject(question.getSubject());
				model.addAttribute("member",i.getMember().getId());
				model.addAttribute("domain",i);
			}
		}

		populateSupportingMember(model, question,supportingMembers,locale.toString());
		return "workflow/question/supportingmember";
	}

	/**
	 * Populate supporting member.
	 *
	 * @param model the model
	 * @param question the question
	 * @param supportingMembers the supporting members
	 * @param locale the locale
	 */
	private void populateSupportingMember(final ModelMap model,final Question question, final List<SupportingMember> supportingMembers,final String locale){
		/*
		 * adding question type in the model which is same as that of question
		 */
		DeviceType questionType=question.getType();
		if(questionType!=null){
			model.addAttribute("questionType", questionType.getName());
		}
		/*
		 * adding session year and session type to model which is same as that obtained from
		 * question's session
		 */
		Session session=question.getSession();
		if(session!=null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/*
		 * adding houseType name which is same as that of question
		 */
		model.addAttribute("houseTypeName",question.getHouseType().getName());
		model.addAttribute("houseType",question.getHouseType().getType());
		/*
		 * add supporting members names
		 */
		List<Member> members=new ArrayList<Member>();
		if(supportingMembers!=null){
			for(SupportingMember i:supportingMembers){
				Member selectedMember=i.getMember();
				members.add(selectedMember);
			}
			if(!members.isEmpty()){
				StringBuffer buffer=new StringBuffer();
				for(Member i:members){
					buffer.append(i.getFullnameLastNameFirst()+",");
				}
				buffer.deleteCharAt(buffer.length()-1);
				model.addAttribute("supportingMembersName", buffer.toString());
			}
		}
		/*
		 * add list of available supporting members decision status
		 */
		List<Status> decisionStatus=Status.findStartingWith("supportingmember_decisionstatus", "name",ApplicationConstants.ASC, locale);
		model.addAttribute("decisionStatus",decisionStatus);
		/*
		 * add primary member name
		 */
		model.addAttribute("primaryMemberName",question.getPrimaryMember().getFullnameLastNameFirst());
		/*
		 * adding priority in model
		 */
		model.addAttribute("priority",question.getPriority());
	}

	/**
	 * Update supporting member.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @param domain the domain
	 * @return the string
	 */
	@RequestMapping(value="supportingmember",method=RequestMethod.PUT)
	public String updateSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final SupportingMember domain) {
		/*
		 * first we update the domain:suporting member
		 */
		String strMember=request.getParameter("member");
		Member member=Member.findById(Member.class, Long.parseLong(strMember));
		domain.setMember(member);
		domain.setApprovalDate(new Date());
		domain.merge();
		/*
		 * then the task gets completed
		 */
		String strTaskId=request.getParameter("task");
		Task task=processService.findTaskById(strTaskId);
		model.addAttribute("task",strTaskId);
		processService.completeTask(task);
		/*
		 * Once both update of domain and task is completed we can show a message indicating the same
		 */
		model.addAttribute("type","taskcompleted");
		return "workflow/question/info";
	}



	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@SuppressWarnings("unused")
	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		/*
		 * adding date
		 */
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setLenient(true);
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
				dateFormat, true));
		/*
		 * adding member
		 */
		binder.registerCustomEditor(Member.class, new BaseEditor(
				new Member()));
		/*
		 * adding status
		 */
		binder.registerCustomEditor(Status.class, new BaseEditor(
				new Status()));
		/*
		 * adding house type
		 */
		binder.registerCustomEditor(HouseType.class, new BaseEditor(
				new HouseType()));
		/*
		 * adding session
		 */
		binder.registerCustomEditor(Session.class, new BaseEditor(
				new Session()));
		/*
		 * adding device type
		 */
		binder.registerCustomEditor(DeviceType.class, new BaseEditor(
				new DeviceType()));
		/*
		 * adding question dates
		 */
		binder.registerCustomEditor(QuestionDates.class, new BaseEditor(
				new QuestionDates()));
		/*
		 * adding language
		 */
		binder.registerCustomEditor(Language.class, new BaseEditor(
				new Language()));
		/*
		 * adding clarification needed from
		 */
		binder.registerCustomEditor(ClarificationNeededFrom.class, new BaseEditor(
				new ClarificationNeededFrom()));
		/*
		 * adding user
		 */
		binder.registerCustomEditor(User.class, new BaseEditor(
				new User()));
		/*
		 * adding user group type
		 */
		binder.registerCustomEditor(UserGroupType.class, new BaseEditor(
				new UserGroupType()));
		/*
		 * adding group
		 */
		binder.registerCustomEditor(Group.class, new BaseEditor(
				new Group()));
		/*
		 * adding ministry
		 */
		binder.registerCustomEditor(Ministry.class, new BaseEditor(
				new Ministry()));
		/*
		 * adding department
		 */
		binder.registerCustomEditor(Department.class, new BaseEditor(
				new Department()));
		/*
		 * adding subdepartment
		 */
		binder.registerCustomEditor(SubDepartment.class, new BaseEditor(
				new SubDepartment()));
	}

	/**
	 * Inits the secretary.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="secretary",method=RequestMethod.GET)
	public String initSecretary(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/*
		 * Obtain question domain
		 */
		String strTaskId=(String) request.getAttribute("pv_taskId");
		Task task=processService.findTaskById(strTaskId);
		model.addAttribute("task",strTaskId);
		Map<String,Object> variables=processService.getVariables(task);
		String questionId=variables.get("pv_deviceid").toString();
		model.addAttribute("question",questionId);
		Integer level=Integer.parseInt(variables.get("pv_level").toString())+1;
		model.addAttribute("level", level);
		Question domain=Question.findById(Question.class,Long.parseLong(questionId));
		Long workflowConfigId=Long.parseLong(variables.get("pv_wc_id_"+domain.getInternalStatus().getType()).toString());
		model.addAttribute("pv_workflowconfig_Id", workflowConfigId);
		/**** populate model ****/		
		populateSecretaryModel(domain,model,request);
		/**** populate actors on the basis of last internal status ****/
		populateActorsAndUsers(domain,model,request,workflowConfigId,level);
		/*
		 * add domain to model
		 */
		model.addAttribute("domain",domain);
		/*
		 * in case of approving authority we need to change the form to approving.jsp
		 */
		List<UserGroup> userGroupsTemp=this.getCurrentUser().getUserGroups();
		if(userGroupsTemp!=null){
			if(!userGroupsTemp.isEmpty()){
				for(UserGroup i:userGroupsTemp){
					UserGroup j=UserGroup.findById(UserGroup.class,i.getId());
					String strType=j.getUserGroupType().getType();
					if(strType.equals("deputy_speaker")
							||strType.equals("speaker")
							||strType.equals("deputy_chairman")
							||strType.equals("chairman")){
						if(domain.getRevisedSubject().isEmpty()){
							domain.setRevisedSubject(domain.getSubject());
						}
						if(domain.getRevisedQuestionText().isEmpty()){
							domain.setRevisedQuestionText(domain.getQuestionText());
						}
						return "workflow/question/approving";
					}else if(strType.equals("section_officer")){
						return "workflow/question/sectionofficer";
					}else if(strType.equals("department")){
						return "workflow/question/department";
					}else if(strType.equals("member")){
						return "workflow/question/member";
					}else if(strType.equals("assistant")){
						return "workflow/question/assistant";
					}
				}
			}
		}
		return "workflow/question/secretary";
	}
	
	private void populateActorsAndUsers(Question domain, ModelMap model,
			HttpServletRequest request, Long workflowConfigId, Integer level) {
				
	}

	private void populateSecretaryModel(final Question domain, final ModelMap model,
			final HttpServletRequest request) {
		/**** clear remarks ****/
		domain.setRemarks("");
		
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
	
	

	/**
	 * Update secretary.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @param domain the domain
	 * @param result the result
	 * @return the string
	 */
	@RequestMapping(value="secretary",method=RequestMethod.PUT)
	public String updateSecretary(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final Question domain,final BindingResult result) {
		updateSupportingMembers(domain, request);
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		/*
		 * add edited on,edited as,edited by
		 */
		domain.setEditedOn(new Date());
		User user=User.findByUserName(this.getCurrentUser().getActualUsername(), domain.getLocale());
		domain.setEditedBy(user);
		List<UserGroup> userGroupsTemp=this.getCurrentUser().getUserGroups();
		if(userGroupsTemp!=null){
			if(!userGroupsTemp.isEmpty()){
				for(UserGroup i:userGroupsTemp){
					UserGroup j=UserGroup.findById(UserGroup.class,i.getId());
					String strType=j.getUserGroupType().getType();
					if(strType.equals("under_secretary")
							||strType.equals("deputy_secretary")
							||strType.equals("officer_special_duty")
							||strType.equals("joint_secretary")
							||strType.equals("secretary")
							||strType.equals("principal_secretary")
							||strType.equals("deputy_speaker")
							||strType.equals("speaker")
							||strType.equals("deputy_chairman")
							||strType.equals("chairman")
							||strType.equals("assistant")
							||strType.equals("section_officer")){
						domain.setEditedAs(i.getUserGroupType());
						break;
					}
				}
			}
		}
		//for traversing the workflow we need two variables pv_nextactor and pv_nextuser
		String actor=request.getParameter("actor");
		//if(actor!=null){
		//if(actor.isEmpty()){
		//domain.setStatus(domain.getInternalStatus());
		//}
		//}
		/*
		 * updating submission date and creation date
		 */
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
		domain.merge();
		/*
		 * complete task
		 */
		String strTaskId=request.getParameter("task");
		Task task=processService.findTaskById(strTaskId);
		model.addAttribute("task",strTaskId);
		Map<String,String> properties=new HashMap<String, String>();
		String endflag=request.getParameter("pv_endflag");
		properties.put("pv_endflag", endflag);
		if(!endflag.equals("end")){
			if(actor!=null){
				if(!actor.isEmpty()){
					//variables needed for finding next actors
					properties.put("pv_sessionId",request.getParameter("sessionId"));
					properties.put("pv_deviceTypeId",request.getParameter("deviceTypeId"));
					properties.put("pv_workflowType",request.getParameter("workflowType"));
					properties.put("pv_groupNumber",request.getParameter("groupNumber"));
					String workflowConfigId=request.getParameter("workflowConfigId");
					properties.put("pv_workflowConfigId",workflowConfigId );
					properties.put("pv_nextactor",actor);
					String nextuser=findNextUser(domain, actor, domain.getLocale());
					properties.put("pv_nextuser",nextuser);
					Integer level=WorkflowConfig.getLevel(Long.parseLong(workflowConfigId), actor);
					properties.put("pv_level",String.valueOf(level));
				}else{
					properties.put("pv_nextactor","");
				}
			}else{
				properties.put("pv_nextactor","");
			}
		}
		processService.completeTask(task, properties);
		/*
		 * Once both update of domain and task is completed we can show a message indicating the same
		 */
		model.addAttribute("type","taskcompleted");
		return "workflow/question/info";
	}


	/**
	 * Find next user.
	 *
	 * @param domain the domain
	 * @param actor the actor
	 * @param locale the locale
	 * @return the string
	 */
	private String findNextUser(final Question domain,final String actor,final String locale){
		UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type",actor, domain.getLocale());

		List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType", userGroupType,"activeFrom",ApplicationConstants.DESC, domain.getLocale());
		Credential credential=null;
		int noOfComparisons=0;
		int noOfSuccess=0;
		if(userGroups!=null){
			if(!userGroups.isEmpty()){
				for(UserGroup i:userGroups){
					noOfComparisons=0;
					noOfSuccess=0;
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
						//in case of actor=department we want to check if user belongs to mantralaya user
						//as role.
						//if(actor.equals("department")){
						//noOfComparisons++;
						//Set<Role> roles=i.getCredential().getRoles();
						//for(Role j:roles){
						//if(j.getType().equals("MANTRALAYA USER")){
						//noOfSuccess++;
						//}
						//}
						//}
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


	/**
	 * Populate actors.
	 *
	 * @param domain the domain
	 * @param model the model
	 * @param request the request
	 * @param sessionId the session id
	 * @param groupNumber the group number
	 */
	
	/**
	 * Populate secretary model.
	 *
	 * @param domain the domain
	 * @param model the model
	 * @param request the request
	 */



	/**
	 * Update supporting members.
	 *
	 * @param domain the domain
	 * @param request the request
	 */
	private void updateSupportingMembers(final Question domain,final HttpServletRequest request){
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



}
