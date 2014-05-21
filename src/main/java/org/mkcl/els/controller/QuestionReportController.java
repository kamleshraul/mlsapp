package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
				if(question.getRevisedQuestionText()!=null && !question.getRevisedQuestionText().isEmpty()) {
					formattedText = question.getRevisedQuestionText();					
				} else {
					formattedText = question.getQuestionText();
				}
				formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, question.getLocale());
				letterVO.setQuestionText(formattedText);		
				Member primaryMember = question.getPrimaryMember();
				if(primaryMember!=null) {
					String primaryMemberName = primaryMember.findFirstLastName();
					if(primaryMemberName!=null) {
						letterVO.setPrimaryMemberName(primaryMemberName);
					} else {
						letterVO.setPrimaryMemberName("");
					}
				} else {
					//error code: No Primary Member Found.
				}
				String allMemberNames = null;
				CustomParameter memberNameFormatParameter = null;
				if(question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_LOWERHOUSE", "");
					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
						allMemberNames = question.findAllMemberNamesWithConstituencies(memberNameFormatParameter.getValue());
					} else {
						allMemberNames = question.findAllMemberNamesWithConstituencies(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
					}
				} else if(question.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_UPPERHOUSE", "");
					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
						allMemberNames = question.findAllMemberNames(memberNameFormatParameter.getValue());
					} else {
						allMemberNames = question.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
					}					
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
				} else {
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
				if(statusType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
					formattedText = FormaterUtil.formatNumbersInGivenText(question.getRejectionReason(), question.getLocale());
					letterVO.setRejectionReason(formattedText);					
				} else {//if(statusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
					formattedText = FormaterUtil.formatNumbersInGivenText(question.getQuestionreferenceText(), question.getLocale());
				}

				String memberOrDepartment=request.getParameter("memberOrDepartment");
				if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && memberOrDepartment!=null && memberOrDepartment.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && memberOrDepartment!=null && memberOrDepartment.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER;
				}else if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && memberOrDepartment!=null && memberOrDepartment.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && memberOrDepartment!=null && memberOrDepartment.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT;
				}
				if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
					String questionsAsked = question.getQuestionsAskedInFactualPosition();
					if(questionsAsked==null) {
						if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							questionsAsked = question.getSession().getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						} else if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							questionsAsked = question.getSession().getParameter(deviceType.getType().trim()+"_clarificationFromMemberQuestions");
						}
					} else if(!questionsAsked.isEmpty()) {
						if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							questionsAsked = question.getSession().getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						} else if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							questionsAsked = question.getSession().getParameter(deviceType.getType().trim()+"_clarificationFromMemberQuestions");
						}
					}
					if(questionsAsked!=null && !questionsAsked.isEmpty()) {
						List<MasterVO> questionsAskedForClarification = new ArrayList<MasterVO>();
						StringBuffer questionIndexesForClarification=new StringBuffer();	
						String allQuestions = "";
						if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							allQuestions = question.getSession().getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						} else if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							allQuestions = question.getSession().getParameter(deviceType.getType().trim()+"_clarificationFromMemberQuestions");
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
				//				Role role = Role.findByFieldName(Role.class, "type", "QIS_PRINCIPAL_SECRETARY", locale.toString());
				//				List<User> users = User.findByRole(false, role.getName(), locale.toString());
				//				//as principal secretary for starred question is only one, so user is obviously first element of the list.
				//				letterVO.setUserName(users.get(0).findFirstLastName());

				/**** generate report ****/				
				try {
					if(status.getType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
						reportFile = generateReportUsingFOP(letterVO, "question_intimationletter_"+statusTypeSplit, "WORD", "intimation_letter", locale.toString());
					} else {
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
			
			if(strSessionType != null && !strSessionType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()
					&& strDeviceType != null && !strDeviceType.isEmpty()
					&& strGroupId != null && !strGroupId.isEmpty()
					&& strSubDepartment != null && !strSubDepartment.isEmpty()){
				
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
				String formattedAnsweringDate = strAnsweringMonth[0] + " " + answeringMonth + " " + strAnsweringDates[2];
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
					String formattedAnsweringDate = strAnsweringMonth[0] + " " + answeringMonth + " " + strAnsweringDates[2];
					data.setAnsweringDate(formattedAnsweringDate);
					String answeringDateInIndianCalendar = FormaterUtil.getIndianDate(answeringDate, locale);
					data.setAnsweringDateInIndianCalendar(answeringDateInIndianCalendar);
					int totalNumberOfDevices = 0;
					for(RoundVO r: roundVOs) {
						totalNumberOfDevices += r.getDeviceVOs().size();
					}
					data.setTotalNumberOfDevices(FormaterUtil.formatNumberNoGrouping(totalNumberOfDevices, locale.toString()));
					data.setRoundVOs(roundVOs);

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
		Boolean isError = false;
		MessageResource errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.errorMessage", locale.toString());
		
		String strQuestionId = request.getParameter("questionId");
		String strClubbedQuestions = request.getParameter("clubbedQuestions");
		String outputFormat = request.getParameter("outputFormat");
		
		if(strQuestionId!=null&&outputFormat!=null){
			if((!strQuestionId.isEmpty())&&!outputFormat.isEmpty()){
				try {
					/**** Generate & Process Report Data ****/
					List<Object> reportFields = new ArrayList<Object>(); //replace this with specific list or data for this report
					
					/**** generate report ****/
					reportFile = generateReportUsingFOP(new Object[]{reportFields}, "xslt_filename", outputFormat, "report_filename", locale.toString());
					if(reportFile!=null) {
						System.out.println("Report generated successfully in " + outputFormat + " format!");
						openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
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
}