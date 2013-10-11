package org.mkcl.els.controller.cis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CommitteeTour;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Town;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/committeetour")
public class CommitteeTourController extends GenericController<CommitteeTour> {

	@Override
	protected void populateNew(final ModelMap model, 
			final CommitteeTour domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		List<State> states = this.populateStates(model, locale);
		
		State state = states.get(0);
		List<District> districts = this.populateDistricts(model, state, locale);
		
		District district = districts.get(0);
		this.populateTowns(model, district, locale);
	}
	
	@Override
	protected void customValidateCreate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceCreationUniqueness(domain, result);
	}

	@Override
	protected void populateEdit(final ModelMap model, 
			final CommitteeTour domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		
		Town town = domain.getTown();
		District district = District.find(town, locale);
		State state = State.find(district, locale);
		
		this.populateStates(model, locale);
		this.populateState(model, state);
		
		this.populateDistricts(model, state, locale);
		this.populateDistrict(model, district);
		
		this.populateTowns(model, district, locale);
	}
	
	@Override
	protected void customValidateUpdate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUpdationUniqueness(domain, result);
		this.valVersionMismatch(domain, result);
	}

	//=============== INTERNAL METHODS =========
	private void populateState(final ModelMap model, 
			final State state) {
		model.addAttribute("state", state);		
	}
	
	private List<State> populateStates(final ModelMap model, 
			final String locale) {
		List<State> states = State.find(locale);
		model.addAttribute("states", states);
		return states;
	}
	
	private void populateDistrict(final ModelMap model, 
			final District district) {
		model.addAttribute("district", district);		
	}
	
	private List<District> populateDistricts(final ModelMap model, 
			final State state, 
			final String locale) {
		Long stateId = state.getId();
		
		try {
			List<District> districts = 
				District.findDistrictsByStateId(stateId, "name", 
						ApplicationConstants.ASC, locale);
			model.addAttribute("districts", districts);
			return districts;
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<District>();
		
	}
	
	private void populateTowns(final ModelMap model, 
			final District district, 
			final String locale) {
		List<Town> towns = Town.find(district, locale);
		model.addAttribute("towns", towns);
	}
	
	//=============== VALIDATIONS ==============
	private void valEmptyAndNull(final CommitteeTour domain, 
			final BindingResult result) {
		// 'town' SHOULD NOT BE NULL
		if(domain.getTown() == null) {
			result.rejectValue("town", "NotEmpty", 
					"Town should not be empty");
		}
		
		// 'venueName' SHOULD NOT BE NULL OR EMPTY
		if(domain.getVenueName() == null || domain.getVenueName().isEmpty()) {
			result.rejectValue("venueName", "NotEmpty", 
					"Venue Name should not be empty");
		}
		
		// 'fromDate' SHOULD NOT BE NULL OR EMPTY
		if(domain.getFromDate() == null) {
			result.rejectValue("fromDate", "NotEmpty", 
					"From Date should not be empty");
		}

		// 'toDate' SHOULD NOT BE NULL OR EMPTY
		if(domain.getToDate() == null) {
			result.rejectValue("toDate", "NotEmpty", 
					"To Date should not be empty");
		}
		
		// 'subject' SHOULD NOT BE NULL OR EMPTY
		if(domain.getSubject() == null || domain.getSubject().isEmpty()) {
			result.rejectValue("subject", "NotEmpty", 
					"Subject should not be empty");
		}
	}
	
	/**
	 * 'town' + 'venueName' + 'fromDate' + 'toDate' + 'subject' MUST 
	 * UNIQUELY REPRESENT AN 'CommitteeTour' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while creating an instance.
	 */
	private void valInstanceCreationUniqueness(final CommitteeTour domain,
			final BindingResult result) {
		Town town = domain.getTown();
		String venueName = domain.getVenueName();
		Date fromDate = domain.getFromDate();
		Date toDate = domain.getToDate();
		String subject = domain.getSubject();
		String locale = domain.getLocale();
		
		if(town != null && venueName != null && fromDate != null
				&& toDate != null && subject != null) {
			CommitteeTour tour = CommitteeTour.find(town, venueName, 
					fromDate, toDate, subject, locale);
			if(tour != null) {
				Object[] errorArgs = new Object[] {town.getName(), 
						venueName};
				
				StringBuffer defaultMessage = new StringBuffer();
				defaultMessage.append("Committee Tour ");
				defaultMessage.append(" already exists for Town: ");
				defaultMessage.append(errorArgs[0]);
				defaultMessage.append(", Venue Name: ");
				defaultMessage.append(errorArgs[1]);
				
				result.rejectValue("venueName", "DuplicateCommitteeTour", 
						errorArgs, defaultMessage.toString());
			}
		}
	}
	
	/**
	 * 'town' + 'venueName' + 'fromDate' + 'toDate' + 'subject' MUST 
	 * UNIQUELY REPRESENT AN 'CommitteeTour' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while updating an instance.
	 */
	private void valInstanceUpdationUniqueness(final CommitteeTour domain,
			final BindingResult result) {
		Town town = domain.getTown();
		String venueName = domain.getVenueName();
		Date fromDate = domain.getFromDate();
		Date toDate = domain.getToDate();
		String subject = domain.getSubject();
		String locale = domain.getLocale();
		
		if(town != null && venueName != null && fromDate != null
				&& toDate != null && subject != null) {
			CommitteeTour tour = CommitteeTour.find(town, venueName, 
					fromDate, toDate, subject, locale);
			if(tour != null) {
				Long domainId = domain.getId();
				Long tourId = tour.getId();
				if(! domainId.equals(tourId)) {
					Object[] errorArgs = new Object[] {town.getName(), 
							venueName};
					
					StringBuffer defaultMessage = new StringBuffer();
					defaultMessage.append("Committee Tour ");
					defaultMessage.append(" already exists for Town: ");
					defaultMessage.append(errorArgs[0]);
					defaultMessage.append(", Venue Name: ");
					defaultMessage.append(errorArgs[1]);
					
					result.rejectValue("venueName", "DuplicateCommitteeTour", 
							errorArgs, defaultMessage.toString());
				}
			}
		}
	}
	
	private void valVersionMismatch(final CommitteeTour domain, 
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
}