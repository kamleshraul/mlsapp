package org.mkcl.els.controller.cis;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.HouseType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/committeetype")
public class CommitteeTypeController extends GenericController<CommitteeType> {

	@Override
	protected void populateNew(final ModelMap model, 
			final CommitteeType domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		this.populateHouseTypes(model, locale);
	}
	
	@Override
	protected void customValidateCreate(final CommitteeType domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUniqueness(domain, result);
	}
	
	@Override
	protected void populateEdit(final ModelMap model, 
			final CommitteeType domain,
			final HttpServletRequest request) {
		this.populateHouseTypes(model, domain.getLocale());
	}
	
	@Override
	protected void customValidateUpdate(final CommitteeType domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUniqueness(domain, result);
		this.valVersionMismatch(domain, result);	
	}
	
	//=============== INTERNAL METHODS =========
	private void populateHouseTypes(final ModelMap model,
			final String locale) {
		List<HouseType> houseTypes = 
			HouseType.findAllNoExclude("name", ApplicationConstants.ASC, locale);
		model.addAttribute("houseTypes", houseTypes);
	}
	
	//=============== VALIDATIONS =========
	private void valEmptyAndNull(final CommitteeType domain,
			final BindingResult result) {
		// 'houseType' SHOULD NOT BE NULL
		if(domain.getHouseType() == null) {
			result.rejectValue("houseType", "NotEmpty", "House Type should not be empty");
		}
		
		// 'name' SHOULD NOT BE NULL OR EMPTY
		if(domain.getName() == null || domain.getName().isEmpty()) {
			result.rejectValue("name", "NotEmpty", "Name should not be empty");
		}	
	}
	
	/**
	 * 'name' MUST UNIQUELY REPRESENT AN 'CommitteeType' INSTANCE.
	 */
	private void valInstanceUniqueness(final CommitteeType domain,
			final BindingResult result) {
		Boolean isDuplicateParam = domain.isDuplicate("name", domain.getName());
		if(isDuplicateParam) {
			result.rejectValue("name", "NonUnique", "An entry with the given name already exists");
		}
	}
	
	private void valVersionMismatch(final CommitteeType domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
}