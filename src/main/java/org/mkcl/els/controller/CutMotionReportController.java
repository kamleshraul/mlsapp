package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.ReminderLetter;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
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
@RequestMapping("cutmotion/report")
public class CutMotionReportController extends BaseController{
	
	@RequestMapping(value="/genreport", method=RequestMethod.GET)
	public String getCutMotionReport(HttpServletRequest request, Model model, Locale locale){
		String retVal = "cutmotion/error";
		try{
			genReport(request, model, locale);
			retVal = "cutmotion/reports/" + request.getParameter("reportout");
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "general_error");
		}
		
		return retVal;		
	}
	
	@SuppressWarnings({ "unchecked" })
	private void genReport(HttpServletRequest request, Model model, Locale locale){
			Map<String, String[]> requestMap = request.getParameterMap();
			Boolean havingIN = Boolean.parseBoolean(request.getParameter("havingIN")); //optionally for selective parameter with IN query
			List<Object[]> report = Query.findReport(request.getParameter("report"), requestMap, havingIN);
			if(report != null && !report.isEmpty()){
				Object[] obj = (Object[])report.get(0);
				if(obj != null){
					model.addAttribute("topHeader", obj[0].toString().split(";"));
				}
				List<String> serialNumbers = populateSerialNumbers(report, locale);
				model.addAttribute("serialNumbers", serialNumbers);
				for(Object[] reObjArr: report) {
					for(int i=0; i<reObjArr.length; i++) {
						if(reObjArr[i]!=null && reObjArr[i].toString().startsWith("<span class=\"currency>")) {
							String currencyValue = reObjArr[i].toString().split("<span class=\"currency>")[1].split("</span>")[0];
							String formattedCurrencyValue = FormaterUtil.formatValueForIndianCurrency(currencyValue, locale.toString());
							reObjArr[i] = formattedCurrencyValue;
						}
					}
				}
			}
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			model.addAttribute("report", report);
	}
		
	@RequestMapping(value="/currentstatusreport", method=RequestMethod.GET)
	public String getCurrentStatusReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){

		String strDevice = request.getParameter("device");
		String strReportType = request.getParameter("reportType");
		String strWfid = request.getParameter("wfdId");
		String strMid = request.getParameter("moId");
		
		WorkflowDetails wfd = null;
		if(strWfid != null && !strWfid.isEmpty()){
			wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
			if(wfd != null){
				model.addAttribute("moId", wfd.getDeviceId());
			}
		}
		
		if(strMid != null && !strMid.isEmpty()){
			model.addAttribute("moId", strMid);
		}
		
		model.addAttribute("reportType", strReportType);
		if(strDevice != null && !strDevice.isEmpty()){		
			model.addAttribute("device", strDevice);
		}

		response.setContentType("text/html; charset=utf-8");
		return "cutmotion/reports/statusreport";
	}
	
	@RequestMapping(value="/{moId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("moId") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
				
		response.setContentType("text/html; charset=utf-8");		
		return CutMotionReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
	}
	
	//TODO: yaadi report for all departments through single screen
	@RequestMapping(value = "/yaadi_report/init", method = RequestMethod.GET)
	public String getYaadiReportInit(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String yaadiInitPage = "workflow/myTasks/error";
		
		CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		HouseType houseType = null;
		String strHouseType = request.getParameter("houseType");
		if(strHouseType!=null && !strHouseType.isEmpty()) {
			houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			if(houseType==null || houseType.getId()==null) {
				if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
					if(csptServer.getValue().equals("TOMCAT")){
						try {
							strHouseType = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							logger.error("request parameter for HouseType has invalid value");
							model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
							return yaadiInitPage;
						}					
					}
				}
				houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, locale.toString());
			}
			if(houseType==null || houseType.getId()==null) {
				houseType=HouseType.findById(HouseType.class,Long.parseLong(strHouseType));
			}
			if(houseType==null || houseType.getId()==null) { //still not found then error
				logger.error("request parameter for HouseType has invalid value");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
				return yaadiInitPage;
			}
			model.addAttribute("houseType", houseType.getType());
			model.addAttribute("houseTypeId", houseType.getId());
		} else {
			logger.error("request parameter for HouseType is not set");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NOT_SET");
			return yaadiInitPage;
		}
		
		SessionType sessionType = null;
		String strSessionType = request.getParameter("sessionType");
		if(strSessionType!=null && !strSessionType.isEmpty()) {			
			if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
				if(csptServer.getValue().equals("TOMCAT")){
					try {
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error("request parameter for SessionType has invalid value");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return yaadiInitPage;
					}					
				}
				sessionType=SessionType.findByFieldName(SessionType.class,"sessionType",strSessionType, locale.toString());
			}
			if(sessionType==null || sessionType.getId()==null) {
				sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			}
			if(sessionType==null || sessionType.getId()==null) { //still not found then error
				logger.error("request parameter for SessionType has invalid value");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
				return yaadiInitPage;
			}
			model.addAttribute("sessionType", sessionType.getId());
			model.addAttribute("sessionTypeName", sessionType.getSessionType());
		} else {
			logger.error("request parameter for SessionType is not set");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NOT_SET");
			return yaadiInitPage;
		}
		
		Integer sessionYear = null;
		String strSessionYear = request.getParameter("sessionYear");
		if(strSessionYear!=null && !strSessionYear.isEmpty()) {			
			if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
				if(csptServer.getValue().equals("TOMCAT")){
					try {
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error("request parameter for SessionYear has invalid value");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return yaadiInitPage;
					}					
				}
				try {
					sessionYear=Integer.parseInt(strSessionYear);
				} catch (Exception e) {
					logger.error("request parameter for SessionYear has invalid value");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return yaadiInitPage;
				}
			}
			if(sessionYear==null) {
				logger.error("request parameter for SessionYear has invalid value");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
				return yaadiInitPage;
			}
			model.addAttribute("sessionYear", sessionYear);
			model.addAttribute("formattedSessionYear", strSessionYear);
		} else {
			logger.error("request parameter for SessionYear is not set");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NOT_SET");
			return yaadiInitPage;
		}
		
		DeviceType deviceType = null;
		String strDeviceType = request.getParameter("deviceType");
		if(strDeviceType!=null && !strDeviceType.isEmpty()) {
			if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
				if(csptServer.getValue().equals("TOMCAT")){
					try {
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error("request parameter for DeviceType has invalid value");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return yaadiInitPage;
					}					
				}
				deviceType=DeviceType.findByFieldName(DeviceType.class,"name",strDeviceType, locale.toString());
			}
			if(deviceType==null || deviceType.getId()==null) {
				deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
			}
			if(deviceType==null || deviceType.getId()==null) { //still not found then error
				logger.error("request parameter for DeviceType has invalid value");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
				return yaadiInitPage;
			}
			model.addAttribute("deviceType", deviceType.getId());
			model.addAttribute("deviceTypeName", deviceType.getName());
		} else {
			logger.error("request parameter for DeviceType is not set");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NOT_SET");
			return yaadiInitPage;
		}
		
		Session session = null;
		try {
			session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			model.addAttribute("sessionId", session.getId());
			
			//populate cmois departments for session & devicetype
			List<MasterVO> allYaadiDepartmentDetails = CutMotion.findAllYaadiDepartmentDetails(session, deviceType, locale.toString());
			if(allYaadiDepartmentDetails==null || allYaadiDepartmentDetails.isEmpty()) {
				logger.warn("No department having cmois yaadi for given session and devicetype");
				model.addAttribute("errorcode", "NO_YAADI_FOUND");
				return yaadiInitPage;
			}
			model.addAttribute("allYaadiDepartmentDetails", allYaadiDepartmentDetails);
			
			model.addAttribute("locale", locale.toString());
			yaadiInitPage = "cutmotion/reports/yaadi_report_init";
			
		} catch (ELSException e) {
			logger.error("session not found with given parameters");
			model.addAttribute("errorcode", "SESSION_NOT_FOUND");
			return yaadiInitPage;
		}		
		
		return yaadiInitPage;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/yaadi_report", method=RequestMethod.GET)
	public @ResponseBody void generateYaadiReport(HttpServletRequest request, HttpServletResponse response, Locale locale){
		//
		String strWfid = request.getParameter("workflowId");		
		WorkflowDetails wfd = null;
		if(strWfid != null && !strWfid.isEmpty()){
			wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
			if(wfd != null){
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.putAll(request.getParameterMap());
				
				CutMotion cutMotion = CutMotion.findById(CutMotion.class, Long.parseLong(wfd.getDeviceId()));
				if(cutMotion!=null) {
					String houseType = cutMotion.getHouseType().getType();
					parametersMap.put("houseType", new String[] {houseType});
					
					String sessionYear = cutMotion.getSession().getYear().toString();
					parametersMap.put("sessionYear", new String[] {sessionYear});
					
					String sessionType = cutMotion.getSession().getType().getId().toString();
					parametersMap.put("sessionType", new String[] {sessionType});
					
					String sessionId = cutMotion.getSession().getId().toString();
					parametersMap.put("sessionId", new String[] {sessionId});
					
					String subDepartment = cutMotion.getSubDepartment().getId().toString();
					if(cutMotion.getYaadiSubDepartment()!=null) {
						subDepartment = cutMotion.getYaadiSubDepartment().getId().toString();
					}
					parametersMap.put("subDepartment", new String[] {subDepartment});
					
					String cutMotionType = cutMotion.getDeviceType().getId().toString();
					parametersMap.put("cutMotionType", new String[] {cutMotionType});
					
					generateTabularFOPReport(request, response, parametersMap, locale);
				}
			}
		} else {
			generateTabularFOPReport(request, response, locale);
		}	
	}
	
	@RequestMapping(value ="/generateIntimationLetter", method = RequestMethod.GET)
	private void generateIntimationLetter(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		try{
			String strId = request.getParameter("motionId");
			String strWorkflowId = request.getParameter("workflowDetailId");
			WorkflowDetails workflowDetails = null;
			String strReportFormat = request.getParameter("outputFormat");
			if(strWorkflowId != null && !strWorkflowId.isEmpty()){
				workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowId));
				if(workflowDetails != null){
					strId = workflowDetails.getDeviceId();
					if(strReportFormat==null || strReportFormat.isEmpty()) {
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
								|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
							strReportFormat = "PDF";
						} else {
							strReportFormat = "WORD";
						}
					}
				} else {
					strReportFormat = "WORD";
				}
			} else {
				strReportFormat = "WORD";
			}
			
			if(strId != null && !strId.isEmpty()){
				CutMotion motion = CutMotion.findById(CutMotion.class, Long.parseLong(strId));
				if(motion!=null) {
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("motionId", new String[]{strId});
					
					@SuppressWarnings("rawtypes")
					List reportData = Query.findReport("CUTMOTION_INTIMATION_LETTER", parameters);
					
					reportFile = generateReportUsingFOP(new Object[] {reportData}, "cutmotion_intimation_letter_template", strReportFormat, "cutmotion_intimationletter",locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
					
					model.addAttribute("info", "general_info");
					//retVal = "cutmotion/info";
				} else {
					isError = true;
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "cutmotion.intimationLetter.noDeviceFound", locale.toString());
				}				
			} else {
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "cutmotion.intimationLetter.noDeviceFound", locale.toString());
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
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}	
	
	@RequestMapping(value="/generateReminderLetter", method=RequestMethod.GET)
	public @ResponseBody void generateReminderLetter(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		File reportFile = null;
		
		HouseType houseType = null;
		String strHouseType = request.getParameter("houseType");
		if(strHouseType!=null && !strHouseType.isEmpty()) {
			houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			if(houseType==null || houseType.getId()==null) {
				CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
					if(csptServer.getValue().equals("TOMCAT")){
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");					
					}
				}
				houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, locale.toString());
			}
			if(houseType==null || houseType.getId()==null) {
				houseType=HouseType.findById(HouseType.class,Long.parseLong(strHouseType));
			}
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> requestMap = request.getParameterMap();
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = Query.findReport(request.getParameter("reportQuery"), requestMap, true);
		
    	if(resultList!=null && !resultList.isEmpty()) {
    		String isDepartmentLogin = request.getParameter("isDepartmentLogin");
    		if(isDepartmentLogin==null || isDepartmentLogin.isEmpty()) {
    			isDepartmentLogin = "NO";
    		} 
    		String countOfCutMotionsInReminder = FormaterUtil.formatNumberNoGrouping(resultList.size(), locale.toString());
    		CutMotion latestCutMotion = null;
    		DeviceType deviceType = null;
    		SubDepartment subDepartment = null;
    		String departmentName = "";
    		String reminderNumberStartLimitingDate = "";
    		String reminderNumberEndLimitingDate = "";
    		
    		Object[] latestResultUnit = resultList.get(resultList.size()-1);
    		latestCutMotion = CutMotion.findById(CutMotion.class, Long.parseLong(latestResultUnit[10].toString()));
    		deviceType = latestCutMotion.getDeviceType();
			subDepartment = latestCutMotion.getSubDepartment();
			//departmentName = latestResultUnit[2].toString();
			departmentName = subDepartment.getMinistryDisplayName();
			Session qSession = latestCutMotion.getSession();
			
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				House correspondingAssemblyHouse = Session.findCorrespondingAssemblyHouseForCouncilSession(qSession);
				Date houseStartDate = correspondingAssemblyHouse.getFirstDate();
				reminderNumberStartLimitingDate = FormaterUtil.formatDateToString(houseStartDate, ApplicationConstants.DB_DATEFORMAT);
				Date houseEndDate = correspondingAssemblyHouse.getLastDate();
				reminderNumberEndLimitingDate = FormaterUtil.formatDateToString(houseEndDate, ApplicationConstants.DB_DATEFORMAT);
			} else {
				Date houseStartDate = qSession.getHouse().getFirstDate();
				reminderNumberStartLimitingDate = FormaterUtil.formatDateToString(houseStartDate, ApplicationConstants.DB_DATEFORMAT);
				Date houseEndDate = qSession.getHouse().getLastDate();
				reminderNumberEndLimitingDate = FormaterUtil.formatDateToString(houseEndDate, ApplicationConstants.DB_DATEFORMAT);
			}
    		
    		StringBuffer deviceIds = new StringBuffer("");
    		List<String> serialNumbers = populateSerialNumbers(resultList, locale);
    		List<String> expectedAnswerReceivingDates = new ArrayList<String>();
    		for(int i=0; i<resultList.size(); i++) {
    			//include each cutmotion id in 'deviceIds' to be saved in domain of this reminder letter
    			Object[] resultUnit = resultList.get(i);
    			deviceIds.append(resultUnit[10].toString());
    			if(i!=resultList.size()-1) {
    				deviceIds.append(",");
    			}      
    			
    			if(resultUnit[5]!=null && !resultUnit[5].toString().isEmpty()) {
    				Date answerRequestedDate = FormaterUtil.formatStringToDate(resultUnit[5].toString(), ApplicationConstants.DB_DATETIME_FORMAT);
					Integer expectedAnswerReceivingDateDuration = 30;
					CustomParameter expectedAnswerReceivingDateDurationCP = CustomParameter.findByName(CustomParameter.class, "CUTMOTION_EXPECTED_ANSWER_RECEIVING_DATE_DURATION", "");
					if(expectedAnswerReceivingDateDurationCP!=null && expectedAnswerReceivingDateDurationCP.getValue()!=null) {
						expectedAnswerReceivingDateDuration = Integer.parseInt(expectedAnswerReceivingDateDurationCP.getValue());
					}
					Date expectedAnswerReceivingDate = Holiday.getNextWorkingDateFrom(answerRequestedDate, expectedAnswerReceivingDateDuration, locale.toString());
					expectedAnswerReceivingDates.add(FormaterUtil.formatDateToString(expectedAnswerReceivingDate, ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_3, locale.toString()));
    			} else {
    				expectedAnswerReceivingDates.add("");
    			}
    		}    
    		
    		String reminderLetterNumber = "";
    		Map<String, String> reminderLetterIdentifiers = new HashMap<String, String>();
    		reminderLetterIdentifiers.put("houseType", houseType.getType());
    		reminderLetterIdentifiers.put("deviceType", deviceType.getType());
    		reminderLetterIdentifiers.put("reminderFor", ApplicationConstants.REMINDER_FOR_REPLY_FROM_DEPARTMENT);
    		reminderLetterIdentifiers.put("reminderTo", subDepartment.getId().toString());
    		reminderLetterIdentifiers.put("reminderNumberStartLimitingDate", reminderNumberStartLimitingDate);
    		reminderLetterIdentifiers.put("reminderNumberEndLimitingDate", reminderNumberEndLimitingDate);
    		reminderLetterIdentifiers.put("locale", locale.toString());
    		ReminderLetter latestReminderLetter = ReminderLetter.findLatestByFieldNames(reminderLetterIdentifiers, locale.toString());
    		//boolean isReminderLetterAlreadyGenerated = false;
    		boolean isRequiredToSend = false;
    		String isRequiredToSendStr = request.getParameter("isRequiredToSend");
    		if(isRequiredToSendStr!=null) {
    			isRequiredToSend = Boolean.parseBoolean(isRequiredToSendStr);
    		}
    		if(latestReminderLetter!=null) {
    			if(!isDepartmentLogin.equals("YES")) {
    				if(isRequiredToSend) {
    					reminderLetterNumber = FormaterUtil.formatNumberNoGrouping((Integer.parseInt(latestReminderLetter.getReminderNumber())+1), locale.toString());
    				} else {
    					reminderLetterNumber = latestReminderLetter.getReminderNumber();
    				}    				
    			} else {
    				reminderLetterNumber = latestReminderLetter.getReminderNumber();
    			}    			
    		} else {
    			reminderLetterNumber = FormaterUtil.formatNumberNoGrouping(1, locale.toString());
    		}
    		
    		String deviceTypeName = "";
    		if(deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {
    			deviceTypeName = ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY_DISPLAYNAME;
    		} else {
    			deviceTypeName = ApplicationConstants.MOTIONS_CUTMOTION_SUPPPLEMENTARY_DISPLAYNAME;
    		}
    		reportFile = generateReportUsingFOP(new Object[] {resultList, expectedAnswerReceivingDates, serialNumbers, reminderLetterNumber, isDepartmentLogin, countOfCutMotionsInReminder}, deviceTypeName+"_reminder_letter_template", request.getParameter("outputFormat"), deviceTypeName+"_reminder_letter", locale.toString());
    		if(reportFile!=null) {
    			System.out.println("Report generated successfully in " + request.getParameter("outputFormat") + " format!");
    			openOrSaveReportFileFromBrowser(response, reportFile, request.getParameter("outputFormat"));  
    			
    			ReminderLetter currentReminderLetter = null;
    			if(!isDepartmentLogin.equals("YES")) {
    				if(isRequiredToSend) {
    					/** SAVE CURRENT REMINDER LETTER ENTRY **/
        				currentReminderLetter = new ReminderLetter();
            			currentReminderLetter.setHouseType(houseType.getType());
            			currentReminderLetter.setDeviceType(deviceType.getType());
            			currentReminderLetter.setDeviceIds(deviceIds.toString());
            			currentReminderLetter.setReminderFor(ApplicationConstants.REMINDER_FOR_REPLY_FROM_DEPARTMENT);
            			currentReminderLetter.setReminderTo(subDepartment.getId().toString());
            			currentReminderLetter.setReminderNumberStartLimitingDate(reminderNumberStartLimitingDate);
            			currentReminderLetter.setReminderNumberEndLimitingDate(reminderNumberEndLimitingDate);
            			currentReminderLetter.setReminderNumber(reminderLetterNumber);
            			currentReminderLetter.setReminderDate(new Date());
            			currentReminderLetter.setStatus(ApplicationConstants.REMINDER_LETTER_DISPATCHED_STATUS);
            			currentReminderLetter.setGeneratedBy(this.getCurrentUser().getActualUsername());
            			currentReminderLetter.setLocale(locale.toString());
            			currentReminderLetter.persist();
    				}  			
    			} else {
    				if(latestReminderLetter!=null && latestReminderLetter.getId()!=null
    						&& latestReminderLetter.getStatus().equals(ApplicationConstants.REMINDER_LETTER_DISPATCHED_STATUS)) {
    					/** ACKNOWLEDGE CURRENT REMINDER LETTER ENTRY **/
    					latestReminderLetter.setStatus(ApplicationConstants.REMINDER_LETTER_ACKNOWLEDGED_STATUS);
    					if(latestReminderLetter.getReminderAcknowledgementDate()==null) {
    						latestReminderLetter.setReminderAcknowledgementDate(new Date());
    					}    					
    					latestReminderLetter.merge();
    				} 				
    			}
    			
    			/**** SEND NOTIFICATION TO DEPARTMENT USERS IF REMINDER LETTER IS GENERATED FROM CUTMOTIONS (D-3) BRANCH AT VIDHAN BHAVAN ****/
    			if(!isDepartmentLogin.equals("YES")) {
					if(isRequiredToSend) {
						/** find co-ordination department user for sending notification **/
        				String departmentCoordinationUsername = "";
    	    			Reference actorAtDepartmentLevel = WorkflowConfig.findActorVOAtGivenLevel(latestCutMotion, latestCutMotion.getStatus(), ApplicationConstants.DEPARTMENT, 9, locale.toString());
    					if(actorAtDepartmentLevel!=null) {
    						String userAtDepartmentLevel = actorAtDepartmentLevel.getId();
        					departmentCoordinationUsername = userAtDepartmentLevel.split("#")[0];
            				NotificationController.sendReminderLetterForReplyNotReceivedFromDepartmentUsers(houseType, deviceType, departmentCoordinationUsername, departmentName, locale.toString());
            				
            				/** UPDATE RECEIVERS IN CURRENT REMINDER LETTER ENTRY **/
            				currentReminderLetter.setReceivers(departmentCoordinationUsername);
                			currentReminderLetter.merge();
    					}
					}        				    					
    			}    			
    		}
    	}		
	}

}


class CutMotionReportHelper{
	private static Logger logger = LoggerFactory.getLogger(CutMotionReportHelper.class);
	
	public static String getCurrentStatusReportData(Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "cutmotion/error";
		try{
			String strDevice = request.getParameter("device"); 
			if(strDevice != null && !strDevice.isEmpty()){
				CutMotion qt = CutMotion.findById(CutMotion.class, id);
				@SuppressWarnings("rawtypes")
				List report = generatetCurrentStatusReport(qt, strDevice, locale.toString());				
				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();
				if(report != null && !report.isEmpty()){
					
					List<User> users = User.findByRole(false, "CMOIS_PRINCIPAL_SECRETARY", locale.toString());
					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
	
					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)? "CMOIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "CMOIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
					if(csptAllwedUserGroupForStatusReportSign != null){
						if(csptAllwedUserGroupForStatusReportSign.getValue() != null && !csptAllwedUserGroupForStatusReportSign.getValue().isEmpty()){
							Object[] lastObject = (Object[]) report.get(report.size()-1); 
							for(Object o : report){
								Object[] objx = (Object[])o;
	
								if(objx[23] != null && !objx[23].toString().isEmpty()){
									if(csptAllwedUserGroupForStatusReportSign.getValue().contains(objx[23].toString())){
										
										UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", objx[23].toString(), locale.toString());
																				
										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
											if(dataMap.get(ApplicationConstants.UNDER_SECRETARY) != null){
												if(objx != null){
													if(objx[6] != null && objx[6].toString().length() > 0){
														dataMap.put(ApplicationConstants.UNDER_SECRETARY, objx);
													}else{
														Object[] tempObj = dataMap.get(ApplicationConstants.UNDER_SECRETARY);
														tempObj[24] = objx[24];
														
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
														tempObj[24] = objx[24];
														
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
							
							WorkflowConfig wfConfig = null;
							CustomParameter csptCurrentStatusAllowedBeforeApproval = CustomParameter.findByName(CustomParameter.class, qt.getDeviceType().getType().toUpperCase()+"_"+qt.getHouseType().getType().toUpperCase()+"_CURRENT_STATUS_REPORT_ALLOWED_BEFORE_APPROVAL", "");
							if(csptCurrentStatusAllowedBeforeApproval!=null && csptCurrentStatusAllowedBeforeApproval.getValue()!=null
									&& csptCurrentStatusAllowedBeforeApproval.getValue().equals("YES")) {
								
								wfConfig = WorkflowConfig.getLatest(qt, ApplicationConstants.CUTMOTION_RECOMMEND_ADMISSION, locale.toString());
								model.addAttribute("currentStatusReportAllowedBeforeApproval", "YES");
								
							} else {
								wfConfig = WorkflowConfig.getLatest(qt, qt.getInternalStatus().getType(), locale.toString());
								model.addAttribute("currentStatusReportAllowedBeforeApproval", "NO");
							}							
							
							//Following block is added for solving the issue of question drafts where in if there exist a draft and later the question is pending
							// at the specific actor, the last remark is displayed
							List<WorkflowActor> wfActors = wfConfig.getWorkflowactors();
							List<WorkflowActor> distinctActors = new ArrayList<WorkflowActor>();
							for(WorkflowActor wf : wfActors){
								UserGroupType userGroupType = wf.getUserGroupType();
								Boolean elementPresent = false;
								for(WorkflowActor wf1 : distinctActors){
									UserGroupType userGroupType1 = wf1.getUserGroupType();
									if(userGroupType.getType().equals(userGroupType1.getType())){
										elementPresent = true;
										break;
									}
								}
								if(!elementPresent){
									distinctActors.add(wf);
								}
							}
							Integer level = null;
							WorkflowDetails  wfDetails = WorkflowDetails.findCurrentWorkflowDetail(qt);
							for(WorkflowActor wf : distinctActors){
								UserGroupType userGroupType = wf.getUserGroupType();
								if(userGroupType.getType().equals(lastObject[27])){
									if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
										for(WorkflowActor wf1 : distinctActors){
											UserGroupType ugt = wf1.getUserGroupType();
											if(ugt.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
												level = wf1.getLevel();
											}
										}
									}else{
										level = wf.getLevel();
									}
								}
									
								
								if(level != null && wf.getLevel()>level){
									if(dataMap.containsKey(userGroupType.getType())
											&&
										(wfDetails!= null && 
										Integer.parseInt(wfDetails.getAssigneeLevel())>=level)){
										Object[] tempObj = dataMap.get(userGroupType.getType());
										tempObj[24] = "";
										tempObj[6] = "";
										dataMap.put(userGroupType.getType(), tempObj);
									}
								}
							}
							CustomParameter onPaperSigningAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "CMOIS_CURRENTSTATUS_ONPAPER_SIGNING_AUTHORITY_"+qt.getHouseType().getType(), "");
							if(onPaperSigningAuthorityParameter != null){
								String signingAuthority = onPaperSigningAuthorityParameter.getValue();
								String[] signingAuthorities = signingAuthority.split(",");
								for(String str : signingAuthorities){
									String authority = str;
									if(str.equals(ApplicationConstants.UNDER_SECRETARY) || str.equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
										str = ApplicationConstants.UNDER_SECRETARY;
									}
									if(dataMap.get(str) == null){
										UserGroupType userGroupType = null;
										Reference ref = null;
										if(authority.equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
											 userGroupType = UserGroupType.
													findByFieldName(UserGroupType.class, "type", authority, locale.toString());
											 ref = UserGroup.findCutMotionActor(qt, authority, String.valueOf(0), locale.toString());
										}else{
											 userGroupType = UserGroupType.
													findByFieldName(UserGroupType.class, "type", str, locale.toString());
											 ref = UserGroup.findCutMotionActor(qt, str, String.valueOf(0), locale.toString());
										}
										
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
											actor[23] = userGroupType.getType();
											actor[24] = new String("");
											actor[27] = null;
											dataMap.put(str, actor);
										}
									}
								}
							}
							model.addAttribute("data", dataMap);
							model.addAttribute("formatData", report.get(report.size()-1));
						}
					}
	
					page = (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "cutmotion/reports/statusreportlowerhouse": "cutmotion/reports/statusreportupperhouse";
				}		
	
				model.addAttribute("moid", qt.getId());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	public static List generatetCurrentStatusReport(final CutMotion motion, final String device, final String locale){
		CustomParameter memberNameFormatParameter = null;
		if(motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
		} else if(motion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_UPPERHOUSE", "");
		}
		String support = "";
		if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
			support = motion.findAllMemberNames(memberNameFormatParameter.getValue());
		} else {
			support = motion.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
		}		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("id",new String[]{motion.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("CMOIS_CURRENTSTATUS_REPORT", parameters);
		for(Object o : list){
			Object[] data = (Object[]) o;
			String details = ((data[4] != null)? data[4].toString():"-");
			String mainTitle = ((data[5] != null)? data[5].toString():"-");
			String remarks = ((data[6] != null)? data[6].toString():"-");
			
			((Object[])o)[15] = support;
			((Object[])o)[4] = FormaterUtil.formatNumbersInGivenText(details, locale);
			((Object[])o)[5] = FormaterUtil.formatNumbersInGivenText(mainTitle, locale);
			((Object[])o)[6] = FormaterUtil.formatNumbersInGivenText(remarks, locale);
			data = null;
		}

		return list;  
	}
	
}