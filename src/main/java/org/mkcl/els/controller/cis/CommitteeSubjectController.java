package org.mkcl.els.controller.cis;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CommitteeSubject;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("committeesubject")
public class CommitteeSubjectController 
	extends GenericController<CommitteeSubject> {
	
	@Override
	protected void customValidateCreate(final CommitteeSubject domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUniqueness(domain, result);
	}

	@Override
	protected void customValidateUpdate(final CommitteeSubject domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUniqueness(domain, result);
		this.valVersionMismatch(domain, result);	
	}

	//=============== INTERNAL METHODS =========
	private void valInstanceUniqueness(final CommitteeSubject domain,
			final BindingResult result) {
		// 'name' SHOULD NOT BE NULL OR EMPTY
		if(domain.getName() == null || domain.getName().isEmpty()) {
			result.rejectValue("name", "NotEmpty", "Name should not be empty");
		}
	}

	/**
	 * 'name' MUST UNIQUELY REPRESENT AN 'CommitteeSubject' INSTANCE.
	 */
	private void valEmptyAndNull(final CommitteeSubject domain,
			final BindingResult result) {
		Boolean isDuplicateParam = domain.isDuplicate("name", domain.getName());
		if(isDuplicateParam) {
			result.rejectValue("name", "NonUnique", 
					"An entry with the given name already exists");
		}
	}
	
	private void valVersionMismatch(final CommitteeSubject domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}

}