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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.activiti.engine.impl.cmd.GetIdentityLinksForTaskCmd;
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
//import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.ClarificationNeededFrom;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.ReferenceUnit;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Resolution;
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

@Controller
@RequestMapping("/workflow/discussionmotion")
public class DiscussionMotionWorkflowController extends BaseController{
	
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
		String discussionMotionId=workflowDetails.getDeviceId();
		model.addAttribute("discussionMotion",discussionMotionId);
		DiscussionMotion discussionMotion=DiscussionMotion.findById(DiscussionMotion.class,Long.parseLong(discussionMotionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers=discussionMotion.getSupportingMembers();
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					i.setApprovedText(discussionMotion.getNoticeContent());
					i.setApprovedSubject(discussionMotion.getSubject());
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
		populateSupportingMember(model, discussionMotion,supportingMembers,locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId",workflowDetails.getId());
		model.addAttribute("status",workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,final DiscussionMotion discussionMotion, final List<SupportingMember> supportingMembers,final String locale){
		/**** Question Type ****/
		DeviceType discussionMotionType=discussionMotion.getType();
		if(discussionMotionType!=null){
			model.addAttribute("discussionMotionType", discussionMotionType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session=discussionMotion.getSession();
		if(session!=null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName",discussionMotion.getHouseType().getName());
		model.addAttribute("houseType",discussionMotion.getHouseType().getType());
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
		model.addAttribute("primaryMemberName",discussionMotion.getPrimaryMember().getFullnameLastNameFirst());
		/**** Priority ****/
		model.addAttribute("priority",discussionMotion.getPriority());
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
		/**** Clarification Needed from ****/
		binder.registerCustomEditor(ClarificationNeededFrom.class, new BaseEditor(
				new ClarificationNeededFrom()));
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
			Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
			if(longWorkflowdetails==null){
				longWorkflowdetails=Long.parseLong(request.getParameter("workflowdetails"));
			}
			workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
			/**** Adding workflowdetails and task to model ****/
			model.addAttribute("workflowdetails",workflowDetails.getId());
			model.addAttribute("workflowstatus",workflowDetails.getStatus());
			DiscussionMotion domain=DiscussionMotion.findById(DiscussionMotion.class,Long.parseLong(workflowDetails.getDeviceId()));

			/**** Populate Model ****/		
			populateModel(domain,model,request,workflowDetails);
			/**** Find Latest Remarks ****//*
			findLatestRemarksByUserGroup(domain,model,request,workflowDetails);*/
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
	
	
	
	private void populateModel(final DiscussionMotion domain, final ModelMap model,
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
		model.addAttribute("formattedDiscussionMotionType",questionType.getName());
		model.addAttribute("discussionMotionType",questionType.getId());
		model.addAttribute("selectedDiscussionMotionType",questionType.getType());


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
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "HIGHEST_DISCUSSIONMOTION_PRIORITY", "");
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
		List<Ministry> selectedministries=domain.getMinistries();
		if(selectedministries!=null && !selectedministries.isEmpty()){
			model.addAttribute("selectedministries",selectedministries);						
			/**** Sub Departments ****/
			List<SubDepartment> subDepartments=new ArrayList<SubDepartment>();
			for(Ministry m:selectedministries){
				List<SubDepartment> assignedSubDepartments = 
						MemberMinister.findAssignedSubDepartments(m, selectedSession.getEndDate(), locale);
				subDepartments.addAll(assignedSubDepartments);
			}
			model.addAttribute("subDepartments",subDepartments);
			List<SubDepartment> selectedSubDepartments=domain.getSubDepartments();
			if(!selectedSubDepartments.isEmpty()){
				model.addAttribute("selectedSubDepartments",selectedSubDepartments);
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
			populateInternalStatus(model,domain,locale);
			
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
			model.addAttribute("oldRecommendationStatus",recommendationStatus.getId());
		}	
		/**** Referenced Devices are collected in refentities****/
		
		/**** Referenced Entities are collected in refentities ****/
		List<ReferenceUnit> referencedEntities = domain.getReferencedEntities();
		if (referencedEntities != null) {
			List<Reference> refentities = new ArrayList<Reference>();
			List<Reference> refmotionentities = new ArrayList<Reference>();
			List<Reference> refquestionentities = new ArrayList<Reference>();
			List<Reference> refresolutionentities = new ArrayList<Reference>();
			for (ReferenceUnit re : referencedEntities) {
				if (re.getDeviceType() != null) {
					if (re.getDeviceType().startsWith(ApplicationConstants.DEVICE_MOTIONS)) {
						Reference reference = new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.formatNumberNoGrouping(re.getNumber(), locale));
						reference.setNumber(String.valueOf(re.getDevice()));
						refentities.add(reference);
						refmotionentities.add(reference);
					} else if (re.getDeviceType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)) {
						Reference reference = new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.formatNumberNoGrouping(re.getNumber(), locale));
						reference.setNumber(String.valueOf(re.getDevice()));
						refentities.add(reference);
						refresolutionentities.add(reference);
					} else if (re.getDeviceType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)) {
						Reference reference = new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.formatNumberNoGrouping(re.getNumber(), locale));
						reference.setNumber(String.valueOf(re.getDevice()));
						refentities.add(reference);
						refquestionentities.add(reference);
					}
				}
			}
			model.addAttribute("referencedMotions", refmotionentities);
			model.addAttribute("referencedQuestions", refquestionentities);
			model.addAttribute("referencedResolutions", refresolutionentities);
			model.addAttribute("referencedEntities", refentities);
		}

		/**** Clubbed Questions are collected in references ****/
		
		/**** Clubbed motions are collected in references ****/
		List<ClubbedEntity> clubbedEntities = DiscussionMotion.findClubbedEntitiesByPosition(domain, ApplicationConstants.DESC);
		if (clubbedEntities != null) {
			List<Reference> references = new ArrayList<Reference>();
			StringBuffer buffer1 = new StringBuffer();
			buffer1.append(memberNames + ",");
			for (ClubbedEntity ce : clubbedEntities) {
				Reference reference = new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getDiscussionMotion().getNumber()));
				reference.setNumber(String.valueOf(ce.getDiscussionMotion().getId()));
				references.add(reference);
				String tempPrimary = ce.getDiscussionMotion().getPrimaryMember().getFullname();
				if (!buffer1.toString().contains(tempPrimary)) {
					buffer1.append(ce.getDiscussionMotion().getPrimaryMember().getFullname() + ",");
				}
				List<SupportingMember> clubbedSupportingMember = ce.getDiscussionMotion().getSupportingMembers();
				if (clubbedSupportingMember != null) {
					if (!clubbedSupportingMember.isEmpty()) {
						for (SupportingMember l : clubbedSupportingMember) {
							String tempSupporting = l.getMember().getFullname();
							if (!buffer1.toString().contains(tempSupporting)) {
								buffer1.append(tempSupporting + ",");
							}
						}
					}
				}
			}
			if (!buffer1.toString().isEmpty()) {
				buffer1.deleteCharAt(buffer1.length() - 1);
			}
			String allMembersNames = buffer1.toString();
			model.addAttribute("memberNames", allMembersNames);
			if (!references.isEmpty()) {
				model.addAttribute("clubbedEntities", references);
			} else {
				if (domain.getParent() != null) {
					model.addAttribute("formattedParentNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
					model.addAttribute("parent", domain.getParent().getId());
				}
			}
		}
	
		/**** level ****/
		model.addAttribute("level",workflowDetails.getAssigneeLevel());

		/**** add domain to model ****/
		model.addAttribute("domain",domain);

		/**** setting the date of factual position receiving. ****/
//		String userGroupType=workflowDetails.getAssigneeUserGroupType();
//		String userGroupId=workflowDetails.getAssigneeUserGroupId();
		
		
		/**** Populating Put up otions and Actors ****/
//		if(userGroupId!=null&&!userGroupId.isEmpty()){
//			UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(userGroupId));
//			List<Reference> actors=new ArrayList<Reference>();
//			actors=WorkflowConfig.findDiscussionMotionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
//			model.addAttribute("internalStatusSelected",internalStatus.getId());
//			model.addAttribute("actors",actors);
//			if(actors!=null&&!actors.isEmpty()){
//				String nextActor=actors.get(0).getId();
//				String[] actorArr=nextActor.split("#");
//				domain.setLevel(actorArr[2]);
//				domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
//			}
//		}
		
	}

	private void populateInternalStatus(final ModelMap model,final DiscussionMotion domain,final String locale) {
		try{
			List<Status> internalStatuses=new ArrayList<Status>();
			DeviceType deviceType=domain.getType();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseType=domain.getHouseType();
			String actor=domain.getActor();
			if(actor==null){
				CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses=Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else if(actor.isEmpty()){
				CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_DEFAULT", "");
				internalStatuses=Status.findStatusContainedIn(defaultStatus.getValue(), locale);
			}else{
				String usergroupType=actor.split("#")[1];
				/**** Final Approving Authority(Final Status) ****/
				CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
				CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
				if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
					CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(), "");
					if(finalApprovingAuthorityStatus == null) {
						finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
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
			CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			if(specificDeviceStatusUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			}else if(specificStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)){
				CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus!=null){
						internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter!=null){
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode","discussionmotion_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus!=null){
					internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"DISCUSSIONMOTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode","discussionmotion_putup_options_final_notset");
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
	
	
	
	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final DiscussionMotion domain,final BindingResult result) {
		
		String userGroupType = null;
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
			/**** Binding Clubbed Entities ****/
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
			/**** Binding Referenced Entities ****/
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
			/**** Workflowdetails ****/
			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,domain.getWorkflowDetailsId());
			userGroupType=workflowDetails.getAssigneeUserGroupType();
			/**** Updating domain ****/
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
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
			
			//---new code
			String currentDeviceTypeWorkflowType = null;
			Workflow workflowFromUpdatedStatus = null;
			if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
					|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_UNCLUBBING)
					|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
				
				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getRecommendationStatus(), domain.getLocale());
			
			} else if(domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)
					|| domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)
					|| (domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))
					||(domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
				
				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
			} else {
				workflowFromUpdatedStatus = null;
			}
			
			//String sendbackactor=request.getParameter("sendbackactor");
			if(workflowFromUpdatedStatus!=null) {
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}
			
			performAction(domain);	
			
			if(currentDeviceTypeWorkflowType==null) {
				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}
			
			DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, domain.getId());
			
			String bulkEdit=request.getParameter("bulkedit");
			if(bulkEdit==null||!bulkEdit.equals("yes")){
				/**** Complete Task ****/		
				String nextuser=domain.getActor();
				String level=domain.getLevel();
				UserGroupType usergroupType = null;
				Map<String,String> properties=new HashMap<String, String>();
				properties.put("pv_deviceId",String.valueOf(domain.getId()));
				properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
				if(nextuser!=null){
					if(!nextuser.isEmpty()){
						String[] temp=nextuser.split("#");
						properties.put("pv_user",temp[0]);
						usergroupType = UserGroupType.findByType(temp[1], locale.toString());
					}
				}	
				String endflag=domain.getEndFlag();
				properties.put("pv_endflag",request.getParameter("endflag"));
				String strTaskId=workflowDetails.getTaskId();
				Task task=processService.findTaskById(strTaskId);
				processService.completeTask(task,properties);		
				if(endflag!=null){
					if(!endflag.isEmpty()){
						if(endflag.equals("continue")){
							ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
							Task newtask=processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = WorkflowDetails.create(domain, newtask, usergroupType, currentDeviceTypeWorkflowType,level);
							
							/**** FOr CLarificationFromMember and Department ****/
							if(domain.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
									&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_SEND_TO_DEPARTMENT)){
									
								Map<String, String> parameters = new HashMap<String, String>();
								User user = User.find(domain.getPrimaryMember());
								Credential credential = user.getCredential();
								parameters.put("pv_endflag", endflag);	
								parameters.put("pv_user",credential.getUsername());
								parameters.put("pv_deviceId", String.valueOf(motion.getId()));
								parameters.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
		
								ProcessDefinition processDefinition1 =processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
								ProcessInstance processInstance1 = processService.createProcessInstance(processDefinition1, parameters);
								Task newMembertask = processService.getCurrentTask(processInstance1);
								WorkflowDetails.create(domain,newMembertask,currentDeviceTypeWorkflowType,level);
												
							}
							domain.setWorkflowDetailsId(workflowDetails2.getId());
							domain.setTaskReceivedOn(new Date());
						}
					}
				}
				workflowDetails.setStatus("COMPLETED");
				workflowDetails.setCompletionTime(new Date());
				workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
				workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
				workflowDetails.merge();
				domain.merge();
				/**** display message ****/
				model.addAttribute("type","taskcompleted");
				return "workflow/info";
			}
			model.addAttribute("type","success");
			populateModel(domain, model, request, workflowDetails);
		}catch(Exception e){
			model.addAttribute("error", e.getMessage());
			logger.error("error", e);
		}
		
		return "workflow/discussionmotion/"+userGroupType;
	}
	
	
	private void performAction(final DiscussionMotion domain) {
		try{
			String internalStatus=domain.getInternalStatus().getType();
			String recommendationStatus=domain.getRecommendationStatus().getType();
			if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)
					&&recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)){
				performActionOnAdmission(domain);
			}
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECTION)
					&&recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECTION)){
				performActionOnRejection(domain);
			}
			/*** Clarification Asked  From Department***/
			else if(internalStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					&&recommendationStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)){
				performActionOnClarificationNeededFromDepartment(domain);
			}
			/*** Clarification Asked From Member***/
			else if(internalStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					&&recommendationStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)){
				performActionOnClarificationNeededFromMember(domain);
			}
			else if(internalStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					&&recommendationStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
				performActionOnClarificationNeededFromMemberAndDepartment(domain);
			}
			
			/**** Clarification not received From Department ****/
			else if(internalStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					&&recommendationStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
				performActionOnClarificationNotReceived(domain);
			}
			/**** Clarification not received From Member ****/
			else if(internalStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					&&recommendationStatus.startsWith(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)){
				performActionOnClarificationNotReceived(domain);
			}
			/**** Clarification received FROM Member ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					&& recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED)){
				performActionOnClarificationReceived(domain);
			}
			/**** Clarification received From Department ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					&& recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED)){
				performActionOnClarificationReceived(domain);
			}
			/**** Clarification received FROM Member & Department ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					&& recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED)){
				performActionOnClarificationReceived(domain);
			}
			/**** Clubbing is approved ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)&&
					recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING)){
				performActionOnClubbing(domain);
			}
			/**** Clubbing is rejected ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECT_CLUBBING)&&
					recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECT_CLUBBING)){
				performActionOnClubbingRejection(domain);
			}
			/**** Name clubbing is approved ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)&&
					recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_NAME_CLUBBING)){
				performActionOnNameClubbing(domain);
			}		
			/**** Name clubbing is rejected ****/		
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECT_NAME_CLUBBING)&&
					recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECT_NAME_CLUBBING)){
				performActionOnNameClubbingRejection(domain);
			}	
			/**** Clubbing Post Admission is approved ****/
			else if(recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)){
				performActionOnClubbingPostAdmission(domain);
			}
			/**** Clubbing Post Admission is rejected ****/
			else if(recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
				performActionOnClubbingRejectionPostAdmission(domain);
			}
			/**** Unclubbing is approved ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_UNCLUBBING)&&
					recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_UNCLUBBING)){
				performActionOnUnclubbing(domain);
			}
			/**** Unclubbing is rejected ****/
			else if(internalStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECT_UNCLUBBING)&&
					recommendationStatus.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECT_UNCLUBBING)){
				performActionOnUnclubbingRejection(domain);
			}
		}catch(Exception e){
			logger.error("error", e);
		}
	}
	
	
	private void performActionOnAdmission(final DiscussionMotion domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		/**** Setting revised subject,question text,revised reason,revised brief explaination if not already set ****/
		if(domain.getRevisedSubject()==null){			
			domain.setRevisedSubject(domain.getSubject());			
		}else if(domain.getRevisedSubject().isEmpty()){
			domain.setRevisedSubject(domain.getSubject());
		}
		
		if(domain.getRevisedNoticeContent()==null){			
			domain.setRevisedNoticeContent(domain.getNoticeContent());			
		}else if(domain.getRevisedNoticeContent().isEmpty()){
			domain.setRevisedNoticeContent(domain.getNoticeContent());
		}		
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
			String subject=null;
			String details=null;
			
			if(domain.getRevisedSubject()!=null){
				if(!domain.getRevisedSubject().isEmpty()){
					subject=domain.getRevisedSubject();
				}else{
					subject=domain.getSubject();
				}
			}else{
				subject=domain.getSubject();
			}
			
			if(domain.getRevisedNoticeContent()!=null){
				if(!domain.getRevisedNoticeContent().isEmpty()){
					details=domain.getRevisedNoticeContent();
				}else{
					details=domain.getNoticeContent();
				}
			}else{
				details=domain.getNoticeContent();
			}
			
			Status status=Status.findByType(ApplicationConstants.DISCUSSIONMOTION_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				DiscussionMotion motion=i.getDiscussionMotion();
				if(motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_CLUBBED)){
					motion.setRevisedSubject(subject);
					motion.setRevisedNoticeContent(details);
					motion.setStatus(finalStatus);
					motion.setInternalStatus(finalStatus);
					motion.setRecommendationStatus(finalStatus);
				}else{					
					motion.setInternalStatus(status);
					motion.setRecommendationStatus(status);
				}			
				motion.simpleMerge();
			}
		}
	}	
	
	private void performActionOnRejection(DiscussionMotion domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		/**** Setting revised subject,question text,revised reason,revised brief explaination if not already set ****/
		if(domain.getRevisedSubject()==null){			
			domain.setRevisedSubject(domain.getSubject());			
		}else if(domain.getRevisedSubject().isEmpty()){
			domain.setRevisedSubject(domain.getSubject());
		}
		
		if(domain.getRevisedNoticeContent()==null){			
			domain.setRevisedNoticeContent(domain.getNoticeContent());			
		}else if(domain.getRevisedNoticeContent().isEmpty()){
			domain.setRevisedNoticeContent(domain.getNoticeContent());
		}		
		
		
		domain.merge(); // Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		DiscussionMotion.updateClubbing(domain);
		
	}
	
	private void performActionOnClarificationNeededFromDepartment(DiscussionMotion domain) {
		
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedNoticeContent() == null || domain.getRevisedNoticeContent().isEmpty()){			
			domain.setRevisedNoticeContent(domain.getNoticeContent());			
		}
	}

	private void performActionOnClarificationNeededFromMember(DiscussionMotion domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
				
		if(domain.getRevisedNoticeContent() == null || domain.getRevisedNoticeContent().isEmpty()){			
			domain.setRevisedNoticeContent(domain.getNoticeContent());			
		}	
	}
	
	private void performActionOnClarificationNeededFromMemberAndDepartment(DiscussionMotion domain) {
		if(domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()){			
			domain.setRevisedSubject(domain.getSubject());			
		}
		
		if(domain.getRevisedNoticeContent() == null || domain.getRevisedNoticeContent().isEmpty()){			
			domain.setRevisedNoticeContent(domain.getNoticeContent());			
		}
		
	}
	
	private void performActionOnClarificationReceived(DiscussionMotion domain) {
		Status newStatus = Status.findByType(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_PUTUP, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");		
	}
	
	private void performActionOnClubbing(DiscussionMotion domain) throws ELSException {
		
		DiscussionMotion.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejection(DiscussionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		DiscussionMotion.unclub(domain, domain.getLocale());
		
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

	private void performActionOnNameClubbing(DiscussionMotion domain) throws ELSException {
		
		DiscussionMotion.updateClubbing(domain);

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
	
	private void performActionOnNameClubbingRejection(DiscussionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		DiscussionMotion.unclub(domain, domain.getLocale());
		
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

	private void performActionOnClubbingPostAdmission(DiscussionMotion domain) throws ELSException {
		
		DiscussionMotion.updateClubbing(domain);
		
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
	
	private void performActionOnClubbingRejectionPostAdmission(DiscussionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		DiscussionMotion.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbing(DiscussionMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		DiscussionMotion.unclub(domain, domain.getLocale());
		
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
	
	private void performActionOnUnclubbingRejection(DiscussionMotion domain) throws ELSException {
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

	private void performActionOnClarificationNotReceived(DiscussionMotion domain) {
		Status newStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_PUTUP, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		
	}	
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value = "/bulkapproval/init", method = RequestMethod.POST)
	public String getBulkApprovalInit(final HttpServletRequest request,
			final Locale locale, final ModelMap model) {

		try {
			/**** Request Params ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strMotionType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strItemsCount = request.getParameter("itemsCount");
			String strFile = request.getParameter("file");
			String strLocale = locale.toString();
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			String strUserGroupType = null;
			String strUsergroup = null;
			if (userGroups != null) {
				if (!userGroups.isEmpty()) {
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DMOIS_ALLOWED_USERGROUPTYPES", "");
					if (customParameter != null) {
						String allowedUserGroups = customParameter.getValue();
						for (UserGroup i : userGroups) {
							if (allowedUserGroups.contains(i.getUserGroupType().getType())) {
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
			for (Role i : roles) {
				if (i.getType().startsWith("MEMBER_")) {
					strRole = i.getType();
					break;
				} else if (i.getType().contains("DMOIS_CLERK")) {
					strRole = i.getType();
					break;
				} else if (i.getType().startsWith("DMOIS_")) {
					strRole = i.getType();
					break;
				}
			}
			if (strHouseType != null && !(strHouseType.isEmpty())
					&& strSessionType != null && !(strSessionType.isEmpty())
					&& strSessionYear != null && !(strSessionYear.isEmpty())
					&& strMotionType != null && !(strMotionType.isEmpty())
					&& strStatus != null && !(strStatus.isEmpty())
					&& strRole != null && !(strRole.isEmpty())
					&& strUsergroup != null && !(strUsergroup.isEmpty())
					&& strUserGroupType != null
					&& !(strUserGroupType.isEmpty()) && strItemsCount != null
					&& !(strItemsCount.isEmpty()) && strFile != null
					&& !(strFile.isEmpty()) && strWorkflowSubType != null
					&& !(strWorkflowSubType.isEmpty())) {
				/**** List of Statuses ****/
				if (strWorkflowSubType.equals("request_to_supporting_member")) {
					Status approveStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale.toString());
					Status rejectStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale.toString());
					List<Status> decisionStatus = new ArrayList<Status>();
					decisionStatus.add(approveStatus);
					decisionStatus.add(rejectStatus);
					model.addAttribute("internalStatuses", decisionStatus);
				} else {
					List<Status> internalStatuses = new ArrayList<Status>();
					HouseType houseType = HouseType.findByFieldName(HouseType.class, "name", strHouseType, strLocale);
					DeviceType motionType = DeviceType.findByFieldName(DeviceType.class, "name", strMotionType, strLocale);
					Status internalStatus = Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class, motionType.getType().toUpperCase()+ "_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_" + internalStatus.getType().toUpperCase() + "_" + strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() + "_" + strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_" + motionType.getType() .toUpperCase() + "_" + strUserGroupType.toUpperCase(), "");
					if (finalApprovingAuthority != null
							&& finalApprovingAuthority.getValue().contains(strUserGroupType)) {
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_PUT_UP_OPTIONS_" + strUserGroupType.toUpperCase(), "");
						if (finalApprovingAuthorityStatus != null) {
							internalStatuses = Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/****
					 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+
					 * INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)
					 ****/
					else if (deviceTypeInternalStatusUsergroup != null) {
						internalStatuses = Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/****
					 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+
					 * USERGROUP(Pre Final Status-House Type Basis)
					 ****/
					else if (deviceTypeHouseTypeUsergroup != null) {
						internalStatuses = Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}
					/****
					 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre
					 * Final Status)
					 ****/
					else if (deviceTypeUsergroup != null) {
						internalStatuses = Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}
					model.addAttribute("internalStatuses", internalStatuses);
				}
				/**** Request Params To Model Attribute ****/
				model.addAttribute("houseType", strHouseType);
				model.addAttribute("sessionType", strSessionType);
				model.addAttribute("sessionYear", strSessionYear);
				model.addAttribute("motionType", strMotionType);
				model.addAttribute("status", strStatus);
				model.addAttribute("role", strRole);
				model.addAttribute("usergroup", strUsergroup);
				model.addAttribute("usergroupType", strUserGroupType);
				model.addAttribute("itemscount", strItemsCount);
				model.addAttribute("file", strFile);
				model.addAttribute("workflowSubType", strWorkflowSubType);
			}
			return "workflow/discussionmotion/bulkapprovalinit";
		} catch (ELSException ee) {
			logger.error(ee.getMessage());
			model.addAttribute("error", ee.getParameter());
			model.addAttribute("type", "error");
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("type", "error");
		}
		return "workflow/info";
	}

	@RequestMapping(value = "/bulkapproval/view", method = RequestMethod.POST)
	public String getBulkApprovalView(final HttpServletRequest request,
			final Locale locale, final Model model) {
		populateBulkApprovalView(model, request, locale.toString());
		return "workflow/discussionmotion/bulkapprovalview";
	}

	@Transactional
	@RequestMapping(value = "/bulkapproval/update", method = RequestMethod.POST)
	public String bulkApproval(final HttpServletRequest request,
			final Locale locale, final Model model) {
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus = request.getParameter("aprstatus");
		String strWorkflowSubType = request.getParameter("workflowSubType");
		StringBuffer recommendAdmissionMsg = new StringBuffer();
		StringBuffer recommendRejectionMsg = new StringBuffer();
		StringBuffer admittedMsg = new StringBuffer();
		StringBuffer rejectedMsg = new StringBuffer();
		if (selectedItems != null && (selectedItems.length > 0)
				&& strStatus != null && !strStatus.isEmpty()
				&& strWorkflowSubType != null && !strWorkflowSubType.isEmpty()) {
			Status status = null;
			if (!strStatus.equals("-")) {
				status = Status.findById(Status.class, Long.parseLong(strStatus));
			}
			for (String i : selectedItems) {
				if (strWorkflowSubType.equals("request_to_supporting_member")) {
					String[] temp = i.split("#");
					Long id = Long.parseLong(temp[0]);
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, id);
					/**** Updating Supporting Member ****/
					SupportingMember supportingMember = SupportingMember.findById(SupportingMember.class, Long.parseLong(temp[1]));
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
					/****
					 * In case of Supporting Member Approval Status should
					 * reflect member's actions
					 ****/
					wfDetails.setInternalStatus(status.getName());
					wfDetails.setRecommendationStatus(status.getName());
					wfDetails.merge();
				} else {
					Long id = Long.parseLong(i);
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, id);
					DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(wfDetails.getDeviceId()));
					String actor = request.getParameter("actor");
					if (actor == null || actor.isEmpty()) {
						actor = motion.getActor();
						String[] temp = actor.split("#");
						actor = temp[1];
					}
					String level = request.getParameter("level");
					if (level == null || level.isEmpty()) {
						level = motion.getLevel();

					}

					if (actor != null && !actor.isEmpty() && level != null
							&& !level.isEmpty()) {
						Reference reference = null;
						try {
							reference = UserGroup.findDiscussionMotionActor(motion, actor, level, locale.toString());
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
						if (reference != null && reference.getId() != null
								&& !reference.getId().isEmpty()
								&& reference.getName() != null
								&& !reference.getName().isEmpty()) {
							/**** Update Actor ****/
							String[] temp = reference.getId().split("#");
							motion.setActor(reference.getId());
							motion.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
							motion.setLevel(temp[2]);
							/**** Update Internal Status and Recommendation Status ****/
							/*if (status != null) {
								motion.setInternalStatus(status);
								motion.setRecommendationStatus(status);
							}
							motion.setEndFlag("continue");*/
							
							if(status != null){
								if(!status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_DISCUSS) 
								&& !status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_SENDBACK)
								&& !status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_SEND_TO_DEPARTMENT)
								&& !status.getType().equals(ApplicationConstants.DISCUSSIONMOTION_PROCESSED_SEND_TO_SECTIONOFFICER)){
									motion.setInternalStatus(status);
								}
								motion.setRecommendationStatus(status);	
								motion.setEndFlag("continue");
							}
							
							/**** Complete Task ****/
							Map<String, String> properties = new HashMap<String, String>();
							properties.put("pv_deviceId", String.valueOf(motion.getId()));
							properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
							properties.put("pv_user", temp[0]);
							properties.put("pv_endflag", motion.getEndFlag());
							UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
							String strTaskId = wfDetails.getTaskId();
							Task task = processService.findTaskById(strTaskId);
							processService.completeTask(task, properties);
							if (motion.getEndFlag() != null
									&& !motion.getEndFlag().isEmpty()
									&& motion.getEndFlag().equals("continue")) {
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
									Status internalStatus = motion.getInternalStatus();
									String internalStatusType = internalStatus.getType();
									Status recommendationStatus = motion.getRecommendationStatus();
									String recommendationStatusType = recommendationStatus.getType();

									if(recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflowFromUpdatedStatus = Workflow.findByStatus(recommendationStatus, locale.toString());
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
									workflowDetails2 = WorkflowDetails.create(motion,newtask,usergroupType,workflowFromUpdatedStatus.getType(),level);
								} catch (ELSException e) {
									e.printStackTrace();
									model.addAttribute("error", e.getParameter());
								}
								motion.setWorkflowDetailsId(workflowDetails2.getId());
								motion.setTaskReceivedOn(new Date());
							}
							/**** Update Old Workflow Details ****/
							wfDetails.setStatus("COMPLETED");
							wfDetails.setInternalStatus(motion.getInternalStatus().getName());
							wfDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
							wfDetails.setCompletionTime(new Date());
							wfDetails.merge();
							/**** Update Motion ****/
							motion.setEditedOn(new Date());
							motion.setEditedBy(this.getCurrentUser().getActualUsername());
							motion.setEditedAs(wfDetails.getAssigneeUserGroupName());
							performAction(motion);
							motion.merge();
							if (motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_ADMISSION)) {
								recommendAdmissionMsg.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale()) + ",");
							} else if (motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_REJECTION)) {
								recommendRejectionMsg.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale()) + ",");
							} else if (motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)) {
								admittedMsg.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale()) + ",");
							} else if (motion.getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECTION)) {
								rejectedMsg.append(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), motion.getLocale()) + ",");
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
		populateBulkApprovalView(model, request, locale.toString());
		return "workflow/eventmotion/bulkapprovalview";
	}

	private void populateBulkApprovalView(final Model model,
			final HttpServletRequest request, final String locale) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strMotionType = request.getParameter("motionType");
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strItemsCount = request.getParameter("itemscount");
		String strFile = request.getParameter("file");
		String strWorkflowSubType = request.getParameter("workflowSubType");
		String strLocale = locale.toString();
		String assignee = this.getCurrentUser().getActualUsername();
		if (strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strMotionType != null && !(strMotionType.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroup != null && !(strUsergroup.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())
				&& strFile != null && !(strFile.isEmpty())
				&& strWorkflowSubType != null
				&& !(strWorkflowSubType.isEmpty())) {
			model.addAttribute("workflowSubType", strWorkflowSubType);
			/**** Workflow Details ****/
			List<WorkflowDetails> workflowDetails = new ArrayList<WorkflowDetails>();

			try {
				workflowDetails = WorkflowDetails.findAll(strHouseType, strSessionType, strSessionYear, strMotionType, strStatus, strWorkflowSubType, assignee, strItemsCount, strLocale, strFile);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
			NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			for (WorkflowDetails i : workflowDetails) {
				BulkApprovalVO bulkApprovalVO = new BulkApprovalVO();
				DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(i.getDeviceId()));
				/**** Bulk Submission for Supporting Members ****/
				if (i.getWorkflowSubType().equals("request_to_supporting_member")) {
					bulkApprovalVO.setId(String.valueOf(i.getId()));
					bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));
					bulkApprovalVO.setDeviceNumber("-");
					bulkApprovalVO.setDeviceType(motion.getType().getName());
					if(motion.getPrimaryMember() != null){
						bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
					}
					bulkApprovalVO.setSubject(motion.getSubject());
					List<SupportingMember> supportingMembers = motion.getSupportingMembers();
					for (SupportingMember j : supportingMembers) {
						User user;
						try {
							user = User.findByUserName(i.getAssignee(), locale.toString());
							Member member = Member.findMember(user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getBirthDate(), locale.toString());
							if (member != null
									&& member.getId() == j.getMember().getId()) {
								bulkApprovalVO.setSupportingMemberId(String.valueOf(j.getId()));
							}

						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}
					bulkApprovalVO.setCurrentStatus(i.getStatus());
					if (i.getInternalStatus() != null) {
						bulkApprovalVO.setLastDecision(i.getInternalStatus());
					} else {
						bulkApprovalVO.setLastDecision("-");
					}
					bulkapprovals.add(bulkApprovalVO);
				} else/**** Bulk Submission For Workflows ****/
				{
					/**** File Bulk Submission ****/
					if (strFile != null && !strFile.isEmpty()
							&& !strFile.equals("-") && motion.getFile() != null
							&& motion.getFile() == Integer.parseInt(strFile)) {
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));
						if (motion.getNumber() != null) {
							bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
						} else {
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(motion.getType().getName());
						if(motion.getPrimaryMember() != null){
							bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
						}
						bulkApprovalVO.setSubject(motion.getSubject());
						if (motion.getRemarks() != null
								&& !motion.getRemarks().isEmpty()) {
							bulkApprovalVO.setLastRemark(motion.getRemarks());
						} else {
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(motion.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(motion.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}/**** Status Wise Bulk Submission ****/
					else if (strFile != null && !strFile.isEmpty()
							&& strFile.equals("-")) {
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));
						if (motion.getNumber() != null) {
							bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
						} else {
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(motion.getType().getName());
						if(motion.getPrimaryMember() != null){
							bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
						}
						bulkApprovalVO.setSubject(motion.getSubject());
						if (motion.getRemarks() != null
								&& !motion.getRemarks().isEmpty()) {
							bulkApprovalVO.setLastRemark(motion.getRemarks());
						} else {
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(motion.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(motion.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}
				}
			}
			model.addAttribute("bulkapprovals", bulkapprovals);
			if (bulkapprovals != null && !bulkapprovals.isEmpty()) {
				model.addAttribute("motionId", bulkapprovals.get(0).getDeviceId());
			}
		}
	}
}
