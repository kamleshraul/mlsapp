package org.mkcl.els.controller.cis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/committeename")
public class CommitteeNameController extends GenericController<CommitteeName> {
	
	@Override
	protected void populateNew(final ModelMap model, 
			final CommitteeName domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		List<HouseType> houseTypes = this.populateHouseTypes(model, locale);
		
		HouseType houseType = houseTypes.get(0);
		this.populateCommitteeTypes(model, houseType, locale);
	}
	
	@Override
	protected void customValidateCreate(final CommitteeName domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valDuration(domain, result);
		this.valMemberCount(domain, result);
		this.valInstanceCreationUniqueness(domain, result);
		this.valRules(domain, result);
	}
	
	@Override
	protected void populateEdit(final ModelMap model, 
			final CommitteeName domain,
			final HttpServletRequest request) {
		this.populateHouseTypes(model, domain.getLocale());
		
		HouseType houseType = domain.getCommitteeType().getHouseType();
		this.populateHouseType(model, houseType);
		this.populateCommitteeTypes(model, houseType, domain.getLocale());
	}

	@Override
	protected void customValidateUpdate(final CommitteeName domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valDuration(domain, result);
		this.valMemberCount(domain, result);
		this.valInstanceUpdationUniqueness(domain, result);
		this.valRules(domain, result);
		this.valVersionMismatch(domain, result);
	}
	
	//=============== INTERNAL METHODS =========
	private void populateHouseType(final ModelMap model, 
			final HouseType houseType) {
		model.addAttribute("houseType", houseType);
	}
	
	private List<HouseType> populateHouseTypes(final ModelMap model,
			final String locale) {
		List<HouseType> houseTypes = 
			HouseType.findAllNoExclude("name", ApplicationConstants.ASC, locale);
		model.addAttribute("houseTypes", houseTypes);
		return houseTypes;
	}
	
	private void populateCommitteeTypes(final ModelMap model,
			final HouseType houseType,
			final String locale) {
		List<CommitteeType> committeeTypes = CommitteeType.find(houseType, locale);
		model.addAttribute("committeeTypes", committeeTypes);
	}
	
	//=============== VALIDATIONS =========
	private void valEmptyAndNull(final CommitteeName domain,
			final BindingResult result) {
		// 'committeeType' SHOULD NOT BE NULL
		if(domain.getCommitteeType() == null) {
			result.rejectValue("committeeType", "NotEmpty", "Committee Type should not be empty");
		}
		
		// 'name' SHOULD NOT BE NULL OR EMPTY
		if(domain.getName() == null || domain.getName().isEmpty()) {
			result.rejectValue("name", "NotEmpty", "Name should not be empty");
		}
		
		// 'displayName' SHOULD NOT BE NULL OR EMPTY
		if(domain.getDisplayName() == null || domain.getDisplayName().isEmpty()) {
			result.rejectValue("displayName", "NotEmpty", "Display Name should not be empty");
		}		
		
		// 'foundationDate' SHOULD NOT BE NULL OR EMPTY
		if(domain.getFoundationDate() == null) {
			result.rejectValue("foundationDate", "NotEmpty", "Foundation Date should not be empty");
		}
	}
	
	/**
	 * EITHER 'durationInYears' OR 'durationInMonths' OR 'durationInDays' MUST BE SET, NOT MORE
	 * THAN ONE.
	 */
	private void valDuration(final CommitteeName domain,
			final BindingResult result) {
		Integer durationInYears = domain.getDurationInYears();
		Integer durationInMonths = domain.getDurationInMonths();
		Integer durationInDays = domain.getDurationInDays();
		
		if(durationInYears == null && durationInMonths == null && durationInDays == null) {
			result.rejectValue("durationInYears", "OneDurationMustBeSet", 
					"Either 'Duration In Years' or 'Duration In Days' or " +
					"'Duration In Days' must be set");
		}
		else if(durationInYears != null && (durationInMonths != null || durationInDays != null)) {
			result.rejectValue("durationInYears", "MoreThanOneDurationCannotBeSet", 
					"More than 1 duration cannot be set");
		}
		else if(durationInMonths != null && (durationInYears != null || durationInDays != null)) {
			result.rejectValue("durationInMonths", "MoreThanOneDurationCannotBeSet", 
					"More than 1 duration cannot be set");
		}
		else if(durationInDays != null && (durationInYears != null || durationInMonths != null)) {
			result.rejectValue("durationInDays", "MoreThanOneDurationCannotBeSet", 
					"More than 1 duration cannot be set");
		}
	}
	
	/**
	 * IF 'houseType' == lowerhouse, ONLY 'noOfLowerHouseMembers' MUST BE SET.
	 * IF 'houseType' == upperhouse, ONLY 'noOfUpperHouseMembers' MUST BE SET.
	 * IF 'houseType' == bothhouse,	'noOfLowerHouseMembers' and 'noOfUpperHouseMembers'
	 * MUST BOTH BE SET.
	 */
	private void valMemberCount(final CommitteeName domain,
			final BindingResult result) {
		CommitteeType committeeType = domain.getCommitteeType();
		if(committeeType != null) {
			HouseType houseType = committeeType.getHouseType();
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(domain.getNoOfLowerHouseMembers() == null 
						|| domain.getNoOfLowerHouseMembers().equals(0)) {
					result.rejectValue("noOfLowerHouseMembers", "NotEmpty", 
							"Number of Assembly members should not be empty");
				}
				
				if(domain.getNoOfUpperHouseMembers() != null
						&& domain.getNoOfUpperHouseMembers().compareTo(0) > 0) {
					result.rejectValue("noOfUpperHouseMembers", "Empty", 
							"Number of Council members should be empty");
				}
			}
			else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(domain.getNoOfUpperHouseMembers() == null 
						|| domain.getNoOfUpperHouseMembers().equals(0)) {
					result.rejectValue("noOfUpperHouseMembers", "NotEmpty", 
							"Number of Council members should not be empty");
				}
				
				if(domain.getNoOfLowerHouseMembers() != null
						&& domain.getNoOfLowerHouseMembers().compareTo(0) > 0) {
					result.rejectValue("noOfLowerHouseMembers", "Empty", 
							"Number of Assembly members should be empty");
				}
			}
			else if(houseType.getType().equals(ApplicationConstants.BOTH_HOUSE)) {
				if(domain.getNoOfLowerHouseMembers() == null 
						|| domain.getNoOfLowerHouseMembers().equals(0)) {
					result.rejectValue("noOfLowerHouseMembers", "NotEmpty", 
							"Number of Assembly members not be empty");
				}
				
				if(domain.getNoOfUpperHouseMembers() == null 
						|| domain.getNoOfUpperHouseMembers().equals(0)) {
					result.rejectValue("noOfUpperHouseMembers", "NotEmpty", 
							"Number of Council members should not be empty");
				}
			}
		}
	}
	
	/**
	 * 'name' + 'committeeType' MUST UNIQUELY REPRESENT AN 'CommitteeName' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule while creating
	 * an instance.
	 */
	private void valInstanceCreationUniqueness(final CommitteeName domain,
			final BindingResult result) {
		if(domain.getName() != null && domain.getCommitteeType() != null) {
			CommitteeName committeeName = 
				CommitteeName.find(domain.getName(), domain.getCommitteeType(), domain.getLocale());
			if(committeeName != null) {
				Object[] errorArgs = new Object[] {committeeName.getName(), 
						committeeName.getCommitteeType().getName()};
				
				StringBuffer defaultMessage = new StringBuffer();
				defaultMessage.append("Committee: ");
				defaultMessage.append(errorArgs[0]);
				defaultMessage.append(" already exists for Committee type: ");
				defaultMessage.append(errorArgs[1]);
				
				result.rejectValue("name", "DuplicateCommitteeName", errorArgs, 
						defaultMessage.toString());
			}
		}
	}
	
	/**
	 * 'name' + 'committeeType' MUST UNIQUELY REPRESENT AN 'CommitteeName' INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule while updating
	 * an instance.
	 */
	private void valInstanceUpdationUniqueness(final CommitteeName domain,
			final BindingResult result) {
		if(domain.getName() != null && domain.getCommitteeType() != null) {
			CommitteeName committeeName = 
				CommitteeName.find(domain.getName(), domain.getCommitteeType(), domain.getLocale());
			if(committeeName != null) {
				Long domainId = domain.getId();
				Long committeeNameId = committeeName.getId();
				if(! domainId.equals(committeeNameId)) {
					Object[] errorArgs = new Object[] {committeeName.getName(), 
							committeeName.getCommitteeType().getName()};
					
					StringBuffer defaultMessage = new StringBuffer();
					defaultMessage.append("Committee: ");
					defaultMessage.append(errorArgs[0]);
					defaultMessage.append(" already exists for Committee type: ");
					defaultMessage.append(errorArgs[1]);
					
					result.rejectValue("name", "DuplicateCommitteeName", errorArgs, 
							defaultMessage.toString());
				}
			}
		}
	}
	
	/**
	 * VALIDATE THE STRUCTURE OF 'rule' AGAINST THE TEMPLATE.
	 */
	// TODO
	private void valRules(final CommitteeName domain,
			final BindingResult result) {

	}
	
	private void valVersionMismatch(final CommitteeName domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
	
	
}