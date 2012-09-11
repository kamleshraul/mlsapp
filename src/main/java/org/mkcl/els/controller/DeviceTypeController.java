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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.QuestionLimitingAction;
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
@RequestMapping("/devicetype")
public class DeviceTypeController extends GenericController<DeviceType> {

    /** The Constant ASC. */
    private static final String ASC = "asc";

    /* (non-Javadoc)
     * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateNew(final ModelMap model, final DeviceType domain,
            final String locale, final HttpServletRequest request) {
        domain.setLocale(locale);
       /*
        * For Starred Questions we need following additional parameters apart from
        * name and type:HAS_STARRED_QUESTION_LIMIT,STARRED_QUESTION_LIMIT,STARRED_QUESTION_LIMITING_ACTIONS,
        * STARRED_QUESTION_WARNING_MESSAGE
        */
        List<QuestionLimitingAction> questionLimitingActions = QuestionLimitingAction.findAll(QuestionLimitingAction.class, "name", ASC,
                locale);
        model.addAttribute("questionLimitingActions", questionLimitingActions);
        model.addAttribute("hasQuestionLimit", "false");
    }

    @Override
    protected void populateEdit(final ModelMap model, final DeviceType domain,
		final HttpServletRequest request) {
        /*
         * For Starred Questions we need following additional parameters apart from
         * name and type:HAS_STARRED_QUESTION_LIMIT,STARRED_QUESTION_LIMIT,STARRED_QUESTION_LIMITING_ACTIONS,
         * STARRED_QUESTION_WARNING_MESSAGE
         */
         List<QuestionLimitingAction> questionLimitingActions = QuestionLimitingAction.findAll(QuestionLimitingAction.class, "name", ASC,
                 domain.getLocale());
         model.addAttribute("questionLimitingActions", questionLimitingActions);
         if(domain.getParameterValue("STARRED_QUESTION_HAS_LIMIT")!=null){
             if(!domain.getParameterValue("STARRED_QUESTION_HAS_LIMIT").isEmpty()){
                 model.addAttribute("hasQuestionLimit", "true");
             }else{
                 model.addAttribute("hasQuestionLimit", "false");
             }
         }else{
             model.addAttribute("hasQuestionLimit", "false");
         }
         model.addAttribute("parameters",domain.getParameters());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void populateCreateIfNoErrors(final ModelMap model, final DeviceType domain,
            final HttpServletRequest request) {
        /*
         * Here we are collecting all the request parameters that begins with 'param_'
         * and storing them as key/value pair.key is obtained by splitting request
         * parameters that begins with param_ and storing index[1]
         */
        Map<String,String[]> params=request.getParameterMap();
        Map<String,String> deviceTypeParams=new HashMap<String, String>();
        for(Entry<String,String[]> i:params.entrySet()){
            String key=i.getKey();
            if(key.startsWith("param_")){
                deviceTypeParams.put(key.split("param_")[1],params.get(i.getKey())[0]);
            }
        }
        domain.setParameters(deviceTypeParams);
    }

    @Override
    protected void populateUpdateIfNoErrors(final ModelMap model, final DeviceType domain,
            final HttpServletRequest request) {
        /*
         * Here we are collecting all the request parameters that begins with 'param_'
         * and storing them as key/value pair.key is obtained by splitting request
         * parameters that begins with param_ and storing index[1]
         */
        Map<String,String[]> params=request.getParameterMap();
        Map<String,String> deviceTypeParams=new HashMap<String, String>();
        for(Entry<String,String[]> i:params.entrySet()){
            String key=i.getKey();
            if(key.startsWith("param_")){
                deviceTypeParams.put(key.split("param_")[1],params.get(i.getKey())[0]);
            }
        }
        domain.setParameters(deviceTypeParams);
    }


}
