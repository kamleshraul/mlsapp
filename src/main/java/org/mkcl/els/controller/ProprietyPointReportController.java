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
import org.mkcl.els.domain.ProprietyPoint;
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
@RequestMapping("proprietypoint/report")
public class ProprietyPointReportController extends BaseController{
	
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
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		String sessionId = request.getParameter("sessionId");
		String reportQueryName = request.getParameter("reportQueryName");
		if(sessionId==null || sessionId.isEmpty()
				|| reportQueryName==null || reportQueryName.isEmpty()) {
			logger.error("**** One of the request parameters is not set ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "prois.tobeadmitted_report.parameterNotSet", locale.toString());						
		} else {
			requestMap.put("sessionId", new String[] {sessionId});
			requestMap.put("locale", new String[]{locale.toString()});
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