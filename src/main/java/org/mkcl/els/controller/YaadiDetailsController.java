package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.YaadiDetails;
import org.mozilla.javascript.ObjArray;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/yaadi_details")
public class YaadiDetailsController extends BaseController {
	
	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String getYaadiDetailsPage(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		
		String retVal = "yaadi_details/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {
			String category = request.getParameter("category");
			model.addAttribute("category", category);
			/** Populate DeviceType */
			String strDeviceType = request.getParameter("deviceType");	
			DeviceType deviceType = 
					DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceTypeType", deviceType.getType());					
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				/** Populate Group*/
				String strGroup = request.getParameter("group");
				Group group = Group.findById(Group.class, Long.parseLong(strGroup));
				/** Compute & Add answeringDates to model*/
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				List<QuestionDates> questionDates = group.getQuestionDates();
				for(QuestionDates i : questionDates){
					String strAnsweringDate = 
							FormaterUtil.getDateFormatter(locale.toString()).format(i.findAnsweringDateForReport());
					MasterVO masterVO = new MasterVO(i.getId(), strAnsweringDate);
					masterVO.setValue(FormaterUtil.formatDateToString(i.getAnsweringDate(), ApplicationConstants.DB_DATEFORMAT));
					masterVOs.add(masterVO);
				}
				model.addAttribute("answeringDates", masterVOs);
			}
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);
			retVal = "yaadi_details/init";			
		} catch(Exception e) {
			logger.error("error", e);
			retVal = "ballot/error";
		}
		
		return retVal;
	}
	
	@RequestMapping(value="/generate_yaadi", method=RequestMethod.GET)
	public String populateYaadiDetails(HttpServletRequest request, ModelMap model, Locale locale) {
		String retVal = "yaadi_details/error";
		
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty()){
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findByName(strHouseType, locale.toString());
					}
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}										
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					if(houseType==null || sessionType==null) {
						logger.error("**** HouseType or SessionType Not Found ****");
						model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");											
					} else {
						model.addAttribute("houseTypeId", houseType.getId());
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOTFOUND");
						} else {
							model.addAttribute("sessionId", session.getId());
							DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
							if(deviceType==null) {								
								logger.error("**** Device Type Not Found ****");
								model.addAttribute("errorcode", "DEVICETYPE_NOTFOUND");
							} else {
								model.addAttribute("deviceTypeId", deviceType.getId());										
								Integer highestYaadiNumber = YaadiDetails.findHighestYaadiNumber(deviceType, session, locale.toString());					
								if(highestYaadiNumber!=null) {
									YaadiDetails yaadiDetails = null;
									List<Device> totalDevicesInYaadi = new ArrayList<Device>();
									if(highestYaadiNumber.intValue()>0) {
										yaadiDetails = YaadiDetails.find(deviceType, session, highestYaadiNumber, locale.toString());
									}	
									if(yaadiDetails==null || yaadiDetails.isNumberedYaadiFilled()) {
										/** populate Data for New Yaadi which is either first or latest **/
										yaadiDetails=null;
										model.addAttribute("yaadiDetailsId", "");
										model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber+1, locale.toString()));
										model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										totalDevicesInYaadi = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, 0, locale.toString());
									} else {
										/** populate Data for Highest Yaadi which is not yet filled **/
										model.addAttribute("yaadiDetailsId", yaadiDetails.getId());
										model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(yaadiDetails.getNumber(), locale.toString()));
										Date yaadiLayingDate = yaadiDetails.getLayingDate();
										if(yaadiLayingDate!=null) {
											model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
											model.addAttribute("isYaadiLayingDateSet", "yes");
										}
										if(yaadiDetails.getLayingStatus()!=null 
												&& (yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_READY)
													|| yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_LAID))
										) {
											totalDevicesInYaadi = yaadiDetails.getDevices();
										} else {
											List<Device> existingDevicesInYaadi = yaadiDetails.getDevices();
											totalDevicesInYaadi.addAll(existingDevicesInYaadi);
											List<Device> newlyAddedDevices = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, existingDevicesInYaadi.size(), locale.toString());
											if(newlyAddedDevices!=null && !newlyAddedDevices.isEmpty()) {
												totalDevicesInYaadi.addAll(newlyAddedDevices);
											}
										}																				
									}
									/** populate device vo **/									
									if(totalDevicesInYaadi!=null && !totalDevicesInYaadi.isEmpty()) {
										String yaadiDevicesCount = FormaterUtil.formatNumberNoGrouping(totalDevicesInYaadi.size(), locale.toString());
										model.addAttribute("yaadiDevicesCount", yaadiDevicesCount);
										List<DeviceVO> totalDevicesInYaadiVOs = populateDevicesForNumberedYaadi(totalDevicesInYaadi, locale.toString());
										totalDevicesInYaadiVOs = DeviceVO.sort(totalDevicesInYaadiVOs, "number", ApplicationConstants.ASC);
										model.addAttribute("totalDevicesInYaadiVOs", totalDevicesInYaadiVOs);
									}
									List<Date> availableYaadiLayingDates = Question.findAvailableYaadiLayingDatesForSession(null, session, locale.toString());
									if(availableYaadiLayingDates!=null && !availableYaadiLayingDates.isEmpty()) {
										List<String> yaadiLayingDates = new ArrayList<String>();
										for(Date eligibleDate: availableYaadiLayingDates) {
											yaadiLayingDates.add(FormaterUtil.formatDateToString(eligibleDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										}
										model.addAttribute("yaadiLayingDates", yaadiLayingDates);
									}
									/** populate group numbers **/
									CustomParameter deviceTypesHavingGroupCP = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_HAVING_GROUPS", "");
									if(deviceTypesHavingGroupCP!=null && deviceTypesHavingGroupCP.getValue()!=null && !deviceTypesHavingGroupCP.getValue().isEmpty()) {
										if(deviceTypesHavingGroupCP.getValue().contains(deviceType.getType())) {
											CustomParameter groupNumberLimitCP = CustomParameter.findByName(CustomParameter.class, "NO_OF_GROUPS", "");
											if(groupNumberLimitCP!=null && groupNumberLimitCP.getValue()!=null && !groupNumberLimitCP.getValue().isEmpty()) {
												Integer groupNumberLimit = Integer.parseInt(groupNumberLimitCP.getValue()); 
												List<Reference> groupNumbers = new ArrayList<Reference>();
												for(Integer i=1; i<=groupNumberLimit; i++) {
													Reference groupNumber = new Reference();
													groupNumber.setNumber(i.toString());
													groupNumber.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
													groupNumbers.add(groupNumber);
												}
												model.addAttribute("groupNumbers", groupNumbers);
											}
										}
									}
									/** populate yaadi statuses **/
									CustomParameter yaadiLayingStatusesCP = CustomParameter.findByName(CustomParameter.class, "YAADI_LAYING_STATUSES", "");		
									if(yaadiLayingStatusesCP!=null && yaadiLayingStatusesCP.getValue()!=null && !yaadiLayingStatusesCP.getValue().isEmpty()) {
										List<Status> yaadiLayingStatuses = Status.findStatusContainedIn(yaadiLayingStatusesCP.getValue(), locale.toString());
										model.addAttribute("yaadiLayingStatuses", yaadiLayingStatuses);
										if(yaadiDetails!=null && yaadiDetails.getLayingStatus()!=null && yaadiDetails.getLayingStatus().getId()!=null) {
											model.addAttribute("yaadiLayingStatus", yaadiDetails.getLayingStatus());
										}
									}
									/** populate whether to allow manually entering questions **/
									CustomParameter manuallyEnteringAllowedCP = CustomParameter.findByName(CustomParameter.class, "QIS_UNSTARRED_YAADI_MANUALLY_ENTERING_ALLOWED", "");
									if(manuallyEnteringAllowedCP!=null) {
										model.addAttribute("manuallyEnteringAllowed", manuallyEnteringAllowedCP.getValue());
									}
									retVal = "yaadi_details/"+ deviceType.getType().trim().toLowerCase() + "_yaadi";
								} else {
									logger.error("**** Error in Query of finding Yaadi Number ****");
									model.addAttribute("errorcode", "QUERY_ERROR");
								}								
							}										
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("device.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("error", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "SOME_EXCEPTION_OCCURED");
				}
			}
		}
		
		return retVal;
	}
	
	@Transactional
	@RequestMapping(value="/generate_yaadi" ,method=RequestMethod.POST)
	public @ResponseBody void generateYaadiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String houseTypeId = request.getParameter("houseType");
		String deviceTypeId = request.getParameter("deviceType");
		String sessionId = request.getParameter("sessionId");
		String yaadiDetailsId = request.getParameter("yaadiDetailsId");		
		String strYaadiNumber = request.getParameter("yaadiNumber");
		String strYaadiLayingDate = request.getParameter("yaadiLayingDate");
		String strChangedYaadiNumber = request.getParameter("changedYaadiNumber");
		String strChangedYaadiLayingDate = request.getParameter("changedYaadiLayingDate");
		String selectedDeviceIds = request.getParameter("selectedDeviceIds");
		String deSelectedDeviceIds = request.getParameter("deSelectedDeviceIds");
		String yaadiLayingStatusId = request.getParameter("yaadiLayingStatus");
		
		if(houseTypeId!=null && deviceTypeId!=null && sessionId!=null 
				&& strYaadiNumber!=null	&& strYaadiLayingDate!=null) {
			if(!houseTypeId.isEmpty() && !deviceTypeId.isEmpty() && !sessionId.isEmpty()
					&& !strYaadiNumber.isEmpty() && !strYaadiLayingDate.isEmpty() && !strYaadiLayingDate.equals("-")) {
				
				try {
					HouseType houseType = null;
					DeviceType deviceType = null;
					String device = null;
					Session session = null;				
					YaadiDetails yaadiDetails = null;
					Status existingLayingStatus = null;
					
					CustomParameter manuallyEnteringAllowedCP = CustomParameter.findByName(CustomParameter.class, "QIS_UNSTARRED_YAADI_MANUALLY_ENTERING_ALLOWED", "");
					boolean isManualEnteringAllowed = false;
					if(manuallyEnteringAllowedCP!=null && manuallyEnteringAllowedCP.getValue()!=null && manuallyEnteringAllowedCP.getValue().equalsIgnoreCase("true")) {
						isManualEnteringAllowed = true;
					}
					
					if(yaadiDetailsId!=null && !yaadiDetailsId.isEmpty()) {
						yaadiDetails = YaadiDetails.findById(YaadiDetails.class, Long.parseLong(yaadiDetailsId));
						if(yaadiDetails==null) {
							isError = true;
							logger.error("**** Check request parameters yaadiDetailsId for invalid value ****");
							errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.yaadiDetailsId.invalid", locale.toString());
						} else {
							houseType = yaadiDetails.getHouseType();
							deviceType = yaadiDetails.getDeviceType();
							device = yaadiDetails.getDevice();
							session = yaadiDetails.getSession();
							existingLayingStatus = yaadiDetails.getLayingStatus();
						}					
					} else {
						yaadiDetails = new YaadiDetails();
						yaadiDetails.setLocale(locale.toString());
						houseType = HouseType.findById(HouseType.class, Long.parseLong(houseTypeId));
						yaadiDetails.setHouseType(houseType);
						deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));
						yaadiDetails.setDeviceType(deviceType);
						if(deviceType!=null) {
							device = deviceType.getDevice();
						} else {
							device = "question_unstarred";						
						}
						yaadiDetails.setDevice(device);
						session = Session.findById(Session.class, Long.parseLong(sessionId));
						yaadiDetails.setSession(session);
					}
					if(!isError) {
						if(existingLayingStatus==null || !existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
							Status layingStatus = null;
							if(yaadiLayingStatusId!=null && !yaadiLayingStatusId.isEmpty()) {
								layingStatus = Status.findById(Status.class, Long.parseLong(yaadiLayingStatusId));
								if(layingStatus==null) {
									isError = true;
									logger.error("**** Check request parameters yaadiLayingStatusId for invalid value ****");
									errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.yaadiLayingStatusId.invalid", locale.toString());
								} else {
									yaadiDetails.setLayingStatus(layingStatus);
								}
							} else {
								layingStatus = Status.findByType(ApplicationConstants.YAADISTATUS_DRAFTED, locale.toString());
								if(layingStatus==null) {
									isError = true;
									logger.error("**** Status with type '" + ApplicationConstants.YAADISTATUS_DRAFTED + "' in locale " + locale.toString() + " not found****");
									errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.statusnotfound", locale.toString());
								} else {
									yaadiDetails.setLayingStatus(layingStatus);
								}
							}
						}						
						if(!isError) {							
//							CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
//							if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
//								if(csptServer.getValue().equals("TOMCAT")){
//									strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
//									strYaadiLayingDate = new String(strYaadiLayingDate.getBytes("ISO-8859-1"), "UTF-8");							
//									if(strChangedYaadiNumber!=null && !strChangedYaadiNumber.isEmpty()) {
//										strChangedYaadiNumber = new String(strChangedYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
//									}
//									if(strChangedYaadiLayingDate!=null && !strChangedYaadiLayingDate.isEmpty()) {
//										strChangedYaadiLayingDate = new String(strChangedYaadiLayingDate.getBytes("ISO-8859-1"), "UTF-8");
//									}							
//								}
//							}
							/** yaadi number **/
							Integer yaadiNumber = null;
							if(existingLayingStatus!=null 
									&& 
									( 
									  existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_READY)
											||
									  existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)
									)
							) {
								yaadiNumber = Integer.parseInt(strYaadiNumber);
							} else {
								if(strChangedYaadiNumber!=null && !strChangedYaadiNumber.isEmpty()) {
									yaadiNumber = Integer.parseInt(strChangedYaadiNumber);
								} else {
									yaadiNumber = Integer.parseInt(strYaadiNumber);
								}
							}							
							yaadiDetails.setNumber(yaadiNumber);
							/** yaadi laying date **/
							Date yaadiLayingDate = null;
							if(existingLayingStatus!=null && existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
								yaadiLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
							} else {
								if(strChangedYaadiLayingDate!=null && !strChangedYaadiLayingDate.isEmpty()
										&& !strChangedYaadiLayingDate.equals("-")) {
									yaadiLayingDate = FormaterUtil.formatStringToDate(strChangedYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								} else {
									yaadiLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
								}
							}							
							yaadiDetails.setLayingDate(yaadiLayingDate);
							/** yaadi devices **/
							List<Device> totalDevicesInYaadi = new ArrayList<Device>();
							if(existingLayingStatus==null || !existingLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
								if(yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_DRAFTED)) {
									/** yaadi removed devices **/
									List<Device> totalRemovedDevicesInYaadi = new ArrayList<Device>();	
									List<Device> existingRemovedDevicesInYaadi = yaadiDetails.getRemovedDevices();
									if(existingRemovedDevicesInYaadi!=null && !existingRemovedDevicesInYaadi.isEmpty()) {
										totalRemovedDevicesInYaadi.addAll(existingRemovedDevicesInYaadi);
									}		
									/** remove deselected devices **/
									if(deSelectedDeviceIds!=null && !deSelectedDeviceIds.isEmpty()) {
										List<Device> deSelectedDevicesInYaadi = new ArrayList<Device>();
										deSelectedDeviceIds = deSelectedDeviceIds.substring(0, deSelectedDeviceIds.length()-1);
										for(String deSelectedDeviceId: deSelectedDeviceIds.split(",")) {
											if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
												Question q = Question.findById(Question.class, Long.parseLong(deSelectedDeviceId));
												if(q!=null) {
													q.setYaadiNumber(null);
													q.setYaadiLayingDate(null);		
													q.simpleMerge();
													if(!isManualEnteringAllowed) {
														deSelectedDevicesInYaadi.add(q);
													}													
												}
											}							
										}
										totalRemovedDevicesInYaadi.addAll(deSelectedDevicesInYaadi);			
									}
									yaadiDetails.setRemovedDevices(totalRemovedDevicesInYaadi);
									/** yaadi selected devices **/					
									if(selectedDeviceIds!=null && !selectedDeviceIds.isEmpty()) {
										List<Device> selectedDevicesInYaadi = new ArrayList<Device>();
										selectedDeviceIds = selectedDeviceIds.substring(0, selectedDeviceIds.length()-1);
										for(String selectedDeviceId: selectedDeviceIds.split(",")) {
											if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
												Question q = Question.findById(Question.class, Long.parseLong(selectedDeviceId));
												if(q!=null) {
													q.setYaadiNumber(yaadiDetails.getNumber());
													q.setYaadiLayingDate(yaadiDetails.getLayingDate());
													q.simpleMerge();
													selectedDevicesInYaadi.add(q);
												}
											}							
										}
										totalDevicesInYaadi.addAll(selectedDevicesInYaadi);				
									}
									yaadiDetails.setDevices(totalDevicesInYaadi);									
								} else {
									List<Device> existingDevicesInYaadi = yaadiDetails.getDevices();
									for(Device d: existingDevicesInYaadi) {
										if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
											Question q = (Question) d;
											q.setYaadiNumber(yaadiDetails.getNumber());
											q.setYaadiLayingDate(yaadiDetails.getLayingDate());
											if(yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
												Status yaadiLaidStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale.toString());
												Status yaadiLaidStatusForGivenDeviceType = Question.findCorrespondingStatusForGivenQuestionType(yaadiLaidStatus, yaadiDetails.getDeviceType());
												q.setRecommendationStatus(yaadiLaidStatusForGivenDeviceType);
											}											
											q.simpleMerge();
										}
									}
								}
								/** save/update yaadi details **/
								if(yaadiDetails.getId()!=null) {
									yaadiDetails.merge();
								} else {
									yaadiDetails.persist();
								}
							}							
							if(yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_DRAFTED)) {
								/** regeneration only when 'manually entering questions' is not allowed **/
								if(!isManualEnteringAllowed) {
									Boolean isYaadiFilledWithSelectedDevices = yaadiDetails.isNumberedYaadiFilled();
									if(isYaadiFilledWithSelectedDevices!=null && isYaadiFilledWithSelectedDevices.equals(false)) {
										yaadiDetails = yaadiDetails.regenerate(totalDevicesInYaadi.size());
									}
								}								
							}
							/** gather report data**/
							if(yaadiDetails.getDevice()!=null && yaadiDetails.getDevice().startsWith("question")) {
								List<Question> totalQuestionsInYaadi = new ArrayList<Question>();
								for(Device d: yaadiDetails.getDevices()) {
									Question q = (Question) d;
									totalQuestionsInYaadi.add(q);								
								}
								totalQuestionsInYaadi = Question.sort(totalQuestionsInYaadi, "number", ApplicationConstants.ASC);
								Object[] reportData = QuestionReportHelper.prepareUnstarredYaadiData(session, totalQuestionsInYaadi, locale.toString());
								/**** generate report ****/
								if(!isError) {
									reportFile = generateReportUsingFOP(reportData, "template_unstarredYaadi_report_"+houseType.getType(), "WORD", "unstarred_question_yaadi", locale.toString());
									if(reportFile!=null) {
										System.out.println("Report generated successfully in word format!");
										openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
										//response.
									}
								}
							}
						}
					}									
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.numberOfQuestionsInYaadiParameterNotSet")) {
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.numberOfQuestionsInYaadiParameterNotSet", locale.toString());				
					} else {
						e.printStackTrace();
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
					}						
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}				
			} else {
				isError = true;
				logger.error("**** Check request parameters houseTypeId, deviceTypeId, sessionId, yaadiNumber, yaadiLayingDate for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.empty", locale.toString());
			}
		} else {
			isError = true;
			logger.error("**** Check request parameters houseTypeId, deviceTypeId, sessionId, yaadiNumber, yaadiLayingDate for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.null", locale.toString());
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
	
	@RequestMapping(value="/generateUnstarredYaadiReport/getYaadiNumberAndDate", method=RequestMethod.GET)
	public String getUnstarredYaadiNumberAndDateForYaadi(HttpServletRequest request, ModelMap model, Locale locale) {
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		
		String retVal = "question/reports/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");

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
						model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND_OR_SESSIONTYPE_NOTFOUND");											
					} else {
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOTFOUND");
							return retVal;
						} else {
							model.addAttribute("sessionId", session.getId());
							Integer highestYaadiNumber = Question.findHighestYaadiNumber(null, session, locale.toString());
							if(highestYaadiNumber!=null) {
								if(highestYaadiNumber==0) {
									model.addAttribute("errorcode", "UNSTARRED_YAADI_NOT_GENERATED_YET");
									return retVal;
								}
								model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber, locale.toString()));
								Date yaadiLayingDate = Question.findYaadiLayingDateForYaadi(null, session, highestYaadiNumber, locale.toString());
								if(yaadiLayingDate!=null) {
									model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								}
							}
							retVal = "yaadi_details/getUnstarredYaadiNumberAndDateForYaadi";
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("error", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "SOME_EXCEPTION_OCCURED");
				}
			}
		}		
		return retVal;
	}
	
	@RequestMapping(value="/generateUnstarredYaadiReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateUnstarredYaadiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String sessionId = request.getParameter("sessionId");
		String strYaadiNumber = request.getParameter("yaadiNumber");
		String strYaadiLayingDate = request.getParameter("yaadiLayingDate");
		
		if(sessionId!=null && strYaadiNumber!=null && strYaadiLayingDate!=null){
			if(!sessionId.isEmpty() && !strYaadiNumber.isEmpty() && !strYaadiLayingDate.isEmpty() && !strYaadiLayingDate.equals("-")){
				try {
					CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
						if(csptServer.getValue().equals("TOMCAT")){
							strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
							strYaadiLayingDate = new String(strYaadiLayingDate.getBytes("ISO-8859-1"), "UTF-8");						
						}
					}
					Session session = Session.findById(Session.class, Long.parseLong(sessionId));
					if(session==null) {
						logger.error("**** Session not found with request parameter sessionId ****");
						throw new ELSException();
					}		
					Integer yaadiNumber = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strYaadiNumber).intValue();
					Date yaadiLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
					List<Question> totalQuestionsInYaadi = Question.findQuestionsInNumberedYaadi(null, session, yaadiNumber, yaadiLayingDate, locale.toString());
					Object[] reportData = QuestionReportHelper.prepareUnstarredYaadiData(session, totalQuestionsInYaadi, locale.toString());
					/**** generate report ****/
					if(!isError) {
						reportFile = generateReportUsingFOP(reportData, "template_unstarredYaadi_report_"+session.getHouse().getType().getType(), "WORD", "unstarred_question_yaadi", locale.toString());
						if(reportFile!=null) {
							System.out.println("Unstarred Yaadi Report generated successfully in word format!");
							openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.numberOfQuestionsInYaadiParameterNotSet")) {
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.numberOfQuestionsInYaadiParameterNotSet", locale.toString());				
					} else {
						e.printStackTrace();
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
					}						
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}
			}else{
				isError = true;
				logger.error("**** Check request parameters sessionId, yaadiNumber, yaadiLayingDate for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters sessionId, yaadiNumber, yaadiLayingDate for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.null", locale.toString());
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
	
	@RequestMapping(value="/generateUnstarredSuchiReport/getYaadiNumberAndDate", method=RequestMethod.GET)
	public String getUnstarredYaadiNumberAndDateForSuchi(HttpServletRequest request, ModelMap model, Locale locale) {
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		
		String retVal = "question/reports/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");

		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()){
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}	
					model.addAttribute("houseTypeType", houseType.getType());
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
							return retVal;
						} else {
							model.addAttribute("sessionId", session.getId());
							Integer highestYaadiNumber = Question.findHighestYaadiNumber(null, session, locale.toString());
							if(highestYaadiNumber!=null) {
								if(highestYaadiNumber==0) {
									model.addAttribute("errorcode", "UNSTARRED_YAADI_NOT_GENERATED_YET");
									return retVal;
								}
								model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(highestYaadiNumber, locale.toString()));
								Date yaadiLayingDate = Question.findYaadiLayingDateForYaadi(null, session, highestYaadiNumber, locale.toString());
								if(yaadiLayingDate!=null) {
									model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								}
							}
							retVal = "yaadi_details/getUnstarredYaadiNumberAndDateForSuchi";
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("error", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "SOME_EXCEPTION_OCCURED");
				}
			}
		}		
		return retVal;
	}
	
	@RequestMapping(value="/generateUnstarredSuchiReport" ,method=RequestMethod.GET)
	public @ResponseBody void generateUnstarredSuchiReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		String sessionId = request.getParameter("sessionId");
		String strYaadiNumber = request.getParameter("yaadiNumber");
		String strYaadiLayingDate = request.getParameter("yaadiLayingDate");
		
		if(sessionId!=null && strYaadiNumber!=null && strYaadiLayingDate!=null){
			if(!sessionId.isEmpty() && !strYaadiNumber.isEmpty() && !strYaadiLayingDate.isEmpty() && !strYaadiLayingDate.equals("-")){
				try {
					CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
						if(csptServer.getValue().equals("TOMCAT")){
							strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
							strYaadiLayingDate = new String(strYaadiLayingDate.getBytes("ISO-8859-1"), "UTF-8");						
						}
					}
					Session session = Session.findById(Session.class, Long.parseLong(sessionId));
					if(session==null) {
						logger.error("**** Session not found with request parameter sessionId ****");
						throw new ELSException();
					}		
					Integer yaadiNumber = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strYaadiNumber).intValue();
					Date yaadiLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
					List<Question> totalQuestionsInYaadi = Question.findQuestionsInNumberedYaadi(null, session, yaadiNumber, yaadiLayingDate, locale.toString());
					String suchiParameter = request.getParameter("suchiParameter");
					Object[] reportData = null;
					if(suchiParameter!=null && suchiParameter.equalsIgnoreCase("session")) {
						reportData = QuestionReportHelper.prepareSessionwiseUnstarredYaadiDataForSuchi(session, totalQuestionsInYaadi, locale.toString());
					} else {
						reportData = QuestionReportHelper.prepareUnstarredYaadiData(session, totalQuestionsInYaadi, locale.toString());
					}					
					/**** generate report ****/
					if(!isError) {
						if(suchiParameter!=null && suchiParameter.equalsIgnoreCase("session")) {
							reportFile = generateReportUsingFOP(reportData, "template_unstarredSuchi_sessionwise_report", "WORD", "unstarred_question_sessionwise_suchi", locale.toString());
						} else {
							reportFile = generateReportUsingFOP(reportData, "template_unstarredSuchi_report", "WORD", "unstarred_question_suchi", locale.toString());
						}						
						if(reportFile!=null) {
							System.out.println("Unstarred Suchi Report generated successfully in word format!");
							openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("question.numberOfQuestionsInYaadiParameterNotSet")) {
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.numberOfQuestionsInYaadiParameterNotSet", locale.toString());				
					} else {
						e.printStackTrace();
						isError = true;					
						errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
					}						
				} catch(Exception e) {
					e.printStackTrace();
					isError = true;					
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
				}
			}else{
				isError = true;
				logger.error("**** Check request parameters sessionId, yaadiNumber, yaadiLayingDate for empty values ****");
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.empty", locale.toString());				
			}
		} else {			
			isError = true;
			logger.error("**** Check request parameters sessionId, yaadiNumber, yaadiLayingDate for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "question.unstarredYaadiReport.reqparam.null", locale.toString());
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
	
	@RequestMapping(value="/bulk_yaadi_update", method=RequestMethod.GET)
	public String initBulkYaadiUpdate(HttpServletRequest request, ModelMap model, Locale locale) {
		String retVal = "yaadi_details/error";
		
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty()){
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findByName(strHouseType, locale.toString());
					}
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}										
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					if(houseType==null) {
						logger.error("**** HouseType Not Found ****");
						model.addAttribute("errorcode", "HOUSETYPE_NOT_FOUND");
					} else if(sessionType==null) {
						logger.error("**** SessionType Not Found ****");
						model.addAttribute("errorcode", "SESSIONTYPE_NOT_FOUND");
					} else if(deviceType==null) {
						logger.error("**** DeviceType Not Found ****");
						model.addAttribute("errorcode", "DEVICETYPE_NOT_FOUND");
					} else {
						model.addAttribute("houseTypeId", houseType.getId());
						Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOT_FOUND");
						} else {
							/** populate yaadi statuses **/
							CustomParameter yaadiLayingStatusesCP = CustomParameter.findByName(CustomParameter.class, "YAADI_LAYING_STATUSES", "");		
							if(yaadiLayingStatusesCP!=null && yaadiLayingStatusesCP.getValue()!=null && !yaadiLayingStatusesCP.getValue().isEmpty()) {
								List<Status> yaadiLayingStatuses = Status.findStatusContainedIn(yaadiLayingStatusesCP.getValue(), locale.toString());
								model.addAttribute("yaadiLayingStatuses", yaadiLayingStatuses);
							}
							/**** populate yaadi laying dates available for selected session ****/							
							List<Date> availableYaadiLayingDates = Question.findAvailableYaadiLayingDatesForSession(null, session, locale.toString());
							if(availableYaadiLayingDates!=null && !availableYaadiLayingDates.isEmpty()) {
								List<MasterVO> yaadiLayingDates = new ArrayList<MasterVO>();
								for(Date eligibleDate: availableYaadiLayingDates) {
									MasterVO yaadiLayingDate = new MasterVO();
									yaadiLayingDate.setName(FormaterUtil.formatDateToString(eligibleDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
									yaadiLayingDate.setValue(FormaterUtil.formatDateToString(eligibleDate, ApplicationConstants.DB_DATEFORMAT));
									yaadiLayingDates.add(yaadiLayingDate);
								}
								model.addAttribute("yaadiLayingDates", yaadiLayingDates);
							}	
							/** populate yaadi count limit to update in one go **/
							CustomParameter yaadiBulkUpdateCountLimitCP = CustomParameter.findByName(CustomParameter.class, deviceType.getType().trim().toUpperCase()+"_YAADI_BULK_UPDATE_COUNT_LIMIT", "");		
							if(yaadiBulkUpdateCountLimitCP!=null && yaadiBulkUpdateCountLimitCP.getValue()!=null && !yaadiBulkUpdateCountLimitCP.getValue().isEmpty()) {
								model.addAttribute("yaadiBulkUpdateCountLimit", yaadiBulkUpdateCountLimitCP.getValue());
							} else {
								model.addAttribute("yaadiBulkUpdateCountLimit", "20");
							}
							retVal = "yaadi_details/"+deviceType.getType().trim()+"_bulk_yaadi_update";
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("device.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("errorcode", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("errorcode", "SOME_EXCEPTION_OCCURED");
				}				
			} else {
				model.addAttribute("errorcode", "REQUEST_PARAM_EMPTY");
			}
		} else {
			model.addAttribute("errorcode", "REQUEST_PARAM_NULL");
		}
		
		return retVal;
	}
	
	@RequestMapping(value = "/bulk_yaadi_selection", method = RequestMethod.GET)
	public String loadBulkYaadiDetails(HttpServletRequest request,
			ModelMap model,
			Locale locale){
		String returnVal = "yaadi_details/error";
		
		String strYaadiNumbers = request.getParameter("yaadiNumbers");
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
		
		if(strYaadiNumbers!=null && !strYaadiNumbers.isEmpty()
				&& strHouseType!=null && !strHouseType.isEmpty()
				&& strSessionType!=null && !strSessionType.isEmpty()
				&& strSessionYear!=null && !strSessionYear.isEmpty()
				&& strDeviceType!=null && !strDeviceType.isEmpty()) {
			try {
				CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(deploymentServerCP.getValue().equals("TOMCAT")){
					strYaadiNumbers = new String(strYaadiNumbers.getBytes("ISO-8859-1"),"UTF-8");		
				}
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				if(houseType==null) {
					houseType = HouseType.findByName(strHouseType, locale.toString());
				}
				if(houseType==null) {
					houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
				}										
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				Integer sessionYear = Integer.parseInt(strSessionYear);
				if(houseType==null) {
					logger.error("**** HouseType Not Found ****");
					model.addAttribute("errorcode", "HOUSETYPE_NOT_FOUND");
				} else if(sessionType==null) {
					logger.error("**** SessionType Not Found ****");
					model.addAttribute("errorcode", "SESSIONTYPE_NOT_FOUND");
				} else if(deviceType==null) {
					logger.error("**** DeviceType Not Found ****");
					model.addAttribute("errorcode", "DEVICETYPE_NOT_FOUND");
				} else {
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					if(session==null) {								
						logger.error("**** Session Not Found ****");
						model.addAttribute("errorcode", "SESSION_NOT_FOUND");
					} else {
						StringBuffer yaadiNumbers = new StringBuffer("");
						for(String yn: strYaadiNumbers.split(",")) {
							if(!yn.isEmpty() && yn.split("-").length>1) {
								int yaadiRangeFirstElement = Integer.parseInt(yn.split("-")[0].trim());
								int yaadiRangeLastElement = Integer.parseInt(yn.split("-")[1].trim());
								for(Integer i=yaadiRangeFirstElement; i<=yaadiRangeLastElement; i++) {
									yaadiNumbers.append(i.toString());
									yaadiNumbers.append(",");
								}
							} else {
								if(!yn.isEmpty()) {									
//									yaadiNumbers.append(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(yn.trim()));
									yaadiNumbers.append(Integer.parseInt(yn.trim()));
									yaadiNumbers.append(",");
								}								
							}
						}
						yaadiNumbers.deleteCharAt(yaadiNumbers.length()-1);
						Map<String, String[]> qparams = new HashMap<String, String[]>();
						qparams.put("locale", new String[]{locale.toString()});
						qparams.put("deviceTypeId", new String[]{strDeviceType});
						qparams.put("sessionId", new String[]{session.getId().toString()});
						qparams.put("yaadiNumbers", new String[]{yaadiNumbers.toString()});
						@SuppressWarnings("unchecked")
						List<Object[]> selectedYaadis = Query.findReport("YAADI_DETAILS_FOR_BULK_UPDATE", qparams, true);
						model.addAttribute("selectedYaadis", selectedYaadis);
						if(selectedYaadis!=null && !selectedYaadis.isEmpty()) {
							model.addAttribute("selectedYaadisCount", selectedYaadis.size());
						}
						returnVal = "yaadi_details/"+deviceType.getType().trim()+"_bulk_yaadi_selection";
					}					
				}					
			} catch(Exception e) {
				logger.error("Exception occured in fetching yaadi details");
				model.addAttribute("errorcode", "SOME_EXCEPTION_OCCURRED");
			}
		} else {
			model.addAttribute("errorcode", "REQUEST_PARAM_NULL");
		}
		
		return returnVal;
	}
	
	@Transactional
	@RequestMapping(value="/bulk_yaadi_update", method=RequestMethod.POST)
	public String bulkYaadiUpdate(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		boolean updated = false;
		StringBuffer success = new StringBuffer();
		
		try{
			String[] selectedItems = request.getParameterValues("items[]");
			String strDeviceType = request.getParameter("deviceType");
			String strYaadiLayingStatus = request.getParameter("yaadiLayingStatus");
			String strYaadiLayingDate = request.getParameter("yaadiLayingDate");			
			
			if(selectedItems != null && selectedItems.length > 0
					&& strDeviceType != null && !strDeviceType.isEmpty()
					&& strYaadiLayingStatus != null && !strYaadiLayingStatus.isEmpty()
					&& strYaadiLayingDate != null && !strYaadiLayingDate.isEmpty()) {
				/**** As It Is Condition ****/
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				Status yaadiLaidStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale.toString());
				Status yaadiLaidStatusForGivenDeviceType = Question.findCorrespondingStatusForGivenQuestionType(yaadiLaidStatus, deviceType);
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					YaadiDetails yd = YaadiDetails.findById(YaadiDetails.class, id);
					boolean isStatusUpdateRequiredForDevices = false;
					boolean isLayingDateUpdateRequiredForDevices = false;					
					if(!strYaadiLayingStatus.equals("-")) {
						Status newYaadiLayingStatus = Status.findById(Status.class, new Long(strYaadiLayingStatus));
						Status existingYaadiLayingStatus = yd.getLayingStatus();
						yd.setLayingStatus(newYaadiLayingStatus);
						if(!newYaadiLayingStatus.getId().equals(existingYaadiLayingStatus.getId())
								&& newYaadiLayingStatus.getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
							
							isStatusUpdateRequiredForDevices = true;
						}
					}		
					Date newLayingDate = FormaterUtil.formatStringToDate(strYaadiLayingDate, ApplicationConstants.DB_DATEFORMAT);
					if(!strYaadiLayingDate.equals("-")) {						
						Date existingLayingDate = yd.getLayingDate();
						yd.setLayingDate(newLayingDate);
						if(newLayingDate.compareTo(existingLayingDate)!=0) {
							
							isLayingDateUpdateRequiredForDevices = true;
						}
					}
					yd.merge();
					YaadiDetails.updateDevices(yd, isStatusUpdateRequiredForDevices, yaadiLaidStatusForGivenDeviceType, isLayingDateUpdateRequiredForDevices, newLayingDate);
					updated = true;
					success.append(FormaterUtil.formatNumberNoGrouping(yd.getNumber(), yd.getLocale())+",");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			updated = false;
		}
		
		if(updated){
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());			
		}else{
			model.addAttribute("failure", "update failed.");			
		}
		
		return loadBulkYaadiDetails(request, model, locale);
	}
	
	@RequestMapping(value = "/publish_suchi_on_selected_answering_date", method = RequestMethod.POST)
    public @ResponseBody int publishSuchiOnSelectedAnsweringDate(final HttpServletRequest request,final Locale locale) {
		int updateStatus = 0;
		String answeringDateId = request.getParameter("answeringDate");
		if(answeringDateId!=null && !answeringDateId.isEmpty()) {
			try {
				QuestionDates answeringDateForSuchi = QuestionDates.findById(QuestionDates.class, Long.parseLong(answeringDateId));
				if(answeringDateForSuchi!=null) {
					if(answeringDateForSuchi.getSuchiPublished()==null || answeringDateForSuchi.getSuchiPublished().equals(false)) {
						answeringDateForSuchi.setSuchiPublished(true);
						answeringDateForSuchi.setSuchiPublishingDate(new Date());
					}
					answeringDateForSuchi.merge();
					updateStatus = 1;
				}
			} catch(Exception e) {
				e.printStackTrace();
				updateStatus = 0;
			}			
		}
		return updateStatus;
	}

}
