/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.DegreeController.java
 * Created On: Mar 17, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.Degree;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class DegreeController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/degree")
public class DegreeController extends GenericController<Degree> {

}
