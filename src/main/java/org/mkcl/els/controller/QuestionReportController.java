package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MinistryVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RoundVO;
import org.mkcl.els.common.xmlvo.QuestionIntimationLetterXmlVO;
import org.mkcl.els.common.xmlvo.QuestionYaadiSuchiXmlVO;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("question/report")
public class QuestionReportController extends BaseController{
	
	/**
	 * To generate statistics report for section officer
	 * @param request
	 * @param response
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(value="/statreport", method=RequestMethod.GET)
	public String getStatsReport(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale){
		try{
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strHouseType = request.getParameter("houseType");
			String strDeviceType = request.getParameter("deviceType");
			
			if(strSessionYear != null && !strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()
					&& strDeviceType != null && !strDeviceType.isEmpty()){

				//find session
				SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, new Integer(strSessionYear));
				DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				String[] strUsergroups = null;
				CustomParameter csptStatReportUserGroups = CustomParameter.findByName(CustomParameter.class, "QIS_ALLOWED_USERGROUPS_FOR_STAT_REPORT_" + strHouseType.toUpperCase() , "");				
				if(csptStatReportUserGroups != null){
					strUsergroups = csptStatReportUserGroups.getValue().split(",");
				}
								
				model.addAttribute("day", FormaterUtil.getDayInMarathi(FormaterUtil.formatDateToString(new Date(), "EEEE", locale.toString()), locale.toString()));
				model.addAttribute("currDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));								
				model.addAttribute("statsHouseType", houseType.getType());
				
				//stat report
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("houseTypeId", new String[]{houseType.getId().toString()});
				parameters.put("sessionTypeId", new String[]{sessionType.getId().toString()});
				parameters.put("sessionYear", new String[]{strSessionYear});
				parameters.put("wfStatusPending", new String[]{"PENDING"});
				parameters.put("wfStatusCompleted", new String[]{"COMPLETED"});
								
				for(String numUg : strUsergroups){
					
					String[] ugNum = numUg.split(":");
					
					switch(Integer.parseInt(ugNum[0])){
						case 1:{
							parameters.put("ugUS", new String[]{ugNum[1]});
						}break;
						case 2:{
							parameters.put("ugUSC", new String[]{ugNum[1]});
						}break;
						case 3:{
							parameters.put("ugPS", new String[]{ugNum[1]});
						}break;
						case 4:{
							parameters.put("ugSPCM", new String[]{ugNum[1]});
						}break;
					}
				}
				
				model.addAttribute("report", Query.findReport("QIS_STATS_REPORT", parameters));
				
				//typist stat report
				model.addAttribute("typistReport", Query.findReport("QIS_STATS_REPORT_TYPIST", parameters));
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "question/reports/statreport";
	}
	
	@RequestMapping(value="/currentstatusreport", method=RequestMethod.GET)
	public String getCurrentStatusReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){

		String strDevice = request.getParameter("device");
		String strReportType = request.getParameter("reportType");
		String strWfid = request.getParameter("wfdId");
		String strQid = request.getParameter("qId");
		
		WorkflowDetails wfd = null;
		if(strWfid != null && !strWfid.isEmpty()){
			wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
			if(wfd != null){
				model.addAttribute("qId", wfd.getDeviceId());
			}
		}
		
		if(strQid != null && !strQid.isEmpty()){
			model.addAttribute("qId", strQid);
		}
		
		model.addAttribute("reportType", strReportType);
		if(strDevice != null && !strDevice.isEmpty()){		
			model.addAttribute("device", strDevice);
		}

		response.setContentType("text/html; charset=utf-8");
		return "question/reports/statusreport";
	}

	@RequestMapping(value="/{qId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("qId") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
				
		response.setContentType("text/html; charset=utf-8");		
		return QuestionReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
	}
	
	@RequestMapping(value="/generateIntimationLetter" ,method=RequestMethod.GET)
	public @ResponseBody void generateIntimationLetter(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String strQuestionId = request.getParameter("questionId");		
		String strWorkflowId = request.getParameter("workflowId");
		String intimationLetterFilter=request.getParameter("intimationLetterFilter");

		//in case if request comes from workflow page, question id is retrived from workflow details
		if(strWorkflowId!=null && !strWorkflowId.isEmpty()) {
			WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowId));
			if(workflowDetails!=null) {
				strQuestionId = workflowDetails.getDeviceId();
			}
		}

		if(strQuestionId!=null && !strQuestionId.isEmpty()) {
			Question question = Question.findById(Question.class, Long.parseLong(strQuestionId));
			if(question!=null) {
				QuestionIntimationLetterXmlVO letterVO = new QuestionIntimationLetterXmlVO();
				DeviceType deviceType = question.getType();
				letterVO.setDeviceType(deviceType.getName());
				if(question.getNumber()!=null) {
					letterVO.setNumber(FormaterUtil.formatNumberNoGrouping(question.getNumber(), question.getLocale()));
				}
				HouseType houseType = question.getHouseType();
				if(houseType!=null) {
					letterVO.setHouseType(houseType.getType());
					letterVO.setHouseTypeName(houseType.getName());
				}	
				Session session = question.getSession();
				if(session!=null) {					
					letterVO.setSessionPlace(session.getPlace().getPlace());
					if(session.getNumber()!=null) {
						letterVO.setSessionNumber(session.getNumber().toString());
					}		
					if(session.getYear()!=null) {
						letterVO.setSessionYear(FormaterUtil.formatNumberNoGrouping(session.getYear(), locale.toString()));
					}
				}
				Group group = question.getGroup();
				if(group!=null) {
					letterVO.setGroupNumber(FormaterUtil.formatNumberNoGrouping(group.getNumber(), question.getLocale()));
				}
				String formattedText = "";
				if(question.getRevisedSubject()!=null && !question.getRevisedSubject().isEmpty()) {
					formattedText = question.getRevisedSubject();					
				} else {
					formattedText = question.getSubject();
				}
				formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, question.getLocale());
				letterVO.setSubject(formattedText);
				if(question.getParent()!=null) {
					formattedText = question.getQuestionText();
				} else {
					if(question.getRevisedQuestionText()!=null && !question.getRevisedQuestionText().isEmpty()) {
						formattedText = question.getRevisedQuestionText();					
					} else {
						formattedText = question.getQuestionText();
					}
				}				
				formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, question.getLocale());
				letterVO.setQuestionText(formattedText);	
				/**** populating member names with customized formatting ****/
				String memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
				CustomParameter memberNameFormatParameter = null;
				if(question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_LOWERHOUSE", "");
				} else if(question.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_UPPERHOUSE", "");
				}
				if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
					memberNameFormat = memberNameFormatParameter.getValue();
				}
				Member primaryMember = question.getPrimaryMember();
				if(primaryMember!=null) {
					String primaryMemberName = primaryMember.findNameInGivenFormat(memberNameFormat);
					if(primaryMemberName!=null) {
						letterVO.setPrimaryMemberName(primaryMemberName);
					} else {
						letterVO.setPrimaryMemberName("");
					}
				} else {
					//error code: No Primary Member Found.
				}
				String allMemberNames = null;
				if(question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					allMemberNames = question.findAllMemberNamesWithConstituencies(memberNameFormat);
				} else if(question.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					allMemberNames = question.findAllMemberNames(memberNameFormat);
				}						
				if(allMemberNames!=null && !allMemberNames.isEmpty()) {
					letterVO.setMemberNames(allMemberNames);
					if(allMemberNames.split(",").length>1) {
						letterVO.setHasMoreMembers("yes");
					} else {
						letterVO.setHasMoreMembers("no");
					}					
				} else {
					isError = true;
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.intimationLetter.noMemberFound", locale.toString());
				}	
				try {
					MemberMinister memberMinister = Question.findMemberMinisterIfExists(question);
					if(memberMinister!=null) {
						letterVO.setPrimaryMemberDesignation(memberMinister.getDesignation().getName());
					} else {
						letterVO.setPrimaryMemberDesignation("");
					}
				} catch(ELSException ex) {
					letterVO.setPrimaryMemberDesignation("");
				}
				SubDepartment subDepartment = question.getSubDepartment();
				if(subDepartment!=null) {
					letterVO.setSubDepartment(subDepartment.getName());
				}
				Department department = subDepartment.getDepartment();
				if(department!=null) {
					letterVO.setDepartment(department.getName());
				}
				/** answering date for starred question **/
				if(question.getType().getType().trim().equals(ApplicationConstants.STARRED_QUESTION)) {
					QuestionDates questionDates = Question.findQuestionDatesForStarredQuestion(question);
					if(questionDates!=null) {
						Date answeringDate = questionDates.getAnsweringDate();
						if(answeringDate!=null) {
							letterVO.setAnsweringDate(FormaterUtil.formatDateToString(answeringDate, "dd-MM-yyyy", question.getLocale()));
						}
						Date lastSendingDateToDepartment = questionDates.getLastReceivingDateFromDepartment();
						if(lastSendingDateToDepartment!=null) {
							letterVO.setLastSendingDateToDepartment(FormaterUtil.formatDateToString(lastSendingDateToDepartment, "dd-MM-yyyy", question.getLocale()));
						}
					}
				} else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
					if(question.getDateOfAnsweringByMinister()!=null){
						SimpleDateFormat dbFormat = null;
						CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
						if(dbDateFormat!=null){
							dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
						}
						//Added the following code to solve the marathi month and day issue
						String[] strAnsweringDates=dbFormat.format(question.getDateOfAnsweringByMinister()).split(",");
						String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
						String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
						String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
						String formattedAnsweringDate = answeringDay+","+strAnsweringMonth[0] + " " + answeringMonth + " " + strAnsweringDates[2];
						letterVO.setAnsweringDate(formattedAnsweringDate);
					}
				}else{
					//answeringDate = question.getDiscussionDate();
				}

				/** referenced question details (later should come through referenced entities) **/
				if(question.getQuestionreferenceText()!=null) {
					formattedText = FormaterUtil.formatNumbersInGivenText(question.getQuestionreferenceText(), question.getLocale());
					letterVO.setQuestionReferenceText(formattedText);
				} else {
					letterVO.setQuestionReferenceText("");
				}

				Status status = question.getInternalStatus();	
				String statusType=status.getType();
				if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION) || statusType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
					if(question.getRejectionReason()!=null && !question.getRejectionReason().isEmpty()) {
						formattedText = FormaterUtil.formatNumbersInGivenText(question.getRejectionReason(), question.getLocale());
						if(formattedText.endsWith("<br><p></p>")) {
							formattedText = formattedText.substring(0, formattedText.length()-11);
						} else if(formattedText.endsWith("<p></p>")) {
							formattedText = formattedText.substring(0, formattedText.length()-7);
						}
						letterVO.setRejectionReason(formattedText);
					} else {
						letterVO.setRejectionReason("");
					}
				}
				
				/** factual position (clarification) received from department **/
				if(question.getFactualPosition()!=null) {
					formattedText = FormaterUtil.formatNumbersInGivenText(question.getFactualPosition(), question.getLocale());
					if(formattedText.endsWith("<br><p></p>")) {
						formattedText = formattedText.substring(0, formattedText.length()-11);
					} else if(formattedText.endsWith("<p></p>")) {
						formattedText = formattedText.substring(0, formattedText.length()-7);
					}
					letterVO.setFactualPosition(formattedText);
				} else {
					letterVO.setFactualPosition("");
				} 
				
				/**** populating fields for half-hour discussion from questions ****/
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					Question referredQuestion = question.getHalfHourDiscusionFromQuestionReference();
					if(referredQuestion!=null) {
						if(referredQuestion.getNumber()!=null) {
							letterVO.setReferredQuestionNumber(FormaterUtil.formatNumberNoGrouping(referredQuestion.getNumber(), locale.toString()));
						}						
						DeviceType referredQuestionDeviceType = referredQuestion.getType();
						if(referredQuestionDeviceType!=null) {
							letterVO.setReferredQuestionDeviceType(referredQuestionDeviceType.getType());
						}
						Member referredQuestionMember = referredQuestion.getPrimaryMember();
						if(referredQuestionMember!=null) {
							letterVO.setReferredQuestionMemberName(referredQuestionMember.findNameInGivenFormat(memberNameFormat));
						}						
						if(referredQuestionDeviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
							QuestionDates answeringQuestionDate = referredQuestion.getAnsweringDate();
							if(answeringQuestionDate!=null && answeringQuestionDate.getAnsweringDate()!=null) {
								letterVO.setReferredQuestionAnsweringDate(FormaterUtil.formatDateToString(answeringQuestionDate.getAnsweringDate(), ApplicationConstants.ROTATIONORDER_DATEFORMAT, locale.toString()));
							}
						}
					} else {
						String referredQuestionNumber = question.getHalfHourDiscusionFromQuestionReferenceNumber();
						if(referredQuestionNumber!=null) {
							letterVO.setReferredQuestionNumber(referredQuestionNumber);
						}
						if(question.getReferenceDeviceType()!=null) {
							letterVO.setReferredQuestionDeviceType(question.getReferenceDeviceType());
						}
						if(question.getReferenceDeviceMember()!=null) {
							letterVO.setReferredQuestionMemberName(question.getReferenceDeviceMember());
						}
						Date referrredQuestionAnsweringDate = question.getReferenceDeviceAnswerDate();
						if(referrredQuestionAnsweringDate!=null) {
							letterVO.setReferredQuestionAnsweringDate(FormaterUtil.formatDateToString(referrredQuestionAnsweringDate, ApplicationConstants.ROTATIONORDER_DATEFORMAT, locale.toString()));
						}
					}					
				}
				
				/**** populating fields for half-hour discussion (common for standalone & from question) ****/
				if(question.getRevisedReason()!=null && !question.getRevisedReason().isEmpty()) {
					formattedText = question.getRevisedReason();
				} else if(question.getReason()!=null) {
					formattedText = question.getReason();					
				} else {
					formattedText = "";
				}
				formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, locale.toString());
				if(formattedText.endsWith("<br><p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-11);
				} else if(formattedText.endsWith("<p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-7);
				}
				letterVO.setReason(formattedText);
				
				if(question.getRevisedBriefExplanation()!=null && !question.getRevisedBriefExplanation().isEmpty()) {
					formattedText = question.getRevisedBriefExplanation();
				} else if(question.getBriefExplanation()!=null) {
					formattedText = question.getBriefExplanation();
				} else {
					formattedText = "";					
				}
				formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, locale.toString());
				if(formattedText.endsWith("<br><p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-11);
				} else if(formattedText.endsWith("<p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-7);
				}
				letterVO.setBriefExplanation(formattedText);
				
				if(question.getDiscussionDate()!=null) {
					letterVO.setDiscussionDate(FormaterUtil.formatDateToString(question.getDiscussionDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString()));
				}

				if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER;
				}else if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT;
				}
				if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
					String questionsAsked = question.getQuestionsAskedInFactualPosition();
					if(questionsAsked==null) {
						if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						} else if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromMemberQuestions");
						}
					} else if(questionsAsked.isEmpty()) {
						if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						} else if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromMemberQuestions");
						}
					}
					if(questionsAsked!=null && !questionsAsked.isEmpty()) {
						List<MasterVO> questionsAskedForClarification = new ArrayList<MasterVO>();
						StringBuffer questionIndexesForClarification=new StringBuffer();	
						String allQuestions = "";
						if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							allQuestions = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						} else if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							allQuestions = session.getParameter(deviceType.getType().trim()+"_clarificationFromMemberQuestions");
						}								
						for(String questionAsked : questionsAsked.split("##")) {
							MasterVO questionAskedForClarification = new MasterVO();
							questionAskedForClarification.setValue(questionAsked);
							questionsAskedForClarification.add(questionAskedForClarification);
							int index = 1;
							for(String allQuestion : allQuestions.split("##")) {
								if(questionAsked.equals(allQuestion)) {
									questionIndexesForClarification.append("(");
									questionIndexesForClarification.append(FormaterUtil.formatNumberNoGrouping(index, question.getLocale()));
									questionIndexesForClarification.append("), ");											
									break;
								} else {
									index++;
								}
							}
						}
						if(!questionIndexesForClarification.toString().isEmpty()) {
							questionIndexesForClarification.deleteCharAt(questionIndexesForClarification.length()-1);
							questionIndexesForClarification.deleteCharAt(questionIndexesForClarification.length()-1);
						}		
						letterVO.setQuestionIndexesForClarification(questionIndexesForClarification.toString());
						letterVO.setQuestionsAskedForClarification(questionsAskedForClarification);
					}
				}
				String statusTypeSplit = statusType.split("_")[statusType.split("_").length-1];

				//				if(statusType.equals("admission")
				//						|| statusType.equals("rejection")) {
				//					WorkflowActor putupActor = WorkflowConfig.findFirstActor(question, status, locale.toString());
				//					if(putupActor!=null) {
				//						String putupActorUsergroupName = putupActor.getUserGroupType().getName();
				//						QuestionDraft putupDraft = Question.findPutupDraft(question.getId(), "question_recommend_"+statusType, putupActorUsergroupName);				
				//						if(putupDraft!=null) {
				//							letterVO.setRemarks(putupDraft.getRemarks());
				//						}
				//					}
				//				}		
				
				/**** In case username is required ****/
				Role role = Role.findByFieldName(Role.class, "type", "QIS_PRINCIPAL_SECRETARY", locale.toString());
				List<User> users = User.findByRole(false, role.getName(), locale.toString());
				//as principal secretary for starred question is only one, so user is obviously first element of the list.
				letterVO.setUserName(users.get(0).findFirstLastName());

				/**** generate report ****/				
				try {
					if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
						&& question.getDateOfAnsweringByMinister()!=null 
						&& question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_FINAL_ADMITTED)
						&& intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit+"_member", "WORD", "intimation_letter", locale.toString());
					}else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
							&& question.getDateOfAnsweringByMinister()!=null 
							&& question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_FINAL_ADMITTED)
							&& intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
							reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit+"_department", "WORD", "intimation_letter", locale.toString()); 
					}else if(intimationLetterFilter!=null && !intimationLetterFilter.isEmpty() && !intimationLetterFilter.equals("-")) {
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+intimationLetterFilter+"_"+statusTypeSplit, "WORD", "_intimation_letter", locale.toString());
					}else {
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit, "WORD", "intimation_letter", locale.toString());
					}					
					System.out.println("Intimation Letter generated successfully in WORD format!");

					openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
				} catch (Exception e) {
					e.printStackTrace();
					isError = true;
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "report.runtimeException.error", locale.toString());
				}				
			}			
		} else {
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.intimationLetter.noQuestionFound", locale.toString());
		}
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}	
	}	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/admissionreport", method=RequestMethod.GET)
	public String admittedAndToBeAdmittedReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "question/error";
		try{
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strHouseType = request.getParameter("houseType");
			String strDeviceType = request.getParameter("deviceType");
			String strGroupId = request.getParameter("groupId");
			String strSubDepartment = request.getParameter("subDepartment");
			
			if(strGroupId == null ){
				
			}
			
			if(strSessionType != null && !strSessionType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()
					&& strDeviceType != null && !strDeviceType.isEmpty()){
				
				SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, new Integer(strSessionYear));
				DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("groupId", new String[]{strGroupId});
				parameters.put("subDepartment", new String[]{strSubDepartment});
				parameters.put("locale", new String[]{locale.toString()});
				
				List report = Query.findReport("ADMISSION_REPORT", parameters);
				
				model.addAttribute("report", report);
				model.addAttribute("formater", new FormaterUtil());
				model.addAttribute("locale", locale.toString());
				model.addAttribute("showSubDepartment", strSubDepartment);
				page = "question/reports/admissionreport";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/halfhourdaysubmitreport", method=RequestMethod.GET)
	public String halfhourDayWiseSubmissionReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "question/error";
		try{
			String strGroupId = request.getParameter("groupId");
			String strSubDepartment = request.getParameter("subDepartment");
			String strDay = request.getParameter("days");
			
			if(strGroupId != null && !strGroupId.isEmpty()
					&& strSubDepartment != null && !strSubDepartment.isEmpty()
					&& strDay != null && !strDay.isEmpty()){
								
				List<Object> objects = QuestionReportHelper.getSesionAndDeviceType(request, locale.toString());
				
				Session session = null; 
				DeviceType deviceType = null;
				if(!objects.isEmpty()){
					session = (Session) objects.get(0);
					deviceType = (DeviceType)objects.get(1);
				}
				
				Integer startDay = new Integer(strDay);
				Integer endDay = new Integer((startDay.intValue()==0)? startDay.intValue():startDay.intValue()-1);
				
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("groupId", new String[]{strGroupId});
				parameters.put("startDay", new String[]{startDay.toString()});
				parameters.put("endDay", new String[]{endDay.toString()});
				parameters.put("subDepartment", new String[]{strSubDepartment});
				parameters.put("subDepartment", new String[]{strSubDepartment});
				parameters.put("locale", new String[]{locale.toString()});
				
				List report = Query.findReport("HALFHOUR_DAYWISE_SUBMISSION_REPORT", parameters);
				
				model.addAttribute("report", report);
				model.addAttribute("formater", new FormaterUtil());
				model.addAttribute("locale", locale.toString());
				
				page = "question/reports/halfhourdaywisesubmitreport";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/hdstatandadmissionreport", method=RequestMethod.GET)
	public String generateHDStatAndAdmissionReport(HttpServletRequest request, Model model, Locale locale){
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		Session session = null;
		DeviceType deviceType = null;
		List<Object> objects = QuestionReportHelper.getSesionAndDeviceType(request, locale.toString());
		if(!objects.isEmpty()){
			session = (Session) objects.get(0);
			deviceType = (DeviceType)objects.get(1);
		}
		parameters.put("locale", new String[]{locale.toString()});
		parameters.put("sessionId", new String[]{session.getId().toString()});
		parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		List headerStatReport = Query.findReport("HALFHOUR_STAT_REPORT", parameters);
		List admissionReport = Query.findReport("HALFHOUR_ADMISSION_REPORT", parameters);
		
		model.addAttribute("headerStats", headerStatReport);
		model.addAttribute("report", admissionReport);
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale.toString());
		CustomParameter csptShowHDAdmissionDetails = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_ADMISSION_DETAILS_" + session.getHouse().getType().getType().toUpperCase(), "");
		if(csptShowHDAdmissionDetails != null){
			model.addAttribute("showStats", csptShowHDAdmissionDetails.getValue());
		}
		return "question/reports/hd_statusreport";
	}	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/shortnoticeanswerdatereport", method=RequestMethod.GET)
	public String getShortNoticeAnswerDateReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "question/reports/error"; 
		try{
			String strWfid = request.getParameter("wfdId");
			String strQid = request.getParameter("qId");
			
			WorkflowDetails wfd = null;
			if(strWfid != null && !strWfid.isEmpty()){
				wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
				if(wfd != null){
					strQid = wfd.getDeviceId();
				}
			}
			
			if(strQid != null && !strQid.isEmpty()){
				Map<String, String[]> params = new HashMap<String, String[]>();
				params.put("locale", new String[]{locale.toString()});
				params.put("qId", new String[]{wfd.getDeviceId()});
				List report = Query.findReport("QUESTIONS_SHORTNOTICE_ANSWERING_REPORT", params);
				model.addAttribute("report", report);
			}
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("errorcode", "insufficient_parameters");
		}

		response.setContentType("text/html; charset=utf-8");
		return "question/reports/shortnoticeanswer";
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/generalreport", method=RequestMethod.GET)
	public String getReport(HttpServletRequest request, Model model, Locale locale){
		
		Map<String, String[]> requestMap = request.getParameterMap();
		List report = Query.findReport(request.getParameter("report"), requestMap);
		if(report != null && !report.isEmpty()){
			Object[] obj = (Object[])report.get(0);
			if(obj != null){
				
				model.addAttribute("topHeader", obj[0].toString().split(";"));
			}
		}
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale.toString());
		model.addAttribute("report", report);
		
		return "question/reports/"+request.getParameter("reportout");		
	}
	//----------------------------------------------------------------------
	@RequestMapping(value="/viewYaadi" ,method=RequestMethod.GET)
	public @ResponseBody void generateYaadiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 

		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");	    
		String strDeviceType=request.getParameter("questionType");
		String reportFormat=request.getParameter("outputFormat");
		if(strDeviceType == null){
			strDeviceType = request.getParameter("deviceType");
		}
		String strAnsweringDate = request.getParameter("answeringDate");

		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null && strAnsweringDate!=null && reportFormat!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty() && !strAnsweringDate.isEmpty() && !reportFormat.isEmpty()) {
				HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
				SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
				Integer sessionYear=Integer.parseInt(strSessionYear);
				Session session = null;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e1) {					
					e1.printStackTrace();
				}				
				DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				Date answeringDate = null;
				if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					QuestionDates questionDates = 
							QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
					answeringDate = questionDates.getAnsweringDate();
				}
				else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
						deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
					CustomParameter dbDateFormat = 
							CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
					CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				}
				Group group = null;
				if(deviceType.getDevice().equals("Question")) {
					try {
						group = Group.find(session, answeringDate, locale.toString());
					} catch (ELSException e) {					
						e.printStackTrace();
					}
				}				
				List<DeviceVO> ballotedDeviceVOs = null;
				try {
					ballotedDeviceVOs = Ballot.findBallotedQuestionVOs(session, deviceType, group, answeringDate, "mr_IN");
				} catch (ELSException e1) {
					e1.printStackTrace();
				}
				if(ballotedDeviceVOs == null) {
					try {
						//response.sendError(404, "Report cannot be generated at this stage.");
						MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "question.yadiReport.noDataFound", locale.toString());
						if(message != null) {
							if(!message.getValue().isEmpty()) {
								response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
							}
						} else {
							response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
						}

						return;
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}
				if(ballotedDeviceVOs.isEmpty()) {
					try {
						//response.sendError(404, "Report cannot be generated at this stage.");
						MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
						if(message != null) {
							if(!message.getValue().isEmpty()) {	            				
								response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
							}
						} else {
							response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
						}

						return;
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}
				QuestionYaadiSuchiXmlVO data = new QuestionYaadiSuchiXmlVO();
				data.setHouseType(houseType.getName());
				data.setSessionNumber(session.getNumber().toString());
				data.setSessionType(sessionType.getSessionType());
				data.setSessionYear(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
				data.setSessionPlace(session.getPlace().getPlace());
				Role role = Role.findByFieldName(Role.class, "type", "QIS_PRINCIPAL_SECRETARY", locale.toString());
				List<User> users = User.findByRole(false, role.getName(), locale.toString());
				//as principal secretary for starred question is only one, so user is obviously first element of the list.
				data.setUserName(users.get(0).findFirstLastName());

				List<MinistryVO> ministryVOs = new ArrayList<MinistryVO>();
				int count = 0;

				try {
					for(Ministry mi: Group.findMinistriesByPriority(group)) { //group.getMinistries()) {
						count++;
						String ministryNumber = FormaterUtil.formatNumberNoGrouping(count, locale.toString());
						MinistryVO ministryVO = new MinistryVO(mi.getId(), ministryNumber, mi.getName());
						ministryVOs.add(ministryVO);	            	
					}
				} catch (ELSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				data.setMinistryVOs(ministryVOs);
				SimpleDateFormat dbFormat = null;
				CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
				if(dbDateFormat!=null){
					dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
				}
				//Added the following code to solve the marathi month and day issue				
				String[] strAnsweringDates=dbFormat.format(answeringDate).split(",");
				String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
				data.setAnsweringDay(answeringDay);
				String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
				String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
				String formattedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString());
				data.setAnsweringDate(formattedAnsweringDate);

				String answeringDateInIndianCalendar = FormaterUtil.getIndianDate(answeringDate, locale);
				data.setAnsweringDateInIndianCalendar(answeringDateInIndianCalendar);

				data.setDeviceVOs(ballotedDeviceVOs);
				data.setTotalNumberOfDevices(FormaterUtil.formatNumberNoGrouping(ballotedDeviceVOs.size(), locale.toString()));
				//generate report
				try {
					reportFile = generateReportUsingFOP(data, "template_questionYaadi_report", reportFormat, "starred_question_yaadi", locale.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Question Yaadi Report generated successfully in " + reportFormat + " format!");

				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			} else{
				logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		} else{
			logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}
	}

	@RequestMapping(value="/viewSuchi" ,method=RequestMethod.GET)
	public @ResponseBody void generateSuchiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		try{
			File reportFile = null; 

			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");	    
			String strDeviceType=request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");
			}
			String strAnsweringDate = request.getParameter("answeringDate");
			String reportFormat=request.getParameter("outputFormat");

			if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null && strAnsweringDate!=null && reportFormat!=null){
				if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty() && !strAnsweringDate.isEmpty() && !reportFormat.isEmpty()) {
					HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
					SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
					Integer sessionYear=Integer.parseInt(strSessionYear);
					Session session = null;
					try {
						session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					} catch (ELSException e3) {
						e3.printStackTrace();
					}
					DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					Date answeringDate = null;
					if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						QuestionDates questionDates = 
								QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
						answeringDate = questionDates.getAnsweringDate();
					}
					else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
							deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
						CustomParameter dbDateFormat = 
								CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
						answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
						answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					}	
					Group group = null;
					if(deviceType.getDevice().equals("Question")) {
						try {
							group = Group.find(session, answeringDate, locale.toString());
						} catch (ELSException e1) {
							e1.printStackTrace();
						}
					}					
					List<RoundVO> roundVOs = null;
					try {
						roundVOs = Ballot.findBallotedRoundVOsForSuchi(session, deviceType, group, answeringDate, locale.toString());
					} catch (ELSException e2) {
						e2.printStackTrace();
					}				
					if(roundVOs == null) {
						try {
							//response.sendError(404, "Report cannot be generated at this stage.");
							MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
							if(message != null) {
								if(!message.getValue().isEmpty()) {
									response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
								} else {
									response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
								}
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Yaadi Report cannot be generated.</h3>");
							}

							return;
						} catch (IOException e) {						
							e.printStackTrace();
						}
					}
					if(roundVOs.isEmpty()) {
						try {
							//response.sendError(404, "Report cannot be generated at this stage.");
							MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
							if(message != null) {
								if(!message.getValue().isEmpty()) {	            				
									response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
								} else {
									response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
								}
							} else {
								response.getWriter().println("<h3>No Question is balloted yet.<br/>So Karyavali Report cannot be generated.</h3>");
							}

							return;
						} catch (IOException e) {						
							e.printStackTrace();
						}
					}
					QuestionYaadiSuchiXmlVO data = new QuestionYaadiSuchiXmlVO();
					data.setHouseType(houseType.getName());
					data.setSessionNumber(session.getNumber().toString());
					data.setSessionType(sessionType.getSessionType());
					data.setSessionYear(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
					data.setSessionPlace(session.getPlace().getPlace());
					Role role = Role.findByFieldName(Role.class, "type", "QIS_PRINCIPAL_SECRETARY", locale.toString());
					List<User> users = User.findByRole(false, role.getName(), locale.toString());
					//as principal secretary for starred question is only one, so user is obviously first element of the list.
					data.setUserName(users.get(0).findFirstLastName());

					List<MinistryVO> ministryVOs = new ArrayList<MinistryVO>();
					int count = 0;
					try {
						for(Ministry mi: Group.findMinistriesByPriority(group)) { //group.getMinistries()) {
							count++;
							String ministryNumber = FormaterUtil.formatNumberNoGrouping(count, locale.toString());
							MinistryVO ministryVO = new MinistryVO(mi.getId(), ministryNumber, mi.getName());
							ministryVOs.add(ministryVO);	            	
						}
					} catch (ELSException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					data.setMinistryVOs(ministryVOs);
					SimpleDateFormat dbFormat = null;
					CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
					if(dbDateFormat!=null){
						dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
					}
					//Added the following code to solve the marathi month and day issue
					String[] strAnsweringDates=dbFormat.format(answeringDate).split(",");
					String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
					data.setAnsweringDay(answeringDay);
					String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
					String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
					String formattedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString());
					data.setAnsweringDate(formattedAnsweringDate);
					String answeringDateInIndianCalendar = FormaterUtil.getIndianDate(answeringDate, locale);
					data.setAnsweringDateInIndianCalendar(answeringDateInIndianCalendar);
					int totalNumberOfDevices = 0;
					for(RoundVO r: roundVOs) {
						totalNumberOfDevices += r.getDeviceVOs().size();
					}
					data.setTotalNumberOfDevices(FormaterUtil.formatNumberNoGrouping(totalNumberOfDevices, locale.toString()));
					data.setRoundVOs(roundVOs);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DATE, -1);
					CustomParameter reportDateFormatParameter = CustomParameter.findByName(CustomParameter.class, "template_questionSuchi_report".toUpperCase() + "_REPORTDATE_FORMAT", "");
					if(reportDateFormatParameter!=null && reportDateFormatParameter.getValue()!=null) {
						String formattedReportDate = FormaterUtil.formatDateToString(calendar.getTime(), reportDateFormatParameter.getValue(), locale.toString());
						if(reportDateFormatParameter.getValue().equals("dd MMM, yyyy")) {
							String[] strDate=formattedReportDate.split(",");
							String[] strMonth=strDate[0].split(" ");
							String month=FormaterUtil.getMonthInMarathi(strMonth[1], locale.toString());
							formattedReportDate = strMonth[0] + " " + month + ", " + strDate[1];
						}
						data.setReportDate(formattedReportDate);
					} else {
						data.setReportDate(FormaterUtil.formatDateToString(calendar.getTime(), ApplicationConstants.REPORT_DATEFORMAT, locale.toString()));
					}					
					//generate report
					try {
						reportFile = generateReportUsingFOP(data, "template_questionSuchi_report", reportFormat, "starred_question_suchi", locale.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Question Suchi Report generated successfully in " + reportFormat + " format!");

					openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
				} else{
					logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values ****");
					try {
						response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values</h3>");
					} catch (IOException e) {
						e.printStackTrace();
					}				
				}
			} else{
				logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}catch (Exception e) {
			logger.debug("viewsuchi", e);
			e.printStackTrace();
			try {
				response.getWriter().println("<h3>Can not create Suchi</h3>");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value="/generateClubbedIntimationLetter/getClubbedQuestions", method=RequestMethod.GET)
	public String getClubbedQuestionsForIntimationReport(HttpServletRequest request, ModelMap model) {
		String retVal = "question/reports/error";
		String questionId = request.getParameter("questionId");
		if(questionId!=null && !questionId.isEmpty()) {
			Question question = Question.findById(Question.class, Long.parseLong(questionId));
			if(question!=null) {
				model.addAttribute("questionId", questionId);
				List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(question, ApplicationConstants.DESC);
				if(clubbedEntities!=null && !clubbedEntities.isEmpty()) {
					List<Reference> nameClubbedQuestionVOs = new ArrayList<Reference>();
					for(ClubbedEntity ce: clubbedEntities) {
						Question clubbedQuestion = ce.getQuestion();
						if(Question.isAdmittedThroughNameClubbing(clubbedQuestion)) {							
							Reference nameClubbedQuestionVO = new Reference();
							nameClubbedQuestionVO.setId(clubbedQuestion.getId().toString());
							nameClubbedQuestionVO.setNumber(FormaterUtil.formatNumberNoGrouping(clubbedQuestion.getNumber(), clubbedQuestion.getLocale()));
							nameClubbedQuestionVOs.add(nameClubbedQuestionVO);
						}
					}
					if(!nameClubbedQuestionVOs.isEmpty()) {
						model.addAttribute("nameClubbedQuestionVOs", nameClubbedQuestionVOs);
						retVal = "question/reports/getClubbedQuestions";
					} else {
						model.addAttribute("errorcode", "noNameClubbedEntitiesFound");
					}					
				} else {
					model.addAttribute("errorcode", "noClubbedEntitiesFound");
				}
			} else {
				model.addAttribute("errorcode", "invalidQuestion");
			}			
		}
		return retVal;
	}
	
	@RequestMapping(value="/generateClubbedIntimationLetter", method=RequestMethod.GET)
	public @ResponseBody void generateClubbedIntimationLetter(HttpServletRequest request, final HttpServletResponse response, final ModelMap model, final Locale locale) {
		File reportFile = null;		
		boolean isError = false;
		MessageResource errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.errorMessage", locale.toString());
		
		String strQuestionId = request.getParameter("questionId");
		String strClubbedQuestions = request.getParameter("clubbedQuestions");
		String outputFormat = request.getParameter("outputFormat");
		
		if(strQuestionId!=null && strClubbedQuestions!=null && outputFormat!=null){
			if((!strQuestionId.isEmpty()) && !strClubbedQuestions.isEmpty() && !outputFormat.isEmpty()){
				try {
					/**** Generate & Process Report Data ****/			
					Question question = Question.findById(Question.class, Long.parseLong(strQuestionId));
					if(question!=null) {
						/**** primary question data****/
						Map<String, String[]> queryParameters = new HashMap<String, String[]>();					
						queryParameters.put("questionId", new String[]{question.getId().toString()});
						queryParameters.put("locale", new String[]{question.getLocale()});
						@SuppressWarnings("unchecked")
						List<Object> primaryQuestionData = Query.findReport("QIS_CLUBBEDINTIMATIONLETTER_PARENTQUESTIONDATA", queryParameters);
						String houseType = question.getHouseType().getType();
						/**** find all member names for the question ****/					
						String allMemberNames = "";
						String memberNameFormat = "";
						CustomParameter memberNameFormatParameter = null;
						if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
							memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CLUBBEDINTIMATIONLETTER_MEMBERNAMEFORMAT_LOWERHOUSE", "");
							if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
								memberNameFormat = memberNameFormatParameter.getValue();
							} else {
								memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;							
							}
							allMemberNames = question.findAllMemberNamesWithConstituencies(memberNameFormat);
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
							memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CLUBBEDINTIMATIONLETTER_MEMBERNAMEFORMAT_UPPERHOUSE", "");
							if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
								memberNameFormat = memberNameFormatParameter.getValue();
							} else {
								memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
							}	
							allMemberNames = question.findAllMemberNames(memberNameFormat);
						}	
						/**** find last member name before first added question ****/
						String lastMemberNameBeforeAddedQuestions = "";
						StringBuffer previousQuestionsMemberNames = new StringBuffer();
						String[] strClubbedQuestionsArr = strClubbedQuestions.split(",");
						String firstAddedQuestionId = strClubbedQuestionsArr[strClubbedQuestionsArr.length-1];
						Question firstAddedQuestion = Question.findById(Question.class, Long.parseLong(firstAddedQuestionId));
						
						String firstAddedQuestionMemberName = firstAddedQuestion.getPrimaryMember().findNameInGivenFormat(memberNameFormat);
						if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
							String constituencyName = firstAddedQuestion.getPrimaryMember().findConstituencyNameForYadiReport(firstAddedQuestion.getSession().getHouse(), "DATE", new Date(), new Date());
							if(!constituencyName.isEmpty()) {
								firstAddedQuestionMemberName += " (" + constituencyName + ")";
							}
						}
						if(allMemberNames!=null && !allMemberNames.isEmpty()) {
							String[] allMemberNamesArr = allMemberNames.split(",");
							for(int i=0; i<allMemberNamesArr.length; i++) {
								if(allMemberNamesArr[i].trim().equals(firstAddedQuestionMemberName.trim())) {
									lastMemberNameBeforeAddedQuestions = allMemberNamesArr[i-1].trim();
									for(int j=0; j<i; j++) {
										if(j!=i-1) {
											previousQuestionsMemberNames.append(allMemberNamesArr[j].trim() + ", ");
										} else {
											previousQuestionsMemberNames.append(allMemberNamesArr[j].trim());
										}									
									}
									break;
								}
							}
							/**** clubbed questions data ****/							
							List<Object> clubbedQuestionData = new ArrayList<Object>();
							StringBuffer clubbedMemberNames = new StringBuffer();
							for(int k=strClubbedQuestionsArr.length-1; k>=0; k--) {
								Question clubbedQuestion = Question.findById(Question.class, Long.parseLong(strClubbedQuestionsArr[k]));
								queryParameters.put("questionId", new String[]{clubbedQuestion.getId().toString()});
								queryParameters.put("locale", new String[]{clubbedQuestion.getLocale()});
								clubbedQuestionData.add(Query.findReport("QIS_CLUBBEDINTIMATIONLETTER_CLUBBEDQUESTIONDATA", queryParameters));
								if(clubbedQuestionData!=null && !clubbedQuestionData.isEmpty()) {
									if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
										clubbedMemberNames.append(QuestionReportHelper.findMemberNamesForAddedQuestion(clubbedQuestion, previousQuestionsMemberNames.toString(), memberNameFormat, true));
									} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
										clubbedMemberNames.append(QuestionReportHelper.findMemberNamesForAddedQuestion(clubbedQuestion, previousQuestionsMemberNames.toString(), memberNameFormat, false));
									}		
									if(clubbedMemberNames!=null && !clubbedMemberNames.toString().isEmpty()) {
										if(k!=0) {
											previousQuestionsMemberNames.append(clubbedMemberNames + ", ");
										}								
									}														
								}
							}							
							
							/**** generate report ****/
							if(!isError) {
								reportFile = generateReportUsingFOP(new Object[]{primaryQuestionData, lastMemberNameBeforeAddedQuestions, clubbedQuestionData, clubbedMemberNames.toString()}, "question_clubbedIntimationLetter", outputFormat, "question_clubbedIntimationLetter", locale.toString());
								if(reportFile!=null) {
									System.out.println("Report generated successfully in " + outputFormat + " format!");
									openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
								}
							}
						} else {
							logger.error("**** No member found for the question ****");
							isError = true;
							errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.intimationLetter.noMemberFound", locale.toString());
						}
					} else {
						logger.error("**** Check request parameter 'questionId' for invalid value ****");
						isError = true;
					}
				} catch(Exception e) {
					logger.error("**** Some Runtime Exception Occurred ****");
					e.printStackTrace();
					isError = true;
				}								
			} else {
				logger.error("**** Check request parameter 'strRequestParameter,outputFormat' for empty values ****");
				isError = true;
			}
		} else{
			logger.error("**** Check request parameter 'strRequestParameter,outputFormat' for null values ****");
			isError = true;				
		}		
		/**** handle errors if any ****/
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/memberwisequestions",method=RequestMethod.GET)
	public String viewMemberWiseReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			List<Reference> eligibleMembers = new ArrayList<Reference>();
			
			String category = request.getParameter("category");
			String strQuestionType = request.getParameter("questionType");
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strGroup = request.getParameter("group");
			String strAnsweringDate = request.getParameter("answeringDate");
			
			if(category!=null&&strQuestionType!=null&&strHouseType!=null && strSessionType!=null && strSessionYear!=null){
				if((!category.isEmpty())&&(!strQuestionType.isEmpty())&&!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()){										
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					if(questionType==null) {
						logger.error("**** DeviceType Not Found ****");
						model.addAttribute("type", "QUESTIONTYPE_NOTFOUND");
						return errorpage;
					}
					model.addAttribute("questionType",questionType.getId());
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}					
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					if(houseType==null || sessionType==null) {
						logger.error("**** HouseType or SessionType Not Found ****");
						model.addAttribute("type", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");
						return errorpage;
					}
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					if(session==null) {								
						logger.error("**** Session Not Found ****");
						model.addAttribute("type", "SESSION_NOTFOUND");
						return errorpage;
					}
					model.addAttribute("session",session.getId());			
					/**** find all members from given house which can submit questions ****/
					Map<String, String[]> queryParameters = new HashMap<String, String[]>();
					queryParameters.put("houseId", new String[]{session.getHouse().getId().toString()});
					queryParameters.put("currentDate", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
					queryParameters.put("locale", new String[]{locale.toString()});
					List resultList = Query.findReport("MEMBERS_ELIGIBLE_FOR_QUESTION_SUBMISSION_IN_GIVEN_HOUSE", queryParameters);
					if(resultList!=null && !resultList.isEmpty()) {
						for(Object o: resultList) {								
							Object[] result = (Object[])o;
							Reference member = new Reference();
							if(result[0]!=null) {
								member.setId(result[0].toString());
							}
							if(result[1]!=null) {
								member.setName(result[1].toString());
							}
							eligibleMembers.add(member);								
						}							
						model.addAttribute("eligibleMembers", eligibleMembers);
					} else {
						//error
					}
				}else{
					logger.error("**** Check request parameter 'session,questionType' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "question/reports/memberwise_questions";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/memberwisequestions/questions",method=RequestMethod.GET)
	public String viewMemberWiseQuestionsReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strMember = request.getParameter("member");
			String strQuestionType = request.getParameter("questionType");
			String strSession = request.getParameter("session");			
			
			if(strMember!=null && strQuestionType!=null && strSession!=null){
				if(!strMember.isEmpty() && !strQuestionType.isEmpty()&& !strSession.isEmpty()){					
					/**** find all questions of members submitted in given session (questions from chart) ****/
					Map<String, String[]> queryParameters = new HashMap<String, String[]>();
					queryParameters.put("sessionId", new String[]{strSession});
					queryParameters.put("memberId", new String[]{strMember});
					queryParameters.put("questionTypeId", new String[]{strQuestionType});
					queryParameters.put("locale", new String[]{locale.toString()});
					List resultList = Query.findReport("QIS_MEMBERWISE_QUESTIONS", queryParameters);
					if(resultList!=null && !resultList.isEmpty()) {													
						model.addAttribute("memberwiseQuestions", resultList);
						model.addAttribute("formatter", new FormaterUtil());
						model.addAttribute("locale", locale.toString());
					} else {
						//error
					}
				}else{
					logger.error("**** Check request parameter 'member,session,questionType' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'member,session,questionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "question/reports/memberwise_questions_data";
	}
	
	@RequestMapping(value="/bulleteinreport" ,method=RequestMethod.GET)
	public @ResponseBody void generateBulleteinReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strGroup = request.getParameter("group");
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()){	
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}					
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					if(houseType==null || sessionType==null) {
						logger.error("**** HouseType or SessionType Not Found ****");
						model.addAttribute("type", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");					
					} else {
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("type", "SESSION_NOTFOUND");					
						} else {
							Session previousSession = Session.findPreviousSession(session);
							/**** find report data ****/
							Map<String, String[]> queryParameters = new HashMap<String, String[]>();
							queryParameters.put("houseTypeId", new String[]{houseType.getId().toString()});
							queryParameters.put("sessionId", new String[]{session.getId().toString()});
							if(previousSession!=null) {
								queryParameters.put("previousSessionId", new String[]{previousSession.getId().toString()});						
							} else {
								queryParameters.put("previousSessionId", new String[]{"-1"});
							}
							queryParameters.put("locale", new String[]{locale.toString()});
							String isAhwalBulletein = request.getParameter("isAhwalBulletein");
							if(isAhwalBulletein!=null) {
								queryParameters.put("isAhwalBulletein", new String[]{isAhwalBulletein});
							} else {
								queryParameters.put("isAhwalBulletein", new String[]{""});
							}
							String queryName = "QIS_BULLETEIN_REPORT";
							Group group = new Group();
							if(strGroup!=null && !strGroup.isEmpty()) {
								group = Group.findById(Group.class, Long.parseLong(strGroup));
							}
							if(group==null) {								
								logger.error("**** Group Not Found ****");
								model.addAttribute("type", "GROUP_NOTFOUND");					
							} else {
								if(group.getId()!=null) {
									queryParameters.put("groupId", new String[]{group.getId().toString()});
									queryName = "QIS_BULLETEIN_GROUPWISE_REPORT";
								}								
								List reportData = Query.findReport(queryName, queryParameters);
								if(reportData!=null && !reportData.isEmpty()) {
									/**** generate report ****/
									if(!isError) {										
										if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
											if(isAhwalBulletein!=null && isAhwalBulletein.equals("yes")) {
												reportFile = generateReportUsingFOP(new Object[]{reportData}, "qis_ahwal_bulletein_report_lowerhouse", "WORD", "qis_ahwal_bulletein_report", locale.toString());
											} else {
												reportFile = generateReportUsingFOP(new Object[]{reportData}, "qis_bulletein_report_lowerhouse", "WORD", "qis_bulletein_report", locale.toString());
											}											
										} else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
											if(isAhwalBulletein!=null && isAhwalBulletein.equals("yes")) {
												reportFile = generateReportUsingFOP(new Object[]{reportData}, "qis_ahwal_bulletein_report_upperhouse", "WORD", "qis_ahwal_bulletein_report", locale.toString());
											} else {
												reportFile = generateReportUsingFOP(new Object[]{reportData}, "qis_bulletein_report_upperhouse", "WORD", "qis_bulletein_report", locale.toString());
											}
										}							
										if(reportFile!=null) {
											System.out.println("Report generated successfully in WORD format!");
											openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
										}
									}
								} else {
									//error
								}
							}							
						}						
					}					
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}
			}else{
				isError = true;
				logger.error("**** Check request parameters houseType, sessionType, sessionYear for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.bulleteinReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters houseType, sessionType, sessionYear for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.bulleteinReport.reqparam.null", locale.toString());
		}
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value="/starredDepartmentwiseStatsReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateStarredDepartmentwiseStatsReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");		
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()){	
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}					
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					if(houseType==null || sessionType==null) {
						logger.error("**** HouseType or SessionType Not Found ****");
						model.addAttribute("type", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");					
					} else {
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("type", "SESSION_NOTFOUND");					
						} else {
							/**** find report data ****/
							Map<String, String[]> queryParameters = new HashMap<String, String[]>();
							queryParameters.put("houseType", new String[]{houseType.getType()});
							queryParameters.put("sessionId", new String[]{session.getId().toString()});
							DeviceType starredQuestionType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale.toString());
							if(starredQuestionType==null) {
								logger.error("**** Starred Question Type Not Found ****");
							}
							queryParameters.put("starredQuestionTypeId", new String[]{starredQuestionType.getId().toString()});
							DeviceType unstarredQuestionType = DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, locale.toString());
							if(unstarredQuestionType==null) {
								logger.error("**** Un-Starred Question Type Not Found ****");
							}
							queryParameters.put("unstarredQuestionTypeId", new String[]{unstarredQuestionType.getId().toString()});
							queryParameters.put("locale", new String[]{locale.toString()});
							
							String queryName = "QIS_STARRED_DEPARTMENTWISE_STATS_"+houseType.getType().toUpperCase()+"_REPORT";														
							List reportData = Query.findReport(queryName, queryParameters);
							if(reportData!=null && !reportData.isEmpty()) {
								List<String> serialNumbers = new ArrayList<String>();
								for(int i=1; i<=reportData.size(); i++) {
									serialNumbers.add(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
								}
								List<String> localisedContent = new ArrayList<String>();
								if(reportData.get(0)!=null) {
									Object[] obj = (Object[]) reportData.get(0);
									for(String i: obj[0].toString().split("~#")) {
										localisedContent.add(i);
									}									
								}
								/**** generate report ****/
								if(!isError) {										
									if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
										reportFile = generateReportUsingFOP(new Object[]{reportData,serialNumbers,localisedContent}, "qis_starredDepartmentwiseStatsReport_lowerhouse", "WORD", "qis_starredDepartmentwiseStats_report", locale.toString());											
									} else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
										reportFile = generateReportUsingFOP(new Object[]{reportData,serialNumbers,localisedContent}, "qis_starredDepartmentwiseStatsReport_upperhouse", "WORD", "qis_starredDepartmentwiseStats_report", locale.toString());
									}							
									if(reportFile!=null) {
										System.out.println("Report generated successfully in WORD format!");
										openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
									}
								}
							} else {
								//error
							}												
						}						
					}					
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}
			}else{
				isError = true;
				logger.error("**** Check request parameters houseType, sessionType, sessionYear for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.bulleteinReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters houseType, sessionType, sessionYear for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.bulleteinReport.reqparam.null", locale.toString());
		}
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/departmentwisequestions", method=RequestMethod.GET)
	public String getDepartmentwiseAdmittedQuestionReport(HttpServletRequest request, Model model, Locale locale){
		
		Map<String, String[]> requestMap = request.getParameterMap();
		List report = Query.findReport(request.getParameter("report"), requestMap);
		if(report != null && !report.isEmpty()){
			String[] clubbedNumbersArr = new String[report.size()];
			int count=0;
			for(Object o: report) {
				Object[] obj = (Object[])o;
				if(obj!=null) {
					if(count==0) {
						model.addAttribute("localisedContent", obj[0].toString().split("~#"));
					}
					if(obj[7]!=null) {
						String[] clubbedQuestionNumbers = obj[7].toString().split(",");
						StringBuffer clubbedNumbers = new StringBuffer();
						clubbedNumbers.append("(");
						for(int i=0; i<clubbedQuestionNumbers.length; i++) {
							if(i!=0 && i%3==0) {
								clubbedNumbers.append("<br/>");
							} 
							clubbedNumbers.append(clubbedQuestionNumbers[i]);
							if(i!=clubbedQuestionNumbers.length-1) {
								clubbedNumbers.append(",");
							}							
						}
						clubbedNumbers.append(")");						
						clubbedNumbersArr[count]=clubbedNumbers.toString();						
					} else {
						clubbedNumbersArr[count]="";
					}
					System.out.println(clubbedNumbersArr[count]);
				}
				count++;
			}
			model.addAttribute("clubbedNumbers", clubbedNumbersArr);
		}
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale.toString());
		model.addAttribute("report", report);
		model.addAttribute("selectedHouseType", request.getParameter("houseType"));
		model.addAttribute("selectedSessionType", request.getParameter("sessionType"));
		model.addAttribute("selectedSessionYear", request.getParameter("sessionYear"));
		model.addAttribute("selectedSubDepartment", request.getParameter("subDepartment"));
		
		return "question/reports/"+request.getParameter("reportout");		
	}//showTabByIdAndUrl('details_tab', 'question/report/sankshiptAhwal');
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/sankshiptAhwal", method=RequestMethod.GET)
	public String getSankshiptAhwalInit(HttpServletRequest request, Model model, Locale locale){		
		return "question/reports/sankshiptahwalinit";	
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/departmentwisequestions/export", method=RequestMethod.GET)
	public void exportDepartmentwiseAdmittedQuestionReport(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		Map<String, String[]> requestMap = request.getParameterMap();
		List report = Query.findReport(request.getParameter("report"), requestMap);		
		if(report != null && !report.isEmpty()){
			List<String> localisedContent = new ArrayList<String>();
			List<String> clubbedNumbersList = new ArrayList<String>();
			int count=0;
			for(Object o: report) {
				Object[] obj = (Object[])o;
				if(obj!=null) {
					if(count==0) {
						for(String i: obj[0].toString().split("~#")) {
							localisedContent.add(i);
						}											
					}
					if(obj[7]!=null) {
						String[] clubbedQuestionNumbers = obj[7].toString().split(",");
						StringBuffer clubbedNumbers = new StringBuffer();
						clubbedNumbers.append("(");
						for(int i=0; i<clubbedQuestionNumbers.length; i++) {
							if(i!=0 && i%3==0) {
								clubbedNumbers.append("<br>");
							} 
							clubbedNumbers.append(clubbedQuestionNumbers[i]);
							if(i!=clubbedQuestionNumbers.length-1) {
								clubbedNumbers.append(",");
							}							
						}
						clubbedNumbers.append(")");					
						clubbedNumbersList.add(clubbedNumbers.toString());										
					} else {
						clubbedNumbersList.add("");	
					}					
				}
				count++;
			}
			model.addAttribute("clubbedNumbers", clubbedNumbersList);
			/**** generate report ****/
			if(!isError) {
				try {
					reportFile = generateReportUsingFOP(new Object[]{report,localisedContent, clubbedNumbersList}, "qis_departmentwise_questions", request.getParameter("outputFormat"), "qis_departmentwisequestions", locale.toString());
				} catch (Exception e) {					
					e.printStackTrace();
				}							
				if(reportFile!=null) {
					System.out.println("Report generated successfully in WORD format!");
					openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
				}
			}
		}			
	}
}

/**** Helper for producing reports ****/
class QuestionReportHelper{
	
	private static Logger logger = LoggerFactory.getLogger(QuestionReportHelper.class);
	
	@SuppressWarnings("rawtypes")
	public static String getCurrentStatusReportData(final Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "question/error";
		try{
			String strDevice = request.getParameter("device"); 
			if(strDevice != null && !strDevice.isEmpty()){
				Question qt = Question.findById(Question.class, id);
				List report = generatetCurrentStatusReport(qt, strDevice, locale.toString());				
				Map<String, Object[]> dataMap = new HashMap<String, Object[]>();
				
				if(report != null && !report.isEmpty()){
										
					List<User> users = User.findByRole(false, "QIS_PRINCIPAL_SECRETARY", locale.toString());
					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
	
					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)? "QIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "QIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
					if(csptAllwedUserGroupForStatusReportSign != null){
						if(csptAllwedUserGroupForStatusReportSign.getValue() != null && !csptAllwedUserGroupForStatusReportSign.getValue().isEmpty()){
						
							for(Object o : report){
								Object[] objx = (Object[])o;
	
								if(objx[27] != null && !objx[27].toString().isEmpty()){
									if(csptAllwedUserGroupForStatusReportSign.getValue().contains(objx[27].toString())){
										
										UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", objx[27].toString(), locale.toString());
																				
										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
											if(dataMap.get(ApplicationConstants.UNDER_SECRETARY) != null){
												if(objx != null){
													if(objx[6] != null && objx[6].toString().length() > 0){
														dataMap.put(ApplicationConstants.UNDER_SECRETARY, objx);
													}else{
														Object[] tempObj = dataMap.get(ApplicationConstants.UNDER_SECRETARY);
														tempObj[28] = objx[28];
														
														dataMap.put(ApplicationConstants.UNDER_SECRETARY, tempObj);
													}
												}
											}else{
												dataMap.put(ApplicationConstants.UNDER_SECRETARY, objx);
											}
										}else{
											if(dataMap.get(userGroupType.getType()) != null){
												if(objx != null){
													if(objx[6] != null && objx[6].toString().length() > 0){
														dataMap.put(userGroupType.getType(), objx);
													}else{
														Object[] tempObj = dataMap.get(userGroupType.getType());
														tempObj[28] = objx[28];
														
														dataMap.put(userGroupType.getType(), tempObj);
													}
												}
												
											}else{
												dataMap.put(userGroupType.getType(), objx);
											}
										}
									}
								}
							}
							
							
							
							if(dataMap.get(ApplicationConstants.PRINCIPAL_SECRETARY) == null){
								UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.PRINCIPAL_SECRETARY, locale.toString());
								
								Object[] dataCollection = new Object[30];
								dataCollection[0] = new String(userGroupType.getName());
								dataCollection[1] = new String("");
								dataCollection[3] = new String("");
								dataCollection[6] = new String("");
								dataCollection[28] = new String("");
								
								dataMap.put(ApplicationConstants.PRINCIPAL_SECRETARY, dataCollection);
							}
							
							if(dataMap.isEmpty()){
								for(String val : csptAllwedUserGroupForStatusReportSign.getValue().split(",")){
								
									Reference ref = UserGroup.findQuestionActor(qt, val, String.valueOf(0), locale.toString());
									Object[] actor = new Object[30];
									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
										actor[0] = new String(ref.getId().split("#")[3]);
										actor[1] = new String(ref.getId().split("#")[4]);
									}else{
										actor[0] = new String(ref.getId().split("#")[3]);
										actor[1] = new String("");
									}
									actor[3] = new String("");
									actor[6] = new String("");
									actor[28] = new String("");
									
									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
										dataMap.put(ApplicationConstants.UNDER_SECRETARY, actor);
									}else{
										dataMap.put(val, actor);
									}
								}
							}
	
							model.addAttribute("data", dataMap);
							model.addAttribute("formatData", report.get(report.size()-1));
						}
					}
	
					page = (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "question/reports/statusreportlowerhouse": "question/reports/statusreportupperhouse";
				}		
	
				model.addAttribute("qid", qt.getId());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	public static List generatetCurrentStatusReport(final Question question, final String device, final String locale){
		CustomParameter memberNameFormatParameter = null;
		if(question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
		} else if(question.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_UPPERHOUSE", "");
		}
		String support = "";
		if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
			support = question.findAllMemberNames(memberNameFormatParameter.getValue());
		} else {
			support = question.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
		}		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("id",new String[]{question.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("QIS_CURRENTSTATUS_REPORT", parameters);
		for(Object o : list){
			Object[] data = (Object[]) o;
			String details = ((data[4] != null)? data[4].toString():"-");
			String subject = ((data[5] != null)? data[5].toString():"-");
			String remarks = ((data[6] != null)? data[6].toString():"-");
			
			((Object[])o)[17] = support;
			((Object[])o)[4] = FormaterUtil.formatNumbersInGivenText(details, locale);
			((Object[])o)[5] = FormaterUtil.formatNumbersInGivenText(subject, locale);
			((Object[])o)[6] = FormaterUtil.formatNumbersInGivenText(remarks, locale);
			data = null;
		}

		return list;  
	}
	
	public static List<Object> getSesionAndDeviceType(HttpServletRequest request, String locale){
		List<Object> objects = new ArrayList<Object>();
		try{
		
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strHouseType = request.getParameter("houseType");
			String strDeviceType = request.getParameter("deviceType");
			
			if(strSessionType != null && !strSessionType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()
					&& strDeviceType != null && !strDeviceType.isEmpty()){
				
				SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, new Integer(strSessionYear));
				DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				objects.add(session);
				objects.add(deviceType);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return objects;
	}

	public static String findMemberNamesForAddedQuestion(Question clubbedQuestion, String previousQuestionsMemberNames, String memberNameFormat, boolean isConstituencyIncluded) {
		Session session = clubbedQuestion.getSession();
		House questionHouse = session.getHouse();
		Date currentDate = new Date();
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Member member = null;
		String memberName = "";
		String constituencyName = "";
		
		/** primary member **/
		member = clubbedQuestion.getPrimaryMember();		
		if(member==null) {
			return allMemberNamesBuffer.toString();
		}		
		memberName = member.findNameInGivenFormat(memberNameFormat);
		if(memberName!=null && !memberName.isEmpty() && !previousQuestionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
			allMemberNamesBuffer.append(memberName);
			if(isConstituencyIncluded) {
				constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
				if(!constituencyName.isEmpty()) {
					allMemberNamesBuffer.append(" (" + constituencyName + ")");			
				}
			}			
		} else {
			return allMemberNamesBuffer.toString();
		}				
		
		/** supporting members **/
		List<SupportingMember> supportingMembers = clubbedQuestion.getSupportingMembers();
		if (supportingMembers != null) {
			for (SupportingMember sm : supportingMembers) {
				member = sm.getMember();
				if(member!=null) {
					memberName = member.findNameInGivenFormat(memberNameFormat);
					if(memberName!=null && !memberName.isEmpty() && !previousQuestionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
						if(member.isSupportingOrClubbedMemberToBeAddedForDevice(clubbedQuestion)) {
							allMemberNamesBuffer.append(", " + memberName);
							if(isConstituencyIncluded) {
								constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
								if(!constituencyName.isEmpty()) {
									allMemberNamesBuffer.append(" (" + constituencyName + ")");						
								}
							}
						}						
					}									
				}				
			}
		}
		
		/** clubbed questions members **/
		List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(clubbedQuestion);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
						|| ce.getQuestion().getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
					member = ce.getQuestion().getPrimaryMember();
					if(member!=null) {
						memberName = member.findNameInGivenFormat(memberNameFormat);
						if(memberName!=null && !memberName.isEmpty() && !previousQuestionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(member.isSupportingOrClubbedMemberToBeAddedForDevice(clubbedQuestion)) {
								allMemberNamesBuffer.append(", " + memberName);
								if(isConstituencyIncluded) {
									constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
									if(!constituencyName.isEmpty()) {
										allMemberNamesBuffer.append(" (" + constituencyName + ")");							
									}
								}
							}							
						}												
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getQuestion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							if(member!=null) {
								memberName = member.findNameInGivenFormat(memberNameFormat);
								if(memberName!=null && !memberName.isEmpty() && !previousQuestionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
									if(member.isSupportingOrClubbedMemberToBeAddedForDevice(clubbedQuestion)) {
										allMemberNamesBuffer.append(", " + memberName);
										if(isConstituencyIncluded) {
											constituencyName = member.findConstituencyNameForYadiReport(questionHouse, "DATE", currentDate, currentDate);
											if(!constituencyName.isEmpty()) {
												allMemberNamesBuffer.append(" (" + constituencyName + ")");							
											}
										}
									}									
								}								
							}
						}
					}
				}
			}
		}		
		return allMemberNamesBuffer.toString();		
	}
}