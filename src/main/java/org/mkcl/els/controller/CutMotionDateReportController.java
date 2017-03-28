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

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("cutmotiondate/report")
public class CutMotionDateReportController extends BaseController{
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/patrakbhag2", method=RequestMethod.GET)
	protected void generatePatrakBhag2Report(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		File reportFile = null;
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		String reportQuery = request.getParameter("reportQuery");
		String xsltFileName = request.getParameter("xsltFileName");
		String outputFormat = request.getParameter("outputFormat");
		String reportFileName = request.getParameter("reportFileName");
		
		if(reportQuery!=null && !reportQuery.isEmpty()
				&& xsltFileName!=null && !xsltFileName.isEmpty()
				&& outputFormat!=null && !outputFormat.isEmpty()
				&& reportFileName!=null && !reportFileName.isEmpty()) {
			try {
				Session session = Session.findById(Session.class, Long.parseLong(request.getParameter("sessionId")));
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(request.getParameter("deviceTypeId")));
				CutMotionDate cutMotionDate = CutMotionDate.findCutMotionDateSessionDeviceType(session, deviceType, locale.toString());
				if(cutMotionDate==null) {
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "cutmotiondate.date_not_found", locale.toString());
				} else {
					@SuppressWarnings("unchecked")
					Map<String, String[]> parameterMap = new HashMap<String, String[]>();
					parameterMap.put("locale", new String[]{locale.toString()});
					parameterMap.putAll(request.getParameterMap());
					/** Populate Headers **/
					@SuppressWarnings("unchecked")
					List<Object[]> reportHeaders = Query.findReport(request.getParameter("reportQuery")+"_HEADERS", parameterMap);
					/** Populate Data **/				
					Map<List<String>, List<Object[]>> discussionDateDepartmentPrioritiesMap = new LinkedHashMap<List<String>, List<Object[]>>();
					List<String> discussionDateKey = null;
					List<Object[]> departmentPriorities = null;
					int i=1;
					for(Date discussionDate: cutMotionDate.findActiveDiscussionDates()) {
						discussionDateKey = new ArrayList<String>();
						discussionDateKey.add(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
						String formattedDiscussionDate = FormaterUtil.formatDateToString(discussionDate, ApplicationConstants.DB_DATEFORMAT);
						discussionDateKey.add(formattedDiscussionDate);
						/* find department priorities for given discussion date */
						parameterMap.put("discussionDate", new String[]{formattedDiscussionDate});
						departmentPriorities = Query.findReport(request.getParameter("reportQuery"), parameterMap);
						if(departmentPriorities!=null && !departmentPriorities.isEmpty()) {
							discussionDateKey.add(departmentPriorities.get(0)[2].toString());
							discussionDateKey.add(departmentPriorities.get(0)[5].toString());
							discussionDateDepartmentPrioritiesMap.put(discussionDateKey, departmentPriorities);
							discussionDateKey = null;
							departmentPriorities = null;
						} else {
							discussionDateDepartmentPrioritiesMap.put(discussionDateKey, new ArrayList<Object[]>());
							discussionDateKey = null;
							departmentPriorities = null;
						}
						i++;
					}									
					/** Populate Publishing Date of Patrak Bhag 2 **/				
					Date publishingDate = cutMotionDate.findPublishingDate();
					if(publishingDate==null) {
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "cutmotiondate.publishing_pending", locale.toString());
					} else {
						String formattedPublishingDate = FormaterUtil.formatDateToString(publishingDate, ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_2, locale.toString());
						String formattedPublishingDateInIndianFormat = FormaterUtil.formatDateToString(publishingDate, ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString());
						String answeringDateInIndianCalendar = FormaterUtil.getIndianDateInShakeFormat(publishingDate, locale);
						formattedPublishingDateInIndianFormat = formattedPublishingDateInIndianFormat + " / " + answeringDateInIndianCalendar;
						/** User Name and Role **/
						String userName = "";
						String userRole="";
						CustomParameter roleCustomParameter = CustomParameter.findByName(CustomParameter.class, "CUTMOTION_PATRAKBHAG2_FOOTER_ROLE", "");
						if(roleCustomParameter==null) {
							logger.error("/**** role parameter for cutmotion patrakbhag2 footer not set. ****/");
							throw new ELSException("CutMotionDateReportController/generatePatrakBhag2Report", "role parameter for cutmotion patrakbhag2 footer not set.");
						}
						Role role = Role.findByFieldName(Role.class, "type", roleCustomParameter.getValue(), locale.toString());
						if(role==null) {
							logger.error("/**** role '"+roleCustomParameter.getValue()+"' is not found. ****/");
							throw new ELSException("CutMotionDateReportController/generatePatrakBhag2Report", "role '"+roleCustomParameter.getValue()+"' is not found.");
						}
						userRole = role.getLocalizedName();
						List<User> users = User.findByRole(false, role.getName(), locale.toString());
						//as principal secretary for cutmotiondate is only one, so user is obviously first element of the list.
						userName = users.get(0).findFirstLastName();
						/**** generate fop report ****/
						/** create report in reportFile **/
						reportFile = generateReportUsingFOP(new Object[] {reportHeaders, discussionDateDepartmentPrioritiesMap, formattedPublishingDate, formattedPublishingDateInIndianFormat, userName, userRole}, xsltFileName, outputFormat, reportFileName, locale.toString());
						/** open reportFile for view/download in browser **/
			    		if(reportFile!=null) {
			    			System.out.println("Report generated successfully in " + outputFormat + " format!");
			    			openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
			    		}
					}
				}								
			} catch(Exception e) {
				e.printStackTrace();
				isError = true;					
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
			}
		} else {
			isError = true;
			logger.error("**** Check request parameters reportQuery, xsltFileName, outputFormat, reportFileName for null values ****");
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
	
}

class CutMotionDateReportHelper{
	
	private static Logger logger = LoggerFactory.getLogger(CutMotionDateReportHelper.class);
	
	private void SimplifyPatrakBhagReportData(List<Object[]> reportData) {
		Map<List<String>, List<Object[]>> simplifiedReportData = new HashMap<List<String>, List<Object[]>>();
		String discussionDate = (String) reportData.get(0)[0];	
		String formattedDiscussionDate = (String) reportData.get(0)[1];
		for(Object[] rd: reportData) {
			if(!rd[0].toString().equals(discussionDate)) {
				discussionDate = rd[0].toString();
				formattedDiscussionDate = rd[1].toString();
			}
		}
	}
	
}