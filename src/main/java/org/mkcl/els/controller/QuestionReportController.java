package org.mkcl.els.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
import org.mkcl.els.common.vo.SessionVO;
import org.mkcl.els.common.xmlvo.QuestionIntimationLetterXmlVO;
import org.mkcl.els.common.xmlvo.QuestionYaadiSuchiXmlVO;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.YaadiDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
				Status status = question.getInternalStatus();	
				String statusType=status.getType();
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
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, question.getLocale());
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
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, question.getLocale());
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
					letterVO.setSubDepartment(subDepartment.getName().trim());
				}
				Department department = subDepartment.getDepartment();
				if(department!=null) {
					letterVO.setDepartment(department.getName().trim());
				}
				letterVO.setIsSubDepartmentNameSameAsDepartmentName(false);
				if(letterVO.getDepartment()!=null && letterVO.getSubDepartment()!=null) {
					MessageResource departmentLabel = MessageResource.findByFieldName(MessageResource.class, "code", "generic.department", locale.toString());
					if(departmentLabel!=null) {
						String dept = letterVO.getDepartment();
						if(letterVO.getDepartment().endsWith(departmentLabel.getValue())) {
							dept = letterVO.getDepartment().split(departmentLabel.getValue())[0].trim();
						}	
						String subDept = letterVO.getSubDepartment();
						if(letterVO.getSubDepartment().endsWith(departmentLabel.getValue())) {
							subDept = letterVO.getSubDepartment().split(departmentLabel.getValue())[0].trim();
						}
						if(dept.equals(subDept)) {
							letterVO.setIsSubDepartmentNameSameAsDepartmentName(true);
						}
					}					
				}
				/** previous related details of question **/
				QuestionDraft questionDraftBeforeChange = null;
				if(question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)
						|| 
					(intimationLetterFilter!=null 
						&& (intimationLetterFilter.equals("groupchanged")
							|| intimationLetterFilter.equals("groupChangedAfterBallot"))
					)
						) {
					questionDraftBeforeChange = question.findLatestPreviousGroupDraft();				
				} else {
					questionDraftBeforeChange = question.findSecondPreviousDraft();
				}
				if(questionDraftBeforeChange!=null) {
					Ministry previousMinistry = questionDraftBeforeChange.getMinistry();
					if(previousMinistry!=null) {
						try {
							MemberMinister memberMinister = Question.findMemberMinisterIfExists(question, previousMinistry);
							if(memberMinister!=null) {
								letterVO.setPreviousMinistryDesignation(memberMinister.getDesignation().getName());
							} else {
								letterVO.setPreviousMinistryDesignation("");
							}
						} catch(ELSException ex) {
							letterVO.setPreviousMinistryDesignation("");
						}						
					}
					SubDepartment previousSubDepartment = questionDraftBeforeChange.getSubDepartment();
					if(previousSubDepartment!=null) {
						letterVO.setPreviousSubDepartment(previousSubDepartment.getName());
						Department previousDepartment = previousSubDepartment.getDepartment();
						if(previousDepartment!=null) {
							letterVO.setPreviousDepartment(previousDepartment.getName());
						}
					}																
				} else {
					isError = true;
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.intimationLetter.noQuestionFound", locale.toString());
				}
				/** answering date for starred question **/
				if(deviceType.getType().trim().equals(ApplicationConstants.STARRED_QUESTION)) {
					QuestionDates questionDates = Question.findQuestionDatesForStarredQuestion(question);
					if(questionDates!=null) {
						Date answeringDate = questionDates.getAnsweringDate();
						if(answeringDate!=null) {
							if(statusType.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED) 
									|| (intimationLetterFilter!=null && intimationLetterFilter.equals("groupChangedAfterBallot"))) {
								
								String formattedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, "dd MMM,yyyy", question.getLocale());
								formattedAnsweringDate = FormaterUtil.formatMonthInMarathiDate(formattedAnsweringDate, locale.toString());
								letterVO.setAnsweringDate(formattedAnsweringDate);
								
							} else if(intimationLetterFilter!=null && intimationLetterFilter.equals("answeringDateForwarded")) {
								SimpleDateFormat dbFormat = null;
								CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
								if(dbDateFormat!=null){
									dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
								}
								//Added the following code to solve the marathi month and day issue
								String[] strAnsweringDates=dbFormat.format(answeringDate).split(",");
								String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
								String dateLabel = "";
								MessageResource dateLabelMessageResource = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
								if(dateLabelMessageResource!=null && dateLabelMessageResource.getValue()!=null) {
									dateLabel = dateLabelMessageResource.getValue();
								}
								String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
								String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
								String formattedAnsweringDate = answeringDay+", "+dateLabel+" "+strAnsweringMonth[0] + " " + answeringMonth + ", " + strAnsweringDates[2];
								letterVO.setAnsweringDate(formattedAnsweringDate);
							} else {
								letterVO.setAnsweringDate(FormaterUtil.formatDateToString(answeringDate, "dd-MM-yyyy", question.getLocale()));
							}							
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
				/** next answering date for starred question **/
				if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					QuestionDates questionDates = question.findNextAnsweringDate();
					if(questionDates!=null) {
						Date nextAnsweringDate = questionDates.getAnsweringDate();
						if(nextAnsweringDate!=null) {
							if(intimationLetterFilter!=null && intimationLetterFilter.equals("answeringDateForwarded")) {
								
								SimpleDateFormat dbFormat = null;
								CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
								if(dbDateFormat!=null){
									dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
								}
								//Added the following code to solve the marathi month and day issue
								String[] strNextAnsweringDates=dbFormat.format(nextAnsweringDate).split(",");
								String answeringDay=FormaterUtil.getDayInMarathi(strNextAnsweringDates[0],locale.toString());
								String dateLabel = "";
								MessageResource dateLabelMessageResource = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
								if(dateLabelMessageResource!=null && dateLabelMessageResource.getValue()!=null) {
									dateLabel = dateLabelMessageResource.getValue();
								}
								String[] strAnsweringMonth=strNextAnsweringDates[1].split(" ");
								String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
								String formattedNextAnsweringDate = answeringDay+", "+dateLabel+" "+strAnsweringMonth[0] + " " + answeringMonth + ", " + strNextAnsweringDates[2];
								letterVO.setNextAnsweringDate(formattedNextAnsweringDate);
								
							} else {
								letterVO.setNextAnsweringDate(FormaterUtil.formatDateToString(nextAnsweringDate, "dd-MM-yyyy", question.getLocale()));
							}							
						} else {
							letterVO.setNextAnsweringDate("_________");
						}
					} else {
						letterVO.setNextAnsweringDate("_________");
					}
				}
				/** ballot date for starred question **/
				if(deviceType.getType().trim().equals(ApplicationConstants.STARRED_QUESTION)
						&& intimationLetterFilter!=null && intimationLetterFilter.equals("groupChangedAfterBallot")) {
					try {
						Ballot ballotForQuestion = Ballot.find(question);
						if(ballotForQuestion!=null) {
							String formattedBallotDate = FormaterUtil.formatDateToString(ballotForQuestion.getBallotDate(), "dd MMM,yyyy", question.getLocale());
							formattedBallotDate = FormaterUtil.formatMonthInMarathiDate(formattedBallotDate, locale.toString());
							letterVO.setBallotDate(formattedBallotDate);							
						}
					} catch (Exception e) {
						isError = true;
						e.printStackTrace();
					}
				}
				/** referenced question details (later should come through referenced entities) **/
				String questionReferenceText = question.findReferencingDetailsText();
				if(questionReferenceText!=null && !questionReferenceText.isEmpty()) {
					formattedText = questionReferenceText;//FormaterUtil.formatNumbersInGivenText(questionReferenceText, question.getLocale());
					letterVO.setQuestionReferenceText(formattedText);
				} else {
					if(question.getQuestionreferenceText()!=null) {
						formattedText = question.getQuestionreferenceText();//FormaterUtil.formatNumbersInGivenText(question.getQuestionreferenceText(), question.getLocale());
						letterVO.setQuestionReferenceText(formattedText);
					} else {
						letterVO.setQuestionReferenceText("");
					}
				}
				
				
				if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION) || statusType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
						|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_REJECTION) || statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECTION)
						|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_REJECTION) || statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECTION)
						|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECTION) || statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION)) {
					if(question.getRejectionReason()!=null && !question.getRejectionReason().isEmpty()) {
						formattedText = question.getRejectionReason();//FormaterUtil.formatNumbersInGivenText(question.getRejectionReason(), question.getLocale());
						if(formattedText.endsWith("<br><p></p>")) {
							formattedText = formattedText.substring(0, formattedText.length()-11);
						} else if(formattedText.endsWith("<p></p>")) {
							formattedText = formattedText.substring(0, formattedText.length()-7);
						}
						letterVO.setRejectionReason(formattedText);
					} else {
						letterVO.setRejectionReason("");
					}
					
					letterVO.setSubject(question.getSubject());
					letterVO.setQuestionText(question.getQuestionText());
				}
				
				/** factual position (clarification) received from department **/
				if(question.getFactualPosition()!=null) {
					formattedText = question.getFactualPosition();//FormaterUtil.formatNumbersInGivenText(question.getFactualPosition(), question.getLocale());
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
						} else if(referredQuestionDeviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
							if(referredQuestion.getYaadiNumber()!=null) {
								letterVO.setReferredQuestionYaadiNumber(FormaterUtil.formatNumberNoGrouping(referredQuestion.getYaadiNumber(), locale.toString()));
							}
							if(referredQuestion.getYaadiLayingDate()!=null) {
								letterVO.setReferredQuestionYaadiLayingDate(FormaterUtil.formatDateToString(referredQuestion.getYaadiLayingDate(), ApplicationConstants.ROTATIONORDER_DATEFORMAT, locale.toString()));
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
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, locale.toString());
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
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, locale.toString());
				if(formattedText.endsWith("<br><p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-11);
				} else if(formattedText.endsWith("<p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-7);
				}
				letterVO.setBriefExplanation(formattedText);
				
				if(question.getDiscussionDate()!=null) {
					letterVO.setDiscussionDate(FormaterUtil.formatDateToString(question.getDiscussionDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString()));
				}
				
				//==================status as per filter for clarification cases==============//
				//for starred
				if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER;
				}else if(statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT;
				}
				//for unstarred
				if(statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER;
				}else if(statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT;
				}
				//for short notice
				if(statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER;
				}else if(statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT;
				}
				//for half hour discussion from question
				if(statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER;
				}else if(statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT;
				}
				//==================end of status as per filter for clarification cases==============//
				
				if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
					
					String questionsAsked = question.getQuestionsAskedInFactualPosition();
					if(questionsAsked==null || questionsAsked.isEmpty()) {
						if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						
						} else if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromMemberQuestions");
						}
					} 
					
					if(questionsAsked!=null && !questionsAsked.isEmpty()) {
						List<MasterVO> questionsAskedForClarification = new ArrayList<MasterVO>();
						StringBuffer questionIndexesForClarification=new StringBuffer();	
						String allQuestions = "";
						if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							
							allQuestions = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentQuestions");
						
						} else if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							
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
					String reportFileName = "intimationletter";
					if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
						reportFileName += "_unstarred";
					} else if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						reportFileName += "_starred";
					} else if(question.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						reportFileName += "_unstarred";
					} else if(question.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
						reportFileName += "_shortnotice";
					} else if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
						reportFileName += "_halfhour";
					} 
					if(question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
						reportFileName += "_laq";
					} else if(question.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
						reportFileName += "_lcq";
					}
					reportFileName += "(" + question.getNumber() + ")";
					if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
						
						reportFileName += "_clarification(department)";
						
					} else if(statusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
						
						if(intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)) {
							reportFileName += "_clarification(department)";
						} else {
							reportFileName += "_clarification(member)";
						}						
						
					} else {
						reportFileName += "_" + statusTypeSplit;
					}	
					String outputFormat = "WORD";
					Set<Role> roles = this.getCurrentUser().getRoles();
					Role departmentRole = Role.findByType("QIS_DEPARTMENT_USER", locale.toString());
					for(Role r :roles){
						if(r.getId().equals(departmentRole.getId())){
							outputFormat = "PDF";
							break;
						}
					}
					
					//flag parameter for letter format separation based on given housetype
					boolean isLetterFormatDependentOnHouseType = false;
					
					if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
						&& question.getDateOfAnsweringByMinister()!=null
						&& question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_FINAL_DATEADMITTED)
						&& intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
						
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit+"_member", outputFormat, reportFileName, locale.toString());
					
					}else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
							&& question.getDateOfAnsweringByMinister()!=null 
							&& question.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_FINAL_DATEADMITTED)
							&& intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
							
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit+"_department", outputFormat, reportFileName, locale.toString()); 
					
					}else if(intimationLetterFilter!=null && !intimationLetterFilter.isEmpty() && !intimationLetterFilter.equals("-")) {
						
						try {
							if(isLetterFormatDependentOnHouseType) {
								reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+intimationLetterFilter+"_"+statusTypeSplit+"_"+question.getHouseType().getType(), outputFormat, reportFileName, locale.toString());
							} else {
								reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+intimationLetterFilter+"_"+statusTypeSplit, outputFormat, reportFileName, locale.toString());
							}
							
						} catch(FileNotFoundException e) {
							if(e.getMessage().equals(ApplicationConstants.XSLT_FILE_NOT_FOUND)) {
								reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+intimationLetterFilter, outputFormat, reportFileName, locale.toString());
							}
						}
						
					}else {
						if(isLetterFormatDependentOnHouseType) {
							reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit+"_"+question.getHouseType().getType(), outputFormat, reportFileName, locale.toString());
						} else {
							reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit, outputFormat, reportFileName, locale.toString());
						}						
					}					
					System.out.println("Intimation Letter generated successfully in "+ outputFormat +" format!");
					
					openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
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
	
	@RequestMapping(value="/generateReminderLetter", method=RequestMethod.GET)
	public @ResponseBody void generateReminderLetter(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		File reportFile = null;
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> requestMap = request.getParameterMap();
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = Query.findReport(request.getParameter("reportQuery"), requestMap, true);
		
    	if(resultList!=null && !resultList.isEmpty()) {
    		List<String> serialNumbers = populateSerialNumbers(resultList, locale);
    		List<String> expectedAnswerReceivingDates = new ArrayList<String>();
    		for(int i=0; i<resultList.size(); i++) {
    			Object[] resultUnit = resultList.get(i);
    			if(resultUnit[5]!=null && !resultUnit[5].toString().isEmpty()) {
    				if(resultUnit[1].toString().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
    					Date answerRequestedDate = FormaterUtil.formatStringToDate(resultUnit[5].toString(), ApplicationConstants.DB_DATETIME_FORMAT);
    					Integer expectedAnswerReceivingDateDuration = 30;
    					CustomParameter expectedAnswerReceivingDateDurationCP = CustomParameter.findByName(CustomParameter.class, "UNSTARRED_QUESTION_EXPECTED_ANSWER_RECEIVING_DATE_DURATION", "");
    					if(expectedAnswerReceivingDateDurationCP!=null && expectedAnswerReceivingDateDurationCP.getValue()!=null) {
    						expectedAnswerReceivingDateDuration = Integer.parseInt(expectedAnswerReceivingDateDurationCP.getValue());
    					}
    					Date expectedAnswerReceivingDate = Holiday.getNextWorkingDateFrom(answerRequestedDate, expectedAnswerReceivingDateDuration, locale.toString());
    					expectedAnswerReceivingDates.add(FormaterUtil.formatDateToString(expectedAnswerReceivingDate, ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_3, locale.toString()));
    				} else {
    					expectedAnswerReceivingDates.add(resultUnit[7].toString());
    				}
    			} else {
    				expectedAnswerReceivingDates.add("");
    			}
    		}
    		HouseType houseType = null;
    		String strHouseType = request.getParameter("houseType");
    		if(strHouseType!=null && !strHouseType.isEmpty()) {
    			houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
    			if(houseType==null || houseType.getId()==null) {
    				houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, locale.toString());
    			}
    			if(houseType==null || houseType.getId()==null) {
    				houseType=HouseType.findById(HouseType.class,Long.parseLong(strHouseType));
    			}
    		}    		
    		reportFile = generateReportUsingFOP(new Object[] {resultList, expectedAnswerReceivingDates, serialNumbers}, "qis_reminder_letter_template_"+houseType.getType().toLowerCase().trim(), request.getParameter("outputFormat"), "qis_reminder_letter", locale.toString()); 		
    		if(reportFile!=null) {
    			System.out.println("Report generated successfully in " + request.getParameter("outputFormat") + " format!");
    			openOrSaveReportFileFromBrowser(response, reportFile, request.getParameter("outputFormat"));
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
			String strAnsweringDate = request.getParameter("answeringDate");
			String strClubbingStatus = request.getParameter("clubbingStatus");
			
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
				parameters.put("answeringDate", new String[]{strAnsweringDate.toString()});
				parameters.put("clubbingStatus", new String[]{strClubbingStatus.toString()});
				
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
	@RequestMapping(value="/halfhourdaysubmitreportdatefilter", method=RequestMethod.GET)
	public String halfhourDayWiseSubmissionReportDateFilter(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "standalonemotion/error";
		try{
			String strGroupId = request.getParameter("groupId");
			String strSubDepartment = request.getParameter("subDepartment");
			String strDay = request.getParameter("subdate");
			
			if(strGroupId != null && !strGroupId.isEmpty()
					&& strSubDepartment != null && !strSubDepartment.isEmpty()
					&& strDay != null && !strDay.isEmpty()){
								
				List<Object> objects = StandaloneMotionReportHelper.getSesionAndDeviceType(request, locale.toString());
				
				Session session = null; 
				DeviceType deviceType = null;
				if(!objects.isEmpty()){
					session = (Session) objects.get(0);
					deviceType = (DeviceType)objects.get(1);
				}
				
				Date subDate = null;
				try{
					subDate = FormaterUtil.stringToDate(strDay, "dd-MM-yyyy");
				}catch(ParseException pe){
					
					String[] vals = ReferenceController.decodedValues(new String[]{strDay});
					strDay = vals[0];

					subDate = FormaterUtil.formatStringToDate(strDay, "dd-MM-yyyy", locale.toString());
				}
				
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("groupId", new String[]{strGroupId});
				parameters.put("subDate", new String[]{FormaterUtil.formatDateToString(subDate, ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("subDepartment", new String[]{strSubDepartment});
				parameters.put("locale", new String[]{locale.toString()});
				
				List report = Query.findReport("HALFHOUR_DAYWISE_SUBMISSION_REPORT_DATE_FILTER", parameters);
				
				model.addAttribute("report", report);
				model.addAttribute("formater", new FormaterUtil());
				model.addAttribute("locale", locale.toString());
				
				page = "standalonemotion/reports/halfhourdaywisesubmitreport";
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
			List<String> serialNumbers = populateSerialNumbers(report, locale);
			model.addAttribute("serialNumbers", serialNumbers);
		}
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale.toString());
		model.addAttribute("report", report);
		
		return "question/reports/"+request.getParameter("reportout");		
	}
	//----------------------------------------------------------------------
	@RequestMapping(value="/viewYaadi" ,method=RequestMethod.GET)
	public @ResponseBody void generateYaadiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model) throws ELSException{
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
						deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
					CustomParameter dbDateFormat = 
							CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
					CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				}
				Group group = null;
				CustomParameter deviceTypesHavingGroupCP = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_HAVING_GROUPS", "");
				if(deviceTypesHavingGroupCP!=null && deviceTypesHavingGroupCP.getValue().contains(deviceType.getType())) {
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
				if(ballotedDeviceVOs==null || ballotedDeviceVOs.isEmpty()) {
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
				QuestionYaadiSuchiXmlVO data = new QuestionYaadiSuchiXmlVO();
				data.setHouseType(houseType.getName());
				data.setSessionNumber(session.getNumber().toString());
				data.setSessionType(sessionType.getSessionType());
				data.setSessionYear(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
				data.setSessionPlace(session.getPlace().getPlace());
				CustomParameter roleCustomParameter = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_YAADI_FOOTER_ROLE", "");
				if(roleCustomParameter==null) {
					logger.error("/**** role parameter for yaadi footer not set. ****/");
					throw new ELSException("QuestionReportController/generateYaadiReport", "role parameter for yaadi footer not set.");
				}
				Role role = Role.findByFieldName(Role.class, "type", roleCustomParameter.getValue(), locale.toString());
				if(role==null) {
					logger.error("/**** role '"+roleCustomParameter.getValue()+"' is not found. ****/");
					throw new ELSException("QuestionReportController/generateYaadiReport", "role '"+roleCustomParameter.getValue()+"' is not found.");
				}
				data.setUserRole(role.getLocalizedName());
				List<User> users = User.findByRole(false, role.getName(), locale.toString());
				//as principal secretary & secretary for starred question is only one, so user is obviously first element of the list.
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
	
	@RequestMapping(value="/generateUnstarredYaadiReport/getUnstarredYaadiNumberAndDate", method=RequestMethod.GET)
	public String getUnstarredYaadiNumberAndDate(HttpServletRequest request, ModelMap model, Locale locale) {
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		
		String retVal = "question/reports/error";
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
						model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");											
					} else {
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOTFOUND");
						} else {
							model.addAttribute("sessionId", session.getId());
							Integer highestYaadiNumber = Question.findHighestYaadiNumber(null, session, locale.toString());
							if(highestYaadiNumber!=null) {
								if(highestYaadiNumber.equals(0)) {
									model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber+1, locale.toString()));
									model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								} else if(Question.isNumberedYaadiFilled(null, session, highestYaadiNumber, locale.toString())) {
									model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber+1, locale.toString()));
									model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								} else {
									model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber, locale.toString()));
									Date yaadiLayingDate = Question.findYaadiLayingDateForYaadi(null, session, highestYaadiNumber, locale.toString());
									if(yaadiLayingDate!=null) {
										model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										model.addAttribute("isYaadiLayingDateSet", "yes");
									}
								}								
							} else {
								logger.error("**** Error in Query of finding Yaadi Number ****");
								model.addAttribute("errorcode", "QUERY_ERROR");
							}
							List<Date> availableYaadiLayingDates = Question.findAvailableYaadiLayingDatesForSession(null, session, locale.toString());
							if(availableYaadiLayingDates!=null && !availableYaadiLayingDates.isEmpty()) {
								List<String> yaadiLayingDates = new ArrayList<String>();
								for(Date eligibleDate: availableYaadiLayingDates) {
									yaadiLayingDates.add(FormaterUtil.formatDateToString(eligibleDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								}
								model.addAttribute("yaadiLayingDates", yaadiLayingDates);
							}																					
							retVal = "question/reports/getUnstarredYaadiNumberAndDate";
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("error", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "SOME_EXCEPTION_OCCURED");
				}
			}
		}		
		return retVal;
	}
	
	@Transactional
	@RequestMapping(value="/generateUnstarredYaadiReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateUnstarredYaadiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String houseTypeId = request.getParameter("houseType");
		String deviceTypeId = request.getParameter("deviceType");
		String sessionId = request.getParameter("sessionId");
		String yaadiDetailsId = request.getParameter("yaadiDetailsId");		
		String strYaadiNumber = request.getParameter("yaadiNumber");
		String strYaadiLayingDate = request.getParameter("yaadiLayingDate");
		String strChangedYaadiNumber = request.getParameter("changedYaadiNumber");
		String strChangedYaadiLayingDate = request.getParameter("changedYaadiLayingDate");
		String selectedDeviceIds = request.getParameter("selectedDeviceIds");
		String deSelectedDeviceIds = request.getParameter("deSelectedDeviceIds");
		String yaadiLayingStatusId = request.getParameter("yaadiLayingStatus");
		
		if(houseTypeId!=null && deviceTypeId!=null && sessionId!=null 
				&& strYaadiNumber!=null	&& strYaadiLayingDate!=null) {
			if(!houseTypeId.isEmpty() && !deviceTypeId.isEmpty() && !sessionId.isEmpty()
					&& !strYaadiNumber.isEmpty() && !strYaadiLayingDate.isEmpty() && !strYaadiLayingDate.equals("-")) {
				
				try {
					HouseType houseType = null;
					DeviceType deviceType = null;
					String device = null;
					Session session = null;				
					YaadiDetails yaadiDetails = null;
					Status existingLayingStatus = null;
					
					CustomParameter manuallyEnteringAllowedCP = CustomParameter.findByName(CustomParameter.class, "QIS_UNSTARRED_YAADI_MANUALLY_ENTERING_ALLOWED", "");
					boolean isManualEnteringAllowed = false;
					if(manuallyEnteringAllowedCP!=null && manuallyEnteringAllowedCP.getValue()!=null && manuallyEnteringAllowedCP.getValue().equalsIgnoreCase("true")) {
						isManualEnteringAllowed = true;
					}
					
					if(yaadiDetailsId!=null && !yaadiDetailsId.isEmpty()) {
						yaadiDetails = YaadiDetails.findById(YaadiDetails.class, Long.parseLong(yaadiDetailsId));
						if(yaadiDetails==null) {
							isError = true;
							logger.error("**** Check request parameters yaadiDetailsId for invalid value ****");
							errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.yaadiDetailsId.invalid", locale.toString());
						} else {
							houseType = yaadiDetails.getHouseType();
							deviceType = yaadiDetails.getDeviceType();
							device = yaadiDetails.getDevice();
							session = yaadiDetails.getSession();
							existingLayingStatus = yaadiDetails.getLayingStatus();
						}					
					} else {
						yaadiDetails = new YaadiDetails();
						yaadiDetails.setLocale(locale.toString());
						houseType = HouseType.findById(HouseType.class, Long.parseLong(houseTypeId));
						yaadiDetails.setHouseType(houseType);
						deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));
						yaadiDetails.setDeviceType(deviceType);
						if(deviceType!=null) {
							device = deviceType.getDevice();
						} else {
							device = "question_unstarred";						
						}
						yaadiDetails.setDevice(device);
						session = Session.findById(Session.class, Long.parseLong(sessionId));
						yaadiDetails.setSession(session);
					}
					if(!isError) {
						if(existingLayingStatus==null || !existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
							Status layingStatus = null;
							if(yaadiLayingStatusId!=null && !yaadiLayingStatusId.isEmpty()) {
								layingStatus = Status.findById(Status.class, Long.parseLong(yaadiLayingStatusId));
								if(layingStatus==null) {
									isError = true;
									logger.error("**** Check request parameters yaadiLayingStatusId for invalid value ****");
									errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.yaadiLayingStatusId.invalid", locale.toString());
								} else {
									yaadiDetails.setLayingStatus(layingStatus);
								}
							} else {
								layingStatus = Status.findByType(ApplicationConstants.YAADISTATUS_DRAFTED, locale.toString());
								if(layingStatus==null) {
									isError = true;
									logger.error("**** Status with type '" + ApplicationConstants.YAADISTATUS_DRAFTED + "' in locale " + locale.toString() + " not found****");
									errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.statusnotfound", locale.toString());
								} else {
									yaadiDetails.setLayingStatus(layingStatus);
								}
							}
						}						
						if(!isError) {							
							CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
							if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
								if(csptServer.getValue().equals("TOMCAT")){
									strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
									strYaadiLayingDate = new String(strYaadiLayingDate.getBytes("ISO-8859-1"), "UTF-8");							
									if(strChangedYaadiNumber!=null && !strChangedYaadiNumber.isEmpty()) {
										strChangedYaadiNumber = new String(strChangedYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
									}
									if(strChangedYaadiLayingDate!=null && !strChangedYaadiLayingDate.isEmpty()) {
										strChangedYaadiLayingDate = new String(strChangedYaadiLayingDate.getBytes("ISO-8859-1"), "UTF-8");
									}							
								}
							}
							/** yaadi number **/
							Integer yaadiNumber = null;
							if(existingLayingStatus!=null 
									&& 
									( 
									  existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_READY)
											||
									  existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)
									)
							) {
								yaadiNumber = Integer.parseInt(strYaadiNumber);
							} else {
								if(strChangedYaadiNumber!=null && !strChangedYaadiNumber.isEmpty()) {
									yaadiNumber = Integer.parseInt(strChangedYaadiNumber);
								} else {
									yaadiNumber = Integer.parseInt(strYaadiNumber);
								}
							}							
							yaadiDetails.setNumber(yaadiNumber);
							/** yaadi laying date **/
							Date yaadiLayingDate = null;
							if(existingLayingStatus!=null && existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
								yaadiLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
							} else {
								if(strChangedYaadiLayingDate!=null && !strChangedYaadiLayingDate.isEmpty()
										&& !strChangedYaadiLayingDate.equals("-")) {
									yaadiLayingDate = FormaterUtil.formatStringToDate(strChangedYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								} else {
									yaadiLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								}
							}							
							yaadiDetails.setLayingDate(yaadiLayingDate);
							/** yaadi devices **/
							List<Device> totalDevicesInYaadi = new ArrayList<Device>();
							if(existingLayingStatus==null || !existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
								if(yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_DRAFTED)) {
									/** yaadi removed devices **/
									List<Device> totalRemovedDevicesInYaadi = new ArrayList<Device>();	
									List<Device> existingRemovedDevicesInYaadi = yaadiDetails.getRemovedDevices();
									if(existingRemovedDevicesInYaadi!=null && !existingRemovedDevicesInYaadi.isEmpty()) {
										totalRemovedDevicesInYaadi.addAll(existingRemovedDevicesInYaadi);
									}		
									/** remove deselected devices **/
									if(deSelectedDeviceIds!=null && !deSelectedDeviceIds.isEmpty()) {
										List<Device> deSelectedDevicesInYaadi = new ArrayList<Device>();
										deSelectedDeviceIds = deSelectedDeviceIds.substring(0, deSelectedDeviceIds.length()-1);
										for(String deSelectedDeviceId: deSelectedDeviceIds.split(",")) {
											if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
												Question q = Question.findById(Question.class, Long.parseLong(deSelectedDeviceId));
												if(q!=null) {
													q.setYaadiNumber(null);
													q.setYaadiLayingDate(null);		
													q.simpleMerge();
													if(!isManualEnteringAllowed) {
														deSelectedDevicesInYaadi.add(q);
													}													
												}
											}							
										}
										totalRemovedDevicesInYaadi.addAll(deSelectedDevicesInYaadi);			
									}
									yaadiDetails.setRemovedDevices(totalRemovedDevicesInYaadi);
									/** yaadi selected devices **/					
									if(selectedDeviceIds!=null && !selectedDeviceIds.isEmpty()) {
										List<Device> selectedDevicesInYaadi = new ArrayList<Device>();
										selectedDeviceIds = selectedDeviceIds.substring(0, selectedDeviceIds.length()-1);
										for(String selectedDeviceId: selectedDeviceIds.split(",")) {
											if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
												Question q = Question.findById(Question.class, Long.parseLong(selectedDeviceId));
												if(q!=null) {
													q.setYaadiNumber(yaadiDetails.getNumber());
													q.setYaadiLayingDate(yaadiDetails.getLayingDate());
													q.simpleMerge();
													selectedDevicesInYaadi.add(q);
												}
											}							
										}
										totalDevicesInYaadi.addAll(selectedDevicesInYaadi);				
									}
									yaadiDetails.setDevices(totalDevicesInYaadi);									
								} else {
									List<Device> existingDevicesInYaadi = yaadiDetails.getDevices();
									for(Device d: existingDevicesInYaadi) {
										if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
											Question q = (Question) d;
											q.setYaadiNumber(yaadiDetails.getNumber());
											q.setYaadiLayingDate(yaadiDetails.getLayingDate());
											if(yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
												Status yaadiLaidStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale.toString());
												Status yaadiLaidStatusForGivenDeviceType = Question.findCorrespondingStatusForGivenQuestionType(yaadiLaidStatus, yaadiDetails.getDeviceType());
												q.setRecommendationStatus(yaadiLaidStatusForGivenDeviceType);
											}											
											q.simpleMerge();
										}
									}
								}
								/** save/update yaadi details **/
								if(yaadiDetails.getId()!=null) {
									yaadiDetails.merge();
								} else {
									yaadiDetails.persist();
								}
							}							
							if(yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_DRAFTED)) {
								/** regeneration only when 'manually entering questions' is not allowed **/
								if(!isManualEnteringAllowed) {
									Boolean isYaadiFilledWithSelectedDevices = yaadiDetails.isNumberedYaadiFilled();
									if(isYaadiFilledWithSelectedDevices!=null && isYaadiFilledWithSelectedDevices.equals(false)) {
										yaadiDetails = yaadiDetails.regenerate(totalDevicesInYaadi.size());
									}
								}								
							}
							/** gather report data**/
							if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
								List<Question> totalQuestionsInYaadi = new ArrayList<Question>();
								for(Device d: yaadiDetails.getDevices()) {
									Question q = (Question) d;
									totalQuestionsInYaadi.add(q);								
								}
								totalQuestionsInYaadi = Question.sort(totalQuestionsInYaadi, "number", ApplicationConstants.ASC);
								Object[] reportData = QuestionReportHelper.prepareUnstarredYaadiData(session, totalQuestionsInYaadi, locale.toString());
								/**** generate report ****/
								if(!isError) {
									reportFile = generateReportUsingFOP(reportData, "template_unstarredYaadi_report", "WORD", "unstarred_question_yaadi", locale.toString());
									if(reportFile!=null) {
										System.out.println("Report generated successfully in word format!");
										openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
										//response.
									}
								}
							}
						}
					}									
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.numberOfQuestionsInYaadiParameterNotSet")) {
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.numberOfQuestionsInYaadiParameterNotSet", locale.toString());				
					} else {
						e.printStackTrace();
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
					}						
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}				
			} else {
				isError = true;
				logger.error("**** Check request parameters houseTypeId, deviceTypeId, sessionId, yaadiNumber, yaadiLayingDate for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.empty", locale.toString());
			}
		} else {
			isError = true;
			logger.error("**** Check request parameters houseTypeId, deviceTypeId, sessionId, yaadiNumber, yaadiLayingDate for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.null", locale.toString());
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

	@RequestMapping(value="/viewSuchi" ,method=RequestMethod.GET)
	public @ResponseBody void generateSuchiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
					String processingMode = session.getParameter(deviceType.getType()+"_"+ApplicationConstants.PROCESSINGMODE);
					Date answeringDate = null;
					if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						QuestionDates questionDates = 
								QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
						answeringDate = questionDates.getAnsweringDate();
					}
					else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
							deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
						CustomParameter dbDateFormat = 
								CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
						answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
						answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					}	
					Group group = null;
					CustomParameter deviceTypesHavingGroupCP = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_HAVING_GROUPS", "");
					if(deviceTypesHavingGroupCP!=null && deviceTypesHavingGroupCP.getValue().contains(deviceType.getType())) {
						try {
							group = Group.find(session, answeringDate, locale.toString());
						} catch (ELSException e1) {
							e1.printStackTrace();
						}
					}					
					List<RoundVO> roundVOs = null;
					try {
						roundVOs = Ballot.findBallotedRoundVOsForSuchi(session, deviceType,processingMode,  group, answeringDate, locale.toString());
					} catch (ELSException e2) {
						e2.printStackTrace();
					}				
					if(roundVOs==null || roundVOs.isEmpty()) {
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
					QuestionYaadiSuchiXmlVO data = new QuestionYaadiSuchiXmlVO();
					data.setHouseType(houseType.getName());
					data.setSessionNumber(session.getNumber().toString());
					data.setSessionType(sessionType.getSessionType());
					data.setSessionYear(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
					data.setSessionPlace(session.getPlace().getPlace());
					CustomParameter roleCustomParameter = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_SUCHI_FOOTER_ROLE", "");
					if(roleCustomParameter==null) {
						logger.error("/**** role parameter for yaadi footer not set. ****/");
						throw new ELSException("QuestionReportController/generateYaadiReport", "role parameter for yaadi footer not set.");
					}
					Role role = Role.findByFieldName(Role.class, "type", roleCustomParameter.getValue(), locale.toString());
					if(role==null) {
						logger.error("/**** role '"+roleCustomParameter.getValue()+"' is not found. ****/");
						throw new ELSException("QuestionReportController/generateYaadiReport", "role '"+roleCustomParameter.getValue()+"' is not found.");
					}
					data.setUserRole(role.getLocalizedName());
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
	
	@RequestMapping(value="/generateUnstarredSuchiReport/getUnstarredYaadiNumberAndDateForSuchi", method=RequestMethod.GET)
	public String getUnstarredYaadiNumberAndDateForSuchi(HttpServletRequest request, ModelMap model, Locale locale) {
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		
		String retVal = "question/reports/error";
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
						model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");											
					} else {
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOTFOUND");
							return retVal;
						} else {
							model.addAttribute("sessionId", session.getId());
							Integer highestYaadiNumber = Question.findHighestYaadiNumber(null, session, locale.toString());
							if(highestYaadiNumber!=null) {
								if(highestYaadiNumber==0) {
									model.addAttribute("errorcode", "UNSTARRED_YAADI_NOT_GENERATED_YET");
									return retVal;
								}
								model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber, locale.toString()));
								Date yaadiLayingDate = Question.findYaadiLayingDateForYaadi(null, session, highestYaadiNumber, locale.toString());
								if(yaadiLayingDate!=null) {
									model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								}
							}
							retVal = "question/reports/getUnstarredYaadiNumberAndDateForSuchi";
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("error", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "SOME_EXCEPTION_OCCURED");
				}
			}
		}		
		return retVal;
	}
	
	@RequestMapping(value="/generateUnstarredSuchiReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateUnstarredSuchiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String sessionId = request.getParameter("sessionId");
		String strYaadiNumber = request.getParameter("yaadiNumber");
		String strYaadiLayingDate = request.getParameter("yaadiLayingDate");
		
		if(sessionId!=null && strYaadiNumber!=null && strYaadiLayingDate!=null){
			if(!sessionId.isEmpty() && !strYaadiNumber.isEmpty() && !strYaadiLayingDate.isEmpty() && !strYaadiLayingDate.equals("-")){
				try {
					CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
						if(csptServer.getValue().equals("TOMCAT")){
							strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
							strYaadiLayingDate = new String(strYaadiLayingDate.getBytes("ISO-8859-1"), "UTF-8");						
						}
					}
					Session session = Session.findById(Session.class, Long.parseLong(sessionId));
					if(session==null) {
						logger.error("**** Session not found with request parameter sessionId ****");
						throw new ELSException();
					}		
					Integer yaadiNumber = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strYaadiNumber).intValue();
					Date yaadiLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
					List<Question> totalQuestionsInYaadi = Question.findQuestionsInNumberedYaadi(null, session, yaadiNumber, yaadiLayingDate, locale.toString());
					Object[] reportData = QuestionReportHelper.prepareUnstarredYaadiData(session, totalQuestionsInYaadi, locale.toString());
					/**** generate report ****/
					if(!isError) {
						reportFile = generateReportUsingFOP(reportData, "template_unstarredSuchi_report", "WORD", "unstarred_question_suchi", locale.toString());
						if(reportFile!=null) {
							System.out.println("Unstarred Suchi Report generated successfully in word format!");
							openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.numberOfQuestionsInYaadiParameterNotSet")) {
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.numberOfQuestionsInYaadiParameterNotSet", locale.toString());				
					} else {
						e.printStackTrace();
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
					}						
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}
			}else{
				isError = true;
				logger.error("**** Check request parameters sessionId, yaadiNumber, yaadiLayingDate for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters sessionId, yaadiNumber, yaadiLayingDate for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.null", locale.toString());
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
	
	@RequestMapping(value="/generateClubbedIntimationLetter/getClubbedQuestions", method=RequestMethod.GET)
	public String getClubbedQuestionsForIntimationReport(HttpServletRequest request, ModelMap model, Locale locale) {
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String retVal = "question/reports/error";
		String questionId = request.getParameter("questionId");
		if(questionId!=null && !questionId.isEmpty()) {
			Question question = Question.findById(Question.class, Long.parseLong(questionId));
			if(question!=null) {
				model.addAttribute("questionId", questionId);
				List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(question, ApplicationConstants.DESC);
				if(clubbedEntities!=null && !clubbedEntities.isEmpty()) {
					List<Reference> clubbedQuestionVOs = new ArrayList<Reference>();
					for(ClubbedEntity ce: clubbedEntities) {
						Question clubbedQuestion = ce.getQuestion();
						if(Question.isAdmittedThroughClubbing(clubbedQuestion)) {							
							Reference clubbedQuestionVO = new Reference();
							clubbedQuestionVO.setId(clubbedQuestion.getId().toString());
							clubbedQuestionVO.setNumber(FormaterUtil.formatNumberNoGrouping(clubbedQuestion.getNumber(), clubbedQuestion.getLocale()));
							clubbedQuestionVOs.add(clubbedQuestionVO);
						}
					}
					if(!clubbedQuestionVOs.isEmpty()) {
						model.addAttribute("nameClubbedQuestionVOs", clubbedQuestionVOs);
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
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		
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
					model.addAttribute("houseType", houseType.getId());
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					model.addAttribute("sessionType", sessionType.getId());
					Integer sessionYear = Integer.parseInt(strSessionYear);
					model.addAttribute("sessionYear", sessionYear);
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
					Date limitingDateForSession = null;
					if(session.getEndDate().compareTo(new Date())<=0) {
						limitingDateForSession = session.getEndDate();
					} else if(session.getStartDate().compareTo(new Date())>=0) {
						limitingDateForSession = session.getStartDate();
					} else {
						limitingDateForSession = new Date();
					}
					queryParameters.put("limitingDateForSession", new String[]{FormaterUtil.formatDateToString(limitingDateForSession, ApplicationConstants.DB_DATEFORMAT)});
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
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String errorpage="ballot/error";
		try{
			String strMember = request.getParameter("member");
			String strQuestionType = request.getParameter("questionType");
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSession = request.getParameter("session");
			String groups = request.getParameter("groups");
			String status = request.getParameter("status");
			
			if(strMember!=null && strQuestionType!=null && strSession!=null){
				if(!strMember.isEmpty() && !strQuestionType.isEmpty()&& !strSession.isEmpty()){					
					/**** find all questions of members submitted in given session (questions from chart) ****/
					Map<String, String[]> queryParameters = new HashMap<String, String[]>();
					queryParameters.put("sessionId", new String[]{strSession});
					queryParameters.put("memberId", new String[]{strMember});
					queryParameters.put("questionTypeId", new String[]{strQuestionType});
					queryParameters.put("houseTypeId", new String[]{strHouseType});
					queryParameters.put("sessionTypeId", new String[]{strSessionType});
					queryParameters.put("sessionYear", new String[]{strSessionYear});							
					String groupNumberLimitParameter = ((CustomParameter) CustomParameter.findByName(CustomParameter.class, "DEFAULT_GROUP_NUMBER", "")).getValue();
					if(groups==null || groups.isEmpty() || groups.equals("0")) {
						for(Integer i=1; i<=Integer.parseInt(groupNumberLimitParameter); i++) {
							queryParameters.put("group"+i, new String[]{i.toString()});
						}						
					} else {						
						for(Integer i=1; i<=Integer.parseInt(groupNumberLimitParameter); i++) {
							boolean isGroupSelectedByUser = false;
							for(String grpNo: groups.split(",")) {
								if(i.toString().equals(grpNo)) {
									isGroupSelectedByUser = true;
									break;
								}
							}
							if(isGroupSelectedByUser) {
								queryParameters.put("group"+i, new String[]{i.toString()});
							} else {
								queryParameters.put("group"+i, new String[]{"-1"});
							}
						}
					}
					queryParameters.put("statusId", new String[]{status});
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
	
	@RequestMapping(value="/questionsonlinesubmissioncountreport/init",method=RequestMethod.GET)
	public String initQuestionsOnlineSubmissionCountReport(final HttpServletRequest request,final ModelMap model,final Locale locale) throws ELSException{
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String responsePage="question/reports/error";
		
		String strQuestionType = request.getParameter("questionType");
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		
		if(strQuestionType!=null && strHouseType!=null && strSessionType!=null && strSessionYear!=null
				&& !strQuestionType.isEmpty() && !strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()) {
			
			/**** populate selected questiontype ****/
			DeviceType questionType=DeviceType.findByName(DeviceType.class, strQuestionType, locale.toString());
			if(questionType==null) {
				questionType=DeviceType.findByType(strQuestionType, locale.toString());
			}
			if(questionType==null) {
				questionType=DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
			}
			if(questionType==null) {
				logger.error("**** parameter questionType is invalid ****");
				model.addAttribute("errorcode", "invalid_parameters");
				return responsePage;
			}
			model.addAttribute("questionType",questionType.getId());
			/**** populate selected housetype ****/
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			if(houseType==null) {
				houseType = HouseType.findByName(strHouseType, locale.toString());
			}
			if(houseType==null) {
				houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
			}
			if(houseType==null) {
				logger.error("**** parameter houseType is invalid ****");
				model.addAttribute("errorcode", "invalid_parameters");
				return responsePage;
			}
			model.addAttribute("houseType", houseType.getId());	
			/**** populate selected sessiontype ****/
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			if(sessionType==null) {
				logger.error("**** parameter sessionType is invalid ****");
				model.addAttribute("errorcode", "invalid_parameters");
				return responsePage;
			}
			model.addAttribute("sessionType", sessionType.getId());
			/**** populate selected sessionyear ****/
			Integer sessionYear = Integer.parseInt(strSessionYear);
			model.addAttribute("sessionYear", sessionYear);		
			/**** populate selected session ****/
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			if(session==null) {								
				logger.error("**** Session Not Found ****");
				model.addAttribute("errorcode", "SESSIONS_NOTFOUND");
				return responsePage;
			}
			model.addAttribute("session",session.getId());
			/**** submission start date of the session as default fromDate ****/
			String submissionStartDateSessionParameter = session.getParameter(questionType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
			if(submissionStartDateSessionParameter==null || submissionStartDateSessionParameter.isEmpty()) {
				logger.error("**** Submission start date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_undefined_for_session");
				return responsePage;
			}
			Date submissionStartDateForSession = FormaterUtil.formatStringToDate(submissionStartDateSessionParameter, ApplicationConstants.DB_DATEFORMAT);
			if(submissionStartDateForSession==null)  {
				logger.error("**** Submission start date parameter is set to invalid value for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_invalid_for_session");
				return responsePage;
			}
			model.addAttribute("defaultFromDate", FormaterUtil.formatDateToString(submissionStartDateForSession, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			/**** submission end date of the session as default toDate ****/
			String submissionEndDateSessionParameter = session.getParameter(questionType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
			if(submissionEndDateSessionParameter==null || submissionEndDateSessionParameter.isEmpty()) {
				logger.error("**** Submission end date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_end_date_parameter_undefined_for_session");
				return responsePage;
			}
			Date submissionEndDateForSession = FormaterUtil.formatStringToDate(submissionEndDateSessionParameter, ApplicationConstants.DB_DATEFORMAT);
			if(submissionEndDateForSession==null)  {
				logger.error("**** Submission end date parameter is set to invalid value for the session ****");
				model.addAttribute("errorcode", "submission_end_date_parameter_invalid_for_session");
				return responsePage;
			}
			model.addAttribute("defaultToDate", FormaterUtil.formatDateToString(submissionEndDateForSession, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			/**** Check whether current date is allowed for submission ****/
			Calendar currentDateCalendar = Calendar.getInstance();
			currentDateCalendar.setTime(new Date());	
//			currentDateCalendar.set(2015, 6, 12);
			currentDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
			currentDateCalendar.set(Calendar.MINUTE, 0);
			currentDateCalendar.set(Calendar.SECOND, 0);
			currentDateCalendar.set(Calendar.MILLISECOND, 0);
			if(	
				(submissionStartDateForSession.before(currentDateCalendar.getTime()) || submissionStartDateForSession.equals(currentDateCalendar.getTime()))
						&&
				(submissionEndDateForSession.after(currentDateCalendar.getTime()) || submissionEndDateForSession.equals(currentDateCalendar.getTime()))
			) {
				model.addAttribute("isCurrentDateValidForSubmission", true);
			} else {
				model.addAttribute("isCurrentDateValidForSubmission", false);
			}
			responsePage = "question/reports/questions_online_submission_count_init";
		} else {
			logger.error("**** Check request parameters 'questionType,houseType,sessionType,sessionYear' for null/empty values ****");
			model.addAttribute("errorcode", "insufficient_parameters");
		}
				
		return responsePage;
	}
	
	@RequestMapping(value="/questionsonlinesubmissioncountreport",method=RequestMethod.GET)
	public @ResponseBody void generateQuestionsOnlineSubmissionCountReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale) throws ELSException{
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		String session = request.getParameter("session");
		String questionType = request.getParameter("questionType");
		String houseType = request.getParameter("houseType");
		String criteria = request.getParameter("criteria");
		String forTodayStr = request.getParameter("forToday");
		String fromDateStr = request.getParameter("fromDate");
		String toDateStr = request.getParameter("toDate");
		
		try {
			if(session!=null && !session.isEmpty() 
					&& questionType!=null && !questionType.isEmpty()
					&& houseType!=null && !houseType.isEmpty()
					&& criteria!=null && !criteria.isEmpty()) {
				/**** set fromDate & toDate to currentDate if forToday parameter is true ****/
				Boolean forToday = Boolean.parseBoolean(forTodayStr);
				if(forToday!=null && forToday.booleanValue()==true) {
					Date currentDate = new Date();
					//format fromDate & toDate for query
					fromDateStr = FormaterUtil.formatDateToString(currentDate, ApplicationConstants.DB_DATEFORMAT);
					toDateStr = FormaterUtil.formatDateToString(currentDate, ApplicationConstants.DB_DATEFORMAT);
				}
				
				if(fromDateStr!=null && !fromDateStr.isEmpty() && toDateStr!=null && !toDateStr.isEmpty()) {
					if(forToday==null || forToday.booleanValue()==false) {
						//handle server encoding for fromDate & toDate parameters
						CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
						if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
							if(csptServer.getValue().equals("TOMCAT")){							
								fromDateStr = new String(fromDateStr.getBytes("ISO-8859-1"), "UTF-8");
								toDateStr = new String(toDateStr.getBytes("ISO-8859-1"), "UTF-8");						
							}
						}
						//format fromDate for query
						Date fromDate = FormaterUtil.formatStringToDate(fromDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
						fromDateStr = FormaterUtil.formatDateToString(fromDate, ApplicationConstants.DB_DATEFORMAT);
						//format toDate for query
						Date toDate = FormaterUtil.formatStringToDate(toDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
						toDateStr = FormaterUtil.formatDateToString(toDate, ApplicationConstants.DB_DATEFORMAT);
					}
					
					//submission status as per devicetype
					Status submitStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, locale.toString());
					DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(questionType));
					submitStatus = Question.findCorrespondingStatusForGivenQuestionType(submitStatus, deviceType);	
					if(submitStatus!=null) {
						Map<String, String[]> queryParameters = new HashMap<String, String[]>();
						queryParameters.put("locale", new String[] {locale.toString()});
						queryParameters.put("sessionId", new String[] {session});
						queryParameters.put("questionTypeId", new String[] {questionType});
						queryParameters.put("houseTypeId", new String[] {houseType});												
						queryParameters.put("fromDate", new String[] {fromDateStr});
						queryParameters.put("toDate", new String[] {toDateStr});
						queryParameters.put("submitStatusId", new String[] {submitStatus.getId().toString()});
						
						String queryName = "QIS_MEMBERWISE_QUESTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						if(criteria.equals("datewise")) {
							queryName = "QIS_DATEWISE_QUESTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						}
						List reportData = Query.findReport(queryName, queryParameters);
						if(reportData!=null && !reportData.isEmpty()) {
							/**** generate report ****/
							if(!isError) {								
								List<String> serialNumbers = populateSerialNumbers(reportData, locale);
								reportFile = generateReportUsingFOP(new Object[]{reportData, criteria, serialNumbers}, "questions_online_submission_counts_template", "WORD", deviceType.getType()+"_"+criteria+"_online_submission_counts_report", locale.toString());				
								if(reportFile!=null) {
									System.out.println("Report generated successfully in WORD format!");
									openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
								}
							}
						} else {
							//error
							isError = true;	
						}
					} else {
						//error code
						isError = true;	
					}					
				} else {
					//error code
					isError = true;	
				}
			} else {
				//error code
				isError = true;	
			}
		} catch(Exception e) {
			//error code
			e.printStackTrace();
			isError = true;					
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
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
	
	@RequestMapping(value="/departmentwiseStatsReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateDepartmentwiseStatsReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");		
		String questionType = request.getParameter("questionType");
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && questionType!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !questionType.isEmpty()){	
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
							if(questionType.equals("starred")) {
								DeviceType starredQuestionType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale.toString());
								queryParameters.put("starredQuestionTypeId", new String[]{starredQuestionType.getId().toString()});
							}							
							DeviceType unstarredQuestionType = DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, locale.toString());
							queryParameters.put("unstarredQuestionTypeId", new String[]{unstarredQuestionType.getId().toString()});
							queryParameters.put("locale", new String[]{locale.toString()});
							String queryName= "QIS_" + questionType.toUpperCase() + "_DEPARTMENTWISE_STATS_"+houseType.getType().toUpperCase()+"_REPORT";														
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
									reportFile = generateReportUsingFOP(new Object[]{reportData,serialNumbers,localisedContent}, "qis_"+questionType+"DepartmentwiseStatsReport_"+houseType.getType(), "WORD", "qis_"+questionType+"DepartmentwiseStats_report", locale.toString());
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
	
	@RequestMapping(value="/unstarredacrosssessiondepartmentwise/sessionsinvolved", method=RequestMethod.GET)
	public String getSessionsInvolvedForUnstarredAcrossSessionDepartmentwiseQuestionsReport(HttpServletRequest request, ModelMap model, Locale locale) {
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String retVal = "question/reports/error";
		String strHouseType = request.getParameter("houseType");
		
		if(strHouseType!=null && !strHouseType.isEmpty()){
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			if(houseType==null) {
				houseType = HouseType.findByName(strHouseType, locale.toString());
			}
			if(houseType==null) {
				houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
			}
			List<SessionVO> sessionVOs = Session.findAllSessionDetailsForGivenHouseType(houseType, null, locale.toString());
			if(sessionVOs==null || sessionVOs.isEmpty()) {
				model.addAttribute("errorcode", "SESSIONS_NOTFOUND");
				return retVal;
			}
			model.addAttribute("sessionVOs", sessionVOs);
			retVal = "question/reports/unstarredacrosssessiondepartmentwise";
		} else {
			model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND");
		}
		return retVal;
	}
	
	@RequestMapping(value="/unstarredacrosssessiondepartmentwise/export" ,method=RequestMethod.GET)
	public @ResponseBody void generateUnstarredAcrossSessionDepartmentwiseQuestionsReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;		
		List<Object[]> unstarredAcrossSessionDepartmentwiseQuestions = null;

		String sessionIds = request.getParameter("sessionIds");		
		if(sessionIds!=null && !sessionIds.isEmpty()) {
			unstarredAcrossSessionDepartmentwiseQuestions = Question.findUnstarredAcrossSessionDepartmentwiseQuestions(sessionIds, locale.toString());
			if(unstarredAcrossSessionDepartmentwiseQuestions==null
					|| unstarredAcrossSessionDepartmentwiseQuestions.isEmpty()) {
				isError = true;		
				logger.error("**** no questions found for given session ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredacrosssessiondepartmentwise.nodata", locale.toString());
			} else {
				/**** generate report ****/
				if(!isError) {
					try {
						reportFile = generateReportUsingFOP(new Object[]{unstarredAcrossSessionDepartmentwiseQuestions}, "template_unstarredacrosssessiondepartmentwise_report", "WORD", "unstarredacrosssessiondepartmentwise_report", locale.toString());
						if(reportFile!=null) {
							System.out.println("Report generated successfully in word format!");
							openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
						}
					} catch (Exception e) {
						e.printStackTrace();
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
					}
				}
			}
		} else {
			isError = true;			
			logger.error("**** request parameter 'sessions' is not set because no session is selected by user ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredacrosssessiondepartmentwise.sessions.notset", locale.toString());
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
	
	@RequestMapping(value="/ahwalStarredUnstarredReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateAhwalStarredUnstarredReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
							queryParameters.put("locale", new String[]{locale.toString()});
							String queryName= "QIS_AHWAL_STARRED_UNSTARRED_STATS_"+houseType.getType().toUpperCase()+"_REPORT";														
							List reportData = Query.findReport(queryName, queryParameters);
							if(reportData!=null && !reportData.isEmpty()) {
								List<String> localisedContent = new ArrayList<String>();
								String percentageStarredAnswered="";								
								if(reportData.get(0)!=null) {
									Object[] obj = (Object[]) reportData.get(0);
									for(String i: obj[1].toString().split("~#")) {
										localisedContent.add(i);
									}			
									if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
										if(obj[8]!=null && !obj[8].toString().isEmpty()
												&& obj[9]!=null && !obj[9].toString().isEmpty()) {
											BigDecimal answeredCount= new BigDecimal((Long)FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(obj[9].toString()));
											BigDecimal admittedCount= new BigDecimal((Long)FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(obj[8].toString()));
											if(admittedCount.intValue()>0) {
												BigDecimal percentAnswered = new BigDecimal(answeredCount.doubleValue()/admittedCount.doubleValue()*100);
												percentAnswered = percentAnswered.setScale(2, RoundingMode.HALF_UP);
												percentageStarredAnswered = FormaterUtil.formatNumberNoGrouping(percentAnswered.doubleValue(), locale.toString());
											}
										}
									}
								}
								String percentageUnStarredAnswered="";	
								if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
									if(reportData.get(1)!=null) {
										Object[] obj = (Object[]) reportData.get(1);									
										if(obj[8]!=null && !obj[8].toString().isEmpty()
												&& obj[9]!=null && !obj[9].toString().isEmpty()) {
											BigDecimal answeredCount= new BigDecimal((Long)FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(obj[9].toString()));
											BigDecimal admittedCount= new BigDecimal((Long)FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(obj[8].toString()));
											if(admittedCount.intValue()>0) {
												BigDecimal percentAnswered = new BigDecimal(answeredCount.doubleValue()/admittedCount.doubleValue()*100);
												percentAnswered = percentAnswered.setScale(2, RoundingMode.HALF_UP);
												percentageUnStarredAnswered = FormaterUtil.formatNumberNoGrouping(percentAnswered.doubleValue(), locale.toString());
											}
										}									
									}
								}								
								List<String> percentageAnswered = new ArrayList<String>();
								percentageAnswered.add(percentageStarredAnswered);
								percentageAnswered.add(percentageUnStarredAnswered);
								/**** generate report ****/
								if(!isError) {			
									reportFile = generateReportUsingFOP(new Object[]{reportData,percentageAnswered,localisedContent}, "qis_ahwal_starredUnstarredStatsReport_"+houseType.getType(), "WORD", "qis_ahwal_starredUnstarredStats_Report", locale.toString());
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
	
	@RequestMapping(value="/ahwalShortNoticeStatsReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateAhwalShortNoticeStatsReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
							queryParameters.put("locale", new String[]{locale.toString()});
							String queryName= "QIS_AHWAL_SHORTNOTICE_STATS_"+houseType.getType().toUpperCase()+"_REPORT";														
							List reportData = Query.findReport(queryName, queryParameters);
							if(reportData!=null && !reportData.isEmpty()) {
								List<String> localisedContent = new ArrayList<String>();
								String percentageAnswered="";
								if(reportData.get(0)!=null) {
									Object[] obj = (Object[]) reportData.get(0);
									for(String i: obj[0].toString().split("~#")) {
										localisedContent.add(i);
									}									
									if(obj[6]!=null && !obj[6].toString().isEmpty()
											&& obj[7]!=null && !obj[7].toString().isEmpty()) {
										BigDecimal answeredCount= new BigDecimal((Long)FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(obj[7].toString()));
										BigDecimal admittedCount= new BigDecimal((Long)FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(obj[6].toString()));
										if(admittedCount.intValue()>0) {
											BigDecimal percentAnswered = new BigDecimal(answeredCount.doubleValue()/admittedCount.doubleValue()*100);
											percentAnswered = percentAnswered.setScale(2, RoundingMode.HALF_UP);
											percentageAnswered = FormaterUtil.formatNumberNoGrouping(percentAnswered.doubleValue(), locale.toString());
										}
									}
								}								
								/**** generate report ****/
								if(!isError) {			
									reportFile = generateReportUsingFOP(new Object[]{reportData,percentageAnswered,localisedContent}, "qis_ahwal_shortNoticeStatsReport_"+houseType.getType(), "WORD", "qis_ahwal_shortNoticeStats_report", locale.toString());
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
	
	@RequestMapping(value="/ahwalHDConditionReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateAhwalHDConditionReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String questionType = request.getParameter("questionType");
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && questionType!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !questionType.isEmpty()){
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
							queryParameters.put("questionType", new String[]{questionType});
							queryParameters.put("locale", new String[]{locale.toString()});
							String queryName= "QIS_AHWAL_HDQ_CONDITION_REPORT";													
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
									if(questionType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
										reportFile = generateReportUsingFOP(new Object[]{reportData,serialNumbers,localisedContent,questionType}, "qis_ahwal_hdcondition_"+houseType.getType(), "WORD", "qis_ahwal_halfHourFromQuestion_Report", locale.toString());
									} else {
										reportFile = generateReportUsingFOP(new Object[]{reportData,serialNumbers,localisedContent,questionType}, "qis_ahwal_hdcondition_"+houseType.getType(), "WORD", "qis_ahwal_halfHourStandalone_Report", locale.toString());
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
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/departmentwisequestions", method=RequestMethod.GET)
	public String getDepartmentwiseQuestionReport(HttpServletRequest request, Model model, Locale locale){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String selectedHouseType = request.getParameter("houseType");
		if(selectedHouseType==null || selectedHouseType.isEmpty()) {
			selectedHouseType = ApplicationConstants.LOWER_HOUSE;
		}
		
		Map<String, String[]> requestMap = request.getParameterMap();
		List answeringDates = Query.findReport("QIS_DEPARTMENTWISE_QUESTIONS_ANSWERING_DATES", requestMap);
		model.addAttribute("answeringDates", answeringDates);
		model.addAttribute("selectedAnsweringDate", request.getParameter("answeringDate"));
		
		DeviceType starredDeviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale.toString());
		if(starredDeviceType!=null) {
			model.addAttribute("starredDeviceType", starredDeviceType.getId());
		}
		Status admittedStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale.toString());
		if(admittedStatus!=null) {
			model.addAttribute("admittedStatus", admittedStatus.getId());
		}
		
		List report = Query.findReport(request.getParameter("report"), requestMap);
		if(report != null && !report.isEmpty()){
			String[] clubbedNumbersArr = new String[report.size()];
			int count=0;
			for(Object o: report) {
				Object[] obj = (Object[])o;
				if(obj!=null) {
					if(count==0) {
						String localisedContent = obj[0].toString().replaceAll("&#10;", "<br/>");
						model.addAttribute("localisedContent", localisedContent.split("~#"));
					}
					if(obj[8]!=null) {
						String[] clubbedQuestionNumbers = obj[8].toString().split(",");
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
		model.addAttribute("selectedDeviceType", request.getParameter("deviceType"));
		model.addAttribute("selectedStatus", request.getParameter("status"));
		
		return "question/reports/"+request.getParameter("reportout")+"_"+selectedHouseType;
	}//showTabByIdAndUrl('details_tab', 'question/report/sankshiptAhwal');
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/sankshiptAhwal", method=RequestMethod.GET)
	public String getSankshiptAhwalInit(HttpServletRequest request, Model model, Locale locale){	
		String selectedHouseType = request.getParameter("selectedHouseType");
		if(selectedHouseType==null || selectedHouseType.isEmpty()) {
			model.addAttribute("errorcode", "insufficient_parameters");
			return "question/reports/error";
		} 
		model.addAttribute("selectedHouseType", selectedHouseType);
		return "question/reports/sankshiptahwalinit";	
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/departmentwisequestions/export", method=RequestMethod.GET)
	public void exportDepartmentwiseAdmittedQuestionReport(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale) throws UnsupportedEncodingException{
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
					if(obj[8]!=null) {
						String[] clubbedQuestionNumbers = obj[8].toString().split(",");
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
			
			String selectedAnsweringDate = request.getParameter("answeringDate");
			if(selectedAnsweringDate==null) {
				selectedAnsweringDate = "";
			}
			
			String selectedHouseType = request.getParameter("houseType");
			if(selectedHouseType==null || selectedHouseType.isEmpty()) {
				selectedHouseType = ApplicationConstants.LOWER_HOUSE;
			}
			
			String totalCount = request.getParameter("totalCount");
			if(totalCount==null) {
				totalCount = "";
			}
			
			String answeredCount = request.getParameter("answeredCount");
			if(answeredCount==null) {
				answeredCount = "";
			}
			
			String answerPendingCount = request.getParameter("answerPendingCount");
			if(answerPendingCount==null) {
				answerPendingCount = "";
			}
			
			List<String> serialNumbers = new ArrayList<String>();
			for(int i=1; i<=report.size(); i++) {
				String sNo = FormaterUtil.formatNumberNoGrouping(i, locale.toString());
				serialNumbers.add(sNo);
			}
			
			CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(deploymentServerCP.getValue().equals("TOMCAT")){
				totalCount = new String(totalCount.getBytes("ISO-8859-1"),"UTF-8");
				answeredCount = new String(answeredCount.getBytes("ISO-8859-1"),"UTF-8");
				answerPendingCount = new String(answerPendingCount.getBytes("ISO-8859-1"),"UTF-8");
			}
			
			/**** generate report ****/
			if(!isError) {
				try {
					reportFile = generateReportUsingFOP(new Object[]{report,localisedContent, clubbedNumbersList, selectedAnsweringDate, totalCount, answeredCount, answerPendingCount, serialNumbers}, "qis_departmentwise_questions_"+selectedHouseType, request.getParameter("outputFormat"), "qis_departmentwisequestions", locale.toString());
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
	
	@RequestMapping(value="/generateUnstarredYaadiReport/init", method=RequestMethod.GET)
	public String initUnstarredQuestionsYaadi(HttpServletRequest request, ModelMap model, Locale locale) {
		String retVal = "question/reports/error";
		
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty()){
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findByName(strHouseType, locale.toString());
					}
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}										
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					if(houseType==null || sessionType==null) {
						logger.error("**** HouseType or SessionType Not Found ****");
						model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");											
					} else {
						model.addAttribute("houseTypeId", houseType.getId());
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOTFOUND");
						} else {
							model.addAttribute("sessionId", session.getId());
							DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
							if(deviceType==null) {								
								logger.error("**** Device Type Not Found ****");
								model.addAttribute("errorcode", "DEVICETYPE_NOTFOUND");
							} else {
								model.addAttribute("deviceTypeId", deviceType.getId());										
								Integer highestYaadiNumber = YaadiDetails.findHighestYaadiNumber(deviceType, session, locale.toString());					
								if(highestYaadiNumber!=null) {
									YaadiDetails yaadiDetails = null;
									List<Device> totalDevicesInYaadi = new ArrayList<Device>();
									if(highestYaadiNumber.intValue()>0) {
										yaadiDetails = YaadiDetails.find(deviceType, session, highestYaadiNumber, locale.toString());
									}	
									if(yaadiDetails==null || yaadiDetails.isNumberedYaadiFilled()) {
										/** populate Data for New Yaadi which is either first or latest **/
										yaadiDetails=null;
										model.addAttribute("yaadiDetailsId", "");
										model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber+1, locale.toString()));
										model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										totalDevicesInYaadi = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, 0, locale.toString());
									} else {
										/** populate Data for Highest Yaadi which is not yet filled **/
										model.addAttribute("yaadiDetailsId", yaadiDetails.getId());
										model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(yaadiDetails.getNumber(), locale.toString()));
										Date yaadiLayingDate = yaadiDetails.getLayingDate();
										if(yaadiLayingDate!=null) {
											model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
											model.addAttribute("isYaadiLayingDateSet", "yes");
										}
										if(yaadiDetails.getLayingStatus()!=null 
												&& (yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_READY)
													|| yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_LAID))
										) {
											totalDevicesInYaadi = yaadiDetails.getDevices();
										} else {
											List<Device> existingDevicesInYaadi = yaadiDetails.getDevices();
											totalDevicesInYaadi.addAll(existingDevicesInYaadi);
											List<Device> newlyAddedDevices = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, existingDevicesInYaadi.size(), locale.toString());
											if(newlyAddedDevices!=null && !newlyAddedDevices.isEmpty()) {
												totalDevicesInYaadi.addAll(newlyAddedDevices);
											}
										}																				
									}
									/** populate device vo **/									
									if(totalDevicesInYaadi!=null && !totalDevicesInYaadi.isEmpty()) {
										String yaadiDevicesCount = FormaterUtil.formatNumberNoGrouping(totalDevicesInYaadi.size(), locale.toString());
										model.addAttribute("yaadiDevicesCount", yaadiDevicesCount);
										List<DeviceVO> totalDevicesInYaadiVOs = populateDevicesForNumberedYaadi(totalDevicesInYaadi, locale.toString());
										totalDevicesInYaadiVOs = DeviceVO.sort(totalDevicesInYaadiVOs, "number", ApplicationConstants.ASC);
										model.addAttribute("totalDevicesInYaadiVOs", totalDevicesInYaadiVOs);
									}
									List<Date> availableYaadiLayingDates = Question.findAvailableYaadiLayingDatesForSession(null, session, locale.toString());
									if(availableYaadiLayingDates!=null && !availableYaadiLayingDates.isEmpty()) {
										List<String> yaadiLayingDates = new ArrayList<String>();
										for(Date eligibleDate: availableYaadiLayingDates) {
											yaadiLayingDates.add(FormaterUtil.formatDateToString(eligibleDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										}
										model.addAttribute("yaadiLayingDates", yaadiLayingDates);
									}
									/** populate group numbers **/
									CustomParameter deviceTypesHavingGroupCP = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_HAVING_GROUPS", "");
									if(deviceTypesHavingGroupCP!=null && deviceTypesHavingGroupCP.getValue()!=null && !deviceTypesHavingGroupCP.getValue().isEmpty()) {
										if(deviceTypesHavingGroupCP.getValue().contains(deviceType.getType())) {
											CustomParameter groupNumberLimitCP = CustomParameter.findByName(CustomParameter.class, "NO_OF_GROUPS", "");
											if(groupNumberLimitCP!=null && groupNumberLimitCP.getValue()!=null && !groupNumberLimitCP.getValue().isEmpty()) {
												Integer groupNumberLimit = Integer.parseInt(groupNumberLimitCP.getValue()); 
												List<Reference> groupNumbers = new ArrayList<Reference>();
												for(Integer i=1; i<=groupNumberLimit; i++) {
													Reference groupNumber = new Reference();
													groupNumber.setNumber(i.toString());
													groupNumber.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
													groupNumbers.add(groupNumber);
												}
												model.addAttribute("groupNumbers", groupNumbers);
											}
										}
									}
									/** populate yaadi statuses **/
									CustomParameter yaadiLayingStatusesCP = CustomParameter.findByName(CustomParameter.class, "YAADI_LAYING_STATUSES", "");		
									if(yaadiLayingStatusesCP!=null && yaadiLayingStatusesCP.getValue()!=null && !yaadiLayingStatusesCP.getValue().isEmpty()) {
										List<Status> yaadiLayingStatuses = Status.findStatusContainedIn(yaadiLayingStatusesCP.getValue(), locale.toString());
										model.addAttribute("yaadiLayingStatuses", yaadiLayingStatuses);
										if(yaadiDetails!=null && yaadiDetails.getLayingStatus()!=null && yaadiDetails.getLayingStatus().getId()!=null) {
											model.addAttribute("yaadiLayingStatus", yaadiDetails.getLayingStatus());
										}
									}
									/** populate whether to allow manually entering questions **/
									CustomParameter manuallyEnteringAllowedCP = CustomParameter.findByName(CustomParameter.class, "QIS_UNSTARRED_YAADI_MANUALLY_ENTERING_ALLOWED", "");
									if(manuallyEnteringAllowedCP!=null) {
										model.addAttribute("manuallyEnteringAllowed", manuallyEnteringAllowedCP.getValue());
									}
									retVal = "question/reports/unstarredyaadi";
								} else {
									logger.error("**** Error in Query of finding Yaadi Number ****");
									model.addAttribute("errorcode", "QUERY_ERROR");
								}								
							}										
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("device.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("error", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "SOME_EXCEPTION_OCCURED");
				}
			}
		}
		
		return retVal;
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
				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();	
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
											}else {
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
							
							CustomParameter onPaperSigningAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "QIS_CURRENTSTATUS_ONPAPER_SIGNING_AUTHORITY_"+qt.getHouseType().getType(), "");
							if(onPaperSigningAuthorityParameter != null){
								String signingAuthority = onPaperSigningAuthorityParameter.getValue();
								String[] signingAuthorities = signingAuthority.split(",");
								for(String str : signingAuthorities){
									if(str.equals(ApplicationConstants.UNDER_SECRETARY) || str.equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
										str = ApplicationConstants.UNDER_SECRETARY;
									}
									if(dataMap.get(str) == null){
										UserGroupType userGroupType = UserGroupType.
												findByFieldName(UserGroupType.class, "type", str, locale.toString());
										Reference ref = UserGroup.findQuestionActor(qt, str, String.valueOf(0), locale.toString());
										if(ref.getId() != null){
											Object[] actor = new Object[32];
											if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) 
													|| ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY)){
												actor[0] = new String(ref.getId().split("#")[3]);
												actor[1] = new String(ref.getId().split("#")[4]);
											}else{
												actor[0] = new String(ref.getId().split("#")[3]);
												actor[1] = new String("");
											}
											actor[3] = new String("");
											actor[6] = new String("");
											actor[27] = userGroupType.getType();
											actor[28] = new String("");
											actor[31] = null;
											dataMap.put(str, actor);
										}
									}
								}
							}
//							if(dataMap.get(ApplicationConstants.SECRETARY) == null 
//									&& qt.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
//								UserGroupType userGroupType = UserGroupType.
//										findByFieldName(UserGroupType.class, "type", ApplicationConstants.SECRETARY, locale.toString());
//								
//								Object[] dataCollection = new Object[32];
//								dataCollection[0] = new String(userGroupType.getName());
//								dataCollection[1] = new String("");
//								dataCollection[3] = new String("");
//								dataCollection[6] = new String("");
//								dataCollection[27] = userGroupType.getType();
//								dataCollection[28] = new String("");
//								dataCollection[31] = null;
//								
//								dataMap.put(ApplicationConstants.SECRETARY, dataCollection);
//							}
//							
//							if(dataMap.get(ApplicationConstants.PRINCIPAL_SECRETARY) == null){
//								UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.PRINCIPAL_SECRETARY, locale.toString());
//								
//								Object[] dataCollection = new Object[32];
//								dataCollection[0] = new String(userGroupType.getName());
//								dataCollection[1] = new String("");
//								dataCollection[3] = new String("");
//								dataCollection[6] = new String("");
//								dataCollection[27] = userGroupType.getType();
//								dataCollection[28] = new String("");
//								dataCollection[31] = null;
//								
//								dataMap.put(ApplicationConstants.PRINCIPAL_SECRETARY, dataCollection);
//							}
							
//							if(dataMap.isEmpty()){
//								for(String val : csptAllwedUserGroupForStatusReportSign.getValue().split(",")){
//								
//									Reference ref = UserGroup.findQuestionActor(qt, val, String.valueOf(0), locale.toString());
//									Object[] actor = new Object[30];
//									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
//										actor[0] = new String(ref.getId().split("#")[3]);
//										actor[1] = new String(ref.getId().split("#")[4]);
//									}else{
//										actor[0] = new String(ref.getId().split("#")[3]);
//										actor[1] = new String("");
//									}
//									actor[3] = new String("");
//									actor[6] = new String("");
//									actor[28] = new String("");
//									
//									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
//										dataMap.put(ApplicationConstants.UNDER_SECRETARY, actor);
//									}else{
//										dataMap.put(val, actor);
//									}
//								}
//							}
	
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
	
	public static Object[] prepareUnstarredYaadiData(final Session session, final List<Question> totalQuestionsInYaadi, String locale) throws ELSException {
		Object[] unstarredYaadiData = null;
		/**** Session Related Data ****/
		HouseType houseType = session.getHouse().getType();
		String houseTypeName = houseType.getName();
		String sessionNumber = session.getNumber().toString();
		String sessionTypeName = session.getType().getSessionType();
		String sessionYear = FormaterUtil.formatNumberNoGrouping(session.getYear(), locale.toString());
		String sessionPlace = session.getPlace().getPlace();
		/**** Questions Data ****/
		String yaadiNumber = null;
		String yaadiLayingDate = null;
		List<Object[]> yaadiQuestions = new ArrayList<Object[]>();
		int count=0;
		for(Question q: totalQuestionsInYaadi) {
			Object[] yaadiQuestion = new Object[15];
			count++;
			yaadiQuestion[0] = FormaterUtil.formatNumberNoGrouping(count, locale);
			yaadiQuestion[1] = q.getId();
			yaadiQuestion[2] = q.getNumber();
			yaadiQuestion[3] = FormaterUtil.formatNumberNoGrouping(q.getNumber(), locale);
			/**** member names ****/
			String allMemberNames = "";	
			String houseTypeType = houseType.getType();		
			String memberNameFormat = null;
			CustomParameter memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_"+houseTypeType.toUpperCase(), "");
			if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
				memberNameFormat = memberNameFormatParameter.getValue();						
			} else {
				memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
			}
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				allMemberNames = q.findAllMemberNamesWithConstituencies(memberNameFormat);
			} else {
				allMemberNames = q.findAllMemberNames(memberNameFormat);
			}
			/**** add below code in case space between title & member name should be removed ****/
			if(allMemberNames!=null && !allMemberNames.isEmpty()) {
				List<Title> titles = Title.findAll(Title.class, "name", ApplicationConstants.ASC, locale);
				if(titles!=null && !titles.isEmpty()) {
					for(Title t: titles) {
						if(t.getName().trim().endsWith(".")) {
							allMemberNames = allMemberNames.replace(t.getName().trim()+" ", t.getName().trim());
						}
					}
				}
			}	
			yaadiQuestion[4] = allMemberNames;
			if(q.getRevisedSubject()!=null && !q.getRevisedSubject().isEmpty()) {
				yaadiQuestion[5] = q.getRevisedSubject();//FormaterUtil.formatNumbersInGivenText(q.getRevisedSubject(), locale);
			} else if(q.getSubject()!=null && !q.getSubject().isEmpty()) {
				yaadiQuestion[5] = q.getSubject();//FormaterUtil.formatNumbersInGivenText(q.getSubject(), locale);
			}
			String content = q.getRevisedQuestionText();
			if(content!=null && !content.isEmpty()) {
				if(content.endsWith("<br><p></p>")) {
					content = content.substring(0, content.length()-11);						
				} else if(content.endsWith("<p></p>")) {
					content = content.substring(0, content.length()-7);					
				}
				//content = FormaterUtil.formatNumbersInGivenText(content, locale);				
			} else {
				content = q.getQuestionText();
				if(content!=null && !content.isEmpty()) {
					if(content.endsWith("<br><p></p>")) {
						content = content.substring(0, content.length()-11);							
					} else if(content.endsWith("<p></p>")) {
						content = content.substring(0, content.length()-7);					
					}
					//content = FormaterUtil.formatNumbersInGivenText(content, locale);					
				}
			}	
			yaadiQuestion[6] = content;
			String answer = q.getAnswer();
			if(answer != null) {
				if(answer.endsWith("<br><p></p>")) {
					answer = answer.substring(0, answer.length()-11);						
				} else if(answer.endsWith("<p></p>")) {
					answer = answer.substring(0, answer.length()-7);					
				}
				//answer = FormaterUtil.formatNumbersInGivenText(answer, locale);
			}				
			yaadiQuestion[7] = answer;
			Member answeringMember = MemberMinister.findMemberHavingMinistryInSession(session, q.getMinistry());
			if(answeringMember != null){
				yaadiQuestion[8] = answeringMember.findNameInGivenFormat(memberNameFormat);
			}
			if(q.getSubDepartment()!=null) {
				yaadiQuestion[9] = q.getSubDepartment().getName();
			} else {
				logger.error("/**** subdepartment has become null for question with id: " + q.getId() + " ****/");
				yaadiQuestion[9] = "";
			}			
			MemberMinister memberMinister = MemberMinister.findMemberMinisterHavingMinistryInSession(session, q.getMinistry());
			if(memberMinister!=null) {
				yaadiQuestion[10] = memberMinister.getDesignation().getName();
			} else {
				yaadiQuestion[10] = "";
			}
			/** referenced question details (later should come through referenced entities) **/
			String questionReferenceText = q.findReferencingDetailsText();
			if(questionReferenceText==null || questionReferenceText.isEmpty()) {
				questionReferenceText = q.getQuestionreferenceText();
			}			
			if(questionReferenceText!=null) {
				//questionReferenceText = FormaterUtil.formatNumbersInGivenText(questionReferenceText, locale);
				yaadiQuestion[11] = questionReferenceText;
			} else {
				yaadiQuestion[11] = "";
			}
			/** answer related dates **/
			SimpleDateFormat answerRelatedDateFormat = null;
			CustomParameter answerRelatedDateFormatParameter = CustomParameter.findByName(CustomParameter.class, "UNSTARRED_YAADI_ANSWER_RELATED_DATE_FORMAT", "");
			if(answerRelatedDateFormatParameter!=null) {
				answerRelatedDateFormat=FormaterUtil.getDateFormatter(answerRelatedDateFormatParameter.getValue(), locale.toString());
			} else {
				answerRelatedDateFormat=FormaterUtil.getDateFormatter(ApplicationConstants.REPORT_DATEFORMAT, locale.toString());
			}
			if(q.getAnswerRequestedDate()!=null) {
				yaadiQuestion[12] = answerRelatedDateFormat.format(q.getAnswerRequestedDate());
			} else {
				yaadiQuestion[12] = "";
			}
			if(q.getAnswerReceivedDate()!=null) {
				yaadiQuestion[13] = answerRelatedDateFormat.format(q.getAnswerReceivedDate());
			} else {
				yaadiQuestion[13] = "";
			}
			if(q.getMinistry()!=null) {
				yaadiQuestion[14] = q.getMinistry().getName();
			} else {
				yaadiQuestion[14] = "";
			}
			if(yaadiNumber==null) {
				if(q.getYaadiNumber()!=null) {
					yaadiNumber = FormaterUtil.formatNumberNoGrouping(q.getYaadiNumber(), locale);
				} else {
					yaadiNumber = "";
				}
			}
			if(yaadiLayingDate==null) {
				if(q.getYaadiLayingDate()!=null) {
					yaadiLayingDate = FormaterUtil.formatDateToString(q.getYaadiLayingDate(), ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_1, locale);
				} else {
					yaadiLayingDate = "";
				}
			}
			//-----------------------------------------------
			yaadiQuestions.add(yaadiQuestion);
		}
		String totalNumberOfYaadiQuestions = FormaterUtil.formatNumberNoGrouping(yaadiQuestions.size(), locale.toString());
		Role role = Role.findByFieldName(Role.class, "type", "QIS_PRINCIPAL_SECRETARY", locale.toString());
		List<User> users = User.findByRole(false, role.getName(), locale.toString());
		//as principal secretary for unstarred question is only one, so user is obviously first element of the list.
		String userName = users.get(0).findFirstLastName();
		
		unstarredYaadiData = new Object[]{yaadiQuestions, totalNumberOfYaadiQuestions, houseTypeName, sessionNumber, sessionTypeName, sessionYear, sessionPlace, userName, yaadiNumber, yaadiLayingDate};
		return unstarredYaadiData;
	}
}