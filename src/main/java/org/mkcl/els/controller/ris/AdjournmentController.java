package org.mkcl.els.controller.ris;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Adjournment;
import org.mkcl.els.domain.AdjournmentReason;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Roster;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roster/adjournment")
public class AdjournmentController extends GenericController<Adjournment>{

	@Override
	protected void populateNew(ModelMap model, Adjournment domain,
			String locale, HttpServletRequest request) {
		/**** Locale ****/
		domain.setLocale(locale);
		/**** Adjournment Reasons ****/
		List<AdjournmentReason> reasons=AdjournmentReason.findAll(AdjournmentReason.class,"reason",ApplicationConstants.ASC, locale);
		model.addAttribute("reasons",reasons);
		if(domain.getAdjournmentReason()!=null){
			model.addAttribute("adjournmentReason",domain.getAdjournmentReason().getId());
		}
		/**** Start Time and End Time ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(customParameter!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),locale);
			if(domain.getStartTime()!=null){
				model.addAttribute("startTime",format.format(domain.getStartTime()));
			}
			if(domain.getEndTime()!=null){
				model.addAttribute("endTime",format.format(domain.getEndTime()));
			}
		}
		/**** Roster ****/
		String strRoster=request.getParameter("roster");
		if(strRoster!=null&&!strRoster.isEmpty()){
			Roster roster=Roster.findById(Roster.class,Long.parseLong(strRoster));
			model.addAttribute("roster",roster.getId());
		}
	}

	@Override
	protected void populateEdit(ModelMap model, Adjournment domain,
			HttpServletRequest request) {
		/**** Adjournment Reasons ****/
		List<AdjournmentReason> reasons=AdjournmentReason.findAll(AdjournmentReason.class,"reason",ApplicationConstants.ASC, domain.getLocale());
		model.addAttribute("reasons",reasons);
		if(domain.getAdjournmentReason()!=null){
			model.addAttribute("adjournmentReason",domain.getAdjournmentReason().getId());
		}

		/**** Start Time and End Time ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(customParameter!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
			if(domain.getStartTime()!=null){
				model.addAttribute("startTime",format.format(domain.getStartTime()));
			}
			if(domain.getEndTime()!=null){
				model.addAttribute("endTime",format.format(domain.getEndTime()));
			}
		}	
		/**** Roster ****/
		if(domain.getRoster()!=null){
		model.addAttribute("roster",domain.getRoster().getId());
		}
	}

	@Override
	protected void customValidateCreate(Adjournment domain,
			BindingResult result, HttpServletRequest request) {
		/**** Start Time and End Time****/
		String strStartTime=request.getParameter("selectedStartTime");
		String strEndTime=request.getParameter("selectedEndTime");
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(strStartTime!=null&&!strStartTime.isEmpty()){			
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setStartTime(format.parse(strStartTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setStartTime(defaultFormat.parse(strStartTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(strEndTime!=null&&!strEndTime.isEmpty()){
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setEndTime(format.parse(strEndTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setEndTime(defaultFormat.parse(strEndTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(domain.getStartTime()==null){
			result.rejectValue("startTime","StartTimeEmpty");
		}
		if(domain.getEndTime()==null){
			result.rejectValue("endTime","EndTimeEmpty");
		}
		/**** Action ****/
		if(domain.getAction()==null){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().isEmpty()){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().equals("-")){
			result.rejectValue("action","ActionEmpty");
		}	
		/**** Reason ****/
		if(domain.getAdjournmentReason()==null){
			result.rejectValue("adjournmentReason", "ReasonEmpty");
		}
		/**** Roster ****/
		if(domain.getRoster()==null){
			result.rejectValue("roster","RosterEmpty");
		}
	}

	@Override
	protected void customValidateUpdate(Adjournment domain,
			BindingResult result, HttpServletRequest request) {
		/**** Start Time and End Time****/
		String strStartTime=request.getParameter("selectedStartTime");
		String strEndTime=request.getParameter("selectedEndTime");
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(strStartTime!=null&&!strStartTime.isEmpty()){			
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setStartTime(format.parse(strStartTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setStartTime(defaultFormat.parse(strStartTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(strEndTime!=null&&!strEndTime.isEmpty()){
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setEndTime(format.parse(strEndTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setEndTime(defaultFormat.parse(strEndTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(domain.getStartTime()==null){
			result.rejectValue("startTime","StartTimeEmpty");
		}
		if(domain.getEndTime()==null){
			result.rejectValue("endTime","EndTimeEmpty");
		}
		/**** Action ****/
		if(domain.getAction()==null){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().isEmpty()){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().equals("-")){
			result.rejectValue("action","ActionEmpty");
		}	
		/**** Reason ****/
		if(domain.getAdjournmentReason()==null){
			result.rejectValue("adjournmentReason", "ReasonEmpty");
		}
		/**** Roster ****/
		if(domain.getRoster()==null){
			result.rejectValue("roster","RosterEmpty");
		}
	}

	@Override
	protected void populateAfterCreate(ModelMap model, Adjournment domain,
			HttpServletRequest request) {
		Boolean updateRosterStatus=Roster.generateSlot(domain);
	}

	@Override
	protected void populateAfterUpdate(ModelMap model, Adjournment domain,
			HttpServletRequest request) {
		//Boolean updateRosterStatus=Roster.generateSlot(domain);		
	}
}
