package org.mkcl.els.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.xmlvo.DeviceXmlVO;
import org.mkcl.els.common.xmlvo.ResolutionIntimationLetterXmlVO;
import org.mkcl.els.common.xmlvo.ResolutionXmlVO;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
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
@RequestMapping("resolution/report")
public class ResolutionReportController extends BaseController{
	
	private static Logger logger = LoggerFactory.getLogger(ResolutionReportController.class);
	
	@RequestMapping(value="/currentstatusreport", method=RequestMethod.GET)
	public String getCurrentStatusReport(final Model model, 
			final HttpServletRequest request, 
			final HttpServletResponse response, 
			final Locale locale){

		String strDevice = request.getParameter("device");
		String strReportType = request.getParameter("reportType");
		String strWfid = request.getParameter("wfdId");
		String strResolutionId = request.getParameter("rId");
		
		WorkflowDetails wfd = null;
		if(strWfid != null && !strWfid.isEmpty()){
			wfd = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWfid));
			if(wfd != null){
				model.addAttribute("rId", wfd.getDeviceId());
			}
		}
		
		if(strResolutionId != null && !strResolutionId.isEmpty()){
			model.addAttribute("rId", strResolutionId);
		}
		
		model.addAttribute("reportType", strReportType);
		if(strDevice != null && !strDevice.isEmpty()){		
			model.addAttribute("device", strDevice);
		}

		response.setContentType("text/html; charset=utf-8");
		return "resolution/reports/statusreport";
	}

	@RequestMapping(value="/{rId}/currentstatusreportvm", method=RequestMethod.GET)
	public String getCurrentStatusReportVM(@PathVariable("rId") Long id,
			final  Model model, 
			final HttpServletRequest request, 
			final HttpServletResponse response, 
			final Locale locale){
		response.setContentType("text/html; charset=utf-8");		
		return getCurrentStatusReportData(id, model, request, response, locale);
	}

	
	@SuppressWarnings("rawtypes")
	public static String getCurrentStatusReportData(final Long id, final Model model, 
			final HttpServletRequest request,
			final HttpServletResponse response, 
			final Locale locale){
		String page = "question/error";
		try{
			String strDevice = request.getParameter("device"); 
			if(strDevice != null && !strDevice.isEmpty()){
				Resolution resolution = Resolution.findById(Resolution.class, id);
				String strHouseType = resolution.getHouseType().getType();
				List report = generatetCurrentStatusReport(resolution, strDevice, locale.toString());		
				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();
				if(report != null && !report.isEmpty()){
					
					List<User> users = User.findByRole(false, "ROIS_PRINCIPALSECRETARY", locale.toString());
					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() + " " + users.get(0).getLastName());
	
					CustomParameter csptAllwedUserGroupForStatusReportSign = CustomParameter.findByName(CustomParameter.class, (strHouseType.equals(ApplicationConstants.LOWER_HOUSE)? "ROIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE": "ROIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE"), "");
					if(csptAllwedUserGroupForStatusReportSign != null){
						if(csptAllwedUserGroupForStatusReportSign.getValue() != null && !csptAllwedUserGroupForStatusReportSign.getValue().isEmpty()){
							String underSecretaryLevelActorUGT = ApplicationConstants.UNDER_SECRETARY;
							if(csptAllwedUserGroupForStatusReportSign.getValue().contains(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)) {
								underSecretaryLevelActorUGT = ApplicationConstants.UNDER_SECRETARY_COMMITTEE;
							}
							Object[] lastObject = (Object[]) report.get(report.size()-1); 
							for(Object o : report){
								Object[] objx = (Object[])o;
	
								if(objx[21] != null && !objx[21].toString().isEmpty()){
									if(csptAllwedUserGroupForStatusReportSign.getValue().contains(objx[21].toString())){
										
										UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", objx[21].toString(), locale.toString());
																				
										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
											if(dataMap.get(underSecretaryLevelActorUGT) != null){
												if(objx != null){
													if(objx[6] != null && objx[6].toString().length() > 0){
														dataMap.put(underSecretaryLevelActorUGT, objx);
													}else{
														Object[] tempObj = dataMap.get(underSecretaryLevelActorUGT);
														tempObj[22] = objx[22];
														
														dataMap.put(underSecretaryLevelActorUGT, tempObj);
													}
												}
											}else {
												dataMap.put(underSecretaryLevelActorUGT, objx);
											}
										}else{
											if(dataMap.get(userGroupType.getType()) != null){
												if(objx != null){
													if(objx[6] != null && objx[6].toString().length() > 0){
														dataMap.put(userGroupType.getType(), objx);
													}else{
														Object[] tempObj = dataMap.get(userGroupType.getType());
														tempObj[22] = objx[22];
														
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
							
							//Following block is added for solving the issue of question drafts where in if there exist a draft and later the question is pending
							// at the specific actor, the last remark is displayed
							WorkflowConfig wfConfig = null;
							if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
								wfConfig = WorkflowConfig.getLatest(resolution, resolution.getInternalStatusLowerHouse().getType(), locale.toString());
							}else{
								wfConfig = WorkflowConfig.getLatest(resolution, resolution.getInternalStatusUpperHouse().getType(), locale.toString());
							}
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
							WorkflowDetails  wfDetails = WorkflowDetails.findCurrentWorkflowDetail(resolution, resolution.getHouseType().getName());
							for(WorkflowActor wf : distinctActors){
								UserGroupType userGroupType = wf.getUserGroupType();
								if(userGroupType.getType().equals(lastObject[21])){
									if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
										for(WorkflowActor wf1 : distinctActors){
											UserGroupType ugt = wf1.getUserGroupType();
											if(ugt.getType().equals(underSecretaryLevelActorUGT)){
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
										tempObj[22] = "";
										tempObj[6] = "";
										dataMap.put(userGroupType.getType(), tempObj);
									}
								}
							}
							CustomParameter onPaperSigningAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "ROIS_CURRENTSTATUS_ONPAPER_SIGNING_AUTHORITY_"+strHouseType, "");
							if(onPaperSigningAuthorityParameter != null){
								String signingAuthority = onPaperSigningAuthorityParameter.getValue();
								String[] signingAuthorities = signingAuthority.split(",");
								for(String str : signingAuthorities){
									String authority = str;
									if(str.equals(ApplicationConstants.UNDER_SECRETARY) || str.equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE)){
										str = underSecretaryLevelActorUGT;
									}
									if(dataMap.get(str) == null){
										UserGroupType userGroupType = null;
										Reference ref = null;
										if(authority.equals(underSecretaryLevelActorUGT)){
											 userGroupType = UserGroupType.
													findByFieldName(UserGroupType.class, "type", authority, locale.toString());
											 ref = UserGroup.findResolutionActor(resolution, strHouseType,authority, String.valueOf(0), locale.toString());
										}else{
											 userGroupType = UserGroupType.
													findByFieldName(UserGroupType.class, "type", str, locale.toString());
											 ref = UserGroup.findResolutionActor(resolution,strHouseType, str, String.valueOf(0), locale.toString());
										}
										
										if(ref.getId() != null){
											Object[] actor = new Object[32];
											if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) 
													|| ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY1)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.DEPUTY_SECRETARY2)
													|| ref.getId().split("#")[1].equals(ApplicationConstants.JOINT_SECRETARY2)){
												actor[0] = new String(ref.getId().split("#")[3]);
												actor[1] = new String(ref.getId().split("#")[4]);
											}else{
												actor[0] = new String(ref.getId().split("#")[3]);
												actor[1] = new String("");
											}
											actor[3] = new String("");
											actor[6] = new String("");
											actor[21] = userGroupType.getType();
											actor[22] = new String("");
											actor[24] = null;
											dataMap.put(str, actor);
										}
									}
								}
							}
//				Map<String, Object[]> dataMap = new LinkedHashMap<String, Object[]>();	
//				if(report != null && !report.isEmpty()){
//										
//					List<User> users = User.findByRole(false, "ROIS_PRINCIPALSECRETARY", locale.toString());
//					model.addAttribute("principalSec", users.get(0).getTitle() + " " + users.get(0).getFirstName() 
//														+ " " + users.get(0).getLastName());
//					CustomParameter csptAllwedUserGroupForStatusReportSign = null;
//					if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
//						csptAllwedUserGroupForStatusReportSign = CustomParameter.
//								findByName(CustomParameter.class,  "ROIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_LOWERHOUSE", "");
//					}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
//						csptAllwedUserGroupForStatusReportSign = CustomParameter.
//								findByName(CustomParameter.class,  "ROIS_ALLOWED_USERGROUPS_FOR_STATUS_REPORT_SIGN_UPPERHOUSE", "");
//					}					
//					if(csptAllwedUserGroupForStatusReportSign != null){
//						if(csptAllwedUserGroupForStatusReportSign.getValue() != null 
//								&& !csptAllwedUserGroupForStatusReportSign.getValue().isEmpty()){
//							for(Object o : report){
//								Object[] objx = (Object[])o;
//	
//								if(objx[21] != null && !objx[21].toString().isEmpty()){
//									if(csptAllwedUserGroupForStatusReportSign.getValue().contains(objx[21].toString())){
//										
//										UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", objx[21].toString(), locale.toString());
//																				
//										if(userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || userGroupType.getType().equals(ApplicationConstants.UNDER_SECRETARY)){
//											if(dataMap.get(underSecretaryLevelActorUGT) != null){
//												if(objx != null){
//													if(objx[6] != null && objx[6].toString().length() > 0){
//														dataMap.put(underSecretaryLevelActorUGT, objx);
//													}else{
//														Object[] tempObj = dataMap.get(underSecretaryLevelActorUGT);
//														tempObj[22] = objx[22];
//														
//														dataMap.put(underSecretaryLevelActorUGT, tempObj);
//													}
//												}
//											}else{
//												dataMap.put(underSecretaryLevelActorUGT, objx);
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
//							if(dataMap.get(ApplicationConstants.OFFICER_ON_SPECIAL_DUTY) == null 
//									&& resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
//								UserGroupType userGroupType = UserGroupType.
//										findByFieldName(UserGroupType.class, "type", ApplicationConstants.OFFICER_ON_SPECIAL_DUTY, locale.toString());
//								
//								Object[] dataCollection = new Object[25];
//								dataCollection[0] = new String(userGroupType.getName());
//								dataCollection[1] = new String("");
//								dataCollection[3] = new String("");
//								dataCollection[6] = new String("");
//								dataCollection[21] = userGroupType.getType();
//								dataCollection[22] = new String("");
//								dataCollection[24] = null;
//								
//								dataMap.put(ApplicationConstants.OFFICER_ON_SPECIAL_DUTY, dataCollection);
//							}
//							
//							if(dataMap.get(ApplicationConstants.SECRETARY) == null 
//									&& resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
//								UserGroupType userGroupType = UserGroupType.
//										findByFieldName(UserGroupType.class, "type", ApplicationConstants.SECRETARY, locale.toString());
//								
//								Object[] dataCollection = new Object[25];
//								dataCollection[0] = new String(userGroupType.getName());
//								dataCollection[1] = new String("");
//								dataCollection[3] = new String("");
//								dataCollection[6] = new String("");
//								dataCollection[21] = userGroupType.getType();
//								dataCollection[22] = new String("");
//								dataCollection[24] = null;
//								
//								dataMap.put(ApplicationConstants.SECRETARY, dataCollection);
//							}
//							
//							if(dataMap.get(ApplicationConstants.PRINCIPAL_SECRETARY) == null){
//								UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.PRINCIPAL_SECRETARY, locale.toString());
//								
//								Object[] dataCollection = new Object[25];
//								dataCollection[0] = new String(userGroupType.getName());
//								dataCollection[1] = new String("");
//								dataCollection[3] = new String("");
//								dataCollection[6] = new String("");
//								dataCollection[21] = userGroupType.getType();
//								dataCollection[22] = new String("");
//								dataCollection[24] = null;
//								
//								dataMap.put(ApplicationConstants.PRINCIPAL_SECRETARY, dataCollection);
//							}
//							
//							if(dataMap.isEmpty()){
//								for(String val : csptAllwedUserGroupForStatusReportSign.getValue().split(",")){
//								
//									Reference ref = UserGroup.findResolutionActor(resolution,resolution.getHouseType().getType(), val, String.valueOf(0), locale.toString());
//									Object[] actor = new Object[30];
//									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
//										actor[0] = new String(ref.getId().split("#")[3]);
//										actor[1] = new String(ref.getId().split("#")[4]);
//									}else{
//										actor[0] = new String(ref.getId().split("#")[3]);
//										actor[1] = new String("");
//									}
//									actor[3] = new String("");
//									actor[6] = new String("");
//									actor[28] = new String("");
//									
//									if(ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY_COMMITTEE) || ref.getId().split("#")[1].equals(ApplicationConstants.UNDER_SECRETARY)){
//										dataMap.put(underSecretaryLevelActorUGT, actor);
//									}else{
//										dataMap.put(val, actor);
//									}
//								}
//							}
							Map<String, Object[]> sortedDataMap = new LinkedHashMap<String, Object[]>();
							for(WorkflowActor wfa:distinctActors){
								UserGroupType ugt = wfa.getUserGroupType();
								if(dataMap.containsKey(ugt.getType())){
									sortedDataMap.put(ugt.getType(), dataMap.get(ugt.getType()));
								}
							}
							model.addAttribute("data", sortedDataMap);
							model.addAttribute("formatData", report.get(report.size()-1));
						}
					}
					if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
						page = "resolution/reports/statusreportlowerhouse";
					}else{
						page = "resolution/reports/statusreportupperhouse";
					}
				}		
	
				model.addAttribute("rId", resolution.getId());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		return page;
	}
	
	@SuppressWarnings("rawtypes")
	public static List generatetCurrentStatusReport(final Resolution resolution, final String device, final String locale){
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale",new String[]{locale.toString()});
		parameters.put("rId",new String[]{resolution.getId().toString()});
		parameters.put("device", new String[]{device});

		List list = Query.findReport("ROIS_CURRENTSTATUS_REPORT", parameters);
		for(Object o : list){
			Object[] data = (Object[]) o;
			String details = ((data[4] != null)? data[4].toString():"-");
			String subject = ((data[5] != null)? data[5].toString():"-");
			String remarks = ((data[6] != null)? data[6].toString():"-");
		
			((Object[])o)[4] = FormaterUtil.formatNumbersInGivenText(details, locale);
			((Object[])o)[5] = FormaterUtil.formatNumbersInGivenText(subject, locale);
			((Object[])o)[6] = FormaterUtil.formatNumbersInGivenText(remarks, locale);
			data = null;
		}

		return list;  
	}
	
	
	@RequestMapping(value="/generateIntimationLetter" ,method=RequestMethod.GET)
	public @ResponseBody void generateIntimationLetter(final HttpServletRequest request,
			final HttpServletResponse response, 
			final Locale locale, 
			final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String strResolutionId = request.getParameter("resolutionId");		
		String strWorkflowId = request.getParameter("workflowId");
		String intimationLetterFilter = request.getParameter("intimationLetterFilter");

		//in case if request comes from workflow page, question id is retrived from workflow details
		if(strWorkflowId!=null && !strWorkflowId.isEmpty()) {
			WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.parseLong(strWorkflowId));
			if(workflowDetails!=null) {
				strResolutionId = workflowDetails.getDeviceId();
			}
		}

		if(strResolutionId!=null && !strResolutionId.isEmpty()) {
			Resolution resolution = Resolution.findById(Resolution.class, Long.parseLong(strResolutionId));
		
			if(resolution!=null) {
				ResolutionIntimationLetterXmlVO letterVO = new ResolutionIntimationLetterXmlVO();
				HouseType houseType = resolution.getHouseType();
				String strHouseType = resolution.getHouseType().getType();
				Status status = null;
				if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
					status = resolution.getInternalStatusLowerHouse();
				}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
					status = resolution.getInternalStatusUpperHouse();
				}
				
				String statusType=status.getType();
				DeviceType deviceType = resolution.getType();
				letterVO.setDeviceType(deviceType.getName());
				if(resolution.getNumber()!=null) {
					letterVO.setNumber(FormaterUtil.formatNumberNoGrouping(resolution.getNumber(), resolution.getLocale()));
				}
				
				if(houseType!=null) {
					letterVO.setHouseType(strHouseType);
					letterVO.setHouseTypeName(houseType.getName());
				}	
				Session session = resolution.getSession();
				if(session!=null) {					
					letterVO.setSessionPlace(session.getPlace().getPlace());
					if(session.getNumber()!=null) {
						letterVO.setSessionNumber(session.getNumber().toString());
					}		
					if(session.getYear()!=null) {
						letterVO.setSessionYear(FormaterUtil.formatNumberNoGrouping(session.getYear(), locale.toString()));
					}
				}

				String formattedText = "";
				if(resolution.getRevisedSubject()!=null && !resolution.getRevisedSubject().isEmpty()) {
					formattedText = resolution.getRevisedSubject();					
				} else {
					formattedText = resolution.getSubject();
				}
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, question.getLocale());
				letterVO.setSubject(formattedText);

				if(resolution.getRevisedNoticeContent()!=null && !resolution.getRevisedNoticeContent().isEmpty()) {
					formattedText = resolution.getRevisedNoticeContent();					
				} else {
					formattedText = resolution.getNoticeContent();
				}
								
				//formattedText = FormaterUtil.formatNumbersInGivenText(formattedText, question.getLocale());
				letterVO.setNoticeContent(formattedText);	
				/**** populating member names with customized formatting ****/
				String memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
				CustomParameter memberNameFormatParameter = null;
				if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_LOWERHOUSE", "");
				} else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "INTIMATIONLETTER_MEMBERNAMEFORMAT_UPPERHOUSE", "");
				}
				if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
					memberNameFormat = memberNameFormatParameter.getValue();
				}
				Member primaryMember = resolution.getMember();
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
				if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
					allMemberNames = resolution.findAllMemberNamesWithConstituencies(memberNameFormat);
				} else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
					allMemberNames = resolution.getMember().findNameInGivenFormat(memberNameFormat);
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
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.intimationLetter.noMemberFound", locale.toString());
				}	
				try {
					MemberMinister memberMinister = Resolution.findMemberMinisterIfExists(resolution);
					if(memberMinister!=null) {
						letterVO.setPrimaryMemberDesignation(memberMinister.getDesignation().getName());
					} else {
						letterVO.setPrimaryMemberDesignation("");
					}
				} catch(ELSException ex) {
					letterVO.setPrimaryMemberDesignation("");
				}
				SubDepartment subDepartment = resolution.getSubDepartment();
				if(subDepartment!=null) {
					letterVO.setSubDepartment(subDepartment.getName().trim());
					letterVO.setMinistryDisplayName(subDepartment.getMinistryDisplayName().trim());
				}
				Department department = subDepartment.getDepartment();
				if(department!=null) {
					letterVO.setDepartment(department.getName().trim());
				}
				letterVO.setIsSubDepartmentNameSameAsDepartmentName(false);
				if(letterVO.getDepartment()!=null && letterVO.getSubDepartment()!=null) {
					MessageResource departmentLabel = MessageResource.findByFieldName(MessageResource.class, "code", "generic.department", locale.toString());
					if(departmentLabel!=null) {
						String dept = letterVO.getDepartment();
						if(letterVO.getDepartment().endsWith(departmentLabel.getValue())) {
							dept = letterVO.getDepartment().split(departmentLabel.getValue())[0].trim();
						}	
						String subDept = letterVO.getSubDepartment();
						if(letterVO.getSubDepartment().endsWith(departmentLabel.getValue())) {
							subDept = letterVO.getSubDepartment().split(departmentLabel.getValue())[0].trim();
						}
						if(dept.equals(subDept)) {
							letterVO.setIsSubDepartmentNameSameAsDepartmentName(true);
						}
					}					
				}
				
				
				if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_REJECTION) || statusType.equals(ApplicationConstants.RESOLUTION_FINAL_REJECTION)|| statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION) || statusType.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION)) {
					if(resolution.getRejectionReason()!=null && !resolution.getRejectionReason().isEmpty()) {
						formattedText = resolution.getRejectionReason();
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
				
				/** factual position (clarification) received from department **/
				if(resolution.getFactualPosition()!=null) {
					formattedText = resolution.getFactualPosition();//FormaterUtil.formatNumbersInGivenText(question.getFactualPosition(), question.getLocale());
					if(formattedText.endsWith("<br><p></p>")) {
						formattedText = formattedText.substring(0, formattedText.length()-11);
					} else if(formattedText.endsWith("<p></p>")) {
						formattedText = formattedText.substring(0, formattedText.length()-7);
					}
					letterVO.setFactualPosition(formattedText);
				} else {
					letterVO.setFactualPosition("");
				} 
				
							
				if(resolution.getDiscussionDate()!=null) {
					letterVO.setDiscussionDate(FormaterUtil.formatDateToString(resolution.getDiscussionDate(), ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT, locale.toString()));
				}
				
				//==================status as per filter for clarification cases==============//
				//for starred
				if(statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBERANDDEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)
						|| statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATIONNEEDEDFROMMEMBERANDDEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.MEMBER)){
					statusType=ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER;
				}else if(statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBERANDDEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)
						|| statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATIONNEEDEDFROMMEMBERANDDEPARTMENT) && intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)){
					statusType=ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT;
				}
			
				//==================end of status as per filter for clarification cases==============//
				
				if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
						|| statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)
						|| statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
						|| statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)) {
					
					String questionsAsked = resolution.getQuestionsAskedInFactualPosition();
					if(questionsAsked==null || questionsAsked.isEmpty()) {
						if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)) {
							
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_questionsAskedForFactualPosition");
						
						} else if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)) {
							
							questionsAsked = session.getParameter(deviceType.getType().trim()+"_questionsAskedForFactualPosition");
						}
					} 
					
					if(questionsAsked!=null && !questionsAsked.isEmpty()) {
						List<MasterVO> questionsAskedForClarification = new ArrayList<MasterVO>();
						StringBuffer questionIndexesForClarification=new StringBuffer();	
						String allQuestions = "";
						if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
								|| statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)) {
							
							allQuestions = session.getParameter(deviceType.getType().trim()+"_questionsAskedForFactualPosition");
						
						} else if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
								|| statusType.equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)) {
							
							allQuestions = session.getParameter(deviceType.getType().trim()+"_questionsAskedForFactualPosition");
						}														
						for(String questionAsked : questionsAsked.split("##")) {
							MasterVO questionAskedForClarification = new MasterVO();
							questionAskedForClarification.setValue(questionAsked);
							questionsAskedForClarification.add(questionAskedForClarification);
							int index = 1;
							for(String allQuestion : allQuestions.split("##")) {
								if(questionAsked.equals(allQuestion)) {
									questionIndexesForClarification.append("(");
									questionIndexesForClarification.append(FormaterUtil.formatNumberNoGrouping(index, resolution.getLocale()));
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
						letterVO.setQuestionIndexesForClarification(questionIndexesForClarification.toString());
						letterVO.setQuestionsAskedForClarification(questionsAskedForClarification);
					}
				}
				String statusTypeSplit = statusType.split("_")[statusType.split("_").length-1];

				/**** In case username is required ****/
				Role role = Role.findByFieldName(Role.class, "type", "ROIS_PRINCIPALSECRETARY", locale.toString());
				List<User> users = User.findByRole(false, role.getName(), locale.toString());
				//as principal secretary for starred question is only one, so user is obviously first element of the list.
				letterVO.setUserName(users.get(0).findFirstLastName());

				/**** generate report ****/				
				try {
					String reportFileName = "intimationletter";
					if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
						reportFileName += "_nonofficial";
					} else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
						reportFileName += "_government";
					} 
					
					reportFileName += "(" + resolution.getNumber() + ")";
					if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
							|| statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)) {
						
						reportFileName += "_clarification(department)";
						
					} else if(statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
							|| statusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)) {
						
						if(intimationLetterFilter!=null && intimationLetterFilter.equals(ApplicationConstants.DEPARTMENT)) {
							reportFileName += "_clarification(department)";
						} else {
							reportFileName += "_clarification(member)";
						}						
						
					} else {
						statusTypeSplit=statusTypeSplit.replace("repeat","");
						reportFileName += "_" + statusTypeSplit;
					}					
					
					String outputFormat = "WORD";
					Set<Role> roles = this.getCurrentUser().getRoles();
					Role departmentRole = Role.findByType("ROIS_DEPARTMENT_USER", locale.toString());
					for(Role r :roles){
						if(r.getId().equals(departmentRole.getId())){
							outputFormat = "PDF";
							break;
						}
					}
					
					if(intimationLetterFilter!=null && !intimationLetterFilter.isEmpty() && !intimationLetterFilter.equals("-")) {
						try {
							reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+intimationLetterFilter+"_"+statusTypeSplit, outputFormat, reportFileName, locale.toString());
						} catch(FileNotFoundException e) {
							if(e.getMessage().equals(ApplicationConstants.XSLT_FILE_NOT_FOUND)) {
								reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+intimationLetterFilter, outputFormat, reportFileName, locale.toString());
							}
						}
						
					}else {
						reportFile = generateReportUsingFOP(letterVO, deviceType.getType()+"_intimationletter_"+statusTypeSplit, outputFormat, reportFileName, locale.toString());
					}					
					System.out.println("Intimation Letter generated successfully in WORD format!");

					openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
				} catch (Exception e) {
					e.printStackTrace();
					isError = true;
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "report.runtimeException.error", locale.toString());
				}				
			}			
		} else {
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.intimationLetter.noResolutionFound", locale.toString());
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
	
	@RequestMapping(value="/temporaryKaryavali", method=RequestMethod.GET)
	public String getKaryavaliNumberAndDate(HttpServletRequest request, ModelMap model, Locale locale) {
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
	
		String retVal = "resolution/reports/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");

		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType != null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty()){
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}					
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					if(houseType==null || sessionType==null) {
						logger.error("**** HouseType or SessionType Not Found ****");
						model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");											
					} else {
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOTFOUND");
						} else {
							DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
							model.addAttribute("sessionId", session.getId());
							Integer highestKaryavaliNumber = Resolution.findHighestKaryavaliNumber(deviceType, session, locale.toString());
							if(highestKaryavaliNumber!=null) {
								if(Resolution.isNumberedKaryavaliFilled(deviceType, session, highestKaryavaliNumber, locale.toString())) {
									model.addAttribute("karyavaliNumber", FormaterUtil.formatNumberNoGrouping(highestKaryavaliNumber+1, locale.toString()));
									model.addAttribute("karyavaliDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								} else {
									model.addAttribute("karyavaliNumber", FormaterUtil.formatNumberNoGrouping(highestKaryavaliNumber, locale.toString()));
									Date karyavaliDate = Resolution.findResolutionKaryavaliDate(deviceType, session, highestKaryavaliNumber, locale.toString());
									if(karyavaliDate!=null) {
										model.addAttribute("karyavaliDate", FormaterUtil.formatDateToString(karyavaliDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										model.addAttribute("isKaryavaliDateSet", "yes");
									}
								}								
							}
																				
							retVal = "resolution/reports/temporarykaryavaliinput";
						}
					}
				} catch(ELSException e) {
					model.addAttribute("error", e.getParameter("error"));		
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("errorcode", "SOME_EXCEPTION_OCCURED");
				}
			}
		}		
		return retVal;
	}
	
	@RequestMapping(value="/generateTemporarykaryavalireport" ,method=RequestMethod.GET)
	public @ResponseBody void generateTemporaryKaryavliReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		String strSessionId = request.getParameter("sessionId");
		String karyavaliNumber = request.getParameter("karyavaliNumber");
		String karyavaliDate = request.getParameter("karyavaliDate");
	    String reportFormat=request.getParameter("outputFormat");
	    ResolutionXmlVO data = new ResolutionXmlVO();
	    Session session = null;
	    if(strSessionId != null && !strSessionId.isEmpty()
	    	&& karyavaliNumber != null && !karyavaliNumber.isEmpty()
	    	&& karyavaliDate != null && !karyavaliDate.isEmpty()
	    	&& reportFormat != null && !reportFormat.isEmpty()){
	    	List<ChartVO> existingResolutions = new ArrayList<ChartVO>();
	    	try {
	    	CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
				if(csptServer.getValue().equals("TOMCAT")){
					karyavaliNumber = new String(karyavaliNumber.getBytes("ISO-8859-1"), "UTF-8");
					karyavaliDate = new String(karyavaliDate.getBytes("ISO-8859-1"), "UTF-8");							
					
				}
			}
	    	session = Session.findById(Session.class, Long.parseLong(strSessionId));
	    	data.setHouseType(session.getHouse().getType().getType());
	    	existingResolutions = Resolution.findResolutionsByKaryavaliNumber(session, Integer.parseInt(karyavaliNumber),locale);
			} catch (NumberFormatException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (ELSException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if(existingResolutions.isEmpty()){
	    		List<ChartVO> resolutions = new ArrayList<ChartVO>();
				try {
					Date karyavalidate = FormaterUtil.stringToDate(karyavaliDate, ApplicationConstants.SERVER_DATEFORMAT);
					resolutions = Resolution.findEligibleAdmittedResolution(session,Integer.parseInt(karyavaliNumber),karyavalidate,locale);
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ELSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    			            
	            data.setResolutionList(resolutions);
	            data.setKaryavaliNumber(Integer.parseInt(karyavaliNumber));
	         }else{
	    		 data.setResolutionList(existingResolutions);
	    		 data.setKaryavaliNumber(Integer.parseInt(karyavaliNumber));

	    	}
	    	
	    	 //generate report
    		try {
				reportFile = generateReportUsingFOP(data, "template_temporary_karyavalireport", reportFormat, "resolution_karyavali", locale.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
    		System.out.println("Resolution Karyavali Report generated successfully in " + reportFormat + " format!");
    		
    		openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
	    }
	  
	}
	
	
	@RequestMapping(value="/acceptanceLetter" ,method=RequestMethod.GET)
	public void generateAcceptanceLetter(final HttpServletRequest request,
			final HttpServletResponse response, 
			final Locale locale, 
			final ModelMap model) throws Exception{
		String strMember = request.getParameter("memberId");
		String strPosition = request.getParameter("position");
		String discussionDate = request.getParameter("discussionDate");
		
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("memberId", new String[]{strMember});
		params.put("discussionDate", new String[]{discussionDate});
		params.put("position", new String[]{strPosition});
		params.put("locale", new String[]{locale.toString()});	
		params.put("userName",new String[]{this.getCurrentUser().getActualUsername()});
		@SuppressWarnings("rawtypes")
		List data = Query.findReport("ROIS_ACCEPTANCE_LETTER", params);
		if(strMember != null && !strMember.isEmpty()){
			File f = generateReportUsingFOP(new Object[]{data}, "rois_acceptance_letter", request.getParameter("reportFormat"), "acceptanceLetter_"+strMember, locale.toString());
			openOrSaveReportFileFromBrowser(response, f, request.getParameter("reportFormat"));
		}
	}
	
	
	/***For all Kind of General Reports ***/
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/generalreport", method=RequestMethod.GET)
	public String getReport(HttpServletRequest request, Model model, Locale locale){
		
		Map<String, String[]> requestMap = request.getParameterMap();
		//List report = Query.findReport(request.getParameter("report"), requestMap);
		Boolean havingIN = Boolean.parseBoolean(request.getParameter("havingIN")); //optionally for selective parameter with IN query
		List report = Query.findReport(request.getParameter("report"), requestMap, havingIN);
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
		
		Role role = Role.findByType("ROIS_PRINCIPALSECRETARY", locale.toString());
		model.addAttribute("userRole", role.getType());
		
		return "resolution/reports/"+request.getParameter("reportout");
	}
	
	@RequestMapping(value="/online_offline_submission_count_report/init",method=RequestMethod.GET)
	public String initOnlineOfflineSubmissionCountReport(final HttpServletRequest request,final ModelMap model,final Locale locale) throws ELSException{
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String responsePage="resolution/reports/error";
		
		String strDeviceType = request.getParameter("deviceType");
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		
		if(strDeviceType!=null && strHouseType!=null && strSessionType!=null && strSessionYear!=null
				&& !strDeviceType.isEmpty() && !strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()) {
			
			/**** populate selected resolutiontype ****/
			DeviceType deviceType=DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
			if(deviceType==null) {
				deviceType=DeviceType.findByType(strDeviceType, locale.toString());
			}
			if(deviceType==null) {
				deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
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
			/**** submission start date of the session as default fromDate ****/
			String submissionStartDateSessionParameter = session.getParameter(deviceType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_START_DATE_SESSION_PARAMETER_KEY);
			if(submissionStartDateSessionParameter==null || submissionStartDateSessionParameter.isEmpty()) {
				logger.error("**** Submission start date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_undefined_for_session");
				return responsePage;
			}
			Date submissionStartDateForSession = FormaterUtil.formatStringToDate(submissionStartDateSessionParameter, ApplicationConstants.DB_DATEFORMAT);
			if(submissionStartDateForSession==null)  {
				logger.error("**** Submission start date parameter is set to invalid value for the session ****");
				model.addAttribute("errorcode", "submission_start_date_parameter_invalid_for_session");
				return responsePage;
			}
			model.addAttribute("defaultFromDate", FormaterUtil.formatDateToString(submissionStartDateForSession, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			/**** submission end date of the session as default toDate ****/
			String submissionEndDateSessionParameter = session.getParameter(deviceType.getType().trim()+"_"+ApplicationConstants.SUBMISSION_END_DATE_SESSION_PARAMETER_KEY);
			if(submissionEndDateSessionParameter==null || submissionEndDateSessionParameter.isEmpty()) {
				logger.error("**** Submission end date parameter is not set for the session ****");
				model.addAttribute("errorcode", "submission_end_date_parameter_undefined_for_session");
				return responsePage;
			}
			Date submissionEndDateForSession = FormaterUtil.formatStringToDate(submissionEndDateSessionParameter, ApplicationConstants.DB_DATEFORMAT);
			if(submissionEndDateForSession==null)  {
				logger.error("**** Submission end date parameter is set to invalid value for the session ****");
				model.addAttribute("errorcode", "submission_end_date_parameter_invalid_for_session");
				return responsePage;
			}
			model.addAttribute("defaultToDate", FormaterUtil.formatDateToString(submissionEndDateForSession, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			/**** Check whether current date is allowed for submission ****/
			Calendar currentDateCalendar = Calendar.getInstance();
			currentDateCalendar.setTime(new Date());	
//			currentDateCalendar.set(2015, 6, 12);
			currentDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
			currentDateCalendar.set(Calendar.MINUTE, 0);
			currentDateCalendar.set(Calendar.SECOND, 0);
			currentDateCalendar.set(Calendar.MILLISECOND, 0);
			if(	
				(submissionStartDateForSession.before(currentDateCalendar.getTime()) || submissionStartDateForSession.equals(currentDateCalendar.getTime()))
						&&
				(submissionEndDateForSession.after(currentDateCalendar.getTime()) || submissionEndDateForSession.equals(currentDateCalendar.getTime()))
			) {
				model.addAttribute("isCurrentDateValidForSubmission", true);
			} else {
				model.addAttribute("isCurrentDateValidForSubmission", false);
			}
			responsePage = "resolution/reports/online_offline_submission_count_report_init";
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
		String deviceType = request.getParameter("deviceType");
		String houseType = request.getParameter("houseType");
		String criteria = request.getParameter("criteria");
		String forTodayStr = request.getParameter("forToday");
		String fromDateStr = request.getParameter("fromDate");
		String toDateStr = request.getParameter("toDate");
		
		try {
			if(session!=null && !session.isEmpty() 
					&& deviceType!=null && !deviceType.isEmpty()
					&& houseType!=null && !houseType.isEmpty()
					&& criteria!=null && !criteria.isEmpty()) {
				Session sessionObj = Session.findById(Session.class, Long.parseLong(session));
				DeviceType deviceTypeObj = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
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
					Status submitStatus = Status.findByType(ApplicationConstants.RESOLUTION_SUBMIT, locale.toString());
					if(submitStatus!=null) {
						Map<String, String[]> queryParameters = new HashMap<String, String[]>();
						queryParameters.put("locale", new String[] {locale.toString()});
						queryParameters.put("sessionId", new String[] {session});
						queryParameters.put("deviceTypeId", new String[] {deviceType});
						queryParameters.put("houseTypeId", new String[] {houseType});												
						queryParameters.put("fromDate", new String[] {fromDateStr});
						queryParameters.put("toDate", new String[] {toDateStr});
						queryParameters.put("submitStatusId", new String[] {submitStatus.getId().toString()});
						
						String queryName = "ROIS_MEMBERWISE_RESOLUTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						if(criteria.equals("datewise")) {
							synchronized (Session.class) {								
								boolean isSubmissionDatesForSessionLoaded = Session.loadSubmissionDatesForDeviceTypeInSession(sessionObj, deviceTypeObj, fromDate, toDate);
								if(isSubmissionDatesForSessionLoaded==false) {
									//error
									isError = true;	
								}
							}
							queryName = "ROIS_DATEWISE_RESOLUTIONS_ONLINE_OFFLINE_SUBMISSION_COUNTS";
						}
						DeviceType resolutionType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
						List reportData = Query.findReport(queryName, queryParameters);
						if(reportData!=null && !reportData.isEmpty()) {
							/**** generate report ****/
							if(!isError) {										
								reportFile = generateReportUsingFOP(new Object[]{reportData, criteria}, "resolutions_online_submission_counts_template", "WORD", resolutionType.getType()+"_"+criteria+"_online_submission_counts_report", locale.toString());				
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
	
		//Print functionality for members for submitted resolutions
		//komala
		@RequestMapping(value="/resolutionPrintReport", method=RequestMethod.GET)
		public void getResolutionReport(HttpServletRequest request, HttpServletResponse response, Model model, Locale locale){
			File reportFile = null; 
			String resolutionId = request.getParameter("resolutionId");	
			String reportFormat=request.getParameter("outputFormat");
			
			Resolution resolution = Resolution.findById(Resolution.class, Long.parseLong(resolutionId));
			
			DeviceXmlVO deviceXmlVO = new DeviceXmlVO();
			deviceXmlVO.setHouseType(resolution.getType().getName());//deviceType
			if(resolution.getNumber()!=null){
				deviceXmlVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(resolution.getNumber(), locale.toString()));//question number
			}
			if(resolution.getSubmissionDate()!=null){
				deviceXmlVO.setSubmissionDate(FormaterUtil.formatDateToString(resolution.getSubmissionDate(), ApplicationConstants.SERVER_DATETIMEFORMAT, locale.toString()));//submission date
			}
			deviceXmlVO.setMemberNames(resolution.getMember().findFirstLastName());//Member Name
			deviceXmlVO.setSubject(resolution.getSubject());//subject
			deviceXmlVO.setContent(resolution.getNoticeContent());//Notice Content
			deviceXmlVO.setConstituency(resolution.getMember().findConstituency().getDisplayName());//constituency		
			Status memberStatus = resolution.findMemberStatus();
			deviceXmlVO.setStatus(memberStatus.getName());//status
			deviceXmlVO.setMinistryName(resolution.getMinistry().getName());//Ministry Name
			deviceXmlVO.setSubdepartmentName(resolution.getSubDepartment().getName());//Subdepartment name			
		
			try {
				reportFile = generateReportUsingFOP(deviceXmlVO, "template_resolution_report", reportFormat, "resolution_"+resolution.getNumber(), locale.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
		}
}
