package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.AdjournmentReason;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("adjournmentreason")
public class AdjournmentReasonController extends GenericController<AdjournmentReason>{

	@Override
	protected void customValidateCreate(AdjournmentReason domain,
			BindingResult result, HttpServletRequest request) {
		 // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
	@Override
	protected void customValidateUpdate(AdjournmentReason domain,
			BindingResult result, HttpServletRequest request) {
		 // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
}
