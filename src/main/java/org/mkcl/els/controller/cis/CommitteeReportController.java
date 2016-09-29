package org.mkcl.els.controller.cis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("committee/report")
public class CommitteeReportController extends BaseController {
	@RequestMapping(value="{id}/viewPatrakBhag" ,method=RequestMethod.GET)
	public @ResponseBody void generatepatrakbhagReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model,@PathVariable("id") final Long id) throws ELSException{
	
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		try {
			/**** find report data ****/
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale.toString()});
			queryParameters.put("id", new String[]{id.toString()});
			 String queryName = "CIS_PATRAKBHAG_REPORT";
			List<Object[]> reportData = Query.findReport(queryName, queryParameters);
			List<String> serialNo=new ArrayList<String>();
			serialNo=this.populateSerialNumbers(reportData, locale);
			List<String> committeIDList=new ArrayList<String>();
			Committee committee = Committee.findById(Committee.class, id);
			String formattedDate = FormaterUtil.formatDateToString(committee.getFormationDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString());
			List<Object[]> reportData1 = Query.findReport("CIS_PATRAKBHAG_INVITEDMEMBER_REPORT", queryParameters);
			List<String> serialNo1=new ArrayList<String>();
			serialNo1=this.populateSerialNumbers(reportData1, locale);
			String DateInIndianCalendar = FormaterUtil.getIndianDate(committee.getFormationDate(), locale);
			if(reportData!=null && !reportData.isEmpty()) {
				committeIDList.add(id.toString());
				/***	For all committee
				String commID="";
				for(int i=0;i<reportData.size();i++)
				{					
					if(i==0)
					{
						committeIDList.add(reportData.get(0)[0].toString());
						commID+=reportData.get(0)[0].toString()+",";
					}
					else
					{
						if(!commID.contains(reportData.get(i)[0].toString())) {
							committeIDList.add(reportData.get(i)[0].toString());
							commID+=reportData.get(i)[0].toString()+",";
						}
					}					
				}
				System.out.println("commID: " + commID);
				for(String i: committeIDList) {
					System.out.println("committeIDList element: " + i);
				}
				
				***/
				/**** generate report ****/
				if(!isError) {
					reportFile = generateReportUsingFOP(new Object[]{reportData,formattedDate,DateInIndianCalendar,reportData1,serialNo,serialNo1}, "cis_patrakbhag_template", "WORD", "cis_patrakbhag_report", locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
				}			
			}
			
		}catch(Exception e) {
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

	
	@RequestMapping(value="{id}/viewMemorandum" ,method=RequestMethod.GET)
	public @ResponseBody void generateviewMemorandumReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model,@PathVariable("id") final Long id) throws ELSException{
	
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		try {
			/**** find report data ****/
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale.toString()});
			queryParameters.put("id", new String[]{id.toString()});
			 String queryName = "CIS_MEMORANDUM_REPORT";
			List<Object[]> reportData = Query.findReport(queryName, queryParameters);
			List<String> serialNo=new ArrayList<String>();
			serialNo=this.populateSerialNumbers(reportData, locale);
			List<String> committeIDList=new ArrayList<String>();
			Committee committee = Committee.findById(Committee.class, id);
	String formattedDate = FormaterUtil.formatDateToString(committee.getFormationDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString());
	List<Object[]> reportData1 = Query.findReport("CIS_MEMORANDUM_INVITEDMEMBER_REPORT", queryParameters);
	List<String> serialNo1=new ArrayList<String>();
	serialNo1=this.populateSerialNumbers(reportData1, locale);
			String DateInIndianCalendar = FormaterUtil.getIndianDate(committee.getFormationDate(), locale);
		
			if(reportData!=null && !reportData.isEmpty()) {
				committeIDList.add(id.toString());
				/***	For all committee
				String commID="";
				for(int i=0;i<reportData.size();i++)
				{					
					if(i==0)
					{
						committeIDList.add(reportData.get(0)[0].toString());
						commID+=reportData.get(0)[0].toString()+",";
					}
					else
					{
						if(!commID.contains(reportData.get(i)[0].toString())) {
							committeIDList.add(reportData.get(i)[0].toString());
							commID+=reportData.get(i)[0].toString()+",";
						}
					}					
				}
				System.out.println("commID: " + commID);
				for(String i: committeIDList) {
					System.out.println("committeIDList element: " + i);
				}
					***/
				/**** generate report ****/
				if(!isError) {
					reportFile = generateReportUsingFOP(new Object[]{reportData,formattedDate,DateInIndianCalendar,serialNo,reportData1,serialNo1}, "cis_memorandum_template", "WORD", "cis_memorandum_report", locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
				}			
			}
			
		}catch(Exception e) {
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
	
	@RequestMapping(value="{id}/internalMeeting" ,method=RequestMethod.GET)
	public @ResponseBody void internalMeetingReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model,@PathVariable("id") final Long id) throws ELSException{
	
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		try {
			/**** find report data ****/
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale.toString()});
			queryParameters.put("id", new String[]{id.toString()});
			 String queryName = "CIS_INTERNALMEETING_REPORT";
			List<Object[]> reportData = Query.findReport(queryName, queryParameters);
			List<String> committeIDList=new ArrayList<String>();
			CommitteeMeeting committeemeeting = CommitteeMeeting.findById(CommitteeMeeting.class, id);
			String formattedDate = FormaterUtil.formatDateToString(committeemeeting.getMeetingDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString());
					
					String DateInIndianCalendar = FormaterUtil.getIndianDate(committeemeeting.getMeetingDate(), locale);
				
			if(reportData!=null && !reportData.isEmpty()) {
				committeIDList.add(id.toString());
				/***	For all committee
				for(int i=0;i<reportData.size();i++)
				{					
					if(i==0)
					{
						committeIDList.add(reportData.get(0)[0].toString());
						commID+=reportData.get(0)[0].toString()+",";
					}
					else
					{
						if(!commID.contains(reportData.get(i)[0].toString())) {
							committeIDList.add(reportData.get(i)[0].toString());
							commID+=reportData.get(i)[0].toString()+",";
						}
					}					
				}
				System.out.println("commID: " + commID);
				for(String i: committeIDList) {
					System.out.println("committeIDList element: " + i);
				}
					***/
				/**** generate report ****/
				if(!isError) {
					reportFile = generateReportUsingFOP(new Object[]{reportData,formattedDate,DateInIndianCalendar,committeIDList}, "cis_internalmeeting_template", "WORD", "cis_internalmeeting_report", locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
				}			
			}
			
		}catch(Exception e) {
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
	
	@RequestMapping(value="{id}/firstMeeting" ,method=RequestMethod.GET)
	public @ResponseBody void firstMeetingReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model,@PathVariable("id") final Long id) throws ELSException{
	
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		try {
			/**** find report data ****/
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale.toString()});
			queryParameters.put("id", new String[]{id.toString()});
			 String queryName = "CIS_firstMeeting_REPORT";
			List<Object[]> reportData = Query.findReport(queryName, queryParameters);
			List<String> committeIDList=new ArrayList<String>();
			
			if(reportData!=null && !reportData.isEmpty()) {
				String commID="";
				for(int i=0;i<reportData.size();i++)
				{					
					if(i==0)
					{
						committeIDList.add(reportData.get(0)[0].toString());
						commID+=reportData.get(0)[0].toString()+",";
					}
					else
					{
						if(!commID.contains(reportData.get(i)[0].toString())) {
							committeIDList.add(reportData.get(i)[0].toString());
							commID+=reportData.get(i)[0].toString()+",";
						}
					}					
				}
				System.out.println("commID: " + commID);
				for(String i: committeIDList) {
					System.out.println("committeIDList element: " + i);
				}
				/**** generate report ****/
				if(!isError) {
					reportFile = generateReportUsingFOP(new Object[]{reportData, committeIDList}, "cis_firstMeeting_template", "WORD", "cis_firstMeeting_report", locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
				}			
			}
			
		}catch(Exception e) {
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
	
	@RequestMapping(value="{id}/meetingAttendance" ,method=RequestMethod.GET)
	public @ResponseBody void meetingAttendanceReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model,@PathVariable("id") final Long id) throws ELSException{
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;
		try {
			/**** find report data ****/
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale.toString()});
			queryParameters.put("id", new String[]{id.toString()});
			 String queryName = "CIS_MEETINGATTENDANCE_REPORT";
			List reportData = Query.findReport(queryName, queryParameters);
			List<String> serialNo=new ArrayList<String>();
			serialNo=this.populateSerialNumbers(reportData, locale);
			CommitteeMeeting committeemeeting = CommitteeMeeting.findById(CommitteeMeeting.class, id);
			String formattedDate = FormaterUtil.formatDateToString(committeemeeting.getMeetingDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString());
			String meetingTime = FormaterUtil.formatNumbersInGivenText(committeemeeting.getStartTime(),locale.toString());
			String meetingLocation = committeemeeting.getMeetingLocation();
			List<Object[]> reportData1 = Query.findReport("CIS_MEETINGATTENDANCE_INVITEDMEMBER_REPORT", queryParameters);
			List<String> serialNo1=new ArrayList<String>();
			serialNo1=this.populateSerialNumbers(reportData1, locale);
			
			List<Object[]> reportData2 = Query.findReport("CIS_MEETINGATTENDANCE_NONCOMMITTEEMEMBER_REPORT", queryParameters);
			List<String> serialNo2=new ArrayList<String>();
			serialNo2=this.populateSerialNumbers(reportData2, locale);
			
			List<Object[]> reportData3 = Query.findReport("CIS_MEETINGATTENDANCE_NONCOMMITTEEMEMBER_REPORT1", queryParameters);
			List<String> serialNo3=new ArrayList<String>();
			serialNo3=this.populateSerialNumbers(reportData3, locale);
					String DateInIndianCalendar = FormaterUtil.getIndianDate(committeemeeting.getMeetingDate(), locale);
				
			if(reportData!=null && !reportData.isEmpty()) {
			
				
				/**** generate report ****/
				if(!isError) {
					reportFile = generateReportUsingFOP(new Object[]{reportData,formattedDate,DateInIndianCalendar,meetingTime,serialNo,meetingLocation,reportData1,serialNo1,reportData2,serialNo2,reportData3,serialNo3}, "cis_meetingattendance_template", "WORD", "cis_meetingattendance_report", locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
				}			
			}
			
		}catch(Exception e) {
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


}
