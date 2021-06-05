package org.mkcl.els.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ExternalController {
	String EXTERNALLINK="http://mls.org.in";
	
	@Autowired
	JwtServiceImpl jwtService;
	
	@Autowired
	SessionRegistry sessionRegistry;
	
	
	
	@RequestMapping(value="/external",method= {RequestMethod.GET,RequestMethod.POST})
	public String externalLink(HttpServletRequest request,HttpServletResponse httpServletResponse,Model model) {
		
		HttpSession session = request.getSession(false);
		String currentLoggedInUserName="";
		SecurityContextImpl sci = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");

		if (sci != null) {
		        UserDetails currentLoggedInUser = (UserDetails) sci.getAuthentication().getPrincipal();
		        WebAuthenticationDetails webAuth= (WebAuthenticationDetails) sci.getAuthentication().getDetails();
		        
		        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
				List<SessionInformation> allSessions=new ArrayList<SessionInformation>();
				
				for(Object obj:allPrincipals) {
					AuthUser authUser=(AuthUser)obj;
					if(authUser!=null && authUser.getActualUsername()!=null 
							&& authUser.getActualUsername().trim().equalsIgnoreCase(currentLoggedInUser.getUsername()) ) {
						
						allSessions = sessionRegistry.getAllSessions(obj,false);
						break;
					}
				}
		        if(currentLoggedInUser!=null && currentLoggedInUser.getUsername()!=null) {
		        	currentLoggedInUserName=currentLoggedInUser.getUsername();
		        	String jwtToken=jwtService.generateToken(currentLoggedInUser, session,allSessions);
		        	if(jwtToken!=null && jwtToken.trim().length()>0) {
		        	httpServletResponse.addHeader("Authorization",String.format("Bearer %s",jwtToken));
		        	model.addAttribute("token", jwtToken);
		        	}
		        }
		}
		
		CustomParameter externalLink = CustomParameter.findByName(CustomParameter.class, "EXTERNAL_LINK", "");
		if(externalLink!=null && externalLink.getValue().trim().length()>0) {
			EXTERNALLINK=externalLink.getValue();
			EXTERNALLINK=String.format("%s?userid=%s",EXTERNALLINK,currentLoggedInUserName);
			model.addAttribute("EXTERNALLINK",EXTERNALLINK);
		}
		/*
		 * httpServletResponse.containsHeader("Access-Control-Allow-Origin");
		 * httpServletResponse.containsHeader("Access-Control-Allow-Methods");
		 * httpServletResponse.containsHeader("Access-Control-Allow-Headers");
		 * httpServletResponse.addHeader("Location", EXTERNALLINK);
		 * httpServletResponse.setStatus(HttpStatus.FOUND.value());
		 */
		
		return "memberreimbursement/memberreimbursement";
		
	}
}
