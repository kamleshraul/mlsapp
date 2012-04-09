/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ReservationController.java
 * Created On: Mar 17, 2012
 */
package org.mkcl.els.controller;

import org.mkcl.els.domain.Reservation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class ReservationController.
 *
 * @author Anand
 * @since v1.0.0
 */
@Controller
@RequestMapping("/reservation")
public class ReservationController extends GenericController<Reservation> {
}
