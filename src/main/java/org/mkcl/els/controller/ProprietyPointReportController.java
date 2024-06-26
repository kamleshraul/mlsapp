package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.xmlvo.QuestionYaadiSuchiXmlVO;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionDates;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.domain.ballot.ProprietyPointBallot;
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
@RequestMapping("proprietypoint/report")
public class ProprietyPointReportController extends BaseController{
	
	
	@RequestMapping(value ="/generateIntimationLetter", method = RequestMethod.GET)
	private void generateIntimationLetter(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			//String strId = request.getParameter("motionId");;
			String ppNumber = null;
			String strWorkflowId = request.getParameter("workflowDetailId");
			WorkflowDetails workflowDetails = null;
			String strReportFormat = request.getParameter("outputFormat");
			if(strWorkflowId != null && !strWorkflowId.isEmpty()){
				workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowId));
				if(workflowDetails != null){
					ppNumber = workflowDetails.getDeviceId();
					if(strReportFormat==null || strReportFormat.isEmpty()) {
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
								|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)
								|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.MEMBER)
								) {
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
			String strCopyType = request.getParameter("copyType");
			Long workflowDetailCount = (long) 0;
			Boolean isResendRevisedMotionTextWorkflow = false;
			if(ppNumber != null && !ppNumber.isEmpty()){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("motionId", new String[]{ppNumber});
				
				@SuppressWarnings("rawtypes")
				List reportData = Query.findReport("POINTS_OF_PROPRIETY_INTIMATION_LETTER", parameters);	
				String templateName = "pointsofpropriety_intimation_letter_template";
				
				
				File reportFile = null;
				
				
				
				reportFile = generateReportUsingFOP(new Object[] {reportData}, templateName, strReportFormat, "pointsofpropriety_intimation_letter",locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
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
		
		return "proprietypoint/reports/"+request.getParameter("reportout");		
	}
	
	@RequestMapping(value="/currentstatusreport", method=RequestMethod.GET)
	public String getCurrentStatusReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){

		String strDevice = request.getParameter("device");
		String strReportType = request.getParameter("reportType");
		String strWfid = request.getParameter("wfdId");
		String strDeviceId = request.getParameter("deviceId");
		
		WorkflowDetails wfd = null;
		if(strWfid != null && !strWfid.isEmpty()){
			wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
			if(wfd != null){
				model.addAttribute("deviceId", wfd.getDeviceId());
			}
		}
		
		if(strDeviceId != null && !strDeviceId.isEmpty()){
			model.addAttribute("deviceId", strDeviceId);
		}
		
		model.addAttribute("reportType", strReportType);
		if(strDevice != null && !strDevice.isEmpty()){		
			model.addAttribute("device", strDevice);
		}

		response.setContentType("text/html; charset=utf-8");
		return "proprietypoint/reports/statusreport";
	}
	
	@RequestMapping(value="/{deviceId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("deviceId") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
				
		response.setContentType("text/html; charset=utf-8");		
		return ProprietyPointReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
	}
	
	@RequestMapping(value="/tobeadmitted" ,method=RequestMethod.GET)
	public @ResponseBody void generateToBeAdmittedReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		@SuppressWarnings("unchecked")
		Map<String, String[]> requestMap = request.getParameterMap();
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.tobeadmitted_report.parameterNotSet", locale.toString());						
		} else {
			//requestMap.put("sessionId", new String[] {sessionId});
			//requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List toBeAdmittedProperietyPoints = Query.findReport(reportQueryName, requestMap);
			try {
				reportFile = generateReportUsingFOP(new Object[] {toBeAdmittedProperietyPoints}, "prois_tobeadmitted_template", "WORD", "prois_tobeadmitted_report", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.tobeadmitted_report.someErrorOccurred", locale.toString());
			}
			System.out.println("PROIS To Be Admitted Report generated successfully in WORD format!");

			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");			
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
	
	@RequestMapping(value="/toberejected" ,method=RequestMethod.GET)
	public @ResponseBody void generateToBeRejectedReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.toberejected_report.parameterNotSet", locale.toString());						
		} else {
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List toBeRejectedProperietyPoints = Query.findReport(reportQueryName, requestMap);
			try {
				reportFile = generateReportUsingFOP(new Object[] {toBeRejectedProperietyPoints}, "prois_toberejected_template", "WORD", "prois_toberejected_report", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.toberejected_report.someErrorOccurred", locale.toString());
			}
			System.out.println("PROIS To Be Rejected Report generated successfully in WORD format!");

			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");			
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
	
	@RequestMapping(value="/submitteddevices" ,method=RequestMethod.GET)
	public @ResponseBody void generateSubmittedDevicesReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String proprietyPointDateStr = request.getParameter("proprietyPointDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		String houseType = request.getParameter("houseType");
		if(sessionId==null || sessionId.isEmpty() || proprietyPointDateStr==null || proprietyPointDateStr.isEmpty() || reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.submitted_devices_report.parameterNotSet", locale.toString());						
		} else {
			Date proprietyPointDate = FormaterUtil.formatStringToDate(proprietyPointDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("proprietyPointDate", new String[] {FormaterUtil.formatDateToString(proprietyPointDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List submittedDevices = Query.findReport(reportQueryName, requestMap, true);
			try {
				  reportFile = generateReportUsingFOP(new Object[] {submittedDevices}, "prois_submitted_devices_template_"+houseType, "WORD", "prois_submitted_devices", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.submitted_devices_report.someErrorOccurred", locale.toString());
			}
			System.out.println("PROIS Submitted Devices Report generated successfully in WORD format!");

			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
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
	
	@RequestMapping(value="/admitteddevices" ,method=RequestMethod.GET)
	public @ResponseBody void generateAdmittedDevicesReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String proprietyPointDateStr = request.getParameter("proprietyPointDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(sessionId==null || sessionId.isEmpty() || proprietyPointDateStr==null || proprietyPointDateStr.isEmpty() || reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.admitted_devices_report.parameterNotSet", locale.toString());						
		} else {
			Date proprietyPointDate = FormaterUtil.formatStringToDate(proprietyPointDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("proprietyPointDate", new String[] {FormaterUtil.formatDateToString(proprietyPointDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List admittedDevices = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {admittedDevices}, "prois_admitted_devices_template", "WORD", "prois_admitted_devices", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.admitted_devices_report.someErrorOccurred", locale.toString());
			}
			System.out.println("PROIS Admitted Devices Report generated successfully in WORD format!");

			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");			
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
	
	@RequestMapping(value="/rejecteddevices" ,method=RequestMethod.GET)
	public @ResponseBody void generateRejectedDevicesReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String proprietyPointDateStr = request.getParameter("proprietyPointDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(sessionId==null || sessionId.isEmpty() || proprietyPointDateStr==null || proprietyPointDateStr.isEmpty() || reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.rejected_devices_report.parameterNotSet", locale.toString());						
		} else {
			Date proprietyPointDate = FormaterUtil.formatStringToDate(proprietyPointDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("proprietyPointDate", new String[] {FormaterUtil.formatDateToString(proprietyPointDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List rejectedDevices = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {rejectedDevices}, "prois_rejected_devices_template", "WORD", "prois_rejected_devices", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.rejected_devices_report.someErrorOccurred", locale.toString());
			}
			System.out.println("PROIS Rejected Devices Report generated successfully in WORD format!");

			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");			
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
	
	@RequestMapping(value="/register" ,method=RequestMethod.GET)
	public @ResponseBody void generateRegisterReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String proprietyPointDateStr = request.getParameter("proprietyPointDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(sessionId==null || sessionId.isEmpty() || reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.register_report.parameterNotSet", locale.toString());						
		} else {
			Date proprietyPointDate = null;
			if(proprietyPointDateStr!=null && !proprietyPointDateStr.isEmpty()) {
				proprietyPointDate = FormaterUtil.formatStringToDate(proprietyPointDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				requestMap.put("proprietyPointDate", new String[] {FormaterUtil.formatDateToString(proprietyPointDate, ApplicationConstants.DB_DATEFORMAT)});
			}		
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List registerMotions = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {registerMotions}, "prois_register_template", "WORD", "prois_register", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.register_report.someErrorOccurred", locale.toString());
			}
			System.out.println("PROIS Register Report generated successfully in WORD format!");

			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");			
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
	
	@RequestMapping(value="/ppballotfop",method=RequestMethod.GET)
	public @ResponseBody void generateProprietyPointBallotReport(final HttpServletRequest request, HttpServletResponse response,final Locale locale, final ModelMap model) throws Exception {
		try {
			ActivityLog.logActivity(request,locale.toString());
		} catch (Exception e) {
			logger.error("error",e);
		}
		
		File reportFile = null;
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		String answeringDate = request.getParameter("answeringDate");
		String sessionId = request.getParameter("sessionId");
		String deviceTypeId =request.getParameter("deviceTypeId");
		String queryName = "PROIS_ASSEMBLY_BALLOT_VIEW_REPORT";
		String fileName = null;
	
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("answeringDate", new String[] {answeringDate});
			queryParameters.put("deviceTypeId", new String[] {deviceTypeId});
			queryParameters.put("sessionId", new String[] {sessionId});
			queryParameters.put("locale", new String[] {locale.toString()});
			
			List reportData = Query.findReport(queryName, queryParameters,false);
			
			if(reportData != null && !reportData.isEmpty()) {
				List<String> serialNumbers = new ArrayList<String>();
				for(int i=1; i<=reportData.size(); i++) {
					serialNumbers.add(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
				}
				
				List<String> localisedContent = new ArrayList<String>();
				if(reportData.get(0) != null) {
					Object[] obj = (Object[]) reportData.get(0);
					for(String i : obj[1].toString().split(";")) {
						localisedContent.add(i);
					}
					for(String j : obj[2].toString().split(";")) {
						localisedContent.add(j);
					}
				}
				
				if(!isError) {
					
					reportFile = generateReportUsingFOP(new Object[] {reportData,serialNumbers,localisedContent,ApplicationConstants.PROPRIETY_POINT}, "propriety_point_ballot_template", "WORD", "propriety_point_ballot_report", locale.toString());
					
					if(reportFile!=null) {
						System.out.println("Report generated successfully in WORD format!");
						openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
					}
				}
				
			}  else {
				//error
				
			}
		
	}
	
	@RequestMapping(value="/ppunballotfop",method=RequestMethod.GET)
	public @ResponseBody void generateProprietyPointUnballotReport(final HttpServletRequest request, HttpServletResponse response,final Locale locale, final ModelMap model) throws Exception {
		try {
			ActivityLog.logActivity(request,locale.toString());
		} catch (Exception e) {
			logger.error("error",e);
		}
		
		File reportFile = null;
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		String answeringDate = request.getParameter("answeringDate");
		String sessionId = request.getParameter("sessionId");
		String deviceTypeId =request.getParameter("deviceTypeId");
		String fileName = null;
		String reportFormat= "WORD";
		
		Session session  = Session.findById(Session.class, Long.parseLong(sessionId));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));
		Date answeringDate2 = FormaterUtil.formatStringToDate(answeringDate, ApplicationConstants.DB_DATEFORMAT);
		
		List<DeviceVO> unBallotedVOs = new ArrayList<DeviceVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate2, locale.toString());
		if(ballot != null){
			List<ProprietyPoint> proprietyPoints = 
					ProprietyPointBallot.computeProprietyPointsForBallot(session, deviceType, answeringDate2, false, false, locale.toString());
			
			List<ProprietyPoint> newProprietyPointList = new ArrayList<ProprietyPoint>();
			for(ProprietyPoint m : proprietyPoints){
				if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale.toString())){
					newProprietyPointList.add(m);
				}
			}
			int count = 1;
			for(ProprietyPoint m : newProprietyPointList) {				
				DeviceVO unBallotedVO = new DeviceVO();
				unBallotedVO.setFormattedSerialNumber(FormaterUtil.formatNumberNoGrouping(count, locale.toString()));
				unBallotedVO.setMemberNames(m.getPrimaryMember().getFullname());
				unBallotedVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(m.getNumber(),locale.toString()));
				if(m.getRevisedSubject() != null && !m.getRevisedSubject().isEmpty()){
					unBallotedVO.setSubject(m.getRevisedSubject());
				}else{
					unBallotedVO.setSubject(m.getSubject());
				}
				
				unBallotedVOs.add(unBallotedVO);
				count++;
			}
			
			QuestionYaadiSuchiXmlVO data = new QuestionYaadiSuchiXmlVO();
			data.setHouseTypeName(session.getHouse().getType().getName());
			data.setSessionNumber(session.getNumber().toString());
			data.setSessionYear(FormaterUtil.formatNumberNoGrouping(session.getYear(), locale.toString()));
			// Device Type field not available in QuestionYaadiSuchiXmlVO so setting deviceType DeviceType in House
			data.setHouseType(deviceType.getName());
			data.setAnsweringDate(FormaterUtil.formatDateToString(answeringDate2, ApplicationConstants.SERVER_DATEFORMAT,locale.toString()));
			data.setDeviceVOs(unBallotedVOs);
			
			try {
				reportFile = generateReportUsingFOP(data, "propriety_point_unballot_template", reportFormat, "propriety_point_unballot_report", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Propriety Point Ballot Generated successfully in " + reportFormat + " format!");

			openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			
		}
		
	}
	
}

class ProprietyPointReportHelper{
	private static Logger logger = LoggerFactory.getLogger(ProprietyPointReportHelper.class);
	
	@SuppressWarnings("rawtypes")
	public static String getCurrentStatusReportData(Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "proprietypoint/error";
		try{
			String strDevice = request.getParameter("device"); 
			if(strDevice != null && !strDevice.isEmpty()){
				ProprietyPoint qt = ProprietyPoint.findById(ProprietyPoint.class, id);
				List report = generatetCurrentStatusReport(qt, strDevice, locale.toString());				
				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();
				if(report != null && !report.isEmpty()){
					
					List<User> users = User.findByRole(false, "PROIS_PRINCIPAL_SECRETARY", locale.toString());
					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
	
					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)? "PROIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "PROIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
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
								
								wfConfig = WorkflowConfig.getLatest(qt, ApplicationConstants.PROPRIETYPOINT_RECOMMEND_ADMISSION, locale.toString());
								model.addAttribute("currentStatusReportAllowedBeforeApproval", "YES");
								
							} else {
								wfConfig = WorkflowConfig.getLatest(qt, qt.getInternalStatus().getType(), locale.toString());
								model.addAttribute("currentStatusReportAllowedBeforeApproval", "NO");
							}							
							
							//Following block is added for solving the issue of propriety point drafts where in if there exist a draft and later the question is pending
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
								if(userGroupType.getType().equals(lastObject[23])){
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
							CustomParameter onPaperSigningAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "PROIS_CURRENTSTATUS_ONPAPER_SIGNING_AUTHORITY_"+qt.getHouseType().getType(), "");
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
											 ref = UserGroup.findProprietyPointActor(qt, authority, String.valueOf(0), locale.toString());
										}else{
											 userGroupType = UserGroupType.
													findByFieldName(UserGroupType.class, "type", str, locale.toString());
											 ref = UserGroup.findProprietyPointActor(qt, str, String.valueOf(0), locale.toString());
										}
										
										if(ref.getId() != null){
											Object[] actor = new Object[32];
											if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) 
													|| ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY1)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY2)){
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
	
					page = (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "proprietypoint/reports/statusreportlowerhouse": "proprietypoint/reports/statusreportupperhouse";
				}		
	
				model.addAttribute("deviceId", qt.getId());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	public static List generatetCurrentStatusReport(final ProprietyPoint proprietyPoint, final String device, final String locale){
		CustomParameter memberNameFormatParameter = null;
		if(proprietyPoint.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
		} else if(proprietyPoint.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_UPPERHOUSE", "");
		}
		String support = "";
		if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
			support = proprietyPoint.findAllMemberNames(memberNameFormatParameter.getValue());
		} else {
			support = proprietyPoint.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
		}		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("id",new String[]{proprietyPoint.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("PROIS_CURRENTSTATUS_REPORT", parameters);
		for(Object o : list){
			Object[] data = (Object[]) o;
			String details = ((data[4] != null)? data[4].toString():"-");
			String subject = ((data[5] != null)? data[5].toString():"-");
			String remarks = ((data[6] != null)? data[6].toString():"-");
			
			((Object[])o)[15] = support;
			((Object[])o)[4] = FormaterUtil.formatNumbersInGivenText(details, locale);
			((Object[])o)[5] = FormaterUtil.formatNumbersInGivenText(subject, locale);
			((Object[])o)[6] = FormaterUtil.formatNumbersInGivenText(remarks, locale);
			data = null;
		}

		return list;  
	}
	

	
	
}