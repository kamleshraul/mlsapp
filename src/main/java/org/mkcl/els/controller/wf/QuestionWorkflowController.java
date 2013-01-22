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
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
		String form=(String) request.getAttribute("form");
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
		return form;
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
		return "workflow/info";
	}



	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@SuppressWarnings("unused")
	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		/**** Date ****/
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setLenient(true);
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
				dateFormat, true));
		/**** Member ****/
		binder.registerCustomEditor(Member.class, new BaseEditor(
				new Member()));		
		/**** Status ****/
		binder.registerCustomEditor(Status.class, new BaseEditor(
				new Status()));
		/**** House Type ****/
		binder.registerCustomEditor(HouseType.class, new BaseEditor(
				new HouseType()));
		/**** Session ****/
		binder.registerCustomEditor(Session.class, new BaseEditor(
				new Session()));
		/**** Device Type ****/
		binder.registerCustomEditor(DeviceType.class, new BaseEditor(
				new DeviceType()));
		/**** Question Dates ****/
		binder.registerCustomEditor(QuestionDates.class, new BaseEditor(
				new QuestionDates()));
		/**** Clarification Needed from ****/
		binder.registerCustomEditor(ClarificationNeededFrom.class, new BaseEditor(
				new ClarificationNeededFrom()));
		/**** Group ****/
		binder.registerCustomEditor(Group.class, new BaseEditor(
				new Group()));
		/**** Ministry ****/
		binder.registerCustomEditor(Ministry.class, new BaseEditor(
				new Ministry()));
		/**** Department ****/
		binder.registerCustomEditor(Department.class, new BaseEditor(
				new Department()));
		/**** Sub Department ****/
		binder.registerCustomEditor(SubDepartment.class, new BaseEditor(
				new SubDepartment()));
		/**** Supporting Member ****/
		binder.registerCustomEditor(List.class, "SupportingMember",
				new CustomCollectionEditor(List.class) {
			@Override
			protected Object convertElement(
					final Object element) {
				String id = null;

				if (element instanceof String) {
					id = (String) element;
				}
				return id != null ? SupportingMember
						.findById(SupportingMember.class,
								Long.valueOf(id))
								: null;
			}
		});

		/**** Clubbed Entity ****/
		binder.registerCustomEditor(List.class, "ClubbedEntity",
				new CustomCollectionEditor(List.class) {
			@Override
			protected Object convertElement(
					final Object element) {
				String id = null;

				if (element instanceof String) {
					id = (String) element;
				}
				return id != null ? ClubbedEntity
						.findById(ClubbedEntity.class,
								Long.valueOf(id))
								: null;
			}
		});

		/**** Referenced Entity ****/
		binder.registerCustomEditor(List.class, "ReferencedEntity",
				new CustomCollectionEditor(List.class) {
			@Override
			protected Object convertElement(
					final Object element) {
				String id = null;

				if (element instanceof String) {
					id = (String) element;
				}
				return id != null ? ReferencedEntity
						.findById(ReferencedEntity.class,
								Long.valueOf(id))
								: null;
			}
		});

		//----------21012013--------------------------
		/**** Referenced Question for half hour discussion from question ****/
		binder.registerCustomEditor(Question.class, new BaseEditor(new Question()));
	}

	/**
	 * Inits the secretary.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="mytask",method=RequestMethod.GET)
	public String initSecretary(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflow ****/
		String strTaskId=(String) request.getAttribute("taskId");
		Task task=processService.findTaskById(strTaskId);
		model.addAttribute("taskid",strTaskId);
		Map<String,Object> variables=processService.getVariables(task);
		String questionId=variables.get("pv_deviceId").toString();
		Question domain=Question.findById(Question.class,Long.parseLong(questionId));
		String usergroup=variables.get("pv_usergroupid").toString();
		String form=variables.get("pv_form").toString();
		String level=variables.get("pv_level").toString();
		model.addAttribute("level",level);
		model.addAttribute("usergroup",usergroup);
		/**** populate model ****/		
		populateModel(domain,model,request,usergroup,level);		
		return form;
	}

	private void populateModel(final Question domain, final ModelMap model,
			final HttpServletRequest request,final String usergroup,final String level) {
		/**** Locale ****/
		String locale=domain.getLocale();
		/**** In case of short notice ****/
		if(domain.getDateOfAnsweringByMinister()!=null){
			String strFormat=FormaterUtil.getDateFormatter(locale).format(domain.getDateOfAnsweringByMinister());
			model.addAttribute("formattedDate",strFormat);
		}	
		
		/**** clear remarks ****/
		domain.setRemarks("");

		

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
		if(domain.getPriority()!=null){
		model.addAttribute("priority",domain.getPriority());
		model.addAttribute("formattedPriority",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getPriority()));
		}


		/**** Ministries ****/
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
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
		}

		/**** Internal Status****/
		UserGroup userGroupTemp=UserGroup.findById(UserGroup.class,Long.parseLong(usergroup));
		String userGroupType=userGroupTemp.getUserGroupType().getType();
		List<Status> internalStatuses=new ArrayList<Status>();
		if((userGroupType.equals("under_secretary")
				||userGroupType.equals("principal_secretary")
				||userGroupType.equals("deputy_secretary")
				||userGroupType.equals("speaker")
				||userGroupType.equals("section_officer")
				)&&domain.getRecommendationStatus().getType().equals("question_after_approval_put_for_dateapproval")){
			Status dateApprovalStatus=Status.findByFieldName(Status.class,"type","question_after_approval_put_for_dateapproval", locale);
			List<Reference> dateapprovalactors=WorkflowConfig.findQuestionActorsVO(domain, dateApprovalStatus, userGroupTemp, Integer.parseInt(level), locale);
			model.addAttribute("dateapprovalactors",dateapprovalactors);
			model.addAttribute("newRecommendationStatus", "question_after_approval_put_for_dateapproval");
			
		}else if(userGroupType.equals("speaker")||userGroupType.equals("chairman")){
			internalStatuses=Status.findStartingWith("question_workflow_approving_", "name", ApplicationConstants.ASC, domain.getLocale());
		}else if(userGroupType.equals("assistant")){
			List<Reference> actors=WorkflowConfig.findQuestionActorsVO(domain, domain.getInternalStatus(), userGroupTemp, Integer.parseInt(level), locale);
			model.addAttribute("actors",actors);
		}else if(userGroupType.equals("section_officer")){
			if(!domain.getRecommendationStatus().getType().equals("question_after_approval_department_sent_answer")){
			List<Reference> actors=WorkflowConfig.findQuestionActorsVO(domain, domain.getInternalStatus(), userGroupTemp, Integer.parseInt(level), locale);
			model.addAttribute("actors",actors);
			Status sendbackStatus=Status.findByFieldName(Status.class,"type","question_workflow_decisionstatus_sendback", locale);
			List<Reference> sendbackactors=WorkflowConfig.findQuestionActorsVO(domain, sendbackStatus, userGroupTemp, Integer.parseInt(level), locale);
			model.addAttribute("sendbackactors",sendbackactors);
			model.addAttribute("newRecommendationStatus", "question_after_approval_send_to_department");
			}else if(domain.getRecommendationStatus().getType().equals("question_after_approval_department_sent_answer")
					&&domain.getType().getType().equals("questions_shortnotice")){
				Status dateApprovalStatus=Status.findByFieldName(Status.class,"type","question_after_approval_put_for_dateapproval", locale);
				List<Reference> dateapprovalactors=WorkflowConfig.findQuestionActorsVO(domain, dateApprovalStatus, userGroupTemp, 1, locale);
				model.addAttribute("dateapprovalactors",dateapprovalactors);
				model.addAttribute("newRecommendationStatus", "question_after_approval_put_for_dateapproval");
			}else{
			model.addAttribute("newRecommendationStatus", "question_after_approval_department_sent_answer");
			}
			
		}else if(userGroupType.equals("department")){
			Status sendbackStatus=Status.findByFieldName(Status.class,"type","question_workflow_decisionstatus_sendback", locale);
			List<Reference> sendbackactors=WorkflowConfig.findQuestionActorsVO(domain, sendbackStatus, userGroupTemp, Integer.parseInt(level), locale);
			model.addAttribute("sendbackactors",sendbackactors);			
		}else{
			internalStatuses=Status.findStartingWith("question_workflow_decisionstatus_", "name", ApplicationConstants.ASC, domain.getLocale());
		}
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

		//---------------------------Added by vikas & dhananjay-------------------------------------
		if(questionType.getType().equals("questions_halfhourdiscussion_from_question") || questionType.getType().equals("questions_halfhourdiscussion_standalone")){
			populateForHalfHourDiscussionEdit(model, domain, request);
		}
		//---------------------------Added by vikas & dhananjay-------------------------------------
		/**** add domain to model ****/
		model.addAttribute("domain",domain);
	}
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
								//-------------21012013
								model.addAttribute("refQuestionId", domain.getHalfHourDiscusionFromQuestionReference().getId());
							}
						}
					}
				}
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
	@Transactional
	@RequestMapping(value="mytask",method=RequestMethod.PUT)
	public String updateSecretary(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final Question domain,final BindingResult result) {
		String strusergroup=request.getParameter("usergroup");
		/**** Updating domain ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strusergroup));
		domain.setEditedAs(userGroup.getUserGroupType().getName());
		String strDateOfAnsweringByMinister=request.getParameter("dateOfAnsweringByMinister");
		Date dateOfAnsweringByMinister=null;
		try {
			if(strDateOfAnsweringByMinister!=null){
				if(!strDateOfAnsweringByMinister.isEmpty()){
					dateOfAnsweringByMinister=FormaterUtil.getDateFormatter("en_US").parse(strDateOfAnsweringByMinister);
				}
			}
		} catch (ParseException e1) {
			try {
				dateOfAnsweringByMinister=FormaterUtil.getDateFormatter(locale.toString()).parse(strDateOfAnsweringByMinister);
			} catch (ParseException e) {
				logger.error(e.getMessage());
			}			
		}
		domain.setDateOfAnsweringByMinister(dateOfAnsweringByMinister);
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
		String flag=request.getParameter("flag");
		if(flag!=null){
			if(!flag.isEmpty()){
				Status status=Status.findByFieldName(Status.class,"type",flag, locale.toString());
				domain.setRecommendationStatus(status);				
			}
		}
		//--------21012013
		String refQuestionId = request.getParameter("halfHourDiscusionFromQuestionReference");
		Question refQuestion = null;
		if(refQuestionId!= null && !refQuestionId.isEmpty()){
			refQuestion = Question.findById(Question.class, Long.parseLong(refQuestionId));
		}
		
		domain.setHalfHourDiscusionFromQuestionReference(refQuestion);
		
		String strDiscussionDate = request.getParameter("discussionDate");
		Date discussionDate = null;
		if(strDiscussionDate!=null && !strDiscussionDate.isEmpty()){
			CustomParameter serverDateFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "SERVER_DATEFORMAT", null);
			CustomParameter dbDateFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", null);
			try {
				if(dbDateFormat!=null){
					SimpleDateFormat serverDateFormater = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), locale.toString());
					SimpleDateFormat dbDateFormater = FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
					
					String iStrdiscussionDate = dbDateFormater.format(serverDateFormater.parse(strDiscussionDate));
					discussionDate = FormaterUtil.getDateFormatter(dbDateFormat.getValue(),"en_US").parse(iStrdiscussionDate);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		domain.setDiscussionDate(discussionDate);
		//------------------------
		performAction(domain);
		domain.merge();
		/*
		 * complete task
		 */
		String strTaskId=request.getParameter("taskid");
		Task task=processService.findTaskById(strTaskId);
		String sendbackactor=request.getParameter("sendbackactor");
		String nextuser=null;
		if(sendbackactor!=null){
			if(!sendbackactor.isEmpty()){
				nextuser=sendbackactor;
			}else{
				nextuser=request.getParameter("actor");	
			}
		}else{
			nextuser=request.getParameter("actor");
		}		
		Map<String,String> properties=new HashMap<String, String>();
		/**** My Task grid ****/
		properties.put("pv_deviceType",domain.getType().getName());
		properties.put("pv_internalStatus",domain.getInternalStatus().getName());
		properties.put("pv_RecommendationStatus",domain.getRecommendationStatus().getName());
		/**** Next user and usergroup ****/
		if(nextuser!=null){
			if(!nextuser.isEmpty()){
				String[] temp=nextuser.split("#");
				properties.put("pv_user",temp[0]);
				properties.put("pv_usergroup",temp[1]);
				properties.put("pv_usergroupid",temp[2]);
				properties.put("pv_level",temp[4]);
				properties.put("pv_form","workflow/question/"+temp[1]);
			}
		}	
		properties.put("pv_endflag",request.getParameter("endflag"));
		processService.completeTask(task, properties);
		/*
		 * Once both update of domain and task is completed we can show a message indicating the same
		 */
		model.addAttribute("type","taskcompleted");
		
		return "workflow/info";
	}

	private void performAction(Question domain) {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		if(internalStatus.equals(ApplicationConstants.QUESTION_APPROVING_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_APPROVING_ADMISSION)){
			performActionOnAdmission(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_APPROVING_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_APPROVING_REJECTION)){
			performActionOnRejection(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_APPROVING_CONVERT_TO_UNSTARRED)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_APPROVING_CONVERT_TO_UNSTARRED)){
			performActionOnConvertToUnstarred(domain);
		}
	}

	private void performActionOnConvertToUnstarred(Question domain) {
		/**** All Clubbed questions internal status,recommendation status
		 * revised subject and revised text will be set same as parent 
		 * question's ****/
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		String subject=null;
		String questionText=null;
		if(domain.getRevisedSubject()!=null){
			if(!domain.getRevisedSubject().isEmpty()){
				subject=domain.getRevisedSubject();
			}else{
				subject=domain.getSubject();
			}
		}else{
			subject=domain.getSubject();
		}
		if(domain.getRevisedQuestionText()!=null){
			if(!domain.getRevisedQuestionText().isEmpty()){
				questionText=domain.getRevisedQuestionText();
			}else{
				questionText=domain.getQuestionText();
			}
		}else{
			questionText=domain.getQuestionText();
		}
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		for(ClubbedEntity i:clubbedEntities){
			Question question=i.getQuestion();
			question.setRevisedSubject(subject);
			question.setRevisedQuestionText(questionText);
			question.setInternalStatus(internalStatus);
			question.setRecommendationStatus(recommendationStatus);
			question.simpleMerge();
		}
	}

	private void performActionOnRejection(Question domain) {

	}

	private void performActionOnAdmission(Question domain) {

	}
}
