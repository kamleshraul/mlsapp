package org.mkcl.els.controller;

import java.io.File;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.xmlvo.TestXmlVO;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.domain.chart.ChartEntry;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
			
			Device.startDeviceWorkflow(deviceName, deviceId, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
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
			
			String[] deviceIdArr = deviceIds.split(",");
			
			for(String deviceId: deviceIdArr) {
				
				Device.startDeviceWorkflow(deviceName, Long.parseLong(deviceId), status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
				
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
	
}