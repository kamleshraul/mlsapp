/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.SessionController.java
 * Created On: Apr 6, 2012
 */
package org.mkcl.els.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The Class SessionController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/session")
public class SessionController extends GenericController<Session> {
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final Session domain,
            final BindingResult result, final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, 

org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateUpdate(final Session domain,
            final BindingResult result, final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * Custom validate.
     *
     * @param sessiondetails the sessiondetails
     * @param result the result
     * @param request the request
     */
    private void customValidate(final Session sessiondetails,
            final BindingResult result, final HttpServletRequest request) {
        if (sessiondetails.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
        if (result.hasErrors()) {
            System.out.println("error");
        }
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, 

javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(	final ModelMap model,
            final Session domain,
            final String locale,
            final HttpServletRequest request) {
        /*
         * setting locale
         */
        domain.setLocale(locale.toString());
        populateSession(model, domain, request, locale);
    }
    @Override
    protected void populateCreateIfNoErrors(final ModelMap model,
            final Session domain, final HttpServletRequest request) {
//    	 CustomParameter parameter = CustomParameter.findByName(
//                 CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
//    	SimpleDateFormat df = new SimpleDateFormat(parameter.getValue());
//    	
//    	try {
//    		domain.setQuestionSubmissionEndDateLH(df.parse((request.getParameter("questionSubmissionEndDateLH"))));
//        	domain.setQuestionSubmissionFirstBatchEndDateUH(df.parse(request.getParameter("questionSubmissionFirstBatchEndDateUH")));
//        	domain.setQuestionSubmissionFirstBatchStartDateUH(df.parse(request.getParameter("questionSubmissionFirstBatchStartDateUH")));
//        	domain.setQuestionSubmissionSecondBatchEndDateUH(df.parse(request.getParameter("questionSubmissionSecondBatchEndDateUH")));
//			domain.setQuestionSubmissionSecondBatchStartDateUH(df.parse(request.getParameter("questionSubmissionSecondBatchStartDateUH")));
//			domain.setQuestionSubmissionStartDateLH(df.parse(request.getParameter("questionSubmissionStartDateLH")));
//    	} catch (ParseException e) {
//			e.printStackTrace();
//		}
    	
    }


    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, 

javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateEdit(final ModelMap model,
            final Session domain,
            final HttpServletRequest request) {
//    	SimpleDateFormat df;
//    	CustomParameter parameter = CustomParameter.findByName(
//                CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
//    	if(domain.getLocale().equals("mr_IN")){
//    		 df = new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
//    	}
//    	else{
//    		 df = new SimpleDateFormat(parameter.getValue(),this.getUserLocale());
//    	}
//   	
//   	if(domain.getQuestionSubmissionEndDateLH()!=null){
//   		model.addAttribute("questionSubmissionEndDateLH",df.format(domain.getQuestionSubmissionEndDateLH()));
//   	}
//   	if(domain.getQuestionSubmissionFirstBatchEndDateUH()!=null){
//   		model.addAttribute("questionSubmissionFirstBatchEndDateUH",df.format(domain.getQuestionSubmissionFirstBatchEndDateUH()));
//   	}
//   	if(domain.getQuestionSubmissionFirstBatchStartDateUH()!=null){
//   		model.addAttribute("questionSubmissionFirstBatchStartDateUH",df.format(domain.getQuestionSubmissionFirstBatchStartDateUH()));
//   	}
//   	if(domain.getQuestionSubmissionSecondBatchEndDateUH()!=null){
//   		model.addAttribute("questionSubmissionSecondBatchEndDateUH",df.format(domain.getQuestionSubmissionSecondBatchEndDateUH()));
//   	}
//   	if(domain.getQuestionSubmissionSecondBatchStartDateUH()!=null){
//   		model.addAttribute("questionSubmissionSecondBatchStartDateUH",df.format(domain.getQuestionSubmissionSecondBatchStartDateUH()));
//   	}
//   	if(domain.getQuestionSubmissionStartDateLH()!=null){
//   		model.addAttribute("questionSubmissionStartDate",df.format(domain.getQuestionSubmissionStartDateLH()));
//   	}
   	  populateSession(model, domain, request, domain.getLocale());
        /*
         * populating enabled device types
         */
        List<DeviceType> deviceTypesEnabled = new ArrayList<DeviceType>();
        String deviceTypesEnabledStr = domain.getDeviceTypesEnabled();
        if(deviceTypesEnabledStr!=null && !deviceTypesEnabledStr.isEmpty()) {
        	for (String deviceTypeEnabledStr : deviceTypesEnabledStr.split(",")) {
        		DeviceType deviceTypeEnabled = DeviceType.findByType(deviceTypeEnabledStr, domain.getLocale());
        		if(deviceTypeEnabled != null) {
        			deviceTypesEnabled.add(deviceTypeEnabled);
        		}
        	}    
        }
        model.addAttribute("deviceTypesEnabled", deviceTypesEnabled);
        
        
//        List<DeviceType> deviceTypesNeedBallot = new ArrayList<DeviceType>();
//        String deviceTypesNeedBallotStr = domain.getDeviceTypesNeedBallot();
//        if(deviceTypesNeedBallotStr!=null && !deviceTypesNeedBallotStr.isEmpty()) {
//        	for (String deviceTypeNeedBallotStr : deviceTypesNeedBallotStr.split(",")) {
//        		DeviceType deviceTypeNeedBallot = DeviceType.findByType(deviceTypeNeedBallotStr, domain.getLocale());
//        		if(deviceTypeNeedBallot != null) {
//        			deviceTypesNeedBallot.add(deviceTypeNeedBallot);
//        		}
//        	}    
//        }
//        model.addAttribute("deviceTypesNeedBallot", deviceTypesNeedBallot);
    }

    private void populateSession(final ModelMap model, final Session domain,
            final HttpServletRequest request, final String locale) {
        /*
         *populating session types
         */
        List<SessionType> sessionTypes = SessionType.findAll(
                SessionType.class, "sessionType", "asc", locale.toString());
        model.addAttribute("sessionType", sessionTypes);
        /*
         * populating session places
         */
        List<SessionPlace> sessionPlace = SessionPlace.findAll(
                SessionPlace.class, "place", "asc", locale.toString());
        model.addAttribute("place",sessionPlace);
        /*
         * populating house types
         */
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "type", "asc", locale.toString());
        model.addAttribute("houseTypes",houseTypes);
        /*
         * populating houses and selected house type
         */
        House selectedHouse=domain.getHouse();
        if(selectedHouse!=null){
            HouseType selectedHouseType=selectedHouse.getType();
            List<House> houses=House.findAllByFieldName(House.class, "type", selectedHouseType,"firstDate",ApplicationConstants.DESC, locale);
            model.addAttribute("houses",houses);
            model.addAttribute("houseTypeSelected", selectedHouseType.getId());
        }else{
            if(houseTypes!=null){
                if(!houseTypes.isEmpty()){
                    HouseType selectedHouseType=houseTypes.get(0);
                    List<House> houses=House.findAllByFieldName(House.class, "type", selectedHouseType,"firstDate",ApplicationConstants.DESC,locale);
                    model.addAttribute("houses",houses);
                    model.addAttribute("houseTypeSelected", selectedHouseType.getId());
                }
            }
        }

        /*
         * populating years
         */
        CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
        List<Integer> years=new ArrayList<Integer>();
        Integer year=new GregorianCalendar().get(Calendar.YEAR);
        if(houseFormationYear!=null){
            Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
            for(int i=year;i>=formationYear;i--){
                years.add(i);
            }
            model.addAttribute("years",years);
        }
        /*
         * setting selected year
         */
        Integer selectedYear=domain.getYear();
        if(selectedYear!=null){
            model.addAttribute("sessionYearSelected",selectedYear);
        }else{
            model.addAttribute("sessionYearSelected",year);
        }
        
        /*
         * populating device types
         */
        List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class, "type", ApplicationConstants.ASC, locale);
        model.addAttribute("deviceTypes", deviceTypes);
        
             
    }

    @Override
    protected void populateUpdateIfNoErrors(final ModelMap model,
            final Session domain, final HttpServletRequest request) {
//    	 CustomParameter parameter = CustomParameter.findByName(
//                 CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
//    	SimpleDateFormat df = new SimpleDateFormat(parameter.getValue());
//    	
//    	try {
//    		domain.setQuestionSubmissionEndDateLH(df.parse((request.getParameter("questionSubmissionEndDateLH"))));
//        	domain.setQuestionSubmissionFirstBatchEndDateUH(df.parse(request.getParameter("questionSubmissionFirstBatchEndDateUH")));
//        	domain.setQuestionSubmissionFirstBatchStartDateUH(df.parse(request.getParameter("questionSubmissionFirstBatchStartDateUH")));
//        	domain.setQuestionSubmissionSecondBatchEndDateUH(df.parse(request.getParameter("questionSubmissionSecondBatchEndDateUH")));
//			domain.setQuestionSubmissionSecondBatchStartDateUH(df.parse(request.getParameter("questionSubmissionSecondBatchStartDateUH")));
//			domain.setQuestionSubmissionStartDateLH(df.parse(request.getParameter("questionSubmissionStartDateLH")));
//    	} catch (ParseException e) {
//			e.printStackTrace();
//		}
//    	
    }
    @RequestMapping(value="/{id}/viewRotationOrder", method = RequestMethod.GET)
    public String viewRotationOrder(final @PathVariable("id") Long id, final ModelMap model,
            final HttpServletRequest request) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String urlPattern=servletPath.split("\\/viewRotationOrder")[0].replace("/"+id,"");
        String messagePattern=urlPattern.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", urlPattern);
        Session domain = Session.findById(Session.class, id);
        model.addAttribute("domain", domain);
        List<Group> groups=Group.findByHouseTypeSessionTypeYear(domain.getHouse().getType(), domain.getType(), domain.getYear());
        model.addAttribute("groups", groups);
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
            request.getSession().removeAttribute("type");
        }
        return urlPattern+"/"+"viewrotationorder";
    }
    
    //---------------------------Added by anand, vikas & dhananjay-------------------------------------
    @RequestMapping(value="{id}/devicetypeconfig", method=RequestMethod.GET)
    public String editSessionDeviceTypeConfig(@PathVariable("id") final Long id,final ModelMap model,
            final HttpServletRequest request ){
    	
    	 final String servletPath = request.getServletPath().replaceFirst("\\/","");
         String urlPattern=servletPath.split("\\/viewRotationOrder")[0].replace("/"+id,"");
         String messagePattern=urlPattern.replaceAll("\\/",".");
         model.addAttribute("messagePattern", messagePattern);
         model.addAttribute("urlPattern", urlPattern);
         model.addAttribute("deviceTypeSelected", request.getParameter("deviceTypeSelected"));
         
         Session domain= Session.findById(Session.class, id);
         List<DeviceType> deviceTypesEnabled = new ArrayList<DeviceType>();
         String deviceTypesEnabledStr = domain.getDeviceTypesEnabled();
         if(deviceTypesEnabledStr!=null && !deviceTypesEnabledStr.isEmpty()) {
        	for (String deviceTypeEnabledStr : deviceTypesEnabledStr.split(",")) {
        		DeviceType deviceTypeEnabled = DeviceType.findByType(deviceTypeEnabledStr, domain.getLocale());
        		if(deviceTypeEnabled != null) {
        			deviceTypesEnabled.add(deviceTypeEnabled);
        			//-------------------dhananjay_23012013------------------------
        			if(domain.getParameters()!=null){
        				List<String> parametersForDeviceType = Session.getParametersSetForDeviceType(domain.getId(), deviceTypeEnabled.getType());
        				if(parametersForDeviceType.isEmpty()){
        					List<CustomParameter> customParameters = CustomParameter.findAllByStartingWith(CustomParameter.class, "name", deviceTypeEnabled.getType()+'%', "name", ApplicationConstants.ASC, null);
    	        			for(CustomParameter i: customParameters) {     
    	        				String key = i.getName().toLowerCase();    	        				
	        					if(i.getValue()!=null && !i.getValue().isEmpty()) {
	        						if(key.endsWith("date")) {
	        							CustomParameter parameter;
										CustomParameter dbParameter;
										if(i.getValue().length()>10){
											 parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
											 dbParameter = CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
										}
										else{
											 parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
											 dbParameter = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
										}		
										SimpleDateFormat dateFormat;
										SimpleDateFormat dbDateFormat;
										Date date;
										
										try {
		
											if (domain.getLocale().equalsIgnoreCase("mr_IN")) {
												
												dateFormat = new SimpleDateFormat(parameter.getValue(), new Locale("hi", "IN"));
												dbDateFormat = new SimpleDateFormat(dbParameter.getValue(), new Locale("hi", "IN"));
												
											} else {
		
												dateFormat = new SimpleDateFormat(parameter.getValue(), new Locale(domain.getLocale()));
												dbDateFormat = new SimpleDateFormat(dbParameter.getValue(), new Locale(domain.getLocale()));
											}
		
											dateFormat.setLenient(true);
		
											date = dbDateFormat.parse(i.getValue());
											
											model.addAttribute(key, dateFormat.format(date)); 										
											
										} catch (ParseException e) {
		
											e.printStackTrace();
										}
	        						} else if(key.endsWith("dates")) {
	        							// add formatting as done for the same type in getParameters() of Session.java
	        						} else {
		        						try {
		        							Integer num = Integer.parseInt(i.getValue());
		        							String value = FormaterUtil.formatNumberNoGrouping(num, domain.getLocale());
		        							model.addAttribute(key, value);
		        						} 
		        						//if parameter value is not a number
		        						catch(NumberFormatException ne) {
		        							model.addAttribute(key, i.getValue());   							
		        						}  
	        						}
	        					}
	        					else {
	        						model.addAttribute(key, "");
	        					}     	        				
    	        			}
        				}
        				else{
        					for(String parameterKey : parametersForDeviceType) {
        						model.addAttribute(parameterKey.toLowerCase(), domain.getParameter(parameterKey));
        					}
        				}
        			}
        			//-----------------------------------------------------------------
        		}
        	}    
        }
        model.addAttribute("deviceTypesEnabled", deviceTypesEnabled);
        model.addAttribute("domain", domain);
        
        //----to show ribbon
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
            request.getSession().removeAttribute("type");
        }
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return urlPattern+"/"+"error";
        }else{
        	return urlPattern ;	
        }
        //----
    }    
   
    //---------------------------Added by anand, vikas & dhananjay-------------------------------------
	@RequestMapping(method= RequestMethod.POST,value="/devicetypeconfig")
    public String updateSessionDeviceTypeConfig(@Valid @ModelAttribute("domain") Session session, BindingResult result,final ModelMap model,
            final HttpServletRequest request, RedirectAttributes redirectAttributes ){ 
		
		Long id = Long.parseLong(request.getParameter("id"));
    	final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String messagePattern=servletPath.replaceAll("\\/",".");
        String returnUrl=servletPath.split("/")[0]+"/"+request.getParameter("id")+"/"+servletPath.split("/")[1];
        
        String deviceTypeSelected = request.getParameter("deviceTypeSelected");
        if(deviceTypeSelected != null){
        	if(deviceTypeSelected.isEmpty()){
        		deviceTypeSelected = "questions_starred";
        	}	        	
        }else if(deviceTypeSelected == null){
        	deviceTypeSelected = "questions_starred";
        }
        
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", servletPath);
        
        Session domain = Session.findById(Session.class, id);
        Map<String, String> parameters;
        
		if(validateSessionDeviceTypeConfig(session, result, model, request, redirectAttributes)){
	        	
	        parameters = domain.getParameters();
	        
	    	@SuppressWarnings("rawtypes")
			Enumeration paramNames = request.getParameterNames();
			if (paramNames != null) {
				while (paramNames.hasMoreElements()) {
					
					String name = (String) paramNames.nextElement();
					if(!(name.equalsIgnoreCase("id")) && !(name.equalsIgnoreCase("version")) && !(name.equalsIgnoreCase("locale")) && !(name.equalsIgnoreCase("deviceTypeSelected"))){
						
						String[] tempValue = request.getParameterValues(name);
						String value= "";
						
						for(int i = 0; i < tempValue.length; i++){
							
							if((i == (tempValue.length - 1))){
								
								if(!tempValue[i].isEmpty()){
									value += tempValue[i];
								}
							}else{
								value += tempValue[i] + "#";
							}
						}
						
						//System.out.println(name+": "+value);
						
						if(value.isEmpty()){
							if(parameters.containsKey(name)){
								parameters.remove(name);
							}
						}
							
						if ((value != null)) {
							if(!(value.isEmpty())){
								parameters.put(name, value);
							}
						}
					}
				}
			}
	    	   	
	    	domain.setParameters(parameters);
	    	domain.merge();
	    	
	    	redirectAttributes.addFlashAttribute("type", "success");
	        //this is done so as to remove the bug due to which update message appears even though there
	        //is a fresh new/edit request i.e after creating/updating records if we click on
	        //new /edit then success message appears
	        request.getSession().setAttribute("type","success");
	        redirectAttributes.addFlashAttribute("msg", "create_success");
	        
	    	returnUrl = "redirect:/" + returnUrl+ "?deviceTypeSelected="+ deviceTypeSelected;
		}else{
			/*
			 * model.addAttribute("type", "error");
			 * model.addAttribute("msg", "create_failed");
			 */
			redirectAttributes.addFlashAttribute("type", "error");
	        //this is done so as to remove the bug due to which update message appears even though there
	        //is a fresh new/edit request i.e after creating/updating records if we click on
	        //new /edit then success message appears
	        request.getSession().setAttribute("type","error");
	        redirectAttributes.addFlashAttribute("msg", "create_failed");
	    	
	    	returnUrl = "redirect:/" + returnUrl+ "?deviceTypeSelected="+ deviceTypeSelected;
		}
    	    	    	    	
    	return returnUrl;    	
    }
	
	public boolean validateSessionDeviceTypeConfig(Session session, BindingResult result,final ModelMap model,
        final HttpServletRequest request, RedirectAttributes redirectAttributes){
		
		boolean returnValue = false;
		
		@SuppressWarnings("rawtypes")
		Enumeration paramNames = request.getParameterNames();
		if (paramNames != null) {
			while (paramNames.hasMoreElements()) {
				
				String name = (String) paramNames.nextElement();
				if(!(name.equalsIgnoreCase("id")) && !(name.equalsIgnoreCase("version")) && !(name.equalsIgnoreCase("locale")) && !(name.equalsIgnoreCase("deviceTypeSelected"))){
					
					String[] tempValue = request.getParameterValues(name);
					for(int i = 0; i < tempValue.length; i++){
						if(!tempValue[i].isEmpty()){
							if(name.toLowerCase().contains("number")){
								if(!name.toLowerCase().endsWith("comparator")){
									try{
										Number num;
										if(tempValue[i].contains(",")){
											num = FormaterUtil.getNumberFormatterNoGrouping(session.getLocale()).parse(tempValue[i].replace(",", ""));
											logger.debug("Session Config Number: "+ num);
											returnValue = true;
										}else{
											num = FormaterUtil.getNumberFormatterNoGrouping(session.getLocale()).parse(tempValue[i]);
											logger.debug("Session Config Number: "+ num);
											returnValue = true;
										}
									}catch(ParseException pe){
										returnValue = false;
										logger.error("Illegal number.");
									}catch(NumberFormatException ne){
										returnValue = false;
										logger.error("Illegal number.");
									}
								}
							}
						}
					}
				}
			}
		}
		
		return returnValue;
	}
}

