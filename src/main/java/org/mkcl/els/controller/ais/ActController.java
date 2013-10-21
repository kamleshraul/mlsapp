package org.mkcl.els.controller.ais;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Act;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.TextDraft;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/act")
public class ActController extends GenericController<Act> {

	@Override
	protected void populateNew(final ModelMap model, final Act domain,
            final String locale, final HttpServletRequest request) {
		/**** Locale ****/
		domain.setLocale(locale);
		/**** years for act ****/
		boolean yearsPopulated = populateYearsForAct(model, domain, locale);	
		if(!yearsPopulated) {
			return;
		}
		/**** populating titles ****/
		boolean titlesPopulated = populatedTitlesForAct(model, domain, locale);		
		if(!titlesPopulated) {
			return;
		}
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final Act domain, final HttpServletRequest request) {
		/**** years for act ****/
		boolean yearsPopulated = populateYearsForAct(model, domain, domain.getLocale());	
		if(!yearsPopulated) {
			return;
		}
		/**** populating titles ****/
		boolean titlesPopulated = populatedTitlesForAct(model, domain, domain.getLocale());		
		if(!titlesPopulated) {
			return;
		}
	}
	
	@Override
	protected String modifyEditUrlPattern(final String newUrlPattern, final HttpServletRequest request, final ModelMap model, final String locale) {
        String edit = request.getParameter("edit");
        if(edit!=null) {
        	if(!edit.isEmpty()) {
        		if(!Boolean.parseBoolean(edit)){
        			model.addAttribute("isFileRemovable", false);
        			return newUrlPattern.replace("edit","editreadonly");
        		}
        	}
        }
		return newUrlPattern;
    }
	
	@Override
	protected void customValidateCreate(final Act domain, final BindingResult result,
			final HttpServletRequest request) {
		customValidateCommon(domain, result, request);		
	}
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, Act domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateCreateIfNoErrors(ModelMap model, Act domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);		
	}
	
	@Override
	protected void customValidateUpdate(final Act domain, final BindingResult result,
			final HttpServletRequest request) {
		customValidateCommon(domain, result, request);	
	}
	
	@Override
	protected void populateUpdateIfErrors(ModelMap model, Act domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);
		populateEdit(model, domain, request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateUpdateIfNoErrors(ModelMap model, Act domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);		
	}
	
	private boolean populateYearsForAct(ModelMap model, Act domain, String locale) {
		CustomParameter firstActYearParameter = CustomParameter.findByName(CustomParameter.class, "FIRST_ACT_YEAR", "");
		if(firstActYearParameter!=null) {
			String firstActYearStr = firstActYearParameter.getValue();
			if(firstActYearStr!=null) {
				if(!firstActYearStr.isEmpty()) {
					int firstActYear = Integer.parseInt(firstActYearStr);
					List<MasterVO> years = new ArrayList<MasterVO>();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					int currentYear = calendar.get(Calendar.YEAR);
					for(int i=currentYear; i>=firstActYear; i--) {
						MasterVO year = new MasterVO();
						year.setNumber(i);
						year.setName(FormaterUtil.formatNumberNoGrouping(i, locale));						
						years.add(year);
					}
					model.addAttribute("years", years);
					if(domain.getYear()!=null) {
						model.addAttribute("selectedYear", domain.getYear());
					}
					return true;
				} else{
					logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set with empty value. ****");
					model.addAttribute("errorcode", "firstactyearnotset");
					return false;
				}
			} else{
				logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set to null. ****");
				model.addAttribute("errorcode", "firstactyearnotset");
				return false;
			}		
		} else{
			logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is not set. ****");
			model.addAttribute("errorcode", "firstactyearnotset");
			return false;
		}
	}
	
	private boolean populatedTitlesForAct(ModelMap model, Act domain, String locale) {
		CustomParameter languagesAllowedForActParameter = CustomParameter.findByName(CustomParameter.class, "ACT_LANGUAGES_ALLOWED", "");
    	if(languagesAllowedForActParameter!=null) {    		
    		String languagesAllowedForAct = languagesAllowedForActParameter.getValue();
    		if(languagesAllowedForAct!=null) {
    			if(!languagesAllowedForAct.isEmpty()) {
    				List<Language> languagesAllowedForTitle = new ArrayList<Language>();
    				for(String languageAllowedForAct: languagesAllowedForAct.split("#")) {	
    					Language languageAllowed = Language.findByFieldName(Language.class, "type", languageAllowedForAct, domain.getLocale());
    					languagesAllowedForTitle.add(languageAllowed);
    				}
    				List<TextDraft> titles = new ArrayList<TextDraft>();
    				if(domain.getTitles()!=null && !domain.getTitles().isEmpty()) {				
    					titles.addAll(domain.getTitles());
    					for(TextDraft title: domain.getTitles()) {
    						languagesAllowedForTitle.remove(title.getLanguage());					
    					}				
    				}
    				if(!languagesAllowedForTitle.isEmpty()) {								
    					for(Language languageAllowedForTitle: languagesAllowedForTitle) {
    						TextDraft title = new TextDraft();
    						title.setLanguage(languageAllowedForTitle);
    						title.setText("");
    						titles.add(title);
    					}
    				}
    				model.addAttribute("titles",titles);
    				return false;
    			} logger.error("**** Custom Parameter 'ACT_LANGUAGES_ALLOWED' is set with empty value. ****");
    			model.addAttribute("errorcode", "act_languages_allowed_notset");
    			return false;
    		} logger.error("**** Custom Parameter 'ACT_LANGUAGES_ALLOWED' is set with null value. ****");
			model.addAttribute("errorcode", "act_languages_allowed_notset");
			return false;
    	} else{
			logger.error("**** Custom Parameter 'ACT_LANGUAGES_ALLOWED' is not set. ****");
			model.addAttribute("errorcode", "act_languages_allowed_notset");
			return false;
		}		
	}
	
	private void customValidateCommon(Act domain, BindingResult result,
			HttpServletRequest request) {
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
		}
		if(domain.getYear()==null){
			result.rejectValue("year","YearEmpty","Year is not set.");
		}
		if(domain.getNumber()==null){
			result.rejectValue("number","NumberEmpty","Number is not set.");
		}
		/**** title validation ****/
		CustomParameter languagesAllowedForActParameter = CustomParameter.findByName(CustomParameter.class, "ACT_LANGUAGES_ALLOWED", "");
		String languagesAllowedForAct = languagesAllowedForActParameter.getValue();
		boolean isTitleInAtleastOneLanguage = false;			
		for(String languageAllowedForAct: languagesAllowedForAct.split("#")) {
			String titleTextInThisLanguage = request.getParameter("title_text_"+languageAllowedForAct);
			if(titleTextInThisLanguage!=null && !titleTextInThisLanguage.isEmpty()) {
				isTitleInAtleastOneLanguage = true;
				break;
			}
		}
		if(isTitleInAtleastOneLanguage==false) {
			result.rejectValue("version","TitleEmpty","Please enter the title in atleast one language.");
		}
	}
	
	private List<TextDraft> updateDraftsOfGivenType(Act domain, String typeOfDraft, HttpServletRequest request) {
		List<TextDraft> draftsOfGivenType = new ArrayList<TextDraft>();
		CustomParameter languagesAllowedForActParameter = CustomParameter.findByName(CustomParameter.class, "ACT_LANGUAGES_ALLOWED", "");
		String languagesAllowedForAct = languagesAllowedForActParameter.getValue();
		for(String languageAllowedInAct: languagesAllowedForAct.split("#")) {
			String draftTextInThisLanguage = request.getParameter(typeOfDraft+"_text_"+languageAllowedInAct);
			if(draftTextInThisLanguage!=null && !draftTextInThisLanguage.isEmpty()) {
				TextDraft draftOfGivenType = null;				
				String draftIdInThisLanguage = request.getParameter(typeOfDraft+"_id_"+languageAllowedInAct);
				if(draftIdInThisLanguage!=null && !draftIdInThisLanguage.isEmpty()) {
					draftOfGivenType = TextDraft.findById(TextDraft.class, Long.parseLong(draftIdInThisLanguage));					
				} else {
					draftOfGivenType = new TextDraft();
				}
				draftOfGivenType.setText(draftTextInThisLanguage);
				if(draftOfGivenType.getLanguage()==null) {
					Language thisLanguage;
					String draftLanguageId = request.getParameter(typeOfDraft+"_language_id_"+languageAllowedInAct);
					if(draftLanguageId!=null && !draftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(draftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInAct, domain.getLocale());
					}					
					draftOfGivenType.setLanguage(thisLanguage);
				}
				draftOfGivenType.setLocale(domain.getLocale());
				draftsOfGivenType.add(draftOfGivenType);
			}
		}
		return draftsOfGivenType;	
	}
	
}
