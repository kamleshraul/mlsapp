package org.mkcl.els.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.CustomParameter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ActivityInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		CustomParameter cspt = CustomParameter.findByName(CustomParameter.class, "INTERCEPT_URL", "");
		
		String url = request.getRequestURL().toString();	
		if(cspt != null && cspt.getValue() != null && !cspt.getValue().isEmpty()){
			if(cspt.getValue().contains(url)){
				ActivityLog.logActivity(request, ApplicationLocale.findDefaultLocale());
			}
		}
		
		return true;
	}
}
