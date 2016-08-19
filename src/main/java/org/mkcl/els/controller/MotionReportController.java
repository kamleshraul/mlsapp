package org.mkcl.els.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.xmlvo.XmlVO;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.WorkflowDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
			if(strMotion != null && !strMotion.isEmpty()){
				Motion m = Motion.findById(Motion.class, new Long(strMotion));
				
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
			
			String strId = request.getParameter("motionId");
			String strReportFormat = request.getParameter("outputFormat");	
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = request.getParameterMap();
				
				List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
				String templateName = request.getParameter("templateName");
				File reportFile = null;				
				
				reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
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
			
			if(strId != null && !strId.isEmpty()){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("motionId", new String[]{strId});
				
				@SuppressWarnings("rawtypes")
				List reportData = Query.findReport("MOTION_NIVEDAN_TARIKH", parameters);	
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
				List reportData = Query.findReport("MOTION_NIVEDAN_TARIKH_LOWERHOUSE", parameters);	
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
		return MotionReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
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