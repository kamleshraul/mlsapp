/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.interceptor.RequestProcessingTimeInterceptor.java
 * Created On: Jul 28, 2015
 */
package org.mkcl.els.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.CustomParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * The Class RequestProcessingTimeInterceptor.
 *
 * @author vikasg
 * @since v1.0.0
 */
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(RequestProcessingTimeInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String url = request.getRequestURL().toString();
		CustomParameter csptURLSToLog = CustomParameter.findByName(CustomParameter.class, "LOG_URLS", "");
		
		if(csptURLSToLog != null && csptURLSToLog.getValue() != null 
				&& !csptURLSToLog.getValue().isEmpty()){
			if(isURLToBeLogged(url, csptURLSToLog.getValue().split(","))){
				ActivityLog.logActivity(request, ApplicationLocale.findDefaultLocale());
			} 
			else if(request.getMethod().equalsIgnoreCase(ApplicationConstants.REQUEST_METHOD_POST)
					|| request.getMethod().equalsIgnoreCase(ApplicationConstants.REQUEST_METHOD_PUT)
					|| request.getMethod().equalsIgnoreCase(ApplicationConstants.REQUEST_METHOD_DELETE)) {
				
				CustomParameter csptURLSToSkipLog = CustomParameter.findByName(CustomParameter.class, "URLS_TO_SKIP_LOG", "");
				if(csptURLSToSkipLog == null){
					ActivityLog.logActivity(request, ApplicationLocale.findDefaultLocale());
				} 
				else if(csptURLSToSkipLog != null && csptURLSToSkipLog.getValue() != null 
						&& !csptURLSToSkipLog.getValue().isEmpty()){
					if(!isURLToBeSkippedFromLog(url, csptURLSToSkipLog.getValue().split(","))){
						ActivityLog.logActivity(request, ApplicationLocale.findDefaultLocale());
					} 
				}
				
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		/*System.out.println("Request URL::" + request.getRequestURL().toString()
				+ " Sent to Handler :: Current Time="
				+ System.currentTimeMillis());*/
		// we can add attributes in the modelAndView and use that in the view
		// page
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		/*long startTime = (Long) request.getAttribute("startTime");
		logger.info("Request URL::" + request.getRequestURL().toString()
				+ ":: End Time=" + System.currentTimeMillis());
		logger.info("Request URL::" + request.getRequestURL().toString()
				+ ":: Time Taken=" + (System.currentTimeMillis() - startTime));*/
	}

	
	private boolean isURLToBeLogged(final String url, final String[] logURLs){
		boolean retVal = false;
		for(String st : logURLs){
			if(url.contains(st)){
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	
	private boolean isURLToBeSkippedFromLog(final String url, final String[] logURLs){
		boolean retVal = false;
		for(String st : logURLs){
			if(url.contains(st)){
				retVal = true;
				break;
			}
		}
		return retVal;
	}
}