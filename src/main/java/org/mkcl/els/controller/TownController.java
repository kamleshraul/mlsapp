package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Town;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/town")
public class TownController extends GenericController<Town> {

	@Override
	protected void populateNew(final ModelMap model, 
			final Town domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		List<State> states = this.populateStates(model, locale);
		
		State state = states.get(0);
		this.populateDistricts(model, state, locale);
	}
	
	@Override
	protected void customValidateCreate(final Town domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceCreationUniqueness(domain, result);
	}
	
	@Override
	protected void populateEdit(final ModelMap model, 
			final Town domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		
		this.populateStates(model, locale);
		
		District district = domain.getDistrict();
		State state = State.find(district, locale);
		this.populateState(model, state);
		this.populateDistricts(model, state, locale);
	}
	
	@Override
	protected void customValidateUpdate(final Town domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUpdationUniqueness(domain, result);
		this.valVersionMismatch(domain, result);
	}

	//=============== INTERNAL METHODS =========
	private void populateState(ModelMap model, State state) {
		model.addAttribute("state", state);		
	}

	private List<State> populateStates(final ModelMap model, 
			final String locale) {
		List<State> states = State.find(locale);
		model.addAttribute("states", states);
		return states;
	}
	
	private void populateDistricts(final ModelMap model, 
			final State state, 
			final String locale) {
		Long stateId = state.getId();
		
		try {
			List<District> districts = 
				District.findDistrictsByStateId(stateId, "name", 
						ApplicationConstants.ASC, locale);
			model.addAttribute("districts", districts);
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
		
	}
	
	//=============== VALIDATIONS ==============
	private void valEmptyAndNull(final Town domain, 
			final BindingResult result) {
		// 'district' SHOULD NOT BE NULL
		if(domain.getDistrict() == null) {
			result.rejectValue("district", "NotEmpty", 
					"District should not be empty");
		}
		
		// 'name' SHOULD NOT BE NULL OR EMPTY
		if(domain.getName() == null || domain.getName().isEmpty()) {
			result.rejectValue("name", "NotEmpty", "Name should not be empty");
		}
	}
	
	/**
	 * 'name' + 'district' MUST UNIQUELY REPRESENT AN 'TOWN' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while creating an instance.
	 */
	private void valInstanceCreationUniqueness(final Town domain, 
			final BindingResult result) {
		if(domain.getName() != null && domain.getDistrict() != null) {
			Town town = 
				Town.find(domain.getName(), domain.getDistrict(), 
						domain.getLocale());
			if(town != null) {
				Object[] errorArgs = new Object[] {town.getName(), 
						town.getDistrict().getName()};
				
				StringBuffer defaultMessage = new StringBuffer();
				defaultMessage.append("Town: ");
				defaultMessage.append(errorArgs[0]);
				defaultMessage.append(" already exists for District: ");
				defaultMessage.append(errorArgs[1]);
				
				result.rejectValue("name", "DuplicateTown", errorArgs, 
						defaultMessage.toString());
			}
		}
	}
	
	/**
	 * 'name' + 'district' MUST UNIQUELY REPRESENT AN 'TOWN' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while updating an instance.
	 */
	private void valInstanceUpdationUniqueness(final Town domain, 
			final BindingResult result) {
		if(domain.getName() != null && domain.getDistrict() != null) {
			Town town = 
				Town.find(domain.getName(), domain.getDistrict(), 
						domain.getLocale());
			if(town != null) {
				Long domainId = domain.getId();
				Long townId = town.getId();
				if(! domainId.equals(townId)) {
					Object[] errorArgs = new Object[] {town.getName(), 
							town.getDistrict().getName()};
					
					StringBuffer defaultMessage = new StringBuffer();
					defaultMessage.append("Town: ");
					defaultMessage.append(errorArgs[0]);
					defaultMessage.append(" already exists for District: ");
					defaultMessage.append(errorArgs[1]);
					
					result.rejectValue("name", "DuplicateTown", errorArgs, 
							defaultMessage.toString());
				}
			}
		}
	}
	
	private void valVersionMismatch(final Town domain, 
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
}