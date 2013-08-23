/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MinistryController.java
 * Created On: Jun 2, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.Ministry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class MinistryController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ministry")
public class MinistryController extends GenericController<Ministry>{

}
