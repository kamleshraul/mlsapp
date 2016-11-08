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
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeSubject;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("committeesubject")
public class CommitteeSubjectController extends GenericController<CommitteeSubject> {
	
	@Override
	protected void populateModule(ModelMap model, HttpServletRequest request,
			String locale, AuthUser currentUser) {
		try {
			this.populateCommitteeTypesAndNames(model, locale);
		} catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void populateNew(final ModelMap model,
			final CommitteeSubject domain, final String locale,
			final HttpServletRequest request) {
		domain.setLocale(locale);
		String strCommitteeName = request.getParameter("committeeName");
		CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, Long.parseLong(strCommitteeName));
		if(committeeName != null){
			model.addAttribute("committeeDisplayName", committeeName.getDisplayName());
			model.addAttribute("committeName",committeeName.getId());
		}
	}
	
	@Override
	protected void populateEdit(final ModelMap model,
			final CommitteeSubject domain, final HttpServletRequest request) {
		String locale = domain.getLocale();
		CommitteeName committeeName = domain.getCommitteeName();
		if(committeeName != null){
			model.addAttribute("committeeDisplayName", committeeName.getDisplayName());
			model.addAttribute("committeName",committeeName.getId());
		}else{
			String strCommitteeName = request.getParameter("committeeName");
			committeeName = CommitteeName.findById(CommitteeName.class, Long.parseLong(strCommitteeName));
			if(committeeName != null){
				model.addAttribute("committeeDisplayName", committeeName.getDisplayName());
				model.addAttribute("committeName",committeeName.getId());
			}
		}
	}
	
	@Override
	protected void customValidateCreate(final CommitteeSubject domain,
			final BindingResult result, 
			final HttpServletRequest request) {
//		this.valEmptyAndNull(domain, result);
		this.valInstanceUniqueness(domain, result);
	}

	@Override
	protected void customValidateUpdate(final CommitteeSubject domain,
			final BindingResult result, 
			final HttpServletRequest request) {
//		this.valEmptyAndNull(domain, result);
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
	
	private void populateCommitteeTypesAndNames(ModelMap model, String locale) throws ELSException {
		UserGroup userGroup = null;
		UserGroupType userGroupType = null;
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		if(userGroups != null && ! userGroups.isEmpty()) {
			CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "CIS_ALLOWED_USERGROUPTYPES", "");
			if(cp != null) {
				List<UserGroupType> configuredUserGroupTypes = 
						CommitteeSubjectController.delimitedStringToUGTList(cp.getValue(), ",", locale);
				
				userGroup = CommitteeSubjectController.getUserGroup(userGroups, configuredUserGroupTypes, locale);
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
					CommitteeSubjectController.getCommitteeNames(committeeNameParam, "##", locale);
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
			throw new ELSException("CommitteeMeetingController.populateModule/4", 
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