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
import org.mkcl.els.controller.QuestionReportController;
import org.mkcl.els.controller.mois.StandaloneController;
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
import org.mkcl.els.domain.ReferenceUnit;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.StandaloneMotionDraft;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.service.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


/**
 * The Class StandaloneMotionWorkflowController.
 *
 * @author vikasg
 * @since v1.0.0
 */
@Controller
@RequestMapping("/workflow/standalonemotion")
public class StandaloneMotionWorkflowController  extends BaseController{

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
		StandaloneMotion question=StandaloneMotion.findById(StandaloneMotion .class,Long.parseLong(questionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers=question.getSupportingMembers();
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					i.setApprovedText(question.getReason());
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
						SimpleDateFormat format = new SimpleDateFormat(customParameter.getValue());
						model.addAttribute("requestReceivedOnDate", format.format(i.getRequestReceivedOn()));
					}
					break;
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, question, supportingMembers,locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task", workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId", workflowDetails.getId());
		model.addAttribute("status", workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,final StandaloneMotion question, final List<SupportingMember> supportingMembers,final String locale){
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
		binder.registerCustomEditor(StandaloneMotion.class, new BaseEditor(new StandaloneMotion()));

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
			StandaloneMotion domain=StandaloneMotion.findById(StandaloneMotion.class,Long.parseLong(workflowDetails.getDeviceId()));

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

	private void populateModel(final StandaloneMotion domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) throws ELSException {

		
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

		/**** Ministries ****/
		List<Ministry> ministries = null;
		CustomParameter csptDeviceTypesHavingGroup = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_GROUPS+"_"+houseType.getType(), locale);
		if(csptDeviceTypesHavingGroup!=null && csptDeviceTypesHavingGroup.getValue()!=null && csptDeviceTypesHavingGroup.getValue().contains(questionType.getType())) {
			ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
		} else {
			ministries = Ministry.findAssignedMinistriesInSession(selectedSession.getStartDate(), locale);
		}
		model.addAttribute("ministries",ministries);
		Ministry ministry=domain.getMinistry();
		if(ministry!=null){
			model.addAttribute("ministrySelected",ministry.getId());
			/**** Group ****/
			Group group=domain.getGroup();

			if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				model.addAttribute("formattedGroup",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getGroup().getNumber()));
				model.addAttribute("group",domain.getGroup().getId());
			}

			/**** Sub Departments ****/
			List<SubDepartment> subDepartments=
					MemberMinister.findAssignedSubDepartments(ministry, selectedSession.getEndDate(), locale);
			model.addAttribute("subDepartments",subDepartments); 
			SubDepartment subDepartment=domain.getSubDepartment();
			if(subDepartment!=null){

				model.addAttribute("subDepartmentSelected",subDepartment.getId());				
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
		model.addAttribute("userGroupName", workflowDetails.getAssigneeUserGroupName());
				
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

			populateInternalStatus(model,domain,locale);
			
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
			model.addAttribute("oldRecommendationStatus",recommendationStatus.getId());
		}	
		/**** Referenced Questions are collected in refentities****/
		List<Reference> refentities=new ArrayList<Reference>();
		List<String> refentitiesSessionDevice = new ArrayList<String>();
		if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			CustomParameter clubbedReferencedEntitiesVisibleUserGroups = 
					CustomParameter.findByName(CustomParameter.class, "SMOIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");
			
			if(clubbedReferencedEntitiesVisibleUserGroups != null 
					&& clubbedReferencedEntitiesVisibleUserGroups.getValue().contains(workflowDetails.getAssigneeUserGroupType())){
				
				refentities = StandaloneController.getReferencedEntityReferences(domain, locale);
				model.addAttribute("referencedQuestions",refentities);
			}
//			if(domain.getReferencedHDS() != null){
//				
//				List<ReferenceUnit> refs = domain.getReferencedEntities();
//				ReferenceUnit refU = null;
//				if(refs != null && !refs.isEmpty()){
//					refU = refs.get(0);
//					
//					refentitiesSessionDevice.add("[" + refU.getSessionTypeName()+", "+
//							FormaterUtil.formatNumberNoGrouping(refU.getSessionYear(), locale) + "], " + 
//							refU.getDeviceName());
//
//					model.addAttribute("referencedQuestions",refentities);
//					model.addAttribute("referencedHDS", refU.getId());
//					model.addAttribute("referencedQuestionsSessionAndDevice", refentitiesSessionDevice);
//				}
//			}
		}else{
			refentities = StandaloneController.getReferencedEntityReferences(domain, locale); 
			model.addAttribute("referencedQuestions", refentities);			
		}

		/**** Clubbed StandaloneMotion are collected in references ****/
		List<Reference> references=new ArrayList<Reference>();
		List<ClubbedEntity> clubbedEntities=StandaloneMotion.findClubbedEntitiesByPosition(domain);
		StringBuffer buffer1=new StringBuffer();
		buffer1.append(memberNames+",");			
		for(ClubbedEntity ce:clubbedEntities){
			Reference reference=new Reference();
			reference.setId(String.valueOf(ce.getId()));
			reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getStandaloneMotion().getNumber()));
			reference.setNumber(String.valueOf(ce.getStandaloneMotion().getId()));
			references.add(reference);
			String tempPrimary=ce.getStandaloneMotion().getPrimaryMember().getFullname();
			if(!buffer1.toString().contains(tempPrimary)){
				buffer1.append(ce.getStandaloneMotion().getPrimaryMember().getFullname()+",");
			}
			List<SupportingMember> clubbedSupportingMember=ce.getStandaloneMotion().getSupportingMembers();
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
		if((domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
				&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) 
					&& (internalStatus.getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
							|| internalStatus.getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {

				/**** setting the questions to be asked in factual position. ****/
				List<MasterVO> questionsToBeAskedInFactualPosition = new ArrayList<MasterVO>();
				String sessionParameter = null;
				if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
					sessionParameter = selectedSession.getParameter("motions_standalonemotion_halfhourdiscussion_questionsAskedForFactualPosition");
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
			}

			/**** populating the questions asked in factual position to show to department. ****/
			if((userGroupType.equals(ApplicationConstants.DEPARTMENT) || userGroupType.equals(ApplicationConstants.MEMBER)) 
					&& (internalStatus.getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
							|| internalStatus.getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
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
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){

				/**** process variables ****/
				//default values for process variables. can set conditionally for given actor here.
				model.addAttribute("pv_mailflag", "off");

				if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (internalStatus.getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) ||
						internalStatus.getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) && recommendationStatus.getType().equals(ApplicationConstants.STANDALONE_PROCESSED_SENDTOSECTIONOFFICER)) {
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
		
		if(questionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			populateForHalfHourDiscussionEdit(model, domain, request);
		}


		/**** Populating Put up otions and Actors ****/
		if(userGroupId!=null&&!userGroupId.isEmpty()){
			UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(userGroupId));
			List<Reference> actors=new ArrayList<Reference>();
			
			if(userGroup.getUserGroupType().getType().equals("department")&&internalStatus.getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)){
				Status sendback=Status.findByType(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK, locale);
				actors=WorkflowConfig.findStandaloneMotionActorsVO(domain,sendback , userGroup, Integer.parseInt(domain.getLevel()), locale);
			}else{
				actors=WorkflowConfig.findStandaloneMotionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
			}
			
			model.addAttribute("internalStatusSelected",internalStatus.getId());
			model.addAttribute("actors",actors);
			model.addAttribute("currentUserLevel", domain.getLevel());
		}
		/**** add domain to model ****/
		model.addAttribute("domain",domain);

		
		/****Level for RevisedDraft workflow****/
		model.addAttribute("revisedDraftInitialLevel",1);
		
		if(domain.getAnswerReceivedDate()!=null) {
			model.addAttribute("formattedAnswerReceivedDate", FormaterUtil.formatDateToString(domain.getAnswerReceivedDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
		}
	}

	private void populateInternalStatus(final ModelMap model,final StandaloneMotion domain,final String locale) {
		try{
			List<Status> internalStatuses=new ArrayList<Status>();
			DeviceType deviceType=domain.getType();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseType=domain.getHouseType();
			String actor=domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses=Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses=Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else{
				String usergroupType=actor.split("#")[1];
				/**** Final Approving Authority(Final Status) ****/
				CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
				CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "STANDALONE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "STANDALONE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "STANDALONE_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
					CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
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
			/**** INternal Status Master ****/
			List<Status> statusMaster = Status.findAllByStartingWith(Status.class, "type", "standalonemotion_", "priority", ApplicationConstants.ASC, locale);
			model.addAttribute("internalStatusMaster", statusMaster);
			/**** Internal Status****/
			model.addAttribute("internalStatuses",internalStatuses);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateForHalfHourDiscussionEdit(final ModelMap model, final StandaloneMotion domain, final HttpServletRequest request) {
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
				model.addAttribute("discussionDateSelected", FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT, "en_US").format(domain.getDiscussionDate()));
				model.addAttribute("formattedDiscussionDateSelected", FormaterUtil.getDateFormatter("dd/MM/yyyy", selectedSession.getLocale()).format(domain.getDiscussionDate()));
			}else{
				model.addAttribute("discussionDateSelected",null);
				model.addAttribute("formattedDiscussionDateSelected", null);
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
			final Locale locale,@Valid @ModelAttribute("domain") final StandaloneMotion domain,final BindingResult result) {
		String userGroupType = null;
		UserGroupType usergroupType = null;
		try{
			
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
					List<ReferenceUnit> referencedEntities=new ArrayList<ReferenceUnit>();
					for(String i:strReferencedEntities){
						ReferenceUnit referencedEntity=ReferenceUnit.findById(ReferenceUnit.class, Long.parseLong(i));
						referencedEntities.add(referencedEntity);
					}
					domain.setReferencedEntities(referencedEntities);
				}
			}

			if(domain.getType() != null){
				if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
						&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {

					if(domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
							|| domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {

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
			if(domain.getType().getType().trim().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(domain.getStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {					
					StandaloneMotionDraft latestDraft = null;
					try {
						latestDraft = StandaloneMotion.getLatestStandaloneMotionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
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
							
							if(domain.getInternalStatus().getType()==ApplicationConstants.STANDALONE_FINAL_REJECTION){
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
			} catch (ParseException e1) {
				try {
					if(strDateOfAnsweringByMinister != null && !strDateOfAnsweringByMinister.isEmpty()){
						dateOfAnsweringByMinister=FormaterUtil.getDateFormatter(locale.toString()).parse(strDateOfAnsweringByMinister);
					}
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
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) 
						&& (domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) 
								|| domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
					if(domain.getLastDateOfFactualPositionReceiving() == null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						if(domain.getNumberOfDaysForFactualPositionReceiving() != null && !domain.getNumberOfDaysForFactualPositionReceiving().equals("")){
							calendar.add(Calendar.DATE, domain.getNumberOfDaysForFactualPositionReceiving());
							domain.setLastDateOfFactualPositionReceiving(calendar.getTime());
						}
					}
				}
			}			


			String currentDeviceTypeWorkflowType = null;
			Workflow workflowFromUpdatedStatus = null;
			if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
					|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)
					|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
				
				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getRecommendationStatus(), domain.getLocale());
			
			} else if(domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)
					|| domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)
					|| (domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))
					||(domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))) {
				
				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
			} else {
				workflowFromUpdatedStatus = null;
			}
			
			//String sendbackactor=request.getParameter("sendbackactor");
			if(workflowFromUpdatedStatus!=null) {
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}		
			
			if(domain.getDrafts()==null) {
				StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class, domain.getId());
				domain.setDrafts(motion.getDrafts());
			}
			
			performAction(domain);
			
			domain.merge();
			
			if(currentDeviceTypeWorkflowType==null) {
				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}
			
			StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class, domain.getId());
			// On Group Change
			boolean isGroupChanged = false;
			boolean isMinistryChanged = false;
			boolean isSubDepartmentChanged = false;
			Group fromGroup = StandaloneMotion.isGroupChanged(motion);
			Ministry prevMinistry = null;
			SubDepartment prevSubDepartment = null;
			String ministrySelected = request.getParameter("ministrySelected");
			if(ministrySelected != null && !ministrySelected.equals("")){
				prevMinistry = Ministry.findById(Ministry.class, Long.parseLong(ministrySelected));
			}
			
			String subdepartmentSelected =request.getParameter("subDepartmentSelected");
			if(subdepartmentSelected != null && !subdepartmentSelected.equals("")){
				prevSubDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(subdepartmentSelected));
			}
			if(fromGroup != null) {
				isGroupChanged = true;
				StandaloneMotion.onGroupChange(motion, fromGroup);
			}else if(prevMinistry != null && !prevMinistry.equals(domain.getMinistry())){
				isMinistryChanged = true;
				StandaloneMotion.onMinistryChange(motion, prevMinistry);
			}else if(prevSubDepartment != null && !prevSubDepartment.equals(domain.getSubDepartment())){
				isSubDepartmentChanged = true;
				StandaloneMotion.onSubDepartmentChange(motion, prevSubDepartment);
			}
			
			if(isGroupChanged || isMinistryChanged || isSubDepartmentChanged) {
				/**** display message ****/
				model.addAttribute("type","taskcompleted");
				return "workflow/info";
			}
			else {
				String bulkEdit=request.getParameter("bulkedit");
				if(bulkEdit==null||!bulkEdit.equals("yes")){
	
					/**** Complete Task ****/
					String endflag = null;
					if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)){
						endflag = "continue";
					}else{
						endflag = domain.getEndFlag();
					}
	
					Map<String,String> properties=new HashMap<String, String>();
					String level="";
					//String currentDeviceTypeWorkflowType = null;
					if(domain.getType() != null){
						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
								&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
	
							//currentDeviceTypeWorkflowType = ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW;
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
									usergroupType = UserGroupType.findByType(temp[1], locale.toString());
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
													if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) ||
															domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
														Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
														reminderto = recepient.getEmail();								
													} else {
														reminderto=request.getParameter("reminderto");								
													}						
													properties.put("pv_reminderto", reminderto);
	
													String remindersubject=request.getParameter("remindersubject");						
													properties.put("pv_remindersubject", remindersubject);
	
													String remindercontent = "";
													if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT) ||
															domain.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
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
							//currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
							String nextuser = request.getParameter("actor");						
	
							properties.put("pv_deviceId", String.valueOf(domain.getId()));
							properties.put("pv_deviceTypeId", String.valueOf(domain.getType().getId()));
	
							if(nextuser!=null){
								if(!nextuser.isEmpty()){
									String[] temp=nextuser.split("#");
									properties.put("pv_user",temp[0]);
									level=temp[2];
									usergroupType = UserGroupType.findByType(temp[1], locale.toString());
								}
							}
							properties.put("pv_endflag", endflag);
						}
					}
		
					String strTaskId=workflowDetails.getTaskId();
					Task task = processService.findTaskById(strTaskId);
					processService.completeTask(task,properties);
	
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask = processService.getCurrentTask(processInstance);
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails newWFDetails = WorkflowDetails.create(motion, newtask, usergroupType, currentDeviceTypeWorkflowType, level);
								domain.setWorkflowDetailsId(newWFDetails.getId());
								domain.setTaskReceivedOn(new Date());
							}
						}			
					}
					
					workflowDetails.setDecisionInternalStatus(domain.getInternalStatus().getName());
					workflowDetails.setDecisionRecommendStatus(domain.getRecommendationStatus().getName());
					workflowDetails.setStatus("COMPLETED");
					workflowDetails.setCompletionTime(new Date());
					workflowDetails.merge();		
					
					//domain.merge();
					/**** display message ****/
					model.addAttribute("type","taskcompleted");
					return "workflow/info";
				}
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
		return "workflow/standalonemotion/"+userGroupType;
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
			String strLocale=locale.toString();
			String strGroup=request.getParameter("group");
			String strFile=request.getParameter("file");
			
			/**** usergroup,usergroupType,role *****/
			//List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
			Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
			UserGroup usergroup = UserGroup.findActive(credential, new Date(), locale.toString());
			String strUsergroup = String.valueOf(usergroup.getId());
			String strUserGroupType = usergroup.getUserGroupType().getType();
//			String strUserGroupType=null;
//			String strUsergroup=null;
//			if(userGroups!=null){
//				if(!userGroups.isEmpty()){
//					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SMOIS_ALLOWED_USERGROUPTYPES", "");
//					if(customParameter!=null){
//						String allowedUserGroups=customParameter.getValue(); 
//						for(UserGroup i:userGroups){
//							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
//								
//								strUsergroup=String.valueOf(i.getId());
//								strUserGroupType=i.getUserGroupType().getType();
//								break;
//							}
//						}
//					}								
//				}
//			}		
			Set<Role> roles=this.getCurrentUser().getRoles();
			String strRole=null;
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					strRole=i.getType();
					break;
				}else if(i.getType().contains("SMOIS_CLERK")){
					strRole=i.getType();
					break;
				}else if(i.getType().startsWith("SMOIS_")){
					strRole=i.getType();
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
					CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "STANDALONE_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "STANDALONE_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "STANDALONE_PUT_UP_OPTIONS_"+questionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"STANDALONE_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus!=null){
							internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** STANDALONE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** STANDALONE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** STANDALONE_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
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
				model.addAttribute("group", strGroup);
			}
			return "workflow/standalonemotion/bulkapprovalinit";		
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
		return "workflow/standalonemotion/bulkapprovalview";		
	}	

	@Transactional
	@RequestMapping(value="/bulkapproval/update",method=RequestMethod.POST)
	public String bulkApproval(final HttpServletRequest request,final Locale locale,
			final Model model){			
		String[] selectedItems = request.getParameterValues("items[]");
		String strInternalStatus=request.getParameter("internalstatus");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String strRemarks = request.getParameter("remarks");
		String strRefText = request.getParameter("refertext");
		String strFile = request.getParameter("file");
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
			
			List<ReferenceUnit> refs = null;
			
			/*if(!strWorkflowSubType.equals("request_to_supporting_member")){
				for(String i : selectedItems){
					
					WorkflowDetails wfDetails=WorkflowDetails.findById(WorkflowDetails.class, new Long(i));
					StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class,Long.parseLong(wfDetails.getDeviceId()));
					
					if(question != null){
						if(question.getReferencedEntities() != null){
							refs = question.getReferencedEntities();
							break;
						}
					}
				}
			}*/
			
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
					StandaloneMotion question = StandaloneMotion.findById(StandaloneMotion.class,Long.parseLong(wfDetails.getDeviceId()));
					if(question != null){
						if(question.getReferencedEntities() != null){
							refs = question.getReferencedEntities();
						}
					}
										
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
							reference = UserGroup.findStandaloneMotionActor(question, actor,level,locale.toString());
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
								if(!status.getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS) 
										&& !status.getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
										&& !status.getType().equals(ApplicationConstants.STANDALONE_PROCESSED_SENDTODEPARTMENT)
										&& !status.getType().equals(ApplicationConstants.STANDALONE_PROCESSED_SENDTOSECTIONOFFICER)){
									question.setInternalStatus(status);
								}
								question.setRecommendationStatus(status);	
								question.setEndFlag("continue");
							}
							
							if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
									&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								
								
								/**** Complete Task ****/
								Map<String,String> properties = new HashMap<String, String>();
								properties.put("pv_deviceId",String.valueOf(question.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
								properties.put("pv_user",temp[0]);
								properties.put("pv_endflag",question.getEndFlag());		
								UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
								String strTaskId = wfDetails.getTaskId();
								Task task = processService.findTaskById(strTaskId);
								processService.completeTask(task,properties);	
								if(question.getEndFlag() != null && !question.getEndFlag().isEmpty()
										&& question.getEndFlag().equals("continue")){
									/**** Create New Workflow Details ****/
									ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
									
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

										if(recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
											workflowFromUpdatedStatus = Workflow.findByStatus(recommendationStatus, locale.toString());
										} 
										else if(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)
												|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)
												|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))) {
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
								wfDetails.setInternalStatus(question.getInternalStatus().getName());
								wfDetails.setRecommendationStatus(question.getRecommendationStatus().getName());
								wfDetails.setCompletionTime(new Date());
								wfDetails.setDecisionInternalStatus(question.getInternalStatus().getName());
								wfDetails.setDecisionRecommendStatus(question.getRecommendationStatus().getName());
								wfDetails.merge();
								/**** Update Question ****/
								if(refs != null){
									question.setReferencedEntities(refs);
								}
								if(strRemarks != null){
									question.setRemarks(strRemarks);
								}
								if(strRefText != null){
									question.setRefText(strRefText);
								}
								
								/*if(strFile != null && !strFile.isEmpty() && !strFile.equals("-")){
									if(question.getFile() == null){
										question.setFile(new Integer(strFile));
									}
								}*/
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
								
							}else{
								/**** Complete Task ****/
								Map<String,String> properties=new HashMap<String, String>();
								properties.put("pv_deviceId",String.valueOf(question.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(question.getType().getId()));
								properties.put("pv_user",temp[0]);
								properties.put("pv_endflag",question.getEndFlag());	
								UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
								String strTaskId=wfDetails.getTaskId();
								Task task=processService.findTaskById(strTaskId);
								processService.completeTask(task,properties);	
								if(question.getEndFlag()!=null&&!question.getEndFlag().isEmpty()
										&&question.getEndFlag().equals("continue")){
									/**** Create New Workflow Details ****/
									ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
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

										if(recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)
												|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)
												|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
											workflowFromUpdatedStatus = Workflow.findByStatus(recommendationStatus, locale.toString());
										} 
										else if(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)
												|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)
												|| (internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
												||(internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED))) {
											workflowFromUpdatedStatus = Workflow.findByStatus(internalStatus, locale.toString());
										}
										else {
											workflowFromUpdatedStatus = Workflow.findByStatus(internalStatus, locale.toString());
										}
										
									} catch(ELSException e) {
										e.printStackTrace();
										model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
										model.addAttribute("type", "error");
										return "workflow/info";
									}
									Task newtask=processService.getCurrentTask(processInstance);
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
								wfDetails.setDecisionInternalStatus(question.getInternalStatus().getName());
								wfDetails.setDecisionRecommendStatus(question.getRecommendationStatus().getName());
								wfDetails.merge();
								/**** Update Motion ****/
								/*if(strFile != null && !strFile.isEmpty() && !strFile.equals("-")){
									if(question.getFile() == null){
										question.setFile(new Integer(strFile));
									}
								}*/
								if(refs != null){
									question.setReferencedEntities(refs);
								}
								if(strRemarks != null){
									question.setRemarks(strRemarks);
								}
								if(strRefText != null){
									question.setRefText(strRefText);
								}
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
							}
							question.merge();
							if(question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_ADMISSION)){
								recommendAdmissionMsg.append(question.formatNumber()+",");
							}else if(question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_RECOMMEND_REJECTION)){
								recommendRejectionMsg.append(question.formatNumber()+",");
							}else if(question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)){
								admittedMsg.append(question.formatNumber()+",");
							}else if(question.getInternalStatus().getType().equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)){
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
		return "workflow/standalonemotion/bulkapprovalview";
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
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String strFile = request.getParameter("file");
		String strLocale=locale.toString();	
		String assignee=this.getCurrentUser().getActualUsername();
		String strGroup=request.getParameter("group");
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strQuestionType!=null&&!(strQuestionType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUsergroupType!=null&&!(strUsergroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
			model.addAttribute("workflowSubType", strWorkflowSubType);
			
			/**** Workflow Details ****/
			List<WorkflowDetails> workflowDetails = null;
			try {
				/*workflowDetails = WorkflowDetails.
						findAll(strHouseType,strSessionType,strSessionYear,
								strQuestionType,strStatus,strWorkflowSubType,
								assignee,strItemsCount,strLocale,strFile);*/
				workflowDetails = WorkflowDetails.
						findAll(strHouseType,strSessionType,strSessionYear,
								strQuestionType,strStatus,strWorkflowSubType,
								assignee,strItemsCount,strLocale,strFile,strGroup, null);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals=new ArrayList<BulkApprovalVO>();
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			int counter = 0;
			for(WorkflowDetails i:workflowDetails){
				BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
				StandaloneMotion question=StandaloneMotion.findById(StandaloneMotion.class,Long.parseLong(i.getDeviceId()));
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
						if(question.getRevisedReason() != null && !question.getRevisedReason().isEmpty()){
							bulkApprovalVO.setReason(question.getRevisedReason());
						}else{
							bulkApprovalVO.setReason(question.getReason());
						}
						if(question.getRevisedBriefExplanation() != null && !question.getRevisedBriefExplanation().isEmpty()){
							bulkApprovalVO.setBriefExpanation(question.getRevisedBriefExplanation());
						}else{
							bulkApprovalVO.setBriefExpanation(question.getBriefExplanation());
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
						Map<String, String[]> requestMap=new HashMap<String, String[]>();			
						requestMap.put("motionId",new String[]{String.valueOf(question.getId())});
						requestMap.put("locale",new String[]{question.getLocale()});
						List result=Query.findReport("STANDALONE_GET_REVISION", requestMap);
						bulkApprovalVO.setRevisions(result);

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

	private void performAction(StandaloneMotion domain) throws ELSException {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
//		/**** Admission ****/
//		if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
//				&&recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)){
//			performActionOnAdmission(domain);
//		}	
//		/**** Rejection ****/
//		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)
//				&&recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)){
//			performActionOnRejection(domain);
//		}	
//		/**** Group Changed ****/
//		else if(internalStatus.equals(ApplicationConstants.STANDALONE_SYSTEM_GROUPCHANGED)&&
//				recommendationStatus.equals(ApplicationConstants.STANDALONE_SYSTEM_GROUPCHANGED)){
//			performActionOnGroupChange(domain);
//		}
//		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REPEATADMISSION)
//				&&recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REPEATADMISSION)){
//			performActionOnAdmission(domain);
//		}
//		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION)
//				&&recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION)){
//			performActionOnRejection(domain);
//		}else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
//				&& recommendationStatus.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED)){
//			performActionOnClarificationNeededFromMember(domain);
//		}
		
		if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
				&& recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)){
			performActionOnAdmission(domain);
		}
		
		
		/**** Repeat Admission ****/
		if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REPEATADMISSION)
				&& recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REPEATADMISSION)){
			performActionOnRepeatAdmission(domain);
		}	
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)){
			performActionOnRejection(domain);
		}
		/*** Clarification Asked  From Department***/
		else if(internalStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
			performActionOnClarificationNeededFromDepartment(domain);
		}
		/*** Clarification Asked From Member***/
		else if(internalStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
			performActionOnClarificationNeededFromMember(domain);
		}
		else if(internalStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
			performActionOnClarificationNeededFromMemberAndDepartment(domain);
		}
		
		/**** Clarification not received From Department ****/
		else if(internalStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&&recommendationStatus.startsWith(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnClarificationNotReceived(domain);
		}
		/**** Clarification not received From Member ****/
		else if(internalStatus.startsWith(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&&recommendationStatus.startsWith(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
			performActionOnClarificationNotReceived(domain);
		}
		/**** Clarification received FROM Member ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
				&& recommendationStatus.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED)){
			performActionOnClarificationReceived(domain);
		}
		/**** Clarification received From Department ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED)){
			performActionOnClarificationReceived(domain);
		}
		/**** Clarification received FROM Member & Department ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
				&& recommendationStatus.equals(ApplicationConstants.STANDALONE_PROCESSED_CLARIFICATION_RECEIVED)){
			performActionOnClarificationReceived(domain);
		}
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING)){
			performActionOnClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECT_CLUBBING)){
			performActionOnClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_NAMECLUBBING)){
			performActionOnNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECT_NAMECLUBBING)){
			performActionOnNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingRejectionPostAdmission(domain);
		}
		/**** Unclubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_UNCLUBBING)){
			performActionOnUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECT_UNCLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_REJECT_UNCLUBBING)){
			performActionOnUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnAdmissionDueToReverseClubbing(domain);
		}
	}

	private void performActionOnAdmission(StandaloneMotion domain) {
		domain.setStatus(domain.getInternalStatus());
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
			String reasonText=null;
			String briefExplainationText=null;
			if(domain.getRevisedSubject()!=null && !domain.getRevisedSubject().isEmpty()){
				subject=domain.getRevisedSubject();				
			}else{
				subject=domain.getSubject();
			}
			if(domain.getRevisedQuestionText()!=null && !domain.getRevisedQuestionText().isEmpty()){
				questionText=domain.getRevisedQuestionText();
			}else{
				questionText=domain.getQuestionText();
			}
			if(domain.getRevisedReason()!=null && !domain.getRevisedReason().isEmpty()){
				reasonText=domain.getRevisedQuestionText();
			}else{
				reasonText=domain.getReason();
			}
			if(domain.getRevisedBriefExplanation()!=null && !domain.getRevisedBriefExplanation().isEmpty()){
				briefExplainationText=domain.getRevisedQuestionText();
			}else{
				briefExplainationText=domain.getBriefExplanation();
			}
			
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				
					for(ClubbedEntity i:clubbedEntities){
						StandaloneMotion question=i.getStandaloneMotion();
						question.setRevisedSubject(subject);
						question.setRevisedQuestionText(questionText);
						question.setRevisedReason(reasonText);
						question.setRevisedBriefExplanation(briefExplainationText);
						question.setStatus(domain.getInternalStatus());
						question.setInternalStatus(domain.getInternalStatus());
						question.setRecommendationStatus(domain.getInternalStatus());
						question.simpleMerge();	
					}
			}
		}
		
		if(domain.getParent() != null) {
			//ClubbedEntity.updateClubbing(domain);

			// Hack (07May2014): Commenting the following line results in 
			// OptimisticLockException.
			domain.setVersion(domain.getVersion() + 1);
		}
	}
	
	
	private void performActionOnRepeatAdmission(StandaloneMotion domain) {
		//domain.setStatus(domain.getInternalStatus());
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
			String reasonText=null;
			String briefExplainationText=null;
			if(domain.getRevisedSubject()!=null && !domain.getRevisedSubject().isEmpty()){
				subject=domain.getRevisedSubject();				
			}else{
				subject=domain.getSubject();
			}
			if(domain.getRevisedQuestionText()!=null && !domain.getRevisedQuestionText().isEmpty()){
				questionText=domain.getRevisedQuestionText();
			}else{
				questionText=domain.getQuestionText();
			}
			if(domain.getRevisedReason()!=null && !domain.getRevisedReason().isEmpty()){
				reasonText=domain.getRevisedQuestionText();
			}else{
				reasonText=domain.getReason();
			}
			if(domain.getRevisedBriefExplanation()!=null && !domain.getRevisedBriefExplanation().isEmpty()){
				briefExplainationText=domain.getRevisedQuestionText();
			}else{
				briefExplainationText=domain.getBriefExplanation();
			}
			
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				
					for(ClubbedEntity i:clubbedEntities){
						StandaloneMotion question=i.getStandaloneMotion();
						question.setRevisedSubject(subject);
						question.setRevisedQuestionText(questionText);
						question.setRevisedReason(reasonText);
						question.setRevisedBriefExplanation(briefExplainationText);
						question.setStatus(domain.getInternalStatus());
						question.setInternalStatus(domain.getInternalStatus());
						question.setRecommendationStatus(domain.getInternalStatus());
						question.simpleMerge();	
					}
			}
		}
		
		if(domain.getParent() != null) {
			//ClubbedEntity.updateClubbing(domain);

			// Hack (07May2014): Commenting the following line results in 
			// OptimisticLockException.
			domain.setVersion(domain.getVersion() + 1);
		}
	}


	private void performActionOnRejection(StandaloneMotion domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
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
			String reasonText=null;
			String briefExplainationText=null;
			if(domain.getRevisedSubject()!=null && !domain.getRevisedSubject().isEmpty()){
				subject=domain.getRevisedSubject();				
			}else{
				subject=domain.getSubject();
			}
			if(domain.getRevisedQuestionText()!=null && !domain.getRevisedQuestionText().isEmpty()){
				questionText=domain.getRevisedQuestionText();
			}else{
				questionText=domain.getQuestionText();
			}
			if(domain.getRevisedReason()!=null && !domain.getRevisedReason().isEmpty()){
				reasonText=domain.getRevisedQuestionText();
			}else{
				reasonText=domain.getReason();
			}
			if(domain.getRevisedBriefExplanation()!=null && !domain.getRevisedBriefExplanation().isEmpty()){
				briefExplainationText=domain.getRevisedQuestionText();
			}else{
				briefExplainationText=domain.getBriefExplanation();
			}
			
			Status putUpForRejection=Status.findByType(ApplicationConstants.STANDALONE_PUTUP_REJECTION, domain.getLocale());
			
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				
					for(ClubbedEntity i:clubbedEntities){
						StandaloneMotion question=i.getStandaloneMotion();
						question.setRevisedSubject(subject);
						question.setRevisedQuestionText(questionText);
						question.setRevisedReason(reasonText);
						question.setRevisedBriefExplanation(briefExplainationText);
						question.setStatus(domain.getInternalStatus());
						question.setInternalStatus(domain.getInternalStatus());
						question.setRecommendationStatus(domain.getInternalStatus());
						question.simpleMerge();	
					}
			}
		}
		
		if(domain.getParent() != null) {
			//ClubbedEntity.updateClubbing(domain);

			// Hack (07May2014): Commenting the following line results in 
			// OptimisticLockException.
			domain.setVersion(domain.getVersion() + 1);
		}
		
		if(domain.getType() != null){
			if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
					&& domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				/******Adding a next HDS of the member to chart on rejection of one of the HDS of that member********/
				StandaloneMotion question = StandaloneMotion.find(domain.getPrimaryMember(), domain.getSession(), domain.getType(), domain.getLocale());
				if(question!=null){
					Chart chart = Chart.find(new Chart(question.getSession(),question.getType(), question.getLocale()));

					if(question.getNumber()== null){
						Integer number = null;
						try {
							number = StandaloneMotion.assignStandaloneMotionNo(question.getHouseType(), question.getSession(), question.getType(), question.getLocale());
						} catch (ELSException e) {
							e.printStackTrace();
						}
						question.setNumber(number+1);
					}
					if(chart != null){
						Chart.addToChart(question);
					}
				}
			}
		}		
	}	
	
	private void performActionOnClarificationNeededFromDepartment(StandaloneMotion domain) {
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
	}

	private void performActionOnClarificationNeededFromMember(StandaloneMotion domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}		
	}
	
	private void performActionOnClarificationNeededFromMemberAndDepartment(StandaloneMotion domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedQuestionText() == null || domain.getRevisedQuestionText().isEmpty()){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}
		
	}
	
	private void performActionOnClarificationNotReceived(StandaloneMotion domain) {
		Status newStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
	}
	
	private void performActionOnClarificationReceived(StandaloneMotion domain) {
		Status newStatus = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP, domain.getLocale());
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
	
	private void performActionOnClubbing(StandaloneMotion domain) throws ELSException {
		
		StandaloneMotion.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejection(StandaloneMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		StandaloneMotion.unclub(domain, domain.getLocale());
		
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

	private void performActionOnNameClubbing(StandaloneMotion domain) throws ELSException {
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
		
		StandaloneMotion.updateClubbing(domain);

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
	
	private void performActionOnNameClubbingRejection(StandaloneMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		StandaloneMotion.unclub(domain, domain.getLocale());
		
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

	private void performActionOnClubbingPostAdmission(StandaloneMotion domain) throws ELSException {
		
		StandaloneMotion.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejectionPostAdmission(StandaloneMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		StandaloneMotion.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbing(StandaloneMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		StandaloneMotion.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbingRejection(StandaloneMotion domain) throws ELSException {
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
	
	private void performActionOnAdmissionDueToReverseClubbing(StandaloneMotion domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, domain.getLocale());
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
	private void findLatestRemarksByUserGroup(final StandaloneMotion domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails)throws ELSException {
		
		String username = this.getCurrentUser().getUsername();
		Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
		UserGroup userGroup = UserGroup.findActive(credential, domain.getSubmissionDate(), domain.getLocale());
		UserGroupType userGroupType = userGroup.getUserGroupType();
		if(userGroupType == null
				|| (!userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)
				&& !userGroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
			CustomParameter customParameter = null;
			if(userGroupType!=null) {
				customParameter = CustomParameter.findByName(CustomParameter.class, "SMOIS_LATESTREVISION_STARTINGACTOR_"+userGroupType.getType().toUpperCase(), "");
				if(customParameter != null){
					String strUsergroupType = customParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "SMOIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
					if(defaultCustomParameter != null){
						String strUsergroupType = defaultCustomParameter.getValue();
						userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
					}
				}
			} else {
				CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "SMOIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
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
			List result=Query.findReport("SMOIS_LATEST_REVISION_FOR_DESKOFFICER", requestMap);
			model.addAttribute("latestRevisions",result);
		}else{
			List result=Query.findReport("SMOIS_LATEST_REVISIONS", requestMap);
			model.addAttribute("latestRevisions",result);
		}
		model.addAttribute("startingActor", userGroupType.getName());
	}	
}