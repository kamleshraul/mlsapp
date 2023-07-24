package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.mkcl.els.common.vo.CountsUsingGroupByReportVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MinistryVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RoundVO;
import org.mkcl.els.common.xmlvo.DiscussionMotionIntimationLetterXmlVO;
//import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.ActivityLog;
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
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.DiscussionMotion;
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
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.ballot.Ballot;
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
@RequestMapping("discussionmotion/report")
public class DiscussionMotionReportController extends BaseController{
	
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
				CustomParameter csptStatReportUserGroups = CustomParameter.findByName(CustomParameter.class, "SMOIS_ALLOWED_USERGROUPS_FOR_STAT_REPORT_" + strHouseType.toUpperCase() , "");				
				if(csptStatReportUserGroups != null){
					strUsergroups = csptStatReportUserGroups.getValue().split(",");
				}
								
				model.addAttribute("day", FormaterUtil.getDayInLocaleLanguage(FormaterUtil.formatDateToString(new Date(), "EEEE", locale.toString()), locale.toString()));
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
		return "discussionmotion/reports/statreport";
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/commonadmissionreport", method = RequestMethod.GET)
	public void fopGeneralReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("discussionmotionId");
			String strReportFormat = request.getParameter("outputFormat");	
			DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(request.getParameter("motionType")) );
			
			String templateName =deviceType.getType()+"_"+request.getParameter("templateName")+"_"+request.getParameter("houseType");
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = request.getParameterMap();
				
				List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
				List reportData1 = Query.findReport(request.getParameter("reportQuery")+"_EXTRA_DETAILS", parameters);	
				
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/memberofoppositionreport", method = RequestMethod.GET)
	public void memberofoppositionReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("discussionmotionId");
			String strReportFormat = request.getParameter("outputFormat");	
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = request.getParameterMap();
				
				List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
				List reportData1 = Query.findReport(request.getParameter("reportQuery")+"_CLUBBED_DETAILS", parameters);	
				String templateName = request.getParameter("templateName");
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/partyreport", method = RequestMethod.GET)
	public void partyReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("discussionmotionId");
			String strReportFormat = request.getParameter("outputFormat");	
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = request.getParameterMap();
				
				List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
				List reportData1 = Query.findReport(request.getParameter("reportQuery")+"_CLUBBED_DETAILS", parameters);	
				String templateName = request.getParameter("templateName");
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/departmentreport", method = RequestMethod.GET)
	public void departmentReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("discussionmotionId");
			String strReportFormat = request.getParameter("outputFormat");	
			DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(request.getParameter("motionType")) );
			
			String templateName =deviceType.getType()+"_"+request.getParameter("templateName")+"_"+request.getParameter("houseType");
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = request.getParameterMap();
				
				List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
				List reportData1 = Query.findReport(request.getParameter("reportQuery")+"_CLUBBED_DETAILS", parameters);	
				
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/houseitemreport", method = RequestMethod.GET)
	public void houseitemreport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("discussionmotionId");
			String strReportFormat = request.getParameter("outputFormat");	
			DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(request.getParameter("motionType")) );
			
			String templateName =deviceType.getType()+"_"+request.getParameter("templateName")+"_"+request.getParameter("houseType");
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = request.getParameterMap();
				
				List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
				List reportData1 = Query.findReport(request.getParameter("reportQuery")+"_CLUBBED_DETAILS", parameters);	
			
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/citationreport", method = RequestMethod.GET)
	public void citationreport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("discussionmotionId");
			String strReportFormat = request.getParameter("outputFormat");	
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = request.getParameterMap();
				
				List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
				List reportData1 = Query.findReport(request.getParameter("reportQuery")+"_CLUBBED_DETAILS", parameters);	
				String templateName = request.getParameter("templateName");
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
/*	@RequestMapping(value ="/commonadmissionreport", method = RequestMethod.GET)
	public void commonAdmissionReport(Model model,HttpServletRequest request,HttpServletResponse response, Locale locale){
		try{
			String strDiscussionMotion = request.getParameter("motionId");
			if(strDiscussionMotion != null && !strDiscussionMotion.isEmpty()){
				DiscussionMotion m = DiscussionMotion.findById(DiscussionMotion.class, new Long(strDiscussionMotion));
				
				if(m != null){
					HouseType ht = m.getHouseType();
					
					if(ht.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						getAdmissionReport(model, request, response, locale);
					}else if(ht.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						getNiverdanTarikh(model, request, response, locale);
					}
				}
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		
	}
	*/
	/**
	 * Admission report for assembly 
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 */
	private void getAdmissionReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("motionId");
			String strReportFormat = request.getParameter("outputFormat");	
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("motionId", new String[]{strId});
				
				@SuppressWarnings("rawtypes")
				List reportData = Query.findReport("DISCUSSIONMOTION_NIVEDAN_TARIKH_LOWERHOUSE", parameters);	
				String templateName = "motion_nivedan_tarikh";
				File reportFile = null;
				
				reportFile = generateReportUsingFOP(new Object[] {reportData}, templateName, strReportFormat, "motionNivedanTarikh",locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
	/**
	 * Admission report for council
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 */
	private void getNiverdanTarikh(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strId = request.getParameter("motionId");
			String strReportFormat = request.getParameter("outputFormat");	
			String strIsAdvanceCopy = request.getParameter("isAdvanceCopy");
			String isAdvanceCopy = "no";
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("motionId", new String[]{strId});
				if(strIsAdvanceCopy != null && !strIsAdvanceCopy.isEmpty()){
					isAdvanceCopy = strIsAdvanceCopy;
				}
				@SuppressWarnings("rawtypes")
				List reportData = Query.findReport("MOTION_NIVEDAN_TARIKH", parameters);	
				String templateName = "motion_nivedan_tarikh";
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {reportData, isAdvanceCopy}, templateName, strReportFormat, "motionNivedanTarikh",locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");;
				//retVal = "motion/info";
			}			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		//return retVal;
	}
	
	@RequestMapping(value="/currentstatusreport", method=RequestMethod.GET)
	public String getCurrentStatusReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){

		String strDevice = request.getParameter("device");
		String strReportType = request.getParameter("reportType");
		String strWfid = request.getParameter("wfdId");
		String strDMid = request.getParameter("dmId");
		
		WorkflowDetails wfd = null;
		if(strWfid != null && !strWfid.isEmpty()){
			wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
			if(wfd != null){
				model.addAttribute("dmId", wfd.getDeviceId());
			}
		}
		
		if(strDMid != null && !strDMid.isEmpty()){
			model.addAttribute("dmId", strDMid);
		}
		
		model.addAttribute("reportType", strReportType);
		if(strDevice != null && !strDevice.isEmpty()){		
			model.addAttribute("device", strDevice);
		}

		response.setContentType("text/html; charset=utf-8");
		return "discussionmotion/reports/statusreport";
	}

	@RequestMapping(value="/{dmId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("dmId") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){

		response.setContentType("text/html; charset=utf-8");	
		UserGroupType ugtDS1 = UserGroupType.findByType(ApplicationConstants.DEPUTY_SECRETARY1, locale.toString());
		if(ugtDS1!=null) {
			model.addAttribute("deputy_secretary1_label", ugtDS1.getDisplayName());
		} else {
			model.addAttribute("deputy_secretary1_label", "");
		}
		UserGroupType ugtDS2 = UserGroupType.findByType(ApplicationConstants.DEPUTY_SECRETARY2, locale.toString());
		if(ugtDS2!=null) {
			model.addAttribute("deputy_secretary2_label", ugtDS2.getDisplayName());
		} else {
			model.addAttribute("deputy_secretary2_label", "");
		}
		UserGroupType ugtDS = UserGroupType.findByType(ApplicationConstants.DEPUTY_SECRETARY, locale.toString());
		if(ugtDS!=null) {
			model.addAttribute("deputy_secretary_label", ugtDS.getDisplayName());
		} else {
			model.addAttribute("deputy_secretary_label", "");
		}
		return DiscussionMotionReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
	}
	
	@RequestMapping(value="/generateIntimationLetter" ,method=RequestMethod.GET)
	public @ResponseBody void generateIntimationLetter(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String strDiscussionMotionId = request.getParameter("discussionmotionId");		
		String strWorkflowId = request.getParameter("workflowId");
		String intimationLetterFilter=request.getParameter("intimationLetterFilter");

		//in case if request comes from workflow page, discussionmotion id is retrived from workflow details
		if(strWorkflowId!=null && !strWorkflowId.isEmpty()) {
			WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowId));
			if(workflowDetails!=null) {
				strDiscussionMotionId = workflowDetails.getDeviceId();
			}
		}

		if(strDiscussionMotionId!=null && !strDiscussionMotionId.isEmpty()) {
			DiscussionMotion discussionmotion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(strDiscussionMotionId));
			if(discussionmotion!=null) {
				DiscussionMotionIntimationLetterXmlVO letterVO = new DiscussionMotionIntimationLetterXmlVO();
				DeviceType deviceType = discussionmotion.getType();
				letterVO.setDeviceType(deviceType.getName());
				if(discussionmotion.getNumber()!=null) {
					letterVO.setNumber(FormaterUtil.formatNumberNoGrouping(discussionmotion.getNumber(), discussionmotion.getLocale()));
				}
				HouseType houseType = discussionmotion.getHouseType();
				if(houseType!=null) {
					letterVO.setHouseType(houseType.getType());
					letterVO.setHouseTypeName(houseType.getName());
				}	
				Session session = discussionmotion.getSession();
				if(session!=null) {					
					letterVO.setSessionPlace(session.getPlace().getPlace());
					if(session.getNumber()!=null) {
						letterVO.setSessionNumber(session.getNumber().toString());
					}		
					if(session.getYear()!=null) {
						letterVO.setSessionYear(FormaterUtil.formatNumberNoGrouping(session.getYear(), locale.toString()));
					}
				}
/*				Group group = discussionmotion.getGroup();
				if(group!=null) {
					letterVO.setGroupNumber(FormaterUtil.formatNumberNoGrouping(group.getNumber(), discussionmotion.getLocale()));
				}*/
				String formattedText = "";
				if(discussionmotion.getRevisedSubject()!=null && !discussionmotion.getRevisedSubject().isEmpty()) {
					formattedText = discussionmotion.getRevisedSubject();					
				} else {
					formattedText = discussionmotion.getSubject();
				}
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, discussionmotion.getLocale());
				letterVO.setSubject(formattedText);
				if(discussionmotion.getParent()!=null) {
					formattedText = discussionmotion.getNoticeContent();
				} else {
					if(discussionmotion.getRevisedNoticeContent()!=null && !discussionmotion.getNoticeContent().isEmpty()) {
						formattedText = discussionmotion.getNoticeContent();					
					} else {
						formattedText = discussionmotion.getNoticeContent();
					}
				}				
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, discussionmotion.getLocale());
				letterVO.setDiscussionMotionText(formattedText);	
				/**** populating member names with customized formatting ****/
				String memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
				CustomParameter memberNameFormatParameter = null;
				if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_LOWERHOUSE", "");
				} else if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_UPPERHOUSE", "");
				}
				if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
					memberNameFormat = memberNameFormatParameter.getValue();
				}
				Member primaryMember = discussionmotion.getPrimaryMember();
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
				if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					allMemberNames = discussionmotion.findAllMemberNamesWithConstituencies(memberNameFormat);
				} else if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					allMemberNames = discussionmotion.findAllMemberNames(memberNameFormat);
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
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.intimationLetter.noMemberFound", locale.toString());
				}	
				try {
					MemberMinister memberMinister = DiscussionMotion.findMemberMinisterIfExists(discussionmotion);
					if(memberMinister!=null) {
						letterVO.setPrimaryMemberDesignation(memberMinister.getDesignation().getName());
					} else {
						letterVO.setPrimaryMemberDesignation("");
					}
				} catch(ELSException ex) {
					letterVO.setPrimaryMemberDesignation("");
				}
			/*	SubDepartment subDepartment = discussionmotion.getSubDepartment();
				if(subDepartment!=null) {
					letterVO.setSubDepartment(subDepartment.getName());
				}
				Department department = subDepartment.getDepartment();
				if(department!=null) {
					letterVO.setDepartment(department.getName());
				}
				*/
				Status status = discussionmotion.getInternalStatus();	
				String statusType=status.getType();
				if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_REJECTION) || statusType.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)) {
					if(discussionmotion.getRejectionReason()!=null && !discussionmotion.getRejectionReason().isEmpty()) {
						formattedText = discussionmotion.getRejectionReason();//FormaterUtil.formatNumbersInGivenText(discussionmotion.getRejectionReason(), discussionmotion.getLocale());
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
				
	/*			*//** factual position (clarification) received from department **//*
				if(discussionmotion.getFactualPosition()!=null) {
					formattedText = discussionmotion.getFactualPosition();//FormaterUtil.formatNumbersInGivenText(discussionmotion.getFactualPosition(), discussionmotion.getLocale());
					if(formattedText.endsWith("<br><p></p>")) {
						formattedText = formattedText.substring(0, formattedText.length()-11);
					} else if(formattedText.endsWith("<p></p>")) {
						formattedText = formattedText.substring(0, formattedText.length()-7);
					}
					letterVO.setFactualPosition(formattedText);
				} else {
					letterVO.setFactualPosition("");
				} */
				
				/**** populating fields for half-hour discussion (common for standalone & from discussionmotion) ****//*
				if(discussionmotion.getRevisedReason()!=null && !discussionmotion.getRevisedReason().isEmpty()) {
					formattedText = discussionmotion.getRevisedReason();
				} else if(discussionmotion.getReason()!=null) {
					formattedText = discussionmotion.getReason();					
				} else {
					formattedText = "";
				}*/
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, locale.toString());
				if(formattedText.endsWith("<br><p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-11);
				} else if(formattedText.endsWith("<p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-7);
				}
				letterVO.setReason(formattedText);
				
	/*			if(discussionmotion.getRevisedBriefExplanation()!=null && !discussionmotion.getRevisedBriefExplanation().isEmpty()) {
					formattedText = discussionmotion.getRevisedBriefExplanation();
				} else if(discussionmotion.getBriefExplanation()!=null) {
					formattedText = discussionmotion.getBriefExplanation();
				} else {
					formattedText = "";					
				}*/
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, locale.toString());
				if(formattedText.endsWith("<br><p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-11);
				} else if(formattedText.endsWith("<p></p>")) {
					formattedText = formattedText.substring(0, formattedText.length()-7);
				}
				letterVO.setBriefExplanation(formattedText);
				
				if(discussionmotion.getDiscussionDate()!=null) {
					letterVO.setDiscussionDate(FormaterUtil.formatDateToString(discussionmotion.getDiscussionDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString()));
				}

				if(statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER;
				}else if(statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT;
				}
				/*
				if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
					String questionsAsked = discussionmotion.getDiscussionMotionsAskedInFactualPosition();
					if(questionsAsked==null) {
						if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentDiscussionMotions");
						} else if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromMemberDiscussionMotions");
						}
					} else if(questionsAsked.isEmpty()) {
						if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentDiscussionMotions");
						} else if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_clarificationFromMemberDiscussionMotions");
						}
					}
					if(questionsAsked!=null && !questionsAsked.isEmpty()) {
						List<MasterVO> questionsAskedForClarification = new ArrayList<MasterVO>();
						StringBuffer questionIndexesForClarification=new StringBuffer();	
						String allDiscussionMotions = "";
						if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							allDiscussionMotions = session.getParameter(deviceType.getType().trim()+"_clarificationFromDepartmentDiscussionMotions");
						} else if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
							allDiscussionMotions = session.getParameter(deviceType.getType().trim()+"_clarificationFromMemberDiscussionMotions");
						}								
						for(String questionAsked : questionsAsked.split("##")) {
							MasterVO questionAskedForClarification = new MasterVO();
							questionAskedForClarification.setValue(questionAsked);
							questionsAskedForClarification.add(questionAskedForClarification);
							int index = 1;
							for(String allDiscussionMotion : allDiscussionMotions.split("##")) {
								if(questionAsked.equals(allDiscussionMotion)) {
									questionIndexesForClarification.append("(");
									questionIndexesForClarification.append(FormaterUtil.formatNumberNoGrouping(index, discussionmotion.getLocale()));
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
						letterVO.setDiscussionMotionIndexesForClarification(questionIndexesForClarification.toString());
						letterVO.setDiscussionMotionsAskedForClarification(questionsAskedForClarification);
					}
				}
				*/
				String statusTypeSplit = statusType.split("_")[statusType.split("_").length-1];

				//				if(statusType.equals("admission")
				//						|| statusType.equals("rejection")) {
				//					WorkflowActor putupActor = WorkflowConfig.findFirstActor(discussionmotion, status, locale.toString());
				//					if(putupActor!=null) {
				//						String putupActorUsergroupName = putupActor.getUserGroupType().getName();
				//						DiscussionMotionDraft putupDraft = DiscussionMotion.findPutupDraft(question.getId(), "question_recommend_"+statusType, putupActorUsergroupName);				
				//						if(putupDraft!=null) {
				//							letterVO.setRemarks(putupDraft.getRemarks());
				//						}
				//					}
				//				}		
				
				/**** In case username is required ****/
				Role role = Role.findByFieldName(Role.class, "type", "SMOIS_PRINCIPAL_SECRETARY", locale.toString());
				List<User> users = User.findByRole(false, role.getName(), locale.toString());
				//as principal secretary for starred discussionmotion is only one, so user is obviously first element of the list.
				letterVO.setUserName(users.get(0).findFirstLastName());

				/**** generate report ****/				
				try {
					String reportFileName = "intimationletter";
					if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
						reportFileName += "_laq";
					} else if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
						reportFileName += "_lcq";
					}
					reportFileName += "(" + discussionmotion.getNumber() + ")";
					if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
						
						reportFileName += "_clarification(department)";
						
					} else if(statusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
						
						if(intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)) {
							reportFileName += "_clarification(department)";
						} else {
							reportFileName += "_clarification(member)";
						}						
						
					} else {
						reportFileName += "_" + statusTypeSplit;
					}
					if(intimationLetterFilter!=null && !intimationLetterFilter.isEmpty() && !intimationLetterFilter.equals("-")) {
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+intimationLetterFilter+"_"+statusTypeSplit + "_" + discussionmotion.getHouseType().getType(), "WORD", reportFileName, locale.toString());
					}else {
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit + "_" + discussionmotion.getHouseType().getType(), "WORD", reportFileName, locale.toString());
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
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.intimationLetter.noDiscussionMotionFound", locale.toString());
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
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/admissionreport", method=RequestMethod.GET)
	public String admittedAndToBeAdmittedReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "discussionmotion/error";
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
				page = "discussionmotion/reports/admissionreport";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/halfhourdaysubmitreport", method=RequestMethod.GET)
	public String halfhourDayWiseSubmissionReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "discussionmotion/error";
		try{
			String strGroupId = request.getParameter("groupId");
			String strSubDepartment = request.getParameter("subDepartment");
			String strDay = request.getParameter("days");
			
			if(strGroupId != null && !strGroupId.isEmpty()
					&& strSubDepartment != null && !strSubDepartment.isEmpty()
					&& strDay != null && !strDay.isEmpty()){
								
				List<Object> objects = DiscussionMotionReportHelper.getSesionAndDeviceType(request, locale.toString());
				
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
				
				List report = Query.findReport("SMOIS_HALFHOUR_DAYWISE_SUBMISSION_REPORT", parameters);
				
				model.addAttribute("report", report);
				model.addAttribute("formater", new FormaterUtil());
				model.addAttribute("locale", locale.toString());
				
				page = "discussionmotion/reports/halfhourdaywisesubmitreport";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/halfhourdaysubmitreportdatefilter", method=RequestMethod.GET)
	public String halfhourDayWiseSubmissionReportDateFilter(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "discussionmotion/error";
		try{
			String strGroupId = request.getParameter("groupId");
			String strSubDepartment = request.getParameter("subDepartment");
			String strDay = request.getParameter("subdate");
			
			if(strGroupId != null && !strGroupId.isEmpty()
					&& strSubDepartment != null && !strSubDepartment.isEmpty()
					&& strDay != null && !strDay.isEmpty()){
								
				List<Object> objects = DiscussionMotionReportHelper.getSesionAndDeviceType(request, locale.toString());
				
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
				
				List report = Query.findReport("SMOIS_HALFHOUR_DAYWISE_SUBMISSION_REPORT_DATE_FILTER", parameters);
				
				model.addAttribute("report", report);
				model.addAttribute("formater", new FormaterUtil());
				model.addAttribute("locale", locale.toString());
				
				page = "discussionmotion/reports/halfhourdaywisesubmitreport";
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
		List<Object> objects = DiscussionMotionReportHelper.getSesionAndDeviceType(request, locale.toString());
		if(!objects.isEmpty()){
			session = (Session) objects.get(0);
			deviceType = (DeviceType)objects.get(1);
		}
		parameters.put("locale", new String[]{locale.toString()});
		parameters.put("sessionId", new String[]{session.getId().toString()});
		parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		List headerStatReport = Query.findReport("SMOIS_HALFHOUR_STAT_REPORT", parameters);
		List admissionReport = Query.findReport("SMOIS_HALFHOUR_ADMISSION_REPORT", parameters);
		
		model.addAttribute("headerStats", headerStatReport);
		model.addAttribute("report", admissionReport);
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale.toString());
		CustomParameter csptShowHDAdmissionDetails = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_ADMISSION_DETAILS_" + session.getHouse().getType().getType().toUpperCase(), "");
		if(csptShowHDAdmissionDetails != null){
			model.addAttribute("showStats", csptShowHDAdmissionDetails.getValue());
		}
		return "discussionmotion/reports/hd_statusreport";
	}	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/shortnoticeanswerdatereport", method=RequestMethod.GET)
	public String getShortNoticeAnswerDateReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "discussionmotion/reports/error"; 
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
				List report = Query.findReport("DISCUSSIONMOTIONS_SHORTNOTICE_ANSWERING_REPORT", params);
				model.addAttribute("report", report);
			}
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("errorcode", "insufficient_parameters");
		}

		response.setContentType("text/html; charset=utf-8");
		return "discussionmotion/reports/shortnoticeanswer";
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
		
		return "discussionmotion/reports/"+request.getParameter("reportout");		
	}
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/smois/generalreport", method=RequestMethod.GET)
	public String getSMOISReport(HttpServletRequest request, Model model, Locale locale){
		
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
		
		return "discussionmotion/reports/"+request.getParameter("reportout");		
	}
	//----------------------------------------------------------------------

		
	@RequestMapping(value="/generateClubbedIntimationLetter/getClubbedDiscussionMotions", method=RequestMethod.GET)
	public String getClubbedDiscussionMotionsForIntimationReport(HttpServletRequest request, ModelMap model) {
		String retVal = "discussionmotion/reports/error";
		String discussionmotionId = request.getParameter("discussionmotionId");
		if(discussionmotionId!=null && !discussionmotionId.isEmpty()) {
			DiscussionMotion discussionmotion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(discussionmotionId));
			if(discussionmotion!=null) {
				model.addAttribute("discussionmotionId", discussionmotionId);
				List<ClubbedEntity> clubbedEntities = DiscussionMotion.findClubbedEntitiesByPosition(discussionmotion, ApplicationConstants.DESC);
				if(clubbedEntities!=null && !clubbedEntities.isEmpty()) {
					List<Reference> nameClubbedDiscussionMotionVOs = new ArrayList<Reference>();
					for(ClubbedEntity ce: clubbedEntities) {
						DiscussionMotion clubbedDiscussionMotion = ce.getDiscussionMotion();
						if(DiscussionMotion.isAdmittedThroughClubbing(clubbedDiscussionMotion)) {							
							Reference nameClubbedDiscussionMotionVO = new Reference();
							nameClubbedDiscussionMotionVO.setId(clubbedDiscussionMotion.getId().toString());
							nameClubbedDiscussionMotionVO.setNumber(FormaterUtil.formatNumberNoGrouping(clubbedDiscussionMotion.getNumber(), clubbedDiscussionMotion.getLocale()));
							nameClubbedDiscussionMotionVOs.add(nameClubbedDiscussionMotionVO);
						}
					}
					if(!nameClubbedDiscussionMotionVOs.isEmpty()) {
						model.addAttribute("nameClubbedDiscussionMotionVOs", nameClubbedDiscussionMotionVOs);
						retVal = "discussionmotion/reports/getClubbedDiscussionMotions";
					} else {
						model.addAttribute("errorcode", "noNameClubbedEntitiesFound");
					}					
				} else {
					model.addAttribute("errorcode", "noClubbedEntitiesFound");
				}
			} else {
				model.addAttribute("errorcode", "invalidDiscussionMotion");
			}			
		}
		return retVal;
	}
	
	@RequestMapping(value="/generateClubbedIntimationLetter", method=RequestMethod.GET)
	public @ResponseBody void generateClubbedIntimationLetter(HttpServletRequest request, final HttpServletResponse response, final ModelMap model, final Locale locale) {
		File reportFile = null;		
		boolean isError = false;
		MessageResource errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.errorMessage", locale.toString());
		
		String strDiscussionMotionId = request.getParameter("discussionmotionId");
		String strClubbedDiscussionMotions = request.getParameter("clubbedDiscussionMotions");
		String outputFormat = request.getParameter("outputFormat");
		
		if(strDiscussionMotionId!=null && strClubbedDiscussionMotions!=null && outputFormat!=null){
			if((!strDiscussionMotionId.isEmpty()) && !strClubbedDiscussionMotions.isEmpty() && !outputFormat.isEmpty()){
				try {
					/**** Generate & Process Report Data ****/			
					DiscussionMotion discussionmotion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(strDiscussionMotionId));
					if(discussionmotion!=null) {
						/**** primary discussionmotion data****/
						Map<String, String[]> queryParameters = new HashMap<String, String[]>();					
						queryParameters.put("discussionmotionId", new String[]{discussionmotion.getId().toString()});
						queryParameters.put("locale", new String[]{discussionmotion.getLocale()});
						@SuppressWarnings("unchecked")
						List<Object> primaryDiscussionMotionData = Query.findReport("SMOIS_CLUBBEDINTIMATIONLETTER_PARENTDISCUSSIONMOTIONDATA", queryParameters);
						String houseType = discussionmotion.getHouseType().getType();
						/**** find all member names for the discussionmotion ****/					
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
							allMemberNames = discussionmotion.findAllMemberNamesWithConstituencies(memberNameFormat);
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
							memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CLUBBEDINTIMATIONLETTER_MEMBERNAMEFORMAT_UPPERHOUSE", "");
							if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
								memberNameFormat = memberNameFormatParameter.getValue();
							} else {
								memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
							}	
							allMemberNames = discussionmotion.findAllMemberNames(memberNameFormat);
						}	
						/**** find last member name before first added discussionmotion ****/
						String lastMemberNameBeforeAddedDiscussionMotions = "";
						StringBuffer previousDiscussionMotionsMemberNames = new StringBuffer();
						String[] strClubbedDiscussionMotionsArr = strClubbedDiscussionMotions.split(",");
						String firstAddedDiscussionMotionId = strClubbedDiscussionMotionsArr[strClubbedDiscussionMotionsArr.length-1];
						DiscussionMotion firstAddedDiscussionMotion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(firstAddedDiscussionMotionId));
						
						String firstAddedDiscussionMotionMemberName = firstAddedDiscussionMotion.getPrimaryMember().findNameInGivenFormat(memberNameFormat);
						if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
							String constituencyName = firstAddedDiscussionMotion.getPrimaryMember().findConstituencyNameForYadiReport(firstAddedDiscussionMotion.getSession().getHouse(), "DATE", new Date(), new Date());
							if(!constituencyName.isEmpty()) {
								firstAddedDiscussionMotionMemberName += " (" + constituencyName + ")";
							}
						}
						if(allMemberNames!=null && !allMemberNames.isEmpty()) {
							String[] allMemberNamesArr = allMemberNames.split(",");
							for(int i=0; i<allMemberNamesArr.length; i++) {
								if(allMemberNamesArr[i].trim().equals(firstAddedDiscussionMotionMemberName.trim())) {
									lastMemberNameBeforeAddedDiscussionMotions = allMemberNamesArr[i-1].trim();
									for(int j=0; j<i; j++) {
										if(j!=i-1) {
											previousDiscussionMotionsMemberNames.append(allMemberNamesArr[j].trim() + ", ");
										} else {
											previousDiscussionMotionsMemberNames.append(allMemberNamesArr[j].trim());
										}									
									}
									break;
								}
							}
							/**** clubbed questions data ****/							
							List<Object> clubbedDiscussionMotionData = new ArrayList<Object>();
							StringBuffer clubbedMemberNames = new StringBuffer();
							for(int k=strClubbedDiscussionMotionsArr.length-1; k>=0; k--) {
								DiscussionMotion clubbedDiscussionMotion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(strClubbedDiscussionMotionsArr[k]));
								queryParameters.put("questionId", new String[]{clubbedDiscussionMotion.getId().toString()});
								queryParameters.put("locale", new String[]{clubbedDiscussionMotion.getLocale()});
								clubbedDiscussionMotionData.add(Query.findReport("SMOIS_CLUBBEDINTIMATIONLETTER_CLUBBEDQUESTIONDATA", queryParameters));
								if(clubbedDiscussionMotionData!=null && !clubbedDiscussionMotionData.isEmpty()) {
									if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
										clubbedMemberNames.append(DiscussionMotionReportHelper.findMemberNamesForAddedDiscussionMotion(clubbedDiscussionMotion, previousDiscussionMotionsMemberNames.toString(), memberNameFormat, true));
									} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
										clubbedMemberNames.append(DiscussionMotionReportHelper.findMemberNamesForAddedDiscussionMotion(clubbedDiscussionMotion, previousDiscussionMotionsMemberNames.toString(), memberNameFormat, false));
									}		
									if(clubbedMemberNames!=null && !clubbedMemberNames.toString().isEmpty()) {
										if(k!=0) {
											previousDiscussionMotionsMemberNames.append(clubbedMemberNames + ", ");
										}								
									}														
								}
							}							
							
							/**** generate report ****/
							if(!isError) {
								reportFile = generateReportUsingFOP(new Object[]{primaryDiscussionMotionData, lastMemberNameBeforeAddedDiscussionMotions, clubbedDiscussionMotionData, clubbedMemberNames.toString()}, "question_clubbedIntimationLetter", outputFormat, "question_clubbedIntimationLetter", locale.toString());
								if(reportFile!=null) {
									System.out.println("Report generated successfully in " + outputFormat + " format!");
									openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
								}
							}
						} else {
							logger.error("**** No member found for the discussionmotion ****");
							isError = true;
							errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.intimationLetter.noMemberFound", locale.toString());
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
	@RequestMapping(value="/memberwise_discussionmotions",method=RequestMethod.GET)
	public String viewMemberWiseReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			List<Reference> eligibleMembers = new ArrayList<Reference>();
			
			String category = request.getParameter("category");
			String strMotionType = request.getParameter("motionType");
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strGroup = request.getParameter("group");
			String strAnsweringDate = request.getParameter("answeringDate");
			
			if(category!=null&&strMotionType!=null&&strHouseType!=null && strSessionType!=null && strSessionYear!=null){
				if((!category.isEmpty())&&(!strMotionType.isEmpty())&&!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()){										
					DeviceType motionType=DeviceType.findById(DeviceType.class,Long.parseLong(strMotionType));
					if(motionType==null) {
						logger.error("**** DeviceType Not Found ****");
						model.addAttribute("type", "MOTIONTYPE_NOTFOUND");
						return errorpage;
					}
					model.addAttribute("motionType",motionType.getId());
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}
					model.addAttribute("houseType", houseType.getId());
					model.addAttribute("houseTypeType", houseType.getType());
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
					/**** find all members from given house which can submit standalone motions ****/
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
					List resultList = Query.findReport("MEMBERS_ELIGIBLE_FOR_DISCUSSIONMOTION_SUBMISSION_IN_GIVEN_HOUSE", queryParameters);
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
					logger.error("**** Check request parameter 'session,motionType' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,motionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "discussionmotion/reports/memberwise_discussionmotions";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/memberwise_discussionmotions/motions",method=RequestMethod.GET)
	public String viewMemberWiseMotionsReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strMember = request.getParameter("member");
			String strMotionType = request.getParameter("motionType");
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSession = request.getParameter("session");
			String groups = request.getParameter("groups");
			String status = request.getParameter("status");
			
			if(strMember!=null && strMotionType!=null && strSession!=null){
				if(!strMember.isEmpty() && !strMotionType.isEmpty()&& !strSession.isEmpty()){					
					/**** find all motions of members submitted in given session ****/
					Map<String, String[]> queryParameters = new HashMap<String, String[]>();
					queryParameters.put("sessionId", new String[]{strSession});
					queryParameters.put("memberId", new String[]{strMember});
					queryParameters.put("motionTypeId", new String[]{strMotionType});
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
					List resultList = Query.findReport("SMOIS_MEMBERWISE_MOTIONS", queryParameters);
					if(resultList!=null && !resultList.isEmpty()) {													
						model.addAttribute("memberwiseMotions", resultList);
						model.addAttribute("formatter", new FormaterUtil());
						model.addAttribute("locale", locale.toString());
					} else {
						//error
					}
				}else{
					logger.error("**** Check request parameter 'member,session,motionType' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'member,session,motionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "discussionmotion/reports/memberwise_discussionmotions_data";
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
							
							String queryName = "SMOIS_BULLETEIN_REPORT";
							
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
									queryName = "SMOIS_BULLETEIN_GROUPWISE_REPORT";
								}
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
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}
			}else{
				isError = true;
				logger.error("**** Check request parameters houseType, sessionType, sessionYear for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.bulleteinReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters houseType, sessionType, sessionYear for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.bulleteinReport.reqparam.null", locale.toString());
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
								DeviceType starredDiscussionMotionType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale.toString());
								queryParameters.put("starredDiscussionMotionTypeId", new String[]{starredDiscussionMotionType.getId().toString()});
							}							
							DeviceType unstarredDiscussionMotionType = DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, locale.toString());
							queryParameters.put("unstarredDiscussionMotionTypeId", new String[]{unstarredDiscussionMotionType.getId().toString()});
							queryParameters.put("locale", new String[]{locale.toString()});
							String queryName= "SMOIS_" + questionType.toUpperCase() + "_DEPARTMENTWISE_STATS_"+houseType.getType().toUpperCase()+"_REPORT";														
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
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.bulleteinReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters houseType, sessionType, sessionYear for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.bulleteinReport.reqparam.null", locale.toString());
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
							String queryName= "QIS_AHWAL_HDS_CONDITION_REPORT";													
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
										reportFile = generateReportUsingFOP(new Object[]{reportData,serialNumbers,localisedContent,questionType}, "qis_ahwal_hdcondition_"+houseType.getType(), "WORD", "qis_ahwal_halfHourFromDiscussionMotion_Report", locale.toString());
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
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.bulleteinReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters houseType, sessionType, sessionYear for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "discussionmotion.bulleteinReport.reqparam.null", locale.toString());
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
	public String getDepartmentwiseDiscussionMotionReport(HttpServletRequest request, Model model, Locale locale){
		
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
						model.addAttribute("localisedContent", obj[0].toString().split("~#"));
					}
					if(obj[8]!=null) {
						String[] clubbedDiscussionMotionNumbers = obj[8].toString().split(",");
						StringBuffer clubbedNumbers = new StringBuffer();
						clubbedNumbers.append("(");
						for(int i=0; i<clubbedDiscussionMotionNumbers.length; i++) {
							if(i!=0 && i%3==0) {
								clubbedNumbers.append("<br/>");
							} 
							clubbedNumbers.append(clubbedDiscussionMotionNumbers[i]);
							if(i!=clubbedDiscussionMotionNumbers.length-1) {
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
		
		return "discussionmotion/reports/"+request.getParameter("reportout");		
	}//showTabByIdAndUrl('details_tab', 'discussionmotion/report/sankshiptAhwal');
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/sankshiptAhwal", method=RequestMethod.GET)
	public String getSankshiptAhwalInit(HttpServletRequest request, Model model, Locale locale){	
		String selectedHouseType = request.getParameter("selectedHouseType");
		if(selectedHouseType==null || selectedHouseType.isEmpty()) {
			model.addAttribute("errorcode", "insufficient_parameters");
			return "discussionmotion/reports/error";
		} 
		model.addAttribute("selectedHouseType", selectedHouseType);
		return "discussionmotion/reports/sankshiptahwalinit";
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/departmentwisequestions/export", method=RequestMethod.GET)
	public void exportDepartmentwiseAdmittedDiscussionMotionReport(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale){
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
						String[] clubbedDiscussionMotionNumbers = obj[8].toString().split(",");
						StringBuffer clubbedNumbers = new StringBuffer();
						clubbedNumbers.append("(");
						for(int i=0; i<clubbedDiscussionMotionNumbers.length; i++) {
							if(i!=0 && i%3==0) {
								clubbedNumbers.append("<br>");
							} 
							clubbedNumbers.append(clubbedDiscussionMotionNumbers[i]);
							if(i!=clubbedDiscussionMotionNumbers.length-1) {
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
			
			/**** generate report ****/
			if(!isError) {
				try {
					reportFile = generateReportUsingFOP(new Object[]{report,localisedContent, clubbedNumbersList, selectedAnsweringDate}, "qis_departmentwise_questions", request.getParameter("outputFormat"), "qis_departmentwisequestions", locale.toString());
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
	
	@RequestMapping(value="/online_offline_submission_count_report/init",method=RequestMethod.GET)
	public String initOnlineOfflineSubmissionCountReport(final HttpServletRequest request,final ModelMap model,final Locale locale) throws ELSException{
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String responsePage="discussionmotion/reports/error";
		
		String strDiscussionMotionType = request.getParameter("questionType");
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		
		if(strDiscussionMotionType!=null && strHouseType!=null && strSessionType!=null && strSessionYear!=null
				&& !strDiscussionMotionType.isEmpty() && !strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()) {
			
			/**** populate selected questiontype ****/
			DeviceType questionType=DeviceType.findByName(DeviceType.class, strDiscussionMotionType, locale.toString());
			if(questionType==null) {
				questionType=DeviceType.findByType(strDiscussionMotionType, locale.toString());
			}
			if(questionType==null) {
				questionType=DeviceType.findById(DeviceType.class, Long.parseLong(strDiscussionMotionType));
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
			/**** populate default from date and default to date ****/
			Date defaultFromDate = null;
			Date defaultToDate = null;
			/**** submission start date of the session as default fromDate ****/
			String submissionStartDateSessionParameter = session.getParameter(questionType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
			if(submissionStartDateSessionParameter==null || submissionStartDateSessionParameter.isEmpty()) {
				logger.error("**** Submission start date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_undefined_for_session");
				return responsePage;				
			}
			
			defaultFromDate = FormaterUtil.formatStringToDate(submissionStartDateSessionParameter, ApplicationConstants.DB_DATEFORMAT);
			if(defaultFromDate==null)  {
				logger.error("**** Submission start date parameter is set to invalid value for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_invalid_for_session");
				return responsePage;
			}
			/**** submission end date of the session as default toDate ****/
			String submissionEndDateSessionParameter = session.getParameter(questionType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
			if(submissionEndDateSessionParameter==null || submissionEndDateSessionParameter.isEmpty()) {
				logger.error("**** Submission end date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_end_date_parameter_undefined_for_session");
				return responsePage;
			}
			defaultToDate = FormaterUtil.formatStringToDate(submissionEndDateSessionParameter, ApplicationConstants.DB_DATEFORMAT);
			if(defaultToDate==null)  {
				logger.error("**** Submission end date parameter is set to invalid value for the session ****");
				model.addAttribute("errorcode", "submission_end_date_parameter_invalid_for_session");
				return responsePage;
			}			
			model.addAttribute("defaultFromDate", FormaterUtil.formatDateToString(defaultFromDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			model.addAttribute("defaultToDate", FormaterUtil.formatDateToString(defaultToDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			/**** Check whether current date is allowed for submission ****/
			Calendar currentDateCalendar = Calendar.getInstance();
			currentDateCalendar.setTime(new Date());	
//			currentDateCalendar.set(2015, 6, 12);
			currentDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
			currentDateCalendar.set(Calendar.MINUTE, 0);
			currentDateCalendar.set(Calendar.SECOND, 0);
			currentDateCalendar.set(Calendar.MILLISECOND, 0);
			if(	
				(defaultFromDate.before(currentDateCalendar.getTime()) || defaultFromDate.equals(currentDateCalendar.getTime()))
						&&
				(defaultToDate.after(currentDateCalendar.getTime()) || defaultToDate.equals(currentDateCalendar.getTime()))
			) {
				model.addAttribute("isCurrentDateValidForSubmission", true);
			} else {
				model.addAttribute("isCurrentDateValidForSubmission", false);
			}
			responsePage = "discussionmotion/reports/online_offline_submission_count_report_init";
		} else {
			logger.error("**** Check request parameters 'questionType,houseType,sessionType,sessionYear' for null/empty values ****");
			model.addAttribute("errorcode", "insufficient_parameters");
		}
				
		return responsePage;
	}
	
	@Transactional
	@RequestMapping(value="/online_offline_submission_count_report",method=RequestMethod.GET)
	public @ResponseBody void generateOnlineOfflineSubmissionCountReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale) throws ELSException{
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
				Session sessionObj = Session.findById(Session.class, Long.parseLong(session));
				/**** set fromDate & toDate ****/
				Date fromDate = null;
				Date toDate = null;
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
						fromDate = FormaterUtil.formatStringToDate(fromDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
						fromDateStr = FormaterUtil.formatDateToString(fromDate, ApplicationConstants.DB_DATEFORMAT);
						//format toDate for query
						toDate = FormaterUtil.formatStringToDate(toDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
						toDateStr = FormaterUtil.formatDateToString(toDate, ApplicationConstants.DB_DATEFORMAT);
					}
					//submission status as per devicetype
					Status submitStatus = Status.findByType(ApplicationConstants.STANDALONE_SUBMIT, locale.toString());					
					DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(questionType));
					if(submitStatus!=null) {
						Map<String, String[]> queryParameters = new HashMap<String, String[]>();
						queryParameters.put("locale", new String[] {locale.toString()});
						queryParameters.put("sessionId", new String[] {session});
						queryParameters.put("questionTypeId", new String[] {questionType});
						queryParameters.put("houseTypeId", new String[] {houseType});												
						queryParameters.put("fromDate", new String[] {fromDateStr});
						queryParameters.put("toDate", new String[] {toDateStr});
						queryParameters.put("submitStatusId", new String[] {submitStatus.getId().toString()});
						
						String queryName = "SMOIS_MEMBERWISE_DISCUSSIONMOTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						if(criteria.equals("datewise")) {	
							synchronized (Session.class) {								
								boolean isSubmissionDatesForSessionLoaded = Session.loadSubmissionDatesForDeviceTypeInSession(sessionObj, deviceType, fromDate, toDate);
								if(isSubmissionDatesForSessionLoaded==false) {
									//error
									isError = true;	
								}
							}							
							queryName = "SMOIS_DATEWISE_DISCUSSIONMOTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						}						
						List reportData = Query.findReport(queryName, queryParameters);
						if(reportData!=null && !reportData.isEmpty()) {
							/**** generate report ****/
							if(!isError) {								
								List<String> serialNumbers = populateSerialNumbers(reportData, locale);
								reportFile = generateReportUsingFOP(new Object[]{reportData, criteria, serialNumbers}, "discussionmotions_online_submission_counts_template", "WORD", deviceType.getType()+"_"+criteria+"_online_submission_counts_report", locale.toString());				
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
}

/**** Helper for producing reports ****/
class DiscussionMotionReportHelper{
	
	private static Logger logger = LoggerFactory.getLogger(DiscussionMotionReportHelper.class);
	
	@SuppressWarnings("rawtypes")
	public static String getCurrentStatusReportData(final Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "discussionmotion/error";
		try{
			
			
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("locale", new String[]{locale.toString()});
			params.put("DmotionId", new String[]{id.toString()});
			
			List report = Query.findReport(request.getParameter("reportOut"), params);
			
			model.addAttribute("data", report);
			
			DiscussionMotion m = DiscussionMotion.findById(DiscussionMotion.class, id);
			page = (m.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "discussionmotion/reports/statusreportcurrentstatuslowerhouse": "discussionmotion/reports/statusreportcurrentstatusupperhouse";
	
			
//			String strDevice = request.getParameter("device"); 
//			if(strDevice != null && !strDevice.isEmpty()){
//				DiscussionMotion qt = DiscussionMotion.findById(DiscussionMotion.class, id);
//				List report = generatetCurrentStatusReport(qt, strDevice, locale.toString());				
//				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();	
//				if(report != null && !report.isEmpty()){
//										
//					List<User> users = User.findByRole(false, "DMOIS_PRINCIPAL_SECRETARY", locale.toString());
//					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
//	
//					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)? "DMOIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "DMOIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
//					if(csptAllwedUserGroupForStatusReportSign != null){
//						if(csptAllwedUserGroupForStatusReportSign.getValue() != null && !csptAllwedUserGroupForStatusReportSign.getValue().isEmpty()){
//							Object[] lastObject = (Object[]) report.get(report.size()-1); 
//							for(Object o : report){
//								Object[] objx = (Object[])o;
//	
//								if(objx[21] != null && !objx[21].toString().isEmpty()){
//									if(csptAllwedUserGroupForStatusReportSign.getValue().contains(objx[21].toString())){
//										
//										UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", objx[21].toString(), locale.toString());
//																				
//										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
//											if(dataMap.get(ApplicationConstants.UNDER_SECRETARY) != null){
//												if(objx != null){
//													if(objx[6] != null && objx[6].toString().length() > 0){
//														dataMap.put(ApplicationConstants.UNDER_SECRETARY, objx);
//													}else{
//														Object[] tempObj = dataMap.get(ApplicationConstants.UNDER_SECRETARY);
//														tempObj[22] = objx[22];
//														
//														dataMap.put(ApplicationConstants.UNDER_SECRETARY, tempObj);
//													}
//												}
//											}else {
//												if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)) {
//													dataMap.put(ApplicationConstants.UNDER_SECRETARY_COMMITTEE, objx);
//												}										
//												else if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)) {
//													dataMap.put(ApplicationConstants.UNDER_SECRETARY, objx);
//												}
//											}
//										}else{
//											if(dataMap.get(userGroupType.getType()) != null){
//												if(objx != null){
//													if(objx[6] != null && objx[6].toString().length() > 0){
//														dataMap.put(userGroupType.getType(), objx);
//													}else{
//														Object[] tempObj = dataMap.get(userGroupType.getType());
//														tempObj[22] = objx[22];
//														
//														dataMap.put(userGroupType.getType(), tempObj);
//													}
//												}
//												
//											}else{
//												dataMap.put(userGroupType.getType(), objx);
//											}
//										}
//									}
//								}
//							}
//							
//							//Following block is added for solving the issue of discussionmotion drafts where in if there exist a draft and later the discussionmotion is pending
//							// at the specific actor, the last remark is displayed
//							WorkflowConfig wfConfig = WorkflowConfig.getLatest(qt, qt.getInternalStatus().getType(), locale.toString());
//							
////							WorkflowConfig wfConfig = null;
////							CustomParameter csptCurrentStatusAllowedBeforeApproval = CustomParameter.findByName(CustomParameter.class, qt.getType().getType().toUpperCase()+"_"+qt.getHouseType().getType().toUpperCase()+"_CURRENT_STATUS_REPORT_ALLOWED_BEFORE_APPROVAL", "");
////							if(csptCurrentStatusAllowedBeforeApproval!=null && csptCurrentStatusAllowedBeforeApproval.getValue()!=null
////									&& csptCurrentStatusAllowedBeforeApproval.getValue().equals("YES")) {
////								
////								wfConfig = WorkflowConfig.getLatest(qt, ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_ADMISSION, locale.toString());
////								model.addAttribute("currentStatusReportAllowedBeforeApproval", "YES");
////								
////							} else {
////								wfConfig = WorkflowConfig.getLatest(qt, qt.getInternalStatus().getType(), locale.toString());
////								model.addAttribute("currentStatusReportAllowedBeforeApproval", "NO");
////							}
//
//							
//							List<WorkflowActor> wfActors = wfConfig.getWorkflowactors();
//							List<WorkflowActor> distinctActors = new ArrayList<WorkflowActor>();
//							for(WorkflowActor wf : wfActors){
//								UserGroupType userGroupType = wf.getUserGroupType();
//								Boolean elementPresent = false;
//								for(WorkflowActor wf1 : distinctActors){
//									UserGroupType userGroupType1 = wf1.getUserGroupType();
//									if(userGroupType.getType().equals(userGroupType1.getType())){
//										elementPresent = true;
//										break;
//									}
//								}
//								if(!elementPresent){
//									distinctActors.add(wf);
//								}
//							}
//							Integer level = null;
//							WorkflowDetails  wfDetails = WorkflowDetails.findCurrentWorkflowDetail(qt);
//							for(WorkflowActor wf : distinctActors){
//								UserGroupType userGroupType = wf.getUserGroupType();
//								if(userGroupType.getType().equals(lastObject[21])){
//									if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
//										for(WorkflowActor wf1 : distinctActors){
//											UserGroupType ugt = wf1.getUserGroupType();
//											if(ugt.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
//												level = wf1.getLevel();
//											}
//										}
//									}else{
//										level = wf.getLevel();
//									}
//								}
//									
//								
//								if(level != null && wf.getLevel()>level){
//									if(dataMap.containsKey(userGroupType.getType())
//											&&
//										(wfDetails!= null && 
//										Integer.parseInt(wfDetails.getAssigneeLevel())>=level)){
//										Object[] tempObj = dataMap.get(userGroupType.getType());
//										tempObj[22] = "";
//										tempObj[6] = "";
//										dataMap.put(userGroupType.getType(), tempObj);
//									}
//								}
//							}
//							CustomParameter onPaperSigningAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "DMOIS_CURRENTSTATUS_ONPAPER_SIGNING_AUTHORITY_"+qt.getHouseType().getType(), "");
//							if(onPaperSigningAuthorityParameter != null){
//								String signingAuthority = onPaperSigningAuthorityParameter.getValue();
//								String[] signingAuthorities = signingAuthority.split(",");
//								for(String str : signingAuthorities){
//									if(str.equals(ApplicationConstants.UNDER_SECRETARY)){
//										str = ApplicationConstants.UNDER_SECRETARY;
//									} else if(str.equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)) {
//										str = ApplicationConstants.UNDER_SECRETARY_COMMITTEE;
//									}
//									if(dataMap.get(str) == null){
//										UserGroupType userGroupType = UserGroupType.
//												findByFieldName(UserGroupType.class, "type", str, locale.toString());
//										Reference ref = UserGroup.findDiscussionMotionActor(qt, str, String.valueOf(0), locale.toString());
//										if(ref.getId() != null){
//											Object[] actor = new Object[32];
//											if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) 
//													|| ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)
//													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY)){
//												actor[0] = new String(ref.getId().split("#")[3]);
//												actor[1] = new String(ref.getId().split("#")[4]);
//											}else{
//												actor[0] = new String(ref.getId().split("#")[3]);
//												actor[1] = new String("");
//											}
//											actor[3] = new String("");
//											actor[6] = new String("");
//											actor[21] = userGroupType.getType();
//											actor[22] = new String("");
//											actor[24] = null;
//											dataMap.put(str, actor);
//										}
//									}
//								}
//							}
////							if(dataMap.get(ApplicationConstants.SECRETARY) == null 
////									&& qt.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
////								UserGroupType userGroupType = UserGroupType.
////										findByFieldName(UserGroupType.class, "type", ApplicationConstants.SECRETARY, locale.toString());
////								
////								Object[] dataCollection = new Object[32];
////								dataCollection[0] = new String(userGroupType.getName());
////								dataCollection[1] = new String("");
////								dataCollection[3] = new String("");
////								dataCollection[6] = new String("");
////								dataCollection[27] = userGroupType.getType();
////								dataCollection[28] = new String("");
////								dataCollection[31] = null;
////								
////								dataMap.put(ApplicationConstants.SECRETARY, dataCollection);
////							}
////							
////							if(dataMap.get(ApplicationConstants.PRINCIPAL_SECRETARY) == null){
////								UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.PRINCIPAL_SECRETARY, locale.toString());
////								
////								Object[] dataCollection = new Object[32];
////								dataCollection[0] = new String(userGroupType.getName());
////								dataCollection[1] = new String("");
////								dataCollection[3] = new String("");
////								dataCollection[6] = new String("");
////								dataCollection[27] = userGroupType.getType();
////								dataCollection[28] = new String("");
////								dataCollection[31] = null;
////								
////								dataMap.put(ApplicationConstants.PRINCIPAL_SECRETARY, dataCollection);
////							}
//							
////							if(dataMap.isEmpty()){
////								for(String val : csptAllwedUserGroupForStatusReportSign.getValue().split(",")){
////								
////									Reference ref = UserGroup.findDiscussionMotionActor(qt, val, String.valueOf(0), locale.toString());
////									Object[] actor = new Object[30];
////									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
////										actor[0] = new String(ref.getId().split("#")[3]);
////										actor[1] = new String(ref.getId().split("#")[4]);
////									}else{
////										actor[0] = new String(ref.getId().split("#")[3]);
////										actor[1] = new String("");
////									}
////									actor[3] = new String("");
////									actor[6] = new String("");
////									actor[28] = new String("");
////									
////									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
////										dataMap.put(ApplicationConstants.UNDER_SECRETARY, actor);
////									}else{
////										dataMap.put(val, actor);
////									}
////								}
////							}
//							Map<String, Object[]> sortedDataMap = new LinkedHashMap<String, Object[]>();
//							for(WorkflowActor wfa:distinctActors){
//								UserGroupType ugt = wfa.getUserGroupType();
//								if(dataMap.containsKey(ugt.getType())){
//									sortedDataMap.put(ugt.getType(), dataMap.get(ugt.getType()));
//								}
//							}
//							model.addAttribute("data", sortedDataMap);
//							model.addAttribute("formatData", report.get(report.size()-1));
//						}
//					}
//	
//					page = (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "discussionmotion/reports/statusreportlowerhouse": "discussionmotion/reports/statusreportupperhouse";
//				}		
//	
//				model.addAttribute("qid", qt.getId());
//			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	public static List generatetCurrentStatusReport(final DiscussionMotion discussionmotion, final String device, final String locale){
		CustomParameter memberNameFormatParameter = null;
		if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
		} else if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_UPPERHOUSE", "");
		}
		String support = "";
		if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
			support = discussionmotion.findAllMemberNames(memberNameFormatParameter.getValue());
		} else {
			support = discussionmotion.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
		}		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("id",new String[]{discussionmotion.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("DMOIS_CURRENTSTATUS_REPORT", parameters);
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
	
	@SuppressWarnings("rawtypes")
	public static String getMemberDraftReportData(final Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "discussionmotion/error";
		try{
			String strDevice = request.getParameter("device"); 
			if(strDevice != null && !strDevice.isEmpty()){
				DiscussionMotion qt = DiscussionMotion.findById(DiscussionMotion.class, id);
				List report = generatetMemberDraftReport(qt, strDevice, locale.toString());				
				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();	
				if(report != null && !report.isEmpty()){
					for(Object o : report){
						Object[] objx = (Object[])o;
						dataMap.put(ApplicationConstants.MEMBER, objx);								
					}
					model.addAttribute("data", dataMap);
					model.addAttribute("formatData", report.get(report.size()-1));
					page = (qt.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "discussionmotion/reports/memberdraftreportlowerhouse": "discussionmotion/reports/memberdraftreportupperhouse";
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
	public static List generatetMemberDraftReport(final DiscussionMotion discussionmotion, final String device, final String locale){
		CustomParameter memberNameFormatParameter = null;
		if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
		} else if(discussionmotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "CURRENTSTATUSREPORT_MEMBERNAMEFORMAT_UPPERHOUSE", "");
		}
		String support = "";
		if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
			support = discussionmotion.findAllMemberNames(memberNameFormatParameter.getValue());
		} else {
			support = discussionmotion.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
		}		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("id",new String[]{discussionmotion.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("QIS_MEMBERDRAFT_REPORT", parameters);
		for(Object o : list){
			Object[] data = (Object[]) o;
			String subject = ((data[12] != null)? data[12].toString():"-");
			String details = ((data[13] != null)? data[13].toString():"-");
			
			((Object[])o)[7] = support;
			((Object[])o)[12] = FormaterUtil.formatNumbersInGivenText(subject, locale);
			((Object[])o)[13] = FormaterUtil.formatNumbersInGivenText(details, locale);
			
			/*try{
				if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
					Integer qNumber = new Integer(FormaterUtil.
							getNumberFormatterNoGrouping(locale.toString()).parse(question.getHalfHourDiscusionFromDiscussionMotionReferenceNumber()).intValue());
					Map<String, String[]> params = new HashMap<String, String[]>();
					params.put("locale", new String[]{locale.toString()});
					params.put("sessionId", new String[]{question.getSession().getId().toString()});
					params.put("qNumber", new String[]{qNumber.toString()});
					List data1 = Query.findReport("HDQ_REFER_QUESTION", params);
					DiscussionMotion referredDiscussionMotion = null;
					if(data1 != null && !data1.isEmpty()){
						String strId = ((Object[])data1.get(0))[0].toString();
						if(strId != null){
							referredDiscussionMotion = DiscussionMotion.findById(DiscussionMotion.class, new Long(strId));
						}
					}
					if(referredDiscussionMotion != null){
						((Object[])o)[36] = FormaterUtil.formatNumberNoGrouping(referredDiscussionMotion.getNumber(), locale);
						((Object[])o)[37] = referredDiscussionMotion.getType().getName();
						((Object[])o)[38] = referredDiscussionMotion.getPrimaryMember().findFirstLastName();
						if(referredDiscussionMotion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)){
							((Object[])o)[39] = FormaterUtil.formatDateToString(referredDiscussionMotion.getDiscussionDate(), ApplicationConstants.SERVER_DATEFORMAT, locale);
						}else{
							((Object[])o)[41] = FormaterUtil.formatNumberNoGrouping(referredDiscussionMotion.getYaadiNumber(), locale);
							((Object[])o)[40] = FormaterUtil.formatDateToString(referredDiscussionMotion.getYaadiLayingDate(), ApplicationConstants.SERVER_DATEFORMAT, locale);
						}
					}
				}
			}catch(Exception e){
				logger.error("error", e);
			}*/
			
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

	public static String findMemberNamesForAddedDiscussionMotion(DiscussionMotion clubbedDiscussionMotion, String previousDiscussionMotionsMemberNames, String memberNameFormat, boolean isConstituencyIncluded) {
		Session session = clubbedDiscussionMotion.getSession();
		House discussionmotionHouse = session.getHouse();
		Date currentDate = new Date();
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Member member = null;
		String memberName = "";
		String constituencyName = "";
		
		/** primary member **/
		member = clubbedDiscussionMotion.getPrimaryMember();		
		if(member==null) {
			return allMemberNamesBuffer.toString();
		}		
		memberName = member.findNameInGivenFormat(memberNameFormat);
		if(memberName!=null && !memberName.isEmpty() && !previousDiscussionMotionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
			allMemberNamesBuffer.append(memberName);
			if(isConstituencyIncluded) {
				constituencyName = member.findConstituencyNameForYadiReport(discussionmotionHouse, "DATE", currentDate, currentDate);
				if(!constituencyName.isEmpty()) {
					allMemberNamesBuffer.append(" (" + constituencyName + ")");			
				}
			}			
		} else {
			return allMemberNamesBuffer.toString();
		}				
		
		/** supporting members **/
		List<SupportingMember> supportingMembers = clubbedDiscussionMotion.getSupportingMembers();
		if (supportingMembers != null) {
			for (SupportingMember sm : supportingMembers) {
				member = sm.getMember();
				if(member!=null) {
					memberName = member.findNameInGivenFormat(memberNameFormat);
					if(memberName!=null && !memberName.isEmpty() && !previousDiscussionMotionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
						if(member.isSupportingOrClubbedMemberToBeAddedForDevice(clubbedDiscussionMotion)) {
							allMemberNamesBuffer.append(", " + memberName);
							if(isConstituencyIncluded) {
								constituencyName = member.findConstituencyNameForYadiReport(discussionmotionHouse, "DATE", currentDate, currentDate);
								if(!constituencyName.isEmpty()) {
									allMemberNamesBuffer.append(" (" + constituencyName + ")");						
								}
							}
						}						
					}									
				}				
			}
		}
		
		/** clubbed discussionmotions members **/
		List<ClubbedEntity> clubbedEntities = DiscussionMotion.findClubbedEntitiesByPosition(clubbedDiscussionMotion, ApplicationConstants.DESC);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed discussionmotions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getDiscussionMotion().getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_CLUBBED)
						|| ce.getDiscussionMotion().getInternalStatus().getType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)) {
					member = ce.getDiscussionMotion().getPrimaryMember();
					if(member!=null) {
						memberName = member.findNameInGivenFormat(memberNameFormat);
						if(memberName!=null && !memberName.isEmpty() && !previousDiscussionMotionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(member.isSupportingOrClubbedMemberToBeAddedForDevice(clubbedDiscussionMotion)) {
								allMemberNamesBuffer.append(", " + memberName);
								if(isConstituencyIncluded) {
									constituencyName = member.findConstituencyNameForYadiReport(discussionmotionHouse, "DATE", currentDate, currentDate);
									if(!constituencyName.isEmpty()) {
										allMemberNamesBuffer.append(" (" + constituencyName + ")");							
									}
								}
							}							
						}												
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getDiscussionMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							if(member!=null) {
								memberName = member.findNameInGivenFormat(memberNameFormat);
								if(memberName!=null && !memberName.isEmpty() && !previousDiscussionMotionsMemberNames.contains(memberName) && !allMemberNamesBuffer.toString().contains(memberName)) {
									if(member.isSupportingOrClubbedMemberToBeAddedForDevice(clubbedDiscussionMotion)) {
										allMemberNamesBuffer.append(", " + memberName);
										if(isConstituencyIncluded) {
											constituencyName = member.findConstituencyNameForYadiReport(discussionmotionHouse, "DATE", currentDate, currentDate);
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
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void convertReportDataIntoMapGenericApproach(final List reportData, final ModelMap model) {
		if(reportData!=null && !reportData.isEmpty()) {
			for(int i=0; i<3; i++) {
				Map dataMap = new LinkedHashMap<String, List<String>>();
				List<String> dataMapValues = new ArrayList<String>();
				String groupByFieldValue = "";
				long groupByFieldCount = 0;
				int groupByFieldRowCount = 0;
				int loopCnt = 1;
				for(Object rd : reportData) {					
					Object[] o = (Object[]) rd;					
					if(i==(3-1)) { //innermost level of group by where directs counts are mapped
						dataMapValues = new ArrayList<String>();
						dataMapValues.add(o[i+1].toString());
						dataMap.put(o[i].toString(), dataMapValues);
						model.addAttribute("dataMap"+(i+1), dataMap);
						groupByFieldCount = Long.parseLong(o[3].toString());
						model.addAttribute("dataMap"+(i+1)+"_"+o[i].toString(), groupByFieldCount);
						groupByFieldRowCount = 1;
						model.addAttribute("dataMap"+(i+1)+"_"+o[i].toString()+"_rowCount", groupByFieldRowCount);
						
					} else { //outer levels of group by where each level is mapped to its next level
						if(loopCnt==1) {
							groupByFieldValue = o[i].toString();
						}
						if(!o[i].toString().equals(groupByFieldValue)) {						
							//dataMap = new LinkedHashMap<String, List<String>>();
							dataMapValues = new ArrayList<String>();
							groupByFieldValue = o[i].toString();
							groupByFieldCount = Long.parseLong(o[3].toString());
							groupByFieldRowCount = 1;
						} else {
							dataMap.put(o[i].toString(), dataMapValues);
							model.addAttribute("dataMap"+(i+1), dataMap);
							groupByFieldCount += Long.parseLong(o[3].toString());
							model.addAttribute("dataMap"+(i+1)+"_"+o[i].toString(), groupByFieldCount);
							groupByFieldRowCount += 1;
							model.addAttribute("dataMap"+(i+1)+"_"+o[i].toString()+"_rowCount", groupByFieldRowCount);
						}
						if(!dataMapValues.contains(o[i+1].toString())) {
							dataMapValues.add(o[i+1].toString());
						}
					}					
					loopCnt++;
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map convertReportDataIntoMap(final List reportData, final ModelMap model) {
		Map dataMap1 = new LinkedHashMap<String, Map>();
		if(reportData!=null && !reportData.isEmpty()) {
			String groupByFieldValue1 = "";			
			int loopCnt1 = 1;
			for(Object rd1 : reportData) {
				long groupByFieldCount1 = 0;
				Object[] o1 = (Object[]) rd1;
				if(loopCnt1==1) {
					groupByFieldValue1 = o1[0].toString();
				}				
				if(loopCnt1==1 || !o1[0].toString().equals(groupByFieldValue1)) {
					Map dataMap2 = new LinkedHashMap<String, Map>();
					String groupByFieldValue2 = "";					
					int loopCnt2 = 1;
					for(Object rd2 : reportData) {		
						long groupByFieldCount2 = 0;
						Object[] o2 = (Object[]) rd2;
						if(o2[0].toString().equals(o1[0].toString())) { //for same group
							if(loopCnt2==1) {
								groupByFieldValue2 = o2[1].toString();	
							}				
							if(loopCnt2==1 || !o2[1].toString().equals(groupByFieldValue2)) {
								Map dataMap3 = new LinkedHashMap<String, Long>();
								String groupByFieldValue3 = "";
								int loopCnt3 = 1;
								for(Object rd3 : reportData) {
									Object[] o3 = (Object[]) rd3;
									if(o3[1].toString().equals(o2[1].toString())) { //for same assistant
										if(loopCnt3==1) {
											groupByFieldValue3 = o3[2].toString();
										}				
										if(loopCnt3==1 || !o3[2].toString().equals(groupByFieldValue3)) {
											dataMap3.put(o3[2].toString(), Long.parseLong(o3[3].toString()));
											groupByFieldValue3 = o3[2].toString();
											groupByFieldCount1 += Long.parseLong(o3[3].toString());
											groupByFieldCount2 += Long.parseLong(o3[3].toString());
										}
									}	
									loopCnt3++;
								}
								dataMap2.put(o2[1].toString(), dataMap3);
								model.addAttribute("dataMap2_"+o2[1].toString(), groupByFieldCount2);
								groupByFieldValue2 = o2[1].toString();
							}
						}		
						loopCnt2++;
					}
					dataMap1.put(o1[0].toString(), dataMap2);
					model.addAttribute("dataMap1_"+o1[0].toString(), groupByFieldCount1);
					groupByFieldValue1 = o1[0].toString();
					groupByFieldCount1 = 0;
				}		
				loopCnt1++;
			}
		}
		
		return dataMap1;
	}
	
	private List<CountsUsingGroupByReportVO> findCountsUsingGroupByReportVO(final List reportData) {
		List<CountsUsingGroupByReportVO> countsUsingGroupByReportVOs = new ArrayList<CountsUsingGroupByReportVO>();
	
		if(reportData!=null && !reportData.isEmpty()) {
			CountsUsingGroupByReportVO[] countsArr = new CountsUsingGroupByReportVO[3];
			for(int i=3; i>0; i--) {
				CountsUsingGroupByReportVO countsUsingGroupByReportVO = new CountsUsingGroupByReportVO();
				int loopCnt = 1;
				String groupByFieldValue = "";
				for(Object rd : reportData) {
					Object[] o = (Object[]) rd;
					if(loopCnt==1) {
						groupByFieldValue = o[i].toString();
						countsArr[i-1].setGroupByFieldValue(groupByFieldValue);
						
					}
					
				}
			}
		}
		
		return countsUsingGroupByReportVOs;
	}
}