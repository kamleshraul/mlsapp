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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.GroupInformation;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.PartialUpdate;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.jdbc.core.SqlOutParameter;
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
@RequestMapping("/groupinformation")
public class GroupInformationController extends GenericController<GroupInformation> {
    
    /** The Constant ASC. */
    private static final String ASC = "asc";
    
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model, final GroupInformation domain,
		final String locale, final HttpServletRequest request) {
	domain.setLocale(locale);
	String defaultHouseName = ((CustomParameter) CustomParameter.findByName(
		CustomParameter.class, "DEFAULT_HOUSETYPE", locale)).getValue();
	HouseType defaultHouseType = (HouseType) HouseType.findByFieldName(
			HouseType.class, "name", defaultHouseName, locale);
	domain.setHouseType(defaultHouseType);
	model.addAttribute("defaultHouseTypeId", defaultHouseType.getId());
	List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", "desc", domain.getLocale());
	model.addAttribute("houseTypes", houseTypes);	
	List<Ministry> ministries = Ministry.findAll(Ministry.class, "name", ASC, domain.getLocale());
	model.addAttribute("ministries", ministries);
	populate(model, domain,request);	
    }
    
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateEdit(final ModelMap model, final GroupInformation domain, final HttpServletRequest request) {	
	List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", "desc", domain.getLocale());
	String defaultHouseName = ((CustomParameter) CustomParameter.findByName(
			CustomParameter.class, "DEFAULT_HOUSETYPE", domain.getLocale())).getValue();
	houseTypes.remove((HouseType) HouseType.findByFieldName(
			HouseType.class, "name", defaultHouseName, domain.getLocale()));
	model.addAttribute("houseTypes", houseTypes);
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
    private void populate(final ModelMap model, final GroupInformation domain, final HttpServletRequest request) {
	List<Group> groups = Group.findAll(Group.class, "name", ASC, domain.getLocale());
	model.addAttribute("groups", groups);
	
	List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ASC, domain.getLocale());
	model.addAttribute("sessionTypes", sessionTypes);	
	
	//ending year will be current year
	SimpleDateFormat df = new SimpleDateFormat("yyyy");
	Integer currentYear = Integer.parseInt(df.format(new Date()));	
	List<String> years = new ArrayList<String>();
	//starting year will be 1937 as per analyst Kartik Sir
	for(Integer i=currentYear; i>=1937; i--) {
		years.add(i.toString());
	}
	model.addAttribute("years", years);		
    }    
    
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final GroupInformation domain,
		final BindingResult result, final HttpServletRequest request) {
    	customValidateGroupInformation(domain, result, request);
    }
    
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateUpdate(final GroupInformation domain,
		final BindingResult result, final HttpServletRequest request) {
    	customValidateGroupInformation(domain, result, request);
    }
    
    /**
     * Custom validate group information.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    private void customValidateGroupInformation(final GroupInformation domain, final BindingResult result,
		final HttpServletRequest request) {
    	// Check for version mismatch
    	if (domain.isVersionMismatch()) {
    		result.rejectValue("VersionMismatch", "version");
    	}
    	
    	// Check for duplicate instance
    	
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
		GroupInformation domain = GroupInformation.findById(GroupInformation.class, id);
    	Session session =Session.findSessionByYearAndSessionType(domain.getYear(), domain.getSessionType(), domain.getLocale());
    	Date sessionStartDate= session.getStartDate();
    	Date sessionEndDate=session.getEndDate();
    	Calendar start = Calendar.getInstance();
    	start.setTime(sessionStartDate);    	
    	Calendar end = Calendar.getInstance();
    	end.setTime(sessionEndDate);
    	List<Date> answeringDates=new ArrayList<Date>();
    	List<String> aDates=new ArrayList<String>();
    	List<String> submissionDates=new ArrayList<String>();
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
    	    switch(domain.getGroup().getName())
    	    {
    	    case 1:
    	    	
    	    	if(sf.format(current).equals("Monday")){
    	    		answeringDates.add(current);
    	    		aDates.add(dateFormat.format(current));
    	    		QuestionDates qd = QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
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
    	    		QuestionDates qd = QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
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
    	    		QuestionDates qd = QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
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
    	    		QuestionDates qd = QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
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
    	    		QuestionDates qd = QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", current, domain.getLocale());
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
    		QuestionDates qd=QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", d, domain.getLocale());
    		if(qd!=null){
    			submissionDates.add(dateFormat.format(qd.getFinalSubmissionDate()));
    		}
    		else{
    			submissionDates.add(dateFormat.format(sDate));
    		}
    		
    	} 
    	
    	model.addAttribute("submissionDates",submissionDates);
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
	public String updateRotationOrder(@Valid @ModelAttribute("domain") GroupInformation domain,
			final BindingResult result, final ModelMap model,
			final RedirectAttributes redirectAttributes,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String messagePattern=servletPath.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", servletPath);
		domain=GroupInformation.findById(GroupInformation.class,domain.getId());
		List<QuestionDates> questionDates=new ArrayList<QuestionDates>();
		int dateCount=Integer.parseInt(request.getParameter("dateCount"));
		SimpleDateFormat sf=null;
		if(domain.getLocale().equals("mr_IN")){
			sf=new SimpleDateFormat("dd/MM/yyyy",new Locale("hi","IN"));
		}
		else{
			sf=new SimpleDateFormat("dd/MM/yyyy",new Locale(domain.getLocale()));
		}
			
		
		Date answeringDate=new Date();
		Date submissionDate=new Date();
		for(int i=0;i<dateCount;i++){
			if(request.getParameter("date"+i)!=null){				
				if(request.getParameter("date"+i).equals("true")){
					String aDate=request.getParameter("answeringDate"+i);
					try {
					 answeringDate=sf.parse(aDate);
					} catch (ParseException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String sDate=request.getParameter("submissionDate"+i);
					try {
						 submissionDate=sf.parse(sDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					QuestionDates qd = QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", answeringDate, domain.getLocale());
					if(qd!=null){
						qd.setAnsweringDate(answeringDate);
						qd.setFinalSubmissionDate(submissionDate);
						questionDates.add(qd);
					}
					else{
						QuestionDates questionDate= new QuestionDates();
						questionDate.setAnsweringDate(answeringDate);
						questionDate.setFinalSubmissionDate(submissionDate);
						questionDate.setLocale(domain.getLocale());
						questionDates.add(questionDate);
					}
						
					
				}
				
			}
			else{
				String aDate=request.getParameter("answeringDate"+i);
				try {
				 answeringDate=sf.parse(aDate);
				} catch (ParseException e) {
						e.printStackTrace();
				}
				QuestionDates qd = QuestionDates.findByFieldName(QuestionDates.class, "answeringDate", answeringDate, domain.getLocale());
				if(qd!=null){
					qd.remove();
				}
			}

		}
		if(!questionDates.isEmpty()){
			domain.setQuestionDates(questionDates);					
			((GroupInformation) domain).merge();
		}
		model.addAttribute("domain", domain);	
		redirectAttributes.addFlashAttribute("type", "success");
		redirectAttributes.addFlashAttribute("msg", "update_success");
		String returnUrl = "redirect:/" + servletPath + "/"
		+ ((GroupInformation) domain).getId() + "/edit";
		return returnUrl;
	}

}
