package org.mkcl.els.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.interceptor.CorsInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class CORSFilter
 */
public class CORSFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsInterceptor.class);

    public static final String REQUEST_ORIGIN_NAME = "Origin";

    public static final String CREDENTIALS_NAME = "Access-Control-Allow-Credentials";
    public static final String ORIGIN_NAME = "Access-Control-Allow-Origin";
    public static final String METHODS_NAME = "Access-Control-Allow-Methods";
    public static final String HEADERS_NAME = "Access-Control-Allow-Headers";
    public static final String MAX_AGE_NAME = "Access-Control-Max-Age";
    /**
     * Default constructor. 
     */
    public CORSFilter() {
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
		HttpServletResponse httpResponse=(HttpServletResponse) response;
		 HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		//httpResponse.setHeader(ORIGIN_NAME, "http://localhost:8080");
		httpResponse.setHeader("Content-Type", "text/html");
		httpResponse.setHeader("Cache-Control", null);
		httpResponse.setHeader("X-Requested-With",null);
		httpResponse.setHeader(METHODS_NAME, "POST,GET,PUT,UPDATE,OPTIONS");
		httpResponse.setHeader(HEADERS_NAME, "Content-Type, Access-Control-Allow-Headers, Authorization,observe,X-Requested-With,X-Auth-Token");
		/*
		 * if (httpRequest.getMethod().equals("OPTIONS")) {
		 * httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED); return; }
		 */
		
		chain.doFilter(request, httpResponse);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
