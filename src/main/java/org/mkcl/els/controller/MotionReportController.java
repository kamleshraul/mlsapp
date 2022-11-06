package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
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
import org.mkcl.els.common.xmlvo.DeviceXmlVO;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.ReferenceLetter;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
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
@RequestMapping("motion/report")
public class MotionReportController extends BaseController{
	
	@RequestMapping(value = "/motion/genreport", method = RequestMethod.GET)
	public String getMotionReport(HttpServletRequest request, Model model, Locale locale){
		String retVal = "motion/error";
		try{
			genReport(request, model, locale);
			retVal = "motion/reports/" + request.getParameter("reportout");
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "general_error");
		}
		
		return retVal;
	}
	
	@RequestMapping(value = "/motion/advancereport", method = RequestMethod.GET)
	public String getMotionReportNew(HttpServletRequest request, Model model, Locale locale){
		String retVal = "motion/error";
		try{
			genReportWithIn(request, model, locale);
			retVal = "motion/reports/" + request.getParameter("reportout");
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "general_error");
		}
		
		return retVal;
	}
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@RequestMapping(value = "/motion/jodpatra", method = RequestMethod.GET)
	public String getJodPatraReport(HttpServletRequest request, Model model, Locale locale){
		String retVal = "motion/error";
		try{
			Map<String, String[]> requestMap = request.getParameterMap();
			List report = Query.findReport(request.getParameter("report"), requestMap);
			
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			model.addAttribute("report", report);
			model.addAttribute("motion", new Motion());
			retVal = "motion/reports/" + request.getParameter("reportout");
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "general_error");
		}
		
		return retVal;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/motion/jodpatraformation", method=RequestMethod.GET)
	public void jodPatraFormation(final HttpServletRequest request, HttpServletResponse response, final ModelMap model, final Locale locale){
		//String returnValue = "motion/error"; 
		try{
			
			Map<String, String[]> requestMap = request.getParameterMap();
			List report = Query.findReport(request.getParameter("report"), requestMap);
			
			model.addAttribute("report", report);
			
			List<Object> header = new ArrayList<Object>();
			List<Object> footer = new ArrayList<Object>();
			String houseType = null;
			Object[] obj = null;
			if(report != null && !report.isEmpty()){
				
				obj = (Object[])report.get(0);
				header.add(obj[0].toString());
				header.add(obj[1].toString());
				header.add(obj[2].toString());
				houseType = obj[9].toString();
			}
			
			Map reportFields = simplifyJodPartraReport(report, new Long(request.getParameter("sessionId")), locale.toString());
			
			//generate report
			File file = generateReportUsingFOP(new Object[]{reportFields, header, houseType, obj[10].toString(),obj[11].toString(),obj[12].toString(),obj[13].toString()}, "template_motion_jodpatra", request.getParameter("reportFormat"), "motion_jod_patra", locale.toString());
			openOrSaveReportFileFromBrowser(response, file, request.getParameter("reportFormat"));
			//returnValue = "roster/rosterreport";
					
		}catch (Exception e) {
			logger.error("error", e);		
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/motion/vivranreport", method=RequestMethod.GET)
	public void generateVivaranReport(final HttpServletRequest request, HttpServletResponse response, final ModelMap model, final Locale locale){
		//String returnValue = "motion/error"; 
		try{
			
			Map<String, String[]> requestMap = request.getParameterMap();
			List report1 = Query.findReport(request.getParameter("report"), requestMap);
			List report2 = Query.findReport("MOTION_JODPATRA_REPORT_VIVARANREPORT", requestMap);
			model.addAttribute("report", report1);
			model.addAttribute("report2", report2);
					
			Map reportFields = simplifyJodPartraReportForVivaranReport(report2, new Long(request.getParameter("sessionId")),report1.size(), locale.toString());
			
			//generate report
			File file = generateReportUsingFOP(new Object[]{report1,reportFields}, "template_motion_vivranreport", request.getParameter("reportFormat"), "vivranreport", locale.toString());
			openOrSaveReportFileFromBrowser(response, file, request.getParameter("reportFormat"));
			//returnValue = "roster/rosterreport";
					
		}catch (Exception e) {
			logger.error("error", e);		
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/motion/discussionstatusformation", method=RequestMethod.GET)
	public void discussionStatusFormation(final HttpServletRequest request, HttpServletResponse response, final ModelMap model, final Locale locale){
		//String returnValue = "motion/error"; 
		try{
			
			Map<String, String[]> requestMap = request.getParameterMap();
			List report = Query.findReport(request.getParameter("report"), requestMap);
			
			model.addAttribute("report", report);
			
			List<Object> header = new ArrayList<Object>();
			List<Object> colHeader = new ArrayList<Object>();
			if(report != null && !report.isEmpty()){
				
				Object[] obj = (Object[])report.get(0);
				String[] arrData = obj[0].toString().split(";");
				for(String s : arrData){
					colHeader.add(s);
				}
				
				header.add(obj[1].toString());
				header.add(obj[2].toString());
				header.add(obj[3].toString());
				header.add(obj[4].toString());
			}
			
			Map reportFields = simplifyDiscussionStatusReport(report, locale.toString());
			
			//generate report
			File file = generateReportUsingFOP(new Object[]{reportFields, header, colHeader}, "template_motion_discussionstatus", request.getParameter("reportFormat"), "motion_discussion_status", locale.toString());
			openOrSaveReportFileFromBrowser(response, file, request.getParameter("reportFormat"));
			//returnValue = "roster/rosterreport";
					
		}catch (Exception e) {
			logger.error("error", e);		
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map simplifyJodPartraReport(final List report, final Long sessionId, String locale){
		Map<String, List> jodPatraData = new LinkedHashMap<String, List>();
		String ministryName = "";
		List<List<Object>> memberContent = new ArrayList<List<Object>>();
		Session session = Session.findById(Session.class, sessionId);
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("onDate", new String[] { session.getStartDate().toString() });
		parameters.put("locale", new String[] { locale.toString() });
		List ministryreport = Query.findReport(ApplicationConstants.ROTATIONORDER_MINISTRY_DEPARTMENTS_REPORT, parameters);
       
		String houseType = session.getHouse().getType().getType();
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
			for(Object ob : ministryreport){
				jodPatraData.put(((Object[])ob)[2].toString(), new ArrayList<Object>());
			}
		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
			for(Object ob : ministryreport){
				jodPatraData.put(((Object[])ob)[3].toString(), new ArrayList<Object>());
			}
		}
		
		
		int index = 0;
		for(Object o: report){
			Object[] objArr = (Object[])o;
			
			List<Object> slotFields = new ArrayList<Object>();
			index++;
			if(!ministryName.equals(objArr[3].toString())){
				if(!ministryName.isEmpty()){
					if(jodPatraData.containsKey(ministryName)){
						if(jodPatraData.get(ministryName) != null){
							if(!jodPatraData.get(ministryName).isEmpty()){
								List data = jodPatraData.get(ministryName);
 								for(Object omd : memberContent){
									data.add(omd);
								}
 								
 								jodPatraData.put(ministryName, data);
							}else{
								jodPatraData.put(ministryName, memberContent);
							}
						}
					}else{
						jodPatraData.put(ministryName, memberContent);
					}					
					memberContent = null;
					memberContent = new ArrayList<List<Object>>();
				}
				ministryName = objArr[3].toString();
				slotFields.add(FormaterUtil.formatNumberNoGrouping(index, locale));
				if(objArr[9] != null && !objArr[9].toString().isEmpty() && objArr[9].toString().equals(ApplicationConstants.LOWER_HOUSE)){
					slotFields.add(objArr[7] + ", " + objArr[8]);
				}else{
					slotFields.add(objArr[7]);
					slotFields.add(objArr[8]);
				}
				slotFields.add(objArr[6]);
				memberContent.add(slotFields);
			}else{
				slotFields.add(FormaterUtil.formatNumberNoGrouping(index, locale));
				if(objArr[9] != null && !objArr[9].toString().isEmpty() && objArr[9].toString().equals(ApplicationConstants.LOWER_HOUSE)){
					slotFields.add(objArr[7] + ", " + objArr[8]);
				}else{
					slotFields.add(objArr[7]);
					slotFields.add(objArr[8]);
				}
				slotFields.add(objArr[6]);
				memberContent.add(slotFields);
			}
			slotFields = null;
		}
		
		
		if (jodPatraData.containsKey(ministryName)) {
			if (jodPatraData.get(ministryName) != null) {
				if (!jodPatraData.get(ministryName).isEmpty()) {
					List data = jodPatraData.get(ministryName);
					for (Object omd : memberContent) {
						data.add(omd);
					}

					jodPatraData.put(ministryName, data);
				} else {
					jodPatraData.put(ministryName, memberContent);
				}
			}
		} else {
			jodPatraData.put(ministryName, memberContent);
		}
		
		Map<String, List> finalJodPatraData = new LinkedHashMap<String, List>();
		int counter = 0;
		for(Map.Entry<String, List> entry : jodPatraData.entrySet()){
			List<Object> newData = new ArrayList<Object>();
			for(Object o : entry.getValue()){
				counter++;
				((ArrayList)o).set(0, FormaterUtil.formatNumberNoGrouping(counter, locale));
				newData.add(o);				
			}
			finalJodPatraData.put(entry.getKey(), newData);
		}
		
		return finalJodPatraData;
	}
	
	
	/*** Following Method is written for Calling attention Vivaran Report
	 * 	Vivaran Report needed extra information hence could not reuse the SimplifyJodpatra Method***/
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map simplifyJodPartraReportForVivaranReport(final List report, final Long sessionId,int i, String locale){
		Map<String, List> jodPatraData = new LinkedHashMap<String, List>();
		String ministryName = "";
		List<List<Object>> memberContent = new ArrayList<List<Object>>();
		Session session = Session.findById(Session.class, sessionId);
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("onDate", new String[] { session.getStartDate().toString() });
		parameters.put("locale", new String[] { locale.toString() });
		List ministryreport = Query.findReport(ApplicationConstants.ROTATIONORDER_MINISTRY_DEPARTMENTS_REPORT, parameters);
       
		String houseType = session.getHouse().getType().getType();
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
			for(Object ob : ministryreport){
				jodPatraData.put(((Object[])ob)[2].toString(), new ArrayList<Object>());
			}
		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
			for(Object ob : ministryreport){
				jodPatraData.put(((Object[])ob)[3].toString(), new ArrayList<Object>());
			}
		}
		
		
		int index = 0;
		for(Object o: report){
			Object[] objArr = (Object[])o;
			
			List<Object> slotFields = new ArrayList<Object>();
			index++;
			if(!ministryName.equals(objArr[3].toString())){
				if(!ministryName.isEmpty()){
					if(jodPatraData.containsKey(ministryName)){
						if(jodPatraData.get(ministryName) != null){
							if(!jodPatraData.get(ministryName).isEmpty()){
								List data = jodPatraData.get(ministryName);
 								for(Object omd : memberContent){
									data.add(omd);
								}
 								
 								jodPatraData.put(ministryName, data);
							}else{
								jodPatraData.put(ministryName, memberContent);
							}
						}
					}else{
						jodPatraData.put(ministryName, memberContent);
					}					
					memberContent = null;
					memberContent = new ArrayList<List<Object>>();
				}
				ministryName = objArr[3].toString();
				slotFields.add(FormaterUtil.formatNumberNoGrouping(index, locale));
				if(objArr[9] != null && !objArr[9].toString().isEmpty() && objArr[9].toString().equals(ApplicationConstants.LOWER_HOUSE)){
					slotFields.add(objArr[7] + ", " + objArr[8]);
				}else{
					slotFields.add(objArr[7]);
					slotFields.add(objArr[8]);
				}
				slotFields.add(objArr[6]);
				slotFields.add(objArr[14]);
				slotFields.add(objArr[11]);
				slotFields.add(++i);
				memberContent.add(slotFields);
			}else{
				slotFields.add(FormaterUtil.formatNumberNoGrouping(index, locale));
				if(objArr[9] != null && !objArr[9].toString().isEmpty() && objArr[9].toString().equals(ApplicationConstants.LOWER_HOUSE)){
					slotFields.add(objArr[7] + ", " + objArr[8]);
				}else{
					slotFields.add(objArr[7]);
					slotFields.add(objArr[8]);
				}
				slotFields.add(objArr[6]);
				slotFields.add(objArr[14]);
				slotFields.add(objArr[11]);
				slotFields.add(++i);
				memberContent.add(slotFields);
			}
			slotFields = null;
		}
		
		
		if (jodPatraData.containsKey(ministryName)) {
			if (jodPatraData.get(ministryName) != null) {
				if (!jodPatraData.get(ministryName).isEmpty()) {
					List data = jodPatraData.get(ministryName);
					for (Object omd : memberContent) {
						data.add(omd);
					}

					jodPatraData.put(ministryName, data);
				} else {
					jodPatraData.put(ministryName, memberContent);
				}
			}
		} else {
			jodPatraData.put(ministryName, memberContent);
		}
		
		Map<String, List> finalJodPatraData = new LinkedHashMap<String, List>();
		int counter = 0;
		for(Map.Entry<String, List> entry : jodPatraData.entrySet()){
			List<Object> newData = new ArrayList<Object>();
			for(Object o : entry.getValue()){
				counter++;
				((ArrayList)o).set(0, FormaterUtil.formatNumberNoGrouping(counter, locale));
				newData.add(o);				
			}
			finalJodPatraData.put(entry.getKey(), newData);
		}
		
		return finalJodPatraData;
	}
	
	@SuppressWarnings("rawtypes")
	private Map simplifyDiscussionStatusReport(final List report, String locale){
		Map<String, List> jodPatraData = new LinkedHashMap<String, List>();
		String ministryName = "";
		List<List<Object>> memberContent = new ArrayList<List<Object>>();
		int index = 0;
		for(Object o: report){
			Object[] objArr = (Object[])o;
			List<Object> slotFields = new ArrayList<Object>();
			index++;
			if(!ministryName.equals(objArr[6].toString())){
				if(!ministryName.isEmpty()){
					jodPatraData.put(ministryName, memberContent);
					memberContent = null;
					memberContent = new ArrayList<List<Object>>();
				}
				ministryName = objArr[6].toString();
				slotFields.add(FormaterUtil.formatNumberNoGrouping(index, locale));
				slotFields.add(objArr[7]);
				slotFields.add(objArr[8]);
				slotFields.add(objArr[9]);
				memberContent.add(slotFields);
			}else{
				slotFields.add(FormaterUtil.formatNumberNoGrouping(index, locale));
				slotFields.add(objArr[7]);
				slotFields.add(objArr[8]);
				slotFields.add(objArr[9]);
				memberContent.add(slotFields);
			}
			slotFields = null;
		}
		
		jodPatraData.put(ministryName, memberContent);
		
		return jodPatraData;
	}
	
	@RequestMapping(value="/cutmotion/genreport", method=RequestMethod.GET)
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
	
	@RequestMapping(value="/eventmotion/genreport", method=RequestMethod.GET)
	public String getEventMotionReport(HttpServletRequest request, Model model, Locale locale){
		String retVal = "eventmotion/error";
		try{
			genReport(request, model, locale);
			retVal = "eventmotion/reports/" + request.getParameter("reportout");
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "general_error");
		}
		
		return retVal;		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void genReport(HttpServletRequest request, Model model, Locale locale){
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
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void genReportWithIn(HttpServletRequest request, Model model, Locale locale){
			Map<String, String[]> requestMap = request.getParameterMap();
			List report = Query.findReportWithIn(request.getParameter("report"), requestMap);
			if(report != null && !report.isEmpty()){
				Object[] obj = (Object[])report.get(0);
				if(obj != null){
					model.addAttribute("topHeader", obj[0].toString().split(";"));
				}
			}
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			model.addAttribute("report", report);
	}
	
	@RequestMapping(value ="/commonadmissionreport", method = RequestMethod.GET)
	public void commonAdmissionReport(Model model,HttpServletRequest request,HttpServletResponse response, Locale locale){
		try{
			String strMotion = request.getParameter("motionId");
			String strWorkflowDetailsId = request.getParameter("workflowDetailId");
			if((strMotion != null && !strMotion.isEmpty())
					|| (strWorkflowDetailsId != null && !strWorkflowDetailsId.isEmpty()) ){
				Motion m = null;
				WorkflowDetails wfDetails = null;
				if(strMotion != null && !strMotion.isEmpty()){
					m = Motion.findById(Motion.class, new Long(strMotion));
				}else if(strWorkflowDetailsId != null && !strWorkflowDetailsId.isEmpty()){
					wfDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowDetailsId));
					if(wfDetails!=null){
						m = Motion.findById(Motion.class, Long.parseLong(wfDetails.getDeviceId()));
					}
				}					
				
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/fopgenreport", method = RequestMethod.GET)
	public void fopGeneralReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		//String retVal = "motion/report";
		try{
			
			String strMotion = request.getParameter("motionId");
			String strWorkflowDetailsId = request.getParameter("workflowDetailId");
			if((strMotion != null && !strMotion.isEmpty())
					|| (strWorkflowDetailsId != null && !strWorkflowDetailsId.isEmpty()) ){
				Motion m = null;
				WorkflowDetails wfDetails = null;
				if(strMotion != null && !strMotion.isEmpty()){
					m = Motion.findById(Motion.class, new Long(strMotion));
				}else if(strWorkflowDetailsId != null && !strWorkflowDetailsId.isEmpty()){
					wfDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowDetailsId));
					if(wfDetails!=null){
						m = Motion.findById(Motion.class, Long.parseLong(wfDetails.getDeviceId()));
					}
				}					
				
				if(m != null){
					HouseType ht = m.getHouseType();
					
					Map<String, String[]> requestParameters = request.getParameterMap();
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.putAll(requestParameters);
					if(parameters.get("motionId")==null) {
						parameters.put("motionId", new String[]{m.getId().toString()});
					}
					
					List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
					List reportData1 = new ArrayList<Object[]>();
					if(ht.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
						reportData1 = Query.findReport(request.getParameter("reportQuery")+"_CLUBBED_DETAILS", parameters);
					}
					String templateName = request.getParameter("templateName")+"_"+ht.getType();
					String strReportFormat = request.getParameter("outputFormat");
					File reportFile = null;
					
					reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
					
					model.addAttribute("info", "general_info");;
					//retVal = "motion/info";
				}
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
			String strWorkflowId = request.getParameter("workflowDetailId");
			WorkflowDetails workflowDetails = null;
			ReferenceLetter referenceLetter = null;
			String referenceNumber = null;
			String referredNumber = null;
			String strDispatchedDate = null;
			String strReferencedDate = null;
			String strReportFormat = request.getParameter("outputFormat");
			@SuppressWarnings("rawtypes")
			List reportData = null;
			String templateName = "motion_nivedan_tarikh_lowerhouse";
			String strCopyType = request.getParameter("copyType");
			Boolean isResendRevisedMotionTextWorkflow = false;
			String strRevisedMotionText = null;
			
			if(strWorkflowId != null && !strWorkflowId.isEmpty()){
				workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowId));
				if(workflowDetails != null){
					strId = workflowDetails.getDeviceId();
					if(workflowDetails.getReferenceNumber() != null && !workflowDetails.getReferenceNumber().isEmpty()){
						referenceLetter = ReferenceLetter.findByFieldName(ReferenceLetter.class, "referenceNumber", workflowDetails.getReferenceNumber(), locale.toString());
						
						if(strCopyType != null && strCopyType == "advanceCopy")
						{
							List<WorkflowDetails> wkfDetails = WorkflowDetails.findAllByFieldName(WorkflowDetails.class, "referenceNumber", workflowDetails.getReferenceNumber(), "assignmentTime", "ASC", locale.toString());
							strDispatchedDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(wkfDetails.get(0).getAssignmentTime(), "CALLINGATTENTIONMOTION_CALLINGATTENTIONDATEFORMAT", locale.toString());
						}
					}
				}
			} 
			
			if(strId != null && !strId.isEmpty())
			{
				Motion motion = Motion.findById(Motion.class, Long.parseLong(strId));
				
				if(referenceLetter==null) {
					ReferenceLetter latestIntimationReferenceLetterHavingMotion = ReferenceLetter.findLatestHavingGivenDevice(strId, ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT, locale.toString());
					
					if(latestIntimationReferenceLetterHavingMotion!=null
							&& latestIntimationReferenceLetterHavingMotion.getParentDeviceId().equals(strId))
					{
						referenceLetter = latestIntimationReferenceLetterHavingMotion;
					}
					else if(latestIntimationReferenceLetterHavingMotion!=null
							&& ! latestIntimationReferenceLetterHavingMotion.getParentDeviceId().equals(Long.parseLong(strId)))
					{
						if(motion.getParent()!=null && motion.getParent().toString().equals(latestIntimationReferenceLetterHavingMotion.getParentDeviceId()))
						{
							referenceLetter = latestIntimationReferenceLetterHavingMotion;
						}
					}
				}
				
				if(referenceLetter==null) { //for advance copy without reference letter
					if(strCopyType != null && strCopyType == "advanceCopy"){
						referenceNumber = FormaterUtil.formatNumberNoGrouping(motion.getId(), locale.toString());
					}
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("motionId", new String[]{strId});
					reportData = Query.findReport("MOTION_NIVEDAN_TARIKH_LOWERHOUSE", parameters);
				} 
				else {
					referenceNumber = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(referenceLetter.getReferenceNumber()), locale.toString());
					if(referenceLetter.getReferredNumber()!=null && !referenceLetter.getReferredNumber().isEmpty()) {
						referredNumber = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(referenceLetter.getReferredNumber()), locale.toString());
					}					
					strDispatchedDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(referenceLetter.getDispatchDate(), "CALLINGATTENTIONMOTION_CALLINGATTENTIONDATEFORMAT", locale.toString());
					
					if(referredNumber!=null) {
						ReferenceLetter previousReferenceLetter = ReferenceLetter.findByFieldName(ReferenceLetter.class, "referenceNumber", referenceLetter.getReferredNumber(), locale.toString());
						if(previousReferenceLetter!=null) {
							strReferencedDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(previousReferenceLetter.getDispatchDate(), "CALLINGATTENTIONMOTION_CALLINGATTENTIONDATEFORMAT", locale.toString());
						}
					}
					
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("motionId", new String[]{strId});
					parameters.put("referenceLetterId", new String[]{referenceLetter.getId().toString()});
					reportData = Query.findReport("MOTION_NIVEDAN_TARIKH_FROM_REFERENCE_LETTER_LOWERHOUSE", parameters);
					
					if(reportData!=null && !reportData.isEmpty()) 
					{
						Object[] obj = (Object[]) reportData.get(0);
						if(obj[8]==null || obj[8].toString().isEmpty()) 
						{							
							if(obj[13]!=null && !obj[13].toString().isEmpty()) {
								/**** populating member names with customized formatting ****/
								String memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
								CustomParameter memberNameFormatParameter = null;
								if(motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
									memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "MOIS_INTIMATIONLETTER_MEMBERNAMEFORMAT_LOWERHOUSE", "");
								} else if(motion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
									memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "MOIS_INTIMATIONLETTER_MEMBERNAMEFORMAT_UPPERHOUSE", "");
								}
								if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
									memberNameFormat = memberNameFormatParameter.getValue();
								}
								
								StringBuffer memberNames = new StringBuffer("");
								String[] memberIds = obj[13].toString().split(",");
								for(String memberId: memberIds) {
									Member member = Member.findById(Member.class, Long.parseLong(memberId));
									memberNames.append(member.findNameInGivenFormat(memberNameFormat));
									memberNames.append(",");
								}
								memberNames.deleteCharAt(memberIds.length-1);
								
								obj[8] = memberNames.toString();
							}
						}
					}
					
					strRevisedMotionText = motion.getRevisedDetails();
					if(referenceLetter.getReferredNumber() != null && !referenceLetter.getReferredNumber().isEmpty())
					{
						strRevisedMotionText = referenceLetter.getNoticeContent();
						strCopyType = "revisedCopy";
						isResendRevisedMotionTextWorkflow = true;
					}
				}
				
				File reportFile = null;
				
				reportFile = generateReportUsingFOP(new Object[] {reportData, strCopyType, isResendRevisedMotionTextWorkflow, strRevisedMotionText, referenceNumber, referredNumber, strDispatchedDate, strReferencedDate}, templateName, strReportFormat, "motionNivedanTarikh",locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
				
				model.addAttribute("info", "general_info");
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
		return "motion/reports/statusreport";
	}
	
	@RequestMapping(value="/{moId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("moId") Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
				
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
		return MotionReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
	}
	
	
	@RequestMapping(value="/online_offline_submission_count_report/init",method=RequestMethod.GET)
	public String initOnlineOfflineSubmissionCountReport(final HttpServletRequest request,final ModelMap model,final Locale locale) throws ELSException{
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String responsePage="motion/reports/error";
		
		String strdeviceType = request.getParameter("deviceType");
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		
		if(strdeviceType!=null && strHouseType!=null && strSessionType!=null && strSessionYear!=null
				&& !strdeviceType.isEmpty() && !strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()) {
			
			/**** populate selected deviceType ****/
			DeviceType deviceType=DeviceType.findByName(DeviceType.class, strdeviceType, locale.toString());
			if(deviceType==null) {
				deviceType=DeviceType.findByType(strdeviceType, locale.toString());
			}
			if(deviceType==null) {
				deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strdeviceType));
			}
			if(deviceType==null) {
				logger.error("**** parameter deviceType is invalid ****");
				model.addAttribute("errorcode", "invalid_parameters");
				return responsePage;
			}
			model.addAttribute("deviceType",deviceType.getId());
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
			String submissionStartDateSessionParameter = session.getParameter(deviceType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_START_DATE_MOIS_SESSION_PARAMETER_KEY);
			if(submissionStartDateSessionParameter==null || submissionStartDateSessionParameter.isEmpty()) {
				logger.error("**** Submission start date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_undefined_for_session");
				return responsePage;				
			}
			
			defaultFromDate = FormaterUtil.formatStringToDate(submissionStartDateSessionParameter, ApplicationConstants.SERVER_DATEFORMAT);
			if(defaultFromDate==null)  {
				logger.error("**** Submission start date parameter is set to invalid value for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_invalid_for_session");
				return responsePage;
			}
			/**** submission end date of the session as default toDate ****/
			String submissionEndDateSessionParameter = session.getParameter(deviceType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_END_DATE_MOIS_SESSION_PARAMETER_KEY);
			if(submissionEndDateSessionParameter==null || submissionEndDateSessionParameter.isEmpty()) {
				logger.error("**** Submission end date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_end_date_parameter_undefined_for_session");
				return responsePage;
			}
			defaultToDate = FormaterUtil.formatStringToDate(submissionEndDateSessionParameter, ApplicationConstants.SERVER_DATEFORMAT);
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
			responsePage = "motion/reports/online_offline_submission_count_report_init";
		} else {
			logger.error("**** Check request parameters 'deviceType,houseType,sessionType,sessionYear' for null/empty values ****");
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
		String deviceTypeId = request.getParameter("deviceType");
		String houseType = request.getParameter("houseType");
		String criteria = request.getParameter("criteria");
		String forTodayStr = request.getParameter("forToday");
		String fromDateStr = request.getParameter("fromDate");
		String toDateStr = request.getParameter("toDate");
		
		try {
			if(session!=null && !session.isEmpty() 
					&& deviceTypeId!=null && !deviceTypeId.isEmpty()
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
					DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));
					if(submitStatus!=null) {
						Map<String, String[]> queryParameters = new HashMap<String, String[]>();
						queryParameters.put("locale", new String[] {locale.toString()});
						queryParameters.put("sessionId", new String[] {session});
						queryParameters.put("deviceTypeId", new String[] {deviceTypeId});
						queryParameters.put("houseTypeId", new String[] {houseType});												
						queryParameters.put("fromDate", new String[] {fromDateStr});
						queryParameters.put("toDate", new String[] {toDateStr});
						queryParameters.put("submitStatusId", new String[] {submitStatus.getId().toString()});
						
						String queryName = "MOIS_MEMBERWISE_MOTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						if(criteria.equals("datewise")) {	
							synchronized (Session.class) {								
								boolean isSubmissionDatesForSessionLoaded = Session.loadSubmissionDatesForDeviceTypeInSession(sessionObj, deviceType, fromDate, toDate);
								if(isSubmissionDatesForSessionLoaded==false) {
									//error
									isError = true;	
								}
							}							
							queryName = "MOIS_DATEWISE_MOTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						}						
						List reportData = Query.findReport(queryName, queryParameters);
						if(reportData!=null && !reportData.isEmpty()) {
							/**** generate report ****/
							if(!isError) {								
								List<String> serialNumbers = populateSerialNumbers(reportData, locale);
								reportFile = generateReportUsingFOP(new Object[]{reportData, criteria, serialNumbers}, "motions_online_submission_counts_template", "WORD", deviceType.getType()+"_"+criteria+"_online_submission_counts_report", locale.toString());				
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
	
		//Print functionality for members for submitted motions
		//komala		
		@RequestMapping(value="/motionPrintReport", method=RequestMethod.GET)
		public void getMotionReport(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale){
			File reportFile = null; 
			String strMotionId = request.getParameter("motionId");	
			String reportFormat=request.getParameter("outputFormat");
			
			Motion motion = Motion.findById(Motion.class, Long.parseLong(strMotionId));
			
			DeviceXmlVO deviceXmlVO = new DeviceXmlVO();
			deviceXmlVO.setHouseType(motion.getType().getName());//deviceType
			if(motion.getNumber()!=null){
				deviceXmlVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(motion.getNumber(), locale.toString()));//question number
			}
			if(motion.getSubmissionDate()!=null){
				deviceXmlVO.setSubmissionDate(FormaterUtil.formatDateToString(motion.getSubmissionDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, locale.toString()));//submission date
			}
			deviceXmlVO.setMemberNames(motion.getPrimaryMember().findFirstLastName());//Member Name
			deviceXmlVO.setSubject(motion.getSubject());//subject
			deviceXmlVO.setContent(motion.getDetails());//Question Text
			deviceXmlVO.setConstituency(motion.getPrimaryMember().findConstituency().getDisplayName());//constituency		
			Status memberStatus = motion.findMemberStatus();
			deviceXmlVO.setStatus(memberStatus.getName());//status
			deviceXmlVO.setMinistryName(motion.getMinistry().getName());//Ministry Name
			deviceXmlVO.setSubdepartmentName(motion.getSubDepartment().getName());//Subdepartment name
			if(motion.getAnsweringDate()!=null){
				deviceXmlVO.setAnsweringDate(motion.getAnsweringDate().toString());//Answering Date
			}
			StringBuffer allMemberNamesBuffer = new StringBuffer("");
			Member member = null;
			String memberName = "";
			List<SupportingMember> supportingMembers = motion.getSupportingMembers();
			if (supportingMembers != null) {
				for (SupportingMember sm : supportingMembers) {
					  member = sm.getMember();
					Status approvalStatus = sm.getDecisionStatus();
					if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
						memberName = member.findFirstLastName();
						if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(allMemberNamesBuffer.length()>0) {
								allMemberNamesBuffer.append(", " + memberName);
							} else {
								allMemberNamesBuffer.append(memberName);
							}									
						}									
					}				
				}
			}	
			deviceXmlVO.setSupportingMembers(allMemberNamesBuffer.toString());
		
			try {
				reportFile = generateReportUsingFOP(deviceXmlVO, "template_motion_report", reportFormat, "question_"+motion.getNumber(), locale.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
		}
}


class MotionReportHelper{
	private static Logger logger = LoggerFactory.getLogger(MotionReportHelper.class);
	
	public static String getCurrentStatusReportData(Long id, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "motion/error";
		try{
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("locale", new String[]{locale.toString()});
			params.put("motionId", new String[]{id.toString()});
			
			List report = Query.findReport(request.getParameter("reportOut"), params);
			
			model.addAttribute("data", report);
			
			Motion m = Motion.findById(Motion.class, id);
			page = (m.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "motion/reports/statusreportlowerhouse": "motion/reports/statusreportupperhouse";
			
		}catch(Exception e){
			logger.error("error", e);
			model.addAttribute("errorcode", "general_error");
		}
		
		return page;
	}
}