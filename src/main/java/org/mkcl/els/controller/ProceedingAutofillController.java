package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.ProceedingAutofill;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/proceedingautofill")
public class ProceedingAutofillController extends GenericController<ProceedingAutofill>{
	
	@Override 
	protected void populateModule(final ModelMap model,
            final HttpServletRequest request, final String locale,
            final AuthUser currentUser) {
		 String username = this.getCurrentUser().getActualUsername();
	     model.addAttribute("username",username);
    }
	
	@Override
	protected void populateNew(final ModelMap model, 
	    		final ProceedingAutofill domain,
	            final String locale, 
	            final HttpServletRequest request) {
	       domain.setLocale(locale);
	       String username = this.getCurrentUser().getActualUsername();
	       model.addAttribute("username",username);
	       
	 }
	
	   @Override
	    protected void customValidateCreate(final ProceedingAutofill domain,
	                                        final BindingResult result,
	                                        final HttpServletRequest request) {
	        
	    }


	    @Override
	    protected void customValidateUpdate(final ProceedingAutofill domain,
	                                        final BindingResult result,
	                                        final HttpServletRequest request) {
	      
	    }
	
	

}
