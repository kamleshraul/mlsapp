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
import org.mkcl.els.common.vo.ProcessInstance;
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
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.WorkflowDetails;
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

	@RequestMapping(value="supportingmember",method=RequestMethod.GET)
	public String initSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Question ****/
		String questionId=workflowDetails.getDeviceId();
		model.addAttribute("question",questionId);
		Question question=Question.findById(Question.class,Long.parseLong(questionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers=question.getSupportingMembers();
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		for(SupportingMember i:supportingMembers){
			if(i.getMember().getId()==member.getId()){
				i.setApprovedQuestionText(question.getQuestionText());
				i.setApprovedSubject(question.getSubject());
				model.addAttribute("member",i.getMember().getId());
				model.addAttribute("domain",i);
				if(i.getDecisionStatus()!=null){
					model.addAttribute("decisionStatus",i.getDecisionStatus().getId());
					model.addAttribute("formattedDecisionStatus",i.getDecisionStatus().getName());
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, question,supportingMembers,locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowdetails",workflowDetails.getId());
		model.addAttribute("status",workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,final Question question, final List<SupportingMember> supportingMembers,final String locale){
		/**** Question Type ****/
		DeviceType questionType=question.getType();
		if(questionType!=null){
			model.addAttribute("questionType", questionType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session=question.getSession();
		if(session!=null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName",question.getHouseType().getName());
		model.addAttribute("houseType",question.getHouseType().getType());
		/**** Supporting Members ****/
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
		/**** Decision Status ****/
		Status approveStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SUPPORTING_MEMBER_APPROVED, locale);
		Status rejectStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SUPPORTING_MEMBER_REJECTED, locale);
		List<Status> decisionStatus=new ArrayList<Status>();
		decisionStatus.add(approveStatus);
		decisionStatus.add(rejectStatus);
		model.addAttribute("decisionStatus",decisionStatus);
		/**** Primary Member ****/
		model.addAttribute("primaryMemberName",question.getPrimaryMember().getFullnameLastNameFirst());
		/**** Priority ****/
		model.addAttribute("priority",question.getPriority());
	}

	@RequestMapping(value="supportingmember",method=RequestMethod.PUT)
	public String updateSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final SupportingMember domain) {
		/**** update supporting member */
		String strMember=request.getParameter("member");
		Member member=Member.findById(Member.class, Long.parseLong(strMember));
		domain.setMember(member);
		domain.setApprovalDate(new Date());
		domain.merge();
		/**** complete the task ****/		 
		String strTaskId=request.getParameter("task");
		Task task=processService.findTaskById(strTaskId);
		processService.completeTask(task);
		model.addAttribute("task",strTaskId);
		/**** update workflow details ****/
		String strWorkflowdetails=request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		workflowDetails.setStatus("COMPLETED");
		workflowDetails.setCompletionTime(new Date());
		workflowDetails.merge();
		/**** display message ****/
		model.addAttribute("type","taskcompleted");
		return "workflow/info";
	}

	@SuppressWarnings("unused")
	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		/**** Date ****/

		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		if(this.getUserLocale().equals(new Locale("mr","IN")))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
					dateFormat, true));
		}
		else
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),this.getUserLocale());
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
					dateFormat, true));
		}
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
		//		binder.registerCustomEditor(List.class, "SupportingMember",
		//				new CustomCollectionEditor(List.class) {
		//			@Override
		//			protected Object convertElement(
		//					final Object element) {
		//				String id = null;
		//
		//				if (element instanceof String) {
		//					id = (String) element;
		//				}
		//				return id != null ? SupportingMember
		//						.findById(SupportingMember.class,
		//								Long.valueOf(id))
		//								: null;
		//			}
		//		});

		/**** Clubbed Entity ****/
		//		binder.registerCustomEditor(List.class, "ClubbedEntity",
		//				new CustomCollectionEditor(List.class) {
		//			@Override
		//			protected Object convertElement(
		//					final Object element) {
		//				String id = null;
		//
		//				if (element instanceof String) {
		//					id = (String) element;
		//				}
		//				return id != null ? ClubbedEntity
		//						.findById(ClubbedEntity.class,
		//								Long.valueOf(id))
		//								: null;
		//			}
		//		});

		/**** Referenced Entity ****/
		//		binder.registerCustomEditor(List.class, "ReferencedEntity",
		//				new CustomCollectionEditor(List.class) {
		//			@Override
		//			protected Object convertElement(
		//					final Object element) {
		//				String id = null;
		//
		//				if (element instanceof String) {
		//					id = (String) element;
		//				}
		//				return id != null ? ReferencedEntity
		//						.findById(ReferencedEntity.class,
		//								Long.valueOf(id))
		//								: null;
		//			}
		//		});
		//----------21012013--------------------------
		/**** Referenced Question for half hour discussion from question ****/
		binder.registerCustomEditor(Question.class, new BaseEditor(new Question()));

	}

	@RequestMapping(method=RequestMethod.GET)
	public String initMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Adding workflowdetails and task to model ****/
		model.addAttribute("workflowdetails",workflowDetails.getId());
		model.addAttribute("workflowstatus",workflowDetails.getStatus());
		Question domain=Question.findById(Question.class,Long.parseLong(workflowDetails.getDeviceId()));

		/**** Populate Model ****/		
		populateModel(domain,model,request,workflowDetails);		
		return workflowDetails.getForm();
	}

	private void populateModel(final Question domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) {
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
				model.addAttribute("selectedSupportingMembersIds",selectedSupportingMembers);
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
					model.addAttribute("answeringDate",domain.getAnsweringDate().getId());
					model.addAttribute("formattedAnsweringDate",FormaterUtil.getDateFormatter(locale).format(domain.getAnsweringDate().getAnsweringDate()));
					model.addAttribute("answeringDateSelected",domain.getAnsweringDate().getId());
				}
			}

			/**** Set Chart answering date ****/
			if(domain.getChartAnsweringDate() != null) {
				model.addAttribute("chartAnsweringDate", domain.getChartAnsweringDate().getId());
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

		/**** UserGroup and UserGroup Type ****/
		model.addAttribute("usergroup",workflowDetails.getAssigneeUserGroupId());
		model.addAttribute("usergroupType",workflowDetails.getAssigneeUserGroupType());

		/**** To have the task creation date and lastReceivingDate if userGroup is department in case of starred questions ***/
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)){
			boolean canAdd = false;

			try{	
				if(domain.getAnsweringDate()!=null){
					if(domain.getAnsweringDate().getLastReceivingDateFromDepartment()!=null){
						model.addAttribute("lastReceivingDateFromDepartment", FormaterUtil.getDateFormatter(locale).format(domain.getAnsweringDate().getLastReceivingDateFromDepartment()));
					}
				}

				CustomParameter serverTimeStamp=CustomParameter.findByName(CustomParameter.class,"SERVER_TIMESTAMP","");
				if(serverTimeStamp!=null){
					if(workflowDetails.getAssignmentTime() != null){							
						model.addAttribute("taskCreationDate", FormaterUtil.getDateFormatter(serverTimeStamp.getValue(),locale).format(workflowDetails.getAssignmentTime()));
					}
				}

				canAdd = true;
			}catch(Exception e){
				logger.error("Last Receiving date from department or task creation date is missing.: "+e.getMessage());
			}
			if(!canAdd){
				model.addAttribute("lastReceivingDateFromDepartment", "");
				model.addAttribute("taskCreationDate", "");
			}
		}

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
			/**** added by sandeep singh(jan 29 2013) ****/
			populateInternalStatus(model,domain,workflowDetails);
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
			model.addAttribute("oldRecommendationStatus",recommendationStatus.getId());
		}	
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
		/**** level ****/
		model.addAttribute("level",workflowDetails.getAssigneeLevel());

		//---------------------------Added by vikas & dhananjay-------------------------------------
		if(questionType.getType().equals("questions_halfhourdiscussion_from_question") || questionType.getType().equals("questions_halfhourdiscussion_standalone")){
			populateForHalfHourDiscussionEdit(model, domain, request);
		}
		//---------------------------Added by vikas & dhananjay-------------------------------------
		/**** add domain to model ****/
		model.addAttribute("domain",domain);
	}

	private void populateInternalStatus(ModelMap model,Question question,WorkflowDetails workflowDetails) {	
		List<Status> internalStatuses=new ArrayList<Status>();
		Status internalStatus=question.getInternalStatus();
		String type=internalStatus.getType();
		String userGroupType=workflowDetails.getAssigneeUserGroupType();
		String locale=question.getLocale();
		String questionType=question.getType().getType();
		/**** First we will check if custom parameter for internal status and usergroupType has been set ****/
		if(type.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)&&
				(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)
						||question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK))){
			CustomParameter assistantSendBackDiscuss=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
			if(assistantSendBackDiscuss!=null){
				internalStatuses=Status.findStatusContainedIn(assistantSendBackDiscuss.getValue(), locale);
			}else{
				CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_BY_DEFAULT","");
				if(defaultCustomParameter!=null){
					internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
				}else{
					model.addAttribute("errorcode","question_putup_options_final_notset");
				}	
			}			
		}
		CustomParameter specificDeviceRecommendationStatusUG=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+question.getRecommendationStatus().getType().toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		if(specificDeviceRecommendationStatusUG!=null){
			internalStatuses=Status.findStatusContainedIn(specificDeviceRecommendationStatusUG.getValue(), locale);
		}else if(specificDeviceStatusUserGroupStatuses!=null){
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
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final Question domain,final BindingResult result) {
		/**** Binding Supporting Members ****/
		String[] strSupportingMembers=request.getParameterValues("supportingMembers");
		if(strSupportingMembers!=null){
			if(strSupportingMembers.length>0){
				List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
				for(String i:strSupportingMembers){
					SupportingMember supportingMember=SupportingMember.findById(SupportingMember.class, Long.parseLong(i));
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
		String[] strClubbedEntities= request.getParameterValues("clubbedEntities");
		if(strClubbedEntities!=null){
			if(strClubbedEntities.length>0){
				List<ClubbedEntity> clubbedEntities=new ArrayList<ClubbedEntity>();
				for(String i:strClubbedEntities){
					ClubbedEntity clubbedEntity=ClubbedEntity.findById(ClubbedEntity.class, Long.parseLong(i));
					clubbedEntities.add(clubbedEntity);
				}
				domain.setClubbedEntities(clubbedEntities);
			}
		}
		String[] strReferencedEntities= request.getParameterValues("referencedEntities");
		if(strReferencedEntities!=null){
			if(strReferencedEntities.length>0){
				List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
				for(String i:strReferencedEntities){
					ReferencedEntity referencedEntity=ReferencedEntity.findById(ReferencedEntity.class, Long.parseLong(i));
					referencedEntities.add(referencedEntity);
				}
				domain.setReferencedEntities(referencedEntities);
			}
		}



		/**** Workflowdetails ****/
		String strWorkflowdetails=(String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));

		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)){

			String operation = request.getParameter("operation");

			if(operation != null){
				if(!operation.isEmpty()){
					if(operation.equals("workflowsubmit")){
						if(domain.getAnswer() == null){
							result.rejectValue("answer", "AnswerEmpty");						
						}
						if(domain.getAnswer().isEmpty()){
							result.rejectValue("answer", "AnswerEmpty");
						}
						if(result.getFieldErrorCount("answer")>0){
							if(!model.containsAttribute("errorcode")){
								model.addAttribute("errorcode","no_answer_provided_department");
								return "workflow/myTasks/error";
							}		
						}
					}
				}
			}
		}

		/**** Updating domain ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
		String strDateOfAnsweringByMinister=request.getParameter("dateOfAnsweringByMinister");
		Date dateOfAnsweringByMinister=null;

		String strDiscussionDate = request.getParameter("discussionDate");
		String strHalfHourDiscussionFromQuestionReference = request.getParameter("halfHourDiscusionFromQuestionReference");
		Date dateDiscussionDate=null;
		Long refQuestionId=null;
		try {
			if(strDateOfAnsweringByMinister!=null){
				if(!strDateOfAnsweringByMinister.isEmpty()){
					dateOfAnsweringByMinister=FormaterUtil.getDateFormatter("en_US").parse(strDateOfAnsweringByMinister);
				}
			}

			if(strDiscussionDate != null){
				if(!strDiscussionDate.isEmpty()){
					dateDiscussionDate = FormaterUtil.getDateFormatter("en_US").parse(strDiscussionDate);
					String strTempDiscussionDate = FormaterUtil.getDateFormatter("yyyy-MM-dd","en_US").format(dateDiscussionDate);
					dateDiscussionDate = FormaterUtil.getDateFormatter("yyyy-MM-dd","en_US").parse(strTempDiscussionDate);

					//set the discussion date
					domain.setDiscussionDate(dateDiscussionDate);
				}			
			}
			if(strHalfHourDiscussionFromQuestionReference != null){
				if(!strHalfHourDiscussionFromQuestionReference.isEmpty()){
					refQuestionId = new Long(strHalfHourDiscussionFromQuestionReference);
					Question refQ = Question.findById(Question.class, refQuestionId);
					domain.setHalfHourDiscusionFromQuestionReference(refQ);
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
		performAction(domain);		
		domain.merge();
		/**** Complete Task ****/		
		//String sendbackactor=request.getParameter("sendbackactor");
		String nextuser=request.getParameter("actor");
		String level="";
		Map<String,String> properties=new HashMap<String, String>();
		properties.put("pv_deviceId",String.valueOf(domain.getId()));
		properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
		if(nextuser!=null){
			if(!nextuser.isEmpty()){
				String[] temp=nextuser.split("#");
				properties.put("pv_user",temp[0]);
				level=temp[2];
			}
		}	
		String endflag=request.getParameter("endflag");
		properties.put("pv_endflag",request.getParameter("endflag"));
		String strTaskId=workflowDetails.getTaskId();
		Task task=processService.findTaskById(strTaskId);
		processService.completeTask(task,properties);		
		if(endflag!=null){
			if(!endflag.isEmpty()){
				if(endflag.equals("continue")){
					ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
					Task newtask=processService.getCurrentTask(processInstance);
					/**** Workflow Detail entry made only if its not the end of workflow ****/
					WorkflowDetails.create(domain,newtask,ApplicationConstants.APPROVAL_WORKFLOW,level);
				}
			}
		}
		workflowDetails.setStatus("COMPLETED");
		workflowDetails.setCompletionTime(new Date());
		workflowDetails.merge();		
		/**** display message ****/
		model.addAttribute("type","taskcompleted");

		return "workflow/info";
	}

	private void performAction(Question domain) {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
			performActionOnAdmission(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)){
			performActionOnRejection(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED)){
			performActionOnConvertToUnstarred(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)){
			performActionOnConvertToUnstarredAndAdmit(domain);
		}else if(internalStatus.startsWith("QUESTION_RECOMMEND_CLARIFICATION_")
				&&recommendationStatus.startsWith("QUESTION_RECOMMEND_CLARIFICATION_")){
			performActionOnClarificationReceived(domain);
		}
	}

	private void performActionOnClarificationReceived(Question domain) {
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
			Status status=Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Question question=i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
					question.setInternalStatus(status);
					question.setRecommendationStatus(status);
					question.simpleMerge();
				}	
			}
		}
	}


	private void performActionOnConvertToUnstarredAndAdmit(Question domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT, domain.getLocale());
		domain.setStatus(finalStatus);
		/**** Setting revised subject,question text,revised reason,revised brief explaination if not already set ****/
		if(domain.getRevisedSubject()==null){			
			domain.setRevisedSubject(domain.getSubject());			
		}else if(domain.getRevisedSubject().isEmpty()){
			domain.setRevisedSubject(domain.getSubject());
		}
		if(domain.getRevisedQuestionText()==null){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}else if(domain.getRevisedQuestionText().isEmpty()){
			domain.setRevisedQuestionText(domain.getQuestionText());
		}
		if(domain.getRevisedReason()==null){
			domain.setRevisedReason(domain.getReason());
		}else if(domain.getRevisedReason().isEmpty()){
			domain.setRevisedReason(domain.getReason());
		}
		if(domain.getRevisedBriefExplanation()==null){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}else if(domain.getRevisedBriefExplanation().isEmpty()){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
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

			Status newInternalStatus=Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, domain.getLocale());
			Status newRecommendationStatus=Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, domain.getLocale());
			DeviceType deviceType=DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
			domain.setType(deviceType);
			for(ClubbedEntity i:clubbedEntities){
				Question question=i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setStatus(finalStatus);
					question.setInternalStatus(finalStatus);
					question.setRecommendationStatus(finalStatus);
					question.setType(deviceType);
				}else{
					question.setInternalStatus(newInternalStatus);
					question.setRecommendationStatus(newRecommendationStatus);
				}			
				question.simpleMerge();
			}
		}
	}
	private void performActionOnConvertToUnstarred(Question domain) {
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
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
			Status status1=Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED, domain.getLocale());
			Status status2=Status.findByType(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED, domain.getLocale());
			DeviceType deviceType=DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
			domain.setType(deviceType);
			for(ClubbedEntity i:clubbedEntities){
				Question question=i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setRecommendationStatus(status2);
					question.setType(deviceType);
				}else{
					question.setInternalStatus(status1);
					question.setRecommendationStatus(status1);				
				}			
				question.simpleMerge();
			}
		}
	}

	private void performActionOnRejection(Question domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_REJECTION, domain.getLocale());
		domain.setStatus(finalStatus);
		/**** Setting revised subject,question text,revised reason,revised brief explaination if not already set ****/
		if(domain.getRevisedSubject()==null){			
			domain.setRevisedSubject(domain.getSubject());			
		}else if(domain.getRevisedSubject().isEmpty()){
			domain.setRevisedSubject(domain.getSubject());
		}
		if(domain.getRevisedQuestionText()==null){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}else if(domain.getRevisedQuestionText().isEmpty()){
			domain.setRevisedQuestionText(domain.getQuestionText());
		}
		if(domain.getRevisedReason()==null){
			domain.setRevisedReason(domain.getReason());
		}else if(domain.getRevisedReason().isEmpty()){
			domain.setRevisedReason(domain.getReason());
		}
		if(domain.getRevisedBriefExplanation()==null){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}else if(domain.getRevisedBriefExplanation().isEmpty()){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
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
			Status status=Status.findByType(ApplicationConstants.QUESTION_PUTUP_REJECTION, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Question question=i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setStatus(finalStatus);
					question.setInternalStatus(finalStatus);
					question.setRecommendationStatus(finalStatus);
				}else{
					question.setInternalStatus(status);
					question.setRecommendationStatus(status);
				}			
				question.simpleMerge();
			}
		}
	}	

	private void performActionOnAdmission(Question domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		/**** Setting revised subject,question text,revised reason,revised brief explaination if not already set ****/
		if(domain.getRevisedSubject()==null){			
			domain.setRevisedSubject(domain.getSubject());			
		}else if(domain.getRevisedSubject().isEmpty()){
			domain.setRevisedSubject(domain.getSubject());
		}
		if(domain.getRevisedQuestionText()==null){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}else if(domain.getRevisedQuestionText().isEmpty()){
			domain.setRevisedQuestionText(domain.getQuestionText());
		}
		if(domain.getRevisedReason()==null){
			domain.setRevisedReason(domain.getReason());
		}else if(domain.getRevisedReason().isEmpty()){
			domain.setRevisedReason(domain.getReason());
		}
		if(domain.getRevisedBriefExplanation()==null){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}else if(domain.getRevisedBriefExplanation().isEmpty()){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
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
			Status status=Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Question question=i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setStatus(finalStatus);
					question.setInternalStatus(finalStatus);
					question.setRecommendationStatus(finalStatus);
				}else{					
					question.setInternalStatus(status);
					question.setRecommendationStatus(status);
				}			
				question.simpleMerge();
			}
		}
	}
}
