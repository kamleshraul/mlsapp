package org.mkcl.els.common.filter;
import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MessageResource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CaptchaFilter implements Filter {

    FilterConfig config;
	private String captchaRequired;
	private String captchaError="";
    
    public CaptchaFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest=(HttpServletRequest) request;
		HttpServletResponse httpResponse=(HttpServletResponse) response;
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		
		
		String captchaRequired="0";
		
		CustomParameter captchaCust = CustomParameter.findByName(CustomParameter.class, "CAPTCHA_REQUIRED", "");
		    if(captchaCust!=null && captchaCust.getValue()!=null 
		    		&& captchaCust.getValue().trim().length()>0 
		    		&& captchaCust.getValue().trim().equals("1")){

		    	captchaRequired="1";
		    }else{
		    	captchaRequired="0";
		    }
		    
		 request.setAttribute("captchaRequired",captchaRequired);
    	if(captchaRequired.trim().equals("1")){
    		//PropertyResourceBundle props1 = (PropertyResourceBundle)PropertyResourceBundle.getBundle("/actionerrors/admin");
    		Locale locale = request.getLocale();
    		String currentLocale=locale!=null && !locale.toString().isEmpty()?locale.toString().trim():"en_US";
    		MessageResource errorMsgResource = MessageResource.findByFieldName(MessageResource.class, "code", "login.invalid.captcha", currentLocale);
    		String errorMessage=(errorMsgResource!=null && !errorMsgResource.getValue().isEmpty())?errorMsgResource.getValue():"Invalid Captcha";
    		boolean returnToLogin=false;
    		String logincaptcha=httpRequest.getParameter("captcha");
    		StringBuffer requestedPath=httpRequest.getRequestURL();
    	     String captcha=(String)httpRequest.getSession().getAttribute("CAPTCHA");
    	     
    	        if(captcha==null || (captcha!=null && !captcha.equalsIgnoreCase(logincaptcha))){
    	        	if( requestedPath.indexOf("j_spring_security_check")>0){
    	        	   httpRequest.getSession().setAttribute("captchaError", errorMessage);
    	        	}
    	        	 returnToLogin=true;
    	        }else{
    	        	httpRequest.getSession().removeAttribute("captchaError");
    	        }
        		if(returnToLogin==true && requestedPath!=null && requestedPath.indexOf("j_spring_security_check")>0 ){
        			String path = "login.htm?captchaInvalidForward=true";    			
        			httpResponse.sendRedirect(path);
        			return;
        		}
    	}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.config=fConfig;
	}

}
