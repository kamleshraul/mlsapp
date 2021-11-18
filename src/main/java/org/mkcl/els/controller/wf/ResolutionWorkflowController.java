package org.mkcl.els.controller.wf;

import java.io.UnsupportedEncodingException;
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
//import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.ResolutionDraft;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Rule;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
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
@Controller
@RequestMapping("/workflow/resolution")
public class ResolutionWorkflowController extends BaseController{
	
	/** The process service. */
	@Autowired
	private IProcessService processService;
	
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
	
	
	
	@RequestMapping(method=RequestMethod.GET)
	public String initMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		WorkflowDetails workflowDetails = null;
		try {
			/**** Workflowdetails ****/
			Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
			if(longWorkflowdetails==null){
				longWorkflowdetails=Long.parseLong(request.getParameter("workflowdetails"));
			}
			workflowDetails = WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
			/**** Adding workflowdetails and task to model ****/
			model.addAttribute("workflowdetails",workflowDetails.getId());
			model.addAttribute("workflowstatus",workflowDetails.getStatus());
			model.addAttribute("workflowHouseType",workflowDetails.getHouseType());
			Resolution domain=Resolution.findById(Resolution.class,Long.parseLong(workflowDetails.getDeviceId()));

			/**** Populate Model ****/		
			populateModel(domain,model,request,workflowDetails);
			
			findLatestRemarksByUserGroup(domain,model,request);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch(ELSException e){
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "** There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}		
		return workflowDetails.getForm();
	}

	private void populateModel(final Resolution domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) throws Exception {
		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit",request.getParameter("bulkedit"));
		Long referencedResolution = Resolution.findReferencedEntity(domain);
		if(referencedResolution != null){
			model.addAttribute("isRepeatWorkFlow", "yes");
		}else{
			model.addAttribute("isRepeatWorkFlow", "no");
		}
		/**** clear remarks ****/
		domain.setRemarks("");	

		/**** Locale ****/
		String locale=domain.getLocale();					

		/**** House Type ****/			
		model.addAttribute("formattedHouseType",domain.getHouseType().getName());
		model.addAttribute("houseType",domain.getHouseType().getId());
		model.addAttribute("houseTypeType",domain.getHouseType().getType());
		
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

		/**** Device Type ****/
		DeviceType deviceType=domain.getType();
		model.addAttribute("formattedDeviceType",deviceType.getName());
		model.addAttribute("deviceType",deviceType.getId());
		model.addAttribute("selectedDeviceType",deviceType.getType());

		

		/**** Primary Member ****/
		String memberName=null;
		Member member=domain.getMember();
		if(member!=null){
			model.addAttribute("member",member.getId());
			memberName=member.getFullname();
			model.addAttribute("formattedMember",memberName);
		}
		/**** Constituency ****/
		Long houseId=selectedSession.getHouse().getId();
		MasterVO constituency=null;
		if(domain.getHouseType().getType().equals("lowerhouse")){
			constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
			model.addAttribute("constituency",constituency.getName());
		}else if(domain.getHouseType().getType().equals("upperhouse")){
			Date currentDate=new Date();
			String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
			constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
			model.addAttribute("constituency",constituency.getName());
		}
		
		/**** Ministries ****/
		if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
			List<MemberMinister> memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, selectedSession, locale);
			/**** To check whether to populate other ministries also for this minister ****/
			Boolean isAllowedToAccessOtherMinistries = false;					
			CustomParameter rolesAllowedForAccessingOtherMinistriesParameter = CustomParameter.findByName(CustomParameter.class, "MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION", "");
			if(rolesAllowedForAccessingOtherMinistriesParameter != null) {
				if(rolesAllowedForAccessingOtherMinistriesParameter.getValue() != null && !rolesAllowedForAccessingOtherMinistriesParameter.getValue().isEmpty()) {
					List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(member, selectedSession, locale);
					for(MemberRole memberRole: memberRoles) {
						for(String allowedRole: rolesAllowedForAccessingOtherMinistriesParameter.getValue().split(",")) {
							if(memberRole.getType().trim().equals(allowedRole)) {
								isAllowedToAccessOtherMinistries = true;
								break;
							}
						}
						if(isAllowedToAccessOtherMinistries == true) {
							break;
						}
					}
				} else {
					logger.error("custom parameter 'RESOLUTIONS_GOVERNMENT_MEMBERROLES_SUBMISSIONFORANYMINISTRY' is not set properly");
					model.addAttribute("errorcode", "resolutions_government_memberroles_submissionforanyministry_notset");
				}
			} else {
				logger.error("custom parameter 'RESOLUTIONS_GOVERNMENT_MEMBERROLES_SUBMISSIONFORANYMINISTRY' is not set");
				model.addAttribute("errorcode", "resolutions_government_memberroles_submissionforanyministry_notset");
			}
			List<Ministry> assignedMinistries=new ArrayList<Ministry>();
			if(isAllowedToAccessOtherMinistries == true) {
				List<Ministry> memberMinistries = new ArrayList<Ministry>();
				List<Ministry> otherMinistries = new ArrayList<Ministry>();
				for(MemberMinister i:memberMinisters){
					memberMinistries.add(i.getMinistry());						
				}				
				//adding ministries of minister adding this resolution
				assignedMinistries.addAll(memberMinistries);
				//also adding ministries that do not belong to minister adding this resolution
				otherMinistries = Ministry.findAssignedMinistries(locale);
				otherMinistries.removeAll(memberMinistries);					
				assignedMinistries.addAll(otherMinistries);
			} else {
				for(MemberMinister i:memberMinisters){
					assignedMinistries.add(i.getMinistry());						
				}				
			}
			model.addAttribute("ministries",assignedMinistries);
			Ministry ministry=domain.getMinistry();
			if(ministry!=null){
				model.addAttribute("ministrySelected",ministry.getId());
				List<SubDepartment> assignedSubDepartments = 
						MemberMinister.findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
				model.addAttribute("subDepartments", assignedSubDepartments);
				SubDepartment subDepartment=domain.getSubDepartment();
				if(subDepartment!=null){
					model.addAttribute("subDepartmentSelected",subDepartment.getId());
				}
			}
			
		}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
			List<Ministry> ministries=Ministry.findAssignedMinistries(locale);
			model.addAttribute("ministries",ministries);
			Ministry ministry=domain.getMinistry();
			if(ministry!=null){
				model.addAttribute("ministrySelected",ministry.getId());
				/**** Sub Departments ****/
				List<SubDepartment> subDepartments=
						MemberMinister.findAssignedSubDepartments(ministry,selectedSession.getStartDate(), locale);
				model.addAttribute("subDepartments",subDepartments);
				SubDepartment subDepartment=domain.getSubDepartment();
				if(subDepartment!=null){
					model.addAttribute("subDepartmentSelected",subDepartment.getId());
				}
//				/**** Departments ****/
//				List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
//				model.addAttribute("departments",departments);
//				Department department=domain.getDepartment();
//				if(department!=null){  
//					model.addAttribute("departmentSelected",department.getId());
//					/**** Sub Departments ****/
//					List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,department, locale);
//					model.addAttribute("subDepartments",subDepartments); 
//					SubDepartment subDepartment=domain.getSubDepartment();
//					if(subDepartment!=null){
//						model.addAttribute("subDepartmentSelected",subDepartment.getId());
//					}
//				}
			}
		}
		
		/**** Submission Date and Creation date****/
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		String houseTypeForStatus = this.getCurrentUser().getHouseType();
		if(dateTimeFormat!=null){            
			if(domain.getSubmissionDate()!=null){
				model.addAttribute("submissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
			}
			if(domain.getCreationDate()!=null){
				model.addAttribute("creationDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
			}
			if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
				if(domain.getWorkflowStartedOnLowerHouse()!=null){
					model.addAttribute("workflowStartedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnLowerHouse()));
				}
				if(domain.getWorkflowStartedOnUpperHouse()!=null){
					model.addAttribute("workflowStartedOnDateUpperHouse", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnUpperHouse()));
				}else{
					model.addAttribute("workflowStartedOnDateUpperHouse",null); 
				}
				if(domain.getTaskReceivedOnLowerHouse()!=null){
					model.addAttribute("taskReceivedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnLowerHouse()));
				}
				if(domain.getTaskReceivedOnUpperHouse()!=null){
					model.addAttribute("taskReceivedOnDateUpperHouse", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnUpperHouse()));
				}else{
					model.addAttribute("taskReceivedOnDateUpperHouse",null); 
				}
			}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
				if(domain.getWorkflowStartedOnUpperHouse()!=null){
					model.addAttribute("workflowStartedOnDateUpperHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnUpperHouse()));
				}
				if(domain.getWorkflowStartedOnLowerHouse()!=null){
					model.addAttribute("workflowStartedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnLowerHouse()));
				}
				if(domain.getTaskReceivedOnUpperHouse()!=null){
					model.addAttribute("taskReceivedOnDateUpperHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnUpperHouse()));
				}
				if(domain.getTaskReceivedOnLowerHouse()!=null){
					model.addAttribute("taskReceivedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnLowerHouse()));
				}
			}
		}
		/**** Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());	

		/**** UserGroup and UserGroup Type ****/
		String userGroupId=workflowDetails.getAssigneeUserGroupId();
		model.addAttribute("usergroup",workflowDetails.getAssigneeUserGroupId());
		model.addAttribute("usergroupType",workflowDetails.getAssigneeUserGroupType());
		model.addAttribute("userGroupName", workflowDetails.getAssigneeUserGroupName());
		StringBuffer userName = new StringBuffer();
		userName.append(this.getCurrentUser().getTitle());
		userName.append(" ");
		userName.append(this.getCurrentUser().getFirstName());
		userName.append(" ");
		userName.append(this.getCurrentUser().getLastName());
		model.addAttribute("userName", userName.toString());
		model.addAttribute("login_username", this.getCurrentUser().getActualUsername());

		/**** To have the task creation date and lastReceivingDate if userGroup is department in case of starred questions ***/
		if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)){
			try{	
				CustomParameter serverTimeStamp=CustomParameter.findByName(CustomParameter.class,"SERVER_TIMESTAMP","");
				if(serverTimeStamp!=null){
					if(workflowDetails.getAssignmentTime() != null){							
						model.addAttribute("taskCreationDate", FormaterUtil.getDateFormatter(serverTimeStamp.getValue(),locale).format(workflowDetails.getAssignmentTime()));
					}
				}
		
			}catch(Exception e){
				logger.error("Last Receiving date from department or task creation date is missing.: "+e.getMessage());
			}
			
		}
		
		/**** discussion date in case of GR ****/
		if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
			/**** discussion date related things in case of GR ****/
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SPEAKER) || 
					workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.CHAIRMAN)){
				model.addAttribute("isDiscussionDateReadOnly", false);
				if(domain.getDiscussionDate() != null) {
					model.addAttribute("expectedDiscussionDate", FormaterUtil.formatDateToString(domain.getDiscussionDate(), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
			} else {
				model.addAttribute("isDiscussionDateReadOnly", true);
			}			
			Rule ruleForDiscussionDateSelected = domain.getRuleForDiscussionDate();
			if(ruleForDiscussionDateSelected!=null) {
				model.addAttribute("ruleForDiscussionDateSelected", ruleForDiscussionDateSelected.getId());
			}
		}

		/**** Status,Internal Status and recommendation Status ****/
		Status status=null;
		Status internalStatus=null;
		Status recommendationStatus=null;
		HouseType houseType = null;
		if(domain.getType()!=null) {
			if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				houseType = HouseType.findByFieldName(HouseType.class, "name", workflowDetails.getHouseType(), locale);
				houseTypeForStatus = houseType.getType();
				model.addAttribute("houseTypeForStatus", houseTypeForStatus);
			} else {
				houseType=domain.getHouseType();
				model.addAttribute("houseTypeForStatus", houseType.getType());
			}
		}
		if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
			
			if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
				status=domain.getStatusLowerHouse();
				internalStatus=domain.getInternalStatusLowerHouse();
				recommendationStatus=domain.getRecommendationStatusLowerHouse();
				Status statusUpperHouse = domain.getStatusUpperHouse();
				if(statusUpperHouse != null) {
					model.addAttribute("statusUpperHouse", statusUpperHouse.getId());
				} else {
					model.addAttribute("statusUpperHouse", null);
				}
				Status internalStatusUpperHouse = domain.getInternalStatusUpperHouse();
				if(internalStatusUpperHouse != null) {
					model.addAttribute("internalStatusUpperHouse", internalStatusUpperHouse.getId());
				} else {
					model.addAttribute("internalStatusUpperHouse", null);
				}
				Status recommendationStatusUpperHouse = domain.getRecommendationStatusUpperHouse();
				if(recommendationStatusUpperHouse != null) {
					model.addAttribute("recommendationStatusUpperHouse", recommendationStatusUpperHouse.getId());
				} else {
					model.addAttribute("recommendationStatusUpperHouse", null);
				}
			}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
				status=domain.getStatusUpperHouse();
				internalStatus=domain.getInternalStatusUpperHouse();
				recommendationStatus=domain.getRecommendationStatusUpperHouse();
				Status statusLowerHouse = domain.getStatusLowerHouse();
				if(statusLowerHouse != null) {
					model.addAttribute("statusLowerHouse", statusLowerHouse.getId());
				} else {
					model.addAttribute("statusLowerHouse", null);
				}
				Status internalStatusLowerHouse = domain.getInternalStatusLowerHouse();
				if(internalStatusLowerHouse != null) {
					model.addAttribute("internalStatusLowerHouse", internalStatusLowerHouse.getId());
				} else {
					model.addAttribute("internalStatusLowerHouse", null);
				}
				Status recommendationStatusLowerHouse = domain.getRecommendationStatusLowerHouse();
				if(recommendationStatusLowerHouse != null) {
					model.addAttribute("recommendationStatusLowerHouse", recommendationStatusLowerHouse.getId());
				} else {
					model.addAttribute("recommendationStatusLowerHouse", null);
				}
			}			
		}else if(domain.getType().getType().trim().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				status=domain.getStatusLowerHouse();
				internalStatus=domain.getInternalStatusLowerHouse();
				recommendationStatus=domain.getRecommendationStatusLowerHouse();
			}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				status=domain.getStatusUpperHouse();
				internalStatus=domain.getInternalStatusUpperHouse();
				recommendationStatus=domain.getRecommendationStatusUpperHouse();
			}
		}
		if(status!=null){
			model.addAttribute("status",status.getId());
		}
		if(internalStatus!=null){
			model.addAttribute("internalStatus",internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
			populateInternalStatus(model,domain,workflowDetails);
		}
		if(recommendationStatus!=null){
			model.addAttribute("recommendationStatus",recommendationStatus.getId());
			model.addAttribute("recommendationStatusType",recommendationStatus.getType());
			model.addAttribute("oldRecommendationStatus",recommendationStatus.getId());
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
		/**** Referenced Resolution are collected in refentities****/
		List<Reference> refentities=new ArrayList<Reference>();
		List<String> refentitiesSessionDevice = new ArrayList<String>();
		
		if(referencedResolution!=null){
			
			Reference reference=new Reference();
			reference.setId(String.valueOf(referencedResolution));
			Resolution refResolution = Resolution.findReferencedResolution(domain);
			if(refResolution != null){
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(refResolution.getNumber()));
				reference.setNumber(String.valueOf(refResolution.getId()));
				refentities.add(reference);
				Session referencedResolutionSession = refResolution.getSession();
				refentitiesSessionDevice.add("[" + referencedResolutionSession.getType().getSessionType()+", "+FormaterUtil.formatNumberNoGrouping(referencedResolutionSession.getYear(), locale) + "], " + refResolution.getType().getName());
			}
		}			
		model.addAttribute("referencedResolutions",refentities);
		model.addAttribute("referencedResolutionsSessionAndDevice", refentitiesSessionDevice);

		/**** level ****/
		model.addAttribute("level",workflowDetails.getAssigneeLevel());
		
		/**** setting the date of factual position receiving. ****/
		String userGroupType=workflowDetails.getAssigneeUserGroupType();
		if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
				internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
			if(domain.getLastDateOfFactualPositionReceiving() == null) {
				List<MasterVO> numberOfDaysForFactualPositionReceiving = new ArrayList<MasterVO>();
				String sessionParameter = selectedSession.getParameter("resolutions_nonofficial_numberOfDaysForFactualPositionReceiving");
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
			
			/**** setting the questions to be asked in factual position. ****/
			List<MasterVO> questionsToBeAskedInFactualPosition = new ArrayList<MasterVO>();
			String sessionParameter = selectedSession.getParameter("resolutions_nonofficial_questionsAskedForFactualPosition");
			if(sessionParameter != null) {
				if(!sessionParameter.isEmpty()) {
					for(String i : sessionParameter.split("##")) {	
						MasterVO questionToBeAskedInFactualPosition = new MasterVO();
						questionToBeAskedInFactualPosition.setName(i);
						questionToBeAskedInFactualPosition.setValue(i);
						if(domain.getQuestionsAskedInFactualPosition()!=null && !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
							for(String j : domain.getQuestionsAskedInFactualPosition().split("##")) {
								if(i.trim().equals(j.trim())) {
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
			
			String noOfRemainderSessionParameter=selectedSession.getParameter("resolutions_nonofficial_numberOfReminderMailForFactualPosition");
			if(noOfRemainderSessionParameter!=null && !noOfRemainderSessionParameter.isEmpty()){
				model.addAttribute("numberOfReminderMailForFactualPosition",Integer.parseInt(noOfRemainderSessionParameter)-1);
			}
		}
		
		
		/**** populating the questions asked in factual position to show to department. ****/
		if((userGroupType.equals(ApplicationConstants.DEPARTMENT)) && (internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
				internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
			String questionsAskedInFactualPosition = "";
			if(domain.getQuestionsAskedInFactualPosition() !=null && !domain.getQuestionsAskedInFactualPosition().isEmpty()) {
				int count = 1;
				for(String i: domain.getQuestionsAskedInFactualPosition().split("##")) {
					questionsAskedInFactualPosition += FormaterUtil.formatNumberNoGrouping(count, domain.getLocale()) + ". " + i;
					count++;
				}
			}
			model.addAttribute("questionsAskedInFactualPosition", questionsAskedInFactualPosition);
		}

		/**** Populating Put up otions and Actors ****/
		if(userGroupId!=null&&!userGroupId.isEmpty()){
			UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(userGroupId));
			List<Reference> actors=new ArrayList<Reference>();
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				if(userGroup.getUserGroupType().getType().equals("department")&&internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)){
					Status sendback=Status.findByType(ApplicationConstants.RESOLUTION_RECOMMEND_SENDBACK, locale);
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						actors=WorkflowConfig.findResolutionActorsVO(domain,sendback , userGroup, Integer.parseInt(domain.getLevelLowerHouse()),houseTypeForStatus, locale);
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						actors=WorkflowConfig.findResolutionActorsVO(domain,sendback , userGroup, Integer.parseInt(domain.getLevelUpperHouse()),houseTypeForStatus, locale);
					}
					
				}else{
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						actors=WorkflowConfig.findResolutionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevelLowerHouse()),houseTypeForStatus, locale);
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						actors=WorkflowConfig.findResolutionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevelUpperHouse()),houseTypeForStatus, locale);
					}
				}
			}else if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						actors=WorkflowConfig.findResolutionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevelLowerHouse()),houseType.getName(), locale);
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						actors=WorkflowConfig.findResolutionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevelUpperHouse()),houseType.getName(), locale);
					}
				
			}
			
			model.addAttribute("internalStatusSelected",internalStatus.getId());
			model.addAttribute("actors",actors);
			if(actors!=null&&!actors.isEmpty()){
				String nextActor=actors.get(0).getId();
				String[] actorArr=nextActor.split("#");
				if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
					domain.setLevelLowerHouse(actorArr[2]);
					domain.setLocalizedActorNameLowerHouse(actorArr[3]+"("+actorArr[4]+")");
				}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
					domain.setLevelUpperHouse(actorArr[2]);
					domain.setLocalizedActorNameUpperHouse(actorArr[3]+"("+actorArr[4]+")");
				}
			}
			if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
				model.addAttribute("actorSelected", domain.getActorLowerHouse());
			}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
				model.addAttribute("actorSelected", domain.getActorUpperHouse());
			}
			
		}
		/**** process variables ****/
		//default values for process variables. can set conditionally for given actor here.
		model.addAttribute("pv_mailflag", "off");
		
		if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
				internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)) && recommendationStatus.getType().equals(ApplicationConstants.RESOLUTION_PROCESSED_SENDTOSECTIONOFFICER)) {
			model.addAttribute("pv_timerflag", "set");
			model.addAttribute("pv_reminderflag", "set");
			Credential sender = Credential.findByFieldName(Credential.class, "username", workflowDetails.getAssignee(), "");
			model.addAttribute("pv_reminderfrom", sender.getEmail());
			String reminderContent = selectedSession.getParameter("resolutions_nonofficial_questionsAskedForFactualPosition");
			reminderContent += "\n" + domain.getRevisedNoticeContent();
			model.addAttribute("pv_remindercontent", reminderContent);
			String reminderSubject = "reminder about resolution number " + domain.getNumber();
			model.addAttribute("pv_remindersubject", reminderSubject);
			String lastTimerDuration = selectedSession.getParameter("resolutions_nonofficial_reminderDayNumberForFactualPosition");
			model.addAttribute("pv_lasttimerduration", lastTimerDuration);
		} else {
			model.addAttribute("pv_timerflag", "off");
			model.addAttribute("pv_reminderflag", "off");
		}
		
		/**** add domain to model ****/
		model.addAttribute("domain",domain);
		
	}

	private void populateInternalStatus(final ModelMap model,final Resolution resolution,final WorkflowDetails workflowDetails) {	
		List<Status> internalStatuses=new ArrayList<Status>();
		Status internalStatus=null;
		HouseType houseType = null;
		if(resolution.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			houseType = HouseType.findByFieldName(HouseType.class, "name", workflowDetails.getHouseType(), resolution.getLocale());
		} else {
			houseType=resolution.getHouseType();
		}
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			internalStatus=resolution.getInternalStatusLowerHouse();
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			internalStatus=resolution.getInternalStatusUpperHouse();
		}
		
		String type=internalStatus.getType();
		String userGroupType=workflowDetails.getAssigneeUserGroupType();
		String locale=resolution.getLocale();
		String resolutionType=resolution.getType().getType();
		/**** First we will check if custom parameter for internal status and usergroupType has been set ****/
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			if(type.equals(ApplicationConstants.RESOLUTION_RECOMMEND_ADMISSION)&&
					(resolution.getRecommendationStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_DISCUSS)
							||resolution.getRecommendationStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_SENDBACK))){
				CustomParameter assistantSendBackDiscuss=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(assistantSendBackDiscuss!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(assistantSendBackDiscuss.getValue(), locale);
					} catch (ELSException e) {						
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}else{
						model.addAttribute("errorcode","resolution_putup_options_final_notset");
					}	
				}			
			}
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			if(type.equals(ApplicationConstants.RESOLUTION_RECOMMEND_ADMISSION)&&
					(resolution.getRecommendationStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_DISCUSS)
							||resolution.getRecommendationStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_SENDBACK))){
				CustomParameter assistantSendBackDiscuss=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(assistantSendBackDiscuss!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(assistantSendBackDiscuss.getValue(), locale);
					} catch (ELSException e) {
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("errorcode","resolution_putup_options_final_notset");
							model.addAttribute("error", e.getParameter());
						}
					}else{
						model.addAttribute("errorcode","resolution_putup_options_final_notset");
					}	
				}			
			}
		}
		CustomParameter specificDeviceRecommendationStatusUG=null;
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			specificDeviceRecommendationStatusUG=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+resolution.getRecommendationStatusLowerHouse().getType().toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			specificDeviceRecommendationStatusUG=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+resolution.getRecommendationStatusUpperHouse().getType().toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		}
		CustomParameter specificDeviceStatusHouseTypeUserGroupStatuses = CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+type.toUpperCase()+"_"+resolution.getHouseType().getType().toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		try{
			if(specificDeviceRecommendationStatusUG!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceRecommendationStatusUG.getValue(), locale);
			}else if(specificDeviceStatusHouseTypeUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceStatusHouseTypeUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceStatusUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			}else if(specificStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)){
				CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus!=null){
						internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter!=null){
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode","resolution_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus!=null){
					internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode","resolution_putup_options_final_notset");
					}		
				}
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
			final Locale locale,@Valid @ModelAttribute("domain") final Resolution domain,final BindingResult result) {
		
		String userGroupType = null;
		try{
			/**** Workflowdetails ****/
			String strWorkflowdetails=(String) request.getParameter("workflowdetails");
			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
			Resolution resolution = null;
			/**** Updating domain ****/
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
			String strDiscussionDate = request.getParameter("discussionDate");
			
			if(request.getParameter("bulkedit")!=null&&!request.getParameter("bulkedit").isEmpty()){
				request.getSession().setAttribute("bulkedit",request.getParameter("bulkedit"));
			}
			
			/**** Is Clarification of Question Received or not *************/
			boolean boolClarificationStatus = false;
			String clarificationStatus = request.getParameter("clarificationStatus");
			if(clarificationStatus != null && !clarificationStatus.isEmpty()){
				boolClarificationStatus = true;
			}
			/**** updating submission date and creation date ****/
			String strCreationDate=request.getParameter("setCreationDate");
			String strSubmissionDate=request.getParameter("setSubmissionDate");
			String strWorkflowStartedOnDate=null;
			String strTaskReceivedOnDate=null;
			HouseType houseType = null;
			if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				houseType = HouseType.findByFieldName(HouseType.class, "name", workflowDetails.getHouseType(), domain.getLocale());
			} else {
				houseType=domain.getHouseType();
			}
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDateLowerHouse");
				strTaskReceivedOnDate=request.getParameter("taskReceivedOnDateLowerHouse");
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDateUpperHouse");
				strTaskReceivedOnDate=request.getParameter("taskReceivedOnDateUpperHouse");
			}
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
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
							domain.setWorkflowStartedOnLowerHouse(format.parse(strWorkflowStartedOnDate));
						}
						if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
							domain.setTaskReceivedOnLowerHouse(format.parse(strTaskReceivedOnDate));
						}
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
							domain.setWorkflowStartedOnUpperHouse(format.parse(strWorkflowStartedOnDate));
						}
						if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
							domain.setTaskReceivedOnUpperHouse(format.parse(strTaskReceivedOnDate));
						}
					}
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			
			
			/**** setting the date of factual position receiving. ****/
			userGroupType=workflowDetails.getAssigneeUserGroupType();
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
						domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
					if(domain.getLastDateOfFactualPositionReceiving() == null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.DATE, domain.getNumberOfDaysForFactualPositionReceiving());
						domain.setLastDateOfFactualPositionReceiving(calendar.getTime());
					}
				}
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
						domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
					if(domain.getLastDateOfFactualPositionReceiving() == null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.DATE, domain.getNumberOfDaysForFactualPositionReceiving());
						domain.setLastDateOfFactualPositionReceiving(calendar.getTime());
					}
				}
			}
			
			{
				/* Find if next actors are not active then create a draft for them if draft is 
				 * not existing for that actors.
				 */
				try{
					String strNextuser = "";
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						strNextuser=request.getParameter("actorLowerHouse");
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						strNextuser=request.getParameter("actorUpperHouse");
					}
					String[] nextuser = null;
					int nextUserLevel = 0;
					if(strNextuser != null && !strNextuser.isEmpty()){
							nextuser = strNextuser.split("#");
							nextUserLevel = Integer.parseInt(nextuser[2]);
					} 
											
										
					if(domain.getId() != null){
						resolution = Resolution.findById(Resolution.class, domain.getId());
					}else{
						resolution = domain.copyResolution();
					}
					
					
					Map<String, String[]> params = new HashMap<String, String[]>();
					params.put("locale", new String[]{domain.getLocale().toString()});
					params.put("houseTypeId", new String[]{domain.getHouseType().getId().toString()});
					//params.put("deviceTypeName", new String[]{domain.getType().getName()});
					params.put("ugType", new String[]{ApplicationConstants.ASSISTANT});
					List data = Query.findReport(domain.getType().getType().toUpperCase()+"_ACTIVE_USER", params);
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
						List<Reference> refs = null;
						if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							refs = WorkflowConfig.
									findResolutionActorsVO(resolution,domain.getInternalStatusLowerHouse(),
											assistant,1,domain.getHouseType().getName(),locale.toString());
						} else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							refs = WorkflowConfig.
									findResolutionActorsVO(resolution,domain.getInternalStatusUpperHouse(),
											assistant,1,domain.getHouseType().getName(),locale.toString());
						}
						
						List<ResolutionDraft> ogDrafts = resolution.getDrafts();					
					
						for(Reference ref : refs){
							
							String[] user = ref.getId().split("#");
							
							if(!user[1].equals(ApplicationConstants.MEMBER) && !user[1].equals(ApplicationConstants.DEPARTMENT) && !user[1].equals(ApplicationConstants.DEPARTMENT_DESKOFFICER) && !ref.getState().equals(ApplicationConstants.ACTOR_ACTIVE)){
								
								int refLevel = Integer.parseInt(user[2]);
								
								if(refLevel < nextUserLevel){
									boolean foundUsersDraft = false;
									if(ogDrafts != null && !ogDrafts.isEmpty()){
										for(ResolutionDraft qd : ogDrafts){
											if(qd.getEditedAs().equals(user[3]) 
													&& qd.getEditedBy().equals(user[0])){
												foundUsersDraft = true;
												break;
											}
										}
										
										if(!foundUsersDraft){
											ResolutionDraft qdn = Resolution.addDraft(resolution, user[0], user[3], ref.getRemark());
											ogDrafts.add(qdn);
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return "redirect:workflow/myTasks/"+workflowDetails.getId()+"/process";
				}
			}
			
			performAction(domain, workflowDetails);		
			String bulkEdit=request.getParameter("bulkedit");
			if(!bulkEdit.equals("yes")){
				domain.merge();
			}
			
			
			/**** set housetype & statuses in draft for government resolution ****/
			if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				ResolutionDraft latestDraft;
				try {
					latestDraft = Resolution.getLatestResolutionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
					
					String internalStatusType = domain.getInternalStatusLowerHouse().getType();
					if(!(internalStatusType.equals(ApplicationConstants.RESOLUTION_INCOMPLETE)) && !(internalStatusType.equals(ApplicationConstants.RESOLUTION_COMPLETE))) {				
						if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){					
							latestDraft.setStatus(domain.getStatusLowerHouse());
							latestDraft.setInternalStatus(domain.getInternalStatusLowerHouse());
							latestDraft.setRecommendationStatus(domain.getRecommendationStatusLowerHouse());					
						} else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){					
							latestDraft.setStatus(domain.getStatusUpperHouse());
							latestDraft.setInternalStatus(domain.getInternalStatusUpperHouse());
							latestDraft.setRecommendationStatus(domain.getRecommendationStatusUpperHouse());					
						}
						latestDraft.setHouseType(houseType);
						latestDraft.setDiscussionDate(domain.getDiscussionDate());
						latestDraft.merge();
					}	
				} catch (ELSException e) {				
					e.printStackTrace();
					model.addAttribute("error", e.getParameter());
				}		
			}
			
			/**** set statuses in draft in case when clarification is not received for factual position in non-official resolution ****/
			if(domain.getType().getType().trim().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					if(domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNOTRECEIVEDFROMDEPARTMENT)) {					
						ResolutionDraft latestDraft;
						try {
							latestDraft = Resolution.getLatestResolutionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
							latestDraft.setInternalStatus(domain.getStatusLowerHouse());
							latestDraft.setRecommendationStatus(domain.getStatusLowerHouse());
							latestDraft.merge();
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}					
		            }			
				} else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
					if(domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNOTRECEIVEDFROMDEPARTMENT)) {
						ResolutionDraft latestDraft;
						try {
							latestDraft = Resolution.getLatestResolutionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
							latestDraft.setInternalStatus(domain.getStatusUpperHouse());
							latestDraft.setRecommendationStatus(domain.getStatusUpperHouse());
							latestDraft.merge();
						} catch (ELSException e) {
							e.printStackTrace();
						}					
		            }
				}
			}
			
			boolean isMinistryChanged = false;
			boolean isSubdepartmentChanged =false;
			String strPreviousMinistrySelected = request.getParameter("ministrySelected");
			String strPreviousSubDepartmentSelected = request.getParameter("subDepartmentSelected");
			Ministry prevMinistry = null;
			if(strPreviousMinistrySelected != null && !strPreviousMinistrySelected.equals("")){
				prevMinistry = Ministry.findById(Ministry.class, Long.parseLong(strPreviousMinistrySelected));
			}else{
				prevMinistry = domain.getMinistry();
			}
			
			SubDepartment prevSubdepartment = null;
			if(strPreviousSubDepartmentSelected != null && !strPreviousSubDepartmentSelected.equals("")){
				prevSubdepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strPreviousSubDepartmentSelected));
			}else{
				prevSubdepartment = domain.getSubDepartment();
			}
			
			
			if(prevMinistry!=null && !prevMinistry.equals(domain.getMinistry())){
				Resolution.onMinistryChange(resolution, prevMinistry);
				isMinistryChanged = true;
			}
			if(prevSubdepartment!=null && !prevSubdepartment.equals(domain.getSubDepartment())){
				Resolution.onSubdepartmentChange(resolution, prevSubdepartment);
				isSubdepartmentChanged = true;
			}
			
			if(isMinistryChanged || isSubdepartmentChanged) {
				/**** display message ****/
				model.addAttribute("type","taskcompleted");
				return "workflow/info";
			}
			else {
				
				/**** Complete Task ****/	
				String nextuser=null;
				String level=null;
				String endflag=null;
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					 nextuser=domain.getActorLowerHouse();
					 level=domain.getLevelLowerHouse();
					 endflag=domain.getEndFlagLowerHouse();
				}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
					 nextuser=domain.getActorUpperHouse();
					 level=domain.getLevelUpperHouse();
					 endflag=domain.getEndFlagUpperHouse();
				}
				if(boolClarificationStatus && workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)){
					endflag = "continue";
				}
				
				Map<String,String> properties=new HashMap<String, String>();
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
										if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
											if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
													domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
												Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
												reminderto = recepient.getEmail();								
											} else {
												reminderto=request.getParameter("reminderto");								
											}
										}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
											if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
													domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
												Credential recepient = Credential.findByFieldName(Credential.class, "username", username, "");
												reminderto = recepient.getEmail();
											} else {
												reminderto=request.getParameter("reminderto");
											}
										}						
										properties.put("pv_reminderto", reminderto);
										
										String remindersubject=request.getParameter("remindersubject");						
										properties.put("pv_remindersubject", remindersubject);
										
										String remindercontent = "";
										if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
											if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
													domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
												remindercontent += domain.getRevisedNoticeContent() + "\n\n";
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
										}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
											if((userGroupType.equals(ApplicationConstants.SECTION_OFFICER)) && (domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT) ||
													domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
												
											} else {
												remindercontent=request.getParameter("remindercontent");
											}
										}						
										properties.put("pv_remindercontent", remindercontent);						
									}
									
									String noOfReminderMail=request.getParameter("numberOfReminderMailForFactualPosition");
									String timeDuration="";
									if(noOfReminderMail!=null && !noOfReminderMail.isEmpty()){
										int numberOfReminderMail=Integer.parseInt(noOfReminderMail);
										properties.put("pv_numberOfReminderMailForFactualPosition",noOfReminderMail);
										for(int i=1;i<=numberOfReminderMail;i++){
											String timeDurationForReminderMail=request.getParameter("remainderMailDifference"+i);
											timeDuration=timeDuration+"PT"+timeDurationForReminderMail+"M";
											if(i+1<=numberOfReminderMail){
												timeDuration=timeDuration+",";
											}
											
										}
										properties.put("pv_remaindermailduration",timeDuration);
									}
								}
								
								
							}
						}
					}else{
					properties.put("pv_mailflag", "off");
					properties.put("pv_timerflag", "off");
					}
				}
				if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
					properties.put("pv_houseType", houseType.getType());
				}
				if(!bulkEdit.equals("yes")){
					if(boolClarificationStatus){
						/**** Process Started and task created ****/
						List<WorkflowDetails> pendingWorkflows = WorkflowDetails.findPendingWorkflowDetails(domain, workflowDetails.getWorkflowType());
						for(WorkflowDetails wd : pendingWorkflows){
							Task prevTask = processService.findTaskById(wd.getTaskId());
							processService.completeTask(prevTask, properties);
							wd.setStatus("TIMEOUT");
							wd.setCompletionTime(new Date());
							wd.merge();
						}
					}else{
						String strTaskId=workflowDetails.getTaskId();
						Task task=processService.findTaskById(strTaskId);
						processService.completeTask(task,properties);
						
						/**** Workflow Detail entry made only if its not the end of workflow ****/
						if(endflag!=null && !endflag.isEmpty()){
							if(endflag.equals("continue")){
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask=processService.getCurrentTask(processInstance);
		
								//houseType for workflow as GR has workflows for both houseTypes
								HouseType houseTypeForWorkflow = null;
								String workflowHouseType = request.getParameter("workflowHouseType");
								if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
									if(workflowHouseType != null) {
										if(!workflowHouseType.isEmpty()) {
											//houseType of current workflow
											houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "name", workflowHouseType, domain.getLocale());
										} else {
											logger.error("request parameter workflowHouseType is empty");
										}
									} else {
										logger.error("request parameter workflowHouseType is null");
									}
								} else {
									//same as that of domain resolution
									houseTypeForWorkflow = domain.getHouseType(); 
								}
								WorkflowDetails workflowDetails2;
								try {
									workflowDetails2 = WorkflowDetails.create(domain,newtask,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,level,houseTypeForWorkflow);
									if(houseTypeForWorkflow.getType().equals(ApplicationConstants.LOWER_HOUSE)){
										domain.setWorkflowDetailsIdLowerHouse(workflowDetails2.getId());
										domain.setTaskReceivedOnLowerHouse(new Date());
									}else if(houseTypeForWorkflow.getType().equals(ApplicationConstants.UPPER_HOUSE)){
										
										domain.setWorkflowDetailsIdUpperHouse(workflowDetails2.getId());
										domain.setTaskReceivedOnUpperHouse(new Date());
									}
								} catch (ELSException e) {							
									e.printStackTrace();
									model.addAttribute("error", e.getParameter());
									model.addAttribute("type", "error");
								}						
							}
						}
						workflowDetails.setStatus("COMPLETED");
						workflowDetails.setCompletionTime(new Date());
						workflowDetails.merge();
					}
					/**** display message ****/
					model.addAttribute("type","taskcompleted");
					return "workflow/info";
					
				}
			domain.merge();
			model.addAttribute("type","success");
			populateModel(domain, model, request, workflowDetails);
			}
		}catch(ELSException e){
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			e.printStackTrace();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
		}
		return "workflow/resolution/"+userGroupType;
		
	}
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/bulkapproval/init",method=RequestMethod.POST)
	public String getBulkApprovalInit(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDeviceType=request.getParameter("deviceType");
		String strStatus=request.getParameter("status");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String strItemsCount=request.getParameter("itemsCount");
		String strFile=request.getParameter("file");
		String strLocale=locale.toString();
		/**** usergroup,usergroupType,role *****/
		List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
		Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
		String strUserGroupType=null;
		String strUsergroup=null;
		if(userGroups!=null){
			if(!userGroups.isEmpty()){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROIS_ALLOWED_USERGROUPTYPES", "");
				if(customParameter!=null){
					String allowedUserGroups=customParameter.getValue(); 
					for(UserGroup i:userGroups){
						if(allowedUserGroups.contains(i.getUserGroupType().getType())){
							UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
							if(ug != null){
								strUsergroup=String.valueOf(i.getId());
								strUserGroupType=i.getUserGroupType().getType();
								break;
							}
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
			}else if(i.getType().contains("ROIS_CLERK")){
				strRole=i.getType();
				break;
			}else if(i.getType().startsWith("ROIS_")){
				strRole=i.getType();
				break;
			}
		}		
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strDeviceType!=null&&!(strDeviceType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strFile!=null&&!(strFile.isEmpty())
				&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
			/**** List of Statuses ****/
				List<Status> internalStatuses=new ArrayList<Status>();
				HouseType houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, strLocale);
				DeviceType motionType=DeviceType.findByFieldName(DeviceType.class,"name",strDeviceType,strLocale);
				Status internalStatus=Status.findByType(strWorkflowSubType, strLocale);
				CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,motionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
				CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "RESOLUTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
				CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "RESOLUTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
				CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "RESOLUTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
				if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
					CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
					if(finalApprovingAuthorityStatus!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
						} catch (ELSException e) {							
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}
				}/**** RESOLUTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
				else if(deviceTypeInternalStatusUsergroup!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
					} catch (ELSException e) {						
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}/**** RESOLUTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
				else if(deviceTypeHouseTypeUsergroup!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
					} catch (ELSException e) {
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}	
				/**** RESOLUTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
				else if(deviceTypeUsergroup!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					} catch (ELSException e) {
						e.printStackTrace();
						model.addAttribute("error", e.getParameter());
					}
				}	
				model.addAttribute("internalStatuses",internalStatuses);
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("deviceType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUserGroupType);
			model.addAttribute("itemscount", strItemsCount);
			model.addAttribute("file", strFile);
			model.addAttribute("workflowSubType",strWorkflowSubType);
		
		return "workflow/resolution/bulkapprovalinit";		
	}	

	@RequestMapping(value="/bulkapproval/view",method=RequestMethod.POST)
	public String getBulkApprovalView(final HttpServletRequest request,final Locale locale,
			final Model model){
		populateBulkApprovalView(model,request,locale.toString());
		return "workflow/resolution/bulkapprovalview";		
	}	

	@Transactional
	@RequestMapping(value="/bulkapproval/update",method=RequestMethod.POST)
	public String bulkApproval(final HttpServletRequest request,final Locale locale,
			final Model model){			
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus=request.getParameter("status");
		String strWorkflowSubType=request.getParameter("workflowSubType");
		String refText = request.getParameter("refertext");
		String remark = request.getParameter("remarks");
		String strFile = request.getParameter("file");
		StringBuffer recommendAdmissionMsg=new StringBuffer();
		StringBuffer recommendRejectionMsg=new StringBuffer();
		StringBuffer recommendClarificationFromDepartmentMsg=new StringBuffer();
		StringBuffer recommendClarificationFromMemberMsg=new StringBuffer();
		StringBuffer admittedMsg=new StringBuffer();
		StringBuffer rejectedMsg=new StringBuffer();
		StringBuffer clarificationNeededFromDepartmentMsg=new StringBuffer();
		StringBuffer clarificationNeededFromMemberMsg=new StringBuffer();
		Status internalStatus=null;
		if(selectedItems != null && (selectedItems.length >0)
				&&strStatus!=null&&!strStatus.isEmpty()
				&&strWorkflowSubType!=null&&!strWorkflowSubType.isEmpty()) {
			Status status=null;
			if(!strStatus.equals("-")){
				status=Status.findById(Status.class,Long.parseLong(strStatus));
			}
			for(String i : selectedItems) {
					HouseType houseType=null;
					Long id = Long.parseLong(i);
					WorkflowDetails wfDetails=WorkflowDetails.findById(WorkflowDetails.class,id);
					
					Resolution resolution = Resolution.findById(Resolution.class,Long.parseLong(wfDetails.getDeviceId()));
					if(resolution.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
						houseType = HouseType.findByFieldName(HouseType.class, "name", wfDetails.getHouseType(), resolution.getLocale());
					} else {
						houseType=resolution.getHouseType();
					}
					String actor=request.getParameter("actor");
					if(actor==null||actor.isEmpty()){
						if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
							actor=resolution.getActorLowerHouse();
						}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
							actor=resolution.getActorUpperHouse();
						}
						
						String[] temp=actor.split("#");
						actor=temp[1];
					}
					String level=request.getParameter("level");
					if(level==null||level.isEmpty()){
						if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
							level=resolution.getLevelLowerHouse();
						}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
							level=resolution.getLevelUpperHouse();
						}
					}
					if(actor!=null&&!actor.isEmpty()
							&&level!=null&&!level.isEmpty()){
						Reference reference=null;
						if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
							try {
								reference=UserGroup.findResolutionActor(resolution,houseType.getType(),actor,level,locale.toString());
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
						}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
							try {
								reference=UserGroup.findResolutionActor(resolution,houseType.getName(),actor,level,locale.toString());
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
						}
						if(reference!=null
								&&reference.getId()!=null&&!reference.getId().isEmpty()
								&&reference.getName()!=null&&!reference.getName().isEmpty()){
							/**** Update Actor ****/
							String[] temp=reference.getId().split("#");
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								resolution.setActorLowerHouse(reference.getId());
								resolution.setLocalizedActorNameLowerHouse(temp[3]+"("+temp[4]+")");
								resolution.setLevelLowerHouse(temp[2]);
							}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
								resolution.setActorUpperHouse(reference.getId());
								resolution.setLocalizedActorNameUpperHouse(temp[3]+"("+temp[4]+")");
								resolution.setLevelUpperHouse(temp[2]);
							}
							
							/**** Update Internal Status and Recommendation Status ****/
							if(status!=null){
								if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									resolution.setInternalStatusLowerHouse(status);
									resolution.setRecommendationStatusLowerHouse(status);	
								}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									resolution.setInternalStatusUpperHouse(status);
									resolution.setRecommendationStatusUpperHouse(status);	
								}
							}
							String endFlag=null;
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								resolution.setEndFlagLowerHouse("continue");
								endFlag=resolution.getEndFlagLowerHouse();
								
							}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
								resolution.setEndFlagUpperHouse("continue");
								endFlag=resolution.getEndFlagUpperHouse();
							}
							
							
							/**** Complete Task ****/
							Map<String,String> properties=new HashMap<String, String>();
							properties.put("pv_deviceId",String.valueOf(resolution.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(resolution.getType().getId()));
							properties.put("pv_user",temp[0]);
							properties.put("pv_endflag",endFlag);
							
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
													
													String reminderto=request.getParameter("reminderto");
													properties.put("pv_reminderto", reminderto);
													
													//String remindersubject=request.getParameter("remindersubject");
													String remindersubject=resolution.getSubject();
													properties.put("pv_remindersubject", remindersubject);
													
													//String remindercontent=request.getParameter("remindercontent");
													String remindercontent=resolution.getNoticeContent();
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

							String strTaskId=wfDetails.getTaskId();
							Task task=processService.findTaskById(strTaskId);
							processService.completeTask(task,properties);	
							if(endFlag!=null&&!endFlag.isEmpty()
									&&endFlag.equals("continue")){
								/**** Create New Workflow Details ****/
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask=processService.getCurrentTask(processInstance);
								WorkflowDetails workflowDetails2;
								try {
									workflowDetails2 = WorkflowDetails.create(resolution,newtask,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,level,houseType);
									if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
										resolution.setWorkflowDetailsIdLowerHouse(workflowDetails2.getId());
										resolution.setTaskReceivedOnLowerHouse(new Date());
										
									}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
										resolution.setWorkflowDetailsIdUpperHouse(workflowDetails2.getId());
										resolution.setTaskReceivedOnUpperHouse(new Date());
									}
								} catch (ELSException e) {
									e.printStackTrace();
									model.addAttribute("error", e.getParameter());
								}
																
							}
							/**** Update Old Workflow Details ****/
							wfDetails.setStatus("COMPLETED");
							if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								wfDetails.setInternalStatus(resolution.getInternalStatusLowerHouse().getName());
								wfDetails.setRecommendationStatus(resolution.getRecommendationStatusLowerHouse().getName());;	
							}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
								wfDetails.setInternalStatus(resolution.getInternalStatusUpperHouse().getName());
								wfDetails.setRecommendationStatus(resolution.getRecommendationStatusUpperHouse().getName());	
							}
							
							wfDetails.setCompletionTime(new Date());
							wfDetails.merge();
							/**** Update Resolution ****/
							resolution.setEditedOn(new Date());
							resolution.setEditedBy(this.getCurrentUser().getActualUsername());
							resolution.setEditedAs(wfDetails.getAssigneeUserGroupName());	
							
							if(refText != null && !refText.isEmpty()){
								resolution.setReferencedResolutionText(refText);
							}
							if(remark != null && !remark.isEmpty()){
								resolution.setRemarks(remark);
							}
							
							if(strFile != null && !strFile.isEmpty() && !strFile.equals("-")){
								if(resolution.getFile() == null){
									resolution.setFile(new Integer(strFile));
								}
							}
													
							try {
								performAction(resolution, wfDetails);
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
//							/***Setting the edited On , Edited By and Edited As***/
//							List<UserGroup> usergroups = this.getCurrentUser().getUserGroups();
//							Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getUsername(), locale.toString());
//							resolution.setEditedBy(credential.getUsername());
//							for(UserGroup u : usergroups){
//								UserGroup userGroup = UserGroup.findActive(credential, u.getUserGroupType(), new Date(), locale.toString());
//								if(userGroup != null){
//									UserGroupType userGroupType = userGroup.getUserGroupType();
//									if(userGroupType != null){
//										resolution.setEditedAs(userGroupType.getName());
//									}
//								}
//							}
							resolution.setEditedOn(new Date());
							resolution.merge();
							
							if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								internalStatus=resolution.getInternalStatusLowerHouse();	
							}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
								internalStatus=resolution.getInternalStatusUpperHouse();	
							}
							if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_ADMISSION)){
								recommendAdmissionMsg.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REJECTION)){
								recommendRejectionMsg.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_ADMISSION)){
								admittedMsg.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_REJECTION)){
								rejectedMsg.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
								recommendClarificationFromDepartmentMsg.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
								recommendClarificationFromMemberMsg.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)){
								clarificationNeededFromDepartmentMsg.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)){
								clarificationNeededFromMemberMsg.append(resolution.formatNumber()+",");
							}
						}
					}					
				}				
			}			
		model.addAttribute("recommendAdmission", recommendAdmissionMsg.toString());
		model.addAttribute("recommendRejection", recommendRejectionMsg.toString());
		model.addAttribute("admitted", admittedMsg.toString());
		model.addAttribute("rejected", rejectedMsg.toString());
		model.addAttribute("recommendClarificationFromDepartment", recommendClarificationFromDepartmentMsg.toString());
		model.addAttribute("recommendClarificationFromMember", recommendClarificationFromMemberMsg.toString());
		model.addAttribute("clarificationNeededFromDepartment", clarificationNeededFromDepartmentMsg.toString());
		model.addAttribute("clarificationNeededFromMember", clarificationNeededFromMemberMsg.toString());
		populateBulkApprovalView(model,request,locale.toString());
		return "workflow/resolution/bulkapprovalview";
	}
	
	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value="/advancedbulkapproval",method=RequestMethod.GET)
	public String getAdvancedBulkApproval(final HttpServletRequest request,final Locale locale,
			final ModelMap model){
		try{
			/**** Request Params ****/
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			String strDeviceType=request.getParameter("deviceType");
			String strStatus=request.getParameter("status");
			String strWorkflowSubType=request.getParameter("workflowSubType");
			//String strItemsCount=request.getParameter("itemsCount");
			String strLocale=locale.toString();
			String assignee=this.getCurrentUser().getActualUsername();
			String strItemsCount = null;
			CustomParameter itemsCountParameter = CustomParameter.findByName(CustomParameter.class, "ADVANCED_BULKAPPROVAL_ITEM_COUNT", "");
			if(itemsCountParameter != null){
				strItemsCount = itemsCountParameter.getValue();
			}
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
			Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
			String strUserGroupType=null;
			String strUsergroup=null;
			if(userGroups!=null){
				if(!userGroups.isEmpty()){
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROIS_ALLOWED_USERGROUPTYPES", "");
					if(customParameter!=null){
						String allowedUserGroups=customParameter.getValue(); 
						for(UserGroup i:userGroups){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
								if(ug != null){
									strUsergroup=String.valueOf(i.getId());
									strUserGroupType=i.getUserGroupType().getType();
									break;
								}
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
				}else if(i.getType().contains("ROIS_CLERK")){
					strRole=i.getType();
					break;
				}else if(i.getType().startsWith("ROIS_")){
					strRole=i.getType();
					break;
				}
			}
			
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strDeviceType!=null&&!(strDeviceType.isEmpty())){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(customParameter!=null){
					String server=customParameter.getValue();
					if(server.equals("TOMCAT")){
						try {
							strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
							strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
							strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
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
				strDeviceType = request.getSession().getAttribute("deviceType").toString();
				strWorkflowSubType = request.getSession().getAttribute("workflowSubType").toString();
				strStatus = request.getSession().getAttribute("status").toString();
			}
			if(strHouseType!=null&&!(strHouseType.isEmpty())
					&&strSessionType!=null&&!(strSessionType.isEmpty())
					&&strSessionYear!=null&&!(strSessionYear.isEmpty())
					&&strDeviceType!=null&&!(strDeviceType.isEmpty())
					&&strStatus!=null&&!(strStatus.isEmpty())
					&&strRole!=null&&!(strRole.isEmpty())
					&&strUsergroup!=null&&!(strUsergroup.isEmpty())
					&&strUserGroupType!=null&&!(strUserGroupType.isEmpty())
					&&strItemsCount!=null&&!(strItemsCount.isEmpty())
					&&strWorkflowSubType!=null&&!(strWorkflowSubType.isEmpty())){	
				/**** List of Statuses ****/
					List<Status> internalStatuses=new ArrayList<Status>();
					HouseType houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, strLocale);
					DeviceType motionType=DeviceType.findByFieldName(DeviceType.class,"name",strDeviceType,strLocale);
					Status internalStatus=Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,motionType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "RESOLUTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+internalStatus.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "RESOLUTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "RESOLUTION_PUT_UP_OPTIONS_"+motionType.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
					if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(strUserGroupType)){
						CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+strUserGroupType.toUpperCase(),"");
						if(finalApprovingAuthorityStatus!=null){
							try {
								internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), strLocale);
							} catch (ELSException e) {							
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
						}
					}/**** RESOLUTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
					else if(deviceTypeInternalStatusUsergroup!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), strLocale);
						} catch (ELSException e) {						
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}/**** RESOLUTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
					else if(deviceTypeHouseTypeUsergroup!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}	
					/**** RESOLUTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
					else if(deviceTypeUsergroup!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}	
					model.addAttribute("internalStatuses",internalStatuses);
				}
				/**** Request Params To Model Attribute ****/
				model.addAttribute("houseType", strHouseType);
				model.addAttribute("sessionType", strSessionType);
				model.addAttribute("sessionYear", strSessionYear);
				model.addAttribute("deviceType", strDeviceType);
				model.addAttribute("status", strStatus);
				model.addAttribute("role", strRole);
				model.addAttribute("usergroup", strUsergroup);
				model.addAttribute("usergroupType", strUserGroupType);
				model.addAttribute("itemscount", strItemsCount);
				model.addAttribute("workflowSubType",strWorkflowSubType);

				/**** Workflow Details ****/
				List<WorkflowDetails> workflowDetails = null;
				try {
					workflowDetails = WorkflowDetails.
					findAll(strHouseType,strSessionType,strSessionYear,
							strDeviceType,strStatus,strWorkflowSubType,
							assignee,strItemsCount,strLocale,null);
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}
				/**** Populating Bulk Approval VOs ****/
				List<BulkApprovalVO> bulkapprovals=new ArrayList<BulkApprovalVO>();
				NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
				for(WorkflowDetails i:workflowDetails){
						HouseType houseType=null;
						BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
						Resolution resolution=Resolution.findById(Resolution.class,Long.parseLong(i.getDeviceId()));
						if(resolution.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
							houseType = HouseType.findByFieldName(HouseType.class, "name", i.getHouseType(), resolution.getLocale());
						} else {
							houseType=resolution.getHouseType();
						}
						/**** Bulk Submission For Workflows ****/
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(resolution.getId()));				
						if(resolution.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(resolution.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(resolution.getType().getName());
						bulkApprovalVO.setMember(resolution.getMember().getFullname());
						if(resolution.getRevisedNoticeContent() != null && !resolution.getRevisedNoticeContent().isEmpty()){
							bulkApprovalVO.setSubject(resolution.getRevisedNoticeContent());
						}else{
							bulkApprovalVO.setSubject(resolution.getNoticeContent());
						}
						Long referencedEntity = Resolution.findReferencedEntity(resolution);
						if(referencedEntity != null){
							Resolution refResolution = Resolution.findReferencedResolution(resolution);
							bulkApprovalVO.setFormattedReferencedNumbers(refResolution.getNumber().toString());
						}
						if(resolution.getRemarks()!=null&&!resolution.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(resolution.getRemarks());
						}else{
							bulkApprovalVO.setLastRemark("-");
						}
						if(resolution.getReferencedResolutionText() != null && !resolution.getReferencedResolutionText().isEmpty()){
							bulkApprovalVO.setBriefExpanation(resolution.getReferencedResolutionText());
						}else{
							bulkApprovalVO.setBriefExpanation("-");
						}
						if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							bulkApprovalVO.setLastDecision(resolution.getInternalStatusLowerHouse().getName());
						}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							bulkApprovalVO.setLastDecision(resolution.getInternalStatusUpperHouse().getName());
						
						}
						
						Map<String, String[]> requestMap=new HashMap<String, String[]>();			
						requestMap.put("resolutionId",new String[]{String.valueOf(resolution.getId())});
						requestMap.put("locale",new String[]{resolution.getLocale()});
						List result=Query.findReport("ROIS_GET_REVISION", requestMap);
						bulkApprovalVO.setRevisions(result);
						bulkApprovalVO.setLastRemarkBy(resolution.getEditedAs());	
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
						
				}		
				model.addAttribute("bulkapprovals", bulkapprovals);
				if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
					model.addAttribute("resolutionId",bulkapprovals.get(0).getDeviceId());
				}
				return "workflow/resolution/advancedbulkapproval";
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
		Resolution tempResolution  = null;
		String resolutionlistSize = request.getParameter("resolutionlistSize");
		if(resolutionlistSize != null && (resolutionlistSize.length() >0)) {
			for(int i =0; i<Integer.parseInt(resolutionlistSize);i++){ 
				String id = request.getParameter("questionId"+i);
				String noticeContent = request.getParameter("noticeContent"+i);
				String actor = request.getParameter("actor"+i);
				String internalStatus = request.getParameter("internalStatus"+i);
				String remark = request.getParameter("remark"+i);
				String refText = request.getParameter("referenceText" + i);
				String workflowDetailsId = request.getParameter("workflowDetailsId"+i);
				Long wrkflowId = Long.parseLong(workflowDetailsId);
				WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class,wrkflowId);
				String strChecked = request.getParameter("chk"+workflowDetailsId);
				if(strChecked != null && !strChecked.isEmpty() && Boolean.parseBoolean(strChecked)){
					HouseType houseType = null;
					Resolution resolution = Resolution.findById(Resolution.class,Long.parseLong(wfDetails.getDeviceId()));
					if(resolution.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
						houseType = HouseType.findByFieldName(HouseType.class, "name", wfDetails.getHouseType(), resolution.getLocale());
					} else {
						houseType=resolution.getHouseType();
					}
					tempResolution = resolution;
					
					if(noticeContent != null && !noticeContent.isEmpty()){
						resolution.setRevisedNoticeContent(noticeContent);
					}
					if(remark != null && !remark.isEmpty()){
						resolution.setRemarks(remark);
					}
					if(refText != null && !refText.isEmpty()){
						resolution.setReferencedResolutionText(refText);
					}
					
					/**** Update Actor ****/
					String[] temp = actor.split("#");
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						resolution.setActorLowerHouse(actor);
						resolution.setLocalizedActorNameLowerHouse(temp[3] + "(" + temp[4] + ")");
						resolution.setLevelLowerHouse(temp[2]);
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						resolution.setActorUpperHouse(actor);
						resolution.setLocalizedActorNameUpperHouse(temp[3] + "(" + temp[4] + ")");
						resolution.setLevelUpperHouse(temp[2]);
					}
					
					/**** Update Internal Status and Recommendation Status ****/
					Status intStatus = Status.findById(Status.class, Long.parseLong(internalStatus));
					if(internalStatus != null){
						if(!intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS) 
								&& !intStatus.getType().equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)){
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								resolution.setInternalStatusLowerHouse(intStatus);
								resolution.setRecommendationStatusLowerHouse(intStatus);
								resolution.setEndFlagLowerHouse("continue");
							}else{
								resolution.setInternalStatusUpperHouse(intStatus);
								resolution.setRecommendationStatusUpperHouse(intStatus);
								resolution.setEndFlagUpperHouse("continue");
							}
							
						}else{
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								resolution.setRecommendationStatusLowerHouse(intStatus);
								resolution.setEndFlagLowerHouse("continue");
							}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
								resolution.setRecommendationStatusUpperHouse(intStatus);
								resolution.setEndFlagUpperHouse("continue");
							}
						}
					}
					
					/**** Complete Task ****/
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(resolution.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(resolution.getType().getId()));
					properties.put("pv_user",temp[0]);
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						properties.put("pv_endflag",resolution.getEndFlagLowerHouse());
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						properties.put("pv_endflag",resolution.getEndFlagUpperHouse());
					}
					
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
											
											String reminderto=request.getParameter("reminderto");
											properties.put("pv_reminderto", reminderto);
											
											//String remindersubject=request.getParameter("remindersubject");
											String remindersubject=resolution.getSubject();
											properties.put("pv_remindersubject", remindersubject);
											
											//String remindercontent=request.getParameter("remindercontent");
											String remindercontent=resolution.getNoticeContent();
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
					
					String strTaskId=wfDetails.getTaskId();
					Task task=processService.findTaskById(strTaskId);
					processService.completeTask(task,properties);
					String endFlag = null;
					String level = null;
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						endFlag = resolution.getEndFlagLowerHouse();
						level = resolution.getLevelLowerHouse();
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						endFlag = resolution.getEndFlagUpperHouse();
						level = resolution.getLevelUpperHouse();
					}
					if(endFlag!=null&&!endFlag.isEmpty()
							&&endFlag.equals("continue")){
						/**** Create New Workflow Details ****/
						ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
						Task newtask=processService.getCurrentTask(processInstance);
						WorkflowDetails workflowDetails2;
						try {
							workflowDetails2 = WorkflowDetails.create(resolution,newtask,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,level,houseType);
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								resolution.setWorkflowDetailsIdLowerHouse(workflowDetails2.getId());
								resolution.setTaskReceivedOnLowerHouse(new Date());
								
							}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
								resolution.setWorkflowDetailsIdUpperHouse(workflowDetails2.getId());
								resolution.setTaskReceivedOnUpperHouse(new Date());
							}
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
														
					}
					/**** Update Old Workflow Details ****/
					wfDetails.setStatus("COMPLETED");
					if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						wfDetails.setInternalStatus(resolution.getInternalStatusLowerHouse().getName());
						wfDetails.setRecommendationStatus(resolution.getRecommendationStatusLowerHouse().getName());;	
					}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						wfDetails.setInternalStatus(resolution.getInternalStatusUpperHouse().getName());
						wfDetails.setRecommendationStatus(resolution.getRecommendationStatusUpperHouse().getName());	
					}
					
					wfDetails.setCompletionTime(new Date());
					wfDetails.merge();
					/**** Update Resolution ****/
					resolution.setEditedOn(new Date());
					resolution.setEditedBy(this.getCurrentUser().getActualUsername());
					resolution.setEditedAs(wfDetails.getAssigneeUserGroupName());
					resolution.setEditedOn(new Date());
					try {
						performAction(resolution, wfDetails);
					} catch (ELSException e) {
						e.printStackTrace();
						logger.error(e.toString());
						model.addAttribute("error", e.getParameter());
					}
					
					resolution.merge();
				}
			}	
		}
		if(tempResolution != null){
			HouseType tempHouseType = tempResolution.getHouseType();
			if(tempHouseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				request.getSession().setAttribute("workflowSubType", tempResolution.getInternalStatusLowerHouse().getType());
			}if(tempHouseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				request.getSession().setAttribute("workflowSubType", tempResolution.getInternalStatusUpperHouse().getType());
			}
			
			request.getSession().setAttribute("houseType", tempResolution.getHouseType().getName());
			request.getSession().setAttribute("sessionType", tempResolution.getSession().getType().getSessionType());
			request.getSession().setAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(tempResolution.getSession().getYear(), locale.toString()));
			request.getSession().setAttribute("deviceType", tempResolution.getType().getName());
			String status = request.getParameter("status");
			if(status != null && !status.isEmpty()){
				request.getSession().setAttribute("status", status);
			}
			
		}
		redirectAttributes.addFlashAttribute("type", "success");
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/workflow/resolution/advancedbulkapproval";
        return returnUrl;
	}

	private void populateBulkApprovalView(final Model model,
			final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDeviceType=request.getParameter("deviceType");			
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
				&&strDeviceType!=null&&!(strDeviceType.isEmpty())
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
						strDeviceType,strStatus,strWorkflowSubType,
						assignee,strItemsCount,strLocale,strFile);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals=new ArrayList<BulkApprovalVO>();
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			for(WorkflowDetails i:workflowDetails){
				HouseType houseType=null;
				BulkApprovalVO bulkApprovalVO=new BulkApprovalVO();				
				Resolution resolution=Resolution.findById(Resolution.class,Long.parseLong(i.getDeviceId()));
				if(resolution.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
					houseType = HouseType.findByFieldName(HouseType.class, "name", i.getHouseType(), resolution.getLocale());
				} else {
					houseType=resolution.getHouseType();
				}
				Integer file=null;
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					file=resolution.getFileLowerHouse();
				}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
					file=resolution.getFileUpperHouse();
				}
				/**** Bulk Submission For Workflows ****/
					/**** File Bulk Submission ****/
					if(strFile!=null&&!strFile.isEmpty()&&!strFile.equals("-")
							&&file!=null
							&&file==Integer.parseInt(strFile)){
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(resolution.getId()));				
						if(resolution.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(resolution.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(resolution.getType().getName());
						bulkApprovalVO.setMember(resolution.getMember().getFullname());
						if(resolution.getRevisedNoticeContent() != null && !resolution.getRevisedNoticeContent().isEmpty()){
							bulkApprovalVO.setSubject(resolution.getRevisedNoticeContent());
						}else{
							bulkApprovalVO.setSubject(resolution.getNoticeContent());
						}
						if(resolution.getRemarks()!=null&&!resolution.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(resolution.getRemarks());
						}else{
							bulkApprovalVO.setLastRemark("-");
						}
						if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							bulkApprovalVO.setLastDecision(resolution.getInternalStatusLowerHouse().getName());
						}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							bulkApprovalVO.setLastDecision(resolution.getInternalStatusUpperHouse().getName());
						
						}	
						bulkApprovalVO.setLastRemarkBy(resolution.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}/**** Status Wise Bulk Submission ****/
					else if(strFile!=null&&!strFile.isEmpty()&&
							strFile.equals("-")){
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(resolution.getId()));				
						if(resolution.getNumber()!=null){
							bulkApprovalVO.setDeviceNumber(format.format(resolution.getNumber()));
						}else{
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(resolution.getType().getName());
						bulkApprovalVO.setMember(resolution.getMember().getFullname());
						if(resolution.getRevisedNoticeContent() != null && !resolution.getRevisedNoticeContent().isEmpty()){
							bulkApprovalVO.setSubject(resolution.getRevisedNoticeContent());
						}else{
							bulkApprovalVO.setSubject(resolution.getNoticeContent());
						}
						if(resolution.getRemarks()!=null&&!resolution.getRemarks().isEmpty()){
							bulkApprovalVO.setLastRemark(resolution.getRemarks());
						}else{
							bulkApprovalVO.setLastRemark("-");
						}
						if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							bulkApprovalVO.setLastDecision(resolution.getInternalStatusLowerHouse().getName());
						}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							bulkApprovalVO.setLastDecision(resolution.getInternalStatusUpperHouse().getName());
						
						}	
						Map<String, String[]> requestMap=new HashMap<String, String[]>();			
						requestMap.put("resolutionId",new String[]{String.valueOf(resolution.getId())});
						requestMap.put("locale",new String[]{resolution.getLocale()});
						List result=Query.findReport("ROIS_GET_REVISION", requestMap);
						bulkApprovalVO.setRevisions(result);
						bulkApprovalVO.setLastRemarkBy(resolution.getEditedAs());	
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}
				}		
			model.addAttribute("bulkapprovals", bulkapprovals);
			if(bulkapprovals!=null&&!bulkapprovals.isEmpty()){
				model.addAttribute("resolutionId",bulkapprovals.get(0).getDeviceId());
			}
		}
	}

	private void performAction(final Resolution domain, final WorkflowDetails workflowDetails) throws ELSException{
		String internalStatus=null;
		String recommendationStatus=null;
		HouseType houseType = null;
		if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			houseType = HouseType.findByFieldName(HouseType.class, "name", workflowDetails.getHouseType(), domain.getLocale());
		} else {
			houseType=domain.getHouseType();
		}
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			internalStatus=domain.getInternalStatusLowerHouse().getType();
			recommendationStatus=domain.getRecommendationStatusLowerHouse().getType();
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			internalStatus=domain.getInternalStatusUpperHouse().getType();
			recommendationStatus=domain.getRecommendationStatusUpperHouse().getType();
		}
		 
		
		if((internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.RESOLUTION_FINAL_ADMISSION)) || (internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION)
						&&recommendationStatus.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION))){
			performActionOnAdmission(domain, workflowDetails);
		}else if(internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.RESOLUTION_FINAL_REJECTION)|| (internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION)
						&&recommendationStatus.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION))){
			performActionOnRejection(domain, workflowDetails);
		}else if(internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)
				&&recommendationStatus.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)){
			performActionOnClarificationNeededFromDepartment(domain);
		}else if(internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)
				&&recommendationStatus.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)){
			performActionOnClarificationNeededFromMember(domain);
		}else if((internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
				recommendationStatus.equals(ApplicationConstants.RESOLUTION_PROCESSED_CLARIFICATIONRECIEVED))||
				(internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)&&
						recommendationStatus.equals(ApplicationConstants.RESOLUTION_PROCESSED_CLARIFICATIONRECIEVED))){
			performActionOnClarificationRecieved(domain);
		}else if(internalStatus.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
				recommendationStatus.equals(ApplicationConstants.RESOLUTION_PROCESSED_CLARIFICATIONNOTRECIEVED)){
			performActionOnClarificationNotRecieved(domain);
		}
		
			
	}

	private void performActionOnClarificationNeededFromDepartment(final Resolution domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT, domain.getLocale());
		if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			domain.setStatusLowerHouse(finalStatus);
		}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
			domain.setStatusUpperHouse(finalStatus);
		}
		
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
		
	}

	private void performActionOnClarificationNeededFromMember(final Resolution domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER, domain.getLocale());
		if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			domain.setStatusLowerHouse(finalStatus);
		}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
			domain.setStatusUpperHouse(finalStatus);
		}
		
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
		
	}

	private void performActionOnRejection(final Resolution domain, final WorkflowDetails workflowDetails) {
		Status finalStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION, domain.getLocale());
		HouseType houseType = null;
		if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			houseType = HouseType.findByFieldName(HouseType.class, "name", workflowDetails.getHouseType(), domain.getLocale());
		} else {
			houseType=domain.getHouseType();
		}
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			domain.setStatusLowerHouse(finalStatus);
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			domain.setStatusUpperHouse(finalStatus);
		}
		
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
		
		
		/******Adding a next resolution of the member to chart on rejection of one of the resolution of that member********/
		Resolution resolution = null;
		try {
			resolution = Resolution.find(domain.getMember(),domain.getSession(),domain.getLocale());
		} catch (ELSException e1) {
			e1.printStackTrace();
		}
		if(resolution!=null){
//			Chart chart = null;
//			try{
//				chart = Chart.find(new Chart(resolution.getSession(),resolution.getType(), resolution.getLocale()));
//			}catch (ELSException e) {
//				e.printStackTrace();
//			}
			if(resolution.getNumber()==null){
				Integer number = null;
				try {
					number = Resolution.assignResolutionNo(resolution.getHouseType(),resolution.getSession(), resolution.getType(), resolution.getLocale());
				} catch (ELSException e) {
					e.printStackTrace();
				}
				resolution.setNumber(number+1);
			}
//			if(chart!=null){
//				try{
//					Chart.addToChart(resolution);
//				}catch (ELSException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}	
	
	private void performActionOnAdmission(final Resolution domain, final WorkflowDetails workflowDetails) {
		Status finalStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, domain.getLocale());
		
		HouseType houseType = null;
		if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			houseType = HouseType.findByFieldName(HouseType.class, "name", workflowDetails.getHouseType(), domain.getLocale());
		} else {
			houseType=domain.getHouseType();
		}
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			domain.setStatusLowerHouse(finalStatus);
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			domain.setStatusUpperHouse(finalStatus);
		}
		
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
		
	}
	
	private void performActionOnClarificationRecieved(final Resolution domain) {
		Status newStatus=Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		/*****Setting the statuses of the factual position received resolution 
		 * to put up so that the assistant can put up for admission/rejection again *******/
		if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			domain.setInternalStatusLowerHouse(newStatus);
			domain.setRecommendationStatusLowerHouse(newStatus);
		
		}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
			domain.setInternalStatusUpperHouse(newStatus);
			domain.setRecommendationStatusUpperHouse(newStatus);
		}
		if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
			if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				domain.setEndFlagLowerHouse("continue");
				domain.setLevelLowerHouse("1");
				domain.setActorLowerHouse(null);
				domain.setLocalizedActorNameLowerHouse("");
			}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				domain.setEndFlagUpperHouse("continue");
				domain.setLevelUpperHouse("1");
				domain.setActorUpperHouse(null);
				domain.setLocalizedActorNameUpperHouse("");
			}
		}
		
				
	}
	
	private void performActionOnClarificationNotRecieved(final Resolution domain) {
		Status finalStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNOTRECEIVEDFROMDEPARTMENT, domain.getLocale());
		Status newStatus=Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		/*****Setting the statuses of the factual position received resolution 
		 * to put up so that the assistant can put up for admission/rejection again *******/
		if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			((Resolution) domain).setStatusLowerHouse(finalStatus);
			((Resolution) domain).setInternalStatusLowerHouse(newStatus);
			((Resolution) domain).setRecommendationStatusLowerHouse(newStatus);
		}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
			((Resolution) domain).setStatusUpperHouse(finalStatus);
			((Resolution) domain).setInternalStatusUpperHouse(newStatus);
			((Resolution) domain).setRecommendationStatusUpperHouse(newStatus);
		}	
		if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
			if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				domain.setEndFlagLowerHouse("continue");
				domain.setLevelLowerHouse("1");
				domain.setActorLowerHouse(null);
				domain.setLocalizedActorNameLowerHouse("");
			}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				domain.setEndFlagUpperHouse("continue");
				domain.setLevelUpperHouse("1");
				domain.setActorUpperHouse(null);
				domain.setLocalizedActorNameUpperHouse("");
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void findLatestRemarksByUserGroup(final Resolution domain, final ModelMap model,
			final HttpServletRequest request)throws ELSException {
		
		String username = this.getCurrentUser().getUsername();
		Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
		UserGroup usergroup = UserGroup.findActive(credential, domain.getSubmissionDate(), domain.getLocale());
		UserGroupType userGroupType = usergroup.getUserGroupType();
		if(userGroupType == null
				|| (!userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)
				&& !userGroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
			CustomParameter customParameter = null;
			if(userGroupType!=null) {
				customParameter = CustomParameter.findByName(CustomParameter.class, "ROIS_LATESTREVISION_STARTINGACTOR_"+userGroupType.getType().toUpperCase(), "");
				if(customParameter != null){
					String strUsergroupType = customParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "ROIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
					if(defaultCustomParameter != null){
						String strUsergroupType = defaultCustomParameter.getValue();
						userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
					}
				}
			} else {
				CustomParameter defaultCustomParameter = CustomParameter.findByName(CustomParameter.class, "ROIS_LATESTREVISION_STARTINGACTOR_DEFAULT", "");
				if(defaultCustomParameter != null){
					String strUsergroupType = defaultCustomParameter.getValue();
					userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type", strUsergroupType, domain.getLocale());
				}
			}			
		}
		
		
		Map<String, String[]> requestMap=new HashMap<String, String[]>();			
		requestMap.put("resolutionId",new String[]{String.valueOf(domain.getId())});
		requestMap.put("locale",new String[]{domain.getLocale()});
		if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)
				|| userGroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			List result=Query.findReport("ROIS_GET_REVISION_FOR_DESKOFFICER", requestMap);
			model.addAttribute("latestRevisions",result);
		}else{
			List result=Query.findReport("ROIS_GET_REVISION", requestMap);
			model.addAttribute("latestRevisions",result);
		}
		model.addAttribute("startingActor", userGroupType.getName());
	}
}