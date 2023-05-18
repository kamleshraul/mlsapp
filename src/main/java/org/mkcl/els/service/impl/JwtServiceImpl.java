package org.mkcl.els.service.impl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.ApiToken;
import org.mkcl.els.domain.Role;
import org.mkcl.els.service.IJwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

@Service("jwtService")
public class JwtServiceImpl implements IJwtService {
	
	String secret="12345";

	public String generateToken(Credential credential,int sessionTimeOutTime) {
		String userName=credential.getUsername();
		Long userId=credential.getId();
		Set<Role> roles = credential.getRoles();
		List<String> userRoles=new ArrayList<String>();
		for(Role r:roles) {
			if(r.getName()!=null && r.getName().trim().length()>0) {
				userRoles.add(r.getName());
			}
		}
		Calendar calendar=Calendar.getInstance();
		
		Map<String,Object> tokenData=new HashMap<String,Object>();
		tokenData.put("userId", userId);
		tokenData.put("roles", userRoles);
		tokenData.put("issuedAt", calendar.getTime());
		tokenData.put("expirationOn",new Date(calendar.getTimeInMillis()+sessionTimeOutTime));
				
		JWTSigner jwtSigner=new JWTSigner(secret);
		return jwtSigner.sign(tokenData);
	
	}
	
	
	
	public boolean verifyJwtToken(String jwtToken,String username,List<SessionInformation> usersActiveSession) {
		JWTVerifier jwtVerified=new JWTVerifier(secret);
		try {
			Map<String, Object> tokenData = jwtVerified.verify(jwtToken);
			
			if(tokenData!=null && tokenData.size()==5) {
				String tokenUserName=(String)tokenData.get("username");
				String userSessionId=(String) tokenData.get("userSessionId");
				List<String> userRoles=(List<String>) tokenData.get("userRole");
				Date issuedAt=  new Date((Long)tokenData.get("issueAt"));
				Date expirationOn=new Date((Long)tokenData.get("expirationOn"));
				boolean isValidToken=false;
				
				if(tokenUserName!=null && tokenUserName.trim().length()>0 
						&& tokenUserName.trim().equals(username.trim())) {
						isValidToken=true;
				}else {
					return false;
				}
				
				//check session id from token
				if(userSessionId!=null && usersActiveSession!=null) {
					boolean activeSessionFound=false;;
					for(SessionInformation sim:usersActiveSession) {
						if(sim!=null && sim.getSessionId()!=null) {
							if(sim.getSessionId().equals(userSessionId) && !sim.isExpired()) {
								activeSessionFound=true;								
								break;
							}
						}
					}
					
					if(!activeSessionFound)
						return false;
				}
				
				if(expirationOn!=null && expirationOn.compareTo(new Date())>0) {
					isValidToken=true;
					return true;
				}else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 	
		return false;
	}

	public String generateToken(UserDetails currentLoggedInUser, HttpSession session,List<SessionInformation> sesssionList) {
		if(currentLoggedInUser!=null && session!=null) {
			String userSessionId=sesssionList.get(0).getSessionId();
			String currentUserName=currentLoggedInUser.getUsername();
			
			
			Set<String> userRoles=new LinkedHashSet<String>();
			if(currentLoggedInUser.getAuthorities()!=null && currentLoggedInUser.getAuthorities().size()>0) {
				Collection<? extends GrantedAuthority> authorities = currentLoggedInUser.getAuthorities();
				Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
				while(iterator.hasNext()) {
					GrantedAuthority userAuthority = iterator.next();
					userRoles.add(userAuthority.getAuthority());
				}
			}
			Calendar calendar=Calendar.getInstance();
			Date issuedAt = calendar.getTime();
			calendar.add(Calendar.SECOND, session.getMaxInactiveInterval());
			Date expirationOn=calendar.getTime();
			
			Map<String,Object> tokenData=new HashMap<String, Object>();
			tokenData.put("username", currentUserName);
			tokenData.put("userSessionId", userSessionId);
			tokenData.put("userRole", userRoles);
			tokenData.put("issueAt", issuedAt);
			tokenData.put("expirationOn", expirationOn);
			JWTSigner jwtSigner=new JWTSigner(secret);
			return jwtSigner.sign(tokenData);
		}
		return null;
	}
	
	public String generateToken(ApiToken JTDetails) {
		if(JTDetails !=null) {
			
			Map<String,Object> tokenData=new HashMap<String, Object>();
			tokenData.put("clientName", JTDetails.getSubUrl());
			tokenData.put("tokenID", JTDetails.getId());
			tokenData.put("fromDate", JTDetails.getFromDate());
			tokenData.put("toDate", JTDetails.getToDate());
			JWTSigner jwtSigner=new JWTSigner(secret);
			return jwtSigner.sign(tokenData);
			
		}
		
		return null;
	}
	
	public boolean verifyJwtToken(String jwtToken) {
		JWTVerifier jwtVerified = new JWTVerifier(secret);
		try {

			Map<String, Object> tokenData = jwtVerified.verify(jwtToken);
			if (tokenData != null) {
				String tokenClientName = (String) tokenData.get("clientName");
				// Todo Change To Token ID
				String tokenId = tokenData.get("tokenID").toString();
				Date fromDate = new Date((Long) tokenData.get("fromDate"));
				Date toDate = new Date((Long) tokenData.get("toDate"));

				ApiToken exToken = ApiToken.findById(ApiToken.class, Long.parseLong(tokenId));

				if (toDate.compareTo(exToken.getToDate()) == 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
}
