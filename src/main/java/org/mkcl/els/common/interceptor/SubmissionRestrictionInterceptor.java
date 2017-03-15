package org.mkcl.els.common.interceptor;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.Role;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SubmissionRestrictionInterceptor extends HandlerInterceptorAdapter {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
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
						Date submitTime = new Date();
						
						Calendar cal = Calendar.getInstance();
						Date startTime = cal.getTime();
						Date endTime = cal.getTime();
						
						String[] strStartTime = csptRestStartTime.getValue().split(":");
						String[] strEndTime = csptRestEndTime.getValue().split(":");
						
						
						startTime.setHours(Integer.parseInt(strStartTime[0])); 
						startTime.setMinutes(Integer.parseInt(strStartTime[1])); 
						startTime.setSeconds(Integer.parseInt(strStartTime[2]));
						
						endTime.setHours(Integer.parseInt(strEndTime[0])); 
						endTime.setMinutes(Integer.parseInt(strEndTime[1])); 
						endTime.setSeconds(Integer.parseInt(strEndTime[2]));	
						
						String[] arrStrUrlOp = restrictionURL.split(":");  
						if(arrStrUrlOp[1].equals("noop")){
							if(submitTime.before(startTime) || submitTime.after(endTime) || Holiday.isHolidayOnDate(submitTime, ApplicationConstants.DEFAULT_LOCALE)){
								response.sendError(HttpServletResponse.SC_FORBIDDEN, "SUBMISSION WINDOW IS CLOSED NOW");
								return false;
							}
						}else if(arrStrUrlOp[1].equals("op")){
							if(operation != null 
									&& !operation.isEmpty() 
									&& containsRestrictions(operation, csptRestOperations.getValue())){
								if(submitTime.before(startTime) || submitTime.after(endTime) || Holiday.isHolidayOnDate(submitTime, ApplicationConstants.DEFAULT_LOCALE)){
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
