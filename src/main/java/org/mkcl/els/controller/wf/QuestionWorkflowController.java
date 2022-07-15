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

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BulkApprovalVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.ClarificationNeededFrom;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
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
import org.mkcl.els.domain.YaadiDetails;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

	@RequestMapping(value="supportingmember", method = RequestMethod.GET)
	public String initSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails = (Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Question ****/
		String questionId = workflowDetails.getDeviceId();
		model.addAttribute("question", questionId);
		Question question = Question.findById(Question.class,Long.parseLong(questionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers = question.getSupportingMembers();
		Member member = Member.findMember(this.getCurrentUser().getFirstName(),
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
						model.addAttribute("decisionStatus", i.getDecisionStatus().getId());
						model.addAttribute("formattedDecisionStatus", i.getDecisionStatus().getName());
					}
					CustomParameter customParameter = CustomParameter
							.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
					if (customParameter != null) {
						SimpleDateFormat format = new SimpleDateFormat(
								customParameter.getValue());
						model.addAttribute("requestReceivedOnDate", format.format(i.getRequestReceivedOn()));
					}
					break;
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, question, supportingMembers, locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task", workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId", workflowDetails.getId());
		model.addAttribute("status", workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,
			final Question question, 
			final List<SupportingMember> supportingMembers,
			final String locale){
		/**** Question Type ****/
		DeviceType questionType = question.getType();
		if(questionType != null){
			model.addAttribute("questionType", questionType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session = question.getSession();
		if(session != null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName", question.getHouseType().getName());
		model.addAttribute("houseType", question.getHouseType().getType());
		/**** Supporting Members ****/
		List<Member> members = new ArrayList<Member>();
		if(supportingMembers != null){
			for(SupportingMember i : supportingMembers){
				Member selectedMember = i.getMember();
				members.add(selectedMember);
			}
			if(!members.isEmpty()){
				StringBuffer buffer = new StringBuffer();
				for(Member i : members){
					buffer.append(i.getFullnameLastNameFirst() + ",");
				}
				buffer.deleteCharAt(buffer.length()-1);
				model.addAttribute("supportingMembersName", buffer.toString());
			}
		}
		/**** Decision Status ****/
		Status approveStatus =Status
				.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale);
		Status rejectStatus = Status
				.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale);
		List<Status> decisionStatus = new ArrayList<Status>();
		decisionStatus.add(approveStatus);
		decisionStatus.add(rejectStatus);
		model.addAttribute("decisionStatus", decisionStatus);
		/**** Primary Member ****/
		model.addAttribute("primaryMemberName", question.getPrimaryMember().getFullnameLastNameFirst());
		/**** Priority ****/
		model.addAttribute("priority", question.getPriority());
	}

	@RequestMapping(value="supportingmember",method=RequestMethod.PUT)
	public String updateSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,
			@Valid @ModelAttribute("domain") final SupportingMember domain) {
		/**** update supporting member */
		String strMember = request.getParameter("currentSupportingMember");
		String requestReceivedOn = request.getParameter("requestReceivedOnDate");
		if(requestReceivedOn != null && !(requestReceivedOn.isEmpty())){
			CustomParameter customParameter = 
					CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
			if(customParameter != null){
				SimpleDateFormat format = new SimpleDateFormat(customParameter.getValue());
				try {
					domain.setRequestReceivedOn(format.parse(requestReceivedOn));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		Member member = Member.findById(Member.class, Long.parseLong(strMember));
		domain.setMember(member);
		domain.setApprovalDate(new Date());
		domain.merge();
		/**** update workflow details ****/
		String strWorkflowdetails = domain.getWorkflowDetailsId();
		if(strWorkflowdetails != null && !strWorkflowdetails.isEmpty()){
			WorkflowDetails workflowDetails = 
					WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();
			/**** complete the task ****/		 
			String strTaskId = workflowDetails.getTaskId();
			Task task = processService.findTaskById(strTaskId);
			processService.completeTask(task);
			model.addAttribute("task", strTaskId);		
		}
		/**** display message ****/
		model.addAttribute("type", "taskcompleted");
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
			Long longWorkflowdetails = (Long) request.getAttribute("workflowdetails");
			if(longWorkflowdetails == null){
				longWorkflowdetails = Long.parseLong(request.getParameter("workflowdetails"));
			}
			workflowDetails = WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);			
			Question domain = Question.findById(Question.class, Long.parseLong(workflowDetails.getDeviceId()));
			if(workflowDetails.getWorkflowSubType().contains("clarification")
					&& workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_TIMEOUT)) {
				/**** display message ****/
				model.addAttribute("type","clarification_task_alreadytimeout");
				return "workflow/info";
			} else if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_TIMEOUT)) {
				/**** display message ****/
				model.addAttribute("type","task_already_timeout");
				return "workflow/info";
			}
			/**** Adding workflowdetails and task to model ****/
			model.addAttribute("workflowdetails", workflowDetails.getId());
			model.addAttribute("workflowstatus", workflowDetails.getStatus());
			model.addAttribute("workflowSubType", workflowDetails.getWorkflowSubType());
			Set<Role> roles = this.getCurrentUser().getRoles();
			for(Role r : roles){
				if(r != null && (r.getType().startsWith("QIS_") || r.getType().startsWith("MEMBER_"))){
					
					model.addAttribute("role", r.getType());
					break;
					
				}
			}
			/**** Populate Model ****/		
			populateModel(domain,model,request,workflowDetails);
			/**** Find Latest Remarks ****/
			findLatestRemarksByUserGroup(domain,model,request,workflowDetails);
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

	private void populateModel(final Question domain,
			final ModelMap model,
			final HttpServletRequest request,
			final WorkflowDetails workflowDetails) throws ELSException {

		/**** Add the re-answer ****/
		model.addAttribute("isReanswered", workflowDetails.getDepartmentAnswer());

		/**** Add reanswer if existing ****/
		model.addAttribute("reanswerText",
				(workflowDetails.getDepartmentAnswer()!=null)? workflowDetails.getDepartmentAnswer():"");

		/**** If reanswer ****/
		boolean boolReanswer = false;
		/**** To set the reanswwerstatus ****/
		String strReanswerStatus = request.getParameter("reanswerstatus");
		if((workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
				|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))
				&& workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)){
			
			boolReanswer = true;
		}
		
//		if(strReanswerStatus != null){
//			if(!strReanswerStatus.isEmpty()){
//				boolReanswer = true;
//			}else{
//				boolReanswer = false;
//			}
//		}else{
//			boolReanswer = false;
//		}
		
		/********Set resendRevisedQuestionText **********/
		boolean  boolResendRevisedQuestionText = false;
		if((workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.ASSISTANT)
				|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER))
				&& workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)
				&& (workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
					|| (workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)))){
			boolResendRevisedQuestionText = true;
		}
		
		/******Set Clarification Not Received *********/
		boolean boolClarificationNotReceived = false;
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)
				&& workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)
				&& (workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
						|| workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
						|| workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))){
			boolClarificationNotReceived = true;
		}
		
		/********Set resendRevisedQuestionText **********/
		boolean  boolResendHalfHourForDiscussionDate = false;
		if((workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.ASSISTANT)
				|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER))
				&& workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)
				&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)){
			boolResendHalfHourForDiscussionDate = true;
		}
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit", request.getParameter("bulkedit"));
		/**** clear remarks ****/
		domain.setRemarks("");	

		/**** Locale ****/
		String locale = domain.getLocale();					

		/**** House Type ****/
		HouseType houseType = domain.getHouseType();
		model.addAttribute("formattedHouseType", houseType.getName());
		model.addAttribute("houseTypeType", houseType.getType());
		model.addAttribute("houseType", houseType.getId());

		/**** Session ****/
		Session selectedSession = domain.getSession();
		model.addAttribute("session", selectedSession.getId());

		/**** Session Year ****/
		Integer sessionYear = 0;
		sessionYear = selectedSession.getYear();
		model.addAttribute("formattedSessionYear",
				FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear", sessionYear);

		/**** Session Type ****/
		SessionType  sessionType = selectedSession.getType();
		model.addAttribute("formattedSessionType", sessionType.getSessionType());
		model.addAttribute("sessionType", sessionType.getId());        

		/**** Question Type ****/
		DeviceType questionType = domain.getType();
		model.addAttribute("formattedQuestionType", questionType.getName());
		model.addAttribute("questionType", questionType.getId());
		model.addAttribute("selectedQuestionType", questionType.getType());

		/**** Original Question Type ****/		
		if(domain.getOriginalType() != null) {
			model.addAttribute("originalType", domain.getOriginalType().getId());
		}

		/**** Primary Member ****/
		String memberNames = null;
		String primaryMemberName = null;
		Member member = domain.getPrimaryMember();
		if(member != null){
			model.addAttribute("primaryMember", member.getId());
			primaryMemberName = member.getFullname();
			memberNames = primaryMemberName;
			model.addAttribute("formattedPrimaryMember", primaryMemberName);
		}
		/**** Constituency ****/
		Long houseId = selectedSession.getHouse().getId();
		MasterVO constituency = null;
		if(houseType.getType().equals("lowerhouse")){
			constituency = Member.findConstituencyByAssemblyId(member.getId(), houseId);
			model.addAttribute("constituency", constituency.getName());
		}else if(houseType.getType().equals("upperhouse")){
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
				for(SupportingMember i : selectedSupportingMembers){
					Member m = i.getMember();
					if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						if(m.isActiveMemberOn(new Date(), locale)){
							bufferFirstNamesFirst.append(m.getFullname() + ",");
						}
					}
					supportingMembers.add(m);
				}
				bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
				model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
				model.addAttribute("supportingMembers", supportingMembers);
				model.addAttribute("selectedSupportingMembersIds", selectedSupportingMembers);
				memberNames = primaryMemberName + "," + bufferFirstNamesFirst.toString();
				model.addAttribute("memberNames", memberNames);
			}else{
				model.addAttribute("memberNames", memberNames);
			}
		}else{
			model.addAttribute("memberNames", memberNames);
		}

		/**** Priorities ****/
		CustomParameter customParameter = 
				CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
		if(customParameter != null){
			List<MasterVO> priorities = new ArrayList<MasterVO>();
			for(int i=1 ; i<=Integer.parseInt(customParameter.getValue()); i++){
				priorities.add(new MasterVO(i, FormaterUtil.getNumberFormatterNoGrouping(locale).format(i)));
			}
			model.addAttribute("priorities",priorities);
		}else{
			logger.error("**** Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set ****");
			model.addAttribute("errorcode","highestquestionprioritynotset");
		}
		if(domain.getPriority() != null){
			model.addAttribute("priority", domain.getPriority());
			model.addAttribute("formattedPriority",
					FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getPriority()));
		}

		/**** Ministries ****/
		List<Ministry> ministries = 
				Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
		model.addAttribute("ministries", ministries);
		Ministry ministry = domain.getMinistry();
		if(ministry != null){
			model.addAttribute("ministrySelected", ministry.getId());
			/**** Group ****/
			Group group = domain.getGroup();
			model.addAttribute("formattedGroup",
					FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
			model.addAttribute("group", domain.getGroup().getId());

			/**** Sub Departments ****/
			Date onDate = selectedSession.getEndDate();
			if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION) && onDate.before(new Date())) {
				CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
				if(csptNewHouseFormationInProcess==null) {
					onDate = new Date();
				} else if(csptNewHouseFormationInProcess.getValue()==null) {
					onDate = new Date();
				} else if(!csptNewHouseFormationInProcess.getValue().equals("YES")) {
					onDate = new Date();
				}
			}
			List<SubDepartment> subDepartments = 
					MemberMinister.findAssignedSubDepartments(ministry, onDate, locale);
			model.addAttribute("subDepartments", subDepartments); 
			SubDepartment subDepartment = domain.getSubDepartment();
			if(subDepartment != null){
				model.addAttribute("subDepartmentSelected", subDepartment.getId());				
			}
			//populate original subdepartment
			SubDepartment originalSubDepartment = domain.getOriginalSubDepartment();
			if(originalSubDepartment != null) {
				model.addAttribute("originalSubDepartment", originalSubDepartment.getId());
			}


			/**** Answering Dates ****/
			if(group != null){
				List<QuestionDates> answeringDates = group.getQuestionDates();
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				for(QuestionDates i : answeringDates){
					MasterVO masterVO = new MasterVO(i.getId(),
							FormaterUtil.getDateFormatter(locale).format(i.getAnsweringDate()));
					masterVOs.add(masterVO);
				}
				model.addAttribute("answeringDates", masterVOs);
				if(domain.getAnsweringDate() != null){
					model.addAttribute("answeringDate", domain.getAnsweringDate().getId());
					model.addAttribute("formattedAnsweringDate",FormaterUtil.getDateFormatter(locale).
							format(domain.getAnsweringDate().getAnsweringDate()));
					model.addAttribute("answeringDateSelected", domain.getAnsweringDate().getId());
				}
				//populate original answering date
				QuestionDates originalAnsweringDate = domain.getOriginalAnsweringDate();
				if(originalAnsweringDate != null) {
					model.addAttribute("originalAnsweringDate", originalAnsweringDate.getId());
				}
			}			
			
			/**** Set Chart answering date ****/
			if(domain.getChartAnsweringDate() != null) {
				model.addAttribute("chartAnsweringDate", domain.getChartAnsweringDate().getId());
				model.addAttribute("formattedChartAnsweringDate",FormaterUtil.getDateFormatter(locale).
						format(domain.getChartAnsweringDate().getAnsweringDate()));
			}
			if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				/** Set Last Date of Answer Receiving from Department **/
				Date lastDateOfAnswerReceiving = null;
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)
						&& workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_SENDTOSECTIONOFFICER)) {
					
					String daysCountForReceivingAnswerFromDepartment = "30";
					CustomParameter csptDaysCountForReceivingAnswerFromDepartment = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+ApplicationConstants.DAYS_COUNT_FOR_RECEIVING_ANSWER_FROM_DEPARTMENT, "");
					if(csptDaysCountForReceivingAnswerFromDepartment!=null
							&& csptDaysCountForReceivingAnswerFromDepartment.getValue()!=null) {
						daysCountForReceivingAnswerFromDepartment = csptDaysCountForReceivingAnswerFromDepartment.getValue();
					}
					if(domain.getAnswerRequestedDate()!=null) {
						lastDateOfAnswerReceiving = Holiday.getNextWorkingDateFrom(domain.getAnswerRequestedDate(), Integer.parseInt(daysCountForReceivingAnswerFromDepartment), locale);
					} else {
						lastDateOfAnswerReceiving = Holiday.getNextWorkingDateFrom(new Date(), Integer.parseInt(daysCountForReceivingAnswerFromDepartment), locale);
					}
					domain.setLastDateOfAnswerReceiving(lastDateOfAnswerReceiving);
				} else {
					if(domain.getLastDateOfAnswerReceiving()!=null) {
						lastDateOfAnswerReceiving = domain.getLastDateOfAnswerReceiving();
					}
				}
				if(lastDateOfAnswerReceiving!=null) {
					model.addAttribute("lastDateOfAnswerReceiving", lastDateOfAnswerReceiving);
					model.addAttribute("formattedLastAnswerReceivingDate", FormaterUtil.formatDateToString(lastDateOfAnswerReceiving, ApplicationConstants.SERVER_DATEFORMAT, locale));
				}				
			
			}			
		}	
		/**** Set Last Answer Receiving Date & Last Date of Changing Department for Starred Questions ****/
		QuestionDates actualAnsweringDateForStarredQuestion = null;
		if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			actualAnsweringDateForStarredQuestion = Question.findQuestionDatesForStarredQuestion(domain);
			if(actualAnsweringDateForStarredQuestion==null) {
				if(domain.getAnsweringDate() != null){
					actualAnsweringDateForStarredQuestion = domain.getAnsweringDate();
				}
				if(domain.getChartAnsweringDate() != null){
					actualAnsweringDateForStarredQuestion = domain.getChartAnsweringDate();
				}
			}
			if(actualAnsweringDateForStarredQuestion.getLastReceivingDateFromDepartment() != null){
				String formattedLastRecievingDateFromDepartment = FormaterUtil.getDateFormatter(locale).
						format(actualAnsweringDateForStarredQuestion.getLastReceivingDateFromDepartment());
				model.addAttribute("lastReceivingDateFromDepartment", formattedLastRecievingDateFromDepartment);
				model.addAttribute("formattedLastAnswerReceivingDate", formattedLastRecievingDateFromDepartment);
				
				if(actualAnsweringDateForStarredQuestion.getLastDateForChangingDepartment()!=null){
					model.addAttribute("formattedLastDateForChangingDepartment", 
							FormaterUtil.getDateFormatter(locale).format(actualAnsweringDateForStarredQuestion.
									getLastDateForChangingDepartment()));
					model.addAttribute("lastDateForChangingDepartment", actualAnsweringDateForStarredQuestion.
							getLastDateForChangingDepartment());
				}				
			}
		}
		/**** Submission Date and Creation date****/
		CustomParameter dateTimeFormat = 
				CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat != null){            
			if(domain.getSubmissionDate() != null){
				model.addAttribute("submissionDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(domain.getCreationDate() != null){
				model.addAttribute("creationDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
			}
			if(domain.getWorkflowStartedOn() != null){
				model.addAttribute("workflowStartedOnDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOn()));
			}
			if(domain.getTaskReceivedOn() != null){
				model.addAttribute("taskReceivedOnDate",FormaterUtil.
						getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOn()));
			}
		}
		/**** Number ****/
		if(domain.getNumber() != null){
			model.addAttribute("formattedNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).
					format(domain.getNumber()));
		}
		/**** Created By ****/
		model.addAttribute("createdBy", domain.getCreatedBy());	

		/**** UserGroup and UserGroup Type ****/
		model.addAttribute("usergroup", workflowDetails.getAssigneeUserGroupId());
		model.addAttribute("usergroupType", workflowDetails.getAssigneeUserGroupType());
		model.addAttribute("userGroupName", workflowDetails.getAssigneeUserGroupName());
		
		/**** To have the task creation date and validation on lastReceivingDate if userGroup is department in case of starred questions ***/
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
				|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			boolean canAdd = false;
			
			try{					
				/** validation for restricting late answer filling **/				
				if(domain.getStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
					//check validation flag
					CustomParameter csptValidationFlagForLastReceivingDateFromDepartment = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_"+domain.getHouseType().getType().toUpperCase()+"_"+ApplicationConstants.VALIDATION_FLAG_FOR_LAST_RECEIVING_DATE_FROM_DEPARTMENT, "");
					if(csptValidationFlagForLastReceivingDateFromDepartment!=null) {
						String validationFlagForLastReceivingDateFromDepartment = csptValidationFlagForLastReceivingDateFromDepartment.getValue();
						if(validationFlagForLastReceivingDateFromDepartment!=null
								&& !validationFlagForLastReceivingDateFromDepartment.isEmpty()) {
							if(Boolean.valueOf(validationFlagForLastReceivingDateFromDepartment)) {
								//perform validation
								if(actualAnsweringDateForStarredQuestion!=null) {
									if(DateUtil.compareDatePartOnly(actualAnsweringDateForStarredQuestion.getLastReceivingDateFromDepartment(), new Date())<0) {
										model.addAttribute("lateAnswerFillingFlag", "set");
									}
								} else {
									if(domain.getAnsweringDate()!=null 
											&& domain.getAnsweringDate().getAnsweringDate().after(domain.getChartAnsweringDate().getAnsweringDate())) {									
										if(DateUtil.compareDatePartOnly(domain.getAnsweringDate().getLastReceivingDateFromDepartment(), new Date())<0) {
											model.addAttribute("lateAnswerFillingFlag", "set");
										}
									} else {
										if(DateUtil.compareDatePartOnly(domain.getChartAnsweringDate().getLastReceivingDateFromDepartment(), new Date())<0) {
											model.addAttribute("lateAnswerFillingFlag", "set");
										}
									}									
								}
							}
						}
					}
				}

				CustomParameter serverTimeStamp = 
						CustomParameter.findByName(CustomParameter.class,"SERVER_TIMESTAMP","");
				if(serverTimeStamp != null){
					if(workflowDetails.getAssignmentTime() != null){	
						String formattedTaskCreationDate = FormaterUtil.
								getDateFormatter(serverTimeStamp.getValue(),locale).
								format(workflowDetails.getAssignmentTime());
						model.addAttribute("taskCreationDate", formattedTaskCreationDate);
					}
				}
				canAdd = true;
			}catch(Exception e){
				logger.error("Last Receiving date from department or task creation date is missing.: "
						+ e.getMessage());
			}
			if(!canAdd){
				model.addAttribute("lastReceivingDateFromDepartment", "");
				model.addAttribute("taskCreationDate", "");
			}
		}

		/**** Status,Internal Status and recommendation Status ****/
		Status status = domain.getStatus();
		Status internalStatus = domain.getInternalStatus();
		Status recommendationStatus = domain.getRecommendationStatus();
		if(status != null){
			model.addAttribute("status",status.getId());
		}
		if(internalStatus != null){
			model.addAttribute("internalStatus", internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			String nextInternalStatus[] = internalStatus.getType().split("_");
			model.addAttribute("nextInternalStatus", nextInternalStatus[nextInternalStatus.length-1]);
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
					|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
				if(workflowDetails.getWorkflowSubType().equals(internalStatus.getType())) {
					model.addAttribute("formattedInternalStatus", internalStatus.getName());
				} else {
					model.addAttribute("formattedInternalStatus", workflowDetails.getInternalStatus());
				}
			} else {
				model.addAttribute("formattedInternalStatus", internalStatus.getName());
			}			
			/**** list of put up options available ****/
			/**** added by sandeep singh(jan 29 2013) ****/

			if(boolReanswer){
				Status reanswerStatus = null;
				if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
					reanswerStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_REANSWER, locale);
				}else if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
					reanswerStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_REANSWER, locale);
				}
				model.addAttribute("reanswerstatus", reanswerStatus.getType());
				domain.setRecommendationStatus(reanswerStatus);
				populateInternalStatus(model, domain.getRecommendationStatus().getType(),
						workflowDetails.getAssigneeUserGroupType(), locale, domain.getType().getType());
			}else if(boolResendRevisedQuestionText){
				Status resendQuestionTextStatus = null;
				if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
					resendQuestionTextStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_RESENDREVISEDQUESTIONTEXT, locale);
				}else if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
					resendQuestionTextStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_RESENDREVISEDQUESTIONTEXT, locale);
				}
				model.addAttribute("resendQuestionTextStatus", resendQuestionTextStatus.getType());
				domain.setRecommendationStatus(resendQuestionTextStatus);
				populateInternalStatus(model, domain.getRecommendationStatus().getType(),
						workflowDetails.getAssigneeUserGroupType(), locale, domain.getType().getType());
			}else if(boolClarificationNotReceived){
				Status clarificationStatus = null;
				if(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
					clarificationStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_REPUTUP, locale);
				}else if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
					clarificationStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_REPUTUP, locale);
				}
				model.addAttribute("clarificationStatus", clarificationStatus.getType());
				domain.setRecommendationStatus(clarificationStatus);
				populateInternalStatus(model, clarificationStatus.getType(),
						workflowDetails.getAssigneeUserGroupType(), locale, domain.getType().getType());
			}else if(boolResendHalfHourForDiscussionDate){
				Status sendHalfHourForDiscussionDate = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_PROCESSED_SENDDISCUSSIONDATEINTIMATION, locale);
				model.addAttribute("sendHalfHourForDiscussionDate", sendHalfHourForDiscussionDate.getType());
				domain.setRecommendationStatus(sendHalfHourForDiscussionDate);
				populateInternalStatus(model, sendHalfHourForDiscussionDate.getType(),
						workflowDetails.getAssigneeUserGroupType(), locale, domain.getType().getType());
			}else{
				if(workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
					populateInternalStatus(model,domain,domain.getRecommendationStatus(),domain.getLocale());
				}else if (workflowDetails.getWorkflowType().equals(ApplicationConstants.QUESTION_SUPPLEMENTARY_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ANSWER_CONFIRMATION_WORKFLOW)){
					populateInternalStatus(model, domain.getRecommendationStatus().getType(),
							workflowDetails.getAssigneeUserGroupType(), locale, domain.getType().getType());
				}else {
					populateInternalStatus(model,domain,domain.getInternalStatus(),domain.getLocale());
				}				
			}
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
			model.addAttribute("oldRecommendationStatus",recommendationStatus.getId());
		}	
		
		
		/****Populating Ballot Status****/
		if(domain.getBallotStatus() != null) {
			model.addAttribute("ballotStatusId", domain.getBallotStatus().getId());
		}
		
		
		/**** Referenced Questions are collected in refentities****/
		List<Reference> refentities = QuestionController.getReferencedEntityReferences(domain, locale);
		model.addAttribute("referencedQuestions",refentities);

		/**** Clubbed Questions are collected in references ****/
		String permission = "no";
		CustomParameter partiallyClubbedParameter = CustomParameter.findByName(CustomParameter.class, "PERMISSION_TO_DISPLAY_PARTIAL_CLUBBED_QUESTIONS", "");
		if(partiallyClubbedParameter != null){
			permission = partiallyClubbedParameter.getValue();
		}
		List<Reference> references = new ArrayList<Reference>();
		List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(domain);
		StringBuffer buffer1 = new StringBuffer();
		buffer1.append(memberNames+",");			
		for(ClubbedEntity ce : clubbedEntities){
			Reference reference = new Reference();
			reference.setId(String.valueOf(ce.getId()));
			reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).
					format(ce.getQuestion().getNumber()));
			reference.setNumber(String.valueOf(ce.getQuestion().getId()));
			if(permission.equals("no")){
				if(ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().contains("FINAL")
						|| ce.getQuestion().getInternalStatus().getType().contains(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)){
					reference.setState("yes");
				}
			}else{
				reference.setState("yes");
			}
			references.add(reference);
			String tempPrimary = ce.getQuestion().getPrimaryMember().getFullname();
			if(!buffer1.toString().contains(tempPrimary)){
				buffer1.append(ce.getQuestion().getPrimaryMember().getFullname() + ",");
			}
			List<SupportingMember> clubbedSupportingMember = ce.getQuestion().getSupportingMembers();
			if(clubbedSupportingMember != null && !clubbedSupportingMember.isEmpty()){
				for(SupportingMember l : clubbedSupportingMember){
					if(l.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						Member supportingMember = l.getMember();
						if(supportingMember.isActiveMemberOn(new Date(), locale)){
							String tempSupporting=supportingMember.getFullname();
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
		String allMembersNames = buffer1.toString();
		model.addAttribute("memberNames", allMembersNames);
		if(!references.isEmpty()){
			model.addAttribute("clubbedQuestions", references);
		}else{
			if(domain.getParent() != null){
				model.addAttribute("formattedParentNumber", FormaterUtil.
						getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
				model.addAttribute("parent", domain.getParent().getId());
				// Populate latest revised question text from parent question
				String latestRevisedQuestionTextFromParentQuestion = domain.getParent().getRevisedQuestionText();
				model.addAttribute("latestRevisedQuestionTextFromParentQuestion",latestRevisedQuestionTextFromParentQuestion);
			}
		}
		
		// Populate latest revised question text from clubbed questions
		String latestRevisedQuestionTextFromClubbedQuestions = "";
		if(clubbedEntities!=null & !clubbedEntities.isEmpty()){
			ClubbedEntity ce = clubbedEntities.get(0); //first position clubbed question
			latestRevisedQuestionTextFromClubbedQuestions = ce.getQuestion().getRevisedQuestionText();			
		}
		model.addAttribute("latestRevisedQuestionTextFromClubbedQuestions",latestRevisedQuestionTextFromClubbedQuestions);
		
		/**** level ****/
		model.addAttribute("level", workflowDetails.getAssigneeLevel());

		/**** setting the date of factual position receiving. ****/
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		String userGroupId = workflowDetails.getAssigneeUserGroupId();
		// String deviceTypeType = domain.getType().getType();
		String internalStatusType = internalStatus.getType();
		if(userGroupType.equals(ApplicationConstants.SECTION_OFFICER) 
				|| userGroupType.equals(ApplicationConstants.ASSISTANT)){
			if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				||internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				||internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				||internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
				/**** setting the questions to be asked in factual position. ****/
				List<MasterVO> questionsToBeAskedInFactualPosition = new ArrayList<MasterVO>();
				String sessionParameter = null;
				if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
					sessionParameter = selectedSession.
							getParameter("questions_starred_clarificationFromDepartmentQuestions");
				}else if (internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
					sessionParameter = selectedSession.
							getParameter("questions_unstarred_clarificationFromDepartmentQuestions");
				}else if (internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
					sessionParameter = selectedSession.
							getParameter("questions_shortnotice_clarificationFromDepartmentQuestions");
				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
					sessionParameter = selectedSession.
							getParameter("questions_halfHourFromQuestion_clarificationFromDepartmentQuestions");
				}
			
				if(sessionParameter != null && !sessionParameter.isEmpty()) {
					for(String i : sessionParameter.split("##")) {	
						MasterVO questionToBeAskedInFactualPosition = new MasterVO();
						questionToBeAskedInFactualPosition.setName(i);
						questionToBeAskedInFactualPosition.setValue(i);
						if(domain.getQuestionsAskedInFactualPosition()!=null 
								&& !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
							for(String j : domain.getQuestionsAskedInFactualPosition().split("##")) {
								if(i.replaceAll("<[^>]+>", "").trim().substring(0, 3).
										contains(j.replaceAll("<[^>]+>", "").trim().substring(0, 3))) {
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
				model.addAttribute("questionsToBeAskedInFactualPosition", questionsToBeAskedInFactualPosition);
			}else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				||internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				||internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				||internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
				/**** setting the questions to be asked in factual position. ****/
				List<MasterVO> questionsToBeAskedInFactualPositionForMember = new ArrayList<MasterVO>();
				String sessionParameter = null;
				if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						||internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
					sessionParameter = selectedSession.
							getParameter("questions_starred_clarificationFromMemberQuestions");
				}else if (internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						||internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
					sessionParameter = selectedSession.
							getParameter("questions_unstarred_clarificationFromMemberQuestions");
				}else if (internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						||internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
					sessionParameter = selectedSession.
							getParameter("questions_shortnotice_clarificationFromMemberQuestions");
				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						||internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
					sessionParameter = selectedSession.
							getParameter("questions_halfHourFromQuestion_clarificationFromMemberQuestions");
				}
			
				if(sessionParameter != null && !sessionParameter.isEmpty()) {
					for(String i : sessionParameter.split("##")) {	
						MasterVO questionToBeAskedInFactualPosition = new MasterVO();
						questionToBeAskedInFactualPosition.setName(i);
						questionToBeAskedInFactualPosition.setValue(i);
						if(domain.getQuestionsAskedInFactualPositionForMember()!=null 
								&& !domain.getQuestionsAskedInFactualPositionForMember().isEmpty()) {
							for(String j : domain.getQuestionsAskedInFactualPositionForMember().split("##")) {
								if(i.replaceAll("<[^>]+>", "").trim().substring(0, 3).
										contains(j.replaceAll("<[^>]+>", "").trim().substring(0, 3))) {
									questionToBeAskedInFactualPosition.setIsSelected(true);
									break;
								} else {
									questionToBeAskedInFactualPosition.setIsSelected(false);
								}
							}
						} else {
							questionToBeAskedInFactualPosition.setIsSelected(false);
						}
						questionsToBeAskedInFactualPositionForMember.add(questionToBeAskedInFactualPosition);
					}
				}				
				model.addAttribute("questionsToBeAskedInFactualPositionForMember", questionsToBeAskedInFactualPositionForMember);
			}else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
				/**** setting the questions to be asked in factual position for Members. ****/
				List<MasterVO> questionsToBeAskedInFactualPositionForMember = new ArrayList<MasterVO>();
				List<MasterVO> questionsToBeAskedInFactualPosition = new ArrayList<MasterVO>();
				String sessionParameterForDepartment = null;
				String sessionParameter = null;
				if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
					sessionParameter = selectedSession.
							getParameter("questions_starred_clarificationFromMemberQuestions");
					sessionParameterForDepartment = selectedSession.
							getParameter("questions_starred_clarificationFromDepartmentQuestions");
				}else if (internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
					sessionParameter = selectedSession.
							getParameter("questions_unstarred_clarificationFromMemberQuestions");
					sessionParameterForDepartment = selectedSession.
							getParameter("questions_unstarred_clarificationFromDepartmentQuestions");
				}else if (internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
					sessionParameter = selectedSession.
							getParameter("questions_shortnotice_clarificationFromMemberQuestions");
					sessionParameterForDepartment = selectedSession.
							getParameter("questions_shortnotice_clarificationFromDepartmentQuestions");
				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
					sessionParameter = selectedSession.
							getParameter("questions_halfHourFromQuestion_clarificationFromMemberQuestions");
					sessionParameterForDepartment = selectedSession.
							getParameter("questions_halfHourFromQuestion_clarificationFromDepartmentQuestions");
				}
			
				if(sessionParameter != null && !sessionParameter.isEmpty()) {
					for(String i : sessionParameter.split("##")) {	
						MasterVO questionToBeAskedInFactualPosition = new MasterVO();
						questionToBeAskedInFactualPosition.setName(i);
						questionToBeAskedInFactualPosition.setValue(i);
						if(domain.getQuestionsAskedInFactualPositionForMember()!=null 
								&& !domain.getQuestionsAskedInFactualPositionForMember().isEmpty()) {
							for(String j : domain.getQuestionsAskedInFactualPositionForMember().split("##")) {
								if(i.replaceAll("<[^>]+>", "").trim().substring(0, 3).
										contains(j.replaceAll("<[^>]+>", "").trim().substring(0, 3))) {
									questionToBeAskedInFactualPosition.setIsSelected(true);
									break;
								} else {
									questionToBeAskedInFactualPosition.setIsSelected(false);
								}
							}
						} else {
							questionToBeAskedInFactualPosition.setIsSelected(false);
						}
						questionsToBeAskedInFactualPositionForMember.add(questionToBeAskedInFactualPosition);
					}
				}				
				model.addAttribute("questionsToBeAskedInFactualPositionForMember", questionsToBeAskedInFactualPositionForMember);
				
				
				/**** setting the questions to be asked in factual position. ****/
				
				if(sessionParameterForDepartment != null && !sessionParameterForDepartment.isEmpty()) {
					for(String i : sessionParameterForDepartment.split("##")) {	
						MasterVO questionToBeAskedInFactualPosition = new MasterVO();
						questionToBeAskedInFactualPosition.setName(i);
						questionToBeAskedInFactualPosition.setValue(i);
						if(domain.getQuestionsAskedInFactualPosition()!=null 
								&& !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
							for(String j : domain.getQuestionsAskedInFactualPosition().split("##")) {
								if(i.replaceAll("<[^>]+>", "").trim().substring(0, 3).
										contains(j.replaceAll("<[^>]+>", "").trim().substring(0, 3))) {
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
				model.addAttribute("questionsToBeAskedInFactualPosition", questionsToBeAskedInFactualPosition);
			}
		}
		if(userGroupType.equals(ApplicationConstants.DEPARTMENT) || userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					||internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
				String questionsAskedInFactualPosition = "";
				if(domain.getQuestionsAskedInFactualPosition() !=null 
						&& !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
					questionsAskedInFactualPosition = 
							domain.getQuestionsAskedInFactualPosition().replaceAll("##", "<br/>");
				}
				model.addAttribute("formattedQuestionsAskedInFactualPosition", questionsAskedInFactualPosition);
				
			}
			
		}else if(userGroupType.equals(ApplicationConstants.MEMBER)){
			if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					||internalStatusType.
						equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.
						equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					||internalStatusType.
						equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.
						equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					||internalStatusType.
						equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					||internalStatusType.
						equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					||internalStatusType.
						equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
				String questionsAskedInFactualPosition = "";
				if(domain.getQuestionsAskedInFactualPositionForMember() !=null 
						&& !domain.getQuestionsAskedInFactualPositionForMember().isEmpty()) {
					questionsAskedInFactualPosition = 
							domain.getQuestionsAskedInFactualPositionForMember().replaceAll("##", "<br/>");
				}
				model.addAttribute("formattedQuestionsAskedInFactualPositionForMember", questionsAskedInFactualPosition);
			}
		}
			

		//---------------------------To find the reansweringAttempt---------------------------------
		if(userGroupType.equals(ApplicationConstants.DEPARTMENT) || userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			CustomParameter answeringAttempts = CustomParameter.
					findByFieldName(CustomParameter.class, "name", 
							ApplicationConstants.MAX_ASWERING_ATTEMPTS_STARRED, "");
			if(answeringAttempts != null){
				model.addAttribute("maxAnsweringAttempts", Integer.valueOf(answeringAttempts.getValue()));
				model.addAttribute("answeringAttempts", ((domain.getAnsweringAttemptsByDepartment() == null)? 0:domain.getAnsweringAttemptsByDepartment()));
			}
		}		

		//---------------------------Added by vikas & dhananjay-------------------------------------
		if(questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ){
			populateForHalfHourDiscussionEdit(model, domain, request);
		}
		//---------------------------Added by vikas & dhananjay-------------------------------------		
		/**** Populating Put up otions and Actors ****/
		if(userGroupId != null && !userGroupId.isEmpty()){
			UserGroup userGroup = UserGroup.findById(UserGroup.class,Long.parseLong(userGroupId));
			List<Reference> actors = new ArrayList<Reference>();
			//TODO: Have to change the condition so as to consider the reanswering coz in normal scenario 
			//department always does is sends back the device			
//			if(boolReanswer){
//				Status reanswerStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_REANSWER, locale);
//				actors = WorkflowConfig.findQuestionActorsVO(domain,reanswerStatus , userGroup, 1, locale);
//			}else{
//				if(userGroup.getUserGroupType().getType().equals("department")
//						&& (internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
//							|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
//							|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION))){
//					Status sendback = null;
//					if(questionType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
//						sendback = Status.findByType(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK, locale);
//					}else if(questionType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
//						sendback = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_SENDBACK, locale);
//					}else if(questionType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
//						sendback = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_SENDBACK, locale);
//					}
//				 	actors = WorkflowConfig.
//							findQuestionActorsVO(domain, sendback ,userGroup, Integer.parseInt(domain.getLevel()), locale);
//				}else if(userGroup.getUserGroupType().getType().equals(ApplicationConstants.MEMBER)
//						&& internalStatusType.
//								equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
//					
//				}else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
//						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
//						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)
//						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW)) {
//					actors = WorkflowConfig.
//							findQuestionActorsVO(domain, recommendationStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
//				}else{
//					actors = WorkflowConfig.
//							findQuestionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
//				}
				
				if(userGroup.getUserGroupType().getType().equals(ApplicationConstants.MEMBER)
						&& internalStatusType.
								endsWith(ApplicationConstants.STATUS_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
					
				}else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW)) {
					actors = WorkflowConfig.
							findQuestionActorsVO(domain, recommendationStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
				}else{
					actors = WorkflowConfig.
							findQuestionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
				}
//			}
			model.addAttribute("internalStatusSelected", internalStatus.getId());
			model.addAttribute("actors",actors);
		}
		/**** add domain to model ****/
		model.addAttribute("domain", domain);

		if(workflowDetails.getSendBackBefore() != null){
			model.addAttribute("sendbacktimelimit", workflowDetails.getSendBackBefore().getTime());
		}
		
		/****Level for RevisedDraft workflow****/
		model.addAttribute("revisedDraftInitialLevel", 1);
		
		/**** Answer related Dates ****/
		String allowedDeviceTypesForAnswerRelatedDates = "";
		CustomParameter deviceTypesForAnswerRelatedDates = 
				CustomParameter.findByName(CustomParameter.class, 
						"DEVICETYPES_FOR_ANSWER_RELATED_DATES", locale.toString());
		if(deviceTypesForAnswerRelatedDates != null) {
			allowedDeviceTypesForAnswerRelatedDates = deviceTypesForAnswerRelatedDates.getValue();
		} else {
			allowedDeviceTypesForAnswerRelatedDates = 
					ApplicationConstants.STARRED_QUESTION + ", " + ApplicationConstants.UNSTARRED_QUESTION;
		}
		model.addAttribute("allowedDeviceTypesForAnswerRelatedDates", allowedDeviceTypesForAnswerRelatedDates);
		if(domain.getAnswerRequestedDate() != null) {
			model.addAttribute("formattedAnswerRequestedDate",
					FormaterUtil.formatDateToString(domain.getAnswerRequestedDate(), 
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		if(domain.getAnswerReceivedDate()!=null) {
			model.addAttribute("formattedAnswerReceivedDate", 
					FormaterUtil.formatDateToString(domain.getAnswerReceivedDate(),
							ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
		
		/**** Yaadi related things ****/
		if(domain.getYaadiLayingDate()!=null) {
			model.addAttribute("yaadiLayingDate", 
					FormaterUtil.formatDateToString(domain.getYaadiLayingDate(),
							ApplicationConstants.SERVER_DATEFORMAT, locale));
		}
		/** check whether question was removed from unstarred yaadi purposely  **/
		if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				&& domain.getParent()==null
				&& domain.getStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				&& domain.getAnswer()!=null && !domain.getAnswer().isEmpty() 
				&& !domain.getAnswer().equals("<p></p>") && !domain.getAnswer().equals("<br><p></p>")) {
			Boolean isRemovedFromYaadiDetails = domain.checkWhetherIsRemovedFromYaadiDetails();
			if(isRemovedFromYaadiDetails!=null && isRemovedFromYaadiDetails.equals(true)) {
				model.addAttribute("isRemovedFromYaadiDetails", true);
			}
		}
		
		/**** Populate flag for department change allowed for given actor ****/
		String processingMode = "";
		String sessionProcessingMode = domain.getSession().getParameter(domain.getType().getType()+"_processingMode");
		if(sessionProcessingMode!=null && !sessionProcessingMode.isEmpty()) {
			processingMode = sessionProcessingMode;
		} else {
			processingMode = domain.getHouseType().getType();
		}
		CustomParameter csptDepartmentChangeRestricted = CustomParameter.findByName(CustomParameter.class, domain.getOriginalType().getType().toUpperCase()+"_"+processingMode.toUpperCase()+"_"+userGroupType.toUpperCase()+"_DEPARTMENT_CHANGE_RESTRICTED", locale);
		if(csptDepartmentChangeRestricted!=null && csptDepartmentChangeRestricted.getValue()!=null && csptDepartmentChangeRestricted.getValue().equals("YES")) {
			
			if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION) //allowed for questions converted to unstarred in previous sessions
					&& new Date().after(domain.getSession().getEndDate())) {
				
				model.addAttribute("departmentChangeRestricted", "NO");
			} else {
				model.addAttribute("departmentChangeRestricted", "YES");
			}			
		} else {
			model.addAttribute("departmentChangeRestricted", "NO");
		}
	}

	private void populateInternalStatus(final ModelMap model,
			final Question domain, final Status putupOptionsStatus,
			final String locale) {
		try{
			List<Status> internalStatuses = new ArrayList<Status>();
			DeviceType deviceType = domain.getType();
			HouseType houseType = domain.getHouseType();
			Group group = domain.getGroup();
			String actor = domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus = CustomParameter.
						findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else{
				String usergroupType = actor.split("#")[1];
				/**** Final Approving Authority(Final Status) ****/
				CustomParameter finalApprovingAuthority = null;
				Workflow workflow = Workflow.findByStatus(putupOptionsStatus, locale);
				if(workflow!=null) {
					if(group!=null) {
						finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_GROUP"+group.getNumber()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(finalApprovingAuthority==null) {
							finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
							if(finalApprovingAuthority==null) {
								finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
							}
						}
					} else {
						finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(finalApprovingAuthority==null) {
							finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						}
					}					
				}				
				if(finalApprovingAuthority==null) {
					finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
				}				
				CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+putupOptionsStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority != null && finalApprovingAuthority.getValue().contains(usergroupType)) {
					CustomParameter finalApprovingAuthorityStatus = null;
					if(workflow!=null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(finalApprovingAuthorityStatus==null) {
							finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase()+"_"+workflow.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						}						
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					}
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
					}
					if(finalApprovingAuthorityStatus != null){
						internalStatuses=Status.
								findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					}
				}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				}	
				/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
				else if(deviceTypeUsergroup != null){
					internalStatuses=Status.
							findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
				}		
			}		
			/**** Internal Status****/
			model.addAttribute("internalStatuses", internalStatuses);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateInternalStatus(final ModelMap model, 
			final String type,
			final String userGroupType,
			final String locale, 
			final String questionType) {
		List<Status> internalStatuses = new ArrayList<Status>();
		try{
			/**** First we will check if custom parameter for device type,internal status and usergroupType has been set ****/
			CustomParameter specificDeviceStatusUserGroupStatuses = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			if(specificDeviceStatusUserGroupStatuses != null){
				internalStatuses = Status.
						findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceUserGroupStatuses != null){
				internalStatuses=Status.
						findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			}else if(specificStatuses != null){
				internalStatuses=Status.
						findStatusContainedIn(specificStatuses.getValue(), locale);
			}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)) {
				CustomParameter finalStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus != null){
					internalStatuses = Status.findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus != null){
						internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter != null){
							internalStatuses = Status.
									findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode","question_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))) {
				CustomParameter recommendStatus = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus != null){
					internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter != null){
						internalStatuses = Status.
								findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode", "question_putup_options_final_notset");
					}		
				}
			}	
			/**** Internal Status****/
			model.addAttribute("internalStatuses", internalStatuses);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void populateForHalfHourDiscussionEdit(final ModelMap model, 
			final Question domain, 
			final HttpServletRequest request) {
		Session selectedSession = domain.getSession();
		DeviceType questionType = domain.getType();

		if (selectedSession != null) {

			Integer selYear = selectedSession.getYear();
			List<Reference> halfhourdiscussion_sessionYears = new ArrayList<Reference> ();

			Reference reference = new Reference();

			reference.setId(selYear.toString());
			reference.setName(FormaterUtil.formatNumberNoGrouping(new Integer(selYear), domain.getLocale()));
			halfhourdiscussion_sessionYears.add(reference);

			reference = null;
			reference = new Reference();

			reference.setId((new Integer(selYear.intValue()-1)).toString());
			reference.setName(FormaterUtil.formatNumberNoGrouping(new Integer(selYear-1), domain.getLocale()));
			halfhourdiscussion_sessionYears.add(reference);				

			model.addAttribute("halfhourdiscussion_sessionYears", halfhourdiscussion_sessionYears);

			/*
			 * adding session.parameters.numberOfSupprtingMembers and
			 * session.parametrs.numberOfSupprtingMembersComparator
			 */
			String numberOfSupportingMembers = 
					selectedSession.getParameter(questionType.getType()+ "_numberOfSupportingMembers");
			String numberOfSupportingMembersComparator = 
					selectedSession.getParameter(questionType.getType()+ "_numberOfSupportingMembersComparator");

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

				model.addAttribute("numberOfSupportingMembersComparatorHTML", numberOfSupportingMembersComparator);
			}

			List<String> discussionDates = new ArrayList<String>();
			SimpleDateFormat sdf = null;

			if (selectedSession != null) {

				//------changed 21012013-----------------
				String strDates = selectedSession.
						getParameter(domain.getType().getType() + "_discussionDates");
				//-----------21012013
				if(strDates != null && !strDates.isEmpty()){

					String[] dates = strDates.split("#");

					try {
						sdf = FormaterUtil.getDBDateParser(selectedSession.getLocale());
						for (int i = 0; i < dates.length; i++) {
							discussionDates.add(FormaterUtil.getDateFormatter("dd/MM/yyyy", 
									selectedSession.getLocale()).format(sdf.parse(dates[i])));
						}
						model.addAttribute("discussionDates", discussionDates);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				}
			}

			if (domain.getDiscussionDate() != null) {
				model.addAttribute("discussionDateSelected", FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT, "en_US").format(domain.getDiscussionDate()));
				model.addAttribute("formattedDiscussionDateSelected", FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
			}else{
				model.addAttribute("discussionDateSelected", null);
				model.addAttribute("formattedDiscussionDateSelected", null);
			}
			if (domain.getHalfHourDiscusionFromQuestionReference() != null) {
				model.addAttribute("referredQuestionNumber", 
						FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getHalfHourDiscusionFromQuestionReference().getNumber()));
				model.addAttribute("refQuestionId", 
						domain.getHalfHourDiscusionFromQuestionReference().getId());
			}else{
				if(domain.getHalfHourDiscusionFromQuestionReferenceNumber() != null
						&& !domain.getHalfHourDiscusionFromQuestionReferenceNumber().isEmpty()){
					model.addAttribute("referredQuestionNumber",
							FormaterUtil.formatNumberNoGrouping(new Integer(domain.getHalfHourDiscusionFromQuestionReferenceNumber()), domain.getLocale()));
				}
			}
			
			if (domain.getReferenceDeviceAnswerDate() != null) {
				model.addAttribute("refDeviceAnswerDate", 
						FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT, "en_US").format(domain.getReferenceDeviceAnswerDate()));
				model.addAttribute("formattedRefDeviceAnswerDate",
						FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getReferenceDeviceAnswerDate()));
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
			final Locale locale,
			@Valid @ModelAttribute("domain") final Question domain,
			final BindingResult result) {
		// Added the following code due to bug in Multselect plugin, When we select no entry , it sets the value as "," which gives exception
		if(domain.getQuestionsAskedInFactualPosition() != null && !domain.getQuestionsAskedInFactualPosition().isEmpty() 
				&& domain.getQuestionsAskedInFactualPosition().equals(",")){
			domain.setQuestionsAskedInFactualPosition("");
		}
		/**** Workflowdetails ****/
		String strWorkflowdetails = (String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails = 
				WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		try{
			Question previousVersionQuestion = Question.findById(Question.class, domain.getId());
			Group prevGroup = previousVersionQuestion.getGroup();
			Ministry prevMinistry = previousVersionQuestion.getMinistry();
			SubDepartment prevSubdepartment = previousVersionQuestion.getSubDepartment();
			
			if(userGroupType.equals(ApplicationConstants.MEMBER)
				&&	domain.getInternalStatus().getType().
				endsWith(ApplicationConstants.STATUS_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
				Question question = Question.findById(Question.class, domain.getId());
				if(domain.getFactualPositionFromMember() != null && !domain.getFactualPositionFromMember().isEmpty()){
					question.setFactualPositionFromMember(domain.getFactualPositionFromMember());
				}
				question.setEditedOn(new Date());
				question.setEditedBy(this.getCurrentUser().getActualUsername());
				question.setEditedAs(workflowDetails.getAssigneeUserGroupName());
				question.merge();
				
				Map<String,String> properties=new HashMap<String, String>();
				properties.put("pv_deviceId", String.valueOf(question.getId()));
				properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
				User user = User.find(domain.getPrimaryMember());
				Credential credential = user.getCredential();
				properties.put("pv_user", credential.getUsername());
				properties.put("pv_endflag", "end");
				Task prevTask = processService.findTaskById(workflowDetails.getTaskId());
				processService.completeTask(prevTask, properties);
				workflowDetails.setStatus("COMPLETED");
				workflowDetails.setCompletionTime(new Date());
				workflowDetails.merge();
				
				/**** display message ****/
				model.addAttribute("type","taskcompleted");
				return "workflow/info";
						
				
			}else{
				String operation = request.getParameter("operation");
				if(operation != null && !operation.isEmpty() && operation.equals("save")){
					previousVersionQuestion.setEditedOn(new Date());
					previousVersionQuestion.setEditedBy(this.getCurrentUser().getActualUsername());
					previousVersionQuestion.setEditedAs(workflowDetails.getAssigneeUserGroupName());
					if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
						previousVersionQuestion.setRevisedQuestionText(domain.getRevisedQuestionText());
					}
					if(domain.getRevisedBriefExplanation() != null && !domain.getRevisedBriefExplanation().isEmpty()){
						previousVersionQuestion.setRevisedBriefExplanation(domain.getRevisedBriefExplanation());
					}
					if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
						previousVersionQuestion.setRevisedSubject(domain.getRevisedSubject());
					}
					if(domain.getRevisedReason() != null && !domain.getRevisedReason().isEmpty()){
						previousVersionQuestion.setRevisedReason(domain.getRevisedReason());
					}
					previousVersionQuestion.merge();
					model.addAttribute("type","success");
					/**** Adding workflowdetails and task to model ****/
					model.addAttribute("workflowdetails", workflowDetails.getId());
					model.addAttribute("workflowstatus", workflowDetails.getStatus());
					model.addAttribute("workflowSubType", workflowDetails.getWorkflowSubType());
					
					Set<Role> roles = this.getCurrentUser().getRoles();
					for(Role r : roles){
						if(r != null && (r.getType().startsWith("QIS_") || r.getType().startsWith("MEMBER_"))){
							
							model.addAttribute("role", r.getType());
							break;
							
						}
					}
					populateModel(previousVersionQuestion, model, request, workflowDetails);
					/**** Find Latest Remarks ****/
					findLatestRemarksByUserGroup(previousVersionQuestion, model, request, workflowDetails);
					//return "workflow/myTasks/"+workflowDetails.getId()+"/process";
					return "workflow/question/"+userGroupType;
				}else{
					/**** Is reanswering ****/
					boolean boolReanswering = false;
					String isReanswering = request.getParameter("reanswerstatus");
					if(isReanswering != null && !isReanswering.isEmpty()){
						boolReanswering = true;
					}
					
					/*** Is Resubmission of Revised Question Text***/
					boolean boolResendRevisedQuestionText = false; 
					String resendRevisedQuestionTextStatus = request.getParameter("resendQuestionTextStatus");
					if(resendRevisedQuestionTextStatus != null && !resendRevisedQuestionTextStatus.isEmpty()){
						boolResendRevisedQuestionText = true;
					}
					
					/**** Is Clarification of Question Received or not *************/
					boolean boolClarificationStatus = false;
					String clarificationStatus = request.getParameter("clarificationStatus");
					if(clarificationStatus != null && !clarificationStatus.isEmpty()){
						boolClarificationStatus = true;
					}
					
					boolean boolSendDiscussionDateToDepartment = false;
					String sendDiscussionDateStatus = request.getParameter("sendHalfHourForDiscussionDate");
					if(sendDiscussionDateStatus != null && !sendDiscussionDateStatus.isEmpty()){
						boolSendDiscussionDateToDepartment = true;
					}
					/**** Binding Supporting Members ****/
					String[] strSupportingMembers = request.getParameterValues("supportingMembers");
					if(strSupportingMembers != null){
						if(strSupportingMembers.length > 0){
							List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
							for(String i : strSupportingMembers){
								SupportingMember supportingMember = 
										SupportingMember.findById(SupportingMember.class, Long.parseLong(i));
								supportingMembers.add(supportingMember);
							}
							domain.setSupportingMembers(supportingMembers);
						}
					}
					
					String[] strClubbedEntities = request.getParameterValues("clubbedEntities");
					if(strClubbedEntities != null){
						if(strClubbedEntities.length > 0){
							List<ClubbedEntity> clubbedEntities = new ArrayList<ClubbedEntity>();
							for(String i : strClubbedEntities){
								ClubbedEntity clubbedEntity = 
										ClubbedEntity.findById(ClubbedEntity.class, Long.parseLong(i));
								clubbedEntities.add(clubbedEntity);
							}
							domain.setClubbedEntities(clubbedEntities);
						}
					}
					
					String[] strReferencedEntities = request.getParameterValues("referencedEntities");
					if(strReferencedEntities != null){
						if(strReferencedEntities.length > 0){
							List<ReferenceUnit> referencedEntities = new ArrayList<ReferenceUnit>();
							for(String i : strReferencedEntities){
								ReferenceUnit referencedEntity = ReferenceUnit.
										findById(ReferenceUnit.class, Long.parseLong(i));
								referencedEntities.add(referencedEntity);
							}
							domain.setReferencedEntities(referencedEntities);
						}
					}
	
					if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
							|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	
						if(operation != null && !operation.isEmpty()){
	
							if(operation.equals("workflowsubmit")){
								if((domain.getAnswer() == null || domain.getAnswer().isEmpty())
										&& (domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_ADMISSION))
										&& (domain.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTOSECTIONOFFICER))
										&& (domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
												|| domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION))){
	
									result.rejectValue("answer", "AnswerEmpty");
									
								} else if((domain.getFactualPosition()==null || domain.getFactualPosition().isEmpty())
										&& (domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_CLARIFICATION_FROM_DEPARTMENT)
												|| domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT))
										&& (domain.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTOSECTIONOFFICER))
										&& (domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
												|| domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION))){
	
									result.rejectValue("factualPosition", "FactualPositionEmpty");
									
								}
								String internalStatusType = domain.getInternalStatus().getType();
								if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)||
										internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECTION)||
										internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECTION)||
										internalStatusType.
										equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION)){
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
							
							if(result.getFieldErrorCount("factualPosition")>0){
								if(!model.containsAttribute("errorcode")){
									model.addAttribute("errorcode","no_factual_position_provided_department");
									return "workflow/myTasks/error";
								}		
							}
						}
					}
	
					/**** Updating domain ****/
					domain.setEditedOn(new Date());
					domain.setEditedBy(this.getCurrentUser().getActualUsername());
					domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
					String strDateOfAnsweringByMinister = request.getParameter("dateOfAnsweringByMinister");
					Date dateOfAnsweringByMinister = null;
	
					/**** Setting the answering attempts in case of department****/
					if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
							|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
	
						boolean goAhead = false;
	
						if(operation != null && !operation.isEmpty()){
							if(operation.equals("workflowsubmit")){
								goAhead = true;
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
					String strHalfHourDiscussionFromQuestionReference = 
							request.getParameter("halfHourDiscusionFromQuestionReference");
					Date dateDiscussionDate = null;
					Long refQuestionId = null;
					try { 
						if(strDateOfAnsweringByMinister != null && !strDateOfAnsweringByMinister.isEmpty()){
							dateOfAnsweringByMinister=FormaterUtil.getDateFormatter("en_US").parse(strDateOfAnsweringByMinister);
						}
	
						if(strDiscussionDate != null){
							if(!strDiscussionDate.isEmpty()){
								dateDiscussionDate = FormaterUtil.getDateFormatter("en_US").parse(strDiscussionDate);
								String strTempDiscussionDate = 
										FormaterUtil.getDateFormatter("yyyy-MM-dd","en_US").format(dateDiscussionDate);
								dateDiscussionDate =
										FormaterUtil.getDateFormatter("yyyy-MM-dd","en_US").parse(strTempDiscussionDate);
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
							if(strDateOfAnsweringByMinister != null && !strDateOfAnsweringByMinister.isEmpty()){
								dateOfAnsweringByMinister = 
										FormaterUtil.getDateFormatter(locale.toString()).
										parse(strDateOfAnsweringByMinister);
							}
						} catch (ParseException e) {
							logger.error(e.getMessage());
						}
					}
					domain.setDateOfAnsweringByMinister(dateOfAnsweringByMinister);
					
					/** copy updated revised question text of parent to its all clubbed questions if any **/
					if(domain.getParent()==null 
							&& domain.getClubbedEntities()!=null 
							&& !domain.getClubbedEntities().isEmpty()) {	
						String updatedRevisedQuestionText = domain.getRevisedQuestionText();
						if(updatedRevisedQuestionText!=null && !updatedRevisedQuestionText.isEmpty()) {
							Question qt = Question.findById(Question.class, domain.getId());
							if(qt.getRevisedQuestionText()==null || qt.getRevisedQuestionText().isEmpty() || !qt.getRevisedQuestionText().equals(updatedRevisedQuestionText)) {
								for(ClubbedEntity ce: domain.getClubbedEntities()) {
									Question clubbedQuestion = ce.getQuestion();
									clubbedQuestion.setRevisedQuestionText(updatedRevisedQuestionText);
									clubbedQuestion.simpleMerge();
								}
							}				
						}			
					}
					
					/**** updating submission date and creation date ****/
					String strCreationDate = request.getParameter("setCreationDate");
					String strSubmissionDate = request.getParameter("setSubmissionDate");
					String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
					String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
					CustomParameter dateTimeFormat = 
							CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
					if(dateTimeFormat != null){
						SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
						try {
							if(strSubmissionDate != null && !strSubmissionDate.isEmpty()){
								domain.setSubmissionDate(format.parse(strSubmissionDate));
							}
							if(strCreationDate != null && !strCreationDate.isEmpty()){
								domain.setCreationDate(format.parse(strCreationDate));
							}
							if(strWorkflowStartedOnDate != null && !strWorkflowStartedOnDate.isEmpty()){
								domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
							}
							if(strTaskReceivedOnDate != null && !strTaskReceivedOnDate.isEmpty()){
								domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
							}
						}
						catch (ParseException e) {
							e.printStackTrace();
						}
					}
					
					/**** answer related dates ****/
					String allowedDeviceTypes = "";
					CustomParameter deviceTypesForAnswerRelatedDates = 
							CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_FOR_ANSWER_RELATED_DATES", locale.toString());
					if(deviceTypesForAnswerRelatedDates!=null) {
						allowedDeviceTypes = deviceTypesForAnswerRelatedDates.getValue();
					} else {
						allowedDeviceTypes = 
								ApplicationConstants.STARRED_QUESTION + ", " + ApplicationConstants.UNSTARRED_QUESTION;
					}
					if(allowedDeviceTypes.contains(domain.getType().getType())) {
						SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
						String strAnswerRequestedDate = request.getParameter("setAnswerRequestedDate");
						if(strAnswerRequestedDate != null && !strAnswerRequestedDate.isEmpty()) {
							if(dateTimeFormat != null) {						
								domain.setAnswerRequestedDate(format.parse(strAnswerRequestedDate));
							}					
						}
						String strAnswerReceivedDate = request.getParameter("setAnswerReceivedDate");
						if(strAnswerReceivedDate !=null && !strAnswerReceivedDate.isEmpty()) {
							if(dateTimeFormat != null) {
								domain.setAnswerReceivedDate(format.parse(strAnswerReceivedDate));
							}					
						}
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)
								&& (workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
										|| workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION))
								&& (domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODEPARTMENT)
										|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_SENDTODEPARTMENT))
								&& (domain.getAnswer()==null || domain.getAnswer().isEmpty())) {
							domain.setAnswerRequestedDate(new Date());
							String daysCountForReceivingAnswerFromDepartment = "30";
							CustomParameter csptDaysCountForReceivingAnswerFromDepartment = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_"+domain.getHouseType().getType().toUpperCase()+"_"+ApplicationConstants.DAYS_COUNT_FOR_RECEIVING_ANSWER_FROM_DEPARTMENT, "");
							if(csptDaysCountForReceivingAnswerFromDepartment!=null
									&& csptDaysCountForReceivingAnswerFromDepartment.getValue()!=null) {
								daysCountForReceivingAnswerFromDepartment = csptDaysCountForReceivingAnswerFromDepartment.getValue();
							}
							Date lastDateOfAnswerReceiving = Holiday.getNextWorkingDateFrom(domain.getAnswerRequestedDate(), Integer.parseInt(daysCountForReceivingAnswerFromDepartment), locale.toString());
							domain.setLastDateOfAnswerReceiving(lastDateOfAnswerReceiving);
						}
						
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)
								&& (workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
										|| workflowDetails.getWorkflowSubType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION))
								&& domain.getAnswer()!=null && !domain.getAnswer().isEmpty() && domain.getAnswerReceivedDate()==null) {					
							domain.setAnswerReceivedDate(new Date());
							domain.setAnswerReceivedMode(ApplicationConstants.ANSWER_RECEIVED_MODE_ONLINE);
						}
						
						if(domain.getLastDateOfAnswerReceiving()==null) {
							String strLastDateOfAnswerReceiving = request.getParameter("setLastDateOfAnswerReceiving");
							if(strLastDateOfAnswerReceiving!=null && !strLastDateOfAnswerReceiving.isEmpty()) {
								Date lastDateOfAnswerReceiving = FormaterUtil.getDateFormatter("en_US").parse(strLastDateOfAnswerReceiving);
								//Added the above code as the following code was giving exception of unparseble date
								//Date lastDateOfAnswerReceiving = FormaterUtil.formatStringToDate(strLastDateOfAnswerReceiving, ApplicationConstants.DB_DATEFORMAT, locale.toString());
								domain.setLastDateOfAnswerReceiving(lastDateOfAnswerReceiving);
							}
						}
					}
	
					/**** setting the date of factual position receiving. ****/
					userGroupType=workflowDetails.getAssigneeUserGroupType();
					
					/** allow question to be eligible for unstarred yaadi if it was removed before & now allowed **/
					String isAllowedInYaadiStr = request.getParameter("isAllowedInYaadi");
					if(isAllowedInYaadiStr!=null && !isAllowedInYaadiStr.isEmpty()) {
						Boolean isAllowedInYaadi = Boolean.parseBoolean(isAllowedInYaadiStr);
						if(isAllowedInYaadi!=null && isAllowedInYaadi.equals(true)) {
							YaadiDetails.allowDeviceInYaadiDetails(domain);
						}
					}					
	
					/**** If reanswer workflow is invoked then its straight forward ****/
					/****  to set the domain's answer as reanswer by department ****/
					if(domain.getType() != null && 
							(domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
							|| domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION))){
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)){
							if(workflowDetails.getDepartmentAnswer() != null){
								if(workflowDetails.getAssigneeUserGroupType().
										equals(ApplicationConstants.SECTION_OFFICER)){
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
								List<WorkflowDetails> reanswerWorkflowsIfAny = WorkflowDetails.
										findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.DESC);
								WorkflowDetails reanswerWorkflowIfAny = null;
	
								if(reanswerWorkflowsIfAny != null && !reanswerWorkflowsIfAny.isEmpty()){
									for(WorkflowDetails wf : reanswerWorkflowsIfAny){
										if(!wf.getProcessId().equals(workflowDetails.getProcessId()) 
												&& wf.getDepartmentAnswer() != null){
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
	
					String currentDeviceTypeWorkflowType = null;
					Workflow workflowFromUpdatedStatus = null;
					if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTODEPARTMENT)){
						 workflowFromUpdatedStatus = Workflow.findByType(ApplicationConstants.QUESTION_SUPPLEMENTARY_WORKFLOW, locale.toString());
					} else if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_RECOMMENDANSWERFORCONFIRMATION)
									|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_ANSWERCONFIRMED)){
						 workflowFromUpdatedStatus = Workflow.findByType(ApplicationConstants.ANSWER_CONFIRMATION_WORKFLOW, locale.toString());
					} else{
						 workflowFromUpdatedStatus = domain.findWorkflowFromStatus();
					}
					if(workflowFromUpdatedStatus!=null) {
						currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
					}			
					
					/**** added by dhananjayb.. required in case when domain is updated with start of new workflow before completion of current workflow ****/
					String endFlagForCurrentWorkflow = domain.getEndFlag();				
					String actorForCurrentWorkflow = domain.getActor();
					{
						/* Find if next actors are not active then create a draft for them if draft is 
						 * not existing for that actors.
						 */
						try{
							String strNextuser = request.getParameter("actor");
							String[] nextuser = null;
							int nextUserLevel = 0;
							if(strNextuser != null && !strNextuser.isEmpty()){
									nextuser = strNextuser.split("#");
									nextUserLevel = Integer.parseInt(nextuser[2]);
							} 
													
							Question q = null;
							
							if(domain.getId() != null){
								q = Question.findById(Question.class, domain.getId());
							}else{
								q = domain.copyQuestion();
							}
							
							
							Map<String, String[]> params = new HashMap<String, String[]>();
							params.put("locale", new String[]{locale.toString()});
							params.put("sessionId", new String[]{domain.getSession().getId().toString()});
							params.put("ugType", new String[]{ApplicationConstants.ASSISTANT});
							params.put("qId", new String[]{domain.getId().toString()});
							List data = Query.findReport("ACTIVE_USER", params);
							String strUsername = null;
							if(data != null && !data.isEmpty()){
								Object[] obj = (Object[])data.get(0);
								strUsername = obj[1].toString();
							}
						
							Credential cr = null;
							if(strUsername != null){
								cr = Credential.findByFieldName(Credential.class, "username", strUsername, null);
							}
							
							if(cr != null){
								UserGroup assistant = UserGroup.findActive(cr, UserGroupType.findByType(ApplicationConstants.ASSISTANT, domain.getLocale()),new Date(), domain.getLocale());
								List<Reference> refs = WorkflowConfig.
										findQuestionActorsVO(q,domain.getInternalStatus(),
												assistant,1,q.getLocale());
								
								Set<QuestionDraft> ogDrafts = q.getDrafts();
								Set<QuestionDraft> drafts = new HashSet<QuestionDraft>();
									
								for(Reference ref : refs){
									
									String[] user = ref.getId().split("#");
									
									if(!user[1].equals(ApplicationConstants.MEMBER) && !user[1].equals(ApplicationConstants.DEPARTMENT) && !user[1].equals(ApplicationConstants.DEPARTMENT_DESKOFFICER) && !ref.getState().equals(ApplicationConstants.ACTOR_ACTIVE)){
										
										int refLevel = Integer.parseInt(user[2]);
										
										if(refLevel < nextUserLevel){
											boolean foundUsersDraft = false;
											if(ogDrafts != null && !ogDrafts.isEmpty()){
												for(QuestionDraft qd : ogDrafts){
													if(qd.getEditedAs().equals(user[3]) 
															&& qd.getEditedBy().equals(user[0])){
														foundUsersDraft = true;
														break;
													}
												}
												
												if(!foundUsersDraft){
													QuestionDraft qdn = Question.addDraft(q, user[0], user[3], ref.getRemark());
													drafts.add(qdn);
												}
											}
										}
									}
								}
								if(drafts != null && !drafts.isEmpty()){
									domain.setDrafts(drafts);
								}
							}
						} catch (ELSException e) {
							e.printStackTrace();
							return "redirect:workflow/myTasks/"+workflowDetails.getId()+"/process";
						}
					}
			
					performAction(domain);
					
					domain.merge();				
					
					Question question = Question.findById(Question.class, domain.getId());
					// On Group Change
					boolean isGroupChanged = false;
					boolean isMinistryChanged = false;
					boolean isSubdepartmentChanged =false;
					
					if(!domain.getGroup().equals(prevGroup)) {
						Group fromGroup = prevGroup;
						Question.onGroupChange(question, fromGroup);
						isGroupChanged = true;
					}else if(!domain.getMinistry().equals(prevMinistry)){
						Question.onMinistryChange(question, prevMinistry);
						isMinistryChanged = true;
					}else if(!domain.getSubDepartment().equals(prevSubdepartment)){
						Question.onSubdepartmentChange(question, prevSubdepartment);
						isSubdepartmentChanged = true;
					}
					
					if(isGroupChanged || isMinistryChanged || isSubdepartmentChanged) {
						//SEND NOTIFICATION FOR DEPARTMENT CHANGE
						String usergroupTypes = "";
						if(userGroupType.equals(ApplicationConstants.DEPARTMENT)
								|| userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
							usergroupTypes = "assistant,section_officer,department";
						} else {
							usergroupTypes = "assistant,clerk";
						}
						NotificationController.sendDepartmentChangeNotification(domain.getNumber().toString(), domain.getType(), domain.getHouseType(), prevSubdepartment.getName(), domain.getSubDepartment().getName(), usergroupTypes, domain.getLocale());
						
						/**** display message ****/
						model.addAttribute("type","taskcompleted");
						return "workflow/info";
					}
					else {
						String bulkEdit=request.getParameter("bulkedit");
						if(bulkEdit==null||!bulkEdit.equals("yes")){
	
							/**** Complete Task ****/
							String endflag = null;
							if(boolReanswering && 
									(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
											|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
								endflag = "continue";
							}else if(boolResendRevisedQuestionText 
									&& (workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)
											||workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.ASSISTANT))){
								endflag = "continue";
							}else if(boolSendDiscussionDateToDepartment && workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)){
								endflag = "continue";
								//Do Nothing
							}else if(boolClarificationStatus && workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)){
								endflag = "continue";
								//Do Nothing
							}else{
								if(workflowDetails.getWorkflowType().equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
									endflag = endFlagForCurrentWorkflow;
								} else {
									endflag = question.getEndFlag();
								}							
							}
	
							Map<String,String> properties=new HashMap<String, String>();
							String level="";						
							String nextuser = null;
							if(boolReanswering){
								if(workflowDetails.getWorkflowType().equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
									nextuser = actorForCurrentWorkflow;
								} else {
									nextuser = question.getActor();
								}							
							}else if(boolResendRevisedQuestionText){
								nextuser = actorForCurrentWorkflow;
							}else if(boolClarificationStatus){
								//DO Nothing
							}else{
								nextuser = request.getParameter("actor");
							}
	
							properties.put("pv_deviceId", String.valueOf(question.getId()));
							properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
							UserGroupType usergroupType = null; 
							if(nextuser != null){
								if(!nextuser.isEmpty()){
									String[] temp = nextuser.split("#");
									properties.put("pv_user", temp[0]);
									level = temp[2];
									usergroupType = UserGroupType.findByType(temp[1], locale.toString());
								}
							}
							properties.put("pv_endflag", endflag);
							
							String strReanswer = request.getParameter("reanswer");
							workflowDetails.setDepartmentAnswer(strReanswer);
	
							String strTaskId = workflowDetails.getTaskId();
							Task task = null;
							if (!boolReanswering && !boolResendRevisedQuestionText && !boolClarificationStatus && !boolSendDiscussionDateToDepartment){
								task = processService.findTaskById(strTaskId);
								processService.completeTask(task,properties);
	
								if(domain.getType() != null 
										&& domain.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
									/**** If user is section officer and if he/she amrks the end of workflow ****/
									/**** Terminate the flows of both normal and reanswer flow if any ****/
									if(workflowDetails.getAssigneeUserGroupType().
											equals(ApplicationConstants.SECTION_OFFICER)){
										if(workflowDetails.getDepartmentAnswer() != null){
	
	
											if(workflowDetails.getPreviousWorkflowDetail() != null){
												WorkflowDetails prevWorkflowDetails = 
														WorkflowDetails.findById(WorkflowDetails.class, workflowDetails.getPreviousWorkflowDetail());
	
												Map<String, String> parameters = new HashMap<String, String>();
												
												parameters.put("locale", locale.toString());
												parameters.put("assignee", workflowDetails.getAssignee());
												parameters.put("status", "PENDING");
												parameters.put("processId", prevWorkflowDetails.getProcessId());
	
												List<WorkflowDetails> pendingWorkflows = 
														WorkflowDetails.findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.DESC);
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
	
	
											List<WorkflowDetails> reanswerWorkflowsIfAny = 
													WorkflowDetails.findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.DESC);
											WorkflowDetails reanswerWorkflowIfAny = null;
	
											if(reanswerWorkflowsIfAny != null && !reanswerWorkflowsIfAny.isEmpty()){
												for(WorkflowDetails wf : reanswerWorkflowsIfAny){
													if(!wf.getProcessId().equals(workflowDetails.getProcessId()) 
															&& wf.getDepartmentAnswer() != null){
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
	
							if(endflag != null && !endflag.isEmpty()){
							
								if(endflag.equals("continue")){
									
									if (boolReanswering){
	
										ProcessDefinition processDefinition =processService.
												findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
										ProcessInstance processInstance = processService.
												createProcessInstance(processDefinition, properties);
										/**** Process Started and task created ****/
										Task reanswertask = processService.getCurrentTask(processInstance);
	
										WorkflowDetails reanswerWorkflowDetails;
										try {
											reanswerWorkflowDetails = WorkflowDetails.
													create(domain,reanswertask,usergroupType,currentDeviceTypeWorkflowType,level);
											question.setWorkflowDetailsId(reanswerWorkflowDetails.getId());
											reanswerWorkflowDetails.setDepartmentAnswer(strReanswer);
											reanswerWorkflowDetails.setPreviousWorkflowDetail(workflowDetails.getId());
	
											reanswerWorkflowDetails.merge();
	
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}				
									}else if(boolResendRevisedQuestionText){
										ProcessDefinition processDefinition =processService.
												findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
										ProcessInstance processInstance = processService.
												createProcessInstance(processDefinition, properties);
										/**** Process Started and task created ****/
										Task resendRevisedQuestionTextTask = processService.getCurrentTask(processInstance);
										WorkflowDetails pendingWorkflow = WorkflowDetails.findCurrentWorkflowDetail(question);
										WorkflowDetails resendRevisedQuestionTextWorkflowDetails;
										try {
											if(pendingWorkflow != null){
												Task prevTask = processService.findTaskById(pendingWorkflow.getTaskId());
												processService.completeTask(prevTask, properties);
												pendingWorkflow.setStatus("COMPLETED");
												pendingWorkflow.setCompletionTime(new Date());
												pendingWorkflow.merge();
											}
											resendRevisedQuestionTextWorkflowDetails = WorkflowDetails.
													create(domain,resendRevisedQuestionTextTask,usergroupType,currentDeviceTypeWorkflowType,level);
											question.setWorkflowDetailsId(resendRevisedQuestionTextWorkflowDetails.getId());
											resendRevisedQuestionTextWorkflowDetails.setPreviousWorkflowDetail(workflowDetails.getId());
											resendRevisedQuestionTextWorkflowDetails.merge();
											
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}				
									}else if(boolClarificationStatus){
										/**** Process Started and task created ****/
										List<WorkflowDetails> pendingWorkflows = WorkflowDetails.findPendingWorkflowDetails(domain, workflowDetails.getWorkflowType());
										for(WorkflowDetails wd : pendingWorkflows){
											Task prevTask = processService.findTaskById(wd.getTaskId());
											processService.completeTask(prevTask, properties);
											wd.setStatus("TIMEOUT");
											wd.setCompletionTime(new Date());
											wd.merge();
										}
									}else if(boolSendDiscussionDateToDepartment){
										ProcessDefinition processDefinition =processService.
												findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
										ProcessInstance processInstance = processService.
												createProcessInstance(processDefinition, properties);
										/**** Process Started and task created ****/
										Task sendDiscussionDateToDepartmentTask = processService.getCurrentTask(processInstance);
										WorkflowDetails pendingWorkflow = WorkflowDetails.findCurrentWorkflowDetail(question);
										WorkflowDetails sendDiscussionDateToDepartmentWorkflowDetails;
										try {
											if(pendingWorkflow != null){
												Task prevTask = processService.findTaskById(pendingWorkflow.getTaskId());
												processService.completeTask(prevTask, properties);
												pendingWorkflow.setStatus("COMPLETED");
												pendingWorkflow.setCompletionTime(new Date());
												pendingWorkflow.merge();
											}
											sendDiscussionDateToDepartmentWorkflowDetails = WorkflowDetails.
													create(domain,sendDiscussionDateToDepartmentTask,usergroupType,currentDeviceTypeWorkflowType,level);
											question.setWorkflowDetailsId(sendDiscussionDateToDepartmentWorkflowDetails.getId());
											sendDiscussionDateToDepartmentWorkflowDetails.setPreviousWorkflowDetail(workflowDetails.getId());
											sendDiscussionDateToDepartmentWorkflowDetails.merge();
											
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
									}
	
	
									if(!boolReanswering && !boolResendRevisedQuestionText && !boolClarificationStatus && !boolSendDiscussionDateToDepartment){
										ProcessInstance processInstance = processService.findProcessInstanceById(
												task.getProcessInstanceId());
										Task newtask = processService.getCurrentTask(processInstance);
										
			
										/**** Workflow Detail entry made only if its not the end of workflow ****/
										WorkflowDetails newWFDetails = 
												WorkflowDetails.create(question, newtask, usergroupType, currentDeviceTypeWorkflowType,level);
										
										/**** SEND NOTIFICATION TO DEPARTMENT USER IF DEVICE IS UNSTARRED QUESTION ****/
										if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
												&& (usergroupType.getType().equals(ApplicationConstants.DEPARTMENT) || usergroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))) {
											
											NotificationController.sendDepartmentProcessNotificationForUnstarredQuestion(domain, newWFDetails.getAssignee(), domain.getLocale());
										}
										
										/**** FOr CLarificationFromMember and Department ****/
										if(domain.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
												&& domain.getRecommendationStatus().getType().endsWith(ApplicationConstants.STATUS_PROCESSED_SENDTODEPARTMENT)){
												
												Map<String, String> parameters = new HashMap<String, String>();
												User user = User.find(domain.getPrimaryMember());
												Credential credential = user.getCredential();
												parameters.put("pv_endflag", endflag);	
												parameters.put("pv_user",credential.getUsername());
												parameters.put("pv_deviceId", String.valueOf(question.getId()));
												parameters.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
	
												ProcessDefinition processDefinition1 =processService.
														findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
												ProcessInstance processInstance1 = processService.
														createProcessInstance(processDefinition1, parameters);
												Task newMembertask = processService.getCurrentTask(processInstance1);
												WorkflowDetails.create(domain,newMembertask,currentDeviceTypeWorkflowType,level);
																
											}
										
										/**** Define the timer ****/
										if (question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
	
											if (userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) {
												if(workflowDetails.getSendBackBefore() != null){
													newWFDetails.setSendBackBefore(workflowDetails.getSendBackBefore());
												}else{
													CustomParameter cstpSendBackTimeLimitMinutes = 
															CustomParameter.findByName(CustomParameter.class,
															ApplicationConstants.DEPARTMENT_SENDBACK_TIME_LIMIT,"");
	
													if (cstpSendBackTimeLimitMinutes != null
															&& !cstpSendBackTimeLimitMinutes.getValue().isEmpty()) {
	
														int timeLimitMinutes = 
																((int)Double.parseDouble(cstpSendBackTimeLimitMinutes.getValue())) * 60;
														newWFDetails.setSendBackBefore(new Date(newWFDetails.getAssignmentTime().getTime()+ (timeLimitMinutes * 60 * 1000)));
													}
												}
											} else if (userGroupType.equals(ApplicationConstants.DEPARTMENT)
													|| userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
	
												/**** save the reanswer by department ****/
												newWFDetails.setDepartmentAnswer(strReanswer);
												newWFDetails.setSendBackBefore(workflowDetails.getSendBackBefore());
											}
										}
										newWFDetails.merge();
									}
								}
											
							}
							if(domain.getChartAnsweringDate() != null) {
								workflowDetails.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
							}
							workflowDetails.setDecisionInternalStatus(question.getInternalStatus().getName());
							workflowDetails.setDecisionRecommendStatus(question.getRecommendationStatus().getName());
							workflowDetails.setStatus("COMPLETED");
							workflowDetails.setCompletionTime(new Date());
							
							workflowDetails.merge();		
							/**** display message ****/
							model.addAttribute("type","taskcompleted");
							return "workflow/info";
						}
					}
					model.addAttribute("type","success");
					populateModel(domain, model, request, workflowDetails);
				}

			}
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
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strQuestionType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strItemsCount = request.getParameter("itemsCount");
			String strLocale = locale.toString();
			String strAnsweringDate = request.getParameter("answeringDate");
			String strGroup = request.getParameter("group");
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
			String strUserGroupType = null;
			String strUsergroup = null;
			if(userGroups != null && !userGroups.isEmpty()){

				CustomParameter customParameter = CustomParameter.
						findByName(CustomParameter.class,"QIS_ALLOWED_USERGROUPTYPES", "");
				if(customParameter != null){
					String allowedUserGroups = customParameter.getValue(); 
					for(UserGroup i : userGroups){
						UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
						if(ug != null){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								strUsergroup = String.valueOf(i.getId());
								strUserGroupType = i.getUserGroupType().getType();
								break;
							}
						}
					}
				}
			}		
			Set<Role> roles = this.getCurrentUser().getRoles();
			String strRole = null;
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					strRole = i.getType();
					break;
				}else if(i.getType().contains("QIS_CLERK")){
					strRole = i.getType();
					break;
				}else if(i.getType().startsWith("QIS_")){
					strRole = i.getType();
					break;
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
					&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
				/**** List of Statuses ****/
				if(strWorkflowSubType.equals(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER)){
					Status approveStatus = Status.
							findByType(ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale.toString());
					Status rejectStatus = Status.
							findByType(ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale.toString());
					List<Status> decisionStatus = new ArrayList<Status>();
					decisionStatus.add(approveStatus);
					decisionStatus.add(rejectStatus);
					model.addAttribute("internalStatuses", decisionStatus);
				}else{
					List<Status> internalStatuses = new ArrayList<Status>();
					HouseType houseType = HouseType.
							findByFieldName(HouseType.class,"name",strHouseType, strLocale);
					DeviceType questionType = DeviceType.
							findByFieldName(DeviceType.class,"name",strQuestionType,strLocale);
					Status internalStatus = Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority = CustomParameter.
							findByName(CustomParameter.class,questionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null 
							&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.
								findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus != null){
							internalStatuses = Status.
									findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE + INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)****/
					else if(deviceTypeHouseTypeInternalStatusUsergroup !=null ){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
					}
					/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses", internalStatuses);
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
				model.addAttribute("workflowSubType",strWorkflowSubType);
				model.addAttribute("answeringDate",strAnsweringDate);
				model.addAttribute("group", strGroup);
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
		String strInternalStatus = request.getParameter("internalstatus");
		String strWorkflowSubType = request.getParameter("workflowSubType");
		StringBuffer recommendAdmissionMsg = new StringBuffer();
		StringBuffer recommendRejectionMsg = new StringBuffer();
		StringBuffer admittedMsg = new StringBuffer();
		StringBuffer rejectedMsg = new StringBuffer();
		if(selectedItems != null && (selectedItems.length >0)
				&&strInternalStatus!=null&&!strInternalStatus.isEmpty()
				&&strWorkflowSubType!=null&&!strWorkflowSubType.isEmpty()) {
			Status status = null;
			if(!strInternalStatus.equals("-")){
				status=Status.findById(Status.class,Long.parseLong(strInternalStatus));
			}
			for(String i : selectedItems) {
				try {
					if(strWorkflowSubType.equals("request_to_supporting_member")){
						String[] temp = i.split("#");
						Long id = Long.parseLong(temp[0]);
						WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class,id);
						/**** Updating Supporting Member ****/
						SupportingMember supportingMember = 
								SupportingMember.findById(SupportingMember.class,Long.parseLong(temp[1]));
						supportingMember.setApprovalDate(new Date());
						supportingMember.setApprovedSubject(wfDetails.getSubject());
						supportingMember.setApprovedText(wfDetails.getText());	
						supportingMember.setDecisionStatus(status);
						/**** Remarks Need To Be Added ****/
						supportingMember.merge();
						/**** complete the task ****/		 
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task);
						/**** Update Workflow Details ****/
						wfDetails.setStatus("COMPLETED");
						wfDetails.setCompletionTime(new Date());
						/**** In case of Supporting Member Approval Status should reflect member's actions ****/
						wfDetails.setDecisionInternalStatus(status.getName());
						wfDetails.setDecisionRecommendStatus(status.getName());
						wfDetails.merge();					
					}else{
						Long id = Long.parseLong(i);
						WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class,id);
						Question question = 
								Question.findById(Question.class,Long.parseLong(wfDetails.getDeviceId()));
						String actor = request.getParameter("actor");
						if(actor == null || actor.isEmpty()){
							actor = question.getActor();
							String[] temp = actor.split("#");
							actor = temp[1];
						}
						String level = request.getParameter("level");
						if(level == null || level.isEmpty()){
							level = question.getLevel();
						}

						if(actor != null && !actor.isEmpty()
								&& level!=null && !level.isEmpty()){
							Reference reference = null;
							try {
								reference = UserGroup.findQuestionActor(question,actor,level,locale.toString());
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
							if(reference != null
									&& reference.getId() != null && !reference.getId().isEmpty()
									&& reference.getName() != null && !reference.getName().isEmpty()){
								/**** Update Actor ****/
								String[] temp = reference.getId().split("#");
								question.setActor(reference.getId());
								question.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
								question.setLevel(temp[2]);
								/**** Update Internal Status and Recommendation Status ****/
								if(status != null){
									if(!status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
									&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODEPARTMENT)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
									&& !status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_DISCUSS) 
									&& !status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_SENDBACK)
									&& !status.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_SENDTODEPARTMENT)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
									&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
									&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODEPARTMENT)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
									&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
									&& !status.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODEPARTMENT)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)
									&& !status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_RECIEVED)
									&& !status.getType().equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)
									&& !status.getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)){
										question.setInternalStatus(status);
										question.setRecommendationStatus(status);
										question.setEndFlag("continue");
									}else if(status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)
											||status.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_RECIEVED)){
										Status toBePutUp = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale.toString());
										question.setInternalStatus(toBePutUp);
										question.setRecommendationStatus(status);
										question.setEndFlag(null);
										question.setActor(null);
										question.setLevel(null);
										question.setLocalizedActorName(null);
									}else if(status.getType().equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)
												|| status.getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
										DeviceType unstarredDeviceType = DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, locale.toString());
										question.setType(unstarredDeviceType);
										
										Status unstarredAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale.toString());									
										question.setStatus(unstarredAdmitStatus);
										question.setInternalStatus(unstarredAdmitStatus);
										
										question.setRecommendationStatus(status);
										question.setEndFlag("continue");
									}else{
										question.setRecommendationStatus(status);
										question.setEndFlag("continue");
									}
								}

								/**** Complete Task ****/
								Map<String,String> properties = new HashMap<String, String>();
								properties.put("pv_deviceId", String.valueOf(question.getId()));
								properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
								properties.put("pv_user", temp[0]);
								properties.put("pv_endflag", question.getEndFlag());
								UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
								String strTaskId = wfDetails.getTaskId();
								Task task = processService.findTaskById(strTaskId);
								processService.completeTask(task, properties);	
								if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
										&& question.getEndFlag().equals("continue")){
									/**** Create New Workflow Details ****/
									ProcessInstance processInstance = 
											processService.findProcessInstanceById(task.getProcessInstanceId());
									Workflow workflowFromUpdatedStatus = null;
									try {
										
										/*
										 * Added by Amit Desai 2 Dec 2014
										 * START...
										 */
										/*if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
													|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_UNCLUBBING)
													|| question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
												workflowFromUpdatedStatus = Workflow.findByStatus(question.getRecommendationStatus(), question.getLocale());
											} else {
												workflowFromUpdatedStatus = Workflow.findByStatus(question.getInternalStatus(), question.getLocale());
										}*/
										Status internalStatus = question.getInternalStatus();
										String internalStatusType = internalStatus.getType();
										Status recommendationStatus = question.getRecommendationStatus();
										String recommendationStatusType = recommendationStatus.getType();

										if(recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
											workflowFromUpdatedStatus = Workflow.findByStatus(recommendationStatus, locale.toString());
										} 
										else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)
												|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)
												|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)
												|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING)
												|| internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)
												|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)
												|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)
												|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING)
												|| (internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED))
												|| (internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED)
												|| (internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED))
												|| (internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED)))) {
											workflowFromUpdatedStatus = Workflow.findByStatus(internalStatus, locale.toString());
										}
										else {
											workflowFromUpdatedStatus = Workflow.findByStatus(internalStatus, locale.toString());
										}
										/*
										 * Added by Amit Desai 2 Dec 2014
										 * ... END
										 */
										
									} catch(ELSException e) {
										e.printStackTrace();
										model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
										model.addAttribute("type", "error");
										return "workflow/info";
									}
									Task newtask = processService.getCurrentTask(processInstance);
									WorkflowDetails workflowDetails2 = null;
									try {
										workflowDetails2 = WorkflowDetails.create(question,newtask,usergroupType,workflowFromUpdatedStatus.getType(),level);
									} catch (ELSException e) {
										e.printStackTrace();
										model.addAttribute("error", e.getParameter());
									}
									question.setWorkflowDetailsId(workflowDetails2.getId());
									question.setTaskReceivedOn(new Date());								
								}
								/**** Update Old Workflow Details ****/
								wfDetails.setStatus("COMPLETED");
//								wfDetails.setInternalStatus(question.getInternalStatus().getName());
//								wfDetails.setRecommendationStatus(question.getRecommendationStatus().getName());
								wfDetails.setCompletionTime(new Date());
								if(!question.getType().getType().startsWith("questions_halfhourdiscussion_") 
								&& !question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
								&& !question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
									wfDetails.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
								}
								wfDetails.setDecisionInternalStatus(question.getInternalStatus().getName());
								wfDetails.setDecisionRecommendStatus(question.getRecommendationStatus().getName());
								/** Temporary fix for removing previous remarks till monsoon session, 2019 end **/
								if(question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {									
									question.setRemarks("");
									wfDetails.setRemarks("");
								}
								wfDetails.merge();
								/**** Update Question ****/
								question.setEditedOn(new Date());
								question.setEditedBy(this.getCurrentUser().getActualUsername());
								question.setEditedAs(wfDetails.getAssigneeUserGroupName());				
								performAction(question);								
								question.merge();
								String internalStatusType = question.getInternalStatus().getType();
								if(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMISSION)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMISSION)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMISSION)){
									recommendAdmissionMsg.append(question.formatNumber()+",");
								}else if(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECTION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECTION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECTION)){
									recommendRejectionMsg.append(question.formatNumber()+",");
								}else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)){
									admittedMsg.append(question.formatNumber()+",");
								}else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECTION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECTION)
										|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION)){
									rejectedMsg.append(question.formatNumber()+",");
								}
							}
						}					
					}
				} catch(ELSException e) {
					e.printStackTrace();
					logger.error(e.getParameter());
					logger.error("Problem in bulk update of workflow details task with ID = "+i);
					continue;
				} catch(Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
					logger.error("Problem in bulk update of workflow details task with ID = "+i);
					continue;
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
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strQuestionType = request.getParameter("deviceType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strItemsCount = request.getParameter("itemscount");
		String strWorkflowSubType = request.getParameter("workflowSubType");
		String strLocale = locale.toString();	
		String assignee = this.getCurrentUser().getActualUsername();
		String strAnsweringDate = request.getParameter("answeringDate");
		String strGroup = request.getParameter("group");
		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear !=null && !(strSessionYear.isEmpty())
				&& strQuestionType!=null && !(strQuestionType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroup != null && !(strUsergroup.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())
				&& strItemsCount!=null && !(strItemsCount.isEmpty())
				&& strWorkflowSubType != null && !(strWorkflowSubType.isEmpty())){	
			model.addAttribute("workflowSubType", strWorkflowSubType);
			Date answeringDate = null;
			if(strAnsweringDate != null && !strAnsweringDate.isEmpty()){
				 answeringDate=FormaterUtil.
						 formatStringToDate(strAnsweringDate, ApplicationConstants.DB_DATEFORMAT);
			}
			/**** Workflow Details ****/
			List<WorkflowDetails> workflowDetails = null;
			try {
				workflowDetails = WorkflowDetails.
						findAll(strHouseType, strSessionType, strSessionYear,
								strQuestionType, strStatus, strWorkflowSubType,
								assignee, strItemsCount, strLocale, null, strGroup, answeringDate);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
			NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			int counter = 0;
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
					bulkApprovalVO.setId(String.valueOf(i.getId()));
					bulkApprovalVO.setDeviceId(String.valueOf(question.getId()));				
					if(question.getNumber()!=null){
						bulkApprovalVO.setDeviceNumber(format.format(question.getNumber()));
					}else{
						bulkApprovalVO.setDeviceNumber("-");
					}
					bulkApprovalVO.setDeviceType(question.getType().getName());
					bulkApprovalVO.setMember(question.getPrimaryMember().getFullname());
					if(question.getRevisedSubject() != null && !question.getRevisedSubject().equals("")){
						bulkApprovalVO.setSubject(question.getRevisedSubject());
					}else{
						bulkApprovalVO.setSubject(question.getSubject());
					}
					if(question.getRevisedQuestionText() != null && !question.getRevisedQuestionText().isEmpty()){
						bulkApprovalVO.setBriefExpanation(question.getRevisedQuestionText());
					}else{
						bulkApprovalVO.setBriefExpanation(question.getQuestionText());
					}
					
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
				
				if(counter == 0){
					model.addAttribute("apprLevel", question.getLevel());
					counter++;
				}
			}
			model.addAttribute("bulkapprovals", bulkapprovals);
			if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
				model.addAttribute("questionId",bulkapprovals.get(0).getDeviceId());				
			}
		}
	}

	
	private void performAction(Question domain) throws ELSException {
		String deviceTypeType = domain.getType().getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			performActionOnStarredQuestion(domain);
		}else if(deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			performActionOnUnstarredQuestion(domain);			
		}else if(deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
			performActionOnShortNoticeQuestion(domain);
		}else if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
			performActionOnHDQQuestion(domain);
		}
	}
	
	private void performActionOnStarredQuestion(final Question domain) throws ELSException {
		String internalStatus = domain.getInternalStatus().getType();
		String recommendationStatus = domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
			performActionOnStarredAdmission(domain);
		}		
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)){
			performActionOnStarredRejection(domain);
		}
		/**** Convert to unstarred and admit ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)){
			performActionOnStarredConvertToUnstarredAndAdmit(domain);
		}
		/**** Convert to unstarred and admit(previous sessions) ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED)){
			// TODO
			performActionOnStarredConvertToUnstarredAndAdmitClubbed(domain);
		}
		/*** Clarification Asked  From Department***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
			performActionOnStarredClarificationNeededFromDepartment(domain);
		}
		/*** Clarification Asked From Member***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
			performActionOnStarredClarificationNeededFromMember(domain);
		}
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
			performActionOnStarredClarificationNeededFromMemberAndDepartment(domain);
		}
		
		/**** Clarification not received From Department ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnStarredClarificationNotReceived(domain);
		}
		/**** Clarification not received From Member ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER)){
			performActionOnStarredClarificationNotReceived(domain);
		}
		/**** Clarification received FROM Member ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnStarredClarificationReceived(domain);
		}
		/**** Clarification received From Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnStarredClarificationReceived(domain);
		}
		/**** Clarification received FROM Member & Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnStarredClarificationReceived(domain);
		}
		/**** Clarification received FROM Member & Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnStarredClarificationNotReceived(domain);
		}
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)){
			performActionOnStarredClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING)){
			performActionOnStarredClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)){
			performActionOnStarredNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECT_NAMECLUBBING)){
			performActionOnStarredNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnStarredClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnStarredClubbingRejectionPostAdmission(domain);
		}
		/**** Clubbing With Previous Session Unstarred is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)){
			performActionOnStarredClubbingWithUnstarredFromPreviousSession(domain);
		}
		/**** Clubbing With Previous Session Unstarred is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)){
			performActionOnStarredClubbingWithUnstarredFromPreviousSessionRejection(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)){
			performActionOnStarredUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_REJECT_UNCLUBBING)){
			performActionOnStarredUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnStarredAdmissionDueToReverseClubbing(domain);
		}
	}

	private void performActionOnStarredAdmission(final Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject() );			
		}
		
		if(domain.getRevisedQuestionText() == null 
				|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//			Status nameClubbing = Status.
//					findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
//					question.setRevisedSubject(subject);
//					question.setRevisedQuestionText(questionText);
//					question.setStatus(domain.getInternalStatus());
//					question.setInternalStatus(domain.getInternalStatus());
//					question.setRecommendationStatus(domain.getInternalStatus());
//					question.simpleMerge();
//				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(nameClubbing);
//					question.simpleMerge();
//				}
//			}
//		}
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
	}

	private void performActionOnStarredConvertToUnstarredAndAdmit(
			Question domain) throws ELSException {
		DeviceType newDeviceType = DeviceType.
				findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		DeviceType originalDeviceType=DeviceType.
				findByType(ApplicationConstants.STARRED_QUESTION,domain.getLocale());
		domain.setType(newDeviceType);
		domain.setOriginalType(originalDeviceType);
		Status finalStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		if(domain.getRevisedSubject() == null 
			|| domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null
			|| domain.getRevisedQuestionText().isEmpty()) {			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//			
//			Status nameClubbing = Status.
//					findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
//					question.setRevisedSubject(subject);
//					question.setRevisedQuestionText(questionText);
//					question.setType(newDeviceType);
//					question.setOriginalType(originalDeviceType);
//					question.setStatus(domain.getStatus());
//					question.setInternalStatus(domain.getInternalStatus());
//					question.setInternalStatus(domain.getRecommendationStatus());
//				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(nameClubbing);
//				}			
//				question.simpleMerge();
//			}
//		}
//		
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		// Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
	}
	
	private void performActionOnStarredConvertToUnstarredAndAdmitClubbed(
			Question domain) {
		DeviceType newDeviceType = DeviceType.
				findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		DeviceType originalDeviceType = DeviceType.
				findByType(ApplicationConstants.STARRED_QUESTION,domain.getLocale());
		domain.setType(newDeviceType);
		domain.setOriginalType(originalDeviceType);
		Status finalStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
		if(clubbedEntities != null){
			String subject = null;
			String questionText = null;
					
			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
				subject = domain.getRevisedSubject();				
			}else{
				subject = domain.getSubject();
			}
			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
				questionText = domain.getRevisedQuestionText();
			}else{
				questionText = domain.getQuestionText();
			}
		
			Status nameClubbing = Status.
					findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i : clubbedEntities){
				Question question = i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setType(newDeviceType);
					question.setOriginalType(originalDeviceType);
					question.setStatus(domain.getStatus());
					question.setInternalStatus(domain.getInternalStatus());
					question.setInternalStatus(domain.getRecommendationStatus());
				}else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
					question.setInternalStatus(nameClubbing);
				}			
				question.simpleMerge();
			}
		}
		
	}
	
	private void performActionOnStarredRejection(Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
			|| domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null 
			|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//						
//			Status putUpForRejection = Status.
//					findByType(ApplicationConstants.QUESTION_PUTUP_REJECTION, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//					Question question=i.getQuestion();
//					String internalStatusType = question.getInternalStatus().getType();
//					if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
//						question.setRevisedSubject(subject);
//						question.setRevisedQuestionText(questionText);
//						question.setStatus(domain.getInternalStatus());
//						question.setInternalStatus(domain.getInternalStatus());
//						question.setRecommendationStatus(domain.getInternalStatus());
//						question.simpleMerge();
//					}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//						question.setInternalStatus(putUpForRejection);
//						question.simpleMerge();
//					}
//				}
//			
//		}
//		
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		// Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
		
	}

	private void performActionOnStarredClarificationReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.
//					findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				if(question.getInternalStatus().getType().
//						equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnStarredClarificationNotReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnStarredClarificationNeededFromDepartment(Question domain) {
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
	}

	private void performActionOnStarredClarificationNeededFromMember(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}		
	}
	
	private void performActionOnStarredClarificationNeededFromMemberAndDepartment(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
	}

	private void performActionOnStarredClubbing(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnStarredClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}

	private void performActionOnStarredNameClubbing(Question domain) throws ELSException {
//		/**** status of question is changed to final admission
//		 * its revised subject,question text are updated (assumption that admitted
//		 * question has right revised texts)
//		 * its clubbing is updated ****/
//		Status finalStatus = Status.
//				findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
//		domain.setStatus(finalStatus);
//		domain.setInternalStatus(finalStatus);
//		domain.setRecommendationStatus(finalStatus);	
//
//		if(domain.getParent().getRevisedSubject()!=null 
//				&& !domain.getParent().getRevisedSubject().isEmpty()){			
//			domain.setRevisedSubject(domain.getParent().getRevisedSubject());			
//		}
//
//		if(domain.getParent().getRevisedQuestionText()!=null 
//				&& !domain.getParent().getRevisedQuestionText().isEmpty()){			
//			domain.setRevisedQuestionText(domain.getParent().getRevisedQuestionText());			
//		}
//
//		ClubbedEntity.updateClubbing(domain);
		
		Question.updateClubbing(domain);

		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			domain.setActor(null);
			domain.setLocalizedActorName("");
			domain.setWorkflowDetailsId(null);
			domain.setLevel("1");
			domain.setWorkflowStarted("NO");
			domain.setEndFlag(null);		
		}		
	}
	
	private void performActionOnStarredNameClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);	
	}

	private void performActionOnStarredClubbingPostAdmission(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnStarredClubbingRejectionPostAdmission(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnStarredClubbingWithUnstarredFromPreviousSession(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnStarredClubbingWithUnstarredFromPreviousSessionRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnStarredUnclubbing(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnStarredUnclubbingRejection(Question domain) throws ELSException {
		/** Back to clubbed state as it was before sending for unclubbing **/
		domain.setInternalStatus(domain.getParent().getInternalStatus());
		domain.setRecommendationStatus(domain.getParent().getInternalStatus());
		domain.simpleMerge();
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnStarredAdmissionDueToReverseClubbing(Question domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
		Workflow processWorkflow = Workflow.findByStatus(admitStatus, domain.getLocale());
		UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, domain.getLocale());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		WorkflowDetails.startProcessAtGivenLevel(domain, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, domain.getLocale());
	}
	
	private void performActionOnUnstarredQuestion(final Question domain) throws ELSException {
		String internalStatus = domain.getInternalStatus().getType();
		String recommendationStatus = domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
				&& (recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
					|| recommendationStatus.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT))){
			performActionOnUnstarredAdmission(domain);
		}		
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECTION)){
			performActionOnUnstarredRejection(domain);
		}
		/*** Clarification Asked  From Department***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
			performActionOnUnstarredClarificationNeededFromDepartment(domain);
		}
		/*** Clarification Asked From Member***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
			performActionOnUnstarredClarificationNeededFromMember(domain);
		}
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
			performActionOnUnstarredClarificationNeededFromMemberAndDepartment(domain);
		}
		
		/**** Clarification not received From Department ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnUnstarredClarificationNotReceived(domain);
		}
		/**** Clarification not received From Member ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnUnstarredClarificationNotReceived(domain);
		}
		/**** Clarification received FROM Member ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnUnstarredClarificationReceived(domain);
		}
		/**** Clarification received From Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnUnstarredClarificationReceived(domain);
		}
		/**** Clarification received FROM Member & Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnUnstarredClarificationReceived(domain);
		}
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)){
			performActionOnUnstarredClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING)){
			performActionOnUnstarredClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)){
			performActionOnUnstarredNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_NAMECLUBBING)){
			performActionOnUnstarredNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnUnstarredClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnUnstarredClubbingRejectionPostAdmission(domain);
		}
		/**** Clubbing With Previous Session Unstarred is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)){
			performActionOnUnstarredClubbingWithUnstarredFromPreviousSession(domain);
		}
		/**** Clubbing With Previous Session Unstarred is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)){
			performActionOnUnstarredClubbingWithUnstarredFromPreviousSessionRejection(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)){
			performActionOnUnstarredUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECT_UNCLUBBING)){
			performActionOnUnstarredUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnUnstarredAdmissionDueToReverseClubbing(domain);
		}
	}

	private void performActionOnUnstarredAdmission(final Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject() );			
		}
		
		if(domain.getRevisedQuestionText() == null 
				|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//			Status nameClubbing = Status.
//					findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
//					question.setRevisedSubject(subject);
//					question.setRevisedQuestionText(questionText);
//					question.setStatus(domain.getInternalStatus());
//					question.setInternalStatus(domain.getInternalStatus());
//					question.setRecommendationStatus(domain.getInternalStatus());
//					question.simpleMerge();
//				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(nameClubbing);
//					question.simpleMerge();
//				}
//			}
//		}
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
	}

	private void performActionOnUnstarredConvertToUnstarredAndAdmit(
			Question domain) throws ELSException {
		DeviceType newDeviceType = DeviceType.
				findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		DeviceType originalDeviceType=DeviceType.
				findByType(ApplicationConstants.STARRED_QUESTION,domain.getLocale());
		domain.setType(newDeviceType);
		domain.setOriginalType(originalDeviceType);
		Status finalStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		if(domain.getRevisedSubject() == null 
			|| domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null
			|| domain.getRevisedQuestionText().isEmpty()) {			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//			
//			Status nameClubbing = Status.
//					findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
//					question.setRevisedSubject(subject);
//					question.setRevisedQuestionText(questionText);
//					question.setType(newDeviceType);
//					question.setOriginalType(originalDeviceType);
//					question.setStatus(domain.getStatus());
//					question.setInternalStatus(domain.getInternalStatus());
//					question.setInternalStatus(domain.getRecommendationStatus());
//				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(nameClubbing);
//				}			
//				question.simpleMerge();
//			}
//		}
//		
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
	}
	
	private void performActionOnUnstarredConvertToUnstarredAndAdmitClubbed(
			Question domain) {
		DeviceType newDeviceType = DeviceType.
				findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		DeviceType originalDeviceType = DeviceType.
				findByType(ApplicationConstants.STARRED_QUESTION,domain.getLocale());
		domain.setType(newDeviceType);
		domain.setOriginalType(originalDeviceType);
		Status finalStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
		if(clubbedEntities != null){
			String subject = null;
			String questionText = null;
					
			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
				subject = domain.getRevisedSubject();				
			}else{
				subject = domain.getSubject();
			}
			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
				questionText = domain.getRevisedQuestionText();
			}else{
				questionText = domain.getQuestionText();
			}
		
			Status nameClubbing = Status.
					findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i : clubbedEntities){
				Question question = i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setType(newDeviceType);
					question.setOriginalType(originalDeviceType);
					question.setStatus(domain.getStatus());
					question.setInternalStatus(domain.getInternalStatus());
					question.setInternalStatus(domain.getRecommendationStatus());
				}else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
					question.setInternalStatus(nameClubbing);
				}			
				question.simpleMerge();
			}
		}
		
	}
	
	private void performActionOnUnstarredRejection(Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
			|| domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null 
			|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//						
//			Status putUpForRejection = Status.
//					findByType(ApplicationConstants.QUESTION_PUTUP_REJECTION, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//					Question question=i.getQuestion();
//					String internalStatusType = question.getInternalStatus().getType();
//					if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
//						question.setRevisedSubject(subject);
//						question.setRevisedQuestionText(questionText);
//						question.setStatus(domain.getInternalStatus());
//						question.setInternalStatus(domain.getInternalStatus());
//						question.setRecommendationStatus(domain.getInternalStatus());
//						question.simpleMerge();
//					}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//						question.setInternalStatus(putUpForRejection);
//						question.simpleMerge();
//					}
//				}
//			
//		}
//		
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
		
	}

	private void performActionOnUnstarredClarificationReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.
//					findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				if(question.getInternalStatus().getType().
//						equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnUnstarredClarificationNotReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnUnstarredClarificationNeededFromDepartment(Question domain) {
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
	}

	private void performActionOnUnstarredClarificationNeededFromMember(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}		
	}
	
	private void performActionOnUnstarredClarificationNeededFromMemberAndDepartment(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
	}

	private void performActionOnUnstarredClubbing(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnstarredClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}

	private void performActionOnUnstarredNameClubbing(Question domain) throws ELSException {
//		/**** status of question is changed to final admission
//		 * its revised subject,question text are updated (assumption that admitted
//		 * question has right revised texts)
//		 * its clubbing is updated ****/
//		Status finalStatus = Status.
//				findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
//		domain.setStatus(finalStatus);
//		domain.setInternalStatus(finalStatus);
//		domain.setRecommendationStatus(finalStatus);	
//
//		if(domain.getParent().getRevisedSubject()!=null 
//				&& !domain.getParent().getRevisedSubject().isEmpty()){			
//			domain.setRevisedSubject(domain.getParent().getRevisedSubject());			
//		}
//
//		if(domain.getParent().getRevisedQuestionText()!=null 
//				&& !domain.getParent().getRevisedQuestionText().isEmpty()){			
//			domain.setRevisedQuestionText(domain.getParent().getRevisedQuestionText());			
//		}
//
//		ClubbedEntity.updateClubbing(domain);
		
		Question.updateClubbing(domain);

		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			domain.setActor(null);
			domain.setLocalizedActorName("");
			domain.setWorkflowDetailsId(null);
			domain.setLevel("1");
			domain.setWorkflowStarted("NO");
			domain.setEndFlag(null);		
		}
	}
	
	private void performActionOnUnstarredNameClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);	
	}

	private void performActionOnUnstarredClubbingPostAdmission(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnstarredClubbingRejectionPostAdmission(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnstarredClubbingWithUnstarredFromPreviousSession(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnstarredClubbingWithUnstarredFromPreviousSessionRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnstarredUnclubbing(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnstarredUnclubbingRejection(Question domain) throws ELSException {
		/** Back to clubbed state as it was before sending for unclubbing **/
		domain.setInternalStatus(domain.getParent().getInternalStatus());
		domain.setRecommendationStatus(domain.getParent().getInternalStatus());
		domain.simpleMerge();
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnstarredAdmissionDueToReverseClubbing(Question domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
		Workflow processWorkflow = Workflow.findByStatus(admitStatus, domain.getLocale());
		UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, domain.getLocale());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		WorkflowDetails.startProcessAtGivenLevel(domain, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, domain.getLocale());
	}
	
	private void performActionOnShortNoticeQuestion(final Question domain) throws ELSException {
		String internalStatus = domain.getInternalStatus().getType();
		String recommendationStatus = domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)){
			performActionOnShortNoticeAdmission(domain);
		}		
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECTION)){
			performActionOnShortNoticeRejection(domain);
		}
		/**** Convert to unstarred and admit ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)){
			performActionOnShortNoticeConvertToUnstarredAndAdmit(domain);
		}
		/**** Convert to unstarred and admit(previous sessions) ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED)){
			// TODO
			performActionOnShortNoticeConvertToUnstarredAndAdmitClubbed(domain);
		}
		/*** Clarification Asked  From Department***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
			performActionOnShortNoticeClarificationNeededFromDepartment(domain);
		}
		/*** Clarification Asked From Member***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
			performActionOnShortNoticeClarificationNeededFromMember(domain);
		}
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
			performActionOnShortNoticeClarificationNeededFromMemberAndDepartment(domain);
		}
		
		/**** Clarification not received From Department ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnShortNoticeClarificationNotReceived(domain);
		}
		/**** Clarification not received From Member ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnShortNoticeClarificationNotReceived(domain);
		}
		/**** Clarification received FROM Member ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnShortNoticeClarificationReceived(domain);
		}
		/**** Clarification received From Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnShortNoticeClarificationReceived(domain);
		}
		/**** Clarification received FROM Member & Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnShortNoticeClarificationReceived(domain);
		}
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)){
			performActionOnShortNoticeClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING)){
			performActionOnShortNoticeClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)){
			performActionOnShortNoticeNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_NAMECLUBBING)){
			performActionOnShortNoticeNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnShortNoticeClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnShortNoticeClubbingRejectionPostAdmission(domain);
		}
		/**** Clubbing With Previous Session Unstarred is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)){
			performActionOnShortNoticeClubbingWithUnstarredFromPreviousSession(domain);
		}
		/**** Clubbing With Previous Session Unstarred is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION)){
			performActionOnShortNoticeClubbingWithUnstarredFromPreviousSessionRejection(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)){
			performActionOnShortNoticeUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECT_UNCLUBBING)){
			performActionOnShortNoticeUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnShortNoticeAdmissionDueToReverseClubbing(domain);
		}
	}

	private void performActionOnShortNoticeAdmission(final Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject() );			
		}
		
		if(domain.getRevisedQuestionText() == null 
				|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//			Status nameClubbing = Status.
//					findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)){
//					question.setRevisedSubject(subject);
//					question.setRevisedQuestionText(questionText);
//					question.setStatus(domain.getInternalStatus());
//					question.setInternalStatus(domain.getInternalStatus());
//					question.setRecommendationStatus(domain.getInternalStatus());
//					question.simpleMerge();
//				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(nameClubbing);
//					question.simpleMerge();
//				}
//			}
//		}
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
	}

	private void performActionOnShortNoticeConvertToUnstarredAndAdmit(
			Question domain) throws ELSException {
		DeviceType newDeviceType = DeviceType.
				findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		DeviceType originalDeviceType=DeviceType.
				findByType(ApplicationConstants.SHORT_NOTICE_QUESTION,domain.getLocale());
		domain.setType(newDeviceType);
		domain.setOriginalType(originalDeviceType);
		Status finalStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		if(domain.getRevisedSubject() == null 
			|| domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null
			|| domain.getRevisedQuestionText().isEmpty()) {			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//			
//			Status nameClubbing = Status.
//					findByType(ApplicationConstants.QUESTION_SHORTNOTICE_UNSTARRED_PUTUP_NAMECLUBBING, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)){
//					question.setRevisedSubject(subject);
//					question.setRevisedQuestionText(questionText);
//					question.setType(newDeviceType);
//					question.setOriginalType(originalDeviceType);
//					question.setStatus(domain.getStatus());
//					question.setInternalStatus(domain.getInternalStatus());
//					question.setInternalStatus(domain.getRecommendationStatus());
//				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(nameClubbing);
//				}			
//				question.simpleMerge();
//			}
//		}
//		
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
	}
	
	private void performActionOnShortNoticeConvertToUnstarredAndAdmitClubbed(
			Question domain) {
		DeviceType newDeviceType = DeviceType.
				findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		DeviceType originalDeviceType = DeviceType.
				findByType(ApplicationConstants.SHORT_NOTICE_QUESTION,domain.getLocale());
		domain.setType(newDeviceType);
		domain.setOriginalType(originalDeviceType);
		Status finalStatus = Status.
				findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
		if(clubbedEntities != null){
			String subject = null;
			String questionText = null;
					
			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
				subject = domain.getRevisedSubject();				
			}else{
				subject = domain.getSubject();
			}
			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
				questionText = domain.getRevisedQuestionText();
			}else{
				questionText = domain.getQuestionText();
			}
		
			Status nameClubbing = Status.
					findByType(ApplicationConstants.QUESTION_UNSTARRED_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i : clubbedEntities){
				Question question = i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setType(newDeviceType);
					question.setOriginalType(originalDeviceType);
					question.setStatus(domain.getStatus());
					question.setInternalStatus(domain.getInternalStatus());
					question.setInternalStatus(domain.getRecommendationStatus());
				}else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED_WITH_PENDING)){
					question.setInternalStatus(nameClubbing);
				}			
				question.simpleMerge();
			}
		}
		
	}
	
	private void performActionOnShortNoticeRejection(Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
			|| domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null 
			|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//						
//			Status putUpForRejection = Status.
//					findByType(ApplicationConstants.QUESTION_SHORTNOTICE_PUTUP_REJECTION, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//					Question question=i.getQuestion();
//					String internalStatusType = question.getInternalStatus().getType();
//					if(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED)){
//						question.setRevisedSubject(subject);
//						question.setRevisedQuestionText(questionText);
//						question.setStatus(domain.getInternalStatus());
//						question.setInternalStatus(domain.getInternalStatus());
//						question.setRecommendationStatus(domain.getInternalStatus());
//						question.simpleMerge();
//					}else if(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED_WITH_PENDING)){
//						question.setInternalStatus(putUpForRejection);
//						question.simpleMerge();
//					}
//				}
//			
//		}
//		
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
		
	}

	private void performActionOnShortNoticeClarificationReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.
//					findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				if(question.getInternalStatus().getType().
//						equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnShortNoticeClarificationNotReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnShortNoticeClarificationNeededFromDepartment(Question domain) {
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
	}

	private void performActionOnShortNoticeClarificationNeededFromMember(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}		
	}
	
	private void performActionOnShortNoticeClarificationNeededFromMemberAndDepartment(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
	}

	private void performActionOnShortNoticeClubbing(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnShortNoticeClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}

	private void performActionOnShortNoticeNameClubbing(Question domain) throws ELSException {
//		/**** status of question is changed to final admission
//		 * its revised subject,question text are updated (assumption that admitted
//		 * question has right revised texts)
//		 * its clubbing is updated ****/
//		Status finalStatus = Status.
//				findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, domain.getLocale());
//		domain.setStatus(finalStatus);
//		domain.setInternalStatus(finalStatus);
//		domain.setRecommendationStatus(finalStatus);	
//
//		if(domain.getParent().getRevisedSubject()!=null 
//				&& !domain.getParent().getRevisedSubject().isEmpty()){			
//			domain.setRevisedSubject(domain.getParent().getRevisedSubject());			
//		}
//
//		if(domain.getParent().getRevisedQuestionText()!=null 
//				&& !domain.getParent().getRevisedQuestionText().isEmpty()){			
//			domain.setRevisedQuestionText(domain.getParent().getRevisedQuestionText());			
//		}
//
//		ClubbedEntity.updateClubbing(domain);
		
		Question.updateClubbing(domain);

		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
//		domain.setEndFlag("end");
//		domain.setActor(null);
//		domain.setLocalizedActorName("");		
		
		if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			domain.setActor(null);
			domain.setLocalizedActorName("");
			domain.setWorkflowDetailsId(null);
			domain.setLevel("1");
			domain.setWorkflowStarted("NO");
			domain.setEndFlag(null);		
		}
	}
	
	private void performActionOnShortNoticeNameClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		// Since Name Clubbing has been rejected, domain's status is
		// TO_BE_PUT_UP. In order to allow the Assistant to put up the
		// question, workflow attributes need to be reset.
//		domain.setEndFlag(null);
//		domain.setLevel("0");
//		domain.setTaskReceivedOn(null);
//		domain.setWorkflowDetailsId(null);
//		domain.setWorkflowStarted("NO");
//		domain.setWorkflowStartedOn(null);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}

	private void performActionOnShortNoticeClubbingPostAdmission(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnShortNoticeClubbingRejectionPostAdmission(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnShortNoticeClubbingWithUnstarredFromPreviousSession(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnShortNoticeClubbingWithUnstarredFromPreviousSessionRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnShortNoticeUnclubbing(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnShortNoticeUnclubbingRejection(Question domain) throws ELSException {
		/** Back to clubbed state as it was before sending for unclubbing **/
		domain.setInternalStatus(domain.getParent().getInternalStatus());
		domain.setRecommendationStatus(domain.getParent().getInternalStatus());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		domain.simpleMerge();		
	}
	
	private void performActionOnShortNoticeAdmissionDueToReverseClubbing(Question domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION, domain.getLocale());
		Workflow processWorkflow = Workflow.findByStatus(admitStatus, domain.getLocale());
		UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, domain.getLocale());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		WorkflowDetails.startProcessAtGivenLevel(domain, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, domain.getLocale());
	}

	private void performActionOnHDQQuestion(final Question domain) throws ELSException {
		String internalStatus = domain.getInternalStatus().getType();
		String recommendationStatus = domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)){
			performActionOnHDQAdmission(domain);
		}		
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION)){
			performActionOnHDQRejection(domain);
		}
		/*** Clarification Asked  From Department***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
			performActionOnHDQClarificationNeededFromDepartment(domain);
		}
		/*** Clarification Asked From Member***/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
			performActionOnHDQClarificationNeededFromMember(domain);
		}
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
			performActionOnHDQClarificationNeededFromMemberAndDepartment(domain);
		}
		
		/**** Clarification not received From Department ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnHDQClarificationNotReceived(domain);
		}
		/**** Clarification not received From Member ****/
		else if(internalStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnHDQClarificationNotReceived(domain);
		}
		/**** Clarification received FROM Member ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnHDQClarificationReceived(domain);
		}
		/**** Clarification received From Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnHDQClarificationReceived(domain);
		}
		/**** Clarification received FROM Member & Department ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED)){
			performActionOnHDQClarificationReceived(domain);
		}
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING)){
			performActionOnHDQClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_CLUBBING)){
			performActionOnHDQClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING)){
			performActionOnHDQNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_NAMECLUBBING)){
			performActionOnHDQNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnHDQClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnHDQClubbingRejectionPostAdmission(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING)){
			performActionOnHDQUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_UNCLUBBING)){
			performActionOnHDQUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnHDQAdmissionDueToReverseClubbing(domain);
		}
	}

	private void performActionOnHDQAdmission(final Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
				||domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject( domain.getSubject() );			
		}
		
		if(domain.getRevisedQuestionText() == null 
				|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
		if(domain.getRevisedReason() == null 
				|| domain.getRevisedReason().isEmpty()){			
			domain.setRevisedReason(domain.getReason());			
		}
		
		if(domain.getRevisedBriefExplanation() == null 
				|| domain.getRevisedBriefExplanation().isEmpty()){			
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//			Status nameClubbing = Status.
//					findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)){
//					question.setRevisedSubject(subject);
//					question.setRevisedQuestionText(questionText);
//					question.setStatus(domain.getInternalStatus());
//					question.setInternalStatus(domain.getInternalStatus());
//					question.setRecommendationStatus(domain.getInternalStatus());
//					question.simpleMerge();
//				}else if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(nameClubbing);
//					question.simpleMerge();
//				}
//			}
//		}
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
	}

	private void performActionOnHDQRejection(Question domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if(domain.getRevisedSubject() == null 
			|| domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null 
			|| domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
		if(domain.getRevisedReason() == null 
				|| domain.getRevisedReason().isEmpty()){			
			domain.setRevisedReason(domain.getReason());			
		}
		
		if(domain.getRevisedBriefExplanation() == null 
				|| domain.getRevisedBriefExplanation().isEmpty()){			
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());			
		}
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			String subject = null;
//			String questionText = null;
//			if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
//				subject = domain.getRevisedSubject();				
//			}else{
//				subject = domain.getSubject();
//			}
//			if(domain.getRevisedQuestionText() != null && !domain.getRevisedQuestionText().isEmpty()){
//				questionText = domain.getRevisedQuestionText();
//			}else{
//				questionText = domain.getQuestionText();
//			}
//						
//			Status putUpForRejection = Status.
//					findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_REJECTION, domain.getLocale());
//			for(ClubbedEntity i:clubbedEntities){
//					Question question=i.getQuestion();
//					String internalStatusType = question.getInternalStatus().getType();
//					if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED)){
//						question.setRevisedSubject(subject);
//						question.setRevisedQuestionText(questionText);
//						question.setStatus(domain.getInternalStatus());
//						question.setInternalStatus(domain.getInternalStatus());
//						question.setRecommendationStatus(domain.getInternalStatus());
//						question.simpleMerge();
//					}else if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//						question.setInternalStatus(putUpForRejection);
//						question.simpleMerge();
//					}
//				}
//			
//		}
//		
//		if(domain.getParent() != null) {
//			ClubbedEntity.updateClubbing(domain);
//
//			// Hack (07May2014): Commenting the following line results in 
//			// OptimisticLockException.
//			domain.setVersion(domain.getVersion() + 1);
//		}
		
		
		Question question = Question.findById(Question.class, domain.getId());
		domain.setDrafts(question.getDrafts());
		domain.simpleMerge(); 
		
		// Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		Question.updateClubbing(domain);
		
	}

	private void performActionOnHDQClarificationReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.
//					findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				if(question.getInternalStatus().getType().
//						equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnHDQClarificationNotReceived(Question domain) {
		Status newStatus = Status.
				findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
//		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
//		if(clubbedEntities != null){
//			Status status = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED, domain.getLocale());
//			for(ClubbedEntity i : clubbedEntities){
//				Question question = i.getQuestion();
//				String internalStatusType = question.getInternalStatus().getType();
//				if(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
//					question.setInternalStatus(status);
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}else{
//					question.setRecommendationStatus(domain.getRecommendationStatus());
//					question.simpleMerge();
//				}
//			}
//		}
		
	}

	private void performActionOnHDQClarificationNeededFromDepartment(Question domain) {
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		if(domain.getRevisedReason() == null || domain.getRevisedReason().isEmpty()){			
			domain.setRevisedReason(domain.getReason());			
		}		
		if(domain.getRevisedBriefExplanation() == null || domain.getRevisedBriefExplanation().isEmpty()){			
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());			
		}
	}

	private void performActionOnHDQClarificationNeededFromMember(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}	
		if(domain.getRevisedReason() == null || domain.getRevisedReason().isEmpty()){			
			domain.setRevisedReason(domain.getReason());			
		}		
		if(domain.getRevisedBriefExplanation() == null || domain.getRevisedBriefExplanation().isEmpty()){			
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());			
		}
	}
	
	private void performActionOnHDQClarificationNeededFromMemberAndDepartment(Question domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		if(domain.getRevisedReason() == null || domain.getRevisedReason().isEmpty()){			
			domain.setRevisedReason(domain.getReason());			
		}		
		if(domain.getRevisedBriefExplanation() == null || domain.getRevisedBriefExplanation().isEmpty()){			
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());			
		}
	}

	private void performActionOnHDQClubbing(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnHDQClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}

	private void performActionOnHDQNameClubbing(Question domain) throws ELSException {
//		/**** status of question is changed to final admission
//		 * its revised subject,question text are updated (assumption that admitted
//		 * question has right revised texts)
//		 * its clubbing is updated ****/
//		Status finalStatus = Status.
//				findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, domain.getLocale());
//		domain.setStatus(finalStatus);
//		domain.setInternalStatus(finalStatus);
//		domain.setRecommendationStatus(finalStatus);	
//
//		if(domain.getParent().getRevisedSubject()!=null 
//				&& !domain.getParent().getRevisedSubject().isEmpty()){			
//			domain.setRevisedSubject(domain.getParent().getRevisedSubject());			
//		}
//
//		if(domain.getParent().getRevisedQuestionText()!=null 
//				&& !domain.getParent().getRevisedQuestionText().isEmpty()){			
//			domain.setRevisedQuestionText(domain.getParent().getRevisedQuestionText());			
//		}
//
//		ClubbedEntity.updateClubbing(domain);
		
		Question.updateClubbing(domain);

		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			domain.setActor(null);
			domain.setLocalizedActorName("");
			domain.setWorkflowDetailsId(null);
			domain.setLevel("1");
			domain.setWorkflowStarted("NO");
			domain.setEndFlag(null);		
		}
	}
	
	private void performActionOnHDQNameClubbingRejection(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);	
	}

	private void performActionOnHDQClubbingPostAdmission(Question domain) throws ELSException {
		
		Question.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnHDQClubbingRejectionPostAdmission(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnHDQUnclubbing(Question domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		Question.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnHDQUnclubbingRejection(Question domain) throws ELSException {
		/** Back to clubbed state as it was before sending for unclubbing **/
		domain.setInternalStatus(domain.getParent().getInternalStatus());
		domain.setRecommendationStatus(domain.getParent().getInternalStatus());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		domain.simpleMerge();
	}
	
	private void performActionOnHDQAdmissionDueToReverseClubbing(Question domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, domain.getLocale());
		Workflow processWorkflow = Workflow.findByStatus(admitStatus, domain.getLocale());
		UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, domain.getLocale());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		WorkflowDetails.startProcessAtGivenLevel(domain, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, domain.getLocale());
	}

	@SuppressWarnings("rawtypes")
	private void findLatestRemarksByUserGroup(final Question domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails)throws ELSException {
		UserGroupType userGroupType = null;
		String username = this.getCurrentUser().getUsername();
		Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
		List<UserGroup> ugroups = this.getCurrentUser().getUserGroups();
		for(UserGroup ug : ugroups){
			UserGroup usergroup = null;
			if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				usergroup = UserGroup.findActive(credential, ug.getUserGroupType(), new Date(), domain.getLocale());
			} else {
				usergroup = UserGroup.findActive(credential, ug.getUserGroupType(), domain.getSubmissionDate(), domain.getLocale());
			}			
			if(usergroup != null){
				userGroupType = usergroup.getUserGroupType();
				break;
			}
		}
		if(userGroupType == null
				|| (!userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)
				&& !userGroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
			CustomParameter customParameter = null;
			if(userGroupType!=null) {
				customParameter = CustomParameter.findByName(CustomParameter.class, "QIS_LATESTREVISION_STARTINGACTOR_"+userGroupType.getType().toUpperCase(), "");
				if(customParameter != null){
					String strUsergroupType = customParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "QIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
					if(defaultCustomParameter != null){
						String strUsergroupType = defaultCustomParameter.getValue();
						userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
					}
				}
			} else {
				CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "QIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
				if(defaultCustomParameter != null){
					String strUsergroupType = defaultCustomParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}
			}			
		}
		Map<String, String[]> requestMap=new HashMap<String, String[]>();			
		requestMap.put("questionId",new String[]{String.valueOf(domain.getId())});
		requestMap.put("locale",new String[]{domain.getLocale()});
		if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)
				|| userGroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			List result=Query.findReport("QIS_LATEST_REVISION_FOR_DESKOFFICER", requestMap);
			model.addAttribute("latestRevisions",result);
		}else{
			List result=Query.findReport("QIS_LATEST_REVISIONS", requestMap);
			model.addAttribute("latestRevisions",result);
		}
		model.addAttribute("startingActor", userGroupType.getName());
	}	
	
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/advancedbulkapproval",method=RequestMethod.GET)
	public String getAdvancedBulkApproval(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		try{
			/**** Request Params ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strQuestionType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strLocale = locale.toString();
			String strAnsweringDate = request.getParameter("answeringDate");
			String strGroup = request.getParameter("group");
			String strSubDepartment = request.getParameter("subdepartment");
			String assignee = this.getCurrentUser().getActualUsername();
			String strItemsCount = request.getParameter("itemsCount");
			if(strItemsCount==null || strItemsCount.isEmpty()) {
				CustomParameter itemsCountParameter = CustomParameter.findByName(CustomParameter.class, "ADVANCED_BULKAPPROVAL_ITEM_COUNT", "");
				if(itemsCountParameter != null){
					strItemsCount = itemsCountParameter.getValue();
				}
			}			
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
			String strUserGroupType = null;
			String strUsergroup = null;
			UserGroup usergroup = null;
			if(userGroups != null && !userGroups.isEmpty()){
				CustomParameter customParameter = CustomParameter.
						findByName(CustomParameter.class,"QIS_ALLOWED_USERGROUPTYPES", "");
				if(customParameter != null){
					String allowedUserGroups = customParameter.getValue(); 
					for(UserGroup i : userGroups){
						UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
						if(ug != null){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								strUsergroup = String.valueOf(i.getId());
								strUserGroupType = i.getUserGroupType().getType();
								usergroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUsergroup));
								break;
							}
						}
					}
				}
			}	
			
			if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
	        }else{
	        	model.addAttribute("type",request.getSession().getAttribute("type"));
	            request.getSession().removeAttribute("type");
	        }
			
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strQuestionType!=null&&!(strQuestionType.isEmpty())){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(customParameter!=null){
					String server=customParameter.getValue();
					if(server.equals("TOMCAT")){
						try {
							strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
							strQuestionType = new String(strQuestionType.getBytes("ISO-8859-1"),"UTF-8");
							strGroup = new String(strGroup.getBytes("ISO-8859-1"),"UTF-8");
							if(strSubDepartment != null && !strSubDepartment.isEmpty()){
								strSubDepartment = new String(strSubDepartment.getBytes("ISO-8859-1"),"UTF-8");
							}
						}
						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				strHouseType = request.getSession().getAttribute("houseType").toString();
				strSessionType = request.getSession().getAttribute("sessionType").toString();
				strSessionYear = request.getSession().getAttribute("sessionYear").toString();
				strQuestionType = request.getSession().getAttribute("deviceType").toString();
				strWorkflowSubType = request.getSession().getAttribute("workflowSubType").toString();
				strStatus = request.getSession().getAttribute("status").toString();
				if(request.getSession().getAttribute("subdepartment") != null){
					strSubDepartment = request.getSession().getAttribute("subdepartment").toString();
				}
				
				if(request.getSession().getAttribute("answeringDate") != null){
					strAnsweringDate = request.getSession().getAttribute("answeringDate").toString();
				}
			}
	
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strQuestionType!=null&&!(strQuestionType.isEmpty())
					&&strStatus!=null&&!(strStatus.isEmpty())
					&&strUsergroup!=null&&!(strUsergroup.isEmpty())
					&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
					&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){
					
					model.addAttribute("status", strStatus);
					model.addAttribute("usergroup", usergroup.getId());
					// Populate Roles
					/**
					 * Rules:
					 * a. QIS roles starts with QIS_, MEMBER_
					 * b. Any user will have single role per device type
					 * c. Any user can have multiple roles limited to one role per device type
					 */
					Set<Role> roles = this.getCurrentUser().getRoles();
					for(Role i : roles) {
						if(i.getType().startsWith("MEMBER_")) {
							model.addAttribute("role", i.getType());
							break;
						}
						else if(i.getType().startsWith("QIS_")) {
							model.addAttribute("role", i.getType());
							break;
						}
					}
					/**** List of Statuses ****/
					List<Status> internalStatuses = new ArrayList<Status>();
					HouseType houseType = HouseType.
							findByFieldName(HouseType.class,"name",strHouseType, strLocale);
					DeviceType questionType = DeviceType.
							findByFieldName(DeviceType.class,"name",strQuestionType,strLocale);
					Status internalStatus = Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority = CustomParameter.
							findByName(CustomParameter.class,questionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null 
							&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.
								findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus != null){
							internalStatuses = Status.
									findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE + INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)****/
					else if(deviceTypeHouseTypeInternalStatusUsergroup !=null ){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
					}
					/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses", internalStatuses);				
					model.addAttribute("workflowSubType", strWorkflowSubType);
					Date answeringDate = null;
					if(strAnsweringDate != null && !strAnsweringDate.isEmpty()){
						 answeringDate=FormaterUtil.
								 formatStringToDate(strAnsweringDate, ApplicationConstants.DB_DATEFORMAT);
						 model.addAttribute("answeringDate", strAnsweringDate);
					}

					/**** Workflow Details ****/
					List<WorkflowDetails> workflowDetails = WorkflowDetails.
								findAll(strHouseType, strSessionType, strSessionYear,
										strQuestionType, strStatus, strWorkflowSubType,
										assignee, strItemsCount, strLocale, null, strGroup, strSubDepartment, answeringDate);
					/**** Populating Bulk Approval VOs ****/
					List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
					NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
					int counter = 0;
					for(WorkflowDetails i:workflowDetails){
						BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
						Question question=Question.findById(Question.class,Long.parseLong(i.getDeviceId()));
						{
							bulkApprovalVO.setId(String.valueOf(i.getId()));
							bulkApprovalVO.setDeviceId(String.valueOf(question.getId()));	
							
							Map<String, String[]> parameters = new HashMap<String, String[]>();
							parameters.put("locale", new String[]{locale.toString()});
							parameters.put("questionId", new String[]{question.getId().toString()});
							List clubbedNumbers = org.mkcl.els.domain.Query.findReport("QIS_GET_CLUBBEDNUMBERS", parameters);
							if(clubbedNumbers != null && !clubbedNumbers.isEmpty() && clubbedNumbers.get(0) != null){
								bulkApprovalVO.setFormattedClubbedNumbers(clubbedNumbers.get(0).toString());
							}
							
							List referencedNumbers = org.mkcl.els.domain.Query.findReport("QIS_GET_REFERENCEDNUMBERS", parameters);
							if(referencedNumbers != null && !referencedNumbers.isEmpty() && referencedNumbers.get(0) != null){
								bulkApprovalVO.setFormattedReferencedNumbers(referencedNumbers.get(0).toString());
							}
							
							if(question.getNumber()!=null){
								bulkApprovalVO.setDeviceNumber(format.format(question.getNumber()));
							}else{
								bulkApprovalVO.setDeviceNumber("-");
							}
							bulkApprovalVO.setDeviceType(question.getType().getName());
							bulkApprovalVO.setMember(question.getPrimaryMember().getFullname());
							if(question.getRevisedSubject() != null && !question.getRevisedSubject().equals("")){
								bulkApprovalVO.setSubject(question.getRevisedSubject());
							}else{
								bulkApprovalVO.setSubject(question.getSubject());
							}
							if(question.getRevisedQuestionText() != null && !question.getRevisedQuestionText().isEmpty()){
								bulkApprovalVO.setBriefExpanation(question.getRevisedQuestionText());
							}else{
								bulkApprovalVO.setBriefExpanation(question.getQuestionText());
							}
							
							if(question.getRemarks()!=null&&!question.getRemarks().isEmpty()){
								bulkApprovalVO.setLastRemark(question.getRemarks());
							}else{
								bulkApprovalVO.setLastRemark("-");
							}
							bulkApprovalVO.setLastDecision(question.getInternalStatus().getName());
							bulkApprovalVO.setLastRemarkBy(question.getEditedAs());	
							bulkApprovalVO.setCurrentStatus(i.getStatus());
							bulkapprovals.add(bulkApprovalVO);
							
						
						if(counter == 0){
							model.addAttribute("level", question.getLevel());
							counter++;
						}
					}
					
					model.addAttribute("bulkapprovals", bulkapprovals);
					if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
						model.addAttribute("questionId",bulkapprovals.get(0).getDeviceId());				
					}
				}
					model.addAttribute("deviceType", questionType.getId());
			}
			if(request.getSession().getAttribute("recommendAdmission")==null){
	            model.addAttribute("recommendAdmission","");
	        }else{
	        	model.addAttribute("recommendAdmission",request.getSession().getAttribute("recommendAdmission"));
	            request.getSession().removeAttribute("recommendAdmission");
	        }
			if(request.getSession().getAttribute("recommendClarification")==null){
	            model.addAttribute("recommendClarification","");
	        }else{
	        	model.addAttribute("recommendClarification",request.getSession().getAttribute("recommendClarification"));
	            request.getSession().removeAttribute("recommendClarification");
	        }
			if(request.getSession().getAttribute("recommendRejection")==null){
	            model.addAttribute("recommendRejection","");
	        }else{
	        	model.addAttribute("recommendRejection",request.getSession().getAttribute("recommendRejection"));
	            request.getSession().removeAttribute("recommendRejection");
	        }
			if(request.getSession().getAttribute("admitted")==null){
	            model.addAttribute("admitted","");
	        }else{
	        	model.addAttribute("admitted",request.getSession().getAttribute("admitted"));
	            request.getSession().removeAttribute("admitted");
	        }
			if(request.getSession().getAttribute("rejected")==null){
	            model.addAttribute("rejected","");
	        }else{
	        	model.addAttribute("rejected",request.getSession().getAttribute("rejected"));
	            request.getSession().removeAttribute("rejected");
	        }
			if(request.getSession().getAttribute("clarification")==null){
	            model.addAttribute("clarification","");
	        }else{
	        	model.addAttribute("clarification",request.getSession().getAttribute("clarification"));
	            request.getSession().removeAttribute("clarification");
	        }
			if(request.getSession().getAttribute("errorMsg")==null){
	            model.addAttribute("errorMsg","");
	        }else{
	        	model.addAttribute("errorMsg",request.getSession().getAttribute("errorMsg"));
	            request.getSession().removeAttribute("errorMsg");
	        }
			return "workflow/question/advancedbulkapproval";	
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
	
	@RequestMapping(value="/advancedbulkapproval",method=RequestMethod.POST)
	public String advancedBulkApproval(final HttpServletRequest request,
			final Locale locale,
			final RedirectAttributes redirectAttributes,
			final ModelMap model){
		String listSize = request.getParameter("questionlistSize");
		Question tempQuestion  = null;
		StringBuffer recommendAdmissionMsg = new StringBuffer();
		StringBuffer recommendRejectionMsg = new StringBuffer();
		StringBuffer recommendClarificationMsg = new StringBuffer();
		StringBuffer admittedMsg = new StringBuffer();
		StringBuffer rejectedMsg = new StringBuffer();
		StringBuffer clarificationMsg = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		if(listSize != null && !listSize.isEmpty()){
			for(int i =0; i<Integer.parseInt(listSize);i++){
				try{
					String id = request.getParameter("questionId"+i);
					String subject = request.getParameter("subject"+i);
					String questionText = request.getParameter("questionText"+i);
					String actor = request.getParameter("actor"+i);
					String internalStatus = request.getParameter("internalStatus"+i);
					String remark = request.getParameter("remark"+i);
					String workflowDetailsId = request.getParameter("workflowDetailsId"+i);
					Long wrkflowId = Long.parseLong(workflowDetailsId);
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class,wrkflowId);
					String strChecked = request.getParameter("chk"+workflowDetailsId);
					if(strChecked != null && !strChecked.isEmpty() && Boolean.parseBoolean(strChecked)){
						Question question = Question.findById(Question.class,Long.parseLong(wfDetails.getDeviceId()));
						tempQuestion = question;
						if(questionText != null && !questionText.isEmpty()){
							question.setRevisedQuestionText(questionText);
						}
						if(remark != null && !remark.isEmpty()){
							question.setRemarks(remark);
						}
						if(subject != null && !subject.isEmpty()){
							question.setRevisedSubject(subject);
						}
						if(actor == null || actor.isEmpty()){
							actor = question.getActor();
							String[] temp = actor.split("#");
							actor = temp[1];
						}
						String level = request.getParameter("questionLevel");
						if(level == null || level.isEmpty()){
							level = question.getLevel();
						}

						/**** Update Actor ****/
						String[] temp = actor.split("#");
						question.setActor(actor);
						question.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						question.setLevel(temp[2]);
						/**** Update Internal Status and Recommendation Status ****/
						Status intStatus = Status.findById(Status.class, Long.parseLong(internalStatus));
						if(internalStatus != null){
							if(!intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODEPARTMENT)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_DISCUSS) 
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_SENDBACK)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_SENDTODEPARTMENT)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODEPARTMENT)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODEPARTMENT)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SENDTOSECTIONOFFICER)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)
							&& !intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_RECIEVED)){
								question.setInternalStatus(intStatus);
								question.setRecommendationStatus(intStatus);
								question.setEndFlag("continue");
							}else if(intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)
									||intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_RECIEVED)){
								Status toBePutUp = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale.toString());
								question.setInternalStatus(toBePutUp);
								question.setRecommendationStatus(toBePutUp);
								question.setEndFlag(null);
								question.setActor(null);
								question.setLevel(null);
								question.setLocalizedActorName(null);
							}else{
								question.setRecommendationStatus(intStatus);
								question.setEndFlag("continue");
							}
						}

						/**** Complete Task ****/
						Map<String,String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId", String.valueOf(question.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
						properties.put("pv_user", temp[0]);
						properties.put("pv_endflag", question.getEndFlag());
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task, properties);	
						if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
								&& question.getEndFlag().equals("continue")){
							/**** Create New Workflow Details ****/
							ProcessInstance processInstance = 
									processService.findProcessInstanceById(task.getProcessInstanceId());
							Workflow workflowFromUpdatedStatus = null;
							Status iStatus = question.getInternalStatus();
							String internalStatusType = iStatus.getType();
							Status recommendationStatus = question.getRecommendationStatus();
							String recommendationStatusType = recommendationStatus.getType();

							if(recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
									|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
								workflowFromUpdatedStatus = Workflow.findByStatus(recommendationStatus, locale.toString());
							} 
							else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)
									|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING)
									|| (internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED))
									|| (internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED)
									|| (internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED))
									|| (internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
									||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED)))) {
								workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
							}
							else {
								workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
							}
							Task newtask = processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = null;
							try {
								workflowDetails2 = WorkflowDetails.create(question,newtask,usergroupType,workflowFromUpdatedStatus.getType(),level);
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
						if(!question.getType().getType().startsWith("questions_halfhourdiscussion_") 
						&& !question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						&& !question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
							wfDetails.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
						}
						wfDetails.setDecisionInternalStatus(question.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(question.getRecommendationStatus().getName());
						wfDetails.merge();
						/**** Update Motion ****/
						question.setEditedOn(new Date());
						question.setEditedBy(this.getCurrentUser().getActualUsername());
						question.setEditedAs(wfDetails.getAssigneeUserGroupName());				
						performAction(question);
						question.merge();
						String internalStatusType = question.getInternalStatus().getType();
						if(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION)
							|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_ADMISSION)
							|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_ADMISSION)
							|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMISSION)){
							recommendAdmissionMsg.append(question.formatNumber()+",");
						}else if(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECTION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECTION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECTION)){
							recommendRejectionMsg.append(question.formatNumber()+",");
						}else if(internalStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
							recommendClarificationMsg.append(question.formatNumber()+",");
						}else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION)){
							admittedMsg.append(question.formatNumber()+",");
						}else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECTION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECTION)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION)){
							rejectedMsg.append(question.formatNumber()+",");
						}else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
							clarificationMsg.append(question.formatNumber()+",");
						}
						redirectAttributes.addFlashAttribute("type", "success");
				        //this is done so as to remove the bug due to which update message appears even though there
				        //is a fresh new/edit request i.e after creating/updating records if we click on
				        //new /edit then success message appears
				        request.getSession().setAttribute("type","success");
				        redirectAttributes.addFlashAttribute("msg", "create_success");
					}
				} catch(ELSException ee) {
					ee.printStackTrace();
					errorMsg.append(tempQuestion.formatNumber()+",");
					continue;
				} catch(Exception e) {
					e.printStackTrace();
					errorMsg.append(tempQuestion.formatNumber()+",");
					continue;
				}
			}
			if(tempQuestion != null){
				request.getSession().setAttribute("houseType", tempQuestion.getHouseType().getName());
				request.getSession().setAttribute("sessionType", tempQuestion.getSession().getType().getSessionType());
				request.getSession().setAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(tempQuestion.getSession().getYear(), locale.toString()));
				request.getSession().setAttribute("deviceType", tempQuestion.getType().getName());
				request.getSession().setAttribute("workflowSubType", tempQuestion.getInternalStatus().getType());
				
			}
			String group = request.getParameter("group");
			if(group != null && !group.isEmpty()){
				request.getSession().setAttribute("strGroup", group);
			}
			String answeringDate = request.getParameter("answeringDate");
			if(answeringDate != null && !answeringDate.isEmpty()){
				request.getSession().setAttribute("strAnsweringDate", answeringDate);
			}
			String status = request.getParameter("status");
			if(status != null && !status.isEmpty()){
				request.getSession().setAttribute("status", status);
			}
			
		}
		request.getSession().setAttribute("recommendAdmission", recommendAdmissionMsg.toString());
		request.getSession().setAttribute("recommendClarification", recommendClarificationMsg.toString());
		request.getSession().setAttribute("recommendRejection", recommendRejectionMsg.toString());
		request.getSession().setAttribute("admitted", admittedMsg.toString());
		request.getSession().setAttribute("rejected", rejectedMsg.toString());
		request.getSession().setAttribute("clarification", clarificationMsg.toString());
		request.getSession().setAttribute("errorMsg", errorMsg.toString());
        String returnUrl = "redirect:/workflow/question/advancedbulkapproval";
        return returnUrl;
//		getAdvancedBulkApproval(request, locale, model);
	}
	
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/supplementquestionworkflow",method=RequestMethod.GET)
	public String getSupplementaryQuestions(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		try{
			/**** Request Params ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strQuestionType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strLocale = locale.toString();
			String assignee = this.getCurrentUser().getActualUsername();
			String strItemsCount = null;
			CustomParameter itemsCountParameter = CustomParameter.findByName(CustomParameter.class, "ADVANCED_BULKAPPROVAL_ITEM_COUNT", "");
			if(itemsCountParameter != null){
				strItemsCount = itemsCountParameter.getValue();
			}
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
			String strUserGroupType = null;
			String strUsergroup = null;
			UserGroup usergroup = null;
			if(userGroups != null && !userGroups.isEmpty()){
				CustomParameter customParameter = CustomParameter.
						findByName(CustomParameter.class,"QIS_ALLOWED_USERGROUPTYPES", "");
				if(customParameter != null){
					String allowedUserGroups = customParameter.getValue(); 
					for(UserGroup i : userGroups){
						UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
						if(ug != null){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								strUsergroup = String.valueOf(i.getId());
								strUserGroupType = i.getUserGroupType().getType();
								usergroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUsergroup));
								break;
							}
						}
					}
				}
			}	
			
			if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
	        }else{
	        	model.addAttribute("type",request.getSession().getAttribute("type"));
	            request.getSession().removeAttribute("type");
	        }
			
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strQuestionType!=null&&!(strQuestionType.isEmpty())){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(customParameter!=null){
					String server=customParameter.getValue();
					if(server.equals("TOMCAT")){
						try {
							strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
							strQuestionType = new String(strQuestionType.getBytes("ISO-8859-1"),"UTF-8");
						}
						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				strHouseType = request.getSession().getAttribute("houseType").toString();
				strSessionType = request.getSession().getAttribute("sessionType").toString();
				strSessionYear = request.getSession().getAttribute("sessionYear").toString();
				strQuestionType = request.getSession().getAttribute("deviceType").toString();
				strWorkflowSubType = request.getSession().getAttribute("workflowSubType").toString();
				strStatus = request.getSession().getAttribute("status").toString();
			}
	
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strQuestionType!=null&&!(strQuestionType.isEmpty())
					&&strUsergroup!=null&&!(strUsergroup.isEmpty())
					&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
					&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){
					
					model.addAttribute("status", strStatus);
					model.addAttribute("usergroup", usergroup.getId());
					// Populate Roles
					/**
					 * Rules:
					 * a. QIS roles starts with QIS_, MEMBER_
					 * b. Any user will have single role per device type
					 * c. Any user can have multiple roles limited to one role per device type
					 */
					Set<Role> roles = this.getCurrentUser().getRoles();
					for(Role i : roles) {
						if(i.getType().startsWith("MEMBER_")) {
							model.addAttribute("role", i.getType());
							break;
						}
						else if(i.getType().startsWith("QIS_")) {
							model.addAttribute("role", i.getType());
							break;
						}
					}
					/**** List of Statuses ****/
					List<Status> internalStatuses = new ArrayList<Status>();
					HouseType houseType = HouseType.
							findByFieldName(HouseType.class,"name",strHouseType, strLocale);
					DeviceType questionType = DeviceType.
							findByFieldName(DeviceType.class,"name",strQuestionType,strLocale);
					
					CustomParameter finalApprovingAuthority = CustomParameter.
							findByName(CustomParameter.class,questionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+ strWorkflowSubType.toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+ houseType.getType().toUpperCase()+"_"+strWorkflowSubType.toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.
							findByName(CustomParameter.class, "QUESTION_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null 
							&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.
								findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus != null){
							internalStatuses = Status.
									findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE + INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)****/
					else if(deviceTypeHouseTypeInternalStatusUsergroup !=null ){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
					}
					/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** QUESTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup != null){
						internalStatuses = Status.
								findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses", internalStatuses);				
					model.addAttribute("workflowSubType", strWorkflowSubType);
					/**** Workflow Details ****/
					List<WorkflowDetails> workflowDetails = WorkflowDetails.
								findAllSupplementaryWorkflow(strHouseType, strSessionType, strSessionYear,
										strQuestionType, strStatus, strWorkflowSubType,
										assignee, strItemsCount, strLocale);
					/**** Populating Bulk Approval VOs ****/
					List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
					int counter = 0;
					for(int i=0; i<workflowDetails.size(); i++){
						WorkflowDetails workflowDetail = workflowDetails.get(i);
						BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
						Question question=Question.findById(Question.class,Long.parseLong(workflowDetail.getDeviceId()));
						Question parent = question.getParent();
						
						//Device Id
						StringBuffer deviceIds = new StringBuffer();
						deviceIds.append(question.getId());
						//Device Number
						StringBuffer deviceNumber = new StringBuffer();
						deviceNumber.append(workflowDetail.getDeviceNumber());
						//Workflow Details 
						StringBuffer workflowDeviceIds = new StringBuffer();
						workflowDeviceIds.append(workflowDetail.getId());
					
						bulkApprovalVO.setFormattedParentNumber(FormaterUtil.formatNumberNoGrouping(parent.getNumber(), locale.toString()));
						bulkApprovalVO.setDeviceType(question.getType().getName());
						bulkApprovalVO.setParentId(parent.getId().toString());
						if(question.getRevisedSubject() != null && !question.getRevisedSubject().equals("")){
							bulkApprovalVO.setSubject(parent.getRevisedQuestionText());
						}else{
							bulkApprovalVO.setSubject(parent.getQuestionText());
						}

						bulkApprovalVO.setLastDecision(question.getRecommendationStatus().getName());
						bulkApprovalVO.setLastRemarkBy(question.getEditedAs());	
						bulkApprovalVO.setCurrentStatus(workflowDetail.getStatus());
						for(int j=i+1;j<workflowDetails.size();j++){
							WorkflowDetails nextWorkflowDetail = workflowDetails.get(j);
							Question nextQuestion = Question.findById(Question.class, Long.parseLong(nextWorkflowDetail.getDeviceId()));
							Question nextParent = nextQuestion.getParent();
							if(nextParent.equals(parent)){
								deviceIds.append("," + nextQuestion.getId());
								deviceNumber.append("," + nextWorkflowDetail.getDeviceNumber());
								workflowDeviceIds.append("," + nextWorkflowDetail.getId());
								i=j;
							}else{
								break;
							}
							
						}
						bulkApprovalVO.setDeviceNumber(deviceNumber.toString());
						bulkApprovalVO.setDeviceId(deviceIds.toString());
						bulkApprovalVO.setId(workflowDeviceIds.toString());
						bulkapprovals.add(bulkApprovalVO);
						if(counter == 0){
							model.addAttribute("level", question.getLevel());
							counter++;
						}
					}
					model.addAttribute("deviceType", questionType.getId());
					model.addAttribute("bulkapprovals", bulkapprovals);
					if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
						model.addAttribute("questionId",bulkapprovals.get(0).getDeviceId());				
					}
			}
			return "workflow/question/supplement/approval";	
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
	
	
	
	@RequestMapping(value="/supplementquestionworkflow",method=RequestMethod.POST)
	public String postSupplementaryQuestions(final HttpServletRequest request,
			final Locale locale,
			final RedirectAttributes redirectAttributes,
			final ModelMap model){
		String listSize = request.getParameter("questionlistSize");
		Question tempQuestion  = null;
		if(listSize != null && !listSize.isEmpty()){
			for(int i =0; i<Integer.parseInt(listSize);i++){
				String actor = request.getParameter("actor"+i);
				String internalStatus = request.getParameter("internalStatus"+i);
				String remark = request.getParameter("remark"+i);
				String workflowDetailsId = request.getParameter("workflowDetailsId"+i);
				String[] workflowDetailsIds = workflowDetailsId.split(",");
				String strChecked = request.getParameter("chk"+workflowDetailsId);
				for(int j = 0; j<workflowDetailsIds.length; j++){
					Long wrkflowId = Long.parseLong(workflowDetailsIds[j]);
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class,wrkflowId);
					if(strChecked != null && !strChecked.isEmpty() && Boolean.parseBoolean(strChecked)){
						Question question = Question.findById(Question.class,Long.parseLong(wfDetails.getDeviceId()));
						tempQuestion = question;
						if(remark != null && !remark.isEmpty()){
							question.setRemarks(remark);
						}
						if(actor == null || actor.isEmpty()){
							actor = question.getActor();
						}
						String level = request.getParameter("questionLevel");
						if(level == null || level.isEmpty()){
							level = question.getLevel();
						}

						/**** Update Actor ****/
						String[] temp = actor.split("#");
						question.setActor(actor);
						question.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						question.setLevel(temp[2]);
						/**** Update Internal Status and Recommendation Status ****/
						Status intStatus = Status.findById(Status.class, Long.parseLong(internalStatus));
						if(internalStatus != null){
							if(intStatus.getType().equals(ApplicationConstants.QUESTION_PROCESSED_SUPPLEMENTARYCLUBBINGRECEIVED)){
								Status supplementaryClubbingReceived = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_SUPPLEMENTARYCLUBBINGRECEIVED, locale.toString());
								question.setRecommendationStatus(supplementaryClubbingReceived);
								question.setEndFlag("end");
								question.setLevel("1");
								question.setTaskReceivedOn(null);
								question.setWorkflowDetailsId(null);
								question.setWorkflowStarted("NO");
								question.setWorkflowStartedOn(null);
								question.setActor(null);
								question.setLocalizedActorName("");	
								
							}else{
								question.setRecommendationStatus(intStatus);
								question.setEndFlag("continue");
							}
						}

						/**** Complete Task ****/
						Map<String,String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId", String.valueOf(question.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
						properties.put("pv_user", temp[0]);
						properties.put("pv_endflag", question.getEndFlag());
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task, properties);	
						if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
								&& question.getEndFlag().equals("continue")){
							/**** Create New Workflow Details ****/
							ProcessInstance processInstance = 
									processService.findProcessInstanceById(task.getProcessInstanceId());
							Workflow workflow = Workflow.findByType(ApplicationConstants.QUESTION_SUPPLEMENTARY_WORKFLOW, locale.toString());
							Task newtask = processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = null;
							try {
								workflowDetails2 = WorkflowDetails.create(question,newtask,usergroupType,workflow.getType(),level);
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
						if(!question.getType().getType().startsWith("questions_halfhourdiscussion_") 
						&& !question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						&& !question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
							wfDetails.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
						}
						wfDetails.setDecisionInternalStatus(question.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(question.getRecommendationStatus().getName());
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
					}
				}
			}
		}
		
		if(tempQuestion != null){
			request.getSession().setAttribute("houseType", tempQuestion.getHouseType().getName());
			request.getSession().setAttribute("sessionType", tempQuestion.getSession().getType().getSessionType());
			request.getSession().setAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(tempQuestion.getSession().getYear(), locale.toString()));
			request.getSession().setAttribute("deviceType", tempQuestion.getType().getName());
			request.getSession().setAttribute("workflowSubType", tempQuestion.getInternalStatus().getType());
			
		}
		String group = request.getParameter("group");
		if(group != null && !group.isEmpty()){
			request.getSession().setAttribute("strGroup", group);
		}
		String answeringDate = request.getParameter("answeringDate");
		if(answeringDate != null && !answeringDate.isEmpty()){
			request.getSession().setAttribute("strAnsweringDate", answeringDate);
		}
		String status = request.getParameter("status");
		if(status != null && !status.isEmpty()){
			request.getSession().setAttribute("status", status);
		}
		redirectAttributes.addFlashAttribute("type", "success");
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/workflow/question/supplementquestionworkflow";
        return returnUrl;
//		getAdvancedBulkApproval(request, locale, model);
	}
	
}