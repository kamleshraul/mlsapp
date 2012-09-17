package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/session/workflowconfig")
public class WorkflowConfigController extends GenericController<WorkflowConfig>{


    @Override
    protected void populateNew(final ModelMap model, final WorkflowConfig domain,
            final String locale, final HttpServletRequest request) {
        /*
         * setting locale
         */
        domain.setLocale(locale);
        /*
         * setting workflows
         */
        List<Workflow> workflows=Workflow.findAll(Workflow.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("workflows",workflows);
        /*
         * setting usergroups
         */
        List<UserGroup> userGroups=UserGroup.findAll(UserGroup.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("usergroups",userGroups);
        /*
         * setting session
         */
        Long sessionId=Long.parseLong(request.getParameter("session"));
        model.addAttribute("session",sessionId);
        /*
         * setting workflow actor count
         */
        model.addAttribute("workflowactorCount", 0);
    }

    @Override
    protected void populateEdit(final ModelMap model, final WorkflowConfig domain,
            final HttpServletRequest request) {
        String locale=domain.getLocale();
        /*
         * setting workflows
         */
        List<Workflow> workflows=Workflow.findAll(Workflow.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("workflows",workflows);
        /*
         * setting usergroups
         */
        List<UserGroup> userGroups=UserGroup.findAll(UserGroup.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("usergroups",userGroups);
        /*
         * setting session
         */
        model.addAttribute("session",domain.getSession().getId());
    }

}
