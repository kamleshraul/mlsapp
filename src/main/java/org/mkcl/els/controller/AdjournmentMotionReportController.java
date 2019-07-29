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
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
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
@RequestMapping("adjournmentmotion/report")
public class AdjournmentMotionReportController extends BaseController{
	
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
		
		return "adjournmentmotion/reports/"+request.getParameter("reportout");		
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
		return "adjournmentmotion/reports/statusreport";
	}
	
	@RequestMapping(value="/{moId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("moId") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
				
		response.setContentType("text/html; charset=utf-8");		
		return AdjournmentMotionReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
	}
	
	@RequestMapping(value="/bhag1" ,method=RequestMethod.GET)
	public @ResponseBody void generateBhag1Report(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String adjourningDateStr = request.getParameter("adjourningDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() 
				|| sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.bhag1.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			CustomParameter bhag1StatusAllowedCP = CustomParameter.findByName(CustomParameter.class, "AMOIS_BHAG_1_ALLOWED_STATUS_TYPES", "");
			String allowedStatusTypesForBhag1 = "";
			if(bhag1StatusAllowedCP!=null && bhag1StatusAllowedCP.getValue()!=null && !bhag1StatusAllowedCP.getValue().isEmpty()) {
				allowedStatusTypesForBhag1 = bhag1StatusAllowedCP.getValue();
			} else {
				allowedStatusTypesForBhag1 = ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_ADMISSION + "," + ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION;
			}
			requestMap.put("allowedStatusTypesForBhag1", new String[]{allowedStatusTypesForBhag1});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List bhag1Motions = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {bhag1Motions}, "amois_bhag1_template", "WORD", "amois_bhag1", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.bhag1.someErrorOccurred", locale.toString());
			}
			System.out.println("AMOIS Bhag 1 Report generated successfully in WORD format!");

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
	
	@RequestMapping(value="/bhag2" ,method=RequestMethod.GET)
	public @ResponseBody void generateBhag2Report(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String adjourningDateStr = request.getParameter("adjourningDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() 
				|| sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.bhag2.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			CustomParameter bhag2StatusAllowedCP = CustomParameter.findByName(CustomParameter.class, "AMOIS_BHAG_2_ALLOWED_STATUS_TYPES", "");
			String allowedStatusTypesForBhag2 = "";
			if(bhag2StatusAllowedCP!=null && bhag2StatusAllowedCP.getValue()!=null && !bhag2StatusAllowedCP.getValue().isEmpty()) {
				allowedStatusTypesForBhag2 = bhag2StatusAllowedCP.getValue();
			} else {
				allowedStatusTypesForBhag2 = ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_REJECTION + "," + ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECTION;
			}
			requestMap.put("allowedStatusTypesForBhag2", new String[]{allowedStatusTypesForBhag2});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List bhag2Motions = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {bhag2Motions}, "amois_bhag2_template", "WORD", "amois_bhag2", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.bhag2.someErrorOccurred", locale.toString());
			}
			System.out.println("AMOIS Bhag 2 Report generated successfully in WORD format!");

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
	
	@RequestMapping(value="/statement" ,method=RequestMethod.GET)
	public @ResponseBody void generateStatementReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String adjourningDateStr = request.getParameter("adjourningDate");
		String sessionId = request.getParameter("sessionId");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() 
				|| sessionId==null || sessionId.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.statement.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			/**** Bhag 1 list ****/
			CustomParameter bhag1StatusAllowedCP = CustomParameter.findByName(CustomParameter.class, "AMOIS_BHAG_1_ALLOWED_STATUS_TYPES", "");
			String allowedStatusTypesForBhag1 = "";
			if(bhag1StatusAllowedCP!=null && bhag1StatusAllowedCP.getValue()!=null && !bhag1StatusAllowedCP.getValue().isEmpty()) {
				allowedStatusTypesForBhag1 = bhag1StatusAllowedCP.getValue();
			} else {
				allowedStatusTypesForBhag1 = ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_ADMISSION + "," + ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION;
			}
			requestMap.put("allowedStatusTypesForBhag1", new String[]{allowedStatusTypesForBhag1});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List bhag1Motions = Query.findReport("AMOIS_BHAG1_REPORT", requestMap, true);
			/**** Bhag 2 list ****/
			CustomParameter bhag2StatusAllowedCP = CustomParameter.findByName(CustomParameter.class, "AMOIS_BHAG_2_ALLOWED_STATUS_TYPES", "");
			String allowedStatusTypesForBhag2 = "";
			if(bhag2StatusAllowedCP!=null && bhag2StatusAllowedCP.getValue()!=null && !bhag2StatusAllowedCP.getValue().isEmpty()) {
				allowedStatusTypesForBhag2 = bhag2StatusAllowedCP.getValue();
			} else {
				allowedStatusTypesForBhag2 = ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_REJECTION + "," + ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECTION;
			}
			requestMap.put("allowedStatusTypesForBhag2", new String[]{allowedStatusTypesForBhag2});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List bhag2Motions = Query.findReport("AMOIS_BHAG2_REPORT", requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {bhag1Motions, bhag2Motions}, "amois_statement_template", "WORD", "amois_statement", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.statement.someErrorOccurred", locale.toString());
			}
			System.out.println("AMOIS Bhag 2 Report generated successfully in WORD format!");

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
	
	@RequestMapping(value="/submittedmotions" ,method=RequestMethod.GET)
	public @ResponseBody void generateSubmittedMotionsReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String adjourningDateStr = request.getParameter("adjourningDate");		
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() 
				|| sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.submitted_motions_report.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List submittedMotions = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {submittedMotions}, "amois_submitted_motions_template", "WORD", "amois_submitted_motions", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.submitted_motions_report.someErrorOccurred", locale.toString());
			}
			System.out.println("AMOIS Submitted Motions Report generated successfully in WORD format!");

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
	
	@RequestMapping(value="/admittedmotions" ,method=RequestMethod.GET)
	public @ResponseBody void generateAdmittedMotionsReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String adjourningDateStr = request.getParameter("adjourningDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() 
				|| sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.admitted_motions_report.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List admittedMotions = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {admittedMotions}, "amois_admitted_motions_template", "WORD", "amois_admitted_motions", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.admitted_motions_report.someErrorOccurred", locale.toString());
			}
			System.out.println("AMOIS Admitted Motions Report generated successfully in WORD format!");

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
	
	@RequestMapping(value="/rejectedmotions" ,method=RequestMethod.GET)
	public @ResponseBody void generateRejectedMotionsReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String adjourningDateStr = request.getParameter("adjourningDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() 
				|| sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.rejected_motions_report.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List rejectedMotions = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {rejectedMotions}, "amois_rejected_motions_template", "WORD", "amois_rejected_motions", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.rejected_motions_report.someErrorOccurred", locale.toString());
			}
			System.out.println("AMOIS Rejected Motions Report generated successfully in WORD format!");

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
		String adjourningDateStr = request.getParameter("adjourningDate");
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(sessionId==null || sessionId.isEmpty() || reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.register_report.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = null;
			if(adjourningDateStr!=null && !adjourningDateStr.isEmpty()) {
				adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
			}		
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("rawtypes")
			List registerMotions = Query.findReport(reportQueryName, requestMap, true);
			try {
				reportFile = generateReportUsingFOP(new Object[] {registerMotions}, "amois_register_template", "WORD", "amois_register", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("**** Some error occurred ****");
				isError = true;
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.register_report.someErrorOccurred", locale.toString());
			}
			System.out.println("AMOIS Register Report generated successfully in WORD format!");

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
	
	@RequestMapping(value ="/generateIntimationLetter", method = RequestMethod.GET)
	private void generateIntimationLetter(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "adjournmentmotion/report";
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
			
			
			
//			String strCopyType = request.getParameter("copyType");
//			Long workflowDetailCount = (long) 0;
//			Boolean isResendRevisedMotionTextWorkflow = false;
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("motionId", new String[]{strId});
				
				@SuppressWarnings("rawtypes")
				List reportData = Query.findReport("ADJOURNMENTMOTION_INTIMATION_LETTER", parameters);
				String templateName = "adjournmentmotion_intimation_letter_template";
				
				
				File reportFile = null;
				
				reportFile = generateReportUsingFOP(new Object[] {reportData}, templateName, strReportFormat, "adjournmentmotion_intimationletter",locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");
				//retVal = "adjournmentmotion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
	@RequestMapping(value ="/generateReminderLetter", method = RequestMethod.GET)
	private void generateReminderLetter(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "adjournmentmotion/report";
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
			
			String queryName = "ADJOURNMENTMOTION_REMINDER1_LETTER";
			String xsltTemplateName = "adjournmentmotion_reminder_letter1_template";
			String reportFileName = "adjournmentmotion_reminderletter1";
			
			String intimationLetterFilter = request.getParameter("intimationLetterFilter");
			if(intimationLetterFilter!=null && intimationLetterFilter.equals("reminder2ToDepartmentForReply")) {
				queryName = "ADJOURNMENTMOTION_REMINDER2_LETTER";
				xsltTemplateName = "adjournmentmotion_reminder_letter2_template";
				reportFileName = "adjournmentmotion_reminderletter2";
			}			
			
//			String strCopyType = request.getParameter("copyType");
//			Long workflowDetailCount = (long) 0;
//			Boolean isResendRevisedMotionTextWorkflow = false;
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("motionId", new String[]{strId});
				
				@SuppressWarnings("rawtypes")
				List reportData = Query.findReport(queryName, parameters);
				
				File reportFile = generateReportUsingFOP(new Object[] {reportData}, xsltTemplateName, strReportFormat, reportFileName, locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");
				//retVal = "adjournmentmotion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
}

class AdjournmentMotionReportHelper{
	private static Logger logger = LoggerFactory.getLogger(AdjournmentMotionReportHelper.class);
	
	public static String getCurrentStatusReportData(Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "adjournmentmotion/error";
		try{
			String strDevice = request.getParameter("device"); 
			if(strDevice != null && !strDevice.isEmpty()){
				AdjournmentMotion qt = AdjournmentMotion.findById(AdjournmentMotion.class, id);
				List report = generatetCurrentStatusReport(qt, strDevice, locale.toString());				
				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();
				if(report != null && !report.isEmpty()){
					
					List<User> users = User.findByRole(false, "AMOIS_PRINCIPAL_SECRETARY", locale.toString());
					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
	
					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)? "AMOIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "AMOIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
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
							CustomParameter csptCurrentStatusAllowedBeforeApproval = CustomParameter.findByName(CustomParameter.class, qt.getType().getType().toUpperCase()+"_"+qt.getHouseType().getType().toUpperCase()+"_CURRENT_STATUS_REPORT_ALLOWED_BEFORE_APPROVAL", "");
							if(csptCurrentStatusAllowedBeforeApproval!=null && csptCurrentStatusAllowedBeforeApproval.getValue()!=null
									&& csptCurrentStatusAllowedBeforeApproval.getValue().equals("YES")) {
								
								wfConfig = WorkflowConfig.getLatest(qt, ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_ADMISSION, locale.toString());
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
							CustomParameter onPaperSigningAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "AMOIS_CURRENTSTATUS_ONPAPER_SIGNING_AUTHORITY_"+qt.getHouseType().getType(), "");
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
											 ref = UserGroup.findAdjournmentMotionActor(qt, authority, String.valueOf(0), locale.toString());
										}else{
											 userGroupType = UserGroupType.
													findByFieldName(UserGroupType.class, "type", str, locale.toString());
											 ref = UserGroup.findAdjournmentMotionActor(qt, str, String.valueOf(0), locale.toString());
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
	
					page = (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "adjournmentmotion/reports/statusreportlowerhouse": "adjournmentmotion/reports/statusreportupperhouse";
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
	public static List generatetCurrentStatusReport(final AdjournmentMotion motion, final String device, final String locale){
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

		List list = Query.findReport("AMOIS_CURRENTSTATUS_REPORT", parameters);
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