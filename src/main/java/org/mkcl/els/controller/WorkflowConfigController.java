package org.mkcl.els.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("userGroupTypes",userGroupTypes);
        /*
         * setting session
         */
        Long sessionId=Long.parseLong(request.getParameter("session"));
        Session selectedSession=Session.findById(Session.class, sessionId);
        model.addAttribute("houseType",selectedSession.getHouse().getType().getType());
        model.addAttribute("session",sessionId);
        /*
         * setting device types
         */
        List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name",ApplicationConstants.ASC, locale);
        model.addAttribute("deviceTypes",deviceTypes);
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
        List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("userGroupTypes",userGroupTypes);
        /*
         * setting session
         */
        model.addAttribute("session",domain.getSession().getId());
        model.addAttribute("houseType",domain.getSession().getHouse().getType().getType());
        /*
         * populating workflow actors
         */
        model.addAttribute("workflowactors", domain.getWorkflowactors());
        model.addAttribute("workflowactorCount", domain.getWorkflowactors().size());
        /*
         * setting device types
         */
        List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name",ApplicationConstants.ASC, locale);
        model.addAttribute("deviceTypes",deviceTypes);        
    }

    private void populateWorkflowActors(final WorkflowConfig domain, final HttpServletRequest request,final BindingResult result){
        List<WorkflowActor> workflowActors = new ArrayList<WorkflowActor>();
        Integer workflowactorCount = Integer.parseInt(request
                .getParameter("workflowactorCount"));
        for (int i = 1; i <= workflowactorCount; i++) {
            WorkflowActor workflowActor=new WorkflowActor();

            String level=request.getParameter("workflowactorLevel"+i);
            if(level!=null){
                workflowActor.setLevel(Integer.parseInt(level));
            }
            
            //group is required only in case when there are more than one actors on the same level
            //group is comma separated list of group numbers
            String group=request.getParameter("workflowactorGroup"+i);
           if(group!=null){
              workflowActor.setGroupName(group);
           }

            String strUserGroupType=request.getParameter("workflowactorName"+ i);
            if(strUserGroupType!=null){
                UserGroupType userGroupType=UserGroupType.findById(UserGroupType.class,Long.parseLong(strUserGroupType));
                workflowActor.setUserGroupType(userGroupType);
            }

            String id=request.getParameter("workflowactorId"+ i);
            if(id!=null){
                if(!id.isEmpty()){
                    workflowActor.setId(Long.parseLong(id));
                }
            }

            String version=request.getParameter("workflowactorVersion"+ i);
            if(version!=null){
                if(!version.isEmpty()){
                    workflowActor.setVersion(Long.parseLong(version));
                }
            }

            String locale=request.getParameter("workflowactorLocale"+ i);
            if(locale!=null){
                if(!locale.isEmpty()){
                    workflowActor.setLocale(locale);
                }
            }
            workflowActors.add(workflowActor);
        }
        domain.setWorkflowactors(workflowActors);
    }


@Override
protected void preValidateCreate(final WorkflowConfig domain,
        final BindingResult result, final HttpServletRequest request) {
    populateWorkflowActors(domain, request, result);
}

@Override
protected void preValidateUpdate(final WorkflowConfig domain,
        final BindingResult result, final HttpServletRequest request) {
    populateWorkflowActors(domain, request, result);

}

@Transactional
@RequestMapping(value = "/workflowactor/{workflowconfigId}/{workflowactorId}/delete", method = RequestMethod.DELETE)
public String deleteFamily(final @PathVariable("workflowconfigId") Long workflowconfigId,
		final @PathVariable("workflowactorId") Long workflowactorId,
        final ModelMap model, final HttpServletRequest request) {
	WorkflowConfig.removeActor(workflowconfigId,workflowactorId);
    return "info";
}

@Override
protected void customValidateCreate(WorkflowConfig domain,
		BindingResult result, HttpServletRequest request) {
}

@Override
protected void customValidateUpdate(WorkflowConfig domain,
		BindingResult result, HttpServletRequest request) {
}

@Override
protected void populateCreateIfNoErrors(ModelMap model,
		WorkflowConfig domain, HttpServletRequest request) {
	domain.setCreatedOn(new Date());
}

@Override
protected void populateUpdateIfNoErrors(ModelMap model,
		WorkflowConfig domain, HttpServletRequest request) {
	domain.setCreatedOn(new Date());
}
}
