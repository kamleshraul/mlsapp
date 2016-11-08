package org.mkcl.els.controller.cis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeMember;
import org.mkcl.els.domain.CommitteeMemberAttendance;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/committee")
public class CommitteeController extends GenericController<Committee> {

	@Override
	protected void populateModule(final ModelMap model,
			final HttpServletRequest request,
			final String locale,
			final AuthUser currentUser) {
		//this.populateCommitteeTypes(model, locale);
		try {
			this.populateCommitteeTypesAndNames(model,locale);
		} catch (ELSException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected void populateNew(final ModelMap model, 
			final Committee domain,
			final String locale,
			final HttpServletRequest request) {
		domain.setLocale(locale);

		List<CommitteeType> committeeTypes = 
			this.populateCommitteeTypes(model, locale);

		CommitteeType committeeType = committeeTypes.get(0);
		List<CommitteeName> committeeNames = 
			this.populateCommitteeNames(model, committeeType, locale);

		CommitteeName committeeName = committeeNames.get(0);
		this.populateFoundationDate(model, committeeName, locale);
	}

	@Override
	protected void customValidateCreate(final Committee domain, 
			final BindingResult result,
			final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		this.valFormationDateBeforeDissolutionDate(domain, result);
		this.valInstanceCreationUniqueness(domain, result);
	}

	@Override
	protected void populateEdit(final ModelMap model, 
			final Committee domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		CommitteeName committeeName = domain.getCommitteeName();
		CommitteeType committeeType = committeeName.getCommitteeType();
		model.addAttribute("committeeName", committeeName);
		this.populateCommitteeType(model, committeeType);
		this.populateCommitteeTypes(model, locale);
		this.populateCommitteeNames(model, committeeType, locale);
		this.populateFoundationDate(model, committeeName, locale);
		this.populateCommitteeMembers(model, domain);
		this.populateInvitedMembers(model, domain);
		this.populateStatus(model, domain);
	}

	@Override
	protected @ResponseBody void  customValidateUpdate(final Committee domain, 
			final BindingResult result,
			final HttpServletRequest request) {
		Status status=null;
		if(request.getParameter("status")!=null){
			status=Status.findById(Status.class, Long.parseLong(request.getParameter("status")));
		}
		String selectedItems=request.getParameter("allItems");
		String[] items=selectedItems.split(",");
	
		if(items.length!=0)
		{
			
			int j=0;
			
			for(String i:items)
			{
				++j;
				if(!"0".equals(i))
				{
					CommitteeMember committeeMember=CommitteeMember.findById(CommitteeMember.class,Long.parseLong(i));
					
					committeeMember.setPosition(j);
					committeeMember.merge();						
				}
			}
		}
			
		domain.setStatus(status);
		this.valEmptyAndNull(domain, result);
		this.valFormationDateBeforeDissolutionDate(domain, result);
		this.valInstanceUpdationUniqueness(domain, result);
		this.valVersionMismatch(domain, result);
	}

	@RequestMapping(value="{id}/view", method=RequestMethod.GET)
	public String view(final ModelMap model, 
			@PathVariable("id") final Long id,
			final Locale locale) {
		Committee committee = Committee.findById(Committee.class, id);
		CommitteeName committeeName = committee.getCommitteeName();
		CommitteeType committeeType = committeeName.getCommitteeType();

		model.addAttribute("id", committee.getId());
		model.addAttribute("committeeType", committeeType.getName());
		model.addAttribute("committeeName", committeeName);

		this.populateFoundationDate(model, committeeName, locale.toString());

		String formationDate = FormaterUtil.formatDateToString(
				committee.getFormationDate(), 
				this.getServerDateFormat(), 
				locale.toString());
		model.addAttribute("formationDate", formationDate);

		String dissolutionDate = FormaterUtil.formatDateToString(
				committee.getDissolutionDate(), 
				this.getServerDateFormat(), 
				locale.toString());
		model.addAttribute("dissolutionDate", dissolutionDate);

		this.populateCommitteeMembers(model, committee);
		this.populateInvitedMembers(model, committee);
		this.populateStatus(model, committee);

		return "committee/view";
	}

	//=============== INTERNAL METHODS =========
	private void populateCommitteeType(final ModelMap model,
			final CommitteeType committeeType) {
		model.addAttribute("committeeType", committeeType);
	}
	
	private List<CommitteeType> populateCommitteeTypes(final ModelMap model,
			final String locale) {
		List<CommitteeType> committeeTypes = CommitteeType.findAll(
				CommitteeType.class, "name", ApplicationConstants.ASC, locale);
		model.addAttribute("committeeTypes", committeeTypes);
		return committeeTypes;
	}

	private List<CommitteeName> populateCommitteeNames(final ModelMap model,
			final CommitteeType committeeType,
			final String locale) {
		List<CommitteeName> committeeNames = 
			CommitteeName.find(committeeType, locale);
		model.addAttribute("committeeNames", committeeNames);
		return committeeNames;
	}

	private void populateFoundationDate(final ModelMap model,
			final CommitteeName committeeName,
			final String locale) {
		String dateFormat = this.getServerDateFormat();
		String foundationDate = FormaterUtil.formatDateToString(
				committeeName.getFoundationDate(), 
				dateFormat, locale.toString());
		model.addAttribute("foundationDate", foundationDate);
	}

	private void populateCommitteeMembers(final ModelMap model,
			final Committee domain) {
		List<CommitteeMember> committeeMembers = domain.getMembers();
		model.addAttribute("committeeMembers", committeeMembers);
	}

	private void populateInvitedMembers(final ModelMap model,
			final Committee domain) {
		List<CommitteeMember> invitedMembers = domain.getInvitedMembers();
		model.addAttribute("invitedMembers", invitedMembers);
	}
	
	private void populateStatus(final ModelMap model, 
			final Committee domain) {
		Status status = domain.getStatus();
		model.addAttribute("status", status);
	}

	private String getServerDateFormat() {
		CustomParameter serverDateFormat = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		return serverDateFormat.getValue();
	}

	//=============== VALIDATIONS =========
	private void valEmptyAndNull(final Committee domain, 
			final BindingResult result) {
		// 'committeeName' SHOULD NOT BE NULL
		if(domain.getCommitteeName() == null) {
			result.rejectValue("committeeName", "NotEmpty",
			"Committee name should not be empty");
		}

		// 'formationDate' SHOULD NOT BE NULL OR EMPTY
		if(domain.getFormationDate() == null) {
			result.rejectValue("formationDate", "NotEmpty",
			"Formation Date should not be empty");
		}

		// 'dissolutionDate' SHOULD NOT BE NULL OR EMPTY
		if(domain.getDissolutionDate() == null) {
			result.rejectValue("dissolutionDate", "NotEmpty",
			"Dissolution Date should not be empty");
		}
	}

	private void valFormationDateBeforeDissolutionDate(final Committee domain, 
			final BindingResult result) {
		Date formationDate = domain.getFormationDate();
		Date dissolutionDate = domain.getDissolutionDate();

		if(formationDate != null && dissolutionDate != null) {
			if(dissolutionDate.before(formationDate)) {
				result.rejectValue("dissolutionDate",
						"DissolutionDateBeforeFormationDate", 
				"Dissolution date cannot be set prior to Formation date");
			}
		}
	}

	/**
	 * 'committeeName' + 'formationDate' MUST UNIQUELY REPRESENT AN 'Committee' 
	 * INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule while 
	 * creating an instance.
	 */
	private void valInstanceCreationUniqueness(final Committee domain,
			final BindingResult result) {
		CommitteeName committeeName = domain.getCommitteeName();
		Date formationDate = domain.getFormationDate();
		if(committeeName != null && formationDate != null) {
			Committee committee = Committee.find(committeeName, 
					formationDate, domain.getLocale());
			if(committee != null) {
				String dateFormat = this.getServerDateFormat();
				String strFormationDate = FormaterUtil.formatDateToString(
						formationDate, dateFormat, domain.getLocale());

				Object[] errorArgs = new Object[] {committeeName.getDisplayName(), 
						strFormationDate};

				StringBuffer defaultMessage = new StringBuffer();
				defaultMessage.append("Committee name: ");
				defaultMessage.append(errorArgs[0]);
				defaultMessage.append(" already exists for formation date: ");
				defaultMessage.append(errorArgs[1]);

				result.rejectValue("committeeName", "DuplicateCommittee", 
						errorArgs, defaultMessage.toString());
			}
		}
	}

	/**
	 * 'committeeName' + 'formationDate' MUST UNIQUELY REPRESENT AN 'Committee' 
	 * INSTANCE.
	 * 
	 * The following algorithm is applied to enforce the above rule while 
	 * updating an instance.
	 */
	private void valInstanceUpdationUniqueness(final Committee domain,
			final BindingResult result) {
		CommitteeName committeeName = domain.getCommitteeName();
		Date formationDate = domain.getFormationDate();
		if(committeeName != null && formationDate != null) {
			Committee committee = 
				Committee.find(committeeName, formationDate, domain.getLocale());
			if(committee != null) {
				Long domainId = domain.getId();
				Long committeeId = committee.getId();
				if(! domainId.equals(committeeId)) {
					String dateFormat = this.getServerDateFormat();
					String strFormationDate = FormaterUtil.formatDateToString(
							formationDate, dateFormat, domain.getLocale());

					Object[] errorArgs = new Object[] {
							committeeName.getDisplayName(), 
							strFormationDate};

					StringBuffer defaultMessage = new StringBuffer();
					defaultMessage.append("Committee name: ");
					defaultMessage.append(errorArgs[0]);
					defaultMessage.append(" already exists for formation date: ");
					defaultMessage.append(errorArgs[1]);

					result.rejectValue("committeeName", "DuplicateCommittee", 
							errorArgs, defaultMessage.toString());
				}
			}
		}
	}

	private void valVersionMismatch(final Committee domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}
	
	private void populateCommitteeTypesAndNames(ModelMap model, String locale) throws ELSException {
		UserGroup userGroup = null;
		UserGroupType userGroupType = null;
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		if(userGroups != null && ! userGroups.isEmpty()) {
			CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "CIS_ALLOWED_USERGROUPTYPES", "");
			if(cp != null) {
				List<UserGroupType> configuredUserGroupTypes = 
						CommitteeController.delimitedStringToUGTList(cp.getValue(), ",", locale);
				
				userGroup = CommitteeController.getUserGroup(userGroups, configuredUserGroupTypes, locale);
				userGroupType = userGroup.getUserGroupType();
				model.addAttribute("usergroup", userGroup.getId());
				model.addAttribute("usergroupType", userGroupType.getType());
			}
			else {
				throw new ELSException("CommitteeController.populateModule/4", 
						"CIS_ALLOWED_USERGROUPTYPES key is not set as CustomParameter");
			}
		}
		if(userGroup == null || userGroupType == null) {
			model.addAttribute("errorcode","current_user_has_no_usergroups");
		}
		
		// Populate CommitteeTypes and CommitteNames
		Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
		String committeeNameParam = parameters.get(ApplicationConstants.COMMITTEENAME_KEY + "_" + locale);
		if(committeeNameParam != null && ! committeeNameParam.equals("")) {
			List<CommitteeName> committeeNames =
					CommitteeController.getCommitteeNames(committeeNameParam, "##", locale);
			List<CommitteeType> committeeTypes = new ArrayList<CommitteeType>();
			for(CommitteeName cn : committeeNames){
				if(!committeeTypes.contains(cn.getCommitteeType())){
						committeeTypes.add(cn.getCommitteeType());
				}
			}
			
			model.addAttribute("committeeNames", committeeNames);
			model.addAttribute("committeeTypes", committeeTypes);
		}
		else {
			throw new ELSException("CommitteeController.populateModule/4", 
					"CommitteeName parameter is not set for Username: " + this.getCurrentUser().getUsername());
		}
	}



	private static List<CommitteeName> getCommitteeNames(
			String committeeNameParam, String delimiter, String locale) {
		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
		String cNames[] = committeeNameParam.split(delimiter);
		for(String cName : cNames){
			List<CommitteeName> comNames = 
					CommitteeName.findAllByFieldName(CommitteeName.class, "displayName", cName, "displayName", "asc", locale);
			if(comNames != null && !comNames.isEmpty()){
				committeeNames.addAll(comNames);
			}
			
		}
		return committeeNames;
	}

	private static UserGroup getUserGroup(List<UserGroup> userGroups,
			List<UserGroupType> configuredUserGroupTypes, String locale) {
		for(UserGroup ug : userGroups) {
			Date todaysDate = new Date();
			if(ug.getActiveFrom().before(todaysDate) && ug.getActiveTo().after(todaysDate)){
				for(UserGroupType ugt : configuredUserGroupTypes) {
					UserGroupType userGroupType = ug.getUserGroupType();
					if(ugt.getId().equals(userGroupType.getId())) {
						return ug;
					}
				}
			}
		}
		return null;
	}

	private static List<UserGroupType> delimitedStringToUGTList(String delimitedUserGroups,
			String delimiter, String locale) {
		List<UserGroupType> userGroupTypes = new ArrayList<UserGroupType>();
		
		String[] strUserGroupTypes = delimitedUserGroups.split(delimiter);
		for(String strUserGroupType : strUserGroupTypes) {
			UserGroupType ugt = UserGroupType.findByType(strUserGroupType, locale);
			userGroupTypes.add(ugt);
		}
		
		return userGroupTypes;
	}

}