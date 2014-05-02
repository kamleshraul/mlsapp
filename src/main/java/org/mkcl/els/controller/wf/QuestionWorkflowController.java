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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler.referenceInsertExecutor;
import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BulkApprovalVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.ClarificationNeededFrom;
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
import org.mkcl.els.domain.Query;
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
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					i.setApprovedText(question.getQuestionText());
					i.setApprovedSubject(question.getSubject());
					model.addAttribute("currentSupportingMember", i.getMember()
							.getId());
					model.addAttribute("domain", i);
					if (i.getDecisionStatus() != null) {
						model.addAttribute("decisionStatus", i
								.getDecisionStatus().getId());
						model.addAttribute("formattedDecisionStatus", i
								.getDecisionStatus().getName());
					}
					CustomParameter customParameter = CustomParameter
							.findByName(CustomParameter.class,
									"SERVER_DATETIMEFORMAT", "");
					if (customParameter != null) {
						SimpleDateFormat format = new SimpleDateFormat(
								customParameter.getValue());
						model.addAttribute("requestReceivedOnDate",
								format.format(i.getRequestReceivedOn()));
					}
					break;
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, question,supportingMembers,locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId",workflowDetails.getId());
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
		Status approveStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale);
		Status rejectStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale);
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
		String strMember=request.getParameter("currentSupportingMember");
		String requestReceivedOn=request.getParameter("requestReceivedOnDate");
		if(requestReceivedOn!=null&& !(requestReceivedOn.isEmpty())){
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT","");
			if(customParameter!=null){
				SimpleDateFormat format=new SimpleDateFormat(customParameter.getValue());
				try {
					domain.setRequestReceivedOn(format.parse(requestReceivedOn));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		Member member=Member.findById(Member.class, Long.parseLong(strMember));
		domain.setMember(member);
		domain.setApprovalDate(new Date());
		domain.merge();
		/**** update workflow details ****/
		String strWorkflowdetails=domain.getWorkflowDetailsId();
		if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();
			/**** complete the task ****/		 
			String strTaskId=workflowDetails.getTaskId();
			Task task=processService.findTaskById(strTaskId);
			processService.completeTask(task);
			model.addAttribute("task",strTaskId);		
		}
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
		WorkflowDetails workflowDetails = null;
		try{
			/**** Workflowdetails ****/
			Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
			if(longWorkflowdetails==null){
				longWorkflowdetails=Long.parseLong(request.getParameter("workflowdetails"));
			}
			workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
			/**** Adding workflowdetails and task to model ****/
			model.addAttribute("workflowdetails",workflowDetails.getId());
			model.addAttribute("workflowstatus",workflowDetails.getStatus());
			Question domain=Question.findById(Question.class,Long.parseLong(workflowDetails.getDeviceId()));

			/**** Populate Model ****/		
			populateModel(domain,model,request,workflowDetails);
		}catch (ELSException e1) {
			model.addAttribute("error", e1.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}

		return workflowDetails.getForm();
	}

	private void populateModel(final Question domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) throws ELSException {

		/**** Add the re-answer ****/
		model.addAttribute("isReanswered", workflowDetails.getDepartmentAnswer());

		/**** Add reanswer if existing ****/
		model.addAttribute("reanswerText", (workflowDetails.getDepartmentAnswer()!=null)? workflowDetails.getDepartmentAnswer():"");

		/**** If reanswer ****/
		boolean boolReanswer = false;
		/**** To set the reanswwerstatus ****/
		String strReanswerStatus = request.getParameter("reanswerstatus");
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)){
			model.addAttribute("reanswerstatus", strReanswerStatus);
		}
		if(strReanswerStatus != null){
			if(!strReanswerStatus.isEmpty()){
				boolReanswer = true;
			}else{
				boolReanswer = false;
			}
		}else{
			boolReanswer = false;
		}
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit",request.getParameter("bulkedit"));
		/**** clear remarks ****/
		domain.setRemarks("");	

		/**** Locale ****/
		String locale=domain.getLocale();					

		/**** House Type ****/
		HouseType houseType=domain.getHouseType();
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseTypeType",houseType.getType());
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
		List<Ministry> ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
		model.addAttribute("ministries",ministries);
		Ministry ministry=domain.getMinistry();
		if(ministry!=null){
			model.addAttribute("ministrySelected",ministry.getId());
			/**** Group ****/
			Group group=domain.getGroup();

			if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
				model.addAttribute("group",domain.getGroup().getId());
			}

			/**** Departments ****/
			List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
			model.addAttribute("departments",departments);
			if(domain.getDepartment()!=null){  
				model.addAttribute("departmentSelected",domain.getDepartment().getId());
			}
			/**** Sub Departments ****/
			List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry, locale);
			model.addAttribute("subDepartments",subDepartments); 
			SubDepartment subDepartment=domain.getSubDepartment();
			if(subDepartment!=null){

				model.addAttribute("subDepartmentSelected",subDepartment.getId());				
			}

			if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
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
						model.addAttribute("formattedLastAnswerReceivingDate", FormaterUtil.getDateFormatter(locale).format(domain.getAnsweringDate().getLastReceivingDateFromDepartment()));
					}
				}
			}
			/**** Set Chart answering date ****/
			if(domain.getChartAnsweringDate() != null) {
				model.addAttribute("chartAnsweringDate", domain.getChartAnsweringDate().getId());
				model.addAttribute("formattedChartAnsweringDate",FormaterUtil.getDateFormatter(locale).format(domain.getChartAnsweringDate().getAnsweringDate()));
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
			String nextInternalStatus[]=internalStatus.getType().split("_");
			model.addAttribute("nextInternalStatus",nextInternalStatus[2]);
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			/**** list of put up options available ****/
			/**** added by sandeep singh(jan 29 2013) ****/

			if(boolReanswer){
				Status reanswerStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_REANSWER, locale);
				domain.setRecommendationStatus(reanswerStatus);
				populateInternalStatus(model, domain.getRecommendationStatus().getType(), workflowDetails.getAssigneeUserGroupType(), locale, domain.getType().getType());
			}else{
				populateInternalStatus(model,domain,locale);
			}
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
			model.addAttribute("oldRecommendationStatus",recommendationStatus.getId());
		}	
		/**** Referenced Questions are collected in refentities****/
		List<Reference> refentities=new ArrayList<Reference>();
		List<String> refentitiesSessionDevice = new ArrayList<String>();
		if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
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

		/**** setting the date of factual position receiving. ****/
		String userGroupType=workflowDetails.getAssigneeUserGroupType();
		String userGroupId=workflowDetails.getAssigneeUserGroupId();
		if((domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))
				||
				(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION))){
			if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) 
					&& (internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
							|| internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {

				/**** setting the questions to be asked in factual position. ****/
				List<MasterVO> questionsToBeAskedInFactualPosition = new ArrayList<MasterVO>();
				String sessionParameter = null;
				if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
					sessionParameter = selectedSession.getParameter("questions_halfhourdiscussion_standalone_questionsAskedForFactualPosition");
				} else if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
						&& internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
					sessionParameter = selectedSession.getParameter("questions_starred_clarificationFromMemberQuestions");
				} else if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
						&& internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
					sessionParameter = selectedSession.getParameter("questions_starred_clarificationFromDepartmentQuestions");
				}
				if(sessionParameter != null) {
					if(!sessionParameter.isEmpty()) {
						for(String i : sessionParameter.split("##")) {	
							MasterVO questionToBeAskedInFactualPosition = new MasterVO();
							questionToBeAskedInFactualPosition.setName(i);
							questionToBeAskedInFactualPosition.setValue(i);
							if(domain.getQuestionsAskedInFactualPosition()!=null && !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
								for(String j : domain.getQuestionsAskedInFactualPosition().split("##")) {
									if(i.replaceAll("<[^>]+>", "").trim().substring(0, 3).contains(j.replaceAll("<[^>]+>", "").trim().substring(0, 3))) {
										questionToBeAskedInFactualPosition.setIsSelected(true);
										break;
									} else {
										questionToBeAskedInFactualPosition.setIsSelected(false);
									}
								}
							} else {
								questionToBeAskedInFactualPosition.setIsSelected(false);
							}
							questionsToBeAskedInFactualPosition.add(questionToBeAskedInFactualPosition);
						}
					}
				}				
				model.addAttribute("questionsToBeAskedInFactualPosition", questionsToBeAskedInFactualPosition);

				if(domain.getLastDateOfFactualPositionReceiving() == null
						&& !domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					List<MasterVO> numberOfDaysForFactualPositionReceiving = new ArrayList<MasterVO>();
					sessionParameter = selectedSession.getParameter(domain.getType().getType()+"_numberOfDaysForFactualPositionReceiving");
					if(sessionParameter != null) {
						if(!sessionParameter.isEmpty()) {
							for(String i : sessionParameter.split("#")) {
								MasterVO data = new MasterVO();
								data.setName(FormaterUtil.formatNumberNoGrouping(Integer.parseInt(i), domain.getLocale()));
								data.setNumber(Integer.parseInt(i));
								numberOfDaysForFactualPositionReceiving.add(data);
							}
						}
					}	

					model.addAttribute("numberOfDaysForFactualPositionReceiving", numberOfDaysForFactualPositionReceiving);
				}						
			}

			/**** populating the questions asked in factual position to show to department. ****/
			if((userGroupType.equals(ApplicationConstants.DEPARTMENT) || userGroupType.equals(ApplicationConstants.MEMBER)) 
					&& (internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
							|| internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
				String questionsAskedInFactualPosition = "";
				if(domain.getQuestionsAskedInFactualPosition() !=null && !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
					//					int count = 1;
					//					for(String i: domain.getQuestionsAskedInFactualPosition().split("##")) {
					//						questionsAskedInFactualPosition += FormaterUtil.formatNumberNoGrouping(count, domain.getLocale()) + ". " + i;
					//						count++;
					//					}
					questionsAskedInFactualPosition = domain.getQuestionsAskedInFactualPosition().replaceAll("##", "<br/>");
				}
				model.addAttribute("formattedQuestionsAskedInFactualPosition", questionsAskedInFactualPosition);
			}
		}
		if(domain.getType() != null){
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

				/**** process variables ****/
				//default values for process variables. can set conditionally for given actor here.
				model.addAttribute("pv_mailflag", "off");

				if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) ||
						internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) && recommendationStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)) {
					model.addAttribute("pv_timerflag", "set");
					model.addAttribute("pv_reminderflag", "set");
					Credential sender = Credential.findByFieldName(Credential.class, "username", workflowDetails.getAssignee(), "");
					model.addAttribute("pv_reminderfrom", sender.getEmail());
					String reminderContent = selectedSession.getParameter(domain.getType().getType()+"_questionsAskedForFactualPosition");
					reminderContent += "\n" + domain.getRevisedQuestionText();
					model.addAttribute("pv_remindercontent", reminderContent);
					String reminderSubject = "reminder about question number " + domain.getNumber();
					model.addAttribute("pv_remindersubject", reminderSubject);
					String lastTimerDuration = selectedSession.getParameter(domain.getType().getType()+"_reminderDayNumberForFactualPosition");
					model.addAttribute("pv_lasttimerduration", lastTimerDuration);
				} else {
					model.addAttribute("pv_timerflag", "off");
					model.addAttribute("pv_reminderflag", "off");
				}

				/**** add domain to model ****/
				model.addAttribute("domain",domain);
			}
		}

		//---------------------------To find the reansweringAttempt---------------------------------
		if(userGroupType.equals(ApplicationConstants.DEPARTMENT)){
			CustomParameter answeringAttempts = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.MAX_ASWERING_ATTEMPTS_STARRED, "");
			if(answeringAttempts != null){
				model.addAttribute("maxAnsweringAttempts", Integer.valueOf(answeringAttempts.getValue()));
				model.addAttribute("answeringAttempts", ((domain.getAnsweringAttemptsByDepartment() == null)? 0:domain.getAnsweringAttemptsByDepartment()));
			}
		}		

		//---------------------------Added by vikas & dhananjay-------------------------------------
		if(questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) || questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			populateForHalfHourDiscussionEdit(model, domain, request);
		}
		//---------------------------Added by vikas & dhananjay-------------------------------------		
		/**** Populating Put up otions and Actors ****/
		if(userGroupId!=null&&!userGroupId.isEmpty()){
			UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(userGroupId));
			List<Reference> actors=new ArrayList<Reference>();
			//TODO: Have to change the condition so as to consider the reanswering coz in normal scenario 
			//department always does is sends back the device			
			if(boolReanswer){
				Status reanswerStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_REANSWER, locale);
				actors=WorkflowConfig.findQuestionActorsVO(domain,reanswerStatus , userGroup, 1, locale);
			}else{
				if(userGroup.getUserGroupType().getType().equals("department")&&internalStatus.getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
					Status sendback=Status.findByType(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK, locale);
					actors=WorkflowConfig.findQuestionActorsVO(domain,sendback , userGroup, Integer.parseInt(domain.getLevel()), locale);
				}else{
					actors=WorkflowConfig.findQuestionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
				}
			}
			model.addAttribute("internalStatusSelected",internalStatus.getId());
			model.addAttribute("actors",actors);
			if(actors!=null&&!actors.isEmpty()){
				String nextActor=actors.get(0).getId();
				String[] actorArr=nextActor.split("#");
				domain.setLevel(actorArr[2]);
				domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
			}
		}
		/**** add domain to model ****/
		model.addAttribute("domain",domain);

		if(workflowDetails.getSendBackBefore() != null){
			model.addAttribute("sendbacktimelimit", workflowDetails.getSendBackBefore().getTime());
		}

	}

	private void populateInternalStatus(final ModelMap model,final Question domain,final String locale) {
		try{
			List<Status> internalStatuses=new ArrayList<Status>();
			DeviceType deviceType=domain.getType();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseType=domain.getHouseType();
			String actor=domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses=Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses=Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else{
				String usergroupType=actor.split("#")[1];
				/**** Final Approving Authority(Final Status) ****/
				CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
				CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
					CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
					}					

					if(finalApprovingAuthorityStatus!=null){
						internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					}
				}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup!=null){
					internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup!=null){
					internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				}	
				/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
				else if(deviceTypeUsergroup!=null){
					internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
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
				String strDates = selectedSession.getParameter(domain.getType().getType()+"_discussionDates");
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
		String userGroupType = null;
		try{
			/**** Is reanswering ****/
			boolean boolReanswering = false;
			String isReanswering = request.getParameter("reanswerstatus");
			if (isReanswering != null) {
				if(!isReanswering.isEmpty()){
					if(isReanswering.equals("reanswer")){
						boolReanswering = true;
						Status reanswerStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_REANSWER, locale.toString());

						String strWorkflowdetails=(String) request.getParameter("workflowdetails");
						WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
						List<Reference> actors=new ArrayList<Reference>();
						UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(workflowDetails.getAssigneeUserGroupId()));
						actors=WorkflowConfig.findQuestionActorsVO(domain,reanswerStatus , userGroup, 1, locale.toString());
						if(!actors.isEmpty()){
							domain.setActor(actors.get(0).getId());
							domain.setRecommendationStatus(reanswerStatus);
						}
					}
				}
			}

			/**** Set the department if department is null ****/
			if(domain.getDepartment() == null){
				if(domain.getSubDepartment() != null){
					domain.setDepartment(domain.getSubDepartment().getDepartment());
				}
			}

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

			/***** To retain the referencedHDS when moving through workflow ****/
			if(domain.getType() != null){
				if((domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
						&& domain.getSession().getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
					String refHDS = request.getParameter("referencedHDS");
					if(refHDS != null && !refHDS.isEmpty()){
						ReferencedEntity refHDSEntity = ReferencedEntity.findById(ReferencedEntity.class, Long.parseLong(refHDS));
						domain.setReferencedHDS(refHDSEntity);
					}
				}			
			}

			if(domain.getType() != null){
				if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
						&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {

					if(domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
							|| domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {

						String strQuestionInFactualPosition = request.getParameter("questionsAskedInThisFactualPosition");
						String strDaysToReceiveFactualPosition = request.getParameter("numberOfDaysForFactualPositionReceiving");

						if(strQuestionInFactualPosition != null && !strQuestionInFactualPosition.isEmpty()) {
							domain.setQuestionsAskedInFactualPosition(strQuestionInFactualPosition);
						}						
						if(strDaysToReceiveFactualPosition != null && !strDaysToReceiveFactualPosition.isEmpty()){
							Integer daysToReceiveFactualPosition = new Integer(strDaysToReceiveFactualPosition);
							domain.setNumberOfDaysForFactualPositionReceiving(daysToReceiveFactualPosition);
						}
					}				
				}
			}

			/**** set statuses in draft in case when clarification is not received for factual position in hds ****/
			if(domain.getType().getType().trim().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(domain.getStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {					
					QuestionDraft latestDraft = null;
					try {
						latestDraft = Question.getLatestQuestionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
					} catch (ELSException e) {
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}		
					if(latestDraft != null){
						latestDraft.setInternalStatus(domain.getStatus());
						latestDraft.setRecommendationStatus(domain.getStatus());
						latestDraft.merge();
					}
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
							if(domain.getAnswer() == null && domain.getFactualPosition()==null && domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
								result.rejectValue("answer", "AnswerEmpty");

								if(domain.getAnswer().isEmpty() && domain.getFactualPosition().isEmpty()){
									result.rejectValue("answer", "AnswerEmpty");
								}
							} 
							if(domain.getInternalStatus().getType()==ApplicationConstants.QUESTION_FINAL_REJECTION){
								if(domain.getRejectionReason()==null){
									result.rejectValue("rejectionReason", "RejectionReasonEmpty");
								}else if(domain.getRejectionReason().isEmpty()){
									result.rejectValue("rejectionReason", "RejectionReasonEmpty");
								}
							}
						}else if(operation.equals("workflowsendback")){
							long currentTimeMillis = System.currentTimeMillis();

							if(currentTimeMillis > workflowDetails.getSendBackBefore().getTime()){
								if(domain.getRemarks() == null){
									result.rejectValue("answer", "AnswerEmpty");						
								}else if(domain.getAnswer().isEmpty()){
									result.rejectValue("answer", "AnswerEmpty");
								}
							}
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

			/**** Updating domain ****/
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
			String strDateOfAnsweringByMinister=request.getParameter("dateOfAnsweringByMinister");
			Date dateOfAnsweringByMinister=null;

			/**** Setting the answering attempts in case of department****/
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)){

				String operation = request.getParameter("operation");

				boolean goAhead = false;

				if(operation != null){
					if(!operation.isEmpty()){
						if(operation.equals("workflowsubmit")){
							goAhead = true;
						}
					}
				}

				if(boolReanswering){
					goAhead = true;
				}	

				if(domain.getAnswer() != null){
					goAhead = true;
				}

				if(goAhead){
					Integer attempts = domain.getAnsweringAttemptsByDepartment();
					if(attempts == null){
						domain.setAnsweringAttemptsByDepartment(1);
					}else{
						domain.setAnsweringAttemptsByDepartment(attempts + 1);
					}
				}
			}

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


			/**** setting the date of factual position receiving. ****/
			userGroupType=workflowDetails.getAssigneeUserGroupType();
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) 
						&& (domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
								|| domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
					if(domain.getLastDateOfFactualPositionReceiving() == null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.DATE, domain.getNumberOfDaysForFactualPositionReceiving());
						domain.setLastDateOfFactualPositionReceiving(calendar.getTime());
					}
				}
			}			

			/**** If reanswer workflow is invoked then its straight forward ****/
			/****  to set the domain's answer as reanswer by department ****/
			if(domain.getType() != null && domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)){
					if(workflowDetails.getDepartmentAnswer() != null){
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)){
							if(workflowDetails.getDepartmentAnswer() != null){
								domain.setAnswer(workflowDetails.getDepartmentAnswer());
							}
						}
					}else{
						/**** if workflow is not of reanswer then in that case find the ****/
						/**** reanswer workflow and set the reanswer to domain ****/

						Map<String, String> parameters = new HashMap<String, String>();
						parameters.put("locale", locale.toString());
						parameters.put("assignee", workflowDetails.getAssignee());
						parameters.put("status", "PENDING");
						parameters.put("deviceId", workflowDetails.getDeviceId());


						List<WorkflowDetails> reanswerWorkflowsIfAny = WorkflowDetails.findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.DESC);
						WorkflowDetails reanswerWorkflowIfAny = null;

						if(reanswerWorkflowsIfAny != null && !reanswerWorkflowsIfAny.isEmpty()){
							for(WorkflowDetails wf : reanswerWorkflowsIfAny){
								if(!wf.getProcessId().equals(workflowDetails.getProcessId()) && wf.getDepartmentAnswer() != null){
									reanswerWorkflowIfAny = wf;
									break;
								}
							}					
							if(reanswerWorkflowIfAny != null){
								domain.setAnswer(reanswerWorkflowIfAny.getDepartmentAnswer());
							}				
						}
					}
				}
			}
			performAction(domain);
			domain.merge();
			String bulkEdit=request.getParameter("bulkedit");
			if(bulkEdit==null||!bulkEdit.equals("yes")){

				/**** Complete Task ****/
				String endflag = null;
				if(boolReanswering && workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)){
					endflag = "continue";
				}else{
					endflag = domain.getEndFlag();
				}

				Map<String,String> properties=new HashMap<String, String>();
				String level="";
				String currentDeviceTypeWorkflowType = null;
				if(domain.getType() != null){
					if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
							&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {

						currentDeviceTypeWorkflowType = ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW;
						String nextuser=domain.getActor();
						level=domain.getLevel();
						properties.put("pv_deviceId",String.valueOf(domain.getId()));
						properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
						String username = "";
						if(nextuser!=null){
							if(!nextuser.isEmpty()){
								String[] temp=nextuser.split("#");
								username = temp[0];
								properties.put("pv_user",username);				
							}
						}	
						properties.put("pv_endflag", endflag);

						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "SERVERCONFIGURED", "");
						String isServerConfigured=customParameter.getValue();
						if(isServerConfigured!=null && !isServerConfigured.equals("")){
							if(isServerConfigured.equals("yes")){
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
												if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) ||
														domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
													Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
													reminderto = recepient.getEmail();								
												} else {
													reminderto=request.getParameter("reminderto");								
												}						
												properties.put("pv_reminderto", reminderto);

												String remindersubject=request.getParameter("remindersubject");						
												properties.put("pv_remindersubject", remindersubject);

												String remindercontent = "";
												if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) ||
														domain.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
													remindercontent += domain.getRevisedQuestionText() + "\n\n";
													if(domain.getQuestionsAskedInFactualPosition() !=null && !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
														int count = 1;
														for(String i: domain.getQuestionsAskedInFactualPosition().split("##")) {
															remindercontent += FormaterUtil.formatNumberNoGrouping(count, domain.getLocale()) + ". " + i + "\n\n";
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
								properties.put("pv_mailflag", "off");
								properties.put("pv_timerflag", "off");
							}
						}

					}else{
						//String sendbackactor=request.getParameter("sendbackactor");
						currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
						String nextuser = null;
						if(boolReanswering){
							nextuser = domain.getActor();
						}else{
							nextuser = request.getParameter("actor");
						}

						properties.put("pv_deviceId",String.valueOf(domain.getId()));
						properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));

						if(nextuser!=null){
							if(!nextuser.isEmpty()){
								String[] temp=nextuser.split("#");
								properties.put("pv_user",temp[0]);
								level=temp[2];
							}
						}
						properties.put("pv_endflag", endflag);
					}
				}

				String strReanswer = request.getParameter("reanswer");
				workflowDetails.setDepartmentAnswer(strReanswer);

				String strTaskId=workflowDetails.getTaskId();
				Task task = null;
				if (!boolReanswering){
					task=processService.findTaskById(strTaskId);
					processService.completeTask(task,properties);

					if(domain.getType() != null && domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
						/**** If user is section officer and if he/she amrks the end of workflow ****/
						/**** Terminate the flows of both normal and reanswer flow if any ****/
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)){
							if(workflowDetails.getDepartmentAnswer() != null){


								if(workflowDetails.getPreviousWorkflowDetail() != null){
									WorkflowDetails prevWorkflowDetails = WorkflowDetails.findById(WorkflowDetails.class, workflowDetails.getPreviousWorkflowDetail());

									Map<String, String> parameters = new HashMap<String, String>();
									parameters.put("locale", locale.toString());
									parameters.put("assignee", workflowDetails.getAssignee());
									parameters.put("status", "PENDING");
									parameters.put("processId", prevWorkflowDetails.getProcessId());

									List<WorkflowDetails> pendingWorkflows = WorkflowDetails.findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.DESC);
									WorkflowDetails pendingWorkflow;

									if(pendingWorkflows != null && !pendingWorkflows.isEmpty()){
										pendingWorkflow = pendingWorkflows.get(0);

										Task prevTask = processService.findTaskById(pendingWorkflow.getTaskId());
										processService.completeTask(prevTask, properties);

										pendingWorkflow.setStatus("COMPLETED");
										pendingWorkflow.setCompletionTime(new Date());
										pendingWorkflow.merge();
									}								
								}
							}else{

								Map<String, String> parameters = new HashMap<String, String>();
								parameters.put("locale", locale.toString());
								parameters.put("assignee", workflowDetails.getAssignee());
								parameters.put("status", "PENDING");
								parameters.put("deviceId", workflowDetails.getDeviceId());


								List<WorkflowDetails> reanswerWorkflowsIfAny = WorkflowDetails.findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.DESC);
								WorkflowDetails reanswerWorkflowIfAny = null;

								if(reanswerWorkflowsIfAny != null && !reanswerWorkflowsIfAny.isEmpty()){
									for(WorkflowDetails wf : reanswerWorkflowsIfAny){
										if(!wf.getProcessId().equals(workflowDetails.getProcessId()) && wf.getDepartmentAnswer() != null){
											reanswerWorkflowIfAny = wf;
											break;
										}
									}

									if(reanswerWorkflowIfAny != null){
										Task prevTask = processService.findTaskById(reanswerWorkflowIfAny.getTaskId());
										processService.completeTask(prevTask, properties);

										reanswerWorkflowIfAny.setStatus("COMPLETED");
										reanswerWorkflowIfAny.setCompletionTime(new Date());
										reanswerWorkflowIfAny.merge();
									}
								}	
							}
						}
					}
				}

				if(endflag!=null){
					if(!endflag.isEmpty()){
						if(endflag.equals("continue")){

							if (boolReanswering){

								ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
								ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
								/**** Stale State Exception ****/
								Question question=Question.findById(Question.class,domain.getId());
								/**** Process Started and task created ****/
								Task reanswertask=processService.getCurrentTask(processInstance);

								WorkflowDetails reanswerWorkflowDetails;
								try {
									reanswerWorkflowDetails = WorkflowDetails.create(domain,reanswertask,ApplicationConstants.APPROVAL_WORKFLOW,level);
									question.setWorkflowDetailsId(reanswerWorkflowDetails.getId());
									reanswerWorkflowDetails.setDepartmentAnswer(strReanswer);
									reanswerWorkflowDetails.setPreviousWorkflowDetail(workflowDetails.getId());

									reanswerWorkflowDetails.merge();

								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}				
							}


							if(!boolReanswering){
								ProcessInstance processInstance = processService.findProcessInstanceById(
										task.getProcessInstanceId());
								Task newtask = processService.getCurrentTask(processInstance);
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails newWFDetails = WorkflowDetails.create(domain, newtask,currentDeviceTypeWorkflowType,level);
								/**** Define the timer ****/
								if (domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {

									if (userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) {
										if(workflowDetails.getSendBackBefore() != null){
											newWFDetails.setSendBackBefore(workflowDetails.getSendBackBefore());
										}else{
											CustomParameter cstpSendBackTimeLimitMinutes = CustomParameter.findByName(CustomParameter.class,
													ApplicationConstants.DEPARTMENT_SENDBACK_TIME_LIMIT,"");

											if (cstpSendBackTimeLimitMinutes != null
													&& !cstpSendBackTimeLimitMinutes.getValue().isEmpty()) {

												int timeLimitMinutes = ((int)Double.parseDouble(cstpSendBackTimeLimitMinutes.getValue())) * 60;
												newWFDetails.setSendBackBefore(new Date(newWFDetails.getAssignmentTime().getTime()+ (timeLimitMinutes * 60 * 1000)));
											}
										}
									} else if (userGroupType.equals(ApplicationConstants.DEPARTMENT)) {

										/**** save the reanswer by department ****/
										newWFDetails.setDepartmentAnswer(strReanswer);
										newWFDetails.setSendBackBefore(workflowDetails.getSendBackBefore());
									}
								}
								newWFDetails.merge();
							}
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
			model.addAttribute("type","success");
			populateModel(domain, model, request, workflowDetails);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "** There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
		return "workflow/question/"+userGroupType;
	}


	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/bulkapproval/init",method=RequestMethod.POST)
	public String getBulkApprovalInit(final HttpServletRequest request,final Locale locale,
			final ModelMap model){

		try{
			/**** Request Params ****/
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			String strQuestionType=request.getParameter("deviceType");
			String strStatus=request.getParameter("status");
			String strWorkflowSubType=request.getParameter("workflowSubType");
			String strItemsCount=request.getParameter("itemsCount");
			String strFile=request.getParameter("file");
			String strLocale=locale.toString();
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
			String strUserGroupType=null;
			String strUsergroup=null;
			if(userGroups!=null){
				if(!userGroups.isEmpty()){
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"QIS_ALLOWED_USERGROUPTYPES", "");
					if(customParameter!=null){
						String allowedUserGroups=customParameter.getValue(); 
						for(UserGroup i:userGroups){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								strUsergroup=String.valueOf(i.getId());
								strUserGroupType=i.getUserGroupType().getType();
								break;
							}
						}
					}								
				}
			}		
			Set<Role> roles=this.getCurrentUser().getRoles();
			String strRole=null;
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					strRole=i.getType();
					break;
				}else if(i.getType().contains("QIS_CLERK")){
					strRole=i.getType();
					break;
				}else if(i.getType().startsWith("QIS_")){
					strRole=i.getType();
					break;
				}else if(i.getType().startsWith("HDS_")){
					if(strQuestionType != null){
						if(!strQuestionType.isEmpty()){
							DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class,"name",strQuestionType,strLocale);
							if(deviceType != null){
								if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
									strRole=i.getType();
									break;
								}
							}
						}
					}
				}

			}		
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strQuestionType!=null&&!(strQuestionType.isEmpty())
					&&strStatus!=null&&!(strStatus.isEmpty())
					&&strRole!=null&&!(strRole.isEmpty())
					&&strUsergroup!=null&&!(strUsergroup.isEmpty())
					&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
					&&strItemsCount!=null&&!(strItemsCount.isEmpty())
					&&strFile!=null&&!(strFile.isEmpty())
					&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
				/**** List of Statuses ****/
				if(strWorkflowSubType.equals("request_to_supporting_member")){
					Status approveStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale.toString());
					Status rejectStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale.toString());
					List<Status> decisionStatus=new ArrayList<Status>();
					decisionStatus.add(approveStatus);
					decisionStatus.add(rejectStatus);
					model.addAttribute("internalStatuses",decisionStatus);
				}else{
					List<Status> internalStatuses=new ArrayList<Status>();
					HouseType houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, strLocale);
					DeviceType questionType=DeviceType.findByFieldName(DeviceType.class,"name",strQuestionType,strLocale);
					Status internalStatus=Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,questionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus!=null){
							internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses",internalStatuses);
				}
				/**** Request Params To Model Attribute ****/
				model.addAttribute("houseType", strHouseType);
				model.addAttribute("sessionType", strSessionType);
				model.addAttribute("sessionYear", strSessionYear);
				model.addAttribute("deviceType", strQuestionType);
				model.addAttribute("status", strStatus);
				model.addAttribute("role", strRole);
				model.addAttribute("usergroup", strUsergroup);
				model.addAttribute("usergroupType", strUserGroupType);
				model.addAttribute("itemscount", strItemsCount);
				model.addAttribute("file", strFile);
				model.addAttribute("workflowSubType",strWorkflowSubType);
			}
			return "workflow/question/bulkapprovalinit";		
		}catch (ELSException ee) {
			model.addAttribute("error", ee.getParameter());
			model.addAttribute("type", "error");
			return "workflow/info";
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
			model.addAttribute("type", "error");
			return "workflow/info";
		}
	}	

	@RequestMapping(value="/bulkapproval/view",method=RequestMethod.POST)
	public String getBulkApprovalView(final HttpServletRequest request,final Locale locale,
			final Model model){
		populateBulkApprovalView(model,request,locale.toString());
		return "workflow/question/bulkapprovalview";		
	}	

	@Transactional
	@RequestMapping(value="/bulkapproval/update",method=RequestMethod.POST)
	public String bulkApproval(final HttpServletRequest request,final Locale locale,
			final Model model){			
		String[] selectedItems = request.getParameterValues("items[]");
		String strInternalStatus=request.getParameter("internalstatus");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		StringBuffer recommendAdmissionMsg=new StringBuffer();
		StringBuffer recommendRejectionMsg=new StringBuffer();
		StringBuffer admittedMsg=new StringBuffer();
		StringBuffer rejectedMsg=new StringBuffer();
		if(selectedItems != null && (selectedItems.length >0)
				&&strInternalStatus!=null&&!strInternalStatus.isEmpty()
				&&strWorkflowSubType!=null&&!strWorkflowSubType.isEmpty()) {
			Status status=null;
			if(!strInternalStatus.equals("-")){
				status=Status.findById(Status.class,Long.parseLong(strInternalStatus));
			}
			for(String i : selectedItems) {
				if(strWorkflowSubType.equals("request_to_supporting_member")){
					String[] temp=i.split("#");
					Long id = Long.parseLong(temp[0]);
					WorkflowDetails wfDetails=WorkflowDetails.findById(WorkflowDetails.class,id);
					/**** Updating Supporting Member ****/
					SupportingMember supportingMember=SupportingMember.findById(SupportingMember.class,Long.parseLong(temp[1]));
					supportingMember.setApprovalDate(new Date());
					supportingMember.setApprovedSubject(wfDetails.getSubject());
					supportingMember.setApprovedText(wfDetails.getText());	
					supportingMember.setDecisionStatus(status);
					/**** Remarks Need To Be Added ****/
					supportingMember.merge();
					/**** complete the task ****/		 
					String strTaskId=wfDetails.getTaskId();
					Task task=processService.findTaskById(strTaskId);
					processService.completeTask(task);
					/**** Update Workflow Details ****/
					wfDetails.setStatus("COMPLETED");
					wfDetails.setCompletionTime(new Date());
					/**** In case of Supporting Member Approval Status should reflect member's actions ****/
					wfDetails.setInternalStatus(status.getName());
					wfDetails.setRecommendationStatus(status.getName());
					wfDetails.merge();					
				}else{
					Long id = Long.parseLong(i);
					WorkflowDetails wfDetails=WorkflowDetails.findById(WorkflowDetails.class,id);
					Question question = Question.findById(Question.class,Long.parseLong(wfDetails.getDeviceId()));
					String actor=request.getParameter("actor");
					if(actor==null||actor.isEmpty()){
						actor=question.getActor();
						String[] temp=actor.split("#");
						actor=temp[1];
					}
					String level=request.getParameter("level");
					if(level==null||level.isEmpty()){
						level=question.getLevel();
					}

					if(actor!=null&&!actor.isEmpty()
							&&level!=null&&!level.isEmpty()){
						Reference reference = null;
						try {
							reference = UserGroup.findQuestionActor(question,actor,level,locale.toString());
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
						if(reference!=null
								&&reference.getId()!=null&&!reference.getId().isEmpty()
								&&reference.getName()!=null&&!reference.getName().isEmpty()){
							/**** Update Actor ****/
							String[] temp=reference.getId().split("#");
							question.setActor(reference.getId());
							question.setLocalizedActorName(temp[3]+"("+temp[4]+")");
							question.setLevel(temp[2]);
							/**** Update Internal Status and Recommendation Status ****/
							if(status!=null){
								if(!status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
										&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)){
									question.setInternalStatus(status);
								}
								question.setRecommendationStatus(status);	
								question.setEndFlag("continue");
							}
							/**** Complete Task ****/
							Map<String,String> properties=new HashMap<String, String>();
							properties.put("pv_deviceId",String.valueOf(question.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
							properties.put("pv_user",temp[0]);
							properties.put("pv_endflag",question.getEndFlag());							
							String strTaskId=wfDetails.getTaskId();
							Task task=processService.findTaskById(strTaskId);
							processService.completeTask(task,properties);	
							if(question.getEndFlag()!=null&&!question.getEndFlag().isEmpty()
									&&question.getEndFlag().equals("continue")){
								/**** Create New Workflow Details ****/
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask=processService.getCurrentTask(processInstance);
								WorkflowDetails workflowDetails2 = null;
								try {
									workflowDetails2 = WorkflowDetails.create(question,newtask,ApplicationConstants.APPROVAL_WORKFLOW,level);
								} catch (ELSException e) {
									e.printStackTrace();
									model.addAttribute("error", e.getParameter());
								}
								question.setWorkflowDetailsId(workflowDetails2.getId());
								question.setTaskReceivedOn(new Date());								
							}
							/**** Update Old Workflow Details ****/
							wfDetails.setStatus("COMPLETED");
							wfDetails.setInternalStatus(question.getInternalStatus().getName());
							wfDetails.setRecommendationStatus(question.getRecommendationStatus().getName());
							wfDetails.setCompletionTime(new Date());
							wfDetails.merge();
							/**** Update Motion ****/
							question.setEditedOn(new Date());
							question.setEditedBy(this.getCurrentUser().getActualUsername());
							question.setEditedAs(wfDetails.getAssigneeUserGroupName());				
							try {
								performAction(question);
							} catch (ELSException e) {
								e.printStackTrace();
								logger.error(e.toString());
								model.addAttribute("error", e.getParameter());
							}
							question.merge();
							if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)){
								recommendAdmissionMsg.append(question.formatNumber()+",");
							}else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)){
								recommendRejectionMsg.append(question.formatNumber()+",");
							}else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
								admittedMsg.append(question.formatNumber()+",");
							}else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)){
								rejectedMsg.append(question.formatNumber()+",");
							}
						}
					}					
				}				
			}			
		}
		model.addAttribute("recommendAdmission", recommendAdmissionMsg.toString());
		model.addAttribute("recommendRejection", recommendRejectionMsg.toString());
		model.addAttribute("admitted", admittedMsg.toString());
		model.addAttribute("rejected", rejectedMsg.toString());
		populateBulkApprovalView(model,request,locale.toString());
		return "workflow/question/bulkapprovalview";
	}

	private void populateBulkApprovalView(final Model model,
			final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("deviceType");			
		String strStatus=request.getParameter("status");
		String strRole=request.getParameter("role");
		String strUsergroup=request.getParameter("usergroup");
		String strUsergroupType=request.getParameter("usergroupType");
		String strItemsCount=request.getParameter("itemscount");
		String strFile=request.getParameter("file");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String strLocale=locale.toString();	
		String assignee=this.getCurrentUser().getActualUsername();
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strQuestionType!=null&&!(strQuestionType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUsergroupType!=null&&!(strUsergroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strFile!=null&&!(strFile.isEmpty())
				&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
			model.addAttribute("workflowSubType", strWorkflowSubType);
			/**** Workflow Details ****/
			List<WorkflowDetails> workflowDetails = null;
			try {
				workflowDetails = WorkflowDetails.
						findAll(strHouseType,strSessionType,strSessionYear,
								strQuestionType,strStatus,strWorkflowSubType,
								assignee,strItemsCount,strLocale,strFile);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals=new ArrayList<BulkApprovalVO>();
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			for(WorkflowDetails i:workflowDetails){
				BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
				Question question=Question.findById(Question.class,Long.parseLong(i.getDeviceId()));
				/**** Bulk Submission for Supporting Members ****/
				if(i.getWorkflowSubType().equals("request_to_supporting_member")){
					bulkApprovalVO.setId(String.valueOf(i.getId()));
					bulkApprovalVO.setDeviceId(String.valueOf(question.getId()));	
					bulkApprovalVO.setDeviceNumber("-");
					bulkApprovalVO.setDeviceType(question.getType().getName());
					bulkApprovalVO.setMember(question.getPrimaryMember().getFullname());
					bulkApprovalVO.setSubject(question.getSubject());							
					List<SupportingMember> supportingMembers=question.getSupportingMembers();
					for(SupportingMember j:supportingMembers){
						User user;
						try {
							user = User.findByUserName(i.getAssignee(), locale.toString());
							Member member=Member.findMember(user.getFirstName(),user.getMiddleName(),user.getLastName(),user.getBirthDate(), locale.toString());
							if(member!=null
									&&member.getId()==j.getMember().getId()){
								bulkApprovalVO.setSupportingMemberId(String.valueOf(j.getId()));
							}
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}
					bulkApprovalVO.setCurrentStatus(i.getStatus());
					if(i.getInternalStatus()!=null){
						bulkApprovalVO.setLastDecision(i.getInternalStatus());
					}else{
						bulkApprovalVO.setLastDecision("-");
					}
					bulkapprovals.add(bulkApprovalVO);
				}else/**** Bulk Submission For Workflows ****/
				{
					/**** File Bulk Submission ****/
					if(strFile!=null&&!strFile.isEmpty()&&!strFile.equals("-")
							&&question.getFile()!=null
							&&question.getFile()==Integer.parseInt(strFile)){
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(question.getId()));				
						if(question.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(question.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(question.getType().getName());
						bulkApprovalVO.setMember(question.getPrimaryMember().getFullname());
						bulkApprovalVO.setSubject(question.getSubject());
						if(question.getRemarks()!=null&&!question.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(question.getRemarks());
						}else{
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(question.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(question.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}/**** Status Wise Bulk Submission ****/
					else if(strFile!=null&&!strFile.isEmpty()&&
							strFile.equals("-")
							){
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(question.getId()));				
						if(question.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(question.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(question.getType().getName());
						bulkApprovalVO.setMember(question.getPrimaryMember().getFullname());
						bulkApprovalVO.setSubject(question.getSubject());
						if(question.getRemarks()!=null&&!question.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(question.getRemarks());
						}else{
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(question.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(question.getEditedAs());	
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}
				}		
			}
			model.addAttribute("bulkapprovals", bulkapprovals);
			if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
				model.addAttribute("questionId",bulkapprovals.get(0).getDeviceId());
			}
		}
	}

	private void performAction(Question domain) throws ELSException {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
			performActionOnAdmission(domain);
		}if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)){
			performActionOnRejection(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REPEATADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REPEATADMISSION)){
			performActionOnAdmission(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REPEATREJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REPEATREJECTION)){
			performActionOnRejection(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_REJECTIONWITHREASON)){
			performActionOnRejection(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED)){
			performActionOnConvertToUnstarred(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED)){
			performActionOnConvertToUnstarredAndAdmitClubbed(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)){
			performActionOnConvertToUnstarredAndAdmit(domain);
		}else if(internalStatus.startsWith("QUESTION_RECOMMEND_CLARIFICATION_")
				&&recommendationStatus.startsWith("QUESTION_RECOMMEND_CLARIFICATION_")){
			performActionOnClarificationReceived(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
			performActionOnClarificationNeededFromDepartment(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
			performActionOnClarificationNeededFromMember(domain);
		}else if((internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_RECIEVED))||
				(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)&&
						recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_RECIEVED))){
			performActionOnClarificationReceived(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_RECIEVED)){
			performActionOnClarificationNotRecieved(domain);
		}else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)){
			performActionOnNameClubbing(domain);
		}
	}

	private void performActionOnNameClubbing(Question domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		domain.setRecommendationStatus(finalStatus);
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

	}


	private void performActionOnClarificationReceived(Question domain) {
		Status newStatus=Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setLevel("1");
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setEndFlag("continue");
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
		/********/
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT, domain.getLocale());
		domain.setStatus(finalStatus);
		DeviceType deviceType=DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		domain.setType(deviceType);
		Status internalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
		domain.setInternalStatus(internalStatus);
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
	
	private void performActionOnConvertToUnstarredAndAdmitClubbed(Question domain) {
		/**** The status,internal status of primary question will be "question_final_convertToUnstarredAndAdmit" 
		 * The recommendation status of primary question will be "question_final_convertToUnstarredAndAdmitClubbedWithPreviousSession" 
		 * The type of primary question will be unstarred and original type will be starred 
		 * The revised subject,revised question text,revised reason and revised brief explaination will be updated ****/
		//Status primaryQuestionNewStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT, domain.getLocale());
		Status primaryQuestionNewStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(primaryQuestionNewStatus);		
		domain.setInternalStatus(primaryQuestionNewStatus);
		DeviceType deviceType=DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		domain.setType(deviceType);		
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
		/**** The status,internal status of clubbed question(which have been through workflow) will be "question_final_convertToUnstarredAndAdmit" 
		 * The recommendation status of clubbed question(which have been through workflow) will be "question_final_convertToUnstarredAndAdmitClubbedWithPreviousSession" 
		 * The type of primary question will be unstarred and original type will be starred 
		 * The revised subject,revised question text will be updated 
		 * The status,internal status of clubbed question(which have not been through workflow) will be "question_putup_convertToUnstarredAndAdmitClubbedWithPreviousSession" 
		 * The recommendation status of clubbed question(which have not been through workflow) will be "question_putup_convertToUnstarredAndAdmitClubbedWithPreviousSession" 
		 * ****/
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
			Status clubbedQuestionsNotInWorkflowStatus=Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Question question=i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setStatus(domain.getStatus());
					question.setInternalStatus(domain.getInternalStatus());
					question.setRecommendationStatus(domain.getRecommendationStatus());
					question.setType(deviceType);
				}else{
					question.setInternalStatus(clubbedQuestionsNotInWorkflowStatus);
					question.setRecommendationStatus(clubbedQuestionsNotInWorkflowStatus);
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

	private void performActionOnRejection(Question domain) throws ELSException {
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
		if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
			if(domain.getRevisedBriefExplanation()==null){
				domain.setRevisedBriefExplanation(domain.getBriefExplanation());
			}else if(domain.getRevisedBriefExplanation().isEmpty()){
				domain.setRevisedBriefExplanation(domain.getBriefExplanation());
			}
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

		if(domain.getType() != null){
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				/******Adding a next HDS of the member to chart on rejection of one of the HDS of that member********/
				Question question = Question.find(domain.getPrimaryMember(), domain.getSession(), domain.getType(), domain.getLocale());
				if(question!=null){
					Chart chart = null;
					chart = Chart.find(new Chart(question.getSession(),question.getType(), question.getLocale()));

					if(question.getNumber()== null){
						Integer number = null;
						try {
							number = Question.assignQuestionNo(question.getHouseType(), question.getSession(), question.getType(), question.getLocale());
						} catch (ELSException e) {
							e.printStackTrace();
						}
						question.setNumber(number+1);
					}
					if(chart!=null){
						Chart.addToChart(question);
					}
				}
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

	//	/**** Bulk Approval ****/
	//	@RequestMapping(value="/bulkapproval",method=RequestMethod.GET)
	//	public String getBulkApprovalView(final HttpServletRequest request,final Locale locale,
	//			final ModelMap model){	
	//		String strHouseType=request.getParameter("houseType");
	//		String strSessionType=request.getParameter("sessionType");
	//		String strSessionYear=request.getParameter("sessionYear");
	//		String strMotionType=request.getParameter("deviceType");
	//		String strStatus=request.getParameter("status");
	//		String strWorkflowSubType=request.getParameter("workflowSubType");
	//		String strLocale=locale.toString();
	//		String strItemsCount=request.getParameter("itemsCount");
	//		String assignee=this.getCurrentUser().getActualUsername();
	//		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
	//		String houseType=strHouseType;
	//		String year=strSessionYear;
	//		String sessionType=strSessionType;
	//		String deviceType=strMotionType;
	//		if(strHouseType!=null&&!(strHouseType.isEmpty())
	//				&&strSessionType!=null&&!(strSessionType.isEmpty())
	//				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
	//				&&strMotionType!=null&&!(strMotionType.isEmpty())
	//				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
	//				&&strStatus!=null&&!(strStatus.isEmpty())
	//				&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){
	//			if(customParameter.getValue().equals("TOMCAT")){
	//				try {
	//					houseType=new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
	//					sessionType=new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
	//					year=new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
	//					deviceType=new String(strMotionType.getBytes("ISO-8859-1"),"UTF-8");
	//				}
	//				catch (UnsupportedEncodingException e) {
	//					e.printStackTrace();
	//				}
	//			}			
	//			List<WorkflowDetails> workflowDetails=WorkflowDetails.
	//			findAll(houseType,sessionType,year,
	//					deviceType,strStatus,strWorkflowSubType,
	//					assignee,strItemsCount,strLocale,"");
	//			List<BulkApprovalVO> bulkapprovals=new ArrayList<BulkApprovalVO>();
	//			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
	//			int count=0;
	//			for(WorkflowDetails i:workflowDetails){
	//				Question question=Question.findById(Question.class,Long.parseLong(i.getDeviceId()));
	//				if(count==0){
	//					populateInternalStatus(model,question,locale.toString());
	//					count++;
	//				}
	//				BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();
	//				bulkApprovalVO.setId(String.valueOf(i.getId()));
	//				bulkApprovalVO.setDeviceId(String.valueOf(question.getId()));
	//				bulkApprovalVO.setDeviceNumber(format.format(question.getNumber()));
	//				bulkApprovalVO.setMember(question.getPrimaryMember().getFullname());
	//				bulkApprovalVO.setSubject(question.getSubject());
	//				if(question.getRemarks()!=null&&!question.getRemarks().isEmpty()){
	//					bulkApprovalVO.setLastRemark(question.getRemarks());
	//				}else{
	//					bulkApprovalVO.setLastRemark("-");
	//				}
	//				bulkApprovalVO.setLastDecision(question.getInternalStatus().getName());
	//				bulkApprovalVO.setLastRemarkBy(question.getEditedAs());	
	//				bulkapprovals.add(bulkApprovalVO);
	//			}
	//			model.addAttribute("bulkapprovals",bulkapprovals);
	//		}
	//		return "question/bulkapproval";		
	//	}	
	//	@Transactional
	//	@RequestMapping(value="/bulkapproval",method=RequestMethod.POST)
	//	public String bulkApproval(final HttpServletRequest request,final Locale locale,
	//			final ModelMap model){	
	//		String selectedItems = request.getParameter("items");
	//		String strStatus=request.getParameter("status");
	//		if(selectedItems != null && ! selectedItems.isEmpty()
	//				&&strStatus!=null&&!strStatus.isEmpty()) {
	//			String[] items = selectedItems.split(",");
	//			List<Question> questions=new ArrayList<Question>();
	//			Status status=Status.findById(Status.class,Long.parseLong(strStatus));
	//			for(String i : items) {
	//				Long id = Long.parseLong(i);
	//				WorkflowDetails wfDetails=WorkflowDetails.findById(WorkflowDetails.class,id);
	//				Question question = Question.findById(Question.class,Long.parseLong(wfDetails.getDeviceId()));
	//				if(!question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
	//					question.setInternalStatus(status);
	//				}
	//				question.setRecommendationStatus(status);
	//				/**** Populate Next Actor ****/
	//				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(wfDetails.getAssigneeUserGroupId()));
	//				List<Reference> actors=WorkflowConfig.findQuestionActorsVO(question, question.getInternalStatus(), userGroup, Integer.parseInt(question.getLevel()), locale.toString());
	//				if(actors!=null&&!actors.isEmpty()){
	//					String nextActor=actors.get(0).getId();
	//					String[] actorArr=nextActor.split("#");
	//					question.setLevel(actorArr[2]);
	//					question.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
	//					question.setActor(nextActor);
	//				}
	//				/**** Complete Task ****/		
	//				String nextuser=question.getActor();
	//				String level=question.getLevel();
	//				Map<String,String> properties=new HashMap<String, String>();
	//				properties.put("pv_deviceId",String.valueOf(question.getId()));
	//				properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
	//				if(nextuser!=null){
	//					if(!nextuser.isEmpty()){
	//						String[] temp=nextuser.split("#");
	//						properties.put("pv_user",temp[0]);
	//					}
	//				}	
	//				String endflag=question.getEndFlag();
	//				properties.put("pv_endflag",request.getParameter("endflag"));
	//				String strTaskId=wfDetails.getTaskId();
	//				Task task=processService.findTaskById(strTaskId);
	//				processService.completeTask(task,properties);		
	//				if(endflag!=null){
	//					if(!endflag.isEmpty()){
	//						if(endflag.equals("continue")){
	//							ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
	//							Task newtask=processService.getCurrentTask(processInstance);
	//							WorkflowDetails workflowDetails2=WorkflowDetails.create(question,newtask,ApplicationConstants.APPROVAL_WORKFLOW,level);
	//							question.setWorkflowDetailsId(workflowDetails2.getId());
	//							question.setTaskReceivedOn(new Date());
	//						}
	//					}
	//				}
	//				/**** Update Workflow Details ****/
	//				wfDetails.setStatus("COMPLETED");
	//				wfDetails.setCompletionTime(new Date());
	//				wfDetails.merge();
	//				/**** Update Domain *****/
	//				question.setEditedOn(new Date());
	//				question.setEditedBy(this.getCurrentUser().getActualUsername());
	//				question.setEditedAs(wfDetails.getAssigneeUserGroupName());				
	//				performAction(question);
	//				question.merge();
	//				questions.add(question);
	//			}
	//			model.addAttribute("questions",questions);
	//		}
	//		return "motion/bulkapprovalack";
	//	}


	//----added for HDS 

	private void performActionOnClarificationNeededFromDepartment(Question domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT, domain.getLocale());
		domain.setStatus(finalStatus);

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
	}

	private void performActionOnClarificationNeededFromMember(Question domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER, domain.getLocale());
		domain.setStatus(finalStatus);

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

	}

	private void performActionOnClarificationNotRecieved(Question domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_DEPARTMENT, domain.getLocale());
		Status newStatus=Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		/*****Setting the statuses of the factual position received resolution 
		 * to put up so that the assistant can put up for admission/rejection again *******/
		((Question) domain).setStatus(finalStatus);
		((Question) domain).setInternalStatus(newStatus);
		((Question) domain).setRecommendationStatus(newStatus);

		domain.setEndFlag("continue");
		domain.setLevel("1");
		domain.setActor(null);
		domain.setLocalizedActorName("");
	}

	@RequestMapping(value="report/currentstatusreport", method=RequestMethod.GET)
	public String getCurrentStatusReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){

		String strDevice = request.getParameter("device");

		if(strDevice != null && !strDevice.isEmpty()){		
			model.addAttribute("device", strDevice);
		}

		response.setContentType("text/html; charset=utf-8");
		return "workflow/question/reports/statusreport";
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="report/{qId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("qId") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "question/error";
		try{
			String strDevice = request.getParameter("device");
			Map<String, MasterVO> finalDataMap = new HashMap<String, MasterVO>();
			
			if(strDevice != null && !strDevice.isEmpty()){
				Question qt = Question.findById(Question.class, id);
				List report = generatetCurrentStatusReport(qt, strDevice, locale.toString());
				//model.addAttribute("report", report);
				if(report != null && !report.isEmpty()){
	
					Object[] obj = (Object[]) report.get(0);
					if(obj[26] != null){
						model.addAttribute("fullSessionName", obj[26].toString());
					}
					if(obj[11] != null){
						model.addAttribute("deviceName", obj[11].toString());
					}
	
					model.addAttribute("currentDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
	
					if(obj[16] != null){
						model.addAttribute("primaryMemConstituency", obj[16].toString());
					}
	
					if(obj[12] != null){
						model.addAttribute("memberName", obj[12].toString());
					}
	
					if(obj[17] != null){
						model.addAttribute("support", obj[17].toString());
					}
	
					if(obj[19] != null){
						model.addAttribute("groupNumber", obj[19].toString());
					}
	
					if(obj[21] != null){
						Date answeringDate = FormaterUtil.formatStringToDate(obj[21].toString(), ApplicationConstants.DB_DATEFORMAT);
						model.addAttribute("answeringDate", FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
					}
	
					if(obj[22] != null){
						Date deptSendDate = FormaterUtil.formatStringToDate(obj[21].toString(), ApplicationConstants.DB_DATEFORMAT);
						model.addAttribute("deptSendDate", FormaterUtil.formatDateToString(deptSendDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
					}
	
					if(obj[23] != null){
						model.addAttribute("priority", obj[23].toString());
					}
	
					if(obj[5] != null){
						model.addAttribute("subject", FormaterUtil.formatNumbersInGivenText(obj[5].toString(), locale.toString()));
					}
	
					if(obj[9] != null){
						model.addAttribute("deviceNumber", obj[9].toString());
					}
					
					if(obj[20] != null){
						model.addAttribute("department", obj[20].toString());
					}
	
					if(obj[24] != null){
						model.addAttribute("ministry", obj[24].toString());
					}
	
					if(obj[4] != null){
						model.addAttribute("details", FormaterUtil.formatNumbersInGivenText(obj[4].toString(), locale.toString()));
					}
	
					List<User> users = User.findByRole(false, "QIS_PRINCIPAL_SECRETARY", locale.toString());
					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
	
					List<MasterVO> actors = new ArrayList<MasterVO>();
					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)? "QIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "QIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
					if(csptAllwedUserGroupForStatusReportSign != null){
						if(csptAllwedUserGroupForStatusReportSign.getValue() != null && !csptAllwedUserGroupForStatusReportSign.getValue().isEmpty()){
							
							for(Object o : report){
								Object[] objx = (Object[])o;
	
								if(objx[27] != null && !objx[27].toString().isEmpty()){
									if(csptAllwedUserGroupForStatusReportSign.getValue().contains(objx[27].toString())){
										
										UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", objx[27].toString(), locale.toString());
										MasterVO actor = new MasterVO();
										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
											actor.setName(userGroupType.getName() + "<br>" + ((objx[1]!=null)?objx[1].toString() : "" ));
										}else{
											actor.setName(userGroupType.getName() + "<br>");
										}
										if(objx[6] != null){
											actor.setValue(objx[6].toString());
										}
										if(objx[28] != null){
											actor.setFormattedNumber(objx[28].toString());
										}
										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
											finalDataMap.put(ApplicationConstants.UNDER_SECRETARY, actor);
										}else{
											finalDataMap.put(userGroupType.getType(), actor);
										}
										
									}
								}
							}
							
							for(String var : csptAllwedUserGroupForStatusReportSign.getValue().split(",")){
								if(var.equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || var.equals(ApplicationConstants.UNDER_SECRETARY)){
									actors.add(finalDataMap.get(ApplicationConstants.UNDER_SECRETARY));
								}else{
									if(finalDataMap.get(var) != null){
										actors.add(finalDataMap.get(var));
									}
								}
							}
							
							if(!actors.isEmpty()){
								String lastUSerGroup = actors.get(actors.size() - 1).getName();
								UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.PRINCIPAL_SECRETARY, locale.toString());
								
								if(!lastUSerGroup.split("<br>")[0].equals(userGroupType.getName())){
									MasterVO actor = new MasterVO();
									actor.setName(userGroupType.getName() + "<br>");
									actor.setValue("");
									actor.setFormattedNumber("");
									actors.add(actor);
								}
							}
							
							if(actors.isEmpty()){
								for(String val : csptAllwedUserGroupForStatusReportSign.getValue().split(",")){
								
									Reference ref = UserGroup.findQuestionActor(qt, val, String.valueOf(0), locale.toString());
									MasterVO actor = new MasterVO();
									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
										actor.setName(ref.getId().split("#")[3] + "<br>" + ref.getId().split("#")[4]);
									}else{
										actor.setName(ref.getId().split("#")[3] + "<br>");
									}
									actor.setValue("");
									actor.setFormattedNumber("");
									actors.add(actor);
								}
							}
	
							model.addAttribute("actors", actors);
						}
					}
	
					page = (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "workflow/question/reports/statusreportlowerhouse": "workflow/question/reports/statusreportupperhouse";
				}		
	
				model.addAttribute("qid", qt.getId());
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}

		response.setContentType("text/html; charset=utf-8");		
		return page;
	}	

	@SuppressWarnings("rawtypes")
	private List generatetCurrentStatusReport(final Question question, final String device, final String locale){
		String support = question.getAllSupportingMembers();
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("id",new String[]{question.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("QIS_CURRENTSTATUS_REPORT", parameters);
		for(Object o : list){
			((Object[])o)[17] = support;			
		}

		return list;  
	}

}
