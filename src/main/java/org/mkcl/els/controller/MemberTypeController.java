/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MemberTypeController.java
 * Created On: May 11, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.MemberType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class MemberTypeController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/membertype")
public class MemberTypeController extends GenericController<MemberType> {

}
