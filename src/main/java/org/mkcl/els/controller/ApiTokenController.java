package org.mkcl.els.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.ApiToken;
import org.mkcl.els.domain.PushMessage;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class JwtTokenController.
 * 
 * @author shubhama
 * @author
 * @since v1.0.0
 */

@Controller
@RequestMapping("/apitoken")

public class ApiTokenController extends GenericController<ApiToken> {

	@Autowired
	JwtServiceImpl jwtService;
	

	@Override
	protected void preValidateCreate(final ApiToken domain,
			final BindingResult result, final HttpServletRequest request) {
		if(domain.getClientName().isEmpty())
		{
			result.rejectValue(" Client Name", " Empty");
		}
		if(domain.getClientName()== null)
		{
			result.rejectValue(" Client Name", " Empty");
		}
		if(domain.getFromDate()==null)
		{
			result.rejectValue(" Client Name", " Empty");
		}
		if(domain.getToDate()==null)
		{
			result.rejectValue(" Client Name", " Empty");
		}
		
		
		
	}
	
	
	
	@Override
	protected void populateAfterCreate(ModelMap model, ApiToken domain,
			HttpServletRequest request) {
		String generatedToken = jwtService.generateToken(domain);
		domain.setToken(generatedToken);
	}
	
	

	@Override
	protected void preValidateUpdate(final ApiToken domain, final BindingResult result,
			final HttpServletRequest request) {

	}

	@Override
	protected void customValidateCreate(final ApiToken domain, final BindingResult result,
			final HttpServletRequest request) {

	}

	@Override
	protected void customValidateUpdate(final ApiToken domain, final BindingResult result,
			final HttpServletRequest request) {

	}

}
