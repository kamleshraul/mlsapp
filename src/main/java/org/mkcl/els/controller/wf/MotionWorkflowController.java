package org.mkcl.els.controller.wf;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BulkApprovalVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
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
@RequestMapping("/workflow/motion")
public class MotionWorkflowController extends BaseController{

	/** The process service. */
	@Autowired
	private IProcessService processService;

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
		/**** Ministry ****/
		binder.registerCustomEditor(Ministry.class, new BaseEditor(
				new Ministry()));
		/**** Department ****/
		binder.registerCustomEditor(Department.class, new BaseEditor(
				new Department()));
		/**** Sub Department ****/
		binder.registerCustomEditor(SubDepartment.class, new BaseEditor(
				new SubDepartment()));
	}


	@RequestMapping(value="supportingmember",method=RequestMethod.GET)
	public String initSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Motion ****/
		String motionId=workflowDetails.getDeviceId();
		model.addAttribute("motion",motionId);
		Motion motion=Motion.findById(Motion.class,Long.parseLong(motionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers=motion.getSupportingMembers();
		
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					i.setApprovedText(motion.getDetails());
					i.setApprovedSubject(motion.getSubject());
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
		populateSupportingMember(model, motion,supportingMembers,locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId",workflowDetails.getId());
		model.addAttribute("status",workflowDetails.getStatus());

		return workflowDetails.getForm();
	}

	private void populateSupportingMember(final ModelMap model,final Motion motion, final List<SupportingMember> supportingMembers,final String locale){
		/**** motion Type ****/
		DeviceType motionType=motion.getType();
		if(motionType!=null){
			model.addAttribute("motionType", motionType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session=motion.getSession();
		if(session!=null){
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName",motion.getHouseType().getName());
		model.addAttribute("houseType",motion.getHouseType().getType());
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
		model.addAttribute("primaryMemberName",motion.getPrimaryMember().getFullnameLastNameFirst());

	}

	@Transactional
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
			workflowDetails.setInternalStatus(domain.getDecisionStatus().getName());
			workflowDetails.setRecommendationStatus(domain.getDecisionStatus().getName());
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

	@RequestMapping(method=RequestMethod.GET)
	public String initMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		if(longWorkflowdetails==null){
			longWorkflowdetails=Long.parseLong(request.getParameter("workflowdetails"));
		}
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Adding workflowdetails and task to model ****/
		model.addAttribute("workflowdetails",workflowDetails.getId());
		model.addAttribute("workflowstatus",workflowDetails.getStatus());
		Motion domain=Motion.findById(Motion.class,Long.parseLong(workflowDetails.getDeviceId()));
		/**** Populate Model ****/		
		populateModel(domain,model,request,workflowDetails);		
		return workflowDetails.getForm();
	}

	private void populateModel(final Motion domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) {
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit",request.getParameter("bulkedit"));
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

		/**** Motion Type ****/
		DeviceType motionType=domain.getType();
		model.addAttribute("formattedMotionType",motionType.getName());
		model.addAttribute("motionType",motionType.getId());
		model.addAttribute("selectedMotionType",motionType.getType());	

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
					/**** All Supporting Members Are Preserved.But the names that appear in supporting 
					 * members list will vary. ****/
					Member m=i.getMember();
					supportingMembers.add(m);					
					if(i.getDecisionStatus()!=null
							&&i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
						bufferFirstNamesFirst.append(m.getFullname()+",");
					}
				}
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
		/**** Ministries And Sub Departments ****/
		List<Ministry> ministries = new ArrayList<Ministry>();
		try {
			ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
		} catch (ELSException e) {
			e.printStackTrace();
		}
		model.addAttribute("ministries",ministries);
		Ministry ministry=domain.getMinistry();
		if(ministry!=null){
			model.addAttribute("ministrySelected",ministry.getId());						
			/**** Sub Departments ****/
			List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,locale);
			model.addAttribute("subDepartments",subDepartments);
			SubDepartment subDepartment=domain.getSubDepartment();
			if(subDepartment!=null){
				model.addAttribute("subDepartmentSelected",subDepartment.getId());
			}
		}							
		/**** Submission Date,Creation date,WorkflowStartedOn date,TaskReceivedOn date****/ 
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
		if(domain.getPostBallotNumber() != null){
			model.addAttribute("formattedPostBallotNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getPostBallotNumber()));
		}
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());
		model.addAttribute("dataEnteredBy",domain.getDataEnteredBy());

		/**** UserGroup and UserGroup Type ****/
		String usergroupType=workflowDetails.getAssigneeUserGroupType();
		String userGroupId=workflowDetails.getAssigneeUserGroupId();
		model.addAttribute("usergroup",workflowDetails.getAssigneeUserGroupId());
		model.addAttribute("usergroupType",workflowDetails.getAssigneeUserGroupType());		

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
			populateInternalStatus(model,domain,usergroupType,locale);
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
		}	
		/**** Referenced Entities are collected in refentities****/		
		List<ReferencedEntity> referencedEntities=domain.getReferencedEntities();
		if(referencedEntities!=null){
			List<Reference> refentities=new ArrayList<Reference>();
			List<Reference> refmotionentities=new ArrayList<Reference>();
			List<Reference> refquestionentities=new ArrayList<Reference>();
			List<Reference> refresolutionentities=new ArrayList<Reference>();
			for(ReferencedEntity re:referencedEntities){
				if(re.getDeviceType() != null){
					if(re.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_MOTIONS)){
						Reference reference=new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(((Motion)re.getDevice()).getNumber()));
						reference.setNumber(String.valueOf(((Motion)re.getDevice()).getId()));
						refentities.add(reference);	
						refmotionentities.add(reference);
					}else if(re.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						Reference reference=new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(((Motion)re.getDevice()).getNumber()));
						reference.setNumber(String.valueOf(((Motion)re.getDevice()).getId()));
						refentities.add(reference);	
						refresolutionentities.add(reference);
					}else if(re.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						Reference reference=new Reference();
						reference.setId(String.valueOf(re.getId()));
						reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(((Motion)re.getDevice()).getNumber()));
						reference.setNumber(String.valueOf(((Motion)re.getDevice()).getId()));
						refentities.add(reference);	
						refquestionentities.add(reference);
					}
				}
			}
			model.addAttribute("referencedMotions",refmotionentities);
			model.addAttribute("referencedQuestions",refquestionentities);
			model.addAttribute("referencedResolutions",refresolutionentities);
			model.addAttribute("referencedEntities",refentities);
		}	
		/**** Clubbed motions are collected in references ****/
		List<ClubbedEntity> clubbedEntities=Motion.findClubbedEntitiesByPosition(domain);
		if(clubbedEntities!=null){
			List<Reference> references=new ArrayList<Reference>();
			StringBuffer buffer1=new StringBuffer();
			buffer1.append(memberNames+",");
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getMotion().getNumber()));
				reference.setNumber(String.valueOf(ce.getMotion().getId()));
				references.add(reference);
				String tempPrimary=ce.getMotion().getPrimaryMember().getFullname();
				if(!buffer1.toString().contains(tempPrimary)){
					buffer1.append(ce.getMotion().getPrimaryMember().getFullname()+",");
				}
				List<SupportingMember> clubbedSupportingMember=ce.getMotion().getSupportingMembers();
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
				model.addAttribute("clubbedEntities",references);
			}else{
				if(domain.getParent()!=null){
					model.addAttribute("formattedParentNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
					model.addAttribute("parent",domain.getParent().getId());
				}
			}
		}		
		/**** Populating Put up otions and Actors ****/
		if(userGroupId!=null&&!userGroupId.isEmpty()){
			UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(userGroupId));
			List<Reference> actors=new ArrayList<Reference>();
			if(userGroup.getUserGroupType().getType().equals("department")&&internalStatus.getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)){
				Status sendback=Status.findByType(ApplicationConstants.MOTION_RECOMMEND_SENDBACK, locale);
				actors=WorkflowConfig.findMotionActorsVO(domain,sendback , userGroup, Integer.parseInt(domain.getLevel()), locale);
			}else{
				actors=WorkflowConfig.findMotionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
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
	}

	private void populateInternalStatus(final ModelMap model,final Motion domain,final String usergroupType,final String locale) {	
		try{
			List<Status> internalStatuses=new ArrayList<Status>();
			DeviceType deviceType=domain.getType();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseType=domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"MOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				}
			}/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			}/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			}	
			/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
			}		
			/**** Internal Status****/
			model.addAttribute("internalStatuses",internalStatuses);	
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
	}

	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final Motion domain,final BindingResult result) {
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
				List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
				for(String i:strReferencedEntities){
					ReferencedEntity referencedEntity=ReferencedEntity.findById(ReferencedEntity.class, Long.parseLong(i));
					referencedEntities.add(referencedEntity);
				}
				domain.setReferencedEntities(referencedEntities);
			}
		}
		/**** Workflowdetails ****/
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,domain.getWorkflowDetailsId());
		String userGroupType=workflowDetails.getAssigneeUserGroupType();
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
		performAction(domain);	
		String bulkEdit=request.getParameter("bulkedit");
		if(bulkEdit==null||!bulkEdit.equals("yes")){
		/**** Complete Task ****/		
		String nextuser=domain.getActor();
		String level=domain.getLevel();
		Map<String,String> properties=new HashMap<String, String>();
		properties.put("pv_deviceId",String.valueOf(domain.getId()));
		properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
		if(nextuser!=null){
			if(!nextuser.isEmpty()){
				String[] temp=nextuser.split("#");
				properties.put("pv_user",temp[0]);
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
					WorkflowDetails workflowDetails2 = null;
					try {
						workflowDetails2 = WorkflowDetails.create(domain,newtask,ApplicationConstants.APPROVAL_WORKFLOW,level);
					} catch (ELSException e) {						
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
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
		domain.merge();
		model.addAttribute("type","success");
		populateModel(domain, model, request, workflowDetails);
		return "workflow/motion/"+userGroupType;
	}

	private void performAction(final Motion domain) {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		if(internalStatus.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)){
			performActionOnAdmission(domain);
		}
	}

	private void performActionOnAdmission(final Motion domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		/**** Setting revised subject,question text,revised reason,revised brief explaination if not already set ****/
		if(domain.getRevisedSubject()==null){			
			domain.setRevisedSubject(domain.getSubject());			
		}else if(domain.getRevisedSubject().isEmpty()){
			domain.setRevisedSubject(domain.getSubject());
		}
		if(domain.getRevisedDetails()==null){			
			domain.setRevisedDetails(domain.getDetails());			
		}else if(domain.getRevisedDetails().isEmpty()){
			domain.setRevisedDetails(domain.getDetails());
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
			if(domain.getRevisedDetails()!=null){
				if(!domain.getRevisedDetails().isEmpty()){
					details=domain.getRevisedDetails();
				}else{
					details=domain.getDetails();
				}
			}else{
				details=domain.getDetails();
			}
			Status status=Status.findByType(ApplicationConstants.MOTION_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Motion motion=i.getMotion();
				if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_CLUBBED)){
					motion.setRevisedSubject(subject);
					motion.setRevisedDetails(details);
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
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/bulkapproval/init",method=RequestMethod.POST)
	public String getBulkApprovalInit(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		
		try{
			/**** Request Params ****/
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			String strMotionType=request.getParameter("deviceType");
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
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"MOIS_ALLOWED_USERGROUPTYPES", "");
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
				}else if(i.getType().contains("MOIS_CLERK")){
					strRole=i.getType();
					break;
				}else if(i.getType().startsWith("MOIS_")){
					strRole=i.getType();
					break;
				}
			}		
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strMotionType!=null&&!(strMotionType.isEmpty())
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
					DeviceType motionType=DeviceType.findByFieldName(DeviceType.class,"name",strMotionType,strLocale);
					Status internalStatus=Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,motionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "MOTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"MOTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus!=null){
							internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						}
					}/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					}/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					}	
					/**** MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup!=null){
						internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}	
					model.addAttribute("internalStatuses",internalStatuses);
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
				model.addAttribute("workflowSubType",strWorkflowSubType);
			}
			return "workflow/motion/bulkapprovalinit";
		}catch(ELSException ee){
			logger.error(ee.getMessage());
			model.addAttribute("error", ee.getParameter());
			model.addAttribute("type", "error");
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("type", "error");
		}
		return "workflow/info";
	}	

	@RequestMapping(value="/bulkapproval/view",method=RequestMethod.POST)
	public String getBulkApprovalView(final HttpServletRequest request,final Locale locale,
			final Model model){
		populateBulkApprovalView(model,request,locale.toString());
		return "workflow/motion/bulkapprovalview";		
	}	

	@Transactional
	@RequestMapping(value="/bulkapproval/update",method=RequestMethod.POST)
	public String bulkApproval(final HttpServletRequest request,final Locale locale,
			final Model model){			
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus=request.getParameter("status");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		StringBuffer recommendAdmissionMsg=new StringBuffer();
		StringBuffer recommendRejectionMsg=new StringBuffer();
		StringBuffer admittedMsg=new StringBuffer();
		StringBuffer rejectedMsg=new StringBuffer();
		if(selectedItems != null && (selectedItems.length >0)
				&&strStatus!=null&&!strStatus.isEmpty()
				&&strWorkflowSubType!=null&&!strWorkflowSubType.isEmpty()) {
			Status status=null;
			if(!strStatus.equals("-")){
				status=Status.findById(Status.class,Long.parseLong(strStatus));
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
					Motion motion = Motion.findById(Motion.class,Long.parseLong(wfDetails.getDeviceId()));
					String actor=request.getParameter("actor");
					if(actor==null||actor.isEmpty()){
						actor=motion.getActor();
						String[] temp=actor.split("#");
						actor=temp[1];
					}
					String level=request.getParameter("level");
					if(level==null||level.isEmpty()){
						level=motion.getLevel();
						
					}
					
					if(actor!=null&&!actor.isEmpty()
							&&level!=null&&!level.isEmpty()){
						Reference reference = null;
						try {
							reference = UserGroup.findMotionActor(motion,actor,level,locale.toString());
						} catch (ELSException e) {							
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
						if(reference!=null
								&&reference.getId()!=null&&!reference.getId().isEmpty()
								&&reference.getName()!=null&&!reference.getName().isEmpty()){
							/**** Update Actor ****/
							String[] temp=reference.getId().split("#");
							motion.setActor(reference.getId());
							motion.setLocalizedActorName(temp[3]+"("+temp[4]+")");
							motion.setLevel(temp[2]);
							/**** Update Internal Status and Recommendation Status ****/
							if(status!=null){
							motion.setInternalStatus(status);
							motion.setRecommendationStatus(status);	
							}
							motion.setEndFlag("continue");
							/**** Complete Task ****/
							Map<String,String> properties=new HashMap<String, String>();
							properties.put("pv_deviceId",String.valueOf(motion.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(motion.getType().getId()));
							properties.put("pv_user",temp[0]);
							properties.put("pv_endflag",motion.getEndFlag());							
							String strTaskId=wfDetails.getTaskId();
							Task task=processService.findTaskById(strTaskId);
							processService.completeTask(task,properties);	
							if(motion.getEndFlag()!=null&&!motion.getEndFlag().isEmpty()
									&&motion.getEndFlag().equals("continue")){
								/**** Create New Workflow Details ****/
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask=processService.getCurrentTask(processInstance);
								WorkflowDetails workflowDetails2 = null;
								try {
									workflowDetails2 = WorkflowDetails.create(motion,newtask,ApplicationConstants.APPROVAL_WORKFLOW,level);
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
							if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION)){
								recommendAdmissionMsg.append(motion.formatNumber()+",");
							}else if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_RECOMMEND_REJECTION)){
								recommendRejectionMsg.append(motion.formatNumber()+",");
							}else if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)){
								admittedMsg.append(motion.formatNumber()+",");
							}else if(motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_REJECTION)){
								rejectedMsg.append(motion.formatNumber()+",");
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
		return "workflow/motion/bulkapprovalview";
	}

	private void populateBulkApprovalView(final Model model,
			final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strMotionType=request.getParameter("motionType");			
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
				&&strMotionType!=null&&!(strMotionType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUsergroupType!=null&&!(strUsergroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strFile!=null&&!(strFile.isEmpty())
				&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
			model.addAttribute("workflowSubType", strWorkflowSubType);
			/**** Workflow Details ****/
			List<WorkflowDetails> workflowDetails = new ArrayList<WorkflowDetails>();
			
			try{
				workflowDetails = WorkflowDetails.findAll(strHouseType,strSessionType,strSessionYear,
						strMotionType,strStatus,strWorkflowSubType,
						assignee,strItemsCount,strLocale,strFile);
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals=new ArrayList<BulkApprovalVO>();
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			for(WorkflowDetails i:workflowDetails){
				BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
				Motion motion=Motion.findById(Motion.class,Long.parseLong(i.getDeviceId()));
				/**** Bulk Submission for Supporting Members ****/
				if(i.getWorkflowSubType().equals("request_to_supporting_member")){
					bulkApprovalVO.setId(String.valueOf(i.getId()));
					bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));	
					bulkApprovalVO.setDeviceNumber("-");
					bulkApprovalVO.setDeviceType(motion.getType().getName());
					bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
					bulkApprovalVO.setSubject(motion.getSubject());							
					List<SupportingMember> supportingMembers=motion.getSupportingMembers();
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
							&&motion.getFile()!=null
							&&motion.getFile()==Integer.parseInt(strFile)){
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));				
						if(motion.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(motion.getType().getName());
						bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
						bulkApprovalVO.setSubject(motion.getSubject());
						if(motion.getRemarks()!=null&&!motion.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(motion.getRemarks());
						}else{
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(motion.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(motion.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}/**** Status Wise Bulk Submission ****/
					else if(strFile!=null&&!strFile.isEmpty()&&
							strFile.equals("-")
					){
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));				
						if(motion.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(motion.getType().getName());
						bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
						bulkApprovalVO.setSubject(motion.getSubject());
						if(motion.getRemarks()!=null&&!motion.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(motion.getRemarks());
						}else{
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
			if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
				model.addAttribute("motionId",bulkapprovals.get(0).getDeviceId());
			}
		}
	}
}
