package org.mkcl.els.controller.edis;

import java.io.File;
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

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
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
		Session lastSessionCreated = null;
		try {
			lastSessionCreated = Session.findLatestSession(authUserHouseType);
		} catch (ELSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		List<Language> languages = null;
		try {
			languages = Language.findAllLanguagesByModule("RIS",locale);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("languages",languages);
	}
	
	@Transactional
	@RequestMapping(value="/compiledreport", method=RequestMethod.GET)
	public String viewRoster(final HttpServletRequest request, final ModelMap model, final Locale locale){
		String retVal= "editing/error"; 
		Part part = new Part();
		model.addAttribute("part", part);
		try{
			
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			String strLanguage=request.getParameter("language");
			String strDay=request.getParameter("day");
			String strAction = request.getParameter("action");
			String strReedit = request.getParameter("reedit");
			model.addAttribute("action", strAction);
			model.addAttribute("reedit", strReedit);
			
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
				/*if(strReedit != null && !strReedit.isEmpty()){
					if(strReedit.equals("true")){
						Proceeding proceed = Proceeding.find
					}
				}*/
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.put("locale", new String[]{locale.toString()});
				parametersMap.put("languageId", new String[]{language.getId().toString()});
				parametersMap.put("rosterId", new String[]{roster.getId().toString()});
				List result=Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);
				
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
						result=Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);
					}
				}
				
				model.addAttribute("report", result);
				
				retVal = "editing/compileedit";
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
	public void generateEditingReports(HttpServletRequest request, HttpServletResponse response, ModelMap model, Locale locale){
		
		try {

			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strLanguage = request.getParameter("language");
			String strDay = request.getParameter("day");
			String strAction = request.getParameter("action");
			String strReedit = request.getParameter("reedit");
			String strReportFormat = request.getParameter("format");
					
			model.addAttribute("action", strAction);
			model.addAttribute("reedit", strReedit);

			if (strHouseType != null && !strHouseType.equals("")
					&& strSessionType != null && !strSessionType.equals("")
					&& strSessionYear != null && !strSessionYear.equals("")
					&& strLanguage != null && !strLanguage.equals("")
					&& strDay != null && !strDay.equals("")
					&& strReportFormat != null && !strReportFormat.isEmpty()) {

				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType,locale.toString());
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Language language = Language.findById(Language.class,Long.parseLong(strLanguage));
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, Integer.parseInt(strSessionYear));
				Roster roster = Roster.findRosterBySessionLanguageAndDay(session, Integer.parseInt(strDay), language,locale.toString());

				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.put("locale", new String[] { locale.toString() });
				parametersMap.put("languageId", new String[] { language.getId().toString() });
				parametersMap.put("rosterId", new String[] { roster.getId().toString() });
				List result = Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT,parametersMap);

				model.addAttribute("report", result);
				String templateName = null;
				if(strReportFormat.equals("PDF")){
					templateName = "template_editor_report_pdf";
				}else if(strReportFormat.equals("WORD")){
					templateName = "template_editor_report_word";
				}
				File reportFile = generateReportUsingFOP(new Object[] { result, strAction },templateName, strReportFormat, "editor report",locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
			}

		} catch (Exception e) {
			if (e instanceof NoResultException) {
				model.addAttribute("error", "No roster found for the day");
				model.addAttribute("errorcode", "PARAMETER_MISMATCH");
			}
			e.printStackTrace();
		}
	}
}
