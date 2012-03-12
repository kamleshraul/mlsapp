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
import java.util.HashSet;

import org.mkcl.els.domain.AuthUser;
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
 *
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
        User user = User.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        Collection<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        for (Role role : user.getRoles()) {
            roles.add(new GrantedAuthorityImpl(role.getName()));
        }
        return new AuthUser(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true, true, roles, user.getId(),
                user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getLastLoginTime(), user.getCode(), user.getMobile());
    }
}
