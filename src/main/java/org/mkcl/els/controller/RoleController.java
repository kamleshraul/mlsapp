/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.RoleController.java
 * Created On: May 11, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class RoleController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/role")
public class RoleController extends GenericController<Role> {

}
