package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Rule;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.TextDraft;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rule")
public class RuleController extends GenericController<Rule> {
	
	@Override
	protected void populateNew(final ModelMap model, final Rule domain,
            final String locale, final HttpServletRequest request) {
		/**** Locale ****/
		domain.setLocale(locale);
		/**** House Type ****/
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "type", "asc", locale.toString());
        model.addAttribute("houseTypes",houseTypes);
        if(domain.getHouseType()!=null) {
        	model.addAttribute("selectedHouseType",domain.getHouseType().getId());
        }        
		/**** years for rule ****/
		boolean yearsPopulated = populateYearsForRule(model, domain, locale);	
		if(!yearsPopulated) {
			return;
		}
		/**** populating titles ****/
		boolean titlesPopulated = populatedTitlesForRule(model, domain, locale);		
		if(!titlesPopulated) {
			return;
		}
		/**** populating contentDrafts ****/
		boolean contentDraftsPopulated = populateContentDraftsForRule(model, domain, locale);		
		if(!contentDraftsPopulated) {
			return;
		}
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final Rule domain, final HttpServletRequest request) {
		/**** House Type ****/
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "type", "asc", domain.getLocale());
        model.addAttribute("houseTypes",houseTypes);
        if(domain.getHouseType()!=null) {
        	model.addAttribute("selectedHouseType",domain.getHouseType().getId());
        }
		/**** years for rule ****/
		boolean yearsPopulated = populateYearsForRule(model, domain, domain.getLocale());	
		if(!yearsPopulated) {
			return;
		}
		/**** populating titles ****/
		boolean titlesPopulated = populatedTitlesForRule(model, domain, domain.getLocale());		
		if(!titlesPopulated) {
			return;
		}
		/**** populating contentDrafts ****/
		boolean contentDraftsPopulated = populateContentDraftsForRule(model, domain, domain.getLocale());	
		if(!contentDraftsPopulated) {
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
	protected void customValidateCreate(final Rule domain, final BindingResult result,
			final HttpServletRequest request) {
		customValidateCommon(domain, result, request);		
	}
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, Rule domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);
		/**** add/update contentDrafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateCreateIfNoErrors(ModelMap model, Rule domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);		
		/**** add/update contentDrafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);
	}
	
	@Override
	protected void customValidateUpdate(final Rule domain, final BindingResult result,
			final HttpServletRequest request) {
		customValidateCommon(domain, result, request);	
	}
	
	@Override
	protected void populateUpdateIfErrors(ModelMap model, Rule domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);
		/**** add/update contentDrafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);
		populateEdit(model, domain, request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateUpdateIfNoErrors(ModelMap model, Rule domain,
			HttpServletRequest request) {
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);	
		/**** add/update contentDrafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);
	}
	
	private boolean populateYearsForRule(ModelMap model, Rule domain, String locale) {
		CustomParameter firstRuleYearParameter = CustomParameter.findByName(CustomParameter.class, "FIRST_RULE_YEAR", "");
		if(firstRuleYearParameter!=null) {
			String firstRuleYearStr = firstRuleYearParameter.getValue();
			if(firstRuleYearStr!=null) {
				if(!firstRuleYearStr.isEmpty()) {
					int firstRuleYear = Integer.parseInt(firstRuleYearStr);
					List<MasterVO> years = new ArrayList<MasterVO>();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					int currentYear = calendar.get(Calendar.YEAR);
					for(int i=currentYear; i>=firstRuleYear; i--) {
						MasterVO year = new MasterVO();
						year.setNumber(i);
						year.setName(FormaterUtil.formatNumberNoGrouping(i, locale));						
						years.add(year);
					}
					model.addAttribute("years", years);
					if(domain.getEditionYear()!=null) {
						model.addAttribute("selectedYear", domain.getEditionYear());
					}
					return true;
				} else{
					logger.error("**** Custom Parameter 'FIRST_RULE_YEAR' is set with empty value. ****");
					model.addAttribute("errorcode", "firstruleyearnotset");
					return false;
				}
			} else{
				logger.error("**** Custom Parameter 'FIRST_RULE_YEAR' is set to null. ****");
				model.addAttribute("errorcode", "firstruleyearnotset");
				return false;
			}		
		} else{
			logger.error("**** Custom Parameter 'FIRST_RULE_YEAR' is not set. ****");
			model.addAttribute("errorcode", "firstruleyearnotset");
			return false;
		}
	}
	
	private boolean populatedTitlesForRule(ModelMap model, Rule domain, String locale) {
		CustomParameter languagesAllowedForRuleParameter = CustomParameter.findByName(CustomParameter.class, "RULE_LANGUAGES_ALLOWED", "");
    	if(languagesAllowedForRuleParameter!=null) {    		
    		String languagesAllowedForRule = languagesAllowedForRuleParameter.getValue();
    		if(languagesAllowedForRule!=null) {
    			if(!languagesAllowedForRule.isEmpty()) {
    				List<Language> languagesAllowedForTitle = new ArrayList<Language>();
    				for(String languageAllowedForRule: languagesAllowedForRule.split("#")) {	
    					Language languageAllowed = Language.findByFieldName(Language.class, "type", languageAllowedForRule, domain.getLocale());
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
    				return true;
    			} logger.error("**** Custom Parameter 'RULE_LANGUAGES_ALLOWED' is set with empty value. ****");
    			model.addAttribute("errorcode", "rule_languages_allowed_notset");
    			return false;
    		} logger.error("**** Custom Parameter 'RULE_LANGUAGES_ALLOWED' is set with null value. ****");
			model.addAttribute("errorcode", "rule_languages_allowed_notset");
			return false;
    	} else{
			logger.error("**** Custom Parameter 'RULE_LANGUAGES_ALLOWED' is not set. ****");
			model.addAttribute("errorcode", "rule_languages_allowed_notset");
			return false;
		}		
	}
	
	private boolean populateContentDraftsForRule(ModelMap model, Rule domain, String locale) {
		CustomParameter languagesAllowedForRuleParameter = CustomParameter.findByName(CustomParameter.class, "RULE_LANGUAGES_ALLOWED", "");
    	if(languagesAllowedForRuleParameter!=null) {    		
    		String languagesAllowedForRule = languagesAllowedForRuleParameter.getValue();
    		if(languagesAllowedForRule!=null) {
    			if(!languagesAllowedForRule.isEmpty()) {
    				List<Language> languagesAllowedForContentDraft = new ArrayList<Language>();
    				for(String languageAllowedForRule: languagesAllowedForRule.split("#")) {	
    					Language languageAllowed = Language.findByFieldName(Language.class, "type", languageAllowedForRule, domain.getLocale());
    					languagesAllowedForContentDraft.add(languageAllowed);
    				}
    				List<TextDraft> contentDrafts = new ArrayList<TextDraft>();
    				if(domain.getContentDrafts()!=null && !domain.getContentDrafts().isEmpty()) {				
    					contentDrafts.addAll(domain.getContentDrafts());
    					for(TextDraft contentDraft: domain.getContentDrafts()) {
    						languagesAllowedForContentDraft.remove(contentDraft.getLanguage());					
    					}				
    				}
    				if(!languagesAllowedForContentDraft.isEmpty()) {								
    					for(Language languageAllowedForContentDraft: languagesAllowedForContentDraft) {
    						TextDraft contentDraft = new TextDraft();
    						contentDraft.setLanguage(languageAllowedForContentDraft);
    						contentDraft.setText("");
    						contentDrafts.add(contentDraft);
    					}
    				}
    				model.addAttribute("contentDrafts",contentDrafts);
    				return true;
    			} logger.error("**** Custom Parameter 'RULE_LANGUAGES_ALLOWED' is set with empty value. ****");
    			model.addAttribute("errorcode", "rule_languages_allowed_notset");
    			return false;
    		} logger.error("**** Custom Parameter 'RULE_LANGUAGES_ALLOWED' is set with null value. ****");
			model.addAttribute("errorcode", "rule_languages_allowed_notset");
			return false;
    	} else{
			logger.error("**** Custom Parameter 'RULE_LANGUAGES_ALLOWED' is not set. ****");
			model.addAttribute("errorcode", "rule_languages_allowed_notset");
			return false;
		}		
	}
	
	private void customValidateCommon(Rule domain, BindingResult result,
			HttpServletRequest request) {
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
		}
		if(domain.getEditionYear()==null){
			result.rejectValue("year","YearEmpty","Edition Year is not set.");
		}
		if(domain.getNumber()==null || domain.getNumber().isEmpty()){
			result.rejectValue("number","NumberEmpty","Number is not set.");
		}
		/**** title validation ****/
		CustomParameter languagesAllowedForRuleParameter = CustomParameter.findByName(CustomParameter.class, "RULE_LANGUAGES_ALLOWED", "");
		String languagesAllowedForRule = languagesAllowedForRuleParameter.getValue();
		boolean isTitleInAtleastOneLanguage = false;			
		for(String languageAllowedForRule: languagesAllowedForRule.split("#")) {
			String titleTextInThisLanguage = request.getParameter("title_text_"+languageAllowedForRule);
			if(titleTextInThisLanguage!=null && !titleTextInThisLanguage.isEmpty()) {
				isTitleInAtleastOneLanguage = true;
				break;
			}
		}
		if(isTitleInAtleastOneLanguage==false) {
			result.rejectValue("version","TitleEmpty","Please enter the title in atleast one language.");
		}
		/**** contentDraft validation ****/
		boolean isContentDraftInAtleastOneLanguage = false;			
		for(String languageAllowedForRule: languagesAllowedForRule.split("#")) {
			String contentDraftTextInThisLanguage = request.getParameter("contentDraft_text_"+languageAllowedForRule);
			if(contentDraftTextInThisLanguage!=null && !contentDraftTextInThisLanguage.isEmpty()) {
				isContentDraftInAtleastOneLanguage = true;
				break;
			}
		}
		if(isContentDraftInAtleastOneLanguage==false) {
			result.rejectValue("version","ContentDraftEmpty","Please enter the content draft in atleast one language.");
		}
	}
	
	private List<TextDraft> updateDraftsOfGivenType(Rule domain, String typeOfDraft, HttpServletRequest request) {
		List<TextDraft> draftsOfGivenType = new ArrayList<TextDraft>();
		CustomParameter languagesAllowedForRuleParameter = CustomParameter.findByName(CustomParameter.class, "RULE_LANGUAGES_ALLOWED", "");
		String languagesAllowedForRule = languagesAllowedForRuleParameter.getValue();
		for(String languageAllowedInRule: languagesAllowedForRule.split("#")) {
			String draftTextInThisLanguage = request.getParameter(typeOfDraft+"_text_"+languageAllowedInRule);
			if(draftTextInThisLanguage!=null && !draftTextInThisLanguage.isEmpty()) {
				TextDraft draftOfGivenType = null;				
				String draftIdInThisLanguage = request.getParameter(typeOfDraft+"_id_"+languageAllowedInRule);
				if(draftIdInThisLanguage!=null && !draftIdInThisLanguage.isEmpty()) {
					draftOfGivenType = TextDraft.findById(TextDraft.class, Long.parseLong(draftIdInThisLanguage));					
				} else {
					draftOfGivenType = new TextDraft();
				}
				draftOfGivenType.setText(draftTextInThisLanguage);
				if(draftOfGivenType.getLanguage()==null) {
					Language thisLanguage;
					String draftLanguageId = request.getParameter(typeOfDraft+"_language_id_"+languageAllowedInRule);
					if(draftLanguageId!=null && !draftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(draftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInRule, domain.getLocale());
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
