package org.mkcl.els.controller.cis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CommitteeMeetingType;
import org.mkcl.els.domain.CommitteeMember;
import org.mkcl.els.domain.CommitteeMemberAttendance;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeSubject;
import org.mkcl.els.domain.CommitteeTour;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Prashnavali;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/committeemeeting")
public class CommitteeMeetingController extends
		GenericController<CommitteeMeeting> {

	@Override
	protected void populateModule(final ModelMap model,
			final HttpServletRequest request, final String locale,
			final AuthUser currentUser) {
		try {
			this.populateCommitteeTypesAndNames(model, locale);
			CustomParameter languageParameter = CustomParameter.findByName(CustomParameter.class, "RIS_LANGUAGE", "");
			String languages = "";
			if(languageParameter != null){
				String[] strLanguages = languageParameter.getValue().split(",");
				for(String s : strLanguages){
					Language language = Language.findByFieldName(Language.class, "type",s , locale);
					languages = languages + language.getId().toString() +",";
				}
				
			}
			model.addAttribute("language", languages);
		} catch (ELSException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void populateNew(final ModelMap model,
			final CommitteeMeeting domain, final String locale,
			final HttpServletRequest request) {
		domain.setLocale(locale);
//		HouseType houseType = this.getCurrentUsersHouseType(locale);
//		Date currentDate = new Date();
//		List<Committee> committees = Committee.findActiveCommittees(houseType,
//				true, currentDate, locale);
//		
//		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
//		for (Committee c : committees) {
//			CommitteeName cn = c.getCommitteeName();
//			committeeNames.add(cn);
//		}
//
//		List<CommitteeName> sortedCNList = CommitteeName.sortByName(
//				committeeNames, ApplicationConstants.ASC);
//		model.addAttribute("committeeNames", sortedCNList);
//		populateCommitteeTypesAndNames(model, locale);
		String strCommitteeType = request.getParameter("committeeType");
		CommitteeType committeeType = CommitteeType.findById(CommitteeType.class, Long.parseLong(strCommitteeType));
//		CommitteeType committeeType = sortedCNList.get(0).getCommitteeType();
		String strCommitteeName = request.getParameter("committeeName");
		CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, Long.parseLong(strCommitteeName));
		if(committeeName != null){
			Committee committee = Committee.findActiveCommittee(committeeName, new Date(), locale);
			this.populateCommitteeMeetingTours(model,committee, locale);
			this.populateprashnavalis(model, locale,committee);
			model.addAttribute("committeeDisplayName", committeeName.getDisplayName());
			model.addAttribute("committeName",committeeName.getId());
			
			model.addAttribute("committeeDisolved",false);
			model.addAttribute("committeeDisolutionDateFormatted","");
			
			if(committee==null) {
				committee=Committee.findLatestCommitteeByCommitteeName(committeeName);
			}
			if(committee==null || 
					(committee.getDissolutionDate()!=null 
					 && !committee.getDissolutionDate().after(new Date()))){
				model.addAttribute("committeeDisolved",true);				
				//check for committee dissolution date
				if(committee!=null && committee.getDissolutionDate() !=null) {
					model.addAttribute("committeeDisolutionDate",committee.getDissolutionDate());
					model.addAttribute("committeeDisolutionDateFormatted",
							FormaterUtil.formatDateToString(committee.getDissolutionDate()
							,ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_2, "mr_IN"));
				}
			}else if(committee==null || committee.getDissolutionDate()==null) {
				model.addAttribute("committeeDisolved",true);
				model.addAttribute("committeeDisolutionDateFormatted","");
			}
		}
		
		this.populateCommitteeType(model, committeeType);
		this.populateCommitteeMeetingTypes(model, locale);
		
		this.populateCommitteeSubjects(model, locale, committeeName);
		

	}

	private HouseType getCurrentUsersHouseType(final String locale) {
		AuthUser authUser = this.getCurrentUser();
		String houseTypeType = authUser.getHouseType();
		HouseType houseType = HouseType.findByType(houseTypeType, locale);
		return houseType;
	}

	@Override
	protected void preValidateCreate(final CommitteeMeeting domain,
			final BindingResult result, final HttpServletRequest request) {
		this.setCommittee(domain, request);
		this.setCommitteeMeetingType(domain, request);
		this.setCommitteeMeetingTour(domain, request);
		this.setCommitteeSubject(domain, request);
	}

	@Override
	protected void customValidateCreate(final CommitteeMeeting domain,
			final BindingResult result, final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		//set Creation Date, created by, edited on , edited by
		domain.setCreationDate(new Date());
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		// this.valFormationDateBeforeDissolutionDate(domain, result);
		// this.valInstanceCreationUniqueness(domain, result);
	}
	
	@Override
	protected void populateEdit(final ModelMap model,
			final CommitteeMeeting domain, final HttpServletRequest request) {
		String locale = domain.getLocale();
		Committee committee = domain.getCommittee();
		this.populateCommitteeName(model, committee);
		this.populateCommitteeNames(model, locale);

		CommitteeName committeeName = domain.getCommittee().getCommitteeName();
		CommitteeType committeeType = committeeName.getCommitteeType();

		this.populateCommitteeType(model, committeeType);
		this.populateCommitteeMeetingType(model,
				domain.getCommitteeMeetingType());
		this.populatePrashnavali(model,
				domain.getPrashnavali());
		this.populateCommitteeMeetingTypes(model, locale);
		this.populateprashnavalis(model, locale, committee);
		this.populateCommitteeSubjects(model, locale, committee.getCommitteeName());
		this.populateCommitteeSubject(model, domain);
		
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){  
		if(domain.getCreationDate()!=null){
			model.addAttribute("creationDate",FormaterUtil.
					getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
		}
		}
		model.addAttribute("createdBy", domain.getCreatedBy());
	
		// this.populateCommitteeNames(model, committeeType, locale);

	}

	@Override
	protected void preValidateUpdate(final CommitteeMeeting domain,
			final BindingResult result, final HttpServletRequest request) {
		this.setCommittee(domain, request);
		this.setCommitteeMeetingType(domain, request);
		this.setCommitteeMeetingTour(domain, request);
		this.setCommitteeSubject(domain, request);
	}

	@Override
	protected void customValidateUpdate(final CommitteeMeeting domain,
			final BindingResult result, final HttpServletRequest request) {
		this.valEmptyAndNull(domain, result);
		// this.valFormationDateBeforeDissolutionDate(domain, result);
		// this.valInstanceUpdationUniqueness(domain, result);
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
		SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
		
		String strCreationDate=request.getParameter("creationDate");
		if(strCreationDate!=null&&!strCreationDate.isEmpty()){
			try {
				domain.setCreationDate(format.parse(strCreationDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String strCreatedBy=request.getParameter("createdBy");
		domain.setCreatedBy(strCreatedBy);
		this.valVersionMismatch(domain, result);
		//set EditedAs
		UserGroupType userGroupType = getUserGroupType(request, domain.getLocale());
				if(userGroupType != null){
					domain.setEditedAs(userGroupType.getName());
				}
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		
		
		}
	}

	@Override
	protected void populateAfterCreate(ModelMap model, CommitteeMeeting domain, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		NotificationController.sendCommitteeMeetingEntryNotification(domain, domain.getLocale());
	}
	
	@Override
	protected void populateAfterUpdate(ModelMap model, CommitteeMeeting domain, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		NotificationController.sendCommitteeMeetingEntryNotification(domain, domain.getLocale());
	}

	@RequestMapping(value = "{id}/view", method = RequestMethod.GET)
	public String view(final ModelMap model, @PathVariable("id") final Long id,
			final Locale locale) {

		CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(
				CommitteeMeeting.class, id);
		Committee committee = committeeMeeting.getCommittee();
		CommitteeName committeeName = committee.getCommitteeName();
		CommitteeType committeeType = committeeName.getCommitteeType();

		model.addAttribute("id", committeeMeeting.getId());
		model.addAttribute("committeeType", committeeType.getName());
		model.addAttribute("committeeName", committeeName.getDisplayName());
		model.addAttribute("committeeSubject",
				committeeMeeting.getCommitteeSubject().getName());

		String meetingDate = FormaterUtil.formatDateToString(
				committeeMeeting.getMeetingDate(), this.getServerDateFormat(),
				locale.toString());
		model.addAttribute("meetingDate", meetingDate);

		model.addAttribute("fromDate", committeeMeeting.getStartTime());

		model.addAttribute("toDate", committeeMeeting.getEndTime());
		if (committeeMeeting.getPrashnavali()==null) {
			//Do nothing
			}
		else
			{	
				model.addAttribute("prashnavali", committeeMeeting
				.getPrashnavali().getPrashnavaliName());
		}
		model.addAttribute("committeemeetingtype", committeeMeeting
				.getCommitteeMeetingType().getName());
		if (committeeMeeting.getCommitteeMeetingType().getType()
				.equalsIgnoreCase("witness_with_tour")) {
			model.addAttribute("committeetour", committeeMeeting
					.getCommitteeTour().getSubject());
		}
		model.addAttribute("speech", committeeMeeting.getSpeech());
		model.addAttribute("venueName", committeeMeeting.getMeetingLocation());
		model.addAttribute("conciseMinutes", committeeMeeting.getConciseMinutes());
		// this.populateCommitteeMembers(model, committee);
		// this.populateInvitedMembers(model, committee);
		// this.populateStatus(model, committee);

		return "committeemeeting/view";
	}

	// =============== INTERNAL METHODS =========
	private void populateCommitteeType(final ModelMap model,
			final CommitteeType committeeType) {
		model.addAttribute("committeeType", committeeType.getName());
	}

	private List<CommitteeType> populateCommitteeTypes(final ModelMap model,
			final String locale) {
		List<CommitteeType> committeeTypes = CommitteeType.findAll(
				CommitteeType.class, "name", ApplicationConstants.ASC, locale);
		model.addAttribute("committeeTypes", committeeTypes);
		return committeeTypes;
	}

	private void populateCommitteeMeetingType(final ModelMap model,
			final CommitteeMeetingType committeeMeetingType) {

		model.addAttribute("committeemeetingtype", committeeMeetingType);
	}

	private List<CommitteeMeetingType> populateCommitteeMeetingTypes(
			final ModelMap model, final String locale) {
		List<CommitteeMeetingType> committeeMeetingTypes = CommitteeMeetingType
				.findAll(CommitteeMeetingType.class, "name",
						ApplicationConstants.ASC, locale);
		model.addAttribute("committeemeetingtypes", committeeMeetingTypes);
		return committeeMeetingTypes;
	}
	private void populatePrashnavali(final ModelMap model,
			final Prashnavali prashnavali) {

		model.addAttribute("prashnavali", prashnavali);
	}
	private List<Prashnavali> populateprashnavalis(
			final ModelMap model, final String locale, final Committee committee) {
//		List<Prashnavali> prashnavalis = Prashnavali
//				.findAll(Prashnavali.class, "prashnavaliName",
//						ApplicationConstants.ASC, locale);
		
		List<Prashnavali> prashnavalis = Prashnavali.
				findAllByFieldName(Prashnavali.class, "committee", committee, "id", "asc", locale);
				
		model.addAttribute("prashnavalis", prashnavalis);
		return prashnavalis;
	}
	private List<CommitteeSubject> populateCommitteeSubjects(
			final ModelMap model, final String locale, final CommitteeName committeeName) {
		
//		List<CommitteeSubject> committeeSubjects = CommitteeSubject
//				.findAll(CommitteeSubject.class, "name",
//						ApplicationConstants.ASC, locale);
		
		List<CommitteeSubject> committeeSubjects = CommitteeSubject.
				findAllByFieldName(CommitteeSubject.class, "committeeName", committeeName, "name", "asc", locale);
		model.addAttribute("committeeSubjects", committeeSubjects);
		return committeeSubjects;
	}
	private void populateCommitteeSubject(final ModelMap model,
			final CommitteeMeeting committeeMeeting) {

		model.addAttribute("committeeSubject", committeeMeeting.getCommitteeSubject());
	}
	private List<CommitteeTour> populateCommitteeMeetingTours(
			final ModelMap model, final Committee committee, final String locale) {
		List<CommitteeTour> committeetours = CommitteeTour.findCommitteeTours(
				committee, locale);
		model.addAttribute("committeetours", committeetours);

		return committeetours;
	}

	private List<CommitteeName> populateCommitteeNames(final ModelMap model,
			final CommitteeType committeeType, final String locale) {
		List<CommitteeName> committeeNames = CommitteeName.find(committeeType,
				locale);
		model.addAttribute("committeeNames", committeeNames);
		return committeeNames;
	}

	private void populateCommitteeNames(final ModelMap model,
			final String locale) {
		HouseType houseType = this.getCurrentUsersHouseType(locale);
		Date currentDate = new Date();
		List<Committee> committees = Committee.findActiveCommittees(houseType,
				true, currentDate, locale);

		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
		for (Committee c : committees) {
			CommitteeName cn = c.getCommitteeName();
			committeeNames.add(cn);
		}

		List<CommitteeName> sortedCNList = CommitteeName.sortByName(
				committeeNames, ApplicationConstants.ASC);
		model.addAttribute("committeeNames", sortedCNList);
	}

	private void populateCommitteeName(final ModelMap model,
			final Committee committee) {
		CommitteeName committeeName = committee.getCommitteeName();
		model.addAttribute("committeeDisplayName", committeeName.getDisplayName());
		model.addAttribute("committeName",committeeName.getId());
	}

	private void populateFoundationDate(final ModelMap model,
			final CommitteeName committeeName, final String locale) {
		String dateFormat = this.getServerDateFormat();
		String foundationDate = FormaterUtil.formatDateToString(
				committeeName.getFoundationDate(), dateFormat,
				locale.toString());
		model.addAttribute("foundationDate", foundationDate);
	}

	private void populateCommitteeMembers(final ModelMap model,
			final CommitteeMeeting domain) {
		List<CommitteeMember> committeeMembers = domain.getCommittee()
				.getMembers();
		model.addAttribute("committeeMembers", committeeMembers);
	}

	private void populateInvitedMembers(final ModelMap model,
			final CommitteeMeeting domain) {
		List<CommitteeMember> invitedMembers = domain.getCommittee()
				.getInvitedMembers();
		model.addAttribute("invitedMembers", invitedMembers);
	}

	private void populateStatus(final ModelMap model,
			final CommitteeMeeting domain) {
		Status status = domain.getCommittee().getStatus();
		model.addAttribute("status", status);
	}

	private String getServerDateFormat() {
		CustomParameter serverDateFormat = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		return serverDateFormat.getValue();
	}

	private String getServerDateTimeFormat() {
		CustomParameter serverDateFormat = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
		return serverDateFormat.getValue();
	}

	// =============== VALIDATIONS =========
	private void valEmptyAndNull(final CommitteeMeeting domain,
			final BindingResult result) {

	}

	private void valFormationDateBeforeDissolutionDate(
			final CommitteeMeeting domain, final BindingResult result) {
		Date formationDate = domain.getCommittee().getFormationDate();
		Date dissolutionDate = domain.getCommittee().getDissolutionDate();

		if (formationDate != null && dissolutionDate != null) {
			if (dissolutionDate.before(formationDate)) {
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
	private void valInstanceCreationUniqueness(final CommitteeMeeting domain,
			final BindingResult result) {
		CommitteeName committeeName = domain.getCommittee().getCommitteeName();
		Date formationDate = domain.getCommittee().getFormationDate();
		if (committeeName != null && formationDate != null) {
			Committee committee = Committee.find(committeeName, formationDate,
					domain.getLocale());
			if (committee != null) {
				String dateFormat = this.getServerDateFormat();
				String strFormationDate = FormaterUtil.formatDateToString(
						formationDate, dateFormat, domain.getLocale());

				Object[] errorArgs = new Object[] {
						committeeName.getDisplayName(), strFormationDate };

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
	private void valInstanceUpdationUniqueness(final CommitteeMeeting domain,
			final BindingResult result) {
		CommitteeName committeeName = domain.getCommittee().getCommitteeName();
		Date formationDate = domain.getCommittee().getFormationDate();
		if (committeeName != null && formationDate != null) {
			Committee committee = Committee.find(committeeName, formationDate,
					domain.getLocale());
			if (committee != null) {
				Long domainId = domain.getId();
				Long committeeId = committee.getId();
				if (!domainId.equals(committeeId)) {
					String dateFormat = this.getServerDateFormat();
					String strFormationDate = FormaterUtil.formatDateToString(
							formationDate, dateFormat, domain.getLocale());

					Object[] errorArgs = new Object[] {
							committeeName.getDisplayName(), strFormationDate };

					StringBuffer defaultMessage = new StringBuffer();
					defaultMessage.append("Committee name: ");
					defaultMessage.append(errorArgs[0]);
					defaultMessage
							.append(" already exists for formation date: ");
					defaultMessage.append(errorArgs[1]);

					result.rejectValue("committeeName", "DuplicateCommittee",
							errorArgs, defaultMessage.toString());
				}
			}
		}
	}

	private void valVersionMismatch(final CommitteeMeeting domain,
			final BindingResult result) {
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}

	// =============== "SET" METHODS ============
	private void setCommittee(final CommitteeMeeting domain,
			final HttpServletRequest request) {
		String strCommitteeNameId = request.getParameter("committeeName");
		if (strCommitteeNameId != null && !strCommitteeNameId.isEmpty()) {
			Long committeeNameId = Long.parseLong(strCommitteeNameId);
			CommitteeName committeeName = CommitteeName.findById(
					CommitteeName.class, committeeNameId);

			Date currentDate = new Date();
			String locale = domain.getLocale();
			Committee committee = Committee.findActiveCommittee(committeeName,
					currentDate, locale);
			domain.setCommittee(committee);
		}
	}

	private void setCommitteeMeetingType(final CommitteeMeeting domain,
			final HttpServletRequest request) {
		String strCommitteeMeetingType = request
				.getParameter("committeemeetingtype");
		if (strCommitteeMeetingType != null
				&& !strCommitteeMeetingType.isEmpty()) {
			Long committeeMeetingTypeId = Long
					.parseLong(strCommitteeMeetingType);
			CommitteeMeetingType committeeMeetingType = CommitteeMeetingType
					.findById(CommitteeMeetingType.class,
							committeeMeetingTypeId);
			domain.setCommitteeMeetingType(committeeMeetingType);
		}
	}

	private void setCommitteeMeetingTour(final CommitteeMeeting domain,
			final HttpServletRequest request) {
		String strCommitteeMeetingTour = request.getParameter("committeetour");
		if (strCommitteeMeetingTour != null
				&& !strCommitteeMeetingTour.isEmpty()) {
			Long committeeMeetingTourId = Long
					.parseLong(strCommitteeMeetingTour);
			CommitteeTour committeeTour = CommitteeTour.findById(
					CommitteeTour.class, committeeMeetingTourId);
			domain.setCommitteeTour(committeeTour);
		}
	}

	private void setCommitteeSubject(final CommitteeMeeting domain,
			final HttpServletRequest request) {
		String strCommitteeSubject = request
				.getParameter("committeeSubjectList");
		if (strCommitteeSubject != null
				&& !strCommitteeSubject.isEmpty()) {
			Long committeeSubjectId = Long
					.parseLong(strCommitteeSubject);
			CommitteeSubject committeeSubject = CommitteeSubject
					.findById(CommitteeSubject.class,
							committeeSubjectId);
			domain.setCommitteeSubject(committeeSubject);
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
						CommitteeMeetingController.delimitedStringToUGTList(cp.getValue(), ",", locale);
				
				userGroup = CommitteeMeetingController.getUserGroup(userGroups, configuredUserGroupTypes, locale);
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
					CommitteeMeetingController.getCommitteeNames(committeeNameParam, "##", locale);
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
	public static UserGroupType getUserGroupType(HttpServletRequest request,
			String locale) {
		String strUserGroupType = request.getParameter("usergroupType");
		if(strUserGroupType != null && !strUserGroupType.isEmpty()){
			UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType,locale);
			return userGroupType;
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

	
	//======= INTERNAL METHOD END ================================

	@RequestMapping(value="/viewCommitteeMemberAttendancereport", method=RequestMethod.GET)
	public String viewMinistryReport(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		try{
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strHouseType = request.getParameter("houseType");
			if(strSessionType != null && !strSessionType.isEmpty()
				&& strSessionYear != null && !strSessionYear.isEmpty()
				&& strHouseType != null && !strHouseType.isEmpty()){
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				List ministryreport = getMinistryReport(locale, session.getStartDate(), model);
	            model.addAttribute("ministryreport", ministryreport);
			}
			
            
		}catch (Exception e) {
			String msg = e.getMessage();
			
			if(e instanceof ELSException){
				model.addAttribute("error", ((ELSException) e).getParameter());
			}else{
				if(msg == null){
					model.addAttribute("error", "Request may not complete successfully.");
				}else{
					model.addAttribute("error", msg);
				}
			}
			logger.error(msg);
			e.printStackTrace();			
		}
		
		return "committeemeeting/viewcommitteemeetingattendancereport";
	}
	
	private List getMinistryReport(Locale locale, Date onDate, ModelMap model){
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		 parameters.put("onDate", new String[]{onDate.toString()});
        parameters.put("locale", new String[]{locale.toString()});
        List ministryreport = Query.findReport(ApplicationConstants.ROTATIONORDER_MINISTRY_DEPARTMENTS_REPORT, parameters);
        
        String ministryName = "";
        for(int index = 0, ministryCounter = 0, deptCounter = 1; index < ministryreport.size(); index++){
        	
        	Object[] row = (Object[])ministryreport.get(index);
        	String minName = (row[2] != null)? row[2].toString(): null; 
        	
        	if(!ministryName.equals(minName)){
        		deptCounter = 1;
        		ministryCounter++;		            		
        	}
        	((Object[])ministryreport.get(index))[0] = FormaterUtil.formatNumberNoGrouping(Integer.valueOf(ministryCounter), locale.toString());
        	((Object[])ministryreport.get(index))[4] = FormaterUtil.formatNumberNoGrouping(Integer.valueOf(deptCounter), locale.toString());
        	deptCounter++;
        	ministryName = row[2].toString();
        }
        return ministryreport;
	}
	/****** Committee Member Attendance ****/
	@RequestMapping(value = "{id}/attendance", method = RequestMethod.GET)
	public String markAttendance(final HttpServletRequest request,
			final ModelMap model, final Locale locale,
			@PathVariable("id") final Long id) {
		
		try {
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(
					CommitteeMeeting.class, id);
			/**** Checking if attendance already created ****/
			Boolean attendanceCreatedAlready = CommitteeMemberAttendance
					.memberAttendanceCreated(committeeMeeting,
							locale.toString());

			if (!attendanceCreatedAlready) {
				String flag = CommitteeMemberAttendance
						.createAllMemberAttendance(committeeMeeting,
								locale.toString());
				if (!flag.contains("SUCCESS")) {
					model.addAttribute("type", flag);
					return "ERROR";
				}
			}
			List<CommitteeMemberAttendance> allItems = null;
			List<CommitteeMemberAttendance> selectedItems = null;
			if (committeeMeeting.getCommitteeTour() == null) {
				allItems = CommitteeMemberAttendance.findAll(committeeMeeting,
						null, false, locale.toString());
				selectedItems=CommitteeMemberAttendance.findAll(committeeMeeting,
						null, true, locale.toString());
			} else {
				model.addAttribute("tourdates", committeeMeeting
						.getCommitteeTour().getItineraries());
				allItems = CommitteeMemberAttendance.findAll(committeeMeeting,
						committeeMeeting.getCommitteeTour().getItineraries()
								.get(0), false, locale.toString());
				selectedItems=CommitteeMemberAttendance.findAll(committeeMeeting,
						committeeMeeting.getCommitteeTour().getItineraries()
						.get(0), true, locale.toString());
			}

			model.addAttribute("allItems", allItems);
			model.addAttribute("allItemsCount", allItems.size());
			model.addAttribute("selectedItems", selectedItems);
			model.addAttribute("selectedItemsCount", selectedItems.size());
		} catch (ELSException e) {
			logger.error("failed", e);
			return "ERROR";
			
		}

		return "committeemeeting/committeemeetingattendance";
	}

	/****** Member Ballot(Council) Attendance Update ****/
	@Transactional
	@RequestMapping(value="/attendance",method=RequestMethod.PUT)
	public @ResponseBody String updateAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale)
	{
	
					
			String selectedItems=request.getParameter("items");
			String[] items=selectedItems.split(",");
			String absentMembers=request.getParameter("absentMembers");
			String[] absentMember=absentMembers.split(",");
			
			if(items.length!=0)
			{
				
				/************* NEED TO BE DONE AFTER APPROVAL FROM BA***************************************/
				for(String i:items)
				{
					
					if(!i.isEmpty())
					{
						CommitteeMemberAttendance committeeMemberAttendance=CommitteeMemberAttendance.findById(CommitteeMemberAttendance.class,Long.parseLong(i));
						
						committeeMemberAttendance.setAttendance(true);
						committeeMemberAttendance.merge();						
					}
				}
				
				for(String i:absentMember)
				{
					
					if(!i.isEmpty())
					{
						CommitteeMemberAttendance committeeMemberAttendance=CommitteeMemberAttendance.findById(CommitteeMemberAttendance.class,Long.parseLong(i));
						
						committeeMemberAttendance.setAttendance(false);
						committeeMemberAttendance.merge();						
					}
				}
			}
			else{
				return "nomembers";
			}
		
		return "success";
	}
	
	/****** Committee Member prashnavali ****/
	@RequestMapping(value = "{id}/prashnavali", method = RequestMethod.GET)
	public String setPrashnavali(final HttpServletRequest request,
			final ModelMap model, final Locale locale,
			@PathVariable("id") final Long id) {
	
		CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(
				CommitteeMeeting.class, id);
		
		List<CommitteeMember> eligibleMembers = null;
		
		eligibleMembers = committeeMeeting.getCommittee().getMembers();


		model.addAttribute("eligibleMembers", eligibleMembers);
		model.addAttribute("commiteemeetingId", id);
		
		return "committeemeeting/committeprashnavali";
	}
	/****** Committee Member prashnavali ****/
	@RequestMapping(value = "/listquestions", method = RequestMethod.GET)
	public String listPrashnavali(final HttpServletRequest request,
			final ModelMap model, final Locale locale) {
		String strMember=request.getParameter("member");
		String commiteemeetingId=request.getParameter("commiteemeetingId");
		if(strMember!=null){
			Member member=Member.findById(Member.class,Long.parseLong(strMember));
			CommitteeMeeting committeeMeeting=CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(commiteemeetingId));
			model.addAttribute("member",member);
			Prashnavali prashnavali = Prashnavali.findById(Prashnavali.class, committeeMeeting.getPrashnavali().getId());
			model.addAttribute("questions", prashnavali.getQuestionAnswers());
		}
		

		return "committeemeeting/listcommitteprashnavali";
	}


	
}