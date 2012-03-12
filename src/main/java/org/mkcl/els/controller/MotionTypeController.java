/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.MotionTypeController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.MotionType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class MotionTypeController.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Controller
@RequestMapping("/masters_motiontype")
public class MotionTypeController extends GenericController<MotionType> {
}
