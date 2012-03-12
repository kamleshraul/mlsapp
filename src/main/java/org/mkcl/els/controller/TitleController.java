/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.TitleController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.Title;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class TitleController.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_titles")
public class TitleController extends GenericController<Title> {
}
