package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("adjournmentmotion/report")
public class AdjournmentMotionReportController extends BaseController{
	
	@RequestMapping(value="/bhag1" ,method=RequestMethod.GET)
	public @ResponseBody void generateBhag1Report(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String adjourningDateStr = request.getParameter("adjourningDate");
		String reportQueryName = request.getParameter("reportQueryName");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() || reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.bhag1.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
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
		String reportQueryName = request.getParameter("reportQueryName");
		if(adjourningDateStr==null || adjourningDateStr.isEmpty() || reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "amois.bhag2.parameterNotSet", locale.toString());						
		} else {
			Date adjourningDate = FormaterUtil.formatStringToDate(adjourningDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
			requestMap.put("adjourningDate", new String[] {FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.DB_DATEFORMAT)});
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
	
}