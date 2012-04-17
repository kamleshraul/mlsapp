/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.SecurityServiceImpl.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class SecurityServiceImpl.
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Service("securityService")
public class SecurityServiceImpl implements UserDetailsService {

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    @SuppressWarnings("deprecation")
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException, DataAccessException {
    	//Here way needs to be found to read the current locale so that user info can be found
    	//locale based.Currently it is set to mr_IN.
    	User user=User.findByUserName(username, "mr_IN");
		String houseType=null;
    	if(user.getId()==null){
            	throw new UsernameNotFoundException("User Not Found");
    	}else{
    		Credential credential=user.getCredential();
    		if(!credential.isEnabled()){
                throw new UsernameNotFoundException("User Not Found");
    		}else{
    			Collection<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
    			if(credential.getRoles().isEmpty()){
                    throw new UsernameNotFoundException("User Not Found");
    			}
    	        for (Role role : credential.getRoles()) {
    	            roles.add(new GrantedAuthorityImpl(role.getName()));
    	            if(role.getName().equals("DATA_ENTRY_OPERATOR_LOWERHOUSE")||role.getName().equals("ADMIN_LOWERHOUSE")){
    	            	houseType=ApplicationConstants.LOWER_HOUSE;
    	            }else if(role.getName().equals("DATA_ENTRY_OPERATOR_UPPERHOUSE")||role.getName().equals("ADMIN_UPPERHOUSE")){
    	            	houseType=ApplicationConstants.UPPER_HOUSE;
    	            }else if(role.getName().equals("DATA_ENTRY_OPERATOR_BOTHHOUSE")||role.getName().equals("ADMIN_BOTHHOUSE")){
    	            	houseType=ApplicationConstants.BOTH_HOUSE;
    	            }else if(role.getName().equals("DATA_ENTRY_OPERATOR_DEFAULTHOUSE")||role.getName().equals("ADMIN_DEFAULTHOUSE")){
                        houseType=ApplicationConstants.DEFAULT_HOUSE;
                    }
    	        }
    	        credential.setLastLoginTime(new Date());
    	        credential.merge();
    	        return new AuthUser(credential.getUsername(), credential.getPassword(),
    	                credential.isEnabled(), true, true, true, roles, user.getTitle(),
    	                user.getFirstName(),user.getMiddleName(), user.getLastName(),houseType,
    	                        credential.getRoles());
    		}
    	}
}
}