package org.mkcl.els.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowActor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflowactor")
public class WorkflowActorController extends GenericController<WorkflowActor>{

	@Override
	protected void populateNew(final ModelMap model, final WorkflowActor domain,
            final String locale, final HttpServletRequest request) {
       domain.setLocale(locale);
       List<UserGroup> userGroups=UserGroup.findAll(UserGroup.class, "name", "desc", domain.getLocale());
       model.addAttribute("userGroups",userGroups);
    }
	
	@Override
	protected void populateEdit(final ModelMap model, final WorkflowActor domain,
            final HttpServletRequest request) {
		List<UserGroup> userGroups=UserGroup.findAll(UserGroup.class, "name", "desc", domain.getLocale());
	       model.addAttribute("userGroups",userGroups);

    }
	
	@Override
	protected void customValidateCreate(final WorkflowActor domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	@Override
	protected void customValidateUpdate(final WorkflowActor domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	private void customValidate(final WorkflowActor domain,
			final BindingResult result, final HttpServletRequest request) {
		new HashMap<String, String>();
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		if (result.hasErrors()) {
			System.out.println("error");
		}
	}
}
