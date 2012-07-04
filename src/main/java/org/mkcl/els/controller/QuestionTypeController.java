/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.QuestionTypeController.java
 * Created On: 20 Jun, 2012
 */
package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.QuestionLimitingAction;
import org.mkcl.els.domain.QuestionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionTypeController.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Controller
@RequestMapping("/questiontype")
public class QuestionTypeController extends GenericController<QuestionType> {
    
    /** The Constant ASC. */
    private static final String ASC = "asc";
    
    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model, final QuestionType domain,
            final String locale, final HttpServletRequest request) {
        domain.setLocale(locale);
        List<QuestionLimitingAction> questionLimitingActions = QuestionLimitingAction.findAll(QuestionLimitingAction.class, "name", ASC,
                locale);
        model.addAttribute("questionLimitingActions", questionLimitingActions);        
    }
    
    protected void populateEdit(final ModelMap model, final QuestionType domain,
		final HttpServletRequest request) {
	List<QuestionLimitingAction> questionLimitingActions = QuestionLimitingAction.findAll(QuestionLimitingAction.class, "name", ASC,
                domain.getLocale());
        model.addAttribute("questionLimitingActions", questionLimitingActions);
    }
    
}
