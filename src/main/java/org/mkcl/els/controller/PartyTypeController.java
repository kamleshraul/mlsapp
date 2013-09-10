package org.mkcl.els.controller;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.PartyType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("partytype")
public class PartyTypeController extends GenericController<PartyType> {

	@Override
	protected void customValidateCreate(final PartyType domain, 
			final BindingResult result,
			final HttpServletRequest request) {
		valEmptyAndNull(domain, result);
		valInstanceUniqueness(domain, result);
	}
	
	@Override
	protected void customValidateUpdate(final PartyType domain, 
			final BindingResult result,
			final HttpServletRequest request) {
		valEmptyAndNull(domain, result);
		valInstanceUniqueness(domain, result);
		valVersionMismatch(domain, result);
	}
	
	//=============== VALIDATIONS =========
	private void valEmptyAndNull(final PartyType domain,
			final BindingResult result) {
		// 'name' SHOULD NOT BE NULL OR EMPTY
		if(domain.getName() == null || domain.getName().isEmpty()) {
			result.rejectValue("name", "NotEmpty", "Name should not be empty");
		}	
	}
	
	/**
	 * 'name' MUST UNIQUELY REPRESENT AN 'PartyType' INSTANCE.
	 */
	private void valInstanceUniqueness(final PartyType domain,
			final BindingResult result) {
		Boolean isDuplicateParam = domain.isDuplicate("name", domain.getName());
		if(isDuplicateParam) {
			result.rejectValue("name", "NonUnique", "An entry with the given name already exists");
		}
	}
	
	private void valVersionMismatch(final PartyType domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
}