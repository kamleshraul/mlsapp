package org.mkcl.els.common.interceptor;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Role;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SubmissionRestrictionInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		String locale = request.getParameter("locale"); //(optional) in case needed anywhere for fetching locale specific objects in the code
		if(locale==null || locale.isEmpty()) {
			locale = ApplicationLocale.findDefaultLocale();
		}
		
		CustomParameter csptRestURLs = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_URLS", "");
		CustomParameter csptRestRoles = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_ROLES", "");
		CustomParameter csptRestStartTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_START_TIME", "");
		CustomParameter csptRestEndTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_END_TIME", "");
		CustomParameter csptRestOperations = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_OPERATIONS", "");
		
		
		if(csptRestURLs != null 
				&& csptRestRoles != null 
				&& csptRestStartTime != null 
				&& csptRestEndTime != null
				&& csptRestOperations != null){
			String reqUrl = request.getRequestURL().toString();
			String url = reqUrl.substring(reqUrl.indexOf("els/")+"els/".length());
			
			String restrictionURL = containsRestrictionsForURL(url, csptRestURLs.getValue());
			
			if(restrictionURL != null && containsRestrictions(restrictionURL, csptRestURLs.getValue())){
				String user = request.getRemoteUser();
				String operation = request.getParameter("operation");				
				
				if(user != null){
					Credential cr = Credential.findByFieldName(Credential.class, "username", user, null);
					if(contains(cr.getRoles(), csptRestRoles.getValue())){
						
						if(containsRestrictionsForDevice(cr,url)){
							/**** check if url has housetype parameter ****/
							String houseTypeType = request.getParameter("houseType");
							if(houseTypeType!=null && !houseTypeType.isEmpty()) {
								HouseType houseType = HouseType.findByType(houseTypeType, locale);
								if(houseType!=null) {
									houseTypeType = houseType.getType(); //make sure to use type from the object												
								} else {
									try {
										houseType = HouseType.findById(HouseType.class, Long.parseLong(houseTypeType));
										if(houseType!=null) {
											houseTypeType = houseType.getType(); //make sure to use type from the object												
										}
									} catch(Exception e) {
										houseTypeType = null;
									}
								}
							} else {
								houseTypeType = null;
							}
							/**** check if url has devicetype parameter ****/
							String deviceTypeType = null;
							String deviceTypeId = request.getParameter("deviceType");
							if(deviceTypeId!=null && !deviceTypeId.isEmpty()) {
								DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));
								if(deviceType!=null) {
									deviceTypeType = deviceType.getType();
								}
							}
							if(deviceTypeType!=null && !deviceTypeType.isEmpty()) {
								if(houseTypeType!=null && !houseTypeType.isEmpty()) {
									csptRestStartTime = CustomParameter.findByName(CustomParameter.class, deviceTypeType.toUpperCase()+"_"+houseTypeType+"_RESTRICTION_START_TIME", "");
									if(csptRestStartTime == null) {
										csptRestStartTime = CustomParameter.findByName(CustomParameter.class, deviceTypeType.toUpperCase()+"_RESTRICTION_START_TIME", "");
									}
									if(csptRestStartTime == null) {
										csptRestStartTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_START_TIME", "");
									}
									
									csptRestEndTime = CustomParameter.findByName(CustomParameter.class, deviceTypeType.toUpperCase()+"_"+houseTypeType+"_RESTRICTION_END_TIME", "");
									if(csptRestEndTime == null) {
										csptRestEndTime = CustomParameter.findByName(CustomParameter.class, deviceTypeType.toUpperCase()+"_RESTRICTION_END_TIME", "");
									}
									if(csptRestEndTime == null) {
										csptRestEndTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_END_TIME", "");
									}
									
								} else {
									csptRestStartTime = CustomParameter.findByName(CustomParameter.class, deviceTypeType.toUpperCase()+"_RESTRICTION_START_TIME", "");
									if(csptRestStartTime == null) {
										csptRestStartTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_START_TIME", "");
									}
									
									csptRestEndTime = CustomParameter.findByName(CustomParameter.class, deviceTypeType.toUpperCase()+"_RESTRICTION_END_TIME", "");
									if(csptRestEndTime == null) {
										csptRestEndTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_END_TIME", "");
									}
								}								
								
							} else if(restrictionURL.contains("memberballot/listchoices")
										|| restrictionURL.contains("ballot/memberballot/choices")) {
								csptRestStartTime = CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_CHOICE_RESTRICTION_START_TIME", "");
								csptRestEndTime = CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_CHOICE_RESTRICTION_END_TIME", "");
								
							} else {
								csptRestStartTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_START_TIME", "");
								csptRestEndTime = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_END_TIME", "");
							}
							
							Date submitTime = new Date();
							
							Calendar startTimeCal = Calendar.getInstance();
							Calendar endTimeCal = Calendar.getInstance();
							
							String[] strStartTime = csptRestStartTime.getValue().split(":");
							String[] strEndTime = csptRestEndTime.getValue().split(":");
							
							
							startTimeCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strStartTime[0]));
							startTimeCal.set(Calendar.MINUTE, Integer.parseInt(strStartTime[1]));
							startTimeCal.set(Calendar.SECOND, Integer.parseInt(strStartTime[2]));
							Date startTime = startTimeCal.getTime();
							
							endTimeCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strEndTime[0]));
							endTimeCal.set(Calendar.MINUTE, Integer.parseInt(strEndTime[1]));
							endTimeCal.set(Calendar.SECOND, Integer.parseInt(strEndTime[2]));
							Date endTime = endTimeCal.getTime();	
							
							String[] arrStrUrlOp = restrictionURL.split(":");  
							if(arrStrUrlOp[1].equals("noop")){
								if(submitTime.before(startTime) || submitTime.after(endTime) || Holiday.isHolidayOnDate(submitTime, locale)){
									response.sendError(HttpServletResponse.SC_FORBIDDEN, "SUBMISSION WINDOW IS CLOSED NOW");
									return false;
								}
							}else if(arrStrUrlOp[1].equals("op")){
								if(operation != null 
										&& !operation.isEmpty() 
										&& containsRestrictions(operation, csptRestOperations.getValue())){
									if(submitTime.before(startTime) || submitTime.after(endTime) || Holiday.isHolidayOnDate(submitTime, locale)){
										response.sendError(HttpServletResponse.SC_FORBIDDEN, "SUBMISSION WINDOW IS CLOSED NOW");
										return false;
									}
								}
							}
						}
						
					}
				}
			}
		}
        return true;
	}
	
	private boolean contains(final Set<Role> roles, final String restrictedRoles){
		boolean retVal = false;
		for(Role r : roles){
			if(restrictedRoles.contains(r.getType())){
				retVal = true;
				break;
			}
		}
		return retVal;
	}
	
	private boolean containsRestrictions(final String data, final String restrictions){
		boolean retVal = false;
		String[] restrictUrls = restrictions.split(",");
		
		for(String r : restrictUrls){
			if(data.equals(r)){
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}
	
	private String containsRestrictionsForURL(final String url, final String restrictions){
		String retVal = null;
		String[] restrictUrls = restrictions.split(",");
		
		for(String r : restrictUrls){
			String[] urlAndOperation = r.split(":");
			if(url.equals(urlAndOperation[0])){
				retVal = r;
				break;
			}
		}
		
		return retVal;
	}
	private boolean containsRestrictionsForDevice(final Credential cr,final String url){
		boolean retVal = false;
		StringBuffer buffer=new StringBuffer();
		for(Role i:cr.getRoles()){
			buffer.append(i.getType()+",");
		}
		if(!buffer.toString().isEmpty()){
		buffer.deleteCharAt(buffer.length()-1);
		}
		buffer.toString();
		String rurl =(url.indexOf("/")>0? url.substring(0,url.indexOf("/")):url);
		CustomParameter csptRestDevices = CustomParameter.findByName(CustomParameter.class, "RESTRICTION_"+buffer.toString()+"_DEVICES", "");
		String[] restrictUrls = csptRestDevices.getValue().split(",");
		
		for(String r : restrictUrls){
			if(rurl.equals(r)){
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}
}
