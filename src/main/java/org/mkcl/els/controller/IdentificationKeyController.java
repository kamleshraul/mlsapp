package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.IdentificationKey;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/identificationkey")
public class IdentificationKeyController extends GenericController<IdentificationKey>{
	
	@Override
	protected void customValidateCreate(final IdentificationKey domain,
            final BindingResult result, final HttpServletRequest request) {
        
    }
	
	@Override
	protected void customValidateUpdate(final IdentificationKey domain,
            final BindingResult result, final HttpServletRequest request) {
        
    }

}
