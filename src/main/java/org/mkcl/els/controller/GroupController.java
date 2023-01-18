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

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
	@Override
	protected void populateModule(final ModelMap model, 
			final HttpServletRequest request, 
			final String locale, 
			final AuthUser currentUser) {
		
		/**** House Types allowed for Current User ****/
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		String houseType = this.getCurrentUser().getHouseType();
		if(houseType != null) {
			if(!houseType.isEmpty()) {
				if(houseType.equals("bothhouse")){
					houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);			
				}else {
					houseTypes = HouseType.
							findAllByFieldName(HouseType.class, "type",houseType,"type",ApplicationConstants.ASC, locale);
				}
				model.addAttribute("houseTypes", houseTypes);		
				model.addAttribute("selectedHouseType", houseTypes.get(0).getType());
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode", "userhousetypenotset");				
			}			
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode", "userhousetypenotset");			
		}
		
		/**** To check whether sessions exist for selected house type  ****/					
		Session lastSessionCreated;
		try {
			lastSessionCreated = Session.findLatestSession(houseTypes.get(0));
			
			if(lastSessionCreated.getId()!=null){
				
				/**** Session Types ****/
				List<SessionType> sessionTypes = SessionType.
						findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
				model.addAttribute("sessionTypes", sessionTypes);
				if(lastSessionCreated.getType() != null) {
					model.addAttribute("selectedSessionType", lastSessionCreated.getType().getType());				
				}
				
				/**** Years ****/
				List<MasterVO> years = new ArrayList<MasterVO>();
				
				//set upper limit for years available
				Integer latestYear = lastSessionCreated.getYear();
				if(latestYear == null) {
					latestYear = new GregorianCalendar().get(Calendar.YEAR); //set as current year in case latest session has no year set.
				}				
				
				//starting year must be set as custom parameter 'HOUSE_FORMATION_YEAR'
				CustomParameter houseFormationYearParameter = CustomParameter.
						findByFieldName(CustomParameter.class, "name", "HOUSE_FORMATION_YEAR", "");
				
				if(houseFormationYearParameter != null) {
					if(!houseFormationYearParameter.getValue().isEmpty()) {
						Integer houseFormationYear;
						try {
							houseFormationYear = Integer.parseInt(houseFormationYearParameter.getValue());
							for(Integer i = latestYear; i >= houseFormationYear; i--) {
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
								model.addAttribute("errorcode", "houseformationyearsetincorrect");						
						}				
					}
					else {
						if(model.get("errorcode") == null)
							model.addAttribute("errorcode", "houseformationyearnotset");					
					}
				}	
				else {
					if(model.get("errorcode") == null)
						model.addAttribute("errorcode", "houseformationyearnotset");				
				}		
							
			}else{
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode", "nosessionentriesfound");			
			}
			
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		} catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, rquest may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}						
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, 

javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, 
			final Group domain,
			final String locale, 
			final HttpServletRequest request) {
		
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
	protected void populateEdit(final ModelMap model, 
			final Group domain, 
			final HttpServletRequest request) {	

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
	private void populate(final ModelMap model, 
			final Group domain, 
			final HttpServletRequest request, 
			final String locale, 
			final HouseType houseType, 
			final SessionType sessionType, 
			final Integer year) {	
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
					if(model.get("errorcode") == null){
						model.addAttribute("errorcode","defaultgroupnumbersetincorrect");
					}
				}catch (ELSException e) {
					model.addAttribute("errorcode","defaultgroupnumbersetincorrect");
				}catch (Exception e) {
					String message = e.getMessage();
					
					if(message == null){
						message = "There is some problem, request may not complete successfully.";
					}
					model.addAttribute("error", message);
					e.printStackTrace();
				}
			} else {
				if(model.get("errorcode") == null)
					model.addAttribute("errorcode","nodefaultgroupnumberfound");				
			}
		} else {
			if(model.get("errorcode") == null)
				model.addAttribute("errorcode","nodefaultgroupnumberfound");			
		}		
		
		try{
			/**** Ministries ****/	
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			List<Ministry> ministries = new ArrayList<Ministry>();
			if(DateUtil.compareDatePartOnly(new Date(), session.getEndDate())<=0) {
				ministries = Ministry.findAssignedMinistriesInSession(session.getStartDate(), locale);
			} else {
				ministries = Ministry.findAll(Ministry.class, "name", ApplicationConstants.ASC, locale);
			}			
			
//			//to exclude ministries of other existing groups. 	
//			List<Ministry> ministriesOfOtherGroupsInSameSession = new ArrayList<Ministry>();
//			if(domain.getMinistries() != null) {
//				if(domain.getNumber() != null) {
//					ministriesOfOtherGroupsInSameSession = Group.findMinistriesInGroupsForSessionExcludingGivenGroup(houseType, sessionType, year, domain.getNumber(), locale);
//				} else {
//					if(model.get("errorcode") == null)
//						model.addAttribute("errorcode","groupnumbernotset");				
//				}
//			} else {
//				ministriesOfOtherGroupsInSameSession = Group.findMinistriesInGroupsForSession(houseType, sessionType, year, locale);
//			}
//			if(!ministriesOfOtherGroupsInSameSession.isEmpty()) {
//				ministries.removeAll(ministriesOfOtherGroupsInSameSession);				
//			}
//			
//			List<Ministry> modelMinistries = new ArrayList<Ministry>();	
//			
//			if(domain.getMinistries() != null) {
//				if(!domain.getMinistries().isEmpty()) {
//					ministries.removeAll(domain.getMinistries());
//					modelMinistries.addAll(domain.getMinistries());
//				}
//			}
			
//			modelMinistries.addAll(ministries);
//			if(modelMinistries.isEmpty()) {
//				if(model.get("errorcode") == null)
//					model.addAttribute("errorcode","incorrectdefaultgroupnumberset");			
//			}
			model.addAttribute("ministries", ministries);
			model.addAttribute("selectedMinistries", domain.getMinistries());
			model.addAttribute("session",session.getId());
			
			
			List<SubDepartment> subdepartments = new ArrayList<SubDepartment>();
			if(domain.getMinistries()!=null && !domain.getMinistries().isEmpty()){
				List<Ministry> ministryList = domain.getMinistries();
				for(Ministry m : ministryList){
					Date onDate = session.getEndDate();
					if(onDate.before(new Date())) {
						onDate = new Date();
					}
					List<SubDepartment> selectedSubdepartments = MemberMinister.findAssignedSubDepartments(m, onDate, locale);
					subdepartments.addAll(selectedSubdepartments);
				}
			}
			
			model.addAttribute("subdepartments", subdepartments);
			model.addAttribute("selectedSubdepartments", domain.getSubdepartments());
				
			
			
		}catch (ELSException e) {
			logger.error(e.getMessage());
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}	
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final Group domain, 
			final BindingResult result, 
			final HttpServletRequest request) {
		try{
			if(domain.getNumber() != null) {
				Group group=Group.findByNumberHouseTypeSessionTypeYear(domain.getNumber(), domain.getHouseType(), domain.getSessionType(), domain.getYear());
				if(group!=null){
					result.rejectValue("number", "NonUnique.groupNumber", "Group already Exist");
				}			
			} else {
				result.rejectValue("number", "NotNull.groupNumber", "Please select group number");
			}
			customValidateCommon(domain, result, request);
		}catch (ELSException e) {
			logger.error(e.getMessage());
			//result.reject("error", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			//result.reject("error", message);
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(final Group domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		try{
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
		}catch (ELSException e) {
			//result.reject("error", e.getParameter());
			logger.error(e.getParameter());
			e.printStackTrace();
		}catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			logger.error(message);
			//result.reject("error", message);
			
			e.printStackTrace();
		}
	}

	/**
	 * Custom validate group information.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void customValidateCommon(final Group domain, 
			final BindingResult result,	
			final HttpServletRequest request) {
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
    private String assignRotationOrder(final @PathVariable("id") Long id,
    		final ModelMap model,
    		final HttpServletRequest request){
    	final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String urlPattern=servletPath.split("\\/edit")[0].replace("/"+id,"");
		String messagePattern=urlPattern.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", urlPattern);
		Group domain = Group.findById(Group.class, id);
    	Session session;
		try {
			session = Session.findSessionByHouseTypeSessionTypeYear(domain.getHouseType(), domain.getSessionType(), domain.getYear());
		
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
	    	List<String> lastDatesForChangingDepartment=new ArrayList<String>();
	    	List<String> yaadiPrintingDates=new ArrayList<String>();
	    	List<String> yaadiReceivingDates=new ArrayList<String>();
	    	List<String> suchhiPrintingDates=new ArrayList<String>();
	    	List<String> suchhiReceivingDates=new ArrayList<String>();
	    	List<String> suchhiDistributionDates=new ArrayList<String>();
	    	List<String> speakerSendingDates=new ArrayList<String>();
	    	List<String> selects = new ArrayList<String>();
	    	List<String> displayAnsweringDates = new ArrayList<String>(); 	
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
	    	SimpleDateFormat dateFormat = FormaterUtil.getDateFormatter(parameter.getValue(), domain.getLocale());
			dateFormat.setLenient(true);
			boolean isRotationOrderSet = domain.isRotationOrderSet();
	    	for (; !start.after(end); start.add(Calendar.DATE, 1)) {
	    	    Date current = start.getTime();
	    	    String select="false";	  
	    	    QuestionDates existingQuestionDate = null;
	    	    switch(domain.getNumber())
	    	    {
		    	    case 1:
		    	    	
		    	    	existingQuestionDate = domain.findQuestionDatesByGroupAndAnsweringDate(current);
		    	    	if(existingQuestionDate!=null) {
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		if(existingQuestionDate.getDisplayAnsweringDate() != null ) {
		    	    		displayAnsweringDates.add(dateFormat.format(existingQuestionDate.getDisplayAnsweringDate()));
		    	    		}else {
		    	    			displayAnsweringDates.add(dateFormat.format(current));
		    	    		}
		    	    		select = "true";
		    	    		selects.add(select);
		    	    	} else if(sf.format(current).equals("Monday")){
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		displayAnsweringDates.add(dateFormat.format(current));
		    	    		if(isRotationOrderSet==false && !Holiday.isHolidayOnDate(current, domain.getLocale())) {
		    	    			select = "true";
		    	    		}
		    	    		selects.add(select);
		    	    	}
		    	    	break;
		    	    	
		    	    case 2:
		    	    	
		    	    	existingQuestionDate = domain.findQuestionDatesByGroupAndAnsweringDate(current);
		    	    	if(existingQuestionDate!=null) {
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		if(existingQuestionDate.getDisplayAnsweringDate() != null ) {
			    	    		displayAnsweringDates.add(dateFormat.format(existingQuestionDate.getDisplayAnsweringDate()));
			    	    		}else {
			    	    			displayAnsweringDates.add(dateFormat.format(current));
			    	    		}
		    	    		select = "true";
		    	    		selects.add(select);
		    	    	} else if(sf.format(current).equals("Tuesday")){
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		displayAnsweringDates.add(dateFormat.format(current));
		    	    		if(isRotationOrderSet==false && !Holiday.isHolidayOnDate(current, domain.getLocale())) {
		    	    			select = "true";
		    	    		}
		    	    		selects.add(select);
		    	    	}
		    	    	break;
		    	    	
		    	    case 3:
		    	    	
		    	    	existingQuestionDate = domain.findQuestionDatesByGroupAndAnsweringDate(current);
		    	    	if(existingQuestionDate!=null) {
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		if(existingQuestionDate.getDisplayAnsweringDate() != null ) {
			    	    		displayAnsweringDates.add(dateFormat.format(existingQuestionDate.getDisplayAnsweringDate()));
			    	    		}else {
			    	    			displayAnsweringDates.add(dateFormat.format(current));
			    	    		}
		    	    		select = "true";
		    	    		selects.add(select);
		    	    	} else if(sf.format(current).equals("Wednesday")){
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		displayAnsweringDates.add(dateFormat.format(current));
		    	    		if(isRotationOrderSet==false && !Holiday.isHolidayOnDate(current, domain.getLocale())) {
		    	    			select = "true";
		    	    		}
		    	    		selects.add(select);
		    	    	}
		    	    	break;
		    	    	
		    	    case 4:
		    	    	
		    	    	existingQuestionDate = domain.findQuestionDatesByGroupAndAnsweringDate(current);
		    	    	if(existingQuestionDate!=null) {
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		if(existingQuestionDate.getDisplayAnsweringDate() != null ) {
			    	    		displayAnsweringDates.add(dateFormat.format(existingQuestionDate.getDisplayAnsweringDate()));
			    	    		}else {
			    	    			displayAnsweringDates.add(dateFormat.format(current));
			    	    		}
		    	    		select = "true";
		    	    		selects.add(select);
		    	    	} else if(sf.format(current).equals("Thursday")){
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		displayAnsweringDates.add(dateFormat.format(current));
		    	    		if(isRotationOrderSet==false && !Holiday.isHolidayOnDate(current, domain.getLocale())) {
		    	    			select = "true";
		    	    		}
		    	    		selects.add(select);
		    	    	}
		    	    	break;
		    	    	
		    	    case 5:
		    	    	
		    	    	existingQuestionDate = domain.findQuestionDatesByGroupAndAnsweringDate(current);
		    	    	if(existingQuestionDate!=null) {
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		if(existingQuestionDate.getDisplayAnsweringDate() != null ) {
			    	    		displayAnsweringDates.add(dateFormat.format(existingQuestionDate.getDisplayAnsweringDate()));
			    	    		}else {
			    	    			displayAnsweringDates.add(dateFormat.format(current));
			    	    		}
		    	    		select = "true";
		    	    		selects.add(select);
		    	    	} else if(sf.format(current).equals("Friday")){
		    	    		answeringDates.add(current);
		    	    		aDates.add(dateFormat.format(current));
		    	    		displayAnsweringDates.add(dateFormat.format(current));
		    	    		if(isRotationOrderSet==false && !Holiday.isHolidayOnDate(current, domain.getLocale())) {
		    	    			select = "true";
		    	    		}
		    	    		selects.add(select);
		    	    	}
		    	    	break;  	
	    	    
	    	    }       	  
	    	}    	
	    	model.addAttribute("answeringDates", aDates);
	    	model.addAttribute("displayAnsweringDates", displayAnsweringDates);
	    	model.addAttribute("originalAnsweringDates", aDates);
	    	model.addAttribute("selects", selects);
	    	for(Date answeringDate:answeringDates){    		
	    		int difference;
	    		Date submissionDate = null;
	    		Date lastReceivingDateFromDepartment = null;
	    		Date lastSendingDateToDepartment = null;
	    		Date lastDateForChangingDepartment = null;
	    		Date yaadiPrintingDate = null;
	    		Date yaadiReceivingDate = null;
	    		Date suchhiPrintingDate = null;
	    		Date suchhiReceivingDate = null;
	    		Date suchhiDistributionDate = null;
	    		Date speakerSendingDate = null;
	 
	    		
	    		if(session.getParameter("questions_starred_finalSubmissionDate_difference") != null) {
		    		difference = Integer.parseInt(session.getParameter("questions_starred_finalSubmissionDate_difference"));
		    		submissionDate = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");    			
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_lastReceivingDateFromDepartment_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_lastReceivingDateFromDepartment_difference"));
	        		lastReceivingDateFromDepartment = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_lastSendingDateToDepartment_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_lastSendingDateToDepartment_difference"));
	    			lastSendingDateToDepartment = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_lastDateForChangingDepartment_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_lastDateForChangingDepartment_difference"));
	    			if(difference<0) {
	    				difference -=  domain.getNumber() - 1;
	    			} else {
	    				difference +=  domain.getNumber() - 1;
	    			}
	    			lastDateForChangingDepartment = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_yaadiPrintingDate_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_yaadiPrintingDate_difference"));
	    			yaadiPrintingDate = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_yaadiReceivingDate_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_yaadiReceivingDate_difference"));
	    			yaadiReceivingDate = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_suchhiPrintingDate_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_suchhiPrintingDate_difference"));
	    			suchhiPrintingDate = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_suchhiReceivingDate_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_suchhiReceivingDate_difference"));
	    			suchhiReceivingDate = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_suchhiDistributionDate_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_suchhiDistributionDate_difference"));
	    			suchhiDistributionDate = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}
	    		
	    		if(session.getParameter("questions_starred_speakerSendingDate_difference") != null) {
	    			difference = Integer.parseInt(session.getParameter("questions_starred_speakerSendingDate_difference"));
	    			speakerSendingDate = Holiday.getLastWorkingDateFrom(answeringDate, difference, domain.getLocale());
	    		} else {
	    			model.addAttribute("errorcode", "sessionparametersnotset");
	    			return urlPattern.replace("rotationorder","error");
	    		}    		
	    		
	    		QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
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
	    			if(qd.getLastDateForChangingDepartment()!=null) {
	    				lastDatesForChangingDepartment.add(dateFormat.format(qd.getLastDateForChangingDepartment()));
	    			}
	    			else {
	    				lastDatesForChangingDepartment.add("");
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
	    			
	    			if(lastDateForChangingDepartment != null) {
	    				lastDatesForChangingDepartment.add(dateFormat.format(lastDateForChangingDepartment));
	    			} else {
	    				lastDatesForChangingDepartment.add("");
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
	    	model.addAttribute("lastDatesForChangingDepartment",lastDatesForChangingDepartment);
	    	model.addAttribute("yaadiPrintingDates",yaadiPrintingDates);
	    	model.addAttribute("yaadiReceivingDates",yaadiReceivingDates);
	    	model.addAttribute("suchhiPrintingDates",suchhiPrintingDates);
	    	model.addAttribute("suchhiReceivingDates",suchhiReceivingDates);
	    	model.addAttribute("suchhiDistributionDates",suchhiDistributionDates);
	    	model.addAttribute("speakerSendingDates",speakerSendingDates);
		    model.addAttribute("dateCount",answeringDates.size());
		    model.addAttribute("domain", domain);
		    model.addAttribute("houseType", domain.getHouseType().getType());
			
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		} catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, rquest may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
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
    @Transactional
	@RequestMapping(value="/rotationorder",method = RequestMethod.POST)
	public String updateRotationOrder(@Valid @ModelAttribute("domain") Group domain,
			final BindingResult result, 
			final ModelMap model,
			final RedirectAttributes redirectAttributes,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String messagePattern=servletPath.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", servletPath);
		domain=Group.findById(Group.class,domain.getId());
		List<QuestionDates> questionDates=new ArrayList<QuestionDates>();
		int dateCount=Integer.parseInt(request.getParameter("dateCount"));
		SimpleDateFormat sf = FormaterUtil.getDateFormatter("dd/MM/yyyy", domain.getLocale());
		List<QuestionDates> questionDatesToRemove = new ArrayList<QuestionDates>();
		for(int i=0;i<dateCount;i++){
			Date answeringDate=null;
			Date originalAnsweringDate=null;
			Date submissionDate=null;
			Date lastSendingDateToDepartment=null;
			Date lastReceivingDateFromDepartment=null;
			Date lastDateForChangingDepartment=null;
			Date yaadiPrintingDate=null;
			Date yaadiReceivingDate=null;
			Date suchhiPrintingDate=null;
			Date suchhiReceivingDate=null;
			Date suchhiDistributionDate=null;
			Date speakerSendingDate=null;
			Date displayAnsweringDate = null;
			if(request.getParameter("date"+i)!=null){				
				if(request.getParameter("date"+i).equals("true")){
					String aDate=request.getParameter("answeringDate"+i);
					String oDate=request.getParameter("originalAnsweringDate"+i);
					try {
						if(aDate!=null){
							if(!aDate.isEmpty()){
								answeringDate=sf.parse(aDate);
							}
						}
						if(oDate!=null){
							if(!oDate.isEmpty()){
								originalAnsweringDate=sf.parse(oDate);
							}
						}
					} catch (ParseException e) {
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
						e.printStackTrace();
					}
					String strLastSendingDateToDepartment=request.getParameter("lastSendingDateToDepartment"+i);
					try {
						if(strLastSendingDateToDepartment!=null){
							if(!strLastSendingDateToDepartment.isEmpty())
								lastSendingDateToDepartment=sf.parse(strLastSendingDateToDepartment);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strLastReceivingDateFromDepartment=request.getParameter("lastReceivingDateFromDepartment"+i);
					try {
						if(strLastReceivingDateFromDepartment!=null){
							if(!strLastReceivingDateFromDepartment.isEmpty())
								lastReceivingDateFromDepartment=sf.parse(strLastReceivingDateFromDepartment);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strLastDateForChangingDepartment=request.getParameter("lastDateForChangingDepartment"+i);
					try {
						if(strLastDateForChangingDepartment!=null){
							if(!strLastDateForChangingDepartment.isEmpty())
								lastDateForChangingDepartment=sf.parse(strLastDateForChangingDepartment);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strYaadiPrintingDate=request.getParameter("yaadiPrintingDate"+i);
					try {
						if(strYaadiPrintingDate!=null){
							if(!strYaadiPrintingDate.isEmpty())
								yaadiPrintingDate=sf.parse(strYaadiPrintingDate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strYaadiReceivingDate=request.getParameter("yaadiReceivingDate"+i);
					try {
						if(strYaadiReceivingDate!=null){
							if(!strYaadiReceivingDate.isEmpty())
								yaadiReceivingDate=sf.parse(strYaadiReceivingDate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strSuchhiPrintingDate=request.getParameter("suchhiPrintingDate"+i);
					try {
						if(strSuchhiPrintingDate!=null){
							if(!strSuchhiPrintingDate.isEmpty())
								suchhiPrintingDate=sf.parse(strSuchhiPrintingDate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strSuchhiReceivingDate=request.getParameter("suchhiReceivingDate"+i);
					try {
						if(strSuchhiReceivingDate!=null){
							if(!strSuchhiReceivingDate.isEmpty())
								suchhiReceivingDate=sf.parse(strSuchhiReceivingDate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strSuchhiDistributionDate=request.getParameter("suchhiDistributionDate"+i);
					try {
						if(strSuchhiDistributionDate!=null){
							if(!strSuchhiDistributionDate.isEmpty())
								suchhiDistributionDate=sf.parse(strSuchhiDistributionDate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String strSpeakerSendingDate=request.getParameter("speakerSendingDate"+i);
					try {
						if(strSpeakerSendingDate!=null){
							if(!strSpeakerSendingDate.isEmpty())
								speakerSendingDate=sf.parse(strSpeakerSendingDate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String displayAD = request.getParameter("displayAnsweringDates"+i);
					try {
						if(displayAD!=null){
							if(!displayAD.isEmpty())
								displayAnsweringDate=sf.parse(displayAD);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					QuestionDates questionDate= new QuestionDates();
					QuestionDates qd= domain.findQuestionDatesByGroupAndAnsweringDate(originalAnsweringDate);					
					if(qd!=null){
						questionDate=qd;
					}					
					questionDate.setAnsweringDate(answeringDate);
					questionDate.setFinalSubmissionDate(submissionDate);
					questionDate.setLastSendingDateToDepartment(lastSendingDateToDepartment);
					questionDate.setLastReceivingDateFromDepartment(lastReceivingDateFromDepartment);
					questionDate.setLastDateForChangingDepartment(lastDateForChangingDepartment);
					questionDate.setYaadiPrintingDate(yaadiPrintingDate);
					questionDate.setYaadiReceivingDate(yaadiReceivingDate);
					questionDate.setSuchhiPrintingDate(suchhiPrintingDate);
					questionDate.setSuchhiReceivingDate(suchhiReceivingDate);
					questionDate.setSuchhiDistributionDate(suchhiDistributionDate);
					questionDate.setSpeakerSendingDate(speakerSendingDate);
					questionDate.setDisplayAnsweringDate(displayAnsweringDate);
					questionDate.setLocale(domain.getLocale());
					questionDates.add(questionDate);					
				}				
			} else{
				String aDate=request.getParameter("originalAnsweringDate"+i);
				try {
					answeringDate=sf.parse(aDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				QuestionDates qd= domain.findQuestionDatesByGroupAndAnsweringDate(answeringDate);				
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
    
    @Override
	protected void preValidateCreate(final Group domain, 
			final BindingResult result,
			final HttpServletRequest request) {
		this.commonPreValidate(domain, result, request);
	}
	
	@Override
	protected void preValidateUpdate(final Group domain, 
			final BindingResult result,
			final HttpServletRequest request) {
		this.commonPreValidate(domain, result, request);
	}
	
	private void commonPreValidate(final Group domain, 
			final BindingResult result,
			final HttpServletRequest request) {
		// House Type
		HouseType houseType = null;
		String strHouseTypeId = request.getParameter("houseType");
		if(strHouseTypeId != null && ! strHouseTypeId.isEmpty()) {
			Long houseTypeId = Long.parseLong(strHouseTypeId);
			houseType = HouseType.findById(HouseType.class, houseTypeId);
		}
		
		// Session Type
		SessionType sessionType = null;
		String strSessionTypeId = request.getParameter("sessionType");
		if(strSessionTypeId != null && ! strSessionTypeId.isEmpty()) {
			Long sessionTypeId = Long.parseLong(strSessionTypeId);
			sessionType = 
				SessionType.findById(SessionType.class, sessionTypeId);
		}
		
		// Session year
		Integer sessionYear = null;
		String strSessionYear = request.getParameter("year");
		if(strSessionYear != null && ! strSessionYear.isEmpty()) {
			sessionYear = Integer.parseInt(strSessionYear);
		}
		
		try {
			Session session = 
				Session.findSessionByHouseTypeSessionTypeYear(houseType, 
						sessionType, sessionYear);
			domain.setSession(session);
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
	/*@Override
	protected void populateAfterUpdate(final ModelMap model, 
			final Group domain,
			final HttpServletRequest request) {
		try {
			Long groupId = domain.getId();
			Group group = Group.findById(Group.class, groupId);
			
			Anonymous anonymous = Anonymous.create(group);
			Thread thread = new Thread(anonymous);
			thread.run();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	/*
	@Override
	 protected void populateUpdateIfNoErrors(final ModelMap model,
	            final Group domain, 
	            final HttpServletRequest request) {
		 List<SubDepartment> subdepartments = domain.getSubdepartments();
		 domain.setSubdepartments(null);
		 domain.setSubdepartments(subdepartments);
		 
		 
	 }*/
}

class Anonymous implements Runnable {
	
	private Group group;
	
	private Anonymous() {
		
	}
	
	public static Anonymous create(final Group group) {
		Anonymous anonymous = new Anonymous();
		anonymous.group = group;
		return anonymous;
	}

	@Override
	public void run() {
		try {
			Group.reshuffle(group);
		}
		catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
}
