package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DocumentLink;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documentlink")
public class DocumentLinkController extends GenericController<DocumentLink>{
	
    @Override
    protected void populateNew(final ModelMap model, final DocumentLink domain,
            final String locale, final HttpServletRequest request) {
       /*** HouseType ***/
       List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", ApplicationConstants.ASC, locale);
       model.addAttribute("houseTypes", houseTypes);
       
       /***Session Type ***/
       List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale);
       model.addAttribute("sessionTypes", sessionTypes);
       
       /*** Session year ***/
       List<Integer> sessionYears = new ArrayList<Integer>();
	   Integer latestYear = new GregorianCalendar().get(Calendar.YEAR);
	   CustomParameter houseFormationYear = 
				CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		if(houseFormationYear != null) {
			Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
			for(int i = latestYear; i >= formationYear; i--) {
				sessionYears.add(i);
			}
		}
		model.addAttribute("sessionYears", sessionYears);
		domain.setLocale(locale);
        
    }
    
	@Override
	protected void populateEdit(final ModelMap model, final DocumentLink domain,
	            final HttpServletRequest request) {
			String locale = domain.getLocale();
			
			/*** HouseType ***/
	       List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", ApplicationConstants.ASC, locale);
	       model.addAttribute("houseTypes", houseTypes);
	       
	       /***Session Type ***/
	       List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale);
	       model.addAttribute("sessionTypes", sessionTypes);
	       
	       /*** Session year ***/
	       List<Integer> sessionYears = new ArrayList<Integer>();
		   Integer latestYear = new GregorianCalendar().get(Calendar.YEAR);
		   CustomParameter houseFormationYear = 
					CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			if(houseFormationYear != null) {
				Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
				for(int i = latestYear; i >= formationYear; i--) {
					sessionYears.add(i);
				}
			}
			model.addAttribute("sessionYears", sessionYears);
			model.addAttribute("locale", domain.getLocale());
			model.addAttribute("selectedSessionType", domain.getSession().getType().getId());
			model.addAttribute("selectedHouseType", domain.getSession().getHouse().getType().getId());
			model.addAttribute("selectedSessionYear", domain.getSession().getYear());
	}
	
	@Override
	protected void preValidateCreate(final DocumentLink domain,
            final BindingResult result, 
            final HttpServletRequest request) {
		String strHouseTypeId = request.getParameter("houseType");
		String strSessionTypeId = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		if(strHouseTypeId != null && !strHouseTypeId.isEmpty()
			&& strSessionTypeId != null && !strSessionTypeId.isEmpty()
			&& strSessionYear != null && !strSessionYear.isEmpty()){
			HouseType houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseTypeId));
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionTypeId));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			try {
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				if(session != null){
					domain.setSession(session);
				}
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}

    }
	
    protected void preValidateUpdate(final DocumentLink domain,
            final BindingResult result, 
            final HttpServletRequest request) {
    	String strHouseTypeId = request.getParameter("houseType");
		String strSessionTypeId = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		if(strHouseTypeId != null && !strHouseTypeId.isEmpty()
			&& strSessionTypeId != null && !strSessionTypeId.isEmpty()
			&& strSessionYear != null && !strSessionYear.isEmpty()){
			HouseType houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseTypeId));
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionTypeId));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			try {
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				if(session != null){
					domain.setSession(session);
				}
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}
    }
    
    @Override
    protected void customValidateCreate(final DocumentLink domain,
            final BindingResult result, 
            final HttpServletRequest request) {
    	
    }
    
    @Override
    protected void customValidateUpdate(final DocumentLink domain,
            final BindingResult result, 
            final HttpServletRequest request) {
    	
    }

}
