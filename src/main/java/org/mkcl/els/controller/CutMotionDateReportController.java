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
import org.mkcl.els.common.vo.CutMotionDepartmentDateVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
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

@Controller
@RequestMapping("cutmotiondate/report")
public class CutMotionDateReportController extends BaseController{
	
	@RequestMapping(value="/currentstatusreport", method=RequestMethod.GET)
	public String getCurrentStatusReport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){

		String strDevice = request.getParameter("device");
		String strReportType = request.getParameter("reportType");
		String strWfid = request.getParameter("wfdId");
		String strCutMotionDateId = request.getParameter("cutMotionDateId");
		
		WorkflowDetails wfd = null;
		if(strWfid != null && !strWfid.isEmpty()){
			wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
			if(wfd != null){
				model.addAttribute("cutMotionDateId", wfd.getDeviceId());
			}
		}
		
		if(strCutMotionDateId != null && !strCutMotionDateId.isEmpty()){
			model.addAttribute("cutMotionDateId", strCutMotionDateId);
		}
		
		model.addAttribute("reportType", strReportType);
		if(strDevice != null && !strDevice.isEmpty()){		
			model.addAttribute("device", strDevice);
		}

		response.setContentType("text/html; charset=utf-8");
		return "cutmotiondate/reports/statusreport";
	}
	
	@RequestMapping(value="/{cutMotionDateId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("cutMotionDateId") Long id, ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale){
				
		response.setContentType("text/html; charset=utf-8");		
		return CutMotionDateReportHelper.getCurrentStatusReportData(id, model, request, response, locale);
	}
	
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
				logger.error(e.getMessage());
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
	
	public static String getCurrentStatusReportData(Long id, ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String page = "cutmotiondate/error";
		try{
			String strDevice = request.getParameter("device"); 
			if(strDevice != null && !strDevice.isEmpty()){
				CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, id);
				List report = generatetCurrentStatusReport(cutMotionDate, strDevice, locale.toString());				
				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();
				if(report != null && !report.isEmpty()){
					
					List<User> users = User.findByRole(false, "CMOIS_PRINCIPAL_SECRETARY", locale.toString());
					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
	
					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (cutMotionDate.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)? "CUTMOTIONDATE_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "CUTMOTIONDATE_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
					if(csptAllwedUserGroupForStatusReportSign != null){
						if(csptAllwedUserGroupForStatusReportSign.getValue() != null && !csptAllwedUserGroupForStatusReportSign.getValue().isEmpty()){
							Object[] lastObject = (Object[]) report.get(report.size()-1); 
							for(Object o : report){
								Object[] objx = (Object[])o;
	
								if(objx[13] != null && !objx[13].toString().isEmpty()){
									if(csptAllwedUserGroupForStatusReportSign.getValue().contains(objx[13].toString())){
										
										UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", objx[13].toString(), locale.toString());
																				
										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
											if(dataMap.get(ApplicationConstants.UNDER_SECRETARY) != null){
												if(objx != null){
													if(objx[4] != null && objx[4].toString().length() > 0){
														dataMap.put(ApplicationConstants.UNDER_SECRETARY, objx);
													}else{
														Object[] tempObj = dataMap.get(ApplicationConstants.UNDER_SECRETARY);
														tempObj[14] = objx[14];
														
														dataMap.put(ApplicationConstants.UNDER_SECRETARY, tempObj);
													}
												}
											}else {
												dataMap.put(ApplicationConstants.UNDER_SECRETARY, objx);
											}
										}else{
											if(dataMap.get(userGroupType.getType()) != null){
												if(objx != null){
													if(objx[4] != null && objx[4].toString().length() > 0){
														dataMap.put(userGroupType.getType(), objx);
													}else{
														Object[] tempObj = dataMap.get(userGroupType.getType());
														tempObj[14] = objx[14];
														
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
							
							//Following block is added for solving the issue of cutmotiondate drafts where in if there exist a draft and later the cutmotiondate is pending
							// at the specific actor, the last remark is displayed
							WorkflowConfig wfConfig = WorkflowConfig.getLatest(cutMotionDate, cutMotionDate.getInternalStatus().getType(), locale.toString());
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
							WorkflowDetails  wfDetails = WorkflowDetails.findCurrentWorkflowDetail(cutMotionDate);
							for(WorkflowActor wf : distinctActors){
								UserGroupType userGroupType = wf.getUserGroupType();
								if(userGroupType.getType().equals(lastObject[13])){
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
										tempObj[14] = "";
										tempObj[4] = "";
										dataMap.put(userGroupType.getType(), tempObj);
									}
								}
							}
							CustomParameter onPaperSigningAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_CURRENTSTATUS_ONPAPER_SIGNING_AUTHORITY_"+cutMotionDate.getHouseType().getType(), "");
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
											 ref = UserGroup.findCutMotionDateActor(cutMotionDate, authority, String.valueOf(0), locale.toString());
										}else{
											 userGroupType = UserGroupType.
													findByFieldName(UserGroupType.class, "type", str, locale.toString());
											 ref = UserGroup.findCutMotionDateActor(cutMotionDate, str, String.valueOf(0), locale.toString());
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
											actor[4] = new String("");
											actor[13] = userGroupType.getType();
											actor[14] = new String("");
											dataMap.put(str, actor);
										}
									}
								}
							}
							model.addAttribute("data", dataMap);
							model.addAttribute("formatData", report.get(report.size()-1));
						}
					}
					
					/**** Load the department dates ****/
					CutMotionDateReportHelper.populateDepartmentDatesInModel(cutMotionDate, model, locale.toString());
	
					page = (cutMotionDate.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? "cutmotiondate/reports/statusreportlowerhouse": "cutmotiondate/reports/statusreportupperhouse";
				}		
	
				model.addAttribute("cutMotionDateId", cutMotionDate.getId());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	public static List generatetCurrentStatusReport(final CutMotionDate cutMotionDate, final String device, final String locale){
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("id",new String[]{cutMotionDate.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("CUTMOTIONDATE_CURRENTSTATUS_REPORT", parameters);
		for(Object o : list){
			Object[] data = (Object[]) o;
			String remarks = ((data[4] != null)? data[4].toString():"-");			
			((Object[])o)[4] = FormaterUtil.formatNumbersInGivenText(remarks, locale);
			data = null;
		}

		return list;  
	}
	
	private static void populateDepartmentDatesInModel(CutMotionDate domain, ModelMap model, String locale) {
		List<CutMotionDepartmentDateVO> departmentDateVOs = new ArrayList<CutMotionDepartmentDateVO>();
		if(domain.getId()!=null) {
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale});
			queryParameters.put("cutMotionDateId", new String[]{domain.getId().toString()});
			@SuppressWarnings("unchecked")
			List<Object[]> queryResult = Query.findReport("LOAD_CUTMOTIONDATE_DEPARTMENT_DATES", queryParameters);
			if(queryResult!=null && !queryResult.isEmpty()) {
				for(Object[] result: queryResult) {
					if(result!=null && result.length==5) {
						CutMotionDepartmentDateVO departmentDateVO = new CutMotionDepartmentDateVO();
						departmentDateVO.setDiscussionDate(result[0].toString());
						departmentDateVO.setFormattedDiscussionDate(result[1].toString());
						departmentDateVO.setSubmissionEndDate(result[2].toString());
						departmentDateVO.setFormattedSubmissionEndDate(result[3].toString());
						List<String[]> departments = new ArrayList<String[]>();
						if(result[4]!=null && !result[4].toString().isEmpty()) {
							String departmentsData = result[4].toString();
							for(String i: departmentsData.split("_;_")) {
								String[] departmentData = i.split("~");
								departmentData[2] = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(departmentData[2]), locale);
								departments.add(departmentData);
							}				
						}					
						departmentDateVO.setDepartments(departments);
						departmentDateVOs.add(departmentDateVO);
					}
				}
			}
		}		
		model.addAttribute("departmentDateVOs", departmentDateVOs);
	}
	
}