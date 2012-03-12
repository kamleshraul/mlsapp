/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.UserServiceImpl.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service.impl;

import org.mkcl.els.domain.User;
import org.mkcl.els.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * The Class UserServiceImpl.
 *
 * @author amitb
 * @version v1.0.0
 */
@Service
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements
        IUserService {
}