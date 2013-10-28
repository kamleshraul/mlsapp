package org.mkcl.els.controller.edis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import java.util.Set;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.UserGroup;
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
	
			List<MasterVO> days = new ArrayList<MasterVO>();
			List<Roster> rosters = Roster.findAll(Roster.class, "day", ApplicationConstants.ASC, locale);
			for(Roster r : rosters){
				MasterVO mV = new MasterVO();
				mV.setId(r.getId());
				mV.setNumber(r.getDay());
				mV.setValue(FormaterUtil.formatNumberNoGrouping(r.getDay(), locale));
				
				days.add(mV);
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
	
	@Transactional
	@RequestMapping(value="/compiledreport", method=RequestMethod.GET)
	public String viewEditingReport(final HttpServletRequest request, final ModelMap model, final Locale locale){
		String retVal= "editing/error"; 
		
		model.addAttribute("undoCount", 0);
		model.addAttribute("redoCount", 0);
		
		try{
			
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
			model.addAttribute("action", strAction);
			model.addAttribute("reedit", strReedit);
			model.addAttribute("reportType", strReportType);
			List result = null;
			if(!strReportType.isEmpty() && !strReportType.equals("nothing")){
				if(strHouseType!=null&&!strHouseType.equals("")&&
						strSessionType!=null&&!strSessionType.equals("")&&
						strSessionYear!=null&&!strSessionYear.equals("")&&
						strLanguage!=null&&!strLanguage.equals("")&&
						strDay!=null&&!strDay.equals("")){
	
					HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
					SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
					Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
					Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
					
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
					
					if(strReedit != null && !strReedit.isEmpty()){
						if(strReedit.equals("true")){
							if(!result.isEmpty()){
								for(int i = 0; i < result.size(); i++){
									String strId = (((Object[])result.get(i))[20]).toString();
									Part partToDel = Part.findById(Part.class, Long.valueOf(strId));
									partToDel.setEditedContent("");
									for(PartDraft pd : partToDel.getPartDrafts()){
										boolean success = pd.remove();
									}
									partToDel.setPartDrafts(null);
									partToDel.merge();
									
								}
							}
							
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
					
					model.addAttribute("report", result);
					
					retVal = "editing/compileedit";
				}
			}
					
		}catch (Exception e) {
			if(e instanceof NoResultException){
				model.addAttribute("error", "No roster found for the day");
				model.addAttribute("errorcode", "PARAMETER_MISMATCH");
			}
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	@Transactional
	@RequestMapping(value="/savepart/{id}", method=RequestMethod.POST)
	public @ResponseBody String saveDraft(@PathVariable(value="id") Long id, Part domain, HttpServletRequest request, Locale locale){
		String retVal = "FAILURE";
		Part part = Part.findById(Part.class, id);
		if(part != null){
			/****Create the part draft****/
			AuthUser user = this.getCurrentUser();
			
			List<UserGroup> userGroups = user.getUserGroups();
			UserGroup userGroup = null;
			
			for(UserGroup ug : userGroups){
				userGroup = ug;
				break;
			}
			
			PartDraft draft = new PartDraft();
			draft.setEditedBy(user.getActualUsername());	
			if(userGroup != null){
				draft.setEditedAs(userGroup.getUserGroupType().getName());
			}
			draft.setEditedOn(new Date());
			draft.setLocale(part.getLocale());
			draft.setMainHeading(part.getMainHeading());
			draft.setPageHeading(part.getPageHeading());
			draft.setRevisedContent(domain.getEditedContent());
			
			part.getPartDrafts().add(draft);
			part.setEditedContent(domain.getEditedContent());
			part.merge();
			
			retVal = "SUCCESS"; 
		}
		
		return retVal;
	}

	@RequestMapping(value="/revisions/{id}", method=RequestMethod.GET)	
	public String getRevisions(@PathVariable(value="id") Long id, HttpServletRequest request, ModelMap model, Locale locale){
		String retVal = "editing/error";
		try{
					
			model.addAttribute("drafts", Part.findRevision(id, locale.toString()));
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
			/*model.addAttribute("searchTerm", domain.getSearchTerm());
			model.addAttribute("replaceTerm", domain.getReplaceTerm());
			model.addAttribute("undoCount", (domain.getUndoCount() + 1));
			domain.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
			domain.setUniqueIdentifierForRedo(UUID.randomUUID().toString());
			model.addAttribute("uniqueIdentifierForUndo", domain.getUniqueIdentifierForUndo());
			model.addAttribute("uniqueIdentifierForRedo", domain.getUniqueIdentifierForRedo());
			model.addAttribute("undoCount", (domain.getUndoCount() + 1));
			model.addAttribute("redoCount", (domain.getRedoCount() + 1));*/
			
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strLanguage = request.getParameter("language");
			String strDay = request.getParameter("day");
			
			String strSearchTerm = request.getParameter("searchTerm");
			String strReplaceTerm = request.getParameter("replaceTerm");
			
			String strUndoCount = request.getParameter("undoCount");
			String strRedoCount = request.getParameter("redoCount");
			Integer undoCount = Integer.valueOf(strUndoCount);
			Integer redoCount = Integer.valueOf(strRedoCount);
			
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
				
				matchedParts = Part.findAllEligibleForReplacement(roster, strSearchTerm, strReplaceTerm, locale.toString()); 
						//Part.findAllPartRosterSearchTerm(roster, domain.getSearchTerm(), locale.toString());//Query.findReport("EDIS_MATCHING_PARTS_FOR_REPLACEMENT", parametersMap);
				
				if(matchedParts != null && !matchedParts.isEmpty()){
					for(int i = 0; i < matchedParts.size(); i++){
						
						Object[] objArr = (Object[])matchedParts.get(i);
						if(!objArr[3].toString().equals(objArr[4].toString())){
							Part partToBeReplaced = Part.findById(Part.class, Long.valueOf(objArr[0].toString()));
							/****Create draft****/
							PartDraft pd = new PartDraft();
							pd.setEditedBy(this.getCurrentUser().getActualUsername());
							pd.setEditedOn(new Date());
							pd.setLocale(locale.toString());
							pd.setMainHeading(partToBeReplaced.getMainHeading());
							pd.setPageHeading(partToBeReplaced.getPageHeading());
							pd.setOriginalText(objArr[3].toString());
							pd.setReplacedText(objArr[4].toString());
							pd.setRevisedContent(objArr[4].toString());
							pd.setUndoCount(undoCount);
							pd.setRedoCount(redoCount);
							pd.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
							pd.setUniqueIdentifierForRedo(UUID.randomUUID().toString());
							
							/****Attach undoCount and undoUID in the result list****/
							((Object[])matchedParts.get(i))[5] = partToBeReplaced.getId().toString()+":"+pd.getUndoCount()+":"+pd.getUniqueIdentifierForUndo();
							((Object[])matchedParts.get(i))[6] = partToBeReplaced.getId().toString()+":"+pd.getRedoCount()+":"+pd.getUniqueIdentifierForRedo();
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
	@RequestMapping(value="/undolastchange",method=RequestMethod.POST)
	public @ResponseBody MasterVO doUndo(HttpServletRequest request, PartDraft domain, HttpServletResponse response, Locale locale){
		MasterVO masterVO = null;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("locale", locale.toString());
		parameters.put("uniqueIdentifierForUndo", domain.getUniqueIdentifierForUndo());
		parameters.put("undoCount", domain.getUndoCount().toString());
		parameters.put("editedOn", (new Date()).toString());
		parameters.put("editedBy", this.getCurrentUser().getActualUsername());
		PartDraft pd = PartDraft.findByFieldNames(PartDraft.class, parameters, locale.toString());
		if(pd != null){
			masterVO = new MasterVO();
			masterVO.setId(pd.getId());
			masterVO.setValue(pd.getOriginalText());
		}
		return masterVO;
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
}
