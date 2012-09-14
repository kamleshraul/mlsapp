package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;


import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Workflow;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wf")
public class WorkflowController extends GenericController<Workflow>{
	@Override
	protected void populateNew(final ModelMap model, final Workflow domain,
            final String locale, final HttpServletRequest request) {
       domain.setLocale(locale);
       List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", domain.getLocale());
       model.addAttribute("deviceTypes",deviceTypes);
    }
	
	@Override
	protected void populateEdit(final ModelMap model, final Workflow domain,
            final HttpServletRequest request) {
			List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", domain.getLocale());
	       model.addAttribute("deviceTypes",deviceTypes);

    }
}
