package org.mkcl.els.controller.cis;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.NonCommitteeMemberType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("noncommitteemembertype")
public class NonCommitteeMemberTypeController 
	extends GenericController<NonCommitteeMemberType> {

	@Override
	protected void customValidateCreate(final NonCommitteeMemberType domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUniqueness(domain, result);
	}

	@Override
	protected void customValidateUpdate(final NonCommitteeMemberType domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUniqueness(domain, result);
		this.valVersionMismatch(domain, result);	
	}

	//=============== INTERNAL METHODS =========
	private void valInstanceUniqueness(final NonCommitteeMemberType domain,
			final BindingResult result) {
		// 'name' SHOULD NOT BE NULL OR EMPTY
		if(domain.getName() == null || domain.getName().isEmpty()) {
			result.rejectValue("name", "NotEmpty", "Name should not be empty");
		}
	}

	/**
	 * 'name' MUST UNIQUELY REPRESENT AN 'NonCommitteeMemberType' INSTANCE.
	 */
	private void valEmptyAndNull(final NonCommitteeMemberType domain,
			final BindingResult result) {
		Boolean isDuplicateParam = domain.isDuplicate("name", domain.getName());
		if(isDuplicateParam) {
			result.rejectValue("name", "NonUnique", 
					"An entry with the given name already exists");
		}
	}
	
	private void valVersionMismatch(final NonCommitteeMemberType domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
}