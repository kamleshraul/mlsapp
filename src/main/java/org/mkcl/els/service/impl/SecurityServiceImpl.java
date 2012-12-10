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
import java.util.List;
import java.util.Set;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.UserGroup;
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
    throws UsernameNotFoundException , DataAccessException {
        //here we will first find if login will be by email or by username.
        CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"LOGIN_BY", "");
        Credential credential=null;
        if(customParameter!=null){
            String value=customParameter.getValue();
            if(value.equals("EMAIL")||value.equals("email")){
                credential = Credential.findByFieldName(Credential.class,"email", username, "");
            }else if(value.equals("USERNAME")||value.equals("username")){
                credential = Credential.findByFieldName(Credential.class,"username", username, "");
            }
        }
        //then we check if login was successful/failure.
        //login fails if username not found or if credential is disabled
        //or if no role is defined
        if(credential!=null){
        Set<Role> allRoles=credential.getRoles();
        if(credential.getId()==null){
            throw new UsernameNotFoundException("User Not Found");
        }else{
            if(!credential.isEnabled()){
                throw new UsernameNotFoundException("User Not Found");
            }else if(allRoles.isEmpty()){
                throw new UsernameNotFoundException("User Not Found");
            }else{
                //here we are setting username,password,isenabled,roles
                Collection<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
                for (Role role : allRoles) {
                    roles.add(new GrantedAuthorityImpl(role.getName()));
                }
                credential.setLastLoginTime(new Date());
                credential.merge();
                List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"credential",credential,"locale",ApplicationConstants.DESC,"");
                return new AuthUser(credential.getUsername(), credential.getPassword(),credential.getUsername(),credential.getEmail(),
                        credential.isEnabled(), true, true, true, roles,credential.getRoles(),userGroups);
            }
        }
    }else{
        throw new UsernameNotFoundException("User Not Found");
    }
    }
}