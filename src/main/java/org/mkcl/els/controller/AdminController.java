package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.xmlvo.TestXmlVO;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.CutMotionDateDraft;
import org.mkcl.els.domain.CutMotionDepartmentDatePriority;
import org.mkcl.els.domain.CutMotionDraft;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.domain.chart.ChartEntry;
import org.mkcl.els.service.INotificationService;
import org.mkcl.els.service.ISecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MLS has many runtime requirements (mainly due to manual errors) which may not be fulfilled by taking 
 * the domain route. This controller provides a start for incorporating such requirements.
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
	
	@Autowired 
	private ISecurityService securityService;
	
	@Autowired 
	private INotificationService notificationService;
	
	@RequestMapping(value="support_activities/module", method=RequestMethod.GET)
    public String populateSupportActivitiesModule(final ModelMap model, 
    		final HttpServletRequest request,
    		final Locale locale) throws ELSException {
//		final String servletPath = request.getServletPath().replaceFirst("\\/","");
//		//here making provisions for displaying error pages
//        if(model.containsAttribute("errorcode")){
//            return servletPath.replace("password","error");
//        }else{
//            return servletPath;
//        }
        
        // Populate locale
     	model.addAttribute("moduleLocale", locale.toString());
     		
		// Populate Device types
  		List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class, "supportOrder", ApplicationConstants.ASC, locale.toString());
  		model.addAttribute("deviceTypes", deviceTypes);
  		String defaultDeviceTypeForSupportActivities = ApplicationConstants.UNSTARRED_QUESTION;
  		CustomParameter csptDefaultDeviceType = CustomParameter.findByName(CustomParameter.class, "DEFAULT_DEVICETYPE_FOR_SUPPORT_ACTIVITIES", "");
  		if(csptDefaultDeviceType!=null 
  				&& csptDefaultDeviceType.getValue()!=null && !csptDefaultDeviceType.getValue().isEmpty()) {
  			defaultDeviceTypeForSupportActivities = csptDefaultDeviceType.getValue();
  		}
  		DeviceType defaultSelectedDeviceType = DeviceType.findByType(defaultDeviceTypeForSupportActivities, locale.toString());
  		model.addAttribute("defaultSelectedDeviceType", defaultSelectedDeviceType.getId());
  		model.addAttribute("selectedDeviceType", defaultSelectedDeviceType.getId());
 		model.addAttribute("deviceTypeType", defaultSelectedDeviceType.getType());
 		model.addAttribute("whichDevice", "questions_"); //as default devicetype is starred / unstarred questions
 		String device = defaultSelectedDeviceType.getDevice();     	
     	
      	// Populate House types
  		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", ApplicationConstants.ASC, locale.toString());
  		model.addAttribute("houseTypes", houseTypes);
  		HouseType defaultSelectedHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale.toString());
  		model.addAttribute("defaultSelectedHouseType", defaultSelectedHouseType.getType());
  		model.addAttribute("defaultSelectedHouseTypeId", defaultSelectedHouseType.getId());
  		
  		// Populate Session types
		List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale.toString());
		model.addAttribute("sessionTypes", sessionTypes);
		
		// Populate latest Session type and year
		SessionType sessionType = null;
		Integer sessionYear = new GregorianCalendar().get(Calendar.YEAR);
		Session latestSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(defaultSelectedHouseType, defaultSelectedDeviceType);
		if(latestSession != null) {
			sessionType = latestSession.getType();
			model.addAttribute("sessionType", sessionType.getId());
			sessionYear = latestSession.getYear();
		}
		else {
			model.addAttribute("errorcode", "nosessionentriesfound");
			return "support_activities/error";
		}
		model.addAttribute("sessionYear", sessionYear);
		int year = sessionYear;
		CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		List<Reference> years = new ArrayList<Reference>();
		if(houseFormationYear != null){
			Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
			for(int i = year; i >= formationYear; i--){
				Reference reference = new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
				years.add(reference);
			}
		}else{
			model.addAttribute("errorcode", "houseformationyearnotset");
			return "support_activities/error";
		}
		model.addAttribute("years", years);
		
     	// Populate Groups
     	List<Reference> groups = new ArrayList<Reference>();
     	String groupNumberLimitParameter = CustomParameter.findByName(CustomParameter.class, "DEFAULT_GROUP_NUMBER", "").getValue();
     	Integer groupNumberLimit=Integer.parseInt(groupNumberLimitParameter);
		for(int i = 1; i <= groupNumberLimit; i++){
			Reference reference = new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
			groups.add(reference);
		}
		model.addAttribute("groups",groups);
		
		// Populate SubDepartments
		List<SubDepartment> currentSubDepartments = SubDepartment.findAllCurrentSubDepartments(locale.toString());
		model.addAttribute("subdepartments",currentSubDepartments);
		
		// Populate Status
		CustomParameter csptStatusesForDefaultDeviceType=CustomParameter.findByName(CustomParameter.class, "STATUS_TYPES_FOR_SUPPORT_ACTIVITIES_OF_"+defaultSelectedDeviceType.getType().toUpperCase(), "");
		if(csptStatusesForDefaultDeviceType!=null && csptStatusesForDefaultDeviceType.getValue()!=null) {
			List<Status> statusesForDefaultDeviceType = Status.findStatusWithSupportOrderContainedIn(csptStatusesForDefaultDeviceType.getValue(), locale.toString());
			model.addAttribute("statusesForDeviceType", statusesForDefaultDeviceType);
		}
		
		
		// Populate Search By Parameter
		try{
			CustomParameter csptSearchByFacility = CustomParameter.findByName(CustomParameter.class, "SEARCHFACILITY_SEARCH_BY", "");
			if(csptSearchByFacility != null && csptSearchByFacility.getValue() != null && ! csptSearchByFacility.getValue().isEmpty()){
				List<MasterVO> searchByData = new ArrayList<MasterVO>();
				for(String sf : csptSearchByFacility.getValue().split(";")){
					String[] data = sf.split(":");
					MasterVO newVO = new MasterVO();
					newVO.setValue(data[0]);
					newVO.setName(data[1]);
					searchByData.add(newVO);
				}
				model.addAttribute("searchBy", searchByData);
			}			
		}catch(Exception e){
			logger.error("error", e);
			return "support_activities/error";
		}
		
		return "support_activities/module";
	}

//	/**
//	 * Starts question workflow at the given level.
//	 * Sample: /els/admin/start_workflow/question/id/3072/status/question_recommend_admission/userGroupType/assistant/level/7
//	 */
//	@Transactional
//	@RequestMapping(value="start_workflow/question/id/{id}/status/{statusType}/userGroupType/{userGroupType}/level/{level}", method=RequestMethod.GET)
//	public @ResponseBody String startQuestionWorkflow(@PathVariable("id") final Long questionId,
//			@PathVariable("statusType") final String statusType,
//			@PathVariable("userGroupType") final String userGroupTypeType,
//			@PathVariable("level") final Integer level,
//			final Locale appLocale) {
//		try {
//			String locale = appLocale.toString();
//			
//			// Update Question
//			Question question = Question.findById(Question.class, questionId);
//			question.removeExistingWorkflowAttributes();
//			
//			// Update existing Workflow Details
//			WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question);
//			if(wfDetails != null) {
//				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
//				wfDetails.merge();
//			}
//			
//			
//			// Compute background information and start workflow
//			Status status = Status.findByType(statusType, locale);
//			UserGroupType userGroupType = UserGroupType.findByType(userGroupTypeType, locale);
//			Workflow workflow = Workflow.findByStatus(status, locale);
//			WorkflowDetails.startProcessAtGivenLevel(question, ApplicationConstants.APPROVAL_WORKFLOW, 
//					workflow, userGroupType, level, locale);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			return "ERROR";
//		}
//		
//		return "SUCCESS";
//	}
	
//	/**
//	 * Ends question workflow & removes workflow specific attributes.
//	 * Sample: /els/admin/end_workflow/question/id/3072
//	 */
//	@Transactional
//	@RequestMapping(value="end_workflow/question/id/{id}", method=RequestMethod.GET)
//	public @ResponseBody String endQuestionWorkflow(@PathVariable("id") final Long questionId) {
//		try {
//			Question question = Question.findById(Question.class, questionId);
//			WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question);
//			if(wfDetails != null) {
//				WorkflowDetails.endProcess(wfDetails);
//			}
//			question.removeExistingWorkflowAttributes();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			return "ERROR";
//		}
//		
//		return "SUCCESS";
//	}
	
//	@Transactional
//	@RequestMapping(value="start_workflow_bulk/question/id/{ids}/status/{statusType}/userGroupType/{userGroupType}/level/{level}", method=RequestMethod.GET)
//	public @ResponseBody String startBulkQuestionWorkflow(@PathVariable("ids") final String questionIds,
//			@PathVariable("statusType") final String statusType,
//			@PathVariable("userGroupType") final String userGroupTypeType,
//			@PathVariable("level") final Integer level,
//			final Locale appLocale) {
//		try {
//			String locale = appLocale.toString();
//			
//			String[] questionIdArr = questionIds.split(",");
//			
//			for(String qid: questionIdArr) {
//				// Update Question			
//				Question question = Question.findById(Question.class, Long.parseLong(qid));
//				question.removeExistingWorkflowAttributes();
//				
//				// Update existing Workflow Details
//				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question);
//				if(wfDetails != null) {
//					wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
//					wfDetails.merge();
//				}
//				
//				
//				// Compute background information and start workflow
//				Status status = Status.findByType(statusType, locale);
//				UserGroupType userGroupType = UserGroupType.findByType(userGroupTypeType, locale);
//				Workflow workflow = Workflow.findByStatus(status, locale);
//				WorkflowDetails.startProcessAtGivenLevel(question, ApplicationConstants.APPROVAL_WORKFLOW, 
//						workflow, userGroupType, level, locale);
//			}		
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			return "ERROR";
//		}
//		
//		return "SUCCESS";
//	}
	
	/**
	 * Removes a device from Chart.
	 * Sample: /els/admin/remove_from_chart/device/id/3072
	 */
	@Transactional
	@RequestMapping(value="remove_from_chart/device/id/{id}", method=RequestMethod.GET)
	public @ResponseBody String removeDeviceFromChart(@PathVariable("id") final Long deviceId) {
		try {
			Device device = Device.findById(Device.class, deviceId);
			Chart.removeFromChart(device);
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	/**
	 * Adds supporting members to a question. Member ids are provided as comma separated string.
	 */
	@Transactional
	@RequestMapping(value="add_supporting_members/question/id/{id}/members/id/{ids}", method=RequestMethod.GET)
	public @ResponseBody String addSupportingMembers(@PathVariable("id") final Long questionId,
			@PathVariable("ids") final String strMemberIds) {
		try {
			// Get question
			Question question = Question.findById(Question.class, questionId);
			String locale = question.getLocale();
			
			// List of member ids
			String[] tokens = strMemberIds.split(",");
			List<Long> memberIds = new ArrayList<Long>();
			for(String token : tokens) {
				Long memberId = Long.parseLong(token.trim());
				memberIds.add(memberId);
			}
			
			// List of Supporting members
			List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
			Status SUPPORTING_MEMBER_APPROVED = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale);
			for(Long memberId : memberIds) {
				Member member = Member.findById(Member.class, memberId);
				
				SupportingMember sm = new SupportingMember();
				sm.setMember(member);
				sm.setLocale(locale);
				sm.setApprovedText(question.getQuestionText());
				sm.setApprovedSubject(question.getSubject());
				sm.setDecisionStatus(SUPPORTING_MEMBER_APPROVED);
				sm.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
				sm.merge();
				
				supportingMembers.add(sm);
			}
			
			List<SupportingMember> existingSupportingMembers = question.getSupportingMembers();
			existingSupportingMembers.addAll(supportingMembers);
			question.setSupportingMembers(existingSupportingMembers);
			question.simpleMerge();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	/**
	 * Removes supporting members from a question. Supporting Member ids are provided as comma separated string.
	 * Sample: /els/admin/remove_supporting_members/question/id/3072/supporting_members/ids/1582, 1537, 1892
	 */
	@Transactional
	@RequestMapping(value="remove_supporting_members/question/id/{id}/supporting_members/ids/{ids}", method=RequestMethod.GET)
	public @ResponseBody String removeSupportingMembers(@PathVariable("id") final Long questionId,
			@PathVariable("ids") final String strSupportingMemberIds) {
		try {
			// Get question
			Question question = Question.findById(Question.class, questionId);
			
			// List of supporting member ids
			String[] tokens = strSupportingMemberIds.split(",");
			List<Long> supportingMemberIds = new ArrayList<Long>();
			for(String token : tokens) {
				Long supportingMemberId = Long.parseLong(token.trim());
				supportingMemberIds.add(supportingMemberId);
			}
			
			// List of supporting members
			List<SupportingMember> supportingMembers = question.getSupportingMembers();
			for(SupportingMember sm : supportingMembers) {
				for(Long supportingMemberId : supportingMemberIds) {
					Long smId = sm.getId();
					if(smId.equals(supportingMemberId)) {
						sm.remove();
						break;
					}
				}
			}
			
			// Reset the list of supporting members
			question.setSupportingMembers(supportingMembers);
			question.simpleMerge();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	/**
	 * Removes member from all questions (whose YAADI is not LAID) where he/she is a supporting member.
	 * Sample: /els/admin/remove_supporting_member_from_all_questions/member/id/307/session/2500/deviceType/id/4
	 */
	@Transactional
	@RequestMapping(value="remove_supporting_member_from_all_questions/member/id/{member_id}/session/id/{session_id}/deviceType/id/{deviceType_id}", 
	method=RequestMethod.GET)
	public @ResponseBody String removeSupportingMember(@PathVariable("member_id") final Long memberId,
			@PathVariable("session_id") final Long sessionId,
			@PathVariable("deviceType_id") final Long deviceTypeId) {
		try {
			Member member = Member.findById(Member.class, memberId);
			Session session = Session.findById(Session.class, sessionId);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, deviceTypeId);
			
			Question.removeAsSupportingMember(member, session, deviceType);
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	// TODO: Get the list of department usernames.
	public @ResponseBody List<String> departmentUsernames() {
		String query = "SELECT CONCAT(first_name, ' ', middle_name, ' ', last_name) AS department, c.username" +
				" FROM credentials c JOIN users u JOIN usergroups ug JOIN usergroups_types ugt" +
				" WHERE c.id = u.credential_id AND c.id = ug.credential AND ug.user_group_type = ugt.id " +
				"	AND ugt.type='department' AND c.enabled=TRUE;";
		return null;
	}	
	
	/**
	 * Kept here for time being. The actual point of invocation will be MemberMinister
	 * on create.
	 */
	@RequestMapping(value="handover_question/member/id/{id}", method=RequestMethod.GET)
	public @ResponseBody String handoverQuestions(@PathVariable("id") final Long memberId,
			final Locale appLocale) {
		try {
			String locale = appLocale.toString();
			Member member = Member.findById(Member.class, memberId);
			
			boolean isActiveMinister = member.isActiveMinisterOn(new Date(), locale);
			if(isActiveMinister) {
				Question.handover(member, new Date());
			}
			else {
				return "MEMBER IS NOT MINISTER";
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";	
	}
	
	@RequestMapping(value="addtochart/{id}/{chartId}", method=RequestMethod.GET)
	public @ResponseBody String AddToCHartQuestions(@PathVariable("id") final String questionIds,
			@PathVariable("chartId") final String chartId,
			final Locale appLocale) {
		try {
			String locale = appLocale.toString();
			String[] cIds = questionIds.split(","); 
			Chart chart = Chart.findById(Chart.class, Long.parseLong(chartId));
			if(cIds.length>0){
				for(int i=0 ; i<cIds.length;i++){
					Question question = Question.findById(Question.class, Long.parseLong(cIds[i]));
					List<Question> onChartQuestions = Chart.findQuestions(question.getPrimaryMember(), chart);
					List<Device> devices = new ArrayList<Device>();
					devices.addAll(onChartQuestions);
					devices.add(question);
					ChartEntry ce = Chart.find(chart, question.getPrimaryMember());
					ce.setDevices(devices);
					ce.merge();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";	
	}
	
	@Transactional
	@RequestMapping(value="club_with_original_unstarred/{childIds}/{parentId}", method=RequestMethod.GET)
	public @ResponseBody String ClubStarredToOriginalUnstarred(@PathVariable("childIds") final String childIds,
			@PathVariable("parentId") final String parentId,
			final Locale appLocale) {
		StringBuffer returnMsg = new StringBuffer();
		StringBuffer clubbedNumbers = new StringBuffer();
		StringBuffer unclubbedNumbers = new StringBuffer();
		try {
			String locale = appLocale.toString();
			String[] cIds = childIds.split(","); 
			Question parentQuestion = Question.findById(Question.class, Long.parseLong(parentId));
			Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION, locale);
			if(parentQuestion.getParent()!=null) {
				returnMsg.append("NOT ALLOWED: Parent question number " + parentQuestion.getNumber() + " is already clubbed");
				return returnMsg.toString();
			}
			if(parentQuestion.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {
				returnMsg.append("NOT ALLOWED: Parent question number " + parentQuestion.getNumber() + " is still in processing");
				return returnMsg.toString();
			}
			if(cIds.length>0){
				for(int i=0 ; i<cIds.length;i++){
					Question childQuestion = Question.findById(Question.class, Long.parseLong(cIds[i]));
					if(childQuestion.getParent()!=null) {
//						childQuestion = childQuestion.getParent();
						if(unclubbedNumbers.length()>0) {
							unclubbedNumbers.append(", " + childQuestion.getNumber() + " (already clubbed)");
						} else {
							unclubbedNumbers.append(childQuestion.getNumber() + " (already clubbed)");
						}
						continue;
					}
					try {
						Question.actualClubbingStarredOriginalUnstarredQuestions(parentQuestion, childQuestion, parentQuestion.getInternalStatus(), parentQuestion.getInternalStatus(), locale);
						if(clubbedNumbers.length()>0) {
							clubbedNumbers.append(", " + childQuestion.getNumber());
						} else {
							clubbedNumbers.append(childQuestion.getNumber());
						}
					} catch(Exception e) {
						if(unclubbedNumbers.length()>0) {
							unclubbedNumbers.append(", " + childQuestion.getNumber() + " (some exception)");
						} else {
							unclubbedNumbers.append(childQuestion.getNumber() + " (some exception)");
						}
						continue;
					}					
				}
				returnMsg.append("SUCCESS: ");
				if(clubbedNumbers.length()>0) {					
					returnMsg.append(clubbedNumbers);
					returnMsg.append(" clubbed... ");
				}
				if(unclubbedNumbers.length()>0) {
					returnMsg.append(unclubbedNumbers);
					returnMsg.append(" not clubbed");
				}				
			} else {
				returnMsg.append("NO CHILD QUESTIONS PROVIDED FOR CLUBBING!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return returnMsg.toString();
	}
	
	@Transactional
	@RequestMapping(value="club_questions_on_record/child/{childIds}/parent/{parentId}", method=RequestMethod.GET)
	public @ResponseBody String ClubQuestionsOnRecord(@PathVariable("childIds") final String childIds,
			@PathVariable("parentId") final String parentId,
			final Locale appLocale) {
		StringBuffer returnMsg = new StringBuffer();
		StringBuffer clubbedNumbers = new StringBuffer();
		StringBuffer unclubbedNumbers = new StringBuffer();
		try {
			String locale = appLocale.toString();
			String[] cIds = childIds.split(","); 
			Question parentQuestion = Question.findById(Question.class, Long.parseLong(parentId));
			Status systemClubbedStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
			if(parentQuestion.getParent()!=null) {
				returnMsg.append("NOT ALLOWED: Parent question number " + parentQuestion.getNumber() + " is already clubbed");
				return returnMsg.toString();
			}
			if(cIds.length>0){
				for(int i=0 ; i<cIds.length;i++){
					Question childQuestion = Question.findById(Question.class, Long.parseLong(cIds[i]));
					if(childQuestion.getParent()!=null) {
//						childQuestion = childQuestion.getParent();
						if(unclubbedNumbers.length()>0) {
							unclubbedNumbers.append(", " + childQuestion.getNumber() + " (already clubbed)");
						} else {
							unclubbedNumbers.append(childQuestion.getNumber() + " (already clubbed)");
						}
						continue;
					}
					try {
						List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
						String latestQuestionText = null;
						if(parentQuestion.getClubbedEntities()!=null && !parentQuestion.getClubbedEntities().isEmpty()){
							for(ClubbedEntity j:parentQuestion.getClubbedEntities()){
								// parent & child need not be disjoint. They could
								// be present in each other's hierarchy.
								Long childQnId = childQuestion.getId();
								Question clubbedQn = j.getQuestion();
								Long clubbedQnId = clubbedQn.getId();
								if(! childQnId.equals(clubbedQnId)) {
									/** fetch parent's latest question text from first of its children **/
									if(latestQuestionText==null) {
										latestQuestionText = clubbedQn.getRevisedQuestionText();
										if(latestQuestionText==null || latestQuestionText.isEmpty()) {
											latestQuestionText = clubbedQn.getQuestionText();
										}
									}
									parentClubbedEntities.add(j);
								}
							}			
						}
						
						List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
						if(childQuestion.getClubbedEntities()!=null && !childQuestion.getClubbedEntities().isEmpty()){
							for(ClubbedEntity k:childQuestion.getClubbedEntities()){
								// parent & child need not be disjoint. They could
								// be present in each other's hierarchy.
								Long parentQnId = parentQuestion.getId();
								Question clubbedQn = k.getQuestion();
								Long clubbedQnId = clubbedQn.getId();
								if(! parentQnId.equals(clubbedQnId)) {
									childClubbedEntities.add(k);
								}
							}
						}
						
						WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(childQuestion);
						if(wfDetails != null) {
							WorkflowDetails.endProcess(wfDetails);
						}
						childQuestion.removeExistingWorkflowAttributes();
						
						/** fetch parent's latest question text **/
						if(latestQuestionText==null) {
							latestQuestionText = parentQuestion.getRevisedQuestionText();
							if(latestQuestionText==null || latestQuestionText.isEmpty()) {
								latestQuestionText = parentQuestion.getQuestionText();
							}
						}
						
						childQuestion.setParent(parentQuestion);
						childQuestion.setClubbedEntities(null);		
						
						Status newStatus = parentQuestion.getStatus();
						childQuestion.setStatus(newStatus);
						Status newInternalStatus = null;
						if(parentQuestion.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {														
							newInternalStatus = Question.findCorrespondingStatusForGivenQuestionType(systemClubbedStatus, childQuestion.getType());
						} else {	
							childQuestion.setType(parentQuestion.getType());
							newInternalStatus = parentQuestion.getInternalStatus();
						}						
						childQuestion.setInternalStatus(newInternalStatus);
						Status newRecommendationStatus = null;
						if(parentQuestion.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {
							newRecommendationStatus = Question.findCorrespondingStatusForGivenQuestionType(systemClubbedStatus, childQuestion.getType());
						} else {	
							childQuestion.setType(parentQuestion.getType());
							newRecommendationStatus = parentQuestion.getInternalStatus();
						}						
						childQuestion.setRecommendationStatus(newRecommendationStatus);
						
						childQuestion.setRevisedQuestionText(latestQuestionText);
						Question.updateDomainFieldsOnClubbingFinalisation(parentQuestion, childQuestion);
						UserGroupType clerkUGT = UserGroupType.findByType(ApplicationConstants.CLERK, locale);
						childQuestion.setEditedAs(clerkUGT.getDisplayName());
						childQuestion.setEditedBy("qis_clerk");
						childQuestion.setEditedOn(new Date());
						childQuestion.merge();

						ClubbedEntity clubbedEntity=new ClubbedEntity();
						clubbedEntity.setDeviceType(childQuestion.getType());
						clubbedEntity.setLocale(childQuestion.getLocale());
						clubbedEntity.setQuestion(childQuestion);
						clubbedEntity.persist();
						parentClubbedEntities.add(clubbedEntity);
						
						if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
							for(ClubbedEntity ce:childClubbedEntities){
								Question question=ce.getQuestion();					
								/** end current clubbing workflow if pending **/
								wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question);
								if(wfDetails != null) {
									WorkflowDetails.endProcess(wfDetails);
								}
								question.removeExistingWorkflowAttributes();
								
								question.setEditedAs(childQuestion.getEditedAs());
								question.setEditedBy(childQuestion.getEditedBy());
								question.setEditedOn(childQuestion.getEditedOn());
								question.setParent(parentQuestion);
								if(question.getRecommendationStatus().getType().contains(ApplicationConstants.STATUS_PENDING_FOR_CLUBBING_APPROVAL)) {
//									if(parentQuestion.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {														
//										question.setInternalStatus(Question.findCorrespondingStatusForGivenQuestionType(systemClubbedStatus, question.getType()));
//									} else {
//										question.setInternalStatus(parentQuestion.getInternalStatus());
//									}						
//									if(parentQuestion.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {
//										newRecommendationStatus = Question.findCorrespondingStatusForGivenQuestionType(systemClubbedStatus, question.getType());
//									} else {	
//										question.setType(parentQuestion.getType());
//										newRecommendationStatus = parentQuestion.getRecommendationStatus();
//									}
									
									//TODO: either unclub this or keep ready for fresh clubbing approval as per its state
									
								} else {
									question.setType(childQuestion.getType());
									question.setStatus(newStatus);
									question.setInternalStatus(newInternalStatus);
									question.setRecommendationStatus(newInternalStatus);
									question.setRevisedQuestionText(latestQuestionText);
									Question.updateDomainFieldsOnClubbingFinalisation(parentQuestion, question);
									question.merge();
									parentClubbedEntities.add(ce);
								}								
							}			
						}
						parentQuestion.setClubbedEntities(parentClubbedEntities);
						parentQuestion.simpleMerge();

						List<ClubbedEntity> clubbedEntities=parentQuestion.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
						Integer position=1;
						for(ClubbedEntity pce:clubbedEntities){
							pce.setPosition(position);
							position++;
							pce.merge();
						}
						
						if(clubbedNumbers.length()>0) {
							clubbedNumbers.append(", " + childQuestion.getNumber());
						} else {
							clubbedNumbers.append(childQuestion.getNumber());
						}
					} catch(Exception e) {
						if(unclubbedNumbers.length()>0) {
							unclubbedNumbers.append(", " + childQuestion.getNumber() + " (some exception)");
						} else {
							unclubbedNumbers.append(childQuestion.getNumber() + " (some exception)");
						}
						continue;
					}					
				}
				returnMsg.append("SUCCESS: ");
				if(clubbedNumbers.length()>0) {					
					returnMsg.append(clubbedNumbers);
					returnMsg.append(" clubbed... ");
				}
				if(unclubbedNumbers.length()>0) {
					returnMsg.append(unclubbedNumbers);
					returnMsg.append(" not clubbed");
				}				
			} else {
				returnMsg.append("NO CHILD QUESTIONS PROVIDED FOR CLUBBING!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return returnMsg.toString();
	}
	
	@RequestMapping(value="support_pwd/{pwd_token}/{username}", method=RequestMethod.GET)
	public @ResponseBody String retrievePwdForSupport(@PathVariable("pwd_token") final String pwdToken, @PathVariable("username") final String username, final Locale appLocale) {
		if(username!=null && !username.isEmpty()) {
			CustomParameter pwdTokenCP = CustomParameter.findByName(CustomParameter.class, "SUPPORT_PASSWORD_TOKEN", "");
			if(pwdTokenCP!=null && pwdTokenCP.getValue()!=null) {
				if(pwdToken!=null && pwdToken.equals(pwdTokenCP.getValue())) {
					Credential cr = Credential.findByFieldName(Credential.class, "username", username, "");
					if(cr!=null) {
						return cr.getPassword();
					}
				}
			} else {
				Credential cr = Credential.findByFieldName(Credential.class, "username", username, "");
				if(cr!=null) {
					return cr.getPassword();
				}
			}						
		}	
		return "";
	}
	
	@RequestMapping(value="support_high_pwd/{pwd_token}/{username}", method=RequestMethod.GET)
	public @ResponseBody String retrieveHighSecurityPwdForSupport(@PathVariable("pwd_token") final String pwdToken, @PathVariable("username") final String username, final Locale appLocale) {
		if(username!=null && !username.isEmpty()) {
			CustomParameter pwdTokenCP = CustomParameter.findByName(CustomParameter.class, "SUPPORT_PASSWORD_TOKEN", "");
			if(pwdTokenCP!=null && pwdTokenCP.getValue()!=null) {
				if(pwdToken!=null && pwdToken.equals(pwdTokenCP.getValue())) {
					Credential cr = Credential.findByFieldName(Credential.class, "username", username, "");
					if(cr!=null) {
						return cr.getHighSecurityPassword();
					}
				}
			} else {
				Credential cr = Credential.findByFieldName(Credential.class, "username", username, "");
				if(cr!=null) {
					return cr.getHighSecurityPassword();
				}
			}						
		}	
		return "";
	}
	
	/**
	 * Starts device workflow at the given level.
	 * Sample: /els/admin/start_workflow/question/id/3072/status/question_recommend_admission/userGroupType/assistant/level/7
	 */
	@Transactional
	@RequestMapping(value="start_workflow/{device}/id/{id}/status/{statusType}/userGroupType/{userGroupType}/level/{level}", method=RequestMethod.GET)
	public @ResponseBody String startDeviceWorkflow(@PathVariable("device") final String deviceName,
			@PathVariable("id") final Long deviceId,
			@PathVariable("statusType") final String statusType,
			@PathVariable("userGroupType") final String userGroupTypeType,
			@PathVariable("level") final Integer level,
			final HttpServletRequest request,
			final Locale appLocale) {
		
		try {
			String locale = appLocale.toString();
			
			Status status = Status.findByType(statusType, locale);
			UserGroupType userGroupType = UserGroupType.findByType(userGroupTypeType, locale);
			
			String workflowHouseType = request.getParameter("workflowHouseType");
			
			Boolean isFlowOnRecomStatusAfterFinalDecision = Boolean.valueOf(request.getParameter("isFlowOnRecomStatusAfterFinalDecision"));
			
			String assignee = request.getParameter("assignee");
			
			if(assignee!=null && !assignee.isEmpty()) {
				try {
					User assigneeUser = User.findByUserName(assignee, locale);
					if(assigneeUser!=null && assigneeUser.getId()!=null) {
						Device.startDeviceWorkflow(deviceName, deviceId, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, assignee, locale);
					} else {
						return "USER_NOT_FOUND";
					}
				} catch(Exception e1) {
					e1.printStackTrace();
					return "USER_NOT_FOUND";
				}
			} else {
				Device.startDeviceWorkflow(deviceName, deviceId, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
			}			
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	/**
	 * Starts device workflow for given devices at the given level.
	 * Sample: /els/admin/start_workflow_bulk/question/id/3072,3075,4035/status/question_recommend_admission/userGroupType/assistant/level/7
	 */
	@Transactional
	@RequestMapping(value="start_workflow_bulk/{device}/id/{ids}/status/{statusType}/userGroupType/{userGroupType}/level/{level}", method=RequestMethod.GET)
	public @ResponseBody String startBulkDeviceWorkflow(@PathVariable("device") final String deviceName,
			@PathVariable("ids") final String deviceIds,
			@PathVariable("statusType") final String statusType,
			@PathVariable("userGroupType") final String userGroupTypeType,
			@PathVariable("level") final Integer level,
			final HttpServletRequest request,
			final Locale appLocale) {
		
		try {
			String locale = appLocale.toString();
			
			Status status = Status.findByType(statusType, locale);
			UserGroupType userGroupType = UserGroupType.findByType(userGroupTypeType, locale);	
			
			String workflowHouseType = request.getParameter("workflowHouseType");
			
			Boolean isFlowOnRecomStatusAfterFinalDecision = Boolean.valueOf(request.getParameter("isFlowOnRecomStatusAfterFinalDecision"));
			
			String assignee = request.getParameter("assignee");
			
			String[] deviceIdArr = deviceIds.split(",");
			
			for(String deviceId: deviceIdArr) {
				
				if(assignee!=null && !assignee.isEmpty()) {
					try {
						User assigneeUser = User.findByUserName(assignee, locale);
						if(assigneeUser!=null && assigneeUser.getId()!=null) {
							Device.startDeviceWorkflow(deviceName, Long.parseLong(deviceId), status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, assignee, locale);
						} else {
							return "USER_NOT_FOUND";
						}
					} catch(Exception e1) {
						e1.printStackTrace();
						return "USER_NOT_FOUND";
					}
				} else {
					Device.startDeviceWorkflow(deviceName, Long.parseLong(deviceId), status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
				}
				
			}		
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	/**
	 * Ends device workflow & removes workflow specific attributes.
	 * Sample: /els/admin/end_workflow/question/id/3072
	 */
	@Transactional
	@RequestMapping(value="end_workflow/{device}/id/{id}", method=RequestMethod.GET)
	public @ResponseBody String endDeviceWorkflow(@PathVariable("device") final String deviceName,
			@PathVariable("id") final Long deviceId,
			final HttpServletRequest request,
			final Locale appLocale) {
		
		try {
			String locale = appLocale.toString();
			
			String workflowHouseType = request.getParameter("workflowHouseType");
			
			Device.endDeviceWorkflow(deviceName, deviceId, workflowHouseType, locale);
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	/**
	 * Ends device workflow & removes workflow specific attributes for given devices.
	 * Sample: /els/admin/end_workflow_bulk/question/id/3072,3075,4035
	 */
	@Transactional
	@RequestMapping(value="end_workflow_bulk/{device}/id/{ids}", method=RequestMethod.GET)
	public @ResponseBody String endBulkDeviceWorkflow(@PathVariable("device") final String deviceName,
			@PathVariable("ids") final String deviceIds,
			final HttpServletRequest request,
			final Locale appLocale) {
		
		try {
			String locale = appLocale.toString();
			
			String workflowHouseType = request.getParameter("workflowHouseType");
			
			String[] deviceIdArr = deviceIds.split(",");
			
			for(String deviceId: deviceIdArr) {
				
				Device.endDeviceWorkflow(deviceName, Long.parseLong(deviceId), workflowHouseType, locale);
				
			}			
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	@RequestMapping(value="/test_report", method=RequestMethod.POST)
	public @ResponseBody void testReport(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		File reportFile = null;
		
//		String localeP = request.getParameter("locale");
		
//		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
//    	parameterMap.put("locale", new String[]{localeP});
//    	List resultList = Query.findReport("TEST_REPORT_QUERY", parameterMap);		
    	
//    	if(resultList!=null && !resultList.isEmpty()) {
//    		Object[] resultElement = (Object[]) resultList.get(0);
		
			StringBuffer testData = new StringBuffer();
			testData.append("<p>first page goes here..</p>");
			testData.append("<p style='page-break-before: always;'>page break inserted.. so second page should go here..</p>");
			
			TestXmlVO testXmlVO = new TestXmlVO();
			testXmlVO.setTestReportName("Test Report");
			testXmlVO.setTestData(testData.toString());
		
    		//VO Approach
//			reportFile = generateReportUsingFOP(testXmlVO, "template_test_report_vo_approach", "WORD", "test_report", locale.toString());
    		
			//Object Array Approach
			reportFile = generateReportUsingFOP(new Object[] {"Test Report", testData.toString()/*, resultList*/}, "template_test_report_objarray_approach", "WORD", "test_report", locale.toString());
			
    		if(reportFile!=null) {
    			System.out.println("Report generated successfully in WORD format!");
    			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
    		}
//    	}		
	}
	
	@Transactional
	@RequestMapping(value="/encrypt_passwords", method=RequestMethod.POST)
	public @ResponseBody String encryptPasswords(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		List<Credential> credentials = Credential.findAll(Credential.class, "username", ApplicationConstants.ASC, "");
		if(credentials!=null && !credentials.isEmpty()) {
			for(Credential cr: credentials) {
				if(cr!=null && cr.getUsername()!=null) {
					String encodedPassword = securityService.getEncodedPassword(cr.getPassword());
					cr.setPassword(encodedPassword);
					cr.merge();					
				}				
			}
		} else {
			return "NO CREDENTIAL FOUND";
		}
		return "SUCCESS";
	}
	
	@Transactional
	@RequestMapping(value="/encrypt_high_security_passwords", method=RequestMethod.POST)
	public @ResponseBody String encryptHighSecurityPasswords(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		List<Credential> credentials = Credential.findAll(Credential.class, "username", ApplicationConstants.ASC, "");
		if(credentials!=null && !credentials.isEmpty()) {
			for(Credential cr: credentials) {
				if(cr!=null && cr.getUsername()!=null && cr.getHighSecurityPassword()!=null && !cr.getHighSecurityPassword().isEmpty()) {
					String encodedPassword = securityService.getEncodedPassword(cr.getHighSecurityPassword());
					cr.setHighSecurityPassword(encodedPassword);
					cr.merge();					
				}				
			}
		} else {
			return "NO CREDENTIAL FOUND";
		}
		return "SUCCESS";
	}
	
	/**
	 * Recovers all the drafts for given question
	 * Sample: /els/admin/recover_drafts/question/id/3072
	 */
	@Transactional
	@RequestMapping(value="recover_drafts/question/id/{id}", method=RequestMethod.GET)
	public @ResponseBody String recoverQuestionDrafts(@PathVariable("id") final Long questionId) {
		try {
			Question question = Question.findById(Question.class, questionId);
			if(question==null) {
				logger.error("question id is invalid");
				throw new Exception();
			}
			List<QuestionDraft> drafts = QuestionDraft.findAllByFieldName(QuestionDraft.class, "questionId", questionId, "id", ApplicationConstants.ASC, question.getLocale());
			if(drafts!=null && !drafts.isEmpty()) {
				question.setDrafts(new LinkedHashSet<QuestionDraft>(drafts));
				question.simpleMerge();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}		
		return "SUCCESS";
	}
	
	@RequestMapping(value="/fop_template_report", method=RequestMethod.GET)
	public @ResponseBody void fopTemplateReport(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		File reportFile = null;
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		String houseTypeStr = request.getParameter("houseType");
		String sessionYearStr = request.getParameter("sessionYear");
		String sessionTypeStr = request.getParameter("sessionTypeId");
		String deviceTypeStr = request.getParameter("deviceTypeId");
		String reportQuery = request.getParameter("reportQuery");
		String xsltFileName = request.getParameter("xsltFileName");
		String outputFormat = request.getParameter("outputFormat");
		String reportFileName = request.getParameter("reportFileName");
		
		
		if(houseTypeStr!=null && !houseTypeStr.isEmpty()
				&& sessionYearStr!=null && !sessionYearStr.isEmpty()
				&& sessionTypeStr!=null && !sessionTypeStr.isEmpty()
				&& deviceTypeStr!=null && !deviceTypeStr.isEmpty()
				&& reportQuery!=null && !reportQuery.isEmpty()
				&& xsltFileName!=null && !xsltFileName.isEmpty()
				&& outputFormat!=null && !outputFormat.isEmpty()
				&& reportFileName!=null && !reportFileName.isEmpty()) {
			
			try {
//				/** find houseType **/
//				HouseType houseType = HouseType.findByType(houseTypeStr, locale.toString());
//				if(houseType==null) {
//					houseType = HouseType.findByName(houseTypeStr, locale.toString());
//				}
//				if(houseType==null) {
//					houseType = HouseType.findById(HouseType.class, Long.parseLong(houseTypeStr));
//				}
//				/** find sessionYear **/
//				Integer sessionYear = Integer.parseInt(sessionYearStr);
//				/** find sessionType **/
//				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(sessionTypeStr));
//				/** find session **/
//				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
				Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		    	parameterMap.put("locale", new String[]{locale.toString()});
		    	@SuppressWarnings("rawtypes")
				List resultList = Query.findReport(reportQuery, parameterMap);
				
		    	if(resultList!=null && !resultList.isEmpty()) {
		    		//Object[] resultElement = (Object[]) resultList.get(0);
				
					//VO Approach
//					reportFile = generateReportUsingFOP(reportXmlVO, xsltFileName, outputFormat, reportFileName, locale.toString());
		    		
					//Object Array Approach
		    		reportFile = generateReportUsingFOP(new Object[] {resultList}, xsltFileName, outputFormat, reportFileName, locale.toString());
					
		    		if(reportFile!=null) {
		    			System.out.println("Report generated successfully in " + outputFormat + " format!");
		    			openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
		    		}
	    		}
			} catch(Exception e) {
				
			}			
		} else {
			isError = true;
			logger.error("**** Check request parameters houseType, sessionType, sessionYear, deviceType for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.reqparam.null", locale.toString());
		}
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * Recovers all the drafts for given cutmotiondate
	 * Sample: /els/admin/recover_drafts/cutmotiondate/id/3072
	 */
	@Transactional
	@RequestMapping(value="recover_drafts/cutmotiondate/id/{id}", method=RequestMethod.GET)
	public @ResponseBody String recoverCutMotionDateDrafts(@PathVariable("id") final Long cutMotionDateId) {
		try {
			CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, cutMotionDateId);
			if(cutMotionDate==null) {
				logger.error("cutmotiondate id is invalid");
				throw new Exception();
			}
			List<CutMotionDateDraft> drafts = CutMotionDate.findDraftsForGivenCutMotionDate(cutMotionDateId);
			if(drafts!=null && !drafts.isEmpty()) {
				cutMotionDate.setDrafts(drafts);
				cutMotionDate.simpleMerge();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}		
		return "SUCCESS";
	}
	
	/**
	 * Recovers all the cutmotiondepartmentdatepriority details for given cutmotiondate
	 * Sample: /els/admin/recover_cutmotiondepartmentdatepriority_details/cutmotiondate/id/3072
	 */
	@Transactional
	@RequestMapping(value="recover_cutmotiondepartmentdatepriority_details/cutmotiondate/id/{id}", method=RequestMethod.GET)
	public @ResponseBody String recoverCutMotionDepartmentDatePriorityDetails(@PathVariable("id") final Long cutMotionDateId) {
		try {
			CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, cutMotionDateId);
			if(cutMotionDate==null) {
				logger.error("cutmotiondate id is invalid");
				throw new Exception();
			}
			List<CutMotionDepartmentDatePriority> departmentDates = CutMotionDate.findDepartmentDatePriorityDetailsForGivenCutMotionDate(cutMotionDateId);
			if(departmentDates!=null && !departmentDates.isEmpty()) {
				cutMotionDate.setDepartmentDates(departmentDates);
				cutMotionDate.simpleMerge();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}		
		return "SUCCESS";
	}
	
	/**
	 * Recovers all the drafts for given cutmotion
	 * Sample: /els/admin/recover_drafts/cutmotion/id/3072
	 */
	@Transactional
	@RequestMapping(value="recover_drafts/cutmotion/id/{id}", method=RequestMethod.GET)
	public @ResponseBody String recoverCutMotionDrafts(@PathVariable("id") final Long cutMotionId) {
		try {
			CutMotion cutMotion = CutMotion.findById(CutMotion.class, cutMotionId);
			if(cutMotion==null) {
				logger.error("cutmotion id is invalid");
				throw new Exception();
			}
			List<CutMotionDraft> drafts = CutMotion.findDraftsForGivenDevice(cutMotionId);
			if(drafts!=null && !drafts.isEmpty()) {
				cutMotion.setDrafts(drafts);
				cutMotion.simpleMerge();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}		
		return "SUCCESS";
	}
	
	@Transactional
	@RequestMapping(value="set_lastAnswerReceivingDateFromDepartment_for_unstarred_questions/id/{ids}", method=RequestMethod.GET)
	public @ResponseBody String setLastAnswerReceivingDateFromDepartmentForUnstarredQuestions(@PathVariable("ids") final String deviceIds, final HttpServletRequest request, final Locale appLocale) {
		
		try {
			String locale = appLocale.toString();
			
			String[] deviceIdArr = deviceIds.split(",");
			
			for(String deviceId: deviceIdArr) {
				/** Set Last Date of Answer Receiving from Department **/				
				Question question = Question.findById(Question.class, Long.parseLong(deviceId));
				if(question!=null) {
					Date lastDateOfAnswerReceiving = null;
					String daysCountForReceivingAnswerFromDepartment = "30";
					CustomParameter csptDaysCountForReceivingAnswerFromDepartment = CustomParameter.findByName(CustomParameter.class, question.getType().getType().toUpperCase()+"_"+question.getHouseType().getType().toUpperCase()+"_"+ApplicationConstants.DAYS_COUNT_FOR_RECEIVING_ANSWER_FROM_DEPARTMENT, "");
					if(csptDaysCountForReceivingAnswerFromDepartment!=null
							&& csptDaysCountForReceivingAnswerFromDepartment.getValue()!=null) {
						daysCountForReceivingAnswerFromDepartment = csptDaysCountForReceivingAnswerFromDepartment.getValue();
					}
					if(question.getAnswerRequestedDate()!=null) {
						lastDateOfAnswerReceiving = Holiday.getNextWorkingDateFrom(question.getAnswerRequestedDate(), Integer.parseInt(daysCountForReceivingAnswerFromDepartment), locale);
					} else {
						lastDateOfAnswerReceiving = Holiday.getNextWorkingDateFrom(new Date(), Integer.parseInt(daysCountForReceivingAnswerFromDepartment), locale);
					}
					question.setLastDateOfAnswerReceiving(lastDateOfAnswerReceiving);
					question.simpleMerge();
				}				
			}		
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return "SUCCESS";
	}
	
	@RequestMapping(value="broadcast_downtime", method=RequestMethod.POST)
	public @ResponseBody String broadcastDowntime(final HttpServletRequest request, final Locale appLocale) {
		try {			
			notificationService.sendVolatileNotificationToAllActiveUsers("Please save your work and logout the system for 10 mins!", appLocale.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}		
		return "SUCCESS";
	}
	
	@RequestMapping(value="test_broadcast", method=RequestMethod.POST)
	public @ResponseBody String testBroadcast(final HttpServletRequest request, final Locale appLocale) {
		try {
			String receivers = request.getParameter("receivers");
//			if(receivers!=null && !receivers.isEmpty()) {
//				NotificationHandler.broadcastMessage("broadcaster", receivers);
//			} else {			
//				return "MISSING_RECEIVERS";
//			}
			//NotificationHandler.broadcastMessage("Here is the broadcast for all!");
			notificationService.sendNotification("broadcaster", "Here is the broadcast!", receivers, appLocale.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}		
		return "SUCCESS";
	}
	
	@Transactional	
	@RequestMapping(value="/club/adjournmentmotion/parent/{parentId}/child/{childID}", method=RequestMethod.GET)
	public @ResponseBody String clubAdjournmentMotions(@PathVariable("parentId") final Long parentId,
			@PathVariable("childId") final Long childId,
			final HttpServletRequest request,
			final Locale appLocale) {	
		try {
			String locale = appLocale.toString();
			AdjournmentMotion parent =  AdjournmentMotion.findById(AdjournmentMotion.class, parentId);
			AdjournmentMotion child =  AdjournmentMotion.findById(AdjournmentMotion.class, childId);
			
			Boolean result = AdjournmentMotion.club(parent, child, locale);
			if(result){
				return "SUCCESS";
			}
			else{
				return "FAILURE";
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	
	@Transactional
	@RequestMapping(value = "/reset_user_password")
	public @ResponseBody String resetUserPassword(HttpServletRequest request, Locale locale){
		String retVal = "FAILURE";
		try{
			String username = request.getParameter("username");
			if(username!=null && !username.isEmpty()) {
				Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
				if(credential != null) {
					String strPassword = Credential.generatePassword(Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
					String encodedPassword = securityService.getEncodedPassword(strPassword);
					credential.setPassword(encodedPassword);
					credential.setPasswordChangeCount(1);
					credential.setPasswordChangeDateTime(new Date());
					credential.merge();
					retVal = "SUCCESS";
				} else {
					retVal = "CREDENTIAL_NOT_FOUND";
				}
			} else {
				retVal = "USERNAME_NOT_SPECIFIED";
			}
		} catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	/***
	 *	Below method will replicate entire ministries and departments allocations for previous working house to current chief minister before expansion
	 *  Before running the method, make sure that all to be ended entries of active members_ministries and their members_departments 
	 *  must have their to_date field set to previousMemberMinistriesToDate which is passed to this method
	 **/
	@Transactional
	@RequestMapping(value = "/{chiefMinisterMemberId}/replicate_ministries_departments_before_expansion")
	public @ResponseBody String replicateMinistriesDepartmentsForCM(HttpServletRequest request, @PathVariable("chiefMinisterMemberId") final Long chiefMinisterMemberId, Locale locale){
		String retVal = "FAILURE";
		try{
			String strPreviousMemberMinistriesToDate = request.getParameter("previousMemberMinistriesToDate");
			if(strPreviousMemberMinistriesToDate!=null && !strPreviousMemberMinistriesToDate.isEmpty()) {
				Date previousMemberMinistriesToDate = FormaterUtil.formatStringToDate(strPreviousMemberMinistriesToDate, ApplicationConstants.DB_DATEFORMAT, locale.toString());
				List<MemberMinister> existingMemberMinisterList = MemberMinister.findAllByFieldName(MemberMinister.class, "ministryToDate", previousMemberMinistriesToDate, "id", ApplicationConstants.ASC, locale.toString());
				if(existingMemberMinisterList!=null) {
					Member chiefMinisterMember = Member.findById(Member.class, chiefMinisterMemberId);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(previousMemberMinistriesToDate);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					Date fromDateForNew = calendar.getTime();
					calendar.add(Calendar.YEAR, 1);
					Date toDateForNew = calendar.getTime();
					for(MemberMinister mm: existingMemberMinisterList) {
						MemberMinister newMemberMinister = new MemberMinister();
						newMemberMinister.setLocale(mm.getLocale());
						newMemberMinister.setMember(chiefMinisterMember);
						newMemberMinister.setDesignation(mm.getDesignation());
						newMemberMinister.setMinistry(mm.getMinistry());
						newMemberMinister.setMinistryFromDate(fromDateForNew);
						newMemberMinister.setMinistryToDate(toDateForNew);
						newMemberMinister.setPriority(mm.getPriority());
						//set member departments list for the member minister
						if(mm.getMemberDepartments()!=null) {
							List<MemberDepartment> newMemberDepartments = new ArrayList<MemberDepartment>();
							for(MemberDepartment md: mm.getMemberDepartments()) {
								if(md.getToDate()!=null && md.getToDate().before(previousMemberMinistriesToDate)) {
									continue;
								} else {
									MemberDepartment newMemberDepartment = new MemberDepartment();
									newMemberDepartment.setLocale(md.getLocale());
									newMemberDepartment.setDepartment(md.getDepartment());
									newMemberDepartment.setFromDate(fromDateForNew);
									newMemberDepartment.setToDate(toDateForNew);
									if(md.getSubDepartments()!=null) {
										List<SubDepartment> newSubDepartments = new ArrayList<SubDepartment>();
										for(SubDepartment sd: md.getSubDepartments()) {
											newSubDepartments.add(sd);
										}
										if(newSubDepartments.size()>=1) {
											newMemberDepartment.setSubDepartments(newSubDepartments);
										}
									}
									newMemberDepartment.setIsIndependentCharge(md.getIsIndependentCharge());
									newMemberDepartments.add(newMemberDepartment);
								}							
							}
							if(newMemberDepartments.size()>=1) {
								newMemberMinister.setMemberDepartments(newMemberDepartments);
							}
						}
						String strOathDate = request.getParameter("oathDate");
						if(strOathDate!=null) {
							Date oathDate = FormaterUtil.formatStringToDate(strOathDate, ApplicationConstants.DB_DATEFORMAT, locale.toString());
							newMemberMinister.setOathDate(oathDate);
						}
						newMemberMinister.persist();
					}
					retVal = "SUCCESS";
					
				} else {
					retVal = "No member ministry found to be replicated!";
				}
				
			} else {
				retVal = "previousMemberMinistriesToDate not provided";
			}
		} catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	@Transactional
	@RequestMapping(value="club_proprietypoints_on_record/child/{childIds}/parent/{parentId}", method=RequestMethod.GET)
	public @ResponseBody String ClubProprietyPointsOnRecord(@PathVariable("childIds") final String childIds,
			@PathVariable("parentId") final String parentId,
			final Locale appLocale) {
		StringBuffer returnMsg = new StringBuffer();
		StringBuffer clubbedNumbers = new StringBuffer();
		StringBuffer unclubbedNumbers = new StringBuffer();
		try {
			String locale = appLocale.toString();
			String[] cIds = childIds.split(","); 
			ProprietyPoint parentProprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, Long.parseLong(parentId));
			Status systemClubbedStatus = Status.findByType(ApplicationConstants.PROPRIETYPOINT_SYSTEM_CLUBBED, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION, locale);
			if(parentProprietyPoint.getParent()!=null) {
				returnMsg.append("NOT ALLOWED: Parent proprietyPoint number " + parentProprietyPoint.getNumber() + " is already clubbed");
				return returnMsg.toString();
			}
			if(cIds.length>0){
				for(int i=0 ; i<cIds.length;i++){
					ProprietyPoint childProprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, Long.parseLong(cIds[i]));
					if(childProprietyPoint.getParent()!=null) {
//						childProprietyPoint = childProprietyPoint.getParent();
						if(unclubbedNumbers.length()>0) {
							unclubbedNumbers.append(", " + childProprietyPoint.getNumber() + " (already clubbed)");
						} else {
							unclubbedNumbers.append(childProprietyPoint.getNumber() + " (already clubbed)");
						}
						continue;
					}
					try {
						List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
						String latestProprietyPointText = null;
						if(parentProprietyPoint.getClubbedEntities()!=null && !parentProprietyPoint.getClubbedEntities().isEmpty()){
							for(ClubbedEntity j:parentProprietyPoint.getClubbedEntities()){
								// parent & child need not be disjoint. They could
								// be present in each other's hierarchy.
								Long childQnId = childProprietyPoint.getId();
								ProprietyPoint clubbedQn = j.getProprietyPoint();
								Long clubbedQnId = clubbedQn.getId();
								if(! childQnId.equals(clubbedQnId)) {
									/** fetch parent's latest proprietyPoint text from first of its children **/
									if(latestProprietyPointText==null) {
										latestProprietyPointText = clubbedQn.getRevisedPointsOfPropriety();
										if(latestProprietyPointText==null || latestProprietyPointText.isEmpty()) {
											latestProprietyPointText = clubbedQn.getPointsOfPropriety();
										}
									}
									parentClubbedEntities.add(j);
								}
							}			
						}
						
						List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
						if(childProprietyPoint.getClubbedEntities()!=null && !childProprietyPoint.getClubbedEntities().isEmpty()){
							for(ClubbedEntity k:childProprietyPoint.getClubbedEntities()){
								// parent & child need not be disjoint. They could
								// be present in each other's hierarchy.
								Long parentQnId = parentProprietyPoint.getId();
								ProprietyPoint clubbedQn = k.getProprietyPoint();
								Long clubbedQnId = clubbedQn.getId();
								if(! parentQnId.equals(clubbedQnId)) {
									childClubbedEntities.add(k);
								}
							}
						}
						
						WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(childProprietyPoint);
						if(wfDetails != null) {
							WorkflowDetails.endProcess(wfDetails);
						}
						childProprietyPoint.removeExistingWorkflowAttributes();
						
						/** fetch parent's latest proprietyPoint text **/
						if(latestProprietyPointText==null) {
							latestProprietyPointText = parentProprietyPoint.getRevisedPointsOfPropriety();
							if(latestProprietyPointText==null || latestProprietyPointText.isEmpty()) {
								latestProprietyPointText = parentProprietyPoint.getPointsOfPropriety();
							}
						}
						
						childProprietyPoint.setParent(parentProprietyPoint);
						childProprietyPoint.setClubbedEntities(null);		
						
						Status newStatus = parentProprietyPoint.getStatus();
						childProprietyPoint.setStatus(newStatus);
						Status newInternalStatus = null;
						if(parentProprietyPoint.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {														
							newInternalStatus = systemClubbedStatus;
						} else {	
							childProprietyPoint.setDeviceType(parentProprietyPoint.getDeviceType());
							newInternalStatus = parentProprietyPoint.getInternalStatus();
						}						
						childProprietyPoint.setInternalStatus(newInternalStatus);
						Status newRecommendationStatus = null;
						if(parentProprietyPoint.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {
							newRecommendationStatus = systemClubbedStatus;
						} else {	
							childProprietyPoint.setDeviceType(parentProprietyPoint.getDeviceType());
							newRecommendationStatus = parentProprietyPoint.getInternalStatus();
						}						
						childProprietyPoint.setRecommendationStatus(newRecommendationStatus);
						
						childProprietyPoint.setRevisedPointsOfPropriety(latestProprietyPointText);
						ProprietyPoint.updateDomainFieldsOnClubbingFinalisation(parentProprietyPoint, childProprietyPoint);
						UserGroupType clerkUGT = UserGroupType.findByType(ApplicationConstants.CLERK, locale);
						childProprietyPoint.setEditedAs(clerkUGT.getDisplayName());
						childProprietyPoint.setEditedBy("qis_clerk");
						childProprietyPoint.setEditedOn(new Date());
						childProprietyPoint.merge();

						ClubbedEntity clubbedEntity=new ClubbedEntity();
						clubbedEntity.setDeviceType(childProprietyPoint.getDeviceType());
						clubbedEntity.setLocale(childProprietyPoint.getLocale());
						clubbedEntity.setProprietyPoint(childProprietyPoint);
						clubbedEntity.persist();
						parentClubbedEntities.add(clubbedEntity);
						
						if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
							for(ClubbedEntity ce:childClubbedEntities){
								ProprietyPoint proprietyPoint=ce.getProprietyPoint();					
								/** end current clubbing workflow if pending **/
								wfDetails = WorkflowDetails.findCurrentWorkflowDetail(proprietyPoint);
								if(wfDetails != null) {
									WorkflowDetails.endProcess(wfDetails);
								}
								proprietyPoint.removeExistingWorkflowAttributes();
								
								proprietyPoint.setEditedAs(childProprietyPoint.getEditedAs());
								proprietyPoint.setEditedBy(childProprietyPoint.getEditedBy());
								proprietyPoint.setEditedOn(childProprietyPoint.getEditedOn());
								proprietyPoint.setParent(parentProprietyPoint);
								if(proprietyPoint.getRecommendationStatus().getType().contains(ApplicationConstants.STATUS_PENDING_FOR_CLUBBING_APPROVAL)) {
//									if(parentProprietyPoint.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {														
//										proprietyPoint.setInternalStatus(ProprietyPoint.findCorrespondingStatusForGivenProprietyPointType(systemClubbedStatus, proprietyPoint.getDeviceType()));
//									} else {
//										proprietyPoint.setInternalStatus(parentProprietyPoint.getInternalStatus());
//									}						
//									if(parentProprietyPoint.getStatus().getPriority().intValue()<admitStatus.getPriority().intValue()) {
//										newRecommendationStatus = ProprietyPoint.findCorrespondingStatusForGivenProprietyPointType(systemClubbedStatus, proprietyPoint.getDeviceType());
//									} else {	
//										proprietyPoint.setDeviceType(parentProprietyPoint.getDeviceType());
//										newRecommendationStatus = parentProprietyPoint.getRecommendationStatus();
//									}
									
									//TODO: either unclub this or keep ready for fresh clubbing approval as per its state
									
								} else {
									proprietyPoint.setDeviceType(childProprietyPoint.getDeviceType());
									proprietyPoint.setStatus(newStatus);
									proprietyPoint.setInternalStatus(newInternalStatus);
									proprietyPoint.setRecommendationStatus(newInternalStatus);
									proprietyPoint.setRevisedPointsOfPropriety(latestProprietyPointText);
									ProprietyPoint.updateDomainFieldsOnClubbingFinalisation(parentProprietyPoint, proprietyPoint);
									proprietyPoint.merge();
									parentClubbedEntities.add(ce);
								}								
							}			
						}
						parentProprietyPoint.setClubbedEntities(parentClubbedEntities);
						parentProprietyPoint.simpleMerge();

						List<ClubbedEntity> clubbedEntities=parentProprietyPoint.findClubbedEntitiesByDeviceNumber(ApplicationConstants.ASC);
						Integer position=1;
						for(ClubbedEntity pce:clubbedEntities){
							pce.setPosition(position);
							position++;
							pce.merge();
						}
						
						if(clubbedNumbers.length()>0) {
							clubbedNumbers.append(", " + childProprietyPoint.getNumber());
						} else {
							clubbedNumbers.append(childProprietyPoint.getNumber());
						}
					} catch(Exception e) {
						if(unclubbedNumbers.length()>0) {
							unclubbedNumbers.append(", " + childProprietyPoint.getNumber() + " (some exception)");
						} else {
							unclubbedNumbers.append(childProprietyPoint.getNumber() + " (some exception)");
						}
						continue;
					}					
				}
				returnMsg.append("SUCCESS: ");
				if(clubbedNumbers.length()>0) {					
					returnMsg.append(clubbedNumbers);
					returnMsg.append(" clubbed... ");
				}
				if(unclubbedNumbers.length()>0) {
					returnMsg.append(unclubbedNumbers);
					returnMsg.append(" not clubbed");
				}				
			} else {
				returnMsg.append("NO CHILD PROPRIETYPOINTS PROVIDED FOR CLUBBING!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
		
		return returnMsg.toString();
	}
	
}