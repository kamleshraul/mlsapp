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

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.AppUser;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Employee;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Role;
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
        Credential credential = Credential.findByFieldName(Credential.class,
                "username", username, "");
        // users will only be allowed login if they have an entry in credential
        // table.
        if (credential == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        else if (!credential.isEnabled()) {
            throw new UsernameNotFoundException("User Is Disabled");
        }
        // for locale based login add code here
        //
        Collection<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        boolean hasUserRole = false;
        if (credential.getRoles().isEmpty()) {
            throw new UsernameNotFoundException(
                    "User must have minimum role of 'USER' to login");
        }
        for (Role role : credential.getRoles()) {
            roles.add(new GrantedAuthorityImpl(role.getName()));
        }
        // Here we are just looking for users by locale mr_IN and setting their
        // values in the AuthUser.
        // Here there needs to be code for getting the locale of the application
        String firstName = "";
        String title = "";
        String middleName = "";
        String lastName = "";
        String photo = "";
        if (credential.getUserType().equals("M")) {
            Member member = Member.findByFieldName(Member.class, "credential",
                    credential, "mr_IN");
            if (member != null) {
                firstName = member.getFirstName();
                if (member.getTitle() == null) {
                    title = "";
                }
                else {
                    title = member.getTitle().getName();
                }
                middleName = member.getMiddleName();
                lastName = member.getLastName();
                photo = member.getPhoto();
            }
        }
        else if (credential.getUserType().equals("E")) {
            Employee employee = Employee.findByFieldName(Employee.class,
                    "crdential", credential, "mr_IN");
            if (employee != null) {
                firstName = employee.getFirstName();
                if (employee.getTitle() == null) {
                    title = "";
                }
                else {
                    title = employee.getTitle().getName();
                }
                middleName = employee.getMiddleName();
                lastName = employee.getLastName();
                photo = employee.getPhoto();
            }
        }
        else {
            AppUser user = AppUser.findByFieldName(AppUser.class, "credential",
                    credential, "en_US");
            if (user != null) {
                firstName = user.getFirstName();
                if (user.getTitle() == null) {
                    title = "";
                }
                else {
                    title = user.getTitle().getName();
                }
                middleName = user.getMiddleName();
                lastName = user.getLastName();
                photo = user.getPhoto();
            }
        }
        // update the last login time of user
        credential.setLastLoginTime(new Date());
        credential.merge();
        return new AuthUser(credential.getUsername(), credential.getPassword(),
                credential.isEnabled(), true, true, true, roles, title,
                firstName, middleName, lastName, credential.getDefaultHouseType()
                        , photo, credential.getRoles(),credential.getId());
    }
}
