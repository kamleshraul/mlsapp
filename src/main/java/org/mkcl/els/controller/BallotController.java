/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.BallotController.java
 * Created On: Jan 11, 2013
 */
package org.mkcl.els.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class BallotController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ballot")
public class BallotController extends BaseController{

    /**
     * Creates the final member ballot.
     *
     * @param request the request
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value="/memberballot/final",method=RequestMethod.POST)
    public String createFinalMemberBallot(final HttpServletRequest request,
            final Locale locale){


        return null;
    }
}
