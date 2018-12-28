package org.mkcl.els.common.util;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.SupportLog;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlStrategy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 
class ConcurrentSessionControlStrategyForELS extends ConcurrentSessionControlStrategy {
 
	ConcurrentSessionControlStrategyForELS(SessionRegistry sessionRegistry) {
		super(sessionRegistry);
    }
 
    protected int getMaximumSessionsForThisUser(org.springframework.security.core.Authentication authentication) {
        int maximumSessions = 1;
        AuthUser currentUser = (AuthUser) authentication.getPrincipal();        
        System.out.println("Is allowed for multi login? : " + currentUser.isAllowedForMultiLogin());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String userAddress = request.getRemoteAddr();
		SupportLog supportLog = SupportLog.findLatest(userAddress);
        if(supportLog!=null) {
        	maximumSessions = -1;
        } else if(currentUser.isAllowedForMultiLogin()==true) {
        	maximumSessions = -1;
        }
        
        /** in case we require role based approach **/
//        for(GrantedAuthority authority: authentication.getAuthorities()) {
//        	if(authority.getAuthority().equals(ApplicationConstants.MEMBER_LOWERHOUSE)) {
//        		maximumSession = 1;
//        		break;
//        	}
//        }
        
        return maximumSessions;
    }
}