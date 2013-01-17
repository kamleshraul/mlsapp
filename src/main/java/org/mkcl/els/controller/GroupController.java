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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.vo.QuestionDatesVO;
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

import com.sun.xml.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;

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

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, 

javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, final Group domain,
			final String locale, final HttpServletRequest request) {
		domain.setLocale(locale);
		List<Ministry> ministries = Ministry.findAll(Ministry.class, "name", ASC, domain.getLocale());
		model.addAttribute("ministries", ministries);
		populate(model, domain,request);

	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, 

javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, final Group domain, final HttpServletRequest request) {	

		List<Ministry> modelMinistries = new ArrayList<Ministry>();
		List<Ministry> ministries = Ministry.findAll(Ministry.class, "name", ASC, domain.getLocale());
		ministries.removeAll(domain.getMinistries());
		modelMinistries.addAll(domain.getMinistries());
		modelMinistries.addAll(ministries);
		model.addAttribute("ministries", modelMinistries);
		populate(model, domain, request);

	}

	/**
	 * Populate.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 */
	private void populate(final ModelMap model, final Group domain, final HttpServletRequest request) {

		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", "desc", domain.getLocale());
		model.addAttribute("houseTypes", houseTypes);

		String defaultGroupNumber = ((CustomParameter) CustomParameter.findByName(
				CustomParameter.class, "DEFAULT_GROUP_NUMBER", null)).getValue();
		Integer groupNo=Integer.parseInt(defaultGroupNumber);
		model.addAttribute("groupNo",groupNo);

		//ending year will be current year
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		Integer currentYear = Integer.parseInt(df.format(new Date()));	
		List<String> years = new ArrayList<String>();
		//starting year will be 1937 as per analyst Kartik Sir
		for(Integer i=currentYear; i>=1937; i--) {
			years.add(i.toString());
		}
		model.addAttribute("years", years);
		//To populate the sessiontype as per the housetype and year
		List<Session> sessions=new ArrayList<Session>();
		if(domain.getHouseType()!=null){
			sessions=Session.findSessionsByHouseTypeAndYear(domain.getHouseType(), domain.getYear());
		}
		else{
			sessions=Session.findSessionsByHouseTypeAndYear(houseTypes.get(0), currentYear);
		}

		List<SessionType> sessionTypes=new ArrayList<SessionType>();
		if(!sessions.isEmpty()){
			for(Session s:sessions){
				sessionTypes.add(s.getType());
			}
		}
		Set<SessionType> uniqueSessionTypes = new HashSet<SessionType>(sessionTypes);
		sessionTypes = new ArrayList<SessionType>(uniqueSessionTypes);
		model.addAttribute("sessionTypes", sessionTypes);

	}
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final Group domain,
			final BindingResult result, final HttpServletRequest request) {
		Group group=Group.findByNumberHouseTypeSessionTypeYear(domain.getNumber(), domain.getHouseType(), domain.getSessionType(), domain.getYear());
		if(group!=null){
			result.rejectValue("number", "NonUnique", "Group already Exist");           

		}
		customValidateGroupInformation(domain, result, request);

	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(final Group domain,
			final BindingResult result, final HttpServletRequest request) {
		//To check whether the group has Changed
		Group group=Group.findById(Group.class,domain.getId());
		if(!group.getNumber().equals(domain.getNumber()) ||!group.getHouseType().equals(domain.getHouseType())||
				!group.getSessionType().equals(domain.getSessionType())||!group.getYear().equals(domain.getYear())){
			Group duplicateGroup=Group.findByNumberHouseTypeSessionTypeYear(domain.getNumber(), domain.getHouseType(), domain.getSessionType(),domain.getYear());
			if(duplicateGroup!=null){
				result.rejectValue("number", "NonUnique", "Group already Exist");
			}
		}
		customValidateGroupInformation(domain, result, request);
	}

	/**
	 * Custom validate group information.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void customValidateGroupInformation(final Group domain, final BindingResult result,
			final HttpServletRequest request) {
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
		Date sessionStartDate= session.getStartDate();
		Date sessionEndDate=session.getEndDate();
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
		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
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
					if(qd!=null) {
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
					if(qd!=null) {
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
					if(qd!=null) {
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
					if(qd!=null) {
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
					if(qd!=null) {
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
			Calendar submissionDate = Calendar.getInstance();
			submissionDate.setTime(d);
			submissionDate.add(Calendar.DATE, -31);
			Date sDate=submissionDate.getTime();
			sDate = Holiday.getLastWorkingDate(sDate, domain.getLocale());

			Calendar lastReceivingDatesFromDepartment1= Calendar.getInstance();
			lastReceivingDatesFromDepartment1.setTime(d);
			lastReceivingDatesFromDepartment1.add(Calendar.DATE, -6);
			Date lRDateFromDepartment=lastReceivingDatesFromDepartment1.getTime();
			lRDateFromDepartment = Holiday.getLastWorkingDate(lRDateFromDepartment, domain.getLocale());

			Calendar lastSendingDateToDepartment= Calendar.getInstance();
			lastSendingDateToDepartment.setTime(d);
			lastSendingDateToDepartment.add(Calendar.DATE, -24);
			Date lSendingDateToDepartment=lastSendingDateToDepartment.getTime();
			lSendingDateToDepartment = Holiday.getLastWorkingDate(lSendingDateToDepartment, domain.getLocale());

			Calendar yaadiPrintingDate= Calendar.getInstance();
			yaadiPrintingDate.setTime(d);
			yaadiPrintingDate.add(Calendar.DATE, -5);
			Date yPrintingDate =yaadiPrintingDate.getTime();
			yPrintingDate = Holiday.getLastWorkingDate(yPrintingDate, domain.getLocale());

			Calendar yaadiReceivingDate= Calendar.getInstance();
			yaadiReceivingDate.setTime(d);
			yaadiReceivingDate.add(Calendar.DATE, -2);
			Date yReceivingDate=yaadiReceivingDate.getTime();
			yReceivingDate = Holiday.getLastWorkingDate(yReceivingDate, domain.getLocale());

			Calendar suchhiPrintingDate= Calendar.getInstance();
			suchhiPrintingDate.setTime(d);
			suchhiPrintingDate.add(Calendar.DATE,-3);
			Date sPrintingDate=suchhiPrintingDate.getTime();
			sPrintingDate = Holiday.getLastWorkingDate(sPrintingDate, domain.getLocale());

			Calendar suchhiReceivingDate= Calendar.getInstance();
			suchhiReceivingDate.setTime(d);
			suchhiReceivingDate.add(Calendar.DATE,-2);
			Date sReceivingDate=suchhiReceivingDate.getTime();
			sReceivingDate = Holiday.getLastWorkingDate(sReceivingDate, domain.getLocale());

			Calendar suchhiDistributionDate= Calendar.getInstance();
			suchhiDistributionDate.setTime(d);
			suchhiDistributionDate.add(Calendar.DATE, -1);
			Date sDistributionDate=suchhiDistributionDate.getTime();
			sDistributionDate = Holiday.getLastWorkingDate(sDistributionDate, domain.getLocale());

			Calendar speakerSendingDate= Calendar.getInstance();
			speakerSendingDate.setTime(d);
			speakerSendingDate.add(Calendar.DATE, -26);
			Date sSendingDate=speakerSendingDate.getTime();
			sSendingDate = Holiday.getLastWorkingDate(sSendingDate, domain.getLocale());

			QuestionDates qd = domain.findQuestionDatesByGroupAndAnsweringDate(d);
			if(qd!=null){
				if(qd.getFinalSubmissionDate()!=null) {
					submissionDates.add(dateFormat.format(qd.getFinalSubmissionDate()));
				}
				else {
					submissionDates.add("");
				}
				//    			lastSendingDatesToDepartment.add(dateFormat.format(qd.getLastSendingDateToDepartment()));
				//    			lastReceivingDatesFromDepartment.add(dateFormat.format(qd.getLastReceivingDateFromDepartment()));
				//    			yaadiPrintingDates.add(dateFormat.format(qd.getYaadiPrintingDate()));
				//    			yaadiReceivingDates.add(dateFormat.format(qd.getYaadiReceivingDate()));
				//    			suchhiPrintingDates.add(dateFormat.format(qd.getSuchhiPrintingDate()));
				//    			suchhiReceivingDates.add(dateFormat.format(qd.getSuchhiReceivingDate()));
				//    			suchhiDistributionDates.add(dateFormat.format(qd.getSuchhiDistributionDate()));
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
				submissionDates.add(dateFormat.format(sDate));
				lastReceivingDatesFromDepartment.add(dateFormat.format(lRDateFromDepartment));
				lastSendingDatesToDepartment.add(dateFormat.format(lSendingDateToDepartment));
				yaadiPrintingDates.add(dateFormat.format(yPrintingDate));
				yaadiReceivingDates.add(dateFormat.format(yReceivingDate));
				suchhiPrintingDates.add(dateFormat.format(sPrintingDate));
				suchhiReceivingDates.add(dateFormat.format(sReceivingDate));
				suchhiDistributionDates.add(dateFormat.format(sDistributionDate));
				speakerSendingDates.add(dateFormat.format(sSendingDate));
				//    			lastSendingDatesToDepartment.add("");
				//    			lastReceivingDatesFromDepartment.add("");
				//    			yaadiPrintingDates.add("");
				//    			yaadiReceivingDates.add("");
				//    			suchhiPrintingDates.add("");
				//    			suchhiReceivingDates.add("");
				//    			suchhiDistributionDates.add("");
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
