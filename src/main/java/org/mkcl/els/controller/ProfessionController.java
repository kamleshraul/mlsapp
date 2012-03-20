/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ProfessionController.java
 * Created On: Mar 17, 2012
 */
package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Profession;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class ProfessionController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_professions")
public class ProfessionController extends GenericController<Profession> {
	
}
