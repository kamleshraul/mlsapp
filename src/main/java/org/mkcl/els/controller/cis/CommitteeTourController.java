package org.mkcl.els.controller.cis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CommitteeReporter;
import org.mkcl.els.domain.CommitteeTour;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.TourItinerary;
import org.mkcl.els.domain.Town;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/committeetour")
public class CommitteeTourController extends GenericController<CommitteeTour> {

	@Override
	protected void populateNew(final ModelMap model, 
			final CommitteeTour domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		
		// States
		List<State> states = this.populateStates(model, locale);
		
		// Districts
		State state = states.get(0);
		List<District> districts = this.populateDistricts(model, state, locale);
		
		// Towns
		District district = districts.get(0);
		this.populateTowns(model, district, locale);
		
		// Tour Itineraries
		this.populateItinerariesCount(model, 0);
		
		// Languages
		this.populateLanguages(model, locale);
		
		// Reporters
		this.populateReportersCount(model, 0);
	}
	
	@Override
	protected void preValidateCreate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.setTourItineraries(domain, request);
		this.setCommitteeReporters(domain, request);
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
		
		// States, Districts, & Towns
		Town town = domain.getTown();
		District district = District.find(town, locale);
		State state = State.find(district, locale);
		
		this.populateStates(model, locale);
		this.populateState(model, state);
		
		this.populateDistricts(model, state, locale);
		this.populateDistrict(model, district);
		
		this.populateTowns(model, district, locale);
		
		// Tour Itineraries
		List<TourItinerary> itineraries = domain.getItineraries();
		this.populateItineraries(model, itineraries);
		this.populateItinerariesCount(model, itineraries.size());
		
		// Languages
		this.populateLanguages(model, locale);
		
		// Reporters
		List<CommitteeReporter> reporters = domain.getReporters();
		this.populateReporters(model, reporters);
		this.populateReportersCount(model, reporters.size());
	}
	
	@Override
	protected void preValidateUpdate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.setTourItineraries(domain, request);
		this.setCommitteeReporters(domain, request);
	}
	
	@Override
	protected void customValidateUpdate(final CommitteeTour domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valInstanceUpdationUniqueness(domain, result);
		this.valVersionMismatch(domain, result);
	}
	
	@RequestMapping(value="{id}/view", method=RequestMethod.GET)
	public String view(final ModelMap model, 
			@PathVariable("id") final Long id,
			final Locale localeObj) {
		String locale = localeObj.toString();
		
		CommitteeTour tour = CommitteeTour.findById(CommitteeTour.class, id);
		Town town = tour.getTown();
		District district = District.find(town, locale);
		State state = State.find(district, locale);
		
		model.addAttribute("id", tour.getId());
		model.addAttribute("state", state.getName());
		model.addAttribute("district", district.getName());
		model.addAttribute("town", town.getName());
		model.addAttribute("venueName", tour.getVenueName());
		
		String fromDate = FormaterUtil.formatDateToString(
				tour.getFromDate(), 
				this.getServerDateFormat(), 
				locale.toString());
		model.addAttribute("fromDate", fromDate);
		
		String toDate = FormaterUtil.formatDateToString(
				tour.getToDate(), 
				this.getServerDateFormat(),
				locale.toString());
		model.addAttribute("toDate", toDate);

		List<TourItinerary> itineraries = tour.getItineraries();
		this.populateItineraries(model, itineraries);
		
		List<CommitteeReporter> reporters = tour.getReporters();
		this.populateReporters(model, reporters);

		return "committeetour/view";
	}
	
	@RequestMapping(value="/touritinerary/{id}/delete", 
			method=RequestMethod.DELETE)
	public String deleteTourItinerary(final @PathVariable("id") Long id) {
	    TourItinerary tourItinerary = 
	    	TourItinerary.findById(TourItinerary.class, id);
	    tourItinerary.remove();
	    return "info";
	}
	
	@RequestMapping(value="/committeereporter/{id}/delete", 
			method=RequestMethod.DELETE)
	public String deleteCommitteeReporter(final @PathVariable("id") Long id) {
		CommitteeReporter committeeReporter = 
			CommitteeReporter.findById(CommitteeReporter.class, id);
		committeeReporter.remove();
	    return "info";
	}
	
	//==========================================
	//	WORKFLOW METHODS
	//==========================================
	

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
	
	private void populateItineraries(final ModelMap model,
			final List<TourItinerary> itineraries) {
		model.addAttribute("itineraries", itineraries);
	}
	
	private void populateItinerariesCount(final ModelMap model,
			final Integer tourItineraryCount) {
		model.addAttribute("tourItineraryCount", tourItineraryCount);
	}
	
	private void populateReporters(final ModelMap model,
			final List<CommitteeReporter> reporters) {
		model.addAttribute("reporters", reporters);
	}
	
	private void populateReportersCount(final ModelMap model,
			final Integer committeeReporterCount) {
		model.addAttribute("committeeReporterCount", committeeReporterCount);
	}
	
	private void populateLanguages(final ModelMap model,
			final String locale) {
		List<Language> languages = new ArrayList<Language>();
		try {
			languages = Language.findAllSortedByPriorityAndName(locale);
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
		model.addAttribute("languages", languages);
	}
	
	private void setCommitteeReporters(final CommitteeTour domain,
			final HttpServletRequest request) {
		List<CommitteeReporter> reporters = new ArrayList<CommitteeReporter>();
		
		Integer committeeReporterCount =
			Integer.parseInt(request.getParameter("committeeReporterCount"));
		for(int i = 1; i <= committeeReporterCount; i++) {
			String strLanguage = 
				request.getParameter("committeeReporterLanguage" + i);
			if(strLanguage != null && ! strLanguage.isEmpty()) {
				CommitteeReporter reporter = new CommitteeReporter();
				
				Long languageId = Long.parseLong(strLanguage);
				Language language = 
					Language.findById(Language.class, languageId);
				reporter.setLanguage(language);
				
				String strId = request.getParameter("committeeReporterId" + i);
				if(strId != null && ! strId.isEmpty()){
					Long id = Long.parseLong(strId);
					reporter.setId(id);
				}

				String locale = 
					request.getParameter("committeeReporterLocale" + i);
				if(locale != null && ! locale.isEmpty()){
					reporter.setLocale(locale);
				}
				
				String strVersion = 
					request.getParameter("committeeReporterVersion" + i);
				if(strVersion != null && ! strVersion.isEmpty()){
					Long version = Long.parseLong(strVersion);
					reporter.setVersion(version);
				}
	
				String strNoOfReporters = 
					request.getParameter("committeeReporterNoOfReporters" + i);
				if(strNoOfReporters != null && ! strNoOfReporters.isEmpty()){
					Integer noOfReporters = Integer.parseInt(strNoOfReporters);
					reporter.setNoOfReporters(noOfReporters);
				}
				
				reporters.add(reporter);
			}
		}
		
		domain.setReporters(reporters);
	}

	private void setTourItineraries(final CommitteeTour domain,
			final HttpServletRequest request) {
		List<TourItinerary> itineraries = new ArrayList<TourItinerary>();
		
		Integer tourItineraryCount =
			Integer.parseInt(request.getParameter("tourItineraryCount"));
		for(int i = 1; i <= tourItineraryCount; i++) {
			String strDate = 
				request.getParameter("tourItineraryDate" + i);
			if(strDate != null && ! strDate.isEmpty()) {
				TourItinerary itinerary = new TourItinerary();

				Date date = FormaterUtil.formatStringToDate(strDate, 
						this.getServerDateFormat());
				itinerary.setDate(date);				 
				
				String strId = request.getParameter("tourItineraryId" + i);
				if(strId != null && ! strId.isEmpty()){
					Long id = Long.parseLong(strId);
					itinerary.setId(id);
				}

				String locale = request.getParameter("tourItineraryLocale" + i);
				if(locale != null && ! locale.isEmpty()){
					itinerary.setLocale(locale);
				}
				
				String strVersion = 
					request.getParameter("tourItineraryVersion" + i);
				if(strVersion != null && ! strVersion.isEmpty()){
					Long version = Long.parseLong(strVersion);
					itinerary.setVersion(version);
				}
				
				String fromTime = 
					request.getParameter("tourItineraryFromTime" + i);
				if(fromTime != null && ! fromTime.isEmpty()){
					itinerary.setFromTime(fromTime);
				}
				
				String toTime = 
					request.getParameter("tourItineraryToTime" + i);
				if(toTime != null && ! toTime.isEmpty()){
					itinerary.setToTime(toTime);
				}
				
				String details = 
					request.getParameter("tourItineraryDetails" + i);
				if(details != null && ! details.isEmpty()){
					itinerary.setDetails(details);
				}
				
				String stayOver = 
					request.getParameter("tourItineraryStayover" + i);
				if(stayOver != null && ! stayOver.isEmpty()){
					itinerary.setStayOver(stayOver);
				}
				
				itineraries.add(itinerary);
			}
		}
		
		domain.setItineraries(itineraries);
	}
	
	private String getServerDateFormat() {
		CustomParameter serverDateFormat = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		return serverDateFormat.getValue();
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