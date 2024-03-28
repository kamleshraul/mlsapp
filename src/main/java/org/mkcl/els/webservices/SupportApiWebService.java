package org.mkcl.els.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.service.ISecurityService;
import org.mkcl.els.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/ws/supportELS")
public class SupportApiWebService {
	@Autowired
	SessionRegistry sessionRegistry;
	
	@Autowired 
	private ISecurityService securityService;
	
	@Autowired
	JwtServiceImpl jwtService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value="/getAuthForSupport",method = RequestMethod.GET)
    public @ResponseBody String getAuthenticateForSupport(
    			HttpServletRequest request, HttpServletResponse response)  {
		
		String responseOnError ="Received Bad Data";
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String token = null;
	
		
		if (username == null  || password == null ){
			System.out.printf("username - [%s], password - [%s]",username,password);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return responseOnError;
		}
		if (username.trim().isEmpty()  || password.trim().isEmpty() ){
			System.out.printf("username - [%s], password - [%s]",username,password);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return responseOnError;
		}
		
		SecurityContext sc = SecurityContextHolder.getContext();
		Authentication auth = authenticateCredentials(username, password,sc);
		token = getToken(request,sc,auth);
		
		if (token != null) {
			response.setStatus(HttpStatus.OK.value());
			return token;
		}
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return  token ;
	}
	
	
	 private Authentication authenticateCredentials(String userName , String password,SecurityContext sc) {
		 UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userName,password);
		 Authentication auth = authenticationManager.authenticate(authReq);		
		 sc.setAuthentication(auth);
		return auth;
	 }
	 
	 private String getToken(HttpServletRequest request,SecurityContext sc,Authentication auth) {
		 	String token = null;

			// we have used overloaded method for getSession which means if session
			// not present it will create new  if we passed true as parameter
			// (It Can return Null ) <-- EdgeCase 
			HttpSession session = request.getSession(true);
			sessionRegistry.registerNewSession(session.getId(), sc.getAuthentication().getPrincipal());
				
			if (session != null) {
				 AuthUser au = (AuthUser) auth.getPrincipal();
				 Long userId = Credential.findUserIdByUsername(au.getUsername());
				 au.setUserId(userId);
				 token = jwtService.generateToken(au, session);			
			}
			return token;
	 }
	 
	 
	 
	 
	 
}

