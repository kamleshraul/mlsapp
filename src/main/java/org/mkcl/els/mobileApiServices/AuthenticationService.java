package org.mkcl.els.mobileApiServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.service.ISecurityService;
import org.mkcl.els.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mobileApiService/AuthService")
public class AuthenticationService {
	
	@Autowired
	SessionRegistry sessionRegistry;
	
	@Autowired 
	private ISecurityService securityService;
	
	@Autowired
	JwtServiceImpl jwtService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	
	


	@RequestMapping(value="/getAuthenticated",consumes = MediaType.APPLICATION_JSON_VALUE)
	    public @ResponseBody String getAuthenticateByCredentials(
	    		@RequestBody Map<String,String> requestMap,
	    			HttpServletRequest request, HttpServletResponse response) throws ELSException{
	   
		   String username = requestMap.get("username");
		   String password = requestMap.get("password");
		   
			if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {

				UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username,
						password);
				Authentication auth = authenticationManager.authenticate(authReq);
				SecurityContext sc = SecurityContextHolder.getContext();
				sc.setAuthentication(auth);
				HttpSession session = request.getSession(true);
				// session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
				sessionRegistry.registerNewSession(session.getId(), sc.getAuthentication().getPrincipal());
				// System.out.println(sessionRegistry.getAllPrincipals().toArray().toString());
				AuthUser au = (AuthUser) auth.getPrincipal();

				if (session != null) {
					String token = jwtService.generateToken(au, session);
					if (token != null) {
						response.setStatus(HttpStatus.OK.value());
						return token;
					}

				}
				
				
			}
		   response.setStatus(HttpStatus.UNAUTHORIZED.value());
		   return null;
	   }
	
	@RequestMapping(value="/logOut",consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String Logout(
    		@RequestBody Map<String,String> requestMap,
    			HttpServletRequest request, HttpServletResponse response,HttpSession session) throws ELSException{
		
		String token = requestMap.get("token");
		
		Map<String ,Object> tokenDetails = jwtService.getLogOutDetailsFromToken(token);

		if (tokenDetails != null) {

			String username = (String) tokenDetails.get("username");
			String sessionId = (String) tokenDetails.get("sessionId");

			List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
			List<SessionInformation> allSessions = new ArrayList<SessionInformation>();
			if (allPrincipals != null) {
				for (Object obj : allPrincipals) {
					AuthUser authUser = (AuthUser) obj;
					if (authUser != null && authUser.getActualUsername() != null
							&& authUser.getActualUsername().trim().equalsIgnoreCase(username)) {
						System.out.println(authUser.getUsername());
						allSessions = sessionRegistry.getAllSessions(obj, false);

					}
				}
			}

			if (allSessions != null) {
				for (SessionInformation si : allSessions) {
					if (si.getSessionId().equals(sessionId)) {
						sessionRegistry.removeSessionInformation(sessionId);
						return "Success";
					}
				}
			}

		}
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		return null;
		
	}

	

}
