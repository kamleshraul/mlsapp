/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.GroupInformationController.java
 * Created On: Jun 29, 2012
 */
package org.mkcl.els.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// TODO: Auto-generated Javadoc
/**
 * The Class GroupInformationController.
 *
 * @author Anand
 * @author Dhananjay
 * @since v1.0.0
 */
@Controller
@RequestMapping("/group")
public class GroupController extends GenericController<Group> {

	/** The Constant ASC. */
	private static final String ASC = "asc";
	
	@Override
	protected void populateModule(final ModelMap model, final HttpServletRequest request, final String locale, final AuthUser currentUser) {
		
		/**** House Types allowed for Current User ****/
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		String houseType=this.getCurrentUser().getHouseType();
		if(houseType != null) {
			if(!houseType.isEmpty()) {
				if(houseType.equals("bothhouse")){
					houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);			
				}else {
					houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"type",ApplicationConstants.ASC, locale);
				}
				model.addAttribute("houseTypes", houseTypes);		
				model.addAttribute("selectedHouseType",houseTypes.get(0).getType());
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode","userhousetypenotset");				
			}			
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode","userhousetypenotset");			
		}
		
		/**** To check whether sessions exist for selected house type  ****/					
		Session lastSessionCreated=Session.findLatestSession(houseTypes.get(0));	

		if(lastSessionCreated.getId()!=null){
			
			/**** Session Types ****/
			List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
			model.addAttribute("sessionTypes",sessionTypes);
			if(lastSessionCreated.getType() != null) {
				model.addAttribute("selectedSessionType",lastSessionCreated.getType().getType());				
			}
			
			/**** Years ****/
			List<MasterVO> years = new ArrayList<MasterVO>();
			
			//set upper limit for years available
			Integer latestYear=lastSessionCreated.getYear();
			if(latestYear == null) {
				latestYear = new GregorianCalendar().get(Calendar.YEAR); //set as current year in case latest session has no year set.
			}				
			
			//starting year must be set as custom parameter 'HOUSE_FORMATION_YEAR'
			CustomParameter houseFormationYearParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "HOUSE_FORMATION_YEAR", "");
			
			if(houseFormationYearParameter != null) {
				if(!houseFormationYearParameter.getValue().isEmpty()) {
					Integer houseFormationYear;
					try {
						houseFormationYear = Integer.parseInt(houseFormationYearParameter.getValue());
						for(Integer i=latestYear; i>=houseFormationYear; i--) {
							MasterVO year = new MasterVO();
							year.setName(FormaterUtil.formatNumberNoGrouping(i, locale));
							year.setValue(i.toString());							
							years.add(year);
						}
						model.addAttribute("years", years);
						if(lastSessionCreated.getYear() != null) {
							if(model.get("errorcode") == null)
								model.addAttribute("selectedYear", latestYear.toString());
						}
					}
					catch(NumberFormatException ne) {
						if(model.get("errorcode") == null)
							model.addAttribute("errorcode","houseformationyearsetincorrect");						
					}				
				}
				else {
					if(model.get("errorcode") == null)
						model.addAttribute("errorcode","houseformationyearnotset");					
				}
			}	
			else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode","houseformationyearnotset");				
			}		
						
		}else{
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode","nosessionentriesfound");			
		}						
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, 

javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, final Group domain,
			final String locale, final HttpServletRequest request) {
		
		/**** Locale ****/
		domain.setLocale(locale);
		
		/**** Selected House Type ****/
		HouseType selectedHouseType = null;
		String hType = request.getParameter("houseType");		
		if(hType != null) {
			if(!hType.isEmpty()) {				
				selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", hType, locale);
				if(selectedHouseType != null) {
					model.addAttribute("houseType", selectedHouseType.getId());
					model.addAttribute("formattedHouseType",selectedHouseType.getName());
				} else {
					if(model.get("errorcode") == null)
						model.addAttribute("errorcode", "houseType_isincorrect");
				}
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode", "houseType_isempty");
			}
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode", "houseType_isnull");
		}
		
		/**** Selected Session Type ****/
		SessionType selectedSessionType = null;
		String sType = request.getParameter("sessionType");			
		if(sType != null) {
			if(!sType.isEmpty()) {
				selectedSessionType = SessionType.findByFieldName(SessionType.class, "type", sType, locale);
				if(selectedSessionType != null) {
					model.addAttribute("sessionType", selectedSessionType.getId());
					model.addAttribute("formattedSessionType",selectedSessionType.getSessionType());
				} else {
					if(model.get("errorcode") == null)
						model.addAttribute("errorcode", "sessionType_isincorrect");
				}
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode", "sessionType_isempty");
			}
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode", "sessionType_isnull");
		}
				
		
		/**** Selected Year ****/
		Integer selectedYear = null;
		String sYear = request.getParameter("year");		
		if(sYear != null) {
			if(!sYear.isEmpty()) {
				try{
					selectedYear = Integer.parseInt(sYear);
					if(selectedYear != null) {
						model.addAttribute("year", selectedYear);
						String formattedYear = FormaterUtil.formatNumberNoGrouping(selectedYear, locale);
						model.addAttribute("formattedYear", formattedYear);
					}
				} catch (NumberFormatException ne) {
					if(model.get("errorcode") == null)
						model.addAttribute("errorcode", "year_isincorrect");
				}
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode", "year_isempty");
			}
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode", "year_isnull");
		}	
		
		populate(model, domain, request, locale, selectedHouseType, selectedSessionType, selectedYear);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, 

javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, final Group domain, final HttpServletRequest request) {	

		/**** Selected House Type ****/					
		HouseType selectedHouseType = domain.getHouseType();
		if(selectedHouseType != null) {
			model.addAttribute("houseType", selectedHouseType.getId());
			model.addAttribute("formattedHouseType",selectedHouseType.getName());
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode", "houseType_isnull");
		}
			
		
		/**** Selected Session Type ****/
		SessionType selectedSessionType = domain.getSessionType();
		if(selectedSessionType != null) {
			model.addAttribute("sessionType", selectedSessionType.getId());
			model.addAttribute("formattedSessionType",selectedSessionType.getSessionType());
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode", "sessionType_isnull");
		}
		
		/**** Selected Year ****/		
		Integer selectedYear = domain.getYear();
		if(selectedYear != null) {
			model.addAttribute("year", selectedYear);
			String formattedYear = FormaterUtil.formatNumberNoGrouping(selectedYear, domain.getLocale());
			model.addAttribute("formattedYear", formattedYear);
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode", "year_isnull");
		}
		
		populate(model, domain, request, domain.getLocale(), domain.getHouseType(), domain.getSessionType(), domain.getYear());
	}

	/**
	 * Populate.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 */
	private void populate(final ModelMap model, final Group domain, final HttpServletRequest request, String locale, HouseType houseType, SessionType sessionType, Integer year) {	
		//upper limit of group numbers allowed must be set as custom parameter 'DEFAULT_GROUP_NUMBER'
		String groupNumberLimitParameter = ((CustomParameter) CustomParameter.findByName(CustomParameter.class, "DEFAULT_GROUP_NUMBER", "")).getValue();
		
		if(groupNumberLimitParameter != null) {
			if(!groupNumberLimitParameter.isEmpty()) {
				try{
					Integer groupNumberLimit=Integer.parseInt(groupNumberLimitParameter);
					model.addAttribute("groupNumberLimit",groupNumberLimit);
					
					/**** Group Numbers ****/
					List<MasterVO> groupNumbers = new ArrayList<MasterVO>();
					
					//to exclude other group numbers whose groups already exist. 					
					List<Integer> groupNumbersOfExistingGroups = new ArrayList<Integer>();					
					if(domain.getNumber()!=null) {
						groupNumbersOfExistingGroups = Group.findGroupNumbersForSessionExcludingGivenGroup(houseType, sessionType, year, domain.getNumber(), locale);
					} else {
						groupNumbersOfExistingGroups = Group.findGroupNumbersForSession(houseType, sessionType, year, locale);
					}
					
					for(Integer i=1; i<=groupNumberLimit; i++) {
						
						if(!groupNumbersOfExistingGroups.contains(i)) {
							MasterVO groupNumber = new MasterVO();
							groupNumber.setName(FormaterUtil.formatNumberNoGrouping(i, locale));
							groupNumber.setValue(i.toString());							
							groupNumbers.add(groupNumber);
						}					
																		
					}
					if(groupNumbers.isEmpty()) {
						if(model.get("errorcode") == null)
							model.addAttribute("errorcode","allgroupssetforsession");						
					}
					model.addAttribute("groupNumbers", groupNumbers);
					if(domain.getNumber() != null) {
						model.addAttribute("selectedNumber", domain.getNumber().toString());
					}
					
				} catch(NumberFormatException ne) {
					if(model.get("errorcode") == null)
						model.addAttribute("errorcode","defaultgroupnumbersetincorrect");					
				}
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode","nodefaultgroupnumberfound");				
			}
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode","nodefaultgroupnumberfound");			
		}		
		
		/**** Ministries ****/		
		List<Ministry> ministries = Ministry.findAll(Ministry.class, "name", ASC, domain.getLocale());
		
		//to exclude ministries of other existing groups. 	
		List<Ministry> ministriesOfOtherGroupsInSameSession = new ArrayList<Ministry>();
		if(domain.getMinistries() != null) {
			if(domain.getNumber() != null) {
				ministriesOfOtherGroupsInSameSession = Group.findMinistriesInGroupsForSessionExcludingGivenGroup(houseType, sessionType, year, domain.getNumber(), locale);
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode","groupnumbernotset");				
			}
		} else {
			ministriesOfOtherGroupsInSameSession = Group.findMinistriesInGroupsForSession(houseType, sessionType, year, locale);
		}
		if(!ministriesOfOtherGroupsInSameSession.isEmpty()) {
			ministries.removeAll(ministriesOfOtherGroupsInSameSession);				
		}
		
		List<Ministry> modelMinistries = new ArrayList<Ministry>();	
		
		if(domain.getMinistries() != null) {
			if(!domain.getMinistries().isEmpty()) {
				ministries.removeAll(domain.getMinistries());
				modelMinistries.addAll(domain.getMinistries());
			}
		}
		
		modelMinistries.addAll(ministries);
		if(modelMinistries.isEmpty()) {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode","incorrectdefaultgroupnumberset");			
		}
		model.addAttribute("ministries", modelMinistries);
		model.addAttribute("selectedMinistries", domain.getMinistries());
	}	
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final Group domain, final BindingResult result, final HttpServletRequest request) {
		if(domain.getNumber() != null) {
			Group group=Group.findByNumberHouseTypeSessionTypeYear(domain.getNumber(), domain.getHouseType(), domain.getSessionType(), domain.getYear());
			if(group!=null){
				result.rejectValue("number", "NonUnique.groupNumber", "Group already Exist");
			}			
		} else {
			result.rejectValue("number", "NotNull.groupNumber", "Please select group number");
		}
		customValidateCommon(domain, result, request);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(final Group domain, final BindingResult result, final HttpServletRequest request) {
		if(domain.getNumber() != null) {
			//To check whether the group has Changed
			Group group=Group.findById(Group.class,domain.getId());
			if(!group.getNumber().equals(domain.getNumber()) ||!group.getHouseType().equals(domain.getHouseType())||
					!group.getSessionType().equals(domain.getSessionType())||!group.getYear().equals(domain.getYear())){
				Group duplicateGroup=Group.findByNumberHouseTypeSessionTypeYear(domain.getNumber(), domain.getHouseType(), domain.getSessionType(),domain.getYear());
				if(duplicateGroup!=null){
					result.rejectValue("number", "NonUnique.groupNumber", "Group already Exist");
				}
			}
		} else {
			result.rejectValue("number", "NotNull.groupNumber", "Please select group number");
		}
		customValidateCommon(domain, result, request);
	}

	/**
	 * Custom validate group information.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void customValidateCommon(final Group domain, final BindingResult result,	final HttpServletRequest request) {
		if(domain.getMinistries() != null) {
			if(domain.getMinistries().size() == 0) {
				result.rejectValue("ministries", "NotEmpty.ministries", "Please select ministries");
			}
		} else {
			result.rejectValue("ministries", "NotNull.ministries", "Please select ministries");
		}

		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}

	/**
     * Assign rotation order.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @SuppressWarnings("unused")
    @RequestMapping(value="/rotationorder/{id}/edit", method=RequestMethod.GET)
    private String assignRotationOrder(final @PathVariable("id") Long id,final ModelMap model,final HttpServletRequest request){
    	final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String urlPattern=servletPath.split("\\/edit")[0].replace("/"+id,"");
		String messagePattern=urlPattern.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", urlPattern);
		Group domain = Group.findById(Group.class, id);
    	Session session =Session.findSessionByHouseTypeSessionTypeYear(domain.getHouseType(), domain.getSessionType(), domain.getYear());
    	if(session == null) {
			model.addAttribute("errorcode", "sessionnotfoundforgroup");    			
			return urlPattern.replace("rotationorder","error");
		}
    	Date sessionStartDate= session.getStartDate();
    	if(sessionStartDate == null) {
			model.addAttribute("errorcode", "sessionstartdatenotset");    			
			return urlPattern.replace("rotationorder","error");
		}
    	Date sessionEndDate=session.getEndDate();
    	if(sessionEndDate == null) {
			model.addAttribute("errorcode", "sessionenddatenotset");    			
			return urlPattern.replace("rotationorder","error");
		}
    	Calendar start = Calendar.getInstance();
    	start.setTime(sessionStartDate);    	
    	Calendar end = Calendar.getInstance();
    	end.setTime(sessionEndDate);
    	List<Date> answeringDates=new ArrayList<Date>();
    	List<String> aDates=new ArrayList<String>();
    	List<String> submissionDates=new ArrayList<String>();
    	List<String> lastSendingDatesToDepartment=new ArrayList<String>();
    	List<String> lastReceivingDatesFromDepartment=new ArrayList<String>();
    	List<String> yaadiPrintingDates=new ArrayList<String>();
    	List<String> yaadiReceivingDates=new ArrayList<String>();
    	List<String> suchhiPrintingDates=new ArrayList<String>();
    	List<String> suchhiReceivingDates=new ArrayList<String>();
    	List<String> suchhiDistributionDates=new ArrayList<String>();
    	List<String> speakerSendingDates=new ArrayList<String>();
    	List<String> selects = new ArrayList<String>();
    	SimpleDateFormat sf=new SimpleDateFormat("EEEE");
    	CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
    	if(parameter == null) {
			model.addAttribute("errorcode", "server_dateformat_notset");    			
			return urlPattern.replace("rotationorder","error");
		} 
    	if(parameter.getValue()==null) {
    		model.addAttribute("errorcode", "server_dateformat_notset");    			
			return urlPattern.replace("rotationorder","error");
		}
    	if(parameter.getValue().isEmpty()) {
    		model.addAttribute("errorcode", "server_dateformat_notset");    			
			return urlPattern.replace("rotationorder","error");
		}
    	SimpleDateFormat dateFormat=null;
    	if(domain.getLocale().equals("mr_IN")){
    		dateFormat=new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
		}
		else{
			dateFormat=new SimpleDateFormat(parameter.getValue(),new Locale(domain.getLocale()));
		}
		dateFormat.setLenient(true);
		
    	for (; !start.after(end); start.add(Calendar.DATE, 1)) {
    	    Date current = start.getTime();
    	    String select="false";	    
    	    switch(domain.getNumber())
    	    {
	    	    case 1:
	    	    	
	    	    	if(sf.format(current).equals("Monday")){
	    	    		answeringDates.add(current);
	    	    		aDates.add(dateFormat.format(current));
	    	    		QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(current);//QuestionDates.findByFieldName (QuestionDates.class, "answeringDate", current, domain.getLocale());
	    	    		if(qd!=null || (!Holiday.isHolidayOnDate(current, domain.getLocale()))) {
	    	    			select = "true";
	    	    		} 	
	    	    		selects.add(select);
	    	    	}
	    	    	break;
	    	    	
	    	    case 2:
	    	    	
	    	    	if(sf.format(current).equals("Tuesday")){
	    	    		answeringDates.add(current);
	    	    		aDates.add(dateFormat.format(current));
	    	    		QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(current);//QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
	    	    		if(qd!=null || (!Holiday.isHolidayOnDate(current, domain.getLocale()))) {
	    	    			select = "true";
	    	    		}   
	    	    		selects.add(select);
	       	    	}
	    	    	break;
	    	    	
	    	    case 3:
	    	    	
	    	    	if(sf.format(current).equals("Wednesday")){
	    	    		answeringDates.add(current);
	    	    		aDates.add(dateFormat.format(current));
	    	    		QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(current);//QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
	    	    		if(qd!=null || (!Holiday.isHolidayOnDate(current, domain.getLocale()))) {
	    	    			select = "true";
	    	    		} 
	    	    		selects.add(select);
	       	    	}
	    	    	break;
	    	    	
	    	    case 4:
	    	    	
	    	    	if(sf.format(current).equals("Thursday")){
	    	    		answeringDates.add(current);
	    	    		aDates.add(dateFormat.format(current));
	    	    		QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(current);//QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
	    	    		if(qd!=null || (!Holiday.isHolidayOnDate(current, domain.getLocale()))) {
	    	    			select = "true";
	    	    		}   
	    	    		selects.add(select);
	    	    	}
	    	    	break;
	    	    	
	    	    case 5:
	    	    	
	    	    	if(sf.format(current).equals("Friday")){ 
	    	    		answeringDates.add(current);
	    	    		aDates.add(dateFormat.format(current));
	    	    		QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(current);//QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
	    	    		if(qd!=null || (!Holiday.isHolidayOnDate(current, domain.getLocale()))) {
	    	    			select = "true";
	    	    		}  
	    	    		selects.add(select);
	       	    	}
	    	    	break;  	
    	    
    	    }    	    
    	}    	
    	model.addAttribute("answeringDates", aDates);
    	model.addAttribute("selects", selects);
    	for(Date d:answeringDates){
    		Calendar dateField = Calendar.getInstance();
    		int difference;
    		Date submissionDate = null;
    		Date lastReceivingDateFromDepartment = null;
    		Date lastSendingDateToDepartment = null;
    		Date yaadiPrintingDate = null;
    		Date yaadiReceivingDate = null;
    		Date suchhiPrintingDate = null;
    		Date suchhiReceivingDate = null;
    		Date suchhiDistributionDate = null;
    		Date speakerSendingDate = null;
    		
    		if(session.getParameter("questions_starred_finalSubmissionDate_difference") != null) {
	    		difference = Integer.parseInt(session.getParameter("questions_starred_finalSubmissionDate_difference"));
	    		submissionDate = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");    			
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_lastReceivingDateFromDepartment_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_lastReceivingDateFromDepartment_difference"));
        		lastReceivingDateFromDepartment = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_lastSendingDateToDepartment_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_lastSendingDateToDepartment_difference"));
    			lastSendingDateToDepartment = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_yaadiPrintingDate_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_yaadiPrintingDate_difference"));
    			yaadiPrintingDate = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_yaadiReceivingDate_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_yaadiReceivingDate_difference"));
    			yaadiReceivingDate = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_suchhiPrintingDate_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_suchhiPrintingDate_difference"));
    			suchhiPrintingDate = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_suchhiReceivingDate_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_suchhiReceivingDate_difference"));
    			suchhiReceivingDate = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_suchhiDistributionDate_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_suchhiDistributionDate_difference"));
    			suchhiDistributionDate = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}
    		
    		if(session.getParameter("questions_starred_speakerSendingDate_difference") != null) {
    			difference = Integer.parseInt(session.getParameter("questions_starred_speakerSendingDate_difference"));
    			speakerSendingDate = Holiday.getLastWorkingDateFrom(dateField, d, difference, domain.getLocale());
    		} else {
    			model.addAttribute("errorcode", "sessionparametersnotset");
    			return urlPattern.replace("rotationorder","error");
    		}    		
    		
    		QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(d);
    		if(qd!=null){
    			if(qd.getFinalSubmissionDate()!=null) {
    				submissionDates.add(dateFormat.format(qd.getFinalSubmissionDate()));
    			}
    			else {
    				submissionDates.add("");
    			}

    			if(qd.getLastSendingDateToDepartment()!=null) {
    				lastSendingDatesToDepartment.add(dateFormat.format(qd.getLastSendingDateToDepartment()));
    			}
    			else {
    				lastSendingDatesToDepartment.add("");
    			}
    			if(qd.getLastReceivingDateFromDepartment()!=null) {
    				lastReceivingDatesFromDepartment.add(dateFormat.format(qd.getLastReceivingDateFromDepartment()));
    			}
    			else {
    				lastReceivingDatesFromDepartment.add("");
    			}
    			if(qd.getYaadiPrintingDate()!=null) {
    				yaadiPrintingDates.add(dateFormat.format(qd.getYaadiPrintingDate()));
    			}
    			else {
    				yaadiPrintingDates.add("");
    			}
    			if(qd.getYaadiReceivingDate()!=null) {
    				yaadiReceivingDates.add(dateFormat.format(qd.getYaadiReceivingDate()));
    			}
    			else {
    				yaadiReceivingDates.add("");
    			}
    			if(qd.getSuchhiPrintingDate()!=null) {
    				suchhiPrintingDates.add(dateFormat.format(qd.getSuchhiPrintingDate()));
    			}
    			else {
    				suchhiPrintingDates.add("");
    			}
    			if(qd.getSuchhiReceivingDate()!=null) {
    				suchhiReceivingDates.add(dateFormat.format(qd.getSuchhiReceivingDate()));
    			}
    			else {
    				suchhiReceivingDates.add("");
    			}
    			if(qd.getSuchhiDistributionDate()!=null) {
    				suchhiDistributionDates.add(dateFormat.format(qd.getSuchhiDistributionDate()));
    			}
    			else {
    				suchhiDistributionDates.add("");
    			}    
    			if(qd.getSpeakerSendingDate()!=null) {
    				speakerSendingDates.add(dateFormat.format(qd.getSpeakerSendingDate()));
    			}
    			else {
    				speakerSendingDates.add("");
    			}    
    		}
    		else{
    			if(submissionDate != null) {
    				submissionDates.add(dateFormat.format(submissionDate));
    			} else {
    				submissionDates.add("");
    			}
    			if(lastReceivingDateFromDepartment != null) {
    				lastReceivingDatesFromDepartment.add(dateFormat.format(lastReceivingDateFromDepartment));
    			} else {
    				lastReceivingDatesFromDepartment.add("");
    			}
    			
    			if(lastSendingDateToDepartment != null) {
    				lastSendingDatesToDepartment.add(dateFormat.format(lastSendingDateToDepartment));
    			} else {
    				lastSendingDatesToDepartment.add("");
    			}
    			
    			if(yaadiPrintingDate != null) {
    				yaadiPrintingDates.add(dateFormat.format(yaadiPrintingDate));
    			} else {
    				yaadiPrintingDates.add("");
    			}
    			
    			if(yaadiReceivingDate != null) {
    				yaadiReceivingDates.add(dateFormat.format(yaadiReceivingDate));
    			} else {
    				yaadiReceivingDates.add("");
    			}
    			
    			if(suchhiPrintingDate != null) {
    				suchhiPrintingDates.add(dateFormat.format(suchhiPrintingDate));
    			} else {
    				suchhiPrintingDates.add("");
    			}
    			
    			if(suchhiReceivingDate != null) {
    				suchhiReceivingDates.add(dateFormat.format(suchhiReceivingDate));
    			} else {
    				suchhiReceivingDates.add("");
    			}
    			
    			if(suchhiDistributionDate != null) {
    				suchhiDistributionDates.add(dateFormat.format(suchhiDistributionDate));
    			} else {
    				suchhiDistributionDates.add("");
    			}
    			
    			if(speakerSendingDate != null) {
    				speakerSendingDates.add(dateFormat.format(speakerSendingDate));
    			} else {
    				speakerSendingDates.add("");
    			}    			
    		}    		
    	}    	
    	model.addAttribute("submissionDates",submissionDates);
    	model.addAttribute("lastSendingDatesToDepartment",lastSendingDatesToDepartment);
    	model.addAttribute("lastReceivingDatesFromDepartment",lastReceivingDatesFromDepartment);
    	model.addAttribute("yaadiPrintingDates",yaadiPrintingDates);
    	model.addAttribute("yaadiReceivingDates",yaadiReceivingDates);
    	model.addAttribute("suchhiPrintingDates",suchhiPrintingDates);
    	model.addAttribute("suchhiReceivingDates",suchhiReceivingDates);
    	model.addAttribute("suchhiDistributionDates",suchhiDistributionDates);
    	model.addAttribute("speakerSendingDates",speakerSendingDates);
	    model.addAttribute("dateCount",answeringDates.size());
	    model.addAttribute("domain", domain);
		return urlPattern+"/"+"edit";
    }


	/**
	 * Update rotation order.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param model the model
	 * @param redirectAttributes the redirect attributes
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value="/rotationorder",method = RequestMethod.POST)
	public String updateRotationOrder(@Valid @ModelAttribute("domain") Group domain,
			final BindingResult result, final ModelMap model,
			final RedirectAttributes redirectAttributes,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String messagePattern=servletPath.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", servletPath);
		domain=Group.findById(Group.class,domain.getId());
		List<QuestionDates> questionDates=new ArrayList<QuestionDates>();
		int dateCount=Integer.parseInt(request.getParameter("dateCount"));
		SimpleDateFormat sf=null;
		if(domain.getLocale().equals("mr_IN")){
			sf=new SimpleDateFormat("dd/MM/yyyy",new Locale("hi","IN"));
		}
		else{
			sf=new SimpleDateFormat("dd/MM/yyyy",new Locale(domain.getLocale()));
		}		
		List<QuestionDates> questionDatesToRemove = new ArrayList<QuestionDates>();
		for(int i=0;i<dateCount;i++){
			Date answeringDate=null;
			Date submissionDate=null;
			Date lastSendingDateToDepartment=null;
			Date lastReceivingDateFromDepartment=null;
			Date yaadiPrintingDate=null;
			Date yaadiReceivingDate=null;
			Date suchhiPrintingDate=null;
			Date suchhiReceivingDate=null;
			Date suchhiDistributionDate=null;
			Date speakerSendingDate=null;
			if(request.getParameter("date"+i)!=null){				
				if(request.getParameter("date"+i).equals("true")){
					String aDate=request.getParameter("answeringDate"+i);
					try {
						if(aDate!=null){
							if(!aDate.isEmpty()){
								answeringDate=sf.parse(aDate);
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String sDate=request.getParameter("submissionDate"+i);
					try {
						if(sDate!=null){
							if(!sDate.isEmpty()){
								submissionDate=sf.parse(sDate);
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strLastSendingDateToDepartment=request.getParameter("lastSendingDateToDepartment"+i);
					try {
						if(strLastSendingDateToDepartment!=null){
							if(!strLastSendingDateToDepartment.isEmpty())
								lastSendingDateToDepartment=sf.parse(strLastSendingDateToDepartment);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strLastReceivingDateFromDepartment=request.getParameter("lastReceivingDateFromDepartment"+i);
					try {
						if(strLastReceivingDateFromDepartment!=null){
							if(!strLastReceivingDateFromDepartment.isEmpty())
								lastReceivingDateFromDepartment=sf.parse(strLastReceivingDateFromDepartment);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strYaadiPrintingDate=request.getParameter("yaadiPrintingDate"+i);
					try {
						if(strYaadiPrintingDate!=null){
							if(!strYaadiPrintingDate.isEmpty())
								yaadiPrintingDate=sf.parse(strYaadiPrintingDate);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strYaadiReceivingDate=request.getParameter("yaadiReceivingDate"+i);
					try {
						if(strYaadiReceivingDate!=null){
							if(!strYaadiReceivingDate.isEmpty())
								yaadiReceivingDate=sf.parse(strYaadiReceivingDate);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strSuchhiPrintingDate=request.getParameter("suchhiPrintingDate"+i);
					try {
						if(strSuchhiPrintingDate!=null){
							if(!strSuchhiPrintingDate.isEmpty())
								suchhiPrintingDate=sf.parse(strSuchhiPrintingDate);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strSuchhiReceivingDate=request.getParameter("suchhiReceivingDate"+i);
					try {
						if(strSuchhiReceivingDate!=null){
							if(!strSuchhiReceivingDate.isEmpty())
								suchhiReceivingDate=sf.parse(strSuchhiReceivingDate);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strSuchhiDistributionDate=request.getParameter("suchhiDistributionDate"+i);
					try {
						if(strSuchhiDistributionDate!=null){
							if(!strSuchhiDistributionDate.isEmpty())
								suchhiDistributionDate=sf.parse(strSuchhiDistributionDate);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strSpeakerSendingDate=request.getParameter("speakerSendingDate"+i);
					try {
						if(strSpeakerSendingDate!=null){
							if(!strSpeakerSendingDate.isEmpty())
								speakerSendingDate=sf.parse(strSpeakerSendingDate);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					QuestionDates questionDate= new QuestionDates();
					QuestionDates qd= domain.findQuestionDatesByGroupAndAnsweringDate( answeringDate);					
					if(qd!=null){
						questionDate=qd;
					}					
					questionDate.setAnsweringDate(answeringDate);
					questionDate.setFinalSubmissionDate(submissionDate);
					questionDate.setLastSendingDateToDepartment(lastSendingDateToDepartment);
					questionDate.setLastReceivingDateFromDepartment(lastReceivingDateFromDepartment);
					questionDate.setYaadiPrintingDate(yaadiPrintingDate);
					questionDate.setYaadiReceivingDate(yaadiReceivingDate);
					questionDate.setSuchhiPrintingDate(suchhiPrintingDate);
					questionDate.setSuchhiReceivingDate(suchhiReceivingDate);
					questionDate.setSuchhiDistributionDate(suchhiDistributionDate);
					questionDate.setSpeakerSendingDate(speakerSendingDate);
					questionDate.setLocale(domain.getLocale());
					questionDates.add(questionDate);					
				}				
			}
			else{
				String aDate=request.getParameter("answeringDate"+i);
				try {
					answeringDate=sf.parse(aDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				QuestionDates qd= domain.findQuestionDatesByGroupAndAnsweringDate( answeringDate);				
				if(qd!=null){
					questionDatesToRemove.add(qd);
				}
			}

		}
		if(!questionDates.isEmpty()){
			domain.setQuestionDates(questionDates);					
			((Group) domain).merge();
			for(QuestionDates qdr : questionDatesToRemove) {
				qdr.remove();
			}
		}
		model.addAttribute("domain", domain);	
		redirectAttributes.addFlashAttribute("type", "success");
		redirectAttributes.addFlashAttribute("msg", "update_success");
		String returnUrl = "redirect:/" + servletPath + "/"
		+ ((Group) domain).getId() + "/edit";
		return returnUrl;
	}   

}
