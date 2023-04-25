package org.mkcl.els.common.interceptor;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.mkcl.els.controller.HomeController;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ibm.icu.text.SimpleDateFormat;


public class ResetPasswordInterceptor extends HandlerInterceptorAdapter {
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
			throws ServletException {
		// if (handler instanceof HomeController) { // only intercept my
		// controller, don't mess with other controllers.
		String currentUser=request.getRemoteUser(); 
		if(currentUser !=null)
		{
		if (isPasswordChangeRequired(currentUser)) {
			if (!request.getRequestURI().contains("password")
					&& !request.getRequestURI().contains("Password")
					&& !request.getRequestURI().contains("login")
					&& !request.getRequestURI().contains("home")) { // avoid
																	// redirection
																	// infinite
																	// loop!
				if (!request.getSession().isNew()) {
					redirect(request, response, "/myprofile/password");

					return false; // request handled, no need to bother
									// controller
				}
			}
		}
		}
		// }
		return true;
	}

    private void redirect(HttpServletRequest request, 
			  HttpServletResponse response, 
			  String path) throws ServletException {
	    try {
		response.sendRedirect(request.getContextPath() + path);
	    }
	    catch (java.io.IOException e) {
		throw new ServletException(e);
	    }
    }

    private boolean isPasswordChangeRequired(String currentUser) {
    	Credential credential=Credential.getBaseRepository().findByFieldName(Credential.class, "username", currentUser, null);
    	
    	CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "PASSWORD_EXPIRY_CONFIGURATION", "");
    	if(cp.getValue().equals("YES") && cp.getValue() != null  && cp != null )
    	{
    		LocalDateTime d1 = new LocalDateTime(credential.getPasswordChangeDateTime().getTime());
    		Integer days = Days.daysBetween(d1, new LocalDateTime()).getDays();
    		CustomParameter dayLimitString = CustomParameter.findByName(CustomParameter.class, "PASSWORD_EXPIRY_DAYS_LIMIT", "");
    		if(days > Integer.parseInt(dayLimitString.getValue()))
    		{
    			credential.setPasswordChangeCount(1);
    		}
    		
    	}
    	 if(credential.getPasswordChangeCount()<=1)
    	{
    		return true;
    	}

    	return false;
    }
}