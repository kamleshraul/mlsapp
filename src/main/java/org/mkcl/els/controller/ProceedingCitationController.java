package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.ProceedingCitation;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/proceedingcitation")
public class ProceedingCitationController extends GenericController<ProceedingCitation>{


	@Override
	 protected void customValidateCreate(final ProceedingCitation domain,
	            final BindingResult result, final HttpServletRequest request) {
	        
	 }
	@Override
	 protected void customValidateUpdate(final ProceedingCitation domain,
	            final BindingResult result, final HttpServletRequest request) {
	        
	 }
}
