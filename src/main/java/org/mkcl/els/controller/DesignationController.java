/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DesignationController.java
 * Created On: Apr 30, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.Designation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class DesignationController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/designation")
public class DesignationController extends GenericController<Designation> {

}
