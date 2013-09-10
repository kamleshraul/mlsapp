package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseParty;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.PartyType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/houseparty")
public class HousePartyController extends GenericController<HouseParty> {

	@Override
	protected void populateNew(final ModelMap model, 
			final HouseParty domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		List<HouseType> houseTypes = this.populateHouseTypes(model, locale);
		
		HouseType houseType = houseTypes.get(0);
		this.populateHouses(model, houseType, locale);
		
		this.populatePartyTypes(model, locale);
		
		List<Party> partyMaster = this.populatePartyMaster(model, locale);
		this.populateParties(model, request, domain, partyMaster);
	}
	
	@Override
	protected void customValidateCreate(final HouseParty domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valFromDateBeforeToDate(domain, result);
		this.valInstanceCreationUniqueness(domain, result);
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, 
			final HouseParty domain,
			final HttpServletRequest request) {
		List<Party> parties = this.retrieveParties(request, domain);
		domain.setParties(parties);
	}
	
	@Override
	protected void populateEdit(final ModelMap model, 
			final HouseParty domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		
		this.populateHouseTypes(model, locale);
		
		HouseType houseType = domain.getHouse().getType();
		this.populateHouseType(model, houseType);
		this.populateHouses(model, houseType, locale);
		
		this.populatePartyTypes(model, locale);
		
		List<Party> partyMaster = this.populatePartyMaster(model, locale);
		this.populateParties(model, request, domain, partyMaster);
	}
	
	@Override
	protected void customValidateUpdate(final HouseParty domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valFromDateBeforeToDate(domain, result);
		this.valInstanceUpdationUniqueness(domain, result);
		this.valVersionMismatch(domain, result);
	}
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, 
			final HouseParty domain,
			final HttpServletRequest request) {
		List<Party> parties = this.retrieveParties(request, domain);
		domain.setParties(parties);
	}

	//=============== INTERNAL METHODS =========
	private void populateHouseType(final ModelMap model, 
			final HouseType houseType) {
		model.addAttribute("houseType", houseType);
	}
	
	private List<HouseType> populateHouseTypes(final ModelMap model,
			final String locale) {
		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", 
				ApplicationConstants.ASC, locale);
		model.addAttribute("houseTypes", houseTypes);
		return houseTypes;
	}
	
	private void populateHouses(final ModelMap model,
			final HouseType houseType,
			final String locale) {
		String houseTypeType = houseType.getType();
		List<House> houses = House.findByHouseType(houseTypeType, locale);
		model.addAttribute("houses", houses);
	}
	
	private void populatePartyTypes(final ModelMap model,
			final String locale) {
		List<PartyType> partyTypes = PartyType.findAll(PartyType.class, "name", 
				ApplicationConstants.ASC, locale);
		model.addAttribute("partyTypes", partyTypes);
	}
	
	private List<Party> populatePartyMaster(final ModelMap model,
			final String locale) {
		List<Party> parties = Party.findActiveParties(locale);
		model.addAttribute("partiesMaster", parties);
		return parties;
	}
	
	private void populateParties(final ModelMap model,
			final HttpServletRequest request,
			final HouseParty domain,
			final List<Party> partyMaster) {
		List<Party> parties = this.retrieveParties(request, domain);
		model.addAttribute("parties", parties);
		
		if(parties.isEmpty()) {
			model.addAttribute("allParties", partyMaster);
		}
		else {
			List<Party> allParties = new ArrayList<Party>();
			for(Party pm : partyMaster) {
				boolean set = true;
				for(Party p : parties) {
					if(p.getId().equals(pm.getId())) {
						set = false;
						break;
					}
				}
				if(set == true) {
					allParties.add(pm);
				}
			}
			model.addAttribute("allParties", allParties);
		}	
	}
	
	/**
	 * Query the @param request for parties. If @param request does not 
	 * contain parties, then retrieve parties from @param domain
	 */
	private List<Party> retrieveParties(final HttpServletRequest request,
			final HouseParty domain) {
		List<Party> parties = new ArrayList<Party>();
		
		String[] strParties = request.getParameterValues("parties");
		if(strParties != null) {
			int length = strParties.length;
			for(int i = 0; i < length; i++) {
				Long id = Long.parseLong(strParties[i]);
				Party party = Party.findById(Party.class, id);
				parties.add(party);
			}
		}
		else if(domain.getParties() != null) {
			parties.addAll(domain.getParties());
		}
		
		return parties;
	}
	
	private String formatDateToString(final Date date,
			final String locale) {
		String dateFormat = this.getServerDateFormat();
		String strDate = FormaterUtil.formatDateToString(date, 
				dateFormat, locale);
		return strDate;
	}
	
	private String getServerDateFormat() {
		CustomParameter serverDateFormat = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		return serverDateFormat.getValue();
	}
	
	//=============== VALIDATIONS =========
	private void valEmptyAndNull(final HouseParty domain, 
			final BindingResult result) {
		// 'house' SHOULD NOT BE NULL
		if(domain.getHouse() == null) {
			result.rejectValue("house", "NotEmpty", 
					"House should not be empty");
		}
		
		// 'partyType' SHOULD NOT BE NULL
		if(domain.getPartyType() == null) {
			result.rejectValue("partyType", "NotEmpty", 
					"Party Type should not be empty");
		}
		
		// 'fromDate' SHOULD NOT BE EMPTY
		if(domain.getFromDate() == null) {
			result.rejectValue("fromDate", "NotEmpty", 
					"From Date should not be empty");
		}
		
		// 'parties' SHOULD NOT BE NULL OR EMPTY
		List<Party> parties = domain.getParties();
		if(parties == null || parties.isEmpty()) {
			result.rejectValue("parties", "NotEmpty", 
					"Parties should not be empty");
		}
	}
	
	private void valFromDateBeforeToDate(final HouseParty domain, 
			final BindingResult result) {
		Date fromDate = domain.getFromDate();
		Date toDate = domain.getToDate();
		
		if(fromDate != null && toDate != null) {
			if(toDate.before(fromDate)) {
				result.rejectValue("toDate", "ToDateBeforeFromDate", 
						"To date cannot be set prior to From date");
			}
		}
	}
	
	/**
	 * 'house' + 'partyType' + 'fromDate' MUST UNIQUELY REPRESENT AN 
	 * 'HouseParty' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while creating an instance.
	 */
	private void valInstanceCreationUniqueness(final HouseParty domain,
			final BindingResult result) {
		House house = domain.getHouse();
		PartyType partyType = domain.getPartyType();
		Date fromDate = domain.getFromDate();
		String locale = domain.getLocale();
		
		if(house != null && partyType != null && fromDate != null) {
			HouseParty houseParty = 
				HouseParty.find(house, partyType, fromDate, locale);
			if(houseParty != null) {
				String strFromDate = this.formatDateToString(fromDate, locale);
				
				Object[] errorArgs = new Object[] {
						houseParty.getHouse().getDisplayName(), 
						houseParty.getPartyType().getName(),
						strFromDate};
				
				StringBuffer defaultMessage = new StringBuffer();
				defaultMessage.append("An entry already exists for");
				defaultMessage.append(" House: ");
				defaultMessage.append(errorArgs[0]);
				defaultMessage.append(", Party Type: ");
				defaultMessage.append(errorArgs[1]);
				defaultMessage.append(", From Date: ");
				defaultMessage.append(errorArgs[2]);
				
				result.rejectValue("name", "DuplicateHouseParty", errorArgs, 
						defaultMessage.toString());
			}
		}
	}
	
	/**
	 * 'house' + 'partyType' + 'fromDate' MUST UNIQUELY REPRESENT AN 
	 * 'HouseParty' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule 
	 * while updating the instance.
	 */
	private void valInstanceUpdationUniqueness(final HouseParty domain,
			final BindingResult result) {
		House house = domain.getHouse();
		PartyType partyType = domain.getPartyType();
		Date fromDate = domain.getFromDate();
		String locale = domain.getLocale();
		
		if(house != null && partyType != null && fromDate != null) {
			HouseParty houseParty = 
				HouseParty.find(house, partyType, fromDate, locale);
			if(houseParty != null) {
				Long domainId = domain.getId();
				Long housePartyId = houseParty.getId();
				if(! domainId.equals(housePartyId)) {
					String strFromDate = 
						this.formatDateToString(fromDate, locale);
					
					Object[] errorArgs = new Object[] {
							houseParty.getHouse().getDisplayName(), 
							houseParty.getPartyType().getName(),
							strFromDate};
					
					StringBuffer defaultMessage = new StringBuffer();
					defaultMessage.append("An entry already exists for");
					defaultMessage.append(" House: ");
					defaultMessage.append(errorArgs[0]);
					defaultMessage.append(" , Party Type: ");
					defaultMessage.append(errorArgs[1]);
					defaultMessage.append(" , From Date: ");
					defaultMessage.append(errorArgs[2]);
					
					result.rejectValue("name", "DuplicateHouseParty", errorArgs, 
							defaultMessage.toString());
				}
			}
		}
	}
	
	private void valVersionMismatch(final HouseParty domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
}