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

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.BallotEvent;
import org.mkcl.els.domain.BallotType;
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
    
	
    /**
     * (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void customValidateCreate(final Session domain,
            final BindingResult result, final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain,
     * org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
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

    /**
     *  (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, 
	 * javax.servlet.http.HttpServletRequest)
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
    
    /**
     * (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, 
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateEdit(final ModelMap model,
            final Session domain,
            final HttpServletRequest request) {
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
        
    }

    private void populateSession(final ModelMap model, final Session domain,
            final HttpServletRequest request, final String locale) {
    	try{
	        /*
	         *populating session types
	         */
	        List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", "asc", locale.toString());
	        model.addAttribute("sessionType", sessionTypes);
	        /*
	         * populating session places
	         */
	        List<SessionPlace> sessionPlace = SessionPlace.findAll(SessionPlace.class, "place", "asc", locale.toString());
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
	
	        CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
	        List<String> years=new ArrayList<String>();
	        Integer year=new GregorianCalendar().get(Calendar.YEAR);
	        if(houseFormationYear!=null){
	            Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
	            for(int i=year;i>=formationYear;i--){
	                years.add(FormaterUtil.getNumberFormatterNoGrouping(locale).format(i));
	            }
	            model.addAttribute("years",years);
	        }
	        /*
	         * populating device types
	         */
	        List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class, "type", ApplicationConstants.ASC, locale);
	        model.addAttribute("deviceTypes", deviceTypes);
    	}catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
        
             
    }

   
    /**
     * @param id
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value="/{id}/viewRotationOrder", method = RequestMethod.GET)
    public String viewRotationOrder(final @PathVariable("id") Long id, final ModelMap model,
            final HttpServletRequest request) {
        
    	final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String urlPattern=servletPath.split("\\/viewRotationOrder")[0].replace("/"+id,"");
        String messagePattern=urlPattern.replaceAll("\\/",".");
        
    	try{
	    	
	        model.addAttribute("messagePattern", messagePattern);
	        model.addAttribute("urlPattern", urlPattern);
	        Session domain = Session.findById(Session.class, id);
	        model.addAttribute("domain", domain);
	        List<Group> groups = new ArrayList<Group>();
			try {
				groups = Group.findByHouseTypeSessionTypeYear(domain.getHouse().getType(), domain.getType(), domain.getYear());
			} catch (ELSException e) {
				e.printStackTrace();
				model.addAttribute("error", e.getParameter());
			}
	        model.addAttribute("groups", groups);
	        if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
	        }else{
	            request.getSession().removeAttribute("type");
	        }
    	}catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
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
         if(domain == null) {
        	 model.addAttribute("errorcode", "nosessionfound");    			
  			 return urlPattern.replace("devicetypeconfig","error");        	 
         }
         
         model.addAttribute("houseType", domain.findHouseType());         
         
         List<DeviceType> deviceTypesEnabled = new ArrayList<DeviceType>();
         String deviceTypesEnabledStr = domain.getDeviceTypesEnabled();         
         if(deviceTypesEnabledStr!=null) {        	
        	for (String deviceTypeEnabledStr : deviceTypesEnabledStr.split(",")) {
        		DeviceType deviceTypeEnabled = DeviceType.findByType(deviceTypeEnabledStr, domain.getLocale());
        		if(deviceTypeEnabled != null) {
        			deviceTypesEnabled.add(deviceTypeEnabled);
        			//-------------------dhananjay_23012013------------------------
        			if(domain.getParameters()!=null){
        				List<String> parametersForDeviceType;
						try {
							parametersForDeviceType = Session.getParametersSetForDeviceType(domain.getId(), deviceTypeEnabled.getType());
							
							if(parametersForDeviceType.isEmpty()){
	        					List<CustomParameter> customParameters = CustomParameter.findAllByStartingWith(CustomParameter.class, "name", deviceTypeEnabled.getType()+'%', "name", ApplicationConstants.ASC, "");
	    	        			for(CustomParameter i: customParameters) {     
	    	        				String key = i.getName().toLowerCase();
	    	        				if(key.contains("_lowerhouse")) {
	    	        					if(domain.findHouseType() != null) {
		    	        					if(!domain.findHouseType().equals("lowerhouse")) {
		    	        						continue;
		    	        					}
	    	        					}
	    	        					key = key.replaceAll("_lowerhouse", "");    	        					
	    	        				} else if(key.contains("_upperhouse")) {
	    	        					if(domain.findHouseType() != null) {
		    	        					if(!domain.findHouseType().equals("upperhouse")) {
		    	        						continue;
		    	        					}
	    	        					}
	    	        					key = key.replaceAll("_upperhouse", "");    	        					
	    	        				}
		        					if(i.getValue()!=null) {
		        						if(!i.getValue().isEmpty()) {
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
			        						} 
		        							//for number formatting in comma/hash separated string fields. 
			        						else if((key.equals("resolutions_nonofficial_numberofdaysforfactualpositionreceiving"))) {
			        							String[] numbers = i.getValue().split("#");
			        							String formattedValue = "";
			        							for(int j = 0; j < numbers.length; j++) {		        								
		        									String num;									
		        									num = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(numbers[j]), domain.getLocale());
		        									logger.debug("Session Config Number: "+ num);
		        									if(j == (numbers.length-1)) {
		        										formattedValue += num;
		        									} else {
		        										formattedValue += num + ",";
		        									}	        								
			        							}		        							
			        							model.addAttribute(key, formattedValue);   
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
	        				}
	        				else{
	        					for(String parameterKey : parametersForDeviceType) {
	        						//for number formatting in comma/hash separated string fields. 
	        						if((parameterKey.equals("resolutions_nonofficial_numberOfDaysForFactualPositionReceiving")) || (parameterKey.equals("questions_halfhourdiscussion_standalone_numberOfDaysForFactualPositionReceiving"))) {
	        							String[] numbers = domain.getParameter(parameterKey).split("#");
	        							String formattedValue = "";
	        							for(int j = 0; j < numbers.length; j++) {		        								
	    									String num;									
	    									num = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(numbers[j]), domain.getLocale());
	    									logger.debug("Session Config Number: "+ num);
	    									if(j == (numbers.length-1)) {
	    										formattedValue += num;
	    									} else {
	    										formattedValue += num + ",";
	    									}	        								
	        							}
	        							model.addAttribute(parameterKey.toLowerCase(), formattedValue);
	        						} else {
	        							model.addAttribute(parameterKey.toLowerCase(), domain.getParameter(parameterKey));
	        							System.out.println(parameterKey.toLowerCase()+":"+domain.getParameter(parameterKey));
	        						}        						
	        					}
	        				}
							
						} catch (ELSException e1) {
							model.addAttribute("error", e1.getParameter());
						}catch (Exception e) {
							String message = e.getMessage();
							
							if(message == null){
								message = "There is some problem, request may not complete successfully.";
							}
							model.addAttribute("error", message);
							e.printStackTrace();
						}
        			}
        			//-----------------------------------------------------------------
        		}            	
        	}    
        }
        else {
        	model.addAttribute("errorcode", "nodevicetypesenabled");    			
			return urlPattern.replace("devicetypeconfig","error");
        }
        model.addAttribute("deviceTypesEnabled", deviceTypesEnabled);
        
        List<BallotType> ballotTypes = BallotType.findAll(BallotType.class, "name", ApplicationConstants.ASC, domain.getLocale());
        model.addAttribute("ballotTypes", ballotTypes);        
        
        List<BallotType> ballotEvents = BallotEvent.findAll(BallotEvent.class, "name", ApplicationConstants.ASC, domain.getLocale());
        model.addAttribute("ballotEvents", ballotEvents);
        
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
    public String updateSessionDeviceTypeConfig(@Valid @ModelAttribute("domain") final Session session, final BindingResult result,final ModelMap model,
            final HttpServletRequest request, final RedirectAttributes redirectAttributes ){ 
		
		Long id = Long.parseLong(request.getParameter("id"));
    	final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String messagePattern=servletPath.replaceAll("\\/",".");
        String returnUrl=servletPath.split("/")[0]+"/"+request.getParameter("id")+"/"+servletPath.split("/")[1];
        try{
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
	        
			//to validate validateSessionDeviceTypeConfig(session, result, model, request, redirectAttributes)){
		        	
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
							//System.out.println("Parameter :"+name+" : "+tempValue.toString());
							//for number formatting in comma separated string fields. 
							if((name.equals("resolutions_nonofficial_numberOfDaysForFactualPositionReceiving")) || name.equals("questions_halfhourdiscussion_standalone_numberOfDaysForFactualPositionReceiving")) {
								String[] numbers = tempValue[i].split(",");
								tempValue[i] = "";
								for(int j = 0; j < numbers.length; j++) {
									try{
										Number num;									
										num = FormaterUtil.getNumberFormatterNoGrouping(session.getLocale()).parse(numbers[j]);
										logger.debug("Session Config Number: "+ num);
										if(j == (numbers.length-1)) {
											tempValue[i] += num;
										} else {
											tempValue[i] += num + "#";
										}									
									}catch(ParseException pe){									
										logger.error("Illegal number.");
									}catch(NumberFormatException ne){									
										logger.error("Illegal number.");
									}
								}
							}
							
							if((i == (tempValue.length - 1))){
								
								if(!tempValue[i].isEmpty()){
									value += tempValue[i];
								}
							}else{
								value += tempValue[i] + "#";
							}
						}
						
						//System.out.println(name+": "+value);					
							
						if ((value != null)) {
							if(value.isEmpty()){
								if(parameters.containsKey(name)){
									parameters.remove(name);
								}
							}
							else {
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
			/*}else{
				
				
		        request.getSession().setAttribute("type","error");
		        redirectAttributes.addFlashAttribute("msg", "create_failed");
		    	
		    	returnUrl = "redirect:/" + returnUrl+ "?deviceTypeSelected="+ deviceTypeSelected;
			}*/
        }catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
    	    	    	    	
    	return returnUrl;    	
    }
	
	public boolean validateSessionDeviceTypeConfig(final Session session, final BindingResult result,final ModelMap model,
        final HttpServletRequest request, final RedirectAttributes redirectAttributes){
		
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

