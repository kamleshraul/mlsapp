package org.mkcl.els.controller.ris;

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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roster/adjournment")
public class AdjournmentController extends GenericController<Adjournment>{

	@Override
	protected void populateNew(final ModelMap model, final Adjournment domain,
			final String locale, final HttpServletRequest request) {
		/**** Adjournment Reasons ****/
		List<AdjournmentReason> reasons=AdjournmentReason.findAll(AdjournmentReason.class,"reason",ApplicationConstants.ASC, locale);
		model.addAttribute("reasons",reasons);
		
		/**** Start Time and End Time ****/
		String strRoster=request.getParameter("roster");
		if(strRoster!=null&&!strRoster.isEmpty()){
			Roster roster=Roster.findById(Roster.class,Long.parseLong(strRoster));
			if(roster.getStartTime()!=null){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT","");
				if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),locale);
				model.addAttribute("startTime",format.format(domain.getStartTime()));
				model.addAttribute("endTime",format.format(domain.getStartTime()));
				}
			}
		}		
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final Adjournment domain,
			final HttpServletRequest request) {
		/**** Adjournment Reasons ****/
		List<AdjournmentReason> reasons=AdjournmentReason.findAll(AdjournmentReason.class,"reason",ApplicationConstants.ASC, domain.getLocale());
		model.addAttribute("reasons",reasons);
		
		/**** Start Time and End Time ****/
		String strRoster=request.getParameter("roster");
		if(strRoster!=null&&!strRoster.isEmpty()){
			Roster roster=Roster.findById(Roster.class,Long.parseLong(strRoster));
			if(roster.getStartTime()!=null){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT","");
				if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				model.addAttribute("startTime",format.format(domain.getStartTime()));
				model.addAttribute("endTime",format.format(domain.getStartTime()));
				}
			}
		}	
	}
}
