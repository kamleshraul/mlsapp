package org.mkcl.els.controller.edis;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.CatchwordHeadingVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProceedingVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.common.vo.VishaysuchiDeviceVO;
import org.mkcl.els.common.vo.VishaysuchiVO;
import org.mkcl.els.common.xmlvo.VishaysuchiXMLVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.EditorPart;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/editing")
public class EditingController extends GenericController<Roster>{
	
	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {
		
		try{
			/**** House Types ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			String houseType=this.getCurrentUser().getHouseType();
			if(houseType.equals("lowerhouse")){
				houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals("upperhouse")){
				houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals("bothhouse")){
				houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}
			model.addAttribute("houseTypes", houseTypes);
			if(houseType.equals("bothhouse")){
				houseType="lowerhouse";
			}
			model.addAttribute("houseType",houseType);
	
			/**** Session Types ****/
			List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
			HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
			Session lastSessionCreated=Session.findLatestSession(authUserHouseType);
			Integer year=new GregorianCalendar().get(Calendar.YEAR);
			if(lastSessionCreated.getId()!=null){
				year=lastSessionCreated.getYear();
				model.addAttribute("sessionType",lastSessionCreated.getType().getId());
			}else{
				model.addAttribute("errorcode","nosessionentriesfound");
			}
			model.addAttribute("sessionTypes",sessionTypes);
				
			/**** Years ****/
			CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			List<Integer> years=new ArrayList<Integer>();
			if(houseFormationYear!=null){
				Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
				for(int i=year;i>=formationYear;i--){
					years.add(i);
				}
			}else{
				model.addAttribute("errorcode", "houseformationyearnotset");
			}
			model.addAttribute("years",years);
			model.addAttribute("sessionYear",year);	
			
			
			/****Members available for the proceeding****/
			
			/**** Roles ****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			for(Role i:roles){
				model.addAttribute("role",i.getType());
				break;
			}

			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			for(UserGroup ug : userGroups){
				if(ug != null){
					model.addAttribute("userGroup", ug.getId());
					model.addAttribute("userGroupType", ug.getUserGroupType().getType());
					break;
				}
			}
			//-----XVX-----			
			/****
			 * Custom Parameter To Determine The Usergroup and usergrouptype
			 * of edis users . here we are determining what status will be
			 * shown to a particular user.
			 ****/
			if (userGroups != null) {
				if (!userGroups.isEmpty()) {
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"EDIS_ALLOWED_USERGROUPTYPES", "");
					if (customParameter != null) {
						String allowedUserGroups = customParameter.getValue();
						for (UserGroup i : userGroups) {
							if (!allowedUserGroups.contains(i.getUserGroupType().getType())) {
								model.addAttribute("errorcode", "edis_allowed_usergroups_notset");
								break;
							}
						}
					} else {
						model.addAttribute("errorcode", "edis_allowed_usergroups_notset");
					}
				} else {
					model.addAttribute("errorcode", "current_user_has_no_usergroups");
				}
			}
			//-----XVX-----
			List<MasterVO> days = new ArrayList<MasterVO>();
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			parameters.put("locale", new String[]{locale});
			List rosterDays = Query.findReport("EDIS_DISTINCT_ROSTER_DAYS", parameters);
			for(Object o : rosterDays){
				MasterVO mV = new MasterVO();
				Integer number = Integer.valueOf(o.toString()); 
				mV.setNumber(number);
				mV.setValue(FormaterUtil.formatNumberNoGrouping(number, locale));
				days.add(mV);
				number = null;
			}
			
           
			/****Days****/
			model.addAttribute("days",days);
			
			/**** Language ****/
			List<Language> languages=Language.findAllLanguagesByModule("RIS",locale);
			model.addAttribute("languages",languages);
			
			
			/**** Report Types ****/
			CustomParameter csptReport = CustomParameter.findByName(CustomParameter.class, "EDITING_REPORT_TYPES", "");
			List<MasterVO> reportTypes = new ArrayList<MasterVO>(); 
			if(csptReport != null){
				if(csptReport.getValue() != null && !csptReport.getValue().isEmpty()){
					for(String value : csptReport.getValue().split(",")){
						MasterVO mVO = new MasterVO();
						String[] splitValue = value.split(":");
						mVO.setValue(splitValue[0]);
						mVO.setName(splitValue[1]);
						reportTypes.add(mVO);
						mVO = null;
					}
				}
			}
			model.addAttribute("reportTypes", reportTypes);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void populateList(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {
		
		 /** Setting Open Word Flag **/			
		model.addAttribute("openWordFlag", false);
	}
	
	/**
	 * @param request
	 * @param model
	 * @param locale
	 * @return
	 * 
	 * <h3>Used to produce the report for viewing in various scenarios</h3>
	 */
	@Transactional
	@RequestMapping(value="/compiledreport", method=RequestMethod.GET)
	public String viewEditingReport(final HttpServletRequest request, final ModelMap model, final Locale locale){
			
		String retVal= "editing/error"; 
		
		model.addAttribute("undoCount", 0);
		model.addAttribute("redoCount", 0);
		
		try{
			
			/****To find the roster****/
			/****To find the session ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			
			String strLanguage = request.getParameter("language");
			String strDay = request.getParameter("day");
			
			/****To find the report type i.e. member or anyother****/
			String strReportType = request.getParameter("reportType");
			/****What action to be allowed at client side****/
			String strAction = request.getParameter("action");
			/****Is it a reedit action****/
			String strReedit = request.getParameter("reedit");
			/****Member id if any****/
			String strMember = request.getParameter("member");
			String strMemberReportType = request.getParameter("memberReportType");
			/****Heading of the page which is to be searched****/
			String strEncodedPageHeader = request.getParameter("pageheader");
			String strPageHeader = "";
			
			/****For decoding the ISO-8859-1 encoded marathi characters****/
			CustomParameter csptDeploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			
			/****For removing the drafted copies if configured to do so****/
			CustomParameter includeWFCopy = CustomParameter.findByName(CustomParameter.class, "EDIS_EDIT_INCLUDE_WF_COPY", "");
			if(includeWFCopy != null){
				if(includeWFCopy.getValue() != null && !includeWFCopy.getValue().isEmpty()){
					model.addAttribute("includeWFCopy", includeWFCopy.getValue());
				}
			}
			
			/****Proceed if all the required parameters are provided****/
			if((strReportType != null && !strReportType.isEmpty())
					&& (strMember != null && !strMember.isEmpty())
					&& (strMemberReportType != null && !strMemberReportType.isEmpty())
					&& (strEncodedPageHeader != null && !strEncodedPageHeader.isEmpty())){
				
				//produce normal report of rosterwise
				if(strReportType.equals("-")){
					strReportType = "other";
				}else{
					//produce reports based on the type of the report
					if(strReportType.equals("member")){
						if(strMember.equals("-")){
							strReportType = "nothing";
						}else{
							if(strMemberReportType.equals("-")){
								strReportType = "nothing";
							}else{
								if(strMemberReportType.equals("member")){
									strReportType = "memberS";
								}else if(strMemberReportType.equals("asmember")){
									strReportType = "member";
								}else if(strMemberReportType.equals("pageheading")){
									strReportType = "memberPageHeading";
									if(csptDeploymentServer != null){
										if(csptDeploymentServer.getValue() != null && !csptDeploymentServer.getValue().isEmpty()){
											if(csptDeploymentServer.getValue().equals("TOMCAT")){
												strPageHeader = new String(strEncodedPageHeader.getBytes("ISO-8859-1"), "UTF-8");
											}
										}
									}
								}
							}
						}
					}else if(strReportType.equals("pageheading")){
						if(strEncodedPageHeader.equals("-")){
							strReportType = "nothing";
						}else{
							strReportType = "pageHeading";
							
							if(csptDeploymentServer != null){
								if(csptDeploymentServer.getValue() != null && !csptDeploymentServer.getValue().isEmpty()){
									if(csptDeploymentServer.getValue().equals("TOMCAT")){
										strPageHeader = new String(strEncodedPageHeader.getBytes("ISO-8859-1"), "UTF-8");
									}
								}
							}
						}
					}
				}
			}
			
			/*
			 * Supply the action, reportType and if its a re-edit action on 
			 * client page
			 * 
			 */
			model.addAttribute("action", strAction);
			model.addAttribute("reedit", strReedit);
			model.addAttribute("reportType", strReportType);
			model.addAttribute("inPlaceOf","यांच्याकरिता");
			List result = null;
			//fetch report based on the report type provided
			//using the categorised report queries 
			if(!strReportType.isEmpty() && !strReportType.equals("nothing")){
				if(strHouseType!=null&&!strHouseType.equals("")&&
						strSessionType!=null&&!strSessionType.equals("")&&
						strSessionYear!=null&&!strSessionYear.equals("")&&
						strLanguage!=null&&!strLanguage.equals("")&&
						strDay!=null&&!strDay.equals("")){
	
					//find session and roster for that particular day
					HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
					SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
					Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
					Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
					
					//if cleanup is setup then clean
					//unwanted drafts(i.e. used for replace and undo facility)
					CustomParameter csptDoCleanUnusedDrafts = CustomParameter.findByName(CustomParameter.class, "EDIS_CLEAN_UNUSED_DRAFTS", "");
					if(csptDoCleanUnusedDrafts != null){
						if(csptDoCleanUnusedDrafts.getValue() != null && !csptDoCleanUnusedDrafts.getValue().isEmpty()){
							boolean doCleanUnusedDrafts = Boolean.parseBoolean(csptDoCleanUnusedDrafts.getValue());
							if(doCleanUnusedDrafts){
								cleanFacilityDrafts(request, roster, locale);
							}
						}
					}
					
					//create parameter map to fetch report
					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("languageId", new String[]{language.getId().toString()});
					parametersMap.put("rosterId", new String[]{roster.getId().toString()});
					
					//fetch report
					if(strReportType.equals("other")){
						result = Query.findReport("EDIS_PROCEEDING_CONTENT_MERGE_REPORT2", parametersMap);
					}else if(strReportType.equals("member")){
						if(strMember != null && !strMember.isEmpty()){
							parametersMap.put("memberId", new String[]{strMember});
							result=Query.findReport("EDIS_PROCEEDING_MEMBER_REPORT", parametersMap);
						}
					}else if(strReportType.equals("device")){
						//TODO: find parts where the device matches
					}else if(strReportType.equals("memberS")){
						if(strMember != null && !strMember.isEmpty()){
							parametersMap.put("memberId", new String[]{strMember});
							result=Query.findReport("EDIS_PROCEEDING_MEMBERS_REPORT", parametersMap);
						}
						//TODO: find parts where current member has spoken
					}else if(strReportType.equals("pageHeading")){
						//TODO: find parts where given heading matches
						parametersMap.put("partId", new String[]{strPageHeader});
						result=Query.findReport("EDIS_PROCEEDING_PAGEHEADING_REPORT", parametersMap);
					}else if(strReportType.equals("memberPageHeading")){
						//TODO: find the parts where given member has spoken on the given page heading
						parametersMap.put("partId", new String[]{strPageHeader});
						parametersMap.put("memberId", new String[]{strMember});
						result=Query.findReport("EDIS_PROCEEDING_MEMBER_PAGEHEADING_REPORT", parametersMap);
					}
					
					//if re-edit request then clean the drafts and allow re-edit 
					if(strReedit != null && !strReedit.isEmpty()){
						if(strReedit.equals("true")){
							if(!result.isEmpty()){
								for(int i = 0; i < result.size(); i++){
									String strId = (((Object[])result.get(i))[20]).toString();
									Part partToDel = Part.findById(Part.class, Long.valueOf(strId));
									List<PartDraft> pds = partToDel.getPartDrafts();
									
									partToDel.setEditedContent("");
									partToDel.setPartDrafts(null);
									
									int length = pds.size();
									for(int k = 0; i < length; i++){
										
										if(!pds.get(0).isWorkflowCopy()){
											PartDraft pd = pds.get(k);
											pds.remove(k);
											pd.remove();
										}
									}															
									partToDel.setPartDrafts(pds);
									partToDel.merge();
									
								}
							}
							
							//find reports for re-edit
							if(strReportType.equals("other")){
								result = Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);
							}else if(strReportType.equals("member")){
								if(strMember != null && !strMember.isEmpty()){
									parametersMap.put("memberId", new String[]{strMember});
									result=Query.findReport("EDIS_PROCEEDING_MEMBER_REPORT", parametersMap);
								}
							}else if(strReportType.equals("device")){
								//TODO: find parts where the device matches
							}else if(strReportType.equals("memberS")){
								if(strMember != null && !strMember.isEmpty()){
									parametersMap.put("memberId", new String[]{strMember});
									result=Query.findReport("EDIS_PROCEEDING_MEMBERS_REPORT", parametersMap);
								}
								//TODO: find parts where current member has spoken
							}else if(strReportType.equals("pageHeading")){
								//TODO: find parts where given heading matches
								parametersMap.put("partId", new String[]{strPageHeader});
								result=Query.findReport("EDIS_PROCEEDING_PAGEHEADING_REPORT", parametersMap);
							}else if(strReportType.equals("memberPageHeading")){
								//TODO: find the parts where given member has spoken on the given page heading
								parametersMap.put("partId", new String[]{strPageHeader});
								parametersMap.put("memberId", new String[]{strMember});
								result=Query.findReport("EDIS_PROCEEDING_MEMBER_PAGEHEADING_REPORT", parametersMap);
							}
						}
					}
					
					//place report in model
					model.addAttribute("report", result);
					
					retVal = "editing/compileedit";
				}
			}
					
		}catch (Exception e) {
			if(e instanceof NoResultException){
				model.addAttribute("error", "No roster found for the day");
				model.addAttribute("errorcode", "PARAMETER_MISMATCH");
			}
			logger.debug("EditingController_viewEditingReport", e);
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * @param id
	 * @param request
	 * @param locale
	 * @return
	 * 
	 * <h3>Save the draft after creating them.
	 */
	@Transactional
	@RequestMapping(value="/savepart/{id}", method=RequestMethod.POST)
	public @ResponseBody MasterVO saveDraft(@PathVariable(value="id") Long id, HttpServletRequest request, Locale locale){
		MasterVO masterVO = null;
		//find the part for which draft is to be created
		//and save the draft
		try{
			//find the required data to be saved in the draft 
			String editedContent = request.getParameter("editedContent");
			String strUndoCount = request.getParameter("undoCount");
			Part part = Part.findById(Part.class, id);
			String strUserGroupType = request.getParameter("userGroupType");
			UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", strUserGroupType, locale.toString());
			
			if(part != null){
				/****Create the part draft****/
				AuthUser user = this.getCurrentUser();
				
				PartDraft draft = new PartDraft();
				draft.setEditedBy(user.getActualUsername());
				draft.setEditedAs(userGroupType.getName());
				draft.setEditedOn(new Date());
				draft.setLocale(part.getLocale());
				draft.setMainHeading(part.getMainHeading());
				draft.setPageHeading(part.getPageHeading());
				draft.setUndoCount(Integer.valueOf(strUndoCount));
				draft.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
				draft.setRevisedContent(editedContent);
				if(part.getEditedContent() != null){
					draft.setOriginalText(part.getEditedContent());
				}else{
					draft.setOriginalText(part.getRevisedContent());
				}
				draft.setReplacedText(editedContent);
				draft.setWorkflowCopy(false);
				
				//save the draft
				part.getPartDrafts().add(draft);
				part.setEditedContent(editedContent);
				part.merge();
				
				//create an unique id and return as response so as to 
				//carry-out undo re-do facility
				masterVO = new MasterVO();
				masterVO.setId(part.getId());
				/*masterVO.setValue(part.getId()+":"+draft.getUndoCount()+":"+draft.getUniqueIdentifierForUndo());*/
				masterVO.setValue(draft.getUndoCount()+":"+draft.getUniqueIdentifierForUndo());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return masterVO;
	}

	/**
	 * @param id
	 * @param request
	 * @param model
	 * @param locale
	 * @return
	 * Fetches the versions of the part edited
	 */
	@RequestMapping(value="/revisions/{id}", method=RequestMethod.GET)	
	public String getRevisions(@PathVariable(value="id") Long id, HttpServletRequest request, ModelMap model, Locale locale){
		String retVal = "editing/error";
		try{
			String strWfCopy = request.getParameter("includeWfCopy");
			Boolean includeWfCopy = Boolean.valueOf(strWfCopy);
					
			model.addAttribute("drafts", Part.findRevision(id, includeWfCopy, locale.toString()));
			retVal = "editing/revisions";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retVal;
	}
	
	@RequestMapping(value="/editorreport", method=RequestMethod.GET)
	public void generateEditingReports(HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		try {

			String strReportFormat = request.getParameter("outputFormat");
			String strReportType = request.getParameter("reportType");
			String strAction = request.getParameter("action");
			
			List result = getEditReport(request, locale);
				
			String templateName = null;
			if(strReportFormat.equals("PDF")){
				templateName = "template_editor_report_pdf";
			}else if(strReportFormat.equals("WORD")){
				templateName = "template_editor_report_word";
			}
			File reportFile = null;				
			
			reportFile = generateReportUsingFOP(new Object[] { result, strAction, strReportType },templateName, strReportFormat, "editor report",locale.toString());
			openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	@RequestMapping(value="/vishaysuchi",method=RequestMethod.GET)
	public String getVishaysuchi(HttpServletRequest request, ModelMap model, Locale locale){
		String retVal = "editing/error";
		try{
			model.addAttribute("report", prepareVishaysuchi(request, model, locale));
			
			retVal = "editing/vishaysuchi";
			
		}catch (Exception e) {
			logger.debug("editing/vishaysuchi", e);
			e.printStackTrace();
			model.addAttribute("errorcode", "UNABLE_TO_GENERATE_VISHAYSUCHI");
		}
		return retVal;
	}	
	
	@RequestMapping(value="/vishaysuchireport",method=RequestMethod.GET)
	public void getVishaysuchiReport(HttpServletRequest request, HttpServletResponse response, Locale locale){
		ModelMap model = new ModelMap();
		try{
			List<VishaysuchiVO> vishaysuchiVOs = prepareVishaysuchi(request, model, locale);
			String strReportFormat = request.getParameter("outputFormat");
			VishaysuchiXMLVO report = new VishaysuchiXMLVO();
			report.setVishaysuchi(vishaysuchiVOs);
			File reportFile = generateReportUsingFOP(report, "template_edis_vishaysuchi", strReportFormat, "vishaySuchi", locale.toString());
			
			openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
			
		}catch (Exception e) {
			logger.debug("editing/vishaysuchireport", e);
			e.printStackTrace();
		}
	}

	
	@RequestMapping(value="/anukramanika", method=RequestMethod.GET)
	public String viewAnukramanika(ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale){
		try{
			
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strLanguage = request.getParameter("language");
			String strDay = request.getParameter("day");
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			String strFromDay = request.getParameter("fromDay");
			String strToDay = request.getParameter("toDay");
			
			String[] decodedValues = EditingControllerUtility.getDecodedString(new String[]{strHouseType, strSessionType, strSessionYear, strLanguage, strDay, strUserGroup, strUserGroupType});
			strHouseType = decodedValues[0];
			strSessionType = decodedValues[1];
			strSessionYear = decodedValues[2];
			strLanguage = decodedValues[3];
			strDay = decodedValues[4];
			strUserGroup = decodedValues[5];
			strUserGroupType = decodedValues[6];
			
			/****Prepare vishaysuchi ****/
			if(strHouseType!=null&&!strHouseType.isEmpty()&&
					strSessionType!=null&&!strSessionType.isEmpty()&&
					strSessionYear!=null&&!strSessionYear.isEmpty()&&
					strLanguage!=null&&!strLanguage.isEmpty()&& 
					((strDay!=null&&!strDay.isEmpty())|| 
							(strFromDay!=null && !strFromDay.isEmpty() && strToDay!=null && !strToDay.isEmpty()))){

				HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
				SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
				Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				Roster firstRoster = Roster.findRosterBySessionLanguageAndDay(session, 1, language, locale.toString());
				Roster roster = null;
				Date fromDayDate = null;
				Date toDayDate = null;
				Long[] rosterIds = null;
				
				if(strFromDay!=null && !strFromDay.isEmpty() && strToDay!=null && !strToDay.isEmpty()){
					int fromDay = Integer.parseInt(strFromDay);
					int toDay = Integer.parseInt(strToDay);
					
					Date date = null;
					if(firstRoster != null){
						date = firstRoster.getStartTime();
					}else{
						date = session.getStartDate();
					}
					Calendar calendarFrom = Calendar.getInstance();
					Calendar calendarTo = Calendar.getInstance();
					
					calendarFrom.setTime(date);
					calendarTo.setTime(date);
					
					if(fromDay > 0 && toDay > 0){
						calendarFrom.add(Calendar.DAY_OF_MONTH, fromDay - 1);
						calendarTo.add(Calendar.DAY_OF_MONTH, toDay - 1);
					}else{
						calendarFrom.add(Calendar.DAY_OF_MONTH, fromDay);
						calendarTo.add(Calendar.DAY_OF_MONTH, toDay);
					}
					fromDayDate = calendarFrom.getTime();
					toDayDate = calendarTo.getTime();
					
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("sessionID", new String[]{session.getId().toString()});
					parameters.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromDayDate, ApplicationConstants.DB_DATEFORMAT)});				
					parameters.put("toDate", new String[]{FormaterUtil.formatDateToString(toDayDate, ApplicationConstants.DB_DATEFORMAT)});
					parameters.put("language", new String[]{language.getId().toString()});
					List rosters = Query.findReport("EDIS_ALL_ROSTER_BETWEEN_DATE", parameters);
					if(rosters != null && !rosters.isEmpty()){
						rosterIds = new Long[rosters.size()];
						for(int i = 0; i < rosters.size(); i++){
							Object[] obj = (Object[])rosters.get(i);
							rosterIds[i] = new Long(obj[0].toString());						
						}
					}
				}
				
				Map<String, String[]> params = new HashMap<String, String[]>();
				params.put("locale", new String[]{locale.toString()});
				
				if(rosterIds == null){
					
					model.addAttribute("firstDayData", "yes");
					
					roster = Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
					
					Calendar fromToDate = Calendar.getInstance();
					if(firstRoster != null){
						fromToDate.setTime(firstRoster.getStartTime());
					}else{
						fromToDate.setTime(session.getStartDate());
					}
					
					fromToDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(strDay) - 1);
					params.put("sessionID", new String[]{session.getId().toString()});
					params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});				
					params.put("toDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});
					params.put("language", new String[]{language.getId().toString()});
				}else{				
					params.put("language", new String[]{language.getId().toString()});
					params.put("sessionID", new String[]{session.getId().toString()});
					params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromDayDate, ApplicationConstants.DB_DATEFORMAT)});				
					params.put("toDate", new String[]{FormaterUtil.formatDateToString(toDayDate, ApplicationConstants.DB_DATEFORMAT)});
				}
				
				List anukramanikaData = Query.findReport("EDIS_ANUKRAMANIKA", params);
				model.addAttribute("report", anukramanikaData);				
			}
			
		}catch(Exception e){
			
			logger.debug("editing/anukramanika", e);
			e.printStackTrace();
		}
		return "editing/anukramanika";
	}
	
	@RequestMapping(value="/anukramanikareport",method=RequestMethod.GET)
	public void getAnukramanikaReport(HttpServletRequest request, HttpServletResponse response, Locale locale){
		ModelMap model = new ModelMap();
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strLanguage = request.getParameter("language");
			String strDay = request.getParameter("day");
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			String strFromDay = request.getParameter("fromDay");
			String strToDay = request.getParameter("toDay");
			
			String[] decodedValues = EditingControllerUtility.getDecodedString(new String[]{strHouseType, strSessionType, strSessionYear, strLanguage, strDay, strUserGroup, strUserGroupType});
			strHouseType = decodedValues[0];
			strSessionType = decodedValues[1];
			strSessionYear = decodedValues[2];
			strLanguage = decodedValues[3];
			strDay = decodedValues[4];
			strUserGroup = decodedValues[5];
			strUserGroupType = decodedValues[6];
			
			/****Prepare vishaysuchi ****/
			if(strHouseType!=null&&!strHouseType.isEmpty()&&
					strSessionType!=null&&!strSessionType.isEmpty()&&
					strSessionYear!=null&&!strSessionYear.isEmpty()&&
					strLanguage!=null&&!strLanguage.isEmpty()&& 
					((strDay!=null&&!strDay.isEmpty())|| 
							(strFromDay!=null && !strFromDay.isEmpty() && strToDay!=null && !strToDay.isEmpty()))){

				HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
				SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
				Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				Roster firstRoster = Roster.findRosterBySessionLanguageAndDay(session, 1, language, locale.toString());
				Roster roster = null;
				Date fromDayDate = null;
				Date toDayDate = null;
				Long[] rosterIds = null;
				
				if(strFromDay!=null && !strFromDay.isEmpty() && strToDay!=null && !strToDay.isEmpty()){
					int fromDay = Integer.parseInt(strFromDay);
					int toDay = Integer.parseInt(strToDay);
					
					Date date = null;
					if(firstRoster != null){
						date = firstRoster.getStartTime();
					}else{
						date = session.getStartDate();
					}
					Calendar calendarFrom = Calendar.getInstance();
					Calendar calendarTo = Calendar.getInstance();
					
					calendarFrom.setTime(date);
					calendarTo.setTime(date);
					
					if(fromDay > 0 && toDay > 0){
						calendarFrom.add(Calendar.DAY_OF_MONTH, fromDay - 1);
						calendarTo.add(Calendar.DAY_OF_MONTH, toDay - 1);
					}else{
						calendarFrom.add(Calendar.DAY_OF_MONTH, fromDay);
						calendarTo.add(Calendar.DAY_OF_MONTH, toDay);
					}
					fromDayDate = calendarFrom.getTime();
					toDayDate = calendarTo.getTime();
					
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("sessionID", new String[]{session.getId().toString()});
					parameters.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromDayDate, ApplicationConstants.DB_DATEFORMAT)});				
					parameters.put("toDate", new String[]{FormaterUtil.formatDateToString(toDayDate, ApplicationConstants.DB_DATEFORMAT)});
					parameters.put("language", new String[]{language.getId().toString()});
					List rosters = Query.findReport("EDIS_ALL_ROSTER_BETWEEN_DATE", parameters);
					if(rosters != null && !rosters.isEmpty()){
						rosterIds = new Long[rosters.size()];
						for(int i = 0; i < rosters.size(); i++){
							Object[] obj = (Object[])rosters.get(i);
							rosterIds[i] = new Long(obj[0].toString());						
						}
					}
				}
				
				Map<String, String[]> params = new HashMap<String, String[]>();
				params.put("locale", new String[]{locale.toString()});
				
				if(rosterIds == null){
					
					model.addAttribute("firstDayData", "yes");
					
					roster = Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
					
					Calendar fromToDate = Calendar.getInstance();
					if(firstRoster != null){
						fromToDate.setTime(firstRoster.getStartTime());
					}else{
						fromToDate.setTime(session.getStartDate());
					}
					
					fromToDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(strDay) - 1);
					params.put("sessionID", new String[]{session.getId().toString()});
					params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});				
					params.put("toDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});
					params.put("language", new String[]{language.getId().toString()});
				}else{				
					params.put("language", new String[]{language.getId().toString()});
					params.put("sessionID", new String[]{session.getId().toString()});
					params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromDayDate, ApplicationConstants.DB_DATEFORMAT)});				
					params.put("toDate", new String[]{FormaterUtil.formatDateToString(toDayDate, ApplicationConstants.DB_DATEFORMAT)});
				}
				
				List anukramanikaData = Query.findReport("EDIS_ANUKRAMANIKA", params);
			
				
				String strReportFormat = request.getParameter("outputFormat");
				File reportFile = generateReportUsingFOP(new Object[]{anukramanikaData}, "template_edis_anukramanika", strReportFormat, "anukramanika", locale.toString());
				
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
			}
			
		}catch (Exception e) {
			logger.debug("editing/vishaysuchireport", e);
			e.printStackTrace();
		}
	}
	
	@Transactional
	@SuppressWarnings("rawtypes")
	private List<VishaysuchiVO> prepareVishaysuchi(HttpServletRequest request, ModelMap model,
			Locale locale) throws NumberFormatException, ELSException{
		
		List<VishaysuchiVO> copyVishaysuchi = null;
		
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strLanguage = request.getParameter("language");
		String strDay = request.getParameter("day");
		String strUserGroup = request.getParameter("userGroup");
		String strUserGroupType = request.getParameter("userGroupType");
		String strFromDay = request.getParameter("fromDay");
		String strToDay = request.getParameter("toDay");
		
		String[] decodedValues = EditingControllerUtility.getDecodedString(new String[]{strHouseType, strSessionType, strSessionYear, strLanguage, strDay, strUserGroup, strUserGroupType});
		strHouseType = decodedValues[0];
		strSessionType = decodedValues[1];
		strSessionYear = decodedValues[2];
		strLanguage = decodedValues[3];
		strDay = decodedValues[4];
		strUserGroup = decodedValues[5];
		strUserGroupType = decodedValues[6];
		
		/****Prepare vishaysuchi ****/
		if(strHouseType!=null&&!strHouseType.isEmpty()&&
				strSessionType!=null&&!strSessionType.isEmpty()&&
				strSessionYear!=null&&!strSessionYear.isEmpty()&&
				strLanguage!=null&&!strLanguage.isEmpty()&& 
				((strDay!=null&&!strDay.isEmpty())|| 
						(strFromDay!=null && !strFromDay.isEmpty() && strToDay!=null && !strToDay.isEmpty()))){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			Roster firstRoster = Roster.findRosterBySessionLanguageAndDay(session, 1, language, locale.toString());
			Roster roster = null;
			Date fromDayDate = null;
			Date toDayDate = null;
			Long[] rosterIds = null;
			
			if(strFromDay!=null && !strFromDay.isEmpty() && strToDay!=null && !strToDay.isEmpty()){
				int fromDay = Integer.parseInt(strFromDay);
				int toDay = Integer.parseInt(strToDay);
				
				Date date = null;
				if(firstRoster != null){
					date = firstRoster.getStartTime();
				}else{
					date = session.getStartDate();
				}
				Calendar calendarFrom = Calendar.getInstance();
				Calendar calendarTo = Calendar.getInstance();
				
				calendarFrom.setTime(date);
				calendarTo.setTime(date);
				
				if(fromDay > 0 && toDay > 0){
					calendarFrom.add(Calendar.DAY_OF_MONTH, fromDay - 1);
					calendarTo.add(Calendar.DAY_OF_MONTH, toDay - 1);
				}else{
					calendarFrom.add(Calendar.DAY_OF_MONTH, fromDay);
					calendarTo.add(Calendar.DAY_OF_MONTH, toDay);
				}
				fromDayDate = calendarFrom.getTime();
				toDayDate = calendarTo.getTime();
				
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("sessionID", new String[]{session.getId().toString()});
				parameters.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromDayDate, ApplicationConstants.DB_DATEFORMAT)});				
				parameters.put("toDate", new String[]{FormaterUtil.formatDateToString(toDayDate, ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("language", new String[]{language.getId().toString()});
				List rosters = Query.findReport("EDIS_ALL_ROSTER_BETWEEN_DATE", parameters);
				if(rosters != null && !rosters.isEmpty()){
					rosterIds = new Long[rosters.size()];
					for(int i = 0; i < rosters.size(); i++){
						Object[] obj = (Object[])rosters.get(i);
						rosterIds[i] = new Long(obj[0].toString());						
					}
				}
			}
			
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("locale", new String[]{locale.toString()});
			List catchWords = Query.findReport("EDIS_DISTINCT_CATCHWORDS", params);
			List members =  null;
			
			if(rosterIds == null){
				
				model.addAttribute("firstDayData", "yes");
				
				roster = Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
				
				Calendar fromToDate = Calendar.getInstance();
				if(firstRoster != null){
					fromToDate.setTime(firstRoster.getStartTime());
				}else{
					fromToDate.setTime(session.getStartDate());
				}
				
				fromToDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(strDay) - 1);
				params.put("sessionID", new String[]{session.getId().toString()});
				params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});				
				params.put("toDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});
				params.put("language", new String[]{language.getId().toString()});
			}else{				
				params.put("language", new String[]{language.getId().toString()});
				params.put("sessionID", new String[]{session.getId().toString()});
				params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromDayDate, ApplicationConstants.DB_DATEFORMAT)});				
				params.put("toDate", new String[]{FormaterUtil.formatDateToString(toDayDate, ApplicationConstants.DB_DATEFORMAT)});
			}
			members = Query.findReport("EDIS_DISTINCT_MEMBERS_OF_ROSTER", params);
			List<VishaysuchiVO> vishaysuchi = new ArrayList<VishaysuchiVO>();
			if(catchWords != null && !catchWords.isEmpty()){
				for(Object o : catchWords){
					Object[] dArr = (Object[])o;
					
					VishaysuchiVO vishaysuchiVO = new VishaysuchiVO();
					vishaysuchiVO.setType("catchWord");
					vishaysuchiVO.setValue(dArr[1].toString());
					vishaysuchi.add(vishaysuchiVO);
				}
			}

			if(members != null && !members.isEmpty()){
				for(Object o : members){
					Object[] dArr = (Object[])o;
					VishaysuchiVO vishaysuchiVO = new VishaysuchiVO();
					vishaysuchiVO.setType("member");
					vishaysuchiVO.setValue(dArr[1].toString());
					vishaysuchiVO.setMemberID(dArr[0].toString());
					vishaysuchi.add(vishaysuchiVO);
				}
			}
			
			Collections.sort(vishaysuchi, new Comparator<VishaysuchiVO>() {
				@Override
				public int compare(VishaysuchiVO val1, VishaysuchiVO val2){
					return (val1.getValue().compareTo(val2.getValue()));
				}
			});
							
			boolean flag = false;
			if(rosterIds == null){
				flag = EditingControllerUtility.canVishaysuchiBePrepared(session, houseType, new Long[]{roster.getId()}, "editing_", locale.toString());
			}else{
				flag = EditingControllerUtility.canVishaysuchiBePrepared(session, houseType, rosterIds, "editing_", locale.toString());
			}
			
			if(flag){
				copyVishaysuchi = new ArrayList<VishaysuchiVO>();
				for(VishaysuchiVO vvo : vishaysuchi){					
					copyVishaysuchi.add(vvo);
				}		
				
				if(vishaysuchi != null && !vishaysuchi.isEmpty()){
					int i = 0;
					for(VishaysuchiVO vo : vishaysuchi){
						List reportData = null;
						if(vo.getType().equals("catchWord")){
							if(rosterIds == null){
								reportData = Part.findVishaySuchiListWithoutMembers(vo.getValue(), new Long[]{roster.getId()}, locale.toString());
							}else{
								reportData = Part.findVishaySuchiListWithoutMembers(vo.getValue(), rosterIds, locale.toString());
							}
							
							if(reportData != null && !reportData.isEmpty()){
								List<CatchwordHeadingVO> headings = new ArrayList<CatchwordHeadingVO>();
								for(Object o : reportData){
									Object[] oArr = (Object[])o;
									
									CatchwordHeadingVO cwHVO = new CatchwordHeadingVO();
									if(oArr[5] != null){
										copyVishaysuchi.get(i).setDeviceName(oArr[5].toString());
										cwHVO.setDeviceName(oArr[5].toString());
									}else{
										copyVishaysuchi.get(i).setDeviceName("");
										cwHVO.setDeviceName("");
									}
									
									if(oArr[6] != null){
										copyVishaysuchi.get(i).setDeviceType(oArr[6].toString());
										cwHVO.setDeviceType(oArr[6].toString());
									}else{
										copyVishaysuchi.get(i).setDeviceType("");
										cwHVO.setDeviceType("");
									}
									
									if(oArr[1] != null){
										cwHVO.setCatchWord(oArr[1].toString());
									}
									if(oArr[9] != null){
										cwHVO.setHeading(oArr[9].toString());
									}
									headings.add(cwHVO);
								}
								copyVishaysuchi.get(i).setHeadings(headings);
							}
						}else if(vo.getType().equals("member")){
							
							params.put("locale", new String[]{locale.toString()});
							if(rosterIds == null){
								//params.put("rosterId", new String[]{roster.getId().toString()});
								Calendar fromToDate = Calendar.getInstance();
								if(firstRoster != null){
									fromToDate.setTime(firstRoster.getStartTime());
								}else{
									fromToDate.setTime(session.getStartDate());
								}
								fromToDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(strDay) - 1);
								params.put("sessionID", new String[]{session.getId().toString()});
								params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});				
								params.put("toDate", new String[]{FormaterUtil.formatDateToString(fromToDate.getTime(), ApplicationConstants.DB_DATEFORMAT)});
								params.put("language", new String[]{language.getId().toString()});
							}else{
								params.put("sessionID", new String[]{session.getId().toString()});
								params.put("fromDate", new String[]{FormaterUtil.formatDateToString(fromDayDate, ApplicationConstants.DB_DATEFORMAT)});				
								params.put("toDate", new String[]{FormaterUtil.formatDateToString(toDayDate, ApplicationConstants.DB_DATEFORMAT)});
								params.put("language", new String[]{language.getId().toString()});
							}
							params.put("memberId", new String[]{vo.getMemberID()});
							reportData = Query.findReport("EDIS_CATCHWORD_AND_HEADINGS_OF_MEMBER_BY_DEVICES", params);
							
							if(reportData != null && !reportData.isEmpty()){
								List<VishaysuchiDeviceVO> vishaysuchiDeviceVos = new ArrayList<VishaysuchiDeviceVO>();
								for(Object o : reportData){
									Object[] objArr = (Object[]) o;
									VishaysuchiDeviceVO vdv = new VishaysuchiDeviceVO();
									if(objArr[0] != null){
										CatchwordHeadingVO catchVO = new CatchwordHeadingVO();
										catchVO.setCatchWord(objArr[0].toString());
										if(objArr[2] != null){
											catchVO.setHeading(objArr[2].toString());
										}
										vdv.setCatchwordHeading(catchVO);
									}
									if(objArr[4] != null){
										vdv.setDeviceName(objArr[4].toString());
									}
									if(objArr[5] != null){
										vdv.setDeviceType(objArr[5].toString());
									}
									if(objArr[6] != null){
										vdv.setPartID(objArr[6].toString());
									}
									vishaysuchiDeviceVos.add(vdv);
								}
								
								copyVishaysuchi.get(i).setVishaysuchiDevices(vishaysuchiDeviceVos);
							}
						}
						 
						/*if((vo.getHeadings() != null && !vo.getHeadings().isEmpty())
								|| (vo.getVishaysuchiDevices() != null && !vo.getVishaysuchiDevices().isEmpty())){*/
							copyVishaysuchi.get(i).setCatchWordIndex(FormaterUtil.findIndexLetterByWord(vo.getValue(), locale.toString()));
						//}
						i++;
					}
				}
			}
		}
		
		/*if(copyVishaysuchi != null && !copyVishaysuchi.isEmpty()){
			
			for(int i = 0; i < copyVishaysuchi.size(); ){
				if(copyVishaysuchi.get(i).getHeadings() != null){
					if(i > 0){
						if(copyVishaysuchi.get(i - 1).getHeadings() == null || copyVishaysuchi.get(i).getVishaysuchiDevices() == null){
							copyVishaysuchi.get(i - 1).setCatchWordIndex("-");
						}
					}
				}
				
				if(copyVishaysuchi.get(i).getVishaysuchiDevices() != null){
					if(i > 0){
						if(copyVishaysuchi.get(i).getVishaysuchiDevices() == null){
							copyVishaysuchi.get(i - 1).setCatchWordIndex("-");
						}
					}
				}
				
				i++;
			}
		}*/
		List<VishaysuchiVO> finalVishaysuchi = new ArrayList<VishaysuchiVO>();
		if(copyVishaysuchi != null && !copyVishaysuchi.isEmpty()){
			for(VishaysuchiVO vo : copyVishaysuchi){
				if(vo.getHeadings() != null || vo.getVishaysuchiDevices() != null){
					finalVishaysuchi.add(vo);
				}
			}	
		}
		return finalVishaysuchi;
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private String getCatchWordOfPart(Map<String, List> catchWords, Long partId){
		String word = null;
		for(Entry<String, List> entry : catchWords.entrySet()){
			if(entry.getValue().contains(partId)){
				word = entry.getKey();
				break;
			}
		}
		return word;
	}
	
	
	@RequestMapping(value="/gememberimage/{id}", method=RequestMethod.GET)
	public void getImage(@PathVariable(value="id") Long memberId , HttpServletRequest request, HttpServletResponse response, Locale locale){
		Member member = Member.findById(Member.class, memberId);
		Document doc=null;
		try {
			doc = Document.findByTag(member.getPhoto());
		} catch (ELSException e1) {		
			e1.printStackTrace();
		}
		
		response.addHeader("Cache-Control", "cache, must-revalidate"); 
		response.setHeader("Pragma", "public");
		response.setContentType("image/jpeg");
		response.setContentLength(doc.getFileData().length);
		
		try {
			FileCopyUtils.copy(doc.getFileData(), response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	@RequestMapping(value="/replace",method=RequestMethod.POST)
	public @ResponseBody List doReplace(HttpServletRequest request, HttpServletResponse response, ModelMap model, Locale locale){
		List matchedParts = null;
		
		try{			
						
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strLanguage = request.getParameter("language");
			String strDay = request.getParameter("day");
			
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			
			String strSearchTerm = request.getParameter("searchTerm");
			String strReplaceTerm = request.getParameter("replaceTerm");
			
			String strUndoCount = request.getParameter("undoCount");
			String strRedoCount = request.getParameter("redoCount");
			Integer undoCount = Integer.valueOf(strUndoCount);
			Integer redoCount = Integer.valueOf(strRedoCount);
			
			String uniqueIdentifierForUndo = UUID.randomUUID().toString();
			String uniqueIdentifierForRedo = UUID.randomUUID().toString();
					
			if (strHouseType != null && !strHouseType.equals("")
					&& strSessionType != null && !strSessionType.equals("")
					&& strSessionYear != null && !strSessionYear.equals("")
					&& strLanguage != null && !strLanguage.equals("")
					&& strDay != null && !strDay.equals("")) {
	
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType,locale.toString());
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Language language = Language.findById(Language.class,Long.parseLong(strLanguage));
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, Integer.parseInt(strSessionYear));
				Roster roster = Roster.findRosterBySessionLanguageAndDay(session, Integer.parseInt(strDay), language,locale.toString());
				UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
				UserGroupType userGroupType = userGroup.getUserGroupType();
				matchedParts = Part.findAllEligibleForReplacement(roster, strSearchTerm, strReplaceTerm, locale.toString());
				
				if(matchedParts != null && !matchedParts.isEmpty()){
					for(int i = 0; i < matchedParts.size(); i++){
						
						Object[] objArr = (Object[])matchedParts.get(i);
						if(!objArr[3].toString().equals(objArr[4].toString())){
							Part partToBeReplaced = Part.findById(Part.class, Long.valueOf(objArr[0].toString()));
							/****Create draft****/
							PartDraft pd = new PartDraft();
							pd.setEditedBy(this.getCurrentUser().getActualUsername());
							pd.setEditedAs(userGroupType.getName());
							pd.setEditedOn(new Date());
							pd.setLocale(locale.toString());
							pd.setMainHeading(partToBeReplaced.getMainHeading());
							pd.setPageHeading(partToBeReplaced.getPageHeading());
							pd.setOriginalText(objArr[3].toString());
							pd.setReplacedText(objArr[4].toString());
							pd.setRevisedContent(objArr[4].toString());
							pd.setUndoCount(undoCount);
							pd.setRedoCount(redoCount);
							/*pd.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
							pd.setUniqueIdentifierForRedo(UUID.randomUUID().toString());*/
							pd.setUniqueIdentifierForUndo(uniqueIdentifierForUndo);
							pd.setUniqueIdentifierForRedo(uniqueIdentifierForRedo);
							pd.setWorkflowCopy(false);
							
							/****Attach undoCount and undoUID in the result list****/
							/*((Object[])matchedParts.get(i))[5] = partToBeReplaced.getId().toString()+":"+pd.getUndoCount()+":"+pd.getUniqueIdentifierForUndo();
							((Object[])matchedParts.get(i))[6] = partToBeReplaced.getId().toString()+":"+pd.getRedoCount()+":"+pd.getUniqueIdentifierForRedo();*/
							((Object[])matchedParts.get(i))[5] = pd.getUndoCount()+":"+pd.getUniqueIdentifierForUndo();
							((Object[])matchedParts.get(i))[6] = pd.getRedoCount()+":"+pd.getUniqueIdentifierForRedo();
							((Object[])matchedParts.get(i))[7] = "include";
							
							partToBeReplaced.getPartDrafts().add(pd);
							partToBeReplaced.setEditedContent(objArr[4].toString());
							partToBeReplaced.merge();
						}else{
							((Object[])matchedParts.get(i))[7] = "exclude";
						}
					}
				}
			}		
						
		}catch (Exception e) {
			e.printStackTrace();
		}
		return matchedParts;
	}
	
	@Transactional
	@RequestMapping(value="/undolastchange/{partid}",method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> doUndo(@PathVariable(value="partid") Long id, HttpServletRequest request, HttpServletResponse response, Locale locale){
		MasterVO masterVO = null;
		try{
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			String strUniqueUndoId = request.getParameter("uniqueIdentifierForUndo");
			String strUndoCount = request.getParameter("undoCount");
			
			if(strUniqueUndoId != null && !strUniqueUndoId.isEmpty()
					&& strUndoCount != null && !strUndoCount.isEmpty()){
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("uniqueIdentifierForUndo", new String[]{strUniqueUndoId});
				parameters.put("undoCount", new String[]{strUndoCount});
				parameters.put("editedOn", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("editedBy", new String[]{this.getCurrentUser().getActualUsername()});
				List pds = Query.findReport("EDIS_FIND_PART_DRAFTS", parameters);
				List<MasterVO> undoData = new ArrayList<MasterVO>();
				
				if(pds != null){
					for(Object o : pds){
						Object[] pd = (Object[])o;
						
						masterVO = new MasterVO();
						if(pd[1] != null){
							masterVO.setValue(pd[1].toString());
						}						
							
						Long pId = null;
						if(pd[3] != null){							
							pId = Long.valueOf(pd[3].toString());
						}
												
						masterVO.setId(pId);
						
						Part pp = Part.findById(Part.class, pId);
						pp.setEditedContent(masterVO.getValue());
						pp.merge();
						
						undoData.add(masterVO);
					}
				}
				
				return undoData;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Transactional
	@RequestMapping(value="/redolastchange/{partid}",method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> doRedo(@PathVariable(value="partid") Long id, HttpServletRequest request, HttpServletResponse response, Locale locale){
		MasterVO masterVO = null;
		try{
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			String strUrData = request.getParameter("urData");
			List<MasterVO> redoData = new ArrayList<MasterVO>();
			
			if(strUrData != null && !strUrData.isEmpty()){
				String[] data = strUrData.split(":"); 
				
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("uniqueIdentifierForUndo", new String[]{data[1]});
				parameters.put("undoCount", new String[]{data[0]});
				parameters.put("editedOn", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("editedBy", new String[]{this.getCurrentUser().getActualUsername()});
				List pds = Query.findReport("EDIS_FIND_PART_DRAFTS", parameters);
				
				if(pds != null){
					for(Object o : pds){
						Object[] pd = (Object[])o;
						
						masterVO = new MasterVO();
						masterVO.setId(Long.valueOf(pd[3].toString()));
						masterVO.setValue(pd[2].toString());
						
						redoData.add(masterVO);
						
						Part pp = Part.findById(Part.class, Long.valueOf(pd[3].toString()));
						pp.setEditedContent(masterVO.getValue());
						pp.merge();
					}
				}
				
				return redoData;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	private List getEditReport(HttpServletRequest request, Locale locale) throws NumberFormatException, ELSException{
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strLanguage = request.getParameter("language");
		String strDay = request.getParameter("day");
		String strReportType = request.getParameter("reportType");
		String strAction = request.getParameter("action");
		String strReedit = request.getParameter("reedit");
		String strMember = request.getParameter("member");
		String strMemberReportType = request.getParameter("memberReportType");
		String strEncodedPageHeader = request.getParameter("pageheader");
		String strPageHeader = "";
		List result = null;
		
		CustomParameter csptDeploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		
		if((strReportType != null && !strReportType.isEmpty())
				&& (strMember != null && !strMember.isEmpty())
				&& (strMemberReportType != null && !strMemberReportType.isEmpty())
				&& (strEncodedPageHeader != null && !strEncodedPageHeader.isEmpty())){
			
			if(strReportType.equals("-")){
				strReportType = "other";
			}else{
				if(strReportType.equals("member")){
					if(strMember.equals("-")){
						strReportType = "nothing";
					}else{
						if(strMemberReportType.equals("-")){
							strReportType = "nothing";
						}else{
							if(strMemberReportType.equals("member")){
								strReportType = "memberS";
							}else if(strMemberReportType.equals("asmember")){
								strReportType = "member";
							}else if(strMemberReportType.equals("pageheading")){
								strReportType = "memberPageHeading";
								if(csptDeploymentServer != null){
									if(csptDeploymentServer.getValue() != null && !csptDeploymentServer.getValue().isEmpty()){
										if(csptDeploymentServer.getValue().equals("TOMCAT")){
											try {
												strPageHeader = new String(strEncodedPageHeader.getBytes("ISO-8859-1"), "UTF-8");
											} catch (UnsupportedEncodingException e) {
												e.printStackTrace();
											}											
										}
									}
								}
							}
						}
					}
				}else if(strReportType.equals("pageheading")){
					if(strPageHeader.equals("-")){
						strReportType = "nothing";
					}else{
						strReportType = "pageHeading";
						
						if(csptDeploymentServer != null){
							if(csptDeploymentServer.getValue() != null && !csptDeploymentServer.getValue().isEmpty()){
								if(csptDeploymentServer.getValue().equals("TOMCAT")){
									try {
										strPageHeader = new String(strEncodedPageHeader.getBytes("ISO-8859-1"), "UTF-8");
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
		if(!strReportType.isEmpty() && !strReportType.equals("nothing")){
			if (strHouseType != null && !strHouseType.equals("")
					&& strSessionType != null && !strSessionType.equals("")
					&& strSessionYear != null && !strSessionYear.equals("")
					&& strLanguage != null && !strLanguage.equals("")
					&& strDay != null && !strDay.equals("")) {
	
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType,locale.toString());
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Language language = Language.findById(Language.class,Long.parseLong(strLanguage));
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, Integer.parseInt(strSessionYear));
				
				Roster roster = Roster.findRosterBySessionLanguageAndDay(session, Integer.parseInt(strDay), language,locale.toString());
	
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.put("locale", new String[]{locale.toString()});
				parametersMap.put("languageId", new String[]{language.getId().toString()});
				parametersMap.put("rosterId", new String[]{roster.getId().toString()});
				
				/*if(strReportType == null){
					result = Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);
				}else */
				if(strReportType.equals("other")){
					result = Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);
				}else if(strReportType.equals("member")){
					if(strMember != null && !strMember.isEmpty()){
						parametersMap.put("memberId", new String[]{strMember});
						result=Query.findReport("EDIS_PROCEEDING_MEMBER_REPORT", parametersMap);
					}
				}else if(strReportType.equals("device")){
					//TODO: find parts where the device matches
				}else if(strReportType.equals("memberS")){
					if(strMember != null && !strMember.isEmpty()){
						parametersMap.put("memberId", new String[]{strMember});
						result=Query.findReport("EDIS_PROCEEDING_MEMBERS_REPORT", parametersMap);
					}
					//TODO: find parts where current member has spoken
				}else if(strReportType.equals("pageHeading")){
					//TODO: find parts where given heading matches
					parametersMap.put("partId", new String[]{strPageHeader});
					result=Query.findReport("EDIS_PROCEEDING_PAGEHEADING_REPORT", parametersMap);
				}else if(strReportType.equals("memberPageHeading")){
					//TODO: find the parts where given member has spoken on the given page heading
					parametersMap.put("partId", new String[]{strPageHeader});
					parametersMap.put("memberId", new String[]{strMember});
					result=Query.findReport("EDIS_PROCEEDING_MEMBER_PAGEHEADING_REPORT", parametersMap);
				}
			}
		}
		
		return result;
	}
	
	
	@Transactional
	@RequestMapping(value="/startworkflow",method=RequestMethod.POST)
	public @ResponseBody String startWorkFlow(HttpServletRequest request, ModelMap model, HttpServletResponse response, Locale locale){
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strLanguage = request.getParameter("language");
		String strDay = request.getParameter("day");
		String strUserGroup = request.getParameter("userGroup");
		String strUserGroupType = request.getParameter("userGroupType");
		String strWfFor = request.getParameter("wffor");
		String strLevel = request.getParameter("level");
		String strDevices = request.getParameter("devices");
		String retVal = "fail";
		try{
			if (strHouseType != null && !strHouseType.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()
					&& strLanguage != null && !strLanguage.isEmpty()
					&& strDay != null && !strDay.isEmpty()) {
	
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType,locale.toString());
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Language language = Language.findById(Language.class,Long.parseLong(strLanguage));
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, Integer.parseInt(strSessionYear));
				
				Roster roster = Roster.findRosterBySessionLanguageAndDay(session, Integer.parseInt(strDay), language,locale.toString());
				UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
				
				Map<String, Object[]> fields = new HashMap<String, Object[]>();
				fields.put("deviceId", new Object[]{roster.getId()});
				
				WorkflowDetails wfIfAny = null;
				
				if(strWfFor.equals(ApplicationConstants.MEMBER)){
					fields.put("workflowSubType", new Object[]{ApplicationConstants.EDITING_RECOMMEND_MEMBERAPPROVAL, ApplicationConstants.EDITING_FINAL_MEMBERAPPROVAL});
					 
				}else if(strWfFor.equals(ApplicationConstants.SPEAKER)){
					fields.put("workflowSubType", new Object[]{ApplicationConstants.EDITING_RECOMMEND_SPEAKERAPPROVAL, ApplicationConstants.EDITING_FINAL_SPEAKERAPPROVAL});
				}
								
				fields.put("sessionType", new Object[]{session.getType().getSessionType()});
				fields.put("sessionYear", new Object[]{FormaterUtil.formatNumberNoGrouping(Integer.valueOf(strSessionYear), locale.toString())});
				fields.put("houseType", new Object[]{session.getHouse().getType().getName()});
				fields.put("status", new Object[]{ApplicationConstants.MYTASK_PENDING, ApplicationConstants.MYTASK_COMPLETED});				
				wfIfAny = WorkflowDetails.find(fields, locale.toString());
				
				
				if (wfIfAny == null) {
					
					List<Part> parts = Part.findAllPartOfProceedingOfRoster(roster, true, locale.toString());
					for (Part p : parts) {
						boolean flag = false;
						
						if(strDevices != null){
							if(p.getDeviceType() != null){
								flag = isDeviceEnabled(p.getDeviceType().getId().toString(), strDevices);
							}
						}else{
							flag = true;
						}
						
						if(flag){
							List<PartDraft> pds = p.getPartDrafts();
							if (pds != null && !pds.isEmpty()) {
								PartDraft pd = pds.get(pds.size() - 1);
								if(pd.getEditedBy().equals(this.getCurrentUser().getActualUsername())){
									if (strWfFor.equals(ApplicationConstants.MEMBER)) {
										pd.setMemberSentCopy(true);
									} else if (strWfFor.equals(ApplicationConstants.SPEAKER)) {
										pd.setSpeakerSentCopy(true);
									}
									pd.merge();
								}else{
									pd = new PartDraft();
									
									pd.setEditedBy(this.getCurrentUser().getActualUsername());
									pd.setEditedOn(new Date());
									pd.setEditedAs(userGroup.getUserGroupType().getName());
									pd.setLocale(locale.toString());
									pd.setMainHeading(p.getMainHeading());
									pd.setPageHeading(p.getPageHeading());
									pd.setWorkflowCopy(true);
									
									if(p.getEditedContent() != null && !p.getEditedContent().isEmpty()){
										pd.setRevisedContent(p.getEditedContent());
									}else if(p.getRevisedContent() != null && !p.getRevisedContent().isEmpty()){
										pd.setRevisedContent(p.getRevisedContent());
									}
									
									if (strWfFor.equals(ApplicationConstants.MEMBER)) {
										pd.setMemberSentCopy(true);
									} else if (strWfFor.equals(ApplicationConstants.SPEAKER)) {
										pd.setSpeakerSentCopy(true);
									}
									p.getPartDrafts().add(pd);
									p.merge();
								}
							}else{
								PartDraft pd = new PartDraft();
								pd.setEditedBy(this.getCurrentUser().getActualUsername());
								pd.setEditedOn(new Date());
								pd.setEditedAs(userGroup.getUserGroupType().getName());
								pd.setLocale(locale.toString());
								pd.setMainHeading(p.getMainHeading());
								pd.setPageHeading(p.getPageHeading());
								pd.setWorkflowCopy(true);
								
								if(p.getEditedContent() != null && !p.getEditedContent().isEmpty()){
									pd.setRevisedContent(p.getEditedContent());
								}else if(p.getRevisedContent() != null && !p.getRevisedContent().isEmpty()){
									pd.setRevisedContent(p.getRevisedContent());
								}
								
								if (strWfFor.equals(ApplicationConstants.MEMBER)) {
									pd.setMemberSentCopy(true);
								} else if (strWfFor.equals(ApplicationConstants.SPEAKER)) {
									pd.setSpeakerSentCopy(true);
								}
								p.getPartDrafts().add(pd);
								p.merge();
							}
						}
					}
					Status status = null;
					String workflowName = null;
					Integer level = Integer.valueOf(strLevel);
					if (strWfFor.equals(ApplicationConstants.MEMBER)) {
						status = Status.findByType("editing_recommend_memberapproval", locale.toString());
						workflowName = "memberapproval_workflow";
					} else if (strWfFor.equals(ApplicationConstants.SPEAKER)) {
						status = Status.findByType("editing_recommend_speakerapproval", locale.toString());
						workflowName = "speakerapproval_workflow";
					}
					model.addAttribute("workflowName", workflowName);
					ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String, String> properties = new HashMap<String, String>();
					WorkflowActor wfActor = WorkflowConfig.findNextEditingActor(houseType, userGroup, status, workflowName, level, locale.toString());
					if (strWfFor.equals("member")) {
						List<Long> devices = new ArrayList<Long>();
						for(String val : strDevices.split(",")){
							devices.add(Long.valueOf(val));
						}
						List<Member> members = Part.findAllProceedingMembersOfRosterHavingDevices(roster, devices, locale.toString());
						for (Member m : members) {
							User user = User.findbyNameBirthDate(m.getFirstName(), m.getMiddleName(),m.getLastName(), m.getBirthDate());
							Credential credential = user.getCredential();
							UserGroup innerUserGroup = UserGroup.findActive(credential, new Date(), locale.toString());
							
							//It is done since in some parts speaker appears as primaryMember
							//so it needs to be excluded from entering the member's workflow
							if(innerUserGroup.getUserGroupType().getType().equals(ApplicationConstants.MEMBER)){
								if (user != null && wfActor != null) {
									properties.put("pv_user", credential.getUsername());
									properties.put("pv_endflag", "continue");
								} else {
									properties.put("pv_user", "");
									properties.put("pv_endflag", "end");
								}
								
								ProcessInstance processInstance = processService.createProcessInstance(processDefinition,properties);
								Task task = processService.getCurrentTask(processInstance);
								EditingControllerUtility.create(this.getCurrentUser(), roster.getId(),session, status, task, ApplicationConstants.APPROVAL_WORKFLOW, strDevices, wfActor.getLevel().toString(),locale.toString());
								
							}

						}
					} else if (strWfFor.equals("speaker")) {
						User user = EditingControllerUtility.getUser(wfActor, houseType, locale.toString());
						if (user != null && wfActor != null) {
							properties.put("pv_user", user.getCredential().getUsername());
							properties.put("pv_endflag", "continue");
						} else {
							properties.put("pv_user", "");
							properties.put("pv_endflag", "end");
						}

						ProcessInstance processInstance = processService.createProcessInstance(processDefinition,properties);
						Task task = processService.getCurrentTask(processInstance);
						EditingControllerUtility.create(this.getCurrentUser(), roster.getId(), session, status, task, ApplicationConstants.APPROVAL_WORKFLOW, strDevices, wfActor.getLevel().toString(), locale.toString());
					}
					retVal="SUCCESS";
				}else{
					retVal="FAIL";
				}
			}			
		}catch(Exception e){
			retVal="FAIL";
			logger.error(e.toString());
			e.printStackTrace();
		}
		return retVal;
	}
	
	//finds out if given device is enabled
	private boolean isDeviceEnabled(final String deviceId, final String devices){
		
		try{
			for(String val : devices.split(",")){
				if(deviceId.equals(val)){
					return true;
				}
			}
		}catch (Exception e) {
			logger.debug("editing/startworkflow",e);
			e.printStackTrace();
		}
		
		return false;
	}
	
//	@Transactional
//	@RequestMapping(value="/generatephotos/{id}",method=RequestMethod.GET)
//	public void generatePhotos(@PathVariable(value="id") Long id, HttpServletRequest request, Locale locale){
//		try{
//			Party party = Party.findById(Party.class, id);
//			
//			List<Member> members = new ArrayList<Member>();
//			
//			for(MemberPartyAssociation mpa : party.getMemberPartyAssociations()){
//				members.add(mpa.getMember());
//			}
//
//			String base = EditingController.class.getClassLoader().getResource("fop_storage").getPath().replaceAll("%20", " ").replaceFirst("/", "") + "memImg";
//			File imgDirectory = new File(base);
//			boolean flag = false;
//			if(imgDirectory.exists()){
//				flag = true;
//			}else{
//				flag = imgDirectory.mkdir();
//			}
//			
//			if(flag){
//				for(Member m : members){
//					if(m != null){
//						Doom doom = new Doom();
//						doom.setValue(m.getId().toString());
//						doom.persist();
//						if(m.getPhoto() != null && !m.getPhoto().isEmpty()){
//							Document doc = Document.findByTag(m.getPhoto());
//							if(doc != null){
//								File file = new File(base+"/"+doc.getOriginalFileName());
//								
//								FileOutputStream fos = new FileOutputStream(file);
//								byte[] data = doc.getFileData();
//								
//								for(int i = 0; i < data.length; i++){
//									fos.write(data[i]);
//								}
//								fos.close();
//							}
//						}
//					}
//				}
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	@Transactional
	private void cleanFacilityDrafts(HttpServletRequest request, Roster roster, Locale locale){
		try {
			List<Part> parts = Part.findAllPartOfProceedingOfRoster(roster, false, locale.toString());
			if(parts != null){
				int length = parts.size();
				for(int i = 0; i < length; i++){
					Part part = parts.get(i);
					List<PartDraft> drafts = part.getPartDrafts();
					part.setPartDrafts(null);
					if(drafts != null){
						int draftLength = drafts.size();
						for(int j = 0; j < draftLength; j++){
							PartDraft pd = drafts.get(j); 
							if(pd.getUniqueIdentifierForRedo() != null || pd.getUniqueIdentifierForUndo() != null){
								drafts.remove(j);
								pd.remove();
							}
						}
						
						part.setPartDrafts(drafts);
						part.merge();
					}
					
				}
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
	/*-------------------Shubham A-------------------------*/
	
	@RequestMapping(value = "/addRosterDataInWord",method = RequestMethod.GET)
	public  String addRosterDataInWord(final HttpServletRequest request,
			final Locale locale,ModelMap model) {
		
		
		DeviceType defaultSelectedDeviceType = DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION, locale.toString());			
		String device = defaultSelectedDeviceType.getDevice();  
		
		// Populate House types
		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", ApplicationConstants.ASC, locale.toString());
		model.addAttribute("houseTypes", houseTypes);
		HouseType defaultSelectedHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale.toString());
			
		// Populate Session types
		List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale.toString());
		model.addAttribute("sessionTypes", sessionTypes);
		
		// Populate latest Session type and year
		SessionType sessionType = null;
		Integer sessionYear = new GregorianCalendar().get(Calendar.YEAR);
		Session latestSession = null;
		try {
			latestSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(defaultSelectedHouseType, defaultSelectedDeviceType);
		} catch (ELSException e) {
			logger.error(e.getMessage());
			
		}
		
		if(latestSession != null) {
			sessionType = latestSession.getType();
			model.addAttribute("sessionType", sessionType.getId());
			sessionYear = latestSession.getYear();
		}else {
			System.out.println("Unable To find Latest Session"); // added SOP if logger not printed
			model.addAttribute("errorcode","");		
			return "common/error";  // returned from here cause can't execute further process if no latest session found
		}
		
		model.addAttribute("sessionYear", sessionYear);
		
		int year = sessionYear;
		CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		List<Reference> years = new ArrayList<Reference>();
		if(houseFormationYear != null){
			Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
			for(int i = year; i >= formationYear; i--){
				Reference reference = new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
				years.add(reference);
			}
		}else{
			logger.error("Cant Find House Formation Year");
			System.out.println("Cant Find House Formation Year"); // added SOP if logger not printed
			model.addAttribute("errorcode", "");	
			return "common/error";
			
		}
		model.addAttribute("years", years);
		
		//-----XVX-----
		List<MasterVO> days = new ArrayList<MasterVO>();
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale", new String[]{locale.toString()});
		List rosterDays = Query.findReport("EDIS_DISTINCT_ROSTER_DAYS", parameters);
		for(Object o : rosterDays){
			MasterVO mV = new MasterVO();
			Integer number = Integer.valueOf(o.toString()); 
			mV.setNumber(number);
			mV.setValue(FormaterUtil.formatNumberNoGrouping(number, locale.toString()));
			days.add(mV);
			number = null;
		}
		
		/****Days****/
		model.addAttribute("days",days);
		
		return "editing/addRosterDataInWord";
	}
	
	@RequestMapping(value = "/displayRosterList",method = RequestMethod.GET)
	public String displayRosterList(final HttpServletRequest request,
			final Locale locale,ModelMap model) {
		
		//the name is searched in detail_view Column
		String gridNameToSearchInDB ="editingRosterList";
		Grid grid = null;
		  try{
	        	grid = Grid.findByDetailView(gridNameToSearchInDB, locale.toString());
	        	model.addAttribute("gridId", grid.getId());
	        }catch (ELSException e) {
	        	System.out.printf("Cant find grid by detail view name of - [%s]",gridNameToSearchInDB); // added SOP if logger not printed
	        	logger.error(e.getMessage());
				model.addAttribute("error", e.getParameter());		
				return "common/error";
			}
		  
		  /** Setting Open Word Flag **/			
		model.addAttribute("openWordFlag", true);
		return "editing/list";
	}
	
	
	@RequestMapping(value="/getEditingRosterForRIS",method=RequestMethod.GET)
	public @ResponseBody ProceedingVO getEditingRosterForRIS(final HttpServletRequest request,
			final Locale locale,ModelMap model) throws ELSException {
		
		ProceedingVO editingRosterDetails = new ProceedingVO();
		int first_slot = 0;
		String dateFormat = "dd-MM-yyyy";
		String timeFormat = "HH:mm";
		
		String strRosterId = request.getParameter("roterId");
		
		Long rosterId  = Long.parseLong(strRosterId);
		Roster roster =Roster.findById(Roster.class, rosterId);
		if(roster == null) {
			throw new ELSException("Unable to Find Roster for Id :- ", strRosterId);
			
		}
		editingRosterDetails.setSlotName("-");
		editingRosterDetails.setLanguageReporter("-");
		editingRosterDetails.setPreviousReporter("-");
		editingRosterDetails.IsEditorTerm(true);
		
		List<Slot> firstAndLastSlot = Slot.getFirstAndLastSlotForGivenRoster(rosterId); 
		editingRosterDetails.setSlotName(firstAndLastSlot.get(first_slot).getName());
		
		String firstSlotDate = FormaterUtil.formatDateToString(firstAndLastSlot.get(first_slot).getStartTime(), dateFormat); 
		String firstSlotTime = FormaterUtil.formatDateToString(firstAndLastSlot.get(first_slot).getStartTime(), timeFormat);
		editingRosterDetails.setCurrentSlotStartDate(firstSlotDate);
		editingRosterDetails.setCurrentSlotStartTime(firstSlotTime);
		
				
		MessageResource generalNotice = MessageResource.findByFieldName(MessageResource.class, "code", "part.generalNotice", locale.toString());
		editingRosterDetails.setGeneralNotice(generalNotice.getValue());
		MessageResource mlsURL = MessageResource.findByFieldName(MessageResource.class, "code", "part.mlsUrl", locale.toString());
		editingRosterDetails.setMlsUrl(mlsURL.getValue());
		
		
		EditorPart editorPart=EditorPart.findByFieldName(EditorPart.class,"roster",roster,locale.toString());
		
		Long editorId = this.getCurrentUser().getUserId();
		User editor = User.findById(User.class, editorId);
		
		if(editorPart==null){
			editorPart=new EditorPart();
			//editorPart.setProceeding(domain);
			editorPart.setRoster(roster);
			editorPart.setLocale(locale.toString());
			editorPart.setEditor(editor);
			editorPart.persist();		
			editingRosterDetails.setPartid(editorPart.getId());
			editingRosterDetails.setVersion(editorPart.getVersion());
		}else{
			editingRosterDetails.setPartid(editorPart.getId());
			editingRosterDetails.setVersion(editorPart.getVersion());
		}
		
		return editingRosterDetails;
	}
	
	
	
}






/**
 * @author vikasg
 *
 * Helps to create the workflow by editor for the first time
 */
class EditingControllerUtility{
	private static Logger logger = LoggerFactory.getLogger(EditingControllerUtility.class);
	
	public static WorkflowDetails create(final AuthUser auser, 
			final Long rosterId, 
			final Session session,
			final Status status,
			final Task task,
			final String workflowType,
			final String devicesEnabled,
			final String assigneeLevel,
			final String locale) throws ELSException{
		
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username = task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup=UserGroup.findActive(credential, new Date(), locale);
					userGroupId=String.valueOf(userGroup.getId());
					userGroupType=userGroup.getUserGroupType().getType();
					userGroupName=userGroup.getUserGroupType().getName();
					
					workflowDetails.setLocale(locale);
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					workflowDetails.setDomainIds(devicesEnabled);
					
					workflowDetails.setAssigner(auser.getActualUsername());
					UserGroup auserGroup = null;
					for(UserGroup ug : auser.getUserGroups()){
						if(ug != null){
							auserGroup = ug;
							break;
						}
					}
					workflowDetails.setAssignerUserGroupId(auserGroup.getId().toString());
					workflowDetails.setAssignerUserGroupType(auserGroup.getUserGroupType().getType());
					
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					
					workflowDetails.setSessionType(session.getType().getSessionType());
					workflowDetails.setSessionYear(FormaterUtil.formatNumberNoGrouping(session.getYear(), locale));
					workflowDetails.setHouseType(session.getHouse().getType().getName());
					
					workflowDetails.setUrlPattern("workflow/editing");
					workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
					workflowDetails.setModule("EDITING");
					workflowDetails.setDeviceType("");
					workflowDetails.setDeviceId(rosterId.toString());
					
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					workflowDetails.setWorkflowSubType(status.getType());
					
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_question", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;		
	}
	
	public static List<WorkflowDetails> create(final Long rosterId,
			final Session session,
			final Status status,
			final List<Task> tasks,
			final String workflowType,
			final String assigneeLevel,
			final String locale) throws ELSException {
		List<WorkflowDetails> workflowDetailsList = new ArrayList<WorkflowDetails>();
		try {
			
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter != null){
				SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i:tasks){
					WorkflowDetails workflowDetails = new WorkflowDetails();
					String userGroupId = null;
					String userGroupType = null;
					String userGroupName = null;				
					String username = i.getAssignee();
					if(username != null){
						if(!username.isEmpty()){
							Credential credential = Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroup userGroup=UserGroup.findActive(credential, new Date(), locale);
							userGroupId=String.valueOf(userGroup.getId());
							userGroupType=userGroup.getUserGroupType().getType();
							userGroupName=userGroup.getUserGroupType().getName();
							workflowDetails.setAssignee(i.getAssignee());
							workflowDetails.setAssigneeUserGroupId(userGroupId);
							workflowDetails.setAssigneeUserGroupType(userGroupType);
							workflowDetails.setAssigneeUserGroupName(userGroupName);
							workflowDetails.setAssigneeLevel(assigneeLevel);
							if(i.getCreateTime()!=null){
								if(!i.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(i.getCreateTime()));
								}
							}
							
							
							workflowDetails.setDeviceId(rosterId.toString());
							
							workflowDetails.setHouseType(session.getHouse().getType().getName());								
							workflowDetails.setSessionType(session.getType().getSessionType());								
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(session.getYear()));
								
								
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							
							workflowDetails.setUrlPattern("workflow/editing");
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							workflowDetails.setModule("EDITING");
							workflowDetails.setDeviceType("");
							workflowDetails.setDeviceId(rosterId.toString());
							
							workflowDetailsList.add((WorkflowDetails) workflowDetails.persist());
						}				
					}
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return workflowDetailsList;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("Editing create workflowdetails", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}	
	
	public static String[] getDecodedString(String[] values){
		CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(deploymentServer != null && deploymentServer.getValue() != null && !deploymentServer.getValue().isEmpty()){
			if(deploymentServer.getValue().equals("TOMCAT")){

				for(int i = 0; i < values.length; i++){
					try {
						if(values[i] != null){
							values[i] = new String(values[i].getBytes("ISO-8859-1"), "UTF-8");
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return values;
	}
	
	public static String concat(String[] value, String seperator){
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < value.length; i++){
			buff.append(value[i]);
			if(i < (value.length -1)){
				buff.append(seperator);
			}
		}
		
		return buff.toString();
	}

	public static  User getUser(final WorkflowActor wfActor, final HouseType houseType, final String locale) {
		UserGroup userGroup = getUserGroup(wfActor, houseType, locale);
		if(userGroup != null) {
			User user = getUser(userGroup, locale);
			return user;
		}
		
		return null;
	}
	
	public static User getUser(final UserGroup userGroup,
			final String locale) {
		Credential credential = userGroup.getCredential();
		User user = User.findByFieldName(User.class,"credential", credential, locale);
		return user;
	}
	
	public static UserGroup getUserGroup(final WorkflowActor wfActor, 
			final HouseType houseType,
			final String locale) {
		List<UserGroup> userGroups = getUserGroups(wfActor, locale);		
		UserGroup userGroup = getEligibleUserGroup(userGroups, houseType, true, locale);
		if(userGroup != null) {
			return userGroup;
		}
		
		return null;
	}
	public static List<UserGroup> getUserGroups(
			final WorkflowActor workflowActor,
			final String locale) {
		UserGroupType userGroupType = workflowActor.getUserGroupType();
		
		List<UserGroup> userGroups = 
			UserGroup.findAllByFieldName(UserGroup.class, "userGroupType", 
					userGroupType, "activeFrom", ApplicationConstants.DESC, 
					locale);
		return userGroups;
	}
	
	public static HouseType getHouseType(final UserGroup userGroup, 
			final String locale) {
		String strHouseType = 
			userGroup.getParameterValue("HOUSETYPE_" + locale);
		if(strHouseType != null && ! strHouseType.trim().isEmpty()) {
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			return houseType;
		}
		
		return null;
	}
	
	public static UserGroup getEligibleUserGroup(List<UserGroup> userGroups,
			final HouseType houseType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		for(UserGroup ug : userGroups) {
			// ug's houseType should be same as @param houseType
			boolean flag1 = false;
			String houseTypeType = houseType.getType();
			HouseType usersHouseType = getHouseType(ug, locale);
			if(isIncludeBothHouseType) {
				if(usersHouseType != null &&
						(usersHouseType.getType().equals(houseTypeType)
						|| usersHouseType.getType().equals(ApplicationConstants.BOTH_HOUSE))) {
					flag1 = true;
				}
			}else {
				if(usersHouseType != null && usersHouseType.getType().equals(houseTypeType)) {
					flag1 = true;
				}
			}
			
			
			// ug must be active
			boolean flag2 = false;
			Date fromDate = ug.getActiveFrom();
			Date toDate = ug.getActiveTo();
			Date currentDate = new Date();
			if((fromDate == null || currentDate.after(fromDate) ||currentDate.equals(fromDate))
					&& (toDate == null || currentDate.before(toDate) || currentDate.equals(toDate))) {
				flag2 = true;
			}
			
			boolean flag3 = false;
			
			
			// if all cases are met then return user
			if(flag1 && flag2) {
				return ug;
			}
		}
		
		return null;
	}

	public static UserGroup getUserGroup(final WorkflowDetails workflowDetails) {
		String strUserGroupId = workflowDetails.getAssigneeUserGroupId();
		Long userGroupId = Long.valueOf(strUserGroupId);
		UserGroup userGroup = UserGroup.findById(UserGroup.class, userGroupId);
		return userGroup;
	}
	
	
	public static WorkflowDetails createInitWorkflowDetails(
			final HttpServletRequest request,
			final AuthUser authUser,
			final UserGroup userGroup,
			final HouseType houseType,
			final Status status,
			final Integer assigneeLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		String wfName = getFullWorkflowName(status);
		String wfSubType =  getWorkflowSubType(status);
		Date assignmentTime = new Date();
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
				
		WorkflowActor wfActor = getNextActor(request, userGroup, houseType, assigneeLevel, locale);
		if(wfActor != null) {
		// User parameters
		
			String assignee = authUser.getUsername();
			UserGroupType ugt = userGroup.getUserGroupType();
			wfDetails.setAssignee(assignee);
			wfDetails.setAssigneeUserGroupType(ugt.getType());
			wfDetails.setAssigneeUserGroupId(String.valueOf(userGroup.getId()));
			wfDetails.setAssigneeUserGroupName(ugt.getName());
			wfDetails.setAssigneeLevel(String.valueOf(assigneeLevel));
			wfDetails.setNextWorkflowActorId(String.valueOf(wfActor.getId()));
			
		}
		wfDetails.setHouseType(houseType.getName());
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.EDITING);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	
	public static WorkflowActor getNextActor(final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final Integer assigneeLevel,
			final String locale) {
		WorkflowActor wfActor = null;
		
		String strWFActorId = request.getParameter("actor");
		
		Status status = getStatus(request);
		String wfName = getFullWorkflowName(status);
			
		wfActor = WorkflowConfig.findNextCommitteeActor(houseType,userGroup, status, wfName, assigneeLevel, locale);
		
		
		return wfActor;
	}
	
	public static Status getStatus(final HttpServletRequest request) {
		String strStatusId = request.getParameter("status");
		Long statusId = Long.valueOf(strStatusId);
		Status status = Status.findById(Status.class, statusId); 
		return status;
	}
	
	public static String getFullWorkflowName(final Status status) {
		String wfName = getWorkflowName(status);
		String fullWfName = wfName + "_workflow";
		return fullWfName;
	}
	
	
	
	public static String getWorkflowName(final Status status) {
		String statusType = status.getType();
		String[] tokens = splitter(statusType, "_");
		int length = tokens.length;
		return tokens[length - 1];
	}
	
	public static String[] splitter(String value, String splitterCharacter){
		String[] vals = value.split(splitterCharacter);
		
		int length = vals.length;
		for(int i = 0; i < length; i++) {
			vals[i] = vals[i].trim();
		}

		return vals;
	}
	public static String getWorkflowSubType(final Status status) {
		return status.getType();
	}
	public static WorkflowDetails createNextActorWorkflowDetails(
			final HttpServletRequest request,
			final Task task,
			final UserGroup currentActorUserGroup,
			final HouseType houseType,
			final Status status,
			final Integer currentActorLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		String wfName = null;
		String wfSubType =  null;
		Date assignmentTime = new Date();
		wfDetails.setProcessId(task.getProcessInstanceId());
		wfDetails.setTaskId(task.getId());
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
		
		// User parameters
		// Not applicable parameters: nextWorkflowActorId
		WorkflowActor nextActor = null;
		UserGroup nextUserGroup = null;
		UserGroupType nextUGT = nextUserGroup.getUserGroupType();
		wfDetails.setAssignee(task.getAssignee());
		wfDetails.setAssigneeUserGroupType(nextUGT.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(nextUserGroup.getId()));
		wfDetails.setAssigneeUserGroupName(nextUGT.getName());
		wfDetails.setAssigneeLevel(String.valueOf(nextActor.getLevel()));
		
		wfDetails.setHouseType(houseType.getName());
		
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.EDITING);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	/**
	 * @param nextWorkflowActor could be null
	 */
	public static void updateWorkflowDetails(final WorkflowDetails workflowDetails, final WorkflowActor nextWorkflowActor) {
		Date completionTime = new Date();
		workflowDetails.setCompletionTime(completionTime);
		workflowDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
		
		if(nextWorkflowActor != null) {
			String wfActorId = String.valueOf(nextWorkflowActor.getId());
			workflowDetails.setNextWorkflowActorId(wfActorId);
		}
		workflowDetails.merge();
	}
	
	/**
	 * @param session
	 * @param houseType
	 * @param deviceIds
	 * @param workflowSubTypeInitial
	 * @param locale
	 * @return
	 * verify if vishaysuchi can be prepared
	 */
	@SuppressWarnings("rawtypes")
	public static boolean canVishaysuchiBePrepared(final Session session, 
					final HouseType houseType,
					final Long[] deviceIds,
					final String workflowSubTypeInitial,
					final String locale){
		boolean retVal = false;
		boolean[] flags = new boolean[deviceIds.length];
		
		int i = 0;
		for(Long deviceId : deviceIds){
			flags[i] = false;
			Integer workflowCount = WorkflowDetails.findIfWorkflowExists(session, houseType, deviceId.toString(), workflowSubTypeInitial, locale);
			if( workflowCount != null && workflowCount > 0){
				//TODO: completeness criteria is very primitive needs to be enhanced to include member and speaker
				//wf Separately rather than including them as whole in 'COMPLETE or PENDING criteria
				//change the way to find member wf then calculate how many member wfs have been
				//been completed and how many pending and how many are timed out
				//similar for speaker wf
				//since if only one wf cycle is complete means vishaysuchi is ready and can be produced
				//which may not be the case
				//need some more clarification on this
				List result = WorkflowDetails.findCompleteness(session, houseType, deviceId.toString(), locale);
							
				if(result != null && !result.isEmpty()){
					Object[] data = (Object[])result.get(0);
					Integer totalTasks = Integer.valueOf(data[1].toString());
					Integer doneTasks = Integer.valueOf(data[2].toString());
					
					if((totalTasks - doneTasks) == 0){
						flags[i] = true;
					}else{
						flags[i] = false;
					}
				}
			}
			i++;
		}
		for(boolean b : flags){
			if(b){
				retVal = true;
			}else{
				retVal = false;
				break;
			}
		}
		return retVal;
	}
}
